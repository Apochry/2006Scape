package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.LadderWeapons.*;
import static com.rs2.tools.mapgen.ModelData.hsl;
import static com.rs2.tools.mapgen.QuestWeapons.*;

/**
 * The Aurelian kingsguard's arsenal (A40): six weapons of King Aurel's household guard, in the set's polished steel,
 * gold and royal crimson, each carrying the king's crown somewhere on it. Built like {@link QuestWeapons}: each in its
 * own frame (s along, w across, t through), placed into a stock item's pose (see DraftArmourD.ARSENAL).
 *
 * <ol>
 * <li>Kingsguard longsword (LONGSWORD): a broad steel blade, a crowned gold crossguard with a crimson stone, a grip of
 * gold wire, a wheel pommel.</li>
 * <li>Kingsguard halberd (HALBERD): a crimson-lacquered haft banded in gold, a broad bearded axe blade, a back spike,
 * a top spike, a small gold crown round the socket and a crimson tassel under the head.</li>
 * <li>Kingsguard warhammer (WARHAMMER): a square steel head whose striking face is a gold crown round a steel boss, a
 * back spike, crimson stones in the cheeks.</li>
 * <li>Kingsguard rondel dagger (DAGGER): a stiff four-sided blade between a steel disc guard and a gold disc pommel, a
 * crimson grip.</li>
 * <li>Royal hunting shortbow (SHORTBOW): dark red-stained limbs, gilded tips and gold bands, a crimson grip wrap.</li>
 * <li>Court sceptre (WAND, magic): a gold rod with a crimson grip, topped by the king's crown on a crimson cap round a
 * glowing sun-gold gem.</li>
 * </ol>
 */
final class ArsenalKingsguard {

	private ArsenalKingsguard() {
	}

	/** The set's family: the armour's steel, gold and crimson, with the sun's glow for the sceptre's gem. */
	static final int STEEL = hsl(40, 0, 78), STEEL_DARK = hsl(40, 0, 54), STEEL_EDGE = hsl(40, 0, 112), GOLD = hsl(8, 5, 70), GOLD_DARK = hsl(8, 5, 46),
			GOLD_BRIGHT = hsl(8, 4, 94), CRIMSON = hsl(0, 6, 38), CRIMSON_DARK = hsl(0, 6, 24), CRIMSON_GEM = hsl(0, 6, 56), GLOW = hsl(9, 5, 92),
			GLOW_CORE = hsl(9, 3, 116), YEW = hsl(6, 2, 38), YEW_DARK = hsl(6, 2, 27), STRING = hsl(8, 1, 96);

	/** A crown of {@code pts} gold points round the axis at s, radius r, rising {@code tall}: a circlet and the points. */
	static void crown(ModelData m, double s, double r, double band, double tall, int pts) {
		crown(m, s, r, band, tall, pts, 1.5);
	}

	/** As above, the points {@code pr} thick at the root. */
	static void crown(ModelData m, double s, double r, double band, double tall, int pts, double pr) {
		collar(m, s, s + band, r, r, GOLD);
		for (int k = 0; k < pts; k++) {
			double a = Math.PI * 2 * k / pts;
			tube(m, f(s + band - 0.5, Math.cos(a) * r, Math.sin(a) * r), f(s + band + tall, Math.cos(a) * (r + 0.6), Math.sin(a) * (r + 0.6)), pr, 0, 4, a,
					GOLD_BRIGHT, GOLD);
		}
	}

	/* ====================================================================== */
	/*                         1. KINGSGUARD LONGSWORD                        */
	/* ====================================================================== */

