package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ArmourForge.add;
import static com.rs2.tools.mapgen.ArmourForge.len;
import static com.rs2.tools.mapgen.ArmourForge.norm;
import static com.rs2.tools.mapgen.ArmourForge.scale;
import static com.rs2.tools.mapgen.ArmourForge.sub;
import static com.rs2.tools.mapgen.BaseMantle.ANKLE;
import static com.rs2.tools.mapgen.BaseMantle.CALF;
import static com.rs2.tools.mapgen.BaseMantle.CAPE_HEM;
import static com.rs2.tools.mapgen.BaseMantle.CAPE_HIP;
import static com.rs2.tools.mapgen.BaseMantle.CAPE_KNEE;
import static com.rs2.tools.mapgen.BaseMantle.CAPE_THIGH;
import static com.rs2.tools.mapgen.BaseMantle.CAPE_TOP;
import static com.rs2.tools.mapgen.BaseMantle.CAPE_WAIST;
import static com.rs2.tools.mapgen.BaseMantle.CHEST;
import static com.rs2.tools.mapgen.BaseMantle.ELBOW;
import static com.rs2.tools.mapgen.BaseMantle.FOREARM;
import static com.rs2.tools.mapgen.BaseMantle.HAND;
import static com.rs2.tools.mapgen.BaseMantle.HEEL;
import static com.rs2.tools.mapgen.BaseMantle.HIP;
import static com.rs2.tools.mapgen.BaseMantle.KNEE;
import static com.rs2.tools.mapgen.BaseMantle.NECK;
import static com.rs2.tools.mapgen.BaseMantle.PELVIS;
import static com.rs2.tools.mapgen.BaseMantle.SHIN;
import static com.rs2.tools.mapgen.BaseMantle.SHOULDER;
import static com.rs2.tools.mapgen.BaseMantle.SKULL;
import static com.rs2.tools.mapgen.BaseMantle.THIGH;
import static com.rs2.tools.mapgen.BaseMantle.TOES;
import static com.rs2.tools.mapgen.BaseMantle.WAIST;
import static com.rs2.tools.mapgen.BaseMantle.BELLY;
import static com.rs2.tools.mapgen.BaseMantle.ell;
import static com.rs2.tools.mapgen.BaseMantle.lerp;
import static com.rs2.tools.mapgen.BaseMantle.sx;
import static com.rs2.tools.mapgen.ModelData.hsl;

import java.io.IOException;

import com.rs2.tools.mapgen.ArmourSets.Style;
import com.rs2.tools.mapgen.BaseMantle.Body;
import com.rs2.tools.mapgen.BaseMantle.Mesh;

/**
 * The Tidecaller base (A28, MANTLE): a sea-mage dressed as a jellyfish. Built from scratch on the mystic robe's rig
 * (see NewBases), every piece measured from the identity-kit body it is worn on, so the male and female fit alike.
 *
 * <ul>
 * <li>Bell (helm): a tall pale jellyfish bell sitting high over the head, four violet "moon-jelly" rings on its crown
 * and pale canals down its sides, a lighter rim and a frill of rounded scallops below it that frames the face (short
 * over the brow, down to the jaw at the sides and the nape behind). A scallop shell with a pearl at the front, a sprig
 * of coral on the left. Named "bell", so it hides nothing: the face and beard show under it.</li>
 * <li>Robe top: a fitted sea-blue robe; over the shoulders a second, wider bell (a capelet dome) with the same frilled
 * margin; two ruffled oral arms hanging down the chest from under it; and from under its back rim seven long tendrils
 * trailing to the calves. Sleeves fitted to the elbow, then a long fin-like membrane hanging from the forearm, ribbed
 * and edged. Named "top", so the arms kit is hidden (the sleeves are the arms).</li>
 * <li>Robe bottom: a skirt flaring from the hips to mid-calf, its hem a ring of rounded scalloped frills, with a
 * fringe of fine tendrils hanging from the frill almost to the ground.</li>
 * <li>Slippers: soft shoes with a ribbed scallop shell capping each toe and a pearl on the instep.</li>
 * <li>Gloves: webbed hands (four finger rays joined by membrane), a flared scalloped fin-cuff at the wrist and a
 * dorsal fin up the outside of the forearm, a coral bracelet with a pearl.</li>
 * </ul>
 *
 * <p>Labels (the rules are BaseMantle's). A ring round a joint takes the joint's own label, so the pivot the client
 * takes for that joint (the centroid of every vertex with that label) stays on the joint: the robe's neck ring 2, the
 * sleeves' shoulder rings 21 / 25 and elbow rings 17 / 23, the skirt's waist ring 29, its hip ring's sides 40 / 42, its
 * knee ring 34 / 35 and shin ring 31 / 37 (per side: each half of the skirt follows its own leg, like the stock robe
 * bottom), the slippers' heels 47 / 48 and the gloves' palms 27 / 28. Everything else takes a label that moves the
 * same way but is never a pivot: the bell the skull 1; the capelet its nearest kit label made safe (chest 8, upper
 * arms 20 / 26); the fins and cuffs the forearms 19 / 22; the skirt between hip and knee the thighs 43 / 44 and below
 * the shin the calves 82 / 83 (so the hem frill and its fringe swing with the shins and never tear); the slipper
 * shafts the ankles 32 / 38 and the shell toes 45 / 46. The back tendrils hang on the stock capes' chain (10 at the
 * top, 9 at the waist, then 16, 14, 13 and 12 down to the tips) at the stock cape's heights, so the walk and run swing
 * them back clear of the legs like a cape.
 */
final class BaseJellyfish {

	private BaseJellyfish() {
	}

