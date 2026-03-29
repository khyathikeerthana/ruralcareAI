from datetime import datetime

from pydantic import BaseModel, Field


class ChatThreadOut(BaseModel):
    appointment_id: int | None = None
    doctor_id: int
    doctor_name: str
    specialization: str
    last_message: str
    last_message_at: datetime
    unread_count: int = 0


class DoctorChatThreadOut(BaseModel):
    appointment_id: int | None = None
    patient_id: int
    patient_name: str
    patient_location: str | None = None
    last_message: str
    last_message_at: datetime
    unread_count: int = 0


class ChatMessageOut(BaseModel):
    id: int
    appointment_id: int | None = None
    sender_user_id: int
    receiver_user_id: int
    message_text: str
    sent_at: datetime
    is_read: bool
    is_mine: bool


class ChatMessageCreate(BaseModel):
    patient_id: int
    doctor_id: int
    appointment_id: int | None = None
    sender_role: str = Field(default="patient")
    message_text: str = Field(min_length=1, max_length=2000)
