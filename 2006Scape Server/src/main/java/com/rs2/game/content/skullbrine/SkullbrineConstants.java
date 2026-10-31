package com.rs2.game.content.skullbrine;

/**
 * Ids and coordinates for Skullbrine Isle, shared by the map/cache generator
 * ({@code com.rs2.tools.mapgen.CursedIslandGenerator}) and the server-side content.
 *
 * NPC and item ids above the original cache's last id are new definitions that the generator appends
 * to the cache (cloned from existing ones with new names and recolours).
 */
public final class SkullbrineConstants {

	private SkullbrineConstants() {
	}

	/* ---------------- NPCs (new definitions) ---------------- */
	public static final int BOSUN_GRIMSBY = 3852;
	public static final int MAD_PETE = 3853;
	public static final int CURSED_PIRATE = 3854;
	public static final int DROWNED_DECKHAND = 3855;
	public static final int CURSED_FIRST_MATE = 3856;
	public static final int CAPTAIN_BLACKBRINE = 3857;

	/* ---------------- items (new definitions) ---------------- */
	public static final int BLACKBRINES_LOG = 7956;
	public static final int CAPTAINS_KEY = 7957;
	public static final int VOID_PEARL = 7958;

	/* ---------------- objects (placed by the generator) ---------------- */
	public static final int CAVE_ENTRANCE = 3735;
	public static final int CAVE_EXIT = 3736;
	public static final int CAPTAINS_CHEST = 375;
	public static final int CAPTAINS_SKELETON = 6893;
	/** The dormant portal in the smugglers' cave: a copy of the stock portal (6282) re-made in the void's colours. */
	public static final int VOID_PORTAL = 15017, STOCK_PORTAL = 6282;

	/* ---------------- places ---------------- */

	/** Where the bosun drops you off on the island jetty. */
	public static final int DOCK_X = 2016, DOCK_Y = 2879;
	/** Where you land back at Port Sarim. */
	public static final int PORT_SARIM_X = 3029, PORT_SARIM_Y = 3216;

	public static final int CAVE_ENTRANCE_X = 2015, CAVE_ENTRANCE_Y = 2928;
	/** Where you appear on the island after leaving the dungeon (just south of the cave mouth). */
	public static final int CAVE_OUTSIDE_X = 2016, CAVE_OUTSIDE_Y = 2926;

	public static final int DUNGEON_EXIT_X = 2015, DUNGEON_EXIT_Y = 9282;
	/** Where you appear in the dungeon after entering the cave. */
	public static final int DUNGEON_ARRIVAL_X = 2016, DUNGEON_ARRIVAL_Y = 9288;

	public static final int CAPTAINS_CHEST_X = 2034, CAPTAINS_CHEST_Y = 9316;
	public static final int CAPTAINS_SKELETON_X = 2008, CAPTAINS_SKELETON_Y = 2889;
	public static final int PORTAL_X = 2015, PORTAL_Y = 9335;

	/** Bounds of the island (surface) and the dungeon, for area checks. */
	public static boolean onIsland(int x, int y) {
		return x >= 1920 && x < 2112 && y >= 2816 && y < 3008;
	}

	public static boolean inDungeon(int x, int y) {
		return x >= 1984 && x < 2048 && y >= 9280 && y < 9344;
	}
}
