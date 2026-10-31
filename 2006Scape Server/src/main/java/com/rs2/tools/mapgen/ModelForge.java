package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ModelData.hsl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Hand-authored models in the old-school style: few polygons, chunky silhouettes, flat-shaded facets and a muted
 * palette taken from the cache's own models (wood h5/s7, stone h10/s0, moss h13/s5, void violet h50, ghost teal h27).
 *
 * Everything is built from code, packed into the model index (idx1) with a version and CRC the client checks, and
 * wired to item / object definitions by {@link VoidRealmGenerator}.
 */
public final class ModelForge {

	/** Bone label the player's animations move a wielded longsword with (read off the rune longsword's model). */
	static final int LONGSWORD_HAND = 27;

	private final CacheEditor cache;
	private final Random random = new Random(1717);
	private int nextModel;
	private final CacheEditor.Archive versionList;
	private final byte[][] tables;

	ModelForge(CacheEditor cache) throws IOException {
		this.cache = cache;
		versionList = CacheEditor.Archive.decode(cache.read(0, CacheEditor.VERSIONLIST_ARCHIVE));
		tables = new byte[][] { versionList.get("model_version"), versionList.get("model_crc") };
		nextModel = Math.max(cache.fileCount(ConfigDefinitions.MODEL_INDEX), tables[0].length / 2);
	}

	/** Packs a model into the next free model id and returns the id. */
	int pack(ModelData m) throws IOException {
		int id = nextModel++;
		cache.writeVersionedFile(ConfigDefinitions.MODEL_INDEX, id, m.encode(), 1, tables);
		return id;
	}

	/** Writes the model version / CRC tables back. Call once after the last {@link #pack}. */
	void finish() throws IOException {
		versionList.put("model_version", tables[0]);
		versionList.put("model_crc", tables[1]);
		byte[] index = versionList.get("model_index");
		if (index.length < tables[0].length / 2) {
			byte[] grown = new byte[tables[0].length / 2];
			System.arraycopy(index, 0, grown, 0, index.length);
			versionList.put("model_index", grown);
		}
		cache.write(0, CacheEditor.VERSIONLIST_ARCHIVE, versionList.encode());
		System.out.println("Forged models up to id " + (nextModel - 1));
	}

	/* ====================================================================== */
	/*                          THE BARROWSTEEL LONGSWORD                     */
	/* ====================================================================== */

	/*
	 * Built once in a "sword frame": s runs from the pommel (0) to the tip (~106) along -z, w is across the blade (y),
	 * t is through it (x). Everything is built at 4x scale so the small parts keep their shape when rounded, then
	 * placed into the inventory pose or the wielded pose.
	 */
	private static final double K = 4;

	private static double[] at(double s, double w, double t) {
		return new double[] { t * K, w * K, -s * K };
	}

