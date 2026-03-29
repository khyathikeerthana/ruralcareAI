from datetime import date

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import func, or_, select
from sqlalchemy.orm import Session

from ..config import get_settings
from ..database import get_db
from ..models.patient import Patient
from ..models.role import Role
from ..models.user import User
from ..schemas.auth import (
    AuthResponse,
    LoginRequest,
    ProfileSetupRequest,
    ProfileSetupResponse,
    ProfileUpdateRequest,
    RegisterRequest,
)
from ..services.auth import hash_password, verify_password
from ..utils.security import create_access_token

router = APIRouter(prefix="/auth", tags=["Authentication"])
settings = get_settings()

ROLE_DESCRIPTIONS = {
    "patient": "Rural patient accessing telemedicine services",
    "doctor": "Medical professional providing consultations",
    "community_health_worker": "Community healthcare worker supporting residents",
    "admin": "Platform administrator monitoring operations",
}

ROLE_DISPLAY_NAMES = {
    "patient": "Patient",
    "doctor": "Medical Professional",
    "community_health_worker": "Community Health Worker",
    "admin": "Admin",
}


def _normalize_email(value: str) -> str:
    return value.strip().lower()


def _normalize_phone(value: str) -> str:
    cleaned = [char for char in value.strip() if char.isdigit() or char == "+"]
    return "".join(cleaned)


def _role_label(role_name: str) -> str:
    return ROLE_DISPLAY_NAMES.get(role_name, role_name.replace("_", " ").title())


def _age_to_birthdate(age: int) -> date:
    current_year = date.today().year
    return date(current_year - age, 1, 1)


def _birthdate_to_age(value: date | None) -> int | None:
    if value is None:
        return None
    today = date.today()
    years = today.year - value.year
    if (today.month, today.day) < (value.month, value.day):
        years -= 1
    return years


def _get_or_create_role(db: Session, role_name: str) -> Role:
    role = db.scalar(select(Role).where(Role.name == role_name))
    if role is not None:
        return role

    role = Role(
        name=role_name,
        description=ROLE_DESCRIPTIONS.get(role_name, f"{role_name.title()} account"),
    )
    db.add(role)
    db.flush()
    return role


@router.post("/register", response_model=AuthResponse, status_code=201)
def register(payload: RegisterRequest, db: Session = Depends(get_db)) -> AuthResponse:
    role_name = payload.role
    normalized_email = _normalize_email(payload.email)
    normalized_phone = _normalize_phone(payload.phone)

    existing_user = db.scalar(
        select(User).where(
            or_(
                func.lower(User.email) == normalized_email,
                User.phone == normalized_phone,
            )
        )
    )
    if existing_user is not None:
        existing_role_name = existing_user.role.name
        if existing_role_name != role_name:
            detail = (
                f"The profile already exists as {_role_label(existing_role_name)}. "
                f"Please login as {_role_label(existing_role_name)}."
            )
        else:
            detail = (
                f"An account already exists as {_role_label(existing_role_name)}. "
                f"Please login as {_role_label(existing_role_name)}."
            )
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=detail,
        )

    role = _get_or_create_role(db, role_name)
    user = User(
        full_name=payload.full_name.strip(),
        email=normalized_email,
        phone=normalized_phone,
        password_hash=hash_password(payload.password),
        role_id=role.id,
        is_active=True,
    )
    db.add(user)
    db.flush()

    if role_name == "patient":
        db.add(Patient(user_id=user.id))

    db.commit()
    db.refresh(user)

    patient_id = None
    if role_name == "patient":
        patient = db.scalar(select(Patient).where(Patient.user_id == user.id))
        if patient:
            patient_id = patient.id

    token = create_access_token(subject=str(user.id), role=role_name)
    return AuthResponse(
        user_id=user.id,
        patient_id=patient_id,
        full_name=user.full_name,
        email=user.email,
        phone=user.phone,
        access_token=token,
        role=role_name,
        expires_in_minutes=settings.access_token_expire_minutes,
    )


@router.post("/login", response_model=AuthResponse)
def login(payload: LoginRequest, db: Session = Depends(get_db)) -> AuthResponse:
    identifier = payload.identifier.strip()
    lowered_identifier = identifier.lower()
    normalized_phone = _normalize_phone(identifier)

    user = db.scalar(
        select(User).where(
            or_(
                func.lower(User.email) == lowered_identifier,
                User.phone == normalized_phone,
            )
        )
    )
    if user is None or not verify_password(payload.password, user.password_hash):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid email/phone or password.",
        )

    if not user.is_active:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="This account is deactivated. Contact admin support.",
        )

    role_name = user.role.name
    if payload.expected_role is not None and role_name != payload.expected_role:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=(
                f"The profile already exists as {_role_label(role_name)}. "
                f"Please login as {_role_label(role_name)}."
            ),
        )

    patient_id = None
    if role_name == "patient":
        patient = db.scalar(select(Patient).where(Patient.user_id == user.id))
        if patient:
            patient_id = patient.id

    token = create_access_token(subject=str(user.id), role=role_name)
    return AuthResponse(
        user_id=user.id,
        patient_id=patient_id,
        full_name=user.full_name,
        email=user.email,
        phone=user.phone,
        access_token=token,
        role=role_name,
        expires_in_minutes=settings.access_token_expire_minutes,
    )


