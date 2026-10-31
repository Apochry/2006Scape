package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.LadderWeapons.*;
import static com.rs2.tools.mapgen.ModelData.hsl;
import static com.rs2.tools.mapgen.QuestWeapons.*;

import com.rs2.game.content.voidrealm.VoidConstants;

/**
 * Two weapon tiers, each a whole family the way rune and dragon are: one material, one palette, one design language,
 * the same ten shapes (and voidsteel adds a longbow). Every shape is built once here and dressed by a {@link Style}, so
 * a starmetal scimitar and a voidsteel scimitar are recognisably the same weapon in two metals, and every starmetal
 * piece is recognisably starmetal.
 *
 * <ul>
 * <li><b>Starmetal</b> (Attack 50, between rune and dragon): the fallen star, worked at Gatehold. Pale silver-lilac
 * metal with a bright honed edge, night-blue grips, star-pointed pommels and guard tips, and a violet star-shard set
 * where blade meets hilt. Clean, straight, heraldic: the Wardens' look.</li>
 * <li><b>Voidsteel</b> (Attack 70, above dragon): starmetal quenched in the dark behind the Starless Gate. Near-black
 * violet metal whose every edge burns, guards swept forward like horns, barbs down the back of the blades, hooked
 * pommels and a single white eye where starmetal had its shard. Menacing, asymmetric: the Starless's look.</li>
 * </ul>
 * Poses and frame lengths are LadderWeapons' and QuestWeapons' (dagger 38, sword 78, longsword 106, scimitar 101,
 * mace 70, warhammer 63, battleaxe 66, 2h sword 143, spear 263, halberd 240, longbow 185).
 */
public final class WeaponSets {

	private WeaponSets() {
	}

	/** A tier's palette and design language. */
	static final class Style {
		final String material;
		final int metal, metalDark, edge, grip, band, accent, accentCore, haft;
		final boolean voidsteel;

		Style(String material, int metal, int metalDark, int edge, int grip, int band, int accent, int accentCore, int haft, boolean voidsteel) {
			this.material = material;
			this.metal = metal;
			this.metalDark = metalDark;
			this.edge = edge;
			this.grip = grip;
			this.band = band;
			this.accent = accent;
			this.accentCore = accentCore;
			this.haft = haft;
			this.voidsteel = voidsteel;
		}
	}

	static final Style STARMETAL_STYLE = new Style("Starmetal", STARMETAL, STARMETAL_DARK, STARMETAL_EDGE, NIGHT, STARMETAL_DARK,
			violet(92, 6), violet(118, 3), hsl(42, 3, 22), false);
	static final Style VOIDSTEEL_STYLE = new Style("Voidsteel", violet(22, 3), violet(11, 2), violet(110, 5), violet(8, 1), violet(62, 6),
			violet(98, 6), hsl(42, 0, 124), violet(12, 2), true);

	/* ====================================================================== */
	/*                            SHARED MOTIFS                               */
	/* ====================================================================== */

	/** The pommel: a star (starmetal) or a hooked spike with a violet bead (voidsteel). */
	static void pommel(ModelData m, double s, double size, Style st) {
		if (!st.voidsteel) {
			star(m, f(s, 0, 0), 2.4 * size, 1.2 * size, STAR_WHITE, st.metal);
			collar(m, s + 1.2 * size, s + 2.6 * size, 1.2 * size, 1.8 * size, st.metalDark);
		} else {
			tube(m, f(s + 2.4 * size, 0, 0), f(s - 1 * size, 1.4 * size, 0), 2.2 * size, 0.9 * size, 4, Math.PI / 4, st.metal, st.metalDark);
			tube(m, f(s - 1 * size, 1.4 * size, 0), f(s - 3.4 * size, -1.6 * size, 0), 0.9 * size, 0, 4, Math.PI / 4, st.edge, st.metal);
			tube(m, f(s + 0.6 * size, 0, 0), f(s + 2.8 * size, 0, 0), 1.6 * size, 1.6 * size, 6, 0, st.band, st.accent);
		}
	}

	/** A grip from s0 to s1, wound with bands every {@code step}. */
	static void grip(ModelData m, double s0, double s1, double r, double step, Style st) {
		tube(m, f(s0, 0, 0), f(s1, 0, 0), r, r * 1.08, 4, Math.PI / 4, st.grip, st.grip);
		for (double s = s0 + step * 0.6; s + 0.8 < s1; s += step) {
			band(m, s, s + 0.8, r * 1.22, st.band);
		}
	}

