package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ArmourForge.add;
import static com.rs2.tools.mapgen.ArmourForge.norm;
import static com.rs2.tools.mapgen.ArmourForge.scale;
import static com.rs2.tools.mapgen.ArmourForge.sub;
import static com.rs2.tools.mapgen.BaseMantle.ell;
import static com.rs2.tools.mapgen.BaseMantle.lerp;
import static com.rs2.tools.mapgen.BaseMantle.sx;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.rs2.tools.mapgen.ArmourSets.Style;
import com.rs2.tools.mapgen.BaseMantle.Body;
import com.rs2.tools.mapgen.BaseMantle.Mesh;

/**
 * The Starless shroud base (A37, Kind.MANTLE): robes cut from the Starless's own shadow, so the wearer reads as a
 * floating wraith: a tall cone of a cowl over a black void, narrow sloping shoulders, sleeves that run out past the
 * hands into ragged points, and a long robe frayed into a hem of points that trail on the floor. Built from scratch
 * on the default body (see NewBases); none of the old mantle's hood, tunic or cape.
 *
 * <ul>
 * <li>Helm ("mask", so the head and beard hide): a tall pointed cowl rising well above the head and leaning forward
 * to its tip, its skirt spread over the shoulders; the face is a recessed black void with two faint violet eye-points;
 * a thin crescent hangs behind the head as a halo, five star points along it.</li>
 * <li>Top ("robe top", so the arms kit hides: the sleeves are the arms): a narrow-shouldered shroud with a violet
 * seam open down the front, a short ragged overhang at the hips, and long sleeves that flare at the wrist and taper
 * into points well past the hands (the hands show inside). A ring of star points floats behind the shoulders.</li>
 * <li>Robe bottom: a robe flaring from the waist to mid-shin, then frayed into a hem of long points that reach the
 * floor, the back ones trailing behind; so the legs and feet hardly show.</li>
 * <li>Boots and gloves: barely-there dark wraps: the body's own feet and hands kits, darkened, with a wrap band and a
 * loose ragged end.</li>
 * </ul>
 *
 * <p>Animation labels. No new vertex takes a pivot label (the player's frames turn each joint about the centroid of
 * certain labels: the neck 2 / 3, shoulders 21 / 25, elbows 17 / 23, hands 27 / 28, waist 29 / 30 / 5, hips 40 / 42,
 * knees 34 + 31 / 35 + 37, 39, feet 47 / 48, the root 0). Where a piece hides a kit (the mask hides the head and jaw,
 * the top the torso and arms, the robe the legs), {@link #anchors} adds, per pivot label the kit carried, a
 * tiny triangle hidden in the limb whose centroid is the kit's centroid of the label (the client drops vertices no face uses when
 * it merges the player), so every joint still turns where it does on the bare body. Otherwise: the whole cowl, eyes and
 * crescent are on the skull 1; the shroud's torso on the chest 8 and belly 4, its overhang and the star ring too
 * (rigid to the torso); each sleeve on the upper arm 20 / 26 to the elbow (18 / 24 at the elbow ring) and the forearm
 * 19 / 22 from there to its points; the robe's waist row on the belly 4 and each half (split front and back, hidden in
 * the hem's points) on its thigh 43 / 44, so the halves swing with the legs and the slits open in the stride instead of
 * tearing. The wraps keep the kits' own labels; their bands are on the ankle 32 / 38 and the forearm 19 / 22.
 */
final class BaseWraith {

	private BaseWraith() {
	}

	/** Labels a player sequence turns a joint about (the centroid of their vertices): never on new geometry. */
	static final Set<Integer> PIVOTS = Set.of(0, 2, 3, 5, 6, 9, 10, 13, 14, 17, 21, 23, 25, 27, 28, 29, 30, 31, 34, 35, 37, 39, 40, 42, 47, 48);

