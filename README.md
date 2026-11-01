# weddinglage

Aplicación web de boda navideña (18 de diciembre de 2027): invitación digital, RSVP, información del día y galería colaborativa.

**Stack:** Angular + TypeScript · Spring Boot · PostgreSQL · Flyway · MinIO · Docker + Nginx  
**Enfoque:** primero desarrollo en **local** con Docker Compose; el despliegue en **VPS** viene después (cuando configures dominio, secretos y backups).

## Documentación

| Documento | Contenido |
|-----------|-----------|
| [`docs/especificacion-boda-navidena.md`](docs/especificacion-boda-navidena.md) | Especificación funcional y técnica (mejorada) |
| [`docs/plan-desarrollo.md`](docs/plan-desarrollo.md) | Plan de desarrollo completo (fases, API, RGPD, costes) |

## Estructura del monorepo

```text
frontend/   # Angular (scaffold pendiente de generación)
backend/    # Spring Boot (scaffold pendiente de generación)
docker/     # Nginx, overlays y scripts de Compose
docs/       # Especificación y plan
```

## Requisitos locales

- Docker y Docker Compose
- (Opcional) Node.js / JDK solo si desarrollas fuera de los contenedores

## Arranque local (objetivo)

Cuando existan los Dockerfiles y el `docker-compose.yml`:

```bash
cp .env.example .env   # cuando esté disponible
docker compose up --build
```

Servicios previstos:

| Servicio | Rol |
|----------|-----|
| Nginx | SPA + proxy `/api` (mismo origen → cookies de sesión) |
| API | Spring Boot |
| PostgreSQL | Datos + Flyway |
| MinIO | Almacenamiento S3-compatible de fotos |
| Mailhog (opcional) | Correo en desarrollo |

La app pública quedará en `http://localhost` (puerto según Compose). Admin en `/admin` con sesión por cookie HttpOnly + CSRF.

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
