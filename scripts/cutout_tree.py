"""Remove parchment paper behind the watercolor tree (edge flood-fill only)."""

from __future__ import annotations

from collections import deque
from pathlib import Path

from PIL import Image, ImageFilter

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "archivos" / "arbol-acuarela.jpeg"
OUT = ROOT / "frontend" / "public" / "arbol-acuarela.png"


def is_paper(r: int, g: int, b: int) -> bool:
    avg = (r + g + b) / 3.0
    sat = max(r, g, b) - min(r, g, b)
    # Warm parchment / cream paper — not gift whites with pattern, not foliage.
    if avg < 168:
        return False
    if sat > 48:
        return False
    # Keep warm-but-chromatic gift ribbons / star gold-ish pixels
    if (r - b) > 55 and avg < 215:
        return False
    return True


def try_rembg(src: Path) -> Image.Image | None:
    try:
        from rembg import remove
    except Exception:
        return None
    data = src.read_bytes()
    out = remove(data)
    from io import BytesIO

    return Image.open(BytesIO(out)).convert("RGBA")


def flood_cutout(src: Path) -> Image.Image:
    img = Image.open(src).convert("RGBA")
    w, h = img.size
    px = img.load()
    visited = [[False] * w for _ in range(h)]
    q: deque[tuple[int, int]] = deque()

    def seed(x: int, y: int) -> None:
        r, g, b, _a = px[x, y]
        if is_paper(r, g, b) and not visited[y][x]:
            visited[y][x] = True
            q.append((x, y))

    for x in range(w):
        seed(x, 0)
        seed(x, h - 1)
    for y in range(h):
        seed(0, y)
        seed(w - 1, y)

    mask = Image.new("L", (w, h), 0)
    mp = mask.load()
    while q:
        x, y = q.popleft()
        mp[x, y] = 255
        for nx, ny in ((x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1)):
            if 0 <= nx < w and 0 <= ny < h and not visited[ny][nx]:
                r, g, b, _a = px[nx, ny]
                if is_paper(r, g, b):
                    visited[ny][nx] = True
                    q.append((nx, ny))

    # Slight expand so paper fringe disappears without eating gifts deep inside.
    mask = mask.filter(ImageFilter.MaxFilter(3))
    mp = mask.load()
    for y in range(h):
        for x in range(w):
            if mp[x, y] > 0:
                r, g, b, _a = px[x, y]
                px[x, y] = (r, g, b, 0)

    bbox = img.getbbox()
    if bbox:
        pad = 8
        x0, y0, x1, y1 = bbox
        img = img.crop(
            (max(0, x0 - pad), max(0, y0 - pad), min(w, x1 + pad), min(h, y1 + pad))
        )
    return img


def main() -> None:
    img = try_rembg(SRC)
    method = "rembg"
    if img is None:
        img = flood_cutout(SRC)
        method = "flood"
    img.save(OUT, "PNG")
    print(method, OUT, img.size, OUT.stat().st_size)


if __name__ == "__main__":
    main()