	private static ModelData swordFrame() {
		ModelData m = new ModelData();
		int gem = hsl(50, 6, 58), gemDark = hsl(50, 7, 38), iron = hsl(0, 0, 22), bronze = hsl(7, 4, 34),
				leather = hsl(5, 3, 17), wrap = hsl(5, 2, 9), steelLight = hsl(27, 1, 76), steelDark = hsl(27, 1, 58);
		// pommel: a violet gem in an iron collar
		m.tube(at(0, 0, 0), at(2.5, 0, 0), 0, 2.8 * K, 4, Math.PI / 4, gem, gem);
		m.tube(at(2.5, 0, 0), at(5, 0, 0), 2.8 * K, 0, 4, Math.PI / 4, gemDark, gemDark);
		m.tube(at(4.5, 0, 0), at(6, 0, 0), 1.9 * K, 1.6 * K, 4, Math.PI / 4, iron, iron);
		// the grip, leather wound with cord
		m.tube(at(6, 0, 0), at(17, 0, 0), 1.35 * K, 1.5 * K, 4, Math.PI / 4, leather, leather);
		for (double s : new double[] { 8.5, 11.5, 14.5 }) {
			m.tube(at(s, 0, 0), at(s + 0.9, 0, 0), 1.75 * K, 1.75 * K, 4, Math.PI / 4, wrap, wrap);
		}
		// the crossguard: a bronze block with wings swept back towards the hand
		m.box(-1.7 * K, -3.4 * K, -19.5 * K, 1.7 * K, 3.4 * K, -16.8 * K, bronze);
		for (int side = -1; side <= 1; side += 2) {
			m.tube(at(18.2, side * 3.2, 0), at(13.5, side * 10.5, 0), 1.5 * K, 0, 4, Math.PI / 4, bronze, bronze);
			// a small violet stone set into each face of the guard
			m.tube(at(18.1, 0, side * 1.6), at(18.1, 0, side * 2.6), 1.1 * K, 0, 4, 0, gem, gem);
		}
		// the blade: a diamond section that narrows below the guard, swells, then runs to a long point
		double[][] stations = { { 19.5, 3.0, 1.1 }, { 24, 2.6, 1.0 }, { 30, 3.9, 1.15 }, { 58, 3.7, 1.05 }, { 82, 3.1, 0.95 },
				{ 97, 2.1, 0.8 } };
		int[][] ring = new int[stations.length][4];
		for (int i = 0; i < stations.length; i++) {
			double s = stations[i][0], hw = stations[i][1], ht = stations[i][2];
			double[][] pts = { at(s, hw, 0), at(s, 0, ht), at(s, -hw, 0), at(s, 0, -ht) };
			for (int k = 0; k < 4; k++) {
				ring[i][k] = m.vertex(pts[k][0], pts[k][1], pts[k][2]);
			}
		}
		double[] tipPt = at(106, 0, 0);
		int tip = m.vertex(tipPt[0], tipPt[1], tipPt[2]);
		for (int i = 0; i + 1 < stations.length; i++) {
			double[] ref = at((stations[i][0] + stations[i + 1][0]) / 2, 0, 0);
			for (int k = 0; k < 4; k++) {
				int col = k % 2 == 0 ? steelLight : steelDark; // the two bevels of each face catch the light differently
				m.quadOut(ring[i][k], ring[i][(k + 1) % 4], ring[i + 1][(k + 1) % 4], ring[i + 1][k], col, ref);
			}
		}
		double[] tipRef = at(100, 0, 0);
		int last = stations.length - 1;
		for (int k = 0; k < 4; k++) {
			m.triOut(ring[last][k], ring[last][(k + 1) % 4], tip, k % 2 == 0 ? steelLight : steelDark, tipRef);
		}
		return m;
	}

	/** The sword as it lies in the inventory and on the ground (matching the rune longsword's pose). */
	ModelData swordInventory() {
		ModelData m = swordFrame();
		double dx = 60, dz = -82, len = Math.hypot(dx, dz);
		double ax = dx / len, az = dz / len; // along the blade, in the ground plane
		m.transform(v -> {
			double s = -v[2] / K, w = v[1] / K, t = v[0] / K;
			return new double[] { -31 + ax * s - az * w, -2.5 - t, 22 + az * s + ax * w }; // -t keeps it a rotation, not a mirror
		});
		return m;
	}

	/** The sword in the right hand: same place and lean as the rune longsword, every vertex on the hand's bone. */
	ModelData swordWielded() {
		ModelData m = swordFrame();
		m.transform(v -> {
			double s = -v[2] / K, w = v[1] / K, t = v[0] / K;
			double z = 9 - s;
			return new double[] { -30 + t, -98 + w - 0.075 * s, z };
		});
		for (int i = 0; i < m.vertexSkins.size(); i++) {
			m.vertexSkins.set(i, LONGSWORD_HAND);
		}
		return m;
	}

	/* ====================================================================== */
	/*                               SCENERY                                  */
	/* ====================================================================== */

