package com.rs2.tools.mapgen;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Generates "Skullbrine Isle" - a cursed pirate island - plus the pirate dungeon beneath it, and packs
 * both into the server cache (which the file server then hands to clients).
 *
 * Run from the "2006Scape Server" directory:
 *   java -cp target/server-1.0-jar-with-dependencies.jar com.rs2.tools.mapgen.CursedIslandGenerator [cacheDir]
 *
 * Safe to re-run: regions that already exist in map_index reuse their file ids and are overwritten.
 * Everything about the layout lives in this class, so tweak the constants/methods below and re-run.
 */
public final class CursedIslandGenerator {

	/* ---------- where things are (absolute world coordinates) ---------- */

	/** The island occupies a 3x3 block of regions so the open sea hides the edge of the world. */
	public static final int SURFACE_REGION_X = 30, SURFACE_REGION_Y = 44, SURFACE_REGIONS = 3;
	public static final int BASE_X = SURFACE_REGION_X * 64, BASE_Y = SURFACE_REGION_Y * 64; // 1920, 2816

	/** Island centre. */
	public static final int CX = 2016, CY = 2912;

	/** The dungeon sits in a single region directly "below" the island centre (y + 6400). */
	public static final int DUNGEON_REGION_X = 31, DUNGEON_REGION_Y = 145;
	public static final int DBASE_X = DUNGEON_REGION_X * 64, DBASE_Y = DUNGEON_REGION_Y * 64; // 1984, 9280

	/* ---------- floor ids (flo.dat index + 1) ---------- */
	static final int WATER = 6, WOOD = 5, SAND = 68, DARK_GRASS = 93, SWAMP = 53, MUDDY_GRASS = 49,
			DEAD_GREEN = 103, PATH = 36, MUD = 63, CAVE_FLOOR = 66, INVISIBLE = 42, ROCK = 56;

	/* ---------- object ids ---------- */
	static final int LEAFY_PALM = 2975, PALM = 1311, DEAD_TREE = 1286, BIG_DEAD_TREE = 1282, ROCKS = 5604,
			GRAVESTONE = 404, SMALL_GRAVESTONE = 400, GRAVE_MARKER = 401, LARGE_GRAVE = 406, SKELETON = 5358,
			LONG_SKELETON = 5360, SKULL_PILE = 3969, ANIMAL_SKULL = 697, HULL = 2177, MAST = 5274,
			BROKEN_BARREL = 672, ANCHOR = 9522, CRATE = 9533, CRATES = 9535, CANNON = 821, CANNONBALLS = 5967,
			SHIP_WHEEL = 252, TORCH = 5494, BARREL = 9519, STACKED_BARRELS = 4641, CHEST = 375,
			FLAGPOLE = 3680, CAVE_ENTRANCE = 3735, CAVE_EXIT = 3736, CAVE_TORCH = 724,
			STALAGMITE = 541, STALAGMITE_2 = 542, DUNGEON_SKULLS = 668, PORTAL = 6282;

	public static final int TYPE_WALL = 0, TYPE_SCENERY = 10, TYPE_GROUND_DECOR = 22;

	/* Key points that the server-side content also refers to (see com.rs2.world.island.SkullbrineIsle). */
	public static final int DOCK_ARRIVAL_X = 2061, DOCK_ARRIVAL_Y = 2912;
	public static final int CAVE_ENTRANCE_X = 2015, CAVE_ENTRANCE_Y = 2934;
	public static final int DUNGEON_ARRIVAL_X = 2016, DUNGEON_ARRIVAL_Y = 9288;
	public static final int DUNGEON_EXIT_X = 2015, DUNGEON_EXIT_Y = 9282;
	public static final int PORTAL_X = 2015, PORTAL_Y = 9335;
	public static final int CAPTAINS_CHEST_X = 2034, CAPTAINS_CHEST_Y = 9316;

	private final Map<Integer, MapRegion> regions = new HashMap<>();
	private final Random random = new Random(0x5C011);
	private CacheEditor cache;

