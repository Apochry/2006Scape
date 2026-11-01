package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ArmourForge.add;
import static com.rs2.tools.mapgen.ArmourForge.dot;
import static com.rs2.tools.mapgen.ArmourForge.norm;
import static com.rs2.tools.mapgen.ArmourForge.scale;
import static com.rs2.tools.mapgen.ArmourForge.sub;
import static com.rs2.tools.mapgen.BasePilgrim.ANKLE;
import static com.rs2.tools.mapgen.BasePilgrim.BELLY;
import static com.rs2.tools.mapgen.BasePilgrim.CHEST;
import static com.rs2.tools.mapgen.BasePilgrim.FORE;
import static com.rs2.tools.mapgen.BasePilgrim.HIPS;
import static com.rs2.tools.mapgen.BasePilgrim.SHIN;
import static com.rs2.tools.mapgen.BasePilgrim.SKULL;
import static com.rs2.tools.mapgen.BasePilgrim.THIGH;
import static com.rs2.tools.mapgen.BasePilgrim.UPPER;
import static com.rs2.tools.mapgen.BasePilgrim.across;
import static com.rs2.tools.mapgen.BasePilgrim.anchors;
import static com.rs2.tools.mapgen.BasePilgrim.copyKit;
import static com.rs2.tools.mapgen.BasePilgrim.fitted;
import static com.rs2.tools.mapgen.BasePilgrim.inner;
import static com.rs2.tools.mapgen.BasePilgrim.legAt;
import static com.rs2.tools.mapgen.BasePilgrim.lerp;
import static com.rs2.tools.mapgen.BasePilgrim.ring;
import static com.rs2.tools.mapgen.BasePilgrim.sx;
import static com.rs2.tools.mapgen.ModelData.hsl;

import java.io.IOException;
import java.util.function.ToIntFunction;

import com.rs2.tools.mapgen.ArmourForge.Frame;
import com.rs2.tools.mapgen.ArmourSets.Style;
import com.rs2.tools.mapgen.BaseMantle.Body;
import com.rs2.tools.mapgen.BasePilgrim.Mesh;

/**
 * The Saltgilt base (A27): the Drowned Maiden's officers, drowned and come back up still in their brass diving suits.
 * Built from scratch on the rune plate's rig (Kind.COAT); see NewBases.
 *
 * <ul>
 * <li>Full helm: a round brass diving helmet, its dome well bigger than the head, a round front porthole of dark sea
 * glass behind a grille, a smaller porthole either side, an air-hose stub with a nut at the back, a rubber gasket at
 * the neck, and a brass collar plate (the corselet's yoke) over the shoulders with a ring of bolts; a few barnacles.
 * Named "full helm", so the head and beard are hidden.</li>
 * <li>Platebody: a bulky canvas suit, the sleeves puffed in accordion folds, a gilt breastplate and backplate bolted on
 * over it (the bolts doubloon-gold), and a heavy belt of lead weights round the waist with a brass buckle. Named
 * "platebody", so the arms kit is hidden and the canvas sleeves are drawn.</li>
 * <li>Legs: baggy canvas trousers folded at the thigh and shin, round leather patches over the knees.</li>
 * <li>Boots: massive lead diving boots, much bigger than the feet: a thick sole, a squared brass toe-cap, a lead weight
 * strapped on the instep, a stout shaft to mid-shin with a brass rim.</li>
 * <li>Gloves: thick, swollen rubber gloves with flared cuffs and a brass ring at the wrist.</li>
 * <li>Shield: a round brass hatch: a riveted rim, a small porthole, a valve wheel at its heart, barnacles.</li>
 * </ul>
 *
 * <p>Labels follow {@link BasePilgrim}: each piece that hides a kit supplies the kit's origin labels with {@link
 * BasePilgrim#anchors}; visible geometry takes safe labels. The dome, its portholes and the hose are on the skull 1;
 * the collar plate and its bolts on the chest 8, so the helmet sits on the shoulders and the gasket between turns
 * with the head. The suit's torso is on the chest 8 / belly 4 by height, the belt and its weights on the belly; the
 * sleeves on the upper arms 20 / 26 and the forearms 19 / 22; the trousers on the still pelvis 41, the thighs 44 / 43,
 * the shins 33 / 36 and the ankles 32 / 38; the boots, rigid lead, all on the ankles 32 / 38; the gloves are the kit's
 * hands, swollen sideways (keeping their labels), the cuffs on the forearms. The shield is all on the
 * left hand 28, in the rune kite's place and plane, like every stock shield.
 */
final class BaseDiver {

	private BaseDiver() {
	}

