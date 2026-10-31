package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.ModelData.hsl;

import java.io.IOException;
import java.util.*;

/**
 * Quest armour for the Void saga, built on top of the cache's own WORN rune armour meshes so every vertex keeps a
 * skin (animation label) the player skeleton already knows.
 *
 * Method, per piece: decode the rune model -> recolour its placeholder colours -> build new geometry (pauldrons,
 * crest wings, emblems, tabard, knee cops...) in a scratch ModelData -> {@link #attach} it, which gives each new vertex
 * the skin of the nearest ORIGINAL vertex (per vertex for cloth that should bend, or by majority vote for rigid plates
 * so a plate never tears across two bones) -> append it with a draw priority chosen against the base model's own.
 *
 * Player labels found in the rune / kit models (front of the player is -z, the player's left is +x, y is down):
 *   1 skull, 2 neck, 3 jaw | 8 chest, 4 belly, 5 hip ring | 21 / 25 right / left upper arm, 20 / 26 forearms,
 *   17 / 23 wrists, 19 / 22 cuffs, 27 / 28 right / left hand (28 also carries the shield) | 29 / 30 / 39 / 40 / 41 / 42
 *   pelvis & belt, 34 / 35 thighs, 33 / 36 knees, 31 / 37 shins, 32 / 38 ankles, 45-48 feet | 43 / 44 / 57 skirt
 *   halves & centre (from the rune plateskirt; used for the tabard so it swings like cloth).
 */
public final class ArmourForge {

	/* ------------------------------------------------------------------ palettes */

	/** One colour scheme; the Royal Guard is the same geometry in Aurel's colours. */
	static final class Palette {
		final String name;
		final int steel, steelDark, steelLight, trim, trimDark, cloth, clothDark, gem, leather, plume, visor;

		Palette(String name, int steel, int steelDark, int steelLight, int trim, int trimDark, int cloth, int clothDark,
				int gem, int leather, int plume, int visor) {
			this.name = name;
			this.steel = steel;
			this.steelDark = steelDark;
			this.steelLight = steelLight;
			this.trim = trim;
			this.trimDark = trimDark;
			this.cloth = cloth;
			this.clothDark = clothDark;
			this.gem = gem;
			this.leather = leather;
			this.plume = plume;
			this.visor = visor;
		}
	}

	/** Gatehold Warden: blackened starmetal, ember-gold trim, a void-violet tabard and an ember stone over the heart. */
	static final Palette WARDEN = new Palette("Gatehold Warden", hsl(38, 1, 40), hsl(38, 1, 26), hsl(38, 1, 54),
			hsl(7, 5, 46), hsl(7, 5, 32), hsl(50, 4, 26), hsl(50, 4, 18), hsl(50, 6, 66), hsl(6, 2, 18), hsl(50, 4, 30), 0);

	/** Aurel's Royal Guard: polished pale starmetal, bright gold, royal violet; the visor glows ember. */
	static final Palette ROYAL = new Palette("Aurel's Royal Guard", hsl(38, 1, 70), hsl(38, 1, 52), hsl(38, 1, 84),
			hsl(8, 6, 58), hsl(7, 6, 42), hsl(50, 5, 34), hsl(50, 5, 24), hsl(8, 7, 80), hsl(6, 3, 24), hsl(50, 6, 40),
			hsl(8, 7, 70));

	/* ------------------------------------------------------------------ base ids (from the item definitions) */

	/** Rune full helm 1163: inv 2813, worn (male) 218. Rune platebody 1127: inv 2605, worn 306 + arms 164. */
	static final int RUNE_HELM_WORN = 218, RUNE_BODY_WORN = 306, RUNE_ARMS_WORN = 164, RUNE_LEGS_WORN = 268,
			RUNE_KITE_WORN = 486, RUNE_BOOTS_WORN = 4954, RUNE_SKIRT_WORN = 266, KIT_HANDS = 176;
	static final int RUNE_HELM_INV = 2813, RUNE_BODY_INV = 2605, RUNE_LEGS_INV = 2582, RUNE_KITE_INV = 2339,
			RUNE_BOOTS_INV = 5037;

	private final ModelForge forge;
	/** What {@link #attach} decided, per piece, for the report. */
	final List<String> skinLog = new ArrayList<>();

	ArmourForge(ModelForge forge) {
		this.forge = forge;
	}

	ModelData read(int id) throws IOException {
		return forge.read(id);
	}

	/* ================================================================== FULL HELM */

