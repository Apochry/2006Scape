package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ArmourForge.add;
import static com.rs2.tools.mapgen.ArmourForge.dot;
import static com.rs2.tools.mapgen.ArmourForge.norm;
import static com.rs2.tools.mapgen.ArmourForge.scale;
import static com.rs2.tools.mapgen.ArmourForge.sub;
import static com.rs2.tools.mapgen.BaseMantle.ell;
import static com.rs2.tools.mapgen.BaseMantle.lerp;
import static com.rs2.tools.mapgen.BaseMantle.sx;

import java.io.IOException;

import com.rs2.tools.mapgen.ArmourSets.Style;
import com.rs2.tools.mapgen.BaseMantle.Body;
import com.rs2.tools.mapgen.BaseMantle.Mesh;

/**
 * The Riftwalker base (A38, Kind.WRAP): a ranger's kit coming apart through a rift. Its plates are separate angular
 * shards hovering a little off the body, and the gaps between them glow violet; it is deliberately lopsided: the right
 * side (the bow arm) is armoured in floating shards, the left bound in black straps under a torn cape. Built from
 * scratch on the default body (see NewBases); nothing of the leather or hide meshes but the rig.
 *
 * <ul>
 * <li>Helm ("mask": head and beard hide): a close faceless hood drawn back to a point, a single glowing seam down the
 * blank face, and riftglass shards floating round the crown, one standing tall over the brow.</li>
 * <li>Top ("top": the arms kit hides, the sleeves are the arms): a dark under-suit glowing violet wherever shards hover
 * over it: shards over the chest and the right flank and shoulder blade, a great shard for a pauldron and more down
 * the right arm; the left arm bound in black straps; a black bandolier across the chest; a torn cape hanging from the
 * left shoulder only, cut away on the slant.</li>
 * <li>Chaps: the under-suit's legs, shards hovering over the right thigh, both knees and the right shin, the left thigh
 * and shin bound in straps, a black belt with a shard at the right hip.</li>
 * <li>Boots: the feet kit in the dark, shards over the toes, the instep and the front of the ankle.</li>
 * <li>Gauntlets: the right a shard gauntlet (shards round the wrist and up the forearm, a band of rift-light at the wrist), the left a strap wrap
 * (black bands round the wrist and the palm).</li>
 * </ul>
 *
 * <p>Animation labels. No new vertex takes a pivot label (see {@link BaseWraith#PIVOTS}); where a piece hides a kit,
 * {@link BaseWraith#anchors} keeps that kit's joint centres on tiny hidden triangles. Each shard is rigid on the label
 * of the limb it floats over, so it follows that limb without touching it: the hood and its shards the skull 1; the
 * chest shards the chest 8, the belly's the belly 4; the pauldron and upper-arm shards the upper arm 20, the elbow's 18,
 * the forearm's 19 (the left arm's straps 26 / 24 / 22 likewise); thigh shards and straps the thighs 43 / 44, knee and
 * shin shards the shins 33 / 36, boot shards the ankles 32 / 38 and toes 45 / 46. The torn cape uses the stock capes'
 * chain (10 at the shoulder, 9 at the waist, 16, 14, 13, 12 below), at the stock cape's heights, so the walk and run
 * swing it back clear of the legs. The gauntlets and boots keep the kits' own labels; the gauntlet's shards are on the
 * forearm 19, the left hand's palm band a ring round the hand's own centre on its label 28 (so the centre stays put).
 */
final class BaseShatter {

	private BaseShatter() {
	}

