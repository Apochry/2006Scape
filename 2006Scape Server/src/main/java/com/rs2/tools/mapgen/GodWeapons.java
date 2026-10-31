package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.LadderWeapons.*;
import static com.rs2.tools.mapgen.ModelData.hsl;
import static com.rs2.tools.mapgen.QuestWeapons.*;

import com.rs2.game.content.voidrealm.VoidConstants;

/**
 * The god tier: ladder ranks 31-45, the weapons of the world's powers. They are bigger than anything below them, with
 * oversized blades, parts that float free of the weapon (separate pieces in one model), crowns of spikes, burning
 * cores and layered bands. Each keeps a dark body so its one or two bright accents read in the client.
 *
 * <p>Same frames and poses as {@link LadderWeapons} (and {@link QuestWeapons} for the halberd, battleaxe, shortbow and
 * staff); the blades may run past the base item's length, as the Last Light's does.
 */
public final class GodWeapons {

	private GodWeapons() {
	}

	static final int GOLD = hsl(8, 4, 64), GOLD_DARK = hsl(8, 5, 44), GOLD_BRIGHT = hsl(9, 2, 106), WHITE_HOT = hsl(9, 1, 124),
			EMBER = hsl(4, 6, 74), EMBER_HOT = hsl(6, 5, 100), RUBY = hsl(0, 6, 52), GHOST = hsl(23, 4, 92), GHOST_CORE = hsl(23, 2, 120);

	/* ====================================================================== */
	/*                                 HELPERS                                */
	/* ====================================================================== */

	/** A ring of {@code seg} tube segments, radius R, centred on frame point (cs, cw, ct), in the plane of U and V (frame axes). */
	static void ring(ModelData m, double cs, double cw, double ct, double[] U, double[] V, double R, double r, int seg, int col, int col2) {
		double[] u = unit(U), w = unit(V);
		for (int i = 0; i < seg; i++) {
			double a0 = Math.PI * 2 * i / seg, a1 = Math.PI * 2 * (i + 1) / seg;
			double[] p0 = f(cs + R * (u[0] * Math.cos(a0) + w[0] * Math.sin(a0)), cw + R * (u[1] * Math.cos(a0) + w[1] * Math.sin(a0)),
					ct + R * (u[2] * Math.cos(a0) + w[2] * Math.sin(a0)));
			double[] p1 = f(cs + R * (u[0] * Math.cos(a1) + w[0] * Math.sin(a1)), cw + R * (u[1] * Math.cos(a1) + w[1] * Math.sin(a1)),
					ct + R * (u[2] * Math.cos(a1) + w[2] * Math.sin(a1)));
			tube(m, p0, p1, r, r, 3, 0, i % 2 == 0 ? col : col2, col);
		}
	}

	/** A flat disc facing along t (in the blade's plane), centred at (s, w). */
	static void disc(ModelData m, double s, double w, double r, double half, int sides, int col) {
		tube(m, f(s, w, -half), f(s, w, half), r, r, sides, 0, col, col);
	}

	/** A floating shard: a small double cone centred at frame point c, pointing along frame direction d. */
	static void shard(ModelData m, double[] c, double[] d, double len, double r, int col, int col2) {
		double[] dir = unit(d);
		double[] a = f(c[0] + dir[0] * len, c[1] + dir[1] * len, c[2] + dir[2] * len),
				b = f(c[0] - dir[0] * len * 0.6, c[1] - dir[1] * len * 0.6, c[2] - dir[2] * len * 0.6), o = f(c[0], c[1], c[2]);
		tube(m, o, a, r, 0, 4, Math.PI / 4, col, col);
		tube(m, o, b, r, 0, 4, Math.PI / 4, col2, col2);
	}

	/** A radiating corona of {@code n} rays in the blade's plane (s-w) round (cs, cw), alternately long and short. */
	static void corona(ModelData m, double cs, double cw, double r0, double rLong, double rShort, int n, double width, int hot, int warm) {
		for (int k = 0; k < n; k++) {
			double a = Math.PI * 2 * k / n + Math.PI / n;
			double len = k % 2 == 0 ? rLong : rShort;
			tube(m, f(cs + Math.cos(a) * r0, cw + Math.sin(a) * r0, 0), f(cs + Math.cos(a) * len, cw + Math.sin(a) * len, 0), width, 0, 4,
					a + Math.PI / 4, k % 2 == 0 ? hot : warm, warm);
		}
	}

	/** A grip from s0 to s1 in {@code col}, wound with bands of {@code band} every {@code step}. */
	static void wound(ModelData m, double s0, double s1, double r, double step, int col, int bandCol) {
		tube(m, f(s0, 0, 0), f(s1, 0, 0), r, r * 1.06, 4, Math.PI / 4, col, col);
		for (double s = s0 + step * 0.5; s + 0.9 < s1; s += step) {
			band(m, s, s + 0.9, r * 1.22, bandCol);
		}
	}

	/* ====================================================================== */
	/*                         31. WARDENSOATH 2H SWORD                       */
	/* ====================================================================== */

