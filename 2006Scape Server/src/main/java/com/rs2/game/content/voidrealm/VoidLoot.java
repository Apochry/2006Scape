package com.rs2.game.content.voidrealm;

import static com.rs2.game.content.voidrealm.VoidConstants.*;

import com.rs2.GameEngine;
import com.rs2.game.npcs.NpcHandler;
import com.rs2.game.players.Player;
import com.rs2.util.Misc;

/**
 * Nythera's loot: every ordinary monster on the continent (and in its dungeons) drops by one shared philosophy,
 * instead of the stock tables' long tail of 1-in-N rolls.
 *
 * <pre>
 *   always   its bones (or ashes, or a unicorn's horn): whatever the monster's own table always drops
 *   40%      nothing else - just the bones
 *   30%      something ordinary   (coins, logs, arrows, raw fish, ore, flax...)
 *   20%      something useful     (runes, herbs, food, potions, better ore and bars)
 *    5%      something valuable   (uncut gems, starmetal, a fat purse)
 *    5%      gear around the monster's level (including the Warden's armour, for the middle band)
 *   +1%      an independent bonus roll: gear that's unusually good for the monster's level
 * </pre>
 *
 * Tables come in three bands by combat level: under 30, 30-49, and 50 and over. Bosses and quest fights keep their
 * own hand-made tables (in npcdrops.json), as do harmless animals (under combat 15).
 */
public final class VoidLoot {

	private VoidLoot() {
	}

	/** Item, min amount, max amount (amounts above 1 only for stackables). */
	private static final int[][][] MISC = {
			{ { 995, 10, 60 }, { 1511, 1, 1 }, { 592, 1, 1 }, { 884, 5, 15 }, { 335, 1, 1 }, { 1779, 1, 1 }, { 1777, 1, 1 },
					{ 2349, 1, 1 }, { 227, 1, 1 } },
			{ { 995, 40, 150 }, { 1521, 1, 1 }, { 1519, 1, 1 }, { 886, 8, 20 }, { 331, 1, 1 }, { 440, 1, 1 }, { 1779, 1, 1 },
					{ 2351, 1, 1 }, { 231, 1, 1 } },
			{ { 995, 100, 300 }, { 1517, 1, 1 }, { 888, 10, 25 }, { 377, 1, 1 }, { 453, 1, 1 }, { 2351, 1, 1 }, { 231, 1, 1 },
					{ 1519, 1, 1 } } };
	private static final int[][][] USEFUL = {
			{ { 561, 3, 8 }, { 562, 5, 12 }, { 199, 1, 1 }, { 201, 1, 1 }, { 203, 1, 1 }, { 333, 1, 1 }, { 329, 1, 1 },
					{ 440, 1, 1 }, { 121, 1, 1 }, { 115, 1, 1 } },
			{ { 561, 5, 12 }, { 563, 3, 8 }, { 564, 4, 10 }, { 205, 1, 1 }, { 207, 1, 1 }, { 209, 1, 1 }, { 379, 1, 1 },
					{ 447, 1, 1 }, { 139, 1, 1 }, { 145, 1, 1 } },
			{ { 560, 5, 15 }, { 563, 5, 12 }, { 565, 3, 8 }, { 207, 1, 1 }, { 211, 1, 1 }, { 213, 1, 1 }, { 373, 1, 1 },
					{ 449, 1, 1 }, { 2359, 1, 1 }, { 2434, 1, 1 }, { 157, 1, 1 } } };
	private static final int[][][] VALUABLE = {
			{ { 1623, 1, 1 }, { 1621, 1, 1 }, { 995, 150, 400 }, { 2357, 1, 1 } },
			{ { 1621, 1, 1 }, { 1619, 1, 1 }, { 995, 400, 1000 }, { STARMETAL, 1, 1 } },
			{ { 1619, 1, 1 }, { 1617, 1, 1 }, { 995, 1000, 2500 }, { STARMETAL, 1, 2 }, { 451, 1, 1 } } };
	private static final int[][][] GEAR = {
			{ { 1325, 1, 1 }, { 1157, 1, 1 }, { 1069, 1, 1 }, { 1193, 1, 1 }, { 1115, 1, 1 }, { 1295, 1, 1 }, { 1141, 1, 1 },
					{ 843, 1, 1 } },
			{ { 1329, 1, 1 }, { 1121, 1, 1 }, { 1145, 1, 1 }, { 1183, 1, 1 }, { 1301, 1, 1 }, { 853, 1, 1 },
					{ WARDEN_HELM, 1, 1 }, { WARDEN_BOOTS, 1, 1 }, { WARDEN_GAUNTLETS, 1, 1 } },
			{ { 1123, 1, 1 }, { 1073, 1, 1 }, { 1161, 1, 1 }, { 1199, 1, 1 }, { 1147, 1, 1 }, { 1213, 1, 1 }, { 857, 1, 1 },
					{ WARDEN_BODY, 1, 1 }, { WARDEN_LEGS, 1, 1 } } };
	private static final int[][][] BONUS = {
			{ { 1329, 1, 1 }, { 1183, 1, 1 }, { 1159, 1, 1 } },
			{ { 1333, 1, 1 }, { 1147, 1, 1 }, { 1113, 1, 1 }, { WARDEN_BODY, 1, 1 } },
			{ { 1079, 1, 1 }, { 1163, 1, 1 }, { 1215, 1, 1 }, { 1149, 1, 1 } } };

