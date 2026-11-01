package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ArmourForge.add;
import static com.rs2.tools.mapgen.ArmourForge.scale;
import static com.rs2.tools.mapgen.ArmourForge.sub;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.rs2.tools.mapgen.ArmourSets.Style;

/**
 * The mantle base (Kind.MANTLE): a hooded cloak for casters, built from scratch on the mystic robe's rig. See NewBases.
 *
 * <ul>
 * <li>Hood: a deep cowl whose lip stands out round a shadowed face, its crown drooping back (not a pointed hat), its
 * skirt tucked down under the mantle. All on the skull label, so it turns and nods with the head.</li>
 * <li>Top: a short shoulder cape (pelerine) open down the front over a fitted tunic with a sash, a standing collar,
 * wide bell sleeves that hang from the forearm, and a long cape falling from the shoulder blades to the ankles.</li>
 * <li>Legs: wrapped leggings cross-bound below the knee, with a split apron (a flap before and behind each thigh) from
 * a belted hip-band.</li>
 * <li>Boots: soft wrapped shoes with upturned toes. Gloves: fingerless hand-wraps on long forearm cuffs.</li>
 * </ul>
 *
 * <p>Every piece is built per body from that body's identity kits (their label centroids are the joints), so it fits
 * the male and the female body. Animation labels: a ring round a joint takes the joint's own label (the shoulder ring
 * 21 / 25, the elbow 17 / 23, the knee 35 / 34 and the shin 37 / 31, the waist 29, the heel 47 / 48, the neck 2), so
 * the pivot the client takes for that joint (the centroid of every vertex with the label) stays on the joint. Every
 * other vertex takes a label that moves the same way but is never a pivot (see {@link #twin}): the bell sleeves the
 * forearm's 19 / 22, the thighs 43 / 44, the shins 82 / 83, the pelerine its nearest kit label. The long cape uses the
 * stock capes' own chain (10 at the shoulders, 9 at the waist, then 14, 13, 12 down to the hem, 11 / 16 / 15 between),
 * with its rows at the stock cape's heights, so the walk and run swing it back clear of the heels.
 */
final class BaseMantle {

	private BaseMantle() {
	}

	/** Right (-x) / left (+x) labels: [0] right, [1] left. */
	static final int[] SHOULDER = { 21, 25 }, ELBOW = { 17, 23 }, FOREARM = { 19, 22 }, HAND = { 27, 28 }, HIP = { 42, 40 }, THIGH = { 44, 43 },
			KNEE = { 34, 35 }, SHIN = { 31, 37 }, CALF = { 83, 82 }, ANKLE = { 32, 38 }, HEEL = { 48, 47 }, TOES = { 45, 46 };
	static final int SKULL = 1, NECK = 2, CHEST = 8, BELLY = 4, WAIST = 29, PELVIS = 30;
	/** The stock capes' chain: 10 at the shoulders (the cape's pivot), 9 at the waist, 14, 13 and 12 below; 11 / 16 / 15 between. */
	static final int CAPE_TOP = 10, CAPE_WAIST = 9, CAPE_THIGH = 14, CAPE_KNEE = 13, CAPE_HEM = 12, CAPE_BACK = 11, CAPE_HIP = 16, CAPE_SHIN = 15;
	/** Skin: the client paints it the player's own skin colour. */
	static final int SKIN = 4550;

	/**
	 * A label that moves as {@code l} does under the player's sequences but is never a pivot: the client pivots a joint
	 * on the centroid of every vertex carrying certain labels (the shoulder 21, the elbow 17, the hip 40, the knee
	 * 35 / 37, the waist 29, the heel 47...), so loose cloth labelled by its nearest kit vertex mustn't add to those.
	 * 0 (the root, which nothing moves) never goes on a new vertex.
	 */
	static int twin(int l) {
		switch (l) {
		case 2:
		case 94:
			return CHEST;
		case 3:
		case 49:
			return SKULL;
		case 21:
			return 20;
		case 25:
			return 26;
		case 17:
			return 18;
		case 23:
			return 24;
		case 29:
			return PELVIS;
		case 5:
			return BELLY;
		case 40:
		case 35:
			return 43;
		case 42:
		case 34:
			return 44;
		case 37:
			return 82;
		case 31:
			return 83;
		case 47:
			return 38;
		case 48:
			return 32;
		case 39:
			return 41;
		default:
			return l;
		}
	}

	/* ====================================================================== */
	/*                                 BODIES                                 */
	/* ====================================================================== */

	/** One body's joints and sizes, read from its identity kits (ArmourPreview.MALE / FEMALE) and its stock cape. */
	static final class Body {
		boolean female;
		ModelData head, torso, arms, hands, legs, feet;
		double headTop, headChin, headFront, headBack, headHW;
		double neckY, neckZ, neckR, waistY, torsoHW, chestZ, backZ;
		double shoulderTop, armOuter;
		/** Per side: joint centres and half-extents {x, z}. */
		double[][] shoulder = new double[2][], elbow = new double[2][], fore = new double[2][], wrist = new double[2][], hand = new double[2][];
		double[][] shoulderR = new double[2][], elbowR = new double[2][], handR = new double[2][];
		double handBottom;
		double legsTop, legsHW, hipsBackZ, hipJointY;
		double[][] knee = new double[2][], shin = new double[2][], ankle = new double[2][], heel = new double[2][];
		double[][] kneeR = new double[2][], shinR = new double[2][], ankleR = new double[2][];
		double soleY, toeZ, heelZ;
		/** The stock cape's rows: y of labels 10, 9, 14, 13, 12. */
		double[] capeY = new double[5];
	}

	private static final Map<Boolean, Body> BODIES = new HashMap<>();

