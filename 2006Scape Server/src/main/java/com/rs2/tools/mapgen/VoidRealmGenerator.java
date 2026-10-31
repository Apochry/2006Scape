package com.rs2.tools.mapgen;

import static com.rs2.game.content.voidrealm.VoidConstants.*;
import static com.rs2.tools.mapgen.CursedIslandGenerator.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Everything for the Void quest series, run as part of {@link CursedIslandGenerator}:
 * <ul>
 * <li>new NPCs, items and objects (cloned from existing ones and recoloured),</li>
 * <li>quest scenery on Skullbrine Isle (the weeping eye, the epitaph graves, the void vein),</li>
 * <li>the void between worlds: a lone platform where the Hollow King waits,</li>
 * <li>Nythera, a new continent west of Skullbrine (7x7 regions with the sea around it).</li>
 * </ul>
 */
final class VoidRealmGenerator {

	private final CursedIslandGenerator g;
	private final Random random = new Random(0x401D);

	VoidRealmGenerator(CursedIslandGenerator g) {
		this.g = g;
	}

	/* ---------- floors (flo index + 1) ---------- */
	static final int GRASS_A = 48, GRASS_B = 49, GRASS_C = 50, GRASS_D = 63, GRASS_E = 64, FOREST_FLOOR = 57, SLOPE = 58,
			MOUNTAIN = 56, SAND_FLOOR = 68, STONE_ROAD = 10, DIRT_ROAD = 81, CRYPT_PURPLE = 116, VOID_BLACK = 37;

	/* ---------- scenery ---------- */
	static final int TREE = 1276, TREE_2 = 1278, OAK = 1281, WILLOW = 1308, MAPLE = 1307, YEW = 1309, EVERGREEN = 1315,
			EVERGREEN_BIG = 1316, FLOWERS = 1192, FERN = 1298, BUSH = 1124, BUSH_2 = 1125, BIG_BUSH = 1123, FALLEN_TREE = 1296,
			BOULDER = 5604, STANDING_STONE = 11782, PILLAR = 2440, PILLAR_2 = 2441, RUBBLE = 2508, TENT = 3984,
			FIRE = 2732, LOGS = 5581, COOKING_POT = 2727, CRYSTAL_SRC = 10014, CRYSTAL_DARK_SRC = 9751, COPPER = 2090,
			TIN = 2094, IRON = 2092, COAL = 2096, MITHRIL = 2102, GROUND_FLOWERS = 4735;

	/* ====================================================================== */
	/*                              DEFINITIONS                               */
	/* ====================================================================== */

	void npcs(ConfigDefinitions.Table npcs) throws java.io.IOException {
		// a seer in deep violet robes, drawn to the island by the portal's song
		ConfigDefinitions.Definition seer = g.npc(npcs, 176, SEER_ILSABET).name("Seer Ilsabet")
				.examine("Her eyes are the colour of the portal's light.").npcActions("Talk-to", null, null, null, null)
				.set(60, npcs.decode(611).get(60)); // the Varrock witch's chathead - the cloned witch has none
		tintNpc(seer, 50, 4, 0.9, 6, c -> (c >> 7 & 7) >= 1 && (c >> 10) != 7 && (c >> 10) != 8); // robes, not skin
		g.ghostly(g.npc(npcs, 1612, HOLLOW_WRAITH), npcs, 1612, 50, 4, 0.75, 14).name("Hollow wraith")
				.examine("Something wearing the shape of a drowned man.").shorts(95, 30);
		g.ghostly(g.npc(npcs, 2263, VOIDLING), npcs, 2263, 52, 6, 0.6, 8).name("Voidling")
				.examine("It squirms out of the rock like a thought you'd rather not have.").shorts(95, 9);
		// the Hollow King: a colossus of absence, cut from the same stone as TzTok-Jad but drained of all its fire
		g.ghostly(g.npc(npcs, 2745, HOLLOW_KING), npcs, 2745, 50, 3, 0.42, 2).name("The Hollow King")
				.examine("Looking at it is like looking down a well with no bottom.").shorts(95, 999).shorts(97, 118)
				.shorts(98, 118);
		g.npc(npcs, 14, OREN).name("Oren the Wanderer").examine("A weathered old man in travel-stained robes.")
				.npcActions("Talk-to", null, null, null, null).set(60, npcs.decode(455).get(60)); // Kaqemeex's chathead
	}

