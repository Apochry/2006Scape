package com.rs2.game.content.voidrealm;

import static com.rs2.game.content.voidrealm.VoidConstants.*;

import com.rs2.Constants;
import com.rs2.game.content.quests.impl.AshesOfTheCrown;
import com.rs2.game.content.quests.impl.Heartforged;
import com.rs2.game.players.Player;

/**
 * The weapon ladder: twenty-five weapons, from Bram's gutting knife (Attack 1) to the Starless's own longsword
 * (Attack 80). Models are built in {@code tools.mapgen.LadderWeapons}; bonuses live in ItemDefinitions.json (written by
 * tools/nythera_data.py); shops, drops and recipes are listed in docs/VOID_QUESTS.md ("The weapon ladder").
 *
 * <p>This class holds the server-side rules: which skill and level each one needs, which attack-style tab it shows,
 * and the two recipes (the Wand of the Scar at a waystone, the Starmetal warhammer at Gatehold's anvil). Attack speed,
 * animations and two-handedness come from the item names, which use the stock words (dagger, scimitar, 2h sword...).
 */
public final class VoidArsenal {

	private VoidArsenal() {
	}

	/** Attack-style tabs (sidebar interfaces), as ItemAssistant.sendWeapon picks them for the stock weapons. */
	static final int TAB_SWORD = 2276, TAB_AXE = 1698, TAB_STAFF = 328, TAB_SPEAR = 4679, TAB_BOW = 1764, TAB_MACE = 3796,
			TAB_LONGSWORD = 2423, TAB_WARHAMMER = 425, TAB_HALBERD = 8460;

	private static final int ATT = Constants.ATTACK, STR = Constants.STRENGTH, RNG = Constants.RANGED, MAG = Constants.MAGIC;

	/** Per rung, in ladder order: tab, then skill/level pairs to wield it. */
	public static final int[][] RULES = { { TAB_SWORD, ATT, 1 }, // 1 Netwright's gutting dagger
			{ TAB_AXE, ATT, 3 }, // 2 Hob's felling axe
			{ TAB_STAFF, MAG, 5 }, // 3 Pilgrim's staff
			{ TAB_SPEAR, ATT, 5 }, // 4 Militia spear
			{ TAB_BOW, RNG, 5 }, // 5 Wolfbone shortbow (arrows up to iron)
			{ TAB_SWORD, ATT, 10 }, // 6 Watchman's sword
			{ TAB_MACE, ATT, 15 }, // 7 Steelcap mace
			{ TAB_LONGSWORD, ATT, 20 }, // 8 Smuggler's scimitar
			{ TAB_WARHAMMER, ATT, 25, STR, 20 }, // 9 Barrowstone warhammer
			{ TAB_STAFF, MAG, 25 }, // 10 Silkwood wand
			{ TAB_LONGSWORD, ATT, 30 }, // 11 Gatehold longsword
			{ TAB_HALBERD, ATT, 30, STR, 30 }, // 12 Warden's halberd
			{ TAB_BOW, RNG, 30 }, // 13 Warden's longbow (arrows up to mithril)
			{ TAB_SWORD, ATT, 35 }, // 14 Scarglass dagger
			{ TAB_AXE, ATT, 40 }, // 15 Oathsworn battleaxe
			{ TAB_SPEAR, ATT, 40 }, // 16 Frostfang spear
			{ TAB_LONGSWORD, ATT, 45, STR, 40 }, // 17 Gravewarden 2h sword
			{ TAB_BOW, RNG, 45 }, // 18 Hollowheart shortbow (arrows up to adamant)
			{ TAB_STAFF, MAG, 50 }, // 19 Wand of the Scar
			{ TAB_WARHAMMER, ATT, 55, STR, 50 }, // 20 Starmetal warhammer
			{ TAB_HALBERD, ATT, 60, STR, 40 }, // 21 Riftglass halberd
			{ TAB_STAFF, MAG, 65, ATT, 40 }, // 22 Rimeheart staff (never needs water runes)
			{ TAB_LONGSWORD, ATT, 70 }, // 23 Nightglass scimitar (special: Rend)
			{ TAB_MACE, ATT, 75 }, // 24 Crownbreaker mace (special: Shatter)
			{ TAB_LONGSWORD, ATT, 80 }, // 25 Starless longsword (special: Eclipse)
	};

	/** The ladder rank (0-24) of an item, or -1. */
	public static int rank(int id) {
		return id >= LADDER[0] && id <= LADDER[LADDER.length - 1] ? id - LADDER[0] : -1;
	}

	/** A short "Attack 40" / "Magic 65, Attack 40" description of what a rung needs. */
	public static String requirement(int rank) {
		StringBuilder sb = new StringBuilder();
		int[] r = RULES[rank];
		for (int i = 1; i + 1 < r.length; i += 2) {
			sb.append(sb.length() > 0 ? ", " : "").append(com.rs2.game.content.skills.SkillHandler.skillNames[r[i]]).append(' ').append(r[i + 1]);
		}
		return sb.toString();
	}

