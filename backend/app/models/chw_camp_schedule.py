from __future__ import annotations

from datetime import datetime

from sqlalchemy import DateTime, ForeignKey, String, Text
from sqlalchemy.orm import Mapped, mapped_column, relationship

from ..database import Base
from .base import TimestampMixin


class ChwCampSchedule(TimestampMixin, Base):
    __tablename__ = "chw_camp_schedules"

    id: Mapped[int] = mapped_column(primary_key=True)
    worker_id: Mapped[int] = mapped_column(ForeignKey("community_health_workers.id", ondelete="CASCADE"), nullable=False)
    village: Mapped[str] = mapped_column(String(255), nullable=False)
    primary_focus: Mapped[str] = mapped_column(String(128), nullable=False)
    sub_focus: Mapped[str | None] = mapped_column(Text, nullable=True)
    scheduled_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=False)
    slot: Mapped[str] = mapped_column(String(32), nullable=False)
    status: Mapped[str] = mapped_column(String(20), default="scheduled", nullable=False)

    worker = relationship("CommunityHealthWorker", backref="camp_schedules")
