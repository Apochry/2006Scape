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
 * <li>Wildgrove (Magic 40): Druids of the deep woods: leaf-green robes, bark belt, an antler crown on the hat, small golden flowers, ivy trailing down the sleeves.</li>
 * <li>Gravescale (Defence 45): Scale mail from the Gravecoil, the barrow serpent (see Gravecoil.java for its real palette and match it): overlapping serpent scales, a fanged snake-head visor, a coiled serpent on the shield, venom-yellow eyes.</li>
 * <li>Gatehold sentinel (Defence 50): The guard on Gatehold's wall: bright steel plate, a Gatehold-blue tabard with the gate-tower crest in gold, a tall plume, a tower-shaped kiteshield boss.</li>
 * <li>Broodkeeper (Magic 50): The Hollow Matriarch's broodkeepers in Hollowdeep: dark chitin plates over robes, sickly glowing egg-sacs, spider legs arching over the hat, many small eyes.</li>
 * <li>Rimeclaw hunter (Ranged 60): Hunters who took Rimeclaw (the Frostfang boss) and wear its hide: ice-blue hide, a claw necklace, big hooked claws at the shoulders and on the vambraces, a beast-head coif with frost.</li>
 * </ol>
 */
final class DraftArmourB {

	private DraftArmourB() {
	}

	/** Draft pieces have no item ids: 0 marks a piece the set has, -1 one it doesn't. */
	static final int[] HAS_ALL = { 0, 0, 0, 0, 0, 0 }, NO_SHIELD = { 0, 0, 0, -1, 0, 0 };

	static final Style[] SETS = {
			new Style("Wildgrove", "wildgrove", Kind.ROBE, Motif.WILDGROVE, 40, hsl(18, 4, 44), hsl(18, 4, 30), hsl(18, 3, 60), hsl(8, 1, 96), hsl(8, 1, 74), hsl(10, 6, 90), hsl(11, 4, 116), hsl(6, 3, 28), hsl(6, 3, 18), NO_SHIELD,
					new String[] { "Wildgrove hat", "Wildgrove robe top", "Wildgrove robe bottom", null, "Wildgrove boots", "Wildgrove gloves" },
					new String[] { "TODO", "TODO", "TODO", null, "TODO", "TODO" }),
			new Style("Gravescale", "gravescale", Kind.CHAIN, Motif.GRAVESCALE, 45, hsl(14, 3, 40), hsl(14, 3, 26), hsl(14, 2, 58), hsl(8, 1, 92), hsl(8, 1, 70), hsl(12, 6, 80), hsl(12, 4, 110), hsl(6, 2, 24), hsl(6, 2, 16), HAS_ALL,
					new String[] { "Gravescale med helm", "Gravescale chainbody", "Gravescale platelegs", "Gravescale sq shield", "Gravescale boots", "Gravescale gloves" },
					new String[] { "TODO", "TODO", "TODO", "TODO", "TODO", "TODO" }),
			new Style("Gatehold sentinel", "sentinel", Kind.PLATE, Motif.SENTINEL, 50, hsl(36, 1, 64), hsl(36, 1, 44), hsl(36, 1, 84), hsl(40, 5, 40), hsl(40, 5, 28), hsl(8, 6, 84), hsl(9, 2, 120), hsl(40, 3, 24), hsl(40, 3, 16), HAS_ALL,
					new String[] { "Gatehold sentinel full helm", "Gatehold sentinel platebody", "Gatehold sentinel platelegs", "Gatehold sentinel kiteshield", "Gatehold sentinel boots", "Gatehold sentinel gauntlets" },
					new String[] { "TODO", "TODO", "TODO", "TODO", "TODO", "TODO" }),
			new Style("Broodkeeper", "broodkeeper", Kind.ROBE, Motif.BROOD, 50, hsl(50, 2, 30), hsl(50, 2, 19), hsl(50, 2, 44), hsl(46, 3, 50), hsl(46, 3, 34), hsl(14, 5, 80), hsl(14, 3, 110), hsl(50, 1, 22), hsl(50, 1, 14), NO_SHIELD,
					new String[] { "Broodkeeper hat", "Broodkeeper robe top", "Broodkeeper robe bottom", null, "Broodkeeper boots", "Broodkeeper gloves" },
					new String[] { "TODO", "TODO", "TODO", null, "TODO", "TODO" }),
			new Style("Rimeclaw hunter", "rimeclaw", Kind.HIDE, Motif.RIMECLAW, 60, hsl(36, 2, 80), hsl(36, 2, 60), hsl(36, 1, 100), hsl(38, 2, 40), hsl(38, 2, 28), hsl(8, 1, 104), hsl(36, 3, 118), hsl(36, 2, 36), hsl(36, 2, 24), NO_SHIELD,
					new String[] { "Rimeclaw hunter coif", "Rimeclaw hunter body", "Rimeclaw hunter chaps", null, "Rimeclaw hunter boots", "Rimeclaw hunter vambraces" },
					new String[] { "TODO", "TODO", "TODO", null, "TODO", "TODO" }),
	};

	/** Each set's weapon, in set order (id 0: not in the game). */
	static final Rung[] WEAPONS = {
			new Rung(0, "Antler staff", "staff", 4000, "TODO", LadderWeapons::pilgrimStaff, STAFF),
			new Rung(0, "Coilfang spear", "spear", 9000, "TODO", LadderWeapons::militiaSpear, SPEAR),
			new Rung(0, "Sentinel halberd", "halberd", 15000, "TODO", LadderWeapons::wardenHalberd, HALBERD),
			new Rung(0, "Brood wand", "wand", 14000, "TODO", LadderWeapons::silkwoodWand, WAND),
			new Rung(0, "Rimeclaw shortbow", "shortbow", 30000, "TODO", () -> LadderWeapons.wolfboneBow(1.0), SHORTBOW),
	};

	/** Whether this file builds the set. */
	static boolean owns(Style st) {
		return st.motif == Motif.WILDGROVE || st.motif == Motif.GRAVESCALE || st.motif == Motif.SENTINEL || st.motif == Motif.BROOD || st.motif == Motif.RIMECLAW;
	}

	static int shieldFace(Style st) {
		return st.main;
	}

	static int shieldRim(Style st) {
		return st.dark;
	}

	/** What's added to the helm (the base mesh, recoloured, is already there). */
	static ModelData helm(Style st, ModelData m) {
		return new ModelData();
	}

	static ModelData body(Style st, ModelData m) {
		return new ModelData();
	}

	/** Right knee, left knee and the front (any may be empty). */
	static ModelData[] legs(Style st, ModelData m) {
		return new ModelData[] { new ModelData(), new ModelData(), new ModelData() };
	}

	static ModelData shield(Style st, ModelData m, Frame f) {
		return new ModelData();
	}

	static ModelData boots(Style st, ModelData m) {
		return new ModelData();
	}

	static ModelData gloves(Style st, ModelData m) {
		return new ModelData();
	}
}
