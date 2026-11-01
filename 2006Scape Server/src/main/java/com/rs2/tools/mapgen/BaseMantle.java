package com.rs2.tools.mapgen;

import java.io.IOException;

import com.rs2.tools.mapgen.ArmourSets.Style;

/** The mantle base (Kind.MANTLE): a hooded cloak, built from scratch on the mystic robe's rig. See NewBases. */
final class BaseMantle {

	private BaseMantle() {
	}

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). Placeholder: the donor, unchanged. */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		return new ModelData[] { donor, donorSecond };
	}
}
