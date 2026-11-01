package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ArmourForge.add;
import static com.rs2.tools.mapgen.ArmourForge.norm;
import static com.rs2.tools.mapgen.ArmourForge.scale;
import static com.rs2.tools.mapgen.ArmourForge.sub;
import static com.rs2.tools.mapgen.BaseJellyfish.limb;
import static com.rs2.tools.mapgen.BaseJellyfish.lining;
import static com.rs2.tools.mapgen.BaseJellyfish.pt;
import static com.rs2.tools.mapgen.BaseJellyfish.quad;
import static com.rs2.tools.mapgen.BaseJellyfish.sheet;
import static com.rs2.tools.mapgen.BaseJellyfish.tri;
import static com.rs2.tools.mapgen.BaseMantle.ANKLE;
import static com.rs2.tools.mapgen.BaseMantle.BELLY;
import static com.rs2.tools.mapgen.BaseMantle.CALF;
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
import static com.rs2.tools.mapgen.BaseMantle.ell;
import static com.rs2.tools.mapgen.BaseMantle.lerp;
import static com.rs2.tools.mapgen.BaseMantle.sx;
import static com.rs2.tools.mapgen.ModelData.hsl;

import java.io.IOException;
import java.util.Random;

import com.rs2.tools.mapgen.ArmourSets.Style;
import com.rs2.tools.mapgen.BaseMantle.Body;
import com.rs2.tools.mapgen.BaseMantle.Mesh;

/**
 * The Wildgrove base (A30, MANTLE): a deep-wood druid half grown into a tree. Built from scratch on the mystic robe's
 * rig (see NewBases), every piece measured from the identity-kit body it is worn on. Bark (grey-brown, its grain in
 * alternating stripes), pale wood for the branches, moss and ivy green, and small golden flowers.
 *
 * <ul>
 * <li>Crown: a bark cowl over the head (open round the face, ragged at its edge, down to the nape behind) and out of
 * it a tall, lopsided crown of branches: a big many-pronged bough on the left, a smaller one on the right and a sprig
 * behind, forking as antlers do, with leaf clusters and a flower or two. Named "crown", so it hides nothing.</li>
 * <li>Bark tunic: three tiers of bark plates overlapping down the trunk like shingles over a bark underlayer, moss in
 * the cracks, ivy climbing from the right hip to the left shoulder; a gnarled burl on each shoulder, mossy on top,
 * with twigs sprouting from it; bark sleeves to the wrist. Named "tunic", so the arms kit is hidden.</li>
 * <li>Root robe: a mossy robe from the waist to below the knee that splits there into ten roots, thick at the top,
 * spreading and tapering to points on the ground.</li>
 * <li>Root boots: bark-bound feet whose toes are roots splaying out over the ground, and a root back from the heel.</li>
 * <li>Twig gauntlets: a bark cuff with a ragged rim, a bark hand and five twig fingers.</li>
 * </ul>
 *
 * <p>Labels (BaseMantle's rules): rings round joints take the joint's label so the client's pivots stay on the joints
 * (the tunic's neck ring 2, the sleeves' shoulder 21 / 25 and elbow 17 / 23 rings, the robe's waist 29, hip sides
 * 40 / 42, knee 34 / 35 and shin 31 / 37 rings, the boots' heels 47 / 48, the gauntlets' hands 27 / 28); everything
 * else a label that moves the same way without pivoting: the crown the skull 1; the bark tiers, ivy and moss the chest
 * 8 and belly 4; each burl and its twigs the upper arm 20 / 26 (rigid, like a pauldron); the sleeves' forearms and
 * the cuffs 19 / 22; the robe's thighs 43 / 44 and each root the calf 82 / 83 of its side, so the roots swing with the
 * shins and never tear; the boots' shafts 32 / 38 and root-toes 45 / 46. Each leg's vertices are kept on its own side
 * of the middle so no two vertices with different labels share a position.
 */
final class BaseTreant {

	private BaseTreant() {
	}

