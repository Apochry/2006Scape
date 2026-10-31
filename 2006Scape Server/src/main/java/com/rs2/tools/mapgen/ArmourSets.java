package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ArmourForge.*;
import static com.rs2.tools.mapgen.ModelData.hsl;

import java.io.IOException;

import com.rs2.game.content.voidrealm.VoidConstants;

/**
 * Six armour sets, each a matched family built on the cache's own WORN meshes (rune plate, green d'hide, mystic) for
 * both bodies: the male and the female model of every piece are re-dyed and dressed separately, so each fits its own
 * body, and every added part takes the skin of the nearest original vertex (see {@link ArmourForge#attach}).
 * Decorations are placed relative to the base mesh's bounds, so they land in the same place on both bodies.
 *
 * <ol>
 * <li>Oathsworn plate (Defence 40): the Barrow Thane's oath-guard. Grave-bronze, ram's horns, a ghost-green oath rune.</li>
 * <li>Duskstalker hide (Ranged 50): Nythera's wolf-runners. Dusk-grey hide, a pale wolf-fur mantle, fang charms.</li>
 * <li>Scarweave robes (Magic 60): cloth woven on the Scar's edge. Night-violet, the Scar burning down the front.</li>
 * <li>Starmetal plate (Defence 60): Gatehold's best, to go with the starmetal weapons. Pale starmetal, star crests.</li>
 * <li>Voidsteel plate (Defence 75): quenched in the dark, with the voidsteel weapons. Black-violet, horns, eyes, spikes.</li>
 * <li>Heartfire regalia (Defence 90): Aurel's own, re-lit. Gold and white, a crown of flame, a halo, the sun on the chest.</li>
 * </ol>
 */
final class ArmourSets {

	static final int HELM = 0, BODY = 1, LEGS = 2, SHIELD = 3, BOOTS = 4, GLOVES = 5;

	enum Kind {
		PLATE, HIDE, ROBE
	}

	enum Motif {
		OATH, DUSK, SCAR, STAR, VOID, HEART
	}

	/** Per kind and slot: male worn, male second (sleeves / torso), female worn, female second, base item, base inventory model. */
	static final int[][][] BASE = {
			{ { 218, -1, 394, -1, 1163, 2813 }, { 306, 164, 468, 344, 1127, 2605 }, { 268, -1, 432, -1, 1079, 2582 }, { 486, -1, 486, -1, 1201, 2339 },
					{ 4954, -1, 5031, -1, 4131, 5037 }, { 176, -1, 355, -1, 1059, -1 } },
			{ { 220, -1, 396, -1, 1169, 2541 }, { 314, -1, 477, -1, 1135, 2745 }, { 258, -1, 423, -1, 1099, 2507 }, null,
					{ 4954, -1, 5031, -1, 4131, 5037 }, { 178, -1, 355, -1, 1059, -1 } },
			{ { 5027, -1, 5033, -1, 4089, 5040 }, { 4952, 5028, 5029, 5035, 4091, 5043 }, { 4844, -1, 5034, -1, 4093, 5042 }, null,
					{ 4955, -1, 5032, -1, 4097, 5038 }, { 4953, -1, 5030, -1, 1059, -1 } } };

	/** One set: its look, its pieces (ids in slot order, -1 for none), level and names. */
	static final class Style {
		final String name, slug;
		final Kind kind;
		final Motif motif;
		final int level;
		final int main, dark, light, trim, trimDark, accent, core, leather, leatherDark;
		final int[] ids;
		final String[] names, examines;

		Style(String name, String slug, Kind kind, Motif motif, int level, int main, int dark, int light, int trim, int trimDark, int accent, int core,
				int leather, int leatherDark, int[] ids, String[] names, String[] examines) {
			this.name = name;
			this.slug = slug;
			this.kind = kind;
			this.motif = motif;
			this.level = level;
			this.main = main;
			this.dark = dark;
			this.light = light;
			this.trim = trim;
			this.trimDark = trimDark;
			this.accent = accent;
			this.core = core;
			this.leather = leather;
			this.leatherDark = leatherDark;
			this.ids = ids;
			this.names = names;
			this.examines = examines;
		}
	}

	static int violet(int light, int intensity) {
		return ConfigDefinitions.voidRamp(light, intensity);
	}

	static final int GHOST = hsl(23, 4, 92), GHOST_CORE = hsl(23, 2, 118), STAR_WHITE = hsl(42, 0, 122), EMBER = hsl(4, 6, 74),
			EMBER_HOT = hsl(6, 5, 100);

