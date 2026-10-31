package com.rs2.tools.mapgen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntBinaryOperator;

import javax.imageio.ImageIO;

import org.apollo.util.CompressionUtil;

/**
 * Renders a top-down picture of part of the map (floor colours, hill shading and object footprints).
 * Handy for scouting existing areas to copy from, and for previewing the generator's output.
 *
 * Usage: MapRender x0 y0 width height out.png [scale] [labels]   (absolute world tiles, plane 0)
 */
public final class MapRender {

	/** Floor colours (flo.dat), indexed by floor id (= overlay/underlay value - 1). */
	final int[] floorRgb;
	final boolean[] floorTextured;
	/** Object names and footprint sizes from loc.dat. */
	final ConfigDefinitions.Table locs;
	private final Map<Integer, int[]> sizes = new HashMap<>();
	private final Map<Integer, String> names = new HashMap<>();

	public interface RegionSource {
		MapRegion get(int regionId);
	}

	public MapRender(CacheEditor cache) throws Exception {
		CacheEditor.Archive config = CacheEditor.Archive.decode(cache.read(0, ConfigDefinitions.CONFIG_ARCHIVE));
		ByteBuffer b = ByteBuffer.wrap(config.get("flo.dat"));
		int count = b.getShort() & 0xFFFF;
		floorRgb = new int[count];
		floorTextured = new boolean[count];
		for (int i = 0; i < count; i++) {
			for (int op; (op = b.get() & 0xFF) != 0;) {
				if (op == 1) {
					floorRgb[i] = (b.get() & 0xFF) << 16 | (b.get() & 0xFF) << 8 | b.get() & 0xFF;
				} else if (op == 2) {
					floorTextured[i] = true;
					b.get();
				} else if (op == 6) {
					while (b.get() != 10) {
						// name
					}
				} else if (op == 7) {
					b.get();
					b.get();
					b.get();
				}
			}
		}
		locs = ConfigDefinitions.load(config, "loc", false);
	}

	public int floorCount() {
		return floorRgb.length;
	}

	int[] size(int id) {
		return sizes.computeIfAbsent(id, i -> {
			if (i >= locs.size()) {
				return new int[] { 1, 1 };
			}
			ConfigDefinitions.Definition d = locs.decode(i);
			byte[] sx = d.get(14), sy = d.get(15);
			names.put(i, d.text(2));
			return new int[] { sx == null ? 1 : sx[0] & 0xFF, sy == null ? 1 : sy[0] & 0xFF };
		});
	}

