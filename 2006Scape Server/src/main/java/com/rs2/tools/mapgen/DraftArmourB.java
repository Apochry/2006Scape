package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ArmourForge.*;
import static com.rs2.tools.mapgen.LadderWeapons.*;
import static com.rs2.tools.mapgen.LowArmour.X;
import static com.rs2.tools.mapgen.LowArmour.Y;
import static com.rs2.tools.mapgen.LowArmour.Z;
import static com.rs2.tools.mapgen.ModelData.hsl;
import static com.rs2.tools.mapgen.QuestWeapons.*;

import com.rs2.tools.mapgen.ArmourForge.Frame;
import com.rs2.tools.mapgen.ArmourSets.Kind;
import com.rs2.tools.mapgen.ArmourSets.Motif;
import com.rs2.tools.mapgen.ArmourSets.Style;
import com.rs2.tools.mapgen.LadderWeapons.Rung;

/**
 * Draft armour sets A30-A34 and their weapons: built and previewed only (they are not in ArmourSets.SETS, have no item
 * ids, and nothing packs them into the cache). See DraftArmour.
 *
 * <ol start="30">
 * <li>Wildgrove (Magic 40): Druids of the deep woods, on the mantle base (BaseMantle): a leaf-wreathed hood with antlers grown through it, a leaf collar on the mantle, ivy and small golden flowers climbing the tunic, a bark sash, leaves trailing down the cape.</li>
 * <li>Gravescale (Defence 45): The Gravecoil, the barrow serpent, worn whole, on its own base (BaseSerpent), in the Gravecoil's colours: its head for a helm (jaws gaping round the face, hood spread, venom-yellow eyes), its body coiled round a scale shirt with the tail hanging behind, banded segmented greaves, scaled boots, gauntlets with fang claws, and a serpent coiled into a round shield with its head raised. The base carries the look; there are no decorations.</li>
 * <li>Gatehold sentinel (Defence 50): The guard on Gatehold's wall, built like the gate-tower he guards, on its own base (BaseBastion): blocky bright steel and Gatehold blue, a battlemented great helm with a portcullis visor and a tall blue plume, a squared breastplate with tower pauldrons and a blue tabard bearing the gold gate-tower, portcullis tassets, blocky sabatons, square-cuffed gauntlets, and a pavise shaped like the gate. The base carries the look; there are no decorations.</li>
 * <li>Broodkeeper (Magic 50): the keepers of the Silkwood broodmother's brood, half become spiders, on their own base (BaseCarapace): a chitin mask-hood with eight glowing eyes over a face of shadow and mandible hooks at the chin, a robe-tunic with chitin lames under a glossy carapace with eight long jointed spider legs arching over the shoulders, a swollen egg-sac abdomen at the small of the back, a skirt of torn silk and web, pointed chitin boots and long clawed chitin fingers. The base carries the look; there are no decorations.</li>
 * <li>Rimeclaw hunter (Ranged 60): Hunters who took Rimeclaw (the Frostfang boss) and wear its hide, on a parka base of their own (BaseParka), round and bundled up: a deep hood in a huge white fur ruff with bone snow-goggles; a puffy quilted ice-blue parka to mid-thigh with a fur hem, Rimeclaw's hooked claws on the shoulders and round the neck; fur-lined trousers; broad-soled mukluks; clawed mittens; frost on the fur. The base carries it all, so it has no decorations.</li>
 * </ol>
 */
final class DraftArmourB {

	private DraftArmourB() {
	}

	/** Draft pieces have no item ids: 0 marks a piece the set has, -1 one it doesn't. */
	static final int[] HAS_ALL = { 0, 0, 0, 0, 0, 0 }, NO_SHIELD = { 0, 0, 0, -1, 0, 0 };

	/* Colours the sets share with their sources: antler, the Gravecoil's verdigris and bone, chitin's sheen, Rimeclaw's claws. */
	static final int ANTLER = hsl(7, 1, 94), ANTLER_DARK = hsl(7, 1, 72), LEAF = hsl(16, 4, 50), BRONZE = hsl(7, 4, 46), BRONZE_DARK = hsl(7, 4, 32),
			VERDIGRIS = hsl(25, 2, 46), BELLY = hsl(9, 2, 72), CHITIN = hsl(48, 1, 17), CHITIN_LIGHT = hsl(48, 1, 32), CHITIN_DARK = hsl(48, 1, 9),
			CLAW = hsl(8, 1, 104), CLAW_DARK = hsl(8, 1, 80);

