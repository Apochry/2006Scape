package com.rs2.tools.mapgen;

import java.io.File;
import java.util.List;

/**
 * An offline look at the {@link Gravecoil} through {@link CreaturePreview}: every frame of every sequence posed from
 * the encoded frame file exactly as the client poses it, and measured against the ground.
 *
 * <p>Usage (from '2006Scape Server'): {@code GravecoilPreview outDir}. Writes {@code idle.gif}, {@code walk.gif},
 * {@code attack.gif}, {@code death.gif}, {@code block.gif}, {@code showcase.gif} (all of them in turn, captioned),
 * {@code gravecoil-card.png}, {@code sheet-<anim>.png} and every frame as {@code frames/<anim>-NN.png}; and prints the
 * ground checks per frame and per sequence. It exits non-zero if any frame sinks into the ground, leaves a part that
 * should be lying on it floating, or lets the body stray from its track.
 */
public final class GravecoilPreview {

	private GravecoilPreview() {
	}

	/**
	 * How far (units) a resting part may be off the ground before the check fails: one unit, the rounding of a vertex.
	 * The walk's track allows a little more: an angle step is 1.4 degrees, about a unit across a body segment.
	 */
	static final double GROUND_TOLERANCE = 1, TRACK_TOLERANCE = 2.5;

	static CreaturePreview.Spec spec() {
		CreaturePreview.Spec s = new CreaturePreview.Spec();
		s.name = Gravecoil.NAME;
		s.file = "gravecoil";
		s.examine = Gravecoil.EXAMINE;
		s.combat = Gravecoil.COMBAT;
		s.where = "Hunts the graves round the Sunken Barrow, Nythera";
		s.stats = new String[][] { { "Hitpoints", "" + Gravecoil.HITPOINTS }, { "Attack", "" + Gravecoil.ATTACK }, { "Strength", "" + Gravecoil.STRENGTH },
				{ "Defence", "" + Gravecoil.DEFENCE }, { "Max hit", Gravecoil.MAX_HIT + " (stab: a strike)" }, { "Attack speed", "4 ticks" },
				{ "Size", "2x2" }, { "Weak to", "ranged (def 12): arrows find the hide" },
				{ "Resists", "magic (def 80): its rings are warded" } };
		s.lore = Gravecoil.LORE;
		s.ids = "npc " + Gravecoil.NPC_ID + "   model " + Gravecoil.MODEL_ID + "   seqs " + Gravecoil.SEQ_STAND + "-" + Gravecoil.SEQ_BLOCK + "   frames "
				+ Gravecoil.FRAME_BASE + "+ (file " + Gravecoil.FRAME_FILE + ")";
		s.base = Gravecoil.model();
		s.rig = Gravecoil.RIG;
		s.seqs = Gravecoil.sequences();
		s.captions.put("idle", "Idle");
		s.captions.put("walk", "Walk (slither)");
		s.captions.put("attack", "Attack");
		s.captions.put("block", "Block");
		s.captions.put("death", "Death");
		s.reps.put("idle", 2);
		s.reps.put("walk", 2);
		s.reps.put("attack", 2);
		s.reps.put("block", 2);
		s.reps.put("death", 1);
		s.speed.put("walk", Gravecoil.WALK_SPEED);
		s.contact = Gravecoil::contact;
		s.extra = Gravecoil::trackError;
		s.extraName = "off-track";
		s.deathHold = 0;
		return s;
	}

	public static void main(String[] args) throws Exception {
		CreaturePreview.Spec s = spec();
		// (showcase order: idle, walk, attack, block, then the death)
		java.util.Map<String, List<FxAnim.Key>> ordered = new java.util.LinkedHashMap<>();
		for (String n : new String[] { "idle", "walk", "attack", "block", "death" }) {
			ordered.put(n, s.seqs.get(n));
		}
		s.seqs = ordered;
		List<CreaturePreview.Ground> checks = CreaturePreview.run(s, new File(args[0]));
		boolean ok = true;
		for (CreaturePreview.Ground g : checks) {
			if (g.worstSink > 0 || g.worstFloat > GROUND_TOLERANCE || g.worstExtra > TRACK_TOLERANCE) {
				System.out.println("GROUND CHECK FAILED: " + g.seq);
				ok = false;
			}
		}
		System.out.println(ok ? "GROUND CHECK: PASSED" : "GROUND CHECK: FAILED");
		if (!ok) {
			System.exit(1);
		}
	}
}
