from datetime import datetime, timezone

from fastapi import APIRouter, Depends
from sqlalchemy import select
from sqlalchemy.orm import Session

from ..database import get_db
from ..models.medical_record import MedicalRecord
from ..schemas.medical_record import MedicalRecordCreate, MedicalRecordOut

router = APIRouter(tags=["MedicalRecords"])


@router.get("/records/{patient_id}", response_model=list[MedicalRecordOut])
def get_records(patient_id: int, db: Session = Depends(get_db)) -> list[MedicalRecordOut]:
    records = db.scalars(
        select(MedicalRecord)
        .where(MedicalRecord.patient_id == patient_id)
        .order_by(MedicalRecord.created_at.desc())
    ).all()

    return [
        MedicalRecordOut(
            id=record.id,
            patient_id=record.patient_id,
            record_type=record.record_type,
            title=record.title,
            description=record.description,
            file_url=record.file_url,
            created_at=record.created_at,
        )
        for record in records
    ]


@router.post("/records", response_model=MedicalRecordOut, status_code=201)
def create_record(payload: MedicalRecordCreate, db: Session = Depends(get_db)) -> MedicalRecordOut:
    record = MedicalRecord(
        patient_id=payload.patient_id,
        doctor_id=payload.doctor_id,
        record_type=payload.record_type,
        title=payload.title,
        description=payload.description,
        file_url=payload.file_url,
    )
    db.add(record)
    db.commit()
    db.refresh(record)

    return MedicalRecordOut(
        id=record.id,
        patient_id=record.patient_id,
        record_type=record.record_type,
        title=record.title,
        description=record.description,
        file_url=record.file_url,
        created_at=record.created_at,
    )
