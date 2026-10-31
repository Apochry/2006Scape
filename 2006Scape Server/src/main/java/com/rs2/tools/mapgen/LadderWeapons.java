package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ModelData.hsl;
import static com.rs2.tools.mapgen.QuestWeapons.*;

import java.util.function.Supplier;

import com.rs2.game.content.voidrealm.VoidConstants;

/**
 * The weapon ladder: twenty-five hand-built weapons from a fishmonger's knife to the Starless's own blade, each a step
 * cooler than the last. Built exactly like {@link QuestWeapons}: one frame per weapon (s along, w across, t through),
 * placed into the inventory and wielded pose of a comparable cache item so that item's camera (opcodes 4-8) and the
 * existing animations fit it unchanged.
 *
 * <h2>The extra poses</h2>
 * QuestWeapons has the halberd, battleaxe, mace, shortbow and staff. The ladder adds nine more, read off the cache the
 * same way (butt and tip of the comparable item's wielded and inventory models, and the skin label of its wielded
 * vertices):
 * <pre>
 *  rune longsword 1303  wield 518 (label 27): pommel (-30,-98,9), tip (-30,-106,-97);  inv 2602: pommel (-31,-2.5,22), tip (31.5,-2.5,-61.5)
 *  rune sword     1289  wield 519 (label 50): pommel (-30,-98,9), tip (-30,-105,-69);  inv 2604: pommel (3,-3,33), tip (-4,-3,-45)
 *  rune dagger    1213  wield 533 (label 50): pommel (-29,-97,7), tip (-29,-104,-30);  inv 2672: pommel (-3,-8,22), tip (9,-8,-31), floating at y -8
 *  rune scimitar  1333  wield 490 (label 50): pommel (-29,-95,18), tip (-29,-126,-78), belly to +y;  inv 2373: pommel (11,-4,43), tip (-19,-4,-53), belly to +x
 *  rune warhammer 1347  wield 542 (label 27): pommel (-29,-96,10), head (-29,-110,-51);  inv 2731: pommel (26,-6,-24), head (-24,-11,14), head tilted up
 *  rune spear     1247  wield 540 (label 27): butt (-30,-47,111), tip (-30,-160,-126);  inv 2719: butt (-58,-4,53), tip (46,-5,-50)
 *  rune 2h sword  1319  wield 546 (label 50): pommel (-30,-93,26), tip (-30,-121,-114); inv 2754: pommel (-44,-3,50), tip (55,-3,-54)
 *  magic longbow  859   wield 563 (label 27): tips (-29,-77,93) and (-29,-131,-84), belly to +y;  inv 2832: tips (-47,-6,63) and (47,-4,-61)
 *  master wand    6914  wield 10702 (label 50): butt (-31,-90,21), tip (-31,-119,-74);  inv 10565: (1,-8,-51) to (1,-8,64); its
 *                 icon camera frames the z = 64 end, so our wand lies along it with its head that way, kept a little short of the end (z -46 to 46) so the head isn't
 *                 thrust into the camera
 * </pre>
 * Grip positions (frame s of the hand at (-30,-97,0)): longsword and sword 9, dagger 7, scimitar 18, warhammer 10,
 * spear 122, 2h sword 26, longbow 95, wand 22.
 */
public final class LadderWeapons {

	private LadderWeapons() {
	}

	/* ====================================================================== */
	/*                                 POSES                                  */
	/* ====================================================================== */

	static final Pose LONGSWORD = new Pose("Rune longsword", 1303, 518, 2602, 27,
			new Place(v(-30, -98, 9), v(-30, -106, -97), v(0, 1, 0), false),
			new Place(v(-31, -2.5, 22), v(31.5, -2.5, -61.5), v(0.81, 0, 0.59), true));
	static final Pose SWORD = new Pose("Rune sword", 1289, 519, 2604, 50,
			new Place(v(-30, -98, 9), v(-30, -105, -69), v(0, 1, 0), false),
			new Place(v(3, -3, 33), v(-4, -3, -45), v(1, 0, 0), true));
	static final Pose DAGGER = new Pose("Rune dagger", 1213, 533, 2672, 50,
			new Place(v(-29, -97, 7), v(-29, -104, -30), v(0, 1, 0), false),
			new Place(v(-3, -8, 22), v(9, -8, -31), v(1, 0, 0), false));
	static final Pose SCIMITAR = new Pose("Rune scimitar", 1333, 490, 2373, 50,
			new Place(v(-29, -95, 18), v(-29, -126, -78), v(0, 1, 0), false),
			new Place(v(11, -4, 43), v(-19, -4, -53), v(1, 0, 0), true));
	static final Pose WARHAMMER = new Pose("Rune warhammer", 1347, 542, 2731, 27,
			new Place(v(-29, -96, 10), v(-29, -110, -51), v(0, 1, 0), false),
			new Place(v(26, -6, -24), v(-24, -11, 14), v(0.5, 0.6, 0.7), true));
	static final Pose SPEAR = new Pose("Rune spear", 1247, 540, 2719, 27,
			new Place(v(-30, -47, 111), v(-30, -160, -126), v(0, 1, 0), false),
			new Place(v(-58, -4, 53), v(46, -5, -50), v(1, 0, 1), true));
	static final Pose TWO_HANDED = new Pose("Rune 2h sword", 1319, 546, 2754, 50,
			new Place(v(-30, -93, 26), v(-30, -121, -114), v(0, 1, 0), false),
			new Place(v(-44, -3, 50), v(55, -3, -54), v(1, 0, 1), true));
	static final Pose LONGBOW = new Pose("Magic longbow", 859, 563, 2832, 27,
			new Place(v(-29, -77, 93), v(-29, -131, -84), v(0, 1, 0), false),
			new Place(v(-47, -6, 63), v(47, -4, -61), v(-0.8, 0, -0.6), true));
	static final Pose WAND = new Pose("Master wand", 6914, 10702, 10565, 50,
			new Place(v(-31, -90, 21), v(-31, -119, -74), v(0, 1, 0), false),
			new Place(v(1, -8, -46), v(1, -8, 46), v(1, 0, 0), true));

	/* ====================================================================== */
	/*                               PALETTE                                  */
	/* ====================================================================== */

	static final int WOOD = hsl(6, 4, 34), WOOD_DARK = hsl(6, 4, 24), WOOD_PALE = hsl(7, 3, 52), LEATHER = hsl(5, 3, 20),
			LEATHER_DARK = hsl(5, 3, 13), TWINE = hsl(8, 2, 62), IRON = hsl(0, 0, 46), IRON_DARK = hsl(0, 0, 32),
			IRON_EDGE = hsl(0, 0, 84), STEEL = hsl(40, 1, 72), STEEL_DARK = hsl(40, 1, 54), STEEL_EDGE = hsl(40, 1, 106),
			BRASS = hsl(8, 4, 56), BRASS_DARK = hsl(8, 4, 38), BRONZE = hsl(7, 4, 44), BRONZE_DARK = hsl(7, 4, 30),
			BONE = hsl(7, 1, 98), BONE_DARK = hsl(7, 1, 76), NIGHT = hsl(42, 4, 30), NIGHT_DARK = hsl(42, 4, 20),
			STAR_WHITE = hsl(42, 0, 122), STARMETAL = hsl(42, 1, 74), STARMETAL_DARK = hsl(42, 1, 56),
			STARMETAL_EDGE = hsl(42, 1, 108), BLACKSTEEL = hsl(42, 1, 22), BLACKSTEEL_LIGHT = hsl(42, 1, 34);

	static int violet(int light, int intensity) {
		return ConfigDefinitions.voidRamp(light, intensity);
	}

	/* ====================================================================== */
	/*                           SHARED BUILDING BITS                         */
	/* ====================================================================== */

	/** A 4-sided pole along s (the usual haft), radius r0 to r1. */
	static void haft(ModelData m, double s0, double s1, double r0, double r1, int col) {
		tube(m, f(s0, 0, 0), f(s1, 0, 0), r0, r1, 4, Math.PI / 4, col, col);
	}

	/** A short collar or wrap round the haft. */
	static void band(ModelData m, double s0, double s1, double r, int col) {
		tube(m, f(s0, 0, 0), f(s1, 0, 0), r, r, 4, Math.PI / 4, col, col);
	}

	/** A round (6-sided) collar. */
	static void collar(ModelData m, double s0, double s1, double r0, double r1, int col) {
		tube(m, f(s0, 0, 0), f(s1, 0, 0), r0, r1, 6, 0, col, col);
	}

	/** A cross-guard bar across w, from -half to +half, at s. */
	static void guard(ModelData m, double s, double half, double r, int col, int tipCol) {
		tube(m, f(s, 0, 0), f(s, half, 0), r, r * 0.8, 4, Math.PI / 4, col, tipCol);
		tube(m, f(s, 0, 0), f(s, -half, 0), r, r * 0.8, 4, Math.PI / 4, col, tipCol);
	}

	/** A gem: a small double pyramid sticking out of both faces of a blade (along t) at (s, w). */
	static void gem(ModelData m, double s, double w, double r, double h, int col, int col2) {
		tube(m, f(s, w, 0), f(s, w, h), r, 0, 4, 0, col, col);
		tube(m, f(s, w, 0), f(s, w, -h), r, 0, 4, 0, col2, col2);
	}

	/** A pommel knob: a round double cone centred at s. */
	static void knob(ModelData m, double s, double r, double len, int col, int col2) {
		tube(m, f(s, 0, 0), f(s - len, 0, 0), r, 0, 6, 0, col, col);
		tube(m, f(s, 0, 0), f(s + len, 0, 0), r, 0, 6, 0, col2, col2);
	}