	static final Style[] SETS = {
			new Style("Oathsworn", "oathsworn", Kind.PLATE, Motif.OATH, 40, hsl(6, 4, 42), hsl(6, 4, 27), hsl(7, 3, 58), hsl(8, 5, 60), hsl(7, 5, 40),
					GHOST, GHOST_CORE, hsl(6, 2, 18), hsl(6, 2, 12), VoidConstants.OATHSWORN_SET,
					new String[] { "Oathsworn full helm", "Oathsworn platebody", "Oathsworn platelegs", "Oathsworn kiteshield", "Oathsworn boots",
							"Oathsworn gauntlets" },
					new String[] { "The Thane's guard swore never to take these off. They kept their word.",
							"Grave-bronze, green at the seams. The oath on the chest still glows.",
							"They've stood in the Barrow for a long time. They're good at standing.",
							"The oath-rune on it is still binding someone. Hopefully not you.", "Heavy bronze boots that never stray from their post.",
							"Bronze gauntlets with a death-grip." }),
			new Style("Duskstalker", "duskstalker", Kind.HIDE, Motif.DUSK, 50, hsl(36, 1, 34), hsl(36, 1, 22), hsl(36, 1, 48), hsl(0, 0, 82),
					hsl(0, 0, 62), violet(70, 6), hsl(8, 1, 110), hsl(6, 3, 26), hsl(6, 3, 16), VoidConstants.DUSKSTALKER_SET,
					new String[] { "Duskstalker coif", null, null, null, "Duskstalker boots", "Duskstalker vambraces" },
					new String[] { "Wolf ears and all. The wolves of Nythera find it very confusing.",
							"Hollowed-wolf hide, tanned at dusk. The fur mantle smells of rain.", "Hide chaps that make no sound at all on wet stone.",
							null, "Soft boots for hunting things that hunt.", "Hide bracers with a fang charm on the cuff." }),
			new Style("Scarweave", "scarweave", Kind.ROBE, Motif.SCAR, 60, violet(22, 3), violet(12, 2), violet(36, 4), violet(98, 6), violet(70, 6),
					violet(120, 3), STAR_WHITE, violet(12, 2), violet(8, 1), VoidConstants.SCARWEAVE_SET,
					new String[] { "Scarweave hat", "Scarweave robe top", "Scarweave robe bottom", null, "Scarweave boots", "Scarweave gloves" },
					new String[] { "Woven on the Scar's edge. The crystal at the tip hums when spells are near.",
							"The Scar runs right down the front, and it's still open.", "Night-dark cloth that hangs as if it's underwater.", null,
							"They leave violet footprints that fade by morning.", "Gloves that never quite touch what you're holding." }),
			new Style("Starmetal", "starmetal", Kind.PLATE, Motif.STAR, 60, hsl(42, 1, 74), hsl(42, 1, 54), hsl(42, 1, 92), NIGHTCOL(), hsl(42, 4, 20),
					violet(92, 6), STAR_WHITE, hsl(42, 3, 22), hsl(42, 3, 14), VoidConstants.STARMETAL_ARMOUR,
					new String[] { "Starmetal full helm", "Starmetal platebody", "Starmetal platelegs", "Starmetal kiteshield", "Starmetal boots",
							"Starmetal gauntlets" },
					new String[] { "A crest of star-points, and a shard of the star over the brow.",
							"Gatehold's finest plate. It rings like a bell when it's struck, which is often.", "Pale starmetal legs. They're lighter than rune.",
							"The Wardens' star, picked out in the star's own metal.", "They ring faintly with every step.", "Starmetal gauntlets, star-pointed at the cuff." }),
			new Style("Voidsteel", "voidsteel", Kind.PLATE, Motif.VOID, 75, violet(22, 3), violet(11, 2), violet(36, 4), violet(104, 5), violet(62, 6),
					violet(98, 6), hsl(42, 0, 124), violet(8, 1), violet(4, 1), VoidConstants.VOIDSTEEL_ARMOUR,
					new String[] { "Voidsteel full helm", "Voidsteel platebody", "Voidsteel platelegs", "Voidsteel kiteshield", "Voidsteel boots",
							"Voidsteel gauntlets" },
					new String[] { "The horns came back from the dark on their own. So did the eye.",
							"Starmetal that went into the dark and came back hungry. The eye on the chest blinks.", "Black plate with burning edges. The spikes are a warning.",
							"The eye in the shield looks where the next blow is coming from.", "Spiked sabatons. Kicking is encouraged.",
							"Knuckle-spiked gauntlets that burn violet at the joints." }),
			new Style("Heartfire", "heartfire", Kind.PLATE, Motif.HEART, 90, hsl(9, 0, 104), hsl(9, 0, 88), hsl(9, 0, 120), hsl(8, 6, 64), hsl(7, 6, 42),
					EMBER_HOT, hsl(9, 1, 124), hsl(8, 5, 54), hsl(7, 5, 38), VoidConstants.HEARTFIRE_SET,
					new String[] { "Heartfire full helm", "Heartfire platebody", "Heartfire platelegs", "Heartfire kiteshield", "Heartfire boots",
							"Heartfire gauntlets" },
					new String[] { "Aurel's crown of flame, and a halo the sky gave back.",
							"Aurel's own plate, re-lit. The sun on the chest is his heart, and it's burning.", "Gold-white tassets over plate that never cools.",
							"The sun of the old kingdom, risen again.", "Every step leaves the ground a little warmer.", "Ember-cuffed gauntlets. A king's hands." }), };

	static int NIGHTCOL() {
		return hsl(42, 4, 30);
	}

	static {
		String[] hide = SETS[1].names;
		hide[BODY] = "Duskstalker body";
		hide[LEGS] = "Duskstalker chaps";
	}

	final ArmourForge af;

	ArmourSets(ArmourForge af) {
		this.af = af;
	}

	/** Whether a set has a piece in the slot. */
	static boolean has(Style st, int slot) {
		return st.ids[slot] >= 0 && BASE[st.kind.ordinal()][slot] != null;
	}

