package com.rs2.tools.mapgen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.apollo.util.CompressionUtil;

/**
 * Checks a stretch of the built world for the slap-dash mistakes that are easy to make when a map is written as code
 * rather than drawn: roads that stop in the middle of a field, roads and rivers ruled dead straight, stray bits of wall,
 * doors with no wall round them, cave mouths in flat ground, scenery dumped in the middle of a road or out in the water,
 * tents perched on a slope, tiles stacked so full the client stops drawing them, and places you can see but never reach.
 *
 * It reads the finished cache (so it judges what the player will actually get, whichever generator wrote it), and
 * prints one line per finding, worst kinds first; {@code -png} also writes the map with every finding ringed.
 *
 * <pre>
 * Usage (from "2006Scape Server", after a regen):
 *   java -cp target/server-1.0-jar-with-dependencies.jar com.rs2.tools.mapgen.WorldLint x0 y0 x1 y1 [options]
 *     -from x,y      also flood-fill the walkable ground from here and report what can't be reached
 *     -png file      write the area with the findings ringed (6 px a tile)
 *     -only KIND,..  just these kinds
 *     -max n         at most n lines per kind (default 40)
 *     -accept file   findings already judged right (default tools/worldlint-accepted.txt): one per line,
 *                    "KIND x,y  why", matched within 2 tiles; '#' starts a comment
 * </pre>
 *
 * Every finding is a question, not a verdict: a path may end at a fishing spot on purpose, and a ruin is meant to have
 * loose walls. Answer each one - fix it, or know why it's right - before calling an area finished.
 */
public final class WorldLint {

	/** Overlay floors that are roads and paths (flo index + 1): road, road_bridge, mud paths, the grey road, fai paths. */
	static final Set<Integer> PATH_FLOORS = Set.of(10, 34, 36, 44, 81, 132, 141, 171, 172, 173);
	/** Overlay floors that are water (any flo whose name says water) and the void's pool. */
	static final Set<Integer> WATER_FLOORS = Set.of(6, 7, 39, 41, 104, 109, 128, 130, 151, 156, 158, 175);
	/** Structural floors a road may lead onto: wooden decks and jetties, bridges, brick, stone and marble floors. */
	static final Set<Integer> STRUCTURE_FLOORS = Set.of(5, 12, 20, 27, 28, 32, 35, 45, 46, 47);

	/**
	 * A straight edge at least this long (tiles) is reported: no worn road runs this true, and no shore or river this.
	 * Shores get more rope: the overlay smoothing cuts every tile staircase into a clean diagonal, so any gently curving
	 * shore shows short exact runs at 1:1 and 2:1, and a run of 16 or so on a long coast still reads as natural.
	 */
	static final int STRAIGHT_PATH = 9, STRAIGHT_WATER = 17;

	final int x0, y0, x1, y1;
	final Map<Integer, MapRegion> regions = new HashMap<>();
	final ConfigDefinitions.Table locs;
	final List<Obj> objects = new ArrayList<>();
	final Map<String, List<String>> findings = new TreeMap<>();
	final List<int[]> marks = new ArrayList<>(); // x, y, kind index
	static final String[] ORDER = { "MUST-REACH", "UNREACHABLE", "TILE-OVERFULL", "DOOR-NO-WALL", "OPENING-UNBACKED", "PATH-DEAD-END",
			"PATH-DIAGONAL-JOIN", "PATH-OBSTRUCTED", "STRAIGHT-PATH", "STRAIGHT-WATER", "WALL-FRAGMENT", "WALL-LOOSE-END",
			"ON-WATER", "WATER-WALKABLE", "STEEP-FOOTPRINT", "OVERLAP", "PATH-SCRAP",
			// design (DesignLint): not broken, but amateur
			"NPC-STUCK", "NPC-UNREACHABLE", "UNSERVED", "LANDMARK-UNPLACED", "LANDMARK-MISSING", "LANDMARK-UNSEEN", "PATH-ISLAND",
			"CLIFF-WALKABLE", "HEIGHT-SPIKE", "COPY-PASTE", "GRID-PATTERN", "UNIFORM-ROTATION", "STRAIGHT-FLOOR-EDGE" };

	/** An object in the area, with what the lint needs from its definition. */
	final class Obj {
		final int id, x, y, type, rot, sx, sy;
		final String name, origin;
		final boolean solid, actions;

		Obj(MapRegion.MapObject o, int wx, int wy) {
			id = o.id;
			origin = o.origin;
			x = wx;
			y = wy;
			type = o.type;
			rot = o.rotation;
			ConfigDefinitions.Definition d = o.id < locs.size() ? locs.decode(o.id) : null;
			String n = d == null ? null : d.text(2);
			name = n == null ? "#" + o.id : n;
			int w = d == null || d.get(14) == null ? 1 : d.get(14)[0] & 0xFF, l = d == null || d.get(15) == null ? 1 : d.get(15)[0] & 0xFF;
			sx = (rot & 1) == 1 ? l : w;
			sy = (rot & 1) == 1 ? w : l;
			solid = d == null || d.get(17) == null;
			boolean a = false;
			for (int op = 30; op < 35 && d != null; op++) {
				String s = d.text(op);
				a |= s != null && !s.equalsIgnoreCase("hidden");
			}
			actions = a;
		}

		boolean is(String... words) {
			String n = name.toLowerCase();
			for (String w : words) {
				if (n.contains(w)) {
					return true;
				}
			}
			return false;
		}

