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
	/* Hold the Gate, Ashes of the Crown's coffer guard, Heartforged, The Hollow King, and the Starless */
	public static final int HOLLOW_RAVAGER = 3883, THANE_OATHSWORN = 3884, RIMEBOUND_SENTINEL = 3885, SIR_VAELRIC = 3886,
			HOLLOW_KNIGHT = 3887, AUREL_SPIRIT = 3888, THE_STARLESS = 3889, STARLESS_SPAWN = 3890;
	/* The five kit-wearers: each wears one of the middle armour sets and carries its matching weapon (VoidLoot.KITS) */
	public static final int SILKWOOD_STALKER = 3891, SCAR_REAVER = 3892, MOONMOURNER = 3893, RIMEBOUND_WARRIOR = 3894, COVE_NIGHTRUNNER = 3895;
	/* the Shardback, a crater beast with its own model and animations (tools/mapgen/Shardback): defined, not yet placed */
	public static final int SHARDBACK = 4000;
	/* the Gravecoil, a barrow serpent with its own model and animations (tools/mapgen/Gravecoil): defined, not yet placed */
	public static final int GRAVECOIL = 4001;

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
	/* Hold the Gate, Heartforged, The Hollow King, and the Starless's uniques */
	public static final int WARDEN_KITESHIELD = 7977, GATE_SPIKES = 7978, STARMETAL = 7979, RIMEHEART = 7980, EMBERBRAND = 7981,
			CROWN_OF_AUREL = 7982, STARLESS_CAPE = 7983, ECLIPSE_AMULET = 7984;
	/* the Gatehold Warden set (Def 30): the kiteshield above, the rest from Dorrin's armoury and the Hollowed */
	public static final int WARDEN_HELM = 7985, WARDEN_BODY = 7986, WARDEN_LEGS = 7987, WARDEN_BOOTS = 7988,
			WARDEN_GAUNTLETS = 7989;
	/* Aurel's Royal Guard (Def 50): drops in the Starless Expanse, or reforged from rune by Dorrin */
	public static final int ROYAL_HELM = 7990, ROYAL_BODY = 7991, ROYAL_LEGS = 7992, ROYAL_KITESHIELD = 7993, ROYAL_BOOTS = 7994,
			ROYAL_GAUNTLETS = 7995;
	/* the saga's weapons */
	public static final int STARFALL_HALBERD = 7996, RIMECLEAVER = 7997, KINGS_SCEPTRE = 7998, DUSKYEW_SHORTBOW = 7999,
			CAGED_STAR_STAFF = 8000;
	/* the weapon ladder (VoidArsenal): twenty-five weapons, each a step past the last, in rank order */
	public static final int GUTTING_DAGGER = 8001, FELLING_AXE = 8002, PILGRIM_STAFF = 8003, MILITIA_SPEAR = 8004,
			WOLFBONE_SHORTBOW = 8005, WATCHMAN_SWORD = 8006, STEELCAP_MACE = 8007, SMUGGLER_SCIMITAR = 8008,
			BARROWSTONE_WARHAMMER = 8009, SILKWOOD_WAND = 8010, GATEHOLD_LONGSWORD = 8011, WARDEN_HALBERD = 8012,
			WARDEN_LONGBOW = 8013, SCARGLASS_DAGGER = 8014, OATHSWORN_BATTLEAXE = 8015, FROSTFANG_SPEAR = 8016,
			GRAVEWARDEN_2H = 8017, HOLLOWHEART_SHORTBOW = 8018, SCAR_WAND = 8019, GRAVESTAR_WARHAMMER = 8020,
			RIFTGLASS_HALBERD = 8021, RIMEHEART_STAFF = 8022, NIGHTGLASS_SCIMITAR = 8023, CROWNBREAKER_MACE = 8024,
			STARLESS_LONGSWORD = 8025;
	/* the ladder's last five, beyond the Starless (Attack/Ranged/Magic 82-99) */
	public static final int EVENTIDE_LONGBOW = 8026, SUNSPEAR = 8027, FIRST_STAR_STAFF = 8028, KINGSBANE_BATTLEAXE = 8029,
			LAST_LIGHT_2H = 8030;
	/** A shard of the Starless itself: it drops one every kill. Quenches starmetal into voidsteel, and feeds the Last Light. */
	public static final int STARLESS_SHARD = 8031;
	/** The two sets' pieces that the combat code names directly (switch cases need constants). */
	public static final int STARMETAL_DAGGER = 8032, VOIDSTEEL_DAGGER = 8042, VOIDSTEEL_LONGBOW = 8052;
	/*
	 * Two weapon tiers, each a full family like rune and dragon, in the same order: dagger, sword, longsword, scimitar,
	 * mace, warhammer, battleaxe, 2h sword, spear, halberd (and voidsteel adds a longbow). Starmetal (Attack 50) sits
	 * between rune and dragon; voidsteel (Attack 70) above dragon.
	 */
	public static final int[] STARMETAL_SET = { 8032, 8033, 8034, 8035, 8036, 8037, 8038, 8039, 8040, 8041 };
	public static final int[] VOIDSTEEL_SET = { 8042, 8043, 8044, 8045, 8046, 8047, 8048, 8049, 8050, 8051, 8052 };
	/*
	 * The god tier (ladder ranks 31-45, Attack/Ranged/Magic 85-99): weapons of the world's powers - the First Star, the
	 * Starless, the void and the Scar, Aurel's heart-fire, the Hollow King, the Wardens' founders, the old crown.
	 */
	public static final int WARDENSOATH_2H = 8053, HEART_OF_AUREL = 8054, SCARMAW_HALBERD = 8055, HOLLOW_CROWN_WARHAMMER = 8056,
			DAWNBRINGER_LONGBOW = 8057, UNMADE_WAND = 8058, FIRST_STAR_SPEAR = 8059, CROWNFIRE_SCIMITAR = 8060,
			GATEBREAKER_BATTLEAXE = 8061, NIGHTFALL_SHORTBOW = 8062, RIFTCALLER_STAFF = 8063, SUNFALL_2H = 8064,
			BLACK_SUN_WARHAMMER = 8065, CROWN_OF_STARS_STAFF = 8066, GODSFALL_2H = 8067;
	public static final int[] GOD_TIER = { WARDENSOATH_2H, HEART_OF_AUREL, SCARMAW_HALBERD, HOLLOW_CROWN_WARHAMMER,
			DAWNBRINGER_LONGBOW, UNMADE_WAND, FIRST_STAR_SPEAR, CROWNFIRE_SCIMITAR, GATEBREAKER_BATTLEAXE, NIGHTFALL_SHORTBOW,
			RIFTCALLER_STAFF, SUNFALL_2H, BLACK_SUN_WARHAMMER, CROWN_OF_STARS_STAFF, GODSFALL_2H };
	/*
	 * Six armour sets (ArmourSets / VoidArmoury), each in slot order: helm, body, legs, shield, boots, gloves (-1 where a
	 * set has no shield). Oathsworn plate (Defence 40), Duskstalker hide (Ranged 50), Scarweave robes (Magic 60),
	 * starmetal plate (Defence 60), voidsteel plate (Defence 75), Heartfire regalia (Defence 90).
	 */
	public static final int[] OATHSWORN_SET = { 8068, 8069, 8070, 8071, 8072, 8073 };
	public static final int[] DUSKSTALKER_SET = { 8074, 8075, 8076, -1, 8077, 8078 };
	public static final int[] SCARWEAVE_SET = { 8079, 8080, 8081, -1, 8082, 8083 };
	public static final int[] STARMETAL_ARMOUR = { 8084, 8085, 8086, 8087, 8088, 8089 };
	public static final int[] VOIDSTEEL_ARMOUR = { 8090, 8091, 8092, 8093, 8094, 8095 };
	public static final int[] HEARTFIRE_SET = { 8096, 8097, 8098, 8099, 8100, 8101 };
	/*
	 * Eight low sets (LowArmour / VoidArmoury), levels 10-40: Gatehold recruit's chain (Defence 10), Woodsman's leathers
	 * (Ranged 15), Croftwise robes (Magic 20), Rattlebone scrap (Defence 25), Blackbrine leathers (Ranged 30), Warden
	 * initiate's plate (Defence 30), Barrowbronze (Defence 35), Veilsworn robes (Magic 40).
	 */
	public static final int[] RECRUIT_SET = { 8102, 8103, 8104, 8105, 8106, 8107 };
	public static final int[] WOODSMAN_SET = { 8108, 8109, 8110, -1, 8111, 8112 };
	public static final int[] CROFTWISE_SET = { 8113, 8114, 8115, -1, 8116, 8117 };
	public static final int[] RATTLEBONE_SET = { 8118, 8119, 8120, 8121, 8122, 8123 };
	public static final int[] BLACKBRINE_SET = { 8124, 8125, 8126, -1, 8127, 8128 };
	public static final int[] INITIATE_SET = { 8129, 8130, 8131, 8132, 8133, 8134 };
	public static final int[] BARROWBRONZE_SET = { 8135, 8136, 8137, 8138, 8139, 8140 };
	public static final int[] VEILSWORN_SET = { 8141, 8142, 8143, -1, 8144, 8145 };
	/*
	 * Ten middle sets (MidArmour / VoidArmoury), levels 20-60: Quarryhand (Defence 20), Silkspinner (Ranged 25),
	 * Stonecaller (Magic 30), Ridgerunner (Ranged 35), Ravager (Defence 40), Moonmourn (Magic 45), Rimebound (Defence 50),
	 * Nightrunner (Ranged 50), Farseer (Magic 55), Last knight (Defence 60).
	 */
	public static final int[] QUARRYHAND_SET = { 8146, 8147, 8148, 8149, 8150, 8151 };
	public static final int[] SILKSPINNER_SET = { 8152, 8153, 8154, -1, 8155, 8156 };
	public static final int[] STONECALLER_SET = { 8157, 8158, 8159, -1, 8160, 8161 };
	public static final int[] RIDGERUNNER_SET = { 8162, 8163, 8164, -1, 8165, 8166 };
	public static final int[] RAVAGER_SET = { 8167, 8168, 8169, 8170, 8171, 8172 };
	public static final int[] MOONMOURN_SET = { 8173, 8174, 8175, -1, 8176, 8177 };
	public static final int[] RIMEBOUND_SET = { 8178, 8179, 8180, 8181, 8182, 8183 };
	public static final int[] NIGHTRUNNER_SET = { 8184, 8185, 8186, -1, 8187, 8188 };
	public static final int[] FARSEER_SET = { 8189, 8190, 8191, -1, 8192, 8193 };
	public static final int[] LAST_KNIGHT_SET = { 8194, 8195, 8196, 8197, 8198, 8199 };
	public static final int[][] ARMOUR_SETS = { OATHSWORN_SET, DUSKSTALKER_SET, SCARWEAVE_SET, STARMETAL_ARMOUR, VOIDSTEEL_ARMOUR, HEARTFIRE_SET,
			RECRUIT_SET, WOODSMAN_SET, CROFTWISE_SET, RATTLEBONE_SET, BLACKBRINE_SET, INITIATE_SET, BARROWBRONZE_SET, VEILSWORN_SET,
			QUARRYHAND_SET, SILKSPINNER_SET, STONECALLER_SET, RIDGERUNNER_SET, RAVAGER_SET, MOONMOURN_SET, RIMEBOUND_SET, NIGHTRUNNER_SET,
			FARSEER_SET, LAST_KNIGHT_SET };
	/* The kit-wearers' weapons: Silkstring shortbow (Ranged 25), Ravager battleaxe (Attack 40), Moonmourn staff (Magic 45),
	 * Rimebound warhammer (Attack 50), Nightrunner shortbow (Ranged 50) */
	public static final int SILKSTRING_SHORTBOW = 8200, RAVAGER_BATTLEAXE = 8201, MOONMOURN_STAFF = 8202, RIMEBOUND_WARHAMMER = 8203,
			NIGHTRUNNER_SHORTBOW = 8204;
	public static final int[] KIT_WEAPONS = { SILKSTRING_SHORTBOW, RAVAGER_BATTLEAXE, MOONMOURN_STAFF, RIMEBOUND_WARHAMMER, NIGHTRUNNER_SHORTBOW };
	/* The kit-wearers, in KIT_WEAPONS order, and the set each one wears */
	public static final int[] KIT_WEARERS = { SILKWOOD_STALKER, SCAR_REAVER, MOONMOURNER, RIMEBOUND_WARRIOR, COVE_NIGHTRUNNER };
	public static final int[][] KIT_SETS = { SILKSPINNER_SET, RAVAGER_SET, MOONMOURN_SET, RIMEBOUND_SET, NIGHTRUNNER_SET };
	public static final int[] LADDER = { GUTTING_DAGGER, FELLING_AXE, PILGRIM_STAFF, MILITIA_SPEAR, WOLFBONE_SHORTBOW,
			WATCHMAN_SWORD, STEELCAP_MACE, SMUGGLER_SCIMITAR, BARROWSTONE_WARHAMMER, SILKWOOD_WAND, GATEHOLD_LONGSWORD,
			WARDEN_HALBERD, WARDEN_LONGBOW, SCARGLASS_DAGGER, OATHSWORN_BATTLEAXE, FROSTFANG_SPEAR, GRAVEWARDEN_2H,
			HOLLOWHEART_SHORTBOW, SCAR_WAND, GRAVESTAR_WARHAMMER, RIFTGLASS_HALBERD, RIMEHEART_STAFF, NIGHTGLASS_SCIMITAR,
			CROWNBREAKER_MACE, STARLESS_LONGSWORD, EVENTIDE_LONGBOW, SUNSPEAR, FIRST_STAR_STAFF, KINGSBANE_BATTLEAXE, LAST_LIGHT_2H,
			WARDENSOATH_2H, HEART_OF_AUREL, SCARMAW_HALBERD, HOLLOW_CROWN_WARHAMMER, DAWNBRINGER_LONGBOW, UNMADE_WAND, FIRST_STAR_SPEAR,
			CROWNFIRE_SCIMITAR, GATEBREAKER_BATTLEAXE, NIGHTFALL_SHORTBOW, RIFTCALLER_STAFF, SUNFALL_2H, BLACK_SUN_WARHAMMER,
			CROWN_OF_STARS_STAFF, GODSFALL_2H };
	// (the next new item is 8205; the next new npc 3896)
	public static final int[] WARDEN_SET = { WARDEN_HELM, WARDEN_BODY, WARDEN_LEGS, WARDEN_BOOTS, WARDEN_GAUNTLETS };
	public static final int[] ROYAL_SET = { ROYAL_HELM, ROYAL_BODY, ROYAL_LEGS, ROYAL_KITESHIELD, ROYAL_BOOTS, ROYAL_GAUNTLETS };

	/* ---------------- graphics (new spotanims, appended after the cache's 666) ---------------- */
	public static final int GFX_VOID_BOLT = 666, GFX_VOID_IMPACT = 667, GFX_HEARTFIRE = 668, GFX_STARFALL_MARK = 669,
			GFX_FALLING_STAR = 670, GFX_ECLIPSE = 671, GFX_RIME_BURST = 672;

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
	/* the later quests and the Starless (all with hand-built models) */
	public static final int BROKEN_BARRICADE = 15010, SPIKED_BARRICADE = 15011, FALLEN_STAR = 15012, RIME_ALTAR = 15013,
			STARLESS_GATE = 15014, STAR_PILLAR = 15015, STAR_PILLAR_LIT = 15016;
	// (15017 is Skullbrine's dormant portal, SkullbrineConstants.VOID_PORTAL)
	/* scenery with hand-built models (tools.mapgen.WorldProps): camp furniture, flags, the old circle and the Gate */
	public static final int CAMP_TENT = 15018, FIRE_RING = 15019, COOK_FIRE_RING = 15020, LOG_SEAT = 15021, GATEHOLD_BANNER = 15022,
			MENHIR = 15023, MENHIR_2 = 15024, MENHIR_3 = 15025, GATE_TOWER = 15026, GATE_TOWER_BROKEN = 15027, FALLEN_MASONRY = 15028,
			PIRATE_FLAG = 15029, GATE_WALL = 15030, GATE_WALL_LOW = 15031;
	/** Skull Rock's sculpted face, in pieces: 15032 to 15079 (SkullRock.MAX_PIECES ids, all defined). */
	public static final int SKULL_ROCK = 15032;
	// (the next new object is 15080)

	/* ---------------- Hold the Gate ---------------- */
	/** The three broken barricades across the Scar road north-east of the Gate. */
	public static final int[][] BARRICADE_SPOTS = { { 1520, 2711 }, { 1521, 2713 }, { 1522, 2715 } };
	/** Where the raid comes from (up the Scar road), and where the defenders stand. */
	public static final int RAID_FROM_X = 1528, RAID_FROM_Y = 2721, RAID_HOLD_X = 1518, RAID_HOLD_Y = 2712;

	/* ---------------- Heartforged ---------------- */
	/** The fallen star in Starfall Crater; the rime altar deep in the Frostfang Caves (local tiles of its region). */
	public static final int STAR_X = 1562, STAR_Y = 2704, ALTAR_LX = 19, ALTAR_LY = 13;

	/* ---------------- the Starless Expanse (the optional mega boss) ---------------- */
	public static final int STARLESS_REGION_X = 26, STARLESS_REGION_Y = 148;
	public static final int STARLESS_X = 1696, STARLESS_Y = 9506, STARLESS_ARRIVAL_X = 1696, STARLESS_ARRIVAL_Y = 9497;
	/** The four star pillars round the Expanse, and the gate home. */
	public static final int[][] STAR_PILLARS = { { 1689, 9499 }, { 1703, 9499 }, { 1703, 9513 }, { 1689, 9513 } };
	public static final int EXPANSE_GATE_X = 1696, EXPANSE_GATE_Y = 9494;
	/** The Starless Gate on the void platform (shown once the Hollow King has fallen). */
	public static final int ARENA_GATE_X = 1568, ARENA_GATE_Y = 9511;

	public static boolean inExpanse(int x, int y) {
		return x >= STARLESS_REGION_X * 64 && x < STARLESS_REGION_X * 64 + 64 && y >= STARLESS_REGION_Y * 64
				&& y < STARLESS_REGION_Y * 64 + 64;
	}

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
	/**
	 * Starfall Crater, where you land, with the rift your fall tore open in its middle: a great scorched bowl near the
	 * heart of the land, its west lip a short walk from the Broken Gate along the old stone road.
	 */
	public static final int CRATER_X = 1556, CRATER_Y = 2702, CRATER_RADIUS = 13;
	public static final int RIFT_X = 1555, RIFT_Y = 2702;
	public static final int LANDING_X = 1556, LANDING_Y = 2698;
	/** The old kingdom's beacon on the east headland, where a fire once answered the Gate's: fallen, left to the gulls. */
	public static final int BEACON_X = 1607, BEACON_Y = 2668;
	/** Oren's camp on the crater's north-west rim, where the stone road leaves it. */
	public static final int OREN_X = 1541, OREN_Y = 2708;

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
	public static final int WARDEN_SHOP = 357;
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
	public static final int[][] WAYSTONE_SPOTS = { { 1504, 2712 }, { 1538, 2700 }, { 1490, 2803 }, { 1476, 2624 }, { 1569, 2737 } };

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
