from datetime import datetime

from pydantic import BaseModel, Field


class PrescriptionCreate(BaseModel):
    consultation_id: int
    patient_id: int
    doctor_id: int
    medication_name: str = Field(min_length=2, max_length=120)
    dosage: str = Field(min_length=2, max_length=120)
    instructions: str | None = Field(default=None, max_length=500)


class PrescriptionOut(BaseModel):
    id: int
    consultation_id: int
    patient_id: int
    doctor_id: int
    medication_name: str
    dosage: str
    instructions: str | None = None
    issued_at: datetime
