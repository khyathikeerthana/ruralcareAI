from datetime import datetime, timezone

from fastapi import APIRouter, Depends
from sqlalchemy import select
from sqlalchemy.orm import Session

from ..database import get_db
from ..models.prescription import Prescription
from ..schemas.prescription import PrescriptionCreate, PrescriptionOut

router = APIRouter(tags=["Prescriptions"])


@router.post("/prescriptions", response_model=PrescriptionOut, status_code=201)
def create_prescription(
    payload: PrescriptionCreate,
    db: Session = Depends(get_db),
) -> PrescriptionOut:
    prescription = Prescription(
        consultation_id=payload.consultation_id,
        patient_id=payload.patient_id,
        doctor_id=payload.doctor_id,
        medication_name=payload.medication_name,
        dosage=payload.dosage,
        instructions=payload.instructions,
        issued_at=datetime.now(timezone.utc),
    )
    db.add(prescription)
    db.commit()
    db.refresh(prescription)
    
    return PrescriptionOut(
        id=prescription.id,
        consultation_id=prescription.consultation_id,
        patient_id=prescription.patient_id,
        doctor_id=prescription.doctor_id,
        medication_name=prescription.medication_name,
        dosage=prescription.dosage,
        instructions=prescription.instructions,
        issued_at=prescription.issued_at,
    )


@router.get("/prescriptions/{patient_id}", response_model=list[PrescriptionOut])
def get_prescriptions(
    patient_id: int,
    db: Session = Depends(get_db),
) -> list[PrescriptionOut]:
    prescriptions = db.scalars(
        select(Prescription)
        .where(Prescription.patient_id == patient_id)
        .order_by(Prescription.issued_at.desc())
    ).all()
    
    return [
        PrescriptionOut(
            id=rx.id,
            consultation_id=rx.consultation_id,
            patient_id=rx.patient_id,
            doctor_id=rx.doctor_id,
            medication_name=rx.medication_name,
            dosage=rx.dosage,
            instructions=rx.instructions,
            issued_at=rx.issued_at,
        )
        for rx in prescriptions
    ]
