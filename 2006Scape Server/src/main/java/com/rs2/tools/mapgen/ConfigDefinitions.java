package com.rs2.tools.mapgen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apollo.util.CompressionUtil;

/**
 * Adds new NPC / item definitions to the "config" archive (npc.dat/npc.idx, obj.dat/obj.idx) by cloning
 * existing ones and overriding a few opcodes (name, examine, actions, recolours, scale, combat level).
 */
public final class ConfigDefinitions {

	public static final int CONFIG_ARCHIVE = 2;
	public static final int MODEL_INDEX = 1;

	/** One opcode + its raw payload. */
	public static final class Op {
		final int opcode;
		final byte[] payload;

		Op(int opcode, byte[] payload) {
			this.opcode = opcode;
			this.payload = payload;
		}
	}

	/** A definition as an ordered list of opcodes; edit it then call {@link #encode()}. */
	public static final class Definition {
		final List<Op> ops = new ArrayList<>();

		public Definition set(int opcode, byte[] payload) {
			for (int i = 0; i < ops.size(); i++) {
				if (ops.get(i).opcode == opcode) {
					ops.set(i, new Op(opcode, payload));
					return this;
				}
			}
			ops.add(new Op(opcode, payload));
			return this;
		}

		public Definition remove(int opcode) {
			ops.removeIf(o -> o.opcode == opcode);
			return this;
		}

		public byte[] get(int opcode) {
			for (Op o : ops) {
				if (o.opcode == opcode) {
					return o.payload;
				}
			}
			return null;
		}

		public Definition name(String name) {
			return set(2, string(name));
		}

		public Definition examine(String text) {
			return set(3, string(text));
		}

		/**
		 * NPC right-click options (opcodes 30-34). Null entries hide that slot. The client sends each slot as a different
		 * packet: 0 first click, 1 ATTACK, 2 second click, 3 third click, 4 fourth click - so slot 1 is only ever
		 * "Attack" (a "Trade" there sends an attack and never reaches the shop handlers in secondClickNpc).
		 */
		public Definition npcActions(String... actions) {
			if (actions.length > 1 && actions[1] != null && !actions[1].equalsIgnoreCase("Attack")) {
				throw new IllegalArgumentException("npc option \"" + actions[1] + "\" in slot 1, which the client sends as an attack:"
						+ " put it in slot 2 (second click) or later");
			}
			for (int i = 0; i < 5; i++) {
				remove(30 + i);
			}
			for (int i = 0; i < actions.length; i++) {
				if (actions[i] != null) {
					set(30 + i, string(actions[i]));
				}
			}
			return this;
		}

		/** Item inventory options (opcodes 35-39). */
		public Definition itemActions(String... actions) {
			for (int i = 0; i < 5; i++) {
				remove(35 + i);
			}
			for (int i = 0; i < actions.length; i++) {
				if (actions[i] != null) {
					set(35 + i, string(actions[i]));
				}
			}
			return this;
		}

		/** Object model scale (opcodes 65/66/67, 128 = normal size). */
		public Definition scale(int x, int height, int y) {
			return shorts(65, x).shorts(66, height).shorts(67, y);
		}

		/** Object footprint in tiles (opcodes 14/15). */
		public Definition footprint(int width, int length) {
			return set(14, new byte[] { (byte) width }).set(15, new byte[] { (byte) length });
		}

		public Definition shorts(int opcode, int... values) {
			ByteBuffer b = ByteBuffer.allocate(values.length * 2);
			for (int v : values) {
				b.putShort((short) v);
			}
			return set(opcode, b.array());
		}

		/** Item value (opcode 12). */
		public Definition cost(int value) {
			return set(12, ByteBuffer.allocate(4).putInt(value).array());
		}

		public Definition recolor(int[] from, int[] to) {
			if (from.length == 0) {
				return remove(40);
			}
			ByteBuffer b = ByteBuffer.allocate(1 + from.length * 4);
			b.put((byte) from.length);
			for (int i = 0; i < from.length; i++) {
				b.putShort((short) from[i]).putShort((short) to[i]);
			}
			return set(40, b.array());
		}