	/**
	 * The cross-guard at s, half-width {@code half}: a straight bar with star-points at the ends (starmetal), or two
	 * horns swept forward towards the tip with burning points (voidsteel). The accent sits in the middle.
	 */
	static void crossguard(ModelData m, double s, double half, double r, Style st) {
		collar(m, s - r, s + r, r * 1.4, r * 1.3, st.metalDark);
		for (int side = -1; side <= 1; side += 2) {
			if (!st.voidsteel) {
				tube(m, f(s, 0, 0), f(s, side * half, 0), r, r * 0.8, 4, Math.PI / 4, st.metalDark, st.metal);
				tube(m, f(s, side * half, 0), f(s, side * (half + r * 1.8), 0), r * 1.1, 0, 4, Math.PI / 4, st.metal, st.metal);
				tube(m, f(s, side * half, 0), f(s + r * 1.6, side * half, 0), r * 0.9, 0, 4, Math.PI / 4, st.edge, st.metal);
				tube(m, f(s, side * half, 0), f(s - r * 1.6, side * half, 0), r * 0.9, 0, 4, Math.PI / 4, st.metalDark, st.metal);
			} else {
				double[] a = f(s, 0, 0), b = f(s + half * 0.25, side * half * 0.75, 0), c = f(s + half * 0.9, side * half * 1.05, 0);
				tube(m, a, b, r * 1.1, r * 0.8, 4, Math.PI / 4, st.metal, st.metalDark);
				tube(m, b, c, r * 0.8, 0, 4, Math.PI / 4, st.edge, st.metal);
			}
		}
		gem(m, s, 0, r * 1.3, r * 2.4, st.accent, st.voidsteel ? st.accentCore : st.accent);
		if (st.voidsteel) {
			tube(m, f(s, 0, r * 2.0), f(s, 0, r * 2.6), r * 0.8, 0.3, 6, 0, st.accentCore, st.accentCore);
			tube(m, f(s, 0, -r * 2.0), f(s, 0, -r * 2.6), r * 0.8, 0.3, 6, 0, st.accentCore, st.accentCore);
		}
	}

	/** A straight blade: flat in the metal, the bevel in the edge colour; voidsteel adds barbs down the back (-w). */
	static void blade(ModelData m, double[][] stations, double tipS, Style st) {
		bladeStrip(m, stations, tipS, st.metal, st.edge, st.voidsteel ? 0.5 : 0.55);
		if (!st.voidsteel) { // a violet star-shard fuller let into the flat, from the guard to halfway up
			double s0 = stations[0][0], len = tipS - s0, hw = stations[0][1], ht = stations[0][2];
			diamond(m, new double[][] { { s0 + 1, hw * 0.22, ht + 0.25 }, { s0 + len * 0.45, hw * 0.18, ht + 0.2 } }, s0 + len * 0.55, 0, st.accent,
					st.accentCore);
		}
		if (st.voidsteel) {
			double s0 = stations[0][0], s1 = tipS;
			for (int k = 1; k <= 3; k++) {
				double s = s0 + (s1 - s0) * (0.3 + 0.17 * k);
				double hw = halfWidthAt(stations, tipS, s);
				tube(m, f(s, -hw + 0.3, 0), f(s - (s1 - s0) * 0.08, -hw - (s1 - s0) * 0.05 - 1.5, 0), 1.1 + (s1 - s0) * 0.008, 0, 4, Math.PI / 4,
						st.metal, st.edge);
			}
		}
	}

	static double halfWidthAt(double[][] st, double tipS, double s) {
		for (int i = 0; i + 1 < st.length; i++) {
			if (s >= st[i][0] && s <= st[i + 1][0]) {
				double t = (s - st[i][0]) / (st[i + 1][0] - st[i][0]);
				return st[i][1] + (st[i + 1][1] - st[i][1]) * t;
			}
		}
		double last = st[st.length - 1][0];
		return st[st.length - 1][1] * Math.max(0, (tipS - s) / (tipS - last));
	}

	/* ====================================================================== */
	/*                               THE SHAPES                               */
	/* ====================================================================== */

