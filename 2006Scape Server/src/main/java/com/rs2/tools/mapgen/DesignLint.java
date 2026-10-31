package com.rs2.tools.mapgen;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The design half of the world gate. WorldLint catches things that are broken; this catches things that are amateur -
 * the tells that a place was written as code rather than made: every tree turned the same way, props in ruled rows, the
 * same little camp pasted in three places, ground that steps up like a wall, floors cut in straight lines, destinations
 * the roads never reach, roads that connect to nothing, a "landmark" nobody walks within sight of, npcs standing in
 * rock, and objects offering options that do nothing.
 *
 * Every check reports through the area's WorldLint, so findings carry the generator line behind them, can be accepted
 * with a reason like any other, and refuse the world at the gate.
 */
final class DesignLint {

	/** A walkable step this many height levels (8 units each) between neighbouring tiles reads as climbing a wall. */
	static final int CLIFF = 14;
	/** A tile this many levels above or below all four neighbours is a spike or a pit, not terrain. */
	static final int SPIKE = 7;
	/**
	 * A floor boundary (underlay or decorative overlay) running dead straight along the grid this far was cut by a
	 * rectangle. Slanted runs get more rope: overlay smoothing turns every staircase into a clean diagonal, and the client
	 * blends underlays across theirs; only a long one is a tell (and underlay slants not at all).
	 */
	static final int STRAIGHT_FLOOR = 14, SLANTED_FLOOR = 22;

	final WorldLint l;

	DesignLint(WorldLint l) {
		this.l = l;
	}

	/* --------------------------------------------------------------- terrain */

	int rawHeight(int x, int y) {
		MapRegion r = l.region(x, y);
		return r == null ? Integer.MIN_VALUE : r.height[0][x & 63][y & 63];
	}

	boolean walkable(int x, int y) {
		MapRegion r = l.region(x, y);
		if (r == null || !l.in(x, y) || (r.settings[0][x & 63][y & 63] & MapRegion.BLOCKED) != 0 || l.water(x, y)) {
			return false;
		}
		for (WorldLint.Obj o : l.objectsAt(x, y)) {
			if (o.scenery() && o.solid) {
				return false;
			}
		}
		return true;
	}