	static final int LEAF = DraftArmourB.LEAF, LEAF_DARK = hsl(18, 4, 36), IVY = hsl(19, 4, 30), MOSS_DARK = hsl(18, 4, 30);

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		Body b = BaseMantle.body(af, female);
		switch (slot) {
		case ArmourSets.HELM:
			return new ModelData[] { crown(st, b), null };
		case ArmourSets.BODY:
			return new ModelData[] { tunic(st, b), null };
		case ArmourSets.LEGS:
			return new ModelData[] { robe(st, b), null };
		case ArmourSets.BOOTS:
			return new ModelData[] { boots(st, b), null };
		case ArmourSets.GLOVES:
			return new ModelData[] { gauntlets(st, b), null };
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

	/**
	 * A branch: a tapered limb from p along d, then {@code depth} more forkings (two children, one bent each way about
	 * a plane that turns with each fork; the odd extra prong from mid-limb). {@code r} is the base radius; twigs at the
	 * tips close to a point. Deterministic for a given Random.
	 */
	static void branch(Mesh me, Random rnd, double[] p, double[] d, double len, double r, int depth, int label, int col, int col2, double[][] tips) {
		double[] dn = norm(d);
		double[] e = add(p, scale(dn, len));
		boolean last = depth == 0;
		limb(me, p, e, r, last ? 0 : r * 0.72, r > 1.6 ? 4 : 3, rnd.nextDouble(), label, label, depth % 2 == 0 ? col : col2);
		if (last) {
			if (tips != null) {
				for (int i = 0; i < tips.length; i++) {
					if (tips[i] == null) {
						tips[i] = e;
						break;
					}
				}
			}
			return;
		}
		double[][] uv = BaseJellyfish.basis(dn);
		double turn = rnd.nextDouble() * Math.PI;
		double[] side = add(scale(uv[0], Math.cos(turn)), scale(uv[1], Math.sin(turn)));
		for (int k = -1; k <= 1; k += 2) {
			double spread = 0.45 + rnd.nextDouble() * 0.35;
			double[] nd = norm(add(add(dn, scale(side, k * spread)), pt(0, -0.25, 0)));
			double f = k < 0 ? 0.72 + rnd.nextDouble() * 0.15 : 0.55 + rnd.nextDouble() * 0.15;
			branch(me, rnd, e, nd, len * f, r * 0.7, depth - 1, label, col, col2, tips);
		}
		if (depth >= 2 && rnd.nextDouble() < 0.6) { // a short prong off the middle of the limb
			double[] nd = norm(add(dn, scale(side, -0.9)));
			branch(me, rnd, lerp(p, e, 0.5), nd, len * 0.45, r * 0.55, 0, label, col, col2, tips);
		}
	}

	/** A leaf: a two-sided diamond from {@code p} along {@code d}, its face towards {@code n}. */
	static void leaf(Mesh me, double[] p, double[] d, double[] n, double len, double w, int label, int col, int col2) {
		double[] dn = norm(d), across = norm(ModelData.cross(dn, n));
		int a = me.v(p, label), t = me.v(add(p, scale(dn, len)), label);
		int l = me.v(add(add(p, scale(dn, len * 0.45)), scale(across, w)), label), r = me.v(add(add(p, scale(dn, len * 0.45)), scale(across, -w)), label);
		sheet(me, a, l, t, r, col, col2, n);
	}

	/** A cluster of {@code k} leaves round a point, fanning out. */
	static void leaves(Mesh me, Random rnd, double[] p, int k, double size, int label) {
		for (int i = 0; i < k; i++) {
			double a = Math.PI * 2 * i / k + rnd.nextDouble() * 0.6;
			double[] d = pt(Math.cos(a), -0.35 + rnd.nextDouble() * 0.5, Math.sin(a));
			double[] n = norm(pt(-Math.sin(a) * 0.3, -1, Math.cos(a) * 0.3));
			leaf(me, p, d, n, size * (0.8 + rnd.nextDouble() * 0.4), size * 0.38, label, i % 2 == 0 ? LEAF : LEAF_DARK, LEAF_DARK);
		}
	}

	/** A small golden flower: five petals round a bright heart, facing {@code n}. */
	static void flower(Mesh me, double[] c, double[] n, double r, int label, Style st) {
		double[][] uv = BaseJellyfish.basis(n);
		double[] nn = norm(n);
		int h = me.v(add(c, scale(nn, r * 0.35)), label);
		int[] pts = new int[5];
		for (int i = 0; i < 5; i++) {
			double a = Math.PI * 2 * i / 5;
			pts[i] = me.v(add(c, add(scale(uv[0], Math.cos(a) * r), scale(uv[1], Math.sin(a) * r))), label);
		}
		double[] behind = sub(c, scale(nn, 6));
		for (int i = 0; i < 5; i++) {
			tri(me, pts[i], pts[(i + 1) % 5], h, i % 2 == 0 ? st.accent : st.core, behind);
		}
	}

	/** A ring round a vertical axis whose radius wanders a little (gnarled bark), {@code n} vertices from {@code th0}. */
	static int[] gnarled(Mesh me, Random rnd, double cx, double y, double cz, double rx, double rzf, double rzb, int n, double th0, double rough, int label) {
		int[] r = new int[n];
		for (int i = 0; i < n; i++) {
			double f = 1 + (rnd.nextDouble() - 0.5) * rough;
			r[i] = me.v(ell(cx, y + (rnd.nextDouble() - 0.5) * rough * 3, cz, rx * f, rzf * f, rzb * f, th0 + 360.0 * i / n), label);
		}
		return r;
	}

	/** A band between two rings, its columns alternating two colours (the grain). */
	static void grain(Mesh me, int[] a, int[] b, int c1, int c2, double[] inside) {
		for (int i = 0; i < a.length; i++) {
			int j = (i + 1) % a.length;
			quad(me, a[i], a[j], b[j], b[i], i % 2 == 0 ? c1 : c2, inside);
		}
	}

	/* ====================================================================== */
	/*                                THE CROWN                               */
	/* ====================================================================== */

	/**
	 * The bark cowl (rings from the crown to a ragged edge: the brow in front, the jaw at the sides, the nape behind)
	 * and the crown of branches growing out of it. All on the skull label.
	 */
	static ModelData crown(Style st, Body b) {
		Mesh me = new Mesh();
		Random rnd = new Random(30);
		double T = b.headTop, F = b.headFront, B = b.headBack, s = (b.headChin - b.headTop) / 24.0, hw = b.headHW;
		double cz = (F + B) / 2;
		int n = 10;
		int[] ra = new int[n], rb = new int[n], rc = new int[n];
		for (int i = 0; i < n; i++) {
			double th = 360.0 * i / n, c = Math.cos(Math.toRadians(th));
			ra[i] = me.v(ell(0, T - 4 * s, cz + 1, (hw + 3) * 0.6, (cz - F + 3) * 0.6, (B - cz + 3.5) * 0.6, th), SKULL);
			rb[i] = me.v(ell(0, T + 2 * s, cz, hw + 3.5, cz - F + 3, B - cz + 4, th), SKULL);
			double e = 4 + 21 * Math.pow((1 - c) / 2, 0.7) + (i % 2 == 1 ? 3 : 0);
			rc[i] = me.v(ell(0, T + e * s, cz, hw + 4, cz - F + 3.5, B - cz + 2.5, th), SKULL);
		}
		int apex = me.v(pt(0, T - 6.5 * s, cz + 1.5), SKULL);
		double[] mid = pt(0, T + 6 * s, cz);
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			// moss on the crown round the branches' roots, and in a few cracks lower down
			tri(me, apex, ra[i], ra[j], i % 3 == 2 ? st.trimDark : st.light, mid);
			quad(me, ra[i], ra[j], rb[j], rb[i], i == 3 || i == 6 ? st.light : i % 2 == 0 ? st.trim : st.trimDark, mid);
			quad(me, rb[i], rb[j], rc[j], rc[i], i == 4 ? st.light : i % 2 == 0 ? st.trimDark : st.trim, mid);
			lining(me, rb[i], rc[i], rc[j], rb[j], st.leatherDark, pt(0, me.p(rc[i])[1] - 4, cz));
		}
		// the crown of branches: a big bough on the left, a smaller one on the right, a sprig behind
		me.prio = 2;
		double[][] tips = new double[40][];
		branch(me, rnd, pt(4 * s, T - 3 * s, cz + 1), pt(0.5, -1, 0.1), 19 * s, 3.2, 3, SKULL, st.leather, st.trim, tips);
		branch(me, rnd, pt(-4.5 * s, T - 2 * s, cz + 0.5), pt(-0.65, -1, -0.05), 15 * s, 2.8, 2, SKULL, st.leather, st.trim, tips);
		branch(me, rnd, pt(0, T - 1 * s, cz + 6), pt(-0.1, -0.8, 0.8), 10 * s, 2.0, 1, SKULL, st.leather, st.trim, tips);
		// leaf clusters on a few tips, and two golden flowers
		int lc = 0;
		for (int i = 0; i < tips.length && tips[i] != null; i++) {
			if (i % 3 == 1 && lc < 4) {
				leaves(me, rnd, tips[i], 3, 7 * s, SKULL);
				lc++;
			}
		}
		flower(me, ell(0, T + 1 * s, cz, hw + 3.4, cz - F + 3.2, B - cz + 4, 34), norm(pt(0.5, -0.3, -1)), 1.8, SKULL, st);
		flower(me, ell(0, T - 1 * s, cz, hw + 3.2, cz - F + 3, B - cz + 4, 300), norm(pt(-0.6, -0.4, -0.7)), 1.5, SKULL, st);
		me.prio = 0;
		return me.done();
	}

