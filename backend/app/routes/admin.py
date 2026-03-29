from __future__ import annotations

from datetime import UTC, date, datetime, time, timedelta
from secrets import randbelow

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy import func, or_, select
from sqlalchemy.orm import Session, joinedload

from ..database import get_db
from ..models.appointment import Appointment
from ..models.community_health_worker import CommunityHealthWorker
from ..models.consultation import Consultation
from ..models.doctor import Doctor
from ..models.medical_record import MedicalRecord
from ..models.patient import Patient
from ..models.prescription import Prescription
from ..models.role import Role
from ..models.user import User
from ..schemas.admin import (
    AdminActivityItem,
    AdminAnalyticsResponse,
    AdminDashboardOverviewResponse,
    AdminDoctorCreateRequest,
    AdminDoctorResponse,
    AdminDoctorUpdateRequest,
    AdminOperationResponse,
    AdminPatientResponse,
    AdminWorkerCreateRequest,
    AdminWorkerResponse,
    AdminWorkerUpdateRequest,
    AdminWorkersSummaryResponse,
    AnalyticsRange,
    ConsultationTrendPoint,
    DoctorStatus,
    RegionalClinicPerformanceItem,
    WorkerStatus,
)
from ..services.auth import hash_password

router = APIRouter(prefix="/admin", tags=["Admin"])

ROLE_DESCRIPTIONS = {
    "doctor": "Medical professional providing consultations",
    "community_health_worker": "Community healthcare worker supporting residents",
    "admin": "Platform administrator monitoring operations",
}


def _as_utc(value: datetime | None) -> datetime:
    if value is None:
        return datetime.now(UTC)
    if value.tzinfo is None:
        return value.replace(tzinfo=UTC)
    return value.astimezone(UTC)


def _normalize_email(value: str) -> str:
    return value.strip().lower()


def _normalize_phone(value: str) -> str:
    cleaned = [char for char in value.strip() if char.isdigit() or char == "+"]
    return "".join(cleaned)


def _split_languages(value: str | None) -> list[str]:
    if value is None:
        return []
    return [item.strip() for item in value.split(",") if item.strip()]


def _join_languages(values: list[str] | None) -> str | None:
    if values is None:
        return None
    clean = [item.strip() for item in values if item.strip()]
    return ", ".join(clean) if clean else None


def _relative_time(value: datetime) -> str:
    now = datetime.now(UTC)
    delta = now - _as_utc(value)
    seconds = max(int(delta.total_seconds()), 0)
    if seconds < 60:
        return "just now"
    minutes = seconds // 60
    if minutes < 60:
        return f"{minutes}m ago"
    hours = minutes // 60
    if hours < 24:
        return f"{hours}h ago"
    days = hours // 24
    return f"{days}d ago"


def _doctor_status(doctor: Doctor) -> DoctorStatus:
    if not doctor.user.is_active or doctor.employment_status == "disabled":
        return "disabled"
    if doctor.employment_status == "pending" or not doctor.is_verified:
        return "pending"
    return "active"


def _worker_status(worker: CommunityHealthWorker) -> WorkerStatus:
    if not worker.user.is_active or worker.status == "offline":
        return "offline"
    if worker.status == "on_field":
        return "on_field"
    return "active"


def _get_or_create_role(db: Session, role_name: str) -> Role:
    role = db.scalar(select(Role).where(Role.name == role_name))
    if role is not None:
        return role

    role = Role(name=role_name, description=ROLE_DESCRIPTIONS.get(role_name))
    db.add(role)
    db.flush()
    return role


def _assert_unique_user_contact(
    db: Session,
    *,
    email: str,
    phone: str,
    exclude_user_id: int | None = None,
) -> None:
    conditions = [
        func.lower(User.email) == email,
        User.phone == phone,
    ]

    existing_users = db.scalars(select(User).where(or_(*conditions))).all()
    for existing_user in existing_users:
        if exclude_user_id is not None and existing_user.id == exclude_user_id:
            continue
        if existing_user.email and existing_user.email.lower() == email:
            raise HTTPException(
                status_code=status.HTTP_409_CONFLICT,
                detail="Email address is already in use.",
            )
        if existing_user.phone == phone:
            raise HTTPException(
                status_code=status.HTTP_409_CONFLICT,
                detail="Phone number is already in use.",
            )


