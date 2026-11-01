package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ArmourForge.add;
import static com.rs2.tools.mapgen.ArmourForge.norm;
import static com.rs2.tools.mapgen.ArmourForge.scale;
import static com.rs2.tools.mapgen.ArmourForge.sub;
import static com.rs2.tools.mapgen.BaseJellyfish.limb;
import static com.rs2.tools.mapgen.BaseJellyfish.pt;
import static com.rs2.tools.mapgen.BaseJellyfish.quad;
import static com.rs2.tools.mapgen.BaseJellyfish.sheet;
import static com.rs2.tools.mapgen.BaseJellyfish.tri;
import static com.rs2.tools.mapgen.BaseMantle.ANKLE;
import static com.rs2.tools.mapgen.BaseMantle.BELLY;
import static com.rs2.tools.mapgen.BaseMantle.CALF;
import static com.rs2.tools.mapgen.BaseMantle.CAPE_HIP;
import static com.rs2.tools.mapgen.BaseMantle.CAPE_KNEE;
import static com.rs2.tools.mapgen.BaseMantle.CAPE_THIGH;
import static com.rs2.tools.mapgen.BaseMantle.CAPE_TOP;
import static com.rs2.tools.mapgen.BaseMantle.CAPE_WAIST;
import static com.rs2.tools.mapgen.BaseMantle.CHEST;
import static com.rs2.tools.mapgen.BaseMantle.FOREARM;
import static com.rs2.tools.mapgen.BaseMantle.HAND;
import static com.rs2.tools.mapgen.BaseMantle.HEEL;
import static com.rs2.tools.mapgen.BaseMantle.NECK;
import static com.rs2.tools.mapgen.BaseMantle.PELVIS;
import static com.rs2.tools.mapgen.BaseMantle.SKULL;
import static com.rs2.tools.mapgen.BaseMantle.THIGH;
import static com.rs2.tools.mapgen.BaseMantle.TOES;
import static com.rs2.tools.mapgen.BaseMantle.WAIST;
import static com.rs2.tools.mapgen.BaseMantle.ell;
import static com.rs2.tools.mapgen.BaseMantle.lerp;
import static com.rs2.tools.mapgen.BaseMantle.sx;
import static com.rs2.tools.mapgen.ModelData.hsl;

import java.io.IOException;

import com.rs2.tools.mapgen.ArmourSets.Style;
import com.rs2.tools.mapgen.BaseMantle.Body;
import com.rs2.tools.mapgen.BaseMantle.Mesh;

/**
 * The Shardhide base (A29, WRAP): the whole pelt of a Shardback, the crater-rim bear with the fallen star's glass
 * growing out of its back, worn by the hunters who killed it. Built from scratch (see NewBases) round the
 * identity-kit body each piece is worn on, in the Shardback's own colours (its hide, its violet glass, its pale tusks).
 *
 * <ul>
 * <li>Bear-head: the bear's scalp pulled over the head as a hood, its upper jaw and snout jutting out over the
 * wearer's brow, a row of teeth along the jaw framing the face down to the long canines, dark eye sockets, round ears,
 * three shards up the crown, and the pelt hanging to the jaw at the sides and the nape behind. Named "bearhead", so it
 * hides nothing: the wearer's face looks out of the bear's mouth.</li>
 * <li>Pelt: a hide jerkin under a heavy hunched mantle of pelt that doubles the shoulders, ragged at its edge, open
 * at the front; the bear's forelegs come over the shoulders and hang down the chest, the paws and their claws resting
 * on the breast; the hide falls behind to the knees, a dark stripe down the spine; pale violet glass shards stand up
 * out of the pelt along both shoulders and down the spine. Named "pelt", so the arms kit stays: the arms are bare.</li>
 * <li>Leggings: hide leggings on a belt, wound with three shaggy fur wraps a leg and cross-bound with thongs.</li>
 * <li>Paw boots: a shaggy cuff at mid-shin over a broad bear's paw with toe pads and four pale claws.</li>
 * <li>Paws (gloves): a shaggy fur cuff on the forearm, a fat paw over the hand with four claws curling from it and a
 * sliver of star-glass at the wrist.</li>
 * </ul>
 *
 * <p>Labels (BaseMantle's rules). The bear-head is all on the skull 1. The jerkin's neck ring takes the neck 2; the
 * mantle, the forelegs and the shoulder shards are rigid on the chest 8 (so the glass never parts from the pelt); the
 * hide down the back and its shards hang on the stock capes' chain at the stock cape's heights (10 / 9 / 16 / 14 / 13),
 * so it swings back clear of the legs. The leggings are BaseMantle's leg rings (the hip ring's outside 40 / 42, the
 * knee 34 / 35 and shin 31 / 37 rings round their joints, so the pivots stay put; the thigh 43 / 44, calf 82 / 83 and
 * ankle 32 / 38 between); a wrap takes the label of the leg ring under it, made safe. The boots: the shaft the ankles
 * 32 / 38, the heel ring 47 / 48 round the heel (the toes' pivot), the paw and claws the toes 45 / 46. The paws: the
 * cuff the forearms 19 / 22, the paw round the hand 27 / 28 (the wrist's pivot).
 */