	/**
	 * The first Wardens swore their oath on this blade at the foot of the first wall: a broad silver greatsword, a guard
	 * built like a battlement, a fuller of night, and three stars that keep watch beside it and never touch it.
	 */
	static ModelData wardensoath() {
		ModelData m = new ModelData();
		int silver = hsl(40, 1, 90), silverDark = hsl(40, 1, 64), edge = hsl(40, 0, 122);
		star(m, f(1.5, 0, 0), 5.0, 1.8, STAR_WHITE, GOLD);
		collar(m, 3.5, 6, 3.0, 2.2, GOLD_DARK);
		wound(m, 6, 34, 2.3, 4.5, NIGHT, GOLD);
		// the battlement guard: a wall with merlons rising towards the blade, a tower at each end
		tube(m, f(36, -17, 0), f(36, 17, 0), 2.6, 2.6, 4, Math.PI / 4, GOLD_DARK, GOLD);
		for (double w : new double[] { -12, -6, 6, 12 }) {
			tube(m, f(37, w, 0), f(41.5, w, 0), 2.0, 2.0, 4, Math.PI / 4, GOLD, GOLD);
		}
		for (int side = -1; side <= 1; side += 2) {
			tube(m, f(32, side * 18.5, 0), f(43, side * 18.5, 0), 3.0, 3.0, 4, Math.PI / 4, GOLD, GOLD_DARK);
			tube(m, f(43, side * 18.5, 0), f(47, side * 18.5, 0), 3.0, 0, 4, Math.PI / 4, NIGHT, NIGHT);
		}
		// the blade: broad, long, honed white
		bladeStrip(m, new double[][] { { 38, 7.6, 1.9 }, { 62, 8.4, 1.9 }, { 122, 7.6, 1.7 }, { 144, 5.4, 1.5 } }, 166, silver, edge, 0.5);
		diamond(m, new double[][] { { 40, 1.8, 2.15 }, { 112, 1.4, 1.95 } }, 124, 0, NIGHT, silverDark);
		// three watch-stars, floating beside the blade
		star(m, f(74, 16, 0), 3.8, 1.3, STAR_WHITE, GOLD_BRIGHT);
		star(m, f(104, -16, 0), 3.8, 1.3, STAR_WHITE, GOLD_BRIGHT);
		star(m, f(134, 14, 0), 3.8, 1.3, STAR_WHITE, GOLD_BRIGHT);
		return m;
	}

	/* ====================================================================== */
	/*                       32. HEART OF AUREL LONGSWORD                     */
	/* ====================================================================== */

	/** Aurel's heart, burning again, threaded on the spine of a black-iron longsword whose edges glow like a forge. */
	static ModelData heartOfAurel() {
		ModelData m = new ModelData();
		int iron = hsl(0, 2, 18), ironLight = hsl(0, 2, 30), heart = hsl(1, 6, 66), heartCore = hsl(6, 4, 118);
		knob(m, 1.5, 3.2, 2.6, GOLD, GOLD_DARK);
		wound(m, 3.5, 17, 1.8, 3.4, LEATHER_DARK, GOLD);
		// two gold horns sweep up from the guard round the heart
		collar(m, 17, 20, 2.8, 2.6, GOLD_DARK);
		for (int side = -1; side <= 1; side += 2) {
			tube(m, f(18.5, 0, 0), f(21, side * 7, 0), 2.0, 1.6, 4, Math.PI / 4, GOLD_DARK, GOLD);
			tube(m, f(21, side * 7, 0), f(32, side * 8.5, 0), 1.6, 1.0, 4, Math.PI / 4, GOLD, GOLD);
			tube(m, f(32, side * 8.5, 0), f(37, side * 5.2, 0), 1.0, 0, 4, Math.PI / 4, GOLD_BRIGHT, GOLD);
		}
		// the spine the heart is threaded on, and the heart, with its flames
		tube(m, f(20, 0, 0), f(36, 0, 0), 1.1, 1.1, 4, Math.PI / 4, ironLight, ironLight);
		tube(m, f(22, 0, 0), f(27.5, 0, 0), 0.6, 4.4, 6, 0.3, heart, heart);
		tube(m, f(27.5, 0, 0), f(34, 0, 0), 4.4, 0.6, 6, 0.3, heartCore, heart);
		for (int k = 0; k < 6; k++) {
			double a = Math.PI * 2 * k / 6;
			tube(m, f(27.5, Math.cos(a) * 4, Math.sin(a) * 4), f(31 + (k % 2) * 2, Math.cos(a) * 7.5, Math.sin(a) * 7.5), 1.3, 0, 4, a, EMBER_HOT, EMBER);
		}
		// the blade, black iron, its edges glowing
		bladeStrip(m, new double[][] { { 35, 4.6, 1.3 }, { 64, 4.8, 1.3 }, { 96, 3.6, 1.1 }, { 104, 2.8, 1.0 } }, 120, iron, EMBER_HOT, 0.5);
		diamond(m, new double[][] { { 37, 0.9, 1.5 }, { 80, 0.7, 1.4 } }, 92, 0, EMBER, heart);
		return m;
	}

	/* ====================================================================== */
	/*                            33. SCARMAW HALBERD                         */
	/* ====================================================================== */

