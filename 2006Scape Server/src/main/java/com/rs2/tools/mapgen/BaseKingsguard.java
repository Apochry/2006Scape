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
import static com.rs2.tools.mapgen.BaseParka.trunk;

import java.io.IOException;

import com.rs2.tools.mapgen.ArmourForge.Frame;
import com.rs2.tools.mapgen.ArmourSets.Style;
import com.rs2.tools.mapgen.BaseCoat.Mesh;
import com.rs2.tools.mapgen.BaseCoat.Rig;

/**
 * The Aurelian kingsguard base (A40, Kind.COAT): King Aurel's household guard, who died holding the gate of his city.
 * A classic royal-guard knight in three materials: polished steel plate, a deep royal crimson, and gold at every edge.
 * Built from scratch on the rune plate's rig; see NewBases.
 *
 * <ul>
 * <li>Full helm (hides head and jaw): a rounded close helm with a dark eye slit and a gold breath-ridge down the face,
 * a gold band at its foot, a gold crown of five points round the top with a small sun on its brow, and a tall crimson
 * plume springing from inside the crown and sweeping back over the nape.</li>
 * <li>Platebody (hides the arms; the plate arms are its second model): a deep-chested breastplate with a standing gold-
 * lipped gorget, big rounded pauldrons (a dome and three lames, every lip gold), a dark belt with a gold buckle, and a
 * crimson tabard front and back from under the gorget to above the knee, gold-hemmed and gold-edged, with the king's
 * charge on the breast and between the shoulders: a crown over a sun with a star in it, cradled by a crescent moon.
 * The arms: rerebrace, a couter with a round gold-rimmed wing, and a vambrace.</li>
 * <li>Platelegs: dark plate legs, cuisses and greaves gold-edged, rounded gold-rimmed knee cops, and tassets of two
 * gold-lipped lames hanging over the outside of each thigh from the waist.</li>
 * <li>Boots: chunky sabatons: a plated shaft with a gold top band, a broad foot of lames with gold lips, a round blunt
 * toe.</li>
 * <li>Gauntlets: the hand in steel (the hands kit's own mesh), a big flared cuff with a gold rim.</li>
 * <li>Shield: a large heater shield, crimson in a raised gold rim, the king's charge in gold on it, steel behind.</li>
 * </ul>
 *
 * <p>Labels. No new vertex takes a pivot label (BaseCoat.PIVOTS); each piece anchors the pivots of the kit it hides on
 * tiny hidden triangles (BaseParka.anchor), keeps each leg's vertices off the middle (BaseParka.apart), and moves apart
 * vertices of different labels at one position (BaseWraith.separate). The helm, crown and plume are rigid on the skull
 * 1. The breastplate's upper rows, the gorget and the tabard above the belt are on the chest 8, the lower rows, the belt
 * and the buckle on the belly 4; below the belt each half of the tabard (split front and back) is on its thigh, 43 left
 * and 44 right, so the halves swing with the legs. Pauldrons (dome and lames) and rerebraces ride the upper arms 20 /
 * 26, couters the elbows 18 / 24, vambraces the forearms 19 / 22. Legs: the waistband on 41, thighs and tassets 43 /
 * 44, knee cops and greaves on the shins 36 / 33, the ankles 38 / 32. Sabatons: shaft and heel on the ankles, the foot
 * from the ball forward on the toes 46 / 45. Gauntlets: the hands kit's own labels, the cuff on the forearms. The
 * shield is on the left hand 28, in the rune kite's place and plane.
 */
final class BaseKingsguard {

	private BaseKingsguard() {
	}

