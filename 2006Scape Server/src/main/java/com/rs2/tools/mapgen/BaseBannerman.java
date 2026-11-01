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
import static com.rs2.tools.mapgen.BaseParka.anchor;
import static com.rs2.tools.mapgen.BaseParka.apart;
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
 * The Herald's base: the Hollow Herald, who walks ahead of the Hollow King to announce him, in dark iron, ghost-pale
 * trim and the King's violet, two tall banners standing over his shoulders like a samurai's sashimono. Built from
 * scratch on the rune plate's rig for the Herald's (A35) draft set; see NewBases.
 *
 * <ul>
 * <li>Helm (named "... full helm", so head and jaw are hidden): a tall closed great helm, flat-topped, a pale cross of
 * eye-slit and breath-ridge on its face with ghost-light behind the slit, an iron crown of spikes round its top, and
 * rising from the crown a crest in the shape of a herald's horn, curving back to a flared bell.</li>
 * <li>Platebody (so the bare arms are hidden; the plate arms are the second model): a breastplate with a standing
 * gorget, under a long split tabard in violet, front and back, from the chest to the shins with a pale hem and the
 * Hollow King's crown (the herald's sigil) on the chest and the back; layered rounded pauldrons, three lames under a
 * dome, pale-edged; a belt with a herald's horn slung at the left hip; and on the back a frame carrying two tall poles,
 * each flying a long violet banner with the pale sigil, well above the head.</li>
 * <li>Platelegs: dark iron cuisses, pointed knee cops and greaves over plate legs.</li>
 * <li>Sabatons: plated boots to mid-shin ending in long pointed toes.</li>
 * <li>Gauntlets: BaseCoat's gauntlets in the dark iron.</li>
 * <li>Shield: a heater shield, pale-rimmed, a short banner with the sigil hanging from a bar across its top edge.</li>
 * </ul>
 *
 * <p>Labels. The helm is rigid on the skull 1. The breastplate, gorget, pauldron domes' tops, sigils, back-frame, poles
 * and banners are rigid on the chest 8, so the banners stand dead still on the back, turning only as the trunk does;
 * the belt and horn are on the belly 4; the pauldrons' lames on the upper arms 20 / 26 (the dome rides the arm too);
 * the tabard above the belt on 8 / 4 and below it in two halves, left on the thigh 43 and right on 44 (a slit between
 * them front and back), so each half swings with its leg instead of tearing. Plate arms: upper arm 20 / 26, forearm
 * 19 / 22. Legs: the fauld 41, thighs 43 / 44, shins 36 / 33, ankles 38 / 32. Sabatons: the shaft top on the shins,
 * the rest on the ankles. Every piece anchors the pivots of the kit it hides on tiny hidden triangles (BaseParka.anchor),
 * and the legs keep each side's vertices off the middle (BaseParka.apart).
 */
final class BaseBannerman {

	private BaseBannerman() {
	}