	/**
	 * A bow's limb, string and grip, generalised from the Duskyew: limb points are given for a bow 127 long and scaled
	 * to {@code L}; {@code bend} deepens the curve (inventory models are drawn deeper, as the cache's are).
	 */
	static void bow(ModelData m, double L, double bend, double[] thick, int wood, int woodDark, int grip, int tipCol, int string) {
		double k = L / 127;
		double[][] limb = { { 3, 4 }, { 8, -0.5 }, { 24, 7 }, { 44, 9.5 }, { 64, 6 }, { 84, 9.5 }, { 104, 7 }, { 119, -0.5 }, { 124, 4 } };
		for (double[] p : limb) {
			p[0] *= k;
			p[1] = (p[1] < 0 ? p[1] : p[1] * bend) * Math.sqrt(k);
		}
		for (int i = 0; i + 1 < limb.length; i++) {
			tube(m, f(limb[i][0], limb[i][1], 0), f(limb[i + 1][0], limb[i + 1][1], 0), thick[i], thick[i + 1], 4, Math.PI / 4,
					i % 2 == 0 ? wood : woodDark, wood);
		}
		double gw = limb[4][1];
		tube(m, f(L / 2 - 8 * k, gw + 1.2, 0), f(L / 2 + 8 * k, gw + 1.2, 0), thick[4] + 0.7, thick[4] + 0.7, 4, Math.PI / 4, grip, grip);
		double tw = limb[0][1];
		tube(m, f(4.5 * k, tw - 1.5, 0), f(-1 * k, tw + 5, 0), thick[0] + 0.9, 0, 4, Math.PI / 4, tipCol, tipCol);
		tube(m, f(L - 4.5 * k, tw - 1.5, 0), f(L + 1 * k, tw + 5, 0), thick[0] + 0.9, 0, 4, Math.PI / 4, tipCol, tipCol);
		tube(m, f(4 * k, tw - 2, 0), f(L - 4 * k, tw - 2, 0), 0.8, 0.8, 3, 0, string, string);
	}

	/**
	 * A long straight blade with a flat and a honed bevel down both edges: stations {s, halfWidth, halfThickness}; the
	 * flat runs at 45% of the width, the edges are sharp, and it closes to a point at {@code tipS}. Unlike
	 * {@link QuestWeapons#plate}, the bevel keeps the same width all the way down, so a long blade's edge can glow.
	 */
	static void bladeStrip(ModelData m, double[][] st, double tipS, int flat, int bevel) {
		bladeStrip(m, st, tipS, flat, bevel, 0.45);
	}

	/** As above, with the flat at {@code inner} of the width. */
	static void bladeStrip(ModelData m, double[][] st, double tipS, int flat, int bevel, double inner) {
		int n = st.length;
		int[][] v = new int[n][6]; // outer +w, inner +w front, inner -w front, outer -w, inner -w back, inner +w back
		for (int i = 0; i < n; i++) {
			double s = st[i][0], hw = st[i][1], ht = st[i][2], iw = hw * inner;
			double[][] p = { f(s, hw, 0), f(s, iw, ht), f(s, -iw, ht), f(s, -hw, 0), f(s, -iw, -ht), f(s, iw, -ht) };
			for (int k = 0; k < 6; k++) {
				v[i][k] = m.vertex(p[k][0], p[k][1], p[k][2]);
			}
		}
		double[] tp = f(tipS, 0, 0);
		int tip = m.vertex(tp[0], tp[1], tp[2]);
		for (int i = 0; i + 1 < n; i++) {
			double[] ref = f((st[i][0] + st[i + 1][0]) / 2, 0, 0);
			for (int k = 0; k < 6; k++) {
				int col = k == 1 || k == 4 ? flat : bevel;
				m.quadOut(v[i][k], v[i][(k + 1) % 6], v[i + 1][(k + 1) % 6], v[i + 1][k], col, ref);
			}
		}
		double[] ref = f((st[n - 1][0] + tipS) / 2, 0, 0);
		for (int k = 0; k < 6; k++) {
			m.triOut(v[n - 1][k], v[n - 1][(k + 1) % 6], tip, k == 1 || k == 4 ? flat : bevel, ref);
		}
		double[] base = f(st[0][0] + 3, 0, 0);
		for (int k = 1; k + 1 < 6; k++) {
			m.triOut(v[0][0], v[0][k], v[0][k + 1], flat, base);
		}
	}

	/* ====================================================================== */
	/*                      1. NETWRIGHT'S GUTTING DAGGER                     */
	/* ====================================================================== */

	/** Bram's fish knife: a thin single-edged iron blade with a belly, on a twine-wrapped wooden handle. No guard. */
	static ModelData guttingDagger() {
		ModelData m = new ModelData();
		// handle: wood, swelling a little towards the butt, two turns of twine
		tube(m, f(0, 0, 0), f(13, 0, 0), 2.2, 1.7, 4, Math.PI / 4, WOOD, WOOD_DARK);
		band(m, 4, 5.5, 2.2, TWINE);
		band(m, 9, 10.5, 2.0, TWINE);
		collar(m, 12.5, 14, 2.0, 1.8, IRON_DARK);
		// the blade: spine along -w, the curved edge on +w
		double[][] blade = { { 13.5, -1.4 }, { 13.5, 1.8 }, { 21, 2.8 }, { 30, 2.4 }, { 37.5, 0 }, { 30, -1.0 }, { 21, -1.4 } };
		double[] h = { 0.8, 0.6, 0, 0, 0, 0.5, 0.8 };
		plateSW(m, blade, h, new double[] { 22, 0.4 }, 0.6, IRON, IRON_EDGE, IRON_DARK);
		return m;
	}

	/* ====================================================================== */
	/*                          2. HOB'S FELLING AXE                          */
	/* ====================================================================== */

	/** A woodsman's axe: a long ash haft, a narrow iron wedge of a head with a bright edge, a poll on the back. */
	static ModelData fellingAxe() {
		ModelData m = new ModelData();
		haft(m, -2, 64, 2.3, 2.0, WOOD);
		band(m, 2, 12, 2.7, LEATHER);
		// the head's eye, and the poll behind it
		tube(m, f(50, -5, 0), f(50, 4, 0), 3.6, 3.6, 4, Math.PI / 4, IRON_DARK, IRON_DARK);
		// the wedge, flaring to the edge (+w)
		double[][] bit = { { 46, 3 }, { 55, 3 }, { 61, 11 }, { 60, 17 }, { 50, 18 }, { 40, 17 }, { 40, 12 } };
		double[] h = { 2.2, 2.2, 1.2, 0, 0, 0, 1.2 };
		plateSW(m, bit, h, new double[] { 50, 10 }, 1.6, IRON, IRON_EDGE, IRON_DARK);
		// the poll: a square hammer-face on the back (-w)
		tube(m, f(50, -4, 0), f(50, -8, 0), 3.4, 3.0, 4, Math.PI / 4, IRON, IRON_DARK);
		return m;
	}

	/* ====================================================================== */
	/*                            3. PILGRIM'S STAFF                          */
	/* ====================================================================== */

	/** A walking staff off the road: a crooked wooden pole with a hooked top, a brass ferrule and a faded ribbon. */
	static ModelData pilgrimStaff() {
		ModelData m = new ModelData();
		int ribbon = hsl(40, 3, 56), ribbonDark = hsl(40, 3, 42);
		tube(m, f(4, 0, 0), f(70, 1.2, 0), 3.4, 3.7, 5, 0, WOOD, WOOD);
		tube(m, f(70, 1.2, 0), f(140, -1.0, 0), 3.7, 3.5, 5, 0.4, WOOD_DARK, WOOD_DARK);
		tube(m, f(140, -1.0, 0), f(205, 0.6, 0), 3.5, 3.8, 5, 0.1, WOOD, WOOD);
		// the crook: the pole bends over at the top
		double[][] crook = { { 205, 0.6 }, { 218, 1.5 }, { 228, 6 }, { 232, 13 }, { 228, 19 }, { 220, 20 } };
		for (int i = 0; i + 1 < crook.length; i++) {
			tube(m, f(crook[i][0], crook[i][1], 0), f(crook[i + 1][0], crook[i + 1][1], 0), 3.8 - i * 0.35, 3.8 - (i + 1) * 0.35, 5, 0,
					i % 2 == 0 ? WOOD_DARK : WOOD, WOOD);
		}
		// brass ferrule at the butt
		collar(m, 0, 8, 2.6, 4.2, BRASS_DARK);
		// a knot of ribbon below the crook, two tails hanging
		collar(m, 196, 201, 4.4, 4.4, ribbon);
		tube(m, f(199, -3.5, 0), f(183, -6.5, 1.5), 1.6, 0.6, 3, 0, ribbon, ribbonDark);
		tube(m, f(199, -3.5, 0), f(186, -5, -2.5), 1.6, 0.6, 3, 0.5, ribbonDark, ribbon);
		// worn grip
		band(m, 110, 128, 4.2, LEATHER);
		return m;
	}

	/* ====================================================================== */
	/*                             4. MILITIA SPEAR                           */
	/* ====================================================================== */

	/** Gatehold's militia carry these: an ash haft, an iron leaf head on a socket with lugs, a night-blue tassel. */
	static ModelData militiaSpear() {
		ModelData m = new ModelData();
		haft(m, 6, 226, 2.9, 2.6, WOOD_PALE);
		tube(m, f(0, 0, 0), f(6, 0, 0), 1.4, 3.1, 4, Math.PI / 4, IRON_DARK, IRON_DARK); // butt spike
		band(m, 108, 136, 3.4, LEATHER);
		// the socket and its lugs
		collar(m, 220, 234, 3.4, 2.8, IRON_DARK);
		tube(m, f(226, -6, 0), f(226, 6, 0), 1.3, 1.3, 4, Math.PI / 4, IRON_DARK, IRON_DARK);
		// the leaf head
		diamond(m, new double[][] { { 233, 2.4, 1.6 }, { 242, 5.2, 1.6 }, { 252, 4.2, 1.2 } }, 266, 0, IRON_EDGE, IRON);
		// a tassel of Gatehold blue under the socket
		for (int k = 0; k < 3; k++) {
			double a = Math.PI * 2 * k / 3;
			tube(m, f(219, 0, 0), f(204, Math.cos(a) * 4.5, Math.sin(a) * 4.5), 2.4, 0.5, 3, a, k == 1 ? NIGHT_DARK : NIGHT, NIGHT);
		}
		return m;
	}

	/* ====================================================================== */
	/*                           5. WOLFBONE SHORTBOW                         */
	/* ====================================================================== */

	static ModelData wolfboneBow(double bend) {
		ModelData m = new ModelData();
		int fur = hsl(6, 1, 46);
		bow(m, 127, bend, new double[] { 1.5, 2.1, 2.9, 3.4, 3.7, 3.4, 2.9, 2.1, 1.5 }, WOOD, WOOD_DARK, fur, BONE, TWINE);
		// wolf-bone nocks: the tips are lashed bone, a pale band where the wood ends
		double k = 1;
		for (double s : new double[] { 13, 114 }) {
			double w = (s < 64 ? 3.5 : 3.5) * bend;
			tube(m, f(s - 2.5, w, 0), f(s + 2.5, w, 0), 2.9, 2.9, 4, Math.PI / 4, BONE_DARK, BONE_DARK);
		}
		return m;
	}