	/** The Scar given teeth: a jaw of black glass on a haft of night, the rift's light in its throat, shards circling it. */
	static ModelData scarmaw() {
		ModelData m = new ModelData();
		int glass = violet(12, 2), glassLight = violet(28, 3), edge = violet(108, 5), tooth = violet(120, 3), seam = violet(124, 2);
		haft(m, 8, 206, 3.2, 2.9, violet(14, 2));
		tube(m, f(-4, 0, 0), f(9, 0, 0), 0, 3.6, 4, Math.PI / 4, edge, glassLight);
		wound(m, 104, 134, 3.4, 6, BLACKSTEEL_LIGHT, violet(62, 6));
		collar(m, 168, 178, 3.6, 5.6, glassLight);
		haft(m, 178, 214, 4.8, 4.2, glass);
		// the upper jaw (+w): a great crescent, saw-toothed along its edge
		double[][] jaw = { { 180, 3 }, { 172, 15 }, { 160, 32 }, { 178, 36 }, { 196, 40 }, { 214, 39 }, { 232, 33 }, { 242, 26 }, { 224, 16 },
				{ 218, 3 } };
		plateSW(m, jaw, new double[] { 2.6, 1.8, 0, 0, 0, 0, 0, 0, 1.8, 2.6 }, new double[] { 200, 20 }, 2.8, glassLight, edge, glass);
		for (double[] t : new double[][] { { 168, 35, -150 }, { 186, 39, 100 }, { 204, 41, 95 }, { 222, 37, 70 }, { 238, 30, 45 } }) {
			double a = Math.toRadians(t[2]);
			tube(m, f(t[0], t[1] - 1, 0), f(t[0] + Math.cos(a) * 7, t[1] + Math.sin(a) * 7, 0), 2.2, 0, 4, Math.PI / 4, tooth, edge);
		}
		// the lower jaw (-w), and its fangs
		double[][] low = { { 184, -3 }, { 212, -3 }, { 210, -14 }, { 196, -30 }, { 198, -16 }, { 188, -12 } };
		plateSW(m, low, new double[] { 2.2, 2.2, 1.0, 0, 0.6, 0.6 }, new double[] { 200, -9 }, 2.2, glassLight, edge, glass);
		tube(m, f(206, -12, 0), f(214, -20, 0), 1.8, 0, 4, Math.PI / 4, tooth, edge);
		// the throat: the rift's seam, and the spike
		tube(m, f(186, 9, 0), f(220, 30, 0), 1.7, 1.7, 4, Math.PI / 4, seam, seam);
		bladeStrip(m, new double[][] { { 212, 4.2, 3.0 }, { 226, 5.6, 2.6 } }, 258, glassLight, edge);
		// four shards circling the head, free of it
		for (int k = 0; k < 4; k++) {
			double a = Math.PI / 4 + Math.PI / 2 * k;
			shard(m, v(206 + (k % 2) * 10, Math.cos(a) * 26, Math.sin(a) * 26), v(1, 0, 0), 6, 2.0, tooth, edge);
		}
		return m;
	}

	/* ====================================================================== */
	/*                        34. HOLLOW CROWN WARHAMMER                      */
	/* ====================================================================== */

	/** The Hollow King's crown, beaten round a block of grave-iron: a halo of gold spikes, and ghost-light in both faces. */
	static ModelData hollowCrown() {
		ModelData m = new ModelData();
		int gold = hsl(8, 3, 54), goldBright = hsl(9, 2, 96);
		haft(m, 2, 62, 2.6, 2.3, BLACKSTEEL);
		knob(m, 1.5, 3.6, 3.0, gold, GOLD_DARK);
		wound(m, 4, 20, 2.9, 4, LEATHER_DARK, gold);
		collar(m, 40, 45, 2.8, 4.0, gold);
		// the head: a great block of black iron, its faces burning with ghost-light
		tube(m, f(54, -11, 0), f(54, 11, 0), 9.6, 9.6, 4, Math.PI / 4, BLACKSTEEL_LIGHT, BLACKSTEEL);
		for (int side = -1; side <= 1; side += 2) {
			tube(m, f(54, side * 11, 0), f(54, side * 13, 0), 9.6, 8.0, 4, Math.PI / 4, BLACKSTEEL, BLACKSTEEL);
			tube(m, f(54, side * 13, 0), f(54, side * 13.6, 0), 5.0, 3.6, 4, Math.PI / 4, GHOST, GHOST_CORE);
		}
		// the crown: a gold band round the head and a halo of spikes in the s-t plane
		tube(m, f(54, -3.5, 0), f(54, 3.5, 0), 10.4, 10.4, 4, Math.PI / 4, gold, gold);
		for (int k = 0; k < 10; k++) {
			double a = Math.PI * 2 * k / 10 + Math.PI / 10;
			double len = k % 2 == 0 ? 21 : 16;
			tube(m, f(54 + Math.cos(a) * 9, 0, Math.sin(a) * 9), f(54 + Math.cos(a) * len, 0, Math.sin(a) * len), 2.4, 0, 4, a,
					k % 2 == 0 ? goldBright : gold, gold);
		}
		// the ghost in the crown: a green eye on each side of the band
		gem(m, 54, 0, 2.8, 11.6, GHOST_CORE, GHOST);
		return m;
	}

	/* ====================================================================== */
	/*                          35. DAWNBRINGER LONGBOW                       */
	/* ====================================================================== */

	/** The bow that shot the first dawn over Nythera: white-gold limbs, a halo round the grip, three new stars rising before it. */
	static ModelData dawnbringer(double bend) {
		ModelData m = new ModelData();
		int pale = hsl(9, 1, 104), paleGold = hsl(8, 3, 78), string = hsl(8, 2, 112);
		bow(m, 185, bend, new double[] { 1.8, 2.4, 3.2, 3.8, 4.1, 3.8, 3.2, 2.4, 1.8 }, pale, paleGold, NIGHT, GOLD, string);
		double rk = Math.sqrt(185 / 127.0), gw = 6 * bend * rk + 1.2;
		ring(m, 92.5, gw + 7, 0, v(1, 0, 0), v(0, 1, 0), 10, 1.1, 12, GOLD_BRIGHT, GOLD);
		disc(m, 92.5, gw + 7, 4.2, 1.2, 8, WHITE_HOT);
		star(m, f(64, gw + 21, 0), 3.4, 1.2, STAR_WHITE, GOLD_BRIGHT);
		star(m, f(92.5, gw + 27, 0), 5.0, 1.6, STAR_WHITE, GOLD_BRIGHT);
		star(m, f(121, gw + 21, 0), 3.4, 1.2, STAR_WHITE, GOLD_BRIGHT);
		return m;
	}

