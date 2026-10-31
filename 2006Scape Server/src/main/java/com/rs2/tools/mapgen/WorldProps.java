package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ModelData.hsl;

import java.util.Random;

/**
 * The furniture of a lived-in world, hand-built to replace stock pieces that never belonged out in the open: a canvas
 * tent with its poles, guys and pegs (the stock "tent" is a black slab); a ring of hearth stones for every campfire,
 * with a cooking tripod where someone actually cooks; flags that fly from real poles; a seat by the fire; the plain
 * standing stones of the old circle; and the Broken Gate itself - two gate towers and the arch that fell between them.
 *
 * Same rules as {@link ModelForge}: few polygons, chunky silhouettes, the cache's muted palette (wood h5/s6, stone
 * h10/s0, canvas h7/s2, moss h13/s3). 128 units to a tile; y is negative upwards; a model sits centred on its footprint.
 */
final class WorldProps {

	private final Random random = new Random(4242);

	/** Tiles between the Broken Gate's towers, centre to centre. */
	static final int GATE_SPAN = 7;

	static final int WOOD = hsl(5, 6, 30), WOOD_DARK = hsl(5, 5, 19), WOOD_RAW = hsl(6, 5, 46), ROPE = hsl(8, 3, 44),
			IRON = hsl(0, 0, 18), CANVAS = hsl(7, 2, 76), CANVAS_SHADE = hsl(7, 2, 66), CANVAS_DARK = hsl(7, 2, 54),
			CANVAS_PATCH = hsl(6, 3, 58), TENT_INSIDE = hsl(6, 3, 16), MOSS = hsl(13, 3, 26), MOSS_LIGHT = hsl(13, 3, 32);
	/** The Gate's dressed stone: darker and warmer than field stone, with more difference block to block. */
	static final int[] ASHLAR = { hsl(8, 1, 30), hsl(8, 1, 36), hsl(9, 1, 26), hsl(8, 1, 40), hsl(10, 0, 33), hsl(8, 1, 23) };
	static final int[] STONE = { hsl(10, 0, 44), hsl(10, 0, 38), hsl(10, 0, 50), hsl(10, 0, 34), hsl(10, 1, 41) };

	/** Both windings of a triangle, for thin things seen from either side (flags, flaps, banners). */
	static void twoSided(ModelData m, int a, int b, int c, int color) {
		m.tri(a, b, c, color);
		m.tri(a, c, b, color);
	}

	/** Sets the paint priority of every face added since {@code from} (higher paints later, over lower). */
	static void priority(ModelData m, int from, int priority) {
		for (int i = from; i < m.faces.size(); i++) {
			m.priorities.set(i, priority);
		}
	}

	static double[] p(double x, double y, double z) {
		return new double[] { x, y, z };
	}

	/* ====================================================================== */
	/*                               THE CAMPS                                */
	/* ====================================================================== */

