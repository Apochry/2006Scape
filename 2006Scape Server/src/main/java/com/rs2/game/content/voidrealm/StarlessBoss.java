package com.rs2.game.content.voidrealm;

import static com.rs2.game.content.voidrealm.VoidConstants.*;
import static com.rs2.game.content.voidrealm.VoidRealm.*;

import com.rs2.event.CycleEvent;
import com.rs2.event.CycleEventContainer;
import com.rs2.event.CycleEventHandler;
import com.rs2.game.content.quests.impl.TheHollowKing;
import com.rs2.game.npcs.Npc;
import com.rs2.game.npcs.NpcHandler;
import com.rs2.game.players.Player;
import com.rs2.util.Misc;

/**
 * The Starless: the optional mega boss beyond the Starless Gate, after The Hollow King. The thing that crowned Aurel -
 * a black sun at the heart of the Starless Expanse. Repeatable, with a kill count and two uniques.
 *
 * Mechanics: starfall (three marked tiles; step off), the eclipse (every so often one of the four star pillars lights;
 * stand by it or be burned by the dark), and its spawn (at 70% and 35% it splits off three spawn, and heals while they
 * live).
 */
final class StarlessBoss {

	private StarlessBoss() {
	}

	static final int HP = 900, MAX_HIT = 16, ATTACK = 180, DEFENCE = 150;

	private static final String[] LINES = { "I AM THE DARK THE STARS ARE AFRAID OF.", "Little light. You smell of my crown.",
			"I gave a king everything he asked for. What would you ask?", "Every star goes out. I am patient.",
			"Burn, then. Burn bright. It makes no difference." };

	static boolean firstClickObject(Player p, int objectId, int x, int y) {
		if (objectId == STARLESS_GATE) {
			if (inExpanse(p.absX, p.absY)) {
				leave(p);
			} else {
				enter(p);
			}
			return true;
		}
		if (objectId == STAR_PILLAR || objectId == STAR_PILLAR_LIT) {
			p.getPacketSender().sendMessage(objectId == STAR_PILLAR_LIT ? "The pillar blazes with stolen starlight. Stay close to it!"
					: "A pillar of black glass. There's a cold spark trapped deep inside.");
			return true;
		}
		return false;
	}

	private static void enter(Player p) {
		if (p.voidKing < TheHollowKing.COMPLETE) {
			p.getPacketSender().sendMessage("The gate is shut, and something on the other side is holding it.");
			return;
		}
		p.startAnimation(827);
		p.getPacketSender().sendMessage("You step through the Starless Gate. Behind you, the light goes out.");
		later(p, 2, () -> {
			p.getPlayerAssistant().movePlayer(STARLESS_ARRIVAL_X, STARLESS_ARRIVAL_Y, 0);
			later(p, 3, () -> awaken(p));
		});
	}

	private static void leave(Player p) {
		clear(p);
		p.startAnimation(827);
		p.getPacketSender().sendMessage("You step back through the gate, onto the void platform.");
		later(p, 2, () -> {
			p.getPlayerAssistant().movePlayer(ARENA_ARRIVAL_X, ARENA_ARRIVAL_Y, 0);
			later(p, 2, () -> KingQuest.showPlatform(p));
		});
	}

	static void clear(Player p) {
		for (int type : new int[] { THE_STARLESS, STARLESS_SPAWN }) {
			Npc n;
			while ((n = findSpawned(p, type)) != null) {
				banish(n);
			}
		}
		showPillars(p, -1);
	}

	/** Lights one pillar for this player (or none, with -1). */
	private static void showPillars(Player p, int lit) {
		if (!inExpanse(p.absX, p.absY)) {
			return;
		}
		for (int i = 0; i < STAR_PILLARS.length; i++) {
			p.getPacketSender().object(i == lit ? STAR_PILLAR_LIT : STAR_PILLAR, STAR_PILLARS[i][0], STAR_PILLARS[i][1], 0, 10);
		}
	}

