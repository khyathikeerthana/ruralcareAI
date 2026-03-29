from functools import lru_cache
from pathlib import Path
from urllib.parse import parse_qsl, urlencode, urlsplit, urlunsplit

from pydantic import AliasChoices, Field, field_validator
from pydantic_settings import BaseSettings, SettingsConfigDict


BACKEND_ROOT = Path(__file__).resolve().parents[1]
ENV_FILE_PATH = BACKEND_ROOT / ".env"


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=str(ENV_FILE_PATH),
        env_file_encoding="utf-8",
        case_sensitive=False,
        extra="ignore"
    )

    app_name: str = "RuralCareAI API"
    app_env: str = "development"
    api_v1_str: str = "/api/v1"

    database_url: str = Field(
        default="postgresql+psycopg://ruralcare_user:CHANGE_ME@localhost:5432/ruralcareai"
    )

    jwt_secret_key: str = Field(
        default="CHANGE_ME_WITH_A_LONG_RANDOM_SECRET",
        validation_alias=AliasChoices("JWT_SECRET_KEY", "JWT_SECRET"),
    )
    access_token_expire_minutes: int = 120
    auto_create_tables: bool = False

    gemini_api_key: str = Field(
        default="",
        validation_alias=AliasChoices("GEMINI_API_KEY"),
    )
    gemini_model: str = Field(default="gemini-2.0-flash-lite")
    gemini_cache_ttl_seconds: int = 900
    gemini_timeout_seconds: int = 40
    gemini_max_output_tokens: int = 700

    cors_origins: list[str] = Field(default_factory=lambda: ["*"])

    @field_validator("database_url", mode="before")
    @classmethod
    def normalize_database_url(cls, value: str) -> str:
        normalized = value.strip().strip('"').strip("'")

        # Accept Prisma-style URL and normalize it for SQLAlchemy + psycopg.
        if normalized.startswith("postgresql://"):
            normalized = "postgresql+psycopg://" + normalized[len("postgresql://") :]

        parsed = urlsplit(normalized)
        if parsed.query:
            cleaned_query = [
                (key, query_value)
                for key, query_value in parse_qsl(parsed.query, keep_blank_values=True)
                if key.lower() != "schema"
            ]
            normalized = urlunsplit(
                (
                    parsed.scheme,
                    parsed.netloc,
                    parsed.path,
                    urlencode(cleaned_query, doseq=True),
                    parsed.fragment,
                )
            )

        return normalized

    @field_validator("cors_origins", mode="before")
    @classmethod
    def parse_cors_origins(cls, value: str | list[str]) -> list[str]:
        if isinstance(value, str):
            return [item.strip() for item in value.split(",") if item.strip()]
        return value


@lru_cache(maxsize=1)
def get_settings() -> Settings:
    return Settings()
