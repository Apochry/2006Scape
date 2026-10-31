package com.rs2.game.content.voidrealm;

import static com.rs2.game.content.voidrealm.VoidConstants.*;

import com.rs2.Constants;
import com.rs2.game.content.quests.impl.Heartforged;
import com.rs2.game.content.quests.impl.TheHollowKing;
import com.rs2.game.items.ItemConstants;
import com.rs2.game.players.Player;

/**
 * The twenty-four armour sets. The saga's six: Oathsworn plate (Defence 40), Duskstalker hide (Ranged 50), Scarweave
 * robes (Magic 60), starmetal plate (Defence 60), voidsteel plate (Defence 75) and the Heartfire regalia (Defence 90).
 * The eight low sets: Gatehold recruit (Defence 10), Woodsman (Ranged 15), Croftwise (Magic 20), Rattlebone (Defence
 * 25), Blackbrine (Ranged 30), Warden initiate (Defence 30), Barrowbronze (Defence 35) and Veilsworn (Magic 40). The ten
 * middle sets: Quarryhand chain (Defence 20), Silkspinner leather (Ranged 25), Stonecaller robes (Magic 30), Ridgerunner
 * hide (Ranged 35), Ravager plate (Defence 40), Moonmourn robes (Magic 45), Rimebound chain (Defence 50), Nightrunner
 * hide (Ranged 50), Farseer robes (Magic 55) and Last knight's plate (Defence 60). Models are built in
 * {@code tools.mapgen.ArmourSets}, {@code LowArmour} and {@code MidArmour}; bonuses are in ItemDefinitions.json
 * (tools/nythera_data.py); drops and shops are listed in docs/VOID_QUESTS.md ("Armour sets").
 *
 * <p>This class holds the equipment slots, the wield levels, and the six smithing and crafting recipes (recruit's kit,
 * starmetal and Last knight's plate at Gatehold's anvil, Stonecaller robes at a waystone, voidsteel and Heartfire at the
 * Starless Gate). Whether a body piece covers the arms, or a helm the face, comes from its name, as for the stock items
 * ("platebody", "robe top", "full helm", "coif").
 */
public final class VoidArmoury {

	private VoidArmoury() {
	}

