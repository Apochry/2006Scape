package com.rs2.tools.mapgen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The shared rig behind the hand-animated creatures: a joint hierarchy turned into a 317 skin list, poses authored in
 * degrees and quantised to what a frame can hold, the frames' ops, and the cache installer. It is {@link Shardback}'s
 * approach made general (the Shardback keeps its own copy, which this reuses the helpers of): a later creature
 * declares its bones and writes poses, and gets frames the client plays exactly as {@link FxAnim} poses them.
 *
 * <h3>How the hierarchy works</h3> A 317 frame is a flat list of transforms on vertex-label groups, applied in skin-list
 * order. Each joint gets an origin slot with no labels (so the frame's x/y/z is an absolute pivot: its rest position)
 * and a rotate slot on every label of its subtree. Listing children before parents makes a child turn about its rest
 * pivot first and then be carried by every parent: forward kinematics, in the frame format. A joint can also have a
 * scale slot (about its pivot) and a translate slot; the root's translate comes last, so it moves the posed creature.
 *
 * <p>Coordinates are model units (128 = a tile), y down (negative is up), ground at y = 0, the creature facing -z.
 * Angles: pitch (x, + = a forward-pointing chain's tip goes down), yaw (y, + = +z turns towards +x), roll (z).
 */
final class CreatureRig {

	/** A joint: the labels it moves (its own and its children's) and where it turns in the rest pose. */
	static final class Bone {
		final String name;
		final int label;
		final Bone parent;
		final double[] pivot;
		final List<Bone> kids = new ArrayList<>();
		boolean scales, moves;
		int rotSlot = -1, scaleSlot = -1, moveSlot = -1;
		int[] labels;

		Bone(String name, int label, Bone parent, double[] pivot) {
			this.name = name;
			this.label = label;
			this.parent = parent;
			this.pivot = pivot;
			if (parent != null) {
				parent.kids.add(this);
			}
		}

		void collect(List<Integer> out) {
			out.add(label);
			for (Bone k : kids) {
				k.collect(out);
			}
		}

		boolean moves(int vertexLabel) {
			return Arrays.binarySearch(labels, vertexLabel) >= 0;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	final List<Bone> bones = new ArrayList<>();
	Bone root;
	/** Skin-list order: every bone after all of its descendants. */
	List<Bone> order;
	AnimCache.SkinList skins;

	Bone bone(String name, int label, Bone parent, double... pivot) {
		Bone b = new Bone(name, label, parent, pivot);
		if (parent == null) {
			if (root != null) {
				throw new IllegalStateException("two roots: " + root + ", " + b);
			}
			root = b;
		}
		bones.add(b);
		return b;
	}

	/** Builds the skin list; {@code order} must list every bone, each after all of its descendants. */
	CreatureRig build(List<Bone> order) {
		if (order.size() != bones.size() || !new HashSet<>(order).containsAll(bones)) {
			throw new IllegalStateException("the skin order must list every bone once");
		}
		Set<Bone> seen = new HashSet<>();
		for (Bone b : order) {
			for (Bone k : b.kids) {
				if (!seen.contains(k)) {
					throw new IllegalStateException(k + " must come before its parent " + b);
				}
			}
			seen.add(b);
		}
		this.order = order;
		List<Integer> types = new ArrayList<>();
		List<int[]> labels = new ArrayList<>();
		for (Bone b : order) {
			List<Integer> l = new ArrayList<>();
			b.collect(l);
			b.labels = l.stream().mapToInt(Integer::intValue).sorted().toArray();
			if (b.moves && b != root) {
				b.moveSlot = slot(types, labels, 1, b.labels);
			}
			if (b.scales) {
				slot(types, labels, 0, new int[0]);
				b.scaleSlot = slot(types, labels, 3, b.labels);
			}
			slot(types, labels, 0, new int[0]); // an origin with no labels: its x/y/z is the pivot itself
			b.rotSlot = slot(types, labels, 2, b.labels);
			if (b == root) {
				b.moveSlot = slot(types, labels, 1, b.labels);
			}
		}
		skins = FxAnim.skinList(types.stream().mapToInt(Integer::intValue).toArray(), labels.toArray(new int[0][]));
		return this;
	}

	private static int slot(List<Integer> types, List<int[]> labels, int type, int[] l) {
		types.add(type);
		labels.add(l);
		return types.size() - 1;
	}

	/* ====================================================================== */
	/*                                  POSES                                 */
	/* ====================================================================== */

	/** One key pose, in degrees, scale factors and model units. Bones it doesn't mention stay at rest. */
	static final class Pose {
		int delay;
		final Map<Bone, double[]> rot = new LinkedHashMap<>(), scale = new LinkedHashMap<>(), move = new LinkedHashMap<>();

		Pose(int delay) {
			this.delay = delay;
		}

		Pose rot(Bone b, double pitch, double yaw, double roll) {
			rot.put(b, new double[] { pitch, yaw, roll });
			return this;
		}

		/** Adds to a bone's rotation. */
		Pose add(Bone b, double pitch, double yaw, double roll) {
			double[] r = rot.computeIfAbsent(b, k -> new double[3]);
			r[0] += pitch;
			r[1] += yaw;
			r[2] += roll;
			return this;
		}

		Pose scale(Bone b, double x, double y, double z) {
			scale.put(b, new double[] { x, y, z });
			return this;
		}

		Pose move(Bone b, double x, double y, double z) {
			move.put(b, new double[] { x, y, z });
			return this;
		}

		double[] rotOf(Bone b) {
			return rot.getOrDefault(b, new double[3]);
		}
	}

	/** A pose quantised to the frame's integers: rotations in 1/256 turns, scales in 1/128, moves in units. */
	static final class Q {
		final int delay;
		final Map<Bone, int[]> rot = new LinkedHashMap<>(), scale = new LinkedHashMap<>(), move = new LinkedHashMap<>();

		Q(int delay) {
			this.delay = delay;
		}

		int[] rot(Bone b) {
			return rot.computeIfAbsent(b, k -> new int[3]);
		}
	}

	Q quantise(Pose p) {
		Q q = new Q(p.delay);
		p.rot.forEach((b, a) -> q.rot.put(b, new int[] { Shardback.units(a[0]), Shardback.units(a[1]), Shardback.units(a[2]) }));
		p.scale.forEach((b, s) -> q.scale.put(b, new int[] { (int) Math.round(128 * s[0]), (int) Math.round(128 * s[1]), (int) Math.round(128 * s[2]) }));
		p.move.forEach((b, m) -> q.move.put(b, new int[] { (int) Math.round(m[0]), (int) Math.round(m[1]), (int) Math.round(m[2]) }));
		return q;
	}

	/** The frame: each bone's ops in skin-list order, leaving out the ones at rest. */
	FxAnim.Key key(Q q) {
		FxAnim.Key key = new FxAnim.Key(q.delay);
		for (Bone b : order) {
			int[] m = q.move.get(b);
			boolean moved = m != null && (m[0] != 0 || m[1] != 0 || m[2] != 0);
			if (moved && b != root) {
				key.op(b.moveSlot, m[0], m[1], m[2]);
			}
			int[] s = q.scale.get(b);
			if (b.scales && s != null && (s[0] != 128 || s[1] != 128 || s[2] != 128)) {
				origin(key, b.scaleSlot - 1, b.pivot);
				key.op(b.scaleSlot, s[0], s[1], s[2]);
			}
			int[] r = q.rot.get(b);
			if (r != null && (r[0] != 0 || r[1] != 0 || r[2] != 0)) {
				origin(key, b.rotSlot - 1, b.pivot);
				key.op(b.rotSlot, r[0], r[1], r[2]);
			}
			if (moved && b == root) {
				key.op(b.moveSlot, m[0], m[1], m[2]);
			}
		}
		return key;
	}

	private static void origin(FxAnim.Key key, int slot, double[] p) {
		key.op(slot, (int) Math.round(p[0]), (int) Math.round(p[1]), (int) Math.round(p[2]));
	}

	/** {@code base} posed by one quantised pose, exactly as the client poses it. */
	ModelData pose(ModelData base, Q q) {
		return FxAnim.pose(base, FxAnim.frames(skins, List.of(key(q))).get(0));
	}

	/** Every frame of a sequence posed from its encoded frames, exactly as the client poses them. */
	List<ModelData> posed(ModelData base, List<FxAnim.Key> keys) {
		List<ModelData> out = new ArrayList<>();
		for (AnimCache.Frame f : FxAnim.frames(skins, keys)) {
			out.add(FxAnim.pose(base, f));
		}
		return out;
	}

	/**
	 * Where a rest-pose point carried by {@code owner} ends up under a quantised pose (in doubles, for planning; the
	 * client's fixed-point rounding can differ by a unit or so - the checks use {@link #pose}).
	 */
	double[] point(Q q, Bone owner, double[] rest) {
		double[] v = rest.clone();
		for (Bone b : order) {
			if (!b.moves(owner.label)) {
				continue;
			}
			int[] m = q.move.get(b);
			if (m != null && b != root) {
				v = new double[] { v[0] + m[0], v[1] + m[1], v[2] + m[2] };
			}
			int[] s = q.scale.get(b);
			if (b.scales && s != null) {
				for (int i = 0; i < 3; i++) {
					v[i] = (v[i] - b.pivot[i]) * s[i] / 128.0 + b.pivot[i];
				}
			}
			int[] r = q.rot.get(b);
			if (r != null) {
				v = Shardback.rotate(v, b.pivot, r[0], r[1], r[2]);
			}
			if (m != null && b == root) {
				v = new double[] { v[0] + m[0], v[1] + m[1], v[2] + m[2] };
			}
		}
		return v;
	}

	/** The lowest point (largest y) among the vertices carrying any of {@code labels}. */
	static int lowest(ModelData m, int... labels) {
		int low = Integer.MIN_VALUE;
		for (int i = 0; i < m.vertices.size(); i++) {
			int l = m.vertexSkins.get(i);
			for (int x : labels) {
				if (l == x) {
					low = Math.max(low, m.vertices.get(i)[1]);
					break;
				}
			}
		}
		return low;
	}

	/** The lowest point of the whole model. */
	static int lowest(ModelData m) {
		int low = Integer.MIN_VALUE;
		for (int[] v : m.vertices) {
			low = Math.max(low, v[1]);
		}
		return low;
	}

	/**
	 * The worst stretch of any edge joining two different bones, posed against rest (1 = unchanged): a seam tearing
	 * open or a part flying off shows up as a large ratio, a joint folding through itself as a small one.
	 */
	static double[] seamStretch(ModelData rest, ModelData posed) {
		double lo = 1, hi = 1;
		for (int[] f : rest.faces) {
			for (int e = 0; e < 3; e++) {
				int a = f[e], b = f[(e + 1) % 3];
				if (rest.vertexSkins.get(a).equals(rest.vertexSkins.get(b))) {
					continue;
				}
				double r = dist(rest.vertices.get(a), rest.vertices.get(b));
				if (r < 6) {
					continue; // (short edges round badly)
				}
				double p = dist(posed.vertices.get(a), posed.vertices.get(b)) / r;
				lo = Math.min(lo, p);
				hi = Math.max(hi, p);
			}
		}
		return new double[] { lo, hi };
	}

	static double dist(int[] a, int[] b) {
		double x = a[0] - b[0], y = a[1] - b[1], z = a[2] - b[2];
		return Math.sqrt(x * x + y * y + z * z);
	}

	/* ====================================================================== */
	/*                                THE CACHE                               */
	/* ====================================================================== */

	/** What a creature puts in the cache, at fixed ids. Sequences are written in order, frames numbered from frameBase. */
	static final class Install {
		String name;
		int npcId, modelId, frameFile, frameBase;
		ModelData model;
		AnimCache.SkinList skins;
		final Map<Integer, List<FxAnim.Key>> seqs = new LinkedHashMap<>();
		ConfigDefinitions.Definition definition;
	}

	/**
	 * Writes a creature into a generated cache: the model, a new frame-group file, its sequences and the npc definition,
	 * each at its fixed id, padding the tables up to them with empty entries. Safe to repeat; refuses if any of its ids
	 * has been taken by something else.
	 */
	static void install(CacheEditor cache, Install c) throws IOException {
		CacheEditor.Archive cfg = CacheEditor.Archive.decode(cache.read(0, ConfigDefinitions.CONFIG_ARCHIVE));
		ConfigDefinitions.Table npcs = ConfigDefinitions.load(cfg, "npc", true);
		boolean ours = npcs.size() > c.npcId && c.name.equals(npcs.decode(c.npcId).text(2));
		CacheEditor.Archive vl = CacheEditor.Archive.decode(cache.read(0, CacheEditor.VERSIONLIST_ARCHIVE));
		byte[] seqDat = cfg.get("seq.dat");
		int seqCount = (seqDat[0] & 0xFF) << 8 | seqDat[1] & 0xFF;
		byte[] animIndex = vl.get("anim_index");
		int firstSeq = c.seqs.keySet().stream().min(Integer::compare).get(), lastSeq = c.seqs.keySet().stream().max(Integer::compare).get();
		if (!ours) {
			require(c, npcs.size() <= c.npcId, "npc " + c.npcId + " is taken (" + npcs.size() + " npcs)");
			require(c, Shardback.versionAt(vl.get("model_version"), c.modelId) == 0, "model " + c.modelId + " is taken");
			require(c, Shardback.versionAt(vl.get("anim_version"), c.frameFile) == 0, "frame file " + c.frameFile + " is taken");
			require(c, animIndex.length / 2 <= c.frameBase, "frame id " + c.frameBase + " is taken (" + animIndex.length / 2 + " frames)");
			require(c, seqCount <= firstSeq, "seq " + firstSeq + " is taken (" + seqCount + " seqs)");
		}

		byte[][] mt = { vl.get("model_version"), vl.get("model_crc") };
		cache.writeVersionedFile(ConfigDefinitions.MODEL_INDEX, c.modelId, c.model.encode(), 1, mt);
		vl.put("model_version", mt[0]);
		vl.put("model_crc", mt[1]);
		vl.put("model_index", Shardback.grow(vl.get("model_index"), mt[0].length / 2));

		List<FxAnim.Key> all = new ArrayList<>();
		c.seqs.values().forEach(all::addAll);
		byte[][] at = { vl.get("anim_version"), vl.get("anim_crc") };
		cache.writeVersionedFile(2, c.frameFile, FxAnim.encode(c.skins, all, c.frameBase), 1, at);
		vl.put("anim_version", at[0]);
		vl.put("anim_crc", at[1]);
		// the client sizes its frame table from anim_index (one u16 per frame id): cover ours, each naming our file
		animIndex = Shardback.grow(animIndex, Math.max(animIndex.length, (c.frameBase + all.size()) * 2));
		for (int i = 0; i < all.size(); i++) {
			ByteBuffer.wrap(animIndex).putShort((c.frameBase + i) * 2, (short) c.frameFile);
		}
		vl.put("anim_index", animIndex);
		cache.write(0, CacheEditor.VERSIONLIST_ARCHIVE, vl.encode());

		List<byte[]> entries = Shardback.seqEntries(seqDat, seqCount);
		while (entries.size() <= lastSeq) {
			entries.add(new byte[] { 0 });
		}
		int frame = c.frameBase;
		for (Map.Entry<Integer, List<FxAnim.Key>> e : c.seqs.entrySet()) {
			entries.set(e.getKey(), Shardback.seqEntry(e.getValue(), frame));
			frame += e.getValue().size();
		}
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		o.write(entries.size() >> 8);
		o.write(entries.size());
		for (byte[] b : entries) {
			o.write(b, 0, b.length);
		}
		cfg.put("seq.dat", o.toByteArray());

		while (npcs.size() < c.npcId) {
			npcs.put(npcs.size(), new ConfigDefinitions.Definition());
		}
		npcs.put(c.npcId, c.definition);
		ConfigDefinitions.save(cfg, npcs);
		cache.write(0, ConfigDefinitions.CONFIG_ARCHIVE, cfg.encode());
		System.out.println(c.name + ": npc " + c.npcId + ", model " + c.modelId + " (" + c.model.faces.size() + " tris), frame file " + c.frameFile
				+ " (frames " + c.frameBase + "-" + (c.frameBase + all.size() - 1) + "), seqs " + firstSeq + "-" + lastSeq);
	}

	private static void require(Install c, boolean ok, String what) {
		if (!ok) {
			throw new IllegalStateException(c.name + ": " + what + " - move its ids clear of it");
		}
	}
}
