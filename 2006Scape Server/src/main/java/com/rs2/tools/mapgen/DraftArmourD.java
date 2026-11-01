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
 * <li>Marrowbound (Ranged 50): the scavenger-shamans who pick over Nythera's old barrows (the Gravecoil's Sunken Barrow
 * among them) and wear the bones of whatever they find there over dark hide. Its own base ({@link BaseMarrow}): a ram's
 * skull mask, horns curling round the cheeks and a sickly green glow deep in the sockets; a ribcage strapped over a
 * fur-collared hide jerkin, a beast skull on one shoulder and a great shoulder blade on the other, a string of teeth;
 * hide chaps splinted with long bones, kneecaps and a fur loincloth; hide boots with bone toe-caps and claws; hide
 * vambraces with bone strips and knuckle-bones. Three materials (yellowed bone, dark hide, a little fur) and the glow
 * as the one accent. No shield. The arsenal ({@link ArsenalMarrow}): a jawbone axe, a femur club, a rib spear, a fang
 * dagger, the Marrowbound longbow (its signature: two long bones lashed into a vertebra) and a skull totem staff.</li>
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
			// bone (main, dark, light), dark hide (trim, trimDark), the sickly glow in the eye sockets (accent, core), fur (leather, leatherDark)
			new Style("Marrowbound", "marrowbound", Kind.WRAP, Motif.MARROW, 50, ArsenalMarrow.BONE, ArsenalMarrow.BONE_DARK, ArsenalMarrow.BONE_LIGHT,
					ArsenalMarrow.HIDE, ArsenalMarrow.HIDE_DARK, ArsenalMarrow.GLOW, ArsenalMarrow.GLOW_CORE, ArsenalMarrow.FUR, ArsenalMarrow.FUR_DARK, NO_SHIELD,
					new String[] { "Marrowbound mask", "Marrowbound top", "Marrowbound chaps", null, "Marrowbound boots", "Marrowbound vambraces" },
					new String[] { "A barrow ram's skull, worn with the ram's full permission. Something in the eye sockets is still looking.",
							"A ribcage over a hide jerkin. The ribs' first owner hasn't asked for them back yet.",
							"Hide chaps splinted with somebody's shinbones. They rattle a bit going downhill.", null,
							"Hide boots capped in bone, with claws for grip. Mostly for grip.",
							"Knuckle-bones over the knuckles. It seemed only fitting." }), };

	/** Each set's arsenal, in set order (id 0: not in the game). STUB: one placeholder each. */
	static final Rung[][] ARSENAL = {
			{ new Rung(0, "Kingsguard longsword", "longsword", 30000, "STUB", LadderWeapons::guttingDagger, LONGSWORD) },
			{ new Rung(0, "Marrowbound jawbone axe", "battleaxe", 34000, "The teeth still work. They just work for you now.", ArsenalMarrow::jawboneAxe,
					BATTLEAXE),
					new Rung(0, "Femur club", "mace", 16000, "Somebody's thighbone, studded with somebody else's knuckles. A real team effort.",
							ArsenalMarrow::femurClub, MACE),
					new Rung(0, "Rib spear", "spear", 22000, "A rib on a stick. The stick is also a bone.", ArsenalMarrow::ribSpear, SPEAR),
					new Rung(0, "Fang dagger", "dagger", 11000, "One very large tooth. You'd rather not meet the rest of the mouth.", ArsenalMarrow::fangDagger,
							DAGGER),
					new Rung(0, "Marrowbound longbow", "longbow", 42000, "Two long bones lashed at the spine. It creaks like a barrow door, and so do its victims.",
							() -> ArsenalMarrow.longbow(1.0), () -> ArsenalMarrow.longbow(1.3), LONGBOW), // inventory bend 1.3, as the ladder's longbows
					new Rung(0, "Skull totem staff", "staff", 28000, "Its eyes light up whenever you cast. It's just happy to be included.", ArsenalMarrow::skullStaff,
							STAFF) }, };

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
