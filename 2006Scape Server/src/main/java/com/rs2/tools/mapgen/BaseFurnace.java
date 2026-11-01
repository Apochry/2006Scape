package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ArmourForge.add;
import static com.rs2.tools.mapgen.ArmourForge.dot;
import static com.rs2.tools.mapgen.ArmourForge.norm;
import static com.rs2.tools.mapgen.ArmourForge.scale;
import static com.rs2.tools.mapgen.ArmourForge.sub;
import static com.rs2.tools.mapgen.BaseBannerman.flat;
import static com.rs2.tools.mapgen.BaseCoat.extent;
import static com.rs2.tools.mapgen.BaseCoat.lerp;
import static com.rs2.tools.mapgen.BaseCoat.mirror;
import static com.rs2.tools.mapgen.BaseCoat.pt;
import static com.rs2.tools.mapgen.BaseCoat.slice;
import static com.rs2.tools.mapgen.BaseParka.anchor;
import static com.rs2.tools.mapgen.BaseParka.apart;
import static com.rs2.tools.mapgen.BaseParka.basis;
import static com.rs2.tools.mapgen.BaseParka.cap;
import static com.rs2.tools.mapgen.BaseParka.hring;
import static com.rs2.tools.mapgen.BaseParka.loft;
import static com.rs2.tools.mapgen.BaseParka.ring;
import static com.rs2.tools.mapgen.BaseParka.shade;
import static com.rs2.tools.mapgen.BaseParka.trunk;

import java.io.IOException;

import com.rs2.tools.mapgen.ArmourForge.Frame;
import com.rs2.tools.mapgen.ArmourSets.Style;
import com.rs2.tools.mapgen.BaseCoat.Mesh;
import com.rs2.tools.mapgen.BaseCoat.Rig;

/**
 * The Emberforged base: Dorrin's forge masterwork, which makes its wearer a walking furnace. Blackened steel, bronze
 * edging, and ember-orange glowing in the seams and the grate; top-heavy and massive. Built from scratch on the rune
 * plate's rig for the Emberforged (A36) draft set; see NewBases.
 *
 * <ul>
 * <li>Helm (named "... full helm", so head and jaw are hidden): a squat riveted furnace-helm, a heavy chamfered iron box
 * wider than it is tall on a flared neck-guard, a glowing slot grille for eyes, bronze-banded, and a chimney stack at
 * the top of the back venting embers.</li>
 * <li>Platebody (the plate arms are the second model): a massive barrel-chested cuirass with a furnace door in the
 * breast, its grate glowing orange inside; oversized blocky riveted pauldrons in two lames with the forge-light in the
 * join; glowing vents across the back; a thick belt, and a heavy leather smith's apron hanging from it over the plate
 * to the knees.</li>
 * <li>Platelegs: heavy plate with bronze-lipped cuisses and greaves, the forge-light in a seam over each knee.</li>
 * <li>Boots: huge anvil-heavy blocks of boots to mid-shin on flat, thick bronze-shod soles.</li>
 * <li>Gauntlets: oversized riveted forge gauntlets: huge blocky hands with a bronze knuckle plate and big flared cuffs
 * with a glowing seam.</li>
 * <li>Shield: an anvil, seen from the side: a flat face, a horn out to one side, the waist and the foot, with a bar of
 * white-hot steel lying across the face.</li>
 * </ul>
 *
 * <p>Labels, as BaseBannerman's: the helm on the skull 1; the cuirass, door, vents and belt on the chest 8 / belly 4;
 * the pauldrons on the upper arms 20 / 26; the apron's two halves (a slit between) on the thighs 43 / 44 below the
 * belt; the plate arms on 20 / 26 and 19 / 22; legs on 41, 43 / 44, 36 / 33, 38 / 32; the boots' shaft top on the
 * shins, the rest on the ankles; the gauntlets' hands on 27 / 28 (centred on the kit's hand), their cuffs on 19 / 22;
 * the shield on 28. Every piece anchors the pivots of the kit it hides (BaseParka.anchor), and the legs keep each side
 * off the middle (BaseParka.apart).
 */
final class BaseFurnace {

