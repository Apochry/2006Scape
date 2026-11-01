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
import static com.rs2.tools.mapgen.ModelData.hsl;

import java.io.IOException;

import com.rs2.tools.mapgen.BaseCoat.Mesh;
import com.rs2.tools.mapgen.BaseCoat.Rig;
import com.rs2.tools.mapgen.ArmourSets.Style;

/**
 * The Rimeclaw hunter base: an arctic hunter's parka in Rimeclaw's ice-blue hide, round, bundled and puffy. Built from
 * scratch on the leather's rig for the Rimeclaw hunter (A34) draft set; see NewBases.
 *
 * <ul>
 * <li>Hood (named "... hood", so the head kit is hidden): a deep tunnel hood round the head, closed behind, its mouth
 * framed by a huge white fur ruff (a fat jagged halo about twice the face's size, frost on its tips), and under it the
 * face, copied from the head identity kit (in skin colour, which the client turns into the player's own), wearing
 * bone snow-goggles: an ivory plate across the eyes with one dark slit.</li>
 * <li>Top (named "... top", so the bare arms are hidden: the parka has its own sleeves): a bulky quilted parka, puffs
 * between sewn seams down the trunk and the sleeves, falling to mid-thigh in two halves split front and back, with a
 * shaggy fur hem, fur cuffs and a fur collar; three of Rimeclaw's hooked claws standing on each shoulder and a necklace
 * of its smaller claws over the chest.</li>
 * <li>Trousers: puffy hide trousers with a fur lining that rolls out over the knee.</li>
 * <li>Mukluks: wide soft boots up to mid-shin with a big fur roll at the top, on broad flat oval soles (the snowshoe
 * look).</li>
 * <li>Mittens: big round mittens with a thumb, three of Rimeclaw's claws for fingertips, and a fur cuff.</li>
 * </ul>
 *
 * <p>Every piece is built round the identity kit it covers (BaseCoat.rig), so the male and female versions fit their
 * own bodies. Labels: no new vertex takes a pivot label (BaseCoat.PIVOTS) but the mittens' hands; instead each piece
 * carries, for every pivot label of the kit it hides, a tiny hidden triangle on that label at the kit's centroid of it
 * ({@link #anchor}), so every joint turns where it does on the bare body. (Faceless kept vertices would not do: the
 * client builds the player model face by face and drops them.) The hood (ruff, goggles, face and all) is rigid on the
 * skull 1. The parka's trunk is on the chest 8 down to the belly 4 at the waist; its two skirt halves on the thighs 43
 * (left) / 44 (right), their top row on 4, so the front and back slits open in the stride; the sleeves on the upper
 * arms 20 / 26 and the forearms 19 / 22, the shoulder claws with the upper arm; and because the top hides the arms kit
 * it anchors the shoulders 21 / 25 and elbows 17 / 23 too. Trousers: the waistband 41, thighs 43 / 44, shins 36 / 33,
 * the cuffs 38 / 32. Mukluks: the shaft top and its fur roll on the shins 36 / 33, the rest (ankle, foot and sole) on the
 * ankles 38 / 32, which carry the whole foot in the stock boots too.
 * Mittens: the hand 28 / 27 (centred on the kit's hand, so its centroid stays put), the cuff 22 / 19.
 */
final class BaseParka {

	private BaseParka() {
	}

	/** White fur, its shadow, and the black of the goggles' slit. */
	static final int FUR = hsl(8, 0, 104), FUR_DARK = hsl(8, 0, 80), FUR_SHADOW = hsl(36, 1, 58), SLIT = hsl(0, 0, 6);

	/** {main, second or null} for the slot (ArmourSets.HELM..GLOVES). */
	static ModelData[] worn(ArmourForge af, Style st, int slot, boolean female, ModelData donor, ModelData donorSecond) throws IOException {
		Rig r = BaseCoat.rig(af, female);
		switch (slot) {
		case ArmourSets.HELM:
			return new ModelData[] { anchor(hood(r, st), r.head), null };
		case ArmourSets.BODY:
			return new ModelData[] { anchor(anchor(apart(parka(r, st)), r.torso), r.arms), null };
		case ArmourSets.LEGS:
			return new ModelData[] { anchor(apart(trousers(r, st)), r.legs), null };
		case ArmourSets.BOOTS:
			return new ModelData[] { anchor(apart(mukluks(r, st)), r.feet), null };
		case ArmourSets.GLOVES:
			return new ModelData[] { anchor(mittens(r, st), r.hands), null };
		default:
			return new ModelData[] { donor, donorSecond };
		}
	}

	/**
	 * For each pivot label (BaseCoat.PIVOTS) of the identity kit the piece hides, a tiny triangle hidden inside the
	 * limb, its three vertices on that label and their centroid exactly the kit's centroid of the label: so every
	 * joint turns about the same point as on the bare body. (Not faceless vertices, as BaseCoat.keep adds: the client
	 * merges the player's parts face by face (Model(int, Model[]) / method465), so a vertex no face uses is dropped.)
	 */
	static ModelData anchor(ModelData m, ModelData kit) {
		java.util.Set<Integer> labels = new java.util.TreeSet<>(kit.vertexSkins);
		for (int label : labels) {
			if (!BaseCoat.PIVOTS.contains(label)) {
				continue;
			}
			double[] c = BaseCoat.centroid(kit, label);
			int x = (int) Math.round(c[0]), y = (int) Math.round(c[1]), z = (int) Math.round(c[2]);
			m.skin = label;
			int a = m.vertex(x + 1, y, z), b = m.vertex(x - 1, y + 1, z), d = m.vertex(x, y - 1, z);
			m.tri(a, b, d, FUR_SHADOW);
			m.priorities.set(m.priorities.size() - 1, 0);
		}
		return m;
	}

