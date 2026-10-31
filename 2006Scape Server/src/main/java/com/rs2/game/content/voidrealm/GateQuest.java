package com.rs2.game.content.voidrealm;

import static com.rs2.game.content.quests.impl.HoldTheGate.*;
import static com.rs2.game.content.voidrealm.VoidConstants.*;
import static com.rs2.game.content.voidrealm.VoidRealm.*;

import com.rs2.event.CycleEvent;
import com.rs2.event.CycleEventContainer;
import com.rs2.event.CycleEventHandler;
import com.rs2.game.content.quests.QuestAssistant;
import com.rs2.game.content.quests.impl.HoldTheGate;
import com.rs2.game.dialogues.ChatEmotes;
import com.rs2.game.npcs.Npc;
import com.rs2.game.npcs.NpcHandler;
import com.rs2.game.players.Player;

/**
 * Server-side content for "Hold the Gate": Ysolde's welcome, the hunt with Kael, Dorrin's spikes, the three barricades
 * on the Scar road, and the night raid. Dialogue ids 20000-20199.
 */
final class GateQuest {

	private GateQuest() {
	}

	private static final int D_YSOLDE = 20000, D_YSOLDE_GO = 20007, D_YSOLDE_WAIT = 20008, D_YSOLDE_RAID = 20010,
			D_YSOLDE_BELL = 20013, D_YSOLDE_WON = 20015, D_KAEL = 20040, D_KAEL_HUNT = 20047, D_KAEL_DONE = 20048,
			D_DORRIN = 20060, D_DORRIN_ORE = 20064, D_DORRIN_SPARE = 20068;
	private static final int A_START = 20000, A_RAID = 20001;
	private static final int OPTION_2 = 9157;

	static boolean firstClickNpc(Player p, int npcType) {
		int stage = p.voidGate;
		switch (npcType) {
		case YSOLDE:
			if (stage == NOT_STARTED && HoldTheGate.canStart(p)) {
				dialogue(p, D_YSOLDE, npcType);
			} else if (stage >= SEE_KAEL && stage < RAID) {
				dialogue(p, D_YSOLDE_WAIT, npcType);
			} else if (stage == RAID) {
				dialogue(p, raidRunning(p) ? D_YSOLDE_BELL + 1 : D_YSOLDE_RAID, npcType);
			} else if (stage == RAID_WON) {
				dialogue(p, D_YSOLDE_WON, npcType);
			} else {
				return false;
			}
			return true;
		case KAEL:
			if (stage == SEE_KAEL) {
				dialogue(p, D_KAEL, npcType);
			} else if (stage == HUNT) {
				dialogue(p, p.gateKills >= HUNT_KILLS ? D_KAEL_DONE : D_KAEL_HUNT, npcType);
			} else {
				return false;
			}
			return true;
		case DORRIN:
			if (stage == SPIKES) {
				dialogue(p, p.getItemAssistant().playerHasItem(IRON_ORE, IRON_NEEDED) ? D_DORRIN_ORE : D_DORRIN, npcType);
			} else if (stage == BARRICADES && !p.getItemAssistant().playerHasItem(GATE_SPIKES)) {
				dialogue(p, D_DORRIN_SPARE, npcType);
			} else {
				return false;
			}
			return true;
		}
		return false;
	}

