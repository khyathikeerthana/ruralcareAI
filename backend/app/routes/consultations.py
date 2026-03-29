from datetime import datetime, timezone

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from ..database import get_db
from ..models.consultation import Consultation
from ..schemas.consultation import ConsultationEndRequest, ConsultationResponse, ConsultationStartRequest

router = APIRouter(tags=["Consultations"])


@router.post("/consultation/start", response_model=ConsultationResponse, status_code=201)
def start_consultation(payload: ConsultationStartRequest, db: Session = Depends(get_db)) -> ConsultationResponse:
    now = datetime.now(timezone.utc)
    record = Consultation(
        appointment_id=payload.appointment_id,
        consultation_mode=payload.mode,
        started_at=now,
    )
    db.add(record)
    db.commit()
    db.refresh(record)

    return ConsultationResponse(
        consultation_id=record.id,
        appointment_id=record.appointment_id,
        mode=record.consultation_mode,
        status="in_progress",
        updated_at=record.updated_at,
    )


@router.post("/consultation/end", response_model=ConsultationResponse)
def end_consultation(payload: ConsultationEndRequest, db: Session = Depends(get_db)) -> ConsultationResponse:
    existing = db.get(Consultation, payload.consultation_id)
    if existing is None:
        raise HTTPException(status_code=404, detail="Consultation not found")

    existing.ended_at = datetime.now(timezone.utc)
    if payload.notes is not None:
        existing.notes = payload.notes
    db.commit()
    db.refresh(existing)

    return ConsultationResponse(
        consultation_id=existing.id,
        appointment_id=existing.appointment_id,
        mode=existing.consultation_mode,
        status="completed",
        updated_at=existing.updated_at,
    )
