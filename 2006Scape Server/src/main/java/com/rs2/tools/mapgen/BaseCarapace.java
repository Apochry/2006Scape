package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ArmourForge.add;
import static com.rs2.tools.mapgen.ArmourForge.norm;
import static com.rs2.tools.mapgen.ArmourForge.scale;
import static com.rs2.tools.mapgen.ArmourForge.sub;
import static com.rs2.tools.mapgen.BaseCoat.ell;
import static com.rs2.tools.mapgen.BaseCoat.mirror;
import static com.rs2.tools.mapgen.BaseCoat.pt;
import static com.rs2.tools.mapgen.ModelData.hsl;

import java.io.IOException;

import com.rs2.tools.mapgen.ArmourSets.Style;
import com.rs2.tools.mapgen.BaseCoat.Mesh;
import com.rs2.tools.mapgen.BaseCoat.Rig;

/**
 * The Broodkeeper base: the keepers of the Silkwood broodmother's brood, half become spiders. Built from scratch for the
 * Broodkeeper (A33) draft set on the mystic robe's rig; see NewBases. Glossy purple-black chitin, a dusky robe, grey
 * silk, the brood's sickly green-yellow glow. No hood, no cape: the silhouette is the spider on the wearer's back.
 *
 * <ul>
 * <li>Helm (a "mask": it hides the head and beard): a segmented chitin mask-hood over the whole head, its face a hollow
 * of shadow; a cluster of eight glowing eyes across the brow (two great, two middling, four small); two curved mandible
 * hooks at the chin; a skirt of chitin over the neck. All on the skull label 1.</li>
 * <li>Body (a "top": it draws its own sleeves): a dark robe-tunic with three chitin lames down the front and a chitin
 * belt; on the back a glossy domed carapace, and eight long jointed spider legs rising from it: the front pair arches
 * over the shoulders and grips the chest, the second arches out wide past the arms, the back two pairs splay behind and
 * down. At the small of the back a swollen abdomen of an egg-sac (the bustle), grey silk banded dark, eggs glowing
 * through it. Robe sleeves with chitin bracers.</li>
 * <li>Legs: dark leggings under a skirt that hangs in torn silk strands: twelve ragged strips of robe and silk, cut to
 * points at different lengths, and web strung between them at the hem.</li>
 * <li>Boots: pointed chitin boots: two lames up the shin and a long pointed toe like the tip of a spider's leg. Gloves:
 * chitin cuffs, and four long jointed clawed fingers on each hand.</li>
 * </ul>
 *
 * <p>Labels, as BaseCoat: the mask on the skull 1; the tunic, the carapace and the spider legs on the chest 8 (above the
 * ribs) and the belly 4 (the spider rides the trunk, so its legs never tear from it); the egg-sac on the belly 4; the
 * sleeves on the upper arms 20 / 26 and forearms 19 / 22; the skirt's waistband on the static belt 41 and each strip on
 * the thigh of its side 43 / 44, the leggings on the thighs, the shins 33 / 36 and the ankles 32 / 38; boots on the ankles
 * and toes 45 / 46; gloves on the forearms and hands 27 / 28. Each piece keeps its donor's pivots on zero-area faces
 * (BaseSerpent.anchors).
 */
final class BaseCarapace {

	private BaseCarapace() {
	}

