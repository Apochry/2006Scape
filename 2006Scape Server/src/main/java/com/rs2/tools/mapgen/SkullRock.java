package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.CursedIslandGenerator.FACE_DEPTH;
import static com.rs2.tools.mapgen.CursedIslandGenerator.FACE_Y0;
import static com.rs2.tools.mapgen.CursedIslandGenerator.SKX;
import static com.rs2.tools.mapgen.ModelData.hsl;

import java.util.ArrayList;
import java.util.List;

/**
 * The sculpted face of Skull Rock: a hand-built model laid over the terrain skull that
 * {@link CursedIslandGenerator} raises out of the island. The terrain gives it its bulk and keeps it walkable (the face
 * and crown can be climbed to the eye fires and the flag); this gives it the bones that make it read as a skull from the
 * ground - brow ridges arched over black eye sockets, the nasal cavity, cheekbones and their arches, a row of great
 * square teeth over the cave mouth with the lower teeth below it, and the jaw's two arms reaching down either side of
 * the plaza.
 *
 * Everything is built in world-tile terms and then dropped onto the real ground: {@link #at} samples the finished
 * heightmap under every vertex, so a ridge follows the slope it lies on. The client can't draw one model this big (see
 * {@link #MAX_DEPTH_SPAN}), so {@link #pieces} cuts the finished face into a few dozen small objects, each non-solid
 * and non-interactive, each standing on the mean height of its anchor tile's corners and centred on its footprint, as
 * the client places them.
 */
final class SkullRock {

	/** The area the face is cut up over: south-west tile and size. It covers the jaw arms, the face and the brow. */
	static final int AX = SKX - 16, AY = FACE_Y0 - 11, SX = 32, SY = 34;
	/** Object ids reserved for the pieces (from {@code VoidConstants.SKULL_ROCK}); the generator fails if it needs more. */
	static final int MAX_PIECES = 48;
	/**
	 * The client depth-sorts a model's faces into 1500 buckets spanning its bounding sphere (Model.method483) and silently
	 * draws nothing if the model is any bigger. Every piece is kept comfortably under that.
	 */
	private static final int MAX_DEPTH_SPAN = 1350;

	static final int BONE = hsl(7, 2, 80), BONE_SHADE = hsl(7, 2, 70), BONE_OLD = hsl(8, 2, 62), BONE_DARK = hsl(7, 1, 44),
			HOLLOW = hsl(7, 1, 22), HOLLOW_DEEP = hsl(6, 1, 9), TOOTH = hsl(8, 2, 86), TOOTH_ROOT = hsl(8, 3, 66);

	/** One piece of the face: an object's anchor tile and footprint, and its model (already placed for that anchor). */
	static final class Piece {
		final int x, y, sizeX, sizeY;
		final ModelData model;

		Piece(int x, int y, int sizeX, int sizeY, ModelData model) {
			this.x = x;
			this.y = y;
			this.sizeX = sizeX;
			this.sizeY = sizeY;
			this.model = model;
		}
	}

	private final CursedIslandGenerator g;
	/** The whole face, in world units: x and z are tile * 128, y is the height (up is negative, as in the client). */
	private final ModelData m = new ModelData();

	SkullRock(CursedIslandGenerator g) {
		this.g = g;
	}

	/** A corner's height in client units (the client reads a height of 1 as 0). */
	private double h(int x, int y) {
		int v = g.heightAt(x, y);
		return 8 * (v == 1 ? 0 : Math.max(0, v));
	}

	/** The ground's height at a point (world tile coordinates, corners at whole numbers), as the client interpolates it. */
	private double ground(double wx, double wy) {
		int x = (int) Math.floor(wx), y = (int) Math.floor(wy);
		double fx = wx - x, fy = wy - y;
		double a = h(x, y) * (1 - fx) + h(x + 1, y) * fx, b = h(x, y + 1) * (1 - fx) + h(x + 1, y + 1) * fx;
		return a * (1 - fy) + b * fy;
	}

	/**
	 * A point on the face, in world units: {@code u} tiles east of the skull's centre line, {@code t} tiles north of the
	 * foot of its face, {@code lift} units above the ground there.
	 */
	double[] at(double u, double t, double lift) {
		double wx = SKX + u, wy = FACE_Y0 + t + 0.5;
		return new double[] { wx * 128, -ground(wx, wy) - lift, wy * 128 };
	}

