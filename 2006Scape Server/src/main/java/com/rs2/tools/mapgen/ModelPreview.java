package com.rs2.tools.mapgen;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import javax.imageio.ImageIO;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rs2.game.content.voidrealm.VoidArsenal;

/**
 * An offline look at the weapon ladder, without the game: flat-shaded, painter-sorted polygons with a back-face cull
 * and one directional light (the art drafts' renderer), plus the 317 client's inventory-sprite camera.
 *
 * <p>For each rung it writes {@code NN-name.png} (the inventory icon through the base item's camera, the inventory model,
 * and the wielded model on the default body seen from the side, with the right hand ringed in red at (-30, -97, 0)),
 * and {@code sheet.png}, a contact sheet of all of them.
 *
 * <p>Usage (from '2006Scape Server', reads the cache only): {@code ModelPreview data/cache outDir [S1|S2] [rank...]}. With
 * S1 or S2 it draws the starmetal or voidsteel set instead, as {@code S1-NN-name.png} and {@code S1-sheet.png}; with G,
 * the god tier, as {@code 31-name.png} to {@code 45-name.png} and {@code G-sheet.png} (ranks 1-15 select them); rank 0
 * asks for the sheet alongside the listed cards.
 */
public final class ModelPreview {

	private ModelPreview() {
	}

	static final Color BG = new Color(40, 44, 42), PANEL = new Color(50, 55, 52), GOLD = new Color(255, 226, 150),
			TEXT = new Color(220, 220, 210), DIM = new Color(160, 160, 150);

	/* ------------------------------------------------------------ rendering */

	/** HSL16 to RGB, with a shade factor on the lightness (as the client's palette, roughly). */
	static int rgb(int c, double shade) {
		double h = ((c >> 10) & 63) / 64.0 + 0.0078125, s = ((c >> 7) & 7) / 8.0 + 0.0625, l = Math.min(1, (c & 127) / 128.0 * shade);
		double q = l < 0.5 ? l * (1 + s) : l + s - l * s, p = 2 * l - q;
		double[] o = new double[3], hs = { h + 1 / 3.0, h, h - 1 / 3.0 };
		for (int i = 0; i < 3; i++) {
			double t = hs[i] < 0 ? hs[i] + 1 : hs[i] > 1 ? hs[i] - 1 : hs[i];
			o[i] = t < 1 / 6.0 ? p + (q - p) * 6 * t : t < 0.5 ? q : t < 2 / 3.0 ? p + (q - p) * (2 / 3.0 - t) * 6 : p;
		}
		return new Color((float) Math.min(1, o[0]), (float) Math.min(1, o[1]), (float) Math.min(1, o[2])).getRGB();
	}

