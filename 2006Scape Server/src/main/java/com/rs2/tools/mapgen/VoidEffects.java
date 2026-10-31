package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ModelData.hsl;

import java.util.*;

/**
 * Void-saga special effects ("graphics" / spotanims) in the old-school style.
 *
 * Every effect is a low-poly model whose VERTEX skins (labels) and FACE skins are laid out to match the skin list
 * of an EXISTING cache animation, so it can be wired in with nothing but a model + a spotanim.dat entry
 * (no new frames). Each one also carries its own authored frame script (a skin list + per-frame
 * translate/rotate/scale/alpha ops in exact 317 semantics) for the "new frames" route; FxAnim encodes those
 * scripts into an idx2 frame-group file.
 *
 * Conventions: y is down (negative = up), 128 units = 1 tile. Projectiles fly toward -z (tail along +z),
 * exactly like the fire blast projectile (model 3087). Player-attached effects sit in front of the body at -z,
 * like the dragon dagger special (model 3478). Face alpha: 0 = opaque .. 255 = invisible.
 */
public final class VoidEffects {

	/** One effect: model + reuse mapping + authored script + spotanim settings + preview camera. */
	public static final class Effect {
		public String key, name, lore;
		public ModelData model;
		public int reuseSeq;          // existing cache seq whose frames drive this model unchanged
		public String reuseNote;      // which spotanim uses that seq today
		public AnimCache.SkinList skins; // authored script
		public List<FxAnim.Key> keys = new ArrayList<>();
		public int resizeXY = 128, resizeZ = 128, brightness = 60, shadow = 60, height;
		public double yaw = 0.6, pitch = 0.45, scale = 0.6;
		public int oy = 95;           // screen y of the model origin in a 160px cell
		public String labelMap;
	}

	/* ------------------------------------------------------------ helpers */

	static int nf(ModelData m) { return m.faces.size(); }

	/** Sets face skin / alpha / priority for faces added since {@code from}. */
	static void tag(ModelData m, int from, int skin, int alpha, int prio) {
		for (int i = from; i < m.faces.size(); i++) { m.faceSkins.set(i, skin); m.alphas.set(i, alpha); m.priorities.set(i, prio); }
	}

	/** Double-sided quad (flat effect sheets are seen from both sides; the client culls back faces). */
	static void quad2(ModelData m, int a, int b, int c, int d, int col) { m.quad(a, b, c, d, col); m.quad(a, d, c, b, col); }

	/** A bipyramid (the classic RS "gem"/"orb"): ring of {@code sides} at cy, apexes at cy-h / cy+h. */
	static void gem(ModelData m, double cx, double cy, double cz, double r, double h, int sides, double twist, int top, int bot) {
		double[] c = { cx, cy, cz };
		int[] ring = new int[sides];
		for (int s = 0; s < sides; s++) {
			double a = Math.PI * 2 * s / sides + twist;
			ring[s] = m.vertex(cx + Math.cos(a) * r, cy, cz + Math.sin(a) * r);
		}
		int up = m.vertex(cx, cy - h, cz), dn = m.vertex(cx, cy + h, cz);
		for (int s = 0; s < sides; s++) {
			int s2 = (s + 1) % sides;
			m.triOut(up, ring[s], ring[s2], top, c);
			m.triOut(dn, ring[s], ring[s2], bot, c);
		}
	}

	/** A gem lying along z (for projectiles): ring in the x-y plane. */
	static void gemZ(ModelData m, double cz, double r, double h, int sides, double twist, int front, int back) {
		double[] c = { 0, 0, cz };
		int[] ring = new int[sides];
		for (int s = 0; s < sides; s++) {
			double a = Math.PI * 2 * s / sides + twist;
			ring[s] = m.vertex(Math.cos(a) * r, Math.sin(a) * r, cz);
		}
		int f = m.vertex(0, 0, cz - h), b = m.vertex(0, 0, cz + h);
		for (int s = 0; s < sides; s++) {
			int s2 = (s + 1) % sides;
			m.triOut(f, ring[s], ring[s2], front, c);
			m.triOut(b, ring[s], ring[s2], back, c);
		}
	}

	static int[] zring(ModelData m, double z, double r, int sides, double twist) {
		int[] ring = new int[sides];
		for (int s = 0; s < sides; s++) {
			double a = Math.PI * 2 * s / sides + twist;
			ring[s] = m.vertex(Math.cos(a) * r, Math.sin(a) * r, z);
		}
		return ring;
	}

	static int[] yring(ModelData m, double y, double r, int sides, double twist) {
		int[] ring = new int[sides];
		for (int s = 0; s < sides; s++) {
			double a = Math.PI * 2 * s / sides + twist;
			ring[s] = m.vertex(Math.cos(a) * r, y, Math.sin(a) * r);
		}
		return ring;
	}

	static void band(ModelData m, int[] a, int[] b, int col, double[] ref) {
		for (int s = 0; s < a.length; s++) {
			int s2 = (s + 1) % a.length;
			m.quadOut(a[s], a[s2], b[s2], b[s], col, ref);
		}
	}