	/** The black of the helm's eye slit. */
	static final int SLIT = ModelData.hsl(0, 0, 5);

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
			return new ModelData[] { anchor(apart(sabatons(r, st)), r.feet), null };
		default:
			return new ModelData[] { anchor(BaseCoat.gauntlets(r, st), r.hands), null };
		}
	}

	/** The inventory model for the slot, or null to let ArmourSets build it the usual way from the worn model(s). */
	static ModelData inventory(ArmourForge af, Style st, int slot, ModelData[] worn) throws IOException {
		return null;
	}

	/* ====================================================================== */
	/*                                 HELPERS                                */
	/* ====================================================================== */

	/**
	 * A flat polygon (star-shaped about its centroid) in the plane through o spanned by u (x) and v (y), lifted
	 * {@code lift} along n, fanned from its centroid; faces n, and also -n if {@code both}.
	 */
	static void flat(Mesh m, double[] o, double[] u, double[] v, double[] n, double[][] pts, double lift, int col, int label, boolean both) {
		double[] base = add(o, scale(n, lift));
		if (pts.length <= 4) { // a triangle or a convex quad: no centre needed
			int[] idx = new int[pts.length];
			for (int i = 0; i < pts.length; i++) {
				idx[i] = m.v(add(base, add(scale(u, pts[i][0]), scale(v, pts[i][1]))), label);
			}
			for (int i = 1; i + 1 < pts.length; i++) {
				m.tri(idx[0], idx[i], idx[i + 1], col, n);
				if (both) {
					m.tri(idx[0], idx[i + 1], idx[i], col, scale(n, -1));
				}
			}
			return;
		}
		double cx = 0, cy = 0;
		for (double[] p : pts) {
			cx += p[0] / pts.length;
			cy += p[1] / pts.length;
		}
		int c = m.v(add(base, add(scale(u, cx), scale(v, cy))), label);
		int[] idx = new int[pts.length];
		for (int i = 0; i < pts.length; i++) {
			idx[i] = m.v(add(base, add(scale(u, pts[i][0]), scale(v, pts[i][1]))), label);
		}
		for (int i = 0; i < pts.length; i++) {
			int j = (i + 1) % pts.length;
			m.tri(c, idx[i], idx[j], col, n);
			if (both) {
				m.tri(c, idx[j], idx[i], col, scale(n, -1));
			}
		}
	}

	/**
	 * The herald's sigil, the Hollow King's crown: a band under five points (the middle one tallest), centred at (x, y)
	 * of the frame, {@code s} across; pale, standing {@code lift} off the plane. Both sides if {@code both}.
	 */
	static void sigil(Mesh m, double[] o, double[] u, double[] v, double[] n, double x, double y, double s, double lift, int col, int label, boolean both) {
		double bw = s / 2, bh = s * 0.2;
		flat(m, o, u, v, n, new double[][] { { x - bw, y }, { x + bw, y }, { x + bw, y + bh }, { x - bw, y + bh } }, lift, col, label, both);
		for (int k = 0; k < 5; k += 2) {
			double px = x - bw + bw * k / 2.0, tall = k == 2 ? s * 0.62 : s * 0.42, half = s * 0.13;
			flat(m, o, u, v, n, new double[][] { { px - half, y + 0.2 }, { px, y - tall }, { px + half, y + 0.2 } }, lift, col, label, both);
		}
	}

	/* ====================================================================== */
	/*                                THE HELM                                */
	/* ====================================================================== */

	/** The great helm: a tall closed bucket with a pale slit-cross, a crown of spikes round its top, a horn for a crest. */
	static ModelData helm(Rig r, Style st) {
		Mesh m = new Mesh();
		int[] hb = r.headB;
		double hx = Math.max(-hb[0], hb[3]), top = hb[1] - 3, bot = hb[4] + 3, front = hb[2] - 2.5, back = hb[5] + 3.0;
		double cz = (front + back) / 2, rz = (back - front) / 2;
		int n = 10;
		// y, rx, rz scale
		double[][] rows = { { bot, hx + 4.0, 1.06 }, { bot - (bot - top) * 0.45, hx + 3.2, 1.0 }, { top + 3, hx + 2.6, 0.96 }, { top, hx + 1.6, 0.86 } };
		int[][] ring = new int[rows.length][];
		double[][] cs = new double[rows.length][];
		for (int k = 0; k < rows.length; k++) {
			cs[k] = pt(0, rows[k][0], cz);
			ring[k] = hring(m, 0, rows[k][0], cz, rows[k][1], rz * rows[k][2], n, 0, 1);
		}
		m.prio = 5;
		loft(m, ring, cs, new int[] { st.main, st.main, st.light });
		int apex = m.v(pt(0, top - 1.5, cz), 1);
		cap(m, ring[rows.length - 1], apex, st.dark, pt(0, top + 6, cz));
		// a pale band round the bottom edge
		int[] b0 = hring(m, 0, bot + 0.5, cz, hx + 4.6, rz * 1.06 + 0.6, n, 0, 1), b1 = hring(m, 0, bot - 3, cz, hx + 4.3, rz * 1.04 + 0.6, n, 0, 1);
		m.prio = 6;
		m.band(b0, b1, true, new int[] { st.trim }, pt(0, bot, cz));
		// the face: an eye slit across, a breath ridge down, as a cross standing proud of the front
		double ey = hb[1] + (hb[4] - hb[1]) * 0.4;
		double fz = cz - rz * 1.0 - 0.6;
		Frame F = new Frame(pt(0, 0, fz), pt(1, 0, 0), pt(0, 1, 0), pt(0, 0, -1));
		m.prio = 7;
		flat(m, F.o, F.u, F.v, F.n, new double[][] { { -hx - 1.5, ey - 2.2 }, { hx + 1.5, ey - 2.2 }, { hx + 1.5, ey + 2.2 }, { -hx - 1.5, ey + 2.2 } }, 0.6, st.trim, 1, false);
		m.prio = 8;
		flat(m, F.o, F.u, F.v, F.n, new double[][] { { -hx, ey - 1.0 }, { hx, ey - 1.0 }, { hx, ey + 1.0 }, { -hx, ey + 1.0 } }, 1.3, SLIT, 1, false);
		for (int e = -1; e <= 1; e += 2) { // the ghost-light of the eyes, behind the slit
			double ex = e * hx * 0.42;
			m.prio = 9;
			flat(m, F.o, F.u, F.v, F.n, new double[][] { { ex - 2.4, ey }, { ex, ey - 1.6 }, { ex + 2.4, ey }, { ex, ey + 1.6 } }, 1.9, st.core, 1, false);
		}
		m.prio = 7;
		flat(m, F.o, F.u, F.v, F.n, new double[][] { { -1.6, ey + 2 }, { 1.6, ey + 2 }, { 1.2, bot - 1 }, { -1.2, bot - 1 } }, 0.8, st.trim, 1, false);
		// the crown of spikes round the top
		double cy = top + 3;
		int sp = 8;
		m.prio = 6;
		for (int k = 0; k < sp; k++) {
			double a = Math.PI * 2 * (k + 0.5) / sp;
			double tall = k == 0 || k == sp - 1 ? 15 : k % 2 == 0 ? 12 : 9;
			double rx = hx + 3.4, rzz = rz * 0.98 + 0.8;
			double[] l = ell(0, cy + 1, cz, rx, rzz, a - 0.3), rr = ell(0, cy + 1, cz, rx, rzz, a + 0.3), in = ell(0, cy, cz, rx - 2.5, rzz - 2.5, a);
			double[] tip = ell(0, cy - tall, cz, rx + 1.5, rzz + 1.5, a);
			int il = m.v(l, 1), ir = m.v(rr, 1), ii = m.v(in, 1), it = m.v(tip, 1);
			double[] ctr = pt(0, cy - 4, cz);
			m.triAway(il, ir, it, st.light, ctr);
			m.triAway(ir, ii, it, st.trim, ctr);
			m.triAway(ii, il, it, st.trim, ctr);
		}
		int[] c0 = hring(m, 0, cy + 2, cz, hx + 3.6, rz * 0.98 + 1.0, n, 0, 1), c1 = hring(m, 0, cy - 1.5, cz, hx + 3.4, rz * 0.98 + 0.8, n, 0, 1);
		m.band(c0, c1, true, new int[] { st.dark }, pt(0, cy, cz));
		// the crest: a herald's horn rising from the crown, curving back to a flared bell
		double[][] path = { { 0, top - 1, cz + 1 }, { 0, top - 13, cz - 1 }, { 0, top - 24, cz + 4 }, { 0, top - 30, cz + 14 } };
		double[] rad = { 4.6, 3.0, 2.8, 3.8 };
		int hn = 6;
		int[][] hr = new int[path.length + 1][];
		double[][] hc = new double[path.length + 1][];
		for (int k = 0; k < path.length; k++) {
			double[] ax = norm(k + 1 < path.length ? sub(path[k + 1], path[k]) : sub(path[k], path[k - 1]));
			hc[k] = path[k];
			hr[k] = ring(m, path[k], ax, rad[k], rad[k], hn, 0, 1);
		}
		double[] bellAx = norm(sub(path[3], path[2]));
		hc[path.length] = add(path[3], scale(bellAx, 4));
		hr[path.length] = ring(m, hc[path.length], bellAx, 10.5, 10.5, hn, 0, 1);
		m.prio = 7;
		loft(m, hr, hc, new int[] { st.trim, shade(st.trim, -14), st.trim, st.trim });
		// the bell's mouth: violet inside
		int mouth = m.v(add(path[3], scale(bellAx, 1.5)), 1);
		for (int j = 0; j < hn; j++) {
			m.tri(hr[path.length][j], hr[path.length][(j + 1) % hn], mouth, st.accent, bellAx);
			m.tri(hr[path.length][j], mouth, hr[path.length][(j + 1) % hn], st.trimDark, scale(bellAx, -1));
		}
		// a pale band where the horn meets the crown
		int[] hb0 = ring(m, pt(0, top - 4, cz + 0.6), pt(0, -1, 0), 4.2, 4.2, hn, 0, 1), hb1 = ring(m, pt(0, top - 7, cz + 0.4), pt(0, -1, 0), 3.9, 3.9, hn, 0, 1);
		m.band(hb0, hb1, true, new int[] { st.trim }, pt(0, top - 5.5, cz + 0.5));
		return m.d;
	}

	/* ====================================================================== */
	/*                                THE BODY                                */
	/* ====================================================================== */

	/**
	 * The breastplate and gorget, the split tabard front and back with the sigil, the layered pauldrons, the belt and
	 * horn, and the back-frame with its two banners.
	 */
	static ModelData body(Rig r, Style st) {
		Mesh m = new Mesh();
		int n = 10;
		double L = r.waistY - r.neckY;
		// ------------------------------------------------ the breastplate
		double[][] rows = { { r.neckY + 1, 1.5, 1.8, 1.8, 8 }, { r.neckY + 6, 3.0, 3.0, 2.6, 8 }, { r.neckY + L * 0.35, 3.0, 4.0, 2.6, 8 },
				{ r.neckY + L * 0.7, 2.6, 3.0, 2.4, 8 }, { r.waistY + 1, 2.8, 2.8, 2.6, 4 } };
		int[][] ring = new int[rows.length][];
		double[][] cs = new double[rows.length][], dim = new double[rows.length][];
		for (int i = 0; i < rows.length; i++) {
			dim[i] = trunk(r, rows[i][0], rows[i][1], rows[i][2], rows[i][3]);
			if (i == 0) {
				dim[i][1] = Math.min(dim[i][1], 11.5);
			}
			cs[i] = pt(0, rows[i][0], dim[i][0]);
			ring[i] = hring(m, 0, rows[i][0], dim[i][0], dim[i][1], dim[i][2], n, 0, (int) rows[i][4]);
		}
		m.prio = 3;
		loft(m, ring, cs, new int[] { st.dark, st.light, st.main, st.main });
		// the gorget: a standing collar, pale at its lip
		double[] s = slice(r.torso, r.neckY + 0.5, 0);
		double ncz = (s[2] + s[3]) / 2, nrx = Math.min(10.5, Math.max(-s[0], s[1])) + 2.4, nrz = (s[3] - s[2]) / 2 + 2.6;
		int[] g0 = hring(m, 0, r.neckY + 4, ncz, nrx + 1.5, nrz + 1.5, n, 0, 8), g1 = hring(m, 0, r.neckY - 5, ncz + 0.5, nrx + 0.6, nrz + 0.6, n, 0, 8),
				g2 = hring(m, 0, r.neckY - 6.5, ncz + 0.5, nrx + 1.4, nrz + 1.4, n, 0, 8);
		m.prio = 4;
		m.band(g0, g1, true, new int[] { st.main }, pt(0, r.neckY, ncz));
		m.band(g1, g2, true, new int[] { st.trim }, pt(0, r.neckY + 2, ncz));
		// ------------------------------------------------ the tabard
		tabard(m, r, st, dim, rows, -1);
		tabard(m, r, st, dim, rows, 1);
		// ------------------------------------------------ the belt over the tabard, the horn at the left hip
		double[] w = dim[rows.length - 1];
		double by = r.waistY - 1;
		int[] t0 = hring(m, 0, by - 3, w[0], w[1] + 1.6, w[2] + 2.0, n, 0, 4), t1 = hring(m, 0, by + 2.5, w[0], w[1] + 1.8, w[2] + 2.2, n, 0, 4);
		m.prio = 6;
		m.band(t0, t1, true, new int[] { st.leatherDark }, pt(0, by, w[0]));
		Frame F = new Frame(pt(0, 0, w[0] - w[2] - 2.2), pt(1, 0, 0), pt(0, 1, 0), pt(0, 0, -1));
		m.prio = 7;
		flat(m, F.o, F.u, F.v, F.n, new double[][] { { -2.8, by - 3.5 }, { 2.8, by - 3.5 }, { 2.8, by + 3.0 }, { -2.8, by + 3.0 } }, 0.6, st.trim, 4, false);
		horn(m, r, st, w, by);
		// ------------------------------------------------ the pauldrons
		for (int side = -1; side <= 1; side += 2) {
			pauldron(m, r, st, side);
		}
		// ------------------------------------------------ the back-frame and the banners
		banners(m, r, st, dim);
		return m.d;
	}

	/**
	 * One face of the tabard (face -1 front, 1 back): from under the gorget to the shins, narrower than the trunk so the
	 * breastplate shows at the sides; above the belt on the trunk, below it in two halves (left on 43, right on 44)
	 * with a slit between. A pale hem and the sigil on the breast (and between the shoulders behind).
	 */
	static void tabard(Mesh m, Rig r, Style st, double[][] dim, double[][] rows, int face) {
		double top = r.neckY + (face < 0 ? 7 : 8), hem = r.knee[1] + (r.ankle[1] - r.knee[1]) * 0.55;
		double waist = r.waistY + 1;
		// the panel's rows: y, half-width, label (0: by side)
		double hwTop = dim[1][1] * 0.62, hwW = dim[4][1] * 0.72;
		double[][] pr = { { top, hwTop, 8 }, { rows[2][0], dim[2][1] * 0.66, 8 }, { waist, hwW, 4 }, { waist + 10, hwW + 1, 0 }, { r.knee[1] - 4, hwW + 2.5, 0 },
				{ hem - 3, hwW + 3, 0 }, { hem, hwW + 3, 0 } };
		// the depth of each row: on the trunk above the belt, then hanging clear of the legs
		double[] zs = new double[pr.length];
		double clear = face < 0 ? 1e9 : -1e9;
		for (int k = 0; k < pr.length; k++) {
			double y = pr[k][0];
			if (y <= waist) {
				int i = k == 0 ? 1 : k == 1 ? 2 : 4;
				zs[k] = dim[i][0] + face * (dim[i][2] + 0.9);
			} else {
				double[] sl = slice(r.legs, Math.min(y, r.knee[1] + 6), 0);
				double z = face < 0 ? sl[2] - 4.5 : sl[3] + 4.5;
				zs[k] = z;
			}
			if (k >= 2) {
				clear = face < 0 ? Math.min(clear, zs[k]) : Math.max(clear, zs[k]);
				zs[k] = clear;
			}
		}
		int cols = 2;
		double gap = 0.9;
		int base = st.accent, inside = st.trimDark;
		for (int side = -1; side <= 1; side += 2) {
			int[][] v = new int[pr.length][cols + 1];
			for (int k = 0; k < pr.length; k++) {
				int lab = pr[k][2] > 0 ? (int) pr[k][2] : side > 0 ? 43 : 44;
				for (int j = 0; j <= cols; j++) {
					double u = j / (double) cols;
					double x = side * (k < 2 ? u * pr[k][1] : gap + u * (pr[k][1] - gap));
					// curve round the trunk a little at the edges
					double z = zs[k] - face * u * u * (k < 3 ? 2.5 : 1.5);
					v[k][j] = m.v(pt(x, pr[k][0], z), lab);
				}
			}
			double[] out = pt(0, 0, face);
			m.prio = 5;
			for (int k = 0; k + 1 < pr.length; k++) {
				int col = k == pr.length - 2 ? st.trim : base;
				for (int j = 0; j < cols; j++) {
					m.quad(v[k][j], v[k][j + 1], v[k + 1][j + 1], v[k + 1][j], col, out);
					if (k >= 2) { // the inside, seen in the stride
						m.quad(v[k][j], v[k + 1][j], v[k + 1][j + 1], v[k][j + 1], inside, scale(out, -1));
					}
				}
			}
			// a pale edge down the outer side, below the belt
			int[] e = new int[pr.length - 2];
			for (int k = 2; k < pr.length; k++) {
				e[k - 2] = m.v(pt(side * (pr[k][1] + 1.0), pr[k][0], zs[k] - face * 1.5 + face * 0.6), pr[k][2] > 0 ? (int) pr[k][2] : side > 0 ? 43 : 44);
			}
			for (int k = 2; k + 1 < pr.length; k++) {
				m.quad(v[k][cols], e[k - 2], e[k - 1], v[k + 1][cols], st.trim, out);
			}
		}
		// the sigil, on the breast or between the shoulders
		double sy = face < 0 ? rows[2][0] + 3 : rows[2][0] - 1;
		double sz = face < 0 ? Math.min(zs[0], zs[1]) : Math.max(zs[0], zs[1]);
		m.prio = 6;
		sigil(m, pt(0, 0, sz), pt(1, 0, 0), pt(0, 1, 0), pt(0, 0, face), 0, sy, hwTop * 1.15, 1.2, st.trim, 8, false);
		Frame f = new Frame(pt(0, 0, sz), pt(1, 0, 0), pt(0, 1, 0), pt(0, 0, face));
		flat(m, f.o, f.u, f.v, f.n, new double[][] { { -1.4, sy + 1.4 }, { 0, sy - 0.2 }, { 1.4, sy + 1.4 }, { 0, sy + 3.0 } }, 1.9, st.core, 8, false);
	}

	/** A herald's horn slung at the left hip: a curved pale horn, iron-banded, its bell forward. */
	static void horn(Mesh m, Rig r, Style st, double[] w, double by) {
		double x = w[1] + 3.0;
		double[][] path = { { x - 1, by + 1, w[0] + 6 }, { x + 1.5, by + 7, w[0] + 1 }, { x + 2, by + 9, w[0] - 6 }, { x + 1, by + 6, w[0] - 12 } };
		double[] rad = { 1.0, 1.8, 2.4, 3.0 };
		int n = 5;
		int[][] rs = new int[path.length + 1][];
		double[][] cs = new double[path.length + 1][];
		for (int k = 0; k < path.length; k++) {
			double[] ax = norm(k + 1 < path.length ? sub(path[k + 1], path[k]) : sub(path[k], path[k - 1]));
			cs[k] = path[k];
			rs[k] = ring(m, path[k], ax, rad[k], rad[k], n, 0, 4);
		}
		double[] ax = norm(sub(path[3], path[2]));
		cs[path.length] = add(path[3], scale(ax, 2.5));
		rs[path.length] = ring(m, cs[path.length], ax, 5.0, 5.0, n, 0, 4);
		m.prio = 7;
		loft(m, rs, cs, new int[] { st.trim, st.trim, st.dark, st.trim });
		int mouth = m.v(add(path[3], scale(ax, 1)), 4);
		cap(m, rs[path.length], mouth, st.trimDark, add(path[3], scale(ax, -3)));
	}

	/**
	 * A layered pauldron: a rounded dome on the shoulder and three lames below it round the upper arm, each pale-edged
	 * and tucked under the one above. All on the upper arm (20 / 26), so it rides the arm.
	 */
	static void pauldron(Mesh m, Rig r, Style st, int side) {
		int label = side > 0 ? 26 : 20;
		double[] J = mirror(pt(r.shoulder[0], r.armTop + 5, r.shoulder[2]), side), E = mirror(r.elbow, side);
		double[] A = norm(sub(E, J));
		double armR = Math.max(5, (extent(r.arms, 21)[3] - extent(r.arms, 21)[0]) / 2);
		double[] c = add(J, pt(side * 2.0, -3.0, 0));
		double[] axis = norm(pt(side * 0.5, -0.86, 0));
		double ra = armR + 6.0, h = armR + 2.5;
		int sides = 7;
		double[][] prof = { { -0.25, 1.04 }, { 0.1, 1.0 }, { 0.55, 0.82 }, { 0.88, 0.45 } };
		int[][] idx = new int[prof.length][sides];
		double[] e1 = pt(0, 0, 1), e2 = norm(ModelData.cross(axis, e1));
		for (int k = 0; k < prof.length; k++) {
			for (int s = 0; s < sides; s++) {
				double ang = Math.PI * 2 * s / sides;
				double[] p = add(add(c, scale(axis, prof[k][0] * h)), add(scale(e1, Math.cos(ang) * ra * prof[k][1]), scale(e2, Math.sin(ang) * ra * 0.92 * prof[k][1])));
				idx[k][s] = m.v(p, label);
			}
		}
		int apex = m.v(add(c, scale(axis, h)), label);
		m.prio = 10;
		int[] cols = { st.trim, st.main, st.light };
		for (int k = 0; k + 1 < prof.length; k++) {
			m.band(idx[k], idx[k + 1], true, new int[] { cols[k] }, add(c, scale(axis, -6)));
		}
		cap(m, idx[prof.length - 1], apex, st.light, c);
		// three lames down the outside of the arm, each pale-lipped
		for (int k = 0; k < 3; k++) {
			double t0 = 1.0 + k * 4.6, t1 = t0 + 6.4;
			double r0 = armR + 5.4 - k * 0.7, r1 = armR + 6.4 - k * 0.7;
			int segs = 3;
			double mid = side * Math.PI / 2, span = 2.0;
			int[][] row = new int[3][segs + 1];
			double[] ts = { t0, t1 - 1.8, t1 }, rs = { r0, r1 - 0.3, r1 };
			for (int q = 0; q < 3; q++) {
				double[] cc = add(J, scale(A, ts[q]));
				for (int j = 0; j <= segs; j++) {
					double phi = mid - span + 2 * span * j / segs;
					row[q][j] = m.v(BaseCoat.around(cc, A, rs[q], rs[q] * 0.95, phi), label);
				}
			}
			double[] in = add(J, scale(A, (t0 + t1) / 2));
			m.band(row[0], row[1], false, new int[] { k % 2 == 0 ? st.main : st.dark }, in);
			m.band(row[1], row[2], false, new int[] { st.trim }, in);
		}
	}

	/**
	 * The back-frame (a bar across the shoulder blades, a spine down to the belt) carrying two tall poles that rise well
	 * above the head, each with an arm at the top from which a long violet banner hangs, the sigil on it, its foot cut
	 * into a swallowtail. All rigid on the chest 8.
	 */
	static void banners(Mesh m, Rig r, Style st, double[][] dim) {
		double backZ = dim[2][0] + dim[2][2] + 2.5;
		double barY = r.neckY + 10, footY = r.waistY - 4;
		double headTop = r.headB[1];
		double poleTop = headTop - 56, flagTop = poleTop + 2, flagBot = headTop - 8;
		m.prio = 4;
		// the cross-bar and the spine of the frame
		ModelData bars = new ModelData();
		bars.shading = 0;
		bars.skin = 8;
		ArmourForge.blade(bars, pt(-14, barY, backZ), pt(14, barY, backZ), pt(0, 0, 1), 3.2, 2.0, st.dark);
		ArmourForge.blade(bars, pt(0, barY, backZ), pt(0, footY, backZ - 0.5), pt(0, 0, 1), 3.0, 2.0, st.dark);
		bars.priorities.replaceAll(x -> 4);
		ArmourForge.merge(m.d, bars, null);
		for (int side = -1; side <= 1; side += 2) {
			double px = side * 13;
			double[] foot = pt(side * 6.5, footY, backZ + 1.5), head = pt(px, poleTop, backZ + 3.5);
			double[] ax = norm(sub(head, foot));
			int pn = 5;
			int[] p0 = ring(m, foot, ax, 1.3, 1.3, pn, 0, 8), p1 = ring(m, head, ax, 1.1, 1.1, pn, 0, 8);
			m.prio = 4;
			m.band(p0, p1, true, new int[] { st.dark }, lerp(foot, head, 0.5));
			int finial = m.v(add(head, scale(ax, 4.5)), 8);
			cap(m, p1, finial, st.trim, lerp(foot, head, 0.95));
			// the arm at the top, out over the shoulder
			double[] armEnd = pt(side * 29, poleTop + 1.5, backZ + 3.5);
			ModelData arm = new ModelData();
			arm.shading = 0;
			arm.skin = 8;
			ArmourForge.blade(arm, pt(px, poleTop + 1.5, backZ + 3.5), armEnd, pt(0, 1, 0), 1.6, 1.6, st.dark);
			arm.priorities.replaceAll(x -> 4);
			ArmourForge.merge(m.d, arm, null);
			// the banner: hangs from the arm beside the pole, both faces violet, a swallowtail foot
			double x0 = px + side * 1.4, x1 = side * 28, z = backZ + 3.5;
			double len = flagBot - flagTop;
			double[][] edge = { { flagTop, 0 }, { flagTop + len * 0.5, 0.4 }, { flagBot, 0.6 } };
			int rowsN = edge.length;
			int[] li = new int[rowsN], ri = new int[rowsN], mi = new int[rowsN];
			for (int k = 0; k < rowsN; k++) {
				double sway = edge[k][1] * 2.0; // the flag stands a little off the plane, as if in a breeze
				li[k] = m.v(pt(x0, edge[k][0], z), 8);
				mi[k] = m.v(pt((x0 + x1) / 2, edge[k][0], z + sway), 8);
				ri[k] = m.v(pt(x1, edge[k][0], z), 8);
			}
			double[] back = pt(0, 0, 1);
			m.prio = 5;
			for (int k = 0; k + 1 < rowsN; k++) {
				int col = k == 0 ? st.accent : shade(st.accent, -8);
				m.quad(li[k], mi[k], mi[k + 1], li[k + 1], col, back);
				m.quad(mi[k], ri[k], ri[k + 1], mi[k + 1], col, back);
				m.quad(li[k], li[k + 1], mi[k + 1], mi[k], col, scale(back, -1));
				m.quad(mi[k], mi[k + 1], ri[k + 1], ri[k], col, scale(back, -1));
			}
			// the swallowtail: two pale-tipped points under the cloth, a notch between them
			int lb = li[rowsN - 1], mb = mi[rowsN - 1], rb = ri[rowsN - 1];
			double sw = edge[rowsN - 1][1] * 2.0;
			int lt = m.v(pt(x0, flagBot + 10, z), 8), rt = m.v(pt(x1, flagBot + 10, z), 8), notch = m.v(pt((x0 + x1) / 2, flagBot + 3, z + sw), 8);
			for (double[] o : new double[][] { back, scale(back, -1) }) {
				m.tri(lb, lt, notch, st.trim, o);
				m.tri(lb, notch, mb, shade(st.accent, -8), o);
				m.tri(mb, notch, rb, shade(st.accent, -8), o);
				m.tri(notch, rt, rb, st.trim, o);
			}
			// the sigil, front and back of the cloth
			double sx = (x0 + x1) / 2;
			double sy = flagTop + len * 0.42;
			double s = Math.abs(x1 - x0) * 0.72;
			m.prio = 6;
			sigil(m, pt(0, 0, z + 0.2), pt(1, 0, 0), pt(0, 1, 0), pt(0, 0, 1), sx, sy, s, 1.0, st.trim, 8, false);
			sigil(m, pt(0, 0, z + 0.2), pt(1, 0, 0), pt(0, 1, 0), pt(0, 0, -1), sx, sy, s, 1.0, st.trim, 8, false);
		}
	}

	/** The plate arms (the platebody's second model): rerebrace, couter and a vambrace with a flared pale cuff. */
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
			Object[][] rr = { { J, Au, armR + 1.0, up }, { lerp(J, E, 0.6), Au, armR + 1.2, up }, { add(E, scale(Au, -1)), Au, armR + 2.2, up },
					{ add(E, scale(Af, 2.0)), Af, foreR + 2.0, fo }, { add(W, scale(Af, -4)), Af, foreR + 1.6, fo }, { W, Af, foreR + 3.4, fo } };
			int[][] rings = new int[rr.length][];
			double[][] cs = new double[rr.length][];
			for (int k = 0; k < rr.length; k++) {
				cs[k] = (double[]) rr[k][0];
				double rad = (double) rr[k][2];
				rings[k] = ring(m, cs[k], (double[]) rr[k][1], rad, rad, n, 0.3, (int) rr[k][3]);
			}
			loft(m, rings, cs, new int[] { st.dark, st.main, st.light, st.main, st.trim });
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                                THE LEGS                                */
	/* ====================================================================== */

	/** Dark iron plate legs: a fauld at the waist, cuisses in two lames, pointed knee cops, greaves. */
	static ModelData legs(Rig r, Style st) {
		Mesh m = new Mesh();
		int n = 7;
		for (int side = -1; side <= 1; side += 2) {
			int thigh = side > 0 ? 43 : 44, shin = side > 0 ? 36 : 33, ank = side > 0 ? 38 : 32;
			double[][] rows = { { r.waistY + 3, 1.6, 41 }, { r.hip[1] + 1, 2.0, thigh }, { r.thigh[1] - 1, 1.8, thigh }, { r.knee[1] + 1, 1.6, shin },
					{ (r.knee[1] + r.ankle[1]) / 2, 2.0, shin }, { r.ankle[1] + 1, 1.4, ank } };
			int[][] idx = new int[rows.length][];
			double[][] cs = new double[rows.length][];
			for (int k = 0; k < rows.length; k++) {
				double[] q = BaseCoat.legRing(r, rows[k][0], side, rows[k][1]);
				if (k == 0) {
					q[0] = side * Math.max(Math.abs(q[0]), 8.5);
					q[2] = Math.max(q[2], 9.5);
				}
				cs[k] = pt(q[0], rows[k][0], q[1]);
				idx[k] = hring(m, q[0], rows[k][0], q[1], q[2], q[3], n, Math.PI / n, (int) rows[k][2]);
			}
			m.prio = 1;
			loft(m, idx, cs, new int[] { st.dark, st.dark, st.dark, st.dark, st.dark });
			BaseCoat.plateArc(m, r, side, r.hip[1] + 3, r.thigh[1] - 3, 1.6, thigh, st.main, st.trim, 2);
			BaseCoat.plateArc(m, r, side, r.knee[1] + 4, r.ankle[1] - 1, 1.2, shin, st.main, st.trim, 1);
			BaseCoat.kneeCop(m, r, side, shin, st);
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                                SABATONS                                */
	/* ====================================================================== */

	/** Plated boots to mid-shin, the foot in overlapping lames running out to a long pointed toe. */
	static ModelData sabatons(Rig r, Style st) {
		Mesh m = new Mesh();
		int[] fb = r.feetB;
		int n = 6;
		for (int side = -1; side <= 1; side += 2) {
			int ank = side > 0 ? 38 : 32, shin = side > 0 ? 36 : 33;
			double topY = r.knee[1] + (r.ankle[1] - r.knee[1]) * 0.4;
			double[] fe = extent(r.feet, side > 0 ? 47 : 48);
			double x = (fe[0] + fe[3]) / 2, hw = (fe[3] - fe[0]) / 2;
			double sole = fb[4] + 0.5, back = fb[5] + 1.0, front = fb[2];
			// the shaft
			double[] q0 = BaseCoat.legRing(r, topY, side, 2.8), q1 = BaseCoat.legRing(r, r.ankle[1], side, 3.2);
			int[] a0 = hring(m, q0[0], topY, q0[1], q0[2], q0[3], n, Math.PI / n, shin), a1 = hring(m, q1[0], r.ankle[1] + 1, q1[1], q1[2], q1[3], n, Math.PI / n, ank);
			// and on down over the heel and instep to the foot's lames
			double[] q2 = BaseCoat.legRing(r, r.ankle[1], side, 3.6);
			double fcz = (front + back) / 2 + 2;
			int[] a2 = hring(m, x, sole - 9, (q2[1] + fcz) / 2, hw + 1.6, q2[3] + 2.5, n, Math.PI / n, ank);
			m.prio = 2;
			m.band(a0, a1, true, new int[] { st.main }, pt(q0[0], (topY + r.ankle[1]) / 2, q0[1]));
			m.band(a1, a2, true, new int[] { st.dark }, pt(q1[0], r.ankle[1] + 5, q1[1]));
			int[] t0 = hring(m, q0[0], topY + 2, q0[1], q0[2] + 0.8, q0[3] + 0.8, n, Math.PI / n, shin), t1 = hring(m, q0[0], topY - 1.5, q0[1], q0[2] + 1.2, q0[3] + 1.2, n, Math.PI / n, shin);
			m.band(t0, t1, true, new int[] { st.trim }, pt(q0[0], topY, q0[1]));
			// the foot: sections along z from the heel to the point, {z, top above the sole, half-width}
			double[][] sec = { { back, 10.5, hw * 0.95 }, { back - 7, 12.0, hw + 1.2 }, { front + 9, 7.0, hw + 1.4 }, { front + 2, 4.6, hw * 0.9 }, { front - 5, 3.0, hw * 0.45 } };
			int[][] sx = new int[sec.length][];
			for (int k = 0; k < sec.length; k++) {
				double z = sec[k][0], tY = sole - sec[k][1], h = sec[k][2];
				sx[k] = new int[] { m.v(pt(x - h, sole, z), ank), m.v(pt(x + h, sole, z), ank), m.v(pt(x + h * 0.9, tY + 2, z), ank), m.v(pt(x, tY, z), ank),
						m.v(pt(x - h * 0.9, tY + 2, z), ank) };
			}
			int tip = m.v(pt(x, sole - 1.5, front - 12), ank);
			for (int k = 0; k + 1 < sec.length; k++) {
				double[] cin = pt(x, sole - 2, (sec[k][0] + sec[k + 1][0]) / 2);
				for (int e = 0; e < 5; e++) {
					int e2 = (e + 1) % 5;
					int col = e == 0 ? st.dark : k % 2 == 0 ? st.main : st.light;
					m.quadAway(sx[k][e], sx[k][e2], sx[k + 1][e2], sx[k + 1][e], col, cin);
				}
			}
			int last = sec.length - 1;
			for (int e = 0; e < 5; e++) {
				m.triAway(sx[last][e], sx[last][(e + 1) % 5], tip, e == 0 ? st.dark : st.trim, pt(x, sole - 2, sec[last][0] + 2));
			}
			for (int e = 1; e + 1 < 5; e++) {
				m.tri(sx[0][0], sx[0][e], sx[0][e + 1], st.dark, pt(0, 0, 1));
			}
			// the pale lips of the lames over the instep
			for (int k = 1; k <= 2; k++) {
				double z = sec[k][0] + (k == 1 ? -0.5 : 0.5), tY = sole - sec[k][1] - 0.6, h = sec[k][2] + 0.4;
				int l0 = m.v(pt(x - h, sole - 1.5, z), ank), l1 = m.v(pt(x, tY, z), ank), l2 = m.v(pt(x + h, sole - 1.5, z), ank);
				int u0 = m.v(pt(x - h, sole - 1.5, z - 1.8), ank), u1 = m.v(pt(x, tY - 0.3, z - 1.8), ank), u2 = m.v(pt(x + h, sole - 1.5, z - 1.8), ank);
				double[] in = pt(x, sole + 4, z);
				m.quadAway(l0, l1, u1, u0, st.trim, in);
				m.quadAway(l1, l2, u2, u1, st.trim, in);
			}
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                               THE SHIELD                               */
	/* ====================================================================== */

	/**
	 * A heater shield in the rune kite's place and plane: a flat top, straight sides curving to a point, pale-rimmed
	 * dark iron; a pale bar along the top edge with a short violet banner hanging from it over the upper face, the sigil
	 * on the banner, its foot cut in a swallowtail. All on the left hand 28.
	 */
	static ModelData shield(ArmourForge af, Style st) throws IOException {
		ModelData kite = af.read(ArmourForge.RUNE_KITE_WORN);
		Frame k = ArmourForge.shieldFrame(kite, 61);
		double[] down = norm(sub(BaseCoat.Y, scale(k.n, dot(BaseCoat.Y, k.n))));
		double[] across = norm(ModelData.cross(k.n, down));
		double[] c = add(k.o, scale(k.n, 1.0));
		Mesh m = new Mesh();
		double W = 20, T = -22;
		// the outline (across, down), clockwise from the top left
		int seg = 4;
		java.util.List<double[]> ol = new java.util.ArrayList<>();
		ol.add(new double[] { -W, T });
		ol.add(new double[] { W, T });
		ol.add(new double[] { W, 0 });
		for (int i = 1; i < seg; i++) { // the right side curving to the point
			double t = i / (double) seg, a = t * Math.PI / 2;
			ol.add(new double[] { W * Math.cos(a), 26 * Math.sin(a) });
		}
		ol.add(new double[] { 0, 28 });
		for (int i = seg - 1; i >= 1; i--) {
			double t = i / (double) seg, a = t * Math.PI / 2;
			ol.add(new double[] { -W * Math.cos(a), 26 * Math.sin(a) });
		}
		ol.add(new double[] { -W, 0 });
		int N = ol.size();
		int[] outer = new int[N], inner = new int[N], backR = new int[N];
		double[] ctr = add(c, scale(down, 2));
		for (int i = 0; i < N; i++) {
			double[] p = ol.get(i);
			double[] q = { p[0] * 0.86, 2 + (p[1] - 2) * 0.86 };
			outer[i] = m.v(add(c, add(scale(across, p[0]), add(scale(down, p[1]), scale(k.n, 0.8)))), 28);
			inner[i] = m.v(add(c, add(scale(across, q[0]), add(scale(down, q[1]), scale(k.n, 1.8)))), 28);
			backR[i] = m.v(add(c, add(scale(across, p[0]), add(scale(down, p[1]), scale(k.n, -2.2)))), 28);
		}
		int boss = m.v(add(ctr, scale(k.n, 3.6)), 28), rear = m.v(add(ctr, scale(k.n, -2.2)), 28);
		m.prio = 10;
		for (int i = 0; i < N; i++) {
			int j = (i + 1) % N;
			m.tri(inner[i], inner[j], boss, st.main, k.n);
			m.quad(outer[i], outer[j], inner[j], inner[i], st.trim, k.n);
			m.quad(outer[i], backR[i], backR[j], outer[j], st.trimDark, sub(scale(add(m.p(outer[i]), m.p(outer[j])), 0.5), ctr));
			m.tri(backR[i], rear, backR[j], st.dark, scale(k.n, -1));
		}
		// the bar along the top, and the banner hanging from it
		double[] lift = scale(k.n, 4.2);
		ModelData bar = new ModelData();
		bar.shading = 0;
		bar.skin = 28;
		ArmourForge.blade(bar, add(add(c, lift), add(scale(across, -W - 3), scale(down, T + 1))), add(add(c, lift), add(scale(across, W + 3), scale(down, T + 1))), k.n, 2.6,
				2.0, st.trim);
		bar.priorities.replaceAll(x -> 11);
		ArmourForge.merge(m.d, bar, null);
		double bw = W * 0.72, bt = T + 2, bb = 8;
		double[][] flag = { { -bw, bt }, { bw, bt }, { bw, bb + 6 }, { 0, bb }, { -bw, bb + 6 } };
		m.prio = 11;
		double[] o = add(c, scale(k.n, 3.4));
		flat(m, o, across, down, k.n, new double[][] { flag[0], flag[1], flag[2], flag[3] }, 0, st.accent, 28, false);
		flat(m, o, across, down, k.n, new double[][] { flag[0], flag[3], flag[4] }, 0, st.accent, 28, false);
		m.prio = 12;
		sigil(m, o, across, down, k.n, 0, -6, bw * 1.1, 0.8, st.trim, 28, false);
		flat(m, o, across, down, k.n, new double[][] { { -1.2, -4.0 }, { 0, -5.6 }, { 1.2, -4.0 }, { 0, -2.4 } }, 1.6, st.core, 28, false);
		return m.d;
	}
}
