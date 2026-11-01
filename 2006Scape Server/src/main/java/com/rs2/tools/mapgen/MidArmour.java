package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ArmourForge.*;
import static com.rs2.tools.mapgen.LowArmour.X;
import static com.rs2.tools.mapgen.LowArmour.Y;
import static com.rs2.tools.mapgen.LowArmour.Z;
import static com.rs2.tools.mapgen.ModelData.hsl;

import com.rs2.game.content.voidrealm.VoidConstants;
import com.rs2.tools.mapgen.ArmourForge.Frame;
import com.rs2.tools.mapgen.ArmourSets.Kind;
import com.rs2.tools.mapgen.ArmourSets.Motif;
import com.rs2.tools.mapgen.ArmourSets.Style;

/**
 * The ten middle armour sets (levels 20-60), between the low sets and the saga's own. Each is cut from the same stock
 * meshes as {@link LowArmour} and belongs to a place or an enemy of Nythera that no other set has claimed:
 *
 * <ol start="15">
 * <li>Quarryhand (Defence 20): Tess's quarry crews: dusty mail, a leather apron, a candle-lamp on the helm.</li>
 * <li>Silkspinner (Ranged 25): the Silkwood stalkers' leathers, bound in spider silk, a web across the back.</li>
 * <li>Stonecaller (Magic 30): the waystones' keepers: slate robes, a ring of standing stones round the hat, violet runes.</li>
 * <li>Ridgerunner (Ranged 35): the north pass's runners: snow-pale hide, white fur ruffs, a rope over the shoulder.</li>
 * <li>Ravager (Defence 40): the Scar reavers, who wear the Hollow ravagers' bone carapace over black iron.</li>
 * <li>Moonmourn (Magic 45): those who still mourn Aurel by the moon: midnight robes, a silver crescent and tears.</li>
 * <li>Rimebound (Defence 50): Frostfang's frozen soldiers: ice-blue mail, an icicle crown, a snowflake shield.</li>
 * <li>Nightrunner (Ranged 50): Smugglers' Cove's night crews: sea-teal and charcoal, a face-mask and a hooded lantern.</li>
 * <li>Farseer (Magic 55): Seer Ilsabet's order: deep teal and gold, a scrying mirror on the breast.</li>
 * <li>Last knight (Defence 60): Aurel's last knights: tarnished steel, crimson, and the king's broken crown.</li>
 * </ol>
 */
final class MidArmour {

	private MidArmour() {
	}

