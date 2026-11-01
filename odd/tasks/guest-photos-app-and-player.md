# Guest photos app + Spotify player

Objective: Independent native-feeling photo upload app via QR; landing gallery is view-only; restore prior music player wired to admin Spotify playlist URL with first-track autoplay.
Branch: local main / Compose
Delivery: ask-on-risk
Test: Compose rebuild + manual smoke (upload token URL, gallery view, player after Entrar)

## Specs

S1. "Lo de subir fotos debe de ser una parte independiente de este landing page, donde atraves de otro qr te permita subir fotos a una app web que se vea la interfaz como nativa en android o ios, pero realmente por debajo es parte del mismo proyecto"
S2. "esta galeria del lading page debe ir enfocada a ver despues esas fotos"
S3. "quiero el reproductor que habia antes en el landingpage pero conectado a esta playlist (configurable la url desde admin) y que se reproduzca la primera cancion al iniciar"

## Tasks

- [x] T1 S1 — route: inline — Photos share app `/fotos` (PWA-ish UI, same API) + admin QR link
- [x] T2 S2 — route: inline — Landing gallery view-only; no upload CTA on landing
- [x] T3 S3 — route: inline — Restore prior player UX + Spotify playlist URL + play first track on start

## Log

L1. User: Lo de subir fotos debe de ser una parte independiente… reproductor que habia antes… playlist… primera cancion al iniciar
L2. Implemented `/fotos` native-feeling share app + manifest; landing gallery view-only; music player restored with Spotify IFrame API (playlist URI / tracks when SPOTIFY_CLIENT_* set).
