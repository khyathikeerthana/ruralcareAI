from datetime import UTC, datetime

from fastapi import APIRouter, Depends, HTTPException, Query
from pydantic import BaseModel
from sqlalchemy import and_, func, select
from sqlalchemy.orm import Session

from ..database import get_db
from ..models.appointment import Appointment
from ..models.doctor import Doctor
from ..models.medical_record import MedicalRecord
from ..models.patient import Patient
from ..models.prescription import Prescription
from ..models.user import User
from ..schemas.doctor import DoctorDetail, DoctorSummary

router = APIRouter(tags=["Doctors"])


class DashboardAppointmentOut(BaseModel):
    appointment_id: int
    patient_id: int
    patient_name: str
    patient_location: str | None = None
    scheduled_at: datetime
    status: str
    consultation_mode: str
    reason: str | None = None


class DoctorDashboardOut(BaseModel):
    doctor_id: int
    doctor_name: str
    total_appointments: int
    waiting_appointments: int
    completed_appointments: int
    done_appointments: int
    upcoming_count: int
    up_next: DashboardAppointmentOut | None = None
    queue: list[DashboardAppointmentOut]


class DoctorPatientSummaryOut(BaseModel):
    patient_id: int
    full_name: str
    location: str | None = None
    gender: str | None = None
    age: int | None = None
    status: str
    upcoming_at: datetime | None = None


class DoctorPatientDetailOut(BaseModel):
    patient_id: int
    full_name: str
    email: str | None = None
    phone: str | None = None
    location: str | None = None
    gender: str | None = None
    age: int | None = None
    blood_type: str | None = None
    weight_kg: float | None = None
    emergency_contact: str | None = None
    last_visit_at: datetime | None = None
    primary_reason: str | None = None
    records_count: int
    prescriptions_count: int


class DoctorPatientUpdateIn(BaseModel):
    full_name: str | None = None
    location: str | None = None
    gender: str | None = None
    blood_type: str | None = None
    weight_kg: float | None = None
    emergency_contact: str | None = None


class DoctorAnalyticsOut(BaseModel):
    consultations_done: int
    avg_consultation_minutes: float
    waiting_now: int
    done_today: int
    common_conditions: list[dict[str, int]]


class DoctorAppointmentItemOut(BaseModel):
    appointment_id: int
    patient_id: int
    patient_name: str
    patient_location: str | None = None
    scheduled_at: datetime
    status: str
    consultation_mode: str
    reason: str | None = None


class DoctorProfileOut(BaseModel):
    doctor_id: int
    full_name: str
    email: str | None = None
    phone: str | None = None
    specialization: str
    years_experience: int
    qualification: str | None = None
    hospital_name: str | None = None
    assigned_location: str | None = None
    clinic_hours: str | None = None
    languages: list[str] = []
    profile_photo_path: str | None = None


class DoctorProfileUpdateIn(BaseModel):
    full_name: str | None = None
    email: str | None = None
    phone: str | None = None
    specialization: str | None = None
    years_experience: int | None = None
    qualification: str | None = None
    hospital_name: str | None = None
    assigned_location: str | None = None
    clinic_hours: str | None = None
    languages: list[str] | None = None
    profile_photo_path: str | None = None


def _safe_age(patient: Patient) -> int | None:
    if patient.date_of_birth is None:
        return None
    today = datetime.now(UTC).date()
    years = today.year - patient.date_of_birth.year
    if (today.month, today.day) < (patient.date_of_birth.month, patient.date_of_birth.day):
        years -= 1
    return years


def _dashboard_status(raw_status: str) -> str:
    status = raw_status.lower()
    if status in {"scheduled", "rescheduled"}:
        return "waiting"
    if status in {"completed"}:
        return "done"
    return status


def _as_dashboard_item(appointment: Appointment) -> DashboardAppointmentOut:
    patient_name = "Patient"
    patient_location = None
    if appointment.patient is not None:
        patient_location = appointment.patient.address
        if appointment.patient.user is not None:
            patient_name = appointment.patient.user.full_name

    return DashboardAppointmentOut(
        appointment_id=appointment.id,
        patient_id=appointment.patient_id,
        patient_name=patient_name,
        patient_location=patient_location,
        scheduled_at=appointment.scheduled_at,
        status=_dashboard_status(appointment.status),
        consultation_mode=appointment.consultation_mode,
        reason=appointment.reason,
    )


