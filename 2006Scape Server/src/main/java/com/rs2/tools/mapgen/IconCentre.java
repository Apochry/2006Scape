package com.rs2.tools.mapgen;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Centres the inventory icon of every item we add. The icon is not a picture in the cache: the client renders the
 * item's model into a 32x32 sprite through a camera stored in the definition (ItemDef.getSprite -> Model.method482):
 * zoom (op 4), pitch (op 5), yaw (op 6), roll (op 95) and two offsets (op 7 sideways, op 8 along the view). Our items
 * borrow a stock item's camera and then get a different model - longer, wider, a hilt moved, a shield with a boss -
 * so the borrowed camera puts the new model off to one side, or past the edge. In the bag that reads as careless.
 *
 * <p>This runs last in {@link CursedIslandGenerator#buildDefinitions}, over every item past the stock table, so new
 * items are centred without anyone having to remember. It projects each model exactly as the client does, takes the
 * screen box of its front faces, and moves the offsets until that box sits in the middle of the sprite; if the box is
 * too big to leave a pixel of outline either side, it first pulls the camera back. Rotations are never touched: which
 * way an item faces in the bag is a choice, where it sits isn't.
 */
final class IconCentre {

	/** The drawn pixels (a pixel is drawn when the model covers its middle) must stay within 1..30, leaving column and row 0 and 31 for the client's black outline. */
	static final double LO = 1.1, HI = 30.9;
	/** How far from the middle a box may sit before we move it (the offsets move it in quarter-pixel steps). */
	static final double TOLERANCE = 0.35;

	private IconCentre() {
	}

	/** One icon's camera, as the client reads it. */
	static final class Cam {
		int zoom = 2000, pitch, yaw, roll, off1, off2;

		static Cam of(ConfigDefinitions.Definition d) {
			Cam c = new Cam();
			c.zoom = u16(d, 4, 2000);
			c.pitch = u16(d, 5, 0);
			c.yaw = u16(d, 6, 0);
			c.roll = u16(d, 95, 0);
			c.off1 = (short) u16(d, 7, 0);
			c.off2 = (short) u16(d, 8, 0);
			return c;
		}

		Cam copy() {
			Cam c = new Cam();
			c.zoom = zoom;
			c.pitch = pitch;
			c.yaw = yaw;
			c.roll = roll;
			c.off1 = off1;
			c.off2 = off2;
			return c;
		}

		/** The {zoom, rot1, rot2, off1, off2} array {@link ModelPreview#icon} takes. */
		int[] preview() {
			return new int[] { zoom, pitch, yaw, off1, off2 };
		}
	}

	static int u16(ConfigDefinitions.Definition d, int op, int dflt) {
		byte[] p = d.get(op);
		return p == null ? dflt : (p[0] & 0xFF) << 8 | p[1] & 0xFF;
	}

	/** The model as the client builds it for the icon: decoded, then resized by ops 110-112. */
	static ModelData model(CacheEditor cache, ConfigDefinitions.Definition d) {
		int id = d.itemModel();
		if (id < 0) {
			return null;
		}
		try {
			byte[] raw = cache.read(ConfigDefinitions.MODEL_INDEX, id);
			if (raw == null) {
				return null;
			}
			ModelData m = ModelData.decode(org.apollo.util.CompressionUtil.degzip(ByteBuffer.wrap(raw)));
			int sx = u16(d, 110, 128), sy = u16(d, 111, 128), sz = u16(d, 112, 128);
			if (sx != 128 || sy != 128 || sz != 128) {
				for (int[] v : m.vertices) {
					v[0] = v[0] * sx / 128;
					v[1] = v[1] * sy / 128;
					v[2] = v[2] * sz / 128;
				}
			}
			return m;
		} catch (Exception e) {
			return null;
		}
	}

	/** Model.method482 with the sprite's centre at 16,16: returns {screen x, screen y, depth}. */
	static double[] project(int[] v, Cam c, int height) {
		double a = c.pitch * Math.PI / 1024, b = c.yaw * Math.PI / 1024, r = c.roll * Math.PI / 1024;
		double ty = Math.sin(a) * c.zoom + height / 2.0 + c.off2, tz = Math.cos(a) * c.zoom + c.off2;
		double x = v[0], y = v[1], z = v[2];
		if (c.roll != 0) {
			double x2 = y * Math.sin(r) + x * Math.cos(r);
			y = y * Math.cos(r) - x * Math.sin(r);
			x = x2;
		}
		double x3 = z * Math.sin(b) + x * Math.cos(b);
		z = z * Math.cos(b) - x * Math.sin(b);
		x = x3 + c.off1;
		y += ty;
		z += tz;
		double y4 = y * Math.cos(a) - z * Math.sin(a), z4 = y * Math.sin(a) + z * Math.cos(a);
		return new double[] { 16 + x * 512 / z4, 16 + y4 * 512 / z4, z4 };
	}

	/** The screen box {x0, y0, x1, y1} of the model's front faces through the camera, or null if nothing faces it. */
	static double[] box(ModelData m, Cam c) {
		int h = 0;
		for (int[] v : m.vertices) {
			h = Math.max(h, -v[1]); // Model.method466: modelHeight is the highest point above y = 0
		}
		double[][] p = new double[m.vertices.size()][];
		for (int i = 0; i < p.length; i++) {
			p[i] = project(m.vertices.get(i), c, h);
		}
		double[] b = { 1e9, 1e9, -1e9, -1e9 };
		boolean any = false;
		for (int[] f : m.faces) {
			double[] A = p[f[0]], B = p[f[1]], C = p[f[2]];
			if (A[2] <= 0 || B[2] <= 0 || C[2] <= 0
					|| (B[0] - A[0]) * (C[1] - A[1]) - (B[1] - A[1]) * (C[0] - A[0]) > 0) {
				continue; // behind the camera, or facing away (the client culls those)
			}
			for (double[] q : new double[][] { A, B, C }) {
				b[0] = Math.min(b[0], q[0]);
				b[1] = Math.min(b[1], q[1]);
				b[2] = Math.max(b[2], q[0]);
				b[3] = Math.max(b[3], q[1]);
			}
			any = true;
		}
		return any ? b : null;
	}

	/** How far the box's middle is from the sprite's, {dx, dy}. */
	static double[] miss(double[] b) {
		return new double[] { (b[0] + b[2]) / 2 - 16, (b[1] + b[3]) / 2 - 16 };
	}

	/** True if the box leaves the outline its pixel on every side. */
	static boolean fits(double[] b) {
		return b[0] >= LO && b[1] >= LO && b[2] <= HI && b[3] <= HI;
	}

	/**
	 * The centred camera for a model: pulled back only as far as it must be, then slid to the middle.
	 *
	 * <p>Sideways is easy: op 7 slides the model across the screen at any pitch. Up and down is not: op 8 is added to
	 * the model's height and its depth alike, so it moves the picture by (cos - sin) of the pitch - nothing at all at
	 * 45 degrees (pitch 256, every book and scroll), where it only pushes the model away and shrinks it. So op 8 is used
	 * only as far as it moves the picture without changing its size by more than a few percent; past that the item
	 * keeps its stock framing, which is how the client's own items at that pitch sit.
	 */
	static Cam centre(ModelData m, Cam base) {
		Cam c = base.copy();
		int depthBudget = base.zoom / 25; // op 8 may change the depth by 4% at most
		for (int pass = 0; pass < 40; pass++) {
			double[] b = box(m, c);
			if (b == null) {
				return base;
			}
			// too big for the sprite at any offset: back the camera off by the overshoot, plus a little
			double over = Math.max((b[2] - b[0]) / (HI - LO), (b[3] - b[1]) / (HI - LO));
			if (over > 1) {
				c.zoom = (int) Math.ceil(c.zoom * over * 1.01) + 2;
				continue;
			}
			double[] e = miss(b);
			boolean moved = false;
			if (Math.abs(e[0]) > TOLERANCE) { // sideways, with the slope measured rather than assumed
				Cam t = c.copy();
				int step = Math.max(4, c.zoom / 100);
				t.off1 += step;
				double[] bt = box(m, t);
				double slope = bt == null ? 0 : (miss(bt)[0] - e[0]) / step;
				if (Math.abs(slope) > 1e-6) {
					int d = (int) Math.round(-e[0] / slope);
					if (d != 0) {
						c.off1 += d;
						moved = true;
					}
				}
			}
			if (Math.abs(e[1]) > TOLERANCE) { // up and down, as far as op 8 can without resizing the picture
				Cam t = c.copy();
				int step = Math.max(4, c.zoom / 100);
				t.off2 += step;
				double[] bt = box(m, t);
				double slope = bt == null ? 0 : (miss(bt)[1] - e[1]) / step;
				if (Math.abs(slope) > 0.02) { // under 1 pixel per 50 units, op 8 is mostly zoom
					int d = (int) Math.round(-e[1] / slope);
					int lo = base.off2 - depthBudget, hi = base.off2 + depthBudget;
					int to = Math.max(lo, Math.min(hi, c.off2 + d));
					if (to != c.off2) {
						c.off2 = to;
						moved = true;
					}
				}
			}
			if (!moved) {
				if (fits(b)) {
					return c;
				}
				c.zoom = (int) (c.zoom * 1.02) + 2; // as centred as it gets but touching an edge: give it room
			}
		}
		return c;
	}

	/** What happened to one item, for the report and the preview sheet. */
	static final class Result {
		final int id;
		final String name;
		final ModelData model;
		final Cam before, after;
		final double missBefore;

		Result(int id, String name, ModelData model, Cam before, Cam after, double missBefore) {
			this.id = id;
			this.name = name;
			this.model = model;
			this.before = before;
			this.after = after;
			this.missBefore = missBefore;
		}

		boolean moved() {
			return before.zoom != after.zoom || before.off1 != after.off1 || before.off2 != after.off2;
		}
	}

	/** Centres every item from {@code firstId} on, writing the new camera back into the table. */
	static List<Result> centreAll(CacheEditor cache, ConfigDefinitions.Table items, int firstId) {
		List<Result> out = new ArrayList<>();
		for (int id = firstId; id < items.size(); id++) {
			ConfigDefinitions.Definition d = items.decode(id);
			if (d.get(97) != null || d.get(98) != null) {
				continue; // a banknote: drawn from its item's icon on the note template
			}
			ModelData m = model(cache, d);
			if (m == null) {
				continue;
			}
			Cam before = Cam.of(d), after = centre(m, before);
			double[] b = box(m, before);
			double[] e = b == null ? new double[2] : miss(b);
			double missBefore = b == null ? 0 : Math.max(Math.hypot(e[0], e[1]), fits(b) ? 0 : 99);
			String name = d.text(2);
			out.add(new Result(id, name, m, before, after, missBefore));
			if (before.zoom != after.zoom) {
				d.shorts(4, after.zoom);
			}
			if (before.off1 != after.off1) {
				d.shorts(7, after.off1);
			}
			if (before.off2 != after.off2) {
				d.shorts(8, after.off2);
			}
			items.put(id, d);
		}
		long moved = out.stream().filter(Result::moved).count();
		for (Result r : out) { // what's still off: worth a look, though at pitch 256 op 8 can't lift or lower a model
			double[] bb = box(r.model, r.after), e = miss(bb);
			if (Math.abs(e[0]) > 0.75 || Math.abs(e[1]) > 0.75 || !fits(bb)) {
				System.out.printf("   icon %d %s still %.1f,%.1f px off centre (pitch %d)%n", r.id, r.name, e[0], e[1], r.after.pitch);
			}
		}
		long clipped = out.stream().filter(r -> r.missBefore >= 99).count();
		System.out.println("Icons: centred " + out.size() + " item icons (" + moved + " moved, " + clipped
				+ " had run off the sprite)");
		if (System.getProperty("icons") != null) {
			try {
				sheet(out, java.nio.file.Paths.get(System.getProperty("icons")));
			} catch (java.io.IOException ex) {
				throw new java.io.UncheckedIOException(ex);
			}
		}
		return out;
	}

	/**
	 * A before/after contact sheet of every icon that moved, worst first, each beside a crosshair through the sprite's
	 * middle ({@code -Dicons=dir}): {@code icons-moved.png}, and {@code icons-all.png} with every centred icon.
	 */
	static void sheet(List<Result> rs, java.nio.file.Path dir) throws java.io.IOException {
		java.nio.file.Files.createDirectories(dir);
		List<Result> moved = new ArrayList<>();
		for (Result r : rs) {
			if (r.moved()) {
				moved.add(r);
			}
		}
		moved.sort((a, b) -> Double.compare(b.missBefore, a.missBefore));
		int up = 3, cell = 32 * up, cols = 6, w = cols * (cell * 2 + 30), rows = Math.max(1, (moved.size() + cols - 1) / cols);
		java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(w, rows * (cell + 30) + 10,
				java.awt.image.BufferedImage.TYPE_INT_RGB);
		java.awt.Graphics2D g = img.createGraphics();
		g.setColor(new java.awt.Color(30, 27, 22));
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		g.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 10));
		for (int i = 0; i < moved.size(); i++) {
			Result r = moved.get(i);
			int x = (i % cols) * (cell * 2 + 30) + 10, y = (i / cols) * (cell + 30) + 10;
			g.drawImage(cross(ModelPreview.icon(r.model, r.before.preview(), up)), x, y, null);
			g.drawImage(cross(ModelPreview.icon(r.model, r.after.preview(), up)), x + cell + 4, y, null);
			g.setColor(new java.awt.Color(200, 190, 160));
			g.drawString(r.id + " " + (r.name == null ? "" : r.name), x, y + cell + 12);
		}
		g.dispose();
		javax.imageio.ImageIO.write(img, "png", dir.resolve("icons-moved.png").toFile());

		int up2 = 2, c2 = 32 * up2 + 4, cols2 = 20;
		java.awt.image.BufferedImage all = new java.awt.image.BufferedImage(cols2 * c2 + 8,
				((rs.size() + cols2 - 1) / cols2) * c2 + 8, java.awt.image.BufferedImage.TYPE_INT_RGB);
		java.awt.Graphics2D ga = all.createGraphics();
		ga.setColor(new java.awt.Color(30, 27, 22));
		ga.fillRect(0, 0, all.getWidth(), all.getHeight());
		for (int i = 0; i < rs.size(); i++) {
			Result r = rs.get(i);
			ga.drawImage(ModelPreview.icon(r.model, r.after.preview(), up2), 4 + (i % cols2) * c2, 4 + (i / cols2) * c2, null);
		}
		ga.dispose();
		javax.imageio.ImageIO.write(all, "png", dir.resolve("icons-all.png").toFile());
	}

	/** A faint crosshair through the sprite's middle, so off-centre reads at a glance. */
	static java.awt.image.BufferedImage cross(java.awt.image.BufferedImage icon) {
		java.awt.Graphics2D g = icon.createGraphics();
		g.setColor(new java.awt.Color(255, 80, 80, 90));
		int m = icon.getWidth() / 2;
		g.drawLine(m, 0, m, icon.getHeight());
		g.drawLine(0, m, icon.getWidth(), m);
		g.dispose();
		return icon;
	}
}