		boolean scenery() {
			return type == 10 || type == 11;
		}

		boolean wall() {
			return type <= 3 || type == 9;
		}

		@Override
		public String toString() {
			return "'" + name + "' (" + id + ", type " + type + ", rot " + rot + ")";
		}
	}

	/** Lints the area of a world held in memory (the generators' regions, before they are packed). */
	WorldLint(Map<Integer, MapRegion> world, ConfigDefinitions.Table locs, int x0, int y0, int x1, int y1) {
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
		this.locs = locs;
		for (MapRegion r : world.values()) {
			int rx = r.regionX, ry = r.regionY;
			if (rx * 64 + 63 < x0 - 2 || rx * 64 > x1 + 2 || ry * 64 + 63 < y0 - 2 || ry * 64 > y1 + 2) {
				continue;
			}
			regions.put(r.id(), r);
			for (MapRegion.MapObject mo : r.objects) {
				int wx = rx * 64 + mo.x, wy = ry * 64 + mo.y;
				if (mo.z == 0 && in(wx, wy)) {
					objects.add(new Obj(mo, wx, wy));
				}
			}
		}
	}

	/** Lints the area as the finished cache has it (so whatever wrote it, this is what the player gets). */
	static WorldLint fromCache(CacheEditor cache, int x0, int y0, int x1, int y1) throws Exception {
		CacheEditor.Archive config = CacheEditor.Archive.decode(cache.read(0, ConfigDefinitions.CONFIG_ARCHIVE));
		CacheEditor.Archive versions = CacheEditor.Archive.decode(cache.read(0, CacheEditor.VERSIONLIST_ARCHIVE));
		Map<Integer, MapRegion> world = new HashMap<>();
		for (CacheEditor.MapIndexEntry e : CacheEditor.parseMapIndex(versions.get("map_index"))) {
			int rx = e.region >> 8, ry = e.region & 0xFF;
			if (rx * 64 + 63 < x0 - 2 || rx * 64 > x1 + 2 || ry * 64 + 63 < y0 - 2 || ry * 64 > y1 + 2) {
				continue;
			}
			byte[] t = cache.read(4, e.terrainFile), o = cache.read(4, e.objectFile);
			if (t == null) {
				continue;
			}
			try {
				world.put(e.region, MapRegion.decode(rx, ry, CompressionUtil.degzip(ByteBuffer.wrap(t)),
						o == null ? null : CompressionUtil.degzip(ByteBuffer.wrap(o))));
			} catch (RuntimeException ex) {
				System.out.println("(region " + rx + "," + ry + " couldn't be read and is skipped: " + ex + ")");
			}
		}
		return new WorldLint(world, ConfigDefinitions.load(config, "loc", false), x0, y0, x1, y1);
	}

	/** Runs every check; with start tiles, also what can't be walked to from any of them. */
	void checkAll(int[]... from) {
		paths();
		straightEdges(this::water, "STRAIGHT-WATER", STRAIGHT_WATER);
		walls();
		openings();
		placement();
		wetFeet();
		if (from != null && from.length > 0 && from[0] != null) {
			reach(from);
		}
	}

	boolean in(int x, int y) {
		return x >= x0 && x <= x1 && y >= y0 && y <= y1;
	}

	MapRegion region(int x, int y) {
		return regions.get((x >> 6) << 8 | (y >> 6));
	}

	int overlay(int x, int y) {
		MapRegion r = region(x, y);
		return r == null ? 0 : r.overlay[0][x & 63][y & 63];
	}

	int height(int x, int y) {
		MapRegion r = region(x, y);
		return r == null ? 0 : Math.max(0, r.height[0][x & 63][y & 63]);
	}

	boolean path(int x, int y) {
		return in(x, y) && PATH_FLOORS.contains(overlay(x, y));
	}

	boolean water(int x, int y) {
		return WATER_FLOORS.contains(overlay(x, y)) && (region(x, y) == null || (region(x, y).settings[0][x & 63][y & 63] & MapRegion.BRIDGE) == 0);
	}

	/** A finding someone has looked at and decided is right as it is: one line of the accepted file. */
	static final class Accepted {
		final String kind, why, line;
		final int x, y, lineNo;
		int hits;

		Accepted(String kind, int x, int y, String why, String line, int lineNo) {
			this.kind = kind;
			this.x = x;
			this.y = y;
			this.why = why;
			this.line = line;
			this.lineNo = lineNo;
		}
	}

	/** Reads the accepted file: "KIND x,y  why" per line, '#' comments. Lines it can't read come back in {@code bad}. */
	static List<Accepted> readAccepted(java.nio.file.Path file, List<String> bad) throws java.io.IOException {
		List<Accepted> out = new ArrayList<>();
		if (!java.nio.file.Files.exists(file)) {
			return out;
		}
		List<String> lines = java.nio.file.Files.readAllLines(file);
		for (int i = 0; i < lines.size(); i++) {
			String l = lines.get(i).replaceAll("#.*", "").trim();
			if (l.isEmpty()) {
				continue;
			}
			String[] f = l.split("\\s+", 3);
			String[] xy = f.length > 1 ? f[1].split(",") : new String[0];
			if (f.length < 3 || xy.length != 2 || !xy[0].matches("\\d+") || !xy[1].matches("\\d+")) {
				bad.add("line " + (i + 1) + ": \"" + lines.get(i).trim() + "\" - needs KIND x,y and a reason why it's right");
				continue;
			}
			out.add(new Accepted(f[0], Integer.parseInt(xy[0]), Integer.parseInt(xy[1]), f[2], lines.get(i).trim(), i + 1));
		}
		return out;
	}