	private BaseFurnace() {
	}

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		Rig r = BaseCoat.rig(af, female);
		switch (slot) {
		case ArmourSets.HELM:
			return new ModelData[] { anchor(helm(r, st), r.head), null };
		case ArmourSets.BODY:
			return new ModelData[] { anchor(apart(body(r, st)), r.torso), anchor(arms(r, st), r.arms) };
		case ArmourSets.LEGS:
			return new ModelData[] { anchor(apart(legs(r, st)), r.legs), null };
		case ArmourSets.SHIELD:
			return new ModelData[] { shield(af, st), null };
		case ArmourSets.BOOTS:
			return new ModelData[] { anchor(apart(boots(r, st)), r.feet), null };
		default:
			return new ModelData[] { anchor(gauntlets(r, st), r.hands), null };
		}
	}

	/** The inventory model for the slot, or null to let ArmourSets build it the usual way from the worn model(s). */
	static ModelData inventory(ArmourForge af, Style st, int slot, ModelData[] worn) throws IOException {
		return null;
	}

	/* ====================================================================== */
	/*                                 HELPERS                                */
	/* ====================================================================== */

	/** A chamfered rectangle round a vertical axis at height y: half-width w (x), half-depth d (z), corners cut by c; 8 points from the front. */
	static int[] box(Mesh m, double cx, double y, double cz, double w, double d, double c, int label) {
		double[][] p = { { -(w - c), -d }, { w - c, -d }, { w, -d + c }, { w, d - c }, { w - c, d }, { -(w - c), d }, { -w, d - c }, { -w, -d + c } };
		int[] out = new int[8];
		for (int i = 0; i < 8; i++) {
			out[i] = m.v(pt(cx + p[i][0], y, cz + p[i][1]), label);
		}
		return out;
	}

	/** As box, round an axis a through c (u across, w the other way, as BaseParka.basis). */
	static int[] boxAx(Mesh m, double[] c, double[] a, double w, double d, double ch, int label) {
		double[][] b = basis(a);
		double[][] p = { { -(w - ch), -d }, { w - ch, -d }, { w, -d + ch }, { w, d - ch }, { w - ch, d }, { -(w - ch), d }, { -w, d - ch }, { -w, -d + ch } };
		int[] out = new int[8];
		for (int i = 0; i < 8; i++) {
			out[i] = m.v(add(c, add(scale(b[0], p[i][0]), scale(b[1], p[i][1]))), label);
		}
		return out;
	}

	/** A square-based rivet head at p standing out along n. */
	static void rivet(Mesh m, double[] p, double[] n, double r, int col, int label) {
		double[][] b = basis(norm(n));
		int[] q = new int[4];
		for (int i = 0; i < 4; i++) {
			double a = Math.PI / 2 * i + Math.PI / 4;
			q[i] = m.v(add(p, add(scale(b[0], Math.cos(a) * r), scale(b[1], Math.sin(a) * r))), label);
		}
		int apex = m.v(add(p, scale(norm(n), r * 0.9)), label);
		cap(m, q, apex, col, sub(p, scale(norm(n), 2)));
	}

	/** An ember: a small tetrahedron at p. */
	static void ember(Mesh m, double[] p, double r, int col, int tip, int label) {
		int a = m.v(add(p, pt(0, -r * 1.3, 0)), label), b = m.v(add(p, pt(-r, r * 0.5, -r * 0.6)), label), c = m.v(add(p, pt(r, r * 0.5, -r * 0.6)), label),
				d = m.v(add(p, pt(0, r * 0.5, r)), label);
		m.triAway(a, b, c, tip, p);
		m.triAway(a, c, d, col, p);
		m.triAway(a, d, b, col, p);
		m.triAway(b, d, c, col, p);
	}

	/* ====================================================================== */
	/*                                THE HELM                                */
	/* ====================================================================== */

	/** The furnace-helm: a squat chamfered iron box on a flared neck-guard, a glowing grille, rivets, a chimney venting embers. */
	static ModelData helm(Rig r, Style st) {
		Mesh m = new Mesh();
		int[] hb = r.headB;
		double hx = Math.max(-hb[0], hb[3]);
		double top = hb[1] - 2, bot = hb[4] + 2, front = hb[2] - 3, back = hb[5] + 4;
		double cz = (front + back) / 2, D = (back - front) / 2, W = hx + 8;
		// the box: neck-guard flare, bronze band, the body, the bevel to the top
		double[][] rows = { { bot + 7, W + 4, D + 3, 4 }, { bot, W + 1.2, D + 1.2, 3 }, { bot - 3, W + 1.2, D + 1.2, 3 }, { bot - 3.5, W, D, 3 }, { top + 3, W, D, 3 },
				{ top, W - 2.5, D - 2.5, 2 } };
		int[][] rg = new int[rows.length][];
		double[][] cs = new double[rows.length][];
		for (int k = 0; k < rows.length; k++) {
			cs[k] = pt(0, rows[k][0], cz);
			rg[k] = box(m, 0, rows[k][0], cz, rows[k][1], rows[k][2], rows[k][3], 1);
		}
		m.prio = 5;
		loft(m, rg, cs, new int[] { st.dark, st.trim, st.trimDark, st.main, st.light });
		int lid = m.v(pt(0, top - 0.5, cz), 1);
		cap(m, rg[rows.length - 1], lid, st.dark, pt(0, top + 5, cz));
		// the grille: a bronze frame, the fire behind, three bars across
		double gy = hb[1] + (hb[4] - hb[1]) * 0.42, fz = cz - D;
		Frame F = new Frame(pt(0, 0, fz), pt(1, 0, 0), pt(0, 1, 0), pt(0, 0, -1));
		double gw = W - 3.5;
		m.prio = 6;
		flat(m, F.o, F.u, F.v, F.n, new double[][] { { -gw - 1.5, gy - 4.2 }, { gw + 1.5, gy - 4.2 }, { gw + 1.5, gy + 4.2 }, { -gw - 1.5, gy + 4.2 } }, 0.6, st.trim, 1, false);
		m.prio = 7;
		flat(m, F.o, F.u, F.v, F.n, new double[][] { { -gw, gy - 2.6 }, { gw, gy - 2.6 }, { gw, gy + 2.6 }, { -gw, gy + 2.6 } }, 1.2, st.accent, 1, false);
		flat(m, F.o, F.u, F.v, F.n, new double[][] { { -gw * 0.6, gy - 1.2 }, { gw * 0.6, gy - 1.2 }, { gw * 0.6, gy + 1.2 }, { -gw * 0.6, gy + 1.2 } }, 1.5, st.core, 1, false);
		m.prio = 8;
		for (int k = -1; k <= 1; k++) {
			double bx = k * gw * 0.5;
			flat(m, F.o, F.u, F.v, F.n, new double[][] { { bx - 1.0, gy - 3.4 }, { bx + 1.0, gy - 3.4 }, { bx + 1.0, gy + 3.4 }, { bx - 1.0, gy + 3.4 } }, 2.2, st.dark, 1, false);
		}
		// rivets down the front corners and over the brow
		m.prio = 7;
		for (int s = -1; s <= 1; s += 2) {
			for (int k = 0; k < 2; k++) {
				double y = k == 0 ? top + 6 : bot - 6;
				rivet(m, pt(s * (W - 2.2), y, fz - 0.2), pt(0, 0, -1), 1.6, st.trim, 1);
			}
		}
		// the chimney, up from the top of the back, venting embers
		double[] c0 = pt(0, top + 1, cz + D * 0.45), c1 = pt(0, top - 14, cz + D * 0.6);
		double[] ax = norm(sub(c1, c0));
		int n = 6;
		int[] s0 = ring(m, c0, ax, 5.2, 5.2, n, 0, 1), s1 = ring(m, c1, ax, 4.6, 4.6, n, 0, 1);
		double[] c2 = add(c1, scale(ax, 1.0)), c3 = add(c1, scale(ax, 4.0));
		int[] s2 = ring(m, c2, ax, 7.2, 7.2, n, 0, 1), s3 = ring(m, c3, ax, 6.8, 6.8, n, 0, 1);
		m.prio = 6;
		m.band(s0, s1, true, new int[] { st.main }, lerp(c0, c1, 0.5));
		m.band(s1, s2, true, new int[] { st.trimDark }, c1);
		m.band(s2, s3, true, new int[] { st.trim }, lerp(c1, c3, 0.5));
		int mouth = m.v(add(c3, scale(ax, -1.5)), 1);
		for (int j = 0; j < n; j++) {
			m.tri(s3[j], s3[(j + 1) % n], mouth, st.core, ax);
		}
		m.prio = 8;
		double[][] em = { { 0.5, -5, 1 }, { -2.5, -10, 2.5 }, { 2, -15, 4.5 }, { -1, -20, 6.5 } };
		for (int k = 0; k < em.length; k++) {
			ember(m, add(c3, pt(em[k][0], em[k][1], em[k][2])), 1.6 - k * 0.2, st.accent, k % 2 == 0 ? st.core : st.accent, 1);
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                                THE BODY                                */
	/* ====================================================================== */

	/** The barrel cuirass with its furnace door, the blocky pauldrons, the back vents, the belt and the smith's apron. */
	static ModelData body(Rig r, Style st) {
		Mesh m = new Mesh();
		int n = 10;
		double L = r.waistY - r.neckY;
		// y, widen x, front, back, label
		double[][] rows = { { r.neckY + 1, 2.0, 2.0, 2.0, 8 }, { r.neckY + 5, 4.5, 6.0, 4.5, 8 }, { r.neckY + L * 0.32, 6.5, 11.0, 6.5, 8 },
				{ r.neckY + L * 0.62, 6.0, 10.0, 6.0, 8 }, { r.waistY - 1, 3.6, 5.0, 4.2, 4 } };
		int[][] rg = new int[rows.length][];
		double[][] cs = new double[rows.length][], dim = new double[rows.length][];
		for (int i = 0; i < rows.length; i++) {
			dim[i] = trunk(r, rows[i][0], rows[i][1], rows[i][2], rows[i][3]);
			if (i == 0) {
				dim[i][1] = Math.min(dim[i][1], 12);
			}
			cs[i] = pt(0, rows[i][0], dim[i][0]);
			rg[i] = hring(m, 0, rows[i][0], dim[i][0], dim[i][1], dim[i][2], n, 0, (int) rows[i][4]);
		}
		m.prio = 3;
		loft(m, rg, cs, new int[] { st.dark, st.light, st.main, st.dark });
		// a bronze band round the barrel under the door
		int[] b0 = hring(m, 0, rows[3][0] - 1.5, dim[3][0], dim[3][1] + 0.6, dim[3][2] + 0.6, n, 0, 8),
				b1 = hring(m, 0, rows[3][0] + 2.0, dim[3][0], dim[3][1] + 0.3, dim[3][2] + 0.3, n, 0, 8);
		m.prio = 4;
		m.band(b0, b1, true, new int[] { st.trim }, cs[3]);
		// ------------------------------------------------ the furnace door in the breast
		double dy = rows[2][0] + 1, dz = dim[2][0] - dim[2][2];
		Frame F = new Frame(pt(0, 0, dz), pt(1, 0, 0), pt(0, 1, 0), pt(0, 0, -1));
		double dw = Math.max(9.0, dim[2][1] * 0.42), dh = 9.0;
		m.prio = 5;
		flat(m, F.o, F.u, F.v, F.n, new double[][] { { -dw, dy - dh }, { dw, dy - dh }, { dw, dy + dh }, { -dw, dy + dh } }, 0.4, st.trim, 8, false);
		m.prio = 6;
		flat(m, F.o, F.u, F.v, F.n, new double[][] { { -dw + 2, dy - dh + 2 }, { dw - 2, dy - dh + 2 }, { dw - 2, dy + dh - 2 }, { -dw + 2, dy + dh - 2 } }, 1.0, st.accent, 8, false);
		m.prio = 7;
		flat(m, F.o, F.u, F.v, F.n, new double[][] { { -dw + 3.5, dy - 1 }, { dw - 3.5, dy - 1 }, { dw - 3.5, dy + dh - 3 }, { -dw + 3.5, dy + dh - 3 } }, 1.4, st.core, 8, false);
		m.prio = 8;
		for (int k = -1; k <= 1; k++) { // the grate's bars
			double bx = k * (dw - 2) * 0.5;
			flat(m, F.o, F.u, F.v, F.n, new double[][] { { bx - 0.9, dy - dh + 1.5 }, { bx + 0.9, dy - dh + 1.5 }, { bx + 0.9, dy + dh - 1.5 }, { bx - 0.9, dy + dh - 1.5 } }, 2.0, st.dark, 8,
					false);
		}
		flat(m, F.o, F.u, F.v, F.n, new double[][] { { -dw + 1.5, dy - 1.0 }, { dw - 1.5, dy - 1.0 }, { dw - 1.5, dy + 0.8 }, { -dw + 1.5, dy + 0.8 } }, 2.0, st.dark, 8, false);
		for (int s = -1; s <= 1; s += 2) { // rivets at the frame's corners, the hinge on the right
			for (int t = -1; t <= 1; t += 2) {
				rivet(m, pt(s * (dw - 1), dy + t * (dh - 1), dz - 0.6), pt(0, 0, -1), 1.4, st.trimDark, 8);
			}
		}
		flat(m, F.o, F.u, F.v, F.n, new double[][] { { -dw - 1.6, dy - 3 }, { -dw + 0.5, dy - 3 }, { -dw + 0.5, dy + 3 }, { -dw - 1.6, dy + 3 } }, 1.4, st.trimDark, 8, false);
		// ------------------------------------------------ the vents across the back
		Frame B = new Frame(pt(0, 0, dim[2][0] + dim[2][2]), pt(1, 0, 0), pt(0, 1, 0), pt(0, 0, 1));
		m.prio = 6;
		for (int k = 0; k < 3; k++) {
			double vy = rows[1][0] + 5 + k * 5.5, vw = dim[2][1] * (0.55 - k * 0.06);
			flat(m, B.o, B.u, B.v, B.n, new double[][] { { -vw - 1, vy - 1.8 }, { vw + 1, vy - 1.8 }, { vw + 1, vy + 1.8 }, { -vw - 1, vy + 1.8 } }, 0.5, st.trimDark, 8, false);
			flat(m, B.o, B.u, B.v, B.n, new double[][] { { -vw, vy - 0.8 }, { vw, vy - 0.8 }, { vw, vy + 0.8 }, { -vw, vy + 0.8 } }, 1.0, k == 1 ? st.core : st.accent, 8, false);
		}
		// ------------------------------------------------ the belt and the apron
		double[] w = dim[rows.length - 1];
		double by = r.waistY;
		int[] t0 = hring(m, 0, by - 4, w[0], w[1] + 1.4, w[2] + 1.4, n, 0, 4), t1 = hring(m, 0, by + 2.5, w[0], w[1] + 1.8, w[2] + 1.8, n, 0, 4);
		m.prio = 5;
		m.band(t0, t1, true, new int[] { st.leatherDark }, pt(0, by, w[0]));
		apron(m, r, st, w, by);
		Frame Fb = new Frame(pt(0, 0, w[0] - w[2] - 2.2), pt(1, 0, 0), pt(0, 1, 0), pt(0, 0, -1));
		m.prio = 7;
		flat(m, Fb.o, Fb.u, Fb.v, Fb.n, new double[][] { { -3.6, by - 4.6 }, { 3.6, by - 4.6 }, { 3.6, by + 3.4 }, { -3.6, by + 3.4 } }, 0.8, st.trim, 4, false);
		flat(m, Fb.o, Fb.u, Fb.v, Fb.n, new double[][] { { -1.8, by - 2.6 }, { 1.8, by - 2.6 }, { 1.8, by + 1.4 }, { -1.8, by + 1.4 } }, 1.4, st.accent, 4, false);
		// ------------------------------------------------ the pauldrons
		for (int side = -1; side <= 1; side += 2) {
			pauldron(m, r, st, side);
		}
		return m.d;
	}

	/** The smith's apron: heavy leather from the belt to the knees in two halves (a slit between), its hem scorched. */
	static void apron(Mesh m, Rig r, Style st, double[] w, double by) {
		double hem = r.knee[1] + 2;
		double hw0 = w[1] * 0.8, hw1 = w[1] * 0.9;
		double[][] pr = { { by + 1, hw0, 4 }, { by + 10, hw0 + 1, 0 }, { hem - 3, hw1, 0 }, { hem, hw1, 0 } };
		double[] zs = new double[pr.length];
		double clear = 1e9;
		for (int k = 0; k < pr.length; k++) {
			double z;
			if (k == 0) {
				z = w[0] - w[2] - 1.6;
			} else {
				double[] sl = slice(r.legs, Math.min(pr[k][0], r.knee[1] + 4), 0);
				z = sl[2] - 4.5;
			}
			clear = Math.min(clear, z);
			zs[k] = clear;
		}
		int leather = shade(st.leather, 14);
		int cols = 2;
		for (int side = -1; side <= 1; side += 2) {
			int[][] v = new int[pr.length][cols + 1];
			for (int k = 0; k < pr.length; k++) {
				int lab = pr[k][2] > 0 ? (int) pr[k][2] : side > 0 ? 43 : 44;
				for (int j = 0; j <= cols; j++) {
					double u = j / (double) cols;
					double x = side * (0.8 + u * (pr[k][1] - 0.8));
					v[k][j] = m.v(pt(x, pr[k][0], zs[k] + u * u * 2.5), lab);
				}
			}
			double[] out = pt(0, 0, -1);
			m.prio = 6;
			for (int k = 0; k + 1 < pr.length; k++) {
				int col = k == pr.length - 2 ? st.leatherDark : k == 0 ? shade(leather, -4) : leather;
				for (int j = 0; j < cols; j++) {
					m.quad(v[k][j], v[k][j + 1], v[k + 1][j + 1], v[k + 1][j], col, out);
					if (k >= 1) {
						m.quad(v[k][j], v[k + 1][j], v[k + 1][j + 1], v[k][j + 1], st.leatherDark, scale(out, -1));
					}
				}
			}
		}
		// Dorrin's mark, branded on the left half: an anvil with a flame rising off it
		double mx = 0.8 + hw0 * 0.5, my = by + 15;
		Frame F = new Frame(pt(0, 0, zs[1] - 0.2), pt(1, 0, 0), pt(0, 1, 0), pt(0, 0, -1));
		m.prio = 7;
		flat(m, F.o, F.u, F.v, F.n, new double[][] { { mx - 3.6, my }, { mx + 4.0, my }, { mx + 4.0, my + 1.8 }, { mx - 3.6, my + 1.8 } }, 0.6, st.trim, 43, false);
		flat(m, F.o, F.u, F.v, F.n, new double[][] { { mx - 3.6, my }, { mx - 3.6, my + 1.8 }, { mx - 6.4, my + 0.4 } }, 0.6, st.trim, 43, false);
		flat(m, F.o, F.u, F.v, F.n, new double[][] { { mx - 1.4, my + 1.8 }, { mx + 1.8, my + 1.8 }, { mx + 3.2, my + 4.6 }, { mx - 2.8, my + 4.6 } }, 0.6, st.trim, 43, false);
		flat(m, F.o, F.u, F.v, F.n, new double[][] { { mx - 1.8, my - 0.2 }, { mx + 0.2, my - 6.0 }, { mx + 2.2, my - 0.2 } }, 0.8, st.accent, 43, false);
	}

	/**
	 * An oversized blocky pauldron: two chamfered boxes stepped down the shoulder, bronze-lipped, the forge-light in
	 * the join between them, rivets on top. On the upper arm (20 / 26).
	 */
	static void pauldron(Mesh m, Rig r, Style st, int side) {
		int label = side > 0 ? 26 : 20;
		double[] J = mirror(pt(r.shoulder[0], r.armTop + 5, r.shoulder[2]), side);
		double armR = Math.max(5, (extent(r.arms, 21)[3] - extent(r.arms, 21)[0]) / 2);
		double[] axis = norm(pt(side * 0.42, -0.9, 0));
		double[] c = add(J, pt(side * 3.0, -4.0, 0));
		double hw = armR + 7.5, hd = armR + 7.0;
		// upper box: from under the lip to the flat top
		double[][] tiers = { { -4.0, 1.04, 2.5 }, { -1.5, 1.04, 2.5 }, { -1.0, 1.0, 2.5 }, { 6.5, 0.94, 2.5 }, { 8.5, 0.72, 2.5 } };
		int[][] rg = new int[tiers.length][];
		double[][] cs = new double[tiers.length][];
		for (int k = 0; k < tiers.length; k++) {
			cs[k] = add(c, scale(axis, tiers[k][0]));
			rg[k] = boxAx(m, cs[k], axis, hw * tiers[k][1], hd * tiers[k][1], tiers[k][2] * tiers[k][1] + 1, label);
		}
		m.prio = 10;
		loft(m, rg, cs, new int[] { st.trim, st.trimDark, st.main, st.light });
		int top = m.v(add(c, scale(axis, 8.8)), label);
		cap(m, rg[tiers.length - 1], top, st.light, c);
		// the lower lame, its top tucked under, the glow in the join
		double[] c2 = add(c, scale(axis, -7.0));
		double[] l0 = add(c2, scale(axis, 3.5)), l1 = add(c2, scale(axis, -1.5)), l2 = add(c2, scale(axis, -3.0));
		int[] g0 = boxAx(m, l0, axis, hw * 0.9, hd * 0.92, 3, label), g1 = boxAx(m, l1, axis, hw * 0.98, hd * 1.0, 3, label),
				g2 = boxAx(m, l2, axis, hw * 1.0, hd * 1.02, 3, label);
		m.band(g0, g1, true, new int[] { st.main }, c2);
		m.band(g1, g2, true, new int[] { st.trim }, c2);
		int[] e0 = boxAx(m, add(c, scale(axis, -4.0)), axis, hw * 0.96, hd * 0.96, 3, label), e1 = boxAx(m, add(c, scale(axis, -3.0)), axis, hw * 0.98, hd * 0.98, 3, label);
		m.prio = 9;
		m.band(e0, e1, true, new int[] { st.accent }, c);
		// rivets along the top
		m.prio = 11;
		for (int k = -1; k <= 1; k += 2) {
			rivet(m, add(add(c, scale(axis, 7.8)), pt(0, 0, k * hd * 0.55)), axis, 1.7, st.trim, label);
		}
	}

	/** The plate arms (the platebody's second model): chunky rerebrace and vambrace, the forge-light at the elbow. */
	static ModelData arms(Rig r, Style st) {
		Mesh m = new Mesh();
		m.prio = 9;
		int n = 6;
		for (int side = -1; side <= 1; side += 2) {
			int up = side > 0 ? 26 : 20, fo = side > 0 ? 22 : 19;
			double[] J = mirror(pt(r.shoulder[0], r.armTop + 4, r.shoulder[2]), side), E = mirror(r.elbow, side),
					W = mirror(pt(r.wrist[0], r.wristY, r.wrist[2]), side);
			double armR = Math.max(5, (extent(r.arms, 21)[3] - extent(r.arms, 21)[0]) / 2);
			double foreR = Math.max(4, (extent(r.arms, 19)[3] - extent(r.arms, 19)[0]) / 2);
			double[] Au = norm(sub(E, J)), Af = norm(sub(W, E));
			Object[][] rr = { { J, Au, armR + 1.6, up }, { add(E, scale(Au, -3)), Au, armR + 2.0, up }, { add(E, scale(Au, -1)), Au, armR + 1.4, up },
					{ add(E, scale(Af, 1.5)), Af, foreR + 1.6, fo }, { add(E, scale(Af, 3)), Af, foreR + 2.2, fo }, { W, Af, foreR + 2.2, fo } };
			int[][] rings = new int[rr.length][];
			double[][] cs = new double[rr.length][];
			for (int k = 0; k < rr.length; k++) {
				cs[k] = (double[]) rr[k][0];
				double rad = (double) rr[k][2];
				rings[k] = ring(m, cs[k], (double[]) rr[k][1], rad, rad, n, 0.3, (int) rr[k][3]);
			}
			loft(m, rings, cs, new int[] { st.main, st.trim, st.accent, st.trim, st.main });
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                                THE LEGS                                */
	/* ====================================================================== */

	/** Heavy plate legs: thick cuisses and greaves with bronze lips, knee cops, the forge-light in a seam over each knee. */
	static ModelData legs(Rig r, Style st) {
		Mesh m = new Mesh();
		int n = 7;
		for (int side = -1; side <= 1; side += 2) {
			int thigh = side > 0 ? 43 : 44, shin = side > 0 ? 36 : 33, ank = side > 0 ? 38 : 32;
			double[][] rows = { { r.waistY + 3, 2.4, 41 }, { r.hip[1] + 1, 3.0, thigh }, { r.thigh[1] - 4, 3.0, thigh }, { r.thigh[1] + 1, 2.4, thigh },
					{ r.knee[1] + 2, 2.4, shin }, { (r.knee[1] + r.ankle[1]) / 2, 3.0, shin }, { r.ankle[1] + 1, 2.2, ank } };
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
			loft(m, idx, cs, new int[] { st.dark, st.main, st.accent, st.dark, st.main, st.dark });
			BaseCoat.plateArc(m, r, side, r.hip[1] + 3, r.thigh[1] - 5, 3.0, thigh, st.light, st.trim, 2);
			BaseCoat.plateArc(m, r, side, r.knee[1] + 5, r.ankle[1] - 1, 2.8, shin, st.light, st.trim, 1);
			BaseCoat.kneeCop(m, r, side, shin, st);
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                                THE BOOTS                               */
	/* ====================================================================== */

	/** Huge anvil-heavy boots: chamfered iron blocks to mid-shin on flat, thick, bronze-shod soles. */
	static ModelData boots(Rig r, Style st) {
		Mesh m = new Mesh();
		int[] fb = r.feetB;
		for (int side = -1; side <= 1; side += 2) {
			int ank = side > 0 ? 38 : 32, shin = side > 0 ? 36 : 33;
			double[] fe = extent(r.feet, side > 0 ? 47 : 48);
			double x = (fe[0] + fe[3]) / 2, hw = (fe[3] - fe[0]) / 2;
			double sole = fb[4] + 0.5, back = fb[5] + 2.5, front = fb[2] - 3.5;
			double fcz = (front + back) / 2, fd = (back - front) / 2;
			double topY = r.knee[1] + (r.ankle[1] - r.knee[1]) * 0.35;
			double[] q0 = BaseCoat.legRing(r, topY, side, 3.6), q1 = BaseCoat.legRing(r, r.ankle[1], side, 4.4);
			// the shaft: a bronze cuff, then the block down to the foot
			int[][] rg = { box(m, q0[0], topY - 2, q0[1], q0[2] + 1.4, q0[3] + 1.4, 2, shin), box(m, q0[0], topY + 2, q0[1], q0[2] + 1.2, q0[3] + 1.2, 2, shin),
					box(m, q1[0], r.ankle[1] + 1, q1[1] + 1, q1[2] + 0.5, q1[3] + 1, 2, ank), box(m, x, sole - 12, fcz + fd * 0.35, hw + 4.5, fd * 0.65 + 1, 3, ank) };
			double[][] cs = { pt(q0[0], topY - 2, q0[1]), pt(q0[0], topY + 2, q0[1]), pt(q1[0], r.ankle[1] + 1, q1[1] + 1), pt(x, sole - 12, fcz + fd * 0.35) };
			m.prio = 2;
			loft(m, rg, cs, new int[] { st.trim, st.main, st.main });
			// the foot: a block from the instep forward, the toe squared and chamfered
			int[] f0 = box(m, x, sole - 12, fcz, hw + 4.0, fd + 0.5, 3, ank), f1 = box(m, x, sole - 2.5, fcz, hw + 4.6, fd + 1.5, 3, ank);
			m.band(f0, f1, true, new int[] { st.main }, pt(x, sole - 7, fcz));
			int lid = m.v(pt(x, sole - 13, fcz), ank);
			cap(m, f0, lid, st.light, pt(x, sole, fcz));
			// the sole: thick and flat, shod in bronze
			int[] s0 = box(m, x, sole - 3, fcz, hw + 5.2, fd + 2.2, 3, ank), s1 = box(m, x, sole + 0.5, fcz, hw + 5.2, fd + 2.2, 3, ank);
			m.band(s0, s1, true, new int[] { st.trim }, pt(x, sole - 1, fcz));
			int foot = m.v(pt(x, sole + 0.5, fcz), ank);
			cap(m, s1, foot, st.dark, pt(x, sole - 6, fcz));
			// rivets on the toe
			m.prio = 3;
			for (int k = -1; k <= 1; k += 2) {
				rivet(m, pt(x + k * (hw + 1), sole - 7, front - 2.2), pt(0, -0.3, -1), 1.5, st.trim, ank);
			}
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                              THE GAUNTLETS                             */
	/* ====================================================================== */

	/** Oversized forge gauntlets: huge blocky hands with a bronze knuckle plate and rivets, big flared cuffs, a glowing seam. */
	static ModelData gauntlets(Rig r, Style st) {
		Mesh m = new Mesh();
		for (int side = -1; side <= 1; side += 2) {
			int hand = side > 0 ? 28 : 27, fore = side > 0 ? 22 : 19;
			double[] W = mirror(BaseCoat.centroid(r.hands, 19), side), H = mirror(r.hand, side), E = mirror(r.elbow, side);
			double[] he = extent(r.hands, 27);
			double hx = (he[3] - he[0]) / 2, hz = (he[5] - he[2]) / 2;
			double[] D = norm(sub(H, W)), Af = norm(sub(W, E));
			double len = 2 * Math.sqrt(dot(sub(H, W), sub(H, W)));
			// the hand: a big chamfered block, wider across the knuckles, the fingers' end squared
			double[][] hs = { { 0.05, hx + 3.2, hz + 3.0 }, { 0.5, hx + 5.2, hz + 4.8 }, { 1.05, hx + 3.6, hz + 3.4 } };
			int[][] rg = new int[hs.length][];
			double[][] cs = new double[hs.length][];
			for (int k = 0; k < hs.length; k++) {
				cs[k] = add(W, scale(D, len * hs[k][0]));
				rg[k] = boxAx(m, cs[k], D, hs[k][1], hs[k][2], 2.2, k == 0 ? fore : hand);
			}
			m.prio = 10;
			loft(m, rg, cs, new int[] { st.main, st.dark });
			int tip = m.v(add(W, scale(D, len * 1.12)), hand);
			cap(m, rg[hs.length - 1], tip, st.dark, cs[hs.length - 2]);
			// the knuckle plate on the back of the hand (the outside), bronze, riveted
			double[] kc = add(add(W, scale(D, len * 0.5)), pt(side * (hx + 5.2), 0, 0));
			double[] out = pt(side, 0, 0);
			Frame K = new Frame(kc, pt(0, 0, 1), D, out);
			m.prio = 11;
			flat(m, K.o, K.u, K.v, K.n, new double[][] { { -hz - 3, -5 }, { hz + 3, -5 }, { hz + 3, 5 }, { -hz - 3, 5 } }, 0.2, st.trim, hand, false);
			for (int k = -1; k <= 1; k += 2) {
				rivet(m, add(kc, add(scale(out, 0.5), pt(0, 0, k * (hz + 1)))), out, 1.4, st.trimDark, hand);
			}
			// the thumb: a block out from the front
			double[] t0 = add(cs[1], pt(-side * 0.5, 0, -hz - 3.5)), t1 = add(t0, pt(-side * 1.0, len * 0.35, -3.0));
			ModelData th = new ModelData();
			th.skin = hand;
			ArmourForge.blade(th, t0, t1, pt(side, 0, 0), 4.6, 3.2, st.main);
			th.priorities.replaceAll(x -> 10);
			th.renderTypes.replaceAll(x -> 0);
			ArmourForge.merge(m.d, th, null);
			// the cuff: flaring up the forearm, a glowing seam, a bronze lip
			double[][] cr = { { 2.0, 6.2 }, { -5, 8.0 }, { -12, 10.5 }, { -14, 10.8 } };
			int[][] ci = new int[cr.length][];
			double[][] cc = new double[cr.length][];
			for (int k = 0; k < cr.length; k++) {
				cc[k] = add(W, scale(Af, cr[k][0]));
				ci[k] = boxAx(m, cc[k], Af, cr[k][1], cr[k][1] * 0.95, 2.5, fore);
			}
			m.prio = 10;
			loft(m, ci, cc, new int[] { st.accent, st.main, st.trim });
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                               THE SHIELD                               */
	/* ====================================================================== */

	/**
	 * An anvil seen from the side, in the rune kite's place and plane: a flat face across the top, the horn tapering out
	 * to one side, the waist, the spreading foot; blackened steel with a bright face and bronze edges, a bar of white-hot
	 * steel lying on the face. All on the left hand 28.
	 */
	static ModelData shield(ArmourForge af, Style st) throws IOException {
		ModelData kite = af.read(ArmourForge.RUNE_KITE_WORN);
		Frame k = ArmourForge.shieldFrame(kite, 61);
		double[] down = norm(sub(BaseCoat.Y, scale(k.n, dot(BaseCoat.Y, k.n))));
		double[] across = norm(ModelData.cross(k.n, down));
		double[] c = add(k.o, scale(k.n, 1.0));
		Mesh m = new Mesh();
		// the outline (across, down), round from the horn's tip; star-shaped about (0, -2)
		double[][] ol = { { -30, -12 }, { -16, -16 }, { 16, -16 }, { 18, -10 }, { 10, -6 }, { 7, 2 }, { 9, 12 }, { 17, 18 }, { 17, 22 }, { -17, 22 }, { -17, 18 }, { -9, 12 },
				{ -7, 2 }, { -10, -6 }, { -17, -8 } };
		int N = ol.length;
		double th = 3.0;
		double[] ctr = add(c, scale(down, -2));
		int[] f = new int[N], b = new int[N];
		for (int i = 0; i < N; i++) {
			double[] p = add(c, add(scale(across, ol[i][0]), scale(down, ol[i][1])));
			f[i] = m.v(add(p, scale(k.n, th)), 28);
			b[i] = m.v(add(p, scale(k.n, -th)), 28);
		}
		int fc = m.v(add(ctr, scale(k.n, th + 0.8)), 28), bc = m.v(add(ctr, scale(k.n, -th)), 28);
		m.prio = 10;
		for (int i = 0; i < N; i++) {
			int j = (i + 1) % N;
			m.tri(f[i], f[j], fc, st.light, k.n);
			m.tri(b[j], b[i], bc, st.main, scale(k.n, -1));
			double[] mid = add(c, add(scale(across, (ol[i][0] + ol[j][0]) / 2), scale(down, (ol[i][1] + ol[j][1]) / 2)));
			int col = ol[i][1] <= -15.9 && ol[j][1] <= -15.9 ? shade(st.light, 18) : st.trim;
			m.quad(f[i], b[i], b[j], f[j], col, sub(mid, add(c, scale(down, -2))));
		}
		// the white-hot bar lying across the face, and the glow under it
		ModelData bar = new ModelData();
		bar.shading = 0;
		bar.skin = 28;
		double[] lift = scale(k.n, th + 2.6);
		ArmourForge.blade(bar, add(add(c, lift), add(scale(across, -12), scale(down, -9))), add(add(c, lift), add(scale(across, 13), scale(down, -10))), k.n, 3.4, 2.2, st.core);
		bar.priorities.replaceAll(x -> 12);
		ArmourForge.merge(m.d, bar, null);
		m.prio = 11;
		flat(m, add(c, scale(k.n, th + 0.9)), across, down, k.n, new double[][] { { -13, -12.5 }, { 14, -13.5 }, { 14, -6.5 }, { -13, -5.5 } }, 0, st.accent, 28, false);
		// a bronze band round the waist
		flat(m, add(c, scale(k.n, th + 0.9)), across, down, k.n, new double[][] { { -7.6, 3 }, { 7.6, 3 }, { 8.2, 7 }, { -8.2, 7 } }, 0, st.trim, 28, false);
		return m.d;
	}
}
