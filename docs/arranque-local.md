# Arranque en local — Weddinglage

Guía corta para levantar el proyecto en tu PC.

| | |
|--|--|
| Carpeta local | `C:\Users\pw-gdelacruz\Documents\privado\Weddinglage` |
| Remoto | https://github.com/Delacruz-6/Weddinglage |
| Rama | `main` |

------------------------------------------------------------------------

## 1. Qué hay hoy en GitHub

En el remoto verás sobre todo **carpetas vacías** (`frontend/`, `backend/`, `docker/` con `.gitkeep`) más la documentación en `docs/`.

Aún **no** está el código de la app ni el `docker-compose.yml`. Por eso `docker compose up` **todavía no puede arrancar la boda**.

Este documento sirve para:

1. Dejar tu PC listo.
2. Saber los comandos exactos de arranque cuando exista Compose (Fase 0).
3. No confundir “repo clonado” con “app en marcha”.

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

Opcional para desarrollar fuera de Docker más adelante: Node.js LTS y JDK 21.

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

## 4. Cómo arrancar (cuando exista Compose)

Cuando en la raíz del repo haya `docker-compose.yml` (y Dockerfiles), el arranque será:

```bat
cd C:\Users\pw-gdelacruz\Documents\privado\Weddinglage
copy .env.example .env
notepad .env
docker compose up --build
```

En `.env` cambia solo las contraseñas locales. **No** subas `.env` a Git (ya está en `.gitignore`).

### URLs previstas

| URL | Qué es |
|-----|--------|
| http://localhost | Web (Angular vía Nginx) |
| http://localhost/api/actuator/health | Salud de la API (nombre exacto según Fase 0) |
| http://localhost/admin | Panel admin |
| http://localhost:9001 | Consola MinIO (si se publica el puerto) |
| http://localhost:8025 | Mailhog (correo de prueba, opcional) |

### Parar

```bat
docker compose down
```

Para borrar también datos de Postgres/MinIO (cuidado):

```bat
docker compose down -v
```

------------------------------------------------------------------------

## 5. Qué servicios levantará Compose

| Servicio | Rol |
|----------|-----|
| `nginx` | SPA + proxy `/api` (mismo origen → cookies) |
| `api` | Spring Boot |
| `postgres` | Base de datos + Flyway |
| `minio` | Fotos (S3-compatible) |
| `mailhog` | Correo en desarrollo (opcional) |

Misma idea en local y luego en VPS; en local no hace falta HTTPS todavía.

------------------------------------------------------------------------

## 6. Si el arranque falla

| Problema | Qué hacer |
|----------|-----------|
| `no configuration file provided` / no encuentra Compose | Aún no está Fase 0: falta `docker-compose.yml` en la raíz |
| Docker no responde | Abre Docker Desktop y espera a que esté “Running” |
| Puerto 80 ocupado | Cuando exista Compose, cambia el mapeo (p. ej. `"8080:80"`) y entra en http://localhost:8080 |
| `pull` / `push` pide login | Autentica GitHub en Windows (`gh auth login` o Credential Manager), sin pegar el PAT en el `remote` |
| Carpetas `frontend`/`backend` vacías | Normal hoy; el scaffold Angular/Spring llega con la Fase 0 |

------------------------------------------------------------------------

## 7. Checklist mínimo

- [ ] Repo en `Documents\privado\Weddinglage`  
- [ ] `git pull origin main` OK  
- [ ] Docker Desktop OK  
- [ ] Esperar (o implementar) Fase 0: Compose + Angular + Spring mínimos  
- [ ] `copy .env.example .env` y `docker compose up --build`  
- [ ] Abrir http://localhost  

------------------------------------------------------------------------

## 8. Relacionado

- Plan operativo Git + local: [`plan-local-github.md`](./plan-local-github.md)  
- Fases y esfuerzo: [`plan-desarrollo.md`](./plan-desarrollo.md)  
- Producto: [`especificacion-boda-navidena.md`](./especificacion-boda-navidena.md)