	/**
	 * A bone running through face points {u, t, lift, radius}: one continuous, smoothly shaded tube that follows the
	 * ground it lies on, with rounded ends. Long spans are cut short, so the bone hugs the slope and no face is too big for
	 * the piece of the model it lands in.
	 */
	private void bone(double[][] pts, int sides, int col, int col2) {
		List<double[]> c = new ArrayList<>(); // u, t, lift, radius
		for (int i = 0; i + 1 < pts.length; i++) {
			double[] p = pts[i], q = pts[i + 1];
			int n = Math.max(1, (int) Math.ceil(Math.hypot(q[0] - p[0], q[1] - p[1]) / 0.7));
			for (int k = 0; k < n; k++) {
				double s0 = k / (double) n;
				c.add(new double[] { lerp(p[0], q[0], s0), lerp(p[1], q[1], s0), lerp(p[2], q[2], s0), lerp(p[3], q[3], s0) });
			}
		}
		c.add(pts[pts.length - 1].clone());
		for (double[] q : c) { // half sunk into the rock: a bone that hovers over the ground reads as a stick lying on it
			q[2] = Math.min(q[2], q[3] * 0.45);
		}
		int n = c.size();
		double[][] w = new double[n][];
		for (int i = 0; i < n; i++) {
			w[i] = at(c.get(i)[0], c.get(i)[1], c.get(i)[2]);
		}
		int shading = m.shading;
		m.shading = 0; // smooth: a bone, not a stack of blocks
		int[][] ring = new int[n][sides];
		double[] normal = null, tangent = null;
		for (int i = 0; i < n; i++) {
			double[] a = w[Math.max(0, i - 1)], b = w[Math.min(n - 1, i + 1)];
			tangent = ModelData.norm(new double[] { b[0] - a[0], b[1] - a[1], b[2] - a[2] });
			if (normal == null) {
				double[] up = Math.abs(tangent[1]) > 0.9 ? new double[] { 1, 0, 0 } : new double[] { 0, 1, 0 };
				normal = ModelData.norm(ModelData.cross(tangent, up));
			} else { // carried along the bone, so its sides don't twist
				double d = normal[0] * tangent[0] + normal[1] * tangent[1] + normal[2] * tangent[2];
				normal = ModelData.norm(new double[] { normal[0] - d * tangent[0], normal[1] - d * tangent[1], normal[2] - d * tangent[2] });
			}
			double[] bi = ModelData.cross(tangent, normal);
			double r = c.get(i)[3];
			for (int k = 0; k < sides; k++) {
				double ang = Math.PI * 2 * k / sides, cs = Math.cos(ang) * r, sn = Math.sin(ang) * r;
				ring[i][k] = m.vertex(w[i][0] + normal[0] * cs + bi[0] * sn, w[i][1] + normal[1] * cs + bi[1] * sn,
						w[i][2] + normal[2] * cs + bi[2] * sn);
			}
		}
		for (int i = 0; i + 1 < n; i++) {
			double[] mid = { (w[i][0] + w[i + 1][0]) / 2, (w[i][1] + w[i + 1][1]) / 2, (w[i][2] + w[i + 1][2]) / 2 };
			for (int k = 0; k < sides; k++) {
				int k2 = (k + 1) % sides;
				m.quadOut(ring[i][k], ring[i][k2], ring[i + 1][k2], ring[i + 1][k], i % 5 == 4 ? col2 : col, mid);
			}
		}
		// rounded ends: each closes to a point a little beyond its last ring
		for (int e = 0; e < 2; e++) {
			int i = e == 0 ? 0 : n - 1, j = e == 0 ? 1 : n - 2;
			double[] d = ModelData.norm(new double[] { w[i][0] - w[j][0], w[i][1] - w[j][1], w[i][2] - w[j][2] });
			double r = c.get(i)[3] * 0.7;
			int tip = m.vertex(w[i][0] + d[0] * r, w[i][1] + d[1] * r, w[i][2] + d[2] * r);
			for (int k = 0; k < sides; k++) {
				m.triOut(ring[i][k], ring[i][(k + 1) % sides], tip, col, w[i]);
			}
		}
		m.shading = shading;
	}

	private static double lerp(double a, double b, double s) {
		return a + (b - a) * s;
	}

