package com.rs2.tools.mapgen;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * A 317-format ("old format") model: vertices, triangles, per-triangle colours and render types, and the vertex /
 * triangle skin labels animations use. Decodes what the client decodes ({@code Model(int)}), and encodes back to the
 * same layout, so we can both read the cache's models and author new ones.
 *
 * Coordinates are in model units (128 per tile); y points down (negative y is up). Colours are 16-bit HSL:
 * hue (6 bits) << 10 | saturation (3 bits) << 7 | lightness (7 bits).
 */
public final class ModelData {

	public final List<int[]> vertices = new ArrayList<>(); // x, y, z
	public final List<Integer> vertexSkins = new ArrayList<>();
	public final List<int[]> faces = new ArrayList<>(); // a, b, c
	public final List<Integer> colors = new ArrayList<>();
	/** 0 = smooth (gouraud) shading, 1 = flat shading. */
	public final List<Integer> renderTypes = new ArrayList<>();
	public final List<Integer> priorities = new ArrayList<>();
	public final List<Integer> alphas = new ArrayList<>();
	public final List<Integer> faceSkins = new ArrayList<>();

	/* ------------------------------------------------------------ decode */

	public static ModelData decode(byte[] m) {
		ByteBuffer h = ByteBuffer.wrap(m, m.length - 18, 18);
		int nv = h.getShort() & 0xFFFF, nf = h.getShort() & 0xFFFF, nt = h.get() & 0xFF;
		int hasTypes = h.get() & 0xFF, priority = h.get() & 0xFF, hasAlpha = h.get() & 0xFF, hasFaceSkins = h.get() & 0xFF,
				hasVertexSkins = h.get() & 0xFF;
		int xLen = h.getShort() & 0xFFFF, yLen = h.getShort() & 0xFFFF, zLen = h.getShort() & 0xFFFF;
		int faceDataLen = h.getShort() & 0xFFFF;
		int o = 0;
		int vFlags = o;
		o += nv;
		int fTypes = o;
		o += nf;
		int fPrio = o;
		if (priority == 255) {
			o += nf;
		}
		int fSkin = o;
		if (hasFaceSkins == 1) {
			o += nf;
		}
		int fRender = o;
		if (hasTypes == 1) {
			o += nf;
		}
		int vSkin = o;
		if (hasVertexSkins == 1) {
			o += nv;
		}
		int fAlpha = o;
		if (hasAlpha == 1) {
			o += nf;
		}
		int fData = o;
		o += faceDataLen;
		int fCol = o;
		o += nf * 2;
		o += nt * 6;
		int vx = o;
		o += xLen;
		int vy = o;
		o += yLen;
		int vz = o;

		ModelData d = new ModelData();
		int[] px = { vx }, py = { vy }, pz = { vz };
		int x = 0, y = 0, z = 0;
		for (int i = 0; i < nv; i++) {
			int f = m[vFlags + i] & 0xFF;
			x += (f & 1) != 0 ? smart(m, px) : 0;
			y += (f & 2) != 0 ? smart(m, py) : 0;
			z += (f & 4) != 0 ? smart(m, pz) : 0;
			d.vertices.add(new int[] { x, y, z });
			d.vertexSkins.add(hasVertexSkins == 1 ? m[vSkin + i] & 0xFF : 0);
		}
		int[] pd = { fData };
		int a = 0, b = 0, c = 0, last = 0;
		for (int i = 0; i < nf; i++) {
			int t = m[fTypes + i] & 0xFF;
			if (t == 1) {
				a = smart(m, pd) + last;
				last = a;
				b = smart(m, pd) + last;
				last = b;
				c = smart(m, pd) + last;
				last = c;
			} else if (t == 2) {
				b = c;
				c = smart(m, pd) + last;
				last = c;
			} else if (t == 3) {
				a = c;
				c = smart(m, pd) + last;
				last = c;
			} else if (t == 4) {
				int tmp = a;
				a = b;
				b = tmp;
				c = smart(m, pd) + last;
				last = c;
			}
			d.faces.add(new int[] { a, b, c });
			d.colors.add((m[fCol + i * 2] & 0xFF) << 8 | m[fCol + i * 2 + 1] & 0xFF);
			d.renderTypes.add(hasTypes == 1 ? m[fRender + i] & 0xFF : 0);
			d.priorities.add(priority == 255 ? m[fPrio + i] & 0xFF : priority);
			d.alphas.add(hasAlpha == 1 ? m[fAlpha + i] & 0xFF : 0);
			d.faceSkins.add(hasFaceSkins == 1 ? m[fSkin + i] & 0xFF : 0);
		}
		return d;
	}

