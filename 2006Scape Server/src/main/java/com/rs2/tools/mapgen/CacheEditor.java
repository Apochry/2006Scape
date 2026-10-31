package com.rs2.tools.mapgen;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

import org.apollo.util.CompressionUtil;

/**
 * Minimal read/write access to a 317-format cache (main_file_cache.dat + idx0..idx4).
 *
 * New file contents are always appended to the end of the data file and the index entry is
 * re-pointed, so existing data is never overwritten in place. That keeps edits safe to repeat
 * (the old sectors simply become unused).
 */
public final class CacheEditor implements Closeable {

	private static final int HEADER = 8, CHUNK = 512, BLOCK = HEADER + CHUNK;

	/** The index of the "versionlist" archive inside idx0. */
	public static final int VERSIONLIST_ARCHIVE = 5;

	/** idx4 holds the gzipped map (terrain/object) files. */
	public static final int MAP_INDEX = 4;

	private final RandomAccessFile data;
	private final RandomAccessFile[] indices = new RandomAccessFile[5];

	public CacheEditor(Path dir) throws IOException {
		data = new RandomAccessFile(dir.resolve("main_file_cache.dat").toFile(), "rw");
		for (int i = 0; i < indices.length; i++) {
			indices[i] = new RandomAccessFile(dir.resolve("main_file_cache.idx" + i).toFile(), "rw");
		}
	}

	public int fileCount(int type) throws IOException {
		return (int) (indices[type].length() / 6);
	}

	public byte[] read(int type, int file) throws IOException {
		RandomAccessFile idx = indices[type];
		if ((long) file * 6 + 6 > idx.length()) {
			return null;
		}
		byte[] entry = new byte[6];
		idx.seek((long) file * 6);
		idx.readFully(entry);
		int size = medium(entry, 0);
		int block = medium(entry, 3);
		if (size <= 0 || block <= 0) {
			return null;
		}
		byte[] out = new byte[size];
		int read = 0;
		byte[] header = new byte[HEADER];
		for (int chunk = 0; read < size; chunk++) {
			data.seek((long) block * BLOCK);
			data.readFully(header);
			int n = Math.min(CHUNK, size - read);
			data.readFully(out, read, n);
			read += n;
			block = medium(header, 4);
		}
		return out;
	}

	public void write(int type, int file, byte[] bytes) throws IOException {
		long end = data.length();
		int block = (int) ((end + BLOCK - 1) / BLOCK);
		int start = block;
		int written = 0;
		for (int chunk = 0; written < bytes.length; chunk++) {
			int n = Math.min(CHUNK, bytes.length - written);
			int next = written + n < bytes.length ? block + 1 : 0;
			byte[] sector = new byte[BLOCK];
			sector[0] = (byte) (file >> 8);
			sector[1] = (byte) file;
			sector[2] = (byte) (chunk >> 8);
			sector[3] = (byte) chunk;
			sector[4] = (byte) (next >> 16);
			sector[5] = (byte) (next >> 8);
			sector[6] = (byte) next;
			sector[7] = (byte) (type + 1);
			System.arraycopy(bytes, written, sector, HEADER, n);
			data.seek((long) block * BLOCK);
			// the final sector only needs to be as long as its payload
			data.write(sector, 0, next == 0 ? HEADER + n : BLOCK);
			written += n;
			block++;
		}
		byte[] entry = new byte[6];
		putMedium(entry, 0, bytes.length);
		putMedium(entry, 3, start);
		RandomAccessFile idx = indices[type];
		if ((long) file * 6 > idx.length()) {
			idx.setLength((long) file * 6); // zero-fill any gap
		}
		idx.seek((long) file * 6);
		idx.write(entry);
	}

	/* ---------------- archives (idx0) ---------------- */

	/** A decoded jagex archive, keyed by name hash, in original order. */
	public static final class Archive {
		public final List<Integer> hashes = new ArrayList<>();
		public final List<byte[]> entries = new ArrayList<>();

		public byte[] get(String name) {
			int i = hashes.indexOf(hash(name));
			return i < 0 ? null : entries.get(i);
		}

		public void put(String name, byte[] value) {
			int h = hash(name);
			int i = hashes.indexOf(h);
			if (i < 0) {
				hashes.add(h);
				entries.add(value);
			} else {
				entries.set(i, value);
			}
		}

		public static Archive decode(byte[] raw) throws IOException {
			ByteBuffer buf = ByteBuffer.wrap(raw);
			int extracted = readMedium(buf), size = readMedium(buf);
			boolean whole = extracted != size;
			if (whole) {
				byte[] compressed = new byte[size], out = new byte[extracted];
				buf.get(compressed);
				CompressionUtil.debzip2(compressed, out);
				buf = ByteBuffer.wrap(out);
			}
			Archive a = new Archive();
			int count = buf.getShort() & 0xFFFF;
			int[] raws = new int[count], sizes = new int[count];
			for (int i = 0; i < count; i++) {
				a.hashes.add(buf.getInt());
				raws[i] = readMedium(buf);
				sizes[i] = readMedium(buf);
			}
			for (int i = 0; i < count; i++) {
				byte[] e = new byte[raws[i]];
				if (whole) {
					buf.get(e);
				} else {
					byte[] c = new byte[sizes[i]];
					buf.get(c);
					CompressionUtil.debzip2(c, e);
				}
				a.entries.add(e);
			}
			return a;
		}