	static final Style[] SETS = {
			new Style("Wildgrove", "wildgrove", Kind.MANTLE, Motif.WILDGROVE, 40, hsl(19, 3, 34), hsl(19, 3, 22), hsl(18, 3, 48), hsl(6, 3, 36), hsl(6, 3, 24),
					hsl(10, 6, 90), hsl(11, 4, 116), hsl(6, 3, 28), hsl(6, 3, 18), NO_SHIELD,
					new String[] { "Wildgrove hood", "Wildgrove mantle top", "Wildgrove leggings", null, "Wildgrove shoes", "Wildgrove handwraps" },
					new String[] { "A cowl wreathed in leaves, with antlers grown up through it. The stag was asked first, apparently.",
							"A leaf-collared mantle over a druid's tunic, ivy climbing the front. It's still growing, slowly, towards the light.",
							"Cross-bound leggings under leaf-hemmed flaps. Hob's woodcutters stop where the Wildgrove starts.", null,
							"Bark-bound wrapped shoes. They leave no tracks, and now and then a flower.", "Leaf-cuffed handwraps, for turning soil and the occasional spell." }),
			new Style("Gravescale", "gravescale", Kind.COAT, Motif.GRAVESCALE, 45, hsl(14, 2, 38), hsl(15, 2, 26), hsl(14, 2, 50), hsl(9, 2, 86), hsl(9, 2, 64),
					hsl(27, 6, 104), hsl(27, 4, 122), hsl(15, 2, 22), hsl(15, 2, 15), HAS_ALL,
					new String[] { "Gravescale serpent helm", "Gravescale coil shirt", "Gravescale banded greaves", "Gravescale coil shield", "Gravescale boots",
							"Gravescale fang gauntlets" },
					new String[] { "A Gravecoil's head, jaws open, hood spread. You look out through its mouth. It was looking out through it first.",
							"A scale shirt with the rest of the Gravecoil still wound round it, its tail hanging down behind. It tightens a little when you breathe in.",
							"Greaves of Gravecoil segments, banded like the serpent, a barrow-king's bronze ring at each knee.",
							"A Gravecoil, coiled flat, head raised over the rim. It looks asleep. They always look asleep.",
							"Scaled boots with a bone point. The Sunken Barrow's water never quite drains out of them.",
							"Scaled gauntlets with a Gravecoil's fangs for fingers. Mind how you scratch." }),
			new Style("Gatehold sentinel", "sentinel", Kind.COAT, Motif.SENTINEL, 50, hsl(38, 1, 76), hsl(38, 1, 56), hsl(38, 0, 98), hsl(40, 5, 40), hsl(40, 5, 28),
					hsl(8, 5, 72), hsl(9, 2, 118), hsl(40, 3, 24), hsl(40, 3, 16), HAS_ALL,
					new String[] { "Gatehold sentinel full helm", "Gatehold sentinel platebody", "Gatehold sentinel tassets", "Gatehold sentinel pavise", "Gatehold sentinel sabatons",
							"Gatehold sentinel gauntlets" },
					new String[] { "A battlemented great helm with a portcullis for a visor, and a plume you can see from the far end of Gatehold's wall. That's the point of it.",
							"Square steel under Gatehold blue, a tower on each shoulder and the gate-tower on the breast in gold. The sentinel is the wall's last tower.",
							"Plate for the wall walk, with a portcullis hung over each thigh. It comes down when it has to.",
							"A gate you carry: blue planks, iron bands, the gate-tower in gold. Anything that gets past it still has the real one to get past.",
							"Square steel sabatons, worn flat on the wall walk.", "Square-cuffed gauntlets rimmed in gold. They've held the gate shut more than once." }),
			new Style("Broodkeeper", "broodkeeper", Kind.MANTLE, Motif.BROOD, 50, hsl(51, 2, 42), hsl(51, 2, 28), hsl(51, 1, 56), CHITIN, CHITIN_DARK, hsl(14, 5, 80),
					hsl(14, 3, 112), CHITIN, CHITIN_DARK, NO_SHIELD,
					new String[] { "Broodkeeper mask", "Broodkeeper carapace top", "Broodkeeper web skirt", null, "Broodkeeper chitin boots", "Broodkeeper claws" },
					new String[] { "A chitin mask with nothing to see behind it but shadow, and eight eyes above. They're all open. They're all yours now.",
							"A robe under a spider's carapace, eight legs over the shoulders and the brood's egg-sac on the back. The broodmother's keepers never set it down.",
							"A skirt of torn silk, web strung between the strands. Something in the Silkwood finishes it for you overnight.", null,
							"Chitin boots that end in a point, like a leg tip. They make no sound on silk.",
							"Chitin cuffs and long jointed claws. The keepers handle the eggs gently, and everything else less so." }),
			new Style("Rimeclaw hunter", "rimeclaw", Kind.WRAP, Motif.RIMECLAW, 60, hsl(36, 3, 74), hsl(36, 3, 54), hsl(36, 2, 94), hsl(38, 3, 36), hsl(38, 3, 24),
					CLAW, hsl(36, 2, 120), hsl(36, 2, 34), hsl(36, 2, 22), NO_SHIELD,
					new String[] { "Rimeclaw hunter hood", "Rimeclaw hunter parka top", "Rimeclaw hunter trousers", null, "Rimeclaw hunter mukluks", "Rimeclaw hunter mittens" },
					new String[] { "A deep parka hood in a ruff of Rimeclaw's fur, bone goggles against the Frostfang glare. The frost on the fur has never melted.",
							"Rimeclaw's hide, quilted thick, its claws on the shoulders and round the neck. Wearing it is like being hugged by something that lost.",
							"Fur-lined hide trousers. The fur is on the inside, where it can do some good, and rolls out at the knee.", null,
							"Wide mukluks on broad soles, for walking on top of the snow instead of in it. The Frostfang ice gives them a wide berth.",
							"Mittens with Rimeclaw's claws for fingertips. It tried them out on the hunters first." }),
	};

	/** Each set's weapon, in set order (id 0: not in the game). */
	static final Rung[] WEAPONS = {
			new Rung(0, "Antler staff", "staff", 4000, "An antler crown on a living branch, a golden flower held in it. The Wildgrove grows its staves; nobody cuts them.", DraftArmourB::antlerStaff, STAFF),
			new Rung(0, "Coilfang spear", "spear", 9000, "A Gravecoil's fang on bog oak, the rest of the Gravecoil wound round the haft. It's dead. Almost certainly.", DraftArmourB::coilfangSpear, SPEAR),
			new Rung(0, "Sentinel halberd", "halberd", 15000, "Bright steel on Gatehold blue, with the gate-tower for a socket. The sentinels say it's for keeping things out, and it is.", DraftArmourB::sentinelHalberd, HALBERD),
			new Rung(0, "Brood wand", "wand", 14000, "A broodmother's leg, its claws closed round three glowing eggs. Something in them is waiting to be useful.", DraftArmourB::broodWand, WAND),
			new Rung(0, "Rimeclaw shortbow", "shortbow", 30000, "Rimeclaw's hide over horn, its claws for nocks. Every draw creaks like the ice in Frostfang.", () -> rimeclawBow(1.0), () -> rimeclawBow(2.4), SHORTBOW),
	};

