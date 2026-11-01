package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ArmourForge.add;
import static com.rs2.tools.mapgen.ArmourForge.dot;
import static com.rs2.tools.mapgen.ArmourForge.norm;
import static com.rs2.tools.mapgen.ArmourForge.scale;
import static com.rs2.tools.mapgen.ArmourForge.sub;
import static com.rs2.tools.mapgen.BaseCoat.mirror;
import static com.rs2.tools.mapgen.BaseCoat.pt;
import static com.rs2.tools.mapgen.ModelData.hsl;

import java.io.IOException;

import com.rs2.tools.mapgen.ArmourForge.Frame;
import com.rs2.tools.mapgen.ArmourSets.Style;
import com.rs2.tools.mapgen.BaseCoat.Mesh;
import com.rs2.tools.mapgen.BaseCoat.Rig;

/**
 * The Gatehold sentinel base: the guard on Gatehold's wall, built like the gate-tower he keeps. Built from scratch for
 * the Gatehold sentinel (A32) draft set on the rune plate's rig; see NewBases. Everything is blocks, flat-shaded: bright
 * steel, Gatehold blue, a little gold.
 *
 * <ul>
 * <li>Helm (a "full helm": it hides the head and beard): a flat-topped octagonal great helm crowned with battlements,
 * its visor a portcullis (bars over a dark slot, spiked at the foot), a gold socket on top and a tall plume of blue
 * feathers rising and sweeping back.</li>
 * <li>Body (a "platebody": it draws its own arms): a squared octagonal breastplate with a standing gorget and a belt; on
 * each shoulder a square tower pauldron (a blue plinth, a steel shaft with an arrow slit, four merlons on top); a
 * Gatehold-blue tabard front and back, the gold gate-tower crest on the breast, its skirt split into two narrow halves
 * that hang between the legs' portcullises; square-tubed steel arms with elbow cops.</li>
 * <li>Legs: square steel cuisses, knee cops and greaves, and over the front and outside of each thigh a portcullis:
 * four iron bars hanging from the belt, two cross-bars, spiked tips.</li>
 * <li>Boots: blocky sabatons (a square shaft with a blue cuff, a block foot, a stepped toe). Gloves: gauntlets with a
 * flared square cuff, a block hand and fingers, gold-rimmed.</li>
 * <li>Shield: a pavise shaped like the gate itself: a full-height board with an arched top, a face of blue door planks,
 * a steel border, two iron bands, the gold gate-tower crest, and two spiked feet. It stands from the knee to over the
 * shoulder.</li>
 * </ul>
 *
 * <p>Labels, as BaseCoat: the helm on the skull 1; the breastplate, gorget, belt and the tabard's upper part on the
 * chest 8 and the belly 4; the tabard's skirt halves on the thighs 43 / 44 (each half swings with its own leg); the
 * tower pauldrons and the upper arms on 20 / 26, the forearms 19 / 22; the legs' belt on 41, cuisses and portcullises
 * on the thighs, knee cops and greaves on the shins 33 / 36; boots on the ankles 32 / 38 and toes 45 / 46; gauntlets
 * on the forearms and hands 27 / 28; the pavise on the left hand 28. No new vertex has a pivot label; each piece keeps
 * its donor's pivots on zero-area faces (BaseSerpent.anchors).
 */
final class BaseBastion {

	private BaseBastion() {
	}

