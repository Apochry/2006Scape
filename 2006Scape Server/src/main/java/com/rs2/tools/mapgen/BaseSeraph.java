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

import com.rs2.tools.mapgen.ArmourForge.Frame;
import com.rs2.tools.mapgen.ArmourSets.Style;
import com.rs2.tools.mapgen.BaseMantle.Body;
import com.rs2.tools.mapgen.BaseMantle.Mesh;

/**
 * The Dawnguard base (A39, Kind.COAT): a winged sun-knight, sworn to the first dawn after the Starless. Its silhouette is
 * a pair of great folded wings of layered metal feather-plates on the back, rising above the shoulders and tapering to
 * points past the hips. Built from scratch on the default body (see NewBases), on the rune plate's rig.
 *
 * <ul>
 * <li>Full helm (hides head and beard): a sleek closed helm with a dark visor slit and a gold crest-ridge, a swept-back
 * wing of three feather-plates at each temple, and a sun-disc standing behind the head with rays all round it.</li>
 * <li>Platebody (hides the arms: the plated sleeves are its own): a fluted cuirass narrowing to the waist, a gorget,
 * pointed rose-edged pauldrons of two lames, fluted plate down the arms, and the wings: each a gold bone from the
 * shoulder blade up to a point above the shoulder, the long primaries hanging from it to the hips, secondaries and
 * rose-tipped coverts layered over them.</li>
 * <li>Platelegs: hourglass faulds of three lames flaring from the waist, over fluted cuisses, pointed knee cops and
 * fluted greaves.</li>
 * <li>Boots: pointed sabatons: a lamed foot running out to a point, a flared cuff at the ankle.</li>
 * <li>Gauntlets: the hand in plate, a fluted cuff flaring up the forearm.</li>
 * <li>Shield: a round sun-disc: a domed sun boss, a rose rim, rays standing out past the rim all round.</li>
 * </ul>
 *
 * <p>Animation labels. No new vertex takes a pivot label ({@link BaseWraith#PIVOTS}); where a piece hides a kit
 * {@link BaseWraith#anchors} keeps the kit's joint centres on tiny hidden triangles. The helm, its wings and the
 * sun-disc are on the skull 1; the cuirass on the chest 8 and the belly 4, the gorget on the chest; the wings rigid on
 * the chest 8 (so they ride the back and never tear when the legs stride); pauldrons and rerebraces on the upper arm
 * 20 / 26, couters 18 / 24, vambraces 19 / 22. The faulds' top row is on the belly 4, every lame below on its thigh's
 * half 43 / 44 (split front and back, so the halves part in the stride); cuisses 43 / 44, knee cops and greaves the
 * shins 33 / 36, the ankles 32 / 38. Sabatons: the cuff and heel on the ankles 32 / 38, the foot from the ball to the
 * point on the toes 45 / 46, with the feet kit's pivots anchored. Gauntlets: the hands kit's own plates (its labels)
 * and the cuff on the forearm 19 / 22. The shield is on the left hand 28, as the stock shields are, in the rune kite's
 * place and plane.
 */
final class BaseSeraph {

	private BaseSeraph() {
	}