	/** The inventory model for the slot, or null to let ArmourSets build it the usual way from the worn model(s). */
	static ModelData inventory(ArmourForge af, Style st, int slot, ModelData[] worn) throws IOException {
		return null;
	}

	/* ====================================================================== */
	/*                                 HELPERS                                */
	/* ====================================================================== */

	/** Two unit vectors at right angles to {@code a} (u as near +x as it can be, w towards the front where it can). */
	static double[][] basis(double[] a) {
		double[] ref = Math.abs(a[0]) > 0.85 ? pt(0, 0, -1) : pt(1, 0, 0);
		double[] u = norm(sub(ref, scale(a, dot(ref, a))));
		double[] w = ModelData.cross(a, u);
		return new double[][] { u, w };
	}

	/** n points round an axis through c (direction a): radius ru along u, rw along w, starting at angle phase. */
	static int[] ring(Mesh m, double[] c, double[] a, double ru, double rw, int n, double phase, int label) {
		double[][] b = basis(a);
		int[] out = new int[n];
		for (int j = 0; j < n; j++) {
			double t = phase + Math.PI * 2 * j / n;
			out[j] = m.v(add(c, add(scale(b[0], ru * Math.cos(t)), scale(b[1], rw * Math.sin(t)))), label);
		}
		return out;
	}

	/** A horizontal ring (BaseCoat.ell: phi 0 at the front) at height y. */
	static int[] hring(Mesh m, double cx, double y, double cz, double rx, double rz, int n, double phase, int label) {
		int[] out = new int[n];
		for (int j = 0; j < n; j++) {
			out[j] = m.v(ell(cx, y, cz, rx, rz, phase + Math.PI * 2 * j / n), label);
		}
		return out;
	}

	/** Bands between successive rings (closed), each quad facing away from the segment of axis between their centres. */
	static void loft(Mesh m, int[][] rings, double[][] centres, int[] cols) {
		for (int k = 0; k + 1 < rings.length; k++) {
			m.band(rings[k], rings[k + 1], true, new int[] { cols[Math.min(k, cols.length - 1)] }, lerp(centres[k], centres[k + 1], 0.5));
		}
	}

	/** A fan from a ring to an apex, facing away from {@code in}. */
	static void cap(Mesh m, int[] ring, int apex, int col, double[] in) {
		for (int j = 0; j < ring.length; j++) {
			m.triAway(ring[j], ring[(j + 1) % ring.length], apex, col, in);
		}
	}

	/**
	 * A hooked claw from {@code root}: it runs along {@code dir} and curls round towards {@code curl}, a triangular
	 * section tapering from {@code w} to a point, {@code len} long; the base colour {@code col}, the tip {@code tip}.
	 */
	static void claw(Mesh m, double[] root, double[] dir, double[] curl, double len, double w, int label, int col, int tip) {
		dir = norm(dir);
		curl = norm(sub(curl, scale(dir, dot(curl, dir))));
		double[] p1 = add(root, scale(dir, len * 0.45));
		double[] d1 = norm(add(dir, scale(curl, 0.7)));
		double[] p2 = add(p1, scale(d1, len * 0.35));
		double[] end = add(p2, scale(norm(add(scale(dir, 0.2), curl)), len * 0.35));
		double[] side = norm(ModelData.cross(dir, curl));
		int[][] rs = new int[3][3];
		double[][] cs = { root, p1, p2 };
		double[] ws = { w, w * 0.7, w * 0.4 };
		double[][] ax = { dir, norm(add(dir, d1)), d1 };
		for (int k = 0; k < 3; k++) {
			double[] u = curl, s = side;
			if (k > 0) {
				u = norm(sub(curl, scale(ax[k], dot(curl, ax[k]))));
				s = norm(ModelData.cross(ax[k], u));
			}
			for (int j = 0; j < 3; j++) {
				double t = Math.PI * 2 * j / 3;
				// the keel (j 0) on the outside of the curve, so the claw reads as a blade
				double ku = j == 0 ? -1.15 : 0.6;
				rs[k][j] = m.v(add(cs[k], add(scale(u, ws[k] * (j == 0 ? ku : 0.55)), scale(s, ws[k] * Math.sin(t) * 0.9))), label);
			}
		}
		int e = m.v(end, label);
		if (len < 9.5) { // a small claw: straight from the root to the bend
			m.band(rs[0], rs[2], true, new int[] { col }, lerp(cs[0], cs[2], 0.5));
		} else {
			for (int k = 0; k < 2; k++) {
				m.band(rs[k], rs[k + 1], true, new int[] { col }, lerp(cs[k], cs[k + 1], 0.5));
			}
		}
		cap(m, rs[2], e, tip, p2);
		m.triAway(rs[0][0], rs[0][1], rs[0][2], col, p1);
	}

