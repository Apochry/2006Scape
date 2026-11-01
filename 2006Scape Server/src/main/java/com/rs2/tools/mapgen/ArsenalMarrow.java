package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.LadderWeapons.band;
import static com.rs2.tools.mapgen.LadderWeapons.collar;
import static com.rs2.tools.mapgen.LadderWeapons.haft;
import static com.rs2.tools.mapgen.ModelData.hsl;
import static com.rs2.tools.mapgen.QuestWeapons.f;
import static com.rs2.tools.mapgen.QuestWeapons.plateSW;
import static com.rs2.tools.mapgen.QuestWeapons.tube;

/**
 * The Marrowbound arsenal (A41): six weapons out of the barrows' leftovers, all one family of yellowed bone, dark hide
 * grips and pale sinew lashings, the set's sickly green glow kept for the staff's skull. Each is built in the weapon
 * frame of {@link QuestWeapons} (s along from the butt, w across to the blade or belly side, t through) and placed in the
 * pose of the stock item it's wielded like; see DraftArmourD.ARSENAL.
 *
 * <ol>
 * <li>Jawbone axe (battleaxe): a great beast's jawbone, its teeth the edge, lashed to a long dark bone haft, a tusk
 * hooked off the back.</li>
 * <li>Femur club (mace): a giant thighbone, its ball-end the head, with a ring of knuckle-bones bound round it.</li>
 * <li>Rib spear (spear): three long bones lashed end to end, tipped with a serrated rib-blade, two teeth hanging off
 * the socket on cords.</li>
 * <li>Fang dagger (dagger): one enormous curved fang on a hide-wrapped grip, a knuckle-bone pommel.</li>
 * <li>Marrowbound longbow (longbow, the set's signature): two long bones for limbs, knobbed at both ends, lashed into a
 * vertebra for a grip, strung with sinew.</li>
 * <li>Skull totem staff (staff): a crooked bone staff with knuckle joints, a little skull on top with the green glow in
 * its eyes, and three teeth dangling under it.</li>
 * </ol>
 */
final class ArsenalMarrow {

	private ArsenalMarrow() {
	}

	/** The set's palette: yellowed bone in three tones, dark hide, fur, sinew, and the glow. */
	static final int BONE = hsl(8, 2, 92), BONE_DARK = hsl(7, 2, 68), BONE_LIGHT = hsl(8, 1, 112), HIDE = hsl(5, 3, 30), HIDE_DARK = hsl(5, 3, 19),
			FUR = hsl(6, 2, 48), FUR_DARK = hsl(5, 2, 30), SINEW = hsl(6, 3, 56), SINEW_PALE = hsl(7, 2, 80), GLOW = hsl(17, 6, 80),
			GLOW_CORE = hsl(17, 4, 108), SOCKET = BaseMarrow.SOCKET;

	/* ------------------------------------------------------------- shared bits */

	/** A hide-wrapped grip from s0 to s1, a sinew lashing at each end. */
	static void grip(ModelData m, double s0, double s1, double r) {
		band(m, s0, s1, r, HIDE);
		band(m, s0, s0 + 1.4, r + 0.35, SINEW);
		band(m, s1 - 1.4, s1, r + 0.35, SINEW);
	}

	/** A knuckle-bone knob (two round cones back to back) centred at s. */
	static void knuckle(ModelData m, double s, double r, double len) {
		tube(m, f(s, 0, 0), f(s - len, 0, 0), r, r * 0.45, 5, 0, BONE, BONE_DARK);
		tube(m, f(s, 0, 0), f(s + len, 0, 0), r, r * 0.55, 5, 0, BONE_LIGHT, BONE);
	}

	/** A tooth hanging on a sinew cord from {@code a} to {@code b}, the tooth pointing on along the cord for {@code len}. */
	static void charm(ModelData m, double[] a, double[] b, double len) {
		tube(m, a, b, 0.55, 0.55, 3, 0, SINEW, SINEW);
		double[] d = { b[0] - a[0], b[1] - a[1], b[2] - a[2] };
		double l = Math.sqrt(d[0] * d[0] + d[1] * d[1] + d[2] * d[2]);
		double[] e = { b[0] + d[0] / l * len * QuestWeapons.K, b[1] + d[1] / l * len * QuestWeapons.K, b[2] + d[2] / l * len * QuestWeapons.K };
		tube(m, b, e, 1.5, 0, 4, Math.PI / 4, BONE_LIGHT, BONE);
	}

