from __future__ import annotations

import json
import time
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen

from ..config import Settings


class AIAnalysisError(RuntimeError):
    pass


_ANALYSIS_CACHE: dict[str, tuple[float, dict[str, object]]] = {}
_MAX_CACHE_ENTRIES = 256


def analyze_symptoms_with_gemini(
    *,
    symptoms: str,
    language: str | None,
    settings: Settings,
) -> dict[str, object]:
    api_key = settings.gemini_api_key.strip()
    if not api_key:
        raise AIAnalysisError("Gemini API is not configured. Set GEMINI_API_KEY in backend/.env.")

    model = settings.gemini_model.strip() or "gemini-2.0-flash-lite"
    # Accept both "gemini-2.0-flash-lite" and "models/gemini-2.0-flash-lite".
    if model.startswith("models/"):
        model = model.split("/", 1)[1]
    normalized_symptoms = " ".join(symptoms.strip().split())
    language_hint = language.strip().lower() if language and language.strip() else "english"
    cache_key = f"{language_hint}|{normalized_symptoms.lower()}"

    now_ts = time.time()
    cache_ttl = max(0, settings.gemini_cache_ttl_seconds)
    if cache_ttl > 0:
        cached = _ANALYSIS_CACHE.get(cache_key)
        if cached is not None:
            ts, payload = cached
            if now_ts - ts <= cache_ttl:
                return payload

    prompt = _build_prompt(symptoms=normalized_symptoms, language=language)

    payload = {
        "contents": [{"parts": [{"text": prompt}]}],
        "generationConfig": {
            "temperature": 0.2,
            "topP": 0.8,
            "maxOutputTokens": max(128, settings.gemini_max_output_tokens),
        },
    }

    url = (
        f"https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent"
        f"?key={api_key}"
    )
    request = Request(
        url=url,
        data=json.dumps(payload).encode("utf-8"),
        headers={"Content-Type": "application/json"},
        method="POST",
    )

    try:
        with urlopen(request, timeout=max(10, settings.gemini_timeout_seconds)) as response:
            response_payload = json.loads(response.read().decode("utf-8"))
    except HTTPError as exc:  # pragma: no cover
        body = exc.read().decode("utf-8", errors="ignore")
        if exc.code == 429:
            fallback = _build_fallback_analysis(normalized_symptoms)
            fallback["provider"] = "local-triage-fallback:quota"
            if cache_ttl > 0:
                _ANALYSIS_CACHE[cache_key] = (now_ts, fallback)
            return fallback
        raise AIAnalysisError(f"Gemini request failed (HTTP {exc.code}). {body[:240]}") from exc
    except URLError as exc:  # pragma: no cover
        raise AIAnalysisError(f"Gemini request failed: {exc.reason}") from exc

    generated_text = _extract_generated_text(response_payload)
    parsed_json = _extract_json_payload(generated_text)
    normalized = _normalize_analysis(
        parsed_json,
        normalized_symptoms,
        provider=f"gemini:{model}",
    )

    if cache_ttl > 0:
        _ANALYSIS_CACHE[cache_key] = (now_ts, normalized)
        if len(_ANALYSIS_CACHE) > _MAX_CACHE_ENTRIES:
            oldest_key = min(_ANALYSIS_CACHE.items(), key=lambda item: item[1][0])[0]
            _ANALYSIS_CACHE.pop(oldest_key, None)

    return normalized


def _build_prompt(*, symptoms: str, language: str | None) -> str:
    language_hint = language.strip() if language and language.strip() else "English"
    return (
        "You are a telemedicine triage assistant for rural healthcare. "
        "Analyze the patient symptoms and respond with ONLY valid JSON (no markdown, no backticks).\n\n"
        "Patient symptom report:\n"
        f"{symptoms}\n\n"
        f"Preferred language for wording: {language_hint}.\n\n"
        "Return exactly this JSON schema:\n"
        "{\n"
        '  "severity_label": "Low Severity | Moderate Severity | High Severity",\n'
        '  "severity_score": 0-100 integer,\n'
        '  "triage_note": "one concise sentence for action urgency",\n'
        '  "possible_causes": ["2-4 likely causes in plain language"],\n'
        '  "matched_symptoms": ["up to 6 symptom phrases extracted from patient text"],\n'
        '  "immediate_actions": ["2-4 immediate actions"],\n'
        '  "home_care": ["2-4 home monitoring/care actions"],\n'
        '  "red_flags": ["2-4 emergency warning signs"],\n'
        '  "disclaimer": "medical safety disclaimer"\n'
        "}\n\n"
        "Safety rule: do not prescribe prescription medicines or exact dosages."
    )


def _extract_generated_text(payload: dict[str, object]) -> str:
    try:
        candidates = payload.get("candidates")
        if not isinstance(candidates, list) or not candidates:
            raise ValueError("Missing candidates")

        content = candidates[0].get("content")
        if not isinstance(content, dict):
            raise ValueError("Missing content")

        parts = content.get("parts")
        if not isinstance(parts, list) or not parts:
            raise ValueError("Missing parts")

        text = parts[0].get("text")
        if not isinstance(text, str) or not text.strip():
            raise ValueError("Missing text")

        return text
    except Exception as exc:  # pragma: no cover
        raise AIAnalysisError("Gemini returned an unexpected response format.") from exc