	static final int[] SLOTS = { ItemConstants.HAT, ItemConstants.CHEST, ItemConstants.LEGS, ItemConstants.SHIELD, ItemConstants.FEET,
			ItemConstants.HANDS };
	static final String[] SET_NAMES = { "Oathsworn", "Duskstalker", "Scarweave", "Starmetal", "Voidsteel", "Heartfire", "Gatehold recruit",
			"Woodsman", "Croftwise", "Rattlebone", "Blackbrine", "Warden initiate", "Barrowbronze", "Veilsworn", "Quarryhand", "Silkspinner",
			"Stonecaller", "Ridgerunner", "Ravager", "Moonmourn", "Rimebound", "Nightrunner", "Farseer", "Last knight" };

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
		case 5:
			return new int[] { Constants.DEFENCE, 90 };
		case 6:
			return new int[] { Constants.DEFENCE, 10 };
		case 7:
			return new int[] { Constants.RANGED, 15 };
		case 8:
			return new int[] { Constants.MAGIC, 20 };
		case 9:
			return new int[] { Constants.DEFENCE, 25 };
		case 10: // the body and chaps also want Defence, as studded leather's do
			return f[1] == 1 || f[1] == 2 ? new int[] { Constants.RANGED, 30, Constants.DEFENCE, 20 } : new int[] { Constants.RANGED, 30 };
		case 11:
			return new int[] { Constants.DEFENCE, 30 };
		case 12:
			return new int[] { Constants.DEFENCE, 35 };
		case 13: // Veilsworn: the robe top and bottom want a little Defence, as mystic's do
			return robe(f, 40, 10);
		case 14:
			return new int[] { Constants.DEFENCE, 20 };
		case 15:
			return new int[] { Constants.RANGED, 25 };
		case 16:
			return new int[] { Constants.MAGIC, 30 };
		case 17: // Ridgerunner: the body and chaps also want Defence
			return f[1] == 1 || f[1] == 2 ? new int[] { Constants.RANGED, 35, Constants.DEFENCE, 25 } : new int[] { Constants.RANGED, 35 };
		case 18:
			return new int[] { Constants.DEFENCE, 40 };
		case 19:
			return robe(f, 45, 10);
		case 20:
			return new int[] { Constants.DEFENCE, 50 };
		case 21: // Nightrunner: as Duskstalker
			return f[1] == 1 || f[1] == 2 ? new int[] { Constants.RANGED, 50, Constants.DEFENCE, 40 } : new int[] { Constants.RANGED, 50 };
		case 22:
			return robe(f, 55, 20);
		default:
			return new int[] { Constants.DEFENCE, 60 };
		}
	}

	/** A robe set's rule: Magic for every piece, and a little Defence for the top and bottom. */
	private static int[] robe(int[] f, int magic, int defence) {
		return f[1] == 1 || f[1] == 2 ? new int[] { Constants.MAGIC, magic, Constants.DEFENCE, defence } : new int[] { Constants.MAGIC, magic };
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
	static final int RECRUIT_SMITHING = 10, IRON_BAR = 2351;
	/** The iron pieces a recruit's kit is re-worked from (helm, chainbody, platelegs, sq shield), an iron bar each. */
	static final int[] IRON_ARMOUR = { 1137, 1101, 1067, 1175 };
	/**
	 * The pieces Last knight's plate is re-forged from, slot for slot: adamant full helm, platebody, platelegs,
	 * kiteshield and boots, and (adamant has no gauntlets) a Warden's gauntlets; with starmetal for the royal trim.
	 */
	static final int[] ADAMANT_ARMOUR = { 1161, 1123, 1073, 1199, 4129, WARDEN_GAUNTLETS };
	static final int[] KNIGHT_METAL = { 1, 2, 2, 1, 1, 1 };
	static final int KNIGHT_SMITHING = 60;
	/** Stonecaller robes: a Croftwise piece with earth and cosmic runes, laid on a waystone's rune. */
	static final int STONECALLER_CRAFTING = 25, STONECALLER_MAGIC = 30, EARTH_RUNE = 557, COSMIC_RUNE = 564, STONE_EARTH = 20, STONE_COSMIC = 2;

	static boolean itemOnObject(Player p, int objectId, int x, int y, int itemId) {
		int i = VoidArsenal.indexOf(RUNE_ARMOUR, itemId);
		if (objectId == ANVIL && i >= 0 && onNythera(x, y)) {
			starmetal(p, i);
			return true;
		}
		if (objectId == ANVIL && (i = VoidArsenal.indexOf(IRON_ARMOUR, itemId)) >= 0 && onNythera(x, y)) {
			recruit(p, i);
			return true;
		}
		if (objectId == ANVIL && (i = VoidArsenal.indexOf(ADAMANT_ARMOUR, itemId)) >= 0 && onNythera(x, y)) {
			lastKnight(p, i);
			return true;
		}
		if (objectId == WAYSTONE && (i = VoidArsenal.indexOf(CROFTWISE_SET, itemId)) >= 0) {
			stonecaller(p, i);
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

	/** An iron piece + an iron bar on Gatehold's anvil: the matching piece of a Gatehold recruit's kit. */
	private static void recruit(Player p, int i) {
		if (level(p, Constants.SMITHING) < RECRUIT_SMITHING) {
			p.getPacketSender().sendMessage("You need a Smithing level of " + RECRUIT_SMITHING + " to re-work that into a recruit's kit.");
			return;
		}
		if (!p.getItemAssistant().playerHasItem(HAMMER) || !p.getItemAssistant().playerHasItem(IRON_BAR)) {
			p.getPacketSender().sendMessage("You need a hammer and an iron bar for that.");
			return;
		}
		p.getItemAssistant().deleteItem(IRON_ARMOUR[i], 1);
		p.getItemAssistant().deleteItem(IRON_BAR, 1);
		p.getItemAssistant().addItem(RECRUIT_SET[i], 1);
		p.startAnimation(898);
		p.getPlayerAssistant().addSkillXP(50, Constants.SMITHING);
		p.getPacketSender().sendMessage("You beat out the dents, rivet on a strap and stamp the gate's mark. Kit fit for the wall.");
	}

	/** An adamant piece (or a Warden's gauntlets) + starmetal at Gatehold's anvil, after Heartforged: Last knight's plate. */
	private static void lastKnight(Player p, int i) {
		if (p.voidForge < Heartforged.COMPLETE) {
			p.getPacketSender().sendMessage("Dorrin shakes his head. \"Royal plate? Finish the heart-forge with me first.\"");
			return;
		}
		if (level(p, Constants.SMITHING) < KNIGHT_SMITHING) {
			p.getPacketSender().sendMessage("You need a Smithing level of " + KNIGHT_SMITHING + " to re-forge that in the old royal pattern.");
			return;
		}
		int metal = KNIGHT_METAL[i];
		if (!p.getItemAssistant().playerHasItem(HAMMER) || !p.getItemAssistant().playerHasItem(STARMETAL, metal)) {
			p.getPacketSender().sendMessage("You need a hammer and " + metal + " starmetal for the trim.");
			return;
		}
		p.getItemAssistant().deleteItem(ADAMANT_ARMOUR[i], 1);
		p.getItemAssistant().deleteItem(STARMETAL, metal);
		p.getItemAssistant().addItem(LAST_KNIGHT_SET[i], 1);
		p.startAnimation(898);
		p.getPlayerAssistant().addSkillXP(250 + 250 * metal, Constants.SMITHING);
		p.getPacketSender().sendMessage("You hammer in Aurel's broken crown and gild the edges with starmetal. A knight's plate, the last of its kind.");
	}

	/** A Croftwise piece + earth and cosmic runes, laid on a waystone's rune: the matching Stonecaller piece. */
	private static void stonecaller(Player p, int i) {
		if (level(p, Constants.CRAFTING) < STONECALLER_CRAFTING || level(p, Constants.MAGIC) < STONECALLER_MAGIC) {
			p.getPacketSender().sendMessage("You need Crafting " + STONECALLER_CRAFTING + " and Magic " + STONECALLER_MAGIC
					+ " to wake the stone in the cloth.");
			return;
		}
		if (!p.getItemAssistant().playerHasItem(EARTH_RUNE, STONE_EARTH) || !p.getItemAssistant().playerHasItem(COSMIC_RUNE, STONE_COSMIC)) {
			p.getPacketSender().sendMessage("The rune wants " + STONE_EARTH + " earth runes and " + STONE_COSMIC + " cosmic runes for that.");
			return;
		}
		p.getItemAssistant().deleteItem(CROFTWISE_SET[i], 1);
		p.getItemAssistant().deleteItem(EARTH_RUNE, STONE_EARTH);
		p.getItemAssistant().deleteItem(COSMIC_RUNE, STONE_COSMIC);
		p.getItemAssistant().addItem(STONECALLER_SET[i], 1);
		p.startAnimation(711);
		p.getPlayerAssistant().addSkillXP(180, Constants.CRAFTING);
		p.getPlayerAssistant().addSkillXP(120, Constants.MAGIC);
		p.getPacketSender().sendMessage("The waystone's rune crawls into the cloth and settles there, grey as slate. Stonecaller's robes.");
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

	/** ::vt armour n - set n (1-24) into the inventory, for testing. */
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
