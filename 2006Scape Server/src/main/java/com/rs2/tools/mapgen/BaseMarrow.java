package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ArmourForge.add;
import static com.rs2.tools.mapgen.ArmourForge.dot;
import static com.rs2.tools.mapgen.ArmourForge.norm;
import static com.rs2.tools.mapgen.ArmourForge.scale;
import static com.rs2.tools.mapgen.ArmourForge.sub;
import static com.rs2.tools.mapgen.BaseCoat.ell;
import static com.rs2.tools.mapgen.BaseCoat.extent;
import static com.rs2.tools.mapgen.BaseCoat.lerp;
import static com.rs2.tools.mapgen.BaseCoat.mirror;
import static com.rs2.tools.mapgen.BaseCoat.pt;
import static com.rs2.tools.mapgen.BaseCoat.slice;
import static com.rs2.tools.mapgen.BaseParka.cap;
import static com.rs2.tools.mapgen.BaseParka.claw;
import static com.rs2.tools.mapgen.BaseParka.hring;
import static com.rs2.tools.mapgen.BaseParka.loft;
import static com.rs2.tools.mapgen.BaseParka.ring;
import static com.rs2.tools.mapgen.BaseParka.shade;
import static com.rs2.tools.mapgen.BaseParka.trunk;
import static com.rs2.tools.mapgen.ModelData.hsl;

import java.io.IOException;

import com.rs2.tools.mapgen.ArmourSets.Style;
import com.rs2.tools.mapgen.BaseCoat.Mesh;
import com.rs2.tools.mapgen.BaseCoat.Rig;

/**
 * The Marrowbound base: the bone-and-hide of the scavenger-shamans who pick over Nythera's old barrows, wearing the
 * bones of whatever they found down there over dark hide. Built from scratch on the leather's rig for the Marrowbound
 * (A41) draft set; see NewBases. Three materials and no more: yellowed bone in three tones, dark brown hide (with a
 * lighter shaggy fur), and one accent, a faint sickly-green glow, only in eye sockets.
 *
 * <ul>
 * <li>Mask (named "... mask", so the head and beard hide: the face is the skull): a great ram's skull worn over the
 * head, its long snout and upper jaw jutting forward over the brow with a row of teeth, dark eye sockets with a green
 * glow deep in each, and two thick ridged horns curling back, down and round beside the cheeks. Behind and under it a
 * dark hide cowl wraps the rest of the head and the throat.</li>
 * <li>Top (named "... top", so the arms kit hides: the arms are drawn bare, the arms kit copied in skin colour): a
 * sleeveless hide jerkin to the upper thigh, split front and back, with a dagged hem and a shaggy fur collar; over it a
 * ribcage chestpiece (a sternum and four pairs of ribs standing proud of the hide, strapped across the back); a beast
 * skull for a pauldron on the right shoulder, a great shoulder blade on the left; and a string of teeth round the
 * neck.</li>
 * <li>Chaps: hide chaps with a long-bone splint strapped down the front of each thigh and shin, a kneecap bone, and a
 * fur loincloth flap front and back.</li>
 * <li>Boots: hide boots wrapped to the ankle, with a bone cap over the toes and three claws curling out of it.</li>
 * <li>Vambraces: hide wraps over the hands (the hands kit, in dark hide), hide vambraces with two bone strips down the
 * back of the forearm, and three knuckle-bones across the knuckles.</li>
 * </ul>
 *
 * <p>Labels. No new vertex takes a pivot label (BaseWraith.PIVOTS) except the knuckle-bones on the hands 27 / 28, which
 * are balanced by a hidden counterweight so the label's centroid stays exactly the kit's ({@link #balance}). Where a
 * piece hides a kit, BaseWraith.anchors adds a tiny hidden triangle per pivot label at the kit's centroid of it (the
 * client drops vertices no face uses), so every joint turns where it does on the bare body; the bare arms and the hide
 * hands are the kits' own meshes with their own labels. The mask is rigid on the skull 1. The jerkin's trunk, ribs,
 * teeth and collar are on the chest 8 (the waist row on the belly 4), its two skirt halves on the thighs 43 / 44 (top
 * row on 4); the skull pauldron on the right upper arm 20, the shoulder blade on the left 26. Chaps: the waistband and
 * loincloth on the static belt 41, thighs and their splints 43 / 44, shins, splints and kneecaps 36 / 33, the cuffs 38 /
 * 32. Boots: the shaft top on the shins 36 / 33, the rest (foot, toe cap, claws) on the ankles 38 / 32. Vambraces: the
 * forearms 22 / 19.
 */
final class BaseMarrow {

	private BaseMarrow() {
	}

