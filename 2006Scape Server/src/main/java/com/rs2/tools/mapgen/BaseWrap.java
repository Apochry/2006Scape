package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ArmourForge.add;
import static com.rs2.tools.mapgen.ArmourForge.decal;
import static com.rs2.tools.mapgen.ArmourForge.diamond;
import static com.rs2.tools.mapgen.ArmourForge.dome;
import static com.rs2.tools.mapgen.ArmourForge.norm;
import static com.rs2.tools.mapgen.ArmourForge.rect;
import static com.rs2.tools.mapgen.ArmourForge.ring;
import static com.rs2.tools.mapgen.ArmourForge.scale;
import static com.rs2.tools.mapgen.ArmourForge.sub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.DoublePredicate;

import com.rs2.tools.mapgen.ArmourForge.Frame;
import com.rs2.tools.mapgen.ArmourSets.Style;

/**
 * The wrap base (Kind.WRAP): a light skirmisher's kit for rangers, built from scratch over the default body, so it
 * shares nothing with the leather / hide meshes but the rig.
 *
 * <ul>
 * <li>Helm: a cloth headband round the brow, knotted at the back with two tails hanging; the hair and crown show.</li>
 * <li>Body: a short, open-throated vest with bare shoulders, a wound waist-sash knotted at the right hip, a bandolier
 * from the left shoulder to the right hip with two pouches, and a guard of two leather lames and a cap on the left
 * shoulder only. Named "vest" / "harness", so the arms kit stays: the arms are bare.</li>
 * <li>Legs: a short kilt of twelve hanging panels in two layers (long under-panels in the gaps of the short outer
 * ones) and a loincloth flap down the middle in front and behind, on a belt with a pouch, over leggings cross-bound
 * with two thongs per leg.</li>
 * <li>Boots: front greaves from ankle to knee, strapped round the calf, over wrapped feet on sandal soles with the
 * toes bare.</li>
 * <li>Gloves: long leather bracers from wrist to elbow, flared and bound with two cords, the palms wrapped and the
 * fingers bare.</li>
 * </ul>
 *
 * <p>Shapes follow the body: every limb and the trunk is measured from the default identity kits (horizontal sections
 * of the kit mesh, as a convex outline round its centre, see {@link Section}) and the new rings are laid round those
 * outlines a little proud of the skin. Bare skin is drawn in the kits' skin colour (4550), which the client swaps for
 * the player's own.
 *
 * <p>Animation labels. The player's frames rotate each limb about the centroid of a few "pivot" labels (the shoulder
 * about 25, the elbow about 23, the hip about 40, the knee about 35 + 37, the wrist about 28, the foot about 47 ...), so
 * new vertices must not pile onto those labels or the joints would move. New vertices take a label that moves exactly
 * the same way but pivots nothing ({@link #safe}): thigh 43 / 44 (the skirt halves, which turn with the hip), shin 36 /
 * 33 (the knee labels), ankle 38 / 32, upper arm 26 / 20, forearm 22 / 19, chest 8, waist 30, skull 1. The donor's own
 * vertices on pivot labels are kept, faceless, so every pivot stays exactly where the stock piece has it; the hands
 * and feet are the kit's own (wrapped), keeping their labels. Otherwise a part takes one label (the guard 26, a bracer
 * 22 / 19, a greave 36 / 33), labels by height (a leg: thigh, shin, ankle), or the label of the nearest kit vertex made
 * safe (the vest, the seat, the soles). Each kilt panel hangs on one thigh (none crosses the middle, which would tear
 * it as the legs part): its top row on the waist (30), the rest on that thigh's skirt half; the middle flaps hang from
 * the waist on the still pelvis (41), between the thighs. The sash ends take the nearest rune plateskirt vertex's label
 * (made safe), so they swing like the plateskirt.
 */
final class BaseWrap {

	private BaseWrap() {
	}