	/** Outward unit direction (x, z) of the strike-impact shard labels 2..9 in seq 660 (read off its frames). */
	static final double[][] SHARD_DIR = new double[10][];
	static {
		double r = Math.sqrt(0.5);
		SHARD_DIR[2] = new double[] { 0, -1 }; SHARD_DIR[3] = new double[] { r, -r }; SHARD_DIR[4] = new double[] { 1, 0 };
		SHARD_DIR[5] = new double[] { r, r }; SHARD_DIR[6] = new double[] { 0, 1 }; SHARD_DIR[7] = new double[] { -r, r };
		SHARD_DIR[8] = new double[] { -1, 0 }; SHARD_DIR[9] = new double[] { -r, -r };
	}

	static int[] range(int a, int b) { int[] r = new int[b - a + 1]; for (int i = a; i <= b; i++) r[i - a] = i; return r; }

	/* ============================================================ 1. VOID BOLT (projectile) */
	/*
	 * A pale-lilac core gem inside a deep-violet translucent shell, dragging a four-band tail that darkens from violet
	 * to void-black. Labels copy the fire blast projectile (gfx 130, model 3087, seq 663 / frame file 214):
	 * 0 core, 1 shell, 2-4 tail rings, 5 tail tip, 6 nose; face skins 2-5 = tail bands (seq 663 fades A[2..8]).
	 */
	public static Effect voidBolt() {
		Effect e = new Effect();
		e.key = "void_bolt"; e.name = "Void bolt (projectile)";
		e.lore = "A sliver of the Starless, flung. It does not glow so much as remove the light around it.";
		ModelData m = new ModelData();
		int f;
		m.skin = 0; f = nf(m);
		gemZ(m, -14, 13, 17, 4, Math.PI / 4, ConfigDefinitions.voidRamp(98, 4), ConfigDefinitions.voidRamp(80, 5));
		tag(m, f, 0, 0, 1);
		m.skin = 1; f = nf(m);
		gemZ(m, -12, 24, 26, 6, 0, ConfigDefinitions.voidRamp(40, 7), ConfigDefinitions.voidRamp(26, 7));
		tag(m, f, 1, 88, 0);
		m.skin = 6; int[] r0 = zring(m, 2, 18, 5, 0.3);
		m.skin = 2; int[] r1 = zring(m, 18, 15, 5, 0.9);
		m.skin = 3; int[] r2 = zring(m, 34, 11, 5, 1.5);
		m.skin = 4; int[] r3 = zring(m, 50, 7, 5, 2.1);
		m.skin = 5; int tip = m.vertex(0, 0, 72);
		f = nf(m); band(m, r0, r1, ConfigDefinitions.voidRamp(52, 7), new double[] { 0, 0, 10 }); tag(m, f, 2, 120, 0);
		f = nf(m); band(m, r1, r2, ConfigDefinitions.voidRamp(38, 7), new double[] { 0, 0, 26 }); tag(m, f, 3, 136, 0);
		f = nf(m); band(m, r2, r3, ConfigDefinitions.voidRamp(24, 6), new double[] { 0, 0, 42 }); tag(m, f, 4, 152, 0);
		f = nf(m);
		for (int s = 0; s < 5; s++) m.triOut(r3[s], r3[(s + 1) % 5], tip, ConfigDefinitions.voidRamp(12, 3), new double[] { 0, 0, 58 });
		tag(m, f, 5, 168, 0);
		e.model = m;
		e.reuseSeq = 663; e.reuseNote = "fire blast projectile (gfx 130/model 3087), frame file 214";
		// authored script: same slot layout as file 214, our own timing: core spins one way, shell the other,
		// tail rings wag side to side and stretch, the whole tail flickers.
		e.skins = FxAnim.skinList(new int[] { 0, 1, 2, 3, 5, 0, 1, 2, 3, 5, 0, 1, 3, 0, 1, 3, 0, 1, 3, 0, 1, 3, 0, 5 },
				new int[][] { { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 1 }, { 1 }, { 1 }, { 1 }, { 1 }, { 2 }, { 2 }, { 2 }, { 3 }, { 3 }, { 3 },
						{ 4 }, { 4 }, { 4 }, { 5 }, { 5 }, { 5 }, { 6 }, range(2, 8) });
		for (int i = 0; i < 8; i++) {
			double ph = Math.PI * 2 * i / 8;
			FxAnim.Key k = new FxAnim.Key(2);
			k.op(2, 0, 0, i * 32).op(3, 128 + (int) (20 * Math.sin(ph)), 128 + (int) (20 * Math.sin(ph)), 128 + (int) (20 * Math.sin(ph)));
			k.op(7, 0, 0, 256 - i * 16 & 0xff).op(8, 128 + (int) (12 * Math.cos(ph)), 128 + (int) (12 * Math.cos(ph)), 128);
			k.op(11, (int) (5 * Math.sin(ph)), (int) (3 * Math.cos(ph)), 4 + (int) (4 * Math.sin(ph)));
			k.op(14, (int) (8 * Math.sin(ph - 0.9)), (int) (5 * Math.cos(ph - 0.9)), 8 + (int) (6 * Math.sin(ph)));
			k.op(17, (int) (12 * Math.sin(ph - 1.8)), (int) (7 * Math.cos(ph - 1.8)), 12 + (int) (8 * Math.sin(ph)));
			k.op(20, (int) (16 * Math.sin(ph - 2.7)), (int) (9 * Math.cos(ph - 2.7)), 18 + (int) (10 * Math.sin(ph)));
			k.op(23, (int) (3 + 3 * Math.sin(ph * 2)), 0, 0);
			e.keys.add(k);
		}
		e.brightness = 60; e.shadow = 60; e.height = 0;
		e.yaw = Math.PI / 2 + 0.25; e.pitch = 0.4; e.scale = 1.0; e.oy = 85;
		e.labelMap = "vskin 0 core gem, 1 shell, 6 tail root ring, 2/3/4 tail rings, 5 tail tip; fskin 0 core, 1 shell, 2-5 tail bands";
		return e;
	}