	/* ====================================================================== */
	/*                           6. WATCHMAN'S SWORD                          */
	/* ====================================================================== */

	/** The watch's sidearm: a straight steel blade, a plain bar guard, leather grip and a Gatehold-blue pommel. */
	static ModelData watchmanSword() {
		ModelData m = new ModelData();
		knob(m, 1.5, 2.6, 2.2, NIGHT, NIGHT_DARK);
		tube(m, f(3.5, 0, 0), f(15, 0, 0), 1.5, 1.6, 4, Math.PI / 4, LEATHER, LEATHER);
		band(m, 7, 8, 1.9, LEATHER_DARK);
		band(m, 11, 12, 1.9, LEATHER_DARK);
		guard(m, 16, 7.5, 1.5, STEEL_DARK, STEEL);
		collar(m, 15, 17.5, 2.0, 2.0, STEEL_DARK);
		diamond(m, new double[][] { { 17.5, 2.9, 1.0 }, { 40, 3.2, 1.0 }, { 64, 2.6, 0.9 } }, 79, 0, STEEL_EDGE, STEEL);
		return m;
	}

	/* ====================================================================== */
	/*                             7. STEELCAP MACE                           */
	/* ====================================================================== */

	/** A sergeant's mace: an oak handle with brass bands, and a steel head of six short flanges under a domed cap. */
	static ModelData steelcapMace() {
		ModelData m = new ModelData();
		haft(m, 3, 46, 2.3, 2.1, WOOD);
		knob(m, 2, 3.0, 2.5, BRASS, BRASS_DARK);
		band(m, 14, 30, 2.7, LEATHER);
		band(m, 30, 32, 2.9, BRASS);
		collar(m, 40, 46, 2.6, 5.0, STEEL_DARK);
		collar(m, 46, 60, 5.0, 4.6, STEEL_DARK);
		tube(m, f(60, 0, 0), f(68, 0, 0), 4.6, 0, 6, 0, STEEL, STEEL);
		for (int i = 0; i < 6; i++) {
			double a = Math.PI * 2 * i / 6;
			double[] V = { Math.sin(a), Math.cos(a), 0 }, N = { Math.cos(a), -Math.sin(a), 0 };
			double[][] pts = { { 44, 3.5 }, { 48, 9.5 }, { 60, 9.5 }, { 65, 3.5 } };
			plate(m, v(0, 0, 0), v(0, 0, 1), V, N, pts, new double[] { 1.3, 0, 0, 1.1 }, new double[] { 54, 6.5 }, 1.4, STEEL, STEEL_EDGE,
					STEEL_DARK);
		}
		return m;
	}

	/* ====================================================================== */
	/*                          8. SMUGGLER'S SCIMITAR                        */
	/* ====================================================================== */

	/** Sly Nettle's stock: a curved steel blade, a brass knuckle-bow over a red rag grip. */
	static ModelData smugglerScimitar() {
		ModelData m = new ModelData();
		int rag = hsl(0, 5, 40), ragDark = hsl(0, 5, 28);
		knob(m, 2, 2.6, 2.2, BRASS, BRASS_DARK);
		tube(m, f(4, 0, 0), f(28, 0, 0), 1.7, 1.9, 4, Math.PI / 4, rag, ragDark);
		for (double s : new double[] { 9, 16, 23 }) {
			band(m, s, s + 1.2, 2.2, ragDark);
		}
		// guard and knuckle-bow (on the edge side, over the fingers)
		guard(m, 29, 6, 1.6, BRASS, BRASS_DARK);
		double[][] bowPts = { { 29, 6 }, { 23, 9.5 }, { 14, 10 }, { 6, 7.5 }, { 2.5, 2.5 } };
		for (int i = 0; i + 1 < bowPts.length; i++) {
			tube(m, f(bowPts[i][0], bowPts[i][1], 0), f(bowPts[i + 1][0], bowPts[i + 1][1], 0), 1.1, 1.1, 4, Math.PI / 4, BRASS, BRASS);
		}
		// the blade in two plates (each star-shaped about its own centre): broad belly on +w, tip sweeping back to -w
		plateSW(m, new double[][] { { 30, -1.6 }, { 50, -2.6 }, { 71, -4.6 }, { 71, 6.4 }, { 50, 7.4 }, { 31, 4.2 } },
				new double[] { 1.1, 1.1, 1.0, 0, 0, 0 }, new double[] { 50, 2 }, 1.0, STEEL, STEEL_EDGE, STEEL_DARK);
		plateSW(m, new double[][] { { 69, -4.4 }, { 86, -8.8 }, { 101, -15 }, { 95, -5.5 }, { 84, 1.8 }, { 69, 6.5 } },
				new double[] { 1.0, 0.9, 0, 0, 0, 0 }, new double[] { 82, -2 }, 0.9, STEEL, STEEL_EDGE, STEEL_DARK);
		return m;
	}

	/* ====================================================================== */
	/*                        9. BARROWSTONE WARHAMMER                        */
	/* ====================================================================== */

	/** Off the barrow's dead: a block of grave-stone on a bog-oak handle, bronze-shod, a ghost-green rune on each face. */
	static ModelData barrowstoneHammer() {
		ModelData m = new ModelData();
		int stone = hsl(10, 0, 56), stoneDark = hsl(10, 0, 40), rune = hsl(23, 5, 92);
		haft(m, 2, 60, 2.4, 2.1, WOOD_DARK);
		knob(m, 2, 3.0, 2.4, BRONZE, BRONZE_DARK);
		band(m, 4, 16, 2.8, LEATHER);
		band(m, 40, 43, 2.9, BRONZE);
		// the head: a square block across w, bronze-capped faces
		tube(m, f(52, -11, 0), f(52, 11, 0), 7.0, 7.0, 4, Math.PI / 4, stone, stoneDark);
		tube(m, f(52, 11, 0), f(52, 14, 0), 6.0, 5.2, 4, Math.PI / 4, BRONZE, BRONZE_DARK);
		tube(m, f(52, -11, 0), f(52, -15, 0), 6.0, 2.0, 4, Math.PI / 4, BRONZE, BRONZE_DARK);
		// a stubby stone point on top
		tube(m, f(56, 0, 0), f(65, 0, 0), 4.0, 0, 4, Math.PI / 4, stoneDark, stoneDark);
		// the rune, glowing faintly on both faces
		gem(m, 52, 0, 2.6, 6.6, rune, rune);
		return m;
	}

	/* ====================================================================== */
	/*                            10. SILKWOOD WAND                           */
	/* ====================================================================== */

	/** Cut from a Silkwood magic tree: a pale wand bound in spider silk, a little milk-white crystal caught in the tip. */
	static ModelData silkwoodWand() {
		ModelData m = new ModelData();
		int wood = hsl(24, 1, 70), woodDark = hsl(24, 1, 52), silk = hsl(0, 0, 112), crystal = hsl(32, 2, 104), crystalCore = hsl(32, 1, 122);
		tube(m, f(3, 0, 0), f(84, 0, 0), 2.1, 1.4, 5, 0, wood, wood);
		tube(m, f(0, 0, 0), f(5, 0, 0), 1.2, 2.9, 5, 0.3, woodDark, woodDark);
		band(m, 14, 30, 2.6, LEATHER);
		for (double s : new double[] { 60, 66, 72, 78 }) {
			collar(m, s, s + 1.0, 1.9, 1.9, silk);
		}
		// three silk strands cradling the crystal
		for (int k = 0; k < 3; k++) {
			double a = Math.PI * 2 * k / 3;
			tube(m, f(83, Math.cos(a) * 1.4, Math.sin(a) * 1.4), f(90, Math.cos(a + 0.6) * 3.6, Math.sin(a + 0.6) * 3.6), 0.6, 0.5, 3, 0, silk, silk);
		}
		tube(m, f(84, 0, 0), f(91, 0, 0), 0.8, 3.0, 6, 0, crystal, crystal);
		tube(m, f(91, 0, 0), f(100, 0, 0), 3.0, 0, 6, 0, crystalCore, crystal);
		return m;
	}

	/* ====================================================================== */
	/*                         11. GATEHOLD LONGSWORD                         */
	/* ====================================================================== */

	/** The watch's longsword: a fullered steel blade, a down-swept guard, a night-blue grip and the Wardens' star. */
	static ModelData gateholdLongsword() {
		ModelData m = new ModelData();
		star(m, f(1, 0, 0), 2.6, 1.3, STAR_WHITE, STEEL);
		collar(m, 3, 5, 1.6, 2.2, STEEL_DARK);
		tube(m, f(5, 0, 0), f(17, 0, 0), 1.6, 1.7, 4, Math.PI / 4, NIGHT, NIGHT_DARK);
		for (double s : new double[] { 7.5, 10.5, 13.5 }) {
			band(m, s, s + 0.8, 1.9, STEEL_DARK);
		}
		// guard: arms sweeping down towards the hand, night-blue tips
		for (int side = -1; side <= 1; side += 2) {
			tube(m, f(18.5, 0, 0), f(17, side * 8, 0), 1.8, 1.3, 4, Math.PI / 4, STEEL_DARK, STEEL);
			tube(m, f(17, side * 8, 0), f(15, side * 10.5, 0), 1.3, 0, 4, Math.PI / 4, NIGHT, NIGHT);
		}
		collar(m, 17, 20, 2.4, 2.0, STEEL_DARK);
		diamond(m, new double[][] { { 20, 3.4, 1.1 }, { 50, 3.5, 1.1 }, { 84, 3.0, 0.95 }, { 96, 2.2, 0.8 } }, 107, 0, STEEL_EDGE, STEEL);
		// the fuller: a dark groove down each face
		for (int side = -1; side <= 1; side += 2) {
			tube(m, f(22, 0, side * 1.05), f(80, 0, side * 0.9), 0.55, 0.4, 3, 0, STEEL_DARK, STEEL_DARK);
		}
		return m;
	}

	/* ====================================================================== */
	/*                           12. WARDEN'S HALBERD                         */
	/* ====================================================================== */

