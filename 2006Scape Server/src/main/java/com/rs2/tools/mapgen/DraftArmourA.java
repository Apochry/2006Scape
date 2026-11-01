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
 * Draft armour sets A25-A29 and their weapons: built and previewed only (they are not in ArmourSets.SETS, have no item
 * ids, and nothing packs them into the cache). See DraftArmour.
 *
 * <ol start="25">
 * <li>Ashen pilgrim (Magic 15): The wandering pilgrims who walk Nythera's roads to the old shrines: ash-grey robes smudged with shrine ash, a string of red prayer beads, a little brass censer with an ember in it, a wide travelling hat.</li>
 * <li>Thornwarden (Ranged 20): Silkwood's edge-keepers, who tend the bramble hedges that keep the spiders in: moss-green skirmisher's wraps (the WRAP base: a headband, a vest and bandolier, a kilt over bound leggings, greaves, long bracers) wound with thorny bramble, red berries, leaves over the shoulder guard, a thorn crown on the headband.</li>
 * <li>Saltgilt (Defence 30): The Drowned Maiden's officers (Skullbrine): sea-weathered mail trimmed in tarnished doubloon-gold, barnacles crusted on the shoulders and helm, a tricorn-ish brim or an anchor on the shield, the Maiden's gilt figurehead.</li>
 * <li>Tidecaller (Magic 35): Sea-mages of Smugglers' Cove who call the tide for the night crews: deep sea-blue robes, coral branches, a scallop-shell hat ornament, pearls, a wave pattern at the hem.</li>
 * <li>Shardhide (Ranged 40): Made from the Shardback, the crater-rim bear whose back bristles with the fallen star's glass: brown bear-hide wraps (the WRAP base), dark fur ruffs, pale glass shards jutting from the shoulders and spine, a bear's skull worn over the headband.</li>
 * </ol>
 */
final class DraftArmourA {

	private DraftArmourA() {
	}

	/** Draft pieces have no item ids: 0 marks a piece the set has, -1 one it doesn't. */
	static final int[] HAS_ALL = { 0, 0, 0, 0, 0, 0 }, NO_SHIELD = { 0, 0, 0, -1, 0, 0 };

	/** Colours the sets share with their weapons. */
	static final int BEAD = hsl(0, 6, 40), BEAD_DARK = hsl(0, 6, 28), BRASS_LIGHT = hsl(8, 4, 70), EMBER_GLOW = hsl(5, 6, 92),
			THORN = hsl(7, 2, 78), THORN_DARK = hsl(7, 2, 58), BERRY = hsl(0, 6, 42), BERRY_LIGHT = hsl(0, 5, 62), LEAF = hsl(14, 5, 60),
			LEAF_DARK = hsl(15, 5, 44), GOLD = hsl(8, 4, 60), GOLD_DARK = hsl(8, 4, 40), BARNACLE = hsl(8, 1, 92), BARNACLE_DARK = hsl(8, 1, 66),
			CORAL = hsl(3, 4, 74), CORAL_DARK = hsl(2, 5, 56), PEARL = hsl(36, 1, 116), SHELL = hsl(5, 2, 100), SHELL_DARK = hsl(4, 3, 80),
			GLASS = ArmourSets.violet(70, 6), GLASS_LIGHT = ArmourSets.violet(98, 7), GLASS_DARK = ArmourSets.violet(50, 6), BONE_PALE = hsl(7, 1, 96),
			FUR = hsl(5, 2, 20), FUR_DARK = hsl(5, 2, 13), BEAR_EAR = hsl(5, 2, 32);

	static final Style[] SETS = {
			new Style("Ashen pilgrim", "ashenpilgrim", Kind.ROBE, Motif.ASHEN, 15, hsl(6, 1, 62), hsl(6, 1, 44), hsl(6, 1, 80), hsl(5, 2, 40), hsl(5, 2, 28),
					BEAD, EMBER_GLOW, hsl(6, 2, 30), hsl(6, 2, 20), NO_SHIELD,
					new String[] { "Ashen pilgrim hat", "Ashen pilgrim robe top", "Ashen pilgrim robe bottom", null, "Ashen pilgrim boots", "Ashen pilgrim gloves" },
					new String[] { "A wide road-hat with a shrine token in the band. It has kept the rain off every road on Nythera.",
							"Ash-grey robes, red prayer beads and a censer whose ember came from the last shrine. It goes to the next one.",
							"Ragged at the hem from shrine to shrine. The pilgrims say the dust is a sort of prayer.", null,
							"Wrapped boots for long roads. The wrappings are older than the boots.", "A bead for every shrine. The string is getting long." }),
			new Style("Thornwarden", "thornwarden", Kind.WRAP, Motif.THORN, 20, hsl(16, 3, 40), hsl(16, 3, 28), hsl(16, 2, 56), hsl(6, 3, 30), hsl(6, 3, 20),
					BERRY, BERRY_LIGHT, hsl(7, 3, 34), hsl(7, 3, 22), NO_SHIELD,
					new String[] { "Thornwarden headband", "Thornwarden vest", "Thornwarden kilt", null, "Thornwarden greaves", "Thornwarden bracers" },
					new String[] { "A crown of bramble on a green headband. Silkwood's spiders give it a wide berth; so does everyone else.",
							"A moss-green vest, the hedge that keeps the spiders in wound along its bandolier. The berries are for the birds. Mostly.",
							"Green panels over bound leggings, and bramble at the knee. Whatever crawls out of Silkwood has to get past these first.", null,
							"Bark greaves, and thorns at the ankle. Spiders won't climb what bites back.", "Thorn-wound bracers with a leaf tucked in. Put them on carefully." }),
			new Style("Saltgilt", "saltgilt", Kind.CHAIN, Motif.SALTGILT, 30, hsl(30, 1, 60), hsl(30, 1, 40), hsl(30, 1, 80), GOLD, GOLD_DARK, BARNACLE,
					hsl(9, 2, 120), hsl(36, 2, 24), hsl(36, 2, 16), HAS_ALL,
					new String[] { "Saltgilt med helm", "Saltgilt chainbody", "Saltgilt platelegs", "Saltgilt sq shield", "Saltgilt boots", "Saltgilt gloves" },
					new String[] { "An officer's tricorn on a mail cap. The barnacles have served aboard the Drowned Maiden longer than anyone.",
							"Sea-green mail and gilt epaulettes. Captain Blackbrine liked his officers to shine, even underwater.",
							"A gold stripe down each leg, for rank. The Maiden kept her officers in style, if not in air.",
							"The Maiden's anchor in gilt. It held her fast to the cove floor for a hundred years.",
							"Gold-buckled sea boots. They still pour out a little seawater every morning.", "Gilt-cuffed gloves, for saluting a captain who isn't there." }),
			new Style("Tidecaller", "tidecaller", Kind.ROBE, Motif.TIDE, 35, hsl(38, 4, 46), hsl(38, 4, 30), hsl(38, 3, 64), hsl(31, 4, 46), hsl(31, 4, 32),
					PEARL, hsl(36, 0, 124), hsl(36, 2, 26), hsl(36, 2, 18), NO_SHIELD,
					new String[] { "Tidecaller hat", "Tidecaller robe top", "Tidecaller robe bottom", null, "Tidecaller boots", "Tidecaller gloves" },
					new String[] { "A scallop shell, a string of pearls and a sprig of coral. The tide comes when it's called; the hat helps.",
							"Sea-blue robes with coral growing at the shoulder. The Cove's night crews don't sail without one aboard.",
							"Waves break round the hem whether there's water or not.", null, "Pearl-buttoned boots, always a little damp.",
							"Coral at the wrist. The tide likes a little jewellery." }),
			new Style("Shardhide", "shardhide", Kind.WRAP, Motif.SHARDHIDE, 40, hsl(5, 2, 40), hsl(5, 2, 28), hsl(5, 2, 52), FUR, FUR_DARK, GLASS, GLASS_LIGHT,
					hsl(6, 2, 26), hsl(6, 2, 17), NO_SHIELD,
					new String[] { "Shardhide headdress", "Shardhide harness", "Shardhide kilt", null, "Shardhide greaves", "Shardhide bracers" },
					new String[] { "A Shardback's skull, star-glass and all. It was a good deal angrier wearing it than you are.",
							"A Shardback-hide harness, the star's glass still jutting from the shoulder. Don't lean back on anything you like.",
							"Strips of bear hide with its claws at the hip. Gatehold's miners won't stand downwind of you.", null,
							"Fur-topped hide greaves over the Shardback's own paws, more or less. The claws still dig in.", "Long hide bracers, fur at the elbow and a sliver of star-glass at each wrist. It hums near starmetal." }),
	};