	/**
	 * A canvas ridge tent on a 2x2 footprint: two poles and a sagging ridge, the door flaps tied back on the south end
	 * (-z) to show the dark inside, guy ropes pegged out front and back and the walls pegged down along both sides.
	 */
	ModelData campTent() {
		ModelData m = new ModelData();
		double W = 94, L = 100, H = 148; // half-width at the eaves, half-length, ridge height
		// the walls: each side in two panels, the middle of the ridge sagging and the middle of the eave billowing out
		int r0 = m.vertex(0, -H, -L), rm = m.vertex(0, -H + 7, 0), r1 = m.vertex(0, -H, L);
		double[] centre = { 0, -H * 0.4, 0 };
		for (int side = -1; side <= 1; side += 2) {
			int e0 = m.vertex(side * W, 0, -L), em = m.vertex(side * (W + 7), 0, 0), e1 = m.vertex(side * W, 0, L);
			int col = side < 0 ? CANVAS : CANVAS_SHADE;
			m.quadOut(e0, em, rm, r0, col, centre);
			m.quadOut(em, e1, r1, rm, side < 0 ? CANVAS_PATCH : col, centre); // a mended panel, a shade off
			// a hem of darker canvas along the bottom, where it's been dragged through the mud
			int h0 = m.vertex(side * (W + 1), -12, -L), hm = m.vertex(side * (W + 8), -12, 0), h1 = m.vertex(side * (W + 1), -12, L);
			int g0 = m.vertex(side * (W + 2), 1, -L), gm = m.vertex(side * (W + 9), 1, 0), g1 = m.vertex(side * (W + 2), 1, L);
			m.quadOut(g0, gm, hm, h0, CANVAS_DARK, centre);
			m.quadOut(gm, g1, h1, hm, CANVAS_DARK, centre);
		}
		// the back: closed
		int b0 = m.vertex(-W, 0, L), b1 = m.vertex(W, 0, L);
		m.triOut(b0, b1, r1, CANVAS_DARK, centre);
		// the front: a canvas end like the back, the open doorway a dark triangle laid just in front of it (so nothing can
		// sort in front of it), and the flaps tied back either side, further out still
		double dw = 40, dh = H * 0.78;
		int f0 = m.vertex(-W, 0, -L), f1 = m.vertex(W, 0, -L);
		m.triOut(f0, f1, r0, CANVAS_SHADE, centre);
		// (the client paints a model's faces far to near by their centres, and the end wall's centre sits higher - so
		// nearer the camera - than the doorway's: face priorities make the doorway, then the flaps and front pole, paint
		// over it)
		int doorFaces = m.faces.size();
		int da = m.vertex(-dw, 0, -L - 2), db = m.vertex(dw, 0, -L - 2), dt = m.vertex(0, -dh, -L - 2);
		m.triOut(da, db, dt, TENT_INSIDE, p(0, -40, 0));
		int sa = m.vertex(-dw * 0.55, 0, -L - 2.5), sb = m.vertex(dw * 0.55, 0, -L - 2.5), st = m.vertex(0, -dh * 0.5, -L - 2.5);
		m.triOut(sa, sb, st, hsl(6, 3, 9), p(0, -40, 0)); // deeper in, darker still
		priority(m, doorFaces, 1);
		int flapFaces = m.faces.size();
		for (int side = -1; side <= 1; side += 2) {
			int fa = m.vertex(side * dw, 0, -L - 4), ft = m.vertex(side * 4, -dh + 4, -L - 4),
					fo = m.vertex(side * (dw + 30), -dh * 0.32, -L - 16);
			twoSided(m, fa, ft, fo, CANVAS_DARK);
			m.tube(p(side * (dw + 20), -dh * 0.36, -L - 12), p(side * (dw + 22), -dh * 0.24, -L - 14), 3, 3, 4, 0, ROPE, ROPE);
		}
		priority(m, flapFaces, 2);
		// poles standing proud of the ridge at each end, and a ridge pole showing between them
		for (int end = -1; end <= 1; end += 2) {
			double z = end * (L + 3);
			int poleFaces = m.faces.size();
			m.tube(p(0, 0, z), p(0, -H - 16, z), 4, 3.4, 4, Math.PI / 4, WOOD, WOOD_DARK);
			if (end < 0) {
				priority(m, poleFaces, 2); // the front pole stands in the doorway
			}
			// a guy rope out to a peg
			double pz = end * (L + 58);
			m.tube(p(0, -H - 10, z), p(0, -6, pz), 1.3, 1.3, 3, 0, ROPE, ROPE);
			m.tube(p(0, 4, pz), p(0, -10, pz + end * 2), 3.4, 2.4, 4, 0, WOOD_RAW, WOOD_RAW);
		}
		m.tube(p(0, -H - 3, -L), p(0, -H - 3, L), 3.2, 3.2, 4, Math.PI / 4, WOOD_DARK, WOOD_DARK);
		// pegs along both sides, with a short tie up to the hem
		for (int side = -1; side <= 1; side += 2) {
			for (double z : new double[] { -L + 10, 0, L - 10 }) {
				double x = side * (W + (z == 0 ? 7 : 0) + 18);
				m.tube(p(x, 4, z), p(x, -8, z), 3, 2.2, 4, 0, WOOD_RAW, WOOD_RAW);
				m.tube(p(x, -6, z), p(side * (W + (z == 0 ? 7 : 0) + 3), -10, z), 1.2, 1.2, 3, 0, ROPE, ROPE);
			}
		}
		return m;
	}