	/**
	 * Warden's great helm: the rune full helm (all skull label 1) with its crest re-dyed, a pair of swept gold wings
	 * (three feathers each, like the gate-guardian statues of Gatehold), a chevron brow guard over the visor and an
	 * ember stone set in it. Wings / brow are rigid on label 1, so they turn and nod with the head.
	 */
	ModelData helm(Palette p) throws IOException {
		ModelData m = recolour(read(RUNE_HELM_WORN), 61, p.steel, 926, p.plume, 0, p.visor);
		ModelData add = new ModelData();
		for (int side = -1; side <= 1; side += 2) {
			double[] root = { side * 10.5, -186, 3 };
			double[][] tips = { { side * 18, -214, 19 }, { side * 19, -201, 25 } };
			double[] widths = { 4.4, 3.6 };
			for (int k = 0; k < 2; k++) {
				double[] r = { root[0], root[1] + k * 4, root[2] + k * 2 };
				blade(add, r, tips[k], new double[] { 0, 0.3, 1 }, widths[k], 1.7, k == 1 ? p.trimDark : p.trim);
			}
			// a small boss where the wing meets the helm
			add.tube(new double[] { side * 9.5, -187, 3 }, new double[] { side * 13.5, -187, 3 }, 4.2, 3.2, 5, 0.3,
					p.trimDark, p.trim);
		}
		// chevron brow guard over the visor slits, standing proud of the face
		double[][] chevL = { { 0, -194.5 }, { -10.5, -190 }, { -10.5, -187 }, { 0, -191 } };
		double[][] chevR = { { 0, -191 }, { 10.5, -187 }, { 10.5, -190 }, { 0, -194.5 } };
		Frame front = new Frame(new double[] { 0, 0, 0 }, new double[] { 1, 0, 0 }, new double[] { 0, 1, 0 },
				new double[] { 0, 0, -1 });
		decal(add, m, front, chevL, 1, 2.2, p.trim, p.trimDark);
		decal(add, m, front, chevR, 1, 2.2, p.trim, p.trimDark);
		// the ember stone at the point of the chevron
		decal(add, m, front, diamond(0, -196.5, 2.4, 3.2), 0.5, 3.6, p.gem, p.gem);
		attach(m, add, Mode.RIGID, 7, null, "helm wings/brow");
		return m;
	}

	/* ================================================================== PLATEBODY */

	/**
	 * Warden's platebody: the rune platebody re-dyed with gold edging on the shoulders, plus heavy domed pauldrons
	 * with a gold rim and a raised ridge (rigid on the upper-arm labels 21 / 25 so they ride the shoulder when the arm
	 * swings) and the Gatehold emblem on the chest: a gold gate arch with the violet-gold Ember burning in its doorway.
	 * Returns { body, arms }: the body (worn model 1) and the re-dyed sleeves (worn model 2, rune 164).
	 */
	ModelData[] body(Palette p) throws IOException {
		ModelData m = recolour(read(RUNE_BODY_WORN), 61, p.steel, 41, p.steelDark, 24, p.trim, 12, p.trimDark);
		ModelData add = new ModelData();
		for (int side = -1; side <= 1; side += 2) {
			// pauldron: an elliptical dome sitting on the shoulder, axis tipped outward
			double[] c = { side * 25, -160, -1 };
			double[] axis = norm(new double[] { side * 0.62, -0.78, 0 });
			double[] across = { 0, 0, 1 };
			dome(add, c, axis, across, 21, 15, 15, 6, p.steelDark, p.steel);
			ring(add, c, axis, across, 22.6, 16.6, -1.5, 3.5, 6, p.trim);
			// the ridge along the top of the pauldron, front to back
			double[] top = add(c, scale(axis, 14.5));
			blade(add, add(top, new double[] { 0, 0, -15 }), add(top, new double[] { 0, 0, 17 }), axis, 5.5, 1.6, p.trim);
		}
		attach(m, add, Mode.RIGID_PER_PART, 10, null, "pauldrons");
		// chest emblem: two pillars and a pointed lintel = the Gatehold gate, the Ember stone inside it
		ModelData emb = new ModelData();
		Frame front = new Frame(new double[] { 0, 0, 0 }, new double[] { 1, 0, 0 }, new double[] { 0, 1, 0 },
				new double[] { 0, 0, -1 });
		decal(emb, m, front, rect(-9.5, -135, -6, -126), 1, 2.4, p.trim, p.trimDark); // pillars
		decal(emb, m, front, rect(6, -135, 9.5, -126), 1, 2.4, p.trim, p.trimDark);
		decal(emb, m, front, rect(-9.5, -150, -6, -135), 1, 2.4, p.trim, p.trimDark);
		decal(emb, m, front, rect(6, -150, 9.5, -135), 1, 2.4, p.trim, p.trimDark);
		decal(emb, m, front, new double[][] { { -12, -150 }, { 12, -150 }, { 0, -159 } }, 1, 2.6, p.trim, p.trimDark);
		decal(emb, m, front, rect(-6, -149, 6, -126), 1, 0.8, p.clothDark, p.clothDark); // the dark doorway
		decal(emb, m, front, diamond(0, -139, 4.2, 6.5), 0.5, 3.8, p.gem, p.trim); // the Ember
		attach(m, emb, Mode.RIGID, 4, null, "chest emblem");
		ModelData arms = recolour(read(RUNE_ARMS_WORN), 61, p.steel, 41, p.trim);
		return new ModelData[] { m, arms };
	}

	/* ================================================================== PLATELEGS */

