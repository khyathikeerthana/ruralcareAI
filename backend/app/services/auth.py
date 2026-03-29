import base64
import hashlib
import hmac
import os

PBKDF2_ITERATIONS = 120_000
SALT_BYTES = 16


def hash_password(password: str) -> str:
    password_bytes = password.encode("utf-8")
    salt = os.urandom(SALT_BYTES)
    digest = hashlib.pbkdf2_hmac("sha256", password_bytes, salt, PBKDF2_ITERATIONS)
    encoded_salt = base64.b64encode(salt).decode("ascii")
    encoded_digest = base64.b64encode(digest).decode("ascii")
    return f"pbkdf2_sha256${PBKDF2_ITERATIONS}${encoded_salt}${encoded_digest}"


def verify_password(plain_password: str, password_hash: str) -> bool:
    try:
        algorithm, iteration_value, encoded_salt, encoded_digest = password_hash.split("$", 3)
        if algorithm != "pbkdf2_sha256":
            return False

        iterations = int(iteration_value)
        salt = base64.b64decode(encoded_salt.encode("ascii"))
        stored_digest = base64.b64decode(encoded_digest.encode("ascii"))
        calculated_digest = hashlib.pbkdf2_hmac(
            "sha256",
            plain_password.encode("utf-8"),
            salt,
            iterations,
        )
        return hmac.compare_digest(calculated_digest, stored_digest)
    except (ValueError, TypeError):
        return False
