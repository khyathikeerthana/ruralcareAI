from __future__ import annotations

from typing import TYPE_CHECKING

from datetime import datetime

from sqlalchemy import DateTime, ForeignKey, String, Text
from sqlalchemy.orm import Mapped, mapped_column, relationship

from ..database import Base
from .base import TimestampMixin

if TYPE_CHECKING:
    from .appointment import Appointment
    from .prescription import Prescription


class Consultation(TimestampMixin, Base):
    __tablename__ = "consultations"

    id: Mapped[int] = mapped_column(primary_key=True)
    appointment_id: Mapped[int] = mapped_column(
        ForeignKey("appointments.id", ondelete="CASCADE"),
        nullable=False,
    )

    consultation_mode: Mapped[str] = mapped_column(String(20), default="video", nullable=False)
    started_at: Mapped[datetime | None] = mapped_column(DateTime(timezone=True), nullable=True)
    ended_at: Mapped[datetime | None] = mapped_column(DateTime(timezone=True), nullable=True)
    notes: Mapped[str | None] = mapped_column(Text, nullable=True)

    appointment: Mapped["Appointment"] = relationship(back_populates="consultations")
    prescriptions: Mapped[list["Prescription"]] = relationship(back_populates="consultation")