	/* ============================================================ 2. VOID BOLT IMPACT */
	/*
	 * A void-black core with six lilac spikes (one per axis) and a thin violet halo: the spikes punch out and the
	 * whole burst swells and fades. Driven unchanged by the fire wave impact (gfx 157, seq 693 / file 186), which
	 * scales labels 0-9 together (45% -> 214%) and fades face skins 0-6.
	 */
	public static Effect voidImpact() {
		Effect e = new Effect();
		e.key = "void_impact"; e.name = "Void bolt impact";
		e.lore = "Where the bolt lands, the world forgets itself for a heartbeat.";
		ModelData m = new ModelData();
		int f;
		m.skin = 0; f = nf(m);
		gem(m, 0, 0, 0, 13, 15, 6, 0, ConfigDefinitions.voidRamp(16, 4), ConfigDefinitions.voidRamp(8, 3));
		tag(m, f, 0, 0, 2);
		double[][] dirs = { { 1, 0, 0 }, { -1, 0, 0 }, { 0, -1, 0 }, { 0, 1, 0 }, { 0, 0, 1 }, { 0, 0, -1 } };
		for (int d = 0; d < 6; d++) {
			m.skin = 1 + d; f = nf(m);
			double[] p0 = { dirs[d][0] * 8, dirs[d][1] * 8, dirs[d][2] * 8 }, p1 = { dirs[d][0] * 44, dirs[d][1] * 44, dirs[d][2] * 44 };
			m.tube(p0, p1, 7, 0, 4, Math.PI / 4, d % 2 == 0 ? ConfigDefinitions.voidRamp(74, 6) : ConfigDefinitions.voidRamp(56, 7), ConfigDefinitions.voidRamp(56, 7));
			tag(m, f, 1 + d, 56, 1);
		}
		m.skin = 7; f = nf(m);
		int[] in = yring(m, 0, 26, 8, Math.PI / 8), out = yring(m, 0, 36, 8, Math.PI / 8);
		for (int s = 0; s < 8; s++) quad2(m, in[s], in[(s + 1) % 8], out[(s + 1) % 8], out[s], s % 2 == 0 ? ConfigDefinitions.voidRamp(60, 7) : ConfigDefinitions.voidRamp(48, 7));
		tag(m, f, 0, 96, 0);
		e.model = m;
		e.reuseSeq = 693; e.reuseNote = "fire wave impact (gfx 157/model 3116), frame file 186";
		// authored: 0 O[all] 1 S[core] 2 S[spikes] 3 R[spikes] 4 O[halo] 5 S[halo] 6 A[core+halo] 7 A[spikes]
		e.skins = FxAnim.skinList(new int[] { 0, 3, 3, 2, 0, 3, 5, 5 },
				new int[][] { range(0, 7), { 0 }, range(1, 6), range(1, 6), { 7 }, { 7 }, { 0 }, range(1, 6) });
		int[] core = { 40, 96, 136, 152, 140, 110, 70, 30 }, spk = { 20, 80, 140, 180, 210, 228, 238, 242 },
				halo = { 30, 70, 112, 150, 190, 222, 248, 266 }, ac = { 0, 0, 0, 4, 10, 16, 24, 30 }, as = { 12, 4, 0, 0, 6, 14, 22, 30 };
		for (int i = 0; i < 8; i++) {
			e.keys.add(new FxAnim.Key(2).op(0, 0, 0, 0).op(1, core[i], core[i], core[i]).op(2, spk[i], spk[i], spk[i]).op(3, 0, i * 6, 0)
					.op(4, 0, 0, 0).op(5, halo[i], 128, halo[i]).op(6, ac[i], 0, 0).op(7, as[i], 0, 0));
		}
		e.brightness = 60; e.shadow = 60; e.height = 100;
		e.scale = 1.0; e.oy = 80;
		e.labelMap = "vskin 0 core, 1-6 spikes (+x,-x,up,down,+z,-z), 7 halo; fskin 0 core+halo, 1-6 spikes";
		return e;
	}

