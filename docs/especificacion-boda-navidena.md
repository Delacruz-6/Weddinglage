# Especificación funcional y técnica — Aplicación web de boda navideña

**Fecha de la boda:** 18 de diciembre de 2027  
**Idioma inicial:** español  
**Tipo de producto:** aplicación web responsive y mobile-first (PWA opcional **post-MVP**)  
**Usuarios:** invitados (con código de invitación) y administrador(es)  
**Repositorio:** monorepo `weddinglage` (`frontend/`, `backend/`, `docker/`, `docs/`)

------------------------------------------------------------------------

## 0. Decisiones fijadas (alcance técnico)

| Tema | Decisión |
|------|----------|
| Frontend | Angular + TypeScript, SCSS, mobile-first |
| Backend | Java + Spring Boot, API REST |
| Persistencia | PostgreSQL + Flyway |
| Auth admin | Cookie HttpOnly + CSRF (mismo origen vía Nginx); **no** JWT en `localStorage` |
| Fotos | MinIO (S3-compatible) en local y en VPS al inicio |
| Acceso invitados | Códigos de invitación; token de subida en QR de fotos; `editToken` hasheado para RSVP |
| Despliegue | Docker Compose en local primero; VPS (Nginx + HTTPS) después |
| MVP excluye | PWA, libro de firmas, árbol virtual (post-MVP) |
| MFA admin | Opcional, más adelante |

**No inventar** nombres de la pareja, hacienda, IBAN, playlist, cupos de autobús, fechas de corte ni textos legales definitivos: se configuran cuando existan.

------------------------------------------------------------------------

## 1. Visión del producto

Crear una aplicación web para la boda que funcione como invitación digital, centro de información para los invitados, formulario de confirmación de asistencia, experiencia navideña interactiva y galería colaborativa de fotografías.

La experiencia debe estar diseñada prioritariamente para móviles: la mayoría accederá escaneando un QR o pulsando un enlace de WhatsApp que incluye el **código de invitación**. También debe ofrecer una experiencia completa en tabletas y ordenadores.

La web pública debe transmitir una boda navideña elegante, romántica y cálida. La complejidad de gestión queda en el panel privado de administración.

### Objetivos principales

- Ofrecer toda la información de la boda desde un único sitio.
- Facilitar la confirmación y modificación de asistencia **por invitación**, sin spam anónimo.
- Recopilar necesidades alimentarias, transporte y preferencias musicales.
- Explicar cómo llegar a la hacienda y mostrar fotografías del lugar.
- Mostrar un timeline visual e interactivo del día de la boda.
- Permitir que los invitados suban fotografías mediante QR con token.
- Mantener las fotografías privadas hasta fecha configurada **y** autorización expresa de administración.
- Permitir que el administrador gestione invitados, fotografías, contenido y configuración.
- Asignar a cada invitación un adorno navideño (árbol virtual = post-MVP).

------------------------------------------------------------------------

## 2. Diseño visual e identidad navideña

### 2.1. Dirección artística

La interfaz debe ser navideña, elegante y romántica, evitando estética infantil o recargada.

**Paleta orientativa:**

- Verde abeto: `#173B30`
- Verde oscuro: `#102820`
- Rojo burdeos: `#762F3D`
- Dorado cálido: `#C7A96B`
- Blanco nieve: `#F7F5EF`
- Crema: `#EDE5D5`

**Recursos visuales:** copos de nieve discretos, guirnaldas, luces cálidas, texturas sutiles, fotografías de la pareja y de la hacienda; tipografía serif para titulares y sans-serif legible para formularios.

Las animaciones deben ser ligeras, no bloquear la navegación y respetar `prefers-reduced-motion`.

### 2.2. Experiencia responsive y mobile-first

**Requisitos obligatorios:**

- Diseñar primero para móvil y ampliar a tablet/escritorio.
- CSS Grid y Flexbox; sin scroll horizontal.
- Menú móvil usable con una mano; áreas táctiles ≈ 44 × 44 px.
- Formularios con campos grandes y teclado móvil apropiado.
- Imágenes adaptativas (`srcset`, WebP/AVIF cuando proceda) y lazy loading fuera del primer viewport.
- No depender de hover para acciones críticas.
- Galería y timeline usables en pantallas pequeñas.
- Panel admin responsive (uso principal en escritorio).
- Contraste suficiente, teclado y etiquetas accesibles.
- Estados visibles de carga, error, éxito y vacío.
- Compatibilidad con Chrome, Safari, Firefox y Edge recientes (incl. iOS/Android).

