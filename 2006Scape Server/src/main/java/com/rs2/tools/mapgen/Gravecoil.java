package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ModelData.hsl;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.rs2.tools.mapgen.CreatureRig.Bone;
import com.rs2.tools.mapgen.CreatureRig.Pose;
import com.rs2.tools.mapgen.CreatureRig.Q;

/**
 * The Gravecoil (combat 52): a barrow serpent of the Sunken Barrow, with its own hand-built model, skeleton and
 * animations. The second creature after the {@link Shardback}, built on the shared {@link CreatureRig}.
 *
 * <p>The barrow-folk sealed a serpent into each king's grave to guard the grave-goods. The Thane's curse would not let
 * his household die, and it did not let the serpents die either: centuries coiled in the dark on offerings and the
 * occasional robber have made them huge, and they still wear the kings' bronze arm-rings they slid into as hatchlings,
 * grown deep into their hides. Gravecoils hunt the mound at night, tasting the air for the warmth of the living.
 *
 * <p><b>Not placed anywhere.</b> This only defines it (model, frames, sequences, npc definition) at fixed ids clear of
 * everything the generators allocate. {@link #install} writes it into an already-generated cache;
 * {@code tools/regen-world.sh} runs it after the world generator and the Shardback.
 *
 * <h3>The body</h3> A serpent is one long chain, so the model is a single skin of eight-sided rings, one per joint
 * station, each ring on its own bone and the faces between rings stretching across the joint (no seams to open). The
 * root is the coil the neck rises from; one chain runs forward (six neck joints, the head, the jaw and a forked tongue,
 * with the hood on the fifth neck joint), the other runs back to the tail (seven joints). Every ring's belly is flat
 * and at y = 0 at rest, so a part of the body that only turns about the vertical (yaw) stays exactly on the ground:
 * the lying body is posed with yaw alone, and the reared neck with pitch. The walk is a slither that follows its own
 * track: each body joint is laid on a fixed sine path in the ground's frame, so nothing slips sideways.
 *
 * <p>Coordinates: model units (128 = a tile), y down (negative is up), it faces -z, ground at y = 0.
 */
public final class Gravecoil {

	private Gravecoil() {
	}

	/* ====================================================================== */
	/*                                   IDS                                  */
	/* ====================================================================== */

	/* Next to the Shardback's (npc 4000, model 16000, frame file 1100 with frames 34000-34049, seqs 4100-4104). */
	public static final int NPC_ID = com.rs2.game.content.voidrealm.VoidConstants.GRAVECOIL;
	public static final int MODEL_ID = 16001;
	public static final int FRAME_FILE = 1101;
	public static final int FRAME_BASE = 34100;
	public static final int SEQ_STAND = 4110, SEQ_WALK = 4111, SEQ_ATTACK = 4112, SEQ_DEATH = 4113, SEQ_BLOCK = 4114;

	public static final String NAME = "Gravecoil";
	public static final String EXAMINE = "Sealed in with a king to guard his gold. Nobody ever told it to stop.";
	public static final String LORE = "The barrow-folk sealed a serpent into every king's grave to guard the grave-goods. The Thane's curse would "
			+ "not let his household die, and it did not let the serpents die either: centuries in the dark on offerings and the odd robber "
			+ "have made them huge, and they still wear the kings' bronze arm-rings they slid into as hatchlings, grown deep into the hide. "
			+ "Gravecoils hunt the mound by night, tasting the air for the warmth of the living.";
	public static final int COMBAT = 52, HITPOINTS = 60, ATTACK = 48, STRENGTH = 50, DEFENCE = 44, MAX_HIT = 7, SIZE = 2;

	/* ====================================================================== */
	/*                                 THE RIG                                */
	/* ====================================================================== */

	static final int ROOT_L = 1, NECK_L = 2, HEAD_L = 8, JAW_L = 9, TONGUE_L = 10, HOOD_L = 11, BODY_L = 12;
	static final int NECKS = 6, BODIES = 7;

	/** The model is shifted this far along z, so the coiled stand sits in the middle of its 2x2. */
	static final double ZS = 34;

	/**
	 * The spine stations, head to tail: z, half-width, top and bottom half-height. One ring each; station 0 is the
	 * head's (where the skull meets the neck), 1-6 the neck (N6 to N1), 7 the root, 8-14 the body (B1 to B7).
	 */
	static final double[][] ST = { { -200, 21, 16, 15 }, { -174, 20, 16, 15 }, { -147, 19, 16, 15 }, { -118, 20, 17, 16 }, { -89, 22, 19, 18 },
			{ -59, 24, 21, 20 }, { -30, 27, 23, 21 }, { 0, 30, 26, 22 }, { 38, 29, 25, 22 }, { 76, 27, 23, 20 }, { 112, 24, 21, 18 },
			{ 146, 21, 18, 16 }, { 178, 17, 15, 13 }, { 206, 13, 11, 10 }, { 230, 9, 8, 7 } };
	static final double TAIL_TIP = 260;
	static final int ROOT_STATION = 7;

	static final CreatureRig RIG = new CreatureRig();
	static final Bone ROOT, HEAD, JAW, TONGUE, HOOD;
	static final Bone[] NECK = new Bone[NECKS], BODY = new Bone[BODIES];
	/** The bone each station's ring is on. */
	static final Bone[] STATION_BONE = new Bone[ST.length];

	static double z(double z) {
		return z + ZS;
	}

	/** Height of a station's ring centre (its belly flat on the ground). */
	static double cy(int station) {
		return -0.924 * ST[station][3];
	}

