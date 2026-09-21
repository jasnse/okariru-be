#!/usr/bin/env bash
# Kirim variables & secrets dari .env.github ke GitHub (repository-level) memakai gh CLI.
#
# Pemakaian:  bash scripts/gh/setup-github.sh [-f FILE] [-R owner/repo] [--dry-run] [--no-known-hosts]
#   -f FILE            file sumber (default: .env.github)
#   -R owner/repo      repo tujuan (default: repo dari git remote)
#   --dry-run          tampilkan apa yang akan dikirim, tanpa mengirim
#   --no-known-hosts   jangan buat secret SSH_KNOWN_HOSTS otomatis (ssh-keyscan)
set -euo pipefail

FILE=".env.github"
REPO=""
DRY_RUN=0
KNOWN_HOSTS=1

while [ $# -gt 0 ]; do
  case "$1" in
    -f) FILE="$2"; shift 2 ;;
    -R) REPO="$2"; shift 2 ;;
    --dry-run) DRY_RUN=1; shift ;;
    --no-known-hosts) KNOWN_HOSTS=0; shift ;;
    -h|--help) sed -n '2,8p' "$0"; exit 0 ;;
    *) echo "Argumen tidak dikenal: $1" >&2; exit 2 ;;
  esac
done

die() { echo "ERROR: $*" >&2; exit 1; }
log() { printf '%s\n' "$*"; }

command -v gh >/dev/null || die "gh CLI belum terpasang (https://cli.github.com)"
[ -f "$FILE" ] || die "File $FILE tidak ditemukan. Jalankan: cp .env.github.example $FILE"
if [ "$DRY_RUN" -eq 0 ]; then
  gh auth status >/dev/null 2>&1 || die "Belum login. Jalankan: gh auth login"
fi
if [ -z "$REPO" ]; then
  REPO="$(gh repo view --json nameWithOwner -q .nameWithOwner 2>/dev/null)" \
    || die "Tidak bisa mendeteksi repo. Pakai -R owner/repo"
fi
log "Repo   : $REPO"
log "Sumber : $FILE"
[ "$DRY_RUN" -eq 1 ] && log "Mode   : DRY RUN (tidak ada yang dikirim)"

# Kunci yang wajib terisi (variable opsional boleh kosong)
REQUIRED=(SERVER_HOST SERVER_USER DEPLOY_PATH DB_NAME DB_USERNAME
          APP_SECURITY_CORS_ALLOWED_ORIGIN SPRING_MAIL_USERNAME
          DB_PASSWORD JWT_SECRET SPRING_MAIL_PASSWORD SSH_PRIVATE_KEY FIREBASE_CREDENTIALS_B64)

declare -A VALUES SECTION
ORDER=()
current=""

while IFS= read -r line || [ -n "$line" ]; do
  line="${line%$'\r'}"
  case "$line" in
    ''|'#'*) continue ;;
    '['*']') current="${line#[}"; current="${current%]}"; continue ;;
  esac
  [[ "$line" == *=* ]] || continue
  key="${line%%=*}"
  val="${line#*=}"
  # buang kutip pembungkus jika ada
  if [[ "$val" =~ ^\"(.*)\"$ ]] || [[ "$val" =~ ^\'(.*)\'$ ]]; then val="${BASH_REMATCH[1]}"; fi
  [ -n "$current" ] || die "Key $key berada di luar section [..]"
  VALUES[$key]="$val"
  SECTION[$key]="$current"
  ORDER+=("$key")
done < "$FILE"

# ---- validasi ----
errors=0
for k in "${REQUIRED[@]}"; do
  v="${VALUES[$k]:-}"
  if [ -z "$v" ] || [[ "$v" == *CHANGE_ME* ]]; then
    echo "  x $k belum diisi (kosong / masih CHANGE_ME)" >&2
    errors=1
  fi
done
for k in "${ORDER[@]}"; do
  v="${VALUES[$k]}"
  [[ "${SECTION[$k]}" == "secrets" || "${SECTION[$k]}" == "variables" ]] && [[ "$v" == *"'"* ]] \
    && { echo "  x $k mengandung tanda petik tunggal (') -- tidak didukung" >&2; errors=1; }
done
[[ "${VALUES[DEPLOY_PATH]:-}" == /* ]] || { echo "  x DEPLOY_PATH harus path absolut (diawali /)" >&2; errors=1; }
[ "$errors" -eq 0 ] || die "Perbaiki $FILE lalu jalankan ulang."

# ---- kirim ----
expand_path() { local p="$1"; printf '%s' "${p/#\~/$HOME}"; }

set_variable() {
  local k="$1" v="$2"
  if [ -z "$v" ]; then log "  - variable $k dilewati (kosong)"; return; fi
  log "  + variable $k = $v"
  [ "$DRY_RUN" -eq 1 ] || gh variable set "$k" --repo "$REPO" --body "$v" >/dev/null
}

set_secret() {   # baca isi dari stdin
  local k="$1"
  log "  + secret   $k"
  if [ "$DRY_RUN" -eq 1 ]; then cat >/dev/null; else gh secret set "$k" --repo "$REPO" >/dev/null; fi
}

for k in "${ORDER[@]}"; do
  v="${VALUES[$k]}"
  case "${SECTION[$k]}" in
    variables) set_variable "$k" "$v" ;;
    secrets)
      [ -n "$v" ] || { log "  - secret $k dilewati (kosong)"; continue; }
      printf '%s' "$v" | set_secret "$k" ;;
    secret-files)
      f="$(expand_path "$v")"; [ -f "$f" ] || die "File untuk $k tidak ada: $f"
      set_secret "$k" < "$f" ;;
    secret-files-b64)
      f="$(expand_path "$v")"; [ -f "$f" ] || die "File untuk $k tidak ada: $f"
      base64 < "$f" | tr -d '\n' | set_secret "$k" ;;
    *) die "Section tidak dikenal untuk $k: ${SECTION[$k]}" ;;
  esac
done

# SSH_KNOWN_HOSTS: sidik jari host server supaya CI menolak server palsu (MITM)
if [ "$KNOWN_HOSTS" -eq 1 ]; then
  command -v ssh-keyscan >/dev/null || die "ssh-keyscan tidak ada; pakai --no-known-hosts dan isi SSH_KNOWN_HOSTS manual"
  port="${VALUES[SERVER_PORT]:-22}"
  if [ "$DRY_RUN" -eq 1 ]; then
    log "  + secret   SSH_KNOWN_HOSTS (dari ssh-keyscan ${VALUES[SERVER_HOST]}:$port)"
  else
    hosts="$(ssh-keyscan -p "$port" -t ed25519 "${VALUES[SERVER_HOST]}" 2>/dev/null)" \
      || die "ssh-keyscan gagal ke ${VALUES[SERVER_HOST]}:$port"
    [ -n "$hosts" ] || die "ssh-keyscan tidak mengembalikan host key"
    printf '%s\n' "$hosts" | set_secret SSH_KNOWN_HOSTS
    log "    (diambil sekali dari jaringan Anda sekarang -- pastikan Anda memang terhubung ke server yang benar)"
  fi
fi

log "Selesai."