### 2.3. PWA (post-MVP)

La instalabilidad como PWA (`manifest`, iconos, service worker) **queda fuera del MVP**. Cuando se aborde:

- No marcar operaciones de red como completadas hasta confirmación del servidor.
- Informar claramente si no hay conexión al subir fotos o enviar formularios.

------------------------------------------------------------------------

## 3. Pantalla de bienvenida y animación inicial

Al acceder a la página pública se mostrará una introducción audiovisual navideña.

### Secuencia propuesta

1. Fondo oscuro con pequeñas estrellas.
2. Animación suave de nieve.
3. Escenario navideño con luces cálidas.
4. Se ilumina un árbol de Navidad.
5. Aparecen los nombres de la pareja (desde configuración; no hardcode).
6. Fecha: **18 · 12 · 2027**.
7. Botón **«Entrar en nuestra Navidad»**.

La introducción debe ser breve, **saltarse** y no obligar a esperar.

### Música de fondo

- Playlist navideña **propia o con licencia clara** (configurable desde admin). No usar canciones comerciales sin derechos.
- Reproductor persistente durante la navegación.
- Controles: reproducir, pausar, anterior/siguiente, volumen.
- Preferencia de sonido en sesión o almacenamiento local.
- **Arranque solo tras gesto del usuario** (p. ej. «Entrar en nuestra Navidad»). Sin autoplay con sonido.
- Cargar la playlist solo cuando el usuario decida reproducirla.

------------------------------------------------------------------------

## 4. Página principal y cuenta atrás

La portada pública debe mostrar (datos desde API/configuración):

- Nombres de la pareja.
- Fecha: 18 de diciembre de 2027.
- Mensaje de bienvenida.
- Fotografía principal.
- Cuenta atrás (días, horas, minutos, segundos).
- Accesos a confirmar asistencia, cómo llegar, timeline y galería (en móvil, atajos bajo el fold si hace falta priorizar identidad).
- Decoración navideña coherente.

La cuenta atrás se calcula en el cliente a partir de la fecha objetivo del servidor, zona `Europe/Madrid` (DST incluido), con estado especial al llegar la fecha.

------------------------------------------------------------------------

## 5. Navegación y secciones públicas

Menú compacto con acceso a:

1. Inicio  
2. Nuestra historia  
3. La celebración  
4. Cómo llegar  
5. Horario del día  
6. Confirmar asistencia  
7. Menú y necesidades alimentarias  
8. Regalo de boda  
9. Adorno navideño (asignación por invitación; árbol virtual post-MVP)  
10. Galería de fotografías  
11. Libro de firmas (**post-MVP**)

Las secciones podrán reorganizarse o desactivarse desde administración.

------------------------------------------------------------------------

## 6. Nuestra historia

Sección editorial editable desde admin:

- Texto de presentación.
- Fotografías.
- Línea temporal de la relación (opcional).
- Mensaje personal para los invitados.

------------------------------------------------------------------------

## 7. La celebración y cómo llegar

### 7.1. Información del lugar

Campos configurables (valores definitivos **pendientes**):

- Nombre de la hacienda, dirección, CP, municipio, provincia.
- Coordenadas, teléfono de contacto, indicaciones.
- Hora de inicio de ceremonia: **13:00**.
- Fotografías de la hacienda.

### 7.2. Enlaces a mapas

- Abrir en Google Maps  
- Abrir en Apple Maps  
- Ver ubicación en el mapa  

Construidos a partir de dirección o coordenadas. No asumir que todos usan Google Maps.

### 7.3. Fotografías de la hacienda

Galería del lugar (fachada, exteriores, ceremonia, salón, jardines, aparcamiento), optimizada, con alt text y ampliación.

### 7.4. Indicaciones adicionales