	/* Chitin (as DraftArmourB), silk, shadow, the brood's glow. */
	static final int CHITIN = hsl(48, 1, 17), CHITIN_LIGHT = hsl(48, 1, 32), CHITIN_DARK = hsl(48, 1, 9), GLOSS = hsl(50, 1, 52), SILK = hsl(51, 0, 88),
			SILK_DARK = hsl(51, 1, 64), SHADOW = hsl(51, 1, 4);

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		Rig r = BaseCoat.rig(af, female);
		BaseMantle.Body b = BaseMantle.body(af, female);
		switch (slot) {
		case ArmourSets.HELM:
			// a mask hides the head and jaw kits, and the mystic hat has no neck or jaw pivots: take them from the rune full helm
			return new ModelData[] { BaseSerpent.anchors(BaseSerpent.anchors(mask(r, b, st), donor), af.read(female ? 394 : ArmourForge.RUNE_HELM_WORN)), null };
		case ArmourSets.BODY:
			return new ModelData[] { BaseSerpent.anchors(body(r, b, st), donor), BaseSerpent.anchors(sleeves(r, st), donorSecond) };
		case ArmourSets.LEGS:
			return new ModelData[] { BaseSerpent.anchors(skirt(r, st), donor), null };
		case ArmourSets.BOOTS:
			return new ModelData[] { BaseSerpent.anchors(boots(r), donor), null };
		case ArmourSets.GLOVES:
			return new ModelData[] { BaseSerpent.anchors(claws(r), donor), null };
		default:
			return new ModelData[] { donor, donorSecond };
		}
	}

	/** The inventory model for the slot, or null to let ArmourSets build it the usual way from the worn model(s). */
	static ModelData inventory(ArmourForge af, Style st, int slot, ModelData[] worn) throws IOException {
		return null;
	}

	/* ====================================================================== */
	/*                                THE MASK                                */
	/* ====================================================================== */

	/**
	 * The mask-hood: rings round the head from the crown to a skirt on the neck, banded like a spider's segments; the front
	 * from brow to chin set back into a hollow of shadow; the eyes across the brow; mandible hooks at the chin.
	 */
	static ModelData mask(Rig r, BaseMantle.Body b, Style st) {
		Mesh m = new Mesh();
		final int L = 1;
		int[] hb = r.headB;
		double T = hb[1], C = b.headChin, F = hb[2], B = hb[5], HW = hb[3];
		double H = C - T, cz = (F + B) / 2 + 0.5, hz = (B - F) / 2;
		int n = 10;
		// y, half-width, half-depth (behind / in front are the same; the face is pulled in below)
		double[][] rows = { { T - 4.5, HW * 0.45, hz * 0.5 }, { T - 1.5, HW * 0.95, hz * 0.95 }, { T + 0.22 * H, HW + 2.6, hz + 3.0 }, { T + 0.55 * H, HW + 2.4, hz + 3.0 },
				{ C + 0.5, HW + 1.6, hz + 2.4 }, { r.neckY + 2.5, HW + 3.5, hz + 2.6 } };
		int R = rows.length;
		int[][] ring = new int[R][n];
		for (int i = 0; i < R; i++) {
			for (int j = 0; j < n; j++) {
				double phi = Math.PI * 2 * j / n;
				double rz = rows[i][2];
				boolean face = (i == 3 || i == 4) && (j == 0 || j == 1 || j == n - 1);
				double[] p = ell(0, rows[i][0], cz, rows[i][1], rz, phi);
				if (face) { // pulled back into the hollow
					p[2] += j == 0 ? 6.0 : 4.0;
				}
				if (i == R - 1) { // the skirt sits lower at the back and sides
					p[1] += 2.5 * (1 + Math.cos(phi + Math.PI)) / 2;
				}
				ring[i][j] = m.v(p, L);
			}
		}
		m.prio = 6;
		for (int i = 0; i + 1 < R; i++) {
			double[] c = pt(0, (rows[i][0] + rows[i + 1][0]) / 2, cz);
			for (int j = 0; j < n; j++) {
				int j2 = (j + 1) % n;
				boolean front = j == 0 || j == n - 1;
				int col = i % 2 == 0 ? CHITIN : CHITIN_LIGHT;
				if (i == 2 || i == 3) {
					col = front ? SHADOW : i == 2 ? CHITIN_LIGHT : CHITIN;
				}
				if (i == 0 && (j == 0 || j == 5)) {
					col = GLOSS;
				}
				m.quadAway(ring[i][j], ring[i][j2], ring[i + 1][j2], ring[i + 1][j], col, c);
			}
		}
		int crown = m.v(pt(0, T - 6, cz + 1), L);
		for (int j = 0; j < n; j++) {
			m.triAway(ring[0][j], ring[0][(j + 1) % n], crown, j % 2 == 0 ? CHITIN_LIGHT : GLOSS, pt(0, T, cz));
		}
		// the brow ridge over the hollow: a chitin lip the eyes sit on
		double browY = rows[2][0] - 0.5, browZ = cz - rows[2][2] - 0.5;
		// the eyes: two great ones in the middle, two middling outside them, four small above and beside
		double[][] eyes = { { -2.6, 1.0, 1.9 }, { 2.6, 1.0, 1.9 }, { -6.4, 0.2, 1.3 }, { 6.4, 0.2, 1.3 }, { -1.6, -3.4, 1.0 }, { 1.6, -3.4, 1.0 }, { -8.8, -2.4, 0.9 },
				{ 8.8, -2.4, 0.9 } };
		m.prio = 8;
		for (double[] e : eyes) {
			double x = e[0] * (HW / 11.0), y = browY + e[1];
			double zz = browZ + Math.pow(Math.abs(x) / (HW + 2.6), 2) * (rows[2][2] * 0.9) + 0.3;
			double[] c = pt(x, y, zz);
			double[] o = norm(pt(x * 0.06, -0.15, -1));
			gem(m, c, o, e[2], L, st.accent, st.core);
		}
		// the mandibles: two hooks from the sides of the chin curving down and in
		for (int s = -1; s <= 1; s += 2) {
			double[] root = pt(s * (HW * 0.6), C - 2.5, cz - rows[4][2] + 0.5);
			double[] mid = add(root, pt(s * 1.6, 5.5, -4.0));
			double[] tip = add(mid, pt(-s * 4.2, 3.5, -0.5));
			ModelData h = new ModelData();
			ArmourForge.blade(h, root, mid, pt(1, 0, 0), 2.0, 1.5, GLOSS);
			ArmourForge.blade(h, mid, tip, pt(0, 0, 1), 1.5, 1.1, CHITIN_LIGHT);
			BaseSerpent.absorb(m, h, 7, L);
		}
		return m.d;
	}

	/** A glowing bead: a little octahedron facing o, radius rad, its front lit. */
	static void gem(Mesh m, double[] c, double[] o, double rad, int label, int col, int core) {
		double[] a = norm(ModelData.cross(o, pt(0, 1, 0))), b = ModelData.cross(a, o);
		int f = m.v(add(c, scale(o, rad * 0.9)), label), k = m.v(add(c, scale(o, -rad * 0.6)), label);
		int[] e = new int[4];
		for (int i = 0; i < 4; i++) {
			double ang = Math.PI / 2 * i;
			e[i] = m.v(add(c, add(scale(a, Math.cos(ang) * rad), scale(b, Math.sin(ang) * rad))), label);
		}
		for (int i = 0; i < 4; i++) {
			m.triAway(e[i], e[(i + 1) % 4], f, i % 2 == 0 ? core : col, c);
			m.triAway(e[i], e[(i + 1) % 4], k, col, c);
		}
	}

	/* ====================================================================== */
	/*                                THE BODY                                */
	/* ====================================================================== */

	/** Scale of a body against the male one (x across, y down the trunk). */
	static double[] scaleOf(Rig r) {
		double hw = Math.max(-BaseCoat.slice(r.torso, r.neckY + 0.4 * (r.waistY - r.neckY), 0)[0], 1);
		return new double[] { hw / 17.6, (r.waistY - r.neckY) / 51.0 };
	}

	/** The robe-tunic, its lames and belt, the carapace, the spider's legs and the egg-sac. */
	static ModelData body(Rig r, BaseMantle.Body b, Style st) {
		Mesh m = new Mesh();
		tunic(m, r, st);
		carapace(m, r, b, st);
		return m.d;
	}

	static int trunkLabel(Rig r, double y) {
		return BaseSerpent.trunkLabel(r, y);
	}

	/** A dark robe-tunic from the neck to the hips, three chitin lames down the front, a chitin belt. */
	static void tunic(Mesh m, Rig r, Style st) {
		int n = 8;
		double top = r.neckY + 1, hem = r.hip[1] - 2;
		double[] ys = { top, r.neckY + 8, r.neckY + 0.5 * (r.waistY - r.neckY), r.waistY - 1, hem };
		int[][] ring = new int[ys.length][];
		double[][] d = new double[ys.length][];
		for (int i = 0; i < ys.length; i++) {
			d[i] = BaseSerpent.trunk(r, ys[i], 1.2 + (i == ys.length - 1 ? 1.5 : 0));
			if (i == 0) {
				d[i][1] = Math.min(d[i][1], 10);
				d[i][2] = Math.min(d[i][2], 9);
			}
			ring[i] = new int[n];
			for (int j = 0; j < n; j++) {
				ring[i][j] = m.v(ell(0, ys[i], d[i][0], d[i][1], d[i][2], Math.PI * 2 * j / n), trunkLabel(r, ys[i]));
			}
		}
		m.prio = 2;
		for (int i = 0; i + 1 < ys.length; i++) {
			m.band(ring[i], ring[i + 1], true, new int[] { st.main, st.dark }, pt(0, (ys[i] + ys[i + 1]) / 2, d[i][0]));
		}
		// the chitin lames: three overlapping shield-shaped plates down the front
		m.prio = 4;
		double y0 = r.neckY + 5, y1 = r.waistY - 4;
		for (int k = 0; k < 3; k++) {
			double a = BaseSerpent.lerp(y0, y1, k / 3.0) - (k > 0 ? 2 : 0), bb = BaseSerpent.lerp(y0, y1, (k + 1) / 3.0) + 1.5;
			double[] da = BaseSerpent.trunk(r, a, 2.0 + k * 0.3), db = BaseSerpent.trunk(r, bb, 2.4 + k * 0.3);
			double w = 5.0 + k * 1.4;
			int lab = trunkLabel(r, (a + bb) / 2);
			int p0 = m.v(pt(-w, a, da[0] - da[2] + 0.8), lab), p1 = m.v(pt(w, a, da[0] - da[2] + 0.8), lab), p2 = m.v(pt(w * 0.9, bb - 2.5, db[0] - db[2] - 0.2), lab),
					p3 = m.v(pt(0, bb, db[0] - db[2] - 0.8), lab), p4 = m.v(pt(-w * 0.9, bb - 2.5, db[0] - db[2] - 0.2), lab);
			int ctr = m.v(pt(0, (a + bb) / 2, Math.min(da[0] - da[2], db[0] - db[2]) - 1.6), lab);
			int[] rim = { p0, p1, p2, p3, p4 };
			for (int i = 0; i < 5; i++) {
				m.tri(rim[i], rim[(i + 1) % 5], ctr, i == 0 ? GLOSS : i % 2 == 0 ? CHITIN : CHITIN_LIGHT, pt(0, 0, -1));
			}
		}
		// the belt: a band of chitin at the waist
		double[] w = BaseSerpent.trunk(r, r.waistY, 2.2);
		int[] b0 = new int[n], b1 = new int[n];
		for (int j = 0; j < n; j++) {
			double phi = Math.PI * 2 * j / n;
			b0[j] = m.v(ell(0, r.waistY - 2.5, w[0], w[1], w[2], phi), 4);
			b1[j] = m.v(ell(0, r.waistY + 3, w[0], w[1] + 0.6, w[2] + 0.6, phi), 4);
		}
		m.prio = 4;
		m.band(b0, b1, true, new int[] { CHITIN_LIGHT, CHITIN }, pt(0, r.waistY, w[0]));
	}

	/**
	 * The spider on the back: a glossy domed carapace on the shoulder blades, eight long jointed legs rising from it and
	 * arching over and round the shoulders, and the swollen egg-sac of its abdomen at the small of the back.
	 */
	static void carapace(Mesh m, Rig r, BaseMantle.Body b, Style st) {
		double[] k = scaleOf(r);
		double sx = k[0], sy = k[1];
		double n = r.neckY, w = r.waistY, zb = b.backZ;
		// the carapace: an ellipsoid swept down the back, its front half inside the trunk
		{
			double top = n + 1, bot = n + 40 * sy;
			int rows = 7;
			double[][] p = new double[rows][], up = new double[rows][];
			double[] ra = new double[rows], rb = new double[rows];
			int[] lab = new int[rows];
			double[] prof = { 0, 0.62, 0.9, 1, 0.94, 0.7, 0 };
			for (int i = 0; i < rows; i++) {
				double t = (double) i / (rows - 1), y = BaseSerpent.lerp(top, bot, t);
				double[] tr = BaseSerpent.trunk(r, y, 0);
				p[i] = pt(0, y, tr[0] + tr[2] - 2.5 + 3.0 * Math.sin(Math.PI * t));
				up[i] = pt(0, -0.25, 1);
				ra[i] = 10.5 * prof[i] * sx;
				rb[i] = 17.0 * prof[i] * sx;
				lab[i] = trunkLabel(r, y);
			}
			m.prio = 6;
			BaseSerpent.sweep(m, p, up, ra, rb, 8, lab, (i, u, s) -> u > 0.6 ? (i == 2 || i == 3 ? GLOSS : CHITIN_LIGHT) : u > -0.2 ? (i % 2 == 0 ? CHITIN_LIGHT : CHITIN) : CHITIN_DARK);
		}
		// the legs: root on the carapace, a first joint high over the shoulder, a second, then the claw
		double[][][] legs = { { { 9, 6, 3 }, { 22, -26, -1 }, { 19, -8, -19 }, { 10, 15, -15 } }, // over the shoulder, gripping the chest
				{ { 12, 12, 4 }, { 34, -20, 2 }, { 41, 2, -5 }, { 39, 24, -6 } }, // out wide past the arm
				{ { 13, 21, 5 }, { 33, -6, 12 }, { 37, 18, 17 }, { 31, 44, 13 } }, // splayed behind
				{ { 11, 30, 4 }, { 26, 12, 17 }, { 27, 40, 22 }, { 18, 58, 16 } } }; // down behind the hips
		m.prio = 7;
		for (int s = -1; s <= 1; s += 2) {
			for (int l = 0; l < legs.length; l++) {
				double[][] q = new double[4][];
				for (int i = 0; i < 4; i++) {
					double[] g = legs[l][i];
					q[i] = pt(s * g[0] * sx, n + g[1] * sy, zb + g[2] * (i == 0 ? 1 : sx));
				}
				q[0][2] = BaseSerpent.trunk(r, q[0][1], 0)[0] + BaseSerpent.trunk(r, q[0][1], 0)[2] + 1.5;
				leg(m, q, trunkLabel(r, q[0][1]), l);
			}
		}
		// the egg-sac: a swollen abdomen hung at the small of the back, grey silk banded dark, the eggs glowing through
		{
			double[] a0 = pt(0, w - 12 * sy, BaseSerpent.trunk(r, w - 12 * sy, 0)[0] + BaseSerpent.trunk(r, w - 12 * sy, 0)[2] - 2);
			double[] dir = norm(pt(0, 0.62, 0.78));
			int rows = 7;
			double len = 32 * sy;
			double[] prof = { 0, 0.62, 0.92, 1, 0.9, 0.6, 0 };
			double[][] p = new double[rows][], up = new double[rows][];
			double[] ra = new double[rows], rb = new double[rows];
			int[] lab = new int[rows];
			for (int i = 0; i < rows; i++) {
				double t = (double) i / (rows - 1);
				p[i] = add(a0, add(scale(dir, len * t), pt(0, 0, 3.0 * Math.sin(Math.PI * t))));
				up[i] = pt(0, -0.6, 1);
				ra[i] = 13.5 * prof[i] * sx;
				rb[i] = 14.0 * prof[i] * sx;
				lab[i] = 4;
			}
			m.prio = 5;
			BaseSerpent.sweep(m, p, up, ra, rb, 8, lab, (i, u, sd) -> {
				if (i == 2 && u > 0.6 || i == 3 && sd > 0.8 || i == 3 && sd < -0.8 && u < 0 || i == 4 && u > 0.3 && sd < -0.3) {
					return st.accent; // an egg glowing through the silk
				}
				return i % 2 == 0 ? SILK : SILK_DARK;
			});
			// spinnerets at the tip
			double[] tip = p[rows - 1];
			ModelData sp = new ModelData();
			ArmourForge.blade(sp, add(tip, scale(dir, -2)), add(tip, scale(dir, 3.5)), pt(1, 0, 0), 1.6, 1.2, CHITIN_LIGHT);
			BaseSerpent.absorb(m, sp, 6, 4);
		}
	}

	/** One spider leg through the four points: square segments tapering to a claw, the joints knuckled. */
	static void leg(Mesh m, double[][] q, int label, int which) {
		double[][] p = { q[0], q[1], q[2], q[3] };
		double[] ra = { 3.0, 2.5, 1.7, 0 };
		double[][] up = new double[4][];
		for (int i = 0; i < 4; i++) {
			up[i] = pt(0, -1, 0.2);
		}
		BaseSerpent.sweep(m, p, up, ra, ra, 4, new int[] { label, label, label, label },
				(i, u, s) -> i == 2 ? (u > 0.3 ? GLOSS : CHITIN_LIGHT) : u > 0.3 ? CHITIN_LIGHT : i == 0 ? CHITIN_DARK : CHITIN);
		// knuckles at the two joints
		for (int i = 1; i <= 2; i++) {
			ModelData kn = new ModelData();
			double[] d = norm(sub(q[i + 1], q[i - 1]));
			ArmourForge.blade(kn, add(q[i], scale(d, -2.6)), add(q[i], scale(d, 2.6)), pt(0, 1, 0), 3.2, 3.2, i == 1 ? GLOSS : CHITIN_LIGHT);
			BaseSerpent.absorb(m, kn, 8, label);
		}
	}

	/** Robe sleeves with chitin bracers (the body's second model). */
	static ModelData sleeves(Rig r, Style st) {
		Mesh m = new Mesh();
		m.prio = 10;
		for (int side = -1; side <= 1; side += 2) {
			int up = side > 0 ? 26 : 20, fo = side > 0 ? 22 : 19;
			double[] J = mirror(pt(r.shoulder[0], r.armTop + 4, r.shoulder[2]), side), E = mirror(r.elbow, side), W = mirror(pt(r.wrist[0], r.wristY, r.wrist[2]), side);
			double armR = Math.max(5, (BaseCoat.extent(r.arms, 21)[3] - BaseCoat.extent(r.arms, 21)[0]) / 2);
			double foreR = Math.max(4, (BaseCoat.extent(r.arms, 19)[3] - BaseCoat.extent(r.arms, 19)[0]) / 2);
			double[] Au = norm(sub(E, J)), Af = norm(sub(W, E));
			double[][] p = { add(J, scale(Au, -1.5)), BaseSerpent.lerp(J, E, 0.5), add(E, scale(Af, 1)), add(E, scale(Af, 3)), add(W, scale(Af, -2)), W };
			double[] rad = { armR + 0.8, armR + 0.7, foreR + 1.8, foreR + 2.4, foreR + 2.6, foreR + 3.0 };
			int[] lab = { up, up, fo, fo, fo, fo };
			double[][] u = new double[p.length][];
			for (int i = 0; i < p.length; i++) {
				u[i] = pt(side, -0.3, 0);
			}
			BaseSerpent.sweep(m, p, u, rad, rad, 6, lab, (i, c, s) -> i >= 3 ? (c > 0.3 ? GLOSS : i == 4 ? CHITIN_LIGHT : CHITIN) : i % 2 == 0 ? st.main : st.dark);
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                                THE LEGS                                */
	/* ====================================================================== */

	/** Leggings, a waistband, and a skirt of torn silk strips with web strung between them at the hem. */
	static ModelData skirt(Rig r, Style st) {
		Mesh m = new Mesh();
		// the leggings
		for (int s = -1; s <= 1; s += 2) {
			int thigh = s > 0 ? 43 : 44, shin = s > 0 ? 36 : 33, ank = s > 0 ? 38 : 32;
			double[][] rows = { { r.waistY + 4, 1.0, thigh }, { r.thigh[1], 1.0, thigh }, { r.knee[1] + 2, 1.0, shin }, { r.ankle[1] + 1, 0.9, ank } };
			int n = 6;
			int[][] idx = new int[rows.length][n];
			double[][] lr = new double[rows.length][];
			for (int k = 0; k < rows.length; k++) {
				double[] q = BaseCoat.legRing(r, rows[k][0], s, rows[k][1]);
				if (k == 0) {
					q[0] = s * Math.max(Math.abs(q[0]), 8.5);
					q[2] = Math.max(q[2], 9.0);
				}
				lr[k] = q;
				for (int j = 0; j < n; j++) {
					idx[k][j] = m.v(ell(q[0], rows[k][0], q[1], q[2], q[3], Math.PI * 2 * j / n), (int) rows[k][2]);
				}
			}
			m.prio = 1;
			for (int k = 0; k + 1 < rows.length; k++) {
				double[] in = pt((lr[k][0] + lr[k + 1][0]) / 2, (rows[k][0] + rows[k + 1][0]) / 2, (lr[k][1] + lr[k + 1][1]) / 2);
				m.band(idx[k], idx[k + 1], true, new int[] { k == 2 ? CHITIN : st.dark, CHITIN_DARK }, in);
			}
		}
		// the waistband
		int wn = 10;
		double[] s0 = BaseCoat.slice(r.legs, r.waistY + 3, 0);
		double cz = (s0[2] + s0[3]) / 2, rx = Math.max(-s0[0], s0[1]) + 2.5, rz = (s0[3] - s0[2]) / 2 + 2.5;
		int[] w0 = new int[wn], w1 = new int[wn];
		for (int j = 0; j < wn; j++) {
			double phi = Math.PI * 2 * j / wn;
			w0[j] = m.v(ell(0, r.waistY - 1.5, cz, rx, rz, phi), 41);
			w1[j] = m.v(ell(0, r.waistY + 5, cz, rx + 0.5, rz + 0.5, phi), 41);
		}
		m.prio = 2;
		m.band(w0, w1, true, new int[] { st.dark, CHITIN }, pt(0, r.waistY + 2, cz));
		// the strips: twelve round the hips, each hanging from the band to a ragged point, robe and silk in turn
		int strips = 12;
		double top = r.waistY + 4;
		double[] lens = { 0.78, 0.55, 0.9, 0.62, 0.84, 0.5 }; // of the way from the band to the ankle
		int[][] hemPts = new int[strips][];
		double[][] hemPos = new double[strips][];
		for (int i = 0; i < strips; i++) {
			double phi = Math.PI * 2 * (i + 0.5) / strips;
			int side = Math.sin(phi) >= 0 ? 1 : -1;
			int lab = side > 0 ? 43 : 44;
			double half = Math.PI / strips * 0.92;
			double len = lens[i % lens.length] * (r.ankle[1] - top);
			double ym = top + len * 0.55, yb = top + len;
			double[] dt = { cz, rx + 0.3, rz + 0.3 };
			double flare = 3.5 + len * 0.06;
			int[] t = { m.v(ell(0, top, dt[0], dt[1], dt[2], phi - half), lab), m.v(ell(0, top, dt[0], dt[1], dt[2], phi + half), lab) };
			int[] mm = { m.v(ell(0, ym, dt[0], dt[1] + flare * 0.6, dt[2] + flare * 0.6, phi - half * 0.95), lab),
					m.v(ell(0, ym, dt[0], dt[1] + flare * 0.6, dt[2] + flare * 0.6, phi + half * 0.95), lab) };
			double ragged = (i % 3 == 0 ? 0.35 : -0.3) * half;
			int[] bt = { m.v(ell(0, yb - len * 0.12, dt[0], dt[1] + flare, dt[2] + flare, phi - half * 0.8), lab),
					m.v(ell(0, yb - len * 0.2, dt[0], dt[1] + flare, dt[2] + flare, phi + half * 0.75), lab) };
			int tip = m.v(ell(0, yb, dt[0], dt[1] + flare + 0.5, dt[2] + flare + 0.5, phi + ragged), lab);
			int col = i % 2 == 0 ? st.main : SILK_DARK, col2 = i % 2 == 0 ? st.dark : SILK;
			double[] in = pt(0, (top + yb) / 2, cz);
			m.prio = 3 + i % 2;
			m.quadAway(t[0], t[1], mm[1], mm[0], col, in);
			m.quadAway(mm[0], mm[1], bt[1], bt[0], col2, in);
			m.triAway(bt[0], bt[1], tip, col, in);
			// the inside, seen as the strips swing
			double[] fq = scale(add(add(m.p(t[0]), m.p(t[1])), add(m.p(mm[0]), m.p(mm[1]))), 0.25);
			m.quad(t[0], t[1], mm[1], mm[0], CHITIN_DARK, sub(in, fq));
			hemPts[i] = new int[] { mm[0], mm[1] };
			hemPos[i] = ell(0, ym, dt[0], dt[1] + flare * 0.6, dt[2] + flare * 0.6, phi);
		}
		// web strung across between neighbouring strips, at mid length (each strand on the thigh of its side)
		m.prio = 6;
		for (int i = 0; i < strips; i++) {
			int j = (i + 1) % strips;
			double phi = Math.PI * 2 * (i + 1) / strips;
			if (Math.abs(Math.sin(phi)) < 0.3) {
				continue; // not across the middle front and back, where the legs part
			}
			int lab = Math.sin(phi) > 0 ? 43 : 44;
			double[] a = m.p(hemPts[i][1]), c = m.p(hemPts[j][0]);
			double[] mid = add(BaseSerpent.lerp(a, c, 0.5), pt(0, 3.5, 0));
			int va = m.v(a, lab), vc = m.v(c, lab), vm = m.v(mid, lab), vm2 = m.v(add(mid, pt(0, 1.2, 0)), lab);
			double[] outw = norm(pt(mid[0], 0, mid[2] - cz));
			m.tri(va, vc, vm, SILK, outw);
			m.tri(va, vm2, vc, SILK_DARK, scale(outw, -1));
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                             BOOTS, CLAWS                               */
	/* ====================================================================== */

	/** Pointed chitin boots: two lames up the shin and a long tapering toe like a spider's leg tip. */
	static ModelData boots(Rig r) {
		Mesh m = new Mesh();
		int n = 6;
		int[] fb = r.feetB;
		for (int s = -1; s <= 1; s += 2) {
			int ank = s > 0 ? 38 : 32, toe = s > 0 ? 46 : 45;
			double ankY = r.ankle[1];
			double[][] bands = { { ankY - 12, ankY + 0.5, 2.0, 3.4 }, { ankY - 2, fb[4] - 5, 2.4, 3.2 } };
			for (int k = 0; k < bands.length; k++) {
				double[] qt = BaseCoat.legRing(r, Math.max(bands[k][0], r.knee[1] + 4), s, bands[k][2]), qb = BaseCoat.legRing(r, Math.min(bands[k][1], ankY), s, bands[k][3]);
				if (k == 1) {
					qb[3] += 1.6;
					qb[1] += 1.2;
				}
				int[] t0 = new int[n], b0 = new int[n];
				for (int j = 0; j < n; j++) {
					double phi = Math.PI * 2 * j / n - Math.PI / n;
					double dip = j == 0 ? 2.5 : 0; // each lame comes to a point at the front
					t0[j] = m.v(ell(qt[0], bands[k][0], qt[1], qt[2], qt[3], phi), ank);
					b0[j] = m.v(ell(qb[0], bands[k][1] + dip, qb[1], qb[2], qb[3], phi), ank);
				}
				double[] in = pt((qt[0] + qb[0]) / 2, (bands[k][0] + bands[k][1]) / 2, (qt[1] + qb[1]) / 2);
				m.prio = 2 + k;
				for (int j = 0; j < n; j++) {
					int col = j == 0 || j == 5 ? (k == 0 ? GLOSS : CHITIN_LIGHT) : j % 2 == 0 ? CHITIN : CHITIN_DARK;
					m.quadAway(t0[j], t0[(j + 1) % n], b0[(j + 1) % n], b0[j], col, in);
				}
			}
			// the foot: a tapering point, longer than the foot, lifting a little at the tip
			double[] fe = BaseCoat.extent(r.feet, s > 0 ? 47 : 48);
			double x = (fe[0] + fe[3]) / 2, hw = (fe[3] - fe[0]) / 2 + 0.8;
			double sole = fb[4] + 0.5, back = fb[5] + 1.0, front = fb[2] - 1.5;
			double[][] sec = { { back, -8.0, hw * 0.9, ank }, { back - 8, -9.5, hw + 0.6, ank }, { front + 9, -5.0, hw * 0.85, toe }, { front + 1, -2.6, hw * 0.45, toe } };
			int[][] sx = new int[sec.length][];
			for (int k = 0; k < sec.length; k++) {
				double z = sec[k][0], topY = sole + sec[k][1], hwk = sec[k][2];
				int lab = (int) sec[k][3];
				sx[k] = new int[] { m.v(pt(x - hwk, sole, z), lab), m.v(pt(x + hwk, sole, z), lab), m.v(pt(x + hwk * 0.9, topY + 1.6, z), lab), m.v(pt(x, topY, z), lab),
						m.v(pt(x - hwk * 0.9, topY + 1.6, z), lab) };
			}
			int tip = m.v(pt(x, sole - 3.5, front - 8), toe);
			m.prio = 2;
			for (int k = 0; k + 1 < sec.length; k++) {
				double[] cin = pt(x, sole - 2, (sec[k][0] + sec[k + 1][0]) / 2);
				for (int e = 0; e < 5; e++) {
					int col = e == 0 ? CHITIN_DARK : e == 3 ? (k == 1 ? GLOSS : CHITIN_LIGHT) : CHITIN;
					m.quadAway(sx[k][e], sx[k][(e + 1) % 5], sx[k + 1][(e + 1) % 5], sx[k + 1][e], col, cin);
				}
			}
			int last = sec.length - 1;
			for (int e = 0; e < 5; e++) {
				m.triAway(sx[last][e], sx[last][(e + 1) % 5], tip, e == 3 ? CHITIN_LIGHT : CHITIN, pt(x, sole - 2, sec[last][0] + 2));
			}
			for (int e = 1; e + 1 < 5; e++) {
				m.tri(sx[0][0], sx[0][e], sx[0][e + 1], CHITIN_DARK, pt(0, 0, 1));
			}
		}
		return m.d;
	}

	/** Chitin cuffs, a chitin hand, four long jointed clawed fingers each. */
	static ModelData claws(Rig r) {
		Mesh m = new Mesh();
		for (int s = -1; s <= 1; s += 2) {
			int hand = s > 0 ? 28 : 27, fore = s > 0 ? 22 : 19;
			double[] W = mirror(BaseCoat.centroid(r.hands, 19), s), H = mirror(r.hand, s), E = mirror(r.elbow, s);
			double[] he = BaseCoat.extent(r.hands, 27);
			double hx = (he[3] - he[0]) / 2, hz = (he[5] - he[2]) / 2;
			double[] Af = norm(sub(W, E)), D = norm(sub(H, W));
			// the cuff, flaring up the forearm, and the hand
			double[][] p = { add(W, scale(Af, -10)), add(W, scale(Af, -3)), add(W, scale(D, 2)), add(W, scale(D, 9)) };
			double[] ra = { 6.8, 5.6, hx + 0.9, hx + 1.1 }, rb = { 6.8, 5.6, hz + 0.9, hz + 1.0 };
			double[][] up = { pt(s, 0, 0), pt(s, 0, 0), pt(s, 0, 0), pt(s, 0, 0) };
			m.prio = 10;
			BaseSerpent.sweep(m, p, up, ra, rb, 6, new int[] { fore, fore, hand, hand }, (i, c, sd) -> i == 0 ? (c > 0.4 ? GLOSS : CHITIN_LIGHT) : c > 0.4 ? CHITIN_LIGHT : CHITIN);
			// the fingers: from the knuckles, two joints, a long hooked claw
			double[] knuck = add(W, scale(D, 9.5));
			m.prio = 11;
			for (int f = 0; f < 4; f++) {
				double zz = -hz * 0.85 + 2 * hz * 0.85 * f / 3.0;
				double len = f == 1 || f == 2 ? 1.0 : 0.82;
				double[] a = add(knuck, pt(0, 0, zz));
				double[] b = add(a, add(scale(D, 7 * len), pt(s * 1.2, 0, zz * 0.12)));
				double[] c = add(b, add(scale(D, 7.5 * len), pt(-s * 2.6, 0, zz * 0.1)));
				double[][] fp = { a, b, c };
				double[] fr = { 1.1, 0.9, 0 };
				double[][] fu = { pt(s, 0, 0), pt(s, 0, 0), pt(s, 0, 0) };
				BaseSerpent.sweep(m, fp, fu, fr, fr, 4, new int[] { hand, hand, hand }, (i, cc, sd) -> i == 1 ? (cc > 0.3 ? GLOSS : CHITIN_LIGHT) : CHITIN);
			}
		}
		return m.d;
	}
}
