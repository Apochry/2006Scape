package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ArmourForge.*;
import static com.rs2.tools.mapgen.ModelData.hsl;

import com.rs2.game.content.voidrealm.VoidConstants;
import com.rs2.tools.mapgen.ArmourForge.Frame;
import com.rs2.tools.mapgen.ArmourSets.Kind;
import com.rs2.tools.mapgen.ArmourSets.Motif;
import com.rs2.tools.mapgen.ArmourSets.Style;

/**
 * The eight low armour sets (levels 10-40), humbler than the saga's six, each built on the plainest stock mesh of its
 * kind: med helm, chainbody and square shield (chain); cowl, leather body and chaps (leather); green d'hide; mystic
 * robes; rune plate for the Warden initiates. Each is tied to an early place on Skullbrine or Nythera:
 *
 * <ol start="7">
 * <li>Gatehold recruit (Defence 10): the outpost's issue mail, with a night-blue tabard, a plume and the Wardens' star.</li>
 * <li>Woodsman (Ranged 15): Hob Ashby's forest leathers: a feathered hood, a bandolier and a hatchet at the hip.</li>
 * <li>Croftwise (Magic 20): Tollan's Croft homespun, a herb-banded hat, a moss-green shawl and an apron.</li>
 * <li>Rattlebone (Defence 25): Rattlebone Row's scrap mail: rust, rags, goat horns and nailed-on bones.</li>
 * <li>Blackbrine (Ranged 30): the boarding leathers of Blackbrine's crew: tar-black, a red bandana and sash, brass.</li>
 * <li>Warden initiate (Defence 30): Wardens' steel before the night-blue is earned; a plume stub, the star on the breast.</li>
 * <li>Barrowbronze (Defence 35): the old kingdom's bronze, gone green in the barrow; a red crest and the old sun.</li>
 * <li>Veilsworn (Magic 40): the first of the void's followers: ash-grey robes, fraying hems and an open violet eye.</li>
 * </ol>
 * Decorations are placed from each mesh's own bounds, as in {@link ArmourSets}, so they sit on both bodies.
 */
final class LowArmour {

	private LowArmour() {
	}

