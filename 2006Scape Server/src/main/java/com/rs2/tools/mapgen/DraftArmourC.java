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
 * Draft armour sets A35-A39 and their weapons: built and previewed only (they are not in ArmourSets.SETS, have no item
 * ids, and nothing packs them into the cache). See DraftArmour.
 *
 * <ol start="35">
 * <li>Herald's (Defence 65): The Hollow Herald's coat of plates (Kind.COAT): dark iron with ghostly pale trim, a violet tabard over a knee-length lamellar skirt, banners hanging from the belt, a herald's horn on the right pauldron, an iron crown of spikes round a kettle helm. (Herald's is a possessive: pieces read 'Herald's kettle helm'.)</li>
 * <li>Emberforged (Defence 70): Dorrin's forge masterwork on the coat base (Kind.COAT): blackened steel with glowing ember seams and rivets, bronze edging, an anvil-and-flame crest on the breast, a chimney on the kettle helm venting embers.</li>
 * <li>Starless shroud (Magic 70): Robes cut from the Starless's own shadow: near-black with scattered star points, a starry hood/hat with a crescent of stars, violet void glow at the core, an open eye on the breast.</li>
 * <li>Riftwalker (Ranged 75): Rangers who walk through the rifts: dark grey-violet hide split by glowing violet rift-cracks, riftglass shards, a hood with a glowing seam over the eyes, black straps.</li>
 * <li>Dawnguard (Defence 80): The guard sworn to the first dawn after the Starless: pale gold-white plate, rose-dawn trim, a rising-sun crest with rays on the helm and breast, wings at the helm's sides. Must read clearly below Heartfire (90) but above Voidsteel (75).</li>
 * </ol>
 */
final class DraftArmourC {

	private DraftArmourC() {
	}

	/** Draft pieces have no item ids: 0 marks a piece the set has, -1 one it doesn't. */
	static final int[] HAS_ALL = { 0, 0, 0, 0, 0, 0 }, NO_SHIELD = { 0, 0, 0, -1, 0, 0 };

	/** Colours the sets share with their weapons. */
	static final int GHOST = hsl(36, 1, 92), GHOST_DARK = hsl(36, 1, 68), GHOST_GLOW = hsl(23, 3, 114), BANNER = ArmourSets.violet(56, 5),
			BANNER_DARK = ArmourSets.violet(34, 5), EMBER = hsl(4, 6, 74), EMBER_HOT = hsl(7, 5, 104), BRONZE_EDGE = hsl(6, 4, 56),
			STAR = hsl(42, 0, 124), SILVER = hsl(42, 0, 98), VOID_GLOW = ArmourSets.violet(100, 6), RIFT = ArmourSets.violet(92, 6),
			RIFT_CORE = ArmourSets.violet(122, 2), RIFTGLASS = ArmourSets.violet(52, 5), SUN = hsl(8, 5, 90), SUN_CORE = hsl(9, 2, 122),
			ROSE = hsl(1, 3, 76), ROSE_DARK = hsl(1, 3, 58), FEATHER = hsl(0, 0, 118), FEATHER_DARK = hsl(0, 0, 96);

	static final Style[] SETS = {
			new Style("Herald's", "herald", Kind.COAT, Motif.HERALD, 65, hsl(0, 0, 30), hsl(0, 0, 18), hsl(0, 0, 46), GHOST, BANNER_DARK, BANNER,
					GHOST_GLOW, hsl(48, 2, 20), hsl(48, 2, 13), HAS_ALL,
					new String[] { "Herald's kettle helm", "Herald's war tunic", "Herald's legguards", "Herald's roundshield", "Herald's riding boots",
							"Herald's gauntlets" },
					new String[] { "A kettle helm under an iron crown of spikes. Whoever wears it is announcing a king, whether they like it or not.",
							"A coat of iron plates under a violet tabard, the Hollow King's crown on the breast and a horn on the shoulder to herald him. Nobody has told it to stop.",
							"Iron-plated legguards with the herald's violet down each side. Heralds walk ahead of their king; these are still walking.",
							"A violet roundshield with the Hollow King's crown over the boss. The sockets are empty, bar one, and that one is looking at you.",
							"Tall riding boots with pale spurs. They've ridden ahead of a king who never arrived.",
							"Long-cuffed gauntlets banded in violet, for holding up proclamations nobody reads any more." }),
			new Style("Emberforged", "emberforged", Kind.COAT, Motif.EMBERFORGED, 70, hsl(4, 1, 26), hsl(4, 1, 16), hsl(4, 1, 40), BRONZE_EDGE,
					hsl(6, 4, 36), EMBER, EMBER_HOT, hsl(5, 3, 22), hsl(5, 3, 14), HAS_ALL,
					new String[] { "Emberforged kettle helm", "Emberforged war tunic", "Emberforged legguards", "Emberforged roundshield", "Emberforged riding boots",
							"Emberforged gauntlets" },
					new String[] { "Dorrin put a chimney on it. He says it's for venting the heat. It's mostly for showing off.",
							"A blackened coat of plates with the forge still glowing in the joins. Dorrin's anvil is on the breast; he signs his best work.",
							"Plated legguards that glow where the lames meet, like a forge with its door left open.",
							"A roundshield with Dorrin's anvil and flame. The only shield in Nythera that's warm to the touch on a winter morning.",
							"Tall riding boots with a bronze spur and an ember in the toe. Puddles steam when you walk through them.",
							"Gauntlets with long bronze-edged cuffs that never quite cool. Dorrin says he forged them without tongs." }),
			new Style("Starless shroud", "shroud", Kind.ROBE, Motif.SHROUD, 70, hsl(44, 2, 14), hsl(44, 2, 8), hsl(44, 2, 24), hsl(44, 3, 30),
					hsl(44, 3, 20), STAR, VOID_GLOW, hsl(44, 1, 12), hsl(44, 1, 8), NO_SHIELD,
					new String[] { "Starless shroud hat", "Starless shroud robe top", "Starless shroud robe bottom", null, "Starless shroud boots",
							"Starless shroud gloves" },
					new String[] { "Cut from the Starless's own shadow, stars and all. They don't line up with any sky you know.",
							"The eye on the breast is the Starless's. It opened the day the robe was cut, and it hasn't blinked since.",
							"Shadow that frays at the hem a little more each night. The violet is what's underneath.", null,
							"Silver-cuffed boots that cast no shadow. They're wearing one already.",
							"Black gloves cuffed in violet. Your hands look a very long way off in them." }),
			new Style("Riftwalker", "riftwalker", Kind.HIDE, Motif.RIFTWALKER, 75, hsl(48, 1, 34), hsl(48, 1, 22), hsl(48, 1, 48), hsl(0, 0, 16),
					hsl(0, 0, 10), RIFT, RIFT_CORE, hsl(48, 2, 26), hsl(48, 2, 16), NO_SHIELD,
					new String[] { "Riftwalker coif", "Riftwalker body", "Riftwalker chaps", null, "Riftwalker boots", "Riftwalker vambraces" },
					new String[] { "The rift runs right over the brow. Riftwalkers say it lets them see the other side, and won't say of what.",
							"Hide split by a rift that never closed, and a quiver of riftglass arrows. Mind the edges.",
							"A riftglass shard grew out of the knee. Riftwalkers take that as a compliment.", null,
							"Dark boots with a shard at the ankle. They've stepped somewhere else and back, more than once.",
							"Black-strapped bracers with rift-light at the wrist and a shard for a fin." }),
			new Style("Dawnguard", "dawnguard", Kind.PLATE, Motif.DAWNGUARD, 80, hsl(2, 1, 104), hsl(2, 1, 84), hsl(2, 0, 118), ROSE, ROSE_DARK, SUN,
					SUN_CORE, hsl(1, 3, 40), hsl(1, 3, 28), HAS_ALL,
					new String[] { "Dawnguard full helm", "Dawnguard platebody", "Dawnguard platelegs", "Dawnguard kiteshield", "Dawnguard boots",
							"Dawnguard gauntlets" },
					new String[] { "Winged, and crowned with the first dawn after the Starless. The Dawnguard swore to see the next one too.",
							"The first sunrise after the Starless, beaten into the breastplate. The wings on the back are for getting there first.",
							"Rose-gold tassets. The Dawnguard stand their watch in the hour when the sky goes pink.",
							"The sun, half-risen over the horizon. The Dawnguard never paint it any higher; it isn't over yet.",
							"Little wings at the heels. They're mostly for show. Mostly.", "Pale gauntlets with a gold ray at the cuff, for saluting the morning." }), };