def _generate_unique_license_number(db: Session) -> str:
    for _ in range(40):
        candidate = f"DR-{datetime.now(UTC):%y%m%d}-{randbelow(10000):04d}"
        exists = db.scalar(select(Doctor.id).where(Doctor.license_number == candidate))
        if exists is None:
            return candidate
    raise HTTPException(
        status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
        detail="Unable to generate doctor license number.",
    )


def _generate_unique_worker_code(db: Session) -> str:
    for _ in range(40):
        candidate = f"WK-{datetime.now(UTC):%y%m%d}-{randbelow(10000):04d}"
        exists = db.scalar(select(CommunityHealthWorker.id).where(CommunityHealthWorker.worker_code == candidate))
        if exists is None:
            return candidate
    raise HTTPException(
        status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
        detail="Unable to generate worker code.",
    )


def _to_doctor_response(doctor: Doctor) -> AdminDoctorResponse:
    return AdminDoctorResponse(
        id=doctor.id,
        user_id=doctor.user_id,
        full_name=doctor.user.full_name,
        email=doctor.user.email,
        phone=doctor.user.phone,
        specialty=doctor.specialization,
        join_date=_as_utc(doctor.created_at),
        experience_years=doctor.years_experience or 0,
        qualification=doctor.qualification,
        languages=_split_languages(doctor.languages),
        hospital=doctor.hospital_name,
        location=doctor.assigned_location,
        photo_path=doctor.user.profile_photo_path,
        status=_doctor_status(doctor),
        is_verified=doctor.is_verified,
    )


def _to_worker_response(worker: CommunityHealthWorker) -> AdminWorkerResponse:
    return AdminWorkerResponse(
        id=worker.id,
        user_id=worker.user_id,
        full_name=worker.user.full_name,
        email=worker.user.email,
        phone=worker.user.phone,
        worker_code=worker.worker_code,
        assigned_village=worker.assigned_village,
        role_title=worker.role_title,
        join_date=_as_utc(worker.created_at),
        photo_path=worker.user.profile_photo_path,
        status=_worker_status(worker),
    )


def _age_from_birthdate(value: date | None) -> int | None:
    if value is None:
        return None
    today = date.today()
    years = today.year - value.year
    if (today.month, today.day) < (value.month, value.day):
        years -= 1
    return years


def _to_patient_response(patient: Patient) -> AdminPatientResponse:
    return AdminPatientResponse(
        id=patient.id,
        user_id=patient.user_id,
        full_name=patient.user.full_name,
        email=patient.user.email,
        phone=patient.user.phone,
        village=patient.address,
        gender=patient.gender,
        age=_age_from_birthdate(patient.date_of_birth),
        join_date=_as_utc(patient.created_at),
        photo_path=patient.photo_path or patient.user.profile_photo_path,
    )


def _is_created_recently(created_at: datetime, updated_at: datetime) -> bool:
    return abs((_as_utc(updated_at) - _as_utc(created_at)).total_seconds()) < 120


