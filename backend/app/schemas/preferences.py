from typing import Literal

from pydantic import BaseModel

LanguageCode = Literal["pa", "hi", "en"]


class LanguageOption(BaseModel):
    code: LanguageCode
    display_name: str
    native_label: str
    is_default: bool = False


class LanguagePreferenceUpdate(BaseModel):
    language_code: LanguageCode


class LanguagePreferenceResponse(BaseModel):
    language_code: LanguageCode
    message: str