	void items(ConfigDefinitions.Table items) throws java.io.IOException {
		splinter(items, EYE_SPLINTER, "Eye splinter", "It weeps cold green light.", 27, 6);
		splinter(items, GRAVE_SPLINTER, "Grave splinter", "Grey as a drowned man's skin. It's whispering.", 42, 2);
		splinter(items, VEIN_SPLINTER, "Vein splinter", "It still pulses, like something alive.", 52, 6);
		ConfigDefinitions.Definition sigil = g.item(items, 1718, VOID_SIGIL).name("Void sigil")
				.examine("Three splinters bound into one. It's so cold it burns.").cost(1);
		tintItem(sigil, 50, 6, 0.8, 10);
		ConfigDefinitions.Definition lantern = g.item(items, 4550, SEERS_LANTERN).name("Seer's lantern")
				.examine("Ilsabet's lantern. Its flame shows places, not things.").itemActions("Gaze", null, null, null, "Drop")
				.cost(2500);
		tintItem(lantern, 50, 5, 1.0, 8);
		ConfigDefinitions.Definition cape = g.item(items, 1052, HOLLOW_CAPE).name("Hollow-touched cape")
				.examine("Woven from the edge of nothing. The Hollow King's mark is on it - and on you.").cost(60000);
		tintItem(cape, 50, 4, 0.55, 0);
		ConfigDefinitions.Definition fang = g.item(items, 4587, HOLLOWFANG).name("Hollowfang scimitar")
				.examine("Forged from a sliver of the platform that shattered under you. It hums when it's hungry.").cost(80000);
		tintItem(fang, 50, 3, 0.6, 6);
	}

	void splinter(ConfigDefinitions.Table items, int id, String name, String examine, int hue, int sat) throws java.io.IOException {
		ConfigDefinitions.Definition d = g.item(items, 1617, id).name(name).examine(examine).cost(1)
				.itemActions(null, null, null, null, "Drop");
		tintItem(d, hue, sat, 0.9, 12);
	}

	void tintItem(ConfigDefinitions.Definition d, int hue, int sat, double light, int add) throws java.io.IOException {
		List<Integer> models = new ArrayList<>();
		models.add(d.itemModel());
		for (int op : new int[] { 23, 25 }) { // male / female worn models
			byte[] p = d.get(op);
			if (p != null) {
				models.add((p[0] & 0xFF) << 8 | p[1] & 0xFF);
			}
		}
		int[][] t = ConfigDefinitions.tint(g.cache, models.stream().mapToInt(i -> i).toArray(), hue, sat, light, add);
		d.recolor(t[0], t[1]);
	}

	void tintNpc(ConfigDefinitions.Definition d, int hue, int sat, double light, int add, java.util.function.IntPredicate filter)
			throws java.io.IOException {
		int[] body = d.npcModels();
		byte[] h = d.get(60);
		int heads = h == null ? 0 : h[0] & 0xFF;
		int[] models = java.util.Arrays.copyOf(body, body.length + heads);
		for (int i = 0; i < heads; i++) {
			models[body.length + i] = (h[1 + i * 2] & 0xFF) << 8 | h[2 + i * 2] & 0xFF;
		}
		int[][] t = ConfigDefinitions.tint(g.cache, models, hue, sat, light, add, filter);
		d.recolor(t[0], t[1]);
	}

	void locs(ConfigDefinitions.Table locs) throws java.io.IOException {
		ConfigDefinitions.Definition vein = g.loc(locs, 2106, VOID_VEIN).name("Void-veined rock")
				.examine("Violet veins crawl through it. They move when you aren't looking.");
		byte[] p = vein.get(40);
		java.nio.ByteBuffer b = java.nio.ByteBuffer.wrap(p, 1, p.length - 1);
		int from = b.getShort() & 0xFFFF;
		vein.recolor(new int[] { from }, new int[] { hsl(51, 7, 40) });
		g.loc(locs, 404, EPITAPH_GRAVE).name("Gravestone").examine("A weathered stone, carved by an unsteady hand.")
				.set(30, ConfigDefinitions.string("Read")).set(31, ConfigDefinitions.string("Dig"));
		ConfigDefinitions.Definition crystal = g.loc(locs, CRYSTAL_SRC, VOID_CRYSTAL).name("Void crystal")
				.examine("It grew here when the void touched the world.");
		g.tintLoc(crystal, 50, 5, 0.9, 10, c -> true);
		ConfigDefinitions.Definition dark = g.loc(locs, CRYSTAL_DARK_SRC, VOID_CRYSTAL_DARK).name("Void crystal")
				.examine("Black glass with a violet heart.");
		g.tintLoc(dark, 52, 6, 0.8, 4, c -> true);
		ConfigDefinitions.Definition eye = g.loc(locs, GHOSTFIRE, EYE_FIRE).name("Ghostfire")
				.examine("It burns in the skull's eye. Something glints beneath the flames.")
				.set(30, ConfigDefinitions.string("Reach-into"));
		g.tintLoc(eye, 23, 6, 1.0, 4, c -> true);
		ConfigDefinitions.Definition rift = g.loc(locs, com.rs2.game.content.skullbrine.SkullbrineConstants.VOID_PORTAL, RIFT)
				.name("Rift").examine("The hole your fall tore in the sky. It hasn't closed.").set(30, ConfigDefinitions.string("Enter"));
		g.tintLoc(rift, 50, 6, 1.0, 12, c -> true);
	}

	/* ====================================================================== */
	/*                         SKULLBRINE ISLE SCENERY                        */
	/* ====================================================================== */

