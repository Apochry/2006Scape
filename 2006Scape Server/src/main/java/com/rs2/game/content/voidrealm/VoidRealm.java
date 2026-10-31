package com.rs2.game.content.voidrealm;

import static com.rs2.game.content.voidrealm.VoidConstants.*;

import com.rs2.Constants;
import com.rs2.GameEngine;
import com.rs2.event.CycleEvent;
import com.rs2.event.CycleEventContainer;
import com.rs2.event.CycleEventHandler;
import com.rs2.game.content.quests.QuestAssistant;
import com.rs2.game.content.quests.impl.BeyondTheVeil;
import com.rs2.game.content.quests.impl.CurseOfSkullbrine;
import com.rs2.game.content.quests.impl.EchoesOfTheVoid;
import com.rs2.game.content.skullbrine.SkullbrineConstants;
import com.rs2.game.dialogues.ChatEmotes;
import com.rs2.game.dialogues.DialogueHandler;
import com.rs2.game.npcs.Npc;
import com.rs2.game.npcs.NpcHandler;
import com.rs2.game.players.Player;
import com.rs2.util.Misc;

/**
 * Server-side content for the Void quest series: "Echoes of the Void" (the three splinters) and "Beyond the Veil"
 * (through the portal, the Hollow King, the fall to Nythera), plus travel between Skullbrine and Nythera and the
 * Seer's lantern.
 *
 * Hooked in through {@link com.rs2.game.content.skullbrine.SkullbrineIsle}, which calls these first.
 */
public final class VoidRealm {

	private VoidRealm() {
	}

	/* Dialogue ids (9600-9799) and option actions. */
	private static final int D_GHOST = 9600, D_GHOST_LATER = 9610, D_SEER_EARLY = 9620, D_SEER_INTRO = 9625,
			D_SEER_WHERE = 9640, D_SEER_HINT = 9655, D_SEER_BIND = 9660, D_SEER_VEIL = 9675, D_SEER_LATER = 9690,
			D_PORTAL = 9700, D_OREN = 9710, D_OREN_LATER = 9730, D_LANTERN = 9740;
	private static final int A_SEER_ASK = 9600, A_SEER_VEIL = 9601, A_PORTAL = 9602, A_LANTERN = 9603;

	private static final int SPADE = 952;
	private static final int[] PICKAXES = { 1265, 1267, 1269, 1273, 1271, 1275 };

	/** Skull Rock's jaw plaza, where the lantern takes you. */
	private static final int PLAZA_X = 2016, PLAZA_Y = 2923;
	/** In front of the portal in Blackbrine's Hold. */
	private static final int PORTAL_SIDE_X = 2016, PORTAL_SIDE_Y = 9332;

	private static final int GHOST = SkullbrineConstants.CAPTAIN_BLACKBRINE;

	/* ====================================================================== */
	/*                                  NPCS                                  */
	/* ====================================================================== */

	public static boolean firstClickNpc(Player player, int npcType) {
		switch (npcType) {
		case GHOST:
			if (player.skullbrine < CurseOfSkullbrine.COMPLETE) {
				return false; // still the Curse of Skullbrine's ghost
			}
			dialogue(player, player.voidEchoes == EchoesOfTheVoid.NOT_STARTED ? D_GHOST : D_GHOST_LATER, npcType);
			return true;
		case SEER_ILSABET:
			if (!EchoesOfTheVoid.canStart(player)) {
				dialogue(player, D_SEER_EARLY, npcType);
			} else if (player.voidEchoes <= EchoesOfTheVoid.STARTED) {
				dialogue(player, D_SEER_INTRO, npcType);
			} else if (player.voidEchoes == EchoesOfTheVoid.SPLINTERS) {
				dialogue(player, EchoesOfTheVoid.hasAllSplinters(player) ? D_SEER_BIND : D_SEER_HINT, npcType);
			} else if (player.voidVeil == BeyondTheVeil.NOT_STARTED) {
				dialogue(player, D_SEER_VEIL, npcType);
			} else {
				dialogue(player, D_SEER_LATER, npcType);
			}
			return true;
		case OREN:
			dialogue(player, player.voidVeil == BeyondTheVeil.FELL ? D_OREN : D_OREN_LATER, npcType);
			return true;
		}
		return false;
	}

	private static void dialogue(Player player, int id, int npcType) {
		player.getDialogueHandler().sendDialogues(id, npcType);
	}

	/** One line of NPC chat; continues to the next id unless {@code last}. */
	private static void npc(Player p, int id, int npc, ChatEmotes emote, boolean last, String... lines) {
		if ((npc == SEER_ILSABET || npc == OREN) && emote != ChatEmotes.HAPPY_JOYFUL && emote != ChatEmotes.CALM_TALK2
				&& emote != ChatEmotes.DEFAULT) {
			emote = ChatEmotes.CALM_TALK1; // their borrowed chatheads only have the talking animations
		}
		p.getDialogueHandler().sendNpcChat(npc, emote, wrap(lines));
		p.nextChat = last ? 0 : id + 1;
	}