Aparcamiento, accesibilidad, anticipación, punto de encuentro del autobús, paradas/horarios, teléfono de incidencias (si se publica).

------------------------------------------------------------------------

## 8. Timeline visual e interactivo del día

Sección **«Así será nuestro día»**: línea temporal con iconos, horas, descripciones y elementos navideños.

### 8.1. Horario inicial (editable en admin)

| Hora  | Momento         | Descripción                          |
|-------|-----------------|--------------------------------------|
| 13:00 | Ceremonia       | Comienza la ceremonia de boda.       |
| 14:00 | Cóctel          | Aperitivos y encuentro.              |
| 15:30 | Almuerzo        | Comida de celebración.               |
| 17:00 | Barra libre     | Fiesta y música.                     |
| 18:00 | Primer autobús  | Primera salida de regreso.           |
| 22:00 | Segundo autobús | Segunda salida de regreso.           |

### 8.2. Comportamiento visual

- Móvil: timeline vertical con tarjetas grandes.
- Escritorio: horizontal o vertical ampliada.
- Iconos; fotos/descripciones ampliadas; diferenciar salidas de autobús.
- Admin puede añadir, eliminar, reordenar o editar; orden por hora salvo override.

### 8.3. Interactividad

Al tocar un evento: tarjeta ampliada. El día de la boda se puede resaltar el siguiente evento según `Europe/Madrid`.

### 8.4. Autobuses

Horarios 18:00 y 22:00 muy visibles. Destino, recogida y paradas configurables.

**Cupo:** cuando se configure capacidad por horario/parada, el RSVP debe **bloquear** nuevas reservas al llenarse o ofrecer **lista de espera** (configurable). No permitir reservar ambos autobuses sin operativa definida. Mientras la capacidad esté pendiente, la UI recoge la intención sin inventar cupos.

------------------------------------------------------------------------

## 9. Confirmación de asistencia (RSVP)

Formulario público **sin cuenta de usuario**, pero **no anónimo abierto**: requiere **código de invitación** válido (enlace WhatsApp/QR).

### 9.1. Datos del formulario

- Nombre, apellidos, correo (UX / contacto; **no** son el secreto de autorización).
- Asistencia: sí / no.
- Número de adultos y niños dentro del cupo de la invitación.
- Menú infantil, alergias, intolerancias, necesidades especiales.
- Necesidad de autobús; horario/parada si aplica (respetando cupos).
- Canciones sugeridas; observaciones.

Campos condicionales solo cuando procedan.

### 9.2. Invitaciones individuales y familiares

- Una **Invitation** es la unidad de gestión (persona o grupo).
- `maxAttendees` limita asistentes confirmados.
- Distinguir siempre: **invitación ≠ invitado (Guest) ≠ asistente confirmado**.
- Una sola respuesta RSVP **por invitación**.

### 9.3. Unicidad y autorización (editToken)

1. El invitado abre el enlace con `inviteCode` (o lo introduce).
2. Envía el formulario; el backend asocia la respuesta a esa `Invitation`.
3. Se genera un `editToken` aleatorio largo; se guarda solo el **hash**; el token en claro se entrega una vez (enlace privado + opcional email).
4. Para modificar: el enlace con token; el backend valida hash, invitación y **fecha límite RSVP**.
5. Nombre/email ayudan a localizar, **nunca** autorizan solos la edición.
6. Rate limit y protección anti-abuso en endpoints públicos.
7. Token revocable / caducable según política.

Casos límite: correo mal escrito (admin puede corregir con auditoría); duplicados evitados por unicidad de RSVP por `invitationId`; familias = N asistentes ≤ `maxAttendees`.

### 9.4. Confirmaciones y privacidad

- Confirmación clara al guardar.
- No exponer datos de otras invitaciones.
- Información de privacidad y consentimiento; política de retención.
- Alergias: datos sensibles → acceso admin restringido y exportaciones cuidadas.

------------------------------------------------------------------------

## 10. Menú, alergias y necesidades alimentarias

Recopilación vía RSVP + sección informativa. Admin consulta y exporta informe para catering. Recopilar solo lo necesario, con aviso de privacidad.

------------------------------------------------------------------------

## 11. Regalo de boda y número de cuenta