def _collect_recent_activity(db: Session, limit: int) -> list[AdminActivityItem]:
    rows: list[tuple[datetime, AdminActivityItem]] = []
    read_limit = max(limit, 12)

    doctors = db.scalars(
        select(Doctor)
        .options(joinedload(Doctor.user))
        .order_by(Doctor.updated_at.desc())
        .limit(read_limit)
    ).unique().all()
    for doctor in doctors:
        changed_at = _as_utc(doctor.updated_at)
        is_created = _is_created_recently(doctor.created_at, doctor.updated_at)
        action = "created" if is_created else "updated"
        rows.append(
            (
                changed_at,
                AdminActivityItem(
                    id=f"doctor-{doctor.id}-{int(changed_at.timestamp())}",
                    title=f"Doctor profile {action}",
                    subtitle=f"{doctor.user.full_name} | {_relative_time(changed_at)}",
                    category="doctor",
                    timestamp=changed_at,
                ),
            )
        )

    workers = db.scalars(
        select(CommunityHealthWorker)
        .options(joinedload(CommunityHealthWorker.user))
        .order_by(CommunityHealthWorker.updated_at.desc())
        .limit(read_limit)
    ).unique().all()
    for worker in workers:
        changed_at = _as_utc(worker.updated_at)
        is_created = _is_created_recently(worker.created_at, worker.updated_at)
        action = "created" if is_created else "updated"
        rows.append(
            (
                changed_at,
                AdminActivityItem(
                    id=f"worker-{worker.id}-{int(changed_at.timestamp())}",
                    title=f"Health worker profile {action}",
                    subtitle=f"{worker.user.full_name} | {_relative_time(changed_at)}",
                    category="worker",
                    timestamp=changed_at,
                ),
            )
        )

    appointments = db.scalars(
        select(Appointment)
        .options(
            joinedload(Appointment.patient).joinedload(Patient.user),
            joinedload(Appointment.doctor).joinedload(Doctor.user),
        )
        .order_by(Appointment.updated_at.desc())
        .limit(read_limit)
    ).unique().all()
    for appointment in appointments:
        changed_at = _as_utc(appointment.updated_at)
        patient_name = appointment.patient.user.full_name if appointment.patient and appointment.patient.user else "Patient"
        doctor_name = appointment.doctor.user.full_name if appointment.doctor and appointment.doctor.user else "Doctor"

        if appointment.status == "completed":
            title = "Consultation completed"
        elif appointment.status == "cancelled":
            title = "Appointment cancelled"
        elif appointment.status == "rescheduled":
            title = "Appointment rescheduled"
        else:
            title = "Appointment scheduled"

        rows.append(
            (
                changed_at,
                AdminActivityItem(
                    id=f"appointment-{appointment.id}-{int(changed_at.timestamp())}",
                    title=title,
                    subtitle=f"{patient_name} with {doctor_name} | {_relative_time(changed_at)}",
                    category="appointment",
                    timestamp=changed_at,
                ),
            )
        )

    consultations = db.scalars(
        select(Consultation)
        .order_by(Consultation.updated_at.desc())
        .limit(read_limit)
    ).all()
    for consultation in consultations:
        changed_at = _as_utc(consultation.updated_at)
        title = "Consultation ended" if consultation.ended_at else "Consultation started"
        rows.append(
            (
                changed_at,
                AdminActivityItem(
                    id=f"consultation-{consultation.id}-{int(changed_at.timestamp())}",
                    title=title,
                    subtitle=f"Appointment #{consultation.appointment_id} | {_relative_time(changed_at)}",
                    category="consultation",
                    timestamp=changed_at,
                ),
            )
        )

    prescriptions = db.scalars(
        select(Prescription)
        .order_by(Prescription.updated_at.desc())
        .limit(read_limit)
    ).all()
    for prescription in prescriptions:
        changed_at = _as_utc(prescription.updated_at)
        rows.append(
            (
                changed_at,
                AdminActivityItem(
                    id=f"prescription-{prescription.id}-{int(changed_at.timestamp())}",
                    title="Prescription updated",
                    subtitle=f"{prescription.medication_name} | {_relative_time(changed_at)}",
                    category="prescription",
                    timestamp=changed_at,
                ),
            )
        )

    records = db.scalars(
        select(MedicalRecord)
        .order_by(MedicalRecord.updated_at.desc())
        .limit(read_limit)
    ).all()
    for record in records:
        changed_at = _as_utc(record.updated_at)
        rows.append(
            (
                changed_at,
                AdminActivityItem(
                    id=f"record-{record.id}-{int(changed_at.timestamp())}",
                    title="Medical record updated",
                    subtitle=f"{record.title} | {_relative_time(changed_at)}",
                    category="record",
                    timestamp=changed_at,
                ),
            )
        )

    rows.sort(key=lambda item: item[0], reverse=True)
    return [item for _, item in rows[:limit]]


def _day_start_utc(target: date) -> datetime:
    return datetime.combine(target, time.min, tzinfo=UTC)


def _resolve_analytics_window(range_name: AnalyticsRange, now: datetime) -> tuple[datetime | None, datetime, datetime, datetime]:
    if range_name == "week":
        current_start = _day_start_utc(now.date() - timedelta(days=6))
        previous_end = current_start
        previous_start = previous_end - timedelta(days=7)
        return current_start, now, previous_start, previous_end

    if range_name == "month":
        current_start = _day_start_utc(now.date() - timedelta(days=29))
        previous_end = current_start
        previous_start = previous_end - timedelta(days=30)
        return current_start, now, previous_start, previous_end

    if range_name == "year":
        current_start = _day_start_utc(now.date() - timedelta(days=364))
        previous_end = current_start
        previous_start = previous_end - timedelta(days=365)
        return current_start, now, previous_start, previous_end

    previous_end = now - timedelta(days=30)
    previous_start = now - timedelta(days=60)
    return None, now, previous_start, previous_end