	static ModelData dagger(Style st) {
		ModelData m = new ModelData();
		pommel(m, 0.8, 0.8, st);
		grip(m, 2.6, 12.2, 1.5, 3.2, st);
		crossguard(m, 13, 4.6, 1.3, st);
		blade(m, new double[][] { { 13.8, 2.9, 0.9 }, { 26, 2.6, 0.85 } }, 40, st);
		return m;
	}

	static ModelData sword(Style st) {
		ModelData m = new ModelData();
		pommel(m, 1, 1, st);
		grip(m, 3.5, 15, 1.6, 3.4, st);
		crossguard(m, 16.2, 7, 1.5, st);
		blade(m, new double[][] { { 17.5, 3.3, 1.0 }, { 46, 3.3, 1.0 }, { 66, 2.7, 0.9 } }, 80, st);
		return m;
	}

	static ModelData longsword(Style st) {
		ModelData m = new ModelData();
		pommel(m, 1, 1.1, st);
		grip(m, 3.5, 17, 1.7, 3.4, st);
		crossguard(m, 18.5, 9.5, 1.7, st);
		blade(m, new double[][] { { 20, 3.9, 1.15 }, { 56, 3.9, 1.1 }, { 88, 3.3, 1.0 }, { 97, 2.5, 0.85 } }, 109, st);
		return m;
	}

	static ModelData scimitar(Style st) {
		ModelData m = new ModelData();
		pommel(m, 1, 1, st);
		grip(m, 3.5, 27, 1.8, 4, st);
		crossguard(m, 29, 6.5, 1.6, st);
		double back = st.voidsteel ? 1.25 : 1.0; // voidsteel curves deeper
		plateSW(m, new double[][] { { 30, -1.7 }, { 50, -2.8 * back }, { 71, -5.0 * back }, { 71, 7.0 }, { 50, 8.0 }, { 31, 4.6 } },
				new double[] { 1.15, 1.15, 1.05, 0, 0, 0 }, new double[] { 50, 2.2 }, 1.05, st.metal, st.edge, st.metalDark);
		plateSW(m, new double[][] { { 69, -4.8 * back }, { 87, -9.6 * back }, { 102, -16.5 * back }, { 96, -6.5 * back }, { 84, 1.6 },
				{ 69, 7.0 } }, new double[] { 1.05, 0.95, 0, 0, 0, 0 }, new double[] { 83, -2.5 * back }, 0.95, st.metal, st.edge, st.metalDark);
		if (st.voidsteel) { // a barb off the back of the curve
			tube(m, f(60, -3.4, 0), f(54, -10, 0), 1.6, 0, 4, Math.PI / 4, st.metal, st.edge);
		}
		return m;
	}

	static ModelData mace(Style st) {
		ModelData m = new ModelData();
		haft(m, 3, 44, 2.4, 2.2, st.haft);
		pommel(m, 1.5, 1.1, st);
		grip(m, 5, 24, 2.7, 4, st);
		collar(m, 38, 44, 2.8, 6.0, st.metalDark);
		collar(m, 44, 60, 6.0, 5.4, st.metalDark);
		tube(m, f(60, 0, 0), f(67, 0, 0), 5.4, 0, 6, 0, st.metal, st.metal);
		int n = st.voidsteel ? 5 : 6;
		for (int i = 0; i < n; i++) {
			double a = Math.PI * 2 * i / n;
			double[] V = { Math.sin(a), Math.cos(a), 0 }, N = { Math.cos(a), -Math.sin(a), 0 };
			double[][] pts = st.voidsteel ? new double[][] { { 42, 4 }, { 44, 10 }, { 54, 13 }, { 70, 8 }, { 62, 5 } }
					: new double[][] { { 43, 4.5 }, { 47, 11 }, { 61, 11 }, { 66, 4.5 } };
			double[] h = st.voidsteel ? new double[] { 1.4, 0, 0, 0, 1.2 } : new double[] { 1.4, 0, 0, 1.2 };
			plate(m, v(0, 0, 0), v(0, 0, 1), V, N, pts, h, new double[] { 53, 7.5 }, 1.5, st.metal, st.edge, st.metalDark);
		}
		// the accent crowning the head
		tube(m, f(64, 0, 0), f(72, 0, 0), 2.6, 0, 6, 0, st.accent, st.accent);
		if (!st.voidsteel) {
			star(m, f(69, 0, 0), 4.2, 1.4, st.accentCore, st.accent);
		} else {
			gem(m, 52, 0, 3.0, 7.0, st.accentCore, st.accent);
		}
		return m;
	}