	public static void main(String[] args) throws Exception {
		Path cacheDir = Paths.get(args.length > 0 ? args[0] : "data/cache");
		CursedIslandGenerator gen = new CursedIslandGenerator();
		try (CacheEditor cache = new CacheEditor(cacheDir)) {
			gen.cache = cache;
			gen.buildSurface();
			gen.buildDungeon();
		}
		gen.pack(cacheDir);
	}

	/* ====================================================================== */
	/*                                SURFACE                                 */
	/* ====================================================================== */

	/** Radius of the coastline (in tiles) at a given angle - a wobbly circle. */
	static double coastRadius(double angle) {
		return 33 + 4 * Math.sin(3 * angle + 1) + 2.5 * Math.sin(5 * angle + 2) + 1.5 * Math.sin(9 * angle);
	}

	/** Signed distance inland from the coast: negative = sea. */
	static double inland(int x, int y) {
		double dx = x - CX, dy = y - CY;
		return coastRadius(Math.atan2(dy, dx)) - Math.sqrt(dx * dx + dy * dy);
	}

	void buildSurface() {
		for (int rx = 0; rx < SURFACE_REGIONS; rx++) {
			for (int ry = 0; ry < SURFACE_REGIONS; ry++) {
				MapRegion r = new MapRegion(SURFACE_REGION_X + rx, SURFACE_REGION_Y + ry);
				regions.put(r.id(), r);
			}
		}

		for (int x = BASE_X; x < BASE_X + 64 * SURFACE_REGIONS; x++) {
			for (int y = BASE_Y; y < BASE_Y + 64 * SURFACE_REGIONS; y++) {
				double d = inland(x, y);
				if (d < 0) {
					sea(x, y);
				} else {
					// beach slope, then gentle interior, plus the skull hill to the north
					double h = 2 + Math.min(d, 6) * 1.6 + noise(x, y) * 2.5;
					double hill = hillHeight(x, y);
					h += hill;
					setHeight(x, y, (int) Math.round(h));
					if (d < 3.2) {
						setUnderlay(x, y, SAND);
					} else if (hill > 22) {
						setUnderlay(x, y, ROCK); // bare rock on the skull hill
					} else {
						double n = noise(x * 3 + 17, y * 3 + 5);
						int floor = n > 0.35 ? SWAMP : n < -0.4 ? DARK_GRASS : DEAD_GREEN;
						if (Math.hypot(x - 1992, y - 2916) < 9) {
							floor = DEAD_GREEN; // graveyard soil
						}
						setUnderlay(x, y, floor);
					}
				}
			}
		}

		paths();
		if (System.getProperty("gallery") != null) {
			// temporary: line up candidate objects to compare them in game
			String[] ids = System.getProperty("gallery").split(",");
			for (int i = 0; i < ids.length; i++) {
				object(Integer.parseInt(ids[i]), 2026 + (i % 6) * 4, 2896 + (i / 6) * 4, TYPE_SCENERY, 0);
			}
		}
		dock();
		pirateCamp();
		shipwreck();
		graveyard();
		skullHill();
		vegetation();
	}

	/** The skull hill rises steeply in the north of the island. */
	static double hillHeight(int x, int y) {
		double hx = x - CX, hy = y - (CY + 27);
		return 75 * Math.exp(-(hx * hx + hy * hy) / (2 * 6.5 * 6.5));
	}

	void sea(int x, int y) {
		MapRegion r = regionAt(x, y);
		int lx = x & 63, ly = y & 63;
		r.overlay[0][lx][ly] = WATER;
		r.underlay[0][lx][ly] = 0;
		r.settings[0][lx][ly] = MapRegion.BLOCKED;
		r.height[0][lx][ly] = 1; // sea level
	}

	void paths() {
		// dock -> camp
		path(2046, 2912, 2032, 2912);
		// camp -> graveyard (west)
		path(2032, 2912, 2016, 2914);
		path(2016, 2914, 1998, 2916);
		// camp -> skull hill cave (north)
		path(2016, 2914, 2016, 2932);
		// camp -> wreck (south)
		path(2024, 2910, 2016, 2888);
	}

