package com.rs2.tools.mapgen;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.*;

/**
 * Demonstrates wiring the effects into a COPY of the cache, both ways, then reads everything back.
 *
 *  Route A (no new frames): pack each model into idx1, append a spotanim.dat entry {model, EXISTING seq, resize,
 *  brightness}. Nothing else changes.
 *  Route B (authored frames): additionally write each authored frame-group file into a new idx2 file, give its
 *  frames fresh global ids (>= current anim_index length), grow anim_index / anim_version / anim_crc, append a
 *  seq.dat entry per effect and a second spotanim pointing at it.
 *
 * Args: cacheCopyDir   (never point this at the live cache)
 */
public class FxWire {
	public static void main(String[] a) throws Exception {
		List<VoidEffects.Effect> fx = VoidEffects.all();
		int[] modelIds = new int[fx.size()];
		try (CacheEditor c = new CacheEditor(Paths.get(a[0]))) {
			// ---- models (idx1) via the existing ModelForge packer (version + CRC tables)
			ModelForge forge = new ModelForge(c);
			for (int i = 0; i < fx.size(); i++) modelIds[i] = forge.pack(fx.get(i).model);
			forge.finish();

			// ---- Route B: frame files (idx2) + anim tables
			CacheEditor.Archive vl = CacheEditor.Archive.decode(c.read(0, CacheEditor.VERSIONLIST_ARCHIVE));
			byte[][] animTables = { vl.get("anim_version"), vl.get("anim_crc") };
			byte[] animIndex = vl.get("anim_index");
			int nextFrame = animIndex.length / 2;
			int nextFile = Math.max(c.fileCount(2), animTables[0].length / 2);
			int[][] newFrameIds = new int[fx.size()][];
			ByteArrayOutputStream grownIndex = new ByteArrayOutputStream();
			grownIndex.write(animIndex);
			for (int i = 0; i < fx.size(); i++) {
				VoidEffects.Effect e = fx.get(i);
				int file = nextFile++;
				byte[] data = FxAnim.encode(e.skins, e.keys, nextFrame);
				c.writeVersionedFile(2, file, data, 1, animTables);
				newFrameIds[i] = new int[e.keys.size()];
				for (int k = 0; k < e.keys.size(); k++) {
					newFrameIds[i][k] = nextFrame++;
					grownIndex.write(file >> 8);
					grownIndex.write(file);
				}
			}
			vl.put("anim_version", animTables[0]);
			vl.put("anim_crc", animTables[1]);
			vl.put("anim_index", grownIndex.toByteArray());
			c.write(0, CacheEditor.VERSIONLIST_ARCHIVE, vl.encode());

			// ---- config: seq.dat (Route B) + spotanim.dat (both routes)
			CacheEditor.Archive cfg = CacheEditor.Archive.decode(c.read(0, ConfigDefinitions.CONFIG_ARCHIVE));
			byte[] seqDat = cfg.get("seq.dat");
			int seqCount = (seqDat[0] & 0xFF) << 8 | seqDat[1] & 0xFF;
			ByteArrayOutputStream seq = new ByteArrayOutputStream();
			seq.write(seqDat, 0, seqDat.length);
			int[] newSeq = new int[fx.size()];
			for (int i = 0; i < fx.size(); i++) {
				newSeq[i] = seqCount++;
				List<FxAnim.Key> keys = fx.get(i).keys;
				seq.write(1);
				seq.write(keys.size());
				for (int k = 0; k < keys.size(); k++) {
					short2(seq, newFrameIds[i][k]);
					short2(seq, 65535);
					short2(seq, keys.get(k).delay);
				}
				seq.write(0);
			}
			byte[] seqOut = seq.toByteArray();
			seqOut[0] = (byte) (seqCount >> 8);
			seqOut[1] = (byte) seqCount;
			cfg.put("seq.dat", seqOut);

			byte[] gfxDat = cfg.get("spotanim.dat");
			int gfxCount = (gfxDat[0] & 0xFF) << 8 | gfxDat[1] & 0xFF;
			ByteArrayOutputStream g = new ByteArrayOutputStream();
			g.write(gfxDat, 0, gfxDat.length);
			StringBuilder table = new StringBuilder();
			for (int route = 0; route < 2; route++) {
				for (int i = 0; i < fx.size(); i++) {
					VoidEffects.Effect e = fx.get(i);
					int id = gfxCount++;
					int s = route == 0 ? e.reuseSeq : newSeq[i];
					g.write(1); short2(g, modelIds[i]);
					g.write(2); short2(g, s);
					if (e.resizeXY != 128) { g.write(4); short2(g, e.resizeXY); }
					if (e.resizeZ != 128) { g.write(5); short2(g, e.resizeZ); }
					g.write(7); g.write(e.brightness);
					g.write(8); g.write(e.shadow);
					g.write(0);
					table.append(String.format("gfx %d  %-16s model %d  seq %d (%s)%n", id, e.key, modelIds[i], s,
							route == 0 ? "existing frames" : "new frames " + newFrameIds[i][0] + ".." + newFrameIds[i][newFrameIds[i].length - 1]));
				}
			}
			byte[] gOut = g.toByteArray();
			gOut[0] = (byte) (gfxCount >> 8);
			gOut[1] = (byte) gfxCount;
			cfg.put("spotanim.dat", gOut);
			c.write(0, ConfigDefinitions.CONFIG_ARCHIVE, cfg.encode());
			System.out.print(table);
		}

		// ---- read everything back from the patched copy and render one strip per new gfx from the cache alone
		try (CacheEditor c = new CacheEditor(Paths.get(a[0]))) {
			CacheEditor.Archive cfg = CacheEditor.Archive.decode(c.read(0, ConfigDefinitions.CONFIG_ARCHIVE));
			List<AnimCache.SpotAnim> gfx = AnimCache.spotAnims(cfg.get("spotanim.dat"));
			List<AnimCache.Seq> seqs = AnimCache.seqs(cfg.get("seq.dat"));
			AnimCache.loadAllFrames(c);
			CacheEditor.Archive vl = CacheEditor.Archive.decode(c.read(0, CacheEditor.VERSIONLIST_ARCHIVE));
			System.out.println("readback: spotanims=" + gfx.size() + " seqs=" + seqs.size() + " frames=" + AnimCache.frames.size()
					+ " anim_index slots=" + vl.get("anim_index").length / 2 + " anim files=" + vl.get("anim_version").length / 2);
			for (int i = gfx.size() - 2 * fx.size(); i < gfx.size(); i++) {
				AnimCache.SpotAnim s = gfx.get(i);
				ModelData m = ModelData.decode(org.apollo.util.CompressionUtil.degzip(ByteBuffer.wrap(c.read(1, s.model))));
				AnimCache.Seq q = seqs.get(s.seq);
				boolean allFrames = Arrays.stream(q.frames).allMatch(f -> AnimCache.frames.containsKey(f));
				System.out.println("  " + s + " | model v=" + m.vertices.size() + " f=" + m.faces.size() + " | seq " + q.id + " frames="
						+ q.frameCount + " allFramesLoaded=" + allFrames);
			}
		}
	}

	static void short2(ByteArrayOutputStream o, int v) { o.write(v >> 8); o.write(v); }
}