	static final int SKULL = 1, CHEST = 8, BELLY = 4;
	static final int[] UPPER = { 20, 26 }, ELBOW = { 18, 24 }, FORE = { 19, 22 }, THIGH = { 44, 43 }, ANKLE = { 32, 38 };

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		Body b = BaseMantle.body(af, female);
		switch (slot) {
		case ArmourSets.HELM:
			return new ModelData[] { anchors(cowl(st, b), b.head), null };
		case ArmourSets.BODY:
			return new ModelData[] { anchors(shroud(st, b), b.torso, b.arms), null };
		case ArmourSets.LEGS:
			return new ModelData[] { anchors(robe(st, b), b.legs), null };
		case ArmourSets.BOOTS:
			return new ModelData[] { footWraps(st, b), null };
		case ArmourSets.GLOVES:
			return new ModelData[] { handWraps(st, b), null };
		default:
			return new ModelData[] { donor, donorSecond };
		}
	}

	/** Landmarks of the pieces this class built, for the set's decorations (see {@link #mark}). */
	private static final Map<ModelData, Map<String, double[]>> MARKS = java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

	/** A landmark of a piece built here, or null: the shroud's "torso" {neckY, waistY, half-width}, the robe's "robe" {waistY, hemY}. */
	static double[] mark(ModelData m, String key) {
		Map<String, double[]> k = MARKS.get(m);
		return k == null ? null : k.get(key);
	}

	static ModelData mark(ModelData m, String key, double... v) {
		MARKS.computeIfAbsent(m, x -> new java.util.HashMap<>()).put(key, v);
		return m;
	}

	/** The inventory model: the usual fit from the worn model. */
	static ModelData inventory(ArmourForge af, Style st, int slot, ModelData[] worn) throws IOException {
		return null;
	}

	/* ====================================================================== */
	/*                           SHARED (A37-A39)                             */
	/* ====================================================================== */

	/**
	 * Appends to {@code m}, for each pivot label the hidden kits carry, a tiny triangle of that label whose centroid is the
	 * kits' centroid of the label (a face, so the client keeps its vertices when it merges the player): every joint the kits gave a centre to
	 * keeps that centre, though the kit isn't drawn. Null kits are skipped.
	 */
	static ModelData anchors(ModelData m, ModelData... kits) {
		Map<Integer, double[]> sum = new TreeMap<>();
		for (ModelData k : kits) {
			if (k == null) {
				continue;
			}
			for (int i = 0; i < k.vertices.size(); i++) {
				int l = k.vertexSkins.get(i);
				if (PIVOTS.contains(l)) {
					int[] v = k.vertices.get(i);
					double[] s = sum.computeIfAbsent(l, x -> new double[4]);
					s[0] += v[0];
					s[1] += v[1];
					s[2] += v[2];
					s[3]++;
				}
			}
		}
		int skin = m.skin, shading = m.shading;
		m.shading = 0;
		for (Map.Entry<Integer, double[]> e : sum.entrySet()) {
			double[] s = e.getValue();
			m.skin = e.getKey();
			double x = Math.round(s[0] / s[3]), y = Math.round(s[1] / s[3]), z = Math.round(s[2] / s[3]);
			// three vertices a unit apart, their offsets summing to zero: the centroid is the kits', and the face is too small to see
			m.tri(m.vertex(x + 1, y, z), m.vertex(x - 1, y + 1, z), m.vertex(x, y - 1, z), 0);
		}
		m.skin = skin;
		m.shading = shading;
		return m;
	}

	/** A copy of a body kit (its own labels, so its joints stay exactly where they were) in one colour. */
	static ModelData kitCopy(ModelData kit, int col) {
		ModelData m = new ModelData();
		m.shading = 0;
		for (int i = 0; i < kit.vertices.size(); i++) {
			m.vertices.add(kit.vertices.get(i).clone());
			m.vertexSkins.add(kit.vertexSkins.get(i));
		}
		for (int i = 0; i < kit.faces.size(); i++) {
			m.faces.add(kit.faces.get(i).clone());
			m.colors.add(col);
			m.renderTypes.add(0);
			m.priorities.add(0);
			m.alphas.add(0);
			m.faceSkins.add(0);
		}
		return m;
	}

	/** A star point seen from any side: an octahedron at c with half-extents r (x, y, z), its upper faces in col, lower in col2. */
	static void sparkle(Mesh me, double[] c, double[] r, int label, int col, int col2) {
		int[] p = new int[6];
		for (int k = 0; k < 6; k++) {
			double[] d = new double[3];
			d[k / 2] = (k % 2 == 0 ? -1 : 1) * r[k / 2];
			p[k] = me.v(add(c, d), label);
		}
		int[][] f = { { 0, 2, 4 }, { 2, 1, 4 }, { 1, 3, 4 }, { 3, 0, 4 }, { 2, 0, 5 }, { 1, 2, 5 }, { 3, 1, 5 }, { 0, 3, 5 } };
		for (int[] t : f) {
			me.tri(p[t[0]], p[t[1]], p[t[2]], t[0] == 2 || t[1] == 2 ? col : col2, c);
		}
	}

	/**
	 * A flat plate, seen from both sides: the outline (points in the plane through o spanned by u, v) fanned from its
	 * middle, the face towards n in {@code front}, the other in {@code back}, thickness 2t, the rim in {@code side}.
	 */
	static void plate(Mesh me, double[] o, double[] u, double[] v, double[] n, double[][] outline, double t, int label, int front, int back, int side) {
		int k = outline.length;
		int[] a = new int[k], b = new int[k];
		double cu = 0, cv = 0;
		for (double[] q : outline) {
			cu += q[0] / k;
			cv += q[1] / k;
		}
		double[] c = add(o, add(scale(u, cu), scale(v, cv)));
		for (int i = 0; i < k; i++) {
			double[] p = add(o, add(scale(u, outline[i][0]), scale(v, outline[i][1])));
			a[i] = me.v(add(p, scale(n, t)), label);
			b[i] = me.v(add(p, scale(n, -t)), label);
		}
		int ca = me.v(add(c, scale(n, t)), label), cb = me.v(add(c, scale(n, -t)), label);
		for (int i = 0; i < k; i++) {
			int j = (i + 1) % k;
			me.tri(a[i], a[j], ca, front, add(c, scale(n, -50)));
			me.tri(b[i], b[j], cb, back, add(c, scale(n, 50)));
			if (side >= 0) {
				me.quad(a[i], a[j], b[j], b[i], side, c);
			}
		}
	}

	/** A four-pointed star outline (frame units): outer radius r, inner ri, points up, down and to the sides. */
	static double[][] star4(double r, double ri) {
		double[][] p = new double[8][];
		for (int k = 0; k < 8; k++) {
			double a = Math.PI / 4 * k, rr = k % 2 == 0 ? r : ri;
			p[k] = new double[] { Math.sin(a) * rr, -Math.cos(a) * rr };
		}
		return p;
	}

	/** A double-sided triangle a, b, tip: one face in col, the other in back. */
	static void flap(Mesh me, int a, int b, int tip, int col, int back, double[] inside) {
		me.tri(a, b, tip, col, inside);
		double[] g = scale(add(add(me.p(a), me.p(b)), me.p(tip)), 1 / 3.0);
		me.tri(a, b, tip, back, sub(scale(g, 2), inside));
	}

	/* ====================================================================== */
	/*                                THE COWL                                */
	/* ====================================================================== */

	/** The void's black. */
	static final int VOID_BLACK = ModelData.hsl(44, 2, 2);
	/** The crescent's pale violet. */
	static final int CRESCENT = ArmourSets.violet(88, 3), CRESCENT_DARK = ArmourSets.violet(60, 3);

	/**
	 * The cowl: rings from a skirt over the shoulders up round the head and on up into a tall point leaning forward,
	 * open over the face from brow to chin round a recessed black void with two violet eye-points; a crescent of stars
	 * behind the head. All on the skull.
	 */
	static ModelData cowl(Style st, Body b) {
		Mesh me = new Mesh();
		double H = b.headTop, C = b.headChin, fz = b.headFront, bz = b.headBack, hw = b.headHW;
		double hc = (fz + bz) / 2, hd = (bz - fz) / 2, hs = (C - H) / 26.0;
		// y, z centre, half-width, half-depth in front, behind
		double[][] R = { { b.neckY + 7, hc + 4, hw + 14, hd + 4, hd + 10 }, { C + 1, hc + 1, hw + 6, hd + 4, hd + 5.5 },
				{ H + (C - H) * 0.48, hc, hw + 5.5, hd + 5, hd + 5.5 }, { H - 1.5, hc - 1, hw + 4.5, hd + 4.5, hd + 4.5 },
				{ H - 13 * hs, hc - 4, hw - 0.5, hd - 1.5, hd - 1 }, { H - 25 * hs, hc - 8.5, 6.8, 6.8, 7.2 }, { H - 36 * hs, hc - 13.5, 3.6, 3.6, 3.8 } };
		double[] tip = { 0, H - 47 * hs, hc - 20 };
		int n = 10, NR = R.length;
		int[][] g = new int[NR][n];
		double[][] cen = new double[NR][];
		for (int r = 0; r < NR; r++) {
			cen[r] = new double[] { 0, R[r][0], R[r][1] };
			for (int i = 0; i < n; i++) {
				g[r][i] = me.v(ell(0, R[r][0], R[r][1], R[r][2], R[r][3], R[r][4], 360.0 * i / n), SKULL);
			}
		}
		// the opening: a pointed arch at the brow, narrowing to the chin
		BaseMantle.move(me, g[3][0], 0, -3.5 * hs, -1.5);
		for (int i : new int[] { 1, 9 }) {
			BaseMantle.scaleX(me, g[1][i], 0.72);
			BaseMantle.scaleX(me, g[3][i], 0.85);
			BaseMantle.move(me, g[3][i], 0, 2 * hs, 0);
		}
		BaseMantle.move(me, g[1][0], 0, 1.5 * hs, 0);
		for (int r = 0; r + 1 < NR; r++) {
			double[] in = lerp(cen[r], cen[r + 1], 0.5);
			for (int i = 0; i < n; i++) {
				int j = (i + 1) % n;
				boolean open = (r == 1 || r == 2) && (i == 0 || j == 0);
				if (!open) {
					int col = r == 0 ? st.dark : r >= 4 && i % 2 == 1 ? st.light : st.main;
					me.quad(g[r][i], g[r][j], g[r + 1][j], g[r + 1][i], col, in);
				}
			}
		}
		me.fan(g[NR - 1], tip, SKULL, st.light, cen[NR - 1]);
		// the underside of the skirt, seen from below
		int[] under = new int[n];
		for (int i = 0; i < n; i++) {
			double[] p = me.p(g[0][i]);
			under[i] = me.v(lerp(new double[] { 0, p[1] - 4, R[0][1] }, p, 0.62), SKULL);
		}
		me.bandIn(g[0], under, true, VOID_BLACK, new double[] { 0, R[0][0] + 30, R[0][1] });
		// the skirt frayed into points over the shoulders, chest and back
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			double[] p = lerp(me.p(g[0][i]), me.p(g[0][j]), 0.5);
			double len = (i % 2 == 0 ? 11 : 7) * hs;
			double[] t = { p[0] * 1.1, p[1] + len, R[0][1] + (p[2] - R[0][1]) * 1.1 };
			flap(me, g[0][i], g[0][j], me.v(t, SKULL), st.dark, VOID_BLACK, new double[] { 0, R[0][0] - 6, R[0][1] });
		}
		// the lip round the opening, and the void behind it
		int[] loop = { g[3][9], g[3][0], g[3][1], g[2][1], g[1][1], g[1][0], g[1][9], g[2][9] };
		double[] mid = { 0, R[2][0], R[2][1] };
		int[] lip = new int[loop.length];
		for (int i = 0; i < loop.length; i++) {
			double[] p = me.p(loop[i]);
			double[] to = sub(mid, p);
			to[2] = 0;
			double l = Math.hypot(to[0], to[1]) + 1e-9;
			lip[i] = me.v(add(p, new double[] { to[0] / l * 2.2, to[1] / l * 2.2, 2.8 }), SKULL);
		}
		double[] front = { 0, R[2][0], fz - 40 };
		me.prio = 1;
		for (int i = 0; i < loop.length; i++) {
			int j = (i + 1) % loop.length;
			me.quadIn(loop[i], loop[j], lip[j], lip[i], st.trim, front);
		}
		int deep = me.v(new double[] { 0, R[2][0], fz + 8 }, SKULL);
		for (int i = 0; i < lip.length; i++) {
			me.triIn(lip[i], lip[(i + 1) % lip.length], deep, VOID_BLACK, front);
		}
		// the eyes: two faint violet points in the dark
		me.prio = 2;
		double eyeY = H + (C - H) * 0.5, eyeZ = fz + 1.5;
		for (int s = -1; s <= 1; s += 2) {
			sparkle(me, new double[] { s * 3.6 * hs, eyeY, eyeZ }, new double[] { 1.7, 0.9, 0.6 }, SKULL, ArmourSets.violet(96, 6), ArmourSets.violet(70, 6));
		}
		// the crescent behind the head, horns down, thickest over the crown; stars along it
		me.prio = 0;
		double[] cc = { 0, H + (C - H) * 0.36, bz + 9 };
		double ro = 24 * hs;
		int seg = 10;
		int[] oa = new int[seg + 1], ob = new int[seg + 1], ia = new int[seg + 1], ib = new int[seg + 1];
		double[] u = { 1, 0, 0 }, v = { 0, -1, 0 }, nz = { 0, 0, 1 };
		for (int k = 0; k <= seg; k++) {
			double t = (double) k / seg, a = Math.toRadians(14 + 152 * t);
			double w = 0.6 + 3.8 * Math.sin(Math.PI * t);
			double[] po = add(cc, add(scale(u, Math.cos(a) * ro), scale(v, Math.sin(a) * ro)));
			double[] pi = add(cc, add(scale(u, Math.cos(a) * (ro - w)), scale(v, Math.sin(a) * (ro - w))));
			oa[k] = me.v(add(po, scale(nz, -0.7)), SKULL);
			ob[k] = me.v(add(po, scale(nz, 0.7)), SKULL);
			ia[k] = me.v(add(pi, scale(nz, -0.7)), SKULL);
			ib[k] = me.v(add(pi, scale(nz, 0.7)), SKULL);
		}
		for (int k = 0; k < seg; k++) {
			me.quad(oa[k], oa[k + 1], ia[k + 1], ia[k], CRESCENT, add(cc, scale(nz, 30)));
			me.quad(ob[k], ob[k + 1], ib[k + 1], ib[k], CRESCENT, add(cc, scale(nz, -30)));
			me.quad(oa[k], oa[k + 1], ob[k + 1], ob[k], CRESCENT_DARK, cc);
		}
		me.prio = 1;
		double[] sa = { 22, 56, 90, 124, 158 }, ss = { 3.0, 3.6, 5.0, 3.6, 3.0 };
		for (int k = 0; k < sa.length; k++) {
			double a = Math.toRadians(sa[k]);
			double[] p = add(cc, add(scale(u, Math.cos(a) * (ro + 0.6)), scale(v, Math.sin(a) * (ro + 0.6))));
			plate(me, p, u, new double[] { 0, 1, 0 }, nz, star4(ss[k] * hs, ss[k] * hs * 0.32), 0.9, SKULL, st.accent, st.accent, -1);
		}
		me.prio = 0;
		return me.done();
	}

	/* ====================================================================== */
	/*                               THE SHROUD                               */
	/* ====================================================================== */

	/** The top: torso, the front seam, the ragged overhang, the sleeves and the ring of stars behind the shoulders. */
	static ModelData shroud(Style st, Body b) {
		Mesh me = new Mesh();
		ModelData kit = ArmourForge.mergeAll(b.torso, b.legs);
		double neck = b.neckY, waist = b.waistY, h = waist - neck, cz = (b.chestZ + b.backZ) / 2;
		double[] ys = { neck + 1, neck + 7, neck + h * 0.42, neck + h * 0.78, waist + 3, waist + 11 };
		double[] out = { 1.2, 1.4, 1.5, 1.7, 2.4, 3.6 };
		double[] flare = { 0, 0, 0, 0, 0.6, 3.4 };
		int n = 8, NR = ys.length;
		int[][] rg = new int[NR][];
		for (int r = 0; r < NR; r++) {
			rg[r] = BaseMantle.fitted(me, kit, ys[r], cz, n, out[r], b.neckR + 2, flare[r], r <= 2 ? CHEST : BELLY);
		}
		for (int r = 0; r + 1 < NR; r++) {
			me.band(rg[r], rg[r + 1], true, r == NR - 2 ? st.dark : st.main, new double[] { 0, (ys[r] + ys[r + 1]) / 2, cz });
		}
		// the seam down the front, the void showing through
		me.prio = 2;
		int[] sl = new int[NR - 1], sr = new int[NR - 1];
		for (int r = 1; r < NR; r++) {
			double[] f = me.p(rg[r][0]);
			int lab = me.m.vertexSkins.get(rg[r][0]);
			double w = r == 1 ? 0.6 : 1.4 + 0.25 * r;
			sl[r - 1] = me.v(f[0] - w, f[1], f[2] - 0.6, lab);
			sr[r - 1] = me.v(f[0] + w, f[1], f[2] - 0.6, lab);
		}
		for (int r = 0; r + 1 < sl.length; r++) {
			me.quad(sl[r], sr[r], sr[r + 1], sl[r + 1], st.core, new double[] { 0, ys[r + 1], cz });
		}
		me.prio = 0;
		// the ragged overhang: the hem frayed into points over the robe
		int[] hem = rg[NR - 1];
		double[] hc = { 0, ys[NR - 1], cz };
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			double[] a = me.p(hem[i]), c = me.p(hem[j]);
			double[] m = lerp(a, c, 0.5);
			double len = 7 + 4 * ((i * 5) % 3);
			double[] t = add(m, new double[] { m[0] * 0.08, len, (m[2] - cz) * 0.08 });
			flap(me, hem[i], hem[j], me.v(t, BELLY), st.dark, VOID_BLACK, hc);
		}
		for (int s = 0; s < 2; s++) {
			sleeve(me, st, b, s);
		}
		// the ring of star points floating behind the shoulders
		double[] rc = { 0, (b.shoulder[0][1] + b.shoulder[1][1]) / 2 + 3, b.backZ + 15 };
		double rr = b.torsoHW + 10;
		for (int k = 0; k < 11; k++) {
			double a = Math.PI * 2 * k / 11;
			double big = k == 0 ? 1.6 : k % 3 == 0 ? 1.2 : 0.85;
			double[] p = { Math.sin(a) * rr, rc[1] - Math.cos(a) * rr, rc[2] + Math.cos(a) * 2 };
			sparkle(me, p, new double[] { 1.9 * big, 3.4 * big, 1.2 * big }, CHEST, st.accent, k % 2 == 0 ? st.core : ArmourSets.violet(64, 4));
		}
		return mark(me.done(), "torso", neck, waist, b.torsoHW);
	}

	/**
	 * A sleeve: from the shoulder (upper arm label) down the arm, flaring at the wrist, then running out past the hand
	 * into eight ragged points (forearm label), the inside lined in black.
	 */
	static void sleeve(Mesh me, Style st, Body b, int s) {
		double sc = b.female ? 0.88 : 1, sg = sx(s);
		double[] sh = b.shoulder[s], el = b.elbow[s], wr = b.wrist[s], hd = b.hand[s];
		double[][] c = { add(sh, new double[] { -sg * 1.5, 1.5, 0 }), lerp(sh, el, 0.5), el, lerp(el, wr, 0.5), add(wr, new double[] { sg * 1.5, 1, 0.5 }),
				new double[] { hd[0] + sg * 3, b.handBottom - 4, hd[2] + 1 } };
		double[][] r = { { 6.6 * sc, 7.2 * sc }, { 5.8 * sc, 6.0 * sc }, { 6.4 * sc, 6.8 * sc }, { 8.8 * sc, 9.4 * sc },
				{ 12.0 * sc, 12.8 * sc }, { 13.6 * sc, 14.4 * sc } };
		int[] labs = { UPPER[s], UPPER[s], ELBOW[s], FORE[s], FORE[s], FORE[s] };
		int n = 8, NR = c.length;
		int[][] rg = new int[NR][n];
		for (int k = 0; k < NR; k++) {
			for (int i = 0; i < n; i++) {
				rg[k][i] = me.v(ell(c[k][0], c[k][1], c[k][2], r[k][0], r[k][1], r[k][1], 360.0 * i / n + 22.5), labs[k]);
			}
		}
		for (int k = 0; k + 1 < NR; k++) {
			me.band(rg[k], rg[k + 1], true, k == 4 ? st.dark : st.main, lerp(c[k], c[k + 1], 0.5));
		}
		// the lining inside the bell
		int[] l0 = new int[n], l1 = new int[n];
		for (int i = 0; i < n; i++) {
			l0[i] = me.v(lerp(c[3], me.p(rg[3][i]), 0.9), FORE[s]);
			l1[i] = me.v(lerp(c[5], me.p(rg[5][i]), 0.94), FORE[s]);
		}
		me.bandIn(l0, l1, true, VOID_BLACK, c[4]);
		// the points: the rim frayed into eight, longest behind and outside, drawing in as they fall
		double[] rim = c[5];
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			double[] a = me.p(rg[5][i]), q = me.p(rg[5][j]);
			double[] m = lerp(a, q, 0.5);
			double th = 360.0 * (i + 0.5) / n + 22.5;
			double back = Math.cos(Math.toRadians(th - (s == 0 ? 225 : 135)));
			double len = (14 + 16 * Math.max(0, back) + 5 * ((i * 7) % 3)) * sc;
			double[] t = add(lerp(rim, m, 0.55), new double[] { 0, len, 0 });
			flap(me, rg[5][i], rg[5][j], me.v(t, FORE[s]), st.main, VOID_BLACK, add(rim, new double[] { 0, len * 0.4, 0 }));
		}
	}

	/* ====================================================================== */
	/*                                THE ROBE                                */
	/* ====================================================================== */

	/**
	 * The robe's section at height y: {cz, rx, rz front, rz behind}, flaring from the waist by f (0 at the waist, 1 at
	 * the hem), always a hand's breadth clear of the legs above y.
	 */
	static double[] robeRing(Body b, double y, double f) {
		int[] lb = b.legs.bounds();
		double cz = (lb[2] + lb[5]) / 2.0;
		double rx = b.legsHW + 2 + 16 * f, rf = (lb[5] - lb[2]) / 2.0 + 2 + 13 * f, rb = rf + 6 * f;
		for (double yy = b.legsTop + 2; yy <= Math.min(y, b.knee[0][1]); yy += 2) {
			double[] s = BaseCoat.slice(b.legs, yy, 0);
			double need = 2.5 + 2.5 * f;
			rx = Math.max(rx, Math.max(-s[0], s[1]) + need);
			rf = Math.max(rf, cz - s[2] + need);
			rb = Math.max(rb, s[3] - cz + need);
		}
		return new double[] { cz, rx, rf, rb };
	}

	/**
	 * The robe bottom: two halves (split front and back) from the waist, flaring to mid-shin, frayed into long points
	 * reaching the floor, the ones behind trailing; lined in black.
	 */
	static ModelData robe(Style st, Body b) {
		Mesh me = new Mesh();
		double top = b.legsTop + 1, kneeY = (b.knee[0][1] + b.knee[1][1]) / 2, ankY = (b.ankle[0][1] + b.ankle[1][1]) / 2;
		double hem = lerp(kneeY, ankY, 0.45), floor = b.soleY + 1;
		double[] ys = { top, b.hipJointY - 4, lerp(b.hipJointY, kneeY, 0.55), kneeY + 3, hem };
		int segs = 6, NR = ys.length;
		for (int side = -1; side <= 1; side += 2) {
			int s = side < 0 ? 0 : 1;
			int[][] o = new int[NR][segs + 1], in = new int[NR][segs + 1];
			double[][] ctr = new double[NR][];
			for (int r = 0; r < NR; r++) {
				double f = (ys[r] - top) / (hem - top);
				double[] rr = robeRing(b, ys[r], f);
				double gap = 0.03 + 0.035 * f;
				int lab = r == 0 ? BELLY : THIGH[s];
				ctr[r] = new double[] { 0, ys[r], rr[0] };
				for (int j = 0; j <= segs; j++) {
					double th = side * Math.toDegrees(gap + (Math.PI - 2 * gap) * j / segs);
					o[r][j] = me.v(ell(0, ys[r], rr[0], rr[1], rr[2], rr[3], th), lab);
					in[r][j] = me.v(ell(0, ys[r], rr[0], rr[1] - 0.8, rr[2] - 0.8, rr[3] - 0.8, th), lab);
				}
			}
			for (int r = 0; r + 1 < NR; r++) {
				double[] ax = lerp(ctr[r], ctr[r + 1], 0.5);
				for (int j = 0; j < segs; j++) {
					int col = r >= NR - 2 ? st.dark : st.main;
					me.quad(o[r][j], o[r][j + 1], o[r + 1][j + 1], o[r + 1][j], col, ax);
					if (r >= 2) {
						me.quadIn(in[r][j], in[r][j + 1], in[r + 1][j + 1], in[r + 1][j], VOID_BLACK, ax);
					}
				}
			}
			// the slits' edges
			for (int e : new int[] { 0, segs }) {
				for (int r = 1; r + 1 < NR; r++) {
					double[] p = me.p(o[r][e]);
					double[] toward = { p[0] - side * 5, p[1], p[2] };
					me.quad(o[r][e], o[r + 1][e], in[r + 1][e], in[r][e], st.dark, toward);
				}
			}
			// the hem's points: one per sector, alternating long and short, the back ones trailing on the floor
			double[] hc = ctr[NR - 1];
			for (int j = 0; j < segs; j++) {
				double[] a = me.p(o[NR - 1][j]), c = me.p(o[NR - 1][j + 1]);
				double[] m = lerp(a, c, 0.5);
				double behind = (m[2] - hc[2]) / Math.max(1, Math.abs(m[2] - hc[2]) + Math.abs(m[0]));
				boolean lng = (j + (side < 0 ? 0 : 1)) % 2 == 0;
				double ty = lng ? floor : lerp(hem, floor, 0.55);
				double spread = lng ? 1.12 : 1.04;
				double[] t = { m[0] * spread, ty, hc[2] + (m[2] - hc[2]) * spread + Math.max(0, behind) * (lng ? 9 : 4) };
				flap(me, o[NR - 1][j], o[NR - 1][j + 1], me.v(t, THIGH[s]), lng ? st.dark : st.main, VOID_BLACK, new double[] { 0, hem - 6, hc[2] });
			}
		}
		// the waist band
		int[] lb = b.legs.bounds();
		double cz = (lb[2] + lb[5]) / 2.0;
		int[] w0 = BaseMantle.fitted(me, b.legs, top - 1, cz, 10, 2.6, 8, 0, BELLY), w1 = BaseMantle.fitted(me, b.legs, top + 5, cz, 10, 2.9, 8, 0, BELLY);
		me.prio = 1;
		me.band(w0, w1, true, st.trimDark, new double[] { 0, top + 2, cz });
		me.prio = 0;
		return mark(me.done(), "robe", top, hem);
	}

	/* ====================================================================== */
	/*                                THE WRAPS                               */
	/* ====================================================================== */

	/** The feet, wrapped in shadow: the feet kit darkened, a band round each ankle and a loose end trailing from it. */
	static ModelData footWraps(Style st, Body b) {
		ModelData m = kitCopy(b.feet, VOID_BLACK);
		Mesh me = new Mesh();
		for (int s = 0; s < 2; s++) {
			double[] an = b.ankle[s];
			double sg = sx(s);
			int[] r0 = BaseMantle.ring(me, an[0], an[1] - 6, an[2] + 0.5, 5.6, 6.4, 6.4, 6, 30, ANKLE[s]);
			int[] r1 = BaseMantle.ring(me, an[0], an[1] + 1, an[2] + 0.5, 5.9, 6.8, 6.8, 6, 30, ANKLE[s]);
			me.band(r0, r1, true, st.dark, new double[] { an[0], an[1] - 2, an[2] });
			int a = me.v(new double[] { an[0] + sg * 5, an[1] - 4, an[2] + 4 }, ANKLE[s]), c = me.v(new double[] { an[0] + sg * 5, an[1] - 1, an[2] + 6 }, ANKLE[s]);
			int t = me.v(new double[] { an[0] + sg * 6.5, b.soleY - 2, an[2] + 13 }, ANKLE[s]);
			flap(me, a, c, t, st.dark, VOID_BLACK, an);
		}
		ArmourForge.merge(m, me.done(), null);
		return m;
	}

	/** The hands, wrapped in shadow: the hands kit darkened, a band round each wrist and a loose end hanging from it. */
	static ModelData handWraps(Style st, Body b) {
		ModelData m = kitCopy(b.hands, st.dark);
		Mesh me = new Mesh();
		for (int s = 0; s < 2; s++) {
			double[] w = b.wrist[s];
			double sg = sx(s);
			int[] r0 = BaseMantle.ring(me, w[0], w[1] - 2.5, w[2], 5.0, 5.4, 5.4, 6, 30, FORE[s]);
			int[] r1 = BaseMantle.ring(me, w[0], w[1] + 2, w[2], 5.2, 5.6, 5.6, 6, 30, FORE[s]);
			me.band(r0, r1, true, st.trimDark, w);
			int a = me.v(new double[] { w[0] + sg * 4.5, w[1] - 1, w[2] - 2 }, FORE[s]), c = me.v(new double[] { w[0] + sg * 4.5, w[1] + 1.5, w[2] + 2 }, FORE[s]);
			int t = me.v(new double[] { w[0] + sg * 6, w[1] + 14, w[2] + 1 }, FORE[s]);
			flap(me, a, c, t, st.dark, VOID_BLACK, w);
		}
		ArmourForge.merge(m, me.done(), null);
		return m;
	}
}