	static synchronized Body body(ArmourForge af, boolean female) throws IOException {
		Body b = BODIES.get(female);
		if (b != null) {
			return b;
		}
		int[] kit = female ? ArmourPreview.FEMALE : ArmourPreview.MALE;
		b = new Body();
		b.female = female;
		b.head = af.read(kit[0]);
		b.torso = af.read(kit[2]);
		b.arms = af.read(kit[3]);
		b.hands = af.read(kit[4]);
		b.legs = af.read(kit[5]);
		b.feet = af.read(kit[6]);
		int[] hb = b.head.bounds();
		b.headTop = hb[1];
		b.headFront = hb[2];
		b.headBack = hb[5];
		b.headHW = hb[3];
		b.headChin = ext(b.head, SKULL)[3];
		int[] tb = b.torso.bounds();
		b.neckY = tb[1];
		b.waistY = tb[4];
		b.torsoHW = Math.max(-tb[0], tb[3]);
		b.chestZ = tb[2];
		b.backZ = tb[5];
		double[] neck = cen(b.head, NECK);
		b.neckZ = neck[2];
		b.neckR = ext(b.head, NECK)[1];
		int[] ab = b.arms.bounds();
		b.shoulderTop = ab[1];
		b.armOuter = Math.max(-ab[0], ab[3]);
		b.handBottom = b.hands.bounds()[4];
		for (int s = 0; s < 2; s++) {
			b.shoulder[s] = cen(b.arms, SHOULDER[s]);
			b.shoulderR[s] = halfXZ(b.arms, SHOULDER[s]);
			b.elbow[s] = cen(b.arms, ELBOW[s]);
			b.elbowR[s] = halfXZ(b.arms, ELBOW[s]);
			b.fore[s] = cen(b.arms, FOREARM[s]);
			b.wrist[s] = cen(b.hands, FOREARM[s]);
			b.hand[s] = cen(b.hands, HAND[s]);
			b.handR[s] = halfXZ(b.hands, HAND[s]);
			b.knee[s] = cen(b.legs, KNEE[s]);
			b.kneeR[s] = halfXZ(b.legs, KNEE[s]);
			b.shin[s] = cen(b.legs, SHIN[s]);
			b.shinR[s] = halfXZ(b.legs, SHIN[s]);
			b.ankle[s] = cen(b.legs, ANKLE[s]);
			b.ankleR[s] = halfXZ(b.legs, ANKLE[s]);
			b.heel[s] = cen(b.feet, HEEL[s]);
		}
		int[] lb = b.legs.bounds();
		b.legsTop = lb[1];
		b.legsHW = Math.max(-lb[0], lb[3]);
		b.hipsBackZ = lb[5];
		b.hipJointY = cen(b.legs, HIP[1])[1];
		int[] fb = b.feet.bounds();
		b.soleY = fb[4];
		b.toeZ = fb[2];
		b.heelZ = fb[5];
		ModelData cape = af.read(female ? 481 : 323);
		int[] chain = { CAPE_TOP, CAPE_WAIST, CAPE_THIGH, CAPE_KNEE, CAPE_HEM };
		for (int i = 0; i < 5; i++) {
			b.capeY[i] = cen(cape, chain[i])[1];
		}
		BODIES.put(female, b);
		return b;
	}

	/** Centroid of the vertices with label {@code l} (also used by the set decorations to find the joints). */
	static double[] cen(ModelData m, int l) {
		double[] s = new double[3];
		int n = 0;
		for (int i = 0; i < m.vertices.size(); i++) {
			if (m.vertexSkins.get(i) == l) {
				int[] v = m.vertices.get(i);
				s[0] += v[0];
				s[1] += v[1];
				s[2] += v[2];
				n++;
			}
		}
		if (n == 0) {
			throw new IllegalStateException("no vertex with label " + l);
		}
		return scale(s, 1.0 / n);
	}

	/** {minX, maxX, minY, maxY, minZ, maxZ} of the vertices with label {@code l}. */
	static double[] ext(ModelData m, int l) {
		double[] e = { 1e9, -1e9, 1e9, -1e9, 1e9, -1e9 };
		for (int i = 0; i < m.vertices.size(); i++) {
			if (m.vertexSkins.get(i) == l) {
				int[] v = m.vertices.get(i);
				for (int k = 0; k < 3; k++) {
					e[k * 2] = Math.min(e[k * 2], v[k]);
					e[k * 2 + 1] = Math.max(e[k * 2 + 1], v[k]);
				}
			}
		}
		return e;
	}

	static double[] halfXZ(ModelData m, int l) {
		double[] e = ext(m, l);
		return new double[] { (e[1] - e[0]) / 2, (e[5] - e[4]) / 2 };
	}

	/* ====================================================================== */
	/*                                  MESH                                  */
	/* ====================================================================== */

	/** A model being built: vertices with their labels, faces wound to face out (or in, for linings). */
	static final class Mesh {
		final ModelData m = new ModelData();
		int prio;

		Mesh() {
			m.shading = 0;
		}

		int v(double[] p, int label) {
			m.skin = label;
			return m.vertex(p[0], p[1], p[2]);
		}

		int v(double x, double y, double z, int label) {
			return v(new double[] { x, y, z }, label);
		}

		/** A triangle facing away from {@code inside}. */
		void tri(int a, int b, int c, int col, double[] inside) {
			m.triOut(a, b, c, col, inside);
			m.priorities.set(m.priorities.size() - 1, prio);
		}

		void quad(int a, int b, int c, int d, int col, double[] inside) {
			tri(a, b, c, col, inside);
			tri(a, c, d, col, inside);
		}

		/** A triangle facing towards {@code toward} (a lining, seen from inside). */
		void triIn(int a, int b, int c, int col, double[] toward) {
			double[] g = scale(add(add(p(a), p(b)), p(c)), 1 / 3.0);
			tri(a, b, c, col, sub(scale(g, 2), toward));
		}

		void quadIn(int a, int b, int c, int d, int col, double[] toward) {
			triIn(a, b, c, col, toward);
			triIn(a, c, d, col, toward);
		}

		double[] p(int i) {
			int[] v = m.vertices.get(i);
			return new double[] { v[0], v[1], v[2] };
		}

		/** Joins two rings of equal length (closed: the last joins the first) with quads facing away from {@code inside}. */
		void band(int[] a, int[] b, boolean closed, int col, double[] inside) {
			int n = a.length;
			for (int i = 0; i < (closed ? n : n - 1); i++) {
				int j = (i + 1) % n;
				quad(a[i], a[j], b[j], b[i], col, inside);
			}
		}