def _in_window(value: datetime, start: datetime | None, end: datetime) -> bool:
    timestamp = _as_utc(value)
    if start is not None and timestamp < start:
        return False
    return timestamp < end


@router.get("/dashboard", response_model=AdminDashboardOverviewResponse)
def get_dashboard_overview(
    limit: int = Query(default=6, ge=1, le=20),
    db: Session = Depends(get_db),
) -> AdminDashboardOverviewResponse:
    now = datetime.now(UTC)
    today_start = _day_start_utc(now.date())
    tomorrow_start = today_start + timedelta(days=1)

    total_patients = db.scalar(select(func.count(Patient.id))) or 0
    total_doctors = db.scalar(select(func.count(Doctor.id))) or 0

    active_consultation_sessions = db.scalar(
        select(func.count(Consultation.id)).where(
            Consultation.started_at.is_not(None),
            Consultation.ended_at.is_(None),
        )
    ) or 0

    scheduled_today = db.scalar(
        select(func.count(Appointment.id)).where(
            Appointment.status.in_(["scheduled", "rescheduled"]),
            Appointment.scheduled_at >= today_start,
            Appointment.scheduled_at < tomorrow_start,
        )
    ) or 0

    active_consultations = int(active_consultation_sessions or scheduled_today)

    completed_today = db.scalar(
        select(func.count(Appointment.id)).where(
            Appointment.status == "completed",
            Appointment.updated_at >= today_start,
            Appointment.updated_at < tomorrow_start,
        )
    ) or 0

    recent_activity = _collect_recent_activity(db, limit=limit)

    return AdminDashboardOverviewResponse(
        total_patients=total_patients,
        total_doctors=total_doctors,
        active_consultations=active_consultations,
        completed_today=completed_today,
        recent_activity=recent_activity,
    )


@router.get("/doctors", response_model=list[AdminDoctorResponse])
def list_admin_doctors(
    status_filter: DoctorStatus | None = Query(default=None, alias="status"),
    search: str | None = Query(default=None),
    db: Session = Depends(get_db),
) -> list[AdminDoctorResponse]:
    query = select(Doctor).options(joinedload(Doctor.user)).order_by(Doctor.created_at.desc())
    if search:
        search_value = f"%{search.strip()}%"
        query = query.join(User).where(
            or_(
                User.full_name.ilike(search_value),
                Doctor.specialization.ilike(search_value),
                Doctor.hospital_name.ilike(search_value),
            )
        )

    doctors = db.scalars(query).unique().all()
    results = [_to_doctor_response(item) for item in doctors]
    if status_filter is not None:
        results = [item for item in results if item.status == status_filter]
    return results


@router.get("/patients", response_model=list[AdminPatientResponse])
def list_admin_patients(
    search: str | None = Query(default=None),
    db: Session = Depends(get_db),
) -> list[AdminPatientResponse]:
    query = select(Patient).options(joinedload(Patient.user)).order_by(Patient.created_at.desc())

    if search:
        search_value = f"%{search.strip()}%"
        query = query.join(User).where(
            or_(
                User.full_name.ilike(search_value),
                User.email.ilike(search_value),
                User.phone.ilike(search_value),
                Patient.address.ilike(search_value),
            )
        )

    patients = db.scalars(query).unique().all()
    return [_to_patient_response(item) for item in patients]


@router.get("/doctors/{doctor_id}", response_model=AdminDoctorResponse)
def get_admin_doctor(doctor_id: int, db: Session = Depends(get_db)) -> AdminDoctorResponse:
    doctor = db.scalar(select(Doctor).options(joinedload(Doctor.user)).where(Doctor.id == doctor_id))
    if doctor is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Doctor not found")
    return _to_doctor_response(doctor)


