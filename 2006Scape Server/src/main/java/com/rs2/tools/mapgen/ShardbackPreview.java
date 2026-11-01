package com.rs2.tools.mapgen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

/**
 * An offline look at the {@link Shardback} without the game: every frame of every sequence posed exactly as the client
 * poses it ({@link FxAnim}, from the encoded frame file) and drawn with {@link ModelPreview}'s flat-shaded renderer from
 * a fixed 3/4 camera, over a tile grid with the model's shadow on it (so a floating or sinking paw shows).
 *
 * <p>Usage (from '2006Scape Server'): {@code ShardbackPreview outDir}. Writes {@code idle.gif}, {@code walk.gif},
 * {@code attack.gif}, {@code death.gif}, {@code block.gif}, a showcase {@code enemy.gif} of them in turn, a card
 * {@code shardback-card.png}, every frame as {@code frames/<anim>-NN.png}, and a contact sheet per animation. It also
 * prints, per frame, how far the lowest vertex is from the ground and how far each planted paw slid.
 */
public final class ShardbackPreview {

	private ShardbackPreview() {
	}

	static final int W = 320, H = 320;
	static double YAW = 0.68, PITCH = 0.42, SCALE = 0.8, OX = 152, OY = 238;
	static final Color BG = new Color(52, 56, 54), GRID = new Color(62, 67, 64), SHADOW = new Color(38, 41, 40);
	static final String[] NAMES = { "idle", "walk", "attack", "death", "block" };

	static double[] project(double x, double y, double z) {
		double cy = Math.cos(YAW), sy = Math.sin(YAW), cp = Math.cos(PITCH), sp = Math.sin(PITCH);
		double px = x * cy - z * sy, pz = x * sy + z * cy;
		return new double[] { OX + px * SCALE, OY + (y * cp - pz * sp) * SCALE, y * sp + pz * cp };
	}

	static final Function<int[], double[]> CAMERA = v -> project(v[0], v[1], v[2]);

	/** One frame: grid (scrolled by {@code groundShift} along z, for the walk), shadow, model. */
	static BufferedImage render(ModelData m, double groundShift, String caption) {
		BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setColor(BG);
		g.fillRect(0, 0, W, H);
		g.setColor(GRID);
		g.setStroke(new BasicStroke(1f));
		double shift = ((groundShift % 128) + 128) % 128;
		for (int i = -3; i <= 3; i++) {
			double[] a = project(i * 128 - 64, 0, -400), b = project(i * 128 - 64, 0, 400);
			g.drawLine((int) a[0], (int) a[1], (int) b[0], (int) b[1]);
			double z = i * 128 - 64 + shift;
			double[] c = project(-400, 0, z), d = project(400, 0, z);
			g.drawLine((int) c[0], (int) c[1], (int) d[0], (int) d[1]);
		}
		g.setColor(SHADOW); // the model flattened onto the ground, straight down
		for (int[] f : m.faces) {
			int[] xs = new int[3], ys = new int[3];
			for (int k = 0; k < 3; k++) {
				int[] v = m.vertices.get(f[k]);
				double[] p = project(v[0], 0, v[2]);
				xs[k] = (int) Math.round(p[0]);
				ys[k] = (int) Math.round(p[1]);
			}
			g.fillPolygon(new Polygon(xs, ys, 3));
		}
		ModelPreview.draw(g, m, CAMERA, 1f);
		if (caption != null) {
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			ModelPreview.label(g, caption, 10, 22, ModelPreview.GOLD, 15, true);
		}
		g.dispose();
		return img;
	}

	/** The posed models of a sequence, posed from the encoded frame file the way the client poses them. */
	static List<ModelData> posed(ModelData base, List<FxAnim.Key> keys) {
		List<ModelData> out = new ArrayList<>();
		for (AnimCache.Frame f : FxAnim.frames(Shardback.SKINS, keys)) {
			out.add(FxAnim.pose(base, f));
		}
		return out;
	}

	/* ------------------------------------------------------------ gif */

	/** A looping GIF; delays in hundredths of a second. */
	static void gif(List<BufferedImage> frames, List<Integer> delays, File out) throws IOException {
		ImageWriter w = ImageIO.getImageWritersByFormatName("gif").next();
		out.delete(); // (an image stream over an existing file would leave its old tail in place)
		try (ImageOutputStream os = ImageIO.createImageOutputStream(out)) {
			w.setOutput(os);
			w.prepareWriteSequence(null);
			for (int i = 0; i < frames.size(); i++) {
				BufferedImage img = frames.get(i);
				IIOMetadata meta = w.getDefaultImageMetadata(ImageTypeSpecifier.createFromRenderedImage(img), null);
				String f = meta.getNativeMetadataFormatName();
				IIOMetadataNode root = (IIOMetadataNode) meta.getAsTree(f);
				IIOMetadataNode gce = child(root, "GraphicControlExtension");
				gce.setAttribute("disposalMethod", "none");
				gce.setAttribute("userInputFlag", "FALSE");
				gce.setAttribute("transparentColorFlag", "FALSE");
				gce.setAttribute("delayTime", Integer.toString(delays.get(i)));
				gce.setAttribute("transparentColorIndex", "0");
				if (i == 0) {
					IIOMetadataNode apps = child(root, "ApplicationExtensions");
					IIOMetadataNode app = new IIOMetadataNode("ApplicationExtension");
					app.setAttribute("applicationID", "NETSCAPE");
					app.setAttribute("authenticationCode", "2.0");
					app.setUserObject(new byte[] { 1, 0, 0 }); // loop for ever
					apps.appendChild(app);
				}
				meta.setFromTree(f, root);
				w.writeToSequence(new IIOImage(img, null, meta), null);
			}
			w.endWriteSequence();
		}
	}

