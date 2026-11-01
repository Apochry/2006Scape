package com.rs2.tools.mapgen;

import java.io.IOException;

import com.rs2.tools.mapgen.ArmourSets.Style;

/**
 * The Rimeclaw hunter base: an arctic hunter's fur-ruffed parka. Built from scratch for the Rimeclaw hunter (A34) draft set; see NewBases.
 *
 * <p>STUB: until it is built, the pieces are the donor's own models.
 */
final class BaseParka {

	private BaseParka() {
	}

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		return new ModelData[] { donor, donorSecond };
	}

	/** The inventory model for the slot, or null to let ArmourSets build it the usual way from the worn model(s). */
	static ModelData inventory(ArmourForge af, Style st, int slot, ModelData[] worn) throws IOException {
		return null;
	}
}