	/**
	 * An oval-sectioned horn, fang or tusk along stations {s, w, half-width along w, half-thickness along t}, closing to a
	 * point at {tipS, tipW}; each span in its own colour (cols[i], the last one for the point).
	 */
	static void tusk(ModelData m, double[][] st, double tipS, double tipW, int[] cols) {
		int n = 6;
		int[][] rg = new int[st.length][n];
		for (int i = 0; i < st.length; i++) {
			for (int j = 0; j < n; j++) {
				double a = Math.PI * 2 * j / n;
				double[] p = f(st[i][0], st[i][1] + Math.cos(a) * st[i][2], Math.sin(a) * st[i][3]);
				rg[i][j] = m.vertex(p[0], p[1], p[2]);
			}
		}
		for (int i = 0; i + 1 < st.length; i++) {
			double[] ref = f((st[i][0] + st[i + 1][0]) / 2, (st[i][1] + st[i + 1][1]) / 2, 0);
			for (int j = 0; j < n; j++) {
				int k = (j + 1) % n;
				m.quadOut(rg[i][j], rg[i][k], rg[i + 1][k], rg[i + 1][j], cols[Math.min(i, cols.length - 1)], ref);
			}
		}
		int last = st.length - 1;
		double[] tp = f(tipS, tipW, 0);
		int tip = m.vertex(tp[0], tp[1], tp[2]);
		double[] ref = f((st[last][0] + tipS) / 2, (st[last][1] + tipW) / 2, 0);
		for (int j = 0; j < n; j++) {
			m.triOut(rg[last][j], rg[last][(j + 1) % n], tip, cols[cols.length - 1], ref);
		}
		double[] bref = f(st[0][0] + 4, st[0][1], 0);
		for (int j = 1; j + 1 < n; j++) {
			m.triOut(rg[0][0], rg[0][j], rg[0][j + 1], cols[0], bref);
		}
	}

	/* ====================================================================== */
	/*                          1. THE JAWBONE AXE                            */
	/* ====================================================================== */

	/**
	 * A great beast's jawbone for a head: a broad bone crescent out to the edge side (+w, where the rune battleaxe's
	 * longer bit is), its outer arc set with teeth, molars at the back and long fangs at the chin; lashed to a dark bone
	 * haft with sinew, a tusk hooked off the back. Battleaxe frame: L 66, the hand at s 9.
	 */
	static ModelData jawboneAxe() {
		ModelData m = new ModelData();
		haft(m, 1, 66, 2.5, 2.3, BONE_DARK);
		knuckle(m, 0, 3.4, 2.8);
		grip(m, 3, 17, 2.9);
		// the jaw
		double[][] jaw = { { 58, 2.5 }, { 66, 3 }, { 71, 9 }, { 67, 17 }, { 57, 23 }, { 45, 25 }, { 35, 23 }, { 31, 18 }, { 37, 14.5 }, { 45, 12.5 },
				{ 51, 8.5 }, { 52, 3 } };
		double[] h = { 2.6, 2.6, 2.2, 2.0, 1.9, 1.9, 1.9, 1.9, 1.8, 1.8, 2.0, 2.6 };
		plateSW(m, jaw, h, new double[] { 56, 14 }, 2.6, BONE, BONE, BONE_DARK);
		// the teeth round the outer arc: blunt molars at the back, long fangs at the chin
		double[][] teeth = { { 70.5, 11.5, 2.4, 3.5 }, { 67.5, 16.5, 2.3, 4.0 }, { 62, 20.5, 2.1, 4.5 }, { 55, 23.7, 1.9, 5.5 }, { 47.5, 25, 1.9, 6.5 },
				{ 39.5, 24.5, 2.2, 8.5 } };
		for (double[] t : teeth) {
			double ds = t[0] - 54, dw = t[1] - 12, l = Math.sqrt(ds * ds + dw * dw);
			ds /= l;
			dw /= l;
			tube(m, f(t[0] - ds, t[1] - dw, 0), f(t[0] + ds * t[3], t[1] + dw * t[3], 0), t[2], 0, 4, Math.PI / 4, BONE_LIGHT, BONE_LIGHT);
		}
		// lashed to the haft
		band(m, 49, 52, 3.4, SINEW);
		band(m, 60, 63, 3.3, SINEW);
		knuckle(m, 67, 3.0, 2.4);
		// a tusk hooked off the back
		tube(m, f(56, -2, 0), f(53, -9, 0), 2.6, 1.8, 5, 0, BONE, BONE);
		tube(m, f(53, -9, 0), f(46, -14, 0), 1.8, 0, 5, 0, BONE_LIGHT, BONE_LIGHT);
		// a tooth on a cord from the pommel
		charm(m, f(-1, 1.5, 0), f(-7, 5, 0), 4.5);
		return m;
	}