	/* ============================================================ 3. EMBERBRAND SPECIAL: "Heartfire Arc" */
	/*
	 * A crescent ribbon of ember light swept diagonally in front of the wielder: a hot gold outer edge over a
	 * translucent orange inner glow, tapering to points. Eight segments with face skins 0-7 in sweep order, exactly
	 * like the dragon dagger special's slash ribbon (gfx 252, model 3478) so seq 1063 (file 247, alpha-only)
	 * reveals and fades them one after another with no new frames.
	 */
	public static Effect emberArc() {
		Effect e = new Effect();
		e.key = "ember_arc"; e.name = "Emberbrand special: Heartfire Arc";
		e.lore = "The Ember remembers every king who held it. For one swing, so does the blade.";
		ModelData m = new ModelData();
		int segs = 8;
		double R = 92, cy = -104;
		int[][] sec = new int[segs + 1][4];
		for (int k = 0; k <= segs; k++) {
			double t = (double) k / segs;
			double phi = Math.toRadians(15 + 150 * t);
			double w = 4 + 18 * Math.sin(Math.PI * Math.pow(t, 0.8));
			m.skin = k;
			// arc plane: x across the body, tilted toward the front (-z) and dropping from upper-right to lower-left
			double[] rad = { 0, 0, 0 };
			for (int j = 0; j < 4; j++) {
				double rr = j == 0 ? R + w : j == 1 ? R : j == 2 ? R - w * 0.9 : R - w * 1.6;
				double x = Math.cos(phi) * rr;
				double fwd = Math.sin(phi) * rr;
				double y = cy - Math.cos(phi) * 46 + fwd * 0.18;
				sec[k][j] = m.vertex(x, y, -fwd * 0.92 - 18);
			}
		}
		for (int k = 0; k < segs; k++) {
			int f = nf(m);
			quad2(m, sec[k][0], sec[k + 1][0], sec[k + 1][1], sec[k][1], hsl(9, 7, 82));
			tag(m, f, k, 0, 10);
			f = nf(m);
			quad2(m, sec[k][1], sec[k + 1][1], sec[k + 1][2], sec[k][2], hsl(7, 7, 62));
			tag(m, f, k, 40, 10);
			f = nf(m);
			quad2(m, sec[k][2], sec[k + 1][2], sec[k + 1][3], sec[k][3], hsl(5, 7, 44));
			tag(m, f, k, 120, 10);
		}
		e.model = m;
		e.reuseSeq = 1063; e.reuseNote = "dragon dagger special (gfx 252/model 3478), frame file 247";
		// authored: 0 O[all] 1 R[all] (follow-through roll) 2..9 A[segment k]
		int[] types = new int[10]; int[][] labs = new int[10][];
		types[0] = 0; labs[0] = range(0, 8); types[1] = 2; labs[1] = range(0, 8);
		for (int k = 0; k < 8; k++) { types[2 + k] = 5; labs[2 + k] = new int[] { k }; }
		e.skins = FxAnim.skinList(types, labs);
		for (int i = 0; i < 9; i++) {
			FxAnim.Key key = new FxAnim.Key(i < 6 ? 1 : 3);
			key.op(0, 0, 0, 0).op(1, 0, (i - 4) & 0xff, 0);
			double head = i * 1.25;
			for (int k = 0; k < 8; k++) {
				int a;
				if (k > head + 0.5) a = 32;              // not reached yet
				else a = (int) Math.min(32, Math.max(0, (head - k - 1.2) * 9)); // trailing fade behind the head
				if (i == 8) a = Math.max(a, 26);
				if (a != 0) key.op(2 + k, a, 0, 0);
			}
			e.keys.add(key);
		}
		e.brightness = 80; e.shadow = 80; e.height = 0;
		e.yaw = 0.35; e.pitch = 0.5; e.scale = 0.62; e.oy = 120;
		e.labelMap = "vskin k = cross-section k (0-8, origin only); fskin k = segment k (0-7) in sweep order";
		return e;
	}

