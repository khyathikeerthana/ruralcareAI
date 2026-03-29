from pydantic import BaseModel


class DoctorSummary(BaseModel):
    id: int
    full_name: str
    specialization: str
    years_experience: int | None = None
    is_verified: bool = False


class DoctorDetail(DoctorSummary):
    bio: str | None = None
    license_number: str | None = None
