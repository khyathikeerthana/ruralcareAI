from datetime import datetime, date

from pydantic import BaseModel


class ChwCampScheduleCreate(BaseModel):
    village: str
    primary_focus: str
    sub_focus: list[str] | None = None
    scheduled_date: date
    slot: str


class ChwCampScheduleOut(BaseModel):
    id: int
    worker_id: int
    village: str
    primary_focus: str
    sub_focus: list[str] | None = None
    scheduled_at: datetime
    slot: str
    status: str

    class Config:
        orm_mode = True
