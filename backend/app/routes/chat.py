from datetime import datetime, timezone

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy import and_, func, or_, select
from sqlalchemy.orm import Session, joinedload

from ..database import get_db
from ..models.appointment import Appointment
from ..models.doctor import Doctor
from ..models.message import Message
from ..models.patient import Patient
from ..schemas.chat import ChatMessageCreate, ChatMessageOut, ChatThreadOut, DoctorChatThreadOut

router = APIRouter(tags=["Chat"])


def _get_patient(db: Session, patient_id: int) -> Patient:
    patient = db.get(Patient, patient_id)
    if patient is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Patient not found")
    return patient


def _get_doctor(db: Session, doctor_id: int) -> Doctor:
    doctor = db.get(Doctor, doctor_id)
    if doctor is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Doctor not found")
    return doctor


def _resolve_chat_appointment(
    db: Session,
    *,
    patient_id: int,
    doctor_id: int,
    appointment_id: int | None,
) -> Appointment | None:
    if appointment_id is not None:
        appointment = db.get(Appointment, appointment_id)
        if appointment is None:
            raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Appointment not found")
        if appointment.patient_id != patient_id or appointment.doctor_id != doctor_id:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Appointment does not match the selected patient and doctor",
            )
        return appointment

    return db.scalar(
        select(Appointment)
        .where(
            and_(
                Appointment.patient_id == patient_id,
                Appointment.doctor_id == doctor_id,
            )
        )
        .order_by(Appointment.scheduled_at.desc())
    )


@router.get("/chat/threads/{patient_id}", response_model=list[ChatThreadOut])
def get_chat_threads(patient_id: int, db: Session = Depends(get_db)) -> list[ChatThreadOut]:
    patient = _get_patient(db, patient_id)

    appointments = db.scalars(
        select(Appointment)
        .options(joinedload(Appointment.doctor).joinedload(Doctor.user))
        .where(Appointment.patient_id == patient_id)
        .order_by(Appointment.scheduled_at.desc())
    ).all()

    latest_appointment_by_doctor: dict[int, Appointment] = {}
    for appointment in appointments:
        if appointment.doctor_id not in latest_appointment_by_doctor:
            latest_appointment_by_doctor[appointment.doctor_id] = appointment

    threads: list[ChatThreadOut] = []
    for doctor_id, appointment in latest_appointment_by_doctor.items():
        doctor = appointment.doctor
        if doctor is None or doctor.user is None:
            continue

        doctor_user_id = doctor.user_id
        latest_message = db.scalar(
            select(Message)
            .where(
                or_(
                    and_(
                        Message.sender_user_id == patient.user_id,
                        Message.receiver_user_id == doctor_user_id,
                    ),
                    and_(
                        Message.sender_user_id == doctor_user_id,
                        Message.receiver_user_id == patient.user_id,
                    ),
                )
            )
            .order_by(Message.sent_at.desc())
        )

        unread_count = db.scalar(
            select(func.count(Message.id))
            .where(
                and_(
                    Message.sender_user_id == doctor_user_id,
                    Message.receiver_user_id == patient.user_id,
                    Message.is_read.is_(False),
                )
            )
        ) or 0

        threads.append(
            ChatThreadOut(
                appointment_id=appointment.id,
                doctor_id=doctor.id,
                doctor_name=doctor.user.full_name,
                specialization=doctor.specialization,
                last_message=(
                    latest_message.message_text
                    if latest_message is not None and latest_message.message_text.strip()
                    else (appointment.reason.strip() if appointment.reason and appointment.reason.strip() else "No messages yet")
                ),
                last_message_at=(
                    latest_message.sent_at
                    if latest_message is not None
                    else appointment.scheduled_at
                ),
                unread_count=int(unread_count),
            )
        )

    threads.sort(key=lambda item: item.last_message_at, reverse=True)
    return threads