	static final Style[] MID = {
			new Style("Quarryhand", "quarryhand", Kind.CHAIN, Motif.QUARRY, 20, hsl(4, 1, 54), hsl(4, 1, 36), hsl(4, 1, 70), hsl(5, 4, 34),
					hsl(5, 4, 22), hsl(8, 6, 84), hsl(9, 2, 120), hsl(5, 3, 30), hsl(5, 3, 20), VoidConstants.QUARRYHAND_SET,
					new String[] { "Quarryhand med helm", "Quarryhand chainbody", "Quarryhand platelegs", "Quarryhand sq shield", "Quarryhand boots",
							"Quarryhand gloves" },
					new String[] { "A miner's helm with a candle-lamp on the brow. Tess swears by it; the candle swears back.",
							"Dusty mail under a leather apron. There's a hammer, and a great deal of rock, in the pockets.",
							"Plate legs with leather knee-pads. The quarry floor is hard on knees.", "A pick and a hammer, crossed: the quarry's mark.",
							"Steel-toed boots, for when the rock falls the wrong way.", "Thick work gloves. Nothing short of a falling boulder gets through." }),
			new Style("Silkspinner", "silkspinner", Kind.LEATHER, Motif.SILK, 25, hsl(20, 1, 32), hsl(20, 1, 21), hsl(20, 1, 46), hsl(10, 0, 106),
					hsl(10, 0, 82), hsl(0, 6, 36), hsl(9, 1, 112), hsl(6, 3, 26), hsl(6, 3, 16), VoidConstants.SILKSPINNER_SET,
					new String[] { "Silkspinner hood", "Silkspinner jerkin", "Silkspinner chaps", null, "Silkspinner boots", "Silkspinner vambraces" },
					new String[] { "Bound in Silkwood silk. The fangs at the brow are real; so were the eyes.",
							"A stalker's jerkin, a web spun across the back. The spiders don't seem to mind.",
							"Silk-wrapped at the shin, so they make no sound in the undergrowth.", null, "Soft boots, silk-bound. They stick to branches.",
							"Bracers wound with silk. The bowstring never slips." }),
			new Style("Stonecaller", "stonecaller", Kind.ROBE, Motif.STONE, 30, hsl(34, 1, 46), hsl(34, 1, 30), hsl(34, 1, 62), hsl(8, 1, 64),
					hsl(8, 1, 44), ArmourSets.violet(96, 6), hsl(42, 0, 120), hsl(6, 2, 30), hsl(6, 2, 20), VoidConstants.STONECALLER_SET,
					new String[] { "Stonecaller hat", "Stonecaller robe top", "Stonecaller robe bottom", null, "Stonecaller boots", "Stonecaller gloves" },
					new String[] { "A ring of little standing stones round the brim. They hum when a waystone is near.",
							"Slate-grey robes and a carved stone on a cord. The rune on it is awake.",
							"The waystones' runes, stitched down the front in violet thread.", null, "Grey boots, dusty from walking stone to stone.",
							"A rune glows on each cuff when you touch a waystone." }),
			new Style("Ridgerunner", "ridgerunner", Kind.HIDE, Motif.RIDGE, 35, hsl(0, 0, 86), hsl(0, 0, 66), hsl(0, 0, 104), hsl(8, 1, 114),
					hsl(8, 1, 94), hsl(36, 4, 76), hsl(36, 1, 118), hsl(6, 2, 60), hsl(6, 2, 46), VoidConstants.RIDGERUNNER_SET,
					new String[] { "Ridgerunner coif", "Ridgerunner body", "Ridgerunner chaps", null, "Ridgerunner boots", "Ridgerunner vambraces" },
					new String[] { "A fur ruff against the wind off the pass. Morra's runners swear by it.",
							"Snow-pale hide and a coil of rope. The pass doesn't forgive a missed step.",
							"Fur-wrapped at the knee, for kneeling in snow to take a shot.", null, "Fur-topped snow boots with iron studs under the heel.",
							"Fur-cuffed bracers. Cold fingers miss." }),
			new Style("Ravager", "ravager", Kind.PLATE, Motif.RAVAGER, 40, hsl(8, 1, 92), hsl(8, 1, 70), hsl(8, 0, 108), hsl(0, 0, 20),
					hsl(0, 0, 11), ArmourSets.violet(84, 6), hsl(42, 0, 124), hsl(0, 0, 14), hsl(0, 0, 8), VoidConstants.RAVAGER_SET,
					new String[] { "Ravager full helm", "Ravager platebody", "Ravager platelegs", "Ravager kiteshield", "Ravager boots",
							"Ravager gauntlets" },
					new String[] { "A ravager's jaw, worn as a visor. The Scar reavers wear what they kill.",
							"Bone carapace over black iron, the claws left on at the shoulder.",
							"Bone plates on black legs. They click when you walk.", "A ravager's maw, still open. Something violet is looking out.",
							"Clawed sabatons. The reavers don't kick, they rake.", "Black gauntlets tipped with a ravager's claws." }),
			new Style("Moonmourn", "moonmourn", Kind.ROBE, Motif.MOON, 45, hsl(42, 3, 26), hsl(42, 3, 16), hsl(42, 2, 38), hsl(40, 0, 98),
					hsl(40, 0, 74), hsl(38, 3, 104), hsl(40, 0, 124), hsl(42, 2, 14), hsl(42, 2, 9), VoidConstants.MOONMOURN_SET,
					new String[] { "Moonmourn hat", "Moonmourn robe top", "Moonmourn robe bottom", null, "Moonmourn boots", "Moonmourn gloves" },
					new String[] { "A mourning veil and a silver moon. Aurel's mourners never stopped.",
							"Midnight robes with the moon on the breast, and three silver tears under it.",
							"The moon that mourned him, sewn round the hem.", null, "Soft boots for walking among graves at night.",
							"Silver-cuffed gloves, cold as moonlight." }),
			new Style("Rimebound", "rimebound", Kind.CHAIN, Motif.RIME, 50, hsl(36, 2, 74), hsl(36, 2, 54), hsl(36, 1, 92), hsl(36, 4, 100),
					hsl(36, 4, 80), hsl(38, 5, 62), hsl(36, 1, 122), hsl(30, 0, 88), hsl(30, 0, 68), VoidConstants.RIMEBOUND_SET,
					new String[] { "Rimebound med helm", "Rimebound chainbody", "Rimebound platelegs", "Rimebound sq shield", "Rimebound boots",
							"Rimebound gloves" },
					new String[] { "A crown of ice that grew on its own, a little more every year in Frostfang.",
							"Mail frozen so long it's half ice. The icicles never melt.", "Frost-crusted legs. They creak like a frozen lake.",
							"A snowflake as big as a shield, set in the shield.", "Ice-capped boots. They never slip on the cave floor.",
							"Gauntlets of frozen mail, spiked with rime." }),
			new Style("Nightrunner", "nightrunner", Kind.HIDE, Motif.NIGHT, 50, hsl(30, 2, 28), hsl(30, 2, 18), hsl(30, 2, 40), hsl(0, 0, 22),
					hsl(0, 0, 13), hsl(8, 6, 84), hsl(9, 3, 116), hsl(5, 2, 20), hsl(5, 2, 12), VoidConstants.NIGHTRUNNER_SET,
					new String[] { "Nightrunner coif", "Nightrunner body", "Nightrunner chaps", null, "Nightrunner boots", "Nightrunner vambraces" },
					new String[] { "A hood and a face-mask. Nobody at the Cove has ever seen a nightrunner's face.",
							"Sea-teal hide, a dark half-cloak and a hooded lantern for signalling ships.",
							"A knife on the thigh. Nightrunners don't like to be followed.", null, "Soft boots that make no sound on the shingle.",
							"Dark bracers stitched in lantern-amber." }),
			new Style("Farseer", "farseer", Kind.ROBE, Motif.SEER, 55, hsl(32, 3, 30), hsl(32, 3, 20), hsl(32, 2, 44), hsl(8, 4, 64), hsl(8, 5, 44),
					hsl(32, 4, 100), hsl(32, 1, 122), hsl(32, 2, 18), hsl(32, 2, 12), VoidConstants.FARSEER_SET,
					new String[] { "Farseer hat", "Farseer robe top", "Farseer robe bottom", null, "Farseer boots", "Farseer gloves" },
					new String[] { "A gold circlet with a seeing-crystal at the brow. It shows you things a moment early.",
							"A scrying mirror on the breast. Ilsabet's order saw the portal coming, and wore these to meet it.",
							"Deep teal with gold, and a crystal at the belt.", null, "Gold-cuffed boots that always seem to know the way.",
							"Gold-cuffed gloves for turning the pages of what hasn't happened yet." }),
			new Style("Last knight", "lastknight", Kind.PLATE, Motif.KNIGHT, 60, hsl(42, 1, 46), hsl(42, 1, 30), hsl(42, 1, 62), hsl(8, 3, 54),
					hsl(8, 4, 36), hsl(0, 5, 34), hsl(9, 2, 100), hsl(0, 4, 24), hsl(0, 4, 16), VoidConstants.LAST_KNIGHT_SET,
					new String[] { "Last knight's full helm", "Last knight's platebody", "Last knight's platelegs", "Last knight's kiteshield",
							"Last knight's boots", "Last knight's gauntlets" },
					new String[] { "A great helm under a broken crown, and a crimson plume. Aurel's last knights wore them into the dark.",
							"Tarnished plate and a crimson tabard with the king's broken crown. They kept faith longer than the king did.",
							"Old steel, still sound, with a crimson tasset.", "The broken crown on crimson. Vaelric carried one like it.",
							"Gilt-toed boots that marched a long way to nowhere.", "Gauntlets with a gold band at the cuff. They still salute." }), };

	/** The square shield's face and rim colours. */
	static int shieldFace(Style st) {
		if (st.motif.draft()) {
			return DraftArmour.shieldFace(st);
		}
		return st.motif == Motif.QUARRY ? st.leather : st.main;
	}

	static int shieldRim(Style st) {
		if (st.motif.draft()) {
			return DraftArmour.shieldRim(st);
		}
		return st.motif == Motif.RIME ? st.trim : st.dark;
	}

	/* ------------------------------------------------------------- the helm */