	/** Points round an ellipse on the face, from angle a0 to a1 (radians, 0 = east, pi/2 = north = up the face). */
	private static double[][] arc(double u0, double t0, double ru, double rt, double a0, double a1, int n,
			java.util.function.DoubleUnaryOperator lift, java.util.function.DoubleUnaryOperator radius) {
		double[][] p = new double[n + 1][];
		for (int i = 0; i <= n; i++) {
			double a = a0 + (a1 - a0) * i / n, s = i / (double) n;
			p[i] = new double[] { u0 + Math.cos(a) * ru, t0 + Math.sin(a) * rt, lift.applyAsDouble(s), radius.applyAsDouble(s) };
		}
		return p;
	}

	/** A hollow: rings of an outline sinking towards a centre, wound to face up out of it, darkening as they go down. */
	private void hollow(double[][] outline, double cu, double ct, double rimLift, double bottomLift, int[] colors) {
		int rings = colors.length;
		int[][] idx = new int[rings + 1][outline.length];
		for (int r = 0; r <= rings; r++) {
			double s = 1 - r / (double) (rings + 0.6); // never quite closes: the pool or the dark shows at the bottom
			for (int k = 0; k < outline.length; k++) {
				double u = cu + (outline[k][0] - cu) * s, t = ct + (outline[k][1] - ct) * s;
				double[] p = at(u, t, rimLift + (bottomLift - rimLift) * r / rings);
				idx[r][k] = m.vertex(p[0], p[1], p[2]);
			}
		}
		double[] below = at(cu, ct, -4000);
		for (int r = 0; r < rings; r++) {
			for (int k = 0; k < outline.length; k++) {
				int k2 = (k + 1) % outline.length;
				m.quadOut(idx[r][k], idx[r][k2], idx[r + 1][k2], idx[r + 1][k], colors[r], below);
			}
		}
	}