	static double[] pivot(int station) {
		return new double[] { 0, cy(station), z(ST[station][0]) };
	}

	static {
		ROOT = RIG.bone("root", ROOT_L, null, pivot(ROOT_STATION));
		Bone parent = ROOT;
		for (int i = 0; i < NECKS; i++) { // N1 (station 6) up to N6 (station 1)
			NECK[i] = parent = RIG.bone("n" + (i + 1), NECK_L + i, parent, pivot(ROOT_STATION - 1 - i));
		}
		HEAD = RIG.bone("head", HEAD_L, NECK[5], pivot(0));
		JAW = RIG.bone("jaw", JAW_L, HEAD, 0, -10, z(-212));
		TONGUE = RIG.bone("tongue", TONGUE_L, HEAD, 0, -15, z(-238));
		TONGUE.moves = true;
		HOOD = RIG.bone("hood", HOOD_L, NECK[4], pivot(2));
		HOOD.scales = true;
		parent = ROOT;
		for (int i = 0; i < BODIES; i++) {
			BODY[i] = parent = RIG.bone("b" + (i + 1), BODY_L + i, parent, pivot(ROOT_STATION + 1 + i));
		}
		STATION_BONE[0] = HEAD;
		for (int i = 0; i < NECKS; i++) {
			STATION_BONE[ROOT_STATION - 1 - i] = NECK[i];
		}
		STATION_BONE[ROOT_STATION] = ROOT;
		for (int i = 0; i < BODIES; i++) {
			STATION_BONE[ROOT_STATION + 1 + i] = BODY[i];
		}
		List<Bone> order = new ArrayList<>(List.of(TONGUE, JAW, HEAD, NECK[5], HOOD));
		for (int i = 4; i >= 0; i--) {
			order.add(NECK[i]);
		}
		for (int i = BODIES - 1; i >= 0; i--) {
			order.add(BODY[i]);
		}
		order.add(ROOT);
		RIG.build(order);
	}

	/* ====================================================================== */
	/*                                THE MODEL                               */
	/* ====================================================================== */

	/*
	 * The barrow's colours: a dull grave-moss hide, bone-white belly scutes and ridge, the kings' bronze gone green at
	 * the edges, and the barrow's ghost-light in its eyes and tongue.
	 */
	static final int HIDE = hsl(14, 2, 38), HIDE_2 = hsl(14, 2, 32), HIDE_TOP = hsl(15, 2, 27), HIDE_DARK = hsl(15, 2, 19),
			BELLY = hsl(9, 2, 84), BELLY_2 = hsl(9, 2, 72), BELLY_3 = hsl(9, 2, 60), BONE = hsl(9, 2, 92), BONE_DARK = hsl(9, 2, 70),
			BRONZE = hsl(7, 4, 46), BRONZE_DARK = hsl(7, 4, 32), VERDIGRIS = hsl(25, 2, 46), EYE = hsl(27, 6, 104), EYE_DARK = hsl(27, 5, 70),
			MOUTH = hsl(0, 4, 34), MOUTH_DARK = hsl(0, 4, 16), TONGUE_C = hsl(27, 4, 74), HOOD_IN = hsl(9, 2, 64);

	/** Vertex ids of each station's ring and of the tail's tip, for the checks (filled by {@link #model()}). */
	static int[][] RINGS = new int[ST.length][];
	static int TIP;

	/** The Gravecoil, every vertex on its bone's label. */
	public static ModelData model() {
		ModelData m = new ModelData();
		body(m);
		head(m);
		hood(m);
		return m;
	}

	/** A station's ring: eight-sided, flat on top, flat (on the ground) underneath. */
	static int[] ring(ModelData m, int s) {
		m.skin = STATION_BONE[s].label;
		return Shardback.ring(m, cy(s), z(ST[s][0]), ST[s][1], ST[s][2], ST[s][3], 8, null);
	}

	/** The spine: rings from the skull back to the tail tip, bands of bronze, a ridge of bone. */
	static void body(ModelData m) {
		for (int s = 0; s < ST.length; s++) {
			RINGS[s] = ring(m, s);
		}
		for (int s = 0; s + 1 < ST.length; s++) {
			final int seg = s;
			Shardback.bridge(m, RINGS[s], RINGS[s + 1], (cy(s) + cy(s + 1)) / 2, (k, up) -> {
				boolean even = seg % 2 == 0;
				if (up > 0.9) {
					return even ? HIDE_DARK : HIDE_TOP; // the dorsal stripe, in blotches
				}
				if (up > 0.3) {
					return even ? HIDE_TOP : HIDE_2;
				}
				if (up > -0.3) {
					return even ? HIDE : HIDE_2;
				}
				if (up < -0.9) {
					return even ? BELLY : BELLY_2; // a scute per segment
				}
				return even ? BELLY_2 : BELLY_3;
			});
		}
		// the tail closes to a point, just off the ground
		int last = ST.length - 1;
		m.skin = BODY[BODIES - 1].label;
		TIP = m.vertices.size();
		Shardback.cap(m, RINGS[last], new double[] { 0, -4, z(TAIL_TIP) }, HIDE_2, new double[] { 0, cy(last), z(ST[last][0]) });
		// the throat behind the skull: a dark cap facing forward, seen when the jaw opens
		m.skin = HEAD.label;
		Shardback.cap(m, RINGS[0], new double[] { 0, cy(0), z(ST[0][0] - 2) }, MOUTH_DARK, new double[] { 0, cy(0), z(ST[0][0] + 20) });
		// the kings' arm-rings, grown into the hide
		for (int s : new int[] { 5, 7, 9, 11, 13 }) {
			band(m, s, s >= 11 ? 6 : 10);
		}
		// a ridge of bone knuckles down the spine between the rings
		for (int s : new int[] { 4, 6, 8, 10, 12 }) {
			m.skin = STATION_BONE[s].label;
			double top = cy(s) - 0.924 * ST[s][2], sz = z(ST[s][0]), r = Math.max(4, ST[s][1] * 0.24);
			m.tube(new double[] { 0, top + 4, sz - 4 }, new double[] { 0, top - r * 1.9, sz + r * 1.6 }, r, 0, 3, Math.PI / 2, BONE, BONE_DARK);
		}
	}