	static boolean requirements(Player p, int id) {
		int rank = rank(id);
		if (rank < 0) {
			return false;
		}
		int[] r = RULES[rank];
		for (int i = 1; i + 1 < r.length; i += 2) {
			int lvl = r[i + 1];
			switch (r[i]) {
			case Constants.ATTACK:
				p.attackLevelReq = lvl;
				break;
			case Constants.STRENGTH:
				p.strengthLevelReq = lvl;
				break;
			case Constants.RANGED:
				p.rangeLevelReq = lvl;
				break;
			case Constants.MAGIC:
				p.magicLevelReq = lvl;
				break;
			}
		}
		return true;
	}

	/** The attack-style tab for a rung, or -1. */
	static int tab(int id) {
		int rank = rank(id);
		return rank < 0 ? -1 : RULES[rank][0];
	}

	/* ====================================================================== */
	/*                                RECIPES                                 */
	/* ====================================================================== */

	static final int WAND_CRAFTING = 50, HAMMER_SMITHING = 55;
	private static final int HAMMER = 2347, COSMIC_RUNE = 564, ANVIL = 2783;

	/** Item-on-object recipes; true if handled. */
	static boolean itemOnObject(Player p, int objectId, int x, int y, int itemId) {
		if (objectId == WAYSTONE && itemId == SILKWOOD_WAND) {
			wakeWand(p);
			return true;
		}
		if (objectId == ANVIL && itemId == BARROWSTONE_WARHAMMER && onNythera(x, y)) {
			forgeHammer(p);
			return true;
		}
		return false;
	}

	private static int level(Player p, int skill) {
		return p.getPlayerAssistant().getLevelForXP(p.playerXP[skill]);
	}

	/** Silkwood wand + a starmetal shard + 25 cosmic runes, touched to a waystone's rune: the Wand of the Scar. */
	private static void wakeWand(Player p) {
		if (p.voidCrown < AshesOfTheCrown.COMPLETE) {
			p.getPacketSender().sendMessage("The waystone's rune only flickers. It isn't awake yet.");
			return;
		}
		if (level(p, Constants.CRAFTING) < WAND_CRAFTING) {
			p.getPacketSender().sendMessage("You need a Crafting level of " + WAND_CRAFTING + " to bind the rune into the wand.");
			return;
		}
		if (!p.getItemAssistant().playerHasItem(STARMETAL) || !p.getItemAssistant().playerHasItem(COSMIC_RUNE, 25)) {
			p.getPacketSender().sendMessage("The rune wants a starmetal shard to sit in, and 25 cosmic runes to bind it.");
			return;
		}
		p.getItemAssistant().deleteItem(SILKWOOD_WAND, 1);
		p.getItemAssistant().deleteItem(STARMETAL, 1);
		p.getItemAssistant().deleteItem(COSMIC_RUNE, 25);
		p.getItemAssistant().addItem(SCAR_WAND, 1);
		p.startAnimation(711);
		p.gfx100(GFX_VOID_IMPACT);
		p.getPlayerAssistant().addSkillXP(1500, Constants.CRAFTING);
		p.getPlayerAssistant().addSkillXP(1500, Constants.MAGIC);
		p.getPacketSender().sendMessage("The waystone's rune crawls off the stone and into the starmetal. You've made a Wand of the Scar.");
	}

	/** Barrowstone warhammer + 2 starmetal at Gatehold's anvil (a hammer, Smithing 55): the Starmetal warhammer. */
	private static void forgeHammer(Player p) {
		if (p.voidForge < Heartforged.COMPLETE) {
			p.getPacketSender().sendMessage("Dorrin shakes his head. \"I'd not know where to start with that. Not yet.\"");
			return;
		}
		if (level(p, Constants.SMITHING) < HAMMER_SMITHING) {
			p.getPacketSender().sendMessage("You need a Smithing level of " + HAMMER_SMITHING + " to face the hammer with starmetal.");
			return;
		}
		if (!p.getItemAssistant().playerHasItem(HAMMER) || !p.getItemAssistant().playerHasItem(STARMETAL, 2)) {
			p.getPacketSender().sendMessage("You need a hammer and two starmetal to face the warhammer.");
			return;
		}
		p.getItemAssistant().deleteItem(BARROWSTONE_WARHAMMER, 1);
		p.getItemAssistant().deleteItem(STARMETAL, 2);
		p.getItemAssistant().addItem(STARMETAL_WARHAMMER, 1);
		p.startAnimation(898);
		p.gfx0(GFX_STARFALL_MARK);
		p.getPlayerAssistant().addSkillXP(2200, Constants.SMITHING);
		p.getPacketSender().sendMessage("You beat the starmetal over the old grave-stone. It rings, and keeps ringing: a Starmetal warhammer.");
	}

	/* ====================================================================== */
	/*                                 ADMIN                                  */
	/* ====================================================================== */

	/** ::vt weapons [from to] - every rung (or ranks from-to, 1-25) into the inventory, for testing (25 need 25 free slots). */
	static void spawnAll(Player p, int from, int to) {
		int n = 0;
		for (int r = Math.max(1, from); r <= Math.min(LADDER.length, to); r++) {
			if (p.getItemAssistant().addItem(LADDER[r - 1], 1)) {
				n++;
			}
		}
		p.getPacketSender().sendMessage("Ladder weapons " + from + "-" + to + ": " + n + " added to your inventory.");
	}
}