	void path(int x0, int y0, int x1, int y1) {
		int steps = Math.max(Math.abs(x1 - x0), Math.abs(y1 - y0));
		for (int i = 0; i <= steps; i++) {
			double t = steps == 0 ? 0 : (double) i / steps;
			int x = (int) Math.round(x0 + (x1 - x0) * t + Math.sin(i * 0.5) * 0.6);
			int y = (int) Math.round(y0 + (y1 - y0) * t + Math.cos(i * 0.45) * 0.6);
			for (int dx = 0; dx <= 1; dx++) {
				for (int dy = 0; dy <= 1; dy++) {
					if (inland(x + dx, y + dy) > 1) {
						setOverlay(x + dx, y + dy, PATH);
					}
				}
			}
		}
	}

	void dock() {
		// a jetty running east from the beach, with a T at the end where the boat ties up
		for (int x = 2044; x <= 2063; x++) {
			for (int y = 2911; y <= 2913; y++) {
				plank(x, y);
			}
		}
		for (int x = 2061; x <= 2064; x++) {
			for (int y = 2906; y <= 2918; y++) {
				plank(x, y);
			}
		}
		object(CRATE, 2062, 2917, TYPE_SCENERY, 0);
		object(BARREL, 2063, 2907, TYPE_SCENERY, 0);
		object(BARREL, 2064, 2908, TYPE_SCENERY, 0);
		object(TORCH, 2060, 2914, TYPE_SCENERY, 0);
		object(TORCH, 2060, 2910, TYPE_SCENERY, 0);
	}

	void plank(int x, int y) {
		MapRegion r = regionAt(x, y);
		int lx = x & 63, ly = y & 63;
		if (inland(x, y) >= 0) {
			r.overlay[0][lx][ly] = WOOD;
			return;
		}
		r.overlay[0][lx][ly] = WOOD;
		r.settings[0][lx][ly] = 0;
		r.height[0][lx][ly] = 3;
	}

	void pirateCamp() {
		clear(2032, 2912, 6);
		// the camp itself
		object(FLAGPOLE, 2032, 2918, TYPE_SCENERY, 0);
		object(TORCH, 2028, 2916, TYPE_SCENERY, 0);
		object(TORCH, 2036, 2916, TYPE_SCENERY, 0);
		object(TORCH, 2028, 2907, TYPE_SCENERY, 0);
		object(TORCH, 2036, 2907, TYPE_SCENERY, 0);
		object(STACKED_BARRELS, 2027, 2913, TYPE_SCENERY, 0);
		object(BARREL, 2027, 2911, TYPE_SCENERY, 0);
		object(CRATES, 2037, 2913, TYPE_SCENERY, 0);
		object(CRATE, 2037, 2911, TYPE_SCENERY, 0);
		object(CANNON, 2039, 2909, TYPE_SCENERY, 1);
		object(CANNONBALLS, 2040, 2907, TYPE_SCENERY, 0);
		object(CHEST, 2033, 2917, TYPE_SCENERY, 0);
	}

	void shipwreck() {
		// The Drowned Maiden: a copy of one of the Port Sarim ships (region 12082, local 22..30 x 9..28),
		// driven stern-first onto the south beach. Gangplanks are left behind.
		stamp(12082, 22, 9, 30, 28, 2011, 2862, new int[] { 2083, 2084 });
		// wreckage strewn across the sand
		object(BROKEN_BARREL, 2008, 2884, TYPE_SCENERY, 0);
		object(BROKEN_BARREL, 2022, 2883, TYPE_SCENERY, 0);
		object(CRATE, 2021, 2886, TYPE_SCENERY, 0);
		object(ANCHOR, 2025, 2882, TYPE_SCENERY, 2);
		object(CANNON, 2008, 2887, TYPE_SCENERY, 3);
		object(SKELETON, 2012, 2886, TYPE_GROUND_DECOR, 1);
		object(LONG_SKELETON, 2019, 2888, TYPE_GROUND_DECOR, 2);
	}