	static int[] base(Style st, int slot) {
		return BASE[st.kind.ordinal()][slot];
	}

	/* ====================================================================== */
	/*                                RECOLOUR                                */
	/* ====================================================================== */

	int recolour(Style st, int slot, int c) {
		switch (st.kind) {
		case PLATE:
			switch (c) {
			case 61:
				return st.main;
			case 41:
				return st.dark;
			case 57:
				return slot == SHIELD ? st.dark : st.light;
			case 24:
			case 8741:
			case 7054:
				return st.trim;
			case 12:
			case 926:
				return st.trimDark;
			case 0:
				return st.motif == Motif.HEART || st.motif == Motif.VOID || st.motif == Motif.OATH ? st.accent : 0;
			case 5400:
				return st.leather;
			case 5648:
				return st.leatherDark;
			case 4550:
				return slot == GLOVES ? st.dark : c;
			case 43072:
			case 22416:
				return st.main;
			}
			return c;
		case HIDE:
			switch (c) {
			case 22408:
			case 15242:
				return st.dark;
			case 22416:
				return st.main;
			case 22424:
				return st.light;
			case 5012:
			case 5400:
				return st.leather;
			case 7566:
			case 5648:
			case 8596:
				return st.leatherDark;
			case 926:
				return st.trimDark;
			case 43059:
				return slot == HELM ? st.main : st.trimDark; // the coif's hood (its 4550 is the face, and stays skin)
			case 57:
			case 61:
				return slot == BOOTS ? st.main : st.trim;
			}
			return c;
		default:
			switch (c) {
			case 40045:
			case 39009:
				return st.main;
			case 40384:
				return st.light;
			case 40918:
				return st.trim;
			case 39463:
				return st.dark;
			case 119:
				return st.accent;
			}
			return c;
		}
	}

	ModelData dyed(Style st, int slot, int model) throws IOException {
		ModelData m = af.read(model);
		for (int i = 0; i < m.colors.size(); i++) {
			m.colors.set(i, recolour(st, slot, m.colors.get(i)));
		}
		return m;
	}

	/* ====================================================================== */
	/*                                 PIECES                                 */
	/* ====================================================================== */

	static final Frame FRONT = new Frame(new double[] { 0, 0, 0 }, new double[] { 1, 0, 0 }, new double[] { 0, 1, 0 }, new double[] { 0, 0, -1 });
	static final Frame BACK = new Frame(new double[] { 0, 0, 0 }, new double[] { 1, 0, 0 }, new double[] { 0, 1, 0 }, new double[] { 0, 0, 1 });

	/** The worn model(s) of a piece for one body: {main, second or null}. */
	ModelData[] worn(Style st, int slot, boolean female) throws IOException {
		int[] b = base(st, slot);
		ModelData m = dyed(st, slot, b[female ? 2 : 0]);
		int secondId = b[female ? 3 : 1];
		ModelData second = secondId < 0 ? null : dyed(st, slot, secondId);
		if (female && slot == BODY) { // the female tops leave a band of bare midriff; close it
			coverMidriff(m, st);
			coverMidriff(second, st);
		}
		// mystic tops have the sleeves first and the torso second; decorate the torso
		ModelData torso = st.kind == Kind.ROBE && slot == BODY ? second : m;
		switch (slot) {
		case HELM:
			helm(st, m);
			break;
		case BODY:
			body(st, torso);
			break;
		case LEGS:
			legs(st, m);
			break;
		case SHIELD:
			shield(st, m);
			break;
		case BOOTS:
			boots(st, m);
			break;
		default:
			gloves(st, m);
		}
		return new ModelData[] { m, second };
	}

	/**
	 * The base item's inventory camera {zoom, rot1, rot2, off1, off2}, adjusted only if the decorated piece spills out
	 * of the 32x32 sprite: first slid sideways to centre it, then pulled back until it fits with a pixel to spare.
	 */
	static int[] fitCamera(int[] base, ModelData inv) {
		int[] cam = base.clone();
		for (int it = 0; it < 40; it++) {
			java.util.function.Function<int[], double[]> p = ModelPreview.itemCamera(cam[0], cam[1], cam[2], cam[3], cam[4], -inv.bounds()[1]);
			double x0 = 1e9, x1 = -1e9, y0 = 1e9, y1 = -1e9, z = 0;
			for (int[] v : inv.vertices) {
				double[] s = p.apply(v);
				x0 = Math.min(x0, s[0]);
				x1 = Math.max(x1, s[0]);
				y0 = Math.min(y0, s[1]);
				y1 = Math.max(y1, s[1]);
				z += s[2] / inv.vertices.size();
			}
			boolean wide = x1 - x0 > 27, tall = y1 - y0 > 27; // a pixel of outline either side
			if (wide || tall) {
				cam[0] = (int) (cam[0] * 1.04) + 4;
			} else if (x0 < 2.5 || x1 > 29.5) {
				cam[3] += (int) Math.round((16 - (x0 + x1) / 2) * z / 512);
				if (Math.abs(16 - (x0 + x1) / 2) < 0.6) {
					break;
				}
			} else if (y0 < 2.5 || y1 > 29.5) {
				cam[0] = (int) (cam[0] * 1.04) + 4;
			} else {
				break;
			}
		}
		return cam;
	}