	/**
	 * A ring of hearth stones and a bed of ash, laid as ground decoration under a burning fire (the fire is the stock
	 * one, for its flames). With {@code tripod}: three poles lashed over it and a blackened pot hanging from them.
	 */
	ModelData fireRing(boolean tripod) {
		ModelData m = new ModelData();
		// scorched earth and ash
		int n = 9;
		int c = m.vertex(0, -1, 0);
		int[] rim = new int[n];
		for (int i = 0; i < n; i++) {
			double a = Math.PI * 2 * i / n, r = 54 + random.nextInt(8);
			rim[i] = m.vertex(Math.cos(a) * r, -1, Math.sin(a) * r);
		}
		for (int i = 0; i < n; i++) {
			m.triOut(c, rim[i], rim[(i + 1) % n], i % 2 == 0 ? hsl(4, 1, 14) : hsl(8, 0, 22), p(0, 100, 0));
		}
		// the stones, each a little different
		int stones = 10;
		for (int i = 0; i < stones; i++) {
			double a = Math.PI * 2 * i / stones + random.nextDouble() * 0.2, r = 50;
			double rx = 12 + random.nextInt(5), ry = 9 + random.nextInt(5);
			int[] cols = { STONE[i % STONE.length], STONE[(i + 2) % STONE.length], STONE[(i + 3) % STONE.length] };
			lump(m, Math.cos(a) * r, -ry * 0.5, Math.sin(a) * r, rx, ry, rx * 0.85, 3, 5, 0.3, cols);
		}
		if (tripod) {
			double apex = -158;
			for (int i = 0; i < 3; i++) {
				double a = Math.PI * 2 * i / 3 + 0.4;
				m.tube(p(Math.cos(a) * 66, 2, Math.sin(a) * 66), p(Math.cos(a) * 4, apex, Math.sin(a) * 4), 3.6, 2.6, 4, a, WOOD,
						WOOD_DARK);
			}
			m.tube(p(0, apex + 4, 0), p(0, apex + 14, 0), 6, 6, 5, 0, ROPE, ROPE); // the lashing
			m.tube(p(0, apex + 12, 0), p(0, -112, 0), 1.4, 1.4, 3, 0, IRON, IRON); // chain
			// the pot: a squat iron belly with a rim, its bail handle over the top
			int pot = hsl(0, 0, 14), potRim = hsl(0, 0, 24);
			m.tube(p(0, -108, 0), p(0, -96, 0), 15, 19, 7, 0, pot, pot);
			m.tube(p(0, -96, 0), p(0, -80, 0), 19, 13, 7, 0, pot, pot);
			m.tube(p(0, -112, 0), p(0, -107, 0), 17, 17, 7, 0, potRim, hsl(5, 3, 30)); // something simmering inside
			m.tube(p(-17, -110, 0), p(0, -124, 0), 1.2, 1.2, 3, 0, IRON, IRON);
			m.tube(p(17, -110, 0), p(0, -124, 0), 1.2, 1.2, 3, 0, IRON, IRON);
		}
		return m;
	}

	/** A log to sit on by the fire: a short round of trunk on its side, bark on, the saw-cut ends pale. */
	ModelData logSeat() {
		ModelData m = new ModelData();
		int bark = hsl(5, 4, 24), barkDark = hsl(5, 4, 18), cut = hsl(7, 5, 58);
		m.tube(p(-52, -21, 0), p(52, -20, 3), 21, 20, 7, 0.2, bark, cut);
		m.tube(p(-10, -40, -4), p(16, -41, -2), 4, 0, 3, 0, barkDark, barkDark); // the stub of a lopped branch
		return m;
	}

