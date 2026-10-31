/**
 * Small pixel helpers for the RuneLite-style overlays (orbs, XP counter),
 * drawing straight into whichever DrawingArea is active and respecting its clip.
 */
final class OverlayDraw {

	private OverlayDraw() {
	}

	private static int[] savedPixels;
	private static int savedWidth, savedHeight, savedTopX, savedTopY, savedBottomX, savedBottomY;

	/** Makes the whole finished frame the drawing area, remembering the previous one. */
	static void beginFrame(int[] framePixels, int width, int height) {
		savedPixels = DrawingArea.pixels;
		savedWidth = DrawingArea.width;
		savedHeight = DrawingArea.height;
		savedTopX = DrawingArea.topX;
		savedTopY = DrawingArea.topY;
		savedBottomX = DrawingArea.bottomX;
		savedBottomY = DrawingArea.bottomY;
		DrawingArea.initDrawingArea(height, width, framePixels);
	}

	/** Puts back the drawing area that was active before {@link #beginFrame}. */
	static void endFrame() {
		DrawingArea.pixels = savedPixels;
		DrawingArea.width = savedWidth;
		DrawingArea.height = savedHeight;
		DrawingArea.setDrawingArea(savedBottomY, savedTopX, savedBottomX, savedTopY);
	}

	/** Alpha-blends one pixel; alpha is 0 (invisible) to 256 (solid). */
	static void blend(int x, int y, int color, int alpha) {
		if (x < DrawingArea.topX || x >= DrawingArea.bottomX || y < DrawingArea.topY || y >= DrawingArea.bottomY) {
			return;
		}
		int i = x + y * DrawingArea.width;
		if (alpha >= 256) {
			DrawingArea.pixels[i] = color;
			return;
		}
		int bg = DrawingArea.pixels[i];
		int inv = 256 - alpha;
		int r = ((color >> 16 & 0xff) * alpha + (bg >> 16 & 0xff) * inv) >> 8;
		int g = ((color >> 8 & 0xff) * alpha + (bg >> 8 & 0xff) * inv) >> 8;
		int b = ((color & 0xff) * alpha + (bg & 0xff) * inv) >> 8;
		DrawingArea.pixels[i] = r << 16 | g << 8 | b;
	}

	/** A solid box with a 1px border and clipped corners, like RuneLite's orb value boxes. */
	static void box(int x, int y, int width, int height, int fill, int border) {
		for (int dy = 0; dy < height; dy++) {
			for (int dx = 0; dx < width; dx++) {
				boolean corner = (dx == 0 || dx == width - 1) && (dy == 0 || dy == height - 1);
				if (corner) {
					continue;
				}
				boolean edge = dx == 0 || dy == 0 || dx == width - 1 || dy == height - 1;
				blend(x + dx, y + dy, edge ? border : fill, 256);
			}
		}
	}

	/**
	 * Draws an orb: a dark rim with a metal ring, a liquid fill rising from the
	 * bottom to {@code fill} (0..1), and a darker shade of the colour above it.
	 * Every pixel is opaque so it can be redrawn over itself each frame.
	 */
	static void orb(int cx, int cy, int radius, double fill, int fillColor, boolean highlighted) {
		int outer2 = radius * radius;
		int ring = radius - 1;
		int ring2 = ring * ring;
		int inner = radius - 3;
		int inner2 = inner * inner;
		int fillTop = cy + inner - (int) Math.round(fill * inner * 2);
		int empty = darken(fillColor, 90);
		for (int dy = -radius; dy <= radius; dy++) {
			for (int dx = -radius; dx <= radius; dx++) {
				int d2 = dx * dx + dy * dy;
				if (d2 > outer2) {
					continue;
				}
				int y = cy + dy;
				int color;
				if (d2 >= ring2) {
					color = 0x000000;
				} else if (d2 >= inner2) {
					// Metal ring, lighter on the top-left
					color = highlighted ? 0xd8d8d8 : dx + dy < 0 ? 0x7a746c : 0x4a4640;
				} else if (y >= fillTop) {
					color = dx + dy < -inner ? lighten(fillColor, 45) : fillColor;
				} else {
					color = empty;
				}
				blend(cx + dx, y, color, 256);
			}
		}
	}

	/** A copy of {@code sprite} shrunk to fit in maxSize x maxSize, keeping transparency. */
	static Sprite shrink(Sprite sprite, int maxSize) {
		double scale = Math.min(1, maxSize / (double) Math.max(sprite.width, sprite.height));
		int w = Math.max(1, (int) Math.round(sprite.width * scale));
		int h = Math.max(1, (int) Math.round(sprite.height * scale));
		Sprite out = new Sprite(w, h);
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				// Average the source pixels covering this one; mostly-empty stays transparent
				int x0 = x * sprite.width / w, x1 = Math.max(x0 + 1, (x + 1) * sprite.width / w);
				int y0 = y * sprite.height / h, y1 = Math.max(y0 + 1, (y + 1) * sprite.height / h);
				int r = 0, g = 0, b = 0, n = 0, total = 0;
				for (int sy = y0; sy < y1; sy++) {
					for (int sx = x0; sx < x1; sx++) {
						total++;
						int c = sprite.pixels[sx + sy * sprite.width];
						if (c != 0) {
							r += c >> 16 & 0xff;
							g += c >> 8 & 0xff;
							b += c & 0xff;
							n++;
						}
					}
				}
				int c = n * 2 >= total && n > 0 ? (r / n) << 16 | (g / n) << 8 | (b / n) : 0;
				out.pixels[x + y * w] = c == 0 && n * 2 >= total ? 1 : c;
			}
		}
		out.trimWidth = w;
		out.trimHeight = h;
		return out;
	}

	static int darken(int c, int amount) {
		int r = Math.max(0, (c >> 16 & 0xff) * (256 - amount) >> 8);
		int g = Math.max(0, (c >> 8 & 0xff) * (256 - amount) >> 8);
		int b = Math.max(0, (c & 0xff) * (256 - amount) >> 8);
		return r << 16 | g << 8 | b;
	}

	static int lighten(int c, int amount) {
		int r = Math.min(255, (c >> 16 & 0xff) + amount);
		int g = Math.min(255, (c >> 8 & 0xff) + amount);
		int b = Math.min(255, (c & 0xff) + amount);
		return r << 16 | g << 8 | b;
	}
}
