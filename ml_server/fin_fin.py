import numpy as np
import matplotlib.pyplot as plt
from matplotlib.path import Path as MplPath
from matplotlib.backends.backend_pdf import PdfPages
from matplotlib import font_manager as fm
from PIL import Image
from collections import Counter
import argparse
import os

plt.rcParams['font.family'] = 'Malgun Gothic'

mono_font_path = fm.findfont("DejaVu Sans Mono")
mono_font_prop = fm.FontProperties(fname=mono_font_path)

schp_parts = {
    "torso": (0, 128, 0),
    "left_arm": (128, 128, 0),
    "right_arm": (0, 0, 128)
}

deep_to_symbol = {
    (0, 255, 255): 'S',
    (0, 255, 0):   'P',
    (255, 0, 0):   'R',
    (255, 255, 0): 'A',
    (255, 20, 147): 'M'
}

symbol_patterns = {
    'S': np.array([["<", " ", "<"],
                   [" ", "<", " "],
                   ["<", " ", "<"]]),
    'P': np.array([["―", "―", "―"],
                   ["|",  "|",  "|"],
                   ["―", "―", "―"]]),
    'R': np.array([["|", "|", "|"],
                   ["/", "/", "/"],
                   ["|", "|", "|"]]),
    'A': np.array([[" ", " ", " "],
                   [" ", "O", " "],
                   [" ", " ", " "]]),
    'M': np.array([["―", "|", "―"],
                   ["|", "―", "|"],
                   ["―", "|", "―"]]),
    '.': np.array([[".", " ", "."],
                   [" ", ".", " "],
                   [".", " ", "."]]),
    'X': np.array([["X", "X", "X"],
                   ["X", "X", "X"],
                   ["X", "X", "X"]])
}

legend_items = [
    ("|", "겉뜨기"),
    ("―", "안뜨기"),
    ("≡", "원코 위 3코 교차뜨기"),
    ("/", "오른코 위 3코 교차뜨기 (오른코 1코 안뜨기)"),
    ("\\", "왼코 위 3코 교차뜨기 (왼코 1코 안뜨기)")
]

def create_mask_with_tolerance(image, target_rgb, tolerance=30):
    return np.all(np.abs(image - np.array(target_rgb)) <= tolerance, axis=-1)

def get_dominant_symbol(part_rgb, deep_img, schp_img):
    mask = create_mask_with_tolerance(schp_img, part_rgb)
    deep_colors = deep_img[mask]
    color_counts = Counter([tuple(color) for color in deep_colors])
    if not color_counts:
        return '.'
    dominant_color = color_counts.most_common(1)[0][0]
    def closest_color(color):
        return min(deep_to_symbol.keys(), key=lambda c: np.linalg.norm(np.array(color) - np.array(c)))
    return deep_to_symbol.get(closest_color(dominant_color), '.')

def auto_scale_outline(outline, target_grid=(200, 150), margin_ratio=0.94):
    rows, cols = target_grid
    ox, oy = outline[:, 0], outline[:, 1]
    w = ox.max() - ox.min()
    h = oy.max() - oy.min()
    scale = min((cols * margin_ratio) / w, (rows * margin_ratio) / h)
    center = outline.mean(axis=0)
    return ((outline - center) * scale + center)

def inside_outline(x, y, outline):
    return MplPath(outline).contains_point((x, y))