	static ModelData helm(Style st, ModelData m) {
		if (st.motif.draft()) {
			return DraftArmour.helm(st, m);
		}
		int[] bb = m.bounds();
		double hw = bb[3], top = bb[1], bottom = bb[4], h = bottom - top, front = bb[2], back = bb[5], cz = (front + back) / 2, dz = (back - front) / 2;
		ModelData add = new ModelData();
		Frame F = ArmourSets.FRONT;
		switch (st.motif) {
		case QUARRY: { // a leather strap round the brow, a candle-lamp on the front of it
			double by = top + h * 0.24;
			ring(add, new double[] { 0, by, cz }, new double[] { 0, -1, 0 }, X, hw * 1.0, dz * 1.0, -0.8, 0.8, 10, st.trim);
			double[] p = project(m, F, 0, by);
			add.tube(add(p, new double[] { 0, 2, -0.5 }), add(p, new double[] { 0, 2, -5 }), 2.6, 2.6, 4, Math.PI / 4, st.trimDark, st.trimDark);
			add.tube(add(p, new double[] { 0, 0.5, -2.8 }), add(p, new double[] { 0, -3.5, -2.8 }), 1.3, 1.3, 5, 0, st.light, st.light);
			blade(add, add(p, new double[] { 0, -3.5, -2.8 }), add(p, new double[] { 0, -9, -2.8 }), X, 1.5, 0.7, st.accent);
			blade(add, add(p, new double[] { 0, -3.8, -3.3 }), add(p, new double[] { 0, -6.5, -3.3 }), X, 0.8, 0.4, st.core);
			break;
		}
		case SILK: { // silk bound round the hood, two fangs at the brow, four red eyes above them
			double by = top + h * 0.22;
			ring(add, new double[] { 0, by, cz }, new double[] { 0, -1, 0 }, X, hw * 1.0, dz * 1.0, -1.0, 1.0, 10, st.trim);
			ring(add, new double[] { 0, by + 4, cz + 1 }, new double[] { 0, -1, 0 }, X, hw * 1.0, dz * 1.0, -0.6, 0.6, 10, st.trimDark);
			for (int k = -1; k <= 1; k += 2) {
				double[] p = project(m, F, k * hw * 0.26, by + 1);
				blade(add, add(p, new double[] { 0, 0, -0.5 }), add(p, new double[] { -k * 0.5, 4.5, -1.8 }), X, 1.1, 0.5, st.core);
			}
			for (int k = 0; k < 4; k++) {
				decal(add, m, F, diamond(-hw * 0.27 + k * hw * 0.18, by - 3.2 + (k == 1 || k == 2 ? -1.0 : 0), 0.7, 0.7), 0.3, 1.9, st.accent,
						st.accent);
			}
			break;
		}
		case STONE: { // a ring of little standing stones round the brim, a waystone rune on the front
			double by = bottom - h * 0.3;
			ring(add, new double[] { 0, by, cz }, new double[] { 0, -1, 0 }, X, hw * 0.98, dz * 0.98, 0, 1.6, 10, st.trimDark);
			for (int k = 0; k < 7; k++) {
				double a = Math.PI * 2 * k / 7 + Math.PI / 2;
				double[] b = { Math.cos(a) * hw * 1.0, by - 1, cz + Math.sin(a) * dz * 1.0 };
				add.tube(b, add(b, new double[] { 0, -(6 + (k % 3) * 1.5), 0 }), 2.0, 1.6, 4, a, st.trim, st.light);
			}
			glyph(add, m, F, 0, by - h * 0.16, 5.0, st);
			break;
		}
		case RIDGE: // a white fur ruff round the neck, an ice-blue bead at the throat
			for (int k = 0; k < 14; k++) {
				double a = Math.PI * 2 * k / 14;
				double x = Math.cos(a) * hw * 0.92, z = cz + Math.sin(a) * dz * 0.92;
				blade(add, new double[] { x, bottom - 6, z }, new double[] { x * 1.12, bottom + 2, cz + (z - cz) * 1.15 },
						new double[] { -Math.sin(a), 0, Math.cos(a) }, 3.0, 1.2, k % 2 == 0 ? st.trim : st.trimDark);
			}
			add.tube(new double[] { 0, bottom - 3, front - 1.5 }, new double[] { 0, bottom - 0.5, front - 2.5 }, 1.5, 1.2, 5, 0, st.accent, st.core);
			break;
		case RAVAGER: // a ravager's jaw for a visor, a spined ridge, violet eye-slits
			for (int side = -1; side <= 1; side += 2) {
				double[] r = { side * hw * 0.72, bottom - h * 0.22, front + 4 }, a = { side * hw * 0.42, bottom + 2, front - 1.5 },
						b = { side * hw * 0.12, bottom - 1, front - 3 };
				blade(add, r, a, Z, 2.4, 1.2, st.light);
				blade(add, a, b, Z, 1.6, 0.8, st.core);
				decal(add, m, F, diamond(side * hw * 0.3, top + h * 0.45, 1.6, 0.8), 0.3, 2.0, st.accent, st.accent);
			}
			for (int k = 0; k < 4; k++) {
				double z = front + 4 + (back - front - 6) * k / 3.0;
				blade(add, new double[] { 0, top + 2, z }, new double[] { 0, top - (k == 1 ? 9 : 6), z + 5 }, Z, 2.2, 1.0, st.trim);
			}
			break;
		case MOON: { // a silver crescent on the front, a mourning veil hanging over the face
			double by = bottom - h * 0.3;
			ring(add, new double[] { 0, by, cz }, new double[] { 0, -1, 0 }, X, hw * 0.98, dz * 0.98, 0, 1.8, 10, st.trimDark);
			crescent(add, m, F, 0, by - h * 0.17, 4.2, st.trim, st.trimDark);
			for (int k = -2; k <= 2; k++) {
				double x = k * hw * 0.2;
				blade(add, new double[] { x, bottom - 2, front + 3 }, new double[] { x * 1.08, bottom + 12 - Math.abs(k) * 1.5, front - 1 }, X, 2.6, 0.5,
						k % 2 == 0 ? st.dark : st.leather);
			}
			break;
		}
		case RIME: // a crown of ice spikes, icicles hanging from the rim
			for (int k = 0; k < 9; k++) {
				double a = Math.PI * 2 * k / 9 + Math.PI / 2;
				double x = Math.cos(a) * hw * 0.78, z = cz + Math.sin(a) * dz * 0.78;
				blade(add, new double[] { x, top + h * 0.25, z }, new double[] { x * 1.12, top - 4 - (k % 2) * 4 - (k == 0 ? 3 : 0), cz + (z - cz) * 1.12 },
						new double[] { -Math.sin(a), 0, Math.cos(a) }, 2.0, 1.0, k % 2 == 0 ? st.trim : st.light);
				if (k % 2 == 1) {
					double xi = Math.cos(a) * hw * 1.0, zi = cz + Math.sin(a) * dz * 1.0;
					add.tube(new double[] { xi, bottom - 2, zi }, new double[] { xi, bottom + 4, zi }, 1.0, 0, 4, 0, st.trim, st.trim);
				}
			}
			break;
		case NIGHT: // a face-mask over the nose and mouth, the hood knotted at the back
			ring(add, new double[] { 0, top + h * 0.66, cz }, new double[] { 0, -1, 0 }, X, hw * 0.8, dz * 1.0, 0, h * 0.17, 10, st.trim);
			for (int side = -1; side <= 1; side += 2) {
				blade(add, new double[] { side * 1.2, top + h * 0.5, back }, new double[] { side * 4.0, top + h * 0.5 + 11, back + 3 }, X, 2.0, 0.5, st.trim);
			}
			break;
		case SEER: { // a gold circlet, a seeing-crystal hanging at the brow, a gold tassel down the back
			double by = bottom - h * 0.3;
			ring(add, new double[] { 0, by, cz }, new double[] { 0, -1, 0 }, X, hw * 1.0, dz * 1.0, -0.2, 1.8, 10, st.trim);
			double[] p = { 0, by - 1.5, cz - dz }; // on the circlet itself: the hat is open at the face, so nothing to project onto
			add.tube(add(p, new double[] { 0, 0, -1.2 }), add(p, new double[] { 0, 2, -1.6 }), 0.4, 1.8, 4, Math.PI / 4, st.accent, st.accent);
			add.tube(add(p, new double[] { 0, 2, -1.6 }), add(p, new double[] { 0, 5, -1.6 }), 1.8, 0, 4, Math.PI / 4, st.core, st.accent);
			blade(add, new double[] { 0, top + 4, cz + 3 }, new double[] { 0, by + 8, back + 4 }, X, 1.4, 0.6, st.trim);
			break;
		}
		case KNIGHT: // a broken crown round the helm, a crimson plume
			for (int k = 0; k < 8; k++) {
				double a = Math.PI * 2 * k / 8 + Math.PI / 2;
				double x = Math.cos(a) * hw * 0.92, z = cz + Math.sin(a) * dz * 0.92;
				double tall = k == 3 ? 2.5 : k % 2 == 0 ? 8 : 5.5; // one point snapped off
				blade(add, new double[] { x, top + h * 0.2, z }, new double[] { x * 1.04, top + h * 0.2 - tall, cz + (z - cz) * 1.04 },
						new double[] { -Math.sin(a), 0, Math.cos(a) }, 2.2, 1.0, k % 2 == 0 ? st.trim : st.trimDark);
			}
			ring(add, new double[] { 0, top + h * 0.2, cz }, new double[] { 0, -1, 0 }, X, hw * 0.94, dz * 0.94, -0.5, 1.4, 10, st.trim);
			for (int k = 0; k < 4; k++) {
				double z = cz - 2 + k * 3;
				blade(add, new double[] { 0, top + 1, z }, new double[] { 0, top - 7 + k * 2.5, z + 7 + k * 2 }, Z, 2.4, 1.1,
						k % 2 == 0 ? st.accent : st.leather);
			}
			break;
		default:
			break;
		}
		return add;
	}