	/**
	 * A ring of fur: a fat roll round an axis through c (direction a) from radius r0 (where it sits) out to a jagged
	 * edge of 2n tufts at r1 (long and short in turn), its crest pushed {@code lift} along the axis. Rows: base, crest,
	 * the tufts, and a shadowed underside back to the base.
	 */
	static void furRing(Mesh m, double[] c, double[] a, double r0x, double r0z, double r1x, double r1z, double lift, int n, int label, int fur, int tip) {
		double[][] b = basis(a);
		int N = n * 2;
		int[] base = new int[N], crest = new int[N], tuft = new int[N], under = new int[N];
		for (int j = 0; j < N; j++) {
			double t = Math.PI * 2 * j / N;
			double cs = Math.cos(t), sn = Math.sin(t);
			double jag = j % 2 == 0 ? 1.0 : 0.82;
			double[] dir0 = add(scale(b[0], r0x * cs), scale(b[1], r0z * sn));
			double[] dirm = add(scale(b[0], (r0x + r1x) / 2 * cs), scale(b[1], (r0z + r1z) / 2 * sn));
			double[] dir1 = add(scale(b[0], r1x * jag * cs), scale(b[1], r1z * jag * sn));
			base[j] = m.v(add(c, add(dir0, scale(a, -lift * 0.4))), label);
			crest[j] = n > 5 ? m.v(add(c, add(dirm, scale(a, -lift))), label) : -1;
			tuft[j] = m.v(add(c, add(dir1, scale(a, lift * (j % 2 == 0 ? 0.35 : 0.0)))), label);
			under[j] = m.v(add(c, add(scale(dir0, 1.02), scale(a, lift * 0.75))), label);
		}
		double[] cIn = add(c, scale(a, lift * 0.1));
		for (int j = 0; j < N; j++) {
			int k = (j + 1) % N;
			double tm = Math.PI * 2 * (j + 0.5) / N;
			double[] core = add(cIn, add(scale(b[0], (r0x * 0.75 + r1x * 0.25) * Math.cos(tm)), scale(b[1], (r0z * 0.75 + r1z * 0.25) * Math.sin(tm))));
			if (n > 5) {
				m.quadAway(base[j], base[k], crest[k], crest[j], fur, core);
				m.quadAway(crest[j], crest[k], tuft[k], tuft[j], j % 2 == 0 ? tip : fur, core);
			} else { // a small ring: straight from the base to the tufts
				m.quadAway(base[j], base[k], tuft[k], tuft[j], j % 2 == 0 ? tip : fur, core);
			}
			m.quadAway(tuft[j], tuft[k], under[k], under[j], FUR_DARK, core);
		}
	}

	/**
	 * Keeps each leg's vertices on its own side of the middle (x at least 1 from it): the client merges the player's
	 * parts by vertex position, so a left-leg vertex landing on a right-leg one would be welded to it and torn across
	 * in the stride.
	 */
	static ModelData apart(ModelData d) {
		for (int i = 0; i < d.vertices.size(); i++) {
			int l = d.vertexSkins.get(i);
			int side = l == 43 || l == 36 || l == 38 || l == 46 ? 1 : l == 44 || l == 33 || l == 32 || l == 45 ? -1 : 0;
			int[] v = d.vertices.get(i);
			if (side != 0 && v[0] * side < 1) {
				v[0] = side;
			}
		}
		return d;
	}

	/** The colour {@code d} lighter (or darker) in the same hue. */
	static int shade(int col, int d) {
		return (col & ~127) | Math.max(0, Math.min(127, (col & 127) + d));
	}

	/** One torso ring:{cz, rx, rz} from the torso kit's section at y, rx widened by mx, the front by mf, the back by mb. */
	static double[] trunk(Rig r, double y, double mx, double mf, double mb) {
		double[] s = slice(r.torso, Math.max(r.neckY + 0.5, Math.min(r.waistY - 0.5, y)), 0);
		double rx = Math.max(-s[0], s[1]) + mx, zF = s[2] - mf, zB = s[3] + mb;
		return new double[] { (zF + zB) / 2, rx, (zB - zF) / 2 };
	}

	/* ====================================================================== */
	/*                                THE HOOD                                */
	/* ====================================================================== */

	/** A point round the face axis (z): t 0 at the top, pi/2 at the left (+x). */
	static double[] fz(double cx, double cy, double z, double rx, double ry, double t) {
		return pt(cx + rx * Math.sin(t), cy - ry * Math.cos(t), z);
	}

