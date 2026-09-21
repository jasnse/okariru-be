#!/usr/bin/env bash
# Dijalankan di RUNNER CI setelah render-env.sh. Tidak ada git clone di server:
# hanya mengirim compose + script + .env (tar lewat SSH), lalu menjalankan remote-deploy.sh.
set -euo pipefail

: "${SSH_PRIVATE_KEY:?}" "${SSH_KNOWN_HOSTS:?}" "${SERVER_HOST:?}" "${SERVER_USER:?}"
: "${DEPLOY_PATH:?}" "${GHCR_USER:?}" "${GHCR_TOKEN:?}"
SERVER_PORT="${SERVER_PORT:-22}"
STAGE_DIR="${STAGE_DIR:-.deploy}"

[[ "$DEPLOY_PATH" == /* ]] || { echo "::error::DEPLOY_PATH harus path absolut" >&2; exit 1; }
[ -f "$STAGE_DIR/.env" ] || { echo "::error::$STAGE_DIR/.env belum dirender (jalankan render-env.sh)" >&2; exit 1; }

umask 077
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
printf '%s\n' "$SSH_PRIVATE_KEY" > "$TMP/id"
printf '%s\n' "$SSH_KNOWN_HOSTS" > "$TMP/known_hosts"

SSH=(ssh -i "$TMP/id" -p "$SERVER_PORT" -o IdentitiesOnly=yes -o BatchMode=yes
     -o StrictHostKeyChecking=yes -o UserKnownHostsFile="$TMP/known_hosts"
     "$SERVER_USER@$SERVER_HOST")

# Susun isi yang dikirim di STAGE_DIR (sudah berisi .env & firebase json)
mkdir -p "$STAGE_DIR/scripts"
cp docker-compose.yml "$STAGE_DIR/docker-compose.yml"
cp scripts/deploy/remote-deploy.sh "$STAGE_DIR/scripts/remote-deploy.sh"

echo "==> Mengirim file ke $SERVER_USER@$SERVER_HOST:$DEPLOY_PATH"
tar -C "$STAGE_DIR" -czf - . | "${SSH[@]}" "mkdir -p '$DEPLOY_PATH' && tar -xzf - -C '$DEPLOY_PATH'"

echo "==> Menjalankan remote-deploy.sh"
# Token GHCR lewat stdin (bukan argumen) supaya tidak terlihat di daftar proses server
printf '%s\n' "$GHCR_TOKEN" | "${SSH[@]}" \
  "DEPLOY_PATH='$DEPLOY_PATH' GHCR_USER='$GHCR_USER' bash '$DEPLOY_PATH/scripts/remote-deploy.sh'"
