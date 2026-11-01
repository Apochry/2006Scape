package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ArmourForge.add;
import static com.rs2.tools.mapgen.ArmourForge.dot;
import static com.rs2.tools.mapgen.ArmourForge.norm;
import static com.rs2.tools.mapgen.ArmourForge.scale;
import static com.rs2.tools.mapgen.ArmourForge.sub;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.rs2.tools.mapgen.ArmourForge.Frame;
import com.rs2.tools.mapgen.ArmourSets.Style;

/**
 * The coat base (Kind.COAT): an armoured long coat, built from scratch on the rune plate's rig. See NewBases.
 *
 * <p>The pieces: a kettle helm (a round iron cap with a drooping brim, open-faced, and a lamellar aventail hanging from
 * under the brim over the neck and nape); a knee-length coat of plates with a high stiff collar, a broad belt, big
 * rounded pauldrons with two lames each, leather sleeves, and a skirt of four overlapping lamellar bands split front and
 * back; armoured trousers (cuisses, knee cops, greaves) that show below the skirt; tall riding boots with a flared
 * cuff; gauntlets with long flared cuffs; and a round shield with a boss. Every piece is built round the identity-kit
 * body it is worn on (head, torso, arms, hands, legs, feet), so the male and the female versions each fit their own
 * body.
 *
 * <p>Labels. Each vertex gets its animation label as it is made, chosen so the stock animations move it with the
 * body part under it, and so it never moves a joint: the frames rotate each limb about the centroid of a few
 * "pivot" labels (shoulders 21 / 25, elbows 17 / 23, hands 27 / 28, hips 40 / 42, knees 34 / 35 / 31 / 37, the pelvis
 * 29, the neck 2 / 3, the feet 47 / 48), so new vertices use labels that move the same way without being pivots: the
 * upper arms 20 / 26 (pauldrons, sleeves), the forearms 19 / 22, the thighs 43 / 44 (the skirt halves of the rune
 * plateskirt: the coat's skirt and the trousers' thighs, so each half of the skirt swings with its leg and the slits
 * front and back open in the stride instead of tearing), the knee caps 33 / 36 (shins), the ankles 32 / 38 (boot
 * shafts), the toes 45 / 46, the chest 8, the belly 4 (also the belt and the top of the skirt: it moves exactly as the hip ring 5 does, but 5 is a pivot of the stock skeleton), the trousers' static waistband 41, and the skull 1 (the whole
 * helm). The donor's own pivot vertices are kept (without faces, gathered on their centroid), so every joint turns
 * about the same point as in the rune piece it replaces.
 */
final class BaseCoat {

	private BaseCoat() {
	}

	/** Labels the player's animations use as a rotation origin: kept from the donor, avoided in new geometry. */
	static final Set<Integer> PIVOTS = Set.of(0, 2, 3, 5, 17, 21, 23, 25, 27, 28, 29, 30, 31, 34, 35, 37, 39, 40, 42, 47, 48);

