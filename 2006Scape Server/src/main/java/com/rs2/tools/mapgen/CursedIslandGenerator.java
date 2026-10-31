package com.rs2.tools.mapgen;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.rs2.game.content.skills.smithing.DragonMetal;
import com.rs2.game.content.skullbrine.SkullbrineConstants;
import static com.rs2.game.content.voidrealm.VoidConstants.PIRATE_FLAG;
import static com.rs2.game.content.skullbrine.SkullbrineConstants.*;

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
			SHIP_WHEEL = 252, TORCH = 5494, BARREL = 9519, STACKED_BARRELS = 4641, CHEST = CAPTAINS_CHEST,
			FLAGPOLE = 3680, CAVE_TORCH = 724,
			STALAGMITE = 541, STALAGMITE_2 = 542, DUNGEON_SKULLS = 668, PORTAL = VOID_PORTAL;

	/* ---------- new objects (recoloured / rescaled clones appended to loc.dat, see buildObjects) ---------- */
	static final int LOC_BASE = 14974;
	static final int GHOSTFIRE = LOC_BASE, GHOST_TORCH = LOC_BASE + 1, BONE_FANG = LOC_BASE + 2, BIG_FANG = LOC_BASE + 3,
			SERPENT_SKULL = LOC_BASE + 4, GIANT_RIBS = LOC_BASE + 5, BLACK_FLAG = LOC_BASE + 6, WITHERED_PALM = LOC_BASE + 7,
			WITHERED_LEAFY_PALM = LOC_BASE + 8, GHOST_LAMP = LOC_BASE + 9, GHOST_LAMP_POST = LOC_BASE + 10;

	/** RS 16-bit HSL colour. */
	static int hsl(int hue, int sat, int light) {
		return hue << 10 | sat << 7 | light;
	}

	static final int BONE = hsl(7, 1, 100), BONE_DARK = hsl(7, 1, 78);

	public static final int TYPE_WALL = 0, TYPE_SCENERY = 10, TYPE_GROUND_DECOR = 22;

	/* Key points (cave, dungeon exit, portal, chest...) come from SkullbrineConstants, shared with the server. */

	final Map<Integer, MapRegion> regions = new HashMap<>();
	private final Random random = new Random(0x5C011);
	CacheEditor cache;
	/** The Void quest series and the continent of Nythera. */
	final VoidRealmGenerator voidRealm = new VoidRealmGenerator(this);
	CacheEditor.Archive config;

	public static void main(String[] args) throws Exception {
		Path cacheDir = Paths.get(args.length > 0 ? args[0] : "data/cache");
		CursedIslandGenerator gen = new CursedIslandGenerator();
		try (CacheEditor cache = new CacheEditor(cacheDir)) {
			gen.cache = cache;
			gen.buildSurface();
			gen.voidRealm.islandScenery();
			gen.buildDungeon();
			gen.voidRealm.arena();
			gen.voidRealm.nythera();
			gen.bedFlatProps();
			gen.buildDefinitions();
			if (System.getProperty("preview") != null) {
				javax.imageio.ImageIO.write(new MapRender(cache).render(id -> gen.regions.get(id), BASE_X, BASE_Y,
						64 * SURFACE_REGIONS, 64 * SURFACE_REGIONS, 6, false), "png", new java.io.File(System.getProperty("preview")));
			}
			if (System.getProperty("zoom") != null) {
				// dev only: top-down close-ups, "x0,y0,w,h,scale,file;..."
				for (String z : System.getProperty("zoom").split(";")) {
					String[] f = z.split(",");
					javax.imageio.ImageIO.write(new MapRender(cache).render(id -> gen.regions.get(id), Integer.parseInt(f[0]),
							Integer.parseInt(f[1]), Integer.parseInt(f[2]), Integer.parseInt(f[3]), Integer.parseInt(f[4]), true), "png",
							new java.io.File(f[5]));
				}
			}
			if (System.getProperty("nythera") != null) {
				javax.imageio.ImageIO.write(new MapRender(cache).render(id -> gen.regions.get(id), VoidRealmGenerator.NX0,
						VoidRealmGenerator.NY0, VoidRealmGenerator.NSIZE, VoidRealmGenerator.NSIZE, 2, false), "png",
						new java.io.File(System.getProperty("nythera")));
			}
		}
		gen.pack(cacheDir);
	}

	/* ====================================================================== */
	/*                                SURFACE                                 */
	/* ====================================================================== */

	/*
	 * Layout (north is up):
	 *
	 *                 ~~~~~ sea ~~~~~
	 *            _____ SKULL ROCK _____          A cliff carved and painted as a giant skull, staring south.
	 *   graveyard   (eyes)  (nose)              Its mouth is the cave into Blackbrine's Hold.
	 *   (west)       [ teeth | mouth ]   swamp   Fangs ring the plaza in front of it like a lower jaw.
	 *                   jaw plaza        (east)
	 *                       |                    serpent skeleton on the east beach
	 *        Rattlebone Row (ruined shanty town)
	 *          Drowned Maiden  [jetty]           Deadwater Cove: a sickly green bay full of wrecks,
	 *     ~~~~~~~ Deadwater Cove ~~~~~~~~~        guarded by sea stacks.
	 */

	/**
	 * Skull Rock: a giant skull half-buried in the north of the island, its face tilted up to the sky (the game camera
	 * looks down on the world, so the face is a gentle slope you can walk over rather than a cliff). Horizontal centre,
	 * the y of its chin, how deep (in tiles) the face is, where the back of the cranium meets the sea, and heights.
	 */
	static final int SKX = 2016, FACE_Y0 = 2928, FACE_DEPTH = 20, SKULL_BACK_Y = 2964;
	static final double FACE_RISE = 46, CRANIUM_H = 84;
	/** Deadwater Cove (an ellipse of green water bitten out of the south coast). */
	static final int COVE_X = 2017, COVE_Y = 2873, COVE_RX = 15, COVE_RY = 13;

	/* extra floors for the cursed palette (flo index + 1) */
	static final int GUNGY_WATER = 7, BLACK = 37, BLACK_SAND = 83, DARK_SAND = 90, AHOY_GRASS = 107, BURGH_SOIL = 131,
			DAGANNOTH_GRASS = 133, BARROW_GRASS = 115, ECTO_GLOW = 114, BONE_ROCK = 124, BONE_ROCK_DARK = 125,
			DARK_PATH = 141, GREY_PATH = 132, DARK_ROCK = 56, SHADOW_ROCK = 57, DEAD_GRASS = 96, ASH = 92,
			MUD_PATH = 81, ROAD = 81;

	/* swamp and Morytania scenery */
	static final int MORY_TREE = 4051, MORY_TREE_BIG = 4052, MORY_TREE_2 = 4053, ROTTING_TREE = 3514, ROTTING_LOG = 3508,
			ROTTING_BRANCH = 3510, HUGE_MUSHROOM = 4055, ROOTS = 1363, SMALL_FERN = 1173, MOSSY_ROCK = 477,
			SMALL_MOSSY_ROCK = 478, SWAMP_BUBBLES = 684, LARGE_BONE = 702, CURVED_BONE = 700, FUNGUS = 1171,
			GALLOWS = 649, PILE_OF_SKULLS = 658, SMALL_BUSH = 3512, ROW_BOAT = 2515, ROW_BOAT_2 = 2516,
			TORN_SAIL = 212, STOCKS = 648, CAGE = 3351, OPEN_COFFIN = 399, CROSS = 898, NET_DRYING = 14972, HAMMOCK = 430,
			LONG_GRASS = 4815, GRASS = 3795, TOTEM = 870;

	/** Radius of the coastline (in tiles) at a given angle - a wobbly circle. */
	static double coastRadius(double angle) {
		return 41 + 3 * Math.sin(3 * angle + 1) + 2.2 * Math.sin(5 * angle + 2) + 1.2 * Math.sin(9 * angle);
	}

	/** Distance (in tiles, roughly) outside the cove's edge: negative = inside the cove. */
	static double coveDistance(int x, int y) {
		double dx = (x - COVE_X) / (double) COVE_RX, dy = (y - COVE_Y) / (double) COVE_RY;
		double wobble = 0.06 * Math.sin(Math.atan2(dy, dx) * 5 + 1.3);
		return (Math.sqrt(dx * dx + dy * dy) - 1 - wobble) * Math.min(COVE_RX, COVE_RY);
	}

	/** Signed distance inland from the coast: negative = sea. */
	static double inland(int x, int y) {
		double dx = x - CX, dy = y - CY;
		double d = coastRadius(Math.atan2(dy, dx)) - Math.sqrt(dx * dx + dy * dy);
		d = Math.min(d, coveDistance(x, y));
		// a ragged edge: smooth curves alone leave long ruler-straight runs of shore once they're cut into tiles
		d += VoidRealmGenerator.ragged(x, y, 21);
		if (skullHeight(x, y) > 3) {
			d = Math.max(d, 1); // the back of the skull rises straight out of the sea
		}
		return d;
	}

	static double smooth(double t) {
		t = Math.max(0, Math.min(1, t));
		return t * t * (3 - 2 * t);
	}

	/** Face coordinate: 0 at the foot of the skull's face, 1 at its brow. */
	static double faceV(int y) {
		return (y - FACE_Y0) / (double) FACE_DEPTH;
	}

	/** Half the width of the skull (narrow jaw, wide cranium, tapering towards the back). */
	static double skullHalfWidth(int y) {
		double v = faceV(y);
		if (v < 0.3) {
			return 10 + Math.max(0, v) / 0.3 * 3;
		}
		if (v <= 1) {
			return 13 + 4 * Math.sin(Math.min(1, (v - 0.3) / 0.5) * Math.PI / 2);
		}
		double t = (y - FACE_Y0 - FACE_DEPTH) / (double) (SKULL_BACK_Y - FACE_Y0 - FACE_DEPTH);
		return 17 * Math.sqrt(Math.max(0, 1 - 0.7 * t * t));
	}

	/** Height (map units) Skull Rock adds on top of the ground, before the eyes and nose are hollowed out. */
	static double skullHeight(int x, int y) {
		double v = faceV(y);
		if (v < -0.02 || y > SKULL_BACK_Y + 1) {
			return 0;
		}
		double h;
		if (v <= 1) {
			h = 6 + FACE_RISE * Math.pow(Math.max(0, v), 1.25); // the chin is a low step, the face rises gently
		} else {
			double t = (y - FACE_Y0 - FACE_DEPTH) / (double) (SKULL_BACK_Y - FACE_Y0 - FACE_DEPTH);
			h = 6 + FACE_RISE + (CRANIUM_H - FACE_RISE) * Math.sin(Math.min(1, t / 0.55) * Math.PI / 2);
			// sheer drop into the sea: the back of the cranium is rounded (it comes furthest out behind the middle) and
			// broken, not cut off along a row
			double u = (x - SKX + 0.5) / 17.0, tb = t + 0.22 * u * u + 0.05 * VoidRealmGenerator.vnoise(x / 2.5, 0.5, 23);
			if (tb > 0.75) {
				h *= Math.max(0, 1 - Math.pow((tb - 0.75) / 0.28, 2));
			}
		}
		// the sides fall away steeply as bone cliffs
		double side = smooth((skullHalfWidth(y) - Math.abs(x - SKX + 0.5)) / 2.5);
		return h * side;
	}

	/** Eye socket (-1 / +1) the tile is in, with how close to the centre (0 = centre, 1 = rim); null if none. */
	static double[] eye(int x, int y) {
		double v = faceV(y), u = x - SKX + 0.5;
		for (int side = -1; side <= 1; side += 2) {
			double ex = (u - side * 6) / 3.6, ey = (v - 0.62) / 0.17;
			double r = Math.sqrt(ex * ex + ey * ey);
			if (r < 1) {
				return new double[] { side, r };
			}
		}
		return null;
	}

	static boolean nose(int x, int y) {
		double v = faceV(y), u = x - SKX + 0.5;
		return v > 0.3 && v < 0.46 && Math.abs(u) < 0.5 + 1.8 * (0.46 - v) / 0.16;
	}

	/** The dark band of the mouth, between the jaws. The fangs stand in it and the cave opens in its middle. */
	static boolean mouth(int x, int y) {
		double v = faceV(y), u = x - SKX + 0.5;
		return v >= 0 && v < 0.13 && Math.abs(u) < 7.5;
	}

	/** What is painted on the skull at (x, y): 0 = bone, 1 = black hollow, 2 = glowing green pool, 3 = shadowed rock / crack. */
	static int skullPaint(int x, int y) {
		double[] e = eye(x, y);
		if (e != null) {
			return e[1] < 0.5 ? 2 : 3;
		}
		if (nose(x, y) || mouth(x, y)) {
			return 1;
		}
		return crack(x, y) ? 3 : 0;
	}

	/** Sutures across the crown and a crack running down from the left brow, so the bone isn't a blank slab. */
	static boolean crack(int x, int y) {
		int u = x - SKX;
		int crown = FACE_Y0 + FACE_DEPTH + 4;
		// zig-zag suture across the crown
		if (Math.abs(u) < 13 && y == crown + ((u + 40) / 2 % 2)) {
			return true;
		}
		// sagittal suture running back from it
		if (y > crown && y < SKULL_BACK_Y - 5 && x == SKX + ((y / 2) % 2) - 1) {
			return true;
		}
		// a crack from the left brow up towards the crown
		int[][] crackTiles = { { -9, 16 }, { -9, 17 }, { -10, 18 }, { -10, 19 }, { -11, 20 }, { -11, 21 }, { -12, 22 },
				{ 9, 18 }, { 10, 19 }, { 10, 20 } };
		for (int[] c : crackTiles) {
			if (u == c[0] && y - FACE_Y0 == c[1]) {
				return true;
			}
		}
		return false;
	}

	/** Depth (height units) the eye sockets and nose are hollowed into the face. */
	static double skullHollow(int x, int y) {
		double[] e = eye(x, y);
		if (e != null) {
			return 30 * Math.sqrt(1 - e[1] * e[1]);
		}
		return nose(x, y) ? 9 : 0;
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
					if (coveDistance(x, y) < 0) {
						setOverlay(x, y, GUNGY_WATER); // the cursed water of Deadwater Cove
					}
					continue;
				}
				// beach slope, rolling ground, and small bumps that catch the light (the client blurs floor colours
				// together, so the ground's texture comes mostly from shading)
				double bumps = 2.6 * noise(x * 3 + 5, y * 3 + 9) + 1.4 * noise(x * 7 + 1, y * 6 + 3)
						+ (random.nextDouble() - 0.5) * 1.6;
				double h = 3 + Math.min(d, 6) * 1.6 + noise(x, y) * 4 + bumps * Math.min(1, d / 3) + westPlateau(x, y);
				double skull = skullHeight(x, y);
				h += skull - skullHollow(x, y);
				setHeight(x, y, (int) Math.round(Math.max(2, h)));
				if (skull > 4) {
					// Skull Rock: bone-white stone, its features painted in black and ghostly green
					setUnderlay(x, y, noise(x * 5, y * 5) > 0.55 ? BONE_ROCK_DARK : BONE_ROCK);
					int paint = skullPaint(x, y);
					if (paint == 1) {
						setOverlay(x, y, VoidRealmGenerator.SKULL_SHADOW);
					} else if (paint == 2) {
						setOverlay(x, y, GUNGY_WATER); // the eyes are pools of the cove's cursed water
					} else if (paint == 3 && crack(x, y)) {
						setUnderlay(x, y, BONE_ROCK_DARK); // sutures: a soft darker line the client blends, not a hard tile
					} else if (paint == 3) {
						setOverlay(x, y, SHADOW_ROCK);
					}
					// the eyes, nose and the steep sides can't be walked on; the face and crown can
					boolean steep = skullHalfWidth(y) - Math.abs(x - SKX + 0.5) < 2.2 || y > SKULL_BACK_Y - 5;
					if (paint != 0 && !mouth(x, y) && !crack(x, y) || steep) {
						block(x, y);
					}
				} else if (d < 3.4) {
					setUnderlay(x, y, noise(x * 4 + 3, y * 4) > 0.2 ? DARK_SAND : BLACK_SAND); // black volcanic sand
				} else {
					double n = noise(x + 17, y + 5) + 0.35 * noise(x * 3 + 2, y * 3 + 31);
					int floor = n > 0.8 ? DEAD_GRASS : n > 0.45 ? MUDDY_GRASS : n > 0.1 ? BARROW_GRASS : n < -0.75 ? BURGH_SOIL
							: n < -0.35 ? DAGANNOTH_GRASS : AHOY_GRASS;
					if (d < 6 && noise(x * 5, y * 5 + 7) > 0.3) {
						floor = ASH; // grey ash where the sand gives way to grass
					}
					setUnderlay(x, y, floor);
				}
			}
		}

		seaStacks();
		swamp();
		paths();
		if (System.getProperty("gallery") != null) {
			// line up candidate objects on the beach west of the cove to compare them in game
			String[] ids = System.getProperty("gallery").split(",");
			for (int i = 0; i < ids.length; i++) {
				object(Integer.parseInt(ids[i]), 2034 + (i % 5) * 4, 2925 + (i / 5) * 5, TYPE_SCENERY, 0);
			}
		}
		jetty();
		fishingBeach();
		rattleboneRow();
		shipwreck();
		coveWrecks();
		graveyard();
		serpentBeach();
		skullRock();
		vegetation();
		smoothOverlays();
		drownedScenery(BASE_X, BASE_Y, 64 * SURFACE_REGIONS);
	}

	/** Smoothing rounds coasts off, and can put the water over a plant or tree that was set on dry land: clear them. */
	void drownedScenery(int x0, int y0, int size) {
		locSize(0); // loads the stock names
		for (int x = x0; x < x0 + size; x++) {
			for (int y = y0; y < y0 + size; y++) {
				int ov = overlayAt(x, y);
				if (ov == WATER || ov == GUNGY_WATER) {
					removeObjects(x, y, o -> VEGETATION.contains(o.id) || plant(o.id));
				}
			}
		}
	}

	boolean plant(int id) {
		if (id >= stockLocs.size()) {
			return false;
		}
		String n = stockLocs.decode(id).text(2);
		n = n == null ? "" : n.toLowerCase();
		return n.matches(".*\\b(flowers?|fern|plant|bush|grass|mushroom|daisies|thistle|tree|oak|willow|yew|maple|evergreen|stump)\\b.*")
				&& !n.contains("water");
	}

	/**
	 * A client object stands level at one height: bed the flat-bottomed ones into level ground, or one end hangs in the
	 * air while the other is buried. Runs last, over every region, once all the terrain is final.
	 */
	void bedFlatProps() {
		for (int pass = 0; pass < 4; pass++) { // neighbours share corners: settle them together
			for (MapRegion r : new ArrayList<>(regions.values())) {
				for (MapRegion.MapObject o : r.objects) {
					if (o.z == 0 && LEVEL_BEDDED.contains(o.id)) {
						int[] size = locSize(o.id);
						int x = r.regionX * 64 + o.x, y = r.regionY * 64 + o.y;
						int sx = (o.rotation & 1) == 1 ? size[1] : size[0], sy = (o.rotation & 1) == 1 ? size[0] : size[1];
						flatten(x, y, x + sx, y + sy);
					}
				}
			}
		}
	}

	/** Flat-bottomed things that are bedded into level ground wherever they're put. */
	static final java.util.Set<Integer> LEVEL_BEDDED = new java.util.HashSet<>(java.util.Arrays.asList(ROW_BOAT, ROW_BOAT_2, TORN_SAIL,
			OPEN_COFFIN, LARGE_GRAVE, GRAVESTONE, SMALL_GRAVESTONE, GRAVE_MARKER, CROSS, NET_DRYING, HAMMOCK));

	/** What {@link #vegetation} plants (removed again wherever the sea ends up over it). */
	static final java.util.Set<Integer> VEGETATION = new java.util.HashSet<>(java.util.Arrays.asList(WITHERED_LEAFY_PALM, WITHERED_PALM, ROTTING_LOG, MORY_TREE,
			MORY_TREE_2, DEAD_TREE, BIG_DEAD_TREE, ROTTING_TREE, HUGE_MUSHROOM, HUGE_MUSHROOM + 1, HUGE_MUSHROOM + 2, ROOTS, MOSSY_ROCK,
			SMALL_MOSSY_ROCK, SMALL_BUSH, LONG_GRASS, GRASS, SMALL_FERN, ROTTING_BRANCH, FUNGUS));

	/** Overlays that should blend with each other when smoothing edges (sea and cove water are one body). */
	static int overlayGroup(int overlay) {
		return overlay == WATER || overlay == GUNGY_WATER ? WATER : overlay;
	}

	/**
	 * Cuts the corners of overlay patches (water, paths, painted features) diagonally, the way the game's own maps
	 * do: a tile whose overlay only continues to two adjacent sides becomes a triangle (shape 1). Rotation picks
	 * the corner the triangle sits in: 0 = south-west, 1 = north-west, 2 = north-east, 3 = south-east.
	 */
	void smoothOverlays() {
		smoothGrid(worldGrid(), BASE_X + 1, BASE_Y + 1, BASE_X + 64 * SURFACE_REGIONS - 2, BASE_Y + 64 * SURFACE_REGIONS - 2,
				CursedIslandGenerator::overlayGroup, ov -> ov == WOOD, ov -> overlayGroup(ov) != WATER);
	}

	/** The surface overlays, in world coordinates. */
	OverlayGrid worldGrid() {
		return new OverlayGrid() {
			public int overlay(int x, int y) {
				return overlayAt(x, y);
			}

			public int shape(int x, int y) {
				MapRegion r = regionAt(x, y);
				return r == null ? 0 : r.overlayShape[0][x & 63][y & 63];
			}

			public void set(int x, int y, int overlay, int shape, int rotation) {
				MapRegion r = regionAt(x, y);
				r.overlay[0][x & 63][y & 63] = overlay;
				r.overlayShape[0][x & 63][y & 63] = shape;
				r.overlayRotation[0][x & 63][y & 63] = rotation;
			}

			public boolean frozen(int x, int y) {
				return stamped.contains(x << 16 | y);
			}
		};
	}

	/** Tiles copied from the game's own buildings: their floors are already shaped, so smoothing leaves them alone. */
	final java.util.Set<Integer> stamped = new java.util.HashSet<>();

	/** Read/write access to one layer of overlays, in world or region-local coordinates. */
	interface OverlayGrid {
		int overlay(int x, int y);

		int shape(int x, int y);

		void set(int x, int y, int overlay, int shape, int rotation);

		/** Tiles smoothing must not touch (stamped buildings). */
		default boolean frozen(int x, int y) {
			return false;
		}
	}

	/** Overlay tile shapes, as the client numbers them (stored value; the client adds one). */
	static final int SHAPE_HALF = 1, SHAPE_CORNER = 7;

	/**
	 * Softens overlay edges the way the game's own maps do, so patches stop reading as stacks of squares:
	 * <ul>
	 * <li>lone one-tile speckles are removed (where {@code speckles} allows it);</li>
	 * <li>outside corners - a tile whose overlay continues to exactly two adjacent sides - and dead ends become a
	 * diagonal half (shape 1);</li>
	 * <li>inside corners - a bare tile cupped by two full overlay tiles and the diagonal between them - get a small
	 * corner triangle (shape 7), so the patch's edge runs on diagonally instead of stepping.</li>
	 * </ul>
	 * Rotation picks the corner: 0 = south-west, 1 = north-west, 2 = north-east, 3 = south-east. {@code ignore}
	 * overlays (wooden floors, invisible cave rock) are never changed. Returns the tiles that were cut to halves.
	 */
	static List<int[]> smoothGrid(OverlayGrid g, int x0, int y0, int x1, int y1, java.util.function.IntUnaryOperator group,
			java.util.function.IntPredicate ignore, java.util.function.IntPredicate speckles) {
		// lone speckles
		for (int x = x0; x <= x1; x++) {
			for (int y = y0; y <= y1; y++) {
				int ov = g.overlay(x, y);
				if (ov == 0 || ignore.test(ov) || !speckles.test(ov) || g.shape(x, y) != 0 || g.frozen(x, y)) {
					continue;
				}
				int gr = group.applyAsInt(ov);
				if (group.applyAsInt(g.overlay(x, y + 1)) != gr && group.applyAsInt(g.overlay(x + 1, y)) != gr
						&& group.applyAsInt(g.overlay(x, y - 1)) != gr && group.applyAsInt(g.overlay(x - 1, y)) != gr) {
					g.set(x, y, 0, 0, 0);
				}
			}
		}
		// outside corners
		List<int[]> halves = new ArrayList<>();
		for (int x = x0; x <= x1; x++) {
			for (int y = y0; y <= y1; y++) {
				int ov = g.overlay(x, y);
				if (ov == 0 || ignore.test(ov) || g.shape(x, y) != 0 || g.frozen(x, y)) {
					continue;
				}
				int gr = group.applyAsInt(ov);
				boolean n = group.applyAsInt(g.overlay(x, y + 1)) == gr, e = group.applyAsInt(g.overlay(x + 1, y)) == gr,
						so = group.applyAsInt(g.overlay(x, y - 1)) == gr, w = group.applyAsInt(g.overlay(x - 1, y)) == gr;
				int count = (n ? 1 : 0) + (e ? 1 : 0) + (so ? 1 : 0) + (w ? 1 : 0);
				if (count == 2 && !(n && so) && !(e && w)) {
					halves.add(new int[] { x, y, so && w ? 0 : w && n ? 1 : n && e ? 2 : 3 });
				} else if (count == 1) { // a dead end: slant it off rather than stopping square
					halves.add(new int[] { x, y, w ? 0 : n ? 1 : e ? 2 : 3 });
				}
			}
		}
		for (int[] c : halves) {
			g.set(c[0], c[1], g.overlay(c[0], c[1]), SHAPE_HALF, c[2]);
		}
		// inside corners
		List<int[]> corners = new ArrayList<>();
		int[][] dirs = { { -1, 0, 0, -1 }, { -1, 0, 0, 1 }, { 1, 0, 0, 1 }, { 1, 0, 0, -1 } }; // SW, NW, NE, SE
		for (int x = x0; x <= x1; x++) {
			for (int y = y0; y <= y1; y++) {
				if (g.overlay(x, y) != 0 || g.frozen(x, y)) {
					continue;
				}
				for (int rot = 0; rot < 4; rot++) {
					int[] d = dirs[rot];
					int a = g.overlay(x + d[0], y + d[1]), b = g.overlay(x + d[2], y + d[3]);
					int diag = g.overlay(x + d[0] + d[2], y + d[1] + d[3]);
					int oppA = g.overlay(x - d[0], y - d[1]), oppB = g.overlay(x - d[2], y - d[3]);
					if (a != 0 && a == b && a == diag && !ignore.test(a) && g.shape(x + d[0], y + d[1]) == 0
							&& g.shape(x + d[2], y + d[3]) == 0 && oppA != a && oppB != a) {
						corners.add(new int[] { x, y, a, rot });
						break;
					}
				}
			}
		}
		for (int[] c : corners) {
			g.set(c[0], c[1], c[2], SHAPE_CORNER, c[3]);
		}
		return halves;
	}

	int overlayAt(int x, int y) {
		MapRegion r = regionAt(x, y);
		return r == null ? 0 : r.overlay[0][x & 63][y & 63];
	}

	int underlayAt(int x, int y) {
		MapRegion r = regionAt(x, y);
		return r == null ? 0 : r.underlay[0][x & 63][y & 63];
	}

	/** The graveyard sits on a low plateau in the west. */
	static double westPlateau(int x, int y) {
		return 7 * smooth(1 - Math.hypot(x - 1989, y - 2918) / 13.0);
	}

	void sea(int x, int y) {
		MapRegion r = regionAt(x, y);
		int lx = x & 63, ly = y & 63;
		r.overlay[0][lx][ly] = WATER;
		// a seabed of dark sand: where the shoreline is cut diagonally, the dry half of the tile shows this rather
		// than the black of a tile with no floor at all
		r.underlay[0][lx][ly] = DARK_SAND;
		r.settings[0][lx][ly] = MapRegion.BLOCKED;
		r.height[0][lx][ly] = 1; // sea level
	}

	void block(int x, int y) {
		regionAt(x, y).settings[0][x & 63][y & 63] |= MapRegion.BLOCKED;
	}

	/** Pillars of black rock standing in the sea, the tallest guarding the mouth of the cove. */
	void seaStacks() {
		int[][] stacks = { { 2005, 2860, 46 }, { 2028, 2858, 38 }, { 2034, 2865, 26 }, { 1999, 2866, 22 },
				{ 1968, 2900, 30 }, { 1972, 2935, 40 }, { 2062, 2930, 34 }, { 2058, 2890, 28 }, { 2044, 2955, 44 },
				{ 1990, 2958, 30 } };
		for (int[] s : stacks) {
			for (int x = s[0] - 2; x <= s[0] + 2; x++) {
				for (int y = s[1] - 2; y <= s[1] + 2; y++) {
					double r = Math.hypot(x - s[0], y - s[1]);
					if (r > 1.9 || inland(x, y) >= 0) {
						continue;
					}
					MapRegion reg = regionAt(x, y);
					int lx = x & 63, ly = y & 63;
					reg.overlay[0][lx][ly] = 0;
					reg.underlay[0][lx][ly] = DARK_ROCK;
					reg.height[0][lx][ly] = (int) (s[2] * (1 - r / 2.2) + 2);
				}
			}
		}
	}

	/** Stagnant green pools and bubbling mud in the east, between the skull and the serpent's beach. */
	void swamp() {
		for (int x = 2028; x < 2056; x++) {
			for (int y = 2914; y < 2944; y++) {
				double d = inland(x, y);
				if (d < 4 || skullHeight(x, y) > 1) {
					continue;
				}
				double n = noise(x * 2 + 40, y * 2 - 13) + 0.35 * noise(x * 7, y * 7);
				if (n > 0.62) {
					setOverlay(x, y, GUNGY_WATER);
					block(x, y);
					setHeight(x, y, heightAt(x, y) - 2);
					if (random.nextInt(7) == 0) {
						object(SWAMP_BUBBLES, x, y, TYPE_GROUND_DECOR, 0);
					}
				} else if (n > 0.45) {
					setUnderlay(x, y, BURGH_SOIL);
				}
			}
		}
	}

	/** Dirt roads (as waypoints) joining the jetty, the town deck, the skull, the graveyard and the serpent's bones. */
	void paths() {
		// jetty -> town deck -> jaw plaza
		road(2.6, ROAD, new int[][] { { 2016, 2892 }, { 2016, 2901 } });
		road(2.6, ROAD, new int[][] { { 2016, 2910 }, { 2014, 2914 }, { 2017, 2918 }, { 2016, 2921 } });
		// deck -> graveyard (west)
		road(2.2, ROAD, new int[][] { { 2011, 2906 }, { 2005, 2909 }, { 1999, 2913 }, { 1993, 2916 } });
		// deck -> swamp -> serpent beach (east): it bends south round the serpent's ribcage and ends at its skull, rather
		// than running through the ribs
		road(2.2, ROAD, new int[][] { { 2021, 2906 }, { 2027, 2909 }, { 2033, 2908 }, { 2037, 2903 }, { 2042, 2899 }, { 2047, 2897 } });
		// Mad Pete's camp, off the east road (wide enough that it joins the road along an edge, not at a corner)
		road(2.4, ROAD, new int[][] { { 2027, 2909 }, { 2029, 2906 }, { 2030, 2904 } });
	}

	/**
	 * Paints a road of the given width through the waypoints: every tile within width/2 of the line gets the overlay,
	 * so there are no gaps or tiles that only touch diagonally. The line wanders a little between the waypoints (a
	 * trodden track never runs ruler-straight) and its width breathes, but it still starts and ends exactly on them.
	 */
	void road(double width, int floor, int[][] points) {
		int seed = points[0][0] * 31 + points[0][1];
		for (double[] pt : VoidRealmGenerator.meander(points, 0.9, 7, seed)) {
			double px = pt[0] + 0.5, py = pt[1] + 0.5;
			double w = width * (0.9 + 0.2 * VoidRealmGenerator.vnoise(pt[2] / 5, 0.5, seed + 1));
			int r = (int) Math.ceil(w);
			for (int x = (int) px - r; x <= (int) px + r; x++) {
				for (int y = (int) py - r; y <= (int) py + r; y++) {
					if (Math.hypot(x + 0.5 - px, y + 0.5 - py) <= w / 2 && inland(x, y) > 0.5 && skullHeight(x, y) < 4
							&& overlayAt(x, y) != WOOD) {
						setOverlay(x, y, floor);
					}
				}
			}
		}
	}

	/** The jetty runs south from the beach into the cove; the bosun ties up at its end. */
	void jetty() {
		for (int y = 2878; y <= 2892; y++) {
			for (int x = 2015; x <= 2017; x++) {
				plank(x, y);
			}
		}
		for (int x = 2012; x <= 2020; x++) {
			for (int y = 2876; y <= 2878; y++) {
				plank(x, y);
			}
		}
		// torches on the jetty's outer planks (not standing in the sea beside it)
		object(GHOST_TORCH, 2015, 2884, TYPE_SCENERY, 0);
		object(GHOST_TORCH, 2017, 2884, TYPE_SCENERY, 0);
		object(GHOST_TORCH, 2012, 2878, TYPE_SCENERY, 0);
		object(GHOST_TORCH, 2020, 2878, TYPE_SCENERY, 0);
		object(BARREL, 2013, 2876, TYPE_SCENERY, 0);
		object(CRATE, 2020, 2876, TYPE_SCENERY, 0);
		object(PIRATE_FLAG, 2018, 2890, TYPE_SCENERY, 1);
	}

	/**
	 * The west beach, where the crew still net shrimp off the shingle: one bare dead tree leans out over the tide line
	 * (a choppable tree - the only firewood near town), with a beached row boat and nets drying beside it.
	 * The fishing spot itself is an npc (316) just off this beach, in spawns.json.
	 */
	void fishingBeach() {
		object(DEAD_TREE, 1983, 2897, TYPE_SCENERY, 1);
		object(ROW_BOAT_2, 1981, 2891, TYPE_SCENERY, 0);
		object(NET_DRYING, 1984, 2893, TYPE_SCENERY, 1);
	}

	void plank(int x, int y) {
		MapRegion r = regionAt(x, y);
		int lx = x & 63, ly = y & 63;
		r.overlay[0][lx][ly] = WOOD;
		if (inland(x, y) < 0) {
			r.settings[0][lx][ly] = 0;
			r.height[0][lx][ly] = 4;
		}
	}

	/**
	 * Rattlebone Row: the shanty town the crew built when they first came ashore. Shacks are copied from
	 * Mos Le'Harmless (region 14638/14637); everything with an option (lockers, ladders, bank booths) is left out.
	 */
	void rattleboneRow() {
		// town square
		for (int x = 2011; x <= 2021; x++) {
			for (int y = 2901; y <= 2910; y++) {
				if (Math.hypot(x - 2016, y - 2905.5) < 5.6) {
					setOverlay(x, y, WOOD); // a deck of salvaged ship's planks
					removeObjectsAt(x, y);
				}
			}
		}
		flatten(2010, 2899, 2022, 2912);
		object(GALLOWS, 2018, 2907, TYPE_SCENERY, 0);
		object(GHOST_TORCH, 2012, 2902, TYPE_SCENERY, 0);
		object(GHOST_TORCH, 2020, 2902, TYPE_SCENERY, 0);
		object(GHOST_TORCH, 2012, 2909, TYPE_SCENERY, 0);
		object(BROKEN_BARREL, 2013, 2906, TYPE_SCENERY, 0);
		object(PILE_OF_SKULLS, 2021, 2905, TYPE_SCENERY, 0);

		// shacks (source rectangle in Mos Le'Harmless -> destination south-west corner)
		stampBuilding(3670, 2963, 3678, 2972, 2001, 2896); // west of the square, door to the south
		stampBuilding(3652, 2969, 3658, 2979, 2023, 2898); // east of the square: Mad Pete's shack
		stampBuilding(3673, 2985, 3679, 2993, 2003, 2911); // north-west, towards the graveyard
		stampBuilding(3678, 2978, 3684, 2985, 2028, 2913); // a lean-to on the swamp's edge

		// the boardwalk along the beach, and what the crew dragged ashore
		for (int x = 2000; x <= 2034; x++) {
			for (int y = 2892; y <= 2893; y++) {
				if (inland(x, y) > 0) {
					setOverlay(x, y, WOOD);
					removeObjectsAt(x, y);
				}
			}
		}
		object(ROW_BOAT, 2025, 2888, TYPE_SCENERY, 1);
		object(ROW_BOAT_2, 2029, 2886, TYPE_SCENERY, 2);
		object(NET_DRYING, 2022, 2891, TYPE_SCENERY, 0);
		object(TORN_SAIL, 2034, 2889, TYPE_SCENERY, 1);
		object(GHOST_LAMP_POST, 2014, 2894, TYPE_SCENERY, 0);
		object(GHOST_LAMP_POST, 2018, 2894, TYPE_SCENERY, 0);
		object(GHOST_LAMP_POST, 2004, 2894, TYPE_SCENERY, 0);
		object(GHOST_LAMP_POST, 2030, 2894, TYPE_SCENERY, 0);
		// the square: stocks, a cage, and lamps
		object(STOCKS, 2013, 2903, TYPE_SCENERY, 0);
		object(SKELETON, 2013, 2902, TYPE_GROUND_DECOR, 0);
		object(CAGE, 2020, 2903, TYPE_SCENERY, 0);
		object(GHOST_LAMP, 2011, 2906, TYPE_SCENERY, 0);
		object(GHOST_LAMP, 2021, 2908, TYPE_SCENERY, 0);
		object(TOTEM, 2016, 2911, TYPE_SCENERY, 0);

		// Mad Pete's camp in front of his shack
		object(STACKED_BARRELS, 2031, 2898, TYPE_SCENERY, 0);
		object(CRATES, 2031, 2901, TYPE_SCENERY, 0);
		object(CANNON, 2033, 2904, TYPE_SCENERY, 1);
		object(CANNONBALLS, 2033, 2907, TYPE_SCENERY, 0);
		object(GHOST_TORCH, 2022, 2896, TYPE_SCENERY, 0);
		object(PIRATE_FLAG, 2026, 2896, TYPE_SCENERY, 0);
	}

	/** Flattens the ground to the average height of a rectangle. */
	void flatten(int x0, int y0, int x1, int y1) {
		int sum = 0, n = 0;
		for (int x = x0; x <= x1; x++) {
			for (int y = y0; y <= y1; y++) {
				sum += heightAt(x, y);
				n++;
			}
		}
		for (int x = x0; x <= x1; x++) {
			for (int y = y0; y <= y1; y++) {
				setHeight(x, y, sum / n);
			}
		}
	}

	/** Copies a building (floor, walls, roof, furniture without options) onto flattened ground. */
	void stampBuilding(int ax0, int ay0, int ax1, int ay1, int destX, int destY) {
		int w = ax1 - ax0, h = ay1 - ay0;
		flatten(destX - 1, destY - 1, destX + w + 1, destY + h + 1);
		for (int x = destX - 1; x <= destX + w + 1; x++) {
			for (int y = destY - 1; y <= destY + h + 1; y++) {
				stamped.add(x << 16 | y);
				removeObjectsAt(x, y);
			}
		}
		int region = (ax0 >> 6) << 8 | (ay0 >> 6);
		stamp(region, ax0 & 63, ay0 & 63, ax1 & 63, ay1 & 63, destX, destY, new int[0], true);
		if (ax0 == 3673 && ay0 == 2985) {
			// this rectangle catches a corner of the next building in Mos Le'Harmless: a wall panel standing on its own
			removeObjects(destX + 5, destY, o -> o.id == 11325);
			removeObjects(destX + 6, destY, o -> o.id == 11325);
		}
	}

	void shipwreck() {
		// The Drowned Maiden: a copy of one of the Port Sarim ships (region 12082, local 22..30 x 9..28), run aground
		// on the west side of the cove with her bow on the beach. Gangplanks are left behind.
		stamp(12082, 22, 9, 30, 28, 2003, 2866, new int[] { 2083, 2084 }, false);
		object(BROKEN_BARREL, 2004, 2889, TYPE_SCENERY, 0);
		object(ANCHOR, 2006, 2884, TYPE_SCENERY, 2);
		object(CRATE, 1992, 2884, TYPE_SCENERY, 0);
		object(ROTTING_LOG, 2008, 2887, TYPE_SCENERY, 1);
		// Captain Blackbrine's remains, washed up beside his ship (searchable - holds his log)
		object(CAPTAINS_SKELETON, CAPTAINS_SKELETON_X, CAPTAINS_SKELETON_Y, TYPE_SCENERY, 1);
		object(LONG_SKELETON, 1999, 2890, TYPE_GROUND_DECOR, 2);
	}

	/** The masts of older wrecks poke out of the cove. */
	void coveWrecks() {
		int[][] masts = { { 2024, 2869 }, { 2027, 2873 }, { 2009, 2866 }, { 2030, 2878 } };
		for (int[] m : masts) {
			object(MAST, m[0], m[1], TYPE_SCENERY, random.nextInt(4));
		}
		object(HULL, 2025, 2871, TYPE_SCENERY, 1);
		object(HULL, 2010, 2864, TYPE_SCENERY, 2);
	}

	void graveyard() {
		int cx = 1988, cy = 2918;
		int[] kinds = { GRAVESTONE, SMALL_GRAVESTONE, GRAVE_MARKER };
		int i = 0;
		for (int gx = cx - 6; gx <= cx + 6; gx += 3) {
			for (int gy = cy - 6; gy <= cy + 6; gy += 3) {
				if (Math.hypot(gx - cx, gy - cy) < 7.5 && !(gx == cx && gy == cy) && random.nextInt(5) != 0) {
					object(kinds[i++ % kinds.length], gx, gy, TYPE_SCENERY, 1);
				}
			}
		}
		for (int x = cx - 8; x <= cx + 8; x++) {
			for (int y = cy - 8; y <= cy + 8; y++) {
				if (Math.hypot(x - cx, y - cy) < 8.5) {
					setUnderlay(x, y, BURGH_SOIL);
				}
			}
		}
		// the captain's empty grave, with ghostfire braziers
		object(LARGE_GRAVE, cx, cy, TYPE_SCENERY, 1);
		object(GHOSTFIRE, cx - 2, cy + 2, TYPE_SCENERY, 0);
		object(GHOSTFIRE, cx + 2, cy + 2, TYPE_SCENERY, 0);
		object(SKULL_PILE, cx + 5, cy - 7, TYPE_SCENERY, 0);
		object(LONG_SKELETON, cx - 7, cy - 3, TYPE_GROUND_DECOR, 0);
		object(MORY_TREE_BIG, cx - 9, cy + 6, TYPE_SCENERY, 0);
		object(MORY_TREE, cx + 8, cy + 7, TYPE_SCENERY, 0);
		object(BIG_DEAD_TREE, cx - 8, cy - 8, TYPE_SCENERY, 0);
		object(PIRATE_FLAG, cx + 1, cy + 8, TYPE_SCENERY, 0);
		object(OPEN_COFFIN, cx + 4, cy + 4, TYPE_SCENERY, 1);
		object(OPEN_COFFIN, cx - 5, cy - 5, TYPE_SCENERY, 0);
		object(CROSS, cx - 1, cy + 7, TYPE_SCENERY, 0); // between the grave rows, not on a grave
		object(CROSS, cx + 6, cy - 2, TYPE_SCENERY, 0);
		object(GHOST_LAMP_POST, cx + 7, cy - 6, TYPE_SCENERY, 0);
		object(GHOST_LAMP_POST, cx - 7, cy + 4, TYPE_SCENERY, 0);
		object(SKELETON, cx + 3, cy - 4, TYPE_GROUND_DECOR, 2);
		object(PILE_OF_SKULLS, cx - 4, cy + 1, TYPE_SCENERY, 0);
	}

	/** The bones of a sea serpent lie half-buried on the east beach. */
	void serpentBeach() {
		object(SERPENT_SKULL, 2049, 2896, TYPE_SCENERY, 1);
		// ribs either side of the spine, running north-west up the beach
		for (int i = 0; i < 6; i++) {
			int x = 2047 - i * 2, y = 2902 + i * 2;
			object(GIANT_RIBS, x - 1, y + 1, TYPE_SCENERY, 3);
			object(GIANT_RIBS, x + 2, y - 1, TYPE_SCENERY, 1);
			object(LARGE_BONE, x, y, TYPE_GROUND_DECOR, i & 3);
		}
		object(CURVED_BONE, 2033, 2916, TYPE_SCENERY, 1);
		object(TORN_SAIL, 2052, 2918, TYPE_SCENERY, 0);
		object(ROW_BOAT_2, 2056, 2909, TYPE_SCENERY, 3);
		object(CURVED_BONE, 2054, 2903, TYPE_SCENERY, 2);
	}

	void skullRock() {
		// the jaw plaza: flat, trodden ground below the chin
		int plazaH = heightAt(SKX, FACE_Y0 - 7);
		for (int x = SKX - 11; x <= SKX + 11; x++) {
			for (int y = FACE_Y0 - 8; y <= FACE_Y0 - 1; y++) {
				if (Math.hypot((x - SKX + 0.5) / 1.6, y - FACE_Y0 + 0.5) < 7.5) {
					setHeight(x, y, plazaH + (int) Math.round(noise(x * 6, y * 6)));
					setOverlay(x, y, 0);
					setUnderlay(x, y, noise(x * 4 + 1, y * 4) > 0.2 ? ASH : BURGH_SOIL);
					removeObjectsAt(x, y);
				}
			}
		}
		// the road from town crosses the plaza as a trodden track and runs right up to the cave mouth
		for (double t = 0; t <= 1; t += 0.05) {
			double px = SKX + 0.5 - t * 1.0 + Math.sin(t * Math.PI * 2) * 1.1, py = FACE_Y0 - 8 + t * 7.4;
			for (int x = (int) px - 1; x <= (int) px + 1; x++) {
				for (int y = (int) py - 1; y <= (int) py + 1; y++) {
					if (Math.hypot(x + 0.5 - px, y + 0.5 - py) <= 1.05) {
						setOverlay(x, y, ROAD);
					}
				}
			}
		}
		// the mouth: the cave opens between the front teeth
		object(CAVE_ENTRANCE, CAVE_ENTRANCE_X, CAVE_ENTRANCE_Y, TYPE_SCENERY, 0);
		// the face itself - brows, sockets, nose, cheekbones, teeth and jaw - is a sculpted model laid over this terrain
		// (SkullRock, placed by VoidRealmGenerator once the whole island is finished)
		// ghostfire burns deep in the eye sockets
		int eyeY = FACE_Y0 + (int) Math.round(0.62 * FACE_DEPTH);
		for (int side = -1; side <= 1; side += 2) {
			int ex = SKX + side * 6 - (side < 0 ? 1 : 0);
			object(GHOSTFIRE, ex, eyeY, TYPE_SCENERY, 0);
			object(GHOSTFIRE, ex - 1, eyeY + 1, TYPE_SCENERY, 0);
			object(GHOSTFIRE, ex + 1, eyeY - 1, TYPE_SCENERY, 0);
		}
		// torches lead the way in
		object(GHOST_TORCH, SKX - 3, FACE_Y0 - 3, TYPE_SCENERY, 0);
		object(GHOST_TORCH, SKX + 2, FACE_Y0 - 3, TYPE_SCENERY, 0);
		int[][] bones = { { -6, -5 }, { 5, -6 }, { -9, -4 }, { 8, -3 }, { -2, -7 }, { 3, -2 }, { -4, -2 } };
		for (int i = 0; i < bones.length; i++) {
			int[] kinds = { CURVED_BONE, LARGE_BONE, SKELETON, ANIMAL_SKULL };
			int k = kinds[i % kinds.length];
			object(k, SKX + bones[i][0], FACE_Y0 + bones[i][1], k == ANIMAL_SKULL ? TYPE_SCENERY : TYPE_GROUND_DECOR, i & 3);
		}
		object(SKULL_PILE, SKX - 8, FACE_Y0 - 2, TYPE_SCENERY, 0);
		object(SKULL_PILE, SKX + 7, FACE_Y0 - 2, TYPE_SCENERY, 0);
		// a black flag flies from the crown
		object(PIRATE_FLAG, SKX, FACE_Y0 + FACE_DEPTH + 5, TYPE_SCENERY, 0);
	}

	void vegetation() {
		// pass 1: trees, palms and other big things, spaced out; pass 2: ground cover between them
		for (int pass = 0; pass < 2; pass++) {
			for (int x = BASE_X; x < BASE_X + 64 * SURFACE_REGIONS; x++) {
				for (int y = BASE_Y; y < BASE_Y + 64 * SURFACE_REGIONS; y++) {
					double d = inland(x, y);
					if (d < 1 || skullHeight(x, y) > 2 || !isFree(x, y, 0)) {
						continue;
					}
					if ((regionAt(x, y).settings[0][x & 63][y & 63] & MapRegion.BLOCKED) != 0) {
						continue;
					}
					if (pass == 0) {
						bigPlant(x, y, d);
					} else {
						groundCover(x, y, d);
					}
				}
			}
		}
	}

	void bigPlant(int x, int y, double d) {
		int roll = random.nextInt(1000), rot = random.nextInt(4);
		if (d < 4) {
			if (roll < 45 && isFree(x, y, 2)) {
				object(roll < 18 ? WITHERED_LEAFY_PALM : WITHERED_PALM, x, y, TYPE_SCENERY, rot);
			} else if (roll < 60 && isFree(x, y, 1)) {
				object(ROTTING_LOG, x, y, TYPE_SCENERY, rot);
			}
			return;
		}
		// thickets where the noise is high, clearings where it's low
		double thicket = noise(x * 2 + 91, y * 2 + 7);
		int treeChance = (int) (40 + 110 * Math.max(0, thicket));
		if (roll < treeChance && isFree(x, y, 2)) {
			int[] trees = { MORY_TREE, MORY_TREE_2, DEAD_TREE, BIG_DEAD_TREE, ROTTING_TREE, MORY_TREE, MORY_TREE_2,
					WITHERED_PALM };
			object(trees[random.nextInt(trees.length)], x, y, TYPE_SCENERY, rot);
		} else if (roll < treeChance + 12 && isFree(x, y, 2)) {
			object(HUGE_MUSHROOM + random.nextInt(3), x, y, TYPE_SCENERY, rot);
		} else if (roll < treeChance + 18 && isFree(x, y, 3)) {
			object(ROOTS, x, y, TYPE_SCENERY, rot);
		} else if (roll < treeChance + 30 && isFree(x, y, 1)) {
			object(random.nextInt(3) == 0 ? MOSSY_ROCK : SMALL_MOSSY_ROCK, x, y, TYPE_SCENERY, rot);
		} else if (roll < treeChance + 42 && isFree(x, y, 1)) {
			object(SMALL_BUSH, x, y, TYPE_SCENERY, rot);
		}
	}

	void groundCover(int x, int y, double d) {
		int roll = random.nextInt(1000), rot = random.nextInt(4);
		if (d < 4) {
			if (roll < 15) {
				object(ANIMAL_SKULL, x, y, TYPE_SCENERY, rot);
			} else if (roll < 40) {
				object(SMALL_MOSSY_ROCK, x, y, TYPE_SCENERY, rot);
			} else if (roll < 55) {
				object(CURVED_BONE, x, y, TYPE_GROUND_DECOR, rot);
			}
			return;
		}
		if (roll < 70) {
			object(LONG_GRASS, x, y, TYPE_GROUND_DECOR, rot);
		} else if (roll < 120) {
			object(GRASS, x, y, TYPE_GROUND_DECOR, rot);
		} else if (roll < 170) {
			object(SMALL_FERN, x, y, TYPE_GROUND_DECOR, rot);
		} else if (roll < 195) {
			object(ROTTING_BRANCH, x, y, TYPE_GROUND_DECOR, rot);
		} else if (roll < 225) {
			object(FUNGUS, x, y, TYPE_GROUND_DECOR, rot);
		} else if (roll < 232) {
			object(ANIMAL_SKULL, x, y, TYPE_SCENERY, rot);
		}
	}

	/**
	 * Copies a rectangle of an existing region (upper planes' terrain and every object) onto the island.
	 * Plane 0 terrain is left alone so the copy sits on our own ground, unless {@code withFloor} is set, in which
	 * case plane 0 overlays (floors) are copied too and objects with options (other than doors) are skipped.
	 */
	void stamp(int sourceRegion, int sx0, int sy0, int sx1, int sy1, int destX, int destY, int[] skipIds, boolean withFloor) {
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
				for (int z = withFloor ? 0 : 1; z < 4; z++) {
					if (z > 0) {
						dst.height[z][lx][ly] = src.height[z][x][y];
						dst.underlay[z][lx][ly] = src.underlay[z][x][y];
						dst.settings[z][lx][ly] = src.settings[z][x][y];
					}
					dst.overlay[z][lx][ly] = src.overlay[z][x][y];
					dst.overlayShape[z][lx][ly] = src.overlayShape[z][x][y];
					dst.overlayRotation[z][lx][ly] = src.overlayRotation[z][x][y];
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
			if (withFloor && hasOptions(o.id)) {
				continue;
			}
			int ax = destX + o.x - sx0, ay = destY + o.y - sy0;
			regionAt(ax, ay).objects.add(new MapRegion.MapObject(o.id, ax & 63, ay & 63, o.z, o.type, o.rotation));
		}
	}

	private ConfigDefinitions.Table locTable;

	/** True when an object has right-click options (we don't copy things players could use but nothing handles). */
	boolean hasOptions(int id) {
		if (locTable == null) {
			try {
				locTable = ConfigDefinitions.load(CacheEditor.Archive.decode(cache.read(0, ConfigDefinitions.CONFIG_ARCHIVE)),
						"loc", false);
			} catch (java.io.IOException e) {
				throw new java.io.UncheckedIOException(e);
			}
		}
		ConfigDefinitions.Definition d = locTable.decode(id);
		for (int op = 30; op < 35; op++) {
			String a = d.text(op);
			if (a != null && !a.equalsIgnoreCase("hidden")) {
				return true;
			}
		}
		return false;
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
		// the Dragonvein cavern, west of the portal chamber
		tunnel(open, 26, 53, 16, 51, 2);
		blob(open, 11, 50, 6, 5);

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

		// the portal chamber's floor is fused to violet glass in a ring around it - the same glass as the void's
		for (int x = 26; x <= 38; x++) {
			for (int y = 49; y <= 59; y++) {
				double dist = Math.hypot(x - 32, y - 53);
				if (open[x][y] && dist > 2.5 && dist < 3.8) {
					r.overlay[0][x][y] = VoidRealmGenerator.VOID_GLASS;
				}
			}
		}

		VoidRealmGenerator.smoothDungeon(r); // soften the ring's stair-stepped edge like every other cave

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
		// Dragonvein cavern: dragon rocks along its walls, and the void vein at its far end
		dragonRocks(r, open);
		voidRealm.voidVein(r, open);
		// portal chamber
		dobj(r, PORTAL, PORTAL_X, PORTAL_Y, TYPE_SCENERY, 0);
		dobj(r, CAVE_TORCH, 2012, 9333, TYPE_SCENERY, 0);
		dobj(r, CAVE_TORCH, 2020, 9333, TYPE_SCENERY, 0);
		dobj(r, DUNGEON_SKULLS, 2012, 9336, TYPE_SCENERY, 0);
		dobj(r, DUNGEON_SKULLS, 2021, 9337, TYPE_SCENERY, 0);
	}

	private boolean[][] dungeonOpen;

	/** Dragon rocks on open tiles against the Dragonvein cavern's wall, spaced apart. */
	void dragonRocks(MapRegion r, boolean[][] open) {
		List<int[]> placed = new ArrayList<>();
		for (int x = 4; x <= 18; x++) {
			for (int y = 44; y <= 56; y++) {
				if (!open[x][y] || !bordersClosed(open, x, y) || openAround(open, x, y, 1) < 4) {
					continue;
				}
				final int fx = x, fy = y;
				if (r.objects.stream().anyMatch(o -> o.x == fx && o.y == fy)) {
					continue;
				}
				if (placed.stream().anyMatch(p -> Math.abs(p[0] - fx) + Math.abs(p[1] - fy) < 5) || placed.size() >= 6) {
					continue;
				}
				placed.add(new int[] { x, y });
				r.objects.add(new MapRegion.MapObject(DragonMetal.DRAGON_ROCK, x, y, 0, TYPE_SCENERY, random.nextInt(4)));
			}
		}
		System.out.println("Dragon rocks: " + placed.size());
		for (int[] p : placed) {
			System.out.println("  dragon rock at " + (p[0] + DBASE_X) + "," + (p[1] + DBASE_Y));
		}
	}

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
	/*                        NPC + ITEM DEFINITIONS                          */
	/* ====================================================================== */

	/** Ghostly sea-green, used for the cursed crew. RS hue is 0..63 (0 = red, ~21 = green, ~42 = blue). */
	static final int GHOST_HUE = 27, VOID_HUE = 50;

	void buildDefinitions() throws java.io.IOException {
		config = CacheEditor.Archive.decode(cache.read(0, ConfigDefinitions.CONFIG_ARCHIVE));
		ConfigDefinitions.Table npcs = ConfigDefinitions.load(config, "npc", true);
		ConfigDefinitions.Table items = ConfigDefinitions.load(config, "obj", false);

		// Port Sarim's boatman, cloned from Captain Tobias
		npc(npcs, 376, BOSUN_GRIMSBY).name("Bosun Grimsby").examine("A weathered sailor with a haunted look.")
				.npcActions("Talk-to", null, null, null, null);
		// the last living survivor of the Drowned Maiden: a ship's cook (Lumbridge's, with his whites gone grey and
		// stained after weeks stranded on the island)
		npc(npcs, 278, MAD_PETE).name("Mad Pete").examine("The Drowned Maiden's cook. He hasn't slept in weeks.")
				.recolor(new int[] { 37, 57, 43928, 8860, 8741, 4626, 107, 123 }, new int[] { hsl(8, 1, 96), 43292, 43292, 43292,
						43177, 43280, hsl(8, 1, 82), hsl(8, 1, 98) })
				.npcActions("Talk-to", null, "Trade", null, null); // slot 2 = "second click" packet (slot 1 is attack)
		// the cursed crew: pirates washed in a pale, drowned green
		ghostly(npc(npcs, 182, CURSED_PIRATE), npcs, 182, GHOST_HUE, 2, 0.75, 22).name("Cursed pirate")
				.examine("A dead man who doesn't know it yet.").shorts(95, 12);
		// a drowned sailor: a pirate's model and rig (not the zombie's, whose few animations fling it about), in sea-teal
		ghostly(npc(npcs, 185, DROWNED_DECKHAND), npcs, 185, 33, 3, 0.7, 10).name("Drowned deckhand")
				.examine("Seaweed hangs from what's left of him.").shorts(95, 5);
		ghostly(npc(npcs, 184, CURSED_FIRST_MATE), npcs, 184, GHOST_HUE, 4, 0.6, 8).name("Cursed first mate")
				.examine("'Smiling' Sam, Blackbrine's first mate. Something glints at his neck.").shorts(95, 20)
				.shorts(97, 160).shorts(98, 160).set(12, new byte[] { 1 });
		ghostly(npc(npcs, 376, CAPTAIN_BLACKBRINE), npcs, 376, GHOST_HUE, 1, 0.5, 60).name("Ghost of Captain Blackbrine")
				.examine("The captain of the Drowned Maiden, bound to this place.").npcActions("Talk-to", null, null, null, null);

		// quest items
		item(items, 1493, BLACKBRINES_LOG).name("Blackbrine's log").examine("The water-stained log of the Drowned Maiden.")
				.itemActions("Read", null, null, null, "Drop");
		item(items, 432, CAPTAINS_KEY).name("Captain's key").examine("A heavy iron key, still warm from Sam's neck.");
		ConfigDefinitions.Definition pearl = item(items, 587, VOID_PEARL).name("Void pearl")
				.examine("It is darker than anything has a right to be. It hums.");
		int[][] tint = ConfigDefinitions.tint(cache, new int[] { pearl.itemModel() }, ConfigDefinitions.VOID, 4, 0.6, 0);
		pearl.recolor(tint[0], tint[1]);
		items.put(VOID_PEARL, pearl);
		voidRealm.npcs(npcs);

		// dragon metal: runite ore and bar, recoloured blood red
		ConfigDefinitions.Definition ore = item(items, 451, DragonMetal.DRAGON_ORE).name("Dragon ore")
				.examine("Blood-red ore. It's warm to the touch.").cost(12000);
		dragonRed(ore, new int[] { ore.itemModel() });
		ConfigDefinitions.Definition bar = item(items, 2363, DragonMetal.DRAGON_BAR).name("Dragon bar")
				.examine("It's a bar of dragon metal.").cost(20000);
		dragonRed(bar, new int[] { bar.itemModel() });
		voidRealm.items(items);

		ConfigDefinitions.Table locs = ConfigDefinitions.load(config, "loc", false);
		buildObjects(locs);

		live.forEach((table, defsById) -> defsById.forEach(table::put));
		ConfigDefinitions.save(config, npcs, items, locs);
		System.out.println("Definitions: " + npcs.size() + " npcs, " + items.size() + " items");
	}

	/** New scenery: recoloured and rescaled copies of existing objects. */
	void buildObjects(ConfigDefinitions.Table locs) throws java.io.IOException {
		if (locs.size() < LOC_BASE) {
			throw new IllegalStateException("loc.dat has " + locs.size() + " entries, expected " + LOC_BASE
					+ " - regenerate from the original cache");
		}
		// green ghost-fire: the curse's colour, burning in the skull's eyes and the braziers
		ConfigDefinitions.Definition fire = loc(locs, 2732, GHOSTFIRE).name("Ghostfire").examine("It burns, but gives no warmth.");
		tintLoc(fire, 23, 6, 1.0, 4, c -> true);
		ConfigDefinitions.Definition torch = loc(locs, 724, GHOST_TORCH).name("Ghostfire torch")
				.examine("Someone keeps these lit. You'd rather not meet them.");
		tintLoc(torch, 23, 6, 1.0, 4, c -> (c >> 7 & 7) == 7);
		// bone-white fangs: stalagmites recoloured and stretched
		loc(locs, 541, BONE_FANG).name("Fang").examine("A tooth as tall as a man.").remove(40).remove(21)
				.recolor(new int[] { 5916 }, new int[] { BONE }).scale(150, 330, 150);
		loc(locs, 541, BIG_FANG).name("Fang").examine("The skull's teeth. Some still have bits of ship in them.").remove(40)
				.remove(21).recolor(new int[] { 5916 }, new int[] { BONE_DARK }).scale(230, 560, 230);
		// the skull of a sea serpent, beached long ago
		loc(locs, 697, SERPENT_SKULL).name("Sea serpent skull").examine("Whatever killed this is still out there.")
				.scale(1000, 1000, 1000).footprint(4, 4);
		loc(locs, 14013, GIANT_RIBS).name("Serpent ribs").examine("The ribs of something enormous.").scale(260, 300, 260);
		// the Jolly Roger of the Drowned Maiden
		ConfigDefinitions.Definition flag = loc(locs, 10541, BLACK_FLAG).name("Black flag").examine("The colours of the Drowned Maiden.");
		tintLoc(flag, 0, 0, 0.2, 2, c -> c != 6932);
		// palms withered by the curse
		ConfigDefinitions.Definition palm = loc(locs, 1311, WITHERED_PALM).examine("This palm has seen better centuries.");
		tintLoc(palm, 9, 2, 0.9, 0, c -> (c >> 10) == 21);
		ConfigDefinitions.Definition leafy = loc(locs, 2975, WITHERED_LEAFY_PALM).examine("Its leaves rattle like dry bones.");
		tintLoc(leafy, 9, 2, 0.9, 0, c -> (c >> 10) == 15);
		// lamps burning with ghostfire
		tintLoc(loc(locs, 201, GHOST_LAMP).examine("The flame inside is the wrong colour."), 23, 6, 1.0, 4, c -> (c >> 7 & 7) == 7);
		tintLoc(loc(locs, 6202, GHOST_LAMP_POST).examine("A lamp post. The light is cold and green."), 23, 6, 1.0, 4,
				c -> (c >> 7 & 7) == 7);
		// dragon rocks: a runite rock with blood-red ore
		ConfigDefinitions.Definition rock = loc(locs, 2106, DragonMetal.DRAGON_ROCK).examine("A rock with veins of dragon ore.");
		dragonRed(rock, rock.locModels());
		voidRealm.locs(locs);
	}

	/** Dragon red: re-hues an existing recolour (runite's cyan), or the model's own colours if it has none. */
	void dragonRed(ConfigDefinitions.Definition d, int[] models) throws java.io.IOException {
		byte[] p = d.get(40);
		if (p != null) {
			int n = p[0] & 0xFF;
			int[] from = new int[n], to = new int[n];
			java.nio.ByteBuffer b = java.nio.ByteBuffer.wrap(p, 1, p.length - 1);
			for (int i = 0; i < n; i++) {
				from[i] = b.getShort() & 0xFFFF;
				int light = b.getShort() & 127;
				to[i] = DRAGON_HUE << 10 | 7 << 7 | light * 3 / 4;
			}
			d.recolor(from, to);
		} else {
			int[][] t = ConfigDefinitions.tint(cache, models, DRAGON_HUE, 7, 0.9, 0);
			d.recolor(t[0], t[1]);
		}
	}

	static final int DRAGON_HUE = 1;

	ConfigDefinitions.Definition loc(ConfigDefinitions.Table table, int source, int id) {
		return putLive(table, id, table.decode(source));
	}

	void tintLoc(ConfigDefinitions.Definition d, int hue, int sat, double lightScale, int lightAdd,
			java.util.function.IntPredicate filter) throws java.io.IOException {
		int[][] t = ConfigDefinitions.tint(cache, d.locModels(), hue, sat, lightScale, lightAdd, filter);
		d.recolor(t[0], t[1]);
	}

	/** Clones NPC {@code source} into id {@code id} (appending if it is the next free id). */
	ConfigDefinitions.Definition npc(ConfigDefinitions.Table table, int source, int id) {
		return putLive(table, id, table.decode(source));
	}

	ConfigDefinitions.Definition item(ConfigDefinitions.Table table, int source, int id) {
		ConfigDefinitions.Definition d = table.decode(source);
		d.remove(97).remove(98); // never inherit noted-item links
		return putLive(table, id, d);
	}

	/** Recolours an NPC definition's models. */
	ConfigDefinitions.Definition ghostly(ConfigDefinitions.Definition d, ConfigDefinitions.Table table, int source, int hue,
			int saturation, double lightScale, int lightAdd) throws java.io.IOException {
		int[][] tint = ConfigDefinitions.tint(cache, d.npcModels(), hue, saturation, lightScale, lightAdd);
		return d.recolor(tint[0], tint[1]);
	}

	/**
	 * Definitions are edited after being placed, so keep a list of them and (re)encode everything when saving.
	 * Ids must be contiguous with the existing table (or replace an existing id).
	 */
	private final Map<ConfigDefinitions.Table, Map<Integer, ConfigDefinitions.Definition>> live = new HashMap<>();

	ConfigDefinitions.Definition putLive(ConfigDefinitions.Table table, int id, ConfigDefinitions.Definition d) {
		if (id > table.size()) {
			throw new IllegalStateException("definition id " + id + " would leave a gap (table has " + table.size() + ")");
		}
		table.put(id, d);
		live.computeIfAbsent(table, t -> new HashMap<>()).put(id, d);
		return d;
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

	void removeObjects(int x, int y, java.util.function.Predicate<MapRegion.MapObject> which) {
		regionAt(x, y).objects.removeIf(o -> o.x == (x & 63) && o.y == (y & 63) && which.test(o));
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
	private ConfigDefinitions.Table stockLocs;
	private final Map<Integer, int[]> locSizes = new HashMap<>();

	/** An object's footprint (width, length) from the cache's own definitions; our new objects count as 4 x 4. */
	int[] locSize(int id) {
		return locSizes.computeIfAbsent(id, i -> {
			try {
				if (stockLocs == null) {
					stockLocs = ConfigDefinitions.load(CacheEditor.Archive.decode(cache.read(0, ConfigDefinitions.CONFIG_ARCHIVE)), "loc",
							false);
				}
			} catch (java.io.IOException e) {
				throw new java.io.UncheckedIOException(e);
			}
			if (i >= stockLocs.size()) {
				return new int[] { 4, 4 }; // ours are mostly big set pieces (the serpent's skull, its ribs): be generous
			}
			ConfigDefinitions.Definition d = stockLocs.decode(i);
			byte[] w = d.get(14), l = d.get(15);
			return new int[] { w == null ? 1 : w[0] & 0xFF, l == null ? 1 : l[0] & 0xFF };
		});
	}

	/**
	 * Whether no object's footprint covers any tile within r of (x, y). Objects are anchored at their south-west tile
	 * and grow north-east, so a big one anchored a few tiles away can still be standing here: checking anchors alone
	 * lets rocks get planted inside trees and boats.
	 */
	boolean footprintFree(int x, int y, int r) {
		java.util.Set<MapRegion> seen = new java.util.HashSet<>();
		for (int[] c : new int[][] { { x - r - 3, y - r - 3 }, { x + r, y - r - 3 }, { x - r - 3, y + r }, { x + r, y + r } }) {
			MapRegion reg = regions.get((c[0] >> 6) << 8 | (c[1] >> 6));
			if (reg == null || !seen.add(reg)) {
				continue;
			}
			for (MapRegion.MapObject o : reg.objects) {
				int ox = reg.regionX * 64 + o.x, oy = reg.regionY * 64 + o.y;
				if (o.z != 0 || ox > x + r || oy > y + r || ox < x - r - 3 || oy < y - r - 3) {
					continue;
				}
				int[] size = locSize(o.id);
				int sx = (o.rotation & 1) == 1 ? size[1] : size[0], sy = (o.rotation & 1) == 1 ? size[0] : size[1];
				if (ox + sx - 1 >= x - r && oy + sy - 1 >= y - r) {
					return false;
				}
			}
		}
		return true;
	}

	boolean isFree(int x, int y, int r) {
		for (int dx = -r; dx <= r; dx++) {
			for (int dy = -r; dy <= r; dy++) {
				MapRegion reg = regionAt(x + dx, y + dy);
				if (reg == null) {
					return false;
				}
				int lx = (x + dx) & 63, ly = (y + dy) & 63;
				if (reg.overlay[0][lx][ly] != 0) {
					return false;
				}
			}
		}
		if (!footprintFree(x, y, r)) {
			return false;
		}
		// keep the town, graveyard, wreck and plaza open
		return Math.hypot(x - 2016, y - 2905) > 7 && Math.hypot(x - 1988, y - 2918) > 9
				&& Math.hypot(x - 2008, y - 2888) > 4 && Math.hypot(x - SKX, y - FACE_Y0 + 4) > 11
				&& Math.hypot(x - 2028, y - 2903) > 5;
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
			if (config != null) {
				cache.write(0, ConfigDefinitions.CONFIG_ARCHIVE, config.encode());
			}
			System.out.println("Updated versionlist: " + index.size() + " regions, " + tables[0].length / 2 + " map files.");
		}
	}
}
