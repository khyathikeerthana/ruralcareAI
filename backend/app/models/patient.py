from __future__ import annotations

from typing import TYPE_CHECKING

from datetime import date

from sqlalchemy import Date, ForeignKey, String, Text
from sqlalchemy.orm import Mapped, mapped_column, relationship

from ..database import Base
from .base import TimestampMixin

if TYPE_CHECKING:
    from .appointment import Appointment
    from .medical_record import MedicalRecord
    from .prescription import Prescription
    from .user import User


class Patient(TimestampMixin, Base):
    __tablename__ = "patients"

    id: Mapped[int] = mapped_column(primary_key=True)
    user_id: Mapped[int] = mapped_column(
        ForeignKey("users.id", ondelete="CASCADE"),
        unique=True,
        nullable=False,
    )

    date_of_birth: Mapped[date | None] = mapped_column(Date, nullable=True)
    gender: Mapped[str | None] = mapped_column(String(20), nullable=True)
    address: Mapped[str | None] = mapped_column(Text, nullable=True)
    blood_type: Mapped[str | None] = mapped_column(String(12), nullable=True)
    emergency_contact: Mapped[str | None] = mapped_column(String(30), nullable=True)
    weight_kg: Mapped[float | None] = mapped_column(nullable=True)
    photo_path: Mapped[str | None] = mapped_column(Text, nullable=True)

    user: Mapped["User"] = relationship(back_populates="patient_profile")
    appointments: Mapped[list["Appointment"]] = relationship(back_populates="patient")
    prescriptions: Mapped[list["Prescription"]] = relationship(back_populates="patient")
    records: Mapped[list["MedicalRecord"]] = relationship(back_populates="patient")
