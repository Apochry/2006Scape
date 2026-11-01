package com.rs2.tools.mapgen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.imageio.ImageIO;

/**
 * An offline look at a {@link CreatureRig} creature without the game (the {@link ShardbackPreview} made general):
 * every frame of every sequence posed exactly as the client poses it, from the encoded frame file, drawn flat-shaded
 * ({@link ModelPreview#draw}) from a fixed 3/4 camera over a tile grid with the model's shadow under it. It writes a
 * looping GIF per animation, a captioned showcase of them all, a contact sheet per animation, every frame as a PNG
 * and a card; and it measures every frame against the ground:
 * <ul>
 * <li><b>sink</b>: how far the lowest vertex is under the ground (y &gt; 0);</li>
 * <li><b>float</b>: for each part that should be resting on the ground in that frame (the creature says which), how
 * far its lowest point is above it - the worst part;</li>
 * <li><b>seams</b>: the most any edge between two bones has stretched or shrunk against the rest pose (a part flying
 * off or a joint folding through itself);</li>
 * <li>and whatever the creature measures itself (the Gravecoil: how far its body strays from its track).</li>
 * </ul>
 */
final class CreaturePreview {

	private CreaturePreview() {
	}

	/** Everything the preview needs to know about a creature. */
	static final class Spec {
		String name, examine, where, lore, ids, file;
		int combat;
		String[][] stats;
		ModelData base;
		CreatureRig rig;
		/** The sequences by name (idle, walk, attack, death, block), as compiled. */
		Map<String, List<FxAnim.Key>> seqs;
		/** Captions, and how many times each plays in the showcase (0 = left out). */
		Map<String, String> captions = new LinkedHashMap<>();
		Map<String, Integer> reps = new LinkedHashMap<>();
		/** Units per client cycle the ground scrolls under a sequence (the walk), so planted parts can be seen to stay. */
		Map<String, Double> speed = new LinkedHashMap<>();
		/** The groups that must touch the ground: (sequence, frame, frames) to name -> labels. */
		ContactRule contact;
		/** An extra per-frame measure (null for none): (sequence, frame, posed) to a value, with its name. */
		Measure extra;
		String extraName;
		/** How long the last frame of the death is held in the GIFs (hundredths of a second), or 0 for its own delay. */
		int deathHold;
	}

	interface ContactRule {
		Map<String, int[]> groups(String seq, int frame, int frames);
	}

	interface Measure {
		double of(String seq, int frame, ModelData posed);
	}

	static final int W = 320, H = 320;
	static final Color BG = new Color(52, 56, 54), GRID = new Color(62, 67, 64), SHADOW = new Color(38, 41, 40);
	static final double YAW = 0.68, PITCH = 0.42;

	/** The camera: a fixed 3/4 view, scaled and placed once per creature so that every frame of every sequence fits. */
	static final class Camera {
		double scale = 1, ox, oy;

		double[] project(double x, double y, double z) {
			double cy = Math.cos(YAW), sy = Math.sin(YAW), cp = Math.cos(PITCH), sp = Math.sin(PITCH);
			double px = x * cy - z * sy, pz = x * sy + z * cy;
			return new double[] { ox + px * scale, oy + (y * cp - pz * sp) * scale, y * sp + pz * cp };
		}

		Function<int[], double[]> fn() {
			return v -> project(v[0], v[1], v[2]);
		}

		/** Fits every model (and its shadow) into the frame, leaving room for a caption at the top. */
		static Camera fit(List<ModelData> all) {
			Camera c = new Camera();
			double[] lo = { 1e9, 1e9 }, hi = { -1e9, -1e9 };
			for (ModelData m : all) {
				for (int[] v : m.vertices) {
					for (double[] q : new double[][] { c.project(v[0], v[1], v[2]), c.project(v[0], 0, v[2]) }) {
						lo[0] = Math.min(lo[0], q[0]);
						lo[1] = Math.min(lo[1], q[1]);
						hi[0] = Math.max(hi[0], q[0]);
						hi[1] = Math.max(hi[1], q[1]);
					}
				}
			}
			double top = 34, margin = 12;
			c.scale = Math.min((W - 2 * margin) / (hi[0] - lo[0]), (H - top - margin) / (hi[1] - lo[1]));
			c.ox = W / 2.0 - (lo[0] + hi[0]) / 2 * c.scale;
			c.oy = top + (H - top - margin) / 2.0 - (lo[1] + hi[1]) / 2 * c.scale;
			return c;
		}
	}

