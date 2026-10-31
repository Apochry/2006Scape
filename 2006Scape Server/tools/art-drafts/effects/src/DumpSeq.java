package com.rs2.tools.mapgen;

import java.nio.file.Paths;
import java.util.*;

/** Prints a sequence's skin list and every frame's transforms. Args: cacheDir seqId... */
public class DumpSeq {
	public static void main(String[] a) throws Exception {
		try (CacheEditor c = new CacheEditor(Paths.get(a[0]))) {
			CacheEditor.Archive cfg = CacheEditor.Archive.decode(c.read(0, ConfigDefinitions.CONFIG_ARCHIVE));
			List<AnimCache.Seq> seqs = AnimCache.seqs(cfg.get("seq.dat"));
			AnimCache.loadAllFrames(c);
			for (int i = 1; i < a.length; i++) {
				AnimCache.Seq q = seqs.get(Integer.parseInt(a[i]));
				System.out.println("=== seq " + q.id + " frames=" + q.frameCount + " loopOffset=" + q.loopOffset + " prio=" + q.priority);
				AnimCache.Frame f0 = AnimCache.frames.get(q.frames[0]);
				System.out.println("file " + f0.file + " skins: " + f0.base.describe());
				for (int k = 0; k < q.frameCount; k++) {
					AnimCache.Frame f = AnimCache.frames.get(q.frames[k]);
					StringBuilder s = new StringBuilder(" f" + k + " id=" + q.frames[k] + " d=" + (q.delays[k] != 0 ? q.delays[k] : f.delay) + ": ");
					for (int o = 0; o < f.slot.length; o++) {
						int t = f.base.types[f.slot[o]];
						if (t == 0 && f.dx[o] == 0 && f.dy[o] == 0 && f.dz[o] == 0) continue;
						s.append(f.slot[o]).append("OTRS?A".charAt(Math.min(t, 5))).append('(').append(f.dx[o]).append(',').append(f.dy[o]).append(',').append(f.dz[o]).append(") ");
					}
					System.out.println(s);
				}
			}
		}
	}
}