	/** Sculpts the whole face into {@link #m}, in world units. */
	private void sculpt() {
		double eyeT = 0.62 * FACE_DEPTH, ru = 3.6, rt = 0.17 * FACE_DEPTH;
		for (int side = -1; side <= 1; side += 2) {
			double u0 = side * 6;
			// the socket: a dark funnel from the rim down to the pool of cursed water at its bottom
			double[][] rim = arc(u0, eyeT, ru * 1.02, rt * 1.02, 0, Math.PI * 2 * 23 / 24, 23, s -> 0, s -> 0);
			hollow(rim, u0, eyeT, 6, 2, new int[] { HOLLOW, HOLLOW_DEEP, HOLLOW_DEEP });
			// the brow: a heavy ridge arched over the top of the socket, thickest above it, overhanging it a little
			double a0 = side > 0 ? -0.15 : Math.PI + 0.15, a1 = side > 0 ? Math.PI + 0.35 : -0.35;
			bone(arc(u0, eyeT + 0.2, ru * 1.05, rt * 1.08, a0, a1, 12, s -> 30 + 62 * Math.sin(s * Math.PI),
					s -> 34 + 40 * Math.sin(s * Math.PI)), 6, BONE, BONE_SHADE);
			// the lower rim, thinner, down the cheek side
			double b0 = side > 0 ? Math.PI + 0.35 : -0.35, b1 = side > 0 ? 2 * Math.PI - 0.15 : -Math.PI + 0.15;
			bone(arc(u0, eyeT, ru * 1.06, rt * 1.1, b0, b1, 8, s -> 22 + 10 * Math.sin(s * Math.PI), s -> 22 + 8 * Math.sin(s * Math.PI)),
					5, BONE_SHADE, BONE);
			// the cheekbone, and its arch running back along the side of the skull to the temple
			bone(new double[][] { { side * 8.4, 6.8, 20, 30 }, { side * 10.6, 7.6, 52, 64 }, { side * 12.2, 9.0, 46, 46 },
					{ side * 13.4, 11.6, 34, 34 }, { side * 14.0, 14.4, 22, 22 } }, 6, BONE, BONE_OLD);
			// the jaw's arm: from the hinge below the cheekbone down and out round the plaza, ending in a broken stub
			bone(new double[][] { { side * 12.6, 6.0, 30, 46 }, { side * 13.4, 3.0, 40, 54 }, { side * 13.6, -0.5, 44, 54 },
					{ side * 13.2, -4.0, 36, 50 }, { side * 12.2, -7.0, 24, 44 }, { side * 11.2, -8.6, 12, 34 } }, 6, BONE_SHADE,
					BONE);
		}
		// the glabella: the brows meet over the bridge of the nose
		bone(new double[][] { { -2.6, eyeT + 3.0, 74, 50 }, { 0, eyeT + 2.4, 84, 58 }, { 2.6, eyeT + 3.0, 74, 50 } }, 6, BONE, BONE_SHADE);
		// the nasal cavity: an inverted heart, bone ridges down its sides and a little spine at its foot
		double top = 9.6, bot = 5.6, half = 2.4;
		double[][] nose = { { 0, top }, { half * 0.45, top - 1.4 }, { half * 0.85, bot + 1.0 }, { half, bot }, { half * 0.4, bot - 0.2 },
				{ 0, bot + 0.5 }, { -half * 0.4, bot - 0.2 }, { -half, bot }, { -half * 0.85, bot + 1.0 }, { -half * 0.45, top - 1.4 } };
		hollow(nose, 0, (top + bot) / 2, 6, 2, new int[] { HOLLOW, HOLLOW_DEEP });
		for (int side = -1; side <= 1; side += 2) {
			bone(new double[][] { { side * 0.35, top + 0.6, 40, 22 }, { side * half * 0.6, top - 1.4, 30, 22 },
					{ side * half * 1.05, bot + 0.6, 24, 24 }, { side * half * 0.75, bot - 0.6, 18, 18 } }, 5, BONE, BONE_SHADE);
		}
		bone(new double[][] { { 0, bot + 0.3, 14, 14 }, { 0, bot - 0.5, 22, 6 } }, 4, BONE_SHADE, BONE);
		// the upper jaw: a gum ridge across the face above the mouth, and the teeth set in it, a gap in the middle where
		// the cave mouth opens between the front teeth. Each tooth reaches right down to the ground: one that ends in the
		// air reads as a peg stuck on the face.
		double gumT = 0.15 * FACE_DEPTH + 0.6;
		bone(new double[][] { { -9.4, gumT + 0.8, 18, 30 }, { -5, gumT + 0.2, 26, 40 }, { -0.5, gumT, 30, 44 }, { 4, gumT + 0.2, 26, 40 },
				{ 8.4, gumT + 0.8, 18, 30 } }, 6, BONE_SHADE, BONE);
		double mid = -0.5; // the cave entrance's centre
		double[] offs = { 1.9, 3.2, 4.5, 5.8, 7.1, 8.3 };
		for (int side = -1; side <= 1; side += 2) {
			for (int i = 0; i < offs.length; i++) {
				if (side > 0 && i == 2) {
					continue; // one knocked out
				}
				double u = mid + side * offs[i];
				double len = i == 0 ? 2.2 : i == 1 ? 2.4 : i < 4 ? 1.8 : 1.5; // the canines (second from the gap) longest
				double w = i < 2 ? 46 : 52;
				boolean broken = side < 0 && i == 3;
				double[] root = at(u, gumT - 0.1, 22), tip = at(u + side * 0.05, gumT - (broken ? len * 0.5 : len), broken ? 12 : 4);
				if (i == 1 && !broken) { // the canine: round and closing to a point
					m.tube(root, tip, w * 0.9, 0, 6, 0, TOOTH, TOOTH_ROOT);
				} else {
					m.tube(root, tip, w, w * 0.62, 4, Math.PI / 4, i % 2 == 0 ? TOOTH : BONE, TOOTH_ROOT);
				}
			}
		}
		// the lower teeth, worn short, standing along the foot of the face below the mouth
		for (int side = -1; side <= 1; side += 2) {
			for (int i = 0; i < 5; i++) {
				if (side < 0 && i == 3) {
					continue;
				}
				double u = mid + side * (2.0 + i * 1.35);
				double[] root = at(u, -0.4, 6), tip = at(u, 0.5, 34 + (i == 0 ? 10 : 0));
				m.tube(root, tip, 40, 28, 4, Math.PI / 4, i % 2 == 0 ? BONE : TOOTH, TOOTH_ROOT);
			}
		}
		// a crack across the left brow and up towards the crown, its edges standing a little proud
		bone(new double[][] { { -9.2, 15.6, 8, 7 }, { -9.8, 17.4, 8, 6 }, { -10.6, 19.6, 6, 6 }, { -11.6, 22.0, 4, 5 } }, 3, BONE_DARK,
				BONE_DARK);
	}

