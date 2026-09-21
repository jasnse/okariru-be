#!/usr/bin/env bash
# Dijalankan di SERVER (dikirim oleh deploy.sh). Server tidak pernah clone repo:
#   1) DB & Redis dibuat SEKALI saja, hanya jika belum ada
#   2) image app di-pull dari GHCR lalu container app dibuat ulang
#   3) cek aplikasi hidup; kalau gagal, rollback ke image sebelumnya
# Env  : DEPLOY_PATH, GHCR_USER
# Stdin: token GHCR (satu baris)
set -euo pipefail

: "${DEPLOY_PATH:?}" "${GHCR_USER:?}"
cd "$DEPLOY_PATH"
IFS= read -r GHCR_TOKEN
[ -n "$GHCR_TOKEN" ] || { echo "Token GHCR kosong" >&2; exit 1; }

log() { printf '[deploy] %s\n' "$*"; }
die() { printf '[deploy] ERROR: %s\n' "$*" >&2; exit 1; }

# Baca nilai dari .env (format KEY='value'); $2 = default
env_get() {
  local v
  v="$(grep -E "^$1=" .env | tail -n1 | cut -d= -f2- || true)"
  v="${v#\'}"; v="${v%\'}"
  printf '%s' "${v:-${2-}}"
}

trap 'docker logout ghcr.io >/dev/null 2>&1 || true' EXIT

command -v docker >/dev/null || die "Docker belum terpasang di server"
docker compose version >/dev/null 2>&1 || die "Docker Compose plugin belum terpasang"
docker info >/dev/null 2>&1 || die "User $(id -un) tidak bisa mengakses Docker (tambahkan ke grup docker)"

chmod 600 .env firebase-service-account.json
mkdir -p db/init "$(env_get POSTGRES_DATA_DIR ./data/postgres)" \
         "$(env_get REDIS_DATA_DIR ./data/redis)" "$(env_get UPLOADS_DIR ./uploads)"

APP_IMAGE="$(env_get APP_IMAGE)"
APP_PORT="$(env_get APP_PORT 8080)"
[ -n "$APP_IMAGE" ] || die "APP_IMAGE kosong di .env"

# Buat service infra hanya jika belum ada. Jika sudah ada: tidak disentuh (tidak di-recreate).
ensure_infra() {
  local svc="$1" cid
  cid="$(docker compose ps -a -q "$svc")"
  if [ -z "$cid" ]; then
    log "$svc belum ada -> dibuat (sekali saja)"
    docker compose up -d --no-build "$svc"
  elif [ "$(docker inspect -f '{{.State.Running}}' "$cid")" != "true" ]; then
    log "$svc ada tapi berhenti -> dinyalakan (tanpa dibuat ulang)"
    docker compose start "$svc"
  else
    log "$svc sudah berjalan -> dilewati"
  fi
}

wait_healthy() {
  local svc="$1" cid status
  cid="$(docker compose ps -q "$svc")"
  for _ in $(seq 1 60); do
    status="$(docker inspect -f '{{if .State.Health}}{{.State.Health.Status}}{{else}}none{{end}}' "$cid")"
    [ "$status" = "healthy" ] && return 0
    sleep 2
  done
  docker compose logs --tail=50 "$svc" || true
  die "$svc tidak healthy dalam 120 detik"
}

# Aplikasi dianggap hidup jika port menjawab HTTP apa pun (200/401/403/503 = server sudah nyala)
wait_app() {
  local cid code
  cid="$(docker compose ps -q app)"
  for _ in $(seq 1 45); do
    [ "$(docker inspect -f '{{.State.Running}}' "$cid" 2>/dev/null)" = "true" ] || return 1
    code="$(curl -s -o /dev/null -w '%{http_code}' "http://127.0.0.1:${APP_PORT}/actuator/health" || true)"
    if [ -n "$code" ] && [ "$code" != "000" ]; then log "app menjawab HTTP $code"; return 0; fi
    sleep 4
  done
  return 1
}

# ---- 1. infra (sekali saja) ----
ensure_infra postgres
ensure_infra redis
wait_healthy postgres
wait_healthy redis

# ---- 2. pull image & deploy app ----
PREV_IMAGE="$(docker compose ps -q app | xargs -r docker inspect -f '{{.Config.Image}}' 2>/dev/null || true)"
log "login ghcr.io & pull $APP_IMAGE (sebelumnya: ${PREV_IMAGE:-tidak ada})"
printf '%s' "$GHCR_TOKEN" | docker login ghcr.io -u "$GHCR_USER" --password-stdin >/dev/null
docker pull "$APP_IMAGE"
docker compose up -d --no-deps --no-build app

# ---- 3. verifikasi & rollback ----
if ! wait_app; then
  docker compose logs --tail=80 app || true
  if [ -n "$PREV_IMAGE" ] && [ "$PREV_IMAGE" != "$APP_IMAGE" ]; then
    log "deploy gagal -> rollback ke $PREV_IMAGE"
    APP_IMAGE="$PREV_IMAGE" docker compose up -d --no-deps --no-build app
  fi
  die "aplikasi tidak sehat setelah deploy"
fi

docker image prune -af --filter "until=168h" >/dev/null || true
log "selesai: $APP_IMAGE"
