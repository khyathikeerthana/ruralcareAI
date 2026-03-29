from datetime import datetime
from typing import Literal

from pydantic import BaseModel

ConsultationMode = Literal["video", "audio", "chat"]


class ConsultationStartRequest(BaseModel):
    appointment_id: int
    mode: ConsultationMode = "video"


class ConsultationEndRequest(BaseModel):
    consultation_id: int
    notes: str | None = None


class ConsultationResponse(BaseModel):
    consultation_id: int
    appointment_id: int
    mode: ConsultationMode
    status: str
    updated_at: datetime
