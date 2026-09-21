#!/usr/bin/env bash
# Dijalankan di RUNNER CI. Merakit file yang akan dikirim ke server dari environment variable:
#   $STAGE_DIR/.env                          (dibaca docker compose di server)
#   $STAGE_DIR/firebase-service-account.json (di-mount ke container app)
# Nilai datang dari GitHub variables/secrets yang di-map di workflow.
set -euo pipefail

STAGE_DIR="${STAGE_DIR:-.deploy}"

REQUIRED=(APP_IMAGE DB_NAME DB_USERNAME DB_PASSWORD JWT_SECRET
          APP_SECURITY_CORS_ALLOWED_ORIGIN SPRING_MAIL_USERNAME SPRING_MAIL_PASSWORD)
OPTIONAL=(DB_SCHEMA REDIS_PORT JWT_TTL_MINUTES APP_PORT JAVA_OPTS APP_UID APP_GID
          POSTGRES_DATA_DIR UPLOADS_DIR REDIS_DATA_DIR)

die() { echo "::error::$*" >&2; exit 1; }

mkdir -p "$STAGE_DIR"
umask 077
ENV_FILE="$STAGE_DIR/.env"
: > "$ENV_FILE"

# Nilai ditulis dalam kutip tunggal: docker compose tidak akan meng-interpolasi $ atau memotong di #.
emit() {
  local key="$1" val="$2"
  case "$val" in *"'"*|*$'\n'*) die "$key mengandung tanda petik tunggal atau newline (tidak didukung)" ;; esac
  printf "%s='%s'\n" "$key" "$val" >> "$ENV_FILE"
}

emit COMPOSE_PROJECT_NAME "${COMPOSE_PROJECT_NAME:-okariru}"

for k in "${REQUIRED[@]}"; do
  [ -n "${!k-}" ] || die "$k kosong. Isi di .env.github lalu jalankan scripts/gh/setup-github.sh"
  emit "$k" "${!k}"
done

# Path host relatif (mis. "uploads/images") dianggap NAMED VOLUME oleh docker compose.
# Awali dengan "./" supaya menjadi bind mount.
HOST_PATHS=(POSTGRES_DATA_DIR UPLOADS_DIR REDIS_DATA_DIR)
is_host_path() { local p; for p in "${HOST_PATHS[@]}"; do [ "$p" = "$1" ] && return 0; done; return 1; }

for k in "${OPTIONAL[@]}"; do
  [ -n "${!k-}" ] || continue
  v="${!k}"
  if is_host_path "$k"; then
    case "$v" in /*|./*|../*|"~"*) ;; *) v="./$v" ;; esac
  fi
  emit "$k" "$v"
done

[ -n "${FIREBASE_CREDENTIALS_B64-}" ] || die "FIREBASE_CREDENTIALS_B64 kosong"
printf '%s' "$FIREBASE_CREDENTIALS_B64" | base64 -d > "$STAGE_DIR/firebase-service-account.json" \
  || die "FIREBASE_CREDENTIALS_B64 bukan base64 yang valid"

echo "Tersusun: $ENV_FILE ($(grep -c . "$ENV_FILE") baris) dan firebase-service-account.json"