	/**
	 * Warden's platelegs: re-dyed rune platelegs, gold knee cops with a forward spike (rigid on the knee labels 33 /
	 * 36), and a void-violet tabard with a gold hem hanging front and back from the belt. The tabard is cloth, so it
	 * is skinned per vertex from the rune PLATESKIRT's labels (belt 29/30/39-42 at the top, skirt halves 43/44 and
	 * centre 57 lower down): it swings with the stride like a skirt instead of tearing between the legs.
	 */
	ModelData legs(Palette p) throws IOException {
		ModelData m = recolour(read(RUNE_LEGS_WORN), 61, p.steel, 41, p.steelDark, 57, p.steel);
		ModelData skirt = read(RUNE_SKIRT_WORN);
		for (int side = -1; side <= 1; side += 2) {
			double x = side * 9;
			Frame f = new Frame(new double[] { 0, 0, 0 }, new double[] { 1, 0, 0 }, new double[] { 0, 1, 0 },
					new double[] { 0, 0, -1 });
			ModelData one = new ModelData();
			decal(one, m, f, new double[][] { { x, -70 }, { x + 6.5, -63 }, { x + 4.5, -55 }, { x, -52 }, { x - 4.5, -55 },
					{ x - 6.5, -63 } }, 1, 2.4, p.trim, p.trimDark);
			double[] hit = surface(m, new double[] { x, -61.5, -100 }, new double[] { 0, 0, 1 });
			double[] base = hit == null ? new double[] { x, -61.5, -12 } : hit;
			one.tube(add(base, new double[] { 0, 0, -1.5 }), add(base, new double[] { 0, 0, -7 }), 3.2, 0, 4, 0.78,
					p.trim, p.trim);
			attach(m, one, Mode.RIGID, 2, null, side < 0 ? "right knee cop" : "left knee cop");
		}
		// tabard, front (z<0) and back (z>0): a flat panel that follows the legs' front surface
		for (int face = -1; face <= 1; face += 2) {
			ModelData tab = new ModelData();
			double[][] outline = { { -7.5, -106 }, { 7.5, -106 }, { 8.5, -66 }, { 0, -60 }, { -8.5, -66 } };
			Frame f = new Frame(new double[] { 0, 0, 0 }, new double[] { 1, 0, 0 }, new double[] { 0, 1, 0 },
					new double[] { 0, 0, face });
			decalStrip(tab, m, f, outline, 1.0, 1.8, face < 0 ? p.cloth : p.clothDark, p.clothDark, 4);
			decal(tab, m, f, new double[][] { { -8.6, -70 }, { 0, -64.2 }, { 8.6, -70 }, { 8.5, -66 }, { 0, -60 },
					{ -8.5, -66 } }, 0.5, 2.3, p.trim, p.trimDark); // gold hem
			if (face < 0) {
				decal(tab, m, f, diamond(0, -88, 3.6, 5.5), 0.3, 2.6, p.trim, p.trimDark); // a little gate-stone
			}
			attach(m, tab, Mode.PER_VERTEX, 2, skirt, face < 0 ? "tabard front" : "tabard back");
		}
		return m;
	}

	/* ================================================================== KITESHIELD */

	/**
	 * Warden's kiteshield: the rune kite (every vertex on the left-hand label 28) re-dyed - gold frame and cross, a
	 * steel field - with the Gatehold gate raised over the boss and the Ember in the doorway. The emblem is built in
	 * the shield's own face plane (found from its field faces) and projected onto the surface.
	 */
	ModelData shield(Palette p) throws IOException {
		ModelData m = recolour(read(RUNE_KITE_WORN), 61, p.steel, 57, p.steelDark, 7054, p.trim);
		Frame f = shieldFrame(read(RUNE_KITE_WORN), 61); // the field faces, before re-dyeing
		ModelData add = new ModelData();
		// the field is ~70 x 55 here; u runs along the kite's length (towards the point), v across it
		double[][][] parts = { rect(-13, -13, 17, -8), rect(-13, 8, 17, 13), // pillars
				{ { -13, -16.5 }, { -13, 16.5 }, { -27, 0 } }, // pointed lintel
		};
		for (double[][] q : parts) {
			decal(add, m, f, q, 1, 3.2, p.trim, p.trimDark);
		}
		decal(add, m, f, rect(-12.5, -8, 17, 8), 1, 1.2, p.clothDark, p.clothDark);
		decal(add, m, f, diamond(1.5, 0, 9, 6), 0.5, 4.8, p.gem, p.trim);
		attach(m, add, Mode.RIGID, 11, null, "shield emblem");
		return m;
	}

	/* ================================================================== BOOTS & GAUNTLETS */

	/** Warden's sabatons: rune boots in charcoal leather with steel plates and gold toe caps (on the toe labels). */
	ModelData boots(Palette p) throws IOException {
		ModelData m = recolour(read(RUNE_BOOTS_WORN), 5400, p.leather, 5648, p.steelDark, 61, p.steel);
		ModelData add = new ModelData();
		for (int side = -1; side <= 1; side += 2) {
			double x = side * 9.5;
			add.tube(new double[] { x, -3.5, -15 }, new double[] { x, -1, -22 }, 4.2, 0, 4, 0.78, p.trim, p.trim);
		}
		attach(m, add, Mode.RIGID_PER_PART, 0, null, "toe caps");
		return m;
	}

	/**
	 * Warden's gauntlets: there are no rune gloves in this cache, so they are built from the default HANDS kit model
	 * (176): the bare hands become steel, and a flared gold-rimmed cuff is added round each wrist (rigid on the cuff
	 * labels 19 / 22, so it follows the forearm while the fingers keep labels 27 / 28).
	 */
	ModelData gauntlets(Palette p) throws IOException {
		ModelData m = recolour(read(KIT_HANDS), 4550, p.steelDark, 43072, p.steel);
		ModelData add = new ModelData();
		for (int side = -1; side <= 1; side += 2) {
			double x = side * 28.5;
			add.tube(new double[] { x, -116, 1 }, new double[] { x, -104, 1 }, 5.4, 7.6, 6, 0.26, p.steel, p.steelDark);
			add.tube(new double[] { x, -105, 1 }, new double[] { x, -102.5, 1 }, 7.9, 7.9, 6, 0.26, p.trim, p.trim);
		}
		attach(m, add, Mode.RIGID_PER_PART, 10, null, "gauntlet cuffs");
		return m;
	}

