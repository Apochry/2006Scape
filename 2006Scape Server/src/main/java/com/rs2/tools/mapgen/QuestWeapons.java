package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ModelData.hsl;

/**
 * Quest weapons of the Void saga, hand-built in the old-school style (few polygons, chunky silhouettes, flat facets,
 * 3-6 muted colours with one accent).
 *
 * <h2>How they are built</h2>
 * Like ModelForge's Barrowsteel longsword, each weapon is built once in its own "weapon frame" and then placed into
 * an inventory pose and a wielded pose:
 * <ul>
 * <li>{@code s} runs along the weapon from the butt / pommel (0) to the tip (about {@code L}), in final model units;</li>
 * <li>{@code w} runs across the weapon in its flat plane (the side the blade / belly of the bow is on is +w);</li>
 * <li>{@code t} runs through it (thickness).</li>
 * </ul>
 * Frame coordinates are stored ×{@link #K} so small parts keep their shape until the pose rounds them.
 *
 * <h2>Matching the existing animations</h2>
 * Every existing weapon's wielded model sits in the right hand at about (-30, -97, 0) (the hand kit's vertices,
 * label 27), lies in the y-z plane (x ≈ -30) and points forward (-z) and up (-y). Each {@link Pose} below was read
 * off the WIELDED model of the comparable cache item: where its butt and tip are, which way its blade faces, and the
 * skin label its vertices carry (27 for rune mace / magic shortbow / rune longsword, 50 for rune halberd / rune
 * battleaxe / staff). Our vertices get the same label, so the existing attack, stand and walk animations move them
 * with the hand exactly as they move the original. The inventory pose likewise copies the original inventory model's
 * butt / tip / blade-side placement, so the item definition's zoom and rotations (opcodes 4-8) can be copied as-is.
 */
public final class QuestWeapons {

	static final double K = 4;

	/** A point in the weapon frame (s along, w across, t through). */
	static double[] f(double s, double w, double t) {
		return new double[] { t * K, w * K, s * K };
	}

	/* ====================================================================== */
	/*                                 POSES                                  */
	/* ====================================================================== */

	/**
	 * Where a weapon frame goes: butt point B, tip point P (frame s = 0 and s = L), a hint for the +w direction
	 * (made perpendicular to the axis), and whether to drop the result onto the ground plane (inventory models rest
	 * on y = 0).
	 */
	static final class Place {
		final double[] b, p, wHint;
		final boolean ground;

		Place(double[] b, double[] p, double[] wHint, boolean ground) {
			this.b = b;
			this.p = p;
			this.wHint = wHint;
			this.ground = ground;
		}
	}

	/** Everything read off one existing cache item. */
	static final class Pose {
		final String baseName;
		final int baseItem, baseWield, baseInv, skin;
		final double length; // L: frame length = |P - B| of the wielded original
		final Place wield, inv;
		/** A second wielded model the base item adds (item opcodes 24/26, e.g. the magic staff's orb), or -1. */
		int baseWieldExtra = -1;

		Pose extra(int model) {
			baseWieldExtra = model;
			return this;
		}

		Pose(String baseName, int baseItem, int baseWield, int baseInv, int skin, Place wield, Place inv) {
			this.baseName = baseName;
			this.baseItem = baseItem;
			this.baseWield = baseWield;
			this.baseInv = baseInv;
			this.skin = skin;
			this.wield = wield;
			this.inv = inv;
			this.length = dist(wield.b, wield.p);
		}

		/** Frame s coordinate of the hand (-30, -97, 1): where the grip must be. */
		double gripS() {
			double[] a = unit(sub(wield.p, wield.b));
			return dot(sub(new double[] { -30, -97, 1 }, wield.b), a);
		}
	}