	/** Steps the player can walk up that are really cliffs; single-tile spikes and pits. */
	void terrain() {
		List<int[]> cliffs = new ArrayList<>(), spikes = new ArrayList<>();
		for (int x = l.x0 + 1; x < l.x1; x++) {
			for (int y = l.y0 + 1; y < l.y1; y++) {
				int h = rawHeight(x, y);
				if (h < 0) {
					continue; // left to the client's own terrain
				}
				for (int[] d : new int[][] { { 1, 0 }, { 0, 1 } }) {
					int n = rawHeight(x + d[0], y + d[1]);
					if (n >= 0 && Math.abs(h - n) >= CLIFF && walkable(x, y) && walkable(x + d[0], y + d[1])) {
						cliffs.add(new int[] { x, y, Math.abs(h - n) });
					}
				}
				int above = 0, below = 0;
				for (int[] d : new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } }) {
					int n = rawHeight(x + d[0], y + d[1]);
					if (n < 0) {
						above = below = -9;
						break;
					}
					above += h - n >= SPIKE ? 1 : 0;
					below += n - h >= SPIKE ? 1 : 0;
				}
				if ((above == 4 || below == 4) && walkable(x, y)) { // (a sea stack or a cave wall is meant to stand up)
					spikes.add(new int[] { x, y });
				}
			}
		}
		for (int[] c : WorldLint.cluster(cliffs, 3)) {
			l.report("CLIFF-WALKABLE", c[0], c[1], "the player can walk straight up a step of " + c[2] * 8
					+ " units here: block the cliff (and give it a rock face) or grade the slope");
		}
		for (int[] c : WorldLint.cluster(spikes, 2)) {
			l.report("HEIGHT-SPIKE", c[0], c[1], "one tile sticks up or drops down from all its neighbours: smooth the heightmap here");
		}
	}

	/** Underlays and decorative overlays whose boundary runs ruler-straight: a rectangle or a region seam showing. */
	void floorEdges() {
		Map<Integer, Integer> under = new HashMap<>(), over = new HashMap<>();
		for (int x = l.x0; x <= l.x1; x++) {
			for (int y = l.y0; y <= l.y1; y++) {
				MapRegion r = l.region(x, y);
				if (r != null) {
					under.merge(r.underlay[0][x & 63][y & 63], 1, Integer::sum);
					int ov = r.overlay[0][x & 63][y & 63];
					if (ov != 0 && !WorldLint.PATH_FLOORS.contains(ov) && !WorldLint.WATER_FLOORS.contains(ov)
							&& !WorldLint.STRUCTURE_FLOORS.contains(ov)) {
						over.merge(ov, 1, Integer::sum);
					}
				}
			}
		}
		under.forEach((id, n) -> {
			if (n >= 40 && id != 0) {
				l.straightEdges((x, y) -> l.in(x, y) && l.region(x, y) != null && l.region(x, y).underlay[0][x & 63][y & 63] == id
						&& !l.water(x, y), "STRAIGHT-FLOOR-EDGE", STRAIGHT_FLOOR, Integer.MAX_VALUE);
			}
		});
		over.forEach((id, n) -> {
			if (n >= 20) {
				l.straightEdges((x, y) -> l.in(x, y) && l.overlay(x, y) == id, "STRAIGHT-FLOOR-EDGE", STRAIGHT_FLOOR, SLANTED_FLOOR);
			}
		});
	}

	/* ---------------------------------------------------------------- clones */

	/** Placed by our own code (not stock map, not a stamped copy of a stock building). */
	static boolean ours(WorldLint.Obj o) {
		return o.origin != null && !o.origin.equals(Origin.STOCK) && !o.origin.contains("stamp");
	}

	/** Things that are meant to line up or face one way: built runs, and things set against a wall or a road. */
	static boolean ordered(WorldLint.Obj o) {
		return o.wall() || o.is("fence", "wall", "door", "gate", "sign", "torch", "lamp", "pillar", "post", "column", "plank",
				"rail", "banner", "flag", "bridge", "jetty", "pier", "crop", "cabbage", "wheat", "potato", "onion", "flax", "grave",
				"tomb", "headstone", "pew", "bookcase", "shelf", "window", "stair", "ladder", "ward", "seal", "pedestal", "rib", "spine",
				"pillar", "statue", "skull rock");
	}

	void clones() {
		Map<Integer, List<WorldLint.Obj>> byId = new HashMap<>();
		for (WorldLint.Obj o : l.objects) {
			if (o.scenery() && ours(o)) {
				byId.computeIfAbsent(o.id, k -> new ArrayList<>()).add(o);
			}
		}
		for (List<WorldLint.Obj> group : byId.values()) {
			WorldLint.Obj first = group.get(0);
			if (ordered(first)) {
				continue;
			}
			// every one of them turned the same way
			if (group.size() >= 8) {
				int[] rots = new int[4];
				for (WorldLint.Obj o : group) {
					rots[o.rot & 3]++;
				}
				int most = Math.max(Math.max(rots[0], rots[1]), Math.max(rots[2], rots[3]));
				if (most >= group.size() * 0.9) {
					l.report("UNIFORM-ROTATION", first.x, first.y, "all " + group.size() + " " + first + " face the same way: turn each one at random");
				}
			}
			// ruled into rows: four or more at the same spacing along one line
			Set<Long> at = new HashSet<>();
			for (WorldLint.Obj o : group) {
				at.add(WorldLint.key(o.x, o.y));
			}
			Set<Long> reported = new HashSet<>();
			for (WorldLint.Obj a : group) {
				for (WorldLint.Obj b : group) {
					int dx = b.x - a.x, dy = b.y - a.y, len = Math.max(Math.abs(dx), Math.abs(dy));
					if (len < 2 || len > 8 || (dx < 0 || dx == 0 && dy < 0)) {
						continue;
					}
					if (at.contains(WorldLint.key(a.x - dx, a.y - dy))) {
						continue; // not the start of the row
					}
					int n = 1;
					while (at.contains(WorldLint.key(a.x + n * dx, a.y + n * dy))) {
						n++;
					}
					if (n < 4) {
						continue;
					}
					// a row only reads as ruled if it stands clear: four in line inside a thick scatter of the same thing
					// is chance, and nobody sees it
					int crowd = 0;
					for (WorldLint.Obj c : group) {
						double t = ((c.x - a.x) * dx + (c.y - a.y) * dy) / (double) (dx * dx + dy * dy);
						if (t < -0.5 || t > n - 0.5) {
							continue;
						}
						double px = a.x + t * dx - c.x, py = a.y + t * dy - c.y;
						boolean member = Math.abs(t - Math.round(t)) < 1e-9 && Math.hypot(px, py) < 1e-9;
						if (!member && Math.hypot(px, py) <= Math.max(2, len * 0.75)) {
							crowd++;
						}
					}
					if (crowd <= n / 2 && reported.add(WorldLint.key(a.x / 8, a.y / 8))) {
						l.report("GRID-PATTERN", a.x, a.y, first + " placed in a ruled row: " + n + " every " + len + " tiles from " + a.x + ","
								+ a.y + ": scatter them, or vary the spacing and offset");
					}
				}
			}
		}
		// the same arrangement pasted in more than one place
		Map<String, List<WorldLint.Obj>> layouts = new HashMap<>();
		for (WorldLint.Obj o : l.objects) {
			if (!o.scenery() || !ours(o) || o.is("tree", "bush", "fern", "grass", "rock", "plant", "fungus", "mushroom", "root", "flower", "weed", "reed", "daisy", "stump")) {
				continue;
			}
			List<String> parts = new ArrayList<>();
			for (int dx = -4; dx <= 4; dx++) {
				for (int dy = -4; dy <= 4; dy++) {
					for (WorldLint.Obj n : l.objectsAt(o.x + dx, o.y + dy)) {
						if (n.scenery() && ours(n) && n.x == o.x + dx && n.y == o.y + dy
								&& !n.is("tree", "bush", "fern", "grass", "rock", "plant", "fungus", "mushroom", "root", "flower", "weed", "reed", "daisy", "stump")) {
							parts.add(dx + "," + dy + ":" + n.id);
						}
					}
				}
			}
			if (parts.size() >= 4) {
				java.util.Collections.sort(parts);
				layouts.computeIfAbsent(String.join(" ", parts), k -> new ArrayList<>()).add(o);
			}
		}
		Set<Long> done = new HashSet<>();
		for (List<WorldLint.Obj> same : layouts.values()) {
			for (int i = 0; i < same.size(); i++) {
				for (int j = i + 1; j < same.size(); j++) {
					WorldLint.Obj a = same.get(i), b = same.get(j);
					if (Math.hypot(a.x - b.x, a.y - b.y) >= 10 && done.add(WorldLint.key(b.x / 12, b.y / 12) * 31 + WorldLint.key(a.x / 12, a.y / 12))) {
						l.report("COPY-PASTE", b.x, b.y, "the same arrangement of objects as at " + a.x + "," + a.y
								+ ": each place should be composed for itself (vary the pieces, their spacing and their turn)");
					}
				}
			}
		}
	}

	/* --------------------------------------------------------------- network */

	boolean pathLike(int x, int y) {
		return l.path(x, y) || l.in(x, y) && WorldLint.STRUCTURE_FLOORS.contains(l.overlay(x, y));
	}

	/**
	 * The path network as the player meets it: path and built floor, joined across gaps of a tile or two (a doorstep, a
	 * jetty's end). Every declared destination must sit on (or just off) the part that reaches an arrival, and no road may
	 * float on its own.
	 */
	void network(int[][] arrivals, List<WorldAreas.Spot> spots) {
		int w = l.x1 - l.x0 + 1, h = l.y1 - l.y0 + 1;
		int[][] comp = new int[w][h];
		List<Integer> sizes = new ArrayList<>();
		sizes.add(0);
		List<int[]> mids = new ArrayList<>();
		mids.add(null);
		for (int x = l.x0; x <= l.x1; x++) {
			for (int y = l.y0; y <= l.y1; y++) {
				if (!pathLike(x, y) || comp[x - l.x0][y - l.y0] != 0) {
					continue;
				}
				int id = sizes.size(), n = 0;
				List<int[]> tiles = new ArrayList<>();
				ArrayDeque<int[]> q = new ArrayDeque<>();
				comp[x - l.x0][y - l.y0] = id;
				q.add(new int[] { x, y });
				while (!q.isEmpty()) {
					int[] p = q.poll();
					n++;
					tiles.add(p);
					for (int dx = -2; dx <= 2; dx++) {
						for (int dy = -2; dy <= 2; dy++) {
							int a = p[0] + dx, b = p[1] + dy;
							if (l.in(a, b) && pathLike(a, b) && comp[a - l.x0][b - l.y0] == 0) {
								comp[a - l.x0][b - l.y0] = id;
								q.add(new int[] { a, b });
							}
						}
					}
				}
				sizes.add(n);
				mids.add(tiles.get(tiles.size() / 2));
			}
		}
		if (sizes.size() == 1) {
			return; // no paths at all (a cave): reach and must-reach cover it
		}
		Set<Integer> main = new HashSet<>();
		for (int[] a : arrivals) {
			int c = firstComponentWalked(comp, a[0], a[1], 40);
			if (c > 0) {
				main.add(c);
			}
		}
		for (WorldAreas.Spot s : spots) {
			// served if any path within reach of it is part of the network (a stamped doorstep nearer by doesn't count against it)
			int c = 0;
			for (int dx = -6; dx <= 6 && !main.contains(c); dx++) {
				for (int dy = -6; dy <= 6 && !main.contains(c); dy++) {
					int a = s.x + dx, b = s.y + dy;
					if (l.in(a, b) && comp[a - l.x0][b - l.y0] != 0 && (c == 0 || main.contains(comp[a - l.x0][b - l.y0]))) {
						c = comp[a - l.x0][b - l.y0];
					}
				}
			}
			if (c == 0) {
				l.report("UNSERVED", s.x, s.y, s.name + " has no path within 6 tiles: lead the path network to it");
			} else if (!main.contains(c)) {
				l.report("UNSERVED", s.x, s.y, s.name + " is on a path that never joins the one from the arrival: connect them");
			}
		}
		for (int c = 1; c < sizes.size(); c++) {
			if (!main.contains(c) && sizes.get(c) >= 8) {
				int[] m = mids.get(c);
				l.report("PATH-ISLAND", m[0], m[1], "a " + sizes.get(c) + "-tile path that joins nothing the player arrives by: connect it or remove it");
			}
		}
	}

	/** The first part of the path network the player walks onto from (x, y), within {@code max} steps. */
	int firstComponentWalked(int[][] comp, int x, int y, int max) {
		int w = l.x1 - l.x0 + 1, h = l.y1 - l.y0 + 1;
		int[][] dist = new int[w][h];
		ArrayDeque<int[]> q = new ArrayDeque<>();
		q.add(new int[] { x, y });
		dist[x - l.x0][y - l.y0] = 1;
		while (!q.isEmpty()) {
			int[] p = q.poll();
			if (comp[p[0] - l.x0][p[1] - l.y0] != 0) {
				return comp[p[0] - l.x0][p[1] - l.y0];
			}
			if (dist[p[0] - l.x0][p[1] - l.y0] > max) {
				continue;
			}
			for (int[] d : new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } }) {
				int a = p[0] + d[0], b = p[1] + d[1];
				if (l.in(a, b) && dist[a - l.x0][b - l.y0] == 0 && (walkable(a, b) || pathLike(a, b))) {
					dist[a - l.x0][b - l.y0] = dist[p[0] - l.x0][p[1] - l.y0] + 1;
					q.add(new int[] { a, b });
				}
			}
		}
		return 0;
	}

	int nearestComponent(int[][] comp, int x, int y, int r) {
		for (int d = 0; d <= r; d++) {
			for (int dx = -d; dx <= d; dx++) {
				for (int dy = -d; dy <= d; dy++) {
					if (Math.max(Math.abs(dx), Math.abs(dy)) == d && l.in(x + dx, y + dy) && comp[x + dx - l.x0][y + dy - l.y0] != 0) {
						return comp[x + dx - l.x0][y + dy - l.y0];
					}
				}
			}
		}
		return 0;
	}

	/* -------------------------------------------------------------- landmark */

	/** The landmark the brief promises must exist, and the player's way through the area must pass within sight of it. */
	void landmark(WorldAreas.Area area) {
		if (area.landmarkX < 0) {
			l.report("LANDMARK-UNPLACED", area.fromX, area.fromY, area.name + " doesn't say where its landmark stands: give it landmarkAt(x, y)");
			return;
		}
		int lx = area.landmarkX, ly = area.landmarkY;
		List<int[]> boxes = new ArrayList<>();
		for (int dx = -2; dx <= 2; dx++) {
			for (int dy = -2; dy <= 2; dy++) {
				for (WorldLint.Obj o : l.objectsAt(lx + dx, ly + dy)) {
					if (o.scenery() && ours(o)) {
						boxes.add(new int[] { o.x, o.y, o.x + Math.max(1, o.sx) - 1, o.y + Math.max(1, o.sy) - 1 });
					}
				}
			}
		}
		if (boxes.isEmpty()) {
			l.report("LANDMARK-MISSING", lx, ly, "the brief's landmark (" + area.brief.landmark + ") isn't here: nothing of ours stands within 2 tiles");
			return;
		}
		boolean anyPath = false;
		double best = Double.MAX_VALUE;
		for (int x = l.x0; x <= l.x1; x++) {
			for (int y = l.y0; y <= l.y1; y++) {
				boolean route = pathLike(x, y);
				anyPath |= route;
				if (!route || l.reached == null || !l.reached[x - l.x0][y - l.y0]) {
					continue;
				}
				for (int[] b : boxes) {
					best = Math.min(best, Math.hypot(Math.max(0, Math.max(b[0] - x, x - b[2])), Math.max(0, Math.max(b[1] - y, y - b[3]))));
				}
			}
		}
		if (anyPath && best > 20) {
			l.report("LANDMARK-UNSEEN", lx, ly, "no walkable path comes within 20 tiles of the landmark ("
					+ (best == Double.MAX_VALUE ? "none" : (int) best + " at best") + "): route a path past it, or it's a landmark nobody sees");
		}
	}

	/* ------------------------------------------------------------------ npcs */

	/** Npcs standing inside rock, walls or water, or where the player can't walk up to them. */
	void npcs(List<int[]> spawns, ConfigDefinitions.Table npcDefs) {
		for (int[] s : spawns) {
			int id = s[0], x = s[1], y = s[2];
			if (!l.in(x, y)) {
				continue;
			}
			String name = null;
			try {
				name = id < npcDefs.size() ? npcDefs.decode(id).text(2) : null;
			} catch (RuntimeException e) {
				// unreadable definition: judge it by id
			}
			String who = (name == null ? "npc" : "'" + name + "'") + " (" + id + ")";
			String n = name == null ? "" : name.toLowerCase();
			if (n.contains("rift") || n.contains("portal")) {
				continue; // rifts hang in the air: that's what they are
			}
			// fishing spots stand in the water, and ducks and swans swim on it: for them only solid things count
			boolean swims = n.contains("spot") || n.contains("duck") || n.contains("swan");
			if (swims ? solidAt(x, y) : !walkable(x, y)) {
				l.report("NPC-STUCK", x, y, who + " spawns inside " + (solidAt(x, y) ? "something solid" : l.water(x, y) ? "water" : "blocked ground")
						+ ": move the spawn onto open ground (and check the placer looks at whole footprints)");
			} else if (!swims && l.reached != null && !l.reachable(x, y)) {
				l.report("NPC-UNREACHABLE", x, y, who + " spawns where the player can't walk to it");
			}
		}
	}

	boolean solidAt(int x, int y) {
		for (WorldLint.Obj o : l.objectsAt(x, y)) {
			if (o.scenery() && o.solid) {
				return true;
			}
		}
		return false;
	}

	/* ---------------------------------------------------------- dead options */

	/**
	 * Our own objects that offer an option ("Search", "Open", "Enter") nothing in the server answers: a dead click is the
	 * surest sign of a prop dropped in without thought. An option counts as handled if game code names the object's
	 * constant or its id.
	 */
	static List<String> deadOptions(Map<Integer, MapRegion> world, ConfigDefinitions.Table locs, int firstCustomId) throws Exception {
		Set<Integer> placed = new HashSet<>();
		for (MapRegion r : world.values()) {
			for (MapRegion.MapObject o : r.objects) {
				if (o.id >= firstCustomId) {
					placed.add(o.id);
				}
			}
		}
		Map<Integer, List<String>> names = new HashMap<>();
		for (Class<?> c : new Class<?>[] { com.rs2.game.content.voidrealm.VoidConstants.class,
				com.rs2.game.content.skullbrine.SkullbrineConstants.class }) {
			for (java.lang.reflect.Field f : c.getFields()) {
				if (f.getType() == int.class && java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
					names.computeIfAbsent(f.getInt(null), k -> new ArrayList<>()).add(f.getName());
				}
			}
		}
		StringBuilder game = new StringBuilder();
		java.nio.file.Path src = java.nio.file.Paths.get("src/main/java/com/rs2/game");
		if (!java.nio.file.Files.isDirectory(src)) {
			return List.of();
		}
		try (java.util.stream.Stream<java.nio.file.Path> files = java.nio.file.Files.walk(src)) {
			for (java.nio.file.Path p : (Iterable<java.nio.file.Path>) files::iterator) {
				String f = p.getFileName().toString();
				if (f.endsWith(".java") && !f.equals("VoidConstants.java") && !f.equals("SkullbrineConstants.java")) {
					game.append(new String(java.nio.file.Files.readAllBytes(p), java.nio.charset.StandardCharsets.UTF_8)).append('\n');
				}
			}
		}
		String code = game.toString();
		List<String> dead = new ArrayList<>();
		for (int id : new java.util.TreeSet<>(placed)) {
			if (id >= locs.size()) {
				continue;
			}
			ConfigDefinitions.Definition d = locs.decode(id);
			List<String> ops = new ArrayList<>();
			for (int op = 30; op < 35; op++) {
				String t = d.text(op);
				if (t != null && !t.equalsIgnoreCase("hidden")) {
					ops.add(t);
				}
			}
			if (ops.isEmpty()) {
				continue;
			}
			boolean handled = java.util.regex.Pattern.compile("\\b" + id + "\\b").matcher(code).find();
			for (String n : names.getOrDefault(id, List.of())) {
				handled |= java.util.regex.Pattern.compile("\\b" + n + "\\b").matcher(code).find();
			}
			if (!handled) {
				dead.add(String.format("%-18s object %d '%s' offers %s but no game code handles it%s: take the option off its definition, or handle it",
						"DEAD-OPTION", id, d.text(2), ops, names.containsKey(id) ? " (" + String.join("/", names.get(id)) + " is never used)" : ""));
			}
		}
		return dead;
	}
}
