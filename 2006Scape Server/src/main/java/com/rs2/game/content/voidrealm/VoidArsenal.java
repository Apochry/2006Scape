package com.rs2.game.content.voidrealm;

import static com.rs2.game.content.voidrealm.VoidConstants.*;

import com.rs2.Constants;
import com.rs2.game.content.quests.impl.AshesOfTheCrown;
import com.rs2.game.content.quests.impl.Heartforged;
import com.rs2.game.content.quests.impl.TheHollowKing;
import com.rs2.game.players.Player;

/**
 * The weapon ladder: thirty weapons, from Bram's gutting knife (Attack 1) to Last Light (Attack 99), and the two smithed
 * tiers (starmetal, Attack 50; voidsteel, Attack 70) that sit beside rune and dragon. Models are built in {@code tools.mapgen.LadderWeapons}; bonuses live in ItemDefinitions.json (written by
 * tools/nythera_data.py); shops, drops and recipes are listed in docs/VOID_QUESTS.md ("The weapon ladder").
 *
 * <p>This class holds the server-side rules: which skill and level each one needs, which attack-style tab it shows,
 * and the two recipes (the Wand of the Scar at a waystone, the Gravestar warhammer at Gatehold's anvil). Attack speed,
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
			{ TAB_WARHAMMER, ATT, 55, STR, 50 }, // 20 Gravestar warhammer
			{ TAB_HALBERD, ATT, 60, STR, 40 }, // 21 Riftglass halberd
			{ TAB_STAFF, MAG, 65, ATT, 40 }, // 22 Rimeheart staff (never needs water runes)
			{ TAB_LONGSWORD, ATT, 70 }, // 23 Nightglass scimitar (special: Rend)
			{ TAB_MACE, ATT, 75 }, // 24 Crownbreaker mace (special: Shatter)
			{ TAB_LONGSWORD, ATT, 80 }, // 25 Starless longsword (special: Eclipse)
			{ TAB_BOW, RNG, 85 }, // 26 Eventide longbow (arrows up to rune; special: Starshot)
			{ TAB_SPEAR, ATT, 85 }, // 27 Sunspear (special: Sunburst)
			{ TAB_STAFF, MAG, 90, ATT, 50 }, // 28 Staff of the first star (never needs fire or air runes)
			{ TAB_AXE, ATT, 90, STR, 80 }, // 29 Kingsbane battleaxe (special: Regicide)
			{ TAB_LONGSWORD, ATT, 99, STR, 90 }, // 30 Last Light 2h sword (special: Last Light)
	};

	/** The two smithed tiers share one shape order: dagger, sword, longsword, scimitar, mace, warhammer, battleaxe, 2h sword, spear, halberd, longbow. */
	static final int[] SET_TABS = { TAB_SWORD, TAB_SWORD, TAB_LONGSWORD, TAB_LONGSWORD, TAB_MACE, TAB_WARHAMMER, TAB_AXE, TAB_LONGSWORD, TAB_SPEAR,
			TAB_HALBERD, TAB_BOW };

	/** Wield levels: starmetal Attack 50 (halberd also Strength 45); voidsteel Attack 70 (halberd also Strength 60, longbow Ranged 70). */
	static final int STARMETAL_LEVEL = 50, VOIDSTEEL_LEVEL = 70;

	/** The rule row (tab, then skill/level pairs) for any ladder or set weapon, or null. */
	static int[] rule(int id) {
		int rank = rank(id);
		if (rank >= 0) {
			return RULES[rank];
		}
		int i = indexOf(STARMETAL_SET, id);
		if (i >= 0) {
			return i == 9 ? new int[] { SET_TABS[i], ATT, STARMETAL_LEVEL, STR, 45 } : new int[] { SET_TABS[i], ATT, STARMETAL_LEVEL };
		}
		i = indexOf(VOIDSTEEL_SET, id);
		if (i >= 0) {
			return i == 10 ? new int[] { SET_TABS[i], RNG, VOIDSTEEL_LEVEL }
					: i == 9 ? new int[] { SET_TABS[i], ATT, VOIDSTEEL_LEVEL, STR, 60 } : new int[] { SET_TABS[i], ATT, VOIDSTEEL_LEVEL };
		}
		return null;
	}

	static int indexOf(int[] a, int id) {
		for (int i = 0; i < a.length; i++) {
			if (a[i] == id) {
				return i;
			}
		}
		return -1;
	}

	/** Every weapon this class rules: the ladder, then both sets. */
	public static boolean isArsenal(int id) {
		return rule(id) != null;
	}

	/** The ladder rank (0-29) of an item, or -1. */
	public static int rank(int id) {
		return id >= LADDER[0] && id <= LADDER[LADDER.length - 1] ? id - LADDER[0] : -1;
	}

	/** A short "Attack 40" / "Magic 65, Attack 40" description of what a rung needs. */
	public static String requirement(int rank) {
		return requirementOf(RULES[rank]);
	}

	/** As {@link #requirement(int)}, for any weapon by item id. */
	public static String requirementFor(int id) {
		int[] r = rule(id);
		return r == null ? "" : requirementOf(r);
	}

	private static String requirementOf(int[] r) {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i + 1 < r.length; i += 2) {
			sb.append(sb.length() > 0 ? ", " : "").append(com.rs2.game.content.skills.SkillHandler.skillNames[r[i]]).append(' ').append(r[i + 1]);
		}
		return sb.toString();
	}

	static boolean requirements(Player p, int id) {
		int[] r = rule(id);
		if (r == null) {
			return false;
		}
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
		int[] r = rule(id);
		return r == null ? -1 : r[0];
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
		if (objectId == ANVIL && indexOf(RUNE_BASES, itemId) >= 0 && onNythera(x, y)) {
			forgeStarmetal(p, indexOf(RUNE_BASES, itemId));
			return true;
		}
		if (objectId == STARLESS_GATE && itemId == STARLESS_LONGSWORD) {
			forgeLastLight(p);
			return true;
		}
		if (objectId == STARLESS_GATE && (indexOf(STARMETAL_SET, itemId) >= 0 || itemId == MAGIC_LONGBOW)) {
			quench(p, itemId == MAGIC_LONGBOW ? 10 : indexOf(STARMETAL_SET, itemId));
			return true;
		}
		return false;
	}

	/* The two sets. Starmetal: a rune weapon re-faced at Gatehold's anvil (Smithing 60, after Heartforged). Voidsteel: a
	 * starmetal weapon (or, for the bow, a magic longbow) quenched in the dark at the Starless Gate (Smithing 75, after
	 * The Hollow King). Both take as much metal as the rune weapon took bars, give or take. */

	static final int STARMETAL_SMITHING = 60, VOIDSTEEL_SMITHING = 75, LAST_LIGHT_SHARDS = 5, LAST_LIGHT_KILLS = 25;
	private static final int MAGIC_LONGBOW = 859;
	/** The rune weapon each starmetal piece is re-faced from, in set order. */
	static final int[] RUNE_BASES = { 1213, 1289, 1303, 1333, 1432, 1347, 1373, 1319, 1247, 3202 };
	/** Starmetal (for the starmetal set) or Starless shards (for voidsteel) per piece, in set order (the 11th is the bow). */
	static final int[] SET_METAL = { 1, 1, 2, 2, 1, 3, 3, 3, 1, 3, 3 };

	private static void forgeStarmetal(Player p, int i) {
		if (p.voidForge < Heartforged.COMPLETE) {
			p.getPacketSender().sendMessage("Dorrin shakes his head. \"Starmetal over rune? Not till I've seen you work a heart-forge.\"");
			return;
		}
		if (level(p, Constants.SMITHING) < STARMETAL_SMITHING) {
			p.getPacketSender().sendMessage("You need a Smithing level of " + STARMETAL_SMITHING + " to face rune with starmetal.");
			return;
		}
		int need = SET_METAL[i];
		if (!p.getItemAssistant().playerHasItem(HAMMER) || !p.getItemAssistant().playerHasItem(STARMETAL, need)) {
			p.getPacketSender().sendMessage("You need a hammer and " + need + " starmetal for that.");
			return;
		}
		p.getItemAssistant().deleteItem(RUNE_BASES[i], 1);
		p.getItemAssistant().deleteItem(STARMETAL, need);
		p.getItemAssistant().addItem(STARMETAL_SET[i], 1);
		p.startAnimation(898);
		p.gfx0(GFX_STARFALL_MARK);
		p.getPlayerAssistant().addSkillXP(400 + 300 * need, Constants.SMITHING);
		p.getPacketSender().sendMessage("The starmetal flows over the rune like water and sets bright. It rings for a long time.");
	}

	private static void quench(Player p, int i) {
		if (p.voidKing < TheHollowKing.COMPLETE) {
			p.getPacketSender().sendMessage("The gate is shut. Whatever's behind it isn't interested yet.");
			return;
		}
		if (level(p, Constants.SMITHING) < VOIDSTEEL_SMITHING) {
			p.getPacketSender().sendMessage("You need a Smithing level of " + VOIDSTEEL_SMITHING + " to quench metal in the dark.");
			return;
		}
		int need = SET_METAL[i];
		if (!p.getItemAssistant().playerHasItem(STARLESS_SHARD, need)) {
			p.getPacketSender().sendMessage("The dark wants " + need + " Starless shard" + (need == 1 ? "" : "s") + " to bite into it.");
			return;
		}
		p.getItemAssistant().deleteItem(i == 10 ? MAGIC_LONGBOW : STARMETAL_SET[i], 1);
		p.getItemAssistant().deleteItem(STARLESS_SHARD, need);
		p.getItemAssistant().addItem(VOIDSTEEL_SET[i], 1);
		p.startAnimation(827);
		p.gfx100(GFX_VOID_IMPACT);
		p.getPlayerAssistant().addSkillXP(1500 + 600 * need, Constants.SMITHING);
		p.getPacketSender().sendMessage("You push it through the gate. Something on the other side bites, and lets go. It comes back voidsteel.");
	}

	/** The Starless longsword + 5 Starless shards, at the Starless Gate, after 25 kills: Last Light. */
	private static void forgeLastLight(Player p) {
		if (p.starlessKills < LAST_LIGHT_KILLS) {
			p.getPacketSender().sendMessage("The sword's eye looks at the gate, then at you. You haven't put out enough of the dark yet. ("
					+ p.starlessKills + "/" + LAST_LIGHT_KILLS + " Starless kills)");
			return;
		}
		if (!p.getItemAssistant().playerHasItem(STARLESS_SHARD, LAST_LIGHT_SHARDS)) {
			p.getPacketSender().sendMessage("The eye wants " + LAST_LIGHT_SHARDS + " Starless shards: one for every star it remembers.");
			return;
		}
		p.getItemAssistant().deleteItem(STARLESS_LONGSWORD, 1);
		p.getItemAssistant().deleteItem(STARLESS_SHARD, LAST_LIGHT_SHARDS);
		p.getItemAssistant().addItem(LAST_LIGHT_2H, 1);
		p.startAnimation(827);
		p.gfx0(GFX_ECLIPSE);
		p.getPacketSender().sendMessage("@red@The eye closes. The blade splits, and through the split comes the last light there ever was.");
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

	/** Barrowstone warhammer + 2 starmetal at Gatehold's anvil (a hammer, Smithing 55): the Gravestar warhammer. */
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
		p.getItemAssistant().addItem(GRAVESTAR_WARHAMMER, 1);
		p.startAnimation(898);
		p.gfx0(GFX_STARFALL_MARK);
		p.getPlayerAssistant().addSkillXP(2200, Constants.SMITHING);
		p.getPacketSender().sendMessage("You beat the starmetal over the old grave-stone. It rings, and keeps ringing: a Gravestar warhammer.");
	}

	/* ====================================================================== */
	/*                                 ADMIN                                  */
	/* ====================================================================== */

	/** ::vt sets 1|2 - the starmetal (1) or voidsteel (2) set into the inventory, for testing (10 or 11 free slots). */
	static void spawnSet(Player p, int which) {
		int[] set = which == 2 ? VOIDSTEEL_SET : STARMETAL_SET;
		int n = 0;
		for (int id : set) {
			if (p.getItemAssistant().addItem(id, 1)) {
				n++;
			}
		}
		p.getPacketSender().sendMessage((which == 2 ? "Voidsteel" : "Starmetal") + " set: " + n + " added to your inventory.");
	}

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
