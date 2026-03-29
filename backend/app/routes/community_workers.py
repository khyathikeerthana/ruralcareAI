from __future__ import annotations

import json
import secrets
import uuid
from datetime import UTC, date, datetime
from typing import Literal

from fastapi import APIRouter, Depends, HTTPException, status
from pydantic import BaseModel, Field
from sqlalchemy import func, or_, select
from sqlalchemy.orm import Session, joinedload

from ..database import get_db
from ..models.community_health_worker import CommunityHealthWorker
from ..models.chw_camp_schedule import ChwCampSchedule
from ..models.medical_record import MedicalRecord
from ..models.patient import Patient
from ..models.role import Role
from ..models.user import User
from ..services.auth import hash_password

router = APIRouter(prefix="/community-health-workers", tags=["CommunityHealthWorkers"])


class ChwWorkerProfileResponse(BaseModel):
    id: int
    user_id: int
    full_name: str
    worker_code: str
    assigned_village: str | None = None


class ChwDashboardStatsResponse(BaseModel):
    total_patients: int
    visits_today: int
    vitals_checked: int
    assigned_village: str | None = None


class ChwRegisterPatientRequest(BaseModel):
    full_name: str = Field(min_length=2, max_length=120)
    age: int = Field(ge=1, le=120)
    gender: Literal["Male", "Female", "Other"]
    aadhaar_id: str | None = Field(default=None, max_length=40)
    phone: str | None = Field(default=None, max_length=20)
    height_cm: float | None = Field(default=None, ge=0)
    weight_kg: float | None = Field(default=None, ge=0)
    blood_type: str | None = Field(default=None, max_length=12)
    photo_base64: str | None = None


class ChwRegisterPatientResponse(BaseModel):
    patient_id: int
    user_id: int
    full_name: str
    village: str
    message: str


class ChwCampScheduleRequest(BaseModel):
    village: str
    primary_focus: str
    sub_focus: list[str] | None = None
    scheduled_date: date
    slot: str


class ChwCampScheduleResponse(BaseModel):
    id: int
    worker_id: int
    village: str
    primary_focus: str
    sub_focus: list[str] | None = None
    scheduled_at: datetime
    slot: str
    status: str


def _normalize_phone(value: str) -> str:
    cleaned = [char for char in value.strip() if char.isdigit() or char == "+"]
    return "".join(cleaned)


def _get_or_create_role(db: Session, role_name: str) -> Role:
    role = db.scalar(select(Role).where(Role.name == role_name))
    if role is not None:
        return role

    role = Role(name=role_name, description=f"{role_name.replace('_', ' ').title()} account")
    db.add(role)
    db.flush()
    return role


def _resolve_worker_or_404(db: Session, worker_id: int) -> CommunityHealthWorker:
    worker = db.scalar(
        select(CommunityHealthWorker)
        .options(joinedload(CommunityHealthWorker.user))
        .where(CommunityHealthWorker.id == worker_id)
    )
    if worker is None or worker.user is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Health worker not found")
    return worker


def _is_created_today_utc(record: MedicalRecord) -> bool:
    created = record.created_at
    if created is None:
        return False

    if created.tzinfo is not None:
        created_date = created.astimezone(UTC).date()
    else:
        created_date = created.date()
    return created_date == date.today()


def _generate_unique_patient_email(db: Session, full_name: str) -> str:
    base_slug = ".".join(part for part in full_name.lower().split() if part)
    base_slug = (base_slug or "patient").replace("@", "").replace(" ", "")

    for _ in range(8):
        suffix = uuid.uuid4().hex[:8]
        candidate = f"{base_slug}.{suffix}@patient.local"
        exists = db.scalar(select(User.id).where(func.lower(User.email) == candidate.lower()))
        if exists is None:
            return candidate

    raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Unable to allocate patient email")


@router.get("/by-user/{user_id}", response_model=ChwWorkerProfileResponse)
def get_worker_by_user(user_id: int, db: Session = Depends(get_db)) -> ChwWorkerProfileResponse:
    worker = db.scalar(
        select(CommunityHealthWorker)
        .options(joinedload(CommunityHealthWorker.user))
        .where(CommunityHealthWorker.user_id == user_id)
    )
    if worker is None or worker.user is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Health worker profile not found")

    return ChwWorkerProfileResponse(
        id=worker.id,
        user_id=worker.user_id,
        full_name=worker.user.full_name,
        worker_code=worker.worker_code,
        assigned_village=worker.assigned_village,
    )