	/* ------------------------------------------------------------- the body */

	static ModelData body(Style st, ModelData m) {
		if (st.motif.draft()) {
			return DraftArmour.body(st, m);
		}
		int[] bb = m.bounds();
		double hw = bb[3], top = bb[1], bottom = bb[4], h = bottom - top, front = bb[2], back = bb[5], cz = (front + back) / 2;
		double cy = top + h * 0.42, sx = hw * 0.7, sy = top + h * 0.12;
		ModelData add = new ModelData();
		Frame F = ArmourSets.FRONT, B = ArmourSets.BACK;
		switch (st.motif) {
		case QUARRY: { // the leather apron and its straps, a pocket, a hammer at the hip
			LowArmour.strips(add, m, F, -hw * 0.5, top + h * 0.34, hw * 0.5, bottom - 1, 3, 3, 1.9, st.trim, st.trimDark);
			for (int side = -1; side <= 1; side += 2) {
				decal(add, m, F, LowArmour.bar(side * hw * 0.55, top + 2, side * hw * 0.42, top + h * 0.36, 1.1), 0.5, 1.9, st.trim, st.trimDark);
			}
			decal(add, m, F, rect(-hw * 0.3, top + h * 0.62, hw * 0.05, top + h * 0.78), 0.5, 2.5, st.leatherDark, st.leatherDark);
			add.tube(new double[] { hw + 0.5, bottom - 13, cz }, new double[] { hw + 1.5, bottom + 6, cz }, 0.9, 0.9, 4, 0, st.leather, st.leather);
			add.tube(new double[] { hw + 0.8, bottom - 12, cz + 3.5 }, new double[] { hw + 0.8, bottom - 12, cz - 3.5 }, 1.8, 1.8, 4, Math.PI / 4, st.dark,
					st.main);
			break;
		}
		case SILK: { // a web across the back, a silk sash and fang toggles on the front
			double r = Math.min(hw * 0.8, h * 0.42);
			web(add, m, B, 0, cy, r, st.trim, st.trimDark);
			decal(add, m, F, new double[][] { { -hw * 0.75, top + 3 }, { -hw * 0.5, top + 3 }, { hw * 0.72, bottom - 2 }, { hw * 0.47, bottom - 2 } }, 0.6,
					1.6, st.trim, st.trimDark);
			for (double t : new double[] { 0.25, 0.5, 0.75 }) {
				double x = -hw * 0.625 + hw * 1.22 * t, y = top + 3 + (bottom - 5 - top) * t;
				double[] p = project(m, F, x, y);
				blade(add, add(p, new double[] { 0, -1, -1.5 }), add(p, new double[] { 0.5, 3, -2.2 }), X, 0.9, 0.5, st.core);
			}
			break;
		}
		case STONE: { // a carved stone on a cord, waystone runes down the front
			double py = top + h * 0.36;
			for (int side = -1; side <= 1; side += 2) {
				decal(add, m, F, LowArmour.bar(side * hw * 0.4, top + 1, side * hw * 0.08, py - 3, 0.5), 0.4, 1.7, st.leatherDark, st.leatherDark);
			}
			decal(add, m, F, polygon(0, py, hw * 0.2, hw * 0.24, 8), 0.5, 2.2, st.trim, st.trimDark);
			glyph(add, m, F, 0, py, hw * 0.16, st);
			for (int k = 0; k < 3; k++) {
				decal(add, m, F, diamond(0, py + h * 0.16 + k * h * 0.12, 1.3, 2.0), 0.3, 1.9, st.accent, st.trimDark);
			}
			break;
		}
		case RIDGE: { // a rope over the shoulder front and back, fur at the hem, a snowflake on the back
			int rope = hsl(7, 2, 70), ropeDark = hsl(7, 2, 52);
			decal(add, m, F, new double[][] { { hw * 0.45, top + 2 }, { hw * 0.75, top + 2 }, { -hw * 0.62, bottom - 2 }, { -hw * 0.92, bottom - 2 } }, 0.6,
					1.8, rope, ropeDark);
			decal(add, m, B, new double[][] { { -hw * 0.75, top + 2 }, { -hw * 0.45, top + 2 }, { hw * 0.92, bottom - 2 }, { hw * 0.62, bottom - 2 } }, 0.6,
					1.8, rope, ropeDark);
			for (double t : new double[] { 0.2, 0.42, 0.64, 0.86 }) {
				double x = hw * 0.6 - hw * 1.37 * t, y = top + 2 + (bottom - 4 - top) * t;
				decal(add, m, F, rect(x - 2.2, y - 0.5, x + 2.2, y + 0.5), 0.4, 2.3, ropeDark, ropeDark);
			}
			flake(add, m, B, 0, cy - 2, h * 0.32, st.accent, st.core);
			double[] hem = LowArmour.hem(m, bottom);
			for (int k = 0; k < 14; k++) {
				double a = Math.PI * 2 * k / 14;
				double x = hem[0] + Math.cos(a) * hem[2] * 0.95, z = hem[1] + Math.sin(a) * hem[3] * 0.95;
				blade(add, new double[] { x, bottom - 4, z }, new double[] { x * 1.06, bottom + 3, z + Math.sin(a) * 1.5 },
						new double[] { -Math.sin(a), 0, Math.cos(a) }, 2.8, 1.0, k % 2 == 0 ? st.trim : st.trimDark);
			}
			break;
		}
		case RAVAGER: // clawed pauldrons, ribbed black belly plates, a violet slit at the sternum
			for (int side = -1; side <= 1; side += 2) {
				double[] c = { side * sx, sy, -1 };
				double[] axis = norm(new double[] { side * 0.6, -0.8, 0 });
				dome(add, c, axis, Z, hw * 0.52, hw * 0.42, hw * 0.34, 6, st.dark, st.main);
				ring(add, c, axis, Z, hw * 0.57, hw * 0.47, -1.2, 2.0, 6, st.trim);
				double[] tip = add(c, scale(axis, hw * 0.3));
				for (int k = -1; k <= 1; k++) {
					double[] r = add(tip, new double[] { 0, 0, k * 5 });
					double[] mid = add(r, new double[] { side * 6, -7, k * 1.5 });
					blade(add, r, mid, Z, 2.2, 1.4, st.light);
					blade(add, mid, add(mid, new double[] { side * 5, 2, k }), Z, 1.4, 0.9, st.core);
				}
			}
			for (int k = 0; k < 4; k++) {
				double y = cy + 2 + k * h * 0.1, w = hw * (0.56 - k * 0.03);
				decal(add, m, F, rect(-w, y, w, y + 2.0), 0.5, 1.9, st.trim, st.trimDark);
			}
			decal(add, m, F, diamond(0, cy - 6, 1.6, 4.2), 0.4, 2.4, st.accent, st.trim);
			decal(add, m, F, diamond(0, cy - 6, 0.6, 2.0), 0.2, 3.0, st.core, st.accent);
			break;
		case MOON: { // the moon on the breast, three tears under it
			crescent(add, m, F, 0, cy + 1, hw * 0.32, st.trim, st.trimDark);
			for (int k = -1; k <= 1; k++) {
				double x = k * hw * 0.2, y = cy + h * 0.2 + Math.abs(k) * -2;
				decal(add, m, F, new double[][] { { x, y - 2.6 }, { x + 1.1, y }, { x, y + 1.4 }, { x - 1.1, y } }, 0.3, 2.0, st.accent, st.trimDark);
			}
			for (double[] s : new double[][] { { hw * 0.3, cy - 4 }, { hw * 0.4, cy + 4 } }) {
				decal(add, m, F, diamond(s[0], s[1], 0.8, 0.8), 0.3, 2.0, st.core, st.core);
			}
			break;
		}
		case RIME: { // frost clusters at the shoulders, icicles hanging from the chest and the hem
			for (int side = -1; side <= 1; side += 2) {
				for (int k = 0; k < 3; k++) {
					double[] r = { side * sx * (0.8 + k * 0.12), sy, cz - 2 + k * 2.5 };
					blade(add, r, add(r, new double[] { side * (3 + k), -(8 - k * 1.5), 0 }), Z, 1.7, 1.0, k == 1 ? st.light : st.trim);
				}
			}
			for (int k = -3; k <= 3; k++) {
				double x = k * hw * 0.14;
				double[] p = project(m, F, x, top + h * 0.3);
				add.tube(add(p, new double[] { 0, 0, -0.8 }), add(p, new double[] { 0, 5 + (k % 2 == 0 ? 3 : 0), -0.8 }), 1.0, 0, 4, 0, st.trim, st.trim);
			}
			decal(add, m, F, rect(-hw * 0.6, top + h * 0.27, hw * 0.6, top + h * 0.31), 0.5, 1.6, st.trimDark, st.trimDark);
			double[] hem = LowArmour.hem(m, bottom);
			for (int k = 0; k < 12; k++) {
				double a = Math.PI * 2 * k / 12;
				double x = hem[0] + Math.cos(a) * hem[2] * 1.0, z = hem[1] + Math.sin(a) * hem[3] * 1.0;
				add.tube(new double[] { x, bottom - 2, z }, new double[] { x, bottom + 3 + (k % 3) * 1.5, z }, 1.1, 0, 4, 0, st.trim, st.trim);
			}
			break;
		}
		case NIGHT: { // a dark half-cloak down the back, crossed belts, a hooded lantern at the hip
			for (int k = 0; k < 9; k++) {
				double a = Math.PI * (0.15 + 0.7 * k / 8);
				double x = -Math.cos(a) * hw * 0.8, z = cz + Math.sin(a) * (back - cz) * 1.05;
				blade(add, new double[] { x, top + 2, z }, new double[] { x * 1.02, bottom + 4, z + 2 }, new double[] { -Math.sin(a), 0, Math.cos(a) }, 4.2, 0.8,
						k % 2 == 0 ? st.trim : st.trimDark);
			}
			for (int side = -1; side <= 1; side += 2) {
				decal(add, m, F, new double[][] { { side * hw * 0.48, top + 2 }, { side * hw * 0.75, top + 2 }, { -side * hw * 0.45, bottom - 2 },
						{ -side * hw * 0.72, bottom - 2 } }, 0.6, 1.5 + (side + 1) * 0.3, st.leatherDark, st.leatherDark);
			}
			for (int k = 0; k < 3; k++) {
				double[] p = project(m, F, -hw * 0.3 + k * hw * 0.12, top + h * 0.3 + k * h * 0.11);
				blade(add, add(p, new double[] { 0, -2, -1.5 }), add(p, new double[] { 0, 3, -1.5 }), X, 0.9, 0.4, hsl(40, 0, 96));
			}
			double lx = -hw - 1, ly = bottom - 6;
			add.tube(new double[] { lx, ly - 7, cz }, new double[] { lx, ly - 5, cz }, 0.6, 0.6, 4, 0, st.leatherDark, st.leatherDark);
			add.tube(new double[] { lx, ly - 5, cz }, new double[] { lx, ly + 2, cz }, 2.6, 2.6, 4, Math.PI / 4, st.accent, st.core);
			add.tube(new double[] { lx, ly - 6, cz }, new double[] { lx, ly - 4.5, cz }, 3.0, 2.8, 4, Math.PI / 4, st.trim, st.trim);
			add.tube(new double[] { lx, ly + 1.5, cz }, new double[] { lx, ly + 3, cz }, 3.0, 3.0, 4, Math.PI / 4, st.trim, st.trim);
			break;
		}
		case SEER: { // a gold collar, the scrying mirror on the breast
			decal(add, m, F, new double[][] { { -hw * 0.55, top + 1 }, { hw * 0.55, top + 1 }, { hw * 0.3, top + h * 0.17 }, { -hw * 0.3, top + h * 0.17 } },
					0.6, 1.6, st.trim, st.trimDark);
			double my = cy + 2;
			decal(add, m, F, polygon(0, my, hw * 0.25, hw * 0.3, 10), 0.5, 2.0, st.trim, st.trimDark);
			decal(add, m, F, polygon(0, my, hw * 0.19, hw * 0.23, 10), 0.4, 2.6, st.accent, st.trim);
			decal(add, m, F, diamond(-hw * 0.05, my - hw * 0.06, hw * 0.06, hw * 0.08), 0.3, 3.1, st.core, st.accent);
			for (int k = 0; k < 4; k++) {
				double a = Math.PI / 2 * k + Math.PI / 4;
				decal(add, m, F, diamond(Math.cos(a) * hw * 0.32, my + Math.sin(a) * hw * 0.37, 0.9, 0.9), 0.3, 2.2, st.trim, st.trim);
			}
			break;
		}
		case KNIGHT: // gold-rimmed pauldrons, the crimson tabard and the broken crown
			for (int side = -1; side <= 1; side += 2) {
				double[] c = { side * sx, sy, -1 };
				double[] axis = norm(new double[] { side * 0.6, -0.8, 0 });
				dome(add, c, axis, Z, hw * 0.55, hw * 0.43, hw * 0.36, 6, st.dark, st.main);
				ring(add, c, axis, Z, hw * 0.6, hw * 0.48, -1.4, 2.4, 6, st.trim);
			}
			for (Frame f : new Frame[] { F, B }) {
				decal(add, m, f, rect(-hw * 0.36, top + h * 0.18, hw * 0.36, bottom - 1), 0.6, 1.5, st.accent, hsl(0, 5, 24));
			}
			crown(add, m, F, 0, cy, h * 0.3, st.trim, st.trimDark, st.core);
			break;
		default:
			break;
		}
		return add;
	}