	/** Each set's weapon, in set order (id 0: not in the game). */
	static final Rung[] WEAPONS = {
			new Rung(0, "Ashwood staff", "staff", 400,
					"Pale ashwood with a censer swinging from the crook. Pilgrims light it at one shrine and carry the ember to the next.", DraftArmourA::ashwoodStaff,
					STAFF),
			new Rung(0, "Thornwood shortbow", "shortbow", 700, "Bramble-wood, thorns and all. The Thornwardens say it keeps your grip honest.",
					() -> thornwoodBow(1.0), () -> thornwoodBow(2.4), SHORTBOW),
			new Rung(0, "Saltgilt cutlass", "scimitar", 2500,
					"An officer's cutlass off the Drowned Maiden: a gilt guard and a doubloon for a pommel. Mad Pete would like the doubloon back.",
					DraftArmourA::saltgiltCutlass, SCIMITAR),
			new Rung(0, "Coral wand", "wand", 3000, "A branch of living coral with a pearl in the fork. It's always damp, and the tide always knows where it is.",
					DraftArmourA::coralWand, WAND),
			new Rung(0, "Shardglass longbow", "longbow", 6000, "The Shardback's glass, set down a bow's back. The arrows leave it with a little starlight on them.",
					() -> shardglassBow(1.0), () -> shardglassBow(2.4), LONGBOW),
	};

	/** Whether this file builds the set. */
	static boolean owns(Style st) {
		return st.motif == Motif.ASHEN || st.motif == Motif.THORN || st.motif == Motif.SALTGILT || st.motif == Motif.TIDE || st.motif == Motif.SHARDHIDE;
	}

	/** Saltgilt's shield: a sea-black board in a gold rim. */
	static int shieldFace(Style st) {
		return st.motif == Motif.SALTGILT ? st.leather : st.main;
	}

	static int shieldRim(Style st) {
		return st.motif == Motif.SALTGILT ? st.trim : st.dark;
	}

	static final double[] UP = { 0, -1, 0 };

	/* ------------------------------------------------------------- the helm */

