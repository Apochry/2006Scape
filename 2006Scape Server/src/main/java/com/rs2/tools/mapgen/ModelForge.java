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
		int gem = ConfigDefinitions.voidRamp(58, 6), gemDark = ConfigDefinitions.voidRamp(38, 7), iron = hsl(0, 0, 22), bronze = hsl(7, 4, 34),
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
		return inventoryPose(swordFrame());
	}

	/** Lays a sword built in the sword frame down in the inventory / ground pose. */
	static ModelData inventoryPose(ModelData m) {
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
		return wieldedPose(swordFrame());
	}

	/** Puts a sword built in the sword frame into the right hand, on the hand's bone. */
	static ModelData wieldedPose(ModelData m) {
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
		int glow = ConfigDefinitions.voidRamp(96, 7), glowCore = ConfigDefinitions.voidRamp(118, 6);
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
		int pane = ConfigDefinitions.voidRamp(92, 6), paneBright = ConfigDefinitions.voidRamp(112, 5);
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
		int crystal = ConfigDefinitions.voidRamp(70, 6), crystalBright = ConfigDefinitions.voidRamp(98, 7);
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


	/* ====================================================================== */
	/*                    EMBERBRAND (Heartforged's reward)                   */
	/* ====================================================================== */

	/**
	 * The Ember forged into a blade: dark starmetal with a seam of molten orange down its spine, a guard like two
	 * licking flames, and the Ember itself - still beating - set where guard meets grip.
	 */
	static ModelData emberbrandFrame() {
		ModelData m = new ModelData();
		int ember = hsl(4, 7, 70), emberHot = hsl(6, 6, 100), coal = hsl(4, 5, 30), star = hsl(42, 1, 34), starDark = hsl(42, 1, 22),
				seam = hsl(5, 7, 84), grip = hsl(2, 4, 14), gold = hsl(8, 5, 52);
		// pommel: a small ember in a gold cage
		m.tube(at(0, 0, 0), at(3, 0, 0), 0, 2.4 * K, 5, 0, ember, ember);
		m.tube(at(3, 0, 0), at(5.5, 0, 0), 2.4 * K, 1.4 * K, 5, 0, gold, gold);
		m.tube(at(5.5, 0, 0), at(17, 0, 0), 1.4 * K, 1.5 * K, 4, Math.PI / 4, grip, grip);
		for (double sp : new double[] { 8, 11, 14 }) {
			m.tube(at(sp, 0, 0), at(sp + 0.8, 0, 0), 1.75 * K, 1.75 * K, 4, Math.PI / 4, gold, gold);
		}
		// guard: two flames curling up towards the blade, with the Ember between them
		for (int side = -1; side <= 1; side += 2) {
			m.tube(at(17.5, side * 1.5, 0), at(22, side * 7.5, 0), 2.1 * K, 1.2 * K, 4, Math.PI / 4, coal, coal);
			m.tube(at(22, side * 7.5, 0), at(28, side * 6.2, 0), 1.2 * K, 0, 4, Math.PI / 4, ember, ember);
		}
		m.tube(at(17, 0, -2.2), at(21.5, 0, 0), 2.6 * K, 2.6 * K, 6, 0, emberHot, ember);
		m.tube(at(17, 0, 2.2), at(21.5, 0, 0), 2.6 * K, 2.6 * K, 6, 0, emberHot, ember);
		// blade: wide and leaf-like, dark starmetal with a glowing seam on each face
		double[][] st = { { 21.5, 2.6, 1.0 }, { 30, 4.1, 1.2 }, { 52, 4.6, 1.15 }, { 74, 3.9, 1.0 }, { 92, 2.4, 0.8 } };
		int[][] ring = new int[st.length][4];
		for (int i = 0; i < st.length; i++) {
			double[][] pts = { at(st[i][0], st[i][1], 0), at(st[i][0], 0, st[i][2]), at(st[i][0], -st[i][1], 0), at(st[i][0], 0, -st[i][2]) };
			for (int k = 0; k < 4; k++) {
				ring[i][k] = m.vertex(pts[k][0], pts[k][1], pts[k][2]);
			}
		}
		double[] tipPt = at(104, 0, 0);
		int tip = m.vertex(tipPt[0], tipPt[1], tipPt[2]);
		for (int i = 0; i + 1 < st.length; i++) {
			double[] ref = at((st[i][0] + st[i + 1][0]) / 2, 0, 0);
			for (int k = 0; k < 4; k++) {
				m.quadOut(ring[i][k], ring[i][(k + 1) % 4], ring[i + 1][(k + 1) % 4], ring[i + 1][k], k % 2 == 0 ? star : starDark, ref);
			}
		}
		for (int k = 0; k < 4; k++) {
			m.triOut(ring[st.length - 1][k], ring[st.length - 1][(k + 1) % 4], tip, k % 2 == 0 ? star : starDark, at(98, 0, 0));
		}
		// the seam: a thin glowing ridge standing proud of each face
		for (int side = -1; side <= 1; side += 2) {
			m.tube(at(23, 0, side * 1.15), at(88, 0, side * 0.85), 0.55 * K, 0.3 * K, 3, 0, seam, seam);
		}
		return m;
	}

	ModelData emberbrandInventory() {
		return inventoryPose(emberbrandFrame());
	}

	ModelData emberbrandWielded() {
		return wieldedPose(emberbrandFrame());
	}

	/* ====================================================================== */
	/*                              MORE SCENERY                              */
	/* ====================================================================== */

	/** A rough lump: rings of jittered vertices from top to bottom, faces wound outward. */
	private ModelData lump(ModelData m, double cx, double cy, double cz, double rx, double ry, double rz, int rings, int sides,
			double jitter, int[] colors) {
		int[][] idx = new int[rings + 1][];
		int top = m.vertex(cx, cy - ry, cz), bottom = m.vertex(cx, cy + ry * 0.55, cz);
		for (int r = 1; r < rings; r++) {
			double th = Math.PI * r / rings;
			idx[r] = new int[sides];
			for (int k = 0; k < sides; k++) {
				double a = Math.PI * 2 * k / sides + r * 0.4;
				double j = 1 + (random.nextDouble() - 0.5) * jitter;
				double y = -Math.cos(th) * ry;
				if (y > ry * 0.55) {
					y = ry * 0.55; // flat underneath, sitting on the ground
				}
				idx[r][k] = m.vertex(cx + Math.cos(a) * Math.sin(th) * rx * j, cy + y, cz + Math.sin(a) * Math.sin(th) * rz * j);
			}
		}
		double[] c = { cx, cy, cz };
		for (int k = 0; k < sides; k++) {
			int k2 = (k + 1) % sides;
			m.triOut(top, idx[1][k], idx[1][k2], colors[0], c);
			for (int r = 1; r + 1 < rings; r++) {
				m.quadOut(idx[r][k], idx[r][k2], idx[r + 1][k2], idx[r + 1][k], colors[Math.min(colors.length - 1, r)], c);
			}
			m.triOut(bottom, idx[rings - 1][k], idx[rings - 1][k2], colors[colors.length - 1], c);
		}
		return m;
	}

	/** Gatehold's barricade, smashed: split stakes and a cross-beam lying in the mud. */
	ModelData brokenBarricade() {
		ModelData m = new ModelData();
		int wood = hsl(5, 6, 30), woodDark = hsl(5, 5, 19), raw = hsl(6, 5, 44), rope = hsl(8, 3, 40);
		m.tube(new double[] { -58, -8, -10 }, new double[] { 54, -6, 18 }, 9, 8, 5, 0.2, wood, raw); // the fallen beam
		double[][][] stakes = { { { -40, 0, 30 }, { -64, -20, 58 } }, { { 10, 0, -30 }, { 40, -14, -54 } }, { { 30, 0, 26 }, { 46, -60, 38 } },
				{ { -18, 0, -18 }, { -24, -38, -40 } } };
		for (int i = 0; i < stakes.length; i++) {
			m.tube(stakes[i][0], stakes[i][1], 6, i % 2 == 0 ? 3 : 0, 4, i, i % 2 == 0 ? woodDark : wood, raw);
		}
		m.tube(new double[] { -6, -10, 4 }, new double[] { 6, -10, 8 }, 10, 10, 6, 0, rope, rope); // a snapped lashing
		return m;
	}

	/** The barricade rebuilt: a cheval-de-frise, crossed stakes tipped with Dorrin's iron spikes. */
	ModelData spikedBarricade() {
		ModelData m = new ModelData();
		int wood = hsl(5, 6, 30), woodDark = hsl(5, 5, 20), iron = hsl(0, 0, 40), ironLight = hsl(0, 0, 62), rope = hsl(8, 3, 40);
		m.tube(new double[] { -62, -40, 0 }, new double[] { 62, -40, 0 }, 9, 9, 6, 0, woodDark, wood); // the spine
		for (int i = 0; i < 4; i++) {
			double x = -48 + i * 32;
			for (int side = -1; side <= 1; side += 2) {
				double[] foot = { x + side * 6, 0, -side * 48 }, tip = { x - side * 6, -86, side * 46 };
				m.tube(foot, tip, 6, 4, 4, 0.3, wood, wood);
				double[] spikeEnd = { tip[0] - side * 2, tip[1] - 22, tip[2] + side * 14 };
				m.tube(tip, spikeEnd, 4.5, 0, 4, 0.3, iron, ironLight);
			}
			m.tube(new double[] { x, -36, -10 }, new double[] { x, -36, 10 }, 11, 11, 6, 0, rope, rope);
		}
		return m;
	}

	/** The fallen star: a scorched, glassy boulder split open on a seam of bright starmetal. */
	ModelData fallenStar() {
		ModelData m = new ModelData();
		int[] rock = { hsl(10, 1, 22), hsl(10, 1, 28), hsl(42, 1, 24), hsl(10, 1, 18), hsl(10, 1, 14) };
		lump(m, 0, -50, 0, 92, 62, 80, 6, 9, 0.35, rock);
		int metal = hsl(42, 3, 92), metalHot = hsl(42, 2, 116), glow = hsl(44, 5, 104);
		// starmetal shards bursting from the crack
		double[][][] shards = { { { -10, -70, -10 }, { -34, -150, -28 } }, { { 14, -78, 0 }, { 42, -148, -8 } }, { { 0, -90, 14 }, { 6, -170, 30 } },
				{ { 30, -50, 40 }, { 70, -96, 66 } }, { { -36, -46, 34 }, { -80, -88, 52 } } };
		for (int i = 0; i < shards.length; i++) {
			m.tube(shards[i][0], shards[i][1], 14 - i, 0, 4, i * 0.8, i % 2 == 0 ? metal : metalHot, metal);
		}
		// the scorched ground ring, slightly raised so it never sinks out of sight
		double[][] scorch = { { -120, 0, -40 }, { -40, 0, -122 }, { 70, 0, -110 }, { 128, 0, 10 }, { 60, 0, 116 }, { -60, 0, 110 } };
		int centre = m.vertex(0, -2, 0);
		int[] ids = new int[scorch.length];
		for (int i = 0; i < scorch.length; i++) {
			ids[i] = m.vertex(scorch[i][0], -2, scorch[i][2]);
		}
		for (int i = 0; i < ids.length; i++) {
			m.triOut(centre, ids[i], ids[(i + 1) % ids.length], i % 2 == 0 ? hsl(4, 2, 12) : hsl(6, 3, 16), new double[] { 0, 100, 0 });
		}
		m.tube(new double[] { 0, -100, 0 }, new double[] { 0, -112, 0 }, 22, 10, 6, 0, glow, glow); // heat shimmering off the top
		return m;
	}

	/**
	 * The rime altar: a boulder of the cave's own grey stone, furred with frost, with a crown of ice grown out of the
	 * top of it and the Rimeheart frozen at the centre of the crown.
	 */
	ModelData rimeAltar() {
		ModelData m = new ModelData();
		int[] stone = { hsl(36, 1, 34), hsl(36, 1, 42), hsl(36, 1, 28), hsl(36, 2, 50), hsl(36, 1, 38), hsl(36, 1, 24) };
		int ice = hsl(36, 3, 92), iceDark = hsl(36, 4, 70), iceDeep = hsl(36, 4, 56), heart = hsl(36, 1, 122), heartGlow = hsl(36, 2, 108);
		lump(m, 0, -40, 0, 70, 46, 62, 5, 8, 0.22, stone); // the boulder
		// frost settled on its shoulders: a ragged white cap
		lump(m, 0, -74, 0, 54, 14, 48, 3, 8, 0.25, new int[] { hsl(36, 1, 104), hsl(36, 2, 96), hsl(36, 1, 110) });
		double[][][] spires = { { { -30, -76, -16 }, { -54, -176, -30 } }, { { 28, -76, -14 }, { 50, -156, -26 } }, { { 0, -76, 28 }, { 6, -196, 50 } },
				{ { 10, -76, -32 }, { 18, -136, -62 } }, { { -26, -76, 24 }, { -44, -128, 44 } }, { { 32, -76, 22 }, { 52, -118, 40 } } };
		for (int i = 0; i < spires.length; i++) {
			m.tube(spires[i][0], spires[i][1], 16 - i * 1.5, 0, 4, i * 0.9, i % 2 == 0 ? iceDeep : iceDark, ice);
		}
		// the Rimeheart, waiting in a cup of ice
		m.tube(new double[] { 0, -80, 0 }, new double[] { 0, -96, 0 }, 22, 26, 6, 0.3, iceDark, ice);
		VoidEffects.gem(m, 0, -112, 0, 12, 16, 6, 0, heart, heartGlow);
		return m;
	}

	/** A ring of black stone standing on its edge, its middle full of violet nothing and pricked with stars. */
	ModelData starlessGate() {
		ModelData m = new ModelData();
		int stone = ConfigDefinitions.voidRamp(16, 1), stoneLight = ConfigDefinitions.voidRamp(24, 1), rim = ConfigDefinitions.voidRamp(40, 4), inner = ConfigDefinitions.voidRamp(30, 7), starCol = hsl(42, 0, 122);
		int seg = 14;
		double R = 120, cy = -150;
		for (int i = 0; i < seg; i++) {
			double a0 = Math.PI * 2 * i / seg, a1 = Math.PI * 2 * (i + 1) / seg;
			double[] p0 = { Math.cos(a0) * R, cy + Math.sin(a0) * R, 0 }, p1 = { Math.cos(a1) * R, cy + Math.sin(a1) * R, 0 };
			m.tube(p0, p1, 22, 22, 5, 0, i % 2 == 0 ? stone : stoneLight, stone);
			if (i % 2 == 0) { // jagged teeth of rock along the outside
				double am = (a0 + a1) / 2;
				m.tube(new double[] { Math.cos(am) * (R + 10), cy + Math.sin(am) * (R + 10), 0 },
						new double[] { Math.cos(am) * (R + 44), cy + Math.sin(am) * (R + 44), 0 }, 12, 0, 4, am, stoneLight, stone);
			}
		}
		// the void inside: a disc, two-sided, with a lighter rim of light
		int c = m.vertex(0, cy, 0);
		int[] ring = new int[seg], rimRing = new int[seg];
		for (int i = 0; i < seg; i++) {
			double a = Math.PI * 2 * i / seg;
			ring[i] = m.vertex(Math.cos(a) * (R - 40), cy + Math.sin(a) * (R - 40), 0);
			rimRing[i] = m.vertex(Math.cos(a) * (R - 12), cy + Math.sin(a) * (R - 12), 0);
		}
		for (int i = 0; i < seg; i++) {
			int i2 = (i + 1) % seg;
			m.tri(c, ring[i], ring[i2], inner);
			m.tri(c, ring[i2], ring[i], inner);
			m.quad(ring[i], rimRing[i], rimRing[i2], ring[i2], rim);
			m.quad(ring[i2], rimRing[i2], rimRing[i], ring[i], rim);
		}
		// stars hanging in the dark
		for (int i = 0; i < 7; i++) {
			double a = random.nextDouble() * Math.PI * 2, r = random.nextDouble() * (R - 50);
			double x = Math.cos(a) * r, y = cy + Math.sin(a) * r;
			m.tube(new double[] { x, y, -6 }, new double[] { x, y, 6 }, 5, 5, 4, a, starCol, starCol);
		}
		// a stone foot either side
		m.tube(new double[] { -70, 0, 0 }, new double[] { -60, -60, 0 }, 34, 18, 5, 0, stone, stone);
		m.tube(new double[] { 70, 0, 0 }, new double[] { 60, -60, 0 }, 34, 18, 5, 0, stone, stone);
		return m;
	}

	/** A star pillar: an obsidian needle with a star caught at its tip, dark or burning. */
	ModelData starPillar(boolean lit) {
		ModelData m = new ModelData();
		int ob = ConfigDefinitions.voidRamp(14, 1), obLight = ConfigDefinitions.voidRamp(22, 2), band = hsl(42, 1, 40);
		int star = lit ? hsl(42, 1, 124) : ConfigDefinitions.voidRamp(34, 2), starHalo = lit ? hsl(42, 3, 108) : ConfigDefinitions.voidRamp(26, 2);
		m.tube(new double[] { 0, 0, 0 }, new double[] { 0, -40, 0 }, 52, 44, 6, 0.2, obLight, ob);
		m.tube(new double[] { 0, -40, 0 }, new double[] { 0, -300, 0 }, 34, 16, 4, Math.PI / 4, ob, ob);
		m.tube(new double[] { 0, -120, 0 }, new double[] { 0, -132, 0 }, 32, 31, 4, Math.PI / 4, band, band);
		m.tube(new double[] { 0, -220, 0 }, new double[] { 0, -230, 0 }, 23, 22, 4, Math.PI / 4, band, band);
		// the star: two crossed double-pyramids
		double sy = -340;
		for (int k = 0; k < 3; k++) {
			double[] d = k == 0 ? new double[] { 1, 0, 0 } : k == 1 ? new double[] { 0, 1, 0 } : new double[] { 0, 0, 1 };
			double L = k == 1 ? 46 : 34;
			m.tube(new double[] { 0, sy, 0 }, new double[] { d[0] * L, sy + d[1] * L, d[2] * L }, 12, 0, 4, 0.4, star, star);
			m.tube(new double[] { 0, sy, 0 }, new double[] { -d[0] * L, sy - d[1] * L, -d[2] * L }, 12, 0, 4, 0.4, star, star);
		}
		if (lit) { // a halo of light round it
			m.tube(new double[] { 0, sy - 4, 0 }, new double[] { 0, sy + 4, 0 }, 40, 40, 8, 0, starHalo, starHalo);
		}
		return m;
	}

	/* ====================================================================== */
	/*                     THE STARLESS (the optional boss)                   */
	/* ====================================================================== */

	/**
	 * The dark between the stars, given a shape: a black sun hanging in the air, eclipsed by itself, with a corona
	 * of violet spines, a pale ring of stolen starlight and one great white eye. All vertices sit on a bone label no
	 * animation moves, so the borrowed animations leave it hanging perfectly, terribly still.
	 */
	ModelData starless() {
		ModelData m = new ModelData();
		m.skin = 200;
		int black = ConfigDefinitions.voidRamp(6, 2), blackLight = ConfigDefinitions.voidRamp(12, 3), corona = ConfigDefinitions.voidRamp(64, 7), coronaHot = ConfigDefinitions.voidRamp(96, 6),
				ring = hsl(42, 1, 110), eye = hsl(42, 0, 125), pupil = ConfigDefinitions.voidRamp(20, 7);
		double cy = -330; // it floats, well above a man's head
		lump(m, 0, cy, 0, 150, 150, 150, 8, 12, 0.08, new int[] { blackLight, black, blackLight, black, black, blackLight, black, black });
		// corona: spines all round the disc, longest at the cardinal points
		int spines = 16;
		for (int i = 0; i < spines; i++) {
			double a = Math.PI * 2 * i / spines;
			double len = i % 4 == 0 ? 250 : i % 2 == 0 ? 205 : 180;
			double[] base = { Math.cos(a) * 130, cy + Math.sin(a) * 130, -10 };
			double[] tip = { Math.cos(a + 0.12) * len, cy + Math.sin(a + 0.12) * len, -30 };
			m.tube(base, tip, i % 2 == 0 ? 30 : 22, 0, 4, a, i % 2 == 0 ? corona : coronaHot, corona);
		}
		// a ring of stolen starlight, tilted through it
		int seg = 18;
		for (int i = 0; i < seg; i++) {
			double a0 = Math.PI * 2 * i / seg, a1 = Math.PI * 2 * (i + 1) / seg, R = 230;
			double[] p0 = { Math.cos(a0) * R, cy + Math.sin(a0) * R * 0.25, Math.sin(a0) * R * 0.95 };
			double[] p1 = { Math.cos(a1) * R, cy + Math.sin(a1) * R * 0.25, Math.sin(a1) * R * 0.95 };
			m.tube(p0, p1, 9, 9, 4, 0, ring, ring);
		}
		// the eye, on the side facing you
		m.tube(new double[] { 0, cy, -138 }, new double[] { 0, cy, -158 }, 62, 48, 10, 0, eye, eye);
		m.tube(new double[] { 0, cy, -158 }, new double[] { 0, cy, -164 }, 26, 20, 8, 0, pupil, pupil);
		// shards of broken platforms orbiting it
		for (int i = 0; i < 5; i++) {
			double a = Math.PI * 2 * i / 5 + 0.5, r = 300;
			double[] p = { Math.cos(a) * r, cy + 120 - i * 50, Math.sin(a) * r };
			m.tube(p, new double[] { p[0] + 20, p[1] - 60, p[2] + 10 }, 26, 0, 4, a, blackLight, black);
			m.tube(p, new double[] { p[0] - 10, p[1] + 30, p[2] - 6 }, 26, 0, 4, a, black, black);
		}
		return m;
	}

	/** Reads a cache model, for comparing against. */
	ModelData read(int id) throws IOException {
		return ModelData.decode(org.apollo.util.CompressionUtil.degzip(ByteBuffer.wrap(cache.read(ConfigDefinitions.MODEL_INDEX, id))));
	}
}
