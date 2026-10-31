package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ModelPreview.*;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.function.Function;

import javax.imageio.ImageIO;

import com.rs2.game.content.voidrealm.VoidArmoury;

/**
 * An offline look at the armour sets (ArmourSets), without the game: each piece worn on the default male and female
 * bodies, front and back, beside its inventory icon; and each whole set worn, front and back, on both. The bodies are
 * the identity-kit models; a worn piece hides the kits the client hides (a platebody or robe top hides the arms, a full
 * helm the head and jaw, a coif the head).
 *
 * <p>Usage (from '2006Scape Server'): {@code ArmourPreview data/cache outDir set [slot... | full]}, set 1-6, slots 0-5
 * (helm, body, legs, shield, boots, gloves). Files: {@code A1-01-oathsworn-helm.png} ... and {@code A1-full-oathsworn.png}.
 */
public final class ArmourPreview {

	private ArmourPreview() {
	}

	/** Identity-kit models: head, jaw, torso, arms, hands, legs, feet (-1 = none). */
	static final int[] MALE = { 230, 249, 292, 151, 176, 254, 181 }, FEMALE = { 403, -1, 456, 332, 353, 419, 358 };
	static final int[] KIT_COLOUR = { ModelData.hsl(4, 3, 70), ModelData.hsl(6, 3, 40), ModelData.hsl(30, 2, 46), ModelData.hsl(4, 3, 70),
			ModelData.hsl(4, 3, 70), ModelData.hsl(38, 2, 36), ModelData.hsl(6, 2, 20) };
	static final String[] SLOT_NAMES = { "helm", "body", "legs", "shield", "boots", "gloves" };

	static void append(ModelData into, ModelData k, int colour) {
		int base = into.vertices.size();
		for (int i = 0; i < k.vertices.size(); i++) {
			into.vertices.add(k.vertices.get(i).clone());
			into.vertexSkins.add(k.vertexSkins.get(i));
		}
		for (int i = 0; i < k.faces.size(); i++) {
			int[] fc = k.faces.get(i);
			into.faces.add(new int[] { fc[0] + base, fc[1] + base, fc[2] + base });
			into.colors.add(colour >= 0 ? colour : k.colors.get(i));
			into.renderTypes.add(0);
			into.priorities.add(colour >= 0 || k.priorities.size() <= i ? 0 : k.priorities.get(i)); // worn pieces keep theirs
			into.alphas.add(0);
			into.faceSkins.add(0);
		}
	}

	/** The body wearing the given pieces (slot -> worn models), hiding what they cover. */
	static ModelData dressed(ModelForge f, ArmourSets.Style st, ModelData[][] worn, boolean female) throws Exception {
		int[] kits = female ? FEMALE : MALE;
		boolean[] hide = new boolean[7];
		if (worn[ArmourSets.HELM] != null) {
			String n = st.names[ArmourSets.HELM].toLowerCase();
			// as ItemData's fullmask / fullhat name lists: a full helm hides head and jaw, a coif, hood or med helm the head
			hide[1] = n.endsWith("full helm") || n.endsWith("mask");
			hide[0] = hide[1] || n.endsWith("coif") || n.endsWith("hood") || n.endsWith("med helm");
		}
		if (worn[ArmourSets.BODY] != null) {
			String n = st.names[ArmourSets.BODY].toLowerCase();
			hide[2] = true;
			hide[3] = n.endsWith("platebody") || n.endsWith("top") || n.endsWith("shirt") || n.endsWith("tunic"); // ItemData's fullbody
		}
		hide[5] = worn[ArmourSets.LEGS] != null;
		hide[4] = worn[ArmourSets.GLOVES] != null;
		hide[6] = worn[ArmourSets.BOOTS] != null;
		ModelData b = new ModelData();
		for (int i = 0; i < kits.length; i++) {
			if (kits[i] >= 0 && !hide[i]) {
				append(b, f.read(kits[i]), KIT_COLOUR[i]);
			}
		}
		for (ModelData[] w : worn) {
			if (w != null) {
				for (ModelData m : w) {
					if (m != null) {
						append(b, m, -1);
					}
				}
			}
		}
		return b;
	}