@router.post("/doctors", response_model=AdminDoctorResponse, status_code=status.HTTP_201_CREATED)
def create_admin_doctor(payload: AdminDoctorCreateRequest, db: Session = Depends(get_db)) -> AdminDoctorResponse:
    email = _normalize_email(payload.email)
    phone = _normalize_phone(payload.phone)
    _assert_unique_user_contact(db, email=email, phone=phone)

    role = _get_or_create_role(db, "doctor")
    user = User(
        full_name=payload.full_name.strip(),
        email=email,
        phone=phone,
        profile_photo_path=(payload.photo_path.strip() if payload.photo_path else None),
        password_hash=hash_password(payload.password),
        role_id=role.id,
        is_active=payload.status != "disabled",
    )
    db.add(user)
    db.flush()

    license_number = payload.license_number.strip() if payload.license_number else _generate_unique_license_number(db)
    existing_license = db.scalar(select(Doctor.id).where(Doctor.license_number == license_number))
    if existing_license is not None:
        raise HTTPException(status_code=status.HTTP_409_CONFLICT, detail="License number already exists.")

    doctor = Doctor(
        user_id=user.id,
        specialization=payload.specialty.strip(),
        license_number=license_number,
        years_experience=payload.experience_years,
        qualification=payload.qualification,
        hospital_name=payload.hospital,
        assigned_location=payload.location,
        languages=_join_languages(payload.languages),
        employment_status=payload.status,
        is_verified=payload.is_verified if payload.status != "pending" else False,
    )
    db.add(doctor)
    db.commit()

    created_doctor = db.scalar(select(Doctor).options(joinedload(Doctor.user)).where(Doctor.id == doctor.id))
    if created_doctor is None:
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Unable to load created doctor")
    return _to_doctor_response(created_doctor)


@router.put("/doctors/{doctor_id}", response_model=AdminDoctorResponse)
def update_admin_doctor(
    doctor_id: int,
    payload: AdminDoctorUpdateRequest,
    db: Session = Depends(get_db),
) -> AdminDoctorResponse:
    doctor = db.scalar(select(Doctor).options(joinedload(Doctor.user)).where(Doctor.id == doctor_id))
    if doctor is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Doctor not found")

    user = doctor.user
    if payload.full_name is not None:
        user.full_name = payload.full_name.strip()

    if payload.email is not None:
        email = _normalize_email(payload.email)
        _assert_unique_user_contact(
            db,
            email=email,
            phone=user.phone or "",
            exclude_user_id=user.id,
        )
        user.email = email

    if payload.phone is not None:
        phone = _normalize_phone(payload.phone)
        _assert_unique_user_contact(
            db,
            email=(user.email or "").lower(),
            phone=phone,
            exclude_user_id=user.id,
        )
        user.phone = phone

    if payload.password is not None:
        user.password_hash = hash_password(payload.password)

    if payload.photo_path is not None:
        user.profile_photo_path = payload.photo_path.strip() or None

    if payload.specialty is not None:
        doctor.specialization = payload.specialty.strip()
    if payload.experience_years is not None:
        doctor.years_experience = payload.experience_years
    if payload.qualification is not None:
        doctor.qualification = payload.qualification
    if payload.hospital is not None:
        doctor.hospital_name = payload.hospital
    if payload.location is not None:
        doctor.assigned_location = payload.location
    if payload.languages is not None:
        doctor.languages = _join_languages(payload.languages)
    if payload.is_verified is not None:
        doctor.is_verified = payload.is_verified

    if payload.status is not None:
        doctor.employment_status = payload.status
        user.is_active = payload.status != "disabled"
        if payload.status == "pending":
            doctor.is_verified = False

    db.commit()

    updated = db.scalar(select(Doctor).options(joinedload(Doctor.user)).where(Doctor.id == doctor.id))
    if updated is None:
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Unable to load updated doctor")
    return _to_doctor_response(updated)


@router.post("/doctors/{doctor_id}/deactivate", response_model=AdminOperationResponse)
def deactivate_admin_doctor(doctor_id: int, db: Session = Depends(get_db)) -> AdminOperationResponse:
    doctor = db.scalar(select(Doctor).options(joinedload(Doctor.user)).where(Doctor.id == doctor_id))
    if doctor is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Doctor not found")

    doctor.employment_status = "disabled"
    doctor.user.is_active = False
    db.commit()

    return AdminOperationResponse(message="Doctor profile deactivated successfully.")