	/** Skin-coloured faces on the trunk below the chest take the top's own colour. */
	static void coverMidriff(ModelData m, Style st) {
		if (m == null) {
			return;
		}
		for (int i = 0; i < m.faces.size(); i++) {
			if (m.colors.get(i) != 4550) {
				continue;
			}
			double x = 0, y = 0;
			for (int v : m.faces.get(i)) {
				x += m.vertices.get(v)[0] / 3.0;
				y += m.vertices.get(v)[1] / 3.0;
			}
			if (Math.abs(x) < 18 && y > -140) {
				m.colors.set(i, st.kind == Kind.HIDE ? st.dark : st.main);
			}
		}
	}

	/** The inventory model, from the male worn model(s), in the base item's own inventory frame. */
	ModelData inventory(Style st, int slot) throws IOException {
		ModelData[] w = worn(st, slot, false);
		int[] b = base(st, slot);
		if (slot == SHIELD) {
			double tau = Math.PI * 2;
			return af.shieldInventory(w[0], -1104 * tau / 2048, 344 * tau / 2048);
		}
		if (b[5] < 0) { // the pair laid side by side, like gloves
			ModelData g = af.gauntletsInventory(w[0]);
			for (int i = 0; i < g.colors.size(); i++) {
				if (g.colors.get(i) == 4550) { // bare hands read as leather mitts on the icon
					g.colors.set(i, st.leatherDark);
				}
			}
			return g;
		}
		ModelData all = w[1] == null ? w[0] : mergeAll(w[0], w[1]);
		return af.inventory(all, b[1] < 0 ? new int[] { b[0] } : new int[] { b[0], b[1] }, b[5]);
	}

	/* ------------------------------------------------------------- the helm */

	void helm(Style st, ModelData m) {
		int[] bb = m.bounds();
		double hw = bb[3], top = bb[1], bottom = bb[4], h = bottom - top, front = bb[2], back = bb[5];
		ModelData add = new ModelData();
		switch (st.motif) {
		case OATH: // ram's horns curling out from the temples
			for (int side = -1; side <= 1; side += 2) {
				double[] r = { side * hw * 0.8, top + h * 0.3, 2 };
				double[] a = { side * (hw + 9), top + h * 0.1, 4 }, b = { side * (hw + 14), top + h * 0.5, 3 }, c = { side * (hw + 9), top + h * 0.78, -5 };
				blade(add, r, a, new double[] { 0, 0, 1 }, 4.6, 2.6, st.trim);
				blade(add, a, b, new double[] { 0, 0, 1 }, 3.8, 2.2, st.trimDark);
				blade(add, b, c, new double[] { 0, 0, 1 }, 2.8, 1.6, st.trim);
			}
			decal(add, m, FRONT, diamond(0, top + h * 0.5, 2.2, 4.2), 0.5, 2.4, st.accent, st.trimDark);
			break;
		case DUSK: // wolf ears
			for (int side = -1; side <= 1; side += 2) {
				double[] r = { side * hw * 0.55, top + h * 0.12, 2 };
				blade(add, r, new double[] { side * hw * 0.75, top - 9, 3 }, new double[] { 1, 0, 0 }, 3.6, 1.6, st.dark);
			}
			decal(add, m, FRONT, diamond(0, top + h * 0.28, 1.8, 2.6), 0.4, 1.8, st.accent, st.trimDark);
			break;
		case SCAR: { // a violet crystal at the tip of the hat, and three shards hanging round it
			double[] tip = { 0, top + 4, (front + back) / 2 + 3 };
			add.tube(new double[] { tip[0], tip[1] + 5, tip[2] }, tip, 3.6, 0, 6, 0.3, st.accent, st.accent);
			add.tube(tip, new double[] { tip[0], tip[1] - 9, tip[2] }, 3.6, 0, 6, 0.3, st.core, st.accent);
			for (int k = 0; k < 3; k++) {
				double a = Math.PI * 2 * k / 3;
				double[] c = { Math.cos(a) * 9, tip[1] - 2, tip[2] + Math.sin(a) * 9 };
				blade(add, new double[] { c[0], c[1] + 3, c[2] }, new double[] { c[0], c[1] - 4, c[2] }, new double[] { Math.cos(a), 0, Math.sin(a) }, 1.4,
						1.0, st.trim);
			}
			ring(add, new double[] { 0, bottom - h * 0.32, (front + back) / 2 }, new double[] { 0, -1, 0 }, new double[] { 1, 0, 0 }, hw * 0.98,
					(back - front) / 2 * 0.98, 0, 2.2, 10, st.trim);
			break;
		}
		case STAR: // a crest of star-points front to back, a shard on the brow
			for (int k = 0; k < 5; k++) {
				double z = front + 3 + (back - front - 6) * k / 4.0;
				double tall = k == 1 || k == 2 ? 13 : 9;
				double[] r = { 0, top + 3, z };
				blade(add, r, new double[] { 0, top - tall, z + (k - 2) * 2.5 }, new double[] { 0, 0, 1 }, 3.0, 1.4, k % 2 == 0 ? st.light : st.main);
			}
			decal(add, m, FRONT, diamond(0, top + h * 0.38, 2.6, 4.2), 0.5, 3.0, st.accent, st.trim);
			break;
		case VOID: // horns swept forward, an eye on the brow
			for (int side = -1; side <= 1; side += 2) {
				double[] r = { side * hw * 0.8, top + h * 0.25, 2 };
				double[] a = { side * (hw + 5), top - 4, -2 }, b = { side * (hw + 2), top - 12, -12 };
				blade(add, r, a, new double[] { 0, 0, 1 }, 3.8, 2.2, st.main);
				blade(add, a, b, new double[] { 0, 0, 1 }, 2.6, 1.6, st.trim);
			}
			decal(add, m, FRONT, new double[][] { { -4.5, top + h * 0.36 }, { 0, top + h * 0.3 }, { 4.5, top + h * 0.36 }, { 0, top + h * 0.42 } }, 0.5,
					2.2, st.core, st.trim);
			decal(add, m, FRONT, diamond(0, top + h * 0.36, 1.2, 1.2), 0.3, 2.9, st.accent, st.accent);
			break;
		case HEART: // a crown of flame round the top, a halo floating above, an ember brow-stone
			for (int k = 0; k < 9; k++) {
				double a = Math.PI * 2 * k / 9 + Math.PI / 2;
				double rx = hw * 0.92, rz = (back - front) / 2 * 0.92, cz = (front + back) / 2;
				double[] r = { Math.cos(a) * rx, top + h * 0.2, cz + Math.sin(a) * rz };
				double tall = k == 0 ? 15 : k % 2 == 0 ? 11 : 8;
				blade(add, r, new double[] { Math.cos(a) * rx * 1.15, top - tall, cz + Math.sin(a) * rz * 1.15 }, new double[] { -Math.sin(a), 0, Math.cos(a) },
						2.8, 1.2, k % 2 == 0 ? st.trim : st.accent);
			}
			ring(add, new double[] { 0, top - 9, (front + back) / 2 + 2 }, new double[] { 0, -1, 0 }, new double[] { 1, 0, 0 }, hw * 0.9, hw * 0.9, 0, 1.6,
					12, st.core);
			decal(add, m, FRONT, diamond(0, top + h * 0.4, 2.6, 4.0), 0.5, 3.0, st.accent, st.trim);
			break;
		}
		af.attach(m, add, Mode.RIGID, 7, null, st.name + " helm");
	}