	/* ====================================================================== */
	/*                                 THE TUNIC                              */
	/* ====================================================================== */

	static ModelData tunic(Style st, Body b) {
		Mesh me = new Mesh();
		Random rnd = new Random(31);
		trunk(me, rnd, st, b);
		for (int s = 0; s < 2; s++) {
			sleeve(me, rnd, st, b, s);
			burl(me, rnd, st, b, s);
		}
		ivy(me, rnd, st, b);
		return me.done();
	}

	/** The bark underlayer from the neck to the hips, a gnarled collar, and three tiers of bark plates over it. */
	static void trunk(Mesh me, Random rnd, Style st, Body b) {
		ModelData kit = ArmourForge.mergeAll(b.torso, b.legs);
		double neck = b.neckY, waist = b.waistY, h = waist - neck, cz = (b.chestZ + b.backZ) / 2;
		double[] ys = { neck + 1.5, neck + h * 0.3, neck + h * 0.65, waist, waist + 10 };
		int n = 10;
		int[][] rg = new int[ys.length][];
		for (int r = 0; r < ys.length; r++) {
			int lab = r == 0 ? NECK : r == 1 ? CHEST : BELLY;
			rg[r] = BaseMantle.fitted(me, kit, ys[r], cz, n, 1.3, b.neckR, r == 4 ? 2 : 0, lab);
		}
		for (int r = 0; r + 1 < ys.length; r++) {
			grain(me, rg[r], rg[r + 1], st.trimDark, MOSS_DARK, pt(0, (ys[r] + ys[r + 1]) / 2, cz));
		}
		// the collar: a ragged ring of bark standing up round the neck
		int cn = 8;
		int[] c0 = new int[cn], c1 = new int[cn];
		for (int i = 0; i < cn; i++) {
			double th = 360.0 * i / cn + 22.5;
			c0[i] = me.v(ell(0, neck + 2, b.neckZ, b.neckR + 4, b.neckR + 3.5, b.neckR + 4, th), CHEST);
			c1[i] = me.v(ell(0, neck - 4 - (i % 2) * 2.5, b.neckZ, b.neckR + 5.5, b.neckR + 5, b.neckR + 5.5, th), CHEST);
		}
		double[] ax = pt(0, neck - 1, b.neckZ);
		grain(me, c0, c1, st.trim, st.trimDark, ax);
		for (int i = 0; i < cn; i++) {
			lining(me, c0[i], c1[i], c1[(i + 1) % cn], c0[(i + 1) % cn], st.leatherDark, ax);
		}
		// the tiers of plates: each flares from its top to a thick lower lip that overlaps the next
		me.prio = 1;
		double[][] tiers = { { 0.12, 0.42 }, { 0.38, 0.72 }, { 0.68, 1.08 } };
		for (int k = 0; k < tiers.length; k++) {
			double y0 = neck + h * tiers[k][0], y1 = neck + h * tiers[k][1];
			int lab0 = y0 < neck + h * 0.45 ? CHEST : BELLY, lab1 = y1 < neck + h * 0.45 ? CHEST : BELLY;
			int[] top = BaseMantle.fitted(me, kit, y0, cz, n, 2.0, b.neckR, 0, lab0);
			int[] bot = new int[n], lip = new int[n];
			int[] base = BaseMantle.fitted(me, kit, y1, cz, n, 3.8 + k * 0.3, b.neckR, 0, lab1);
			for (int i = 0; i < n; i++) {
				double[] q = me.p(base[i]);
				double f = 1 + (rnd.nextDouble() - 0.3) * 0.08;
				bot[i] = me.v(pt(q[0] * f, q[1] + (i % 3 == 0 ? 2 : 0), (q[2] - cz) * f + cz), lab1);
				lip[i] = me.v(pt(q[0] * 0.93, q[1] - 0.5, (q[2] - cz) * 0.93 + cz), lab1);
			}
			double[] in = pt(0, (y0 + y1) / 2, cz);
			for (int i = 0; i < n; i++) {
				int j = (i + 1) % n;
				boolean moss = (i + k) % 4 == 0;
				quad(me, top[i], top[j], bot[j], bot[i], moss ? st.light : i % 2 == 0 ? st.trim : st.leather, in);
				lining(me, bot[i], bot[j], lip[j], lip[i], st.trimDark, pt(0, y1 - 6, cz));
			}
		}
		me.prio = 0;
	}

