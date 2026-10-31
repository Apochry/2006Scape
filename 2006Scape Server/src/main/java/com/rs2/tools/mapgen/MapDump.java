package com.rs2.tools.mapgen;

import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

import org.apollo.util.CompressionUtil;

/**
 * Debug helper: prints the objects (with type/rotation) used in an existing region.
 * Usage (from "2006Scape Server"): java -cp server.jar com.rs2.tools.mapgen.MapDump regionId [objectId]
 */
public final class MapDump {

	public static void main(String[] args) throws Exception {
		int regionId = Integer.parseInt(args[0]);
		try (CacheEditor cache = new CacheEditor(Paths.get("data/cache"))) {
			CacheEditor.Archive versions = CacheEditor.Archive.decode(cache.read(0, CacheEditor.VERSIONLIST_ARCHIVE));
			for (CacheEditor.MapIndexEntry e : CacheEditor.parseMapIndex(versions.get("map_index"))) {
				if (e.region != regionId) {
					continue;
				}
				MapRegion r = MapRegion.decode(regionId >> 8, regionId & 0xFF,
						CompressionUtil.degzip(java.nio.ByteBuffer.wrap(cache.read(4, e.terrainFile))),
						CompressionUtil.degzip(java.nio.ByteBuffer.wrap(cache.read(4, e.objectFile))));
				if (args.length > 1 && args[1].equals("planes")) {
					for (int z = 1; z < 4; z++) {
						System.out.println("== plane " + z + " (overlay/settings, '.' = empty)");
						for (int y = 63; y >= 0; y--) {
							StringBuilder row = new StringBuilder(String.format("%2d ", y));
							for (int x = 0; x < 64; x++) {
								row.append(r.overlay[z][x][y] != 0 ? (char) ('a' + r.overlay[z][x][y] % 26) : r.settings[z][x][y] != 0 ? (char) ('0' + r.settings[z][x][y]) : '.');
							}
							System.out.println(row);
						}
					}
					System.out.println("== plane 0 settings (2 = bridge)");
					for (int y = 63; y >= 0; y--) {
						StringBuilder row = new StringBuilder(String.format("%2d ", y));
						for (int x = 0; x < 64; x++) {
							row.append(r.settings[0][x][y] != 0 ? (char) ('0' + r.settings[0][x][y]) : '.');
						}
						System.out.println(row);
					}
					return;
				}
				Map<String, Integer> counts = new TreeMap<>();
				for (MapRegion.MapObject o : r.objects) {
					if (args.length > 1 && o.id != Integer.parseInt(args[1])) {
						continue;
					}
					if (args.length > 1) {
						System.out.println(o);
					}
					counts.merge(o.id + " type=" + o.type, 1, Integer::sum);
				}
				counts.forEach((k, v) -> System.out.println(k + " x" + v));
			}
		}
	}
}