	private static int smart(byte[] m, int[] pos) {
		int v = m[pos[0]] & 0xFF;
		if (v < 128) {
			pos[0]++;
			return v - 64;
		}
		int r = ((m[pos[0]] & 0xFF) << 8 | m[pos[0] + 1] & 0xFF) - 49152;
		pos[0] += 2;
		return r;
	}

	/* ------------------------------------------------------------ encode */

	public byte[] encode() {
		int nv = vertices.size(), nf = faces.size();
		ByteArrayOutputStream vFlags = new ByteArrayOutputStream(), xs = new ByteArrayOutputStream(),
				ys = new ByteArrayOutputStream(), zs = new ByteArrayOutputStream();
		int lx = 0, ly = 0, lz = 0;
		for (int[] v : vertices) {
			int dx = v[0] - lx, dy = v[1] - ly, dz = v[2] - lz;
			int f = (dx != 0 ? 1 : 0) | (dy != 0 ? 2 : 0) | (dz != 0 ? 4 : 0);
			vFlags.write(f);
			if (dx != 0) {
				putSmart(xs, dx);
			}
			if (dy != 0) {
				putSmart(ys, dy);
			}
			if (dz != 0) {
				putSmart(zs, dz);
			}
			lx = v[0];
			ly = v[1];
			lz = v[2];
		}
		ByteArrayOutputStream fTypes = new ByteArrayOutputStream(), fData = new ByteArrayOutputStream();
		int last = 0;
		for (int[] f : faces) { // every face written in full (type 1): simple and always valid
			fTypes.write(1);
			putSmart(fData, f[0] - last);
			putSmart(fData, f[1] - f[0]);
			putSmart(fData, f[2] - f[1]);
			last = f[2];
		}
		boolean anyType = renderTypes.stream().anyMatch(t -> t != 0);
		boolean anyAlpha = alphas.stream().anyMatch(t -> t != 0);
		boolean anyFaceSkin = faceSkins.stream().anyMatch(t -> t != 0);
		boolean anyVertexSkin = vertexSkins.stream().anyMatch(t -> t != 0);
		int prio0 = priorities.isEmpty() ? 0 : priorities.get(0);
		boolean onePriority = priorities.stream().allMatch(p -> p == prio0);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		write(out, vFlags.toByteArray());
		write(out, fTypes.toByteArray());
		if (!onePriority) {
			priorities.forEach(out::write);
		}
		if (anyFaceSkin) {
			faceSkins.forEach(out::write);
		}
		if (anyType) {
			renderTypes.forEach(out::write);
		}
		if (anyVertexSkin) {
			vertexSkins.forEach(out::write);
		}
		if (anyAlpha) {
			alphas.forEach(out::write);
		}
		write(out, fData.toByteArray());
		for (int c : colors) {
			out.write(c >> 8);
			out.write(c);
		}
		write(out, xs.toByteArray());
		write(out, ys.toByteArray());
		write(out, zs.toByteArray());
		ByteBuffer foot = ByteBuffer.allocate(18);
		foot.putShort((short) nv).putShort((short) nf).put((byte) 0);
		foot.put((byte) (anyType ? 1 : 0)).put((byte) (onePriority ? prio0 : 255)).put((byte) (anyAlpha ? 1 : 0))
				.put((byte) (anyFaceSkin ? 1 : 0)).put((byte) (anyVertexSkin ? 1 : 0));
		foot.putShort((short) xs.size()).putShort((short) ys.size()).putShort((short) zs.size()).putShort((short) fData.size());
		write(out, foot.array());
		return out.toByteArray();
	}

	private static void putSmart(ByteArrayOutputStream o, int v) {
		if (v >= -64 && v < 64) {
			o.write(v + 64);
		} else {
			if (v < -16384 || v >= 16384) {
				throw new IllegalArgumentException("model delta out of range: " + v);
			}
			int u = v + 49152;
			o.write(u >> 8);
			o.write(u);
		}
	}