	static ModelData warhammer(Style st) {
		ModelData m = new ModelData();
		haft(m, 2, 60, 2.5, 2.2, st.haft);
		pommel(m, 2, 1.1, st);
		grip(m, 4, 18, 2.8, 4, st);
		collar(m, 42, 46, 2.8, 3.4, st.metalDark);
		tube(m, f(52, -6, 0), f(52, 8, 0), 6.8, 6.8, 4, Math.PI / 4, st.metal, st.metalDark);
		tube(m, f(52, 8, 0), f(52, 12.5, 0), 6.8, 8.2, 4, Math.PI / 4, st.metalDark, st.edge);
		tube(m, f(52, -6, 0), f(51, -10, 0), 6.8, 4.6, 4, Math.PI / 4, st.metalDark, st.metalDark);
		if (st.voidsteel) { // a long hooked beak
			tube(m, f(51, -10, 0), f(44, -20, 0), 4.6, 2.2, 4, Math.PI / 4, st.metal, st.metalDark);
			tube(m, f(44, -20, 0), f(36, -24, 0), 2.2, 0, 4, Math.PI / 4, st.edge, st.metal);
		} else {
			tube(m, f(51, -10, 0), f(48, -22, 0), 4.6, 0, 4, Math.PI / 4, st.metal, st.metalDark);
		}
		tube(m, f(57, 0, 0), f(67, 0, 0), 3.8, 0, 4, Math.PI / 4, st.metal, st.metalDark);
		gem(m, 52, 1, 3.6, 8.6, st.accent, st.voidsteel ? st.accentCore : st.accent);
		return m;
	}

	static ModelData battleaxe(Style st) {
		ModelData m = new ModelData();
		haft(m, 2, 64, 2.6, 2.3, st.haft);
		pommel(m, 2, 1.2, st);
		grip(m, 4, 18, 2.9, 4, st);
		collar(m, 34, 62, 4.0, 3.6, st.metalDark);
		double[][] bit = st.voidsteel
				? new double[][] { { 38, 3 }, { 30, 12 }, { 20, 24 }, { 32, 25 }, { 38, 30 }, { 48, 26 }, { 60, 29 }, { 66, 24 }, { 76, 22 }, { 64, 10 }, { 58, 3 } }
				: new double[][] { { 38, 3 }, { 33, 10 }, { 25, 22 }, { 38, 25 }, { 50, 26 }, { 62, 25 }, { 72, 21 }, { 63, 10 }, { 58, 3 } };
		double[] bh = new double[bit.length];
		bh[0] = bh[bh.length - 1] = 2.4;
		bh[1] = bh[bh.length - 2] = 1.6;
		plateSW(m, bit, bh, new double[] { 48, 13 }, 2.6, st.metal, st.edge, st.metalDark);
		if (st.voidsteel) { // a second, hooked bit behind
			double[][] back = { { 40, -3 }, { 36, -10 }, { 30, -20 }, { 44, -16 }, { 56, -22 }, { 58, -10 }, { 56, -3 } };
			plateSW(m, back, new double[] { 2.2, 1.4, 0, 0, 0, 1.4, 2.2 }, new double[] { 47, -9 }, 2.2, st.metal, st.edge, st.metalDark);
		} else { // a star-pointed back spike
			tube(m, f(48, -3, 0), f(48, -17, 0), 3.6, 0, 4, Math.PI / 4, st.metal, st.metalDark);
		}
		tube(m, f(61, 0, 0), f(72, 0, 0), 3.4, 0, 4, Math.PI / 4, st.metal, st.metalDark);
		gem(m, 48, 0, 3.8, 7.0, st.accent, st.voidsteel ? st.accentCore : st.accent);
		return m;
	}

	static ModelData twoHanded(Style st) {
		ModelData m = new ModelData();
		pommel(m, 2.5, 1.6, st);
		grip(m, 5, 34, 2.3, 4.5, st);
		crossguard(m, 36, 14, 2.6, st);
		blade(m, new double[][] { { 38.5, 5.6, 1.7 }, { 48, 6.4, 1.8 }, { 108, 5.8, 1.6 }, { 128, 4.6, 1.4 } }, 147, st);
		// a ricasso collar below the guard
		collar(m, 38, 44, 3.4, 3.0, st.metalDark);
		return m;
	}