	/** A bronze band round a station's ring: over the back and down the sides, open under the belly. */
	static void band(ModelData m, int s, double len) {
		m.skin = STATION_BONE[s].label;
		double cy = cy(s), sz = z(ST[s][0]), w = ST[s][1], top = ST[s][2], bot = ST[s][3];
		int[][] outer = new int[2][], inner = new int[2][];
		for (int e = 0; e < 2; e++) {
			double zz = sz + (e == 0 ? -len / 2 : len / 2);
			outer[e] = arc(m, cy, zz, w + 3.5, top + 3.5, bot + 3.5);
			inner[e] = arc(m, cy, zz, w - 1.5, top - 1.5, bot - 1.5);
		}
		double[] mid = { 0, cy, sz };
		// sectors 7, 0, 1, 2, 3: right side, over the top, left side (k to k + 1)
		for (int k : new int[] { 7, 0, 1, 2, 3 }) {
			int k2 = (k + 1) % 8;
			int col = k == 1 ? BRONZE : k == 7 || k == 3 ? VERDIGRIS : BRONZE_DARK;
			m.quadOut(outer[0][k], outer[0][k2], outer[1][k2], outer[1][k], col, mid);
			m.quadOut(outer[0][k], outer[0][k2], inner[0][k2], inner[0][k], BRONZE_DARK, new double[] { 0, cy, sz + 40 });
			m.quadOut(outer[1][k], outer[1][k2], inner[1][k2], inner[1][k], BRONZE_DARK, new double[] { 0, cy, sz - 40 });
		}
		// the band's two ends, low on either side
		for (int k : new int[] { 7, 4 }) {
			double[] ref = { k == 7 ? -60 : 60, cy + 30, sz };
			m.quadOut(outer[0][k], outer[1][k], inner[1][k], inner[0][k], BRONZE_DARK, ref);
		}
	}

	/** The upper part of a station-shaped ring (its vertices 7, 0, 1, 2, 3, 4: down to the lower sides, not the belly). */
	static int[] arc(ModelData m, double cy, double z, double w, double top, double bot) {
		int[] out = new int[8];
		for (int k : new int[] { 7, 0, 1, 2, 3, 4 }) {
			double a = Math.PI / 8 + k * Math.PI / 4, sn = Math.sin(a);
			out[k] = m.vertex(Math.cos(a) * w, cy - sn * (sn > 0 ? top : bot), z);
		}
		return out;
	}

	/** A closed six-sided ring across the head: two lower corners, two side points, two on top. */
	static int[] skullRing(ModelData m, double z, double w, double top, double bottom, boolean jaw) {
		double mid = jaw ? top + (bottom - top) * 0.55 : bottom + (top - bottom) * 0.6;
		double[][] p = jaw ? new double[][] { { w, top }, { 0.9 * w, mid }, { 0.45 * w, bottom }, { -0.45 * w, bottom }, { -0.9 * w, mid }, { -w, top } }
				: new double[][] { { w, bottom }, { 0.96 * w, mid }, { 0.5 * w, top }, { -0.5 * w, top }, { -0.96 * w, mid }, { -w, bottom } };
		int[] r = new int[6];
		for (int i = 0; i < 6; i++) {
			r[i] = m.vertex(p[i][0], p[i][1], z(z));
		}
		return r;
	}