	/** Re-flows dialogue text so no line is wider than the chatbox beside a chathead (at most 4 lines). */
	static String[] wrap(String... lines) {
		String text = String.join(" ", lines);
		java.util.List<String> best = flow(text, 50);
		if (best.size() > 4) {
			return lines;
		}
		// balance: the narrowest width that still needs no more lines, so the last line isn't one lonely word
		for (int width = text.length() / best.size(); width < 50; width++) {
			java.util.List<String> f = flow(text, width);
			if (f.size() == best.size()) {
				best = f;
				break;
			}
		}
		return best.toArray(new String[0]);
	}

	private static java.util.List<String> flow(String text, int width) {
		java.util.List<String> out = new java.util.ArrayList<>();
		StringBuilder cur = new StringBuilder();
		for (String w : text.split(" ")) {
			if (cur.length() > 0 && cur.length() + 1 + w.length() > width) {
				out.add(cur.toString());
				cur.setLength(0);
			}
			cur.append(cur.length() > 0 ? " " : "").append(w);
		}
		out.add(cur.toString());
		return out;
	}

	private static void me(Player p, int id, boolean last, String line) {
		p.getDialogueHandler().sendPlayerChat(wrap(line));
		p.nextChat = last ? 0 : id + 1;
	}

	/** Called from SkullbrineIsle#sendDialogue for ids 9600-9799. */
	public static boolean sendDialogue(Player p, int id, int npcId) {
		if (id < 9600 || id >= 9800) {
			return false;
		}
		DialogueHandler d = p.getDialogueHandler();
		p.talkingNpc = npcId;
		p.nextChat = 0;
		final int SEER = SEER_ILSABET;
		switch (id) {
		/* ---------------- Captain Blackbrine's ghost: the door sings ---------------- */
		case D_GHOST:
			npc(p, id, GHOST, ChatEmotes.CALM_TALK2, false, "You came back. Good. Listen... since the pearl went",
					"home, the door has started singing.");
			break;
		case D_GHOST + 1:
			me(p, id, false, "Singing?");
			break;
		case D_GHOST + 2:
			npc(p, id, GHOST, ChatEmotes.DISTRESSED, false, "Not to me. To YOU. It says your name in the dark, over",
					"and over, like a sailor counting the days home.");
			break;
		case D_GHOST + 3:
			npc(p, id, GHOST, ChatEmotes.CALM_TALK1, false, "Three nights ago a woman stepped off a fishing boat and",
					"sat down in front of my skull like she owned it. Calls",
					"herself a seer. Says the door can be opened - properly.");
			break;
		case D_GHOST + 4:
			npc(p, id, GHOST, ChatEmotes.CALM_TALK2, true, "She's camped by the skull's jaw. Go and talk to her.",
					"And mind her eyes - they're the colour of that door.");
			start(p);
			break;
		case D_GHOST_LATER:
			if (p.voidVeil >= BeyondTheVeil.FELL) {
				npc(p, id, GHOST, ChatEmotes.HAPPY_JOYFUL, true, "You went through... and came back! Not many can say",
						"that. Not ANY, until you.");
			} else if (p.voidEchoes >= EchoesOfTheVoid.COMPLETE) {
				npc(p, id, GHOST, ChatEmotes.DISTRESSED, true, "That sigil you carry... the door goes quiet when you're",
						"near, like it's holding its breath.");
			} else {
				npc(p, id, GHOST, ChatEmotes.CALM_TALK1, true, "The door sings louder every night. Talk to the seer",
						"by the skull's jaw.");
			}
			break;

		/* ---------------- Seer Ilsabet: Echoes of the Void ---------------- */
		case D_SEER_EARLY:
			npc(p, id, SEER, ChatEmotes.CALM_TALK1, true, "The curse still holds this island. I can't hear the door",
					"over the moaning of the dead. Lift it, then we'll talk.");
			break;
		case D_SEER_INTRO:
			npc(p, id, SEER, ChatEmotes.HAPPY_JOYFUL, false, "There you are. The one the door keeps whispering about.",
					"I am Ilsabet. I've followed that song across three seas.");
			start(p);
			break;
		case D_SEER_INTRO + 1:
			me(p, id, false, "What is it? The portal, I mean.");
			break;
		case D_SEER_INTRO + 2:
			npc(p, id, SEER, ChatEmotes.CALM_TALK2, false, "A door. On the other side is a land called Nythera.",
					"Long ago someone sealed it shut. When your captain tore",
					"the pearl out, the seal cracked - and three splinters flew.");
			break;
		case D_SEER_INTRO + 3:
			npc(p, id, SEER, ChatEmotes.CALM_TALK1, false, "Bind the splinters into a sigil, and the door will open",
					"for whoever holds it. Without them, it only sings.");
			break;
		case D_SEER_INTRO + 4:
			d.sendOption("Where are the splinters?", "Why would I want to open it?");
			p.dialogueAction = A_SEER_ASK;
			break;
		case D_SEER_INTRO + 5:
			me(p, id, false, "Why would I want to open it?");
			break;
		case D_SEER_INTRO + 6:
			npc(p, id, SEER, ChatEmotes.DISTRESSED, false, "Because something on the other side already knows your",
					"name. Better to walk through on your own terms than to",
					"wait for it to come through on its own.");
			break;
		case D_SEER_INTRO + 7:
			me(p, D_SEER_WHERE - 1, false, "...Fine. Where are the splinters?");
			break;
		case D_SEER_WHERE - 1:
		case D_SEER_WHERE:
			npc(p, D_SEER_WHERE, SEER, ChatEmotes.CALM_TALK2, false, "I've scried for them. The first weeps in the eye of",
					"the skull - the eye that cries. Reach into its ghostfire.");
			break;
		case D_SEER_WHERE + 1:
			npc(p, id, SEER, ChatEmotes.CALM_TALK1, false, "The second sleeps with the dead, in the grave of the man",
					"who saw the far shore. Read the stones in the graveyard.",
					"You'll need a spade.");
			break;
		case D_SEER_WHERE + 2:
			npc(p, id, SEER, ChatEmotes.DISTRESSED, false, "And the third is still screaming. Deep in the rock below",
					"the skull, where the dragon-metal grows. Bring a pickaxe.");
			break;
		case D_SEER_WHERE + 3:
			npc(p, id, SEER, ChatEmotes.CALM_TALK2, true, "Bring all three to me. And a strong stomach - the",
					"splinters have been alone in the dark a long time.");
			if (p.voidEchoes < EchoesOfTheVoid.SPLINTERS) {
				p.voidEchoes = EchoesOfTheVoid.SPLINTERS;
				QuestAssistant.sendStages(p);
			}
			break;
		case D_SEER_HINT:
			npc(p, id, SEER, ChatEmotes.CALM_TALK1, true, seerHint(p));
			break;
		case D_SEER_BIND:
			npc(p, id, SEER, ChatEmotes.HAPPY_JOYFUL, false, "All three... I can feel them humming from here.",
					"Give them to me. Hold still.");
			break;
		case D_SEER_BIND + 1:
			d.sendStatement("Ilsabet presses the splinters together between her palms.",
					"Violet light leaks between her fingers. The air goes cold,",
					"and for a moment the whole island is perfectly silent.");
			p.gfx0(86);
			p.nextChat = id + 1;
			break;
		case D_SEER_BIND + 2:
			npc(p, id, SEER, ChatEmotes.CALM_TALK2, false, "The Void sigil. The door will know it - and you.",
					"Take my lantern too. Gaze into its flame and it will",
					"bring you back to this skull from anywhere.");
			break;
		case D_SEER_BIND + 3:
			EchoesOfTheVoid.complete(p);
			QuestAssistant.sendStages(p);
			break;

		/* ---------------- Seer Ilsabet: Beyond the Veil ---------------- */
		case D_SEER_VEIL:
			npc(p, id, SEER, ChatEmotes.DISTRESSED, false, "You've carried the sigil long enough to feel it. Something",
					"on the other side is pressing back against the door.");
			break;
		case D_SEER_VEIL + 1:
			npc(p, id, SEER, ChatEmotes.SAD, false, "I looked through the weeping eye again last night. There",
					"is a king beyond the door. A king made of nothing,",
					"sitting on a throne in the dark. Waiting.");
			break;
		case D_SEER_VEIL + 2:
			d.sendOption("Then I'll face him.", "I need to prepare first.");
			p.dialogueAction = A_SEER_VEIL;
			break;
		case D_SEER_VEIL + 3:
			me(p, id, false, "Then I'll face him.");
			break;
		case D_SEER_VEIL + 4:
			npc(p, id, SEER, ChatEmotes.CALM_TALK1, false, "Use the sigil on the door in the portal chamber. Bring",
					"food. Wear your best. And listen to me:");
			break;
		case D_SEER_VEIL + 5:
			npc(p, id, SEER, ChatEmotes.DISTRESSED, true, "I don't think you can beat him. I think he wants to see",
					"you. That might be worse.");
			if (p.voidVeil == BeyondTheVeil.NOT_STARTED) {
				p.voidVeil = BeyondTheVeil.STARTED;
				QuestAssistant.sendStages(p);
			}
			break;
		case D_SEER_VEIL + 6:
			me(p, id, true, "I need to prepare first.");
			break;
		case D_SEER_LATER:
			if (p.voidVeil >= BeyondTheVeil.FELL) {
				npc(p, id, SEER, ChatEmotes.CALM_TALK2, true, "You fell to Nythera and lived. He let you go. Remember",
						"that - nothing he does is mercy.");
			} else if (p.voidVeil == BeyondTheVeil.IN_VOID) {
				npc(p, id, SEER, ChatEmotes.DISTRESSED, true, "You saw him, didn't you? The door is still open for you.",
						"Go back when you're ready.");
			} else {
				npc(p, id, SEER, ChatEmotes.CALM_TALK1, true, "Use the sigil on the door in the portal chamber, below",
						"the skull. I'll be listening.");
			}
			break;

		/* ---------------- the open portal ---------------- */
		case D_PORTAL:
			d.sendStatement("The sigil flares in your hand. The portal tears open, and",
					"beyond it is nothing - utter, endless nothing - and, far",
					"off in it, something that turns to look at you.");
			p.nextChat = id + 1;
			break;
		case D_PORTAL + 1:
			d.sendOption("Step through.", "Not yet.");
			p.dialogueAction = A_PORTAL;
			break;

		/* ---------------- Oren the Wanderer ---------------- */
		case D_OREN:
			npc(p, id, OREN, ChatEmotes.DISTRESSED, false, "Easy, easy! You fell out of the sky, friend - straight",
					"through a hole in it. I've sat by this crater three days",
					"waiting for something to come out of that rift.");
			break;
		case D_OREN + 1:
			npc(p, id, OREN, ChatEmotes.CALM_TALK1, false, "Didn't expect it to be a person.");
			break;
		case D_OREN + 2:
			me(p, id, false, "Where am I?");
			break;
		case D_OREN + 3:
			npc(p, id, OREN, ChatEmotes.SAD, false, "Nythera. Or what's left of it. Green hills, a sky full of",
					"violet stars... and a shadow creeping in from the east.",
					"The land there is dying. Hollowing out.");
			break;
		case D_OREN + 4:
			npc(p, id, OREN, ChatEmotes.DISTRESSED, false, "You came through HIS door, didn't you? The Hollow King's.",
					"He let you go. He never lets anyone go.");
			break;
		case D_OREN + 5:
			me(p, id, false, "He said he'd come for me when I was worth devouring.");
			break;
		case D_OREN + 6:
			npc(p, id, OREN, ChatEmotes.CALM_TALK2, false, "Then we'd best make sure you're never worth it - or",
					"that you're strong enough to stop him when you are.");
			break;
		case D_OREN + 7:
			npc(p, id, OREN, ChatEmotes.CALM_TALK1, false, "There are people inland who'll want to meet you. Not",
					"today - you're shaking. The rift behind you knows you",
					"now; it'll take you home and bring you back.");
			break;
		case D_OREN + 8:
			npc(p, id, OREN, ChatEmotes.HAPPY_JOYFUL, false, "Here - this was on the ground beside you when you",
					"landed. Looks like a piece of that platform followed you",
					"down. A blade, if I'm any judge.");
			break;
		case D_OREN + 9:
			BeyondTheVeil.complete(p);
			QuestAssistant.sendStages(p);
			break;
		case D_OREN_LATER:
			if (p.voidVeil >= BeyondTheVeil.COMPLETE) {
				npc(p, id, OREN, ChatEmotes.CALM_TALK1, true, "When you're ready, the old road leads inland past the",
						"Broken Gate. I'll be here... for now.");
			} else {
				npc(p, id, OREN, ChatEmotes.DISTRESSED, true, "Who... how did you get here?");
			}
			break;

		/* ---------------- Seer's lantern ---------------- */
		case D_LANTERN:
			d.sendOption("Skull Rock, Skullbrine Isle", "Starfall Crater, Nythera");
			p.dialogueAction = A_LANTERN;
			break;
		default:
			return false;
		}
		return true;
	}