	/** Carried on the wall: a blackened-steel axe head and spike, and a night-blue pennant with the Wardens' star. */
	static ModelData wardenHalberd() {
		ModelData m = new ModelData();
		haft(m, 8, 204, 3.2, 2.9, WOOD_DARK);
		tube(m, f(0, 0, 0), f(9, 0, 0), 1.6, 3.8, 4, Math.PI / 4, BLACKSTEEL, BLACKSTEEL);
		band(m, 104, 134, 3.7, NIGHT);
		band(m, 150, 162, 3.6, NIGHT);
		collar(m, 168, 176, 4.0, 5.0, BLACKSTEEL);
		haft(m, 176, 214, 4.4, 4.0, BLACKSTEEL);
		// the axe blade (+w), squared and bearded
		double[][] blade = { { 180, 3 }, { 210, 3 }, { 222, 26 }, { 206, 32 }, { 186, 33 }, { 170, 27 } };
		plateSW(m, blade, new double[] { 2.2, 2.2, 0, 0, 0, 0 }, new double[] { 195, 18 }, 2.0, BLACKSTEEL_LIGHT, STEEL_EDGE, BLACKSTEEL);
		// the back spike (-w)
		tube(m, f(196, -3, 0), f(194, -20, 0), 3.4, 0, 4, Math.PI / 4, BLACKSTEEL_LIGHT, BLACKSTEEL);
		// the top spike
		diamond(m, new double[][] { { 212, 3.6, 3.0 }, { 220, 4.6, 2.4 } }, 242, 0, STEEL_EDGE, STEEL);
		// the pennant, hanging off the back below the head, with a white star on it
		double[][] flag = { { 140, -3 }, { 168, -3 }, { 160, -30 }, { 152, -25 }, { 146, -32 } };
		plateSW(m, flag, new double[] { 0.6, 0.6, 0.4, 0.4, 0.4 }, new double[] { 155, -14 }, 0.6, NIGHT, NIGHT, NIGHT_DARK);
		star(m, f(156, -13, 0), 3.4, 1.4, STAR_WHITE, STEEL);
		return m;
	}

	/* ====================================================================== */
	/*                           13. WARDEN'S LONGBOW                         */
	/* ====================================================================== */

	/** The wall's bow: dark yew, blackened-steel caps, a night-blue grip with the Wardens' star set in it. */
	static ModelData wardenLongbow(double bend) {
		ModelData m = new ModelData();
		bow(m, 185, bend, new double[] { 1.7, 2.4, 3.2, 3.8, 4.1, 3.8, 3.2, 2.4, 1.7 }, hsl(6, 4, 30), hsl(6, 4, 22), NIGHT, BLACKSTEEL_LIGHT,
				hsl(8, 1, 80));
		double gw = 6 * bend * Math.sqrt(185 / 127.0) + 1.2;
		star(m, f(92.5, gw + 4.6, 0), 2.6, 1.3, STAR_WHITE, STEEL);
		// blackened-steel bands where the limbs meet the grip
		for (double s : new double[] { 78, 107 }) {
			double w = 6.9 * bend * Math.sqrt(185 / 127.0);
			tube(m, f(s - 2, w, 0), f(s + 2, w, 0), 4.6, 4.6, 4, Math.PI / 4, BLACKSTEEL_LIGHT, BLACKSTEEL);
		}
		return m;
	}

	/* ====================================================================== */
	/*                           14. SCARGLASS DAGGER                         */
	/* ====================================================================== */

	/** A shard of the Scar's black glass, knapped to an edge and bound to a bone grip. The first violet on the ladder. */
	static ModelData scarglassDagger() {
		ModelData m = new ModelData();
		int glass = violet(18, 3), glassLight = violet(34, 4), glint = violet(84, 6);
		tube(m, f(0, 0, 0), f(12, 0, 0), 2.0, 1.6, 5, 0, BONE_DARK, BONE);
		for (double s : new double[] { 3, 6.5, 10 }) {
			collar(m, s, s + 1, 2.1, 2.1, BLACKSTEEL);
		}
		// guard: two small glass barbs, and a glint of violet where the shard is seated
		tube(m, f(12.5, 0, 0), f(10, 5.5, 0), 1.5, 0, 4, 0.4, glass, glassLight);
		tube(m, f(12.5, 0, 0), f(10.5, -4.5, 0), 1.4, 0, 4, 0.4, glass, glassLight);
		gem(m, 13.5, 0, 1.6, 2.6, glint, glint);
		// the shard: irregular, knapped, with violet light along its edges
		double[][] shard = { { 13, -2.4 }, { 19, -3.4 }, { 26, -2.6 }, { 33, -1.4 }, { 39.5, 0.6 }, { 31, 2.8 }, { 24, 3.9 }, { 16, 2.9 } };
		plateSW(m, shard, new double[] { 0.9, 0, 0, 0, 0, 0, 0, 0.9 }, new double[] { 23, 0.3 }, 1.0, glass, glint, glassLight);
		return m;
	}

	/* ====================================================================== */
	/*                         15. OATHSWORN BATTLEAXE                        */
	/* ====================================================================== */

	/** An old king's guard's axe from the barrow: two bronze crescents, honed edges pale with grave-light. */
	static ModelData oathswornAxe() {
		ModelData m = new ModelData();
		int ghost = hsl(25, 2, 90), ghostGem = hsl(23, 5, 86);
		haft(m, 2, 64, 2.6, 2.3, WOOD_DARK);
		knob(m, 2, 3.4, 2.6, BRONZE, BRONZE_DARK);
		band(m, 4, 16, 3.0, LEATHER_DARK);
		band(m, 20, 22, 3.0, BRONZE);
		collar(m, 34, 62, 4.2, 3.6, BRONZE_DARK);
		// the great bit (+w) and the lesser (-w)
		double[][] big = { { 38, 3 }, { 33, 10 }, { 25, 22 }, { 38, 25 }, { 50, 26 }, { 62, 25 }, { 72, 21 }, { 63, 10 }, { 58, 3 } };
		plateSW(m, big, new double[] { 2.4, 1.6, 0, 0, 0, 0, 0, 1.6, 2.4 }, new double[] { 48, 13 }, 2.6, BRONZE, ghost, BRONZE_DARK);
		double[][] small = { { 40, -3 }, { 37, -9 }, { 33, -17 }, { 48, -20 }, { 63, -17 }, { 59, -9 }, { 56, -3 } };
		plateSW(m, small, new double[] { 2.2, 1.4, 0, 0, 0, 1.4, 2.2 }, new double[] { 48, -9 }, 2.4, BRONZE, ghost, BRONZE_DARK);
		// a bronze spike on top, and the grave-light in the socket
		tube(m, f(61, 0, 0), f(73, 0, 0), 3.6, 0, 4, Math.PI / 4, BRONZE, BRONZE_DARK);
		gem(m, 48, 0, 2.2, 5.0, ghostGem, ghostGem);
		return m;
	}

	/* ====================================================================== */
	/*                            16. FROSTFANG SPEAR                         */
	/* ====================================================================== */

	/** Rimeclaw's fang, set on a frost-blackened haft: a long ice blade with two icicle wings, rime glittering on it. */
	static ModelData frostfangSpear() {
		ModelData m = new ModelData();
		int haftCol = hsl(36, 1, 20), wrap = hsl(36, 3, 46), ice = hsl(36, 2, 94), iceDark = hsl(36, 3, 72), rime = hsl(36, 1, 116);
		haft(m, 6, 214, 3.0, 2.7, haftCol);
		tube(m, f(0, 0, 0), f(6, 0, 0), 1.0, 3.2, 4, Math.PI / 4, iceDark, iceDark);
		band(m, 108, 136, 3.5, wrap);
		band(m, 170, 176, 3.4, wrap);
		// frost crystals bristling round the socket
		collar(m, 206, 216, 3.4, 4.8, iceDark);
		for (int k = 0; k < 5; k++) {
			double a = Math.PI * 2 * k / 5 + 0.3;
			tube(m, f(212, 0, 0), f(206 - (k % 2) * 4, Math.cos(a) * 8, Math.sin(a) * 8), 2.2, 0, 4, a, ice, iceDark);
		}
		// the fang: a long, slightly curved ice blade with rime along its edges
		bladeStrip(m, new double[][] { { 215, 4.6, 2.6 }, { 230, 6.0, 2.6 }, { 248, 5.0, 2.2 }, { 262, 3.0, 1.6 } }, 282, ice, rime);
		// two icicle wings
		tube(m, f(222, 4, 0), f(212, 15, 0), 2.6, 0, 4, 0.3, ice, iceDark);
		tube(m, f(222, -4, 0), f(212, -15, 0), 2.6, 0, 4, 0.3, ice, iceDark);
		return m;
	}

	/* ====================================================================== */
	/*                        17. GRAVEWARDEN 2H SWORD                        */
	/* ====================================================================== */

	/** The barrow-guard's greatsword: a broad black-iron blade whose edges carry a cold grave-light, a tombstone guard. */
	static ModelData gravewardenSword() {
		ModelData m = new ModelData();
		int iron = hsl(10, 0, 24), ironLight = hsl(10, 0, 34), ghost = hsl(25, 2, 86), ghostGem = hsl(23, 5, 88), stone = hsl(10, 0, 50),
				stoneDark = hsl(10, 0, 36);
		// a bone-ringed grip and a skull-ish pommel
		knob(m, 2.5, 4.0, 3.0, BONE, BONE_DARK);
		tube(m, f(5, 0, 0), f(34, 0, 0), 2.2, 2.4, 4, Math.PI / 4, LEATHER_DARK, LEATHER_DARK);
		for (double s : new double[] { 9, 16, 23, 30 }) {
			band(m, s, s + 1.4, 2.7, BONE_DARK);
		}
		// the tombstone guard: a stone block, rounded on top, a grave-light rune in it
		tube(m, f(36, -12, 0), f(36, 12, 0), 3.4, 3.4, 4, Math.PI / 4, stone, stoneDark);
		tube(m, f(36, 12, 0), f(36, 15, 0), 3.4, 1.0, 4, Math.PI / 4, stoneDark, stoneDark);
		tube(m, f(36, -12, 0), f(36, -15, 0), 3.4, 1.0, 4, Math.PI / 4, stoneDark, stoneDark);
		gem(m, 37, 0, 2.2, 3.8, ghostGem, ghostGem);
		// the blade: broad, black iron, the edges lit
		bladeStrip(m, new double[][] { { 38, 6.0, 1.8 }, { 46, 7.0, 1.8 }, { 100, 6.2, 1.6 }, { 128, 5.0, 1.4 } }, 146, iron, ghost, 0.62);
		// a dark ridge down each face
		for (int side = -1; side <= 1; side += 2) {
			tube(m, f(40, 0, side * 1.8), f(118, 0, side * 1.5), 0.8, 0.5, 3, 0, ironLight, ironLight);
		}
		return m;
	}