	/**
	 * The parka hood: a tunnel round the head, deepest at the crown, closing behind; the ruff round its mouth; the
	 * player's face inside (copied from the head kit) wearing bone snow-goggles. All on the skull label 1.
	 */
	static ModelData hood(Rig r, Style st) {
		Mesh m = new Mesh();
		int[] hb = r.headB;
		double hx = Math.max(-hb[0], hb[3]), top = hb[1], chin = hb[4], front = hb[2], back = hb[5];
		double hh = chin - top;
		double fcy = top + hh * 0.52;
		int n = 12;
		// ------------------------------------------------ the tunnel: rings round the face axis, front to back
		double lipZ = front + 3.5;
		double[][] rows = { // z, rx, ry, cy offset
				{ lipZ, hx + 1.0, hh * 0.5 + 0.5, 0 }, { front + 9, hx + 5.5, hh * 0.5 + 6, -1.5 }, { (front + back) / 2 + 3, hx + 6.5, hh * 0.5 + 7, -2.5 },
				{ back + 3, hx + 4.5, hh * 0.5 + 4, -3 }, { back + 7.5, hx - 1, hh * 0.5 - 4, -3 } };
		int[][] ring = new int[rows.length][];
		double[][] ctr = new double[rows.length][];
		for (int k = 0; k < rows.length; k++) {
			ring[k] = new int[n];
			ctr[k] = pt(0, fcy + rows[k][3], rows[k][0]);
			for (int j = 0; j < n; j++) {
				ring[k][j] = m.v(fz(0, fcy + rows[k][3], rows[k][0], rows[k][1], rows[k][2], Math.PI * 2 * j / n), 1);
			}
		}
		m.prio = 3;
		for (int k = 0; k + 1 < rows.length; k++) {
			for (int j = 0; j < n; j++) {
				int jj = (j + 1) % n;
				double t = Math.PI * 2 * (j + 0.5) / n;
				int col = k == 0 ? st.dark : (j % 2 == 0 ? st.main : st.main);
				m.quadAway(ring[k][j], ring[k][jj], ring[k + 1][jj], ring[k + 1][j], col, lerp(ctr[k], ctr[k + 1], 0.5));
			}
		}
		int apex = m.v(pt(0, fcy - 3, back + 9.5), 1);
		cap(m, ring[rows.length - 1], apex, st.dark, ctr[rows.length - 2]);
		// the lining, seen round the face inside the mouth
		int[] lin = new int[n];
		for (int j = 0; j < n; j++) {
			lin[j] = m.v(fz(0, fcy - 1, front + 8, hx + 3.5, hh * 0.5 + 4, Math.PI * 2 * j / n), 1);
		}
		for (int j = 0; j < n; j++) {
			int jj = (j + 1) % n;
			double t = Math.PI * 2 * (j + 0.5) / n;
			double[] mid = scale(add(m.p(ring[0][j]), m.p(lin[jj])), 0.5);
			m.quad(ring[0][j], ring[0][jj], lin[jj], lin[j], FUR_SHADOW, sub(pt(0, fcy, mid[2]), mid));
		}
		// ------------------------------------------------ the ruff: a huge jagged halo of fur round the mouth
		ruff(m, r, st, fcy, lipZ, hx + 1.0, hh * 0.5 + 0.5);
		// ------------------------------------------------ the face, from the head kit
		face(m, r);
		// ------------------------------------------------ the snow-goggles
		goggles(m, r, st);
		return m.d;
	}

	static void ruff(Mesh m, Rig r, Style st, double cy, double z, double rx, double ry) {
		int N = 20;
		int[] in = new int[N], crest = new int[N], tuft = new int[N], back = new int[N];
		for (int j = 0; j < N; j++) {
			double t = Math.PI * 2 * j / N;
			// smaller under the chin, so it sits on the chest instead of in it
			double low = Math.max(0, -Math.cos(t));
			double g = 1 - 0.34 * low;
			double jag = j % 2 == 0 ? 1.0 : 0.8;
			in[j] = m.v(fz(0, cy, z - 0.5, rx + 0.5, ry + 0.5, t), 1);
			crest[j] = m.v(fz(0, cy, z - 7.5 + low * 2.5, (rx + 8) * g + 0.5, (ry + 8) * g, t), 1);
			tuft[j] = m.v(fz(0, cy, z - 1 + (j % 2 == 0 ? 0.5 : 3.0), (rx * 2.45) * g * jag + 1, (ry * 2.05) * g * jag + 1, t), 1);
			back[j] = m.v(fz(0, cy, z + 7, (rx * 1.85) * g, (ry * 1.6) * g, t), 1);
		}
		m.prio = 5;
		for (int j = 0; j < N; j++) {
			int k = (j + 1) % N;
			double tm = Math.PI * 2 * (j + 0.5) / N;
			double low = Math.max(0, -Math.cos(tm)), g = 1 - 0.34 * low;
			double[] core = fz(0, cy, z - 1, (rx * 1.55) * g, (ry * 1.45) * g, tm);
			m.quadAway(in[j], in[k], crest[k], crest[j], j % 2 == 0 ? FUR : FUR_DARK, core);
			m.quadAway(crest[j], crest[k], tuft[k], tuft[j], j % 2 == 0 ? st.core : FUR, core);
			m.quadAway(tuft[j], tuft[k], back[k], back[j], FUR_DARK, core);
		}
	}

	/** The front of the head kit (the face), every vertex on the skull: seen inside the hood. */
	static void face(Mesh m, Rig r) {
		ModelData h = r.head;
		double cz = (r.headB[2] + r.headB[5]) / 2.0;
		int[] map = new int[h.vertices.size()];
		java.util.Arrays.fill(map, -1);
		m.prio = 2;
		for (int i = 0; i < h.faces.size(); i++) {
			int[] f = h.faces.get(i);
			double z = 0, y = 0;
			for (int v : f) {
				z += h.vertices.get(v)[2] / 3.0;
				y += h.vertices.get(v)[1] / 3.0;
			}
			if (z > cz - 1.5 || y < r.headB[1] + 4) {
				continue;
			}
			int[] nv = new int[3];
			for (int k = 0; k < 3; k++) {
				if (map[f[k]] < 0) {
					map[f[k]] = m.v(ArmourForge.d(h.vertices.get(f[k])), 1);
				}
				nv[k] = map[f[k]];
			}
			m.d.tri(nv[0], nv[1], nv[2], h.colors.get(i));
			m.d.priorities.set(m.d.priorities.size() - 1, 2);
		}
	}

