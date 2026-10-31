package com.rs2.tools.mapgen;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The gate's own test: plants one of each amateur mistake the gate exists to catch into an open meadow in Nythera,
 * then checks every one of them was reported. A gate nobody tests quietly stops working - a threshold tuned for one
 * false positive, a check that throws early - and then it passes everything. Run it after changing WorldLint,
 * DesignLint or WorldGate:
 *
 * <pre>
 *   GENOPTS=-Dworldgate.selftest=true tools/regen-world.sh
 * </pre>
 *
 * It never writes the cache or the stamp: the planted world is thrown away whatever the result.
 */
final class GateSelfTest {

	/** An open, empty meadow east of the crater, well away from the roads and every named place. */
	static final int X = 1601, Y = 2683;

	private GateSelfTest() {
	}

	static boolean enabled() {
		return Boolean.getBoolean("worldgate.selftest");
	}

	/** Plants the mistakes; returns what each should be reported as, keyed by a tile it should be reported near. */
	static Map<String, String> plant(CursedIslandGenerator g) {
		Map<String, String> expect = new LinkedHashMap<>();
		int t = CursedIslandGenerator.TYPE_SCENERY;
		// clear the ground first, so nothing already there muddies the test
		for (int x = X; x < X + 45; x++) {
			for (int y = Y; y < Y + 30; y++) {
				g.removeObjectsAt(x, y);
			}
		}
		// a ruled row of oaks, every 3 tiles
		for (int i = 0; i < 5; i++) {
			g.object(VoidRealmGenerator.OAK, X + 2 + 3 * i, Y + 1, t, i & 3);
		}
		expect.put("GRID-PATTERN", "a ruled row of oaks");
		// nine of a prop scattered about, every one turned the same way
		int[][] fangs = { { 22, 3 }, { 26, 6 }, { 30, 2 }, { 24, 10 }, { 33, 8 }, { 28, 12 }, { 36, 4 }, { 21, 14 }, { 35, 13 } };
		for (int[] f : fangs) {
			g.object(CursedIslandGenerator.BONE_FANG, X + f[0], Y + f[1], t, 0);
		}
		expect.put("UNIFORM-ROTATION", "nine bone fangs all facing one way");
		// the same little camp pasted twice
		for (int cx : new int[] { X + 22, X + 34 }) {
			int cy = Y + 22;
			g.object(VoidRealmGenerator.BARREL, cx, cy, t, 1);
			g.object(VoidRealmGenerator.CRATE, cx + 2, cy, t, 0);
			g.object(VoidRealmGenerator.CRATES, cx, cy + 2, t, 2);
			g.object(CursedIslandGenerator.SKULL_PILE, cx + 3, cy + 3, t, 1);
		}
		expect.put("COPY-PASTE", "the same camp pasted twice");
		// a step the height of a house that the player can walk straight up, and a single-tile spike
		for (int x = X + 30; x < X + 38; x++) {
			for (int y = Y + 16; y < Y + 20; y++) {
				g.setHeight(x, y, g.heightAt(x, Y + 15) + (y < Y + 18 ? 0 : 30)); // (heights are stored positive; below 0 means "natural")
			}
		}
		expect.put("CLIFF-WALKABLE", "a walkable cliff");
		g.setHeight(X + 24, Y + 18, g.heightAt(X + 24, Y + 18) + 12);
		expect.put("HEIGHT-SPIKE", "a one-tile spike");
		// a rectangle of a different ground cut into the grass
		for (int x = X + 1; x < X + 17; x++) {
			for (int y = Y + 6; y < Y + 20; y++) {
				g.setUnderlay(x, y, VoidRealmGenerator.SAND_FLOOR);
			}
		}
		expect.put("STRAIGHT-FLOOR-EDGE", "a rectangular patch of sand");
		// a rabbit spawned inside a boulder
		g.object(CursedIslandGenerator.MOSSY_ROCK, X + 40, Y + 22, t, 0);
		g.voidRealm.spawns.add(new int[] { 1192, X + 40, Y + 22, 1 });
		expect.put("NPC-STUCK", "a rabbit inside a boulder");
		// a stretch of road that goes nowhere from nowhere
		g.voidRealm.road(2.2, VoidRealmGenerator.DIRT_ROAD, new int[][] { { X + 3, Y + 26 }, { X + 11, Y + 27 }, { X + 18, Y + 26 } }, 1.0, 7, 4242);
		expect.put("PATH-ISLAND", "a road that joins nothing");
		// a prop offering an option nothing answers
		ConfigDefinitions.Table locs = ConfigDefinitions.load(g.config, "loc", false);
		locs.put(CursedIslandGenerator.BONE_FANG, locs.decode(CursedIslandGenerator.BONE_FANG).set(30, ConfigDefinitions.string("Search")));
		ConfigDefinitions.save(g.config, locs);
		expect.put("DEAD-OPTION", "a bone fang with a 'Search' that does nothing");
		return expect;
	}

	/** Prints which planted mistakes the gate caught; true only if it caught every one. */
	static boolean judge(Map<String, String> expect, String report) {
		boolean all = true;
		System.out.println();
		System.out.println("GATE SELF-TEST: planted " + expect.size() + " amateur mistakes in Nythera at " + X + "," + Y);
		for (Map.Entry<String, String> e : expect.entrySet()) {
			boolean caught = report.contains(e.getKey() + " ");
			all &= caught;
			System.out.printf("   %-8s %-20s %s%n", caught ? "caught" : "MISSED", e.getKey(), e.getValue());
		}
		System.out.println(all ? "GATE SELF-TEST: PASSED - the gate catches every planted mistake (nothing written)"
				: "GATE SELF-TEST: FAILED - the gate let a planted mistake through: fix the check before trusting the gate");
		return all;
	}
}