	/* ====================================================================== */
	/*                          18. HOLLOWHEART SHORTBOW                      */
	/* ====================================================================== */

	/** Ribbed limbs of bone and violet, strung with something that isn't gut, and a heart in the grip that still beats. */
	static ModelData hollowheartBow(double bend) {
		ModelData m = new ModelData();
		int rib = violet(40, 4), heart = violet(88, 6), heartCore = violet(114, 4), string = violet(100, 5);
		bow(m, 127, bend, new double[] { 1.5, 2.2, 3.0, 3.5, 3.8, 3.5, 3.0, 2.2, 1.5 }, BONE, rib, violet(26, 3), BONE_DARK, string);
		// rib spurs off the back of each limb
		for (double s : new double[] { 22, 36, 91, 105 }) {
			double w = (s < 64 ? (s < 30 ? 7 : 9) : (s > 100 ? 7 : 9)) * bend;
			tube(m, f(s, w, 0), f(s + (s < 64 ? -5 : 5), w + 6.5, 0), 1.5, 0, 4, Math.PI / 4, BONE_DARK, BONE);
		}
		// the heart, on the belly of the grip
		double gw = 6 * bend + 4.6;
		tube(m, f(58, gw, 0), f(64, gw + 1.5, 0), 1.4, 4.2, 6, 0, heart, heart);
		tube(m, f(64, gw + 1.5, 0), f(71, gw, 0), 4.2, 1.2, 6, 0, heartCore, heart);
		return m;
	}

	/* ====================================================================== */
	/*                           19. WAND OF THE SCAR                         */
	/* ====================================================================== */

	/** The Silkwood wand, woken at a waystone: the wood gone black as Scar glass, a violet crystal caged in starmetal. */
	static ModelData scarWand() {
		ModelData m = new ModelData();
		int rod = violet(16, 2), rodLight = violet(28, 3), crystal = violet(96, 6), core = violet(120, 3);
		tube(m, f(3, 0, 0), f(82, 0, 0), 2.2, 1.6, 5, 0, rod, rodLight);
		tube(m, f(0, 0, 0), f(5, 0, 0), 1.0, 2.8, 5, 0.3, STARMETAL_DARK, STARMETAL_DARK);
		band(m, 14, 30, 2.6, LEATHER_DARK);
		for (double s : new double[] { 32, 56, 74 }) {
			collar(m, s, s + 1.4, 2.3, 2.3, STARMETAL);
		}
		// three starmetal prongs round the crystal
		for (int k = 0; k < 3; k++) {
			double a = Math.PI * 2 * k / 3;
			tube(m, f(80, Math.cos(a) * 1.8, Math.sin(a) * 1.8), f(88, Math.cos(a) * 5.2, Math.sin(a) * 5.2), 1.0, 0.8, 3, a, STARMETAL, STARMETAL);
			tube(m, f(88, Math.cos(a) * 5.2, Math.sin(a) * 5.2), f(100, Math.cos(a) * 1.4, Math.sin(a) * 1.4), 0.8, 0, 3, a, STARMETAL_EDGE, STARMETAL);
		}
		tube(m, f(82, 0, 0), f(90, 0, 0), 0.8, 3.6, 6, 0.5, crystal, crystal);
		tube(m, f(90, 0, 0), f(101, 0, 0), 3.6, 0, 6, 0.5, core, crystal);
		return m;
	}

	/* ====================================================================== */
	/*                         20. GRAVESTAR WARHAMMER                        */
	/* ====================================================================== */

	/** The Barrowstone hammer, faced in starmetal at Gatehold's anvil: a pale ringing head, a beak, a star-shard set in it. */
	static ModelData gravestarHammer() {
		ModelData m = new ModelData();
		int shard = violet(92, 6), shardCore = violet(118, 3);
		haft(m, 2, 60, 2.5, 2.2, BLACKSTEEL);
		knob(m, 2, 3.4, 2.6, STARMETAL, STARMETAL_DARK);
		band(m, 4, 16, 2.9, LEATHER_DARK);
		for (double s : new double[] { 20, 36, 42 }) {
			band(m, s, s + 1.6, 2.9, STARMETAL_DARK);
		}
		// the head: a striking face on +w, flaring, and a beak on -w
		tube(m, f(52, -6, 0), f(52, 8, 0), 7.4, 7.4, 4, Math.PI / 4, STARMETAL, STARMETAL_DARK);
		tube(m, f(52, 8, 0), f(52, 13, 0), 7.4, 9.0, 4, Math.PI / 4, STARMETAL_DARK, STARMETAL_EDGE);
		tube(m, f(52, -6, 0), f(51, -10, 0), 7.4, 5.0, 4, Math.PI / 4, STARMETAL_DARK, STARMETAL_DARK);
		tube(m, f(51, -10, 0), f(46, -24, 0), 5.0, 0, 4, Math.PI / 4, STARMETAL, STARMETAL_DARK);
		// a short spike on top
		tube(m, f(57, 0, 0), f(68, 0, 0), 4.2, 0, 4, Math.PI / 4, STARMETAL, STARMETAL_DARK);
		// the star-shard, through the head
		gem(m, 52, 1, 4.6, 12, shard, shard);
		gem(m, 52, 1, 2.2, 13.5, shardCore, shardCore);
		return m;
	}

	/* ====================================================================== */
	/*                          21. RIFTGLASS HALBERD                         */
	/* ====================================================================== */

	/** A blade knapped from the rift in Starfall Crater: jagged violet glass, every edge alight, a seam of the rift in it. */
	static ModelData riftglassHalberd() {
		ModelData m = new ModelData();
		int glass = violet(46, 5), glassDark = violet(30, 4), edge = violet(108, 5), rift = violet(122, 2);
		haft(m, 8, 204, 3.3, 3.0, violet(14, 2));
		tube(m, f(0, 0, 0), f(9, 0, 0), 1.6, 3.8, 4, Math.PI / 4, STARMETAL_DARK, STARMETAL_DARK);
		band(m, 104, 134, 3.8, BLACKSTEEL_LIGHT);
		band(m, 150, 162, 3.7, BLACKSTEEL_LIGHT);
		for (double s : new double[] { 100, 136, 164 }) {
			collar(m, s, s + 2, 4.0, 4.0, STARMETAL);
		}
		collar(m, 166, 176, 4.0, 5.6, STARMETAL_DARK);
		haft(m, 176, 214, 4.6, 4.2, STARMETAL_DARK);
		// the great jagged crescent (+w)
		double[][] blade = { { 178, 3 }, { 172, 14 }, { 158, 30 }, { 172, 33 }, { 184, 38 }, { 198, 36 }, { 210, 41 }, { 226, 34 }, { 238, 27 },
				{ 222, 18 }, { 216, 4 } };
		plateSW(m, blade, new double[] { 2.6, 1.8, 0, 0, 0, 0, 0, 0, 0, 1.6, 2.6 }, new double[] { 196, 19 }, 2.8, glass, edge, glassDark);
		// a jagged hook behind (-w)
		double[][] hook = { { 182, -3 }, { 206, -3 }, { 204, -14 }, { 190, -30 }, { 192, -16 }, { 184, -12 } };
		plateSW(m, hook, new double[] { 2.0, 2.0, 1.0, 0, 0.6, 0.6 }, new double[] { 194, -9 }, 2.0, glass, edge, glassDark);
		// the top spike, and the rift's seam glowing through the blade
		bladeStrip(m, new double[][] { { 212, 4.0, 3.0 }, { 224, 5.2, 2.6 } }, 250, glass, edge);
		tube(m, f(184, 10, 0), f(216, 30, 0), 1.6, 1.6, 4, Math.PI / 4, rift, rift);
		gem(m, 200, 20, 3.6, 4.8, rift, edge);
		return m;
	}

	/* ====================================================================== */
	/*                            22. RIMEHEART STAFF                         */
	/* ====================================================================== */

	/** A staff of rime-wood with a frozen heart held in a claw of ice. It never needs water runes: it is the water. */
	static ModelData rimeheartStaff() {
		ModelData m = new ModelData();
		int wood = hsl(36, 1, 54), woodDark = hsl(36, 2, 40), ice = hsl(36, 2, 96), iceDark = hsl(36, 3, 74), heart = hsl(36, 3, 112),
				heartCore = hsl(36, 1, 124), frost = hsl(36, 1, 116);
		tube(m, f(6, 0, 0), f(80, 0.8, 0), 4.0, 4.3, 5, 0, wood, wood);
		tube(m, f(80, 0.8, 0), f(150, -0.6, 0), 4.3, 4.0, 5, 0.3, woodDark, woodDark);
		tube(m, f(150, -0.6, 0), f(198, 0, 0), 4.0, 4.6, 5, 0.1, wood, wood);
		tube(m, f(0, 0, 0), f(6, 0, 0), 1.6, 4.4, 5, 0, iceDark, iceDark);
		for (double s : new double[] { 40, 104, 136, 176 }) {
			collar(m, s, s + 3, 5.0, 5.0, frost);
		}
		// the claw: five icicle talons curling up and in round the heart
		collar(m, 194, 204, 4.8, 7.4, iceDark);
		for (int k = 0; k < 5; k++) {
			double a = Math.PI * 2 * k / 5;
			double[] p0 = f(202, Math.cos(a) * 6, Math.sin(a) * 6), p1 = f(216, Math.cos(a) * 15, Math.sin(a) * 15),
					p2 = f(238, Math.cos(a + 0.3) * 6, Math.sin(a + 0.3) * 6);
			tube(m, p0, p1, 3.0, 2.2, 4, a, ice, iceDark);
			tube(m, p1, p2, 2.2, 0, 4, a, ice, frost);
		}
		// the heart: a faceted crystal, lit from inside
		tube(m, f(208, 0, 0), f(220, 0, 0), 2.0, 9.0, 6, 0.3, heart, heart);
		tube(m, f(220, 0, 0), f(234, 0, 0), 9.0, 0, 6, 0.3, heartCore, heart);
		// icicles hanging under the claw
		for (int k = 0; k < 3; k++) {
			double a = Math.PI * 2 * k / 3 + 0.5;
			tube(m, f(196, Math.cos(a) * 5, Math.sin(a) * 5), f(178 - k * 4, Math.cos(a) * 6, Math.sin(a) * 6), 1.8, 0, 4, a, ice, frost);
		}
		return m;
	}

	/* ====================================================================== */
	/*                          23. NIGHTGLASS SCIMITAR                       */
	/* ====================================================================== */