	/* ====================================================================== */
	/*                                 FLAGS                                  */
	/* ====================================================================== */

	/** A cloth hanging or flying off a line of points: columns of (x, yTop, yBottom, z), two-sided, folds shaded. */
	private static void cloth(ModelData m, double[][] cols, int light, int dark) {
		int[] top = new int[cols.length], bot = new int[cols.length];
		for (int i = 0; i < cols.length; i++) {
			top[i] = m.vertex(cols[i][0], cols[i][1], cols[i][3]);
			bot[i] = m.vertex(cols[i][0], cols[i][2], cols[i][3]);
		}
		for (int i = 0; i + 1 < cols.length; i++) {
			int col = i % 2 == 0 ? light : dark;
			twoSided(m, top[i], top[i + 1], bot[i + 1], col);
			twoSided(m, top[i], bot[i + 1], bot[i], col);
		}
	}

	/**
	 * The Drowned Maiden's colours on a real pole: a ship's spar stepped in a cairn of stones, a black flag torn to
	 * rags along its trailing edge, with a bone-white skull and crossbones on it.
	 */
	ModelData pirateFlag() {
		ModelData m = new ModelData();
		int black = hsl(0, 0, 10), blackFold = hsl(0, 0, 16), bone = hsl(7, 1, 100), spar = hsl(5, 4, 20);
		// the cairn it's stepped in
		for (int i = 0; i < 5; i++) {
			double a = Math.PI * 2 * i / 5;
			lump(m, Math.cos(a) * 24, -10, Math.sin(a) * 24, 18, 14, 16, 3, 5, 0.35,
					new int[] { STONE[i % 5], STONE[(i + 1) % 5], STONE[(i + 3) % 5] });
		}
		lump(m, 0, -26, 0, 18, 14, 18, 3, 5, 0.3, new int[] { STONE[3], STONE[0], STONE[1] });
		m.tube(p(0, 0, 0), p(0, -320, 0), 5.5, 4, 5, 0, spar, spar);
		m.tube(p(0, -320, 0), p(0, -330, 0), 6, 0, 5, 0, spar, spar); // the truck at the top
		m.tube(p(0, -306, 0), p(0, -300, 0), 7, 7, 5, 0, IRON, IRON); // an iron band where the halyard's tied
		// the flag flies off to +x, rippling, its trailing third in rags
		double top = -310, bot = -222;
		double[][] cols = { { 2, top, bot, 0 }, { 30, top + 2, bot + 1, 9 }, { 58, top + 4, bot + 3, -3 }, { 86, top + 7, bot + 1, 8 },
				{ 104, top + 10, bot + 12, -2 }, { 122, top + 16, bot - 2, 6 }, { 138, top + 28, bot + 20, 0 } };
		cloth(m, cols, black, blackFold);
		// tattered tongues off the bottom edge
		double[][] tongues = { { 86, bot + 1, 8, 98, bot + 26 }, { 104, bot + 12, -2, 114, bot + 34 }, { 122, bot - 2, 6, 130, bot + 22 } };
		for (double[] t : tongues) {
			int a = m.vertex(t[0] - 8, t[1] - 1, t[2]), b = m.vertex(t[0] + 6, t[1] - 1, t[2]), c = m.vertex(t[3], t[4], t[2] - 2);
			twoSided(m, a, b, c, blackFold);
		}
		// skull and crossbones, on both faces: z follows the ripple between columns 1 (z 9) and 2 (z -3)
		for (int face = -1; face <= 1; face += 2) {
			final double off = face * 2.5;
			java.util.function.DoubleUnaryOperator z = x -> 9 + (x - 30) / 28.0 * -12 + off;
			double sx = 44, sy = -274;
			int n = 7;
			int c = m.vertex(sx, sy, z.applyAsDouble(sx));
			int[] ring = new int[n];
			for (int i = 0; i < n; i++) {
				double a = Math.PI * 2 * i / n;
				double x = sx + Math.cos(a) * 12, y = sy + Math.sin(a) * 13 - (Math.sin(a) > 0.5 ? -3 : 0);
				ring[i] = m.vertex(x, y, z.applyAsDouble(x));
			}
			for (int i = 0; i < n; i++) {
				m.tri(c, face > 0 ? ring[(i + 1) % n] : ring[i], face > 0 ? ring[i] : ring[(i + 1) % n], bone);
			}
			for (int k = -1; k <= 1; k += 2) { // the bones, crossed under it
				double x0 = sx - 18, x1 = sx + 18, y0 = sy + 18 + k * 8, y1 = sy + 18 - k * 8;
				int a = m.vertex(x0, y0 - 2.5, z.applyAsDouble(x0)), b = m.vertex(x1, y1 - 2.5, z.applyAsDouble(x1)),
						cc = m.vertex(x1, y1 + 2.5, z.applyAsDouble(x1)), d = m.vertex(x0, y0 + 2.5, z.applyAsDouble(x0));
				if (face > 0) {
					m.tri(a, cc, b, bone);
					m.tri(a, d, cc, bone);
				} else {
					m.tri(a, b, cc, bone);
					m.tri(a, cc, d, bone);
				}
			}
		}
		return m;
	}

