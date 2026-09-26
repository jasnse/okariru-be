# Okariru — Backend

Backend REST API untuk aplikasi pinjaman/koperasi **Okariru**. Dipakai oleh app mobile (Android) dan FE web.

- Repo: https://github.com/jasnse/okariru-be
- Base URL (dev/staging saat ini): `http://35.184.39.133:8080/`

## Tech Stack

- **Java 21** + **Kotlin 2.3.20** (mixed, build lewat `kotlin-maven-plugin`)
- **Spring Boot 4.1.0** — Web MVC, Data JPA, Validation, Security, Actuator, Cache, Mail
- **PostgreSQL** (schema default: `core`)
- **Redis** — dipakai untuk OTP (reset password) dan cache
- **JWT** (`jjwt` 0.12.6) — autentikasi
- **Firebase Admin SDK** — push notification (FCM)
- **SpringDoc OpenAPI** — Swagger UI
- **JaCoCo** — code coverage, minimum **80%** (line & branch) di `verify` phase
- **Lombok**

## Struktur Modul (package utama)

```
com.project.binar.okariru
├── controller/     # REST endpoints
├── entity/         # JPA entities
├── dto/            # request/response DTO (excluded dari coverage)
├── config/         # security, CORS, redis, firebase config (excluded dari coverage)
└── ...
```

## Menjalankan Secara Lokal

### 1. Lewat Docker Compose (disarankan)

```bash
make init      # copy .env.example -> .env, siapkan folder data
```

Isi `.env` (lihat `.env.example` untuk daftar lengkap variabel):
- `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `DB_SCHEMA`
- `JWT_SECRET` (min. 32 karakter — generate: `openssl rand -hex 32`)
- `SPRING_MAIL_USERNAME` / `SPRING_MAIL_PASSWORD` (Gmail App Password, untuk email OTP reset password)
- Firebase: taruh `firebase-service-account.json` di root project

```bash
make up        # build + jalankan postgres, redis, app
make logs-app  # lihat log aplikasi
```

Perintah lain: `make ps`, `make down`, `make restart`, `make backup`, `make restore FILE=...`, `make psql`, `make redis-cli` — lihat `make help` untuk daftar lengkap.

### 2. Lewat IDE (tanpa Docker)

- Jalankan PostgreSQL & Redis lokal (atau lewat Docker hanya untuk 2 service ini).
- Buat `.env` di root project (dibaca otomatis lewat `spring.config.import=optional:file:.env`), isi minimal `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`.
- Kalau tidak lewat Docker, override `DB_URL` ke `jdbc:postgresql://localhost:5432/okariru?currentSchema=core`.
- Run `OkariruApplication`.

### Testing & Coverage

```bash
./mvnw test              # unit test
./mvnw verify             # test + JaCoCo coverage check (minimum 80%)
```

## Autentikasi

- Login (customer & employee) mengembalikan JWT.
- Kirim token di header `Authorization: Bearer <token>` untuk endpoint yang butuh auth.
- `app.security.jwt-ttl-minutes` default 60 menit.

## Daftar Endpoint (ringkas)

| Resource | Base path | Keterangan |
|---|---|---|
| Login | `POST /api/v1/login/customer`, `POST /api/v1/login/employe` | Login customer / karyawan |
| Customer | `/api/v1/customer` | Register, get, update profile (`/me`), FCM token, delete |
| Employee | `/api/v1/employees` | CRUD karyawan |
| Pinjaman (produk) | `/api/v1/pinjaman` | CRUD jenis/produk pinjaman |
| Pinjaman Transaction | `/api/v1/pinjaman/transaction` | Ajukan pinjaman, review, approval, disburse |
| Angsuran | `/api/v1/angsuran` | Generate cicilan, bayar angsuran |
| Plafond | `/api/v1/plafond` | Limit pinjaman customer |
| Document | `/api/v1/document` | Upload/download dokumen pendukung pinjaman (multipart) |
| Reset Password | `/api/v1/reset` | Forgot password (OTP email), validate OTP, reset password |
| Notification | `/api/v1/notification` | Notifikasi in-app |
| Role / RoleGroup | `/api/v1/roles`, `/api/v1/roleGroup` | RBAC — lihat `RBAC.md` di root project |
| Menu / Menugroup | `/api/v1/menu`, `/api/v1/menugroup` | Menu untuk RBAC/dashboard employee |

Dokumentasi lengkap & try-it-out: **Swagger UI** — `http://<host>:8080/swagger-ui.html` (springdoc-openapi).

## Environment Variables Penting

| Var | Fungsi |
|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_SCHEMA` | Koneksi PostgreSQL |
| `REDIS_HOST`, `REDIS_PORT` | Koneksi Redis (OTP store) |
| `JWT_SECRET`, `JWT_TTL_MINUTES` | Konfigurasi token JWT |
| `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD` | SMTP Gmail untuk kirim OTP reset password |
| `FIREBASE_CREDENTIALS_PATH` | Path service account Firebase (FCM push notification). Kalau file tidak ada, push notification otomatis nonaktif |
| `APP_SECURITY_CORS_ALLOWED_ORIGIN` | Origin FE yang diizinkan CORS |
| `UPLOADS_DIR` | Folder penyimpanan file upload dokumen |

**Jangan commit** `.env`, `firebase-service-account.json`, atau file kredensial lain — semua sudah di `.gitignore`.

## Terkait

- Mobile app (Android/Kotlin): https://github.com/jasnse/Okariru-Mobile
- Dokumen desain/slicing & RBAC: lihat `../RBAC.md` dan `../*.md` di root project `C:\Binar\project`