	/* ====================================================================== */
	/*                           36. WAND OF THE UNMADE                       */
	/* ====================================================================== */

	/** A rod of the void with a piece of nothing at its tip: a black orb, six shards of the world it ate circling it. */
	static ModelData unmadeWand() {
		ModelData m = new ModelData();
		int rod = violet(8, 1), rodLight = violet(20, 2), bandCol = violet(62, 6), shardCol = violet(98, 6), core = violet(124, 2),
				orb = violet(4, 1);
		tube(m, f(3, 0, 0), f(80, 0, 0), 2.2, 1.6, 6, 0, rod, rodLight);
		tube(m, f(0, 0, 0), f(4, 0, 0), 0.8, 2.6, 6, 0, bandCol, bandCol);
		band(m, 14, 30, 2.6, LEATHER_DARK);
		for (double s : new double[] { 33, 52, 70 }) {
			collar(m, s, s + 1.2, 2.4, 2.4, bandCol);
		}
		for (int k = 0; k < 3; k++) {
			double a = Math.PI * 2 * k / 3;
			tube(m, f(78, Math.cos(a) * 1.6, Math.sin(a) * 1.6), f(86, Math.cos(a) * 5.4, Math.sin(a) * 5.4), 1.0, 0.6, 3, a, rodLight, rodLight);
		}
		// the orb of nothing, and the glint deep in it
		tube(m, f(84, 0, 0), f(92, 0, 0), 0, 6.5, 8, 0.2, orb, orb);
		tube(m, f(92, 0, 0), f(100, 0, 0), 6.5, 0, 8, 0.2, orb, orb);
		gem(m, 92, 0, 1.6, 7.4, core, core);
		// six shards in a tilted orbit
		double[] U = { 0.35, 0.94, 0 }, V = { 0, 0, 1 };
		for (int k = 0; k < 6; k++) {
			double a = Math.PI * 2 * k / 6;
			double[] c = { 92 + 13 * (U[0] * Math.cos(a) + V[0] * Math.sin(a)), 13 * (U[1] * Math.cos(a) + V[1] * Math.sin(a)),
					13 * (U[2] * Math.cos(a) + V[2] * Math.sin(a)) };
			shard(m, c, v(c[0] - 92, c[1], c[2]), 3.6, 1.4, shardCol, core);
		}
		return m;
	}

	/* ====================================================================== */
	/*                        37. SPEAR OF THE FIRST STAR                     */
	/* ====================================================================== */

	/** The first star that fell, made a spear-head: a white star at the socket, a long white point, gold rings hung round it. */
	static ModelData firstStarSpear() {
		ModelData m = new ModelData();
		int haftCol = hsl(40, 1, 74), haftDark = hsl(40, 1, 56), white = hsl(40, 0, 118), whiteDark = hsl(40, 1, 92);
		haft(m, 6, 210, 2.9, 2.6, haftCol);
		tube(m, f(-6, 0, 0), f(6, 0, 0), 0, 3.2, 4, Math.PI / 4, GOLD, GOLD_DARK);
		wound(m, 106, 136, 3.0, 6, NIGHT, GOLD);
		tube(m, f(150, 0, 0), f(176, 0, 0), 2.75, 2.75, 4, Math.PI / 4, haftDark, haftDark);
		collar(m, 206, 218, 3.0, 4.2, GOLD_DARK);
		// the star at the socket
		star(m, f(222, 0, 0), 17, 4.4, STAR_WHITE, GOLD_BRIGHT);
		// the point: a long four-edged diamond
		diamond(m, new double[][] { { 224, 4.6, 4.6 }, { 246, 5.6, 5.6 }, { 270, 3.2, 3.2 } }, 300, 0, white, whiteDark);
		// two gold rings hung round the haft below the star, touching nothing
		ring(m, 198, 0, 0, v(0, 1, 0), v(0, 0, 1), 10, 1.2, 10, GOLD_BRIGHT, GOLD);
		ring(m, 186, 0, 0, v(0, 1, 0), v(0, 0, 1), 7, 1.0, 8, GOLD_BRIGHT, GOLD);
		return m;
	}

	/* ====================================================================== */
	/*                          38. CROWNFIRE SCIMITAR                        */
	/* ====================================================================== */

	/** The old kingdom's crown, melted in Aurel's heart-fire and poured into a blade: crimson steel, a crown for a guard, flame on its back. */
	static ModelData crownfire() {
		ModelData m = new ModelData();
		int steel = hsl(0, 6, 26), steelLight = hsl(0, 6, 40), edge = hsl(7, 3, 112);
		tube(m, f(-2.5, 0, 0), f(1.5, 0, 0), 0, 3.0, 6, 0, RUBY, RUBY);
		tube(m, f(1.5, 0, 0), f(4.5, 0, 0), 3.0, 1.9, 6, 0, GOLD, GOLD_DARK);
		wound(m, 4.5, 26, 1.9, 4, LEATHER_DARK, GOLD);
		// the crown guard: a gold circlet whose points rise along the blade
		collar(m, 26, 30, 2.4, 6.4, GOLD_DARK);
		collar(m, 30, 32, 6.4, 6.4, GOLD);
		for (int k = 0; k < 5; k++) {
			double a = Math.PI * 2 * k / 5 + 0.3;
			tube(m, f(31.5, Math.cos(a) * 6, Math.sin(a) * 6), f(39, Math.cos(a) * 7.2, Math.sin(a) * 7.2), 1.8, 0, 4, a, GOLD_BRIGHT, GOLD);
		}
		gem(m, 31, 0, 2.2, 7.2, RUBY, EMBER_HOT);
		// the blade, deeply curved
		plateSW(m, new double[][] { { 32, -1.8 }, { 52, -3.0 }, { 74, -5.6 }, { 74, 7.6 }, { 52, 8.8 }, { 33, 5.0 } },
				new double[] { 1.2, 1.2, 1.1, 0, 0, 0 }, new double[] { 52, 2.4 }, 1.1, steelLight, edge, steel);
		plateSW(m, new double[][] { { 72, -5.4 }, { 90, -11 }, { 108, -21 }, { 100, -8 }, { 87, 1.5 }, { 72, 7.6 } },
				new double[] { 1.1, 1.0, 0, 0, 0, 0 }, new double[] { 86, -3 }, 1.0, steelLight, edge, steel);
		// flames licking back off the spine
		for (double[] fl : new double[][] { { 44, -2.6 }, { 58, -3.8 }, { 72, -5.4 }, { 86, -9.5 } }) {
			tube(m, f(fl[0], fl[1] + 0.4, 0), f(fl[0] - 8, fl[1] - 7, 0), 2.0, 0, 4, Math.PI / 4, EMBER_HOT, EMBER);
		}
		return m;
	}