	/*
	 * Values read off the cache (see README for the vertex dumps):
	 *  rune halberd  wield 555 (label 50): butt ring centre (-30,-47,110), tip spike (-30,-151,-106), blade below (+y);
	 *                inv 2791: butt (59,-4,-43), head end (-50,-3,57), blade towards -x/-z.
	 *  rune battleaxe wield 550 (label 50): pommel (-30,-95,10), head top (-30,-109,-55); longer bit below (+y);
	 *                inv 2778: pommel (4,-1,28), head top (-2,-1,-36); longer bit towards +x.
	 *  rune mace     wield 502 (label 27): pommel (-29,-96,10), head top (-29,-110,-59);
	 *                inv 2464: pommel (5,-4,30), head top (-2,-18,-38) (the head rests on the ground, handle raised).
	 *  magic shortbow wield 512 (label 27): lower tip (-29,-81,63), upper tip (-29,-122,-57), grip bulges to +y;
	 *                inv 2562: tips (14,-4,36) and (-26,-4,-38), grip bulges to +x.
	 *  staff/magic staff wield 534 (label 50): butt (-30,-48,109), top (-30,-145,-102); the magic staff adds its orb
 *                as a second wielded model, 567 (opcodes 24/26), reaching to (-30,-157,-125);
	 *                inv 2810: butt (-58,-2,52), shaft top (46,-3,-46).
	 */
	static final Pose HALBERD = new Pose("Rune halberd", 3202, 555, 2791, 50,
			new Place(v(-30, -47, 110), v(-30, -151, -106), v(0, 1, 0), false),
			new Place(v(59, -4, -43), v(-50, -3, 57), v(-1, 0, -1), true));
	static final Pose BATTLEAXE = new Pose("Rune battleaxe", 1373, 550, 2778, 50,
			new Place(v(-30, -95.5, 10), v(-30, -109, -55), v(0, 1, 0), false),
			new Place(v(4, -1, 28), v(-2, -1, -36), v(1, 0, 0), true));
	static final Pose MACE = new Pose("Rune mace", 1432, 502, 2464, 27,
			new Place(v(-29, -96, 9.5), v(-29, -110, -59), v(0, 1, 0), false),
			new Place(v(5, -4, 30), v(-2, -18, -38), v(1, 0, 0), true));
	static final Pose SHORTBOW = new Pose("Magic shortbow", 861, 512, 2562, 27,
			new Place(v(-29, -81, 63), v(-29, -122, -57), v(0, 1, 0), false),
			new Place(v(14, -4, 36), v(-26, -4, -38), v(1, 0, 0), true));
	static final Pose STAFF = new Pose("Magic staff", 1389, 534, 2810, 50,
			new Place(v(-30, -48, 109), v(-30, -145, -102), v(0, 1, 0), false),
			new Place(v(-58, -2, 52.5), v(46, -3, -46.5), v(1, 0, 1), true)).extra(567);

	/** Places a frame-built model: s → along B→P (scaled so L maps onto |P - B|), w → the hint, t → w × a. */
	static ModelData place(ModelData m, Pose pose, Place pl) {
		double[] a = unit(sub(pl.p, pl.b));
		double[] w = unit(sub(pl.wHint, mul(a, dot(pl.wHint, a))));
		double[] t = cross(w, a); // det[t w a] = +1: a rotation, never a mirror, so face winding survives
		double sc = dist(pl.b, pl.p) / pose.length;
		m.transform(q -> {
			double tt = q[0] / K * sc, ww = q[1] / K * sc, ss = q[2] / K * sc;
			return new double[] { pl.b[0] + a[0] * ss + w[0] * ww + t[0] * tt, pl.b[1] + a[1] * ss + w[1] * ww + t[1] * tt,
					pl.b[2] + a[2] * ss + w[2] * ww + t[2] * tt };
		});
		if (pl.ground) {
			int maxY = m.bounds()[4];
			m.transform(q -> new double[] { q[0], q[1] - maxY, q[2] });
		}
		return m;
	}

	static ModelData wielded(ModelData frame, Pose pose) {
		place(frame, pose, pose.wield);
		for (int i = 0; i < frame.vertexSkins.size(); i++) {
			frame.vertexSkins.set(i, pose.skin);
		}
		return frame;
	}

	static ModelData inventory(ModelData frame, Pose pose) {
		place(frame, pose, pose.inv);
		for (int i = 0; i < frame.vertexSkins.size(); i++) {
			frame.vertexSkins.set(i, 0);
		}
		return frame;
	}