@router.delete("/doctors/{doctor_id}", response_model=AdminOperationResponse)
def delete_admin_doctor(doctor_id: int, db: Session = Depends(get_db)) -> AdminOperationResponse:
    doctor = db.scalar(select(Doctor).options(joinedload(Doctor.user)).where(Doctor.id == doctor_id))
    if doctor is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Doctor not found")

    db.delete(doctor.user)
    db.commit()

    return AdminOperationResponse(message="Doctor profile deleted successfully.")


@router.get("/workers", response_model=list[AdminWorkerResponse])
def list_admin_workers(
    status_filter: WorkerStatus | None = Query(default=None, alias="status"),
    search: str | None = Query(default=None),
    db: Session = Depends(get_db),
) -> list[AdminWorkerResponse]:
    query = select(CommunityHealthWorker).options(joinedload(CommunityHealthWorker.user)).order_by(
        CommunityHealthWorker.created_at.desc()
    )

    if search:
        search_value = f"%{search.strip()}%"
        query = query.join(User).where(
            or_(
                User.full_name.ilike(search_value),
                User.email.ilike(search_value),
                CommunityHealthWorker.assigned_village.ilike(search_value),
                CommunityHealthWorker.worker_code.ilike(search_value),
            )
        )

    workers = db.scalars(query).unique().all()
    results = [_to_worker_response(item) for item in workers]
    if status_filter is not None:
        results = [item for item in results if item.status == status_filter]
    return results


@router.get("/workers/summary", response_model=AdminWorkersSummaryResponse)
def get_admin_workers_summary(db: Session = Depends(get_db)) -> AdminWorkersSummaryResponse:
    workers = db.scalars(
        select(CommunityHealthWorker).options(joinedload(CommunityHealthWorker.user))
    ).unique().all()

    active_workers = [item for item in workers if item.user.is_active]
    active_now = [item for item in active_workers if _worker_status(item) in ("active", "on_field")]
    on_field = [item for item in active_workers if _worker_status(item) == "on_field"]

    return AdminWorkersSummaryResponse(
        total_workers=len(active_workers),
        active_now=len(active_now),
        on_field=len(on_field),
    )


@router.get("/workers/{worker_id}", response_model=AdminWorkerResponse)
def get_admin_worker(worker_id: int, db: Session = Depends(get_db)) -> AdminWorkerResponse:
    worker = db.scalar(
        select(CommunityHealthWorker).options(joinedload(CommunityHealthWorker.user)).where(CommunityHealthWorker.id == worker_id)
    )
    if worker is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Health worker not found")
    return _to_worker_response(worker)


@router.post("/workers", response_model=AdminWorkerResponse, status_code=status.HTTP_201_CREATED)
def create_admin_worker(payload: AdminWorkerCreateRequest, db: Session = Depends(get_db)) -> AdminWorkerResponse:
    email = _normalize_email(payload.email)
    phone = _normalize_phone(payload.phone)
    _assert_unique_user_contact(db, email=email, phone=phone)

    role = _get_or_create_role(db, "community_health_worker")
    user = User(
        full_name=payload.full_name.strip(),
        email=email,
        phone=phone,
        profile_photo_path=(payload.photo_path.strip() if payload.photo_path else None),
        password_hash=hash_password(payload.password),
        role_id=role.id,
        is_active=payload.status != "offline",
    )
    db.add(user)
    db.flush()

    worker_code = payload.worker_code.strip() if payload.worker_code else _generate_unique_worker_code(db)
    code_exists = db.scalar(select(CommunityHealthWorker.id).where(CommunityHealthWorker.worker_code == worker_code))
    if code_exists is not None:
        raise HTTPException(status_code=status.HTTP_409_CONFLICT, detail="Worker ID already exists.")

    worker = CommunityHealthWorker(
        user_id=user.id,
        worker_code=worker_code,
        assigned_village=payload.assigned_village,
        role_title=payload.role_title,
        status=payload.status,
        last_seen_at=datetime.now(UTC),
    )
    db.add(worker)
    db.commit()

    created_worker = db.scalar(
        select(CommunityHealthWorker)
        .options(joinedload(CommunityHealthWorker.user))
        .where(CommunityHealthWorker.id == worker.id)
    )
    if created_worker is None:
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Unable to load created worker")
    return _to_worker_response(created_worker)