	private static void write(ByteArrayOutputStream o, byte[] b) {
		o.write(b, 0, b.length);
	}

	/* ------------------------------------------------------------ building */

	/** Current skin label for vertices added from now on (bones for animated / wielded models). */
	public int skin;
	/** Shading for faces added from now on: 1 = flat (faceted, the classic look), 0 = smooth. */
	public int shading = 1;

	public static int hsl(int hue, int sat, int light) {
		return (hue & 63) << 10 | (sat & 7) << 7 | light & 127;
	}

	public int vertex(double x, double y, double z) {
		vertices.add(new int[] { (int) Math.round(x), (int) Math.round(y), (int) Math.round(z) });
		vertexSkins.add(skin);
		return vertices.size() - 1;
	}

	public void tri(int a, int b, int c, int color) {
		faces.add(new int[] { a, b, c });
		colors.add(color);
		renderTypes.add(shading);
		priorities.add(0);
		alphas.add(0);
		faceSkins.add(0);
	}

	public void quad(int a, int b, int c, int d, int color) {
		tri(a, b, c, color);
		tri(a, c, d, color);
	}

	/** A triangle wound so its normal points away from the reference point (the client culls back faces). */
	public void triOut(int a, int b, int c, int color, double[] ref) {
		int[] A = vertices.get(a), B = vertices.get(b), C = vertices.get(c);
		double ux = B[0] - A[0], uy = B[1] - A[1], uz = B[2] - A[2], vx = C[0] - A[0], vy = C[1] - A[1], vz = C[2] - A[2];
		double nx = uy * vz - uz * vy, ny = uz * vx - ux * vz, nz = ux * vy - uy * vx;
		double fx = (A[0] + B[0] + C[0]) / 3.0 - ref[0], fy = (A[1] + B[1] + C[1]) / 3.0 - ref[1],
				fz = (A[2] + B[2] + C[2]) / 3.0 - ref[2];
		if (nx * fx + ny * fy + nz * fz < 0) {
			tri(a, c, b, color);
		} else {
			tri(a, b, c, color);
		}
	}

	public void quadOut(int a, int b, int c, int d, int color, double[] ref) {
		triOut(a, b, c, color, ref);
		triOut(a, c, d, color, ref);
	}

	/**
	 * A tapered tube from p0 to p1 (radius r0 to r1, {@code sides} sides), capped at both ends unless a radius is 0
	 * (then it closes to a point). {@code twist} rotates the ring. Faces face outward from the axis.
	 */
	public void tube(double[] p0, double[] p1, double r0, double r1, int sides, double twist, int color, int capColor) {
		double[] d = { p1[0] - p0[0], p1[1] - p0[1], p1[2] - p0[2] };
		double len = Math.sqrt(d[0] * d[0] + d[1] * d[1] + d[2] * d[2]);
		for (int i = 0; i < 3; i++) {
			d[i] /= len;
		}
		double[] up = Math.abs(d[1]) > 0.9 ? new double[] { 1, 0, 0 } : new double[] { 0, 1, 0 };
		double[] u = norm(cross(d, up)), v = cross(d, u);
		int[][] ring = new int[2][sides];
		double[][] ends = { p0, p1 };
		double[] radii = { r0, r1 };
		int[] tip = { -1, -1 };
		for (int e = 0; e < 2; e++) {
			if (radii[e] <= 0) {
				tip[e] = vertex(ends[e][0], ends[e][1], ends[e][2]);
				continue;
			}
			for (int s = 0; s < sides; s++) {
				double a = Math.PI * 2 * s / sides + twist;
				double c = Math.cos(a) * radii[e], sn = Math.sin(a) * radii[e];
				ring[e][s] = vertex(ends[e][0] + u[0] * c + v[0] * sn, ends[e][1] + u[1] * c + v[1] * sn,
						ends[e][2] + u[2] * c + v[2] * sn);
			}
		}
		double[] mid = { (p0[0] + p1[0]) / 2, (p0[1] + p1[1]) / 2, (p0[2] + p1[2]) / 2 };
		for (int s = 0; s < sides; s++) {
			int s2 = (s + 1) % sides;
			if (tip[0] >= 0) {
				triOut(tip[0], ring[1][s], ring[1][s2], color, mid);
			} else if (tip[1] >= 0) {
				triOut(ring[0][s], ring[0][s2], tip[1], color, mid);
			} else {
				quadOut(ring[0][s], ring[0][s2], ring[1][s2], ring[1][s], color, mid);
			}
		}
		for (int e = 0; e < 2; e++) {
			if (tip[e] >= 0 || sides < 3) {
				continue;
			}
			double[] ref = { mid[0], mid[1], mid[2] };
			for (int s = 1; s + 1 < sides; s++) {
				triOut(ring[e][0], ring[e][s], ring[e][s + 1], capColor, ref);
			}
		}
	}