	/* ====================================================================== */
	/*                            2. THE FEMUR CLUB                           */
	/* ====================================================================== */

	/**
	 * A giant's thighbone, knee end down for a pommel, the ball end up for a head, with six knuckle-bones bound round
	 * the head in sinew. Mace frame: L 70, the hand at s 8, the head round s 56.
	 */
	static ModelData femurClub() {
		ModelData m = new ModelData();
		// the knee end: two knobs side by side
		tube(m, f(4, 1.8, 0), f(-1, 1.8, 0), 3.0, 1.2, 6, 0, BONE, BONE_DARK);
		tube(m, f(4, -1.8, 0), f(-1, -1.8, 0), 3.0, 1.2, 6, 0, BONE, BONE_DARK);
		// the shaft, swelling to the neck
		tube(m, f(3, 0, 0), f(44, 0, 0), 2.7, 3.0, 6, 0, BONE, BONE);
		grip(m, 5, 19, 3.2);
		tube(m, f(44, 0, 0), f(50, 0, 0), 3.0, 4.6, 6, 0, BONE_DARK, BONE_DARK);
		// the ball
		tube(m, f(50, 0, 0), f(57, 0.8, 0), 4.6, 8.4, 6, 0, BONE, BONE);
		tube(m, f(57, 0.8, 0), f(65, 0.8, 0), 8.4, 6.0, 6, 0, BONE_LIGHT, BONE_LIGHT);
		tube(m, f(65, 0.8, 0), f(71, 0.4, 0), 6.0, 0, 6, 0, BONE_LIGHT, BONE_LIGHT);
		// the knob of the hip joint off one side
		tube(m, f(52, -3, 0), f(59, -10, 0), 4.0, 1.8, 5, 0, BONE, BONE_DARK);
		// the knuckle-bones round the head, bound in sinew
		band(m, 55.2, 58.2, 8.4, SINEW);
		for (int i = 0; i < 6; i++) {
			double a = Math.PI * 2 * i / 6 + Math.PI / 6;
			double cw = Math.cos(a), ct = Math.sin(a);
			tube(m, f(56.7, cw * 6.5, ct * 6.5), f(57.2, cw * 11.0, ct * 11.0), 2.6, 2.1, 4, Math.PI / 4, BONE_LIGHT, BONE_LIGHT);
			tube(m, f(57.2, cw * 11.0, ct * 11.0), f(57.4, cw * 13.6, ct * 13.6), 2.1, 0, 4, Math.PI / 4, BONE, BONE);
		}
		charm(m, f(-1, 0, 0), f(-7, 3.5, 0), 4.0);
		return m;
	}

	/* ====================================================================== */
	/*                             3. THE RIB SPEAR                           */
	/* ====================================================================== */