	/** Black glass from the dark between the stars, curved like a new moon, its whole edge burning violet. */
	static ModelData nightglassScimitar() {
		ModelData m = new ModelData();
		int glass = violet(12, 2), glassLight = violet(24, 3), edge = violet(104, 6), gemCol = violet(90, 6), gemCore = violet(120, 3);
		// pommel gem, a black grip wound with starmetal wire
		tube(m, f(-2, 0, 0), f(2, 0, 0), 0, 3.0, 6, 0, gemCol, gemCol);
		tube(m, f(2, 0, 0), f(5, 0, 0), 3.0, 1.8, 6, 0, STARMETAL, STARMETAL_DARK);
		tube(m, f(5, 0, 0), f(27, 0, 0), 1.8, 2.0, 4, Math.PI / 4, BLACKSTEEL, BLACKSTEEL);
		for (double s : new double[] { 8, 12, 16, 20, 24 }) {
			band(m, s, s + 0.8, 2.25, STARMETAL_DARK);
		}
		// the guard: two crescent wings of starmetal, a violet eye between them
		for (int side = -1; side <= 1; side += 2) {
			tube(m, f(29, 0, 0), f(27, side * 6, 0), 2.2, 1.6, 4, Math.PI / 4, STARMETAL_DARK, STARMETAL);
			tube(m, f(27, side * 6, 0), f(33, side * 10, 0), 1.6, 0, 4, Math.PI / 4, STARMETAL, STARMETAL_EDGE);
		}
		gem(m, 29.5, 0, 2.4, 3.4, gemCol, gemCore);
		// the blade: two plates, a deep curve, the belly on +w and the point sweeping back
		plateSW(m, new double[][] { { 30, -1.8 }, { 50, -3.0 }, { 72, -5.4 }, { 72, 7.4 }, { 50, 8.6 }, { 31, 4.8 } },
				new double[] { 1.2, 1.2, 1.1, 0, 0, 0 }, new double[] { 50, 2.4 }, 1.1, glassLight, edge, glass);
		plateSW(m, new double[][] { { 70, -5.2 }, { 88, -10.5 }, { 104, -19 }, { 97, -7.5 }, { 85, 1.5 }, { 70, 7.4 } },
				new double[] { 1.1, 1.0, 0, 0, 0, 0 }, new double[] { 84, -3 }, 1.0, glassLight, edge, glass);
		return m;
	}

	/* ====================================================================== */
	/*                          24. CROWNBREAKER MACE                         */
	/* ====================================================================== */

	/**
	 * The mace that broke the Hollow King's crown: a burst of starmetal shards split open round a white-hot star, and
	 * beneath it the broken circlet of the crown itself, gold, its points snapped short.
	 */
	static ModelData crownbreakerMace() {
		ModelData m = new ModelData();
		int gold = hsl(8, 4, 62), goldDark = hsl(8, 5, 42), goldBright = hsl(8, 3, 90), core = violet(122, 2), glow = violet(100, 6);
		haft(m, 4, 44, 2.6, 2.3, BLACKSTEEL);
		knob(m, 2.5, 3.6, 3.0, gold, goldDark);
		band(m, 6, 22, 3.0, LEATHER_DARK);
		band(m, 24, 26, 3.1, gold);
		// the broken crown: a circlet with five points, two of them snapped short
		collar(m, 38, 42, 3.0, 8.5, goldDark);
		collar(m, 42, 46, 8.5, 8.5, gold);
		for (int k = 0; k < 5; k++) {
			double a = Math.PI * 2 * k / 5 + 0.3;
			double len = k == 1 || k == 3 ? 3 : 9;
			tube(m, f(45, Math.cos(a) * 8, Math.sin(a) * 8), f(46 + len, Math.cos(a) * 9.5, Math.sin(a) * 9.5), 2.2, k == 1 || k == 3 ? 1.4 : 0, 4, a,
					goldBright, gold);
		}
		// the star inside, white-hot
		star(m, f(60, 0, 0), 15.0, 3.8, glow, core);
		tube(m, f(48, 0, 0), f(60, 0, 0), 2.0, 7.0, 6, 0, glow, glow);
		tube(m, f(60, 0, 0), f(70, 0, 0), 7.0, 2.0, 6, 0, core, core);
		// five starmetal shards bursting outward round it, the star's rays between them
		for (int k = 0; k < 5; k++) {
			double a = Math.PI * 2 * k / 5 + Math.PI / 5;
			double[] V = { Math.sin(a), Math.cos(a), 0 }, N = { Math.cos(a), -Math.sin(a), 0 };
			double[][] pts = { { 48, 7 }, { 55, 15 }, { 68, 17 }, { 76, 9 }, { 64, 8 } };
			plate(m, v(0, 0, 0), v(0, 0, 1), V, N, pts, new double[] { 1.3, 0, 0, 0, 1.1 }, new double[] { 61, 11.5 }, 1.4, STARMETAL,
					STARMETAL_EDGE, STARMETAL_DARK);
		}
		return m;
	}

	/* ====================================================================== */
	/*                          25. STARLESS LONGSWORD                        */
	/* ====================================================================== */

	/**
	 * The Starless's own edge, shed like a scale: a blade of the dark between the stars with stars still in it, its edges
	 * white-violet, a corona for a guard round a single open eye, and a black sun for a pommel.
	 */
	static ModelData starlessLongsword() {
		ModelData m = new ModelData();
		int black = violet(6, 1), blackLight = violet(14, 2), edge = violet(118, 3), corona = violet(80, 6), coronaHot = violet(104, 5),
				eye = hsl(42, 0, 124), pupil = violet(24, 7);
		// the black sun pommel, its corona flaring
		star(m, f(0.5, 0, 0), 4.2, 1.5, corona, coronaHot);
		tube(m, f(-2.2, 0, 0), f(0.5, 0, 0), 0, 3.0, 6, 0, black, black);
		tube(m, f(0.5, 0, 0), f(3.2, 0, 0), 3.0, 1.6, 6, 0, black, black);
		tube(m, f(3.2, 0, 0), f(16, 0, 0), 1.6, 1.8, 4, Math.PI / 4, blackLight, blackLight);
		for (double s : new double[] { 6, 9.5, 13 }) {
			band(m, s, s + 0.8, 2.0, coronaHot);
		}
		// the guard: a corona of eight rays round an open eye
		for (int k = 0; k < 8; k++) {
			double a = Math.PI * 2 * k / 8;
			double r = k % 2 == 0 ? 14 : 9;
			tube(m, f(18.5, Math.cos(a) * 3, Math.sin(a) * 3), f(18.5 + (k % 2 == 0 ? 3 : 1), Math.cos(a) * r, Math.sin(a) * r * 0.55), 1.8, 0, 4, a,
					k % 2 == 0 ? coronaHot : corona, corona);
		}
		collar(m, 16.5, 20.5, 3.4, 3.4, black);
		tube(m, f(18.5, 0, 0), f(18.5, 0, 2.6), 3.0, 3.0, 8, 0, black, eye);
		tube(m, f(18.5, 0, 0), f(18.5, 0, -2.6), 3.0, 3.0, 8, 0, black, eye);
		tube(m, f(18.5, 0, 2.6), f(18.5, 0, 3.3), 1.5, 0.9, 6, 0, pupil, pupil);
		tube(m, f(18.5, 0, -2.6), f(18.5, 0, -3.3), 1.5, 0.9, 6, 0, pupil, pupil);
		// the blade: broad and black, its edges burning white-violet
		bladeStrip(m, new double[][] { { 20, 4.0, 1.3 }, { 26, 5.2, 1.4 }, { 60, 5.0, 1.35 }, { 88, 4.0, 1.2 }, { 98, 2.9, 1.0 } }, 110, black, edge,
				0.52);
		// stars in it, glinting on both faces
		double[][] stars = { { 34, 0.6 }, { 47, -0.9 }, { 59, 0.8 }, { 72, -0.6 }, { 84, 0.4 } };
		for (double[] st : stars) {
			gem(m, st[0], st[1], 1.2, 2.3, eye, edge);
		}
		return m;
	}

	/* ====================================================================== */
	/*                            26. EVENTIDE LONGBOW                        */
	/* ====================================================================== */

	/** A bow of dusk: limbs gilt at the tips going violet at the grip, a setting sun behind the hand, a string of starlight. */
	static ModelData eventideBow(double bend) {
		ModelData m = new ModelData();
		int dusk = violet(34, 5), duskDark = violet(20, 4), gold = hsl(8, 4, 66), goldDark = hsl(8, 5, 44), sun = hsl(7, 5, 92), sunCore = hsl(9, 2, 122);
		double k = 185 / 127.0, rk = Math.sqrt(k);
		bow(m, 185, bend, new double[] { 1.7, 2.3, 3.1, 3.7, 4.0, 3.7, 3.1, 2.3, 1.7 }, dusk, duskDark, NIGHT_DARK, gold, STAR_WHITE);
		// gilt sleeves over the outer limbs
		double[][] limb = { { 3, 4 }, { 8, -0.5 }, { 24, 7 } };
		for (int end = 0; end < 2; end++) {
			for (int i = 0; i + 1 < limb.length; i++) {
				double s0 = limb[i][0] * k, s1 = limb[i + 1][0] * k;
				double w0 = (limb[i][1] < 0 ? limb[i][1] : limb[i][1] * bend) * rk, w1 = (limb[i + 1][1] < 0 ? limb[i + 1][1] : limb[i + 1][1] * bend) * rk;
				if (end == 1) {
					s0 = 185 - s0;
					s1 = 185 - s1;
				}
				tube(m, f(s0, w0, 0), f(s1, w1, 0), 2.3 + i * 0.6, 2.9 + i * 0.6, 4, Math.PI / 4, i == 0 ? gold : goldDark, gold);
			}
		}
		// the setting sun behind the grip: a gold disc risen past the belly, and its rays
		double gw = 6 * bend * rk + 1.2, sw = gw + 7;
		tube(m, f(92.5, sw, -1.0), f(92.5, sw, 1.0), 10.5, 10.5, 8, 0, sun, sun);
		tube(m, f(92.5, sw, -1.4), f(92.5, sw, 1.4), 6.0, 6.0, 8, 0, sunCore, sunCore);
		for (int r = 0; r < 7; r++) {
			double a = Math.PI * (r + 1) / 8; // a fan over the outside (+w)
			double len = r % 2 == 0 ? 24 : 17;
			tube(m, f(92.5 - Math.cos(a) * 10, sw + Math.sin(a) * 10, 0), f(92.5 - Math.cos(a) * len, sw + Math.sin(a) * len, 0), 1.9, 0, 4,
					Math.PI / 4, r % 2 == 0 ? sun : gold, gold);
		}
		// a star at each nock
		star(m, f(-1, 4 * bend * rk + 5, 0), 3.2, 1.0, STAR_WHITE, sun);
		star(m, f(186, 4 * bend * rk + 5, 0), 3.2, 1.0, STAR_WHITE, sun);
		return m;
	}

