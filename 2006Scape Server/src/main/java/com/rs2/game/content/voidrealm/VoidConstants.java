package com.rs2.game.content.voidrealm;

/**
 * Ids and coordinates for the Void quest series ("Echoes of the Void", "Beyond the Veil"), the void between
 * worlds and the continent of Nythera. Shared by the map/cache generator
 * ({@code com.rs2.tools.mapgen.VoidRealmGenerator}) and the server-side content ({@link VoidRealm}).
 */
public final class VoidConstants {

	private VoidConstants() {
	}

	/* ---------------- NPCs (new definitions) ---------------- */
	public static final int SEER_ILSABET = 3858;
	public static final int HOLLOW_WRAITH = 3859;
	public static final int VOIDLING = 3860;
	public static final int HOLLOW_KING = 3861;
	public static final int OREN = 3862;

	/* ---------------- items (new definitions) ---------------- */
	public static final int EYE_SPLINTER = 7961;
	public static final int GRAVE_SPLINTER = 7962;
	public static final int VEIN_SPLINTER = 7963;
	public static final int VOID_SIGIL = 7964;
	public static final int SEERS_LANTERN = 7965;
	public static final int HOLLOW_CAPE = 7966;
	public static final int HOLLOWFANG = 7967;

	/* ---------------- objects (new definitions) ---------------- */
	public static final int VOID_VEIN = 14986;
	public static final int EPITAPH_GRAVE = 14987;
	public static final int VOID_CRYSTAL = 14988;
	public static final int VOID_CRYSTAL_DARK = 14989;
	public static final int EYE_FIRE = 14990;
	/** The violet rift torn open in Starfall Crater. */
	public static final int RIFT = 14991;

	/* ---------------- Skullbrine Isle ---------------- */

	public static final int ILSABET_X = 2023, ILSABET_Y = 2922;

	/** Ghostfire at the lower lip of each of Skull Rock's eyes. The east (right-hand) eye is the one that weeps. */
	public static final int LEFT_EYE_X = 2009, RIGHT_EYE_X = 2022, EYE_FIRE_Y = 2938;

	/** The six graves with epitaphs in the graveyard of the drowned, and what each one says. */
	public static final int[][] GRAVES = { { 1985, 2912 }, { 1991, 2912 }, { 1982, 2915 }, { 1994, 2921 }, { 1985, 2921 },
			{ 1991, 2924 } };
	public static final String[][] EPITAPHS = {
			{ "Here lies Tam 'Two-Hooks' Doyle, gunner.", "He'd have wanted a louder funeral." },
			{ "Bartholomew Finch, bosun's mate.", "Survived three shipwrecks. Drowned in a puddle." },
			{ "Old Salt Marrow, ship's cook before Pete.", "His stew killed more men than the sea ever did." },
			{ "Ned Grimsby. Sailor, brother, friend.", "Come home, Ned. - your brother" },
			{ "An unknown sailor.", "The sea gave him back without a name." },
			{ "Jory Vane, lookout of the Drowned Maiden.", "'From the crow's nest I saw it - a far shore", "under violet stars.'" } };
	/** Index in {@link #GRAVES} of Jory Vane, the lookout who saw the other shore. */
	public static final int JORY = 5;

	/* ---------------- the void between ---------------- */
	public static final int ARENA_REGION_X = 24, ARENA_REGION_Y = 148;
	public static final int ARENA_X = 1568, ARENA_Y = 9504; // centre of the platform
	public static final int ARENA_ARRIVAL_X = 1568, ARENA_ARRIVAL_Y = 9497;
	public static final int KING_X = 1566, KING_Y = 9507;

	public static boolean inArena(int x, int y) {
		return x >= ARENA_REGION_X * 64 && x < ARENA_REGION_X * 64 + 64 && y >= ARENA_REGION_Y * 64 && y < ARENA_REGION_Y * 64 + 64;
	}

	/* ---------------- Nythera ---------------- */
	public static final int NYTHERA_REGION_X0 = 20, NYTHERA_REGION_Y0 = 39, NYTHERA_REGIONS = 7;
	public static final int NYTHERA_CX = 1504, NYTHERA_CY = 2720;
	/** Starfall Crater, where you land, with the rift your fall tore open in its middle. */
	public static final int CRATER_X = 1574, CRATER_Y = 2650;
	public static final int RIFT_X = 1573, RIFT_Y = 2650;
	public static final int LANDING_X = 1574, LANDING_Y = 2646;
	/** Oren's camp on the crater's north-west rim. */
	public static final int OREN_X = 1562, OREN_Y = 2661;

	public static boolean onNythera(int x, int y) {
		return x >= NYTHERA_REGION_X0 * 64 && x < (NYTHERA_REGION_X0 + NYTHERA_REGIONS) * 64 && y >= NYTHERA_REGION_Y0 * 64
				&& y < (NYTHERA_REGION_Y0 + NYTHERA_REGIONS) * 64;
	}
}