	static IIOMetadataNode child(IIOMetadataNode root, String name) {
		for (int i = 0; i < root.getLength(); i++) {
			if (root.item(i).getNodeName().equalsIgnoreCase(name)) {
				return (IIOMetadataNode) root.item(i);
			}
		}
		IIOMetadataNode n = new IIOMetadataNode(name);
		root.appendChild(n);
		return n;
	}

	/* ------------------------------------------------------------ checks */

	/** Per frame: the lowest vertex's height, and for each paw on the ground how far it moved since the last frame. */
	static void report(String name, List<ModelData> frames, List<FxAnim.Key> keys, double speed) {
		int[][] pawVerts = Shardback.soles(Shardback.model());
		double[][] last = new double[4][];
		int elapsed = 0;
		StringBuilder sb = new StringBuilder(name + ":\n");
		for (int fi = 0; fi < frames.size(); fi++) {
			ModelData m = frames.get(fi);
			sb.append(String.format("  %2d d=%-2d low=%4d ", fi, keys.get(fi).delay, Shardback.lowest(m)));
			for (int leg = 0; leg < 4; leg++) {
				double x = 0, y = 0, z = 0;
				for (int v : pawVerts[leg]) {
					x += m.vertices.get(v)[0];
					y += m.vertices.get(v)[1];
					z += m.vertices.get(v)[2];
				}
				int n = pawVerts[leg].length;
				double[] now = { x / n, y / n, z / n - speed * elapsed }; // in the ground's frame
				boolean down = now[1] > -2;
				sb.append(String.format(" paw%d y=%5.1f", leg, now[1]));
				if (down && last[leg] != null && last[leg][1] > -2) {
					sb.append(String.format(" slide=%4.1f", Math.hypot(now[0] - last[leg][0], now[2] - last[leg][2])));
				} else {
					sb.append("           ");
				}
				last[leg] = now;
			}
			sb.append('\n');
			elapsed += keys.get(fi).delay;
		}
		System.out.print(sb);
	}

	/* ------------------------------------------------------------ the card */