	/** Whether this file builds the set. */
	static boolean owns(Style st) {
		return st.motif == Motif.WILDGROVE || st.motif == Motif.GRAVESCALE || st.motif == Motif.SENTINEL || st.motif == Motif.BROOD || st.motif == Motif.RIMECLAW;
	}

	/** The Gravescale shield is dark hide rimmed in the kings' bronze. */
	static int shieldFace(Style st) {
		return st.motif == Motif.GRAVESCALE ? st.dark : st.main;
	}

	static int shieldRim(Style st) {
		return st.motif == Motif.GRAVESCALE ? BRONZE : st.dark;
	}

	/* ------------------------------------------------------------- the helm */

	/** What's added to the helm (the base mesh, recoloured, is already there). */
	static ModelData helm(Style st, ModelData m) {
		int[] bb = m.bounds();
		double hw = bb[3], top = bb[1], bottom = bb[4], h = bottom - top, front = bb[2], back = bb[5], cz = (front + back) / 2, dz = (back - front) / 2;
		ModelData add = new ModelData();
		Frame F = ArmourSets.FRONT;
		switch (st.motif) {
		case WILDGROVE: { // a leafy cowl: a wreath of leaves round the face, antlers grown through the hood, golden flowers at the brow
			double oy = top + h * 0.45, ow = hw * 0.66, oh = h * 0.3;
			for (int side = -1; side <= 1; side += 2) {
				double[] r = side(m, side, top + h * 0.2, cz - dz * 0.1, hw);
				double[] a = plus(r, new double[] { side * 5, -9, 1 }), b = plus(a, new double[] { side * 5, -9, 2 });
				blade(add, plus(r, new double[] { -side * 1.5, 0, 0 }), a, Z, 2.0, 1.5, ANTLER);
				blade(add, a, b, Z, 1.6, 1.2, ANTLER_DARK);
				double[] t = lerp(r, a, 0.75);
				blade(add, t, plus(t, new double[] { side * 1, -8, -3 }), Z, 1.2, 0.9, ANTLER);
				double[] t2 = lerp(a, b, 0.6);
				blade(add, t2, plus(t2, new double[] { side * 5, -3, 1 }), Z, 1.0, 0.8, ANTLER);
			}
			for (int k = 0; k < 10; k++) { // the wreath, round the lip
				double a = Math.PI * 2 * k / 10 + 0.3;
				double ca = Math.cos(a), sa = Math.sin(a);
				double[] p = project(m, F, ca * ow, oy + sa * oh);
				blade(add, plus(p, new double[] { 0, 0, -0.4 }), plus(p, new double[] { ca * 5.5, sa * 5.5, -1.8 }), new double[] { -sa, ca, 0 }, 2.1, 0.5,
						k % 2 == 0 ? st.light : LEAF);
			}
			for (int k = -1; k <= 1; k++) {
				double[] p = project(m, F, k * 4.2, oy - oh - 1.5 + Math.abs(k) * 1.8);
				flower(add, plus(p, new double[] { 0, 0, -0.8 }), new double[] { 0, -0.3, -1 }, 2.3, st.accent, st.core);
			}
			break;
		}
		case GRAVESCALE: // BaseSerpent carries the whole look
			break;
		case SENTINEL: // BaseBastion carries the whole look
			break;
		case BROOD: // BaseCarapace carries the whole look
			break;
		default:
			break;
		}
		return add;
	}

	/* ------------------------------------------------------------- the body */

	static ModelData body(Style st, ModelData m) {
		int[] bb = m.bounds();
		double hw = bb[3], top = bb[1], bottom = bb[4], h = bottom - top, front = bb[2], back = bb[5], cz = (front + back) / 2;
		double cy = top + h * 0.42, sx = hw * 0.7, sy = top + h * 0.12;
		ModelData add = new ModelData();
		Frame F = ArmourSets.FRONT, B = ArmourSets.BACK;
		switch (st.motif) {
		case WILDGROVE: { // on the mantle: a collar of leaves, ivy climbing the tunic with golden flowers, an antler toggle on the bark sash, leaves down the cape
			Mantle mm = new Mantle(m);
			for (Frame f : new Frame[] { F, B }) {
				for (int k = -2; k <= 2; k++) {
					double[] r = plus(project(m, f, k * 4.0, mm.neck - 1 + Math.abs(k) * 1.0), scale(f.n, 0.6));
					double[] t = plus(project(m, f, k * 7.5, mm.neck + 9 - Math.abs(k) * 0.5), scale(f.n, 2.0));
					blade(add, r, t, X, 3.8, 0.7, (k + 2) % 2 == 0 ? st.light : LEAF);
				}
			}
			double[][] vine = { { -2.5, mm.waist - 1 }, { 2.8, lerp(mm.waist, mm.neck, 0.22) }, { -2.2, lerp(mm.waist, mm.neck, 0.45) }, { 2.0, lerp(mm.waist, mm.neck, 0.66) },
					{ -0.5, lerp(mm.waist, mm.neck, 0.84) } };
			for (int i = 0; i + 1 < vine.length; i++) {
				decal(add, m, F, LowArmour.bar(vine[i][0], vine[i][1], vine[i + 1][0], vine[i + 1][1], 0.8), 0.4, 1.8, st.dark, st.dark);
				double[] p = project(m, F, vine[i + 1][0], vine[i + 1][1]);
				int side = vine[i + 1][0] > 0 ? 1 : -1;
				blade(add, plus(p, new double[] { 0, 0, -1.2 }), plus(p, new double[] { side * 4.5, -2, -2.2 }), Y, 2.0, 0.5, LEAF);
			}
			for (int i : new int[] { 2 }) {
				double[] p = project(m, F, vine[i][0], vine[i][1]);
				flower(add, plus(p, new double[] { 0, 0, -1.0 }), new double[] { 0, 0, -1 }, 2.2, st.accent, st.core);
			}
			decal(add, m, F, diamond(4.5, mm.waist - 2.5, 1.6, 3.4), 0.4, 3.2, ANTLER, ANTLER_DARK);
			// ivy leaves trailing down the cape, each its own sprig so it rides its own row of the cape
			double[][] sprigs = { { -6, mm.capeHip - 2 }, { 5, lerp(mm.capeHip, mm.hem, 0.4) }, { -6, lerp(mm.capeHip, mm.hem, 0.7) }, { 4, mm.hem - 7 } };
			for (int i = 0; i < sprigs.length; i++) {
				double[] p = plus(project(m, B, sprigs[i][0], sprigs[i][1]), new double[] { 0, 0, 0.6 });
				int side = i % 2 == 0 ? 1 : -1;
				blade(add, p, plus(p, new double[] { side * 4.5, 2.5, 1.2 }), Y, 2.2, 0.5, i % 2 == 0 ? LEAF : st.light);
			}
			break;
		}
		case GRAVESCALE: // BaseSerpent carries the whole look
			break;
		case SENTINEL: // BaseBastion carries the whole look
			break;
		case BROOD: // BaseCarapace carries the whole look
			break;
		default:
			break;
		}
		return add;
	}