		/** Model ids from opcode 1 (NPC: count + shorts, item: single short). */
		public int[] npcModels() {
			byte[] p = get(1);
			if (p == null) {
				return new int[0];
			}
			int[] out = new int[p[0] & 0xFF];
			for (int i = 0; i < out.length; i++) {
				out[i] = (p[1 + i * 2] & 0xFF) << 8 | p[2 + i * 2] & 0xFF;
			}
			return out;
		}

		/** Model ids of an object definition (opcode 1 or 5). */
		public int[] locModels() {
			byte[] p = get(1);
			int stride = 3;
			if (p == null) {
				p = get(5);
				stride = 2;
			}
			if (p == null) {
				return new int[0];
			}
			int[] out = new int[p[0] & 0xFF];
			for (int i = 0; i < out.length; i++) {
				out[i] = (p[1 + i * stride] & 0xFF) << 8 | p[2 + i * stride] & 0xFF;
			}
			return out;
		}

		public String text(int opcode) {
			byte[] p = get(opcode);
			return p == null ? null : new String(p, 0, p.length - 1, StandardCharsets.ISO_8859_1);
		}

		public int itemModel() {
			byte[] p = get(1);
			return p == null ? -1 : (p[0] & 0xFF) << 8 | p[1] & 0xFF;
		}

		public byte[] encode() {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			for (Op o : ops) {
				out.write(o.opcode);
				out.write(o.payload, 0, o.payload.length);
			}
			out.write(0);
			return out.toByteArray();
		}
	}

	/** A definition table (npc or obj) loaded from the config archive. */
	public static final class Table {
		final String name;
		final boolean npc;
		final List<byte[]> entries = new ArrayList<>();
		/** The .dat starts with two bytes (the entry count) before the first definition. */
		final boolean headerIsCount;

		Table(String name, boolean npc, byte[] dat, byte[] idx) {
			this.name = name;
			this.npc = npc;
			this.headerIsCount = ((dat[0] & 0xFF) << 8 | dat[1] & 0xFF) == ((idx[0] & 0xFF) << 8 | idx[1] & 0xFF);
			ByteBuffer ib = ByteBuffer.wrap(idx);
			int count = ib.getShort() & 0xFFFF;
			int pos = 2;
			for (int i = 0; i < count; i++) {
				int len = ib.getShort() & 0xFFFF;
				byte[] e = new byte[len];
				System.arraycopy(dat, pos, e, 0, len);
				entries.add(e);
				pos += len;
			}
		}

		public int size() {
			return entries.size();
		}

		public Definition decode(int id) {
			ByteBuffer b = ByteBuffer.wrap(entries.get(id));
			Definition d = new Definition();
			while (b.hasRemaining()) {
				int op = b.get() & 0xFF;
				if (op == 0) {
					break;
				}
				int start = b.position();
				skip(b, op, npc, name.equals("loc"));
				byte[] payload = new byte[b.position() - start];
				System.arraycopy(entries.get(id), start, payload, 0, payload.length);
				d.ops.add(new Op(op, payload));
			}
			return d;
		}

		/** Sets (or appends) a definition and returns its id. */
		public int put(int id, Definition d) {
			byte[] bytes = d.encode();
			if (id == entries.size()) {
				entries.add(bytes);
			} else {
				entries.set(id, bytes);
			}
			return id;
		}

		void writeTo(CacheEditor.Archive config) {
			ByteArrayOutputStream dat = new ByteArrayOutputStream();
			ByteBuffer idx = ByteBuffer.allocate(2 + entries.size() * 2);
			idx.putShort((short) entries.size());
			int header = headerIsCount ? entries.size() : 0; // the client skips these 2 bytes (indices start at 2)
			dat.write(header >> 8);
			dat.write(header);
			for (byte[] e : entries) {
				idx.putShort((short) e.length);
				dat.write(e, 0, e.length);
			}
			config.put(name + ".dat", dat.toByteArray());
			config.put(name + ".idx", idx.array());
		}
	}