	/** The black of the eye slit and the shadow inside the crown. */
	static final int SLIT = ModelData.hsl(0, 0, 6);

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		Rig r = BaseCoat.rig(af, female);
		switch (slot) {
		case ArmourSets.HELM:
			return new ModelData[] { BaseWraith.separate(anchor(helm(r, st), r.head)), null };
		case ArmourSets.BODY:
			return new ModelData[] { BaseWraith.separate(anchor(apart(body(r, st)), r.torso)), BaseWraith.separate(anchor(arms(r, st), r.arms)) };
		case ArmourSets.LEGS:
			return new ModelData[] { BaseWraith.separate(anchor(apart(legs(r, st)), r.legs)), null };
		case ArmourSets.SHIELD:
			return new ModelData[] { shield(af, st), null };
		case ArmourSets.BOOTS:
			return new ModelData[] { BaseWraith.separate(anchor(apart(sabatons(r, st)), r.feet)), null };
		default:
			return new ModelData[] { BaseWraith.separate(gauntlets(r, st)), null };
		}
	}

	/** The inventory model for the slot, or null to let ArmourSets build it the usual way from the worn model(s). */
	static ModelData inventory(ArmourForge af, Style st, int slot, ModelData[] worn) throws IOException {
		return null;
	}

	/* ====================================================================== */
	/*                                 HELPERS                                */
	/* ====================================================================== */

	/** A flat polygon in the plane (o; u, v), lifted {@code lift} along n, facing n; flat-shaded. See BaseBannerman.flat. */
	static void flat(Mesh m, double[] o, double[] u, double[] v, double[] n, double[][] pts, double lift, int col, int label) {
		int sh = m.d.shading;
		m.d.shading = 1;
		BaseBannerman.flat(m, o, u, v, n, pts, lift, col, label, false);
		m.d.shading = sh;
	}

	/** A regular polygon's outline round (x, y), radius r, k sides, the first at angle a0. */
	static double[][] poly(double x, double y, double r, int k, double a0) {
		double[][] p = new double[k][];
		for (int i = 0; i < k; i++) {
			double a = a0 + Math.PI * 2 * i / k;
			p[i] = new double[] { x + Math.cos(a) * r, y + Math.sin(a) * r };
		}
		return p;
	}

	/**
	 * The king's charge, in the plane (o; u across, v down; facing n): a crown of three points over a sun disc with a
	 * four-pointed star in it, cradled by a crescent moon; {@code s} high from the crown's tallest point to the moon's
	 * foot, centred on x, its top at y. The crown, sun and moon in {@code gold}, the star in {@code core}.
	 */
	static void charge(Mesh m, double[] o, double[] u, double[] v, double[] n, double x, double y, double s, double lift, int gold, int core, int label) {
		int prio = m.prio;
		// the crown: a band and three points, the middle one tallest
		double bw = s * 0.36, bandTop = y + s * 0.27, bandBot = bandTop + s * 0.12;
		flat(m, o, u, v, n, new double[][] { { x - bw, bandTop }, { x + bw, bandTop }, { x + bw * 0.86, bandBot }, { x - bw * 0.86, bandBot } }, lift, gold, label);
		for (int k = -1; k <= 1; k++) {
			double px = x + k * bw * 0.82, tall = k == 0 ? s * 0.27 : s * 0.17, half = s * 0.1;
			flat(m, o, u, v, n, new double[][] { { px - half, bandTop + 0.3 }, { px, bandTop - tall }, { px + half, bandTop + 0.3 } }, lift, gold, label);
		}
		// the sun, a star in it
		double sy = bandBot + s * 0.27, R = s * 0.22;
		flat(m, o, u, v, n, poly(x, sy, R, 8, Math.PI / 8), lift, gold, label);
		m.prio = prio + 1;
		double[][] star = BaseWraith.star4(R * 0.85, R * 0.3);
		for (double[] p : star) {
			p[0] += x;
			p[1] += sy;
		}
		flat(m, o, u, v, n, star, lift + 0.6, core, label);
		m.prio = prio;
		// the moon: a crescent under the sun, its horns rising either side of it
		int k = 6;
		double Rm = R + s * 0.13, a0 = Math.toRadians(15), a1 = Math.toRadians(165);
		double[] base = add(o, scale(n, lift));
		int[] outer = new int[k + 1], inner = new int[k + 1];
		for (int i = 0; i <= k; i++) {
			double t = i / (double) k, a = a0 + (a1 - a0) * t, th = Math.sin(Math.PI * t) * s * 0.11;
			outer[i] = m.v(add(base, add(scale(u, x + Math.cos(a) * Rm), scale(v, sy + Math.sin(a) * Rm))), label);
			inner[i] = i == 0 || i == k ? outer[i] : m.v(add(base, add(scale(u, x + Math.cos(a) * (Rm - th)), scale(v, sy + Math.sin(a) * (Rm - th)))), label);
		}
		int sh = m.d.shading;
		m.d.shading = 1;
		for (int i = 0; i < k; i++) {
			if (i == 0) {
				m.tri(outer[0], outer[1], inner[1], gold, n);
			} else if (i == k - 1) {
				m.tri(outer[i], outer[k], inner[i], gold, n);
			} else {
				m.quad(outer[i], outer[i + 1], inner[i + 1], inner[i], gold, n);
			}
		}
		m.d.shading = sh;
		m.prio = prio;
	}

	/* ====================================================================== */
	/*                                THE HELM                                */
	/* ====================================================================== */

	/**
	 * The close helm: a rounded steel helm closed over the face, a dark eye slit, a gold breath-ridge and a gold foot,
	 * a gold crown of five points round the top (a small sun on its brow) and a crimson plume rising from inside the
	 * crown and sweeping back. All on the skull 1.
	 */
	static ModelData helm(Rig r, Style st) {
		Mesh m = new Mesh();
		int[] hb = r.headB;
		double hx = Math.max(-hb[0], hb[3]), top = hb[1], chin = hb[4], h = chin - top;
		double cz = (hb[2] + hb[5]) / 2.0 + 0.5, rz = (hb[5] - hb[2]) / 2.0 + 3.2;
		int n = 10;
		double eyeY = top + h * 0.42;
		// y, rx, rz
		double[][] rows = { { chin + 5, hx + 5.2, rz + 1.6 }, { chin - 3, hx + 3.8, rz + 0.4 }, { eyeY, hx + 4.0, rz + 0.6 }, { top + h * 0.16, hx + 3.4, rz * 0.94 },
				{ top - 1.5, (hx + 3.4) * 0.72, rz * 0.7 } };
		int[][] ring = new int[rows.length][];
		double[][] cs = new double[rows.length][];
		for (int k = 0; k < rows.length; k++) {
			cs[k] = pt(0, rows[k][0], cz);
			ring[k] = hring(m, 0, rows[k][0], cz, rows[k][1], rows[k][2], n, 0, 1);
		}
		m.prio = 5;
		loft(m, ring, cs, new int[] { st.main, st.main, st.light, st.light });
		int apex = m.v(pt(0, top - 5, cz + 0.5), 1);
		cap(m, ring[rows.length - 1], apex, st.light, pt(0, top + 6, cz));
		// a gold band round the foot
		int[] f0 = hring(m, 0, rows[0][0] + 0.5, cz, rows[0][1] + 0.7, rows[0][2] + 0.7, n, 0, 1),
				f1 = hring(m, 0, rows[0][0] - 3.2, cz, rows[0][1] + 0.1, rows[0][2] + 0.2, n, 0, 1);
		m.prio = 6;
		m.band(f0, f1, true, new int[] { st.trim }, pt(0, rows[0][0], cz));
		// the eye slit, on the eye ring, round the front
		m.prio = 7;
		int sn = 4;
		int[] sTop = new int[sn + 1], sBot = new int[sn + 1];
		for (int j = 0; j <= sn; j++) {
			double phi = -Math.PI * 0.4 + Math.PI * 0.8 * j / sn;
			sTop[j] = m.v(ell(0, eyeY - 1.4, cz, rows[2][1] + 0.9, rows[2][2] + 0.9, phi), 1);
			sBot[j] = m.v(ell(0, eyeY + 1.4, cz, rows[2][1] + 0.9, rows[2][2] + 0.9, phi), 1);
		}
		for (int j = 0; j < sn; j++) {
			m.quadAway(sTop[j], sTop[j + 1], sBot[j + 1], sBot[j], SLIT, pt(0, eyeY, cz));
		}
		// the breath-ridge: a gold strip down the face from the slit to the foot, and a gold brow over the slit
		double fz0 = cz - rows[2][2] - 1.2, fz1 = cz - rows[1][2] - 1.0, fz2 = cz - rows[0][2] - 0.6;
		int[] rl = { m.v(pt(-1.6, eyeY + 2.2, fz0), 1), m.v(pt(-1.6, rows[1][0], fz1), 1), m.v(pt(-1.8, rows[0][0] - 3, fz2), 1) },
				rr = { m.v(pt(1.6, eyeY + 2.2, fz0), 1), m.v(pt(1.6, rows[1][0], fz1), 1), m.v(pt(1.8, rows[0][0] - 3, fz2), 1) },
				rc = { m.v(pt(0, eyeY + 2.2, fz0 - 0.9), 1), m.v(pt(0, rows[1][0], fz1 - 1.1), 1), m.v(pt(0, rows[0][0] - 3, fz2 - 0.9), 1) };
		for (int k = 0; k < 2; k++) {
			m.quad(rl[k], rl[k + 1], rc[k + 1], rc[k], st.trim, pt(-1, 0, -2));
			m.quad(rc[k], rc[k + 1], rr[k + 1], rr[k], st.trim, pt(1, 0, -2));
		}
		int[] bt = new int[sn + 1], bb = new int[sn + 1];
		for (int j = 0; j <= sn; j++) {
			double phi = -Math.PI * 0.42 + Math.PI * 0.84 * j / sn;
			bt[j] = m.v(ell(0, eyeY - 4.6, cz, rows[2][1] + 0.4, rows[2][2] + 0.5, phi), 1);
			bb[j] = m.v(ell(0, eyeY - 2.0, cz, rows[2][1] + 1.3, rows[2][2] + 1.4, phi), 1);
		}
		for (int j = 0; j < sn; j++) {
			m.quadAway(bt[j], bt[j + 1], bb[j + 1], bb[j], st.trim, pt(0, eyeY - 2, cz + 4));
		}
		// the crown: a gold circlet round the top and five points rising from it, the front one tallest
		double c0 = rows[3][0] + 2.5, c1 = c0 - 4.5;
		double crx = rows[3][1] + 1.0, crz = rows[3][2] + 1.0;
		int cn = 10;
		int[] k0 = hring(m, 0, c0, cz, crx, crz, cn, 0, 1), k1 = hring(m, 0, c1, cz, crx + 0.4, crz + 0.4, cn, 0, 1),
				k2 = hring(m, 0, c1, cz, crx - 1.6, crz - 1.6, cn, 0, 1);
		m.prio = 8;
		m.band(k0, k1, true, new int[] { st.trim }, pt(0, (c0 + c1) / 2, cz));
		for (int j = 0; j < cn; j++) { // the circlet's top edge, and its inside in shadow
			int jj = (j + 1) % cn;
			m.quad(k1[j], k1[jj], k2[jj], k2[j], st.trimDark, pt(0, -1, 0));
		}
		for (int p = 0; p < 5; p++) {
			double a = Math.PI * 2 * p / 5;
			double tall = p == 0 ? 13 : 9, half = p == 0 ? 0.36 : 0.3;
			double[] l = ell(0, c1 + 0.6, cz, crx + 0.4, crz + 0.4, a - half), rt = ell(0, c1 + 0.6, cz, crx + 0.4, crz + 0.4, a + half),
					in = ell(0, c1 + 1.0, cz, crx - 2.2, crz - 2.2, a), tip = ell(0, c1 - tall, cz, crx + 1.4, crz + 1.4, a);
			int il = m.v(l, 1), ir = m.v(rt, 1), ii = m.v(in, 1), it = m.v(tip, 1);
			double[] ctr = pt(0, c1 - 3, cz);
			m.triAway(il, ir, it, st.trim, ctr);
			m.triAway(ir, ii, it, st.trimDark, ctr);
			m.triAway(ii, il, it, st.trimDark, ctr);
		}
		// the small sun on the crown's brow
		double[] sc = ell(0, (c0 + c1) / 2, cz, crx + 0.9, crz + 0.9, 0);
		Frame F = new Frame(sc, pt(1, 0, 0), pt(0, 1, 0), pt(0, 0, -1));
		m.prio = 9;
		flat(m, F.o, F.u, F.v, F.n, poly(0, 0, 3.0, 8, Math.PI / 8), 0.4, st.core, 1);
		// the plume: rising from inside the crown, sweeping back over the nape
		double[][] path = { { 0, top - 2, cz + 1 }, { 0, top - 12, cz + 1 }, { 0, top - 19, cz + 8 }, { 0, top - 18, cz + 18 }, { 0, top - 8, cz + 25 },
				{ 0, top + 6, cz + 26 } };
		double[] ru = { 3.4, 5.0, 5.6, 5.2, 4.2, 2.2 }, rw = { 4.4, 7.0, 8.0, 7.6, 6.0, 3.0 };
		int pn = 6;
		int[][] pr = new int[path.length][];
		for (int k = 0; k < path.length; k++) {
			double[] ax = norm(k == 0 ? sub(path[1], path[0]) : k + 1 < path.length ? sub(path[k + 1], path[k - 1]) : sub(path[k], path[k - 1]));
			pr[k] = ring(m, path[k], ax, ru[k], rw[k], pn, 0, 1);
		}
		m.prio = 4;
		loft(m, pr, path, new int[] { st.accent, st.accent, st.accent, st.leather, st.leather });
		cap(m, pr[path.length - 1], m.v(pt(0, top + 12, cz + 25), 1), st.leather, path[path.length - 1]);
		return m.d;
	}

	/* ====================================================================== */
	/*                                THE BODY                                */
	/* ====================================================================== */

	/** The breastplate, gorget, belt, the tabard front and back with the charge, and the pauldrons. */
	static ModelData body(Rig r, Style st) {
		Mesh m = new Mesh();
		int n = 10;
		double L = r.waistY - r.neckY;
		// ------------------------------------------------ the breastplate: deep in the chest
		double[][] rows = { { r.neckY + 1, 1.6, 2.0, 1.8, 8 }, { r.neckY + 6, 3.6, 3.8, 2.8, 8 }, { r.neckY + L * 0.36, 3.4, 5.0, 2.8, 8 },
				{ r.neckY + L * 0.72, 2.6, 3.4, 2.4, 4 }, { r.waistY + 1, 2.8, 2.8, 2.6, 4 } };
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
		loft(m, ring, cs, new int[] { st.light, st.main, st.main, st.dark });
		// the gorget: a standing collar with a gold lip
		double[] s = slice(r.torso, r.neckY + 0.5, 0);
		double ncz = (s[2] + s[3]) / 2, nrx = Math.min(10.5, Math.max(-s[0], s[1])) + 2.6, nrz = (s[3] - s[2]) / 2 + 2.8;
		int[] g0 = hring(m, 0, r.neckY + 4, ncz, nrx + 2.0, nrz + 2.0, n, 0, 8), g1 = hring(m, 0, r.neckY - 4, ncz + 0.5, nrx + 0.8, nrz + 0.8, n, 0, 8),
				g2 = hring(m, 0, r.neckY - 6, ncz + 0.5, nrx + 1.6, nrz + 1.6, n, 0, 8);
		m.prio = 4;
		m.band(g0, g1, true, new int[] { st.main }, pt(0, r.neckY, ncz));
		m.band(g1, g2, true, new int[] { st.trim }, pt(0, r.neckY + 2, ncz));
		// ------------------------------------------------ the tabard, front and back
		tabard(m, r, st, dim, rows, -1);
		tabard(m, r, st, dim, rows, 1);
		// ------------------------------------------------ the belt over it, a gold buckle
		double[] w = dim[rows.length - 1];
		double by = r.waistY - 1;
		int[] t0 = hring(m, 0, by - 3, w[0], w[1] + 1.8, w[2] + 2.6, n, 0, 4), t1 = hring(m, 0, by + 2.5, w[0], w[1] + 2.0, w[2] + 2.8, n, 0, 4);
		m.prio = 7;
		m.band(t0, t1, true, new int[] { st.dark }, pt(0, by, w[0]));
		Frame F = new Frame(pt(0, 0, w[0] - w[2] - 2.8), pt(1, 0, 0), pt(0, 1, 0), pt(0, 0, -1));
		m.prio = 8;
		flat(m, F.o, F.u, F.v, F.n, new double[][] { { -3.4, by - 4.0 }, { 3.4, by - 4.0 }, { 3.4, by + 3.5 }, { -3.4, by + 3.5 } }, 0.7, st.trim, 4);
		m.prio = 9;
		flat(m, F.o, F.u, F.v, F.n, new double[][] { { -1.6, by - 2.0 }, { 1.6, by - 2.0 }, { 1.6, by + 1.5 }, { -1.6, by + 1.5 } }, 1.3, st.trimDark, 4);
		// ------------------------------------------------ the pauldrons
		for (int side = -1; side <= 1; side += 2) {
			pauldron(m, r, st, side);
		}
		return m.d;
	}

	/**
	 * One face of the tabard (face -1 front, 1 back): from under the gorget to above the knee, narrower than the trunk
	 * so the breastplate shows at the sides, widening below the belt; above the belt on the trunk (8 / 4), below it in
	 * two halves (left on 43, right on 44) with a slit between, lined in the dark crimson. A gold hem, gold edges down
	 * the sides below the belt, and the charge on the breast (between the shoulders behind).
	 */
	static void tabard(Mesh m, Rig r, Style st, double[][] dim, double[][] rows, int face) {
		double top = r.neckY + (face < 0 ? 7 : 8), hem = r.knee[1] - 2;
		double waist = r.waistY + 1;
		double hwTop = dim[1][1] * 0.6, hwW = dim[4][1] * 0.7;
		// y, half-width, label (0: by side)
		double[][] pr = { { top, hwTop, 8 }, { rows[2][0], dim[2][1] * 0.62, 8 }, { waist, hwW, 4 }, { waist + 10, hwW + 1.5, 0 }, { hem - 3.5, hwW + 3.5, 0 },
				{ hem, hwW + 3.7, 0 } };
		double[] zs = new double[pr.length];
		double clear = face < 0 ? 1e9 : -1e9;
		for (int k = 0; k < pr.length; k++) {
			double y = pr[k][0];
			if (y <= waist) {
				int i = k == 0 ? 1 : k == 1 ? 2 : 4;
				zs[k] = dim[i][0] + face * (dim[i][2] + 1.0);
			} else {
				double[] sl = slice(r.legs, Math.min(y, r.knee[1] + 6), 0);
				zs[k] = face < 0 ? sl[2] - 5.0 : sl[3] + 5.0;
			}
			if (k >= 2) {
				clear = face < 0 ? Math.min(clear, zs[k]) : Math.max(clear, zs[k]);
				zs[k] = clear;
			}
		}
		int cols = 2;
		double gap = 0.9;
		int sh = m.d.shading;
		m.d.shading = 1; // a cloth panel, lined: flat, so its two sides don't darken each other
		for (int side = -1; side <= 1; side += 2) {
			int[][] v = new int[pr.length][cols + 1];
			for (int k = 0; k < pr.length; k++) {
				int lab = pr[k][2] > 0 ? (int) pr[k][2] : side > 0 ? 43 : 44;
				for (int j = 0; j <= cols; j++) {
					double u = j / (double) cols;
					double x = side * (k < 2 ? u * pr[k][1] : gap + u * (pr[k][1] - gap));
					double z = zs[k] - face * u * u * (k < 3 ? 2.5 : 1.5);
					v[k][j] = m.v(pt(x, pr[k][0], z), lab);
				}
			}
			double[] out = pt(0, 0, face);
			m.prio = 5;
			for (int k = 0; k + 1 < pr.length; k++) {
				int col = k == pr.length - 2 ? st.trim : st.accent;
				for (int j = 0; j < cols; j++) {
					m.quad(v[k][j], v[k][j + 1], v[k + 1][j + 1], v[k + 1][j], col, out);
					if (k >= 2) { // the lining, seen in the stride
						m.quad(v[k][j], v[k + 1][j], v[k + 1][j + 1], v[k][j + 1], st.leather, scale(out, -1));
					}
				}
			}
			// a gold edge down the outer side below the belt
			int[] e = new int[pr.length - 2];
			for (int k = 2; k < pr.length; k++) {
				e[k - 2] = m.v(pt(side * (pr[k][1] + 1.2), pr[k][0], zs[k] - face * 1.5 + face * 0.6), pr[k][2] > 0 ? (int) pr[k][2] : side > 0 ? 43 : 44);
			}
			for (int k = 2; k + 1 < pr.length; k++) {
				m.quad(v[k][cols], e[k - 2], e[k - 1], v[k + 1][cols], st.trim, out);
			}
		}
		m.d.shading = sh;
		// the charge, on the breast or between the shoulders
		double sy = face < 0 ? pr[0][0] + 1.5 : pr[0][0] + 0.5;
		double sz = face < 0 ? Math.min(zs[0], zs[1]) : Math.max(zs[0], zs[1]);
		m.prio = 6;
		charge(m, pt(0, 0, sz), pt(-face, 0, 0), pt(0, 1, 0), pt(0, 0, face), 0, sy, hwTop * 1.6, 1.0, st.trim, st.core, 8);
	}

	/**
	 * A big rounded pauldron: a dome on the shoulder, gold-lipped, and three lames below it round the outside of the
	 * upper arm, each gold-lipped and tucked under the one above. All on the upper arm (20 / 26), so it rides the arm.
	 */
	static void pauldron(Mesh m, Rig r, Style st, int side) {
		int label = side > 0 ? 26 : 20;
		double[] J = mirror(pt(r.shoulder[0], r.armTop + 5, r.shoulder[2]), side), E = mirror(r.elbow, side);
		double[] A = norm(sub(E, J));
		double armR = Math.max(5, (extent(r.arms, 21)[3] - extent(r.arms, 21)[0]) / 2);
		double[] c = add(J, pt(side * 2.2, -3.4, 0));
		double[] axis = norm(pt(side * 0.5, -0.86, 0));
		double ra = armR + 7.0, h = armR + 3.5;
		int sides = 8;
		// along the axis (x h), radius (x ra): the gold lip, the rim, the shoulder, the crown
		double[][] prof = { { -0.32, 1.02 }, { -0.12, 1.06 }, { 0.12, 1.0 }, { 0.55, 0.8 }, { 0.88, 0.42 } };
		int[][] idx = new int[prof.length][sides];
		double[] e1 = pt(0, 0, 1), e2 = norm(ModelData.cross(axis, e1));
		for (int k = 0; k < prof.length; k++) {
			for (int q = 0; q < sides; q++) {
				double ang = Math.PI * 2 * (q + 0.5) / sides;
				double[] p = add(add(c, scale(axis, prof[k][0] * h)), add(scale(e1, Math.cos(ang) * ra * prof[k][1]), scale(e2, Math.sin(ang) * ra * 0.9 * prof[k][1])));
				idx[k][q] = m.v(p, label);
			}
		}
		int apex = m.v(add(c, scale(axis, h)), label);
		m.prio = 10;
		int[] cols = { st.trim, st.main, st.main, st.light };
		for (int k = 0; k + 1 < prof.length; k++) {
			m.band(idx[k], idx[k + 1], true, new int[] { cols[k] }, add(c, scale(axis, -6)));
		}
		cap(m, idx[prof.length - 1], apex, st.light, c);
		// the underside of the lip, in shadow
		int under = m.v(add(c, scale(axis, -0.32 * h + 1.5)), label);
		for (int q = 0; q < sides; q++) {
			m.triAway(idx[0][q], idx[0][(q + 1) % sides], under, st.dark, add(c, scale(axis, 8)));
		}
		// three lames down the outside of the arm
		for (int k = 0; k < 3; k++) {
			double t0 = 1.5 + k * 4.6, t1 = t0 + 6.4;
			double r0 = armR + 5.6 - k * 0.8, r1 = armR + 6.6 - k * 0.8;
			int segs = 3;
			double mid = side * Math.PI / 2, span = 1.9;
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
			m.band(row[0], row[1], false, new int[] { k == 1 ? st.light : st.main }, in);
			m.band(row[1], row[2], false, new int[] { k == 2 ? st.trim : st.dark }, in);
		}
	}

	/** The plate arms (the platebody's second model): a rerebrace, a couter with a round gold-rimmed wing, a vambrace. */
	static ModelData arms(Rig r, Style st) {
		Mesh m = new Mesh();
		m.prio = 9;
		int n = 6;
		for (int side = -1; side <= 1; side += 2) {
			int up = side > 0 ? 26 : 20, el = side > 0 ? 24 : 18, fo = side > 0 ? 22 : 19;
			double[] J = mirror(pt(r.shoulder[0], r.armTop + 4, r.shoulder[2]), side), E = mirror(r.elbow, side),
					W = mirror(pt(r.wrist[0], r.wristY, r.wrist[2]), side);
			double armR = Math.max(5, (extent(r.arms, 21)[3] - extent(r.arms, 21)[0]) / 2);
			double foreR = Math.max(4, (extent(r.arms, 19)[3] - extent(r.arms, 19)[0]) / 2);
			double[] Au = norm(sub(E, J)), Af = norm(sub(W, E)), Ae = norm(add(Au, Af));
			Object[][] rr = { { J, Au, armR + 1.0, up }, { lerp(J, E, 0.6), Au, armR + 1.4, up }, { add(E, scale(Au, -2)), Au, armR + 1.8, el },
					{ add(E, scale(Af, 2.5)), Af, foreR + 2.0, el }, { add(W, scale(Af, -4)), Af, foreR + 1.8, fo }, { W, Af, foreR + 2.6, fo } };
			int[][] rings = new int[rr.length][];
			double[][] cs = new double[rr.length][];
			for (int k = 0; k < rr.length; k++) {
				cs[k] = (double[]) rr[k][0];
				double rad = (double) rr[k][2];
				rings[k] = ring(m, cs[k], (double[]) rr[k][1], rad, rad, n, 0.3, (int) rr[k][3]);
			}
			loft(m, rings, cs, new int[] { st.main, st.light, st.dark, st.main, st.main });
			// the couter's wing: a round plate on the outside of the elbow, gold-rimmed, domed
			double[] out = norm(sub(pt(side, 0, 0), scale(Ae, dot(pt(side, 0, 0), Ae))));
			double[] c = add(E, scale(out, armR + 2.6));
			int wn = 7;
			int[] rim = ring(m, c, out, 5.6, 5.6, wn, 0, el), mid = ring(m, add(c, scale(out, 1.4)), out, 4.2, 4.2, wn, 0, el);
			int tip = m.v(add(c, scale(out, 2.6)), el), back = m.v(add(c, scale(out, -1.4)), el);
			m.prio = 10;
			m.band(rim, mid, true, new int[] { st.trim }, add(c, scale(out, -3)));
			cap(m, mid, tip, st.light, add(c, scale(out, -3)));
			cap(m, rim, back, st.dark, add(c, scale(out, 3)));
			m.prio = 9;
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                                THE LEGS                                */
	/* ====================================================================== */

	/** Dark plate legs with gold-edged cuisses and greaves, rounded knee cops, and tassets over the outside of each thigh. */
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
			loft(m, idx, cs, new int[] { st.dark });
			BaseCoat.plateArc(m, r, side, r.hip[1] + 3, r.thigh[1] - 3, 1.7, thigh, st.main, st.trim, 2);
			BaseCoat.plateArc(m, r, side, r.knee[1] + 4, r.ankle[1] - 1, 1.3, shin, st.main, st.trim, 1);
			kneeCop(m, r, side, shin, st);
			tasset(m, r, side, thigh, st);
		}
		// the waistband
		int wn = 10;
		double[] s = slice(r.legs, r.waistY + 2, 0);
		double cz = (s[2] + s[3]) / 2, rx = Math.max(-s[0], s[1]) + 2.0, rz = (s[3] - s[2]) / 2 + 2.0;
		int[] w0 = hring(m, 0, r.waistY - 1.5, cz, rx, rz, wn, 0, 41), w1 = hring(m, 0, r.waistY + 5, cz, rx + 0.6, rz + 0.6, wn, 0, 41);
		m.prio = 2;
		m.band(w0, w1, true, new int[] { st.dark }, pt(0, r.waistY + 2, cz));
		return m.d;
	}

	/** A rounded knee cop over the front of the knee: a gold rim, a steel dome. */
	static void kneeCop(Mesh m, Rig r, int side, int label, Style st) {
		double y = r.knee[1] - 2.5;
		double[] lr = BaseCoat.legRing(r, y, side, 2.0);
		double[] c = pt(lr[0], y, lr[1] - lr[3] + 1.0);
		double[] axis = norm(pt(side * 0.2, 0.1, -1));
		int sides = 7;
		int[] rim = ring(m, c, axis, 6.4, 6.0, sides, 0, label), mid = ring(m, add(c, scale(axis, 1.6)), axis, 5.0, 4.6, sides, 0, label),
				top = ring(m, add(c, scale(axis, 3.4)), axis, 2.8, 2.6, sides, 0, label);
		int tip = m.v(add(c, scale(axis, 4.2)), label);
		double[] in = add(c, scale(axis, -4));
		m.prio = 3;
		m.band(rim, mid, true, new int[] { st.trim }, in);
		m.band(mid, top, true, new int[] { st.main }, in);
		cap(m, top, tip, st.light, in);
	}

	/** Tassets: two gold-lipped lames hanging from the waist over the outside and front of the thigh, on the thigh's label. */
	static void tasset(Mesh m, Rig r, int side, int label, Style st) {
		double y0 = r.waistY + 2, y1 = r.hip[1] + 16;
		int segs = 4, lames = 2;
		double lh = (y1 - y0) / lames;
		double a0 = side * 0.55, a1 = side * 2.05; // from the front-outside round to the outside-back
		for (int l = 0; l < lames; l++) {
			double ya = y0 + l * lh - (l > 0 ? 2.0 : 0), yb = y0 + (l + 1) * lh;
			double[] ys = { ya, yb - 2.2, yb };
			int[][] row = new int[3][segs + 1];
			for (int q = 0; q < 3; q++) {
				double f = (ys[q] - y0) / (y1 - y0);
				double[] lr = BaseCoat.legRing(r, Math.max(r.waistY + 3, ys[q]), side, 3.6 + 2.6 * f + l * 0.6 + (q > 0 ? 0.4 : 0));
				double cx = side * Math.max(Math.abs(lr[0]), 8.0);
				for (int j = 0; j <= segs; j++) {
					double phi = a0 + (a1 - a0) * j / segs;
					row[q][j] = m.v(ell(cx, ys[q], lr[1], lr[2] + 1.0, lr[3] + 0.5, phi), label);
				}
			}
			double[] lr = BaseCoat.legRing(r, (ya + yb) / 2, side, 0);
			double[] in = pt(side * Math.max(Math.abs(lr[0]), 8.0), (ya + yb) / 2, lr[1]);
			m.prio = 4;
			m.band(row[0], row[1], false, new int[] { l == 0 ? st.light : st.main }, in);
			m.band(row[1], row[2], false, new int[] { st.trim }, in);
		}
	}

	/* ====================================================================== */
	/*                               SABATONS                                 */
	/* ====================================================================== */

	/** Chunky sabatons: a plated shaft with a gold top band, a broad foot of gold-lipped lames, a round blunt toe. */
	static ModelData sabatons(Rig r, Style st) {
		Mesh m = new Mesh();
		int[] fb = r.feetB;
		int n = 7;
		for (int side = -1; side <= 1; side += 2) {
			int ank = side > 0 ? 38 : 32, toe = side > 0 ? 46 : 45;
			double topY = r.knee[1] + (r.ankle[1] - r.knee[1]) * 0.45;
			double[] fe = extent(r.feet, side > 0 ? 47 : 48);
			double x = (fe[0] + fe[3]) / 2, hw = (fe[3] - fe[0]) / 2;
			double sole = fb[4] + 0.5, back = fb[5] + 1.5, front = fb[2] - 1.0;
			// the shaft, flaring a little at the top, a gold band round it
			double[] q0 = BaseCoat.legRing(r, topY, side, 3.2), q1 = BaseCoat.legRing(r, r.ankle[1], side, 3.0);
			int[] a0 = hring(m, q0[0], topY, q0[1], q0[2], q0[3], n, Math.PI / n, ank), a1 = hring(m, q1[0], r.ankle[1] + 1, q1[1], q1[2], q1[3], n, Math.PI / n, ank);
			double fcz = (front + back) / 2 + 3;
			int[] a2 = hring(m, x, sole - 9.5, (q1[1] + fcz) / 2, hw + 2.4, q1[3] + 3.0, n, Math.PI / n, ank);
			m.prio = 2;
			m.band(a0, a1, true, new int[] { st.main }, pt(q0[0], (topY + r.ankle[1]) / 2, q0[1]));
			m.band(a1, a2, true, new int[] { st.dark }, pt(q1[0], r.ankle[1] + 5, q1[1]));
			int[] t0 = hring(m, q0[0], topY + 2.2, q0[1], q0[2] + 0.9, q0[3] + 0.9, n, Math.PI / n, ank),
					t1 = hring(m, q0[0], topY - 2.2, q0[1], q0[2] + 1.6, q0[3] + 1.6, n, Math.PI / n, ank);
			m.prio = 3;
			m.band(t0, t1, true, new int[] { st.trim }, pt(q0[0], topY, q0[1]));
			// the foot: sections along z from the heel to the toe, {z, top above the sole, half-width, label}
			double[][] sec = { { back, 10.5, hw + 0.4, ank }, { back - 7, 12.5, hw + 2.0, ank }, { front + 10, 8.5, hw + 2.4, toe }, { front + 3, 6.5, hw + 2.0, toe },
					{ front - 1.5, 3.6, hw + 0.6, toe } };
			int[][] sx = new int[sec.length][];
			for (int k = 0; k < sec.length; k++) {
				double z = sec[k][0], tY = sole - sec[k][1], h = sec[k][2];
				int lab = (int) sec[k][3];
				sx[k] = new int[] { m.v(pt(x - h, sole, z), lab), m.v(pt(x + h, sole, z), lab), m.v(pt(x + h * 0.95, tY + 2.5, z), lab), m.v(pt(x, tY, z), lab),
						m.v(pt(x - h * 0.95, tY + 2.5, z), lab) };
			}
			int nose = m.v(pt(x, sole - 2.0, front - 4.0), toe);
			m.prio = 2;
			for (int k = 0; k + 1 < sec.length; k++) {
				double[] cin = pt(x, sole - 3, (sec[k][0] + sec[k + 1][0]) / 2);
				for (int e = 0; e < 5; e++) {
					int e2 = (e + 1) % 5;
					int col = e == 0 ? st.dark : k == 0 ? st.main : k % 2 == 1 ? st.light : st.main;
					m.quadAway(sx[k][e], sx[k][e2], sx[k + 1][e2], sx[k + 1][e], col, cin);
				}
			}
			int last = sec.length - 1;
			for (int e = 0; e < 5; e++) {
				m.triAway(sx[last][e], sx[last][(e + 1) % 5], nose, e == 0 ? st.dark : st.trim, pt(x, sole - 3, sec[last][0] + 3));
			}
			for (int e = 1; e + 1 < 5; e++) {
				m.tri(sx[0][0], sx[0][e], sx[0][e + 1], st.dark, pt(0, 0, 1));
			}
			// the gold lips of the lames over the instep
			for (int k = 1; k <= 3; k += 2) {
				double z = sec[k][0] - 0.3, tY = sole - sec[k][1] - 0.7, hh = sec[k][2] + 0.5;
				int lab = (int) sec[k][3];
				int l0 = m.v(pt(x - hh, sole - 2.0, z), lab), l1 = m.v(pt(x, tY, z), lab), l2 = m.v(pt(x + hh, sole - 2.0, z), lab);
				int u0 = m.v(pt(x - hh, sole - 2.0, z - 2.2), lab), u1 = m.v(pt(x, tY - 0.2, z - 2.2), lab), u2 = m.v(pt(x + hh, sole - 2.0, z - 2.2), lab);
				double[] in = pt(x, sole + 4, z);
				m.prio = 3;
				m.quadAway(l0, l1, u1, u0, st.trim, in);
				m.quadAway(l1, l2, u2, u1, st.trim, in);
			}
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                               GAUNTLETS                                */
	/* ====================================================================== */

	/** Gauntlets: the hand in steel (the hands kit, its own labels), a big flared cuff with a gold rim. */
	static ModelData gauntlets(Rig r, Style st) {
		ModelData g = BaseWraith.kitCopy(r.hands, st.main);
		for (int i = 0; i < g.faces.size(); i++) {
			int[] f = g.faces.get(i);
			double y = (g.vertices.get(f[0])[1] + g.vertices.get(f[1])[1] + g.vertices.get(f[2])[1]) / 3.0;
			if (y < r.wristY + 7) { // the back of the hand over the knuckles a shade lighter
				g.colors.set(i, st.light);
			}
		}
		Mesh m = new Mesh();
		for (int side = -1; side <= 1; side += 2) {
			int fo = side > 0 ? 22 : 19;
			double[] W = mirror(BaseCoat.centroid(r.hands, 19), side), E = mirror(r.elbow, side);
			double[] Af = norm(sub(W, E));
			double[][] c = { add(W, scale(Af, 2.0)), add(W, scale(Af, -3.5)), add(W, scale(Af, -10)), add(W, scale(Af, -12)), add(W, scale(Af, -11)) };
			double[] rad = { 5.4, 6.4, 9.0, 9.4, 7.6 };
			int[][] rg = new int[c.length][];
			for (int k = 0; k < c.length; k++) {
				rg[k] = ring(m, c[k], Af, rad[k], rad[k], 8, 0.2, fo);
			}
			m.prio = 10;
			m.band(rg[0], rg[1], true, new int[] { st.dark }, W);
			m.band(rg[1], rg[2], true, new int[] { st.main }, add(W, scale(Af, -6)));
			m.band(rg[2], rg[3], true, new int[] { st.trim }, add(W, scale(Af, -10)));
			for (int j = 0; j < 8; j++) { // the rim's top, rolled over, in gold
				int jj = (j + 1) % 8;
				m.quad(rg[3][j], rg[3][jj], rg[4][jj], rg[4][j], st.trimDark, scale(Af, -1));
			}
		}
		ArmourForge.merge(g, m.d, null);
		return g;
	}

	/* ====================================================================== */
	/*                               THE SHIELD                               */
	/* ====================================================================== */

	/**
	 * A large heater shield in the rune kite's place and plane: a flat top, straight sides curving to a point; crimson
	 * in a raised gold rim, the king's charge on it in gold, steel behind. All on the left hand 28.
	 */
	static ModelData shield(ArmourForge af, Style st) throws IOException {
		ModelData kite = af.read(ArmourForge.RUNE_KITE_WORN);
		Frame k = ArmourForge.shieldFrame(kite, 61);
		double[] down = norm(sub(BaseCoat.Y, scale(k.n, dot(BaseCoat.Y, k.n))));
		double[] across = norm(ModelData.cross(k.n, down));
		double[] c = add(k.o, scale(k.n, 1.0));
		Mesh m = new Mesh();
		double W = 22, T = -25, B = 31;
		java.util.List<double[]> ol = new java.util.ArrayList<>();
		ol.add(new double[] { -W, T });
		ol.add(new double[] { 0, T - 2.5 });
		ol.add(new double[] { W, T });
		ol.add(new double[] { W, 0 });
		int seg = 4;
		for (int i = 1; i < seg; i++) {
			double a = i / (double) seg * Math.PI / 2;
			ol.add(new double[] { W * Math.cos(a), (B - 2) * Math.sin(a) });
		}
		ol.add(new double[] { 0, B });
		for (int i = seg - 1; i >= 1; i--) {
			double a = i / (double) seg * Math.PI / 2;
			ol.add(new double[] { -W * Math.cos(a), (B - 2) * Math.sin(a) });
		}
		ol.add(new double[] { -W, 0 });
		int N = ol.size();
		int[] outer = new int[N], lip = new int[N], inner = new int[N], backR = new int[N];
		double[] ctr = add(c, scale(down, 2));
		for (int i = 0; i < N; i++) {
			double[] p = ol.get(i);
			double[] q = { p[0] * 0.84, 2 + (p[1] - 2) * 0.86 };
			double[] q2 = { p[0] * 0.92, 2 + (p[1] - 2) * 0.93 };
			outer[i] = m.v(add(c, add(scale(across, p[0]), add(scale(down, p[1]), scale(k.n, 0.6)))), 28);
			lip[i] = m.v(add(c, add(scale(across, q2[0]), add(scale(down, q2[1]), scale(k.n, 2.6)))), 28);
			inner[i] = m.v(add(c, add(scale(across, q[0]), add(scale(down, q[1]), scale(k.n, 1.6)))), 28);
			backR[i] = m.v(add(c, add(scale(across, p[0]), add(scale(down, p[1]), scale(k.n, -2.4)))), 28);
		}
		int boss = m.v(add(ctr, scale(k.n, 3.4)), 28), rear = m.v(add(ctr, scale(k.n, -3.4)), 28);
		m.prio = 10;
		for (int i = 0; i < N; i++) {
			int j = (i + 1) % N;
			m.tri(inner[i], inner[j], boss, st.accent, k.n);
			double[] edgeMid = scale(add(m.p(outer[i]), m.p(outer[j])), 0.5);
			m.quad(outer[i], outer[j], lip[j], lip[i], st.trim, add(sub(edgeMid, ctr), scale(k.n, 6)));
			m.quad(lip[i], lip[j], inner[j], inner[i], st.trimDark, add(sub(ctr, edgeMid), scale(k.n, 12)));
			m.quad(outer[i], backR[i], backR[j], outer[j], st.dark, sub(edgeMid, ctr));
			m.tri(backR[i], rear, backR[j], st.main, scale(k.n, -1));
		}
		// the charge, in gold, on the field
		m.prio = 11;
		double[] o = add(c, scale(k.n, 3.0));
		charge(m, o, across, down, k.n, 0, -19, 34, 0.9, st.trim, st.core, 28);
		return m.d;
	}
}
