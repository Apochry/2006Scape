package com.rs2.tools.mapgen;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * Preview's flat-shaded painter renderer, extended for effects: per-face alpha (client semantics:
 * 0 = opaque, 255 = invisible; colour = src*(256-a)+dst*a >> 8), face priorities as a tie-break,
 * a brightness boost (spotanim opcode 7) and a faint 1-tile ground grid for scale.
 */
public final class FxRender {

	public static void ground(BufferedImage img, int ox, int oy, double scale, double yaw, double pitch, int w, int h, int cx, int cy) {
		Graphics2D g = img.createGraphics();
		g.setColor(new Color(34, 38, 34));
		g.fillRect(cx, cy, w, h);
		g.setClip(cx, cy, w, h);
		g.setColor(new Color(52, 58, 50));
		double cyw = Math.cos(yaw), syw = Math.sin(yaw), cp = Math.cos(pitch), sp = Math.sin(pitch);
		for (int i = -3; i <= 3; i++) {
			for (int pass = 0; pass < 2; pass++) {
				double[] a = pass == 0 ? new double[] { i * 128 - 64, -3 * 128 - 64 } : new double[] { -3 * 128 - 64, i * 128 - 64 };
				double[] b = pass == 0 ? new double[] { i * 128 - 64, 3 * 128 + 64 } : new double[] { 3 * 128 + 64, i * 128 - 64 };
				int[] pa = proj(a[0], 0, a[1], ox, oy, scale, cyw, syw, cp, sp), pb = proj(b[0], 0, b[1], ox, oy, scale, cyw, syw, cp, sp);
				g.drawLine(pa[0], pa[1], pb[0], pb[1]);
			}
		}
		g.dispose();
	}

	static int[] proj(double x0, double y0, double z0, int ox, int oy, double scale, double cy, double sy, double cp, double sp) {
		double x = x0 * cy - z0 * sy, z = x0 * sy + z0 * cy;
		double y2 = y0 * cp - z * sp;
		return new int[] { (int) (ox + x * scale), (int) (oy + y2 * scale) };
	}

	public static void draw(BufferedImage img, ModelData m, int ox, int oy, double scale, double yaw, double pitch, double bright,
			Rectangle clip) {
		double cy = Math.cos(yaw), sy = Math.sin(yaw), cp = Math.cos(pitch), sp = Math.sin(pitch);
		int n = m.vertices.size();
		double[][] P = new double[n][3];
		for (int i = 0; i < n; i++) {
			int[] v = m.vertices.get(i);
			double x = v[0] * cy - v[2] * sy, z = v[0] * sy + v[2] * cy, y = v[1];
			double y2 = y * cp - z * sp, z2 = y * sp + z * cp;
			P[i] = new double[] { ox + x * scale, oy + y2 * scale, z2 };
		}
		Integer[] ord = new Integer[m.faces.size()];
		for (int i = 0; i < ord.length; i++) ord[i] = i;
		Arrays.sort(ord, (a, b) -> {
			int[] fa = m.faces.get(a), fb = m.faces.get(b);
			double za = P[fa[0]][2] + P[fa[1]][2] + P[fa[2]][2], zb = P[fb[0]][2] + P[fb[1]][2] + P[fb[2]][2];
			int c = Double.compare(zb, za);
			return c != 0 ? c : Integer.compare(m.priorities.get(a), m.priorities.get(b));
		});
		double[] L = { -0.5, -0.7, -0.5 };
		double ll = Math.sqrt(1.24);
		for (int i = 0; i < 3; i++) L[i] /= ll;
		Graphics2D g = img.createGraphics();
		if (clip != null) g.setClip(clip);
		for (int fi : ord) {
			int a = m.alphas.get(fi);
			if (a >= 255) continue;
			int[] f = m.faces.get(fi);
			double[] A = P[f[0]], B = P[f[1]], C = P[f[2]];
			double cross = (B[0] - A[0]) * (C[1] - A[1]) - (B[1] - A[1]) * (C[0] - A[0]);
			if (cross > 0) continue;
			int[] va = m.vertices.get(f[0]), vb = m.vertices.get(f[1]), vc = m.vertices.get(f[2]);
			double ux = vb[0] - va[0], uy = vb[1] - va[1], uz = vb[2] - va[2], vx = vc[0] - va[0], vy = vc[1] - va[1], vz = vc[2] - va[2];
			double nx = uy * vz - uz * vy, ny = uz * vx - ux * vz, nz = ux * vy - uy * vx, nl = Math.sqrt(nx * nx + ny * ny + nz * nz) + 1e-9;
			double d = (nx * L[0] + ny * L[1] + nz * L[2]) / nl;
			double shade = (0.75 + 0.45 * d) * bright;
			Color col = new Color(Preview.rgb(m.colors.get(fi), shade));
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (256 - a) / 256f));
			g.setColor(col);
			Polygon p = new Polygon(new int[] { (int) A[0], (int) B[0], (int) C[0] }, new int[] { (int) A[1], (int) B[1], (int) C[1] }, 3);
			g.fill(p);
			if (a == 0) g.draw(p);
		}
		g.dispose();
	}

	public static void label(BufferedImage img, String s, int x, int y, Color c) {
		Graphics2D g = img.createGraphics();
		g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 11));
		g.setColor(Color.BLACK);
		g.drawString(s, x + 1, y + 1);
		g.setColor(c);
		g.drawString(s, x, y);
		g.dispose();
	}

	/** Recolours like SpotAnim.getModel (Model.method476). */
	public static ModelData recolor(ModelData m, int[] from, int[] to) {
		for (int i = 0; i < 6; i++) {
			if (from[i] == 0 && to[i] == 0) continue;
			for (int k = 0; k < m.colors.size(); k++) if (m.colors.get(k) == from[i]) m.colors.set(k, to[i]);
		}
		return m;
	}

	/** Resize like method478 (x and y by resizeXY... the client passes (xy, xy, z) as x, y?, z). */
	public static ModelData resize(ModelData m, int xy, int z) {
		if (xy == 128 && z == 128) return m;
		for (int[] v : m.vertices) { v[0] = v[0] * xy / 128; v[1] = v[1] * z / 128; v[2] = v[2] * xy / 128; }
		return m;
	}
}
