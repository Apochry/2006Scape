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
 * <li>Ashen pilgrim (Magic 15): The wandering pilgrims who walk Nythera's roads to the old shrines, a walking haystack under a hat (its own base, BasePilgrim): a huge woven conical road-hat, a three-tier straw rain-cape over an ash-grey wrap jacket, a bedroll across the shoulders, red prayer beads, a rope belt with the little brass censer hung from it, loose trousers tied over leg-wraps, straw sandals and hand-wraps.</li>
 * <li>Thornwarden (Ranged 20): Silkwood's edge-keepers, who tend the bramble hedges that keep the spiders in and vanish into them: a ghillie suit of leaf shingles sticking out at angles, a walking shrub with a face (its own base, BaseGhillie): a leaf hood flaring over a shadowed face, a hunched leaf shroud longer at the back over a bramble-wound jerkin, leaf-shingled chaps over bound leggings, wicker-bound boots, bark bracers with thorn spurs; red-berried bramble wound through it all.</li>
 * <li>Saltgilt (Defence 30): The Drowned Maiden's officers (Skullbrine), drowned and come back up still in their brass diving suits (their own base, BaseDiver): a round brass diving helmet with a grilled porthole and a bolted collar, a bulky canvas suit with a gilt breastplate bolted on and a belt of lead weights, baggy canvas legs, massive lead boots, swollen rubber gloves, a round brass hatch for a shield; barnacles and doubloon-gold trim.</li>
 * <li>Tidecaller (Magic 35): Sea-mages of Smugglers' Cove who call the tide for the night crews, dressed as jellyfish (its own base, BaseJellyfish): a pale bell worn high over the head with a frilled margin and a scallop shell; a sea-blue robe under a second bell at the shoulders, trailing tendrils to the calves; finned sleeves; a skirt with a scalloped hem and a fringe of tendrils; shell-toed slippers; webbed fin gloves; coral and pearls. The base carries all of it: no decorations are added.</li>
 * <li>Shardhide (Ranged 40): Made from the Shardback, the crater-rim bear whose back bristles with the fallen star's glass, and worn whole (its own base, BasePelt): the bear's head as a hood, its snout over the brow and its teeth framing the face; a hunched pelt mantle with the forelegs over the shoulders and the paws on the chest, the hide to the knees behind, star-glass jutting along the shoulders and spine; fur-wrapped leggings, paw boots and paw gloves with claws. The base carries all of it: no decorations are added.</li>
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
			new Style("Ashen pilgrim", "ashenpilgrim", Kind.MANTLE, Motif.ASHEN, 15, hsl(6, 1, 62), hsl(6, 1, 44), hsl(6, 1, 80), hsl(5, 2, 40), hsl(5, 2, 28),
					BEAD, EMBER_GLOW, hsl(6, 2, 30), hsl(6, 2, 20), NO_SHIELD,
					new String[] { "Ashen pilgrim hat", "Ashen pilgrim rain-cape top", "Ashen pilgrim trousers", null, "Ashen pilgrim sandals", "Ashen pilgrim hand-wraps" },
					new String[] { "A woven road-hat as wide as a cart. It has kept the rain off every road on Nythera, and the sun off most.",
							"A straw rain-cape over ash-grey robes, a bedroll, red prayer beads and a censer whose ember came from the last shrine. It goes to the next one.",
							"Loose road-trousers tied at the shin over leg-wraps. The pilgrims say the dust is a sort of prayer.", null,
							"Straw sandals, re-plaited at every shrine. The soles are thicker than the pilgrim's patience, just.",
							"Cloth wound round the palms for a long day on the staff. The fingers are left free for counting beads." }),
			new Style("Thornwarden", "thornwarden", Kind.WRAP, Motif.THORN, 20, hsl(16, 3, 40), hsl(16, 3, 28), hsl(16, 2, 56), hsl(6, 3, 30), hsl(6, 3, 20),
					BERRY, BERRY_LIGHT, hsl(7, 3, 34), hsl(7, 3, 22), NO_SHIELD,
					new String[] { "Thornwarden hood", "Thornwarden shroud top", "Thornwarden chaps", null, "Thornwarden wicker boots", "Thornwarden bracers" },
					new String[] { "A hood shingled in hedge leaves, a bramble round the brow. Silkwood's spiders give it a wide berth; so does everyone else.",
							"A shroud of overlapping leaves over a bramble-wound jerkin. Stand still in a hedge and you are the hedge. The berries are for the birds. Mostly.",
							"Leaf-shingled chaps over bound leggings, and bramble at the knee. Whatever crawls out of Silkwood has to get past these first.", null,
							"Boots bound in wicker to the shin. Spiders won't climb what bites back.", "Bark bracers with the thorns left on. Put them on carefully." }),
			new Style("Saltgilt", "saltgilt", Kind.COAT, Motif.SALTGILT, 30, hsl(28, 1, 52), hsl(28, 1, 38), hsl(28, 1, 64), GOLD, GOLD_DARK, BARNACLE,
					hsl(9, 2, 120), hsl(36, 2, 24), hsl(36, 2, 16), HAS_ALL,
					new String[] { "Saltgilt diving full helm", "Saltgilt diving platebody", "Saltgilt diving legs", "Saltgilt hatch shield", "Saltgilt lead boots",
							"Saltgilt diving gloves" },
					new String[] { "A brass diving helmet, barnacled, the glass still fogged. Something inside it is still breathing, slowly.",
							"Waterlogged canvas and a gilt breastplate. Captain Blackbrine liked his officers to shine, even a hundred years under.",
							"Baggy diving canvas, patched at the knee. The Maiden kept her officers in style, if not in air.",
							"A brass hatch off the Drowned Maiden, porthole, wheel and all. It held the sea out for a while.",
							"Lead boots for walking the cove floor. They still pour out a little seawater every morning.",
							"Swollen rubber gloves with brass cuffs, for saluting a captain who isn't there." }),
			new Style("Tidecaller", "tidecaller", Kind.MANTLE, Motif.TIDE, 35, hsl(37, 5, 40), hsl(37, 5, 26), hsl(35, 3, 86), hsl(34, 2, 106), hsl(36, 3, 64),
					PEARL, CORAL, hsl(36, 2, 26), hsl(36, 2, 18), NO_SHIELD,
					new String[] { "Tidecaller bell", "Tidecaller robe top", "Tidecaller robe bottom", null, "Tidecaller slippers", "Tidecaller fin gloves" },
					new String[] { "A jellyfish bell to wear, scallop, pearl and all. The tide comes when it's called; the bell helps.",
							"A sea-blue robe under a second bell, its tendrils trailing behind. The Cove's night crews don't sail without one aboard.",
							"Frilled like a jelly's bell, and fringed to the ankle. Waves break round the hem whether there's water or not.", null,
							"Shell-toed slippers with a pearl on each. Always a little damp.", "Webbed and finned. Swimming is easier; writing letters is not." }),
			new Style("Shardhide", "shardhide", Kind.WRAP, Motif.SHARDHIDE, 40, Shardback.HIDE, Shardback.HIDE_DARK, Shardback.BELLY, FUR, FUR_DARK, GLASS,
					GLASS_LIGHT, hsl(6, 2, 26), hsl(6, 2, 17), NO_SHIELD,
					new String[] { "Shardhide bearhead", "Shardhide pelt", "Shardhide leggings", null, "Shardhide paw boots", "Shardhide paws" },
					new String[] { "The Shardback's own head, teeth and all. Its glass still hums when there's starmetal about.",
							"A whole Shardback, glass and all, its forepaws over your shoulders. It was a good deal angrier wearing it than you are.",
							"Hide leggings wound with Shardback fur. Gatehold's miners won't stand downwind of you.", null,
							"The Shardback's hind paws, more or less. The claws still dig in.", "Its forepaws, claws and a sliver of star-glass. Mind how you scratch." }),
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
			default:
				break;
			}
		}
		switch (st.motif) {
		default:
			break;
		}
		return new ModelData[] { knees[0], knees[1], fr };
	}

	/* ------------------------------------------------------------- the shield */

	/** Nothing: the one shield here, Saltgilt's porthole hatch, is all BaseDiver's. */
	static ModelData shield(Style st, ModelData m, Frame f) {
		return new ModelData();
	}

	/* ------------------------------------------------------------- boots, gloves */

	static ModelData boots(Style st, ModelData m) {
		int[] bb = m.bounds();
		double hw = bb[3], front = bb[2], top = bb[1];
		ModelData add = new ModelData();
		for (int side = -1; side <= 1; side += 2) {
			double x = side * hw * 0.56;
			switch (st.motif) {
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
