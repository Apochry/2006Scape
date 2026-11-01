package com.rs2.tools.mapgen;

import java.io.IOException;

import com.rs2.tools.mapgen.ArmourSets.Style;

/** The wrap base (Kind.WRAP): a light skirmisher's wraps, built from scratch on the leather's rig. See NewBases. */
final class BaseWrap {

	private BaseWrap() {
	}

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). Placeholder: the donor, unchanged. */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		return new ModelData[] { donor, donorSecond };
	}
}