	/**
	 * Three long bones lashed end to end for a shaft, knobbed at the joints, a hide grip, and a serrated rib-blade for a
	 * head bound into a sinew socket, two teeth hanging off it on cords. Spear frame: L 262, the hand at s 122.
	 */
	static ModelData ribSpear() {
		ModelData m = new ModelData();
		knuckle(m, 2.5, 3.6, 3.0);
		haft(m, 5, 76, 2.7, 2.9, BONE);
		haft(m, 76, 150, 2.9, 2.8, BONE_DARK);
		haft(m, 150, 222, 2.8, 2.7, BONE);
		for (double s : new double[] { 76, 150 }) {
			collar(m, s - 2.5, s + 2.5, 3.9, 3.9, BONE_LIGHT);
			band(m, s - 5, s - 3, 3.2, SINEW);
			band(m, s + 3, s + 5, 3.2, SINEW);
		}
		grip(m, 108, 134, 3.3);
		// the socket: sinew wound thick, a hide band round it
		collar(m, 212, 226, 3.4, 3.0, SINEW);
		band(m, 216, 219, 3.7, HIDE_DARK);
		// the rib-blade, its +w edge cut into teeth
		double[][] blade = { { 222, -3.4 }, { 222, 3.4 }, { 231, 7.8 }, { 235, 5.4 }, { 241, 8.8 }, { 245, 6.0 }, { 251, 9.0 }, { 255, 6.2 }, { 261, 8.2 },
				{ 265, 5.6 }, { 270, 5.4 }, { 284, 1.2 }, { 270, -3.6 }, { 254, -6.6 }, { 236, -6.8 } };
		double[] h = { 1.8, 1.8, 0.5, 1.2, 0.4, 1.2, 0.4, 1.2, 0.4, 1.2, 0.5, 0, 0.6, 0.9, 1.3 };
		plateSW(m, blade, h, new double[] { 248, 0.8 }, 2.0, BONE, BONE_LIGHT, BONE_DARK);
		// two teeth on cords off the socket
		charm(m, f(214, 3.4, 1.0), f(199, 6.5, 2.0), 5.0);
		charm(m, f(214, 2.4, -2.0), f(202, 4.5, -4.0), 4.0);
		return m;
	}

	/* ====================================================================== */
	/*                            4. THE FANG DAGGER                          */
	/* ====================================================================== */

	/** One enormous fang, curved towards its edge (+w), its yellowed root set on a hide grip, a knuckle-bone pommel. Dagger frame: L 38, the hand at s 7. */
	static ModelData fangDagger() {
		ModelData m = new ModelData();
		knuckle(m, 0.5, 2.9, 2.2);
		tube(m, f(1.5, 0, 0), f(13, 0, 0), 2.1, 1.9, 4, Math.PI / 4, HIDE, HIDE);
		band(m, 2.5, 3.8, 2.4, SINEW);
		band(m, 7.0, 8.3, 2.3, SINEW);
		band(m, 11.2, 12.5, 2.2, SINEW);
		tusk(m, new double[][] { { 12.5, 0, 2.8, 2.6 }, { 15.5, 0.2, 3.9, 3.0 }, { 23, 0.9, 3.6, 2.4 }, { 31, 2.2, 2.7, 1.8 }, { 37, 3.9, 1.7, 1.2 } }, 43,
				6.6, new int[] { BONE_DARK, BONE, BONE, BONE_LIGHT, BONE_LIGHT });
		charm(m, f(-1.2, 0.8, 0), f(-5.5, 3.6, 0), 3.0);
		return m;
	}

	/* ====================================================================== */
	/*                         5. THE MARROWBOUND LONGBOW                     */
	/* ====================================================================== */