	/** One frame: grid (scrolled by {@code groundShift} along z), shadow, model, caption. */
	static BufferedImage render(Camera cam, ModelData m, double groundShift, String caption) {
		BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setColor(BG);
		g.fillRect(0, 0, W, H);
		g.setColor(GRID);
		g.setStroke(new BasicStroke(1f));
		double shift = ((groundShift % 128) + 128) % 128;
		int reach = 6;
		for (int i = -reach; i <= reach; i++) {
			double[] a = cam.project(i * 128 - 64, 0, -reach * 128), b = cam.project(i * 128 - 64, 0, reach * 128);
			g.drawLine((int) a[0], (int) a[1], (int) b[0], (int) b[1]);
			double z = i * 128 - 64 + shift;
			double[] c = cam.project(-reach * 128, 0, z), d = cam.project(reach * 128, 0, z);
			g.drawLine((int) c[0], (int) c[1], (int) d[0], (int) d[1]);
		}
		g.setColor(SHADOW); // the model flattened onto the ground, straight down
		for (int[] f : m.faces) {
			int[] xs = new int[3], ys = new int[3];
			for (int k = 0; k < 3; k++) {
				int[] v = m.vertices.get(f[k]);
				double[] p = cam.project(v[0], 0, v[2]);
				xs[k] = (int) Math.round(p[0]);
				ys[k] = (int) Math.round(p[1]);
			}
			g.fillPolygon(new Polygon(xs, ys, 3));
		}
		ModelPreview.draw(g, m, cam.fn(), 1f);
		if (caption != null) {
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			ModelPreview.label(g, caption, 10, 22, ModelPreview.GOLD, 15, true);
		}
		g.dispose();
		return img;
	}

	/* ------------------------------------------------------------ checks */

	/** The ground check of one sequence: per frame and its worst. */
	static final class Ground {
		final String seq;
		int frames, cycles, checkedGroups, touchingFrames;
		double worstFloat, worstSink, stretchLo = 1, stretchHi = 1, worstExtra;
		final StringBuilder detail = new StringBuilder();

		Ground(String seq) {
			this.seq = seq;
		}

		String summary(String extraName) {
			return String.format("%-7s %2d frames %4d cycles (%.2f s)  rest-on-ground checks %3d in %2d frames: worst float %.0f, worst sink %.0f;"
					+ " seams %.2f-%.2f%s", seq, frames, cycles, cycles * 0.02, checkedGroups, touchingFrames, worstFloat, worstSink, stretchLo, stretchHi,
					extraName == null ? "" : String.format("; %s %.1f", extraName, worstExtra));
		}
	}

	static Ground check(Spec s, String seq, List<ModelData> posed, List<FxAnim.Key> keys) {
		Ground r = new Ground(seq);
		r.frames = posed.size();
		for (int i = 0; i < posed.size(); i++) {
			ModelData m = posed.get(i);
			r.cycles += keys.get(i).delay;
			int low = CreatureRig.lowest(m);
			double sink = Math.max(0, low);
			r.worstSink = Math.max(r.worstSink, sink);
			Map<String, int[]> groups = s.contact.groups(seq, i, posed.size());
			StringBuilder parts = new StringBuilder();
			double fl = 0;
			for (Map.Entry<String, int[]> e : groups.entrySet()) {
				int gl = CreatureRig.lowest(m, e.getValue());
				fl = Math.max(fl, -gl);
				parts.append(' ').append(e.getKey()).append('=').append(gl);
			}
			if (!groups.isEmpty()) {
				r.touchingFrames++;
				r.checkedGroups += groups.size();
			}
			r.worstFloat = Math.max(r.worstFloat, fl);
			double[] st = CreatureRig.seamStretch(s.base, m);
			r.stretchLo = Math.min(r.stretchLo, st[0]);
			r.stretchHi = Math.max(r.stretchHi, st[1]);
			double ex = s.extra == null ? 0 : s.extra.of(seq, i, m);
			r.worstExtra = Math.max(r.worstExtra, ex);
			r.detail.append(String.format("  %-6s %2d d=%-2d lowest=%3d sink=%.0f float=%.0f seams=%.2f-%.2f%s |%s%n", seq, i, keys.get(i).delay, low, sink, fl,
					st[0], st[1], s.extra == null ? "" : String.format(" %s=%.1f", s.extraName, ex), parts));
		}
		return r;
	}

	/* ------------------------------------------------------------ the card */