	/** Bone snow-goggles: an ivory plate curving round the face at the eyes, a single dark slit across it. */
	static void goggles(Mesh m, Rig r, Style st) {
		// the eyes: the head kit's black faces
		double ey = 0, ez = 0;
		int cnt = 0;
		for (int i = 0; i < r.head.faces.size(); i++) {
			if (r.head.colors.get(i) == 0) {
				for (int v : r.head.faces.get(i)) {
					ey += r.head.vertices.get(v)[1];
					ez += r.head.vertices.get(v)[2];
					cnt++;
				}
			}
		}
		double hx = Math.max(-r.headB[0], r.headB[3]);
		if (cnt == 0) {
			ey = r.headB[1] + (r.headB[4] - r.headB[1]) * 0.42;
			ez = r.headB[2] + 3;
		} else {
			ey /= cnt;
			ez /= cnt;
		}
		int cols = 4;
		double[] ys = { ey - 3.6, ey - 1.0, ey + 1.0, ey + 3.4 };
		int[][] g = new int[ys.length][cols + 1];
		for (int q = 0; q < ys.length; q++) {
			for (int j = 0; j <= cols; j++) {
				double u = -1 + 2.0 * j / cols;
				double x = u * (hx + 1.5);
				double z = ez - 2.6 + u * u * 8.5 - (q == 0 || q == 3 ? 0 : 0.3);
				g[q][j] = m.v(pt(x, ys[q], z), 1);
			}
		}
		m.prio = 6;
		double[] behind = pt(0, ey, ez + 12);
		for (int q = 0; q + 1 < ys.length; q++) {
			for (int j = 0; j < cols; j++) {
				m.quadAway(g[q][j], g[q][j + 1], g[q + 1][j + 1], g[q + 1][j], q == 1 ? SLIT : (j == 0 || j == cols - 1) ? st.trimDark : st.accent, behind);
			}
		}
		// a bone ridge over the slit, standing proud
		int[] t0 = new int[cols + 1], t1 = new int[cols + 1];
		for (int j = 0; j <= cols; j++) {
			double u = -1 + 2.0 * j / cols;
			double x = u * (hx + 1.0);
			t0[j] = m.v(pt(x, ey - 3.6, ez - 2.6 + u * u * 8.5), 1);
			t1[j] = m.v(pt(x, ey - 4.8, ez - 4.0 + u * u * 8.0), 1);
		}
		m.bandFacing(t0, t1, false, st.light, pt(0, -1, -1));
	}

	/* ====================================================================== */
	/*                                THE PARKA                               */
	/* ====================================================================== */

