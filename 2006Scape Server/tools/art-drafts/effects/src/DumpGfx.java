package com.rs2.tools.mapgen;

import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.*;

/** Dumps every spotanim with its sequence, frame file, skin list and the model's labels. Args: cacheDir [gfxId...] */
public class DumpGfx {
	public static void main(String[] a) throws Exception {
		try (CacheEditor c = new CacheEditor(Paths.get(a[0]))) {
			CacheEditor.Archive cfg = CacheEditor.Archive.decode(c.read(0, ConfigDefinitions.CONFIG_ARCHIVE));
			List<AnimCache.SpotAnim> gfx = AnimCache.spotAnims(cfg.get("spotanim.dat"));
			List<AnimCache.Seq> seqs = AnimCache.seqs(cfg.get("seq.dat"));
			AnimCache.loadAllFrames(c);
			System.out.println("spotanims=" + gfx.size() + " seqs=" + seqs.size() + " frames=" + AnimCache.frames.size() + " frameFiles=" + AnimCache.skinLists.size());
			Set<Integer> only = new HashSet<>();
			for (int i = 1; i < a.length; i++) only.add(Integer.parseInt(a[i]));
			for (AnimCache.SpotAnim g : gfx) {
				if (!only.isEmpty() && !only.contains(g.id)) continue;
				StringBuilder s = new StringBuilder(g.toString());
				ModelData m = null;
				try {
					byte[] raw = c.read(1, g.model);
					if (raw != null) m = ModelData.decode(org.apollo.util.CompressionUtil.degzip(ByteBuffer.wrap(raw)));
				} catch (Exception e) { s.append(" [model err]"); }
				if (m != null) {
					TreeSet<Integer> vs = new TreeSet<>(m.vertexSkins), fs = new TreeSet<>(m.faceSkins), al = new TreeSet<>(m.alphas), pr = new TreeSet<>(m.priorities);
					TreeSet<Integer> cols = new TreeSet<>(m.colors);
					s.append(" | v=" + m.vertices.size() + " f=" + m.faces.size() + " vskins=" + vs + " fskins=" + fs + " alpha=" + al + " prio=" + pr + " cols=" + (cols.size() > 8 ? cols.size() + "" : cols.toString()));
				}
				if (g.seq >= 0 && g.seq < seqs.size()) {
					AnimCache.Seq q = seqs.get(g.seq);
					s.append("\n    seq " + q.id + " n=" + q.frameCount + " frames=" + Arrays.toString(q.frames) + " delays=" + Arrays.toString(q.delays));
					AnimCache.Frame f0 = AnimCache.frames.get(q.frames.length > 0 ? q.frames[0] : -1);
					if (f0 != null) s.append("\n    file " + f0.file + " skins: " + f0.base.describe());
					else s.append("\n    (frame not loaded)");
				}
				System.out.println(s);
			}
		}
	}
}
