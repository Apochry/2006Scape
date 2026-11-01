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
 * <li>Herald's (Defence 65): The Hollow Herald's plate: dark iron with ghostly pale trim, a long violet banner/tabard, a herald's horn on the shoulder, a hollow-eyed crowned helm. (Herald's is a possessive: pieces read 'Herald's full helm'.)</li>
 * <li>Emberforged (Defence 70): Dorrin's forge masterwork: blackened steel with glowing ember seams and rivets, bronze edging, an anvil-and-flame crest, a chimney-like crest on the helm venting embers.</li>
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

	static final Style[] SETS = {
			new Style("Herald's", "herald", Kind.PLATE, Motif.HERALD, 65, hsl(0, 0, 30), hsl(0, 0, 18), hsl(0, 0, 46), ArmourSets.violet(70, 5), ArmourSets.violet(50, 5), hsl(23, 4, 92), hsl(23, 2, 118), hsl(48, 2, 20), hsl(48, 2, 13), HAS_ALL,
					new String[] { "Herald's full helm", "Herald's platebody", "Herald's platelegs", "Herald's kiteshield", "Herald's boots", "Herald's gauntlets" },
					new String[] { "TODO", "TODO", "TODO", "TODO", "TODO", "TODO" }),
			new Style("Emberforged", "emberforged", Kind.PLATE, Motif.EMBERFORGED, 70, hsl(4, 1, 34), hsl(4, 1, 22), hsl(4, 1, 50), hsl(6, 5, 58), hsl(6, 5, 40), hsl(4, 6, 74), hsl(6, 5, 100), hsl(5, 3, 22), hsl(5, 3, 14), HAS_ALL,
					new String[] { "Emberforged full helm", "Emberforged platebody", "Emberforged platelegs", "Emberforged kiteshield", "Emberforged boots", "Emberforged gauntlets" },
					new String[] { "TODO", "TODO", "TODO", "TODO", "TODO", "TODO" }),
			new Style("Starless shroud", "shroud", Kind.ROBE, Motif.SHROUD, 70, hsl(44, 2, 14), hsl(44, 2, 8), hsl(44, 2, 24), hsl(42, 0, 96), hsl(42, 0, 72), hsl(42, 0, 122), ArmourSets.violet(100, 6), hsl(44, 1, 12), hsl(44, 1, 8), NO_SHIELD,
					new String[] { "Starless shroud hat", "Starless shroud robe top", "Starless shroud robe bottom", null, "Starless shroud boots", "Starless shroud gloves" },
					new String[] { "TODO", "TODO", "TODO", null, "TODO", "TODO" }),
			new Style("Riftwalker", "riftwalker", Kind.HIDE, Motif.RIFTWALKER, 75, hsl(48, 1, 34), hsl(48, 1, 22), hsl(48, 1, 48), hsl(0, 0, 16), hsl(0, 0, 10), ArmourSets.violet(90, 6), hsl(42, 0, 124), hsl(48, 2, 26), hsl(48, 2, 16), NO_SHIELD,
					new String[] { "Riftwalker coif", "Riftwalker body", "Riftwalker chaps", null, "Riftwalker boots", "Riftwalker vambraces" },
					new String[] { "TODO", "TODO", "TODO", null, "TODO", "TODO" }),
			new Style("Dawnguard", "dawnguard", Kind.PLATE, Motif.DAWNGUARD, 80, hsl(9, 2, 100), hsl(9, 2, 76), hsl(9, 1, 118), hsl(2, 4, 70), hsl(2, 4, 52), hsl(8, 6, 90), hsl(9, 3, 122), hsl(6, 3, 30), hsl(6, 3, 20), HAS_ALL,
					new String[] { "Dawnguard full helm", "Dawnguard platebody", "Dawnguard platelegs", "Dawnguard kiteshield", "Dawnguard boots", "Dawnguard gauntlets" },
					new String[] { "TODO", "TODO", "TODO", "TODO", "TODO", "TODO" }),
	};

	/** Each set's weapon, in set order (id 0: not in the game). */
	static final Rung[] WEAPONS = {
			new Rung(0, "Herald's mace", "mace", 50000, "TODO", LadderWeapons::steelcapMace, MACE),
			new Rung(0, "Emberforged warhammer", "warhammer", 80000, "TODO", LadderWeapons::barrowstoneHammer, WARHAMMER),
			new Rung(0, "Shroud staff", "staff", 90000, "TODO", LadderWeapons::pilgrimStaff, STAFF),
			new Rung(0, "Riftwalker longbow", "longbow", 120000, "TODO", () -> LadderWeapons.wardenLongbow(1.0), LONGBOW),
			new Rung(0, "Dawnguard 2h sword", "2h sword", 200000, "TODO", LadderWeapons::gravewardenSword, TWO_HANDED),
	};

	/** Whether this file builds the set. */
	static boolean owns(Style st) {
		return st.motif == Motif.HERALD || st.motif == Motif.EMBERFORGED || st.motif == Motif.SHROUD || st.motif == Motif.RIFTWALKER || st.motif == Motif.DAWNGUARD;
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
