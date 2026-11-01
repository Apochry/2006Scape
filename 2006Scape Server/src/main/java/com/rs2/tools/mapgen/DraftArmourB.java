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
 * <li>Wildgrove (Magic 40): Druids of the deep woods, half grown into trees (their own base, BaseTreant): a bark cowl with a tall, lopsided crown of many-pronged branches and leaf clusters; a tunic of overlapping bark plates, mossy and ivy-grown, with gnarled burls on the shoulders sprouting twigs; a robe that splits below the knee into roots spreading to the ground; root-toed boots; twig-fingered bark gauntlets; small golden flowers. The base carries all of it: no decorations are added.</li>
 * <li>Gravescale (Defence 45): Scale mail from the Gravecoil, the barrow serpent (see Gravecoil.java for its real palette and match it): overlapping serpent scales, a fanged snake-head visor, a coiled serpent on the shield, venom-yellow eyes.</li>
 * <li>Gatehold sentinel (Defence 50): The guard on Gatehold's wall: bright steel plate, a Gatehold-blue tabard with the gate-tower crest in gold, a tall plume, a tower-shaped kiteshield boss.</li>
 * <li>Broodkeeper (Magic 50): the keepers of the Silkwood broodmother's brood, on the mantle base (BaseMantle): many small eyes glowing in the hood's shadow and a spider on its crown, chitin lames down the tunic, a carapace on the mantle with a broodmother's legs over the shoulders, sickly glowing egg-sacs.</li>
 * <li>Rimeclaw hunter (Ranged 60): Hunters who took Rimeclaw (the Frostfang boss) and wear its hide: ice-blue hide, a claw necklace, big hooked claws at the shoulders and on the vambraces, a beast-head coif with frost.</li>
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
			new Style("Wildgrove", "wildgrove", Kind.MANTLE, Motif.WILDGROVE, 40, hsl(17, 4, 34), hsl(18, 4, 22), hsl(16, 5, 46), hsl(7, 2, 47), hsl(7, 2, 33),
					hsl(10, 6, 90), hsl(11, 4, 116), hsl(8, 2, 54), hsl(6, 3, 18), NO_SHIELD,
					new String[] { "Wildgrove crown", "Wildgrove bark tunic", "Wildgrove root robe", null, "Wildgrove root boots", "Wildgrove twig gauntlets" },
					new String[] { "A bark cowl with a crown of branches grown out of it. The druid didn't put it on so much as let it grow.",
							"Bark plates over bark plates, moss in the cracks and ivy climbing. It creaks when you breathe.",
							"A robe that gives up being a robe about the knee and becomes roots. Hob's woodcutters stop where the Wildgrove starts.", null,
							"Roots for toes. Standing still is no trouble at all; walking takes a little persuading.", "Bark gauntlets with twigs for fingers. They grip a staff like a branch grips the sky." }),
			new Style("Gravescale", "gravescale", Kind.COAT, Motif.GRAVESCALE, 45, hsl(14, 2, 38), hsl(15, 2, 26), hsl(14, 2, 50), hsl(9, 2, 86), hsl(9, 2, 64),
					hsl(27, 6, 104), hsl(27, 4, 122), hsl(15, 2, 22), hsl(15, 2, 15), HAS_ALL,
					new String[] { "Gravescale med helm", "Gravescale chainbody", "Gravescale platelegs", "Gravescale sq shield", "Gravescale boots", "Gravescale gloves" },
					new String[] { "A Gravecoil's head, ghost-light still in its eyes. It was sealed in to guard a king; now it guards you.",
							"Gravecoil scales over mail, bone belly-scutes down the front and a barrow-king's bronze at the waist.",
							"Scaled legs with the kings' bronze rings grown into them. The serpent couldn't get them off either.",
							"A Gravecoil, coiled. It looks asleep. They always look asleep.", "Bronze-ringed boots. The Sunken Barrow's water never quite drains out of them.",
							"A king's bronze arm-ring, still on the scaled cuff the serpent grew round it." }),
			new Style("Gatehold sentinel", "sentinel", Kind.COAT, Motif.SENTINEL, 50, hsl(38, 1, 76), hsl(38, 1, 56), hsl(38, 0, 98), hsl(40, 5, 40), hsl(40, 5, 28),
					hsl(8, 5, 72), hsl(9, 2, 118), hsl(40, 3, 24), hsl(40, 3, 16), HAS_ALL,
					new String[] { "Gatehold sentinel full helm", "Gatehold sentinel platebody", "Gatehold sentinel platelegs", "Gatehold sentinel kiteshield", "Gatehold sentinel boots", "Gatehold sentinel gauntlets" },
					new String[] { "A plume you can see from the far end of Gatehold's wall. That's the point of it.",
							"Bright steel under Gatehold blue, the gate-tower on the breast in gold. Polished every night, for whoever is left to see.",
							"Plate for the wall walk: up the steps, along the wall, down the steps, for ever.",
							"The gate-tower in gold. Anything that gets past it still has the real one to get past.", "Steel-capped boots, worn thin on the wall walk.",
							"Gauntlets banded in Gatehold blue. They've held the gate shut more than once." }),
			new Style("Broodkeeper", "broodkeeper", Kind.MANTLE, Motif.BROOD, 50, hsl(51, 2, 42), hsl(51, 2, 28), hsl(51, 1, 56), CHITIN, CHITIN_DARK, hsl(14, 5, 80),
					hsl(14, 3, 112), CHITIN, CHITIN_DARK, NO_SHIELD,
					new String[] { "Broodkeeper hood", "Broodkeeper mantle top", "Broodkeeper leggings", null, "Broodkeeper shoes", "Broodkeeper handwraps" },
					new String[] { "A spider crouches on the cowl, legs round it. In the shadow underneath, every eye is open. It isn't dead. It's on duty.",
							"A mantle under a chitin carapace, the brood's eggs slung beneath it. The broodmother's keepers never set them down.",
							"Egg-sacs hang at the belt, glowing faintly. Best not to be standing near when they hatch.", null,
							"Chitin-spurred wrapped shoes for walking where the brood nests.",
							"Chitin cuffs and hooked spurs on the wraps. The keepers handle the eggs gently, and everything else less so." }),
			new Style("Rimeclaw hunter", "rimeclaw", Kind.WRAP, Motif.RIMECLAW, 60, hsl(36, 3, 74), hsl(36, 3, 54), hsl(36, 2, 94), hsl(38, 3, 36), hsl(38, 3, 24),
					CLAW, hsl(36, 2, 120), hsl(36, 2, 34), hsl(36, 2, 22), NO_SHIELD,
					new String[] { "Rimeclaw hunter coif", "Rimeclaw hunter body", "Rimeclaw hunter chaps", null, "Rimeclaw hunter boots", "Rimeclaw hunter vambraces" },
					new String[] { "Rimeclaw's own head, worn as a hood. The frost on it has never melted, and nor has its expression.",
							"Rimeclaw's hide and a necklace of its claws. The rake-marks on the back were made before you won.",
							"Ice-blue chaps with a claw strapped over each knee, still sharp, still cold.", null,
							"Fur-cuffed boots with Rimeclaw's claws over the toes. The Frostfang ice gives them a wide berth.",
							"Vambraces hooked with Rimeclaw's claws. It tried them out on the hunters first." }),
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
		case GRAVESCALE: { // the serpent's head: a broad snout jutting from the brow, ghost-lit eyes on it, fangs, bone horns swept back
			double[] brow = project(m, F, 0, top + h * 0.24);
			double[] tip = plus(brow, new double[] { 0, 4, -11 }), wide = lerp(brow, tip, 0.3);
			blade(add, plus(brow, new double[] { 0, 0, 1 }), tip, X, hw * 0.75, 2.6, st.main);
			blade(add, plus(brow, new double[] { 0, -1.5, 1 }), plus(tip, new double[] { 0, -1.8, 1 }), X, hw * 0.22, 3.4, st.dark);
			for (int side = -1; side <= 1; side += 2) {
				double[] e = plus(wide, new double[] { side * hw * 0.42, -1.6, 0 });
				add.tube(e, plus(e, new double[] { side * 1.2, -1.8, -0.6 }), 1.6, 0.5, 4, Math.PI / 4, st.accent, st.core);
				double[] fang = plus(lerp(brow, tip, 0.62), new double[] { side * hw * 0.26, 1.2, 0 });
				blade(add, fang, plus(fang, new double[] { -side * 0.4, 7, 0.8 }), X, 1.2, 0.7, st.trim);
				double[] r = { side * hw * 0.62, top + h * 0.26, cz - 2 }, a = { side * (hw + 2.5), top - 2, cz + dz * 0.9 }, b = { side * (hw + 3.5), top - 3, back + 11 };
				blade(add, r, a, Z, 2.8, 1.7, st.trim);
				blade(add, a, b, Z, 1.8, 1.1, st.trimDark);
			}
			for (int k = 0; k < 3; k++) { // bone knuckles down the back of the skull
				double z = cz + dz * (0.15 + k * 0.4), y = top + 1.5 + k * h * 0.16;
				add.tube(new double[] { 0, y + 2, z - 2 }, new double[] { 0, y - 4.5 + k, z + 3.5 }, 2.6 - k * 0.3, 0, 3, Math.PI / 2, st.trim, st.trimDark);
			}
			break;
		}
		case SENTINEL: { // a tall plume in a gold socket, the gate-tower on the brow
			double[] s = { 0, top + 2, cz + 2 };
			add.tube(plus(s, new double[] { 0, 3, 0 }), plus(s, new double[] { 0, -4, 0 }), 3.6, 2.6, 6, 0, st.accent, st.accent);
			for (int k = 0; k < 5; k++) {
				double zt = -6 + k * 3.5, tall = 24 - Math.abs(k - 1.5) * 2.6;
				blade(add, plus(s, new double[] { 0, -2, k - 2 }), plus(s, new double[] { 0, -tall, zt + 5 }), Z, 3.2, 1.2, k % 2 == 0 ? st.trim : st.core);
			}
			tower(add, m, F, 0, top + h * 0.3, 8, st.accent, hsl(8, 5, 46), st.trimDark, 1.0);
			break;
		}
		case BROOD: { // the brood's eyes in the hood's shadow, and a spider crouched on the crown, its legs gripping round the cowl
			double oy = top + h * 0.45;
			double[][] eyes = { { -2.4, -1.8, 1.4 }, { 2.4, -1.8, 1.4 }, { -4.6, -3.8, 0.9 }, { 4.6, -3.8, 0.9 }, { -1.3, 1.8, 0.9 }, { 1.3, 1.8, 0.9 }, { -4.4, 1.0, 0.7 },
					{ 4.4, 1.0, 0.7 } };
			for (double[] e : eyes) {
				double[] p = project(m, F, e[0], oy + e[1]);
				add.tube(plus(p, new double[] { 0, 0, 0.3 }), plus(p, new double[] { 0, 0, -1.4 }), e[2], 0, 4, Math.PI / 4, st.accent, st.core);
			}
			double[] hp = crown(m, front + (back - front) * 0.3, top), ap = crown(m, cz + dz * 0.3, top);
			pod(add, plus(hp, new double[] { 0, -2.0, -3.5 }), plus(hp, new double[] { 0, -2.4, 3.5 }), 3.2, CHITIN_LIGHT, CHITIN);
			pod(add, plus(ap, new double[] { 0, -3.5, -5 }), plus(ap, new double[] { 0, -2.5, 8 }), 5.6, CHITIN, CHITIN_LIGHT);
			add.tube(plus(ap, new double[] { 0, -8.4, 0 }), plus(ap, new double[] { 0, -9.6, 3 }), 1.4, 1.0, 4, Math.PI / 4, st.accent, st.accent);
			for (int k = -1; k <= 1; k += 2) { // two eyes on the spider's own head
				add.tube(plus(hp, new double[] { k * 1.2, -3.6, -3.4 }), plus(hp, new double[] { k * 1.4, -3.8, -5.0 }), 0.8, 0, 4, Math.PI / 4, st.accent, st.core);
			}
			for (int side = -1; side <= 1; side += 2) {
				for (int k = 0; k < 3; k++) {
					double[] root = plus(hp, new double[] { side * 2.4, -2.2, -1.5 + k * 2.0 });
					double[] foot = side(m, side, top + h * (0.34 + 0.05 * Math.abs(k - 1)), front + (back - front) * (0.2 + k * 0.27), hw);
					foot = plus(foot, new double[] { side * 0.6, 0, 0 });
					double[] knee = plus(lerp(root, foot, 0.45), new double[] { side * 5.5, -7, 0 });
					blade(add, root, knee, Y, 1.7, 1.2, CHITIN);
					blade(add, knee, foot, Y, 1.4, 1.0, k % 2 == 0 ? CHITIN_LIGHT : CHITIN_DARK);
				}
			}
			break;
		}
		case RIMECLAW: { // Rimeclaw's own head worn as a hood: the snout over the brow, fangs, ears laid back, frost on the crown
			double[] brow = project(m, F, 0, top + h * 0.12);
			double[] tip = plus(brow, new double[] { 0, 0.5, -12 }), wide = lerp(brow, tip, 0.3);
			blade(add, plus(brow, new double[] { 0, 0, 1 }), tip, X, hw * 0.62, 3.0, st.main);
			blade(add, plus(brow, new double[] { 0, -1.8, 1 }), plus(tip, new double[] { 0, -1.4, 1.5 }), X, hw * 0.2, 3.8, st.light);
			add.tube(plus(tip, new double[] { 0, -0.5, 2.2 }), plus(tip, new double[] { 0, 0.5, -0.8 }), 1.8, 0.8, 4, Math.PI / 4, hsl(38, 4, 18), hsl(38, 4, 18));
			for (int side = -1; side <= 1; side += 2) {
				double[] fang = plus(lerp(brow, tip, 0.35), new double[] { side * hw * 0.45, 1.5, 0 });
				blade(add, fang, plus(fang, new double[] { side * 0.3, 9, 0.8 }), X, 1.4, 0.9, st.accent);
				double[] e = plus(wide, new double[] { side * hw * 0.42, -1.2, 0.5 });
				add.tube(e, plus(e, new double[] { side * 1.0, -1.2, -0.8 }), 1.3, 0.4, 4, Math.PI / 4, st.core, st.core);
				double[] ear = { side * hw * 0.6, top + h * 0.12, cz - 2 };
				blade(add, ear, plus(ear, new double[] { side * 3.5, -7, 8 }), Z, 3.0, 1.1, st.main);
			}
			for (int k = 0; k < 3; k++) { // rime on the crown
				double z = cz + dz * (k * 0.35 - 0.1), x = (k - 1) * 2.0;
				add.tube(new double[] { x, top + 2 + k * 0.8, z }, new double[] { x * 1.8, top - 5 + k, z + 2 }, 1.6, 0, 4, 0.3, st.core, st.core);
			}
			break;
		}
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
		case GRAVESCALE: { // bone belly-scutes down the front, the kings' bronze round the waist, a ridge of bone down the spine
			for (int k = 0; k < 5; k++) {
				double y = top + h * 0.2 + k * h * 0.12, w = hw * (0.42 - k * 0.03);
				decal(add, m, F, new double[][] { { -w, y }, { w, y }, { w * 0.85, y + h * 0.1 }, { -w * 0.85, y + h * 0.1 } }, 0.6, 2.0, k % 2 == 0 ? st.trim : BELLY,
						st.trimDark);
			}
			for (Frame f : new Frame[] { F, B }) {
				LowArmour.strips(add, m, f, -hw * 1.05, bottom - h * 0.16, hw * 1.05, bottom - h * 0.06, 4, 1, 2.4, BRONZE, VERDIGRIS);
			}
			for (int k = 0; k < 4; k++) {
				double y = top + h * 0.14 + k * h * 0.17;
				double[] p = project(m, B, 0, y);
				add.tube(plus(p, new double[] { 0, 2.5, -1.5 }), plus(p, new double[] { 0, -3, 7 - k * 0.8 }), 3.0 - k * 0.3, 0, 3, Math.PI / 2, st.trim, st.trimDark);
			}
			break;
		}
		case SENTINEL: { // pauldrons edged in Gatehold blue, the blue tabard front and back, the gate-tower in gold
			for (int side = -1; side <= 1; side += 2) {
				double[] c = { side * sx, sy, -1 };
				double[] axis = norm(new double[] { side * 0.6, -0.8, 0 });
				dome(add, c, axis, Z, hw * 0.55, hw * 0.44, hw * 0.34, 6, st.dark, st.main);
				ring(add, c, axis, Z, hw * 0.6, hw * 0.49, -1.4, 2.2, 6, st.trim);
				double[] tip = plus(c, scale(axis, hw * 0.34));
				add.tube(tip, plus(tip, scale(axis, 2.5)), 1.8, 0.6, 4, Math.PI / 4, st.accent, st.accent);
			}
			for (Frame f : new Frame[] { F, B }) {
				decal(add, m, f, rect(-hw * 0.38, top + h * 0.16, hw * 0.38, bottom - 1), 0.6, 1.5, st.trim, st.trimDark);
				for (int side = -1; side <= 1; side += 2) {
					decal(add, m, f, rect(side * hw * 0.38 - 0.9, top + h * 0.16, side * hw * 0.38 + 0.9, bottom - 1), 0.5, 1.9, st.accent, hsl(8, 5, 46));
				}
			}
			tower(add, m, F, 0, cy + 1, h * 0.36, st.accent, hsl(8, 5, 46), st.trimDark, 1.0);
			break;
		}
		case BROOD: { // chitin lames down the tunic, a carapace on the pelerine with a broodmother's legs hooked over the shoulders, the brood's sign on the cape, egg-sacs carried on the back
			Mantle mm = new Mantle(m);
			for (int k = 0; k < 3; k++) {
				double y = lerp(mm.neck + 6, mm.waist - 6, k / 3.0), hh = (mm.waist - mm.neck - 12) / 3.0, w = 3.6 + k * 1.6;
				decal(add, m, F, new double[][] { { -w, y }, { w, y }, { w * 0.92, y + hh * 0.75 }, { 0, y + hh * 1.05 }, { -w * 0.92, y + hh * 0.75 } }, 0.6, 2.4 - k * 0.3,
						k % 2 == 0 ? CHITIN_LIGHT : CHITIN, CHITIN_DARK);
			}
			double cw = mm.shoulderX * 0.62;
			decal(add, m, B, new double[][] { { 0, mm.neck - 1 }, { cw, mm.neck + 4 }, { cw * 0.92, mm.pelerineHem - 3 }, { 0, mm.pelerineHem + 2 }, { -cw * 0.92, mm.pelerineHem - 3 },
					{ -cw, mm.neck + 4 } }, 0.6, 2.0, CHITIN, CHITIN_DARK);
			for (int side = -1; side <= 1; side += 2) { // a broodmother's legs, hooked over the shoulders from the carapace behind
				double[] root = plus(project(m, B, side * cw * 0.6, mm.neck + 5), new double[] { 0, 0, 1.6 });
				double[] foot = plus(project(m, F, side * mm.shoulderX * 0.62, lerp(mm.neck, mm.pelerineHem, 0.7)), new double[] { 0, 0, -1.0 });
				double[] knee = { side * mm.shoulderX * 0.62, mm.neck - 9, 0 };
				blade(add, root, knee, X, 2.0, 1.4, CHITIN);
				blade(add, knee, foot, X, 1.6, 1.2, CHITIN_LIGHT);
				add.tube(plus(knee, new double[] { 0, 1.5, 0 }), plus(knee, new double[] { 0, -1.5, 0 }), 1.7, 1.7, 4, Math.PI / 4, CHITIN_DARK, CHITIN_DARK);
			}
			double hy = mm.capeHip, hs = 8.5;
			decal(add, m, B, new double[][] { { -hs * 0.65, hy - hs }, { hs * 0.65, hy - hs }, { 0, hy } }, 0.3, 1.6, st.accent, st.accent);
			decal(add, m, B, new double[][] { { 0, hy }, { hs * 0.65, hy + hs }, { -hs * 0.65, hy + hs } }, 0.3, 1.6, st.accent, st.accent);
			for (int k = -1; k <= 1; k++) { // the eggs, slung under the carapace
				double[] p = project(m, B, k * 4.2, mm.pelerineHem + 1 - Math.abs(k) * 1.5);
				egg(add, plus(p, new double[] { 0, 0, 1.2 + (k == 0 ? 0.6 : 0) }), 2.4 - Math.abs(k) * 0.3, 7.0 - Math.abs(k), st.accent, st.core);
			}
			break;
		}
		case RIMECLAW: { // a necklace of claws, Rimeclaw's great hooked claws over the shoulders, three rake-scars across the back
			double ny = top + h * 0.14;
			for (int k = -3; k <= 3; k++) {
				double x = k * hw * 0.11, y = ny + (9 - k * k) * -0.35 + 3;
				double[] p = project(m, F, x, y);
				add.tube(plus(p, new double[] { 0, -0.5, -0.8 }), plus(p, new double[] { k * 0.3, 5.0 - Math.abs(k) * 0.4, -3.0 }), 1.3, 0, 4, Math.PI / 4,
						k % 2 == 0 ? st.accent : CLAW_DARK, st.accent);
			}
			decal(add, m, F, new double[][] { { -hw * 0.38, ny - 2.5 }, { -hw * 0.2, ny + 1.2 }, { 0, ny + 2.3 }, { hw * 0.2, ny + 1.2 }, { hw * 0.38, ny - 2.5 }, { hw * 0.36, ny - 1.2 },
					{ hw * 0.2, ny + 2.4 }, { 0, ny + 3.5 }, { -hw * 0.2, ny + 2.4 }, { -hw * 0.36, ny - 1.2 } }, 0.3, 1.3, st.trimDark, st.trimDark);
			for (int side = -1; side <= 1; side += 2) {
				for (int k = -1; k <= 1; k++) {
					double[] r = { side * sx * (0.92 + 0.06 * Math.abs(k)), sy + 1, cz + k * 4.5 };
					double[] mid = plus(r, new double[] { side * 5, -9.5 + Math.abs(k) * 2, k * 1.8 });
					double[] tip = plus(mid, new double[] { side * 6.5, 4.5, k * 1.0 });
					blade(add, r, mid, Z, 2.8, 1.7, k == 0 ? st.accent : CLAW_DARK);
					blade(add, mid, tip, Z, 1.8, 1.1, st.accent);
				}
			}
			for (int k = -1; k <= 1; k++) {
				double x0 = -hw * 0.5 + k * hw * 0.32, x1 = hw * 0.2 + k * hw * 0.32;
				decal(add, m, B, LowArmour.bar(x0, top + h * 0.22, x1, top + h * 0.66, 0.9), 0.3, 1.6, st.trimDark, st.trimDark);
			}
			break;
		}
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
			case GRAVESCALE: // a bone scute over each knee
				decal(one, m, F, new double[][] { { x - 5, kneeY - 6 }, { x + 5, kneeY - 6 }, { x + 4, kneeY + 5 }, { x, kneeY + 8 }, { x - 4, kneeY + 5 } }, 0.8, 2.4, st.trim,
						st.trimDark);
				break;
			case SENTINEL: { // bright knee-plates, a gold boss on each
				LowArmour.kneePlate(one, m, x, kneeY, st.light, st.dark);
				double[] hit = surface(m, new double[] { x, kneeY, -100 }, new double[] { 0, 0, 1 });
				double[] p = hit == null ? new double[] { x, kneeY, -12 } : hit;
				one.tube(plus(p, new double[] { 0, 0, -1.5 }), plus(p, new double[] { 0, 0, -4.5 }), 2.2, 0.8, 6, 0, st.accent, st.accent);
				break;
			}
			case RIMECLAW: { // Rimeclaw's paw over each knee
				for (int k = -1; k <= 1; k++) { // three claws, fanned down over the knee like a paw
					double[] p = project(m, F, x + k * 2.6, kneeY - 8 + Math.abs(k) * 1.5);
					double[] r = plus(p, new double[] { 0, 0, -1.2 }), mid = plus(r, new double[] { k * 1.2, 5.5 - Math.abs(k), -4.5 });
					blade(one, r, mid, X, 1.6, 1.1, k == 0 ? st.accent : CLAW_DARK);
					blade(one, mid, plus(mid, new double[] { k * 0.4, 4.5 - Math.abs(k), 2.2 }), X, 1.1, 0.8, st.accent);
				}
				break;
			}
			default:
				break;
			}
		}
		switch (st.motif) {
		case GRAVESCALE: { // belly-scutes down the front, the kings' bronze rings round the thighs
			for (int k = 0; k < 3; k++) {
				double y = top + 2 + k * h * 0.09, w = hw * (0.22 - k * 0.03);
				decal(fr, m, F, new double[][] { { -w, y }, { w, y }, { w * 0.8, y + h * 0.08 }, { -w * 0.8, y + h * 0.08 } }, 0.8, 2.0, k % 2 == 0 ? st.trim : BELLY, st.trimDark);
			}
			for (int side = -1; side <= 1; side += 2) {
				for (Frame f : new Frame[] { F, B }) {
					decal(fr, m, f, rect(side * kneeX - 6.5, top + h * 0.3, side * kneeX + 6.5, top + h * 0.36), 0.7, 2.0, BRONZE, VERDIGRIS);
				}
			}
			break;
		}
		case SENTINEL: // the blue tasset, edged in gold, the tower small on it
			decal(fr, m, F, rect(-hw * 0.3, top + 2, hw * 0.3, top + h * 0.3), 0.8, 1.7, st.trim, st.trimDark);
			decal(fr, m, F, rect(-hw * 0.3, top + h * 0.3 - 1.4, hw * 0.3, top + h * 0.3), 0.6, 2.3, st.accent, hsl(8, 5, 46));
			break;
		case BROOD: { // chitin plates on the front flaps, a cluster of egg-sacs at the belt, chitin tatters at the flaps' hems
			double waist = BaseMantle.cen(m, BaseMantle.WAIST)[1];
			for (int s = 0; s < 2; s++) {
				double[] knee = BaseMantle.cen(m, BaseMantle.KNEE[s]);
				double x = BaseMantle.cen(m, BaseMantle.THIGH[s])[0], hem = knee[1] - 7, len = hem - waist - 12;
				for (int k = 0; k < 2; k++) {
					double y = waist + 8 + k * len / 2, w = 4.6 - k * 0.6, hh = len / 2 - 1;
					decal(fr, m, F, new double[][] { { x - w, y }, { x + w, y }, { x + w * 0.9, y + hh * 0.8 }, { x, y + hh * 1.05 }, { x - w * 0.9, y + hh * 0.8 } }, 0.6,
							2.2 - k * 0.3, k % 2 == 0 ? CHITIN_LIGHT : CHITIN, CHITIN_DARK);
				}
				for (Frame f : new Frame[] { F }) {
					for (int k = -1; k <= 1; k++) {
						double[] p = project(m, f, x + k * 4.0, hem - 1.5);
						blade(fr, p, plus(plus(p, scale(f.n, 1.0)), new double[] { k * 0.8, 6 + (k == 0 ? 1.5 : 0), 0 }), X, 1.8, 0.9, k == 0 ? CHITIN : CHITIN_LIGHT);
					}
				}
			}
			for (int k = 0; k < 3; k++) { // egg-sacs hung at the belt, on the left hip
				double a = -Math.PI * 0.2 + (k - 1) * 0.26;
				double[] hip = skin(m, a, waist + 6 + (k == 1 ? 2.5 : 0), cz, 2.0 + (k == 1 ? 0.6 : 0));
				if (hip != null) {
					egg(fr, plus(hip, new double[] { 0, -2.5, 0 }), 2.6 - (k == 1 ? 0 : 0.4), 7.5 - (k == 1 ? 0 : 1), st.accent, st.core);
				}
			}
			break;
		}
		case RIMECLAW: // a belt with a claw for a buckle
			for (Frame f : new Frame[] { F, B }) {
				decal(fr, m, f, rect(-hw * 1.02, top + 1, hw * 1.02, top + 5), 0.7, 1.6, st.trim, st.trimDark);
			}
			double[] p = project(m, F, -hw * 0.15, top + 3);
			blade(fr, plus(p, new double[] { -2, -0.5, -1.5 }), plus(p, new double[] { 3, 1.5, -2.5 }), Y, 1.6, 0.9, st.accent);
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
		case GRAVESCALE: { // a Gravecoil, coiled, its head raised in the middle
			int n = 13;
			double turns = 1.3, r0 = s * 0.8, r1 = s * 0.24;
			double[][] pt = new double[n + 1][];
			for (int i = 0; i <= n; i++) {
				double t = (double) i / n, a = -Math.PI * 0.5 + Math.PI * 2 * turns * t;
				double r = r0 + (r1 - r0) * t;
				pt[i] = new double[] { cu + Math.cos(a) * r, cv + Math.sin(a) * r };
			}
			for (int i = 0; i < n; i++) {
				double t = (double) i / n, wd = s * (0.06 + 0.09 * t);
				boolean ringed = i == 5 || i == 10;
				decal(add, m, f, LowArmour.bar(pt[i][0], pt[i][1], pt[i + 1][0], pt[i + 1][1], wd), 0.5, ringed ? 2.6 : 2.0, ringed ? BRONZE : i % 2 == 0 ? st.trim : BELLY,
						ringed ? VERDIGRIS : st.trimDark);
			}
			double[] hd = pt[n], dir = { pt[n][0] - pt[n - 1][0], pt[n][1] - pt[n - 1][1] };
			double dl = Math.hypot(dir[0], dir[1]);
			double ux = dir[0] / dl, uy = dir[1] / dl, vx = -uy, vy = ux, hs = s * 0.27;
			decal(add, m, f, new double[][] { { hd[0] - vx * hs * 0.5, hd[1] - vy * hs * 0.5 }, { hd[0] + ux * hs * 0.6 - vx * hs * 0.75, hd[1] + uy * hs * 0.6 - vy * hs * 0.75 },
					{ hd[0] + ux * hs * 1.6, hd[1] + uy * hs * 1.6 }, { hd[0] + ux * hs * 0.6 + vx * hs * 0.75, hd[1] + uy * hs * 0.6 + vy * hs * 0.75 },
					{ hd[0] + vx * hs * 0.5, hd[1] + vy * hs * 0.5 } }, 0.5, 2.6, st.trim, st.trimDark);
			for (int k = -1; k <= 1; k += 2) {
				decal(add, m, f, diamond(hd[0] + ux * hs * 0.6 + vx * hs * 0.38 * k, hd[1] + uy * hs * 0.6 + vy * hs * 0.38 * k, hs * 0.16, hs * 0.16), 0.3, 3.1, st.accent, st.accent);
			}
			break;
		}
		case SENTINEL: { // the gate-tower raised as the boss over a field of Gatehold blue
			decal(add, m, f, diamond(0, 2, 15, 26), 0.5, 1.6, st.trim, st.trimDark);
			tower(add, m, f, 0, 1, 36, st.accent, hsl(8, 5, 46), st.trimDark, 1.7);
			break;
		}
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
			case GRAVESCALE: // the kings' bronze at the ankle, a bone toe-cap
				LowArmour.toeCap(add, x, front, st.trim);
				LowArmour.cuff(add, m, side, top, top + 3.5, 0.7, BRONZE, BRONZE_DARK);
				break;
			case SENTINEL:
				LowArmour.toeCap(add, x, front, st.light);
				LowArmour.cuff(add, m, side, top, top + 2.5, 0.6, st.accent, st.leather);
				break;
			case BROOD: { // a chitin cuff, two hooked chitin spurs over the toes
				int s = side < 0 ? 0 : 1;
				double[] c = BaseMantle.cen(m, BaseMantle.ANKLE[s]), t = BaseMantle.cen(m, BaseMantle.TOES[s]);
				add.tube(new double[] { c[0], top - 0.5, c[2] + 0.5 }, new double[] { c[0], top + 3.5, c[2] + 0.5 }, 6.9, 6.7, 6, 0.26, CHITIN, CHITIN_DARK);
				for (int k = -1; k <= 1; k += 2) {
					add.tube(new double[] { t[0] + k * 2.0, t[1] - 5, t[2] + 5 }, new double[] { t[0] + k * 2.8, t[1] - 7.5, t[2] - 8 }, 1.4, 0, 4, 0.78, CHITIN_LIGHT, CHITIN_LIGHT);
				}
				break;
			}
			case RIMECLAW: // a frosted fur cuff, two claws over the toes
				LowArmour.cuff(add, m, side, top, top + 4.5, 1.3, st.light, st.core);
				for (int k = -1; k <= 1; k += 2) {
					add.tube(new double[] { x + k * 1.8, -3, front + 5 }, new double[] { x + k * 2.2, -0.5, front - 3 }, 1.4, 0, 4, 0.78, st.accent, st.accent);
				}
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
			case GRAVESCALE: // a king's bronze arm-ring over a scaled cuff
				LowArmour.gauntlet(add, x, top, 6.8, st.main, st.dark, BRONZE);
				add.tube(new double[] { x, top + 2, 1 }, new double[] { x, top + 4, 1 }, 6.4, 6.4, 6, 0.26, VERDIGRIS, VERDIGRIS);
				break;
			case SENTINEL:
				LowArmour.gauntlet(add, x, top, 7.6, st.main, st.dark, st.accent);
				add.tube(new double[] { x, top + 4, 1 }, new double[] { x, top + 6, 1 }, 7.0, 7.2, 6, 0.26, st.trim, st.trim);
				break;
			case BROOD: { // a chitin cuff, a glowing egg on the back of the hand, hooked spurs on the cuff
				int s = side < 0 ? 0 : 1;
				double[] c = cuffTop(m, side, top), hd = BaseMantle.cen(m, BaseMantle.HAND[s]);
				add.tube(new double[] { c[0], top - 0.5, c[2] }, new double[] { c[0], top + 4, c[2] }, 7.4, 7.0, 6, 0.26, CHITIN, CHITIN_DARK);
				pod(add, new double[] { hd[0] + side * 3.6, hd[1] - 9.5, hd[2] + 1 }, new double[] { hd[0] + side * 3.6, hd[1] - 3.5, hd[2] + 1 }, 2.0, st.accent, st.core);
				for (int k = -1; k <= 1; k += 2) {
					add.tube(new double[] { c[0] + side * 6, top + 3, c[2] + k * 3 }, new double[] { c[0] + side * 9, top + 9, c[2] + k * 3.5 }, 1.2, 0, 4, 0.78, CHITIN_LIGHT, CHITIN_LIGHT);
				}
				break;
			}
			case RIMECLAW: // a strap, and great hooked claws up the outside of each vambrace
				add.tube(new double[] { x, top + 3, 0.5 }, new double[] { x, top + 5.5, 0.5 }, 5.4, 5.4, 6, 0.26, st.trim, st.trimDark);
				for (int k = -1; k <= 1; k++) {
					double[] r = { x + side * 4.5, top + 9 + Math.abs(k) * 1.5, 1 + k * 3.2 };
					double[] mid = plus(r, new double[] { side * 5, -8 + Math.abs(k) * 2, k * 0.8 });
					blade(add, r, mid, Z, 2.2, 1.3, k == 0 ? st.accent : CLAW_DARK);
					blade(add, mid, plus(mid, new double[] { side * 0.5, -5.5, k * 0.5 + 3 }), Z, 1.3, 0.8, st.accent);
				}
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
