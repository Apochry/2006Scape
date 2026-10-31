package com.rs2.tools.mapgen;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * An editable 64x64 map region (4 planes) in the 317 terrain/object file format.
 *
 * Floor ids are the values stored in the map file, i.e. the flo.dat index + 1
 * (e.g. water = 6, sand = 68, grassland = 48).
 */
public final class MapRegion {

	public static final int SIZE = 64;

	/** Tile setting flags. */
	public static final int BLOCKED = 1, BRIDGE = 2, ROOF_HIDE = 4;

	public final int regionX, regionY;

	/** -1 = let the client generate a natural height, otherwise the height level (each level = 8 units). */
	public final int[][][] height = new int[4][SIZE][SIZE];
	public final int[][][] overlay = new int[4][SIZE][SIZE];
	public final int[][][] overlayShape = new int[4][SIZE][SIZE];
	public final int[][][] overlayRotation = new int[4][SIZE][SIZE];
	public final int[][][] underlay = new int[4][SIZE][SIZE];
	public final int[][][] settings = new int[4][SIZE][SIZE];
	public final List<MapObject> objects = new ArrayList<>();

	public static final class MapObject {
		public final int id, x, y, z, type, rotation;

		public MapObject(int id, int x, int y, int z, int type, int rotation) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.z = z;
			this.type = type;
			this.rotation = rotation;
		}

		int packedPosition() {
			return z << 12 | x << 6 | y;
		}

		@Override
		public String toString() {
			return "obj " + id + " @" + x + "," + y + "," + z + " type=" + type + " rot=" + rotation;
		}
	}

	public MapRegion(int regionX, int regionY) {
		this.regionX = regionX;
		this.regionY = regionY;
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < SIZE; x++) {
				java.util.Arrays.fill(height[z][x], -1);
			}
		}
	}

	public int id() {
		return regionX << 8 | regionY;
	}

	public byte[] encodeTerrain() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE; y++) {
					// every value rides in a single byte, the underlay and settings inside the opcode itself (81 + underlay,
					// 49 + settings): anything bigger silently wraps into another opcode and scrambles the rest of the
					// region. Usually it means an object id was passed where a floor was meant.
					if (underlay[z][x][y] < 0 || 81 + underlay[z][x][y] > 255 || settings[z][x][y] < 0 || settings[z][x][y] > 31
							|| overlay[z][x][y] < 0 || overlay[z][x][y] > 255) {
						throw new IllegalStateException("region " + regionX + "," + regionY + " tile " + x + "," + y + " plane " + z
								+ ": underlay " + underlay[z][x][y] + " / settings " + settings[z][x][y] + " / overlay " + overlay[z][x][y]
								+ " won't fit in the map format (is an object id being used as a floor?)");
					}
					if (overlay[z][x][y] != 0) {
						out.write(2 + (overlayShape[z][x][y] << 2) + overlayRotation[z][x][y]);
						out.write(overlay[z][x][y]);
					}
					if (settings[z][x][y] != 0) {
						out.write(49 + settings[z][x][y]);
					}
					if (underlay[z][x][y] != 0) {
						out.write(81 + underlay[z][x][y]);
					}
					int h = height[z][x][y];
					if (h < 0) {
						out.write(0);
					} else {
						out.write(1);
						out.write(Math.max(1, Math.min(255, h)));
					}
				}
			}
		}
		return out.toByteArray();
	}

	public byte[] encodeObjects() {
		List<MapObject> sorted = new ArrayList<>(objects);
		sorted.sort(Comparator.comparingInt((MapObject o) -> o.id).thenComparingInt(MapObject::packedPosition));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int lastId = -1;
		int i = 0;
		while (i < sorted.size()) {
			int id = sorted.get(i).id;
			writeSmart(out, id - lastId);
			lastId = id;
			int lastPos = 0;
			for (; i < sorted.size() && sorted.get(i).id == id; i++) {
				MapObject o = sorted.get(i);
				int pos = o.packedPosition();
				writeSmart(out, pos - lastPos + 1);
				lastPos = pos;
				out.write(o.type << 2 | o.rotation & 3);
			}
			writeSmart(out, 0);
		}
		writeSmart(out, 0);
		return out.toByteArray();
	}

	public static MapRegion decode(int regionX, int regionY, byte[] terrain, byte[] objectData) {
		MapRegion r = new MapRegion(regionX, regionY);
		ByteBuffer b = ByteBuffer.wrap(terrain);
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE; y++) {
					while (true) {
						int op = b.get() & 0xFF;
						if (op == 0) {
							r.height[z][x][y] = -1;
							break;
						}
						if (op == 1) {
							r.height[z][x][y] = b.get() & 0xFF;
							break;
						}
						if (op <= 49) {
							r.overlay[z][x][y] = b.get() & 0xFF;
							r.overlayShape[z][x][y] = (op - 2) / 4;
							r.overlayRotation[z][x][y] = (op - 2) & 3;
						} else if (op <= 81) {
							r.settings[z][x][y] = op - 49;
						} else {
							r.underlay[z][x][y] = op - 81;
						}
					}
				}
			}
		}
		if (objectData != null) {
			ByteBuffer o = ByteBuffer.wrap(objectData);
			int id = -1;
			for (int d; (d = readSmart(o)) != 0;) {
				id += d;
				int pos = 0;
				for (int pd; (pd = readSmart(o)) != 0;) {
					pos += pd - 1;
					int attr = o.get() & 0xFF;
					r.objects.add(new MapObject(id, pos >> 6 & 63, pos & 63, pos >> 12 & 3, attr >> 2, attr & 3));
				}
			}
		}
		return r;
	}

	private static void writeSmart(ByteArrayOutputStream out, int v) {
		if (v < 128) {
			out.write(v);
		} else {
			v += 32768;
			out.write(v >> 8);
			out.write(v);
		}
	}

	private static int readSmart(ByteBuffer b) {
		int peek = b.get(b.position()) & 0xFF;
		return peek < 128 ? b.get() & 0xFF : (b.getShort() & 0xFFFF) - 32768;
	}
}