	/** Each set's weapon, in set order (id 0: not in the game). */
	static final Rung[] WEAPONS = {
			new Rung(0, "Herald's mace", "mace", 50000, "The Hollow Herald's mace. The pennant still bears the crown of a king who isn't there to wear it.",
					DraftArmourC::heraldsMace, MACE),
			new Rung(0, "Emberforged warhammer", "warhammer", 80000, "Dorrin's own hammer, or its twin. The face is still white-hot from the last thing it struck.",
					DraftArmourC::emberforgedHammer, WARHAMMER),
			new Rung(0, "Shroud staff", "staff", 90000, "The Starless's eye on a staff of its shadow. Whatever it's looking at, you'd rather it wasn't you.",
					DraftArmourC::shroudStaff, STAFF),
			new Rung(0, "Riftwalker longbow", "longbow", 120000, "Riftglass grew along the limbs on its own. Its arrows land a moment before you loose them.",
					() -> riftwalkerBow(1.0), () -> riftwalkerBow(1.3), LONGBOW), // inventory bend 1.3, as the ladder's longbows
			new Rung(0, "Dawnguard 2h sword", "2h sword", 200000,
					"A sunrise for a guard and a wing either side. The Dawnguard draw it at first light every day, just in case.", DraftArmourC::dawnguardSword,
					TWO_HANDED), };

	/** Whether this file builds the set. */
	static boolean owns(Style st) {
		return st.motif == Motif.HERALD || st.motif == Motif.EMBERFORGED || st.motif == Motif.SHROUD || st.motif == Motif.RIFTWALKER || st.motif == Motif.DAWNGUARD;
	}

	static int shieldFace(Style st) {
		return st.motif == Motif.HERALD ? st.accent : st.main; // the herald's roundshield is violet
	}

	static int shieldRim(Style st) {
		return st.dark;
	}

	/* ------------------------------------------------------------- the helm */