	/* ====================================================================== */
	/*                         39. GATEBREAKER BATTLEAXE                      */
	/* ====================================================================== */

	/**
	 * The founders broke the old king's gate with it, then built a better one: two broad bits of night-steel faced with
	 * a gold portcullis, a tower for a pommel and the Wardens' star on top.
	 */
	static ModelData gatebreaker() {
		ModelData m = new ModelData();
		int steel = hsl(42, 3, 40), steelDark = NIGHT_DARK;
		haft(m, 2, 70, 2.8, 2.5, NIGHT_DARK);
		// the tower pommel, crenellated
		tube(m, f(-3, 0, 0), f(4, 0, 0), 4.2, 4.2, 4, Math.PI / 4, GOLD_DARK, GOLD);
		wound(m, 5, 21, 3.0, 4, LEATHER_DARK, GOLD);
		collar(m, 34, 64, 4.2, 3.8, GOLD_DARK);
		double[][] front = { { 37, 3 }, { 29, 13 }, { 16, 30 }, { 32, 34 }, { 50, 35 }, { 68, 34 }, { 84, 30 }, { 71, 13 }, { 61, 3 } };
		plateSW(m, front, new double[] { 2.8, 1.8, 0, 0, 0, 0, 0, 1.8, 2.8 }, new double[] { 50, 17 }, 3.0, steel, GOLD_BRIGHT, steelDark);
		double[][] back = { { 39, -3 }, { 31, -12 }, { 19, -27 }, { 34, -30 }, { 50, -31 }, { 66, -30 }, { 81, -27 }, { 69, -12 }, { 59, -3 } };
		plateSW(m, back, new double[] { 2.8, 1.8, 0, 0, 0, 0, 0, 1.8, 2.8 }, new double[] { 50, -15 }, 3.0, steel, GOLD_BRIGHT, steelDark);
		// the gold portcullis on both cheeks of both bits
		for (int side = -1; side <= 1; side += 2) {
			for (double t : new double[] { 3.3, -3.3 }) {
				for (double s : new double[] { 42, 50, 58 }) {
					tube(m, f(s, side * 6, t), f(s, side * 24, t), 0.7, 0.7, 3, 0, GOLD, GOLD);
				}
				for (double w : new double[] { 12, 19 }) {
					tube(m, f(38, side * w, t), f(62, side * w, t), 0.7, 0.7, 3, 0, GOLD, GOLD);
				}
			}
		}
		// the Wardens' star on top
		tube(m, f(64, 0, 0), f(70, 0, 0), 3.8, 2.4, 4, Math.PI / 4, GOLD, GOLD_DARK);
		star(m, f(75, 0, 0), 6.0, 2.0, STAR_WHITE, GOLD_BRIGHT);
		return m;
	}

	/* ====================================================================== */
	/*                          40. NIGHTFALL SHORTBOW                        */
	/* ====================================================================== */

	/** The Starless's own bow: black horn, a string of violet fire, and an eclipse that hangs behind the grip. */
	static ModelData nightfall(double bend) {
		ModelData m = new ModelData();
		int horn = violet(8, 1), hornLight = violet(20, 2), fire = violet(110, 5), glow = violet(90, 6);
		bow(m, 127, bend, new double[] { 1.6, 2.3, 3.1, 3.6, 3.9, 3.6, 3.1, 2.3, 1.6 }, hornLight, horn, horn, fire, fire);
		double gw = 6 * bend + 1.2;
		// horns curling off each tip
		tube(m, f(4, 4 * bend - 1, 0), f(-6, 4 * bend + 8, 0), 1.6, 0, 4, Math.PI / 4, horn, fire);
		tube(m, f(123, 4 * bend - 1, 0), f(133, 4 * bend + 8, 0), 1.6, 0, 4, Math.PI / 4, horn, fire);
		// the eclipse behind the grip: a black disc, a burning ring, an eye in the middle
		disc(m, 63.5, gw + 10, 6.6, 1.4, 10, violet(4, 1));
		ring(m, 63.5, gw + 10, 0, v(1, 0, 0), v(0, 1, 0), 8.6, 1.2, 12, fire, glow);
		disc(m, 63.5, gw + 10, 2.0, 1.9, 6, STAR_WHITE);
		corona(m, 63.5, gw + 10, 9.6, 17, 13, 10, 1.4, fire, glow);
		return m;
	}

