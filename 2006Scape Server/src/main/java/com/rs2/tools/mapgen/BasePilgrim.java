package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ArmourForge.add;
import static com.rs2.tools.mapgen.ArmourForge.norm;
import static com.rs2.tools.mapgen.ArmourForge.scale;
import static com.rs2.tools.mapgen.ArmourForge.sub;
import static com.rs2.tools.mapgen.ModelData.hsl;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.ToIntFunction;

import com.rs2.tools.mapgen.ArmourSets.Style;
import com.rs2.tools.mapgen.BaseMantle.Body;

/**
 * The Ashen pilgrim base (A25): a road-worn shrine pilgrim, a walking haystack under a hat. Built from scratch on the
 * mystic robe's rig (Kind.MANTLE); see NewBases.
 *
 * <ul>
 * <li>Hat: a huge, shallow conical travelling hat (a kasa) a good deal wider than the shoulders, woven in a checker of
 * pale and darker straw, its edge bound in rope, a dark headband frame under it sitting on the brow, a cord tied under
 * the chin and a brass shrine token at the front. Named "hat", so the face and hair show under it.</li>
 * <li>Top: a three-tier straw rain-cape (a mino): a thatched shoulder cape all round, open at the throat, over two longer
 * tiers down the back that flare out wide behind the arms to mid-thigh, every tier's hem cut into jagged straw points,
 * the straw fluted in bundles; under it an ash-grey wrap jacket with a crossed collar and loose sleeves, a twisted
 * rope belt with its ends hanging, red prayer beads looped down the chest with a brass bead, a rolled bedroll strapped
 * across the shoulders, and the little brass censer, ember glowing in its slots, hung from the belt. Named "top", so
 * the arms kit is hidden and the jacket's own sleeves are drawn.</li>
 * <li>Trousers: loose, ballooning below the knee and gathered at the shin by a rope tie, over pale leg-wraps wound
 * down to the ankle.</li>
 * <li>Sandals: thick straw soles under pale socks, the cords crossed over the instep and tied round the ankle.</li>
 * <li>Hand-wraps: pale cloth wound over the wrist and palm, the fingers bare.</li>
 * </ul>
 *
 * <p>Animation labels. The player's frames turn each joint about the centroid of a few "origin" labels ({@link
 * #ORIGINS}: the shoulder 21 / 25, the elbow 17 / 23, the hand 27 / 28, the hips 40 / 42, the knees 34+31 / 35+37,
 * the heels 47 / 48, the neck 2+3, the waist 29 / 30 / 5, and the cape chain 10 / 9 / 14 / 13). The client merges the
 * player's models by their faces, so a vertex no face uses is dropped: a piece that hides an identity kit has to
 * supply the kit's origin vertices itself. Each piece does so with {@link #anchors}: for every origin label of each kit
 * it hides, as many vertices as the kit has on it, a hair apart round that label's centroid and joined into tiny
 * triangles buried in the limb, so every joint turns exactly where it does on the bare body. Visible geometry never
 * takes an origin label: it takes a "twin" that every frame moves the same way ({@link #safe}): the chest 8 (for the
 * neck), the belly 4 (for the waist), the still pelvis 41, the upper arms 20 / 26, the forearms 19 / 22, the thighs
 * 44 / 43, the shins 33 / 36, the ankles 32 / 38, the toes 45 / 46, the skull 1. The shoulder cape takes its nearest
 * torso or arm label (made safe), so it rides the shoulders and the upper arms. The two back tiers take the stock
 * cape's chain by height (10 at the shoulders, 11, 9 at the waist, 16, 14 at the thigh), so the walk and run swing the
 * thatch back clear of the legs. The sandals and hand-wraps are built on copies of the kit's own feet and hands, keeping
 * their labels, so their origins are the kit's; the palm wraps on the hand labels are centred on the hand (moving its
 * origin by under a unit). The soles take their nearest foot label, made safe (the heel's 47 / 48 become the ankle's
 * 38 / 32, which every frame moves the same way).
 *
 * <p>The helpers here ({@link Mesh}, {@link #anchors}, {@link #safe}, {@link #copyKit}, {@link #cord}, {@link #bead},
 * {@link #fitted}) are shared with {@link BaseGhillie} and {@link BaseDiver}.
 */
final class BasePilgrim {

	private BasePilgrim() {
	}

	/* ====================================================================== */
	/*                          SHARED: LABELS, MESH                          */
	/* ====================================================================== */

	/** Labels a player frame uses as a rotation (or scale) origin: never on visible geometry, supplied by {@link #anchors}. */
	static final Set<Integer> ORIGINS = Set.of(0, 2, 3, 5, 9, 10, 13, 14, 17, 21, 23, 25, 27, 28, 29, 30, 31, 34, 35, 37, 39, 40, 42, 47, 48);
	/** Right (x < 0) / left: the upper arm, forearm, hand, thigh, shin, ankle and toes labels (none an origin but the hand). */
	static final int[] UPPER = { 20, 26 }, FORE = { 19, 22 }, HAND = { 27, 28 }, THIGH = { 44, 43 }, SHIN = { 33, 36 }, ANKLE = { 32, 38 }, TOES = { 45, 46 };
	static final int SKULL = 1, CHEST = 8, BELLY = 4, HIPS = 41;
	/** The stock capes' chain, top to hem, with the labels between its rows. */
	static final int CAPE_TOP = 10, CAPE_BACK = 11, CAPE_WAIST = 9, CAPE_HIP = 16, CAPE_THIGH = 14, CAPE_SHIN = 15, CAPE_KNEE = 13, CAPE_HEM = 12;
	static final int SKIN = 4550;

	/** Straw, rope and leg-wrap colours. */
	static final int STRAW = hsl(8, 3, 66), STRAW_LIGHT = hsl(8, 3, 80), STRAW_DARK = hsl(7, 3, 48), THATCH = hsl(7, 2, 56), THATCH_DARK = hsl(7, 2, 42),
			THATCH_TIP = hsl(7, 1, 70), THATCH_IN = hsl(7, 2, 30), ROPE = hsl(7, 3, 38), ROPE_LIGHT = hsl(7, 3, 52), WRAP = hsl(6, 1, 88), WRAP_DARK = hsl(6, 1, 70),
			BLANKET = hsl(40, 2, 40), BLANKET_DARK = hsl(40, 2, 30), BLANKET_LIGHT = hsl(40, 1, 62);

	/** A label that every player frame moves exactly as it moves {@code l}, but which is no joint's origin. */
	static int safe(int l) {
		switch (l) {
		case 2:
		case 49:
		case 94:
			return CHEST;
		case 3:
			return SKULL;
		case 5:
		case 29:
		case 30:
			return BELLY;
		case 39:
			return HIPS;
		case 35:
		case 40:
			return 43;
		case 34:
		case 42:
			return 44;
		case 37:
			return 36;
		case 31:
			return 33;
		case 21:
		case 17:
			return 20;
		case 25:
		case 23:
			return 26;
		case 47:
			return 38;
		case 48:
			return 32;
		case 0:
			return 32;
		default:
			return l;
		}
	}

