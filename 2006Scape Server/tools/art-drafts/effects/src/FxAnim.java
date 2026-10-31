package com.rs2.tools.mapgen;

import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * Applies 317 animation frames to a {@link ModelData} exactly as the client does (Model.method469/470/472):
 * vertices are grouped by vertex-skin label, faces by face-skin label; each frame is a list of
 * (slot, x, y, z) where the slot's type in the skin list decides the meaning:
 *   0 origin  - pivot = centroid of the slot's labels + (x,y,z)
 *   1 translate the slot's labels by (x,y,z)
 *   2 rotate about the pivot: x/y/z are 0..255 (= 0..360 deg), applied in client order roll(z), pitch(x), yaw(y)
 *   3 scale about the pivot, 128 = 100%
 *   5 alpha: face alpha += x*8 (clamped 0..255; 0 = opaque) for faces whose FACE skin is in the slot's labels
 * Every frame is applied to a fresh copy of the base model (frames are absolute, not cumulative).
 *
 * Also contains an encoder for an idx2 frame-group file (the inverse of Class36.method529), for the
 * "author new frames" route.
 */
public final class FxAnim {

	static final int[] SIN = new int[2048], COS = new int[2048];
	static {
		for (int i = 0; i < 2048; i++) {
			SIN[i] = (int) (65536D * Math.sin(i * 0.0030679614999999999D));
			COS[i] = (int) (65536D * Math.cos(i * 0.0030679614999999999D));
		}
	}

	/** A copy of {@code base} posed by {@code f} (null frame = base pose). */
	public static ModelData pose(ModelData base, AnimCache.Frame f) {
		ModelData m = copy(base);
		if (f == null) return m;
		int nv = m.vertices.size();
		int[] X = new int[nv], Y = new int[nv], Z = new int[nv];
		for (int i = 0; i < nv; i++) { int[] v = m.vertices.get(i); X[i] = v[0]; Y[i] = v[1]; Z[i] = v[2]; }
		Map<Integer, List<Integer>> vg = new HashMap<>(), fg = new HashMap<>();
		for (int i = 0; i < nv; i++) vg.computeIfAbsent(m.vertexSkins.get(i), k -> new ArrayList<>()).add(i);
		for (int i = 0; i < m.faces.size(); i++) fg.computeIfAbsent(m.faceSkins.get(i), k -> new ArrayList<>()).add(i);
		int[] alpha = new int[m.faces.size()];
		for (int i = 0; i < alpha.length; i++) alpha[i] = m.alphas.get(i);
		int px = 0, py = 0, pz = 0;
		for (int o = 0; o < f.slot.length; o++) {
			int type = f.base.types[f.slot[o]];
			int[] labels = f.base.labels[f.slot[o]];
			int j = f.dx[o], k = f.dy[o], l = f.dz[o];
			if (type == 0) {
				long sx = 0, sy = 0, sz = 0; int n = 0;
				for (int lab : labels) for (int v : vg.getOrDefault(lab, List.of())) { sx += X[v]; sy += Y[v]; sz += Z[v]; n++; }
				if (n > 0) { px = (int) (sx / n) + j; py = (int) (sy / n) + k; pz = (int) (sz / n) + l; }
				else { px = j; py = k; pz = l; }
			} else if (type == 1) {
				for (int lab : labels) for (int v : vg.getOrDefault(lab, List.of())) { X[v] += j; Y[v] += k; Z[v] += l; }
			} else if (type == 2) {
				for (int lab : labels) for (int v : vg.getOrDefault(lab, List.of())) {
					X[v] -= px; Y[v] -= py; Z[v] -= pz;
					int k6 = (j & 0xff) * 8, l6 = (k & 0xff) * 8, i7 = (l & 0xff) * 8;
					if (i7 != 0) { int s = SIN[i7], c = COS[i7]; int t = Y[v] * s + X[v] * c >> 16; Y[v] = Y[v] * c - X[v] * s >> 16; X[v] = t; }
					if (k6 != 0) { int s = SIN[k6], c = COS[k6]; int t = Y[v] * c - Z[v] * s >> 16; Z[v] = Y[v] * s + Z[v] * c >> 16; Y[v] = t; }
					if (l6 != 0) { int s = SIN[l6], c = COS[l6]; int t = Z[v] * s + X[v] * c >> 16; Z[v] = Z[v] * c - X[v] * s >> 16; X[v] = t; }
					X[v] += px; Y[v] += py; Z[v] += pz;
				}
			} else if (type == 3) {
				for (int lab : labels) for (int v : vg.getOrDefault(lab, List.of())) {
					X[v] = (X[v] - px) * j / 128 + px; Y[v] = (Y[v] - py) * k / 128 + py; Z[v] = (Z[v] - pz) * l / 128 + pz;
				}
			} else if (type == 5) {
				for (int lab : labels) for (int fc : fg.getOrDefault(lab, List.of())) alpha[fc] = Math.max(0, Math.min(255, alpha[fc] + j * 8));
			}
		}
		for (int i = 0; i < nv; i++) m.vertices.set(i, new int[] { X[i], Y[i], Z[i] });
		for (int i = 0; i < alpha.length; i++) m.alphas.set(i, alpha[i]);
		return m;
	}