	/** The weeping eye, the epitaph graves and Seer Ilsabet's camp. Run after the island surface is built. */
	void islandScenery() {
		// a ghostfire burns at the lower lip of each eye, where you can reach it
		for (int x : new int[] { LEFT_EYE_X, RIGHT_EYE_X }) {
			g.removeObjectsAt(x, EYE_FIRE_Y);
			g.object(EYE_FIRE, x, EYE_FIRE_Y, TYPE_SCENERY, 0);
		}
		// the east eye weeps: a trail of the cove's green water runs down the face to the jaw
		for (int y = FACE_Y0 + 3; y < EYE_FIRE_Y; y++) {
			int x = RIGHT_EYE_X + ((y / 2) % 2 == 0 ? 0 : 1) - (y < FACE_Y0 + 6 ? 1 : 0);
			if (g.overlayAt(x, y) == 0) {
				g.setOverlay(x, y, GUNGY_WATER);
			}
		}
		// the graves with epitaphs replace whatever stood there
		for (int[] grave : GRAVES) {
			g.removeObjectsAt(grave[0], grave[1]);
			g.object(EPITAPH_GRAVE, grave[0], grave[1], TYPE_SCENERY, 1);
		}
		// Ilsabet's camp east of the jaw plaza: a tent, a fire and her reading stone
		g.removeObjectsAt(ILSABET_X + 2, ILSABET_Y + 1);
		g.object(TENT, ILSABET_X + 2, ILSABET_Y + 1, TYPE_SCENERY, 3);
		g.object(VOID_CRYSTAL, ILSABET_X + 1, ILSABET_Y - 3, TYPE_SCENERY, 0);
	}

	/** The void vein in the Dragonvein cavern (dungeon-region local coordinates). */
	void voidVein(MapRegion r, boolean[][] open) {
		for (int x = 2; x <= 10; x++) {
			for (int y = 44; y <= 56; y++) {
				final int fx = x, fy = y;
				if (open[x][y] && CursedIslandGenerator.bordersClosed(open, x, y)
						&& CursedIslandGenerator.openAround(open, x, y, 1) >= 4
						&& r.objects.stream().noneMatch(o -> Math.abs(o.x - fx) <= 1 && Math.abs(o.y - fy) <= 1)) {
					r.objects.add(new MapRegion.MapObject(VOID_VEIN, x, y, 0, TYPE_SCENERY, 0));
					r.objects.add(new MapRegion.MapObject(VOID_CRYSTAL_DARK, Math.max(1, x - 1), y + 2, 0, TYPE_SCENERY, 0));
					System.out.println("Void vein at " + (x + DBASE_X) + "," + (y + DBASE_Y));
					return;
				}
			}
		}
		throw new IllegalStateException("no place for the void vein");
	}

	/* ====================================================================== */
	/*                           THE VOID BETWEEN                             */
	/* ====================================================================== */

	/** A ragged platform of black stone hanging in nothing, ringed with violet crystals. */
	void arena() {
		MapRegion r = new MapRegion(ARENA_REGION_X, ARENA_REGION_Y);
		g.regions.put(r.id(), r);
		int bx = ARENA_REGION_X * 64, by = ARENA_REGION_Y * 64;
		int cx = ARENA_X - bx, cy = ARENA_Y - by;
		boolean[][] land = new boolean[64][64];
		for (int x = 0; x < 64; x++) {
			for (int y = 0; y < 64; y++) {
				double a = Math.atan2(y - cy, x - cx);
				double rad = 9.5 + 1.2 * Math.sin(3 * a + 0.4) + 0.8 * Math.sin(7 * a + 2);
				land[x][y] = Math.hypot(x - cx, (y - cy) * 1.05) < rad;
			}
		}
		// floating shards of rock around the platform
		int[][] islets = { { -14, 4, 2 }, { 13, -6, 2 }, { 15, 8, 1 }, { -12, -9, 1 }, { 3, 15, 2 }, { -6, -14, 1 } };
		for (int[] i : islets) {
			for (int x = cx + i[0] - 2; x <= cx + i[0] + 2; x++) {
				for (int y = cy + i[1] - 2; y <= cy + i[1] + 2; y++) {
					if (Math.hypot(x - cx - i[0], y - cy - i[1]) <= i[2] + 0.3) {
						land[x][y] = true;
					}
				}
			}
		}
		for (int x = 0; x < 64; x++) {
			for (int y = 0; y < 64; y++) {
				if (land[x][y]) {
					boolean edge = !land[x - 1][y] || !land[x + 1][y] || !land[x][y - 1] || !land[x][y + 1];
					r.underlay[0][x][y] = DARK_ROCK;
					r.height[0][x][y] = 24 + (int) Math.round(noise(x * 7, y * 7) * 2);
					double d = Math.hypot(x - cx, y - cy);
					if (d > 4.6 && d < 5.8) {
						r.overlay[0][x][y] = CRYPT_PURPLE; // a ring of violet light around the centre
					} else if (noise(x * 5 + 3, y * 5) > 0.55) {
						r.overlay[0][x][y] = SHADOW_ROCK;
					}
					if (Math.hypot(x - cx, y - cy) > 11) {
						r.settings[0][x][y] = MapRegion.BLOCKED; // the islets are out of reach
					}
					if (edge) {
						r.height[0][x][y] -= 6;
					}
				} else {
					// the void: nothing at all, far below
					r.settings[0][x][y] = MapRegion.BLOCKED;
					r.underlay[0][x][y] = DARK_ROCK;
					r.overlay[0][x][y] = INVISIBLE;
					r.height[0][x][y] = 160;
				}
			}
		}
		// crystals around the rim, two great ones flanking the king
		int[][] crystals = { { -8, 2 }, { 8, 2 }, { -6, -6 }, { 6, -6 }, { -3, 8 }, { 4, 8 }, { -9, -3 } }; // keep the arrival tile (0,-7) clear
		for (int i = 0; i < crystals.length; i++) {
			r.objects.add(new MapRegion.MapObject(i % 2 == 0 ? VOID_CRYSTAL : VOID_CRYSTAL_DARK, cx + crystals[i][0] - 1,
					cy + crystals[i][1] - 1, 0, TYPE_SCENERY, i & 3));
		}
		for (int[] i : islets) {
			r.objects.add(new MapRegion.MapObject(VOID_CRYSTAL_DARK, cx + i[0] - 1, cy + i[1] - 1, 0, TYPE_SCENERY, 0));
		}
	}