	static final int BRASS = hsl(8, 4, 60), BRASS_LIGHT = hsl(8, 4, 78), BRASS_DARK = hsl(7, 4, 42), COPPER = hsl(5, 4, 50), VERDIGRIS = hsl(26, 2, 62),
			GLASS = hsl(30, 3, 18), GLASS_GLINT = hsl(30, 2, 60), LEAD = hsl(30, 0, 62), LEAD_DARK = hsl(30, 0, 42), RUBBER = hsl(36, 1, 22),
			BARNACLE = DraftArmourA.BARNACLE, BARNACLE_DARK = DraftArmourA.BARNACLE_DARK;

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		Body b = BaseMantle.body(af, female);
		switch (slot) {
		case ArmourSets.HELM:
			return new ModelData[] { helmet(af, st, b, female), null };
		case ArmourSets.BODY:
			return new ModelData[] { suit(st, b), null };
		case ArmourSets.LEGS:
			return new ModelData[] { trousers(st, b), null };
		case ArmourSets.SHIELD:
			return new ModelData[] { hatch(af, st), null };
		case ArmourSets.BOOTS:
			return new ModelData[] { boots(st, b), null };
		default:
			return new ModelData[] { gloves(st, b), null };
		}
	}

	/** The inventory model for the slot, or null to let ArmourSets build it the usual way from the worn model(s). */
	static ModelData inventory(ArmourForge af, Style st, int slot, ModelData[] worn) throws IOException {
		return null;
	}

	/* ====================================================================== */
	/*                                 PARTS                                  */
	/* ====================================================================== */

	/** A spherical cap's point: polar angle {@code pol} from the axis a (through c), azimuth {@code az}, radius r. */
	static double[] sph(double[] c, double[][] frame, double r, double pol, double az) {
		double[] a = frame[0], u = frame[1], w = frame[2];
		return add(c, add(scale(a, Math.cos(pol) * r), add(scale(u, Math.sin(pol) * Math.cos(az) * r), scale(w, Math.sin(pol) * Math.sin(az) * r))));
	}

	/**
	 * A porthole on a sphere (centre c, radius R) facing along d: a raised brass rim, dark glass set in it, a glint, and
	 * (if {@code bars}) a grille of three brass bars across it.
	 */
	static void porthole(Mesh me, double[] c, double R, double[] d, double r, boolean bars, int n, int label) {
		d = norm(d);
		double[][] uw = across(d);
		double[] p = add(c, scale(d, R - 0.6));
		int[] base = ring(me, p, d, r + 1.8, r + 1.8, n, 0, label), top = ring(me, add(p, scale(d, 2.6)), d, r + 0.9, r + 0.9, n, 0, label),
				glass = ring(me, add(p, scale(d, 1.6)), d, r, r, n, 0, label);
		me.tube(base, top, new int[] { BRASS, BRASS_DARK }, p, add(p, scale(d, 2.6)));
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			me.quadDir(top[i], top[j], glass[j], glass[i], BRASS_LIGHT, d);
		}
		int gc = me.v(add(p, scale(d, 1.4)), label);
		for (int i = 0; i < n; i++) {
			me.triDir(glass[i], glass[(i + 1) % n], gc, i == 1 ? GLASS_GLINT : GLASS, d);
		}
		if (bars) {
			int prio = me.prio;
			me.prio = prio + 1;
			for (int k = -1; k <= 1; k++) {
				boolean vert = k == 0;
				double[] along = vert ? uw[1] : uw[0], off = vert ? uw[0] : uw[1];
				double[] m0 = add(add(p, scale(d, 2.3)), scale(off, vert ? 0 : k * r * 0.45));
				double half = vert ? r : r * 0.88;
				int a0 = me.v(add(add(m0, scale(along, -half)), scale(off, -0.7)), label), a1 = me.v(add(add(m0, scale(along, half)), scale(off, -0.7)), label),
						a2 = me.v(add(add(m0, scale(along, half)), scale(off, 0.7)), label), a3 = me.v(add(add(m0, scale(along, -half)), scale(off, 0.7)), label);
				me.quadDir(a0, a1, a2, a3, BRASS, d);
			}
			me.prio = prio;
		}
	}

	/** A barnacle: a squat pale cone on the surface at p along n, its mouth dark. */
	static void barnacle(Mesh me, double[] p, double[] n, double r, int label) {
		n = norm(n);
		int[] b = ring(me, add(p, scale(n, -0.5)), n, r, r, 5, 0.3, label), t = ring(me, add(p, scale(n, r * 0.9)), n, r * 0.45, r * 0.45, 5, 0.3, label);
		me.tube(b, t, new int[] { BARNACLE, BARNACLE_DARK }, p, add(p, scale(n, r)));
		int m = me.v(add(p, scale(n, r * 0.6)), label);
		for (int i = 0; i < 5; i++) {
			me.triDir(t[i], t[(i + 1) % 5], m, BARNACLE_DARK, n);
		}
	}

	/** A bolt head: a little brass pyramid. */
	static void bolt(Mesh me, double[] p, double[] n, int label) {
		n = norm(n);
		int[] b = ring(me, p, n, 1.5, 1.5, 3, 0.5, label);
		int t = me.v(add(p, scale(n, 1.3)), label);
		for (int i = 0; i < 3; i++) {
			me.tri(b[i], b[(i + 1) % 3], t, i == 0 ? BRASS_DARK : BRASS_LIGHT, sub(p, n));
		}
	}

	/** The point of {@code onto} hit by the ray along z at (x, y) from the front (side -1) or the back (1). */
	static double[] onFace(ModelData onto, double x, double y, int side) {
		double[] h = ArmourForge.surface(onto, new double[] { x, y, 200 * side }, new double[] { 0, 0, -side });
		return h != null ? h : new double[] { x, y, side * 14 };
	}

	/**
	 * A plate laid over the front (side -1) or back (1) of {@code onto}: the outline (x, y) dropped onto it, {@code lift}
	 * proud, domed a little more towards its middle, with a rim band in {@code rim}; bolts at every outline point.
	 */
	static void plate(Mesh me, ModelData onto, double[][] outline, int side, double lift, int col, int rim, ToIntFunction<double[]> label) {
		int n = outline.length;
		double cx = 0, cy = 0;
		for (double[] o : outline) {
			cx += o[0] / n;
			cy += o[1] / n;
		}
		int[] out = new int[n], in = new int[n];
		double[] dir = { 0, 0, side };
		for (int i = 0; i < n; i++) {
			double[] q = onFace(onto, outline[i][0], outline[i][1], side);
			double[] qi = onFace(onto, lerp(outline[i][0], cx, 0.22), lerp(outline[i][1], cy, 0.22), side);
			out[i] = me.v(add(q, scale(dir, lift)), label.applyAsInt(q));
			in[i] = me.v(add(qi, scale(dir, lift + 0.9)), label.applyAsInt(qi));
		}
		double[] qc = onFace(onto, cx, cy, side);
		int c = me.v(add(qc, scale(dir, lift + 1.6)), label.applyAsInt(qc));
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			me.quadDir(out[i], out[j], in[j], in[i], rim, dir);
			me.triDir(in[i], in[j], c, i % 2 == 0 ? col : BRASS_LIGHT, dir);
		}
		int prio = me.prio;
		me.prio = prio + 1;
		for (int i = 0; i < n; i++) {
			double[] q = me.p(out[i]);
			bolt(me, add(q, scale(dir, 0.3)), dir, label.applyAsInt(q));
		}
		me.prio = prio;
	}

	/* ====================================================================== */
	/*                                THE HELMET                              */
	/* ====================================================================== */

	/**
	 * The diving helmet: a sphere well round the head, open at the bottom over a rubber gasket at the neck, the brass
	 * collar plate on the shoulders below it with its ring of bolts, the portholes, the air-hose stub, some barnacles
	 * and a patch or two of verdigris.
	 */
	static ModelData helmet(ArmourForge af, Style st, Body b, boolean female) throws IOException {
		Mesh me = new Mesh();
		double hTop = b.headTop, chin = b.headChin;
		double hcz = (b.headFront + b.headBack) / 2;
		double R = (chin - hTop) / 2 + (b.female ? 9.5 : 9);
		double[] c = { 0, (hTop + chin) / 2 + 2.5, hcz + 1.5 };
		double[][] fr = { { 0, -1, 0 }, { 1, 0, 0 }, { 0, 0, -1 } }; // the axis up; azimuth 0 to the left (+x), 90 to the front
		int az = 10;
		double[] pols = { 0.5, 1.0, 1.55, 2.05, 2.45 };
		int[][] g = new int[pols.length][az];
		for (int k = 0; k < pols.length; k++) {
			for (int i = 0; i < az; i++) {
				g[k][i] = me.v(sph(c, fr, R, pols[k], Math.PI * 2 * i / az + 0.13), SKULL);
			}
		}
		int apex = me.v(sph(c, fr, R, 0, 0), SKULL);
		me.prio = 5;
		for (int i = 0; i < az; i++) {
			me.tri(apex, g[0][i], g[0][(i + 1) % az], BRASS_LIGHT, c);
		}
		for (int k = 0; k + 1 < pols.length; k++) {
			for (int i = 0; i < az; i++) {
				int j = (i + 1) % az;
				// the brass catches the light on the upper dome; tarnish and verdigris low at the back
				int col = k == 0 ? BRASS_LIGHT : k >= 3 ? BRASS_DARK : BRASS;
				if (k == 3 && (i == 6 || i == 7)) {
					col = VERDIGRIS;
				}
				me.quad(g[k][i], g[k][j], g[k + 1][j], g[k + 1][i], col, c);
			}
		}
		// the gasket from the dome's mouth to the collar
		int last = pols.length - 1;
		double neckY = b.neckY + 1.5;
		int[] gk = new int[az];
		for (int i = 0; i < az; i++) {
			double a = Math.PI * 2 * i / az + 0.13;
			gk[i] = me.v(Math.cos(a) * (b.neckR + 4.5), neckY, b.neckZ + 0.5 - Math.sin(a) * (b.neckR + 4.5), CHEST);
		}
		me.prio = 4;
		me.band(g[last], gk, true, new int[] { RUBBER }, 0, c[2], false);
		// the collar plate: a broad brass yoke on the shoulders, a rim, a ring of bolts
		double ox = b.torsoHW + 9, ozF = -b.chestZ + 7, ozB = b.backZ + 6, oy = b.shoulderTop + 6;
		int[] y0 = new int[az], y1 = new int[az], y2 = new int[az];
		for (int i = 0; i < az; i++) {
			double a = Math.PI * 2 * i / az + 0.13, sa = Math.sin(a);
			double rz = sa > 0 ? ozF : ozB;
			y0[i] = me.v(Math.cos(a) * (b.neckR + 4.5), neckY + 0.5, b.neckZ + 0.5 - sa * (b.neckR + 4.5), CHEST);
			y1[i] = me.v(Math.cos(a) * ox, oy, -sa * rz, CHEST);
			y2[i] = me.v(Math.cos(a) * (ox - 0.4), oy + 3.0, -sa * (rz - 0.4), CHEST);
		}
		me.prio = 5;
		me.band(y0, y1, true, new int[] { BRASS, BRASS_LIGHT }, 0, 0, false);
		me.band(y1, y2, true, new int[] { BRASS_DARK }, 0, 0, false);
		me.prio = 6;
		for (int i = 0; i < az; i++) {
			double a = Math.PI * 2 * (i + 0.5) / az + 0.13, sa = Math.sin(a);
			if (i % 5 == 2) {
				continue; // none under the hose and the porthole
			}
			double t = 0.62, rz = sa > 0 ? ozF : ozB;
			double[] p = { Math.cos(a) * lerp(b.neckR + 4.5, ox, t), lerp(neckY + 0.5, oy, t) - 0.9, lerp(b.neckZ + 0.5, 0, t) - sa * lerp(b.neckR + 4.5, rz, t) };
			double[] n = norm(new double[] { Math.cos(a) * 0.4, -1, -sa * 0.4 });
			bolt(me, p, n, CHEST);
		}
		// portholes: the big one in front behind its grille, a smaller one either side
		me.prio = 7;
		porthole(me, c, R, new double[] { 0, 0.08, -1 }, b.female ? 6.0 : 6.6, true, 8, SKULL);
		for (int s = 0; s < 2; s++) {
			porthole(me, c, R, new double[] { sx(s), 0.05, -0.12 }, 3.9, false, 6, SKULL);
		}
		// the air-hose stub at the back, a nut on it, the cut hose
		double[] hd = norm(new double[] { 0.25, -0.45, 1 });
		double[] h0 = add(c, scale(hd, R - 1)), h1 = add(c, scale(hd, R + 3.5)), h2 = add(c, scale(hd, R + 6)), h3 = add(c, scale(hd, R + 9.5));
		int[] r0 = ring(me, h0, hd, 2.6, 2.6, 5, 0, SKULL), r1 = ring(me, h1, hd, 3.8, 3.8, 5, 0, SKULL), r2 = ring(me, h2, hd, 3.8, 3.8, 5, 0, SKULL),
				r3 = ring(me, h3, hd, 2.3, 2.3, 5, 0, SKULL);
		me.tube(r0, r1, new int[] { BRASS }, h0, h1);
		me.tube(r1, r2, new int[] { BRASS_DARK, BRASS_LIGHT }, h1, h2);
		me.tube(r2, r3, new int[] { RUBBER }, h2, h3);
		int hc = me.v(h3, SKULL);
		for (int i = 0; i < 5; i++) {
			me.triDir(r3[i], r3[(i + 1) % 5], hc, LEAD_DARK, hd);
		}
		// barnacles crusted low on the dome, at the back and the right
		me.prio = 8;
		double[][] bs = { { 2.15, 3.6, 1.8 }, { 1.95, 4.05, 1.3 }, { 2.2, 2.5, 1.5 } };
		for (double[] q : bs) {
			double[] p = sph(c, fr, R, q[0], q[1]);
			barnacle(me, p, sub(p, c), q[2], SKULL);
		}
		anchors(me, b.head, BRASS);
		int jaw = (female ? ArmourPreview.FEMALE : ArmourPreview.MALE)[1];
		if (jaw >= 0) {
			anchors(me, af.read(jaw), BRASS);
		}
		return me.done();
	}

	/* ====================================================================== */
	/*                                 THE SUIT                               */
	/* ====================================================================== */

	static ModelData suit(Style st, Body b) {
		Mesh me = new Mesh();
		ModelData trunk = ArmourForge.mergeAll(b.torso, b.legs);
		double cz = (b.chestZ + b.backZ) / 2, L = b.waistY - b.neckY;
		// ------------------------------------------------ the canvas torso, bulky
		double[][] rows = { { b.neckY + 1.5, 2.5, CHEST }, { b.neckY + L * 0.32, 4.2, CHEST }, { b.neckY + L * 0.68, 4.6, BELLY }, { b.waistY, 4.4, BELLY },
				{ b.waistY + 9, 5.0, BELLY } };
		int n = 10;
		int[][] tr = new int[rows.length][];
		for (int r = 0; r < rows.length; r++) {
			tr[r] = fitted(me, trunk, rows[r][0], cz, n, rows[r][1], b.neckR, (int) rows[r][2]);
		}
		me.prio = 1;
		for (int r = 0; r + 1 < rows.length; r++) {
			me.band(tr[r], tr[r + 1], true, new int[] { st.main, st.light }, 0, cz, false);
		}
		ModelData canvas = BaseWrap.compact(me.m);
		ToIntFunction<double[]> byHeight = p -> p[1] < b.neckY + L * 0.5 ? CHEST : BELLY;
		// ------------------------------------------------ the sleeves: puffed, folded like a bellows
		for (int s = 0; s < 2; s++) {
			double sc = b.female ? 0.85 : 1;
			double[] sh = b.shoulder[s], el = b.elbow[s], wr = b.wrist[s];
			double[] up = norm(sub(el, sh)), fo = norm(sub(wr, el));
			double[][] c = { add(sh, new double[] { -sx(s) * 0.5, -2.5, 0 }), lerp(sh, el, 0.35), lerp(sh, el, 0.62), el, lerp(el, wr, 0.35), lerp(el, wr, 0.62),
					lerp(el, wr, 0.95) };
			double[][] ax = { up, up, up, norm(add(up, fo)), fo, fo, fo };
			double[] rad = { Math.max(b.shoulderR[s][0], b.shoulderR[s][1]) + 3.2, 9.0 * sc, 7.4 * sc, 9.0 * sc, 8.4 * sc, 7.0 * sc, 7.6 * sc };
			int[] labs = { UPPER[s], UPPER[s], UPPER[s], UPPER[s], FORE[s], FORE[s], FORE[s] };
			int[][] rg = new int[c.length][];
			for (int k = 0; k < c.length; k++) {
				rg[k] = ring(me, c[k], ax[k], rad[k], rad[k], 7, 0.2, labs[k]);
			}
			for (int k = 0; k + 1 < c.length; k++) {
				me.tube(rg[k], rg[k + 1], new int[] { k % 2 == 0 ? st.main : st.dark }, c[k], c[k + 1]);
			}
		}
		// ------------------------------------------------ the gilt breast- and backplates, bolted on
		me.prio = 3;
		double hw = b.torsoHW;
		double[][] breast = { { 0, b.neckY + 3 }, { hw * 0.6, b.neckY + 4 }, { hw * 0.98, b.neckY + L * 0.24 }, { hw * 0.9, b.neckY + L * 0.5 }, { hw * 0.55, b.neckY + L * 0.68 },
				{ 0, b.neckY + L * 0.76 }, { -hw * 0.55, b.neckY + L * 0.68 }, { -hw * 0.9, b.neckY + L * 0.5 }, { -hw * 0.98, b.neckY + L * 0.24 },
				{ -hw * 0.6, b.neckY + 4 } };
		plate(me, canvas, breast, -1, 1.3, st.trim, st.trimDark, byHeight);
		double[][] back = { { 0, b.neckY + 3 }, { hw * 0.75, b.neckY + 4 }, { hw * 0.95, b.neckY + L * 0.35 }, { hw * 0.7, b.neckY + L * 0.6 }, { 0, b.neckY + L * 0.66 },
				{ -hw * 0.7, b.neckY + L * 0.6 }, { -hw * 0.95, b.neckY + L * 0.35 }, { -hw * 0.75, b.neckY + 4 } };
		plate(me, canvas, back, 1, 1.3, st.trim, st.trimDark, byHeight);
		// a doubloon-gold boss in the middle of the breastplate, and barnacles crusted on the backplate's corner
		me.prio = 5;
		double[] boss = onFace(canvas, 0, b.neckY + L * 0.4, -1);
		int[] bo = ring(me, add(boss, new double[] { 0, 0, -3.4 }), new double[] { 0, 0, -1 }, 3.6, 3.6, 8, 0, CHEST),
				bi = ring(me, add(boss, new double[] { 0, 0, -4.4 }), new double[] { 0, 0, -1 }, 2.2, 2.2, 8, 0, CHEST);
		int bm = me.v(add(boss, new double[] { 0, 0, -4.8 }), CHEST);
		for (int i = 0; i < 8; i++) {
			int j = (i + 1) % 8;
			me.quadDir(bo[i], bo[j], bi[j], bi[i], st.trimDark, new double[] { 0, 0, -1 });
			me.triDir(bi[i], bi[j], bm, i % 2 == 0 ? BRASS_LIGHT : st.trim, new double[] { 0, 0, -1 });
		}
		for (int k = 0; k < 3; k++) {
			double[] p = onFace(canvas, -hw * (0.55 + k * 0.12), b.neckY + L * (0.42 + (k % 2) * 0.1), 1);
			barnacle(me, add(p, new double[] { 0, 0, 2.0 }), new double[] { -0.2, -0.2, 1 }, k == 1 ? 1.4 : 1.9, CHEST);
		}
		// ------------------------------------------------ the weight belt: leather, lead blocks round it, a brass buckle
		me.prio = 4;
		int[] w0 = fitted(me, trunk, b.waistY - 3.5, cz, 12, 5.4, b.neckR, BELLY), w1 = fitted(me, trunk, b.waistY + 3.5, cz, 12, 5.8, b.neckR, BELLY);
		me.band(w0, w1, true, new int[] { st.leather }, 0, cz, false);
		for (int k = 0; k < 7; k++) {
			double th = 40 + 280.0 * k / 6;
			double a = Math.toRadians(th);
			double[] h = BaseMantle.hit(trunk, b.waistY, cz, th);
			double rad = (h == null ? 14 : Math.hypot(h[0], h[2] - cz)) + 6.2;
			double[] o = { Math.sin(a), 0, -Math.cos(a) }, t = { Math.cos(a), 0, Math.sin(a) };
			double[] p = { o[0] * rad, b.waistY + 1, cz + o[2] * rad };
			block(me, p, o, t, 3.4, 4.6, 2.2, LEAD, LEAD_DARK, BELLY);
		}
		double[] bk = onFace(me.m, 0, b.waistY, -1);
		block(me, add(bk, new double[] { 0, 0, -0.6 }), new double[] { 0, 0, -1 }, new double[] { 1, 0, 0 }, 3.6, 3.6, 1.0, BRASS, BRASS_DARK, BELLY);
		anchors(me, b.torso, st.main);
		anchors(me, b.arms, st.main);
		return me.done();
	}

	/** A box on a surface: centre p, out o (its front), along t; half-sizes (along t, up, out). */
	static void block(Mesh me, double[] p, double[] o, double[] t, double hx, double hy, double hz, int col, int col2, int label) {
		double[] up = { 0, -1, 0 };
		int[] v = new int[8];
		for (int k = 0; k < 8; k++) {
			double[] q = add(p, add(scale(t, (k & 1) == 0 ? -hx : hx), add(scale(up, (k & 2) == 0 ? -hy : hy), scale(o, (k & 4) == 0 ? -hz : hz))));
			v[k] = me.v(q, label);
		}
		int[][] faces = { { 4, 5, 7, 6 }, { 0, 1, 5, 4 }, { 2, 3, 7, 6 }, { 0, 2, 6, 4 }, { 1, 3, 7, 5 } };
		for (int f = 0; f < faces.length; f++) {
			int[] q = faces[f];
			me.quad(v[q[0]], v[q[1]], v[q[2]], v[q[3]], f == 0 ? col : col2, p);
		}
	}

	/* ====================================================================== */
	/*                                 THE LEGS                               */
	/* ====================================================================== */

	/** Baggy canvas trousers, folded at the thigh and shin, leather patches over the knees. */
	static ModelData trousers(Style st, Body b) {
		Mesh me = new Mesh();
		int n = 8;
		for (int s = 0; s < 2; s++) {
			double kneeY = b.knee[s][1], ankY = b.ankle[s][1];
			double[][] rows = { { b.waistY + 1, 3.0, HIPS }, { b.hipJointY + 2, 4.6, THIGH[s] }, { lerp(b.hipJointY, kneeY, 0.45), 3.4, THIGH[s] },
					{ kneeY - 4, 4.4, THIGH[s] }, { kneeY + 5, 4.4, SHIN[s] }, { lerp(kneeY, ankY, 0.5), 3.0, SHIN[s] }, { ankY - 4, 3.8, SHIN[s] }, { ankY + 2, 2.2, ANKLE[s] } };
			int[][] rg = new int[rows.length][];
			double[][] q = new double[rows.length][];
			for (int r = 0; r < rows.length; r++) {
				q[r] = legAt(b, rows[r][0], s, rows[r][1]);
				if (r == 0) {
					q[r][0] = sx(s) * Math.max(Math.abs(q[r][0]), 8.5);
					q[r][2] = Math.max(q[r][2], 11);
				} else {
					inner(q[r], s, 1.3);
				}
				rg[r] = new int[n];
				for (int j = 0; j < n; j++) {
					double a = Math.PI * 2 * j / n + Math.PI / n;
					rg[r][j] = me.v(q[r][0] + Math.sin(a) * q[r][2], rows[r][0], q[r][1] - Math.cos(a) * q[r][3], (int) rows[r][2]);
				}
			}
			me.prio = 1;
			for (int r = 0; r + 1 < rows.length; r++) {
				me.band(rg[r], rg[r + 1], true, r == 0 ? new int[] { st.leather } : new int[] { r % 2 == 0 ? st.dark : st.main, st.main },
						(q[r][0] + q[r + 1][0]) / 2, (q[r][1] + q[r + 1][1]) / 2, false);
			}
			// the knee patch: a stitched leather disc on the front of the knee
			double[] kq = legAt(b, kneeY, s, 4.6);
			double[] kc = { kq[0], kneeY, kq[1] - kq[3] - 0.4 };
			double[] kn = norm(new double[] { sx(s) * 0.12, 0, -1 });
			int[] p0 = ring(me, kc, kn, 4.6, 5.4, 7, 0, THIGH[s]), p1 = ring(me, add(kc, scale(kn, 0.9)), kn, 3.6, 4.4, 7, 0, THIGH[s]);
			int pm = me.v(add(kc, scale(kn, 1.2)), THIGH[s]);
			me.prio = 2;
			for (int i = 0; i < 7; i++) {
				int j = (i + 1) % 7;
				me.quadDir(p0[i], p0[j], p1[j], p1[i], st.leatherDark, kn);
				me.triDir(p1[i], p1[j], pm, st.leather, kn);
			}
		}
		anchors(me, b.legs, st.main);
		return me.done();
	}

	/* ====================================================================== */
	/*                             BOOTS, GLOVES                              */
	/* ====================================================================== */

	/**
	 * Lead diving boots, much bigger than the foot: a thick sole, a blunt box of a foot with a squared brass toe-cap, a
	 * lead weight strapped over the instep, a stout shaft to mid-shin under a brass rim.
	 */
	static ModelData boots(Style st, Body b) {
		Mesh me = new Mesh();
		for (int s = 0; s < 2; s++) {
			double sg = sx(s);
			double x0 = 1e9, x1 = -1e9, z0 = 1e9, z1 = -1e9;
			for (int[] v : b.feet.vertices) {
				if (v[0] * sg > 0) {
					x0 = Math.min(x0, v[0]);
					x1 = Math.max(x1, v[0]);
					z0 = Math.min(z0, v[2]);
					z1 = Math.max(z1, v[2]);
				}
			}
			double hw = (x1 - x0) / 2 + 4.6, sole = b.soleY + 2.5, ankY = b.ankle[s][1];
			double cx = sg * Math.max(Math.abs((x0 + x1) / 2) + 1.2, hw * 1.04 + 1.6); // never across the middle (the boots would weld)
			// sections along the foot, heel to toe: z, top y, half-width
			double[][] sec = { { z1 + 5, sole - 16, hw * 0.9 }, { lerp(z0, z1, 0.55), sole - 17, hw }, { z0 + 1, sole - 12, hw * 1.04 }, { z0 - 7, sole - 9, hw * 0.95 } };
			int[][] sx5 = new int[sec.length][];
			for (int k = 0; k < sec.length; k++) {
				double z = sec[k][0], top = sec[k][1], h = sec[k][2];
				int lab = ANKLE[s]; // lead doesn't bend: the whole boot on the ankle
				sx5[k] = new int[] { me.v(cx - h, sole, z, lab), me.v(cx + h, sole, z, lab), me.v(cx + h, top + 2.5, z, lab), me.v(cx + h * 0.6, top, z, lab),
						me.v(cx - h * 0.6, top, z, lab), me.v(cx - h, top + 2.5, z, lab) };
			}
			me.prio = 2;
			for (int k = 0; k + 1 < sec.length; k++) {
				double[] in = { cx, sole - 6, (sec[k][0] + sec[k + 1][0]) / 2 };
				for (int e = 0; e < 6; e++) {
					int e2 = (e + 1) % 6;
					int col = e == 0 ? LEAD_DARK : k == sec.length - 2 ? (e == 3 ? BRASS_LIGHT : BRASS) : (e == 3 ? LEAD : LEAD_DARK);
					me.quad(sx5[k][e], sx5[k][e2], sx5[k + 1][e2], sx5[k + 1][e], col, in);
				}
			}
			int lastS = sec.length - 1;
			for (int e = 1; e + 1 < 6; e++) {
				me.triDir(sx5[lastS][0], sx5[lastS][e], sx5[lastS][e + 1], BRASS_DARK, new double[] { 0, 0, -1 });
				me.triDir(sx5[0][0], sx5[0][e], sx5[0][e + 1], LEAD_DARK, new double[] { 0, 0, 1 });
			}
			// the sole: a thick lip round the foot's bottom
			// the weight over the instep, strapped
			me.prio = 3;
			double iz = lerp(z0, z1, 0.4);
			block(me, new double[] { cx, sole - 17.5, iz }, new double[] { 0, -1, 0.15 }, new double[] { 1, 0, 0 }, hw * 0.75, 3.6, 1.6, LEAD, LEAD_DARK, ANKLE[s]);
			// the shaft: from inside the foot up to mid-shin, its brass rim
			double[] ys = { sole - 13, ankY - 3, ankY - 15 };
			double[] mg = { 3.6, 3.8, 4.2 };
			int n = 7;
			int[][] rg = new int[ys.length][];
			double[][] q = new double[ys.length][];
			for (int r = 0; r < ys.length; r++) {
				if (r == 0) {
					q[r] = new double[] { cx, (z0 + z1) / 2 + 3, hw * 0.85, (z1 - z0) / 2 - 2 };
				} else {
					q[r] = inner(legAt(b, ys[r], s, mg[r]), s, 1.2);
				}
				rg[r] = new int[n];
				for (int j = 0; j < n; j++) {
					double a = Math.PI * 2 * j / n;
					rg[r][j] = me.v(q[r][0] + Math.sin(a) * q[r][2], ys[r], q[r][1] - Math.cos(a) * q[r][3], ANKLE[s]);
				}
			}
			me.prio = 2;
			for (int r = 0; r + 1 < ys.length; r++) {
				me.band(rg[r], rg[r + 1], true, new int[] { r == 0 ? LEAD_DARK : st.leatherDark, st.leather }, (q[r][0] + q[r + 1][0]) / 2, (q[r][1] + q[r + 1][1]) / 2,
						false);
			}
			int lr = ys.length - 1;
			double[] rq = inner(new double[] { q[lr][0], q[lr][1], q[lr][2] + 1.4, q[lr][3] + 1.4 }, s, 1.0);
			int[] rim0 = new int[n], rim1 = new int[n];
			for (int j = 0; j < n; j++) {
				double a = Math.PI * 2 * j / n;
				rim0[j] = me.v(rq[0] + Math.sin(a) * rq[2], ys[lr] + 3.5, rq[1] - Math.cos(a) * rq[3], ANKLE[s]);
				rim1[j] = me.v(rq[0] + Math.sin(a) * rq[2], ys[lr] - 0.6, rq[1] - Math.cos(a) * rq[3], ANKLE[s]);
			}
			me.prio = 3;
			me.band(rim0, rim1, true, new int[] { BRASS, BRASS_LIGHT }, rq[0], rq[1], false);
		}
		anchors(me, b.feet, LEAD_DARK);
		return me.done();
	}

	/** Thick rubber gloves (the kit's hands, swollen) and flared cuffs with a brass wrist ring. */
	static ModelData gloves(Style st, Body b) {
		Mesh me = new Mesh();
		// the hands swollen sideways (x and z about each hand's centre, not along it), which keeps every vertex apart
		ModelData fat = ArmourForge.copyModel(b.hands);
		fat.transform(v -> {
			double[] h = b.hand[v[0] < 0 ? 0 : 1];
			return new double[] { h[0] + (v[0] - h[0]) * 1.4, v[1], h[2] + (v[2] - h[2]) * 1.4 };
		});
		copyKit(me, fat, (c, o) -> RUBBER, 1);
		for (int s = 0; s < 2; s++) {
			double[] w = b.wrist[s], e = b.elbow[s];
			double[] ax = norm(sub(e, w));
			double sc = b.female ? 0.85 : 1;
			double[][] c = { add(w, scale(ax, -1)), add(w, scale(ax, 2)), add(w, scale(ax, 4)), add(w, scale(ax, 10)), add(w, scale(ax, 11.5)) };
			double[] rad = { 6.6 * sc, 6.6 * sc, 6.9 * sc, 9.4 * sc, 8.6 * sc };
			int[][] rg = new int[c.length][];
			for (int k = 0; k < c.length; k++) {
				rg[k] = ring(me, c[k], ax, rad[k], rad[k], 7, 0, FORE[s]);
			}
			me.prio = 3;
			int[][] cols = { { BRASS, BRASS_LIGHT }, { BRASS_DARK }, { st.leather, st.leatherDark }, { st.leatherDark } };
			for (int k = 0; k + 1 < c.length; k++) {
				me.tube(rg[k], rg[k + 1], cols[k], c[k], c[k + 1]);
			}
		}
		return me.done();
	}

	/* ====================================================================== */
	/*                                THE HATCH                               */
	/* ====================================================================== */

	/**
	 * A round brass hatch in the rune kite's place and plane: a heavy rim with bolts, a slightly domed face, a small
	 * porthole above the middle, a valve wheel (rim, four spokes, hub) at the heart, barnacles on the lower edge.
	 */
	static ModelData hatch(ArmourForge af, Style st) throws IOException {
		ModelData kite = af.read(ArmourForge.RUNE_KITE_WORN);
		Frame k = ArmourForge.shieldFrame(kite, 61);
		double[] nn = k.n, down = norm(sub(new double[] { 0, 1, 0 }, scale(nn, dot(new double[] { 0, 1, 0 }, nn)))), acr = norm(ModelData.cross(nn, down));
		double[] c = add(k.o, scale(nn, 1.0));
		Mesh me = new Mesh();
		int L = 28, n = 10;
		double R = 24;
		// radius, height over the face plane
		double[][] prof = { { 0, 3.0 }, { 12, 1.8 }, { 19.5, 1.2 }, { 21.5, 3.2 }, { 23.5, 3.2 }, { R, 0.6 }, { R, -2.4 }, { 0, -2.6 } };
		int[][] rg = new int[prof.length][];
		for (int q = 0; q < prof.length; q++) {
			rg[q] = new int[n];
			for (int j = 0; j < n; j++) {
				double a = Math.PI * 2 * j / n + Math.PI / n;
				rg[q][j] = me.v(add(c, add(scale(nn, prof[q][1]), add(scale(down, Math.cos(a) * prof[q][0]), scale(acr, Math.sin(a) * prof[q][0])))), L);
			}
		}
		me.prio = 10;
		double[] behind = add(c, scale(nn, -14));
		int[] cols = { BRASS_LIGHT, BRASS, BRASS_DARK, BRASS_LIGHT, BRASS, BRASS_DARK, LEAD_DARK };
		for (int q = 0; q + 1 < prof.length; q++) {
			for (int j = 0; j < n; j++) {
				int jj = (j + 1) % n;
				int col = q < cols.length ? cols[q] : LEAD_DARK;
				if (q == 0 || q == 1) {
					col = j % 2 == 0 ? BRASS : BRASS_LIGHT;
				}
				double[] ref = q >= prof.length - 2 ? add(c, scale(nn, 6)) : behind;
				if (q == 0) {
					me.tri(rg[0][j], rg[1][jj], rg[1][j], col, ref);
				} else if (q == prof.length - 2) {
					me.tri(rg[q][j], rg[q][jj], rg[q + 1][j], LEAD_DARK, ref);
				} else {
					me.quad(rg[q][j], rg[q][jj], rg[q + 1][jj], rg[q + 1][j], col, ref);
				}
			}
		}
		// bolts round the rim
		me.prio = 11;
		for (int j = 0; j < n; j++) {
			double a = Math.PI * 2 * j / n;
			double[] p = add(c, add(scale(nn, 3.4), add(scale(down, Math.cos(a) * 22.5), scale(acr, Math.sin(a) * 22.5))));
			bolt(me, p, nn, L);
		}
		// the valve wheel at the heart: a ring on four spokes round a hub
		double[] wc = add(c, scale(nn, 6.5));
		int wn = 10;
		int[] wo = new int[wn], wi = new int[wn], wb = new int[wn];
		for (int j = 0; j < wn; j++) {
			double a = Math.PI * 2 * j / wn;
			double[] dirA = add(scale(down, Math.cos(a)), scale(acr, Math.sin(a)));
			wo[j] = me.v(add(wc, scale(dirA, 9.5)), L);
			wi[j] = me.v(add(wc, scale(dirA, 7.5)), L);
			wb[j] = me.v(add(add(wc, scale(dirA, 8.5)), scale(nn, -1.6)), L);
		}
		me.prio = 12;
		for (int j = 0; j < wn; j++) {
			int jj = (j + 1) % wn;
			me.quadDir(wo[j], wo[jj], wi[jj], wi[j], j % 2 == 0 ? BRASS_LIGHT : BRASS, nn);
			me.tri(wo[j], wb[j], wo[jj], BRASS_DARK, wc);
			me.tri(wo[jj], wb[j], wb[jj], BRASS_DARK, wc);
		}
		for (int k2 = 0; k2 < 4; k2++) {
			double a = Math.PI / 2 * k2 + Math.PI / 4;
			double[] dirA = add(scale(down, Math.cos(a)), scale(acr, Math.sin(a))), side = add(scale(down, -Math.sin(a)), scale(acr, Math.cos(a)));
			int a0 = me.v(add(add(wc, scale(dirA, 1.5)), scale(side, -1.0)), L), a1 = me.v(add(add(wc, scale(dirA, 7.8)), scale(side, -1.0)), L),
					a2 = me.v(add(add(wc, scale(dirA, 7.8)), scale(side, 1.0)), L), a3 = me.v(add(add(wc, scale(dirA, 1.5)), scale(side, 1.0)), L);
			me.quadDir(a0, a1, a2, a3, BRASS, nn);
		}
		int[] hub = ring(me, add(wc, scale(nn, -3.5)), nn, 2.8, 2.8, 6, 0, L), hubT = ring(me, add(wc, scale(nn, 0.8)), nn, 2.4, 2.4, 6, 0, L);
		me.tube(hub, hubT, new int[] { BRASS_DARK }, add(wc, scale(nn, -3.5)), add(wc, scale(nn, 0.8)));
		int hm = me.v(add(wc, scale(nn, 1.6)), L);
		for (int j = 0; j < 6; j++) {
			me.triDir(hubT[j], hubT[(j + 1) % 6], hm, BRASS_LIGHT, nn);
		}
		// a small porthole above the wheel, and barnacles on the lower rim
		me.prio = 11;
		double[] pc = add(add(c, scale(down, -14.5)), scale(nn, -20));
		porthole(me, pc, 22.6, nn, 3.4, false, 8, L);
		for (int j = 0; j < 3; j++) {
			double a = 0.35 + j * 0.4;
			double rr = 20.5 - (j % 2) * 2.2;
			double[] p = add(c, add(scale(nn, 2.6), add(scale(down, Math.cos(a) * rr), scale(acr, Math.sin(a) * rr))));
			barnacle(me, p, add(nn, scale(down, 0.2)), j % 2 == 0 ? 2.0 : 1.5, L);
		}
		return me.done();
	}
}