def _to_appointment_item(appointment: Appointment) -> DoctorAppointmentItemOut:
    patient_name = "Patient"
    patient_location = None
    if appointment.patient is not None:
        patient_location = appointment.patient.address
        if appointment.patient.user is not None:
            patient_name = appointment.patient.user.full_name

    return DoctorAppointmentItemOut(
        appointment_id=appointment.id,
        patient_id=appointment.patient_id,
        patient_name=patient_name,
        patient_location=patient_location,
        scheduled_at=appointment.scheduled_at,
        status=appointment.status,
        consultation_mode=appointment.consultation_mode,
        reason=appointment.reason,
    )

# Keep the mock data for initial seeding if needed
DOCTOR_DIRECTORY: list[DoctorDetail] = [
    DoctorDetail(
        id=1,
        full_name="Dr. Meera Nair",
        specialization="General Medicine",
        years_experience=8,
        is_verified=True,
        bio="Focuses on rural primary care and chronic disease follow-up.",
        license_number="TN-MBBS-12001",
    ),
    DoctorDetail(
        id=2,
        full_name="Dr. Arjun Verma",
        specialization="Pediatrics",
        years_experience=11,
        is_verified=True,
        bio="Supports child growth monitoring and preventive care.",
        license_number="TN-MD-77882",
    ),
    DoctorDetail(
        id=3,
        full_name="Dr. Divya Menon",
        specialization="Dermatology",
        years_experience=6,
        is_verified=False,
        bio="Provides tele-dermatology support for low-bandwidth consultations.",
        license_number="TN-MD-99304",
    ),
]


@router.get("/doctors", response_model=list[DoctorSummary])
def list_doctors(
    specialization: str | None = Query(default=None),
    search: str | None = Query(default=None),
    db: Session = Depends(get_db),
) -> list[DoctorSummary]:
    query = select(Doctor).join(User).order_by(Doctor.id)
    
    if specialization:
        specialization_lower = specialization.strip().lower()
        query = query.where(
            Doctor.specialization.ilike(f"%{specialization_lower}%")
        )
    
    if search:
        search_lower = search.strip().lower()
        query = query.where(
            (User.full_name.ilike(f"%{search_lower}%")) |
            (Doctor.specialization.ilike(f"%{search_lower}%")) |
            (Doctor.bio.ilike(f"%{search_lower}%"))
        )
    
    doctors = db.scalars(query).unique().all()
    
    return [
        DoctorSummary(
            id=doctor.id,
            full_name=doctor.user.full_name,
            specialization=doctor.specialization,
            years_experience=doctor.years_experience or 0,
            is_verified=doctor.is_verified,
            bio=doctor.bio,
        )
        for doctor in doctors
    ]


@router.get("/doctors/{doctor_id}", response_model=DoctorDetail)
def get_doctor(doctor_id: int, db: Session = Depends(get_db)) -> DoctorDetail:
    doctor = db.get(Doctor, doctor_id)
    if doctor is None:
        raise HTTPException(status_code=404, detail="Doctor not found")
    
    return DoctorDetail(
        id=doctor.id,
        full_name=doctor.user.full_name,
        specialization=doctor.specialization,
        years_experience=doctor.years_experience or 0,
        is_verified=doctor.is_verified,
        bio=doctor.bio,
        license_number=doctor.license_number,
    )


@router.get("/doctors/by-user/{user_id}", response_model=DoctorDetail)
def get_doctor_by_user(user_id: int, db: Session = Depends(get_db)) -> DoctorDetail:
    doctor = db.scalar(
        select(Doctor)
        .join(User)
        .where(Doctor.user_id == user_id)
    )
    if doctor is None:
        raise HTTPException(status_code=404, detail="Doctor not found")

    return DoctorDetail(
        id=doctor.id,
        full_name=doctor.user.full_name,
        specialization=doctor.specialization,
        years_experience=doctor.years_experience or 0,
        is_verified=doctor.is_verified,
        bio=doctor.bio,
        license_number=doctor.license_number,
    )