	/** The body (front or back), fitted into w x h, drawn whole. */
	static void bodyView(Graphics2D g, ModelData m, boolean back, int x, int y, int w, int h, int[] region) {
		double yaw = back ? Math.PI + 0.3 : 0.3, pitch = 0.12;
		ModelData frame = new ModelData(); // frame a fixed region (by default head to feet, arms out), the same in every view
		int[] r = region == null ? new int[] { -40, -238, 40, 10 } : region;
		frame.vertices.add(new int[] { r[0], r[1], 0 });
		frame.vertices.add(new int[] { r[2], r[3], 0 });
		frame.vertices.add(new int[] { 0, r[1], -30 });
		frame.vertices.add(new int[] { 0, r[3], 30 });
		java.awt.Shape clip = g.getClip();
		g.setClip(x, y, w, h);
		double sc = fit(frame, yaw, pitch, w, h);
		Function<int[], double[]> p = centred(frame, ortho(sc, yaw, pitch), x + w / 2.0, y + h / 2.0);
		draw(g, m, p, 1f);
		g.setClip(clip);
	}

	/** The region a piece occupies (x0, y0, x1, y1), with a margin of body round it; null for the whole body. */
	static int[] region(ModelData[] worn) {
		if (worn == null) {
			return null;
		}
		int[] r = { 999, 999, -999, -999 };
		for (ModelData m : worn) {
			if (m != null) {
				int[] b = m.bounds();
				r[0] = Math.min(r[0], b[0]);
				r[1] = Math.min(r[1], b[1]);
				r[2] = Math.max(r[2], b[3]);
				r[3] = Math.max(r[3], b[4]);
			}
		}
		int cx = (r[0] + r[2]) / 2, cy = (r[1] + r[3]) / 2, half = Math.max((r[2] - r[0]) / 2, (r[3] - r[1]) / 2) + 22;
		half = Math.max(half, 45);
		return new int[] { cx - half, cy - (int) (half * 1.4), cx + half, cy + (int) (half * 1.4) };
	}

	static BufferedImage card(String tag, String title, String sub, String stats, BufferedImage icon, ModelData male, ModelData female, int[] region) {
		int vw = 170, W = 150 + vw * 4 + 10, H = 370;
		BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(BG);
		g.fillRect(0, 0, W, H);
		label(g, tag + "  " + title, 12, 24, GOLD, 17, true);
		label(g, sub, 12, 44, TEXT, 12, false);
		label(g, stats == null ? "" : stats, 12, 62, DIM, 12, false);
		g.setColor(PANEL);
		g.fillRect(8, 72, W - 16, H - 80);
		if (icon != null) {
			g.drawImage(icon, 16, 90, null);
			label(g, "icon", 16, 236, DIM, 11, false);
		}
		String[] cap = { "male, front", "male, back", "female, front", "female, back" };
		for (int k = 0; k < 4; k++) {
			int x0 = 150 + k * vw;
			bodyView(g, k < 2 ? male : female, k % 2 == 1, x0, 80, vw, 262, region);
			label(g, cap[k], x0 + 40, H - 14, DIM, 11, false);
			if (region != null) {
				// the whole body, small, in the corner of each view
				bodyView(g, k < 2 ? male : female, k % 2 == 1, x0 + vw - 52, 80, 50, 96, null);
			}
		}
		g.dispose();
		return img;
	}

	/** "def 86/84/76/-6/84 (stab/slash/crush/magic/ranged)  +28 ranged  +2 prayer" per item. */
	static Map<Integer, String> armourStats(File json) {
		Map<Integer, String> out = new java.util.HashMap<>();
		String[][] extra = { { "attackMagic", "magic" }, { "attackRange", "ranged" }, { "strengthBonus", "str" }, { "prayerBonus", "prayer" } };
		try (java.io.FileReader r = new java.io.FileReader(json)) {
			for (com.google.gson.JsonElement e : com.google.gson.JsonParser.parseReader(r).getAsJsonArray()) {
				com.google.gson.JsonObject o = e.getAsJsonObject();
				if (!o.has("bonuses")) {
					continue;
				}
				com.google.gson.JsonObject b = o.getAsJsonObject("bonuses");
				StringBuilder sb = new StringBuilder("def");
				String[] d = { "defenceStab", "defenceSlash", "defenceCrush", "defenceMagic", "defenceRange" };
				for (int i = 0; i < d.length; i++) {
					sb.append(i == 0 ? " " : "/").append(b.has(d[i]) ? b.get(d[i]).getAsInt() : 0);
				}
				sb.append(" (stab/slash/crush/magic/ranged)");
				for (String[] k : extra) {
					if (b.has(k[0])) {
						int v = b.get(k[0]).getAsInt();
						sb.append("   ").append(v > 0 ? "+" : "").append(v).append(' ').append(k[1]);
					}
				}
				out.put(o.get("id").getAsInt(), sb.toString());
			}
		} catch (Exception e) {
			System.out.println("no bonuses: " + e);
		}
		return out;
	}