	/* ====================================================================== */
	/*                                NYTHERA                                 */
	/* ====================================================================== */

	static final int NX0 = NYTHERA_REGION_X0 * 64, NY0 = NYTHERA_REGION_Y0 * 64, NSIZE = NYTHERA_REGIONS * 64;

	/** Value noise in [-1, 1] on a lattice of the given scale, smoothly interpolated. */
	static double vnoise(double x, double y, int seed) {
		int x0 = (int) Math.floor(x), y0 = (int) Math.floor(y);
		double fx = x - x0, fy = y - y0;
		fx = fx * fx * (3 - 2 * fx);
		fy = fy * fy * (3 - 2 * fy);
		double a = hash(x0, y0, seed), b = hash(x0 + 1, y0, seed), c = hash(x0, y0 + 1, seed), d = hash(x0 + 1, y0 + 1, seed);
		return (a + (b - a) * fx) * (1 - fy) + (c + (d - c) * fx) * fy;
	}

	static double hash(int x, int y, int seed) {
		long h = x * 374761393L + y * 668265263L + seed * 2147483647L;
		h = (h ^ (h >> 13)) * 1274126177L;
		h ^= h >> 16;
		return ((h & 0xFFFF) / 32767.5) - 1;
	}

	static double fbm(double x, double y, int seed) {
		return vnoise(x, y, seed) * 0.55 + vnoise(x * 2.1, y * 2.1, seed + 1) * 0.28 + vnoise(x * 4.3, y * 4.3, seed + 2) * 0.17;
	}

	/** Signed distance inland from Nythera's coast (negative = sea). */
	static double coast(int x, int y) {
		double dx = x - NYTHERA_CX, dy = y - NYTHERA_CY;
		double a = Math.atan2(dy, dx);
		double radius = 132 + 14 * Math.sin(2 * a + 0.7) + 9 * Math.sin(3 * a + 2.1) + 6 * Math.sin(5 * a + 0.3)
				+ 3 * Math.sin(11 * a) + 16 * fbm(x / 38.0, y / 38.0, 7);
		// a deep bay on the south-west coast
		double bay = 26 - Math.hypot(x - 1400, y - 2600);
		return Math.min(radius - Math.hypot(dx, dy * 1.08), -bay);
	}

	/** The northern mountains: 0 outside, up to 1 on the ridge line. */
	static double mountain(int x, int y) {
		double ridgeY = 2792 + 12 * Math.sin((x - NYTHERA_CX) / 31.0) + 6 * fbm(x / 20.0, 3, 11);
		double across = Math.abs(y - ridgeY) / (20 + 8 * fbm(x / 25.0, 9, 12));
		double along = smooth(1 - Math.abs(x - NYTHERA_CX + 10) / 105.0);
		return Math.max(0, 1 - across) * along;
	}

	/** The lake in the west-centre. */
	static double lake(int x, int y) {
		return 15 + 5 * fbm(x / 9.0, y / 9.0, 21) - Math.hypot(x - 1440, (y - 2716) * 1.3);
	}

	/** The river: from a spring in the mountains down to the lake, then out of it to the south-west bay. */
	static final int[][] RIVER = { { 1476, 2796 }, { 1470, 2784 }, { 1474, 2772 }, { 1464, 2762 }, { 1458, 2748 }, { 1448, 2736 },
			{ 1440, 2718 }, { 1432, 2700 }, { 1420, 2692 }, { 1424, 2676 }, { 1412, 2664 }, { 1414, 2648 }, { 1402, 2636 },
			{ 1398, 2618 } };

	/** Distance from each tile to the river's edge (negative = in the water), filled by {@link #buildRiver()}. */
	private final float[][] riverField = new float[NSIZE][NSIZE];

	double riverDistance(int x, int y) {
		int lx = x - NX0, ly = y - NY0;
		return lx < 0 || ly < 0 || lx >= NSIZE || ly >= NSIZE ? 99 : riverField[lx][ly];
	}

