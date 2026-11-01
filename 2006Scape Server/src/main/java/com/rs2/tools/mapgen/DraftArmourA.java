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
 * <li>Thornwarden (Ranged 20): Silkwood's edge-keepers, who tend the bramble hedges that keep the spiders in: moss-green leathers wound with thorny bramble, red berries, leaves at the shoulder, a thorn crown on the hood.</li>
 * <li>Saltgilt (Defence 30): The Drowned Maiden's officers (Skullbrine): sea-weathered mail trimmed in tarnished doubloon-gold, barnacles crusted on the shoulders and helm, a tricorn-ish brim or an anchor on the shield, the Maiden's gilt figurehead.</li>
 * <li>Tidecaller (Magic 35): Sea-mages of Smugglers' Cove who call the tide for the night crews: deep sea-blue robes, coral branches, a scallop-shell hat ornament, pearls, a wave pattern at the hem.</li>
 * <li>Shardhide (Ranged 40): Made from the Shardback, the crater-rim bear whose back bristles with the fallen star's glass: brown bear hide, dark fur ruffs, pale glass shards jutting from the shoulders and spine, a bear-skull coif.</li>
 * </ol>
 */
final class DraftArmourA {

	private DraftArmourA() {
	}

	/** Draft pieces have no item ids: 0 marks a piece the set has, -1 one it doesn't. */
	static final int[] HAS_ALL = { 0, 0, 0, 0, 0, 0 }, NO_SHIELD = { 0, 0, 0, -1, 0, 0 };

	static final Style[] SETS = {
			new Style("Ashen pilgrim", "ashenpilgrim", Kind.ROBE, Motif.ASHEN, 15, hsl(6, 1, 60), hsl(6, 1, 42), hsl(6, 1, 80), hsl(2, 4, 40), hsl(2, 4, 28), hsl(4, 6, 74), hsl(6, 5, 100), hsl(6, 2, 30), hsl(6, 2, 20), NO_SHIELD,
					new String[] { "Ashen pilgrim hat", "Ashen pilgrim robe top", "Ashen pilgrim robe bottom", null, "Ashen pilgrim boots", "Ashen pilgrim gloves" },
					new String[] { "TODO", "TODO", "TODO", null, "TODO", "TODO" }),
			new Style("Thornwarden", "thornwarden", Kind.LEATHER, Motif.THORN, 20, hsl(16, 3, 40), hsl(16, 3, 28), hsl(16, 2, 56), hsl(6, 3, 30), hsl(6, 3, 20), hsl(0, 5, 44), hsl(0, 4, 70), hsl(7, 3, 34), hsl(7, 3, 22), NO_SHIELD,
					new String[] { "Thornwarden hood", "Thornwarden jerkin", "Thornwarden chaps", null, "Thornwarden boots", "Thornwarden vambraces" },
					new String[] { "TODO", "TODO", "TODO", null, "TODO", "TODO" }),
			new Style("Saltgilt", "saltgilt", Kind.CHAIN, Motif.SALTGILT, 30, hsl(30, 1, 60), hsl(30, 1, 40), hsl(30, 1, 80), hsl(9, 4, 53), hsl(9, 4, 38), hsl(8, 1, 100), hsl(9, 2, 120), hsl(30, 2, 28), hsl(30, 2, 18), HAS_ALL,
					new String[] { "Saltgilt med helm", "Saltgilt chainbody", "Saltgilt platelegs", "Saltgilt sq shield", "Saltgilt boots", "Saltgilt gloves" },
					new String[] { "TODO", "TODO", "TODO", "TODO", "TODO", "TODO" }),
			new Style("Tidecaller", "tidecaller", Kind.ROBE, Motif.TIDE, 35, hsl(38, 4, 48), hsl(38, 4, 32), hsl(38, 3, 66), hsl(2, 5, 70), hsl(2, 5, 52), hsl(40, 0, 116), hsl(30, 3, 110), hsl(36, 2, 26), hsl(36, 2, 18), NO_SHIELD,
					new String[] { "Tidecaller hat", "Tidecaller robe top", "Tidecaller robe bottom", null, "Tidecaller boots", "Tidecaller gloves" },
					new String[] { "TODO", "TODO", "TODO", null, "TODO", "TODO" }),
			new Style("Shardhide", "shardhide", Kind.HIDE, Motif.SHARDHIDE, 40, hsl(6, 3, 36), hsl(6, 3, 24), hsl(6, 2, 50), hsl(6, 2, 20), hsl(6, 2, 13), hsl(34, 2, 96), hsl(34, 1, 122), hsl(7, 3, 30), hsl(7, 3, 20), NO_SHIELD,
					new String[] { "Shardhide coif", "Shardhide body", "Shardhide chaps", null, "Shardhide boots", "Shardhide vambraces" },
					new String[] { "TODO", "TODO", "TODO", null, "TODO", "TODO" }),
	};

	/** Each set's weapon, in set order (id 0: not in the game). */
	static final Rung[] WEAPONS = {
			new Rung(0, "Ashwood staff", "staff", 400, "TODO", LadderWeapons::pilgrimStaff, STAFF),
			new Rung(0, "Thornwood shortbow", "shortbow", 700, "TODO", () -> LadderWeapons.wolfboneBow(1.0), SHORTBOW),
			new Rung(0, "Saltgilt cutlass", "scimitar", 2500, "TODO", LadderWeapons::smugglerScimitar, SCIMITAR),
			new Rung(0, "Coral wand", "wand", 3000, "TODO", LadderWeapons::silkwoodWand, WAND),
			new Rung(0, "Shardglass longbow", "longbow", 6000, "TODO", () -> LadderWeapons.wardenLongbow(1.0), LONGBOW),
	};

	/** Whether this file builds the set. */
	static boolean owns(Style st) {
		return st.motif == Motif.ASHEN || st.motif == Motif.THORN || st.motif == Motif.SALTGILT || st.motif == Motif.TIDE || st.motif == Motif.SHARDHIDE;
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
