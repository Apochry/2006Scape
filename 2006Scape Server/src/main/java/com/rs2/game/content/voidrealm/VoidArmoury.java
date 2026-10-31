package com.rs2.game.content.voidrealm;

import static com.rs2.game.content.voidrealm.VoidConstants.*;

import com.rs2.Constants;
import com.rs2.game.content.quests.impl.Heartforged;
import com.rs2.game.content.quests.impl.TheHollowKing;
import com.rs2.game.items.ItemConstants;
import com.rs2.game.players.Player;

/**
 * The six armour sets: Oathsworn plate (Defence 40), Duskstalker hide (Ranged 50), Scarweave robes (Magic 60),
 * starmetal plate (Defence 60), voidsteel plate (Defence 75) and the Heartfire regalia (Defence 90). Models are built in
 * {@code tools.mapgen.ArmourSets}; bonuses are in ItemDefinitions.json (tools/nythera_data.py); drops and shops are
 * listed in docs/VOID_QUESTS.md ("Armour sets").
 *
 * <p>This class holds the equipment slots, the wield levels, and the three smithing recipes (starmetal at Gatehold's
 * anvil, voidsteel and Heartfire at the Starless Gate). Whether a body piece covers the arms, or a helm the face,
 * comes from its name, as for the stock items ("platebody", "robe top", "full helm", "coif").
 */
public final class VoidArmoury {

	private VoidArmoury() {
	}

	static final int[] SLOTS = { ItemConstants.HAT, ItemConstants.CHEST, ItemConstants.LEGS, ItemConstants.SHIELD, ItemConstants.FEET,
			ItemConstants.HANDS };
	static final String[] SET_NAMES = { "Oathsworn", "Duskstalker", "Scarweave", "Starmetal", "Voidsteel", "Heartfire" };

	public static void slots(int[] targetSlots) {
		for (int[] set : ARMOUR_SETS) {
			for (int i = 0; i < set.length; i++) {
				if (set[i] >= 0) {
					targetSlots[set[i]] = SLOTS[i];
				}
			}
		}
	}

	/** {set, slot} of an armour piece, or null. */
	static int[] find(int id) {
		for (int s = 0; s < ARMOUR_SETS.length; s++) {
			for (int i = 0; i < ARMOUR_SETS[s].length; i++) {
				if (ARMOUR_SETS[s][i] == id) {
					return new int[] { s, i };
				}
			}
		}
		return null;
	}

	/** Skill/level pairs to wear a piece, or null. */
	static int[] rule(int id) {
		int[] f = find(id);
		if (f == null) {
			return null;
		}
		switch (f[0]) {
		case 0:
			return new int[] { Constants.DEFENCE, 40 };
		case 1: // the body and chaps also want Defence, as green d'hide's do
			return f[1] == 1 || f[1] == 2 ? new int[] { Constants.RANGED, 50, Constants.DEFENCE, 40 } : new int[] { Constants.RANGED, 50 };
		case 2:
			return new int[] { Constants.MAGIC, 60, Constants.DEFENCE, 20 };
		case 3:
			return new int[] { Constants.DEFENCE, 60 };
		case 4:
			return new int[] { Constants.DEFENCE, 75 };
		default:
			return new int[] { Constants.DEFENCE, 90 };
		}
	}

	public static String requirement(int id) {
		int[] r = rule(id);
		if (r == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i + 1 < r.length; i += 2) {
			sb.append(sb.length() > 0 ? ", " : "").append(com.rs2.game.content.skills.SkillHandler.skillNames[r[i]]).append(' ').append(r[i + 1]);
		}
		return sb.toString();
	}

	static boolean requirements(Player p, int id) {
		int[] r = rule(id);
		if (r == null) {
			return false;
		}
		for (int i = 0; i + 1 < r.length; i += 2) {
			switch (r[i]) {
			case Constants.DEFENCE:
				p.defenceLevelReq = r[i + 1];
				break;
			case Constants.RANGED:
				p.rangeLevelReq = r[i + 1];
				break;
			case Constants.MAGIC:
				p.magicLevelReq = r[i + 1];
				break;
			}
		}
		return true;
	}

	/* ====================================================================== */
	/*                                RECIPES                                 */
	/* ====================================================================== */

	static final int STARMETAL_SMITHING = 65, VOIDSTEEL_SMITHING = 80, HEARTFIRE_SMITHING = 90, HEARTFIRE_KILLS = 25;
	private static final int HAMMER = 2347, ANVIL = 2783;
	/** The rune pieces starmetal armour is faced over (rune has no gauntlets: leather gloves take their place). */
	static final int[] RUNE_ARMOUR = { 1163, 1127, 1079, 1201, 4131, 1059 };
	/** Starmetal per starmetal piece, Starless shards per voidsteel piece; the Heartfire regalia takes twice the shards. */
	static final int[] METAL = { 2, 4, 3, 3, 1, 1 };

	static boolean itemOnObject(Player p, int objectId, int x, int y, int itemId) {
		int i = VoidArsenal.indexOf(RUNE_ARMOUR, itemId);
		if (objectId == ANVIL && i >= 0 && onNythera(x, y)) {
			starmetal(p, i);
			return true;
		}
		if (objectId == STARLESS_GATE) {
			if ((i = VoidArsenal.indexOf(STARMETAL_ARMOUR, itemId)) >= 0) {
				voidsteel(p, i);
				return true;
			}
			if ((i = VoidArsenal.indexOf(ROYAL_SET, itemId)) >= 0) {
				heartfire(p, i);
				return true;
			}
		}
		return false;
	}