	/**
	 * The face, cut into pieces small enough for the client to draw. Faces are dealt into square cells by their centres
	 * (a cell too big for one model is quartered again); each cell becomes one object whose footprint covers its
	 * vertices, so the client draws it once every tile under it has been laid.
	 */
	List<Piece> pieces() {
		sculpt();
		List<Piece> out = new ArrayList<>();
		List<Integer> all = new ArrayList<>();
		for (int f = 0; f < m.faces.size(); f++) {
			all.add(f);
		}
		cut(all, AX, AY, 6, out);
		if (out.size() > MAX_PIECES) {
			throw new IllegalStateException("Skull Rock needs " + out.size() + " pieces; only " + MAX_PIECES + " ids are reserved");
		}
		return out;
	}

	private void cut(List<Integer> faces, int x0, int y0, int size, List<Piece> out) {
		java.util.Map<Integer, List<Integer>> byCell = new java.util.TreeMap<>();
		for (int f : faces) {
			int[] fc = m.faces.get(f);
			double x = 0, z = 0;
			for (int v : fc) {
				x += m.vertices.get(v)[0] / 3.0;
				z += m.vertices.get(v)[2] / 3.0;
			}
			int cx = (int) Math.floor((x / 128 - x0) / size), cy = (int) Math.floor((z / 128 - y0) / size);
			byCell.computeIfAbsent(cy * 1000 + cx, k -> new ArrayList<>()).add(f);
		}
		for (java.util.Map.Entry<Integer, List<Integer>> e : byCell.entrySet()) {
			int cx = Math.floorMod(e.getKey(), 1000), cy = Math.floorDiv(e.getKey(), 1000);
			Piece p = piece(e.getValue());
			if (p == null && size > 1) {
				cut(e.getValue(), x0 + cx * size, y0 + cy * size, (size + 1) / 2, out);
			} else if (p == null) {
				throw new IllegalStateException("a single tile of Skull Rock is too big to draw");
			} else {
				out.add(p);
			}
		}
	}

	/** One piece from these faces, or null if it would be too big for the client to draw. */
	private Piece piece(List<Integer> faces) {
		java.util.Map<Integer, Integer> remap = new java.util.LinkedHashMap<>();
		double minX = 1e9, maxX = -1e9, minZ = 1e9, maxZ = -1e9;
		for (int f : faces) {
			for (int v : m.faces.get(f)) {
				if (remap.putIfAbsent(v, remap.size()) == null) {
					int[] p = m.vertices.get(v);
					minX = Math.min(minX, p[0]);
					maxX = Math.max(maxX, p[0]);
					minZ = Math.min(minZ, p[2]);
					maxZ = Math.max(maxZ, p[2]);
				}
			}
		}
		int ax = (int) Math.floor(minX / 128), ay = (int) Math.floor(minZ / 128);
		int sx = Math.max(1, (int) Math.ceil(maxX / 128) - ax), sy = Math.max(1, (int) Math.ceil(maxZ / 128) - ay);
		double ox = (ax + sx / 2.0) * 128, oz = (ay + sy / 2.0) * 128;
		double base = (h(ax, ay) + h(ax + 1, ay) + h(ax + 1, ay + 1) + h(ax, ay + 1)) / 4.0;
		ModelData d = new ModelData();
		double r2 = 0, up = 0, down = 0;
		for (int v : remap.keySet()) {
			int[] p = m.vertices.get(v);
			double x = p[0] - ox, y = p[1] + base, z = p[2] - oz;
			d.vertex(x, y, z);
			r2 = Math.max(r2, x * x + z * z);
			up = Math.max(up, -y);
			down = Math.max(down, y);
		}
		// the client's own measure of the model (Model.method466): its depth range runs over this many buckets
		double r = Math.ceil(Math.sqrt(r2));
		if (Math.sqrt(r * r + up * up) + Math.sqrt(r * r + down * down) + 2 > MAX_DEPTH_SPAN) {
			return null;
		}
		for (int f : faces) {
			int[] fc = m.faces.get(f);
			d.tri(remap.get(fc[0]), remap.get(fc[1]), remap.get(fc[2]), m.colors.get(f));
			d.priorities.set(d.priorities.size() - 1, m.priorities.get(f));
			d.renderTypes.set(d.renderTypes.size() - 1, m.renderTypes.get(f));
		}
		return new Piece(ax, ay, sx, sy, d);
	}
}