@router.get("/doctors/{doctor_id}/dashboard", response_model=DoctorDashboardOut)
def get_doctor_dashboard(doctor_id: int, db: Session = Depends(get_db)) -> DoctorDashboardOut:
    doctor = db.get(Doctor, doctor_id)
    if doctor is None:
        raise HTTPException(status_code=404, detail="Doctor not found")

    appointments = db.scalars(
        select(Appointment)
        .where(Appointment.doctor_id == doctor_id)
        .order_by(Appointment.scheduled_at)
    ).all()

    now = datetime.now(UTC)
    waiting_statuses = {"scheduled", "rescheduled"}
    done_statuses = {"completed"}

    total_appointments = len(appointments)
    waiting_appointments = sum(1 for item in appointments if item.status in waiting_statuses)
    completed_appointments = sum(1 for item in appointments if item.status in done_statuses)

    upcoming_rows = [item for item in appointments if item.scheduled_at >= now and item.status in waiting_statuses]
    queue = [_as_dashboard_item(item) for item in upcoming_rows[:10]]
    up_next = _as_dashboard_item(upcoming_rows[0]) if upcoming_rows else None

    return DoctorDashboardOut(
        doctor_id=doctor.id,
        doctor_name=doctor.user.full_name,
        total_appointments=total_appointments,
        waiting_appointments=waiting_appointments,
        completed_appointments=completed_appointments,
        done_appointments=completed_appointments,
        upcoming_count=len(upcoming_rows),
        up_next=up_next,
        queue=queue,
    )


@router.get("/doctors/{doctor_id}/appointments", response_model=list[DoctorAppointmentItemOut])
def list_doctor_appointments(
    doctor_id: int,
    status: str | None = Query(default=None),
    db: Session = Depends(get_db),
) -> list[DoctorAppointmentItemOut]:
    doctor = db.get(Doctor, doctor_id)
    if doctor is None:
        raise HTTPException(status_code=404, detail="Doctor not found")

    query = (
        select(Appointment)
        .where(Appointment.doctor_id == doctor_id)
        .order_by(Appointment.scheduled_at.desc())
    )

    if status is not None and status.strip():
        status_tokens = [token.strip().lower() for token in status.split(",") if token.strip()]
        if status_tokens:
            query = query.where(func.lower(Appointment.status).in_(status_tokens))

    appointments = db.scalars(query).all()
    return [_to_appointment_item(item) for item in appointments]


@router.get("/doctors/{doctor_id}/profile", response_model=DoctorProfileOut)
def get_doctor_profile(doctor_id: int, db: Session = Depends(get_db)) -> DoctorProfileOut:
    doctor = db.get(Doctor, doctor_id)
    if doctor is None or doctor.user is None:
        raise HTTPException(status_code=404, detail="Doctor not found")

    languages = [item.strip() for item in (doctor.languages or "").split(",") if item.strip()]

    return DoctorProfileOut(
        doctor_id=doctor.id,
        full_name=doctor.user.full_name,
        email=doctor.user.email,
        phone=doctor.user.phone,
        specialization=doctor.specialization,
        years_experience=doctor.years_experience or 0,
        qualification=doctor.qualification,
        hospital_name=doctor.hospital_name,
        assigned_location=doctor.assigned_location,
        clinic_hours=doctor.bio,
        languages=languages,
        profile_photo_path=doctor.user.profile_photo_path,
    )