	/**
	 * Gatehold's banner: a tall pole with a crossbar, the banner hanging from it in the Wardens' night-blue with a
	 * pale gold star, its foot cut in a swallowtail.
	 */
	ModelData gateholdBanner() {
		ModelData m = new ModelData();
		int blue = hsl(42, 4, 34), blueFold = hsl(42, 4, 27), gold = hsl(8, 5, 82), trim = hsl(8, 5, 62);
		m.box(-16, -10, -16, 16, 0, 16, STONE[1]); // a footing stone
		m.tube(p(0, -10, 0), p(0, -330, 0), 5.5, 4.5, 5, 0, WOOD, WOOD_DARK);
		VoidEffectsHelper.knob(m, 0, -338, 0, 8, trim);
		m.tube(p(-44, -314, 0), p(44, -314, 0), 3.2, 3.2, 4, Math.PI / 4, WOOD_DARK, WOOD_DARK); // the crossbar
		// the banner, hanging just in front of the pole and stirring a little
		double z0 = 8, top = -312, bot = -150;
		double[][] cols = { { -38, top, bot + 18, z0 }, { -13, top, bot + 4, z0 + 3 }, { 13, top, bot + 4, z0 - 1 }, { 38, top, bot + 18, z0 + 2 } };
		cloth(m, cols, blue, blueFold);
		// the swallowtail: two points hanging below the middle
		int l0 = m.vertex(-38, bot + 18, z0), lm = m.vertex(-13, bot + 4, z0 + 3), lt = m.vertex(-30, bot - 22, z0 + 1);
		twoSided(m, l0, lm, lt, blueFold);
		int r0 = m.vertex(13, bot + 4, z0 - 1), r1 = m.vertex(38, bot + 18, z0 + 2), rt = m.vertex(30, bot - 22, z0 + 1);
		twoSided(m, r0, r1, rt, blue);
		int v0 = m.vertex(-13, bot + 4, z0 + 3), v1 = m.vertex(13, bot + 4, z0 - 1), vm = m.vertex(0, bot + 24, z0 + 1);
		twoSided(m, v0, vm, v1, blueFold);
		// a gold hem along the top, and the star
		m.box(-39, -314, z0 - 2, 39, -302, z0 + 4, trim);
		for (int face = -1; face <= 1; face += 2) {
			double sz = z0 + 1 + face * 3.5, sy = -238;
			int c = m.vertex(0, sy, sz);
			int[] pts = new int[10];
			for (int i = 0; i < 10; i++) {
				double a = -Math.PI / 2 + Math.PI * i / 5, r = i % 2 == 0 ? 26 : 10;
				pts[i] = m.vertex(Math.cos(a) * r, sy + Math.sin(a) * r, sz);
			}
			for (int i = 0; i < 10; i++) {
				if (face > 0) {
					m.tri(c, pts[(i + 1) % 10], pts[i], gold);
				} else {
					m.tri(c, pts[i], pts[(i + 1) % 10], gold);
				}
			}
		}
		return m;
	}