	/* ------------------------------------------------------------- the body */

	void body(Style st, ModelData m) {
		int[] bb = m.bounds();
		double hw = bb[3], top = bb[1], bottom = bb[4], h = bottom - top;
		ModelData add = new ModelData();
		double sx = hw * 0.7, sy = top + h * 0.12; // the shoulder
		double cy = top + h * 0.42; // the middle of the chest
		switch (st.motif) {
		case OATH:
			for (int side = -1; side <= 1; side += 2) {
				double[] c = { side * sx, sy, -1 };
				double[] axis = norm(new double[] { side * 0.6, -0.8, 0 });
				dome(add, c, axis, new double[] { 0, 0, 1 }, hw * 0.55, hw * 0.42, hw * 0.4, 6, st.dark, st.main);
				ring(add, c, axis, new double[] { 0, 0, 1 }, hw * 0.6, hw * 0.47, -1.5, 2.5, 6, st.trim);
			}
			oathRune(add, m, FRONT, 0, cy, h * 0.42, st);
			break;
		case DUSK: // a fur mantle round the shoulders, a fang necklace
			for (int k = 0; k < 14; k++) {
				double a = Math.PI * k / 13; // from one shoulder round the back to the other
				double x = -Math.cos(a) * hw * 0.85, z = Math.sin(a) * 9 - 2;
				double[] r = { x, top + 3, z };
				blade(add, r, new double[] { x * 1.25, top + h * 0.3, z + (z > 0 ? 4 : -2) }, new double[] { -Math.sin(a), 0, Math.cos(a) }, 4.5, 2.0,
						k % 2 == 0 ? st.trim : st.trimDark);
			}
			for (int k = -2; k <= 2; k++) {
				double x = k * hw * 0.16, y = top + h * 0.28 + Math.abs(k) * -1.5;
				double[] p = project(m, FRONT, x, y);
				blade(add, add(p, new double[] { 0, 0, -1.5 }), add(p, new double[] { 0, 5, -2.5 }), new double[] { 1, 0, 0 }, 1.2, 0.8,
						k == 0 ? st.accent : st.core);
			}
			break;
		case SCAR: // the Scar, burning down the front
			scar(add, m, FRONT, 0, top + h * 0.15, bottom - h * 0.1, hw * 0.3, st);
			for (int side = -1; side <= 1; side += 2) {
				double[] c = { side * sx, sy, 0 };
				blade(add, c, new double[] { side * (sx + 6), sy - 12, 2 }, new double[] { 0, 0, 1 }, 2.6, 1.4, st.trim);
				blade(add, new double[] { side * sx * 0.8, sy, 3 }, new double[] { side * (sx + 1), sy - 9, 6 }, new double[] { 0, 0, 1 }, 2.0, 1.2, st.accent);
			}
			break;
		case STAR:
			for (int side = -1; side <= 1; side += 2) {
				double[] c = { side * sx, sy, -1 };
				double[] axis = norm(new double[] { side * 0.6, -0.8, 0 });
				dome(add, c, axis, new double[] { 0, 0, 1 }, hw * 0.55, hw * 0.42, hw * 0.4, 6, st.dark, st.main);
				ring(add, c, axis, new double[] { 0, 0, 1 }, hw * 0.6, hw * 0.47, -1.5, 2.5, 6, st.trim);
				double[] tip = add(c, scale(axis, hw * 0.4));
				blade(add, tip, add(tip, scale(axis, 9)), new double[] { 0, 0, 1 }, 2.6, 1.6, st.light);
			}
			star4(add, m, FRONT, 0, cy, h * 0.34, st.trim, st.accent, st.core);
			break;
		case VOID:
			for (int side = -1; side <= 1; side += 2) {
				double[] c = { side * sx, sy, -1 };
				double[] axis = norm(new double[] { side * 0.6, -0.8, 0 });
				dome(add, c, axis, new double[] { 0, 0, 1 }, hw * 0.55, hw * 0.42, hw * 0.36, 6, st.dark, st.main);
				ring(add, c, axis, new double[] { 0, 0, 1 }, hw * 0.6, hw * 0.47, -1.5, 2.5, 6, st.trim);
				double[] tip = add(c, scale(axis, hw * 0.3));
				for (int k = -1; k <= 1; k++) {
					double[] dir = norm(add(axis, new double[] { 0, 0, k * 0.6 }));
					blade(add, add(tip, new double[] { 0, 0, k * 5 }), add(add(tip, new double[] { 0, 0, k * 6 }), scale(dir, k == 0 ? 14 : 10)),
							new double[] { 1, 0, 0 }, 2.4, 1.6, k == 0 ? st.trim : st.main);
				}
			}
			eye(add, m, FRONT, 0, cy, hw * 0.34, st);
			break;
		case HEART:
			for (int side = -1; side <= 1; side += 2) { // winged pauldrons: three flame-feathers sweeping up and out
				double[] c = { side * sx, sy, 0 };
				double[] axis = norm(new double[] { side * 0.6, -0.8, 0 });
				dome(add, c, axis, new double[] { 0, 0, 1 }, hw * 0.55, hw * 0.42, hw * 0.36, 6, st.dark, st.main);
				ring(add, c, axis, new double[] { 0, 0, 1 }, hw * 0.6, hw * 0.47, -1.5, 2.5, 6, st.trim);
				for (int k = 0; k < 3; k++) {
					double[] r = add(c, new double[] { side * 2, -hw * 0.25, 4 + k * 3 });
					double[] t = add(r, new double[] { side * (10 + k * 4), -(16 - k * 3), 6 + k * 2 });
					blade(add, r, t, new double[] { 0, 0, 1 }, 3.4, 1.4, k == 1 ? st.accent : st.trim);
				}
			}
			sun(add, m, FRONT, 0, cy, h * 0.3, st);
			break;
		}
		af.attach(m, add, Mode.RIGID_PER_PART, 10, null, st.name + " body");
	}