	private static void skip(ByteBuffer b, int op, boolean npc, boolean loc) {
		if (loc) {
			skipLoc(b, op);
			return;
		}
		if (op == 2 || op == 3 || op >= 30 && op < 40) {
			while (b.get() != 10) {
				// string terminated by '\n'
			}
			return;
		}
		if (op == 40) {
			b.position(b.position() + 1 + (b.get(b.position()) & 0xFF) * 4);
			return;
		}
		if (npc) {
			switch (op) {
			case 1:
			case 60:
				b.position(b.position() + 1 + (b.get(b.position()) & 0xFF) * 2);
				return;
			case 12: case 100: case 101:
				b.get();
				return;
			case 13: case 14: case 90: case 91: case 92: case 95: case 97: case 98: case 102: case 103:
				b.getShort();
				return;
			case 17:
				b.position(b.position() + 8);
				return;
			case 93: case 99: case 107:
				return;
			case 106: {
				b.getShort();
				b.getShort();
				int n = b.get() & 0xFF;
				b.position(b.position() + (n + 1) * 2);
				return;
			}
			default:
				throw new IllegalStateException("unknown npc opcode " + op);
			}
		}
		switch (op) {
		case 11: case 16:
			return;
		case 12:
			b.getInt();
			return;
		case 23: case 25:
			b.position(b.position() + 3);
			return;
		case 113: case 114: case 115:
			b.get();
			return;
		default:
			if (op >= 100 && op < 110) {
				b.getInt();
				return;
			}
			b.getShort(); // 1,4-8,10,24,26,78,79,90-93,95,97,98,110-112
		}
	}

	/** Object (loc.dat) opcodes, as read by the 317 client's ObjectDef. */
	private static void skipLoc(ByteBuffer b, int op) {
		switch (op) {
		case 1:
			b.position(b.position() + 1 + (b.get(b.position()) & 0xFF) * 3);
			return;
		case 2: case 3: case 30: case 31: case 32: case 33: case 34: case 35: case 36: case 37: case 38:
			while (b.get() != 10) {
				// string terminated by '\n'
			}
			return;
		case 5:
			b.position(b.position() + 1 + (b.get(b.position()) & 0xFF) * 2);
			return;
		case 40:
			b.position(b.position() + 1 + (b.get(b.position()) & 0xFF) * 4);
			return;
		case 14: case 15: case 19: case 28: case 29: case 39: case 69: case 75:
			b.get();
			return;
		case 17: case 18: case 21: case 22: case 23: case 62: case 64: case 73: case 74:
			return;
		case 24: case 60: case 65: case 66: case 67: case 68: case 70: case 71: case 72:
			b.getShort();
			return;
		case 77: {
			b.getShort();
			b.getShort();
			int n = b.get() & 0xFF;
			b.position(b.position() + (n + 1) * 2);
			return;
		}
		default:
			throw new IllegalStateException("unknown loc opcode " + op);
		}
	}

	public static Table load(CacheEditor.Archive config, String name, boolean npc) {
		return new Table(name, npc, config.get(name + ".dat"), config.get(name + ".idx"));
	}

	public static void save(CacheEditor.Archive config, Table... tables) {
		for (Table t : tables) {
			t.writeTo(config);
		}
	}

	/* ---------------- model colours ---------------- */