	/**
	 * Copies a rectangle of an existing region (upper planes' terrain and every object) onto the island.
	 * Plane 0 terrain is left alone so the copy sits on our own sea/sand.
	 */
	void stamp(int sourceRegion, int sx0, int sy0, int sx1, int sy1, int destX, int destY, int[] skipIds) {
		MapRegion src;
		try {
			src = loadRegion(sourceRegion);
		} catch (Exception e) {
			throw new IllegalStateException("could not read source region " + sourceRegion, e);
		}
		for (int x = sx0; x <= sx1; x++) {
			for (int y = sy0; y <= sy1; y++) {
				int ax = destX + x - sx0, ay = destY + y - sy0;
				MapRegion dst = regionAt(ax, ay);
				int lx = ax & 63, ly = ay & 63;
				removeObjectsAt(ax, ay);
				for (int z = 1; z < 4; z++) {
					dst.height[z][lx][ly] = src.height[z][x][y];
					dst.overlay[z][lx][ly] = src.overlay[z][x][y];
					dst.overlayShape[z][lx][ly] = src.overlayShape[z][x][y];
					dst.overlayRotation[z][lx][ly] = src.overlayRotation[z][x][y];
					dst.underlay[z][lx][ly] = src.underlay[z][x][y];
					dst.settings[z][lx][ly] = src.settings[z][x][y];
				}
			}
		}
		outer:
		for (MapRegion.MapObject o : src.objects) {
			if (o.x < sx0 || o.x > sx1 || o.y < sy0 || o.y > sy1) {
				continue;
			}
			for (int skip : skipIds) {
				if (o.id == skip) {
					continue outer;
				}
			}
			int ax = destX + o.x - sx0, ay = destY + o.y - sy0;
			regionAt(ax, ay).objects.add(new MapRegion.MapObject(o.id, ax & 63, ay & 63, o.z, o.type, o.rotation));
		}
	}

	MapRegion loadRegion(int regionId) throws java.io.IOException {
		CacheEditor.Archive versionList = CacheEditor.Archive.decode(cache.read(0, CacheEditor.VERSIONLIST_ARCHIVE));
		for (CacheEditor.MapIndexEntry e : CacheEditor.parseMapIndex(versionList.get("map_index"))) {
			if (e.region == regionId) {
				return MapRegion.decode(regionId >> 8, regionId & 0xFF,
						org.apollo.util.CompressionUtil.degzip(java.nio.ByteBuffer.wrap(cache.read(CacheEditor.MAP_INDEX, e.terrainFile))),
						org.apollo.util.CompressionUtil.degzip(java.nio.ByteBuffer.wrap(cache.read(CacheEditor.MAP_INDEX, e.objectFile))));
			}
		}
		throw new java.io.FileNotFoundException("region " + regionId);
	}

	void graveyard() {
		int[][] graves = { {1988, 2920}, {1991, 2920}, {1994, 2920}, {1988, 2917}, {1991, 2917}, {1994, 2917},
				{1988, 2914}, {1991, 2914}, {1994, 2913}, {1989, 2911} };
		int[] kinds = { GRAVESTONE, SMALL_GRAVESTONE, GRAVE_MARKER };
		for (int i = 0; i < graves.length; i++) {
			object(kinds[i % kinds.length], graves[i][0], graves[i][1], TYPE_SCENERY, 1);
		}
		object(LARGE_GRAVE, 1985, 2916, TYPE_SCENERY, 1);
		object(SKULL_PILE, 1996, 2922, TYPE_SCENERY, 0);
		object(LONG_SKELETON, 1993, 2909, TYPE_GROUND_DECOR, 0);
		object(BIG_DEAD_TREE, 1984, 2922, TYPE_SCENERY, 0);
		object(BIG_DEAD_TREE, 1997, 2908, TYPE_SCENERY, 0);
	}