	public static ModelData copy(ModelData b) {
		ModelData m = new ModelData();
		for (int[] v : b.vertices) m.vertices.add(v.clone());
		m.vertexSkins.addAll(b.vertexSkins);
		for (int[] f : b.faces) m.faces.add(f.clone());
		m.colors.addAll(b.colors); m.renderTypes.addAll(b.renderTypes); m.priorities.addAll(b.priorities);
		m.alphas.addAll(b.alphas); m.faceSkins.addAll(b.faceSkins);
		return m;
	}

	/* =================================================================== authoring new frames */

	/** One authored frame: ops in slot order, each {slot, x, y, z}; delay in client cycles (20 ms). */
	public static final class Key {
		public final int delay; public final List<int[]> ops = new ArrayList<>();
		public Key(int delay) { this.delay = delay; }
		public Key op(int slot, int x, int y, int z) { ops.add(new int[] { slot, x, y, z }); return this; }
	}

	public static AnimCache.SkinList skinList(int[] types, int[][] labels) {
		AnimCache.SkinList s = new AnimCache.SkinList(); s.types = types; s.labels = labels; return s;
	}

	/** Builds client-equivalent Frame objects for a script (the same structure method529 produces). */
	public static List<AnimCache.Frame> frames(AnimCache.SkinList sl, List<Key> keys) {
		byte[] file = encode(sl, keys, 0);
		Map<Integer, AnimCache.Frame> save = AnimCache.frames; Map<Integer, AnimCache.SkinList> saveS = AnimCache.skinLists;
		AnimCache.frames = new HashMap<>(); AnimCache.skinLists = new HashMap<>();
		AnimCache.loadFrameFile(-1, file); // decode with the client's algorithm: proves the encoding
		List<AnimCache.Frame> out = new ArrayList<>();
		for (int i = 0; i < keys.size(); i++) out.add(AnimCache.frames.get(i));
		AnimCache.frames = save; AnimCache.skinLists = saveS;
		return out;
	}

	/**
	 * Encodes a frame-group file (the uncompressed contents of one idx2 entry), frame ids firstId.. in order.
	 * Layout (Class36.method529): [u16 count, per frame u16 id + u8 slotCount] [u8 flags per slot]
	 * [signed smarts] [u8 delay per frame] [skin list] [u16 len1-2, u16 len2, u16 len3, u16 len4].
	 */
	public static byte[] encode(AnimCache.SkinList sl, List<Key> keys, int firstId) {
		ByteArrayOutputStream s1 = new ByteArrayOutputStream(), s2 = new ByteArrayOutputStream(), s3 = new ByteArrayOutputStream(),
				s4 = new ByteArrayOutputStream(), s5 = new ByteArrayOutputStream();
		s1.write(keys.size() >> 8); s1.write(keys.size());
		int n = sl.types.length;
		for (int i = 0; i < keys.size(); i++) {
			Key k = keys.get(i);
			int id = firstId + i;
			s1.write(id >> 8); s1.write(id); s1.write(n);
			int[][] bySlot = new int[n][];
			for (int[] op : k.ops) bySlot[op[0]] = op;
			for (int s = 0; s < n; s++) {
				int[] op = bySlot[s];
				if (op == null) { s2.write(0); continue; }
				int d = sl.types[s] == 3 ? 128 : 0;
				int flags = (op[1] != d ? 1 : 0) | (op[2] != d ? 2 : 0) | (op[3] != d ? 4 : 0);
				if (flags == 0 && sl.types[s] == 0) flags = 1; // an explicit origin with zero offset still counts as present
				s2.write(flags);
				if ((flags & 1) != 0) smart(s3, op[1]);
				if ((flags & 2) != 0) smart(s3, op[2]);
				if ((flags & 4) != 0) smart(s3, op[3]);
			}
			s4.write(k.delay);
		}
		s5.write(n);
		for (int t : sl.types) s5.write(t);
		for (int[] l : sl.labels) { s5.write(l.length); for (int x : l) s5.write(x); }
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (ByteArrayOutputStream b : List.of(s1, s2, s3, s4, s5)) out.write(b.toByteArray(), 0, b.size());
		int[] lens = { s1.size() - 2, s2.size(), s3.size(), s4.size() };
		for (int l : lens) { out.write(l >> 8); out.write(l); }
		return out.toByteArray();
	}

	static void smart(ByteArrayOutputStream o, int v) {
		if (v >= -64 && v < 64) o.write(v + 64);
		else { int u = v + 49152; o.write(u >> 8); o.write(u); }
	}
}