	static final int SKULL = 1, CHEST = 8, BELLY = 4;
	static final int[] UPPER = { 20, 26 }, ELBOW = { 18, 24 }, FORE = { 19, 22 }, HAND = { 27, 28 }, THIGH = { 44, 43 }, SHIN = { 33, 36 },
			ANKLE = { 32, 38 }, TOES = { 45, 46 };
	/** The stock capes' chain. */
	static final int CAPE_TOP = 10, CAPE_WAIST = 9, CAPE_HIP = 16, CAPE_THIGH = 14, CAPE_KNEE = 13, CAPE_HEM = 12;

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		Body b = BaseMantle.body(af, female);
		switch (slot) {
		case ArmourSets.HELM:
			return new ModelData[] { BaseWraith.separate(BaseWraith.anchors(hood(st, b), b.head)), null };
		case ArmourSets.BODY:
			return new ModelData[] { BaseWraith.separate(BaseWraith.anchors(top(st, b), b.torso, b.arms)), null };
		case ArmourSets.LEGS:
			return new ModelData[] { BaseWraith.separate(BaseWraith.anchors(chaps(st, b), b.legs)), null };
		case ArmourSets.BOOTS:
			return new ModelData[] { BaseWraith.separate(boots(st, b)), null };
		case ArmourSets.GLOVES:
			return new ModelData[] { BaseWraith.separate(gauntlets(st, b)), null };
		default:
			return new ModelData[] { donor, donorSecond };
		}
	}

	/** The inventory model: the usual fit from the worn model. */
	static ModelData inventory(ArmourForge af, Style st, int slot, ModelData[] worn) throws IOException {
		return null;
	}

	/* ====================================================================== */
	/*                                 SHARDS                                 */
	/* ====================================================================== */

	/** Shard outlines: x across, y along, each within -1..1, all convex and angular. */
	static final double[][][] SHAPES = { { { -0.5, -1 }, { 0.55, -0.75 }, { 0.62, 0.35 }, { 0.05, 1 }, { -0.62, 0.5 } },
			{ { -0.35, -1 }, { 0.62, -0.55 }, { 0.42, 0.95 }, { -0.62, 0.7 } }, { { 0.05, -1 }, { 0.62, 0.25 }, { 0.15, 1 }, { -0.6, 0.35 } },
			{ { -0.6, -0.85 }, { 0.3, -1 }, { 0.62, 0.1 }, { 0.35, 0.95 }, { -0.45, 0.75 } } };

	/** The rift's glow, under the shards; riftglass. */
	static final int GLOW = ArmourSets.violet(92, 6), GLOW_DIM = ArmourSets.violet(64, 6), GLASS = ArmourSets.violet(52, 5), GLASS_LIGHT = ArmourSets.violet(84, 4);

	/**
	 * An angular shard: the outline (shape {@code shape}, mirrored in x if {@code flip}) scaled w across {@code u} and h
	 * along {@code v} at o, a slab {@code t} thick behind it (towards -n), its face rising to a ridge {@code crown} above
	 * the middle. Faces alternate col / col2 so the facets catch the light; sides and back in edge.
	 */
	static void shard(Mesh me, double[] o, double[] u, double[] v, double[] n, int shape, boolean flip, double w, double h, double t, double crown, int label,
			int col, int col2, int edge) {
		n = norm(n);
		u = norm(sub(u, scale(n, dot(u, n))));
		v = norm(sub(sub(v, scale(n, dot(v, n))), scale(u, dot(v, u))));
		double[][] out = SHAPES[shape % SHAPES.length];
		int k = out.length;
		int[] top = new int[k], bot = new int[k];
		for (int i = 0; i < k; i++) {
			double x = out[i][0] * (flip ? -1 : 1) * w, y = out[i][1] * h;
			double[] p = add(o, add(scale(u, x), scale(v, y)));
			top[i] = me.v(p, label);
			bot[i] = me.v(add(p, scale(n, -t)), label);
		}
		int apex = me.v(add(add(o, scale(n, crown)), scale(v, -0.15 * h)), label), back = me.v(add(o, scale(n, -t)), label);
		double[] behind = add(o, scale(n, -10)), front = add(o, scale(n, 10));
		for (int i = 0; i < k; i++) {
			int j = (i + 1) % k;
			me.tri(top[i], top[j], apex, i % 2 == 0 ? col : col2, behind);
			me.tri(bot[i], bot[j], back, edge, front);
			me.quad(top[i], top[j], bot[j], bot[i], edge, add(o, scale(n, -t / 2)));
		}
		// the rift's light behind it, showing round its edges: the outline grown, a little further back
		int[] gl = new int[k];
		double[] gc = add(o, scale(n, -t - 0.6));
		for (int i = 0; i < k; i++) {
			double x = out[i][0] * (flip ? -1 : 1) * (w + 1.6), y = out[i][1] * (h + 1.6);
			gl[i] = me.v(add(gc, add(scale(u, x), scale(v, y))), label);
		}
		int gm = me.v(gc, label);
		for (int i = 0; i < k; i++) {
			me.tri(gl[i], gl[(i + 1) % k], gm, GLOW, behind);
		}
	}

	/** A riftglass splinter: a four-sided double point from a to b, widest {@code r} a third of the way. */
	static void splinter(Mesh me, double[] a, double[] b, double r, int label, int col, int col2) {
		double[] d = norm(sub(b, a));
		double[] e1 = norm(ModelData.cross(d, Math.abs(d[1]) > 0.9 ? new double[] { 1, 0, 0 } : new double[] { 0, 1, 0 })), e2 = ModelData.cross(d, e1);
		double[] m = lerp(a, b, 0.35);
		int[] ring = new int[4];
		for (int i = 0; i < 4; i++) {
			double an = Math.PI / 2 * i;
			ring[i] = me.v(add(m, add(scale(e1, Math.cos(an) * r), scale(e2, Math.sin(an) * r * 0.7))), label);
		}
		int ta = me.v(a, label), tb = me.v(b, label);
		for (int i = 0; i < 4; i++) {
			int j = (i + 1) % 4;
			me.tri(ring[i], ring[j], tb, i % 2 == 0 ? col : col2, m);
			me.tri(ring[i], ring[j], ta, col2, m);
		}
	}

	/** A shard on a limb: over the point at angle phi (0 front, pi/2 the +x side) round the axis c (direction a), radius rad, floating {@code lift} off it. */
	static void limbShard(Mesh me, double[] c, double[] a, double rad, double phi, double lift, int shape, boolean flip, double w, double h, int label, Style st) {
		double[] p = BaseCoat.around(c, a, rad, rad, phi);
		double[] n = norm(sub(p, c));
		double[] o = add(p, scale(n, lift));
		shard(me, o, ModelData.cross(a, n), a, n, shape, flip, w, h, 1.2, 1.6 + w * 0.15, label, st.main, st.light, st.trimDark);
	}

	/** A black strap round a limb at c (axis a), radius rad, width wd, wound on the slant by {@code slant}. */
	static void strap(Mesh me, double[] c, double[] a, double rad, double wd, double slant, int n, int label, int col) {
		int[] r0 = new int[n], r1 = new int[n];
		for (int i = 0; i < n; i++) {
			double phi = Math.PI * 2 * i / n;
			double off = Math.sin(phi) * slant;
			r0[i] = me.v(BaseCoat.around(add(c, scale(a, off - wd / 2)), a, rad, rad, phi), label);
			r1[i] = me.v(BaseCoat.around(add(c, scale(a, off + wd / 2)), a, rad + 0.2, rad + 0.2, phi), label);
		}
		me.band(r0, r1, true, col, c);
	}

	/* ====================================================================== */
	/*                                THE HOOD                                */
	/* ====================================================================== */

	/**
	 * The hood: rings fitted round the head kit, closed over the face (faceless), drawn back over the crown to a point
	 * behind; an angular skirt over the neck; a seam of rift-light down the face; riftglass splinters floating round
	 * the crown.
	 */
	static ModelData hood(Style st, Body b) {
		Mesh me = new Mesh();
		double H = b.headTop, C = b.headChin, hc = (b.headFront + b.headBack) / 2, hs = (C - H) / 24.0;
		double[] ys = { b.neckY + 5, C + 2, H + (C - H) * 0.62, H + (C - H) * 0.3, H + 1, H - 4 * hs };
		double[] mg = { 6.5, 2.4, 2.4, 2.4, 2.0, 0 };
		int n = 10, NR = ys.length;
		int[][] g = new int[NR][n];
		double[][] cen = new double[NR][];
		for (int r = 0; r < NR; r++) {
			double[] s = BaseCoat.slice(b.head, Math.max(H + 1.5, Math.min(C, ys[r])), 0);
			double rx = Math.max(-s[0], s[1]) + mg[r], zf = s[2] - mg[r], zb = s[3] + mg[r];
			double cz = (zf + zb) / 2, rz = (zb - zf) / 2;
			if (r == 0) {
				rx = b.headHW + 9;
				rz = (b.headBack - b.headFront) / 2 + 6;
				cz = hc + 3;
			}
			if (r == NR - 1) { // the crown, drawn back
				rx *= 0.55;
				rz *= 0.5;
				cz += 4;
			}
			cen[r] = new double[] { 0, ys[r], cz };
			for (int i = 0; i < n; i++) {
				double[] p = ell(0, ys[r], cz, rx, rz, rz, 360.0 * i / n);
				if (r == 0 && i % 5 == 0) { // the skirt cut to a point before and behind
					p[1] += 4;
				}
				g[r][i] = me.v(p, SKULL);
			}
		}
		for (int r = 0; r + 1 < NR; r++) {
			double[] in = lerp(cen[r], cen[r + 1], 0.5);
			for (int i = 0; i < n; i++) {
				int j = (i + 1) % n;
				boolean face = (i == 0 || j == 0 || i == 9 || j == 1) && r >= 1 && r <= 2;
				int col = r == 0 ? st.dark : face ? st.dark : (i + r) % 3 == 0 ? st.light : st.main;
				me.quad(g[r][i], g[r][j], g[r + 1][j], g[r + 1][i], col, in);
			}
		}
		me.fan(g[NR - 1], new double[] { 0, H - 3 * hs, cen[NR - 1][2] + 9 }, SKULL, st.main, cen[NR - 1]);
		// under the skirt
		int[] under = new int[n];
		for (int i = 0; i < n; i++) {
			double[] p = me.p(g[0][i]);
			under[i] = me.v(lerp(new double[] { 0, p[1] - 3, cen[0][2] }, p, 0.6), SKULL);
		}
		me.bandIn(g[0], under, true, st.trimDark, new double[] { 0, ys[0] + 30, cen[0][2] });
		// the seam down the blank face, from the brow to the chin
		me.prio = 2;
		int[] sl = new int[4], sr = new int[4];
		for (int r = 1; r <= 4; r++) {
			double[] f = me.p(g[r][0]);
			double w = r == 4 ? 0.4 : 0.9;
			sl[r - 1] = me.v(new double[] { f[0] - w, f[1], f[2] - 0.6 }, SKULL);
			sr[r - 1] = me.v(new double[] { f[0] + w, f[1], f[2] - 0.6 }, SKULL);
		}
		for (int r = 0; r < 3; r++) {
			me.quad(sl[r], sr[r], sr[r + 1], sl[r + 1], r == 1 ? st.core : st.accent, new double[] { 0, ys[r + 2], hc });
		}
		me.prio = 1;
		// riftglass splinters floating round the crown, one standing tall over the brow
		double[] axis = { 0, H + 4, hc + 1 };
		double rr = b.headHW + 6.5;
		for (int k = 0; k < 6; k++) {
			double a = Math.toRadians(30 + 60 * k + (k % 2) * 12);
			double y = axis[1] - 3 - 4 * ((k * 2) % 3);
			double[] base = { Math.sin(a) * rr, y, axis[2] - Math.cos(a) * rr * 0.95 };
			double[] dir = norm(new double[] { Math.sin(a) * 0.55, -1, -Math.cos(a) * 0.55 });
			double len = 7 + 3 * (k % 3);
			splinter(me, add(base, scale(dir, -len * 0.3)), add(base, scale(dir, len * 0.7)), 1.6, SKULL, k % 2 == 0 ? GLASS_LIGHT : GLOW, GLASS);
		}
		double[] crest = { 0, H - 9 * hs, b.headFront + 2 };
		splinter(me, add(crest, new double[] { 0, 5, 1.5 }), add(crest, new double[] { 0, -10 * hs, -2 }), 2.2, SKULL, GLOW, GLASS);
		me.prio = 0;
		return me.done();
	}

	/* ====================================================================== */
	/*                                 THE TOP                                */
	/* ====================================================================== */

	/** The under-suit, its shards and straps, both sleeves, and the torn cape. */
	static ModelData top(Style st, Body b) {
		Mesh me = new Mesh();
		ModelData kit = ArmourForge.mergeAll(b.torso, b.legs);
		double neck = b.neckY, waist = b.waistY, h = waist - neck, cz = (b.chestZ + b.backZ) / 2;
		double[] ys = { neck + 1, neck + 7, neck + h * 0.38, neck + h * 0.7, waist + 1, waist + 7 };
		double[] out = { 1.0, 1.2, 1.2, 1.3, 1.6, 2.2 };
		int n = 8, NR = ys.length;
		int[][] rg = new int[NR][];
		for (int r = 0; r < NR; r++) {
			rg[r] = BaseMantle.fitted(me, kit, ys[r], cz, n, out[r], b.neckR + 2, 0, r <= 2 ? CHEST : BELLY);
		}
		for (int r = 0; r + 1 < NR; r++) {
			double[] in = { 0, (ys[r] + ys[r + 1]) / 2, cz };
			for (int i = 0; i < n; i++) {
				int j = (i + 1) % n;
				// the glow under the shards: the chest, the right flank and the right shoulder blade
				me.quad(rg[r][i], rg[r][j], rg[r + 1][j], rg[r + 1][i], r == NR - 2 ? st.trimDark : st.dark, in);
			}
		}
		// shards: th degrees round the torso (0 front, 90 left, 270 right), height fraction, shape, w, h
		double[][] sh = { { 334, 0.3, 0, 8.5, 10 }, { 24, 0.3, 1, 7, 8.5 }, { 356, 0.62, 2, 6, 8.5 }, { 316, 0.72, 3, 7, 7.5 }, { 286, 0.46, 1, 6, 11 },
				{ 206, 0.3, 0, 8.5, 9.5 }, { 182, 0.68, 2, 7, 8 }, { 240, 0.52, 3, 6, 8.5 } };
		for (double[] q : sh) {
			double y = neck + h * q[1];
			double[] hit = BaseMantle.hit(kit, y, cz, q[0]);
			double a = Math.toRadians(q[0]);
			double[] nrm = norm(new double[] { Math.sin(a), 0, -Math.cos(a) });
			double[] p = hit == null ? ell(0, y, cz, 16, 12, 12, q[0]) : hit;
			double[] o = add(p, scale(nrm, 3.6));
			int lab = q[1] < 0.5 ? CHEST : BELLY;
			shard(me, o, ModelData.cross(new double[] { 0, 1, 0 }, nrm), new double[] { 0.15, 1, 0 }, nrm, (int) q[2], q[0] > 180, q[3], q[4], 1.3, 2.6, lab,
					st.main, st.light, st.trimDark);
		}
		// the bandolier: a black strap from the left shoulder across to the right hip, front and back
		bandolier(me, st, b, kit, cz);
		for (int s = 0; s < 2; s++) {
			arm(me, st, b, s);
		}
		cape(me, st, b);
		return me.done();
	}

	/** A black strap over the left shoulder, across the chest to the right hip and back up behind. */
	static void bandolier(Mesh me, Style st, Body b, ModelData kit, double cz) {
		double sl = b.shoulder[1][0] - 7;
		for (int face = -1; face <= 1; face += 2) {
			int seg = 6;
			int[] l = new int[seg + 1], r = new int[seg + 1];
			for (int k = 0; k <= seg; k++) {
				double t = (double) k / seg;
				double x = lerp(sl, -b.torsoHW + 3, t), y = lerp(b.neckY + 2, b.waistY - 1, t);
				double[] hit = ArmourForge.surface(kit, new double[] { x, y, face * -150 }, new double[] { 0, 0, -face });
				double[] p = hit == null ? new double[] { x, y, cz + face * 12 } : hit;
				double[] dz = { 0, 0, face * 1.9 };
				double[] across = norm(new double[] { (b.waistY - b.neckY) * 0.4, (b.torsoHW + sl) * 0.4, 0 });
				int lab = y < b.neckY + (b.waistY - b.neckY) * 0.5 ? CHEST : BELLY;
				l[k] = me.v(add(add(p, dz), scale(across, 2.2)), lab);
				r[k] = me.v(add(add(p, dz), scale(across, -2.2)), lab);
			}
			me.prio = 1;
			for (int k = 0; k < seg; k++) {
				me.quad(l[k], r[k], r[k + 1], l[k + 1], st.trim, new double[] { 0, b.neckY + 20, cz });
			}
			me.prio = 0;
		}
	}

	/**
	 * A sleeve: the under-suit from the shoulder to the wrist; on the right (s 0) glowing under a great pauldron shard and
	 * shards down the arm, on the left dark and bound in black straps.
	 */
	static void arm(Mesh me, Style st, Body b, int s) {
		double sg = sx(s), sc = b.female ? 0.88 : 1;
		boolean shards = s == 0;
		double[] sh = add(b.shoulder[s], new double[] { -sg * 1.0, 0.5, 0 }), el = b.elbow[s], wr = b.wrist[s];
		double[] au = norm(sub(el, sh)), af = norm(sub(wr, el));
		double ru = 5.4 * sc, rf = 4.6 * sc;
		Object[][] rings = { { sh, au, ru + 1.2, UPPER[s] }, { lerp(sh, el, 0.5), au, ru, UPPER[s] }, { el, norm(add(au, af)), ru - 0.2, ELBOW[s] },
				{ lerp(el, wr, 0.5), af, rf + 0.4, FORE[s] }, { add(wr, scale(af, 1.5)), af, rf + 0.6, FORE[s] } };
		int n = 6;
		int[][] idx = new int[rings.length][n];
		for (int k = 0; k < rings.length; k++) {
			for (int j = 0; j < n; j++) {
				double r = (double) rings[k][2];
				idx[k][j] = me.v(BaseCoat.around((double[]) rings[k][0], (double[]) rings[k][1], r, r, Math.PI * 2 * j / n + 0.3), (int) rings[k][3]);
			}
		}
		for (int k = 0; k + 1 < rings.length; k++) {
			me.band(idx[k], idx[k + 1], true, shards ? st.dark : st.main, lerp((double[]) rings[k][0], (double[]) rings[k + 1][0], 0.5));
		}
		double out = sg * Math.PI / 2; // phi of the outside of the arm
		if (shards) {
			// the pauldron: a great shard over the shoulder, two smaller under it
			double[] top = add(b.shoulder[s], new double[] { sg * 7.5, -13 * sc, 0.5 });
			double[] nrm = norm(new double[] { sg * 0.85, -0.45, 0 });
			shard(me, top, new double[] { 0, 0, 1 }, norm(new double[] { sg * 0.35, 1, 0 }), nrm, 2, true, 8.5 * sc, 16 * sc, 1.8, 3.6, UPPER[s], st.main, st.light,
					st.trimDark);
			// splinters drifting off the shoulder and the arm, coming apart
			double[][] drift = { { 15, -27, -3, 7 }, { 19, -14, 5, 5.5 }, { 13, 4, -7, 4.5 } };
			for (double[] d : drift) {
				double[] c0 = add(b.shoulder[s], new double[] { sg * d[0] * sc, d[1] * sc, d[2] });
				double[] dir = norm(new double[] { sg * 0.5, -1, d[2] * 0.08 });
				splinter(me, add(c0, scale(dir, -d[3] * 0.4)), add(c0, scale(dir, d[3] * 0.6)), 1.4, UPPER[s], GLASS_LIGHT, GLASS);
			}
			double[] t2 = add(b.shoulder[s], new double[] { sg * 9.5, -2 * sc, 0 });
			shard(me, t2, new double[] { 0, 0, 1 }, norm(new double[] { -sg * 0.3, -1, 0 }), norm(new double[] { sg, -0.25, 0 }), 0, false, 8 * sc, 9 * sc, 1.5, 3.0,
					UPPER[s], st.main, st.light, st.trimDark);
			limbShard(me, lerp(sh, el, 0.62), au, ru, out - 0.7, 2.8, 1, false, 5.5 * sc, 6.5 * sc, UPPER[s], st);
			limbShard(me, el, norm(add(au, af)), ru, out + 0.3, 2.6, 2, true, 5.5 * sc, 6 * sc, ELBOW[s], st);
			limbShard(me, lerp(el, wr, 0.42), af, rf, out, 2.6, 0, true, 6 * sc, 7.5 * sc, FORE[s], st);
			limbShard(me, lerp(el, wr, 0.6), af, rf, out - 1.4, 2.4, 2, false, 4.8 * sc, 6.5 * sc, FORE[s], st);
		} else {
			// bound in straps from the shoulder to the wrist
			strap(me, lerp(sh, el, 0.2), au, ru + 0.9, 2.4, 1.6, n, UPPER[s], st.trim);
			strap(me, lerp(sh, el, 0.62), au, ru + 0.7, 2.4, -1.6, n, UPPER[s], st.trim);
			strap(me, el, norm(add(au, af)), ru + 0.5, 2.2, 1.4, n, ELBOW[s], st.trimDark);
			strap(me, lerp(el, wr, 0.4), af, rf + 1.0, 2.4, -1.4, n, FORE[s], st.trim);
			strap(me, lerp(el, wr, 0.82), af, rf + 1.1, 2.6, 1.2, n, FORE[s], st.trim);
		}
	}

	/**
	 * The torn cape: from the left shoulder down the back, widening to the left, its hem cut away on the slant (long on the
	 * left, short towards the middle) and torn into points; lined. On the stock capes' chain.
	 */
	static void cape(Mesh me, Style st, Body b) {
		double hips = b.hipsBackZ;
		double top = b.shoulderTop + 2;
		double sl = b.shoulder[1][0];
		// y, label, x of the inner (middle) edge, x of the outer edge, z
		double[][] rows = { { top - 1, CAPE_TOP, sl - 15, sl + 9, b.backZ + 1.5 }, { (top + b.capeY[1]) / 2, CAPE_TOP, sl - 19, sl + 12, b.backZ + 4 },
				{ b.capeY[1], CAPE_WAIST, sl - 21, sl + 14, hips + 5 }, { (b.capeY[1] + b.capeY[2]) / 2, CAPE_HIP, sl - 21, sl + 16, hips + 7.5 },
				{ b.capeY[2], CAPE_THIGH, sl - 20, sl + 18, hips + 9.5 } };
		int cols = 4, R = rows.length;
		int[][] o = new int[R][cols + 1], in = new int[R][cols + 1];
		for (int r = 0; r < R; r++) {
			for (int i = 0; i <= cols; i++) {
				double u = (double) i / cols;
				double x = lerp(rows[r][2], rows[r][3], u);
				double z = rows[r][4] - 2.5 * Math.pow(2 * u - 1, 2);
				if (r <= 1 && i >= cols - 1) { // the outer side wraps round over the top of the shoulder
					z -= (r == 0 ? 9 : 5) * (i == cols ? 1.4 : 0.7);
				}
				int lab = (int) rows[r][1];
				o[r][i] = me.v(new double[] { x, rows[r][0], z }, lab);
				in[r][i] = me.v(new double[] { x, rows[r][0], z - 0.8 }, lab);
			}
		}
		double[] body = { sl, 0, -40 };
		for (int r = 0; r + 1 < R; r++) {
			for (int i = 0; i < cols; i++) {
				body[1] = (rows[r][0] + rows[r + 1][0]) / 2;
				me.quad(o[r][i], o[r][i + 1], o[r + 1][i + 1], o[r + 1][i], r == 0 ? st.leather : st.leatherDark, body);
				if (r > 0) {
					me.quadIn(in[r][i], in[r][i + 1], in[r + 1][i + 1], in[r + 1][i], st.trimDark, body);
				}
			}
		}
		// the hem: cut on the slant and torn, a point under each column, longest at the outside
		int last = R - 1;
		for (int i = 0; i < cols; i++) {
			double u = (i + 0.5) / cols;
			double[] a = me.p(o[last][i]), c = me.p(o[last][i + 1]);
			double[] m = lerp(a, c, 0.5);
			double drop = lerp(6, b.capeY[3] - b.capeY[2] + 6, u) * (i % 2 == 0 ? 1 : 0.7);
			int lab = drop > 18 ? CAPE_KNEE : CAPE_THIGH;
			int t = me.v(new double[] { m[0] + (u - 0.5) * 3, m[1] + drop, m[2] + drop * 0.15 }, lab);
			BaseWraith.flap(me, o[last][i], o[last][i + 1], t, st.leatherDark, st.trimDark, new double[] { m[0], m[1] + drop / 2, -40 });
		}
		// a riftglass clasp where it hangs from the shoulder
		double[] cl = { sl - 3, top + 2, b.chestZ + 2 };
		splinter(me, add(cl, new double[] { 0, 4, 0 }), add(cl, new double[] { 1, -5, -1 }), 2.0, CHEST, GLOW, GLASS);
	}

	/* ====================================================================== */
	/*                                THE CHAPS                               */
	/* ====================================================================== */

	/** One leg's ring at height y: {cx, cz, rx, rz} from the legs kit's section on that side, plus a margin. */
	static double[] legRing(Body b, double y, int side, double margin) {
		double[] s = BaseCoat.slice(b.legs, y, side);
		return new double[] { (s[0] + s[1]) / 2, (s[2] + s[3]) / 2, (s[1] - s[0]) / 2 + margin, (s[3] - s[2]) / 2 + margin };
	}

	/** The under-suit's legs, the shards over them and the straps binding them, a belt with a shard at the right hip. */
	static ModelData chaps(Style st, Body b) {
		Mesh me = new Mesh();
		int n = 6;
		double kneeY = (b.knee[0][1] + b.knee[1][1]) / 2, ankY = (b.ankle[0][1] + b.ankle[1][1]) / 2;
		for (int s = 0; s < 2; s++) {
			int side = s == 0 ? -1 : 1;
			boolean shards = s == 0;
			double[][] rows = { { b.legsTop + 2, 1.2, BELLY }, { b.hipJointY + 1, 1.4, THIGH[s] }, { lerp(b.hipJointY, kneeY, 0.5), 1.4, THIGH[s] },
					{ kneeY - 2, 1.3, THIGH[s] }, { kneeY + 6, 1.3, SHIN[s] }, { lerp(kneeY, ankY, 0.55), 1.2, SHIN[s] }, { ankY - 1, 1.1, ANKLE[s] } };
			int[][] idx = new int[rows.length][n];
			double[][] lr = new double[rows.length][];
			for (int k = 0; k < rows.length; k++) {
				double[] q = legRing(b, rows[k][0], side, rows[k][1]);
				if (k == 0) {
					q[0] = side * Math.max(Math.abs(q[0]), 8.5);
					q[2] = Math.max(q[2], 9.5);
				}
				lr[k] = q;
				for (int j = 0; j < n; j++) {
					idx[k][j] = me.v(ell(q[0], rows[k][0], q[1], q[2], q[3], q[3], 360.0 * j / n), (int) rows[k][2]);
				}
			}
			for (int k = 0; k + 1 < rows.length; k++) {
				double[] in = { (lr[k][0] + lr[k + 1][0]) / 2, (rows[k][0] + rows[k + 1][0]) / 2, (lr[k][1] + lr[k + 1][1]) / 2 };
				for (int j = 0; j < n; j++) {
					int jj = (j + 1) % n;
					me.quad(idx[k][j], idx[k][jj], idx[k + 1][jj], idx[k + 1][j], shards || k == 0 ? st.dark : st.main, in);
				}
			}
			// the leg's axis at a height, for shards and straps
			double[] th0 = { lr[1][0], rows[1][0], lr[1][1] }, th1 = { lr[3][0], rows[3][0], lr[3][1] }, sh0 = { lr[4][0], rows[4][0], lr[4][1] },
					sh1 = { lr[6][0], rows[6][0], lr[6][1] };
			double[] at = norm(sub(th1, th0)), as = norm(sub(sh1, sh0));
			double rt = (lr[2][2] + lr[2][3]) / 2, rs = (lr[5][2] + lr[5][3]) / 2;
			double outside = side * Math.PI / 2;
			if (shards) {
				limbShard(me, lerp(th0, th1, 0.32), at, rt, 0.15, 2.8, 0, false, 7.5, 11, THIGH[s], st);
				limbShard(me, lerp(th0, th1, 0.5), at, rt, outside + 0.25, 2.6, 1, true, 6, 10, THIGH[s], st);
				limbShard(me, lerp(sh0, sh1, 0.3), as, rs, -0.1, 2.6, 3, true, 6, 9, SHIN[s], st);
				limbShard(me, lerp(sh0, sh1, 0.72), as, rs, 0.3, 2.2, 2, false, 4.8, 7, SHIN[s], st);
			} else {
				strap(me, lerp(th0, th1, 0.15), at, rt + 0.9, 2.4, 1.6, n, THIGH[s], st.trim);
				strap(me, lerp(th0, th1, 0.55), at, rt + 0.8, 2.4, -1.6, n, THIGH[s], st.trim);
				limbShard(me, lerp(sh0, sh1, 0.45), as, rs, 0, 2.4, 1, false, 5.2, 8, SHIN[s], st);
				strap(me, lerp(sh0, sh1, 0.85), as, rs + 0.9, 2.2, 1.2, n, SHIN[s], st.trim);
			}
			// the knee shard
			double[] kn = { lerp(lr[3][0], lr[4][0], 0.5), kneeY + 1, lerp(lr[3][1], lr[4][1], 0.5) - lerp(lr[3][3], lr[4][3], 0.5) - 3.0 };
			shard(me, kn, new double[] { 1, 0, 0 }, new double[] { 0, 1, 0.2 }, new double[] { side * 0.15, -0.2, -1 }, shards ? 2 : 0, !shards, 5.4, 6.2, 1.3, 2.4,
					SHIN[s], st.main, st.light, st.trimDark);
		}
		// the belt, a shard over the right hip
		int[] lb = b.legs.bounds();
		double cz = (lb[2] + lb[5]) / 2.0;
		int[] w0 = BaseMantle.fitted(me, b.legs, b.legsTop, cz, 10, 2.2, 8, 0, BELLY), w1 = BaseMantle.fitted(me, b.legs, b.legsTop + 4.5, cz, 10, 2.4, 8, 0, BELLY);
		me.prio = 1;
		me.band(w0, w1, true, st.trim, new double[] { 0, b.legsTop + 2, cz });
		me.prio = 0;
		double[] hp = BaseMantle.hit(b.legs, b.legsTop + 10, cz, 285);
		if (hp != null) {
			double[] nrm = norm(new double[] { -1, 0, -0.25 });
			shard(me, add(hp, scale(nrm, 3.2)), new double[] { 0, 0, -1 }, new double[] { -0.2, 1, 0 }, nrm, 3, false, 5.5, 7, 1.3, 1.8, THIGH[0], st.main, st.light,
					st.dark);
		}
		return me.done();
	}

	/* ====================================================================== */
	/*                            BOOTS, GAUNTLETS                            */
	/* ====================================================================== */

	/** The feet kit in the dark, shards over the toes, the instep and the front of the ankle. */
	static ModelData boots(Style st, Body b) {
		ModelData m = BaseWraith.kitCopy(b.feet, st.dark);
		Mesh me = new Mesh();
		for (int s = 0; s < 2; s++) {
			double sg = sx(s);
			double[] an = b.ankle[s];
			double fx = b.heel[s][0], sole = b.soleY;
			double[] toe = { fx, sole - 4.5, b.toeZ + 4.5 }, inst = { fx, sole - 8.5, lerp(b.heelZ, b.toeZ, 0.45) };
			shard(me, toe, new double[] { 1, 0, 0 }, new double[] { 0, 0.45, -1 }, new double[] { 0, -1, -0.45 }, 2, s == 1, 4.6, 5.0, 1.0, 1.4, TOES[s], st.main,
					st.light, st.dark);
			shard(me, inst, new double[] { 1, 0, 0 }, new double[] { 0, 0.7, -1 }, new double[] { 0, -1, -0.7 }, 1, s == 1, 4.4, 4.5, 1.0, 1.4, ANKLE[s], st.main,
					st.light, st.dark);
			double[] fr = { an[0] + sg * 1.5, an[1] - 3, an[2] - 7 };
			shard(me, fr, new double[] { 1, 0, 0 }, new double[] { 0, 1, 0 }, norm(new double[] { sg * 0.4, 0, -1 }), 1, s == 1, 5.0, 7.0, 1.1, 1.8, ANKLE[s], st.main,
					st.light, st.trimDark);
		}
		ArmourForge.merge(m, me.done(), null);
		return m;
	}

	/** The right hand a shard gauntlet, the left a strap wrap; the hands kit in the dark under both. */
	static ModelData gauntlets(Style st, Body b) {
		ModelData m = BaseWraith.kitCopy(b.hands, st.dark);
		Mesh me = new Mesh();
		for (int s = 0; s < 2; s++) {
			double sg = sx(s);
			double[] w = b.wrist[s], hd = b.hand[s];
			double[] ax = norm(sub(hd, w));
			if (s == 0) {
				limbShard(me, add(w, new double[] { 0, -3, 0 }), ax, 4.5, sg * Math.PI / 2, 2.2, 0, true, 3.8, 6, FORE[s], st);
				limbShard(me, add(w, new double[] { 0, -2, 0 }), ax, 4.5, 0.2, 2.0, 2, false, 3.2, 4.8, FORE[s], st);
				limbShard(me, add(w, new double[] { 0, -7, 0 }), ax, 5.0, sg * Math.PI / 2 - 1.2, 2.2, 3, false, 3.4, 5, FORE[s], st);
				strap(me, w, ax, 5.0, 2.0, 0, 6, FORE[s], GLOW);
			} else {
				strap(me, add(w, new double[] { 0, -2, 0 }), ax, 5.2, 2.4, 1.0, 6, FORE[s], st.trim);
				strap(me, add(w, new double[] { 0, 3, 0 }), ax, 5.0, 2.2, -1.0, 6, FORE[s], st.trim);
				strap(me, hd, ax, b.handR[s][0] + 0.9, 2.6, 1.2, 6, HAND[s], st.trim);
			}
		}
		ArmourForge.merge(m, me.done(), null);
		return m;
	}
}