	/* ====================================================================== */
	/*                           BUILDING HELPERS                             */
	/* ====================================================================== */

	/**
	 * A flat blade plate with a honed bevel, built in an arbitrary plane of the frame: outline points {@code pts[i] =
	 * {u, v}} (star-shaped about {@code c}) map to origin + u·U + v·V. Each outline point has its own half-thickness
	 * along N (0 = a sharp edge, where front and back meet). An inner copy of the outline (pulled 45% towards the
	 * centre) carries the flat of the blade at half-thickness {@code hc}; between it and the outline runs the bevel.
	 * Bevels that end in a sharp edge get the bright {@code bevel} colour - the classic two-tone old-school blade.
	 */
	static void plate(ModelData m, double[] o, double[] U, double[] V, double[] N, double[][] pts, double[] h, double[] c, double hc,
			int face, int bevel, int side) {
		int n = pts.length;
		double k = 0.55;
		int[] fr = new int[n], bk = new int[n], ifr = new int[n], ibk = new int[n];
		for (int i = 0; i < n; i++) {
			double[] p = add(o, add(mul(U, pts[i][0]), mul(V, pts[i][1])));
			fr[i] = m.vertex(K * (p[0] + N[0] * h[i]), K * (p[1] + N[1] * h[i]), K * (p[2] + N[2] * h[i]));
			bk[i] = h[i] == 0 ? fr[i] : m.vertex(K * (p[0] - N[0] * h[i]), K * (p[1] - N[1] * h[i]), K * (p[2] - N[2] * h[i]));
			double[] q = add(o, add(mul(U, c[0] + (pts[i][0] - c[0]) * k), mul(V, c[1] + (pts[i][1] - c[1]) * k)));
			double hi = Math.max(hc, h[i]);
			ifr[i] = m.vertex(K * (q[0] + N[0] * hi), K * (q[1] + N[1] * hi), K * (q[2] + N[2] * hi));
			ibk[i] = m.vertex(K * (q[0] - N[0] * hi), K * (q[1] - N[1] * hi), K * (q[2] - N[2] * hi));
		}
		double[] cp = add(o, add(mul(U, c[0]), mul(V, c[1])));
		int cf = m.vertex(K * (cp[0] + N[0] * hc), K * (cp[1] + N[1] * hc), K * (cp[2] + N[2] * hc));
		int cb = m.vertex(K * (cp[0] - N[0] * hc), K * (cp[1] - N[1] * hc), K * (cp[2] - N[2] * hc));
		double[] refF = mul(add(cp, mul(N, -1000)), K), refB = mul(add(cp, mul(N, 1000)), K), refC = mul(cp, K);
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			// the flat
			m.triOut(cf, ifr[i], ifr[j], face, refF);
			m.triOut(cb, ibk[i], ibk[j], face, refB);
			// the bevel
			int bc = h[i] == 0 && h[j] == 0 ? bevel : face;
			m.quadOut(ifr[i], ifr[j], fr[j], fr[i], bc, refF);
			m.quadOut(ibk[i], ibk[j], bk[j], bk[i], bc, refB);
			if (h[i] == 0 && h[j] == 0) {
				continue; // a sharp edge: front and back meet, no side wall
			}
			if (h[i] == 0) {
				m.triOut(fr[i], fr[j], bk[j], side, refC);
			} else if (h[j] == 0) {
				m.triOut(fr[i], fr[j], bk[i], side, refC);
			} else {
				m.quadOut(fr[i], fr[j], bk[j], bk[i], side, refC);
			}
		}
	}

	/** A plate in the frame's own s-w plane (u = s, v = w, thickness along t). */
	static void plateSW(ModelData m, double[][] pts, double[] h, double[] c, double hc, int face, int bevel, int side) {
		plate(m, v(0, 0, 0), v(0, 0, 1), v(0, 1, 0), v(1, 0, 0), pts, h, c, hc, face, bevel, side);
	}

	/** A tube between two frame points (radii in frame units). */
	static void tube(ModelData m, double[] a, double[] b, double r0, double r1, int sides, double twist, int col, int cap) {
		m.tube(a, b, r0 * K, r1 * K, sides, twist, col, cap);
	}

	/**
	 * A diamond-section blade or spike along s: stations {s, halfWidth(w), halfThickness(t)}, closing to a point at
	 * {@code tipS}. The two bevels of each face get the two shades.
	 */
	static void diamond(ModelData m, double[][] st, double tipS, double tipW, int light, int dark) {
		int[][] ring = new int[st.length][4];
		for (int i = 0; i < st.length; i++) {
			double[][] pts = { f(st[i][0], st[i][1] + (st[i].length > 3 ? st[i][3] : 0), 0), f(st[i][0], st[i].length > 3 ? st[i][3] : 0, st[i][2]),
					f(st[i][0], -st[i][1] + (st[i].length > 3 ? st[i][3] : 0), 0), f(st[i][0], st[i].length > 3 ? st[i][3] : 0, -st[i][2]) };
			for (int k = 0; k < 4; k++) {
				ring[i][k] = m.vertex(pts[k][0], pts[k][1], pts[k][2]);
			}
		}
		double[] tp = f(tipS, tipW, 0);
		int tip = m.vertex(tp[0], tp[1], tp[2]);
		for (int i = 0; i + 1 < st.length; i++) {
			double[] ref = f((st[i][0] + st[i + 1][0]) / 2, st[i].length > 3 ? st[i][3] : 0, 0);
			for (int k = 0; k < 4; k++) {
				m.quadOut(ring[i][k], ring[i][(k + 1) % 4], ring[i + 1][(k + 1) % 4], ring[i + 1][k], k % 2 == 0 ? light : dark, ref);
			}
		}
		int last = st.length - 1;
		double[] ref = f((st[last][0] + tipS) / 2, (tipW + (st[last].length > 3 ? st[last][3] : 0)) / 2, 0);
		for (int k = 0; k < 4; k++) {
			m.triOut(ring[last][k], ring[last][(k + 1) % 4], tip, k % 2 == 0 ? light : dark, ref);
		}
		// close the base
		double[] bref = f(st[0][0] + 5, 0, 0);
		m.triOut(ring[0][0], ring[0][1], ring[0][2], dark, bref);
		m.triOut(ring[0][0], ring[0][2], ring[0][3], dark, bref);
	}

	/** A star: three crossed double-pyramids (like ModelForge's star pillar), centred on a frame point. */
	static void star(ModelData m, double[] c, double len, double r, int col, int col2) {
		double[][] dirs = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
		for (int k = 0; k < 3; k++) {
			double l = k == 2 ? len * 1.25 : len; // a little longer along the staff, like a star's long ray
			for (int sgn = -1; sgn <= 1; sgn += 2) {
				double[] e = { c[0] + dirs[k][0] * l * K * sgn, c[1] + dirs[k][1] * l * K * sgn, c[2] + dirs[k][2] * l * K * sgn };
				m.tube(c, e, r * K, 0, 4, Math.PI / 4 + k * 0.3, (k + (sgn > 0 ? 1 : 0)) % 2 == 0 ? col : col2, col);
			}
		}
	}

	/* ====================================================================== */
	/*                        1. THE STARFALL HALBERD                         */
	/* ====================================================================== */

	/**
	 * Starfall halberd - forged by Dorrin from the fallen star's metal. A long charcoal haft, a broad crescent axe
	 * blade of pale starmetal hanging below the socket (where the rune halberd's blade is), a back-hook, a diamond
	 * top-spike, and a violet star-shard set through the blade as the one accent. Built in the halberd frame: L = 240
	 * (butt at s = 0, hand at s ≈ 121, top spike at s = 240).
	 */
	static ModelData halberdFrame() {
		ModelData m = new ModelData();
		int metal = hsl(42, 1, 70), metalDark = hsl(42, 1, 56), metalEdge = hsl(42, 1, 102), haft = ConfigDefinitions.voidRamp(18, 2),
				wrap = ConfigDefinitions.voidRamp(30, 3), shard = ConfigDefinitions.voidRamp(74, 6);
		// the haft
		tube(m, f(8, 0, 0), f(206, 0, 0), 3.4, 3.1, 4, Math.PI / 4, haft, haft);
		// butt: a starmetal ferrule and a stubby point
		tube(m, f(7, 0, 0), f(16, 0, 0), 4.6, 4.1, 4, Math.PI / 4, metalDark, metalDark);
		tube(m, f(7, 0, 0), f(0, 0, 0), 4.6, 0, 4, Math.PI / 4, metalDark, metalDark);
		// two grip wraps where the hands go
		tube(m, f(104, 0, 0), f(134, 0, 0), 3.9, 3.9, 4, Math.PI / 4, wrap, wrap);
		tube(m, f(150, 0, 0), f(164, 0, 0), 3.8, 3.8, 4, Math.PI / 4, wrap, wrap);
		// the socket collar under the head
		tube(m, f(168, 0, 0), f(176, 0, 0), 4.2, 5.4, 6, 0, metalDark, metalDark);
		tube(m, f(176, 0, 0), f(213, 0, 0), 4.6, 4.2, 4, Math.PI / 4, metalDark, metalDark);
		// the great crescent blade, on the +w side: horns curl back towards the haft
		double[][] blade = { { 178, 3 }, { 174, 13 }, { 162, 31 }, { 180, 35 }, { 198, 38 }, { 216, 36 }, { 234, 29 }, { 220, 16 }, { 212, 3 } };
		double[] bh = { 2.4, 1.8, 0, 0, 0, 0, 0, 1.8, 2.4 };
		plateSW(m, blade, bh, new double[] { 196, 18 }, 2.6, metal, metalEdge, metalDark);
		// the back-hook on the -w side
		double[][] hook = { { 182, -3 }, { 206, -3 }, { 200, -12 }, { 184, -27 }, { 188, -12 } };
		double[] hh = { 2.0, 2.0, 1.2, 0, 1.2 };
		plateSW(m, hook, hh, new double[] { 194, -8 }, 2.0, metal, metalEdge, metalDark);
		// the top spike: a diamond blade
		diamond(m, new double[][] { { 212, 3.6, 3.2 }, { 222, 5.2, 2.6 } }, 241, 0, metalEdge, metal);
		// a violet star-shard set through the blade
		tube(m, f(196, 18, 0), f(196, 18, 4.8), 4.2, 0, 4, 0, shard, shard);
		tube(m, f(196, 18, 0), f(196, 18, -4.8), 4.2, 0, 4, 0, shard, shard);
		return m;
	}

	/* ====================================================================== */
	/*                          2. THE RIMECLEAVER                            */
	/* ====================================================================== */

	/**
	 * Rimecleaver - a battleaxe of the rime, its head never thawed. A frost-blackened haft with ice-blue wraps, a big
	 * crescent bit of pale rime-steel on the side where the rune battleaxe's longer bit is, and on the other side a
	 * crest of three icicles where a second bit would be. An icicle spike tops it. Battleaxe frame: L = 66 (pommel at
	 * s = 0, hand at s ≈ 9).
	 */
	static ModelData battleaxeFrame() {
		ModelData m = new ModelData();
		int ice = hsl(36, 2, 92), iceDark = hsl(36, 3, 70), iceDeep = hsl(36, 3, 50), rime = hsl(36, 1, 112), haft = hsl(36, 1, 18),
				wrap = hsl(36, 3, 44),
				steel = hsl(36, 1, 34);
		// haft and wraps
		tube(m, f(3, 0, 0), f(62, 0, 0), 2.6, 2.4, 4, Math.PI / 4, haft, haft);
		tube(m, f(5, 0, 0), f(9, 0, 0), 3.1, 3.1, 4, Math.PI / 4, wrap, wrap);
		tube(m, f(13, 0, 0), f(17, 0, 0), 3.1, 3.1, 4, Math.PI / 4, wrap, wrap);
		// pommel: a blunt ice crystal
		tube(m, f(3, 0, 0), f(-2, 0, 0), 3.6, 0, 4, 0, iceDark, iceDark);
		// the socket
		tube(m, f(32, 0, 0), f(60, 0, 0), 4.0, 3.6, 4, Math.PI / 4, steel, steel);
		// the crescent bit (+w)
		double[][] bit = { { 37, 3 }, { 33, 10 }, { 24, 22 }, { 36, 24 }, { 49, 25 }, { 62, 24 }, { 72, 20 }, { 63, 10 }, { 58, 3 } };
		double[] bh = { 2.4, 1.6, 0, 0, 0, 0, 0, 1.6, 2.4 };
		plateSW(m, bit, bh, new double[] { 48, 13 }, 2.7, iceDark, rime, iceDeep);
		// the icicle crest (-w): three crystals, longest in the middle
		tube(m, f(40, -3, 0), f(32, -15, 0), 3.4, 0, 4, 0.3, iceDark, ice);
		tube(m, f(47, -3, 0), f(45, -24, 0), 4.2, 0, 4, 0.6, ice, iceDark);
		tube(m, f(54, -3, 0), f(60, -17, 0), 3.6, 0, 4, 0.2, iceDark, ice);
		// the icicle spike on top
		tube(m, f(59, 0, 0), f(74, 1, 0), 3.4, 0, 4, Math.PI / 4, ice, iceDark);
		return m;
	}

	/* ====================================================================== */
	/*                   3. THE HOLLOW KING'S SCEPTRE (mace)                  */
	/* ====================================================================== */

	/**
	 * The Hollow King's sceptre - the crown he no longer has a head for, worn on a rod of black iron. Six gold
	 * flanges rise round the head like the points of a crown, and inside it there is nothing: a slow violet void
	 * where the jewel should be. Mace frame: L = 70 (pommel s = 0, hand s ≈ 8, head centre s ≈ 54).
	 */
	static ModelData maceFrame() {
		ModelData m = new ModelData();
		int gold = hsl(8, 4, 60), goldDark = hsl(8, 5, 40), goldBright = hsl(8, 3, 86), iron = ConfigDefinitions.voidRamp(16, 1), void_ = ConfigDefinitions.voidRamp(30, 7), voidGlow = ConfigDefinitions.voidRamp(76, 6);
		// rod and pommel
		tube(m, f(4, 0, 0), f(46, 0, 0), 2.6, 2.3, 4, Math.PI / 4, iron, iron);
		tube(m, f(0, 0, 0), f(4, 0, 0), 2.0, 4.0, 6, 0, gold, gold);
		tube(m, f(4, 0, 0), f(7, 0, 0), 4.0, 2.4, 6, 0, goldDark, goldDark);
		tube(m, f(22, 0, 0), f(25, 0, 0), 3.1, 3.1, 4, Math.PI / 4, goldDark, goldDark);
		// the crown's circlet
		tube(m, f(40, 0, 0), f(46, 0, 0), 3.0, 9.5, 6, 0, goldDark, goldDark);
		tube(m, f(46, 0, 0), f(50, 0, 0), 9.5, 9.5, 6, 0, gold, goldDark);
		// the void in the middle
		tube(m, f(48, 0, 0), f(56, 0, 0), 7.0, 7.4, 6, Math.PI / 6, void_, void_);
		tube(m, f(56, 0, 0), f(64, 0, 0), 7.4, 0, 6, Math.PI / 6, voidGlow, voidGlow);
		// five flanges, the points of the crown
		for (int i = 0; i < 5; i++) {
			double a = Math.PI * 2 * i / 5;
			double[] V = { Math.sin(a), Math.cos(a), 0 }; // radial direction in (t, w)
			double[] N = { Math.cos(a), -Math.sin(a), 0 };
			double[][] pts = { { 43, 4 }, { 48, 14 }, { 72, 12 }, { 60, 5 } };
			double[] h = { 1.6, 0, 0, 1.2 };
			plate(m, v(0, 0, 0), v(0, 0, 1), V, N, pts, h, new double[] { 54, 8.5 }, 1.8, gold, goldBright, goldDark);
		}
		return m;
	}

	/* ====================================================================== */
	/*                         4. THE DUSKYEW SHORTBOW                        */
	/* ====================================================================== */

	/**
	 * Duskyew shortbow - cut from a yew that grew in the Scar and came up black and violet. Faceted limbs swept into
	 * recurved horn tips capped with starmetal, a ghost-teal grip, and a pale string. Bow frame: tip to tip L ≈ 127,
	 * the belly bulging to +w like the magic shortbow's, grip centred on the hand (s ≈ 64, w ≈ 6).
	 */
	static ModelData shortbowFrame() {
		return shortbowFrame(1.0);
	}

	/**
	 * @param bend how deep the bow's curve is. The cache's own bow is drawn deeper in its inventory model than in
	 *            its wielded one (grip ~16 units off the string line vs ~5), so the inventory model uses bend 2.4.
	 */
	static ModelData shortbowFrame(double bend) {
		ModelData m = new ModelData();
		int wood = ConfigDefinitions.voidRamp(26, 2), woodDark = ConfigDefinitions.voidRamp(16, 2), grip = hsl(27, 3, 42), cap = hsl(42, 1, 80), string = hsl(42, 0, 100);
		// the limb: a chain of tapered 4-sided tubes through these (s, w) points, recurving at both tips
		// (the magic shortbow's limb is a shallow double curve: grip ~5 off the tip-to-tip line, limbs ~8, tips back on it)
		double[][] limb = { { 3, 4 }, { 8, -0.5 }, { 24, 7 }, { 44, 9.5 }, { 64, 6 }, { 84, 9.5 }, { 104, 7 }, { 119, -0.5 }, { 124, 4 } };
		double[] r = { 1.6, 2.3, 3.1, 3.7, 4.0, 3.7, 3.1, 2.3, 1.6 };
		for (int i = 0; i < limb.length; i++) {
			limb[i][1] = limb[i][1] < 0 ? limb[i][1] : limb[i][1] * bend;
		}
		for (int i = 0; i + 1 < limb.length; i++) {
			tube(m, f(limb[i][0], limb[i][1], 0), f(limb[i + 1][0], limb[i + 1][1], 0), r[i], r[i + 1], 4, Math.PI / 4,
					i % 2 == 0 ? wood : woodDark, wood);
		}
		// the grip wrap
		tube(m, f(56, 6 * bend + 1.2, 0), f(72, 6 * bend + 1.2, 0), 4.7, 4.7, 4, Math.PI / 4, grip, grip);
		// starmetal horn caps on the tips
		double tw = 4 * bend;
		tube(m, f(4.5, tw - 1.5, 0), f(-1, tw + 5, 0), 2.5, 0, 4, Math.PI / 4, cap, cap);
		tube(m, f(122.5, tw - 1.5, 0), f(128, tw + 5, 0), 2.5, 0, 4, Math.PI / 4, cap, cap);
		// the string, from tip to tip across the recurves, on the archer's (-w) side of the grip
		tube(m, f(4, tw - 2, 0), f(123, tw - 2, 0), 0.8, 0.8, 3, 0, string, string);
		return m;
	}

	/* ====================================================================== */
	/*                       5. STAFF OF THE CAGED STAR                       */
	/* ====================================================================== */

	/**
	 * Staff of the caged star - a void-yew staff whose head is a cage of four starmetal prongs, and in the cage a
	 * violet star the Hollow King pulled from the sky. Staff frame: L = 232 (butt s = 0, hand s ≈ 119, top s = 232);
	 * like the magic staff's orb, the cage tops out just past the shaft.
	 */
	static ModelData staffFrame() {
		ModelData m = new ModelData();
		int wood = ConfigDefinitions.voidRamp(20, 2), woodDark = ConfigDefinitions.voidRamp(13, 2), metal = hsl(42, 1, 84), metalDark = hsl(42, 1, 58), star = ConfigDefinitions.voidRamp(92, 6),
				starCore = ConfigDefinitions.voidRamp(120, 3);
		// the shaft: a slightly knotted void-yew pole
		tube(m, f(6, 0, 0), f(80, 0.8, 0), 4.0, 4.3, 5, 0, wood, wood);
		tube(m, f(80, 0.8, 0), f(150, -0.6, 0), 4.3, 4.0, 5, 0.3, woodDark, woodDark);
		tube(m, f(150, -0.6, 0), f(200, 0, 0), 4.0, 4.6, 5, 0.1, wood, wood);
		// butt ferrule
		tube(m, f(6, 0, 0), f(12, 0, 0), 5.0, 5.0, 5, 0, metalDark, metalDark);
		tube(m, f(6, 0, 0), f(0, 0, 0), 5.0, 1.8, 5, 0, metalDark, metalDark);
		// collar
		tube(m, f(194, 0, 0), f(204, 0, 0), 5.0, 7.0, 6, 0, metalDark, metal);
		// four prongs curving out round the star and back in
		double[][] prong = { { 203, 5.5 }, { 213, 15 }, { 235, 15 }, { 250, 3 } };
		for (int k = 0; k < 4; k++) {
			double a = Math.PI / 4 + Math.PI / 2 * k;
			for (int i = 0; i + 1 < prong.length; i++) {
				double[] p0 = f(prong[i][0], Math.cos(a) * prong[i][1], Math.sin(a) * prong[i][1]);
				double[] p1 = f(prong[i + 1][0], Math.cos(a) * prong[i + 1][1], Math.sin(a) * prong[i + 1][1]);
				tube(m, p0, p1, i == 2 ? 2.6 : 3.0, i == 2 ? 1.6 : i == 0 ? 3.0 : 2.6, 4, a, i == 1 ? metal : metalDark, metal);
			}
		}
		// the cap where the prongs meet
		tube(m, f(248, 0, 0), f(258, 0, 0), 4.2, 0, 4, Math.PI / 4, metal, metal);
		// the star
		star(m, f(225, 0, 0), 12, 4.6, star, starCore);
		return m;
	}

	/* ====================================================================== */

	/** One weapon: its name, how to build its frame, and the cache item it copies placement from. */
	static final class Weapon {
		final String name;
		final java.util.function.Supplier<ModelData> frame, invFrame;
		final Pose pose;

		Weapon(String name, java.util.function.Supplier<ModelData> frame, Pose pose) {
			this(name, frame, frame, pose);
		}

		Weapon(String name, java.util.function.Supplier<ModelData> frame, java.util.function.Supplier<ModelData> invFrame, Pose pose) {
			this.name = name;
			this.frame = frame;
			this.invFrame = invFrame;
			this.pose = pose;
		}

		ModelData inventoryModel() {
			return inventory(invFrame.get(), pose);
		}

		ModelData wieldedModel() {
			return wielded(frame.get(), pose);
		}
	}

	static final Weapon[] ALL = { new Weapon("Starfall halberd", QuestWeapons::halberdFrame, HALBERD),
			new Weapon("Rimecleaver", QuestWeapons::battleaxeFrame, BATTLEAXE),
			new Weapon("Hollow King's sceptre", QuestWeapons::maceFrame, MACE),
			new Weapon("Duskyew shortbow", QuestWeapons::shortbowFrame, () -> shortbowFrame(2.4), SHORTBOW),
			new Weapon("Staff of the caged star", QuestWeapons::staffFrame, STAFF) };

	/* ------------------------------------------------------------ vector bits */

	static double[] v(double x, double y, double z) {
		return new double[] { x, y, z };
	}

	static double[] add(double[] a, double[] b) {
		return new double[] { a[0] + b[0], a[1] + b[1], a[2] + b[2] };
	}

	static double[] sub(double[] a, double[] b) {
		return new double[] { a[0] - b[0], a[1] - b[1], a[2] - b[2] };
	}

	static double[] mul(double[] a, double k) {
		return new double[] { a[0] * k, a[1] * k, a[2] * k };
	}

	static double dot(double[] a, double[] b) {
		return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
	}

	static double[] cross(double[] a, double[] b) {
		return new double[] { a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0] };
	}

	static double dist(double[] a, double[] b) {
		return Math.sqrt(dot(sub(a, b), sub(a, b)));
	}

	static double[] unit(double[] a) {
		return mul(a, 1 / Math.sqrt(dot(a, a)));
	}
}