	/** The signature sword: a broad blade with a dark fuller, a crowned gold crossguard set with a crimson stone, a gold-wire grip. */
	static ModelData longsword() {
		ModelData m = new ModelData();
		// the wheel pommel: a gold disc, a crimson stone on each face
		tube(m, f(1.2, 0, -1.4), f(1.2, 0, 1.4), 3.4, 3.4, 8, Math.PI / 8, GOLD, GOLD_DARK);
		gem(m, 1.2, 0, 1.5, 2.2, CRIMSON_GEM, CRIMSON);
		tube(m, f(-2.6, 0, 0), f(-1.8, 0, 0), 0, 1.3, 4, Math.PI / 4, GOLD, GOLD);
		// the grip, bound in gold wire
		tube(m, f(4.2, 0, 0), f(17, 0, 0), 1.7, 1.8, 4, Math.PI / 4, GOLD_DARK, GOLD_DARK);
		for (double s : new double[] { 5.5, 8.5, 11.5, 14.5 }) {
			band(m, s, s + 1.1, 2.0, GOLD);
		}
		collar(m, 3.6, 4.6, 2.1, 2.1, GOLD);
		// the crossguard: a gold bar, its ends turned up towards the blade
		guard(m, 18.2, 9.5, 1.9, GOLD, GOLD);
		for (int side = -1; side <= 1; side += 2) {
			tube(m, f(18.2, side * 9.5, 0), f(21.5, side * 12, 0), 1.6, 0.9, 4, Math.PI / 4, GOLD, GOLD_BRIGHT);
		}
		// the crown on the guard, round the blade's root, a crimson stone in its band
		double[][] crown = { { 17.6, -6.4 }, { 17.6, 6.4 }, { 26.5, 7.2 }, { 22.4, 3.4 }, { 28.4, 0 }, { 22.4, -3.4 }, { 26.5, -7.2 } };
		plateSW(m, crown, new double[] { 1.9, 1.9, 1.1, 1.6, 1.1, 1.6, 1.1 }, new double[] { 20.6, 0 }, 2.0, GOLD, GOLD_BRIGHT, GOLD_DARK);
		gem(m, 20.4, 0, 1.6, 2.9, CRIMSON_GEM, CRIMSON);
		// the blade, broad, a dark fuller down each face
		bladeStrip(m, new double[][] { { 21, 4.0, 1.3 }, { 30, 4.3, 1.35 }, { 72, 3.9, 1.25 }, { 93, 3.0, 1.05 }, { 100, 2.2, 0.85 } }, 110, STEEL, STEEL_EDGE,
				0.5);
		for (int side = -1; side <= 1; side += 2) {
			tube(m, f(29, 0, side * 1.2), f(84, 0, side * 1.0), 0.6, 0.45, 3, 0, STEEL_DARK, STEEL_DARK);
		}
		return m;
	}

	/* ====================================================================== */
	/*                          2. KINGSGUARD HALBERD                         */
	/* ====================================================================== */

	/** The gate-polearm: a crimson-lacquered haft, a broad bearded axe blade, a back spike, a gold crown round the socket. */
	static ModelData halberd() {
		ModelData m = new ModelData();
		haft(m, 8, 206, 3.2, 2.9, CRIMSON_DARK);
		tube(m, f(0, 0, 0), f(9, 0, 0), 1.6, 3.9, 4, Math.PI / 4, GOLD, GOLD);
		band(m, 9, 12, 3.7, GOLD);
		band(m, 104, 134, 3.6, CRIMSON);
		band(m, 150, 164, 3.5, CRIMSON);
		for (double s : new double[] { 102, 134, 148, 164 }) {
			band(m, s, s + 2, 3.9, GOLD);
		}
		// the socket and its crown
		collar(m, 168, 176, 3.8, 4.8, GOLD_DARK);
		haft(m, 176, 216, 4.4, 4.0, STEEL_DARK);
		crown(m, 174, 5.0, 3.0, 5.0, 5);
		// the axe blade (+w): broad, its beard swept down towards the hand
		double[][] blade = { { 186, 3 }, { 214, 3 }, { 230, 30 }, { 212, 37 }, { 190, 38 }, { 166, 31 }, { 176, 14 } };
		plateSW(m, blade, new double[] { 2.4, 2.4, 0, 0, 0, 0, 1.2 }, new double[] { 198, 20 }, 2.2, STEEL, STEEL_EDGE, STEEL_DARK);
		// a gold rivet-boss on the blade
		gem(m, 198, 9, 2.0, 3.4, GOLD, GOLD_DARK);
		// the back spike (-w)
		tube(m, f(200, -3, 0), f(196, -22, 0), 3.6, 0, 4, Math.PI / 4, STEEL, STEEL_DARK);
		band(m, 214, 217, 4.6, GOLD);
		// the top spike
		diamond(m, new double[][] { { 216, 3.8, 3.0 }, { 224, 4.8, 2.6 } }, 248, 0, STEEL_EDGE, STEEL);
		// a crimson tassel hanging off the back of the socket, a gold knot
		tube(m, f(170, -4.2, 0), f(168, -5.5, 0), 1.8, 2.2, 6, 0, GOLD, GOLD);
		tube(m, f(168, -5.5, 0), f(150, -7, 0), 2.4, 3.6, 6, 0, CRIMSON, CRIMSON_DARK);
		return m;
	}