@router.get("/{worker_id}/dashboard", response_model=ChwDashboardStatsResponse)
def get_worker_dashboard_stats(worker_id: int, db: Session = Depends(get_db)) -> ChwDashboardStatsResponse:
    worker = _resolve_worker_or_404(db, worker_id)
    assigned_village = (worker.assigned_village or "").strip()
    if not assigned_village:
        return ChwDashboardStatsResponse(
            total_patients=0,
            visits_today=0,
            vitals_checked=0,
            assigned_village=None,
        )

    patient_ids = db.scalars(
        select(Patient.id).where(func.lower(func.coalesce(Patient.address, "")) == assigned_village.lower())
    ).all()
    if not patient_ids:
        return ChwDashboardStatsResponse(
            total_patients=0,
            visits_today=0,
            vitals_checked=0,
            assigned_village=assigned_village,
        )

    records = db.scalars(select(MedicalRecord).where(MedicalRecord.patient_id.in_(patient_ids))).all()
    visits_today = len({record.patient_id for record in records if _is_created_today_utc(record)})
    vitals_checked = sum(1 for record in records if (record.record_type or "").lower() == "vitals_capture")

    return ChwDashboardStatsResponse(
        total_patients=len(patient_ids),
        visits_today=visits_today,
        vitals_checked=vitals_checked,
        assigned_village=assigned_village,
    )


@router.post("/{worker_id}/camp-schedules", response_model=ChwCampScheduleResponse, status_code=status.HTTP_201_CREATED)
def create_camp_schedule_for_worker(worker_id: int, payload: ChwCampScheduleRequest, db: Session = Depends(get_db)) -> ChwCampScheduleResponse:
    worker = _resolve_worker_or_404(db, worker_id)
    if not worker.assigned_village or not worker.assigned_village.strip():
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Worker does not have an assigned village.")

    if payload.village.strip().lower() != worker.assigned_village.strip().lower():
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Camp village must match worker assigned village.")

    scheduled_at = datetime.combine(payload.scheduled_date, datetime.min.time())

    schedule = ChwCampSchedule(
        worker_id=worker.id,
        village=payload.village.strip(),
        primary_focus=payload.primary_focus.strip(),
        sub_focus=json.dumps(payload.sub_focus or []),
        scheduled_at=scheduled_at,
        slot=payload.slot.strip(),
        status="scheduled",
    )
    db.add(schedule)
    db.commit()
    db.refresh(schedule)

    return ChwCampScheduleResponse(
        id=schedule.id,
        worker_id=schedule.worker_id,
        village=schedule.village,
        primary_focus=schedule.primary_focus,
        sub_focus=json.loads(schedule.sub_focus) if schedule.sub_focus else None,
        scheduled_at=schedule.scheduled_at,
        slot=schedule.slot,
        status=schedule.status,
    )


@router.post("/{worker_id}/patients", response_model=ChwRegisterPatientResponse, status_code=status.HTTP_201_CREATED)
def register_patient_by_worker(
    worker_id: int,
    payload: ChwRegisterPatientRequest,
    db: Session = Depends(get_db),
) -> ChwRegisterPatientResponse:
    worker = _resolve_worker_or_404(db, worker_id)
    village = (worker.assigned_village or "").strip()
    if not village:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Worker does not have an assigned village. Ask admin to assign one before registering patients.",
        )

    normalized_phone = _normalize_phone(payload.phone) if payload.phone else None
    if normalized_phone:
        user_exists = db.scalar(select(User.id).where(User.phone == normalized_phone))
        if user_exists is not None:
            raise HTTPException(status_code=status.HTTP_409_CONFLICT, detail="Phone number already belongs to another account.")

    generated_email = _generate_unique_patient_email(db, payload.full_name.strip())

    role = _get_or_create_role(db, "patient")
    user = User(
        full_name=payload.full_name.strip(),
        email=generated_email,
        phone=normalized_phone,
        password_hash=hash_password(secrets.token_urlsafe(18)),
        role_id=role.id,
        is_active=True,
    )
    db.add(user)
    db.flush()

    today = date.today()
    birth_date = date(today.year - payload.age, 1, 1)
    blood_type = payload.blood_type.strip().upper() if payload.blood_type else None
    aadhaar = payload.aadhaar_id.strip() if payload.aadhaar_id else None

    patient = Patient(
        user_id=user.id,
        date_of_birth=birth_date,
        gender=payload.gender,
        address=village,
        blood_type=blood_type,
        emergency_contact=aadhaar,
        weight_kg=payload.weight_kg,
        photo_path=(payload.photo_base64.strip() if payload.photo_base64 else None),
    )
    db.add(patient)
    db.flush()

    vitals_description = {
        "captured_by": "community_health_worker",
        "captured_by_worker_id": worker.id,
        "height_cm": payload.height_cm,
        "weight_kg": payload.weight_kg,
        "blood_type": blood_type,
        "aadhaar_id": aadhaar,
    }
    record = MedicalRecord(
        patient_id=patient.id,
        doctor_id=None,
        record_type="vitals_capture",
        title="Initial vitals captured by CHW",
        description=json.dumps(vitals_description),
        file_url=None,
    )
    db.add(record)

    db.commit()

    return ChwRegisterPatientResponse(
        patient_id=patient.id,
        user_id=user.id,
        full_name=user.full_name,
        village=village,
        message="Patient registered successfully.",
    )