	/** The skin colour (the client turns it into the player's own), and the black of an empty eye socket. */
	static final int SKIN = 4550, SOCKET = hsl(5, 2, 7);

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		Rig r = BaseCoat.rig(af, female);
		switch (slot) {
		case ArmourSets.HELM: {
			int jawKit = (female ? ArmourPreview.FEMALE : ArmourPreview.MALE)[1];
			return new ModelData[] { BaseWraith.separate(BaseWraith.anchors(mask(r, st), r.head, jawKit < 0 ? null : af.read(jawKit))), null };
		}
		case ArmourSets.BODY: {
			ModelData m = BaseWraith.kitCopy(r.arms, SKIN);
			ArmourForge.merge(m, BaseParka.apart(top(r, st)), null);
			return new ModelData[] { BaseWraith.separate(BaseWraith.anchors(m, r.torso)), null };
		}
		case ArmourSets.LEGS:
			return new ModelData[] { BaseWraith.separate(BaseWraith.anchors(BaseParka.apart(chaps(r, st)), r.legs)), null };
		case ArmourSets.BOOTS:
			return new ModelData[] { BaseWraith.separate(BaseWraith.anchors(BaseParka.apart(boots(r, st)), r.feet)), null };
		case ArmourSets.GLOVES: {
			ModelData m = BaseWraith.kitCopy(r.hands, st.trimDark);
			ArmourForge.merge(m, vambraces(r, st), null);
			return new ModelData[] { BaseWraith.separate(m), null };
		}
		default:
			return new ModelData[] { donor, donorSecond };
		}
	}

	/** The inventory model for the slot, or null to let ArmourSets build it the usual way from the worn model(s). */
	static ModelData inventory(ArmourForge af, Style st, int slot, ModelData[] worn) throws IOException {
		return null;
	}

	/* ====================================================================== */
	/*                                 HELPERS                                */
	/* ====================================================================== */

	/** n points round an axis through c (direction a): ry towards {@code upHint} (made square to a), rx across; phi 0 at the up side. */
	static int[] ringUp(Mesh m, double[] c, double[] a, double[] upHint, double rx, double ry, int n, int label) {
		double[] up = norm(sub(upHint, scale(a, dot(upHint, a))));
		double[] sd = norm(ModelData.cross(a, up));
		int[] out = new int[n];
		for (int j = 0; j < n; j++) {
			double t = Math.PI * 2 * j / n;
			out[j] = m.v(add(c, add(scale(sd, rx * Math.sin(t)), scale(up, ry * Math.cos(t)))), label);
		}
		return out;
	}

	/**
	 * A raised strip (a strap, a rib, a sternum) along a path: at point i the outward normal nrm[i] and the across
	 * direction acr[i], half-width hw[i]; the strip's top stands {@code th} off the path along the normal, its two walls
	 * reach back down to it. Top in {@code top}, the wall on the -acr side in {@code wallA}, the +acr side in {@code wallB};
	 * both ends closed.
	 */
	static void strip(Mesh m, double[][] p, double[][] nrm, double[][] acr, double[] hw, double th, int label, int top, int wallA, int wallB) {
		int n = p.length;
		int[] a0 = new int[n], b0 = new int[n], a1 = new int[n], b1 = new int[n];
		for (int i = 0; i < n; i++) {
			double[] A = add(p[i], scale(acr[i], -hw[i])), B = add(p[i], scale(acr[i], hw[i]));
			a0[i] = m.v(A, label);
			b0[i] = m.v(B, label);
			a1[i] = m.v(add(A, scale(nrm[i], th)), label);
			b1[i] = m.v(add(B, scale(nrm[i], th)), label);
		}
		for (int i = 0; i + 1 < n; i++) {
			int j = i + 1;
			double[] nm = norm(add(nrm[i], nrm[j])), am = norm(add(acr[i], acr[j]));
			m.quad(a1[i], b1[i], b1[j], a1[j], top, nm);
			m.quad(a0[i], a1[i], a1[j], a0[j], wallA, scale(am, -1));
			m.quad(b0[i], b1[i], b1[j], b0[j], wallB, am);
		}
		m.quad(a0[0], b0[0], b1[0], a1[0], wallA, sub(p[0], p[1]));
		m.quad(a0[n - 1], b0[n - 1], b1[n - 1], a1[n - 1], wallB, sub(p[n - 1], p[n - 2]));
	}

	/** A little pyramid (a tooth, a knuckle, a glint) from a base ring of {@code sides} round root to the apex; no base. */
	static void spike(Mesh m, double[] root, double[] apex, double r, int sides, int label, int col, int col2) {
		double[] a = norm(sub(apex, root));
		int[] rg = ring(m, root, a, r, r, sides, 0.3, label);
		int t = m.v(apex, label);
		for (int j = 0; j < sides; j++) {
			m.triAway(rg[j], rg[(j + 1) % sides], t, j % 2 == 0 ? col : col2, root);
		}
	}

	/**
	 * A ring of fur round a vertical axis at c: from radius r0 at y, over a crest, out to a jagged edge of 2n tufts at r1
	 * hanging {@code drop} below, and a shadowed underside back to the base.
	 */
	static void furCollar(Mesh m, double[] c, double r0x, double r0z, double r1x, double r1z, double drop, int n, int label, int fur, int tip, int under) {
		int N = n * 2;
		int[] base = new int[N], crest = new int[N], tuft = new int[N], und = new int[N];
		for (int j = 0; j < N; j++) {
			double t = Math.PI * 2 * j / N;
			double jag = j % 2 == 0 ? 1.0 : 0.86;
			base[j] = m.v(ell(c[0], c[1], c[2], r0x, r0z, t), label);
			crest[j] = m.v(ell(c[0], c[1] - 2.2, c[2], (r0x + r1x) / 2 + 0.6, (r0z + r1z) / 2 + 0.6, t), label);
			tuft[j] = m.v(ell(c[0], c[1] + drop * (j % 2 == 0 ? 1.0 : 0.55), c[2], r1x * jag, r1z * jag, t), label);
			und[j] = m.v(ell(c[0], c[1] + 1.5, c[2], r0x * 1.02, r0z * 1.02, t), label);
		}
		for (int j = 0; j < N; j++) {
			int k = (j + 1) % N;
			double tm = Math.PI * 2 * (j + 0.5) / N;
			double[] core = ell(c[0], c[1] + drop * 0.2, c[2], (r0x * 0.7 + r1x * 0.3), (r0z * 0.7 + r1z * 0.3), tm);
			m.quadAway(base[j], base[k], crest[k], crest[j], fur, core);
			m.quadAway(crest[j], crest[k], tuft[k], tuft[j], j % 2 == 0 ? tip : fur, core);
			m.quadAway(tuft[j], tuft[k], und[k], und[j], under, core);
		}
	}

	/**
	 * Keeps the centroid of {@code label}'s vertices in {@code m} at {@code target} (a kit's own centroid of a pivot label),
	 * after new geometry was given that label: adds as many vertices again (tiny triangles of that label, hidden inside
	 * the limb) gathered at the new geometry's mirror image through the target, so the label's centroid is the target.
	 */
	static void balance(Mesh m, int label, double[] target) {
		double[] s = new double[3];
		int n = 0;
		for (int i = 0; i < m.d.vertices.size(); i++) {
			if (m.d.vertexSkins.get(i) == label) {
				int[] v = m.d.vertices.get(i);
				for (int k = 0; k < 3; k++) {
					s[k] += v[k];
				}
				n++;
			}
		}
		// k more vertices (whole triangles) whose sum is (n + k) * target - s: round the point that much off the target
		int k3 = (n + 2) / 3, k = k3 * 3;
		double[] c = new double[3];
		for (int q = 0; q < 3; q++) {
			c[q] = ((n + k) * target[q] - s[q]) / k;
		}
		int x = (int) Math.round(c[0]), y = (int) Math.round(c[1]), z = (int) Math.round(c[2]);
		m.d.skin = label;
		for (int t = 0; t < k3; t++) { // each a unit apart along y (shifts summing to zero), as the client welds vertices that share a position
			int d = (t % 2 == 1 ? 1 : -1) * ((t + 1) / 2) * 2;
			m.d.tri(m.d.vertex(x + 1, y + d, z), m.d.vertex(x - 1, y + d + 1, z), m.d.vertex(x, y + d - 1, z), SOCKET);
		}
	}

	/* ====================================================================== */
	/*                                THE MASK                                */
	/* ====================================================================== */

	/**
	 * The ram's-skull mask: a hide cowl round the head; over it the skull, lofted along an axis from the poll (above the
	 * back of the crown) down and forward to the snout's tip, out in front of the face; deep eye sockets with a green
	 * glow; a row of teeth under the snout; and two ridged horns curling round beside the cheeks. All on the skull 1.
	 */
	static ModelData mask(Rig r, Style st) {
		Mesh m = new Mesh();
		int[] hb = r.headB;
		double hx = Math.max(-hb[0], hb[3]), top = hb[1], chin = hb[4], front = hb[2], back = hb[5];
		double hh = chin - top, hd = back - front, cz = (front + back) / 2;
		// ------------------------------------------------ the cowl: an egg round the head, its skirt over the throat and nape
		int n = 8;
		double[][] cw = { // y, rx, rz, cz
				{ top + hh * 0.18, hx * 0.75, hd * 0.38, cz + 2 }, { top + hh * 0.5, hx + 2.5, hd * 0.5 + 2.0, cz + 1.5 }, { chin + 1, hx + 1.5, hd * 0.5 + 1.0, cz + 2.5 },
				{ chin + 5, hx + 3.8, hd * 0.5 + 0.8, cz + 4.5 } };
		int[][] cr = new int[cw.length][];
		double[][] cc = new double[cw.length][];
		for (int k = 0; k < cw.length; k++) {
			cc[k] = pt(0, cw[k][0], cw[k][3]);
			cr[k] = hring(m, 0, cw[k][0], cw[k][3], cw[k][1], cw[k][2], n, Math.PI / n, 1);
		}
		m.prio = 1;
		loft(m, cr, cc, new int[] { st.trimDark, st.trimDark, st.trim });
		cap(m, cr[0], m.v(pt(0, top - 2.5, cz + 2), 1), st.trimDark, cc[1]);
		// ------------------------------------------------ the skull
		double[] P0 = pt(0, top - 2.5, cz + 6), T = pt(0, chin - 2, front - 8);
		double[] a = norm(sub(T, P0));
		double[] w = pt(0, a[2], -a[1]); // square to the axis, up and forward
		double[][] sk = { // t along the axis, half-width, half-depth (along w), shift along w
				{ 0.0, 7.0, 5.0, 0.5 }, { 0.15, hx + 4.5, 10.0, 0.5 }, { 0.36, hx + 5.5, 10.5, 0 }, { 0.58, hx - 1.5, 8.0, -0.5 }, { 0.8, 7.0, 6.0, -0.8 },
				{ 1.0, 5.4, 4.6, -0.8 } };
		int S = sk.length, sn = 8;
		double L = Math.sqrt(dot(sub(T, P0), sub(T, P0)));
		int[][] rg = new int[S][];
		double[][] sc = new double[S][];
		for (int k = 0; k < S; k++) {
			sc[k] = add(add(P0, scale(a, sk[k][0] * L)), scale(w, sk[k][3]));
			rg[k] = ringUp(m, sc[k], a, w, sk[k][1], sk[k][2], sn, 1);
		}
		m.prio = 3;
		for (int k = 0; k + 1 < S; k++) {
			for (int j = 0; j < sn; j++) {
				int jj = (j + 1) % sn;
				double phi = Math.PI * 2 * (j + 0.5) / sn;
				boolean under = Math.cos(phi) < -0.3;
				int col = k == 0 ? st.light : under ? st.dark : k == 1 && Math.cos(phi) > 0.5 ? st.light : st.main;
				m.quadAway(rg[k][j], rg[k][jj], rg[k + 1][jj], rg[k + 1][j], col, lerp(sc[k], sc[k + 1], 0.5));
			}
		}
		cap(m, rg[0], m.v(sub(P0, scale(a, 2.5)), 1), st.dark, sc[1]);
		// the end of the snout: the nose holes dark, ringed in bone
		int nose = m.v(add(T, add(scale(a, 1.8), scale(w, -0.5))), 1);
		for (int j = 0; j < sn; j++) {
			double phi = Math.PI * 2 * (j + 0.5) / sn;
			m.triAway(rg[S - 1][j], rg[S - 1][(j + 1) % sn], nose, Math.cos(phi) < 0.2 && Math.cos(phi) > -0.75 ? SOCKET : st.dark, sc[S - 2]);
		}
		// ------------------------------------------------ the eye sockets
		double[] fwd = norm(add(w, a));
		for (int side = -1; side <= 1; side += 2) {
			double t = 0.42, phi = side * 0.72;
			double rx = hx + 4.0, ry = 10.0;
			double[] N = norm(add(scale(pt(side, 0, 0), 0.5), fwd));
			double[] c = add(add(add(P0, scale(a, t * L)), add(scale(pt(1, 0, 0), rx * Math.sin(phi)), scale(w, ry * Math.cos(phi)))), scale(N, 0.5));
			socket(m, c, N, a, 6.0, st);
		}
		// ------------------------------------------------ the teeth along the snout, and a pair of fangs at the front
		m.prio = 4;
		for (int side = -1; side <= 1; side += 2) {
			for (int q = 1; q < 4; q++) {
				double t = 0.55 + q * 0.12;
				double rx = lerp(pt(sk[3][1], 0, 0), pt(sk[5][1], 0, 0), (t - 0.56) / 0.44)[0];
				double ry = lerp(pt(sk[3][2], 0, 0), pt(sk[5][2], 0, 0), (t - 0.56) / 0.44)[0];
				double phi = side * (Math.PI - 0.62);
				double[] root = add(add(P0, scale(a, t * L)), add(scale(pt(1, 0, 0), rx * Math.sin(phi) * 0.92), scale(w, ry * Math.cos(phi) * 0.92 - 0.6)));
				double len = q == 3 ? 7.5 : 4.0;
				double[] tip = add(root, add(scale(w, -len), scale(a, q == 3 ? -1.5 : 0.3)));
				spike(m, root, tip, q == 3 ? 1.8 : 1.4, 3, 1, st.light, st.main);
			}
		}
		// ------------------------------------------------ the horns
		for (int side = -1; side <= 1; side += 2) {
			horn(m, r, st, side, add(add(P0, scale(a, 0.17 * L)), add(scale(pt(side, 0, 0), (hx + 4.5) * Math.sin(1.2)), scale(w, 10.0 * Math.cos(1.2)))));
		}
		return m.d;
	}

	/** An eye socket on the skull at c facing N: a bone rim proud of the skull, a dark hollow, a green glow deep inside. */
	static void socket(Mesh m, double[] c, double[] N, double[] a, double rad, Style st) {
		int k = 6;
		double[] up = norm(sub(scale(a, -1), scale(N, dot(scale(a, -1), N))));
		double[] sd = norm(ModelData.cross(N, up));
		int[] rim = new int[k], in = new int[k];
		for (int j = 0; j < k; j++) {
			double t = Math.PI * 2 * j / k + Math.PI / k;
			double[] d = add(scale(sd, Math.sin(t)), scale(up, Math.cos(t) * 0.85));
			rim[j] = m.v(add(c, add(scale(N, 1.6 + (Math.cos(t) > 0 ? 0.8 : 0)), scale(d, rad))), 1);
			in[j] = m.v(add(c, add(scale(N, 0.6), scale(d, rad * 0.62))), 1);
		}
		int deep = m.v(add(c, scale(N, -1.4)), 1);
		m.prio = 5;
		for (int j = 0; j < k; j++) {
			int jj = (j + 1) % k;
			double t = Math.PI * 2 * (j + 0.5) / k + Math.PI / k;
			m.quad(rim[j], rim[jj], in[jj], in[j], Math.cos(t) > 0.3 ? st.light : st.main, N);
			m.tri(in[j], in[jj], deep, SOCKET, N);
		}
		// the glow: a small bright point in the hollow
		m.prio = 6;
		double[] g = add(c, scale(N, -0.2));
		double gr = rad * 0.36;
		int g0 = m.v(add(g, scale(up, gr)), 1), g1 = m.v(add(g, scale(sd, gr * 0.85)), 1), g2 = m.v(add(g, scale(up, -gr)), 1), g3 = m.v(add(g, scale(sd, -gr * 0.85)), 1);
		int gc = m.v(add(g, scale(N, 0.9)), 1);
		m.tri(g0, g1, gc, st.core, N);
		m.tri(g1, g2, gc, st.accent, N);
		m.tri(g2, g3, gc, st.accent, N);
		m.tri(g3, g0, gc, st.core, N);
	}

	/**
	 * A ram's horn from {@code start} on the side of the skull: it rises, curls back, down and forward round a sideways
	 * axis, coming out beside the cheek, moving outwards as it goes and tapering to a blunt tip; ridged in two shades.
	 */
	static void horn(Mesh m, Rig r, Style st, int side, double[] start) {
		int K = 7, sides = 5;
		double th0 = -0.75, th1 = th0 + Math.PI * 2 * 0.86;
		double R0 = 13.0, R1 = 5.5;
		// the axis: so that the start is on the curl at th0, radius R0
		double[] A = sub(start, pt(0, -Math.cos(th0) * R0, Math.sin(th0) * R0));
		double[][] p = new double[K][];
		for (int i = 0; i < K; i++) {
			double f = i / (double) (K - 1);
			double th = th0 + (th1 - th0) * f, R = R0 + (R1 - R0) * f;
			p[i] = add(A, pt(side * (1.5 + 10.0 * Math.pow(f, 0.8)), -Math.cos(th) * R, Math.sin(th) * R));
		}
		int[][] rg = new int[K][];
		for (int i = 0; i < K; i++) {
			double f = i / (double) (K - 1);
			double[] a = norm(i == 0 ? sub(p[1], p[0]) : i == K - 1 ? sub(p[K - 1], p[K - 2]) : sub(p[i + 1], p[i - 1]));
			double rad = 5.8 - 3.8 * f;
			rg[i] = ring(m, p[i], a, rad, rad * 0.9, sides, 0.4, 1);
		}
		m.prio = 4;
		int ridge = shade(st.dark, 12);
		for (int i = 0; i + 1 < K; i++) {
			m.band(rg[i], rg[i + 1], true, new int[] { i % 2 == 0 ? st.dark : ridge }, lerp(p[i], p[i + 1], 0.5));
		}
		double[] dirEnd = norm(sub(p[K - 1], p[K - 2]));
		cap(m, rg[K - 1], m.v(add(p[K - 1], scale(dirEnd, 2.5)), 1), st.main, p[K - 2]);
	}

	/* ====================================================================== */
	/*                                 THE TOP                                */
	/* ====================================================================== */

	/**
	 * The top: a sleeveless hide jerkin to the upper thigh (split front and back, a dagged hem), a fur collar, the
	 * ribcage chestpiece strapped over it, the skull pauldron (right) and the shoulder blade (left), and a string of
	 * teeth. The bare arms are added by {@link #worn} (the arms kit, in skin).
	 */
	static ModelData top(Rig r, Style st) {
		Mesh m = new Mesh();
		int n = 10;
		double L = r.waistY - r.neckY;
		// ------------------------------------------------ the jerkin's trunk: y, widen x, front, back, label
		double[][] rows = { { r.neckY + 1.0, 1.0, 1.5, 1.5, 8 }, { r.neckY + 7, 1.6, 1.8, 1.8, 8 }, { r.neckY + L * 0.42, 1.6, 1.8, 1.8, 8 },
				{ r.neckY + L * 0.76, 1.6, 1.8, 1.8, 8 }, { r.waistY + 2.5, 2.4, 2.4, 2.4, 4 } };
		int R = rows.length;
		int[][] ring = new int[R][];
		double[][] ctr = new double[R][], dim = new double[R][];
		for (int i = 0; i < R; i++) {
			double[] t = trunk(r, rows[i][0], rows[i][1], rows[i][2], rows[i][3]);
			if (i == 0) {
				t[1] = Math.min(t[1], 11.0);
			}
			dim[i] = t;
			ctr[i] = pt(0, rows[i][0], t[0]);
			ring[i] = hring(m, 0, rows[i][0], t[0], t[1], t[2], n, 0, (int) rows[i][4]);
		}
		m.prio = 2;
		for (int i = 0; i + 1 < R; i++) {
			m.band(ring[i], ring[i + 1], true, new int[] { i == R - 2 ? st.trimDark : st.trim }, lerp(ctr[i], ctr[i + 1], 0.5));
		}
		// ------------------------------------------------ the skirt: to the upper thigh, split front and back, dagged
		double[] wst = dim[R - 1];
		double top = rows[R - 1][0], hem = r.hip[1] + (r.thigh[1] - r.hip[1]) * 0.2;
		double[] waist = { wst[0], wst[1] - 1.0, wst[2] - 1.0 };
		int segs = 6;
		double[] ys = { top, (top + hem) / 2, hem };
		for (int side = -1; side <= 1; side += 2) {
			int label = side > 0 ? 43 : 44;
			int[][] row = new int[ys.length][segs + 1];
			double[][] sk = new double[ys.length][];
			for (int q = 0; q < ys.length; q++) {
				double f = (ys[q] - top) / (hem - top);
				sk[q] = BaseCoat.skirtRing(r, waist, top, ys[q], f * 0.45);
				double gap = 0.04 + 0.05 * f;
				for (int j = 0; j <= segs; j++) {
					double phi = side * (gap + (Math.PI - 2 * gap) * j / segs);
					double dag = q == ys.length - 1 && j % 2 == 1 ? 4.5 : 0;
					row[q][j] = m.v(ell(0, ys[q] + dag, sk[q][0], sk[q][1] + 0.4 * q, sk[q][2] + 0.4 * q, phi), q == 0 ? 4 : label);
				}
			}
			for (int q = 0; q + 1 < ys.length; q++) {
				double[] in = pt(0, (ys[q] + ys[q + 1]) / 2, sk[q][0]);
				m.band(row[q], row[q + 1], false, new int[] { q == 0 ? st.trim : shade(st.trim, -5) }, in);
			}
		}
		// ------------------------------------------------ the fur collar
		double[] s = slice(r.torso, r.neckY + 1, 0);
		double ncz = (s[2] + s[3]) / 2, nrx = Math.min(11.0, Math.max(-s[0], s[1])) + 1.5, nrz = (s[3] - s[2]) / 2 + 2.0;
		m.prio = 5;
		furCollar(m, pt(0, r.neckY + 2.5, ncz + 0.5), nrx, nrz, nrx + 5.5, nrz + 5.0, 4.5, 6, 8, st.leather, shade(st.leather, 10), st.leatherDark);
		// ------------------------------------------------ the ribcage
		ribcage(m, r, st);
		// ------------------------------------------------ the string of teeth
		teeth(m, r, st);
		// ------------------------------------------------ the pauldrons
		skullPauldron(m, r, st, -1);
		shoulderBlade(m, r, st, 1);
		return m.d;
	}

	/** The jerkin's surface at height y round the trunk (margin as the jerkin's, plus out): {cz, rx, rz}. */
	static double[] hide(Rig r, double y, double out) {
		double[] t = trunk(r, y, 1.6 + out, 1.8 + out, 1.8 + out);
		return t;
	}

	/** A point on the jerkin's surface and its outward normal, at height y and angle phi (0 front, + towards +x). */
	static double[][] onHide(Rig r, double y, double phi, double out) {
		double[] t = hide(r, y, out);
		double[] p = ell(0, y, t[0], t[1], t[2], phi);
		double[] nrm = norm(pt(Math.sin(phi) / t[1], 0, -Math.cos(phi) / t[2]));
		return new double[][] { p, nrm };
	}

	/** A sternum down the front and four pairs of ribs curving round to the sides, standing proud of the jerkin; a strap across the back. */
	static void ribcage(Mesh m, Rig r, Style st) {
		double L = r.waistY - r.neckY;
		double y0 = r.neckY + 14, dy = L * 0.155;
		m.prio = 6;
		for (int k = 0; k < 4; k++) {
			double y = y0 + k * dy;
			double hwr = 2.3 - k * 0.15;
			for (int side = -1; side <= 1; side += 2) {
				double[] phis = { 0.2, 0.7, 1.2, 1.72 };
				double[] lift = { 0, -1.2, -2.8, -4.2 };
				double[][] p = new double[4][], nrm = new double[4][], acr = new double[4][];
				double[] hw = new double[4];
				for (int i = 0; i < 4; i++) {
					double[][] q = onHide(r, y + lift[i], side * phis[i], 0.5 + (i == 1 || i == 2 ? 0.6 : 0));
					p[i] = q[0];
					nrm[i] = q[1];
					acr[i] = pt(0, 1, 0);
					hw[i] = hwr * (1 - i * 0.12);
				}
				strip(m, p, nrm, acr, hw, 1.5, 8, st.main, st.light, st.dark);
			}
		}
		// the sternum
		double[] ys = { r.neckY + 10, y0 + 1.5 * dy, y0 + 3 * dy + 3 };
		double[][] p = new double[3][], nrm = new double[3][], acr = new double[3][];
		for (int i = 0; i < 3; i++) {
			double[][] q = onHide(r, ys[i], 0, 1.6);
			p[i] = q[0];
			nrm[i] = q[1];
			acr[i] = pt(1, 0, 0);
		}
		m.prio = 7;
		strip(m, p, nrm, acr, new double[] { 2.2, 2.8, 2.2 }, 1.6, 8, st.light, st.main, st.main);
		// the spine down the back: vertebrae in two shades
		double[] vy = { r.neckY + 6, r.neckY + L * 0.3, r.neckY + L * 0.55, r.neckY + L * 0.8, r.waistY - 1 };
		for (int i = 0; i + 1 < vy.length; i++) {
			double[][] q0 = onHide(r, vy[i] + 0.6, Math.PI, 0.4), q1 = onHide(r, vy[i + 1] - 0.6, Math.PI, 0.4);
			strip(m, new double[][] { q0[0], q1[0] }, new double[][] { q0[1], q1[1] }, new double[][] { pt(1, 0, 0), pt(1, 0, 0) }, new double[] { 2.4, 2.0 }, 1.8, 8,
					i % 2 == 0 ? st.main : st.light, st.dark, st.dark);
		}
		// the strap across the back, from the second rib's ends
		m.prio = 6;
		double y = y0 + dy - 4.6;
		for (int side = -1; side <= 1; side += 2) {
			double[] phis = { 1.7, 2.4, 3.14 };
			double[][] sp = new double[3][], sn = new double[3][], sa = new double[3][];
			for (int i = 0; i < 3; i++) {
				double[][] q = onHide(r, y, side * phis[i], 0.4);
				sp[i] = q[0];
				sn[i] = q[1];
				sa[i] = pt(0, 1, 0);
			}
			strip(m, sp, sn, sa, new double[] { 1.8, 1.8, 1.8 }, 0.8, 8, st.leather, st.leatherDark, st.leatherDark);
		}
	}

	/** A string of teeth round the neck, just under the fur collar: five teeth hanging point-down, the middle one longest. */
	static void teeth(Mesh m, Rig r, Style st) {
		double y0 = r.neckY + 8.5;
		m.prio = 8;
		for (int k = -2; k <= 2; k++) {
			double phi = k * 0.42;
			double y = y0 + 3.0 - Math.abs(k) * 1.4;
			double[][] q = onHide(r, y, phi, 1.6);
			double len = k == 0 ? 9.0 : 7.0 - Math.abs(k) * 0.8;
			claw(m, q[0], pt(0, 1, -0.2), pt(0, 0, -1), len, k == 0 ? 2.1 : 1.8, 8, st.light, st.main);
		}
		// the thong between them
		double[][] p = new double[5][], nrm = new double[5][], acr = new double[5][];
		for (int i = 0; i < 5; i++) {
			double phi = (i - 2) * 0.55;
			double[][] q = onHide(r, y0 + 2.6 - Math.abs(i - 2) * 1.9, phi, 0.9);
			p[i] = q[0];
			nrm[i] = q[1];
			acr[i] = pt(0, 1, 0);
		}
		m.prio = 7;
		strip(m, p, nrm, acr, new double[] { 0.7, 0.7, 0.7, 0.7, 0.7 }, 0.7, 8, st.trimDark, st.trimDark, st.trimDark);
	}

	/**
	 * A beast's skull worn as a pauldron on the shoulder, its snout pointing out and forward, two stubby horns curling
	 * back: on the upper arm's label.
	 */
	static void skullPauldron(Mesh m, Rig r, Style st, int side) {
		int label = side > 0 ? 26 : 20;
		double[] c = mirror(pt(r.shoulder[0] - 2.5, r.armTop - 4.5, r.shoulder[2] + 3.5), side);
		double[] a = norm(pt(side * 0.45, 0.3, -0.85));
		double[] up = pt(0, -1, 0);
		double[][] sk = { { -9, 6.5, 5.5 }, { -3.5, 10.5, 9.0 }, { 3.0, 9.6, 8.0 }, { 9.0, 6.2, 5.0 }, { 13.0, 4.6, 3.6 } };
		int sn = 6, S = sk.length;
		int[][] rg = new int[S][];
		double[][] cs = new double[S][];
		for (int k = 0; k < S; k++) {
			cs[k] = add(c, scale(a, sk[k][0]));
			rg[k] = ringUp(m, cs[k], a, up, sk[k][1], sk[k][2], sn, label);
		}
		m.prio = 9;
		for (int k = 0; k + 1 < S; k++) {
			for (int j = 0; j < sn; j++) {
				int jj = (j + 1) % sn;
				int col = k == 0 ? st.light : j == 2 || j == 3 ? st.dark : k == 1 && (j == 1 || j == 4) ? SOCKET : k == 1 ? st.light : st.main;
				m.quadAway(rg[k][j], rg[k][jj], rg[k + 1][jj], rg[k + 1][j], col, lerp(cs[k], cs[k + 1], 0.5));
			}
		}
		cap(m, rg[0], m.v(add(c, scale(a, -11.5)), label), st.main, cs[1]);
		cap(m, rg[S - 1], m.v(add(c, scale(a, 15)), label), st.dark, cs[S - 2]);
		// two stubby horns from the crown, curling back
		double[] upv = norm(sub(up, scale(a, dot(up, a))));
		double[] sd = norm(ModelData.cross(a, upv));
		m.prio = 10;
		for (int h = -1; h <= 1; h += 2) {
			double[] root = add(cs[1], add(scale(upv, 7.0), scale(sd, h * 6.0)));
			claw(m, root, add(add(scale(upv, 0.8), scale(sd, h * 0.7)), scale(a, -0.2)), scale(a, -1), 12.0, 2.8, label, st.dark, st.light);
		}
	}

	/** A great shoulder blade over the shoulder: a thick bone plate, broad over the top, pointed down the arm, with its spine ridge. */
	static void shoulderBlade(Mesh m, Rig r, Style st, int side) {
		int label = side > 0 ? 26 : 20;
		double[] S0 = mirror(pt(r.shoulder[0] - 1.0, r.shoulder[1] + 2.0, r.shoulder[2]), side);
		double R = (r.shoulder[1] - r.armTop) + 7.5;
		double[] up = pt(0, -1, 0), out = pt(side, 0, 0), fwd = pt(0, 0, -1);
		// outline (u front-back, v down the arm from the top), and the centre
		double[][] ol = { { -1.05, -0.45 }, { 1.05, -0.45 }, { 0.8, 0.35 }, { 0.22, 1.25 }, { -0.22, 1.25 }, { -0.8, 0.35 } };
		int k = ol.length;
		int[] o = new int[k], i0 = new int[k];
		double kk = 0.95;
		for (int j = 0; j < k; j++) {
			double[] d = norm(add(up, add(scale(out, ol[j][1] * kk * 1.15 + 0.12), scale(fwd, ol[j][0] * kk))));
			o[j] = m.v(add(S0, scale(d, R + 2.2)), label);
			i0[j] = m.v(add(S0, scale(d, R - 0.2)), label);
		}
		double[] dc = norm(add(up, add(scale(out, 0.35 * kk * 1.15 + 0.12), scale(fwd, 0))));
		int oc = m.v(add(S0, scale(dc, R + 3.0)), label), ic = m.v(add(S0, scale(dc, R + 0.6)), label);
		m.prio = 9;
		for (int j = 0; j < k; j++) {
			int jj = (j + 1) % k;
			m.triAway(o[j], o[jj], oc, j == 0 || j == 5 ? st.light : st.main, S0);
			m.quadAway(o[j], o[jj], i0[jj], i0[j], st.dark, add(S0, scale(dc, R)));
			m.tri(i0[j], i0[jj], ic, st.dark, scale(dc, -1));
		}
		// the spine of the blade: a ridge across it, front to back
		double[][] p = new double[3][], nrm = new double[3][], acr = new double[3][];
		for (int j = 0; j < 3; j++) {
			double u = -0.8 + 0.8 * j;
			double[] d = norm(add(up, add(scale(out, 0.05 * kk * 1.15 + 0.12), scale(fwd, u * kk))));
			p[j] = add(S0, scale(d, R + 2.4 + (j == 1 ? 0.5 : 0)));
			nrm[j] = d;
			acr[j] = norm(sub(out, scale(d, dot(out, d))));
		}
		m.prio = 10;
		strip(m, p, nrm, acr, new double[] { 1.3, 1.6, 1.3 }, 2.0, label, st.light, st.main, st.dark);
	}

	/* ====================================================================== */
	/*                                THE CHAPS                               */
	/* ====================================================================== */

	/** Hide chaps, a long-bone splint strapped down each thigh and shin, kneecap bones, and a fur loincloth front and back. */
	static ModelData chaps(Rig r, Style st) {
		Mesh m = new Mesh();
		int n = 6;
		int hideA = shade(st.trim, -4), hideB = st.trimDark;
		for (int side = -1; side <= 1; side += 2) {
			int thigh = side > 0 ? 43 : 44, shin = side > 0 ? 36 : 33, ank = side > 0 ? 38 : 32;
			double[][] rows = { { r.waistY + 3, 1.6, 41 }, { r.hip[1] + 1, 2.0, thigh }, { r.thigh[1] - 3, 1.8, thigh }, { r.knee[1] + 2, 1.7, shin },
					{ (r.knee[1] + r.ankle[1]) / 2, 1.6, shin }, { r.ankle[1] + 1, 1.5, ank } };
			int[][] idx = new int[rows.length][];
			double[][] cs = new double[rows.length][];
			for (int k = 0; k < rows.length; k++) {
				double[] q = BaseCoat.legRing(r, rows[k][0], side, rows[k][1]);
				if (k == 0) {
					q[0] = side * Math.max(Math.abs(q[0]), 8.5);
					q[2] = Math.max(q[2], 10.0);
				}
				cs[k] = pt(q[0], rows[k][0], q[1]);
				idx[k] = hring(m, q[0], rows[k][0], q[1], q[2], q[3], n, Math.PI / n, (int) rows[k][2]);
			}
			m.prio = 1;
			loft(m, idx, cs, new int[] { hideA, hideA, hideB, hideA, hideA });
			// the splints: a long bone down the front of the thigh, another down the shin, each strapped twice
			m.prio = 3;
			splint(m, r, st, side, r.hip[1] + 5, r.thigh[1] - 1, 2.4, thigh);
			splint(m, r, st, side, r.knee[1] + 4.5, r.ankle[1] - 9.5, 2.1, shin);
			// the kneecap: a round bone boss on the front of the knee
			double y = r.knee[1] - 2.0;
			double[] lr = BaseCoat.legRing(r, y, side, 1.7);
			double[] c = pt(lr[0] + side * 0.6, y, lr[1] - lr[3] + 0.6);
			m.prio = 4;
			int[] k0 = ring(m, c, pt(0, 0, -1), 4.0, 4.4, 5, 0, shin);
			int[] k1 = ring(m, add(c, pt(0, 0, -2.2)), pt(0, 0, -1), 2.6, 2.8, 5, 0, shin);
			m.band(k0, k1, true, new int[] { st.main }, add(c, pt(0, 0, 2)));
			cap(m, k1, m.v(add(c, pt(0, 0, -3.0)), shin), st.light, add(c, pt(0, 0, 1)));
		}
		// the loincloth: a fur flap hanging front and back from the belt, on the static belt label
		double[] s = slice(r.legs, r.waistY + 3, 0);
		double bot = r.hip[1] + (r.thigh[1] - r.hip[1]) * 0.42;
		m.prio = 5;
		flap(m, r.waistY + 1.5, bot, s[2] - 2.8, -1, 6.0, 4.8, st);
		flap(m, r.waistY + 1.5, bot - 3, s[3] + 2.8, 1, 6.5, 5.2, st);
		return m.d;
	}

	/**
	 * One loincloth flap at depth z (dir -1 front, +1 back), from y0 to y1, half-width w0 at the top, w1 at the bottom,
	 * swinging out a little: fur outside, hide inside, the lower edge cut into three tongues.
	 */
	static void flap(Mesh m, double y0, double y1, double z, int dir, double w0, double w1, Style st) {
		double[] ys = { y0, (y0 + y1) / 2, y1 };
		double[] zs = { z, z + dir * 1.2, z + dir * 2.6 };
		int cols = 3;
		int[][] o = new int[3][cols + 1], in = new int[3][cols + 1];
		for (int q = 0; q < 3; q++) {
			double hw = w0 + (w1 - w0) * q / 2.0;
			for (int j = 0; j <= cols; j++) {
				double u = -1 + 2.0 * j / cols;
				double tongue = q == 2 ? (j == 1 || j == 2 ? 3.0 : 0) : 0;
				double bow = (1 - u * u) * 1.0;
				o[q][j] = m.v(pt(u * hw, ys[q] + tongue, zs[q] + dir * (0.6 + bow)), 41);
				in[q][j] = m.v(pt(u * hw, ys[q] + tongue, zs[q] + dir * (bow - 0.4)), 41);
			}
		}
		double[] outv = pt(0, 0, dir);
		for (int q = 0; q < 2; q++) {
			for (int j = 0; j < cols; j++) {
				m.quad(o[q][j], o[q][j + 1], o[q + 1][j + 1], o[q + 1][j], q == 0 ? st.leather : shade(st.leather, -6), outv);
				m.quad(in[q][j], in[q][j + 1], in[q + 1][j + 1], in[q + 1][j], st.trimDark, scale(outv, -1));
			}
		}
		for (int j = 0; j < cols; j++) { // the lower edge
			m.quad(o[2][j], o[2][j + 1], in[2][j + 1], in[2][j], st.leatherDark, pt(0, 1, 0));
		}
	}

	/** A long bone splint down the front of a leg from y0 to y1: a knobbed bone a little out from the hide, strapped near each end. */
	static void splint(Mesh m, Rig r, Style st, int side, double y0, double y1, double rad, int label) {
		double phi = side * 0.55;
		double[] l0 = BaseCoat.legRing(r, y0, side, 1.7 + rad + 0.6), l1 = BaseCoat.legRing(r, y1, side, 1.7 + rad + 0.6);
		double[] p0 = ell(l0[0], y0, l0[1], l0[2], l0[3], phi), p1 = ell(l1[0], y1, l1[1], l1[2], l1[3], phi);
		double[] a = norm(sub(p1, p0));
		double len = Math.sqrt(dot(sub(p1, p0), sub(p1, p0)));
		// stations along the bone: knob, neck, shaft, shaft, neck, knob
		double[][] st6 = { { 0, rad * 1.45 }, { 0.14, rad * 1.05 }, { 0.86, rad * 1.05 }, { 1.0, rad * 1.45 } };
		int[][] rg = new int[st6.length][];
		double[][] cs = new double[st6.length][];
		for (int k = 0; k < st6.length; k++) {
			cs[k] = add(p0, scale(a, st6[k][0] * len));
			rg[k] = ring(m, cs[k], a, st6[k][1], st6[k][1] * 0.85, 4, Math.PI / 4, label);
		}
		loft(m, rg, cs, new int[] { st.light, st.main, st.light });
		cap(m, rg[0], m.v(add(p0, scale(a, -rad * 1.4)), label), st.light, cs[1]);
		cap(m, rg[st6.length - 1], m.v(add(p1, scale(a, rad * 1.4)), label), st.light, cs[st6.length - 2]);
		// two straps round the front of the leg over the bone
		for (double f : new double[] { 0.25, 0.75 }) {
			double y = y0 + (y1 - y0) * f;
			double[] lr = BaseCoat.legRing(r, y, side, 1.7 + 0.3);
			double[] lo = BaseCoat.legRing(r, y, side, 1.7 + rad * 2 + 0.9);
			int sg = 4;
			int[] t0 = new int[sg + 1], t1 = new int[sg + 1], b0 = new int[sg + 1], b1 = new int[sg + 1];
			for (int j = 0; j <= sg; j++) {
				double ph = phi - 1.0 + 2.0 * j / sg;
				double bump = Math.cos((ph - phi) * 1.6);
				double[] q = j == 0 || j == sg ? lr : bump > 0.5 ? lo : lr;
				t0[j] = m.v(ell(q[0], y - 0.9, q[1], q[2], q[3], ph), label);
				t1[j] = m.v(ell(q[0], y + 0.9, q[1], q[2], q[3], ph), label);
			}
			m.prio = 4;
			m.band(t0, t1, false, new int[] { st.leather }, pt(lr[0], y, lr[1]));
		}
		m.prio = 3;
	}

	/* ====================================================================== */
	/*                                THE BOOTS                               */
	/* ====================================================================== */

	/** Hide boots wrapped to above the ankle, a folded cuff, a bone cap over the toes and three claws curling out of it. */
	static ModelData boots(Rig r, Style st) {
		Mesh m = new Mesh();
		int[] fb = r.feetB;
		int n = 6;
		for (int side = -1; side <= 1; side += 2) {
			int ank = side > 0 ? 38 : 32, shin = side > 0 ? 36 : 33;
			double topY = r.ankle[1] - 9;
			double[] fe = extent(r.feet, side > 0 ? 47 : 48);
			double x = (fe[0] + fe[3]) / 2, hw = (fe[3] - fe[0]) / 2;
			double sole = fb[4] + 0.5, back = fb[5] + 1.0, front = fb[2] - 1.0;
			double fcz = (front + back) / 2, flen = (back - front) / 2;
			double[] q0 = BaseCoat.legRing(r, topY, side, 2.4), q1 = BaseCoat.legRing(r, r.ankle[1], side, 2.6);
			int[][] idx = new int[4][];
			double[][] cs = new double[4][];
			cs[0] = pt(q0[0], topY, q0[1]);
			idx[0] = hring(m, q0[0], topY, q0[1], q0[2], q0[3], n, Math.PI / n, shin);
			cs[1] = pt(q1[0], r.ankle[1] + 1, q1[1] + 0.5);
			idx[1] = hring(m, q1[0], r.ankle[1] + 1, q1[1] + 0.5, q1[2], q1[3] + 0.5, n, Math.PI / n, ank);
			double[][] foot = { { sole - 6.5, hw + 2.0, flen + 0.2 }, { sole - 1.0, hw + 2.4, flen + 1.0 } };
			for (int k = 0; k < 2; k++) {
				cs[2 + k] = pt(x, foot[k][0], fcz);
				idx[2 + k] = hring(m, x, foot[k][0], fcz, foot[k][1], foot[k][2], n, Math.PI / n, ank);
			}
			m.prio = 2;
			loft(m, idx, cs, new int[] { st.trim, shade(st.trim, -4), st.trim });
			cap(m, idx[3], m.v(pt(x, sole + 0.5, fcz), ank), st.leatherDark, pt(x, sole - 4, fcz));
			// the folded cuff at the top
			int[] cf = hring(m, q0[0], topY + 3.5, q0[1], q0[2] + 1.6, q0[3] + 1.6, n, Math.PI / n, shin);
			m.prio = 3;
			m.band(idx[0], cf, true, new int[] { st.leather }, pt(q0[0], topY + 1, q0[1]));
			// the bone toe cap: a shell over the front of the foot
			double zt = front, cy = sole - 3.0;
			int arc = 5;
			int[] a0 = new int[arc], a1 = new int[arc];
			for (int j = 0; j < arc; j++) {
				double t = -Math.PI * 0.55 + Math.PI * 1.1 * j / (arc - 1);
				a0[j] = m.v(pt(x + Math.sin(t) * (hw + 3.0), cy - Math.cos(t) * 4.6, zt + 8.0), ank);
				a1[j] = m.v(pt(x + Math.sin(t) * (hw + 2.0), cy - Math.cos(t) * 3.4, zt + 2.5), ank);
			}
			int tip = m.v(pt(x, cy - 0.5, zt - 1.0), ank);
			m.prio = 4;
			double[] inC = pt(x, cy + 1, zt + 6);
			for (int j = 0; j + 1 < arc; j++) {
				m.quadAway(a0[j], a0[j + 1], a1[j + 1], a1[j], j == 1 || j == 2 ? st.light : st.main, inC);
				m.triAway(a1[j], a1[j + 1], tip, st.main, inC);
			}
			// the claws, curling down out of the cap
			m.prio = 5;
			for (int k = -1; k <= 1; k++) {
				double[] root = pt(x + k * (hw * 0.75), cy - 0.5, zt + 1.5);
				claw(m, root, pt(k * 0.15, 0.15, -1), pt(0, 1, 0), 7.0, 1.7, ank, st.light, st.dark);
			}
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                              THE VAMBRACES                             */
	/* ====================================================================== */

	/** Hide vambraces flaring to the wrist with two bone strips down the back of the forearm, and three knuckle-bones on each hand. */
	static ModelData vambraces(Rig r, Style st) {
		Mesh m = new Mesh();
		for (int side = -1; side <= 1; side += 2) {
			int hand = side > 0 ? 28 : 27, fore = side > 0 ? 22 : 19;
			double[] E = mirror(r.elbow, side), W = mirror(pt(r.wrist[0], r.wristY - 1, r.wrist[2]), side);
			double foreR = Math.max(4, (extent(r.arms, 19)[3] - extent(r.arms, 19)[0]) / 2);
			double[] Af = norm(sub(W, E));
			double fl = Math.sqrt(dot(sub(W, E), sub(W, E)));
			int n = 5;
			double[][] rr = { { 0.18, foreR + 1.2 }, { 0.6, foreR + 1.7 }, { 1.0, foreR + 2.4 } };
			int[][] rg = new int[rr.length][];
			double[][] cs = new double[rr.length][];
			for (int k = 0; k < rr.length; k++) {
				cs[k] = add(E, scale(Af, rr[k][0] * fl));
				rg[k] = ring(m, cs[k], Af, rr[k][1], rr[k][1] * 0.95, n, 0.3, fore);
			}
			m.prio = 3;
			loft(m, rg, cs, new int[] { st.trim, shade(st.trim, -4) });
			// the flared lip at the wrist
			int[] lip = ring(m, add(W, scale(Af, 1.4)), Af, foreR + 3.4, foreR + 3.2, n, 0.3, fore);
			m.band(rg[2], lip, true, new int[] { st.trimDark }, add(W, scale(Af, -2)));
			// two bone strips down the back (outside) of the forearm
			m.prio = 4;
			for (int k = -1; k <= 1; k += 2) {
				double[] off = add(scale(pt(side, 0, 0), foreR + 1.9), pt(0, 0, k * 2.4));
				double[] p0 = add(add(E, scale(Af, 0.22 * fl)), off), p1 = add(add(E, scale(Af, 0.92 * fl)), add(off, scale(pt(side, 0, 0), 0.6)));
				double[][] p = { p0, p1 };
				double[][] nrm = { pt(side, 0, 0), pt(side, 0, 0) };
				double[] ac = norm(ModelData.cross(Af, pt(side, 0, 0)));
				double[][] acr = { ac, ac };
				strip(m, p, nrm, acr, new double[] { 1.2, 1.3 }, 1.5, fore, st.main, st.light, st.dark);
			}
			// the knuckle-bones, across the back of the hand
			double[] he = extent(r.hands, 27);
			double[] H = mirror(r.hand, side);
			double outX = side > 0 ? -he[0] : he[0];
			double ky = he[4] - 4.0;
			m.prio = 5;
			for (int k = -1; k <= 1; k++) {
				double z = H[2] + k * 2.8;
				double[] root = pt(outX + side * 0.2, ky, z);
				spike(m, root, add(root, pt(side * 2.0, 0.3, 0)), 1.7, 4, hand, st.light, st.main);
			}
			balance(m, hand, BaseCoat.centroid(r.hands, hand));
		}
		return m.d;
	}
}