	void skullHill() {
		// carve a flat ledge in front of the cave
		for (int x = CAVE_ENTRANCE_X - 3; x <= CAVE_ENTRANCE_X + 4; x++) {
			for (int y = CAVE_ENTRANCE_Y - 4; y <= CAVE_ENTRANCE_Y; y++) {
				setHeight(x, y, heightAt(CAVE_ENTRANCE_X, CAVE_ENTRANCE_Y - 4));
				setOverlay(x, y, PATH);
				removeObjectsAt(x, y);
			}
		}
		object(CAVE_ENTRANCE, CAVE_ENTRANCE_X, CAVE_ENTRANCE_Y, TYPE_SCENERY, 0);
		object(SKULL_PILE, CAVE_ENTRANCE_X - 2, CAVE_ENTRANCE_Y, TYPE_SCENERY, 0);
		object(SKULL_PILE, CAVE_ENTRANCE_X + 4, CAVE_ENTRANCE_Y, TYPE_SCENERY, 0);
		object(TORCH, CAVE_ENTRANCE_X - 1, CAVE_ENTRANCE_Y - 2, TYPE_SCENERY, 0);
		object(TORCH, CAVE_ENTRANCE_X + 3, CAVE_ENTRANCE_Y - 2, TYPE_SCENERY, 0);
		for (int i = 0; i < 10; i++) {
			int x = CX - 8 + random.nextInt(17), y = CY + 26 + random.nextInt(10);
			if (isFree(x, y, 1)) {
				object(ROCKS + random.nextInt(3), x, y, TYPE_SCENERY, random.nextInt(4));
			}
		}
	}

	void vegetation() {
		for (int x = BASE_X; x < BASE_X + 64 * SURFACE_REGIONS; x++) {
			for (int y = BASE_Y; y < BASE_Y + 64 * SURFACE_REGIONS; y++) {
				double d = inland(x, y);
				if (d < 1 || !isFree(x, y, 2)) {
					continue;
				}
				int roll = random.nextInt(1000);
				if (d < 4.5) {
					if (roll < 35) {
						object(roll < 12 ? LEAFY_PALM : PALM, x, y, TYPE_SCENERY, random.nextInt(4));
					}
				} else if (roll < 22) {
					object(roll < 8 ? BIG_DEAD_TREE : DEAD_TREE, x, y, TYPE_SCENERY, random.nextInt(4));
				} else if (roll < 26) {
					object(ROCKS + random.nextInt(3), x, y, TYPE_SCENERY, random.nextInt(4));
				} else if (roll < 28) {
					object(ANIMAL_SKULL, x, y, TYPE_SCENERY, random.nextInt(4));
				}
			}
		}
	}

	/* ====================================================================== */
	/*                                DUNGEON                                 */
	/* ====================================================================== */

