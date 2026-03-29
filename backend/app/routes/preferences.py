from fastapi import APIRouter

from ..schemas.preferences import (
    LanguageOption,
    LanguagePreferenceResponse,
    LanguagePreferenceUpdate,
)

router = APIRouter(prefix="/preferences", tags=["Preferences"])

SUPPORTED_LANGUAGES: list[LanguageOption] = [
    LanguageOption(code="pa", display_name="Punjabi", native_label="ਪੰਜਾਬੀ", is_default=True),
    LanguageOption(code="hi", display_name="Hindi", native_label="हिन्दी", is_default=False),
    LanguageOption(code="en", display_name="English", native_label="English", is_default=False),
]


@router.get("/languages", response_model=list[LanguageOption])
def list_supported_languages() -> list[LanguageOption]:
    return SUPPORTED_LANGUAGES


@router.put("/language", response_model=LanguagePreferenceResponse)
def update_language_preference(payload: LanguagePreferenceUpdate) -> LanguagePreferenceResponse:
    # Placeholder for persistence in users/preferences table after auth integration.
    return LanguagePreferenceResponse(
        language_code=payload.language_code,
        message="Language preference received.",
    )
