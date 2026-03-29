from __future__ import annotations

from typing import TYPE_CHECKING

from sqlalchemy import Boolean, ForeignKey, String, Text
from sqlalchemy.orm import Mapped, mapped_column, relationship

from ..database import Base
from .base import TimestampMixin

if TYPE_CHECKING:
    from .appointment import Appointment
    from .medical_record import MedicalRecord
    from .prescription import Prescription
    from .user import User


class Doctor(TimestampMixin, Base):
    __tablename__ = "doctors"

    id: Mapped[int] = mapped_column(primary_key=True)
    user_id: Mapped[int] = mapped_column(
        ForeignKey("users.id", ondelete="CASCADE"),
        unique=True,
        nullable=False,
    )

    specialization: Mapped[str] = mapped_column(String(80), nullable=False)
    license_number: Mapped[str] = mapped_column(String(50), unique=True, nullable=False)
    years_experience: Mapped[int | None] = mapped_column(nullable=True)
    qualification: Mapped[str | None] = mapped_column(String(120), nullable=True)
    hospital_name: Mapped[str | None] = mapped_column(String(180), nullable=True)
    assigned_location: Mapped[str | None] = mapped_column(Text, nullable=True)
    languages: Mapped[str | None] = mapped_column(String(255), nullable=True)
    employment_status: Mapped[str] = mapped_column(String(20), default="active", nullable=False)
    bio: Mapped[str | None] = mapped_column(Text, nullable=True)
    is_verified: Mapped[bool] = mapped_column(Boolean, default=False, nullable=False)

    user: Mapped["User"] = relationship(back_populates="doctor_profile")
    appointments: Mapped[list["Appointment"]] = relationship(back_populates="doctor")
    prescriptions: Mapped[list["Prescription"]] = relationship(back_populates="doctor")
    records: Mapped[list["MedicalRecord"]] = relationship(back_populates="doctor")