	/* ------------------------------------------------------------- the legs */

	/** Right knee, left knee and the front (any may be empty). */
	static ModelData[] legs(Style st, ModelData m) {
		int[] bb = m.bounds();
		double hw = bb[3], top = bb[1], bottom = bb[4], h = bottom - top, front = bb[2], back = bb[5], cz = (front + back) / 2;
		double kneeY = top + h * 0.55, kneeX = hw * 0.53;
		ModelData[] knees = { new ModelData(), new ModelData() };
		ModelData fr = new ModelData();
		Frame F = ArmourSets.FRONT, B = ArmourSets.BACK;
		for (int side = -1, i = 0; side <= 1; side += 2, i++) {
			double x = side * kneeX;
			ModelData one = knees[i];
			switch (st.motif) {
			case GRAVESCALE: // BaseSerpent carries the whole look
				break;
			case SENTINEL: // BaseBastion carries the whole look
				break;
			default:
				break;
			}
		}
		switch (st.motif) {
		case WILDGROVE: { // ivy trailing down the right front flap, and a hem of leaves on every flap
			double waist = BaseMantle.cen(m, BaseMantle.WAIST)[1];
			for (int s = 0; s < 2; s++) {
				double[] knee = BaseMantle.cen(m, BaseMantle.KNEE[s]);
				double x = BaseMantle.cen(m, BaseMantle.THIGH[s])[0], hem = knee[1] - 7;
				if (s == 0) {
					double[][] vine = { { x + 2, waist + 6 }, { x - 2, lerp(waist + 6, hem - 3, 0.33) }, { x + 2.2, lerp(waist + 6, hem - 3, 0.66) }, { x - 1, hem - 3 } };
					for (int i = 0; i + 1 < vine.length; i++) {
						decal(fr, m, F, LowArmour.bar(vine[i][0], vine[i][1], vine[i + 1][0], vine[i + 1][1], 0.8), 0.4, 1.8, st.dark, st.dark);
						double[] p = project(m, F, vine[i + 1][0], vine[i + 1][1]);
						int side = i % 2 == 0 ? 1 : -1;
						blade(fr, plus(p, new double[] { 0, 0, -1.2 }), plus(p, new double[] { side * 4.5, -2, -2.2 }), Y, 2.0, 0.5, LEAF);
					}
					double[] p = project(m, F, vine[1][0], vine[1][1]);
					flower(fr, plus(p, new double[] { 0, 0, -1.0 }), new double[] { 0, 0, -1 }, 2.2, st.accent, st.core);
				}
				for (Frame f : new Frame[] { F }) {
					for (int k = -1; k <= 1; k++) {
						double[] p = project(m, f, x + k * 4.2, hem - 1.5);
						blade(fr, p, plus(plus(p, scale(f.n, 1.2)), new double[] { k * 1.2, 6.5 + (k == 0 ? 1.5 : 0), 0 }), X, 2.2, 0.5, (k + 1) % 2 == 0 ? st.light : LEAF);
					}
				}
			}
			break;
		}
		case GRAVESCALE: // BaseSerpent carries the whole look
			break;
		case SENTINEL: // BaseBastion carries the whole look
			break;
		case BROOD: // BaseCarapace carries the whole look
			break;
		default:
			break;
		}
		return new ModelData[] { knees[0], knees[1], fr };
	}

	/* ------------------------------------------------------------- the shield */

	static ModelData shield(Style st, ModelData m, Frame f) {
		double[] e = LowArmour.extent(m, f); // u0, u1, v0, v1
		double w = (e[1] - e[0]) / 2, hgt = (e[3] - e[2]) / 2, cu = (e[0] + e[1]) / 2, cv = (e[2] + e[3]) / 2;
		double s = Math.min(w, hgt);
		ModelData add = new ModelData();
		switch (st.motif) {
		case GRAVESCALE: // BaseSerpent carries the whole look
			break;
		case SENTINEL: // BaseBastion carries the whole look
			break;
		default:
			break;
		}
		return add;
	}

	/* ------------------------------------------------------------- boots, gloves */

	static ModelData boots(Style st, ModelData m) {
		int[] bb = m.bounds();
		double hw = bb[3], front = bb[2], top = bb[1];
		ModelData add = new ModelData();
		for (int side = -1; side <= 1; side += 2) {
			double x = side * hw * 0.56;
			switch (st.motif) {
			case WILDGROVE: { // a bark cuff round the top of the shoe, two leaves tucked in it
				double[] c = BaseMantle.cen(m, BaseMantle.ANKLE[side < 0 ? 0 : 1]);
				add.tube(new double[] { c[0], top - 0.5, c[2] + 0.5 }, new double[] { c[0], top + 3, c[2] + 0.5 }, 7.0, 6.8, 6, 0.26, st.trim, st.trimDark);
				for (int k = -1; k <= 1; k += 2) {
					double[] p = { c[0] + k * 2.5, top + 1, c[2] - 6 };
					blade(add, p, plus(p, new double[] { k * 2, -6, -2 }), X, 1.8, 0.4, k < 0 ? st.light : st.main);
				}
				break;
			}
			case GRAVESCALE: // BaseSerpent carries the whole look
				break;
			case SENTINEL: // BaseBastion carries the whole look
				break;
			case BROOD: // BaseCarapace carries the whole look
				break;
			default:
				break;
			}
		}
		return add;
	}

