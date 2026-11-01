package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ArmourForge.add;
import static com.rs2.tools.mapgen.ArmourForge.norm;
import static com.rs2.tools.mapgen.ArmourForge.scale;
import static com.rs2.tools.mapgen.ArmourForge.sub;
import static com.rs2.tools.mapgen.BasePilgrim.ANKLE;
import static com.rs2.tools.mapgen.BasePilgrim.BELLY;
import static com.rs2.tools.mapgen.BasePilgrim.CHEST;
import static com.rs2.tools.mapgen.BasePilgrim.FORE;
import static com.rs2.tools.mapgen.BasePilgrim.HIPS;
import static com.rs2.tools.mapgen.BasePilgrim.SHIN;
import static com.rs2.tools.mapgen.BasePilgrim.SKULL;
import static com.rs2.tools.mapgen.BasePilgrim.THIGH;
import static com.rs2.tools.mapgen.BasePilgrim.UPPER;
import static com.rs2.tools.mapgen.BasePilgrim.anchors;
import static com.rs2.tools.mapgen.BasePilgrim.bead;
import static com.rs2.tools.mapgen.BasePilgrim.capeLabel;
import static com.rs2.tools.mapgen.BasePilgrim.copyKit;
import static com.rs2.tools.mapgen.BasePilgrim.cord;
import static com.rs2.tools.mapgen.BasePilgrim.ell;
import static com.rs2.tools.mapgen.BasePilgrim.fitted;
import static com.rs2.tools.mapgen.BasePilgrim.inner;
import static com.rs2.tools.mapgen.BasePilgrim.legAt;
import static com.rs2.tools.mapgen.BasePilgrim.lerp;
import static com.rs2.tools.mapgen.BasePilgrim.nearest;
import static com.rs2.tools.mapgen.BasePilgrim.onFront;
import static com.rs2.tools.mapgen.BasePilgrim.ring;
import static com.rs2.tools.mapgen.BasePilgrim.sx;
import static com.rs2.tools.mapgen.ModelData.hsl;

import java.io.IOException;
import java.util.function.ToIntFunction;

import com.rs2.tools.mapgen.ArmourSets.Style;
import com.rs2.tools.mapgen.BaseMantle.Body;
import com.rs2.tools.mapgen.BasePilgrim.Mesh;

/**
 * The Thornwarden base (A26): Silkwood's hedge-keepers in ghillie suits, a walking shrub with a face. Built from
 * scratch on the leather's rig (Kind.WRAP); see NewBases. Nothing here is the old WRAP base: no headband, vest or kilt.
 *
 * <ul>
 * <li>Hood: a cowl shingled all over in leaves that stick out at angles, a crest of them standing up from the crown, a
 * fringe of long leaves flaring forward from the brow over the face, which is a shadow with two pale eyes in it; a
 * bramble circlet with berries. Named "hood", so the head is hidden (the beard shows under the shadow).</li>
 * <li>Top: a hunched shroud of leaf shingles: a mantle all round the shoulders, humped over the shoulder blades, and a
 * longer drape down the back to behind the knees, rising at the sides, both cut ragged by their last row of leaves.
 * Under it a moss-green jerkin with bramble wound across it and berries in the bramble, and bound sleeves. Named
 * "top", so the arms kit is hidden and the bound sleeves are drawn.</li>
 * <li>Chaps: rows of leaf shingles over the front and outside of each thigh, a bramble round each thigh, over leggings
 * bound in diagonal strips.</li>
 * <li>Boots: the foot in dark leather, its shaft bound in a wicker lattice to mid-shin.</li>
 * <li>Bracers: ridged bark from the wrist to the elbow, three thorn spurs standing off the outside of each, over dark
 * gloves.</li>
 * </ul>
 *
 * <p>A leaf is a folded diamond (two triangles meeting at a raised midrib, so the fold catches the light), stood on
 * the surface under it and kicked out from it at an angle, the upper rows further than the lower, so the outline is
 * broken all round. The last row of each shingled sheet is two-sided (seen from below as it hangs).
 *
 * <p>Labels follow {@link BasePilgrim}: every piece that hides a kit supplies the kit's origin labels with {@link
 * BasePilgrim#anchors}; visible geometry takes safe labels. The hood is all on the skull 1; the mantle and its leaves
 * take their nearest torso or arm label (made safe), so they ride the shoulders and upper arms; the back drape and
 * its leaves take the stock cape's chain by height, so the walk swings it back off the legs; the chaps' leaves and
 * brambles take their thigh (44 / 43), the leggings thigh, shin (33 / 36) and ankle (32 / 38) by height, the
 * waistband the still pelvis 41; the boot shafts the ankle labels; the bracers and their spurs the forearm (19 /
 * 22), the gloves the kit's own hands.
 */