	static double[] cross(double[] a, double[] b) {
		return new double[] { a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0] };
	}

	static double[] norm(double[] a) {
		double l = Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
		return new double[] { a[0] / l, a[1] / l, a[2] / l };
	}

	/** Applies {@code f} to every vertex (e.g. to place a model built in its own frame). */
	public void transform(java.util.function.UnaryOperator<double[]> f) {
		for (int[] v : vertices) {
			double[] r = f.apply(new double[] { v[0], v[1], v[2] });
			v[0] = (int) Math.round(r[0]);
			v[1] = (int) Math.round(r[1]);
			v[2] = (int) Math.round(r[2]);
		}
	}

	/**
	 * A ring-based solid: {@code rings[i] = {y, radiusX, radiusZ}} stacked along y, each ring with {@code sides}
	 * vertices; closed with caps. Good for blades (2-4 sides), posts, trunks, crystals. Colour per band.
	 */
	public void lathe(double cx, double cz, double[][] rings, int sides, double twist, int[] bandColors, boolean capTop,
			boolean capBottom) {
		int[][] idx = new int[rings.length][sides];
		for (int r = 0; r < rings.length; r++) {
			for (int s = 0; s < sides; s++) {
				double a = Math.PI * 2 * s / sides + twist;
				idx[r][s] = vertex(cx + Math.cos(a) * rings[r][1], rings[r][0], cz + Math.sin(a) * rings[r][2]);
			}
		}
		for (int r = 0; r + 1 < rings.length; r++) {
			int col = bandColors[Math.min(r, bandColors.length - 1)];
			for (int s = 0; s < sides; s++) {
				int s2 = (s + 1) % sides;
				quad(idx[r][s], idx[r][s2], idx[r + 1][s2], idx[r + 1][s], col);
			}
		}
		if (capBottom) {
			int c = vertex(cx, rings[0][0], cz);
			for (int s = 0; s < sides; s++) {
				tri(c, idx[0][(s + 1) % sides], idx[0][s], bandColors[0]);
			}
		}
		if (capTop) {
			int last = rings.length - 1;
			int c = vertex(cx, rings[last][0], cz);
			for (int s = 0; s < sides; s++) {
				tri(c, idx[last][s], idx[last][(s + 1) % sides], bandColors[bandColors.length - 1]);
			}
		}
	}

	/** An axis-aligned box from (x0,y0,z0) to (x1,y1,z1). */
	public void box(double x0, double y0, double z0, double x1, double y1, double z1, int color) {
		int a = vertex(x0, y0, z0), b = vertex(x1, y0, z0), c = vertex(x1, y0, z1), d = vertex(x0, y0, z1);
		int e = vertex(x0, y1, z0), f = vertex(x1, y1, z0), g = vertex(x1, y1, z1), h = vertex(x0, y1, z1);
		quad(a, b, c, d, color); // y0 face
		quad(h, g, f, e, color); // y1 face
		quad(a, e, f, b, color);
		quad(b, f, g, c, color);
		quad(c, g, h, d, color);
		quad(d, h, e, a, color);
	}

	/** Bounding box: minX, minY, minZ, maxX, maxY, maxZ. */
	public int[] bounds() {
		int[] b = { Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE,
				Integer.MIN_VALUE };
		for (int[] v : vertices) {
			for (int i = 0; i < 3; i++) {
				b[i] = Math.min(b[i], v[i]);
				b[i + 3] = Math.max(b[i + 3], v[i]);
			}
		}
		return b;
	}
}