	static final Style[] LOW = {
			new Style("Gatehold recruit", "recruit", Kind.CHAIN, Motif.RECRUIT, 10, hsl(0, 0, 56), hsl(0, 0, 38), hsl(0, 0, 72), hsl(42, 4, 32),
					hsl(42, 4, 20), hsl(42, 0, 112), hsl(42, 0, 124), hsl(5, 3, 28), hsl(5, 3, 18), VoidConstants.RECRUIT_SET,
					new String[] { "Recruit's med helm", "Recruit's chainbody", "Recruit's platelegs", "Recruit's sq shield", "Recruit's boots",
							"Recruit's gloves" },
					new String[] { "Gatehold issue. The plume tells the sergeants which way you're facing.",
							"Iron rings under a Wardens' tabard. Every recruit's first night on the wall is spent in this.",
							"Second-hand plate. The last recruit grew out of them, apparently.",
							"Painted in the Wardens' night-blue. The star is mostly still there.", "Hobnailed boots for long marches round the outpost.",
							"Issue gloves, a size too big, as issue gloves always are." }),
			new Style("Woodsman", "woodsman", Kind.LEATHER, Motif.WOODSMAN, 15, hsl(22, 3, 30), hsl(22, 3, 20), hsl(22, 2, 42), hsl(6, 3, 42),
					hsl(6, 3, 28), hsl(2, 5, 46), hsl(0, 0, 74), hsl(6, 4, 30), hsl(6, 4, 19), VoidConstants.WOODSMAN_SET,
					new String[] { "Woodsman's hood", "Woodsman's jerkin", "Woodsman's chaps", null, "Woodsman's boots", "Woodsman's vambraces" },
					new String[] { "Hob Ashby's spare hood, with a jay's feather in it for luck.",
							"Oiled leather that has turned a lot of falling branches. The hatchet comes with it.",
							"Patched at the knee. Woodcutters kneel more than you'd think.", null, "Boots that know every root on the forest path.",
							"Leather bracers worn smooth by a bowstring." }),
			new Style("Croftwise", "croftwise", Kind.ROBE, Motif.CROFT, 20, hsl(8, 2, 72), hsl(8, 2, 52), hsl(8, 1, 90), hsl(21, 3, 36),
					hsl(21, 3, 24), hsl(10, 6, 84), hsl(50, 3, 84), hsl(6, 3, 32), hsl(6, 3, 22), VoidConstants.CROFTWISE_SET,
					new String[] { "Croftwise hat", "Croftwise robe top", "Croftwise robe bottom", null, "Croftwise boots", "Croftwise gloves" },
					new String[] { "Marta Tollan's mother's hat. There's still thyme in the band.",
							"Homespun wool under a moss-green shawl. It smells of hearth smoke and sage.",
							"Skirts and an apron, with pockets full of seeds and a sprig of something.", null,
							"Wool-lined boots. Practical, if not fast.", "Garden gloves that have pulled up a great many nettles." }),
			new Style("Rattlebone", "rattlebone", Kind.CHAIN, Motif.RATTLE, 25, hsl(4, 3, 34), hsl(4, 3, 22), hsl(5, 2, 46), hsl(9, 2, 96),
					hsl(8, 2, 70), hsl(0, 6, 38), hsl(9, 1, 110), hsl(5, 3, 24), hsl(5, 3, 16), VoidConstants.RATTLEBONE_SET,
					new String[] { "Rattlebone med helm", "Rattlebone chainbody", "Rattlebone platelegs", "Rattlebone sq shield", "Rattlebone boots",
							"Rattlebone gloves" },
					new String[] { "Scrap iron and two goat horns, held together by optimism.",
							"Rusted mail with ribs sewn on the front. Not, you hope, human ones.", "Salvaged from the Drowned Maiden. Still damp.",
							"A ship's hatch with bones nailed across it. Mad Pete calls it 'heritage'.",
							"Bone toe-caps. On Rattlebone Row, kicking still counts as an argument.", "Knucklebones on the knuckles. Subtle." }),
			new Style("Blackbrine", "blackbrine", Kind.HIDE, Motif.BRINE, 30, hsl(5, 1, 18), hsl(5, 1, 11), hsl(5, 1, 28), hsl(8, 5, 60),
					hsl(7, 5, 40), hsl(0, 6, 40), hsl(9, 1, 104), hsl(5, 2, 22), hsl(5, 2, 14), VoidConstants.BLACKBRINE_SET,
					new String[] { "Blackbrine coif", "Blackbrine body", "Blackbrine chaps", null, "Blackbrine boots", "Blackbrine vambraces" },
					new String[] { "Tarred leather under a red bandana: the boarding dress of Blackbrine's crew.",
							"Black leather stiff with tar and salt. The skull on the back says you're crew.",
							"Boarding chaps, made for climbing rigging rather than sitting down.", null,
							"Sea boots with a brass buckle. They squelch, faintly, for ever.", "Brass-studded bracers for swinging on ropes." }),
			new Style("Warden initiate", "initiate", Kind.PLATE, Motif.INITIATE, 30, hsl(40, 1, 64), hsl(40, 1, 46), hsl(40, 0, 84), hsl(42, 4, 34),
					hsl(42, 4, 22), hsl(42, 0, 112), hsl(42, 0, 124), hsl(5, 3, 26), hsl(5, 3, 17), VoidConstants.INITIATE_SET,
					new String[] { "Initiate's full helm", "Initiate's platebody", "Initiate's platelegs", "Initiate's kiteshield", "Initiate's boots",
							"Initiate's gauntlets" },
					new String[] { "An initiate's helm. The plume grows when you've earned it; this one is a stub.",
							"Wardens' steel with the star on the breast, over a tabard in the night-blue.",
							"They've walked the wall a hundred times, and will walk it a hundred more.",
							"The Wardens' star on night-blue. Every initiate paints their own, and it shows.", "Steel-capped boots for the wall walk.",
							"Steel gauntlets with night-blue cuffs." }),
			new Style("Barrowbronze", "barrowbronze", Kind.CHAIN, Motif.BARROW, 35, hsl(28, 2, 54), hsl(28, 2, 36), hsl(27, 1, 70), hsl(7, 4, 52),
					hsl(6, 4, 34), hsl(1, 5, 38), hsl(9, 4, 92), hsl(5, 3, 24), hsl(5, 3, 16), VoidConstants.BARROWBRONZE_SET,
					new String[] { "Barrowbronze med helm", "Barrowbronze chainbody", "Barrowbronze platelegs", "Barrowbronze sq shield", "Barrowbronze boots",
							"Barrowbronze gloves" },
					new String[] { "Old-kingdom bronze, gone green in the barrow. The crest was red once.",
							"Bronze mail from the old kingdom's day. The sun on the breast is theirs.",
							"Green with age and still sound. Nobody remembers how to make bronze like this.",
							"The old kingdom's sun, under centuries of verdigris.", "Barrow boots, bronze-toed.",
							"Bronze-backed gloves from a barrow-guard's hands. He won't miss them. Probably." }),
			new Style("Veilsworn", "veilsworn", Kind.ROBE, Motif.VEIL, 40, hsl(46, 1, 30), hsl(46, 1, 18), hsl(46, 1, 44), ArmourSets.violet(70, 6),
					ArmourSets.violet(40, 5), ArmourSets.violet(100, 6), hsl(42, 0, 120), hsl(46, 1, 20), hsl(46, 1, 12), VoidConstants.VEILSWORN_SET,
					new String[] { "Veilsworn hat", "Veilsworn robe top", "Veilsworn robe bottom", null, "Veilsworn boots", "Veilsworn gloves" },
					new String[] { "The void's first followers wore these to the Scar's edge. Most of them stayed.",
							"An open eye stitched on the breast. It wasn't open when it was stitched.",
							"Grey robes that fray into nothing at the hem, a little more each day.", null,
							"Soft boots for walking where the ground is thin.", "Grey gloves. The fingertips are stained violet." }), };

