from datetime import datetime
from typing import Literal

from pydantic import BaseModel, Field

SeverityLabel = Literal["Low Severity", "Moderate Severity", "High Severity"]


class SymptomAnalysisRequest(BaseModel):
    patient_id: int | None = None
    symptoms: str = Field(min_length=5, max_length=3000)
    language: str | None = Field(default=None, max_length=40)


class SymptomAnalysisResponse(BaseModel):
    severity_label: SeverityLabel
    severity_score: int = Field(ge=0, le=100)
    triage_note: str
    possible_causes: list[str]
    matched_symptoms: list[str]
    immediate_actions: list[str]
    home_care: list[str]
    red_flags: list[str]
    disclaimer: str
    provider: str
    generated_at: datetime