	/** A wedge of a viper's head, bone horns swept back over ghost-lit eyes; a hinged jaw with fangs and a forked tongue. */
	static void head(ModelData m) {
		m.skin = HEAD.label;
		// z, half-width, top, mouth line
		double[][] sk = { { -202, 20, -30, -12 }, { -226, 27, -33, -10 }, { -250, 25, -29, -9 }, { -270, 17, -23, -9 }, { -284, 9, -16, -9 } };
		int[][] rings = new int[sk.length][];
		for (int i = 0; i < sk.length; i++) {
			rings[i] = skullRing(m, sk[i][0], sk[i][1], sk[i][2], sk[i][3], false);
		}
		for (int i = 0; i + 1 < sk.length; i++) {
			double[] ref = { 0, (sk[i][2] + sk[i][3]) / 2, z((sk[i][0] + sk[i + 1][0]) / 2) };
			for (int k = 0; k < 6; k++) {
				int k2 = (k + 1) % 6;
				int col = k == 5 ? MOUTH : k == 2 ? (i % 2 == 0 ? HIDE_DARK : HIDE_TOP) : k == 1 || k == 3 ? HIDE_TOP : HIDE;
				m.quadOut(rings[i][k], rings[i][k2], rings[i + 1][k2], rings[i + 1][k], col, ref);
			}
		}
		fan(m, rings[0], HIDE_2, new double[] { 0, -20, z(-230) });
		fan(m, rings[sk.length - 1], HIDE_2, new double[] { 0, -16, z(-262) });
		for (int s = -1; s <= 1; s += 2) {
			VoidEffects.gem(m, s * 25, -23, z(-240), 5.5, 5, 4, Math.PI / 4, EYE, EYE_DARK); // the barrow's ghost-light
			m.tube(new double[] { s * 15, -29, z(-246) }, new double[] { s * 26, -48, z(-214) }, 6, 0, 4, Math.PI / 4, BONE, BONE_DARK); // horns
			m.tube(new double[] { s * 9, -11, z(-272) }, new double[] { s * 8, -4, z(-276) }, 2.8, 0, 3, 0, BONE, BONE); // fangs
		}
		m.skin = JAW.label;
		double[][] jw = { { -206, 18, -11, -1 }, { -230, 24, -9, 0 }, { -254, 21, -8, 0 }, { -272, 13, -8, -2 }, { -283, 7, -8, -4 } };
		int[][] jr = new int[jw.length][];
		for (int i = 0; i < jw.length; i++) {
			jr[i] = skullRing(m, jw[i][0], jw[i][1], jw[i][2], jw[i][3], true);
		}
		for (int i = 0; i + 1 < jw.length; i++) {
			double[] ref = { 0, (jw[i][2] + jw[i][3]) / 2, z((jw[i][0] + jw[i + 1][0]) / 2) };
			for (int k = 0; k < 6; k++) {
				int k2 = (k + 1) % 6;
				int col = k == 5 ? MOUTH : k == 2 ? BELLY : k == 1 || k == 3 ? BELLY_2 : HIDE_2;
				m.quadOut(jr[i][k], jr[i][k2], jr[i + 1][k2], jr[i + 1][k], col, ref);
			}
		}
		fan(m, jr[0], MOUTH_DARK, new double[] { 0, -5, z(-230) });
		fan(m, jr[jw.length - 1], HIDE_2, new double[] { 0, -5, z(-262) });
		m.skin = TONGUE.label; // hidden in the skull at rest; frames slide it out through the lips
		m.tube(new double[] { 0, -15, z(-238) }, new double[] { 0, -15, z(-268) }, 2.6, 1.8, 4, Math.PI / 4, TONGUE_C, TONGUE_C);
		for (int s = -1; s <= 1; s += 2) {
			m.tube(new double[] { 0, -15, z(-266) }, new double[] { s * 5, -15, z(-282) }, 1.8, 0, 3, 0, TONGUE_C, TONGUE_C);
		}
	}

	/** Closes a ring with a fan from its first vertex, each face turned away from {@code inside}. */
	static void fan(ModelData m, int[] ring, int color, double[] inside) {
		for (int k = 1; k + 1 < ring.length; k++) {
			m.triOut(ring[0], ring[k], ring[k + 1], color, inside);
		}
	}

	/** The hood: two flat fans either side of the upper neck, pale and ribbed in front, dark with a skull's eyes behind. */
	static void hood(ModelData m) {
		m.skin = HOOD.label;
		double[][] outline = { { 13, -114 }, { 28, -110 }, { 46, -122 }, { 60, -146 }, { 64, -170 }, { 55, -188 }, { 37, -200 }, { 17, -206 }, { 12, -202 } };
		double yTop = -17, yBot = -11; // (lying flat at rest; reared, the top is its back and the bottom its front)
		for (int s = -1; s <= 1; s += 2) {
			int n = outline.length;
			int[] top = new int[n], bot = new int[n];
			for (int i = 0; i < n; i++) {
				top[i] = m.vertex(s * outline[i][0], yTop, z(outline[i][1]));
				bot[i] = m.vertex(s * outline[i][0], yBot, z(outline[i][1]));
			}
			int ct = m.vertex(s * 30, yTop - 1, z(-160)), cb = m.vertex(s * 30, yBot + 1, z(-160));
			double[] in = { s * 30, (yTop + yBot) / 2, z(-160) };
			for (int i = 0; i + 1 < n; i++) {
				boolean eye = i == 3 || i == 4; // two pale spots on its back, like a skull's eyes
				m.triOut(ct, top[i], top[i + 1], eye ? BONE_DARK : HIDE_DARK, in);
				m.triOut(cb, bot[i], bot[i + 1], i % 2 == 0 ? HOOD_IN : BELLY_3, in); // ribs
				m.quadOut(top[i], top[i + 1], bot[i + 1], bot[i], HIDE_DARK, in);
			}
			m.triOut(ct, top[n - 1], top[0], HIDE_DARK, in);
			m.triOut(cb, bot[n - 1], bot[0], HOOD_IN, in);
			m.quadOut(top[n - 1], top[0], bot[0], bot[n - 1], HIDE_DARK, in);
		}
	}

	/* ====================================================================== */
	/*                                  POSES                                 */
	/* ====================================================================== */

	/** The body's resting curl: yaw at the root and each body joint (degrees). Yaw alone keeps it on the ground. */
	static final double[] CURL = { 0, 8, 14, 18, 20, 20, 18, 14 };

	static Pose body(Pose p, double rootYaw, double[] yaws) {
		p.rot(ROOT, 0, rootYaw, 0);
		for (int i = 0; i < BODIES; i++) {
			p.rot(BODY[i], 0, yaws[i], 0);
		}
		return p;
	}

	static Pose curl(Pose p, double tailFlick) {
		double[] y = new double[BODIES];
		for (int i = 0; i < BODIES; i++) {
			y[i] = CURL[i + 1] + (i >= 5 ? tailFlick * (i - 4) : 0);
		}
		return body(p, CURL[0], y);
	}