final class BaseGhillie {

	private BaseGhillie() {
	}

	static final int LEAF = DraftArmourA.LEAF, LEAF_DARK = DraftArmourA.LEAF_DARK, LEAF_DRY = hsl(10, 3, 46), LEAF_OLIVE = hsl(13, 4, 50),
			SHADOW = hsl(16, 2, 6), EYE = hsl(12, 3, 100), BARK = hsl(7, 2, 30), BARK_LIGHT = hsl(7, 2, 44), WICKER = hsl(8, 3, 58), WICKER_DARK = hsl(7, 3, 38),
			STEM = hsl(4, 3, 30);

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		Body b = BaseMantle.body(af, female);
		switch (slot) {
		case ArmourSets.HELM:
			return new ModelData[] { hood(st, b), null };
		case ArmourSets.BODY:
			return new ModelData[] { shroud(st, b), null };
		case ArmourSets.LEGS:
			return new ModelData[] { chaps(st, b), null };
		case ArmourSets.BOOTS:
			return new ModelData[] { boots(st, b), null };
		case ArmourSets.GLOVES:
			return new ModelData[] { bracers(st, b), null };
		default:
			return new ModelData[] { donor, donorSecond };
		}
	}

	/** The inventory model for the slot, or null to let ArmourSets build it the usual way from the worn model(s). */
	static ModelData inventory(ArmourForge af, Style st, int slot, ModelData[] worn) throws IOException {
		return null;
	}

	/* ====================================================================== */
	/*                               LEAVES                                   */
	/* ====================================================================== */

	/** A small deterministic hash in [0, 1). */
	static double hash(int a, int b) {
		int h = a * 73856093 ^ b * 19349663 ^ 0x5bd1e995;
		h ^= h >>> 13;
		h *= 0x27d4eb2d;
		h ^= h >>> 15;
		return (h & 0xffff) / 65536.0;
	}

	/** One of the leaf colours, picked by hash. */
	static int leafColour(Style st, double h) {
		return h < 0.34 ? LEAF : h < 0.62 ? LEAF_DARK : h < 0.84 ? LEAF_OLIVE : LEAF_DRY;
	}

	/**
	 * A leaf: a folded diamond from {@code base} along {@code dir}, its face towards {@code out}, the midrib raised (the
	 * two halves meet at an angle); {@code twoSided} adds the underside.
	 */
	static void leaf(Mesh me, double[] base, double[] dir, double[] out, double len, double wid, int col, int under, boolean twoSided, int label) {
		dir = norm(dir);
		double[] side = norm(ModelData.cross(out, dir));
		double[] f = norm(ModelData.cross(dir, side));
		if (ArmourForge.dot(f, out) < 0) {
			f = scale(f, -1);
		}
		double[] mid = add(base, scale(dir, len * 0.42));
		int b = me.v(base, label), t = me.v(add(base, scale(dir, len)), label);
		int l = me.v(add(add(mid, scale(side, wid / 2)), scale(f, -wid * 0.28)), label), r = me.v(add(add(mid, scale(side, -wid / 2)), scale(f, -wid * 0.28)), label);
		me.triDir(b, l, t, col, f);
		me.triDir(b, t, r, col, f);
		if (twoSided) {
			double[] g = scale(f, -1);
			me.triDir(b, l, t, under, g);
			me.triDir(b, t, r, under, g);
		}
	}

	/** The outward normal of the ellipse (rx, rz) round (0, cz) at the point p, horizontal. */
	static double[] outward(double[] p, double cz, double rx, double rz) {
		return norm(new double[] { p[0] / (rx * rx), 0, (p[2] - cz) / (rz * rz) });
	}

	/**
	 * A shingled sheet over the arc th0..th1 (degrees, 0 the front): rows of {y at the front, y at the back, cz, rx, rz
	 * front, rz back} (y blends between them round the arc, so a hem can drop behind), the surface in {@code col} with
	 * a {@code lining}, then leaves: on every row but the first, and between rows, staggered, each hanging from the
	 * surface and kicked out by {@code kick0} degrees at the top falling to {@code kick1} at the hem; the hem row two-sided.
	 */
	static void sheet(Mesh me, Style st, double[][] rows, double th0, double th1, int cols, ToIntFunction<double[]> label, int col, int lining,
			double kick0, double kick1, double leafLen, int seed) {
		int R = rows.length;
		int[][] g = new int[R][cols + 1];
		double[][][] p = new double[R][cols + 1][];
		for (int r = 0; r < R; r++) {
			for (int i = 0; i <= cols; i++) {
				double th = th0 + (th1 - th0) * i / cols, w = (1 - Math.cos(Math.toRadians(th))) / 2;
				double[] q = ell(0, lerp(rows[r][0], rows[r][1], w), rows[r][2], rows[r][3], rows[r][4], rows[r][5], th);
				p[r][i] = q;
				g[r][i] = me.v(q, label.applyAsInt(q));
			}
		}
		int prio = me.prio;
		for (int r = 0; r + 1 < R; r++) {
			me.band(g[r], g[r + 1], false, new int[] { col }, 0, rows[r][2], false);
			if (r > 0) {
				me.band(g[r], g[r + 1], false, new int[] { lining }, 0, rows[r][2], true);
			}
		}
		me.prio = prio + 1;
		// the leaves: row by row, top down, each row of anchors on a grid row and the staggered ones between rows
		int k = 0;
		for (int r = 1; r < R; r++) {
			for (int half = 0; half < 2; half++) {
				boolean between = half == 0;
				int n = between ? cols : cols + 1;
				double f = (r - (between ? 0.5 : 0)) / (R - 1.0);
				boolean hem = r == R - 1 && !between;
				for (int i = 0; i < n; i++) {
					double[] a;
					double cz = rows[r][2], rx = rows[r][3], rz;
					if (between) {
						a = scale(add(add(p[r - 1][i], p[r - 1][i + 1]), add(p[r][i], p[r][i + 1])), 0.25);
					} else {
						a = p[r][i];
					}
					rz = a[2] - cz < 0 ? rows[r][4] : rows[r][5];
					double[] o = outward(a, cz, rx, rz);
					double h = hash(seed + k, 17), h2 = hash(seed + k, 91);
					k++;
					double kick = Math.toRadians(lerp(kick0, kick1, f) + (h2 - 0.5) * 16);
					double[] dir = add(scale(new double[] { 0, 1, 0 }, Math.cos(kick)), scale(o, Math.sin(kick)));
					dir = add(dir, new double[] { (h - 0.5) * 0.35, 0, 0 });
					double len = leafLen * (0.85 + h2 * 0.35);
					double[] base = add(a, scale(o, 0.6));
					int lab = label.applyAsInt(base);
					leaf(me, base, dir, o, len, len * 0.62, leafColour(st, h), LEAF_DARK, hem, lab);
				}
			}
		}
		me.prio = prio;
	}

	/** A bramble along a path: a dark stem, pale thorns standing off it, and clusters of berries at the given nodes. */
	static void bramble(Mesh me, double[][] path, double[] out, int[] berries, ToIntFunction<double[]> label, int seed) {
		int n = path.length;
		int[] prev = null;
		for (int i = 0; i < n; i++) {
			double[] d = norm(sub(path[Math.min(i + 1, n - 1)], path[Math.max(i - 1, 0)]));
			int[] cur = ring(me, path[i], d, 0.8, 0.8, 3, 0.3, label.applyAsInt(path[i]));
			if (prev != null) {
				me.tube(prev, cur, new int[] { STEM }, path[i - 1], path[i]);
			}
			prev = cur;
			if (i > 0 && i < n - 1) { // a thorn, alternately to either side
				double[] side = norm(ModelData.cross(d, out));
				double[] dirT = norm(add(scale(side, i % 2 == 0 ? 1 : -1), scale(out, 0.7)));
				int lab = label.applyAsInt(path[i]);
				int a = me.v(add(path[i], scale(d, -1.1)), lab), c = me.v(add(path[i], scale(d, 1.1)), lab), t = me.v(add(add(path[i], scale(dirT, 3.4)), scale(d, 0.8)), lab);
				me.triDir(a, c, t, DraftArmourA.THORN, out);
				me.triDir(a, c, t, DraftArmourA.THORN_DARK, scale(out, -1));
			}
		}
		for (int bi : berries) {
			double[] c = path[Math.min(bi, n - 1)];
			int lab = label.applyAsInt(c);
			for (int k = 0; k < 3; k++) {
				double a = Math.PI * 2 * k / 3 + seed;
				double[] q = add(c, new double[] { Math.cos(a) * 1.6, Math.sin(a) * 1.6 + 1.0, 0 });
				bead(me, add(q, scale(out, 0.6)), add(out, new double[] { Math.cos(a) * 0.4, Math.sin(a) * 0.4, 0 }), 1.5,
						k == 1 ? DraftArmourA.BERRY_LIGHT : DraftArmourA.BERRY, hsl(0, 6, 28), lab);
			}
		}
	}

	/* ====================================================================== */
	/*                                 HOOD                                   */
	/* ====================================================================== */

	/**
	 * The leaf hood: a cowl round the head (closed over the crown and brow, open over the face below the brow, its
	 * lower edge on the shoulders), every row shingled with leaves pointing down and out, a crest standing up off the
	 * crown, long leaves flaring forward off the brow, a shadow for a face with two pale eyes, a bramble circlet.
	 */
	static ModelData hood(Style st, Body b) {
		Mesh me = new Mesh();
		double top = b.headTop, hw = b.headHW;
		double[] hs = BaseCoat.slice(b.head, top + 10, 0);
		double cz = (hs[2] + hs[3]) / 2, rzF = cz - hs[2] + 3.5, rzB = hs[3] - cz + 3.5, rx = Math.max(-hs[0], hs[1]) + 3.5;
		double brow = top + 11, chin = b.neckY + 3;
		int n = 12;
		// rows: y, rx, rz front, rz back, closed (over the face) or open
		double[][] rows = { { top + 2.5, rx * 0.62, rzF * 0.6, rzB * 0.62 }, { top + 6.5, rx * 0.93, rzF * 0.92, rzB * 0.95 }, { brow, rx + 0.4, rzF + 0.4, rzB + 0.6 },
				{ lerp(brow, chin, 0.5), rx + 0.2, rzF - 1, rzB + 0.8 }, { chin, b.neckR + 9, b.neckR + 5, b.neckR + 8 } };
		int R = rows.length;
		int[][] g = new int[R][n];
		for (int r = 0; r < R; r++) {
			for (int i = 0; i < n; i++) {
				double th = 360.0 * i / n;
				g[r][i] = me.v(ell(0, rows[r][0], r == R - 1 ? b.neckZ + 1 : cz, rows[r][1], rows[r][2], rows[r][3], th), SKULL);
			}
		}
		int crown = me.v(0, top - 1.5, cz + 1, SKULL);
		me.prio = 5;
		for (int i = 0; i < n; i++) {
			me.tri(g[0][i], g[0][(i + 1) % n], crown, st.dark, new double[] { 0, top + 12, cz });
		}
		for (int r = 0; r + 1 < R; r++) {
			for (int i = 0; i < n; i++) {
				int j = (i + 1) % n;
				boolean face = r >= 2 && (i == n - 1 || i == 0); // the opening: from the brow down, either side of the front
				if (face) {
					continue;
				}
				double[] ax = { 0, (rows[r][0] + rows[r + 1][0]) / 2, cz };
				me.quad(g[r][i], g[r][j], g[r + 1][j], g[r + 1][i], st.dark, ax);
				if (r >= 2) { // the lining, seen through the opening
					me.quadIn(g[r][i], g[r][j], g[r + 1][j], g[r + 1][i], BaseGhillie.SHADOW, ax);
				}
			}
		}
		// the shadow where the face is, set back in the opening, and two pale eyes in it
		double fz = b.headFront + 4.5;
		int s0 = me.v(-rx * 0.55, brow - 1, fz, SKULL), s1 = me.v(rx * 0.55, brow - 1, fz, SKULL), s2 = me.v(rx * 0.6, chin - 2, fz + 2, SKULL),
				s3 = me.v(-rx * 0.6, chin - 2, fz + 2, SKULL);
		me.quadDir(s0, s1, s2, s3, SHADOW, new double[] { 0, 0, -1 });
		me.prio = 6;
		for (int e = -1; e <= 1; e += 2) {
			double ex = e * (b.female ? 3.0 : 3.6), ey = brow + (b.female ? 4.5 : 5);
			int a = me.v(ex - 1.3, ey, fz - 0.5, SKULL), c = me.v(ex + 1.3, ey, fz - 0.5, SKULL), t = me.v(ex + e * 0.4, ey - 1.2, fz - 0.5, SKULL),
					u = me.v(ex - e * 0.2, ey + 0.9, fz - 0.5, SKULL);
			me.triDir(a, t, c, EYE, new double[] { 0, 0, -1 });
			me.triDir(a, c, u, EYE, new double[] { 0, 0, -1 });
		}
		// the leaves: shingled rows round the hood, kicked out further near the crown
		me.prio = 7;
		int k = 0;
		for (int r = 1; r < R; r++) {
			int m = r == R - 1 ? 12 : 10;
			for (int i = 0; i < m; i++) {
				double th = 360.0 * (i + (r % 2) * 0.5) / m;
				double d = Math.abs(((th + 180) % 360) - 180);
				if (r >= 2 && d < 34) {
					continue; // not over the face: the brow fringe covers it
				}
				double y = rows[r][0] - (r == 1 ? 1 : 2);
				double gr = r == R - 1 ? 1.1 : 0.3;
				double[] a = ell(0, y, r == R - 1 ? b.neckZ + 1 : cz, rows[r][1] + gr, rows[r][2] + gr, rows[r][3] + gr, th);
				double[] o = outward(a, cz, rows[r][1], rows[r][3]);
				double h = hash(k, 3), h2 = hash(k, 5);
				k++;
				double kick = Math.toRadians(lerp(62, 28, (r - 1) / (R - 2.0)) + (h2 - 0.5) * 18);
				double[] dir = add(scale(new double[] { 0, 1, 0 }, Math.cos(kick)), scale(o, Math.sin(kick)));
				double len = (r == R - 1 ? 10.6 : 9.5) * (0.85 + 0.3 * h);
				leaf(me, a, dir, o, len, len * 0.62, leafColour(st, h), LEAF_DARK, r == R - 1, SKULL);
			}
		}
		// the crest: leaves standing up off the crown, leaning back
		for (int i = 0; i < 5; i++) {
			double x = (i - 2) * 2.6;
			double[] a = { x, top + 0.5, cz + 1 + Math.abs(i - 2) * 1.2 };
			double[] dir = { x * 0.12, -1, 0.55 + Math.abs(i - 2) * 0.1 };
			double len = i == 2 ? 13 : 10;
			leaf(me, a, dir, new double[] { x * 0.1, 0, 1 }, len, len * 0.55, leafColour(st, hash(i, 11)), LEAF_DARK, true, SKULL);
		}
		// the fringe: long leaves flaring forward off the brow over the shadowed face
		for (int i = 0; i < 5; i++) {
			double u = (i - 2) / 2.0;
			double th = u * 40;
			double[] a = ell(0, brow - 1.5, cz, rows[2][1], rows[2][2], rows[2][3], th);
			double[] dir = { u * 0.6, 0.55, -1 };
			double len = (i == 2 ? 7.5 : 11.5) * (0.9 + 0.2 * hash(i, 7)); // short in the middle: the eyes look out under it
			leaf(me, a, dir, new double[] { u * 0.3, -1, -0.25 }, len, len * 0.6, leafColour(st, hash(i, 13)), LEAF_DARK, true, SKULL);
		}
		// a bramble circlet round the hood above the brow, berries at the side
		double[][] path = new double[9][];
		for (int i = 0; i < 9; i++) {
			double th = 40 + 280.0 * i / 8;
			path[i] = ell(0, top + 5 + Math.sin(i * 1.7) * 1.2, cz, rows[1][1] + 1.6, rows[1][2] + 1.6, rows[1][3] + 1.6, th);
		}
		me.prio = 8;
		bramble(me, path, new double[] { 0, -0.4, 0.2 }, new int[] { 2 }, p -> SKULL, 1);
		anchors(me, b.head, st.dark);
		return me.done();
	}

	/* ====================================================================== */
	/*                                SHROUD                                  */
	/* ====================================================================== */

	static ModelData shroud(Style st, Body b) {
		Mesh me = new Mesh();
		ModelData trunk = ArmourForge.mergeAll(b.torso, b.legs);
		double cz = (b.chestZ + b.backZ) / 2, L = b.waistY - b.neckY;
		// ------------------------------------------------ the jerkin
		double[][] rows = { { b.neckY + 1.5, 1.2, CHEST }, { b.neckY + L * 0.35, 1.4, CHEST }, { b.neckY + L * 0.7, 1.4, BELLY }, { b.waistY, 1.6, BELLY },
				{ b.waistY + 9, 2.4, BELLY } };
		int n = 8;
		int[][] jr = new int[rows.length][];
		for (int r = 0; r < rows.length; r++) {
			jr[r] = fitted(me, trunk, rows[r][0], cz, n, rows[r][1], b.neckR, (int) rows[r][2]);
		}
		me.prio = 1;
		for (int r = 0; r + 1 < rows.length; r++) {
			me.band(jr[r], jr[r + 1], true, new int[] { r == rows.length - 2 ? st.leather : st.main }, 0, cz, false);
		}
		ModelData jerkin = BaseWrap.compact(me.m);
		// ------------------------------------------------ the bound sleeves
		for (int s = 0; s < 2; s++) {
			double[] sh = b.shoulder[s], el = b.elbow[s], wr = b.wrist[s];
			double[] up = norm(sub(el, sh)), fo = norm(sub(wr, el));
			double[][] c = { add(sh, new double[] { -sx(s) * 0.5, -1.5, 0 }), lerp(sh, el, 0.5), el, lerp(el, wr, 0.5), lerp(el, wr, 0.95) };
			double[][] ax = { up, up, norm(add(up, fo)), fo, fo };
			double sc = b.female ? 0.85 : 1;
			double[] rad = { Math.max(b.shoulderR[s][0], b.shoulderR[s][1]) + 1.5, 6.4 * sc, Math.max(b.elbowR[s][0], b.elbowR[s][1]) + 1.6, 5.6 * sc, 5.0 * sc };
			int[] labs = { UPPER[s], UPPER[s], UPPER[s], FORE[s], FORE[s] };
			int[][] rg = new int[c.length][];
			for (int k = 0; k < c.length; k++) {
				rg[k] = ring(me, c[k], ax[k], rad[k], rad[k], 6, 0.2, labs[k]);
			}
			for (int k = 0; k + 1 < c.length; k++) {
				me.tube(rg[k], rg[k + 1], new int[] { k % 2 == 0 ? st.dark : st.leather, k % 2 == 0 ? st.leather : st.dark }, c[k], c[k + 1]);
			}
		}
		// ------------------------------------------------ a bramble wound across the jerkin, berries in it
		me.prio = 3;
		double hw = b.torsoHW;
		double[][] path = new double[8][];
		for (int i = 0; i < 8; i++) {
			double t = i / 7.0;
			double x = lerp(hw * 0.75, -hw * 0.8, t), y = lerp(b.neckY + L * 0.12, b.waistY + 4, t) + Math.sin(t * Math.PI * 3) * 2.5;
			path[i] = add(onFront(jerkin, x + Math.sin(t * Math.PI * 4) * 1.5, y), new double[] { 0, 0, -1.4 });
		}
		bramble(me, path, new double[] { 0, 0, -1 }, new int[] { 2, 5 }, p -> p[1] < b.neckY + L * 0.5 ? CHEST : BELLY, 2);
		// ------------------------------------------------ the leaf shroud
		ModelData ref = ArmourForge.mergeAll(b.torso, b.arms);
		ToIntFunction<double[]> shoulders = p -> BasePilgrim.shoulderLabel(ref, p, b.torsoHW);
		ToIntFunction<double[]> chain = p -> capeLabel(b, p[1]);
		double ao = b.armOuter, sTop = b.shoulderTop, fr = -b.chestZ, bk = b.backZ, hipBk = b.hipsBackZ;
		// the back drape first (the mantle overlaps it): from under the mantle to behind the knees, rising at the sides
		me.prio = 2;
		double hemBack = b.knee[0][1] + 2, hemSide = b.hipJointY - 10;
		sheet(me, st, new double[][] { { sTop + 12, sTop + 12, cz, ao - 1, fr + 4, bk + 13 }, { b.waistY - 4, b.waistY - 4, cz, ao + 3, fr + 6, bk + 15 },
				{ lerp(b.waistY, hemSide, 0.6), lerp(b.waistY, hemBack, 0.55), cz, ao + 5, fr + 7, hipBk + 14 }, { hemSide, hemBack, cz, ao + 6, fr + 8, hipBk + 15 } },
				74, 286, 9, chain, st.dark, st.dark, 34, 24, 14, 40);
		// the mantle: round the neck, over the shoulders and humped behind, open at the throat
		me.prio = 4;
		sheet(me, st, new double[][] { { b.neckY - 2.5, b.neckY - 3.5, b.neckZ, b.neckR + 5, b.neckR + 4, b.neckR + 6 }, { sTop + 1, sTop - 1, cz, ao + 1, fr + 5, bk + 10 },
				{ sTop + 13, sTop + 11, cz, ao + 4.5, fr + 7, bk + 14 }, { sTop + 24, sTop + 22, cz, ao + 6.5, fr + 8.5, bk + 15.5 } }, 26, 334, 13, shoulders, st.dark,
				st.dark, 58, 30, 12, 8);
		anchors(me, b.torso, st.main);
		anchors(me, b.arms, st.main);
		return me.done();
	}

	/* ====================================================================== */
	/*                                 CHAPS                                  */
	/* ====================================================================== */

	/** Bound leggings, a belt, leaf shingles over the front and outside of each thigh, a bramble round each thigh. */
	static ModelData chaps(Style st, Body b) {
		Mesh me = new Mesh();
		int n = 7;
		double[][][] legRows = new double[2][][];
		for (int s = 0; s < 2; s++) {
			double kneeY = b.knee[s][1], ankY = b.ankle[s][1];
			double[][] rows = { { b.waistY + 1, 1.8, HIPS }, { b.hipJointY + 2, 1.8, THIGH[s] }, { (b.hipJointY + kneeY) / 2, 1.6, THIGH[s] }, { kneeY - 2, 1.6, THIGH[s] },
					{ kneeY + 6, 1.5, SHIN[s] }, { (kneeY + ankY) / 2 + 4, 1.4, SHIN[s] }, { ankY + 3, 1.2, ANKLE[s] } };
			legRows[s] = rows;
			int[][] rg = new int[rows.length][];
			double[][] q = new double[rows.length][];
			for (int r = 0; r < rows.length; r++) {
				q[r] = legAt(b, rows[r][0], s, rows[r][1]);
				if (r == 0) {
					q[r][0] = sx(s) * Math.max(Math.abs(q[r][0]), 8.5);
					q[r][2] = Math.max(q[r][2], 10);
				} else {
					inner(q[r], s, 1.2);
				}
				rg[r] = new int[n];
				for (int j = 0; j < n; j++) {
					double a = Math.PI * 2 * j / n;
					rg[r][j] = me.v(q[r][0] + Math.sin(a) * q[r][2], rows[r][0], q[r][1] - Math.cos(a) * q[r][3], (int) rows[r][2]);
				}
			}
			me.prio = 1;
			for (int r = 0; r + 1 < rows.length; r++) {
				// bound in strips: the colours step round one column per row, a spiral of binding
				int[] cols = new int[n];
				for (int j = 0; j < n; j++) {
					cols[j] = (j + r) % 3 == 0 ? st.leather : st.dark;
				}
				me.band(rg[r], rg[r + 1], true, r == 0 ? new int[] { st.leatherDark } : cols, (q[r][0] + q[r + 1][0]) / 2, (q[r][1] + q[r + 1][1]) / 2, false);
			}
			// the leaves over the thigh: four staggered rows over the front and the outside
			me.prio = 3;
			int k = 0;
			for (int r = 0; r < 4; r++) {
				double y = lerp(b.hipJointY - 4, kneeY + 1, r / 3.5);
				double[] lq = inner(legAt(b, y, s, 2.2), s, 1.2);
				int lab = r == 0 ? HIPS : THIGH[s];
				int m = 5;
				for (int i = 0; i < m; i++) {
					// from inside-front round the front to behind the outside
					double th = sx(s) * (-25 + 125.0 * (i + (r % 2) * 0.5) / (m - 0.5));
					double a = Math.toRadians(th);
					double[] p = { lq[0] + Math.sin(a) * lq[2], y, lq[1] - Math.cos(a) * lq[3] };
					double[] o = norm(new double[] { Math.sin(a) / lq[2], 0, -Math.cos(a) / lq[3] });
					double h = hash(s * 50 + k, 23), h2 = hash(s * 50 + k, 29);
					k++;
					double kick = Math.toRadians(32 + (h2 - 0.5) * 18);
					double[] dir = add(scale(new double[] { 0, 1, 0 }, Math.cos(kick)), scale(o, Math.sin(kick)));
					double len = 11 * (0.85 + 0.3 * h);
					leaf(me, add(p, scale(o, 0.4)), dir, o, len, len * 0.6, leafColour(st, h), LEAF_DARK, r == 3, lab);
				}
			}
			// a bramble round the thigh above the knee
			double by = kneeY - 9;
			double[] bq = inner(legAt(b, by, s, 3.2), s, 1.2);
			double[][] path = new double[7][];
			for (int i = 0; i < 7; i++) {
				double a = Math.toRadians(sx(s) * (-60 + 220.0 * i / 6));
				path[i] = new double[] { bq[0] + Math.sin(a) * bq[2], by + Math.sin(i * 2.1) * 1.5, bq[1] - Math.cos(a) * bq[3] };
			}
			me.prio = 4;
			int tl = THIGH[s];
			bramble(me, path, new double[] { sx(s) * 0.5, 0, -1 }, new int[] { 2 }, p -> tl, 3 + s);
		}
		anchors(me, b.legs, st.dark);
		return me.done();
	}

	/* ====================================================================== */
	/*                            BOOTS, BRACERS                              */
	/* ====================================================================== */

	/** The kit's feet in dark leather, the shafts bound in a wicker lattice from the ankle to mid-shin. */
	static ModelData boots(Style st, Body b) {
		Mesh me = new Mesh();
		copyKit(me, b.feet, (c, o) -> c[1] > b.soleY - 2 ? BARK : st.leatherDark, 1);
		int n = 7;
		for (int s = 0; s < 2; s++) {
			double ankY = b.ankle[s][1];
			double[] ys = { b.soleY - 13, ankY - 2, ankY - 11, ankY - 20 };
			double[] mg = { 1.4, 1.7, 2.2, 3.0 };
			int[][] rg = new int[ys.length][];
			double[][] q = new double[ys.length][];
			for (int r = 0; r < ys.length; r++) {
				if (ys[r] > b.legs.bounds()[4] - 1) {
					double[] f = BaseCoat.slice(b.feet, ys[r], s == 0 ? -1 : 1);
					q[r] = new double[] { (f[0] + f[1]) / 2, (f[2] + f[3]) / 2, (f[1] - f[0]) / 2 + mg[r], (f[3] - f[2]) / 2 + mg[r] };
				} else {
					q[r] = inner(legAt(b, ys[r], s, mg[r]), s, 1.2);
				}
				rg[r] = new int[n];
				for (int j = 0; j < n; j++) {
					double a = Math.PI * 2 * j / n + (r % 2) * Math.PI / n;
					rg[r][j] = me.v(q[r][0] + Math.sin(a) * q[r][2], ys[r], q[r][1] - Math.cos(a) * q[r][3], ANKLE[s]);
				}
			}
			me.prio = 2;
			for (int r = 0; r + 1 < ys.length; r++) {
				int[] cols = { r % 2 == 0 ? WICKER : WICKER_DARK, r % 2 == 0 ? WICKER_DARK : WICKER };
				me.band(rg[r], rg[r + 1], true, cols, (q[r][0] + q[r + 1][0]) / 2, (q[r][1] + q[r + 1][1]) / 2, false);
			}
			// the top: a rolled wicker rim
			int last = ys.length - 1;
			int[] rim = new int[n];
			double[] rq = inner(new double[] { q[last][0], q[last][1], q[last][2] + 1.2, q[last][3] + 1.2 }, s, 1.0);
			for (int j = 0; j < n; j++) {
				double a = Math.PI * 2 * j / n + (last % 2) * Math.PI / n;
				rim[j] = me.v(rq[0] + Math.sin(a) * rq[2], ys[last] + 2.2, rq[1] - Math.cos(a) * rq[3], ANKLE[s]);
			}
			me.band(rg[last], rim, true, new int[] { BARK_LIGHT }, q[last][0], q[last][1], false);
		}
		return me.done();
	}

	/** Bark bracers from the wrist to the elbow, ridged, three thorn spurs off the outside; the hands in dark gloves. */
	static ModelData bracers(Style st, Body b) {
		Mesh me = new Mesh();
		copyKit(me, b.hands, (c, o) -> st.leatherDark, 1);
		for (int s = 0; s < 2; s++) {
			double[] w = b.wrist[s], e = b.elbow[s];
			double[] ax = norm(sub(e, w));
			double sc = b.female ? 0.85 : 1;
			double[][] c = { add(w, scale(ax, 3)), add(w, scale(ax, 6)), lerp(w, e, 0.62), lerp(w, e, 0.88) };
			double[] rad = { 5.4 * sc, 6.2 * sc, 7.2 * sc, 7.9 * sc };
			int n = 7;
			int[][] rg = new int[c.length][];
			for (int k = 0; k < c.length; k++) {
				rg[k] = ring(me, c[k], ax, rad[k], rad[k], n, 0, FORE[s]);
			}
			me.prio = 3;
			for (int k = 0; k + 1 < c.length; k++) {
				me.tube(rg[k], rg[k + 1], k == 0 ? new int[] { BARK } : new int[] { BARK, BARK_LIGHT }, c[k], c[k + 1]);
			}
			// a lip at the elbow end
			int[] lip = ring(me, add(c[3], scale(ax, 1.5)), ax, rad[3] - 1.5, rad[3] - 1.5, n, 0, FORE[s]);
			me.tube(rg[3], lip, new int[] { BARK_LIGHT }, c[3], add(c[3], scale(ax, 1.5)));
			// thorn spurs off the outside, raking back towards the elbow
			double[] outS = { sx(s), 0, 0.15 };
			for (int k = 0; k < 3; k++) {
				double t = 0.3 + k * 0.22;
				double[] base = add(lerp(c[1], c[3], t), scale(norm(outS), lerp(rad[1], rad[3], t) - 0.6));
				double[] tip = add(add(base, scale(norm(outS), 4.5 + k)), scale(ax, 3.5));
				int[] r0 = ring(me, base, norm(sub(tip, base)), 1.5, 1.5, 3, 0.4 * k, FORE[s]);
				int t0 = me.v(tip, FORE[s]);
				for (int i = 0; i < 3; i++) {
					me.tri(r0[i], r0[(i + 1) % 3], t0, i == 0 ? DraftArmourA.THORN_DARK : DraftArmourA.THORN, base);
				}
			}
		}
		return me.done();
	}
}