	static boolean sendDialogue(Player p, int id, int npcId) {
		if (id < 20000 || id >= 20200) {
			return false;
		}
		p.talkingNpc = npcId;
		p.nextChat = 0;
		switch (id) {
		/* ---------------- Captain Ysolde: a stranger who fell out of the sky ---------------- */
		case D_YSOLDE:
			npc(p, id, YSOLDE, ChatEmotes.CALM_TALK1, false, "So you're the one who fell out of the sky. Oren sent word.",
					"I thought you'd be taller.");
			break;
		case D_YSOLDE + 1:
			me(p, id, false, "I get that a lot. What happened to this place?");
			break;
		case D_YSOLDE + 2:
			npc(p, id, YSOLDE, ChatEmotes.CALM_TALK2, false, "The same thing that happened to the sky you fell through.",
					"Ever since the rift tore open, the Hollowed have been coming out of the Scar at night. More every week.");
			break;
		case D_YSOLDE + 3:
			npc(p, id, YSOLDE, ChatEmotes.CALM_TALK1, false, "Last night they smashed the barricades on the Scar road.",
					"Tonight they'll come for the gate itself, and I've thirty souls and nine spears.");
			break;
		case D_YSOLDE + 4:
			npc(p, id, YSOLDE, ChatEmotes.CALM_TALK2, false, "You want Gatehold's help getting home? Then stand a watch with",
					"us first. Earn it.");
			break;
		case D_YSOLDE + 5:
			p.getDialogueHandler().sendOption("I'll stand your watch.", "Find someone else.");
			p.dialogueAction = A_START;
			break;
		case D_YSOLDE_GO:
			npc(p, id, YSOLDE, ChatEmotes.HAPPY_JOYFUL, true, "Good. Find Warden Kael at the Scar Watch, east along the",
					"road. She'll show you what we're up against.");
			break;
		case D_YSOLDE_WAIT:
			npc(p, id, YSOLDE, ChatEmotes.CALM_TALK1, true, waitLine(p));
			break;
		case D_YSOLDE_RAID:
			npc(p, id, YSOLDE, ChatEmotes.CALM_TALK1, false, "Barricades are up. Dorrin's spikes will bleed anything that",
					"climbs them. Now we wait for dark - or we don't.");
			break;
		case D_YSOLDE_RAID + 1:
			npc(p, id, YSOLDE, ChatEmotes.CALM_TALK2, false, "I can ring the bell and draw them in now, while you're",
					"rested. Stand at the barricades and don't leave them. Ready?");
			break;
		case D_YSOLDE_RAID + 2:
			p.getDialogueHandler().sendOption("Ring the bell.", "Not yet.");
			p.dialogueAction = A_RAID;
			break;
		case D_YSOLDE_BELL:
			npc(p, id, YSOLDE, ChatEmotes.ANGRY1, true, "To the barricades! Here they come!");
			break;
		case D_YSOLDE_BELL + 1:
			npc(p, id, YSOLDE, ChatEmotes.ANGRY1, true, "What are you doing here? Get back to the barricades!");
			break;
		case D_YSOLDE_WON:
			npc(p, id, YSOLDE, ChatEmotes.HAPPY_JOYFUL, false, "Ha! You held. Thirty years I've stood on these walls and",
					"I've never seen the Hollowed run before.");
			break;
		case D_YSOLDE_WON + 1:
			npc(p, id, KAEL, ChatEmotes.CALM_TALK2, false, "They weren't running from us, Captain.");
			break;
		case D_YSOLDE_WON + 2:
			me(p, id, false, "Kael? What did you see?");
			break;
		case D_YSOLDE_WON + 3:
			npc(p, id, KAEL, ChatEmotes.CALM_TALK1, false, "Up on the ridge, at the edge of the Scar, while the raid broke.",
					"Something tall. It wore a crown. It just stood there, and watched you fight, and then it called them home.");
			break;
		case D_YSOLDE_WON + 4:
			npc(p, id, YSOLDE, ChatEmotes.CALM_TALK2, false, "...A crown. Nythera hasn't had a king in three hundred years.",
					"Rest. Then come and see me. I'll want to talk about this.");
			break;
		case D_YSOLDE_WON + 5:
			npc(p, id, YSOLDE, ChatEmotes.HAPPY_JOYFUL, true, "And take this. Kael's old shield. Gatehold looks after its own.");
			HoldTheGate.complete(p);
			QuestAssistant.sendStages(p);
			break;

		/* ---------------- Warden Kael: the hunt ---------------- */
		case D_KAEL:
			npc(p, id, KAEL, ChatEmotes.CALM_TALK1, false, "Ysolde sent you? Then she's desperate, or she likes you.",
					"Either way, I'll take the help.");
			break;
		case D_KAEL + 1:
			me(p, id, false, "What do you need?");
			break;
		case D_KAEL + 2:
			npc(p, id, KAEL, ChatEmotes.CALM_TALK2, false, "The raids are getting bigger because the packs are getting",
					"bigger. Thin them out before nightfall and fewer reach the walls.");
			break;
		case D_KAEL + 3:
			npc(p, id, KAEL, ChatEmotes.CALM_TALK1, false, "Put down " + HUNT_KILLS + " Hollowed - wolves or husks, out on the",
					"black ground east of here. Watch them as they die. Tell me if they try to go anywhere.");
			break;
		case D_KAEL + 4:
			me(p, id, false, "Go anywhere?");
			break;
		case D_KAEL + 5:
			npc(p, id, KAEL, ChatEmotes.CALM_TALK2, true, "They always crawl towards the Scar when they're dying. Like",
					"something's calling them. Go on.");
			if (p.voidGate == SEE_KAEL) {
				p.voidGate = HUNT;
				p.gateKills = 0;
				QuestAssistant.sendStages(p);
			}
			break;
		case D_KAEL_HUNT:
			npc(p, id, KAEL, ChatEmotes.CALM_TALK1, true, (HUNT_KILLS - p.gateKills) + " more. They're thick on the black ground",
					"east of the Watch.");
			break;
		case D_KAEL_DONE:
			npc(p, id, KAEL, ChatEmotes.HAPPY_JOYFUL, false, "That's " + HUNT_KILLS + ". Did they crawl towards the Scar?");
			break;
		case D_KAEL_DONE + 1:
			me(p, id, false, "Every one of them.");
			break;
		case D_KAEL_DONE + 2:
			npc(p, id, KAEL, ChatEmotes.CALM_TALK2, true, "...Then something in there is calling them. Right. Next problem:",
					"the barricades on the Scar road. Dorrin, the smith, can make spikes for them - if someone brings him iron.");
			if (p.voidGate == HUNT) {
				p.voidGate = SPIKES;
				QuestAssistant.sendStages(p);
			}
			break;

		/* ---------------- Dorrin: the spikes ---------------- */
		case D_DORRIN:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK1, false, "Spikes for the barricades? Aye, Kael said. I can do it, but my",
					"iron's all gone into spearheads.");
			break;
		case D_DORRIN + 1:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK2, true, "Bring me " + IRON_NEEDED + " iron ore. Tess's quarry, east along the",
					"foothills - iron's on the east face. You'll want a pickaxe, mind.");
			break;
		case D_DORRIN_ORE:
			npc(p, id, DORRIN, ChatEmotes.HAPPY_JOYFUL, false, "Now that's good ore. Give it here.");
			break;
		case D_DORRIN_ORE + 1:
			if (!p.getItemAssistant().playerHasItem(IRON_ORE, IRON_NEEDED)) {
				dialogue(p, D_DORRIN, DORRIN);
				return true;
			}
			p.getItemAssistant().deleteItem(IRON_ORE, IRON_NEEDED);
			p.getItemAssistant().addOrDropItem(GATE_SPIKES, 3);
			p.startAnimation(898);
			p.voidGate = BARRICADES;
			QuestAssistant.sendStages(p);
			p.getDialogueHandler().sendStatement(wide("Dorrin hammers the ore into three bundles of wicked iron spikes."));
			p.nextChat = id + 1;
			break;
		case D_DORRIN_ORE + 2:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK1, true, "Three barricades on the road east of the Gate, just past the",
					"hatch. Hammer these into each one and the Hollowed will bleed for every step.");
			break;
		case D_DORRIN_SPARE:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK2, true, "Lost them? Lucky for you I made a spare set.");
			int need = 3 - Integer.bitCount(p.gateFlags & 7);
			if (need > 0) {
				p.getItemAssistant().addOrDropItem(GATE_SPIKES, need);
			}
			break;
		default:
			return false;
		}
		return true;
	}

	private static String waitLine(Player p) {
		switch (p.voidGate) {
		case SEE_KAEL:
			return "Warden Kael's at the Scar Watch, east along the road. Go.";
		case HUNT:
			return "Kael's got you hunting? Good. Every one you put down out there is one less at my gate.";
		case SPIKES:
			return "Dorrin needs iron for those spikes. Tess's quarry will have it.";
		default:
			return "The barricades are on the road just east of the Gate. All three, mind.";
		}
	}

	static boolean handleOption(Player p, int button) {
		if (p.dialogueAction != A_START && p.dialogueAction != A_RAID) {
			return false;
		}
		if (button != OPTION_2 && button != OPTION_2 + 1) {
			return false;
		}
		int action = p.dialogueAction;
		p.dialogueAction = -1;
		if (button != OPTION_2) {
			p.getPacketSender().closeAllWindows();
			return true;
		}
		if (action == A_START) {
			if (p.voidGate == NOT_STARTED) {
				p.voidGate = SEE_KAEL;
				QuestAssistant.sendStages(p);
			}
			dialogue(p, D_YSOLDE_GO, YSOLDE);
		} else {
			dialogue(p, D_YSOLDE_BELL, YSOLDE);
			startRaid(p);
		}
		return true;
	}

	/* ====================================================================== */
	/*                              THE BARRICADES                            */
	/* ====================================================================== */

	static boolean firstClickObject(Player p, int objectId, int x, int y) {
		if (objectId != BROKEN_BARRICADE && objectId != SPIKED_BARRICADE) {
			return false;
		}
		int which = barricadeAt(x, y);
		if (which < 0) {
			return false;
		}
		if (objectId == SPIKED_BARRICADE || (p.gateFlags & 1 << which) != 0) {
			p.getPacketSender().sendMessage("The barricade bristles with iron. Nothing's climbing that without bleeding.");
			return true;
		}
		if (p.voidGate < BARRICADES) {
			p.getPacketSender().sendMessage("A heap of smashed timber. It'll need more than wood to stop the Hollowed.");
			return true;
		}
		if (!p.getItemAssistant().playerHasItem(GATE_SPIKES)) {
			p.getPacketSender().sendMessage("You need a bundle of Dorrin's spikes to repair it.");
			return true;
		}
		p.startAnimation(898);
		p.getItemAssistant().deleteItem(GATE_SPIKES, 1);
		p.gateFlags |= 1 << which;
		showBarricades(p);
		int done = Integer.bitCount(p.gateFlags & 7);
		p.getPacketSender().sendMessage("You stack the timber and hammer the spikes in. (" + done + " of 3.)");
		if (done == 3 && p.voidGate == BARRICADES) {
			p.voidGate = RAID;
			QuestAssistant.sendStages(p);
			p.getPacketSender().sendMessage("The road is barred. Captain Ysolde will want to know.");
		}
		return true;
	}

	private static int barricadeAt(int x, int y) {
		for (int i = 0; i < BARRICADE_SPOTS.length; i++) {
			if (Math.abs(BARRICADE_SPOTS[i][0] - x) <= 1 && Math.abs(BARRICADE_SPOTS[i][1] - y) <= 1) {
				return i;
			}
		}
		return -1;
	}

	/** Shows this player the barricades they've repaired. */
	static void showBarricades(Player p) {
		if (Math.abs(p.absX - BARRICADE_SPOTS[0][0]) > 48 || Math.abs(p.absY - BARRICADE_SPOTS[0][1]) > 48) {
			return;
		}
		for (int i = 0; i < BARRICADE_SPOTS.length; i++) {
			if ((p.gateFlags & 1 << i) != 0 || p.voidGate > RAID) {
				p.getPacketSender().object(SPIKED_BARRICADE, BARRICADE_SPOTS[i][0], BARRICADE_SPOTS[i][1], 1, 10);
			}
		}
	}

	/* ====================================================================== */
	/*                                 THE RAID                               */
	/* ====================================================================== */

	private static final java.util.Set<Integer> RAIDING = new java.util.HashSet<>();

	private static boolean raidRunning(Player p) {
		return RAIDING.contains(p.getId());
	}

	private static final int[][] WAVE_SPOTS = { { 0, 0 }, { 2, 1 }, { -2, 1 }, { 1, 3 }, { -1, 3 } };

	private static void spawnRaider(Player p, int type, int slot, int hp, int maxHit, int att, int def) {
		int x = RAID_FROM_X + WAVE_SPOTS[slot][0], y = RAID_FROM_Y + WAVE_SPOTS[slot][1];
		NpcHandler.spawnNpc(p, type, x, y, 0, 1, hp, maxHit, att, def, true, false);
	}

	private static void startRaid(Player p) {
		if (raidRunning(p) || p.voidGate != RAID) {
			return;
		}
		RAIDING.add(p.getId());
		p.getPacketSender().sendMessage("@red@The alarm bell clangs across Gatehold. Something on the Scar road howls back.");
		p.getPlayerAssistant().sendCameraShake(1, 2, 1, 2);
		CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
			int tick, wave, waveTick = 6;
			final java.util.Set<Npc> bled = new java.util.HashSet<>();

			@Override
			public void execute(CycleEventContainer c) {
				tick++;
				boolean strayed = Math.abs(p.absX - RAID_HOLD_X) > 24 || Math.abs(p.absY - RAID_HOLD_Y) > 24;
				if (p.disconnected || p.isDead || strayed) {
					clearRaiders(p);
					if (!p.disconnected) {
						p.getPacketSender().sendMessage("@red@You left the barricades! The Hollowed pour past and melt back into the dark.");
						p.getPacketSender().sendMessage("Speak to Captain Ysolde to try again.");
					}
					c.stop();
					return;
				}
				// raiders that reach the spikes tear themselves open on them
				for (Npc n : NpcHandler.npcs) {
					if (n != null && !n.isDead && n.spawnedBy == p.getId() && isRaider(n.npcType) && !bled.contains(n)
							&& Math.abs(n.absX - RAID_HOLD_X) <= 5 && Math.abs(n.absY - RAID_HOLD_Y) <= 5) {
						bled.add(n);
						int d = Math.max(1, n.MaxHP / 5);
						n.HP = Math.max(1, n.HP - d);
						n.handleHitMask(d);
						n.forceChat("*YELP*");
					}
				}
				if (aliveRaiders(p) > 0) {
					return;
				}
				if (wave == 3) {
					p.voidGate = RAID_WON;
					QuestAssistant.sendStages(p);
					p.getPacketSender().sendMessage("@gre@The last of them falls. Up on the ridge, something turns away... the raid is broken!");
					p.getPacketSender().sendMessage("Captain Ysolde will want to hear it from you.");
					c.stop();
					return;
				}
				if (--waveTick > 0) {
					return;
				}
				wave++;
				waveTick = 5;
				if (wave == 1) {
					p.getPacketSender().sendMessage("@red@Wolves! Three of them, coming down the road!");
					for (int i = 0; i < 3; i++) {
						spawnRaider(p, HOLLOWED_WOLF, i, 28, 3, 25, 20);
					}
				} else if (wave == 2) {
					p.getPacketSender().sendMessage("@red@Husks - shambling out of the dark in a line!");
					for (int i = 0; i < 3; i++) {
						spawnRaider(p, HOLLOW_HUSK, i, 34, 4, 30, 25);
					}
				} else {
					p.getPacketSender().sendMessage("@red@The ground shakes. Something huge is coming down the road...");
					p.getPlayerAssistant().sendCameraShake(2, 3, 2, 3);
					spawnRaider(p, HOLLOW_RAVAGER, 0, 80, 6, 45, 40);
					spawnRaider(p, HOLLOWED_WOLF, 3, 28, 3, 25, 20);
					spawnRaider(p, HOLLOWED_WOLF, 4, 28, 3, 25, 20);
					Npc rav = findSpawned(p, HOLLOW_RAVAGER);
					if (rav != null) {
						rav.forceChat("GRRRAAAAHHH!");
					}
				}
			}

			@Override
			public void stop() {
				RAIDING.remove(p.getId());
			}
		}, 1);
	}

	private static boolean isRaider(int type) {
		return type == HOLLOWED_WOLF || type == HOLLOW_HUSK || type == HOLLOW_RAVAGER;
	}

	private static int aliveRaiders(Player p) {
		return aliveSpawned(p, HOLLOWED_WOLF) + aliveSpawned(p, HOLLOW_HUSK) + aliveSpawned(p, HOLLOW_RAVAGER);
	}

	private static void clearRaiders(Player p) {
		for (int type : new int[] { HOLLOWED_WOLF, HOLLOW_HUSK, HOLLOW_RAVAGER }) {
			Npc n;
			while ((n = findSpawned(p, type)) != null) {
				banish(n);
			}
		}
	}

	static void npcKilled(Player killer, int npcType) {
		if (killer.voidGate == HUNT && (npcType == HOLLOWED_WOLF || npcType == HOLLOW_HUSK)) {
			killer.gateKills++;
			if (killer.gateKills < HUNT_KILLS) {
				killer.getPacketSender().sendMessage("It dies dragging itself towards the Scar. (" + killer.gateKills + " of " + HUNT_KILLS + ")");
			} else if (killer.gateKills == HUNT_KILLS) {
				killer.getPacketSender().sendMessage("That's " + HUNT_KILLS + ". Every one of them crawled towards the Scar. Kael should hear this.");
			}
		}
		if (npcType == HOLLOW_RAVAGER) {
			killer.getPacketSender().sendMessage("The ravager crashes down into the spikes and lies still.");
		}
	}

	/** Never leave a player half-way through a raid after a relog. */
	static void onLogin(Player p) {
		RAIDING.remove(p.getId());
	}
}