	static ModelData gloves(Style st, ModelData m) {
		int[] bb = m.bounds();
		double hw = bb[3], top = bb[1];
		ModelData add = new ModelData();
		for (int side = -1; side <= 1; side += 2) {
			double x = side * (hw - 6.5);
			switch (st.motif) {
			case WILDGROVE: { // a bark cuff with a ring of leaves, at the top of the wraps
				double[] c = cuffTop(m, side, top);
				add.tube(new double[] { c[0], top - 0.5, c[2] }, new double[] { c[0], top + 2.5, c[2] }, 7.4, 7.4, 6, 0.26, st.trim, st.trimDark);
				for (int k = 0; k < 4; k++) {
					double a = Math.PI * 2 * k / 4 + 0.4;
					double[] p = { c[0] + Math.cos(a) * 7, top + 1, c[2] + Math.sin(a) * 7 };
					blade(add, p, plus(p, new double[] { Math.cos(a) * 1.5, 6, Math.sin(a) * 1.5 }), new double[] { -Math.sin(a), 0, Math.cos(a) }, 1.8, 0.4,
							k % 2 == 0 ? st.light : st.main);
				}
				break;
			}
			case GRAVESCALE: // BaseSerpent carries the whole look
				break;
			case SENTINEL: // BaseBastion carries the whole look
				break;
			case BROOD: // BaseCarapace carries the whole look
				break;
			default:
				break;
			}
		}
		return add;
	}

	/* ------------------------------------------------------------- shapes */

	/** Landmarks of a mantle top (BaseMantle), from its own labels: neck, waist, the cape's hip row and hem, the pelerine's hem, the shoulders. */
	static final class Mantle {
		final double neck, waist, capeHip, hem, pelerineHem, shoulderX;

		Mantle(ModelData m) {
			neck = BaseMantle.cen(m, BaseMantle.NECK)[1];
			waist = BaseMantle.cen(m, BaseMantle.CAPE_WAIST)[1] + 3;
			capeHip = BaseMantle.cen(m, BaseMantle.CAPE_HIP)[1];
			hem = m.bounds()[4];
			pelerineHem = (BaseMantle.cen(m, BaseMantle.ELBOW[0])[1] + BaseMantle.cen(m, BaseMantle.ELBOW[1])[1]) / 2 - 9;
			shoulderX = BaseMantle.cen(m, BaseMantle.SHOULDER[1])[0] + 4;
		}
	}

	/** Where a ray from the side ({@code side} -1 / 1) at (y, z) meets the mesh (or the bounds' edge). */
	static double[] side(ModelData m, int side, double y, double z, double hw) {
		double[] h = surface(m, new double[] { side * 150, y, z }, new double[] { -side, 0, 0 });
		return h == null ? new double[] { side * hw, y, z } : h;
	}

	/** Where a ray straight down at depth z meets the mesh. */
	static double[] crown(ModelData m, double z, double top) {
		double[] h = surface(m, new double[] { 0, top - 60, z }, new double[] { 0, 1, 0 });
		return h == null ? new double[] { 0, top, z } : h;
	}

	/** The centre of the top of one glove's cuff: the mean of its vertices within 2 of the top on that side. */
	static double[] cuffTop(ModelData m, int side, double top) {
		double[] c = new double[3];
		int n = 0;
		for (int[] v : m.vertices) {
			if (v[1] <= top + 2 && Math.signum(v[0]) == side) {
				c[0] += v[0];
				c[1] += v[1];
				c[2] += v[2];
				n++;
			}
		}
		return scale(c, 1.0 / Math.max(1, n));
	}

	static double[] plus(double[] a, double[] b) {
		return new double[] { a[0] + b[0], a[1] + b[1], a[2] + b[2] };
	}

	static double lerp(double a, double b, double t) {
		return a + (b - a) * t;
	}

	static double[] lerp(double[] a, double[] b, double t) {
		return new double[] { a[0] + (b[0] - a[0]) * t, a[1] + (b[1] - a[1]) * t, a[2] + (b[2] - a[2]) * t };
	}

	/** The point on a mesh's outer surface at angle {@code a} round the vertical axis through z = cz, height y, pushed out by {@code out}. */
	static double[] skin(ModelData m, double a, double y, double cz, double out) {
		double ca = Math.cos(a), sa = Math.sin(a);
		double[] hit = surface(m, new double[] { ca * 150, y, cz + sa * 150 }, new double[] { -ca, 0, -sa });
		return hit == null ? null : plus(hit, new double[] { ca * out, 0, sa * out });
	}

	/** A small flower facing {@code n}: a flared cup of petals and a bright centre. */
	static void flower(ModelData add, double[] p, double[] n, double r, int petal, int core) {
		add.tube(p, plus(p, scale(n, 1.0)), r * 0.5, r, 5, 0, petal, petal);
		add.tube(plus(p, scale(n, 1.0)), plus(p, scale(n, 1.8)), r * 0.4, 0, 4, 0, core, core);
	}

	/** An egg-sac hanging from its top at c: long {@code len}, widest {@code r} low down. */
	static void egg(ModelData add, double[] c, double r, double len, int col, int col2) {
		double[] mid = plus(c, new double[] { 0, len * 0.6, 0 }), bot = plus(c, new double[] { 0, len, 0 });
		add.tube(c, mid, r * 0.35, r, 6, 0, col, col);
		add.tube(mid, bot, r, 0, 6, 0, col2, col2);
	}