	/**
	 * The parka: a quilted trunk (puffs between sewn seams), a fur collar, two skirt halves to mid-thigh with a fur
	 * hem, puffy quilted sleeves with fur cuffs, three hooked claws on each shoulder and a claw necklace.
	 */
	static ModelData parka(Rig r, Style st) {
		Mesh m = new Mesh();
		int n = 12;
		double L = r.waistY - r.neckY;
		// ------------------------------------------------ the trunk: y, widen x, front, back, label, colour of the band below
		double[][] rows = { { r.neckY + 1.0, 1.5, 2.0, 2.0, 8 }, { r.neckY + 6, 3.0, 4.0, 3.5, 8 }, { r.neckY + L * 0.30, 5.5, 9.5, 8.0, 8 },
				{ r.neckY + L * 0.47, 3.5, 5.5, 4.5, 8 }, { r.neckY + L * 0.68, 6.5, 10.0, 8.5, 8 }, { r.neckY + L * 0.86, 4.0, 6.0, 5.0, 4 },
				{ r.waistY + 2.5, 7.0, 10.0, 8.5, 4 } };
		int R = rows.length;
		int[][] ring = new int[R][];
		double[][] ctr = new double[R][], dim = new double[R][];
		for (int i = 0; i < R; i++) {
			double[] t = trunk(r, rows[i][0], rows[i][1], rows[i][2], rows[i][3]);
			if (i == 0) {
				t[1] = Math.min(t[1], 11.5);
			}
			dim[i] = t;
			ctr[i] = pt(0, rows[i][0], t[0]);
			ring[i] = hring(m, 0, rows[i][0], t[0], t[1], t[2], n, 0, (int) rows[i][4]);
		}
		m.prio = 3;
		for (int i = 0; i + 1 < R; i++) {
			// the upper half of each puff in the hide, the lower half (into the seam) a shade darker
			int col = i == 0 ? st.dark : i % 2 == 1 ? st.main : shade(st.main, -10);
			m.band(ring[i], ring[i + 1], true, new int[] { col }, lerp(ctr[i], ctr[i + 1], 0.5));
		}
		// ------------------------------------------------ the skirt: to mid-thigh, split front and back, each half on its thigh
		double[] w = dim[R - 1];
		double top = rows[R - 1][0], hem = (r.hip[1] + r.thigh[1]) / 2 + 2;
		double[] waist = { w[0], w[1] - 1.0, w[2] - 1.0 };
		int segs = 6;
		double[] ys = { top, top + (hem - top) * 0.45, top + (hem - top) * 0.62, hem };
		double[] grow = { -0.6, 2.2, 0.8, 2.6 };
		for (int side = -1; side <= 1; side += 2) {
			int label = side > 0 ? 43 : 44;
			int[][] row = new int[ys.length][segs + 1];
			double[][] sk = new double[ys.length][];
			for (int q = 0; q < ys.length; q++) {
				double f = (ys[q] - top) / (hem - top);
				sk[q] = BaseCoat.skirtRing(r, waist, top, ys[q], f * 0.6);
				double gap = 0.03 + 0.05 * f;
				for (int j = 0; j <= segs; j++) {
					double phi = side * (gap + (Math.PI - 2 * gap) * j / segs);
					row[q][j] = m.v(ell(0, ys[q], sk[q][0], sk[q][1] + grow[q], sk[q][2] + grow[q], phi), q == 0 ? 4 : label);
				}
			}
			for (int q = 0; q + 1 < ys.length; q++) {
				double[] in = pt(0, (ys[q] + ys[q + 1]) / 2, sk[q][0]);
				m.band(row[q], row[q + 1], false, new int[] { q == 1 ? shade(st.main, -10) : st.main }, in);
			}
			// the fur hem: a shaggy roll round the bottom of the half
			int H = 10;
			int[] h0 = new int[H + 1], h1 = new int[H + 1], h2 = new int[H + 1];
			double[] s3 = sk[3];
			for (int j = 0; j <= H; j++) {
				double gap = 0.08;
				double phi = side * (gap + (Math.PI - 2 * gap) * j / H);
				double jag = j % 2 == 0 ? 3.6 : 1.4;
				h0[j] = m.v(ell(0, hem - 3.5, s3[0], s3[1] + grow[3] + 1.6, s3[2] + grow[3] + 1.6, phi), label);
				h1[j] = m.v(ell(0, hem + 0.5, s3[0], s3[1] + grow[3] + 3.6, s3[2] + grow[3] + 3.6, phi), label);
				h2[j] = m.v(ell(0, hem + 1.5 + jag, s3[0], s3[1] + grow[3] + 1.4, s3[2] + grow[3] + 1.4, phi), label);
			}
			m.prio = 4;
			double[] in = pt(0, hem, s3[0]);
			for (int j = 0; j < H; j++) {
				m.quadAway(h0[j], h0[j + 1], h1[j + 1], h1[j], FUR, in);
				m.quadAway(h1[j], h1[j + 1], h2[j + 1], h2[j], j % 2 == 0 ? st.core : FUR_DARK, in);
			}
			m.prio = 3;
		}
		// ------------------------------------------------ the fur collar round the neck (under the hood's ruff)
		double[] s = slice(r.torso, r.neckY + 1, 0);
		double ncz = (s[2] + s[3]) / 2, nrx = Math.min(11.5, Math.max(-s[0], s[1])) + 2, nrz = (s[3] - s[2]) / 2 + 2.5;
		m.prio = 5;
		furRing(m, pt(0, r.neckY + 3, ncz), pt(0, -1, 0), nrx, nrz, nrx + 7, nrz + 7, 3, 6, 8, FUR, st.core);
		// ------------------------------------------------ the sleeves
		for (int side = -1; side <= 1; side += 2) {
			sleeve(m, r, st, side);
		}
		// ------------------------------------------------ the claw necklace
		necklace(m, r, st, dim[2], rows[2][0]);
		return m.d;
	}

	/** A puffy quilted sleeve, a fat fur cuff at the wrist, and three of Rimeclaw's hooked claws standing on the shoulder. */
	static void sleeve(Mesh m, Rig r, Style st, int side) {
		int up = side > 0 ? 26 : 20, fo = side > 0 ? 22 : 19;
		double[] J = mirror(pt(r.shoulder[0], r.armTop + 3, r.shoulder[2]), side), E = mirror(r.elbow, side), W = mirror(pt(r.wrist[0], r.wristY - 1, r.wrist[2]), side);
		double armR = Math.max(5, (extent(r.arms, 21)[3] - extent(r.arms, 21)[0]) / 2);
		double foreR = Math.max(4, (extent(r.arms, 19)[3] - extent(r.arms, 19)[0]) / 2);
		double[] Au = norm(sub(E, J)), Af = norm(sub(W, E));
		int n = 7;
		// centre, axis, radius, label, colour of the band below
		Object[][] rr = { { add(J, scale(Au, -2)), Au, armR + 1.0, up }, { add(J, scale(Au, 5)), Au, armR + 4.4, up }, { lerp(J, E, 0.55), Au, armR + 2.4, up },
				{ add(E, scale(Au, -1.5)), Au, armR + 4.2, up }, { add(E, scale(Af, 2.5)), Af, foreR + 2.6, fo }, { lerp(E, W, 0.6), Af, foreR + 4.0, fo },
				{ add(W, scale(Af, -1.0)), Af, foreR + 2.6, fo } };
		int[][] rings = new int[rr.length][];
		double[][] cs = new double[rr.length][];
		for (int k = 0; k < rr.length; k++) {
			cs[k] = (double[]) rr[k][0];
			double rad = (double) rr[k][2];
			rings[k] = ring(m, cs[k], (double[]) rr[k][1], rad, rad * 0.95, n, 0.2, (int) rr[k][3]);
		}
		m.prio = 6;
		int mid = shade(st.main, -10);
		int[] cols = { st.main, mid, st.main, mid, st.main, mid };
		loft(m, rings, cs, cols);
		// the fur cuff
		m.prio = 7;
		furRing(m, add(W, scale(Af, -0.5)), scale(Af, -1), foreR + 2.2, foreR + 2.0, foreR + 6.5, foreR + 6.2, 2.4, 5, fo, FUR, st.core);
		// the claws on the shoulder: three, standing up and hooking out over the arm
		m.prio = 8;
		double[] top = add(J, pt(side * 2.5, -armR * 0.6 - 2, 0));
		double[][] cl = { { 0, 21, 3.6 }, { -5.5, 16, 2.9 }, { 5.5, 16, 2.9 } };
		for (double[] c : cl) {
			double[] root = add(top, pt(-side * Math.abs(c[0]) * 0.25, Math.abs(c[0]) * 0.25, c[0]));
			claw(m, root, pt(side * 0.35, -1, c[0] * 0.04), pt(side, 0.25, 0), c[1], c[2], up, st.accent, st.core);
		}
	}

