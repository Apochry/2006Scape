package com.rs2.tools.mapgen;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

/**
 * Builds every Void effect, checks the model round-trip, encodes the authored frame scripts into idx2 frame-group
 * files (and decodes them back with the client's algorithm), and renders:
 *   out/<key>_strip.png        authored script, one cell per frame
 *   out/<key>_reuse_strip.png  the same model driven by the EXISTING cache sequence (no new frames)
 *   out/<key>_views.png        static model, 3 angles
 *   out/<key>.timing           per-frame delays (for the GIF maker)
 * Args: cacheDir outDir
 */
public class FxMain {
	static final int CELL = 160;

	public static void main(String[] a) throws Exception {
		Path out = Paths.get(a[1]);
		Files.createDirectories(out);
		try (CacheEditor c = new CacheEditor(Paths.get(a[0]))) {
			CacheEditor.Archive cfg = CacheEditor.Archive.decode(c.read(0, ConfigDefinitions.CONFIG_ARCHIVE));
			List<AnimCache.Seq> seqs = AnimCache.seqs(cfg.get("seq.dat"));
			AnimCache.loadAllFrames(c);
			StringBuilder report = new StringBuilder();
			for (VoidEffects.Effect e : VoidEffects.all()) {
				ModelData m = e.model;
				// ---- round trip
				byte[] enc = m.encode();
				ModelData d = ModelData.decode(enc);
				boolean ok = same(m, d);
				// ---- authored frames -> idx2 file -> decoded with the client's algorithm
				byte[] frameFile = FxAnim.encode(e.skins, e.keys, 0);
				Files.write(out.resolve(e.key + ".frames.dat"), frameFile);
				List<AnimCache.Frame> mine = FxAnim.frames(e.skins, e.keys);
				// ---- reuse check: the existing sequence's skin list only references labels we use as intended
				AnimCache.Seq q = seqs.get(e.reuseSeq);
				AnimCache.Frame f0 = AnimCache.frames.get(q.frames[0]);
				Set<Integer> vl = new TreeSet<>(m.vertexSkins), fl = new TreeSet<>(m.faceSkins);
				report.append(String.format("%-16s v=%3d f=%3d bytes=%5d roundtrip=%s vskins=%s fskins=%s alpha=%s prio=%s | script: %d slots, %d frames, %d bytes | reuse seq %d (%d frames, file %d: %s)%n",
						e.key, m.vertices.size(), m.faces.size(), enc.length, ok ? "OK" : "MISMATCH", vl, fl, new TreeSet<>(m.alphas),
						new TreeSet<>(m.priorities), e.skins.types.length, mine.size(), frameFile.length, q.id, q.frameCount, f0.file,
						f0.base.describe()));
				// ---- strips
				strip(e, mine, e.keys.stream().mapToInt(k -> k.delay).toArray(), out.resolve(e.key + "_strip.png"), "authored script");
				int n = q.frameCount;
				int[] pick = pick(n, 12);
				List<AnimCache.Frame> rf = new ArrayList<>();
				int[] delays = new int[pick.length];
				for (int i = 0; i < pick.length; i++) {
					AnimCache.Frame fr = AnimCache.frames.get(q.frames[pick[i]]);
					rf.add(fr);
					int dsum = 0;
					int to = i + 1 < pick.length ? pick[i + 1] : n;
					for (int k = pick[i]; k < to; k++) dsum += q.delays[k] != 0 ? q.delays[k] : AnimCache.frames.get(q.frames[k]).delay;
					delays[i] = Math.max(1, dsum);
				}
				strip(e, rf, delays, out.resolve(e.key + "_reuse_strip.png"), "existing seq " + q.id + " (frames " + Arrays.toString(pick) + ")");
				views(e, out.resolve(e.key + "_views.png"));
			}
			Files.writeString(out.resolve("report.txt"), report.toString());
			System.out.print(report);
		}
	}