	/* ============================================================ 4. STARFALL MARKER */
	/*
	 * A ground sigil (8-point star in a ring) of pale starmetal and gold, with three star motes hovering over it:
	 * "something is falling here". Laid out for the ice rush impact's sequence (gfx 361, seq 1959 / file 521):
	 * label 0 (sigil) grows 12% -> 94% while fading, labels 1-3 (motes) pop in one after another.
	 */
	public static Effect starfallMarker() {
		Effect e = new Effect();
		e.key = "starfall_marker"; e.name = "Starfall marker";
		e.lore = "Old Gatehold astronomers drew this sign where the sky was about to answer.";
		ModelData m = new ModelData();
		int f;
		m.skin = 0; f = nf(m);
		int n = 16;
		int[] in = yring(m, -2, 54, n, 0), out = yring(m, -2, 62, n, 0);
		for (int s = 0; s < n; s++) m.quad(in[s], out[s], out[(s + 1) % n], in[(s + 1) % n], s % 2 == 0 ? hsl(9, 6, 72) : hsl(27, 1, 74));
		int c = m.vertex(0, -2, 0);
		for (int p = 0; p < 8; p++) {
			double a = Math.PI * 2 * p / 8, len = p % 2 == 0 ? 80 : 48, wid = Math.PI / 8 * (p % 2 == 0 ? 0.55 : 0.45);
			int tipv = m.vertex(Math.cos(a) * len, -2, Math.sin(a) * len);
			int l = m.vertex(Math.cos(a - wid) * 18, -2, Math.sin(a - wid) * 18), r = m.vertex(Math.cos(a + wid) * 18, -2, Math.sin(a + wid) * 18);
			m.tri(c, r, tipv, p % 2 == 0 ? hsl(9, 6, 80) : hsl(8, 5, 64));
			m.tri(c, tipv, l, p % 2 == 0 ? hsl(9, 5, 70) : hsl(8, 5, 56));
		}
		// make every ground face point up (toward the camera)
		for (int i = f; i < nf(m); i++) {
			int[] fc = m.faces.get(i);
			int[] A = m.vertices.get(fc[0]), B = m.vertices.get(fc[1]), C = m.vertices.get(fc[2]);
			double ny = (B[2] - A[2]) * (C[0] - A[0]) - (B[0] - A[0]) * (C[2] - A[2]);
			if (ny > 0) { int t = fc[1]; fc[1] = fc[2]; fc[2] = t; }
		}
		tag(m, f, 0, 40, 0);
		for (int k = 0; k < 3; k++) {
			double a = Math.PI * 2 * k / 3 - Math.PI / 2;
			m.skin = 1 + k; f = nf(m);
			gem(m, Math.cos(a) * 40, -30, Math.sin(a) * 40, 9, 15, 4, 0, hsl(9, 4, 100), hsl(9, 6, 76));
			tag(m, f, 1 + k, 0, 1);
		}
		e.model = m;
		e.reuseSeq = 1959; e.reuseNote = "ice rush impact (gfx 361/model 6387), frame file 521";
		// authored loop: 0 O[0] 1 R[0] 2 S[0] 3 A[0] then per mote O/T/S
		e.skins = FxAnim.skinList(new int[] { 0, 2, 3, 5, 0, 1, 3, 0, 1, 3, 0, 1, 3 },
				new int[][] { { 0 }, { 0 }, { 0 }, { 0 }, { 1 }, { 1 }, { 1 }, { 2 }, { 2 }, { 2 }, { 3 }, { 3 }, { 3 } });
		for (int i = 0; i < 8; i++) {
			double ph = Math.PI * 2 * i / 8;
			int s = 128 + (int) (10 * Math.sin(ph));
			FxAnim.Key k = new FxAnim.Key(4).op(1, 0, i * 4, 0).op(2, s, 128, s).op(3, (int) (4 + 4 * Math.sin(ph)), 0, 0);
			for (int j = 0; j < 3; j++) {
				double q = ph + j * 2.1;
				int sc = 104 + (int) (40 * Math.max(0, Math.sin(q)));
				k.op(5 + j * 3, 0, (int) (-8 * Math.sin(q)), 0).op(6 + j * 3, sc, sc, sc);
			}
			e.keys.add(k);
		}
		e.brightness = 70; e.shadow = 70; e.height = 0;
		e.yaw = 0.3; e.pitch = 0.75; e.scale = 0.75; e.oy = 90;
		e.labelMap = "vskin/fskin 0 ground sigil, 1-3 star motes";
		return e;
	}