	public static void main(String[] args) throws Exception {
		File out = new File(args[1]);
		out.mkdirs();
		int set = Integer.parseInt(args[2]);
		ArmourSets.Style st = ArmourSets.SETS[set - 1];
		Map<Integer, String> stats = armourStats(new File("data/cfg/ItemDefinitions.json"));
		try (CacheEditor c = new CacheEditor(java.nio.file.Paths.get(args[0]))) {
			ModelForge f = new ModelForge(c);
			CacheEditor.Archive cfg = CacheEditor.Archive.decode(c.read(0, ConfigDefinitions.CONFIG_ARCHIVE));
			ConfigDefinitions.Table obj = ConfigDefinitions.load(cfg, "obj", false);
			ArmourSets as = new ArmourSets(new ArmourForge(f));
			String tagBase = "A" + set;
			for (int a = 3; a < args.length; a++) {
				if (args[a].equals("full")) {
					ModelData[][] wm = new ModelData[6][], wf = new ModelData[6][];
					for (int s = 0; s < 6; s++) {
						if (ArmourSets.has(st, s)) {
							wm[s] = as.worn(st, s, false);
							wf[s] = as.worn(st, s, true);
						}
					}
					ModelData male = dressed(f, st, wm, false), female = dressed(f, st, wf, true);
					String req = VoidArmoury.requirement(st.ids[1]);
					BufferedImage img = card(tagBase, st.name + " - the full set", st.kind.name().toLowerCase() + "  -  " + req, "", null, male, female, null);
					// the set's icons along the left
					Graphics2D g = img.createGraphics();
					int row = 0;
					for (int s = 0; s < 6; s++) {
						if (ArmourSets.has(st, s)) {
							ModelData inv = as.inventory(st, s);
							int[] cam = ArmourSets.fitCamera(itemCam(obj.decode(ArmourSets.base(st, s)[4])), inv);
							g.drawImage(icon(inv, cam, 2), 16 + (row % 2) * 66, 84 + (row / 2) * 66, null);
							row++;
						}
					}
					g.dispose();
					File file = new File(out, tagBase + "-full-" + st.slug + ".png");
					ImageIO.write(img, "png", file);
					System.out.println("wrote " + file);
					continue;
				}
				int s = Integer.parseInt(args[a]);
				if (!ArmourSets.has(st, s)) {
					continue;
				}
				int n = 0;
				for (int k = 0; k <= s; k++) {
					n += ArmourSets.has(st, k) ? 1 : 0;
				}
				ModelData[][] wm = new ModelData[6][], wf = new ModelData[6][];
				wm[s] = as.worn(st, s, false);
				wf[s] = as.worn(st, s, true);
				ModelData inv = as.inventory(st, s);
				int[] cam = ArmourSets.fitCamera(itemCam(obj.decode(ArmourSets.base(st, s)[4])), inv);
				int tris = wm[s][0].faces.size() + (wm[s][1] == null ? 0 : wm[s][1].faces.size());
				String tag = String.format("%s-%02d", tagBase, n);
				BufferedImage img = card(tag, st.names[s], SLOT_NAMES[s] + "  -  " + VoidArmoury.requirement(st.ids[s]) + "  -  " + tris + " tris",
						stats.get(st.ids[s]), icon(inv, cam, 4), dressed(f, st, wm, false), dressed(f, st, wf, true), region(wm[s]));
				File file = new File(out, tag + "-" + st.slug + "-" + SLOT_NAMES[s] + ".png");
				ImageIO.write(img, "png", file);
				System.out.println("wrote " + file + " (" + tris + " tris)");
			}
		}
	}
}