	static final int SKULL = 1, CHEST = 8, BELLY = 4;
	static final int[] UPPER = { 20, 26 }, ELBOW = { 18, 24 }, FORE = { 19, 22 }, THIGH = { 44, 43 }, SHIN = { 33, 36 }, ANKLE = { 32, 38 }, TOES = { 45, 46 };

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		Body b = BaseMantle.body(af, female);
		switch (slot) {
		case ArmourSets.HELM:
			return new ModelData[] { BaseWraith.separate(BaseWraith.anchors(helm(st, b), b.head)), null };
		case ArmourSets.BODY:
			return new ModelData[] { BaseWraith.separate(BaseWraith.anchors(cuirass(st, b), b.torso, b.arms)), null };
		case ArmourSets.LEGS:
			return new ModelData[] { BaseWraith.separate(BaseWraith.anchors(legs(st, b), b.legs)), null };
		case ArmourSets.SHIELD:
			return new ModelData[] { shield(af, st), null };
		case ArmourSets.BOOTS:
			return new ModelData[] { BaseWraith.separate(BaseWraith.anchors(sabatons(st, b), b.feet)), null };
		default:
			return new ModelData[] { BaseWraith.separate(gauntlets(st, b)), null };
		}
	}

	/** The inventory model: the usual fit from the worn model. */
	static ModelData inventory(ArmourForge af, Style st, int slot, ModelData[] worn) throws IOException {
		return null;
	}

	/* ====================================================================== */
	/*                                 PARTS                                  */
	/* ====================================================================== */

	/**
	 * A feather-plate from its root a to its point t: a flat six-sided plate, width w, its face (towards {@code facing})
	 * rising to a ridge down the middle; the face in col, the back in back. Flat-shaded.
	 */
	static void feather(Mesh me, double[] a, double[] t, double w, double[] facing, int label, int col, int col2, int back) {
		int shading = me.m.shading;
		me.m.shading = 1;
		double[] d = sub(t, a);
		double L = Math.sqrt(dot(d, d));
		double[] v = scale(d, 1 / L);
		double[] n = norm(sub(facing, scale(v, dot(facing, v))));
		double[] u = ModelData.cross(v, n);
		double[][] out = { { 0, 0 }, { 0.85, 0.16 }, { 0.75, 0.72 }, { 0, 1 }, { -0.75, 0.72 }, { -0.85, 0.16 } };
		int k = out.length;
		int[] r = new int[k];
		for (int i = 0; i < k; i++) {
			r[i] = me.v(add(a, add(scale(u, out[i][0] * w), scale(v, out[i][1] * L))), label);
		}
		double[] mid = add(a, scale(v, 0.45 * L));
		int ridge = me.v(add(mid, scale(n, Math.min(1.1, w * 0.2))), label), under = me.v(add(mid, scale(n, -0.5)), label);
		for (int i = 0; i < k; i++) {
			int j = (i + 1) % k;
			me.tri(r[i], r[j], ridge, i < 3 ? col : col2, add(mid, scale(n, -10)));
			me.tri(r[i], r[j], under, back, add(mid, scale(n, 10)));
		}
		me.m.shading = shading;
	}

	/** A closed ring round the axis c (direction a), {@code n} vertices, radius r, every other one pushed out by {@code flute}. */
	static int[] fluted(Mesh me, double[] c, double[] a, double rx, double rz, double flute, int n, double phase, int label) {
		int[] r = new int[n];
		for (int i = 0; i < n; i++) {
			double f = i % 2 == 0 ? flute : 0;
			r[i] = me.v(BaseCoat.around(c, a, rx + f, rz + f, Math.PI * 2 * i / n + phase), label);
		}
		return r;
	}

	/** Bands between rings, the faces alternating col / col2 round the ring (the flutes catch the light). */
	static void bands(Mesh me, int[][] rings, double[][] centres, int col, int col2) {
		for (int k = 0; k + 1 < rings.length; k++) {
			int n = rings[k].length;
			double[] in = lerp(centres[k], centres[k + 1], 0.5);
			for (int i = 0; i < n; i++) {
				int j = (i + 1) % n;
				me.quad(rings[k][i], rings[k][j], rings[k + 1][j], rings[k + 1][i], i % 2 == 0 ? col : col2, in);
			}
		}
	}

	/* ====================================================================== */
	/*                                THE HELM                                */
	/* ====================================================================== */

	/** The rays' gold, the disc's. */
	static final int SUN = DraftArmourC.SUN, SUN_CORE = DraftArmourC.SUN_CORE, SUN_BACK = ModelData.hsl(8, 4, 62);

	/** The sleek closed helm, the wings at its temples and the sun-disc behind it. */
	static ModelData helm(Style st, Body b) {
		Mesh me = new Mesh();
		double H = b.headTop, C = b.headChin, hc = (b.headFront + b.headBack) / 2, hw = b.headHW, hs = (C - H) / 24.0;
		double[] ys = { b.neckY + 4, C + 1, H + (C - H) * 0.58, H + (C - H) * 0.36, H + (C - H) * 0.12, H - 2 };
		double[] mg = { 4.5, 2.6, 2.6, 2.6, 2.4, 0 };
		int n = 12, NR = ys.length;
		int[][] g = new int[NR][n];
		double[][] cen = new double[NR][];
		for (int r = 0; r < NR; r++) {
			double[] s = BaseCoat.slice(b.head, Math.max(H + 1.5, Math.min(C, ys[r])), 0);
			double rx = Math.max(-s[0], s[1]) + mg[r], zf = s[2] - mg[r], zb = s[3] + mg[r];
			double cz = (zf + zb) / 2, rz = (zb - zf) / 2;
			if (r == 0) {
				rx = Math.max(rx, b.neckR + 8);
				rz = Math.max(rz, b.neckR + 7.5);
				cz = b.neckZ + 0.5;
			}
			if (r == NR - 1) {
				rx *= 0.62;
				rz *= 0.66;
				cz -= 0.5;
			}
			if (r == 1) { // the chin drawn to a point
				zf -= 2;
			}
			cen[r] = new double[] { 0, ys[r], cz };
			for (int i = 0; i < n; i++) {
				double[] p = ell(0, ys[r], cz, rx, rz, rz, 360.0 * i / n);
				if (r == 1 && i == 0) {
					p[2] -= 2.5;
				}
				g[r][i] = me.v(p, SKULL);
			}
		}
		for (int r = 0; r + 1 < NR; r++) {
			double[] in = lerp(cen[r], cen[r + 1], 0.5);
			for (int i = 0; i < n; i++) {
				int j = (i + 1) % n;
				int col = r == 0 ? st.trim : (i % 2 == 0 ? st.main : st.light);
				me.quad(g[r][i], g[r][j], g[r + 1][j], g[r + 1][i], col, in);
			}
		}
		double[] apex = { 0, H - 5 * hs, cen[NR - 1][2] - 1 };
		me.fan(g[NR - 1], apex, SKULL, st.light, cen[NR - 1]);
		// the visor: a narrow dark slit across the eyes, standing a little proud
		me.prio = 2;
		double vy = ys[3] + (ys[2] - ys[3]) * 0.55;
		int[] vt = new int[5], vb = new int[5];
		for (int q = 0; q < 5; q++) {
			double th = -50 + 25 * q;
			double[] hit = BaseMantle.hit(b.head, vy, hc, th);
			double[] p = hit == null ? ell(0, vy, hc, hw + 2.6, 15, 15, th) : hit;
			double a = Math.toRadians(th);
			double[] o = { Math.sin(a) * 3.2, 0, -Math.cos(a) * 3.2 };
			vt[q] = me.v(add(p, add(o, new double[] { 0, -1.1, 0 })), SKULL);
			vb[q] = me.v(add(p, add(o, new double[] { 0, 1.1, 0 })), SKULL);
		}
		for (int q = 0; q < 4; q++) {
			me.quad(vt[q], vt[q + 1], vb[q + 1], vb[q], st.leatherDark, new double[] { 0, vy, hc });
		}
		// a gold ridge over the crown, brow to nape
		me.prio = 1;
		int seg = 5;
		for (int k = 0; k < seg; k++) {
			double a0 = Math.PI * k / seg, a1 = Math.PI * (k + 1) / seg;
			double rz = (cen[3][2] - (BaseCoat.slice(b.head, ys[3], 0)[2] - 2.6)), ry = (ys[3] - apex[1]);
			double[] p0 = { 0, ys[3] - Math.sin(a0) * ry - 1.0, cen[3][2] - Math.cos(a0) * rz }, p1 = { 0, ys[3] - Math.sin(a1) * ry - 1.0, cen[3][2] - Math.cos(a1) * rz };
			double[] o0 = norm(sub(p0, new double[] { 0, ys[3], cen[3][2] })), o1 = norm(sub(p1, new double[] { 0, ys[3], cen[3][2] }));
			int l0 = me.v(add(p0, new double[] { -0.9, 0, 0 }), SKULL), r0 = me.v(add(p0, new double[] { 0.9, 0, 0 }), SKULL);
			int l1 = me.v(add(p1, new double[] { -0.9, 0, 0 }), SKULL), r1 = me.v(add(p1, new double[] { 0.9, 0, 0 }), SKULL);
			int t0 = me.v(add(p0, scale(o0, 2.2)), SKULL), t1 = me.v(add(p1, scale(o1, 2.2)), SKULL);
			double[] c = { 0, ys[3], cen[3][2] };
			me.quad(l0, l1, t1, t0, st.accent, c);
			me.quad(r0, r1, t1, t0, st.accent, c);
		}
		// a wing at each temple, three feather-plates swept back and up
		for (int s = 0; s < 2; s++) {
			double sg = sx(s);
			double[] root = { sg * (hw + 2.4), ys[3] + 1, hc + 1 };
			for (int k = 0; k < 3; k++) {
				double[] tip = { sg * (hw + 6 + k * 1.5), H - (13 - k * 5) * hs, b.headBack + 9 + k * 3 };
				feather(me, add(root, new double[] { 0, k * 2.2, k * 1.4 }), tip, 3.2 - k * 0.4, new double[] { sg, 0, 0 }, SKULL, k == 0 ? st.light : st.main,
						st.main, st.dark);
			}
		}
		// the sun-disc behind the head, its rays all round
		me.prio = 0;
		double[] dc = { 0, H + (C - H) * 0.42, b.headBack + 8 };
		double R = 10.5 * hs;
		Frame f = new Frame(dc, new double[] { 1, 0, 0 }, new double[] { 0, 1, 0 }, new double[] { 0, 0, 1 });
		double[][] disc = new double[12][];
		for (int k = 0; k < 12; k++) {
			double a = Math.PI * 2 * k / 12;
			disc[k] = new double[] { Math.cos(a) * R, Math.sin(a) * R };
		}
		BaseWraith.plate(me, f.o, f.u, f.v, f.n, disc, 0.7, SKULL, SUN, SUN, -1);
		for (int k = 0; k < 12; k++) {
			double a = Math.PI * 2 * (k + 0.5) / 12, len = (k % 2 == 0 ? 23 : 17) * hs;
			double ca = Math.cos(a), sa = Math.sin(a), w = 2.2 * hs;
			double[][] ray = { { ca * (R - 1) - sa * w, sa * (R - 1) + ca * w }, { ca * len, sa * len }, { ca * (R - 1) + sa * w, sa * (R - 1) - ca * w } };
			BaseWraith.plate(me, add(dc, new double[] { 0, 0, 0.3 }), f.u, f.v, f.n, ray, 0.4, SKULL, k % 2 == 0 ? SUN : st.trim, SUN, -1);
		}
		return me.done();
	}

	/* ====================================================================== */
	/*                               THE CUIRASS                              */
	/* ====================================================================== */

	/** The fluted cuirass, the gorget, the pauldrons, the plated sleeves and the wings. */
	static ModelData cuirass(Style st, Body b) {
		Mesh me = new Mesh();
		ModelData kit = ArmourForge.mergeAll(b.torso, b.legs);
		double neck = b.neckY, waist = b.waistY, h = waist - neck, cz = (b.chestZ + b.backZ) / 2;
		double[] ys = { neck + 2, neck + h * 0.3, neck + h * 0.55, waist - 4, waist + 2 };
		double[] out = { 1.6, 2.6, 2.4, 1.4, 2.4 };
		int n = 12, NR = ys.length;
		int[][] rg = new int[NR][n];
		double[][] cen = new double[NR][];
		for (int r = 0; r < NR; r++) {
			int lab = r <= 2 ? CHEST : BELLY;
			cen[r] = new double[] { 0, ys[r], cz };
			for (int i = 0; i < n; i++) {
				double th = 360.0 * i / n;
				double[] hit = BaseMantle.hit(kit, ys[r], cz, th);
				double rad = hit == null ? 10 : Math.max(b.neckR + 2, Math.hypot(hit[0], hit[2] - cz));
				double fl = (i % 2 == 0 && r >= 1 && r <= 3) ? 1.1 : 0;
				double a = Math.toRadians(th);
				rg[r][i] = me.v(new double[] { Math.sin(a) * (rad + out[r] + fl), ys[r], cz - Math.cos(a) * (rad + out[r] + fl) }, lab);
			}
		}
		for (int r = 0; r + 1 < NR; r++) {
			double[] in = lerp(cen[r], cen[r + 1], 0.5);
			for (int i = 0; i < n; i++) {
				int j = (i + 1) % n;
				int col = r == NR - 2 ? st.trim : i % 2 == 0 ? st.light : st.main;
				me.quad(rg[r][i], rg[r][j], rg[r + 1][j], rg[r + 1][i], col, in);
			}
		}
		// the gorget: a collar standing round the neck
		int[] c0 = BaseMantle.ring(me, 0, neck + 3, b.neckZ, b.neckR + 4.5, b.neckR + 4, b.neckR + 4, 10, 0, CHEST);
		int[] c1 = BaseMantle.ring(me, 0, neck - 4, b.neckZ, b.neckR + 3.5, b.neckR + 3, b.neckR + 3, 10, 0, CHEST);
		me.band(c0, c1, true, st.trim, new double[] { 0, neck, b.neckZ });
		for (int s = 0; s < 2; s++) {
			pauldron(me, st, b, s);
			sleeve(me, st, b, s);
			wing(me, st, b, s);
		}
		return BaseWraith.mark(me.done(), "torso", neck, waist, b.torsoHW);
	}

	/** A pointed pauldron: two lames over the shoulder, a raised rose edge, a point at the top. */
	static void pauldron(Mesh me, Style st, Body b, int s) {
		double sg = sx(s), sc = b.female ? 0.88 : 1;
		double[] sh = b.shoulder[s];
		double[] c = add(sh, new double[] { sg * 1.5, -4 * sc, 0 });
		double[] axis = norm(new double[] { sg * 0.55, -0.83, 0 });
		for (int l = 0; l < 2; l++) {
			double[] cl = add(c, scale(axis, -l * 5.5 * sc));
			double ra = (10.5 + l * 0.8) * sc, rb = (9.5 + l * 0.6) * sc;
			int n = 8;
			int[] lo = new int[n], hi = new int[n];
			for (int i = 0; i < n; i++) {
				double phi = Math.PI * 2 * i / n + Math.PI / n;
				lo[i] = me.v(add(BaseCoat.around(cl, axis, ra, rb, phi), scale(axis, -2.5)), UPPER[s]);
				hi[i] = me.v(add(BaseCoat.around(cl, axis, ra * 0.86, rb * 0.86, phi), scale(axis, 2.5)), UPPER[s]);
			}
			double[] in = add(cl, scale(axis, -6));
			for (int i = 0; i < n; i++) {
				int j = (i + 1) % n;
				me.quad(lo[i], lo[j], hi[j], hi[i], l == 1 ? st.trim : i % 2 == 0 ? st.main : st.light, in);
			}
			if (l == 0) {
				int tip = me.v(add(c, scale(axis, 9 * sc)), UPPER[s]);
				for (int i = 0; i < n; i++) {
					me.tri(hi[i], hi[(i + 1) % n], tip, i % 2 == 0 ? st.light : st.main, in);
				}
			}
		}
	}

	/** Fluted plate down the arm: rerebrace, couter, vambrace. */
	static void sleeve(Mesh me, Style st, Body b, int s) {
		double sc = b.female ? 0.88 : 1;
		double[] sh = b.shoulder[s], el = b.elbow[s], wr = b.wrist[s];
		double[] au = norm(sub(el, sh)), af = norm(sub(wr, el)), ae = norm(add(au, af));
		double[][] c = { add(sh, scale(au, 3)), lerp(sh, el, 0.6), el, lerp(el, wr, 0.45), add(wr, scale(af, 0.5)) };
		double[][] ax = { au, au, ae, af, af };
		double[] r = { 6.0 * sc, 5.6 * sc, 6.2 * sc, 5.2 * sc, 5.8 * sc };
		int[] lab = { UPPER[s], UPPER[s], ELBOW[s], FORE[s], FORE[s] };
		int n = 8;
		int[][] rg = new int[c.length][];
		for (int k = 0; k < c.length; k++) {
			rg[k] = fluted(me, c[k], ax[k], r[k], r[k], k == 2 ? 0 : 0.7, n, 0.2, lab[k]);
		}
		bands(me, rg, c, st.main, st.light);
	}

	/**
	 * A folded wing on the back: a gold bone from the shoulder blade up and out to a point above the shoulder, the
	 * primaries hanging from its top to points past the hips, the secondaries over them, rose-tipped coverts along the bone.
	 */
	static void wing(Mesh me, Style st, Body b, int s) {
		double sg = sx(s), sc = b.female ? 0.92 : 1;
		double back = b.backZ;
		double[] root = { sg * 7, b.shoulder[s][1] + 2, back + 5 };
		double[] wrist = { sg * 28 * sc, b.headTop + 4, back + 14 };
		double tipY = b.hipJointY + 14;
		double[] facing = { sg * 0.4, -0.15, 1 };
		// the bone, gold, from the shoulder blade to the wrist
		ModelData bone = new ModelData();
		bone.skin = CHEST;
		bone.tube(root, wrist, 2.0, 1.4, 4, Math.PI / 4, st.accent, st.accent);
		bone.renderTypes.replaceAll(x -> 1);
		ArmourForge.merge(me.m, bone, null);
		// primaries: three broad plates hanging from the wrist, fanned to points past the hips
		for (int k = 0; k < 3; k++) {
			double[] a = add(lerp(wrist, root, k * 0.14), new double[] { -sg * 1.5, 5, 1.0 + k * 1.2 });
			double[] tip = { sg * (24 - k * 5.5) * sc, tipY - (2 - k) * 7, back + 19 + k * 1.2 };
			feather(me, a, tip, 10.5 * sc, facing, CHEST, st.light, st.main, st.dark);
		}
		// secondaries over them, from the middle of the bone
		for (int k = 0; k < 3; k++) {
			double[] a = add(lerp(wrist, root, 0.42 + k * 0.2), new double[] { 0, 3, 5.5 + k * 0.8 });
			double[] tip = { sg * (15 - k * 2) * sc, tipY - 30 - k * 9, back + 22 + k * 0.6 };
			feather(me, a, tip, 8.5 * sc, facing, CHEST, st.light, st.light, st.dark);
		}
		// coverts along the bone, rose at the tips
		for (int k = 0; k < 3; k++) {
			double[] a = add(lerp(wrist, root, 0.08 + k * 0.3), new double[] { 0, -1, 9 + k * 0.5 });
			double[] tip = add(a, new double[] { -sg * 2.5, 24 * sc, 2.5 });
			feather(me, a, tip, 8 * sc, facing, CHEST, st.light, st.trim, st.dark);
		}
	}

	/* ====================================================================== */
	/*                                THE LEGS                                */
	/* ====================================================================== */

	/** One leg's ring at height y: {cx, cz, rx, rz}. */
	static double[] legRing(Body b, double y, int side, double margin) {
		return BaseShatter.legRing(b, y, side, margin);
	}

	/** Hourglass faulds flaring from the waist over fluted cuisses, pointed knee cops and fluted greaves. */
	static ModelData legs(Style st, Body b) {
		Mesh me = new Mesh();
		int n = 10;
		double kneeY = (b.knee[0][1] + b.knee[1][1]) / 2, ankY = (b.ankle[0][1] + b.ankle[1][1]) / 2;
		for (int s = 0; s < 2; s++) {
			int side = s == 0 ? -1 : 1;
			double[][] rows = { { b.hipJointY - 2, 1.6, THIGH[s] }, { lerp(b.hipJointY, kneeY, 0.55), 1.5, THIGH[s] }, { kneeY - 3, 1.5, THIGH[s] },
					{ kneeY + 6, 1.6, SHIN[s] }, { lerp(kneeY, ankY, 0.55), 1.5, SHIN[s] }, { ankY - 1, 1.6, ANKLE[s] } };
			int[][] idx = new int[rows.length][];
			double[][] cs = new double[rows.length][];
			for (int k = 0; k < rows.length; k++) {
				double[] q = legRing(b, rows[k][0], side, rows[k][1]);
				if (k == 0) {
					q[0] = side * Math.max(Math.abs(q[0]), 8.5);
					q[2] = Math.max(q[2], 9);
				}
				cs[k] = new double[] { q[0], rows[k][0], q[1] };
				int[] r = new int[n];
				for (int j = 0; j < n; j++) {
					double f = j % 2 == 0 && k != 2 && k != 3 ? 0.9 : 0;
					double[] p = ell(q[0], rows[k][0], q[1], q[2] + f, q[3] + f, q[3] + f, 360.0 * j / n);
					p[0] = side * Math.max(side * p[0], 1.0); // never across the middle
					r[j] = me.v(p, (int) rows[k][2]);
				}
				idx[k] = r;
			}
			bands(me, idx, cs, st.main, st.light);
			// the knee cop: a point over the knee, rose-edged
			double[] kc = { lerp(cs[2][0], cs[3][0], 0.5), kneeY + 1, 0 };
			double[] lr = legRing(b, kneeY + 1, side, 2.2);
			kc[2] = lr[1] - lr[3] + 0.5;
			double[] axis = norm(new double[] { side * 0.1, 0.1, -1 });
			int m = 6;
			int[] base = new int[m];
			for (int i = 0; i < m; i++) {
				base[i] = me.v(BaseCoat.around(kc, axis, 5.4, 6.2, Math.PI * 2 * i / m), SHIN[s]);
			}
			int tip = me.v(add(kc, scale(axis, 4.2)), SHIN[s]);
			for (int i = 0; i < m; i++) {
				me.tri(base[i], base[(i + 1) % m], tip, i % 2 == 0 ? st.light : st.trim, add(kc, scale(axis, -4)));
			}
		}
		// the faulds: three lames flaring from the waist, split before and behind, each half on its thigh
		int[] lb = b.legs.bounds();
		double cz = (lb[2] + lb[5]) / 2.0;
		double top = b.legsTop - 1, hem = b.hipJointY + 15;
		int tiers = 3, segs = 5;
		double H = (hem - top) / tiers;
		for (int side = -1; side <= 1; side += 2) {
			int s = side < 0 ? 0 : 1;
			for (int k = 0; k < tiers; k++) {
				double y0 = top + k * H - (k > 0 ? 2.5 : 0), y1 = top + (k + 1) * H;
				double[] ys = { y0, y1 - 2, y1 };
				int[][] row = new int[3][segs + 1];
				for (int q = 0; q < 3; q++) {
					double f = (ys[q] - top) / (hem - top);
					double[] sl = BaseCoat.slice(b.legs, Math.max(b.legsTop + 1, Math.min(ys[q], kneeY)), 0);
					double rx = Math.max(-sl[0], sl[1]) + 2.4 + 9 * f * f + 1.2 * f, rz = (sl[3] - sl[2]) / 2 + 2.4 + 7 * f * f + f;
					double gap = 0.04 + 0.1 * f;
					int lab = k == 0 && q == 0 ? BELLY : THIGH[s];
					for (int j = 0; j <= segs; j++) {
						double phi = side * (gap + (Math.PI - 2 * gap) * j / segs);
						row[q][j] = me.v(BaseCoat.ell(0, ys[q], cz, rx + (q == 1 ? 0.4 : 0), rz + (q == 1 ? 0.4 : 0), phi), lab);
					}
				}
				double[] in = { 0, (y0 + y1) / 2, cz };
				for (int j = 0; j < segs; j++) {
					me.quad(row[0][j], row[0][j + 1], row[1][j + 1], row[1][j], j % 2 == 0 ? st.main : st.light, in);
					me.quad(row[1][j], row[1][j + 1], row[2][j + 1], row[2][j], st.trim, in);
				}
			}
		}
		// the waist band
		int[] w0 = BaseMantle.fitted(me, b.legs, top - 1, cz, 10, 2.8, 8, 0, BELLY), w1 = BaseMantle.fitted(me, b.legs, top + 4, cz, 10, 3.0, 8, 0, BELLY);
		me.band(w0, w1, true, st.accent, new double[] { 0, top + 1.5, cz });
		return me.done();
	}

	/* ====================================================================== */
	/*                        SABATONS, GAUNTLETS, SHIELD                     */
	/* ====================================================================== */

	/** Pointed sabatons: a flared cuff round the ankle, lames over the foot, running out to a point past the toes. */
	static ModelData sabatons(Style st, Body b) {
		Mesh me = new Mesh();
		for (int s = 0; s < 2; s++) {
			double[] an = b.ankle[s];
			double fx = b.heel[s][0], sole = b.soleY;
			// the cuff and shaft
			int[] r0 = BaseMantle.ring(me, an[0], an[1] - 9, an[2] + 0.5, 6.8, 7.4, 7.4, 8, 22.5, ANKLE[s]);
			int[] r1 = BaseMantle.ring(me, an[0], an[1] - 4, an[2] + 0.5, 5.6, 6.4, 6.4, 8, 22.5, ANKLE[s]);
			int[] r2 = BaseMantle.ring(me, an[0], sole - 9, an[2] + 0.5, 5.8, 7.0, 7.0, 8, 22.5, ANKLE[s]);
			me.band(r0, r1, true, st.trim, new double[] { an[0], an[1] - 6, an[2] });
			me.band(r1, r2, true, st.main, new double[] { an[0], an[1], an[2] });
			// the foot: sections along z from the heel to the point
			double[][] ft = { { b.heelZ + 1, sole - 8, 5.6, 1.0 }, { an[2] - 2, sole - 10, 6.4, 1.0 }, { lerp(an[2], b.toeZ, 0.55), sole - 7, 6.2, 0.9 },
					{ b.toeZ + 1, sole - 4.5, 4.4, 0.7 } };
			int[] labs = { ANKLE[s], ANKLE[s], TOES[s], TOES[s] };
			int[][] fr = new int[ft.length][];
			for (int k = 0; k < ft.length; k++) {
				double w = ft[k][2], top = ft[k][1];
				fr[k] = new int[] { me.v(new double[] { fx - w, sole + 0.5, ft[k][0] }, labs[k]), me.v(new double[] { fx + w, sole + 0.5, ft[k][0] }, labs[k]),
						me.v(new double[] { fx + w * 0.9, top + 2.5, ft[k][0] }, labs[k]), me.v(new double[] { fx, top, ft[k][0] }, labs[k]),
						me.v(new double[] { fx - w * 0.9, top + 2.5, ft[k][0] }, labs[k]) };
			}
			for (int k = 0; k + 1 < ft.length; k++) {
				double[] in = { fx, sole - 3, (ft[k][0] + ft[k + 1][0]) / 2 };
				for (int e = 0; e < 5; e++) {
					int e2 = (e + 1) % 5;
					int col = e == 0 ? st.dark : k == 1 ? st.trim : e % 2 == 0 ? st.main : st.light;
					me.quad(fr[k][e], fr[k][e2], fr[k + 1][e2], fr[k + 1][e], col, in);
				}
			}
			int point = me.v(new double[] { fx, sole - 1.5, b.toeZ - 7 }, TOES[s]);
			for (int e = 0; e < 5; e++) {
				me.tri(fr[ft.length - 1][e], fr[ft.length - 1][(e + 1) % 5], point, e == 0 ? st.dark : st.light, new double[] { fx, sole - 3, b.toeZ + 4 });
			}
			int heel = me.v(new double[] { fx, sole - 4, b.heelZ + 2.5 }, ANKLE[s]);
			for (int e = 0; e < 5; e++) {
				me.tri(fr[0][e], fr[0][(e + 1) % 5], heel, st.main, new double[] { fx, sole - 4, b.heelZ - 6 });
			}
		}
		return me.done();
	}

	/** Gauntlets: the hand in pale plate (the hands kit, its own labels), a fluted cuff flaring up the forearm. */
	static ModelData gauntlets(Style st, Body b) {
		ModelData m = BaseWraith.kitCopy(b.hands, st.main);
		for (int i = 0; i < m.faces.size(); i++) {
			int[] f = m.faces.get(i);
			if ((f[0] + f[1] + f[2]) % 3 == 0) {
				m.colors.set(i, st.light);
			}
		}
		Mesh me = new Mesh();
		for (int s = 0; s < 2; s++) {
			double[] w = b.wrist[s], el = b.elbow[s];
			double[] af = norm(sub(w, el));
			double[][] c = { add(w, scale(af, 1.5)), add(w, scale(af, -4)), add(w, scale(af, -10)), add(w, scale(af, -11.5)) };
			double[] r = { 5.4, 6.2, 8.2, 8.4 };
			int[][] rg = new int[c.length][];
			for (int k = 0; k < c.length; k++) {
				rg[k] = fluted(me, c[k], af, r[k], r[k], k == 3 ? 0 : 0.8, 10, 0, FORE[s]);
			}
			bands(me, new int[][] { rg[0], rg[1], rg[2] }, new double[][] { c[0], c[1], c[2] }, st.main, st.light);
			me.band(rg[2], rg[3], true, st.trim, c[2]);
		}
		ArmourForge.merge(m, me.done(), null);
		return m;
	}

	/**
	 * The round sun-disc shield, in the rune kite's place and plane: a pale face, a rose rim, a domed gold sun for a
	 * boss with its rays raised on the face, and rays standing out past the rim all round. All on the left hand 28.
	 */
	static ModelData shield(ArmourForge af, Style st) throws IOException {
		ModelData kite = af.read(ArmourForge.RUNE_KITE_WORN);
		Frame k = ArmourForge.shieldFrame(kite, 61);
		double[] down = norm(sub(new double[] { 0, 1, 0 }, scale(k.n, dot(new double[] { 0, 1, 0 }, k.n))));
		double[] across = norm(ModelData.cross(k.n, down));
		double[] c = add(k.o, scale(k.n, 1.0));
		Mesh me = new Mesh();
		int n = 12, L = 28;
		double R = 21;
		double[][] prof = { { 7.0, 1.4 }, { 18.5, 0.2 }, { 19.5, 1.4 }, { R, 1.6 }, { R + 0.5, 0 }, { R, -2.4 } };
		int[][] ring = new int[prof.length][n];
		for (int q = 0; q < prof.length; q++) {
			for (int j = 0; j < n; j++) {
				double a = Math.PI * 2 * j / n;
				ring[q][j] = me.v(add(c, add(scale(k.n, prof[q][1]), add(scale(down, Math.cos(a) * prof[q][0]), scale(across, Math.sin(a) * prof[q][0])))), L);
			}
		}
		double[] behind = add(c, scale(k.n, -12));
		for (int j = 0; j < n; j++) {
			int jj = (j + 1) % n;
			me.quad(ring[0][j], ring[0][jj], ring[1][jj], ring[1][j], j % 2 == 0 ? st.main : st.light, behind);
			me.quad(ring[1][j], ring[1][jj], ring[2][jj], ring[2][j], st.trimDark, add(c, scale(k.n, -40)));
			me.quad(ring[2][j], ring[2][jj], ring[3][jj], ring[3][j], st.trim, behind);
			me.quad(ring[3][j], ring[3][jj], ring[4][jj], ring[4][j], st.trim, c);
			me.quad(ring[4][j], ring[4][jj], ring[5][jj], ring[5][j], st.trimDark, c);
		}
		int bk = me.v(add(c, scale(k.n, -2.4)), L);
		for (int j = 0; j < n; j++) {
			me.tri(ring[5][j], ring[5][(j + 1) % n], bk, st.dark, scale(k.n, -1));
		}
		// the sun: a gold dome
		double[][] boss = { { 7.4, 1.2 }, { 6.6, 3.4 }, { 4.2, 5.6 } };
		int bn = 8;
		int[][] br = new int[boss.length][bn];
		for (int q = 0; q < boss.length; q++) {
			for (int j = 0; j < bn; j++) {
				double a = Math.PI * 2 * j / bn;
				br[q][j] = me.v(add(c, add(scale(k.n, boss[q][1]), add(scale(down, Math.cos(a) * boss[q][0]), scale(across, Math.sin(a) * boss[q][0])))), L);
			}
		}
		int apex = me.v(add(c, scale(k.n, 6.6)), L);
		for (int q = 0; q + 1 < boss.length; q++) {
			for (int j = 0; j < bn; j++) {
				int jj = (j + 1) % bn;
				me.quad(br[q][j], br[q][jj], br[q + 1][jj], br[q + 1][j], q == 0 ? SUN : SUN_CORE, add(c, scale(k.n, -3)));
			}
		}
		for (int j = 0; j < bn; j++) {
			me.tri(br[2][j], br[2][(j + 1) % bn], apex, SUN_CORE, add(c, scale(k.n, -3)));
		}
		// rays: raised on the face from the sun to the rim, then standing out past it, long and short in turn
		for (int j = 0; j < 12; j++) {
			double a = Math.PI * 2 * (j + 0.5) / 12, ca = Math.cos(a), sa = Math.sin(a);
			double len = j % 2 == 0 ? 33 : 27, w = 2.6;
			double[][] ray = { { 7.6, -w }, { len, 0 }, { 7.6, w } };
			double[][] o = new double[3][];
			for (int q = 0; q < 3; q++) {
				o[q] = new double[] { ray[q][0] * ca - ray[q][1] * sa, ray[q][0] * sa + ray[q][1] * ca };
			}
			BaseWraith.plate(me, add(c, scale(k.n, 1.8)), down, across, k.n, o, 0.6, L, j % 2 == 0 ? SUN : st.trim, SUN_BACK, -1);
		}
		return me.done();
	}
}