	static BufferedImage card(ModelData still) {
		int cw = 760, ch = 420;
		BufferedImage img = new BufferedImage(cw, ch, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(ModelPreview.BG);
		g.fillRect(0, 0, cw, ch);
		g.setColor(ModelPreview.PANEL);
		g.fillRect(10, 10, 340, 400);
		g.drawImage(render(still, 0, null), 20, 50, null);
		ModelPreview.label(g, "Haunts the rim of Starfall Crater, Nythera", 22, 34, ModelPreview.DIM, 13, false);
		int x = 368, y = 44;
		ModelPreview.label(g, Shardback.NAME, x, y, ModelPreview.GOLD, 30, true);
		ModelPreview.label(g, "(level-" + Shardback.COMBAT + ")", x + 190, y, new Color(255, 255, 0), 18, true);
		y += 24;
		ModelPreview.label(g, "\"" + Shardback.EXAMINE + "\"", x, y, ModelPreview.TEXT, 11, false);
		y += 30;
		String[][] stats = { { "Hitpoints", "" + Shardback.HITPOINTS }, { "Attack", "" + Shardback.ATTACK }, { "Strength", "" + Shardback.STRENGTH },
				{ "Defence", "" + Shardback.DEFENCE }, { "Max hit", Shardback.MAX_HIT + " (crush: a bite)" }, { "Attack speed", "4 ticks" },
				{ "Size", "2x2" }, { "Weak to", "magic - the glass rings and cracks" }, { "Resists", "ranged - arrows glance off the shards" } };
		for (String[] s : stats) {
			ModelPreview.label(g, s[0], x, y, ModelPreview.DIM, 13, false);
			ModelPreview.label(g, s[1], x + 110, y, ModelPreview.TEXT, 13, true);
			y += 19;
		}
		y += 10;
		String lore = "When the star came down on Nythera, the rock-bears denning on that hill were not killed but fused: their "
				+ "hides went to slag and the star's glass grew up through their backs. Shardbacks are what crawled out of the smoke - "
				+ "half-blind, glass-ridged, and so hungry for starmetal that Gatehold's miners won't work the crater rim without a watchman.";
		g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
		for (String line : wrap(g, lore, cw - x - 18)) {
			g.setColor(Color.BLACK);
			g.drawString(line, x + 1, y + 1);
			g.setColor(ModelPreview.TEXT);
			g.drawString(line, x, y);
			y += 16;
		}
		y += 8;
		ModelPreview.label(g, "npc " + Shardback.NPC_ID + "   model " + Shardback.MODEL_ID + "   seqs " + Shardback.SEQ_STAND + "-"
				+ Shardback.SEQ_BLOCK + "   frames " + Shardback.FRAME_BASE + "+ (file " + Shardback.FRAME_FILE + ")", x, y, ModelPreview.DIM, 11,
				false);
		y += 15;
		long bones = still.vertexSkins.stream().distinct().count();
		ModelPreview.label(g, still.faces.size() + " triangles, " + still.vertices.size() + " vertices, " + bones + " bone groups", x, y, ModelPreview.DIM,
				11, false);
		g.dispose();
		return img;
	}

	static List<String> wrap(Graphics2D g, String text, int width) {
		List<String> out = new ArrayList<>();
		StringBuilder line = new StringBuilder();
		for (String w : text.split(" ")) {
			String t = line.length() == 0 ? w : line + " " + w;
			if (g.getFontMetrics().stringWidth(t) > width && line.length() > 0) {
				out.add(line.toString());
				line = new StringBuilder(w);
			} else {
				line = new StringBuilder(t);
			}
		}
		out.add(line.toString());
		return out;
	}

	/* ------------------------------------------------------------ main */

	public static void main(String[] args) throws Exception {
		File out = new File(args[0]), framesDir = new File(out, "frames");
		framesDir.mkdirs();
		ModelData base = Shardback.model();
		if (ModelData.decode(base.encode()).faces.size() != base.faces.size()) {
			throw new IllegalStateException("model round trip failed");
		}
		System.out.println("model: " + base.vertices.size() + " vertices, " + base.faces.size() + " triangles, bounds "
				+ java.util.Arrays.toString(base.bounds()));
		List<List<FxAnim.Key>> seqs = new ArrayList<>(Shardback.sequences().values());
		// sequences() is stand, walk, attack, death, block
		List<BufferedImage> show = new ArrayList<>();
		List<Integer> showDelays = new ArrayList<>();
		double walkSpeed = 128.0 / 30;
		for (int s = 0; s < seqs.size(); s++) {
			List<FxAnim.Key> keys = seqs.get(s);
			List<ModelData> posed = posed(base, keys);
			report(NAMES[s], posed, keys, s == 1 ? walkSpeed : 0);
			List<BufferedImage> imgs = new ArrayList<>();
			List<Integer> delays = new ArrayList<>();
			int elapsed = 0;
			for (int i = 0; i < posed.size(); i++) {
				BufferedImage img = render(posed.get(i), s == 1 ? walkSpeed * elapsed : 0, null);
				imgs.add(img);
				delays.add(keys.get(i).delay * 2); // client cycles are 20 ms; GIF delays are 10 ms
				ImageIO.write(img, "png", new File(framesDir, String.format("%s-%02d.png", NAMES[s], i)));
				elapsed += keys.get(i).delay;
			}
			if (s == 3) { // the death holds on its last frame for a moment before the loop starts again
				delays.set(delays.size() - 1, 150);
			}
			gif(imgs, delays, new File(out, NAMES[s] + ".gif"));
			sheet(imgs, NAMES[s], new File(out, "sheet-" + NAMES[s] + ".png"));
			// the showcase: idle twice, walk three times, attack twice, block, death
			int reps = s == 0 ? 2 : s == 1 ? 3 : s == 2 ? 2 : 1;
			String caption = new String[] { "Idle", "Walk", "Attack", "Death", "Block" }[s];
			if (s == 4) {
				continue; // (block is a bonus; the showcase keeps to the four)
			}
			int el = 0;
			for (int r = 0; r < reps; r++) {
				for (int i = 0; i < posed.size(); i++) {
					show.add(render(posed.get(i), s == 1 ? walkSpeed * el : 0, caption));
					showDelays.add(delays.get(i));
					el += keys.get(i).delay;
				}
			}
		}
		gif(show, showDelays, new File(out, "enemy.gif"));
		ImageIO.write(card(posed(base, seqs.get(0)).get(0)), "png", new File(out, "shardback-card.png"));
		System.out.println("wrote " + out);
	}

	/** All frames of one animation side by side, for checking at a glance. */
	static void sheet(List<BufferedImage> imgs, String name, File out) throws IOException {
		int cols = Math.min(imgs.size(), 6), rows = (imgs.size() + cols - 1) / cols, s = 240;
		BufferedImage sh = new BufferedImage(cols * s, rows * s, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = sh.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		for (int i = 0; i < imgs.size(); i++) {
			g.drawImage(imgs.get(i), (i % cols) * s, (i / cols) * s, s, s, null);
			ModelPreview.label(g, name + " " + i, (i % cols) * s + 6, (i / cols) * s + 16, ModelPreview.GOLD, 12, true);
		}
		g.dispose();
		ImageIO.write(sh, "png", out);
	}
}