	/* ------------------------------------------------------------- the legs */

	void legs(Style st, ModelData m) {
		int[] bb = m.bounds();
		double hw = bb[3], top = bb[1], bottom = bb[4], h = bottom - top;
		double kneeY = top + h * 0.55, kneeX = hw * 0.53;
		for (int side = -1; side <= 1; side += 2) {
			double x = side * kneeX;
			ModelData one = new ModelData();
			switch (st.motif) {
			case DUSK:
				decal(one, m, FRONT, new double[][] { { x - 4, kneeY - 5 }, { x + 4, kneeY - 5 }, { x + 4.5, kneeY + 5 }, { x - 4.5, kneeY + 5 } }, 0.6, 1.6,
						st.leather, st.leatherDark);
				break;
			case SCAR:
				break;
			default: {
				decal(one, m, FRONT, new double[][] { { x, kneeY - 8.5 }, { x + 6.5, kneeY - 1.5 }, { x + 4.5, kneeY + 6.5 }, { x, kneeY + 9.5 },
						{ x - 4.5, kneeY + 6.5 }, { x - 6.5, kneeY - 1.5 } }, 1, 2.4, st.trim, st.trimDark);
				double[] hit = surface(m, new double[] { x, kneeY, -100 }, new double[] { 0, 0, 1 });
				double[] p = hit == null ? new double[] { x, kneeY, -12 } : hit;
				int col = st.motif == Motif.HEART ? st.accent : st.motif == Motif.OATH ? st.accent : st.trim;
				double len = st.motif == Motif.VOID ? 9 : 5;
				one.tube(add(p, new double[] { 0, 0, -1.5 }), add(p, new double[] { 0, st.motif == Motif.VOID ? -2 : 0, -1.5 - len }), 3.0, 0, 4, 0.78,
						col, col);
			}
			}
			if (one.vertices.size() > 0) {
				af.attach(m, one, Mode.RIGID, 2, null, st.name + (side < 0 ? " right knee" : " left knee"));
			}
		}
		ModelData front = new ModelData();
		switch (st.motif) {
		case HEART: // gold tassets over the hips, an ember stone at the belt
			for (int side = -1; side <= 1; side += 2) {
				double x = side * hw * 0.45;
				decal(front, m, FRONT, new double[][] { { x - 5.5, top + 3 }, { x + 5.5, top + 3 }, { x + 5, top + h * 0.3 }, { x - 5, top + h * 0.3 } },
						1.0, 2.0, st.trim, st.trimDark);
			}
			decal(front, m, FRONT, diamond(0, top + 5, 3.0, 3.6), 0.5, 2.6, st.accent, st.trim);
			break;
		case SCAR: // the Scar's hem: a burning panel down the front
			scar(front, m, FRONT, 0, top + h * 0.1, bottom - h * 0.12, hw * 0.22, st);
			break;
		case DUSK: // a belt pouch
			decal(front, m, FRONT, rect(hw * 0.2, top + 3, hw * 0.55, top + 11), 0.6, 2.4, st.leather, st.leatherDark);
			break;
		case OATH:
			decal(front, m, FRONT, rect(-3, top + 2, 3, top + h * 0.22), 0.8, 1.8, st.trim, st.trimDark);
			break;
		default:
			break;
		}
		if (front.vertices.size() > 0) {
			af.attach(m, front, Mode.PER_VERTEX, 2, null, st.name + " legs front");
		}
	}

