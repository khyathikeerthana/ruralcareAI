from datetime import datetime
from typing import Literal

from pydantic import BaseModel, Field

AppointmentStatus = Literal["scheduled", "rescheduled", "completed", "cancelled"]
ConsultationMode = Literal["video", "audio", "chat"]


class AppointmentCreate(BaseModel):
    patient_id: int
    doctor_id: int
    scheduled_at: datetime
    consultation_mode: ConsultationMode = "video"
    reason: str = Field(min_length=3, max_length=500)


class AppointmentUpdate(BaseModel):
    status: AppointmentStatus


class AppointmentOut(BaseModel):
    id: int
    patient_id: int
    doctor_id: int
    scheduled_at: datetime
    consultation_mode: ConsultationMode
    reason: str | None = None
    status: AppointmentStatus
    created_at: datetime
    doctor_name: str | None = None
    specialization: str | None = None
    patient_name: str | None = None
    patient_location: str | None = None