	/* ====================================================================== */
	/*                           41. RIFTCALLER STAFF                         */
	/* ====================================================================== */

	/** It calls the rift open wherever it is planted: two jagged leaves of rift-glass held apart, the seam blazing between them. */
	static ModelData riftcaller() {
		ModelData m = new ModelData();
		int glass = violet(46, 5), glassDark = violet(28, 4), edge = violet(108, 5), seam = violet(124, 2), haftCol = violet(14, 2);
		tube(m, f(6, 0, 0), f(198, 0, 0), 3.6, 4.0, 6, 0, haftCol, haftCol);
		tube(m, f(-2, 0, 0), f(6, 0, 0), 1.2, 3.9, 6, 0, edge, glassDark);
		band(m, 100, 138, 4.2, BLACKSTEEL_LIGHT);
		for (double s : new double[] { 40, 98, 138, 176 }) {
			collar(m, s, s + 2.5, 4.6, 4.6, violet(62, 6));
		}
		collar(m, 194, 202, 4.2, 6.4, glassDark);
		// the two leaves, held apart
		for (int side = -1; side <= 1; side += 2) {
			tube(m, f(200, side * 3, 0), f(208, side * 7, 0), 1.8, 1.4, 4, Math.PI / 4, glassDark, glassDark);
			double[][] leaf = { { 204, 5 }, { 214, 17 }, { 228, 14 }, { 242, 22 }, { 258, 10 }, { 240, 6.5 }, { 222, 8 } };
			for (double[] p : leaf) {
				p[1] *= side;
			}
			plateSW(m, leaf, new double[] { 1.8, 0, 0, 0, 0, 1.6, 1.6 }, new double[] { 230, side * 12 }, 2.0, glass, edge, glassDark);
		}
		// the seam, burning
		diamond(m, new double[][] { { 206, 2.8, 1.8 }, { 232, 4.0, 2.2 } }, 264, 0, seam, edge);
		// shards drawn out of the rift, circling
		for (int k = 0; k < 4; k++) {
			double a = Math.PI * 2 * k / 4 + 0.4;
			shard(m, v(230 + (k % 2) * 8, Math.cos(a) * 28, Math.sin(a) * 28), v(1, 0, 0), 6, 2.0, edge, glass);
		}
		return m;
	}

	/* ====================================================================== */
	/*                            42. SUNFALL 2H SWORD                        */
	/* ====================================================================== */

	/** The sun brought down in Aurel's heart-fire: a wavy gold greatsword, its edges white with heat, the sun itself for a guard. */
	static ModelData sunfall() {
		ModelData m = new ModelData();
		int goldBlade = hsl(8, 4, 70), goldShade = hsl(7, 5, 48), edge = hsl(9, 1, 122);
		knob(m, 2, 3.4, 3.0, GOLD, GOLD_DARK);
		wound(m, 4, 31, 2.3, 4.5, hsl(1, 3, 22), GOLD);
		// the sun guard
		disc(m, 36, 0, 9.0, 2.0, 10, GOLD);
		disc(m, 36, 0, 5.0, 2.6, 10, WHITE_HOT);
		corona(m, 36, 0, 8.5, 22, 15, 12, 2.3, EMBER_HOT, EMBER);
		// the flamberge blade, in two plates, waved along both edges
		for (int half = 0; half < 2; half++) {
			double s0 = half == 0 ? 42 : 96, s1 = half == 0 ? 98 : 150;
			java.util.List<double[]> up = new java.util.ArrayList<>(), down = new java.util.ArrayList<>();
			for (double s = s0; s <= s1 + 0.1; s += 7) {
				double taper = half == 0 ? 1 : 1 - (s - 96) / 70.0;
				double wv = (8.6 + 1.8 * Math.sin((s - 42) / 7.0 * Math.PI / 2)) * taper;
				up.add(new double[] { s, wv });
				down.add(new double[] { s, -wv });
			}
			java.util.List<double[]> pts = new java.util.ArrayList<>(up);
			if (half == 1) {
				pts.add(new double[] { 172, 0 });
			}
			for (int i = down.size() - 1; i >= 0; i--) {
				pts.add(down.get(i));
			}
			double[] h = new double[pts.size()];
			java.util.Arrays.fill(h, 0);
			h[0] = 1.6;
			h[h.length - 1] = 1.6;
			if (half == 0) { // the far ends meet the second plate: give them thickness
				h[up.size() - 1] = 1.6;
				h[up.size()] = 1.6;
			}
			plateSW(m, pts.toArray(new double[0][]), h, new double[] { (s0 + s1) / 2, 0 }, 1.9, goldBlade, edge, goldShade);
		}
		diamond(m, new double[][] { { 42, 1.6, 2.2 }, { 120, 1.2, 2.0 } }, 134, 0, EMBER_HOT, EMBER);
		return m;
	}

	/* ====================================================================== */
	/*                          43. BLACK SUN WARHAMMER                       */
	/* ====================================================================== */