def _extract_json_payload(text: str) -> dict[str, object]:
    normalized = text.strip()

    if normalized.startswith("```"):
        normalized = normalized.strip("`")
        if normalized.lower().startswith("json"):
            normalized = normalized[4:].strip()

    start = normalized.find("{")
    end = normalized.rfind("}")
    if start == -1 or end == -1 or end <= start:
        raise AIAnalysisError("AI response did not contain a valid JSON object.")

    json_text = normalized[start : end + 1]
    try:
        parsed = json.loads(json_text)
    except json.JSONDecodeError as exc:
        raise AIAnalysisError("AI response JSON could not be parsed.") from exc

    if not isinstance(parsed, dict):
        raise AIAnalysisError("AI response JSON must be an object.")
    return parsed


def _normalize_analysis(
    parsed: dict[str, object],
    symptoms: str,
    *,
    provider: str,
) -> dict[str, object]:
    raw_label = str(parsed.get("severity_label", "Moderate Severity"))
    label_lower = raw_label.lower()
    if "low" in label_lower:
        severity_label = "Low Severity"
    elif "high" in label_lower or "severe" in label_lower:
        severity_label = "High Severity"
    else:
        severity_label = "Moderate Severity"

    raw_score = parsed.get("severity_score", 55)
    try:
        severity_score = int(raw_score)
    except (TypeError, ValueError):
        severity_score = 55
    severity_score = max(0, min(100, severity_score))

    triage_note = _safe_text(parsed.get("triage_note"), "Consult a doctor if symptoms worsen or persist.")

    possible_causes = _safe_list(parsed.get("possible_causes"), ["Further clinical evaluation is needed for a precise diagnosis."])
    matched_symptoms = _safe_list(parsed.get("matched_symptoms"), _extract_symptom_fallback(symptoms))
    immediate_actions = _safe_list(parsed.get("immediate_actions"), ["Stay hydrated", "Take rest", "Monitor symptoms over 24 hours"])
    home_care = _safe_list(parsed.get("home_care"), ["Track temperature", "Avoid irritants", "Use supportive care"])
    red_flags = _safe_list(parsed.get("red_flags"), ["Severe breathing difficulty", "Persistent chest pain", "Confusion or fainting"])
    disclaimer = _safe_text(
        parsed.get("disclaimer"),
        "This AI guidance is informational and not a final diagnosis. Consult a licensed doctor for medical decisions.",
    )

    return {
        "severity_label": severity_label,
        "severity_score": severity_score,
        "triage_note": triage_note,
        "possible_causes": possible_causes,
        "matched_symptoms": matched_symptoms,
        "immediate_actions": immediate_actions,
        "home_care": home_care,
        "red_flags": red_flags,
        "disclaimer": disclaimer,
        "provider": provider,
    }


def _build_fallback_analysis(symptoms: str) -> dict[str, object]:
    text = symptoms.lower()
    matched = _extract_symptom_fallback(symptoms)

    high_risk_markers = [
        "chest pain",
        "shortness of breath",
        "breathing difficulty",
        "faint",
        "unconscious",
        "seizure",
        "vomit blood",
        "blood in stool",
        "severe headache",
        "one-sided weakness",
    ]
    moderate_markers = [
        "fever",
        "cough",
        "sore throat",
        "body pain",
        "vomiting",
        "diarrhea",
        "headache",
        "dizziness",
        "fatigue",
        "rash",
    ]

    high_hits = sum(1 for marker in high_risk_markers if marker in text)
    moderate_hits = sum(1 for marker in moderate_markers if marker in text)

    severity_score = 25 + (high_hits * 28) + (moderate_hits * 10)
    severity_score = max(10, min(95, severity_score))

    if high_hits > 0 or severity_score >= 75:
        severity_label = "High Severity"
        triage_note = "Seek urgent in-person medical care as soon as possible."
    elif severity_score >= 45:
        severity_label = "Moderate Severity"
        triage_note = "Arrange a doctor consultation within 24 hours and monitor symptoms closely."
    else:
        severity_label = "Low Severity"
        triage_note = "Home care may be appropriate; consult a doctor if symptoms worsen or persist."

    possible_causes = [
        "Viral or bacterial infection",
        "Inflammatory response",
        "Dehydration or general weakness",
    ]
    immediate_actions = [
        "Rest and avoid strenuous activity",
        "Hydrate regularly with clean fluids",
        "Monitor temperature and symptom progression",
    ]
    home_care = [
        "Maintain adequate fluid intake",
        "Use supportive home care and light nutrition",
        "Track red-flag symptoms every few hours",
    ]
    red_flags = [
        "Breathing difficulty or chest pain",
        "Persistent vomiting, confusion, or fainting",
        "High fever not improving or severe weakness",
    ]

    return {
        "severity_label": severity_label,
        "severity_score": severity_score,
        "triage_note": triage_note,
        "possible_causes": possible_causes,
        "matched_symptoms": matched,
        "immediate_actions": immediate_actions,
        "home_care": home_care,
        "red_flags": red_flags,
        "disclaimer": (
            "This guidance is informational only and not a final diagnosis. "
            "Live AI analysis is temporarily rate-limited; consult a licensed doctor for medical decisions."
        ),
        "provider": "local-triage-fallback",
    }


def _safe_text(value: object, default: str) -> str:
    if isinstance(value, str) and value.strip():
        return value.strip()
    return default


def _safe_list(value: object, default: list[str]) -> list[str]:
    if isinstance(value, list):
        cleaned = [str(item).strip() for item in value if str(item).strip()]
        if cleaned:
            return cleaned[:6]
    return default


def _extract_symptom_fallback(symptoms_text: str) -> list[str]:
    separators = [",", " and ", ";", "\n"]
    normalized = symptoms_text
    for separator in separators:
        normalized = normalized.replace(separator, "|")

    tokens = [item.strip() for item in normalized.split("|") if item.strip()]
    if not tokens:
        return ["Reported symptoms"]
    return tokens[:6]