	/** The neck: pitch (negative rears it up) and yaw at each of the six joints, base first. */
	static Pose neck(Pose p, double[] pitch, double[] yaw) {
		for (int i = 0; i < NECKS; i++) {
			p.rot(NECK[i], pitch[i], yaw[i], 0);
		}
		return p;
	}

	/** The head: its pitch, yaw and roll at the top of the neck; the jaw opens by {@code jaw} degrees. */
	static Pose head(Pose p, double pitch, double yaw, double roll, double jaw) {
		p.rot(HEAD, pitch, yaw, roll);
		if (jaw != 0) {
			p.rot(JAW, jaw, 0, 0);
		}
		return p;
	}

	static Pose hood(Pose p, double spread) {
		return p.scale(HOOD, spread, 1, 1);
	}

	/** The tongue: 0 in, 1 all the way out (it slides forward and down to the lips). */
	static Pose tongue(Pose p, double out) {
		if (out > 0) {
			p.move(TONGUE, 0, 5 * Math.min(1, out * 2), -44 * out);
		}
		return p;
	}

	static double sum(double[] a) {
		double s = 0;
		for (double x : a) {
			s += x;
		}
		return s;
	}

	static double wave(double t) {
		return Math.sin(t * Math.PI * 2);
	}

	/** The reared stance: the neck rises in an S from the coil and the head is held level. */
	static final double[] REAR = { -14, -26, -30, -22, 20, 28 };

	/**
	 * Stand: a cobra's slow sway, its head held still while the neck weaves beneath it, the hood breathing, the
	 * tongue tasting the air twice a loop, the tail tip twitching on the ground.
	 */
	static List<Pose> stand() {
		List<Pose> out = new ArrayList<>();
		int n = 20;
		for (int i = 0; i < n; i++) {
			double t = (double) i / n;
			double sway = wave(t), bob = wave(2 * t + 0.1);
			Pose p = curl(new Pose(5), 6 * wave(t + 0.3));
			double[] pitch = REAR.clone(), yaw = new double[NECKS];
			pitch[1] += 2.5 * bob;
			pitch[2] -= 2 * bob;
			yaw[0] = 6 * sway;
			yaw[1] = 4 * wave(t - 0.08);
			yaw[2] = -3 * wave(t - 0.16);
			yaw[3] = -4 * wave(t - 0.2);
			neck(p, pitch, yaw);
			double flick = t >= 0.2 && t < 0.35 ? 1 : t >= 0.65 && t < 0.8 ? 1 : 0;
			double tip = (int) Math.round(t * n) % 3 == 1 ? 0.7 : 1; // the tongue flickers as it tastes
			head(p, -sum(pitch) + 6 + 1.5 * bob, -3 * sway, 2 * wave(t + 0.25), flick > 0 ? 4 : 0);
			hood(p, 1 + 0.05 * bob);
			tongue(p, flick * tip);
			out.add(p);
		}
		return out;
	}

	/* ------------------------------------------------------------- walk */

	/** Slither: one cycle covers two tiles (60 client cycles), a wave of 256 units along a track it never leaves. */
	static final int WALK_FRAMES = 20, WALK_DELAY = 3;
	static final double WALK_SPEED = 128.0 / 30, TRACK_WAVE = 256, TRACK_AMP = 20;

	/** The track in the ground's frame: its x at ground z. */
	static double track(double gz) {
		return TRACK_AMP * Math.sin(2 * Math.PI * gz / TRACK_WAVE);
	}

	/** Where the root is on the ground at a time (client cycles into the cycle): it moves towards -z. */
	static double rootGroundZ(double cycles) {
		return -WALK_SPEED * cycles;
	}

	/** The point on the track a chord's length {@code len} further back (+z) from (x, z). */
	static double[] along(double x, double z, double len) {
		double lo = z, hi = z + len * 1.2 + 4;
		for (int it = 0; it < 60; it++) {
			double mid = (lo + hi) / 2;
			if (Math.hypot(track(mid) - x, mid - z) < len) {
				lo = mid;
			} else {
				hi = mid;
			}
		}
		return new double[] { track(lo), lo };
	}

