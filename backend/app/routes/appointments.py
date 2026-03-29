from datetime import datetime, timezone

from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy import and_, select
from sqlalchemy.orm import Session

from ..database import get_db
from ..models.appointment import Appointment
from ..schemas.appointment import AppointmentCreate, AppointmentOut, AppointmentUpdate

router = APIRouter(tags=["Appointments"])


def _to_appointment_out(appointment: Appointment) -> AppointmentOut:
    doctor_name = None
    specialization = None
    patient_name = None
    patient_location = None

    if appointment.doctor is not None:
        specialization = appointment.doctor.specialization
        if appointment.doctor.user is not None:
            doctor_name = appointment.doctor.user.full_name

    if appointment.patient is not None:
        patient_location = appointment.patient.address
        if appointment.patient.user is not None:
            patient_name = appointment.patient.user.full_name

    return AppointmentOut(
        id=appointment.id,
        patient_id=appointment.patient_id,
        doctor_id=appointment.doctor_id,
        scheduled_at=appointment.scheduled_at,
        consultation_mode=appointment.consultation_mode,
        reason=appointment.reason,
        status=appointment.status,
        created_at=appointment.created_at,
        doctor_name=doctor_name,
        specialization=specialization,
        patient_name=patient_name,
        patient_location=patient_location,
    )


@router.post("/appointments", response_model=AppointmentOut, status_code=201)
def create_appointment(payload: AppointmentCreate, db: Session = Depends(get_db)) -> AppointmentOut:
    appointment = Appointment(
        patient_id=payload.patient_id,
        doctor_id=payload.doctor_id,
        scheduled_at=payload.scheduled_at,
        consultation_mode=payload.consultation_mode,
        reason=payload.reason,
        status="scheduled",
    )
    db.add(appointment)
    db.commit()
    db.refresh(appointment)

    return _to_appointment_out(appointment)


@router.get("/appointments", response_model=list[AppointmentOut])
def list_appointments(
    patient_id: int | None = Query(default=None),
    doctor_id: int | None = Query(default=None),
    db: Session = Depends(get_db),
) -> list[AppointmentOut]:
    query = select(Appointment)
    
    if patient_id is not None:
        query = query.where(Appointment.patient_id == patient_id)
    if doctor_id is not None:
        query = query.where(Appointment.doctor_id == doctor_id)

    appointments = db.scalars(query.order_by(Appointment.scheduled_at)).all()

    return [_to_appointment_out(appt) for appt in appointments]


@router.put("/appointments/{appointment_id}", response_model=AppointmentOut)
def update_appointment(
    appointment_id: int,
    payload: AppointmentUpdate,
    db: Session = Depends(get_db),
) -> AppointmentOut:
    appointment = db.get(Appointment, appointment_id)
    if appointment is None:
        raise HTTPException(status_code=404, detail="Appointment not found")

    appointment.status = payload.status
    db.commit()
    db.refresh(appointment)

    return _to_appointment_out(appointment)


@router.get("/appointments/upcoming/{patient_id}", response_model=AppointmentOut | None)
def get_upcoming_appointment(
    patient_id: int,
    db: Session = Depends(get_db),
) -> AppointmentOut | None:
    now = datetime.now(timezone.utc)
    appointment = db.scalar(
        select(Appointment)
        .where(
            and_(
                Appointment.patient_id == patient_id,
                Appointment.status == "scheduled",
                Appointment.scheduled_at > now,
            )
        )
        .order_by(Appointment.scheduled_at)
    )

    if appointment is None:
        return None

    return _to_appointment_out(appointment)