	/* ------------------------------------------------------------- the legs */

	/** Right knee, left knee and the front (any may be empty). */
	static ModelData[] legs(Style st, ModelData m) {
		if (st.motif.draft()) {
			return DraftArmour.legs(st, m);
		}
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
			case QUARRY: // leather knee-pads
				decal(one, m, F, new double[][] { { x - 5, kneeY - 6 }, { x + 5, kneeY - 6 }, { x + 5.5, kneeY + 6 }, { x - 5.5, kneeY + 6 } }, 0.8, 2.4,
						st.trim, st.trimDark);
				break;
			case SILK: // silk bound round the shins
				for (Frame f : new Frame[] { F, B }) {
					for (int k = 0; k < 2; k++) {
						double y = kneeY + 8 + k * 6;
						decal(one, m, f, rect(x - 5.5, y, x + 5.5, y + 2), 0.5, 1.5, st.trim, st.trimDark);
					}
				}
				break;
			case RIDGE: // fur wrapped at the knee
				for (Frame f : new Frame[] { F, B }) {
					decal(one, m, f, rect(x - 6, kneeY - 3, x + 6, kneeY + 3), 0.6, 1.9, st.trim, st.trimDark);
				}
				break;
			case RAVAGER: // black knee-plates with a bone claw
			case KNIGHT: {
				LowArmour.kneePlate(one, m, x, kneeY, st.trim, st.trimDark);
				double[] hit = surface(m, new double[] { x, kneeY, -100 }, new double[] { 0, 0, 1 });
				double[] p = hit == null ? new double[] { x, kneeY, -12 } : hit;
				int col = st.motif == Motif.RAVAGER ? st.light : st.trim;
				one.tube(add(p, new double[] { 0, 0, -1.5 }), add(p, new double[] { 0, st.motif == Motif.RAVAGER ? -3 : 0, -6.5 }), 2.6, 0, 4, 0.78, col, col);
				break;
			}
			case RIME: // ice knee-plates, an icicle under each
				LowArmour.kneePlate(one, m, x, kneeY, st.trim, st.trimDark);
				double[] p = project(m, F, x, kneeY + 7);
				one.tube(add(p, new double[] { 0, 0, -1.5 }), add(p, new double[] { 0, 6, -1.5 }), 1.2, 0, 4, 0, st.light, st.light);
				break;
			case NIGHT: // a knife on the right thigh
				if (side < 0) {
					decal(one, m, F, rect(x - 1.8, top + h * 0.2, x + 1.8, top + h * 0.42), 0.6, 2.2, st.leatherDark, st.leatherDark);
					double[] q = project(m, F, x, top + h * 0.2);
					one.tube(add(q, new double[] { 0, 0, -2 }), add(q, new double[] { 0, -4, -2 }), 0.8, 0.8, 4, 0, st.trimDark, st.trimDark);
					one.tube(add(q, new double[] { -1.8, 0, -2 }), add(q, new double[] { 1.8, 0, -2 }), 0.6, 0.6, 4, 0, st.accent, st.accent);
				}
				break;
			default:
				break;
			}
		}
		switch (st.motif) {
		case QUARRY: // a tool belt with a pouch
			for (Frame f : new Frame[] { F, B }) {
				decal(fr, m, f, rect(-hw * 1.02, top + 1, hw * 1.02, top + 5), 0.7, 1.6, st.trim, st.trimDark);
			}
			decal(fr, m, F, rect(hw * 0.25, top + 4, hw * 0.6, top + 12), 0.6, 2.4, st.leather, st.leatherDark);
			break;
		case STONE: // waystone runes down the front, a slate band at the hem
			for (int k = 0; k < 4; k++) {
				glyph(fr, m, F, 0, top + h * (0.18 + k * 0.18), 4.4, st);
			}
			break;
		case MOON: // a crescent and stars round the hem
			for (int k = -1; k <= 1; k++) {
				crescent(fr, m, F, k * hw * 0.5, bottom - h * 0.14, 3.4, st.trim, st.trimDark);
			}
			for (int k = 0; k < 3; k++) {
				decal(fr, m, F, diamond(-hw * 0.25 + k * hw * 0.25, top + h * (0.3 + (k % 2) * 0.16), 0.8, 0.8), 0.3, 1.9, st.core, st.core);
			}
			break;
		case SEER: { // two gold bands down the front, a crystal at the belt
			for (int side = -1; side <= 1; side += 2) {
				decal(fr, m, F, rect(side * hw * 0.22 - 1.1, top + 2, side * hw * 0.22 + 1.1, bottom - 3), 0.5, 1.6, st.trim, st.trimDark);
			}
			double[] p = project(m, F, 0, top + 4);
			fr.tube(add(p, new double[] { 0, 0, -1.5 }), add(p, new double[] { 0, 3.5, -2 }), 0.4, 2.0, 4, Math.PI / 4, st.accent, st.accent);
			fr.tube(add(p, new double[] { 0, 3.5, -2 }), add(p, new double[] { 0, 8.5, -2 }), 2.0, 0, 4, Math.PI / 4, st.core, st.accent);
			break;
		}
		case KNIGHT: // the crimson tasset, edged in gold
			decal(fr, m, F, rect(-hw * 0.3, top + 2, hw * 0.3, top + h * 0.3), 0.8, 1.7, st.accent, hsl(0, 5, 24));
			decal(fr, m, F, rect(-hw * 0.3, top + h * 0.3 - 1.4, hw * 0.3, top + h * 0.3), 0.6, 2.3, st.trim, st.trimDark);
			break;
		case RAVAGER: // bone strips over the hips
			for (int k = -2; k <= 2; k++) {
				double x = k * hw * 0.26;
				decal(fr, m, F, rect(x - hw * 0.1, top + 2, x + hw * 0.1, top + h * 0.2), 0.8, 1.8, st.light, st.dark);
			}
			break;
		default:
			break;
		}
		return new ModelData[] { knees[0], knees[1], fr };
	}

	/* ------------------------------------------------------------- the shield */

	static ModelData shield(Style st, ModelData m, Frame f) {
		if (st.motif.draft()) {
			return DraftArmour.shield(st, m, f);
		}
		double[] e = LowArmour.extent(m, f); // u0, u1, v0, v1
		double w = (e[1] - e[0]) / 2, hgt = (e[3] - e[2]) / 2, cu = (e[0] + e[1]) / 2, cv = (e[2] + e[3]) / 2;
		double s = Math.min(w, hgt);
		ModelData add = new ModelData();
		switch (st.motif) {
		case QUARRY: { // a pick and a hammer, crossed
			int haft = hsl(7, 3, 68), head = st.light;
			decal(add, m, f, LowArmour.bar(cu - s * 0.7, cv + s * 0.75, cu + s * 0.55, cv - s * 0.55, s * 0.1), 0.5, 2.0, haft, st.trimDark);
			decal(add, m, f, LowArmour.bar(cu + s * 0.7, cv + s * 0.75, cu - s * 0.55, cv - s * 0.55, s * 0.1), 0.5, 2.0, haft, st.trimDark);
			// the pick's head: a curved bar across the top of one haft; the hammer's: a block on the other
			decal(add, m, f, new double[][] { { cu + s * 0.2, cv - s * 0.85 }, { cu + s * 0.62, cv - s * 0.62 }, { cu + s * 0.85, cv - s * 0.2 },
					{ cu + s * 0.72, cv - s * 0.2 }, { cu + s * 0.52, cv - s * 0.52 }, { cu + s * 0.2, cv - s * 0.72 } }, 0.5, 2.6, head, st.dark);
			decal(add, m, f, new double[][] { { cu - s * 0.78, cv - s * 0.5 }, { cu - s * 0.5, cv - s * 0.78 }, { cu - s * 0.3, cv - s * 0.58 },
					{ cu - s * 0.58, cv - s * 0.3 } }, 0.5, 2.6, head, st.dark);
			break;
		}
		case RIME:
			flake(add, m, f, cu, cv, s * 1.6, st.trim, st.core);
			break;
		case RAVAGER: // a ravager's maw: a ring of teeth round a violet eye
			decal(add, m, f, polygon(0, 2, 12.5, 18, 12), 0.5, 1.8, st.trim, st.trimDark);
			for (int k = 0; k < 10; k++) {
				double a = Math.PI * 2 * k / 10;
				double ca = Math.cos(a), sa = Math.sin(a);
				decal(add, m, f, new double[][] { { ca * 11.5 - sa * 1.7, 2 + sa * 16.5 + ca * 1.7 }, { ca * 8, 2 + sa * 11 },
						{ ca * 11.5 + sa * 1.7, 2 + sa * 16.5 - ca * 1.7 } }, 0.4, 2.4, st.core, st.light);
			}
			decal(add, m, f, diamond(0, 2, 3.8, 5.6), 0.3, 2.8, st.accent, st.accent);
			decal(add, m, f, diamond(0, 2, 1.2, 3.4), 0.2, 3.3, ArmourSets.violet(6, 1), ArmourSets.violet(6, 1));
			break;
		case KNIGHT:
			decal(add, m, f, diamond(0, 2, 12, 21), 0.5, 1.6, st.accent, hsl(0, 5, 24));
			crown(add, m, f, 0, 0, 22, st.trim, st.trimDark, st.core);
			break;
		default:
			break;
		}
		return add;
	}

	/* ------------------------------------------------------------- boots, gloves */

	static ModelData boots(Style st, ModelData m) {
		if (st.motif.draft()) {
			return DraftArmour.boots(st, m);
		}
		int[] bb = m.bounds();
		double hw = bb[3], front = bb[2], top = bb[1];
		ModelData add = new ModelData();
		for (int side = -1; side <= 1; side += 2) {
			double x = side * hw * 0.56;
			switch (st.motif) {
			case QUARRY:
				LowArmour.toeCap(add, x, front, st.main);
				LowArmour.cuff(add, m, side, top, top + 3.5, 0.7, st.trim, st.trimDark);
				break;
			case SILK:
				LowArmour.cuff(add, m, side, top, top + 2, 0.5, st.trim, st.trimDark);
				LowArmour.cuff(add, m, side, top + 4, top + 5.5, 0.4, st.trimDark, st.trimDark);
				break;
			case RIDGE:
				LowArmour.cuff(add, m, side, top, top + 5, 1.4, st.trim, st.trimDark);
				break;
			case RAVAGER: // two bone claws over the toes
				for (int k = -1; k <= 1; k += 2) {
					add.tube(new double[] { x + k * 1.8, -3, front + 5 }, new double[] { x + k * 2.2, -1, front - 3 }, 1.4, 0, 4, 0.78, st.light, st.light);
				}
				LowArmour.cuff(add, m, side, top, top + 3, 0.6, st.trim, st.trimDark);
				break;
			case RIME:
				LowArmour.toeCap(add, x, front, st.trim);
				LowArmour.cuff(add, m, side, top, top + 3, 0.7, st.light, st.trimDark);
				break;
			case MOON:
			case STONE:
			case SEER:
				LowArmour.cuff(add, m, side, top, top + 2.5, 0.6, st.trim, st.trimDark);
				break;
			case NIGHT:
				LowArmour.cuff(add, m, side, top, top + 3, 0.6, st.trim, st.trimDark);
				break;
			case KNIGHT:
				LowArmour.toeCap(add, x, front, st.trim);
				break;
			default:
				break;
			}
		}
		return add;
	}

	static ModelData gloves(Style st, ModelData m) {
		if (st.motif.draft()) {
			return DraftArmour.gloves(st, m);
		}
		int[] bb = m.bounds();
		double hw = bb[3], top = bb[1];
		ModelData add = new ModelData();
		for (int side = -1; side <= 1; side += 2) {
			double x = side * (hw - 6.5);
			switch (st.motif) {
			case QUARRY:
				LowArmour.gauntlet(add, x, top, 7.0, st.trim, st.trimDark, st.leatherDark);
				break;
			case RAVAGER:
				LowArmour.gauntlet(add, x, top, 7.4, st.trim, st.trimDark, st.main);
				for (int k = -1; k <= 1; k++) {
					add.tube(new double[] { x + side * 2, top + 18, -2 + k * 2.5 }, new double[] { x + side * 2.5, top + 20, -8 + k * 2.5 }, 1.2, 0, 4, 0.78,
							st.light, st.light);
				}
				break;
			case RIME:
				LowArmour.gauntlet(add, x, top, 7.2, st.main, st.dark, st.trim);
				for (int k = -1; k <= 1; k++) {
					add.tube(new double[] { x + side * 3, top + 6, k * 3 }, new double[] { x + side * 7, top + 3, k * 3.5 }, 1.0, 0, 4, 0.78, st.trim, st.trim);
				}
				break;
			case KNIGHT:
				LowArmour.gauntlet(add, x, top, 7.6, st.main, st.dark, st.trim);
				break;
			case SILK:
				add.tube(new double[] { x, top + 1, 0.5 }, new double[] { x, top + 3, 0.5 }, 5.3, 5.3, 6, 0.26, st.trim, st.trim);
				add.tube(new double[] { x, top + 5, 0.5 }, new double[] { x, top + 6.5, 0.5 }, 5.3, 5.3, 6, 0.26, st.trimDark, st.trimDark);
				break;
			case RIDGE:
				add.tube(new double[] { x, top + 1, 0.5 }, new double[] { x, top + 5, 0.5 }, 6.0, 6.0, 6, 0.26, st.trim, st.trimDark);
				break;
			case NIGHT:
				add.tube(new double[] { x, top + 2, 0.5 }, new double[] { x, top + 4, 0.5 }, 5.3, 5.3, 6, 0.26, st.accent, st.accent);
				break;
			case STONE:
				add.tube(new double[] { x, top + 7, 1 }, new double[] { x, top + 9.5, 1 }, 6.2, 6.2, 6, 0.26, st.accent, st.trimDark);
				break;
			case MOON:
			case SEER:
				add.tube(new double[] { x, top + 7, 1 }, new double[] { x, top + 9.5, 1 }, 6.2, 6.2, 6, 0.26, st.trim, st.trimDark);
				break;
			default:
				break;
			}
		}
		return add;
	}

	/* ------------------------------------------------------------- emblems */

	/** A regular n-gon (radii rx, ry) as an outline. */
	static double[][] polygon(double x, double y, double rx, double ry, int n) {
		double[][] p = new double[n][];
		for (int k = 0; k < n; k++) {
			double a = Math.PI * 2 * k / n + Math.PI / n;
			p[k] = new double[] { x + Math.cos(a) * rx, y + Math.sin(a) * ry };
		}
		return p;
	}

	/** A crescent moon, horns to the right, as a band of quads that thins to points. */
	static void crescent(ModelData add, ModelData m, Frame f, double x, double y, double r, int col, int col2) {
		int n = 8;
		for (int i = 0; i < n; i++) {
			double a0 = Math.PI / 2 + Math.PI * i / n, a1 = Math.PI / 2 + Math.PI * (i + 1) / n;
			double w0 = r * 0.5 * Math.sin(Math.PI * i / n), w1 = r * 0.5 * Math.sin(Math.PI * (i + 1) / n);
			decal(add, m, f, new double[][] { { x + Math.cos(a0) * r, y - Math.sin(a0) * r }, { x + Math.cos(a1) * r, y - Math.sin(a1) * r },
					{ x + Math.cos(a1) * (r - w1), y - Math.sin(a1) * (r - w1) }, { x + Math.cos(a0) * (r - w0), y - Math.sin(a0) * (r - w0) } }, 0.4, 2.2,
					col, col2);
		}
	}

	/** A waystone rune: a stave crossed by a chevron, in the set's violet. */
	static void glyph(ModelData add, ModelData m, Frame f, double x, double y, double size, Style st) {
		double s = size / 2, w = Math.max(0.5, size * 0.09);
		decal(add, m, f, rect(x - w, y - s, x + w, y + s), 0.3, 2.6, st.accent, st.accent);
		decal(add, m, f, LowArmour.bar(x - s * 0.7, y - s * 0.1, x, y + s * 0.4, w), 0.3, 2.6, st.accent, st.accent);
		decal(add, m, f, LowArmour.bar(x, y + s * 0.4, x + s * 0.7, y - s * 0.1, w), 0.3, 2.6, st.accent, st.accent);
	}

	/** A spider's web: eight spokes and two rings of threads. */
	static void web(ModelData add, ModelData m, Frame f, double x, double y, double r, int col, int col2) {
		double w = Math.max(0.45, r * 0.035);
		double[][] spoke = new double[8][];
		for (int k = 0; k < 8; k++) {
			double a = Math.PI * 2 * k / 8 + Math.PI / 8;
			spoke[k] = new double[] { Math.cos(a), Math.sin(a) };
			decal(add, m, f, LowArmour.bar(x, y, x + spoke[k][0] * r, y + spoke[k][1] * r, w), 0.4, 1.8, col, col2);
		}
		for (double t : new double[] { 0.45, 0.85 }) {
			for (int k = 0; k < 8; k++) {
				double[] a = spoke[k], b = spoke[(k + 1) % 8];
				decal(add, m, f, LowArmour.bar(x + a[0] * r * t, y + a[1] * r * t, x + b[0] * r * t, y + b[1] * r * t, w), 0.4, 1.8, col, col2);
			}
		}
	}

	/** A six-pointed snowflake: three crossed staves, each with a pair of branches. */
	static void flake(ModelData add, ModelData m, Frame f, double x, double y, double size, int col, int core) {
		double s = size / 2, w = Math.max(0.6, size * 0.045);
		for (int k = 0; k < 3; k++) {
			double a = Math.PI * k / 3 + Math.PI / 2, ca = Math.cos(a), sa = Math.sin(a);
			decal(add, m, f, LowArmour.bar(x - ca * s, y - sa * s, x + ca * s, y + sa * s, w), 0.4, 2.0, col, col);
			for (int e = -1; e <= 1; e += 2) {
				double bx = x + e * ca * s * 0.6, by = y + e * sa * s * 0.6;
				for (int b = -1; b <= 1; b += 2) {
					double ba = a + b * Math.PI / 4 * e;
					decal(add, m, f, LowArmour.bar(bx, by, bx + e * Math.cos(ba) * s * 0.3, by + e * Math.sin(ba) * s * 0.3, w * 0.8), 0.4, 2.0, col, col);
				}
			}
		}
		decal(add, m, f, polygon(x, y, s * 0.16, s * 0.16, 6), 0.3, 2.6, core, col);
	}

	/** The old king's crown, broken: a band and five points, the second from the right snapped short. */
	static void crown(ModelData add, ModelData m, Frame f, double x, double y, double size, int col, int col2, int gem) {
		double s = size / 2, bw = s * 0.9, by0 = y + s * 0.1, by1 = y + s * 0.42;
		decal(add, m, f, rect(x - bw, by0, x + bw, by1), 0.5, 2.0, col, col2);
		for (int k = 0; k < 5; k++) {
			double px = x - bw + bw * 2 * k / 4.0, tall = k == 3 ? s * 0.32 : k % 2 == 0 ? s * 0.9 : s * 0.62;
			double half = bw * 0.2;
			decal(add, m, f, new double[][] { { px - half, by0 }, { px, by0 - tall }, { px + half, by0 } }, 0.5, 2.0, col, col2);
		}
		decal(add, m, f, diamond(x, (by0 + by1) / 2, s * 0.13, s * 0.13), 0.3, 2.7, gem, col);
	}
}
