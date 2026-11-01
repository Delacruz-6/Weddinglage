# Weddinglage MVP (Fases 0–4)

Objective: Runnable monorepo — Angular + Spring Boot + Postgres + S3-compatible storage via Docker Compose; local-first MVP.
Branch: `feature/mvp-foundations`
Delivery: ask-on-risk · Test runner: JUnit (backend) + Docker Compose smoke

## Specs

S1. "docker compose up --build OK"; "http://localhost/api/actuator/health → 200"; "Login admin con creds .env"; "Cambiar un texto de config y verlo en GET /api/public/wedding / shell público sin rebuild de contenidos."

S2. "Móvil usable; countdown correcto; sin autoplay; contenidos desde API; IBAN vacío no rompe UI." — bienvenida saltables, countdown Europe/Madrid hasta 18/12/2027, secciones desde API, playlist sin autoplay.

S3. "Familia create→edit; duplicado 409; export usable; cupos no inventados (UI lista, bloqueo solo si hay BusCapacity)." — RSVP por Invitation, editToken hasheado, adornos, exports CSV.

S4. "Sin publicación no hay acceso por URL adivinable; con doble condición sí; GPS ausente en archivo servido." — upload token/QR, MinIO/S3Mock, moderación, publishAt AND adminAuthorized.

S5. "Ensayo con 5–10 invitaciones seed; restore documentado; app estable en Compose." — dashboard real, hardening, backups docs.

## Tasks

- T0 S1 — inline — done — Fase 0 cimientos (Compose healthy, login, config round-trip)
- T1 S2 — inline — done — Fase 1 web pública (Angular SPA, timeline, countdown, sections)
- T2 S3 — inline — done — Fase 2 RSVP + adornos + CSV exports
- T3 S4 — inline — done — Fase 3 photos + publication gate (403 until published)
- T4 S5 — inline — done — Fase 4 dashboard + backups-and-vps.md + seed invitations

## Log

L1. User: Implement the plan as specified (Plan completo MVP Weddinglage). Do NOT edit the plan file. Complete all to-dos.
L2. Local object store: `adobe/s3mock` (official MinIO image blocked / cleanstart volume perms failed on Docker Desktop Windows).
L3. Smoke: health 200, admin login, config PUT→public GET, RSVP 409, photos 403 unpublished, ornament STAR for INVITE001, exports/guests 200, SPA app-root.
L4. Fixed export MultipleBagFetchException; UTF-8 timeline seeds via PostgreSQL U& escapes; V5 seed ornaments.
