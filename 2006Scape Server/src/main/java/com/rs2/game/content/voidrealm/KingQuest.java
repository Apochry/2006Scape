package com.rs2.game.content.voidrealm;

import static com.rs2.game.content.quests.impl.TheHollowKing.*;
import static com.rs2.game.content.voidrealm.VoidConstants.*;
import static com.rs2.game.content.voidrealm.VoidRealm.*;

import com.rs2.Constants;
import com.rs2.event.CycleEvent;
import com.rs2.event.CycleEventContainer;
import com.rs2.event.CycleEventHandler;
import com.rs2.game.content.quests.QuestAssistant;
import com.rs2.game.content.quests.impl.TheHollowKing;
import com.rs2.game.dialogues.ChatEmotes;
import com.rs2.game.npcs.Npc;
import com.rs2.game.npcs.NpcHandler;
import com.rs2.game.players.Player;
import com.rs2.util.Misc;

/**
 * Server-side content for "The Hollow King": the Scar splitting, the road through the rift, the battle on the void
 * platform with the Emberbrand, Aurel's release, and the Starless Gate it leaves open. Dialogue ids 20400-20599.
 */
final class KingQuest {

	private KingQuest() {
	}

	private static final int D_KAEL = 20400, D_KAEL_WAIT = 20407, D_MAELIS = 20410, D_MAELIS_WAIT = 20416, D_OREN = 20420,
			D_OREN_WAIT = 20427, D_AUREL = 20440, D_YSOLDE = 20460, D_RIFT = 20480, D_NO_BLADE = 20481;
	private static final int A_START = 20400, A_RIFT = 20401;
	private static final int OPTION_2 = 9157;

	static boolean firstClickNpc(Player p, int npcType) {
		int stage = p.voidKing;
		switch (npcType) {
		case KAEL:
			if (stage == NOT_STARTED && TheHollowKing.canStart(p)) {
				dialogue(p, D_KAEL, npcType);
			} else if (stage == STARTED) {
				dialogue(p, D_KAEL_WAIT, npcType);
			} else {
				return false;
			}
			return true;
		case MAELIS:
			if (stage == STARTED) {
				dialogue(p, D_MAELIS, npcType);
			} else if (stage == SEE_OREN || stage == ROAD_OPEN) {
				dialogue(p, D_MAELIS_WAIT, npcType);
			} else {
				return false;
			}
			return true;
		case OREN:
			if (stage == SEE_OREN) {
				dialogue(p, D_OREN, npcType);
			} else if (stage == ROAD_OPEN) {
				dialogue(p, D_OREN_WAIT, npcType);
			} else {
				return false;
			}
			return true;
		case AUREL_SPIRIT:
			dialogue(p, D_AUREL, npcType);
			return true;
		case YSOLDE:
			if (stage == KING_FALLEN) {
				dialogue(p, D_YSOLDE, npcType);
				return true;
			}
			return false;
		}
		return false;
	}

