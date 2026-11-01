# Backups and VPS notes — Weddinglage

Operational guide for Phase 4 hardening. Keep secrets out of git.

## Local / Compose backups

### PostgreSQL

```bat
docker compose exec -T postgres pg_dump -U %POSTGRES_USER% %POSTGRES_DB% > backup-weddinglage.sql
```

Restore into a fresh volume (destructive — only on purpose):

```bat
docker compose exec -T postgres psql -U %POSTGRES_USER% -d %POSTGRES_DB% < backup-weddinglage.sql
```

Practice restore once before the wedding month: dump → new empty Postgres → restore → spot-check admin login and invitation count.

### MinIO

Sync the data volume or use `mc mirror` against a second bucket/path off-site.

Local Compose uses Adobe S3Mock (ephemeral). On a VPS with real MinIO/R2, snapshot the object volume weekly (or `mc mirror` off-site) and retain ≥ 14 days. The API creates the bucket on first upload if missing.

## Production VPS (brief)

1. Clone the private repo; copy `.env.example` → `.env` with strong secrets.
2. Point DNS at the VPS; terminate TLS (Caddy, Certbot + Nginx, or similar).
3. Run the same Compose stack (or a `docker-compose.prod.yml` overlay) with published 443 only.
4. Schedule daily `pg_dump` and weekly MinIO volume snapshot.
5. Do not deploy feature changes on the wedding day.

## Security checklist (pre-wedding)

- [ ] HTTPS valid; HTTP redirects to HTTPS
- [ ] Admin password strong; lockout verified
- [ ] Gallery publication **not** authorized until post-event decision
- [ ] Upload tokens rotated/printed
- [ ] Rate limits active on login, RSVP, upload
- [ ] Backup restore tested within last 30 days