	/** A Nytheran waystone: a rough leaning menhir with a violet rune cut into its face and moss round its foot. */
	ModelData waystone() {
		ModelData m = new ModelData();
		double[][] rings = { { 0, 44, 38 }, { -55, 42, 35 }, { -120, 37, 30 }, { -180, 28, 23 }, { -214, 13, 11 } };
		int sides = 6;
		int[][] idx = new int[rings.length][sides];
		for (int r = 0; r < rings.length; r++) {
			double lean = -rings[r][0] * 0.07;
			for (int s = 0; s < sides; s++) {
				double a = Math.PI * 2 * s / sides + 0.3;
				double j = r == 0 ? 1 : 0.88 + random.nextDouble() * 0.24; // hewn, not turned
				idx[r][s] = m.vertex(lean + Math.cos(a) * rings[r][1] * j, rings[r][0] + (r > 0 ? random.nextInt(9) - 4 : 0),
						Math.sin(a) * rings[r][2] * j);
			}
		}
		int[] band = { hsl(10, 0, 30), hsl(10, 0, 36), hsl(10, 0, 41), hsl(10, 0, 45) };
		int moss = hsl(13, 3, 24), mossLight = hsl(13, 3, 30);
		for (int r = 0; r + 1 < rings.length; r++) {
			double[] ref = { -(rings[r][0] + rings[r + 1][0]) / 2 * 0.07, (rings[r][0] + rings[r + 1][0]) / 2, 0 };
			for (int s = 0; s < sides; s++) {
				int col = band[r];
				if (r == 0 && s % 3 != 1) {
					col = s % 2 == 0 ? moss : mossLight; // moss creeping up from the ground
				} else if ((s + r) % 2 == 0) {
					col = band[Math.max(0, r - 1)]; // alternate facets a shade darker, like split stone
				}
				m.quadOut(idx[r][s], idx[r][(s + 1) % sides], idx[r + 1][(s + 1) % sides], idx[r + 1][s], col, ref);
			}
		}
		int top = m.vertex(-rings[rings.length - 1][0] * 0.07 + 3, -226, 2);
		double[] topRef = { 15, -200, 0 };
		for (int s = 0; s < sides; s++) {
			m.triOut(idx[rings.length - 1][s], idx[rings.length - 1][(s + 1) % sides], top, band[3], topRef);
		}
		// the rune: three glowing strokes standing just proud of the stone's south face
		int glow = hsl(50, 7, 96), glowCore = hsl(50, 6, 118);
		double face = -37; // south side (-z)
		double[][][] strokes = { { { 6, -78, face }, { 6, -158, face - 2 } }, { { 6, -150, face - 2 }, { -12, -128, face - 1 } },
				{ { 6, -112, face - 1 }, { 24, -92, face } } };
		for (double[][] st : strokes) {
			m.tube(st[0], st[1], 3.4, 3.4, 4, Math.PI / 4, glow, glowCore);
		}
		// a couple of fallen chips at its foot
		m.tube(new double[] { 52, 0, 18 }, new double[] { 50, -12, 16 }, 11, 0, 5, 0.4, band[1], band[1]);
		m.tube(new double[] { -46, 0, -30 }, new double[] { -44, -8, -28 }, 8, 0, 4, 0.1, band[0], band[0]);
		return m;
	}

	/** A void lantern on a wooden post: an iron hood over violet panes that glow at dusk. */
	ModelData voidLantern() {
		ModelData m = new ModelData();
		int wood = hsl(5, 7, 30), woodDark = hsl(5, 6, 20), iron = hsl(0, 0, 20), stone = hsl(10, 0, 38);
		int pane = hsl(50, 6, 92), paneBright = hsl(50, 5, 112);
		m.box(-17, -7, -17, 17, 0, 17, stone); // a footing stone
		m.tube(new double[] { 0, -7, 0 }, new double[] { 0, -206, 0 }, 7, 6, 4, Math.PI / 4, wood, woodDark);
		m.tube(new double[] { 0, -40, 0 }, new double[] { 0, -46, 0 }, 8.5, 8.5, 4, Math.PI / 4, iron, iron); // iron bands
		m.tube(new double[] { 0, -150, 0 }, new double[] { 0, -156, 0 }, 8.5, 8.5, 4, Math.PI / 4, iron, iron);
		// the arm and its brace
		m.box(-4, -204, -4, 44, -196, 4, woodDark);
		m.tube(new double[] { 2, -160, 0 }, new double[] { 26, -197, 0 }, 3, 3, 4, Math.PI / 4, woodDark, woodDark);
		// chain, hood, panes, base
		m.tube(new double[] { 38, -196, 0 }, new double[] { 38, -182, 0 }, 1.6, 1.6, 4, 0, iron, iron);
		m.tube(new double[] { 38, -170, 0 }, new double[] { 38, -186, 0 }, 13, 0, 6, 0, iron, iron);
		m.tube(new double[] { 38, -144, 0 }, new double[] { 38, -170, 0 }, 9, 10, 6, 0, pane, paneBright);
		m.tube(new double[] { 38, -140, 0 }, new double[] { 38, -145, 0 }, 11, 11, 6, 0, iron, iron);
		// the frame's corner bars
		for (int k = 0; k < 3; k++) {
			double a = Math.PI * 2 * k / 3 + 0.5;
			double x = 38 + Math.cos(a) * 10.5, z = Math.sin(a) * 10.5;
			m.tube(new double[] { x, -145, z }, new double[] { x, -170, z }, 1.4, 1.4, 4, 0, iron, iron);
		}
		return m;
	}

