from datetime import datetime

from pydantic import BaseModel


class MedicalRecordCreate(BaseModel):
    patient_id: int
    doctor_id: int | None = None
    record_type: str
    title: str
    description: str | None = None
    file_url: str | None = None


class MedicalRecordOut(BaseModel):
    id: int
    patient_id: int
    record_type: str
    title: str
    description: str | None = None
    file_url: str | None = None
    created_at: datetime