	private static void start(Player p) {
		if (p.voidEchoes == EchoesOfTheVoid.NOT_STARTED) {
			p.voidEchoes = EchoesOfTheVoid.STARTED;
			QuestAssistant.sendStages(p);
		}
	}

	private static String[] seerHint(Player p) {
		if (!p.getItemAssistant().playerHasItem(EYE_SPLINTER)) {
			return new String[] { "The first splinter weeps in the eye of the skull - the eye", "that cries. Reach into its ghostfire." };
		}
		if (!p.getItemAssistant().playerHasItem(GRAVE_SPLINTER)) {
			return new String[] { "The second sleeps in the grave of the man who saw the far", "shore. Read the stones. Bring a spade." };
		}
		return new String[] { "The last is screaming in the rock below, where the", "dragon-metal grows. Bring a pickaxe." };
	}

	/** Called from SkullbrineIsle#handleOption. */
	public static boolean handleOption(Player p, int buttonId) {
		boolean first = buttonId == 9157, second = buttonId == 9158;
		if (!first && !second) {
			return false;
		}
		switch (p.dialogueAction) {
		case A_SEER_ASK:
			p.dialogueAction = -1;
			dialogue(p, first ? D_SEER_WHERE : D_SEER_INTRO + 5, SEER_ILSABET);
			return true;
		case A_SEER_VEIL:
			p.dialogueAction = -1;
			dialogue(p, first ? D_SEER_VEIL + 3 : D_SEER_VEIL + 6, SEER_ILSABET);
			return true;
		case A_PORTAL:
			p.dialogueAction = -1;
			p.getPacketSender().closeAllWindows();
			if (first) {
				enterVoid(p);
			}
			return true;
		case A_LANTERN:
			p.dialogueAction = -1;
			p.getPacketSender().closeAllWindows();
			if (first) {
				lanternTo(p, PLAZA_X, PLAZA_Y, "The flame shows you a grinning skull of bone... and then you're standing in front of it.");
			} else {
				lanternTo(p, LANDING_X, LANDING_Y - 2, "The flame shows you a smoking crater under violet stars... and then you're there.");
			}
			return true;
		}
		return false;
	}