@router.get("/chat/threads/doctor/{doctor_id}", response_model=list[DoctorChatThreadOut])
def get_doctor_chat_threads(doctor_id: int, db: Session = Depends(get_db)) -> list[DoctorChatThreadOut]:
    doctor = _get_doctor(db, doctor_id)

    appointments = db.scalars(
        select(Appointment)
        .options(joinedload(Appointment.patient).joinedload(Patient.user))
        .where(Appointment.doctor_id == doctor_id)
        .order_by(Appointment.scheduled_at.desc())
    ).all()

    latest_appointment_by_patient: dict[int, Appointment] = {}
    for appointment in appointments:
        if appointment.patient_id not in latest_appointment_by_patient:
            latest_appointment_by_patient[appointment.patient_id] = appointment

    threads: list[DoctorChatThreadOut] = []
    for patient_id, appointment in latest_appointment_by_patient.items():
        patient = appointment.patient
        if patient is None or patient.user is None:
            continue

        latest_message = db.scalar(
            select(Message)
            .where(
                or_(
                    and_(
                        Message.sender_user_id == patient.user_id,
                        Message.receiver_user_id == doctor.user_id,
                    ),
                    and_(
                        Message.sender_user_id == doctor.user_id,
                        Message.receiver_user_id == patient.user_id,
                    ),
                )
            )
            .order_by(Message.sent_at.desc())
        )

        unread_count = db.scalar(
            select(func.count(Message.id))
            .where(
                and_(
                    Message.sender_user_id == patient.user_id,
                    Message.receiver_user_id == doctor.user_id,
                    Message.is_read.is_(False),
                )
            )
        ) or 0

        threads.append(
            DoctorChatThreadOut(
                appointment_id=appointment.id,
                patient_id=patient.id,
                patient_name=patient.user.full_name,
                patient_location=patient.address,
                last_message=(
                    latest_message.message_text
                    if latest_message is not None and latest_message.message_text.strip()
                    else (appointment.reason.strip() if appointment.reason and appointment.reason.strip() else "No messages yet")
                ),
                last_message_at=(
                    latest_message.sent_at
                    if latest_message is not None
                    else appointment.scheduled_at
                ),
                unread_count=int(unread_count),
            )
        )

    threads.sort(key=lambda item: item.last_message_at, reverse=True)
    return threads


@router.get("/chat/messages", response_model=list[ChatMessageOut])
def get_chat_messages(
    patient_id: int = Query(...),
    doctor_id: int = Query(...),
    appointment_id: int | None = Query(default=None),
    viewer_role: str = Query(default="patient"),
    db: Session = Depends(get_db),
) -> list[ChatMessageOut]:
    patient = _get_patient(db, patient_id)
    doctor = _get_doctor(db, doctor_id)

    resolved_appointment = _resolve_chat_appointment(
        db,
        patient_id=patient_id,
        doctor_id=doctor_id,
        appointment_id=appointment_id,
    )

    viewer = viewer_role.strip().lower()
    if viewer not in {"patient", "doctor"}:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="viewer_role must be patient or doctor")

    query = (
        select(Message)
        .where(
            or_(
                and_(
                    Message.sender_user_id == patient.user_id,
                    Message.receiver_user_id == doctor.user_id,
                ),
                and_(
                    Message.sender_user_id == doctor.user_id,
                    Message.receiver_user_id == patient.user_id,
                ),
            )
        )
        .order_by(Message.sent_at.asc())
    )

    if resolved_appointment is not None:
        query = query.where(Message.appointment_id == resolved_appointment.id)

    messages = db.scalars(query).all()

    unread_target_user_id = patient.user_id if viewer == "patient" else doctor.user_id
    mine_user_id = patient.user_id if viewer == "patient" else doctor.user_id

    unread_for_viewer = [
        message
        for message in messages
        if message.receiver_user_id == unread_target_user_id and not message.is_read
    ]
    if unread_for_viewer:
        for message in unread_for_viewer:
            message.is_read = True
        db.commit()

    return [
        ChatMessageOut(
            id=message.id,
            appointment_id=message.appointment_id,
            sender_user_id=message.sender_user_id,
            receiver_user_id=message.receiver_user_id,
            message_text=message.message_text,
            sent_at=message.sent_at,
            is_read=message.is_read,
            is_mine=message.sender_user_id == mine_user_id,
        )
        for message in messages
    ]


@router.post("/chat/messages", response_model=ChatMessageOut, status_code=201)
def send_chat_message(payload: ChatMessageCreate, db: Session = Depends(get_db)) -> ChatMessageOut:
    patient = _get_patient(db, payload.patient_id)
    doctor = _get_doctor(db, payload.doctor_id)

    sender_role = payload.sender_role.strip().lower()
    if sender_role not in {"patient", "doctor"}:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="sender_role must be patient or doctor")

    resolved_appointment = _resolve_chat_appointment(
        db,
        patient_id=payload.patient_id,
        doctor_id=payload.doctor_id,
        appointment_id=payload.appointment_id,
    )

    sender_user_id = patient.user_id if sender_role == "patient" else doctor.user_id
    receiver_user_id = doctor.user_id if sender_role == "patient" else patient.user_id

    message = Message(
        appointment_id=resolved_appointment.id if resolved_appointment is not None else None,
        sender_user_id=sender_user_id,
        receiver_user_id=receiver_user_id,
        message_text=payload.message_text.strip(),
        sent_at=datetime.now(timezone.utc),
        is_read=False,
    )

    db.add(message)
    db.commit()
    db.refresh(message)

    return ChatMessageOut(
        id=message.id,
        appointment_id=message.appointment_id,
        sender_user_id=message.sender_user_id,
        receiver_user_id=message.receiver_user_id,
        message_text=message.message_text,
        sent_at=message.sent_at,
        is_read=message.is_read,
        is_mine=sender_role == "patient",
    )