final class BasePelt {

	private BasePelt() {
	}

	/**
	 * The Shardback's colours: its hide's hue, a little lighter than on the beast so the pelt reads on a player; its
	 * glass, mouth and eyes as they are; bone-pale teeth and claws.
	 */
	static final int HIDE = hsl(5, 2, 50), HIDE_2 = hsl(5, 2, 44), HIDE_TOP = hsl(5, 2, 35), HIDE_DARK = hsl(5, 2, 25), BELLY_C = Shardback.BELLY,
			PAW = hsl(5, 1, 27), LEG = hsl(5, 2, 42), LEG_DARK = hsl(5, 2, 32), SHARD = Shardback.SHARD_C, SHARD_LIGHT = Shardback.SHARD_LIGHT,
			SHARD_DARK = Shardback.SHARD_DARK, TUSK = DraftArmourA.BONE_PALE, MOUTH = Shardback.MOUTH, MOUTH_DARK = Shardback.MOUTH_DARK,
			NOSE = hsl(5, 1, 10), EYE = Shardback.EYE;

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		Body b = BaseMantle.body(af, female);
		switch (slot) {
		case ArmourSets.HELM:
			return new ModelData[] { bearhead(st, b), null };
		case ArmourSets.BODY:
			return new ModelData[] { pelt(st, b), null };
		case ArmourSets.LEGS:
			return new ModelData[] { leggings(st, b), null };
		case ArmourSets.BOOTS:
			return new ModelData[] { pawBoots(st, b), null };
		case ArmourSets.GLOVES:
			return new ModelData[] { paws(st, b), null };
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

	/** A shard of star-glass: a four-sided spike from {@code base} along {@code dir}, its facets in light and dark glass. */
	static void shard(Mesh me, double[] base, double[] dir, double len, double r, int label, boolean bright) {
		double[] tip = add(base, scale(norm(dir), len));
		double[][] uv = BaseJellyfish.basis(dir);
		int t = me.v(tip, label);
		int[] rg = new int[4];
		for (int i = 0; i < 4; i++) {
			double a = Math.PI / 2 * i + 0.4;
			double rr = r * (i % 2 == 0 ? 1 : 0.7);
			rg[i] = me.v(add(base, add(scale(uv[0], Math.cos(a) * rr), scale(uv[1], Math.sin(a) * rr))), label);
		}
		double[] in = lerp(base, tip, 0.25);
		int[] cols = bright ? new int[] { SHARD_LIGHT, SHARD, SHARD_LIGHT, SHARD_DARK } : new int[] { SHARD, SHARD_DARK, SHARD_LIGHT, SHARD_DARK };
		for (int i = 0; i < 4; i++) {
			tri(me, rg[i], rg[(i + 1) % 4], t, cols[i], in);
		}
	}

	/** A claw or tooth: a three-sided curved spike from {@code base}, first along {@code d0} then bending to {@code d1}. */
	static void claw(Mesh me, double[] base, double[] d0, double[] d1, double len, double r, int label, int col) {
		double[] mid = add(base, scale(norm(d0), len * 0.55));
		double[] tip = add(mid, scale(norm(d1), len * 0.5));
		limb(me, base, mid, r, r * 0.6, 3, 0.5, label, label, col);
		limb(me, mid, tip, r * 0.6, 0, 3, 0.5, label, label, col);
	}

	/**
	 * A shaggy fur collar round a limb: a ring at {@code y} (round centre c, radii rx / rz) whose lower edge hangs in
	 * tufts, {@code drop} long, flaring out by {@code flare}.
	 */
	static void ruff(Mesh me, double[] c, double y, double rx, double rz, double drop, double flare, int n, int label, int col, int col2) {
		int[] top = new int[n], low = new int[n * 2];
		for (int i = 0; i < n; i++) {
			double th = 360.0 * i / n;
			top[i] = me.v(ell(c[0], y, c[2], rx, rz, rz, th), label);
			low[i * 2] = me.v(ell(c[0], y + drop, c[2], rx + flare, rz + flare, rz + flare, th + 180.0 / n * 0.2), label);
			low[i * 2 + 1] = me.v(ell(c[0], y + drop * 0.45, c[2], rx + flare * 0.8, rz + flare * 0.8, rz + flare * 0.8, th + 180.0 / n), label);
		}
		double[] ax = pt(c[0], y + drop / 2, c[2]);
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			tri(me, top[i], low[i * 2], low[i * 2 + 1], i % 2 == 0 ? col : col2, ax);
			tri(me, top[i], low[i * 2 + 1], top[j], i % 2 == 0 ? col : col2, ax);
			tri(me, top[j], low[i * 2 + 1], low[j * 2], i % 2 == 0 ? col2 : col, ax);
		}
	}

	/* ====================================================================== */
	/*                               THE BEAR-HEAD                            */
	/* ====================================================================== */