	/** Gatehold's gate-tower: a crenellated tower with a dark arched gate at its foot. */
	static void tower(ModelData add, ModelData m, Frame f, double x, double y, double size, int col, int col2, int door, double lift) {
		double s = size;
		decal(add, m, f, rect(x - s * 0.3, y - s * 0.24, x + s * 0.3, y + s * 0.5), 0.5, 2.0 * lift, col, col2);
		decal(add, m, f, rect(x - s * 0.4, y - s * 0.36, x + s * 0.4, y - s * 0.22), 0.5, 2.3 * lift, col, col2);
		for (int k = -1; k <= 1; k++) {
			double xi = x + k * s * 0.3;
			decal(add, m, f, rect(xi - s * 0.08, y - s * 0.5, xi + s * 0.08, y - s * 0.35), 0.5, 2.3 * lift, col, col2);
		}
		decal(add, m, f, new double[][] { { x - s * 0.13, y + s * 0.5 }, { x - s * 0.13, y + s * 0.26 }, { x, y + s * 0.14 }, { x + s * 0.13, y + s * 0.26 }, { x + s * 0.13, y + s * 0.5 } },
				0.3, 2.0 * lift + 0.6, door, door);
		decal(add, m, f, rect(x - s * 0.04, y - s * 0.12, x + s * 0.04, y + s * 0.04), 0.3, 2.0 * lift + 0.5, door, door);
	}

	/** Half the width of a mesh at height y: the furthest vertex from the middle within {@code band} of it. */
	static double halfWidth(ModelData m, double y, double band) {
		double w = 0;
		for (int[] v : m.vertices) {
			if (Math.abs(v[1] - y) <= band) {
				w = Math.max(w, Math.abs(v[0]));
			}
		}
		return w;
	}

	/** A double cone from p0 to p1, widest ({@code r}) a little under halfway: a spider's body, a seed, a pod. */
	static void pod(ModelData add, double[] p0, double[] p1, double r, int col, int col2) {
		double[] mid = lerp(p0, p1, 0.45);
		add.tube(p0, mid, 0, r, 6, 0, col, col);
		add.tube(mid, p1, r, 0, 6, 0, col2, col2);
	}

	/* ====================================================================== */
	/*                                WEAPONS                                 */
	/* ====================================================================== */

	/** The Wildgrove druids' staff: a gnarled branch wound with ivy, an antler crown at the top holding a golden flower. */
	static ModelData antlerStaff() {
		ModelData m = new ModelData();
		int bark = hsl(6, 3, 34), barkDark = hsl(6, 3, 22), ivy = hsl(19, 3, 30), leaf = hsl(18, 3, 48), petal = hsl(10, 6, 90), heart = hsl(11, 4, 116);
		tube(m, f(4, 0, 0), f(72, 1.4, 0.4), 3.4, 3.8, 5, 0, bark, bark);
		tube(m, f(72, 1.4, 0.4), f(146, -1.2, -0.3), 3.8, 3.4, 5, 0.4, barkDark, barkDark);
		tube(m, f(146, -1.2, -0.3), f(204, 0.4, 0), 3.4, 4.0, 5, 0.1, bark, bark);
		tube(m, f(-2, 0, 0), f(4, 0, 0), 2.0, 3.6, 5, 0, barkDark, barkDark);
		band(m, 110, 128, 4.2, hsl(6, 3, 18));
		// ivy wound up the top of the staff, with leaves
		int n = 12;
		for (int i = 0; i < n; i++) {
			double s0 = 140 + i * 5.5, s1 = s0 + 5.5, a0 = i * 1.1, a1 = (i + 1) * 1.1;
			tube(m, f(s0, Math.cos(a0) * 4.0, Math.sin(a0) * 4.0), f(s1, Math.cos(a1) * 4.0, Math.sin(a1) * 4.0), 0.8, 0.8, 3, 0, ivy, ivy);
			if (i % 3 == 1) {
				tube(m, f(s1, Math.cos(a1) * 4.0, Math.sin(a1) * 4.0), f(s1 - 3, Math.cos(a1) * 9, Math.sin(a1) * 9), 1.8, 0, 3, a1, leaf, leaf);
			}
		}
		// the antler crown: two beams spreading from a bark knot, each with two tines
		collar(m, 198, 208, 4.0, 5.2, barkDark);
		for (int side = -1; side <= 1; side += 2) {
			double[][] beam = { { 204, side * 3 }, { 217, side * 12 }, { 235, side * 16 }, { 252, side * 11 } };
			for (int i = 0; i + 1 < beam.length; i++) {
				tube(m, f(beam[i][0], beam[i][1], 0), f(beam[i + 1][0], beam[i + 1][1], 0), 3.4 - i * 0.7, 2.8 - i * 0.7 - (i == 2 ? 1.4 : 0), 4, Math.PI / 4,
						i % 2 == 0 ? ANTLER : ANTLER_DARK, ANTLER);
			}
			tube(m, f(215, side * 11, 0), f(224, side * 22, 0), 2.2, 0, 4, Math.PI / 4, ANTLER, ANTLER);
			tube(m, f(232, side * 16, 0), f(243, side * 24, 0), 1.8, 0, 4, Math.PI / 4, ANTLER, ANTLER);
		}
		// a golden flower held in the crown
		double cs = 224;
		for (int k = 0; k < 5; k++) {
			double a = Math.PI * 2 * k / 5 + Math.PI / 2;
			tube(m, f(cs, 0, 0), f(cs + Math.sin(a) * 8.5, Math.cos(a) * 8.5, 0), 3.4, 0, 4, 0, petal, petal);
		}
		gem(m, cs, 0, 2.8, 3.4, heart, heart);
		tube(m, f(208, 0, 0), f(cs, 0, 0), 1.0, 1.0, 3, 0, ivy, ivy);
		return m;
	}