	/**
	 * Points along a smooth curve (Catmull-Rom) through the waypoints, about one per tile, nudged sideways by a gentle
	 * wave so the line meanders between them instead of running straight.
	 */
	static List<double[]> meander(int[][] pts, double amplitude, double wavelength, int seed) {
		List<double[]> out = new ArrayList<>();
		double travelled = 0;
		for (int i = 0; i + 1 < pts.length; i++) {
			int[] p0 = pts[Math.max(0, i - 1)], p1 = pts[i], p2 = pts[i + 1], p3 = pts[Math.min(pts.length - 1, i + 2)];
			int steps = (int) Math.ceil(Math.hypot(p2[0] - p1[0], p2[1] - p1[1]) * 2);
			for (int k = 0; k < steps; k++) {
				double t = k / (double) steps, t2 = t * t, t3 = t2 * t;
				double x = 0.5 * (2 * p1[0] + (-p0[0] + p2[0]) * t + (2 * p0[0] - 5 * p1[0] + 4 * p2[0] - p3[0]) * t2
						+ (-p0[0] + 3 * p1[0] - 3 * p2[0] + p3[0]) * t3);
				double y = 0.5 * (2 * p1[1] + (-p0[1] + p2[1]) * t + (2 * p0[1] - 5 * p1[1] + 4 * p2[1] - p3[1]) * t2
						+ (-p0[1] + 3 * p1[1] - 3 * p2[1] + p3[1]) * t3);
				double dx = p2[0] - p1[0], dy = p2[1] - p1[1], len = Math.hypot(dx, dy);
				travelled += 0.5;
				// the wave fades out at the ends so the line still starts and finishes where it should
				double fade = Math.min(1, Math.min(i + t, pts.length - 1 - i - t));
				double off = amplitude * fade * (Math.sin(travelled / wavelength * Math.PI * 2) * 0.7
						+ vnoise(travelled / wavelength, 0.5, seed) * 0.6);
				out.add(new double[] { x - dy / len * off, y + dx / len * off, travelled });
			}
		}
		return out;
	}

	void buildRiver() {
		for (float[] row : riverField) {
			java.util.Arrays.fill(row, 99f);
		}
		for (double[] pt : meander(RIVER, 3.2, 16, 33)) {
			double half = 1.1 + 0.9 * (0.5 + 0.5 * vnoise(pt[2] / 11, 0, 34)) + pt[2] / 260.0; // widens downstream
			for (int x = (int) pt[0] - 8; x <= pt[0] + 8; x++) {
				for (int y = (int) pt[1] - 8; y <= pt[1] + 8; y++) {
					int lx = x - NX0, ly = y - NY0;
					if (lx >= 0 && ly >= 0 && lx < NSIZE && ly < NSIZE) {
						float d = (float) (Math.hypot(x + 0.5 - pt[0], y + 0.5 - pt[1]) - half);
						riverField[lx][ly] = Math.min(riverField[lx][ly], d);
					}
				}
			}
		}
	}

	static double segmentDistance(double px, double py, int[] a, int[] b) {
		double vx = b[0] - a[0], vy = b[1] - a[1];
		double t = Math.max(0, Math.min(1, ((px - a[0]) * vx + (py - a[1]) * vy) / (vx * vx + vy * vy)));
		return Math.hypot(px - a[0] - t * vx, py - a[1] - t * vy);
	}

	/** The Hollow Scar in the east, where the Hollow King's shadow has withered the land: 0..1. */
	static double scar(int x, int y) {
		return smooth(1 - Math.hypot((x - 1600) / 34.0, (y - 2738) / 28.0) + 0.25 * fbm(x / 12.0, y / 12.0, 41));
	}

	/** Starfall Crater: 0..1 towards its centre. */
	static double crater(int x, int y) {
		return smooth(1 - Math.hypot(x - CRATER_X, y - CRATER_Y) / 9.0);
	}

	/** Forest density 0..1: thick in the west and the foothills, open meadows in the middle and south. */
	static double forest(int x, int y) {
		double west = smooth((1480 - x) / 60.0);
		double foothills = smooth(1 - Math.abs(y - 2768) / 18.0) * 0.8;
		return Math.max(0, Math.max(west, foothills) * 0.9 + 0.45 * fbm(x / 18.0, y / 18.0, 51) - 0.1);
	}

	void nythera() {
		for (int rx = 0; rx < NYTHERA_REGIONS; rx++) {
			for (int ry = 0; ry < NYTHERA_REGIONS; ry++) {
				MapRegion r = new MapRegion(NYTHERA_REGION_X0 + rx, NYTHERA_REGION_Y0 + ry);
				g.regions.put(r.id(), r);
			}
		}
		buildRiver();
		for (int x = NX0; x < NX0 + NSIZE; x++) {
			for (int y = NY0; y < NY0 + NSIZE; y++) {
				terrain(x, y);
			}
		}
		slopes();
		// the old stone road from the crater to the Broken Gate, then dirt tracks north to the pass and east to the scar
		road(2.6, STONE_ROAD, new int[][] { { 1564, 2662 }, { 1558, 2670 }, { 1546, 2676 }, { 1538, 2688 }, { 1526, 2694 },
				{ 1518, 2704 }, { 1510, 2710 } }, 1.6, 14, 91);
		road(2.2, DIRT_ROAD, new int[][] { { 1510, 2714 }, { 1506, 2726 }, { 1496, 2732 }, { 1492, 2746 }, { 1484, 2754 },
				{ 1482, 2768 }, { 1480, 2782 }, { 1482, 2800 } }, 2.0, 12, 92);
		road(2.0, DIRT_ROAD, new int[][] { { 1514, 2712 }, { 1526, 2718 }, { 1538, 2728 }, { 1552, 2730 }, { 1566, 2738 },
				{ 1578, 2736 } }, 1.8, 13, 93);
		craterAndCamp();
		ruins();
		scarScenery();
		ores();
		vegetation();
		smoothOverlays();
	}