Campos en configuración (no hardcode en Angular):

- Titular, IBAN, texto de agradecimiento, botón copiar IBAN sin espacios.
- IBAN legible en UI; valor pendiente de proporcionar.

------------------------------------------------------------------------

## 12. Adorno navideño por invitación

Cada **Invitation** tiene un adorno asignado (catálogo configurable).

### 12.1. Tipos (ejemplos)

Estrella dorada, bola roja, campana, reno, muñeco de nieve, copo, regalo, ángel, bastón de caramelo.

### 12.2. Invitado

Consultar adorno e ilustración; instrucciones para llevarlo a la boda (vía código de invitación).

### 12.3. Admin

CRUD de tipos; asignación manual o aleatoria; evitar repeticiones si se configura; exportar listado.

### 12.4. Árbol virtual (post-MVP)

Árbol decorado con adornos asignados, sin exponer datos personales innecesarios.

------------------------------------------------------------------------

## 13. Galería colaborativa mediante QR

### 13.1. Acceso mediante QR

- QR → página de subida con **token de subida** (no URL abierta indefinida).
- Imprimible para mesas/carteles.
- Sin instalar app ni crear cuenta.

### 13.2. Subida

Seleccionar galería/cámara, múltiples imágenes, preview, progreso, reintentos, confirmación solo tras ACK del servidor. Desde escritorio: selector de archivos.

### 13.3. Moderación

Estados: `PENDING` | `APPROVED` | `REJECTED` | `PUBLISHED` | `DELETED`. Solo admin aprueba/rechaza/publica/borra. Invitados no eliminan lo ya enviado ni tocan fotos ajenas.

### 13.4. Publicación diferida (doble condición)

1. Fecha/hora de publicación alcanzada **y**  
2. Autorización expresa de un administrador.

Antes: sin URLs públicas, miniaturas ni metadatos para invitados. La fecha sola **no** publica. Fecha inicial pendiente (p. ej. 25/12/2027 es solo ejemplo).

### 13.5. Galería publicada

Mosaico, fullscreen, navegación, descarga individual; ZIP masivo opcional/post-MVP si el volumen lo permite; paginación y miniaturas.

### 13.6. Privacidad y seguridad de imágenes

- Almacenamiento privado (MinIO); URLs firmadas con caducidad.
- **Validación MIME real** del contenido (no solo extensión).
- Límites de tamaño y número; rate limit por token/IP.
- **Strip EXIF/GPS** (y metadatos sensibles) en el procesamiento.
- Política de retención y aviso de uso/publicación.

------------------------------------------------------------------------

## 14. Libro de firmas digital (post-MVP)

Mensajes/felicitaciones moderados por admin. Fuera del MVP.

------------------------------------------------------------------------

## 15. Panel privado de administración

Ruta: `/admin`. Autorización real en backend (no solo ocultar UI).

### 15.1. Autenticación

- Usuario/contraseña; hash robusto (p. ej. Argon2/bcrypt).
- Sesión en **cookie HttpOnly, Secure, SameSite** + **protección CSRF**.
- Recuperación de contraseña (cuando se habilite correo).
- Cierre de sesión; rate limit de login; auditoría de acciones relevantes.
- **MFA opcional más adelante** (no bloquea MVP).

### 15.2. Admin mínimo desde Fase 1

Desde los cimientos: login admin + edición mínima de `WeddingConfiguration` / textos / timeline / venue placeholders, para **evitar hardcode** de contenidos en Angular.

### 15.3. Dashboard (datos reales)

Invitaciones, previstos, confirmados, no asistentes, pendientes, adultos/niños, menús infantiles, alergias, autobús, canciones, fotos pendientes/aprobadas, estado de publicación.

### 15.4–15.7. Gestión

Invitados/invitaciones, fotos, publicación, configuración general (pareja, fecha, hacienda, IBAN, fechas límite, playlist licenciada, adornos, legales), exportaciones CSV/XLSX (catering, alergias, buses, canciones, adornos) tratadas como datos privados.

------------------------------------------------------------------------

## 16. Arquitectura técnica

### 16.1. Tecnologías