@router.post("/profile/setup", response_model=ProfileSetupResponse)
def setup_patient_profile(payload: ProfileSetupRequest, db: Session = Depends(get_db)) -> ProfileSetupResponse:
    user = db.get(User, payload.user_id)
    if user is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found.")

    role_name = user.role.name
    if role_name != "patient":
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Profile setup is currently available for Patient accounts only.",
        )

    profile = user.patient_profile
    if profile is None:
        profile = Patient(user_id=user.id)
        db.add(profile)

    user.full_name = payload.full_name.strip()
    profile.date_of_birth = _age_to_birthdate(payload.age)
    profile.gender = payload.gender
    profile.address = payload.village.strip()
    profile.blood_type = payload.blood_type.strip().upper()

    db.commit()
    db.refresh(user)
    db.refresh(profile)

    return ProfileSetupResponse(
        user_id=user.id,
        full_name=user.full_name,
        email=user.email,
        phone=user.phone,
        age=_birthdate_to_age(profile.date_of_birth),
        gender=profile.gender,
        village=profile.address,
        blood_type=profile.blood_type,
        weight_kg=profile.weight_kg,
        photo_path=profile.photo_path,
        message="Profile setup completed successfully.",
    )


@router.get("/profile/{user_id}", response_model=ProfileSetupResponse)
def get_patient_profile(user_id: int, db: Session = Depends(get_db)) -> ProfileSetupResponse:
    user = db.get(User, user_id)
    if user is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found.")

    if user.role.name != "patient":
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Profile details are currently available for Patient accounts only.",
        )

    profile = user.patient_profile
    if profile is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Profile setup pending.")

    resolved_blood_type = profile.blood_type or profile.emergency_contact
    if profile.gender is None and profile.address is None and resolved_blood_type is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Profile setup pending.")

    return ProfileSetupResponse(
        user_id=user.id,
        full_name=user.full_name,
        email=user.email,
        phone=user.phone,
        age=_birthdate_to_age(profile.date_of_birth),
        gender=profile.gender,
        village=profile.address,
        blood_type=resolved_blood_type,
        weight_kg=profile.weight_kg,
        photo_path=profile.photo_path,
        message="Profile details fetched successfully.",
    )


@router.put("/profile/{user_id}", response_model=ProfileSetupResponse)
def update_patient_profile(
    user_id: int,
    payload: ProfileUpdateRequest,
    db: Session = Depends(get_db),
) -> ProfileSetupResponse:
    user = db.get(User, user_id)
    if user is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found.")

    if user.role.name != "patient":
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Profile updates are currently available for Patient accounts only.",
        )

    profile = user.patient_profile
    if profile is None:
        profile = Patient(user_id=user.id)
        db.add(profile)

    normalized_email = _normalize_email(payload.email)
    normalized_phone = _normalize_phone(payload.phone)

    email_conflict = db.scalar(
        select(User).where(
            func.lower(User.email) == normalized_email,
            User.id != user.id,
        )
    )
    if email_conflict is not None:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Email address is already in use by another account.",
        )

    phone_conflict = db.scalar(
        select(User).where(
            User.phone == normalized_phone,
            User.id != user.id,
        )
    )
    if phone_conflict is not None:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Phone number is already in use by another account.",
        )

    user.full_name = payload.full_name.strip()
    user.email = normalized_email
    user.phone = normalized_phone

    profile.address = payload.village.strip()
    profile.blood_type = payload.blood_type.strip().upper()
    if payload.age is not None:
        profile.date_of_birth = _age_to_birthdate(payload.age)
    if payload.gender is not None:
        profile.gender = payload.gender
    if payload.weight_kg is not None:
        profile.weight_kg = payload.weight_kg
    if payload.photo_path is not None:
        profile.photo_path = payload.photo_path

    db.commit()
    db.refresh(user)
    db.refresh(profile)

    return ProfileSetupResponse(
        user_id=user.id,
        full_name=user.full_name,
        email=user.email,
        phone=user.phone,
        age=_birthdate_to_age(profile.date_of_birth),
        gender=profile.gender,
        village=profile.address,
        blood_type=profile.blood_type,
        weight_kg=profile.weight_kg,
        photo_path=profile.photo_path,
        message="Profile updated successfully.",
    )