		void bandIn(int[] a, int[] b, boolean closed, int col, double[] toward) {
			int n = a.length;
			for (int i = 0; i < (closed ? n : n - 1); i++) {
				int j = (i + 1) % n;
				quadIn(a[i], a[j], b[j], b[i], col, toward);
			}
		}

		/** Closes a ring with a fan to a point. */
		void fan(int[] ring, double[] tip, int label, int col, double[] inside) {
			int c = v(tip, label);
			for (int i = 0; i < ring.length; i++) {
				tri(ring[i], ring[(i + 1) % ring.length], c, col, inside);
			}
		}

		/** The finished model: vertices no face uses are dropped (the client drops them when it merges the player). */
		ModelData done() {
			boolean[] used = new boolean[m.vertices.size()];
			for (int[] f : m.faces) {
				used[f[0]] = used[f[1]] = used[f[2]] = true;
			}
			int[] map = new int[used.length];
			ModelData o = new ModelData();
			for (int i = 0; i < used.length; i++) {
				if (used[i]) {
					if (m.vertexSkins.get(i) == 0) {
						throw new IllegalStateException("unlabelled vertex " + i);
					}
					map[i] = o.vertices.size();
					o.vertices.add(m.vertices.get(i).clone());
					o.vertexSkins.add(m.vertexSkins.get(i));
				}
			}
			for (int i = 0; i < m.faces.size(); i++) {
				int[] f = m.faces.get(i);
				o.faces.add(new int[] { map[f[0]], map[f[1]], map[f[2]] });
				o.colors.add(m.colors.get(i));
				o.renderTypes.add(m.renderTypes.get(i));
				o.priorities.add(m.priorities.get(i));
				o.alphas.add(0);
				o.faceSkins.add(0);
			}
			return o;
		}
	}

	/**
	 * A point on an ellipse round a vertical axis through (cx, y, cz): {@code th} in degrees from the front (-z) round
	 * through the left (+x) to the back (+z); half-widths rx, and rzf in front / rzb behind.
	 */
	static double[] ell(double cx, double y, double cz, double rx, double rzf, double rzb, double th) {
		double a = Math.toRadians(th), c = Math.cos(a);
		return new double[] { cx + Math.sin(a) * rx, y, cz - c * (c > 0 ? rzf : rzb) };
	}

	/** A closed ring of {@code n} labelled vertices on such an ellipse, the first at angle {@code th0}. */
	static int[] ring(Mesh me, double cx, double y, double cz, double rx, double rzf, double rzb, int n, double th0, int label) {
		int[] r = new int[n];
		for (int i = 0; i < n; i++) {
			r[i] = me.v(ell(cx, y, cz, rx, rzf, rzb, th0 + 360.0 * i / n), label);
		}
		return r;
	}

	/** The label of the nearest vertex of {@code ref} that has one, made safe by {@link #twin}. */
	static int nearest(ModelData ref, double[] p) {
		double best = Double.MAX_VALUE;
		int lab = CHEST;
		for (int i = 0; i < ref.vertices.size(); i++) {
			int l = ref.vertexSkins.get(i);
			if (l == 0) {
				continue;
			}
			int[] v = ref.vertices.get(i);
			double dx = v[0] - p[0], dy = v[1] - p[1], dz = v[2] - p[2], d = dx * dx + dy * dy + dz * dz;
			if (d < best) {
				best = d;
				lab = l;
			}
		}
		return twin(lab);
	}

	/** The ray from outside at angle th (degrees, as {@link #ell}) to the axis (0, y, cz): where it meets {@code kit}, or null. */
	static double[] hit(ModelData kit, double y, double cz, double th) {
		double a = Math.toRadians(th);
		double[] dir = { -Math.sin(a), 0, Math.cos(a) };
		return ArmourForge.surface(kit, new double[] { Math.sin(a) * 150, y, cz - Math.cos(a) * 150 }, dir);
	}

	/** A closed ring fitted round {@code kit} at height y: each vertex where the ray meets it, pushed out by {@code out}. */
	static int[] fitted(Mesh me, ModelData kit, double y, double cz, int n, double out, double minR, double flare, int label) {
		int[] r = new int[n];
		for (int i = 0; i < n; i++) {
			double th = 360.0 * i / n, a = Math.toRadians(th);
			double[] h = hit(kit, y, cz, th);
			double rad = h == null ? minR : Math.max(minR, Math.hypot(h[0], h[2] - cz));
			rad = rad + out + flare;
			r[i] = me.v(Math.sin(a) * rad, y, cz - Math.cos(a) * rad, label);
		}
		return r;
	}

	static double lerp(double a, double b, double t) {
		return a + (b - a) * t;
	}

	static double[] lerp(double[] a, double[] b, double t) {
		return new double[] { lerp(a[0], b[0], t), lerp(a[1], b[1], t), lerp(a[2], b[2], t) };
	}

	/** Mirrors x for the right side (s = 0). */
	static double sx(int s) {
		return s == 0 ? -1 : 1;
	}

	/** The hood's shadow: the set's darkest cloth, near black. */
	static int shadow(Style st) {
		return st.dark & ~127 | Math.min(st.dark & 127, 7);
	}