	/** What's added to the helm (the base mesh, recoloured, is already there). */
	static ModelData helm(Style st, ModelData m) {
		int[] bb = m.bounds();
		double top = bb[1], bottom = bb[4], h = bottom - top;
		ModelData add = new ModelData();
		switch (st.motif) {
		case ASHEN: { // a wide travelling brim, a dark band with a brass shrine-token, red beads hanging at the side
			double by = bottom - h * 0.3 - 2.5;
			double[] s = slab(m, by - 2, by + 2);
			brim(add, s[0], by + 1, s[1], s[2] * 0.9, s[3] * 0.9, s[2] * 2.0, s[3] * 1.8, 3.5, 1.2, 10, st.trim, st.trimDark);
			ring(add, new double[] { s[0], by + 0.5, s[1] }, UP, X, s[2] * 1.0, s[3] * 1.0, 0, 3.0, 10, st.trimDark);
			double[] p = { s[0], by - 1.5, s[1] - s[3] - 0.8 };
			add.tube(p, add(p, new double[] { 0, 0, -1.4 }), 2.2, 2.2, 6, 0, BRASS, BRASS_DARK);
			ball(add, add(p, new double[] { 0, 0, -1.8 }), 0.9, st.core);
			for (int k = 0; k < 4; k++) { // a short string of beads from the brim, at the left
				ball(add, new double[] { s[0] + s[2] * 1.45, by + 4 + k * 2.6, s[1] - s[3] * 0.5 }, 1.2, 3, k == 3 ? BRASS : st.accent);
			}
			break;
		}
		case THORN: { // a crown of bramble wound round the headband: thorns standing up, berries and leaves at the side
			double by = top + 2.2;
			double[] s = slab(m, top - 1, top + 3.5); // the band (the wrap's helm is a headband: its top is the band's)
			ring(add, new double[] { s[0], by, s[1] }, UP, X, s[2] * 1.06, s[3] * 1.06, -1.6, 1.6, 8, st.trim);
			for (int k = 0; k < 9; k++) {
				double a = Math.PI * 2 * k / 9 - Math.PI / 2;
				double[] o = { Math.cos(a), 0, Math.sin(a) };
				double[] r = { s[0] + o[0] * s[2] * 1.04, by, s[1] + o[2] * s[3] * 1.04 };
				double tall = k % 2 == 0 ? 10 : 6.5;
				blade(add, r, add(r, new double[] { o[0] * 3.5, -tall, o[2] * 3.5 }), new double[] { -o[2], 0, o[0] }, 1.8, 1.0, k % 2 == 0 ? THORN : THORN_DARK);
			}
			double[] c = { s[0] - s[2] * 0.95, by + 1, s[1] - s[3] * 0.35 };
			ball(add, add(c, new double[] { -1.2, 0, -1 }), 1.5, BERRY);
			ball(add, add(c, new double[] { -1.2, 2.2, 1 }), 1.5, BERRY_LIGHT);
			ball(add, add(c, new double[] { -1.0, -1.0, 2.2 }), 1.3, BERRY);
			blade(add, add(c, new double[] { 0, 0, 2 }), add(c, new double[] { -4, -3, 9 }), Y, 2.0, 0.4, LEAF);
			blade(add, add(c, new double[] { 0, 1, 3 }), add(c, new double[] { -3, 4, 10 }), Y, 1.8, 0.4, LEAF_DARK);
			break;
		}
		case SALTGILT: { // a tricorn brim, gold-laced and turned up; barnacles crusted on the crown at one side
			double by = top + h * 0.34;
			double[] s = slab(m, by - 2, by + 2);
			tricorn(add, s[0], by, s[1], s[2] * 0.95, s[3] * 0.95, s[2] * 1.8, s[3] * 1.65, st.leather, st.trim);
			for (int k = 0; k < 4; k++) {
				double a = Math.PI * (0.72 + k * 0.2), y = top + h * (0.14 + (k % 2) * 0.1);
				double[] q = surface(m, new double[] { s[0] + Math.cos(a) * 40, y, s[1] + Math.sin(a) * 40 }, new double[] { -Math.cos(a), 0, -Math.sin(a) });
				if (q != null) {
					barnacle(add, q, new double[] { Math.cos(a), -0.4, Math.sin(a) }, k % 2 == 0 ? 1.9 : 1.4);
				}
			}
			break;
		}
		case TIDE: { // a string of pearls round the hat, a scallop shell at the front, coral branching up the side
			double by = bottom - h * 0.3;
			double[] s = slab(m, by - 2, by + 2);
			ring(add, new double[] { s[0], by, s[1] }, UP, X, s[2] * 1.0, s[3] * 1.0, 0, 2.0, 10, st.trimDark);
			for (int k = 0; k < 9; k++) {
				double a = Math.PI * 2 * k / 9 + Math.PI / 2;
				ball(add, new double[] { s[0] + Math.cos(a) * s[2] * 1.06, by - 1, s[1] + Math.sin(a) * s[3] * 1.06 }, 1.2, 3, PEARL);
			}
			Frame hf = new Frame(new double[] { s[0], 0, s[1] - s[3] - 1 }, X, Y, new double[] { 0, 0, -1 });
			shell(add, m, hf, 0, by - 9, 8, SHELL, SHELL_DARK);
			coral(add, new double[] { s[0] + s[2] * 0.85, by - 1, s[1] + s[3] * 0.2 }, norm(new double[] { 0.45, -1, 0.15 }), 6, 1.4, 2, CORAL, CORAL_DARK);
			break;
		}
		case SHARDHIDE: { // a bear's skull worn over the headband: snout and fangs over the brow, round ears, star-glass up the crown
			double by = top + 0.5; // the band's top edge; the skull sits on the crown above it, the hair showing behind
			double[] s = slab(m, top - 1, top + 3.5);
			double fz = s[1] - s[3], cx = s[0];
			// the skull's crown, its snout reaching out over the brow, dark eye-holes either side of it
			dome(add, new double[] { cx, by + 0.5, s[1] + 0.5 }, UP, Z, s[3] * 1.04, s[2] * 1.1, 10.5, 6, BONE_DARK, BONE_PALE);
			add.tube(new double[] { cx, by - 2.5, fz + 5 }, new double[] { cx, by + 0.5, fz - 6 }, 3.8, 2.7, 4, Math.PI / 4, BONE_PALE, BONE_DARK);
			add.tube(new double[] { cx, by + 0.5, fz - 6 }, new double[] { cx, by + 1, fz - 8 }, 2.7, 1.5, 4, Math.PI / 4, BONE_DARK, FUR_DARK);
			for (int side = -1; side <= 1; side += 2) {
				add.tube(new double[] { cx + side * 3.6, by - 3.5, fz + 2.5 }, new double[] { cx + side * 3.8, by - 3.5, fz - 0.6 }, 1.5, 1.1, 4, Math.PI / 4,
						FUR_DARK, FUR_DARK);
				add.tube(new double[] { cx + side * 2.0, by + 1, fz - 4.5 }, new double[] { cx + side * 2.2, by + 6.5, fz - 4 }, 1.1, 0, 4, 0.78, BONE_PALE,
						BONE_PALE);
				// round fur ears, standing off the crown at either side
				double[] e = { cx + side * s[2] * 0.9, by - 5, s[1] + 1.5 };
				add.tube(e, add(e, new double[] { side * 3.5, -5, 0.5 }), 3.0, 2.2, 5, 0, BEAR_EAR, BONE_DARK);
			}
			for (int k = 0; k < 3; k++) { // star-glass fanned up the crown, the middle shard tallest
				double[] r = { cx + (k - 1) * 2.5, by - 8.5, s[1] + 2 };
				add.tube(r, add(r, new double[] { (k - 1) * 4, -(k == 1 ? 13 : 9), 3 }), 2.6, 0, 4, 0.4, k == 1 ? st.core : st.accent, st.accent);
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
		double top = bb[1], bottom = bb[4], h = bottom - top, front = bb[2], back = bb[5], cz = (front + back) / 2;
		// the robe torso's bounds take in its collar flaps; measure the chest itself
		double hw = st.kind == Kind.ROBE ? halfWidth(m, top + h * 0.3, top + h * 0.6) : bb[3];
		double cy = top + h * 0.42, sx = hw * 0.7, sy = top + h * 0.12;
		ModelData add = new ModelData();
		Frame F = ArmourSets.FRONT, B = ArmourSets.BACK;
		switch (st.motif) {
		case ASHEN: { // red prayer beads round the neck, a scrip-strap across, a brass censer with an ember hung from it at the hip
			double ny = top + h * 0.22;
			// the strap first, so the beads lie over it: over the left shoulder, down to the right hip, front and back
			for (Frame f : new Frame[] { F, B }) {
				decal(add, m, f, new double[][] { { hw * 0.5, top + h * 0.2 }, { hw * 0.78, top + h * 0.2 }, { -hw * 0.72, bottom - 3 }, { -hw * 1.0, bottom - 3 } }, 0.6,
						1.6, st.leather, st.leatherDark);
			}
			for (int k = 0; k <= 8; k++) {
				double a = Math.PI * k / 8;
				double x = -Math.cos(a) * hw * 0.42, y = ny + Math.sin(a) * h * 0.34;
				decal(add, m, F, diamond(x, y, 1.7, 1.7), 0.4, 2.8, k % 4 == 0 ? BRASS : st.accent, BEAD_DARK);
			}
			decal(add, m, F, polygon(0, ny + h * 0.42, 2.6, 2.6, 6), 0.4, 3.0, BRASS, BRASS_DARK);
			decal(add, m, F, diamond(0, ny + h * 0.42, 1.2, 1.2), 0.3, 3.6, st.core, BRASS);
			// the censer, on a short chain from the strap's end at the right hip: a brass pot, its lid, the ember in its slots
			double lx = -halfWidth(m, bottom - 6, bottom) - 3.8, ly = bottom + 4, lz = cz - 2;
			add.tube(new double[] { lx + 2, bottom - 4, lz }, new double[] { lx, ly - 5, lz }, 0.6, 0.6, 3, 0, BRASS_DARK, BRASS_DARK);
			add.tube(new double[] { lx, ly - 5.5, lz }, new double[] { lx, ly - 3, lz }, 1.0, 4.2, 6, 0, BRASS_LIGHT, BRASS);
			add.tube(new double[] { lx, ly - 3, lz }, new double[] { lx, ly + 2.5, lz }, 4.2, 4.0, 6, 0, BRASS, BRASS_DARK);
			add.tube(new double[] { lx, ly + 2.5, lz }, new double[] { lx, ly + 4.5, lz }, 4.0, 0, 6, 0, BRASS_DARK, BRASS_DARK);
			for (int k = 0; k < 3; k++) { // the ember, glowing through the slots
				double a = Math.PI * 2 * k / 3 - Math.PI / 2;
				add.tube(new double[] { lx + Math.cos(a) * 3.6, ly, lz + Math.sin(a) * 3.6 }, new double[] { lx + Math.cos(a) * 4.5, ly, lz + Math.sin(a) * 4.5 }, 1.4,
						1.4, 4, 0, st.core, st.core);
			}
			break;
		}
		case THORN: { // bramble wound along the bandolier, thorns and berries on it; a spray of leaves over the shoulder guard
			double[] sl = BaseWrap.strapLine(m), dir = norm(new double[] { sl[2] - sl[0], sl[3] - sl[1], 0 });
			double[] across = { -dir[1], dir[0] };
			for (Frame f : new Frame[] { F, B }) { // the vine winds over and under the strap
				double[][] path = new double[13][];
				for (int i = 0; i < path.length; i++) {
					double t = i / (path.length - 1.0), w = Math.sin(t * Math.PI * 5) * 2.6;
					path[i] = new double[] { sl[0] + (sl[2] - sl[0]) * t + across[0] * w, sl[1] + (sl[3] - sl[1]) * t + across[1] * w };
				}
				BaseWrap.ribbon(add, m, f, path, 0.9, 1.9, st.trim, -1);
				for (int k = 1; k <= 5; k++) {
					double t = k / 6.0, w = (k % 2 == 0 ? 1 : -1) * 2.4;
					thornAt(add, m, f, sl[0] + (sl[2] - sl[0]) * t + across[0] * w, sl[1] + (sl[3] - sl[1]) * t + across[1] * w, 4.2,
							k % 2 == 0 ? THORN : THORN_DARK);
				}
			}
			for (double[] b : new double[][] { { 0.3, -2.8 }, { 0.34, -0.8 }, { 0.27, 0.6 }, { 0.78, 3.0 }, { 0.82, 1.2 } }) {
				double x = sl[0] + (sl[2] - sl[0]) * b[0] + across[0] * b[1], y = sl[1] + (sl[3] - sl[1]) * b[0] + across[1] * b[1];
				decal(add, m, F, diamond(x, y, 1.7, 1.7), 0.4, 3.4, BERRY, hsl(0, 6, 28));
			}
			double[] g = BaseWrap.guardCap(m); // leaves fanned over the guard's cap, front to back
			for (int k = 0; k < 5; k++) {
				double[] r = { g[0] - 1, g[1] + 1.5, g[2] - 6 + k * 3 };
				blade(add, r, add(r, new double[] { 7 + (k % 2) * 2.5, 4 + (k % 2), (k - 2) * 2.2 }), Y, 2.8, 0.5, k % 2 == 0 ? LEAF : LEAF_DARK);
			}
			break;
		}
		case SALTGILT: { // gold epaulettes, an officer's gold buttons, barnacles crusted on the left shoulder, the Maiden's anchor on the back
			for (int side = -1; side <= 1; side += 2) {
				double[] c = { side * sx, sy, cz - 1 };
				double[] axis = norm(new double[] { side * 0.5, -0.86, 0 });
				dome(add, c, axis, Z, hw * 0.46, hw * 0.4, hw * 0.16, 5, st.trimDark, st.trim);
			}
			for (int k = 0; k < 3; k++) {
				double a = Math.PI * (1.25 + k * 0.25);
				double[] c = { sx * 1.05 + Math.cos(a) * 4, sy - 2.5, cz + Math.sin(a) * 6 };
				barnacle(add, c, norm(new double[] { 0.4, -1, Math.sin(a) * 0.4 }), k % 2 == 0 ? 2.0 : 1.5);
			}
			for (int side = -1; side <= 1; side += 2) {
				for (int k = 0; k < 2; k++) {
					decal(add, m, F, diamond(side * hw * 0.28, top + h * (0.4 + k * 0.24), 1.8, 1.8), 0.6, 2.8, st.trim, st.trimDark);
				}
			}
			anchor(add, m, B, 0, cy + 1, h * 0.62, st.trim, st.trimDark);
			break;
		}
		case TIDE: { // a string of pearls and a scallop on the breast, coral grown over the left shoulder, waves breaking round the back
			double ny = top + h * 0.22;
			for (int k = 0; k <= 8; k++) {
				double a = Math.PI * k / 8;
				decal(add, m, F, diamond(-Math.cos(a) * hw * 0.4, ny + Math.sin(a) * h * 0.18, 1.2, 1.2), 0.4, 2.4, PEARL, st.trimDark);
			}
			shell(add, m, F, 0, ny + h * 0.31, h * 0.2, SHELL, SHELL_DARK);
			double[] p = { sx * 1.0, sy - 1, cz };
			coral(add, p, norm(new double[] { 0.35, -1, -0.2 }), 7, 1.8, 2, CORAL, CORAL_DARK);
			coral(add, add(p, new double[] { -2, 0, 3 }), norm(new double[] { 0.1, -1, 0.45 }), 5, 1.4, 1, CORAL_DARK, CORAL);
			for (int k = 0; k < 4; k++) {
				waveCrest(add, m, B, -hw * 0.95 + k * hw * 0.48, bottom - h * 0.1, hw * 0.48, h * 0.17, PEARL, st.trim);
			}
			break;
		}
		case SHARDHIDE: { // a dark fur ruff, star-glass jutting from the shoulder guard and the bare shoulder, and down the spine
			double[] t = BaseWrap.trunk(m);
			double thw = t[0], ttop = t[1], th = t[2] - t[1], tcz = (t[3] + t[4]) / 2, tsx = thw * 0.7, tsy = ttop + th * 0.12;
			for (int k = 0; k < 14; k++) {
				double a = Math.PI * 2 * k / 14;
				double x = Math.cos(a) * thw * 0.6, z = tcz + Math.sin(a) * (t[4] - t[3]) / 2 * 0.78;
				blade(add, new double[] { x, ttop + 1, z }, new double[] { x * 1.35, ttop + th * 0.2, tcz + (z - tcz) * 1.25 },
						new double[] { -Math.sin(a), 0, Math.cos(a) }, 3.0, 1.1, k % 2 == 0 ? st.trim : st.trimDark);
			}
			double[] g = BaseWrap.guardCap(m);
			for (int k = 0; k < 3; k++) { // from the guard's cap (they ride the arm with it)
				double[] r = { g[0] + k * 1.2, g[1] + 1.5, g[2] - 3 + k * 3.5 };
				add.tube(r, add(r, new double[] { 4 + k * 2, -(13 - k * 2.5), 3 + k * 1.5 }), 2.8 - k * 0.3, 0, 4, 0.4 + k, k == 1 ? st.core : st.accent, st.accent);
			}
			for (int k = 0; k < 2; k++) { // and two from the hide strap over the bare right shoulder
				double[] r = { -tsx * (0.72 + k * 0.12), tsy, tcz - 1 + k * 3.5 };
				add.tube(r, add(r, new double[] { -(3 + k * 2), -(11 - k * 2.5), 3 + k * 1.5 }), 2.4 - k * 0.3, 0, 4, 0.4 + k, k == 0 ? st.core : st.accent,
						st.accent);
			}
			for (int k = 0; k < 4; k++) {
				double y = ttop + th * (0.25 + k * 0.17);
				double[] p = project(m, B, 0, y);
				add.tube(add(p, new double[] { 0, 0, -1 }), add(p, new double[] { (k % 2 == 0 ? 1.5 : -1.5), -4, 9.5 - k * 1.2 }), 2.6 - k * 0.2, 0, 4, 0.4 + k,
						k % 2 == 0 ? st.accent : st.core, st.accent);
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
			case THORN: { // bramble wound round each leg above the knee, between the kilt and the greave
				double y = top + h * 0.64 - 7;
				double[] s = legRing(m, side, y, 2.6);
				ring(one, new double[] { s[0], y, s[1] }, norm(new double[] { 0, -1, -0.25 }), X, s[2], s[3], -1.3, 1.3, 6, st.trim);
				for (int k = 0; k < 5; k++) {
					double a = Math.PI * 2 * k / 5 + 0.6;
					double[] o = { Math.cos(a), 0, Math.sin(a) }, r = { s[0] + o[0] * s[2], y - o[2] * 1.5, s[1] + o[2] * s[3] };
					one.tube(r, add(r, new double[] { o[0] * 4, -1.2, o[2] * 4 }), 1.0, 0, 3, 0, k % 2 == 0 ? THORN : THORN_DARK, THORN);
				}
				break;
			}
			case SALTGILT: // gilt knee-cops, a barnacle crusted on the right
				LowArmour.kneePlate(one, m, x, kneeY, st.trim, st.trimDark);
				if (side < 0) {
					double[] p = project(m, F, x + 3.5, kneeY + 6);
					barnacle(one, p, new double[] { 0.3, 0, -1 }, 1.6);
					barnacle(one, add(p, new double[] { -2.4, -2, 0 }), new double[] { 0, 0, -1 }, 1.2);
				}
				break;
			case SHARDHIDE: { // a ruff of fur bound round the leg above the knee, a shard at the outside of it
				double y = top + h * 0.64 - 8;
				double[] s = legRing(m, side, y, 2.2);
				for (int k = 0; k < 6; k++) {
					double a = Math.PI * 2 * k / 6 + 0.2, ca = Math.cos(a), sa = Math.sin(a);
					double[] r = { s[0] + ca * s[2], y - 1.5, s[1] + sa * s[3] };
					blade(one, r, add(r, new double[] { ca * 2, 6 + (k % 2) * 1.5, sa * 2 }), new double[] { -sa, 0, ca }, 2.6, 0.9, k % 2 == 0 ? st.trim : st.trimDark);
				}
				double[] r = { s[0] + side * s[2], y, s[1] };
				one.tube(add(r, new double[] { -side * 1.5, 1, 0 }), add(r, new double[] { side * 5, -8, -1.5 }), 2.2, 0, 4, 0.4, st.accent, st.accent);
				one.tube(add(r, new double[] { -side * 1.5, 3, 2 }), add(r, new double[] { side * 4, -3, 3.5 }), 1.6, 0, 4, 0.9, st.core, st.core);
				break;
			}
			default:
				break;
			}
		}
		switch (st.motif) {
		case ASHEN: { // a knotted cord at the waist, its ends hanging; a hem gone ragged on the road
			double[] p = project(m, F, -hw * 0.12, top + 3);
			fr.tube(add(p, new double[] { 1, 0, -0.5 }), add(p, new double[] { -1, 0, -2.5 }), 1.8, 1.8, 5, 0, st.light, st.dark);
			for (int k = 0; k < 2; k++) {
				double x0 = -hw * 0.12 + (k == 0 ? -1.5 : 1.5), y1 = top + h * (0.34 + k * 0.08);
				double[] a = project(m, F, x0, top + 5), b = project(m, F, x0 + (k == 0 ? -2 : 1), y1);
				fr.tube(add(a, new double[] { 0, 0, -1.2 }), add(b, new double[] { 0, 0, -1.2 }), 0.7, 0.7, 4, 0, st.light, st.light);
				fr.tube(add(b, new double[] { 0, -1.2, -1.2 }), add(b, new double[] { 0, 1.2, -1.2 }), 1.3, 1.3, 5, 0, st.dark, st.dark);
			}
			double[] hem = LowArmour.hem(m, bottom);
			for (int k = 0; k < 12; k++) {
				double a = Math.PI * 2 * k / 12 + 0.2;
				double x = hem[0] + Math.cos(a) * hem[2] * 0.95, z = hem[1] + Math.sin(a) * hem[3] * 0.95;
				blade(fr, new double[] { x, bottom - 5, z }, new double[] { x * 1.05, bottom + 2 + (k % 3), z + Math.sin(a) * 1.5 },
						new double[] { -Math.sin(a), 0, Math.cos(a) }, 2.8, 0.6, k % 2 == 0 ? st.dark : hsl(6, 1, 30));
			}
			break;
		}
		case THORN: // berries and a leaf at the belt
			for (double[] b : new double[][] { { hw * 0.45, top + 5 }, { hw * 0.55, top + 7.5 }, { hw * 0.36, top + 8 } }) {
				decal(fr, m, F, diamond(b[0], b[1], 1.5, 1.5), 0.4, 3.0, BERRY, hsl(0, 6, 28));
			}
			for (int k = 0; k < 2; k++) {
				double[] p = project(m, F, hw * 0.5, top + 4);
				blade(fr, add(p, new double[] { 0, 0, -1 }), add(p, new double[] { 3 + k * 2, 6 + k * 4, -2 }), Z, 2.2, 0.4, k == 0 ? LEAF : LEAF_DARK);
			}
			break;
		case SALTGILT: // a sword-belt with a doubloon buckle, the gold stripe down each leg
			for (Frame f : new Frame[] { F, B }) {
				decal(fr, m, f, rect(-hw * 1.02, top + 1, hw * 1.02, top + 5), 0.7, 1.6, st.leather, st.leatherDark);
			}
			decal(fr, m, F, polygon(0, top + 3, 3.2, 3.2, 8), 0.6, 2.6, st.trim, st.trimDark);
			decal(fr, m, F, polygon(0, top + 3, 1.6, 1.6, 6), 0.3, 3.2, st.core, st.trim);
			for (int side = -1; side <= 1; side += 2) {
				Frame sf = new Frame(new double[] { 0, 0, cz }, new double[] { 0, 0, side }, Y, new double[] { side, 0, 0 });
				decal(fr, m, sf, rect(-1.2, top + 6, 1.2, kneeY - 8), 0.5, 1.6, st.trim, st.trimDark);
			}
			break;
		case TIDE: { // waves breaking round the hem, pearls at the belt
			double[] hem = LowArmour.hem(m, bottom);
			for (Frame f : new Frame[] { F, B }) {
				for (int k = 0; k < 5; k++) {
					double w = hem[2] * 0.36;
					waveCrest(fr, m, f, -hem[2] * 0.9 + k * w, bottom - h * 0.1, w, h * 0.1, PEARL, st.trimDark);
				}
			}
			for (int k = -2; k <= 2; k++) {
				decal(fr, m, F, diamond(k * hw * 0.14, top + 4 + Math.abs(k) * -0.6, 1.2, 1.2), 0.4, 2.6, PEARL, st.trimDark);
			}
			break;
		}
		case SHARDHIDE: { // a bear-claw trophy hung from the belt at the left hip (the pouch is at the right)
			double cx = hw * 0.48;
			for (int k = -1; k <= 1; k++) {
				double[] p = project(m, F, cx + k * 2.2, top + 9 + Math.abs(k));
				fr.tube(add(p, new double[] { 0, -2, -1 }), add(p, new double[] { k * 0.5, 4, -2.5 }), 1.1, 0, 4, 0.78, BONE_PALE, BONE_PALE);
			}
			decal(fr, m, F, rect(cx - 3.8, top + 4.5, cx + 3.8, top + 8), 0.5, 2.0, st.leather, st.leatherDark);
			break;
		}
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
		if (st.motif == Motif.SALTGILT) { // the Maiden's anchor in gold, a coil of rope round it, barnacles in the corner
			anchor(add, m, f, cu, cv + s * 0.04, s * 1.55, st.trim, st.trimDark);
			for (int k = 0; k < 4; k++) {
				double[] p = project(m, f, cu + w * (0.66 + (k % 2) * 0.16), cv - hgt * (0.6 + (k / 2) * 0.17));
				barnacle(add, p, f.n, k % 2 == 0 ? 2.2 : 1.6);
			}
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
			case ASHEN: // road-worn wrappings
				LowArmour.cuff(add, m, side, top, top + 2.5, 0.6, st.trim, st.trimDark);
				LowArmour.cuff(add, m, side, top + 5, top + 7, 0.6, st.trimDark, st.trimDark);
				break;
			case THORN: { // bramble round the ankle, under the greave, thorns out; two more thorns up the greave's face
				double ground = bb[4], y = ground - 17;
				double[] s = limbSlab(m, side, y - 1.5, y + 1.5);
				ring(add, new double[] { s[0], y, s[1] }, UP, X, s[2] * 1.12, s[3] * 1.12, -1.2, 1.2, 6, st.trim);
				for (int k = 0; k < 4; k++) {
					double a = Math.PI * 2 * k / 4 + 0.5;
					double[] o = { Math.cos(a), 0, Math.sin(a) }, r = { s[0] + o[0] * s[2] * 1.1, y, s[1] + o[2] * s[3] * 1.1 };
					add.tube(r, add(r, new double[] { o[0] * 3.5, -1.5, o[2] * 3.5 }), 1.0, 0, 3, 0, k % 2 == 0 ? THORN : THORN_DARK, THORN);
				}
				double gx = BaseWrap.layer(m, BaseWrap.PRIO_GREAVE, side, top + 12).cx;
				for (int k = 0; k < 2; k++) {
					thornAt(add, m, ArmourSets.FRONT, gx + (k == 0 ? -1 : 1) * side * 1.5, top + 11 + k * 9, 3.6, k == 0 ? THORN : THORN_DARK);
				}
				break;
			}
			case SALTGILT: // folded sea-boot tops and a gold buckle
				LowArmour.cuff(add, m, side, top, top + 4, 1.0, st.dark, st.leatherDark);
				double[] q = surface(m, new double[] { x, top + 9, -100 }, Z);
				double bz = q == null ? front + 4.5 : q[2];
				add.tube(new double[] { x, top + 9, bz + 1 }, new double[] { x, top + 9, bz - 1.5 }, 1.8, 1.8, 4, 0.78, st.trim, st.trimDark);
				break;
			case TIDE: // a sea-green cuff with a pearl
				LowArmour.cuff(add, m, side, top, top + 2.5, 0.6, st.trim, st.trimDark);
				double[] s = limbSlab(m, side, top, top + 3);
				ball(add, new double[] { s[0], top + 1.5, s[1] - s[3] - 1.2 }, 1.7, PEARL);
				break;
			case SHARDHIDE: { // fur round the greaves' tops, claws over the bare toes
				LowArmour.cuff(add, m, side, top, top + 4.5, 1.2, st.trim, st.trimDark);
				double ground = bb[4];
				double fx = limbSlab(m, side, ground - 7, ground - 2)[0];
				for (int k = -1; k <= 1; k++) {
					add.tube(new double[] { fx + k * 2.2, ground - 8, front + 5 }, new double[] { fx + k * 2.6, ground - 4.5, front - 2 }, 1.1, 0, 4, 0.78, BONE_PALE,
							BONE_PALE);
				}
				break;
			}
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
			case ASHEN: // a bracelet of prayer beads round the wrist
				for (int k = 0; k < 6; k++) {
					double a = Math.PI * 2 * k / 6;
					ball(add, new double[] { x + Math.cos(a) * 5.6, top + 8, 1 + Math.sin(a) * 5.6 }, 1.5, 3, k == 0 ? BRASS : st.accent);
				}
				break;
			case THORN: { // a bramble band round the bracer under the elbow, thorns and a leaf tucked in
				double y = top + 5;
				double[] s = limbSlab(m, side, y - 1, y + 3.5);
				double r = Math.max(s[2], s[3]) + 0.7, bx = s[0], bz = s[1];
				add.tube(new double[] { bx, y, bz }, new double[] { bx, y + 2.5, bz }, r, r, 6, 0.26, st.trim, st.trimDark);
				for (int k = 0; k < 3; k++) {
					double a = Math.PI * 2 * k / 3 + 0.4;
					add.tube(new double[] { bx + Math.cos(a) * (r - 0.6), y + 1, bz + Math.sin(a) * (r - 0.6) },
							new double[] { bx + Math.cos(a) * (r + 3), y - 0.5, bz + Math.sin(a) * (r + 3) }, 0.9, 0, 3, 0, THORN, THORN);
				}
				blade(add, new double[] { bx + side * (r - 1.5), y + 1, bz }, new double[] { bx + side * (r + 1.5), y - 5, bz + 3 }, Z, 2.0, 0.4, LEAF);
				break;
			}
			case SALTGILT:
				LowArmour.gauntlet(add, x, top, 6.6, st.leather, st.leatherDark, st.trim);
				break;
			case TIDE: // a coral bracelet with a pearl
				add.tube(new double[] { x, top + 7, 1 }, new double[] { x, top + 9.5, 1 }, 6.2, 6.2, 6, 0.26, CORAL, CORAL_DARK);
				ball(add, new double[] { x, top + 8.2, -5.8 }, 1.3, PEARL);
				break;
			case SHARDHIDE: { // fur round the bracers' tops, a shard on the back of each wrist
				double[] s = limbSlab(m, side, top + 2, top + 6);
				double r = Math.max(s[2], s[3]) + 0.8;
				add.tube(new double[] { s[0], top + 1.5, s[1] }, new double[] { s[0], top + 5.5, s[1] }, r, r - 0.6, 6, 0.26, st.trim, st.trimDark);
				double wy = bb[4] - 13;
				double[] w = limbSlab(m, side, wy - 1.5, wy + 1.5);
				add.tube(new double[] { w[0] + side * (w[2] - 0.5), wy, w[1] }, new double[] { w[0] + side * (w[2] + 4), wy - 6, w[1] + 1 }, 1.5, 0, 4, 0.4,
						st.accent, st.accent);
				break;
			}
			default:
				break;
			}
		}
		return add;
	}

	/* ------------------------------------------------------------- the weapons */

	/** The pilgrims' staff: pale ashwood with a crook at the top, a brass censer swinging from it, prayer beads looped below. */
	static ModelData ashwoodStaff() {
		ModelData m = new ModelData();
		int ash = hsl(6, 1, 62), ashDark = hsl(6, 1, 44), soot = hsl(6, 1, 26);
		tube(m, f(4, 0, 0), f(70, 1.0, 0), 3.3, 3.6, 5, 0, ash, ash);
		tube(m, f(70, 1.0, 0), f(140, -0.8, 0), 3.6, 3.4, 5, 0.4, ashDark, ashDark);
		tube(m, f(140, -0.8, 0), f(204, 0.4, 0), 3.4, 3.7, 5, 0.1, ash, ash);
		for (double s : new double[] { 60, 166 }) { // knots, smudged with shrine ash
			band(m, s, s + 3, 4.0, soot);
		}
		double cw = 15.5; // the crook's end, where the censer hangs
		double[][] crook = { { 204, 0.4 }, { 215, 1.3 }, { 223, 5 }, { 226.5, 10.5 }, { 223.5, 15 }, { 217, cw } };
		for (int i = 0; i + 1 < crook.length; i++) {
			tube(m, f(crook[i][0], crook[i][1], 0), f(crook[i + 1][0], crook[i + 1][1], 0), 3.9 - i * 0.35, 3.9 - (i + 1) * 0.35, 4, Math.PI / 4,
					i % 2 == 0 ? ashDark : ash, ash);
		}
		collar(m, 0, 8, 2.6, 4.0, BRASS_DARK);
		band(m, 112, 130, 4.1, LEATHER);
		// the censer: a short chain from the crook's end, a brass pot with a domed lid, the ember glowing in its slots
		tube(m, f(217, cw, 0), f(210, cw, 0), 0.7, 0.7, 3, 0, BRASS_DARK, BRASS_DARK);
		tube(m, f(210, cw, 0), f(205, cw, 0), 1.2, 5.6, 6, 0, BRASS_LIGHT, BRASS);
		tube(m, f(205, cw, 0), f(194, cw, 0), 5.6, 5.0, 6, 0, BRASS, BRASS_DARK);
		tube(m, f(194, cw, 0), f(190, cw, 0), 5.0, 0, 6, 0, BRASS_DARK, BRASS_DARK);
		for (int k = 0; k < 3; k++) {
			double a = Math.PI * 2 * k / 3 + 0.3;
			tube(m, f(199.5, cw + Math.cos(a) * 4.8, Math.sin(a) * 4.8), f(199.5, cw + Math.cos(a) * 6.1, Math.sin(a) * 6.1), 1.7, 1.7, 4, 0, EMBER_GLOW, EMBER_GLOW);
		}
		// prayer beads, looped over the staff under the crook and hanging on the other side, a brass bead at the bottom
		double[][] beads = { { 199, -4.2 }, { 195.5, -6.0 }, { 191.5, -7.0 }, { 187.5, -6.6 }, { 184, -5.0 } };
		for (int k = 0; k < beads.length; k++) {
			wball(m, beads[k][0], beads[k][1], 0, 1.5, k == 2 ? BRASS : BEAD);
		}
		collar(m, 199, 202, 3.9, 3.9, BEAD_DARK);
		tube(m, f(184, -5.0, 0), f(178, -5.5, 0), 1.3, 0.4, 3, 0, BEAD_DARK, BEAD_DARK);
		return m;
	}

	/** The Thornwardens' bow: bramble-wood with its thorns left on, a moss-green grip, berries and a leaf tied at it. */
	static ModelData thornwoodBow(double bend) {
		ModelData m = new ModelData();
		int wood = hsl(6, 3, 30), woodDark = hsl(6, 3, 21), grip = hsl(16, 3, 40), string = hsl(7, 2, 74);
		bow(m, 127, bend, new double[] { 1.5, 2.1, 2.9, 3.4, 3.8, 3.4, 2.9, 2.1, 1.5 }, wood, woodDark, grip, THORN, string);
		for (double s : new double[] { 18, 30, 42, 85, 97, 109 }) { // thorns down the back of both limbs
			double w = limbW(s, 127, bend);
			tube(m, f(s, w + 1.5, 0), f(s + (s < 64 ? -2 : 2), w + 5.5, 0), 1.1, 0, 3, 0.3, s % 2 == 0 ? THORN : THORN_DARK, THORN);
		}
		double gw = limbW(63.5, 127, bend);
		wball(m, 61, gw + 4.6, 0.6, 1.6, BERRY);
		wball(m, 64, gw + 5.0, -0.9, 1.6, BERRY_LIGHT);
		wball(m, 66.5, gw + 4.4, 0.5, 1.4, BERRY);
		tube(m, f(60, gw + 3.4, 0), f(50, gw + 7.5, 1.0), 2.4, 0, 3, 0.3, LEAF, LEAF);
		tube(m, f(67, gw + 3.4, 0), f(77, gw + 6.5, -1.0), 2.2, 0, 3, 0.3, LEAF_DARK, LEAF_DARK);
		return m;
	}

	/** A Drowned Maiden officer's cutlass: a broad sea-dulled blade, a gilt shell guard, a doubloon for a pommel, barnacles on the guard. */
	static ModelData saltgiltCutlass() {
		ModelData m = new ModelData();
		int steel = hsl(30, 1, 72), steelDark = hsl(30, 1, 52), edge = hsl(30, 1, 104), grip = hsl(36, 2, 22), wire = GOLD;
		// the doubloon pommel: a coin on edge across the grip's end
		tube(m, f(1, 0, -1.0), f(1, 0, 1.0), 3.4, 3.4, 6, 0, GOLD_DARK, hsl(8, 5, 74));
		tube(m, f(3, 0, 0), f(28, 0, 0), 1.7, 1.9, 4, Math.PI / 4, grip, grip);
		for (double s : new double[] { 9, 16, 23 }) {
			band(m, s, s + 1.0, 2.1, wire);
		}
		// the shell guard: a gilt plate on the knuckle side from the cross-guard back to the pommel, and a short quillon
		plateSW(m, new double[][] { { 30.5, -2 }, { 30.5, 9 }, { 25, 11.5 }, { 15, 12 }, { 6, 9.5 }, { 3, 3 }, { 9, 4.5 }, { 26, 3 } },
				new double[] { 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9 }, new double[] { 18, 7.5 }, 0.9, GOLD, GOLD, GOLD_DARK);
		tube(m, f(29.5, 0, 0), f(28, -6.5, 0), 1.6, 0.9, 4, Math.PI / 4, GOLD, GOLD_DARK);
		collar(m, 28.5, 31.5, 2.3, 2.1, GOLD_DARK);
		wball(m, 20, 11.6, 1.4, 1.5, BARNACLE);
		wball(m, 23.5, 10.8, -1.3, 1.2, BARNACLE_DARK);
		// the blade: broad at the belly, a clipped back near the point
		plateSW(m, new double[][] { { 31, -1.8 }, { 52, -2.4 }, { 74, -3.8 }, { 74, 7.6 }, { 52, 8.2 }, { 32, 4.6 } }, new double[] { 1.1, 1.1, 1.0, 0, 0, 0 },
				new double[] { 52, 2.4 }, 1.0, steel, edge, steelDark);
		plateSW(m, new double[][] { { 72, -3.6 }, { 86, -6.6 }, { 101, -11.5 }, { 96, -3.5 }, { 86, 3.4 }, { 72, 7.8 } }, new double[] { 1.0, 0.9, 0, 0, 0, 0 },
				new double[] { 84, 0 }, 0.9, steel, edge, steelDark);
		// a gold line inlaid along the spine
		tube(m, f(34, -1.4, 0), f(70, -3.2, 0), 0.6, 0.5, 3, 0, GOLD, GOLD);
		return m;
	}

	/** The Tidecallers' wand: a sea-teal grip, and a branch of coral that forks at the top, a pearl caught at its heart. */
	static ModelData coralWand() {
		ModelData m = new ModelData();
		int teal = hsl(31, 4, 40), tealDark = hsl(31, 4, 26), silver = hsl(36, 0, 96);
		tube(m, f(0, 0, 0), f(5, 0, 0), 1.2, 2.6, 5, 0.3, tealDark, tealDark);
		tube(m, f(4, 0, 0), f(46, 0, 0), 2.1, 1.8, 5, 0, teal, teal);
		band(m, 12, 30, 2.5, tealDark);
		for (double s : new double[] { 15, 20, 25 }) {
			collar(m, s, s + 0.9, 2.6, 2.6, silver);
		}
		collar(m, 44, 48, 2.0, 2.6, silver);
		// the coral: a trunk and two forks, each forking again, coral-pink with darker tips
		double[] t0 = { 47, 0, 0 }, t1 = { 64, 0.5, 0 };
		tube(m, f(t0[0], t0[1], 0), f(t1[0], t1[1], 0), 2.6, 2.1, 5, 0.2, CORAL, CORAL_DARK);
		double[][] forks = { { 74, 5.5, 1 }, { 76, -4.5, -1 } };
		for (double[] fk : forks) {
			tube(m, f(t1[0], t1[1], 0), f(fk[0], fk[1], fk[2]), 2.0, 1.5, 4, 0.4, CORAL, CORAL_DARK);
			double sw = Math.signum(fk[1]);
			tube(m, f(fk[0], fk[1], fk[2]), f(fk[0] + 9, fk[1] + sw * 4, fk[2]), 1.5, 0, 4, 0.4, CORAL, CORAL);
			tube(m, f(fk[0], fk[1], fk[2]), f(fk[0] + 11, fk[1] - sw * 2.5, -fk[2] * 2), 1.4, 0, 4, 0.4, CORAL_DARK, CORAL);
			tube(m, f(fk[0] - 4, fk[1] * 0.6, fk[2]), f(fk[0] - 1, fk[1] + sw * 5, fk[2] * 2), 1.0, 0, 4, 0.4, CORAL_DARK, CORAL_DARK);
		}
		// the pearl, held in the fork
		wball(m, 72, 0.4, 0, 3.4, PEARL);
		wball(m, 72, 0.4, 0, 2.0, hsl(36, 0, 124));
		return m;
	}

	/** The Shardhide bow: a long bow of dark wood, its grip wrapped in Shardback fur, the star's glass set down its back and at its tips. */
	static ModelData shardglassBow(double bend) {
		ModelData m = new ModelData();
		int wood = hsl(5, 2, 30), woodDark = hsl(5, 2, 20), string = hsl(8, 1, 80);
		bow(m, 185, bend, new double[] { 1.7, 2.4, 3.2, 3.8, 4.1, 3.8, 3.2, 2.4, 1.7 }, wood, woodDark, FUR, GLASS_LIGHT, string);
		for (double s : new double[] { 30, 46, 62, 123, 139, 155 }) { // shards along the back of the limbs, leaning out to the tips
			double w = limbW(s, 185, bend), lean = s < 92.5 ? -1 : 1;
			int big = s == 46 || s == 139 ? 1 : 0;
			tube(m, f(s, w + 1.5, 0), f(s + lean * (3 + big * 2), w + 7 + big * 4, 0), 2.0 + big * 0.4, 0, 4, 0.4, big == 1 ? GLASS_LIGHT : GLASS, GLASS);
		}
		double gw = limbW(92.5, 185, bend);
		tube(m, f(92.5, gw + 2, 0), f(92.5, gw + 13, 0), 3.0, 0, 4, 0.4, GLASS_LIGHT, GLASS);
		tube(m, f(88, gw + 2, 0), f(85, gw + 8, 0.5), 2.0, 0, 4, 1.1, GLASS_DARK, GLASS);
		tube(m, f(97, gw + 2, 0), f(100, gw + 8, -0.5), 2.0, 0, 4, 1.1, GLASS, GLASS);
		for (double s : new double[] { 80, 105 }) { // dark fur ruffs where the limbs meet the grip
			double w = limbW(s, 185, bend);
			tube(m, f(s - 2.5, w, 0), f(s + 2.5, w, 0), 4.8, 4.8, 5, 0.3, FUR_DARK, FUR_DARK);
		}
		return m;
	}

	/** Where a {@link LadderWeapons#bow}'s limb is (its w) at s, for a bow of length L and the given bend. */
	private static double limbW(double s, double L, double bend) {
		double k = L / 127;
		double[][] limb = { { 3, 4 }, { 8, -0.5 }, { 24, 7 }, { 44, 9.5 }, { 64, 6 }, { 84, 9.5 }, { 104, 7 }, { 119, -0.5 }, { 124, 4 } };
		for (int i = 0; i + 1 < limb.length; i++) {
			double s0 = limb[i][0] * k, s1 = limb[i + 1][0] * k;
			if (s >= s0 && s <= s1) {
				double w0 = (limb[i][1] < 0 ? limb[i][1] : limb[i][1] * bend) * Math.sqrt(k), w1 = (limb[i + 1][1] < 0 ? limb[i + 1][1] : limb[i + 1][1] * bend) * Math.sqrt(k);
				return w0 + (w1 - w0) * (s - s0) / (s1 - s0);
			}
		}
		return 0;
	}

	/** A bead in the weapon frame, centred on (s, w, t). */
	private static void wball(ModelData m, double s, double w, double t, double r, int col) {
		tube(m, f(s - r, w, t), f(s, w, t), 0, r, 4, Math.PI / 4, col, col);
		tube(m, f(s, w, t), f(s + r, w, t), r, 0, 4, Math.PI / 4, col, col);
	}

	/* ------------------------------------------------------------- shapes */

	private static double[] add(double[] a, double[] b) {
		return ArmourForge.add(a, b);
	}

	/** Half the width of a mesh's vertices between y0 and y1. */
	private static double halfWidth(ModelData m, double y0, double y1) {
		double w = 0;
		for (int[] v : m.vertices) {
			if (v[1] >= y0 && v[1] <= y1) {
				w = Math.max(w, Math.abs(v[0]));
			}
		}
		return w;
	}

	/** Centre and half-extents {cx, cz, rx, rz} of the vertices between y0 and y1. */
	private static double[] slab(ModelData m, double y0, double y1) {
		double x0 = 1e9, x1 = -1e9, z0 = 1e9, z1 = -1e9;
		for (int[] v : m.vertices) {
			if (v[1] >= y0 && v[1] <= y1) {
				x0 = Math.min(x0, v[0]);
				x1 = Math.max(x1, v[0]);
				z0 = Math.min(z0, v[2]);
				z1 = Math.max(z1, v[2]);
			}
		}
		if (x0 > x1) {
			int[] b = m.bounds();
			return new double[] { 0, (b[2] + b[5]) / 2.0, b[3], (b[5] - b[2]) / 2.0 };
		}
		return new double[] { (x0 + x1) / 2, (z0 + z1) / 2, (x1 - x0) / 2, (z1 - z0) / 2 };
	}

	/** A ring round one leg of a wrap's leggings (the base's leggings layer) at y: {cx, cz, rx, rz}, {@code grow} proud of them. */
	private static double[] legRing(ModelData m, int side, double y, double grow) {
		BaseWrap.Section s = BaseWrap.layer(m, BaseWrap.PRIO_LEGGINGS, side, y);
		return new double[] { s.cx, s.cz, (s.r(0) + s.r(Math.PI)) / 2 + grow, (s.r(Math.PI / 2) + s.r(-Math.PI / 2)) / 2 + grow };
	}

	/** As {@link #slab}, for one leg (side -1 or 1). */
	private static double[] limbSlab(ModelData m, int side, double y0, double y1) {
		double x0 = 1e9, x1 = -1e9, z0 = 1e9, z1 = -1e9;
		for (int[] v : m.vertices) {
			if (v[1] >= y0 && v[1] <= y1 && v[0] * side > 0) {
				x0 = Math.min(x0, v[0]);
				x1 = Math.max(x1, v[0]);
				z0 = Math.min(z0, v[2]);
				z1 = Math.max(z1, v[2]);
			}
		}
		if (x0 > x1) {
			return new double[] { side * 9, 0, 6, 6 };
		}
		return new double[] { (x0 + x1) / 2, (z0 + z1) / 2, (x1 - x0) / 2, (z1 - z0) / 2 };
	}

	/** A small round bead (two four-sided cones base to base). */
	private static void ball(ModelData add, double[] c, double r, int col) {
		ball(add, c, r, 4, col);
	}

	/** A bead of two {@code sides}-sided cones base to base (three sides for the smallest). */
	private static void ball(ModelData add, double[] c, double r, int sides, int col) {
		add.tube(new double[] { c[0], c[1] - r, c[2] }, c, 0, r, sides, Math.PI / 4, col, col);
		add.tube(c, new double[] { c[0], c[1] + r, c[2] }, r, 0, sides, Math.PI / 4, col, col);
	}

	/**
	 * A hat brim: a flat ring from the crown (rxIn, rzIn) out to (rxOut, rzOut), its outer edge dropping by {@code droop}
	 * (negative: turned up), {@code th} thick, with a top and an underside.
	 */
	private static void brim(ModelData add, double cx, double y, double cz, double rxIn, double rzIn, double rxOut, double rzOut, double droop, double th,
			int sides, int top, int under) {
		int[] in = new int[sides], out = new int[sides], inB = new int[sides], outB = new int[sides];
		for (int s = 0; s < sides; s++) {
			double a = Math.PI * 2 * s / sides + Math.PI / sides, ca = Math.cos(a), sa = Math.sin(a);
			in[s] = add.vertex(cx + ca * rxIn, y, cz + sa * rzIn);
			out[s] = add.vertex(cx + ca * rxOut, y + droop, cz + sa * rzOut);
			inB[s] = add.vertex(cx + ca * rxIn, y + th, cz + sa * rzIn);
			outB[s] = add.vertex(cx + ca * rxOut, y + droop + th, cz + sa * rzOut);
		}
		double[] below = { cx, y + 60, cz }, above = { cx, y - 60, cz }, mid = { cx, y + droop / 2, cz };
		for (int s = 0; s < sides; s++) {
			int t = (s + 1) % sides;
			add.quadOut(in[s], in[t], out[t], out[s], top, below);
			add.quadOut(inB[s], inB[t], outB[t], outB[s], under, above);
			add.quadOut(out[s], out[t], outB[t], outB[s], under, mid);
		}
	}

	/** A tricorn's brim: turned up between three points (front, and back at either side), gold-laced at the edge. */
	private static void tricorn(ModelData add, double cx, double y, double cz, double rxIn, double rzIn, double rxOut, double rzOut, int felt, int lace) {
		int n = 12;
		int[] in = new int[n], out = new int[n], edge = new int[n], inB = new int[n];
		for (int s = 0; s < n; s++) {
			double a = Math.PI * 2 * s / n - Math.PI / 2, ca = Math.cos(a), sa = Math.sin(a); // s = 0 at the front (-z)
			double corner = Math.pow(Math.max(0, Math.cos(3 * (a + Math.PI / 2))), 2); // 1 at the three points
			double r = 0.8 + 0.2 * corner, rise = 5.5 * (1 - corner) + 1;
			in[s] = add.vertex(cx + ca * rxIn, y, cz + sa * rzIn);
			inB[s] = add.vertex(cx + ca * rxIn, y + 1.2, cz + sa * rzIn);
			out[s] = add.vertex(cx + ca * rxOut * r, y - rise, cz + sa * rzOut * r);
			edge[s] = add.vertex(cx + ca * (rxOut * r + 0.8), y - rise - 1.6, cz + sa * (rzOut * r + 0.8));
		}
		double[] c = { cx, y, cz };
		for (int s = 0; s < n; s++) {
			int t = (s + 1) % n;
			add.quadOut(in[s], in[t], out[t], out[s], felt, c); // the inside face, towards the crown
			add.quadOut(inB[s], inB[t], out[t], out[s], felt, new double[] { cx, y - 40, cz }); // the outside, seen from below and around
			add.quadOut(out[s], out[t], edge[t], edge[s], lace, c);
			add.quadOut(out[s], out[t], edge[t], edge[s], lace, new double[] { cx, y + 40, cz });
		}
	}

	/** A barnacle: a squat pale cone standing on the surface at p, along n. */
	private static void barnacle(ModelData add, double[] p, double[] n, double r) {
		double[] d = norm(n);
		add.tube(add(p, scale(d, -0.6)), add(p, scale(d, r * 1.1)), r, r * 0.45, 4, 0.3, BARNACLE, BARNACLE_DARK);
	}

	/** A thorn standing out of the surface at frame point (x, y). */
	private static void thornAt(ModelData add, ModelData m, Frame f, double x, double y, double len, int col) {
		double[] p = project(m, f, x, y);
		double[] o = norm(add(f.n, new double[] { x > 0 ? 0.5 : -0.5, -0.3, 0 }));
		add.tube(add(p, scale(f.n, -0.5)), add(p, scale(o, len)), 0.9, 0, 3, 0, col, col);
	}

	/** A branch of coral from p along d: a tapering limb that forks twice. */
	private static void coral(ModelData add, double[] p, double[] d, double len, double r, int depth, int col, int col2) {
		double[] e = add(p, scale(d, len));
		if (depth == 0) {
			add.tube(p, e, r, 0, 4, 0.4, col, col);
			return;
		}
		add.tube(p, e, r, r * 0.75, 4, 0.4, col, col2);
		double[] side = norm(ModelData.cross(d, Math.abs(d[1]) > 0.9 ? X : Y));
		for (int k = -1; k <= 1; k += 2) {
			double[] nd = norm(add(d, scale(side, k * 0.75)));
			coral(add, e, nd, len * 0.72, r * 0.75, depth - 1, k < 0 ? col2 : col, col);
		}
	}

	/** A regular n-gon (radii rx, ry) as an outline. */
	private static double[][] polygon(double x, double y, double rx, double ry, int n) {
		return MidArmour.polygon(x, y, rx, ry, n);
	}

	/** A scallop shell: a fan of ribs from the hinge at the bottom, alternate shades, two little ears. */
	private static void shell(ModelData add, ModelData m, Frame f, double x, double y, double size, int col, int col2) {
		double r = size * 0.55, hy = y + size * 0.4;
		int n = 5;
		for (int k = 0; k < n; k++) {
			double a0 = Math.PI * (1.12 + 0.76 * k / n), a1 = Math.PI * (1.12 + 0.76 * (k + 1) / n);
			decal(add, m, f, new double[][] { { x, hy }, { x + Math.cos(a0) * r * 1.35, hy + Math.sin(a0) * r * 1.35 }, { x + Math.cos(a1) * r * 1.35,
					hy + Math.sin(a1) * r * 1.35 } }, 0.4, k % 2 == 0 ? 2.4 : 2.0, k % 2 == 0 ? col : col2, col2);
		}
		decal(add, m, f, rect(x - r * 0.4, hy - r * 0.18, x + r * 0.4, hy + r * 0.12), 0.4, 2.6, col2, col2);
	}

	/** A ship's anchor: ring, stock, shank, curved arms and flukes. */
	private static void anchor(ModelData add, ModelData m, Frame f, double x, double y, double size, int col, int col2) {
		double s = size, w = Math.max(0.6, s * 0.05);
		decal(add, m, f, diamond(x, y - s * 0.42, s * 0.08, s * 0.08), 0.4, 2.4, col, col2);
		decal(add, m, f, rect(x - s * 0.22, y - s * 0.33, x + s * 0.22, y - s * 0.27), 0.4, 2.6, col, col2);
		decal(add, m, f, rect(x - w, y - s * 0.36, x + w, y + s * 0.4), 0.4, 2.2, col, col2);
		for (int k = -1; k <= 1; k += 2) {
			decal(add, m, f, LowArmour.bar(x + k * s * 0.33, y + s * 0.1, x + k * s * 0.22, y + s * 0.34, w), 0.4, 2.2, col, col2);
			decal(add, m, f, LowArmour.bar(x + k * s * 0.23, y + s * 0.33, x, y + s * 0.42, w), 0.4, 2.2, col, col2);
			decal(add, m, f, new double[][] { { x + k * s * 0.33, y + s * 0.0 }, { x + k * s * 0.42, y + s * 0.2 }, { x + k * s * 0.25, y + s * 0.16 } }, 0.4, 2.4,
					col, col2);
		}
	}

	/** A breaking wave: a crest leaning forward over its own trough, curling at the top. */
	private static void waveCrest(ModelData add, ModelData m, Frame f, double x, double y, double w, double h, int col, int col2) {
		decal(add, m, f, new double[][] { { x, y }, { x + w * 0.55, y - h * 0.8 }, { x + w * 0.9, y - h }, { x + w * 1.08, y - h * 0.68 }, { x + w, y } }, 0.4, 2.0,
				col, col2);
	}
}