	/** Bosses and minibosses (hand-made tables), and the adds summoned in boss fights (bones only). */
	private static final int[] CURATED = { BARROW_THANE, HOLLOW_MATRIARCH, RIMECLAW, BROODMOTHER, HOLLOW_HERALD,
			RIMEBOUND_SENTINEL, SIR_VAELRIC, THE_STARLESS, STARLESS_SPAWN, HOLLOW_KNIGHT };

	/** Whether this kill uses Nythera's loot (rather than the npc's own 1-in-N drops). */
	public static boolean applies(int npcType, int x, int y) {
		if (!onNythera(x, y) && nytheraDungeon(x, y) < 0) {
			return false;
		}
		for (int c : CURATED) {
			if (c == npcType) {
				return false;
			}
		}
		return NpcHandler.getNpcListCombat(npcType) >= 15;
	}

	/** Rolls the tier (the monster's always-drops are dropped by the caller, from its own table). */
	public static void roll(Player p, int npcType, int x, int y) {
		int level = NpcHandler.getNpcListCombat(npcType);
		int band = level < 30 ? 0 : level < 50 ? 1 : 2;
		int[][][] tier = TIERS[tier()];
		if (tier != null) {
			drop(p, tier[band], x, y);
		}
		if (bonus()) {
			drop(p, BONUS[band], x, y);
		}
	}

	private static final int[][][][] TIERS = { null, MISC, USEFUL, VALUABLE, GEAR };
	static final String[] TIER_NAMES = { "just bones", "misc", "useful", "valuable", "gear" };

	/** 0 = just bones (40%), 1 = misc (30%), 2 = useful (20%), 3 = valuable (5%), 4 = gear (5%). */
	static int tier() {
		int r = Misc.random(99); // 0-99
		return r < 40 ? 0 : r < 70 ? 1 : r < 90 ? 2 : r < 95 ? 3 : 4;
	}

	/** The independent 1% bonus roll for unusually good gear. */
	static boolean bonus() {
		return Misc.random(99) == 0;
	}

	private static void drop(Player p, int[][] table, int x, int y) {
		int[] e = table[Misc.random(table.length - 1)];
		int amount = e[1] + (e[2] > e[1] ? Misc.random(e[2] - e[1]) : 0);
		GameEngine.itemHandler.createGroundItem(p, e[0], x, y, amount, p.playerId);
	}
}
