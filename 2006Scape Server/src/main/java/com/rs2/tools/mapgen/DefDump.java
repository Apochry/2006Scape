package com.rs2.tools.mapgen;

import java.nio.file.Paths;

/**
 * Lists definitions from the config archive, for finding ids to use in the generator.
 * Usage: DefDump loc|npc|obj [cacheDir]  -> "id name [size] models" per line.
 */
public final class DefDump {
	public static void main(String[] args) throws Exception {
		String kind = args[0];
		try (CacheEditor cache = new CacheEditor(Paths.get(args.length > 1 ? args[1] : "data/cache"))) {
			CacheEditor.Archive config = CacheEditor.Archive.decode(cache.read(0, ConfigDefinitions.CONFIG_ARCHIVE));
			ConfigDefinitions.Table t = ConfigDefinitions.load(config, kind, kind.equals("npc"));
			for (int i = 0; i < t.size(); i++) {
				ConfigDefinitions.Definition d = t.decode(i);
				StringBuilder sb = new StringBuilder().append(i).append('\t').append(d.text(2));
				if (kind.equals("loc")) {
					byte[] sx = d.get(14), sy = d.get(15);
					sb.append('\t').append(sx == null ? 1 : sx[0] & 0xFF).append('x').append(sy == null ? 1 : sy[0] & 0xFF);
					sb.append('\t').append(java.util.Arrays.toString(d.locModels()));
					for (int a = 30; a < 35; a++) {
						if (d.text(a) != null) {
							sb.append(" [").append(d.text(a)).append(']');
						}
					}
					if (d.get(24) != null) {
						sb.append(" anim");
					}
					if (d.get(40) != null) {
						sb.append(" recol");
					}
				}
				System.out.println(sb);
			}
		}
	}
}
