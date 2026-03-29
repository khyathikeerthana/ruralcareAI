import logging

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy import inspect, text

from . import models  # noqa: F401
from .config import get_settings
from .database import Base, engine
from .routes import admin, ai, appointments, auth, chat, community_workers, consultations, doctors, preferences, prescriptions, records

settings = get_settings()
logger = logging.getLogger(__name__)


def _ensure_patient_profile_columns() -> None:
    try:
        with engine.begin() as connection:
            inspector = inspect(connection)
            if "patients" not in inspector.get_table_names():
                return

            existing_columns = {column["name"] for column in inspector.get_columns("patients")}
            statements: list[str] = []

            if "weight_kg" not in existing_columns:
                if connection.dialect.name == "postgresql":
                    statements.append("ALTER TABLE patients ADD COLUMN weight_kg DOUBLE PRECISION")
                else:
                    statements.append("ALTER TABLE patients ADD COLUMN weight_kg REAL")

            if "photo_path" not in existing_columns:
                statements.append("ALTER TABLE patients ADD COLUMN photo_path TEXT")

            for statement in statements:
                connection.execute(text(statement))
    except Exception as exc:  # pragma: no cover
        logger.warning("Skipping patient profile schema alignment due to DB error: %s", exc)


def _ensure_roles() -> None:
    roles = {
        "patient": "Rural patient accessing telemedicine services",
        "doctor": "Medical professional providing consultations",
        "community_health_worker": "Community healthcare worker supporting residents",
        "admin": "Platform administrator monitoring operations",
    }

    try:
        with engine.begin() as connection:
            dialect = connection.dialect.name
            for role_name, description in roles.items():
                if dialect == "postgresql":
                    connection.execute(
                        text(
                            """
                            INSERT INTO roles (name, description)
                            VALUES (:name, :description)
                            ON CONFLICT (name) DO NOTHING
                            """
                        ),
                        {"name": role_name, "description": description},
                    )
                else:
                    existing = connection.execute(
                        text("SELECT id FROM roles WHERE name = :name"),
                        {"name": role_name},
                    ).first()
                    if existing is None:
                        connection.execute(
                            text("INSERT INTO roles (name, description) VALUES (:name, :description)"),
                            {"name": role_name, "description": description},
                        )
    except Exception as exc:  # pragma: no cover
        logger.warning("Skipping role alignment due to DB error: %s", exc)


def _ensure_admin_management_schema() -> None:
    try:
        with engine.begin() as connection:
            inspector = inspect(connection)
            table_names = set(inspector.get_table_names())

            if "users" in table_names:
                user_columns = {column["name"] for column in inspector.get_columns("users")}
                if "profile_photo_path" not in user_columns:
                    connection.execute(text("ALTER TABLE users ADD COLUMN profile_photo_path TEXT"))

            if "doctors" in table_names:
                doctor_columns = {column["name"] for column in inspector.get_columns("doctors")}
                statements: list[str] = []

                if "qualification" not in doctor_columns:
                    statements.append("ALTER TABLE doctors ADD COLUMN qualification VARCHAR(120)")
                if "hospital_name" not in doctor_columns:
                    statements.append("ALTER TABLE doctors ADD COLUMN hospital_name VARCHAR(180)")
                if "assigned_location" not in doctor_columns:
                    statements.append("ALTER TABLE doctors ADD COLUMN assigned_location TEXT")
                if "languages" not in doctor_columns:
                    statements.append("ALTER TABLE doctors ADD COLUMN languages VARCHAR(255)")
                if "employment_status" not in doctor_columns:
                    statements.append("ALTER TABLE doctors ADD COLUMN employment_status VARCHAR(20) NOT NULL DEFAULT 'active'")

                for statement in statements:
                    connection.execute(text(statement))

            if "community_health_workers" not in table_names:
                if connection.dialect.name == "postgresql":
                    connection.execute(
                        text(
                            """
                            CREATE TABLE IF NOT EXISTS community_health_workers (
                                id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                                worker_code VARCHAR(50) NOT NULL UNIQUE,
                                assigned_village TEXT,
                                role_title VARCHAR(80) NOT NULL DEFAULT 'Community Health Worker',
                                status VARCHAR(20) NOT NULL DEFAULT 'active',
                                last_seen_at TIMESTAMPTZ,
                                created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
                            )
                            """
                        )
                    )
                    connection.execute(
                        text(
                            "CREATE INDEX IF NOT EXISTS idx_community_health_workers_status ON community_health_workers(status)"
                        )
                    )
                else:
                    connection.execute(
                        text(
                            """
                            CREATE TABLE IF NOT EXISTS community_health_workers (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                user_id INTEGER NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                                worker_code TEXT NOT NULL UNIQUE,
                                assigned_village TEXT,
                                role_title TEXT NOT NULL DEFAULT 'Community Health Worker',
                                status TEXT NOT NULL DEFAULT 'active',
                                last_seen_at TEXT,
                                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
                            )
                            """
                        )
                    )
    except Exception as exc:  # pragma: no cover
        logger.warning("Skipping admin management schema alignment due to DB error: %s", exc)


app = FastAPI(
    title=settings.app_name,
    version="0.1.0",
    description="Role-based telemedicine API for RuralCareAI"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.on_event("startup")
def startup_event() -> None:
    _ensure_patient_profile_columns()
    _ensure_roles()
    _ensure_admin_management_schema()
    if settings.app_env.lower() != "production" and settings.auto_create_tables:
        try:
            Base.metadata.create_all(bind=engine)
        except Exception as exc:  # pragma: no cover
            logger.warning("Skipping table auto-creation due to DB connection error: %s", exc)


app.include_router(auth.router, prefix=settings.api_v1_str)
app.include_router(doctors.router, prefix=settings.api_v1_str)
app.include_router(appointments.router, prefix=settings.api_v1_str)
app.include_router(chat.router, prefix=settings.api_v1_str)
app.include_router(consultations.router, prefix=settings.api_v1_str)
app.include_router(prescriptions.router, prefix=settings.api_v1_str)
app.include_router(records.router, prefix=settings.api_v1_str)
app.include_router(preferences.router, prefix=settings.api_v1_str)
app.include_router(ai.router, prefix=settings.api_v1_str)
app.include_router(admin.router, prefix=settings.api_v1_str)
app.include_router(community_workers.router, prefix=settings.api_v1_str)


@app.get("/health")
def health_check() -> dict[str, str]:
    return {"status": "ok", "service": "ruralcareai-backend"}
