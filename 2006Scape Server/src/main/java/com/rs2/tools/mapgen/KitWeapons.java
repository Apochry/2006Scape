package com.rs2.tools.mapgen;

import static com.rs2.tools.mapgen.LadderWeapons.*;
import static com.rs2.tools.mapgen.ModelData.hsl;
import static com.rs2.tools.mapgen.QuestWeapons.*;

import com.rs2.game.content.voidrealm.VoidConstants;

/**
 * The kit-wearers' weapons: each of the five enemies that wear a middle armour set (MidArmour) carries the matching
 * weapon, drawn on the same frames and poses as {@link LadderWeapons}. They drop it, now and then, with the armour.
 */
public final class KitWeapons {

	private KitWeapons() {
	}

	/** The Silkwood stalkers' bow: pale silkwood bound in spider silk, fangs for nocks, two red eyes at the grip. */
	static ModelData silkstringBow(double bend) {
		ModelData m = new ModelData();
		int wood = hsl(8, 1, 66), woodDark = hsl(8, 1, 48), silk = hsl(10, 0, 110), fang = hsl(9, 1, 104), eye = hsl(0, 6, 40);
		bow(m, 127, bend, new double[] { 1.4, 2.0, 2.8, 3.3, 3.6, 3.3, 2.8, 2.0, 1.4 }, wood, woodDark, silk, fang, silk);
		for (double s : new double[] { 22, 30, 97, 105 }) { // silk wound round the limbs
			double w = (s < 64 ? 7 + (s - 24) * 0.12 : 7 + (104 - s) * 0.12) * bend;
			tube(m, f(s - 1.2, w, 0), f(s + 1.2, w, 0), 3.4, 3.4, 4, Math.PI / 4, silk, silk);
		}
		for (int k = -1; k <= 1; k += 2) {
			gem(m, 63.5 + k * 3, 6 * bend + 2.4, 1.0, 1.6, eye, eye);
		}
		return m;
	}

	/** The Scar reavers' axe: a ravager's jawbone for a bit, its teeth still in, on a black haft; violet in the socket. */
	static ModelData ravagerAxe() {
		ModelData m = new ModelData();
		int bone = hsl(8, 1, 96), boneDark = hsl(8, 1, 74), black = hsl(0, 0, 16), tooth = hsl(9, 1, 112), glow = ArmourSets.violet(84, 6);
		haft(m, 2, 64, 2.6, 2.3, black);
		knob(m, 2, 3.4, 2.6, bone, boneDark);
		band(m, 4, 16, 3.0, hsl(0, 0, 26));
		collar(m, 34, 62, 4.2, 3.6, black);
		// the jaw: one great crescent on +w, a short hook on -w
		double[][] big = { { 38, 3 }, { 30, 11 }, { 24, 23 }, { 36, 27 }, { 50, 28 }, { 64, 27 }, { 74, 22 }, { 64, 11 }, { 58, 3 } };
		plateSW(m, big, new double[] { 2.4, 1.6, 0, 0, 0, 0, 0, 1.6, 2.4 }, new double[] { 48, 14 }, 2.6, bone, boneDark, boneDark);
		for (int k = 0; k < 6; k++) { // teeth along the edge
			double t = (k + 0.5) / 6, s = 27 + t * 44, w = 24.5 + Math.sin(Math.PI * t) * 3.5;
			tube(m, f(s, w, 0), f(s + (t - 0.5) * 3, w + 5.5, 0), 1.6, 0, 4, Math.PI / 4, tooth, tooth);
		}
		double[][] hook = { { 42, -3 }, { 40, -9 }, { 34, -16 }, { 46, -15 }, { 56, -9 }, { 54, -3 } };
		plateSW(m, hook, new double[] { 2.2, 1.4, 0, 0, 1.4, 2.2 }, new double[] { 47, -8 }, 2.4, bone, boneDark, boneDark);
		tube(m, f(61, 0, 0), f(72, 0, 0), 3.6, 0, 4, Math.PI / 4, black, black);
		gem(m, 48, 0, 2.4, 5.0, glow, glow);
		return m;
	}