	/** The square shield's face and rim colours (chain sets). */
	static int shieldFace(Style st) {
		return st.motif == Motif.RECRUIT ? st.trim : st.motif == Motif.RATTLE ? st.leather : st.main;
	}

	static int shieldRim(Style st) {
		return st.motif == Motif.BARROW ? st.trim : st.motif == Motif.RATTLE ? st.dark : st.main;
	}

	static final double[] Z = { 0, 0, 1 }, X = { 1, 0, 0 }, Y = { 0, 1, 0 };

	/* ------------------------------------------------------------- the helm */

	static ModelData helm(Style st, ModelData m) {
		int[] bb = m.bounds();
		double hw = bb[3], top = bb[1], bottom = bb[4], h = bottom - top, front = bb[2], back = bb[5], cz = (front + back) / 2;
		ModelData add = new ModelData();
		switch (st.motif) {
		case RECRUIT: // a short night-blue plume, the star on the brow
			blade(add, new double[] { 0, top + 3, cz - 3 }, new double[] { 0, top - 7, cz + 7 }, Z, 3.2, 1.2, st.trim);
			blade(add, new double[] { 0, top + 3, cz }, new double[] { 0, top - 3, cz + 10 }, Z, 2.6, 1.0, st.trimDark);
			decal(add, m, ArmourSets.FRONT, diamond(0, top + h * 0.2, 1.7, 2.3), 0.4, 1.6, st.accent, st.trim);
			break;
		case WOODSMAN: // a jay's feather in the band
			blade(add, new double[] { hw * 0.75, top + h * 0.32, cz }, new double[] { hw + 4, top - 9, cz + 5 }, Z, 2.0, 0.6, st.accent);
			blade(add, new double[] { hw * 0.75, top + h * 0.34, cz + 1 }, new double[] { hw + 1, top - 3, cz + 9 }, Z, 1.5, 0.5, st.trim);
			break;
		case CROFT: { // a moss-green hat band, herbs tucked in it, one yellow flower
			double by = bottom - h * 0.32;
			ring(add, new double[] { 0, by, cz }, new double[] { 0, -1, 0 }, X, hw * 0.98, (back - front) / 2 * 0.98, 0, 2.4, 10, st.trim);
			for (int k = 0; k < 3; k++) {
				double[] r = { hw * (0.55 + k * 0.12), by - 1, cz + 2 + k * 2 };
				blade(add, r, new double[] { r[0] + 2 + k, by - 8 - k * 2, r[2] + 1 }, Z, 1.6, 0.5, k == 1 ? st.trimDark : st.trim);
			}
			add.tube(new double[] { hw * 0.68 + 3, by - 10, cz + 5 }, new double[] { hw * 0.68 + 3, by - 12.5, cz + 5 }, 1.8, 1.0, 5, 0, st.accent,
					st.accent);
			break;
		}
		case RATTLE: // two goat horns, a bone brow-plate
			for (int side = -1; side <= 1; side += 2) {
				double[] r = { side * hw * 0.75, top + h * 0.28, cz }, a = { side * (hw + 6), top + h * 0.02, cz + 1 },
						b = { side * (hw + 7), top - 8, cz - 2 };
				blade(add, r, a, Z, 2.8, 1.8, st.trim);
				blade(add, a, b, Z, 2.0, 1.3, st.trimDark);
			}
			decal(add, m, ArmourSets.FRONT, rect(-hw * 0.45, top + h * 0.2, hw * 0.45, top + h * 0.3), 0.4, 1.6, st.trim, st.trimDark);
			break;
		case BRINE: // the bandana knotted at the back, a brass earring
			add.tube(new double[] { 0, top + h * 0.35, back - 1.5 }, new double[] { 0, top + h * 0.35, back + 1.5 }, 2.4, 2.0, 6, 0, st.accent,
					st.accent);
			for (int side = -1; side <= 1; side += 2) {
				blade(add, new double[] { side * 1.2, top + h * 0.36, back }, new double[] { side * 4.5, top + h * 0.36 + 13, back + 4 }, X, 2.3, 0.5,
						st.accent);
			}
			ring(add, new double[] { -hw - 0.6, top + h * 0.78, cz }, X, Z, 1.8, 1.8, -0.5, 0.5, 6, st.trim);
			break;
		case INITIATE: // a plume stub at the back of the crown, the star on the brow
			blade(add, new double[] { 0, top + 2, back - 5 }, new double[] { 0, top - 4, back + 3 }, Z, 3.0, 1.2, st.trim);
			ArmourSets.star4(add, m, ArmourSets.FRONT, 0, top + h * 0.34, 8, st.accent, st.trim, st.core);
			break;
		case BARROW: // a red horse-hair crest front to back, bronze cheek-guards
			for (int k = 0; k < 6; k++) {
				double z = front + 3 + (back - front - 6) * k / 5.0;
				blade(add, new double[] { 0, top + 3, z }, new double[] { 0, top - (k == 1 || k == 2 ? 9 : 6), z + 2.5 }, Z, 2.6, 1.3,
						k % 2 == 0 ? st.accent : hsl(1, 5, 30));
			}
			for (int side = -1; side <= 1; side += 2) {
				blade(add, new double[] { side * hw * 0.95, top + h * 0.5, front + 6 }, new double[] { side * hw * 0.9, bottom + 3, front + 4 }, Z, 3.6,
						1.0, st.trim);
			}
			break;
		case VEIL: { // a violet band, an eye over the brow, rags hanging from the brim
			double by = bottom - h * 0.3;
			ring(add, new double[] { 0, by, cz }, new double[] { 0, -1, 0 }, X, hw * 0.98, (back - front) / 2 * 0.98, 0, 2.0, 10, st.trimDark);
			ArmourSets.eye(add, m, ArmourSets.FRONT, 0, by - h * 0.12, 3.4, st);
			for (int k = -1; k <= 1; k++) {
				double x = k * hw * 0.4;
				blade(add, new double[] { x, bottom - 1, back - 2 }, new double[] { x * 1.1, bottom + 7 + Math.abs(k) * -2, back + 1 }, X, 2.2, 0.5,
						k == 0 ? st.dark : st.main);
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
		double dz = (back - front) / 2, cy = top + h * 0.42, sx = hw * 0.7, sy = top + h * 0.12;
		ModelData add = new ModelData();
		Frame F = ArmourSets.FRONT, B = ArmourSets.BACK;
		switch (st.motif) {
		case RECRUIT: // the Wardens' tabard front and back, the star, a belt
			for (Frame f : new Frame[] { F, B }) { // in strips, so the cloth follows the mail instead of the rings poking through
				strips(add, m, f, -hw * 0.45, top + h * 0.2, hw * 0.45, bottom - 1, 3, 3, 1.9, st.trim, st.trimDark);
				strips(add, m, f, -hw * 1.05, bottom - h * 0.24, hw * 1.05, bottom - h * 0.11, 4, 1, 3.0, st.leather, st.leatherDark);
			}
			ArmourSets.star4(add, m, F, 0, cy - 1, h * 0.3, st.accent, st.light, st.core);
			break;
		case WOODSMAN: // a bandolier across front and back, buckles, a hatchet at the hip
			decal(add, m, F, new double[][] { { -hw * 0.75, top + 3 }, { -hw * 0.48, top + 3 }, { hw * 0.72, bottom - 2 }, { hw * 0.45, bottom - 2 } }, 0.6,
					1.6, st.leatherDark, st.leatherDark);
			decal(add, m, B, new double[][] { { hw * 0.48, top + 3 }, { hw * 0.75, top + 3 }, { -hw * 0.45, bottom - 2 }, { -hw * 0.72, bottom - 2 } }, 0.6,
					1.6, st.leatherDark, st.leatherDark);
			for (double t : new double[] { 0.3, 0.62 }) {
				double x = -hw * 0.615 + hw * 1.2 * t, y = top + 3 + (bottom - 5 - top) * t;
				decal(add, m, F, diamond(x, y, 1.6, 1.6), 0.4, 2.4, st.core, st.trimDark);
			}
			add.tube(new double[] { hw + 0.5, bottom - 13, cz }, new double[] { hw + 1.5, bottom + 7, cz }, 0.9, 0.9, 4, 0, st.trim, st.trim);
			blade(add, new double[] { hw + 0.8, bottom - 11, cz }, new double[] { hw + 1.2, bottom - 11, cz - 8 }, Y, 3.2, 0.6, st.core);
			break;
		case CROFT: // a moss-green shawl round the shoulders, a wooden toggle
			for (int k = 0; k < 12; k++) { // draped: each strip hangs straight down from the shoulder line, a fringe at the hem
				double a = Math.PI * k / 11;
				double x = -Math.cos(a) * hw * 0.66, z = cz + Math.sin(a) * dz * 1.0 - 1;
				blade(add, new double[] { x, top + 1, z }, new double[] { x * 1.05, top + h * 0.38, z + (z > cz ? 1.5 : -1.5) },
						new double[] { -Math.sin(a), 0, Math.cos(a) }, 4.4, 1.0, k % 2 == 0 ? st.trim : st.trimDark);
			}
			decal(add, m, F, diamond(0, top + h * 0.2, 1.8, 2.4), 0.4, 2.4, st.leather, st.leatherDark);
			break;
		case RATTLE: // ribs on the chest, bone spikes at the shoulders, a rag sash
			decal(add, m, F, new double[][] { { hw * 0.45, top + 2 }, { hw * 0.8, top + 2 }, { -hw * 0.6, bottom - 1 }, { -hw * 0.95, bottom - 1 } }, 0.6,
					1.3, st.accent, hsl(0, 6, 26));
			for (int k = 0; k < 3; k++) {
				double y = cy - 7 + k * 5.5, w = hw * (0.55 - k * 0.06);
				decal(add, m, F, rect(-w, y, w, y + 2.2), 0.5, 2.1, st.trim, st.trimDark);
			}
			decal(add, m, F, rect(-1.1, cy - 9, 1.1, cy + 9), 0.5, 2.3, st.trim, st.trimDark);
			for (int side = -1; side <= 1; side += 2) {
				blade(add, new double[] { side * sx, sy, cz }, new double[] { side * (sx + 5), sy - 11, cz + 1 }, Z, 2.4, 1.6, st.trim);
				blade(add, new double[] { side * sx * 0.72, sy, cz + 3 }, new double[] { side * (sx + 1), sy - 8, cz + 5 }, Z, 1.8, 1.2, st.trimDark);
			}
			break;
		case BRINE: // a red sash, brass buttons, the skull and crossbones on the back
			decal(add, m, F, new double[][] { { hw * 0.45, top + 2 }, { hw * 0.8, top + 2 }, { -hw * 0.6, bottom - 1 }, { -hw * 0.95, bottom - 1 } }, 0.6,
					1.4, st.accent, hsl(0, 6, 26));
			for (int k = 0; k < 3; k++) {
				decal(add, m, F, diamond(-hw * 0.32, top + h * (0.3 + k * 0.2), 1.3, 1.3), 0.4, 2.2, st.trim, st.trimDark);
			}
			skull(add, m, B, 0, cy, h * 0.36, st);
			break;
		case INITIATE: // plain pauldrons, the night-blue tabard and the star
			for (int side = -1; side <= 1; side += 2) {
				double[] c = { side * sx, sy, -1 };
				double[] axis = norm(new double[] { side * 0.6, -0.8, 0 });
				dome(add, c, axis, Z, hw * 0.5, hw * 0.4, hw * 0.32, 6, st.dark, st.main);
				ring(add, c, axis, Z, hw * 0.55, hw * 0.45, -1.2, 2.0, 6, st.trim);
			}
			decal(add, m, F, rect(-hw * 0.34, top + h * 0.2, hw * 0.34, bottom - 2), 0.6, 1.5, st.trim, st.trimDark);
			ArmourSets.star4(add, m, F, 0, cy, h * 0.3, st.accent, st.light, st.core);
			break;
		case BARROW: // a bronze pectoral, the old kingdom's sun
			decal(add, m, F, new double[][] { { -hw * 0.62, top + 1 }, { hw * 0.62, top + 1 }, { hw * 0.32, top + h * 0.24 }, { -hw * 0.32, top + h * 0.24 } },
					0.6, 1.6, st.trim, st.trimDark);
			ArmourSets.sun(add, m, F, 0, cy + 3, h * 0.38, st);
			break;
		case VEIL: // the open eye, and a hem that frays into strips
			ArmourSets.eye(add, m, F, 0, cy, hw * 0.32, st);
			double[] hem = hem(m, bottom); // the hem's own size, not the shoulders'
			for (int k = 0; k < 10; k++) {
				double a = Math.PI * 2 * k / 10 + 0.3;
				double x = hem[0] + Math.cos(a) * hem[2] * 0.92, z = hem[1] + Math.sin(a) * hem[3] * 0.92;
				blade(add, new double[] { x, bottom - 3, z }, new double[] { x * 1.05, bottom + 5 + (k % 3) * 2, z + Math.sin(a) * 1.5 },
						new double[] { -Math.sin(a), 0, Math.cos(a) }, 2.6, 0.6, k % 2 == 0 ? st.dark : st.main);
			}
			break;
		default:
			break;
		}
		return add;
	}

	/** A cloth panel as an n x rows grid of decals, so it follows a bumpy surface (mail) instead of the rings poking through. */
	static void strips(ModelData add, ModelData m, Frame f, double x0, double y0, double x1, double y1, int n, int rows, double hOut, int top,
			int side) {
		for (int i = 0; i < n; i++) {
			double a = x0 + (x1 - x0) * i / n, b = x0 + (x1 - x0) * (i + 1) / n;
			for (int j = 0; j < rows; j++) {
				double c = y0 + (y1 - y0) * j / rows, d = y0 + (y1 - y0) * (j + 1) / rows;
				decal(add, m, f, rect(a, c, b, d), 0.8, hOut, top, side);
			}
		}
	}

	/** Centre and half-extents {cx, cz, rx, rz} of a mesh's vertices within 5 of its bottom edge. */
	static double[] hem(ModelData m, double bottom) {
		double x0 = 1e9, x1 = -1e9, z0 = 1e9, z1 = -1e9;
		for (int[] v : m.vertices) {
			if (v[1] >= bottom - 5) {
				x0 = Math.min(x0, v[0]);
				x1 = Math.max(x1, v[0]);
				z0 = Math.min(z0, v[2]);
				z1 = Math.max(z1, v[2]);
			}
		}
		return new double[] { (x0 + x1) / 2, (z0 + z1) / 2, (x1 - x0) / 2, (z1 - z0) / 2 };
	}

	/** A small skull over crossed bones. */
	static void skull(ModelData add, ModelData m, Frame f, double x, double y, double size, Style st) {
		double s = size / 2;
		decal(add, m, f, bar(x - s, y - s * 0.6, x + s, y + s * 0.9, s * 0.16), 0.5, 1.8, st.core, st.trimDark);
		decal(add, m, f, bar(x + s, y - s * 0.6, x - s, y + s * 0.9, s * 0.16), 0.5, 1.8, st.core, st.trimDark);
		double r = s * 0.5;
		double[][] head = new double[7][];
		for (int k = 0; k < 7; k++) {
			double a = Math.PI * 2 * k / 7 - Math.PI / 2;
			head[k] = new double[] { x + Math.cos(a) * r, y - s * 0.15 + Math.sin(a) * r * (Math.sin(a) > 0 ? 1.25 : 1) };
		}
		decal(add, m, f, head, 0.5, 2.4, st.core, st.trimDark);
		for (int side = -1; side <= 1; side += 2) {
			decal(add, m, f, diamond(x + side * r * 0.42, y - s * 0.12, r * 0.22, r * 0.24), 0.3, 2.8, st.dark, st.dark);
		}
	}

	/** A bar of half-width w from (x0, y0) to (x1, y1), as a convex outline. */
	static double[][] bar(double x0, double y0, double x1, double y1, double w) {
		double dx = x1 - x0, dy = y1 - y0, l = Math.hypot(dx, dy), nx = -dy / l * w, ny = dx / l * w;
		return new double[][] { { x0 + nx, y0 + ny }, { x1 + nx, y1 + ny }, { x1 - nx, y1 - ny }, { x0 - nx, y0 - ny } };
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
			case RECRUIT:
				kneePlate(one, m, x, kneeY, st.dark, st.trimDark);
				break;
			case INITIATE:
				kneePlate(one, m, x, kneeY, st.trim, st.trimDark);
				break;
			case BARROW:
				kneePlate(one, m, x, kneeY, st.trim, st.trimDark);
				decal(one, m, F, diamond(x, kneeY, 1.6, 1.6), 0.3, 3.2, st.core, st.trim);
				break;
			case RATTLE:
				decal(one, m, F, diamond(x, kneeY, 4.2, 5.0), 0.6, 2.2, st.trim, st.trimDark);
				break;
			case WOODSMAN:
				decal(one, m, F, new double[][] { { x - 4, kneeY - 5 }, { x + 4, kneeY - 5 }, { x + 4.5, kneeY + 5 }, { x - 4.5, kneeY + 5 } }, 0.6, 1.5,
						st.leatherDark, st.leatherDark);
				break;
			default:
				break;
			}
		}
		switch (st.motif) {
		case WOODSMAN: // a belt pouch
			decal(fr, m, F, rect(hw * 0.2, top + 3, hw * 0.55, top + 11), 0.6, 2.4, st.trim, st.trimDark);
			break;
		case CROFT: { // the apron, a pocket and a sprig in it
			decal(fr, m, F, rect(-hw * 0.5, top + 2, hw * 0.5, top + h * 0.62), 0.6, 1.2, st.light, st.dark);
			decal(fr, m, F, rect(-hw * 0.32, top + h * 0.28, hw * 0.02, top + h * 0.42), 0.6, 2.2, st.dark, st.trimDark);
			double[] p = project(m, F, -hw * 0.15, top + h * 0.28);
			blade(fr, add(p, new double[] { 0, 1, -2.5 }), add(p, new double[] { 1.5, -7, -3.5 }), Z, 1.5, 0.5, st.trim);
			break;
		}
		case RATTLE: { // a rag at the hip
			double[] p = project(m, F, -hw * 0.62, top + 4);
			blade(fr, add(p, new double[] { 0, 0, -1 }), add(p, new double[] { -1, 15, -2 }), X, 2.4, 0.5, st.accent);
			break;
		}
		case BRINE: { // the red sash round the waist, its tail at the hip
			for (Frame f : new Frame[] { F, B }) {
				decal(fr, m, f, rect(-hw * 1.05, top + 1, hw * 1.05, top + 6), 0.7, 1.6, st.accent, hsl(0, 6, 26));
			}
			double[] p = project(m, F, hw * 0.6, top + 5);
			blade(fr, add(p, new double[] { 0, 0, -1.5 }), add(p, new double[] { 1.5, 14, -2.5 }), X, 2.4, 0.5, st.accent);
			decal(fr, m, F, diamond(hw * 0.6, top + 3.5, 1.5, 1.5), 0.4, 2.6, st.trim, st.trimDark);
			break;
		}
		case INITIATE: // a night-blue tasset down the front
			decal(fr, m, F, rect(-hw * 0.22, top + 2, hw * 0.22, top + h * 0.3), 0.8, 1.8, st.trim, st.trimDark);
			break;
		case BARROW: // bronze strips (pteruges) over the hips
			for (int k = -2; k <= 2; k++) {
				double x = k * hw * 0.28;
				decal(fr, m, F, rect(x - hw * 0.11, top + 2, x + hw * 0.11, top + h * 0.22), 0.8, 1.8, st.trim, st.trimDark);
			}
			break;
		case VEIL: { // violet marks down the front, a frayed hem
			for (int k = 0; k < 3; k++) {
				decal(fr, m, F, diamond(0, top + h * (0.22 + k * 0.2), 1.6, 2.4), 0.4, 1.8, st.accent, st.trimDark);
			}
			for (int k = 0; k < 12; k++) {
				double a = Math.PI * 2 * k / 12 + 0.2;
				double x = Math.cos(a) * hw * 0.9, z = cz + Math.sin(a) * (back - front) / 2 * 0.9;
				blade(fr, new double[] { x, bottom - 4, z }, new double[] { x * 1.04, bottom + 2 + (k % 3), z }, new double[] { -Math.sin(a), 0, Math.cos(a) },
						2.8, 0.6, k % 2 == 0 ? st.dark : st.main);
			}
			break;
		}
		default:
			break;
		}
		return new ModelData[] { knees[0], knees[1], fr };
	}

	static void kneePlate(ModelData one, ModelData m, double x, double y, int top, int side) {
		decal(one, m, ArmourSets.FRONT, new double[][] { { x, y - 8 }, { x + 6, y - 1.5 }, { x + 4.2, y + 6 }, { x, y + 9 }, { x - 4.2, y + 6 },
				{ x - 6, y - 1.5 } }, 1, 2.2, top, side);
	}

	/* ------------------------------------------------------------- the shield */

	static ModelData shield(Style st, ModelData m, Frame f) {
		double[] e = extent(m, f); // u0, u1, v0, v1
		double w = (e[1] - e[0]) / 2, hgt = (e[3] - e[2]) / 2, cu = (e[0] + e[1]) / 2, cv = (e[2] + e[3]) / 2;
		double s = Math.min(w, hgt);
		ModelData add = new ModelData();
		switch (st.motif) {
		case RECRUIT:
			ArmourSets.star4(add, m, f, cu, cv, s * 1.2, st.accent, st.light, st.core);
			break;
		case RATTLE: // crossed bones nailed over the hatch, a skull at the cross
			decal(add, m, f, bar(cu - s * 0.75, cv - s * 0.75, cu + s * 0.75, cv + s * 0.75, s * 0.1), 0.5, 2.0, st.trim, st.trimDark);
			decal(add, m, f, bar(cu + s * 0.75, cv - s * 0.75, cu - s * 0.75, cv + s * 0.75, s * 0.1), 0.5, 2.0, st.trim, st.trimDark);
			for (int k = -1; k <= 1; k += 2) { // the hatch's planks
				decal(add, m, f, rect(cu + k * s * 0.33 - 0.5, cv - hgt * 0.92, cu + k * s * 0.33 + 0.5, cv + hgt * 0.92), 0.3, 0.9, st.leatherDark,
						st.leatherDark);
			}
			skull(add, m, f, cu, cv, s * 0.7, st);
			break;
		case INITIATE:
			decal(add, m, f, diamond(0, 2, 12, 21), 0.5, 1.6, st.trim, st.trimDark);
			ArmourSets.star4(add, m, f, 0, 1, 24, st.accent, st.light, st.core);
			break;
		case BARROW:
			ArmourSets.sun(add, m, f, cu, cv, s * 1.4, st);
			break;
		default:
			break;
		}
		return add;
	}

	/** The extent of a mesh in a frame's (u, v) plane: {u0, u1, v0, v1}. */
	static double[] extent(ModelData m, Frame f) {
		double[] e = { 1e9, -1e9, 1e9, -1e9 };
		for (int[] v : m.vertices) {
			double[] q = sub(new double[] { v[0], v[1], v[2] }, f.o);
			double u = dot(q, f.u), w = dot(q, f.v);
			e[0] = Math.min(e[0], u);
			e[1] = Math.max(e[1], u);
			e[2] = Math.min(e[2], w);
			e[3] = Math.max(e[3], w);
		}
		return e;
	}

	/* ------------------------------------------------------------- boots, gloves */

	static ModelData boots(Style st, ModelData m) {
		int[] bb = m.bounds();
		double hw = bb[3], front = bb[2], top = bb[1];
		ModelData add = new ModelData();
		for (int side = -1; side <= 1; side += 2) {
			double x = side * hw * 0.56;
			switch (st.motif) {
			case RECRUIT:
			case INITIATE:
				toeCap(add, x, front, st.main);
				break;
			case RATTLE:
				toeCap(add, x, front, st.trim);
				break;
			case BARROW:
				toeCap(add, x, front, st.trim);
				cuff(add, m, side, top, top + 3, 0.6, st.trim, st.trimDark);
				break;
			case WOODSMAN:
				cuff(add, m, side, top, top + 4.5, 0.9, st.light, st.dark);
				break;
			case BRINE:
				cuff(add, m, side, top, top + 4.5, 0.9, st.leather, st.leatherDark);
				add.tube(new double[] { x, top + 6, front + 2.5 }, new double[] { x, top + 6, front }, 1.6, 1.6, 4, 0.78, st.trim, st.trim);
				break;
			case VEIL:
				cuff(add, m, side, top, top + 3, 0.6, st.trimDark, st.trimDark);
				break;
			case CROFT: // a moss-green tie round the top
				cuff(add, m, side, top, top + 2.5, 0.5, st.trim, st.trimDark);
				break;
			default:
				break;
			}
		}
		return add;
	}

	static void toeCap(ModelData add, double x, double front, int col) {
		add.tube(new double[] { x, -3.5, front + 7 }, new double[] { x, -1, front }, 4.2, 0, 4, 0.78, col, col);
	}

	/** A band round the top of one boot (side -1 or 1), sized from the boot's own rim. */
	static void cuff(ModelData add, ModelData m, int side, double y0, double y1, double grow, int col, int col2) {
		double cx = 0, cz = 0, r = 0;
		int n = 0;
		for (int[] v : m.vertices) {
			if (Math.signum(v[0]) == side && v[1] <= y0 + 3) {
				cx += v[0];
				cz += v[2];
				n++;
			}
		}
		if (n == 0) {
			return;
		}
		cx /= n;
		cz /= n;
		for (int[] v : m.vertices) {
			if (Math.signum(v[0]) == side && v[1] <= y0 + 3) {
				r = Math.max(r, Math.hypot(v[0] - cx, v[2] - cz));
			}
		}
		add.tube(new double[] { cx, y0 - 0.5, cz }, new double[] { cx, y1, cz }, r + grow, r + grow * 0.8, 6, 0.3, col, col2);
	}

	static ModelData gloves(Style st, ModelData m) {
		int[] bb = m.bounds();
		double hw = bb[3], top = bb[1];
		ModelData add = new ModelData();
		for (int side = -1; side <= 1; side += 2) {
			double x = side * (hw - 6.5);
			switch (st.motif) {
			case RECRUIT:
				gauntlet(add, x, top, 6.2, st.leather, st.leatherDark, st.leatherDark);
				break;
			case INITIATE:
				gauntlet(add, x, top, 7.6, st.main, st.dark, st.trim);
				break;
			case BARROW:
				gauntlet(add, x, top, 6.6, st.trim, st.trimDark, st.main);
				break;
			case RATTLE: // knucklebones
				gauntlet(add, x, top, 6.2, st.leather, st.leatherDark, st.accent);
				for (int k = -1; k <= 1; k++) {
					add.tube(new double[] { x + side * 2, top + 18, -2 + k * 2.5 }, new double[] { x + side * 2, top + 18, -5 + k * 2.5 }, 1.3, 1.0, 4, 0.78,
							st.trim, st.trim);
				}
				break;
			case WOODSMAN:
			case BRINE:
				add.tube(new double[] { x, top + 2, 0.5 }, new double[] { x, top + 5, 0.5 }, 5.2, 5.2, 6, 0.26, st.trim, st.trim);
				break;
			case CROFT:
				add.tube(new double[] { x, top + 7, 1 }, new double[] { x, top + 9.5, 1 }, 6.2, 6.2, 6, 0.26, st.trim, st.trim);
				break;
			case VEIL:
				add.tube(new double[] { x, top + 7, 1 }, new double[] { x, top + 9.5, 1 }, 6.2, 6.2, 6, 0.26, st.trim, st.trimDark);
				break;
			default:
				break;
			}
		}
		return add;
	}

	/** A flared gauntlet cuff over the wrist, banded at the top. */
	static void gauntlet(ModelData add, double x, double top, double flare, int col, int col2, int band) {
		add.tube(new double[] { x, top - 2, 1 }, new double[] { x, top + 10, 1 }, flare - 2.2, flare, 6, 0.26, col, col2);
		add.tube(new double[] { x, top + 9, 1 }, new double[] { x, top + 11.5, 1 }, flare + 0.3, flare + 0.3, 6, 0.26, band, band);
	}
}