	/* ====================================================================== */
	/*                                 PIECES                                 */
	/* ====================================================================== */

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES): the mantle's own mesh, on its own (no second model). */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		Body b = body(af, female);
		switch (slot) {
		case ArmourSets.HELM:
			return new ModelData[] { hood(st, b), null };
		case ArmourSets.BODY:
			return new ModelData[] { mantle(st, b), null };
		case ArmourSets.LEGS:
			return new ModelData[] { leggings(st, b), null };
		case ArmourSets.BOOTS:
			return new ModelData[] { shoes(st, b), null };
		case ArmourSets.GLOVES:
			return new ModelData[] { wraps(st, b), null };
		default:
			return new ModelData[] { donor, donorSecond };
		}
	}

	/* ------------------------------------------------------------- the hood */

	/**
	 * The cowl, designed round the male head (top -196, chin -172, face -20, back 4, half-width 11) and moved onto the
	 * body's own head keeping its margins: rings from a crown drooping back, out round the head, down to a skirt that
	 * tucks under the pelerine. The front is open from brow to chin; a thick lip runs round the opening and a recessed
	 * shadow fills it (the hood hides the head: the face is in shadow, the beard shows below it).
	 */
	static ModelData hood(Style st, Body b) {
		Mesh me = new Mesh();
		double hs = (b.headChin - b.headTop) / 24.0, ds = (b.headBack - b.headFront) / 24.0;
		// y, z centre, half-width, half-depth (male design)
		double[][] rings = { { -205.5, 5, 3, 3 }, { -202.5, -2.5, 11, 12 }, { -195.5, -6.5, 16, 16.5 }, { -185, -6, 17.5, 18.5 }, { -173.5, -5, 17, 18 },
				{ -165, -2, 15, 15 }, { -159, 0.5, 14.5, 14 } };
		double[] ang = { 0, 38, 72, 108, 144, 180, 216, 252, 288, 322 };
		int n = ang.length, R = rings.length;
		int[][] g = new int[R][n];
		double[][] cen = new double[R][];
		for (int r = 0; r < R; r++) {
			double y = b.headTop + (rings[r][0] + 196) * hs, cz = b.headFront + (rings[r][1] + 20) * ds;
			double rx = b.headHW + rings[r][2] - 11, rz = (b.headBack - b.headFront) / 2 + rings[r][3] - 12;
			if (r == 0) {
				rx = rings[0][2];
				rz = rings[0][3];
			}
			if (r == R - 1) { // the skirt: wide enough to go round the collar, under the pelerine
				rx = Math.max(rx, b.neckR + 7.5);
				rz = Math.max(rz, b.neckR + 7);
				y = Math.max(y, b.neckY + 4);
			}
			cen[r] = new double[] { 0, y, cz };
			for (int i = 0; i < n; i++) {
				g[r][i] = me.v(ell(0, y, cz, rx, rz, rz, ang[i]), SKULL);
			}
		}
		// the opening's outline: a pointed brow, narrowing to the chin
		move(me, g[2][0], 0, -4.5 * hs, -1.5);
		for (int i : new int[] { 1, 9 }) {
			scaleX(me, g[2][i], 0.8);
			move(me, g[2][i], 0, 2.5 * hs, 0);
			scaleX(me, g[4][i], 0.75);
			move(me, g[4][i], 0, -1, 0);
		}
		move(me, g[4][0], 0, 2 * hs, 0);
		// the shell, open at the front over the face (bands 2-3 and 3-4, sectors -38..38)
		for (int r = 0; r + 1 < R; r++) {
			double[] in = lerp(cen[r], cen[r + 1], 0.5);
			for (int i = 0; i < n; i++) {
				int j = (i + 1) % n;
				boolean open = (r == 2 || r == 3) && (i == 0 || j == 0);
				if (!open) {
					me.quad(g[r][i], g[r][j], g[r + 1][j], g[r + 1][i], r >= R - 2 ? st.dark : st.main, in);
				}
			}
		}
		me.fan(new int[] { g[0][0], g[0][1], g[0][2], g[0][3], g[0][4], g[0][5], g[0][6], g[0][7], g[0][8], g[0][9] }, add(cen[0], new double[] { 0, -1.5, 1 }),
				SKULL, st.main, add(cen[0], new double[] { 0, 6, 0 }));
		// the opening's rim (brow, sides, chin), as a loop, and the lip: its inner edge set back and in
		int[] loop = { g[2][9], g[2][0], g[2][1], g[3][1], g[4][1], g[4][0], g[4][9], g[3][9] };
		double[] mid = { 0, cen[3][1], cen[3][2] };
		int[] lip = new int[loop.length];
		for (int i = 0; i < loop.length; i++) {
			double[] p = me.p(loop[i]);
			double[] toward = sub(mid, p);
			toward[2] = 0;
			double l = Math.hypot(toward[0], toward[1]) + 1e-9;
			lip[i] = me.v(add(p, new double[] { toward[0] / l * 2.6, toward[1] / l * 2.6, 3.2 }), SKULL);
		}
		me.prio = 1;
		double[] front = { 0, cen[3][1], b.headFront - 40 };
		for (int i = 0; i < loop.length; i++) {
			int j = (i + 1) % loop.length;
			me.quadIn(loop[i], loop[j], lip[j], lip[i], st.trim, front);
		}
		// the shadow in the hood: a shallow bowl behind the lip
		double[] deep = { 0, lerp(cen[2][1], cen[4][1], 0.45), b.headFront + 7 * ds };
		int c = me.v(deep, SKULL);
		for (int i = 0; i < lip.length; i++) {
			me.triIn(lip[i], lip[(i + 1) % lip.length], c, shadow(st), front);
		}
		me.prio = 0;
		return me.done();
	}

	static void move(Mesh me, int i, double dx, double dy, double dz) {
		int[] v = me.m.vertices.get(i);
		v[0] = (int) Math.round(v[0] + dx);
		v[1] = (int) Math.round(v[1] + dy);
		v[2] = (int) Math.round(v[2] + dz);
	}

	static void scaleX(Mesh me, int i, double f) {
		int[] v = me.m.vertices.get(i);
		v[0] = (int) Math.round(v[0] * f);
	}

	/* ------------------------------------------------------------- the top */

	/** Tunic, sleeves, pelerine, collar and the long cape, all in one model (the decorations go on it). */
	static ModelData mantle(Style st, Body b) {
		Mesh me = new Mesh();
		tunic(me, st, b);
		for (int s = 0; s < 2; s++) {
			sleeve(me, st, b, s);
		}
		pelerine(me, st, b);
		cape(me, st, b);
		return me.done();
	}

	/** The fitted tunic: neck to below the waist, a trim down the front, a sash, a hem band. */
	static void tunic(Mesh me, Style st, Body b) {
		ModelData kit = ArmourForge.mergeAll(b.torso, b.legs);
		double neck = b.neckY, waist = b.waistY, h = waist - neck;
		double[] ys = { neck + 1.5, neck + h * 0.3, neck + h * 0.65, waist, waist + 9, waist + 12 };
		int n = 8;
		int[][] rg = new int[ys.length][];
		double cz = (b.chestZ + b.backZ) / 2;
		for (int r = 0; r < ys.length; r++) {
			int lab = r == 0 ? NECK : r == 1 ? CHEST : BELLY;
			double out = r == 0 ? 1.4 : r < 4 ? 1.3 : 2.0;
			rg[r] = fitted(me, kit, ys[r], cz, n, out, b.neckR, r >= 4 ? (r - 3) * 1.4 : 0, lab);
		}
		for (int r = 0; r + 1 < ys.length; r++) {
			me.band(rg[r], rg[r + 1], true, r == ys.length - 2 ? st.trim : st.light, new double[] { 0, (ys[r] + ys[r + 1]) / 2, cz });
		}
		// the trim down the front, raised on the tunic
		me.prio = 2;
		int[] l = new int[ys.length - 2], rr = new int[ys.length - 2];
		for (int r = 1; r < ys.length - 1; r++) {
			double[] f = me.p(rg[r][0]);
			int lab = me.m.vertexSkins.get(rg[r][0]);
			l[r - 1] = me.v(f[0] - 1.8, f[1], f[2] - 0.5, lab);
			rr[r - 1] = me.v(f[0] + 1.8, f[1], f[2] - 0.5, lab);
		}
		for (int r = 0; r + 1 < l.length; r++) {
			me.quad(l[r], rr[r], rr[r + 1], l[r + 1], st.trim, new double[] { 0, ys[r + 1], cz });
		}
		// the sash at the waist
		int[] s0 = fitted(me, kit, waist - 5, cz, n, 2.2, b.neckR, 0, BELLY), s1 = fitted(me, kit, waist + 0.5, cz, n, 2.4, b.neckR, 0, BELLY);
		me.band(s0, s1, true, st.trimDark, new double[] { 0, waist - 2, cz });
		me.prio = 0;
	}

	/**
	 * A sleeve: fitted from the shoulder (its ring on the shoulder label, round the shoulder joint) to the elbow (on the
	 * elbow label), then a bell on the forearm label opening round the wrist, its back corner hanging low.
	 */
	static void sleeve(Mesh me, Style st, Body b, int s) {
		double sc = b.female ? 0.85 : 1;
		int n = 8;
		double[] sh = b.shoulder[s], el = b.elbow[s], fo = b.fore[s];
		double[][] c = { sh, el, lerp(el, fo, 0.6), add(fo, new double[] { 0, 10 * sc, 0 }), add(fo, new double[] { 0, 13 * sc, 0 }) };
		double[][] r = { { b.shoulderR[s][0] + 1.6, b.shoulderR[s][1] + 1.6 }, { Math.max(b.elbowR[s][0] + 1.8, 5.4), Math.max(b.elbowR[s][1] + 1.8, 5.4) },
				{ 9 * sc, 9 * sc }, { 13 * sc, 13 * sc }, { 14 * sc, 14 * sc } };
		int[] labs = { SHOULDER[s], ELBOW[s], FOREARM[s], FOREARM[s], FOREARM[s] };
		double backOut = s == 0 ? 225 : 135; // the corner that hangs: behind and outside
		int[][] rg = new int[c.length][n];
		for (int k = 0; k < c.length; k++) {
			for (int i = 0; i < n; i++) {
				double th = 360.0 * i / n + 22.5;
				double[] p = ell(c[k][0], c[k][1], c[k][2], r[k][0], r[k][1], r[k][1], th);
				if (k >= 3) {
					double d = Math.cos(Math.toRadians(th - backOut));
					p[1] += 13 * sc * Math.pow(Math.max(0, d), 1.5);
				}
				rg[k][i] = me.v(p, labs[k]);
			}
		}
		for (int k = 0; k + 1 < c.length; k++) {
			me.band(rg[k], rg[k + 1], true, k == 3 ? st.trim : st.main, lerp(c[k], c[k + 1], 0.5));
		}
		// the lining inside the bell
		int[] in0 = new int[n], in1 = new int[n];
		for (int i = 0; i < n; i++) {
			in0[i] = me.v(lerp(c[2], me.p(rg[2][i]), 0.9), FOREARM[s]);
			in1[i] = me.v(lerp(c[4], me.p(rg[4][i]), 0.92), FOREARM[s]);
		}
		double[] axis = { c[3][0], c[3][1], c[3][2] };
		me.bandIn(in0, in1, true, st.dark, axis);
	}

	/**
	 * The pelerine: a short cape over the shoulders and upper arms, open down the front. Each vertex takes its nearest
	 * kit label (chest in the middle, the upper arm over the arm), so it rides the shoulders.
	 */
	static void pelerine(Mesh me, Style st, Body b) {
		ModelData ref = ArmourForge.mergeAll(b.torso, b.arms);
		double front = Math.max(-b.chestZ, 12) + 5, back = b.backZ + 6, hem = (b.elbow[0][1] + b.elbow[1][1]) / 2 - 9;
		double top = b.shoulderTop;
		// y, half-width, half-depth front, behind, front opening (degrees each side)
		double[][] rings = { { b.neckY - 1, b.neckR + 4, b.neckR + 3, b.neckR + 3, 20 }, { top, b.armOuter - 1.5, front - 3, back - 2.5, 23 },
				{ top + (hem - top) * 0.45, b.armOuter + 3.5, front - 1, back - 0.5, 27 }, { hem - 3, b.armOuter + 5, front, back, 31 },
				{ hem, b.armOuter + 5.5, front + 0.3, back + 0.3, 32 } };
		int cols = 8, R = rings.length;
		int[][] g = new int[R][cols + 3];
		for (int r = 0; r < R; r++) {
			double a = rings[r][4];
			for (int i = 0; i <= cols + 2; i++) {
				double th = i == 0 ? a : i == cols + 2 ? 360 - a : i == 1 ? a + 5 : i == cols + 1 ? 355 - a : a + 5 + (350 - 2 * a) * (i - 1) / cols;
				double[] p = ell(0, rings[r][0], 0, rings[r][1], rings[r][2], rings[r][3], th);
				g[r][i] = me.v(p, nearest(ref, p));
			}
		}
		for (int r = 0; r + 1 < R; r++) {
			double[] in = { 0, (rings[r][0] + rings[r + 1][0]) / 2, 0 };
			for (int i = 0; i <= cols + 1; i++) {
				boolean edge = i == 0 || i == cols + 1 || r == R - 2;
				me.quad(g[r][i], g[r][i + 1], g[r + 1][i + 1], g[r + 1][i], edge ? st.trim : st.main, in);
			}
		}
		// the lining, seen under the hem and inside the front edges
		int[] l0 = new int[cols + 3], l1 = new int[cols + 3];
		for (int i = 0; i <= cols + 2; i++) {
			double[] p0 = me.p(g[2][i]), p1 = me.p(g[R - 1][i]);
			double[] a0 = { 0, p0[1], 0 }, a1 = { 0, p1[1], 0 };
			p0 = lerp(a0, p0, 0.95);
			p1 = lerp(a1, p1, 0.96);
			l0[i] = me.v(p0, nearest(ref, p0));
			l1[i] = me.v(p1, nearest(ref, p1));
		}
		me.bandIn(l0, l1, false, st.dark, new double[] { 0, hem - 8, 0 });
		// the standing collar, open at the front, higher behind
		int cn = 7;
		int[] c0 = new int[cn], c1 = new int[cn];
		for (int i = 0; i < cn; i++) {
			double th = 22 + (360 - 44) * i / (cn - 1);
			double rise = 5 + 4.5 * (1 - Math.cos(Math.toRadians(th))) / 2;
			c0[i] = me.v(ell(0, b.neckY + 1, b.neckZ, b.neckR + 3, b.neckR + 2.5, b.neckR + 2.5, th), CHEST);
			c1[i] = me.v(ell(0, b.neckY + 1 - rise, b.neckZ, b.neckR + 4.5, b.neckR + 3.8, b.neckR + 3.8, th), CHEST);
		}
		double[] ax = { 0, b.neckY - 3, b.neckZ };
		me.band(c0, c1, false, st.main, ax);
		me.bandIn(c0, c1, false, st.dark, ax);
	}

	/**
	 * The long cape: from the shoulder blades (under the pelerine) to the ankles, flaring and falling away behind. Its
	 * rows are the stock cape's chain, at the stock cape's heights (the hem continues the last label down).
	 */
	static void cape(Mesh me, Style st, Body b) {
		double hem = b.soleY - 15;
		double top = Math.max(b.capeY[0], b.shoulderTop + 2);
		double hips = b.hipsBackZ;
		// y, label, z of the middle, half-width, z of the edges
		double[][] rows = { { top, CAPE_TOP, b.backZ + 2, b.torsoHW, b.backZ - 1.5 }, { b.capeY[1], CAPE_WAIST, hips + 5.5, b.torsoHW + 1, hips + 1.5 },
				{ (b.capeY[1] + b.capeY[2]) / 2, CAPE_HIP, hips + 8, b.legsHW + 5, hips + 2.5 }, { b.capeY[2], CAPE_THIGH, hips + 10, b.legsHW + 8, hips + 3 },
				{ b.capeY[3], CAPE_KNEE, hips + 12, b.legsHW + 11, hips + 4 }, { hem - 4, CAPE_HEM, hips + 15.5, b.legsHW + 14, hips + 5 },
				{ hem, CAPE_HEM, hips + 16, b.legsHW + 14.5, hips + 5 } };
		int cols = 6, R = rows.length;
		int[][] o = new int[R][cols], in = new int[R][cols];
		for (int r = 0; r < R; r++) {
			for (int i = 0; i < cols; i++) {
				double u = -1 + 2.0 * i / (cols - 1);
				double z = rows[r][2] - (rows[r][2] - rows[r][4]) * u * u;
				int lab = (int) rows[r][1];
				o[r][i] = me.v(u * rows[r][3], rows[r][0], z, lab);
				in[r][i] = me.v(u * rows[r][3] * 0.985, rows[r][0], z - 0.9, lab);
			}
		}
		for (int r = 0; r + 1 < R; r++) {
			double[] body = { 0, (rows[r][0] + rows[r + 1][0]) / 2, -40 };
			me.band(o[r], o[r + 1], false, r == R - 2 ? st.trim : st.main, body);
			if (r > 0) { // the top band's inside is hidden by the back
				me.bandIn(in[r], in[r + 1], false, st.dark, body);
			}
		}
	}

	/* ------------------------------------------------------------- the legs */

	/** Leggings fitted to each leg and cross-bound below the knee, a belted hip-band, and a flap before and behind each thigh. */
	static ModelData leggings(Style st, Body b) {
		Mesh me = new Mesh();
		int[] lb = b.legs.bounds();
		double cz = (lb[2] + lb[5]) / 2.0;
		double waist = b.legsTop + 1;
		int n = 12;
		int[] w0 = fitted(me, b.legs, waist, cz, n, 1.8, 8, 0, WAIST), w1 = fitted(me, b.legs, waist + 4.5, cz, n, 1.9, 8, 0, PELVIS);
		int[] w2 = fitted(me, b.legs, b.hipJointY + 3, cz, n, 1.7, 8, 0, PELVIS);
		me.band(w0, w1, true, st.leather, new double[] { 0, waist + 2, cz });
		me.band(w1, w2, true, st.dark, new double[] { 0, waist + 8, cz });
		for (int s = 0; s < 2; s++) {
			leg(me, st, b, s);
		}
		return me.done();
	}

	/** The leg's rings: y, centre x, centre z, half-width, half-depth, label. */
	static double[][] legRings(Body b, int s) {
		double sg = sx(s);
		double[] k = b.knee[s], sh = b.shin[s], an = b.ankle[s];
		double hipX = sg * b.legsHW / 2, top = b.hipJointY - 2;
		double kx = k[0], ky = k[1];
		return new double[][] { { top, (hipX + kx) / 2, 1, b.legsHW / 2 + 1.2, 9.5, THIGH[s] }, { lerp(top, ky, 0.5), lerp(hipX, kx, 0.6), 0, lerp(b.legsHW / 2 + 1.2, b.kneeR[s][0] + 1.4, 0.5), 9.2, THIGH[s] },
				{ ky, kx, k[2], b.kneeR[s][0] + 1.4, b.kneeR[s][1] + 1.3, KNEE[s] }, { sh[1], sh[0], sh[2], b.shinR[s][0] + 1.2, b.shinR[s][1] + 1.2, SHIN[s] },
				{ lerp(sh[1], an[1], 0.5), lerp(sh[0], an[0], 0.5), lerp(sh[2], an[2], 0.5), lerp(b.shinR[s][0], b.ankleR[s][0], 0.6) + 1.3,
						lerp(b.shinR[s][1], b.ankleR[s][1], 0.5) + 1.3, CALF[s] },
				{ an[1] - 2, an[0], an[2], b.ankleR[s][0] + 1.6, b.ankleR[s][1] + 1.5, ANKLE[s] } };
	}

	/** The leg at height y: {cx, cz, rx, rz}, between its rings. */
	static double[] legAt(double[][] rr, double y) {
		for (int i = 0; i + 1 < rr.length; i++) {
			if (y <= rr[i + 1][0] || i + 2 == rr.length) {
				double t = Math.max(0, Math.min(1, (y - rr[i][0]) / (rr[i + 1][0] - rr[i][0])));
				return new double[] { lerp(rr[i][1], rr[i + 1][1], t), lerp(rr[i][2], rr[i + 1][2], t), lerp(rr[i][3], rr[i + 1][3], t), lerp(rr[i][4], rr[i + 1][4], t) };
			}
		}
		return null;
	}

	static void leg(Mesh me, Style st, Body b, int s) {
		double sg = sx(s);
		double[][] rr = legRings(b, s);
		int n = 8;
		int[][] g = new int[rr.length][n];
		for (int k = 0; k < rr.length; k++) {
			for (int i = 0; i < n; i++) {
				double th = 360.0 * i / n;
				// the hip ring's outer side carries the hip joint's own label (the thigh's pivot)
				int lab = (int) rr[k][5];
				if (k == 0 && Math.abs(th - (s == 0 ? 270 : 90)) <= 45) {
					lab = HIP[s];
				}
				g[k][i] = me.v(ell(rr[k][1], rr[k][0], rr[k][2], rr[k][3], rr[k][4], rr[k][4], th), lab);
			}
		}
		for (int k = 0; k + 1 < rr.length; k++) {
			me.band(g[k], g[k + 1], true, st.dark, new double[] { (rr[k][1] + rr[k + 1][1]) / 2, (rr[k][0] + rr[k + 1][0]) / 2, (rr[k][2] + rr[k + 1][2]) / 2 });
		}
		// the cross-binding: two strips wound opposite ways from under the knee to the ankle
		me.prio = 2;
		double y0 = rr[2][0] + 5, y1 = rr[5][0] - 1.5;
		int seg = 7;
		for (int hx = 0; hx < 2; hx++) {
			double dir = hx == 0 ? 1 : -1;
			int[] up = new int[seg + 1], dn = new int[seg + 1];
			double[][] cs = new double[seg + 1][];
			for (int i = 0; i <= seg; i++) {
				double t = (double) i / seg, y = lerp(y0, y1, t);
				double th = (s == 0 ? 0 : 180) + dir * 360 * 1.25 * t;
				double[] l = legAt(rr, y);
				cs[i] = new double[] { l[0], y, l[1] };
				double[] p = ell(l[0], y, l[1], l[2] + 0.7, l[3] + 0.7, l[3] + 0.7, th);
				up[i] = me.v(p[0], p[1] - 1.1, p[2], CALF[s]);
				dn[i] = me.v(p[0], p[1] + 1.1, p[2], CALF[s]);
			}
			for (int i = 0; i < seg; i++) {
				me.quad(up[i], up[i + 1], dn[i + 1], dn[i], hx == 0 ? st.leather : st.trim, lerp(cs[i], cs[i + 1], 0.5));
			}
		}
		me.prio = 0;
		// the flaps, before and behind the thigh
		double ftop = b.legsTop + 6, fmid = rr[1][0], fbot = rr[2][0] - 7;
		for (int fb = 0; fb < 2; fb++) {
			double base = fb == 0 ? 0 : 180;
			double[] spread = { -48, -16, 16, 48 };
			double[] ys = { ftop, fmid, fbot - 2.5, fbot };
			int[][] f = new int[ys.length][spread.length], fl = new int[ys.length][spread.length];
			for (int r = 0; r < ys.length; r++) {
				double[] l = legAt(rr, Math.max(ys[r], rr[0][0]));
				double grow = r == 0 ? 3.2 : 2.4 + r * 0.7;
				for (int i = 0; i < spread.length; i++) {
					double th = base + spread[i] * (fb == 0 ? sg : -sg);
					double[] p = ell(l[0], ys[r], l[1], l[2] + grow, l[3] + grow, l[3] + grow, th);
					int lab = r == 0 ? PELVIS : THIGH[s];
					f[r][i] = me.v(p, lab);
					double[] q = ell(l[0], ys[r], l[1], l[2] + grow - 0.8, l[3] + grow - 0.8, l[3] + grow - 0.8, th);
					fl[r][i] = me.v(q, lab);
				}
			}
			for (int r = 0; r + 1 < ys.length; r++) {
				double[] l = legAt(rr, Math.max(ys[r], rr[0][0]));
				double[] ax = { l[0], (ys[r] + ys[r + 1]) / 2, l[1] };
				me.band(f[r], f[r + 1], false, r == ys.length - 2 ? st.trim : st.main, ax);
				if (r >= 2) {
					me.bandIn(fl[1], fl[r + 1], false, st.dark, ax);
				}
			}
		}
	}

	/* ------------------------------------------------------------- shoes, wraps */

	/** Soft shoes: a shaft wrapped twice round the ankle, a rounded foot and a toe that turns up to a point. */
	static ModelData shoes(Style st, Body b) {
		Mesh me = new Mesh();
		for (int s = 0; s < 2; s++) {
			double[] an = b.ankle[s];
			double fx = an[0], fz = an[2] + 0.5, ay = an[1];
			int n = 6;
			// the shaft
			double[][] sh = { { ay - 8, 5.6, 6.4 }, { ay, 5.0, 5.8 }, { -2, 5.6, 7.2 } };
			int[][] sr = new int[sh.length][];
			for (int k = 0; k < sh.length; k++) {
				sr[k] = ring(me, fx, sh[k][0], fz, sh[k][1], sh[k][2], sh[k][2], n, 30, ANKLE[s]);
			}
			for (int k = 0; k + 1 < sh.length; k++) {
				me.band(sr[k], sr[k + 1], true, st.leatherDark, new double[] { fx, (sh[k][0] + sh[k + 1][0]) / 2, fz });
			}
			// the foot, along z: heel, middle (the heel label, the toes' pivot), forefoot, toe; then the turned-up point
			double toe = b.toeZ, sole = b.soleY;
			double[][] ft = { { b.heelZ - 1, sole - 6.5, 5.2, 6 }, { b.heel[s][2], sole - 6, 6.0, 6.2 }, { lerp(b.heel[s][2], toe, 0.55), sole - 4.4, 5.8, 4.4 },
					{ toe + 1.5, sole - 3, 4.4, 3.0 } };
			int[] labs = { ANKLE[s], HEEL[s], TOES[s], TOES[s] };
			int[][] fr = new int[ft.length][n];
			for (int k = 0; k < ft.length; k++) {
				for (int i = 0; i < n; i++) {
					double a = Math.PI * 2 * (i + 0.5) / n;
					fr[k][i] = me.v(fx + Math.cos(a) * ft[k][2], ft[k][1] + Math.sin(a) * ft[k][3], ft[k][0], labs[k]);
				}
			}
			for (int k = 0; k + 1 < ft.length; k++) {
				me.band(fr[k], fr[k + 1], true, st.leatherDark, new double[] { fx, (ft[k][1] + ft[k + 1][1]) / 2, (ft[k][0] + ft[k + 1][0]) / 2 });
			}
			me.fan(fr[0], new double[] { fx, ft[0][1], ft[0][0] + 1.2 }, ANKLE[s], st.leatherDark, new double[] { fx, ft[0][1], ft[0][0] - 5 });
			me.fan(fr[ft.length - 1], new double[] { fx, sole - 7.5, toe - 5.5 }, TOES[s], st.trim, new double[] { fx, ft[3][1], ft[3][0] + 5 });
			// the wraps: two bands round the shaft and a strap over the instep
			me.prio = 2;
			for (double y : new double[] { ay - 4.5 }) {
				double t = (y - sh[0][0]) / (sh[2][0] - sh[0][0]);
				double rx = lerp(sh[0][1], sh[2][1], t) + 0.8, rz = lerp(sh[0][2], sh[2][2], t) + 0.8;
				int[] a = ring(me, fx, y - 1.3, fz, rx, rz, rz, n, 30, ANKLE[s]), c = ring(me, fx, y + 1.3, fz, rx + 0.2, rz + 0.2, rz + 0.2, n, 30, ANKLE[s]);
				me.band(a, c, true, st.leather, new double[] { fx, y, fz });
			}
			me.prio = 0;
		}
		return me.done();
	}

	/** Fingerless wraps: a long cuff up the forearm, a wrapped band at the wrist, wrapped palms, bare fingertips and thumb. */
	static ModelData wraps(Style st, Body b) {
		Mesh me = new Mesh();
		for (int s = 0; s < 2; s++) {
			double sg = sx(s);
			double[] el = b.elbow[s], fo = b.fore[s], wr = b.wrist[s], hd = b.hand[s];
			double bottom = b.handBottom, hrx = b.handR[s][0], hrz = b.handR[s][1];
			int n = 6;
			double[] c0 = lerp(el, fo, 0.55), c2 = add(wr, new double[] { 0, 0.5, 0 });
			double[][] rings = { { c0[0], c0[1], c0[2], 6.6, 6.6 }, { c0[0], c0[1] + 3, c0[2], 6.3, 6.3 }, { c2[0], c2[1], c2[2], 4.6, 5.4 },
					{ hd[0] - sg * 0.5, bottom - 10.5, hd[2], hrx - 0.6, hrz + 0.6 }, { hd[0], bottom - 5, hd[2], hrx - 1.2, hrz + 0.4 },
					{ hd[0] + sg * 0.3, bottom - 0.8, hd[2], hrx - 2.0, hrz - 0.6 } };
			int[] labs = { FOREARM[s], FOREARM[s], FOREARM[s], HAND[s], HAND[s], HAND[s] };
			int[] cols = { st.trim, st.dark, st.trim, st.leather, SKIN };
			int[][] rg = new int[rings.length][];
			for (int k = 0; k < rings.length; k++) {
				rg[k] = ring(me, rings[k][0], rings[k][1], rings[k][2], rings[k][3], rings[k][4], rings[k][4], n, 30, labs[k]);
			}
			for (int k = 0; k + 1 < rings.length; k++) {
				me.band(rg[k], rg[k + 1], true, cols[k], new double[] { (rings[k][0] + rings[k + 1][0]) / 2, (rings[k][1] + rings[k + 1][1]) / 2, rings[k][2] });
			}
			double[] last = rings[rings.length - 1];
			me.fan(rg[rings.length - 1], new double[] { last[0], bottom + 0.6, last[2] }, HAND[s], SKIN, new double[] { last[0], last[1] - 4, last[2] });
			// the thumb, on the front of the hand (-z), towards the body
			double[] t0 = { hd[0] - sg * 1.5, bottom - 10, hd[2] - hrz + 1 }, t1 = { hd[0] - sg * 2.5, bottom - 4, hd[2] - hrz - 2.5 };
			ModelData th = new ModelData();
			th.tube(t0, t1, 1.9, 1.3, 4, Math.PI / 4, SKIN, SKIN);
			for (int i = 0; i < th.vertices.size(); i++) {
				th.vertexSkins.set(i, HAND[s]);
			}
			th.renderTypes.replaceAll(x -> 0);
			ArmourForge.merge(me.m, th, null);
		}
		return me.done();
	}
}