	/* ====================================================================== */
	/*                                 OBJECTS                                */
	/* ====================================================================== */

	/** Called first from SkullbrineIsle#firstClickObject. */
	public static boolean firstClickObject(Player p, int objectId, int x, int y) {
		switch (objectId) {
		case EYE_FIRE:
			reachIntoEye(p, x);
			return true;
		case EPITAPH_GRAVE:
			int grave = graveAt(x, y);
			if (grave >= 0) {
				p.getDialogueHandler().sendStatement(EPITAPHS[grave]);
				p.nextChat = 0;
			}
			return true;
		case VOID_VEIN:
			mineVein(p);
			return true;
		case RIFT:
			useRift(p);
			return true;
		case SkullbrineConstants.VOID_PORTAL:
			if (onNythera(x, y)) {
				useRift(p);
				return true;
			}
			return touchPortal(p);
		}
		return false;
	}

	/** Second option on objects (the graves' "Dig"). */
	public static boolean secondClickObject(Player p, int objectId, int x, int y) {
		if (objectId == EPITAPH_GRAVE) {
			digGrave(p, graveAt(x, y));
			return true;
		}
		return false;
	}

	/** Called first from SkullbrineIsle#itemOnObject. */
	public static boolean itemOnObject(Player p, int objectId, int x, int y, int itemId) {
		if (objectId == SkullbrineConstants.VOID_PORTAL && itemId == VOID_SIGIL && !onNythera(x, y)) {
			return touchPortal(p);
		}
		if (objectId == EPITAPH_GRAVE && itemId == SPADE) {
			digGrave(p, graveAt(x, y));
			return true;
		}
		return false;
	}

