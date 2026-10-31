package com.rs2.game.content.voidrealm;

import static com.rs2.game.content.voidrealm.VoidConstants.*;

import com.rs2.game.items.ItemConstants;
import com.rs2.game.players.Player;

/**
 * Equipment rules for the saga's hand-built gear: which slot each piece goes in, the levels to wear it, and the
 * attack-style tab for weapons whose names the generic name-matching in ItemAssistant doesn't recognise.
 */
public final class VoidGear {

	private VoidGear() {
	}

	public static void slots(int[] targetSlots) {
		int[] slot = { ItemConstants.HAT, ItemConstants.CHEST, ItemConstants.LEGS, ItemConstants.FEET, ItemConstants.HANDS };
		for (int i = 0; i < WARDEN_SET.length; i++) {
			targetSlots[WARDEN_SET[i]] = slot[i];
		}
		int[] royal = { ItemConstants.HAT, ItemConstants.CHEST, ItemConstants.LEGS, ItemConstants.SHIELD, ItemConstants.FEET,
				ItemConstants.HANDS };
		for (int i = 0; i < ROYAL_SET.length; i++) {
			targetSlots[ROYAL_SET[i]] = royal[i];
		}
		for (int w : new int[] { STARFALL_HALBERD, RIMECLEAVER, KINGS_SCEPTRE, DUSKYEW_SHORTBOW, CAGED_STAR_STAFF }) {
			targetSlots[w] = ItemConstants.WEAPON;
		}
		VoidArmoury.slots(targetSlots);
		for (int[] set : new int[][] { LADDER, STARMETAL_SET, VOIDSTEEL_SET }) {
			for (int w : set) {
				targetSlots[w] = ItemConstants.WEAPON;
			}
		}
	}

	/** Sets the player's level requirements for one of the saga's items; false if it isn't one. */
	public static boolean requirements(Player p, int id) {
		if (VoidArsenal.requirements(p, id) || VoidArmoury.requirements(p, id)) {
			return true;
		}
		for (int w : WARDEN_SET) {
			if (id == w) {
				p.defenceLevelReq = 30;
				return true;
			}
		}
		for (int r : ROYAL_SET) {
			if (id == r) {
				p.defenceLevelReq = 50;
				return true;
			}
		}
		switch (id) {
		case STARFALL_HALBERD:
			p.attackLevelReq = 60;
			p.strengthLevelReq = 30;
			return true;
		case RIMECLEAVER:
			p.attackLevelReq = 55;
			return true;
		case KINGS_SCEPTRE:
			p.attackLevelReq = 60;
			return true;
		case DUSKYEW_SHORTBOW:
			p.rangeLevelReq = 50;
			return true;
		case CAGED_STAR_STAFF:
			p.magicLevelReq = 60;
			p.attackLevelReq = 40;
			return true;
		}
		return false;
	}

	/** The attack-style tab for the saga's weapons (and the ladder's); false to let the generic rules decide. */
	public static boolean sendWeapon(Player p, int weapon, String name) {
		int tab = VoidArsenal.tab(weapon);
		switch (weapon) {
		case STARFALL_HALBERD:
			tab = 8460; // jab, swipe, fend
			break;
		case RIMECLEAVER:
			tab = 1698; // chop, hack, smash, block
			break;
		case KINGS_SCEPTRE:
			tab = 3796; // pound, pummel, spike, block
			break;
		default:
			if (tab < 0) {
				return false;
			}
		}
		p.getPacketSender().setSidebarInterface(0, tab);
		p.getPacketSender().sendFrame246(tab + 1, 200, weapon);
		p.getPacketSender().sendString(name, tab + 3);
		return true;
	}
}