	static List<Pose> walk() {
		List<Pose> out = new ArrayList<>();
		double[] len = new double[BODIES + 1];
		for (int i = 0; i <= BODIES; i++) {
			double from = ST[ROOT_STATION + i][0], to = i < BODIES ? ST[ROOT_STATION + i + 1][0] : TAIL_TIP;
			len[i] = to - from;
		}
		for (int f = 0; f < WALK_FRAMES; f++) {
			double t = (double) f / WALK_FRAMES, gz = rootGroundZ(f * WALK_DELAY);
			// lay the body on the track, joint by joint (ground frame)
			double[][] pts = new double[BODIES + 2][];
			pts[0] = new double[] { track(gz), gz };
			for (int i = 0; i <= BODIES; i++) {
				pts[i + 1] = along(pts[i][0], pts[i][1], len[i]);
			}
			// each joint aims at the track a segment further on from where the joint before it really is, in whole
			// angle steps, so the rounding never adds up along the body (compile() then checks it against the client)
			double[] heading = new double[BODIES + 1], at = pts[0].clone();
			double turned = 0;
			double[] yaws = new double[BODIES];
			for (int i = 0; i <= BODIES; i++) {
				double[] aim = along(at[0], at[1], len[i]);
				double want = Math.toDegrees(Math.atan2(aim[0] - at[0], aim[1] - at[1]));
				double step = Shardback.units(want - turned) * 360.0 / 256;
				turned += step;
				heading[i] = turned;
				if (i > 0) {
					yaws[i - 1] = step;
				}
				at = new double[] { at[0] + len[i] * Math.sin(Math.toRadians(turned)), at[1] + len[i] * Math.cos(Math.toRadians(turned)) };
			}
			Pose p = new Pose(WALK_DELAY);
			body(p, heading[0], yaws);
			p.move(ROOT, pts[0][0], 0, 0);
			// the reared neck keeps its head on the way ahead while the coil swings beneath it; the hood is folded
			double sway = wave(t * 2 + 0.15);
			double[] pitch = { -10, -18, -24, -18, -8, 0 }, yaw = new double[NECKS];
			pitch[2] += 2 * wave(t * 4);
			yaw[0] = -heading[0] * 0.7;
			yaw[1] = -heading[0] * 0.3 + 4 * sway;
			yaw[2] = -3 * sway;
			neck(p, pitch, yaw);
			double flick = t >= 0.4 && t < 0.55 ? 1 : 0;
			head(p, -sum(pitch) + 8 - 2 * wave(t * 4), -2 * sway, 0, 0);
			hood(p, 0.42);
			tongue(p, flick);
			out.add(p);
		}
		return out;
	}

	/* ------------------------------------------------------------- attack */

	/**
	 * Attack: it draws its head back into a tight S with the hood flared and the jaw cracking open, holds, then
	 * strikes - the neck uncoils, the head drives forward and down, the jaw gapes and slams shut on the bite - and
	 * whips back up, overshooting before it settles. The coil slides forward a little behind the strike.
	 */
	static List<Pose> attack() {
		List<Pose> out = new ArrayList<>();
		// delay | neck pitch x6 | neck yaw (base) | head tilt (from level), jaw | hood | root slide z | tail lash
		double[][] k = { { 4, -14, -26, -30, -22, 20, 28, 0, 6, 0, 1.0, 0, 0 }, // ready
				{ 5, -10, -30, -38, -30, 24, 34, -4, 0, 10, 1.15, 4, -4 }, // drawing back
				{ 6, -6, -32, -46, -36, 30, 40, -6, -6, 22, 1.3, 8, -8 }, // coiled: the hood wide, the jaw cracking
				{ 2, -24, -16, -8, -2, 6, 10, -2, 6, 46, 1.3, -4, 6 }, // the strike: uncoiling, jaw gaping
				{ 2, -30, -4, 2, 2, 2, 4, 0, 14, 4, 1.35, -6, 10 }, // the bite: full reach, the jaw slams shut
				{ 4, -29, -5, 2, 3, 3, 3, 4, 12, 2, 1.3, -6, 6 }, // it holds on, worrying
				{ 4, -16, -30, -32, -22, 14, 20, -2, 4, 8, 1.2, -3, -4 }, // whips back up
				{ 5, -16, -28, -34, -26, 18, 30, 0, 2, 2, 1.1, -2, 0 }, // overshoots back
				{ 6, -14, -26, -30, -22, 20, 28, 0, 6, 0, 1.0, 0, 0 } }; // ready again
		for (double[] f : k) {
			Pose p = new Pose((int) f[0]);
			double[] pitch = { f[1], f[2], f[3], f[4], f[5], f[6] }, yaw = new double[NECKS];
			yaw[0] = f[7];
			yaw[2] = -f[7] * 0.5;
			double[] tail = new double[BODIES];
			for (int i = 0; i < BODIES; i++) {
				tail[i] = CURL[i + 1] + (i >= 3 ? f[12] * (i - 2) * 0.5 : 0);
			}
			body(p, CURL[0], tail);
			if (f[11] != 0) {
				p.move(ROOT, 0, 0, f[11]);
			}
			neck(p, pitch, yaw);
			head(p, -sum(pitch) + f[8], -f[7] * 0.5, 0, f[9]);
			hood(p, f[10]);
			out.add(p);
		}
		return out;
	}

	/* ------------------------------------------------------------- block */

	/** Block: it recoils from the blow, head jerked up and back, hissing with the hood flared, then settles. */
	static List<Pose> block() {
		List<Pose> out = new ArrayList<>();
		double[][] k = { { 3, -18, -32, -34, -24, 12, 20, 6, -8, 22, 1.25, 6 }, { 4, -20, -34, -38, -28, 8, 16, 9, -12, 28, 1.32, 9 },
				{ 4, -16, -29, -32, -24, 16, 26, 4, -2, 10, 1.15, 4 }, { 4, -14, -26, -30, -22, 20, 28, 0, 6, 0, 1.0, 0 } };
		for (double[] f : k) {
			Pose p = new Pose((int) f[0]);
			curl(p, -f[11] * 0.6);
			if (f[11] != 0) {
				p.move(ROOT, 0, 0, f[11]);
			}
			double[] pitch = { f[1], f[2], f[3], f[4], f[5], f[6] }, yaw = new double[NECKS];
			yaw[1] = f[7];
			neck(p, pitch, yaw);
			head(p, -sum(pitch) + f[8], -f[7], f[7] * 0.5, f[9]);
			hood(p, f[10]);
			out.add(p);
		}
		return out;
	}

	/* ------------------------------------------------------------- death */

	/** The death's first frame with it lying on the ground (every part of it touching from here on). */
	static final int DEATH_DOWN = 5;