| Capa | Tecnología |
|------|------------|
| Frontend | Angular + TypeScript |
| Estilos | SCSS, Grid, Flexbox |
| Backend | Spring Boot, REST |
| DB | PostgreSQL + Flyway |
| Seguridad | Spring Security (sesión + CSRF) |
| Fotos | MinIO (S3 API); miniaturas WebP/AVIF |
| Proxy | Nginx |
| Local | Docker Compose: Postgres + MinIO (+ Mailhog opcional) |
| Prod | Mismo stack en VPS + TLS |

### 16.2. Arquitectura lógica

```text
                 INVITADOS / ADMIN
                         |
                  Navegador web
                         |
                  Angular + TypeScript
                         |
                      HTTPS
                         |
                 Nginx / Reverse Proxy
                         |
                 Spring Boot REST API
                         |
       +-----------------+------------------+
       |                 |                  |
   PostgreSQL         MinIO            Correo (opc.)
   Datos y RSVP       Fotografías      Mailhog local
```

Misma forma en local (`docker compose up`) y en VPS; cambian dominio, TLS y secretos.

### 16.3. Módulos backend

`auth`, `invitation`, `guest`, `rsvp`, `dietary`, `transport`, `music`, `ornament`, `photo`, `venue`, `timeline`, `configuration`, `admin`, `notification` (opc.); `guestbook` post-MVP.

------------------------------------------------------------------------

## 17. Modelo de datos inicial

| Entidad | Responsabilidad |
|---------|-----------------|
| `AdminUser` | Administradores |
| `Invitation` | Unidad de gestión; `inviteCode`, `maxAttendees`, adorno |
| `Guest` | Personas de la invitación |
| `Rsvp` | Respuesta; `editTokenHash`; caducidad por fecha límite |
| `DietaryRequirement` | Alergias / necesidades |
| `TransportReservation` | Autobús, horario, parada; respeta cupos |
| `SongSuggestion` | Canciones propuestas |
| `OrnamentType` / `GuestOrnament` | Catálogo y asignación |
| `Photo` | Metadatos, storage key, estado moderación |
| `PhotoPublicationConfig` | Fecha objetivo + flag autorización admin |
| `Venue` / `VenueImage` | Hacienda y fotos |
| `TimelineEvent` | Agenda |
| `WeddingConfiguration` | Config general |
| `PlaylistTrack` | Playlist licenciada/propia |
| `GuestbookMessage` | Post-MVP |
| `AdminAuditLog` | Auditoría |

------------------------------------------------------------------------

## 18. API REST orientativa

### Público

```http
GET    /api/public/wedding
GET    /api/public/venue
GET    /api/public/timeline
GET    /api/public/ornaments/{inviteCode}
POST   /api/public/rsvp                 # requiere inviteCode
GET    /api/public/rsvp/{editToken}
PUT    /api/public/rsvp/{editToken}
POST   /api/public/photos               # requiere upload token (+ rate limit)
GET    /api/public/photos               # solo si publicación efectiva
```

### Administración

```http
POST   /api/admin/auth/login
POST   /api/admin/auth/logout
GET    /api/admin/csrf                  # o cookie/header CSRF según convención Spring
GET    /api/admin/dashboard
CRUD   /api/admin/invitations
CRUD   /api/admin/photos (+ approve/reject)
GET/PUT /api/admin/publication
CRUD   /api/admin/timeline
CRUD   /api/admin/configuration
GET    /api/admin/exports/*
```

DTOs, validación, paginación, errores coherentes y permisos en todos los endpoints admin.

------------------------------------------------------------------------

## 19. Seguridad, privacidad y cumplimiento

- HTTPS en producción; Compose local puede usar HTTP en red interna.
- Autorización en backend; passwords hasheados.
- Tokens RSVP y de subida: aleatorios, hasheados en reposo cuando aplique.
- CSRF con cookies de sesión; CORS mínimo.
- Rate limiting (login, RSVP, upload).
- Fotos privadas + URLs firmadas; validación MIME; strip EXIF/GPS.
- Auditoría admin; backups DB + bucket.
- RGPD: minimización, acceso/rectificación/supresión, retención limitada, aviso de fotos.
- No exponer datos privados por IDs predecibles ni URLs permanentes de originales.

