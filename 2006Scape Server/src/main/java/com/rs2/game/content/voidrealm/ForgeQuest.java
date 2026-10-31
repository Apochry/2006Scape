package com.rs2.game.content.voidrealm;

import static com.rs2.game.content.quests.impl.Heartforged.*;
import static com.rs2.game.content.voidrealm.VoidConstants.*;
import static com.rs2.game.content.voidrealm.VoidRealm.*;

import com.rs2.Constants;
import com.rs2.event.CycleEvent;
import com.rs2.event.CycleEventContainer;
import com.rs2.event.CycleEventHandler;
import com.rs2.game.content.quests.QuestAssistant;
import com.rs2.game.content.quests.impl.Heartforged;
import com.rs2.game.dialogues.ChatEmotes;
import com.rs2.game.npcs.Npc;
import com.rs2.game.npcs.NpcHandler;
import com.rs2.game.players.Player;
import com.rs2.util.Misc;

/**
 * Server-side content for "Heartforged": Maelis's plan, the fallen star in Starfall Crater, the rime shrine and its
 * sentinel, Sir Vaelric's ambush at Dorrin's forge, and the Emberbrand. Dialogue ids 20200-20399.
 */
final class ForgeQuest {

	private ForgeQuest() {
	}

	private static final int D_MAELIS = 20200, D_MAELIS_LOW = 20208, D_MAELIS_WAIT = 20209, D_MAELIS_EMBER = 20210,
			D_DORRIN = 20230, D_DORRIN_LIST = 20236, D_DORRIN_FORGE = 20240, D_DORRIN_FIGHT = 20246, D_DORRIN_DONE = 20250,
			D_DORRIN_REFORGE = 20258, D_VAELRIC_DEAD = 20270;
	private static final int A_START = 20200;
	private static final int OPTION_2 = 9157;

	/** forgeFlags: the star has already thrown out its voidlings once. */
	private static final int STAR_WOKEN = 2;

	static boolean firstClickNpc(Player p, int npcType) {
		int stage = p.voidForge;
		switch (npcType) {
		case MAELIS:
			if (stage == NOT_STARTED && Heartforged.canStart(p)) {
				dialogue(p, miningLevel(p) >= MINING_LEVEL ? D_MAELIS : D_MAELIS_LOW, npcType);
			} else if (stage > NOT_STARTED && stage < COMPLETE && !owns(p, EMBER)) {
				dialogue(p, D_MAELIS_EMBER, npcType);
			} else if (stage > NOT_STARTED && stage < COMPLETE) {
				dialogue(p, D_MAELIS_WAIT, npcType);
			} else {
				return false;
			}
			return true;
		case DORRIN:
			if (stage == STARTED) {
				dialogue(p, D_DORRIN, npcType);
			} else if (stage == MATERIALS) {
				dialogue(p, hasMaterials(p) ? D_DORRIN_FORGE : D_DORRIN_LIST, npcType);
			} else if (stage == FORGING) {
				dialogue(p, D_DORRIN_FIGHT, npcType);
			} else if (stage == KNIGHT_SLAIN) {
				dialogue(p, hasMaterials(p) ? D_DORRIN_DONE : D_DORRIN_LIST, npcType);
			} else if (stage == COMPLETE && !owns(p, EMBERBRAND)) {
				dialogue(p, D_DORRIN_REFORGE, npcType);
			} else {
				return false;
			}
			return true;
		}
		return false;
	}

	private static int miningLevel(Player p) {
		return p.getPlayerAssistant().getLevelForXP(p.playerXP[Constants.MINING]);
	}

	private static boolean hasMaterials(Player p) {
		return p.getItemAssistant().playerHasItem(STARMETAL) && p.getItemAssistant().playerHasItem(RIMEHEART)
				&& p.getItemAssistant().playerHasItem(EMBER);
	}

	/** Carried, worn or banked. */
	static boolean owns(Player p, int item) {
		if (p.getItemAssistant().playerHasItem(item) || p.playerEquipment[p.playerWeapon] == item) {
			return true;
		}
		for (int b : p.bankItems) {
			if (b == item + 1) {
				return true;
			}
		}
		return false;
	}

