from __future__ import annotations

from typing import TYPE_CHECKING

from datetime import datetime

from sqlalchemy import DateTime, ForeignKey, String, Text
from sqlalchemy.orm import Mapped, mapped_column, relationship

from ..database import Base
from .base import TimestampMixin

if TYPE_CHECKING:
    from .consultation import Consultation
    from .doctor import Doctor
    from .patient import Patient


class Prescription(TimestampMixin, Base):
    __tablename__ = "prescriptions"

    id: Mapped[int] = mapped_column(primary_key=True)
    consultation_id: Mapped[int] = mapped_column(
        ForeignKey("consultations.id", ondelete="CASCADE"),
        nullable=False,
    )
    patient_id: Mapped[int] = mapped_column(ForeignKey("patients.id", ondelete="CASCADE"), nullable=False)
    doctor_id: Mapped[int] = mapped_column(ForeignKey("doctors.id", ondelete="CASCADE"), nullable=False)

    medication_name: Mapped[str] = mapped_column(String(120), nullable=False)
    dosage: Mapped[str] = mapped_column(String(120), nullable=False)
    instructions: Mapped[str | None] = mapped_column(Text, nullable=True)
    issued_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=False)

    consultation: Mapped["Consultation"] = relationship(back_populates="prescriptions")
    patient: Mapped["Patient"] = relationship(back_populates="prescriptions")
    doctor: Mapped["Doctor"] = relationship(back_populates="prescriptions")
