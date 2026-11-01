from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
PUBLIC = ROOT / "frontend" / "public"
ASSETS = Path(
    r"C:\Users\pw-gdelacruz\.cursor\projects\c-Users-pw-gdelacruz-Documents-privado-Weddinglage\assets"
)

SRC_TREE = ASSETS / "arbol-acuarela-60bdc9a5-2540-4048-9817-b46605ab3722.jpg"
SRC_MIST = ASSETS / "muerdago-acuarela-9139e586-e103-42d6-b300-c8c2ffb61b5f.png"


def punch_white(img: Image.Image) -> Image.Image:
    img = img.convert("RGBA")
    px = img.load()
    w, h = img.size
    for y in range(h):
        for x in range(w):
            r, g, b, _a = px[x, y]
            if r >= 245 and g >= 245 and b >= 245:
                px[x, y] = (r, g, b, 0)
            elif r >= 230 and g >= 230 and b >= 230:
                whiteness = (r + g + b) / 3
                alpha = int(max(0, min(255, (255 - whiteness) * 8)))
                px[x, y] = (r, g, b, alpha)
    return img


def remove_parchment(img: Image.Image) -> Image.Image:
    img = img.convert("RGBA")
    px = img.load()
    w, h = img.size
    samples = []
    for sx, sy in (
        (8, 8),
        (w - 9, 8),
        (8, h - 9),
        (w - 9, h - 9),
        (w // 2, 12),
        (w // 2, h - 12),
    ):
        samples.append(px[sx, sy][:3])
    samples.sort(key=lambda c: sum(c), reverse=True)
    br, bg, bb = [sum(c[i] for c in samples[:3]) // 3 for i in range(3)]
    print("parchment sample", br, bg, bb)

    for y in range(h):
        for x in range(w):
            r, g, b, _a = px[x, y]
            dr, dg, db = r - br, g - bg, b - bb
            dist = (dr * dr + dg * dg + db * db) ** 0.5
            mx, mn = max(r, g, b), min(r, g, b)
            near_neutral = (mx - mn) < 28
            light = (r + g + b) / 3 > 210
            if dist < 28 or (near_neutral and light and dist < 55):
                px[x, y] = (r, g, b, 0)
            elif dist < 48 or (near_neutral and light and dist < 80):
                if dist < 48:
                    t = (dist - 28) / 20
                else:
                    t = (dist - 55) / 25
                alpha = int(max(0, min(255, t * 255)))
                px[x, y] = (r, g, b, alpha)

    bbox = img.getbbox()
    if bbox:
        pad = 12
        x0, y0, x1, y1 = bbox
        x0 = max(0, x0 - pad)
        y0 = max(0, y0 - pad)
        x1 = min(w, x1 + pad)
        y1 = min(h, y1 + pad)
        img = img.crop((x0, y0, x1, y1))
    return img


def main() -> None:
    mist = punch_white(Image.open(SRC_MIST))
    out_mist = PUBLIC / "muerdago-acuarela.png"
    mist.save(out_mist, "PNG")
    print("mistletoe", out_mist, out_mist.stat().st_size)

    tree = remove_parchment(Image.open(SRC_TREE))
    out_tree = PUBLIC / "arbol-acuarela.png"
    tree.save(out_tree, "PNG")
    print("tree", out_tree, out_tree.stat().st_size, tree.size)


if __name__ == "__main__":
    main()