	/* ====================================================================== */
	/*                         3. KINGSGUARD WARHAMMER                        */
	/* ====================================================================== */

	/** A square steel head, its striking face a gold crown round a steel boss, a back spike, crimson stones in the cheeks. */
	static ModelData warhammer() {
		ModelData m = new ModelData();
		haft(m, 2, 60, 2.5, 2.2, STEEL_DARK);
		knob(m, 2, 3.2, 2.6, GOLD, GOLD_DARK);
		band(m, 4.5, 18, 2.8, CRIMSON);
		for (double s : new double[] { 4.5, 11, 17.5 }) {
			band(m, s, s + 1.2, 3.0, GOLD);
		}
		collar(m, 38, 41, 2.6, 3.4, GOLD);
		double hs = 52;
		// the head: a square block across the haft, gold-banded at each end
		tube(m, f(hs, -9, 0), f(hs, 9, 0), 7.6, 7.6, 4, Math.PI / 4, STEEL, STEEL);
		tube(m, f(hs, 8, 0), f(hs, 10, 0), 8.3, 8.3, 4, Math.PI / 4, GOLD, GOLD);
		tube(m, f(hs, -8, 0), f(hs, -10, 0), 8.3, 8.3, 4, Math.PI / 4, GOLD, GOLD);
		// the face (+w): a crown, its circlet round a steel boss and its points standing out past it
		tube(m, f(hs, 10, 0), f(hs, 13, 0), 6.6, 6.6, 8, Math.PI / 8, GOLD, GOLD_DARK);
		tube(m, f(hs, 10, 0), f(hs, 14.5, 0), 4.8, 4.2, 6, 0, STEEL_EDGE, STEEL);
		for (int k = 0; k < 5; k++) {
			double a = Math.PI * 2 * k / 5 + Math.PI / 2;
			tube(m, f(hs + Math.cos(a) * 5.6, 12.5, Math.sin(a) * 5.6), f(hs + Math.cos(a) * 6.4, 18.5, Math.sin(a) * 6.4), 1.7, 0, 4, a, GOLD_BRIGHT, GOLD);
		}
		// the back spike (-w)
		tube(m, f(hs, -10, 0), f(hs, -23, 0), 6.0, 0, 4, Math.PI / 4, STEEL, STEEL_DARK);
		// crimson stones in the cheeks
		gem(m, hs, 0, 2.4, 9.0, CRIMSON_GEM, CRIMSON);
		// a short top spike
		tube(m, f(hs + 6, 0, 0), f(hs + 15, 0, 0), 3.0, 0, 4, Math.PI / 4, STEEL_EDGE, STEEL);
		return m;
	}

	/* ====================================================================== */
	/*                       4. KINGSGUARD RONDEL DAGGER                      */
	/* ====================================================================== */