	/** The safe label of {@code ref}'s labelled vertex nearest p. */
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
		return safe(lab);
	}

	/** A model being built: each vertex takes its label as it is made, each face the current priority. */
	static final class Mesh {
		final ModelData m = new ModelData();
		int prio;

		Mesh() {
			m.shading = 0;
		}

		int v(double x, double y, double z, int label) {
			m.skin = label;
			return m.vertex(x, y, z);
		}

		int v(double[] p, int label) {
			return v(p[0], p[1], p[2], label);
		}

		double[] p(int i) {
			int[] v = m.vertices.get(i);
			return new double[] { v[0], v[1], v[2] };
		}

		double[] centre(int... idx) {
			double[] c = new double[3];
			for (int i : idx) {
				c = add(c, p(i));
			}
			return scale(c, 1.0 / idx.length);
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

		/** A triangle facing along {@code dir}. */
		void triDir(int a, int b, int c, int col, double[] dir) {
			tri(a, b, c, col, sub(centre(a, b, c), dir));
		}

		void quadDir(int a, int b, int c, int d, int col, double[] dir) {
			triDir(a, b, c, col, dir);
			triDir(a, c, d, col, dir);
		}

		/** A triangle facing towards the point {@code toward} (a lining, seen from inside). */
		void triIn(int a, int b, int c, int col, double[] toward) {
			double[] g = centre(a, b, c);
			tri(a, b, c, col, sub(scale(g, 2), toward));
		}

		void quadIn(int a, int b, int c, int d, int col, double[] toward) {
			triIn(a, b, c, col, toward);
			triIn(a, c, d, col, toward);
		}

		/**
		 * Quads between two rows, each facing away from the vertical axis (cx, cz) at its own height ({@code in}: towards
		 * it, a lining); colour per span from {@code cols}.
		 */
		void band(int[] a, int[] b, boolean closed, int[] cols, double cx, double cz, boolean in) {
			int n = a.length;
			for (int i = 0; i < (closed ? n : n - 1); i++) {
				int j = (i + 1) % n;
				double[] g = centre(a[i], a[j], b[j], b[i]);
				double[] ax = { cx, g[1], cz };
				if (in) {
					quadIn(a[i], a[j], b[j], b[i], cols[i % cols.length], ax);
				} else {
					quad(a[i], a[j], b[j], b[i], cols[i % cols.length], ax);
				}
			}
		}

		/** Quads between two rings round a (slanted) axis: each faces away from the segment c0-c1 near it. */
		void tube(int[] a, int[] b, int[] cols, double[] c0, double[] c1) {
			int n = a.length;
			double[] d = sub(c1, c0);
			double dd = ArmourForge.dot(d, d) + 1e-9;
			for (int i = 0; i < n; i++) {
				int j = (i + 1) % n;
				double[] g = centre(a[i], a[j], b[j], b[i]);
				double t = Math.max(0, Math.min(1, ArmourForge.dot(sub(g, c0), d) / dd));
				quad(a[i], a[j], b[j], b[i], cols[i % cols.length], add(c0, scale(d, t)));
			}
		}

		/** The finished model: vertices no face uses are dropped (the client drops them when it merges the player). */
		ModelData done() {
			return BaseWrap.compact(m);
		}
	}

	/**
	 * The origin vertices of a kit the piece hides: for each origin label of {@code kit}, as many vertices as the kit
	 * has on it, two units apart round that label's centroid (their sum exactly the kit's), joined into tiny triangles
	 * buried in the limb, so the merged player's centroid of every origin label (each joint's pivot) is the bare body's.
	 */
	static void anchors(Mesh me, ModelData kit, int col) {
		if (kit == null) {
			return;
		}
		Map<Integer, double[]> sum = new TreeMap<>();
		for (int i = 0; i < kit.vertices.size(); i++) {
			int l = kit.vertexSkins.get(i);
			if (ORIGINS.contains(l)) {
				int[] v = kit.vertices.get(i);
				double[] s = sum.computeIfAbsent(l, k -> new double[4]);
				s[0] += v[0];
				s[1] += v[1];
				s[2] += v[2];
				s[3]++;
			}
		}
		int[][] pairs = { { 2, 0, 0 }, { 0, 0, 2 }, { 0, 2, 0 }, { 2, 0, 2 }, { 2, 0, -2 }, { 0, 2, 2 }, { 2, 2, 0 }, { 2, -2, 0 } };
		for (Map.Entry<Integer, double[]> e : sum.entrySet()) {
			int l = e.getKey();
			double[] s = e.getValue();
			int n = (int) s[3];
			int[][] off = new int[n][3];
			int k = 0;
			if (n % 2 == 1) {
				k = 1; // one at the centre
			}
			for (int p = 0; k + 1 < n + 1 && k < n; p++, k += 2) {
				off[k] = pairs[p].clone();
				off[k + 1] = new int[] { -pairs[p][0], -pairs[p][1], -pairs[p][2] };
			}
			int[] base = new int[3], rem = new int[3];
			for (int a = 0; a < 3; a++) {
				long total = Math.round(s[a]);
				base[a] = (int) Math.round(total / (double) n);
				rem[a] = (int) (total - (long) base[a] * n);
			}
			int[] idx = new int[n];
			for (int i = 0; i < n; i++) {
				int[] q = new int[3];
				for (int a = 0; a < 3; a++) {
					q[a] = base[a] + off[i][a] + (i < Math.abs(rem[a]) ? Integer.signum(rem[a]) : 0);
				}
				idx[i] = me.v(q[0], q[1], q[2], l);
			}
			if (n >= 3) {
				for (int i = 0; i < n; i += 3) {
					me.m.tri(idx[i % n], idx[(i + 1) % n], idx[(i + 2) % n], col);
					me.m.priorities.set(me.m.priorities.size() - 1, 0);
				}
			} else { // one or two: close the triangle on a vertex of the safe twin
				int c = me.v(base[0], base[1] + 1, base[2] + 1, safe(l));
				me.m.tri(idx[0], idx[n - 1], c, col);
				me.m.priorities.set(me.m.priorities.size() - 1, 0);
			}
		}
	}

	/** Colours a copied kit face, from its centroid and the kit's own colour. */
	interface FaceColour {
		int at(double[] c, int original);
	}

	/** Appends a copy of an identity kit (its own labels, so its joints are the bare body's) in the given colours. */
	static void copyKit(Mesh me, ModelData kit, FaceColour col, int prio) {
		int base = me.m.vertices.size();
		for (int i = 0; i < kit.vertices.size(); i++) {
			me.m.vertices.add(kit.vertices.get(i).clone());
			me.m.vertexSkins.add(kit.vertexSkins.get(i));
		}
		for (int i = 0; i < kit.faces.size(); i++) {
			int[] f = kit.faces.get(i);
			double[] c = new double[3];
			for (int k : f) {
				c = add(c, ArmourForge.d(kit.vertices.get(k)));
			}
			me.m.faces.add(new int[] { f[0] + base, f[1] + base, f[2] + base });
			me.m.colors.add(col.at(scale(c, 1 / 3.0), kit.colors.get(i)));
			me.m.renderTypes.add(0);
			me.m.priorities.add(prio);
			me.m.alphas.add(0);
			me.m.faceSkins.add(0);
		}
	}

	/** Two unit vectors square to the axis a (the first as near the x axis as it can be). */
	static double[][] across(double[] a) {
		double[] u = Math.abs(a[0]) > 0.9 ? ModelData.cross(a, new double[] { 0, 0, 1 }) : ModelData.cross(new double[] { 1, 0, 0 }, a);
		u = norm(ModelData.cross(a, u));
		if (u[0] < 0 && Math.abs(a[0]) <= 0.9) {
			u = scale(u, -1);
		}
		double[] w = norm(ModelData.cross(a, u));
		return new double[][] { u, w };
	}

	/** A ring of n vertices round the axis a through c (radii ru along the first square vector, rw the second). */
	static int[] ring(Mesh me, double[] c, double[] a, double ru, double rw, int n, double phase, int label) {
		double[][] uw = across(norm(a));
		int[] r = new int[n];
		for (int i = 0; i < n; i++) {
			double t = Math.PI * 2 * i / n + phase;
			r[i] = me.v(add(c, add(scale(uw[0], Math.cos(t) * ru), scale(uw[1], Math.sin(t) * rw))), label);
		}
		return r;
	}

	/** A thin cord (an open prism of {@code sides}) along a path. */
	static void cord(Mesh me, double[][] path, double r, int sides, int col, int label) {
		int[] prev = null;
		for (int i = 0; i < path.length; i++) {
			double[] d = norm(sub(path[Math.min(i + 1, path.length - 1)], path[Math.max(i - 1, 0)]));
			int[] cur = ring(me, path[i], d, r, r, sides, 0.3, label);
			if (prev != null) {
				me.tube(prev, cur, new int[] { col }, path[i - 1], path[i]);
			}
			prev = cur;
		}
	}

	/** A bead: a four-sided pyramid standing on p along n. */
	static void bead(Mesh me, double[] p, double[] n, double r, int col, int col2, int label) {
		double[][] uw = across(norm(n));
		int[] b = new int[4];
		for (int k = 0; k < 4; k++) {
			double t = Math.PI / 2 * k + Math.PI / 4;
			b[k] = me.v(add(p, add(scale(uw[0], Math.cos(t) * r), scale(uw[1], Math.sin(t) * r))), label);
		}
		int apex = me.v(add(p, scale(norm(n), r * 1.1)), label);
		for (int k = 0; k < 4; k++) {
			me.tri(b[k], b[(k + 1) % 4], apex, k % 2 == 0 ? col : col2, sub(p, scale(norm(n), 3)));
		}
	}

	/** A ring fitted round {@code kit} at height y (Mantle's angles: 0 at the front, 90 the left), {@code out} proud of it. */
	static int[] fitted(Mesh me, ModelData kit, double y, double cz, int n, double out, double minR, int label) {
		int[] r = new int[n];
		for (int i = 0; i < n; i++) {
			double th = 360.0 * i / n, a = Math.toRadians(th);
			double[] h = BaseMantle.hit(kit, y, cz, th);
			double rad = h == null ? minR : Math.max(minR, Math.hypot(h[0], h[2] - cz));
			rad += out;
			r[i] = me.v(Math.sin(a) * rad, y, cz - Math.cos(a) * rad, label);
		}
		return r;
	}

	/** One leg's section at y: {cx, cz, rx, rz}, {@code margin} proud of the legs kit (side 0 right, 1 left). */
	static double[] legAt(Body b, double y, int s, double margin) {
		double[] q = BaseCoat.slice(b.legs, y, s == 0 ? -1 : 1);
		return new double[] { (q[0] + q[1]) / 2, (q[2] + q[3]) / 2, (q[1] - q[0]) / 2 + margin, (q[3] - q[2]) / 2 + margin };
	}

	/** Keeps a leg's section {cx, cz, rx, rz} at least {@code gap} clear of the middle (x = 0), squeezing its inner side. */
	static double[] inner(double[] q, int s, double gap) {
		double d = gap - (Math.abs(q[0]) - q[2]);
		if (d > 0) {
			q[2] -= d / 2;
			q[0] += sx(s) * d / 2;
		}
		return q;
	}

	static double sx(int s) {
		return s == 0 ? -1 : 1;
	}

	static double lerp(double a, double b, double t) {
		return a + (b - a) * t;
	}

	static double[] lerp(double[] a, double[] b, double t) {
		return new double[] { lerp(a[0], b[0], t), lerp(a[1], b[1], t), lerp(a[2], b[2], t) };
	}

	/** A point at angle th (degrees: 0 the front, 90 the left, 180 the back) on an ellipse round (cx, y, cz). */
	static double[] ell(double cx, double y, double cz, double rx, double rzf, double rzb, double th) {
		return BaseMantle.ell(cx, y, cz, rx, rzf, rzb, th);
	}

	/* ====================================================================== */
	/*                                 PIECES                                 */
	/* ====================================================================== */

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		Body b = BaseMantle.body(af, female);
		switch (slot) {
		case ArmourSets.HELM:
			return new ModelData[] { hat(st, b), null };
		case ArmourSets.BODY:
			return new ModelData[] { body(st, b), null };
		case ArmourSets.LEGS:
			return new ModelData[] { trousers(st, b), null };
		case ArmourSets.BOOTS:
			return new ModelData[] { sandals(st, b), null };
		case ArmourSets.GLOVES:
			return new ModelData[] { wraps(st, b), null };
		default:
			return new ModelData[] { donor, donorSecond };
		}
	}

	/** The inventory model for the slot, or null to let ArmourSets build it the usual way from the worn model(s). */
	static ModelData inventory(ArmourForge af, Style st, int slot, ModelData[] worn) throws IOException {
		int[] b = ArmourSets.base(st, slot);
		ModelData m = ArmourForge.copyModel(worn[0]);
		// the hat tipped to show its cone; the trousers turned to show both legs
		double tilt = Math.toRadians(slot == ArmourSets.HELM ? 50 : 0), yaw = Math.toRadians(slot == ArmourSets.LEGS ? 90 : 0);
		switch (slot) {
		case ArmourSets.BODY: // the rain-cape laid out back up: the thatch and the bedroll are what it is
			m.transform(v -> new double[] { -v[0], v[1], -v[2] });
			return af.inventory(m, new int[] { b[0], b[1] }, b[5]);
		case ArmourSets.HELM:
		case ArmourSets.LEGS: {
			int[] bb = m.bounds();
			double cy = (bb[1] + bb[4]) / 2.0, cz = (bb[2] + bb[5]) / 2.0;
			m.transform(v -> {
				double y = v[1] - cy, z = v[2] - cz;
				double y2 = y * Math.cos(tilt) - z * Math.sin(tilt), z2 = y * Math.sin(tilt) + z * Math.cos(tilt);
				double x3 = v[0] * Math.cos(yaw) + z2 * Math.sin(yaw), z3 = -v[0] * Math.sin(yaw) + z2 * Math.cos(yaw);
				return new double[] { x3, y2 + cy, z3 + cz };
			});
			return af.inventory(m, b[0], b[5]);
		}
		default:
			return null;
		}
	}

	/* ------------------------------------------------------------- the hat */

	/**
	 * The kasa: a shallow cone from an apex over the crown out to an edge far past the shoulders, in three woven rings
	 * (a checker of two straws), a rope-bound edge, the darker underside, the headband frame that sits on the brow, a
	 * cord down to a knot under the chin, and a brass shrine token on the band. All on the skull label.
	 */
	static ModelData hat(Style st, Body b) {
		Mesh me = new Mesh();
		int n = 16;
		double top = b.headTop;
		double bandY = top + 8.5;
		double[] hs = BaseCoat.slice(b.head, bandY, 0);
		double hcz = (hs[2] + hs[3]) / 2, brx = Math.max(-hs[0], hs[1]) + 1.8, brz = (hs[3] - hs[2]) / 2 + 1.8;
		double R = b.armOuter + 14, apexY = top - 17, edgeY = top + 11, th = 2.4;
		double[] ts = { 0.3, 0.64, 1.0 };
		int apex = me.v(0, apexY, hcz + 1, SKULL);
		int[][] ring = new int[ts.length][];
		for (int k = 0; k < ts.length; k++) {
			ring[k] = hatRing(me, R * ts[k], profile(apexY, edgeY, ts[k]), hcz, n);
		}
		double[] below = { 0, apexY + 45, hcz };
		me.prio = 6;
		for (int j = 0; j < n; j++) {
			me.tri(apex, ring[0][j], ring[0][(j + 1) % n], j % 2 == 0 ? STRAW_LIGHT : STRAW, below);
		}
		for (int k = 0; k + 1 < ts.length; k++) {
			for (int j = 0; j < n; j++) {
				int jj = (j + 1) % n;
				me.quad(ring[k][j], ring[k][jj], ring[k + 1][jj], ring[k + 1][j], (j + k + 1) % 2 == 0 ? STRAW_LIGHT : STRAW, below);
			}
		}
		// the bound edge, rolled under
		int[] rim = hatRing(me, R + 0.9, edgeY + 1.6, hcz, n), rimU = hatRing(me, R - 0.8, edgeY + th + 0.4, hcz, n);
		me.band(ring[2], rim, true, new int[] { ROPE, ROPE_LIGHT }, 0, hcz, false);
		for (int j = 0; j < n; j++) {
			int jj = (j + 1) % n;
			me.quadDir(rim[j], rim[jj], rimU[jj], rimU[j], ROPE, new double[] { 0, 1, 0 });
		}
		// the underside, up to the headband
		double tb = brx / R;
		int[] u1 = hatRing(me, R * 0.6, profile(apexY, edgeY, 0.6) + th, hcz, n), u2 = new int[n];
		for (int j = 0; j < n; j++) {
			double a = Math.PI * 2 * j / n;
			u2[j] = me.v(Math.sin(a) * brx, profile(apexY, edgeY, tb) + th, hcz - Math.cos(a) * brz, SKULL);
		}
		double[] above = { 0, apexY - 40, hcz };
		for (int j = 0; j < n; j++) {
			int jj = (j + 1) % n;
			me.quad(rimU[j], rimU[jj], u1[jj], u1[j], STRAW_DARK, above);
			me.quad(u1[j], u1[jj], u2[jj], u2[j], THATCH_DARK, above);
		}
		// the headband frame sitting on the brow
		int[] hb = new int[n];
		for (int j = 0; j < n; j++) {
			double a = Math.PI * 2 * j / n;
			hb[j] = me.v(Math.sin(a) * (brx - 0.2), bandY, hcz - Math.cos(a) * (brz - 0.2), SKULL);
		}
		me.prio = 7;
		me.band(u2, hb, true, new int[] { st.leatherDark }, 0, hcz, false);
		// the shrine token at the front of the band
		bead(me, new double[] { 0, bandY - 3.5, hcz - brz - 0.2 }, new double[] { 0, -0.15, -1 }, 2.2, DraftArmourA.BRASS_LIGHT, LadderWeapons.BRASS, SKULL);
		// the chin cord: from the band at either side, down past the cheeks to a knot under the chin
		double chinY = b.headChin + (b.female ? 3 : 4.5), chinZ = b.headFront + (b.female ? 6 : 7);
		for (int s = 0; s < 2; s++) {
			double x = sx(s);
			double[][] path = { { x * (brx - 0.5), bandY - 0.5, hcz + 1 }, { x * (b.headHW + 1.2), lerp(bandY, chinY, 0.55), hcz - 2 },
					{ x * 2.5, chinY, chinZ + 1 }, { 0, chinY + 1.2, chinZ } };
			cord(me, path, 0.7, 3, ROPE, SKULL);
		}
		bead(me, new double[] { 0, chinY + 1.5, chinZ - 0.5 }, new double[] { 0, 0.5, -1 }, 1.4, ROPE_LIGHT, ROPE, SKULL);
		return me.done();
	}

	/** The hat's top at fraction t of the way out: a cone, a little steeper towards the edge. */
	static double profile(double apexY, double edgeY, double t) {
		return apexY + (edgeY - apexY) * (0.78 * t + 0.22 * t * t);
	}

	static int[] hatRing(Mesh me, double r, double y, double cz, int n) {
		int[] out = new int[n];
		for (int j = 0; j < n; j++) {
			double a = Math.PI * 2 * j / n;
			out[j] = me.v(Math.sin(a) * r, y, cz - Math.cos(a) * r, SKULL);
		}
		return out;
	}

	/* ------------------------------------------------------------- the top */

	static ModelData body(Style st, Body b) {
		Mesh me = new Mesh();
		ModelData trunk = ArmourForge.mergeAll(b.torso, b.legs);
		double cz = (b.chestZ + b.backZ) / 2, L = b.waistY - b.neckY;
		// ------------------------------------------------ the jacket
		double[][] rows = { { b.neckY + 1.5, 1.2, CHEST }, { b.neckY + L * 0.3, 1.4, CHEST }, { b.neckY + L * 0.65, 1.5, BELLY }, { b.waistY, 1.7, BELLY },
				{ b.waistY + 12, 3.4, BELLY } };
		int n = 8;
		int[][] jr = new int[rows.length][];
		for (int r = 0; r < rows.length; r++) {
			jr[r] = fitted(me, trunk, rows[r][0], cz, n, rows[r][1], b.neckR, (int) rows[r][2]);
		}
		me.prio = 1;
		for (int r = 0; r + 1 < rows.length; r++) {
			me.band(jr[r], jr[r + 1], true, new int[] { st.main }, 0, cz, false);
		}
		ModelData jacket = BaseWrap.compact(me.m);
		// the crossed collar: the left side over the right, from the neck down to under the belt
		ToIntFunction<double[]> byHeight = p -> p[1] < b.neckY + L * 0.5 ? CHEST : BELLY;
		me.prio = 2;
		strip(me, jacket, new double[][] { { 5.5, b.neckY + 1 }, { 1.5, b.neckY + L * 0.3 }, { -3.5, b.neckY + L * 0.62 }, { -6, b.waistY + 2 } }, 1.8, 0.7,
				st.trimDark, byHeight);
		strip(me, jacket, new double[][] { { -5.5, b.neckY + 1 }, { -1.8, b.neckY + L * 0.24 }, { 0.5, b.neckY + L * 0.36 } }, 1.6, 0.5, st.trim, byHeight);
		// ------------------------------------------------ the sleeves
		for (int s = 0; s < 2; s++) {
			sleeve(me, st, b, s);
		}
		// ------------------------------------------------ the rope belt, its knot and ends
		me.prio = 3;
		int[] b0 = fitted(me, trunk, b.waistY - 3.2, cz, 10, 2.6, b.neckR, BELLY), b1 = fitted(me, trunk, b.waistY + 0.8, cz, 10, 2.9, b.neckR, BELLY);
		me.band(b0, b1, true, new int[] { ROPE, ROPE_LIGHT }, 0, cz, false);
		double[] knot = add(lerp(me.p(b0[1]), me.p(b1[1]), 0.5), new double[] { 0, 0, -1.2 });
		bead(me, knot, new double[] { 0.3, 0, -1 }, 2.4, ROPE_LIGHT, ROPE, BELLY);
		for (int k = 0; k < 2; k++) {
			double[] e0 = add(knot, new double[] { k * 1.6 - 0.8, 1, -0.4 }), e1 = add(e0, new double[] { k * 2.2 - 0.6, 10 - k * 2.5, -1.2 });
			cord(me, new double[][] { e0, lerp(e0, e1, 0.5), e1 }, 0.8, 3, ROPE, BELLY);
			bead(me, e1, new double[] { 0, 1, 0 }, 1.3, ROPE_LIGHT, ROPE, BELLY);
		}
		// ------------------------------------------------ the prayer beads, looped down the chest
		me.prio = 4;
		int beads = 11;
		double yTop = b.neckY + 3, yBot = b.neckY + L * 0.74, w = b.neckR + 2.5;
		for (int k = 0; k < beads; k++) {
			double t = -1 + 2.0 * k / (beads - 1);
			double x = w * Math.sin(t * Math.PI / 2) * 1.05, y = yTop + (yBot - yTop) * (1 - t * t);
			double[] p = onFront(jacket, x, y);
			boolean brass = k == beads / 2;
			bead(me, add(p, new double[] { 0, 0, -0.9 }), new double[] { x * 0.03, 0, -1 }, brass ? 2.3 : 1.55, brass ? DraftArmourA.BRASS_LIGHT : st.accent,
					brass ? LadderWeapons.BRASS : DraftArmourA.BEAD_DARK, y < b.neckY + L * 0.5 ? CHEST : BELLY);
		}
		double[] tas = onFront(jacket, 0, yBot + 3.5);
		cord(me, new double[][] { add(tas, new double[] { 0, -1, -1.6 }), add(tas, new double[] { 0.4, 4, -1.8 }) }, 0.9, 3, st.accent, BELLY);
		// ------------------------------------------------ the straw rain-cape
		cape(me, st, b, cz);
		// ------------------------------------------------ the censer, hung from the belt at the right
		censer(me, st, me.p(b1[9]), b);
		// the joints of the hidden kits (the torso, and the arms, which the top's sleeves replace)
		anchors(me, b.torso, st.main);
		anchors(me, b.arms, st.main);
		return me.done();
	}

	/** Where the vertical ray at (x, y) from the front meets {@code onto} (or a little in front of the chest). */
	static double[] onFront(ModelData onto, double x, double y) {
		double[] h = ArmourForge.surface(onto, new double[] { x, y, -200 }, new double[] { 0, 0, 1 });
		return h != null ? h : new double[] { x, y, -14 };
	}

	/** A flat band laid down the front of {@code onto} along a path, {@code lift} proud of it. */
	static void strip(Mesh me, ModelData onto, double[][] path, double half, double lift, int col, ToIntFunction<double[]> label) {
		int n = path.length;
		int[] l = new int[n], r = new int[n];
		for (int i = 0; i < n; i++) {
			double[] d = { path[Math.min(i + 1, n - 1)][0] - path[Math.max(i - 1, 0)][0], path[Math.min(i + 1, n - 1)][1] - path[Math.max(i - 1, 0)][1] };
			double dl = Math.hypot(d[0], d[1]) + 1e-9, ax = -d[1] / dl * half, ay = d[0] / dl * half;
			double[] pl = onFront(onto, path[i][0] + ax, path[i][1] + ay), pr = onFront(onto, path[i][0] - ax, path[i][1] - ay);
			l[i] = me.v(add(pl, new double[] { 0, 0, -lift }), label.applyAsInt(pl));
			r[i] = me.v(add(pr, new double[] { 0, 0, -lift }), label.applyAsInt(pr));
		}
		for (int i = 0; i + 1 < n; i++) {
			me.quadDir(l[i], l[i + 1], r[i + 1], r[i], col, new double[] { 0, 0, -1 });
		}
	}

	/** A loose sleeve from the shoulder to just above the wrist, its mouth closed by a dark lining round the forearm. */
	static void sleeve(Mesh me, Style st, Body b, int s) {
		double sc = b.female ? 0.86 : 1;
		double[] sh = b.shoulder[s], el = b.elbow[s], wr = b.wrist[s];
		double[] up = norm(sub(el, sh)), fo = norm(sub(wr, el));
		double[][] c = { add(sh, new double[] { -sx(s) * 0.5, -1.5, 0 }), lerp(sh, el, 0.55), el, lerp(el, wr, 0.5), lerp(el, wr, 0.86) };
		double[][] ax = { up, up, norm(add(up, fo)), fo, fo };
		double[] rad = { Math.max(b.shoulderR[s][0], b.shoulderR[s][1]) + 1.8, 7.0 * sc, Math.max(b.elbowR[s][0], b.elbowR[s][1]) + 2.6, 7.8 * sc, 9.2 * sc };
		int[] labs = { UPPER[s], UPPER[s], UPPER[s], FORE[s], FORE[s] };
		int n = 6;
		int[][] rg = new int[c.length][];
		for (int k = 0; k < c.length; k++) {
			rg[k] = ring(me, c[k], ax[k], rad[k], rad[k], n, 0.2, labs[k]);
		}
		me.prio = 1;
		for (int k = 0; k + 1 < c.length; k++) {
			me.tube(rg[k], rg[k + 1], new int[] { k == c.length - 2 ? st.light : st.main }, c[k], c[k + 1]);
		}
		// the mouth: a dark lining from the sleeve's edge in to the wrist
		int[] in = ring(me, add(c[4], scale(fo, -1.5)), fo, 4.2 * sc, 4.2 * sc, n, 0.2, FORE[s]);
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			me.quadDir(rg[4][i], rg[4][j], in[j], in[i], st.dark, fo);
		}
	}

	/**
	 * The straw rain-cape: a thatched shoulder cape all round (open at the throat), and two long tiers down the back,
	 * flaring out behind the arms to mid-thigh. Each tier's hem is cut into straw points, and its straw is fluted.
	 */
	static void cape(Mesh me, Style st, Body b, double cz) {
		ModelData ref = ArmourForge.mergeAll(b.torso, b.arms);
		ToIntFunction<double[]> shoulders = p -> nearest(ref, p);
		ToIntFunction<double[]> chain = p -> capeLabel(b, p[1]);
		double ao = b.armOuter, sTop = b.shoulderTop, fr = -b.chestZ, bk = b.backZ, hipBk = b.hipsBackZ;
		int[] outer = { THATCH, THATCH_DARK }, lining = { THATCH_DARK };
		// the lowest tier first (the ones above overlap it)
		me.prio = 2;
		tier(me, new double[][] { { b.waistY - 14, cz, ao + 4, fr + 8, bk + 14 }, { b.hipJointY - 2, cz, ao + 13, fr + 10, hipBk + 20 },
				{ b.hipJointY + 22, cz, ao + 19, fr + 12, hipBk + 25 } }, 84, 276, 13, 10, outer, lining, chain, 3);
		me.prio = 3;
		tier(me, new double[][] { { sTop + 10, cz, ao, fr + 6, bk + 8 }, { b.waistY - 1, cz, ao + 15, fr + 9, bk + 20 } }, 82, 278, 11, 8, outer, lining,
				chain, 7);
		me.prio = 4;
		tier(me, new double[][] { { b.neckY - 1.5, b.neckZ, b.neckR + 5, b.neckR + 4, b.neckR + 5 }, { sTop + 1, cz, ao + 1.5, fr + 6, bk + 7 },
				{ sTop + 32, cz, ao + 11, fr + 12, bk + 15 } }, 20, 340, 16, 9, outer, lining, shoulders, 11);
		// the bedroll, rolled and tied across the shoulders, on the cape's back
		double by = sTop + 13, f = (by - (sTop + 1)) / 31.0, backZ = cz + lerp(bk + 7, bk + 15, f) + 1.5;
		double half = ao * 0.78, rr = b.female ? 4.8 : 5.6;
		double[] c0 = { -half, by, backZ + rr }, c1 = { half, by, backZ + rr }, xa = { 1, 0, 0 };
		int[] e0 = ring(me, c0, xa, rr, rr, 8, 0, CHEST), e1 = ring(me, c1, xa, rr, rr, 8, 0, CHEST);
		me.prio = 5;
		me.tube(e0, e1, new int[] { BLANKET, BLANKET_DARK }, c0, c1);
		int m0 = me.v(add(c0, new double[] { -0.6, 0, 0 }), CHEST), m1 = me.v(add(c1, new double[] { 0.6, 0, 0 }), CHEST);
		for (int i = 0; i < 8; i++) {
			int j = (i + 1) % 8;
			me.tri(e0[i], e0[j], m0, i % 2 == 0 ? BLANKET_LIGHT : BLANKET, c1);
			me.tri(e1[i], e1[j], m1, i % 2 == 0 ? BLANKET_LIGHT : BLANKET, c0);
		}
		me.prio = 6;
		for (int k = 0; k < 2; k++) {
			double x = (k == 0 ? -1 : 1) * (half - 5);
			double[] t0 = { x - 1, by, backZ + rr }, t1 = { x + 1, by, backZ + rr };
			int[] r0 = ring(me, t0, xa, rr + 0.7, rr + 0.7, 8, 0, CHEST), r1 = ring(me, t1, xa, rr + 0.7, rr + 0.7, 8, 0, CHEST);
			me.tube(r0, r1, new int[] { ROPE, ROPE_LIGHT }, t0, t1);
		}
	}

	/** The stock cape's chain label for height y: on a row near it, the label between rows otherwise. */
	static int capeLabel(Body b, double y) {
		double[] ys = b.capeY;
		int[] row = { CAPE_TOP, CAPE_WAIST, CAPE_THIGH, CAPE_KNEE, CAPE_HEM }, mid = { CAPE_BACK, CAPE_HIP, CAPE_SHIN, CAPE_HEM };
		if (y <= ys[0]) {
			return CAPE_TOP;
		}
		for (int k = 0; k + 1 < ys.length; k++) {
			if (y <= ys[k + 1]) {
				double t = (y - ys[k]) / (ys[k + 1] - ys[k]);
				return t < (k == 0 ? 0.42 : 0.28) ? row[k] : t > 0.72 ? row[k + 1] : mid[k]; // the top row is always on 10, the chain's head
			}
		}
		return CAPE_HEM;
	}

	/**
	 * One tier of thatch over the arc th0..th1 (degrees): rows of {y, cz, rx, rz front, rz back}, the last one the hem,
	 * cut into points (a point on each column, a notch {@code depth} up between), the points kicked out a little and of
	 * uneven length; alternate columns stand proud (bundles of straw). The inside is lined.
	 */
	static void tier(Mesh me, double[][] rows, double th0, double th1, int cols, double depth, int[] outer, int[] lining, ToIntFunction<double[]> label,
			int seed) {
		int R = rows.length;
		for (int pass = 0; pass < 2; pass++) {
			boolean in = pass == 1;
			double inset = in ? -1.3 : 0;
			int[][] g = new int[R][cols + 1];
			int[] notch = new int[cols];
			for (int r = 0; r < R; r++) {
				for (int i = 0; i <= cols; i++) {
					double th = th0 + (th1 - th0) * i / cols;
					double flute = r > 0 && i % 2 == 0 ? 1.4 : 0;
					double[] row = rows[r];
					double y = row[0];
					double grow = inset + flute;
					if (r == R - 1) { // the points: uneven, and kicked out
						int h = Math.floorMod(i * 7 + seed * 3, 5);
						y += h * 1.1;
						grow += 2.2;
					}
					double[] p = ell(0, y, row[1], row[2] + grow, row[3] + grow, row[4] + grow, th);
					g[r][i] = me.v(p, label.applyAsInt(p));
				}
			}
			double[] hem = rows[R - 1], above = rows[R - 2];
			for (int i = 0; i < cols; i++) {
				double th = th0 + (th1 - th0) * (i + 0.5) / cols, t = depth / (hem[0] - above[0]);
				double y = hem[0] - depth;
				double[] p = ell(0, y, lerp(hem[1], above[1], t), lerp(hem[2], above[2], t) + inset + 0.7, lerp(hem[3], above[3], t) + inset + 0.7,
						lerp(hem[4], above[4], t) + inset + 0.7, th);
				notch[i] = me.v(p, label.applyAsInt(p));
			}
			int[] cl = in ? lining : outer;
			for (int r = 0; r + 2 < R; r++) {
				if (in && r == 0) {
					continue; // hidden against the body
				}
				me.band(g[r], g[r + 1], false, cl, 0, rows[r][1], in);
			}
			for (int i = 0; i < cols; i++) {
				int a = g[R - 2][i], b = g[R - 2][i + 1], ta = g[R - 1][i], tb = g[R - 1][i + 1], nn = notch[i];
				double[] ax = { 0, me.p(nn)[1], hem[1] };
				int c = cl[i % cl.length], tip = in ? cl[0] : THATCH_TIP;
				if (in) {
					me.triIn(a, b, nn, c, ax);
					me.triIn(a, nn, ta, c, ax);
					me.triIn(b, tb, nn, c, ax);
				} else {
					me.tri(a, b, nn, c, ax);
					me.tri(a, nn, ta, i % 2 == 0 ? tip : c, ax);
					me.tri(b, tb, nn, i % 2 == 0 ? c : tip, ax);
				}
			}
		}
	}

	/** The brass censer on a short cord from the belt: a lid, the ember glowing through a band of slots, a round pot. */
	static void censer(Mesh me, Style st, double[] from, Body b) {
		double sc = b.female ? 0.85 : 1;
		double[] top = add(from, new double[] { -1.5, 7 * sc, -4.5 });
		cord(me, new double[][] { add(from, new double[] { 0, 0, -0.8 }), lerp(from, top, 0.5), top }, 0.5, 3, LadderWeapons.BRASS_DARK, BELLY);
		double r = 3.4 * sc;
		double[][] prof = { { 0, 0 }, { r * 0.75, 2.6 * sc }, { r, 3.4 * sc }, { r, 5.4 * sc }, { r * 0.7, 8.0 * sc }, { 0, 8.8 * sc } };
		int[] cols = { DraftArmourA.BRASS_LIGHT, LadderWeapons.BRASS, st.core, LadderWeapons.BRASS, LadderWeapons.BRASS_DARK };
		int sides = 6;
		int[][] rg = new int[prof.length][];
		for (int k = 0; k < prof.length; k++) {
			double[] c = add(top, new double[] { 0, prof[k][1], 0 });
			rg[k] = new int[sides];
			for (int i = 0; i < sides; i++) {
				double a = Math.PI * 2 * i / sides;
				rg[k][i] = me.v(c[0] + Math.sin(a) * prof[k][0], c[1], c[2] - Math.cos(a) * prof[k][0], BELLY);
			}
		}
		me.prio = 5;
		double[] axis = add(top, new double[] { 0, 4 * sc, 0 });
		for (int k = 0; k + 1 < prof.length; k++) {
			for (int i = 0; i < sides; i++) {
				int j = (i + 1) % sides;
				int col = k == 2 ? (i % 2 == 0 ? st.core : LadderWeapons.BRASS_DARK) : cols[k];
				if (k == 0) {
					me.tri(rg[0][i], rg[1][j], rg[1][i], col, axis);
				} else if (k == prof.length - 2) {
					me.tri(rg[k][i], rg[k][j], rg[k + 1][i], col, axis);
				} else {
					me.quad(rg[k][i], rg[k][j], rg[k + 1][j], rg[k + 1][i], col, axis);
				}
			}
		}
	}

	/* ------------------------------------------------------------- the trousers */

	/**
	 * Loose trousers: from a waistband on the still pelvis (41) down the thighs (44 / 43), ballooning below the knee
	 * (shins 33 / 36), gathered at the shin by a rope tie, then pale wraps wound tight down to the ankle (32 / 38).
	 */
	static ModelData trousers(Style st, Body b) {
		Mesh me = new Mesh();
		int n = 8;
		for (int s = 0; s < 2; s++) {
			double kneeY = b.knee[s][1], ankY = b.ankle[s][1], tieY = ankY - 20;
			// y, margin, label; rows past the tie are the leg-wraps
			double[][] rows = { { b.waistY + 1, 1.8, HIPS }, { b.hipJointY + 2, 3.2, THIGH[s] }, { (b.hipJointY + kneeY) / 2, 4.2, THIGH[s] },
					{ kneeY - 2, 5.0, THIGH[s] }, { kneeY + 8, 6.2, SHIN[s] }, { tieY - 3.5, 6.6, SHIN[s] }, { tieY, 1.6, SHIN[s] }, { tieY + 1, 1.4, SHIN[s] },
					{ (tieY + ankY) / 2 + 1, 1.2, SHIN[s] }, { ankY + 2.5, 1.1, ANKLE[s] } };
			int[][] rg = new int[rows.length][];
			double[][] q = new double[rows.length][];
			for (int r = 0; r < rows.length; r++) {
				q[r] = legAt(b, rows[r][0], s, rows[r][1]);
				if (r == 0) { // the seat: the two tops meet in the middle
					q[r][0] = sx(s) * Math.max(Math.abs(q[r][0]), 8.5);
					q[r][2] = Math.max(q[r][2], 10);
				} else {
					inner(q[r], s, 1.5);
				}
				rg[r] = new int[n];
				for (int j = 0; j < n; j++) {
					double a = Math.PI * 2 * j / n + Math.PI / n;
					rg[r][j] = me.v(q[r][0] + Math.sin(a) * q[r][2], rows[r][0], q[r][1] - Math.cos(a) * q[r][3], (int) rows[r][2]);
				}
			}
			me.prio = 1;
			for (int r = 0; r + 1 < rows.length; r++) {
				int[] cols;
				if (r == 5) {
					cols = new int[] { st.dark, st.trimDark }; // the gathered folds
				} else if (r >= 6) {
					cols = new int[] { (r % 2 == 0) ? WRAP : WRAP_DARK, (r % 2 == 0) ? WRAP_DARK : WRAP };
				} else {
					cols = new int[] { st.dark };
				}
				double cx = (q[r][0] + q[r + 1][0]) / 2, cz = (q[r][1] + q[r + 1][1]) / 2;
				me.band(rg[r], rg[r + 1], true, cols, cx, cz, false);
			}
			// the tie: a rope band round the gather, its ends knotted at the outside
			double[] t = inner(legAt(b, tieY - 0.5, s, 2.6), s, 1.5);
			int[] t0 = new int[n], t1 = new int[n];
			for (int j = 0; j < n; j++) {
				double a = Math.PI * 2 * j / n;
				t0[j] = me.v(t[0] + Math.sin(a) * t[2], tieY - 2.2, t[1] - Math.cos(a) * t[3], SHIN[s]);
				t1[j] = me.v(t[0] + Math.sin(a) * t[2], tieY + 0.8, t[1] - Math.cos(a) * t[3], SHIN[s]);
			}
			me.prio = 2;
			me.band(t0, t1, true, new int[] { ROPE, ROPE_LIGHT }, t[0], t[1], false);
			double[] k = { t[0] + sx(s) * (t[2] + 0.6), tieY - 0.7, t[1] - 1 };
			bead(me, k, new double[] { sx(s), 0, -0.3 }, 1.6, ROPE_LIGHT, ROPE, SHIN[s]);
			cord(me, new double[][] { k, add(k, new double[] { sx(s) * 1.2, 4.5, -1 }) }, 0.6, 3, ROPE, SHIN[s]);
		}
		anchors(me, b.legs, st.dark);
		return me.done();
	}

	/* ------------------------------------------------------------- sandals, wraps */

	/** The kit's feet in pale socks on thick straw soles, the cords crossed over the instep and tied round the ankle. */
	static ModelData sandals(Style st, Body b) {
		Mesh me = new Mesh();
		copyKit(me, b.feet, (c, o) -> WRAP, 1);
		for (int s = 0; s < 2; s++) {
			double sgn = sx(s);
			double x0 = 1e9, x1 = -1e9, z0 = 1e9, z1 = -1e9, sole = -1e9;
			for (int[] v : b.feet.vertices) {
				if (v[0] * sgn > 0) {
					x0 = Math.min(x0, v[0]);
					x1 = Math.max(x1, v[0]);
					z0 = Math.min(z0, v[2]);
					z1 = Math.max(z1, v[2]);
					sole = Math.max(sole, v[1]);
				}
			}
			double cx = (x0 + x1) / 2, hw = (x1 - x0) / 2 + 1.2;
			double[][] out = { { -hw * 0.7, z0 - 2 }, { -hw, z0 + 5 }, { -hw * 0.95, z1 - 4 }, { -hw * 0.7, z1 + 1.8 }, { hw * 0.7, z1 + 1.8 }, { hw * 0.95, z1 - 4 },
					{ hw, z0 + 5 }, { hw * 0.7, z0 - 2 } };
			int m = out.length;
			int[] tp = new int[m], bt = new int[m];
			double yT = sole - 1.6, yB = sole + 2.2;
			for (int i = 0; i < m; i++) {
				double[] p = { cx + out[i][0], yT, out[i][1] };
				int lab = nearest(b.feet, p);
				tp[i] = me.v(p, lab);
				bt[i] = me.v(cx + out[i][0] * 0.96, yB, out[i][1], lab);
			}
			me.prio = 2;
			double[] mid = { cx, (yT + yB) / 2, (z0 + z1) / 2 };
			for (int i = 0; i < m; i++) {
				int j = (i + 1) % m;
				me.quad(tp[i], tp[j], bt[j], bt[i], i % 2 == 0 ? STRAW_DARK : STRAW, mid);
			}
			for (int i = 1; i + 1 < m; i++) {
				me.triDir(tp[0], tp[i], tp[i + 1], STRAW, new double[] { 0, -1, 0 });
				me.triDir(bt[0], bt[i], bt[i + 1], ROPE, new double[] { 0, 1, 0 });
			}
			// the cords: crossed over the instep from the sole's sides, and round the ankle
			double ay = sole - 13;
			double[] an = legSliceFeet(b, ay, s);
			int[] a0 = new int[6], a1 = new int[6];
			for (int j = 0; j < 6; j++) {
				double a = Math.PI * 2 * j / 6;
				a0[j] = me.v(an[0] + Math.sin(a) * (an[2] + 1.0), ay - 1, an[1] - Math.cos(a) * (an[3] + 1.0), ANKLE[s]);
				a1[j] = me.v(an[0] + Math.sin(a) * (an[2] + 1.1), ay + 1.2, an[1] - Math.cos(a) * (an[3] + 1.1), ANKLE[s]);
			}
			me.prio = 3;
			me.band(a0, a1, true, new int[] { ROPE }, an[0], an[1], false);
			for (int k = 0; k < 2; k++) {
				double side = k == 0 ? -1 : 1;
				double[] p0 = { cx + side * hw * 0.92, yT - 0.2, z0 + 4 }, p2 = { cx - side * (an[2] + 0.6), ay + 1, an[1] - an[3] * 0.4 };
				double[] p1 = lerp(p0, p2, 0.5);
				double[] top = ArmourForge.surface(b.feet, new double[] { p1[0], -60, p1[2] }, new double[] { 0, 1, 0 });
				if (top != null) {
					p1 = new double[] { p1[0], top[1] - 1.0, p1[2] };
				}
				int l0 = nearest(b.feet, p0), l1 = nearest(b.feet, p1);
				double[][] path = { p0, p1, p2 };
				int[] prev = null;
				for (int i = 0; i < 3; i++) {
					double[] d = norm(sub(path[Math.min(i + 1, 2)], path[Math.max(i - 1, 0)]));
					int[] cur = ring(me, path[i], d, 0.7, 0.7, 3, 0.3, i == 0 ? l0 : i == 1 ? l1 : ANKLE[s]);
					if (prev != null) {
						me.tube(prev, cur, new int[] { ROPE }, path[i - 1], path[i]);
					}
					prev = cur;
				}
			}
		}
		return me.done();
	}

	/** The feet kit's section at y on one side: {cx, cz, rx, rz}. */
	static double[] legSliceFeet(Body b, double y, int s) {
		double[] q = BaseCoat.slice(b.feet, y, s == 0 ? -1 : 1);
		return new double[] { (q[0] + q[1]) / 2, (q[2] + q[3]) / 2, (q[1] - q[0]) / 2, (q[3] - q[2]) / 2 };
	}

	/** The kit's hands, wound in pale cloth over the wrist and palm, the fingers bare. */
	static ModelData wraps(Style st, Body b) {
		Mesh me = new Mesh();
		int[] hb = b.hands.bounds();
		copyKit(me, b.hands, (c, o) -> {
			int s = c[0] < 0 ? 0 : 1;
			return c[1] < b.hand[s][1] + 1.5 ? (((int) Math.floor((c[1] - hb[1]) / 3.0)) % 2 == 0 ? WRAP : WRAP_DARK) : SKIN;
		}, 1);
		for (int s = 0; s < 2; s++) {
			double[] h = b.hand[s], w = b.wrist[s];
			double[] axis = norm(sub(h, w));
			// two wound bands: round the wrist (forearm label), round the palm (hand label, centred on the hand)
			double[] q0 = BaseCoat.slice(b.hands, w[1] - 2, s == 0 ? -1 : 1), q1 = BaseCoat.slice(b.hands, h[1] - 1, s == 0 ? -1 : 1);
			double rw = Math.max(q0[1] - q0[0], q0[3] - q0[2]) / 2 + 1.0, rh = Math.max(q1[1] - q1[0], q1[3] - q1[2]) / 2 + 0.8;
			double[] wc = { (q0[0] + q0[1]) / 2, w[1] - 2, (q0[2] + q0[3]) / 2 }, hc = { (q1[0] + q1[1]) / 2, h[1] - 1, (q1[2] + q1[3]) / 2 };
			int[] r0 = ring(me, add(wc, scale(axis, -3)), axis, rw, rw, 6, 0, FORE[s]), r1 = ring(me, add(wc, scale(axis, 2)), axis, rw + 0.4, rw + 0.4, 6, 0.5, FORE[s]);
			me.prio = 2;
			me.tube(r0, r1, new int[] { WRAP, WRAP_DARK }, add(wc, scale(axis, -3)), add(wc, scale(axis, 2)));
			int[] p0 = ring(me, add(hc, scale(axis, -2.5)), axis, rh, rh * 0.8, 6, 0, HAND[s]), p1 = ring(me, add(hc, scale(axis, 2.5)), axis, rh, rh * 0.8, 6, 0.5,
					HAND[s]);
			me.tube(p0, p1, new int[] { WRAP_DARK, WRAP }, add(hc, scale(axis, -2.5)), add(hc, scale(axis, 2.5)));
		}
		return me.done();
	}
}