	/* ====================================================================== */
	/*                     THE OLD KINGDOM - stone and ruin                    */
	/* ====================================================================== */

	/** A plain standing stone of the old circle: tall, hewn, leaning a little, grown with lichen. Each call differs. */
	ModelData menhir() {
		ModelData m = new ModelData();
		double h = 230 + random.nextInt(60);
		double[][] rings = { { 0, 34, 28 }, { -h * 0.3, 33, 26 }, { -h * 0.62, 29, 22 }, { -h * 0.88, 21, 16 }, { -h, 10, 8 } };
		int sides = 5;
		double lean = (random.nextDouble() - 0.5) * 0.16, twist = random.nextDouble() * Math.PI;
		int[][] idx = new int[rings.length][sides];
		for (int r = 0; r < rings.length; r++) {
			for (int s = 0; s < sides; s++) {
				double a = Math.PI * 2 * s / sides + twist;
				double j = r == 0 ? 1 : 0.86 + random.nextDouble() * 0.28;
				idx[r][s] = m.vertex(-rings[r][0] * lean + Math.cos(a) * rings[r][1] * j, rings[r][0], Math.sin(a) * rings[r][2] * j);
			}
		}
		int[] band = { hsl(10, 0, 36), hsl(10, 0, 42), hsl(10, 0, 46), hsl(10, 0, 50) };
		for (int r = 0; r + 1 < rings.length; r++) {
			double y = (rings[r][0] + rings[r + 1][0]) / 2;
			double[] ref = { -y * lean, y, 0 };
			for (int s = 0; s < sides; s++) {
				int col = (s + r) % 2 == 0 ? band[r] : band[Math.max(0, r - 1)];
				if (r == 0 && s % 2 == 0 || r == 1 && s == 3) {
					col = s % 4 == 0 ? MOSS : MOSS_LIGHT;
				} else if (r == 2 && s == 1) {
					col = hsl(11, 2, 58); // a pale crust of lichen
				}
				m.quadOut(idx[r][s], idx[r][(s + 1) % sides], idx[r + 1][(s + 1) % sides], idx[r + 1][s], col, ref);
			}
		}
		int top = m.vertex(h * lean + 4, -h - 10, 0);
		for (int s = 0; s < sides; s++) {
			m.triOut(idx[rings.length - 1][s], idx[rings.length - 1][(s + 1) % sides], top, band[3], p(h * lean, -h + 30, 0));
		}
		return m;
	}

	/** One dressed block of the old masonry, as a square-section prism from a to b (any angle). */
	private static void block(ModelData m, double[] a, double[] b, double r, double twist, int col) {
		m.tube(a, b, r, r, 4, twist, col, col);
	}