@router.put("/workers/{worker_id}", response_model=AdminWorkerResponse)
def update_admin_worker(
    worker_id: int,
    payload: AdminWorkerUpdateRequest,
    db: Session = Depends(get_db),
) -> AdminWorkerResponse:
    worker = db.scalar(
        select(CommunityHealthWorker)
        .options(joinedload(CommunityHealthWorker.user))
        .where(CommunityHealthWorker.id == worker_id)
    )
    if worker is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Health worker not found")

    user = worker.user
    if payload.full_name is not None:
        user.full_name = payload.full_name.strip()

    if payload.email is not None:
        email = _normalize_email(payload.email)
        _assert_unique_user_contact(db, email=email, phone=user.phone or "", exclude_user_id=user.id)
        user.email = email

    if payload.phone is not None:
        phone = _normalize_phone(payload.phone)
        _assert_unique_user_contact(db, email=(user.email or "").lower(), phone=phone, exclude_user_id=user.id)
        user.phone = phone

    if payload.password is not None:
        user.password_hash = hash_password(payload.password)

    if payload.photo_path is not None:
        user.profile_photo_path = payload.photo_path.strip() or None

    if payload.worker_code is not None:
        worker_code = payload.worker_code.strip()
        existing_code = db.scalar(
            select(CommunityHealthWorker.id).where(
                CommunityHealthWorker.worker_code == worker_code,
                CommunityHealthWorker.id != worker.id,
            )
        )
        if existing_code is not None:
            raise HTTPException(status_code=status.HTTP_409_CONFLICT, detail="Worker ID already exists.")
        worker.worker_code = worker_code

    if payload.assigned_village is not None:
        worker.assigned_village = payload.assigned_village
    if payload.role_title is not None:
        worker.role_title = payload.role_title
    if payload.status is not None:
        worker.status = payload.status
        user.is_active = payload.status != "offline"
    worker.last_seen_at = datetime.now(UTC)

    db.commit()

    updated = db.scalar(
        select(CommunityHealthWorker)
        .options(joinedload(CommunityHealthWorker.user))
        .where(CommunityHealthWorker.id == worker.id)
    )
    if updated is None:
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Unable to load updated worker")
    return _to_worker_response(updated)


@router.post("/workers/{worker_id}/deactivate", response_model=AdminOperationResponse)
def deactivate_admin_worker(worker_id: int, db: Session = Depends(get_db)) -> AdminOperationResponse:
    worker = db.scalar(
        select(CommunityHealthWorker)
        .options(joinedload(CommunityHealthWorker.user))
        .where(CommunityHealthWorker.id == worker_id)
    )
    if worker is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Health worker not found")

    worker.status = "offline"
    worker.user.is_active = False
    worker.last_seen_at = datetime.now(UTC)
    db.commit()

    return AdminOperationResponse(message="Health worker deactivated successfully.")


@router.delete("/workers/{worker_id}", response_model=AdminOperationResponse)
def delete_admin_worker(worker_id: int, db: Session = Depends(get_db)) -> AdminOperationResponse:
    worker = db.scalar(
        select(CommunityHealthWorker)
        .options(joinedload(CommunityHealthWorker.user))
        .where(CommunityHealthWorker.id == worker_id)
    )
    if worker is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Health worker not found")

    db.delete(worker.user)
    db.commit()

    return AdminOperationResponse(message="Health worker profile deleted successfully.")


