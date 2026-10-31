package com.rs2.tools.mapgen;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.function.Function;

import javax.imageio.ImageIO;

/**
 * Renders the quest weapons next to the cache items they are modelled on, at the same scale, and checks they
 * round-trip through ModelData.encode() / decode(). Rendering is Preview's software renderer (copied, generalised to
 * take a projection so the inventory icon can use the client's item camera).
 *
 * Usage: WeaponSheet <cacheDir> <outDir>
 */
public class WeaponSheet {

	/* ------------------------------------------------------------ Preview's renderer, generalised */

	static int rgb(int c, double shade) {
		return Preview.rgb(c, shade);
	}

	/** Draws m with proj(vertex) -> {screenX, screenY, depth}; painter's sort, back-face cull, Preview's light. */
	static void draw(Graphics2D g, ModelData m, Function<int[], double[]> proj, float alpha) {
		int n = m.vertices.size();
		double[][] P = new double[n][];
		for (int i = 0; i < n; i++) {
			P[i] = proj.apply(m.vertices.get(i));
		}
		Integer[] ord = new Integer[m.faces.size()];
		for (int i = 0; i < ord.length; i++) {
			ord[i] = i;
		}
		Arrays.sort(ord, (a, b) -> {
			int[] fa = m.faces.get(a), fb = m.faces.get(b);
			double za = P[fa[0]][2] + P[fa[1]][2] + P[fa[2]][2], zb = P[fb[0]][2] + P[fb[1]][2] + P[fb[2]][2];
			return Double.compare(zb, za);
		});
		double[] L = { -0.5, -0.7, -0.5 };
		double ll = Math.sqrt(1.24);
		for (int i = 0; i < 3; i++) {
			L[i] /= ll;
		}
		Composite old = g.getComposite();
		if (alpha < 1) {
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		}
		for (int fi : ord) {
			int[] f = m.faces.get(fi);
			double[] A = P[f[0]], B = P[f[1]], C = P[f[2]];
			double cross = (B[0] - A[0]) * (C[1] - A[1]) - (B[1] - A[1]) * (C[0] - A[0]);
			if (cross > 0) {
				continue; // back face
			}
			int[] a = m.vertices.get(f[0]), b = m.vertices.get(f[1]), c = m.vertices.get(f[2]);
			double ux = b[0] - a[0], uy = b[1] - a[1], uz = b[2] - a[2], vx = c[0] - a[0], vy = c[1] - a[1], vz = c[2] - a[2];
			double nx = uy * vz - uz * vy, ny = uz * vx - ux * vz, nz = ux * vy - uy * vx, nl = Math.sqrt(nx * nx + ny * ny + nz * nz) + 1e-9;
			double d = (nx * L[0] + ny * L[1] + nz * L[2]) / nl;
			double shade = 0.75 + 0.45 * d;
			g.setColor(new Color(rgb(m.colors.get(fi), shade)));
			Polygon p = new Polygon(new int[] { (int) Math.round(A[0]), (int) Math.round(B[0]), (int) Math.round(C[0]) },
					new int[] { (int) Math.round(A[1]), (int) Math.round(B[1]), (int) Math.round(C[1]) }, 3);
			g.fill(p);
			g.draw(p);
		}
		g.setComposite(old);
	}

	/** Preview's orthographic yaw/pitch camera. */
	static Function<int[], double[]> ortho(double ox, double oy, double scale, double yaw, double pitch) {
		double cy = Math.cos(yaw), sy = Math.sin(yaw), cp = Math.cos(pitch), sp = Math.sin(pitch);
		return v -> {
			double x = v[0] * cy - v[2] * sy, z = v[0] * sy + v[2] * cy, y = v[1];
			double y2 = y * cp - z * sp, z2 = y * sp + z * cp;
			return new double[] { ox + x * scale, oy + y2 * scale, z2 };
		};
	}