	static boolean sendDialogue(Player p, int id, int npcId) {
		if (id < 20400 || id >= 20600) {
			return false;
		}
		p.talkingNpc = npcId;
		p.nextChat = 0;
		switch (id) {
		/* ---------------- Kael: the Scar is splitting ---------------- */
		case D_KAEL:
			npc(p, id, KAEL, ChatEmotes.DISTRESSED, false, "You feel that? The ground's been humming since dawn.");
			break;
		case D_KAEL + 1:
			npc(p, id, KAEL, ChatEmotes.CALM_TALK2, false, "Look at the Scar. It's wider. Every hour, a little wider - like",
					"something's prising it open from the other side.");
			break;
		case D_KAEL + 2:
			me(p, id, false, "Vaelric said the King would open it himself, and walk through.");
			break;
		case D_KAEL + 3:
			npc(p, id, KAEL, ChatEmotes.CALM_TALK1, false, "Then he's cutting a road. And when it's done, every Hollowed in",
					"the void comes down it - into Gatehold, into everything.");
			break;
		case D_KAEL + 4:
			npc(p, id, KAEL, ChatEmotes.CALM_TALK2, false, "We can't hold that. Not with nine spears and a wall. But you've",
					"got a sword that cuts him. Maybe we don't wait for him to come to us.");
			break;
		case D_KAEL + 5:
			p.getDialogueHandler().sendOption("We take the fight to him.", "I need to prepare.");
			p.dialogueAction = A_START;
			break;
		case D_KAEL + 6:
			npc(p, id, KAEL, ChatEmotes.CALM_TALK1, true, "Then go and ask Maelis how. If anyone knows the way into the",
					"void that doesn't kill you, it's her and her books.");
			break;
		case D_KAEL_WAIT:
			npc(p, id, KAEL, ChatEmotes.CALM_TALK1, true, "Maelis, up at the stones. Ask her how we get to him. Hurry.");
			break;

		/* ---------------- Maelis: the way in ---------------- */
		case D_MAELIS:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK1, false, "Into the void? On purpose? ...Yes. Yes, it can be done. The",
					"chronicle says Aurel walked there and back, more than once, before it kept him.");
			break;
		case D_MAELIS + 1:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK2, false, "The rift you fell through still leads to his platform. It only",
					"spat you out because you had nothing it recognised.");
			break;
		case D_MAELIS + 2:
			npc(p, id, MAELIS, ChatEmotes.HAPPY_JOYFUL, false, "But now you carry his heart in a sword. The rift will know it.",
					"Someone has to tune it, though - someone who has sat beside it and listened.");
			break;
		case D_MAELIS + 3:
			me(p, id, false, "Oren. He waited by it for three days.");
			break;
		case D_MAELIS + 4:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK1, true, "Then go to Oren, at the crater. And when you face the King -",
					"remember there's a man under that crown. Aurel. He might still be listening.");
			if (p.voidKing == STARTED) {
				p.voidKing = SEE_OREN;
				QuestAssistant.sendStages(p);
			}
			break;
		case D_MAELIS_WAIT:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK1, true, p.voidKing == SEE_OREN ? "Oren, at the crater. He'll know how the rift sings."
					: "The rift is open? Then go. And hold the Emberbrand in your hand - not your pack. It must see him.");
			break;

		/* ---------------- Oren: tuning the rift ---------------- */
		case D_OREN:
			npc(p, id, OREN, ChatEmotes.CALM_TALK1, false, "Back again! And you're carrying... oh. Oh, that's him, isn't it.",
					"In the sword. I can hear him.");
			break;
		case D_OREN + 1:
			me(p, id, false, "Maelis says you can tune the rift so it takes me to him.");
			break;
		case D_OREN + 2:
			npc(p, id, OREN, ChatEmotes.CALM_TALK2, false, "Tune it? It's not a fiddle. ...But it does hum. And it hums",
					"different when that blade's near. Hm-hm-hmm... there.");
			break;
		case D_OREN + 3:
			p.getDialogueHandler().sendStatement(wide("Oren hums a low, crooked note. The rift answers - and its violet light turns",
					"the cold black of the void platform."));
			p.nextChat = id + 1;
			break;
		case D_OREN + 4:
			npc(p, id, OREN, ChatEmotes.CALM_TALK1, false, "Hold the sword in your hand when you step in, or it won't know",
					"you. And bring food. Lots.");
			break;
		case D_OREN + 5:
			npc(p, id, OREN, ChatEmotes.HAPPY_JOYFUL, true, "Come back, friend. You're the only one who ever listens to me.");
			if (p.voidKing == SEE_OREN) {
				p.voidKing = ROAD_OPEN;
				QuestAssistant.sendStages(p);
			}
			break;
		case D_OREN_WAIT:
			npc(p, id, OREN, ChatEmotes.CALM_TALK1, true, "The rift's ready. Sword in your hand, food in your pack. Go on.");
			break;

		/* ---------------- Aurel, at the end ---------------- */
		case D_AUREL:
			npc(p, id, AUREL_SPIRIT, ChatEmotes.SAD, false, "...I can see. Three hundred years, and I can see again.");
			break;
		case D_AUREL + 1:
			me(p, id, false, "King Aurel?");
			break;
		case D_AUREL + 2:
			npc(p, id, AUREL_SPIRIT, ChatEmotes.CALM_TALK2, false, "Once. Before the crown. I went looking for a cure for my",
					"people, and something in the dark offered me one. It only wanted a crown on my head.");
			break;
		case D_AUREL + 3:
			npc(p, id, AUREL_SPIRIT, ChatEmotes.SAD, false, "The crown was never mine. It belonged to the thing that lives",
					"in the dark between the stars. I only wore it. It wore me.");
			break;
		case D_AUREL + 4:
			me(p, id, false, "Sir Vaelric said to tell you he kept his oath.");
			break;
		case D_AUREL + 5:
			npc(p, id, AUREL_SPIRIT, ChatEmotes.HAPPY_JOYFUL, false, "...Of course he did. Of course he did.");
			break;
		case D_AUREL + 6:
			npc(p, id, AUREL_SPIRIT, ChatEmotes.CALM_TALK2, false, "Listen. The thing that crowned me is still out there. It has a",
					"gate, here, on this platform. It is hungry, and it is patient, and it has seen you now.");
			break;
		case D_AUREL + 7:
			npc(p, id, AUREL_SPIRIT, ChatEmotes.CALM_TALK1, false, "Do not go through that gate unless you are sure. Take this, and",
					"go home. Tell Gatehold its king is at rest.");
			GearQuest.giveMace(p);
			break;
		case D_AUREL + 8:
			p.getDialogueHandler().sendStatement(wide("Aurel's shape thins to light, and then to nothing. Far away, a bell is",
					"ringing in Gatehold. The platform lets you go."));
			p.nextChat = 0;
			leaveArena(p);
			break;

		/* ---------------- Ysolde: the end ---------------- */
		case D_YSOLDE:
			npc(p, id, YSOLDE, ChatEmotes.CALM_TALK1, false, "The Scar stopped humming an hour ago. Kael says it's closing.",
					"Tell me that's you.");
			break;
		case D_YSOLDE + 1:
			me(p, id, false, "The Hollow King is gone. Aurel is at rest.");
			break;
		case D_YSOLDE + 2:
			npc(p, id, YSOLDE, ChatEmotes.HAPPY_JOYFUL, false, "...Three hundred years. My grandmother's grandmother was born",
					"under that crown's shadow.");
			break;
		case D_YSOLDE + 3:
			npc(p, id, YSOLDE, ChatEmotes.CALM_TALK2, false, "You fell out of the sky a stranger. Now you're Gatehold's. Whatever",
					"else is out there between the stars - you won't face it alone.");
			break;
		case D_YSOLDE + 4:
			npc(p, id, YSOLDE, ChatEmotes.HAPPY_JOYFUL, true, "Now. Kael's broken out the good ale. Come and drink it before she",
					"does.");
			TheHollowKing.complete(p);
			QuestAssistant.sendStages(p);
			break;

		case D_RIFT:
			p.getDialogueHandler().sendOption("Skull Rock, on Skullbrine Isle.", "The void platform.");
			p.dialogueAction = A_RIFT;
			break;
		case D_NO_BLADE:
			p.getDialogueHandler().sendStatement(wide("The rift shows you the platform - then flinches away. It doesn't know you.",
					"(Wield the Emberbrand to take the rift to the void platform.)"));
			p.nextChat = 0;
			break;
		default:
			return false;
		}
		return true;
	}

	static boolean handleOption(Player p, int button) {
		if (p.dialogueAction != A_START && p.dialogueAction != A_RIFT || (button != OPTION_2 && button != OPTION_2 + 1)) {
			return false;
		}
		int action = p.dialogueAction;
		p.dialogueAction = -1;
		if (action == A_START) {
			if (button == OPTION_2) {
				if (p.voidKing == NOT_STARTED) {
					p.voidKing = STARTED;
					QuestAssistant.sendStages(p);
				}
				dialogue(p, D_KAEL + 6, KAEL);
			} else {
				p.getPacketSender().closeAllWindows();
			}
			return true;
		}
		p.getPacketSender().closeAllWindows();
		if (button == OPTION_2) {
			riftToSkullbrine(p);
		} else if (p.playerEquipment[p.playerWeapon] != EMBERBRAND) {
			dialogue(p, D_NO_BLADE, -1);
		} else {
			toPlatform(p);
		}
		return true;
	}

	/** The rift in Starfall Crater. Returns false for the ordinary way back to Skullbrine. */
	static boolean useRift(Player p) {
		if (inArena(p.absX, p.absY)) {
			if (findSpawned(p, AUREL_SPIRIT) != null) {
				dialogue(p, D_AUREL, AUREL_SPIRIT);
				return true;
			}
			p.startAnimation(827);
			p.getPacketSender().sendMessage("You step into the tear in the dark...");
			later(p, 2, () -> {
				p.getPlayerAssistant().movePlayer(LANDING_X, LANDING_Y, 0);
				p.getPacketSender().sendMessage("...and out into Starfall Crater.");
			});
			return true;
		}
		if (p.voidKing < ROAD_OPEN) {
			return false;
		}
		dialogue(p, D_RIFT, -1);
		return true;
	}

	private static void toPlatform(Player p) {
		p.startAnimation(827);
		p.gfx0(86);
		p.getPacketSender().sendMessage("The Emberbrand beats in your hand. The rift knows it, and opens...");
		later(p, 2, () -> {
			p.getPlayerAssistant().movePlayer(ARENA_ARRIVAL_X, ARENA_ARRIVAL_Y, 0);
			later(p, 2, () -> showPlatform(p));
			if (p.voidKing == ROAD_OPEN) {
				later(p, 2, () -> startBattle(p));
			} else {
				later(p, 2, () -> p.getPacketSender().sendMessage(p.voidKing >= COMPLETE
						? "The platform is silent. At its far edge, the Starless Gate stands open." : "The platform is silent now."));
			}
		});
	}

	/** Back to the crater (Aurel's farewell, or the platform being left). */
	static void leaveArena(Player p) {
		Npc a;
		while ((a = findSpawned(p, AUREL_SPIRIT)) != null) {
			banish(a);
		}
		if (!inArena(p.absX, p.absY)) {
			return;
		}
		if (p.voidKing == KING_FALLEN) {
			p.getPacketSender().sendMessage("Aurel's last words go with you: \"Tell Gatehold its king is at rest.\"");
		}
		later(p, 3, () -> {
			if (inArena(p.absX, p.absY)) {
				p.getPlayerAssistant().movePlayer(LANDING_X, LANDING_Y, 0);
				p.getPacketSender().sendMessage("You stumble out of the rift into Starfall Crater.");
			}
		});
	}

	/* ====================================================================== */
	/*                           THE BATTLE ON THE PLATFORM                    */
	/* ====================================================================== */

	private static final String[] KING_LINES = { "That blade... that is MINE!", "You carry my heart like a stolen purse.",
			"I will take it back from your cooling hand.", "The void remembers everything. It will remember you screaming.",
			"Kneel to your King!" };

	private static void clearKing(Player p) {
		for (int type : new int[] { HOLLOW_KING, HOLLOW_KNIGHT }) {
			Npc n;
			while ((n = findSpawned(p, type)) != null) {
				banish(n);
			}
		}
	}

	private static void startBattle(Player p) {
		if (p.voidKing != ROAD_OPEN || !inArena(p.absX, p.absY)) {
			return;
		}
		clearKing(p);
		NpcHandler.spawnNpc(p, HOLLOW_KING, KING_X, KING_Y, 0, 0, 300, 8, 90, 75, false, false);
		Npc king = findSpawned(p, HOLLOW_KING);
		if (king == null) {
			return;
		}
		p.getPlayerAssistant().sendCameraShake(1, 4, 1, 4);
		CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
			int tick, lastHp = king.HP, end = -1;
			boolean called;
			int[][] sky;

			@Override
			public void execute(CycleEventContainer c) {
				tick++;
				if (p.disconnected || p.isDead || !inArena(p.absX, p.absY)) {
					clearKing(p);
					if (!p.disconnected && p.voidKing == ROAD_OPEN) {
						p.getPacketSender().sendMessage("The Hollow King's laughter follows you out of the void.");
					}
					c.stop();
					return;
				}
				if (end >= 0) {
					ending(p, king, tick - end, c);
					return;
				}
				if (tick == 1) {
					kingSays(p, king, "You came. And you brought me my heart.");
					return;
				}
				if (tick == 4) {
					kingSays(p, king, "Then I will take it back - and walk down your road myself.");
					return;
				}
				if (tick == 7) {
					p.getPacketSender().sendMessage("@red@The Hollow King rises from his throne!");
					king.underAttack = true;
					king.killerId = p.getId();
					lastHp = king.HP;
					return;
				}
				if (tick < 7) {
					return;
				}
				// the void closes every wound but the Emberbrand's
				if (king.HP < lastHp && p.playerEquipment[p.playerWeapon] != EMBERBRAND) {
					king.HP = lastHp;
					p.getPacketSender().sendMessage("The wound closes as soon as it opens. Only the Emberbrand can hurt him!");
				}
				lastHp = king.HP;
				if (king.isDead || king.HP <= king.MaxHP * 15 / 100) {
					end = tick;
					ending(p, king, 0, c);
					return;
				}
				if (!called && king.HP <= king.MaxHP / 2) {
					called = true;
					kingSays(p, king, "Knights! Your King commands you!");
					NpcHandler.spawnNpc(p, HOLLOW_KNIGHT, p.absX - 1, p.absY + 1, 0, 1, 40, 5, 45, 40, true, false);
					NpcHandler.spawnNpc(p, HOLLOW_KNIGHT, p.absX + 1, p.absY - 1, 0, 1, 40, 5, 45, 40, true, false);
				}
				if (tick % 15 == 0) { // a wave of void
					king.animNumber = 2656;
					king.animUpdateRequired = true;
					king.updateRequired = true;
					GearQuest.voidBolt(p, king, 1);
					later(p, 2, () -> strike(p, 3 + Misc.random(4)));
					p.getPacketSender().sendMessage("A wave of void rolls out of the King and over you.");
				}
				if (tick % 13 == 0) { // the sky falls: three marked tiles, one of them yours
					sky = new int[][] { { p.absX, p.absY }, { p.absX + Misc.random(4) - 2, p.absY + Misc.random(4) - 2 },
							{ p.absX + Misc.random(4) - 2, p.absY + Misc.random(4) - 2 } };
					GearQuest.markStars(p, sky);
					kingSays(p, king, "The sky is mine too!");
					p.getPacketSender().sendMessage("@red@The void above you darkens. Move!");
				}
				if (sky != null && tick % 13 == 2) {
					GearQuest.dropStars(p, sky);
				}
				if (sky != null && tick % 13 == 3) {
					boolean hit = false;
					for (int[] t : sky) {
						hit |= p.absX == t[0] && p.absY == t[1];
					}
					if (hit) {
						strike(p, 10 + Misc.random(5));
						p.getPacketSender().sendMessage("A shard of the sky slams into you!");
					}
					sky = null;
				}
				if (tick % 17 == 0) {
					kingSays(p, king, KING_LINES[(tick / 17) % KING_LINES.length]);
				}
			}

			@Override
			public void stop() {
				p.getPlayerAssistant().sendCameraReset();
			}
		}, 1);
	}

	/** The crown lets go: a scripted end, so the King never simply "dies" and drops loot. */
	private static void ending(Player p, Npc king, int t, CycleEventContainer c) {
		if (t == 0) {
			king.underAttack = false;
			king.killerId = 0;
			if (king.HP < 1) {
				king.HP = 1;
			}
			Npc k;
			while ((k = findSpawned(p, HOLLOW_KNIGHT)) != null) {
				banish(k);
			}
			p.getCombatAssistant().resetPlayerAttack();
			p.getPlayerAssistant().sendCameraShake(3, 12, 3, 12);
			kingSays(p, king, "No. NO. The light - it burns - I remember the light...");
		} else if (t == 4) {
			kingSays(p, king, "...Vaelric? Is that you?");
		} else if (t == 7) {
			p.gfx0(86);
			p.getPacketSender().sendMessage("The crown cracks down the middle. The darkness pours out of it like smoke.");
			int x = KING_X + 2, y = KING_Y + 2;
			banish(king);
			p.voidKing = KING_FALLEN;
			QuestAssistant.sendStages(p);
			NpcHandler.spawnNpc(p, AUREL_SPIRIT, x, y, 0, 0, 1, 0, 0, 0, false, false);
		} else if (t == 10) {
			c.stop();
			p.getPlayerAssistant().sendCameraReset();
			dialogue(p, D_AUREL, AUREL_SPIRIT);
		}
	}

	/** The way home from the platform, once the King is gone: a little tear of the rift by the arrival point. */
	static final int HOME_RIFT_X = 1564, HOME_RIFT_Y = 9497;

	static void showPlatform(Player p) {
		if (!inArena(p.absX, p.absY)) {
			return;
		}
		if (p.voidKing >= KING_FALLEN) {
			p.getPacketSender().object(RIFT, HOME_RIFT_X, HOME_RIFT_Y, 0, 10);
		}
		if (p.voidKing >= COMPLETE) {
			p.getPacketSender().object(STARLESS_GATE, ARENA_GATE_X, ARENA_GATE_Y, 0, 10);
		}
	}

	/** Damage that can kill: these fights are real. */
	static void strike(Player p, int damage) {
		damage = Math.min(damage, p.playerLevel[Constants.HITPOINTS]);
		if (damage > 0) {
			p.dealDamage(damage);
			p.handleHitMask(damage);
		}
	}

	static void npcKilled(Player killer, int npcType) {
		// the battle loop notices the King falling and plays the ending; nothing else to do here
	}
}