	private static int level(Player p, int skill) {
		return p.getPlayerAssistant().getLevelForXP(p.playerXP[skill]);
	}

	private static void starmetal(Player p, int i) {
		if (p.voidForge < Heartforged.COMPLETE) {
			p.getPacketSender().sendMessage("Dorrin shakes his head. \"Plate's harder than blades. Finish the heart-forge with me first.\"");
			return;
		}
		if (level(p, Constants.SMITHING) < STARMETAL_SMITHING) {
			p.getPacketSender().sendMessage("You need a Smithing level of " + STARMETAL_SMITHING + " to face rune armour with starmetal.");
			return;
		}
		if (!p.getItemAssistant().playerHasItem(HAMMER) || !p.getItemAssistant().playerHasItem(STARMETAL, METAL[i])) {
			p.getPacketSender().sendMessage("You need a hammer and " + METAL[i] + " starmetal for that.");
			return;
		}
		p.getItemAssistant().deleteItem(RUNE_ARMOUR[i], 1);
		p.getItemAssistant().deleteItem(STARMETAL, METAL[i]);
		p.getItemAssistant().addItem(STARMETAL_ARMOUR[i], 1);
		p.startAnimation(898);
		p.gfx0(GFX_STARFALL_MARK);
		p.getPlayerAssistant().addSkillXP(500 + 350 * METAL[i], Constants.SMITHING);
		p.getPacketSender().sendMessage("The starmetal runs over the rune and sets pale and bright.");
	}

	private static void voidsteel(Player p, int i) {
		if (p.voidKing < TheHollowKing.COMPLETE) {
			p.getPacketSender().sendMessage("The gate is shut. Whatever's behind it isn't interested yet.");
			return;
		}
		if (level(p, Constants.SMITHING) < VOIDSTEEL_SMITHING) {
			p.getPacketSender().sendMessage("You need a Smithing level of " + VOIDSTEEL_SMITHING + " to quench plate in the dark.");
			return;
		}
		if (!p.getItemAssistant().playerHasItem(STARLESS_SHARD, METAL[i])) {
			p.getPacketSender().sendMessage("The dark wants " + METAL[i] + " Starless shard" + (METAL[i] == 1 ? "" : "s") + " for that.");
			return;
		}
		p.getItemAssistant().deleteItem(STARMETAL_ARMOUR[i], 1);
		p.getItemAssistant().deleteItem(STARLESS_SHARD, METAL[i]);
		p.getItemAssistant().addItem(VOIDSTEEL_ARMOUR[i], 1);
		p.startAnimation(827);
		p.gfx100(GFX_VOID_IMPACT);
		p.getPlayerAssistant().addSkillXP(1800 + 700 * METAL[i], Constants.SMITHING);
		p.getPacketSender().sendMessage("You push it through the gate. It comes back black, and its edges are burning.");
	}

	/** A Royal guard piece + its voidsteel twin + Starless shards, at the gate, after everything: the Heartfire regalia. */
	private static void heartfire(Player p, int i) {
		if (!VoidArsenal.allQuests(p)) {
			p.getPacketSender().sendMessage("Aurel's plate won't burn again for anyone who's left his story unfinished. (Every quest in the saga.)");
			return;
		}
		if (p.starlessKills < HEARTFIRE_KILLS) {
			p.getPacketSender().sendMessage("The dark won't give up its fire yet. (" + p.starlessKills + "/" + HEARTFIRE_KILLS + " Starless kills)");
			return;
		}
		if (level(p, Constants.SMITHING) < HEARTFIRE_SMITHING) {
			p.getPacketSender().sendMessage("You need a Smithing level of " + HEARTFIRE_SMITHING + " to re-light a king's plate.");
			return;
		}
		int shards = METAL[i] * 2;
		if (!p.getItemAssistant().playerHasItem(VOIDSTEEL_ARMOUR[i]) || !p.getItemAssistant().playerHasItem(STARLESS_SHARD, shards)) {
			p.getPacketSender().sendMessage("It needs the matching voidsteel piece and " + shards + " Starless shards.");
			return;
		}
		p.getItemAssistant().deleteItem(ROYAL_SET[i], 1);
		p.getItemAssistant().deleteItem(VOIDSTEEL_ARMOUR[i], 1);
		p.getItemAssistant().deleteItem(STARLESS_SHARD, shards);
		p.getItemAssistant().addItem(HEARTFIRE_SET[i], 1);
		p.startAnimation(827);
		p.gfx0(GFX_HEARTFIRE);
		p.getPlayerAssistant().addSkillXP(4000 + 600 * shards, Constants.SMITHING);
		p.getPacketSender().sendMessage("@red@The dark lets go of the fire it took. Aurel's plate comes back burning.");
	}

	/** ::vt armour n - set n (1-6) into the inventory, for testing. */
	static void spawnSet(Player p, int n) {
		int s = Math.max(1, Math.min(ARMOUR_SETS.length, n)) - 1, added = 0;
		for (int id : ARMOUR_SETS[s]) {
			if (id >= 0 && p.getItemAssistant().addItem(id, 1)) {
				added++;
			}
		}
		p.getPacketSender().sendMessage(SET_NAMES[s] + " armour: " + added + " added to your inventory.");
	}
}