	static boolean sendDialogue(Player p, int id, int npcId) {
		if (id < 20200 || id >= 20400) {
			return false;
		}
		p.talkingNpc = npcId;
		p.nextChat = 0;
		switch (id) {
		/* ---------------- Maelis: a blade for a king ---------------- */
		case D_MAELIS:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK1, false, "Ah. You. I've been reading - well, I'm always reading - but",
					"I've been reading about the Ember.");
			break;
		case D_MAELIS + 1:
			me(p, id, false, "Is it safe? It hasn't stopped beating.");
			break;
		case D_MAELIS + 2:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK2, false, "Safe? No. Useful? Enormously. The Hollow King is made of void,",
					"and void closes over anything you stick in it. Swords, spells, spears. It just... heals.");
			break;
		case D_MAELIS + 3:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK1, false, "But not around his own heart. A blade forged around the Ember",
					"would cut him the way nothing else can. It would remember being him.");
			break;
		case D_MAELIS + 4:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK2, false, "It needs metal that never belonged to this world, and cold that",
					"never thaws to temper it. Dorrin will know the rest. He's the only smith I'd trust with it.");
			break;
		case D_MAELIS + 5:
			p.getDialogueHandler().sendOption("I'll take the Ember to Dorrin.", "Not right now.");
			p.dialogueAction = A_START;
			break;
		case D_MAELIS + 6:
			npc(p, id, MAELIS, ChatEmotes.HAPPY_JOYFUL, true, "Good. And keep it close. He'll feel it moving. He'll want it back.");
			break;
		case D_MAELIS_LOW:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK2, true, "There's a way to use the Ember against him - but it means",
					"digging metal out of a fallen star. Come back when you can swing a pickaxe properly. (Level "
							+ MINING_LEVEL + " Mining.)");
			break;
		case D_MAELIS_WAIT:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK1, true, forgeHint(p));
			break;
		case D_MAELIS_EMBER:
			npc(p, id, MAELIS, ChatEmotes.DISTRESSED, false, "You've lost it? The Ember? ...No. No, it's found its way back",
					"here, look. It was sitting on Volume Nine.");
			break;
		case D_MAELIS_EMBER + 1:
			p.getItemAssistant().addOrDropItem(EMBER, 1);
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK2, true, "It wants to be used. Don't lose it again.");
			break;

		/* ---------------- Dorrin: the materials ---------------- */
		case D_DORRIN:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK1, false, "Maelis sent you? With... is that thing beating?");
			break;
		case D_DORRIN + 1:
			me(p, id, false, "It's the heart of the Hollow King. Maelis wants a sword forged around it.");
			break;
		case D_DORRIN + 2:
			npc(p, id, DORRIN, ChatEmotes.DISTRESSED, false, "Of course she does. Of course. Right.");
			break;
		case D_DORRIN + 3:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK2, false, "A blade like that, ordinary iron won't hold it - it'd crack the",
					"first time it beat. I need starmetal. There's a star came down in the crater, same night you did.");
			break;
		case D_DORRIN + 4:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK1, false, "And I need to quench it in something colder than the void. The",
					"old folk said there's a shrine of rime deep in the Frostfang Caves. Bring me its heart.");
			break;
		case D_DORRIN + 5:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK2, true, "Starmetal, the Rimeheart, and that Ember. Then we'll talk.");
			if (p.voidForge == STARTED) {
				p.voidForge = MATERIALS;
				QuestAssistant.sendStages(p);
			}
			break;
		case D_DORRIN_LIST:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK1, true, missingLine(p));
			break;
		case D_DORRIN_FORGE:
			npc(p, id, DORRIN, ChatEmotes.HAPPY_JOYFUL, false, "Starmetal. A Rimeheart. And the Ember. Hand them over and",
					"stand back.");
			break;
		case D_DORRIN_FORGE + 1:
			if (!hasMaterials(p)) {
				dialogue(p, D_DORRIN_LIST, DORRIN);
				return true;
			}
			p.voidForge = FORGING;
			QuestAssistant.sendStages(p);
			p.getDialogueHandler().sendStatement(wide("Dorrin sets the Ember into the furnace. It flares violet, and every fire in",
					"Gatehold gutters at once."));
			p.nextChat = id + 1;
			break;
		case D_DORRIN_FORGE + 2:
			npc(p, id, DORRIN, ChatEmotes.DISTRESSED, false, "That's... not good. Something felt that.");
			break;
		case D_DORRIN_FORGE + 3:
			p.getDialogueHandler().sendStatement(wide("Out on the square, the air splits open, and a knight in hollow black armour",
					"steps through it."));
			p.nextChat = 0;
			later(p, 1, () -> ambush(p));
			break;
		case D_DORRIN_FIGHT:
			npc(p, id, DORRIN, ChatEmotes.ANGRY1, true, "Don't look at me - deal with HIM! I can't finish it with that thing",
					"stood there!");
			if (findSpawned(p, SIR_VAELRIC) == null) {
				later(p, 2, () -> ambush(p));
			}
			break;
		case D_DORRIN_DONE:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK2, false, "He's gone? Good. Then let's finish this before anything else",
					"comes looking.");
			break;
		case D_DORRIN_DONE + 1:
			p.startAnimation(898);
			p.getDialogueHandler().sendStatement(wide("Dorrin folds the starmetal around the Ember, hammers until the violet light",
					"is sealed inside, and quenches it in the Rimeheart's cold."));
			p.nextChat = id + 1;
			break;
		case D_DORRIN_DONE + 2:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK1, false, "There. Best thing I've ever made, and I never want to make",
					"another. Feel that? It's still beating.");
			break;
		case D_DORRIN_DONE + 3:
			me(p, id, false, "Vaelric said the King will open the Scar himself.");
			break;
		case D_DORRIN_DONE + 4:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK2, true, "Then you'd better be holding that when he does. Tell Kael.",
					"She'll be watching the Scar.");
			if (p.voidForge == KNIGHT_SLAIN) {
				p.getItemAssistant().deleteItem(STARMETAL, 1);
				p.getItemAssistant().deleteItem(RIMEHEART, 1);
				p.getItemAssistant().deleteItem(EMBER, 1);
				Heartforged.complete(p);
				QuestAssistant.sendStages(p);
			}
			break;
		case D_DORRIN_REFORGE:
			npc(p, id, DORRIN, ChatEmotes.ANNOYED, true, "You LOST it? ...It came back here on its own, you know. Was",
					"stuck in my anvil this morning. Take it, and keep hold of it.");
			if (!owns(p, EMBERBRAND)) {
				p.getItemAssistant().addOrDropItem(EMBERBRAND, 1);
			}
			break;

		/* ---------------- Sir Vaelric's last words ---------------- */
		case D_VAELRIC_DEAD:
			p.getDialogueHandler().sendStatement(wide("Sir Vaelric falls to one knee. Under the helm, his voice is an old man's."));
			p.nextChat = id + 1;
			break;
		case D_VAELRIC_DEAD + 1:
			npc(p, id, SIR_VAELRIC, ChatEmotes.SAD, false, "I swore... to guard the King. Even after. Even when he was",
					"not the King any more.");
			break;
		case D_VAELRIC_DEAD + 2:
			npc(p, id, SIR_VAELRIC, ChatEmotes.SAD, false, "He is tired of waiting. He will open the Scar himself... and",
					"walk through it. Forge your blade, quickly.");
			break;
		case D_VAELRIC_DEAD + 3:
			npc(p, id, SIR_VAELRIC, ChatEmotes.SAD, true, "And when you find him... tell Aurel that Vaelric kept his oath.");
			break;
		default:
			return false;
		}
		return true;
	}

	private static String forgeHint(Player p) {
		switch (p.voidForge) {
		case STARTED:
			return "Take the Ember to Dorrin, at the forge in the square. He'll know what the blade needs.";
		case MATERIALS:
			return "Starmetal from the fallen star, and the heart of the rime shrine. Dorrin can't start without both.";
		case FORGING:
			return "Something came for the Ember? Of course it did. Don't let it leave Gatehold!";
		default:
			return "Dorrin can finish the blade now. Go!";
		}
	}

	private static String missingLine(Player p) {
		StringBuilder s = new StringBuilder("Still need ");
		java.util.List<String> need = new java.util.ArrayList<>();
		if (!p.getItemAssistant().playerHasItem(STARMETAL)) {
			need.add("starmetal from the fallen star in the crater");
		}
		if (!p.getItemAssistant().playerHasItem(RIMEHEART)) {
			need.add("the Rimeheart, from the shrine off the frozen hall in the Frostfang Caves");
		}
		if (!p.getItemAssistant().playerHasItem(EMBER)) {
			need.add("the Ember itself");
		}
		s.append(String.join(", and ", need)).append(".");
		return s.toString();
	}

	static boolean handleOption(Player p, int button) {
		if (p.dialogueAction != A_START || (button != OPTION_2 && button != OPTION_2 + 1)) {
			return false;
		}
		p.dialogueAction = -1;
		if (button == OPTION_2) {
			if (p.voidForge == NOT_STARTED) {
				p.voidForge = STARTED;
				QuestAssistant.sendStages(p);
			}
			if (!p.getItemAssistant().playerHasItem(EMBER)) {
				p.getItemAssistant().addOrDropItem(EMBER, 1);
			}
			dialogue(p, D_MAELIS + 6, MAELIS);
		} else {
			p.getPacketSender().closeAllWindows();
		}
		return true;
	}

	/* ====================================================================== */
	/*                       THE FALLEN STAR AND THE SHRINE                    */
	/* ====================================================================== */

	static boolean firstClickObject(Player p, int objectId, int x, int y) {
		if (objectId == FALLEN_STAR) {
			mineStar(p);
			return true;
		}
		if (objectId == RIME_ALTAR) {
			takeRimeheart(p);
			return true;
		}
		return false;
	}

	private static void mineStar(Player p) {
		if (p.voidForge >= COMPLETE) {
			GearQuest.mineStar(p);
			return;
		}
		if (p.voidForge != MATERIALS || p.getItemAssistant().playerHasItem(STARMETAL)) {
			p.getPacketSender().sendMessage(p.voidForge < MATERIALS ? "The star is still too hot to touch. It hums."
					: "You've all the starmetal you need.");
			return;
		}
		if (!hasPickaxe(p)) {
			p.getPacketSender().sendMessage("You need a pickaxe to chip at the star.");
			return;
		}
		if (miningLevel(p) < MINING_LEVEL) {
			p.getPacketSender().sendMessage("You need a Mining level of " + MINING_LEVEL + " to chip starmetal free.");
			return;
		}
		if (aliveSpawned(p, VOIDLING) > 0) {
			p.getPacketSender().sendMessage("Not with those things crawling all over you!");
			return;
		}
		p.startAnimation(625);
		p.getPacketSender().sendMessage("You swing at the star. It rings like a bell.");
		later(p, 3, () -> {
			if (Math.abs(p.absX - STAR_X) > 3 || Math.abs(p.absY - STAR_Y) > 3) {
				return;
			}
			if ((p.forgeFlags & STAR_WOKEN) == 0) {
				p.forgeFlags |= STAR_WOKEN;
				p.getPacketSender().sendMessage("@red@The star cracks - and the void inside it comes boiling out!");
				p.getPlayerAssistant().sendCameraShake(1, 3, 1, 3);
				for (int i = 0; i < 2; i++) {
					NpcHandler.spawnNpc(p, VOIDLING, p.absX + (i == 0 ? 1 : -1), p.absY, 0, 1, 22, 3, 25, 18, true, false);
				}
				later(p, 2, () -> p.getPlayerAssistant().sendCameraReset());
				return;
			}
			p.startAnimation(625);
			p.getItemAssistant().addOrDropItem(STARMETAL, 1);
			p.getPlayerAssistant().addSkillXP(600, Constants.MINING);
			p.getPacketSender().sendMessage("A shard of starmetal comes away in your hand. It reflects nothing.");
		});
	}

	private static void takeRimeheart(Player p) {
		if (p.voidForge >= COMPLETE) {
			GearQuest.wakeSentinel(p);
			return;
		}
		if (p.voidForge != MATERIALS || p.getItemAssistant().playerHasItem(RIMEHEART)) {
			p.getPacketSender().sendMessage("Frost creeps up your fingers. Best leave it be.");
			return;
		}
		if ((p.forgeFlags & SENTINEL_SLAIN) != 0) {
			p.startAnimation(832);
			p.getItemAssistant().addOrDropItem(RIMEHEART, 1);
			p.getPacketSender().sendMessage("You prise the Rimeheart out of the altar. It's so cold it burns.");
			return;
		}
		if (findSpawned(p, RIMEBOUND_SENTINEL) != null) {
			p.getPacketSender().sendMessage("The sentinel won't let you near it!");
			return;
		}
		p.getPacketSender().sendMessage("As you reach for the altar, the ice behind you cracks and stands up.");
		NpcHandler.spawnNpc(p, RIMEBOUND_SENTINEL, p.absX + 1, p.absY - 1, p.heightLevel, 1, 110, 7, 60, 55, true, false);
		GearQuest.sentinelFight(p);
	}

	/* ====================================================================== */
	/*                              SIR VAELRIC                               */
	/* ====================================================================== */

	private static final String[] VAELRIC_LINES = { "Give back the King's heart, thief.", "I held the Gate for three hundred years.",
			"My oath outlived my flesh. It will outlive you.", "For Aurel!" };

	private static void ambush(Player p) {
		if (p.voidForge != FORGING || findSpawned(p, SIR_VAELRIC) != null) {
			return;
		}
		NpcHandler.spawnNpc(p, SIR_VAELRIC, p.absX + 2, p.absY + 1, 0, 1, 170, 9, 75, 65, true, false);
		Npc knight = findSpawned(p, SIR_VAELRIC);
		if (knight == null) {
			return;
		}
		knight.gfx0(86);
		knight.forceChat("The King's heart. Give it to me.");
		CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
			int tick, shieldHp = -1, shieldTicks;
			boolean called;

			@Override
			public void execute(CycleEventContainer c) {
				tick++;
				if (knight.isDead || knight.npcType != SIR_VAELRIC || p.disconnected || p.isDead
						|| Math.abs(p.absX - knight.absX) > 20 || Math.abs(p.absY - knight.absY) > 20) {
					if (!knight.isDead && knight.npcType == SIR_VAELRIC) {
						banish(knight);
						Npc k;
						while ((k = findSpawned(p, HOLLOW_KNIGHT)) != null) {
							banish(k);
						}
						if (!p.disconnected) {
							p.getPacketSender().sendMessage("Sir Vaelric steps back into the dark. Dorrin can't work like this...");
						}
					}
					c.stop();
					return;
				}
				// every twenty ticks he raises a shield of void for five
				if (tick % 20 == 0) {
					shieldHp = knight.HP;
					shieldTicks = 5;
					knight.gfx0(86);
					knight.forceChat("The void keeps me.");
					p.getPacketSender().sendMessage("Vaelric raises a shield of void. Your blows slide off it!");
				}
				if (shieldTicks > 0) {
					if (knight.HP < shieldHp) {
						knight.HP = shieldHp;
					}
					if (--shieldTicks == 0) {
						p.getPacketSender().sendMessage("The void shield breaks apart.");
					}
				}
				if (!called && knight.HP <= knight.MaxHP / 2) {
					called = true;
					knight.forceChat("Brothers! To me!");
					p.getPacketSender().sendMessage("@red@Two hollow knights tear their way out of the air beside him!");
					NpcHandler.spawnNpc(p, HOLLOW_KNIGHT, p.absX - 1, p.absY + 1, 0, 1, 40, 5, 45, 40, true, false);
					NpcHandler.spawnNpc(p, HOLLOW_KNIGHT, p.absX + 1, p.absY - 1, 0, 1, 40, 5, 45, 40, true, false);
				}
				if (tick % 11 == 0) {
					knight.forceChat(VAELRIC_LINES[Misc.random(VAELRIC_LINES.length - 1)]);
				}
			}

			@Override
			public void stop() {
			}
		}, 1);
	}

	static void npcKilled(Player killer, int npcType) {
		if (npcType == RIMEBOUND_SENTINEL && killer.voidForge >= COMPLETE) {
			killer.getPacketSender().sendMessage("The sentinel shatters. It'll be back - the shrine always makes another.");
		} else if (npcType == RIMEBOUND_SENTINEL) {
			killer.forgeFlags |= SENTINEL_SLAIN;
			killer.getPacketSender().sendMessage("The sentinel shatters into a thousand pieces. The altar's heart is unguarded.");
		} else if (npcType == VOIDLING && killer.voidForge == MATERIALS && aliveSpawned(killer, VOIDLING) == 0
				&& (killer.forgeFlags & STAR_WOKEN) != 0) {
			killer.getPacketSender().sendMessage("The last of the star's void is gone. Now you can mine it.");
		} else if (npcType == SIR_VAELRIC && killer.voidForge == FORGING) {
			killer.voidForge = KNIGHT_SLAIN;
			QuestAssistant.sendStages(killer);
			Npc k;
			while ((k = findSpawned(killer, HOLLOW_KNIGHT)) != null) {
				banish(k);
			}
			later(killer, 2, () -> dialogue(killer, D_VAELRIC_DEAD, SIR_VAELRIC));
		}
	}
}