	/** The Moonmourners' staff: midnight wood, and a silver crescent holding a pale moon at the top. */
	static ModelData moonmournStaff() {
		ModelData m = new ModelData();
		int wood = hsl(42, 2, 22), woodDark = hsl(42, 2, 14), silver = hsl(40, 0, 98), silverDark = hsl(40, 0, 74), moon = hsl(38, 2, 108),
				moonCore = hsl(40, 0, 124);
		tube(m, f(4, 0, 0), f(110, 0.6, 0), 3.4, 3.6, 5, 0, wood, wood);
		tube(m, f(110, 0.6, 0), f(200, 0, 0), 3.6, 3.3, 5, 0.3, woodDark, woodDark);
		collar(m, 0, 8, 2.6, 4.0, silverDark);
		band(m, 110, 128, 4.0, hsl(42, 2, 10));
		for (double s : new double[] { 160, 176, 192 }) {
			collar(m, s, s + 1.5, 3.9, 3.9, silver);
		}
		// the crescent: an arc of tube round the moon, open at the top, thickest where it sits on the staff
		double cs = 216, r = 15;
		int n = 9;
		for (int i = 0; i < n; i++) {
			double a0 = Math.PI * (0.2 + 1.6 * i / n), a1 = Math.PI * (0.2 + 1.6 * (i + 1) / n);
			double t0 = 1 - Math.abs(i - n / 2.0) / (n / 2.0), t1 = 1 - Math.abs(i + 1 - n / 2.0) / (n / 2.0);
			tube(m, f(cs + Math.cos(a0) * r, Math.sin(a0) * r, 0), f(cs + Math.cos(a1) * r, Math.sin(a1) * r, 0), 0.8 + t0 * 2.0, 0.8 + t1 * 2.0, 4,
					Math.PI / 4, i % 2 == 0 ? silver : silverDark, silver);
		}
		collar(m, 198, 204, 3.6, 2.4, silver);
		// the moon, held in the crescent
		tube(m, f(208, 0, 0), f(216, 0, 0), 1.0, 7.5, 6, 0, moon, moon);
		tube(m, f(216, 0, 0), f(224, 0, 0), 7.5, 1.0, 6, 0, moonCore, moon);
		return m;
	}

	/** The Rimebound warriors' hammer: a block of Frostfang ice on a frost-blackened haft, icicles hanging off it. */
	static ModelData rimeboundHammer() {
		ModelData m = new ModelData();
		int haftCol = hsl(36, 1, 20), wrap = hsl(36, 3, 46), ice = hsl(36, 2, 94), iceDark = hsl(36, 3, 72), rime = hsl(36, 1, 118),
				deep = hsl(38, 5, 62);
		haft(m, 2, 60, 2.4, 2.1, haftCol);
		knob(m, 2, 3.0, 2.4, iceDark, iceDark);
		band(m, 4, 16, 2.8, wrap);
		band(m, 40, 43, 2.9, ice);
		// the head: an ice block across w, clear at the faces, deep blue at its heart
		tube(m, f(52, -12, 0), f(52, 12, 0), 7.6, 7.6, 4, Math.PI / 4, ice, iceDark);
		tube(m, f(52, 12, 0), f(52, 15, 0), 7.0, 4.0, 4, Math.PI / 4, rime, iceDark);
		tube(m, f(52, -12, 0), f(52, -15, 0), 7.0, 4.0, 4, Math.PI / 4, rime, iceDark);
		gem(m, 52, 0, 3.2, 8.2, deep, deep);
		// icicles under the head, a rime spike on top
		for (double w : new double[] { -9, -3, 4, 10 }) {
			tube(m, f(46, w, 0), f(46 - 6 - Math.abs(w) * 0.3, w, 0), 1.6, 0, 4, 0, ice, ice);
		}
		tube(m, f(58, 0, 0), f(70, 0, 0), 4.2, 0, 4, Math.PI / 4, rime, ice);
		return m;
	}