	/* ====================================================================== */
	/*                                27. SUNSPEAR                            */
	/* ====================================================================== */

	/** A spear with the sun on it: a white-gold leaf blade rising out of a burning corona, on a haft of pale ash bound in gold. */
	static ModelData sunspear() {
		ModelData m = new ModelData();
		int ash = hsl(8, 2, 84), ashDark = hsl(8, 2, 66), gold = hsl(8, 4, 64), goldDark = hsl(8, 5, 44), goldBright = hsl(9, 2, 112),
				ember = hsl(4, 6, 74), emberHot = hsl(6, 5, 100), core = hsl(9, 1, 124);
		haft(m, 6, 206, 3.0, 2.7, ash);
		tube(m, f(-6, 0, 0), f(6, 0, 0), 0, 3.2, 4, Math.PI / 4, gold, goldDark);
		band(m, 106, 136, 3.2, LEATHER);
		for (double s : new double[] { 60, 104, 136, 180 }) {
			band(m, s, s + 2, 3.3, gold);
		}
		tube(m, f(150, 0, 0), f(170, 0, 0), 2.7, 2.7, 4, Math.PI / 4, ashDark, ashDark);
		// the socket, and the sun: a gold disc ringed with burning rays
		collar(m, 200, 214, 3.0, 4.2, goldDark);
		collar(m, 214, 224, 4.2, 3.6, gold);
		double cs = 218;
		tube(m, f(cs, 0, -1.8), f(cs, 0, 1.8), 9.0, 9.0, 10, 0, gold, gold);
		tube(m, f(cs, 0, -2.4), f(cs, 0, 2.4), 5.0, 5.0, 10, 0, core, core);
		for (int k = 0; k < 12; k++) {
			double a = Math.PI * 2 * k / 12 + Math.PI / 12;
			double r = (k % 2 == 0 ? 24 : 16) * (Math.abs(Math.cos(a)) > 0.8 ? 0.75 : 1);
			tube(m, f(cs + Math.cos(a) * 8, Math.sin(a) * 8, 0), f(cs + Math.cos(a) * r, Math.sin(a) * r, 0), 2.4, 0, 4, a + Math.PI / 4,
					k % 2 == 0 ? emberHot : ember, ember);
		}
		// the blade: a long leaf of white gold, its edges catching the light
		bladeStrip(m, new double[][] { { 222, 3.0, 2.0 }, { 238, 6.8, 2.2 }, { 254, 5.6, 1.9 }, { 268, 3.4, 1.4 } }, 290, goldBright, core, 0.5);
		gem(m, 236, 0, 2.2, 3.4, emberHot, emberHot);
		return m;
	}

	/* ====================================================================== */
	/*                         28. STAFF OF THE FIRST STAR                    */
	/* ====================================================================== */

	/** The first star that ever burned over Nythera, caught in a gold armillary of three rings on a staff of night. */
	static ModelData firstStarStaff() {
		ModelData m = new ModelData();
		int gold = hsl(8, 4, 64), goldDark = hsl(8, 5, 44), goldBright = hsl(9, 2, 104), core = hsl(42, 0, 126), halo = hsl(10, 2, 116);
		tube(m, f(6, 0, 0), f(196, 0, 0), 3.6, 4.0, 6, 0, NIGHT, NIGHT_DARK);
		tube(m, f(-2, 0, 0), f(6, 0, 0), 1.2, 3.9, 6, 0, gold, goldDark);
		band(m, 100, 138, 4.2, NIGHT_DARK);
		for (double s : new double[] { 30, 98, 138, 170 }) {
			collar(m, s, s + 2.5, 4.6, 4.6, gold);
		}
		// small stars set up the haft, like the staff's own sky
		for (double s : new double[] { 50, 74, 160, 184 }) {
			gem(m, s, 0, 1.2, 4.8, goldBright, goldBright);
		}
		// the cradle: three gold arms lifting the armillary
		collar(m, 194, 202, 4.2, 6.2, goldDark);
		double cs = 216, R = 14;
		for (int k = 0; k < 3; k++) {
			double a = Math.PI * 2 * k / 3;
			tube(m, f(200, Math.cos(a) * 5, Math.sin(a) * 5), f(cs - R * 0.8, Math.cos(a) * R * 0.6, Math.sin(a) * R * 0.6), 1.6, 1.2, 4, a, gold, gold);
		}
		// the armillary: three gold rings round one centre, each tilted
		double[][][] planes = { { { 1, 0, 0 }, { 0, 1, 0 } }, { { 1, 0, 0 }, { 0, 0.5, 0.866 } }, { { 0.25, 0.7, 0 }, { 0, 0, 1 } } };
		for (int p = 0; p < planes.length; p++) {
			double[] U = unit(planes[p][0]), V = unit(planes[p][1]);
			int seg = 10;
			for (int i = 0; i < seg; i++) {
				double a0 = Math.PI * 2 * i / seg, a1 = Math.PI * 2 * (i + 1) / seg;
				double[] p0 = f(cs + R * (U[0] * Math.cos(a0) + V[0] * Math.sin(a0)), R * (U[1] * Math.cos(a0) + V[1] * Math.sin(a0)),
						R * (U[2] * Math.cos(a0) + V[2] * Math.sin(a0)));
				double[] p1 = f(cs + R * (U[0] * Math.cos(a1) + V[0] * Math.sin(a1)), R * (U[1] * Math.cos(a1) + V[1] * Math.sin(a1)),
						R * (U[2] * Math.cos(a1) + V[2] * Math.sin(a1)));
				tube(m, p0, p1, 1.1, 1.1, 3, 0, p == 0 ? goldBright : gold, gold);
			}
		}
		// the first star, white, with a halo, its long ray up the staff
		star(m, f(cs, 0, 0), 11, 3.2, core, halo);
		tube(m, f(cs, 0, 0), f(cs + 6, 0, 0), 6, 0, 6, 0, core, core);
		tube(m, f(cs, 0, 0), f(cs - 6, 0, 0), 6, 0, 6, 0, halo, halo);
		return m;
	}

	/* ====================================================================== */
	/*                          29. KINGSBANE BATTLEAXE                       */
	/* ====================================================================== */

	/**
	 * The axe that was forged to end a king: two great black bits edged in gold, a crown on the haft-top with a single red
	 * stone in it, and a haft of blackened oak bound in gold.
	 */
	static ModelData kingsbaneAxe() {
		ModelData m = new ModelData();
		int black = BLACKSTEEL, blackLight = BLACKSTEEL_LIGHT, gold = hsl(8, 4, 64), goldDark = hsl(8, 5, 44), goldBright = hsl(9, 2, 106),
				ruby = hsl(0, 6, 56), rubyHot = hsl(1, 5, 86);
		haft(m, 2, 70, 2.8, 2.5, hsl(6, 3, 18));
		knob(m, 1.5, 3.8, 3.0, gold, goldDark);
		band(m, 4, 20, 3.1, LEATHER_DARK);
		for (double s : new double[] { 21, 30 }) {
			band(m, s, s + 1.5, 3.2, gold);
		}
		collar(m, 34, 64, 4.2, 3.8, goldDark);
		// the front bit: a huge crescent, gold along the whole edge
		double[][] front = { { 37, 3 }, { 30, 12 }, { 16, 27 }, { 26, 30 }, { 38, 33 }, { 50, 34 }, { 62, 33 }, { 74, 30 }, { 82, 25 }, { 70, 12 },
				{ 60, 3 } };
		plateSW(m, front, new double[] { 2.8, 1.8, 0, 0, 0, 0, 0, 0, 0, 1.8, 2.8 }, new double[] { 49, 16 }, 2.8, blackLight, goldBright, black);
		// the back bit, smaller, swept the other way
		double[][] back = { { 39, -3 }, { 34, -10 }, { 22, -22 }, { 36, -24 }, { 48, -25 }, { 60, -24 }, { 70, -20 }, { 62, -10 }, { 57, -3 } };
		plateSW(m, back, new double[] { 2.4, 1.6, 0, 0, 0, 0, 0, 1.6, 2.4 }, new double[] { 48, -13 }, 2.4, blackLight, goldBright, black);
		// gold rivets on the bits' cheeks
		gem(m, 49, 9, 2.0, 3.6, gold, gold);
		gem(m, 48, -8, 1.8, 3.2, gold, gold);
		// the crown on top: a gold circlet, five points, and the one red stone
		collar(m, 64, 66, 3.8, 6.4, goldDark);
		collar(m, 66, 70, 6.4, 6.4, gold);
		for (int k = 0; k < 5; k++) {
			double a = Math.PI * 2 * k / 5;
			tube(m, f(69, Math.cos(a) * 6, Math.sin(a) * 6), f(77, Math.cos(a) * 7, Math.sin(a) * 7), 1.9, 0, 4, a, goldBright, gold);
		}
		tube(m, f(68, 0, 0), f(73, 0, 0), 3.4, 2.4, 6, 0, ruby, ruby);
		tube(m, f(73, 0, 0), f(77, 0, 0), 2.4, 0, 6, 0, rubyHot, ruby);
		return m;
	}

	/* ====================================================================== */
	/*                              30. LAST LIGHT                            */
	/* ====================================================================== */