	/** A bark sleeve: shoulder ring (21 / 25), the upper arm (20 / 26), elbow ring (17 / 23), forearm to a ragged cuff (19 / 22). */
	static void sleeve(Mesh me, Random rnd, Style st, Body b, int s) {
		double sc = b.female ? 0.86 : 1;
		int n = 7;
		double[] sh = b.shoulder[s], el = b.elbow[s], wr = b.wrist[s];
		double[][] c = { sh, lerp(sh, el, 0.5), el, lerp(el, wr, 0.5), add(wr, pt(0, -1, 0)) };
		double[] r = { Math.max(b.shoulderR[s][0], b.shoulderR[s][1]) + 1.4, 6.6 * sc, Math.max(b.elbowR[s][0] + 2.2, 5.6 * sc), 6.0 * sc, 6.8 * sc };
		int[] labs = { SHOULDER[s], s == 0 ? 20 : 26, ELBOW[s], FOREARM[s], FOREARM[s] };
		int[][] rg = new int[c.length][];
		for (int k = 0; k < c.length; k++) {
			rg[k] = gnarled(me, rnd, c[k][0], c[k][1], c[k][2], r[k], r[k], r[k], n, 0, k == 0 || k == 2 ? 0 : 0.14, labs[k]);
		}
		for (int k = 0; k + 1 < c.length; k++) {
			grain(me, rg[k], rg[k + 1], k == 3 ? st.trim : st.trimDark, k == 3 ? st.leather : st.trim, lerp(c[k], c[k + 1], 0.5));
		}
	}

