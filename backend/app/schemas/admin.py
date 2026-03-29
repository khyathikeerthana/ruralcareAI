from __future__ import annotations

from datetime import datetime
from typing import Literal

from pydantic import BaseModel, Field

AnalyticsRange = Literal["week", "month", "year", "all_time"]
DoctorStatus = Literal["active", "pending", "disabled"]
WorkerStatus = Literal["active", "on_field", "offline"]
ActivityCategory = Literal[
    "doctor",
    "worker",
    "appointment",
    "consultation",
    "prescription",
    "record",
    "account",
    "system",
]


class AdminActivityItem(BaseModel):
    id: str
    title: str
    subtitle: str
    category: ActivityCategory = "system"
    timestamp: datetime


class AdminDashboardOverviewResponse(BaseModel):
    total_patients: int
    total_doctors: int
    active_consultations: int
    completed_today: int
    recent_activity: list[AdminActivityItem] = Field(default_factory=list)


class AdminDoctorCreateRequest(BaseModel):
    full_name: str = Field(min_length=2, max_length=120)
    email: str = Field(min_length=5, max_length=255)
    phone: str = Field(min_length=7, max_length=20)
    password: str = Field(min_length=8, max_length=72)
    specialty: str = Field(min_length=2, max_length=80)
    experience_years: int = Field(default=0, ge=0, le=80)
    qualification: str | None = Field(default=None, max_length=120)
    hospital: str | None = Field(default=None, max_length=180)
    location: str | None = Field(default=None, max_length=255)
    languages: list[str] = Field(default_factory=list)
    status: DoctorStatus = "active"
    is_verified: bool = False
    license_number: str | None = Field(default=None, max_length=50)
    photo_path: str | None = None


class AdminDoctorUpdateRequest(BaseModel):
    full_name: str | None = Field(default=None, min_length=2, max_length=120)
    email: str | None = Field(default=None, min_length=5, max_length=255)
    phone: str | None = Field(default=None, min_length=7, max_length=20)
    password: str | None = Field(default=None, min_length=8, max_length=72)
    specialty: str | None = Field(default=None, min_length=2, max_length=80)
    experience_years: int | None = Field(default=None, ge=0, le=80)
    qualification: str | None = Field(default=None, max_length=120)
    hospital: str | None = Field(default=None, max_length=180)
    location: str | None = Field(default=None, max_length=255)
    languages: list[str] | None = None
    status: DoctorStatus | None = None
    is_verified: bool | None = None
    photo_path: str | None = None


class AdminDoctorResponse(BaseModel):
    id: int
    user_id: int
    full_name: str
    email: str | None = None
    phone: str | None = None
    specialty: str
    join_date: datetime
    experience_years: int
    qualification: str | None = None
    languages: list[str] = Field(default_factory=list)
    hospital: str | None = None
    location: str | None = None
    photo_path: str | None = None
    status: DoctorStatus
    is_verified: bool


class AdminWorkerCreateRequest(BaseModel):
    full_name: str = Field(min_length=2, max_length=120)
    email: str = Field(min_length=5, max_length=255)
    phone: str = Field(min_length=7, max_length=20)
    password: str = Field(min_length=8, max_length=72)
    worker_code: str | None = Field(default=None, max_length=50)
    assigned_village: str | None = Field(default=None, max_length=255)
    role_title: str = Field(default="Community Health Worker", min_length=2, max_length=80)
    status: WorkerStatus = "active"
    photo_path: str | None = None


class AdminWorkerUpdateRequest(BaseModel):
    full_name: str | None = Field(default=None, min_length=2, max_length=120)
    email: str | None = Field(default=None, min_length=5, max_length=255)
    phone: str | None = Field(default=None, min_length=7, max_length=20)
    password: str | None = Field(default=None, min_length=8, max_length=72)
    worker_code: str | None = Field(default=None, max_length=50)
    assigned_village: str | None = Field(default=None, max_length=255)
    role_title: str | None = Field(default=None, min_length=2, max_length=80)
    status: WorkerStatus | None = None
    photo_path: str | None = None


class AdminWorkerResponse(BaseModel):
    id: int
    user_id: int
    full_name: str
    email: str | None = None
    phone: str | None = None
    worker_code: str
    assigned_village: str | None = None
    role_title: str
    join_date: datetime
    photo_path: str | None = None
    status: WorkerStatus


class AdminPatientResponse(BaseModel):
    id: int
    user_id: int
    full_name: str
    email: str | None = None
    phone: str | None = None
    village: str | None = None
    gender: str | None = None
    age: int | None = None
    join_date: datetime
    photo_path: str | None = None


class AdminWorkersSummaryResponse(BaseModel):
    total_workers: int
    active_now: int
    on_field: int


class ConsultationTrendPoint(BaseModel):
    label: str
    value: int


class RegionalClinicPerformanceItem(BaseModel):
    clinic_name: str
    total_appointments: int
    completed_appointments: int
    performance_percent: float


class AdminAnalyticsResponse(BaseModel):
    range: AnalyticsRange
    consultation_growth_percent: float
    consultation_count: int
    patient_satisfaction_score: float
    patient_satisfaction_samples: int
    efficiency_percent: float
    active_chws: int
    active_chws_percent: float
    consultation_trends: list[ConsultationTrendPoint] = Field(default_factory=list)
    regional_clinic_performance: list[RegionalClinicPerformanceItem] = Field(default_factory=list)


class AdminOperationResponse(BaseModel):
    message: str