	static int[] pick(int n, int max) {
		if (n <= max) { int[] r = new int[n]; for (int i = 0; i < n; i++) r[i] = i; return r; }
		int[] r = new int[max];
		for (int i = 0; i < max; i++) r[i] = (int) Math.round(i * (n - 1) / (double) (max - 1));
		return r;
	}

	static ModelData posed(VoidEffects.Effect e, AnimCache.Frame f) {
		return FxRender.resize(FxAnim.pose(e.model, f), e.resizeXY, e.resizeZ);
	}

	static void strip(VoidEffects.Effect e, List<AnimCache.Frame> frames, int[] delays, Path file, String caption) throws IOException {
		int n = frames.size();
		BufferedImage img = new BufferedImage(CELL * n, CELL + 16, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setColor(new Color(20, 20, 24));
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		g.dispose();
		double bright = 1 + e.brightness / 400.0;
		for (int i = 0; i < n; i++) {
			int ox = i * CELL + CELL / 2;
			FxRender.ground(img, ox, e.oy, e.scale, e.yaw, e.pitch, CELL - 2, CELL, i * CELL + 1, 0);
			FxRender.draw(img, posed(e, frames.get(i)), ox, e.oy, e.scale, e.yaw, e.pitch, bright, new Rectangle(i * CELL + 1, 0, CELL - 2, CELL));
			FxRender.label(img, "f" + i + " d" + delays[i], i * CELL + 4, 12, new Color(200, 200, 200));
		}
		FxRender.label(img, e.name + " - " + caption, 4, CELL + 12, new Color(255, 214, 120));
		javax.imageio.ImageIO.write(img, "png", file.toFile());
		StringBuilder t = new StringBuilder();
		for (int d : delays) t.append(d).append(' ');
		Files.writeString(Paths.get(file.toString().replace(".png", ".timing")), t.toString().trim());
	}

	static void views(VoidEffects.Effect e, Path file) throws IOException {
		double[][] cams = { { e.yaw, e.pitch }, { e.yaw + 2.2, 0.15 }, { 0.0, 1.45 } };
		String[] names = { "in-game", "side", "top" };
		BufferedImage img = new BufferedImage(CELL * 3, CELL + 16, BufferedImage.TYPE_INT_RGB);
		double bright = 1 + e.brightness / 400.0;
		for (int i = 0; i < 3; i++) {
			int ox = i * CELL + CELL / 2;
			FxRender.ground(img, ox, e.oy, e.scale, cams[i][0], cams[i][1], CELL - 2, CELL, i * CELL + 1, 0);
			ModelData m = FxRender.resize(FxAnim.copy(e.model), e.resizeXY, e.resizeZ);
			FxRender.draw(img, m, ox, e.oy, e.scale, cams[i][0], cams[i][1], bright, new Rectangle(i * CELL + 1, 0, CELL - 2, CELL));
			FxRender.label(img, names[i], i * CELL + 4, 12, new Color(200, 200, 200));
		}
		FxRender.label(img, e.name + " (base pose)", 4, CELL + 12, new Color(255, 214, 120));
		javax.imageio.ImageIO.write(img, "png", file.toFile());
	}

	static boolean same(ModelData a, ModelData b) {
		if (a.vertices.size() != b.vertices.size() || a.faces.size() != b.faces.size()) return false;
		for (int i = 0; i < a.vertices.size(); i++) if (!Arrays.equals(a.vertices.get(i), b.vertices.get(i))) return false;
		for (int i = 0; i < a.faces.size(); i++) if (!Arrays.equals(a.faces.get(i), b.faces.get(i))) return false;
		return a.colors.equals(b.colors) && a.vertexSkins.equals(b.vertexSkins) && a.faceSkins.equals(b.faceSkins)
				&& a.alphas.equals(b.alphas) && a.priorities.equals(b.priorities) && a.renderTypes.equals(b.renderTypes);
	}
}
