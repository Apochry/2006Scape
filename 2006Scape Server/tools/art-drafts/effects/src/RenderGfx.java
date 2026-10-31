package com.rs2.tools.mapgen;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.List;

/** Renders an existing cache spotanim through its sequence as a strip. Args: cacheDir outDir yawDeg gfxId... */
public class RenderGfx {
	public static void main(String[] a) throws Exception {
		try (CacheEditor c = new CacheEditor(Paths.get(a[0]))) {
			CacheEditor.Archive cfg = CacheEditor.Archive.decode(c.read(0, ConfigDefinitions.CONFIG_ARCHIVE));
			List<AnimCache.SpotAnim> gfx = AnimCache.spotAnims(cfg.get("spotanim.dat"));
			List<AnimCache.Seq> seqs = AnimCache.seqs(cfg.get("seq.dat"));
			AnimCache.loadAllFrames(c);
			double yaw = Math.toRadians(Double.parseDouble(a[2]));
			for (int i = 3; i < a.length; i++) {
				AnimCache.SpotAnim g = gfx.get(Integer.parseInt(a[i]));
				ModelData base = ModelData.decode(org.apollo.util.CompressionUtil.degzip(ByteBuffer.wrap(c.read(1, g.model))));
				FxRender.recolor(base, g.from, g.to);
				AnimCache.Seq q = seqs.get(g.seq);
				int n = Math.min(q.frameCount, 24), cell = 150;
				BufferedImage img = new BufferedImage(cell * n, cell, BufferedImage.TYPE_INT_RGB);
				int[] bb = base.bounds();
				double ext = Math.max(Math.max(bb[3] - bb[0], bb[5] - bb[2]), bb[4] - bb[1]);
				double scale = Math.min(0.9, 100.0 / Math.max(ext, 1));
				for (int k = 0; k < n; k++) {
					ModelData m = FxRender.resize(FxAnim.pose(base, AnimCache.frames.get(q.frames[k])), g.resizeXY, g.resizeZ);
					FxRender.ground(img, k * cell + cell / 2, cell * 2 / 3, scale, yaw, 0.45, cell, cell, k * cell, 0);
					FxRender.draw(img, m, k * cell + cell / 2, cell * 2 / 3, scale, yaw, 0.45, 1.0 + g.brightness / 200.0, new Rectangle(k * cell, 0, cell, cell));
					FxRender.label(img, "f" + k, k * cell + 3, 12, Color.LIGHT_GRAY);
				}
				FxRender.label(img, "gfx " + g.id + " seq " + g.seq, 3, cell - 4, Color.YELLOW);
				javax.imageio.ImageIO.write(img, "png", new java.io.File(a[1], "cache_gfx" + g.id + ".png"));
			}
		}
	}
}
