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
 *
 * <p>The draft sets (A25-A41) each have a base of their own, picked by the set's slug before the kind: the kind then
 * only says which donor rig it is built on and which skill it needs (COAT: melee, on the rune plate; MANTLE: magic, on
 * the mystic robe; WRAP: ranged, on the leather).
 */
final class NewBases {

	private NewBases() {
	}

	/**
	 * The worn model(s) for one piece of a generated base: {main, second or null}. {@code donor} / {@code donorSecond}
	 * are the stock donor's worn models for the slot (second: the platebody's arms, the mystic top's torso).
	 */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		switch (st.slug) { // a base of the set's own
		case "ashenpilgrim":
			return BasePilgrim.worn(af, st, slot, female, donor, donorSecond);
		case "thornwarden":
			return BaseGhillie.worn(af, st, slot, female, donor, donorSecond);
		case "saltgilt":
			return BaseDiver.worn(af, st, slot, female, donor, donorSecond);
		case "tidecaller":
			return BaseJellyfish.worn(af, st, slot, female, donor, donorSecond);
		case "shardhide":
			return BasePelt.worn(af, st, slot, female, donor, donorSecond);
		case "wildgrove":
			return BaseTreant.worn(af, st, slot, female, donor, donorSecond);
		case "gravescale":
			return BaseSerpent.worn(af, st, slot, female, donor, donorSecond);
		case "sentinel":
			return BaseBastion.worn(af, st, slot, female, donor, donorSecond);
		case "broodkeeper":
			return BaseCarapace.worn(af, st, slot, female, donor, donorSecond);
		case "rimeclaw":
			return BaseParka.worn(af, st, slot, female, donor, donorSecond);
		case "herald":
			return BaseBannerman.worn(af, st, slot, female, donor, donorSecond);
		case "emberforged":
			return BaseFurnace.worn(af, st, slot, female, donor, donorSecond);
		case "shroud":
			return BaseWraith.worn(af, st, slot, female, donor, donorSecond);
		case "riftwalker":
			return BaseShatter.worn(af, st, slot, female, donor, donorSecond);
		case "dawnguard":
			return BaseSeraph.worn(af, st, slot, female, donor, donorSecond);
		case "kingsguard":
			return BaseKingsguard.worn(af, st, slot, female, donor, donorSecond);
		case "marrowbound":
			return BaseMarrow.worn(af, st, slot, female, donor, donorSecond);
		default:
			break;
		}
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

	/**
	 * The inventory model for one piece of a generated base, or null to build it the usual way (ArmourSets.inventory)
	 * from {@code worn}, the male worn models {main, second or null}.
	 */
	static ModelData inventory(ArmourForge af, Style st, int slot, ModelData[] worn) throws IOException {
		switch (st.slug) {
		case "ashenpilgrim":
			return BasePilgrim.inventory(af, st, slot, worn);
		case "thornwarden":
			return BaseGhillie.inventory(af, st, slot, worn);
		case "saltgilt":
			return BaseDiver.inventory(af, st, slot, worn);
		case "tidecaller":
			return BaseJellyfish.inventory(af, st, slot, worn);
		case "shardhide":
			return BasePelt.inventory(af, st, slot, worn);
		case "wildgrove":
			return BaseTreant.inventory(af, st, slot, worn);
		case "gravescale":
			return BaseSerpent.inventory(af, st, slot, worn);
		case "sentinel":
			return BaseBastion.inventory(af, st, slot, worn);
		case "broodkeeper":
			return BaseCarapace.inventory(af, st, slot, worn);
		case "rimeclaw":
			return BaseParka.inventory(af, st, slot, worn);
		case "herald":
			return BaseBannerman.inventory(af, st, slot, worn);
		case "emberforged":
			return BaseFurnace.inventory(af, st, slot, worn);
		case "shroud":
			return BaseWraith.inventory(af, st, slot, worn);
		case "riftwalker":
			return BaseShatter.inventory(af, st, slot, worn);
		case "dawnguard":
			return BaseSeraph.inventory(af, st, slot, worn);
		case "kingsguard":
			return BaseKingsguard.inventory(af, st, slot, worn);
		case "marrowbound":
			return BaseMarrow.inventory(af, st, slot, worn);
		default:
			return null;
		}
	}
}