	/**
	 * The scalp over the head (rings from the crown down to an edge that runs along the jaw line: the brow in front,
	 * the jaw at the sides, the nape behind), the snout jutting from the brow, the teeth along the edge, eye sockets,
	 * ears and three shards. All on the skull label.
	 */
	static ModelData bearhead(Style st, Body b) {
		Mesh me = new Mesh();
		double T = b.headTop, F = b.headFront, B = b.headBack, s = (b.headChin - b.headTop) / 24.0, hw = b.headHW;
		double cz = (F + B) / 2;
		int n = 10;
		int[] ra = new int[n], rb = new int[n], rc = new int[n];
		double[] ey = new double[n];
		for (int i = 0; i < n; i++) {
			double th = 360.0 * i / n, c = Math.cos(Math.toRadians(th));
			ra[i] = me.v(ell(0, T - 5 * s, cz + 1, (hw + 4) * 0.62, (cz - F + 4) * 0.62, (B - cz + 5) * 0.62, th), SKULL);
			rb[i] = me.v(ell(0, T + 1.5 * s, cz, hw + 4.5, cz - F + 4.5, B - cz + 5.5, th), SKULL);
			double e = 6.5 + 19.5 * Math.pow((1 - c) / 2, 0.6);
			ey[i] = T + e * s + (i % 2 == 1 && Math.abs(c) < 0.9 ? 2.5 * s : 0);
			rc[i] = me.v(ell(0, ey[i], cz, hw + 5, cz - F + 5, B - cz + 2.5, th), SKULL);
		}
		int apex = me.v(pt(0, T - 8 * s, cz + 2), SKULL);
		double[] mid = pt(0, T + 6 * s, cz);
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			boolean spine = i == 4 || i == 5;
			tri(me, apex, ra[i], ra[j], HIDE_TOP, mid);
			quad(me, ra[i], ra[j], rb[j], rb[i], spine || i == 0 || i == 9 ? HIDE_TOP : HIDE, mid);
			quad(me, rb[i], rb[j], rc[j], rc[i], i % 2 == 0 ? HIDE : HIDE_2, mid);
			// the inside of the pelt, seen beside the face
			quad(me, rb[i], rc[i], rc[j], rb[j], HIDE_DARK, add(mid, scale(sub(me.p(rc[i]), mid), 3)));
		}
		// the snout: rings across it (x, y) from the brow out, the palate dark red underneath, the nose at the tip
		double[][] sn = { { F - 3, T + 2.5 * s, 6.5, 4.6 }, { F - 9 * s, T + 3.5 * s, 5.6, 4.1 }, { F - 15 * s, T + 5 * s, 4.4, 3.3 }, { F - 19 * s, T + 6 * s, 3.2, 2.7 } };
		int sn8 = 8;
		int[][] g = new int[sn.length][sn8];
		for (int k = 0; k < sn.length; k++) {
			for (int i = 0; i < sn8; i++) {
				double ph = Math.PI * 2 * i / sn8;
				g[k][i] = me.v(pt(Math.sin(ph) * sn[k][2], sn[k][1] - Math.cos(ph) * sn[k][3], sn[k][0]), SKULL);
			}
		}
		for (int k = 0; k + 1 < sn.length; k++) {
			double[] ax = pt(0, (sn[k][1] + sn[k + 1][1]) / 2, (sn[k][0] + sn[k + 1][0]) / 2);
			for (int i = 0; i < sn8; i++) {
				int j = (i + 1) % sn8;
				int col = i == 3 || i == 4 ? MOUTH : i == 0 || i == 7 ? HIDE_TOP : HIDE;
				quad(me, g[k][i], g[k][j], g[k + 1][j], g[k + 1][i], col, ax);
			}
		}
		double[] tip = sn[sn.length - 1];
		int nose = me.v(pt(0, tip[1] - 0.6, tip[0] - 2), SKULL);
		for (int i = 0; i < sn8; i++) {
			tri(me, g[sn.length - 1][i], g[sn.length - 1][(i + 1) % sn8], nose, i == 3 || i == 4 ? MOUTH : NOSE, pt(0, tip[1], tip[0] + 4));
		}
		// the teeth: along the scalp's edge either side of the snout, and the long canines under it
		me.prio = 2;
		for (double th : new double[] { 26, 46, 66, 294, 314, 334 }) {
			double c = Math.cos(Math.toRadians(th));
			double e = T + (6.5 + 19.5 * Math.pow((1 - c) / 2, 0.6)) * s;
			double[] p = ell(0, e - 0.5, cz, hw + 4.4, cz - F + 4.4, B - cz + 2, th);
			double[] in = norm(pt(-p[0], 0, cz - p[2]));
			claw(me, p, pt(in[0] * 0.3, 1, in[2] * 0.3), pt(in[0] * 0.6, 1, in[2] * 0.6), 4 * s, 1.1, SKULL, TUSK);
		}
		for (int sd = -1; sd <= 1; sd += 2) {
			double[] p = pt(sd * 3.0, sn[2][1] + sn[2][3] * 0.7, sn[2][0] + 1);
			claw(me, p, pt(0, 1, 0.1), pt(-sd * 0.2, 1, 0.5), 6.5 * s, 1.3, SKULL, TUSK);
			// the eye sockets on the brow, either side of the snout, a glint in each
			double ex = sd * 6.2, eyY = T + 0.5 * s, ez = F - 3.4;
			int a = me.v(pt(ex - sd * 2.4, eyY, ez), SKULL), b1 = me.v(pt(ex, eyY - 1.8, ez - 0.4), SKULL), c1 = me.v(pt(ex + sd * 2.2, eyY + 0.4, ez + 0.6), SKULL),
					d1 = me.v(pt(ex, eyY + 1.8, ez - 0.6), SKULL);
			quad(me, a, b1, c1, d1, MOUTH_DARK, pt(ex, eyY, ez + 10));
			int gl = me.v(pt(ex - sd * 0.4, eyY - 0.2, ez - 0.9), SKULL), g2 = me.v(pt(ex + sd * 0.7, eyY - 0.6, ez - 0.7), SKULL),
					g3 = me.v(pt(ex + sd * 0.3, eyY + 0.7, ez - 0.9), SKULL);
			tri(me, gl, g2, g3, EYE, pt(ex, eyY, ez + 10));
			// a round ear, standing up off the crown
			double[] e0 = ell(0, T - 3 * s, cz, hw + 2.5, cz - F + 2.5, B - cz + 3, sd > 0 ? 62 : 298), e1 = ell(0, T - 1.5 * s, cz, hw + 3.5, cz - F + 3.5, B - cz + 4, sd > 0 ? 100 : 260);
			double[] et = add(lerp(e0, e1, 0.5), pt(sd * 2.2, -6 * s, 0.5));
			int ia = me.v(e0, SKULL), ib = me.v(e1, SKULL), ic = me.v(et, SKULL), id = me.v(add(lerp(e0, et, 0.5), pt(sd * 0.5, 0, -1.2)), SKULL),
					ie = me.v(add(lerp(e1, et, 0.5), pt(sd * 0.8, 0, 1.2)), SKULL);
			double[] fwd = norm(pt(sd * 0.25, -0.1, -1));
			sheet(me, ia, id, ie, ib, BELLY_C, HIDE_DARK, fwd);
			sheet(me, id, ic, ic, ie, BELLY_C, HIDE_DARK, fwd);
		}
		// three shards up the crown and down the back of the head
		shard(me, pt(0, T - 6 * s, cz + 1), pt(0, -1, 0.45), 8 * s, 1.9, SKULL, true);
		shard(me, pt(0, T - 2 * s, cz + 8), pt(0.1, -0.7, 1), 9 * s, 2.0, SKULL, false);
		shard(me, pt(0, T + 6 * s, B + 3.5), pt(-0.1, -0.35, 1), 6.5 * s, 1.6, SKULL, true);
		me.prio = 0;
		return me.done();
	}

	/* ====================================================================== */
	/*                                 THE PELT                               */
	/* ====================================================================== */

	static ModelData pelt(Style st, Body b) {
		Mesh me = new Mesh();
		jerkin(me, st, b);
		mantle(me, st, b);
		drape(me, st, b);
		for (int s = 0; s < 2; s++) {
			foreleg(me, st, b, s);
		}
		return me.done();
	}

	/** A hide jerkin from the neck to the hips, under the pelt, laced up the front. */
	static void jerkin(Mesh me, Style st, Body b) {
		ModelData kit = ArmourForge.mergeAll(b.torso, b.legs);
		double neck = b.neckY, waist = b.waistY, h = waist - neck;
		double[] ys = { neck + 1.5, neck + h * 0.35, neck + h * 0.7, waist, waist + 8 };
		int n = 10;
		int[][] rg = new int[ys.length][];
		double cz = (b.chestZ + b.backZ) / 2;
		for (int r = 0; r < ys.length; r++) {
			int lab = r == 0 ? NECK : r == 1 ? CHEST : BELLY;
			rg[r] = BaseMantle.fitted(me, kit, ys[r], cz, n, 1.5, b.neckR, r == 4 ? 2.2 : 0, lab);
		}
		for (int r = 0; r + 1 < ys.length; r++) {
			me.band(rg[r], rg[r + 1], true, r == ys.length - 2 ? LEG_DARK : LEG, pt(0, (ys[r] + ys[r + 1]) / 2, cz));
		}
		// a belt of dark hide
		int[] w0 = BaseMantle.fitted(me, kit, waist - 3.5, cz, n, 2.2, b.neckR, 0, BELLY), w1 = BaseMantle.fitted(me, kit, waist + 1.5, cz, n, 2.4, b.neckR, 0, BELLY);
		me.prio = 1;
		me.band(w0, w1, true, st.trimDark, pt(0, waist - 1, cz));
		me.prio = 0;
	}

	/**
	 * The mantle: rings from a collar hunched up behind the neck, out over the shoulders (twice their width) to a
	 * ragged rim; open down the front. Lined inside. Rigid on the chest, with three shards along each shoulder.
	 */
	static void mantle(Mesh me, Style st, Body b) {
		double top = b.shoulderTop, front = Math.max(-b.chestZ, 12), back = b.backZ, sc = b.female ? 0.88 : 1;
		// y, half-width, half-depth front, behind, the hump (raised behind), the front opening (degrees each side)
		double[][] rows = { { b.neckY - 3, b.neckR + 6, b.neckR + 4, b.neckR + 7, 7, 28 }, { top - 6 * sc, b.torsoHW + 5, front + 2, back + 8, 10, 29 },
				{ top - 1 * sc, b.torsoHW + 14 * sc, front + 6, back + 12, 6, 31 }, { top + 8 * sc, b.armOuter + 9 * sc, front + 8.5, back + 14, 1, 33 },
				{ top + 20 * sc, b.armOuter + 8 * sc, front + 9, back + 15, 0, 36 } };
		int cols = 14, R = rows.length;
		int[][] g = new int[R][cols + 1];
		for (int r = 0; r < R; r++) {
			double a = rows[r][5];
			for (int i = 0; i <= cols; i++) {
				double th = a + (360 - 2 * a) * i / cols, c = Math.cos(Math.toRadians(th));
				double y = rows[r][0] - rows[r][4] * sc * Math.pow(Math.max(0, -c), 1.5);
				if (r == R - 1) { // the ragged edge: tufts hanging lower, longer behind
					y += (i % 2 == 1 ? 6 : 0) * sc + (1 - c) * 3 * sc;
				}
				g[r][i] = me.v(ell(0, y, 0, rows[r][1], rows[r][2], rows[r][3], th), CHEST);
			}
		}
		double[] in = pt(0, top + 4, 0);
		for (int r = 0; r + 1 < R; r++) {
			for (int i = 0; i < cols; i++) {
				boolean spine = i == cols / 2 - 1 || i == cols / 2;
				int col = r == 0 ? HIDE_TOP : spine && r <= 2 ? HIDE_TOP : r == R - 2 ? (i % 2 == 0 ? HIDE : HIDE_2) : HIDE;
				quad(me, g[r][i], g[r][i + 1], g[r + 1][i + 1], g[r + 1][i], col, in);
				if (r >= 2) {
					BaseJellyfish.lining(me, g[r][i], g[r + 1][i], g[r + 1][i + 1], g[r][i + 1], st.leatherDark, pt(0, (me.p(g[r][i])[1] + me.p(g[r + 1][i])[1]) / 2, 0));
				}
			}
		}
		// the shards along each shoulder, leaning out
		me.prio = 2;
		for (int s = 0; s < 2; s++) {
			double sg = sx(s);
			double[][] at = { { 58, 1, 17 }, { 84, 1.6, 23 }, { 110, 1.2, 16 }, { 138, 2.0, 11 } };
			for (int k = 0; k < at.length; k++) {
				double th = s == 1 ? at[k][0] : 360 - at[k][0];
				double c = Math.cos(Math.toRadians(th));
				double y = lerp(rows[1][0], rows[2][0], 0.5) - lerp(rows[1][4], rows[2][4], 0.5) * sc * Math.pow(Math.max(0, -c), 1.5);
				double[] p = ell(0, y, 0, lerp(rows[1][1], rows[2][1], 0.5) - 1.5, lerp(rows[1][2], rows[2][2], 0.5) - 1.5, lerp(rows[1][3], rows[2][3], 0.5) - 1.5, th);
				double[] dir = pt(sg * (0.45 + k * 0.1), -1, -c * 0.35 + 0.1);
				shard(me, p, dir, at[k][2] * sc, 3.2 * sc, CHEST, k % 2 == 0);
			}
		}
		// and the biggest, up the hump behind the neck
		double[][] hump = { { 0, 1.5, 24, 0.35 }, { -4.5, 4, 17, 0.6 }, { 4.5, 4.5, 15, 0.7 } };
		for (double[] h : hump) {
			double y = rows[1][0] - rows[1][4] * sc + h[1] * sc;
			shard(me, pt(h[0] * sc, y, back + 7 - h[1] * 0.4), pt(h[0] * 0.08, -1, h[3]), h[2] * sc, 3.8 * sc, CHEST, h[0] == 0);
		}
		me.prio = 0;
	}

	/**
	 * The hide down the back: from under the mantle to the knees, narrowing, a dark stripe down the spine, its hem
	 * ragged; shards down the spine. On the cape chain, its rows at the stock cape's heights.
	 */
	static void drape(Mesh me, Style st, Body b) {
		double hips = b.hipsBackZ, sc = b.female ? 0.88 : 1;
		double hem = b.capeY[3] + 4;
		// y, label, z of the middle, half-width, z of the edges
		double[][] rows = { { b.shoulderTop + 8 * sc, CAPE_TOP, b.backZ + 10, b.torsoHW + 10, b.backZ + 2 }, { b.capeY[1], CAPE_WAIST, hips + 6, b.torsoHW + 7, hips + 1 },
				{ (b.capeY[1] + b.capeY[2]) / 2, CAPE_HIP, hips + 8, b.legsHW + 7, hips + 2 }, { b.capeY[2], CAPE_THIGH, hips + 10, b.legsHW + 6, hips + 3 },
				{ hem, CAPE_KNEE, hips + 12, b.legsHW + 3, hips + 5 } };
		int cols = 6, R = rows.length;
		int[][] o = new int[R][cols + 1];
		for (int r = 0; r < R; r++) {
			for (int i = 0; i <= cols; i++) {
				double u = -1 + 2.0 * i / cols;
				double z = rows[r][2] - (rows[r][2] - rows[r][4]) * u * u;
				double y = rows[r][0];
				if (r == R - 1) {
					y += (i % 2 == 0 ? 7 : 0) * sc - Math.abs(u) * 3;
				}
				o[r][i] = me.v(pt(u * rows[r][3], y, z), (int) rows[r][1]);
			}
		}
		for (int r = 0; r + 1 < R; r++) {
			double[] body = pt(0, (rows[r][0] + rows[r + 1][0]) / 2, -40);
			for (int i = 0; i < cols; i++) {
				boolean spine = i == cols / 2 - 1 || i == cols / 2;
				int col = spine ? HIDE_TOP : (r + i) % 2 == 0 ? HIDE : HIDE_2;
				quad(me, o[r][i], o[r][i + 1], o[r + 1][i + 1], o[r + 1][i], col, body);
				quad(me, o[r][i], o[r + 1][i], o[r + 1][i + 1], o[r][i + 1], st.leatherDark, pt(0, body[1], 80));
			}
		}
		// the shards down the spine, standing back off the hide
		me.prio = 2;
		double[][] sp = { { 0.35, 16 }, { 0.85, 14 }, { 1.4, 11 }, { 2.1, 8 } };
		for (int k = 0; k < sp.length; k++) {
			int r = (int) sp[k][0];
			double f = sp[k][0] - r;
			double y = lerp(rows[r][0], rows[r + 1][0], f), z = lerp(rows[r][2], rows[r + 1][2], f) - 0.5;
			shard(me, pt((k % 2 == 0 ? 1 : -1) * 1.5, y, z - 1), pt((k % 2 == 0 ? 0.3 : -0.3), -1, 0.55), sp[k][1] * sc, 3.2 * sc, (int) rows[r][1], k % 2 == 1);
		}
		me.prio = 0;
	}

	/** One of the bear's forelegs: over the shoulder from the mantle's top and down the chest to a paw with four claws. */
	static void foreleg(Mesh me, Style st, Body b, int s) {
		double sg = sx(s), top = b.shoulderTop, sc = b.female ? 0.86 : 1, chest = b.chestZ;
		double[][] p = { { sg * 13 * sc, top - 11 * sc, 4 }, { sg * 15 * sc, top - 10 * sc, chest - 5 }, { sg * 12.5 * sc, top + 6 * sc, chest - 9 },
				{ sg * 9.5 * sc, top + 19 * sc, chest - 6 } };
		double[] r = { 6.5 * sc, 6.5 * sc, 5.6 * sc, 4.8 * sc };
		BaseJellyfish.tubePath(me, p, r, new int[] { CHEST, CHEST, CHEST, CHEST }, 6, 0, new int[] { HIDE_TOP, HIDE_2, HIDE });
		// the paw: a flattened lump on the chest
		double[] pc = add(p[3], pt(-sg * 0.5, 4.5 * sc, 0.5));
		double[][] pp = { p[3], pc, add(pc, pt(0, 4 * sc, 1.2)) };
		BaseJellyfish.tubePath(me, pp, new double[] { 5.2 * sc, 6.2 * sc, 0 }, new int[] { CHEST, CHEST, CHEST }, 6, 0, new int[] { PAW });
		me.prio = 2;
		for (int k = 0; k < 4; k++) {
			double x = (k - 1.5) * 2.4 * sc;
			double[] base = add(pc, pt(x, 4.2 * sc, -2.6 * sc));
			claw(me, base, pt(x * 0.05, 1, -0.35), pt(0, 0.6, 1), 5.5 * sc, 1.0, CHEST, TUSK);
		}
		me.prio = 0;
	}

	/* ====================================================================== */
	/*                                 THE LEGS                               */
	/* ====================================================================== */

	/** Hide leggings on a belt, three shaggy fur wraps a leg and a thong wound between them. */
	static ModelData leggings(Style st, Body b) {
		Mesh me = new Mesh();
		int[] lb = b.legs.bounds();
		double cz = (lb[2] + lb[5]) / 2.0, waist = b.legsTop + 1;
		int n = 12;
		int[] w0 = BaseMantle.fitted(me, b.legs, waist, cz, n, 1.8, 8, 0, WAIST), w1 = BaseMantle.fitted(me, b.legs, waist + 5, cz, n, 2.0, 8, 0, PELVIS);
		int[] w2 = BaseMantle.fitted(me, b.legs, b.hipJointY + 3, cz, n, 1.8, 8, 0, PELVIS);
		me.band(w0, w1, true, st.trimDark, pt(0, waist + 2, cz));
		me.band(w1, w2, true, st.leather, pt(0, waist + 8, cz));
		for (int s = 0; s < 2; s++) {
			double[][] rr = BaseMantle.legRings(b, s);
			int k8 = 8;
			int[][] g = new int[rr.length][k8];
			for (int k = 0; k < rr.length; k++) {
				for (int i = 0; i < k8; i++) {
					double th = 360.0 * i / k8;
					int lab = (int) rr[k][5];
					if (k == 0 && Math.abs(th - (s == 0 ? 270 : 90)) <= 45) {
						lab = BaseMantle.HIP[s];
					}
					g[k][i] = me.v(ell(rr[k][1], rr[k][0], rr[k][2], rr[k][3] + 0.6, rr[k][4] + 0.6, rr[k][4] + 0.6, th), lab);
				}
			}
			for (int k = 0; k + 1 < rr.length; k++) {
				me.band(g[k], g[k + 1], true, k % 2 == 0 ? st.leather : st.leatherDark, pt((rr[k][1] + rr[k + 1][1]) / 2, (rr[k][0] + rr[k + 1][0]) / 2, (rr[k][2] + rr[k + 1][2]) / 2));
			}
			// the fur wraps: high on the thigh, above the knee, and on the calf
			me.prio = 1;
			double[] ys = { lerp(rr[0][0], rr[1][0], 0.35), lerp(rr[1][0], rr[2][0], 0.55), lerp(rr[3][0], rr[4][0], 0.3) };
			int[] labs = { THIGH[s], THIGH[s], CALF[s] };
			for (int k = 0; k < ys.length; k++) {
				double[] l = BaseMantle.legAt(rr, ys[k]);
				ruff(me, pt(l[0], 0, l[1]), ys[k] - 2, l[2] + 1.6, l[3] + 1.6, 7, 2.2, 8, labs[k], k == 1 ? st.trim : HIDE, k == 1 ? st.trimDark : HIDE_2);
			}
			// a thong wound down the shin between the knee and the calf wrap
			me.prio = 2;
			double y0 = rr[2][0] + 3, y1 = ys[2] - 3;
			int seg = 4;
			int[] up = new int[seg + 1], dn = new int[seg + 1];
			double[][] cs = new double[seg + 1][];
			for (int i = 0; i <= seg; i++) {
				double t = (double) i / seg, y = lerp(y0, y1, t);
				double[] l = BaseMantle.legAt(rr, y);
				cs[i] = pt(l[0], y, l[1]);
				double[] q = ell(l[0], y, l[1], l[2] + 1.2, l[3] + 1.2, l[3] + 1.2, (s == 0 ? 20 : 340) + 300 * t * (s == 0 ? 1 : -1));
				int lab = t < 0.4 ? BaseMantle.KNEE[s] == 35 ? 36 : 33 : CALF[s];
				up[i] = me.v(pt(q[0], q[1] - 1, q[2]), lab);
				dn[i] = me.v(pt(q[0], q[1] + 1, q[2]), lab);
			}
			for (int i = 0; i < seg; i++) {
				quad(me, up[i], up[i + 1], dn[i + 1], dn[i], st.trimDark, lerp(cs[i], cs[i + 1], 0.5));
			}
			me.prio = 0;
		}
		// the legs' inner sides meet in the middle: keep each leg's vertices on its own side, so no two vertices with
		// different legs' labels share a position (the client merges vertices by position, the first label winning)
		java.util.Set<Integer> left = java.util.Set.of(43, 36, 35, 37, 82, 38, 40), right = java.util.Set.of(44, 33, 34, 31, 83, 32, 42);
		for (int v = 0; v < me.m.vertices.size(); v++) {
			int[] q = me.m.vertices.get(v);
			int l = me.m.vertexSkins.get(v);
			if (left.contains(l) && q[0] < 1) {
				q[0] = 1;
			} else if (right.contains(l) && q[0] > -1) {
				q[0] = -1;
			}
		}
		return me.done();
	}

	/* ====================================================================== */
	/*                               BOOTS, PAWS                             */
	/* ====================================================================== */

	/** Bear-paw boots: a shaggy cuff at mid-shin, a shaft down to a broad paw with toe pads and four claws. */
	static ModelData pawBoots(Style st, Body b) {
		Mesh me = new Mesh();
		for (int s = 0; s < 2; s++) {
			double[] an = b.ankle[s], sh = b.shin[s];
			double fx = an[0], fz = an[2] + 0.5, ay = an[1], sole = b.soleY, toe = b.toeZ, sc = b.female ? 0.9 : 1;
			int n = 6;
			double topY = lerp(sh[1], ay, 0.45);
			double[][] shaft = { { topY, 6.4, 7.0 }, { ay, 5.6, 6.4 }, { sole - 8, 6.8, 7.6 } };
			int[][] sr = new int[shaft.length][];
			for (int k = 0; k < shaft.length; k++) {
				sr[k] = BaseMantle.ring(me, fx, shaft[k][0], fz, shaft[k][1] * sc, shaft[k][2] * sc, shaft[k][2] * sc, n, 0, ANKLE[s]);
			}
			for (int k = 0; k + 1 < shaft.length; k++) {
				me.band(sr[k], sr[k + 1], true, k == 0 ? LEG_DARK : LEG, pt(fx, (shaft[k][0] + shaft[k + 1][0]) / 2, fz));
			}
			me.prio = 1;
			ruff(me, pt(fx, 0, fz), topY - 2.5, 7.0 * sc, 7.6 * sc, 8, 2.2, 6, ANKLE[s], HIDE, HIDE_2);
			me.prio = 0;
			// the paw along z: heel, middle (the heel label, the toes' pivot), the broad forefoot, the toes
			double[][] ft = { { b.heelZ, sole - 6.5, 6.4, 6.8 }, { b.heel[s][2], sole - 6.5, 7.6, 7.0 }, { toe + 1, sole - 4.4, 8.2, 4.6 } };
			int[] labs = { ANKLE[s], HEEL[s], TOES[s] };
			int[][] fr = new int[ft.length][7];
			for (int k = 0; k < ft.length; k++) {
				for (int i = 0; i < 7; i++) {
					double a = Math.PI * 2 * (i + 0.5) / 7;
					fr[k][i] = me.v(pt(fx + Math.cos(a) * ft[k][2] * sc, ft[k][1] + Math.sin(a) * ft[k][3] * sc, ft[k][0]), labs[k]);
				}
			}
			for (int k = 0; k + 1 < ft.length; k++) {
				me.band(fr[k], fr[k + 1], true, k == 1 ? PAW : LEG, pt(fx, (ft[k][1] + ft[k + 1][1]) / 2, (ft[k][0] + ft[k + 1][0]) / 2));
			}
			me.fan(fr[0], pt(fx, ft[0][1], ft[0][0] + 1.5), ANKLE[s], LEG_DARK, pt(fx, ft[0][1], ft[0][0] - 5));
			me.fan(fr[ft.length - 1], pt(fx, sole - 4, toe - 2.5), TOES[s], PAW, pt(fx, ft[2][1], ft[2][0] + 5));
			// four claws over the toes, curling down to the ground
			me.prio = 2;
			for (int k = 0; k < 4; k++) {
				double x = fx + (k - 1.5) * 3.3 * sc;
				double[] base = pt(x, sole - 5, toe);
				limb(me, base, add(base, pt((k - 1.5) * 0.8, 4.5, -5.5 * sc)), 1.5 * sc, 0, 3, 0.5, TOES[s], TOES[s], TUSK);
			}
			me.prio = 0;
		}
		return me.done();
	}

	/** Paw gloves: a shaggy cuff on the forearm, a fat paw round the hand (27 / 28), four claws and a shard at the wrist. */
	static ModelData paws(Style st, Body b) {
		Mesh me = new Mesh();
		for (int s = 0; s < 2; s++) {
			double sg = sx(s);
			double[] el = b.elbow[s], wr = b.wrist[s], hd = b.hand[s];
			double bottom = b.handBottom, hrx = b.handR[s][0], hrz = b.handR[s][1], sc = b.female ? 0.86 : 1;
			int n = 6;
			double[] c0 = lerp(el, wr, 0.45);
			double[][] rings = { { c0[0], c0[1], c0[2], 5.6 * sc, 5.6 * sc },
					{ hd[0], bottom - 11, hd[2], hrx + 2.2, hrz + 2.6 }, { hd[0] + sg * 0.4, bottom - 4.5, hd[2] - 0.5, hrx + 2.6, hrz + 3.0 } };
			int[] labs = { FOREARM[s], HAND[s], HAND[s] };
			int[][] rg = new int[rings.length][];
			for (int k = 0; k < rings.length; k++) {
				rg[k] = BaseMantle.ring(me, rings[k][0], rings[k][1], rings[k][2], rings[k][3], rings[k][4], rings[k][4], n, 0, labs[k]);
			}
			for (int k = 0; k + 1 < rings.length; k++) {
				me.band(rg[k], rg[k + 1], true, k == 0 ? LEG : PAW, pt(rings[k][0], (rings[k][1] + rings[k + 1][1]) / 2, rings[k][2]));
			}
			double[] last = rings[rings.length - 1];
			me.fan(rg[rings.length - 1], pt(last[0], bottom + 1, last[2]), HAND[s], PAW, pt(last[0], last[1] - 4, last[2]));
			me.prio = 1;
			ruff(me, pt(c0[0], 0, c0[2]), c0[1] - 3, 6.0 * sc, 6.0 * sc, 9, 2.6, 6, FOREARM[s], HIDE, HIDE_2);
			me.prio = 2;
			// four claws from the front of the paw, curling under
			for (int k = 0; k < 4; k++) {
				double z = hd[2] + (k - 1.5) * 2.4 * sc;
				double[] base = pt(hd[0] - sg * 1.5, bottom - 1.5, z - 1.5);
				claw(me, base, pt(-sg * 0.25, 1, -0.5), pt(-sg * 0.4, 0.5, -1), 5.5 * sc, 1.0, HAND[s], TUSK);
			}
			// a sliver of star-glass on the back of the wrist
			shard(me, add(wr, pt(sg * 4.5 * sc, 1, 1)), pt(sg * 0.8, -1, 0.3), 7 * sc, 1.5, FOREARM[s], true);
			me.prio = 0;
		}
		return me.done();
	}
}