	/* ================================================================== INVENTORY MODELS */

	/**
	 * Turns a worn model into an inventory model in the same frame as the rune item's own inventory model (so the
	 * rune definition's zoom / rotation / offsets can be reused): the axis-aligned rotation that best maps the rune
	 * WORN bounds onto the rune INVENTORY bounds is found, applied, and the bounds re-centred. Skins are cleared.
	 */
	ModelData inventory(ModelData worn, int runeWorn, int runeInv) throws IOException {
		return inventory(worn, new int[] { runeWorn }, runeInv);
	}

	/** As above with several rune worn models forming the reference (the platebody icon includes its sleeves). */
	ModelData inventory(ModelData worn, int[] runeWorn, int runeInv) throws IOException {
		ModelData rw = new ModelData();
		for (int id : runeWorn) {
			merge(rw, read(id), null);
		}
		ModelData ri = read(runeInv);
		double[] cw = centre(rw.bounds()), ci = centre(ri.bounds());
		double best = 1e18;
		int[][] bestR = null;
		for (int[][] r : rotations()) { // chamfer distance between the rotated rune worn mesh and the rune inventory mesh
			double err = 0;
			for (int[] v : rw.vertices) {
				double[] p = rot(r, sub(d(v), cw));
				double nearest = 1e18;
				for (int[] q : ri.vertices) {
					double[] dd = sub(p, sub(d(q), ci));
					nearest = Math.min(nearest, dot(dd, dd));
				}
				err += nearest;
			}
			if (err < best) {
				best = err;
				bestR = r;
			}
		}
		final int[][] R = bestR;
		double[] rc = rot(R, cw);
		double[] shift = sub(ci, rc);
		ModelData m = Anim.copy(worn);
		m.transform(v -> add(rot(R, v), shift));
		for (int i = 0; i < m.vertexSkins.size(); i++) {
			m.vertexSkins.set(i, 0);
		}
		for (int i = 0; i < m.faceSkins.size(); i++) {
			m.faceSkins.set(i, 0);
		}
		return m;
	}

	/**
	 * The kite icon is not an axis-aligned turn of the worn kite, so the shield is moved frame to frame: the face
	 * frame of the rune worn kite (its field faces, colour 61) onto the face frame of the rune inventory kite, whose
	 * normal is taken on the side the inventory camera (the item's rotation opcodes 5 / 6) sees.
	 */
	ModelData shieldInventory(ModelData worn, double yaw, double pitch) throws IOException {
		Frame a = shieldFrame(read(RUNE_KITE_WORN), 61);
		Frame b = shieldFrame(read(RUNE_KITE_INV), 61, null);
		double cy = Math.cos(yaw), sy = Math.sin(yaw), cp = Math.cos(pitch), sp = Math.sin(pitch);
		double depth = b.n[1] * sp + (b.n[0] * sy + b.n[2] * cy) * cp; // Preview's z2 of the normal
		if (depth > 0) { // facing away from the camera: flip to the other side
			b = new Frame(b.o, b.u, scale(b.v, -1), scale(b.n, -1));
		}
		final Frame A = a, B = b;
		ModelData m = Anim.copy(worn);
		m.transform(v -> {
			double[] q = sub(v, A.o);
			double cu = dot(q, A.u), cv = dot(q, A.v), cn = dot(q, A.n);
			return add(B.o, add(scale(B.u, cu), add(scale(B.v, cv), scale(B.n, cn))));
		});
		for (int i = 0; i < m.vertexSkins.size(); i++) {
			m.vertexSkins.set(i, 0);
		}
		for (int i = 0; i < m.faceSkins.size(); i++) {
			m.faceSkins.set(i, 0);
		}
		return m;
	}

	/** Gauntlets have no rune counterpart: the pair is drawn together side by side, palms down, skins cleared. */
	ModelData gauntletsInventory(ModelData worn) {
		ModelData m = Anim.copy(worn);
		m.transform(v -> {
			double x = v[0] - Math.signum(v[0]) * 17; // bring the hands together
			return new double[] { x, -(v[2]) - 4, (v[1] + 103) }; // lay them down: fingers point to +z
		});
		for (int i = 0; i < m.vertexSkins.size(); i++) {
			m.vertexSkins.set(i, 0);
		}
		for (int i = 0; i < m.faceSkins.size(); i++) {
			m.faceSkins.set(i, 0);
		}
		return m;
	}

	static double[] centre(int[] b) {
		return new double[] { (b[0] + b[3]) / 2.0, (b[1] + b[4]) / 2.0, (b[2] + b[5]) / 2.0 };
	}

	static double[] rot(int[][] r, double[] v) {
		return new double[] { r[0][0] * v[0] + r[0][1] * v[1] + r[0][2] * v[2], r[1][0] * v[0] + r[1][1] * v[1] + r[1][2] * v[2],
				r[2][0] * v[0] + r[2][1] * v[1] + r[2][2] * v[2] };
	}