@router.put("/doctors/{doctor_id}/profile", response_model=DoctorProfileOut)
def update_doctor_profile(
    doctor_id: int,
    payload: DoctorProfileUpdateIn,
    db: Session = Depends(get_db),
) -> DoctorProfileOut:
    doctor = db.get(Doctor, doctor_id)
    if doctor is None or doctor.user is None:
        raise HTTPException(status_code=404, detail="Doctor not found")

    if payload.full_name is not None:
        doctor.user.full_name = payload.full_name.strip()
    if payload.email is not None:
        doctor.user.email = payload.email.strip().lower()
    if payload.phone is not None:
        doctor.user.phone = payload.phone.strip()

    if payload.specialization is not None:
        doctor.specialization = payload.specialization.strip()
    if payload.years_experience is not None:
        doctor.years_experience = max(0, payload.years_experience)
    if payload.qualification is not None:
        doctor.qualification = payload.qualification.strip() or None
    if payload.hospital_name is not None:
        doctor.hospital_name = payload.hospital_name.strip() or None
    if payload.assigned_location is not None:
        doctor.assigned_location = payload.assigned_location.strip() or None
    if payload.clinic_hours is not None:
        doctor.bio = payload.clinic_hours.strip() or None
    if payload.languages is not None:
        cleaned_languages = [item.strip() for item in payload.languages if item.strip()]
        doctor.languages = ", ".join(cleaned_languages) if cleaned_languages else None
    if payload.profile_photo_path is not None:
        doctor.user.profile_photo_path = payload.profile_photo_path.strip() or None

    db.add(doctor)
    db.add(doctor.user)
    db.commit()
    db.refresh(doctor)
    db.refresh(doctor.user)

    languages = [item.strip() for item in (doctor.languages or "").split(",") if item.strip()]
    return DoctorProfileOut(
        doctor_id=doctor.id,
        full_name=doctor.user.full_name,
        email=doctor.user.email,
        phone=doctor.user.phone,
        specialization=doctor.specialization,
        years_experience=doctor.years_experience or 0,
        qualification=doctor.qualification,
        hospital_name=doctor.hospital_name,
        assigned_location=doctor.assigned_location,
        clinic_hours=doctor.bio,
        languages=languages,
        profile_photo_path=doctor.user.profile_photo_path,
    )


@router.get("/doctors/{doctor_id}/patients", response_model=list[DoctorPatientSummaryOut])
def list_doctor_patients(
    doctor_id: int,
    query: str | None = Query(default=None),
    db: Session = Depends(get_db),
) -> list[DoctorPatientSummaryOut]:
    doctor = db.get(Doctor, doctor_id)
    if doctor is None:
        raise HTTPException(status_code=404, detail="Doctor not found")

    appointments = db.scalars(
        select(Appointment)
        .where(Appointment.doctor_id == doctor_id)
        .order_by(Appointment.scheduled_at.desc())
    ).all()

    latest_by_patient: dict[int, Appointment] = {}
    for appointment in appointments:
        if appointment.patient_id not in latest_by_patient:
            latest_by_patient[appointment.patient_id] = appointment

    results: list[DoctorPatientSummaryOut] = []
    search = query.strip().lower() if query else ""

    for appointment in latest_by_patient.values():
        patient = db.get(Patient, appointment.patient_id)
        if patient is None or patient.user is None:
            continue

        if search:
            haystack = f"{patient.user.full_name} {patient.address or ''} {patient.id}".lower()
            if search not in haystack:
                continue

        status = "STABLE"
        if appointment.status == "completed":
            status = "COMPLETED"
        elif appointment.reason and any(word in appointment.reason.lower() for word in ["chest", "pain", "urgent", "fever", "critical"]):
            status = "URGENT"
        elif appointment.status in {"scheduled", "rescheduled"}:
            status = "NEW"

        results.append(
            DoctorPatientSummaryOut(
                patient_id=patient.id,
                full_name=patient.user.full_name,
                location=patient.address,
                gender=patient.gender,
                age=_safe_age(patient),
                status=status,
                upcoming_at=appointment.scheduled_at,
            )
        )

    return results


