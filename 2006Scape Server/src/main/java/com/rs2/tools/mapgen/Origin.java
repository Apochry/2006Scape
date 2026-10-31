package com.rs2.tools.mapgen;

import java.util.stream.Collectors;

/**
 * Where in the generators something was made: the nearest few map-generator frames on the stack, newest first, e.g.
 * "VoidRealmGenerator.tent:1360 < camps:1430 < nythera:1012". Map objects and roads carry one, so the world gate can say
 * which line of code produced each finding instead of leaving you to hunt for it by coordinates.
 */
final class Origin {

	static final String STOCK = "stock map";
	private static final StackWalker WALKER = StackWalker.getInstance();
	private static final String PACKAGE = Origin.class.getPackageName() + ".";

	private Origin() {
	}

	/** A road as the generator asked for it: its waypoints and the code that asked. */
	static final class Road {
		final int[][] points;
		final String origin;

		Road(int[][] points, String origin) {
			this.points = points;
			this.origin = origin;
		}
	}

	static final java.util.List<Road> ROADS = new java.util.ArrayList<>();

	/** Who painted each path tile (world x << 16 | y), so a path finding names the code behind that very tile. */
	static final java.util.Map<Integer, String> PATH_TILES = new java.util.HashMap<>();

	static void pathTile(int x, int y) {
		PATH_TILES.put(x << 16 | y, here());
	}

	static String pathTileNear(int x, int y) {
		for (int r = 0; r <= 1; r++) {
			for (int dx = -r; dx <= r; dx++) {
				for (int dy = -r; dy <= r; dy++) {
					String o = PATH_TILES.get((x + dx) << 16 | (y + dy));
					if (o != null) {
						return o;
					}
				}
			}
		}
		return null;
	}

	/** Records a road the generator is painting, so path findings can name the call that laid it. */
	static void road(int[][] points) {
		ROADS.add(new Road(points, here()));
	}

	/** The code behind the road passing closest to (x, y), if one passes within r tiles. */
	static String roadNear(int x, int y, double r) {
		String best = null;
		double bestD = r;
		for (Road road : ROADS) {
			for (int i = 0; i + 1 < road.points.length; i++) {
				double ax = road.points[i][0], ay = road.points[i][1], bx = road.points[i + 1][0], by = road.points[i + 1][1];
				double len = (bx - ax) * (bx - ax) + (by - ay) * (by - ay);
				double t = len == 0 ? 0 : Math.max(0, Math.min(1, ((x - ax) * (bx - ax) + (y - ay) * (by - ay)) / len));
				double d = Math.hypot(x - (ax + t * (bx - ax)), y - (ay + t * (by - ay)));
				if (d <= bestD) {
					bestD = d;
					best = road.origin;
				}
			}
		}
		return best;
	}

	static String here() {
		return WALKER.walk(frames -> frames
				.filter(f -> f.getClassName().startsWith(PACKAGE) && !f.getClassName().endsWith(".MapRegion")
						&& !f.getClassName().endsWith(".Origin") && !f.getClassName().endsWith(".MapRegion$MapObject"))
				.limit(3).map(new java.util.function.Function<StackWalker.StackFrame, String>() {
					String lastClass;

					@Override
					public String apply(StackWalker.StackFrame f) {
						String c = f.getClassName().substring(f.getClassName().lastIndexOf('.') + 1);
						c = c.contains("$") ? c.substring(0, c.indexOf('$')) : c;
						String s = (c.equals(lastClass) ? "" : c + ".") + f.getMethodName() + ":" + f.getLineNumber();
						lastClass = c;
						return s;
					}
				}).collect(Collectors.joining(" < ")));
	}
}