	/** Rimeclaw's smaller claws on a thong round the chest, hooking forward at the tips. */
	static void necklace(Mesh m, Rig r, Style st, double[] chest, double chestY) {
		double y0 = r.neckY + 12;
		double cz = chest[0], rx = chest[1], rz = chest[2];
		m.prio = 8;
		int k0 = -2;
		for (int k = k0; k <= -k0; k++) {
			double phi = k * 0.3;
			double y = y0 + 5.5 - Math.abs(k) * 1.3;
			double[] p = ell(0, y, cz, (rx - 1.5) * 0.98, rz + 0.2, phi);
			double len = k == 0 ? 11 : 9 - Math.abs(k);
			claw(m, p, pt(0, 1, -0.15), pt(0, 0, -1), len, k == 0 ? 2.4 : 2.0, 8, k % 2 == 0 ? st.accent : DraftArmourB.CLAW_DARK, st.core);
		}
	}

	/* ====================================================================== */
	/*                               THE TROUSERS                             */
	/* ====================================================================== */

	/** Puffy quilted hide trousers, the fur lining showing as a shaggy cuff at each ankle. */
	static ModelData trousers(Rig r, Style st) {
		Mesh m = new Mesh();
		int n = 8;
		int mid = shade(st.trim, -8);
		for (int side = -1; side <= 1; side += 2) {
			int thigh = side > 0 ? 43 : 44, shin = side > 0 ? 36 : 33, ank = side > 0 ? 38 : 32;
			// y, margin, label: puffs and seams in turn, from the waist to the ankle
			double[][] rows = { { r.waistY + 3, 2.0, 41 }, { r.hip[1] + 1, 4.4, thigh }, { (r.hip[1] + r.thigh[1]) / 2, 2.6, thigh }, { r.thigh[1] - 3, 4.6, thigh },
					{ r.knee[1] + 2, 2.4, shin }, { (r.knee[1] + r.ankle[1]) / 2, 4.0, shin }, { r.ankle[1] + 1, 2.2, ank } };
			int[][] idx = new int[rows.length][n];
			double[][] cs = new double[rows.length][];
			for (int k = 0; k < rows.length; k++) {
				double[] q = BaseCoat.legRing(r, rows[k][0], side, rows[k][1]);
				if (k == 0) {
					q[0] = side * Math.max(Math.abs(q[0]), 8.5);
					q[2] = Math.max(q[2], 10.5);
				}
				cs[k] = pt(q[0], rows[k][0], q[1]);
				idx[k] = hring(m, q[0], rows[k][0], q[1], q[2], q[3], n, Math.PI / n, (int) rows[k][2]);
			}
			m.prio = 1;
			loft(m, idx, cs, new int[] { st.trim, mid, st.trim, mid, st.trim, mid });
			// the fur lining, turned out at the ankle
			double[] q = BaseCoat.legRing(r, r.ankle[1] + 1, side, 2.2);
			m.prio = 2;
			furRing(m, pt(q[0], r.ankle[1] + 1, q[1]), pt(0, -1, 0), q[2], q[3], q[2] + 4.0, q[3] + 4.0, 2.0, 4, ank, FUR, st.core);
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                                THE MUKLUKS                             */
	/* ====================================================================== */

	/** Wide soft boots to mid-shin with a big fur roll at the top, on broad flat oval soles. */
	static ModelData mukluks(Rig r, Style st) {
		Mesh m = new Mesh();
		int[] fb = r.feetB;
		int n = 6;
		for (int side = -1; side <= 1; side += 2) {
			int ank = side > 0 ? 38 : 32, shin = side > 0 ? 36 : 33;
			double topY = r.knee[1] + (r.ankle[1] - r.knee[1]) * 0.3;
			double[] fe = extent(r.feet, side > 0 ? 47 : 48);
			double x = (fe[0] + fe[3]) / 2, hw = (fe[3] - fe[0]) / 2;
			double sole = fb[4] + 0.5, back = fb[5] + 1.5, front = fb[2] - 2.0;
			double fcz = (front + back) / 2 + 0.5, flen = (back - front) / 2;
			// the shaft from the fur roll to the ankle, then the foot: a broad rounded slipper down onto the sole
			int[][] idx = new int[4][];
			double[][] cs = new double[4][];
			double[] q0 = BaseCoat.legRing(r, topY, side, 4.6), q1 = BaseCoat.legRing(r, r.ankle[1], side, 5.6);
			cs[0] = pt(q0[0], topY, q0[1]);
			idx[0] = hring(m, q0[0], topY, q0[1], q0[2], q0[3], n, Math.PI / n, shin);
			cs[1] = pt(q1[0], r.ankle[1] + 1, q1[1] + 0.5);
			idx[1] = hring(m, q1[0], r.ankle[1] + 1, q1[1] + 0.5, q1[2], q1[3] + 0.5, n, Math.PI / n, ank);
			double[][] foot = { { sole - 7.5, hw + 3.6, flen + 0.5 }, { sole - 2.0, hw + 4.4, flen + 2.4 } };
			for (int k = 0; k < 2; k++) {
				cs[2 + k] = pt(x, foot[k][0], fcz);
				idx[2 + k] = new int[n];
				for (int j = 0; j < n; j++) {
					double[] p = ell(x, foot[k][0], fcz, foot[k][1], foot[k][2], Math.PI / n + Math.PI * 2 * j / n);
					idx[2 + k][j] = m.v(p, ank);
				}
			}
			m.prio = 2;
			loft(m, idx, cs, new int[] { st.main, shade(st.main, -10), st.dark });
			// the sole: a flat oval, wider and longer than the foot, pale woven hide round a dark tread
			int sn = 8;
			int[] s0 = new int[sn], s1 = new int[sn];
			double scz = fcz - 1;
			for (int j = 0; j < sn; j++) {
				double phi = Math.PI * 2 * j / sn;
				double[] a = ell(x, sole - 3.0, scz, hw + 6.0, flen + 4.5, phi), b = ell(x, sole + 0.5, scz, hw + 5.5, flen + 4.0, phi);
				int lab = ank; // the whole foot moves with the ankle, as the stock boots do (their toe labels only flex the tip)
				s0[j] = m.v(a, lab);
				s1[j] = m.v(b, lab);
			}
			m.band(s0, s1, true, new int[] { st.light }, pt(x, sole - 1, scz));
			int c0 = m.v(pt(x, sole - 3.0, scz), ank), c1 = m.v(pt(x, sole + 0.5, scz), ank);
			cap(m, s0, c0, st.light, pt(x, sole + 3, scz));
			cap(m, s1, c1, st.trimDark, pt(x, sole - 6, scz));
			// the fur roll at the top of the shaft
			m.prio = 3;
			furRing(m, pt(q0[0], topY + 1, q0[1]), pt(0, -1, 0), q0[2], q0[3], q0[2] + 6.5, q0[3] + 6.5, 3.4, 5, shin, FUR, st.core);
		}
		return m.d;
	}

	/* ====================================================================== */
	/*                                THE MITTENS                             */
	/* ====================================================================== */

	/** Big round mittens with a thumb and three of Rimeclaw's claws for fingertips, on fat fur cuffs. */
	static ModelData mittens(Rig r, Style st) {
		Mesh m = new Mesh();
		for (int side = -1; side <= 1; side += 2) {
			int hand = side > 0 ? 28 : 27, fore = side > 0 ? 22 : 19;
			double[] W = mirror(BaseCoat.centroid(r.hands, 19), side), H = mirror(r.hand, side);
			double[] he = extent(r.hands, 27);
			double hx = (he[3] - he[0]) / 2, hz = (he[5] - he[2]) / 2;
			double[] D = norm(sub(H, W));
			double len = 2 * Math.sqrt(dot(sub(H, W), sub(H, W)));
			int n = 6;
			double[][] hs = { { 0.06, 1.0 + 2.8 / hx }, { 0.5, 1.0 + 4.6 / hx }, { 0.95, 1.0 + 2.6 / hx } };
			int[][] rings = new int[hs.length][];
			double[][] cs = new double[hs.length][];
			for (int k = 0; k < hs.length; k++) {
				cs[k] = add(W, scale(D, len * hs[k][0]));
				rings[k] = ring(m, cs[k], D, hx * hs[k][1], hz * hs[k][1], n, 0, k == 0 ? fore : hand);
			}
			m.prio = 10;
			loft(m, rings, cs, new int[] { st.main, shade(st.main, -10) });
			double[] tipC = add(W, scale(D, len * 1.05));
			int apex = m.v(tipC, hand);
			cap(m, rings[hs.length - 1], apex, st.dark, cs[hs.length - 2]);
			// the thumb, out from the front of the mitten
			double[] t0 = add(cs[1], pt(-side * 0.5, 0, -hz * 1.1)), t1 = add(t0, pt(-side * 1.0, len * 0.32, -3.5));
			m.d.skin = hand;
			ModelData th = new ModelData();
			th.skin = hand;
			ArmourForge.blade(th, t0, t1, pt(side, 0, 0), 3.0, 2.0, st.dark);
			th.priorities.replaceAll(x -> 10);
			th.renderTypes.replaceAll(x -> 0);
			ArmourForge.merge(m.d, th, null);
			// the claws: three hooked tips out of the end of the mitten, curling towards the palm (inward, -side)
			m.prio = 11;
			for (int k = -1; k <= 1; k++) {
				double[] root = add(add(W, scale(D, len * 0.9)), pt(side * 1.0, 0, k * hz * 0.75));
				claw(m, root, add(D, pt(0, 0, k * 0.15)), pt(-side, 0.2, -0.3), 8.0, 1.8, hand, st.accent, st.core);
			}
			// the fur cuff
			m.prio = 11;
			double[] E = mirror(r.elbow, side);
			double[] Af = norm(sub(W, E));
			furRing(m, add(W, scale(Af, -3.0)), scale(Af, -1), hx + 3.2, hz + 3.2, hx + 8.5, hz + 8.0, 2.8, 4, fore, FUR, st.core);
		}
		return m.d;
	}
}