	private static void awaken(Player p) {
		if (!inExpanse(p.absX, p.absY) || findSpawned(p, THE_STARLESS) != null) {
			return;
		}
		NpcHandler.spawnNpc(p, THE_STARLESS, STARLESS_X - 1, STARLESS_Y - 1, 0, 0, HP, MAX_HIT, ATTACK, DEFENCE, false, false);
		Npc boss = findSpawned(p, THE_STARLESS);
		if (boss == null) {
			return;
		}
		boss.gfx0(86);
		p.getPlayerAssistant().sendCameraShake(2, 6, 2, 6);
		CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
			int tick, split, eclipse = -1, eclipseAt = 30;
			boolean warned;
			int[][] fall;

			@Override
			public void execute(CycleEventContainer c) {
				tick++;
				if (p.disconnected || p.isDead || !inExpanse(p.absX, p.absY) || boss.isDead || boss.npcType != THE_STARLESS) {
					if (!boss.isDead && boss.npcType == THE_STARLESS) {
						clear(p);
					}
					c.stop();
					return;
				}
				if (tick == 1) {
					say(p, boss, "So. The little light that freed my king.");
					return;
				}
				if (tick == 4) {
					say(p, boss, "He was a good crown. You will be a better one.");
					return;
				}
				if (tick == 6) {
					p.getPacketSender().sendMessage("@red@The Starless opens its eye.");
					boss.underAttack = true;
					boss.killerId = p.getId();
					return;
				}
				if (tick < 6) {
					return;
				}
				// its spawn: at 70% and at 35%, three of them, and it drinks from them while they live
				int[] at = { HP * 70 / 100, HP * 35 / 100 };
				if (split < 2 && boss.HP <= at[split]) {
					split++;
					say(p, boss, "Children. Feed me.");
					p.getPacketSender().sendMessage("@red@Three pieces of the dark tear away. Kill them before it feeds!");
					for (int i = 0; i < 3; i++) {
						int[] o = { -2, 0, 2 };
						NpcHandler.spawnNpc(p, STARLESS_SPAWN, p.absX + o[i], p.absY + (i == 1 ? 2 : 1), 0, 1, 60, 8, 90, 70, true, false);
					}
				}
				int spawn = aliveSpawned(p, STARLESS_SPAWN);
				if (spawn > 0 && tick % 2 == 0 && boss.HP < boss.MaxHP) {
					boss.HP = Math.min(boss.MaxHP, boss.HP + 3 * spawn);
					boss.gfx0(86);
				}
				// starfall: three tiles marked, then struck
				int every = boss.HP <= HP / 4 ? 6 : 9;
				if (fall == null && tick % every == 0) {
					fall = new int[][] { { p.absX, p.absY }, { p.absX + Misc.random(4) - 2, p.absY + Misc.random(4) - 2 },
							{ p.absX + Misc.random(4) - 2, p.absY + Misc.random(4) - 2 } };
					GearQuest.markStars(p, fall);
					if (!warned) {
						warned = true;
						p.getPacketSender().sendMessage("@red@Stars are falling! Where the ground flares, step off it - fast.");
					}
				} else if (fall != null && tick % every == 2) {
					GearQuest.dropStars(p, fall);
				} else if (fall != null && tick % every == 3) {
					boolean hit = false;
					for (int[] t : fall) {
						hit |= p.absX == t[0] && p.absY == t[1];
					}
					if (hit) {
						KingQuest.strike(p, 14 + Misc.random(12));
						p.getPacketSender().sendMessage("A falling star burns through you!");
					}
					fall = null;
				}
				// the eclipse: one pillar lights; be beside it when the dark comes
				if (eclipse < 0 && tick >= eclipseAt) {
					eclipse = Misc.random(STAR_PILLARS.length - 1);
					showPillars(p, eclipse);
					p.getPacketSender().stillGfx(GFX_ECLIPSE, STARLESS_X, STARLESS_Y, 0, 0);
					say(p, boss, "Let there be NO light.");
					p.getPacketSender().sendMessage("@red@The Starless swallows the sky. One pillar still burns - get to it!");
					p.getPlayerAssistant().sendCameraShake(1, 3, 1, 3);
					eclipseAt = tick + 7;
				} else if (eclipse >= 0 && tick >= eclipseAt) {
					int[] pl = STAR_PILLARS[eclipse];
					if (Math.abs(p.absX - pl[0]) <= 2 && Math.abs(p.absY - pl[1]) <= 2) {
						p.getPacketSender().sendMessage("The pillar's light holds the dark off you.");
					} else {
						p.gfx100(GFX_VOID_IMPACT);
						p.getPacketSender().stillGfx(GFX_ECLIPSE, p.absX, p.absY, 0, 0);
						KingQuest.strike(p, 30 + Misc.random(15));
						p.getPacketSender().sendMessage("@red@The dark closes over you, and it is very, very cold.");
					}
					showPillars(p, -1);
					eclipse = -1;
					eclipseAt = tick + 33;
					p.getPlayerAssistant().sendCameraReset();
				}
				if (tick % 19 == 0) {
					say(p, boss, LINES[(tick / 19) % LINES.length]);
				}
			}

			@Override
			public void stop() {
				p.getPlayerAssistant().sendCameraReset();
			}
		}, 1);
	}

	private static void say(Player p, Npc boss, String text) {
		boss.forceChat(text);
		p.getPacketSender().sendMessage("@dre@The Starless: @bla@" + text);
	}

	static void npcKilled(Player killer, int npcType) {
		if (npcType != THE_STARLESS) {
			return;
		}
		killer.starlessKills++;
		Npc n;
		while ((n = findSpawned(killer, STARLESS_SPAWN)) != null) {
			banish(n);
		}
		showPillars(killer, -1);
		killer.getPacketSender().sendMessage("@gre@The Starless collapses into itself, and for a moment there are stars again.");
		killer.getPacketSender().sendMessage("Your Starless kill count is: @red@" + killer.starlessKills + "@bla@.");
		later(killer, 8, () -> {
			if (inExpanse(killer.absX, killer.absY) && findSpawned(killer, THE_STARLESS) == null) {
				killer.getPacketSender().sendMessage("Far off in the dark, something begins, very slowly, to burn again...");
				later(killer, 12, () -> awaken(killer));
			}
		});
	}
}
