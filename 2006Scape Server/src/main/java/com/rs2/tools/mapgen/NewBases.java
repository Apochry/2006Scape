package com.rs2.tools.mapgen;

import java.io.IOException;

import com.rs2.tools.mapgen.ArmourSets.Style;

/**
 * Three bases built from scratch, so a set doesn't have to be a recoloured stock mesh: the {@link ArmourSets.Kind}s
 * COAT (an armoured long coat: {@link BaseCoat}), MANTLE (a hooded cloak: {@link BaseMantle}) and WRAP (a light
 * skirmisher's wraps: {@link BaseWrap}). Each kind has a stock donor in {@link ArmourSets#BASE} (the rune plate, the
 * mystic robe and the leather), whose worn model arrives here already read: it supplies the rig (every new vertex takes
 * the animation label of the nearest donor or body vertex, as {@link ArmourForge} does for decorations), the item's
 * camera and the inventory fit. What's drawn is the base's own mesh, in the set's colours. The set's motif
 * decorations then go on top as usual.
 */
final class NewBases {

	private NewBases() {
	}

	/**
	 * The worn model(s) for one piece of a generated base: {main, second or null}. {@code donor} / {@code donorSecond}
	 * are the stock donor's worn models for the slot (second: the platebody's arms, the mystic top's torso).
	 */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		switch (st.kind) {
		case COAT:
			return BaseCoat.worn(af, st, slot, female, donor, donorSecond);
		case MANTLE:
			return BaseMantle.worn(af, st, slot, female, donor, donorSecond);
		case WRAP:
			return BaseWrap.worn(af, st, slot, female, donor, donorSecond);
		default:
			return new ModelData[] { donor, donorSecond };
		}
	}
}