	/** A stiff four-sided blade between a steel disc guard and a gold disc pommel; a crimson grip, gold-banded. */
	static ModelData rondelDagger() {
		ModelData m = new ModelData();
		// the pommel disc, gold, a crimson boss on its end
		tube(m, f(-0.8, 0, 0), f(1.0, 0, 0), 3.8, 3.8, 8, Math.PI / 8, GOLD, GOLD_DARK);
		tube(m, f(-0.8, 0, 0), f(-2.2, 0, 0), 1.8, 0, 6, 0, CRIMSON_GEM, CRIMSON_GEM);
		// the grip
		tube(m, f(1, 0, 0), f(11, 0, 0), 1.5, 1.6, 6, 0, CRIMSON, CRIMSON);
		for (double s : new double[] { 3.5, 7.5 }) {
			band(m, s, s + 1.0, 1.85, GOLD);
		}
		// the guard disc: steel, gold-rimmed
		tube(m, f(11, 0, 0), f(12.6, 0, 0), 4.4, 4.4, 8, Math.PI / 8, STEEL, STEEL_DARK);
		tube(m, f(11.4, 0, 0), f(12.2, 0, 0), 5.0, 5.0, 8, Math.PI / 8, GOLD, GOLD);
		// the blade: a stiff diamond section to a needle point
		diamond(m, new double[][] { { 12.6, 2.4, 1.6 }, { 22, 2.1, 1.4 }, { 31, 1.3, 1.0 } }, 40, 0, STEEL_EDGE, STEEL);
		return m;
	}

	/* ====================================================================== */
	/*                        5. ROYAL HUNTING SHORTBOW                       */
	/* ====================================================================== */

	/** The king's hunting bow: red-stained yew, gilded tips and gold bands, a crimson grip wrap. */
	static ModelData shortbow(double bend) {
		ModelData m = new ModelData();
		double L = 127, k = L / 127, rk = Math.sqrt(k);
		bow(m, L, bend, new double[] { 1.7, 2.3, 3.0, 3.5, 3.8, 3.5, 3.0, 2.3, 1.7 }, YEW, YEW_DARK, CRIMSON, GOLD, STRING);
		// gold bands where the limbs meet the grip, and gilt collars up the limbs
		for (double s : new double[] { 49, 78, 24, 103 }) {
			double t = (s < 63.5 ? s : L - s) / k;
			double w = (t < 24 ? 4 + (t - 8) * 0.19 : t < 44 ? 7 + (t - 24) * 0.125 : 9.5 - (t - 44) * 0.175) * bend * rk;
			boolean grip = s > 40 && s < 90;
			tube(m, f(s - (grip ? 1.5 : 1), w, 0), f(s + (grip ? 1.5 : 1), w, 0), grip ? 4.6 : 3.4, grip ? 4.6 : 3.4, 4, Math.PI / 4, GOLD, GOLD);
		}
		// a small gold crown boss on the back of the grip
		double gw = 6 * bend * rk;
		gem(m, L / 2, gw + 4.0, 2.0, 3.0, GOLD_BRIGHT, GOLD);
		return m;
	}

	/* ====================================================================== */
	/*                             6. COURT SCEPTRE                           */
	/* ====================================================================== */

	/** The court mage's sceptre: a gold rod, a crimson grip, the king's crown on a crimson cap round a glowing gem. */
	static ModelData sceptre() {
		ModelData m = new ModelData();
		knob(m, 1.5, 2.6, 2.2, GOLD, GOLD_DARK);
		tube(m, f(2, 0, 0), f(80, 0, 0), 1.5, 1.8, 6, 0, GOLD, GOLD);
		band(m, 12, 32, 2.1, CRIMSON);
		collar(m, 11, 12.5, 2.5, 2.5, GOLD_DARK);
		collar(m, 31.5, 33, 2.5, 2.5, GOLD_DARK);
		// a knop halfway up
		collar(m, 52, 55, 1.8, 3.2, GOLD);
		collar(m, 55, 58, 3.2, 1.8, GOLD_DARK);
		// the crown: a cup flaring to the circlet, its points round the gem, a crimson cap inside
		collar(m, 76, 82, 1.9, 6.2, GOLD_DARK);
		crown(m, 82, 6.4, 4.0, 10, 5, 2.2);
		collar(m, 84, 88, 5.8, 5.2, CRIMSON);
		collar(m, 88, 91, 5.2, 3.4, CRIMSON_DARK);
		// the gem, glowing the sun's gold, standing up out of the crown
		tube(m, f(88, 0, 0), f(93, 0, 0), 0, 3.2, 8, Math.PI / 8, GLOW, GLOW);
		tube(m, f(93, 0, 0), f(99, 0, 0), 3.2, 0, 8, Math.PI / 8, GLOW_CORE, GLOW_CORE);
		return m;
	}
}
