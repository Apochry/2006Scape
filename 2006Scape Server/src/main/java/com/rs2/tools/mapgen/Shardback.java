package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ConfigDefinitions.voidRamp;
import static com.rs2.tools.mapgen.ModelData.hsl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The Shardback (combat 47): a crater beast of Nythera, with its own hand-built model, its own skeleton and its own
 * animations - the first creature in the cache whose frames were authored here rather than borrowed.
 *
 * <p>When the star came down on Nythera, the rock-bears denning on that hill were not killed but fused: their hides
 * went to slag and the star's glass grew up through their backs. What came out of the smoke are Shardbacks - half-blind,
 * glass-ridged, and so hungry for starmetal that Gatehold's miners won't work the crater rim without a watchman.
 *
 * <p><b>Not placed anywhere.</b> This only defines it (model, frames, sequences, npc definition), at fixed ids well
 * clear of everything the generators allocate, so a later change can spawn it. {@link #install} writes it into an
 * already-generated cache; {@code tools/regen-world.sh} runs it after the world generator.
 *
 * <h3>The rig</h3> Frames in the 317 format are flat lists of transforms on vertex-label groups, applied in skin-list
 * order. A hierarchy comes from the order: every joint's rotation is applied child-first about its <i>rest</i> pivot
 * (an origin slot with no labels, so its x/y/z are an absolute point), and then its parents' rotations carry it along.
 * Each leg is upper / lower / paw; the legs are posed by two-bone IK in the body's frame, so planted paws stay put on
 * the ground while the body shifts its weight over them, and the paws are counter-rotated to stay flat.
 *
 * <p>Coordinates: model units (128 = a tile), y down (negative is up), the beast faces -z, ground at y = 0.
 */
public final class Shardback {

	private Shardback() {
	}

	/* ====================================================================== */
	/*                                   IDS                                  */
	/* ====================================================================== */

	/*
	 * Fixed ids, well above everything the stock cache and the generators use (npcs end at 3890, models at 15404,
	 * frame files at 1043, frame ids at 33567, sequences at 3996). The gaps are filled with empty entries the client
	 * skips; install() refuses to run if anything else has reached these slots.
	 */
	public static final int NPC_ID = com.rs2.game.content.voidrealm.VoidConstants.SHARDBACK;
	public static final int MODEL_ID = 16000;
	public static final int FRAME_FILE = 1100;
	public static final int FRAME_BASE = 34000;
	public static final int SEQ_STAND = 4100, SEQ_WALK = 4101, SEQ_ATTACK = 4102, SEQ_DEATH = 4103, SEQ_BLOCK = 4104;

	public static final String NAME = "Shardback";
	public static final String EXAMINE = "Its back bristles with the glass the star left behind. So does its temper.";
	public static final int COMBAT = 47, HITPOINTS = 48, ATTACK = 36, STRENGTH = 42, DEFENCE = 40, MAX_HIT = 6, SIZE = 2;

	/* ====================================================================== */
	/*                                 THE RIG                                */
	/* ====================================================================== */

	static final int TORSO = 1, NECK = 2, HEAD = 3, JAW = 4, TAIL1 = 5, TAIL2 = 6;
	/** Legs: 0 fore-left (+x), 1 fore-right (-x), 2 hind-left, 3 hind-right. Labels upper 10+i, lower 14+i, paw 18+i. */
	static final int UPPER = 10, LOWER = 14, PAW = 18, SHARD = 22, SHARDS = 5;
	/** The glass on its flanks: carried by the body (it has no joint of its own), but measured apart from the hull. */
	static final int FLANK = 27;

	/** A joint: the labels it moves (its own and its children's), and where it turns in the rest pose. */
	static final class Bone {
		final int label;
		final double[] pivot;
		final List<Bone> kids = new ArrayList<>();
		int slot = -1; // its rotate (or scale) slot; the origin slot is slot - 1

		Bone(int label, Bone parent, double... pivot) {
			this.label = label;
			this.pivot = pivot;
			if (parent != null) {
				parent.kids.add(this);
			}
		}

		void labels(List<Integer> out) {
			out.add(label);
			for (Bone k : kids) {
				k.labels(out);
			}
		}
	}

	/** A leg: hip (or shoulder) pivot, rest knee and ankle, and which way the knee bends (+1 = towards +z, back). */
	static final class Leg {
		final int index, side, bend;
		final double[] hip, knee, ankle;
		final double l1, l2;
		Bone upper, lower, paw;

		Leg(int index, int side, double[] hip, double[] ankle, double l1, double l2, int bend) {
			this.index = index;
			this.side = side;
			this.hip = hip;
			this.ankle = ankle;
			this.l1 = l1;
			this.l2 = l2;
			this.bend = bend;
			double[] k = solveKnee(hip, ankle[1], ankle[2], l1, l2, bend);
			knee = new double[] { (hip[0] + ankle[0]) / 2, k[0], k[1] };
		}

		boolean fore() {
			return index < 2;
		}
	}

	static final Leg[] LEGS = new Leg[4];
	static final Bone ROOT, NECK_B, HEAD_B, JAW_B, TAIL1_B, TAIL2_B, FLANK_B;
	static final Bone[] SHARD_B = new Bone[SHARDS];
	/** Shard bases (x, y, z), heights and base radii, front to back. */
	static final double[][] SHARD_AT = { { 0, -152, -50, 58, 15 }, { 0, -154, -20, 74, 18 }, { 0, -146, 12, 60, 15 },
			{ 0, -137, 40, 44, 12 }, { 0, -126, 64, 30, 9 } };
	static final double[] SHARD_LEAN = { 6, -7, 5, -4, 3 };
	static final double[] ROOT_PIVOT = { 0, -92, 8 };
	static final AnimCache.SkinList SKINS;

	static {
		ROOT = new Bone(TORSO, null, ROOT_PIVOT);
		for (int i = 0; i < 4; i++) {
			int side = i % 2 == 0 ? 1 : -1;
			boolean fore = i < 2;
			double[] hip = fore ? new double[] { side * 54, -106, -38 } : new double[] { side * 46, -100, 56 };
			double[] ankle = fore ? new double[] { side * 56, -17, -42 } : new double[] { side * 48, -17, 64 };
			Leg l = LEGS[i] = new Leg(i, side, hip, ankle, fore ? 52 : 46, fore ? 52 : 54, fore ? 1 : -1);
			l.upper = new Bone(UPPER + i, ROOT, hip);
			l.lower = new Bone(LOWER + i, l.upper, l.knee);
			l.paw = new Bone(PAW + i, l.lower, ankle);
		}
		NECK_B = new Bone(NECK, ROOT, 0, -112, -64);
		HEAD_B = new Bone(HEAD, NECK_B, 0, -106, -94);
		JAW_B = new Bone(JAW, HEAD_B, 0, -96, -98);
		TAIL1_B = new Bone(TAIL1, ROOT, 0, -108, 84);
		TAIL2_B = new Bone(TAIL2, TAIL1_B, 0, -101, 110);
		FLANK_B = new Bone(FLANK, ROOT, 0, -130, 0); // no slot: it only moves with the body
		for (int i = 0; i < SHARDS; i++) {
			SHARD_B[i] = new Bone(SHARD + i, ROOT, SHARD_AT[i][0], SHARD_AT[i][1], SHARD_AT[i][2]);
		}
		// skin list: [origin, rotate] per joint, children before parents; shard [origin, scale]; root origin, rotate, translate
		List<Integer> types = new ArrayList<>();
		List<int[]> labels = new ArrayList<>();
		List<Bone> order = new ArrayList<>();
		for (Leg l : LEGS) {
			order.addAll(List.of(l.paw, l.lower, l.upper));
		}
		order.addAll(List.of(JAW_B, HEAD_B, NECK_B, TAIL2_B, TAIL1_B));
		for (Bone b : order) {
			b.slot = addPair(types, labels, b, 2);
		}
		for (Bone b : SHARD_B) {
			b.slot = addPair(types, labels, b, 3);
		}
		ROOT.slot = addPair(types, labels, ROOT, 2);
		types.add(1);
		labels.add(labelsOf(ROOT));
		SKINS = FxAnim.skinList(types.stream().mapToInt(Integer::intValue).toArray(), labels.toArray(new int[0][]));
	}

	private static int addPair(List<Integer> types, List<int[]> labels, Bone b, int type) {
		types.add(0);
		labels.add(new int[0]); // an origin with no labels: its x/y/z is the pivot itself
		types.add(type);
		labels.add(labelsOf(b));
		return types.size() - 1;
	}

	private static int[] labelsOf(Bone b) {
		List<Integer> l = new ArrayList<>();
		b.labels(l);
		return l.stream().mapToInt(Integer::intValue).sorted().toArray();
	}

	/** Two-bone IK in the y-z plane: the knee {y, z} for a hip and an ankle target, bending towards +z if bend > 0. */
	static double[] solveKnee(double[] hip, double ty, double tz, double l1, double l2, int bend) {
		double dy = ty - hip[1], dz = tz - hip[2];
		double d = Math.max(Math.abs(l1 - l2) + 1, Math.min(l1 + l2 - 0.5, Math.hypot(dy, dz)));
		double phi = Math.atan2(dz, dy);
		double alpha = Math.acos(Math.max(-1, Math.min(1, (l1 * l1 + d * d - l2 * l2) / (2 * l1 * d))));
		double a = phi + bend * alpha;
		return new double[] { hip[1] + l1 * Math.cos(a), hip[2] + l1 * Math.sin(a) };
	}

	/* ====================================================================== */
	/*                                THE MODEL                               */
	/* ====================================================================== */

	static final int HIDE_TOP = hsl(5, 1, 30), HIDE = hsl(5, 1, 41), HIDE_2 = hsl(5, 2, 35), HIDE_DARK = hsl(5, 1, 22),
			BELLY = hsl(6, 2, 52), GLASSED = voidRamp(34, 4), GLASSED_2 = voidRamp(44, 5), LEG = hsl(5, 1, 36), LEG_DARK = hsl(5, 1, 27),
			PAW_C = hsl(5, 1, 24), CLAW = voidRamp(24, 3), SHARD_C = voidRamp(70, 6), SHARD_LIGHT = voidRamp(98, 7),
			SHARD_DARK = voidRamp(50, 6), TUSK = voidRamp(104, 4), EYE = hsl(42, 3, 112), MOUTH = hsl(1, 5, 22),
			MOUTH_DARK = hsl(1, 4, 13);

	/** The Shardback, every vertex on its bone's label. */
	public static ModelData model() {
		ModelData m = new ModelData();
		Random r = new Random(0x5AD);
		torso(m, r);
		for (Leg l : LEGS) {
			leg(m, l);
		}
		head(m);
		tail(m);
		shards(m);
		return m;
	}

	/** A ring of {@code sides} vertices round the z axis at z, flat on top and bottom, jittered mirror-symmetrically. */
	static int[] ring(ModelData m, double cy, double z, double rx, double ryTop, double ryBot, int sides, double[] jitter) {
		int[] out = new int[sides];
		for (int k = 0; k < sides; k++) {
			double a = Math.PI / sides + k * 2 * Math.PI / sides;
			double s = Math.sin(a), j = jitter == null ? 1 : jitter[k];
			out[k] = m.vertex(Math.cos(a) * rx * j, cy - s * (s > 0 ? ryTop : ryBot) * j, z);
		}
		return out;
	}

	/** Jitter for an 8-sided ring, equal for each vertex and its mirror (k and 3 - k). */
	static double[] jitter8(Random r, double amount) {
		double[] j = new double[8];
		int[][] pairs = { { 0, 3 }, { 1, 2 }, { 4, 7 }, { 5, 6 } };
		for (int[] p : pairs) {
			j[p[0]] = j[p[1]] = 1 + (r.nextDouble() - 0.5) * 2 * amount;
		}
		return j;
	}

	interface Shade {
		int color(int k, double up);
	}

	/** Joins two rings with faces wound away from the axis between them; up = the face's upward-ness (-1..1). */
	static void bridge(ModelData m, int[] a, int[] b, double cy, Shade shade) {
		int n = a.length;
		for (int k = 0; k < n; k++) {
			int k2 = (k + 1) % n;
			double mid = Math.PI / n + (k + 0.5) * 2 * Math.PI / n;
			int[] va = m.vertices.get(a[k]), vb = m.vertices.get(b[k]);
			double[] ref = { 0, cy, (va[2] + vb[2]) / 2.0 };
			m.quadOut(a[k], a[k2], b[k2], b[k], shade.color(k, Math.sin(mid)), ref);
		}
	}

	static void cap(ModelData m, int[] ring, double[] tip, int color, double[] ref) {
		int t = m.vertex(tip[0], tip[1], tip[2]);
		for (int k = 0; k < ring.length; k++) {
			m.triOut(ring[k], ring[(k + 1) % ring.length], t, color, ref);
		}
	}

	/** The body: a barrel of slag-dark hide, highest over the shoulders, glassed violet along the spine. */
	static void torso(ModelData m, Random r) {
		m.skin = TORSO;
		// z, centre y, half-width, top, bottom
		double[][] st = { { -78, -108, 34, 28, 24 }, { -52, -110, 56, 48, 46 }, { -18, -110, 60, 50, 48 }, { 18, -106, 52, 40, 42 },
				{ 52, -102, 44, 34, 34 }, { 80, -100, 32, 24, 24 } };
		int[][] rings = new int[st.length][];
		for (int i = 0; i < st.length; i++) {
			rings[i] = ring(m, st[i][1], st[i][0], st[i][2], st[i][3], st[i][4], 8, i == 0 ? null : jitter8(r, 0.06));
		}
		for (int i = 0; i + 1 < st.length; i++) {
			final int seg = i;
			bridge(m, rings[i], rings[i + 1], (st[i][1] + st[i + 1][1]) / 2, (k, up) -> {
				if (up > 0.9) {
					return seg >= 1 && seg <= 3 ? (seg % 2 == 0 ? GLASSED : GLASSED_2) : HIDE_TOP; // the star's glass along the spine
				}
				if (up > 0.3) {
					return (k + seg) % 2 == 0 ? HIDE_TOP : HIDE_2;
				}
				if (up > -0.3) {
					return (k + seg) % 2 == 0 ? HIDE : HIDE_2;
				}
				return up < -0.9 ? BELLY : HIDE;
			});
		}
		cap(m, rings[0], new double[] { 0, -108, -86 }, HIDE_DARK, new double[] { 0, -108, -60 });
		cap(m, rings[st.length - 1], new double[] { 0, -100, 96 }, HIDE_2, new double[] { 0, -100, 60 });
		// glass growing out of the shoulders and flanks, leaning outward
		m.skin = FLANK;
		for (int s = -1; s <= 1; s += 2) {
			m.tube(new double[] { s * 48, -136, -30 }, new double[] { s * 80, -176, -16 }, 12, 0, 4, 0.4, SHARD_C, SHARD_C);
			m.tube(new double[] { s * 44, -128, 14 }, new double[] { s * 68, -156, 28 }, 9, 0, 4, 0.2, SHARD_LIGHT, SHARD_C);
			m.tube(new double[] { s * 38, -122, 46 }, new double[] { s * 54, -142, 56 }, 7, 0, 4, 0.1, SHARD_DARK, SHARD_C);
		}
	}

	/** A leg: a heavy upper limb, a knee knob, a lower limb and a broad paw with three glass claws. */
	static void leg(ModelData m, Leg l) {
		double x = l.hip[0], s = l.side;
		boolean fore = l.fore();
		double r1 = fore ? 27 : 27, r2 = fore ? 19 : 17, r3 = fore ? 16 : 13;
		m.skin = l.upper.label;
		double[] top = { x - s * 14, l.hip[1] - 12, l.hip[2] + (fore ? -2 : 4) }; // set into the body, so it lies flat on its side
		m.tube(top, l.knee, r1, r2, 6, 0.3, fore ? LEG : HIDE_2, LEG);
		m.skin = l.lower.label;
		// the knee knob: a short fat ring, so the joint never opens up when it bends
		double[] dir = { 0, l.ankle[1] - l.knee[1], l.ankle[2] - l.knee[2] };
		double len = Math.hypot(dir[1], dir[2]);
		double[] kTop = { l.knee[0], l.knee[1] - dir[1] / len * 9, l.knee[2] - dir[2] / len * 9 };
		double[] kBot = { l.knee[0], l.knee[1] + dir[1] / len * 9, l.knee[2] + dir[2] / len * 9 };
		m.tube(kTop, kBot, r2 + 2, r2 + 1, 6, 0, LEG_DARK, LEG_DARK);
		m.tube(kBot, new double[] { l.ankle[0], l.ankle[1] + 4, l.ankle[2] }, r2, r3, 6, 0.3, LEG, LEG_DARK);
		m.skin = l.paw.label;
		paw(m, l.ankle, fore ? 21 : 18, fore ? 16 : 14, fore ? 31 : 27);
	}

	/** A paw under the ankle: flat on the ground, sloping up to the ankle, toes forward (-z), heel back. */
	static void paw(ModelData m, double[] a, double halfW, double heel, double toe) {
		double x = a[0], z = a[2];
		int b0 = m.vertex(x - halfW, 0, z + heel), b1 = m.vertex(x + halfW, 0, z + heel), b2 = m.vertex(x + halfW, 0, z - toe),
				b3 = m.vertex(x - halfW, 0, z - toe);
		double tw = halfW * 0.72;
		int t0 = m.vertex(x - tw, a[1] - 4, z + heel * 0.6), t1 = m.vertex(x + tw, a[1] - 4, z + heel * 0.6),
				t2 = m.vertex(x + tw, -9, z - toe * 0.8), t3 = m.vertex(x - tw, -9, z - toe * 0.8);
		double[] c = { x, -7, z - toe * 0.2 };
		m.quadOut(b0, b1, b2, b3, PAW_C, c);
		m.quadOut(t0, t1, t2, t3, LEG_DARK, c);
		m.quadOut(b0, b1, t1, t0, PAW_C, c);
		m.quadOut(b1, b2, t2, t1, PAW_C, c);
		m.quadOut(b2, b3, t3, t2, LEG_DARK, c);
		m.quadOut(b3, b0, t0, t3, PAW_C, c);
		for (int k = -1; k <= 1; k++) {
			double cx = x + k * halfW * 0.62;
			m.tube(new double[] { cx, -5, z - toe + 4 }, new double[] { cx, -1, z - toe - 12 }, 4.5, 0, 4, Math.PI / 4, CLAW, CLAW);
		}
	}

	/** Neck, a blunt heavy skull with swept glass horns and two star-gold eyes, and a tusked lower jaw. */
	static void head(ModelData m) {
		m.skin = NECK;
		m.tube(new double[] { 0, -116, -56 }, new double[] { 0, -106, -100 }, 33, 27, 6, 0, HIDE_2, HIDE);
		m.skin = HEAD;
		double[][] st = { { -88, -110, 31, 25, 14 }, { -116, -108, 29, 21, 12 }, { -142, -103, 21, 14, 9 } };
		int[][] rings = new int[st.length][];
		for (int i = 0; i < st.length; i++) {
			rings[i] = ring(m, st[i][1], st[i][0], st[i][2], st[i][3], st[i][4], 6, null);
		}
		for (int i = 0; i + 1 < st.length; i++) {
			bridge(m, rings[i], rings[i + 1], (st[i][1] + st[i + 1][1]) / 2, (k, up) -> up > 0.6 ? HIDE_TOP : up < -0.6 ? MOUTH_DARK : HIDE);
		}
		cap(m, rings[0], new double[] { 0, -112, -80 }, HIDE_DARK, new double[] { 0, -108, -110 });
		cap(m, rings[st.length - 1], new double[] { 0, -101, -156 }, HIDE_DARK, new double[] { 0, -104, -130 });
		for (int s = -1; s <= 1; s += 2) {
			m.tube(new double[] { s * 15, -126, -112 }, new double[] { s * 30, -162, -74 }, 9, 0, 4, 0.5, SHARD_C, SHARD_LIGHT); // horns
			VoidEffects.gem(m, s * 26, -114, -122, 5, 4.5, 4, 0, EYE, EYE); // the star in its eyes
		}
		m.skin = JAW;
		double[][] js = { { -94, -92, 26, 4, 10 }, { -126, -89, 24, 4, 10 }, { -148, -92, 16, 3, 7 } };
		int[][] jr = new int[js.length][];
		for (int i = 0; i < js.length; i++) {
			jr[i] = ring(m, js[i][1], js[i][0], js[i][2], js[i][3], js[i][4], 6, null);
		}
		for (int i = 0; i + 1 < js.length; i++) {
			bridge(m, jr[i], jr[i + 1], (js[i][1] + js[i + 1][1]) / 2, (k, up) -> up > 0.6 ? MOUTH : up < -0.6 ? BELLY : HIDE_2);
		}
		cap(m, jr[0], new double[] { 0, -90, -90 }, HIDE_DARK, new double[] { 0, -90, -110 });
		cap(m, jr[js.length - 1], new double[] { 0, -92, -156 }, HIDE_2, new double[] { 0, -91, -136 });
		for (int s = -1; s <= 1; s += 2) { // glass tusks jutting up past the upper lip
			m.tube(new double[] { s * 18, -93, -136 }, new double[] { s * 25, -128, -146 }, 6.5, 0, 4, Math.PI / 4, TUSK, TUSK);
		}
	}

	static void tail(ModelData m) {
		m.skin = TAIL1;
		m.tube(new double[] { 0, -110, 76 }, new double[] { 0, -101, 112 }, 13, 9, 5, 0, HIDE_2, HIDE);
		m.skin = TAIL2;
		m.tube(new double[] { 0, -101, 110 }, new double[] { 0, -92, 128 }, 9, 6, 5, 0, HIDE, HIDE);
		m.tube(new double[] { 0, -92, 126 }, new double[] { 0, -84, 150 }, 7, 0, 4, 0.4, SHARD_C, SHARD_C); // a glass spike
	}

	/** The ridge of star-glass down its spine: the tallest over the shoulders, all raked back. */
	static void shards(ModelData m) {
		int[][] cols = { { SHARD_C, SHARD_LIGHT }, { SHARD_LIGHT, SHARD_C }, { SHARD_C, SHARD_DARK }, { SHARD_DARK, SHARD_C },
				{ SHARD_C, SHARD_C } };
		for (int i = 0; i < SHARDS; i++) {
			m.skin = SHARD + i;
			double[] b = SHARD_AT[i];
			double h = b[3], r = b[4];
			double[] base = { b[0], b[1] + 8, b[2] }, tip = { b[0] + SHARD_LEAN[i], b[1] - h, b[2] + h * 0.42 };
			m.tube(base, tip, r, 0, 4, 0.35 + i * 0.5, cols[i][0], cols[i][1]);
			if (i < 3) { // a smaller crystal growing beside each of the big ones
				double side = SHARD_LEAN[i] > 0 ? -1 : 1;
				m.tube(new double[] { base[0] + side * 14, base[1] + 4, base[2] + 6 },
						new double[] { base[0] + side * 30, b[1] - h * 0.45, b[2] + h * 0.3 }, r * 0.55, 0, 4, 0.8, SHARD_DARK, SHARD_C);
			}
		}
	}

	/* ====================================================================== */
	/*                                  POSES                                 */
	/* ====================================================================== */

	enum Ground {
		/** the feet are placed by IK: leave the height alone */
		FEET,
		/** push it up if anything is under the ground */
		CLAMP,
		/** rest it on the ground: its lowest point at y = 0 */
		CONTACT
	}

	/** One key pose. Angles in degrees: pitch (x, + = tip down / swing back), yaw (y), roll (z, + = top towards -x). */
	static final class Pose {
		final int delay;
		final Map<Bone, double[]> rot = new LinkedHashMap<>();
		final double[] rootRot = new double[3], rootT = new double[3];
		/** world ankle targets for IK legs; a leg with none keeps the angles set on it */
		final double[][] foot = new double[4][];
		final double[] pawTilt = new double[4];
		double bristle = 1;
		Ground ground = Ground.FEET;
		/** for CONTACT: how far above the ground its lowest point should be (a bounce) */
		double hover;
		/** legs that must lie on the ground (lower leg and paw touching it), solved from their explicit angles */
		final boolean[] rest = new boolean[4];
		/** the head (and the tail) must rest on the ground */
		boolean headDown, tailDown;

		Pose(int delay) {
			this.delay = delay;
			for (int i = 0; i < 4; i++) {
				foot[i] = LEGS[i].ankle.clone(); // planted where it stands
			}
		}

		Pose set(Bone b, double pitch, double yaw, double roll) {
			rot.put(b, new double[] { pitch, yaw, roll });
			return this;
		}

		Pose body(double pitch, double yaw, double roll, double tx, double ty, double tz) {
			rootRot[0] = pitch;
			rootRot[1] = yaw;
			rootRot[2] = roll;
			rootT[0] = tx;
			rootT[1] = ty;
			rootT[2] = tz;
			return this;
		}

		Pose foot(int leg, double x, double y, double z, double tilt) {
			foot[leg] = new double[] { x, y, z };
			pawTilt[leg] = tilt;
			return this;
		}

		/** Explicit leg angles (no IK): upper pitch / roll and lower pitch; the paw is kept level with the ground. */
		/** A leg that lies on the ground: these angles are where the search for the resting pose starts. */
		Pose rest(int leg, double upper, double upperRoll, double lower) {
			limb(leg, upper, upperRoll, lower);
			rest[leg] = true;
			return this;
		}

		Pose limb(int leg, double upper, double upperRoll, double lower) {
			foot[leg] = null;
			Leg l = LEGS[leg];
			set(l.upper, upper, 0, upperRoll);
			set(l.lower, lower, 0, 0);
			rot.remove(l.paw);
			return this;
		}

		/** Takes these joints' angles from a solved frame (as whole angle steps). */
		void keep(Solved s, Bone... bones) {
			for (Bone b : bones) {
				int[] a = s.rot.get(b);
				if (a != null) {
					set(b, a[0] * 360.0 / 256, a[1] * 360.0 / 256, a[2] * 360.0 / 256);
				}
			}
		}

		Pose head(double neck, double neckYaw, double head, double headYaw, double jaw) {
			set(NECK_B, neck, neckYaw, 0);
			set(HEAD_B, head, headYaw, 0);
			set(JAW_B, jaw, 0, 0);
			return this;
		}

		Pose tail(double pitch1, double yaw1, double pitch2, double yaw2) {
			set(TAIL1_B, pitch1, yaw1, 0);
			set(TAIL2_B, pitch2, yaw2, 0);
			return this;
		}
	}

	static int units(double deg) {
		int u = (int) Math.round(deg * 256 / 360.0);
		return ((u % 256) + 384) % 256 - 128; // -128..127; the client takes it & 0xff
	}

	static double rad(int units) {
		return (units & 0xff) * 8 * Math.PI * 2 / 2048;
	}

	/** The client's rotation (roll z, then pitch x, then yaw y) about p, in doubles. */
	static double[] rotate(double[] v, double[] p, int ax, int ay, int az) {
		double x = v[0] - p[0], y = v[1] - p[1], z = v[2] - p[2];
		if (az != 0) {
			double a = rad(az), s = Math.sin(a), c = Math.cos(a), t = y * s + x * c;
			y = y * c - x * s;
			x = t;
		}
		if (ax != 0) {
			double a = rad(ax), s = Math.sin(a), c = Math.cos(a), t = y * c - z * s;
			z = y * s + z * c;
			y = t;
		}
		if (ay != 0) {
			double a = rad(ay), s = Math.sin(a), c = Math.cos(a), t = z * s + x * c;
			z = z * c - x * s;
			x = t;
		}
		return new double[] { x + p[0], y + p[1], z + p[2] };
	}

	/** The inverse of {@link #rotate}. */
	static double[] unrotate(double[] v, double[] p, int ax, int ay, int az) {
		double[] r = rotate(v, p, 0, -ay, 0);
		r = rotate(r, p, -ax, 0, 0);
		return rotate(r, p, 0, 0, -az);
	}

	/** A pose resolved to the frame's integers: joint rotations (client units), body rotation and translation. */
	static final class Solved {
		final Map<Bone, int[]> rot = new LinkedHashMap<>();
		int[] rr, rt;
	}

	/** Resolves a pose: IK for the legs with targets, everything quantised to what a frame can hold. */
	static Solved solve(Pose p) {
		Solved s = new Solved();
		int[] rr = s.rr = new int[] { units(p.rootRot[0]), units(p.rootRot[1]), units(p.rootRot[2]) };
		int[] rt = s.rt = new int[] { (int) Math.round(p.rootT[0]), (int) Math.round(p.rootT[1]), (int) Math.round(p.rootT[2]) };
		p.rot.forEach((b, a) -> s.rot.put(b, new int[] { units(a[0]), units(a[1]), units(a[2]) }));
		for (Leg l : LEGS) {
			double[] w = p.foot[l.index];
			if (w == null) {
				if (!p.rot.containsKey(l.paw)) { // (one settled onto the ground has its paw placed already)
					int[] u = s.rot.get(l.upper), lo = s.rot.get(l.lower);
					setLeg(s, l, u[0], lo[0], u[2], 0);
				}
				continue;
			}
			// the target in the body's own (rest) frame
			double[] local = unrotate(new double[] { w[0] - rt[0], w[1] - rt[1], w[2] - rt[2] }, ROOT_PIVOT, rr[0], rr[1], rr[2]);
			double[] k = solveKnee(l.hip, local[1], local[2], l.l1, l.l2, l.bend);
			double up = Math.atan2(k[1] - l.hip[2], k[0] - l.hip[1]) - Math.atan2(l.knee[2] - l.hip[2], l.knee[1] - l.hip[1]);
			int uq = units(Math.toDegrees(up));
			double upQ = Math.toRadians(uq * 360.0 / 256);
			// re-derive the knee from the quantised angle, so the lower leg aims from where the knee really is
			double[] kq = rotate(l.knee, l.hip, uq, 0, 0);
			double low = Math.atan2(local[2] - kq[2], local[1] - kq[1]) - Math.atan2(l.ankle[2] - l.knee[2], l.ankle[1] - l.knee[1]) - upQ;
			int lq = units(Math.toDegrees(low));
			// roll the whole leg out or in so the paw lands at the target's x, too
			double drop = Math.max(40, local[1] - l.hip[1]);
			int roll = units(Math.toDegrees(Math.asin(Math.max(-0.5, Math.min(0.5, (local[0] - l.ankle[0]) / drop)))));
			setLeg(s, l, uq, lq, roll, units(p.pawTilt[l.index]));
		}
		return s;
	}

	/** Sets a leg's three joints; the paw counter-rotates against the leg and body so it stays level (plus its tilt). */
	static void setLeg(Solved s, Leg l, int upper, int lower, int roll, int tilt) {
		s.rot.put(l.upper, new int[] { upper, 0, roll });
		s.rot.put(l.lower, new int[] { lower, 0, 0 });
		s.rot.put(l.paw, new int[] { wrap(tilt - upper - lower - s.rr[0]), 0, 0 });
	}

	static int wrap(int u) {
		return ((u % 256) + 384) % 256 - 128;
	}

	/** The frame: [origin, rotate] for each joint that turns, children first, then the shards, then the body. */
	static FxAnim.Key key(Solved s, Pose p) {
		List<int[]> ops = new ArrayList<>();
		Bone[] slotted = new Bone[SKINS.types.length];
		for (Leg l : LEGS) {
			for (Bone b : new Bone[] { l.paw, l.lower, l.upper }) {
				slotted[b.slot] = b;
			}
		}
		for (Bone b : new Bone[] { JAW_B, HEAD_B, NECK_B, TAIL2_B, TAIL1_B }) {
			slotted[b.slot] = b;
		}
		for (int i = 0; i < slotted.length; i++) {
			Bone b = slotted[i];
			int[] a = b == null ? null : s.rot.get(b);
			if (a != null && (a[0] != 0 || a[1] != 0 || a[2] != 0)) {
				ops.add(origin(i - 1, b.pivot));
				ops.add(new int[] { i, a[0], a[1], a[2] });
			}
		}
		if (Math.abs(p.bristle - 1) > 0.01) {
			for (int i = 0; i < SHARDS; i++) {
				Bone b = SHARD_B[i];
				double g = 1 + (p.bristle - 1) * (1 - i * 0.12); // the big front shards flare the most
				ops.add(origin(b.slot - 1, b.pivot));
				ops.add(new int[] { b.slot, (int) Math.round(128 * (1 + (g - 1) * 0.4)), (int) Math.round(128 * g),
						(int) Math.round(128 * (1 + (g - 1) * 0.4)) });
			}
		}
		int[] rr = s.rr, rt = s.rt;
		if (rr[0] != 0 || rr[1] != 0 || rr[2] != 0) {
			ops.add(origin(ROOT.slot - 1, ROOT_PIVOT));
			ops.add(new int[] { ROOT.slot, rr[0], rr[1], rr[2] });
		}
		if (rt[0] != 0 || rt[1] != 0 || rt[2] != 0) {
			ops.add(new int[] { ROOT.slot + 1, rt[0], rt[1], rt[2] });
		}
		FxAnim.Key key = new FxAnim.Key(p.delay);
		for (int[] o : ops) {
			key.op(o[0], o[1], o[2], o[3]);
		}
		return key;
	}

	/** One pose as one frame, without the ground checks (see {@link #compile(List)}). */
	static FxAnim.Key compile(Pose p) {
		return key(solve(p), p);
	}

	private static int[] origin(int slot, double[] p) {
		return new int[] { slot, (int) Math.round(p[0]), (int) Math.round(p[1]), (int) Math.round(p[2]) };
	}

	/**
	 * Compiles a whole sequence against the client's own integer posing. A frame can only hold whole angle steps
	 * (1.4 degrees) and the client's fixed-point rotations round every joint a little upward, so a straight IK
	 * solution leaves planted paws hovering a few units and creeping from frame to frame. So for each frame the
	 * neighbouring angle steps of every planted leg are tried, each posed exactly as the client poses it, and the one
	 * that puts the sole closest to its target is kept. Legs, head and tail that lie on the ground are then settled onto
	 * it, a CONTACT body is put down on it under them (again, until it holds) and a CLAMP body is lifted out of it.
	 * Every frame is then checked ({@link #check}), and the build fails if anything is off the ground or through it.
	 */
	static List<FxAnim.Key> compile(List<Pose> poses) {
		ModelData base = model();
		int[][] soles = soles(base);
		double[][] off = new double[4][];
		for (int leg = 0; leg < 4; leg++) {
			double[] c = centroid(base, soles[leg]);
			off[leg] = new double[] { c[0] - LEGS[leg].ankle[0], c[1] - LEGS[leg].ankle[1], c[2] - LEGS[leg].ankle[2] };
		}
		List<FxAnim.Key> out = new ArrayList<>();
		for (int i = 0; i < poses.size(); i++) {
			Pose p = poses.get(i);
			Solved s = solve(p);
			for (int iter = 0; iter < 16; iter++) {
				s = solve(p);
				refine(s, p, base, soles, off);
				settle(s, p, base); // legs, head and tail lying down come to rest on the ground under it
				for (Leg l : LEGS) {
					if (p.rest[l.index]) {
						p.keep(s, l.upper, l.lower, l.paw);
					}
				}
				if (p.headDown) {
					p.keep(s, NECK_B, HEAD_B);
				}
				if (p.tailDown) {
					p.keep(s, TAIL1_B, TAIL2_B);
				}
				if (p.ground == Ground.CONTACT) { // the body on the ground: its hull, its flank or a shoulder under it
					int shift = bodyLow(posed(base, s, p), p) + (int) Math.round(p.hover);
					if (shift != 0) {
						p.rootT[1] -= shift;
						continue;
					}
				}
				int low = lowest(posed(base, s, p));
				boolean lifted = false;
				for (int leg = 0; leg < 4 && low > 1; leg++) {
					if (p.foot[leg] != null && p.pawTilt[leg] != 0) {
						p.foot[leg][1] -= low; // a swinging paw's claws caught the ground: lift it clear
						lifted = true;
					}
				}
				if (lifted) {
					continue;
				}
				int free = lowestFree(posed(base, s, p), p);
				if (p.ground == Ground.CLAMP && free > 0) {
					p.rootT[1] -= free; // (not for a planted paw: the IK holds that, and lifting the body won't lift it)
					continue;
				}
				break;
			}
			List<String> bad = check(p, posed(base, s, p));
			if (!bad.isEmpty()) {
				throw new IllegalStateException("Shardback: frame " + i + " is off the ground: " + String.join("; ", bad));
			}
			out.add(key(s, p));
		}
		return out;
	}

	static ModelData posed(ModelData base, Solved s, Pose p) {
		return FxAnim.pose(base, FxAnim.frames(SKINS, List.of(key(s, p))).get(0));
	}

	/** Vertex ids per label in the rest model. */
	private static int[][] byLabel;

	/** The lowest point (largest y) of the vertices on these labels. */
	static int low(ModelData m, int... labels) {
		if (byLabel == null) {
			ModelData base = model();
			List<List<Integer>> l = new ArrayList<>();
			for (int i = 0; i < 256; i++) {
				l.add(new ArrayList<>());
			}
			for (int v = 0; v < base.vertices.size(); v++) {
				l.get(base.vertexSkins.get(v)).add(v);
			}
			byLabel = new int[256][];
			for (int i = 0; i < 256; i++) {
				byLabel[i] = l.get(i).stream().mapToInt(Integer::intValue).toArray();
			}
		}
		int y = Integer.MIN_VALUE;
		for (int label : labels) {
			for (int v : byLabel[label]) {
				y = Math.max(y, m.vertices.get(v)[1]);
			}
		}
		return y;
	}

	/** The lowest point of the body proper: its hull and flank, and the upper legs of legs lying on the ground. */
	static int bodyLow(ModelData m, Pose p) {
		int y = low(m, TORSO, FLANK);
		for (int i = 0; i < 4; i++) {
			if (p.rest[i]) {
				y = Math.max(y, low(m, UPPER + i));
			}
		}
		return y;
	}

	/** The parts the contact report measures: name, labels. */
	static final String[] PART_NAMES = { "hull", "flank", "head", "tail", "FL", "FR", "HL", "HR" };
	static final int[][] PART_LABELS = { { TORSO }, { FLANK }, { HEAD, JAW }, { TAIL1, TAIL2 }, leg(0), leg(1), leg(2), leg(3) };

	static int[] leg(int i) {
		return new int[] { UPPER + i, LOWER + i, PAW + i };
	}

	/**
	 * What should touch the ground in this pose and doesn't, or what has gone through it: planted paws, legs lying
	 * down (both the lower leg and the paw), the hull of a body lying down and a head resting on the ground - each
	 * within two units of it - and nothing anywhere more than two units under it. (Under a bounce, "the ground" is
	 * {@code hover} units below where they rest.)
	 */
	static List<String> check(Pose p, ModelData m) {
		List<String> bad = new ArrayList<>();
		double t = -p.hover;
		int all = lowest(m);
		if (all > t + 2) {
			bad.add("something is " + (int) Math.round(all - t) + " under the ground");
		}
		String[] names = { "FL", "FR", "HL", "HR" };
		for (int i = 0; i < 4; i++) {
			if (p.rest[i]) {
				near(bad, names[i] + " lower leg", low(m, LOWER + i), t);
				near(bad, names[i] + " paw", low(m, PAW + i), t);
			} else if (p.foot[i] != null && p.pawTilt[i] == 0) {
				near(bad, names[i] + " planted paw", low(m, PAW + i), t);
			}
		}
		if (p.ground == Ground.CONTACT) {
			near(bad, "body", bodyLow(m, p), t);
		}
		if (p.headDown) {
			near(bad, "head", low(m, HEAD, JAW), t);
		}
		if (p.tailDown) {
			near(bad, "tail", low(m, TAIL2), t);
		}
		return bad;
	}

	private static void near(List<String> bad, String what, int y, double t) {
		if (Math.abs(y - t) > 2) {
			bad.add(what + (y < t ? " floats " + Math.round(t - y) : " sinks " + Math.round(y - t)));
		}
	}

	/**
	 * Brings resting legs and a resting head down onto the ground: from the authored angles, steps each leg's hip
	 * (pitch and roll), knee and paw (pitch and roll), and the neck and head, one angle step at a time while that gets
	 * its lower leg and paw (or its head) closer to lying on the ground without going into it, and without wandering
	 * far from the pose as authored. Legs and head are independent, so they are all searched at once.
	 */
	static void settle(Solved s, Pose p, ModelData base) {
		int[][] prm = new int[6][];
		for (Leg l : LEGS) {
			if (p.rest[l.index]) {
				int[] up = s.rot.get(l.upper), lo = s.rot.get(l.lower), pw = s.rot.get(l.paw);
				prm[l.index] = new int[] { up[0], up[2], lo[0], pw[0], pw[2] };
			}
		}
		if (p.headDown) {
			int[] n = s.rot.getOrDefault(NECK_B, new int[3]), h = s.rot.getOrDefault(HEAD_B, new int[3]);
			prm[4] = new int[] { n[0], n[1], h[0], h[1], h[2] };
		}
		if (p.tailDown) {
			int[] t1 = s.rot.getOrDefault(TAIL1_B, new int[3]), t2 = s.rot.getOrDefault(TAIL2_B, new int[3]);
			prm[5] = new int[] { t1[0], t1[1], t2[0], t2[1] };
		}
		if (Arrays.stream(prm).allMatch(x -> x == null)) {
			return;
		}
		int[][] design = new int[6][];
		for (int g = 0; g < 6; g++) {
			design[g] = prm[g] == null ? null : prm[g].clone();
		}
		double t = -p.hover;
		int[][] bestPrm = new int[6][];
		double[] bestCost = new double[6];
		Arrays.fill(bestCost, Double.MAX_VALUE);
		java.util.Random rnd = new java.util.Random(4103);
		for (int start = 0; start < 13; start++) {
			int[][] cur = new int[6][];
			for (int g = 0; g < 6; g++) {
				if (design[g] != null) {
					cur[g] = design[g].clone();
					for (int k = 0; start > 0 && k < cur[g].length; k++) {
						cur[g][k] += rnd.nextInt(49) - 24;
					}
				}
			}
			double[] c = descend(s, p, base, cur, design, t);
			for (int g = 0; g < 6; g++) {
				if (cur[g] != null && c[g] < bestCost[g]) {
					bestCost[g] = c[g];
					bestPrm[g] = cur[g];
				}
			}
		}
		apply(s, bestPrm);
	}

	/** The search's moves: one joint by 8, 4, 2 or 1 steps either way, or two joints by 4 or 1 steps together. */
	private static final List<int[]> MOVES = new ArrayList<>();
	static {
		for (int k = 0; k < 5; k++) {
			for (int st : new int[] { 8, -8, 4, -4, 2, -2, 1, -1 }) {
				int[] d = new int[5];
				d[k] = st;
				MOVES.add(d);
			}
		}
		for (int a = 0; a < 5; a++) {
			for (int b = a + 1; b < 5; b++) {
				for (int st : new int[] { 4, 1 }) {
					for (int sa = -1; sa <= 1; sa += 2) {
						for (int sb = -1; sb <= 1; sb += 2) {
							int[] d = new int[5];
							d[a] = sa * st;
							d[b] = sb * st;
							MOVES.add(d);
						}
					}
				}
			}
		}
	}

	/** Coordinate descent on every group at once (they don't touch each other), in steps of 8, 4, 2 and 1. */
	private static double[] descend(Solved s, Pose p, ModelData base, int[][] prm, int[][] design, double t) {
		double[] best = new double[6];
		apply(s, prm);
		ModelData m = posed(base, s, p);
		for (int g = 0; g < 6; g++) {
			best[g] = prm[g] == null ? 0 : restCost(g, m, prm[g], design[g], t);
		}
		for (int iter = 0; iter < 200; iter++) {
			boolean better = false;
			for (int[] d : MOVES) {
				int[][] trial = new int[6][];
				for (int g = 0; g < 6; g++) {
					if (prm[g] != null) {
						trial[g] = prm[g].clone();
						for (int k = 0; k < trial[g].length; k++) {
							trial[g][k] += d[k];
						}
					}
				}
				apply(s, trial);
				m = posed(base, s, p);
				for (int g = 0; g < 6; g++) {
					if (prm[g] != null) {
						double c = restCost(g, m, trial[g], design[g], t);
						if (c < best[g] - 1e-9) {
							best[g] = c;
							prm[g] = trial[g];
							better = true;
						}
					}
				}
			}
			if (!better) {
				break;
			}
		}
		apply(s, prm);
		return best;
	}

	private static void apply(Solved s, int[][] prm) {
		for (Leg l : LEGS) {
			int[] q = prm[l.index];
			if (q != null) {
				s.rot.put(l.upper, new int[] { wrap(q[0]), 0, wrap(q[1]) });
				s.rot.put(l.lower, new int[] { wrap(q[2]), 0, 0 });
				s.rot.put(l.paw, new int[] { wrap(q[3]), 0, wrap(q[4]) });
			}
		}
		if (prm[4] != null) {
			s.rot.put(NECK_B, new int[] { wrap(prm[4][0]), wrap(prm[4][1]), 0 });
			s.rot.put(HEAD_B, new int[] { wrap(prm[4][2]), wrap(prm[4][3]), wrap(prm[4][4]) });
		}
		if (prm[5] != null) {
			s.rot.put(TAIL1_B, new int[] { wrap(prm[5][0]), wrap(prm[5][1]), 0 });
			s.rot.put(TAIL2_B, new int[] { wrap(prm[5][2]), wrap(prm[5][3]), 0 });
		}
	}

	/** How far a resting leg (or the head, g = 4, or the tail, g = 5) is from lying on the ground at t, and how far it strayed. */
	private static double restCost(int g, ModelData m, int[] q, int[] design, double t) {
		double c = 0;
		int deepest;
		if (g < 4) {
			int yl = low(m, LOWER + g), yp = low(m, PAW + g);
			deepest = Math.max(Math.max(yl, yp), low(m, UPPER + g));
			c += (yl - t) * (yl - t) + (yp - t) * (yp - t);
		} else if (g == 4) {
			int yh = low(m, HEAD, JAW);
			deepest = Math.max(yh, low(m, NECK));
			c += (yh - t) * (yh - t);
		} else {
			int yt = low(m, TAIL2);
			deepest = Math.max(yt, low(m, TAIL1));
			c += (yt - t) * (yt - t);
		}
		double pen = Math.max(0, deepest - t);
		c += 60 * pen * pen;
		for (int k = 0; k < q.length; k++) {
			c += 0.015 * (q[k] - design[k]) * (q[k] - design[k]);
		}
		return c;
	}

	/** Picks, per planted leg, the hip / knee / roll steps (near the IK answer) that land the sole best. */
	static void refine(Solved s, Pose p, ModelData base, int[][] soles, double[][] off) {
		int[][] start = new int[4][];
		double[] best = new double[4];
		int[][] pick = new int[4][];
		boolean any = false;
		for (Leg l : LEGS) {
			int i = l.index;
			if (p.foot[i] != null && p.pawTilt[i] == 0) {
				start[i] = new int[] { s.rot.get(l.upper)[0], s.rot.get(l.lower)[0], s.rot.get(l.upper)[2] };
				best[i] = Double.MAX_VALUE;
				any = true;
			}
		}
		if (!any) {
			return;
		}
		for (int c = 0; c < 75; c++) {
			int du = c % 5 - 2, dl = c / 5 % 5 - 2, dr = c / 25 - 1;
			for (Leg l : LEGS) {
				if (start[l.index] != null) {
					int[] st = start[l.index];
					setLeg(s, l, st[0] + du, st[1] + dl, st[2] + dr, 0);
				}
			}
			ModelData posed = FxAnim.pose(base, FxAnim.frames(SKINS, List.of(key(s, p))).get(0));
			for (Leg l : LEGS) {
				int i = l.index;
				if (start[i] == null) {
					continue;
				}
				double[] at = centroid(posed, soles[i]);
				double ex = at[0] - p.foot[i][0] - off[i][0], ey = at[1] - p.foot[i][1] - off[i][1], ez = at[2] - p.foot[i][2] - off[i][2];
				double err = ex * ex + 4 * ey * ey + ez * ez;
				if (err < best[i] - 1e-9) {
					best[i] = err;
					pick[i] = new int[] { start[i][0] + du, start[i][1] + dl, start[i][2] + dr };
				}
			}
		}
		for (Leg l : LEGS) {
			if (pick[l.index] != null) {
				setLeg(s, l, pick[l.index][0], pick[l.index][1], pick[l.index][2], 0);
			}
		}
	}

	/** Per leg, the vertices on the sole of its paw (the paw's vertices lying on the ground in the rest pose). */
	static int[][] soles(ModelData base) {
		int[][] out = new int[4][];
		for (int leg = 0; leg < 4; leg++) {
			List<Integer> ids = new ArrayList<>();
			for (int i = 0; i < base.vertices.size(); i++) {
				if (base.vertexSkins.get(i) == PAW + leg && base.vertices.get(i)[1] == 0) {
					ids.add(i);
				}
			}
			out[leg] = ids.stream().mapToInt(Integer::intValue).toArray();
		}
		return out;
	}

	static double[] centroid(ModelData m, int[] ids) {
		double[] c = new double[3];
		for (int v : ids) {
			for (int a = 0; a < 3; a++) {
				c[a] += m.vertices.get(v)[a] / (double) ids.length;
			}
		}
		return c;
	}

	/** The largest y of a posed model (y is down: its lowest point). */
	static int lowest(ModelData m) {
		int low = Integer.MIN_VALUE;
		for (int[] v : m.vertices) {
			low = Math.max(low, v[1]);
		}
		return low;
	}

	/** The lowest point of everything but the paws planted by IK. */
	static int lowestFree(ModelData m, Pose p) {
		low(m, TORSO); // (fills the label table)
		int y = Integer.MIN_VALUE;
		for (int label = 0; label < 256; label++) {
			int leg = label - PAW;
			if (leg >= 0 && leg < 4 && p.foot[leg] != null && p.pawTilt[leg] == 0) {
				continue;
			}
			for (int v : byLabel[label]) {
				y = Math.max(y, m.vertices.get(v)[1]);
			}
		}
		return y;
	}

	/* ====================================================================== */
	/*                               ANIMATIONS                               */
	/* ====================================================================== */

	static double wave(double t) {
		return Math.sin(t * Math.PI * 2);
	}

	/** Stand: a slow breath that lifts the shoulders, its weight rolling from side to side, a look round and a sniff. */
	static List<Pose> stand() {
		List<Pose> out = new ArrayList<>();
		int n = 16;
		for (int i = 0; i < n; i++) {
			double t = (double) i / n;
			double breath = wave(t * 2 - 0.25); // two breaths a loop
			double sway = wave(t);
			Pose p = new Pose(7).body(-1.0 * breath, 2 * sway, 2.2 * sway, 5.0 * sway, 1.5 + 1.6 * breath, 3 * wave(t + 0.25));
			// it looks to one side, then the other, then drops its nose to sniff the ground
			double look = t < 0.5 ? 26 * Math.sin(t * 2 * Math.PI) : 0;
			double sniff = t >= 0.5 ? Math.sin((t - 0.5) * 2 * Math.PI) : 0;
			p.head(4 + 20 * sniff - 2 * breath, look * 0.45, 3 + 14 * sniff, look * 0.55, sniff > 0.3 ? 10 * sniff : 2 + breath);
			p.tail(-4 + 3 * breath, 14 * wave(t + 0.1), 6, 18 * wave(t - 0.05));
			p.bristle = 1 + 0.03 * Math.max(0, breath);
			out.add(p);
		}
		return out;
	}

	/** Walk: a heavy four-beat lateral walk, one tile a cycle, so planted paws stay planted while it moves. */
	static List<Pose> walk() {
		List<Pose> out = new ArrayList<>();
		int n = 10, delay = 3; // 30 client cycles = one 600 ms tick = one tile
		double speed = 128.0 / (n * delay); // units per client cycle
		double duty = 0.62, stride = speed * n * delay * duty, lift = 24;
		double[] phase = { 0.25, 0.75, 0, 0.5 }; // fore-left, fore-right, hind-left, hind-right: LH, LF, RH, RF
		for (int i = 0; i < n; i++) {
			double t = (double) i / n;
			// the body dips as each hind paw takes the weight, sways over the planted side, and the shoulders roll
			double dip = 2.5 * Math.cos((t - 0.08) * 4 * Math.PI);
			Pose p = new Pose(delay).body(1.2 * Math.cos((t - 0.3) * 4 * Math.PI), 2.5 * wave(t + 0.25), 2.2 * wave(t + 0.05),
					3.5 * wave(t + 0.05), 7 + dip, 0);
			for (int leg = 0; leg < 4; leg++) {
				Leg l = LEGS[leg];
				double u = ((t - phase[leg]) % 1 + 1) % 1; // 0 = touchdown
				double cz = l.ankle[2] + (l.fore() ? -4 : -6), y = l.ankle[1], z, tilt = 0;
				if (u < duty) {
					z = cz - stride / 2 + stride * (u / duty); // planted: the ground slides back under it
				} else {
					double s = (u - duty) / (1 - duty), e = s * s * (3 - 2 * s);
					z = cz + stride / 2 - stride * e;
					y -= lift * Math.sin(Math.PI * s) * (l.fore() ? 1 : 0.8);
					tilt = 38 * Math.sin(Math.PI * Math.min(1, s * 1.4)); // heel up as it swings through
				}
				p.foot(leg, l.ankle[0], y, z, tilt);
			}
			// head nods against the dip; tail swings against the sway
			p.head(6 - 3 * Math.cos((t - 0.2) * 4 * Math.PI), -4 * wave(t + 0.25), 4 - 2 * Math.cos((t - 0.3) * 4 * Math.PI), -3 * wave(t + 0.2),
					3);
			p.tail(-6, -12 * wave(t + 0.15), 8, -16 * wave(t + 0.05));
			out.add(p);
		}
		return out;
	}

	/** Attack: it rocks back onto its haunches with its glass up and jaw open, then throws itself forward into a bite. */
	static List<Pose> attack() {
		List<Pose> out = new ArrayList<>();
		Leg fr = LEGS[1];
		double fx = fr.ankle[0], fz = fr.ankle[2];
		// delay, body pitch, yaw, roll, tx, ty, tz | neck, neck yaw, head, head yaw, jaw | bristle | fore-right paw y, z, tilt
		double[][] k = { { 4, 0, 0, 0, 0, 1, 0, 4, 0, 3, 0, 2, 1.0, -17, fz, 0 }, // ready
				{ 5, -5, -2, -1.5, -2, 6, 12, -12, -3, -6, -2, 10, 1.18, -24, fz + 6, 20 }, // gathering
				{ 6, -10, -4, -3, -3, 9, 22, -26, -6, -12, -4, 26, 1.38, -46, fz + 2, 34 }, // coiled: paw up, glass up
				{ 3, 4, 2, 1.5, 0, 2, -14, 8, 2, 6, 2, 40, 1.45, -40, fz - 34, 10 }, // the lunge, jaw wide
				{ 3, 10, 3, 2, 1, 6, -36, 24, 4, 14, 4, 2, 1.5, -17, fz - 52, 0 }, // bite: jaw snaps shut, paw slams down
				{ 4, 7, 6, 1, 1, 9, -32, 20, 10, 12, 8, 7, 1.4, -17, fz - 52, 0 }, // it worries the bite
				{ 4, 4, -3, -1, 0, 7, -20, 12, -6, 8, -6, 10, 1.25, -17, fz - 52, 0 }, // shakes the other way
				{ 5, 1, 0, 0, 0, 4, -8, 6, 0, 4, 0, 5, 1.1, -42, fz - 24, 24 }, // pulls back, paw lifting
				{ 6, 0, 0, 0, 0, 1, 0, 4, 0, 3, 0, 2, 1.0, -17, fz, 0 } }; // paw back down: ready again
		for (double[] f : k) {
			Pose p = new Pose((int) f[0]).body(f[1], f[2], f[3], f[4], f[5], f[6]);
			p.ground = Ground.CLAMP;
			p.head(f[7], f[8], f[9], f[10], f[11]);
			p.bristle = f[12];
			p.foot(1, fx, f[13], f[14], f[15]);
			p.tail(-8 - f[1], -f[2] * 2, 6 + f[1], -f[2] * 3);
			out.add(p);
		}
		return out;
	}

	/** Block: it flinches back from the blow, head jerked up and away, and its glass bristles. */
	static List<Pose> block() {
		List<Pose> out = new ArrayList<>();
		double[][] k = { { 3, -4, -3, -2, 0, 4, 10, -12, -10, -8, -8, 14, 1.3 }, { 4, -6, -4, -3, 0, 6, 14, -16, -14, -10, -10, 18, 1.4 },
				{ 4, -2, -1, -1, 0, 3, 6, -6, -6, -3, -4, 8, 1.2 }, { 4, 0, 0, 0, 0, 1, 0, 4, 0, 3, 0, 2, 1.0 } };
		for (double[] f : k) {
			Pose p = new Pose((int) f[0]).body(f[1], f[2], f[3], f[4], f[5], f[6]);
			p.head(f[7], f[8], f[9], f[10], f[11]);
			p.bristle = f[12];
			p.tail(-10, 8, 10, 12);
			out.add(p);
		}
		return out;
	}

	/**
	 * Death: the killing blow rears it up; it comes down hard and staggers, its forelegs give and its knees and chin
	 * hit the dirt, then the hindquarters go and it rolls half onto its side: the thighs drape down, the lower legs and
	 * paws lie along the ground, and the head and tail rest in the dust. Everything from the knees hitting the ground on
	 * is placed by {@link #settle} and checked by {@link #check}. (It stops at about 40 degrees: it is wider across the
	 * hips than its legs are long, so rolled much further the legs on top can't reach the ground and would hang in the
	 * air, and further still the glass on its lower flank would prop it up.)
	 */
	static List<Pose> death() {
		List<Pose> out = new ArrayList<>();
		// 1. the blow: it rears, head thrown back, jaw open, forepaws off the ground
		Pose p = new Pose(4).body(-10, 0, 0, 0, 2, 12);
		p.head(-22, 0, -14, 0, 30);
		p.bristle = 1.3;
		p.foot(0, LEGS[0].ankle[0], -30, LEGS[0].ankle[2] - 6, 20).foot(1, LEGS[1].ankle[0], -34, LEGS[1].ankle[2] - 10, 24);
		p.tail(-18, 0, -6, 0);
		out.add(p);
		p = new Pose(6).body(-14, -4, -3, -4, 0, 18);
		p.head(-30, -6, -18, -6, 36);
		p.bristle = 1.35;
		p.foot(0, LEGS[0].ankle[0], -44, LEGS[0].ankle[2] - 10, 28).foot(1, LEGS[1].ankle[0], -50, LEGS[1].ankle[2] - 16, 30);
		p.tail(-24, 0, -8, 0);
		out.add(p);
		// 2. it comes down hard and staggers towards -x, catching itself on a splayed foreleg
		p = new Pose(5).body(7, 6, 7, -9, 12, 2);
		p.ground = Ground.CLAMP;
		p.head(16, 10, 10, 8, 20);
		p.bristle = 1.1;
		p.foot(1, LEGS[1].ankle[0] - 12, -17, LEGS[1].ankle[2] - 10, 0);
		p.tail(-6, 10, 6, 14);
		out.add(p);
		// 3. the forelegs give: knees and paws hit the dirt, then its chin; the rump is still up on the hind legs
		p = new Pose(5).body(11, 8, 10, -14, 30, 0);
		p.head(24, 14, 12, 10, 24);
		p.headDown = true;
		p.rest(0, -30, 10, 70).rest(1, -34, -14, 74);
		p.tail(-4, 14, 8, 18);
		out.add(p);
		// 4. the hindquarters go and it drops, rolling over
		p = new Pose(4).body(6, -4, 28, -18, 60, 4);
		p.ground = Ground.CONTACT;
		p.head(24, 18, 12, 12, 28);
		p.headDown = p.tailDown = true;
		p.rest(0, -60, -18, 50).rest(1, -70, 6, 50).rest(2, -30, -18, 70).rest(3, -33, 6, 80);
		p.tail(6, 18, 12, 22);
		p.bristle = 0.96;
		out.add(p);
		// 5. it hits the ground and bounces once
		p = new Pose(3).body(4, -8, 30, -22, 60, 4);
		p.ground = Ground.CONTACT;
		p.hover = 5;
		p.head(18, 24, 6, 14, 22);
		p.headDown = p.tailDown = true;
		p.rest(0, -40, -40, 40).rest(1, -50, 14, 40).rest(2, -24, -40, 50).rest(3, -30, 14, 60);
		p.tail(2, 20, 10, 24);
		p.bristle = 0.96;
		out.add(p);
		// 6. it settles half on its side, every leg sprawled out along the ground,
		// a last breath out, the head lolls, the jaw goes slack, one hind leg kicks and is still
		double[][] settle = { { 4, 36, 34, 0, 0 }, { 5, 38, 30, 10, 6 }, { 6, 39, 36, -5, 0 }, { 8, 40, 40, 2, 0 },
				{ 85, 40, 42, 0, 0 } }; // (held past the 4 ticks the server keeps a dead npc, so it never stands back up)
		for (double[] st : settle) {
			p = new Pose((int) st[0]).body(5, -10, st[1], -26, 60, 4);
			p.ground = Ground.CONTACT;
			p.head(20, 30, 10, 18, st[2]);
			p.set(HEAD_B, 10, 18, 14); // the head lolls over on its side
			p.headDown = p.tailDown = true;
			p.rest(0, -30, -56, 30).rest(1, -40, 20, 30).rest(2, -20 + st[3], -56, 40 - st[3]).rest(3, -26, 20, 40 + st[4]);
			p.tail(8, 24 - st[3], 14, 30 - st[3]);
			p.bristle = 0.94;
			out.add(p);
		}
		out.replaceAll(Shardback::mirror); // (authored falling to -x; it goes down on its +x side)
		return out;
	}

	/** The pose reflected left to right: legs swap sides, yaw and roll turn the other way. */
	static Pose mirror(Pose p) {
		Pose m = new Pose(p.delay);
		m.body(p.rootRot[0], -p.rootRot[1], -p.rootRot[2], -p.rootT[0], p.rootT[1], p.rootT[2]);
		Map<Bone, Bone> swap = new java.util.HashMap<>();
		for (Leg l : LEGS) {
			Leg o = LEGS[l.index ^ 1];
			swap.put(l.upper, o.upper);
			swap.put(l.lower, o.lower);
			swap.put(l.paw, o.paw);
		}
		for (Leg l : LEGS) {
			m.rot.remove(l.paw); // (re-added below if the source sets it)
		}
		p.rot.forEach((b, a) -> m.rot.put(swap.getOrDefault(b, b), new double[] { a[0], -a[1], -a[2] }));
		for (int i = 0; i < 4; i++) {
			double[] f = p.foot[i ^ 1];
			m.foot[i] = f == null ? null : new double[] { -f[0], f[1], f[2] };
			m.pawTilt[i] = p.pawTilt[i ^ 1];
		}
		m.bristle = p.bristle;
		m.ground = p.ground;
		m.hover = p.hover;
		m.headDown = p.headDown;
		m.tailDown = p.tailDown;
		for (int i = 0; i < 4; i++) {
			m.rest[i] = p.rest[i ^ 1];
		}
		return m;
	}

	/* ====================================================================== */
	/*                               THE CACHE                                */
	/* ====================================================================== */

	/** Every sequence: id, its poses. Frame ids run from FRAME_BASE in this order. */
	static Map<Integer, List<FxAnim.Key>> sequences() {
		Map<Integer, List<FxAnim.Key>> s = new LinkedHashMap<>();
		s.put(SEQ_STAND, compile(stand()));
		s.put(SEQ_WALK, compile(walk()));
		s.put(SEQ_ATTACK, compile(attack()));
		s.put(SEQ_DEATH, compile(death()));
		s.put(SEQ_BLOCK, compile(block()));
		return s;
	}

	/** A seq.dat entry: its frames (ids from {@code first}) with their delays (stand and walk loop as npc idle anims). */
	static byte[] seqEntry(List<FxAnim.Key> keys, int first) {
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		o.write(1);
		o.write(keys.size());
		for (int i = 0; i < keys.size(); i++) {
			int id = first + i;
			o.write(id >> 8);
			o.write(id);
			o.write(0xFF); // no secondary frame
			o.write(0xFF);
			o.write(0);
			o.write(keys.get(i).delay);
		}
		o.write(0);
		return o.toByteArray();
	}

	/** The npc definition, as the client reads it (EntityDef). */
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

	/**
	 * Writes the Shardback into a generated cache: the model, a new frame-group file, five sequences and the npc
	 * definition, each at its fixed id. Safe to repeat; refuses if any of its ids has been taken by something else.
	 */
	public static void install(CacheEditor cache) throws IOException {
		CacheEditor.Archive cfg = CacheEditor.Archive.decode(cache.read(0, ConfigDefinitions.CONFIG_ARCHIVE));
		ConfigDefinitions.Table npcs = ConfigDefinitions.load(cfg, "npc", true);
		boolean ours = npcs.size() > NPC_ID && NAME.equals(npcs.decode(NPC_ID).text(2));
		CacheEditor.Archive vl = CacheEditor.Archive.decode(cache.read(0, CacheEditor.VERSIONLIST_ARCHIVE));
		byte[] seqDat = cfg.get("seq.dat");
		int seqCount = (seqDat[0] & 0xFF) << 8 | seqDat[1] & 0xFF;
		byte[] animIndex = vl.get("anim_index");
		if (!ours) {
			require(npcs.size() <= NPC_ID, "npc " + NPC_ID + " is taken (" + npcs.size() + " npcs)");
			require(versionAt(vl.get("model_version"), MODEL_ID) == 0, "model " + MODEL_ID + " is taken");
			require(versionAt(vl.get("anim_version"), FRAME_FILE) == 0, "frame file " + FRAME_FILE + " is taken");
			require(animIndex.length / 2 <= FRAME_BASE, "frame id " + FRAME_BASE + " is taken (" + animIndex.length / 2 + " frames)");
			require(seqCount <= SEQ_STAND, "seq " + SEQ_STAND + " is taken (" + seqCount + " seqs)");
		}
		Map<Integer, List<FxAnim.Key>> seqs = sequences();

		// the model
		byte[][] mt = { vl.get("model_version"), vl.get("model_crc") };
		cache.writeVersionedFile(ConfigDefinitions.MODEL_INDEX, MODEL_ID, model().encode(), 1, mt);
		vl.put("model_version", mt[0]);
		vl.put("model_crc", mt[1]);
		vl.put("model_index", grow(vl.get("model_index"), mt[0].length / 2));

		// the frames: one frame-group file holding every sequence's frames, ids FRAME_BASE..
		List<FxAnim.Key> all = new ArrayList<>();
		seqs.values().forEach(all::addAll);
		byte[][] at = { vl.get("anim_version"), vl.get("anim_crc") };
		cache.writeVersionedFile(2, FRAME_FILE, FxAnim.encode(SKINS, all, FRAME_BASE), 1, at);
		vl.put("anim_version", at[0]);
		vl.put("anim_crc", at[1]);
		// the client sizes its frame table from anim_index (one u16 per frame id): cover ours, each naming our file
		animIndex = grow(animIndex, Math.max(animIndex.length, (FRAME_BASE + all.size()) * 2));
		for (int i = 0; i < all.size(); i++) {
			ByteBuffer.wrap(animIndex).putShort((FRAME_BASE + i) * 2, (short) FRAME_FILE);
		}
		vl.put("anim_index", animIndex);
		cache.write(0, CacheEditor.VERSIONLIST_ARCHIVE, vl.encode());

		// the sequences, padded with empty ones up to ours
		List<byte[]> entries = seqEntries(seqDat, seqCount);
		while (entries.size() <= SEQ_BLOCK) {
			entries.add(new byte[] { 0 });
		}
		int frame = FRAME_BASE;
		for (Map.Entry<Integer, List<FxAnim.Key>> e : seqs.entrySet()) {
			entries.set(e.getKey(), seqEntry(e.getValue(), frame));
			frame += e.getValue().size();
		}
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		o.write(entries.size() >> 8);
		o.write(entries.size());
		for (byte[] b : entries) {
			o.write(b, 0, b.length);
		}
		cfg.put("seq.dat", o.toByteArray());

		// the npc, padded with empty definitions up to its id (a later generator change can still fill those)
		while (npcs.size() < NPC_ID) {
			npcs.put(npcs.size(), new ConfigDefinitions.Definition());
		}
		npcs.put(NPC_ID, definition());
		ConfigDefinitions.save(cfg, npcs);
		cache.write(0, ConfigDefinitions.CONFIG_ARCHIVE, cfg.encode());
		ModelData m = model();
		System.out.println("Shardback: npc " + NPC_ID + ", model " + MODEL_ID + " (" + m.faces.size() + " tris), frame file " + FRAME_FILE
				+ " (frames " + FRAME_BASE + "-" + (FRAME_BASE + all.size() - 1) + "), seqs " + SEQ_STAND + "-" + SEQ_BLOCK);
	}

	/** Splits seq.dat into its raw entries (each up to and including its 0 terminator). */
	static List<byte[]> seqEntries(byte[] dat, int count) {
		List<byte[]> out = new ArrayList<>();
		int pos = 2;
		for (int i = 0; i < count; i++) {
			int start = pos;
			for (int op; (op = dat[pos++] & 0xFF) != 0;) {
				if (op == 1) {
					pos += 1 + (dat[pos] & 0xFF) * 6;
				} else if (op == 2 || op == 6 || op == 7) {
					pos += 2;
				} else if (op == 3) {
					pos += 1 + (dat[pos] & 0xFF);
				} else if (op == 5 || op == 8 || op == 9 || op == 10 || op == 11) {
					pos += 1;
				} else if (op == 12) {
					pos += 4;
				} else if (op != 4) {
					throw new IllegalStateException("seq op " + op + " in seq " + i);
				}
			}
			out.add(Arrays.copyOfRange(dat, start, pos));
		}
		return out;
	}

	static int versionAt(byte[] table, int id) {
		return id * 2 + 2 > table.length ? 0 : (table[id * 2] & 0xFF) << 8 | table[id * 2 + 1] & 0xFF;
	}

	static byte[] grow(byte[] in, int size) {
		return in.length >= size ? in : Arrays.copyOf(in, size);
	}

	static void require(boolean ok, String what) {
		if (!ok) {
			throw new IllegalStateException("Shardback: " + what + " - move its ids (Shardback.java) clear of it");
		}
	}

	/** {@code Shardback [cacheDir]}: installs it into a generated cache. */
	public static void main(String[] args) throws Exception {
		try (CacheEditor cache = new CacheEditor(Paths.get(args.length > 0 ? args[0] : "data/cache"))) {
			install(cache);
		}
	}
}