	/* ============================================================ 5. FALLING STAR IMPACT */
	/*
	 * A chunky starmetal star (gold-white heart, pale-steel points) falls down a column of light that fades from
	 * white-gold at the ground to violet at the top, then a gold splash flares on the ground. Laid out like gfx 437
	 * (model 9250, seq 2597 / file 657): vertex labels 0-8 are rings of the light column at the same heights,
	 * label 9 is the falling object (seq 2597 translates it from 583 above to 75 below its base and spins it),
	 * face skins 0-8 are the column segments lit top to bottom, face skin 0 + label 0 also carry the ground splash.
	 */
	public static Effect fallingStar() {
		Effect e = new Effect();
		e.key = "falling_star"; e.name = "Falling star impact";
		e.lore = "Starmetal does not fall so much as arrive. The ground is never quite the same afterwards.";
		ModelData m = new ModelData();
		double[] hs = { -2, -13, -34, -69, -122, -177, -240, -328, -494, -720 };
		int[] cols = { hsl(9, 4, 100), hsl(9, 5, 94), hsl(9, 6, 86), hsl(9, 6, 78), hsl(8, 6, 70), hsl(10, 4, 64), hsl(48, 3, 60),
				ConfigDefinitions.voidRamp(54, 5), ConfigDefinitions.voidRamp(44, 7) };
		int[][] rings = new int[hs.length][];
		for (int k = 0; k < hs.length; k++) {
			m.skin = k < 9 ? k : 10;
			double r = 7 + 9 * k / 9.0;
			rings[k] = yring(m, hs[k], r, 4, Math.PI / 4);
		}
		for (int k = 0; k < 9; k++) {
			int f = nf(m);
			band(m, rings[k], rings[k + 1], cols[k], new double[] { 0, (hs[k] + hs[k + 1]) / 2, 0 });
			tag(m, f, k, 72, 0);
		}
		// ground splash on label 0 / face skin 0
		m.skin = 0;
		int f = nf(m);
		int c = m.vertex(0, -3, 0);
		for (int p = 0; p < 8; p++) {
			double a = Math.PI * 2 * p / 8 + Math.PI / 8, len = p % 2 == 0 ? 84 : 52;
			int tipv = m.vertex(Math.cos(a) * len, -3, Math.sin(a) * len);
			int l = m.vertex(Math.cos(a - 0.36) * 26, -3, Math.sin(a - 0.36) * 26), r = m.vertex(Math.cos(a + 0.36) * 26, -3, Math.sin(a + 0.36) * 26);
			m.tri(c, tipv, r, p % 2 == 0 ? hsl(8, 7, 80) : hsl(6, 7, 58));
			m.tri(c, l, tipv, p % 2 == 0 ? hsl(8, 7, 70) : hsl(6, 7, 50));
		}
		for (int i = f; i < nf(m); i++) {
			int[] fc = m.faces.get(i);
			int[] A = m.vertices.get(fc[0]), B = m.vertices.get(fc[1]), C = m.vertices.get(fc[2]);
			double ny = (B[2] - A[2]) * (C[0] - A[0]) - (B[0] - A[0]) * (C[2] - A[2]);
			if (ny > 0) { int t = fc[1]; fc[1] = fc[2]; fc[2] = t; }
		}
		tag(m, f, 0, 32, 1);
		// the star itself: label 9 / face skin 9, centred where model 9250 keeps its falling object (y ~ -84)
		m.skin = 9;
		f = nf(m);
		gem(m, 0, -84, 0, 22, 24, 6, 0, hsl(9, 6, 96), hsl(9, 7, 74));
		double[][] dirs = { { 1, 0, 0 }, { -1, 0, 0 }, { 0, 0, 1 }, { 0, 0, -1 }, { 0, -1, 0 }, { 0, 1, 0 } };
		for (double[] d : dirs) {
			m.tube(new double[] { d[0] * 10, -84 + d[1] * 10, d[2] * 10 }, new double[] { d[0] * 54, -84 + d[1] * 54, d[2] * 54 }, 11, 0, 4,
					Math.PI / 4, hsl(27, 1, 90), hsl(27, 1, 72));
		}
		tag(m, f, 9, 0, 2);
		e.model = m;
		e.reuseSeq = 2597; e.reuseNote = "gfx 437 (model 9250), frame file 657";
		// authored: 0 O[9] 1 T[9] 2 R[9] 3 A[9] 4 O[0] 5 S[0] 6 A[0] 7 A[1..8]
		e.skins = FxAnim.skinList(new int[] { 0, 1, 2, 5, 0, 3, 5, 5 },
				new int[][] { { 9 }, { 9 }, { 9 }, { 9 }, { 0 }, { 0 }, { 0 }, range(1, 8) });
		int[] ty = { -640, -500, -370, -250, -140, -50, 70, 78, 78, 78 };
		int[] spl = { 0, 0, 0, 0, 0, 0, 90, 150, 190, 215 }, aspl = { 32, 32, 32, 32, 32, 32, 0, 4, 14, 26 };
		int[] astar = { 14, 6, 0, 0, 0, 0, 0, 10, 32, 32 }, atrail = { 16, 8, 2, 0, 0, 2, 6, 14, 24, 32 };
		for (int i = 0; i < 10; i++) {
			e.keys.add(new FxAnim.Key(i < 6 ? 2 : 3).op(0, 0, 0, 0).op(1, 0, ty[i], 0).op(2, 0, i * 20 & 0xff, i * 6).op(3, astar[i], 0, 0)
					.op(4, 0, 0, 0).op(5, spl[i] == 0 ? 1 : spl[i], 128, spl[i] == 0 ? 1 : spl[i]).op(6, aspl[i], 0, 0).op(7, atrail[i], 0, 0));
		}
		e.brightness = 90; e.shadow = 90; e.height = 0;
		e.yaw = 0.5; e.pitch = 0.45; e.scale = 0.21; e.oy = 146;
		e.labelMap = "vskin 0-8 light-column rings (heights as model 9250), 10 column top, 9 the star; fskin 0-8 column segments (0 also the ground splash), 9 star";
		return e;
	}

