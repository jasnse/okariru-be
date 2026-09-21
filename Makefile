DC ?= docker compose
BACKUP_DIR ?= backups

.DEFAULT_GOAL := help
.PHONY: help init config build up down restart rebuild ps logs logs-app logs-db logs-redis \
        shell psql redis-cli backup restore clean

help: ## Tampilkan daftar perintah
	@grep -E '^[a-zA-Z_-]+:.*?## ' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "} {printf "  make %-12s %s\n", $$1, $$2}'

init: ## Siapkan folder data dan .env (jalankan sekali di awal)
	mkdir -p data/postgres data/redis uploads/images db/init $(BACKUP_DIR)
	@if [ ! -f .env ]; then cp .env.example .env && echo ".env dibuat dari .env.example, isi nilainya dulu."; else echo ".env sudah ada."; fi

config: ## Validasi docker-compose.yml + .env
	$(DC) config --quiet && echo "Konfigurasi valid."

build: ## Build image aplikasi
	$(DC) build

up: ## Jalankan semua service di background (build kalau perlu)
	$(DC) up -d --build

down: ## Hentikan dan hapus container (data di host tetap aman)
	$(DC) down

restart: ## Restart semua service
	$(DC) restart

rebuild: ## Build ulang tanpa cache lalu jalankan
	$(DC) build --no-cache
	$(DC) up -d

rc:
	$(DC) up -d


ps: ## Lihat status container
	$(DC) ps

logs: ## Ikuti log semua service
	$(DC) logs -f --tail=100

logs-app: ## Ikuti log aplikasi Spring Boot
	$(DC) logs -f --tail=100 app

logs-db: ## Ikuti log PostgreSQL
	$(DC) logs -f --tail=100 postgres

logs-redis: ## Ikuti log Redis
	$(DC) logs -f --tail=100 redis

shell: ## Masuk shell container aplikasi
	$(DC) exec app sh

psql: ## Masuk konsol psql di container PostgreSQL
	$(DC) exec postgres sh -c 'psql -U "$$POSTGRES_USER" -d "$$POSTGRES_DB"'

redis-cli: ## Masuk redis-cli
	$(DC) exec redis redis-cli

backup: ## Backup database ke folder backups/ (file .sql bertimestamp)
	mkdir -p $(BACKUP_DIR)
	$(DC) exec -T postgres sh -c 'pg_dump -U "$$POSTGRES_USER" "$$POSTGRES_DB"' > $(BACKUP_DIR)/okariru_$$(date +%Y%m%d_%H%M%S).sql
	@echo "Backup tersimpan di $(BACKUP_DIR)/"

restore: ## Restore database: make restore FILE=backups/xxx.sql
	@test -n "$(FILE)" || (echo "Pakai: make restore FILE=backups/nama-file.sql" && exit 1)
	$(DC) exec -T postgres sh -c 'psql -U "$$POSTGRES_USER" -d "$$POSTGRES_DB"' < $(FILE)

clean: ## Hapus container + image lokal aplikasi (data database TIDAK dihapus)
	$(DC) down --rmi local --remove-orphans