def plot_pattern_with_legend(grid_size, outline, pattern, fill_pattern=None, color='black'):
    rows, cols = grid_size
    fig, ax = plt.subplots(figsize=(8.3, 11.7), facecolor='white')
    ax.set_facecolor('white')
    for x in range(cols + 1):
        ax.plot([x, x], [0, rows], color='gray', linewidth=0.7)
    for y in range(rows + 1):
        ax.plot([0, cols], [y, y], color='gray', linewidth=0.7)
    offset_x = (cols - (outline[:, 0].max() - outline[:, 0].min())) // 2 - outline[:, 0].min()
    offset_y = (rows - (outline[:, 1].max() - outline[:, 1].min())) // 2 - outline[:, 1].min()
    shifted_outline = outline + np.array([offset_x, offset_y])
    ph, pw = pattern.shape
    filled = np.zeros((rows, cols), dtype=bool)
    for y in range(0, rows - ph + 1, ph):
        for x in range(0, cols - pw + 1, pw):
            inside_patch = all(
                inside_outline(x + dx + 0.5, y + dy + 0.5, shifted_outline)
                for dy in range(ph) for dx in range(pw)
            )
            if inside_patch:
                for dy in range(ph):
                    for dx in range(pw):
                        gx = x + dx
                        gy = y + dy
                        if 0 <= gx < cols and 0 <= gy < rows:
                            char = pattern[dy, dx]
                            ax.text(gx + 0.48, rows - gy - 0.48, char,
                                    fontsize=3, ha='center', va='center',
                                    fontproperties=mono_font_prop)
                            filled[gy, gx] = True
    if fill_pattern is not None:
        fh, fw = fill_pattern.shape
        for gy in range(rows):
            for gx in range(cols):
                if not filled[gy, gx] and inside_outline(gx + 0.5, gy + 0.5, shifted_outline):
                    dy = gy % fh
                    dx = gx % fw
                    char = fill_pattern[dy, dx]
                    ax.text(gx + 0.48, rows - gy - 0.48, char,
                            fontsize=3, ha='center', va='center',
                            fontproperties=mono_font_prop)
    closed_outline = np.vstack([shifted_outline, shifted_outline[0]])
    ax.plot(closed_outline[:, 0], rows - closed_outline[:, 1], color=color, linewidth=0.8)
    lx, ly = cols - 1, -10
    for i, (s, desc) in enumerate(legend_items):
        ax.text(lx, ly - i * 3, f"{s}  =  {desc}", fontsize=8, ha='right', va='top')
    ax.set_xlim(0, cols + 25)
    ax.set_ylim(-15, rows + 5)
    ax.axis("off")
    ax.set_aspect('equal')
    return fig

def main(deep_img_path, schp_img_path, front_template_path, sleeve_template_path, save_pdf_path):
    target_size = (256, 256)
    deep_img = np.array(Image.open(deep_img_path).convert("RGB").resize(target_size, Image.NEAREST))
    schp_img = np.array(Image.open(schp_img_path).convert("RGB").resize(target_size, Image.NEAREST))
    front_outline = np.load(front_template_path, allow_pickle=True)['outline']
    sleeve_outline = np.load(sleeve_template_path, allow_pickle=True)['outline']
    patterns_array = []
    for part in ['torso', 'left_arm', 'right_arm']:
        rgb = schp_parts[part]
        symbol = get_dominant_symbol(rgb, deep_img, schp_img)
        patterns_array.append(symbol_patterns[symbol])
    grid_size = (200, 150)
    front_scaled = auto_scale_outline(front_outline, grid_size)
    sleeve_scaled = auto_scale_outline(sleeve_outline, grid_size)
    fig_front = plot_pattern_with_legend(
        grid_size, front_scaled,
        patterns_array[0],
        fill_pattern=patterns_array[2]
    )
    fig_sleeve = plot_pattern_with_legend(
        grid_size, sleeve_scaled,
        patterns_array[1],
        fill_pattern=patterns_array[2]
    )
    with PdfPages(save_pdf_path) as pdf:
        pdf.savefig(fig_front)
        pdf.savefig(fig_sleeve)
    print(f"PDF 저장 완료: {save_pdf_path}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--deep", required=True, help="DeepLab 결과 이미지 경로")
    parser.add_argument("--schp", required=True, help="SCHP 결과 이미지 경로")
    parser.add_argument("--front", required=True, help="front_template.npz 경로")
    parser.add_argument("--sleeve", required=True, help="sleeve_template.npz 경로")
    parser.add_argument("--output", required=True, help="생성할 PDF 파일 경로")
    args = parser.parse_args()
    os.makedirs("uploads", exist_ok=True)
    main(
        deep_img_path=args.deep,
        schp_img_path=args.schp,
        front_template_path=args.front,
        sleeve_template_path=args.sleeve,
        save_pdf_path=args.output
    )