	void terrain(int x, int y) {
		double inland = coast(x, y);
		if (inland < 0) {
			g.sea(x, y);
			return;
		}
		double m = mountain(x, y);
		double rolling = 5 * fbm(x / 26.0, y / 26.0, 61) + 2 * fbm(x / 8.0, y / 8.0, 62);
		double h = 3 + Math.min(inland, 10) * 0.9 + Math.max(0, rolling + 2) * smooth(inland / 8)
				+ 70 * Math.pow(m, 1.6) * (0.8 + 0.4 * fbm(x / 7.0, y / 7.0, 63));
		double lk = lake(x, y), river = riverDistance(x, y);
		boolean water = lk > 0 || river < 0 && m < 0.6;
		if (water) {
			h = Math.min(h, 2);
		} else if (lk > -4 || river < 3) {
			h = Math.min(h, 3 + Math.max(0, Math.min(lk < -4 ? 99 : -lk, river)) * 1.6); // banks slope down to the water
		}
		h -= 9 * crater(x, y); // the crater's bowl
		g.setHeight(x, y, (int) Math.round(Math.max(1, h)));
		if (water) {
			g.setOverlay(x, y, WATER);
			g.block(x, y);
			g.setUnderlay(x, y, SAND_FLOOR);
			return;
		}
		int floor;
		double n = fbm(x / 6.0, y / 6.0, 71);
		double sc = scar(x, y);
		if (inland < 3.5) {
			floor = SAND_FLOOR;
		} else if (m > 0.55) {
			floor = n > 0.2 ? MOUNTAIN : SLOPE;
		} else if (m > 0.3) {
			floor = n > 0.1 ? SLOPE : GRASS_D;
		} else if (sc > 0.15) {
			floor = sc > 0.6 ? (n > 0.2 ? ASH : BURGH_SOIL) : n > 0 ? DEAD_GRASS : MUDDY_GRASS;
		} else if (forest(x, y) > 0.55) {
			floor = n > 0.25 ? FOREST_FLOOR : n > -0.2 ? GRASS_D : GRASS_E;
		} else {
			floor = n > 0.35 ? GRASS_B : n > 0 ? GRASS_A : n > -0.35 ? GRASS_C : GRASS_E;
		}
		g.setUnderlay(x, y, floor);
		if (sc > 0.7 && fbm(x / 4.0, y / 4.0, 72) > 0.3) {
			g.setOverlay(x, y, VOID_BLACK); // patches where the ground has simply gone
		}
	}