	/**
	 * Two long bones for limbs, each knobbed at both ends, bowed out to the belly (+w) and lashed into a vertebra for a
	 * grip (its spine standing out from the belly, wings either side), strung with sinew on the -w side between bone
	 * nocks. Longbow frame: L 185 tip to tip, the hand at the middle; {@code bend} deepens the curve (1.3 for the
	 * inventory model, as the ladder's longbows).
	 */
	static ModelData longbow(double bend) {
		ModelData m = new ModelData();
		double L = 185, rk = Math.sqrt(L / 127), mid = L / 2;
		double gw = 6 * bend * rk, bw = 9.5 * bend * rk, tw = 4 * bend * rk;
		for (int d = -1; d <= 1; d += 2) {
			double[][] p = { { mid + d * 9, gw }, { mid + d * 38, bw }, { mid + d * 66, bw * 0.82 }, { mid + d * 86, tw } };
			double[] r = { 3.6, 3.2, 2.8, 2.4 };
			for (int i = 0; i + 1 < p.length; i++) {
				tube(m, f(p[i][0], p[i][1], 0), f(p[i + 1][0], p[i + 1][1], 0), r[i], r[i + 1], 5, 0, i == 1 ? BONE_DARK : BONE, BONE);
			}
			// the knobs: where it goes into the grip, and the tip, with a hooked nock past it
			tube(m, f(p[0][0] - d * 2, p[0][1], 0), f(p[0][0] + d * 4, p[0][1] + 0.5, 0), 5.0, 3.8, 6, 0, BONE_LIGHT, BONE_LIGHT);
			tube(m, f(p[3][0] - d * 3.5, p[3][1] + 0.4, 0), f(p[3][0] + d * 1.5, p[3][1], 0), 3.0, 4.2, 6, 0, BONE_LIGHT, BONE_LIGHT);
			tube(m, f(p[3][0] + d * 1.5, p[3][1], 0), f(p[3][0] + d * 6.5, p[3][1] + 4.0, 0), 3.6, 0, 5, 0, BONE, BONE);
			band(m, p[0][0] + d * 6.0 - 1.4, p[0][0] + d * 6.0 + 1.4, 4.1, SINEW);
		}
		// the vertebra: a stout body, hide round the middle where the hand goes, the spine standing out, a wing either side
		tube(m, f(mid - 8, gw, 0), f(mid + 8, gw, 0), 5.8, 5.8, 6, 0, BONE, BONE);
		tube(m, f(mid - 3.5, gw, 0), f(mid + 3.5, gw, 0), 6.3, 6.3, 6, 0, HIDE, HIDE);
		tube(m, f(mid - 2, gw + 4, 0), f(mid + 3, gw + 16, 0), 3.6, 1.0, 4, Math.PI / 4, BONE_LIGHT, BONE_LIGHT);
		tube(m, f(mid + 4, gw + 2, 4.5), f(mid + 2, gw + 5, 12), 2.4, 0.7, 4, Math.PI / 4, BONE, BONE);
		tube(m, f(mid + 4, gw + 2, -4.5), f(mid + 2, gw + 5, -12), 2.4, 0.7, 4, Math.PI / 4, BONE, BONE);
		// the string
		tube(m, f(mid - 86, tw - 1.2, 0), f(mid + 86, tw - 1.2, 0), 0.8, 0.8, 3, 0, SINEW_PALE, SINEW_PALE);
		// a tooth hanging off the lower lashing
		charm(m, f(mid - 15, gw + 3.5, 0), f(mid - 21, gw + 10, 0), 4.5);
		return m;
	}

	/* ====================================================================== */
	/*                          6. THE SKULL TOTEM STAFF                      */
	/* ====================================================================== */