	static final double[] X = { 1, 0, 0 }, Y = { 0, 1, 0 }, Z = { 0, 0, 1 };

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		Rig r = rig(af, female);
		switch (slot) {
		case ArmourSets.HELM:
			return new ModelData[] { keep(helm(r, st), donor), null };
		case ArmourSets.BODY:
			return new ModelData[] { keep(coat(r, st), donor), keep(sleeves(r, st), donorSecond) };
		case ArmourSets.LEGS:
			return new ModelData[] { keep(legs(r, st), donor), null };
		case ArmourSets.SHIELD:
			return new ModelData[] { shield(af, st), null };
		case ArmourSets.BOOTS:
			return new ModelData[] { keep(boots(r, st), donor), null };
		default:
			return new ModelData[] { keep(gauntlets(r, st), donor), null };
		}
	}

	/**
	 * Appends, for each pivot label of the donor, as many vertices as the donor has on it, all at their centroid (no
	 * faces): the composite player model's centroid of every such label, so every joint's centre of rotation, stays
	 * exactly what the rune piece gives it. Gathered on the joint's centre (inside the limb) rather than spread over the
	 * donor's surface, they're never the nearest vertex to a decoration attached on top, so no decoration takes a pivot
	 * label from them, and they don't stretch the piece's bounds.
	 */
	static ModelData keep(ModelData m, ModelData donor) {
		if (donor == null) {
			return m;
		}
		Map<Integer, double[]> sum = new java.util.TreeMap<>();
		for (int i = 0; i < donor.vertices.size(); i++) {
			int label = donor.vertexSkins.get(i);
			if (PIVOTS.contains(label)) {
				int[] v = donor.vertices.get(i);
				double[] a = sum.computeIfAbsent(label, k -> new double[4]);
				a[0] += v[0];
				a[1] += v[1];
				a[2] += v[2];
				a[3]++;
			}
		}
		for (Map.Entry<Integer, double[]> e : sum.entrySet()) {
			double[] a = e.getValue();
			int n = (int) a[3];
			// spread the remainder of the rounding over the copies, so their sum (and the centroid) is exact
			int[] lo = new int[3], extra = new int[3];
			for (int k = 0; k < 3; k++) {
				long total = Math.round(a[k]);
				lo[k] = (int) Math.floorDiv(total, n);
				extra[k] = (int) (total - (long) lo[k] * n);
			}
			for (int i = 0; i < n; i++) {
				m.vertices.add(new int[] { lo[0] + (i < extra[0] ? 1 : 0), lo[1] + (i < extra[1] ? 1 : 0), lo[2] + (i < extra[2] ? 1 : 0) });
				m.vertexSkins.add(e.getKey());
			}
		}
		return m;
	}

	/* ====================================================================== */
	/*                                THE BODY                                */
	/* ====================================================================== */

	/** Where the body's parts are, from the identity kits (the right side, x < 0; mirror for the left). */
	static final class Rig {
		ModelData head, torso, arms, hands, legs, feet;
		double neckY, waistY;
		double[] shoulder, elbow, wrist, hand, hip, thigh, knee, ankle;
		double armTop, wristY;
		int[] headB, feetB;
	}

	/** The two bodies' rigs, per forge (male, female). */
	private static final Map<ArmourForge, Rig[]> RIGS = java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

	/** Each generated mesh's landmarks, so a set's decorations can be placed on the coat's own shapes. */
	private static final Map<ModelData, Map<String, double[]>> MARKS = java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

	/**
	 * A landmark of a piece this class built (null if it has none): "torso" {neckY, waistY}, "chest" / "belly" {y, cz,
	 * rx, rz}, "belt" {top, bottom, cz, rx, rz}, "skirt" {top, hem, cz top, rx top, rz top, cz hem, rx hem, rz hem} (the
	 * outer lips), "lip0".."lip3" {y, cz, rx, rz, half the slit's angle} (each band's lower edge, lip3 the hem), "pauldron-1" / "pauldron1" {centre, axis, height, radius}; the helm's "brim" {y, cz, rx, rz, apexY,
	 * neckY}; the legs' "knee-1" / "knee1" {cop centre, cop axis}; the boots' "heel-1" / "heel1" {x, sole, back z, cuff
	 * y}; the gauntlets' "cuff-1" / "cuff1" {wrist, forearm axis, knuckle plate centre}; the shield's "shield" {centre,
	 * normal, down, across, radius}.
	 */
	static double[] mark(ModelData m, String key) {
		Map<String, double[]> k = MARKS.get(m);
		return k == null ? null : k.get(key);
	}


	static Rig rig(ArmourForge af, boolean female) throws IOException {
		Rig[] both = RIGS.computeIfAbsent(af, k -> new Rig[2]);
		Rig cached = both[female ? 1 : 0];
		if (cached != null) {
			return cached;
		}
		int[] k = female ? ArmourPreview.FEMALE : ArmourPreview.MALE;
		Rig r = new Rig();
		r.head = af.read(k[0]);
		r.torso = af.read(k[2]);
		r.arms = af.read(k[3]);
		r.hands = af.read(k[4]);
		r.legs = af.read(k[5]);
		r.feet = af.read(k[6]);
		int[] tb = r.torso.bounds();
		r.neckY = tb[1];
		r.waistY = tb[4];
		r.shoulder = centroid(r.arms, 21);
		r.armTop = extent(r.arms, 21)[1];
		r.elbow = centroid(r.arms, 17);
		r.wrist = centroid(r.arms, 19);
		r.wristY = extent(r.arms, 19)[4];
		r.hand = centroid(r.hands, 27);
		r.hip = centroid(r.legs, 42);
		r.thigh = centroid(r.legs, 34);
		r.knee = centroid(r.legs, 31);
		r.ankle = centroid(r.legs, 32);
		r.headB = r.head.bounds();
		r.feetB = r.feet.bounds();
		both[female ? 1 : 0] = r;
		return r;
	}

	static double[] centroid(ModelData m, int label) {
		double[] c = new double[3];
		int n = 0;
		for (int i = 0; i < m.vertices.size(); i++) {
			if (m.vertexSkins.get(i) == label) {
				int[] v = m.vertices.get(i);
				c[0] += v[0];
				c[1] += v[1];
				c[2] += v[2];
				n++;
			}
		}
		return scale(c, 1.0 / Math.max(1, n));
	}

	/** Bounds (minX, minY, minZ, maxX, maxY, maxZ) of a label's vertices. */
	static double[] extent(ModelData m, int label) {
		double[] e = { 1e9, 1e9, 1e9, -1e9, -1e9, -1e9 };
		for (int i = 0; i < m.vertices.size(); i++) {
			if (m.vertexSkins.get(i) == label) {
				int[] v = m.vertices.get(i);
				for (int a = 0; a < 3; a++) {
					e[a] = Math.min(e[a], v[a]);
					e[a + 3] = Math.max(e[a + 3], v[a]);
				}
			}
		}
		return e;
	}

	/**
	 * The cross-section of a kit mesh at height y, on one side (side -1 / 1: x of that sign; 0: both): {minX, maxX, minZ,
	 * maxZ}, from where its edges cross the plane (or its nearest vertices, if none do).
	 */
	static double[] slice(ModelData m, double y, int side) {
		double[] e = { 1e9, -1e9, 1e9, -1e9 };
		for (int[] f : m.faces) {
			for (int k = 0; k < 3; k++) {
				int[] a = m.vertices.get(f[k]), b = m.vertices.get(f[(k + 1) % 3]);
				if ((a[1] - y) * (b[1] - y) > 0 || a[1] == b[1]) {
					continue;
				}
				double t = (y - a[1]) / (double) (b[1] - a[1]);
				double x = a[0] + (b[0] - a[0]) * t, z = a[2] + (b[2] - a[2]) * t;
				if (side != 0 && x * side < -0.5) {
					continue;
				}
				e[0] = Math.min(e[0], x);
				e[1] = Math.max(e[1], x);
				e[2] = Math.min(e[2], z);
				e[3] = Math.max(e[3], z);
			}
		}
		if (e[0] > e[1]) { // nothing crosses: the nearest vertices
			double best = 1e9;
			for (int[] v : m.vertices) {
				if (side == 0 || v[0] * side >= -0.5) {
					best = Math.min(best, Math.abs(v[1] - y));
				}
			}
			for (int[] v : m.vertices) {
				if ((side == 0 || v[0] * side >= -0.5) && Math.abs(v[1] - y) <= best + 0.5) {
					e[0] = Math.min(e[0], v[0]);
					e[1] = Math.max(e[1], v[0]);
					e[2] = Math.min(e[2], v[2]);
					e[3] = Math.max(e[3], v[2]);
				}
			}
		}
		return e;
	}

	/* ====================================================================== */
	/*                              MESH BUILDING                             */
	/* ====================================================================== */

	/** A mesh under construction: each vertex takes its label as it is made, each face the current priority. */
	static final class Mesh {
		final ModelData d = new ModelData();
		int prio;

		Mesh() {
			d.shading = 0;
			MARKS.put(d, new HashMap<>());
		}

		/** Records a landmark of this piece for the set's decorations (see {@link #mark(ModelData, String)}). */
		void mark(String key, double... v) {
			MARKS.get(d).put(key, v);
		}

		int v(double[] p, int label) {
			d.skin = label;
			return d.vertex(p[0], p[1], p[2]);
		}

		double[] p(int i) {
			return ArmourForge.d(d.vertices.get(i));
		}

		/** A triangle facing {@code out} (the client culls back faces). */
		void tri(int a, int b, int c, int col, double[] out) {
			double[] A = p(a), n = ModelData.cross(sub(p(b), A), sub(p(c), A));
			if (dot(n, out) < 0) {
				int t = b;
				b = c;
				c = t;
			}
			d.tri(a, b, c, col);
			d.priorities.set(d.priorities.size() - 1, prio);
		}

		void quad(int a, int b, int c, int e, int col, double[] out) {
			tri(a, b, c, col, out);
			tri(a, c, e, col, out);
		}

		/** A quad facing away from the point {@code in}. */
		void quadAway(int a, int b, int c, int e, int col, double[] in) {
			double[] m = scale(add(add(p(a), p(b)), add(p(c), p(e))), 0.25);
			quad(a, b, c, e, col, sub(m, in));
		}

		void triAway(int a, int b, int c, int col, double[] in) {
			double[] m = scale(add(add(p(a), p(b)), p(c)), 1 / 3.0);
			tri(a, b, c, col, sub(m, in));
		}

		/** Quads between two rows of vertices (same length), facing away from {@code in}; col per span. */
		void band(int[] r0, int[] r1, boolean closed, int[] col, double[] in) {
			int n = r0.length, spans = closed ? n : n - 1;
			for (int i = 0; i < spans; i++) {
				int j = (i + 1) % n;
				quadAway(r0[i], r0[j], r1[j], r1[i], col[i % col.length], in);
			}
		}

		/** As band, but each quad faces along {@code out} (for flat bands like a brim's top). */
		void bandFacing(int[] r0, int[] r1, boolean closed, int col, double[] out) {
			int n = r0.length, spans = closed ? n : n - 1;
			for (int i = 0; i < spans; i++) {
				int j = (i + 1) % n;
				quad(r0[i], r0[j], r1[j], r1[i], col, out);
			}
		}
	}

	/** A point on an ellipse at height y: x = cx + rx sin(phi), z = cz - rz cos(phi): phi 0 at the front, pi/2 at the left (+x). */
	static double[] ell(double cx, double y, double cz, double rx, double rz, double phi) {
		return new double[] { cx + rx * Math.sin(phi), y, cz - rz * Math.cos(phi) };
	}

	/** A point round an axis through c (direction a): phi 0 towards the front (-z), pi/2 towards +x. */
	static double[] around(double[] c, double[] a, double rx, double rz, double phi) {
		double[] u = norm(sub(X, scale(a, dot(X, a)))), w = ModelData.cross(a, u);
		if (w[2] > 0) {
			w = scale(w, -1);
		}
		return add(c, add(scale(u, rx * Math.sin(phi)), scale(w, rz * Math.cos(phi))));
	}

	static double[] lerp(double[] a, double[] b, double t) {
		return add(a, scale(sub(b, a), t));
	}

	static double[] mirror(double[] p, int side) {
		return new double[] { Math.abs(p[0]) * side, p[1], p[2] };
	}

	static double[] pt(double x, double y, double z) {
		return new double[] { x, y, z };
	}

	/* ====================================================================== */
	/*                                THE COAT                                */
	/* ====================================================================== */

	/** One ring of the torso: centre z, half-width, half-depth at height y (the torso kit's section plus a margin). */
	static double[] torsoRing(Rig r, double y, double margin, double front) {
		double[] s = slice(r.torso, Math.max(r.neckY + 0.5, Math.min(r.waistY - 0.5, y)), 0);
		double rx = Math.max(-s[0], s[1]) + margin, zF = s[2] - margin - front, zB = s[3] + margin;
		return new double[] { (zF + zB) / 2, rx, (zB - zF) / 2 };
	}

	/**
	 * The coat: torso of plates over the chest, a high stiff collar, a broad belt, big rounded pauldrons, and a
	 * knee-length skirt of four lamellar bands, split at the front and back, each half on its thigh's label.
	 */
	static ModelData coat(Rig r, Style st) {
		Mesh m = new Mesh();
		int n = 8;
		double L = r.waistY - r.neckY;
		// ------------------------------------------------ the torso
		double[][] rows = { { r.neckY + 1.5, 2.6, 0, 8 }, { r.neckY + 6, 2.2, 0.5, 8 }, { r.neckY + L * 0.32, 2.6, 1.2, 8 },
				{ r.neckY + L * 0.66, 2.4, 0.6, 4 }, { r.waistY - 2, 2.0, 0, 4 } };
		int[][] ring = new int[rows.length][n];
		double[][] ctr = new double[rows.length][];
		double[][] dims = new double[rows.length][];
		for (int i = 0; i < rows.length; i++) {
			double[] t = torsoRing(r, rows[i][0], rows[i][1], rows[i][2]);
			if (i == 0) {
				t[1] = Math.min(t[1], 10.5); // the neck, not the shoulders
			}
			dims[i] = t;
			ctr[i] = pt(0, rows[i][0], t[0]);
			for (int j = 0; j < n; j++) {
				ring[i][j] = m.v(ell(0, rows[i][0], t[0], t[1], t[2], Math.PI * 2 * j / n), (int) rows[i][3]);
			}
		}
		m.prio = 3;
		for (int i = 0; i + 1 < rows.length; i++) {
			m.band(ring[i], ring[i + 1], true, new int[] { i == 0 ? st.dark : st.main }, lerp(ctr[i], ctr[i + 1], 0.5));
		}
		// the front closure: a raised strip down the middle from the collar to the belt
		int[][] strip = new int[rows.length - 1][2];
		for (int i = 1; i < rows.length; i++) {
			for (int e = 0; e < 2; e++) {
				double phi = (e == 0 ? -1 : 1) * 1.9 / dims[i][1];
				strip[i - 1][e] = m.v(ell(0, rows[i][0], dims[i][0], dims[i][1] + 0.8, dims[i][2] + 0.8, phi), (int) rows[i][3]);
			}
		}
		m.prio = 4;
		for (int i = 0; i + 1 < strip.length; i++) {
			m.quad(strip[i][0], strip[i][1], strip[i + 1][1], strip[i + 1][0], st.trim, pt(0, 0, -1));
		}

		// ------------------------------------------------ the belt
		double[] w = dims[rows.length - 1];
		m.mark("torso", r.neckY, r.waistY);
		m.mark("chest", rows[2][0], dims[2][0], dims[2][1], dims[2][2]);
		m.mark("belly", rows[3][0], dims[3][0], dims[3][1], dims[3][2]);
		double beltTop = r.waistY - 6.5, beltBot = r.waistY + 1.5;
		int[] b0 = new int[n], b1 = new int[n];
		for (int j = 0; j < n; j++) {
			double phi = Math.PI * 2 * j / n;
			b0[j] = m.v(ell(0, beltTop, w[0], w[1] + 1.4, w[2] + 1.4, phi), 4);
			b1[j] = m.v(ell(0, beltBot, w[0], w[1] + 1.8, w[2] + 1.8, phi), 4);
		}
		m.prio = 4;
		m.band(b0, b1, true, new int[] { st.leather }, pt(0, r.waistY, w[0]));
		double[] bf = ell(0, (beltTop + beltBot) / 2, w[0], w[1] + 1.6, w[2] + 1.6, 0);
		m.d.skin = 4;
		buckle(m, bf, st);

		// ------------------------------------------------ the skirt: four lamellar bands, split front and back
		double top = r.waistY - 1, hem = r.knee[1] - 3.5;
		m.mark("belt", beltTop, beltBot, w[0], w[1] + 1.6, w[2] + 1.6);
		double[] skT = skirtRing(r, w, top, top + 4, 0.06), skH = skirtRing(r, w, top, hem, 1);
		m.mark("skirt", top, hem, skT[0], skT[1] + 1.9, skT[2] + 1.9, skH[0], skH[1] + 1.9, skH[2] + 1.9);
		int tiers = 4, segs = 4;
		double H = (hem - top) / tiers;
		m.prio = 3;
		for (int side = -1; side <= 1; side += 2) {
			int label = side > 0 ? 43 : 44;
			for (int k = 0; k < tiers; k++) {
				double y0 = top + k * H - (k > 0 ? 3.5 : 0), y1 = top + (k + 1) * H, ym = y1 - 2.4;
				double[] ys = { y0, ym, y1 };
				double[] grow = { k == 0 ? 0 : -0.6, 1.2, 1.9 };
				int[][] row = new int[3][segs + 1];
				double[] lip = skirtRing(r, w, top, y1, (y1 - top) / (hem - top));
				m.mark("lip" + k, y1, lip[0], lip[1] + grow[2], lip[2] + grow[2], 0.035 + 0.09 * (y1 - top) / (hem - top));
				for (int q = 0; q < 3; q++) {
					double f = (ys[q] - top) / (hem - top);
					double gap = 0.035 + 0.09 * f;
					double[] sk = skirtRing(r, w, top, ys[q], f);
					int lab = k == 0 && q == 0 ? 4 : label;
					for (int j = 0; j <= segs; j++) {
						double phi = side * (gap + (Math.PI - 2 * gap) * j / segs);
						row[q][j] = m.v(ell(0, ys[q], sk[0], sk[1] + grow[q], sk[2] + grow[q], phi), lab);
					}
				}
				double[] in = pt(0, (y0 + y1) / 2, w[0]);
				int[] plate = { st.main };
				m.band(row[0], row[1], false, plate, in);
				m.band(row[1], row[2], false, new int[] { st.trim }, in);
			}
		}

		// ------------------------------------------------ the high collar, open at the front
		collar(m, r, st);

		// ------------------------------------------------ the pauldrons
		for (int side = -1; side <= 1; side += 2) {
			pauldron(m, r, st, side);
		}
		return m.d;
	}

	/**
	 * The skirt's section at height y ({cz, rx, rz}): the coat's waist flaring by {@code f} (0 at the belt, 1 at the
	 * hem), but never closer than a hand's breadth to the widest the legs (and the trousers over them) get above y.
	 */
	static double[] skirtRing(Rig r, double[] w, double top, double y, double f) {
		double front = w[0] - w[2] - 0.4 - 6.0 * f, back = w[0] + w[2] + 0.4 + 6.5 * f, rx = w[1] + 0.2 + 7.0 * f;
		for (double yy = top; yy <= Math.min(y, r.knee[1]); yy += 2) {
			double[] s = slice(r.legs, Math.max(r.waistY + 0.5, yy), 0);
			double need = 4.4 + 2.0 * f;
			rx = Math.max(rx, Math.max(-s[0], s[1]) + need);
			front = Math.min(front, s[2] - need);
			back = Math.max(back, s[3] + need);
		}
		return new double[] { (front + back) / 2, rx, (back - front) / 2 };
	}

	/** A square buckle at p, facing front. */
	static void buckle(Mesh m, double[] p, Style st) {
		int lab = m.d.skin;
		double s = 3.2;
		int[] o = new int[4], i = new int[4];
		double[][] c = { { -s, -s }, { s, -s }, { s, s }, { -s, s } };
		for (int k = 0; k < 4; k++) {
			o[k] = m.v(pt(p[0] + c[k][0], p[1] + c[k][1], p[2] - 0.8), lab);
			i[k] = m.v(pt(p[0] + c[k][0] * 0.45, p[1] + c[k][1] * 0.45, p[2] - 1.4), lab);
		}
		m.prio = 5;
		for (int k = 0; k < 4; k++) {
			int j = (k + 1) % 4;
			m.quad(o[k], o[j], i[j], i[k], st.trim, pt(0, 0, -1));
		}
		m.quad(i[0], i[1], i[2], i[3], st.trimDark, pt(0, 0, -1));
	}

	/** A stiff standing collar round the neck and nape, rising higher at the back, open over the throat. */
	static void collar(Mesh m, Rig r, Style st) {
		double[] s = slice(r.torso, r.neckY + 0.5, 0);
		double cz = (s[2] + s[3]) / 2, rx = Math.min(10.5, Math.max(-s[0], s[1])) + 1.6, rz = (s[3] - s[2]) / 2 + 2.2;
		int segs = 5;
		double a0 = Math.PI * 0.36;
		int[] ob = new int[segs + 1], ot = new int[segs + 1], ib = new int[segs + 1], it = new int[segs + 1];
		for (int j = 0; j <= segs; j++) {
			double phi = a0 + (Math.PI * 2 - 2 * a0) * j / segs;
			double rise = 10 + 3.5 * (-Math.cos(phi)); // 13.5 at the back, ~9 at the front corners
			double yb = r.neckY + 3, yt = r.neckY - rise;
			ob[j] = m.v(ell(0, yb, cz, rx + 1.4, rz + 1.4, phi), 8);
			ot[j] = m.v(ell(0, yt, cz + 0.8, rx + 4.2, rz + 3.6, phi), 8);
			ib[j] = m.v(ell(0, yb, cz, rx, rz, phi), 8);
			it[j] = m.v(ell(0, yt, cz + 0.8, rx + 2.8, rz + 2.2, phi), 8);
		}
		double[] axis = pt(0, r.neckY - 5, cz);
		m.prio = 4;
		m.band(ob, ot, false, new int[] { st.dark }, axis);
		for (int j = 0; j < segs; j++) { // the inside faces the neck
			double[] mid = scale(add(m.p(ib[j]), m.p(it[j + 1])), 0.5);
			m.quad(ib[j], ib[j + 1], it[j + 1], it[j], st.leatherDark, sub(pt(axis[0], mid[1], axis[2]), mid));
		}
		m.bandFacing(ot, it, false, st.trim, pt(0, -1, 0));
		for (int e : new int[] { 0, segs }) { // the cut edges at the front
			m.quad(ob[e], ot[e], it[e], ib[e], st.trim, e == 0 ? pt(1, 0, -1.5) : pt(-1, 0, -1.5));
		}
	}

	/** A big rounded pauldron: a dome on the shoulder and two lames below it round the upper arm (labels 20 / 26). */
	static void pauldron(Mesh m, Rig r, Style st, int side) {
		int label = side > 0 ? 26 : 20;
		double[] J = mirror(pt(r.shoulder[0], r.armTop + 5, r.shoulder[2]), side);
		double[] E = mirror(r.elbow, side);
		double[] A = norm(sub(E, J));
		double armR = Math.max(5, (extent(r.arms, 21)[3] - extent(r.arms, 21)[0]) / 2);
		// the dome
		double[] c = add(J, pt(side * 2.6, -2.5, 0));
		double[] axis = norm(pt(side * 0.55, -0.84, 0));
		double ra = armR + 4.4, rb = armR + 3.8, h = armR * 0.95 + 1;
		int sides = 6;
		double[][] prof = { { -1.8 / h, 1.05 }, { 0, 1.0 }, { 0.5, 0.9 }, { 0.84, 0.56 } };
		int[][] idx = new int[prof.length][sides];
		double[] e1 = Z, e2 = norm(ModelData.cross(axis, e1));
		for (int k = 0; k < prof.length; k++) {
			for (int s = 0; s < sides; s++) {
				double ang = Math.PI * 2 * s / sides + Math.PI / sides;
				double[] p = add(add(c, scale(axis, prof[k][0] * h)), add(scale(e1, Math.cos(ang) * ra * prof[k][1]), scale(e2, Math.sin(ang) * rb * prof[k][1])));
				idx[k][s] = m.v(p, label);
			}
		}
		int apex = m.v(add(c, scale(axis, h)), label);
		m.mark("pauldron" + side, c[0], c[1], c[2], axis[0], axis[1], axis[2], h, ra);
		m.prio = 10;
		int[] cols = { st.trim, st.main, st.light };
		for (int k = 0; k + 1 < prof.length; k++) {
			m.band(idx[k], idx[k + 1], true, new int[] { cols[k] }, add(c, scale(axis, -6)));
		}
		for (int s = 0; s < sides; s++) {
			m.triAway(idx[prof.length - 1][s], idx[prof.length - 1][(s + 1) % sides], apex, st.light, c);
		}
		// the lames: two bands round the outside of the upper arm, each a little lower and tucked under the one above
		for (int k = 0; k < 2; k++) {
			double t0 = 2.5 + k * 5.2, t1 = t0 + 7.0;
			double r0 = armR + 3.8 - k * 0.6, r1 = armR + 4.8 - k * 0.6;
			int segs = 3;
			double mid = side * Math.PI / 2, span = 1.9;
			int[][] row = new int[3][segs + 1];
			double[] ts = { t0, t1 - 2.0, t1 }, rs = { r0, r1 - 0.3, r1 };
			for (int q = 0; q < 3; q++) {
				double[] cc = add(J, scale(A, ts[q]));
				for (int j = 0; j <= segs; j++) {
					double phi = mid - span + 2 * span * j / segs;
					row[q][j] = m.v(around(cc, A, rs[q], rs[q] * 0.95, phi), label);
				}
			}
			double[] in = add(J, scale(A, (t0 + t1) / 2));
			m.band(row[0], row[1], false, new int[] { st.main }, in);
			m.band(row[1], row[2], false, new int[] { st.trim }, in);
		}
	}

	/** The sleeves (the body's second model): leather from the shoulder to the wrist, a trim band at the cuff. */
	static ModelData sleeves(Rig r, Style st) {
		Mesh m = new Mesh();
		m.prio = 10;
		int n = 5;
		for (int side = -1; side <= 1; side += 2) {
			int up = side > 0 ? 26 : 20, el = side > 0 ? 23 : 17, fo = side > 0 ? 22 : 19;
			double[] J = mirror(pt(r.shoulder[0], r.armTop + 4, r.shoulder[2]), side), E = mirror(r.elbow, side),
					W = mirror(pt(r.wrist[0], r.wristY, r.wrist[2]), side);
			double armR = Math.max(5, (extent(r.arms, 21)[3] - extent(r.arms, 21)[0]) / 2);
			double foreR = Math.max(4, (extent(r.arms, 19)[3] - extent(r.arms, 19)[0]) / 2);
			double[] Au = norm(sub(E, J)), Af = norm(sub(W, E));
			Object[][] rings = { { J, Au, armR + 0.6, up }, { lerp(J, E, 0.5), Au, armR + 0.2, up }, { E, norm(add(Au, Af)), foreR + 1.6, el },
					{ add(W, scale(Af, -3.0)), Af, foreR + 1.6, fo }, { W, Af, foreR + 2.2, fo } };
			int[][] idx = new int[rings.length][n];
			for (int k = 0; k < rings.length; k++) {
				double[] c = (double[]) rings[k][0], a = (double[]) rings[k][1];
				double rad = (double) rings[k][2];
				for (int j = 0; j < n; j++) {
					idx[k][j] = m.v(around(c, a, rad, rad, Math.PI * 2 * j / n + 0.3), (int) rings[k][3]);
				}
			}
			for (int k = 0; k + 1 < rings.length; k++) {
				m.band(idx[k], idx[k + 1], true, new int[] { k >= 3 ? st.trim : st.leather }, lerp((double[]) rings[k][0], (double[]) rings[k + 1][0], 0.5));
			}
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                                THE HELM                                */
	/* ====================================================================== */

	/**
	 * A kettle helm: a round iron cap over the skull, a broad brim drooping all round (narrowest over the face), and a
	 * two-tier lamellar aventail hanging from under the brim over the ears, nape and neck. The face is open, so it is
	 * worn over the head kit (the name must not hide it). All on the skull label 1.
	 */
	static ModelData helm(Rig r, Style st) {
		Mesh m = new Mesh();
		int[] hb = r.headB;
		double hTop = hb[1], hBot = hb[4], hh = hBot - hTop;
		double brimY = hTop + hh * 0.31;
		int n = 10;
		// the cap: rings from the brim up to the crown, each clearing the head kit's section with a margin
		double[] ys = { brimY, brimY - (brimY - hTop) * 0.5, brimY - (brimY - hTop) * 0.86 };
		double[] margin = { 2.0, 2.4, 2.4 };
		int[][] ring = new int[ys.length][n];
		double[][] dim = new double[ys.length][];
		for (int k = 0; k < ys.length; k++) {
			double[] s = slice(r.head, ys[k], 0);
			double rx = Math.max(-s[0], s[1]) + margin[k], zF = s[2] - margin[k], zB = s[3] + margin[k];
			if (k > 0) { // keep the cap round: no ring wider than the one below
				rx = Math.min(rx, dim[k - 1][1] * (k == 1 ? 0.97 : 0.8));
			}
			dim[k] = new double[] { (zF + zB) / 2, rx, (zB - zF) / 2 };
			if (k > 0) {
				dim[k][2] = Math.min(dim[k][2], dim[k - 1][2] * (k == 1 ? 0.97 : 0.8));
				dim[k][0] = dim[0][0] + (dim[k][0] - dim[0][0]) * 0.5;
			}
			for (int j = 0; j < n; j++) {
				ring[k][j] = m.v(ell(0, ys[k], dim[k][0], dim[k][1], dim[k][2], Math.PI * 2 * j / n), 1);
			}
		}
		double cz = dim[0][0];
		int apex = m.v(pt(0, hTop - 3, cz + 0.5), 1);
		m.mark("brim", brimY, cz, dim[0][1], dim[0][2], hTop - 3, r.neckY);
		m.prio = 6;
		m.band(ring[0], ring[1], true, new int[] { st.main }, pt(0, ys[1] + 4, cz));
		m.band(ring[1], ring[2], true, new int[] { st.main }, pt(0, ys[2] + 4, cz));
		for (int j = 0; j < n; j++) {
			m.triAway(ring[2][j], ring[2][(j + 1) % n], apex, st.light, pt(0, ys[2] + 6, cz));
		}
		// a raised band round the cap just above the brim
		int[] h0 = new int[n], h1 = new int[n];
		for (int j = 0; j < n; j++) {
			double phi = Math.PI * 2 * j / n;
			h0[j] = m.v(ell(0, brimY - 0.2, cz, dim[0][1] + 0.6, dim[0][2] + 0.6, phi), 1);
			h1[j] = m.v(ell(0, brimY - 3.0, cz, dim[0][1] * 0.985 + 0.5, dim[0][2] * 0.985 + 0.5, phi), 1);
		}
		m.prio = 7;
		m.band(h0, h1, true, new int[] { st.trim }, pt(0, brimY, cz));
		// the brim: top, edge, underside; narrow over the face, broad at the sides and back
		int[] it = new int[n], ot = new int[n], ob = new int[n], ib = new int[n];
		for (int j = 0; j < n; j++) {
			double phi = Math.PI * 2 * j / n;
			double wb = 5.6 - 2.2 * Math.cos(phi); // 3.4 at the front, 7.8 at the back
			double droop = 2.6 + 0.8 * -Math.cos(phi);
			it[j] = m.v(ell(0, brimY + 0.2, cz, dim[0][1] + 0.4, dim[0][2] + 0.4, phi), 1);
			ot[j] = m.v(ell(0, brimY + droop, cz, dim[0][1] + wb, dim[0][2] + wb, phi), 1);
			ob[j] = m.v(ell(0, brimY + droop + 1.3, cz, dim[0][1] + wb - 0.3, dim[0][2] + wb - 0.3, phi), 1);
			ib[j] = m.v(ell(0, brimY + 1.6, cz, dim[0][1] - 0.2, dim[0][2] - 0.2, phi), 1);
		}
		m.prio = 6;
		m.bandFacing(it, ot, true, st.dark, pt(0, -1, 0));
		m.band(ot, ob, true, new int[] { st.trim }, pt(0, brimY + 2, cz));
		m.bandFacing(ob, ib, true, st.trimDark, pt(0, 1, 0));
		// the aventail: two tiers of plates from under the brim down to the shoulders, round the sides and back
		int segs = 9;
		double a0 = Math.PI * 0.4;
		double base = r.neckY + 2.5;
		double[][] tierY = { { brimY + 1.0, r.neckY - 5.5 } };
		double[][] tierR = { { -0.6, -1.0 } };
		for (int t = 0; t < tierY.length; t++) {
			int[][] o = new int[3][segs + 1], in = new int[2][segs + 1];
			double y0 = tierY[t][0], y1 = tierY[t][1], ym = y1 - 2.2;
			double[] yy = { y0, ym, y1 };
			for (int q = 0; q < 3; q++) {
				double f = (yy[q] - y0) / (y1 - y0);
				double grow = tierR[t][0] + (tierR[t][1] - tierR[t][0]) * f;
				for (int j = 0; j <= segs; j++) {
					double phi = a0 + (Math.PI * 2 - 2 * a0) * j / segs;
					o[q][j] = m.v(ell(0, yy[q], cz + 1.0 * f + t, dim[0][1] + grow, dim[0][2] + grow, phi), 1);
					if (q != 1) {
						in[q / 2][j] = m.v(ell(0, yy[q], cz + 1.0 * f + t, dim[0][1] + grow - 0.9, dim[0][2] + grow - 0.9, phi), 1);
					}
				}
			}
			double[] ax = pt(0, (y0 + y1) / 2, cz);
			m.prio = 5;
			m.band(o[0], o[1], false, new int[] { st.dark, st.main }, ax);
			m.band(o[1], o[2], false, new int[] { st.trim }, ax);
			for (int j = 0; j < segs; j++) { // the lining, seen through the open face
				double[] mid = scale(add(m.p(in[0][j]), m.p(in[1][j + 1])), 0.5);
				m.quad(in[0][j], in[0][j + 1], in[1][j + 1], in[1][j], st.leatherDark, sub(pt(ax[0], mid[1], ax[2]), mid));
			}
			for (int e : new int[] { 0, segs }) { // the front edges
				m.quad(in[0][e], o[0][e], o[2][e], in[1][e], st.trimDark, pt(e == 0 ? 1 : -1, 0, -2));
			}
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                                THE LEGS                                */
	/* ====================================================================== */

	/** One leg's ring at height y: {cx, cz, rx, rz} from the legs kit's section on that side, plus a margin. */
	static double[] legRing(Rig r, double y, int side, double margin) {
		double[] s = slice(r.legs, y, side);
		double cx = (s[0] + s[1]) / 2, cz = (s[2] + s[3]) / 2;
		return new double[] { cx, cz, (s[1] - s[0]) / 2 + margin, (s[3] - s[2]) / 2 + margin };
	}

	/**
	 * Armoured trousers: leather legs (thighs on 43 / 44, shins on the knee-cap labels 33 / 36, ankles on 32 / 38, the
	 * waistband on the static belt label 41), a plate cuisse down the front of each thigh, a knee cop and a greave.
	 */
	static ModelData legs(Rig r, Style st) {
		Mesh m = new Mesh();
		int n = 6;
		for (int side = -1; side <= 1; side += 2) {
			int thigh = side > 0 ? 43 : 44, shin = side > 0 ? 36 : 33, ank = side > 0 ? 38 : 32;
			double[][] rows = { { r.waistY + 3, 1.2, 41 }, { r.hip[1] + 1, 1.4, thigh }, { (r.hip[1] + r.thigh[1]) / 2, 1.4, thigh }, { r.thigh[1] - 1, 1.3, thigh },
					{ r.knee[1] + 1, 1.3, shin }, { (r.knee[1] + r.ankle[1]) / 2, 1.2, shin }, { r.ankle[1] + 1, 1.1, ank } };
			int[][] idx = new int[rows.length][n];
			double[][] lr = new double[rows.length][];
			for (int k = 0; k < rows.length; k++) {
				double[] q = legRing(r, rows[k][0], side, rows[k][1]);
				if (k == 0) { // the top of the leg: from the middle to the hip
					q = legRing(r, rows[k][0], side, rows[k][1]);
					q[0] = side * Math.max(Math.abs(q[0]), 8.5);
					q[2] = Math.max(q[2], 9.5);
				}
				lr[k] = q;
				for (int j = 0; j < n; j++) {
					idx[k][j] = m.v(ell(q[0], rows[k][0], q[1], q[2], q[3], Math.PI * 2 * j / n), (int) rows[k][2]);
				}
			}
			m.prio = 1;
			for (int k = 0; k + 1 < rows.length; k++) {
				double[] in = pt((lr[k][0] + lr[k + 1][0]) / 2, (rows[k][0] + rows[k + 1][0]) / 2, (lr[k][1] + lr[k + 1][1]) / 2);
				m.band(idx[k], idx[k + 1], true, new int[] { st.leather }, in);
			}
			// the cuisse: a plate over the front and outside of the thigh, two lames
			m.prio = 1;
			plateArc(m, r, side, r.hip[1] + 4, r.thigh[1] - 3, 1.4, thigh, st.main, st.trim, 2);
			// the greave down the shin
			plateArc(m, r, side, r.knee[1] + 4, r.ankle[1] - 2, 0.9, shin, st.main, st.trimDark, 1);
			// the knee cop
			kneeCop(m, r, side, shin, st);
		}
		// the waistband round both legs
		int wn = 10;
		double[] s = slice(r.legs, r.waistY + 2, 0);
		double cz = (s[2] + s[3]) / 2, rx = Math.max(-s[0], s[1]) + 2.0, rz = (s[3] - s[2]) / 2 + 2.0;
		int[] w0 = new int[wn], w1 = new int[wn];
		for (int j = 0; j < wn; j++) {
			double phi = Math.PI * 2 * j / wn;
			w0[j] = m.v(ell(0, r.waistY - 1.5, cz, rx, rz, phi), 41);
			w1[j] = m.v(ell(0, r.waistY + 5, cz, rx + 0.6, rz + 0.6, phi), 41);
		}
		m.prio = 2;
		m.band(w0, w1, true, new int[] { st.leatherDark }, pt(0, r.waistY + 2, cz));
		return m.d;
	}

	/**
	 * A curved plate over the front of one leg from y0 to y1 (rows lames, the lower edge of each in the second colour):
	 * offset {@code out} from the leg, spanning the front and turned a little round the outside.
	 */
	static void plateArc(Mesh m, Rig r, int side, double y0, double y1, double out, int label, int col, int edge, int lames) {
		int segs = 4;
		double span = 1.25;
		double mid = side * 0.35; // turned a little towards the outside of the leg
		double lh = (y1 - y0) / lames;
		for (int l = 0; l < lames; l++) {
			double a = y0 + l * lh - (l > 0 ? 1.5 : 0), b = y0 + (l + 1) * lh;
			double[] ys = { a, b - 1.8, b };
			double[] grow = { 0, 0.3, 0.5 };
			int[][] row = new int[3][segs + 1];
			for (int q = 0; q < 3; q++) {
				double[] lr = legRing(r, ys[q], side, out + grow[q] + l * 0.3);
				for (int j = 0; j <= segs; j++) {
					double phi = mid - span + 2 * span * j / segs;
					row[q][j] = m.v(ell(lr[0], ys[q], lr[1], lr[2], lr[3], phi), label);
				}
			}
			double[] lr = legRing(r, (a + b) / 2, side, 0);
			double[] in = pt(lr[0], (a + b) / 2, lr[1]);
			m.band(row[0], row[1], false, new int[] { col }, in);
			m.band(row[1], row[2], false, new int[] { edge }, in);
		}
	}

	/** A rounded knee cop on the front of the knee, with a little wing on the outside. */
	static void kneeCop(Mesh m, Rig r, int side, int label, Style st) {
		double y = r.knee[1] - 2.5;
		double[] lr = legRing(r, y, side, 1.8);
		double[] c = pt(lr[0], y, lr[1] - lr[3] + 1.0);
		double[] axis = norm(pt(side * 0.15, 0.15, -1));
		int sides = 6;
		double ra = 5.0, rb = 5.6, h = 3.4;
		double[] e1 = Y, e2 = norm(ModelData.cross(axis, e1));
		e1 = norm(ModelData.cross(e2, axis));
		int[] base = new int[sides], midR = new int[sides];
		for (int s = 0; s < sides; s++) {
			double ang = Math.PI * 2 * s / sides;
			base[s] = m.v(add(c, add(scale(e1, Math.cos(ang) * rb), scale(e2, Math.sin(ang) * ra))), label);
			midR[s] = m.v(add(add(c, scale(axis, h * 0.6)), add(scale(e1, Math.cos(ang) * rb * 0.7), scale(e2, Math.sin(ang) * ra * 0.7))), label);
		}
		int tip = m.v(add(c, scale(axis, h)), label);
		m.mark("knee" + side, c[0], c[1], c[2], axis[0], axis[1], axis[2]);
		m.prio = 2;
		m.band(base, midR, true, new int[] { st.main }, add(c, scale(axis, -4)));
		for (int s = 0; s < sides; s++) {
			m.triAway(midR[s], midR[(s + 1) % sides], tip, st.light, add(c, scale(axis, -4)));
		}
	}

	/* ====================================================================== */
	/*                             BOOTS, GAUNTLETS                           */
	/* ====================================================================== */

	/**
	 * Tall riding boots: a shaft up to just under the knee, ending in a flared bucket cuff (higher at the front), and a
	 * foot with a squared toe and a heel. Shafts on the ankle labels 32 / 38 (they bend with the knee), toes on 45 / 46.
	 */
	static ModelData boots(Rig r, Style st) {
		Mesh m = new Mesh();
		int n = 6;
		int[] fb = r.feetB;
		for (int side = -1; side <= 1; side += 2) {
			int ank = side > 0 ? 38 : 32, toe = side > 0 ? 46 : 45;
			double kneeY = r.knee[1], ankY = r.ankle[1];
			double[][] rows = { { fb[4] - 7.0, 1.8 }, { ankY + 4, 2.6 }, { ankY - 1, 2.9 }, { (kneeY + ankY) / 2, 3.2 }, { kneeY + 12, 3.3 } };
			int[][] idx = new int[rows.length][n];
			double[][] lr = new double[rows.length][];
			for (int k = 0; k < rows.length; k++) {
				double[] q = legRing(r, Math.max(rows[k][0], ankY), side, rows[k][1]);
				if (k == 0) {
					q[2] += 0.6;
					q[3] += 1.0;
				}
				lr[k] = q;
				for (int j = 0; j < n; j++) {
					idx[k][j] = m.v(ell(q[0], rows[k][0], q[1], q[2], q[3], Math.PI * 2 * j / n + Math.PI / n), ank);
				}
			}
			m.prio = 2;
			for (int k = 0; k + 1 < rows.length; k++) {
				double[] in = pt((lr[k][0] + lr[k + 1][0]) / 2, (rows[k][0] + rows[k + 1][0]) / 2, (lr[k][1] + lr[k + 1][1]) / 2);
				m.band(idx[k], idx[k + 1], true, new int[] { st.leatherDark }, in);
			}
			// the bucket cuff: flares out from the top of the shaft, its top edge higher over the knee
			double[] t = lr[rows.length - 1];
			int[] c0 = new int[n], c1 = new int[n], c2 = new int[n];
			for (int j = 0; j < n; j++) {
				double phi = Math.PI * 2 * j / n + Math.PI / n;
				double lift = 3.5 * Math.cos(phi); // higher at the front
				c0[j] = m.v(ell(t[0], kneeY + 13.5, t[1], t[2] + 0.5, t[3] + 0.5, phi), ank);
				c1[j] = m.v(ell(t[0], kneeY + 6 - lift, t[1] - 0.4, t[2] + 2.6, t[3] + 2.8, phi), ank);
				c2[j] = m.v(ell(t[0], kneeY + 4.2 - lift, t[1] - 0.4, t[2] + 2.9, t[3] + 3.1, phi), ank);
			}
			double[] in = pt(t[0], kneeY + 4, t[1]);
			m.prio = 3;
			m.band(c0, c1, true, new int[] { st.leather }, in);
			m.band(c1, c2, true, new int[] { st.trim }, in);
			// the foot: heel, instep, ball, toe sections along z
			double[] fe = extent(r.feet, side > 0 ? 47 : 48);
			double x = (fe[0] + fe[3]) / 2, hw = (fe[3] - fe[0]) / 2;
			double sole = fb[4] + 0.5, back = fb[5] + 1.0, front = fb[2] - 1.5;
			m.mark("heel" + side, x, sole, back, kneeY + 6 - 3.5);
			double[][] sec = { { back, -6.5, hw * 0.85, ank }, { back - 9, -9.5, hw + 0.6, ank }, { front + 9, -4.0, hw + 0.8, toe }, { front + 2, -1.5, hw * 0.75, toe } };
			int[][] sx = new int[sec.length][];
			for (int k = 0; k < sec.length; k++) {
				double z = sec[k][0], topY = sole + sec[k][1], hwk = sec[k][2];
				int lab = (int) sec[k][3];
				sx[k] = new int[] { m.v(pt(x - hwk, sole, z), lab), m.v(pt(x + hwk, sole, z), lab), m.v(pt(x + hwk * 0.95, topY + 1.5, z), lab),
						m.v(pt(x, topY, z), lab), m.v(pt(x - hwk * 0.95, topY + 1.5, z), lab) };
			}
			m.prio = 2;
			for (int k = 0; k + 1 < sec.length; k++) {
				double[] cin = pt(x, sole - 2, (sec[k][0] + sec[k + 1][0]) / 2);
				for (int e = 0; e < 5; e++) {
					int e2 = (e + 1) % 5;
					int col = e == 0 ? st.leatherDark : k == sec.length - 2 ? st.trim : st.leatherDark;
					m.quadAway(sx[k][e], sx[k][e2], sx[k + 1][e2], sx[k + 1][e], col, cin);
				}
			}
			int last = sec.length - 1;
			for (int e = 1; e + 1 < 5; e++) {
				m.tri(sx[last][0], sx[last][e], sx[last][e + 1], st.trim, pt(0, 0, -1));
				m.tri(sx[0][0], sx[0][e], sx[0][e + 1], st.leatherDark, pt(0, 0, 1));
			}
			// the heel block
			int[] hb = new int[8];
			double hz0 = back - 0.5, hz1 = back - 5.5;
			for (int k = 0; k < 8; k++) {
				double xx = x + ((k & 1) == 0 ? -hw * 0.8 : hw * 0.8), yy = (k & 2) == 0 ? sole - 1.0 : sole + 1.2, zz = (k & 4) == 0 ? hz0 : hz1;
				hb[k] = m.v(pt(xx, yy, zz), ank);
			}
			double[] hc = pt(x, sole + 0.1, (hz0 + hz1) / 2);
			int[][] hq = { { 0, 1, 3, 2 }, { 4, 5, 7, 6 }, { 0, 1, 5, 4 }, { 2, 3, 7, 6 }, { 0, 2, 6, 4 }, { 1, 3, 7, 5 } };
			for (int[] q : hq) {
				m.quadAway(hb[q[0]], hb[q[1]], hb[q[2]], hb[q[3]], st.dark, hc);
			}
		}
		return m.d;
	}

	/**
	 * Gauntlets: a plated mitten hand (on the hand labels 27 / 28, so the wrist still turns it), a knuckle plate, a
	 * thumb, and a long cuff flaring up the forearm (19 / 22) over the coat's sleeve.
	 */
	static ModelData gauntlets(Rig r, Style st) {
		Mesh m = new Mesh();
		for (int side = -1; side <= 1; side += 2) {
			int hand = side > 0 ? 28 : 27, fore = side > 0 ? 22 : 19;
			double[] W = mirror(centroid(r.hands, 19), side), H = mirror(r.hand, side), E = mirror(r.elbow, side);
			double[] he = extent(r.hands, 27);
			double hx = (he[3] - he[0]) / 2, hz = (he[5] - he[2]) / 2;
			double[] Af = norm(sub(W, E));
			// the cuff: rings up the forearm from the wrist, flaring
			int n = 6;
			double[][] cr = { { 3.5, 5.6 }, { -6, 7.0 }, { -14, 8.8 }, { -16, 9.0 } };
			int[][] ci = new int[cr.length][n];
			for (int k = 0; k < cr.length; k++) {
				double[] c = add(W, scale(Af, cr[k][0]));
				for (int j = 0; j < n; j++) {
					ci[k][j] = m.v(around(c, Af, cr[k][1], cr[k][1] * 0.95, Math.PI * 2 * j / n), fore);
				}
			}
			m.prio = 10;
			m.band(ci[0], ci[1], true, new int[] { st.main }, add(W, scale(Af, -1)));
			m.band(ci[1], ci[2], true, new int[] { st.main, st.light }, add(W, scale(Af, -10)));
			m.band(ci[2], ci[3], true, new int[] { st.trim }, add(W, scale(Af, -15)));
			// the hand: wrist, knuckles, fingertips (curled a little towards the palm)
			double[] D = norm(sub(H, W));
			double[][] hs = { { 3.0, 1.0, 1.0 }, { 9.5, 1.15, 1.08 }, { 16.5, 0.85, 0.85 } };
			int[][] hi = new int[hs.length][4];
			for (int k = 0; k < hs.length; k++) {
				double[] c = add(W, scale(D, hs[k][0]));
				if (k == 2) {
					c = add(c, pt(-side * 1.2, 0, -0.5));
				}
				double ax = hx * hs[k][1], az = hz * hs[k][2];
				double[][] corner = { { -ax, -az }, { ax, -az }, { ax, az }, { -ax, az } };
				for (int q = 0; q < 4; q++) {
					hi[k][q] = m.v(add(c, pt(corner[q][0], 0, corner[q][1])), k == 0 ? fore : hand);
				}
			}
			int tip = m.v(add(add(W, scale(D, 19)), pt(-side * 1.6, 0, -0.5)), hand);
			for (int k = 0; k + 1 < hs.length; k++) {
				double[] in = add(W, scale(D, (hs[k][0] + hs[k + 1][0]) / 2));
				for (int q = 0; q < 4; q++) {
					m.quadAway(hi[k][q], hi[k][(q + 1) % 4], hi[k + 1][(q + 1) % 4], hi[k + 1][q], st.dark, in);
				}
			}
			for (int q = 0; q < 4; q++) {
				m.triAway(hi[2][q], hi[2][(q + 1) % 4], tip, st.dark, add(W, scale(D, 14)));
			}
			// the knuckle plate on the back of the hand (the outside)
			double[] kc = add(add(W, scale(D, 8.5)), pt(side * (hx + 0.6), 0, 0));
			m.mark("cuff" + side, W[0], W[1], W[2], Af[0], Af[1], Af[2], kc[0], kc[1], kc[2]);
			int[] kp = new int[4], kq = new int[4];
			double[][] kk = { { -4.5, -hz * 1.1 }, { -4.5, hz * 1.1 }, { 4.0, hz * 1.15 }, { 4.0, -hz * 1.15 } };
			for (int q = 0; q < 4; q++) {
				kp[q] = m.v(add(kc, pt(-side * 0.8, kk[q][0], kk[q][1])), hand);
				kq[q] = m.v(add(kc, pt(side * 0.6, kk[q][0] * 0.8, kk[q][1] * 0.75)), hand);
			}
			m.prio = 11;
			for (int q = 0; q < 4; q++) {
				m.quadAway(kp[q], kp[(q + 1) % 4], kq[(q + 1) % 4], kq[q], st.trim, add(kc, pt(-side * 4, 0, 0)));
			}
			m.quad(kq[0], kq[1], kq[2], kq[3], st.main, pt(side, 0, 0));
			// the thumb, from the front of the wrist forward and down
			double[] t0 = add(add(W, scale(D, 5)), pt(-side * 0.5, 0, -hz - 0.5)), t1 = add(t0, pt(-side * 1.0, 6, -2.5));
			m.prio = 10;
			ModelData th = new ModelData();
			th.skin = hand;
			ArmourForge.blade(th, t0, t1, pt(side, 0, 0), 1.8, 1.4, st.dark);
			th.priorities.replaceAll(x -> 10);
			th.renderTypes.replaceAll(x -> 0);
			ArmourForge.merge(m.d, th, null);
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                               THE SHIELD                               */
	/* ====================================================================== */

	/**
	 * A round shield with a domed boss and a raised rim, in the rune kite's place and plane (so it's held the same and
	 * the kite's inventory fit still works); every vertex on the left-hand label 28, centred where the kite's were.
	 */
	static ModelData shield(ArmourForge af, Style st) throws IOException {
		ModelData kite = af.read(ArmourForge.RUNE_KITE_WORN);
		Frame k = ArmourForge.shieldFrame(kite, 61);
		double[] down = norm(sub(Y, scale(k.n, dot(Y, k.n))));
		double[] across = norm(ModelData.cross(k.n, down));
		double[] c = add(k.o, scale(k.n, 1.0));
		Mesh m = new Mesh();
		m.mark("shield", c[0], c[1], c[2], k.n[0], k.n[1], k.n[2], down[0], down[1], down[2], across[0], across[1], across[2], 25);
		int n = 10;
		double R = 25;
		// radius, height over the face plane
		double[][] prof = { { 6.0, 1.2 }, { 19.5, 0.0 }, { 21.0, 1.3 }, { 23.4, 1.5 }, { R, 0.2 }, { R, -2.6 } };
		int[][] ring = new int[prof.length][n];
		for (int q = 0; q < prof.length; q++) {
			for (int j = 0; j < n; j++) {
				double a = Math.PI * 2 * j / n;
				double[] p = add(c, add(scale(k.n, prof[q][1]), add(scale(down, Math.cos(a) * prof[q][0]), scale(across, Math.sin(a) * prof[q][0]))));
				ring[q][j] = m.v(p, 28);
			}
		}
		double[] behind = add(c, scale(k.n, -12));
		m.prio = 10;
		int field = DraftArmour.shieldFace(st);
		m.band(ring[0], ring[1], true, new int[] { field }, behind);
		m.band(ring[1], ring[2], true, new int[] { st.trimDark }, add(c, scale(k.n, -40)));
		m.band(ring[2], ring[3], true, new int[] { st.trim }, behind);
		m.band(ring[3], ring[4], true, new int[] { st.trim }, behind);
		m.band(ring[4], ring[5], true, new int[] { st.trimDark }, c);
		int back = m.v(add(c, scale(k.n, -2.6)), 28);
		for (int j = 0; j < n; j++) {
			m.tri(ring[5][j], ring[5][(j + 1) % n], back, st.dark, scale(k.n, -1));
		}
		// the boss: a dome with a flange, riveted
		double[][] boss = { { 6.6, 1.0 }, { 6.2, 2.6 }, { 4.4, 5.0 }, { 2.0, 6.4 } };
		int bn = 6;
		int[][] br = new int[boss.length][bn];
		for (int q = 0; q < boss.length; q++) {
			for (int j = 0; j < bn; j++) {
				double a = Math.PI * 2 * j / bn;
				br[q][j] = m.v(add(c, add(scale(k.n, boss[q][1]), add(scale(down, Math.cos(a) * boss[q][0]), scale(across, Math.sin(a) * boss[q][0])))), 28);
			}
		}
		int apex = m.v(add(c, scale(k.n, 7.0)), 28);
		m.prio = 11;
		m.band(br[0], br[1], true, new int[] { st.trimDark }, add(c, scale(k.n, -3)));
		m.band(br[1], br[2], true, new int[] { st.light }, add(c, scale(k.n, -3)));
		m.band(br[2], br[3], true, new int[] { st.light }, add(c, scale(k.n, -3)));
		for (int j = 0; j < bn; j++) {
			m.triAway(br[3][j], br[3][(j + 1) % bn], apex, st.light, add(c, scale(k.n, -3)));
		}
		return m.d;
	}
}