	/** Moon-jelly violet (the rings on the crown, the tendrils), pearl, coral and shell. */
	static final int CLOVER = hsl(52, 3, 78), TENDRIL = hsl(55, 2, 96), PEARL = DraftArmourA.PEARL, CORAL = DraftArmourA.CORAL,
			CORAL_DARK = DraftArmourA.CORAL_DARK, SHELL = DraftArmourA.SHELL, SHELL_DARK = DraftArmourA.SHELL_DARK;

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		Body b = BaseMantle.body(af, female);
		switch (slot) {
		case ArmourSets.HELM:
			return new ModelData[] { bell(st, b), null };
		case ArmourSets.BODY:
			return new ModelData[] { robe(st, b), null };
		case ArmourSets.LEGS:
			return new ModelData[] { skirt(st, b), null };
		case ArmourSets.BOOTS:
			return new ModelData[] { slippers(st, b), null };
		case ArmourSets.GLOVES:
			return new ModelData[] { gloves(st, b), null };
		default:
			return new ModelData[] { donor, donorSecond };
		}
	}

	/** The inventory model for the slot, or null to let ArmourSets build it the usual way from the worn model(s). */
	static ModelData inventory(ArmourForge af, Style st, int slot, ModelData[] worn) throws IOException {
		return null;
	}

	/* ====================================================================== */
	/*                       SHARED HELPERS (also used by                     */
	/*                        BasePelt and BaseTreant)                        */
	/* ====================================================================== */

	static double[] pt(double x, double y, double z) {
		return new double[] { x, y, z };
	}

	/** A triangle facing away from {@code inside}, skipped if it is degenerate once rounded to whole units. */
	static void tri(Mesh me, int a, int b, int c, int col, double[] inside) {
		if (a == b || b == c || a == c) {
			return;
		}
		int[] A = me.m.vertices.get(a), B = me.m.vertices.get(b), C = me.m.vertices.get(c);
		long ux = B[0] - A[0], uy = B[1] - A[1], uz = B[2] - A[2], vx = C[0] - A[0], vy = C[1] - A[1], vz = C[2] - A[2];
		if (uy * vz - uz * vy == 0 && uz * vx - ux * vz == 0 && ux * vy - uy * vx == 0) {
			return;
		}
		me.tri(a, b, c, col, inside);
	}

	static void quad(Mesh me, int a, int b, int c, int d, int col, double[] inside) {
		tri(me, a, b, c, col, inside);
		tri(me, a, c, d, col, inside);
	}

	/** A lining quad: it faces towards {@code toward} (a point inside the garment, e.g. on its axis). */
	static void lining(Mesh me, int a, int b, int c, int d, int col, double[] toward) {
		double[] g = scale(add(add(me.p(a), me.p(b)), add(me.p(c), me.p(d))), 0.25);
		quad(me, a, b, c, d, col, add(g, sub(g, toward)));
	}

	/**
	 * A quad seen from both sides: its front (colour {@code front}) faces along {@code n}, its back ({@code back}, or
	 * none if negative) the other way. Both sides share the vertices.
	 */
	static void sheet(Mesh me, int a, int b, int c, int d, int front, int back, double[] n) {
		double[] g = scale(add(add(me.p(a), me.p(b)), add(me.p(c), me.p(d))), 0.25);
		double[] u = scale(norm(n), 40);
		if (front >= 0) {
			quad(me, a, b, c, d, front, sub(g, u));
		}
		if (back >= 0) {
			quad(me, a, b, c, d, back, add(g, u));
		}
	}

	/**
	 * A two-sided ribbon along {@code pts}: at each point it spans +-{@code half[i]} (a zero half-width makes a point),
	 * its front faces {@code n[i]}; {@code front[i]} colours the segment after point i.
	 */
	static void ribbon(Mesh me, double[][] pts, double[][] half, double[][] n, int[] labels, int[] front, int back) {
		int k = pts.length;
		int[] l = new int[k], r = new int[k];
		for (int i = 0; i < k; i++) {
			if (len(half[i]) < 0.35) {
				l[i] = r[i] = me.v(pts[i], labels[i]);
			} else {
				l[i] = me.v(sub(pts[i], half[i]), labels[i]);
				r[i] = me.v(add(pts[i], half[i]), labels[i]);
			}
		}
		for (int i = 0; i + 1 < k; i++) {
			sheet(me, l[i], r[i], r[i + 1], l[i + 1], front[i], back, add(n[i], n[i + 1]));
		}
	}

	/** Two unit vectors perpendicular to {@code d} (and to each other). */
	static double[][] basis(double[] d) {
		double[] dn = norm(d);
		double[] up = Math.abs(dn[1]) > 0.9 ? pt(1, 0, 0) : pt(0, 1, 0);
		double[] u = norm(ModelData.cross(dn, up));
		return new double[][] { u, ModelData.cross(dn, u) };
	}

	/** An open tapered tube from a (radius ra) to b (radius rb, 0 for a point), {@code sides} sides, faces out. */
	static int[][] limb(Mesh me, double[] a, double[] b, double ra, double rb, int sides, double twist, int la, int lb, int col) {
		double[][] uv = basis(sub(b, a));
		int[][] rg = new int[2][];
		double[][] c = { a, b };
		double[] rr = { ra, rb };
		int[] ll = { la, lb };
		for (int e = 0; e < 2; e++) {
			if (rr[e] <= 0.01) {
				int t = me.v(c[e], ll[e]);
				rg[e] = new int[sides];
				java.util.Arrays.fill(rg[e], t);
				continue;
			}
			rg[e] = new int[sides];
			for (int s = 0; s < sides; s++) {
				double ang = Math.PI * 2 * s / sides + twist;
				rg[e][s] = me.v(add(c[e], add(scale(uv[0], Math.cos(ang) * rr[e]), scale(uv[1], Math.sin(ang) * rr[e]))), ll[e]);
			}
		}
		for (int s = 0; s < sides; s++) {
			int t = (s + 1) % sides;
			double[] in = lerp(a, b, 0.5);
			quad(me, rg[0][s], rg[0][t], rg[1][t], rg[1][s], col, in);
		}
		return rg;
	}

	/** A closed tube along a path of points with radii and labels: rings of {@code sides}, the far end closed to a point if its radius is 0. */
	static void tubePath(Mesh me, double[][] pts, double[] radii, int[] labels, int sides, double twist, int[] cols) {
		int k = pts.length;
		int[][] rg = new int[k][];
		for (int i = 0; i < k; i++) {
			double[] d = sub(pts[Math.min(i + 1, k - 1)], pts[Math.max(i - 1, 0)]);
			double[][] uv = basis(d);
			rg[i] = new int[sides];
			if (radii[i] <= 0.01) {
				java.util.Arrays.fill(rg[i], me.v(pts[i], labels[i]));
				continue;
			}
			for (int s = 0; s < sides; s++) {
				double ang = Math.PI * 2 * s / sides + twist;
				rg[i][s] = me.v(add(pts[i], add(scale(uv[0], Math.cos(ang) * radii[i]), scale(uv[1], Math.sin(ang) * radii[i]))), labels[i]);
			}
		}
		for (int i = 0; i + 1 < k; i++) {
			double[] in = lerp(pts[i], pts[i + 1], 0.5);
			for (int s = 0; s < sides; s++) {
				int t = (s + 1) % sides;
				quad(me, rg[i][s], rg[i][t], rg[i + 1][t], rg[i + 1][s], cols[Math.min(i, cols.length - 1)], in);
			}
		}
		if (radii[0] > 0.01) { // cap the start
			for (int s = 1; s + 1 < sides; s++) {
				tri(me, rg[0][0], rg[0][s], rg[0][s + 1], cols[0], pts[Math.min(1, k - 1)]);
			}
		}
	}

	/** A pearl (or any small gem): an octahedron. */
	static void pearl(Mesh me, double[] c, double r, int label, int col) {
		int[] v = new int[6];
		double[][] d = { { r, 0, 0 }, { -r, 0, 0 }, { 0, r, 0 }, { 0, -r, 0 }, { 0, 0, r }, { 0, 0, -r } };
		for (int i = 0; i < 6; i++) {
			v[i] = me.v(add(c, d[i]), label);
		}
		for (int x = 0; x < 2; x++) {
			for (int y = 2; y < 4; y++) {
				for (int z = 4; z < 6; z++) {
					tri(me, v[x], v[y], v[z], col, c);
				}
			}
		}
	}

	/**
	 * A scallop shell lying on a surface: a fan of ribs from the hinge, {@code up} along its axis, {@code right} across,
	 * {@code out} away from the surface; size the fan's radius. Ribs alternate the two colours.
	 */
	static void shell(Mesh me, double[] hinge, double[] up, double[] right, double[] out, double size, int label, int c1, int c2) {
		int ribs = 7;
		int h = me.v(add(hinge, scale(out, 0.6)), label);
		int[] edge = new int[ribs + 1];
		for (int i = 0; i <= ribs; i++) {
			double a = Math.toRadians(-78 + 156.0 * i / ribs);
			double r = size * (i % 2 == 0 ? 0.9 : 1.0);
			double bulge = 1.4 * Math.cos(a) * size / 8;
			edge[i] = me.v(add(add(hinge, add(scale(up, Math.cos(a) * r), scale(right, Math.sin(a) * r))), scale(out, bulge)), label);
		}
		int c = me.v(add(add(hinge, scale(up, size * 0.45)), scale(out, 1.8 * size / 8)), label);
		double[] in = sub(hinge, scale(out, 6));
		for (int i = 0; i < ribs; i++) {
			tri(me, edge[i], edge[i + 1], c, i % 2 == 0 ? c1 : c2, in);
		}
		tri(me, h, edge[0], c, c2, in);
		tri(me, h, c, edge[ribs], c2, in);
		// the two little wings at the hinge
		for (int sd = -1; sd <= 1; sd += 2) {
			int w0 = me.v(add(hinge, add(scale(right, sd * size * 0.42), scale(up, size * 0.05))), label);
			int w1 = me.v(add(hinge, add(scale(right, sd * size * 0.42), scale(up, size * 0.3))), label);
			tri(me, h, w0, w1, c1, in);
		}
	}

	/** A branching sprig of coral: a tapered branch, two smaller branches from 60% up it, and so on {@code depth} times. */
	static void coral(Mesh me, double[] p, double[] d, double length, double r, int depth, int label, int c1, int c2) {
		double[] e = add(p, scale(norm(d), length));
		limb(me, p, e, r, 0, 4, 0.4, label, label, c1);
		if (depth > 0) {
			double[][] uv = basis(d);
			for (int k = -1; k <= 1; k += 2) {
				double[] nd = norm(add(norm(d), scale(uv[0], k * 0.75)));
				coral(me, lerp(p, e, 0.55), nd, length * 0.62, r * 0.72, depth - 1, label, c2, c1);
			}
		}
	}

	/* ====================================================================== */
	/*                                THE BELL                                */
	/* ====================================================================== */

	/**
	 * The bell: rings round a vertical axis over the head, from a rounded crown out to a wide rim tilted back (just
	 * above the brow in front, over the ears at the sides), a frill of rounded scallops flaring under the rim, and a
	 * fringe of short tentacles hanging from under it at the sides and back. All on the skull label.
	 */
	static ModelData bell(Style st, Body b) {
		Mesh me = new Mesh();
		double T = b.headTop, s = (b.headChin - b.headTop) / 24.0;
		double cz = (b.headFront + b.headBack) / 2 + 2, w = b.headHW + 11.5, d = (b.headBack - b.headFront) / 2 + 9.5;
		int n = 12;
		// the dome: fraction of the radius, height above the head's top
		double[][] dome = { { 0.36, 25 }, { 0.70, 21 }, { 0.92, 13.5 }, { 1.0, 5 } };
		int R = dome.length + 1;
		int[][] g = new int[R][n];
		double[] rimY = new double[n];
		for (int i = 0; i < n; i++) {
			double th = 360.0 * i / n, c = Math.cos(Math.toRadians(th));
			for (int r = 0; r < dome.length; r++) {
				g[r][i] = me.v(ell(0, T - dome[r][1] * s, cz, w * dome[r][0], d * dome[r][0], d * dome[r][0], th), SKULL);
			}
			rimY[i] = T + (0.5 + 9 * (1 - c) / 2) * s;
			g[R - 1][i] = me.v(ell(0, rimY[i], cz, w * 1.02, d * 1.02, d * 1.02, th), SKULL);
		}
		int apex = me.v(pt(0, T - 27 * s, cz), SKULL);
		double[] mid = pt(0, T - 8 * s, cz);
		// four violet moon-jelly rings round the crown (centred at 45, 135, 225, 315 degrees), pale canals down the sides
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			tri(me, apex, g[0][i], g[0][j], st.light, mid);
			for (int r = 0; r + 1 < R; r++) {
				int col = r == 1 && i % 3 == 1 ? CLOVER : r >= 2 && i % 2 == 0 ? st.trim : st.light;
				quad(me, g[r][i], g[r][j], g[r + 1][j], g[r + 1][i], col, mid);
			}
		}
		// the lining under the rim (seen from below and under the front rim)
		int[] in3 = new int[n], inR = new int[n];
		for (int i = 0; i < n; i++) {
			in3[i] = me.v(lerp(pt(0, me.p(g[R - 2][i])[1], cz), me.p(g[R - 2][i]), 0.95), SKULL);
			inR[i] = me.v(lerp(pt(0, rimY[i], cz), me.p(g[R - 1][i]), 0.96), SKULL);
		}
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			lining(me, in3[i], in3[j], inR[j], inR[i], st.trimDark, pt(0, (me.p(in3[i])[1] + rimY[i]) / 2, cz));
		}
		// the frill: scallops flaring under the rim, a little longer at the sides and back
		me.prio = 1;
		int[] notch = new int[n];
		double[] len = new double[n];
		for (int i = 0; i < n; i++) {
			double th = 360.0 * i / n, c = Math.cos(Math.toRadians(th));
			len[i] = (3 + 5 * (1 - c) / 2) * s;
			notch[i] = me.v(ell(0, rimY[i] + len[i] * 0.45, cz, w * 1.02 + 2, d * 1.02 + 2, d * 1.02 + 2, th), SKULL);
		}
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			double th = 360.0 * i / n, step = 360.0 / n;
			double l = (len[i] + len[j]) / 2, y0 = (rimY[i] + rimY[j]) / 2 + l * 0.45;
			int a = me.v(ell(0, y0 + l * 0.55, cz, w * 1.02 + 4, d * 1.02 + 4, d * 1.02 + 4, th + step * 0.3), SKULL);
			int c = me.v(ell(0, y0 + l * 0.55, cz, w * 1.02 + 4, d * 1.02 + 4, d * 1.02 + 4, th + step * 0.7), SKULL);
			double[] ax = pt(0, y0, cz);
			int col = i % 2 == 0 ? st.trim : st.light;
			quad(me, g[R - 1][i], g[R - 1][j], notch[j], notch[i], col, ax);
			quad(me, notch[i], a, c, notch[j], col, ax);
			// the inside of the frill
			double[] outside = add(ax, scale(sub(me.p(notch[i]), ax), 3));
			quad(me, g[R - 1][i], notch[i], notch[j], g[R - 1][j], st.trimDark, outside);
			quad(me, notch[i], notch[j], c, a, st.trimDark, outside);
		}
		// the fringe of tentacles from under the rim, at the sides and back
		double[] ang = { 70, 112, 150, 180, 210, 248, 290 };
		for (int k = 0; k < ang.length; k++) {
			double th = ang[k], c = Math.cos(Math.toRadians(th));
			double y0 = T + (0.5 + 9 * (1 - c) / 2) * s + 1;
			double[] top = ell(0, y0, cz, w * 0.94, d * 0.94, d * 0.94, th);
			double[] rad = norm(pt(top[0], 0, top[2] - cz)), tan = pt(-rad[2], 0, rad[0]);
			double length = (11 + (k % 3) * 3.5) * s;
			int seg = 3;
			double[][] p = new double[seg + 1][], hf = new double[seg + 1][], nn = new double[seg + 1][];
			int[] lab = new int[seg + 1], cl = new int[seg + 1];
			for (int q = 0; q <= seg; q++) {
				double f = (double) q / seg;
				p[q] = add(add(top, scale(rad, 1.5 * f + Math.sin(f * 4 + k) * 1.2 * f)), pt(0, length * f, 0));
				hf[q] = scale(tan, q == seg ? 0 : lerp(1.3, 0.7, f));
				nn[q] = rad;
				lab[q] = SKULL;
				cl[q] = k % 2 == 0 ? TENDRIL : st.trim;
			}
			ribbon(me, p, hf, nn, lab, cl, st.trimDark);
		}
		// the scallop shell on the front of the bell, a pearl at its hinge; a sprig of coral on the left
		me.prio = 2;
		double[] hinge = pt(0, T - 3 * s, cz - d * 1.0 - 0.6);
		shell(me, hinge, norm(pt(0, -1, 0.32)), pt(1, 0, 0), norm(pt(0, -0.3, -1)), 7.5 * s, SKULL, SHELL, SHELL_DARK);
		pearl(me, add(hinge, pt(0, 0.6, -1.6)), 1.7, SKULL, PEARL);
		double[] cp = ell(0, T - 6 * s, cz, w * 0.97, d * 0.97, d * 0.97, 108);
		coral(me, cp, pt(0.6, -1, 0.35), 8 * s, 1.5, 1, SKULL, CORAL, CORAL_DARK);
		me.prio = 0;
		return me.done();
	}

	/* ====================================================================== */
	/*                               THE ROBE TOP                             */
	/* ====================================================================== */

	static ModelData robe(Style st, Body b) {
		Mesh me = new Mesh();
		torso(me, st, b);
		for (int s = 0; s < 2; s++) {
			sleeve(me, st, b, s);
		}
		capelet(me, st, b);
		tendrils(me, st, b);
		return me.done();
	}

	/** The fitted robe from the neck to below the waist, and two ruffled oral arms down the front. */
	static void torso(Mesh me, Style st, Body b) {
		ModelData kit = ArmourForge.mergeAll(b.torso, b.legs);
		double neck = b.neckY, waist = b.waistY, h = waist - neck;
		double[] ys = { neck + 1.5, neck + h * 0.3, neck + h * 0.65, waist, waist + 10 };
		int n = 10;
		int[][] rg = new int[ys.length][];
		double cz = (b.chestZ + b.backZ) / 2;
		for (int r = 0; r < ys.length; r++) {
			int lab = r == 0 ? NECK : r == 1 ? CHEST : BELLY;
			rg[r] = BaseMantle.fitted(me, kit, ys[r], cz, n, r == 0 ? 1.4 : 1.6, b.neckR, r == 4 ? 2.5 : 0, lab);
		}
		for (int r = 0; r + 1 < ys.length; r++) {
			me.band(rg[r], rg[r + 1], true, r == ys.length - 2 ? st.dark : st.main, pt(0, (ys[r] + ys[r + 1]) / 2, cz));
		}
		// the oral arms: two ruffled ribbons from under the capelet's front rim down to the waist
		me.prio = 2;
		double top = b.shoulderTop + 10, bot = waist + 6;
		for (int k = -1; k <= 1; k += 2) {
			int seg = 6;
			double[][] p = new double[seg + 1][], hf = new double[seg + 1][], nn = new double[seg + 1][];
			int[] lab = new int[seg + 1], col = new int[seg + 1];
			for (int i = 0; i <= seg; i++) {
				double t = (double) i / seg, y = lerp(top, bot, t);
				double[] f = ArmourForge.surface(kit, pt(k * 3.2, y, -100), pt(0, 0, 1));
				double z = (f == null ? b.chestZ : f[2]) - 1.6;
				double wob = (i % 2 == 0 ? 1 : -1) * 1.3;
				p[i] = pt(k * (3.0 + t * 1.5) + wob, y, z - Math.abs(wob) * 0.4);
				hf[i] = pt((2.6 - t * 0.9) * (i % 2 == 0 ? 1 : 0.55), 0, 0);
				nn[i] = pt(0, 0, -1);
				lab[i] = y < neck + h * 0.45 ? CHEST : BELLY;
				col[i] = i % 2 == 0 ? TENDRIL : st.trim;
			}
			ribbon(me, p, hf, nn, lab, col, -1);
		}
		me.prio = 0;
	}

	/**
	 * A sleeve: fitted from the shoulder ring (21 / 25) past the elbow ring (17 / 23) to a cuff at the wrist, and a fin:
	 * a ribbed membrane hanging from the back of the forearm, trailing low behind the wrist (on the forearm label).
	 */
	static void sleeve(Mesh me, Style st, Body b, int s) {
		double sg = sx(s), sc = b.female ? 0.86 : 1;
		int n = 8;
		double[] sh = b.shoulder[s], el = b.elbow[s], wr = b.wrist[s];
		double[][] c = { sh, lerp(sh, el, 0.5), el, lerp(el, wr, 0.5), add(wr, pt(0, -1.5, 0)) };
		double[] r = { Math.max(b.shoulderR[s][0], b.shoulderR[s][1]) + 1.6, 7.0 * sc, Math.max(b.elbowR[s][0] + 2.2, 5.6 * sc), 6.2 * sc, 7.4 * sc };
		int[] labs = { SHOULDER[s], s == 0 ? 20 : 26, ELBOW[s], FOREARM[s], FOREARM[s] };
		int[][] rg = new int[c.length][];
		for (int k = 0; k < c.length; k++) {
			rg[k] = BaseMantle.ring(me, c[k][0], c[k][1], c[k][2], r[k], r[k], r[k], n, 22.5, labs[k]);
		}
		for (int k = 0; k + 1 < c.length; k++) {
			me.band(rg[k], rg[k + 1], true, k == 3 ? st.dark : st.main, lerp(c[k], c[k + 1], 0.5));
		}
		// the cuff's lining
		int[] lin = new int[n];
		for (int i = 0; i < n; i++) {
			lin[i] = me.v(lerp(c[4], me.p(rg[4][i]), 0.8), FOREARM[s]);
		}
		me.bandIn(rg[4], lin, true, st.dark, add(c[4], pt(0, 30, 0)));
		// the fin: attached along the outside of the forearm, spreading out and down like a wing, facing forward
		me.prio = 1;
		double back = 0.3, out = 0.95;
		int ribs = 4;
		int[] att = new int[ribs + 1], free = new int[ribs + 1];
		for (int i = 0; i <= ribs; i++) {
			double t = (double) i / ribs;
			double[] a = lerp(lerp(el, wr, 0.08), add(wr, pt(0, -1, 0)), t);
			double rr = lerp(5.6, 7.0, t) * sc;
			double[] at = add(a, pt(sg * out * rr, 0, back * rr));
			att[i] = me.v(at, FOREARM[s]);
			double drop = (4 + 17 * Math.pow(t, 1.15)) * sc, spread = (4 + 7 * Math.sin(Math.PI * (0.25 + 0.6 * t))) * sc;
			free[i] = me.v(add(at, pt(sg * spread, drop, 2.5 * sc)), FOREARM[s]);
		}
		for (int i = 0; i < ribs; i++) {
			double[] nrm = pt(sg * 0.15, 0, -1);
			sheet(me, att[i], att[i + 1], free[i + 1], free[i], i % 2 == 0 ? st.light : st.trim, st.trimDark, nrm);
		}
		// the fin's tip: a last scallop below the wrist
		int tip = me.v(add(me.p(free[ribs]), pt(-sg * 3 * sc, 6 * sc, 0)), FOREARM[s]);
		double[] nrm = pt(sg * 0.15, 0, -1);
		double[] g = scale(add(add(me.p(att[ribs]), me.p(free[ribs])), me.p(tip)), 1 / 3.0);
		tri(me, att[ribs], free[ribs], tip, st.trim, sub(g, scale(norm(nrm), 40)));
		tri(me, att[ribs], free[ribs], tip, st.trimDark, add(g, scale(norm(nrm), 40)));
		me.prio = 0;
	}

	/**
	 * The capelet: a second bell over the shoulders, domed from the collar out past the shoulders to a rim (higher in
	 * front), a frill of rounded scallops under the rim, lined underneath. Each vertex takes its nearest kit label made
	 * safe, so it rides the chest and, at the sides, the upper arms.
	 */
	static void capelet(Mesh me, Style st, Body b) {
		ModelData ref = ArmourForge.mergeAll(b.torso, b.arms);
		double top = b.shoulderTop, cz = (b.chestZ + b.backZ) / 2 + 0.5, sc = b.female ? 0.88 : 1;
		double front = Math.max(-b.chestZ, 12), back = b.backZ;
		int n = 16;
		// y offset from the shoulder top, half-width, half-depth in front, behind
		double[][] rings = { { -7, b.neckR + 4, b.neckR + 4, b.neckR + 4 }, { -4.5, b.torsoHW + 3, front + 2.5, back + 3 },
				{ 3 * sc, b.armOuter + 3, front + 6, back + 6.5 } };
		int R = rings.length + 1;
		int[][] g = new int[R][n];
		double[] rimY = new double[n];
		for (int i = 0; i < n; i++) {
			double th = 360.0 * i / n + 11.25, c = Math.cos(Math.toRadians(th));
			for (int r = 0; r < rings.length; r++) {
				double[] p = ell(0, top + rings[r][0], cz, rings[r][1], rings[r][2], rings[r][3], th);
				g[r][i] = me.v(p, r == 0 ? CHEST : BaseMantle.nearest(ref, p));
			}
			rimY[i] = top + (14 + 10 * (1 - c) / 2) * sc;
			double[] p = ell(0, rimY[i], cz, b.armOuter + 7.5 * sc, front + 8.5, back + 9.5, th);
			g[R - 1][i] = me.v(p, BaseMantle.nearest(ref, p));
		}
		double[] mid = pt(0, top + 10, cz);
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			for (int r = 0; r + 1 < R; r++) {
				int col = r == 0 ? st.trim : (i % 2 == 0 && r == R - 2) ? st.trim : st.light;
				if (r == 1 && (i % 4 == 1 || i % 4 == 2)) {
					col = CLOVER;
				}
				quad(me, g[r][i], g[r][j], g[r + 1][j], g[r + 1][i], col, mid);
			}
		}
		// the frill, and its lining and the dome's, seen from below
		me.prio = 1;
		int[] notch = new int[n];
		double[] len = new double[n];
		for (int i = 0; i < n; i++) {
			double th = 360.0 * i / n + 11.25, c = Math.cos(Math.toRadians(th));
			len[i] = (7.5 + 3 * (1 - c) / 2) * sc;
			double[] p = ell(0, rimY[i] + len[i] * 0.45, cz, b.armOuter + 8.5 * sc, front + 9.5, back + 10.5, th);
			notch[i] = me.v(p, BaseMantle.nearest(ref, p));
		}
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			double th = 360.0 * i / n + 11.25, step = 360.0 / n;
			double l = (len[i] + len[j]) / 2, y0 = (rimY[i] + rimY[j]) / 2 + l * 0.45;
			double[] pa = ell(0, y0 + l * 0.55, cz, b.armOuter + 9.6 * sc, front + 10.6, back + 11.6, th + step * 0.3);
			double[] pc = ell(0, y0 + l * 0.55, cz, b.armOuter + 9.6 * sc, front + 10.6, back + 11.6, th + step * 0.7);
			int a = me.v(pa, BaseMantle.nearest(ref, pa)), c = me.v(pc, BaseMantle.nearest(ref, pc));
			double[] ax = pt(0, y0, cz);
			int col = i % 2 == 0 ? st.trim : st.light;
			quad(me, g[R - 1][i], g[R - 1][j], notch[j], notch[i], col, ax);
			quad(me, notch[i], a, c, notch[j], col, ax);
			double[] outside = add(ax, scale(sub(me.p(notch[i]), ax), 3));
			quad(me, g[R - 1][i], notch[i], notch[j], g[R - 1][j], st.trimDark, outside);
			quad(me, notch[i], notch[j], c, a, st.trimDark, outside);
			// under the dome, from the rim in to the shoulders
			lining(me, g[R - 2][i], g[R - 1][i], g[R - 1][j], g[R - 2][j], st.trimDark, pt(0, rimY[i] + 15, cz));
		}
		me.prio = 0;
	}

	/**
	 * Seven tendrils from under the capelet's back rim down to the calves: two-sided ribbons, wavy, on the cape chain
	 * (one label per row, the rows at the stock cape's heights), spread a little wider as they fall.
	 */
	static void tendrils(Mesh me, Style st, Body b) {
		double hips = b.hipsBackZ, sc = b.female ? 0.88 : 1;
		double yTop = b.shoulderTop + 17 * sc, yEnd = b.shin[0][1] + 10;
		double[] ys = { yTop, b.capeY[1], (b.capeY[1] + b.capeY[2]) / 2, b.capeY[2], b.capeY[3], Math.max(b.capeY[4], b.capeY[3] + 6), yEnd };
		int[] labs = { CAPE_TOP, CAPE_WAIST, CAPE_HIP, CAPE_THIGH, CAPE_KNEE, CAPE_HEM, CAPE_HEM };
		double[] zs = { b.backZ + 2, hips + 4, hips + 6, hips + 8, hips + 10, hips + 11, hips + 12 };
		int k = 7;
		for (int t = 0; t < k; t++) {
			double u = (t - (k - 1) / 2.0) / ((k - 1) / 2.0); // -1..1 across the back
			double x0 = u * b.torsoHW * 0.82, end = yEnd - (t % 3) * 6 + (t == 3 ? 4 : 0);
			int m = ys.length;
			double[][] p = new double[m][], hf = new double[m][], nn = new double[m][];
			int[] col = new int[m];
			for (int i = 0; i < m; i++) {
				double y = i == m - 1 ? end : ys[i];
				double f = (double) i / (m - 1);
				double wav = Math.sin(f * Math.PI * 2.2 + t * 1.7) * 2.2 * f;
				double x = x0 * (1 + 0.35 * f) + wav;
				double z = zs[i] - Math.abs(u) * 2.5 * (1 - f * 0.5);
				p[i] = pt(x, y, z);
				double w = i == m - 1 ? 0 : lerp(2.4, 1.0, f) * sc;
				double tw = Math.sin(f * Math.PI * 1.6 + t) * 0.5;
				hf[i] = pt(w * Math.cos(tw), 0, w * Math.sin(tw));
				nn[i] = pt(-Math.sin(tw), 0, Math.cos(tw));
				col[i] = t % 2 == 0 ? (i % 2 == 0 ? st.trim : st.light) : (i % 2 == 0 ? TENDRIL : CLOVER);
			}
			ribbon(me, p, hf, nn, labs, col, st.trimDark);
		}
	}

	/* ====================================================================== */
	/*                               THE SKIRT                                */
	/* ====================================================================== */

	/**
	 * The robe bottom: a skirt from the waist (29) out over the hips (40 / 42 at the sides) and down each leg's half
	 * (thigh 43 / 44, knee 34 / 35, shin 31 / 37, calf 82 / 83) to a hem of rounded scallops at mid-calf, lined, with a
	 * fringe of fine tendrils hanging from the scallops almost to the ground.
	 */
	static ModelData skirt(Style st, Body b) {
		Mesh me = new Mesh();
		int[] lb = b.legs.bounds();
		double cz = (lb[2] + lb[5]) / 2.0 + 1, hw = b.legsHW, sc = b.female ? 0.92 : 1;
		double knee = (b.knee[0][1] + b.knee[1][1]) / 2, shin = (b.shin[0][1] + b.shin[1][1]) / 2, ankle = (b.ankle[0][1] + b.ankle[1][1]) / 2;
		double hem = lerp(shin, ankle, 0.3);
		// y, half-width, half-depth front, behind
		double[][] rings = { { b.legsTop + 1, hw + 1.2, 11.5, 13 }, { b.hipJointY, hw + 3.5, 13.5, 15 }, { lerp(b.hipJointY, knee, 0.5), hw + 6.5, 15.5, 17 },
				{ knee, hw + 9 * sc, 17.5, 19 }, { shin, hw + 10.5 * sc, 18.5, 20 }, { hem, hw + 12 * sc, 19.5, 21 } };
		int n = 12, R = rings.length;
		int[][] g = new int[R][n];
		for (int r = 0; r < R; r++) {
			for (int i = 0; i < n; i++) {
				double th = 360.0 * i / n + 15;
				double[] p = ell(0, rings[r][0], cz, rings[r][1], rings[r][2], rings[r][3], th);
				int s = p[0] < 0 ? 0 : 1;
				int lab;
				switch (r) {
				case 0:
					lab = WAIST;
					break;
				case 1:
					lab = Math.abs(Math.sin(Math.toRadians(th))) > 0.6 ? HIP[s] : PELVIS;
					break;
				case 2:
					lab = THIGH[s];
					break;
				case 3:
					lab = KNEE[s];
					break;
				case 4:
					lab = SHIN[s];
					break;
				default:
					lab = CALF[s];
				}
				g[r][i] = me.v(p, lab);
			}
		}
		for (int r = 0; r + 1 < R; r++) {
			double[] ax = pt(0, (rings[r][0] + rings[r + 1][0]) / 2, cz);
			for (int i = 0; i < n; i++) {
				int j = (i + 1) % n;
				int col = r == 0 ? st.dark : r >= 3 && i % 2 == 0 ? st.dark : st.main;
				quad(me, g[r][i], g[r][j], g[r + 1][j], g[r + 1][i], col, ax);
			}
		}
		// the lining from the shin down, seen under the hem
		int[] l4 = new int[n], l5 = new int[n];
		for (int i = 0; i < n; i++) {
			l4[i] = me.v(lerp(pt(0, rings[4][0], cz), me.p(g[4][i]), 0.95), me.m.vertexSkins.get(g[5][i]));
			l5[i] = me.v(lerp(pt(0, rings[5][0], cz), me.p(g[5][i]), 0.96), me.m.vertexSkins.get(g[5][i]));
		}
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			lining(me, l4[i], l5[i], l5[j], l4[j], st.dark, pt(0, (rings[4][0] + rings[5][0]) / 2, cz));
		}
		// the hem's frill: rounded scallops flaring out, each with a fringe of tendrils
		me.prio = 1;
		int[] notch = new int[n];
		double L = 9 * sc;
		for (int i = 0; i < n; i++) {
			double th = 360.0 * i / n + 15;
			double[] p = ell(0, hem + L * 0.4, cz, rings[5][1] + 2.5, rings[5][2] + 2.5, rings[5][3] + 2.5, th);
			notch[i] = me.v(p, CALF[p[0] < 0 ? 0 : 1]);
		}
		double ground = b.soleY - 6;
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			double th = 360.0 * i / n + 15, step = 360.0 / n;
			double[] pa = ell(0, hem + L, cz, rings[5][1] + 4.5, rings[5][2] + 4.5, rings[5][3] + 4.5, th + step * 0.28);
			double[] pc = ell(0, hem + L, cz, rings[5][1] + 4.5, rings[5][2] + 4.5, rings[5][3] + 4.5, th + step * 0.72);
			int s = me.p(notch[i])[0] + me.p(notch[j])[0] < 0 ? 0 : 1;
			int a = me.v(pa, CALF[s]), c = me.v(pc, CALF[s]);
			double[] ax = pt(0, hem + L * 0.5, cz);
			int col = i % 2 == 0 ? st.trim : st.light;
			quad(me, g[R - 1][i], g[R - 1][j], notch[j], notch[i], col, ax);
			quad(me, notch[i], a, c, notch[j], col, ax);
			double[] outside = add(ax, scale(sub(me.p(notch[i]), ax), 3));
			quad(me, g[R - 1][i], notch[i], notch[j], g[R - 1][j], st.trimDark, outside);
			quad(me, notch[i], notch[j], c, a, st.trimDark, outside);
			// a tendril from the middle of the scallop, nearly to the ground
			double[] top = lerp(pa, pc, 0.5);
			top = lerp(ax, top, 0.93);
			double end = ground - (i % 3) * 4 * sc;
			double[] rad = norm(pt(top[0], 0, top[2] - cz));
			double[] tan = pt(-rad[2], 0, rad[0]);
			int seg = 3;
			double[][] p = new double[seg + 1][], hf = new double[seg + 1][], nn = new double[seg + 1][];
			int[] lab = new int[seg + 1], cl = new int[seg + 1];
			for (int k = 0; k <= seg; k++) {
				double f = (double) k / seg;
				double wav = Math.sin(f * Math.PI * 1.5 + i) * 1.8 * f;
				p[k] = add(add(top, scale(rad, wav + f * 1.5)), pt(0, (end - top[1]) * f, 0));
				hf[k] = scale(tan, k == seg ? 0 : lerp(1.6, 0.8, f) * sc);
				nn[k] = rad;
				lab[k] = CALF[s];
				cl[k] = i % 2 == 0 ? TENDRIL : st.trim;
			}
			ribbon(me, p, hf, nn, lab, cl, st.trimDark);
		}
		me.prio = 0;
		return me.done();
	}

	/* ====================================================================== */
	/*                            SLIPPERS, GLOVES                            */
	/* ====================================================================== */

	/** Soft slippers: a low shaft round the ankle (32 / 38), the foot (heel 47 / 48, toes 45 / 46), a scallop shell over each toe. */
	static ModelData slippers(Style st, Body b) {
		Mesh me = new Mesh();
		for (int s = 0; s < 2; s++) {
			double[] an = b.ankle[s];
			double fx = an[0], fz = an[2] + 0.5, ay = an[1], sole = b.soleY, toe = b.toeZ;
			int n = 6;
			double[][] sh = { { ay + 3, 5.4, 6.2 }, { sole - 7, 5.8, 7.0 } };
			int[][] sr = new int[sh.length][];
			for (int k = 0; k < sh.length; k++) {
				sr[k] = BaseMantle.ring(me, fx, sh[k][0], fz, sh[k][1], sh[k][2], sh[k][2], n, 30, ANKLE[s]);
			}
			me.band(sr[0], sr[1], true, st.main, pt(fx, (sh[0][0] + sh[1][0]) / 2, fz));
			int[] cuff = BaseMantle.ring(me, fx, ay + 1.5, fz, 6.4, 7.2, 7.2, n, 30, ANKLE[s]);
			me.band(cuff, sr[0], true, st.trim, pt(fx, ay + 2, fz));
			// the foot along z: heel, middle (the heel label: the toes' pivot), forefoot, toe
			double[][] ft = { { b.heelZ - 1, sole - 6.5, 5.2, 6 }, { b.heel[s][2], sole - 6, 6.0, 6.2 }, { lerp(b.heel[s][2], toe, 0.55), sole - 4.6, 5.8, 4.6 },
					{ toe - 0.5, sole - 3, 4.6, 3.0 } };
			int[] labs = { ANKLE[s], HEEL[s], TOES[s], TOES[s] };
			int[][] fr = new int[ft.length][n];
			for (int k = 0; k < ft.length; k++) {
				for (int i = 0; i < n; i++) {
					double a = Math.PI * 2 * (i + 0.5) / n;
					fr[k][i] = me.v(pt(fx + Math.cos(a) * ft[k][2], ft[k][1] + Math.sin(a) * ft[k][3], ft[k][0]), labs[k]);
				}
			}
			for (int k = 0; k + 1 < ft.length; k++) {
				me.band(fr[k], fr[k + 1], true, k == 0 ? st.dark : st.main, pt(fx, (ft[k][1] + ft[k + 1][1]) / 2, (ft[k][0] + ft[k + 1][0]) / 2));
			}
			me.fan(fr[0], pt(fx, ft[0][1], ft[0][0] + 1.2), ANKLE[s], st.dark, pt(fx, ft[0][1], ft[0][0] - 5));
			me.fan(fr[ft.length - 1], pt(fx, sole - 4, toe - 2.5), TOES[s], st.dark, pt(fx, ft[3][1], ft[3][0] + 5));
			// the shell over the toe: its hinge on the instep, fanning forward
			me.prio = 2;
			double[] hinge = pt(fx, sole - 10.6, lerp(b.heel[s][2], toe, 0.42));
			shell(me, hinge, norm(pt(0, 0.42, -1)), pt(1, 0, 0), norm(pt(0, -1, -0.42)), 9, TOES[s], SHELL, SHELL_DARK);
			pearl(me, add(hinge, pt(0, -1.6, 0.6)), 1.5, TOES[s], PEARL);
			me.prio = 0;
		}
		return me.done();
	}

	/**
	 * Webbed gloves: a flared fin-cuff at the wrist, a dorsal fin up the outside of the forearm, a coral bracelet, the
	 * palm (27 / 28) and four finger rays joined by webbing.
	 */
	static ModelData gloves(Style st, Body b) {
		Mesh me = new Mesh();
		for (int s = 0; s < 2; s++) {
			double sg = sx(s);
			double[] el = b.elbow[s], fo = b.fore[s], wr = b.wrist[s], hd = b.hand[s];
			double bottom = b.handBottom, hrx = b.handR[s][0], hrz = b.handR[s][1], sc = b.female ? 0.85 : 1;
			int n = 6;
			// forearm and hand: rings down to the knuckles
			double[][] rings = { { fo[0], fo[1] + 1, fo[2], 6.0 * sc, 6.0 * sc }, { wr[0], wr[1], wr[2], 4.8 * sc, 5.4 * sc },
					{ hd[0] - sg * 0.4, bottom - 11, hd[2], hrx + 0.2, hrz + 0.8 }, { hd[0] + sg * 0.2, bottom - 6, hd[2], hrx - 0.4, hrz + 1.2 } };
			int[] labs = { FOREARM[s], FOREARM[s], HAND[s], HAND[s] };
			int[][] rg = new int[rings.length][];
			for (int k = 0; k < rings.length; k++) {
				rg[k] = BaseMantle.ring(me, rings[k][0], rings[k][1], rings[k][2], rings[k][3], rings[k][4], rings[k][4], n, 30, labs[k]);
			}
			for (int k = 0; k + 1 < rings.length; k++) {
				me.band(rg[k], rg[k + 1], true, k == 0 ? st.main : st.light, pt(rings[k][0], (rings[k][1] + rings[k + 1][1]) / 2, rings[k][2]));
			}
			// the webbed fingers: a flat paddle below the knuckles in the hand's plane (y-z), four rays
			double kx = hd[0] + sg * 0.2, ky = bottom - 6, kz = hd[2];
			double[][] tips = { { -5.2, 3.8 }, { -1.8, 5.4 }, { 1.6, 5.2 }, { 4.6, 3.6 } };
			int[] web = new int[9];
			for (int i = 0; i < 4; i++) {
				web[i * 2] = me.v(pt(kx, ky + tips[i][1] * sc + 1.5, kz + tips[i][0] * sc), HAND[s]);
				if (i < 3) {
					web[i * 2 + 1] = me.v(pt(kx, ky + (tips[i][1] + tips[i + 1][1]) / 2 * sc - 0.6, kz + (tips[i][0] + tips[i + 1][0]) / 2 * sc), HAND[s]);
				}
			}
			int base0 = me.v(pt(kx, ky - 1, kz - 4.4 * sc), HAND[s]), base1 = me.v(pt(kx, ky - 1, kz + 4.4 * sc), HAND[s]);
			int cen = me.v(pt(kx, ky - 1.5, kz), HAND[s]);
			int[] fanPts = { base0, web[0], web[1], web[2], web[3], web[4], web[5], web[6], base1 };
			for (int i = 0; i + 1 < fanPts.length; i++) {
				double[] g = scale(add(add(me.p(cen), me.p(fanPts[i])), me.p(fanPts[i + 1])), 1 / 3.0);
				int col = (i % 2 == 1) ? st.main : st.light;
				tri(me, cen, fanPts[i], fanPts[i + 1], col, sub(g, pt(sg * 30, 0, 0)));
				tri(me, cen, fanPts[i], fanPts[i + 1], col, add(g, pt(sg * 30, 0, 0)));
			}
			// the thumb
			limb(me, pt(hd[0] - sg * 1.5, bottom - 10, hd[2] - hrz + 1), pt(hd[0] - sg * 2.6, bottom - 4, hd[2] - hrz - 2.2), 1.7, 0, 4, Math.PI / 4, HAND[s], HAND[s],
					st.light);
			// the fin-cuff: a flared scalloped bell opening up the forearm from the wrist
			me.prio = 1;
			int cn = 6;
			int[] c0 = new int[cn], c1 = new int[cn];
			double cy = wr[1] + 1;
			for (int i = 0; i < cn; i++) {
				double th = 360.0 * i / cn + 30;
				c0[i] = me.v(ell(wr[0], cy, wr[2], 5.6 * sc, 6.2 * sc, 6.2 * sc, th), FOREARM[s]);
				c1[i] = me.v(ell(wr[0], cy - (i % 2 == 0 ? 7.5 : 5) * sc, wr[2], 9.2 * sc, 9.6 * sc, 9.6 * sc, th), FOREARM[s]);
			}
			double[] ax = pt(wr[0], cy - 3, wr[2]);
			for (int i = 0; i < cn; i++) {
				int j = (i + 1) % cn;
				quad(me, c0[i], c0[j], c1[j], c1[i], i % 2 == 0 ? st.trim : st.light, ax);
				quad(me, c0[i], c1[i], c1[j], c0[j], st.trimDark, add(ax, scale(sub(me.p(c1[i]), ax), 3)));
			}
			// the dorsal fin up the outside of the forearm
			double[] f0 = add(wr, pt(sg * 5 * sc, -3, 0.5)), f1 = add(lerp(wr, el, 0.62), pt(sg * 5.2 * sc, 0, 1));
			int a0 = me.v(f0, FOREARM[s]), a1 = me.v(f1, FOREARM[s]);
			int t0 = me.v(add(f0, pt(sg * 6 * sc, -4, 1.5)), FOREARM[s]), t1 = me.v(add(lerp(f0, f1, 0.6), pt(sg * 4 * sc, -6, 2)), FOREARM[s]);
			sheet(me, a0, a1, t1, t0, st.trim, st.trimDark, pt(0, 0, -1));
			// a coral bracelet and a pearl
			me.prio = 2;
			coral(me, add(wr, pt(-sg * 3 * sc, -2.5, -5 * sc)), pt(-sg * 0.4, -1, -0.5), 4.5 * sc, 1.2, 0, FOREARM[s], CORAL, CORAL_DARK);
			pearl(me, add(wr, pt(0, -0.5, -6.6 * sc)), 1.4, FOREARM[s], PEARL);
			me.prio = 0;
		}
		return me.done();
	}
}