	private static int graveAt(int x, int y) {
		for (int i = 0; i < GRAVES.length; i++) {
			if (GRAVES[i][0] == x && GRAVES[i][1] == y) {
				return i;
			}
		}
		return -1;
	}

	/* ---------------- the weeping eye ---------------- */

	private static void reachIntoEye(Player p, int x) {
		if (p.voidEchoes != EchoesOfTheVoid.SPLINTERS) {
			p.getPacketSender().sendMessage("The ghostfire burns cold and bright. You have no reason to put your hand in it.");
			return;
		}
		p.startAnimation(832);
		if (x != RIGHT_EYE_X) {
			p.gfx0(78);
			hurt(p, 2 + Misc.random(3));
			p.getDialogueHandler().sendStatement("The cold fire bites to the bone! In the flames you see a throne",
					"of black glass on a platform hanging in nothing. Something vast",
					"sits on it - and slowly turns its head towards you.");
			p.nextChat = 0;
			p.getPacketSender().sendMessage("This eye doesn't weep. Whatever's in here, it isn't a splinter.");
			return;
		}
		if (p.getItemAssistant().playerHasItem(EYE_SPLINTER)) {
			p.getPacketSender().sendMessage("Only ghostfire now. The eye has given up its splinter.");
			return;
		}
		if (p.getItemAssistant().freeSlots() < 1) {
			p.getPacketSender().sendMessage("You need a free inventory space.");
			return;
		}
		p.getItemAssistant().addItem(EYE_SPLINTER, 1);
		p.getDialogueHandler().sendItemChat(EYE_SPLINTER, 200, "Eye splinter", wrap(
				"The fire parts like water. You see a green land under violet",
				"stars, a crater smoking on its shore... then your fingers",
				"close on something sharp, and the vision is gone."));
		p.nextChat = 0;
	}

	/* ---------------- the graveyard ---------------- */

	private static void digGrave(Player p, int grave) {
		if (grave < 0) {
			return;
		}
		if (!p.getItemAssistant().playerHasItem(SPADE)) {
			p.getPacketSender().sendMessage("You'd need a spade to dig here.");
			return;
		}
		if (p.voidEchoes != EchoesOfTheVoid.SPLINTERS) {
			p.getPacketSender().sendMessage("You have no reason to disturb the dead.");
			return;
		}
		p.startAnimation(830);
		int gx = GRAVES[grave][0], gy = GRAVES[grave][1];
		if (grave != JORY) {
			if (System.currentTimeMillis() - p.lastGraveGrab < 15000) {
				p.getPacketSender().sendMessage("You're not putting your hand anywhere near that grave again.");
				return;
			}
			p.lastGraveGrab = System.currentTimeMillis();
			p.getPacketSender().sendMessage("A cold, wet hand bursts out of the earth and grabs your wrist!");
			hurt(p, 1 + Misc.random(2));
			NpcHandler.spawnNpc(p, SkullbrineConstants.DROWNED_DECKHAND, gx + 1, gy, 0, 1, 12, 2, 12, 12, true, false);
			Npc hand = findSpawned(p, SkullbrineConstants.DROWNED_DECKHAND);
			if (hand != null) {
				hand.forceChat("Not... my... grave...");
			}
			return;
		}
		if (p.getItemAssistant().playerHasItem(GRAVE_SPLINTER)) {
			p.getPacketSender().sendMessage("Jory's grave is empty now. Whatever lay in it has gone.");
			return;
		}
		if (aliveSpawned(p, HOLLOW_WRAITH) > 0) {
			p.getPacketSender().sendMessage("The wraith is still here!");
			return;
		}
		p.getPacketSender().sendMessage("Jory's grave splits open - and something that was never Jory climbs out!");
		p.getPlayerAssistant().sendCameraShake(1, 8, 1, 8);
		NpcHandler.spawnNpc(p, HOLLOW_WRAITH, gx, gy - 1, 0, 1, 34, 4, 30, 25, true, false);
		Npc wraith = findSpawned(p, HOLLOW_WRAITH);
		if (wraith != null) {
			wraith.forceChat("He saw the far shore... and so did I...");
		}
		later(p, 4, () -> p.getPlayerAssistant().sendCameraReset());
	}

	/* ---------------- the shrieking vein ---------------- */