	/**
	 * The 317 client's inventory-sprite camera (ItemDef.getSprite -> Model.method482): yaw by rotation2 (op 6),
	 * offsets (op 7 / op 8), pitch by rotation1 (op 5) at distance zoom (op 4), perspective x/z << 9, centre 16,16.
	 */
	static Function<int[], double[]> itemCamera(int zoom, int rot1, int rot2, int off1, int off2, int modelHeight) {
		double p = rot1 * Math.PI * 2 / 2048, y = rot2 * Math.PI * 2 / 2048;
		double sp = Math.sin(p), cp = Math.cos(p), sy = Math.sin(y), cy = Math.cos(y);
		double yOff = sp * zoom + modelHeight / 2.0 + off2, zOff = cp * zoom + off2;
		return v -> {
			double x = v[0], yy = v[1], z = v[2];
			double x2 = z * sy + x * cy, z2 = z * cy - x * sy;
			x2 += off1;
			yy += yOff;
			z2 += zOff;
			double y3 = yy * cp - z2 * sp, z3 = yy * sp + z2 * cp;
			return new double[] { 16 + x2 * 512 / z3, 16 + y3 * 512 / z3, z3 };
		};
	}

	/** A 32x32 inventory sprite (with the client's black outline and drop shadow), blown up ×scale. */
	static BufferedImage icon(ModelData m, int[] def, int up) {
		BufferedImage s = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = s.createGraphics();
		int mh = -m.bounds()[1];
		draw(g, m, itemCamera(def[0], def[1], def[2], def[3], def[4], mh), 1f);
		g.dispose();
		// outline: transparent pixels next to drawn ones turn black; then a shadow down-right
		int[][] px = new int[32][32];
		for (int x = 0; x < 32; x++) {
			for (int y = 0; y < 32; y++) {
				px[x][y] = s.getRGB(x, y);
			}
		}
		BufferedImage o = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < 32; x++) {
			for (int y = 0; y < 32; y++) {
				int c = px[x][y];
				if ((c >>> 24) != 0) {
					o.setRGB(x, y, c);
					continue;
				}
				boolean edge = x > 0 && (px[x - 1][y] >>> 24) != 0 || x < 31 && (px[x + 1][y] >>> 24) != 0 || y > 0 && (px[x][y - 1] >>> 24) != 0
						|| y < 31 && (px[x][y + 1] >>> 24) != 0;
				boolean shadow = x > 0 && y > 0 && (px[x - 1][y - 1] >>> 24) != 0;
				if (edge) {
					o.setRGB(x, y, 0xFF010101);
				} else if (shadow) {
					o.setRGB(x, y, 0xFF302020);
				}
			}
		}
		BufferedImage big = new BufferedImage(32 * up, 32 * up, BufferedImage.TYPE_INT_RGB);
		Graphics2D bg = big.createGraphics();
		bg.setColor(new Color(62, 53, 41)); // the inventory's brown
		bg.fillRect(0, 0, 32 * up, 32 * up);
		bg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		bg.drawImage(o, 0, 0, 32 * up, 32 * up, null);
		bg.setColor(new Color(90, 80, 64));
		bg.drawRect(0, 0, 32 * up - 1, 32 * up - 1);
		bg.dispose();
		return big;
	}

	/* ------------------------------------------------------------ cache bits */

	static int[] itemCam(ConfigDefinitions.Definition d) {
		int[] r = new int[5];
		int[] ops = { 4, 5, 6, 7, 8 };
		int[] dflt = { 2000, 0, 0, 0, 0 };
		for (int i = 0; i < 5; i++) {
			byte[] p = d.get(ops[i]);
			r[i] = p == null ? dflt[i] : (short) ((p[0] & 0xFF) << 8 | p[1] & 0xFF);
			if (i < 3 && p != null) {
				r[i] &= 0xFFFF;
			}
		}
		return r;
	}

	/** The default male body (identity kits 0, 10, 18, 26, 33, 36, 42), for checking the weapon sits in the hand. */
	static ModelData body(ModelForge f) throws Exception {
		ModelData b = new ModelData();
		for (int id : new int[] { 230, 249, 292, 151, 176, 254, 181 }) {
			ModelData k = f.read(id);
			int base = b.vertices.size();
			for (int i = 0; i < k.vertices.size(); i++) {
				b.vertices.add(k.vertices.get(i).clone());
				b.vertexSkins.add(k.vertexSkins.get(i));
			}
			for (int i = 0; i < k.faces.size(); i++) {
				int[] fc = k.faces.get(i);
				b.faces.add(new int[] { fc[0] + base, fc[1] + base, fc[2] + base });
				int col = k.colors.get(i);
				b.colors.add(col < 100 ? ModelData.hsl(4, 3, 70) : col); // kit colours are recoloured in game; keep them readable
				b.renderTypes.add(0);
				b.priorities.add(0);
				b.alphas.add(0);
				b.faceSkins.add(0);
			}
		}
		return b;
	}

	static ModelData merge(ModelData a, ModelData b) {
		ModelData m = new ModelData();
		for (ModelData k : new ModelData[] { a, b }) {
			int base = m.vertices.size();
			for (int i = 0; i < k.vertices.size(); i++) {
				m.vertices.add(k.vertices.get(i).clone());
				m.vertexSkins.add(k.vertexSkins.get(i));
			}
			for (int i = 0; i < k.faces.size(); i++) {
				int[] fc = k.faces.get(i);
				m.faces.add(new int[] { fc[0] + base, fc[1] + base, fc[2] + base });
				m.colors.add(k.colors.get(i));
				m.renderTypes.add(k.renderTypes.get(i));
				m.priorities.add(0);
				m.alphas.add(0);
				m.faceSkins.add(0);
			}
		}
		return m;
	}

	static void label(Graphics2D g, String s, int x, int y, Color c) {
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		g.setColor(Color.BLACK);
		g.drawString(s, x + 1, y + 1);
		g.setColor(c);
		g.drawString(s, x, y);
	}

	/* ------------------------------------------------------------ main */

	public static void main(String[] args) throws Exception {
		File outDir = new File(args[1]);
		outDir.mkdirs();
		try (CacheEditor c = new CacheEditor(java.nio.file.Paths.get(args[0]))) {
			ModelForge f = new ModelForge(c);
			CacheEditor.Archive cfg = CacheEditor.Archive.decode(c.read(0, ConfigDefinitions.CONFIG_ARCHIVE));
			ConfigDefinitions.Table obj = ConfigDefinitions.load(cfg, "obj", false);
			ModelData body = body(f);

			QuestWeapons.Weapon[] ws = QuestWeapons.ALL;
			int cellW = 1500, rowH = 330, headH = 40;
			BufferedImage sheet = new BufferedImage(cellW, headH + rowH * ws.length, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = sheet.createGraphics();
			g.setColor(new Color(44, 48, 44));
			g.fillRect(0, 0, sheet.getWidth(), sheet.getHeight());
			label(g, "Void saga quest weapons  -  each next to the cache item it copies placement, scale and bone label from (white wire = original)", 12, 24,
					new Color(255, 230, 160));

			StringBuilder report = new StringBuilder();
			for (int r = 0; r < ws.length; r++) {
				QuestWeapons.Weapon w = ws[r];
				QuestWeapons.Pose p = w.pose;
				ModelData inv = w.inventoryModel(), wld = w.wieldedModel();
				// round trip
				ModelData inv2 = ModelData.decode(inv.encode()), wld2 = ModelData.decode(wld.encode());
				boolean ok = same(inv, inv2) && same(wld, wld2);
				Set<Integer> skins = new TreeSet<>(wld2.vertexSkins), cols = new TreeSet<>(wld.colors);
				ModelData oInv = f.read(p.baseInv), oWld = f.read(p.baseWield);
				if (p.baseWieldExtra >= 0) {
					oWld = merge(oWld, f.read(p.baseWieldExtra));
				}
				int[] cam = itemCam(obj.decode(p.baseItem));
				report.append(String.format("%-24s inv v=%d f=%d | wield v=%d f=%d | colours=%d | skins=%s | bytes inv=%d wield=%d | round-trip %s | grip s=%.0f/L=%.0f | base %s (%d: inv %d, wield %d, cam %s)%n",
						w.name, inv2.vertices.size(), inv2.faces.size(), wld2.vertices.size(), wld2.faces.size(), cols.size(), skins,
						inv.encode().length, wld.encode().length, ok ? "OK" : "MISMATCH", p.gripS(), p.length, p.baseName, p.baseItem,
						p.baseInv, p.baseWield, Arrays.toString(cam)));

				int y0 = headH + r * rowH;
				g.setColor(new Color(r % 2 == 0 ? 52 : 48, r % 2 == 0 ? 57 : 53, r % 2 == 0 ? 52 : 48));
				g.fillRect(0, y0, cellW, rowH);
				label(g, w.name, 12, y0 + 18, new Color(255, 230, 160));
				label(g, String.format("%d tris (wield) / %d (inv) - like %s, bone %d", wld.faces.size(), inv.faces.size(), p.baseName, p.skin), 12,
						y0 + 34, new Color(200, 200, 190));

				// 1. inventory icons (client item camera, 32x32 ×3), ours then the original
				BufferedImage myIcon = icon(inv, cam, 3), oIcon = icon(oInv, cam, 3);
				g.drawImage(myIcon, 12, y0 + 50, null);
				g.drawImage(oIcon, 12, y0 + 50 + 100 + 30, null);
				label(g, "icon", 12, y0 + 162, Color.WHITE);
				label(g, p.baseName, 12, y0 + 292, new Color(180, 180, 180));
				ImageIO.write(myIcon, "png", new File(outDir, slug(w.name) + "_icon.png"));

				// 2. inventory model from above: original | ours, same scale (scale chosen per row so small items are legible)
				double invScale = Math.min(2.4, 150.0 / Math.max(80, extent(oInv)));
				int iy = y0 + 185;
				draw(g, oInv, centred(oInv, 215, iy, invScale, 0, Math.PI / 2 - 0.0001), 1f);
				Function<int[], double[]> invOurs = centred(oInv, 395, iy, invScale, 0, Math.PI / 2 - 0.0001); // centred on the original's box
				draw(g, inv, invOurs, 1f);
				wire(g, oInv, invOurs, new Color(255, 255, 255, 70));
				label(g, "inventory model, top: original | ours (+ original's wire)", 125, y0 + 318, Color.WHITE);

				// 3. wielded, side view (the y-z plane the hand holds it in): original | ours, same scale, hand at the same spot
				double ws_ = Math.min(2.6, 215.0 / p.length);
				int sx = 500;
				Function<int[], double[]> s1 = shifted(ortho(0, 0, ws_, Math.PI / 2, 0.0001), sx + 125, y0 + 185),
						s2 = shifted(ortho(0, 0, ws_, Math.PI / 2, 0.0001), sx + 375, y0 + 185);
				draw(g, oWld, s1, 1f);
				draw(g, wld, s2, 1f);
				wire(g, oWld, s2, new Color(255, 255, 255, 70));
				for (Function<int[], double[]> s : List.of(s1, s2)) {
					double[] h = s.apply(new int[] { -30, -97, 0 });
					g.setColor(new Color(255, 80, 80));
					g.drawOval((int) h[0] - 4, (int) h[1] - 4, 8, 8);
				}
				label(g, String.format("wielded, side view (x%.1f; red ring = right hand, -30,-97,0)", ws_), sx + 10, y0 + 18, Color.WHITE);
				label(g, "original", sx + 10, y0 + 318, new Color(180, 180, 180));
				label(g, "ours (+ original's wire)", sx + 260, y0 + 318, new Color(180, 180, 180));

				// 4. wielded, two 3/4 views
				draw(g, wld, centred(wld, 1080, y0 + 120, ws_ * 0.8, 0.75, 0.35), 1f);
				draw(g, wld, centred(wld, 1080, y0 + 245, ws_ * 0.8, -2.3, 0.5), 1f);
				label(g, "3/4 front | 3/4 back", 1020, y0 + 318, Color.WHITE);

				// 5. on the default body
				ModelData onBody = merge(body, wld);
				draw(g, onBody, ortho(1370, y0 + 290, 0.98, 2.4, 0.15), 1f);
				label(g, "on the default body", 1310, y0 + 318, Color.WHITE);

				// per-model PNGs: 3 angles + icon
				BufferedImage single = new BufferedImage(900, 340, BufferedImage.TYPE_INT_RGB);
				Graphics2D sg = single.createGraphics();
				sg.setColor(new Color(44, 48, 44));
				sg.fillRect(0, 0, 900, 340);
				sg.drawImage(icon(inv, cam, 4), 10, 30, null);
				double vs = Math.min(3.0, 210 / p.length);
				draw(sg, wld, centred(wld, 330, 180, vs, Math.PI / 2, 0.0001), 1f);
				draw(sg, wld, centred(wld, 560, 180, vs, 0.75, 0.35), 1f);
				draw(sg, wld, centred(wld, 780, 180, vs, -2.3, 0.5), 1f);
				label(sg, w.name + "  (icon | side | 3/4 front | 3/4 back)", 10, 18, Color.WHITE);
				ImageIO.write(single, "png", new File(outDir, slug(w.name) + ".png"));

				// big close-ups for judging the look
				BufferedImage big = new BufferedImage(1000, 420, BufferedImage.TYPE_INT_RGB);
				Graphics2D bg = big.createGraphics();
				bg.setColor(new Color(44, 48, 44));
				bg.fillRect(0, 0, 1000, 420);
				double z = 360.0 / Math.max(1, p.length * 1.15);
				draw(bg, wld, centred(wld, 250, 210, z * 1.6, Math.PI / 2, 0.0001), 1f);
				draw(bg, wld, centred(wld, 750, 210, z * 1.6, 0.8, 0.4), 1f);
				ImageIO.write(big, "png", new File(outDir, slug(w.name) + "_big.png"));
			}
			ImageIO.write(sheet, "png", new File(outDir, "sheet.png"));
			System.out.print(report);
		}
	}

	/** Shifts a projection so the hand point (-30,-97,0) lands on screen at (dx, dy). */
	static Function<int[], double[]> shifted(Function<int[], double[]> p, double dx, double dy) {
		double[] h = p.apply(new int[] { -30, -97, 0 });
		return v -> {
			double[] q = p.apply(v);
			return new double[] { q[0] - h[0] + dx, q[1] - h[1] + dy, q[2] };
		};
	}

	/** An orthographic view centred on the model's bounding box. */
	static Function<int[], double[]> centred(ModelData m, double cx, double cy, double scale, double yaw, double pitch) {
		int[] b = m.bounds();
		int[] mid = { (b[0] + b[3]) / 2, (b[1] + b[4]) / 2, (b[2] + b[5]) / 2 };
		Function<int[], double[]> p = ortho(0, 0, scale, yaw, pitch);
		double[] c = p.apply(mid);
		return v -> {
			double[] q = p.apply(v);
			return new double[] { q[0] - c[0] + cx, q[1] - c[1] + cy, q[2] };
		};
	}

	/** Largest bounding-box side. */
	static int extent(ModelData m) {
		int[] b = m.bounds();
		return Math.max(b[3] - b[0], Math.max(b[4] - b[1], b[5] - b[2]));
	}

	/** Draws every edge of m as a thin line (for comparing shapes over another model). */
	static void wire(Graphics2D g, ModelData m, Function<int[], double[]> proj, Color c) {
		g.setColor(c);
		for (int[] f : m.faces) {
			for (int k = 0; k < 3; k++) {
				double[] a = proj.apply(m.vertices.get(f[k])), b = proj.apply(m.vertices.get(f[(k + 1) % 3]));
				g.drawLine((int) Math.round(a[0]), (int) Math.round(a[1]), (int) Math.round(b[0]), (int) Math.round(b[1]));
			}
		}
	}

	static boolean same(ModelData a, ModelData b) {
		if (a.vertices.size() != b.vertices.size() || a.faces.size() != b.faces.size()) {
			return false;
		}
		for (int i = 0; i < a.vertices.size(); i++) {
			if (!Arrays.equals(a.vertices.get(i), b.vertices.get(i)) || !a.vertexSkins.get(i).equals(b.vertexSkins.get(i))) {
				return false;
			}
		}
		for (int i = 0; i < a.faces.size(); i++) {
			if (!Arrays.equals(a.faces.get(i), b.faces.get(i)) || !a.colors.get(i).equals(b.colors.get(i))
					|| !a.renderTypes.get(i).equals(b.renderTypes.get(i))) {
				return false;
			}
		}
		return true;
	}

	static String slug(String s) {
		return s.toLowerCase().replaceAll("[^a-z0-9]+", "_").replaceAll("_$", "");
	}
}