	public BufferedImage render(RegionSource src, int x0, int y0, int w, int h, int scale, boolean labels) {
		BufferedImage img = new BufferedImage(w * scale, h * scale, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		IntBinaryOperator height = (x, y) -> {
			MapRegion r = src.get((x >> 6) << 8 | (y >> 6));
			if (r == null) {
				return 0;
			}
			int v = r.height[0][x & 63][y & 63];
			return v < 0 ? 0 : v * 8;
		};
		for (int x = x0; x < x0 + w; x++) {
			for (int y = y0; y < y0 + h; y++) {
				MapRegion r = src.get((x >> 6) << 8 | (y >> 6));
				int px = (x - x0) * scale, py = (y0 + h - 1 - y) * scale;
				if (r == null) {
					g.setColor(Color.BLACK);
					g.fillRect(px, py, scale, scale);
					continue;
				}
				int lx = x & 63, ly = y & 63;
				int ov = r.overlay[0][lx][ly], un = r.underlay[0][lx][ly];
				int rgb = 0x202020;
				if (ov > 0 && ov - 1 < floorRgb.length) {
					rgb = ov == 42 ? 0x000000 : ov == 6 ? 0x2a4f7a : ov == 7 ? 0x3d5a3a : ov == 5 ? 0x8a6a3a : floorRgb[ov - 1] == 0xFF00FF ? 0x5a5a5a : floorRgb[ov - 1];
				} else if (un > 0 && un - 1 < floorRgb.length) {
					rgb = floorRgb[un - 1];
				}
				// hill shading from the slope towards the north-west light
				int dh = height.applyAsInt(x - 1, y + 1) - height.applyAsInt(x, y);
				double shade = Math.max(0.45, Math.min(1.45, 1 + dh / 260.0));
				Color c = new Color(rgb);
				g.setColor(new Color(clamp(c.getRed() * shade), clamp(c.getGreen() * shade), clamp(c.getBlue() * shade)));
				g.fillRect(px, py, scale, scale);
				if ((r.settings[0][lx][ly] & 1) != 0 && ov != 6) {
					g.setColor(new Color(0, 0, 0, 60));
					g.fillRect(px, py, scale, scale);
				}
			}
		}
		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, Math.max(8, scale + 2)));
		for (int rx = x0 >> 6; rx <= (x0 + w - 1) >> 6; rx++) {
			for (int ry = y0 >> 6; ry <= (y0 + h - 1) >> 6; ry++) {
				MapRegion r = src.get(rx << 8 | ry);
				if (r == null) {
					continue;
				}
				for (MapRegion.MapObject o : r.objects) {
					if (o.z != 0) {
						continue;
					}
					int ax = (rx << 6) + o.x, ay = (ry << 6) + o.y;
					if (ax < x0 || ay < y0 || ax >= x0 + w || ay >= y0 + h) {
						continue;
					}
					int px = (ax - x0) * scale, py = (y0 + h - 1 - ay) * scale;
					if (o.type <= 3) { // walls: a line on the tile edge
						g.setColor(new Color(230, 230, 230));
						g.setStroke(new BasicStroke(Math.max(1, scale / 3f)));
						int rot = o.rotation;
						if (o.type == 2 || o.type == 0) {
							switch (rot) {
							case 0: g.drawLine(px, py, px, py + scale); break;
							case 1: g.drawLine(px, py, px + scale, py); break;
							case 2: g.drawLine(px + scale, py, px + scale, py + scale); break;
							default: g.drawLine(px, py + scale, px + scale, py + scale);
							}
						} else {
							g.fillRect(px, py, Math.max(2, scale / 3), Math.max(2, scale / 3));
						}
						continue;
					}
					if (o.type == 22) {
						g.setColor(new Color(255, 230, 120));
						g.fillOval(px + scale / 3, py + scale / 3, Math.max(2, scale / 3), Math.max(2, scale / 3));
						continue;
					}
					if (o.type >= 4 && o.type <= 8) {
						g.setColor(new Color(200, 160, 255));
						g.fillRect(px + scale / 3, py + scale / 3, Math.max(2, scale / 3), Math.max(2, scale / 3));
						continue;
					}
					int[] s = size(o.id);
					int sw = (o.rotation & 1) == 1 ? s[1] : s[0], sh = (o.rotation & 1) == 1 ? s[0] : s[1];
					g.setColor(new Color(255, 80, 80, 170));
					g.fillRect(px + 1, py - (sh - 1) * scale + 1, sw * scale - 2, sh * scale - 2);
					if (labels) {
						g.setColor(Color.WHITE);
						g.drawString(String.valueOf(o.id), px, py + scale);
					}
				}
			}
		}
		// grid every 8 tiles, labelled with absolute coordinates
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
		for (int x = x0; x < x0 + w; x++) {
			if (x % 8 == 0) {
				g.setColor(new Color(0, 255, 255, 90));
				g.drawLine((x - x0) * scale, 0, (x - x0) * scale, h * scale);
				g.setColor(Color.CYAN);
				g.drawString(String.valueOf(x), (x - x0) * scale + 2, 10);
			}
		}
		for (int y = y0; y < y0 + h; y++) {
			if (y % 8 == 0) {
				int py = (y0 + h - y) * scale;
				g.setColor(new Color(0, 255, 255, 90));
				g.drawLine(0, py, w * scale, py);
				g.setColor(Color.CYAN);
				g.drawString(String.valueOf(y), 2, py - 2);
			}
		}
		g.dispose();
		return img;
	}

	private static int clamp(double v) {
		return (int) Math.max(0, Math.min(255, v));
	}

	public static void main(String[] args) throws Exception {
		int x0 = Integer.parseInt(args[0]), y0 = Integer.parseInt(args[1]), w = Integer.parseInt(args[2]),
				h = Integer.parseInt(args[3]);
		int scale = args.length > 5 ? Integer.parseInt(args[5]) : 6;
		boolean labels = args.length > 6;
		try (CacheEditor cache = new CacheEditor(Paths.get("data/cache"))) {
			CacheEditor.Archive versions = CacheEditor.Archive.decode(cache.read(0, CacheEditor.VERSIONLIST_ARCHIVE));
			Map<Integer, CacheEditor.MapIndexEntry> index = new HashMap<>();
			for (CacheEditor.MapIndexEntry e : CacheEditor.parseMapIndex(versions.get("map_index"))) {
				index.put(e.region, e);
			}
			Map<Integer, MapRegion> loaded = new HashMap<>();
			RegionSource src = id -> loaded.computeIfAbsent(id, i -> {
				CacheEditor.MapIndexEntry e = index.get(i);
				if (e == null) {
					return null;
				}
				try {
					byte[] t = cache.read(4, e.terrainFile), o = cache.read(4, e.objectFile);
					if (t == null) {
						return null;
					}
					return MapRegion.decode(i >> 8, i & 0xFF, CompressionUtil.degzip(ByteBuffer.wrap(t)),
							o == null ? null : CompressionUtil.degzip(ByteBuffer.wrap(o)));
				} catch (Exception ex) {
					return null;
				}
			});
			MapRender m = new MapRender(cache);
			ImageIO.write(m.render(src, x0, y0, w, h, scale, labels), "png", new File(args[4]));
			if (labels) {
				m.names.forEach((id, name) -> System.out.println(id + "\t" + name));
			}
		}
	}
}