	static ModelData spear(Style st) {
		ModelData m = new ModelData();
		haft(m, 6, 222, 2.9, 2.6, st.haft);
		tube(m, f(0, 0, 0), f(6, 0, 0), 1.2, 3.1, 4, Math.PI / 4, st.metalDark, st.metalDark);
		grip(m, 106, 136, 3.2, 6, st);
		collar(m, 214, 228, 3.2, 3.8, st.metalDark);
		gem(m, 221, 0, 3.0, 7.5, st.accent, st.voidsteel ? st.accentCore : st.accent);
		bladeStrip(m, new double[][] { { 227, 3.6, 2.0 }, { 241, 6.2, 2.0 }, { 254, 4.2, 1.6 } }, 272, st.metal, st.edge);
		for (int side = -1; side <= 1; side += 2) {
			if (st.voidsteel) { // barbed wings swept back
				tube(m, f(232, side * 4.5, 0), f(218, side * 13, 0), 2.2, 0, 4, Math.PI / 4, st.metal, st.edge);
			} else { // star-pointed lugs
				tube(m, f(222, side * 3, 0), f(222, side * 8, 0), 1.5, 0, 4, Math.PI / 4, st.metal, st.metal);
			}
		}
		return m;
	}

	static ModelData halberd(Style st) {
		ModelData m = new ModelData();
		haft(m, 8, 204, 3.2, 2.9, st.haft);
		tube(m, f(0, 0, 0), f(9, 0, 0), 1.6, 3.8, 4, Math.PI / 4, st.metalDark, st.metalDark);
		grip(m, 104, 134, 3.6, 6, st);
		collar(m, 166, 176, 4.0, 5.2, st.metalDark);
		haft(m, 176, 214, 4.5, 4.1, st.metalDark);
		double[][] blade = st.voidsteel
				? new double[][] { { 178, 3 }, { 170, 16 }, { 156, 32 }, { 172, 33 }, { 182, 39 }, { 198, 35 }, { 212, 40 }, { 228, 32 }, { 238, 26 },
						{ 220, 16 }, { 214, 3 } }
				: new double[][] { { 178, 3 }, { 174, 13 }, { 162, 31 }, { 180, 35 }, { 198, 38 }, { 216, 36 }, { 234, 29 }, { 220, 16 }, { 212, 3 } };
		double[] h = new double[blade.length];
		h[0] = h[h.length - 1] = 2.4;
		h[1] = h[h.length - 2] = 1.8;
		plateSW(m, blade, h, new double[] { 196, 18 }, 2.6, st.metal, st.edge, st.metalDark);
		double[][] hook = st.voidsteel ? new double[][] { { 182, -3 }, { 206, -3 }, { 204, -14 }, { 188, -32 }, { 192, -16 }, { 184, -12 } }
				: new double[][] { { 182, -3 }, { 206, -3 }, { 200, -12 }, { 184, -27 }, { 188, -12 } };
		double[] hh = new double[hook.length];
		hh[0] = hh[1] = 2.0;
		plateSW(m, hook, hh, new double[] { 194, -8 }, 2.0, st.metal, st.edge, st.metalDark);
		bladeStrip(m, new double[][] { { 212, 3.8, 3.0 }, { 222, 5.0, 2.6 } }, 244, st.metal, st.edge);
		gem(m, 196, 18, 4.4, 6.8, st.accent, st.voidsteel ? st.accentCore : st.accent);
		return m;
	}

	/** Voidsteel's longbow: black-violet limbs, burning caps, an eye in the grip and a string of light. */
	static ModelData longbow(Style st, double bend) {
		ModelData m = new ModelData();
		bow(m, 185, bend, new double[] { 1.7, 2.4, 3.2, 3.8, 4.1, 3.8, 3.2, 2.4, 1.7 }, st.metal, st.band, st.grip, st.edge, st.edge);
		double k = Math.sqrt(185 / 127.0), gw = 6 * bend * k + 1.2;
		// the eye, open on the back of the grip
		tube(m, f(92.5, gw + 5, -1.8), f(92.5, gw + 5, 1.8), 4.2, 4.2, 8, 0, st.metal, st.accent);
		tube(m, f(92.5, gw + 5, -2.4), f(92.5, gw + 5, 2.4), 2.0, 2.0, 6, 0, st.accentCore, st.accentCore);
		tube(m, f(92.5, gw + 9, 0), f(92.5, gw + 14, 0), 1.8, 0, 4, Math.PI / 4, st.edge, st.metal);
		for (double s : new double[] { 30, 155 }) { // barbs off the limbs
			double w = 7.5 * bend * k;
			tube(m, f(s, w, 0), f(s + (s < 92 ? -8 : 8), w + 8, 0), 1.6, 0, 4, Math.PI / 4, st.metal, st.edge);
		}
		return m;
	}