	/* ============================================================ 6. ECLIPSE RING */
	/*
	 * Eight arcs of gold corona light ring the target and close in on it while a black sun with a violet corona
	 * swells at the centre - the Starless swallowing the light. Reuses the strike impact (seq 660 / file 202,
	 * gfx 92-101) with an inversion trick: each arc segment is given the shard label whose outward push points
	 * at the CENTRE from where the segment sits, so the "fly outward" frames make the ring contract instead.
	 */
	public static Effect eclipseRing() {
		Effect e = new Effect();
		e.key = "eclipse_ring"; e.name = "Eclipse ring";
		e.lore = "When the Starless turns its face toward you, the sky closes like an eye.";
		ModelData m = new ModelData();
		int f;
		m.skin = 0; f = nf(m);
		gem(m, 0, 0, 0, 15, 15, 8, 0, ConfigDefinitions.voidRamp(10, 3), ConfigDefinitions.voidRamp(5, 2));
		tag(m, f, 0, 0, 2);
		m.skin = 1; f = nf(m);
		int[] ci = yring(m, 0, 15, 8, Math.PI / 8), co = yring(m, 0, 27, 16, 0);
		for (int s = 0; s < 8; s++) {
			int a = ci[s], b = ci[(s + 1) % 8], tipv = co[2 * s + 1];
			m.tri(a, tipv, b, ConfigDefinitions.voidRamp(54, 7));
			m.tri(a, b, tipv, ConfigDefinitions.voidRamp(40, 7));
		}
		tag(m, f, 1, 72, 1);
		StringBuilder map = new StringBuilder();
		for (int s = 0; s < 8; s++) {
			double a0 = Math.PI * 2 * s / 8 - Math.PI / 8 + 0.09, a1 = Math.PI * 2 * (s + 1) / 8 - Math.PI / 8 - 0.09, am = (a0 + a1) / 2;
			// pick the seq-660 shard label whose outward push points back at the centre from here
			int best = 2; double bd = -9;
			for (int l = 2; l <= 9; l++) {
				double d = -(SHARD_DIR[l][0] * Math.cos(am) + SHARD_DIR[l][1] * Math.sin(am));
				if (d > bd) { bd = d; best = l; }
			}
			map.append(String.format("arc@%d deg->label %d ", Math.round(Math.toDegrees(am)), best));
			m.skin = best; f = nf(m);
			int steps = 2;
			int[][] pts = new int[steps + 1][4];
			for (int j = 0; j <= steps; j++) {
				double a = a0 + (a1 - a0) * j / steps;
				double ri = 62, ro = 74;
				pts[j][0] = m.vertex(Math.cos(a) * ri, -5, Math.sin(a) * ri);
				pts[j][1] = m.vertex(Math.cos(a) * ro, -5, Math.sin(a) * ro);
				pts[j][2] = m.vertex(Math.cos(a) * ro, 5, Math.sin(a) * ro);
				pts[j][3] = m.vertex(Math.cos(a) * ri, 5, Math.sin(a) * ri);
			}
			double[] ref = { Math.cos(am) * 68, 0, Math.sin(am) * 68 };
			for (int j = 0; j < steps; j++) {
				m.quadOut(pts[j][0], pts[j + 1][0], pts[j + 1][1], pts[j][1], hsl(9, 6, 84), ref); // top
				m.quadOut(pts[j][1], pts[j + 1][1], pts[j + 1][2], pts[j][2], hsl(7, 7, 60), ref);  // outer
				m.quadOut(pts[j][3], pts[j + 1][3], pts[j + 1][0], pts[j][0], hsl(8, 6, 72), ref);  // inner
				m.quadOut(pts[j][2], pts[j + 1][2], pts[j + 1][3], pts[j][3], hsl(6, 6, 40), ref);  // bottom
			}
			m.quadOut(pts[0][0], pts[0][1], pts[0][2], pts[0][3], hsl(7, 6, 56), ref);
			m.quadOut(pts[steps][0], pts[steps][1], pts[steps][2], pts[steps][3], hsl(7, 6, 56), ref);
			tag(m, f, best, 0, 0);
		}
		e.model = m;
		e.reuseSeq = 660; e.reuseNote = "strike impact (gfx 92/95/98/101, model 3082), frame file 202 - shard labels inverted";
		e.labelMap = "vskin/fskin 0 black sun, 1 corona, 2-9 arc segments: " + map;
		// authored: 0 O[2..9] 1 R[2..9] 2 O[0] 3 S[0] 4 A[0] 5 O[1] 6 S[1] 7 R[1] 8 A[1] 9..16 T[2..9] 17 A[2..9]
		int[] types = new int[18]; int[][] labs = new int[18][];
		types[0] = 0; labs[0] = range(2, 9); types[1] = 2; labs[1] = range(2, 9);
		types[2] = 0; labs[2] = new int[] { 0 }; types[3] = 3; labs[3] = new int[] { 0 }; types[4] = 5; labs[4] = new int[] { 0 };
		types[5] = 0; labs[5] = new int[] { 1 }; types[6] = 3; labs[6] = new int[] { 1 }; types[7] = 2; labs[7] = new int[] { 1 };
		types[8] = 5; labs[8] = new int[] { 1 };
		for (int l = 2; l <= 9; l++) { types[7 + l] = 1; labs[7 + l] = new int[] { l }; }
		types[17] = 5; labs[17] = range(2, 9);
		e.skins = FxAnim.skinList(types, labs);
		double[] close = { 0, 0.05, 0.14, 0.27, 0.42, 0.58, 0.72, 0.84, 0.92, 0.97 };
		int[] sun = { 20, 34, 52, 72, 92, 112, 132, 150, 190, 60 }, cor = { 30, 50, 70, 90, 110, 130, 150, 170, 260, 330 };
		int[] aRing = { 20, 10, 4, 0, 0, 0, 0, 4, 18, 32 }, aSun = { 16, 6, 0, 0, 0, 0, 0, 0, 0, 24 }, aCor = { 20, 12, 6, 2, 0, 0, 0, 0, 8, 28 };
		for (int i = 0; i < 10; i++) {
			FxAnim.Key k = new FxAnim.Key(i < 8 ? 3 : 2);
			k.op(0, 0, 0, 0).op(1, 0, i * 5 & 0xff, 0).op(2, 0, 0, 0).op(3, sun[i], sun[i], sun[i]).op(4, aSun[i], 0, 0);
			k.op(5, 0, 0, 0).op(6, cor[i], 128, cor[i]).op(7, 0, i * 12 & 0xff, 0).op(8, aCor[i], 0, 0);
			for (int l = 2; l <= 9; l++) {
				// inward along the segment's own direction (= the opposite of the shard label's outward push)
				double d = 50 * close[i];
				k.op(7 + l, (int) Math.round(SHARD_DIR[l][0] * d), 0, (int) Math.round(SHARD_DIR[l][1] * d));
			}
			k.op(17, aRing[i], 0, 0);
			e.keys.add(k);
		}
		e.brightness = 60; e.shadow = 60; e.height = 0; e.resizeXY = 160; e.resizeZ = 160;
		e.yaw = 0.3; e.pitch = 0.7; e.scale = 0.62; e.oy = 85;
		return e;
	}

