package com.rs2.tools.mapgen;

import com.rs2.tools.mapgen.ArmourForge.Frame;
import com.rs2.tools.mapgen.ArmourSets.Style;
import com.rs2.tools.mapgen.LadderWeapons.Rung;

/**
 * Seventeen draft armour sets: A25-A39, each with a matching weapon, and A40-A41, each with an arsenal of weapons. They are built on the same stock meshes and
 * frames as the middle sets ({@link MidArmour} hands their pieces on to here) and previewed with
 * {@code ArmourPreview} (sets 25-39) and {@code ModelPreview ... D} (their weapons). They are not in
 * {@link ArmourSets#SETS}, have no item ids, and nothing packs them into the cache: putting one in the game means
 * giving it ids in VoidConstants and adding it to SETS, like the middle sets.
 *
 * <p>The sets are split over three files ({@link DraftArmourA} A25-A29, {@link DraftArmourB} A30-A34,
 * {@link DraftArmourC} A35-A39, {@link DraftArmourD} A40-A41); this class only gathers them and hands each piece to the right one.
 */
final class DraftArmour {

	private DraftArmour() {
	}

	/** The first draft set's number (A25). */
	static final int FIRST = 25;

	static final Style[] DRAFT = ArmourSets.concat(ArmourSets.concat(ArmourSets.concat(DraftArmourA.SETS, DraftArmourB.SETS), DraftArmourC.SETS),
			DraftArmourD.SETS);

	/** The single weapons of A25-A39, in set order (A40-A41 have a whole arsenal each: DraftArmourD.ARSENAL). */
	static final Rung[] WEAPONS = concat(concat(DraftArmourA.WEAPONS, DraftArmourB.WEAPONS), DraftArmourC.WEAPONS);

	static Rung[] concat(Rung[] a, Rung[] b) {
		Rung[] r = java.util.Arrays.copyOf(a, a.length + b.length);
		System.arraycopy(b, 0, r, a.length, b.length);
		return r;
	}

	/** The draft set numbered {@code n} (25-41). */
	static Style set(int n) {
		return DRAFT[n - FIRST];
	}

	/** What wearing it takes, from its kind and level (draft pieces have no ids to look a rule up by). */
	static String requirement(Style st) {
		ArmourSets.Kind k = st.kind;
		String skill = k == ArmourSets.Kind.ROBE || k == ArmourSets.Kind.MANTLE ? "Magic"
				: k == ArmourSets.Kind.HIDE || k == ArmourSets.Kind.LEATHER || k == ArmourSets.Kind.WRAP ? "Ranged" : "Defence";
		return skill + " " + st.level;
	}

	/** What wielding set {@code i}'s weapon takes: Ranged for a bow, Magic for a staff or wand, Attack otherwise, at the set's level. */
	static String weaponRequirement(int i) {
		String t = WEAPONS[i].type;
		return (t.contains("bow") ? "Ranged " : t.equals("staff") || t.equals("wand") ? "Magic " : "Attack ") + DRAFT[i].level;
	}

	static int shieldFace(Style st) {
		return DraftArmourA.owns(st) ? DraftArmourA.shieldFace(st) : DraftArmourB.owns(st) ? DraftArmourB.shieldFace(st) : DraftArmourC.owns(st) ? DraftArmourC.shieldFace(st)
				: DraftArmourD.shieldFace(st);
	}

	static int shieldRim(Style st) {
		return DraftArmourA.owns(st) ? DraftArmourA.shieldRim(st) : DraftArmourB.owns(st) ? DraftArmourB.shieldRim(st) : DraftArmourC.owns(st) ? DraftArmourC.shieldRim(st)
				: DraftArmourD.shieldRim(st);
	}

	static ModelData helm(Style st, ModelData m) {
		return DraftArmourA.owns(st) ? DraftArmourA.helm(st, m) : DraftArmourB.owns(st) ? DraftArmourB.helm(st, m) : DraftArmourC.owns(st) ? DraftArmourC.helm(st, m)
				: DraftArmourD.helm(st, m);
	}

	static ModelData body(Style st, ModelData m) {
		return DraftArmourA.owns(st) ? DraftArmourA.body(st, m) : DraftArmourB.owns(st) ? DraftArmourB.body(st, m) : DraftArmourC.owns(st) ? DraftArmourC.body(st, m)
				: DraftArmourD.body(st, m);
	}

	static ModelData[] legs(Style st, ModelData m) {
		return DraftArmourA.owns(st) ? DraftArmourA.legs(st, m) : DraftArmourB.owns(st) ? DraftArmourB.legs(st, m) : DraftArmourC.owns(st) ? DraftArmourC.legs(st, m)
				: DraftArmourD.legs(st, m);
	}

	static ModelData shield(Style st, ModelData m, Frame f) {
		return DraftArmourA.owns(st) ? DraftArmourA.shield(st, m, f) : DraftArmourB.owns(st) ? DraftArmourB.shield(st, m, f) : DraftArmourC.owns(st) ? DraftArmourC.shield(st, m, f)
				: DraftArmourD.shield(st, m, f);
	}

	static ModelData boots(Style st, ModelData m) {
		return DraftArmourA.owns(st) ? DraftArmourA.boots(st, m) : DraftArmourB.owns(st) ? DraftArmourB.boots(st, m) : DraftArmourC.owns(st) ? DraftArmourC.boots(st, m)
				: DraftArmourD.boots(st, m);
	}

	static ModelData gloves(Style st, ModelData m) {
		return DraftArmourA.owns(st) ? DraftArmourA.gloves(st, m) : DraftArmourB.owns(st) ? DraftArmourB.gloves(st, m) : DraftArmourC.owns(st) ? DraftArmourC.gloves(st, m)
				: DraftArmourD.gloves(st, m);
	}
}