	/**
	 * One of the Broken Gate's towers on a 2x2 footprint: courses of big dressed blocks over a wider plinth, moss at
	 * the foot. The tall one still carries the springing of the arch, reaching west towards its partner (-x); the
	 * short one was broken off at half height. Both have ragged tops where blocks have fallen away.
	 */
	ModelData gateTower(boolean tall) {
		ModelData m = new ModelData();
		double half = 92, course = 36;
		int courses = tall ? 11 : 5;
		m.box(-half - 14, -10, -half - 14, half + 14, 2, half + 14, MOSS); // the plinth's foot, mossed over
		m.box(-half - 8, -course * 0.9, -half - 8, half + 8, -10, half + 8, ASHLAR[3]);
		for (int k = 0; k < courses; k++) {
			double y0 = -course * 0.9 - k * course, y1 = y0 - course + 2; // a thin mortar line between courses
			boolean top = k == courses - 1;
			// running bond: each course is three blocks, laid across the tower, then along it, so the joints alternate
			for (int b = 0; b < 3; b++) {
				if (top && (tall ? b == 1 : b != 0)) {
					continue; // fallen from the ragged top
				}
				double a0 = -half + b * half * 2 / 3 + 1.5, a1 = a0 + half * 2 / 3 - 3;
				double in = random.nextInt(5), rise = top ? random.nextInt(12) : 0;
				int col = ASHLAR[(k * 2 + b * 3 + random.nextInt(2)) % ASHLAR.length];
				if (k % 2 == 0) {
					m.box(a0, y0, -half + in, a1, y1 - rise, half - in, col);
				} else {
					m.box(-half + in, y0, a0, half - in, y1 - rise, a1, col);
				}
			}
			if (k == 1 || k == 2) { // moss creeping up one corner
				m.box(-half - 2, y0, half * 0.35, -half * 0.35, y1, half + 2, k == 1 ? MOSS : MOSS_LIGHT);
			}
		}
		if (tall) {
			// an arrow slit facing the road (south, -z), and the arch's springing: wedge-shaped voussoirs curving out
			// over the road towards the other tower (-x here; the tower is turned to face it), the last one snapped off
			m.box(-9, -course * 6, -half - 3, 9, -course * 8, -half + 4, hsl(8, 1, 8));
			double cx = -GATE_SPAN * 64, cy = -course * 6.5, R = GATE_SPAN * 64 - half + 10; // the arch's centre, between the towers
			for (int i = 0; i < 3; i++) {
				double a0 = Math.toRadians(4 + i * 11), a1 = Math.toRadians(4 + (i + 1) * 11 - 1.2);
				double[] p0 = { cx + Math.cos(a0) * R, cy - Math.sin(a0) * R, 0 }, p1 = { cx + Math.cos(a1) * R, cy - Math.sin(a1) * R, 0 };
				block(m, p0, p1, 46, Math.PI / 4, ASHLAR[(i * 2 + 1) % ASHLAR.length]);
			}
		}
		return m;
	}

	/**
	 * A length of the old curtain wall that ran out from the Gate (1x1, running along x): coursed blocks to a ragged top
	 * that steps down along its length. {@code high}: the stretch next to a tower; otherwise a low stump of it.
	 */
	ModelData gateWall(boolean high) {
		ModelData m = new ModelData();
		double half = 64, thick = 30, course = 36;
		m.box(-half, -10, -thick - 8, half, 2, thick + 8, MOSS);
		int[] heights = high ? new int[] { 5, 4, 4, 2 } : new int[] { 2, 2, 1, 1 }; // courses standing, per quarter
		for (int q = 0; q < 4; q++) {
			double x0 = -half + q * half / 2, x1 = x0 + half / 2;
			for (int k = 0; k < heights[q]; k++) {
				double y0 = -10 - k * course, y1 = y0 - course + 2;
				boolean joint = (k + q) % 2 == 0; // running bond: the joints alternate course to course
				double in = random.nextInt(3), rise = k == heights[q] - 1 && high ? random.nextInt(8) : 0;
				int col = ASHLAR[(k * 3 + q * 2 + random.nextInt(2)) % ASHLAR.length];
				if (joint || !high) {
					m.box(x0, y0, -thick + in, x1, y1 - rise, thick - in, col);
				} else {
					m.box(x0, y0, -thick + in, (x0 + x1) / 2 - 0.5, y1 - rise, thick - in, col);
					m.box((x0 + x1) / 2 + 0.5, y0, -thick + in, x1, y1 - rise, thick - in, ASHLAR[(k + q + 3) % ASHLAR.length]);
				}
			}
		}
		// a block fallen off the top, lying at its foot
		block(m, p(-20, -16, -thick - 26), p(24, -18, -thick - 30), 15, 0.4, ASHLAR[1]);
		return m;
	}