	List<Accepted> accepted = new ArrayList<>();
	int acceptedHits;

	void report(String kind, int x, int y, String text) {
		for (Accepted a : accepted) {
			if (a.kind.equals(kind) && Math.abs(a.x - x) <= 2 && Math.abs(a.y - y) <= 2) {
				a.hits++;
				acceptedHits++;
				return;
			}
		}
		String from = madeBy(kind, x, y);
		findings.computeIfAbsent(kind, k -> new ArrayList<>())
				.add(String.format("%-18s %d,%d  %s", kind, x, y, text) + (from == null ? "" : "\n" + String.format("%-18s %s", "", "<- " + from)));
		int k = 0;
		for (int i = 0; i < ORDER.length; i++) {
			if (ORDER[i].equals(kind)) {
				k = i;
			}
		}
		marks.add(new int[] { x, y, k });
	}

	/**
	 * The generator code behind a finding: the road painted through it for path findings, otherwise whatever placed the
	 * objects on (or right next to) that tile. Null when it was all stock map.
	 */
	String madeBy(String kind, int x, int y) {
		if (kind.startsWith("PATH-") || kind.equals("STRAIGHT-PATH") || kind.equals("UNREACHABLE") && path(x, y)) {
			String painted = Origin.pathTileNear(x, y);
			if (painted != null) {
				return "path painted by " + painted;
			}
			String road = Origin.roadNear(x, y, 4);
			if (road != null) {
				return "road from " + road;
			}
		}
		java.util.LinkedHashSet<String> who = new java.util.LinkedHashSet<>();
		for (int r = 0; r <= 1 && who.isEmpty(); r++) {
			for (int dx = -r; dx <= r; dx++) {
				for (int dy = -r; dy <= r; dy++) {
					for (Obj o : objectsAt(x + dx, y + dy)) {
						boolean relevant = !(kind.startsWith("WALL-") || kind.startsWith("DOOR-")) || o.wall();
						if (relevant && o.origin != null && !o.origin.equals(Origin.STOCK)) {
							who.add(o.name + " from " + o.origin);
						}
					}
				}
			}
		}
		if (who.isEmpty()) {
			return null;
		}
		List<String> l = new ArrayList<>(who);
		return String.join("; ", l.subList(0, Math.min(2, l.size()))) + (l.size() > 2 ? " (+" + (l.size() - 2) + " more)" : "");
	}

	/* ------------------------------------------------------------------ paths */