	/** The Gravescale spear: a Gravecoil coiled up a bog-oak haft, its fang for the blade, bronze rings and ghost-light. */
	static ModelData coilfangSpear() {
		ModelData m = new ModelData();
		int oak = hsl(6, 3, 20), hide = hsl(14, 2, 40), hideLight = hsl(14, 2, 52), hideDark = hsl(15, 2, 26), belly = hsl(9, 2, 84), bronze = hsl(7, 4, 46), bronzeDark = hsl(7, 4, 32),
				bone = hsl(9, 2, 92), boneEdge = hsl(9, 1, 116), eye = hsl(27, 6, 104);
		haft(m, 6, 216, 2.9, 2.6, oak);
		tube(m, f(0, 0, 0), f(6, 0, 0), 1.2, 3.1, 4, Math.PI / 4, bronzeDark, bronzeDark);
		band(m, 108, 136, 3.4, LEATHER);
		for (double s : new double[] { 104, 136 }) {
			band(m, s, s + 3, 3.7, bronze);
		}
		// the serpent, coiled up the haft from the grip to the socket
		int n = 16;
		double s0 = 144, s1 = 206, turns = 1.75;
		double[] prev = null;
		for (int i = 0; i <= n; i++) {
			double t = (double) i / n, a = t * Math.PI * 2 * turns, s = s0 + (s1 - s0) * t, r = 5.2;
			double[] p = f(s, Math.cos(a) * r, Math.sin(a) * r);
			if (prev != null) {
				double rad = 1.3 + t * 1.6;
				tube(m, prev, p, rad - 0.07, rad, 4, a, i % 3 == 0 ? belly : i % 2 == 0 ? hide : hideLight, hide);
			}
			prev = p;
		}
		// its head, resting on the socket, ghost-lit eyes
		double a = Math.PI * 2 * turns;
		tube(m, prev, f(214, Math.cos(a) * 4.4, Math.sin(a) * 4.4), 3.0, 3.4, 4, Math.PI / 4, hide, hide);
		tube(m, f(214, Math.cos(a) * 4.4, Math.sin(a) * 4.4), f(223, Math.cos(a) * 4.0, Math.sin(a) * 4.0), 3.4, 1.0, 4, Math.PI / 4, hideDark, belly);
		gem(m, 216, Math.cos(a) * 4.6, 1.3, 4.0, eye, eye);
		// the bronze socket, ringed in verdigris
		collar(m, 210, 222, 3.0, 4.0, bronzeDark);
		collar(m, 218, 221, 4.2, 4.2, VERDIGRIS);
		// the fang: long, curved, bone, a groove of ghost-light down it
		double[][] fang = { { 221, -3.6 }, { 240, -4.4 }, { 258, -3.4 }, { 274, -0.6 }, { 286, 3.4 }, { 268, 4.0 }, { 248, 4.6 }, { 228, 4.2 }, { 221, 3.6 } };
		plateSW(m, fang, new double[] { 2.2, 0, 0, 0, 0, 0, 0, 0, 2.2 }, new double[] { 240, 0.4 }, 2.0, bone, boneEdge, hsl(9, 2, 70));
		tube(m, f(226, 0.4, 0), f(262, 0.8, 0), 0.9, 0.5, 3, 0, eye, eye);
		return m;
	}

	/** The sentinel's halberd: a bright steel crescent and spike over a gold tower-socket, a Gatehold-blue tassel and haft. */
	static ModelData sentinelHalberd() {
		ModelData m = new ModelData();
		int plume = hsl(40, 0, 112), blue = hsl(40, 5, 40), blueDark = hsl(40, 5, 26), gold = hsl(8, 5, 72), goldDark = hsl(8, 5, 46), steel = hsl(38, 1, 80), steelDark = hsl(38, 1, 58),
				edge = hsl(38, 0, 116);
		haft(m, 8, 204, 3.2, 2.9, blueDark);
		tube(m, f(0, 0, 0), f(9, 0, 0), 1.6, 3.8, 4, Math.PI / 4, steelDark, steelDark);
		band(m, 104, 134, 3.6, LEATHER);
		for (double s : new double[] { 60, 102, 134 }) {
			collar(m, s, s + 2, 3.6, 3.6, gold);
		}
		// the tower: a gold socket below the head, crenellated at the top, a blue window in it
		collar(m, 146, 162, 3.4, 5.0, goldDark);
		collar(m, 162, 166, 5.8, 5.8, gold);
		for (int k = 0; k < 4; k++) {
			double a = Math.PI * 2 * k / 4 + Math.PI / 4;
			tube(m, f(166, Math.cos(a) * 4.6, Math.sin(a) * 4.6), f(170.5, Math.cos(a) * 4.6, Math.sin(a) * 4.6), 1.7, 1.7, 4, a + Math.PI / 4, gold, gold);
		}
		gem(m, 155, 0, 1.6, 4.6, blue, blue);
		haft(m, 166, 214, 3.8, 3.4, steelDark);
		// a tassel of Gatehold blue hanging from the tower
		for (int k = 0; k < 4; k++) {
			double a = Math.PI * 2 * k / 4;
			tube(m, f(148, 0, 0), f(131 - (k % 2) * 3, Math.cos(a) * 4.4, Math.sin(a) * 4.4), 2.6, 0.5, 3, a, k % 2 == 0 ? plume : gold, plume);
		}
		// the crescent axe (+w), the back hook (-w), the spike
		double[][] blade = { { 184, 3 }, { 212, 3 }, { 226, 16 }, { 232, 30 }, { 216, 34 }, { 196, 36 }, { 176, 33 }, { 170, 20 } };
		plateSW(m, blade, new double[] { 2.2, 2.2, 0, 0, 0, 0, 0, 0 }, new double[] { 198, 18 }, 2.0, steel, edge, steelDark);
		gem(m, 200, 16, 2.2, 2.6, gold, gold);
		tube(m, f(198, -3, 0), f(196, -13, 0), 3.0, 2.0, 4, Math.PI / 4, steel, steelDark);
		tube(m, f(196, -13, 0), f(206, -22, 0), 2.0, 0, 4, Math.PI / 4, steel, steelDark);
		bladeStrip(m, new double[][] { { 212, 3.6, 2.8 }, { 222, 4.4, 2.4 } }, 252, steel, edge);
		return m;
	}