	/* ====================================================================== */
	/*                                THE SETS                                */
	/* ====================================================================== */

	static final String[] SHAPES = { "dagger", "sword", "longsword", "scimitar", "mace", "warhammer", "battleaxe", "2h sword", "spear",
			"halberd", "longbow" };
	static final Pose[] POSES = { DAGGER, SWORD, LONGSWORD, SCIMITAR, MACE, WARHAMMER, BATTLEAXE, TWO_HANDED, SPEAR, HALBERD, LONGBOW };

	static ModelData build(int shape, Style st, boolean inv) {
		switch (shape) {
		case 0:
			return dagger(st);
		case 1:
			return sword(st);
		case 2:
			return longsword(st);
		case 3:
			return scimitar(st);
		case 4:
			return mace(st);
		case 5:
			return warhammer(st);
		case 6:
			return battleaxe(st);
		case 7:
			return twoHanded(st);
		case 8:
			return spear(st);
		case 9:
			return halberd(st);
		default:
			return longbow(st, inv ? 1.3 : 1.0);
		}
	}

	static final String[] STARMETAL_EXAMINES = { "A starmetal dagger. It rings like a struck bell when it's drawn.",
			"A starmetal sword. The shard in the guard catches light that isn't there.",
			"A starmetal longsword, Gatehold's finest work. It's lighter than it looks.",
			"A starmetal scimitar. The curve was Dorrin's idea; the star was the star's.",
			"A starmetal mace. There's a little star on top, still burning.",
			"A starmetal warhammer. Every blow rings out across the battlefield.",
			"A starmetal battleaxe. The edge is pale and very, very bright.",
			"A starmetal 2h sword. Hold it up at night and the stars hold still.",
			"A starmetal spear. It points at the sky when you're not holding it.",
			"A starmetal halberd. The Wardens would kill for one. Some have." };
	static final String[] VOIDSTEEL_EXAMINES = { "A voidsteel dagger. The dark went in, and something sharper came out.",
			"A voidsteel sword. The eye in the guard follows whatever you're about to hit.",
			"A voidsteel longsword. The edge burns violet and leaves the air colder.",
			"A voidsteel scimitar, curved like a closing eye.",
			"A voidsteel mace. It's heavier on the downswing than physics allows.",
			"A voidsteel warhammer. The beak was not the smith's idea.",
			"A voidsteel battleaxe. Its second bit grew there on its own.",
			"A voidsteel 2h sword. The barbs on the back point the way the dark went.",
			"A voidsteel spear. Its barbs keep whatever they catch.",
			"A voidsteel halberd. The blade is jagged where the Starless bit it.",
			"A voidsteel longbow. The string is a thread of light the dark didn't finish eating." };
	static final int[] STARMETAL_COSTS = { 18000, 30000, 45000, 36000, 22000, 60000, 70000, 95000, 28000, 90000 };
	static final int[] VOIDSTEEL_COSTS = { 60000, 90000, 140000, 110000, 70000, 170000, 190000, 260000, 85000, 250000, 160000 };

	static Rung[] set(int[] ids, Style st, String[] examines, int[] costs) {
		Rung[] r = new Rung[ids.length];
		for (int i = 0; i < ids.length; i++) {
			int shape = i;
			r[i] = new Rung(ids[i], st.material + " " + SHAPES[i], SHAPES[i], costs[i], examines[i], () -> build(shape, st, false),
					() -> build(shape, st, true), POSES[i]);
		}
		return r;
	}

	static final Rung[] STARMETAL_SET = set(VoidConstants.STARMETAL_SET, STARMETAL_STYLE, STARMETAL_EXAMINES, STARMETAL_COSTS);
	static final Rung[] VOIDSTEEL_SET = set(VoidConstants.VOIDSTEEL_SET, VOIDSTEEL_STYLE, VOIDSTEEL_EXAMINES, VOIDSTEEL_COSTS);
}
