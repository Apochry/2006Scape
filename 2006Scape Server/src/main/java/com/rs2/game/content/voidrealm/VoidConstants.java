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
	/* the people and creatures of Nythera */
	public static final int BRAM = 3863, MARTA = 3864, HOB = 3865, KAEL = 3866, PILGRIM = 3867, HOLLOWED_WOLF = 3868,
			HOLLOW_HUSK = 3869;
	/* Gatehold, the quarry, the Last Hearth and Smugglers' Cove */
	public static final int YSOLDE = 3870, PELL = 3871, DORRIN = 3872, TESS = 3873, MORRA = 3874, NETTLE = 3875;
	/* dungeon bosses */
	public static final int BARROW_THANE = 3876, HOLLOW_MATRIARCH = 3877, RIMECLAW = 3878;
	/* Ashes of the Crown */
	public static final int MAELIS = 3879, BROODMOTHER = 3880, HOLLOW_HERALD = 3881, HOLLOW_SHADE = 3882;

	/* ---------------- items (new definitions) ---------------- */
	public static final int EYE_SPLINTER = 7961;
	public static final int GRAVE_SPLINTER = 7962;
	public static final int VEIN_SPLINTER = 7963;
	public static final int VOID_SIGIL = 7964;
	public static final int SEERS_LANTERN = 7965;
	public static final int HOLLOW_CAPE = 7966;
	public static final int HOLLOWFANG = 7967;
	/* boss drops */
	public static final int HOLLOWHEART_AMULET = 7968, BARROWSTEEL_LONGSWORD = 7969, FROSTBOUND_BOOTS = 7970;
	/* Ashes of the Crown */
	public static final int THANE_PAGE = 7971, SILK_PAGE = 7972, NETTLE_PAGE = 7973, ROYAL_CHRONICLE = 7974, EMBER = 7975,
			GATEHOLD_SIGNET = 7976;

	/* ---------------- objects (new definitions) ---------------- */
	public static final int VOID_VEIN = 14986;
	public static final int EPITAPH_GRAVE = 14987;
	public static final int VOID_CRYSTAL = 14988;
	public static final int VOID_CRYSTAL_DARK = 14989;
	public static final int EYE_FIRE = 14990;
	/** The violet rift torn open in Starfall Crater. */
	public static final int RIFT = 14991;
	/** Dungeon entrances on Nythera (each one's exit inside is the ordinary cave exit, 3736). */
	public static final int BARROW_DOOR = 14992, HOLLOW_FISSURE = 14993, FROST_CAVE = 14994;
	/** Scenery with hand-built models (tools/mapgen/ModelForge). */
	public static final int WAYSTONE = 14995, VOID_LANTERN = 14996, HOLLOWED_TREE = 14997;
	/* Ashes of the Crown */
	public static final int THANE_COFFER = 14998, SILK_COCOON = 14999, VAULT_HATCH = 15000, SUN_WARD = 15001, STAR_WARD = 15002,
			MOON_WARD = 15003, VAULT_SEAL = 15004, EMBER_PEDESTAL = 15005, MAELIS_BOOKS = 15006;
	/** The wards once lit (shown to the player who lit them). */
	public static final int SUN_WARD_LIT = 15007, STAR_WARD_LIT = 15008, MOON_WARD_LIT = 15009;

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

	/* camps (scenery in VoidRealmGenerator#camps, people in spawns.json) */
	/** Netwright's landing: Bram's fishing camp on the lake's east shore. */
	public static final int FISH_CAMP_X = 1459, FISH_CAMP_Y = 2712;
	/** Tollan's Croft: Marta's farm on the hills between the crater and the Broken Gate. */
	public static final int CROFT_X = 1522, CROFT_Y = 2662;
	/** Hob's woodcutting camp on the edge of the Greenreach forest. */
	public static final int WOOD_CAMP_X = 1446, WOOD_CAMP_Y = 2652;
	/** The Scar Watch: Warden Kael's lookout on the western edge of the Hollow Scar. */
	public static final int WATCH_X = 1574, WATCH_Y = 2741;
	/** Where the old roads meet at the Broken Gate. */
	public static final int GATE_X = 1510, GATE_Y = 2712;

	/** Shops in data/cfg/shops.json. */
	public static final int BRAM_SHOP = 350, MARTA_SHOP = 351, PELL_SHOP = 352, DORRIN_SHOP = 353, TESS_SHOP = 354,
			MORRA_SHOP = 355, NETTLE_SHOP = 356;

	/** Gatehold: the frontier outpost in the Broken Gate's shadow (bank, store, smithy). Square centre. */
	public static final int GATEHOLD_X = 1497, GATEHOLD_Y = 2713;
	/** Tess's quarry in the northern foothills. */
	public static final int QUARRY_X = 1532, QUARRY_Y = 2765;
	/** The Last Hearth, the inn where the north road comes down off the pass. */
	public static final int INN_X = 1483, INN_Y = 2808;
	/** Smugglers' Cove on the south coast. */
	public static final int SMUGGLER_COVE_X = 1470, SMUGGLER_COVE_Y = 2619;
	/** Silkwood: magic trees in the western forest, spun over by giant spiders. Over the river from Hob's camp. */
	public static final int SILKWOOD_X = 1390, SILKWOOD_Y = 2668;

	/* ---------------- Nythera's dungeons ---------------- */
	/** The Sunken Barrow: an old king's tomb in the hills west of the standing stones. */
	public static final int BARROW_DOOR_X = 1478, BARROW_DOOR_Y = 2739;
	/** Hollowdeep: the Scar's heart, a fissure down into void-eaten caves. */
	public static final int FISSURE_X = 1601, FISSURE_Y = 2742;
	/** Frostfang Caves: ice caverns in the mountain beside the pass. */
	public static final int FROST_CAVE_X = 1489, FROST_CAVE_Y = 2789;

	/** Region x of each dungeon (all at region y 151), and where you arrive inside (local 32, 9). */
	public static final int BARROW_REGION_X = 20, HOLLOWDEEP_REGION_X = 22, FROSTFANG_REGION_X = 24, NY_DUNGEON_REGION_Y = 151;
	/** The Vault beneath the Gate (Ashes of the Crown), also at region y 151. */
	public static final int VAULT_REGION_X = 26;
	/** Vault layout, in local tiles of its region: the three wards, the seal across the passage, the Ember, the Herald. */
	public static final int[][] WARD_SPOTS = { { 25, 26 }, { 32, 30 }, { 39, 26 } }; // sun, star, moon
	public static final int SEAL_LX = 32, SEAL_LY = 37, EMBER_LX = 32, EMBER_LY = 54, HERALD_LX = 32, HERALD_LY = 49;
	/** The hatch into the vault, among the Broken Gate's rubble; Archivist Maelis's camp in the standing stones. */
	public static final int HATCH_X = 1512, HATCH_Y = 2714, MAELIS_X = 1502, MAELIS_Y = 2738;
	/** Waystones (the network Ashes of the Crown opens): name and the tile beside each stone you arrive on. */
	public static final String[] WAYSTONE_NAMES = { "Gatehold", "Starfall Crater", "The Last Hearth", "Smugglers' Cove",
			"The Scar Watch" };
	public static final int[][] WAYSTONE_SPOTS = { { 1504, 2712 }, { 1566, 2664 }, { 1490, 2803 }, { 1476, 2624 }, { 1569, 2737 } };

	public static int dungeonBaseX(int regionX) {
		return regionX * 64;
	}

	public static final int DUNGEON_BASE_Y = NY_DUNGEON_REGION_Y * 64;

	/** Which Nythera dungeon region a tile is in (BARROW_REGION_X etc.), or -1. */
	public static int nytheraDungeon(int x, int y) {
		if (y < DUNGEON_BASE_Y || y >= DUNGEON_BASE_Y + 64) {
			return -1;
		}
		int rx = x >> 6;
		return rx == BARROW_REGION_X || rx == HOLLOWDEEP_REGION_X || rx == FROSTFANG_REGION_X || rx == VAULT_REGION_X ? rx : -1;
	}

	public static boolean onNythera(int x, int y) {
		return x >= NYTHERA_REGION_X0 * 64 && x < (NYTHERA_REGION_X0 + NYTHERA_REGIONS) * 64 && y >= NYTHERA_REGION_Y0 * 64
				&& y < (NYTHERA_REGION_Y0 + NYTHERA_REGIONS) * 64;
	}
}