	/** A tree the Scar has emptied: a twisted grey trunk, bare limbs, and void crystal pushing up through the roots. */
	ModelData hollowedTree() {
		ModelData m = new ModelData();
		int bark = hsl(5, 2, 20), barkDark = hsl(5, 2, 13), barkGrey = hsl(7, 1, 26);
		int crystal = hsl(50, 6, 70), crystalBright = hsl(50, 7, 98);
		m.shading = 0; // trunk and limbs smooth-shaded, like the cache's trees
		double[][] trunk = { { 0, 0, 0 }, { 5, -75, 3 }, { -7, -145, 8 }, { 3, -200, 1 }, { -2, -236, 4 } };
		double[] radius = { 17, 13, 9, 5, 0 };
		int[] colors = { barkDark, bark, barkGrey, bark };
		for (int i = 0; i + 1 < trunk.length; i++) {
			m.tube(trunk[i], trunk[i + 1], radius[i], radius[i + 1], 5, i * 0.6, colors[i], colors[i]);
		}
		double[][][] limbs = { { { -4, -125, 6 }, { -62, -178, 14 } }, { { -40, -158, 11 }, { -52, -214, -8 } },
				{ { 2, -165, 4 }, { 50, -214, -22 } }, { { 30, -192, -12 }, { 62, -196, -30 } }, { { 4, -98, 1 }, { 46, -122, 38 } },
				{ { 1, -60, -2 }, { -26, -86, -40 } } };
		double[] limbR = { 6, 3, 5, 2.5, 5, 4 };
		for (int i = 0; i < limbs.length; i++) {
			m.tube(limbs[i][0], limbs[i][1], limbR[i], 0, 4, i, i % 2 == 0 ? bark : barkGrey, bark);
		}
		double[][][] roots = { { { 0, -14, 0 }, { 38, 0, 22 } }, { { 0, -14, 0 }, { -34, 0, 26 } }, { { 0, -14, 0 }, { -18, 0, -40 } },
				{ { 0, -14, 0 }, { 30, 0, -30 } } };
		for (double[][] r : roots) {
			m.tube(r[0], r[1], 9, 0, 4, 0.3, barkDark, barkDark);
		}
		m.shading = 1; // crystal: hard facets
		double[][][] shards = { { { 30, 0, 6 }, { 44, -62, 4 } }, { { 22, 0, 16 }, { 26, -36, 30 } }, { { -26, 0, -12 }, { -40, -48, -24 } },
				{ { -14, 0, 30 }, { -12, -28, 40 } } };
		double[] shardR = { 10, 7, 9, 6 };
		for (int i = 0; i < shards.length; i++) {
			m.tube(shards[i][0], shards[i][1], shardR[i], 0, 4, i * 0.7, i % 2 == 0 ? crystal : crystalBright, crystal);
		}
		return m;
	}

	/** Reads a cache model, for comparing against. */
	ModelData read(int id) throws IOException {
		return ModelData.decode(org.apollo.util.CompressionUtil.degzip(ByteBuffer.wrap(cache.read(ConfigDefinitions.MODEL_INDEX, id))));
	}
}
