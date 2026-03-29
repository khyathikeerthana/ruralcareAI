from datetime import datetime, timezone

from fastapi import APIRouter, HTTPException, status

from ..config import get_settings
from ..schemas.ai import SymptomAnalysisRequest, SymptomAnalysisResponse
from ..services.ai_analysis import AIAnalysisError, analyze_symptoms_with_gemini

router = APIRouter(tags=["AI"])
settings = get_settings()


@router.post("/ai/analyze-symptoms", response_model=SymptomAnalysisResponse)
def analyze_symptoms(payload: SymptomAnalysisRequest) -> SymptomAnalysisResponse:
    try:
        analysis = analyze_symptoms_with_gemini(
            symptoms=payload.symptoms,
            language=payload.language,
            settings=settings,
        )
    except AIAnalysisError as exc:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail=str(exc),
        ) from exc

    return SymptomAnalysisResponse(
        severity_label=analysis["severity_label"],
        severity_score=analysis["severity_score"],
        triage_note=analysis["triage_note"],
        possible_causes=analysis["possible_causes"],
        matched_symptoms=analysis["matched_symptoms"],
        immediate_actions=analysis["immediate_actions"],
        home_care=analysis["home_care"],
        red_flags=analysis["red_flags"],
        disclaimer=analysis["disclaimer"],
        provider=str(analysis.get("provider", f"gemini:{settings.gemini_model}")),
        generated_at=datetime.now(timezone.utc),
    )
