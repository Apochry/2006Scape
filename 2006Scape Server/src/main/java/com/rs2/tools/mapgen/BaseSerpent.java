package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ArmourForge.add;
import static com.rs2.tools.mapgen.ArmourForge.dot;
import static com.rs2.tools.mapgen.ArmourForge.norm;
import static com.rs2.tools.mapgen.ArmourForge.scale;
import static com.rs2.tools.mapgen.ArmourForge.sub;
import static com.rs2.tools.mapgen.BaseCoat.ell;
import static com.rs2.tools.mapgen.BaseCoat.mirror;
import static com.rs2.tools.mapgen.BaseCoat.pt;
import static com.rs2.tools.mapgen.ModelData.hsl;

import java.io.IOException;
import java.util.Map;

import com.rs2.tools.mapgen.ArmourForge.Frame;
import com.rs2.tools.mapgen.ArmourSets.Style;
import com.rs2.tools.mapgen.BaseCoat.Mesh;
import com.rs2.tools.mapgen.BaseCoat.Rig;

/**
 * The Gravescale base: a Gravecoil (the barrow serpent) coiled round its wearer. Built from scratch for the Gravescale
 * (A31) draft set on the rune plate's rig; see NewBases. In the Gravecoil's own colours (Gravecoil.java): grave-moss
 * hide with a darker dorsal stripe, bone-white belly scutes, the kings' bronze rings gone green, venom-yellow eyes.
 *
 * <ul>
 * <li>Helm: the serpent's head, jaws gaping. The upper head (skull, brow ridges, snout) sits over the wearer's crown and
 * juts out over the brow; two long fangs hang from the upper jaw in front of the face; the lower jaw is a U of bone-white
 * chin scales under the wearer's chin, with small fangs pointing up; the face looks out through the open mouth (the name
 * hides nothing). Venom-yellow eyes on the brow, short bone horns swept back, and the serpent's neck running down the
 * back of the head to the nape. All on the skull label 1.</li>
 * <li>Body (a "shirt", so the arms are drawn by its own sleeves): a scale shirt with scalloped rows, and the serpent's
 * body coiled round it: a yoke round the base of the neck lying on the shoulders, then two thick banded coils round the
 * chest and the belt, dorsal blotches on top, pale ventral scutes underneath, a bronze ring grown into the hide; the tail
 * comes out at the back of the belt and hangs behind to the calves, its tip hooked back. Scaled sleeves to the wrist.</li>
 * <li>Legs: banded segmented greaves: each leg a stack of overlapping flared bands like a serpent's segments, the front
 * of every band a pale ventral plate, a bronze ring at the knee.</li>
 * <li>Boots: scaled boots, two overlapping bands round the shaft and a pointed bone toe. Gloves: scaled gauntlets
 * whose fingers end in four long curved bone fangs.</li>
 * <li>Shield: a Gravecoil coiled flat into a round shield, three turns round a dark boss, its head raised at the top,
 * jaws open, looking out over the rim.</li>
 * </ul>
 *
 * <p>Labels. As BaseCoat: new vertices never take a pivot label; the helm is the skull 1; the shirt and the coils the
 * chest 8 above the ribs and the belly 4 below (the lowest coil sits above the hip joints, so the thighs swing under it);
 * the sleeves the upper arms 20 / 26 and the forearms 19 / 22; the greaves the static belt 41, the thighs 43 / 44, the
 * shins 33 / 36 and the ankles 32 / 38; the boots the ankles and the toes 45 / 46; the gauntlets the forearms and the
 * hands 27 / 28; the shield the left hand 28. The tail hangs on the stock capes' chain (9 at the waist, 14, 13, 12
 * down to the hem), its rows at the stock cape's heights, so the walk and run swing it back off the heels as they do a
 * cape. Every piece keeps its donor's pivot vertices (see {@link #anchors}), on faces of zero area so the client's
 * player merge keeps them too.
 */
final class BaseSerpent {

	private BaseSerpent() {
	}