	/**
	 * A crooked bone staff, knuckle joints at its bends, a hide grip; on top a little skull looking out over the +w side
	 * (forward in the hand) with the green glow in its sockets, a short snout with two teeth, and three teeth dangling on
	 * cords under it. Staff frame: L 232, the hand at s 119; the skull reaches past the top like the magic staff's orb.
	 */
	static ModelData skullStaff() {
		ModelData m = new ModelData();
		double[][] sh = { { 4, 0, 0 }, { 58, 1.8, 0.6 }, { 104, -1.3, -0.4 }, { 150, 1.6, 0.3 }, { 204, 0, 0 } };
		double[] r = { 3.4, 3.8, 3.5, 3.8, 3.6 };
		for (int i = 0; i + 1 < sh.length; i++) {
			tube(m, f(sh[i][0], sh[i][1], sh[i][2]), f(sh[i + 1][0], sh[i + 1][1], sh[i + 1][2]), r[i], r[i + 1], 5, 0.3 * i, i % 2 == 0 ? BONE : BONE_DARK,
					BONE);
		}
		for (int i = 1; i < 4; i++) {
			tube(m, f(sh[i][0] - 2.5, sh[i][1], sh[i][2]), f(sh[i][0] + 2.5, sh[i][1], sh[i][2]), 5.0, 5.0, 5, 0, BONE_LIGHT, BONE_LIGHT);
		}
		tube(m, f(4, 0, 0), f(-1, 0, 0), 4.2, 1.5, 6, 0, BONE_DARK, BONE_DARK);
		grip(m, 110, 132, 4.3);
		// the collar the skull sits in
		collar(m, 194, 204, 4.6, 6.0, HIDE);
		band(m, 196, 197.5, 5.6, SINEW);
		// the skull: round the staff's axis, its face turned from +w (forward in the hand) a third of the way round to -t (up, in the inventory)
		double F = -Math.PI / 3, fw = Math.cos(F), ft = Math.sin(F);
		double[][] st = { { 202, 5.5 }, { 207, 10.4 }, { 216, 12.0 }, { 225, 10.6 }, { 231, 6.4 } };
		int n = 6;
		int[][] rg = new int[st.length][n];
		for (int i = 0; i < st.length; i++) {
			for (int j = 0; j < n; j++) {
				double a = Math.PI * 2 * j / n;
				double[] p = f(st[i][0], Math.cos(a) * st[i][1], Math.sin(a) * st[i][1]);
				rg[i][j] = m.vertex(p[0], p[1], p[2]);
			}
		}
		for (int i = 0; i + 1 < st.length; i++) {
			double[] ref = f((st[i][0] + st[i + 1][0]) / 2, 0, 0);
			for (int j = 0; j < n; j++) {
				int k = (j + 1) % n;
				double face = Math.cos(Math.PI * 2 * (j + 0.5) / n - F); // 1 where the quad faces the way the skull looks
				int col = i == 1 && face > 0.8 ? SOCKET : i == 0 ? BONE_DARK : i == 2 && face < -0.4 ? BONE_DARK : i >= 2 ? BONE_LIGHT : BONE;
				m.quadOut(rg[i][j], rg[i][k], rg[i + 1][k], rg[i + 1][j], col, ref);
			}
		}
		double[] apex = f(235, 0, 0);
		int ap = m.vertex(apex[0], apex[1], apex[2]);
		for (int j = 0; j < n; j++) {
			m.triOut(rg[st.length - 1][j], rg[st.length - 1][(j + 1) % n], ap, BONE_LIGHT, f(229, 0, 0));
		}
		// the glow in the sockets
		for (int e = -1; e <= 1; e += 2) {
			double a = F + e * Math.PI / 6;
			double rr = 10.2;
			double cw = Math.cos(a), ct = Math.sin(a);
			tube(m, f(211.5, cw * (rr - 1.5), ct * (rr - 1.5)), f(211.5, cw * (rr + 1.2), ct * (rr + 1.2)), 3.0, 0, 4, Math.PI / 4, GLOW, GLOW_CORE);
		}
		// the snout and its two teeth
		tube(m, f(208, fw * 7, ft * 7), f(204.5, fw * 15, ft * 15), 4.6, 3.2, 4, Math.PI / 4, BONE, BONE_DARK);
		double[][] fang = { { 13, 2.0 }, { 13, -2.0 } };
		for (double[] q : fang) { // two teeth under the snout, either side of it
			double qw = fw * q[0] - ft * q[1], qt = ft * q[0] + fw * q[1];
			tube(m, f(205, qw, qt), f(199, qw * 1.04, qt * 1.04), 1.3, 0, 3, 0, BONE_LIGHT, BONE_LIGHT);
		}
		// three teeth dangling under it
		for (int k = 0; k < 3; k++) {
			double a = Math.PI * 2 * k / 3 + 0.5;
			double cw = Math.cos(a), ct = Math.sin(a);
			charm(m, f(195, cw * 5.8, ct * 5.8), f(181 - k * 3, cw * 7.5, ct * 7.5), 5.0);
		}
		return m;
	}
}