	void paths() {
		boolean[][] seen = new boolean[x1 - x0 + 1][y1 - y0 + 1];
		for (int x = x0; x <= x1; x++) {
			for (int y = y0; y <= y1; y++) {
				if (!path(x, y) || seen[x - x0][y - y0]) {
					continue;
				}
				List<int[]> comp = new ArrayList<>();
				ArrayDeque<int[]> q = new ArrayDeque<>();
				q.add(new int[] { x, y });
				seen[x - x0][y - y0] = true;
				while (!q.isEmpty()) {
					int[] p = q.poll();
					comp.add(p);
					for (int[] d : new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } }) {
						int nx = p[0] + d[0], ny = p[1] + d[1];
						if (path(nx, ny) && !seen[nx - x0][ny - y0]) {
							seen[nx - x0][ny - y0] = true;
							q.add(new int[] { nx, ny });
						}
					}
				}
				pathComponent(comp);
			}
		}
		// two path tiles touching only at a corner: the player sees a road, walks it, and finds a pinched join
		for (int x = x0; x < x1; x++) {
			for (int y = y0; y < y1; y++) {
				if (path(x, y) && path(x + 1, y + 1) && !path(x + 1, y) && !path(x, y + 1)
						|| path(x + 1, y) && path(x, y + 1) && !path(x, y) && !path(x + 1, y + 1)) {
					report("PATH-DIAGONAL-JOIN", x, y, "path tiles meet only at a corner: widen the joint");
				}
			}
		}
		straightEdges(this::path, "STRAIGHT-PATH", STRAIGHT_PATH);
	}

	void pathComponent(List<int[]> comp) {
		if (comp.size() < 6) {
			int[] p = comp.get(0);
			if (!near(p[0], p[1], 2, this::anchor)) {
				report("PATH-SCRAP", p[0], p[1], comp.size() + " tile(s) of path on their own");
			}
			return;
		}
		// a path's ends: tiles that see far fewer path tiles around them than the path's ordinary width gives
		int[] counts = new int[comp.size()];
		for (int i = 0; i < comp.size(); i++) {
			int c = 0;
			for (int dx = -3; dx <= 3; dx++) {
				for (int dy = -3; dy <= 3; dy++) {
					c += path(comp.get(i)[0] + dx, comp.get(i)[1] + dy) ? 1 : 0;
				}
			}
			counts[i] = c;
		}
		int[] sorted = counts.clone();
		java.util.Arrays.sort(sorted);
		int median = sorted[sorted.length / 2];
		List<int[]> ends = new ArrayList<>();
		for (int i = 0; i < comp.size(); i++) {
			if (counts[i] < median * 0.62) {
				ends.add(comp.get(i));
			}
		}
		for (int[] e : cluster(ends, 4)) {
			if (!near(e[0], e[1], 3, this::anchor) && !edgeOfArea(e[0], e[1], 3)) {
				report("PATH-DEAD-END", e[0], e[1], "the path stops here with nothing to arrive at (" + comp.size() + "-tile path)");
			}
		}
	}

	/** Something a path can sensibly end at: a door, building, jetty, landmark, camp, water's edge or another floor. */
	boolean anchor(int x, int y) {
		if (water(x, y) || STRUCTURE_FLOORS.contains(overlay(x, y))) {
			return true;
		}
		for (Obj o : objectsAt(x, y)) {
			if (o.actions || o.wall() || o.is("tent", "fire", "sign", "statue", "stone", "gate", "door", "ladder", "stair", "cave",
					"bridge", "jetty", "well", "altar", "portal", "shrine", "stall", "table", "boat", "waystone", "tower", "banner",
					"fountain", "grave", "tomb", "crystal", "rift", "lantern", "skull", "bone", "ribs", "skeleton", "wreck")) {
				return true;
			}
		}
		return false;
	}

	boolean edgeOfArea(int x, int y, int r) {
		return x - x0 < r || x1 - x < r || y - y0 < r || y1 - y < r;
	}

	/* ------------------------------------------------------------- straightness */

	interface TileSet {
		boolean has(int x, int y);
	}

	/**
	 * Edges of a floor that run dead straight: along a row or column, a true diagonal, or a 2:1 slope. Worn roads,
	 * rivers and shores wander; a ruler-straight edge is the generator showing through.
	 */
	void straightEdges(TileSet set, String kind, int straight) {
		straightEdges(set, kind, straight, straight);
	}

	/** As above, with a separate (longer) allowance for runs that aren't along the grid. */
	void straightEdges(TileSet set, String kind, int straight, int slanted) {
		int[][] dirs = { { 1, 0 }, { 0, 1 }, { 1, 1 }, { 1, -1 }, { 2, 1 }, { 1, 2 }, { 2, -1 }, { 1, -2 } };
		Set<Long> reported = new HashSet<>();
		for (int[] d : dirs) {
			Set<Long> used = new HashSet<>();
			for (int x = x0; x <= x1; x++) {
				for (int y = y0; y <= y1; y++) {
					int[] out = outside(set, x, y);
					if (out == null || used.contains(key(x, y))) {
						continue;
					}
					// the outward side must be across the line, and the same all the way along it
					int side = Integer.signum(out[0] * -d[1] + out[1] * d[0]);
					if (side == 0) {
						continue;
					}
					if (outsideMatches(set, x - d[0], y - d[1], d, side)) {
						continue; // not the start of the run
					}
					int n = 0, cx = x, cy = y;
					while (outsideMatches(set, cx, cy, d, side)) {
						used.add(key(cx, cy));
						n++;
						cx += d[0];
						cy += d[1];
					}
					double len = n * Math.hypot(d[0], d[1]);
					if (len >= (d[0] == 0 || d[1] == 0 ? straight : slanted) && !edgeOfArea(x, y, 2) && !edgeOfArea(cx - d[0], cy - d[1], 2)) {
						int mx = (x + cx - d[0]) / 2, my = (y + cy - d[1]) / 2;
						if (reported.add(key(mx / 3, my / 3))) {
							report(kind, mx, my, String.format("edge runs dead straight for %.0f tiles (%d,%d to %d,%d)", len, x, y,
									cx - d[0], cy - d[1]));
						}
					}
				}
			}
		}
	}

	boolean outsideMatches(TileSet set, int x, int y, int[] d, int side) {
		int[] out = outside(set, x, y);
		return out != null && Integer.signum(out[0] * -d[1] + out[1] * d[0]) == side;
	}

	/** For a tile on the set's edge, the summed direction to its outside 4-neighbours; null if not an edge tile. */
	int[] outside(TileSet set, int x, int y) {
		if (!in(x, y) || !set.has(x, y)) {
			return null;
		}
		int ox = 0, oy = 0;
		boolean edge = false;
		for (int[] d : new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } }) {
			if (!set.has(x + d[0], y + d[1])) {
				ox += d[0];
				oy += d[1];
				edge = true;
			}
		}
		return edge ? new int[] { ox, oy } : null;
	}

	static long key(int x, int y) {
		return (long) x << 32 | (y & 0xFFFFFFFFL);
	}

	/* ------------------------------------------------------------ walls, doors */

	/**
	 * Walls as a graph of unit segments between tile corners. A door must sit in a wall (wall on both of its ends), a
	 * wall should run into something (another wall, a post, rock, a building, a cliff or water), and a short run of wall
	 * standing on its own in open ground is a stray.
	 */
	void walls() {
		Map<Long, List<long[]>> at = new HashMap<>(); // corner -> segments (a, b, door?)
		Map<Long, Obj> owner = new HashMap<>();
		Set<Long> posts = new HashSet<>();
		List<long[]> segs = new ArrayList<>();
		for (Obj o : objects) {
			if (!o.wall()) {
				continue;
			}
			int x = o.x, y = o.y;
			long[][] corners = { { x, y + 1 }, { x + 1, y + 1 }, { x + 1, y }, { x, y } }; // NW, NE, SE, SW
			long[][] edges = { { key(x, y), key(x, y + 1) }, { key(x, y + 1), key(x + 1, y + 1) }, { key(x + 1, y), key(x + 1, y + 1) },
					{ key(x, y), key(x + 1, y) } }; // W, N, E, S
			List<long[]> mine = new ArrayList<>();
			if (o.type == 0) {
				mine.add(edges[o.rot & 3]);
			} else if (o.type == 2) {
				mine.add(edges[o.rot & 3]);
				mine.add(edges[(o.rot + 1) & 3]);
			} else if (o.type == 9) {
				mine.add((o.rot & 1) == 0 ? new long[] { key(x, y), key(x + 1, y + 1) } : new long[] { key(x, y + 1), key(x + 1, y) });
			} else { // a post at a corner
				long[] c = corners[o.rot & 3];
				posts.add(key((int) c[0], (int) c[1]));
				continue;
			}
			boolean door = o.is("door", "gate");
			for (long[] e : mine) {
				long[] s = { e[0], e[1], door ? 1 : 0 };
				segs.add(s);
				at.computeIfAbsent(e[0], k -> new ArrayList<>()).add(s);
				at.computeIfAbsent(e[1], k -> new ArrayList<>()).add(s);
				owner.put(e[0] * 31 + e[1], o);
			}
		}
		// components of connected segments
		Set<long[]> seen = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
		for (long[] s0 : segs) {
			if (seen.contains(s0)) {
				continue;
			}
			List<long[]> comp = new ArrayList<>();
			ArrayDeque<long[]> q = new ArrayDeque<>();
			q.add(s0);
			seen.add(s0);
			while (!q.isEmpty()) {
				long[] s = q.poll();
				comp.add(s);
				for (int e = 0; e < 2; e++) {
					for (long[] n : at.get(s[e])) {
						if (seen.add(n)) {
							q.add(n);
						}
					}
				}
			}
			Set<Long> loose = new HashSet<>();
			int doors = 0;
			for (long[] s : comp) {
				doors += (int) s[2];
				for (int e = 0; e < 2; e++) {
					if (at.get(s[e]).size() == 1 && !posts.contains(s[e]) && !backed(cx(s[e]), cy(s[e]))) {
						loose.add(s[e]);
					}
				}
			}
			// two loose ends a step or two apart are the two sides of a doorway or a gap in a fence: that's fine
			for (long c : new ArrayList<>(loose)) {
				for (long d : new ArrayList<>(loose)) {
					if (c != d && Math.max(Math.abs(cx(c) - cx(d)), Math.abs(cy(c) - cy(d))) <= 2) {
						loose.remove(c);
						loose.remove(d);
					}
				}
			}
			Obj o = owner.get(comp.get(0)[0] * 31 + comp.get(0)[1]);
			boolean ruin = o != null && o.is("ruin", "broken", "crumbl", "rubble");
			int fx = cx(comp.get(0)[0]), fy = cy(comp.get(0)[0]);
			for (long[] s : comp) {
				if (s[2] == 1) {
					boolean a = at.get(s[0]).size() > 1 || posts.contains(s[0]) || backed(cx(s[0]), cy(s[0]));
					boolean b = at.get(s[1]).size() > 1 || posts.contains(s[1]) || backed(cx(s[1]), cy(s[1]));
					if (!a || !b) {
						Obj d = owner.get(s[0] * 31 + s[1]);
						report("DOOR-NO-WALL", cx(s[0]), cy(s[0]), (d == null ? "door" : d.toString())
								+ " has no wall on " + (!a && !b ? "either side" : "one side") + ": a door needs a wall to be a door in");
					}
				}
			}
			if (comp.size() - doors <= 3 && !loose.isEmpty() && !ruin) {
				report("WALL-FRAGMENT", fx, fy, comp.size() + " segment(s) of " + (o == null ? "wall" : o.toString())
						+ " standing on their own: join it to something or take it out");
			} else if (!ruin) {
				for (long c : loose) {
					report("WALL-LOOSE-END", cx(c), cy(c), (o == null ? "wall" : o.toString())
							+ " stops in open ground: run it into a building, rock, post or cliff, or close the loop");
				}
			}
		}
	}

	static int cx(long k) {
		return (int) (k >> 32);
	}

	static int cy(long k) {
		return (int) k;
	}

	/** Whether a wall's end at this tile corner runs into something: solid scenery, a cliff, or water. */
	/**
	 * Things that don't hold a wall up: plants and loose props. A wall that stops against a fern or a barrel still stops
	 * in open ground - only rock, a cliff, a building or another structure can take the end of a wall.
	 */
	static final String[] SOFT = { "tree", "bush", "fern", "plant", "grass", "log", "mushroom", "fungus", "flower", "reed", "weed",
			"shrub", "barrel", "crate", "sack", "box", "bones", "skull", "root", "branch", "stump", "leaves", "palm", "web", "net" };

	boolean backed(int cx, int cy) {
		for (int x = cx - 1; x <= cx; x++) {
			for (int y = cy - 1; y <= cy; y++) {
				if (water(x, y)) {
					return true;
				}
				for (Obj o : objectsAt(x, y)) {
					if (o.scenery() && o.solid && !o.is(SOFT)) {
						return true;
					}
				}
			}
		}
		int lo = Integer.MAX_VALUE, hi = Integer.MIN_VALUE;
		for (int x = cx - 1; x <= cx + 1; x++) {
			for (int y = cy - 1; y <= cy + 1; y++) {
				lo = Math.min(lo, height(x, y));
				hi = Math.max(hi, height(x, y));
			}
		}
		return hi - lo >= 6;
	}

	/**
	 * Openings that stand as scenery - cave mouths, tunnels, archways, doorways: each needs something to open into. A
	 * cave mouth wants a cliff or rock face behind it, a doorway wants walls either side.
	 */
	void openings() {
		for (Obj o : objects) {
			if (!o.scenery() || !o.is("cave", "tunnel", "doorway", "archway", "entrance", "crevice", "passage", "mine")
					|| o.is("trapdoor", "hatch", "ladder", "stair", "cart")) {
				continue;
			}
			int base = height(o.x, o.y), rise = 0, solids = 0;
			for (int x = o.x - 2; x <= o.x + o.sx + 1; x++) {
				for (int y = o.y - 2; y <= o.y + o.sy + 1; y++) {
					rise = Math.max(rise, height(x, y) - base);
					for (Obj n : objectsAt(x, y)) {
						if (n != o && n.solid && (n.scenery() || n.wall())) {
							solids++;
						}
					}
				}
			}
			if (rise < 6 && solids < 3) {
				report("OPENING-UNBACKED", o.x, o.y, o + " stands in open ground (ground rises " + rise
						+ ", " + solids + " solid things beside it): set it into a cliff, rock face or wall");
			}
		}
	}

	/* ------------------------------------------------------------- placement */

	void placement() {
		Map<Long, List<Obj>> byTile = new HashMap<>();
		for (Obj o : objects) {
			if (!o.scenery()) {
				continue;
			}
			for (int x = o.x; x < o.x + o.sx; x++) {
				for (int y = o.y; y < o.y + o.sy; y++) {
					byTile.computeIfAbsent(key(x, y), k -> new ArrayList<>()).add(o);
				}
			}
		}
		Set<Obj> overlapped = new HashSet<>(); // (the same object twice on a tile is deliberate: two halves of an arch)
		for (Map.Entry<Long, List<Obj>> e : byTile.entrySet()) {
			List<Obj> l = e.getValue();
			int x = cx(e.getKey()), y = cy(e.getKey());
			if (l.size() >= 5) {
				report("TILE-OVERFULL", x, y, l.size() + " objects cover this tile: the client won't draw the fifth or later");
			}
			for (int i = 0; i < l.size(); i++) {
				for (int j = i + 1; j < l.size(); j++) {
					Obj a = l.get(i), b = l.get(j);
					if (a.solid && b.solid && a.id != b.id && overlapped.add(a.id < b.id ? a : b)) {
						report("OVERLAP", x, y, a + " and " + b + " occupy the same tile");
					}
				}
			}
		}
		for (Obj o : objects) {
			// in the middle of a road (not at its edge, where a sign or a lamp belongs)
			if (o.scenery() && o.solid && !o.is("door", "gate")) {
				road: for (int x = o.x; x < o.x + o.sx; x++) {
					for (int y = o.y; y < o.y + o.sy; y++) {
						if (path(x, y) && path(x + 1, y) && path(x - 1, y) && path(x, y + 1) && path(x, y - 1)) {
							report("PATH-OBSTRUCTED", x, y, o + " blocks the middle of the road");
							break road;
						}
					}
				}
			}
			// out in the water
			if (o.type != 22 && water(o.x, o.y) && !o.name.startsWith("#") && !o.is("boat", "ship", "jetty", "pier", "bridge", "fish", "buoy", "rock", "lily", "reed",
					"net", "waterfall", "wreck", "mast", "hull", "figurehead", "post", "pole", "crate", "barrel", "pool", "tear", "spout",
					"bubble", "kelp")) {
				report("ON-WATER", o.x, o.y, o + " stands in open water");
			}
			// something with a flat base (a tent, hut, stall, table, big prop) on ground that tilts under it
			if (o.scenery() && (o.sx > 1 || o.sy > 1 || o.is("tent", "stall", "table", "bench", "hut", "house", "bed", "cart"))
					&& !o.is("rock", "boulder", "tree", "palm", "root", "bush", "crystal", "skull", "ribs", "bone", "cave", "entrance", "oak",
							"willow", "yew", "maple", "evergreen", "pine", "standing stone", "stump", "log")) {
				int lo = Integer.MAX_VALUE, hi = Integer.MIN_VALUE;
				for (int x = o.x; x <= o.x + o.sx; x++) {
					for (int y = o.y; y <= o.y + o.sy; y++) {
						lo = Math.min(lo, height(x, y));
						hi = Math.max(hi, height(x, y));
					}
				}
				if (hi - lo >= 5) {
					report("STEEP-FOOTPRINT", o.x, o.y, o + " sits on ground that drops " + (hi - lo) * 8
							+ " units across it: one side will float or sink. Level the ground under it");
				}
			}
		}
	}

	/** Whole tiles of water with nothing stopping the player walking out across them (no block flag, not a bridge). */
	void wetFeet() {
		List<int[]> wet = new ArrayList<>();
		for (int x = x0; x <= x1; x++) {
			for (int y = y0; y <= y1; y++) {
				MapRegion r = region(x, y);
				if (r != null && water(x, y) && r.overlayShape[0][x & 63][y & 63] == 0
						&& (r.settings[0][x & 63][y & 63] & MapRegion.BLOCKED) == 0) {
					wet.add(new int[] { x, y });
				}
			}
		}
		for (int[] c : cluster(wet, 2)) {
			report("WATER-WALKABLE", c[0], c[1], "the player can walk out onto this water: block it (or make it a bridge or a shallow ford on purpose)");
		}
	}

	/* ---------------------------------------------------------- reachability */

	/** The walkable ground reach() flooded from its start tile. */
	boolean[][] reached;

	/**
	 * Whether the player can walk onto this tile, or up beside it (an npc, a door, a cave mouth), from the start - or
	 * beside the whole of any object standing on it, since a big object is used from any of its sides.
	 */
	boolean reachable(int x, int y) {
		// the things on the spot a player would use (not, say, a big sculpted model that happens to overlap it)
		List<Obj> here = new ArrayList<>(objectsAt(x, y));
		if (here.stream().anyMatch(o -> o.actions)) {
			here.removeIf(o -> !o.actions);
		}
		if (here.isEmpty()) { // a bare spot (an npc's post): any neighbour will do
			for (int i = x - 1; i <= x + 1; i++) {
				for (int j = y - 1; j <= y + 1; j++) {
					if (reached != null && in(i, j) && reached[i - x0][j - y0]) {
						return true;
					}
				}
			}
			return false;
		}
		for (Obj o : here) {
			if (besideReached(o)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Whether a reached tile lies along one of the object's sides. The client walks up to an object only from a side,
	 * never a corner (Game.doWalkTo is called with no walk-to-nearest fallback for object clicks), so an object whose
	 * only open neighbours are diagonal can be seen and clicked but never used.
	 */
	boolean besideReached(Obj o) {
		int sx = Math.max(1, o.sx), sy = Math.max(1, o.sy);
		for (int i = o.x - 1; i <= o.x + sx; i++) {
			for (int j = o.y - 1; j <= o.y + sy; j++) {
				boolean corner = (i == o.x - 1 || i == o.x + sx) && (j == o.y - 1 || j == o.y + sy);
				if (!corner && reached != null && in(i, j) && reached[i - x0][j - y0]) {
					return true;
				}
			}
		}
		return false;
	}

	void reach(int sx, int sy) {
		reach(new int[][] { { sx, sy } });
	}

	/** Floods the walkable ground from the start tiles and reports paths and usable things that can't be reached. */
	void reach(int[][] starts) {
		int sx = starts[0][0], sy = starts[0][1];
		boolean[][] solid = new boolean[x1 - x0 + 1][y1 - y0 + 1];
		Set<Long> blockedEdge = new HashSet<>(); // key(tile) * 4 + direction (0 W, 1 N, 2 E, 3 S)
		for (int x = x0; x <= x1; x++) {
			for (int y = y0; y <= y1; y++) {
				MapRegion r = region(x, y);
				solid[x - x0][y - y0] = r == null || (r.settings[0][x & 63][y & 63] & MapRegion.BLOCKED) != 0 || water(x, y);
			}
		}
		for (Obj o : objects) {
			if (o.scenery() && o.solid) {
				for (int x = o.x; x < o.x + o.sx; x++) {
					for (int y = o.y; y < o.y + o.sy; y++) {
						if (in(x, y)) {
							solid[x - x0][y - y0] = true;
						}
					}
				}
			} else if (o.solid && (o.type == 0 || o.type == 2) && !o.is("door", "gate")) {
				int[][] step = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };
				for (int k = 0; k < (o.type == 2 ? 2 : 1); k++) {
					int d = (o.rot + k) & 3;
					blockedEdge.add(key(o.x, o.y) * 4 + d);
					blockedEdge.add(key(o.x + step[d][0], o.y + step[d][1]) * 4 + ((d + 2) & 3));
				}
			}
		}
		reached = new boolean[x1 - x0 + 1][y1 - y0 + 1];
		ArrayDeque<int[]> q = new ArrayDeque<>();
		for (int[] st : starts) {
			if (in(st[0], st[1])) {
				reached[st[0] - x0][st[1] - y0] = true;
				q.add(new int[] { st[0], st[1] });
			}
		}
		int[][] step = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };
		while (!q.isEmpty()) {
			int[] p = q.poll();
			for (int d = 0; d < 4; d++) {
				int nx = p[0] + step[d][0], ny = p[1] + step[d][1];
				if (in(nx, ny) && !reached[nx - x0][ny - y0] && !solid[nx - x0][ny - y0] && !blockedEdge.contains(key(p[0], p[1]) * 4 + d)) {
					reached[nx - x0][ny - y0] = true;
					q.add(new int[] { nx, ny });
				}
			}
		}
		List<int[]> lost = new ArrayList<>();
		for (int x = x0; x <= x1; x++) {
			for (int y = y0; y <= y1; y++) {
				if (path(x, y) && !reached[x - x0][y - y0] && !solid[x - x0][y - y0]) {
					lost.add(new int[] { x, y });
				}
			}
		}
		for (int[] c : cluster(lost, 3)) {
			report("UNREACHABLE", c[0], c[1], "a path you can't walk to from " + sx + "," + sy);
		}
		for (Obj o : objects) {
			if (!o.actions) {
				continue;
			}
			if (!besideReached(o) && !edgeOfArea(o.x, o.y, 2)) {
				report("UNREACHABLE", o.x, o.y, o + " can't be walked up to from " + sx + "," + sy);
			}
		}
	}

	/* ------------------------------------------------------------------ util */

	final Map<Long, List<Obj>> objectIndex = new HashMap<>();

	List<Obj> objectsAt(int x, int y) {
		if (objectIndex.isEmpty()) {
			for (Obj o : objects) {
				for (int i = o.x; i < o.x + Math.max(1, o.sx); i++) {
					for (int j = o.y; j < o.y + Math.max(1, o.sy); j++) {
						objectIndex.computeIfAbsent(key(i, j), k -> new ArrayList<>()).add(o);
					}
				}
			}
		}
		return objectIndex.getOrDefault(key(x, y), List.of());
	}

	boolean near(int x, int y, int r, TileSet test) {
		for (int dx = -r; dx <= r; dx++) {
			for (int dy = -r; dy <= r; dy++) {
				if (test.has(x + dx, y + dy)) {
					return true;
				}
			}
		}
		return false;
	}

	/** Groups tiles within r of each other; returns one representative (the middle one) per group. */
	static List<int[]> cluster(List<int[]> tiles, int r) {
		List<List<int[]>> groups = new ArrayList<>();
		for (int[] t : tiles) {
			List<int[]> home = null;
			for (List<int[]> g : groups) {
				for (int[] u : g) {
					if (Math.abs(u[0] - t[0]) <= r && Math.abs(u[1] - t[1]) <= r) {
						home = g;
						break;
					}
				}
				if (home != null) {
					break;
				}
			}
			if (home == null) {
				groups.add(home = new ArrayList<>());
			}
			home.add(t);
		}
		List<int[]> out = new ArrayList<>();
		for (List<int[]> g : groups) {
			out.add(g.get(g.size() / 2));
		}
		return out;
	}

	void print(int max, Set<String> only) {
		Map<String, List<String>> ordered = new LinkedHashMap<>();
		for (String k : ORDER) {
			if (findings.containsKey(k) && (only == null || only.contains(k))) {
				ordered.put(k, findings.get(k));
			}
		}
		int total = 0;
		for (Map.Entry<String, List<String>> e : ordered.entrySet()) {
			List<String> l = e.getValue();
			for (int i = 0; i < Math.min(max, l.size()); i++) {
				System.out.println(l.get(i));
			}
			if (l.size() > max) {
				System.out.println(String.format("%-18s ... and %d more", e.getKey(), l.size() - max));
			}
			total += l.size();
		}
		System.out.println("-- " + total + " finding(s) in " + x0 + "," + y0 + " to " + x1 + "," + y1 + ": " + summary(ordered)
				+ (acceptedHits > 0 ? " (" + acceptedHits + " accepted, not shown)" : ""));
	}

	static String summary(Map<String, List<String>> f) {
		StringBuilder b = new StringBuilder();
		f.forEach((k, v) -> b.append(b.length() == 0 ? "" : ", ").append(k).append(' ').append(v.size()));
		return b.length() == 0 ? "clean" : b.toString();
	}

	void png(CacheEditor cache, String file, Set<String> only) throws Exception {
		int scale = 6;
		BufferedImage img = new MapRender(cache).render(id -> regions.get(id), x0, y0, x1 - x0 + 1, y1 - y0 + 1, scale, false);
		Graphics2D g = img.createGraphics();
		Color[] colors = { Color.RED, Color.RED, Color.MAGENTA, Color.ORANGE, Color.ORANGE, Color.YELLOW, Color.YELLOW, Color.PINK, Color.CYAN,
				Color.CYAN, Color.WHITE, Color.WHITE, Color.BLUE, Color.BLUE, Color.GREEN, Color.LIGHT_GRAY, Color.LIGHT_GRAY,
				Color.RED, Color.RED, Color.MAGENTA, Color.MAGENTA, Color.MAGENTA, Color.MAGENTA, Color.ORANGE, Color.YELLOW, Color.YELLOW,
				Color.PINK, Color.PINK, Color.PINK, Color.CYAN };
		g.setStroke(new BasicStroke(2));
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
		for (int[] m : marks) {
			if (only != null && !only.contains(ORDER[m[2]])) {
				continue;
			}
			int px = (m[0] - x0) * scale + scale / 2, py = (y1 - m[1]) * scale + scale / 2;
			g.setColor(Color.BLACK);
			g.drawOval(px - 9, py - 9, 18, 18);
			g.setColor(colors[m[2] % colors.length]);
			g.drawOval(px - 8, py - 8, 16, 16);
			g.drawString(ORDER[m[2]].substring(0, 4), px + 9, py - 6);
		}
		ImageIO.write(img, "png", new File(file));
	}

	public static void main(String[] args) throws Exception {
		int x0 = Integer.parseInt(args[0]), y0 = Integer.parseInt(args[1]), x1 = Integer.parseInt(args[2]), y1 = Integer.parseInt(args[3]);
		int[] from = null;
		String png = null;
		Set<String> only = null;
		int max = 40;
		String accept = "tools/worldlint-accepted.txt";
		for (int i = 4; i < args.length; i++) {
			switch (args[i]) {
			case "-from":
				String[] f = args[++i].split(",");
				from = new int[] { Integer.parseInt(f[0]), Integer.parseInt(f[1]) };
				break;
			case "-png":
				png = args[++i];
				break;
			case "-only":
				only = new HashSet<>(java.util.Arrays.asList(args[++i].split(",")));
				break;
			case "-accept":
				accept = args[++i];
				break;
			case "-max":
				max = Integer.parseInt(args[++i]);
				break;
			default:
				throw new IllegalArgumentException("unknown option " + args[i]);
			}
		}
		try (CacheEditor cache = new CacheEditor(Paths.get("data/cache"))) {
			WorldLint lint = fromCache(cache, Math.min(x0, x1), Math.min(y0, y1), Math.max(x0, x1), Math.max(y0, y1));
			List<String> bad = new ArrayList<>();
			lint.accepted = readAccepted(Paths.get(accept), bad);
			bad.forEach(b -> System.out.println("(accepted file " + b + ")"));
			lint.checkAll(from == null ? null : new int[][] { from });
			lint.print(max, only);
			if (png != null) {
				lint.png(cache, png, only);
			}
		}
	}
}