	/* The Gravecoil's colours (Gravecoil.java), and the venom in its eyes. */
	static final int HIDE = hsl(14, 2, 38), HIDE_2 = hsl(14, 2, 32), HIDE_TOP = hsl(15, 2, 27), HIDE_DARK = hsl(15, 2, 19), BELLY = hsl(9, 2, 84),
			BELLY_2 = hsl(9, 2, 72), BELLY_3 = hsl(9, 2, 60), BONE = hsl(9, 2, 92), BONE_DARK = hsl(9, 2, 70), BRONZE = hsl(7, 4, 46),
			BRONZE_DARK = hsl(7, 4, 32), VERDIGRIS = hsl(25, 2, 46), MOUTH = hsl(0, 4, 34), MOUTH_DARK = hsl(0, 4, 16), VENOM = hsl(11, 7, 92),
			VENOM_DARK = hsl(10, 7, 56), HIDE_LIGHT = hsl(14, 2, 50);

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		Rig r = BaseCoat.rig(af, female);
		BaseMantle.Body b = BaseMantle.body(af, female);
		switch (slot) {
		case ArmourSets.HELM:
			return new ModelData[] { anchors(helm(r, b), donor), null };
		case ArmourSets.BODY:
			return new ModelData[] { anchors(body(r, b), donor), anchors(sleeves(r), donorSecond) };
		case ArmourSets.LEGS:
			return new ModelData[] { anchors(greaves(r), donor), null };
		case ArmourSets.SHIELD:
			return new ModelData[] { shield(af), null };
		case ArmourSets.BOOTS:
			return new ModelData[] { anchors(boots(r), donor), null };
		default:
			return new ModelData[] { anchors(gauntlets(r), donor), null };
		}
	}

	/** The inventory model for the slot, or null to let ArmourSets build it the usual way from the worn model(s). */
	static ModelData inventory(ArmourForge af, Style st, int slot, ModelData[] worn) throws IOException {
		if (slot == ArmourSets.BODY) { // the shirt and its coils without the hanging tail, which would shrink the icon
			Rig r = BaseCoat.rig(af, false);
			Mesh m = new Mesh();
			shirt(m, r);
			coils(m, r, BaseMantle.body(af, false), false);
			ModelData all = ArmourForge.mergeAll(m.d, sleeves(r));
			return af.inventory(all, new int[] { ArmourForge.RUNE_BODY_WORN, ArmourForge.RUNE_ARMS_WORN }, ArmourForge.RUNE_BODY_INV);
		}
		return null;
	}

	/* ====================================================================== */
	/*                           SHARED: RIG, SWEEPS                          */
	/* ====================================================================== */

	/**
	 * Keeps the donor's pivots: for each pivot label (BaseCoat.PIVOTS) as many vertices as the donor has on it, laid in a
	 * short line through the label's centroid (so their sum is the donor's), joined by triangles of zero area. The
	 * client's player merge only keeps vertices that a face uses, and it pivots each joint on the centroid of its labels,
	 * so these are what keep every joint turning about the same point as in the stock piece. They lie inside the limb and
	 * draw nothing.
	 */
	static ModelData anchors(ModelData m, ModelData donor) {
		if (donor == null) {
			return m;
		}
		Map<Integer, double[]> sum = new java.util.TreeMap<>();
		for (int i = 0; i < donor.vertices.size(); i++) {
			int label = donor.vertexSkins.get(i);
			if (BaseCoat.PIVOTS.contains(label)) {
				int[] v = donor.vertices.get(i);
				double[] a = sum.computeIfAbsent(label, k -> new double[4]);
				a[0] += v[0];
				a[1] += v[1];
				a[2] += v[2];
				a[3]++;
			}
		}
		int prio = m.priorities.isEmpty() ? 0 : m.priorities.get(0);
		int col = m.colors.isEmpty() ? 0 : m.colors.get(0);
		for (Map.Entry<Integer, double[]> e : sum.entrySet()) {
			double[] a = e.getValue();
			int n = (int) a[3];
			int[] c = { (int) Math.round(a[0] / n), (int) Math.round(a[1] / n), (int) Math.round(a[2] / n) };
			int[] idx = new int[Math.max(3, n)];
			for (int i = 0; i < n; i++) {
				int off = (i + 1) / 2 * (i % 2 == 0 ? 1 : -1); // 0, -1, 1, -2, 2...: symmetric about the centroid
				if (n % 2 == 0) {
					off = (i / 2 + 1) * (i % 2 == 0 ? 1 : -1); // 1, -1, 2, -2...
				}
				m.vertices.add(new int[] { c[0], c[1], c[2] + off });
				m.vertexSkins.add(e.getKey());
				idx[i] = m.vertices.size() - 1;
			}
			for (int i = n; i < idx.length; i++) {
				idx[i] = idx[i % n];
			}
			for (int i = 0; i < idx.length; i += 3) { // collinear: no area, nothing drawn
				int p = idx[Math.min(i, idx.length - 1)], q = idx[Math.min(i + 1, idx.length - 1)], s = idx[Math.min(i + 2, idx.length - 1)];
				if (i + 2 >= idx.length) {
					p = idx[idx.length - 3];
					q = idx[idx.length - 2];
					s = idx[idx.length - 1];
				}
				m.faces.add(new int[] { p, q, s });
				m.colors.add(col);
				m.renderTypes.add(0);
				m.priorities.add(prio);
				m.alphas.add(0);
				m.faceSkins.add(0);
			}
		}
		return m;
	}

	/** Colour of one face of a sweep: segment i, the face's direction as cos / sin of its angle from the sweep's "up". */
	interface Col {
		int at(int seg, double up, double side);
	}

	/**
	 * A tube swept along {@code p}: at each point a ring of {@code sides} vertices round the tangent, radius ra[i] along
	 * {@code up[i]} (made square to the tangent) and rb[i] across it, with label lab[i]; a radius of 0 makes a single
	 * point (a tip). Faces face away from the axis. Returns each ring's vertex ids.
	 */
	static int[][] sweep(Mesh m, double[][] p, double[][] up, double[] ra, double[] rb, int sides, int[] lab, Col col) {
		int n = p.length;
		int[][] ring = new int[n][sides];
		for (int i = 0; i < n; i++) {
			double[] t = norm(sub(p[Math.min(i + 1, n - 1)], p[Math.max(i - 1, 0)]));
			double[] u = norm(sub(up[i], scale(t, dot(up[i], t))));
			double[] w = ModelData.cross(t, u);
			if (ra[i] <= 0 && rb[i] <= 0) {
				int tip = m.v(p[i], lab[i]);
				java.util.Arrays.fill(ring[i], tip);
				continue;
			}
			for (int k = 0; k < sides; k++) {
				double a = Math.PI * 2 * (k + 0.5) / sides;
				ring[i][k] = m.v(add(p[i], add(scale(u, Math.cos(a) * ra[i]), scale(w, Math.sin(a) * rb[i]))), lab[i]);
			}
		}
		for (int i = 0; i + 1 < n; i++) {
			double[] mid = lerp(p[i], p[i + 1], 0.5);
			for (int k = 0; k < sides; k++) {
				int k2 = (k + 1) % sides;
				double a = Math.PI * 2 * (k + 1) / sides;
				int c = col.at(i, Math.cos(a), Math.sin(a));
				int a0 = ring[i][k], a1 = ring[i][k2], b0 = ring[i + 1][k], b1 = ring[i + 1][k2];
				if (b0 == b1) {
					m.triAway(a0, a1, b0, c, mid);
				} else if (a0 == a1) {
					m.triAway(a0, b1, b0, c, mid);
				} else {
					m.quadAway(a0, a1, b1, b0, c, mid);
				}
			}
		}
		return ring;
	}

	/** Closes a ring with a flat fan to its centre, facing {@code out}. */
	static void cap(Mesh m, int[] ring, int label, int col, double[] out) {
		double[] c = new double[3];
		for (int v : ring) {
			c = add(c, m.p(v));
		}
		int ci = m.v(scale(c, 1.0 / ring.length), label);
		for (int k = 0; k < ring.length; k++) {
			m.tri(ring[k], ring[(k + 1) % ring.length], ci, col, out);
		}
	}

	static double lerp(double a, double b, double t) {
		return a + (b - a) * t;
	}

	static double[] lerp(double[] a, double[] b, double t) {
		return BaseCoat.lerp(a, b, t);
	}

	/** Catmull-Rom through (x, y) keys (x increasing): the value at x. */
	static double spline(double[][] keys, double x) {
		int n = keys.length;
		if (x <= keys[0][0]) {
			return keys[0][1];
		}
		if (x >= keys[n - 1][0]) {
			return keys[n - 1][1];
		}
		int i = 0;
		while (keys[i + 1][0] < x) {
			i++;
		}
		double x0 = keys[i][0], x1 = keys[i + 1][0], t = (x - x0) / (x1 - x0);
		double p0 = keys[Math.max(i - 1, 0)][1], p1 = keys[i][1], p2 = keys[i + 1][1], p3 = keys[Math.min(i + 2, n - 1)][1];
		return 0.5 * (2 * p1 + (-p0 + p2) * t + (2 * p0 - 5 * p1 + 4 * p2 - p3) * t * t + (-p0 + 3 * p1 - 3 * p2 + p3) * t * t * t);
	}

	/**
	 * The trunk's section at height y, grown by {@code margin}: {cz, rx, rz}. The torso kit's down to the waist; below it
	 * the larger of the waist's and the hips' (from the legs kit), so whatever wraps the hips clears both.
	 */
	static double[] trunk(Rig r, double y, double margin) {
		double[] t = BaseCoat.torsoRing(r, y, margin, 0);
		if (y <= r.waistY) {
			return t;
		}
		double[] s = BaseCoat.slice(r.legs, y, 0);
		double rx = Math.max(t[1], Math.max(-s[0], s[1]) + margin);
		double zF = Math.min(t[0] - t[2], s[2] - margin), zB = Math.max(t[0] + t[2], s[3] + margin);
		return new double[] { (zF + zB) / 2, rx, (zB - zF) / 2 };
	}

	/** The stock cape's chain at height y (waist 9, thigh 14, knee 13, hem 12; no stock cape has the 16 between): the row nearest y. */
	static int capeLabel(BaseMantle.Body b, double y) {
		double[] ys = { b.capeY[1], b.capeY[2], b.capeY[3], b.capeY[4] };
		int[] labs = { BaseMantle.CAPE_WAIST, BaseMantle.CAPE_THIGH, BaseMantle.CAPE_KNEE, BaseMantle.CAPE_HEM };
		int best = 0;
		for (int i = 1; i < ys.length; i++) {
			if (Math.abs(y - ys[i]) < Math.abs(y - ys[best])) {
				best = i;
			}
		}
		return labs[best];
	}

	/** The serpent's hide by a face's direction: dorsal blotches on top, the flanks, pale ventral scutes underneath. */
	static int hide(int seg, double up) {
		boolean even = seg % 2 == 0;
		if (up > 0.6) {
			return even ? HIDE : HIDE_DARK;
		}
		if (up > -0.3) {
			return even ? HIDE_LIGHT : HIDE_TOP;
		}
		return even ? BELLY : BELLY_2;
	}

	/* ====================================================================== */
	/*                                THE HELM                                */
	/* ====================================================================== */

	/**
	 * The serpent's head over the wearer's: the upper head is a shell of arched sections from the nape to the snout (over
	 * the crown, out past the brow), lined in the mouth's red; the lower jaw a U under the chin; fangs, eyes, horns and the
	 * neck down the back. Laid out on the head kit's own box, so it fits either body.
	 */
	static ModelData helm(Rig r, BaseMantle.Body b) {
		Mesh m = new Mesh();
		final int L = 1;
		int[] hb = r.headB;
		double T = hb[1], C = b.headChin, F = hb[2], B = hb[5], HW = hb[3];
		double H = C - T, D = B - F;
		// the sections, back to snout: u (0 back .. 1 tip), z, half-width, top, lip
		double[][] sec = { { 0.0, B + 0.30 * D, HW * 1.02, T + 0.10 * H, C + 0.10 * H }, { 0.18, B - 0.05 * D, HW * 1.20, T - 0.22 * H, T + 0.80 * H },
				{ 0.36, F + 0.55 * D, HW * 1.30, T - 0.26 * H, T + 0.62 * H }, { 0.55, F + 0.18 * D, HW * 1.18, T - 0.24 * H, T + 0.40 * H },
				{ 0.72, F - 0.22 * D, HW * 0.92, T - 0.04 * H, T + 0.36 * H }, { 0.88, F - 0.52 * D, HW * 0.64, T + 0.10 * H, T + 0.36 * H },
				{ 1.0, F - 0.70 * D, HW * 0.34, T + 0.20 * H, T + 0.34 * H } };
		int arcs = 6; // quads across each section, -90 to 90 degrees
		int S = sec.length;
		int[][] out = new int[S][arcs + 1], in = new int[S][arcs + 1];
		double[][] axis = new double[S][];
		for (int s = 0; s < S; s++) {
			double z = sec[s][1], hw = sec[s][2], top = sec[s][3], lip = sec[s][4];
			axis[s] = pt(0, lip, z);
			for (int k = 0; k <= arcs; k++) {
				double t = -Math.PI / 2 + Math.PI * k / arcs;
				double x = hw * Math.sin(t), y = lip - (lip - top) * Math.cos(t);
				out[s][k] = m.v(pt(x, y, z), L);
				in[s][k] = m.v(pt(x * 0.86, y + (lip - y) * 0.12 + 0.6, z), L);
			}
		}
		m.prio = 6;
		for (int s = 0; s + 1 < S; s++) {
			double[] c = lerp(axis[s], axis[s + 1], 0.5);
			for (int k = 0; k < arcs; k++) {
				double t = -Math.PI / 2 + Math.PI * (k + 0.5) / arcs;
				int col = hide(s, Math.cos(t) * 1.6 - 0.2);
				if (Math.abs(t) < 0.3) {
					col = s % 2 == 0 ? HIDE_DARK : HIDE_TOP; // the dorsal stripe down the crown
				}
				m.quadAway(out[s][k], out[s][k + 1], out[s + 1][k + 1], out[s + 1][k], col, c);
			}
			if (s >= 3) { // the mouth's lining, seen through the gape (it faces in, towards the axis)
				for (int k = 0; k < arcs; k++) {
					double[] q = scale(add(add(m.p(in[s][k]), m.p(in[s][k + 1])), add(m.p(in[s + 1][k]), m.p(in[s + 1][k + 1]))), 0.25);
					m.quad(in[s][k], in[s][k + 1], in[s + 1][k + 1], in[s + 1][k], s >= 3 ? MOUTH : MOUTH_DARK, sub(c, q));
				}
			}
			for (int k : new int[] { 0, arcs }) { // the lip: the upper jaw's edge, bone white
				m.quad(out[s][k], out[s + 1][k], in[s + 1][k], in[s][k], s >= 2 ? BONE_DARK : HIDE_2, pt(k == 0 ? -0.3 : 0.3, 1, 0));
			}
		}
		// the snout's tip, closed over and under
		int nose = m.v(pt(0, sec[S - 1][3] + 1.2, sec[S - 1][1] - 2.5), L);
		for (int k = 0; k < arcs; k++) {
			m.triAway(out[S - 1][k], out[S - 1][k + 1], nose, HIDE_TOP, axis[S - 2]);
			m.tri(in[S - 1][k], in[S - 1][k + 1], nose, MOUTH, pt(0, 1, 0.3));
		}
		// the back of the skull closed down to the nape
		int nape = m.v(pt(0, sec[0][4] - 2, sec[0][1] + 2.5), L);
		for (int k = 0; k < arcs; k++) {
			m.triAway(out[0][k], out[0][k + 1], nape, k == arcs / 2 - 1 || k == arcs / 2 ? HIDE_DARK : HIDE_2, pt(0, (sec[0][3] + sec[0][4]) / 2, sec[1][1]));
		}
		// the neck down the back of the head to the nape, under the skull's back
		{
			double[][] p = { pt(0, T + 0.45 * H, B - 2), pt(0, C + 0.05 * H, B + 3.5), pt(0, r.neckY + 4, B + 5.5) };
			double[][] up = { pt(0, -0.2, 1), pt(0, -0.2, 1), pt(0, -0.3, 1) };
			double[] ra = { HW * 0.75, HW * 0.62, HW * 0.55 }, rb = { HW * 0.9, HW * 0.8, HW * 0.72 };
			int[][] nr = sweep(m, p, up, ra, rb, 6, new int[] { L, L, L }, (i, up2, sd) -> hide(i + 1, up2));
			cap(m, nr[2], L, BELLY_3, pt(0, 1, 0));
		}
		// the lower jaw: a U under the chin from hinge to hinge, chin scales outside, mouth inside, fangs up at the front
		{
			double[] hinge = pt(HW * 1.15, T + 0.78 * H, F + 0.62 * D);
			double front = F - 0.42 * D, wz = hinge[2] - front;
			double floorY = C + 0.30 * H, tipY = C + 0.12 * H;
			int st = 7;
			int[][] js = new int[st][5];
			double[][] jc = new double[st][];
			for (int i = 0; i < st; i++) {
				double a = -Math.PI / 2 + Math.PI * i / (st - 1); // -90 right hinge .. 90 left hinge
				double sa = Math.sin(a), ca = Math.cos(a);
				double x = hinge[0] * sa, z = hinge[2] - wz * ca;
				double y = lerp(hinge[1], floorY, Math.pow(ca, 0.7));
				if (Math.abs(sa) < 0.3) {
					y = lerp(y, tipY, 1 - Math.abs(sa) / 0.3); // the chin's tip lifts a little
				}
				double[] o = norm(pt(sa, 0, -ca * 0.9)); // outward in plan
				double[] P = pt(x, y, z);
				jc[i] = P;
				double h = 3.6 + 1.6 * ca;
				js[i][0] = m.v(add(P, scale(o, 0.4)), L); // the top of the lip
				js[i][1] = m.v(add(add(P, scale(o, 1.2)), pt(0, h * 0.5, 0)), L);
				js[i][2] = m.v(add(add(P, scale(o, 0.3)), pt(0, h, 0)), L); // under the jaw
				js[i][3] = m.v(add(add(P, scale(o, -2.2)), pt(0, h * 0.8, 0)), L);
				js[i][4] = m.v(add(P, scale(o, -2.0)), L);
			}
			m.prio = 6;
			int[] cols = { HIDE_2, BELLY, BELLY_2, MOUTH, MOUTH_DARK };
			for (int i = 0; i + 1 < st; i++) {
				double[] mid = lerp(jc[i], jc[i + 1], 0.5);
				double[] o = norm(sub(pt(mid[0], mid[1], mid[2]), pt(0, mid[1], hinge[2])));
				for (int k = 0; k < 5; k++) {
					int k2 = (k + 1) % 5;
					double[] q = scale(add(add(m.p(js[i][k]), m.p(js[i][k2])), add(m.p(js[i + 1][k]), m.p(js[i + 1][k2]))), 0.25);
					double[] inside = add(lerp(m.p(js[i][0]), m.p(js[i][2]), 0.5), scale(o, -0.9));
					m.quad(js[i][k], js[i][k2], js[i + 1][k2], js[i + 1][k], cols[k] == BELLY && i % 2 == 1 ? BELLY_3 : cols[k], sub(q, inside));
				}
			}
			for (int e : new int[] { 0, st - 1 }) { // the hinge ends
				m.prio = 6;
				for (int k = 1; k + 1 < 5; k++) {
					m.tri(js[e][0], js[e][k], js[e][k + 1], HIDE_TOP, pt(0, 0, 1));
				}
			}
			// lower fangs, pointing up from the front of the jaw
			m.prio = 7;
			for (int s = -1; s <= 1; s += 2) {
				int i = s < 0 ? 2 : st - 3;
				double[] base = add(jc[i], pt(0, 0.4, 0));
				ModelData lf = new ModelData();
				ArmourForge.blade(lf, add(base, pt(0, 1.0, 0)), add(base, pt(-s * 0.5, -5.0, -0.6)), pt(1, 0, 0), 1.0, 0.8, BONE);
				absorb(m, lf, 7, L);
			}
		}
		// the upper fangs: long, from the front of the upper jaw, hanging in front of the face
		m.prio = 7;
		for (int s = -1; s <= 1; s += 2) {
			double[] root = pt(s * sec[4][2] * 0.78, sec[4][4] + 0.2, sec[4][1] + 1.0);
			fang(m, root, add(root, pt(-s * 0.6, 0.42 * H, 1.6)), 1.5, L);
		}
		// the eyes: venom-yellow, slanted, bulging from the brow; a bone ridge over each
		for (int s = -1; s <= 1; s += 2) {
			double[] c = pt(s * sec[3][2] * 0.80, lerp(sec[3][4], sec[3][3], 0.55), sec[3][1] + 1.5);
			double[] o = norm(pt(s * 1.0, -0.35, -0.55));
			eye(m, c, o, 1.35, L);
			// a short bone horn swept back from over the eye
			double[] h0 = add(c, pt(-s * 1.0, -3.2, 3.0));
			ModelData horn = new ModelData();
			horn.skin = L;
			ArmourForge.blade(horn, h0, add(h0, pt(s * 3.5, -5.5, 12)), pt(s, 0, 0), 2.2, 1.4, BONE);
			absorb(m, horn, 7, L);
		}
		// the hood: a cobra's frill flaring from behind the jaw hinges to the shoulders, framing the face; ribbed bone-pale in
		// front, the dark hide behind
		m.prio = 5;
		int rows = 4;
		for (int s = -1; s <= 1; s += 2) {
			int[] inner = new int[rows + 1], outer = new int[rows + 1];
			for (int k = 0; k <= rows; k++) {
				double v = (double) k / rows, bulge = Math.sin(Math.PI * Math.min(1, v * 1.15));
				double y = lerp(T + 0.10 * H, C + 0.28 * H, v);
				inner[k] = m.v(pt(s * HW * 0.92, y, F + 0.80 * D + 2.0 * v), L);
				outer[k] = m.v(pt(s * HW * (1.05 + 0.85 * bulge), y + 1.5 * bulge, B + 0.5 + 3.0 * v - 1.5 * bulge), L);
			}
			for (int k = 0; k < rows; k++) {
				m.quad(inner[k], outer[k], outer[k + 1], inner[k + 1], k % 2 == 0 ? BELLY_3 : BONE_DARK, pt(0, 0, -1));
				m.quad(inner[k], outer[k], outer[k + 1], inner[k + 1], k == 1 || k == 2 ? HIDE_DARK : HIDE_TOP, pt(0, 0, 1));
			}
		}
		return m.d;
	}

	/** A curved fang from root to tip (two tapering segments, bending forward), bone. */
	static void fang(Mesh m, double[] root, double[] tip, double w, int label) {
		double[] d = sub(tip, root);
		double[] mid = add(lerp(root, tip, 0.5), scale(norm(pt(d[0], 0, -Math.abs(d[1]) * 0.15 - 0.4)), 0.6));
		ModelData f = new ModelData();
		f.skin = label;
		double[] across = pt(1, 0, 0);
		ArmourForge.blade(f, add(root, scale(norm(d), -1.0)), mid, across, w, w * 0.8, BONE);
		ArmourForge.blade(f, mid, tip, across, w * 0.7, w * 0.55, BONE_DARK);
		absorb(m, f, m.prio, label);
	}

	/** A slanted venom-yellow eye: a low diamond bulge facing {@code o}, its slit dark. */
	static void eye(Mesh m, double[] c, double[] o, double sz, int label) {
		double[] a = norm(sub(pt(0, 0, -1), scale(o, dot(pt(0, 0, -1), o)))); // along the eye, towards the snout
		double[] b = ModelData.cross(o, a);
		double[][] rim = { add(c, scale(a, 3.4 * sz)), add(c, add(scale(b, 1.7 * sz), scale(a, -0.3 * sz))), add(c, scale(a, -2.6 * sz)), add(c, add(scale(b, -1.7 * sz), scale(a, -0.3 * sz))) };
		int[] ri = new int[4];
		for (int k = 0; k < 4; k++) {
			ri[k] = m.v(rim[k], label);
		}
		int apex = m.v(add(c, add(scale(o, 1.8 * sz), scale(a, 0.4 * sz))), label);
		m.prio = 8;
		for (int k = 0; k < 4; k++) {
			m.triAway(ri[k], ri[(k + 1) % 4], apex, k % 2 == 0 ? VENOM : VENOM_DARK, add(c, scale(o, -3)));
		}
		// the slit: a thin dark sliver across the bulge
		int s0 = m.v(add(add(c, scale(o, 1.95 * sz)), add(scale(b, 1.3 * sz), scale(a, 0.4 * sz))), label), s1 = m.v(add(add(c, scale(o, 1.95 * sz)), add(scale(b, -1.3 * sz), scale(a, 0.4 * sz))), label);
		int s2 = m.v(add(c, add(scale(o, 2.1 * sz), scale(a, 0.9 * sz))), label);
		m.prio = 9;
		m.tri(s0, s1, s2, MOUTH_DARK, o);
	}

	/** Appends a scratch part to the mesh: every vertex on {@code label}, priority {@code prio}, smooth shading. */
	static void absorb(Mesh m, ModelData part, int prio, int label) {
		part.vertexSkins.replaceAll(x -> label);
		part.priorities.replaceAll(x -> prio);
		part.renderTypes.replaceAll(x -> 0);
		ArmourForge.merge(m.d, part, null);
	}

	/* ====================================================================== */
	/*                           THE BODY: SHIRT, COILS                       */
	/* ====================================================================== */

	/** The scale shirt, and the serpent coiled round it with its tail hanging behind. */
	static ModelData body(Rig r, BaseMantle.Body b) {
		Mesh m = new Mesh();
		shirt(m, r);
		coils(m, r, b, true);
		return m.d;
	}

	/** Belly label below this height, chest above. */
	static int trunkLabel(Rig r, double y) {
		return y < r.neckY + 0.45 * (r.waistY - r.neckY) ? 8 : 4;
	}

	/** A scale shirt from the neck to the hips: rows of scales, each row's lower edge scalloped. */
	static void shirt(Mesh m, Rig r) {
		int n = 10;
		double top = r.neckY + 1, hem = r.hip[1] - 4;
		int rows = 6;
		int[][] ring = new int[rows + 1][n];
		double[][] dim = new double[rows + 1][];
		for (int i = 0; i <= rows; i++) {
			double y = lerp(top, hem, (double) i / rows);
			double[] t = trunk(r, y, 1.0 + 0.15 * i);
			if (i == 0) {
				t[1] = Math.min(t[1], 10.5);
				t[2] = Math.min(t[2], 9);
			}
			dim[i] = t;
			for (int j = 0; j < n; j++) {
				double sc = i > 0 && j % 2 == 1 ? 2.2 : 0; // the scallops: every other point a little lower and further out
				double yy = Math.min(y + sc, hem + 1);
				ring[i][j] = m.v(ell(0, yy, t[0], t[1] + sc * 0.3, t[2] + sc * 0.3, Math.PI * 2 * (j + (i % 2) * 0.5) / n), trunkLabel(r, y));
			}
		}
		m.prio = 2;
		for (int i = 0; i < rows; i++) {
			double[] c = pt(0, lerp(top, hem, (i + 0.5) / rows), (dim[i][0] + dim[i + 1][0]) / 2);
			int[] cols = { i % 2 == 0 ? HIDE_DARK : HIDE_TOP, i % 2 == 0 ? HIDE_DARK : HIDE_TOP };
			for (int j = 0; j < n; j++) {
				int j2 = (j + 1) % n;
				m.quadAway(ring[i][j], ring[i][j2], ring[i + 1][j2], ring[i + 1][j], cols[j % 2], c);
			}
		}
	}

	/**
	 * The serpent: from the nape (where the helm's neck comes down) it lies round the base of the neck as a yoke on the
	 * shoulders, then winds down the trunk in two thick coils, chest and belt, and its tail drops from the back of the
	 * belt to the calves. Thin where it passes under the arms; thickest round the chest.
	 */
	static void coils(Mesh m, Rig r, BaseMantle.Body b, boolean withTail) {
		double neck = r.neckY, hipY = r.hip[1] - 6;
		// height (as a fraction from the neck to above the hips) by turn: phi/pi
		double[][] yk = { { -0.6, -0.16 }, { 0, -0.03 }, { 0.5, -0.015 }, { 1, 0.035 }, { 1.5, 0.07 }, { 2, 0.27 }, { 2.5, 0.42 }, { 3, 0.56 }, { 3.5, 0.67 },
				{ 4, 0.76 }, { 4.5, 0.84 }, { 5, 0.91 }, { 5.5, 0.965 }, { 5.8, 0.985 } };
		double[][] rk = { { -0.6, 4.2 }, { 0, 5.2 }, { 1.5, 6.2 }, { 2.5, 7.6 }, { 4.5, 7.4 }, { 5.8, 6.2 } };
		// the yoke round the base of the neck
		double[] ys = BaseCoat.slice(r.torso, neck + 8, 0);
		double yrx = Math.max(-ys[0], ys[1]) * 0.74, ycz = (ys[2] + ys[3]) / 2, yrz = (ys[3] - ys[2]) / 2 - 0.5;
		java.util.List<double[]> P = new java.util.ArrayList<>(), U = new java.util.ArrayList<>();
		java.util.List<Double> RA = new java.util.ArrayList<>(), RB = new java.util.ArrayList<>();
		java.util.List<Integer> LB = new java.util.ArrayList<>();
		double step = Math.PI / 5;
		for (double phi = -0.6 * Math.PI; phi <= 5.8 * Math.PI + 1e-6; phi += step) {
			double f = spline(yk, phi / Math.PI), y = neck + f * (hipY - neck);
			double R = spline(rk, phi / Math.PI);
			double psi = Math.PI - phi; // the back, then the left, the front, the right...
			double cs = Math.cos(psi), sn = Math.sin(psi);
			double side = sn * sn; // 1 at the sides
			boolean underArm = y > r.armTop + 6 && y < r.wristY + 2;
			double ra = R * (underArm ? 1 - 0.6 * side : 1); // flattened against the trunk under the arms
			double[] t = trunk(r, y, ra + 0.6);
			double cz = t[0], rx = t[1], rz = t[2];
			double blend = Math.max(0, Math.min(1, (phi / Math.PI - 1.3) / 0.7)); // 0: on the yoke, 1: on the trunk
			blend = blend * blend * (3 - 2 * blend);
			double[] yoke = { ycz, yrx + R * 0.15, yrz + R * 0.6 };
			if (phi < 0) { // the neck rising behind, towards the head
				double g = -phi / (0.6 * Math.PI);
				P.add(pt(0, y, lerp(ycz + yoke[2], r.headB[5] + 4, g)));
				U.add(norm(pt(0, -0.4, 1)));
				RA.add(R * 0.95);
				RB.add(R * 1.15);
				LB.add(8);
				continue;
			}
			double[] c = lerp(ell(0, y, yoke[0], yoke[1], yoke[2], psi), ell(0, y, cz, rx, rz, psi), blend);
			double[] outw = norm(pt(sn, 0, -cs));
			U.add(norm(add(outw, pt(0, -0.75, 0))));
			P.add(c);
			RA.add(ra);
			RB.add(R);
			LB.add(trunkLabel(r, y));
		}
		int coilN = P.size();
		// the tail: from the last turn (back right) round to the middle of the back, then down behind the legs to the calves
		double[] last = P.get(coilN - 1);
		double zb = b.hipsBackZ;
		double[][] tail = { pt(last[0] * 0.45, last[1] + 3.5, zb + 5.0), pt(-1.5, lerp(last[1], b.capeY[2], 0.33), zb + 6.5), pt(0.5, lerp(last[1], b.capeY[2], 0.7), zb + 7.5),
				pt(1.5, b.capeY[2] + 2, zb + 8.0), pt(2.5, lerp(b.capeY[2], b.capeY[3], 0.6), zb + 8.5), pt(4.5, b.capeY[3] + 3.5, zb + 10.0),
				pt(6.5, b.capeY[4] + 5.5, zb + 13.5), pt(8.0, b.capeY[4] + 2.5, zb + 18.5), pt(8.5, b.capeY[4] - 3.5, zb + 21.0) };
		double[] tr = { 5.2, 4.8, 4.3, 3.8, 3.2, 2.6, 2.0, 1.4, 0 };
		for (int i = 0; i < (withTail ? tail.length : 0); i++) {
			P.add(tail[i]);
			double[] d = sub(tail[Math.min(i + 1, tail.length - 1)], tail[Math.max(i - 1, 0)]);
			U.add(norm(sub(pt(0, -0.2, 1), scale(norm(d), dot(pt(0, -0.2, 1), norm(d))))));
			RA.add(tr[i]);
			RB.add(tr[i] * 1.1);
			LB.add(capeLabel(b, tail[i][1]));
		}
		int n = P.size();
		double[][] p = P.toArray(new double[0][]), up = U.toArray(new double[0][]);
		double[] ra = new double[n], rb = new double[n];
		int[] lab = new int[n];
		for (int i = 0; i < n; i++) {
			ra[i] = RA.get(i);
			rb[i] = RB.get(i);
			lab[i] = LB.get(i);
		}
		// the kings' bronze ring grown into the hide: two segments, on the front of the chest coil
		int ring1 = (int) Math.round((3.0 + 0.6) * Math.PI / step), ring2 = (int) Math.round((5.0 - 0.6 + 0.6) * Math.PI / step);
		for (int k : new int[] { ring1, ring1 + 1, ring2, ring2 + 1 }) {
			if (k < coilN) {
				ra[k] *= 1.12;
				rb[k] *= 1.12;
			}
		}
		m.prio = 4;
		final int r1 = ring1, r2 = ring2;
		int[][] rings = sweep(m, p, up, ra, rb, 6, lab, (i, u, s) -> i == r1 || i == r2 ? (u > 0 ? BRONZE : VERDIGRIS) : hide(i, u));
		cap(m, rings[0], 8, HIDE_TOP, pt(0, -1, 0.3));
		if (!withTail) {
			cap(m, rings[n - 1], 4, HIDE_TOP, sub(p[n - 1], p[n - 2]));
		}
	}

	/** Scaled sleeves (the body's second model): shoulder to wrist, banded, a pale cuff. */
	static ModelData sleeves(Rig r) {
		Mesh m = new Mesh();
		m.prio = 10;
		int n = 6;
		for (int side = -1; side <= 1; side += 2) {
			int up = side > 0 ? 26 : 20, fo = side > 0 ? 22 : 19;
			double[] J = mirror(pt(r.shoulder[0], r.armTop + 4, r.shoulder[2]), side), E = mirror(r.elbow, side), W = mirror(pt(r.wrist[0], r.wristY, r.wrist[2]), side);
			double armR = Math.max(5, (BaseCoat.extent(r.arms, 21)[3] - BaseCoat.extent(r.arms, 21)[0]) / 2);
			double foreR = Math.max(4, (BaseCoat.extent(r.arms, 19)[3] - BaseCoat.extent(r.arms, 19)[0]) / 2);
			double[] Au = norm(sub(E, J)), Af = norm(sub(W, E));
			double[][] p = { add(J, scale(Au, -1.5)), lerp(J, E, 0.45), add(E, scale(Au, -2)), add(E, scale(Af, 2)), add(W, scale(Af, -3.0)), W };
			double[] rad = { armR + 0.8, armR + 0.6, foreR + 1.8, foreR + 1.8, foreR + 1.6, foreR + 2.4 };
			int[] lab = { up, up, up, fo, fo, fo };
			double[][] u = new double[p.length][];
			for (int i = 0; i < p.length; i++) {
				u[i] = pt(side, -0.2, 0);
			}
			sweep(m, p, u, rad, rad, n, lab, (i, c, s) -> i == p.length - 2 ? (c > -0.2 ? BELLY : BELLY_2) : i % 2 == 0 ? HIDE_DARK : HIDE_TOP);
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                                THE LEGS                                */
	/* ====================================================================== */

	/** One leg's ring at height y: {cx, cz, rx, rz}. */
	static double[] leg(Rig r, double y, int side, double margin) {
		return BaseCoat.legRing(r, y, side, margin);
	}

	/**
	 * Banded segmented greaves: a waistband, then each leg a stack of short flared bands, each overlapping the one below
	 * like a serpent's segments, the front face of each a pale ventral plate; a bronze ring at the knee.
	 */
	static ModelData greaves(Rig r) {
		Mesh m = new Mesh();
		int n = 6;
		// the waistband round both legs, on the static belt label
		{
			int wn = 10;
			double[] s = BaseCoat.slice(r.legs, r.waistY + 2, 0);
			double cz = (s[2] + s[3]) / 2, rx = Math.max(-s[0], s[1]) + 2.2, rz = (s[3] - s[2]) / 2 + 2.2;
			int[] w0 = new int[wn], w1 = new int[wn];
			for (int j = 0; j < wn; j++) {
				double phi = Math.PI * 2 * j / wn;
				w0[j] = m.v(ell(0, r.waistY - 1.5, cz, rx, rz, phi), 41);
				w1[j] = m.v(ell(0, r.waistY + 6, cz, rx + 0.8, rz + 0.8, phi), 41);
			}
			m.prio = 2;
			m.band(w0, w1, true, new int[] { HIDE_DARK, HIDE_TOP }, pt(0, r.waistY + 2, cz));
		}
		for (int side = -1; side <= 1; side += 2) {
			int thigh = side > 0 ? 43 : 44, shin = side > 0 ? 36 : 33, ank = side > 0 ? 38 : 32;
			double y0 = r.waistY + 4, kneeY = r.knee[1], y1 = r.ankle[1] + 2;
			// band tops and bottoms: four on the thigh, the knee ring, four on the shin
			java.util.List<double[]> bands = new java.util.ArrayList<>(); // top, bottom, label, kind (0 plain, 1 knee)
			int thighBands = 4, shinBands = 4;
			double kt = kneeY - 6, kb = kneeY + 3;
			for (int k = 0; k < thighBands; k++) {
				double a = lerp(y0, kt, (double) k / thighBands), bb = lerp(y0, kt, (k + 1.0) / thighBands) + 2.5;
				bands.add(new double[] { a, bb, thigh, 0 });
			}
			bands.add(new double[] { kt, kb + 1.5, shin, 1 });
			for (int k = 0; k < shinBands; k++) {
				double a = lerp(kb, y1, (double) k / shinBands), bb = lerp(kb, y1, (k + 1.0) / shinBands) + 2.5;
				bands.add(new double[] { a, Math.min(bb, y1 + 1), k == shinBands - 1 ? ank : shin, 0 });
			}
			int idx = 0;
			for (double[] bd : bands) {
				double top = bd[0], bot = bd[1];
				int lab = (int) bd[2];
				boolean knee = bd[3] == 1;
				double[] qt = leg(r, Math.max(top, r.waistY + 2), side, 1.0 + (knee ? 1.0 : 0));
				double[] qb = leg(r, Math.min(bot, r.ankle[1]), side, 2.4 + (knee ? 1.2 : 0));
				if (top < r.hip[1]) { // the top of the thigh: from the middle out to the hip
					qt[0] = side * Math.max(Math.abs(qt[0]), 8.0);
					qt[2] = Math.max(qt[2], 8.5);
				}
				if (bot < r.hip[1] + 6) {
					qb[0] = side * Math.max(Math.abs(qb[0]), 8.0);
					qb[2] = Math.max(qb[2], 8.5);
				}
				int[] t0 = new int[n], b0 = new int[n];
				for (int j = 0; j < n; j++) {
					double phi = Math.PI * 2 * j / n - Math.PI / n;
					t0[j] = m.v(ell(qt[0], top, qt[1], qt[2], qt[3], phi), lab);
					b0[j] = m.v(ell(qb[0], bot, qb[1], qb[2], qb[3], phi), lab);
				}
				double[] in = pt((qt[0] + qb[0]) / 2, (top + bot) / 2, (qt[1] + qb[1]) / 2);
				m.prio = 1 + (idx % 2);
				for (int j = 0; j < n; j++) {
					int j2 = (j + 1) % n;
					int col;
					if (knee) {
						col = j == 0 ? BRONZE : j % 2 == 0 ? VERDIGRIS : BRONZE_DARK;
					} else if (j == 0) {
						col = idx % 2 == 0 ? HIDE_LIGHT : HIDE; // the ventral plate down the front
					} else {
						col = idx % 2 == 0 ? (j == 3 ? HIDE_TOP : HIDE) : (j == 3 ? HIDE_DARK : HIDE_TOP);
						if (j == 2 || j == 4) {
							col = idx % 2 == 0 ? HIDE_TOP : HIDE_DARK;
						}
					}
					m.quadAway(t0[j], t0[j2], b0[j2], b0[j], col, in);
				}
				idx++;
			}
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                             BOOTS, GAUNTLETS                           */
	/* ====================================================================== */

	/** Scaled boots: two overlapping bands up the shin, a scaled foot and a pointed bone toe. */
	static ModelData boots(Rig r) {
		Mesh m = new Mesh();
		int n = 6;
		int[] fb = r.feetB;
		for (int side = -1; side <= 1; side += 2) {
			int ank = side > 0 ? 38 : 32, toe = side > 0 ? 46 : 45;
			double ankY = r.ankle[1];
			double[][] bands = { { ankY - 12, ankY + 1, 2.6, 3.6 }, { ankY - 3, fb[4] - 5.5, 2.6, 3.4 } };
			for (int k = 0; k < bands.length; k++) {
				double[] qt = leg(r, Math.max(bands[k][0], r.knee[1] + 4), side, bands[k][2]), qb = leg(r, Math.min(bands[k][1], ankY), side, bands[k][3]);
				if (k == 1) {
					qb[2] += 0.6;
					qb[3] += 1.4;
					qb[1] += 0.8;
				}
				int[] t0 = new int[n], b0 = new int[n];
				for (int j = 0; j < n; j++) {
					double phi = Math.PI * 2 * j / n - Math.PI / n;
					t0[j] = m.v(ell(qt[0], bands[k][0], qt[1], qt[2], qt[3], phi), ank);
					b0[j] = m.v(ell(qb[0], bands[k][1], qb[1], qb[2], qb[3], phi), ank);
				}
				double[] in = pt((qt[0] + qb[0]) / 2, (bands[k][0] + bands[k][1]) / 2, (qt[1] + qb[1]) / 2);
				m.prio = 2 + k;
				for (int j = 0; j < n; j++) {
					int j2 = (j + 1) % n;
					int col = j == 0 ? (k == 0 ? BELLY : BELLY_2) : k == 0 ? (j % 2 == 0 ? HIDE_TOP : HIDE) : (j % 2 == 0 ? HIDE_2 : HIDE_DARK);
					m.quadAway(t0[j], t0[j2], b0[j2], b0[j], col, in);
				}
			}
			// the foot: heel, instep, ball, and a pointed toe of bone
			double[] fe = BaseCoat.extent(r.feet, side > 0 ? 47 : 48);
			double x = (fe[0] + fe[3]) / 2, hw = (fe[3] - fe[0]) / 2;
			double sole = fb[4] + 0.5, back = fb[5] + 1.0, front = fb[2] - 1.5;
			double[][] sec = { { back, -7.5, hw * 0.9, ank }, { back - 9, -10.0, hw + 0.9, ank }, { front + 10, -5.0, hw + 1.0, toe }, { front + 3, -2.6, hw * 0.6, toe } };
			int[][] sx = new int[sec.length][];
			for (int k = 0; k < sec.length; k++) {
				double z = sec[k][0], topY = sole + sec[k][1], hwk = sec[k][2];
				int lab = (int) sec[k][3];
				sx[k] = new int[] { m.v(pt(x - hwk, sole, z), lab), m.v(pt(x + hwk, sole, z), lab), m.v(pt(x + hwk * 0.95, topY + 1.8, z), lab), m.v(pt(x, topY, z), lab),
						m.v(pt(x - hwk * 0.95, topY + 1.8, z), lab) };
			}
			int tip = m.v(pt(x, sole - 1.6, front - 4.5), toe);
			m.prio = 2;
			for (int k = 0; k + 1 < sec.length; k++) {
				double[] cin = pt(x, sole - 2, (sec[k][0] + sec[k + 1][0]) / 2);
				for (int e = 0; e < 5; e++) {
					int e2 = (e + 1) % 5;
					int col = e == 0 ? HIDE_DARK : k == sec.length - 2 ? BONE_DARK : (e == 3 || e == 2) ? (k % 2 == 0 ? HIDE_TOP : HIDE) : HIDE_2;
					m.quadAway(sx[k][e], sx[k][e2], sx[k + 1][e2], sx[k + 1][e], col, cin);
				}
			}
			int last = sec.length - 1;
			for (int e = 0; e < 5; e++) {
				m.triAway(sx[last][e], sx[last][(e + 1) % 5], tip, e == 3 || e == 2 ? BONE : BONE_DARK, pt(x, sole - 2, sec[last][0] + 3));
			}
			for (int e = 1; e + 1 < 5; e++) {
				m.tri(sx[0][0], sx[0][e], sx[0][e + 1], HIDE_DARK, pt(0, 0, 1));
			}
		}
		return m.d;
	}

	/** Scaled gauntlets: a banded cuff up the forearm, a plated hand, and four long curved fang claws. */
	static ModelData gauntlets(Rig r) {
		Mesh m = new Mesh();
		for (int side = -1; side <= 1; side += 2) {
			int hand = side > 0 ? 28 : 27, fore = side > 0 ? 22 : 19;
			double[] W = mirror(BaseCoat.centroid(r.hands, 19), side), H = mirror(r.hand, side), E = mirror(r.elbow, side);
			double[] he = BaseCoat.extent(r.hands, 27);
			double hx = (he[3] - he[0]) / 2, hz = (he[5] - he[2]) / 2;
			double[] Af = norm(sub(W, E));
			// the cuff: two overlapping flared bands up the forearm
			double[][] p = { add(W, scale(Af, 3.0)), add(W, scale(Af, -5)), add(W, scale(Af, -5.5)), add(W, scale(Af, -13)) };
			double[] rad = { 5.4, 6.8, 5.8, 7.6 };
			for (int k = 0; k < 2; k++) {
				double[][] pp = { p[2 * k + 1], p[2 * k] };
				double[] rr = { rad[2 * k + 1], rad[2 * k] };
				double[][] u = { pt(side, 0, 0), pt(side, 0, 0) };
				m.prio = 10 + k;
				sweep(m, pp, u, rr, rr, 6, new int[] { fore, fore }, (i, c, s) -> c > 0.4 ? HIDE_TOP : c < -0.4 ? BELLY_2 : HIDE);
			}
			// the hand: a plated mitten
			double[] D = norm(sub(H, W));
			double[][] hs = { { 2.5, 1.05, 1.05 }, { 9.0, 1.2, 1.12 }, { 14.5, 1.0, 0.95 } };
			int[][] hi = new int[hs.length][4];
			for (int k = 0; k < hs.length; k++) {
				double[] c = add(W, scale(D, hs[k][0]));
				double ax = hx * hs[k][1], az = hz * hs[k][2];
				double[][] corner = { { -ax, -az }, { ax, -az }, { ax, az }, { -ax, az } };
				for (int q = 0; q < 4; q++) {
					hi[k][q] = m.v(add(c, pt(corner[q][0], 0, corner[q][1])), k == 0 ? fore : hand);
				}
			}
			m.prio = 10;
			for (int k = 0; k + 1 < hs.length; k++) {
				double[] in = add(W, scale(D, (hs[k][0] + hs[k + 1][0]) / 2));
				for (int q = 0; q < 4; q++) {
					boolean outside = (q == 1 && side > 0) || (q == 3 && side < 0);
					m.quadAway(hi[k][q], hi[k][(q + 1) % 4], hi[k + 1][(q + 1) % 4], hi[k + 1][q], outside ? HIDE_TOP : k == 0 ? HIDE : HIDE_2, in);
				}
			}
			for (int q = 1; q + 1 < 4; q++) {
				m.tri(hi[2][0], hi[2][q], hi[2][q + 1], HIDE_DARK, D);
			}
			// the fang claws: four, from the ends of the fingers, curving down and in towards the palm
			m.prio = 11;
			double[] base = add(W, scale(D, hs[2][0] - 0.5));
			for (int k = 0; k < 4; k++) {
				double zz = -hz * 0.9 + 2 * hz * 0.9 * k / 3.0;
				double[] root = add(base, pt(0, 0, zz));
				double len = k == 1 || k == 2 ? 9.5 : 7.5;
				double[] tip = add(root, add(scale(D, len), pt(-side * 2.6, 0, (zz) * 0.15)));
				if (k == 1 || k == 2) {
					fang(m, root, tip, 1.1, hand);
				} else { // the outer two straight, to save faces
					ModelData f = new ModelData();
					ArmourForge.blade(f, add(root, scale(D, -1.0)), tip, pt(0, 0, 1), 1.0, 0.8, BONE_DARK);
					absorb(m, f, 11, hand);
				}
			}
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                               THE SHIELD                               */
	/* ====================================================================== */

	/**
	 * A Gravecoil coiled flat into a round shield: three turns spiralling in round a dark domed boss, every turn banded,
	 * the scutes on the inside of each turn; the outer end rises at the top into the head, jaws open, looking out. In the
	 * rune kite's place and plane, every vertex on the left hand's label 28.
	 */
	static ModelData shield(ArmourForge af) throws IOException {
		ModelData kite = af.read(ArmourForge.RUNE_KITE_WORN);
		Frame k = ArmourForge.shieldFrame(kite, 61);
		double[] nrm = k.n;
		double[] down = norm(sub(BaseCoat.Y, scale(nrm, dot(BaseCoat.Y, nrm))));
		double[] across = norm(ModelData.cross(nrm, down));
		double[] c = add(k.o, scale(nrm, 1.0));
		final int L = 28;
		Mesh m = new Mesh();
		// the backing: a shallow dark disc, under the coils
		double R = 24;
		int bn = 8;
		int[] rim = new int[bn], mid = new int[bn];
		for (int j = 0; j < bn; j++) {
			double a = Math.PI * 2 * j / bn;
			rim[j] = m.v(add(c, add(scale(nrm, -1.5), add(scale(down, Math.cos(a) * R), scale(across, Math.sin(a) * R)))), L);
			mid[j] = m.v(add(c, add(scale(nrm, 0.5), add(scale(down, Math.cos(a) * R * 0.5), scale(across, Math.sin(a) * R * 0.5)))), L);
		}
		int ctr = m.v(add(c, scale(nrm, 2.5)), L);
		m.prio = 9;
		for (int j = 0; j < bn; j++) {
			int j2 = (j + 1) % bn;
			m.quad(rim[j], rim[j2], mid[j2], mid[j], HIDE_DARK, nrm);
			m.tri(mid[j], mid[j2], ctr, HIDE_TOP, nrm);
		}
		int back = m.v(add(c, scale(nrm, -2.5)), L);
		for (int j = 0; j < bn; j++) {
			m.tri(rim[j], rim[(j + 1) % bn], back, HIDE_DARK, scale(nrm, -1));
		}
		// the coil: a spiral from the boss outwards, three turns, ending at the top where the neck rises
		java.util.List<double[]> P = new java.util.ArrayList<>(), U = new java.util.ArrayList<>();
		java.util.List<Double> RA = new java.util.ArrayList<>();
		double turns = 2.6, r0 = 5.5, r1 = 20.5;
		int segs = 22;
		for (int i = 0; i <= segs; i++) {
			double t = (double) i / segs;
			double a = Math.PI + Math.PI * 2 * turns * (1 - t); // ends at the top (a = pi: up)
			double rr = lerp(r0, r1, Math.pow(t, 0.85));
			double tube = lerp(2.2, 3.8, t);
			P.add(add(c, add(scale(nrm, 1.2 + tube * 0.4), add(scale(down, Math.cos(a) * rr), scale(across, Math.sin(a) * rr)))));
			U.add(nrm);
			RA.add(tube);
		}
		// the neck rising off the face at the top, the head looking out
		double[] top = P.get(P.size() - 1);
		double[] upv = scale(down, -1);
		double[][] neck = { add(top, add(scale(upv, 3.0), scale(nrm, 2.5))), add(top, add(scale(upv, 5.0), scale(nrm, 6.5))) };
		for (double[] q : neck) {
			P.add(q);
			U.add(norm(add(nrm, scale(upv, 0.6))));
			RA.add(3.6);
		}
		int n = P.size();
		double[][] p = P.toArray(new double[0][]), up = U.toArray(new double[0][]);
		double[] ra = new double[n], rb = new double[n];
		int[] lab = new int[n];
		for (int i = 0; i < n; i++) {
			ra[i] = RA.get(i);
			rb[i] = RA.get(i) * 1.15;
			lab[i] = L;
		}
		ra[0] = rb[0] = 0; // the tail's tip, at the boss
		m.prio = 10;
		final int bronzeAt = segs - 8;
		int[][] rings = sweep(m, p, up, ra, rb, 5, lab, (i, u, s) -> i == bronzeAt ? (u > 0 ? BRONZE : VERDIGRIS) : u < -0.5 ? HIDE_DARK : hide(i, u + 0.1));
		// the head: an upper jaw jutting out from the face over the rim, a lower jaw dropped open, fangs, eyes
		double[] hc = p[n - 1];
		double[] fwd = norm(add(nrm, scale(upv, 0.15)));
		double hw = 4.4;
		int[] neckRing = rings[n - 1];
		double[] snout = add(hc, scale(fwd, 9.0));
		// the upper head: from the neck ring to the snout, over the top
		int[] hu = new int[5];
		double[][] hp = { add(hc, add(scale(across, -hw), scale(upv, 1.0))), add(hc, add(scale(upv, 4.0), scale(fwd, 2.0))), add(hc, add(scale(across, hw), scale(upv, 1.0))),
				add(snout, scale(across, 2.0)), add(snout, scale(across, -2.0)) };
		for (int i = 0; i < 5; i++) {
			hu[i] = m.v(hp[i], L);
		}
		double[] hcen = add(hc, scale(fwd, 3.0));
		m.prio = 11;
		m.triAway(hu[0], hu[1], hu[4], HIDE_TOP, add(hcen, scale(upv, -4)));
		m.triAway(hu[1], hu[2], hu[3], HIDE_TOP, add(hcen, scale(upv, -4)));
		m.triAway(hu[1], hu[3], hu[4], HIDE_DARK, add(hcen, scale(upv, -4)));
		int palate = m.v(add(hc, add(scale(fwd, 3.0), scale(upv, -0.5))), L);
		m.tri(hu[0], hu[4], palate, MOUTH, scale(upv, -1));
		m.tri(hu[4], hu[3], palate, MOUTH, scale(upv, -1));
		m.tri(hu[3], hu[2], palate, MOUTH, scale(upv, -1));
		// the lower jaw, dropped open below
		double[] jt = add(add(hc, scale(fwd, 7.0)), scale(upv, -5.0));
		int j0 = m.v(add(hc, add(scale(across, -hw * 0.9), scale(upv, -1.0))), L), j1 = m.v(add(hc, add(scale(across, hw * 0.9), scale(upv, -1.0))), L);
		int j2 = m.v(add(jt, scale(across, 1.6)), L), j3 = m.v(add(jt, scale(across, -1.6)), L), jb = m.v(add(add(hc, scale(fwd, 3.0)), scale(upv, -4.0)), L);
		m.tri(j0, j1, j2, MOUTH_DARK, upv);
		m.tri(j0, j2, j3, MOUTH_DARK, upv);
		m.triAway(j0, j3, jb, BELLY, add(hcen, scale(upv, 2)));
		m.triAway(j1, j2, jb, BELLY, add(hcen, scale(upv, 2)));
		m.triAway(j2, j3, jb, BELLY_2, add(hcen, scale(upv, 2)));
		// fangs and eyes
		for (int s = -1; s <= 1; s += 2) {
			double[] fr = add(add(snout, scale(across, s * 1.6)), scale(fwd, -1.2));
			ModelData f = new ModelData();
			ArmourForge.blade(f, fr, add(fr, add(scale(upv, -4.2), scale(fwd, 0.6))), across, 0.8, 0.6, BONE);
			absorb(m, f, 12, L);
			double[] ec = add(add(hc, add(scale(across, s * 3.0), scale(upv, 2.3))), scale(fwd, 2.5));
			eye(m, ec, norm(add(add(scale(across, s * 0.8), scale(upv, 0.5)), scale(fwd, 0.6))), 0.8, L);
		}
		return m.d;
	}
}