	/**
	 * Death: the killing blow rears it up as high as it goes, jaw wide; it wavers at the top, then topples over to its
	 * right, the neck coming down across the dirt; the impact slams the jaw shut, the body thrashes once, and it goes
	 * slack, its head rolled over, its jaw hanging open and its tongue out; a last curl of the tail, and it is still.
	 */
	static List<Pose> death() {
		List<Pose> out = new ArrayList<>();
		// delay | neck pitch x6 | neck yaw x6 | head tilt, yaw, roll, jaw | hood | tongue | tail flick | root yaw
		double[][] k = {
				{ 4, -22, -32, -28, -10, 8, 12, 0, 0, 0, 0, 0, 0, -18, 0, 0, 36, 1.3, 0, 4, 0 }, // the blow: it rears, head thrown back
				{ 6, -24, -36, -30, -14, 6, 10, 4, -4, -4, 0, 0, 0, -14, 8, -16, 40, 1.3, 0, -4, 0 }, // it wavers at the top
				{ 5, -18, -28, -22, -8, 6, 8, -14, -6, -6, -4, 0, 0, -6, 6, -14, 30, 1.1, 0, -8, 2 }, // tips over to its right
				{ 4, -11, -17, -12, -4, 4, 4, -24, -10, -6, -2, 0, 0, 0, 0, -10, 22, 0.8, 0, -4, 4 }, // falling
				{ 3, -4, -6, -4, -1, 0, 0, -28, -12, -6, 0, 4, 6, 0, 4, -6, 14, 0.6, 0, 4, 6 }, // nearly down
				{ 3, 0, 0, 0, 0, 0, 0, -30, -12, -6, 0, 6, 8, 0, 8, 0, 4, 0.45, 0, 10, 6 }, // the impact: down, the jaw slammed shut
				{ 3, 0, 0, 0, 0, 0, 0, -34, -16, -2, 6, 8, 10, 0, 12, -8, 12, 0.4, 0, -10, 2 }, // it thrashes once
				{ 4, 0, 0, 0, 0, 0, 0, -32, -14, -4, 4, 8, 10, 0, 12, -14, 16, 0.35, 0.6, 2, 4 }, // goes slack, the head rolls
				{ 5, 0, 0, 0, 0, 0, 0, -32, -14, -4, 4, 8, 10, 0, 12, -16, 18, 0.35, 0.8, 18, 4 }, // a last curl of the tail
				{ 6, 0, 0, 0, 0, 0, 0, -32, -14, -4, 4, 8, 10, 0, 12, -16, 18, 0.35, 0.8, 6, 4 }, // and it lets go
				{ 85, 0, 0, 0, 0, 0, 0, -32, -14, -4, 4, 8, 10, 0, 12, -16, 18, 0.35, 0.8, 4, 4 } }; // (held past the server's removal)
		for (double[] f : k) {
			Pose p = new Pose((int) f[0]);
			double[] y = new double[BODIES];
			for (int i = 0; i < BODIES; i++) {
				y[i] = CURL[i + 1] + (i >= 4 ? f[19] * (i - 3) * 0.5 : 0);
			}
			body(p, CURL[0] + f[20], y);
			double[] pitch = new double[NECKS], yaw = new double[NECKS];
			for (int i = 0; i < NECKS; i++) {
				pitch[i] = f[1 + i];
				yaw[i] = f[7 + i];
			}
			neck(p, pitch, yaw);
			head(p, -sum(pitch) + f[13], f[14], f[15], f[16]);
			hood(p, f[17]);
			tongue(p, f[18]);
			out.add(p);
		}
		return out;
	}

	/* ====================================================================== */
	/*                       COMPILING, WITH THE GROUND                       */
	/* ====================================================================== */

	/** The label groups that must rest on the ground: the lying body always, and everything once it has died. */
	static Map<String, int[]> bodyGroups() {
		Map<String, int[]> g = new LinkedHashMap<>();
		g.put("root", new int[] { ROOT_L });
		for (int i = 0; i < BODIES; i++) {
			g.put("b" + (i + 1), new int[] { BODY[i].label });
		}
		return g;
	}

	static Map<String, int[]> allGroups() {
		Map<String, int[]> g = new LinkedHashMap<>();
		for (int i = 0; i < NECKS; i++) {
			g.put("n" + (i + 1), new int[] { NECK[i].label });
		}
		g.put("head", new int[] { HEAD_L, JAW_L });
		g.putAll(bodyGroups());
		return g;
	}

	/** Which groups must touch the ground in a frame of a sequence. */
	static Map<String, int[]> contact(String seq, int frame, int frames) {
		if (seq.equals("death") && frame >= DEATH_DOWN) {
			return allGroups();
		}
		return bodyGroups();
	}

	/**
	 * Compiles a sequence. Every lying part is posed by yaw alone and already on the ground; for the death's lying
	 * frames, the head (the one part that rolls) is settled: its pitch and the top neck joint's are tried a step at a
	 * time, posed exactly as the client poses them, until the head rests on the ground without going into it.
	 */
	static List<FxAnim.Key> compile(String name, List<Pose> poses) {
		ModelData base = model();
		List<FxAnim.Key> out = new ArrayList<>();
		for (int i = 0; i < poses.size(); i++) {
			Q q = RIG.quantise(poses.get(i));
			if (name.equals("death") && contact(name, i, poses.size()).containsKey("head")) {
				settleHead(base, q);
			}
			if (name.equals("walk")) {
				followTrack(base, q, i);
			}
			out.add(RIG.key(q));
		}
		return out;
	}