	/** Distinct face colours of a model (idx1), read straight from the 317 model format. */
	public static Set<Integer> modelColors(CacheEditor cache, int modelId) throws IOException {
		byte[] raw = cache.read(MODEL_INDEX, modelId);
		Set<Integer> colors = new LinkedHashSet<>();
		if (raw == null) {
			return colors;
		}
		byte[] m = CompressionUtil.degzip(ByteBuffer.wrap(raw));
		ByteBuffer h = ByteBuffer.wrap(m, m.length - 18, 18);
		int vertices = h.getShort() & 0xFFFF, triangles = h.getShort() & 0xFFFF;
		h.get(); // textured triangles
		int textures = h.get() & 0xFF, priority = h.get() & 0xFF, alpha = h.get() & 0xFF, triSkins = h.get() & 0xFF,
				vertexSkins = h.get() & 0xFF;
		h.getShort();
		h.getShort();
		h.getShort();
		int triData = h.getShort() & 0xFFFF;
		int offset = vertices + triangles;
		if (priority == 255) {
			offset += triangles;
		}
		if (triSkins == 1) {
			offset += triangles;
		}
		if (textures == 1) {
			offset += triangles;
		}
		if (vertexSkins == 1) {
			offset += vertices;
		}
		if (alpha == 1) {
			offset += triangles;
		}
		offset += triData;
		ByteBuffer c = ByteBuffer.wrap(m, offset, triangles * 2);
		for (int i = 0; i < triangles; i++) {
			colors.add(c.getShort() & 0xFFFF);
		}
		return colors;
	}

	/**
	 * Builds recolour pairs that shift every colour of the given models to one hue / saturation while
	 * keeping its lightness (scaled), e.g. to make a pirate look ghostly.
	 * RS colours are 16-bit HSL: hue (6 bits) | saturation (3 bits) | lightness (7 bits).
	 */
	public static int[][] tint(CacheEditor cache, int[] models, int hue, int saturation, double lightScale, int lightAdd)
			throws IOException {
		return tint(cache, models, hue, saturation, lightScale, lightAdd, c -> true);
	}

	/** As above, but only recolours the colours accepted by {@code filter} (16-bit HSL). */
	public static int[][] tint(CacheEditor cache, int[] models, int hue, int saturation, double lightScale, int lightAdd,
			java.util.function.IntPredicate filter) throws IOException {
		Set<Integer> all = new LinkedHashSet<>();
		for (int model : models) {
			all.addAll(modelColors(cache, model));
		}
		List<Integer> from = new ArrayList<>(), to = new ArrayList<>();
		for (int col : all) {
			if (from.size() == 255) {
				break;
			}
			if (!filter.test(col)) {
				continue;
			}
			int light = col & 127;
			int newLight = Math.max(0, Math.min(127, (int) (light * lightScale) + lightAdd));
			int newCol = hue == VOID ? voidRamp(newLight, saturation) : hue << 10 | saturation << 7 | newLight;
			if (newCol != col) {
				from.add(col);
				to.add(newCol);
			}
		}
		return new int[][] { from.stream().mapToInt(i -> i).toArray(), to.stream().mapToInt(i -> i).toArray() };
	}

	/**
	 * Pass as the hue to {@link #tint} for the void's own colours: a ramp rather than one flat hue, so shading reads.
	 * Shadows sink to a deep indigo, mid-tones are a muted violet and highlights lift to a cool lilac - and the
	 * saturation is capped, so nothing tinted with it ever goes the hot magenta of a flat hue-50 recolour.
	 */
	public static final int VOID = 64;

	/** The void ramp at a given lightness; {@code intensity} (0-7) is how strongly violet the mid-tones are. */
	public static int voidRamp(int light, int intensity) {
		int hue = light < 20 ? 45 : light < 44 ? 46 : light < 72 ? 47 : light < 96 ? 48 : 49;
		int sat = Math.min(4, intensity);
		if (light < 14) {
			sat = Math.min(sat, 2); // near-black stays near-black
		} else if (light > 100) {
			sat = Math.min(sat, 3); // the brightest highlights go pale, not neon
		}
		return hue << 10 | sat << 7 | light;
	}

	static byte[] string(String s) {
		byte[] b = (s + "\n").getBytes(StandardCharsets.ISO_8859_1);
		return b;
	}
}