	/**
	 * What fell between the towers: dressed blocks tumbled every which way, a curved piece of the arch still in one
	 * lump, and chips of stone scattered round them (2x2).
	 */
	ModelData fallenMasonry() {
		ModelData m = new ModelData();
		block(m, p(-70, -26, -40), p(10, -30, -66), 30, 0.3, ASHLAR[0]);
		block(m, p(30, -24, 20), p(86, -28, 60), 28, 1.1, ASHLAR[2]);
		block(m, p(-40, -22, 50), p(-34, -82, 36), 26, 0.7, ASHLAR[4]); // one standing on its end
		block(m, p(-6, -60, -50), p(46, -56, -30), 22, 0.2, ASHLAR[3]); // fallen across the first
		// a piece of the arch: three voussoirs still mortared together, lying on its side
		double R = 120;
		for (int i = 0; i < 3; i++) {
			double a0 = Math.toRadians(200 + i * 22), a1 = Math.toRadians(200 + (i + 1) * 22 - 2);
			double[] p0 = { 60 + Math.cos(a0) * R, -22, -10 + Math.sin(a0) * R }, p1 = { 60 + Math.cos(a1) * R, -22, -10 + Math.sin(a1) * R };
			block(m, p0, p1, 24, 0, ASHLAR[(i + 1) % ASHLAR.length]);
		}
		for (int i = 0; i < 7; i++) {
			double a = random.nextDouble() * Math.PI * 2, r = 40 + random.nextDouble() * 70;
			lump(m, Math.cos(a) * r, -5, Math.sin(a) * r, 8 + random.nextInt(6), 6 + random.nextInt(4), 8, 3, 4, 0.4,
					new int[] { STONE[i % 5], STONE[(i + 2) % 5] });
		}
		return m;
	}

	/* ====================================================================== */

	/** A rough lump: rings of jittered vertices, faces wound outward, flat underneath. */
	void lump(ModelData m, double cx, double cy, double cz, double rx, double ry, double rz, int rings, int sides, double jitter,
			int[] colors) {
		int[][] idx = new int[rings + 1][];
		int top = m.vertex(cx, cy - ry, cz), bottom = m.vertex(cx, cy + ry * 0.55, cz);
		for (int r = 1; r < rings; r++) {
			double th = Math.PI * r / rings;
			idx[r] = new int[sides];
			for (int k = 0; k < sides; k++) {
				double a = Math.PI * 2 * k / sides + r * 0.4;
				double j = 1 + (random.nextDouble() - 0.5) * jitter;
				double y = Math.min(-Math.cos(th) * ry, ry * 0.55);
				idx[r][k] = m.vertex(cx + Math.cos(a) * Math.sin(th) * rx * j, cy + y, cz + Math.sin(a) * Math.sin(th) * rz * j);
			}
		}
		double[] c = { cx, cy, cz };
		for (int k = 0; k < sides; k++) {
			int k2 = (k + 1) % sides;
			m.triOut(top, idx[1][k], idx[1][k2], colors[0], c);
			for (int r = 1; r + 1 < rings; r++) {
				m.quadOut(idx[r][k], idx[r][k2], idx[r + 1][k2], idx[r + 1][k], colors[Math.min(colors.length - 1, r)], c);
			}
			m.triOut(bottom, idx[rings - 1][k], idx[rings - 1][k2], colors[colors.length - 1], c);
		}
	}

	/** Small shared bits. */
	static final class VoidEffectsHelper {
		private VoidEffectsHelper() {
		}

		/** A small faceted ball (a finial). */
		static void knob(ModelData m, double x, double y, double z, double r, int col) {
			m.tube(p(x, y + r, z), p(x, y, z), r * 0.6, r, 5, 0, col, col);
			m.tube(p(x, y, z), p(x, y - r, z), r, 0, 5, 0, col, col);
		}
	}
}