@router.get("/doctors/{doctor_id}/patients/{patient_id}", response_model=DoctorPatientDetailOut)
def get_doctor_patient_detail(doctor_id: int, patient_id: int, db: Session = Depends(get_db)) -> DoctorPatientDetailOut:
    doctor = db.get(Doctor, doctor_id)
    if doctor is None:
        raise HTTPException(status_code=404, detail="Doctor not found")

    patient = db.get(Patient, patient_id)
    if patient is None or patient.user is None:
        raise HTTPException(status_code=404, detail="Patient not found")

    latest_visit = db.scalar(
        select(Appointment)
        .where(and_(Appointment.patient_id == patient_id, Appointment.doctor_id == doctor_id))
        .order_by(Appointment.scheduled_at.desc())
    )

    records_count = db.scalar(
        select(func.count(MedicalRecord.id)).where(MedicalRecord.patient_id == patient_id)
    ) or 0
    prescriptions_count = db.scalar(
        select(func.count(Prescription.id)).where(Prescription.patient_id == patient_id)
    ) or 0

    return DoctorPatientDetailOut(
        patient_id=patient.id,
        full_name=patient.user.full_name,
        email=patient.user.email,
        phone=patient.user.phone,
        location=patient.address,
        gender=patient.gender,
        age=_safe_age(patient),
        blood_type=patient.blood_type,
        weight_kg=patient.weight_kg,
        emergency_contact=patient.emergency_contact,
        last_visit_at=latest_visit.scheduled_at if latest_visit is not None else None,
        primary_reason=latest_visit.reason if latest_visit is not None else None,
        records_count=records_count,
        prescriptions_count=prescriptions_count,
    )


@router.put("/doctors/{doctor_id}/patients/{patient_id}", response_model=DoctorPatientDetailOut)
def update_doctor_patient_detail(
    doctor_id: int,
    patient_id: int,
    payload: DoctorPatientUpdateIn,
    db: Session = Depends(get_db),
) -> DoctorPatientDetailOut:
    _ = db.get(Doctor, doctor_id)
    if _ is None:
        raise HTTPException(status_code=404, detail="Doctor not found")

    patient = db.get(Patient, patient_id)
    if patient is None or patient.user is None:
        raise HTTPException(status_code=404, detail="Patient not found")

    if payload.full_name is not None and payload.full_name.strip():
        patient.user.full_name = payload.full_name.strip()
    if payload.location is not None:
        patient.address = payload.location.strip()
    if payload.gender is not None:
        patient.gender = payload.gender.strip()
    if payload.blood_type is not None:
        patient.blood_type = payload.blood_type.strip().upper()
    if payload.weight_kg is not None:
        patient.weight_kg = payload.weight_kg
    if payload.emergency_contact is not None:
        patient.emergency_contact = payload.emergency_contact.strip()

    db.commit()

    return get_doctor_patient_detail(doctor_id=doctor_id, patient_id=patient_id, db=db)


@router.get("/doctors/{doctor_id}/analytics", response_model=DoctorAnalyticsOut)
def get_doctor_analytics(doctor_id: int, db: Session = Depends(get_db)) -> DoctorAnalyticsOut:
    doctor = db.get(Doctor, doctor_id)
    if doctor is None:
        raise HTTPException(status_code=404, detail="Doctor not found")

    appointments = db.scalars(
        select(Appointment)
        .where(Appointment.doctor_id == doctor_id)
        .order_by(Appointment.scheduled_at.desc())
    ).all()

    done = [item for item in appointments if item.status == "completed"]
    waiting = [item for item in appointments if item.status in {"scheduled", "rescheduled"}]

    total_minutes = 0
    if done:
        total_minutes = 15 * len(done)

    condition_counts: dict[str, int] = {}
    for appointment in appointments:
        if not appointment.reason:
            continue
        reason = appointment.reason.lower()
        label = "General"
        if "fever" in reason or "flu" in reason:
            label = "Fever & Flu"
        elif "cough" in reason:
            label = "Seasonal Cough"
        elif "allergy" in reason:
            label = "Allergy"
        elif "diabetes" in reason:
            label = "Diabetes"
        condition_counts[label] = condition_counts.get(label, 0) + 1

    sorted_conditions = sorted(condition_counts.items(), key=lambda item: item[1], reverse=True)

    return DoctorAnalyticsOut(
        consultations_done=len(done),
        avg_consultation_minutes=round((total_minutes / len(done)), 1) if done else 0.0,
        waiting_now=len(waiting),
        done_today=sum(1 for item in done if item.scheduled_at.date() == datetime.now(UTC).date()),
        common_conditions=[{"name": name, "count": count} for name, count in sorted_conditions[:4]],
    )