	/** Draws m with proj(vertex) = {screenX, screenY, depth}: painter's sort, back faces culled, one light. */
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
		// painter's order by depth, with a face's draw priority pulling it forward a little: the client draws a model's
		// higher-priority faces (attached plates and emblems) over its long base faces, whose centroids can sort nearer
		double[] key = new double[ord.length];
		for (int i = 0; i < ord.length; i++) {
			int[] f = m.faces.get(i);
			int prio = m.priorities.size() > i ? m.priorities.get(i) : 0;
			key[i] = P[f[0]][2] + P[f[1]][2] + P[f[2]][2] - prio * 4.5;
		}
		Arrays.sort(ord, (a, b) -> Double.compare(key[b], key[a]));
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
			if ((B[0] - A[0]) * (C[1] - A[1]) - (B[1] - A[1]) * (C[0] - A[0]) > 0) {
				continue; // back face
			}
			int[] a = m.vertices.get(f[0]), b = m.vertices.get(f[1]), c = m.vertices.get(f[2]);
			double ux = b[0] - a[0], uy = b[1] - a[1], uz = b[2] - a[2], vx = c[0] - a[0], vy = c[1] - a[1], vz = c[2] - a[2];
			double nx = uy * vz - uz * vy, ny = uz * vx - ux * vz, nz = ux * vy - uy * vx, nl = Math.sqrt(nx * nx + ny * ny + nz * nz) + 1e-9;
			double shade = 0.75 + 0.45 * (nx * L[0] + ny * L[1] + nz * L[2]) / nl;
			g.setColor(new Color(rgb(m.colors.get(fi), shade)));
			Polygon p = new Polygon(new int[] { (int) Math.round(A[0]), (int) Math.round(B[0]), (int) Math.round(C[0]) },
					new int[] { (int) Math.round(A[1]), (int) Math.round(B[1]), (int) Math.round(C[1]) }, 3);
			g.fill(p);
			g.draw(p);
		}
		g.setComposite(old);
	}

	/** An orthographic yaw/pitch view. */
	static Function<int[], double[]> ortho(double scale, double yaw, double pitch) {
		double cy = Math.cos(yaw), sy = Math.sin(yaw), cp = Math.cos(pitch), sp = Math.sin(pitch);
		return v -> {
			double x = v[0] * cy - v[2] * sy, z = v[0] * sy + v[2] * cy, y = v[1];
			return new double[] { x * scale, (y * cp - z * sp) * scale, y * sp + z * cp };
		};
	}

	/** {@code p} shifted so the bounding-box centre of {@code m} lands at (cx, cy). */
	static Function<int[], double[]> centred(ModelData m, Function<int[], double[]> p, double cx, double cy) {
		double[] lo = { 1e9, 1e9 }, hi = { -1e9, -1e9 };
		for (int[] v : m.vertices) {
			double[] q = p.apply(v);
			for (int k = 0; k < 2; k++) {
				lo[k] = Math.min(lo[k], q[k]);
				hi[k] = Math.max(hi[k], q[k]);
			}
		}
		double dx = cx - (lo[0] + hi[0]) / 2, dy = cy - (lo[1] + hi[1]) / 2;
		return v -> {
			double[] q = p.apply(v);
			return new double[] { q[0] + dx, q[1] + dy, q[2] };
		};
	}

	/** The scale that fits m inside w x h under a unit-scale view. */
	static double fit(ModelData m, double yaw, double pitch, double w, double h) {
		Function<int[], double[]> p = ortho(1, yaw, pitch);
		double[] lo = { 1e9, 1e9 }, hi = { -1e9, -1e9 };
		for (int[] v : m.vertices) {
			double[] q = p.apply(v);
			for (int k = 0; k < 2; k++) {
				lo[k] = Math.min(lo[k], q[k]);
				hi[k] = Math.max(hi[k], q[k]);
			}
		}
		return Math.min(w / Math.max(1, hi[0] - lo[0]), h / Math.max(1, hi[1] - lo[1]));
	}

	/**
	 * The 317 client's inventory-sprite camera (ItemDef.getSprite -> Model.method482): yaw by rotation2 (op 6), offsets
	 * (op 7 / op 8), pitch by rotation1 (op 5) at distance zoom (op 4), perspective x/z << 9, centre 16,16.
	 */
	static Function<int[], double[]> itemCamera(int zoom, int rot1, int rot2, int off1, int off2, int modelHeight) {
		double p = rot1 * Math.PI * 2 / 2048, y = rot2 * Math.PI * 2 / 2048;
		double sp = Math.sin(p), cp = Math.cos(p), sy = Math.sin(y), cy = Math.cos(y);
		double yOff = sp * zoom + modelHeight / 2.0 + off2, zOff = cp * zoom + off2;
		return v -> {
			double x2 = v[2] * sy + v[0] * cy + off1, z2 = v[2] * cy - v[0] * sy + zOff, yy = v[1] + yOff;
			double y3 = yy * cp - z2 * sp, z3 = yy * sp + z2 * cp;
			return new double[] { 16 + x2 * 512 / z3, 16 + y3 * 512 / z3, z3 };
		};
	}

	/** A 32x32 inventory sprite (black outline, drop shadow) on the inventory's brown, blown up x{@code up}. */
	static BufferedImage icon(ModelData m, int[] cam, int up) {
		BufferedImage s = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = s.createGraphics();
		draw(g, m, itemCamera(cam[0], cam[1], cam[2], cam[3], cam[4], -m.bounds()[1]), 1f);
		g.dispose();
		BufferedImage o = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < 32; x++) {
			for (int y = 0; y < 32; y++) {
				int c = s.getRGB(x, y);
				if ((c >>> 24) != 0) {
					o.setRGB(x, y, c);
				} else if (x > 0 && (s.getRGB(x - 1, y) >>> 24) != 0 || x < 31 && (s.getRGB(x + 1, y) >>> 24) != 0
						|| y > 0 && (s.getRGB(x, y - 1) >>> 24) != 0 || y < 31 && (s.getRGB(x, y + 1) >>> 24) != 0) {
					o.setRGB(x, y, 0xFF010101);
				} else if (x > 0 && y > 0 && (s.getRGB(x - 1, y - 1) >>> 24) != 0) {
					o.setRGB(x, y, 0xFF302020);
				}
			}
		}
		BufferedImage big = new BufferedImage(32 * up, 32 * up, BufferedImage.TYPE_INT_RGB);
		Graphics2D bg = big.createGraphics();
		bg.setColor(new Color(62, 53, 41));
		bg.fillRect(0, 0, 32 * up, 32 * up);
		bg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		bg.drawImage(o, 0, 0, 32 * up, 32 * up, null);
		bg.setColor(new Color(90, 80, 64));
		bg.drawRect(0, 0, 32 * up - 1, 32 * up - 1);
		bg.dispose();
		return big;
	}

	/* ------------------------------------------------------------ cache bits */

	/** The base item's camera: zoom, rotations, offsets (opcodes 4-8). */
	static int[] itemCam(ConfigDefinitions.Definition d) {
		int[] r = new int[5], ops = { 4, 5, 6, 7, 8 }, dflt = { 2000, 0, 0, 0, 0 };
		for (int i = 0; i < 5; i++) {
			byte[] p = d.get(ops[i]);
			r[i] = p == null ? dflt[i] : (short) ((p[0] & 0xFF) << 8 | p[1] & 0xFF);
			if (i < 3 && p != null) {
				r[i] &= 0xFFFF;
			}
		}
		return r;
	}

	/** The default male body (identity kits), to show the weapon in the hand. */
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
				b.colors.add(col < 100 ? ModelData.hsl(4, 3, 70) : col);
				b.renderTypes.add(0);
				b.priorities.add(0);
				b.alphas.add(0);
				b.faceSkins.add(0);
			}
		}
		return b;
	}

	/** Item bonuses from data/cfg/ItemDefinitions.json, as a short line ("+41 stab +41 slash ... +45 str"). */
	static Map<Integer, String> bonuses(File json) {
		Map<Integer, String> out = new LinkedHashMap<>();
		String[][] keys = { { "attackStab", "stab" }, { "attackSlash", "slash" }, { "attackCrush", "crush" }, { "attackMagic", "magic" },
				{ "attackRange", "ranged" }, { "strengthBonus", "str" }, { "prayerBonus", "prayer" }, { "defenceMagic", "mag def" } };
		try (FileReader r = new FileReader(json)) {
			JsonArray a = JsonParser.parseReader(r).getAsJsonArray();
			for (JsonElement e : a) {
				JsonObject o = e.getAsJsonObject();
				if (!o.has("bonuses")) {
					continue;
				}
				JsonObject b = o.getAsJsonObject("bonuses");
				StringBuilder sb = new StringBuilder();
				for (String[] k : keys) {
					if (b.has(k[0]) && b.get(k[0]).getAsInt() > 0) {
						sb.append(sb.length() > 0 ? "  " : "").append('+').append(b.get(k[0]).getAsInt()).append(' ').append(k[1]);
					}
				}
				out.put(o.get("id").getAsInt(), sb.toString());
			}
		} catch (Exception e) {
			System.out.println("no bonuses: " + e);
		}
		return out;
	}

	static void label(Graphics2D g, String s, int x, int y, Color c, int size, boolean bold) {
		g.setFont(new Font(Font.SANS_SERIF, bold ? Font.BOLD : Font.PLAIN, size));
		g.setColor(Color.BLACK);
		g.drawString(s, x + 1, y + 1);
		g.setColor(c);
		g.drawString(s, x, y);
	}

	/* ------------------------------------------------------------ the views */

	/** The inventory model, seen from above and a little in front (as it lies on the ground). */
	static void inventoryView(Graphics2D g, ModelData inv, int x, int y, int w, int h) {
		double yaw = 0.35, pitch = 1.1;
		double sc = Math.min(2.6, fit(inv, yaw, pitch, w, h));
		draw(g, inv, centred(inv, ortho(sc, yaw, pitch), x + w / 2.0, y + h / 2.0), 1f);
	}

	/** The wielded model on the body, from the side (the y-z plane the hand holds it in), hand ringed in red. */
	static void wieldedView(Graphics2D g, ModelData body, ModelData wld, int x, int y, int w, int h) {
		double yaw = Math.PI / 2 + 0.25, pitch = 0.12;
		ModelData both = new ModelData();
		// frame the weapon and the arm holding it (with the shoulder and hip for scale), not the whole body
		both.vertices.addAll(wld.vertices);
		both.vertices.add(new int[] { -30, -150, 0 });
		both.vertices.add(new int[] { -30, -60, 25 });
		java.awt.Shape clip = g.getClip();
		g.setClip(x, y, w, h);
		double sc = fit(both, yaw, pitch, w, h);
		Function<int[], double[]> p = centred(both, ortho(sc, yaw, pitch), x + w / 2.0, y + h / 2.0);
		draw(g, body, p, 0.35f);
		draw(g, wld, p, 1f);
		double[] hand = p.apply(new int[] { -30, -97, 0 });
		g.setColor(new Color(255, 80, 80));
		g.drawOval((int) hand[0] - 4, (int) hand[1] - 4, 8, 8);
		g.setClip(clip);
	}

	/** One rung's card: icon, inventory model, wielded on the body; name, type, level and stats. */
	static BufferedImage card(LadderWeapons.Rung r, String tag, ModelData body, int[] cam, String stats, ModelData baseInv) {
		BufferedImage img = new BufferedImage(600, 300, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(BG);
		g.fillRect(0, 0, 600, 300);
		ModelData inv = r.weapon.inventoryModel(), wld = r.weapon.wieldedModel();
		label(g, tag + "  " + r.name, 12, 24, GOLD, 17, true);
		label(g, r.type + "  -  " + requirement(r) + "  -  " + wld.faces.size() + " tris", 12, 44, TEXT, 12, false);
		label(g, stats == null ? "" : stats, 12, 62, DIM, 12, false);
		g.setColor(PANEL);
		g.fillRect(8, 72, 584, 220);
		g.drawImage(icon(inv, cam, 4), 16, 84, null);
		label(g, "icon", 16, 228, DIM, 11, false);
		if (baseInv != null) { // the base item's own icon through the same camera, to check the fit
			g.drawImage(icon(baseInv, cam, 2), 80, 222, null);
			label(g, "base", 48, 258, DIM, 11, false);
		}
		inventoryView(g, inv, 160, 80, 190, 200);
		label(g, "inventory", 160, 286, DIM, 11, false);
		wieldedView(g, body, wld, 360, 80, 226, 205);
		label(g, "wielded (red ring = hand)", 410, 286, DIM, 11, false);
		g.dispose();
		return img;
	}

	/** What wielding it takes (a draft set's weapon has no id, so it comes from the set). */
	static String requirement(LadderWeapons.Rung r) {
		for (int i = 0; i < DraftArmour.WEAPONS.length; i++) {
			if (DraftArmour.WEAPONS[i] == r) {
				return DraftArmour.weaponRequirement(i);
			}
		}
		return VoidArsenal.requirementFor(r.id);
	}

	public static void main(String[] args) throws Exception {
		File out = new File(args[1]);
		out.mkdirs();
		java.util.Set<Integer> only = new java.util.HashSet<>();
		String set = args.length > 2 && args[2].matches("S1|S2|G|K|D") ? args[2] : null;
		for (int i = set == null ? 2 : 3; i < args.length; i++) {
			only.add(Integer.parseInt(args[i]));
		}
		Map<Integer, String> stats = bonuses(new File("data/cfg/ItemDefinitions.json"));
		try (CacheEditor c = new CacheEditor(java.nio.file.Paths.get(args[0]))) {
			ModelForge f = new ModelForge(c);
			CacheEditor.Archive cfg = CacheEditor.Archive.decode(c.read(0, ConfigDefinitions.CONFIG_ARCHIVE));
			ConfigDefinitions.Table obj = ConfigDefinitions.load(cfg, "obj", false);
			ModelData body = body(f);
			boolean god = "G".equals(set), kit = "K".equals(set), draft = "D".equals(set);
			int[] kitSets = { 16, 19, 20, 21, 22 }; // the armour set each kit weapon belongs to (A16-W-..., next to the set's cards)
			LadderWeapons.Rung[] all = set == null ? LadderWeapons.LADDER
					: god ? GodWeapons.GOD : kit ? KitWeapons.KIT : draft ? DraftArmour.WEAPONS : set.equals("S1") ? WeaponSets.STARMETAL_SET : WeaponSets.VOIDSTEEL_SET;
			String prefix = set == null || god ? "" : set + "-";
			int first = god ? LadderWeapons.LADDER.length : 0; // the god tier carries on the ladder's numbering (31-45)
			String title = set == null ? "The weapon ladder"
					: kit ? "The kit-wearers' weapons" : draft ? "The draft sets' weapons (A25-A39, not in the game)" : god ? "The god tier (ladder 31-45)" : set.equals("S1") ? "The starmetal set (Attack 50)" : "The voidsteel set (Attack 70)";
			int cols = 5, cw = 360, ch = 300, rows = (all.length + cols - 1) / cols;
			BufferedImage sheet = new BufferedImage(cols * cw, 50 + rows * ch, BufferedImage.TYPE_INT_RGB);
			Graphics2D sg = sheet.createGraphics();
			sg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			sg.setColor(BG);
			sg.fillRect(0, 0, sheet.getWidth(), sheet.getHeight());
			label(sg, title + " - inventory model (left) and wielded on the body (right, red ring = hand)", 14, 32, GOLD, 20, true);
			for (int i = 0; i < all.length; i++) {
				LadderWeapons.Rung r = all[i];
				int rank = i + 1;
				int[] cam = itemCam(obj.decode(r.weapon.pose.baseItem));
				ModelData inv = r.weapon.inventoryModel(), wld = r.weapon.wieldedModel();
				// round trip through the cache format
				if (ModelData.decode(inv.encode()).faces.size() != inv.faces.size() || ModelData.decode(wld.encode()).faces.size() != wld.faces.size()) {
					System.out.println("ROUND TRIP FAILED: " + r.name);
				}
				System.out.printf("%02d %-28s wield %3d tris, inv %3d tris, %d colours, grip s=%.0f of %.0f%n", rank, r.name, wld.faces.size(),
						inv.faces.size(), new java.util.TreeSet<>(wld.colors).size(), r.weapon.pose.gripS(), r.weapon.pose.length);
				String tag = draft ? "A" + (DraftArmour.FIRST + i) + "-W" : kit ? "A" + kitSets[i] + "-W" : prefix + String.format("%02d", rank + first);
				if (only.isEmpty() || only.contains(rank)) {
					String slug = r.name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
					ImageIO.write(card(r, tag, body, cam, stats.get(r.id), f.read(r.weapon.pose.baseInv)), "png", new File(out, tag + "-" + slug + ".png"));
				}
				int x0 = (i % cols) * cw, y0 = 50 + (i / cols) * ch;
				sg.setColor((i % cols + i / cols) % 2 == 0 ? PANEL : new Color(46, 50, 48));
				sg.fillRect(x0, y0, cw, ch);
				label(sg, tag + " " + r.name, x0 + 8, y0 + 18, GOLD, 13, true);
				label(sg, r.type + " - " + requirement(r), x0 + 8, y0 + 34, TEXT, 11, false);
				sg.drawImage(icon(inv, cam, 2), x0 + 8, y0 + 42, null);
				inventoryView(sg, inv, x0 + 8, y0 + 110, 140, 180);
				wieldedView(sg, body, wld, x0 + 150, y0 + 40, 205, 255);
			}
			sg.dispose();
			if (only.isEmpty() || only.contains(0)) {
				ImageIO.write(sheet, "png", new File(out, (god ? "G-" : prefix) + "sheet.png"));
			}
		}
	}
}
