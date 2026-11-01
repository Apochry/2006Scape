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
 * <li>Aurelian kingsguard (Defence 55): King Aurel's household guard, who died holding the gate of his city before he
 * became the Hollow King; their arms turn up in the ruins still polished. On a base of their own (BaseKingsguard): a
 * classic royal guard in polished steel, royal crimson and gold. A rounded close helm with a gold crown of five points
 * (a small sun on its brow) and a crimson plume sweeping back; a deep breastplate with big rounded gold-lipped pauldrons
 * under a crimson tabard, front and back, bearing the king's charge (his crown over the riddle's sun, star and moon);
 * plate legs with tassets and round knee cops; chunky sabatons; gold-cuffed gauntlets; a crimson heater shield in a gold
 * rim with the charge. The base carries it all, so it has no decorations. Its arsenal (ArsenalKingsguard): a longsword
 * with a crowned guard, a halberd, a warhammer with a crown for a face, a rondel dagger, a royal hunting shortbow and
 * the court mage's sceptre.</li>
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
			new Style("Aurelian kingsguard", "kingsguard", Kind.COAT, Motif.KINGSGUARD, 55, hsl(40, 0, 76), hsl(40, 0, 50), hsl(40, 0, 96), hsl(8, 5, 70),
					hsl(8, 5, 46), hsl(0, 6, 38), hsl(9, 3, 112), hsl(0, 6, 24), hsl(0, 3, 14), HAS_ALL,
					new String[] { "Kingsguard full helm", "Kingsguard platebody", "Kingsguard platelegs", "Kingsguard heater shield", "Kingsguard sabatons",
							"Kingsguard gauntlets" },
					new String[] { "King Aurel's crown on a guardsman's helm. The plume has been brushed, and nobody living owns a brush.",
							"The king's crown, sun, star and moon on the tabard. Found in the ruins of the gate, polished as if for inspection.",
							"Plate legs for standing at a gate. The kingsguard stood at it until the gate was all that was left.",
							"Crimson and gold, with the king's charge on it. It held the gate. The gate didn't hold.",
							"Heavy sabatons, made for not taking a single step back.", "Gold-cuffed gauntlets. Someone keeps the gold bright." }),
			// bone (main, dark, light), dark hide (trim, trimDark), the sickly glow in the eye sockets (accent, core), fur (leather, leatherDark)
			new Style("Marrowbound", "marrowbound", Kind.WRAP, Motif.MARROW, 50, ArsenalMarrow.BONE, ArsenalMarrow.BONE_DARK, ArsenalMarrow.BONE_LIGHT,
					ArsenalMarrow.HIDE, ArsenalMarrow.HIDE_DARK, ArsenalMarrow.GLOW, ArsenalMarrow.GLOW_CORE, ArsenalMarrow.FUR, ArsenalMarrow.FUR_DARK, NO_SHIELD,
					new String[] { "Marrowbound mask", "Marrowbound top", "Marrowbound chaps", null, "Marrowbound boots", "Marrowbound vambraces" },
					new String[] { "A barrow ram's skull, worn with the ram's full permission. Something in the eye sockets is still looking.",
							"A ribcage over a hide jerkin. The ribs' first owner hasn't asked for them back yet.",
							"Hide chaps splinted with somebody's shinbones. They rattle a bit going downhill.", null,
							"Hide boots capped in bone, with claws for grip. Mostly for grip.",
							"Knuckle-bones over the knuckles. It seemed only fitting." }), };

	/** Each set's arsenal, in set order (id 0: not in the game). */
	static final Rung[][] ARSENAL = {
			{ new Rung(0, "Kingsguard longsword", "longsword", 32000, "The kingsguard's sword. The crown on the guard was for the king; the edge was for everyone else.",
					ArsenalKingsguard::longsword, LONGSWORD),
					new Rung(0, "Kingsguard halberd", "halberd", 45000, "Long enough to hold a gate with. Very nearly long enough.", ArsenalKingsguard::halberd,
							HALBERD),
					new Rung(0, "Kingsguard warhammer", "warhammer", 38000, "The crown on its face leaves the king's seal on whatever it hits.",
							ArsenalKingsguard::warhammer, WARHAMMER),
					new Rung(0, "Kingsguard rondel dagger", "dagger", 18000, "For the gaps in other people's armour. The kingsguard knew where all of them were.",
							ArsenalKingsguard::rondelDagger, DAGGER),
					new Rung(0, "Royal hunting shortbow", "shortbow", 24000, "The king hunted with the kingsguard. The kingsguard made sure he hit something.",
							() -> ArsenalKingsguard.shortbow(1.0), () -> ArsenalKingsguard.shortbow(2.4), SHORTBOW), // inventory bend 2.4, as the ladder's shortbows
					new Rung(0, "Court sceptre", "wand", 40000, "The court mage's sceptre. Someone has polished the gem very recently.", ArsenalKingsguard::sceptre,
							WAND) },
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
