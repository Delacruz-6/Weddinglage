# Arranque en local — Weddinglage

Guía corta para levantar el proyecto en tu PC con Docker Compose.

| | |
|--|--|
| Carpeta local | `C:\Users\pw-gdelacruz\Documents\privado\Weddinglage` |
| Remoto | https://github.com/Delacruz-6/Weddinglage |
| Rama | `main` |

------------------------------------------------------------------------

## 1. Qué hay en el repo

En la raíz están `docker-compose.yml`, `.env.example`, `backend/Dockerfile`, `frontend/Dockerfile` (SPA en nginx `:80`) y `docker/nginx/default.conf` (edge proxy).

Este documento sirve para:

1. Dejar tu PC listo.
2. Arrancar con Compose.
3. Saber URLs y cómo parar / resetear datos.

------------------------------------------------------------------------

## 2. Requisitos en Windows

Instala y comprueba:

```bat
git --version
docker version
docker compose version
```

- [ ] Git  
- [ ] Docker Desktop (con WSL2 si te lo pide) **en marcha** (icono de ballena)  
- [ ] Acceso al repo **privado** con tu cuenta GitHub  

Opcional para desarrollar fuera de Docker: Node.js LTS y JDK 21.

------------------------------------------------------------------------

## 3. Tener el repo en disco

Si aún no está clonado:

```bat
mkdir C:\Users\pw-gdelacruz\Documents\privado
cd C:\Users\pw-gdelacruz\Documents\privado
git clone https://github.com/Delacruz-6/Weddinglage.git
cd Weddinglage
```

Si ya existe la carpeta:

```bat
cd C:\Users\pw-gdelacruz\Documents\privado\Weddinglage
git checkout main
git pull origin main
```

Comprueba que el remoto **no** lleva token en la URL:

```bat
git remote -v
```

Debe verse: `https://github.com/Delacruz-6/Weddinglage.git`

------------------------------------------------------------------------

## 4. Cómo arrancar

```bat
cd C:\Users\pw-gdelacruz\Documents\privado\Weddinglage
copy .env.example .env
notepad .env
docker compose up --build
```

En `.env` cambia contraseñas locales y `TOKEN_PEPPER`. **No** subas `.env` a Git (ya está en `.gitignore`).

Compose fija hosts internos (`postgres`, `minio`, `mailhog`); no hace falta `POSTGRES_HOST` en `.env`.

### URLs

| URL | Qué es |
|-----|--------|
| http://localhost | Web (Nginx → SPA) |
| http://localhost/api/actuator/health | Salud de la API |
| http://localhost/admin | Panel admin |
| http://localhost:9000 | Object storage S3Mock (API) |
| http://localhost:8025 | Mailhog (correo de prueba) |
| localhost:5432 | Postgres (debug local) |

### Cómo está cableado Nginx

- Puerto **80** en el host → servicio `nginx` (`nginx:alpine` + `docker/nginx/default.conf`)
- `location /api/` → `http://api:8080/api/` (headers Host / X-Forwarded-* y `Set-Cookie`)
- `location /` → `http://web:80/` (contenedor Angular/nginx)

### Parar

```bat
docker compose down
```

Para borrar también datos de Postgres/MinIO (cuidado):

```bat
docker compose down -v
```

------------------------------------------------------------------------

## 5. Servicios de Compose

| Servicio | Rol |
|----------|-----|
| `nginx` | Edge: SPA + proxy `/api` (mismo origen → cookies) |
| `web` | Frontend Angular servido en `:80` |
| `api` | Spring Boot |
| `postgres` | Base de datos + Flyway |
| `minio` | Fotos (S3-compatible) |
| `minio-init` | Crea el bucket una vez |
| `mailhog` | Correo en desarrollo |

Misma idea en local y luego en VPS; en local no hace falta HTTPS todavía.

------------------------------------------------------------------------

## 6. Si el arranque falla

| Problema | Qué hacer |
|----------|-----------|
| `no configuration file provided` | Ejecutá los comandos desde la raíz del repo (`Weddinglage`) |
| Docker no responde | Abrí Docker Desktop y esperá a que esté “Running” |
| Puerto 80 ocupado | Cambiá el mapeo de `nginx` a p. ej. `"8080:80"` y entrá en http://localhost:8080 |
| Healthcheck de `api` falla | La JRE puede no traer `curl`/`wget`; el contenedor igual puede estar sano — revisá logs con `docker compose logs api` |
| Falta `frontend/Dockerfile` | El build de `web` lo necesita; regeneralo o pedí el scaffold |
| `pull` / `push` pide login | Autenticá GitHub en Windows (`gh auth login` o Credential Manager), sin pegar el PAT en el `remote` |

------------------------------------------------------------------------

## 7. Checklist mínimo

- [ ] Repo en `Documents\privado\Weddinglage`  
- [ ] `git pull origin main` OK  
- [ ] Docker Desktop OK  
- [ ] `copy .env.example .env` y editar secretos  
- [ ] `docker compose up --build`  
- [ ] Abrir http://localhost  

------------------------------------------------------------------------

## 8. Relacionado

- Plan operativo Git + local: [`plan-local-github.md`](./plan-local-github.md)  
- Fases y esfuerzo: [`plan-desarrollo.md`](./plan-desarrollo.md)  
- Producto: [`especificacion-boda-navidena.md`](./especificacion-boda-navidena.md)