	/** The Starless's heart, struck off it: a black sun with a crown of white-violet fire, and a ring that circles it for ever. */
	static ModelData blackSun() {
		ModelData m = new ModelData();
		int black = violet(6, 1), blackLight = violet(16, 2), corona = violet(84, 6), hot = violet(112, 4);
		haft(m, 2, 62, 2.6, 2.3, black);
		tube(m, f(-2, 0, 0), f(3, 0, 0), 0, 3.4, 6, 0, corona, corona);
		wound(m, 4, 20, 2.9, 4, blackLight, corona);
		collar(m, 40, 45, 2.8, 4.0, blackLight);
		// the black sun: a drum across the haft, eclipse-rings burning on both faces
		tube(m, f(54, -11, 0), f(54, 11, 0), 9.0, 9.0, 8, 0, blackLight, black);
		for (int side = -1; side <= 1; side += 2) {
			ring(m, 54, side * 11.2, 0, v(1, 0, 0), v(0, 0, 1), 7.6, 1.2, 10, hot, corona);
		}
		// the crown of fire, in the s-t plane round the drum
		for (int k = 0; k < 12; k++) {
			double a = Math.PI * 2 * k / 12 + Math.PI / 12;
			double len = k % 2 == 0 ? 22 : 15.5;
			tube(m, f(54 + Math.cos(a) * 8.6, 0, Math.sin(a) * 8.6), f(54 + Math.cos(a) * len, 0, Math.sin(a) * len), 2.2, 0, 4, a,
					k % 2 == 0 ? hot : corona, corona);
		}
		// the ring that circles it, free of it
		ring(m, 54, 0, 0, v(1, 0, 0), v(0, 1, 0.35), 26, 1.0, 14, corona, hot);
		return m;
	}

	/* ====================================================================== */
	/*                         44. CROWN OF STARS STAFF                       */
	/* ====================================================================== */

	/** The old kingdom's crown, as the sky wore it: seven stars hung in a ring of gold over a staff of night, the first star at the heart. */
	static ModelData crownOfStars() {
		ModelData m = new ModelData();
		tube(m, f(6, 0, 0), f(200, 0, 0), 3.6, 4.0, 6, 0, NIGHT, NIGHT_DARK);
		tube(m, f(-2, 0, 0), f(6, 0, 0), 1.2, 3.9, 6, 0, GOLD, GOLD_DARK);
		band(m, 100, 138, 4.2, NIGHT_DARK);
		for (double s : new double[] { 30, 98, 138, 170 }) {
			collar(m, s, s + 2.5, 4.6, 4.6, GOLD);
		}
		for (double s : new double[] { 50, 74, 160, 184 }) {
			gem(m, s, 0, 1.2, 4.8, GOLD_BRIGHT, GOLD_BRIGHT);
		}
		collar(m, 198, 206, 4.2, 7.0, GOLD_DARK);
		tube(m, f(206, 0, 0), f(212, 0, 0), 7.0, 2.0, 6, 0, GOLD, GOLD);
		// the floating crown: a gold ring with seven star-points standing on it
		double cs = 228, R = 20;
		ring(m, cs, 0, 0, v(0, 1, 0), v(0, 0, 1), R, 1.2, 14, GOLD_BRIGHT, GOLD);
		for (int k = 0; k < 7; k++) {
			double a = Math.PI * 2 * k / 7;
			double[] c = { cs + 3, Math.cos(a) * R, Math.sin(a) * R };
			tube(m, f(c[0] - 3, c[1], c[2]), f(c[0] + 11, c[1], c[2]), 2.6, 0, 4, a, STAR_WHITE, GOLD_BRIGHT);
			tube(m, f(c[0] - 3, c[1], c[2]), f(c[0] - 7, c[1], c[2]), 2.6, 0, 4, a, GOLD, GOLD);
		}
		// the first star, hanging in the middle
		star(m, f(cs + 2, 0, 0), 13, 3.6, WHITE_HOT, GOLD_BRIGHT);
		tube(m, f(cs + 2, 0, 0), f(cs + 8, 0, 0), 5.0, 0, 6, 0, WHITE_HOT, WHITE_HOT);
		tube(m, f(cs + 2, 0, 0), f(cs - 4, 0, 0), 5.0, 0, 6, 0, GOLD_BRIGHT, GOLD_BRIGHT);
		return m;
	}

	/* ====================================================================== */
	/*                             45. GODSFALL 2H SWORD                      */
	/* ====================================================================== */

	/**
	 * Forged from all of them: the Starless's black for its core, the old crown's gold for its body, the first star's
	 * white for its edge, Aurel's heart in the guard, an eclipse for a pommel, and a crown of spikes that circles the
	 * blade and touches nothing.
	 */
	static ModelData godsfall() {
		ModelData m = new ModelData();
		int black = violet(6, 1), blackEdge = violet(40, 4), white = hsl(9, 0, 125), heart = hsl(1, 6, 66), heartCore = hsl(6, 4, 118),
				corona = violet(90, 6);
		// the eclipse pommel
		disc(m, 1, 0, 4.4, 1.8, 8, black);
		ring(m, 1, 0, 0, v(1, 0, 0), v(0, 1, 0), 5.8, 0.9, 10, corona, WHITE_HOT);
		collar(m, 3, 6, 2.8, 2.2, GOLD_DARK);
		wound(m, 6, 34, 2.3, 4, black, GOLD);
		// the guard: gold wings swept towards the point, the heart between them
		collar(m, 34, 39, 3.6, 3.4, GOLD_DARK);
		for (int side = -1; side <= 1; side += 2) {
			tube(m, f(36.5, 0, 0), f(40, side * 13, 0), 3.2, 2.2, 4, Math.PI / 4, GOLD_DARK, GOLD);
			tube(m, f(40, side * 13, 0), f(52, side * 21, 0), 2.2, 0, 4, Math.PI / 4, GOLD_BRIGHT, GOLD);
			tube(m, f(38, side * 6, 0), f(32, side * 11, 0), 1.6, 0, 4, Math.PI / 4, GOLD, GOLD);
		}
		tube(m, f(36.5, 0, -4.8), f(36.5, 0, 4.8), 4.4, 4.4, 8, 0, heart, heart);
		tube(m, f(36.5, 0, 4.8), f(36.5, 0, 6.2), 2.4, 0, 6, 0, heartCore, heartCore);
		tube(m, f(36.5, 0, -4.8), f(36.5, 0, -6.2), 2.4, 0, 6, 0, heartCore, heartCore);
		// the blade: gold body, white edge, black core
		bladeStrip(m, new double[][] { { 39, 9.2, 2.2 }, { 56, 10.8, 2.3 }, { 120, 9.8, 2.0 }, { 148, 6.6, 1.6 } }, 176, GOLD, white, 0.62);
		diamond(m, new double[][] { { 41, 3.6, 2.6 }, { 60, 4.2, 2.65 }, { 126, 3.6, 2.35 } }, 156, 0, black, blackEdge);
		// the crown of spikes circling the blade, touching nothing
		for (int k = 0; k < 8; k++) {
			double a = Math.PI * 2 * k / 8 + Math.PI / 8;
			double[] c = { 64, Math.cos(a) * 19, Math.sin(a) * 19 };
			tube(m, f(c[0] - 4, c[1], c[2]), f(c[0] + 9, c[1] * 0.92, c[2] * 0.92), 2.2, 0, 4, a, WHITE_HOT, GOLD_BRIGHT);
			tube(m, f(c[0] - 4, c[1], c[2]), f(c[0] - 8, c[1], c[2]), 2.2, 0, 4, a, GOLD, GOLD);
		}
		return m;
	}