	/** The nightrunners' bow: a charcoal recurve with sea-teal grips and lantern-amber tips, for shooting by signal-light. */
	static ModelData nightrunnerBow(double bend) {
		ModelData m = new ModelData();
		int wood = hsl(0, 0, 22), woodDark = hsl(0, 0, 13), teal = hsl(30, 3, 34), amber = hsl(8, 6, 84), string = hsl(0, 0, 58);
		bow(m, 127, bend, new double[] { 1.5, 2.1, 2.9, 3.4, 3.8, 3.4, 2.9, 2.1, 1.5 }, wood, woodDark, teal, amber, string);
		for (double s : new double[] { 40, 87 }) {
			double w = 9.5 * bend - 0.2;
			tube(m, f(s - 1.6, w, 0), f(s + 1.6, w, 0), 3.9, 3.9, 4, Math.PI / 4, teal, teal);
		}
		gem(m, 63.5, 6 * bend + 2.8, 1.3, 2.0, amber, amber);
		return m;
	}

	/** The kit-wearers, in KIT order: name, examine, combat level. */
	static final String[] WEARER_NAMES = { "Silkwood stalker", "Scar reaver", "Moonmourner", "Rimebound warrior", "Cove nightrunner" };
	static final String[] WEARER_EXAMINES = { "A hunter of Silkwood, wrapped in spider silk. You didn't hear the bow until it was drawn.",
			"A raider from the Hollow Scar in a ravager's bones, axe and all.",
			"One of the mourners who keep watch over the barrow. They don't like visitors.",
			"A warrior of the Frostfang deeps, armoured in ice that never melts.",
			"One of the Cove's night crew. Whatever they're smuggling, they'd rather you didn't see it." };
	static final int[] WEARER_COMBAT = { 28, 45, 48, 55, 54 };

	/**
	 * Kit-wearer {@code i} as one model on the human rig: the default male body wearing every piece of its set (the
	 * same worn models the items use, hiding what they cover) with its weapon in hand. Vertex labels are kept, so the
	 * stock human animations move it.
	 */
	static ModelData wearer(ModelForge forge, ArmourSets as, int i) throws Exception {
		ArmourSets.Style st = null;
		for (ArmourSets.Style s : ArmourSets.SETS) {
			if (s.ids == VoidConstants.KIT_SETS[i]) {
				st = s;
			}
		}
		ModelData[][] worn = new ModelData[6][];
		for (int s = 0; s < 6; s++) {
			if (ArmourSets.has(st, s)) {
				worn[s] = as.worn(st, s, false);
			}
		}
		ModelData m = ArmourPreview.dressed(forge, st, worn, false);
		ArmourPreview.append(m, KIT[i].weapon.wieldedModel(), -1);
		return m;
	}

	static final Rung[] KIT = {
			new Rung(VoidConstants.SILKSTRING_SHORTBOW, "Silkstring shortbow", "shortbow", 900,
					"Silkwood strung with spider silk. It never makes a sound, and neither do the stalkers who carry it.",
					() -> silkstringBow(1.0), () -> silkstringBow(2.4), SHORTBOW),
			new Rung(VoidConstants.RAVAGER_BATTLEAXE, "Ravager battleaxe", "battleaxe", 9000,
					"A ravager's jaw, teeth and all, on a black haft. The Scar reavers swear it still bites on its own.", KitWeapons::ravagerAxe,
					BATTLEAXE),
			new Rung(VoidConstants.MOONMOURN_STAFF, "Moonmourn staff", "staff", 12000,
					"A silver crescent holding a pale moon. The mourners light it over the barrow every night.", KitWeapons::moonmournStaff, STAFF),
			new Rung(VoidConstants.RIMEBOUND_WARHAMMER, "Rimebound warhammer", "warhammer", 18000,
					"A block of Frostfang ice that has never melted. It hits like the mountain.", KitWeapons::rimeboundHammer, WARHAMMER),
			new Rung(VoidConstants.NIGHTRUNNER_SHORTBOW, "Nightrunner shortbow", "shortbow", 22000,
					"Charcoal and sea-teal, its tips lit like a signal lantern. The Cove's night crews shoot by its light.",
					() -> nightrunnerBow(1.0), () -> nightrunnerBow(2.4), SHORTBOW), };
}
