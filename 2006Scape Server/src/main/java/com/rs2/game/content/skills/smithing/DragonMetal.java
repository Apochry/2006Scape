package com.rs2.game.content.skills.smithing;

/**
 * Dragon ore, dragon bars and dragon smithing.
 *
 * The ore, bar and rock are new definitions that {@code CursedIslandGenerator} adds to the cache (runite
 * recoloured blood red). The rocks are in the Dragonvein cavern, west of the portal chamber in
 * Blackbrine's Hold under Skullbrine Isle. Dragon levels are not scaled by
 * {@link com.rs2.Constants#MINING_SMITHING_LEVEL_SCALE}: they sit just above rune (ore and bars 50, items 51-59).
 */
public final class DragonMetal {

	private DragonMetal() {
	}

	/* new definitions (generator) */
	public static final int DRAGON_ORE = 7959;
	public static final int DRAGON_BAR = 7960;
	public static final int DRAGON_ROCK = 14985;

	public static final int MINING_LEVEL = 60;

	/* smelting: 1 dragon ore + 4 coal at any furnace */
	public static final int SMELT_LEVEL = 60;
	public static final int SMELT_XP = 65;
	public static final int COAL_NEEDED = 4;
	/** Index of the dragon bar in {@link Smelting#data}. */
	public static final int SMELT_INDEX = 9;

	/** Dragon items that can be smithed, as {item id, column interface, row, bars, level, xp}. */
	public static final int[][] ITEMS = {
			{ 1215, 1119, 0, 1, 60, 100 }, // dagger
			{ 4587, 1119, 2, 2, 64, 200 }, // scimitar
			{ 1305, 1119, 3, 2, 65, 200 }, // longsword
			{ 7158, 1119, 4, 3, 70, 300 }, // 2h sword
			{ 6739, 1120, 0, 1, 61, 100 }, // axe
			{ 1434, 1120, 1, 1, 62, 100 }, // mace
			{ 1377, 1120, 3, 3, 68, 300 }, // battleaxe
			{ 3140, 1121, 0, 3, 69, 300 }, // chainbody
			{ 4087, 1121, 1, 3, 72, 300 }, // platelegs
			{ 4585, 1121, 2, 3, 72, 300 }, // plateskirt
			{ 1149, 1122, 0, 1, 63, 100 }, // med helm
			{ 1187, 1122, 2, 2, 66, 200 }, // sq shield
	};

	public static int[] forItem(int itemId) {
		for (int[] item : ITEMS) {
			if (item[0] == itemId) {
				return item;
			}
		}
		return null;
	}
}