		/** Encodes with each entry individually bzip2'd (supported by both client and server). */
		public byte[] encode() throws IOException {
			List<byte[]> compressed = new ArrayList<>();
			for (byte[] e : entries) {
				compressed.add(CompressionUtil.bzip2(e));
			}
			ByteArrayOutputStream body = new ByteArrayOutputStream();
			body.write(entries.size() >> 8);
			body.write(entries.size());
			for (int i = 0; i < entries.size(); i++) {
				int h = hashes.get(i);
				body.write(h >> 24);
				body.write(h >> 16);
				body.write(h >> 8);
				body.write(h);
				writeMedium(body, entries.get(i).length);
				writeMedium(body, compressed.get(i).length);
			}
			for (byte[] c : compressed) {
				body.write(c);
			}
			byte[] b = body.toByteArray();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			writeMedium(out, b.length);
			writeMedium(out, b.length);
			out.write(b);
			return out.toByteArray();
		}
	}

	public static int hash(String name) {
		int h = 0;
		for (char c : name.toUpperCase().toCharArray()) {
			h = h * 61 + c - 32;
		}
		return h;
	}

	/* ---------------- map files ---------------- */

	/** A row of the map_index table. */
	public static final class MapIndexEntry {
		public int region, terrainFile, objectFile;
		public boolean members;
	}

	public static List<MapIndexEntry> parseMapIndex(byte[] raw) {
		List<MapIndexEntry> list = new ArrayList<>();
		ByteBuffer b = ByteBuffer.wrap(raw);
		while (b.remaining() >= 7) {
			MapIndexEntry e = new MapIndexEntry();
			e.region = b.getShort() & 0xFFFF;
			e.terrainFile = b.getShort() & 0xFFFF;
			e.objectFile = b.getShort() & 0xFFFF;
			e.members = b.get() != 0;
			list.add(e);
		}
		return list;
	}

	public static byte[] encodeMapIndex(List<MapIndexEntry> list) {
		ByteBuffer b = ByteBuffer.allocate(list.size() * 7);
		for (MapIndexEntry e : list) {
			b.putShort((short) e.region).putShort((short) e.terrainFile).putShort((short) e.objectFile).put((byte) (e.members ? 1 : 0));
		}
		return b.array();
	}

	/**
	 * Writes a gzipped map file into idx4 and records its version + CRC in the versionlist tables.
	 * Map files carry their 2-byte version as a trailer, which the client verifies against map_crc.
	 */
	public void writeMapFile(int file, byte[] uncompressed, int version, byte[][] versionTables) throws IOException {
		byte[] gz = CompressionUtil.gzip(uncompressed);
		byte[] stored = new byte[gz.length + 2];
		System.arraycopy(gz, 0, stored, 0, gz.length);
		stored[gz.length] = (byte) (version >> 8);
		stored[gz.length + 1] = (byte) version;
		write(MAP_INDEX, file, stored);

		CRC32 crc = new CRC32();
		crc.update(gz);
		int c = (int) crc.getValue();
		versionTables[0] = grow(versionTables[0], (file + 1) * 2);
		versionTables[1] = grow(versionTables[1], (file + 1) * 4);
		ByteBuffer.wrap(versionTables[0]).putShort(file * 2, (short) version);
		ByteBuffer.wrap(versionTables[1]).putInt(file * 4, c);
	}

	private static byte[] grow(byte[] in, int size) {
		if (in.length >= size) {
			return in;
		}
		byte[] out = new byte[size];
		System.arraycopy(in, 0, out, 0, in.length);
		return out;
	}

	/* ---------------- helpers ---------------- */

	private static int medium(byte[] b, int o) {
		return (b[o] & 0xFF) << 16 | (b[o + 1] & 0xFF) << 8 | b[o + 2] & 0xFF;
	}

	private static void putMedium(byte[] b, int o, int v) {
		b[o] = (byte) (v >> 16);
		b[o + 1] = (byte) (v >> 8);
		b[o + 2] = (byte) v;
	}

	private static int readMedium(ByteBuffer b) {
		return (b.get() & 0xFF) << 16 | (b.get() & 0xFF) << 8 | b.get() & 0xFF;
	}

	private static void writeMedium(ByteArrayOutputStream o, int v) {
		o.write(v >> 16);
		o.write(v >> 8);
		o.write(v);
	}

	@Override
	public void close() throws IOException {
		data.close();
		for (RandomAccessFile f : indices) {
			f.close();
		}
	}
}
