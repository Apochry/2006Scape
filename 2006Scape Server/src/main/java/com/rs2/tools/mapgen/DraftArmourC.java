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
 * <li>Herald's (Defence 65): The Hollow Herald, who announces the Hollow King's coming, on a base of their own (BaseBannerman): dark iron with ghostly pale trim and the King's violet. Two tall banners with the pale sigil (the King's hollow crown) stand on a back-frame over the shoulders like a sashimono; a crested great helm with an iron crown of spikes and a herald's horn for a crest; a long split violet tabard to the shins over a breastplate, the sigil on it; layered pauldrons; a horn at the hip; pointed sabatons; a heater shield with a banner hung from its top. The base carries it all, so it has no decorations. (Herald's is a possessive: pieces read 'Herald's full helm'.)</li>
 * <li>Emberforged (Defence 70): Dorrin's forge masterwork, which makes its wearer a walking furnace, on a base of its own (BaseFurnace): blackened steel, bronze edging, ember-orange glowing in the seams and the grate; top-heavy and massive. A squat riveted furnace-helm with a glowing grille and a chimney venting embers; a barrel-chested cuirass with a glowing furnace door in the breast, vents across the back, oversized blocky pauldrons, and a heavy smith's apron (Dorrin's mark on it) from the belt; heavy plate legs; anvil-heavy boots; oversized forge gauntlets; an anvil for a shield. The base carries it all, so it has no decorations.</li>
 * <li>Starless shroud (Magic 70): Robes cut from the Starless's own shadow, on its own wraith base (BaseWraith): a tall pointed cowl over a black void with two violet eye-points and a crescent of stars behind the head, a narrow shroud with sleeves running out past the hands into ragged points and a ring of stars floating behind the shoulders, a robe frayed into long points trailing on the floor, dark wraps for hands and feet. Near-black with scattered star points, a violet seam of void-light down the front, an open eye on the breast.</li>
 * <li>Riftwalker (Ranged 75): Rangers who walk through the rifts, on their own shattered base (BaseShatter): armour broken into angular shards that hover off the body with violet light in the gaps, worn lopsided: the right side in floating shards (a great shard for a pauldron, more down the arm and the right leg, a shard gauntlet), the left bound in black straps under a torn cape from the left shoulder; a faceless hood with one glowing seam down the face and riftglass splinters floating round the crown; shard-plated boots. Dark grey-violet with glowing violet.</li>
 * <li>Dawnguard (Defence 80): The guard sworn to the first dawn after the Starless, on its own winged base (BaseSeraph): a pair of great folded wings of layered metal feather-plates on the back, rising above the shoulders and tapering past the hips; a sleek winged helm with a sun-disc and its rays behind the head; fluted plate with a rising-sun crest on the breast, pointed pauldrons, hourglass faulds, fluted greaves, pointed sabatons, fluted gauntlets, a round sun-disc shield with rays standing out past the rim. Pale gold-white plate, rose-dawn trim. Above Voidsteel (75) for the wings and the sun, below Heartfire (90) for the gold.</li>
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
					new String[] { "Herald's full helm", "Herald's platebody", "Herald's platelegs", "Herald's banner shield", "Herald's sabatons",
							"Herald's gauntlets" },
					new String[] { "A great helm under an iron crown, a herald's horn for a crest. Whoever wears it is announcing a king, whether they like it or not.",
							"Iron under the Hollow King's violet, his crown on the breast and two banners standing over the shoulders. Nobody has told it to stop.",
							"Dark iron plate. Heralds walk ahead of their king; these are still walking.",
							"A heater shield with the King's banner hung from its top. The sockets in the crown are empty, bar one, and that one is looking at you.",
							"Pointed sabatons, for walking ahead of a king who never arrived.",
							"Dark gauntlets, for holding up proclamations nobody reads any more." }),
			new Style("Emberforged", "emberforged", Kind.COAT, Motif.EMBERFORGED, 70, hsl(4, 1, 26), hsl(4, 1, 16), hsl(4, 1, 40), BRONZE_EDGE,
					hsl(6, 4, 36), EMBER, EMBER_HOT, hsl(5, 3, 22), hsl(5, 3, 14), HAS_ALL,
					new String[] { "Emberforged full helm", "Emberforged platebody", "Emberforged platelegs", "Emberforged anvil", "Emberforged boots",
							"Emberforged gauntlets" },
					new String[] { "Dorrin put a chimney on it. He says it's for venting the heat. It's mostly for showing off.",
							"A furnace with a door in the front, and you inside it. Dorrin signed the apron; he signs his best work.",
							"Heavy plate that glows at the knee, like a forge with its door left open.",
							"An anvil, to block with. Dorrin left a bar on it to finish later. It's still white-hot.",
							"Boots like a pair of anvils. Puddles steam when you walk through them, if you can lift your feet.",
							"Gauntlets the size of a smith's tongs, and they don't need them. Dorrin says he forged them barehanded." }),
			new Style("Starless shroud", "shroud", Kind.MANTLE, Motif.SHROUD, 70, hsl(44, 2, 14), hsl(44, 2, 8), hsl(44, 2, 24), hsl(44, 3, 30),
					hsl(44, 3, 20), STAR, VOID_GLOW, hsl(44, 1, 12), hsl(44, 1, 8), NO_SHIELD,
					new String[] { "Starless shroud mask", "Starless shroud robe top", "Starless shroud robe bottom", null, "Starless shroud footwraps",
							"Starless shroud handwraps" },
					new String[] { "A cowl cut from the Starless's own shadow, with nothing inside it but two violet points. The stars behind it don't line up with any sky you know.",
							"Sleeves that run on past your hands, and stars that follow you about. The eye on the breast opened the day the shroud was cut, and it hasn't blinked since.",
							"Shadow that frays a little more each night, until it trails on the floor. Nobody hears you coming.", null,
							"Wraps of shadow round the feet. They don't so much walk as drift.",
							"Wraps of shadow round the hands. Your fingers look a very long way off in them." }),
			new Style("Riftwalker", "riftwalker", Kind.WRAP, Motif.RIFTWALKER, 75, hsl(48, 1, 34), hsl(48, 1, 22), hsl(48, 1, 48), hsl(0, 0, 16),
					hsl(0, 0, 10), RIFT, RIFT_CORE, hsl(48, 2, 26), hsl(48, 2, 16), NO_SHIELD,
					new String[] { "Riftwalker mask", "Riftwalker top", "Riftwalker chaps", null, "Riftwalker boots", "Riftwalker gauntlets" },
					new String[] { "A hood with no face, only a seam of rift-light where one should be. Riftwalkers say it lets them see the other side, and won't say of what.",
							"Armour that came apart in a rift and never quite came back together. The shards keep to their places, give or take a finger's breadth of light.",
							"Shards hover over one leg and straps hold the other together. Riftwalkers take whatever comes back through with them.", null,
							"Shard-plated boots. They've stepped somewhere else and back, more than once, and kept a little of the somewhere else.",
							"A shard gauntlet for the bow hand and a strap wrap for the other. The shards don't touch the glove. They never have." }),
			new Style("Dawnguard", "dawnguard", Kind.COAT, Motif.DAWNGUARD, 80, hsl(7, 1, 104), hsl(5, 1, 84), hsl(8, 1, 119), ROSE, ROSE_DARK, SUN,
					SUN_CORE, hsl(1, 3, 40), hsl(1, 3, 28), HAS_ALL,
					new String[] { "Dawnguard full helm", "Dawnguard platebody", "Dawnguard platelegs", "Dawnguard sunshield", "Dawnguard sabatons",
							"Dawnguard gauntlets" },
					new String[] { "Winged at the temples, with the first dawn after the Starless standing behind it. The Dawnguard swore to see the next one too.",
							"The first sunrise beaten into the breastplate, and a pair of wings in plate on the back. The wings are for getting there first.",
							"Rose-edged faulds over fluted plate. The Dawnguard stand their watch in the hour when the sky goes pink.",
							"A round shield made a sun, its rays standing out past the rim. Held up to the east, it's hard to tell which is which.",
							"Pointed sabatons. The Dawnguard march towards the sunrise, and the toes are pointed that way to save time.",
							"Fluted gauntlets, rose at the cuff, for saluting the morning." }), };

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
		return st.main; // (the herald's banner shield and Dorrin's anvil are their bases' own)
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
		case HERALD: // the great helm, its crown and horn crest are the base's own (BaseBannerman)
			break;
		case EMBERFORGED: // the furnace-helm, its grille and chimney are the base's own (BaseFurnace)
			break;
		case SHROUD: // the cowl, its void, its eyes and the crescent of stars are all the base's (BaseWraith)
			break;
		case RIFTWALKER: // the hood's seam and its floating splinters are the base's (BaseShatter)
			break;
		case DAWNGUARD: // the wings, the crest and the sun-disc are the base's (BaseSeraph)
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
		case HERALD: // the tabard, sigils, pauldrons, horn and banners are the base's own (BaseBannerman)
			break;
		case EMBERFORGED: // the furnace door, vents, pauldrons and apron (Dorrin's mark on it) are the base's own (BaseFurnace)
			break;
		case SHROUD: { // the Starless's open eye on the breast, over the seam of void-light; a few stars caught in the shadow, front and back
			double[] t = BaseWraith.mark(m, "torso"); // neck, waist, half-width
			double ty = t[0], th = t[1] - t[0], w = t[2];
			eye(add, m, F, 0, ty + th * 0.55, Math.max(7, w * 0.42));
			double[][] fs = { { -0.55, 0.2, 1.5 }, { 0.6, 0.55, 1.2 }, { -0.35, 0.8, 1.0 } };
			for (double[] s : fs) {
				decal(add, m, F, starPts(s[0] * w, ty + s[1] * th, s[2], s[2] * 0.4, 4), 0.3, 1.6, st.accent, SILVER);
			}
			double[][] bs = { { -0.4, 0.3, 1.8 }, { 0.25, 0.55, 1.2 }, { -0.1, 0.85, 1.4 } };
			for (double[] s : bs) {
				decal(add, m, B, starPts(s[0] * w, ty + s[1] * th, s[2], s[2] * 0.4, 4), 0.3, 1.6, st.accent, SILVER);
			}
			break;
		}
		case RIFTWALKER: // the shards, straps and torn cape are the base's (BaseShatter)
			break;
		case DAWNGUARD: { // the first sunrise after the Starless, beaten into the breastplate (the wings and pauldrons are the base's)
			double[] t = BaseWraith.mark(m, "torso"); // neck, waist
			double th = t[1] - t[0];
			risingSun(add, m, F, 0, t[0] + th * 0.42, th * 0.2, st);
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
			case DAWNGUARD: // its knee cops are the base's
				break;
			case RIFTWALKER: // its knee shards are the base's
				break;
			default:
				break;
			}
		}
		switch (st.motif) {
		case SHROUD: { // stars caught in the robe's shadow, a few to each half, front and back (its points are the base's own)
			double[] r = BaseWraith.mark(m, "robe"); // waist, hem
			double rh = r[1] - r[0];
			double[][] fs = { { -0.42, 0.3, 1.6 }, { 0.5, 0.5, 2.2 }, { -0.62, 0.72, 1.3 }, { 0.3, 0.86, 1.1 } };
			for (double[] s : fs) {
				decal(fr, m, F, starPts(s[0] * hw, r[0] + s[1] * rh, s[2], s[2] * 0.4, 4), 0.3, 1.6, st.accent, SILVER);
			}
			double[][] bs = { { 0.45, 0.35, 1.7 }, { -0.5, 0.62, 1.3 } };
			for (double[] s : bs) {
				decal(fr, m, B, starPts(s[0] * hw, r[0] + s[1] * rh, s[2], s[2] * 0.4, 4), 0.3, 1.6, st.accent, SILVER);
			}
			break;
		}
		case RIFTWALKER: // the shards and straps are the base's
			break;
		case DAWNGUARD: // the hourglass faulds are the base's
			break;
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
		case DAWNGUARD: // the sun-disc and its rays are the base's
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
			case SHROUD: // barely-there wraps: nothing on them
				break;
			case RIFTWALKER: // shard-plated in the base
				break;
			case DAWNGUARD: // the pointed sabatons are the base's
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
			case SHROUD: // barely-there wraps: nothing on them
				break;
			case RIFTWALKER: // the shard gauntlet and the strap wrap are the base's
				break;
			case DAWNGUARD: // the fluted gauntlets are the base's
				break;
			default:
				break;
			}
		}
		return add;
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

	/** An open eye: a white almond, a violet iris, a black pupil. */
	static void eye(ModelData add, ModelData m, Frame f, double x, double y, double size) {
		decal(add, m, f, almond(x, y, size, size * 0.48), 0.5, 2.1, STAR, SILVER);
		decal(add, m, f, MidArmour.polygon(x, y, size * 0.34, size * 0.4, 6), 0.3, 2.7, VOID_GLOW, VOID_GLOW);
		decal(add, m, f, diamond(x, y, size * 0.12, size * 0.24), 0.2, 3.2, ArmourSets.violet(6, 1), ArmourSets.violet(6, 1));
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