	/** Gold for the crest and fittings, and the dark of the visor and the door. */
	static final int GOLD = hsl(8, 5, 72), GOLD_DARK = hsl(8, 5, 50), SLOT = hsl(40, 2, 8), PLUME_LIGHT = hsl(40, 6, 60);

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		Rig r = BaseCoat.rig(af, female);
		BaseMantle.Body b = BaseMantle.body(af, female);
		switch (slot) {
		case ArmourSets.HELM:
			return new ModelData[] { BaseSerpent.anchors(helm(r, b, st), donor), null };
		case ArmourSets.BODY:
			return new ModelData[] { BaseSerpent.anchors(body(r, st), donor), BaseSerpent.anchors(arms(r, st), donorSecond) };
		case ArmourSets.LEGS:
			return new ModelData[] { BaseSerpent.anchors(legs(r, st), donor), null };
		case ArmourSets.SHIELD:
			return new ModelData[] { pavise(af, st), null };
		case ArmourSets.BOOTS:
			return new ModelData[] { BaseSerpent.anchors(boots(r, st), donor), null };
		default:
			return new ModelData[] { BaseSerpent.anchors(gauntlets(r, st), donor), null };
		}
	}

	/** The inventory model for the slot, or null to let ArmourSets build it the usual way from the worn model(s). */
	static ModelData inventory(ArmourForge af, Style st, int slot, ModelData[] worn) throws IOException {
		if (slot == ArmourSets.SHIELD) { // a quarter turn in its plane, so the kite's icon camera stands the gate up, arch on top
			ModelData p = ArmourForge.copyModel(worn[0]);
			Frame k = ArmourForge.shieldFrame(af.read(ArmourForge.RUNE_KITE_WORN), 61);
			double[] n = k.n;
			double th = Math.PI / 2;
			double c = Math.cos(th), s = Math.sin(th);
			double[] o = k.o;
			p.transform(v -> { // Rodrigues, about n through o
				double[] q = sub(v, o);
				double[] r = add(add(scale(q, c), scale(ModelData.cross(n, q), s)), scale(n, dot(n, q) * (1 - c)));
				return add(r, o);
			});
			double tau = Math.PI * 2;
			return af.shieldInventory(p, -1104 * tau / 2048, 344 * tau / 2048);
		}
		return null;
	}

	/* ====================================================================== */
	/*                                 BLOCKS                                 */
	/* ====================================================================== */

	/** A flat-shaded mesh. */
	static Mesh mesh() {
		Mesh m = new Mesh();
		m.d.shading = 1;
		return m;
	}

	/**
	 * A block from end A (c - ay hy) to end B (c + ay hy), half-sizes (hxA, hzA) at A and (hxB, hzB) at B along ax / az.
	 * cols: +x, -x, +z, -z, A, B (negative: that face is left off).
	 */
	static void block(Mesh m, double[] c, double[] ax, double[] ay, double[] az, double hy, double hxA, double hzA, double hxB, double hzB, int label, int[] cols) {
		int[][] v = new int[2][4];
		double[][] hs = { { hxA, hzA }, { hxB, hzB } };
		double[][] sg = { { -1, -1 }, { 1, -1 }, { 1, 1 }, { -1, 1 } };
		for (int e = 0; e < 2; e++) {
			double[] ce = add(c, scale(ay, e == 0 ? -hy : hy));
			for (int k = 0; k < 4; k++) {
				v[e][k] = m.v(add(ce, add(scale(ax, sg[k][0] * hs[e][0]), scale(az, sg[k][1] * hs[e][1]))), label);
			}
		}
		// sides: k=1..2 is +x, 3..0 is -x, 2..3 is +z, 0..1 is -z
		int[][] side = { { 1, 2 }, { 3, 0 }, { 2, 3 }, { 0, 1 } };
		for (int s = 0; s < 4; s++) {
			if (cols[s] >= 0) {
				m.quadAway(v[0][side[s][0]], v[0][side[s][1]], v[1][side[s][1]], v[1][side[s][0]], cols[s], c);
			}
		}
		if (cols[4] >= 0) {
			m.quad(v[0][0], v[0][1], v[0][2], v[0][3], cols[4], scale(ay, -1));
		}
		if (cols[5] >= 0) {
			m.quad(v[1][0], v[1][1], v[1][2], v[1][3], cols[5], ay);
		}
	}

	static final double[] X = BaseCoat.X, Y = BaseCoat.Y, Z = BaseCoat.Z;

	/** An upright box (y down), centre c, half-sizes hx, hy, hz. */
	static void box(Mesh m, double[] c, double hx, double hy, double hz, int label, int[] cols) {
		block(m, c, X, Y, Z, hy, hx, hz, hx, hz, label, cols);
	}

	/** The colours of a box: sides, front, top, bottom (negative: left off). */
	static int[] cols(int side, int front, int top, int bottom) {
		return new int[] { side, side, side, front, top, bottom };
	}

	/** An octagon of 8 vertices round (0, y, cz): half-sizes rx, rz, corners cut by ch; the first edge is the front. */
	static int[] oct(Mesh m, double y, double cz, double rx, double rz, double ch, int label) {
		double[][] p = { { -(rx - ch), -rz }, { rx - ch, -rz }, { rx, -(rz - ch) }, { rx, rz - ch }, { rx - ch, rz }, { -(rx - ch), rz }, { -rx, rz - ch },
				{ -rx, -(rz - ch) } };
		int[] r = new int[8];
		for (int k = 0; k < 8; k++) {
			r[k] = m.v(pt(p[k][0], y, cz + p[k][1]), label);
		}
		return r;
	}

	/** A raised plate on a plane (origin o, across A, down D, out N): the rectangle a0..a1 x d0..d1, h proud. */
	static void plate(Mesh m, double[] o, double[] A, double[] D, double[] N, double a0, double d0, double a1, double d1, double h, int label, int front, int side) {
		double[] c = add(o, add(scale(A, (a0 + a1) / 2), add(scale(D, (d0 + d1) / 2), scale(N, h / 2))));
		block(m, c, A, N, D, h / 2, (a1 - a0) / 2, (d1 - d0) / 2, (a1 - a0) / 2, (d1 - d0) / 2, label, new int[] { side, side, side, side, -1, front });
	}

	/**
	 * Gatehold's gate-tower crest on a plane, centred at (a, d), {@code s} tall: the tower with its battlement band and
	 * three merlons, and a dark arched gate at its foot.
	 */
	static void crest(Mesh m, double[] o, double[] A, double[] D, double[] N, double a, double d, double s, int label) {
		double h = Math.max(0.8, s * 0.05);
		plate(m, o, A, D, N, a - s * 0.27, d - s * 0.22, a + s * 0.27, d + s * 0.5, h, label, GOLD, GOLD_DARK);
		plate(m, o, A, D, N, a - s * 0.38, d - s * 0.34, a + s * 0.38, d - s * 0.2, h * 1.5, label, GOLD, GOLD_DARK);
		for (int k = -1; k <= 1; k++) {
			plate(m, o, A, D, N, a + k * s * 0.29 - s * 0.08, d - s * 0.5, a + k * s * 0.29 + s * 0.08, d - s * 0.32, h * 1.5, label, GOLD, GOLD_DARK);
		}
		plate(m, o, A, D, N, a - s * 0.12, d + s * 0.18, a + s * 0.12, d + s * 0.5, h * 1.6, label, SLOT, SLOT);
	}

	/* ====================================================================== */
	/*                                THE HELM                                */
	/* ====================================================================== */

	/** The battlemented great helm: an octagonal tower over the head, a portcullis visor, merlons, a plume. */
	static ModelData helm(Rig r, BaseMantle.Body b, Style st) {
		Mesh m = mesh();
		final int L = 1;
		int[] hb = r.headB;
		double T = hb[1], C = b.headChin, F = hb[2], B = hb[5], HW = hb[3];
		double top = T - 5, bot = C + 6;
		double cz = (F + B) / 2 - 1.5, rx = HW + 3.2, rz = (B - F) / 2 + 3.8, ch = 4.0;
		double zf = cz - rz;
		// the shell: the sides as one band, the front framing the visor slot
		int[] o0 = oct(m, top, cz, rx, rz, ch, L), o1 = oct(m, bot, cz, rx, rz, ch, L);
		m.prio = 6;
		int[] colsSide = { -1, st.main, st.light, st.main, st.dark, st.main, st.light, st.main };
		for (int k = 1; k < 8; k++) {
			int k2 = (k + 1) % 8;
			m.quadAway(o0[k], o0[k2], o1[k2], o1[k], colsSide[k], pt(0, (top + bot) / 2, cz));
		}
		m.prio = 7;
		for (int k = 1; k + 1 < 8; k++) {
			m.tri(o0[0], o0[k], o0[k + 1], st.dark, pt(0, -1, 0));
		}
		// the front: a frame round the visor, the slot dark and set back
		double fx = rx - ch, vx = fx * 0.72, vt = T + 0.30 * (C - T), vb = C - 0.5;
		double[] A = X, D = Y, N = pt(0, 0, -1);
		double[] o = pt(0, 0, zf);
		int[][] fr = { { -1, 0 }, { 1, 0 } };
		m.prio = 6;
		quadAt(m, o, A, D, N, -fx, top, fx, vt, 0, L, st.light);
		quadAt(m, o, A, D, N, -fx, vb, fx, bot, 0, L, st.light);
		quadAt(m, o, A, D, N, -fx, vt, -vx, vb, 0, L, st.light);
		quadAt(m, o, A, D, N, vx, vt, fx, vb, 0, L, st.light);
		quadAt(m, o, A, D, N, -vx, vt, vx, vb, -3.0, L, SLOT); // the slot's back
		// the slot's walls
		m.prio = 6;
		for (int s = -1; s <= 1; s += 2) {
			int a0 = m.v(pt(s * vx, vt, zf), L), a1 = m.v(pt(s * vx, vb, zf), L), a2 = m.v(pt(s * vx, vb, zf + 3), L), a3 = m.v(pt(s * vx, vt, zf + 3), L);
			m.quad(a0, a1, a2, a3, st.dark, pt(-s, 0, 0));
		}
		int t0 = m.v(pt(-vx, vt, zf), L), t1 = m.v(pt(vx, vt, zf), L), t2 = m.v(pt(vx, vt, zf + 3), L), t3 = m.v(pt(-vx, vt, zf + 3), L);
		m.quad(t0, t1, t2, t3, st.dark, pt(0, 1, 0));
		// the portcullis: three bars down, two across, spiked at the foot
		m.prio = 8;
		for (int k = -1; k <= 1; k++) {
			double x = k * vx * 0.62;
			box(m, pt(x, (vt + vb) / 2 - 0.5, zf - 0.3), 0.9, (vb - vt) / 2 - 0.5, 0.9, L, cols(st.dark, st.main, -1, -1));
			int tipv = m.v(pt(x, vb + 2.2, zf - 0.6), L);
			int p0 = m.v(pt(x - 0.9, vb - 1.0, zf - 1.2), L), p1 = m.v(pt(x + 0.9, vb - 1.0, zf - 1.2), L), p2 = m.v(pt(x, vb - 1.0, zf + 0.6), L);
			m.triAway(p0, p1, tipv, st.light, pt(x, vb - 2, zf));
			m.triAway(p1, p2, tipv, st.main, pt(x, vb - 2, zf));
			m.triAway(p2, p0, tipv, st.main, pt(x, vb - 2, zf));
		}
		for (int k = 0; k < 2; k++) {
			double y = vt + (vb - vt) * (0.3 + 0.38 * k);
			box(m, pt(0, y, zf - 0.5), vx, 0.8, 0.8, L, cols(st.dark, st.main, st.light, -1));
		}
		// a Gatehold-blue band round the foot of the helm, and a gold string-course under the battlements
		int[] u0 = oct(m, bot - 3.2, cz, rx + 0.7, rz + 0.7, ch, L), u1 = oct(m, bot + 0.3, cz, rx + 0.7, rz + 0.7, ch, L);
		int[] v0 = oct(m, top + 1.2, cz, rx + 0.5, rz + 0.5, ch, L), v1 = oct(m, top + 2.6, cz, rx + 0.5, rz + 0.5, ch, L);
		m.prio = 8;
		m.band(u0, u1, true, new int[] { st.trim, st.trim, st.trimDark, st.trimDark, st.trimDark, st.trimDark, st.trimDark, st.trim }, pt(0, bot, cz));
		m.band(v0, v1, true, new int[] { GOLD, GOLD, GOLD_DARK, GOLD_DARK, GOLD_DARK, GOLD_DARK, GOLD_DARK, GOLD }, pt(0, top + 2, cz));
		// battlements round the crown: a merlon on each face and each corner
		m.prio = 7;
		double mh = 4.6;
		for (int k = 0; k < 8; k++) {
			double a = Math.PI * 2 * k / 8; // 0 front, pi/2 left
			double ex = Math.sin(a), ez = -Math.cos(a);
			boolean corner = k % 2 == 1;
			double px = corner ? Math.signum(ex) * (rx - ch / 2) : ex * (rx - 1.2), pz = corner ? Math.signum(ez) * (rz - ch / 2) : ez * (rz - 1.2);
			double hx = corner ? 2.0 : Math.abs(ex) > 0.5 ? 1.2 : 2.6, hz = corner ? 2.0 : Math.abs(ez) > 0.5 ? 1.2 : 2.6;
			box(m, pt(px, top - mh / 2, cz + pz), hx, mh / 2, hz, L, cols(st.main, st.light, st.light, -1));
		}
		// the plume: a gold socket, then blue feathers rising high and sweeping back
		box(m, pt(0, top - 2.0, cz + 2), 2.6, 2.0, 2.6, L, cols(GOLD_DARK, GOLD, GOLD, -1));
		ModelData pl = new ModelData();
		double[] root = pt(0, top - 3.5, cz + 2);
		double[][] tips = { { 0, -34, -4 }, { 0, -38, 4 }, { 0, -34, 12 }, { 0, -27, 19 }, { 0, -18, 23 } };
		for (int k = 0; k < tips.length; k++) {
			double sc = (r.headB[4] - r.headB[1]) / 32.0;
			double[] tip = add(root, scale(tips[k], sc));
			double[] mid = add(BaseSerpent.lerp(root, tip, 0.55), pt(0, 0, -3.0 * sc));
			ArmourForge.blade(pl, add(root, pt(0, 1, 0)), mid, X, 3.4, 1.2, k % 2 == 0 ? st.trim : PLUME_LIGHT);
			ArmourForge.blade(pl, mid, tip, X, 2.6, 0.9, k % 2 == 0 ? PLUME_LIGHT : st.trim);
		}
		BaseSerpent.absorb(m, pl, 7, L);
		for (int i = 0; i < pl.faces.size(); i++) {
			m.d.renderTypes.set(m.d.renderTypes.size() - 1 - i, 1);
		}
		return m.d;
	}

	/** A flat quad on a plane (o, A across, D down as world y, N out): a0..a1 by y0..y1, set {@code off} along N. */
	static void quadAt(Mesh m, double[] o, double[] A, double[] D, double[] N, double a0, double y0, double a1, double y1, double off, int label, int col) {
		double[] base = add(o, scale(N, off));
		int p0 = m.v(add(base, add(scale(A, a0), pt(0, y0, 0))), label), p1 = m.v(add(base, add(scale(A, a1), pt(0, y0, 0))), label);
		int p2 = m.v(add(base, add(scale(A, a1), pt(0, y1, 0))), label), p3 = m.v(add(base, add(scale(A, a0), pt(0, y1, 0))), label);
		m.quad(p0, p1, p2, p3, col, N);
	}

	/* ====================================================================== */
	/*                                THE BODY                                */
	/* ====================================================================== */

	/** The breastplate's octagon at height y: {cz, rx, rz}. */
	static double[] torso(Rig r, double y, double margin) {
		return BaseCoat.torsoRing(r, y, margin, 0);
	}

	/** Breastplate, gorget, belt, tabard and crest, the tower pauldrons. */
	static ModelData body(Rig r, Style st) {
		Mesh m = mesh();
		double L = r.waistY - r.neckY;
		double[] ys = { r.neckY + 1, r.neckY + 7, r.neckY + 0.5 * L, r.waistY - 1 };
		double[][] d = new double[ys.length][];
		for (int i = 0; i < ys.length; i++) {
			d[i] = torso(r, ys[i], 2.4);
		}
		// the shoulder line squared off: as wide as the chest, as deep
		d[1][1] = Math.max(d[1][1], d[2][1]);
		d[1][2] = Math.max(d[1][2], d[2][2]);
		d[3][1] = Math.min(d[3][1], d[2][1]) - 0.5;
		d[0] = new double[] { d[1][0], d[1][1] * 0.55, d[1][2] * 0.72 };
		int[][] ring = new int[ys.length][];
		for (int i = 0; i < ys.length; i++) {
			ring[i] = oct(m, ys[i], d[i][0], d[i][1], d[i][2], Math.min(d[i][1], d[i][2]) * 0.3, i < 3 ? 8 : 4);
		}
		m.prio = 3;
		int[] front = { st.light, st.main, st.dark, st.dark, st.dark, st.dark, st.dark, st.main };
		int[] side = { st.main, st.main, st.main, st.dark, st.dark, st.dark, st.main, st.main };
		m.band(ring[0], ring[1], true, new int[] { st.light, st.main, st.main, st.dark, st.dark, st.dark, st.main, st.main }, pt(0, ys[1] + 6, d[1][0]));
		m.band(ring[1], ring[2], true, front, pt(0, (ys[1] + ys[2]) / 2, d[1][0]));
		m.band(ring[2], ring[3], true, side, pt(0, (ys[2] + ys[3]) / 2, d[2][0]));
		// the gorget: a standing octagonal collar round the neck
		int[] g0 = oct(m, r.neckY + 2, d[0][0], d[0][1] * 0.95, d[0][2] * 0.95, 2.5, 8), g1 = oct(m, r.neckY - 4, d[0][0], d[0][1] * 0.9, d[0][2] * 0.9, 2.5, 8);
		m.prio = 4;
		m.band(g0, g1, true, new int[] { st.main, st.light, st.main, st.dark, st.dark, st.dark, st.main, st.light }, pt(0, r.neckY - 1, d[0][0]));
		// the belt: Gatehold blue, a gold buckle
		double[] w = torso(r, r.waistY - 1, 3.2);
		int[] b0 = oct(m, r.waistY - 4, w[0], d[3][1] + 1.0, w[2], Math.min(d[3][1], w[2]) * 0.3, 4), b1 = oct(m, r.waistY + 3.5, w[0], d[3][1] + 1.4, w[2] + 0.4,
				Math.min(d[3][1], w[2]) * 0.3, 4);
		m.prio = 5;
		m.band(b0, b1, true, new int[] { st.trimDark }, pt(0, r.waistY, w[0]));
		box(m, pt(0, r.waistY - 0.25, w[0] - w[2] - 1.0), 3.2, 3.2, 0.9, 4, cols(GOLD_DARK, GOLD, GOLD, GOLD_DARK));
		// the tabard: front and back, a panel over the breastplate, the skirt in two halves between the legs' portcullises
		double tw = Math.min(d[2][1] * 0.52, 9.5);
		double hem = r.knee[1] - 13;
		for (int face = -1; face <= 1; face += 2) {
			double z = face < 0 ? d[2][0] - d[2][2] - 0.6 : d[2][0] + d[2][2] + 0.6;
			double[] N = pt(0, 0, face);
			double[] o = pt(0, 0, z);
			m.prio = 5;
			// over the chest: blue, edged in gold
			quadAt2(m, o, N, -tw, r.neckY + 9, tw, r.waistY - 4.5, 8, 4, r, st.trim);
			for (int s = -1; s <= 1; s += 2) {
				quadAt2(m, add(o, scale(N, 0.4)), N, s > 0 ? tw - 1.2 : -tw, r.neckY + 9, s > 0 ? tw : -tw + 1.2, r.waistY - 4.5, 8, 4, r, GOLD);
			}
			quadAt2(m, add(o, scale(N, 0.4)), N, -tw, r.neckY + 9, tw, r.neckY + 10.2, 8, 8, r, GOLD);
			// the skirt halves, each on its thigh
			double[] sk = BaseCoat.slice(r.legs, r.waistY + 6, 0);
			double zs = face < 0 ? sk[2] - 3.6 : sk[3] + 3.6;
			for (int s = -1; s <= 1; s += 2) {
				int lab = s > 0 ? 43 : 44;
				double x0 = s * 0.8, x1 = s * tw * 0.72;
				double[] zz = { zs, zs + face * 1.5 };
				int q0 = m.v(pt(x0, r.waistY + 2, w[0] + face * (w[2] + 0.6)), lab), q1 = m.v(pt(x1, r.waistY + 2, w[0] + face * (w[2] + 0.6)), lab);
				int q2 = m.v(pt(x1 * 1.08, hem, zz[1]), lab), q3 = m.v(pt(x0, hem, zz[1]), lab);
				m.quad(q0, q1, q2, q3, st.trim, N);
				m.quad(q0, q1, q2, q3, st.trimDark, scale(N, -1));
				int e0 = m.v(pt(x0, hem - 2.0, zz[1] + face * 0.4), lab), e1 = m.v(pt(x1 * 1.07, hem - 2.0, zz[1] + face * 0.4), lab);
				int e2 = m.v(pt(x1 * 1.08, hem, zz[1] + face * 0.4), lab), e3 = m.v(pt(x0, hem, zz[1] + face * 0.4), lab);
				m.quad(e0, e1, e2, e3, GOLD, N);
			}
			if (face < 0) { // the gate-tower crest on the breast
				double cy = r.neckY + 0.42 * L + 2;
				crest(m, add(o, scale(N, 0.3)), X, Y, N, 0, cy, Math.min(tw * 1.6, L * 0.55), 8);
			}
		}
		// the tower pauldrons
		for (int s = -1; s <= 1; s += 2) {
			tower(m, r, st, s);
		}
		return m.d;
	}

	/** A flat quad facing N on the plane through o: x0..x1 by y0..y1, labelled by height (chest above, belly below). */
	static void quadAt2(Mesh m, double[] o, double[] N, double x0, double y0, double x1, double y1, int labTop, int labBot, Rig r, int col) {
		double split = r.neckY + 0.45 * (r.waistY - r.neckY);
		int p0 = m.v(add(o, pt(x0, y0, 0)), y0 < split ? labTop : labBot), p1 = m.v(add(o, pt(x1, y0, 0)), y0 < split ? labTop : labBot);
		int p2 = m.v(add(o, pt(x1, y1, 0)), y1 < split ? labTop : labBot), p3 = m.v(add(o, pt(x0, y1, 0)), y1 < split ? labTop : labBot);
		m.quad(p0, p1, p2, p3, col, N);
	}

	/** A tower pauldron on one shoulder: a blue plinth, a steel shaft with an arrow slit outside, four merlons. */
	static void tower(Mesh m, Rig r, Style st, int s) {
		int lab = s > 0 ? 26 : 20;
		double[] sh = mirror(r.shoulder, s);
		double hx = 7.4, hz = 8.6, topY = r.armTop - 8, botY = r.armTop + 11;
		double cx = sh[0] + s * 1.6;
		m.prio = 11;
		// the shaft
		box(m, pt(cx, (topY + botY - 3) / 2, sh[2]), hx, (botY - 3 - topY) / 2, hz, lab, new int[] { s > 0 ? st.light : st.main, s > 0 ? st.main : st.light, st.dark, st.main,
				st.dark, -1 });
		// the plinth: Gatehold blue, a little wider
		box(m, pt(cx, botY - 1.5, sh[2]), hx + 0.9, 2.2, hz + 0.9, lab, new int[] { st.trim, st.trim, st.trimDark, st.trim, st.trimDark, st.trimDark });
		// the merlons
		double mh = 4.2;
		for (int a = -1; a <= 1; a += 2) {
			for (int c = -1; c <= 1; c += 2) {
				box(m, pt(cx + a * (hx - 2.0), topY - mh / 2, sh[2] + c * (hz - 2.2)), 2.0, mh / 2, 2.2, lab, cols(st.main, st.light, st.light, -1));
			}
		}
		// the arrow slit on the outside, and a gold band under the battlements
		m.prio = 12;
		double ox = cx + s * (hx + 0.2);
		int a0 = m.v(pt(ox, topY + 3.5, sh[2] - 1.0), lab), a1 = m.v(pt(ox, topY + 3.5, sh[2] + 1.0), lab), a2 = m.v(pt(ox, topY + 10.5, sh[2] + 1.0), lab),
				a3 = m.v(pt(ox, topY + 10.5, sh[2] - 1.0), lab);
		m.quad(a0, a1, a2, a3, SLOT, pt(s, 0, 0));
		box(m, pt(cx, topY + 1.0, sh[2]), hx + 0.35, 1.0, hz + 0.35, lab, new int[] { GOLD, GOLD, GOLD_DARK, GOLD, -1, -1 });
	}

	/** Square-tubed steel arms (the body's second model): rerebrace, an elbow cop, vambrace. */
	static ModelData arms(Rig r, Style st) {
		Mesh m = mesh();
		m.prio = 10;
		for (int s = -1; s <= 1; s += 2) {
			int up = s > 0 ? 26 : 20, fo = s > 0 ? 22 : 19;
			double[] J = mirror(BaseCoat.pt(r.shoulder[0], r.armTop + 6, r.shoulder[2]), s), E = mirror(r.elbow, s), W = mirror(pt(r.wrist[0], r.wristY, r.wrist[2]), s);
			double[] au = norm(sub(E, J)), af = norm(sub(W, E));
			double armR = Math.max(5, (BaseCoat.extent(r.arms, 21)[3] - BaseCoat.extent(r.arms, 21)[0]) / 2) + 0.4;
			double foreR = Math.max(4, (BaseCoat.extent(r.arms, 19)[3] - BaseCoat.extent(r.arms, 19)[0]) / 2) + 1.2;
			double[] ax = norm(sub(X, scale(au, dot(X, au)))), az = ModelData.cross(ax, au);
			double hu = Math.hypot(E[0] - J[0], E[1] - J[1]) / 2;
			block(m, BaseSerpent.lerp(J, E, 0.5), ax, au, az, hu + 1, armR, armR, armR - 0.4, armR - 0.4, up, new int[] { st.main, st.main, st.dark, st.light, -1, -1 });
			double[] bx = norm(sub(X, scale(af, dot(X, af)))), bz = ModelData.cross(bx, af);
			double hf = Math.hypot(W[0] - E[0], W[1] - E[1]) / 2;
			block(m, BaseSerpent.lerp(E, W, 0.5), bx, af, bz, hf + 0.5, foreR, foreR, foreR + 0.6, foreR + 0.6, fo, new int[] { st.main, st.main, st.dark, st.light, -1, st.dark });
			// the elbow cop
			box(m, add(E, pt(0, 0, 1.2)), foreR + 0.8, 2.6, foreR + 0.8, fo, cols(st.light, st.light, st.light, st.dark));
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                                THE LEGS                                */
	/* ====================================================================== */

	/** Square cuisses, knee cops, greaves; a portcullis over the front and outside of each thigh. */
	static ModelData legs(Rig r, Style st) {
		Mesh m = mesh();
		// the belt band round both legs, on the static belt label
		double[] s0 = BaseCoat.slice(r.legs, r.waistY + 3, 0);
		double bcz = (s0[2] + s0[3]) / 2, brx = Math.max(-s0[0], s0[1]) + 2.6, brz = (s0[3] - s0[2]) / 2 + 2.6;
		int[] w0 = oct(m, r.waistY - 1.5, bcz, brx, brz, 4, 41), w1 = oct(m, r.waistY + 6, bcz, brx + 0.6, brz + 0.6, 4, 41);
		m.prio = 2;
		m.band(w0, w1, true, new int[] { st.main, st.light, st.main, st.dark, st.dark, st.dark, st.main, st.light }, pt(0, r.waistY + 2, bcz));
		for (int s = -1; s <= 1; s += 2) {
			int thigh = s > 0 ? 43 : 44, shin = s > 0 ? 36 : 33;
			// the cuisse: from the hip to above the knee
			double y0 = r.waistY + 5, y1 = r.knee[1] - 5;
			double[] qa = BaseCoat.legRing(r, y0 + 6, s, 1.6), qb = BaseCoat.legRing(r, y1, s, 1.6);
			qa[0] = s * Math.max(Math.abs(qa[0]), 8.5);
			m.prio = 1;
			double[] cA = pt(qa[0], y0, qa[1]), cB = pt(qb[0], y1, qb[1]);
			block(m, BaseSerpent.lerp(cA, cB, 0.5), X, norm(sub(cB, cA)), Z, BaseSerpent.dist(cA, cB) / 2, qa[2], qa[3], qb[2], qb[3], thigh,
					new int[] { st.main, st.main, st.dark, st.light, -1, -1 });
			// the knee cop
			double[] kr = BaseCoat.legRing(r, r.knee[1], s, 2.2);
			m.prio = 3;
			box(m, pt(kr[0], r.knee[1] - 2, kr[1] - kr[3] + 1.5), kr[2] * 0.75, 4.2, 2.4, shin, cols(st.main, st.light, st.light, st.dark));
			// the greave: from the knee to the ankle
			double g0 = r.knee[1] + 2, g1 = r.ankle[1] + 1;
			double[] ga = BaseCoat.legRing(r, g0, s, 1.5), gb = BaseCoat.legRing(r, g1 - 2, s, 1.2);
			double[] gA = pt(ga[0], g0, ga[1]), gB = pt(gb[0], g1, gb[1]);
			m.prio = 2;
			block(m, BaseSerpent.lerp(gA, gB, 0.5), X, norm(sub(gB, gA)), Z, BaseSerpent.dist(gA, gB) / 2, ga[2], ga[3], gb[2], gb[3], shin,
					new int[] { st.main, st.main, st.dark, st.light, -1, -1 });
			// the portcullis: four bars hanging over the front and outside of the thigh, two cross-bars, spiked tips
			double top = r.waistY + 4, foot = r.knee[1] - 11;
			double[] pr = BaseCoat.legRing(r, (top + foot) / 2, s, 3.4);
			double[] angs = { -0.45, 0.2, 0.85, 1.5 };
			double[][] at = new double[angs.length][];
			m.prio = 4;
			for (int k = 0; k < angs.length; k++) {
				double a = angs[k] * s; // from the front round to the outside
				double bx = pr[0] + Math.sin(a) * (pr[2] + 0.6), bz = pr[1] - Math.cos(a) * (pr[3] + 0.4);
				if (s * bx < 4.2) {
					bx = s * 4.2; // clear of the tabard's halves in the middle
				}
				at[k] = pt(bx, 0, bz);
				double len = foot - top - (k == angs.length - 1 ? 3 : 0);
				box(m, pt(bx, top + len / 2, bz), 1.1, len / 2, 1.1, thigh, cols(st.dark, st.main, -1, -1));
				int tip = m.v(pt(bx, top + len + 3.4, bz), thigh);
				int p0 = m.v(pt(bx - 1.1, top + len, bz - 1.1), thigh), p1 = m.v(pt(bx + 1.1, top + len, bz - 1.1), thigh), p2 = m.v(pt(bx + 1.1, top + len, bz + 1.1), thigh),
						p3 = m.v(pt(bx - 1.1, top + len, bz + 1.1), thigh);
				double[] in = pt(bx, top + len - 2, bz);
				m.triAway(p0, p1, tip, st.light, in);
				m.triAway(p1, p2, tip, st.main, in);
				m.triAway(p2, p3, tip, st.dark, in);
				m.triAway(p3, p0, tip, st.main, in);
			}
			m.prio = 5;
			for (int c = 0; c < 2; c++) {
				double y = top + (foot - top) * (0.28 + 0.42 * c);
				for (int k = 0; k + 1 < at.length; k++) {
					double[] a = pt(at[k][0], y, at[k][2]), bb = pt(at[k + 1][0], y, at[k + 1][2]);
					double[] dir = norm(sub(bb, a));
					double[] out = norm(ModelData.cross(Y, dir));
					if (dot(out, sub(BaseSerpent.lerp(a, bb, 0.5), pt(pr[0], y, pr[1]))) < 0) {
						out = scale(out, -1);
					}
					block(m, add(BaseSerpent.lerp(a, bb, 0.5), scale(out, 0.9)), out, dir, Y, BaseSerpent.dist(a, bb) / 2 + 0.9, 0.7, 0.9, 0.7, 0.9, thigh,
							new int[] { st.main, -1, st.dark, st.light, -1, -1 });
				}
			}
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                             BOOTS, GAUNTLETS                           */
	/* ====================================================================== */

	/** Blocky sabatons: a square shaft with a blue cuff, a block foot, a stepped toe. */
	static ModelData boots(Rig r, Style st) {
		Mesh m = mesh();
		int[] fb = r.feetB;
		for (int s = -1; s <= 1; s += 2) {
			int ank = s > 0 ? 38 : 32, toe = s > 0 ? 46 : 45;
			double[] fe = BaseCoat.extent(r.feet, s > 0 ? 47 : 48);
			double x = (fe[0] + fe[3]) / 2, hw = (fe[3] - fe[0]) / 2 + 1.3;
			double sole = fb[4] + 0.5, back = fb[5] + 1.2, front = fb[2] - 1.5;
			double[] lr = BaseCoat.legRing(r, r.ankle[1], s, 2.4);
			double sTop = r.ankle[1] - 13, sBot = sole - 7;
			m.prio = 2;
			box(m, pt(lr[0], (sTop + sBot) / 2, lr[1] + 0.5), lr[2] + 0.6, (sBot - sTop) / 2, lr[3] + 0.8, ank, cols(st.main, st.light, -1, -1));
			box(m, pt(lr[0], sTop + 1.8, lr[1] + 0.5), lr[2] + 1.4, 2.0, lr[3] + 1.6, ank, new int[] { st.trim, st.trim, st.trimDark, st.trim, st.trimDark, st.trimDark });
			// the foot block, heel to instep
			double mid = (back + front) / 2;
			box(m, pt(x, sole - 3.8, (back + mid) / 2), hw, 3.8, (back - mid) / 2, ank, new int[] { st.main, st.main, st.dark, -1, st.main, st.dark });
			// the toe: a stepped block, lower towards the tip
			box(m, pt(x, sole - 3.2, (mid + front + 4) / 2), hw, 3.2, (mid - front - 4) / 2, toe, new int[] { st.main, st.main, -1, st.light, st.light, st.dark });
			box(m, pt(x, sole - 1.8, front + 2), hw - 0.8, 1.8, 2.0, toe, new int[] { st.main, st.main, -1, st.light, st.light, st.dark });
		}
		return m.d;
	}

	/** Square-cuffed gauntlets: a flared square cuff, gold-rimmed; a block hand, block fingers, a thumb. */
	static ModelData gauntlets(Rig r, Style st) {
		Mesh m = mesh();
		for (int s = -1; s <= 1; s += 2) {
			int hand = s > 0 ? 28 : 27, fore = s > 0 ? 22 : 19;
			double[] W = mirror(BaseCoat.centroid(r.hands, 19), s), H = mirror(r.hand, s), E = mirror(r.elbow, s);
			double[] he = BaseCoat.extent(r.hands, 27);
			double hx = (he[3] - he[0]) / 2 + 1.0, hz = (he[5] - he[2]) / 2 + 1.0;
			double[] af = norm(sub(W, E));
			double[] ax = norm(sub(X, scale(af, dot(X, af)))), az = ModelData.cross(ax, af);
			m.prio = 10;
			// the cuff: wide at the top, narrowing to the wrist
			block(m, add(W, scale(af, -5.5)), ax, af, az, 6.5, 7.6, 7.6, 5.2, 5.2, fore, new int[] { st.main, st.main, st.dark, st.light, st.dark, -1 });
			m.prio = 11;
			block(m, add(W, scale(af, -12.0)), ax, af, az, 0.9, 8.0, 8.0, 8.0, 8.0, fore, new int[] { GOLD, GOLD, GOLD_DARK, GOLD, GOLD, GOLD_DARK });
			// the hand and the fingers
			double[] D = norm(sub(H, W));
			double[] bx = norm(sub(X, scale(D, dot(X, D)))), bz = ModelData.cross(bx, D);
			m.prio = 10;
			block(m, add(W, scale(D, 5.0)), bx, D, bz, 4.5, hx, hz, hx + 0.3, hz + 0.3, hand, new int[] { st.main, st.main, st.dark, st.light, -1, -1 });
			block(m, add(W, scale(D, 13.0)), bx, D, bz, 3.5, hx + 0.1, hz, hx - 0.6, hz - 0.6, hand, new int[] { st.dark, st.dark, st.dark, st.main, -1, st.dark });
			box(m, add(add(W, scale(D, 6)), pt(-s * 0.8, 0, -hz - 1.0)), 1.6, 3.0, 1.4, hand, cols(st.main, st.light, -1, st.dark));
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                               THE PAVISE                               */
	/* ====================================================================== */

	/**
	 * The pavise: a gate. A board taller than the rune kite, its top arched, faced with vertical blue planks, a steel
	 * border, two iron bands, the gate-tower crest and two spiked feet; in the kite's plane, on the left hand 28.
	 */
	static ModelData pavise(ArmourForge af, Style st) throws IOException {
		ModelData kite = af.read(ArmourForge.RUNE_KITE_WORN);
		Frame k = ArmourForge.shieldFrame(kite, 61);
		double[] N = k.n;
		double[] D = norm(sub(Y, scale(N, dot(Y, N))));
		double[] A = norm(ModelData.cross(N, D));
		double[] o = add(k.o, add(scale(N, 1.0), scale(D, 4.0)));
		final int L = 28;
		Mesh m = mesh();
		double hw = 21, bot = 44, spring = -20, th = 1.6, back = -1.6;
		int cols = 6;
		double[] as = new double[cols + 1], tops = new double[cols + 1];
		for (int i = 0; i <= cols; i++) {
			as[i] = -hw + 2 * hw * i / cols;
			tops[i] = spring - Math.sqrt(Math.max(0, hw * hw - as[i] * as[i]));
		}
		java.util.function.BiFunction<Double, Double, double[]> P = (a, d) -> add(o, add(scale(A, a), scale(D, d)));
		int[] fb = new int[cols + 1], ft = new int[cols + 1], bb = new int[cols + 1], bt = new int[cols + 1];
		for (int i = 0; i <= cols; i++) {
			fb[i] = m.v(add(P.apply(as[i], bot), scale(N, th)), L);
			ft[i] = m.v(add(P.apply(as[i], tops[i]), scale(N, th)), L);
			bb[i] = m.v(add(P.apply(as[i], bot), scale(N, back)), L);
			bt[i] = m.v(add(P.apply(as[i], tops[i]), scale(N, back)), L);
		}
		// the sides go straight up to the springing of the arch
		int fsl = m.v(add(P.apply(-hw, spring), scale(N, th)), L), fsr = m.v(add(P.apply(hw, spring), scale(N, th)), L);
		int bsl = m.v(add(P.apply(-hw, spring), scale(N, back)), L), bsr = m.v(add(P.apply(hw, spring), scale(N, back)), L);
		m.prio = 9;
		int plankA = st.trim, plankB = hsl(40, 5, 33);
		for (int i = 0; i < cols; i++) {
			int c = i % 2 == 0 ? plankA : plankB;
			m.quad(fb[i], fb[i + 1], ft[i + 1], ft[i], c, N);
			m.quad(bb[i], bb[i + 1], bt[i + 1], bt[i], st.leatherDark, scale(N, -1));
		}
		// the edges: the sides, the foot, the arch
		double[] cen = o;
		m.quadAway(fb[0], bb[0], bb[cols], fb[cols], st.dark, cen);
		m.quadAway(fb[0], bb[0], bsl, fsl, st.main, cen);
		m.quadAway(fb[cols], bb[cols], bsr, fsr, st.main, cen);
		m.quadAway(fsl, bsl, bt[0], ft[0], st.main, cen);
		m.quadAway(fsr, bsr, bt[cols], ft[cols], st.main, cen);
		for (int i = 0; i < cols; i++) {
			m.quadAway(ft[i], ft[i + 1], bt[i + 1], bt[i], st.light, cen);
		}
		// the steel border: up both sides and round the arch, and along the foot
		m.prio = 10;
		double[] onFace = add(o, scale(N, th));
		plate(m, onFace, A, D, N, -hw, spring, -hw + 2.6, bot, 1.4, L, st.light, st.main);
		plate(m, onFace, A, D, N, hw - 2.6, spring, hw, bot, 1.4, L, st.light, st.main);
		plate(m, onFace, A, D, N, -hw + 2.6, bot - 2.6, hw - 2.6, bot, 1.4, L, st.main, st.dark);
		for (int i = 0; i < cols; i++) { // the arch's border, one block per plank
			double[] a0 = P.apply(as[i], tops[i]), a1 = P.apply(as[i + 1], tops[i + 1]);
			double[] dir = norm(sub(a1, a0));
			double[] inw = norm(sub(P.apply(0.0, spring), BaseSerpent.lerp(a0, a1, 0.5)));
			double[] c = add(add(BaseSerpent.lerp(a0, a1, 0.5), scale(inw, 1.3)), scale(N, th + 0.7));
			block(m, c, inw, dir, N, BaseSerpent.dist(a0, a1) / 2 + 0.6, 1.3, 0.7, 1.3, 0.7, L, new int[] { st.main, st.main, st.light, -1, -1, -1 });
		}
		// two iron bands across, riveted gold at the ends
		for (double d : new double[] { 30, -12 }) {
			plate(m, onFace, A, D, N, -hw + 2.6, d - 1.8, hw - 2.6, d + 1.8, 1.1, L, st.dark, st.dark);
		}
		// the crest, in the middle
		crest(m, add(onFace, scale(N, 0.2)), A, D, N, 0, 9, 25, L);
		// the spiked feet
		for (int s = -1; s <= 1; s += 2) {
			double[] f0 = P.apply(s * (hw - 4), bot);
			int tip = m.v(add(P.apply(s * (hw - 4), bot + 6), scale(N, 0)), L);
			int p0 = m.v(add(f0, add(scale(A, -2), scale(N, th))), L), p1 = m.v(add(f0, add(scale(A, 2), scale(N, th))), L), p2 = m.v(add(f0, add(scale(A, 2), scale(N, back))), L),
					p3 = m.v(add(f0, add(scale(A, -2), scale(N, back))), L);
			double[] in = P.apply(s * (hw - 4), bot - 3);
			m.triAway(p0, p1, tip, st.main, in);
			m.triAway(p1, p2, tip, st.dark, in);
			m.triAway(p2, p3, tip, st.dark, in);
			m.triAway(p3, p0, tip, st.dark, in);
		}
		return m.d;
	}
}