	static List<int[][]> rotations() {
		List<int[][]> out = new ArrayList<>();
		int[][] perms = { { 0, 1, 2 }, { 0, 2, 1 }, { 1, 0, 2 }, { 1, 2, 0 }, { 2, 0, 1 }, { 2, 1, 0 } };
		for (int[] pm : perms) {
			for (int s = 0; s < 8; s++) {
				int[][] r = new int[3][3];
				for (int k = 0; k < 3; k++) {
					r[k][pm[k]] = (s >> k & 1) == 0 ? 1 : -1;
				}
				if (det(r) == 1) {
					out.add(r);
				}
			}
		}
		return out;
	}

	static int det(int[][] r) {
		return r[0][0] * (r[1][1] * r[2][2] - r[1][2] * r[2][1]) - r[0][1] * (r[1][0] * r[2][2] - r[1][2] * r[2][0])
				+ r[0][2] * (r[1][0] * r[2][1] - r[1][1] * r[2][0]);
	}

	static int[] rotatedBounds(int[] b, int[][] r) {
		int[] o = { Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE,
				Integer.MIN_VALUE };
		for (int c = 0; c < 8; c++) {
			int[] p = { b[(c & 1) == 0 ? 0 : 3], b[(c & 2) == 0 ? 1 : 4], b[(c & 4) == 0 ? 2 : 5] };
			for (int k = 0; k < 3; k++) {
				int v = r[k][0] * p[0] + r[k][1] * p[1] + r[k][2] * p[2];
				o[k] = Math.min(o[k], v);
				o[k + 3] = Math.max(o[k + 3], v);
			}
		}
		return o;
	}

	/* ================================================================== skinning */

	enum Mode {
		/** Each new vertex copies the label of the nearest original vertex (cloth that should bend). */
		PER_VERTEX,
		/** The whole piece takes the most common nearest label (a rigid plate never tears across bones). */
		RIGID,
		/** As RIGID, but per connected part (e.g. one pauldron per shoulder in one call). */
		RIGID_PER_PART
	}

	/**
	 * Appends {@code add} to {@code base}. New vertices take the skin of the nearest vertex of {@code donor} (the base
	 * model itself when null) as described by {@code mode}; new faces get draw priority {@code prio}, no face skin and
	 * smooth shading like the rune meshes.
	 */
	void attach(ModelData base, ModelData add, Mode mode, int prio, ModelData donor, String what) {
		ModelData ref = donor == null ? base : donor;
		int origCount = donor == null ? base.vertices.size() : ref.vertices.size();
		int n = add.vertices.size();
		int[] nearest = new int[n];
		for (int i = 0; i < n; i++) {
			int[] v = add.vertices.get(i);
			long bestD = Long.MAX_VALUE;
			int best = 0;
			for (int j = 0; j < origCount; j++) {
				int[] o = ref.vertices.get(j);
				long dx = v[0] - o[0], dy = v[1] - o[1], dz = v[2] - o[2], d = dx * dx + dy * dy + dz * dz;
				if (d < bestD) {
					bestD = d;
					best = j;
				}
			}
			nearest[i] = ref.vertexSkins.get(best);
		}
		int[] skins = nearest.clone();
		if (mode != Mode.PER_VERTEX) {
			int[] part = mode == Mode.RIGID ? new int[n] : components(add);
			Map<Integer, Map<Integer, Integer>> votes = new HashMap<>();
			for (int i = 0; i < n; i++) {
				votes.computeIfAbsent(part[i], k -> new HashMap<>()).merge(nearest[i], 1, Integer::sum);
			}
			for (int i = 0; i < n; i++) {
				skins[i] = Collections.max(votes.get(part[i]).entrySet(), Map.Entry.comparingByValue()).getKey();
			}
		}
		TreeMap<Integer, Integer> used = new TreeMap<>();
		for (int s : skins) {
			used.merge(s, 1, Integer::sum);
		}
		skinLog.add(what + " (" + mode + ", prio " + prio + "): labels " + used + " (label=vertex count)");
		for (int i = 0; i < n; i++) {
			add.vertexSkins.set(i, skins[i]);
		}
		add.priorities.replaceAll(x -> prio);
		add.renderTypes.replaceAll(x -> 0);
		merge(base, add, null);
	}