	/**
	 * The walk against the client's own integer posing: from the root back, each body joint's yaw is tried a step
	 * either side and the one that puts the next ring (or the tail's tip) closest to the track is kept. A joint only
	 * moves what lies behind it, so one pass settles the lot.
	 */
	static void followTrack(ModelData base, Q q, int frame) {
		double gz = rootGroundZ(frame * WALK_DELAY);
		Bone[] chain = new Bone[BODIES + 1];
		chain[0] = ROOT;
		System.arraycopy(BODY, 0, chain, 1, BODIES);
		for (int i = 0; i < chain.length; i++) {
			int[] r = q.rot(chain[i]);
			int start = r[1], best = start;
			double bestErr = Double.MAX_VALUE;
			for (int d = -2; d <= 2; d++) {
				r[1] = start + d;
				ModelData m = RIG.pose(base, q);
				double err = i < BODIES ? offTrack(m, RINGS[ROOT_STATION + 1 + i], gz) : offTrack(m, new int[] { TIP }, gz);
				if (err < bestErr - 1e-9) {
					bestErr = err;
					best = r[1];
				}
			}
			r[1] = best;
		}
	}

	/** How far (across the track) the centre of some vertices is from the track, in the ground's frame. */
	static double offTrack(ModelData posed, int[] ids, double rootGz) {
		double x = 0, zz = 0;
		for (int v : ids) {
			x += posed.vertices.get(v)[0] / (double) ids.length;
			zz += posed.vertices.get(v)[2] / (double) ids.length;
		}
		return Math.abs(x - track(zz - z(0) + rootGz));
	}

	/** Nudges the head's pitch and roll (within a few steps) until its lowest point is on the ground. */
	static void settleHead(ModelData base, Q q) {
		int[] h = q.rot(HEAD).clone();
		int[] best = h;
		double bestCost = Double.MAX_VALUE;
		for (int dp = -10; dp <= 10; dp++) {
			for (int dr = -4; dr <= 4; dr++) {
				q.rot.put(HEAD, new int[] { h[0] + dp, h[1], h[2] + dr });
				ModelData m = RIG.pose(base, q);
				int low = CreatureRig.lowest(m, HEAD_L, JAW_L, TONGUE_L);
				double cost = (low > 0 ? 1000 * low : -low * 10) + Math.abs(dp) + Math.abs(dr) * 1.5;
				if (cost < bestCost) {
					bestCost = cost;
					best = q.rot.get(HEAD).clone();
				}
			}
		}
		q.rot.put(HEAD, best);
	}

	/**
	 * The walk's check that the body follows its track: the worst distance (units, across the track) of any lying
	 * ring's centre from the track, in the ground's frame. Zero for every other sequence.
	 */
	static double trackError(String seq, int frame, ModelData posed) {
		if (!seq.equals("walk")) {
			return 0;
		}
		double gz = rootGroundZ(frame * WALK_DELAY), worst = offTrack(posed, new int[] { TIP }, gz);
		for (int s = ROOT_STATION; s < ST.length; s++) {
			worst = Math.max(worst, offTrack(posed, RINGS[s], gz));
		}
		return worst;
	}

	/** Every sequence, in the order its frames are numbered from FRAME_BASE. */
	static Map<String, Integer> SEQ_IDS = new LinkedHashMap<>();
	static {
		SEQ_IDS.put("idle", SEQ_STAND);
		SEQ_IDS.put("walk", SEQ_WALK);
		SEQ_IDS.put("attack", SEQ_ATTACK);
		SEQ_IDS.put("death", SEQ_DEATH);
		SEQ_IDS.put("block", SEQ_BLOCK);
	}

	static Map<String, List<FxAnim.Key>> sequences() {
		Map<String, List<FxAnim.Key>> s = new LinkedHashMap<>();
		s.put("idle", compile("idle", stand()));
		s.put("walk", compile("walk", walk()));
		s.put("attack", compile("attack", attack()));
		s.put("death", compile("death", death()));
		s.put("block", compile("block", block()));
		return s;
	}

	/* ====================================================================== */
	/*                                THE CACHE                               */
	/* ====================================================================== */

	static ConfigDefinitions.Definition definition() {
		ConfigDefinitions.Definition d = new ConfigDefinitions.Definition();
		d.set(1, new byte[] { 1, (byte) (MODEL_ID >> 8), (byte) MODEL_ID });
		d.name(NAME).examine(EXAMINE);
		d.set(12, new byte[] { SIZE });
		d.shorts(13, SEQ_STAND).shorts(14, SEQ_WALK);
		d.npcActions(null, "Attack", null, null, null);
		d.shorts(95, COMBAT);
		return d;
	}

	static CreatureRig.Install spec() {
		CreatureRig.Install c = new CreatureRig.Install();
		c.name = NAME;
		c.npcId = NPC_ID;
		c.modelId = MODEL_ID;
		c.frameFile = FRAME_FILE;
		c.frameBase = FRAME_BASE;
		c.model = model();
		c.skins = RIG.skins;
		sequences().forEach((name, keys) -> c.seqs.put(SEQ_IDS.get(name), keys));
		c.definition = definition();
		return c;
	}

	public static void install(CacheEditor cache) throws java.io.IOException {
		CreatureRig.install(cache, spec());
	}

	/** {@code Gravecoil [cacheDir]}: installs it into a generated cache. */
	public static void main(String[] args) throws Exception {
		try (CacheEditor cache = new CacheEditor(Paths.get(args.length > 0 ? args[0] : "data/cache"))) {
			install(cache);
		}
	}
}