	/** The broodkeepers' wand: a jointed chitin leg, its claws curled round three sickly glowing egg-sacs, small eyes up the shaft. */
	static ModelData broodWand() {
		ModelData m = new ModelData();
		int glow = hsl(14, 5, 80), glowCore = hsl(14, 3, 112), wrap = hsl(52, 2, 30);
		double[][] seg = { { 0, 0 }, { 26, 0.6 }, { 50, -0.5 }, { 72, 0.6 }, { 84, 0 } };
		for (int i = 0; i + 1 < seg.length; i++) {
			tube(m, f(seg[i][0], seg[i][1], 0), f(seg[i + 1][0], seg[i + 1][1], 0), 2.2 - i * 0.25, 1.9 - i * 0.25, 5, 0, i % 2 == 0 ? CHITIN : CHITIN_LIGHT, CHITIN);
			band(m, seg[i + 1][0] - 1.2, seg[i + 1][0] + 1.2, 2.6 - i * 0.25, CHITIN_LIGHT);
		}
		tube(m, f(-6, 1.5, 0), f(0, 0, 0), 0, 2.2, 4, 0, CHITIN_DARK, CHITIN_DARK);
		band(m, 12, 28, 2.6, wrap);
		for (double[] e : new double[][] { { 40, 1.1 }, { 52, -1.0 }, { 64, 0.9 } }) {
			gem(m, e[0], e[1], 0.9, 2.6, glow, glow);
		}
		// four hooked claws curled round the egg cluster
		for (int k = 0; k < 4; k++) {
			double a = Math.PI * 2 * k / 4 + Math.PI / 4;
			double[] p0 = f(82, Math.cos(a) * 1.6, Math.sin(a) * 1.6), p1 = f(91, Math.cos(a) * 7.5, Math.sin(a) * 7.5), p2 = f(103, Math.cos(a) * 2.0, Math.sin(a) * 2.0);
			tube(m, p0, p1, 1.3, 1.0, 3, a, CHITIN_LIGHT, CHITIN);
			tube(m, p1, p2, 1.0, 0, 3, a, CHITIN, CHITIN);
		}
		for (int k = 0; k < 3; k++) {
			double a = Math.PI * 2 * k / 3;
			double[] c = f(92 + (k == 0 ? 2 : 0), Math.cos(a) * 2.0, Math.sin(a) * 2.0);
			double[] lo = f(86 + (k == 0 ? 2 : 0), Math.cos(a) * 1.4, Math.sin(a) * 1.4), hi = f(98 + (k == 0 ? 2 : 0), Math.cos(a) * 1.6, Math.sin(a) * 1.6);
			tube(m, lo, c, 1.0, 3.3, 6, 0, glow, glow);
			tube(m, c, hi, 3.3, 0, 6, 0, glowCore, glow);
		}
		return m;
	}

	/** The Rimeclaw hunters' bow: limbs of frosted hide over horn, Rimeclaw's own hooked claws for nocks and spurs. */
	static ModelData rimeclawBow(double bend) {
		ModelData m = new ModelData();
		int ice = hsl(36, 3, 74), iceDark = hsl(36, 3, 54), strap = hsl(38, 3, 30), frost = hsl(36, 2, 120);
		bow(m, 127, bend, new double[] { 1.6, 2.2, 3.0, 3.5, 3.8, 3.5, 3.0, 2.2, 1.6 }, ice, iceDark, strap, CLAW, frost);
		// the nocks: great hooked claws curling back over the tips
		double tw = 4 * bend;
		for (int end = 0; end < 2; end++) {
			double s = end == 0 ? 3 : 124, d = end == 0 ? -1 : 1;
			double[] p0 = f(s, tw, 0), p1 = f(s + d * 6, tw + 6, 0), p2 = f(s + d * 2, tw + 11, 0);
			tube(m, p0, p1, 2.0, 1.4, 4, Math.PI / 4, CLAW_DARK, CLAW);
			tube(m, p1, p2, 1.4, 0, 4, Math.PI / 4, CLAW, CLAW);
		}
		// hooked claw spurs off the back of each limb, frosted hide wraps beside them
		for (double s : new double[] { 30, 97 }) {
			double w = 8.6 * bend, d = s < 64 ? 1 : -1;
			double[] p0 = f(s, w, 0), p1 = f(s + d * 4, w + 6, 0), p2 = f(s + d * 9, w + 7.5, 0);
			tube(m, p0, p1, 1.9, 1.3, 4, Math.PI / 4, CLAW_DARK, CLAW);
			tube(m, p1, p2, 1.3, 0, 4, Math.PI / 4, CLAW, CLAW);
			tube(m, f(s - d * 6 - 1.6, w - 0.4, 0), f(s - d * 6 + 1.6, w - 0.4, 0), 3.9, 3.9, 4, Math.PI / 4, frost, frost);
		}
		// Rimeclaw's paw on the back of the grip: three hooked claws fanned out
		double gw = 6 * bend + 1.2;
		for (int k = -1; k <= 1; k++) {
			double[] p0 = f(63.5 + k * 3.5, gw + 2.5, 0), p1 = f(63.5 + k * 6.5, gw + 9 - Math.abs(k) * 1.5, 0), p2 = f(63.5 + k * 6.5 - 3, gw + 13 - Math.abs(k) * 2, 0);
			tube(m, p0, p1, 1.8, 1.2, 4, Math.PI / 4, k == 0 ? CLAW : CLAW_DARK, CLAW);
			tube(m, p1, p2, 1.2, 0, 4, Math.PI / 4, CLAW, CLAW);
		}
		return m;
	}
}