	/* ------------------------------------------------------------- the shield */

	void shield(Style st, ModelData m) throws IOException {
		Frame k = shieldFrame(af.read(486), 61);
		Frame f = new Frame(k.o, k.v, k.u, k.n); // x across the kite, y down its length (towards the point)
		ModelData add = new ModelData();
		switch (st.motif) {
		case OATH:
			oathRune(add, m, f, 0, 3, 30, st);
			decal(add, m, f, diamond(0, -17, 5, 5), 0.5, 3.4, st.trim, st.trimDark);
			break;
		case STAR:
			star4(add, m, f, 0, 1, 34, st.trim, st.accent, st.core);
			break;
		case VOID:
			eye(add, m, f, 0, 1, 11, st);
			for (int side = -1; side <= 1; side += 2) {
				decal(add, m, f, new double[][] { { side * 9, -20 }, { side * 22, -26 }, { side * 14, -14 } }, 0.5, 3.0, st.trim, st.trimDark);
			}
			break;
		case HEART:
			sun(add, m, f, 0, 1, 32, st);
			break;
		default:
			break;
		}
		af.attach(m, add, Mode.RIGID, 11, null, st.name + " shield");
	}

	/* ------------------------------------------------------------- boots, gloves */

	void boots(Style st, ModelData m) {
		int[] bb = m.bounds();
		double hw = bb[3], front = bb[2], top = bb[1];
		ModelData add = new ModelData();
		for (int side = -1; side <= 1; side += 2) {
			double x = side * hw * 0.56;
			switch (st.motif) {
			case DUSK: // a fur cuff
				add.tube(new double[] { x, top + 1, -1 }, new double[] { x, top + 5, -1 }, 6.4, 5.6, 6, 0.3, st.trim, st.trimDark);
				break;
			case SCAR:
				add.tube(new double[] { x, -3.5, front + 6 }, new double[] { x, -1.5, front + 1 }, 2.8, 0, 4, 0.78, st.trim, st.trim);
				break;
			case VOID:
				add.tube(new double[] { x, -3.5, front + 8 }, new double[] { x, -2, front - 3 }, 4.0, 0, 4, 0.78, st.trim, st.trim);
				add.tube(new double[] { x, top + 4, 0 }, new double[] { x + side * 6, top - 2, 1 }, 2.4, 0, 4, 0.78, st.main, st.trim);
				break;
			default:
				add.tube(new double[] { x, -3.5, front + 7 }, new double[] { x, -1, front }, 4.2, 0, 4, 0.78,
						st.motif == Motif.HEART ? st.trim : st.trim, st.trim);
				if (st.motif == Motif.HEART) {
					add.tube(new double[] { x, top + 2, -1 }, new double[] { x, top + 4.5, -1 }, 6.6, 6.6, 6, 0.3, st.accent, st.accent);
				}
			}
		}
		af.attach(m, add, Mode.RIGID_PER_PART, 0, null, st.name + " boots");
	}

	void gloves(Style st, ModelData m) {
		int[] bb = m.bounds();
		double hw = bb[3], top = bb[1];
		ModelData add = new ModelData();
		for (int side = -1; side <= 1; side += 2) {
			double x = side * (hw - 6.5);
			int cuff = st.motif == Motif.HEART || st.motif == Motif.SCAR ? st.accent : st.trim;
			double flare = st.kind == Kind.PLATE ? 7.6 : 6.4;
			add.tube(new double[] { x, top - 2, 1 }, new double[] { x, top + 10, 1 }, flare - 2.2, flare, 6, 0.26, st.kind == Kind.PLATE ? st.main : st.dark,
					st.dark);
			add.tube(new double[] { x, top + 9, 1 }, new double[] { x, top + 11.5, 1 }, flare + 0.3, flare + 0.3, 6, 0.26, cuff, cuff);
			if (st.motif == Motif.VOID) { // knuckle spikes
				for (int k = -1; k <= 1; k++) {
					add.tube(new double[] { x + side * 2, top + 18, -3 + k * 2.5 }, new double[] { x + side * 2, top + 18, -9 + k * 2.5 }, 1.4, 0, 4, 0.78,
							st.trim, st.trim);
				}
			}
			if (st.motif == Motif.STAR) {
				add.tube(new double[] { x, top + 4, 1 }, new double[] { x + side * 8, top - 2, 1 }, 2.0, 0, 4, 0.78, st.light, st.light);
			}
			if (st.motif == Motif.DUSK) {
				add.tube(new double[] { x - side * 0.5, top + 6, -6 }, new double[] { x - side * 0.5, top + 11, -8 }, 1.1, 0, 4, 0.78, st.core, st.core);
			}
		}
		af.attach(m, add, Mode.RIGID_PER_PART, 10, null, st.name + " gloves");
	}