	/**
	 * The last light there was before the Starless ate the sky, held in a sword: a split blade of white gold with the rift
	 * burning in the slot between its tines, and an eclipse for a guard - a black sun in a ring of fire.
	 */
	static ModelData lastLight() {
		ModelData m = new ModelData();
		int metal = hsl(9, 2, 88), metalDark = hsl(9, 3, 62), edge = hsl(9, 1, 120), gold = hsl(8, 4, 64), goldDark = hsl(8, 5, 44),
				black = violet(6, 1), rift = violet(100, 6), riftCore = violet(124, 2), corona = hsl(6, 5, 96), coronaHot = hsl(9, 2, 120);
		// a little sun for a pommel, a black grip bound in gold
		star(m, f(2, 0, 0), 4.4, 1.6, coronaHot, gold);
		tube(m, f(4, 0, 0), f(7, 0, 0), 3.0, 2.0, 6, 0, gold, goldDark);
		tube(m, f(7, 0, 0), f(36, 0, 0), 2.2, 2.4, 4, Math.PI / 4, black, black);
		for (double s : new double[] { 11, 16, 21, 26, 31 }) {
			band(m, s, s + 0.9, 2.65, gold);
		}
		// the eclipse: a black disc in a burning ring, a corona of rays flat in the blade's plane
		double es = 41;
		tube(m, f(es, 0, -2.2), f(es, 0, 2.2), 7.4, 7.4, 10, 0, black, black);
		tube(m, f(es, 0, -1.4), f(es, 0, 1.4), 9.6, 9.6, 10, 0.3, coronaHot, coronaHot);
		for (int k = 0; k < 14; k++) {
			double a = Math.PI * 2 * k / 14 + Math.PI / 14;
			double len = (k % 2 == 0 ? 19 : 14) * (Math.abs(Math.cos(a)) > 0.8 ? 0.7 : 1);
			tube(m, f(es + Math.cos(a) * 8.5, Math.sin(a) * 8.5, 0), f(es + Math.cos(a) * len, Math.sin(a) * len, 0), 2.0, 0, 4, a + Math.PI / 4,
					k % 2 == 0 ? coronaHot : corona, corona);
		}
		// the split blade: two tines meeting at the point, the rift between them
		for (int side = -1; side <= 1; side += 2) {
			double[][] tine = { { 48, 2.4 }, { 48, 8.0 }, { 70, 9.0 }, { 108, 8.2 }, { 132, 5.6 }, { 154, 0.4 }, { 130, 2.2 } };
			for (double[] p : tine) {
				p[1] *= side;
			}
			plateSW(m, tine, new double[] { 1.5, 0, 0, 0, 0, 0, 1.3 }, new double[] { 96, side * 4.6 }, 1.4, metal, edge, metalDark);
		}
		diamond(m, new double[][] { { 47, 2.2, 0.9 }, { 96, 2.4, 1.0 }, { 126, 1.8, 0.8 } }, 148, 0, riftCore, rift);
		collar(m, 44, 50, 3.6, 2.6, goldDark);
		gem(m, es, 0, 3.2, 3.8, riftCore, riftCore);
		return m;
	}

	/* ====================================================================== */
	/*                               THE LADDER                               */
	/* ====================================================================== */

	/** One rung: id, name, examine, cost, type (as players read it), the models and the base item's pose. */
	static final class Rung {
		final int id;
		final String name, examine, type;
		final int cost;
		final Weapon weapon;

		Rung(int id, String name, String type, int cost, String examine, Supplier<ModelData> frame, Supplier<ModelData> invFrame, Pose pose) {
			this.id = id;
			this.name = name;
			this.type = type;
			this.cost = cost;
			this.examine = examine;
			this.weapon = new Weapon(name, frame, invFrame, pose);
		}

		Rung(int id, String name, String type, int cost, String examine, Supplier<ModelData> frame, Pose pose) {
			this(id, name, type, cost, examine, frame, frame, pose);
		}
	}

	static final Rung[] LADDER = {
			new Rung(VoidConstants.LADDER[0], "Netwright's gutting dagger", "dagger", 30, "Bram's fish knife. It's gutted more trout than enemies. So far.",
					LadderWeapons::guttingDagger, DAGGER),
			new Rung(VoidConstants.LADDER[1], "Hob's felling axe", "axe", 60, "Hob swears by it. Hob also swears at it.",
					LadderWeapons::fellingAxe, BATTLEAXE),
			new Rung(VoidConstants.LADDER[2], "Pilgrim's staff", "staff", 80, "It's walked every road on Nythera. The ribbon was blue once.",
					LadderWeapons::pilgrimStaff, STAFF),
			new Rung(VoidConstants.LADDER[3], "Militia spear", "spear", 150, "Gatehold's militia carry these. The tassel is in the watch's colours.",
					LadderWeapons::militiaSpear, SPEAR),
			new Rung(VoidConstants.LADDER[4], "Wolfbone shortbow", "shortbow", 120, "Its nocks are carved from a Hollowed wolf. The wolf didn't need them any more.",
					() -> wolfboneBow(1.0), () -> wolfboneBow(2.4), SHORTBOW),
			new Rung(VoidConstants.LADDER[5], "Watchman's sword", "sword", 400, "Standard issue for the night watch. Somebody polishes it every night.",
					LadderWeapons::watchmanSword, SWORD),
			new Rung(VoidConstants.LADDER[6], "Steelcap mace", "mace", 700, "A sergeant's mace. The flanges are filed, not sharpened. It doesn't need sharpening.",
					LadderWeapons::steelcapMace, MACE),
			new Rung(VoidConstants.LADDER[7], "Smuggler's scimitar", "scimitar", 1500, "Sly Nettle swears it fell off a ship. Several ships, by the nicks in it.",
					LadderWeapons::smugglerScimitar, SCIMITAR),
			new Rung(VoidConstants.LADDER[8], "Barrowstone warhammer", "warhammer", 2500, "A grave-marker on a stick. The rune on it still remembers whose grave.",
					LadderWeapons::barrowstoneHammer, WARHAMMER),
			new Rung(VoidConstants.LADDER[9], "Silkwood wand", "wand", 3000, "Magic wood, wound in spider silk. Something in the crystal is listening.",
					LadderWeapons::silkwoodWand, WAND),
			new Rung(VoidConstants.LADDER[10], "Gatehold longsword", "longsword", 15000, "The watch's longsword. The star on the pommel is the one over the Gate.",
					LadderWeapons::gateholdLongsword, LONGSWORD),
			new Rung(VoidConstants.LADDER[11], "Warden's halberd", "halberd", 22000, "Carried on the wall at night. The pennant is for the people behind it.",
					LadderWeapons::wardenHalberd, HALBERD),
			new Rung(VoidConstants.LADDER[12], "Warden's longbow", "longbow", 12000, "The wall's bow. It's dark yew, so they can't see it coming either.",
					() -> wardenLongbow(1.0), () -> wardenLongbow(1.3), LONGBOW),
			new Rung(VoidConstants.LADDER[13], "Scarglass dagger", "dagger", 18000, "Knapped from the Scar's black glass. It's sharper than it has any right to be.",
					LadderWeapons::scarglassDagger, DAGGER),
			new Rung(VoidConstants.LADDER[14], "Oathsworn battleaxe", "battleaxe", 40000, "The Thane's guard swore to hold this for ever. Ever ended.",
					LadderWeapons::oathswornAxe, BATTLEAXE),
			new Rung(VoidConstants.LADDER[15], "Frostfang spear", "spear", 35000, "Rimeclaw's fang on a pole. The cold comes up the haft into your hands.",
					LadderWeapons::frostfangSpear, SPEAR),
			new Rung(VoidConstants.LADDER[16], "Gravewarden 2h sword", "2h sword", 60000, "A greatsword for guarding the dead. Its edges glow like a grave at night.",
					LadderWeapons::gravewardenSword, TWO_HANDED),
			new Rung(VoidConstants.LADDER[17], "Hollowheart shortbow", "shortbow", 50000, "Ribs, and a heart that still beats when you draw it. Best not to ask whose.",
					() -> hollowheartBow(1.0), () -> hollowheartBow(2.4), SHORTBOW),
			new Rung(VoidConstants.LADDER[18], "Wand of the Scar", "wand", 70000, "A Silkwood wand that touched a waystone's rune. It's been looking at you since.",
					LadderWeapons::scarWand, WAND),
			new Rung(VoidConstants.LADDER[19], "Gravestar warhammer", "warhammer", 90000, "Grave-stone faced in starmetal. It rings for a long time after every blow.",
					LadderWeapons::gravestarHammer, WARHAMMER),
			new Rung(VoidConstants.LADDER[20], "Riftglass halberd", "halberd", 140000, "Knapped from the rift's own edge. Through the seam you can see somewhere else.",
					LadderWeapons::riftglassHalberd, HALBERD),
			new Rung(VoidConstants.LADDER[21], "Rimeheart staff", "staff", 130000, "A frozen heart in a claw of ice. It never needs water runes; it's made of the stuff.",
					LadderWeapons::rimeheartStaff, STAFF),
			new Rung(VoidConstants.LADDER[22], "Nightglass scimitar", "scimitar", 220000, "Black glass from between the stars, curved like a new moon. Its edge never stops burning.",
					LadderWeapons::nightglassScimitar, SCIMITAR),
			new Rung(VoidConstants.LADDER[23], "Crownbreaker mace", "mace", 260000, "This broke the Hollow King's crown. The star inside it is still angry about that.",
					LadderWeapons::crownbreakerMace, MACE),
			new Rung(VoidConstants.LADDER[24], "Starless longsword", "longsword", 400000, "The Starless shed it like a scale. There are stars in the blade, and the eye is open.",
					LadderWeapons::starlessLongsword, LONGSWORD),
			new Rung(VoidConstants.LADDER[25], "Eventide longbow", "longbow", 300000, "Strung at dusk with the first starlight. Its arrows land after dark, wherever you are.",
					() -> eventideBow(1.0), () -> eventideBow(1.3), LONGBOW),
			new Rung(VoidConstants.LADDER[26], "Sunspear", "spear", 450000, "The sun, on a stick. The Starless has never come near anyone holding one.",
					LadderWeapons::sunspear, SPEAR),
			new Rung(VoidConstants.LADDER[27], "Staff of the first star", "staff", 500000, "The first star that ever burned over Nythera, kept in gold. It never needs fire or air runes.",
					LadderWeapons::firstStarStaff, STAFF),
			new Rung(VoidConstants.LADDER[28], "Kingsbane battleaxe", "battleaxe", 600000, "Forged to end a king. The stone in the crown is the colour of the job.",
					LadderWeapons::kingsbaneAxe, BATTLEAXE),
			new Rung(VoidConstants.LADDER[29], "Last Light 2h sword", "2h sword", 1000000, "The last light before the Starless ate the sky. It's still burning, and it remembers.",
					LadderWeapons::lastLight, TWO_HANDED), };

	/** Every rung's weapon, in ladder order. */
	static Weapon[] weapons() {
		Weapon[] w = new Weapon[LADDER.length];
		for (int i = 0; i < w.length; i++) {
			w[i] = LADDER[i].weapon;
		}
		return w;
	}
}
