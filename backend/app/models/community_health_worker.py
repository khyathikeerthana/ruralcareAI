from __future__ import annotations

from typing import TYPE_CHECKING

from datetime import datetime

from sqlalchemy import DateTime, ForeignKey, String, Text
from sqlalchemy.orm import Mapped, mapped_column, relationship

from ..database import Base
from .base import TimestampMixin

if TYPE_CHECKING:
    from .user import User


class CommunityHealthWorker(TimestampMixin, Base):
    __tablename__ = "community_health_workers"

    id: Mapped[int] = mapped_column(primary_key=True)
    user_id: Mapped[int] = mapped_column(
        ForeignKey("users.id", ondelete="CASCADE"),
        unique=True,
        nullable=False,
    )

    worker_code: Mapped[str] = mapped_column(String(50), unique=True, nullable=False)
    assigned_village: Mapped[str | None] = mapped_column(Text, nullable=True)
    role_title: Mapped[str] = mapped_column(String(80), default="Community Health Worker", nullable=False)
    status: Mapped[str] = mapped_column(String(20), default="active", nullable=False)
    last_seen_at: Mapped[datetime | None] = mapped_column(DateTime(timezone=True), nullable=True)

    user: Mapped["User"] = relationship(back_populates="worker_profile")