	/** The kit index (ArmourPreview.MALE / FEMALE) of the head, torso, arms, hands, legs and feet. */
	static final int HEAD = 0, TORSO = 2, ARMS = 3, HANDS = 4, LEGS = 5, FEET = 6;
	/** The rune plateskirt (male, female): the cloth rig for the sash ends. */
	static final int SKIRT_MALE = 266, SKIRT_FEMALE = 430;
	/** The kits' skin colour; the client turns it into the player's. */
	static final int SKIN = 4550;
	/** Labels whose centroid is a joint's pivot in the player's frames: kept only on the donor's own vertices. */
	static final Set<Integer> PIVOTS = Set.of(0, 2, 3, 5, 17, 21, 23, 25, 27, 28, 29, 31, 34, 35, 37, 40, 42, 47, 48);

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		int[] kit = female ? ArmourPreview.FEMALE : ArmourPreview.MALE;
		ModelData m;
		switch (slot) {
		case ArmourSets.HELM:
			m = helm(af, st, kit);
			break;
		case ArmourSets.BODY:
			m = body(af, st, kit, female);
			break;
		case ArmourSets.LEGS:
			m = legs(af, st, kit);
			break;
		case ArmourSets.BOOTS:
			m = boots(af, st, kit);
			break;
		case ArmourSets.GLOVES:
			m = gloves(af, st, kit);
			break;
		default:
			return new ModelData[] { donor, donorSecond };
		}
		anchors(m, donor);
		return new ModelData[] { m, null };
	}

	/* ====================================================================== */
	/*                                  HELM                                  */
	/* ====================================================================== */

	/** A headband round the brow, lower at the back, knotted there with two tails; a stud on the brow. */
	static ModelData helm(ArmourForge af, Style st, int[] kit) throws IOException {
		ModelData head = af.read(kit[HEAD]);
		double top = head.bounds()[1], chin = extreme(head, 1, 1, true);
		double hh = chin - top, y0 = top + hh * 0.2, y1 = top + hh * 0.42, tilt = hh * 0.1;
		DoublePredicate all = x -> true;
		int K = 12;
		ModelData out = new ModelData();
		ModelData band = part();
		int[] up = new int[K], lo = new int[K], in = new int[K];
		double[] c0 = null;
		for (int k = 0; k < K; k++) {
			double a = -Math.PI / 2 + Math.PI * 2 * k / K, dy = tilt * Math.max(0, Math.sin(a));
			Section s0 = section(head, y0 + dy, all), s1 = section(head, y1 + dy, all);
			up[k] = v(band, s0.at(a, 1.0, y0 + dy));
			lo[k] = v(band, s1.at(a, 1.3, y1 + dy));
			in[k] = v(band, s0.at(a, -1.5, y0 + dy + 0.3));
			c0 = c0 == null ? new double[] { s0.cx, 0, s0.cz } : c0;
		}
		for (int k = 0; k < K; k++) {
			int j = (k + 1) % K;
			double a = -Math.PI / 2 + Math.PI * 2 * (k + 0.5) / K;
			double[] ref = { c0[0], (y0 + y1) / 2 + tilt * Math.max(0, Math.sin(a)), c0[2] };
			band.quadOut(up[k], up[j], lo[j], lo[k], st.main, ref);
			band.quadOut(in[k], in[j], up[j], up[k], st.dark, add(ref, new double[] { 0, 8, 0 }));
		}
		// the stud on the brow, then the knot and its two tails at the back
		ModelData bits = part();
		decal(bits, band, ArmourSets.FRONT, diamond(0, (y0 + y1) / 2, 1.7, 1.9), 0.3, 1.3, st.light, st.dark);
		Section sb = section(head, (y0 + y1) / 2 + tilt, all);
		double[] knot = sb.at(Math.PI / 2, 2.0, (y0 + y1) / 2 + tilt);
		bits.tube(add(knot, new double[] { -2.6, 0, -0.6 }), add(knot, new double[] { 2.6, 0, -0.6 }), 2.2, 2.2, 5, 0.3, st.dark, st.dark);
		for (int side = -1; side <= 1; side += 2) {
			double[][] path = { add(knot, new double[] { side * 1.2, 0.5, 1.0 }), add(knot, new double[] { side * 2.6, 6, 3.8 }),
					add(knot, new double[] { side * 3.8, 11.5 - side, 5.6 }), add(knot, new double[] { side * 4.4, 16.5 - side * 1.5, 6.6 }) };
			tail(bits, path, new double[] { 1, 0, 0 }, new double[] { 2.0, 1.8, 1.5, 1.1 }, st.main, st.dark, st.light);
		}
		finish(out, band, 7, 1);
		finish(out, bits, 7, 1);
		return out;
	}

	/* ====================================================================== */
	/*                                  BODY                                  */
	/* ====================================================================== */

	/** The vest, the waist-sash and its knot, the bandolier and pouches, the left shoulder guard. */
	static ModelData body(ArmourForge af, Style st, int[] kit, boolean female) throws IOException {
		ModelData torso = af.read(kit[TORSO]), arms = af.read(kit[ARMS]);
		ModelData skirt = af.read(female ? SKIRT_FEMALE : SKIRT_MALE);
		int[] tb = torso.bounds();
		double top = tb[1], bot = tb[4], h = bot - top, hw = tb[3], cz = (tb[2] + tb[5]) / 2.0;
		DoublePredicate all = x -> true;
		ModelData out = new ModelData();

		// the vest: rings round the trunk from the neck to the top of the sash; open at the throat, cut away at the shoulders
		int K = 12;
		double[] rows = { 0.01, 0.06, 0.18, 0.34, 0.53, 0.74 };
		double[][][] pts = new double[rows.length][K][];
		double[][] centres = new double[rows.length][];
		for (int r = 0; r < rows.length; r++) {
			double y = Math.min(top + h * rows[r], bot - 0.4);
			Section s = section(torso, y, all);
			centres[r] = new double[] { s.cx, y, s.cz };
			for (int k = 0; k < K; k++) {
				pts[r][k] = s.at(-Math.PI / 2 + Math.PI * 2 * k / K, 1.2, y);
			}
		}
		ModelData vest = part();
		double vDepth = 0.36, vHalf = hw * 0.4;
		loft(vest, pts, centres, true, (r, k, c) -> {
			double t = (c[1] - top) / h, ax = Math.abs(c[0]);
			boolean front = c[2] < cz;
			if (front && t < vDepth && ax < vHalf * (1 - t / vDepth)) {
				return SKIN; // the open throat
			}
			if (t < 0.2 && ax > hw * (0.6 + t * 1.3)) {
				return SKIN; // bare shoulders: the vest's straps are narrower than the trunk
			}
			return st.main;
		});
		finish(out, vest, PRIO_VEST, -1, torso);

		// the waist-sash: a wound band, proud of the vest, from its hem to the waist
		double[] srows = { 0.68, 1.0 };
		ModelData sash = part();
		double[][][] sp = new double[srows.length + 1][K][];
		double[][] sc = new double[srows.length + 1][];
		for (int r = 0; r <= srows.length; r++) {
			double t = r == 0 ? srows[0] : srows[r - 1];
			double y = Math.min(top + h * t, bot - 0.4);
			Section s = section(torso, y, all);
			sc[r] = new double[] { s.cx, y, s.cz };
			for (int k = 0; k < K; k++) {
				sp[r][k] = s.at(-Math.PI / 2 + Math.PI * 2 * k / K, r == 0 ? 0.6 : 2.6 + (r == 2 ? 0.4 : 0), y + (r == 0 ? -0.6 : 0));
			}
		}
		loft(sash, sp, sc, true, (r, k, c) -> st.dark);
		finish(out, sash, PRIO_SASH, -1, torso);

		ModelData onto = ArmourForge.mergeAll(vest, sash);

		// the bandolier: over the left shoulder, down across the chest and the back to the right hip
		ModelData strap = part();
		double[] line = strapLine(onto);
		double sx0 = line[0], sy0 = line[1], ex = line[2], ey = line[3];
		for (Frame f : new Frame[] { ArmourSets.FRONT, ArmourSets.BACK }) {
			double[][] path = new double[6][];
			for (int i = 0; i < path.length; i++) {
				double s = i / (path.length - 1.0);
				path[i] = new double[] { sx0 + (ex - sx0) * s, sy0 + (ey - sy0) * s };
			}
			ribbon(strap, onto, f, path, 2.3, 1.0, st.leather, -1);
		}
		Frame topF = new Frame(new double[] { 0, top - 60, 0 }, new double[] { 1, 0, 0 }, new double[] { 0, 0, 1 }, new double[] { 0, -1, 0 });
		ribbon(strap, onto, topF, new double[][] { { sx0, tb[2] + 1.5 }, { sx0, cz }, { sx0, tb[5] - 1.5 } }, 2.3, 1.0, st.leather, -1);
		// a buckle and two pouches on the front run
		double[] dir = norm(new double[] { ex - sx0, ey - sy0, 0 });
		double[] across = { -dir[1], dir[0] };
		decal(strap, onto, ArmourSets.FRONT, turned(sx0 + (ex - sx0) * 0.2, sy0 + (ey - sy0) * 0.2, dir, across, 1.6, 2.6), 0.4, 1.9, st.light, st.leatherDark);
		for (double s : new double[] { 0.48, 0.66 }) {
			decal(strap, onto, ArmourSets.FRONT, turned(sx0 + (ex - sx0) * s, sy0 + (ey - sy0) * s, dir, across, 2.6, 3.0), 0.4, 3.6, st.leatherDark,
					st.leather);
		}
		finish(out, strap, 5, -1, torso);

		// the sash's knot at the right hip and its two ends, hanging over the belt and the kilt
		ModelData knot = part();
		double ky = top + h * 0.86;
		Section ks = section(torso, Math.min(ky, bot - 0.4), all);
		double ka = Math.PI + 0.55;
		double[] kp = ks.at(ka, 3.4, ky), kn = { Math.cos(ka), 0, Math.sin(ka) };
		knot.tube(sub(kp, scale(kn, 1.5)), add(kp, scale(kn, 1.8)), 2.6, 2.0, 5, 0.2, st.light, st.dark);
		for (int e = 0; e < 2; e++) {
			double off = e == 0 ? -1.4 : 1.6, len = e == 0 ? 1.0 : 0.8;
			double[] t = { -Math.sin(ka), 0, Math.cos(ka) }; // along the hip
			double[][] path = { add(kp, add(scale(t, off), scale(kn, 1.2))), add(kp, add(scale(t, off * 1.4), add(scale(kn, 2.0), new double[] { 0, 8 * len, 0 }))),
					add(kp, add(scale(t, off * 1.9), add(scale(kn, 2.6), new double[] { 0, 16 * len, 0 }))) };
			tail(knot, path, t, new double[] { 2.1, 1.9, 1.6 }, st.dark, st.dark, st.light);
		}
		finish(out, knot, 6, -1, skirt);

		// the guard on the left shoulder: a cap and two overlapping lames, riding the upper arm
		ModelData guard = part();
		DoublePredicate left = x -> x > 12;
		double armTop = extremeX(arms, true, 12);
		double[] lames = { armTop - 1.5, armTop + 5 };
		double span = Math.PI * 0.62;
		int N = 5;
		for (int l = 0; l < lames.length; l++) {
			double ya = lames[l], yb = ya + 8.5;
			Section sa = section(arms, ya + 1, left), sbb = section(arms, yb, left);
			int[] ta = new int[N + 1], tb2 = new int[N + 1];
			for (int i = 0; i <= N; i++) {
				double a = -span + 2 * span * i / N;
				ta[i] = v(guard, sa.at(a, 3.4 - l * 0.4, ya));
				tb2[i] = v(guard, sbb.at(a, 4.8 - l * 0.2, yb));
			}
			double[] ref = { (sa.cx + sbb.cx) / 2, (ya + yb) / 2, (sa.cz + sbb.cz) / 2 };
			for (int i = 0; i < N; i++) {
				int col = l == 0 ? st.leather : st.leatherDark;
				twoSided(guard, ta[i], ta[i + 1], tb2[i + 1], tb2[i], col, st.dark, ref);
			}
		}
		Section cap = section(arms, armTop + 3, left);
		double rx = Math.max(4, cap.r(0)), rz = Math.max(4, cap.r(Math.PI / 2));
		dome(guard, new double[] { cap.cx + 0.5, armTop + 1.5, cap.cz }, norm(new double[] { 0.35, -1, 0 }), new double[] { 0, 0, 1 }, rz + 3.6, rx + 3.2, 4.5,
				6, st.leather, st.light);
		finish(out, guard, 10, GUARD_LABEL);
		return out;
	}

	/* ====================================================================== */
	/*                                  LEGS                                  */
	/* ====================================================================== */

	/** The belt, the cross-bound leggings and the two-layered kilt of hanging panels. */
	static ModelData legs(ArmourForge af, Style st, int[] kit) throws IOException {
		ModelData legs = af.read(kit[LEGS]);
		int[] lb = legs.bounds();
		double top = lb[1], ankle = lb[4], hw = lb[3];
		double crotch = Math.max(Math.max(extreme(legs, 39, 1, true), extreme(legs, 40, 1, true)), Math.max(extreme(legs, 41, 1, true), extreme(legs, 42, 1, true)));
		double knee = (avgY(legs, 34) + avgY(legs, 35) + avgY(legs, 31) + avgY(legs, 37)) / 4;
		DoublePredicate all = x -> true;
		ModelData out = new ModelData();
		int legCol = st.light, legDark = st.leatherDark;

		// the seat of the leggings: rings round the hips down to the crotch, closed between the legs
		int K = 10;
		double[] prow = { top + 0.4, crotch + 0.6 };
		ModelData seat = part();
		double[][][] pp = new double[prow.length][K][];
		double[][] pc = new double[prow.length][];
		for (int r = 0; r < prow.length; r++) {
			Section s = section(legs, prow[r], all);
			pc[r] = new double[] { s.cx, prow[r], s.cz };
			for (int k = 0; k < K; k++) {
				pp[r][k] = s.at(-Math.PI / 2 + Math.PI * 2 * k / K, 1.0, prow[r]);
			}
		}
		int[][] ids = loft(seat, pp, pc, true, (r, k, c) -> legCol);
		int mid = v(seat, new double[] { pc[prow.length - 1][0], crotch + 1.5, pc[prow.length - 1][2] });
		for (int k = 0; k < K; k++) {
			seat.triOut(ids[prow.length - 1][k], ids[prow.length - 1][(k + 1) % K], mid, legCol, new double[] { 0, crotch - 10, pc[0][2] });
		}
		finish(out, seat, 1, -1, legs);

		// each leg: thigh, knee, shin to the ankle; then two thongs wound round it in opposite senses, crossing front and back
		double hemOuter = crotch + (knee - crotch) * 0.42, hemUnder = crotch + (knee - crotch) * 0.68;
		for (int side = -1; side <= 1; side += 2) {
			final int sd = side;
			DoublePredicate one = x -> x * sd > 0.5;
			double[] lrow = { crotch + 1, knee - 4, knee + 3, knee + (ankle - knee) * 0.45, ankle - 0.5 };
			int L = 6;
			double[][][] lp = new double[lrow.length][L][];
			double[][] lc = new double[lrow.length][];
			for (int r = 0; r < lrow.length; r++) {
				Section s = section(legs, lrow[r], one);
				lc[r] = new double[] { s.cx, lrow[r], s.cz };
				for (int k = 0; k < L; k++) {
					lp[r][k] = s.at(-Math.PI / 2 + Math.PI * 2 * k / L + Math.PI / L, 1.1, lrow[r]);
				}
			}
			ModelData leg = part();
			loft(leg, lp, lc, true, (r, k, c) -> r == 1 ? legDark : legCol);
			finishBy(out, leg, 1, legLabel(side, knee, ankle));

			ModelData thongs = part();
			// two turns each; they cross at the front just above the knee (between the kilt and the greave), behind at thigh and calf
			double y0 = hemOuter - 3, y1 = ankle - 3, turns = 2, t0 = (knee - 5 - y0) / (y1 - y0);
			int n = 12;
			for (int dirn = -1; dirn <= 1; dirn += 2) {
				int[] lo2 = new int[n + 1], hi2 = new int[n + 1];
				for (int i = 0; i <= n; i++) {
					double t = (double) i / n, y = y0 + (y1 - y0) * t, a = -Math.PI / 2 + dirn * Math.PI * 2 * turns * (t - t0);
					Section s = section(legs, y, one);
					double[] p = s.at(a, 2.1, y);
					lo2[i] = v(thongs, add(p, new double[] { 0, 1.0, 0 }));
					hi2[i] = v(thongs, add(p, new double[] { 0, -1.0, 0 }));
				}
				for (int i = 0; i < n; i++) {
					double y = y0 + (y1 - y0) * (i + 0.5) / n;
					Section s = section(legs, y, one);
					thongs.quadOut(lo2[i], lo2[i + 1], hi2[i + 1], hi2[i], legDark, new double[] { s.cx, y, s.cz });
				}
			}
			finishBy(out, thongs, 2, legLabel(side, knee, ankle));
		}

		// the kilt: twelve panels hung from under the belt, short outer ones and long under ones by turns, none across the
		// middle (each hangs on one thigh and swings with it: top on the waist, 30, below that the skirt half, 43 / 44);
		// a loincloth flap hangs straight down the middle in front and behind, between the thighs
		double py = top + 5;
		Section waist = section(legs, py, all);
		for (int layer = 0; layer < 2; layer++) {
			boolean outer = layer == 1;
			double hem = outer ? hemOuter : hemUnder, ym = py + (hem - py) * 0.86;
			Section sm = section(legs, ym, all), sh = section(legs, hem, all);
			double width = 0.235;
			for (int p = 0; p < 6; p++) {
				ModelData kilt = part();
				double ac = -Math.PI / 2 + Math.PI / 12 + Math.PI * 2 * p / 6 + (outer ? 0 : Math.PI / 6);
				double[] as = { ac - width, ac + width };
				int[][] g = new int[3][2];
				for (int e = 0; e < 2; e++) {
					g[0][e] = v(kilt, waist.at(as[e], outer ? 3.0 : 2.3, py));
					g[1][e] = v(kilt, sm.at(as[e], outer ? 4.4 : 3.0, ym));
					g[2][e] = v(kilt, sh.at(as[e], outer ? 5.2 : 3.8, hem));
				}
				int tip = v(kilt, sh.at(ac, outer ? 5.6 : 4.1, hem + (outer ? 4 : 3.5)));
				double[] ref1 = { waist.cx, (py + ym) / 2, waist.cz }, ref2 = { sh.cx, (ym + hem) / 2, sh.cz };
				if (outer) { // the under-panels lie behind: the outer ones need no back
					kilt.quadOut(g[0][0], g[0][1], g[1][1], g[1][0], st.main, ref1);
					kilt.quadOut(g[1][0], g[1][1], g[2][1], g[2][0], st.trim, ref2);
					kilt.triOut(g[2][0], g[2][1], tip, st.trim, ref2);
				} else {
					twoSided(kilt, g[0][0], g[0][1], g[1][1], g[1][0], st.dark, st.dark, ref1);
					twoSided(kilt, g[1][0], g[1][1], g[2][1], g[2][0], st.trimDark, st.trimDark, ref2);
					kilt.triOut(g[2][0], g[2][1], tip, st.trimDark, ref2);
					int[] f = kilt.faces.get(kilt.faces.size() - 1);
					kilt.tri(f[0], f[2], f[1], st.trimDark);
				}
				final double waistY = py;
				final int thigh = Math.cos(ac) > 0 ? 43 : 44;
				finishBy(out, kilt, outer ? 3 : 2, q -> q[1] <= waistY + 0.5 ? 30 : thigh);
			}
		}
		ModelData flaps = part();
		double fh = crotch + (knee - crotch) * 0.6;
		for (int fb = -1; fb <= 1; fb += 2) {
			double a = fb * Math.PI / 2;
			Section sc2 = section(legs, crotch - 2, all), sf = section(legs, fh, all);
			double[] t0 = waist.at(a, 2.6, py), t1 = sc2.at(a, 3.4, crotch - 2), t2 = sf.at(a, 3.2, fh);
			t2[2] = t1[2] + fb * 0.8; // hangs straight down from the hips, clear of the thighs
			double[][] path = { t0, t1, t2, add(t2, new double[] { 0, 3.5, fb * 0.3 }) };
			tail(flaps, path, new double[] { 1, 0, 0 }, new double[] { 2.6, 3.0, 3.2, 0.4 }, st.dark, st.dark, st.trim);
		}
		final double flapTop = py;
		finishBy(out, flaps, 3, p -> p[1] <= flapTop + 0.5 ? 30 : 41);

		// the belt, closed on top, with a buckle and a pouch at the right hip
		ModelData belt = part();
		double[] brow = { top + 0.3, top + 6.5 };
		double[][][] bp = new double[3][K][];
		double[][] bc = new double[3][];
		for (int r = 0; r < 3; r++) {
			double y = r == 0 ? brow[0] : brow[r - 1];
			Section s = section(legs, y, all);
			bc[r] = new double[] { s.cx, y, s.cz };
			for (int k = 0; k < K; k++) {
				bp[r][k] = s.at(-Math.PI / 2 + Math.PI * 2 * k / K, r == 0 ? 0.2 : 3.6, y);
			}
		}
		loft(belt, bp, bc, true, (r, k, c) -> st.leatherDark);
		ModelData bits = part();
		decal(bits, belt, ArmourSets.FRONT, rect(-2.8, top + 1, 2.8, top + 6), 0.4, 1.4, st.light, st.leatherDark);
		decal(bits, belt, ArmourSets.FRONT, rect(-2.8 + 1.4, top + 2.3, 2.8 - 1.4, top + 4.7), 0.3, 1.9, st.leatherDark, st.light);
		decal(bits, belt, ArmourSets.FRONT, rect(-hw * 0.78, top + 2, -hw * 0.42, top + 10), 0.4, 3.0, st.leather, st.leatherDark);
		belt = ArmourForge.mergeAll(belt, bits);
		finish(out, belt, 4, 30);
		return out;
	}

	/** Leg labels by height: the thigh turns with the hip (43 / 44), below the knee with the shin (36 / 33), then the ankle. */
	static java.util.function.ToIntFunction<int[]> legLabel(int side, double knee, double ankle) {
		return p -> p[1] < knee ? (side > 0 ? 43 : 44) : p[1] < ankle - 6 ? (side > 0 ? 36 : 33) : side > 0 ? 38 : 32;
	}

	/* ====================================================================== */
	/*                                 BOOTS                                  */
	/* ====================================================================== */

	/** Wrapped feet with bare toes on sandal soles, strapped at instep and ankle, and greaves from ankle to knee. */
	static ModelData boots(ArmourForge af, Style st, int[] kit) throws IOException {
		ModelData feet = af.read(kit[FEET]), legs = af.read(kit[LEGS]);
		int[] fb = feet.bounds();
		double ground = fb[4], toe = fb[2], heel = fb[5], ankleTop = fb[1];
		double knee = (avgY(legs, 34) + avgY(legs, 35) + avgY(legs, 31) + avgY(legs, 37)) / 4, legBottom = legs.bounds()[4];
		ModelData out = new ModelData();

		// the feet: the kit's own, wrapped close (so they keep its labels), the toes bare, lifted onto the soles
		ModelData foot = inflate(feet, 0.8);
		double lift = ground - 2.6;
		for (int[] p : foot.vertices) {
			p[1] = (int) Math.min(p[1], Math.round(lift));
		}
		for (int i = 0; i < foot.faces.size(); i++) {
			double[] c = centroid(foot, i);
			foot.colors.set(i, c[2] < toe + 5.5 ? SKIN : c[1] > lift - 4 ? st.leatherDark : st.light);
		}
		for (int i = 0; i < foot.vertexSkins.size(); i++) {
			foot.vertexSkins.set(i, safe(foot.vertexSkins.get(i)));
		}
		foot.priorities.replaceAll(x -> 2);
		foot.renderTypes.replaceAll(x -> 0);
		ArmourForge.merge(out, foot, null);

		for (int side = -1; side <= 1; side += 2) {
			final int sd = side;
			// the sole: the foot's footprint, a little wider, from the ground up
			ModelData sole = part();
			List<double[]> print = new ArrayList<>();
			for (int[] p : feet.vertices) {
				if (p[0] * side > 0 && p[1] > ground - 8) {
					print.add(new double[] { p[0], p[2] });
				}
			}
			double[][] hull = hull(print);
			double[] hc = mean(hull);
			int n = hull.length;
			int[] t = new int[n], b = new int[n];
			for (int i = 0; i < n; i++) {
				double[] d = sub2(hull[i], hc);
				double l = Math.hypot(d[0], d[1]) + 1e-9;
				double x = hull[i][0] + d[0] / l * 1.4, z = hull[i][1] + d[1] / l * 1.4;
				t[i] = v(sole, new double[] { x, lift - 0.2, z });
				b[i] = v(sole, new double[] { x, ground, z });
			}
			int tc = v(sole, new double[] { hc[0], lift - 0.2, hc[1] });
			double[] in = { hc[0], ground - 1.5, hc[1] };
			for (int i = 0; i < n; i++) {
				int j = (i + 1) % n;
				sole.triOut(t[i], t[j], tc, st.leatherDark, new double[] { hc[0], ground + 20, hc[1] });
				sole.quadOut(t[i], t[j], b[j], b[i], st.trimDark, in);
			}
			finishNearest(out, sole, 1, feet);

			// straps over the instep and round the ankle
			ModelData straps = part();
			double zi = toe + (heel - toe) * 0.45;
			double[] ib = box(feet, sd, 1, zi - 2.5, zi + 2.5);
			ring(straps, new double[] { (ib[0] + ib[3]) / 2, (ib[1] + ib[4]) / 2, zi }, new double[] { 0, 0, 1 }, new double[] { 1, 0, 0 },
					(ib[3] - ib[0]) / 2 + 1.5, (ib[4] - ib[1]) / 2 + 1.5, -1.2, 1.2, 6, st.leather);
			double ya = ankleTop + (ground - ankleTop) * 0.45;
			double[] ab = box(feet, sd, 0, ya - 2.5, ya + 2.5);
			ring(straps, new double[] { (ab[0] + ab[3]) / 2, ya, (ab[2] + ab[5]) / 2 }, new double[] { 0, -1, 0 }, new double[] { 1, 0, 0 },
					(ab[3] - ab[0]) / 2 + 1.6, (ab[5] - ab[2]) / 2 + 1.6, -1.2, 1.2, 6, st.leather);
			finishNearest(out, straps, 3, feet);

			// the greave: a plate round the front of the shin with a flared lip under the knee, strapped round the calf
			DoublePredicate one = x -> x * sd > 0.5;
			ModelData greave = part();
			double g0 = legBottom - 2, g1 = knee + 3;
			double[] gy = { g0, g0 + (g1 - g0) * 0.36, g0 + (g1 - g0) * 0.72, g1, g1 - 3 };
			double[] grow = { 2.3, 2.4, 2.6, 2.8, 4.0 };
			int G = 5;
			double span = Math.PI * 0.58;
			int[][] gid = new int[gy.length][G + 1];
			Section[] gs = new Section[gy.length];
			for (int r = 0; r < gy.length; r++) {
				gs[r] = section(legs, Math.min(gy[r], legBottom - 0.6), one);
				for (int i = 0; i <= G; i++) {
					gid[r][i] = v(greave, gs[r].at(-Math.PI / 2 - span + 2 * span * i / G, grow[r], gy[r]));
				}
			}
			for (int r = 0; r + 1 < gy.length; r++) {
				double[] ref = { (gs[r].cx + gs[r + 1].cx) / 2, (gy[r] + gy[r + 1]) / 2, (gs[r].cz + gs[r + 1].cz) / 2 };
				for (int i = 0; i < G; i++) {
					int col = r == gy.length - 2 ? st.light : st.leather;
					greave.quadOut(gid[r][i], gid[r][i + 1], gid[r + 1][i + 1], gid[r + 1][i], col, ref);
				}
			}
			for (double f : new double[] { 0.2, 0.62 }) {
				double y = g0 + (g1 - g0) * f;
				Section s = section(legs, Math.min(y, legBottom - 0.6), one);
				int[] lo = new int[G + 1], hi = new int[G + 1];
				for (int i = 0; i <= G; i++) {
					double a = Math.PI / 2 - (Math.PI - span) - 0.15 + 2 * (Math.PI - span + 0.15) * i / G;
					lo[i] = v(greave, s.at(a, 2.6, y + 1.1));
					hi[i] = v(greave, s.at(a, 2.6, y - 1.1));
				}
				for (int i = 0; i < G; i++) {
					greave.quadOut(lo[i], lo[i + 1], hi[i + 1], hi[i], st.leatherDark, new double[] { s.cx, y, s.cz });
				}
			}
			finish(out, greave, 3, side > 0 ? 36 : 33);
		}
		return out;
	}

	/* ====================================================================== */
	/*                                 GLOVES                                 */
	/* ====================================================================== */

	/** Long bracers from the wrist to the elbow, flared and cord-bound; the palms wrapped, the fingers bare. */
	static ModelData gloves(ArmourForge af, Style st, int[] kit) throws IOException {
		ModelData arms = af.read(kit[ARMS]), hands = af.read(kit[HANDS]);
		ModelData limb = ArmourForge.mergeAll(arms, hands);
		ModelData out = new ModelData();
		double handTop = Math.min(extreme(hands, 27, 1, false), extreme(hands, 28, 1, false));
		double handBot = Math.max(extreme(hands, 27, 1, true), extreme(hands, 28, 1, true));
		double elbow = (avgY(arms, 17) + avgY(arms, 23)) / 2;

		// the hands: the kit's own, wrapped close: the palm and back bound, the fingers bare
		ModelData hand = new ModelData();
		for (int i = 0; i < hands.faces.size(); i++) {
			int[] f = hands.faces.get(i);
			int a = hands.vertexSkins.get(f[0]), b = hands.vertexSkins.get(f[1]), c = hands.vertexSkins.get(f[2]);
			if ((a == 27 || a == 28) && (b == 27 || b == 28) && (c == 27 || c == 28)) {
				hand.faces.add(f.clone());
				hand.colors.add(0);
				hand.renderTypes.add(0);
				hand.priorities.add(10);
				hand.alphas.add(0);
				hand.faceSkins.add(0);
			}
		}
		for (int i = 0; i < hands.vertices.size(); i++) {
			hand.vertices.add(hands.vertices.get(i).clone());
			hand.vertexSkins.add(hands.vertexSkins.get(i));
		}
		hand = inflate(compact(hand), 0.6);
		for (int i = 0; i < hand.faces.size(); i++) {
			double[] c = centroid(hand, i);
			hand.colors.set(i, c[1] < handTop + (handBot - handTop) * 0.5 ? st.leatherDark : SKIN);
		}
		ArmourForge.merge(out, hand, null);

		for (int side = -1; side <= 1; side += 2) {
			final int sd = side;
			DoublePredicate one = x -> x * sd > 12;
			double[] ys = { handTop - 1, handTop - 1 + (elbow + 3 - handTop) * 0.33, handTop - 1 + (elbow + 3 - handTop) * 0.67, elbow + 3, elbow + 0.5 };
			double[] grow = { 1.1, 1.3, 1.5, 1.8, 3.0 };
			int K = 6;
			double[][][] p = new double[ys.length][K][];
			double[][] c = new double[ys.length][];
			for (int r = 0; r < ys.length; r++) {
				Section s = section(limb, ys[r], one);
				c[r] = new double[] { s.cx, ys[r], s.cz };
				for (int k = 0; k < K; k++) {
					p[r][k] = s.at(Math.PI * 2 * k / K + Math.PI / K, grow[r], ys[r]);
				}
			}
			ModelData bracer = part();
			loft(bracer, p, c, true, (r, k, cc) -> r == ys.length - 2 ? st.leatherDark : st.leather);
			// two cords round it
			for (double f : new double[] { 0.22, 0.6 }) {
				double y = ys[0] + (ys[3] - ys[0]) * f, gr = grow[0] + (grow[3] - grow[0]) * f + 0.8;
				double[][][] q = new double[2][K][];
				double[][] qc = new double[2][];
				for (int r = 0; r < 2; r++) {
					double yy = y + (r == 0 ? -1 : 1);
					Section s = section(limb, yy, one);
					qc[r] = new double[] { s.cx, yy, s.cz };
					for (int k = 0; k < K; k++) {
						q[r][k] = s.at(Math.PI * 2 * k / K, gr, yy);
					}
				}
				loft(bracer, q, qc, true, (r, k, cc) -> st.light);
			}
			finish(out, bracer, 10, side > 0 ? 22 : 19);
		}
		return out;
	}

	/* ====================================================================== */
	/*                     MEASURES (for the motif decorations)               */
	/* ====================================================================== */

	/** Face priorities the pieces are built with, so a decoration can measure one layer: leggings, greave, bracer. */
	static final int PRIO_LEGGINGS = 1, PRIO_GREAVE = 3, PRIO_BRACER = 10;

	/** The shoulder guard's label: the left upper arm's (turns with the shoulder, pivots nothing). */
	static final int GUARD_LABEL = 26;

	/** The vest's and the sash's face priorities. */
	static final int PRIO_VEST = 3, PRIO_SASH = 4;

	/**
	 * The trunk of a worn wrap body, {hw, top, bottom, front, back}: the vest and the sash only (not the bandolier, the
	 * shoulder guard or the sash ends).
	 */
	static double[] trunk(ModelData body) {
		double[] t = { 0, 1e9, -1e9, 1e9, -1e9 };
		for (int i = 0; i < body.faces.size(); i++) {
			int pr = body.priorities.get(i);
			if (pr != PRIO_VEST && pr != PRIO_SASH) {
				continue;
			}
			for (int v : body.faces.get(i)) {
				int[] p = body.vertices.get(v);
				t[0] = Math.max(t[0], Math.abs(p[0]));
				t[1] = Math.min(t[1], p[1]);
				t[2] = Math.max(t[2], p[1]);
				t[3] = Math.min(t[3], p[2]);
				t[4] = Math.max(t[4], p[2]);
			}
		}
		return t;
	}

	/** The bandolier's line on a worn wrap body, in the FRONT / BACK frames: {x0, y0, x1, y1} (left shoulder to right hip). */
	static double[] strapLine(ModelData body) {
		double[] t = trunk(body);
		double hw = t[0] - 3, h = t[2] - t[1];
		return new double[] { hw * 0.48, t[1] + 0.7, -hw, t[1] + h * 0.88 };
	}

	/** The shoulder guard's cap on a worn wrap body: {x, top y, z} (its centre across, its highest point). */
	static double[] guardCap(ModelData body) {
		double[] b = { 1e9, 1e9, 1e9, -1e9, -1e9, -1e9 };
		for (int i = 0; i < body.vertices.size(); i++) {
			if (body.vertexSkins.get(i) != GUARD_LABEL) {
				continue;
			}
			int[] p = body.vertices.get(i);
			for (int k = 0; k < 3; k++) {
				b[k] = Math.min(b[k], p[k]);
				b[k + 3] = Math.max(b[k + 3], p[k]);
			}
		}
		return new double[] { (b[0] + b[3]) / 2, b[1], (b[2] + b[5]) / 2 };
	}

	/** The section, at y, of one layer of a worn wrap piece (its faces of priority {@code prio}) on one side (0: both). */
	static Section layer(ModelData m, int prio, int side, double y) {
		ModelData f = new ModelData();
		for (int i = 0; i < m.faces.size(); i++) {
			if (m.priorities.get(i) == prio) {
				f.faces.add(m.faces.get(i));
			}
		}
		f.vertices.addAll(m.vertices);
		return section(f, y, side == 0 ? x -> true : x -> x * side > 0.5);
	}

	/** The top or bottom (y) of one layer of a worn wrap piece on one side. */
	static double layerY(ModelData m, int prio, int side, boolean bottom) {
		double e = bottom ? -1e9 : 1e9;
		for (int i = 0; i < m.faces.size(); i++) {
			if (m.priorities.get(i) != prio) {
				continue;
			}
			for (int v : m.faces.get(i)) {
				int[] p = m.vertices.get(v);
				if (side == 0 || p[0] * side > 0) {
					e = bottom ? Math.max(e, p[1]) : Math.min(e, p[1]);
				}
			}
		}
		return e;
	}

	/* ====================================================================== */
	/*                               LABELLING                                */
	/* ====================================================================== */

	/**
	 * A label that moves the same way as {@code l} in the player's frames but is not a joint's pivot, so new vertices
	 * can carry it without moving the joint.
	 */
	static int safe(int l) {
		switch (l) {
		case 2:
		case 94:
			return 8; // neck: with the chest
		case 3:
			return 1; // jaw: with the skull
		case 5:
		case 29:
			return 30; // waist: turns with the trunk
		case 35:
		case 40:
			return 43; // left thigh / hip: the left skirt half turns with the left hip
		case 34:
		case 42:
			return 44;
		case 37:
			return 36; // left shin: the left knee label turns with hip and knee
		case 31:
			return 33;
		case 23:
		case 25:
			return 26; // left upper arm: turns with the shoulder only
		case 17:
		case 21:
			return 20;
		case 47:
			return 38; // heel: with the ankle
		case 48:
			return 32;
		default:
			return l;
		}
	}

	/** The donor's vertices on pivot labels, kept without faces so every joint pivots where the stock piece has it. */
	static void anchors(ModelData m, ModelData donor) {
		for (int i = 0; i < donor.vertices.size(); i++) {
			if (PIVOTS.contains(donor.vertexSkins.get(i))) {
				m.vertices.add(donor.vertices.get(i).clone());
				m.vertexSkins.add(donor.vertexSkins.get(i));
			}
		}
	}

	/** A scratch part: smooth shaded. */
	static ModelData part() {
		ModelData m = new ModelData();
		m.shading = 0;
		return m;
	}

	/** Labels a part with one label (or, label < 0, by the nearest vertex of ref, made safe) and appends it. */
	static void finish(ModelData out, ModelData part, int prio, int label) {
		finish(out, part, prio, label, null);
	}

	static void finish(ModelData out, ModelData part, int prio, int label, ModelData ref) {
		for (int i = 0; i < part.vertices.size(); i++) {
			part.vertexSkins.set(i, label >= 0 ? label : safe(nearest(ref, part.vertices.get(i))));
		}
		part.priorities.replaceAll(x -> prio);
		ArmourForge.merge(out, part, null);
	}

	static void finishBy(ModelData out, ModelData part, int prio, java.util.function.ToIntFunction<int[]> label) {
		for (int i = 0; i < part.vertices.size(); i++) {
			part.vertexSkins.set(i, label.applyAsInt(part.vertices.get(i)));
		}
		part.priorities.replaceAll(x -> prio);
		ArmourForge.merge(out, part, null);
	}

	static void finishNearest(ModelData out, ModelData part, int prio, ModelData ref) {
		finish(out, part, prio, -1, ref);
	}

	/** The label of ref's vertex nearest p (never 0: the root, which stays on the ground while the feet move). */
	static int nearest(ModelData ref, int[] p) {
		long best = Long.MAX_VALUE;
		int l = 0;
		for (int j = 0; j < ref.vertices.size(); j++) {
			int[] o = ref.vertices.get(j);
			if (ref.vertexSkins.get(j) == 0) {
				continue;
			}
			long dx = p[0] - o[0], dy = p[1] - o[1], dz = p[2] - o[2], d = dx * dx + dy * dy + dz * dz;
			if (d < best) {
				best = d;
				l = ref.vertexSkins.get(j);
			}
		}
		return l;
	}

	/* ====================================================================== */
	/*                                GEOMETRY                                */
	/* ====================================================================== */

	/**
	 * A horizontal section of a body mesh: where its faces cross the plane y, as a convex outline (x, z) round its
	 * centre. {@link #at} gives the point on the outline in a direction (angle 0 = +x, the player's left; pi / 2 = +z,
	 * the back; -pi / 2 the front), pushed out by {@code grow}.
	 */
	static final class Section {
		final double cx, cz;
		final double[][] hull;

		Section(double[][] hull) {
			this.hull = hull;
			double x0 = 1e9, x1 = -1e9, z0 = 1e9, z1 = -1e9;
			for (double[] p : hull) {
				x0 = Math.min(x0, p[0]);
				x1 = Math.max(x1, p[0]);
				z0 = Math.min(z0, p[1]);
				z1 = Math.max(z1, p[1]);
			}
			cx = (x0 + x1) / 2;
			cz = (z0 + z1) / 2;
		}

		/** Distance from the centre to the outline along angle a. */
		double r(double a) {
			double dx = Math.cos(a), dz = Math.sin(a), best = -1;
			int n = hull.length;
			for (int i = 0; i < n; i++) {
				double[] p = hull[i], q = hull[(i + 1) % n];
				double ex = q[0] - p[0], ez = q[1] - p[1];
				double den = dx * ez - dz * ex;
				if (Math.abs(den) < 1e-9) {
					continue;
				}
				double px = p[0] - cx, pz = p[1] - cz;
				double t = (px * ez - pz * ex) / den, s = (px * dz - pz * dx) / den;
				if (t > 0 && s >= -1e-6 && s <= 1 + 1e-6) {
					best = Math.max(best, t);
				}
			}
			return best < 0 ? 2 : best;
		}

		double[] at(double a, double grow, double y) {
			double r = r(a) + grow;
			return new double[] { cx + Math.cos(a) * r, y, cz + Math.sin(a) * r };
		}
	}

	/** The section of {@code ref} at y, of the crossing points whose x passes {@code keep} (nudged up or down if empty). */
	static Section section(ModelData ref, double y, DoublePredicate keep) {
		for (int tries = 0; tries < 24; tries++) {
			double yy = y + 0.137 + (tries % 2 == 0 ? 1 : -1) * (tries / 2) * 0.75;
			List<double[]> pts = new ArrayList<>();
			for (int[] f : ref.faces) {
				for (int e = 0; e < 3; e++) {
					int[] a = ref.vertices.get(f[e]), b = ref.vertices.get(f[(e + 1) % 3]);
					if ((a[1] - yy) * (b[1] - yy) < 0) {
						double t = (yy - a[1]) / (b[1] - a[1]);
						double x = a[0] + (b[0] - a[0]) * t, z = a[2] + (b[2] - a[2]) * t;
						if (keep.test(x)) {
							pts.add(new double[] { x, z });
						}
					}
				}
			}
			double[][] h = hull(pts);
			if (h.length >= 3) {
				return new Section(h);
			}
		}
		return new Section(new double[][] { { -3, -3 }, { 3, -3 }, { 3, 3 }, { -3, 3 } });
	}

	/** Convex hull (monotone chain), counter-clockwise. */
	static double[][] hull(List<double[]> pts) {
		double[][] p = pts.toArray(new double[0][]);
		Arrays.sort(p, (a, b) -> a[0] != b[0] ? Double.compare(a[0], b[0]) : Double.compare(a[1], b[1]));
		int n = p.length;
		if (n < 3) {
			return p;
		}
		double[][] h = new double[2 * n][];
		int k = 0;
		for (int i = 0; i < n; i++) {
			while (k >= 2 && cross(h[k - 2], h[k - 1], p[i]) <= 0) {
				k--;
			}
			h[k++] = p[i];
		}
		for (int i = n - 2, t = k + 1; i >= 0; i--) {
			while (k >= t && cross(h[k - 2], h[k - 1], p[i]) <= 0) {
				k--;
			}
			h[k++] = p[i];
		}
		return Arrays.copyOf(h, Math.max(0, k - 1));
	}

	static double cross(double[] o, double[] a, double[] b) {
		return (a[0] - o[0]) * (b[1] - o[1]) - (a[1] - o[1]) * (b[0] - o[0]);
	}

	interface TriColour {
		int at(int row, int col, double[] centroid);
	}

	/** Rings of points joined into bands (closed round each ring); faces point away from each band's centre line. */
	static int[][] loft(ModelData m, double[][][] pts, double[][] centres, boolean closed, TriColour colour) {
		int rows = pts.length, cols = pts[0].length;
		int[][] id = new int[rows][cols];
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				id[r][c] = v(m, pts[r][c]);
			}
		}
		for (int r = 0; r + 1 < rows; r++) {
			double[] ref = scale(add(centres[r], centres[r + 1]), 0.5);
			for (int c = 0; c < (closed ? cols : cols - 1); c++) {
				int c2 = (c + 1) % cols;
				int a = id[r][c], b = id[r][c2], cc = id[r + 1][c2], d = id[r + 1][c];
				m.triOut(a, b, cc, colour.at(r, c, centroid(m, a, b, cc)), ref);
				m.triOut(a, cc, d, colour.at(r, c, centroid(m, a, cc, d)), ref);
			}
		}
		return id;
	}

	/** A quad facing away from ref in {@code out}, and its back in {@code back}. */
	static void twoSided(ModelData m, int a, int b, int c, int d, int out, int back, double[] ref) {
		m.quadOut(a, b, c, d, out, ref);
		int n = m.faces.size();
		for (int i = n - 2; i < n; i++) {
			int[] f = m.faces.get(i);
			m.tri(f[0], f[2], f[1], back);
		}
	}

	/** A hanging strip (both sides) along a path, its width along {@code across} (half-widths per point), the tip in tipCol. */
	static void tail(ModelData m, double[][] path, double[] across, double[] half, int col, int back, int tipCol) {
		int n = path.length;
		int[] l = new int[n], r = new int[n];
		for (int i = 0; i < n; i++) {
			l[i] = v(m, add(path[i], scale(across, -half[i])));
			r[i] = v(m, add(path[i], scale(across, half[i])));
		}
		for (int i = 0; i + 1 < n; i++) {
			double[] d = sub(path[i + 1], path[i]);
			double[] nrm = norm(ModelData.cross(d, across));
			double[] ref = sub(scale(add(path[i], path[i + 1]), 0.5), scale(nrm, 10));
			twoSided(m, l[i], r[i], r[i + 1], l[i + 1], i == n - 2 ? tipCol : col, back, ref);
		}
	}

	/** A flat band laid on a surface along a path given in a frame (projected along -n), {@code out} proud of it, with edges. */
	static void ribbon(ModelData m, ModelData onto, Frame f, double[][] path, double half, double out, int col, int side) {
		int n = path.length;
		int[] l = new int[n], r = new int[n], ll = new int[n], rr = new int[n];
		double[][] pl = new double[n][], pr = new double[n][];
		for (int i = 0; i < n; i++) {
			double[] d = sub2(path[Math.min(i + 1, n - 1)], path[Math.max(i - 1, 0)]);
			double dl = Math.hypot(d[0], d[1]) + 1e-9;
			double ax = -d[1] / dl * half, ay = d[0] / dl * half;
			pl[i] = hit(onto, f, path[i][0] + ax, path[i][1] + ay, i == 0 ? null : pl[i - 1]);
			pr[i] = hit(onto, f, path[i][0] - ax, path[i][1] - ay, i == 0 ? null : pr[i - 1]);
			l[i] = v(m, add(pl[i], scale(f.n, out)));
			r[i] = v(m, add(pr[i], scale(f.n, out)));
			if (side >= 0) {
				ll[i] = v(m, add(pl[i], scale(f.n, -0.3)));
				rr[i] = v(m, add(pr[i], scale(f.n, -0.3)));
			}
		}
		for (int i = 0; i + 1 < n; i++) {
			double[] ml = scale(add(pl[i], pl[i + 1]), 0.5), mr = scale(add(pr[i], pr[i + 1]), 0.5);
			m.quadOut(l[i], l[i + 1], r[i + 1], r[i], col, sub(scale(add(ml, mr), 0.5), scale(f.n, 12)));
			if (side >= 0) {
				m.quadOut(ll[i], ll[i + 1], l[i + 1], l[i], side, mr);
				m.quadOut(rr[i], rr[i + 1], r[i + 1], r[i], side, ml);
			}
		}
	}

	/**
	 * Frame point (a, b) dropped onto the surface along -n; where the ray misses (off the edge), at the depth of the
	 * previous point (or of the surface's nearest vertex), never left floating in the frame's plane.
	 */
	static double[] hit(ModelData onto, Frame f, double a, double b, double[] prev) {
		double[] p = f.at(a, b);
		double[] h = ArmourForge.surface(onto, add(p, scale(f.n, 150)), scale(f.n, -1));
		if (h != null) {
			return h;
		}
		double depth;
		if (prev != null) {
			depth = ArmourForge.dot(sub(prev, f.o), f.n);
		} else {
			double best = 1e18;
			depth = 0;
			for (int[] q : onto.vertices) {
				double[] d = sub(ArmourForge.d(q), p);
				double along = ArmourForge.dot(d, f.n), off = ArmourForge.dot(d, d) - along * along;
				if (off < best) {
					best = off;
					depth = ArmourForge.dot(sub(ArmourForge.d(q), f.o), f.n);
				}
			}
		}
		return add(p, scale(f.n, depth - ArmourForge.dot(sub(p, f.o), f.n)));
	}

	/** A rectangle centred on (x, y) in a frame, its length along dir and its width along across. */
	static double[][] turned(double x, double y, double[] dir, double[] across, double halfLen, double halfWid) {
		double[][] o = new double[4][];
		int[][] s = { { -1, -1 }, { 1, -1 }, { 1, 1 }, { -1, 1 } };
		for (int i = 0; i < 4; i++) {
			o[i] = new double[] { x + dir[0] * halfLen * s[i][0] + across[0] * halfWid * s[i][1], y + dir[1] * halfLen * s[i][0] + across[1] * halfWid * s[i][1] };
		}
		return o;
	}

	/** A copy of m with every vertex pushed {@code d} out along its normal (faces assumed wound consistently). */
	static ModelData inflate(ModelData m, double d) {
		ModelData o = ArmourForge.copyModel(m);
		int n = m.vertices.size();
		double[][] nrm = new double[n][3];
		// a pair (hands, feet) is two bodies: measure the winding against each side's own centre
		double[][] mc = new double[2][3];
		int[] cnt = new int[2];
		for (int[] p : m.vertices) {
			int s = p[0] >= 0 ? 1 : 0;
			mc[s] = add(mc[s], ArmourForge.d(p));
			cnt[s]++;
		}
		for (int s = 0; s < 2; s++) {
			mc[s] = scale(mc[s], 1.0 / Math.max(1, cnt[s]));
		}
		double sign = 0;
		for (int i = 0; i < m.faces.size(); i++) {
			int[] f = m.faces.get(i);
			double[] a = ArmourForge.d(m.vertices.get(f[0]));
			double[] fn = ModelData.cross(sub(ArmourForge.d(m.vertices.get(f[1])), a), sub(ArmourForge.d(m.vertices.get(f[2])), a));
			double[] c = centroid(m, i);
			sign += ArmourForge.dot(fn, sub(c, mc[c[0] >= 0 ? 1 : 0]));
			for (int k : f) {
				nrm[k] = add(nrm[k], fn);
			}
		}
		double s = sign < 0 ? -1 : 1;
		for (int i = 0; i < n; i++) {
			double l = ArmourForge.len(nrm[i]);
			if (l < 1e-9) {
				continue;
			}
			int[] p = o.vertices.get(i);
			double[] q = add(ArmourForge.d(m.vertices.get(i)), scale(nrm[i], s * d / l));
			p[0] = (int) Math.round(q[0]);
			p[1] = (int) Math.round(q[1]);
			p[2] = (int) Math.round(q[2]);
		}
		return o;
	}

	/** Drops vertices no face uses. */
	static ModelData compact(ModelData m) {
		int[] map = new int[m.vertices.size()];
		Arrays.fill(map, -1);
		ModelData o = new ModelData();
		for (int i = 0; i < m.faces.size(); i++) {
			int[] f = m.faces.get(i);
			int[] g = new int[3];
			for (int k = 0; k < 3; k++) {
				if (map[f[k]] < 0) {
					map[f[k]] = o.vertices.size();
					o.vertices.add(m.vertices.get(f[k]).clone());
					o.vertexSkins.add(m.vertexSkins.get(f[k]));
				}
				g[k] = map[f[k]];
			}
			o.faces.add(g);
			o.colors.add(m.colors.get(i));
			o.renderTypes.add(m.renderTypes.get(i));
			o.priorities.add(m.priorities.get(i));
			o.alphas.add(m.alphas.get(i));
			o.faceSkins.add(m.faceSkins.get(i));
		}
		return o;
	}

	static int v(ModelData m, double[] p) {
		return m.vertex(p[0], p[1], p[2]);
	}

	static double[] centroid(ModelData m, int face) {
		int[] f = m.faces.get(face);
		return centroid(m, f[0], f[1], f[2]);
	}

	static double[] centroid(ModelData m, int a, int b, int c) {
		int[] p = m.vertices.get(a), q = m.vertices.get(b), r = m.vertices.get(c);
		return new double[] { (p[0] + q[0] + r[0]) / 3.0, (p[1] + q[1] + r[1]) / 3.0, (p[2] + q[2] + r[2]) / 3.0 };
	}

	static double[] mean(double[][] p) {
		double[] o = new double[2];
		for (double[] q : p) {
			o[0] += q[0] / p.length;
			o[1] += q[1] / p.length;
		}
		return o;
	}

	static double[] sub2(double[] a, double[] b) {
		return new double[] { a[0] - b[0], a[1] - b[1] };
	}

	/** The largest (max) or smallest coordinate {@code axis} of the vertices labelled l (NaN if none). */
	static double extreme(ModelData m, int l, int axis, boolean max) {
		double e = max ? -1e9 : 1e9;
		for (int i = 0; i < m.vertices.size(); i++) {
			if (m.vertexSkins.get(i) == l) {
				e = max ? Math.max(e, m.vertices.get(i)[axis]) : Math.min(e, m.vertices.get(i)[axis]);
			}
		}
		return e;
	}

	/** The top (smallest y) of the vertices on one side (left if {@code left}) beyond |x| > minX. */
	static double extremeX(ModelData m, boolean left, double minX) {
		double e = 1e9;
		for (int[] p : m.vertices) {
			if ((left ? p[0] : -p[0]) > minX) {
				e = Math.min(e, p[1]);
			}
		}
		return e;
	}

	static double avgY(ModelData m, int l) {
		double s = 0;
		int n = 0;
		for (int i = 0; i < m.vertices.size(); i++) {
			if (m.vertexSkins.get(i) == l) {
				s += m.vertices.get(i)[1];
				n++;
			}
		}
		return n == 0 ? 0 : s / n;
	}

	/** Bounds {x0, y0, z0, x1, y1, z1} of one side's vertices with coordinate {@code axis} (0 = y, 1 = z) between lo and hi. */
	static double[] box(ModelData m, int side, int axis, double lo, double hi) {
		double[] b = { 1e9, 1e9, 1e9, -1e9, -1e9, -1e9 };
		for (int[] p : m.vertices) {
			double c = axis == 0 ? p[1] : p[2];
			if (p[0] * side > 0 && c >= lo && c <= hi) {
				for (int k = 0; k < 3; k++) {
					b[k] = Math.min(b[k], p[k]);
					b[k + 3] = Math.max(b[k + 3], p[k]);
				}
			}
		}
		if (b[0] > b[3]) {
			return new double[] { side * 9 - 5, lo, -5, side * 9 + 5, hi, 5 };
		}
		return b;
	}
}