	/* ====================================================================== */
	/*                                THE TIER                                */
	/* ====================================================================== */

	static final Rung[] GOD = {
			new Rung(VoidConstants.WARDENSOATH_2H, "Wardensoath 2h sword", "2h sword", 1500000,
					"The first Wardens swore on this. Three stars still stand watch beside it.", GodWeapons::wardensoath, TWO_HANDED),
			new Rung(VoidConstants.HEART_OF_AUREL, "Heart of Aurel longsword", "longsword", 1600000,
					"Aurel's heart, burning again. It beats when you swing it, and it remembers being a king's.", GodWeapons::heartOfAurel,
					LONGSWORD),
			new Rung(VoidConstants.SCARMAW_HALBERD, "Scarmaw halberd", "halberd", 1700000,
					"The Scar, given teeth. What it bites goes somewhere else, a piece at a time.", GodWeapons::scarmaw, HALBERD),
			new Rung(VoidConstants.HOLLOW_CROWN_WARHAMMER, "Hollow Crown warhammer", "warhammer", 1800000,
					"The Hollow King's crown, beaten round grave-iron. The ghost in it still wants to rule.", GodWeapons::hollowCrown,
					WARHAMMER),
			new Rung(VoidConstants.DAWNBRINGER_LONGBOW, "Dawnbringer longbow", "longbow", 1800000,
					"It shot the first dawn over Nythera. Three stars rise in front of it every time it's drawn.",
					() -> dawnbringer(1.0), () -> dawnbringer(1.3), LONGBOW),
			new Rung(VoidConstants.UNMADE_WAND, "Wand of the Unmade", "wand", 1900000,
					"A piece of nothing on a stick. The shards circling it were part of something, once.", GodWeapons::unmadeWand, WAND),
			new Rung(VoidConstants.FIRST_STAR_SPEAR, "Spear of the First Star", "spear", 2000000,
					"The first star that ever fell, made a spear-head. Point it at the dark and the dark looks away.",
					GodWeapons::firstStarSpear, SPEAR),
			new Rung(VoidConstants.CROWNFIRE_SCIMITAR, "Crownfire scimitar", "scimitar", 2000000,
					"The old kingdom's crown, melted in heart-fire and poured into a blade. It still expects to be obeyed.",
					GodWeapons::crownfire, SCIMITAR),
			new Rung(VoidConstants.GATEBREAKER_BATTLEAXE, "Gatebreaker battleaxe", "battleaxe", 2200000,
					"The founders broke the old king's gate with this, then built a better one.", GodWeapons::gatebreaker, BATTLEAXE),
			new Rung(VoidConstants.NIGHTFALL_SHORTBOW, "Nightfall shortbow", "shortbow", 2300000,
					"The Starless's own bow. Behind the grip, an eclipse waits to be let go.", () -> nightfall(1.0), () -> nightfall(2.4),
					SHORTBOW),
			new Rung(VoidConstants.RIFTCALLER_STAFF, "Riftcaller staff", "staff", 2400000,
					"Plant it and the rift opens. It never needs earth or water runes; the rift has plenty of both.",
					GodWeapons::riftcaller, STAFF),
			new Rung(VoidConstants.SUNFALL_2H, "Sunfall 2h sword", "2h sword", 2600000,
					"The sun, brought down in Aurel's heart-fire and beaten into a blade. It's never once been cold.",
					GodWeapons::sunfall, TWO_HANDED),
			new Rung(VoidConstants.BLACK_SUN_WARHAMMER, "Black Sun warhammer", "warhammer", 2800000,
					"The Starless's heart, struck off it. A ring of its fire circles it for ever, and so do its victims.",
					GodWeapons::blackSun, WARHAMMER),
			new Rung(VoidConstants.CROWN_OF_STARS_STAFF, "Crown of Stars staff", "staff", 3000000,
					"The crown the sky wore, before kings. It needs no elemental runes at all: the elements kneel to it.",
					GodWeapons::crownOfStars, STAFF),
			new Rung(VoidConstants.GODSFALL_2H, "Godsfall 2h sword", "2h sword", 5000000,
					"Starless black, crown gold, first-star white and a king's heart. Nothing made it. Everything did.",
					GodWeapons::godsfall, TWO_HANDED), };
}