	private static void mineVein(Player p) {
		if (p.voidEchoes != EchoesOfTheVoid.SPLINTERS) {
			p.getPacketSender().sendMessage("The violet veins pulse slowly. You'd rather leave this rock alone.");
			return;
		}
		if (p.getItemAssistant().playerHasItem(VEIN_SPLINTER)) {
			p.getPacketSender().sendMessage("The vein is dark and still now.");
			return;
		}
		if (!hasPickaxe(p)) {
			p.getPacketSender().sendMessage("You need a pickaxe to break into the vein.");
			return;
		}
		if (aliveSpawned(p, VOIDLING) > 0) {
			p.getPacketSender().sendMessage("Deal with the voidlings first!");
			return;
		}
		p.startAnimation(625);
		if (p.voidVeinCleared) {
			if (p.getItemAssistant().freeSlots() < 1) {
				p.getPacketSender().sendMessage("You need a free inventory space.");
				return;
			}
			p.voidVeinCleared = false;
			p.getItemAssistant().addItem(VEIN_SPLINTER, 1);
			p.getDialogueHandler().sendItemChat(VEIN_SPLINTER, 200, "Vein splinter", wrap(
					"With the voidlings gone the vein is silent. One swing",
					"cracks it open, and a splinter drops into your palm,",
					"still beating like a heart."));
			p.nextChat = 0;
			return;
		}
		p.getPacketSender().sendMessage("Your pick bites into the violet vein - and it SHRIEKS.");
		p.getPlayerAssistant().sendCameraShake(2, 10, 2, 10);
		later(p, 2, () -> {
			p.getPacketSender().sendMessage("Something squirms out of the cracks!");
			p.voidlingsLeft = 3;
			spawnVoidling(p);
			p.getPlayerAssistant().sendCameraReset();
		});
	}

	/** Voidlings come out of the vein one at a time (single-way combat): four in all. */
	private static void spawnVoidling(Player p) {
		NpcHandler.spawnNpc(p, VOIDLING, p.absX, p.absY, p.heightLevel, 1, 10 + (3 - p.voidlingsLeft) * 3, 2,
				14, 10, true, false);
		p.getPlayerAssistant().sendCameraShake(1, 4, 1, 4);
		later(p, 2, () -> p.getPlayerAssistant().sendCameraReset());
	}

	/* ---------------- the portal and the rift ---------------- */

	/** Returns false to let Curse of Skullbrine handle the portal (pearl, dormant messages). */
	private static boolean touchPortal(Player p) {
		if (p.skullbrine < CurseOfSkullbrine.COMPLETE) {
			return false;
		}
		if (p.voidVeil >= BeyondTheVeil.FELL) {
			p.startAnimation(827);
			p.getPacketSender().sendMessage("The door knows you now. You step through, and fall gently...");
			later(p, 2, () -> {
				p.getPlayerAssistant().movePlayer(LANDING_X, LANDING_Y, 0);
				p.getPacketSender().sendMessage("...onto the scorched floor of Starfall Crater.");
			});
			return true;
		}
		if (p.voidVeil >= BeyondTheVeil.STARTED && p.getItemAssistant().playerHasItem(VOID_SIGIL)) {
			dialogue(p, D_PORTAL, -1);
			return true;
		}
		if (p.getItemAssistant().playerHasItem(VOID_SIGIL)) {
			p.getPacketSender().sendMessage("The sigil burns cold in your hand and the portal shivers. Ilsabet will know what this means.");
			return true;
		}
		if (p.voidVeil >= BeyondTheVeil.STARTED) {
			p.getPacketSender().sendMessage("The door won't open without the Void sigil. Ilsabet may be able to help.");
			return true;
		}
		return false;
	}

	private static void useRift(Player p) {
		if (p.voidVeil < BeyondTheVeil.FELL) {
			p.getPacketSender().sendMessage("The rift spits violet sparks at you.");
			return;
		}
		p.startAnimation(827);
		p.getPacketSender().sendMessage("You step into the rift. The world turns inside out...");
		later(p, 2, () -> {
			p.getPlayerAssistant().movePlayer(PORTAL_SIDE_X, PORTAL_SIDE_Y, 0);
			p.getPacketSender().sendMessage("...and you stumble out of the portal beneath Skull Rock.");
		});
	}

	/* ====================================================================== */
	/*                         THE HOLLOW KING (the void)                      */
	/* ====================================================================== */

	private static final String[] TAUNTS = { "Your blade is a candle in a hurricane.", "I have eaten brighter lights than you.",
			"Do you feel it? The edges of you, coming loose?", "Again. Amuse me.", "Your world tastes of salt and fear." };

	private static void enterVoid(Player p) {
		if (p.voidVeil < BeyondTheVeil.STARTED || p.voidVeil >= BeyondTheVeil.FELL) {
			return;
		}
		p.voidVeil = BeyondTheVeil.IN_VOID;
		QuestAssistant.sendStages(p);
		p.startAnimation(714);
		p.gfx0(86);
		later(p, 3, () -> {
			p.getPlayerAssistant().movePlayer(ARENA_ARRIVAL_X, ARENA_ARRIVAL_Y, 0);
			later(p, 2, () -> startScene(p));
		});
	}