@router.get("/analytics", response_model=AdminAnalyticsResponse)
def get_admin_analytics(
    range_name: AnalyticsRange = Query(default="month", alias="range"),
    db: Session = Depends(get_db),
) -> AdminAnalyticsResponse:
    now = datetime.now(UTC)
    current_start, current_end, previous_start, previous_end = _resolve_analytics_window(range_name, now)

    appointments = db.scalars(
        select(Appointment).options(joinedload(Appointment.doctor))
    ).all()

    current_appointments = [
        item for item in appointments if _in_window(_as_utc(item.updated_at), current_start, current_end)
    ]
    current_completed = [item for item in current_appointments if item.status == "completed"]

    previous_completed = [
        item
        for item in appointments
        if item.status == "completed"
        and _as_utc(item.updated_at) >= previous_start
        and _as_utc(item.updated_at) < previous_end
    ]

    current_completed_count = len(current_completed)
    previous_completed_count = len(previous_completed)

    if previous_completed_count == 0:
        consultation_growth = 100.0 if current_completed_count > 0 else 0.0
    else:
        consultation_growth = ((current_completed_count - previous_completed_count) / previous_completed_count) * 100

    consultation_count = len(current_appointments)

    prescriptions = db.scalars(select(Prescription)).all()
    current_prescriptions = [
        item for item in prescriptions if _in_window(_as_utc(item.issued_at), current_start, current_end)
    ]

    completion_ratio = current_completed_count / consultation_count if consultation_count > 0 else 0.0
    prescription_ratio = len(current_prescriptions) / current_completed_count if current_completed_count > 0 else 0.0

    delays: list[float] = []
    timely_completions = 0
    for appointment in current_completed:
        delay_minutes = (_as_utc(appointment.updated_at) - _as_utc(appointment.scheduled_at)).total_seconds() / 60
        bounded_delay = max(delay_minutes, 0.0)
        delays.append(bounded_delay)
        if bounded_delay <= 30.0:
            timely_completions += 1

    timely_completion_ratio = (timely_completions / current_completed_count) if current_completed_count > 0 else 0.0
    if consultation_count == 0:
        patient_satisfaction = 0.0
    else:
        weighted_score = (
            (completion_ratio * 0.50)
            + (timely_completion_ratio * 0.30)
            + (min(prescription_ratio, 1.0) * 0.20)
        )
        patient_satisfaction = max(0.0, min(5.0, weighted_score * 5.0))

    if delays:
        average_delay = sum(delays) / len(delays)
        efficiency_percent = ((30.0 - average_delay) / 30.0) * 100.0
    else:
        efficiency_percent = 0.0
    efficiency_percent = max(-100.0, min(100.0, efficiency_percent))

    workers = db.scalars(
        select(CommunityHealthWorker).options(joinedload(CommunityHealthWorker.user))
    ).unique().all()
    active_workers = [item for item in workers if item.user.is_active]
    active_chws = [item for item in active_workers if _worker_status(item) in ("active", "on_field")]

    today = now.date()
    active_today = [
        item
        for item in active_workers
        if (_as_utc(item.last_seen_at).date() if item.last_seen_at else _as_utc(item.updated_at).date()) == today
    ]

    active_chws_percent = (len(active_today) / len(active_workers) * 100.0) if active_workers else 0.0

    day_points: list[ConsultationTrendPoint] = []
    day_counts: dict[date, int] = {}
    for appointment in appointments:
        if appointment.status != "completed":
            continue
        completed_date = _as_utc(appointment.updated_at).date()
        day_counts[completed_date] = day_counts.get(completed_date, 0) + 1

    for offset in range(6, -1, -1):
        chart_day = now.date() - timedelta(days=offset)
        day_points.append(
            ConsultationTrendPoint(
                label=chart_day.strftime("%a").upper(),
                value=day_counts.get(chart_day, 0),
            )
        )

    doctors = db.scalars(select(Doctor)).all()
    clinic_names = sorted({(doctor.hospital_name or "").strip() for doctor in doctors if (doctor.hospital_name or "").strip()})

    clinic_performance: list[RegionalClinicPerformanceItem] = []
    for clinic_name in clinic_names:
        clinic_appointments = [
            item
            for item in current_appointments
            if item.doctor is not None and (item.doctor.hospital_name or "").strip() == clinic_name
        ]
        total_appointments = len(clinic_appointments)
        completed_appointments = sum(1 for item in clinic_appointments if item.status == "completed")
        performance = (completed_appointments / total_appointments * 100.0) if total_appointments > 0 else 0.0

        clinic_performance.append(
            RegionalClinicPerformanceItem(
                clinic_name=clinic_name,
                total_appointments=total_appointments,
                completed_appointments=completed_appointments,
                performance_percent=round(performance, 1),
            )
        )

    clinic_performance.sort(key=lambda item: item.performance_percent, reverse=True)

    return AdminAnalyticsResponse(
        range=range_name,
        consultation_growth_percent=round(consultation_growth, 1),
        consultation_count=current_completed_count,
        patient_satisfaction_score=round(patient_satisfaction, 1),
        patient_satisfaction_samples=max(current_completed_count, consultation_count),
        efficiency_percent=round(efficiency_percent, 1),
        active_chws=len(active_chws),
        active_chws_percent=round(active_chws_percent, 1),
        consultation_trends=day_points,
        regional_clinic_performance=clinic_performance,
    )