	/* ============================================================ 7. RIME BURST */
	/*
	 * A pale ice-crystal heart in a frosted shell, with eight long ice shards that burst outward, alternately tilted
	 * up and down. Mirrors the strike impact model (3082) label for label so seq 660 (file 202) drives it unchanged:
	 * 0 core (scaled 30%->300% and faded), 1 shell, 2-9 shards pushed outward in the eight compass directions.
	 */
	public static Effect rimeBurst() {
		Effect e = new Effect();
		e.key = "rime_burst"; e.name = "Rime burst";
		e.lore = "Rime ice from the Starless edge: cold enough to crack the air it freezes.";
		ModelData m = new ModelData();
		int f;
		m.skin = 0; f = nf(m);
		gem(m, 0, 0, 0, 9, 13, 6, 0, hsl(36, 4, 104), hsl(36, 5, 84));
		tag(m, f, 0, 0, 2);
		m.skin = 1; f = nf(m);
		gem(m, 0, 0, 0, 17, 15, 8, Math.PI / 8, hsl(36, 4, 88), hsl(36, 5, 70));
		tag(m, f, 1, 96, 1);
		for (int l = 2; l <= 9; l++) {
			double dx = SHARD_DIR[l][0], dz = SHARD_DIR[l][1], up = l % 2 == 0 ? -1 : 1;
			m.skin = l; f = nf(m);
			double[] p0 = { dx * 10, up * 2, dz * 10 }, p1 = { dx * 36, up * 11, dz * 36 };
			m.tube(p0, p1, 5, 0, 3, l * 0.7, l % 2 == 0 ? hsl(36, 6, 80) : hsl(36, 7, 60), hsl(36, 6, 70));
			tag(m, f, l, 40, 0);
		}
		e.model = m;
		e.reuseSeq = 660; e.reuseNote = "strike impact (gfx 92/95/98/101, model 3082), frame file 202";
		// authored: 0 O[0] 1 S[0] 2 R[0] 3 A[0] 4 O[1] 5 S[1] 6 A[1] 7..14 T[2..9] 15 A[2..9]
		int[] types = new int[16]; int[][] labs = new int[16][];
		types[0] = 0; labs[0] = new int[] { 0 }; types[1] = 3; labs[1] = new int[] { 0 }; types[2] = 2; labs[2] = new int[] { 0 };
		types[3] = 5; labs[3] = new int[] { 0 }; types[4] = 0; labs[4] = new int[] { 1 }; types[5] = 3; labs[5] = new int[] { 1 };
		types[6] = 5; labs[6] = new int[] { 1 };
		for (int l = 2; l <= 9; l++) { types[5 + l] = 1; labs[5 + l] = new int[] { l }; }
		types[15] = 5; labs[15] = range(2, 9);
		e.skins = FxAnim.skinList(types, labs);
		int[] core = { 50, 130, 170, 150, 110, 70, 36, 12, 1, 1 }, shell = { 40, 100, 160, 210, 250, 280, 300, 316, 328, 336 };
		int[] aCore = { 0, 0, 0, 0, 4, 10, 18, 26, 32, 32 }, aShell = { 0, 0, 2, 6, 10, 14, 18, 22, 26, 32 };
		int[] out = { 0, 8, 20, 32, 42, 50, 56, 60, 63, 65 }, drop = { 0, -4, -6, -6, -4, 0, 4, 9, 14, 20 }, aSh = { 12, 0, 0, 0, 2, 6, 12, 18, 24, 32 };
		for (int i = 0; i < 10; i++) {
			FxAnim.Key k = new FxAnim.Key(2).op(0, 0, 0, 0).op(1, core[i], core[i], core[i]).op(2, 0, i * 10, 0).op(3, aCore[i], 0, 0)
					.op(4, 0, 0, 0).op(5, shell[i], shell[i] * 3 / 4, shell[i]).op(6, aShell[i], 0, 0);
			for (int l = 2; l <= 9; l++) k.op(5 + l, (int) Math.round(SHARD_DIR[l][0] * out[i]), drop[i], (int) Math.round(SHARD_DIR[l][1] * out[i]));
			k.op(15, aSh[i], 0, 0);
			e.keys.add(k);
		}
		e.brightness = 60; e.shadow = 60; e.height = 100; e.resizeXY = 192; e.resizeZ = 192;
		e.scale = 0.75; e.oy = 80;
		e.labelMap = "vskin/fskin 0 ice heart, 1 frost shell, 2-9 shards (N, NE, E, SE, S, SW, W, NW in seq 660's push directions)";
		return e;
	}

	public static List<Effect> all() {
		return List.of(voidBolt(), voidImpact(), emberArc(), starfallMarker(), fallingStar(), eclipseRing(), rimeBurst());
	}
}