	/** What's added to the helm (the base mesh, recoloured, is already there). */
	static ModelData helm(Style st, ModelData m) {
		int[] bb = m.bounds();
		double hw = bb[3], top = bb[1], bottom = bb[4], h = bottom - top, front = bb[2], back = bb[5], cz = (front + back) / 2, dz = (back - front) / 2;
		ModelData add = new ModelData();
		Frame F = ArmourSets.FRONT;
		switch (st.motif) {
		case HERALD: // an iron crown of thin spikes round the kettle helm's cap, ghost-light at its brow, violet ribbons from the brim
		case EMBERFORGED: // a forge chimney on the crown venting flame, a glowing seam round the cap, ember rivets on the hat band
			coatHelm(add, m, st);
			break;
		case SHROUD: { // a band of void-light round the crown, a crescent of stars arched over the brow, shadow trailing from the brim
			double cx = (bb[0] + bb[3]) / 2.0, by = top + h * 0.62;
			ring(add, new double[] { cx, by, cz }, new double[] { 0, -1, 0 }, X, hw * 0.98, dz * 0.98, -0.7, 0.7, 10, st.core);
			double[][] stars = { { -5.2, 0.5, 2.1 }, { 0, -3.0, 3.0 }, { 5.2, 0.5, 2.1 }, { 1.5, -h * 0.25, 1.5 }, { -1.5, -h * 0.36, 1.2 } };
			for (double[] s : stars) {
				decal(add, m, F, starPts(cx + s[0], top + h * 0.47 + s[1], s[2], s[2] * 0.4, 4), 0.3, 2.0, st.accent, SILVER);
			}
			for (int k = -2; k <= 2; k++) {
				double x = k * hw * 0.32;
				blade(add, new double[] { x, bottom - 3, back - 2 }, new double[] { x * 1.12, bottom + 9 - Math.abs(k) * 2, back + 3 }, X, 2.6, 0.5,
						k % 2 == 0 ? st.dark : st.main);
			}
			break;
		}
		case RIFTWALKER: { // a rift splitting the hood over the eyes and back over the crown, riftglass shards growing from it
			double y0 = top + h * 0.09; // on the hood's edge, just over the eyes
			crack(add, m, F, new double[][] { { -hw * 0.6, y0 + 0.6 }, { -hw * 0.25, y0 - 0.8 }, { hw * 0.05, y0 + 0.8 }, { hw * 0.35, y0 - 0.6 },
					{ hw * 0.6, y0 + 0.6 } }, 0.8, st.accent, st.trim);
			Frame T = new Frame(new double[] { 0, top, 0 }, X, Z, new double[] { 0, -1, 0 });
			crack(add, m, T, new double[][] { { 0.5, front + 6 }, { -1.5, cz - 2 }, { 1.2, cz + 3 }, { -0.5, back - 4 } }, 0.8, st.accent, st.trim);
			for (int k = 0; k < 3; k++) {
				double z = cz - 1 + k * 3.5, x = (k - 1) * 2.5;
				double[] p = surface(m, new double[] { x, top - 50, z }, new double[] { 0, 1, 0 });
				double[] b = p == null ? new double[] { x, top + 1, z } : pt(p, 0, 1, 0);
				shard(add, b, new double[] { x * 0.4, -1, 0.5 + k * 0.3 }, 6 + (k == 1 ? 4 : 0), 1.6, k == 1 ? st.core : st.accent, RIFTGLASS);
			}
			break;
		}
		case DAWNGUARD: { // a rising sun standing on the crown, rays fanned behind it; white wings at the temples; a sun on the brow
			double sy = top + 3.5, sz = cz + 1;
			Frame S = new Frame(new double[] { 0, sy, sz }, X, Y, new double[] { 0, 0, -1 });
			slab(add, S, semicircle(0, 0, 6.4, 7), 1.0, st.accent, st.trim, st.trimDark);
			slab(add, S, semicircle(0, -0.2, 3.4, 5), 1.6, st.core, st.accent, st.accent);
			for (int k = 0; k < 5; k++) {
				double a = Math.PI + Math.PI * (k + 0.5) / 5, ca = Math.cos(a), sa = Math.sin(a);
				double len = k % 2 == 0 ? 16 : 12;
				blade(add, new double[] { ca * 5.6, sy + sa * 5.6, sz }, new double[] { ca * len, sy + sa * len, sz }, new double[] { -sa, ca, 0 }, 2.0, 0.7,
						k % 2 == 0 ? st.accent : st.trim);
			}
			for (int side = -1; side <= 1; side += 2) { // a white wing at each temple, swept back
				double[] u = norm(new double[] { side * 0.3, 0, 1 });
				wing(add, new double[] { side * hw * 0.97, top + h * 0.45, cz - 2 }, u, Y, new double[] { side, 0, 0 }, 1.55, 0.7, st);
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
		case HERALD: // the violet tabard over the coat, the hollow crown on it, banners hanging from the belt, the horn on the right pauldron
		case EMBERFORGED: // Dorrin's anvil on the breast, the forge glowing in the belly lames and the pauldrons, ember rivets down the skirt
			coatBody(add, m, st);
			break;
		case SHROUD: { // the open eye in a ring of void-light, stars scattered front and back, a hem frayed into shadow
			double[] hm = LowArmour.hem(m, bottom);
			double w = hm[2];
			eye(add, m, F, 0, cy - 1, Math.max(6.5, w * 0.42));
			double[][] fs = { { -0.62, -0.32 }, { 0.58, -0.28 }, { -0.4, 0.42 }, { 0.5, 0.36 }, { 0.1, 0.72 }, { -0.7, 0.85 } };
			for (int k = 0; k < fs.length; k++) {
				double sz = k % 3 == 0 ? 1.9 : 1.3;
				decal(add, m, F, starPts(fs[k][0] * w, cy + fs[k][1] * h * 0.6, sz, sz * 0.42, 4), 0.3, 2.0, st.accent, SILVER);
			}
			double[][] bs = { { -0.5, -0.4 }, { 0.3, 0.05 }, { 0.6, 0.4 }, { -0.35, 0.55 }, { 0.2, 0.75 } };
			for (int k = 0; k < bs.length; k++) {
				double sz = k == 1 ? 2.6 : 1.4;
				decal(add, m, B, starPts(bs[k][0] * w, cy + bs[k][1] * h * 0.6, sz, sz * 0.42, 4), 0.3, 2.0, st.accent, SILVER);
			}
			for (int k = 0; k < 8; k++) {
				double a = Math.PI * 2 * k / 8 + 0.3;
				double x = hm[0] + Math.cos(a) * hm[2] * 0.95, z = hm[1] + Math.sin(a) * hm[3] * 0.95;
				blade(add, new double[] { x, bottom - 4, z }, new double[] { x * 1.06, bottom + 6 + (k % 3) * 2, z + Math.sin(a) * 1.5 },
						new double[] { -Math.sin(a), 0, Math.cos(a) }, 2.8, 0.6, k % 2 == 0 ? st.dark : st.main);
			}
			break;
		}
		case RIFTWALKER: { // a rift torn across the chest and the back, a black strap and a quiver of riftglass arrows, shards at the shoulders
			crack(add, m, F, new double[][] { { -hw * 0.6, top + 4 }, { -hw * 0.25, top + h * 0.3 }, { -hw * 0.38, top + h * 0.45 }, { hw * 0.1, top + h * 0.62 },
					{ hw * 0.02, top + h * 0.75 }, { hw * 0.5, bottom - 3 } }, 1.0, st.accent, st.trim);
			crack(add, m, F, new double[][] { { -hw * 0.38, top + h * 0.45 }, { -hw * 0.62, top + h * 0.62 }, { -hw * 0.55, top + h * 0.78 } }, 0.7, st.accent,
					st.trim);
			crack(add, m, B, new double[][] { { hw * 0.5, top + 5 }, { hw * 0.15, top + h * 0.35 }, { hw * 0.3, top + h * 0.55 }, { -hw * 0.2, bottom - 3 } },
					0.9, st.accent, st.trim);
			decal(add, m, F, new double[][] { { hw * 0.45, top + 1 }, { hw * 0.78, top + 1 }, { -hw * 0.6, bottom - 1 }, { -hw * 0.93, bottom - 1 } }, 0.6, 1.4,
					st.trim, st.trimDark);
			decal(add, m, F, diamond(hw * 0.12, top + h * 0.38, 1.6, 1.6), 0.4, 2.2, st.core, st.accent);
			double[] q0 = { hw * 0.55, top - 3, back + 2.5 }, q1 = { -hw * 0.3, bottom - 6, back + 2.5 };
			add.tube(q0, q1, 3.0, 2.6, 6, 0, st.leather, st.leatherDark);
			add.tube(pt(q0, 0, 0.5, 0), pt(q0, 0.3, 2.5, 0), 3.4, 3.4, 6, 0, st.trim, st.trim);
			for (int k = -1; k <= 1; k++) {
				double[] a0 = pt(q0, k * 1.4, 0, k * 0.8), a1 = pt(q0, 4 + k * 1.2, -7 - (k == 0 ? 2 : 0), k * 1.2);
				add.tube(a0, a1, 0.6, 0.6, 3, 0, st.leatherDark, st.leatherDark);
				add.tube(a1, pt(a1, 1.8, -3.5, 0), 1.2, 0, 4, 0, k == 0 ? st.core : st.accent, st.accent);
			}
			for (int side = -1; side <= 1; side += 2) {
				for (int k = 0; k < 2; k++) {
					double[] b = { side * hw * (0.62 + k * 0.2), top + 1.5, cz + (k == 0 ? -1.5 : 2) };
					shard(add, b, new double[] { side * 0.45, -1, k * 0.3 }, 6 - k * 1.5, 1.5, k == 0 ? st.accent : RIFTGLASS, RIFTGLASS);
				}
			}
			break;
		}
		case DAWNGUARD: { // rose-gold pauldrons, the sun rising on the breast, white wings folded down the back
			for (int side = -1; side <= 1; side += 2) {
				double[] c = { side * sx, sy, -1 };
				double[] axis = norm(new double[] { side * 0.6, -0.8, 0 });
				dome(add, c, axis, Z, hw * 0.55, hw * 0.43, hw * 0.36, 6, st.dark, st.main);
				ring(add, c, axis, Z, hw * 0.6, hw * 0.48, -1.4, 2.4, 6, st.trim);
				// a white wing on each shoulder blade, raised up and out and angled away from the back
				double[] root = pt(project(m, B, side * hw * 0.1, top + h * 0.34), 0, 0, 1.5);
				wing(add, root, norm(new double[] { side, 0, 0.5 }), Y, Z, 1.8, 0.8, st);
			}
			risingSun(add, m, F, 0, cy + 3, h * 0.22, st);
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
			case HERALD:
			case EMBERFORGED:
				coatKnee(one, m, side, st);
				break;
			case DAWNGUARD:
				LowArmour.kneePlate(one, m, x, kneeY, st.trim, st.trimDark);
				decal(one, m, F, starPts(x, kneeY, 2.6, 1.0, 4), 0.3, 2.9, st.accent, st.trim);
				break;
			case RIFTWALKER: {
				double[] hit = surface(m, new double[] { x, kneeY, -100 }, new double[] { 0, 0, 1 });
				double[] p = hit == null ? new double[] { x, kneeY, -12 } : hit;
				shard(one, pt(p, 0, 1, 1), new double[] { side * 0.3, -0.6, -1 }, 6, 1.6, st.accent, RIFTGLASS);
				break;
			}
			default:
				break;
			}
		}
		switch (st.motif) {
		case HERALD: // the herald's livery: a violet stripe down the outside of each leg
		case EMBERFORGED: // the forge-light in the joins of the cuisses
			coatLegs(fr, m, st);
			break;
		case SHROUD: { // the void showing through a seam down the front, stars either side of it, the hem frayed into shadow
			decal(fr, m, F, rect(-1.2, top + 6, 1.2, bottom - h * 0.12), 0.4, 1.4, st.core, st.trimDark);
			double[][] fs = { { -0.3, 0.2 }, { 0.25, 0.32 }, { -0.16, 0.48 }, { 0.32, 0.62 }, { -0.35, 0.74 } };
			for (int k = 0; k < fs.length; k++) {
				double sz = k == 2 ? 2.4 : 1.4;
				decal(fr, m, F, starPts(fs[k][0] * hw, top + fs[k][1] * h, sz, sz * 0.4, 4), 0.3, 2.0, st.accent, SILVER);
			}
			double[] hm = LowArmour.hem(m, bottom);
			for (int k = 0; k < 12; k++) {
				double a = Math.PI * 2 * k / 12 + 0.2;
				double x = hm[0] + Math.cos(a) * hm[2] * 0.92, z = hm[1] + Math.sin(a) * hm[3] * 0.92;
				blade(fr, new double[] { x, bottom - 4, z }, new double[] { x * 1.05, bottom + 3 + (k % 3) * 1.5, z }, new double[] { -Math.sin(a), 0, Math.cos(a) },
						2.8, 0.6, k % 2 == 0 ? st.dark : st.main);
			}
			break;
		}
		case RIFTWALKER: // the rift down the right thigh
			crack(fr, m, F, new double[][] { { -kneeX * 0.7, top + h * 0.1 }, { -kneeX * 1.25, top + h * 0.25 }, { -kneeX * 0.9, top + h * 0.38 },
					{ -kneeX * 1.2, top + h * 0.47 } }, 0.8, st.accent, st.trim);
			break;
		case DAWNGUARD: { // rose tassets over the hips, a sun at the belt
			for (int side = -1; side <= 1; side += 2) {
				double x = side * hw * 0.45;
				decal(fr, m, F, new double[][] { { x - 5.5, top + 3 }, { x + 5.5, top + 3 }, { x + 5, top + h * 0.26 }, { x, top + h * 0.31 }, { x - 5, top + h * 0.26 } },
						1.0, 2.0, st.trim, st.trimDark);
			}
			decal(fr, m, F, semicircle(0, top + 6, 3.4, 5), 0.5, 2.4, st.accent, st.trim);
			decal(fr, m, F, rect(-4.6, top + 6, 4.6, top + 7.4), 0.5, 2.6, st.trim, st.trimDark);
			break;
		}
		default:
			break;
		}
		return new ModelData[] { knees[0], knees[1], fr };
	}

	/* ------------------------------------------------------------- the shield */

	static ModelData shield(Style st, ModelData m, Frame k) {
		// the kite frame handed in runs corner to corner; upright instead (y straight down the face, x across), so the emblems stand level with its cross
		double[] down = norm(ArmourForge.sub(Y, scale(k.n, k.n[1])));
		Frame f = new Frame(k.o, norm(ModelData.cross(k.n, down)), down, k.n);
		ModelData add = new ModelData();
		switch (st.motif) {
		case HERALD: // the hollow crown over the boss of a violet roundshield
		case EMBERFORGED: // the anvil under the boss, the flame rising off it, ember rivets round the field
			coatShield(add, m, st);
			break;
		case DAWNGUARD: // the first dawn: the sun rising over the horizon
			risingSun(add, m, f, 2, 6, 15, st);
			break;
		default:
			break;
		}
		return add;
	}

	/* ------------------------------------------------------------- boots, gloves */

	static ModelData boots(Style st, ModelData m) {
		int[] bb = m.bounds();
		double hw = bb[3], front = bb[2], top = bb[1], back = bb[5];
		ModelData add = new ModelData();
		for (int side = -1; side <= 1; side += 2) {
			double x = side * hw * 0.56;
			switch (st.motif) {
			case HERALD:
			case EMBERFORGED:
				coatBoot(add, m, side, st);
				break;
			case SHROUD:
				LowArmour.cuff(add, m, side, top, top + 2.5, 0.6, SILVER, st.trimDark);
				LowArmour.cuff(add, m, side, top + 4, top + 5, 0.4, st.core, st.core);
				break;
			case RIFTWALKER:
				LowArmour.cuff(add, m, side, top, top + 3.5, 0.7, st.trim, st.trimDark);
				shard(add, new double[] { side * (hw - 1), top + 6, 2 }, new double[] { side * 0.5, -0.8, 0.5 }, 5, 1.3, st.accent, RIFTGLASS);
				break;
			case DAWNGUARD:
				LowArmour.toeCap(add, x, front, st.trim);
				for (int k = 0; k < 2; k++) {
					double[] r = { side * (hw - 2), top + 5 + k * 2.5, back - 4 };
					blade(add, r, pt(r, side * (3 + k), -7 + k * 3, 7 - k), new double[] { side * 0.4, 0, 1 }, 2.0 - k * 0.3, 0.6, k == 0 ? FEATHER : FEATHER_DARK);
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
			case HERALD:
			case EMBERFORGED:
				coatGauntlet(add, m, side, st);
				break;
			case SHROUD:
				add.tube(new double[] { x, top + 7, 1 }, new double[] { x, top + 9.5, 1 }, 6.2, 6.2, 6, 0.26, SILVER, st.trimDark);
				add.tube(new double[] { x, top + 5, 1 }, new double[] { x, top + 6.5, 1 }, 6.0, 6.0, 6, 0.26, st.core, st.core);
				break;
			case RIFTWALKER:
				add.tube(new double[] { x, top + 1, 0.5 }, new double[] { x, top + 6, 0.5 }, 5.6, 5.8, 6, 0.26, st.trim, st.trimDark);
				add.tube(new double[] { x, top + 3, 0.5 }, new double[] { x, top + 4, 0.5 }, 6.0, 6.0, 6, 0.26, st.accent, st.accent);
				shard(add, new double[] { x + side * 5, top + 3, 0.5 }, new double[] { side, -0.6, 0 }, 5, 1.2, st.accent, RIFTGLASS);
				break;
			case DAWNGUARD:
				LowArmour.gauntlet(add, x, top, 7.6, st.main, st.dark, st.trim);
				blade(add, new double[] { x + side * 6, top + 4, 1 }, new double[] { x + side * 11, top - 3, 1 }, Z, 1.8, 0.7, st.accent);
				break;
			default:
				break;
			}
		}
		return add;
	}

	/* ------------------------------------------------------------- on the coat (Kind.COAT: BaseCoat) */

	/*
	 * Herald's and Emberforged are worn on the coat base, so their decorations are placed on its landmarks (BaseCoat.mark):
	 * the kettle helm's brim and cap, the coat's chest, belt, skirt bands and pauldrons, the knee cops, the boot heels,
	 * the gauntlet cuffs and the roundshield's face.
	 */

	/** The kettle helm: the herald's spiked iron crown and ribbons, or Dorrin's chimney and glowing seam. */
	static void coatHelm(ModelData add, ModelData m, Style st) {
		double[] b = BaseCoat.mark(m, "brim"); // y, cz, rx, rz, apexY, neckY
		double by = b[0], cz = b[1], rx = b[2], rz = b[3];
		double[] up = { 0, -1, 0 };
		if (st.motif == Motif.HERALD) {
			double cy = by - 4.6, crx = rx * 0.95 + 1.0, crz = rz * 0.95 + 1.0;
			ring(add, new double[] { 0, cy, cz }, up, X, crx, crz, -1.0, 1.0, 10, st.trim);
			for (int k = 0; k < 7; k++) {
				double a = -Math.PI / 2 + Math.PI * 2 * k / 7;
				double x = Math.cos(a) * crx, z = cz + Math.sin(a) * crz;
				double tall = k == 0 ? 11 : k % 2 == 0 ? 6 : 8;
				blade(add, new double[] { x, cy - 0.5, z }, new double[] { x * 1.12, cy - tall, cz + (z - cz) * 1.12 }, new double[] { -Math.sin(a), 0, Math.cos(a) },
						1.8, 0.9, k == 0 ? st.core : st.trim);
			}
			for (int side = -1; side <= 1; side += 2) { // ribbons streaming from under the back of the brim
				double[] r0 = { side * 2.4, by + 3.0, cz + rz + 5.5 }, r1 = { side * 5.5, b[5] + 2, cz + rz + 8.5 };
				blade(add, r0, r1, X, 2.4, 0.5, side < 0 ? st.accent : st.trimDark);
			}
		} else {
			double[] base = { 0, b[4] + 3.5, cz + 0.5 }, mouth = { 0, b[4] - 6, cz + 1 };
			add.tube(base, mouth, 3.0, 2.6, 4, Math.PI / 4, st.dark, st.dark);
			add.tube(pt(mouth, 0, 1.5, 0), pt(mouth, 0, -1.2, 0), 3.8, 4.0, 4, Math.PI / 4, st.trim, st.core);
			for (int k = -1; k <= 1; k++) {
				blade(add, pt(mouth, k * 1.4, 0, 0), pt(mouth, k * 2.6, -(k == 0 ? 9 : 6), k * 0.8), Z, 1.5, 0.8, k == 0 ? st.core : st.accent);
			}
			ring(add, new double[] { 0, by - 6.5, cz }, up, X, rx * 0.88 + 0.6, rz * 0.88 + 0.6, -0.6, 0.6, 10, st.accent);
			for (int k = -2; k <= 2; k++) { // rivets glowing on the hat band
				double a = k * 0.5;
				double[] p = { Math.sin(a) * (rx + 0.6), by - 1.6, cz - Math.cos(a) * (rz + 0.6) };
				double[] o = norm(new double[] { Math.sin(a) / rx, 0, -Math.cos(a) / rz });
				add.tube(p, ArmourForge.add(p, scale(o, 1.5)), 0.9, 0, 4, 0.4, st.core, st.core);
			}
		}
	}

	/** The coat: the herald's tabard, crown, banners and horn; or Dorrin's anvil, the forge-glow and ember rivets. */
	static void coatBody(ModelData add, ModelData m, Style st) {
		double[] torso = BaseCoat.mark(m, "torso"), chest = BaseCoat.mark(m, "chest"), belly = BaseCoat.mark(m, "belly"), belt = BaseCoat.mark(m, "belt");
		double neckY = torso[0], waistY = torso[1];
		Frame F = ArmourSets.FRONT, B = ArmourSets.BACK;
		if (st.motif == Motif.HERALD) {
			double pw = chest[2] * 0.42;
			for (Frame f : new Frame[] { F, B }) { // the tabard, front and back, pale-hemmed
				decal(add, m, f, rect(-pw, neckY + (f == F ? 5 : 8), pw, belt[0] - 1.5), 0.6, 1.5, st.accent, st.trimDark);
				decal(add, m, f, rect(-pw - 0.4, belt[0] - 3.2, pw + 0.4, belt[0] - 1.0), 0.6, 2.0, st.trim, GHOST_DARK);
			}
			hollowCrown(add, m, F, 0, chest[0] + 3, (waistY - neckY) * 0.36, st);
			for (int face = -1; face <= 1; face += 2) { // banners hanging from the belt over the skirt, either side of the slit
				for (int side = -1; side <= 1; side += 2) {
					banner(add, m, side, face, st);
				}
			}
			double[] p = BaseCoat.mark(m, "pauldron-1"); // the horn, over the right pauldron
			horn(add, new double[] { p[0], p[1], p[2] }, new double[] { p[3], p[4], p[5] }, p[6], p[7], st);
		} else {
			double as = (waistY - neckY) * 0.56; // Dorrin's anvil on a blackened, bronze-edged plate
			decal(add, m, F, MidArmour.polygon(0, chest[0] + 0.5, as * 0.52, as * 0.6, 6), 0.5, 1.2, st.dark, st.trim);
			anvil(add, m, F, 0.5, chest[0] + 2.5, as * 0.9, st);
			for (int k = 0; k < 2; k++) { // the belly lames, the forge-light in their joins
				double y = belly[0] - 1 + k * 6, w = belly[2] * (0.8 - k * 0.04);
				for (Frame f : new Frame[] { F, B }) {
					decal(add, m, f, rect(-w, y, w, y + 1.5), 0.3, 1.5, st.accent, st.trimDark);
				}
			}
			for (int side = -1; side <= 1; side += 2) { // a glowing seam round each pauldron
				double[] p = BaseCoat.mark(m, "pauldron" + side);
				double[] c = { p[0], p[1], p[2] }, ax = { p[3], p[4], p[5] };
				ring(add, ArmourForge.add(c, scale(ax, p[6] * 0.46)), ax, Z, p[7] * 0.93 + 0.4, p[7] * 0.82 + 0.4, -0.5, 0.5, 7, st.accent);
			}
			for (int k = -1; k <= 1; k += 2) { // ember rivets on the belt
				for (int j = 1; j <= 2; j++) {
					double a = k * j * 0.42;
					double[] q = { Math.sin(a) * belt[3], (belt[0] + belt[1]) / 2, belt[2] - Math.cos(a) * belt[4] };
					double[] o = norm(new double[] { Math.sin(a) / belt[3], 0, -Math.cos(a) / belt[4] });
					add.tube(q, ArmourForge.add(q, scale(o, 1.4)), 1.0, 0, 4, 0.4, st.core, st.accent);
				}
			}
			for (int k = 0; k < 4; k++) { // and down the skirt, by the slit, one glowing in each band
				double[] lip = BaseCoat.mark(m, "lip" + k), above = k == 0 ? BaseCoat.mark(m, "skirt") : BaseCoat.mark(m, "lip" + (k - 1));
				double y = lip[0] - (lip[0] - above[0]) * 0.45;
				for (int side = -1; side <= 1; side += 2) {
					double[] hit = surface(m, new double[] { side * 4.6, y, -120 }, Z);
					if (hit != null) {
						add.tube(pt(hit, 0, 0, 0.3), pt(hit, 0, 0, -0.9), 1.3, 0, 4, 0.4, k == 3 ? st.core : st.accent, st.accent);
					}
				}
			}
		}
	}

	/**
	 * One of the herald's banners: a violet tail hanging from under the belt over one quarter of the skirt (side -1 / 1:
	 * right / left; face -1 / 1: front / back), draped over the lips of the bands, its point pale. It is rigid on that
	 * half of the skirt, so it swings with the leg like the plates under it.
	 */
	static void banner(ModelData add, ModelData m, int side, int face, Style st) {
		double[] belt = BaseCoat.mark(m, "belt");
		double x0 = side * 3.6, x1 = side * 9.4;
		int rows = 6;
		double[][] pl = new double[rows][], pr = new double[rows][];
		for (int k = 0; k < rows; k++) {
			double y, cz, rx, rz;
			if (k == 0) { // tucked under the belt
				y = belt[1] - 2;
				cz = belt[2];
				rx = belt[3] - 0.6;
				rz = belt[4] - 0.6;
			} else {
				double[] lip = BaseCoat.mark(m, "lip" + Math.min(k - 1, 3));
				y = k == rows - 1 ? lip[0] + 5.5 : lip[0];
				cz = lip[1];
				rx = lip[2] + 0.7;
				rz = lip[3] + 0.7;
			}
			double xl = k == rows - 1 ? (x0 + x1) / 2 : x0, xr = k == rows - 1 ? (x0 + x1) / 2 : x1;
			pl[k] = new double[] { xl, y, cz + face * rz * Math.sqrt(Math.max(0, 1 - xl * xl / (rx * rx))) };
			pr[k] = new double[] { xr, y, cz + face * rz * Math.sqrt(Math.max(0, 1 - xr * xr / (rx * rx))) };
		}
		double[] out = { 0, 0, face };
		int[] l = new int[rows], r = new int[rows];
		for (int k = 0; k < rows; k++) {
			l[k] = add.vertex(pl[k][0], pl[k][1], pl[k][2]);
			r[k] = add.vertex(pr[k][0], pr[k][1], pr[k][2]);
		}
		for (int k = 0; k + 1 < rows; k++) {
			int col = k >= rows - 2 ? st.trim : face < 0 ? st.accent : st.trimDark;
			faceOut(add, l[k], r[k], r[k + 1], col, out);
			if (k + 1 < rows - 1) {
				faceOut(add, l[k], r[k + 1], l[k + 1], col, out);
			}
		}
	}

	/** A triangle wound to face along {@code out}. */
	static void faceOut(ModelData add, int a, int b, int c, int col, double[] out) {
		double[] A = ArmourForge.d(add.vertices.get(a));
		double[] n = ModelData.cross(ArmourForge.sub(ArmourForge.d(add.vertices.get(b)), A), ArmourForge.sub(ArmourForge.d(add.vertices.get(c)), A));
		if (ArmourForge.dot(n, out) < 0) {
			add.tri(a, c, b, col);
		} else {
			add.tri(a, b, c, col);
		}
	}

	/** A knee cop's stud: the herald's ghost-light, or an ember. */
	static void coatKnee(ModelData one, ModelData m, int side, Style st) {
		double[] k = BaseCoat.mark(m, "knee" + side); // cop centre, cop axis
		double[] c = { k[0], k[1], k[2] }, ax = { k[3], k[4], k[5] };
		one.tube(ArmourForge.add(c, scale(ax, 2.6)), ArmourForge.add(c, scale(ax, 4.6)), 1.6, 0, 4, 0.78, st.core, st.motif == Motif.HERALD ? st.trim : st.accent);
	}

	/** Under the coat: the herald's violet livery stripe down the outside of each leg; Dorrin's ember seams across the greaves. */
	static void coatLegs(ModelData fr, ModelData m, Style st) {
		double[] kr = BaseCoat.mark(m, "knee-1"), kl = BaseCoat.mark(m, "knee1");
		int[] bb = m.bounds();
		double top = bb[1], kneeY = (kr[1] + kl[1]) / 2;
		for (int side = -1; side <= 1; side += 2) {
			double[] kc = side < 0 ? kr : kl;
			if (st.motif == Motif.HERALD) {
				Frame f = new Frame(new double[] { 0, 0, 0 }, new double[] { 0, 0, side }, new double[] { 0, 1, 0 }, new double[] { side, 0, 0 });
				decal(fr, m, f, rect(-1.3, top + 8, 1.3, kneeY - 10), 0.4, 1.2, st.accent, st.trimDark);
			} else { // the forge-light in the joins of the cuisses
				for (int k = 0; k < 2; k++) {
					double y = top + (kneeY - top) * (0.45 + k * 0.22);
					decal(fr, m, ArmourSets.FRONT, rect(kc[0] - 3.6, y, kc[0] + 3.6, y + 1.3), 0.3, 1.4, st.accent, st.trimDark);
				}
			}
		}
	}

	/** The roundshield: the hollow crown over the boss on violet, or the anvil under it with ember rivets round the field. */
	static void coatShield(ModelData add, ModelData m, Style st) {
		double[] s = BaseCoat.mark(m, "shield"); // centre, normal, down, across, radius
		double[] c = { s[0], s[1], s[2] }, n = { s[3], s[4], s[5] }, down = { s[6], s[7], s[8] }, across = { s[9], s[10], s[11] };
		Frame f = new Frame(c, across, down, n);
		if (st.motif == Motif.HERALD) {
			hollowCrown(add, m, f, 0, -11.5, 15, st);
			for (int k = -1; k <= 1; k += 2) { // two pale studs under the boss
				decal(add, m, f, diamond(k * 7, 11, 1.6, 1.6), 0.3, 1.8, st.trim, GHOST_DARK);
			}
			decal(add, m, f, diamond(0, 13, 1.8, 2.4), 0.3, 2.2, st.core, st.trim);
		} else {
			anvil(add, m, f, 1, 14.5, 15, st);
			ring(add, ArmourForge.add(c, scale(n, 0.6)), n, down, 7.6, 7.6, 0, 1.0, 8, st.accent);
			for (int k = 0; k < 6; k++) { // rivets round the upper field
				double a = Math.PI * (1.1 + 0.16 * k);
				double[] p = f.at(Math.cos(a) * 16.5, Math.sin(a) * 16.5);
				double[] hit = surface(m, ArmourForge.add(p, scale(n, 30)), scale(n, -1));
				double[] q = hit == null ? p : hit;
				add.tube(q, ArmourForge.add(q, scale(n, 1.4)), 1.1, 0, 4, 0.4, k % 2 == 0 ? st.core : st.accent, st.accent);
			}
		}
	}

	/** A riding boot's spur at the heel: the herald's pale, Dorrin's bronze with an ember rowel and an ember in the toe. */
	static void coatBoot(ModelData add, ModelData m, int side, Style st) {
		double[] h = BaseCoat.mark(m, "heel" + side); // x, sole, back, cuff
		double x = h[0];
		double[] s0 = { x, h[1] - 4.0, h[2] - 0.5 }, s1 = { x, h[1] - 3.2, h[2] + 4.5 };
		int col = st.trim;
		blade(add, s0, s1, X, 0.9, 0.6, col);
		add.tube(new double[] { x - 0.5, s1[1], s1[2] + 1.0 }, new double[] { x + 0.5, s1[1], s1[2] + 1.0 }, 1.9, 1.9, 5, 0.3,
				st.motif == Motif.HERALD ? st.core : st.accent, st.motif == Motif.HERALD ? st.trim : st.core);
		for (int e = -1; e <= 1; e += 2) { // the spur's strap round the ankle
			blade(add, new double[] { x + e * 4.5, h[1] - 4.5, h[2] - 3.5 }, new double[] { x + e * 3.0, h[1] - 4.2, h[2] - 0.2 }, Y, 0.8, 0.4, col);
		}
		if (st.motif == Motif.EMBERFORGED) {
			int[] bb = m.bounds();
			add.tube(new double[] { x, h[1] - 3.0, bb[2] + 3.0 }, new double[] { x, h[1] - 4.6, bb[2] + 2.0 }, 1.4, 0.9, 4, 0.78, st.core, st.core);
		}
	}

	/** A gauntlet's cuff: the herald's violet band and a ghost-light on the knuckles; Dorrin's glowing seam and ember knuckles. */
	static void coatGauntlet(ModelData add, ModelData m, int side, Style st) {
		double[] g = BaseCoat.mark(m, "cuff" + side); // wrist, forearm axis, knuckle plate
		double[] w = { g[0], g[1], g[2] }, af = { g[3], g[4], g[5] }, kc = { g[6], g[7], g[8] };
		ring(add, ArmourForge.add(w, scale(af, -8.5)), scale(af, -1), Z, 7.9, 7.6, -1.0, 1.0, 7, st.accent); // a band round the cuff
		double[] out = { side, 0, 0 };
		if (st.motif == Motif.HERALD) {
			add.tube(ArmourForge.add(kc, scale(out, 0.4)), ArmourForge.add(kc, scale(out, 1.9)), 1.3, 0, 4, 0.78, st.core, st.core);
		} else {
			for (int k = -1; k <= 1; k++) {
				double[] p = pt(kc, 0, 2.5, k * 2.2);
				add.tube(ArmourForge.add(p, scale(out, 0.4)), ArmourForge.add(p, scale(out, 1.6)), 0.9, 0, 4, 0.78, k == 0 ? st.core : st.accent, st.accent);
			}
		}
	}

	/* ------------------------------------------------------------- emblems and parts */

	/** A point offset from {@code p}. */
	static double[] pt(double[] p, double x, double y, double z) {
		return new double[] { p[0] + x, p[1] + y, p[2] + z };
	}

	/** An almond (an eye's outline), half-width w, half-height h. */
	static double[][] almond(double x, double y, double w, double h) {
		return new double[][] { { x - w, y }, { x - w * 0.4, y - h }, { x + w * 0.4, y - h }, { x + w, y }, { x + w * 0.4, y + h }, { x - w * 0.4, y + h } };
	}

	/** An n-pointed star outline: outer radius r, inner radius ri, one point straight up. */
	static double[][] starPts(double x, double y, double r, double ri, int n) {
		double[][] p = new double[n * 2][];
		for (int k = 0; k < n * 2; k++) {
			double a = -Math.PI / 2 + Math.PI * k / n, rr = k % 2 == 0 ? r : ri;
			p[k] = new double[] { x + Math.cos(a) * rr, y + Math.sin(a) * rr };
		}
		return p;
	}

	/** The upper half of a disc on the line y (y is down), from the left end over the top to the right. */
	static double[][] semicircle(double x, double y, double r, int n) {
		double[][] p = new double[n][];
		for (int k = 0; k < n; k++) {
			double a = Math.PI + Math.PI * k / (n - 1);
			p[k] = new double[] { x + Math.cos(a) * r, y + Math.sin(a) * r };
		}
		return p;
	}

	/** A free-standing plate: the outline (frame coordinates, star-shaped about its middle) extruded {@code t} either side of the frame's plane. */
	static void slab(ModelData add, Frame f, double[][] outline, double t, int front, int back, int side) {
		int n = outline.length;
		int[] a = new int[n], b = new int[n];
		double cu = 0, cv = 0;
		for (double[] q : outline) {
			cu += q[0] / n;
			cv += q[1] / n;
		}
		for (int i = 0; i < n; i++) {
			double[] p = f.at(outline[i][0], outline[i][1]);
			double[] p0 = ArmourForge.add(p, scale(f.n, t)), p1 = ArmourForge.add(p, scale(f.n, -t));
			a[i] = add.vertex(p0[0], p0[1], p0[2]);
			b[i] = add.vertex(p1[0], p1[1], p1[2]);
		}
		double[] c = f.at(cu, cv), ca = ArmourForge.add(c, scale(f.n, t)), cb = ArmourForge.add(c, scale(f.n, -t));
		int ia = add.vertex(ca[0], ca[1], ca[2]), ib = add.vertex(cb[0], cb[1], cb[2]);
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			add.triOut(a[i], a[j], ia, front, ArmourForge.add(c, scale(f.n, -20)));
			add.triOut(b[i], b[j], ib, back, ArmourForge.add(c, scale(f.n, 20)));
			add.quadOut(a[i], a[j], b[j], b[i], side, c);
		}
	}

	/**
	 * A Dawnguard wing: a flat slab from its root at {@code o}, the leading edge rising along {@code u} (outward) and up to the tip, the feathers
	 * scalloped back down its trailing edge; {@code v} is down. White on the outer face, rose at the edges.
	 */
	static void wing(ModelData add, double[] o, double[] u, double[] v, double[] out, double s, double t, Style st) {
		double[] n = norm(ModelData.cross(u, v));
		if (ArmourForge.dot(n, out) < 0) {
			n = scale(n, -1);
		}
		Frame f = new Frame(o, u, v, n);
		double[][] outline = { { 0, -2 }, { 5, -8 }, { 15, -12.5 }, { 13, -7.5 }, { 15, -4.5 }, { 11, -1 }, { 9, 3 }, { 3, 3 } };
		for (double[] p : outline) {
			p[0] *= s;
			p[1] *= s;
		}
		slab(add, f, outline, t, FEATHER, FEATHER, st.trim);
	}

	/** A riftglass shard: a short prism from {@code base} along {@code dir}, closing to a point. */
	static void shard(ModelData add, double[] base, double[] dir, double len, double r, int col, int col2) {
		double[] d = norm(dir);
		double[] mid = ArmourForge.add(base, scale(d, len * 0.35)), tip = ArmourForge.add(base, scale(d, len));
		add.tube(base, mid, r * 0.8, r, 4, 0.4, col2, col2);
		add.tube(mid, tip, r, 0, 4, 0.4, col, col);
	}

	/** A jagged crack through the points, glowing, with a darker lip. */
	static void crack(ModelData add, ModelData m, Frame f, double[][] pts, double w, int glow, int lip) {
		for (int i = 0; i + 1 < pts.length; i++) {
			double ww = w * (1 - 0.3 * i / pts.length);
			decal(add, m, f, LowArmour.bar(pts[i][0], pts[i][1], pts[i + 1][0], pts[i + 1][1], ww), 0.3, 1.5, glow, lip);
		}
	}

	/** An open eye: a white almond, a violet iris, a black pupil. */
	static void eye(ModelData add, ModelData m, Frame f, double x, double y, double size) {
		decal(add, m, f, almond(x, y, size, size * 0.48), 0.5, 2.1, STAR, SILVER);
		decal(add, m, f, MidArmour.polygon(x, y, size * 0.34, size * 0.4, 6), 0.3, 2.7, VOID_GLOW, VOID_GLOW);
		decal(add, m, f, diamond(x, y, size * 0.12, size * 0.24), 0.2, 3.2, ArmourSets.violet(6, 1), ArmourSets.violet(6, 1));
	}

	/** The herald's sigil: the Hollow King's crown, its sockets empty but for one cold light. */
	static void hollowCrown(ModelData add, ModelData m, Frame f, double x, double y, double size, Style st) {
		double s = size / 2, bw = s * 0.8, by0 = y - s * 0.05, by1 = y + s * 0.32, hOut = Math.max(2.0, s * 0.18);
		decal(add, m, f, rect(x - bw, by0, x + bw, by1), 0.5, hOut, st.trim, GHOST_DARK);
		for (int k = 0; k < 5; k++) {
			double px = x - bw + bw * 2 * k / 4.0, tall = k == 2 ? s * 0.95 : k % 2 == 0 ? s * 0.7 : s * 0.5, half = bw * 0.2;
			decal(add, m, f, new double[][] { { px - half, by0 + 0.5 }, { px, by0 - tall }, { px + half, by0 + 0.5 } }, 0.5, hOut, st.trim, GHOST_DARK);
		}
		for (int k = -1; k <= 1; k++) {
			double gx = x + k * bw * 0.55, gy = (by0 + by1) / 2;
			decal(add, m, f, diamond(gx, gy, s * 0.13, s * 0.13), 0.3, hOut + 0.6, k == 0 ? st.core : hsl(0, 0, 4), k == 0 ? st.core : hsl(0, 0, 4));
		}
	}

	/** The herald's horn, lying over a pauldron from its mouthpiece at the front to a bell raised up behind the shoulder. */
	static void horn(ModelData add, double[] c, double[] axis, double height, double radius, Style st) {
		double[] z = { 0, 0, 1 }, up = { 0, -1, 0 };
		double[][] sp = new double[6][];
		sp[0] = ArmourForge.add(ArmourForge.add(c, scale(axis, height * 0.75 + 1.4)), scale(z, -radius * 0.8));
		sp[1] = ArmourForge.add(ArmourForge.add(c, scale(axis, height + 1.6)), scale(z, -radius * 0.35));
		sp[2] = ArmourForge.add(ArmourForge.add(c, scale(axis, height + 2.3)), scale(z, radius * 0.1));
		sp[3] = ArmourForge.add(ArmourForge.add(c, scale(axis, height * 0.92 + 2.6)), scale(z, radius * 0.5));
		sp[4] = ArmourForge.add(sp[3], ArmourForge.add(scale(up, 4), scale(z, 2.5)));
		sp[5] = ArmourForge.add(sp[4], ArmourForge.add(scale(up, 4), scale(z, 0.5)));
		double[] r = { 1.4, 1.8, 2.3, 2.9, 3.6, 6.4 };
		for (int i = 0; i + 1 < 6; i++) {
			add.tube(sp[i], sp[i + 1], r[i], r[i + 1], i == 4 ? 6 : 4, 0, i == 3 ? st.accent : st.trim, i == 4 ? hsl(0, 0, 6) : st.trim);
		}
	}

	/** Dorrin's mark: an anvil, a flame rising off it. */
	static void anvil(ModelData add, ModelData m, Frame f, double x, double y, double size, Style st) {
		double s = size / 2;
		decal(add, m, f, rect(x - s * 0.5, y - s * 0.02, x + s * 0.62, y + s * 0.2), 0.5, 2.0, st.trim, st.trimDark);
		decal(add, m, f, new double[][] { { x - s * 0.5, y - s * 0.02 }, { x - s * 0.5, y + s * 0.2 }, { x - s * 0.95, y + s * 0.02 } }, 0.5, 2.0, st.trim,
				st.trimDark);
		decal(add, m, f, rect(x - s * 0.16, y + s * 0.2, x + s * 0.22, y + s * 0.45), 0.5, 1.9, st.trimDark, st.trimDark);
		decal(add, m, f, new double[][] { { x - s * 0.24, y + s * 0.42 }, { x + s * 0.3, y + s * 0.42 }, { x + s * 0.5, y + s * 0.6 }, { x - s * 0.45, y + s * 0.6 } },
				0.5, 2.0, st.trim, st.trimDark);
		double fy = y - s * 0.05;
		decal(add, m, f, new double[][] { { x, fy - s * 1.0 }, { x + s * 0.12, fy - s * 0.62 }, { x + s * 0.34, fy - s * 0.42 }, { x + s * 0.28, fy - s * 0.08 },
				{ x - s * 0.28, fy - s * 0.08 }, { x - s * 0.34, fy - s * 0.4 }, { x - s * 0.18, fy - s * 0.55 } }, 0.5, 2.1, st.accent, st.trimDark);
		decal(add, m, f, new double[][] { { x + s * 0.02, fy - s * 0.66 }, { x + s * 0.17, fy - s * 0.3 }, { x + s * 0.12, fy - s * 0.12 }, { x - s * 0.12, fy - s * 0.12 },
				{ x - s * 0.17, fy - s * 0.3 } }, 0.4, 2.7, st.core, st.accent);
	}

	/** The first dawn: a gold sun half-risen over a rose horizon, its rays fanned above. */
	static void risingSun(ModelData add, ModelData m, Frame f, double x, double y, double r, Style st) {
		int n = 7;
		for (int k = 0; k < n; k++) {
			double a = Math.PI + Math.PI * (k + 0.5) / n, ca = Math.cos(a), sa = Math.sin(a), w = r * 0.16;
			double len = r * (k % 2 == 0 ? 2.0 : 1.55);
			decal(add, m, f, new double[][] { { x + ca * r * 1.08 - sa * w, y + sa * r * 1.08 + ca * w }, { x + ca * len, y + sa * len },
					{ x + ca * r * 1.08 + sa * w, y + sa * r * 1.08 - ca * w } }, 0.5, 2.0, k % 2 == 0 ? st.accent : st.trim, st.trimDark);
		}
		decal(add, m, f, semicircle(x, y, r, 8), 0.5, 2.3, st.accent, st.trim);
		decal(add, m, f, semicircle(x, y, r * 0.55, 6), 0.4, 2.9, st.core, st.accent);
		decal(add, m, f, rect(x - r * 1.9, y, x + r * 1.9, y + r * 0.24), 0.5, 2.6, st.trim, st.trimDark);
	}

	/* ------------------------------------------------------------- the weapons */

	/** The Hollow Herald's mace: six iron flanges rising to crown points round a ghost-light, a violet pennant under the head. */
	static ModelData heraldsMace() {
		ModelData m = new ModelData();
		int iron = hsl(0, 0, 26), ironLight = hsl(0, 0, 40);
		haft(m, 3, 46, 2.4, 2.2, iron);
		knob(m, 2, 3.2, 2.6, GHOST, GHOST_DARK);
		band(m, 6, 22, 2.8, BANNER_DARK);
		for (double s : new double[] { 6, 14, 22 }) {
			band(m, s, s + 1.2, 3.0, GHOST);
		}
		collar(m, 38, 44, 2.6, 4.6, ironLight);
		collar(m, 44, 60, 4.6, 4.2, iron);
		for (int i = 0; i < 6; i++) {
			double a = Math.PI * 2 * i / 6;
			double[] V = { Math.sin(a), Math.cos(a), 0 }, N = { Math.cos(a), -Math.sin(a), 0 };
			double[][] pts = { { 43, 3.4 }, { 47, 8.6 }, { 57, 8.8 }, { 62, 6.0 }, { 64, 3.4 } };
			plate(m, v(0, 0, 0), v(0, 0, 1), V, N, pts, new double[] { 1.3, 0, 0, 0, 1.1 }, new double[] { 54, 5.8 }, 1.4, iron, ironLight, iron);
		}
		// the hollow crown on top, a cold light where its jewel was
		collar(m, 60, 63.5, 3.6, 5.6, GHOST_DARK);
		for (int k = 0; k < 5; k++) {
			double a = Math.PI * 2 * k / 5;
			tube(m, f(63, Math.cos(a) * 4.8, Math.sin(a) * 4.8), f(71 + (k == 0 ? 2 : 0), Math.cos(a) * 5.6, Math.sin(a) * 5.6), 1.7, 0, 4, a, GHOST, GHOST);
		}
		tube(m, f(61, 0, 0), f(69, 0, 0), 3.4, 0, 6, 0, GHOST_GLOW, GHOST_GLOW);
		double[][] flag = { { 25, 2.6 }, { 37, 2.6 }, { 35, 22 }, { 31, 17 }, { 27, 23 } };
		plateSW(m, flag, new double[] { 0.6, 0.6, 0.4, 0.4, 0.4 }, new double[] { 31, 11 }, 0.6, BANNER, BANNER, BANNER_DARK);
		gem(m, 31, 10, 1.6, 1.4, GHOST, GHOST);
		return m;
	}

	/** Dorrin's own hammer: a blackened block bound in bronze, ember seams across its cheeks, its face still white-hot. */
	static ModelData emberforgedHammer() {
		ModelData m = new ModelData();
		int steel = hsl(4, 1, 26), steelLight = hsl(4, 1, 38), bronze = BRONZE_EDGE, bronzeDark = hsl(6, 4, 36);
		haft(m, 2, 60, 2.5, 2.2, steel);
		knob(m, 2, 3.2, 2.5, bronze, bronzeDark);
		band(m, 4, 17, 2.9, LEATHER_DARK);
		band(m, 18, 19.5, 3.0, bronze);
		band(m, 38, 40, 3.0, bronze);
		tube(m, f(52, -10, 0), f(52, 10, 0), 8.0, 8.0, 4, Math.PI / 4, steel, steel);
		for (int side = -1; side <= 1; side += 2) {
			tube(m, f(52, side * 10, 0), f(52, side * 12, 0), 8.7, 8.7, 4, Math.PI / 4, bronze, bronze);
		}
		tube(m, f(52, 12, 0), f(52, 15.5, 0), 8.0, 7.2, 4, Math.PI / 4, steelLight, EMBER_HOT);
		tube(m, f(52, -12, 0), f(52, -19, 0), 7.6, 3.0, 4, Math.PI / 4, steelLight, EMBER);
		for (int side = -1; side <= 1; side += 2) {
			tube(m, f(52, -9, side * 5.9), f(52, 9, side * 5.9), 1.0, 1.0, 3, 0, EMBER, EMBER);
			tube(m, f(46.5, 0, side * 5.9), f(57.5, 0, side * 5.9), 1.0, 1.0, 3, 0, EMBER, EMBER);
		}
		gem(m, 52, 0, 1.8, 7.2, EMBER_HOT, EMBER_HOT);
		tube(m, f(57, 0, 0), f(63, 0, 0), 4.0, 2.4, 4, 0.3, EMBER, EMBER);
		tube(m, f(63, 0, 0), f(71, 0, 0), 2.4, 0, 4, 0.3, EMBER_HOT, EMBER_HOT);
		return m;
	}

	/** A staff of the Starless's shadow: the head is its eye, open in an almond of black, a star over it and stars set down the shaft. */
	static ModelData shroudStaff() {
		ModelData m = new ModelData();
		int shadow = hsl(44, 2, 10), shadowLight = hsl(44, 2, 20), black = ArmourSets.violet(6, 1);
		tube(m, f(6, 0, 0), f(80, 0.6, 0.4), 3.6, 3.9, 5, 0, shadow, shadow);
		tube(m, f(80, 0.6, 0.4), f(150, -0.5, -0.3), 3.9, 3.6, 5, 0.6, shadowLight, shadowLight);
		tube(m, f(150, -0.5, -0.3), f(198, 0, 0), 3.6, 4.2, 5, 1.2, shadow, shadow);
		tube(m, f(-2, 0, 0), f(6, 0, 0), 1.2, 3.8, 5, 0, SILVER, SILVER);
		band(m, 108, 132, 4.2, black);
		for (double s : new double[] { 40, 134, 172 }) {
			collar(m, s, s + 2, 4.4, 4.4, SILVER);
		}
		for (double s : new double[] { 62, 86, 156 }) {
			gem(m, s, 0, 1.1, 4.8, STAR, STAR);
		}
		collar(m, 194, 202, 4.2, 6.0, SILVER);
		// the almond: two arcs of shadow, lashes of it flaring out
		double s0 = 200, s1 = 248, cs = (s0 + s1) / 2;
		int n = 5;
		for (int side = -1; side <= 1; side += 2) {
			for (int i = 0; i < n; i++) {
				double u0 = (double) i / n, u1 = (double) (i + 1) / n;
				double r0 = 1.4 + Math.sin(Math.PI * u0) * 1.4, r1 = 1.4 + Math.sin(Math.PI * u1) * 1.4;
				tube(m, f(s0 + (s1 - s0) * u0, side * 15 * Math.sin(Math.PI * u0), 0), f(s0 + (s1 - s0) * u1, side * 15 * Math.sin(Math.PI * u1), 0), r0, r1, 4,
						Math.PI / 4, i % 2 == 0 ? shadow : shadowLight, shadow);
			}
			for (double u : new double[] { 0.3, 0.7 }) {
				double s = s0 + (s1 - s0) * u, w = side * 15 * Math.sin(Math.PI * u);
				tube(m, f(s, w, 0), f(s + (u - 0.5) * 12, w + side * 8, 0), 1.8, 0, 4, Math.PI / 4, shadowLight, shadow);
			}
		}
		// the eye itself, a white ball looking out of both faces: a violet iris, a black pupil
		tube(m, f(cs - 9, 0, 0), f(cs - 5, 0, 0), 0, 7.6, 8, Math.PI / 8, SILVER, SILVER);
		tube(m, f(cs - 5, 0, 0), f(cs + 5, 0, 0), 7.6, 7.6, 8, Math.PI / 8, STAR, STAR);
		tube(m, f(cs + 5, 0, 0), f(cs + 9, 0, 0), 7.6, 0, 8, Math.PI / 8, SILVER, SILVER);
		tube(m, f(cs, 0, -7.4), f(cs, 0, 7.4), 4.6, 4.6, 8, Math.PI / 8, VOID_GLOW, VOID_GLOW);
		tube(m, f(cs, 0, -7.9), f(cs, 0, 7.9), 1.9, 1.9, 6, 0, black, black);
		// and a star over it
		star(m, f(s1 + 6, 0, 0), 6.5, 1.8, STAR, SILVER);
		return m;
	}

	/** A riftwalker's longbow: dark limbs split by the rift's light, riftglass bound along their backs and grown out past the nocks. */
	static ModelData riftwalkerBow(double bend) {
		ModelData m = new ModelData();
		double L = 185, k = L / 127, rk = Math.sqrt(k);
		int limb = hsl(48, 1, 30), limbDark = hsl(48, 1, 20), strap = hsl(0, 0, 14);
		bow(m, L, bend, new double[] { 1.8, 2.5, 3.3, 3.9, 4.2, 3.9, 3.3, 2.5, 1.8 }, limb, limbDark, strap, RIFTGLASS, ArmourSets.violet(112, 3));
		double[][] lp = { { 24, 7 }, { 44, 9.5 }, { 64, 6 }, { 84, 9.5 }, { 104, 7 } };
		for (double[] p : lp) {
			p[0] *= k;
			p[1] *= bend * rk;
		}
		// the rift: a glowing seam down the back of each limb
		for (int i = 0; i + 1 < lp.length; i++) {
			if (i == 1 || i == 2) {
				continue;
			}
			tube(m, f(lp[i][0], lp[i][1] + 3.0, 0), f(lp[i + 1][0], lp[i + 1][1] + 3.6, 0), 0.9, 0.9, 3, 0, RIFT, RIFT);
		}
		// riftglass shards along the backs of the limbs
		for (double s : new double[] { 40, 58, 127, 145 }) {
			double t = (s < 92.5 ? s : L - s) / k; // position on the unscaled limb
			double w = (t < 44 ? 7 + (t - 24) * 0.125 : 9.5 - (t - 44) * 0.175) * bend * rk + 3.2;
			double lean = s < 92.5 ? -2.5 : 2.5;
			tube(m, f(s, w - 1, 0), f(s + lean * 0.3, w + 2, 0), 1.6, 2.0, 4, Math.PI / 4, RIFTGLASS, RIFTGLASS);
			tube(m, f(s + lean * 0.3, w + 2, 0), f(s + lean, w + 8, 0), 2.0, 0, 4, Math.PI / 4, RIFT, RIFT);
		}
		// black straps where the limbs meet the grip, the rift open in the grip itself
		for (double s : new double[] { 78, 107 }) {
			double w = 6.9 * bend * rk;
			tube(m, f(s - 2, w, 0), f(s + 2, w, 0), 4.7, 4.7, 4, Math.PI / 4, strap, strap);
		}
		double gw = 6 * bend * rk + 1.2;
		gem(m, L / 2, gw + 1.0, 1.8, 6.0, RIFT_CORE, RIFT);
		// riftglass grown out past each nock
		double tw = 4 * bend * rk;
		tube(m, f(2 * k, tw + 2, 0), f(-9, tw + 10, 0), 1.9, 0, 4, Math.PI / 4, RIFT, RIFTGLASS);
		tube(m, f(L - 2 * k, tw + 2, 0), f(L + 9, tw + 10, 0), 1.9, 0, 4, Math.PI / 4, RIFT, RIFTGLASS);
		return m;
	}

	/** The Dawnguard's greatsword: a pale gold-white blade, a sun half-risen over a guard of two white wings. */
	static ModelData dawnguardSword() {
		ModelData m = new ModelData();
		int pearl = hsl(5, 1, 106), edge = hsl(9, 1, 124), gold = hsl(8, 4, 66), goldDark = hsl(8, 5, 46), grip = hsl(1, 3, 36);
		// a little sun for a pommel, a rose grip bound in gold
		tube(m, f(1, 0, -1.4), f(1, 0, 1.4), 3.6, 3.6, 8, Math.PI / 8, gold, SUN);
		tube(m, f(-3.5, 0, 0), f(-0.5, 0, 0), 0, 2.2, 4, Math.PI / 4, SUN, SUN);
		tube(m, f(4, 0, 0), f(34, 0, 0), 2.2, 2.4, 4, Math.PI / 4, grip, grip);
		for (double s : new double[] { 9, 16, 23, 30 }) {
			band(m, s, s + 1.2, 2.7, gold);
		}
		// the guard: two wings, a gold leading edge swept out and up, white feathers hanging from it towards the hand
		for (int side = -1; side <= 1; side += 2) {
			double[] e0 = { 36, side * 2.5 }, e1 = { 43, side * 21 };
			tube(m, f(e0[0], e0[1], 0), f(e1[0], e1[1], 0), 2.4, 1.2, 4, Math.PI / 4, gold, gold);
			for (int k = 0; k < 3; k++) {
				double t = 0.35 + 0.3 * k;
				double rs = e0[0] + (e1[0] - e0[0]) * t, rw = e0[1] + (e1[1] - e0[1]) * t;
				double ts = rs - 10 - k * 3, tw = rw + side * (1.5 + k * 1.5);
				double ms = (rs + ts) / 2, mw = (rw + tw) / 2;
				double ns = -(tw - rw), nw = ts - rs, nl = Math.hypot(ns, nw), wd = 2.8;
				double[][] pts = { { rs, rw }, { ms + ns / nl * wd, mw + nw / nl * wd }, { ts, tw }, { ms - ns / nl * wd, mw - nw / nl * wd } };
				plateSW(m, pts, new double[] { 1.0, 0, 0, 0 }, new double[] { ms, mw }, 0.9, k % 2 == 0 ? FEATHER : FEATHER_DARK, edge, FEATHER_DARK);
			}
		}
		// the blade, a gold line down each face
		bladeStrip(m, new double[][] { { 40, 5.0, 1.6 }, { 50, 6.2, 1.6 }, { 104, 5.6, 1.45 }, { 128, 4.2, 1.2 } }, 148, pearl, edge, 0.55);
		for (int side = -1; side <= 1; side += 2) {
			tube(m, f(52, 0, side * 1.5), f(112, 0, side * 1.3), 0.7, 0.5, 3, 0, gold, gold);
		}
		// the sun, half-risen over the guard, its rays fanned up the blade
		double cs = 38, r = 8.5;
		double[][] disc = new double[7][];
		double[] h = new double[7];
		for (int k = 0; k < 7; k++) {
			double a = -Math.PI / 2 + Math.PI * k / 6;
			disc[k] = new double[] { cs + Math.cos(a) * r, Math.sin(a) * r };
			h[k] = 2.4;
		}
		plateSW(m, disc, h, new double[] { cs + r * 0.4, 0 }, 2.6, SUN, SUN, gold);
		gem(m, cs + r * 0.4, 0, 2.6, 3.6, SUN_CORE, SUN_CORE);
		for (int k = 0; k < 7; k++) {
			double a = -Math.PI / 2 + Math.PI * (k + 0.5) / 7;
			double len = k % 2 == 0 ? 19 : 14;
			tube(m, f(cs + Math.cos(a) * (r + 0.5), Math.sin(a) * (r + 0.5), 0), f(cs + Math.cos(a) * len, Math.sin(a) * len, 0), 2.0, 0, 4, Math.PI / 4,
					k % 2 == 0 ? SUN : ROSE, gold);
		}
		return m;
	}
}