	/** Connected components of a model's faces, per vertex. */
	static int[] components(ModelData m) {
		int n = m.vertices.size();
		int[] parent = new int[n];
		for (int i = 0; i < n; i++) {
			parent[i] = i;
		}
		for (int[] f : m.faces) {
			union(parent, f[0], f[1]);
			union(parent, f[1], f[2]);
		}
		// pieces that touch (share a position) belong together too, e.g. a pauldron's rim and its dome
		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				int[] a = m.vertices.get(i), b = m.vertices.get(j);
				if (Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]) + Math.abs(a[2] - b[2]) <= 6 && sameSide(a, b)) {
					union(parent, i, j);
				}
			}
		}
		int[] out = new int[n];
		for (int i = 0; i < n; i++) {
			out[i] = find(parent, i);
		}
		return out;
	}

	private static boolean sameSide(int[] a, int[] b) {
		return Math.signum(a[0]) == Math.signum(b[0]) || Math.abs(a[0]) < 3 || Math.abs(b[0]) < 3;
	}

	private static int find(int[] p, int i) {
		while (p[i] != i) {
			p[i] = p[p[i]];
			i = p[i];
		}
		return i;
	}

	private static void union(int[] p, int a, int b) {
		p[find(p, a)] = find(p, b);
	}

	/** Appends all of {@code add} (vertices with their skins, faces with all attributes) to {@code base}. */
	static ModelData merge(ModelData base, ModelData add, Object unused) {
		int off = base.vertices.size();
		for (int i = 0; i < add.vertices.size(); i++) {
			base.vertices.add(add.vertices.get(i).clone());
			base.vertexSkins.add(add.vertexSkins.get(i));
		}
		for (int i = 0; i < add.faces.size(); i++) {
			int[] f = add.faces.get(i);
			base.faces.add(new int[] { f[0] + off, f[1] + off, f[2] + off });
			base.colors.add(add.colors.get(i));
			base.renderTypes.add(add.renderTypes.get(i));
			base.priorities.add(add.priorities.get(i));
			base.alphas.add(add.alphas.get(i));
			base.faceSkins.add(add.faceSkins.get(i));
		}
		return base;
	}

	static ModelData mergeAll(ModelData... ms) {
		ModelData o = new ModelData();
		for (ModelData m : ms) {
			merge(o, m, null);
		}
		return o;
	}

	static ModelData recolour(ModelData m, int... pairs) {
		for (int i = 0; i < m.colors.size(); i++) {
			for (int k = 0; k < pairs.length; k += 2) {
				if (m.colors.get(i) == pairs[k]) {
					m.colors.set(i, pairs[k + 1]);
					break;
				}
			}
		}
		return m;
	}

	/* ================================================================== geometry helpers */

	/** A plane frame: origin, u and v in the plane, n the outward normal. */
	static final class Frame {
		final double[] o, u, v, n;

		Frame(double[] o, double[] u, double[] v, double[] n) {
			this.o = o;
			this.u = u;
			this.v = v;
			this.n = n;
		}

		double[] at(double a, double b) {
			return add(o, add(scale(u, a), scale(v, b)));
		}
	}

	/** The shield's face frame: n = mean normal of the faces of colour {@code col} turned outward (+x), u its length. */
	static Frame shieldFrame(ModelData m, int col) {
		return shieldFrame(m, col, new double[] { 1, 0, 0 });
	}

	/** As above, face normals turned to agree with {@code hint} (null: with the largest face's normal). */
	static Frame shieldFrame(ModelData m, int col, double[] hint) {
		if (hint == null) {
			double big = -1;
			for (int i = 0; i < m.faces.size(); i++) {
				if (m.colors.get(i) != col) {
					continue;
				}
				int[] f = m.faces.get(i);
				double[] a = d(m.vertices.get(f[0]));
				double[] fn = ModelData.cross(sub(d(m.vertices.get(f[1])), a), sub(d(m.vertices.get(f[2])), a));
				if (len(fn) > big) {
					big = len(fn);
					hint = fn;
				}
			}
		}
		double[] n = new double[3], c = new double[3];
		double area = 0;
		List<double[]> pts = new ArrayList<>();
		for (int i = 0; i < m.faces.size(); i++) {
			if (m.colors.get(i) != col) {
				continue;
			}
			int[] f = m.faces.get(i);
			double[] a = d(m.vertices.get(f[0])), b = d(m.vertices.get(f[1])), cc = d(m.vertices.get(f[2]));
			double[] fn = ModelData.cross(sub(b, a), sub(cc, a));
			double ar = len(fn);
			if (dot(fn, hint) < 0) {
				fn = scale(fn, -1);
			}
			n = add(n, fn);
			c = add(c, scale(add(add(a, b), cc), ar / 3));
			area += ar;
			pts.add(a);
			pts.add(b);
			pts.add(cc);
		}
		n = norm(n);
		c = scale(c, 1 / area);
		double far = -1;
		double[] u = { 0, 1, 0 };
		for (double[] p : pts) {
			double[] q = sub(p, c);
			q = sub(q, scale(n, dot(q, n)));
			if (len(q) > far) {
				far = len(q);
				u = norm(q);
			}
		}
		double[] v = norm(ModelData.cross(n, u));
		return new Frame(c, u, v, n);
	}

	/** Casts a ray from {@code from} along {@code dir} against all triangles of {@code m}; the nearest hit or null. */
	static double[] surface(ModelData m, double[] from, double[] dir) {
		double best = Double.MAX_VALUE;
		for (int[] f : m.faces) {
			double[] a = d(m.vertices.get(f[0])), b = d(m.vertices.get(f[1])), c = d(m.vertices.get(f[2]));
			double[] e1 = sub(b, a), e2 = sub(c, a), pv = ModelData.cross(dir, e2);
			double det = dot(e1, pv);
			if (Math.abs(det) < 1e-9) {
				continue;
			}
			double[] tv = sub(from, a);
			double uu = dot(tv, pv) / det;
			if (uu < 0 || uu > 1) {
				continue;
			}
			double[] qv = ModelData.cross(tv, e1);
			double vv = dot(dir, qv) / det;
			if (vv < 0 || uu + vv > 1) {
				continue;
			}
			double t = dot(e2, qv) / det;
			if (t > 0 && t < best) {
				best = t;
			}
		}
		return best == Double.MAX_VALUE ? null : add(from, scale(dir, best));
	}

	/**
	 * A raised plate following a surface: the convex outline (in frame coordinates) is projected onto {@code onto}
	 * along -n, then extruded from {@code hIn} below to {@code hOut} above the surface. Top faces {@code top}, sides
	 * {@code side}.
	 */
	static void decal(ModelData m, ModelData onto, Frame f, double[][] outline, double hIn, double hOut, int top,
			int side) {
		int k = outline.length;
		int[] lo = new int[k], hi = new int[k];
		double[] centre = new double[3];
		for (int i = 0; i < k; i++) {
			double[] p = project(onto, f, outline[i][0], outline[i][1]);
			double[] a = add(p, scale(f.n, -hIn)), b = add(p, scale(f.n, hOut));
			lo[i] = m.vertex(a[0], a[1], a[2]);
			hi[i] = m.vertex(b[0], b[1], b[2]);
			centre = add(centre, scale(p, 1.0 / k));
		}
		// the top is a fan round the projected centre, so it follows a bulging surface (a knee) instead of cutting it
		double[] ca = new double[2];
		for (double[] q : outline) {
			ca[0] += q[0] / k;
			ca[1] += q[1] / k;
		}
		double[] cTop = add(project(onto, f, ca[0], ca[1]), scale(f.n, hOut));
		int mid = m.vertex(cTop[0], cTop[1], cTop[2]);
		double[] below = add(centre, scale(f.n, -20));
		for (int i = 0; i < k; i++) {
			m.triOut(hi[i], hi[(i + 1) % k], mid, top, below);
		}
		for (int i = 0; i < k; i++) {
			int j = (i + 1) % k;
			m.quadOut(lo[i], lo[j], hi[j], hi[i], side, centre);
		}
	}

	/** As {@link #decal} but the outline is subdivided into {@code rows} horizontal bands so it can bend (cloth). */
	static void decalStrip(ModelData m, ModelData onto, Frame f, double[][] outline, double hIn, double hOut, int top,
			int side, int rows) {
		// outline here: top-left, top-right, right shoulder, bottom point, left shoulder (a pennant shape)
		double[] tl = outline[0], tr = outline[1], r = outline[2], bot = outline[3], l = outline[4];
		int[][] grid = new int[rows + 1][2];
		int[][] gridLo = new int[rows + 1][2];
		double[] centre = project(onto, f, (tl[0] + tr[0]) / 2, (tl[1] + bot[1]) / 2);
		for (int i = 0; i <= rows; i++) {
			double t = (double) i / rows;
			double[][] pts = { lerp2(tl, l, t), lerp2(tr, r, t) };
			for (int s = 0; s < 2; s++) {
				double[] p = project(onto, f, pts[s][0], pts[s][1]);
				double[] h = add(p, scale(f.n, hOut)), lw = add(p, scale(f.n, -hIn));
				grid[i][s] = m.vertex(h[0], h[1], h[2]);
				gridLo[i][s] = m.vertex(lw[0], lw[1], lw[2]);
			}
		}
		double[] pb = project(onto, f, bot[0], bot[1]);
		double[] hb = add(pb, scale(f.n, hOut)), lb = add(pb, scale(f.n, -hIn));
		int tip = m.vertex(hb[0], hb[1], hb[2]), tipLo = m.vertex(lb[0], lb[1], lb[2]);
		for (int i = 0; i < rows; i++) {
			double[] below = add(project(onto, f, 0, lerp2(tl, l, (i + 0.5) / rows)[1]), scale(f.n, -20));
			m.quadOut(grid[i][0], grid[i][1], grid[i + 1][1], grid[i + 1][0], top, below);
			m.quadOut(gridLo[i][0], grid[i][0], grid[i + 1][0], gridLo[i + 1][0], side, centre);
			m.quadOut(gridLo[i][1], grid[i][1], grid[i + 1][1], gridLo[i + 1][1], side, centre);
		}
		m.triOut(grid[rows][0], grid[rows][1], tip, top, add(pb, scale(f.n, -20)));
		m.quadOut(gridLo[rows][0], grid[rows][0], tip, tipLo, side, centre);
		m.quadOut(gridLo[rows][1], grid[rows][1], tip, tipLo, side, centre);
		m.quadOut(gridLo[0][0], grid[0][0], grid[0][1], gridLo[0][1], side, centre);
	}

	static double[] lerp2(double[] a, double[] b, double t) {
		return new double[] { a[0] + (b[0] - a[0]) * t, a[1] + (b[1] - a[1]) * t };
	}

	/** Frame point (a, b) dropped onto the surface along -n (or left in the plane if the ray misses). */
	static double[] project(ModelData onto, Frame f, double a, double b) {
		double[] p = f.at(a, b);
		double[] hit = surface(onto, add(p, scale(f.n, 150)), scale(f.n, -1));
		return hit == null ? p : hit;
	}

	static double[][] rect(double x0, double y0, double x1, double y1) {
		return new double[][] { { x0, y0 }, { x1, y0 }, { x1, y1 }, { x0, y1 } };
	}

	static double[][] diamond(double x, double y, double hw, double hh) {
		return new double[][] { { x, y - hh }, { x + hw, y }, { x, y + hh }, { x - hw, y } };
	}

	/**
	 * A flat tapering blade (diamond cross-section) from p0 to a point at p1: half-width {@code w} along the part of
	 * {@code widthHint} perpendicular to the blade, half-thickness {@code t}.
	 */
	static void blade(ModelData m, double[] p0, double[] p1, double[] widthHint, double w, double t, int col) {
		double[] d = norm(sub(p1, p0));
		double[] wd = norm(sub(widthHint, scale(d, dot(widthHint, d))));
		double[] td = norm(ModelData.cross(d, wd));
		double[] mid = add(p0, scale(sub(p1, p0), 0.3));
		double[][] ring = { add(mid, scale(wd, w)), add(mid, scale(td, t)), add(mid, scale(wd, -w)), add(mid, scale(td, -t)) };
		int[] r = new int[4];
		for (int i = 0; i < 4; i++) {
			r[i] = m.vertex(ring[i][0], ring[i][1], ring[i][2]);
		}
		int a = m.vertex(p0[0], p0[1], p0[2]), b = m.vertex(p1[0], p1[1], p1[2]);
		for (int i = 0; i < 4; i++) {
			int j = (i + 1) % 4;
			m.triOut(r[i], r[j], b, col, mid);
			m.triOut(r[i], r[j], a, col, mid);
		}
	}

	/** An elliptical dome on base centre c, rising {@code h} along {@code axis}; radii ra along {@code across}, rb. */
	static void dome(ModelData m, double[] c, double[] axis, double[] across, double ra, double rb, double h, int sides,
			int col, int topCol) {
		double[] e1 = norm(sub(across, scale(axis, dot(across, axis))));
		double[] e2 = norm(ModelData.cross(axis, e1));
		double[][] prof = { { 0, 1 }, { 0.5, 0.93 }, { 0.85, 0.62 } };
		int[][] idx = new int[prof.length][sides];
		for (int r = 0; r < prof.length; r++) {
			for (int s = 0; s < sides; s++) {
				double ang = Math.PI * 2 * s / sides + Math.PI / sides;
				double[] p = add(add(c, scale(axis, prof[r][0] * h)),
						add(scale(e1, Math.cos(ang) * ra * prof[r][1]), scale(e2, Math.sin(ang) * rb * prof[r][1])));
				idx[r][s] = m.vertex(p[0], p[1], p[2]);
			}
		}
		double[] apex = add(c, scale(axis, h));
		int ap = m.vertex(apex[0], apex[1], apex[2]);
		for (int r = 0; r + 1 < prof.length; r++) {
			double[] ref = add(c, scale(axis, (prof[r][0] + prof[r + 1][0]) / 2 * h));
			for (int s = 0; s < sides; s++) {
				m.quadOut(idx[r][s], idx[r][(s + 1) % sides], idx[r + 1][(s + 1) % sides], idx[r + 1][s], r == 0 ? col : topCol, ref);
			}
		}
		for (int s = 0; s < sides; s++) {
			m.triOut(idx[prof.length - 1][s], idx[prof.length - 1][(s + 1) % sides], ap, topCol, c);
		}
		double[] under = add(c, scale(axis, 30));
		int cc = m.vertex(c[0], c[1], c[2]);
		for (int s = 0; s < sides; s++) {
			m.triOut(idx[0][s], idx[0][(s + 1) % sides], cc, col, under);
		}
	}

	/** A short elliptical band round {@code axis} from h0 to h1 (a rim). */
	static void ring(ModelData m, double[] c, double[] axis, double[] across, double ra, double rb, double h0, double h1,
			int sides, int col) {
		double[] e1 = norm(sub(across, scale(axis, dot(across, axis))));
		double[] e2 = norm(ModelData.cross(axis, e1));
		int[][] idx = new int[2][sides];
		double[] hs = { h0, h1 };
		for (int r = 0; r < 2; r++) {
			for (int s = 0; s < sides; s++) {
				double ang = Math.PI * 2 * s / sides + Math.PI / sides;
				double[] p = add(add(c, scale(axis, hs[r])), add(scale(e1, Math.cos(ang) * ra), scale(e2, Math.sin(ang) * rb)));
				idx[r][s] = m.vertex(p[0], p[1], p[2]);
			}
		}
		double[] ref = add(c, scale(axis, (h0 + h1) / 2));
		for (int s = 0; s < sides; s++) {
			m.quadOut(idx[0][s], idx[0][(s + 1) % sides], idx[1][(s + 1) % sides], idx[1][s], col, ref);
		}
		double[] below = add(c, scale(axis, h1 + 30));
		for (int s = 1; s + 1 < sides; s++) {
			m.triOut(idx[0][0], idx[0][s], idx[0][s + 1], col, below); // closes the underside
		}
	}

	static double[] d(int[] v) {
		return new double[] { v[0], v[1], v[2] };
	}

	static double[] add(double[] a, double[] b) {
		return new double[] { a[0] + b[0], a[1] + b[1], a[2] + b[2] };
	}

	static double[] sub(double[] a, double[] b) {
		return new double[] { a[0] - b[0], a[1] - b[1], a[2] - b[2] };
	}

	static double[] scale(double[] a, double s) {
		return new double[] { a[0] * s, a[1] * s, a[2] * s };
	}

	static double dot(double[] a, double[] b) {
		return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
	}

	static double len(double[] a) {
		return Math.sqrt(dot(a, a));
	}

	static double[] norm(double[] a) {
		return scale(a, 1 / len(a));
	}
}