	/**
	 * A burl on the shoulder: a lumpy knot of wood, mossy on top, two twigs sprouting from it (one in leaf, one in
	 * flower). On the upper arm's label, so it rides the shoulder like a pauldron.
	 */
	static void burl(Mesh me, Random rnd, Style st, Body b, int s) {
		double sg = sx(s), sc = b.female ? 0.86 : 1;
		int lab = s == 0 ? 20 : 26;
		double[] c = add(b.shoulder[s], pt(sg * 4.5 * sc, -7 * sc, 0));
		double rad = 12.5 * sc;
		int n = 7;
		double[][] lv = { { -0.85, 0.5 }, { -0.25, 1.0 }, { 0.45, 0.92 } };
		int[][] rg = new int[lv.length][n];
		for (int k = 0; k < lv.length; k++) {
			for (int i = 0; i < n; i++) {
				double a = Math.PI * 2 * i / n + k * 0.4, f = lv[k][1] * (0.82 + rnd.nextDouble() * 0.36);
				rg[k][i] = me.v(add(c, pt(Math.cos(a) * rad * f * 1.05, lv[k][0] * rad * 0.8, Math.sin(a) * rad * f)), lab);
			}
		}
		int top = me.v(add(c, pt(sg * 1.5, -rad * 0.95, 0.5)), lab), bot = me.v(add(c, pt(-sg * 1, rad * 0.75, 0)), lab);
		me.prio = 1;
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			tri(me, top, rg[0][i], rg[0][j], st.light, c);
			quad(me, rg[0][i], rg[0][j], rg[1][j], rg[1][i], i % 3 == 0 ? st.trim : st.light, c);
			quad(me, rg[1][i], rg[1][j], rg[2][j], rg[2][i], i % 2 == 0 ? st.trim : st.trimDark, c);
			tri(me, rg[2][i], bot, rg[2][j], st.trimDark, c);
		}
		// the twigs
		me.prio = 2;
		double[][] tips = new double[12][];
		branch(me, rnd, add(c, pt(sg * 2, -rad * 0.7, -2)), pt(sg * 0.5, -1, -0.3), 7 * sc, 1.3, 1, lab, st.leather, st.trim, tips);
		branch(me, rnd, add(c, pt(-sg * 1, -rad * 0.6, 3)), pt(-sg * 0.1, -1, 0.6), 5.5 * sc, 1.1, 1, lab, st.leather, st.trim, tips);
		if (tips[0] != null) {
			leaves(me, rnd, tips[0], 3, 4 * sc, lab);
		}
		if (tips[2] != null) {
			flower(me, tips[2], pt(0, -1, -0.3), 1.6, lab, st);
		}
		me.prio = 0;
	}

	/** Ivy climbing the trunk from the right hip to the left shoulder: a stem over the plates, leaves and flowers along it. */
	static void ivy(Mesh me, Random rnd, Style st, Body b) {
		ModelData kit = ArmourForge.mergeAll(b.torso, b.legs);
		double neck = b.neckY, waist = b.waistY, h = waist - neck;
		int seg = 7;
		double[][] p = new double[seg + 1][];
		for (int i = 0; i <= seg; i++) {
			double t = (double) i / seg, y = lerp(waist + 6, neck + h * 0.12, t);
			double x = lerp(-b.torsoHW * 0.75, b.torsoHW * 0.7, t) + Math.sin(t * Math.PI * 3) * 2.5;
			double[] f = ArmourForge.surface(kit, pt(x, y, -100), pt(0, 0, 1));
			double z = (f == null ? b.chestZ : f[2]) - 5;
			p[i] = pt(x, y, z);
		}
		me.prio = 3;
		for (int i = 0; i < seg; i++) {
			int lab = CHEST;
			double[] d = sub(p[i + 1], p[i]);
			double[] across = norm(pt(-d[1], d[0], 0));
			int a = me.v(add(p[i], scale(across, 0.8)), lab), c = me.v(sub(p[i], scale(across, 0.8)), lab);
			int a2 = me.v(add(p[i + 1], scale(across, 0.8)), lab), c2 = me.v(sub(p[i + 1], scale(across, 0.8)), lab);
			sheet(me, a, a2, c2, c, IVY, -1, pt(0, 0, -1));
			double side = i % 2 == 0 ? 1 : -1;
			leaf(me, p[i], add(scale(across, side), pt(0, 0.3, -0.2)), pt(0, -0.2, -1), 4.2, 1.8, lab, i % 2 == 0 ? LEAF : LEAF_DARK, LEAF_DARK);
			if (i % 3 == 1) {
				flower(me, add(lerp(p[i], p[i + 1], 0.5), add(scale(across, -side * 2.2), pt(0, 0, -0.5))), pt(0, -0.2, -1), 1.6, lab, st);
			}
		}
		me.prio = 0;
	}

	/* ====================================================================== */
	/*                                THE ROBE                                */
	/* ====================================================================== */

	/**
	 * The root robe: a mossy skirt from the waist (29) over the hips (40 / 42 at the sides) and down each leg's half
	 * (thighs 43 / 44, knee ring 34 / 35, shin ring 31 / 37) to just below the knee, where it splits into ten roots
	 * (calf 82 / 83 of their side) spreading and tapering to points on the ground.
	 */
	static ModelData robe(Style st, Body b) {
		Mesh me = new Mesh();
		Random rnd = new Random(32);
		int[] lb = b.legs.bounds();
		double cz = (lb[2] + lb[5]) / 2.0 + 1, hw = b.legsHW, sc = b.female ? 0.92 : 1;
		double knee = (b.knee[0][1] + b.knee[1][1]) / 2, shin = (b.shin[0][1] + b.shin[1][1]) / 2;
		double[][] rings = { { b.legsTop + 1, hw + 1.2, 11.5, 13 }, { b.hipJointY, hw + 3, 13, 14.5 }, { lerp(b.hipJointY, knee, 0.5), hw + 5, 14.5, 16 },
				{ knee, hw + 6.5 * sc, 16, 17.5 }, { shin, hw + 7.5 * sc, 17, 18.5 } };
		int n = 10, R = rings.length;
		int[][] g = new int[R][n];
		for (int r = 0; r < R; r++) {
			for (int i = 0; i < n; i++) {
				double th = 360.0 * i / n + 18;
				double rough = r >= 3 ? 1 + (rnd.nextDouble() - 0.5) * 0.08 : 1;
				double[] p = ell(0, rings[r][0], cz, rings[r][1] * rough, rings[r][2] * rough, rings[r][3] * rough, th);
				int s = p[0] < 0 ? 0 : 1;
				int lab = r == 0 ? WAIST : r == 1 ? (Math.abs(Math.sin(Math.toRadians(th))) > 0.6 ? HIP[s] : PELVIS) : r == 2 ? THIGH[s] : r == 3 ? KNEE[s] : SHIN[s];
				g[r][i] = me.v(p, lab);
			}
		}
		for (int r = 0; r + 1 < R; r++) {
			double[] ax = pt(0, (rings[r][0] + rings[r + 1][0]) / 2, cz);
			for (int i = 0; i < n; i++) {
				int j = (i + 1) % n;
				int col = r == 0 ? st.trimDark : r == R - 2 ? (i % 2 == 0 ? st.trim : st.trimDark) : (i * 7 + r * 3) % 5 == 0 ? st.light : st.main;
				quad(me, g[r][i], g[r][j], g[r + 1][j], g[r + 1][i], col, ax);
			}
		}
		// the inside of the skirt from the knee down, seen between the roots
		int[] l3 = new int[n], l4 = new int[n];
		for (int i = 0; i < n; i++) {
			l3[i] = me.v(lerp(pt(0, rings[3][0], cz), me.p(g[3][i]), 0.94), me.m.vertexSkins.get(g[4][i]) == SHIN[0] ? CALF[0] : CALF[1]);
			l4[i] = me.v(lerp(pt(0, rings[4][0], cz), me.p(g[4][i]), 0.94), me.m.vertexSkins.get(g[4][i]) == SHIN[0] ? CALF[0] : CALF[1]);
		}
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			lining(me, l3[i], l4[i], l4[j], l3[j], st.dark, pt(0, (rings[3][0] + rings[4][0]) / 2, cz));
		}
		// the roots: one from each column of the hem, thick where they leave the robe, spreading to the ground
		me.prio = 1;
		double ground = b.soleY - 1;
		for (int i = 0; i < n; i++) {
			double th = 360.0 * i / n + 18 + 18;
			double[] top = ell(0, shin - 2, cz, rings[4][1] - 1.5, rings[4][2] - 1.5, rings[4][3] - 1.5, th);
			int s = top[0] < 0 ? 0 : 1;
			double[] rad = norm(pt(top[0], 0, top[2] - cz)), tan = pt(-rad[2], 0, rad[0]);
			double drop = ground - top[1], wob = (rnd.nextDouble() - 0.5) * 3;
			double reach = (8 + rnd.nextDouble() * 6) * sc;
			double[][] p = { top, add(add(top, scale(rad, reach * 0.15)), add(scale(tan, wob), pt(0, drop * 0.4, 0))),
					add(add(top, scale(rad, reach * 0.45)), add(scale(tan, -wob * 0.5), pt(0, drop * 0.78, 0))), add(add(top, scale(rad, reach)), pt(0, drop, 0)) };
			double r0 = 4.6 * sc;
			int L = CALF[s];
			int[] labs = { L, L, L, L };
			BaseJellyfish.tubePath(me, p, new double[] { r0, r0 * 0.72, r0 * 0.48, 0 }, labs, 4, Math.toRadians(th) + Math.PI / 4,
					new int[] { st.trim, i % 2 == 0 ? st.trimDark : st.trim, st.trimDark });
			if (i % 4 == 1) { // a rootlet off the side
				double[] q = p[2];
				limb(me, q, add(add(q, scale(rad, 4 * sc)), add(scale(tan, 3), pt(0, ground - q[1], 0))), 1.4, 0, 3, 0.3, L, L, st.trimDark);
			}
		}
		// moss tufts and a flower at the hips
		me.prio = 2;
		flower(me, ell(0, b.hipJointY + 3, cz, rings[1][1] + 0.6, rings[1][2] + 0.6, rings[1][3] + 0.6, 330), norm(pt(-0.4, -0.2, -1)), 1.7, PELVIS, st);
		flower(me, ell(0, lerp(b.hipJointY, knee, 0.35), cz, rings[2][1] - 0.4, rings[2][2] - 0.4, rings[2][3] - 0.4, 40), norm(pt(0.5, -0.2, -1)), 1.5, THIGH[1], st);
		me.prio = 0;
		sideways(me);
		return me.done();
	}

	/** Keeps each leg's vertices on its own side of the middle (no two labels at one position once the client merges). */
	static void sideways(Mesh me) {
		java.util.Set<Integer> left = java.util.Set.of(43, 36, 35, 37, 82, 38, 40, 46, 47), right = java.util.Set.of(44, 33, 34, 31, 83, 32, 42, 45, 48);
		for (int v = 0; v < me.m.vertices.size(); v++) {
			int[] q = me.m.vertices.get(v);
			int l = me.m.vertexSkins.get(v);
			if (left.contains(l) && q[0] < 1) {
				q[0] = 1;
			} else if (right.contains(l) && q[0] > -1) {
				q[0] = -1;
			}
		}
	}

	/* ====================================================================== */
	/*                            BOOTS, GAUNTLETS                            */
	/* ====================================================================== */

	/** Root boots: a bark shaft (32 / 38), a bark-bound foot (heel 47 / 48) whose toes are four roots splaying out (45 / 46), one root back from the heel. */
	static ModelData boots(Style st, Body b) {
		Mesh me = new Mesh();
		Random rnd = new Random(33);
		for (int s = 0; s < 2; s++) {
			double sg = sx(s);
			double[] an = b.ankle[s];
			double fx = an[0], fz = an[2] + 0.5, ay = an[1], sole = b.soleY, toe = b.toeZ, sc = b.female ? 0.9 : 1;
			int n = 7;
			int[] s0 = gnarled(me, rnd, fx, ay - 4, fz, 6.2 * sc, 6.8 * sc, 6.8 * sc, n, 0, 0.12, ANKLE[s]);
			int[] s1 = gnarled(me, rnd, fx, sole - 8, fz, 6.4 * sc, 7.2 * sc, 7.2 * sc, n, 0, 0.1, ANKLE[s]);
			grain(me, s0, s1, st.trim, st.trimDark, pt(fx, (ay + sole) / 2, fz));
			double[][] ft = { { b.heelZ - 0.5, sole - 6.5, 6.0, 6.2 }, { b.heel[s][2], sole - 6.5, 7.0, 6.6 }, { lerp(b.heel[s][2], toe, 0.62), sole - 5, 6.6, 5.0 } };
			int[] labs = { ANKLE[s], HEEL[s], TOES[s] };
			int[][] fr = new int[ft.length][n];
			for (int k = 0; k < ft.length; k++) {
				for (int i = 0; i < n; i++) {
					double a = Math.PI * 2 * (i + 0.5) / n;
					fr[k][i] = me.v(pt(fx + Math.cos(a) * ft[k][2] * sc, ft[k][1] + Math.sin(a) * ft[k][3] * sc, ft[k][0]), labs[k]);
				}
			}
			for (int k = 0; k + 1 < ft.length; k++) {
				grain(me, fr[k], fr[k + 1], st.trimDark, st.trim, pt(fx, (ft[k][1] + ft[k + 1][1]) / 2, (ft[k][0] + ft[k + 1][0]) / 2));
			}
			me.fan(fr[0], pt(fx, ft[0][1], ft[0][0] + 1.2), ANKLE[s], st.trimDark, pt(fx, ft[0][1], ft[0][0] - 5));
			me.fan(fr[ft.length - 1], pt(fx, sole - 5, ft[2][0] - 2), TOES[s], st.trimDark, pt(fx, ft[2][1], ft[2][0] + 5));
			// the root-toes
			me.prio = 1;
			for (int k = 0; k < 4; k++) {
				double u = (k - 1.5) / 1.5;
				double[] base = pt(fx + u * 4 * sc, sole - 5.5, ft[2][0] + 1);
				double[] mid = add(base, pt(u * 4 * sc + sg * 0.5, 3, -6 * sc));
				double[] tip = add(mid, pt(u * 5 * sc, sole - mid[1] - 0.5, -5 * sc - (k == 1 || k == 2 ? 2 : 0)));
				limb(me, base, mid, 2.3 * sc, 1.5 * sc, 3, 0.4, TOES[s], TOES[s], k % 2 == 0 ? st.trim : st.trimDark);
				limb(me, mid, tip, 1.5 * sc, 0, 3, 0.4, TOES[s], TOES[s], st.trimDark);
			}
			double[] hb = pt(fx + sg * 2, sole - 5, b.heelZ - 1);
			limb(me, hb, add(hb, pt(sg * 4, 4.5, 6 * sc)), 2.0, 0, 3, 0.2, ANKLE[s], ANKLE[s], st.trimDark);
			me.prio = 0;
		}
		sideways(me);
		return me.done();
	}

	/** Twig gauntlets: a bark cuff with a ragged rim (19 / 22), a bark hand (27 / 28), four twig fingers and a twig thumb. */
	static ModelData gauntlets(Style st, Body b) {
		Mesh me = new Mesh();
		Random rnd = new Random(34);
		for (int s = 0; s < 2; s++) {
			double sg = sx(s);
			double[] el = b.elbow[s], wr = b.wrist[s], hd = b.hand[s];
			double bottom = b.handBottom, hrx = b.handR[s][0], hrz = b.handR[s][1], sc = b.female ? 0.86 : 1;
			int n = 6;
			double[] c0 = lerp(el, wr, 0.5);
			// the cuff: a flared, ragged rim up the forearm
			int[] rim = new int[n], cw = gnarled(me, rnd, wr[0], wr[1], wr[2], 5.2 * sc, 5.8 * sc, 5.8 * sc, n, 0, 0.1, FOREARM[s]);
			for (int i = 0; i < n; i++) {
				rim[i] = me.v(ell(c0[0], c0[1] - (i % 2) * 3, c0[2], 7.6 * sc, 7.8 * sc, 7.8 * sc, 360.0 * i / n), FOREARM[s]);
			}
			grain(me, rim, cw, st.trim, st.trimDark, pt(wr[0], (wr[1] + c0[1]) / 2, wr[2]));
			for (int i = 0; i < n; i++) {
				lining(me, rim[i], cw[i], cw[(i + 1) % n], rim[(i + 1) % n], st.leatherDark, pt(wr[0], (wr[1] + c0[1]) / 2, wr[2]));
			}
			// the hand
			int[] h0 = gnarled(me, rnd, hd[0], bottom - 11, hd[2], hrx + 1.0, hrz + 1.4, hrz + 1.4, n, 0, 0.08, HAND[s]);
			int[] h1 = gnarled(me, rnd, hd[0] + sg * 0.3, bottom - 5, hd[2], hrx + 0.6, hrz + 1.6, hrz + 1.6, n, 0, 0.08, HAND[s]);
			grain(me, cw, h0, st.trimDark, st.trim, pt(wr[0], (wr[1] + bottom - 11) / 2, wr[2]));
			grain(me, h0, h1, st.trim, st.trimDark, pt(hd[0], bottom - 8, hd[2]));
			me.fan(h1, pt(hd[0] + sg * 0.3, bottom - 2.5, hd[2]), HAND[s], st.trimDark, pt(hd[0], bottom - 9, hd[2]));
			// twig fingers, hanging from the knuckles and curling forward a little; the thumb in front
			me.prio = 1;
			for (int k = 0; k < 4; k++) {
				double z = hd[2] + (k - 1.5) * 2.2 * sc;
				double[] base = pt(hd[0] + sg * 0.3, bottom - 3.5, z);
				double[] mid = add(base, pt(sg * 0.4, 5 * sc, -1.2 - (k == 0 ? 0.6 : 0)));
				double[] tip = add(mid, pt(-sg * 0.3, 4.5 * sc, -2.4));
				limb(me, base, mid, 1.1, 0.8, 3, 0.3 + k, HAND[s], HAND[s], st.leather);
				limb(me, mid, tip, 0.8, 0, 3, 0.3 + k, HAND[s], HAND[s], st.trim);
			}
			double[] tb = pt(hd[0] - sg * 1.5, bottom - 9, hd[2] - hrz);
			limb(me, tb, add(tb, pt(-sg * 1.2, 6 * sc, -3.5)), 1.2, 0, 3, 0.2, HAND[s], HAND[s], st.leather);
			// a leaf on the back of the hand
			leaf(me, add(hd, pt(sg * (hrx + 0.6), -3, 1)), pt(0, 1, 0.4), pt(sg, 0, 0), 4, 1.6, HAND[s], LEAF, LEAF_DARK);
			me.prio = 0;
		}
		return me.done();
	}
}