	/** Blackbrine's Hold: entrance cave -> winding tunnel -> plunder cavern -> portal chamber. */
	void buildDungeon() {
		MapRegion r = new MapRegion(DUNGEON_REGION_X, DUNGEON_REGION_Y);
		regions.put(r.id(), r);
		boolean[][] open = new boolean[64][64];

		// entrance cave (south)
		blob(open, 32, 8, 6, 5);
		// winding tunnel north-east
		tunnel(open, 32, 12, 40, 22, 2);
		tunnel(open, 40, 22, 46, 30, 2);
		// plunder cavern (east) with the captain's chest
		blob(open, 48, 36, 8, 7);
		// tunnel back west to the portal chamber
		tunnel(open, 44, 40, 36, 46, 2);
		tunnel(open, 36, 46, 32, 48, 2);
		// portal chamber (north)
		blob(open, 32, 53, 7, 6);
		// a side grotto off the tunnel (west), home to the cursed crew
		tunnel(open, 38, 20, 24, 26, 2);
		blob(open, 20, 28, 6, 5);

		for (int x = 0; x < 64; x++) {
			for (int y = 0; y < 64; y++) {
				if (open[x][y]) {
					r.underlay[0][x][y] = noise(x * 2, y * 2) > 0.3 ? CAVE_FLOOR : MUD;
					r.height[0][x][y] = 2 + (int) Math.round((noise(x * 4 + 3, y * 4 + 9) + 1) * 1.5);
				} else {
					// Walls are made from terrain: rock rises steeply away from the floor, and only the
					// rock face is drawn - anything deeper in is an invisible tile so the void stays black.
					r.settings[0][x][y] = MapRegion.BLOCKED;
					r.underlay[0][x][y] = ROCK;
					if (bordersOpen(open, x, y, 1)) {
						r.height[0][x][y] = 34 + (int) Math.round(noise(x * 5 + 1, y * 5 + 2) * 8);
					} else {
						r.overlay[0][x][y] = INVISIBLE;
						r.height[0][x][y] = 50;
					}
				}
			}
		}

		// stalagmites hugging the walls
		for (int x = 1; x < 63; x++) {
			for (int y = 1; y < 63; y++) {
				if (open[x][y] && bordersClosed(open, x, y) && openAround(open, x, y, 2) >= 15 && random.nextInt(10) == 0) {
					r.objects.add(new MapRegion.MapObject(random.nextBoolean() ? STALAGMITE : STALAGMITE_2, x, y, 0, TYPE_SCENERY, random.nextInt(4)));
				}
			}
		}

		// make the portal chamber floor glow a sickly purple ring
		for (int x = 26; x <= 38; x++) {
			for (int y = 49; y <= 59; y++) {
				double dist = Math.hypot(x - 32, y - 53);
				if (open[x][y] && dist > 2.5 && dist < 3.8) {
					r.overlay[0][x][y] = 116; // flo 115: morytania_barrow_crypt_purple
				}
			}
		}

		this.dungeonOpen = open;
		// entrance
		dobj(r, CAVE_EXIT, DUNGEON_EXIT_X, DUNGEON_EXIT_Y, TYPE_SCENERY, 2);
		dobj(r, CAVE_TORCH, 2012, 9287, TYPE_SCENERY, 0);
		dobj(r, CAVE_TORCH, 2020, 9287, TYPE_SCENERY, 0);
		// tunnel dressing
		dobj(r, STALAGMITE, 2022, 9300, TYPE_SCENERY, 0);
		dobj(r, STALAGMITE_2, 2027, 9306, TYPE_SCENERY, 1);
		dobj(r, DUNGEON_SKULLS, 2026, 9303, TYPE_SCENERY, 0);
		// plunder cavern
		dobj(r, CHEST, CAPTAINS_CHEST_X, CAPTAINS_CHEST_Y, TYPE_SCENERY, 0);
		dobj(r, STACKED_BARRELS, 2028, 9318, TYPE_SCENERY, 0);
		dobj(r, CRATES, 2037, 9312, TYPE_SCENERY, 0);
		dobj(r, CRATE, 2036, 9320, TYPE_SCENERY, 0);
		dobj(r, CANNON, 2030, 9313, TYPE_SCENERY, 2);
		dobj(r, CAVE_TORCH, 2031, 9320, TYPE_SCENERY, 0);
		dobj(r, CAVE_TORCH, 2033, 9311, TYPE_SCENERY, 0);
		// crew grotto
		dobj(r, SKULL_PILE, 2002, 9310, TYPE_SCENERY, 0);
		dobj(r, LONG_SKELETON, 2006, 9306, TYPE_GROUND_DECOR, 0);
		dobj(r, CAVE_TORCH, 2005, 9312, TYPE_SCENERY, 0);
		// portal chamber
		dobj(r, PORTAL, PORTAL_X, PORTAL_Y, TYPE_SCENERY, 0);
		dobj(r, CAVE_TORCH, 2012, 9333, TYPE_SCENERY, 0);
		dobj(r, CAVE_TORCH, 2020, 9333, TYPE_SCENERY, 0);
		dobj(r, DUNGEON_SKULLS, 2012, 9336, TYPE_SCENERY, 0);
		dobj(r, DUNGEON_SKULLS, 2021, 9337, TYPE_SCENERY, 0);
	}

