package com.rs2.tools.mapgen;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Read-only decoders for the 317 animation data, ported 1:1 from the client (SpotAnim.readValues,
 * Animation.readValues, Class36.method529 (frames), Class18 (skin list / "frame base")).
 *
 *  - config archive (idx0 #2) "spotanim.dat": graphics = model + seq + resize + rotation + brightness + recolours
 *  - config archive "seq.dat": sequences = list of (primary frame id, secondary frame id, delay)
 *  - idx2 (gzipped): frame-group files. Each holds ONE skin list and many frames; frame ids inside are global.
 */
public final class AnimCache {

	/* ---------------------------------------------------------------- spotanim */
	public static final class SpotAnim {
		public int id, model = 0, seq = -1, resizeXY = 128, resizeZ = 128, rotation, brightness, shadow;
		public int[] from = new int[6], to = new int[6];
		public String toString() {
			StringBuilder s = new StringBuilder("gfx " + id + " model=" + model + " seq=" + seq);
			if (resizeXY != 128 || resizeZ != 128) s.append(" resize=" + resizeXY + "/" + resizeZ);
			if (rotation != 0) s.append(" rot=" + rotation);
			if (brightness != 0 || shadow != 0) s.append(" light=" + brightness + "/" + shadow);
			for (int i = 0; i < 6; i++) if (from[i] != 0) s.append(" recol " + from[i] + "->" + to[i]);
			return s.toString();
		}
	}

	public static List<SpotAnim> spotAnims(byte[] dat) {
		ByteBuffer b = ByteBuffer.wrap(dat);
		int n = b.getShort() & 0xFFFF;
		List<SpotAnim> out = new ArrayList<>();
		for (int j = 0; j < n; j++) {
			SpotAnim s = new SpotAnim();
			s.id = j;
			for (;;) {
				int op = b.get() & 0xFF;
				if (op == 0) break;
				if (op == 1) s.model = b.getShort() & 0xFFFF;
				else if (op == 2) s.seq = b.getShort() & 0xFFFF;
				else if (op == 4) s.resizeXY = b.getShort() & 0xFFFF;
				else if (op == 5) s.resizeZ = b.getShort() & 0xFFFF;
				else if (op == 6) s.rotation = b.getShort() & 0xFFFF;
				else if (op == 7) s.brightness = b.get() & 0xFF;
				else if (op == 8) s.shadow = b.get() & 0xFF;
				else if (op >= 40 && op < 50) s.from[op - 40] = b.getShort() & 0xFFFF;
				else if (op >= 50 && op < 60) s.to[op - 50] = b.getShort() & 0xFFFF;
				else throw new IllegalStateException("spotanim op " + op);
			}
			out.add(s);
		}
		return out;
	}

	/* ---------------------------------------------------------------- seq */
	public static final class Seq {
		public int id, frameCount, loopOffset = -1, priority = 5, shield = -1, weapon = -1, loops = 99;
		public int[] frames = new int[0], frames2 = new int[0], delays = new int[0];
	}

	public static List<Seq> seqs(byte[] dat) {
		ByteBuffer b = ByteBuffer.wrap(dat);
		int n = b.getShort() & 0xFFFF;
		List<Seq> out = new ArrayList<>();
		for (int j = 0; j < n; j++) {
			Seq s = new Seq();
			s.id = j;
			for (;;) {
				int op = b.get() & 0xFF;
				if (op == 0) break;
				if (op == 1) {
					s.frameCount = b.get() & 0xFF;
					s.frames = new int[s.frameCount];
					s.frames2 = new int[s.frameCount];
					s.delays = new int[s.frameCount];
					for (int k = 0; k < s.frameCount; k++) {
						s.frames[k] = b.getShort() & 0xFFFF;
						s.frames2[k] = b.getShort() & 0xFFFF;
						if (s.frames2[k] == 65535) s.frames2[k] = -1;
						s.delays[k] = b.getShort() & 0xFFFF;
					}
				} else if (op == 2) s.loopOffset = b.getShort() & 0xFFFF;
				else if (op == 3) { int k = b.get() & 0xFF; for (int l = 0; l < k; l++) b.get(); }
				else if (op == 4) { }
				else if (op == 5) s.priority = b.get() & 0xFF;
				else if (op == 6) s.shield = b.getShort() & 0xFFFF;
				else if (op == 7) s.weapon = b.getShort() & 0xFFFF;
				else if (op == 8) s.loops = b.get() & 0xFF;
				else if (op == 9 || op == 10 || op == 11) b.get();
				else if (op == 12) b.getInt();
				else throw new IllegalStateException("seq op " + op + " in seq " + j);
			}
			out.add(s);
		}
		return out;
	}

	/* ---------------------------------------------------------------- frames */
	/** Class18: the skin list ("frame base"): per transform slot a type (0 origin,1 translate,2 rotate,3 scale,5 alpha) and the labels it moves. */
	public static final class SkinList {
		public int file;
		public int[] types;
		public int[][] labels;
		public String describe() {
			StringBuilder s = new StringBuilder();
			for (int i = 0; i < types.length; i++) s.append(i).append(':').append("OTRS?A".charAt(Math.min(types[i], 5))).append(Arrays.toString(labels[i])).append(' ');
			return s.toString();
		}
	}

	/** Class36: one frame = list of (slot, x, y, z), with implied origin slots already inserted, exactly as the client builds it. */
	public static final class Frame {
		public int id, file, delay;
		public SkinList base;
		public int[] slot, dx, dy, dz;
	}

	public static Map<Integer, Frame> frames = new HashMap<>();
	public static Map<Integer, SkinList> skinLists = new HashMap<>();

	public static void loadFrameFile(int file, byte[] raw) {
		ByteBuffer h = ByteBuffer.wrap(raw, raw.length - 8, 8);
		int i = h.getShort() & 0xFFFF, j = h.getShort() & 0xFFFF, k = h.getShort() & 0xFFFF, l = h.getShort() & 0xFFFF;
		int o = 0;
		int p1 = o; o += i + 2;
		int[] p2 = { o }; o += j;
		int[] p3 = { o }; o += k;
		int[] p4 = { o }; o += l;
		int p5 = o;
		SkinList sl = new SkinList();
		sl.file = file;
		int c = raw[p5++] & 0xFF;
		sl.types = new int[c];
		sl.labels = new int[c][];
		for (int a = 0; a < c; a++) sl.types[a] = raw[p5++] & 0xFF;
		for (int a = 0; a < c; a++) {
			int n = raw[p5++] & 0xFF;
			sl.labels[a] = new int[n];
			for (int q = 0; q < n; q++) sl.labels[a][q] = raw[p5++] & 0xFF;
		}
		skinLists.put(file, sl);
		int[] q1 = { p1 };
		int count = u16(raw, q1);
		for (int f = 0; f < count; f++) {
			Frame fr = new Frame();
			fr.id = u16(raw, q1);
			fr.file = file;
			fr.delay = raw[p4[0]++] & 0xFF;
			fr.base = sl;
			int n = raw[q1[0]++] & 0xFF;
			List<int[]> ops = new ArrayList<>();
			int last = -1;
			for (int s = 0; s < n; s++) {
				int flags = raw[p2[0]++] & 0xFF;
				if (flags > 0) {
					if (sl.types[s] != 0) {
						for (int t = s - 1; t > last; t--) {
							if (sl.types[t] != 0) continue;
							ops.add(new int[] { t, 0, 0, 0 });
							break;
						}
					}
					int d = sl.types[s] == 3 ? 128 : 0;
					int x = (flags & 1) != 0 ? smart(raw, p3) : d;
					int y = (flags & 2) != 0 ? smart(raw, p3) : d;
					int z = (flags & 4) != 0 ? smart(raw, p3) : d;
					ops.add(new int[] { s, x, y, z });
					last = s;
				}
			}
			fr.slot = new int[ops.size()]; fr.dx = new int[ops.size()]; fr.dy = new int[ops.size()]; fr.dz = new int[ops.size()];
			for (int a = 0; a < ops.size(); a++) { int[] op = ops.get(a); fr.slot[a] = op[0]; fr.dx[a] = op[1]; fr.dy[a] = op[2]; fr.dz[a] = op[3]; }
			frames.put(fr.id, fr);
		}
	}

	static int u16(byte[] b, int[] p) { int v = (b[p[0]] & 0xFF) << 8 | b[p[0] + 1] & 0xFF; p[0] += 2; return v; }

	/** Stream.method421: signed smart. */
	static int smart(byte[] b, int[] p) {
		int v = b[p[0]] & 0xFF;
		if (v < 128) { p[0]++; return v - 64; }
		int r = ((b[p[0]] & 0xFF) << 8 | b[p[0] + 1] & 0xFF) - 49152;
		p[0] += 2;
		return r;
	}

	public static void loadAllFrames(CacheEditor c) throws IOException {
		int n = c.fileCount(2);
		for (int f = 0; f < n; f++) {
			byte[] raw = c.read(2, f);
			if (raw == null || raw.length < 10) continue;
			try {
				loadFrameFile(f, org.apollo.util.CompressionUtil.degzip(ByteBuffer.wrap(raw)));
			} catch (Exception e) {
				System.err.println("frame file " + f + ": " + e);
			}
		}
	}
}
