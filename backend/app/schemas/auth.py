from typing import Literal

from pydantic import BaseModel, Field, model_validator

RoleName = Literal["patient", "doctor", "community_health_worker", "admin"]
GenderName = Literal["Male", "Female", "Other"]


class RegisterRequest(BaseModel):
    full_name: str = Field(min_length=2, max_length=120)
    email: str = Field(min_length=5, max_length=255)
    phone: str = Field(min_length=7, max_length=20)
    password: str = Field(min_length=8, max_length=72)
    role: RoleName

    @model_validator(mode="after")
    def validate_contact_details(self) -> "RegisterRequest":
        if "@" not in self.email:
            raise ValueError("Enter a valid email address.")
        return self


class LoginRequest(BaseModel):
    identifier: str = Field(min_length=3, max_length=255)
    password: str = Field(min_length=4, max_length=72)
    expected_role: RoleName | None = None


class AuthResponse(BaseModel):
    user_id: int
    patient_id: int | None = None
    full_name: str
    email: str | None = None
    phone: str | None = None
    access_token: str
    token_type: str = "bearer"
    role: RoleName
    expires_in_minutes: int


class ProfileSetupRequest(BaseModel):
    user_id: int = Field(gt=0)
    full_name: str = Field(min_length=2, max_length=120)
    age: int = Field(ge=1, le=120)
    gender: GenderName
    village: str = Field(min_length=2, max_length=255)
    blood_type: str = Field(min_length=1, max_length=12)


class ProfileSetupResponse(BaseModel):
    user_id: int
    full_name: str
    email: str | None = None
    phone: str | None = None
    age: int | None = None
    gender: GenderName | None = None
    village: str | None = None
    blood_type: str | None = None
    weight_kg: float | None = None
    photo_path: str | None = None
    message: str | None = None


class ProfileUpdateRequest(BaseModel):
    full_name: str = Field(min_length=2, max_length=120)
    email: str = Field(min_length=5, max_length=255)
    phone: str = Field(min_length=7, max_length=20)
    village: str = Field(min_length=2, max_length=255)
    blood_type: str = Field(min_length=1, max_length=12)
    age: int | None = Field(default=None, ge=1, le=120)
    gender: GenderName | None = None
    weight_kg: float | None = Field(default=None, ge=0)
    photo_path: str | None = None

    @model_validator(mode="after")
    def validate_contact_details(self) -> "ProfileUpdateRequest":
        if "@" not in self.email:
            raise ValueError("Enter a valid email address.")
        return self