	/* ------------------------------------------------------------- emblems */

	/** The oath rune: a stave with two arms raised (like a Y over an I), in ghost light. */
	static void oathRune(ModelData add, ModelData m, Frame f, double x, double y, double size, Style st) {
		double s = size / 2, w = size * 0.07;
		decal(add, m, f, rect(x - w, y - s * 0.6, x + w, y + s), 0.5, 2.2, st.accent, st.trimDark);
		decal(add, m, f, new double[][] { { x - w, y - s * 0.45 }, { x - s * 0.62, y - s }, { x - s * 0.62 + w * 2.2, y - s }, { x + w, y - s * 0.62 } },
				0.5, 2.2, st.accent, st.trimDark);
		decal(add, m, f, new double[][] { { x - w, y - s * 0.62 }, { x + s * 0.62 - w * 2.2, y - s }, { x + s * 0.62, y - s }, { x + w, y - s * 0.45 } },
				0.5, 2.2, st.accent, st.trimDark);
		decal(add, m, f, diamond(x, y + s * 0.15, w * 2.6, w * 2.6), 0.3, 3.0, st.core, st.accent);
	}

	/** A four-pointed star, its centre a gem. */
	static void star4(ModelData add, ModelData m, Frame f, double x, double y, double size, int col, int gem, int core) {
		double s = size / 2, w = size * 0.11;
		decal(add, m, f, new double[][] { { x, y - s }, { x + w, y }, { x, y + s }, { x - w, y } }, 0.6, 2.2, col, col);
		decal(add, m, f, new double[][] { { x - s * 0.8, y }, { x, y - w }, { x + s * 0.8, y }, { x, y + w } }, 0.6, 2.2, col, col);
		decal(add, m, f, diamond(x, y, w * 1.4, w * 1.4), 0.5, 3.4, gem, col);
		decal(add, m, f, diamond(x, y, w * 0.6, w * 0.6), 0.3, 4.0, core, gem);
	}

	/** An open eye: white almond, violet iris. */
	static void eye(ModelData add, ModelData m, Frame f, double x, double y, double size, Style st) {
		decal(add, m, f, new double[][] { { x - size, y }, { x - size * 0.4, y - size * 0.45 }, { x + size * 0.4, y - size * 0.45 }, { x + size, y },
				{ x + size * 0.4, y + size * 0.45 }, { x - size * 0.4, y + size * 0.45 } }, 0.5, 2.0, st.core, st.trim);
		decal(add, m, f, diamond(x, y, size * 0.3, size * 0.38), 0.3, 2.8, st.accent, st.accent);
		decal(add, m, f, diamond(x, y, size * 0.1, size * 0.18), 0.2, 3.3, violet(6, 1), violet(6, 1));
	}

	/** The sun: an ember heart in a gold disc, eight rays. */
	static void sun(ModelData add, ModelData m, Frame f, double x, double y, double size, Style st) {
		double s = size / 2;
		for (int k = 0; k < 8; k++) {
			double a = Math.PI * 2 * k / 8;
			double len = k % 2 == 0 ? s : s * 0.72, w = s * 0.13;
			double ca = Math.cos(a), sa = Math.sin(a);
			decal(add, m, f, new double[][] { { x + ca * s * 0.35 - sa * w, y + sa * s * 0.35 + ca * w }, { x + ca * len, y + sa * len },
					{ x + ca * s * 0.35 + sa * w, y + sa * s * 0.35 - ca * w } }, 0.5, 2.0, st.trim, st.trimDark);
		}
		double r = s * 0.42;
		double[][] disc = new double[8][];
		for (int k = 0; k < 8; k++) {
			double a = Math.PI * 2 * k / 8 + Math.PI / 8;
			disc[k] = new double[] { x + Math.cos(a) * r, y + Math.sin(a) * r };
		}
		decal(add, m, f, disc, 0.5, 2.4, st.trim, st.trimDark);
		decal(add, m, f, diamond(x, y, r * 0.55, r * 0.7), 0.4, 3.4, st.accent, st.trim);
		decal(add, m, f, diamond(x, y, r * 0.22, r * 0.3), 0.2, 4.0, st.core, st.accent);
	}

	/** The Scar: a jagged burning line from y0 down to y1. */
	static void scar(ModelData add, ModelData m, Frame f, double x, double y0, double y1, double zig, Style st) {
		int n = 6;
		for (int i = 0; i < n; i++) {
			double ya = y0 + (y1 - y0) * i / n, yb = y0 + (y1 - y0) * (i + 1) / n;
			double xa = x + (i % 2 == 0 ? -zig : zig) * (i == 0 ? 0.3 : 1), xb = x + (i % 2 == 0 ? zig : -zig);
			double w = 1.6 - i * 0.12;
			decal(add, m, f, new double[][] { { xa - w, ya }, { xa + w, ya }, { xb + w, yb }, { xb - w, yb } }, 0.4, 1.8, i % 2 == 0 ? st.accent : st.trim,
					st.trimDark);
		}
	}
}