	private boolean[][] dungeonOpen;

	void dobj(MapRegion r, int id, int x, int y, int type, int rot) {
		if (id != CAVE_EXIT && !dungeonOpen[x - DBASE_X][y - DBASE_Y]) {
			System.out.println("warning: dungeon object " + id + " at " + x + "," + y + " is inside rock");
		}
		r.objects.add(new MapRegion.MapObject(id, x - DBASE_X, y - DBASE_Y, 0, type, rot));
	}

	static void blob(boolean[][] open, int cx, int cy, int rx, int ry) {
		for (int x = cx - rx - 2; x <= cx + rx + 2; x++) {
			for (int y = cy - ry - 2; y <= cy + ry + 2; y++) {
				double a = Math.atan2(y - cy, x - cx);
				double wobble = 1 + 0.15 * Math.sin(4 * a + cx);
				double dx = (x - cx) / (rx * wobble), dy = (y - cy) / (ry * wobble);
				if (dx * dx + dy * dy <= 1 && x > 1 && y > 1 && x < 62 && y < 62) {
					open[x][y] = true;
				}
			}
		}
	}

	static void tunnel(boolean[][] open, int x0, int y0, int x1, int y1, int width) {
		int steps = Math.max(Math.abs(x1 - x0), Math.abs(y1 - y0)) * 2;
		for (int i = 0; i <= steps; i++) {
			double t = (double) i / steps;
			int x = (int) Math.round(x0 + (x1 - x0) * t), y = (int) Math.round(y0 + (y1 - y0) * t);
			for (int dx = -width / 2; dx <= width / 2; dx++) {
				for (int dy = -width / 2; dy <= width / 2; dy++) {
					open[x + dx][y + dy] = true;
				}
			}
		}
	}

	/** Number of open tiles in the (2r+1)^2 square - used to keep narrow tunnels clear. */
	static int openAround(boolean[][] open, int x, int y, int r) {
		int n = 0;
		for (int dx = -r; dx <= r; dx++) {
			for (int dy = -r; dy <= r; dy++) {
				int nx = x + dx, ny = y + dy;
				if (nx >= 0 && ny >= 0 && nx < 64 && ny < 64 && open[nx][ny]) {
					n++;
				}
			}
		}
		return n;
	}

	static boolean bordersClosed(boolean[][] open, int x, int y) {
		return !open[x - 1][y] || !open[x + 1][y] || !open[x][y - 1] || !open[x][y + 1];
	}

	static boolean bordersOpen(boolean[][] open, int x, int y, int r) {
		for (int dx = -r; dx <= r; dx++) {
			for (int dy = -r; dy <= r; dy++) {
				int nx = x + dx, ny = y + dy;
				if (nx >= 0 && ny >= 0 && nx < 64 && ny < 64 && open[nx][ny]) {
					return true;
				}
			}
		}
		return false;
	}

	/* ====================================================================== */
	/*                                HELPERS                                 */
	/* ====================================================================== */

	MapRegion regionAt(int x, int y) {
		return regions.get((x >> 6) << 8 | (y >> 6));
	}

	void setHeight(int x, int y, int h) {
		regionAt(x, y).height[0][x & 63][y & 63] = h;
	}

	int heightAt(int x, int y) {
		return regionAt(x, y).height[0][x & 63][y & 63];
	}

	void setUnderlay(int x, int y, int floor) {
		regionAt(x, y).underlay[0][x & 63][y & 63] = floor;
	}

	void setOverlay(int x, int y, int floor) {
		regionAt(x, y).overlay[0][x & 63][y & 63] = floor;
	}

	void object(int id, int x, int y, int type, int rot) {
		regionAt(x, y).objects.add(new MapRegion.MapObject(id, x & 63, y & 63, 0, type, rot));
	}

	void removeObjectsAt(int x, int y) {
		regionAt(x, y).objects.removeIf(o -> o.x == (x & 63) && o.y == (y & 63));
	}

