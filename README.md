# weddinglage

Aplicación web de boda navideña (18 de diciembre de 2027): invitación digital, RSVP, información del día y galería colaborativa.

**Stack:** Angular + TypeScript · Spring Boot · PostgreSQL · Flyway · MinIO · Docker + Nginx  
**Enfoque:** primero desarrollo en **local** con Docker Compose; el despliegue en **VPS** viene después (cuando configures dominio, secretos y backups).

## Documentación

| Documento | Contenido |
|-----------|-----------|
| [`docs/especificacion-boda-navidena.md`](docs/especificacion-boda-navidena.md) | Especificación funcional y técnica (mejorada) |
| [`docs/plan-desarrollo.md`](docs/plan-desarrollo.md) | Plan de desarrollo completo (fases, API, RGPD, costes) |
| [`docs/arranque-local.md`](docs/arranque-local.md) | Arranque local con Docker Compose (Windows) |

## Estructura del monorepo

```text
frontend/   # Angular SPA (imagen nginx en :80)
backend/    # Spring Boot API (:8080 interno)
docker/     # Nginx edge (proxy SPA + /api)
docs/       # Especificación y plan
```

## Requisitos locales

- Docker y Docker Compose
- (Opcional) Node.js / JDK solo si desarrollas fuera de los contenedores

## Arranque local

```bash
cp .env.example .env   # en Windows: copy .env.example .env
# Editar .env: contraseñas locales y TOKEN_PEPPER
docker compose up --build
```

Detalle paso a paso en Windows: [`docs/arranque-local.md`](docs/arranque-local.md).

Entrada pública: **http://localhost** (Nginx en `:80`).

| Servicio | Rol | Puertos |
|----------|-----|---------|
| `nginx` | Proxy: `/` → `web:80`, `/api/` → `api:8080` | **80** |
| `web` | SPA Angular (nginx interno) | 80 (interno) |
| `api` | Spring Boot | 8080 (interno) |
| `postgres` | Datos + Flyway | 5432 (debug) |
| `minio` | S3-compatible local (Adobe S3Mock) | 9000→9090 |
| `mailhog` | SMTP de desarrollo + UI | 1025, 8025 |

Arquitectura de red: el navegador solo habla con Nginx. Cookies de sesión y CSRF van por el mismo origen (`localhost`) gracias al proxy `/api/`.

Admin en `/admin` con sesión por cookie HttpOnly + CSRF.

## Remoto GitHub

Repositorio previsto: `Delacruz-6/weddinglage` (**privado** recomendado: datos de invitados e IBAN).

Para publicar este árbol por primera vez (desde tu máquina):

```bash
git remote add origin git@github.com:Delacruz-6/weddinglage.git
git push -u origin main
```

## Producción (más adelante)

Mismo Compose (o variante `docker-compose.prod.yml`) en un VPS: DNS, HTTPS (Let’s Encrypt), secretos fuera de git y backups de Postgres + MinIO. Detalle de costes y calendario en el [plan de desarrollo](docs/plan-desarrollo.md).

## Datos pendientes

No hardcodear nombres, hacienda, IBAN, playlist, cupos de autobús ni fechas de corte: se cargan por configuración/admin cuando existan. Ver sección de decisiones pendientes en la especificación y en el plan.