	static BufferedImage card(Spec s, Camera cam, ModelData still) {
		int cw = 760, ch = 440;
		BufferedImage img = new BufferedImage(cw, ch, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(ModelPreview.BG);
		g.fillRect(0, 0, cw, ch);
		g.setColor(ModelPreview.PANEL);
		g.fillRect(10, 10, 340, ch - 20);
		g.drawImage(render(cam, still, 0, null), 20, 56, null);
		ModelPreview.label(g, s.where, 22, 34, ModelPreview.DIM, 13, false);
		int x = 368, y = 44;
		ModelPreview.label(g, s.name, x, y, ModelPreview.GOLD, 30, true);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
		int nameW = g.getFontMetrics().stringWidth(s.name);
		ModelPreview.label(g, "(level-" + s.combat + ")", x + nameW + 14, y, new Color(255, 255, 0), 18, true);
		y += 24;
		ModelPreview.label(g, "\"" + s.examine + "\"", x, y, ModelPreview.TEXT, 11, false);
		y += 30;
		for (String[] st : s.stats) {
			ModelPreview.label(g, st[0], x, y, ModelPreview.DIM, 13, false);
			ModelPreview.label(g, st[1], x + 110, y, ModelPreview.TEXT, 13, true);
			y += 19;
		}
		y += 10;
		g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
		for (String line : ShardbackPreview.wrap(g, s.lore, cw - x - 18)) {
			g.setColor(Color.BLACK);
			g.drawString(line, x + 1, y + 1);
			g.setColor(ModelPreview.TEXT);
			g.drawString(line, x, y);
			y += 16;
		}
		y += 8;
		ModelPreview.label(g, s.ids, x, y, ModelPreview.DIM, 11, false);
		y += 15;
		long bones = still.vertexSkins.stream().distinct().count();
		ModelPreview.label(g, still.faces.size() + " triangles, " + still.vertices.size() + " vertices, " + bones + " bone groups", x, y, ModelPreview.DIM, 11,
				false);
		g.dispose();
		return img;
	}

	/** All frames of one animation at full size, five to a row, each numbered. */
	static void sheet(List<BufferedImage> imgs, String name, File out) throws IOException {
		int cols = Math.min(imgs.size(), 5), rows = (imgs.size() + cols - 1) / cols;
		BufferedImage sh = new BufferedImage(cols * W, rows * H, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = sh.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		for (int i = 0; i < imgs.size(); i++) {
			g.drawImage(imgs.get(i), (i % cols) * W, (i / cols) * H, null);
			ModelPreview.label(g, name + " " + i, (i % cols) * W + 8, (i / cols) * H + 18, ModelPreview.GOLD, 13, true);
		}
		g.setColor(BG.darker());
		for (int c = 1; c < cols; c++) {
			g.drawLine(c * W, 0, c * W, rows * H);
		}
		for (int r = 1; r < rows; r++) {
			g.drawLine(0, r * H, cols * W, r * H);
		}
		g.dispose();
		ImageIO.write(sh, "png", out);
	}

	/* ------------------------------------------------------------ the lot */

	/** Writes the GIFs, showcase, sheets, frames and card into {@code out}, and prints the ground checks. */
	static List<Ground> run(Spec s, File out) throws IOException {
		File framesDir = new File(out, "frames");
		framesDir.mkdirs();
		if (ModelData.decode(s.base.encode()).faces.size() != s.base.faces.size()) {
			throw new IllegalStateException("model round trip failed");
		}
		System.out.println(s.name + ": " + s.base.vertices.size() + " vertices, " + s.base.faces.size() + " triangles, bounds "
				+ java.util.Arrays.toString(s.base.bounds()));
		Map<String, List<ModelData>> posed = new LinkedHashMap<>();
		List<ModelData> everything = new ArrayList<>();
		s.seqs.forEach((name, keys) -> {
			List<ModelData> p = s.rig.posed(s.base, keys);
			posed.put(name, p);
			everything.addAll(p);
		});
		Camera cam = Camera.fit(everything);
		List<Ground> checks = new ArrayList<>();
		List<BufferedImage> show = new ArrayList<>();
		List<Integer> showDelays = new ArrayList<>();
		for (String name : s.seqs.keySet()) {
			List<FxAnim.Key> keys = s.seqs.get(name);
			List<ModelData> frames = posed.get(name);
			Ground gr = check(s, name, frames, keys);
			checks.add(gr);
			double speed = s.speed.getOrDefault(name, 0.0);
			List<BufferedImage> imgs = new ArrayList<>();
			List<Integer> delays = new ArrayList<>();
			int elapsed = 0;
			for (int i = 0; i < frames.size(); i++) {
				BufferedImage img = render(cam, frames.get(i), speed * elapsed, null);
				imgs.add(img);
				delays.add(keys.get(i).delay * 2); // client cycles are 20 ms; GIF delays are 10 ms
				ImageIO.write(img, "png", new File(framesDir, String.format("%s-%02d.png", name, i)));
				elapsed += keys.get(i).delay;
			}
			if (name.equals("death") && s.deathHold > 0) {
				delays.set(delays.size() - 1, s.deathHold);
			}
			ShardbackPreview.gif(imgs, delays, new File(out, name + ".gif"));
			sheet(imgs, name, new File(out, "sheet-" + name + ".png"));
			int reps = s.reps.getOrDefault(name, 1);
			int el = 0;
			for (int r = 0; r < reps; r++) {
				for (int i = 0; i < frames.size(); i++) {
					show.add(render(cam, frames.get(i), speed * el, s.captions.getOrDefault(name, name)));
					showDelays.add(delays.get(i));
					el += keys.get(i).delay;
				}
			}
		}
		ShardbackPreview.gif(show, showDelays, new File(out, "showcase.gif"));
		ModelData still = posed.values().iterator().next().get(0);
		ImageIO.write(card(s, Camera.fit(List.of(still)), still), "png", new File(out, s.file + "-card.png"));
		System.out.printf("camera: scale %.3f (a tile is %.0f px across)%n", cam.scale, 128 * cam.scale);
		for (Ground gr : checks) {
			System.out.print(gr.detail);
		}
		System.out.println("ground contact, per sequence:");
		for (Ground gr : checks) {
			System.out.println("  " + gr.summary(s.extraName));
		}
		System.out.println("wrote " + out);
		return checks;
	}
}