	/** Removes scattered scenery in a radius and marks the ground as trodden. */
	void clear(int cx, int cy, int radius) {
		for (int x = cx - radius; x <= cx + radius; x++) {
			for (int y = cy - radius; y <= cy + radius; y++) {
				if (Math.hypot(x - cx, y - cy) <= radius) {
					removeObjectsAt(x, y);
					setUnderlay(x, y, MUDDY_GRASS);
				}
			}
		}
	}

	/** True when nothing is placed within {@code r} tiles and the tile isn't a path/dock. */
	boolean isFree(int x, int y, int r) {
		for (int dx = -r; dx <= r; dx++) {
			for (int dy = -r; dy <= r; dy++) {
				MapRegion reg = regionAt(x + dx, y + dy);
				if (reg == null) {
					return false;
				}
				int lx = (x + dx) & 63, ly = (y + dy) & 63;
				if (reg.overlay[0][lx][ly] == PATH || reg.overlay[0][lx][ly] == WOOD) {
					return false;
				}
				for (MapRegion.MapObject o : reg.objects) {
					if (o.x == lx && o.y == ly) {
						return false;
					}
				}
			}
		}
		// keep the camp, graveyard and wreck clearings open
		return Math.hypot(x - 2032, y - 2912) > 8 && Math.hypot(x - 1991, y - 2916) > 9
				&& Math.hypot(x - 2015, y - 2884) > 8 && Math.hypot(x - CAVE_ENTRANCE_X, y - CAVE_ENTRANCE_Y) > 5;
	}

	/** Cheap deterministic value noise in roughly [-1, 1]. */
	static double noise(int x, int y) {
		double v = Math.sin(x * 0.31 + y * 0.17) * 0.5 + Math.sin(x * 0.11 - y * 0.23 + 1.7) * 0.35
				+ Math.sin(x * 0.53 + y * 0.47 + 4.1) * 0.15;
		return v;
	}

	/* ====================================================================== */
	/*                                PACKING                                 */
	/* ====================================================================== */

	void pack(Path cacheDir) throws Exception {
		try (CacheEditor cache = new CacheEditor(cacheDir)) {
			CacheEditor.Archive versionList = CacheEditor.Archive.decode(cache.read(0, CacheEditor.VERSIONLIST_ARCHIVE));
			List<CacheEditor.MapIndexEntry> index = new ArrayList<>(CacheEditor.parseMapIndex(versionList.get("map_index")));
			byte[][] tables = { versionList.get("map_version"), versionList.get("map_crc") };
			int nextFile = Math.max(cache.fileCount(CacheEditor.MAP_INDEX), tables[0].length / 2);

			for (MapRegion region : regions.values()) {
				CacheEditor.MapIndexEntry entry = null;
				for (CacheEditor.MapIndexEntry e : index) {
					if (e.region == region.id()) {
						entry = e;
					}
				}
				if (entry == null) {
					entry = new CacheEditor.MapIndexEntry();
					entry.region = region.id();
					entry.terrainFile = nextFile++;
					entry.objectFile = nextFile++;
					entry.members = true; // byte 1 = accessible on free worlds too
					index.add(entry);
				}
				int version = 1;
				cache.writeMapFile(entry.terrainFile, region.encodeTerrain(), version, tables);
				cache.writeMapFile(entry.objectFile, region.encodeObjects(), version, tables);
				System.out.println("Wrote region " + region.id() + " (" + region.regionX + "," + region.regionY + ") files "
						+ entry.terrainFile + "/" + entry.objectFile + " objects=" + region.objects.size());
			}
			versionList.put("map_index", CacheEditor.encodeMapIndex(index));
			versionList.put("map_version", tables[0]);
			versionList.put("map_crc", tables[1]);
			cache.write(0, CacheEditor.VERSIONLIST_ARCHIVE, versionList.encode());
			System.out.println("Updated versionlist: " + index.size() + " regions, " + tables[0].length / 2 + " map files.");
		}
	}
}