	/** The confrontation, tick by tick: the king speaks, fights, tires of it, and drops the player to Nythera. */
	private static void startScene(Player p) {
		Npc old;
		while ((old = findSpawned(p, HOLLOW_KING)) != null) {
			old.isDead = true;
			old.needRespawn = false;
			old.absX = 0;
			old.absY = 0;
		}
		NpcHandler.spawnNpc(p, HOLLOW_KING, KING_X, KING_Y, 0, 0, 2500, 3, 240, 600, false, false);
		p.getPacketSender().sendMessage("You are standing on a platform of black stone hanging in nothing.");
		p.getPlayerAssistant().sendCameraShake(1, 4, 1, 4);
		CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
			int tick = 0, finale = -1;

			@Override
			public void execute(CycleEventContainer c) {
				tick++;
				Npc king = findSpawned(p, HOLLOW_KING);
				if (p.disconnected || !inArena(p.absX, p.absY) || p.isDead || king == null) {
					c.stop();
					return;
				}
				if (finale < 0) {
					fight(p, king, tick);
					if (tick >= 55 || tick > 12 && p.playerLevel[Constants.HITPOINTS] <= maxHp(p) * 2 / 5) {
						finale = tick;
					}
					return;
				}
				int t = tick - finale;
				if (t == 0) {
					king.underAttack = false;
					king.killerId = 0;
					kingSays(p, king, "Enough.");
					p.getPlayerAssistant().sendCameraShake(3, 12, 3, 12);
				} else if (t == 3) {
					kingSays(p, king, "I could unmake you with a thought. But I would rather watch you run.");
				} else if (t == 7) {
					kingSays(p, king, "Fall, little light. Fall to Nythera. When you are worth devouring... I will come.");
				} else if (t == 11) {
					p.getPlayerAssistant().sendCameraShake(4, 20, 4, 20);
					p.startAnimation(714);
					p.gfx0(86);
					p.getDialogueHandler().sendStatement("The Hollow King closes his hand. The platform shatters",
							"beneath your feet, and you fall - through the dark, through",
							"the cold, through a sky full of violet stars...");
					p.nextChat = 0;
				} else if (t == 15) {
					c.stop();
					land(p);
				}
			}

			@Override
			public void stop() {
				p.getPlayerAssistant().sendCameraReset();
			}
		}, 1);
	}

	private static void fight(Player p, Npc king, int tick) {
		switch (tick) {
		case 2:
			kingSays(p, king, "So. The little light found my door.");
			return;
		case 6:
			kingSays(p, king, "Three splinters of my seal, bound into a key. Clever. Clever things taste the best.");
			return;
		case 10:
			kingSays(p, king, "Come, then. Show me what your world is made of.");
			p.getPacketSender().sendMessage("The Hollow King rises from his throne!");
			king.underAttack = true;
			king.killerId = p.getId();
			return;
		}
		if (tick > 12 && tick % 8 == 0) {
			// a wave of void: it tears at the player, never quite enough to kill
			kingSays(p, king, TAUNTS[(tick / 8) % TAUNTS.length]); // each taunt once, in turn
			king.animNumber = 2656; // TzTok-Jad's magic attack
			king.animUpdateRequired = true;
			king.updateRequired = true;
			p.gfx0(78);
			p.getPlayerAssistant().sendCameraShake(2, 10, 2, 10);
			hurt(p, Math.max(2, maxHp(p) / 8) + Misc.random(2));
			later(p, 2, () -> p.getPlayerAssistant().sendCameraReset());
		}
		if (tick > 12 && king.HP < king.MaxHP - 25) {
			king.HP = king.MaxHP; // the wounds simply close
			if (Misc.random(2) == 0) {
				p.getPacketSender().sendMessage("Your strike passes through the Hollow King like smoke. The darkness closes behind it.");
			}
		}
	}

	private static void land(Player p) {
		p.getPlayerAssistant().movePlayer(LANDING_X, LANDING_Y, 0);
		// the jump from the void to Nythera is a long one; re-send it so a client still fetching the crater's map retries
		later(p, 1, () -> p.getPlayerAssistant().movePlayer(LANDING_X, LANDING_Y, 0));
		p.voidVeil = BeyondTheVeil.FELL;
		QuestAssistant.sendStages(p);
		p.playerLevel[Constants.HITPOINTS] = Math.max(p.playerLevel[Constants.HITPOINTS], maxHp(p) / 2);
		p.getPlayerAssistant().refreshSkill(Constants.HITPOINTS);
		later(p, 2, () -> {
			p.getDialogueHandler().sendStatement("You crash down into a smoking crater. Above you, a rift",
					"in the sky crackles shut... and stays, a violet scar, at the",
					"crater's heart. Someone is shouting from the rim.");
			p.nextChat = 0;
		});
	}

	/** If a player logs in inside the void (the scene was interrupted), put them back by the portal. */
	public static void onLogin(Player p) {
		if (inArena(p.absX, p.absY)) {
			p.getPlayerAssistant().movePlayer(PORTAL_SIDE_X, PORTAL_SIDE_Y, 0);
			p.getPacketSender().sendMessage("The void spits you back out beside the portal.");
		}
	}

	/* ====================================================================== */
	/*                             ITEMS AND DROPS                            */
	/* ====================================================================== */

	/** First click on an item. */
	public static boolean clickItem(Player p, int itemId) {
		if (itemId != SEERS_LANTERN) {
			return false;
		}
		if (p.voidVeil >= BeyondTheVeil.FELL) {
			dialogue(p, D_LANTERN, -1);
		} else {
			lanternTo(p, PLAZA_X, PLAZA_Y, "The flame shows you a grinning skull of bone... and then you're standing in front of it.");
		}
		return true;
	}

	private static void lanternTo(Player p, int x, int y, String message) {
		p.getPacketSender().sendMessage("You gaze into the lantern's violet flame...");
		p.getPlayerAssistant().startTeleport(x, y, 0, "modern");
		later(p, 5, () -> {
			if (Math.abs(p.absX - x) < 4 && Math.abs(p.absY - y) < 4) {
				p.getPacketSender().sendMessage(message);
			}
		});
	}

	/** Called when an NPC dies. */
	public static void npcKilled(Player killer, int npcType, int x, int y, int height) {
		if (killer == null) {
			return;
		}
		switch (npcType) {
		case HOLLOW_WRAITH:
			if (killer.voidEchoes == EchoesOfTheVoid.SPLINTERS && !killer.getItemAssistant().playerHasItem(GRAVE_SPLINTER)) {
				GameEngine.itemHandler.createGroundItem(killer, GRAVE_SPLINTER, x, y, 1, killer.playerId);
				killer.getPacketSender().sendMessage("The wraith comes apart like wet smoke. Something grey clatters to the ground.");
			}
			break;
		case VOIDLING:
			if (killer.voidEchoes == EchoesOfTheVoid.SPLINTERS && killer.voidlingsLeft > 0) {
				killer.voidlingsLeft--;
				killer.getPacketSender().sendMessage(killer.voidlingsLeft == 0 ? "The vein convulses - one more claws its way out!"
						: "The vein shrieks again, and another voidling squirms free!");
				later(killer, 2, () -> spawnVoidling(killer));
			} else if (killer.voidEchoes == EchoesOfTheVoid.SPLINTERS && aliveSpawned(killer, VOIDLING) == 0) {
				killer.voidVeinCleared = true;
				killer.getPacketSender().sendMessage("The last voidling bursts. The vein falls silent - now's your chance!");
			}
			break;
		}
	}

	/* ====================================================================== */
	/*                                 HELPERS                                */
	/* ====================================================================== */

	static boolean hasPickaxe(Player player) {
		for (int p : PICKAXES) {
			if (player.getItemAssistant().playerHasItem(p) || player.playerEquipment[player.playerWeapon] == p) {
				return true;
			}
		}
		return false;
	}

	/** How many NPCs of this type the player has spawned that are still alive. */
	static int aliveSpawned(Player player, int npcType) {
		int count = 0;
		for (Npc n : NpcHandler.npcs) {
			if (n != null && n.npcType == npcType && n.spawnedBy == player.getId() && !n.isDead) {
				count++;
			}
		}
		return count;
	}

	static Npc findSpawned(Player player, int npcType) {
		for (Npc n : NpcHandler.npcs) {
			if (n != null && n.npcType == npcType && n.spawnedBy == player.getId() && !n.isDead) {
				return n;
			}
		}
		return null;
	}

	/** The Hollow King speaks over his head and in the chatbox, so his words are never missed. */
	static void kingSays(Player p, Npc king, String text) {
		king.forceChat(text);
		p.getPacketSender().sendMessage("@dre@The Hollow King: @bla@" + text);
	}

	static int maxHp(Player player) {
		return player.getPlayerAssistant().getLevelForXP(player.playerXP[Constants.HITPOINTS]);
	}

	/** Hurts the player, but never enough to kill them. */
	static void hurt(Player player, int damage) {
		damage = Math.min(damage, player.playerLevel[Constants.HITPOINTS] - 1);
		if (damage > 0) {
			player.dealDamage(damage);
			player.handleHitMask(damage);
		}
	}

	/** Runs {@code r} after the given number of game ticks. */
	static void later(Player player, int ticks, Runnable r) {
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
				r.run();
			}

			@Override
			public void stop() {
			}
		}, ticks);
	}

	/** Admin test hook (::vt) that fires the same handlers a client click would. */
	public static void debug(Player p, String[] a) {
		int[] n = new int[a.length];
		for (int i = 1; i < a.length; i++) {
			n[i] = Integer.parseInt(a[i]);
		}
		switch (a[0]) {
		case "npc":
			if (!firstClickNpc(p, n[1])) {
				com.rs2.game.content.skullbrine.SkullbrineIsle.firstClickNpc(p, n[1]);
			}
			break;
		case "obj":
			firstClickObject(p, n[1], n[2], n[3]);
			break;
		case "obj2":
			secondClickObject(p, n[1], n[2], n[3]);
			break;
		case "use":
			itemOnObject(p, n[1], n[2], n[3], n[4]);
			break;
		case "item":
			clickItem(p, n[1]);
			break;
		case "journal":
			if (n[1] == 1) {
				EchoesOfTheVoid.showInformation(p);
			} else {
				BeyondTheVeil.showInformation(p);
			}
			break;
		case "list":
			for (Npc npc : NpcHandler.npcs) {
				if (npc != null && npc.spawnedBy == p.getId()) {
					p.getPacketSender().sendMessage(npc.npcType + " @ " + npc.absX + "," + npc.absY + " hp " + npc.HP + " dead "
							+ npc.isDead + " target " + npc.killerId);
				}
			}
			break;
		}
	}
}
