from __future__ import annotations

from typing import TYPE_CHECKING

from sqlalchemy import ForeignKey, String, Text
from sqlalchemy.orm import Mapped, mapped_column, relationship

from ..database import Base
from .base import TimestampMixin

if TYPE_CHECKING:
    from .doctor import Doctor
    from .patient import Patient


class MedicalRecord(TimestampMixin, Base):
    __tablename__ = "medical_records"

    id: Mapped[int] = mapped_column(primary_key=True)
    patient_id: Mapped[int] = mapped_column(ForeignKey("patients.id", ondelete="CASCADE"), nullable=False)
    doctor_id: Mapped[int | None] = mapped_column(
        ForeignKey("doctors.id", ondelete="SET NULL"),
        nullable=True,
    )

    record_type: Mapped[str] = mapped_column(String(80), nullable=False)
    title: Mapped[str] = mapped_column(String(180), nullable=False)
    description: Mapped[str | None] = mapped_column(Text, nullable=True)
    file_url: Mapped[str | None] = mapped_column(String(300), nullable=True)

    patient: Mapped["Patient"] = relationship(back_populates="records")
    doctor: Mapped["Doctor"] = relationship(back_populates="records")