------------------------------------------------------------------------

## 20. Rendimiento y calidad

Priorizar LCP del hero; optimizar imágenes; lazy load; playlist bajo demanda; paginar galería; miniaturas; pruebas en iOS/Android reales. Objetivo WCAG 2.2 AA en flujos principales en la medida aplicable. Tests de reglas de negocio, permisos y flujo de publicación de fotos.

------------------------------------------------------------------------

## 21. Fases de desarrollo (reordenadas)

### Fase 0 — Cimientos

Monorepo, Docker Compose (Postgres + MinIO + Mailhog opc.), Nginx, healthchecks, Angular shell, Spring Boot + Security (cookie/CSRF), **admin/config mínimo**, Flyway inicial.

### Fase 1 — Web pública + countdown

Bienvenida saltables, identidad navideña, countdown `Europe/Madrid`, historia/lugar/timeline/IBAN **desde API**.

### Fase 2 — RSVP + adornos

Invitaciones, invite codes, editToken, alergias/bus/canciones, cupos bus cuando existan, exports básicos, asignación de adornos.

### Fase 3 — Fotos + QR

Upload token, MinIO, MIME real, EXIF strip, miniaturas, moderación, doble condición de publicación, galería.

### Fase 4 — Admin completo + hardening

Dashboard real, exports catering/bus, rate limits, backups, pruebas móviles, preparación VPS.

### Fase 5 — Post-MVP

PWA, libro de firmas, árbol virtual, MFA, ZIP masivo, migrar storage a R2/S3 si hace falta.

------------------------------------------------------------------------

## 22. Decisiones pendientes y datos necesarios

**No inventar:**

- Nombres en la invitación; hacienda (nombre, dirección, coords, fotos).
- Titular e IBAN; playlist y derechos.
- Fecha límite RSVP; fecha/hora publicación de fotos.
- Destino/paradas/capacidad de autobuses.
- Número de invitados y estructura de invitaciones.
- Catálogo definitivo de adornos; contacto incidencias; textos legales.

### Valores iniciales conocidos

| Configuración | Valor |
|---------------|-------|
| Fecha de la boda | 18/12/2027 |
| Zona horaria | Europe/Madrid |
| Ceremonia | 13:00 |
| Cóctel | 14:00 |
| Almuerzo | 15:30 |
| Barra libre | 17:00 |
| Primer autobús | 18:00 |
| Segundo autobús | 22:00 |
| Publicación de fotos | Pendiente |
| Hacienda / IBAN / nombres | Pendiente |

------------------------------------------------------------------------

## 23. Criterios de aceptación principales

1. Usable en móvil, tablet y escritorio.  
2. Acceso por QR/enlace con código de invitación sin instalar app.  
3. Intro saltables; música solo tras gesto; playlist licenciada/propia.  
4. Countdown correcto hasta 18/12/2027 (`Europe/Madrid`).  
5. Cómo llegar con dirección (cuando exista), fotos y mapas.  
6. Timeline con los seis hitos iniciales, editable.  
7. RSVP enviar/editar solo con inviteCode + editToken; una respuesta por invitación.  
8. Límites `maxAttendees` respetados; cupos bus cuando configurados.  
9. Export alergias/menús/buses.  
10. Adorno asignable por invitación.  
11. Subida de fotos con token, rate limit, MIME real, sin EXIF/GPS sensibles.  
12. Fotos inaccesibles hasta doble condición de publicación.  
13. Solo admin borra/modera fotos.  
14. Panel gestiona invitados, fotos, horarios y config; auth cookie+CSRF.  
15. Datos privados protegidos en backend.  
16. Rendimiento aceptable en móvil.

------------------------------------------------------------------------

## 24. Resultado esperado

Una aplicación web de boda navideña, elegante y responsive, con experiencia cuidada en móvil: invitación, información, RSVP seguro, adornos y galería colaborativa moderada. La pareja gestiona todo desde `/admin`. Desarrollo **local-first** con Docker; despliegue a VPS cuando se decida.

**La web debe sentirse como una invitación navideña personalizada y convertirse en el espacio digital de toda la celebración.**