	/** Rock faces too steep to climb are blocked, so the mountains are walls with passes through them. */
	void slopes() {
		for (int x = NX0 + 1; x < NX0 + NSIZE - 1; x++) {
			for (int y = NY0 + 1; y < NY0 + NSIZE - 1; y++) {
				if (mountain(x, y) < 0.2 || coast(x, y) < 0) {
					continue;
				}
				int h = g.heightAt(x, y), steep = 0;
				for (int[] d : new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } }) {
					steep = Math.max(steep, Math.abs(h - g.heightAt(x + d[0], y + d[1])));
				}
				if (steep > 9) {
					g.block(x, y);
					g.setUnderlay(x, y, MOUNTAIN);
				}
			}
		}
		// a pass over the ridge where the north road goes
		for (int x = 1474; x <= 1486; x++) {
			for (int y = 2760; y <= 2820; y++) {
				MapRegion r = g.regionAt(x, y);
				r.settings[0][x & 63][y & 63] &= ~MapRegion.BLOCKED;
			}
		}
	}

	/** A winding road: every tile within width/2 of the meandering line gets the floor (no gaps, no diagonal-only joins). */
	void road(double width, int floor, int[][] points, double amplitude, double wavelength, int seed) {
		for (double[] pt : meander(points, amplitude, wavelength, seed)) {
			for (int x = (int) pt[0] - 3; x <= pt[0] + 3; x++) {
				for (int y = (int) pt[1] - 3; y <= pt[1] + 3; y++) {
					if (Math.hypot(x + 0.5 - pt[0], y + 0.5 - pt[1]) <= width / 2 && g.overlayAt(x, y) != WATER) {
						g.setOverlay(x, y, floor);
						g.removeObjectsAt(x, y);
					}
				}
			}
		}
	}

	void craterAndCamp() {
		// scorched, glassy ground where you fell, crystals thrown up around the rim
		for (int x = CRATER_X - 12; x <= CRATER_X + 12; x++) {
			for (int y = CRATER_Y - 12; y <= CRATER_Y + 12; y++) {
				double d = Math.hypot(x - CRATER_X, y - CRATER_Y);
				if (d < 7.5) {
					g.setUnderlay(x, y, d < 4 ? ASH : BURGH_SOIL);
					if (d < 3.4) {
						g.setOverlay(x, y, CRYPT_PURPLE); // glassy violet where the rift burned the ground
					}
				} else if (d < 11 && fbm(x / 3.0, y / 3.0, 81) > 0) {
					g.setUnderlay(x, y, DEAD_GRASS);
				}
			}
		}
		g.object(RIFT, RIFT_X - 1, RIFT_Y, TYPE_SCENERY, 0);
		int[][] crystals = { { -7, 2 }, { 6, 4 }, { 7, -4 }, { -5, -6 }, { 1, 8 }, { -9, -1 } };
		for (int i = 0; i < crystals.length; i++) {
			g.object(i % 2 == 0 ? VOID_CRYSTAL : VOID_CRYSTAL_DARK, CRATER_X + crystals[i][0] - 1, CRATER_Y + crystals[i][1] - 1,
					TYPE_SCENERY, i & 3);
		}
		g.object(FIRE, CRATER_X + 3, CRATER_Y - 3, TYPE_SCENERY, 0);
		g.object(FIRE, CRATER_X - 4, CRATER_Y + 2, TYPE_SCENERY, 0);
		// Oren's camp on the rim
		g.object(TENT, OREN_X - 3, OREN_Y + 2, TYPE_SCENERY, 2);
		g.object(FIRE, OREN_X + 1, OREN_Y + 1, TYPE_SCENERY, 0);
		g.object(LOGS, OREN_X + 3, OREN_Y + 1, TYPE_SCENERY, 1);
		g.object(LOGS, OREN_X + 1, OREN_Y + 3, TYPE_SCENERY, 0);
		g.object(COOKING_POT, OREN_X + 1, OREN_Y - 1, TYPE_SCENERY, 0);
	}

	/** The Broken Gate where the old road ends, and a circle of standing stones on the hill above it. */
	void ruins() {
		int gx = 1510, gy = 2712;
		g.object(PILLAR, gx - 3, gy + 1, TYPE_SCENERY, 0);
		g.object(PILLAR_2, gx + 3, gy + 1, TYPE_SCENERY, 0);
		g.object(RUBBLE, gx - 2, gy + 3, TYPE_SCENERY, 1);
		g.object(RUBBLE, gx + 2, gy - 2, TYPE_SCENERY, 2);
		g.object(PILLAR, gx - 3, gy - 3, TYPE_SCENERY, 0);
		for (int i = 0; i < 7; i++) {
			double a = i * Math.PI * 2 / 7;
			int x = 1502 + (int) Math.round(Math.cos(a) * 7), y = 2738 + (int) Math.round(Math.sin(a) * 7);
			g.removeObjectsAt(x, y);
			g.object(STANDING_STONE, x, y, TYPE_SCENERY, i & 3);
		}
	}

	void scarScenery() {
		for (int x = 1556; x < 1650; x++) {
			for (int y = 2700; y < 2780; y++) {
				double s = scar(x, y);
				if (s < 0.3 || coast(x, y) < 4 || !free(x, y, 2)) {
					continue;
				}
				int roll = random.nextInt(1000);
				if (roll < 10 * s) {
					g.object(random.nextBoolean() ? VOID_CRYSTAL : VOID_CRYSTAL_DARK, x, y, TYPE_SCENERY, random.nextInt(4));
				} else if (roll < 45 * s) {
					g.object(random.nextBoolean() ? DEAD_TREE : BIG_DEAD_TREE, x, y, TYPE_SCENERY, random.nextInt(4));
				}
			}
		}
	}

	/** Clusters of ore rocks in the foothills and on the beaches' rocky points. */
	void ores() {
		int[][] clusters = { { 1452, 2768, COPPER, TIN }, { 1532, 2772, IRON, COAL }, { 1500, 2778, COAL, MITHRIL },
				{ 1420, 2760, IRON, TIN }, { 1556, 2700, COPPER, IRON } };
		for (int[] c : clusters) {
			int placed = 0;
			for (int attempt = 0; attempt < 60 && placed < 7; attempt++) {
				int x = c[0] + random.nextInt(9) - 4, y = c[1] + random.nextInt(9) - 4;
				if (free(x, y, 0) && !blocked(x, y) && coast(x, y) > 4) {
					g.object(placed % 2 == 0 ? c[2] : c[3], x, y, TYPE_SCENERY, random.nextInt(4));
					placed++;
				}
			}
		}
	}

	void vegetation() {
		for (int pass = 0; pass < 2; pass++) {
			for (int x = NX0 + 2; x < NX0 + NSIZE - 2; x++) {
				for (int y = NY0 + 2; y < NY0 + NSIZE - 2; y++) {
					double inland = coast(x, y);
					if (inland < 1 || blocked(x, y) || !free(x, y, 0) || crater(x, y) > 0 || scar(x, y) > 0.3
							|| Math.hypot(x - OREN_X, y - OREN_Y) < 6) {
						continue;
					}
					if (pass == 0) {
						bigPlant(x, y, inland);
					} else {
						groundCover(x, y, inland);
					}
				}
			}
		}
	}

	void bigPlant(int x, int y, double inland) {
		int roll = random.nextInt(1000), rot = random.nextInt(4);
		double m = mountain(x, y), f = forest(x, y);
		boolean nearWater = lake(x, y) > -5 || riverDistance(x, y) < 4;
		if (inland < 4) {
			if (roll < 8 && free(x, y, 2)) {
				g.object(BOULDER, x, y, TYPE_SCENERY, rot);
			}
			return;
		}
		if (m > 0.35) {
			if (roll < 25 && free(x, y, 2)) {
				g.object(random.nextBoolean() ? EVERGREEN : EVERGREEN_BIG, x, y, TYPE_SCENERY, rot);
			} else if (roll < 45 && free(x, y, 1)) {
				g.object(BOULDER, x, y, TYPE_SCENERY, rot);
			}
			return;
		}
		int treeChance = (int) (6 + 150 * f);
		if (roll < treeChance && free(x, y, 2)) {
			int t;
			if (nearWater && random.nextInt(3) == 0) {
				t = WILLOW;
			} else {
				int k = random.nextInt(100);
				t = k < 45 ? (k % 2 == 0 ? TREE : TREE_2) : k < 75 ? OAK : k < 88 ? (y > 2750 ? MAPLE : EVERGREEN) : k < 95 ? MAPLE : YEW;
			}
			g.object(t, x, y, TYPE_SCENERY, rot);
		} else if (roll < treeChance + 10 && free(x, y, 2)) {
			g.object(random.nextInt(3) == 0 ? BIG_BUSH : random.nextBoolean() ? BUSH : BUSH_2, x, y, TYPE_SCENERY, rot);
		} else if (roll < treeChance + 12 && free(x, y, 3) && f > 0.4) {
			g.object(FALLEN_TREE, x, y, TYPE_SCENERY, rot);
		} else if (roll < treeChance + 15 && free(x, y, 1)) {
			g.object(BOULDER, x, y, TYPE_SCENERY, rot);
		}
	}

	void groundCover(int x, int y, double inland) {
		if (inland < 4 || mountain(x, y) > 0.35) {
			return;
		}
		int roll = random.nextInt(1000), rot = random.nextInt(4);
		double f = forest(x, y);
		if (f < 0.5 && roll < 40) {
			g.object(GROUND_FLOWERS + random.nextInt(4), x, y, TYPE_GROUND_DECOR, rot);
		} else if (roll < 60 && f >= 0.5) {
			g.object(FERN + random.nextInt(3), x, y, TYPE_SCENERY, rot);
		} else if (roll < 75) {
			g.object(FLOWERS + random.nextInt(2), x, y, TYPE_SCENERY, rot);
		} else if (roll < 110) {
			g.object(LONG_GRASS, x, y, TYPE_GROUND_DECOR, rot);
		}
	}

	boolean blocked(int x, int y) {
		return (g.regionAt(x, y).settings[0][x & 63][y & 63] & MapRegion.BLOCKED) != 0;
	}

	/** Nothing placed within r tiles, no overlay (road/water), and inside Nythera's regions. */
	boolean free(int x, int y, int r) {
		for (int dx = -r; dx <= r; dx++) {
			for (int dy = -r; dy <= r; dy++) {
				int ax = x + dx, ay = y + dy;
				if (ax < NX0 || ay < NY0 || ax >= NX0 + NSIZE || ay >= NY0 + NSIZE) {
					return false;
				}
				MapRegion reg = g.regionAt(ax, ay);
				int lx = ax & 63, ly = ay & 63;
				if (reg.overlay[0][lx][ly] != 0) {
					return false;
				}
				for (MapRegion.MapObject o : reg.objects) {
					if (o.x == lx && o.y == ly) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/** Cuts the corners of water, roads and patches diagonally (see CursedIslandGenerator#smoothOverlays). */
	void smoothOverlays() {
		List<int[]> changes = new ArrayList<>();
		for (int x = NX0 + 1; x < NX0 + NSIZE - 1; x++) {
			for (int y = NY0 + 1; y < NY0 + NSIZE - 1; y++) {
				int ov = g.overlayAt(x, y);
				if (ov == 0) {
					continue;
				}
				boolean n = g.overlayAt(x, y + 1) == ov, e = g.overlayAt(x + 1, y) == ov, so = g.overlayAt(x, y - 1) == ov,
						w = g.overlayAt(x - 1, y) == ov;
				int count = (n ? 1 : 0) + (e ? 1 : 0) + (so ? 1 : 0) + (w ? 1 : 0);
				if (count != 2 || n && so || e && w) {
					continue;
				}
				changes.add(new int[] { x, y, so && w ? 0 : w && n ? 1 : n && e ? 2 : 3 });
			}
		}
		for (int[] c : changes) {
			MapRegion r = g.regionAt(c[0], c[1]);
			r.overlayShape[0][c[0] & 63][c[1] & 63] = 1;
			r.overlayRotation[0][c[0] & 63][c[1] & 63] = c[2];
		}
	}
}
