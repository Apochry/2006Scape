package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.LadderWeapons.*;
import static com.rs2.tools.mapgen.ModelData.hsl;
import static com.rs2.tools.mapgen.QuestWeapons.*;

import com.rs2.tools.mapgen.ArmourForge.Frame;
import com.rs2.tools.mapgen.ArmourSets.Kind;
import com.rs2.tools.mapgen.ArmourSets.Motif;
import com.rs2.tools.mapgen.ArmourSets.Style;
import com.rs2.tools.mapgen.LadderWeapons.Rung;

/**
 * Draft armour sets A40-A41, each with an arsenal of weapons rather than a single one, aiming at the look of Old
 * School RuneScape's own gear: built and previewed only (they are not in ArmourSets.SETS, have no item ids, and
 * nothing packs them into the cache). See DraftArmour. Preview the armour with {@code ArmourPreview ... 40|41} and the
 * arsenals with {@code ModelPreview data/cache out X}.
 *
 * <ol start="40">
 * <li>Aurelian kingsguard (Defence 55): STUB.</li>
 * <li>Marrowbound (Ranged 50): STUB.</li>
 * </ol>
 */
final class DraftArmourD {

	private DraftArmourD() {
	}

	static final int[] HAS_ALL = { 0, 0, 0, 0, 0, 0 }, NO_SHIELD = { 0, 0, 0, -1, 0, 0 };

	static final Style[] SETS = {
			new Style("Aurelian kingsguard", "kingsguard", Kind.COAT, Motif.KINGSGUARD, 55, hsl(0, 0, 70), hsl(0, 0, 50), hsl(0, 0, 90), hsl(8, 5, 70),
					hsl(8, 5, 50), hsl(0, 6, 40), hsl(9, 3, 110), hsl(0, 5, 34), hsl(0, 5, 22), HAS_ALL,
					new String[] { "Kingsguard full helm", "Kingsguard platebody", "Kingsguard platelegs", "Kingsguard kiteshield", "Kingsguard boots",
							"Kingsguard gauntlets" },
					new String[] { "STUB", "STUB", "STUB", "STUB", "STUB", "STUB" }),
			new Style("Marrowbound", "marrowbound", Kind.WRAP, Motif.MARROW, 50, hsl(8, 2, 80), hsl(8, 2, 60), hsl(8, 1, 100), hsl(5, 3, 36),
					hsl(5, 3, 24), hsl(0, 5, 44), hsl(10, 4, 90), hsl(6, 3, 34), hsl(6, 3, 22), NO_SHIELD,
					new String[] { "Marrowbound mask", "Marrowbound top", "Marrowbound chaps", null, "Marrowbound boots", "Marrowbound vambraces" },
					new String[] { "STUB", "STUB", "STUB", null, "STUB", "STUB" }), };

	/** Each set's arsenal, in set order (id 0: not in the game). STUB: one placeholder each. */
	static final Rung[][] ARSENAL = {
			{ new Rung(0, "Kingsguard longsword", "longsword", 30000, "STUB", LadderWeapons::guttingDagger, LONGSWORD) },
			{ new Rung(0, "Marrowbound dagger", "dagger", 20000, "STUB", LadderWeapons::guttingDagger, DAGGER) }, };

	/** Whether this file builds the set. */
	static boolean owns(Style st) {
		return st.motif == Motif.KINGSGUARD || st.motif == Motif.MARROW;
	}

	/** The set an arsenal weapon belongs to, or -1. */
	static int setOf(Rung r) {
		for (int s = 0; s < ARSENAL.length; s++) {
			for (Rung w : ARSENAL[s]) {
				if (w == r) {
					return s;
				}
			}
		}
		return -1;
	}

	/** What wielding an arsenal weapon takes: Ranged for a bow, Magic for a staff or wand, Attack otherwise, at its set's level. */
	static String requirement(Rung r) {
		String t = r.type;
		return (t.contains("bow") ? "Ranged " : t.equals("staff") || t.equals("wand") ? "Magic " : "Attack ") + SETS[setOf(r)].level;
	}

	static int shieldFace(Style st) {
		return st.main;
	}

	static int shieldRim(Style st) {
		return st.dark;
	}

	/* The motif decorations on top of the base (the bases are expected to carry most of the look). */

	static ModelData helm(Style st, ModelData m) {
		return new ModelData();
	}

	static ModelData body(Style st, ModelData m) {
		return new ModelData();
	}

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
