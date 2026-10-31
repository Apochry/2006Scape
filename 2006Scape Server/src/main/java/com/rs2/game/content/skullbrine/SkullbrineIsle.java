package com.rs2.game.content.skullbrine;

import static com.rs2.game.content.skullbrine.SkullbrineConstants.*;

import com.rs2.GameEngine;
import com.rs2.event.CycleEvent;
import com.rs2.event.CycleEventContainer;
import com.rs2.event.CycleEventHandler;
import com.rs2.game.content.quests.QuestAssistant;
import com.rs2.game.content.quests.impl.CurseOfSkullbrine;
import com.rs2.game.content.voidrealm.VoidRealm;
import com.rs2.game.dialogues.ChatEmotes;
import com.rs2.game.dialogues.DialogueHandler;
import com.rs2.game.players.Player;

/**
 * Server-side content for Skullbrine Isle: the boat from Port Sarim, the cave in Skull Rock's mouth,
 * the quest "Curse of Skullbrine" (dialogues, searchables, chest, portal) and Mad Pete's shop.
 *
 * Quest stages are stored in {@link Player#skullbrine}; see {@link CurseOfSkullbrine} for what each means.
 *
 * Hooks into the core server are one-liners that call the static methods here and stop if they return true.
 */
public final class SkullbrineIsle {

	private SkullbrineIsle() {
	}

	/** Shop id in data/cfg/shops.json. */
	public static final int SALVAGE_SHOP = 349;

	/* Dialogue ids (DialogueHandler#sendDialogues) and option actions (Player#dialogueAction). */
	private static final int D_GRIMSBY_START = 9500, D_GRIMSBY_SAIL = 9510, D_GRIMSBY_RETURN = 9515,
			D_PETE = 9520, D_PETE_HINT = 9540, D_PETE_DONE = 9545,
			D_CAPTAIN = 9560, D_CAPTAIN_DONE = 9575;

	private static final int A_GRIMSBY_ACCEPT = 9500, A_GRIMSBY_SAIL = 9501, A_GRIMSBY_RETURN = 9502;

	/* ====================================================================== */
	/*                                NPC CLICKS                              */
	/* ====================================================================== */

	public static boolean firstClickNpc(Player player, int npcType) {
		if (VoidRealm.firstClickNpc(player, npcType)) {
			return true;
		}
		switch (npcType) {
		case BOSUN_GRIMSBY:
			if (onIsland(player.absX, player.absY)) {
				dialogue(player, D_GRIMSBY_RETURN, npcType);
			} else if (player.skullbrine == 0) {
				dialogue(player, D_GRIMSBY_START, npcType);
			} else {
				dialogue(player, D_GRIMSBY_SAIL, npcType);
			}
			return true;
		case MAD_PETE:
			if (player.skullbrine <= 1) {
				dialogue(player, D_PETE, npcType);
			} else if (player.skullbrine >= CurseOfSkullbrine.COMPLETE) {
				dialogue(player, D_PETE_DONE, npcType);
			} else {
				dialogue(player, D_PETE_HINT, npcType);
			}
			return true;
		case CAPTAIN_BLACKBRINE:
			if (player.skullbrine == CurseOfSkullbrine.PEARL_RETURNED) {
				dialogue(player, D_CAPTAIN_DONE + 1, npcType); // thanks + reward (also reachable if the portal scene was interrupted)
			} else {
				dialogue(player, player.skullbrine >= CurseOfSkullbrine.COMPLETE ? D_CAPTAIN_DONE : D_CAPTAIN, npcType);
			}
			return true;
		}
		return false;
	}

	public static boolean secondClickNpc(Player player, int npcType) {
		if (npcType == MAD_PETE) {
			player.getShopAssistant().openShop(SALVAGE_SHOP);
			return true;
		}
		return false;
	}

	/* ====================================================================== */
	/*                               DIALOGUES                                */
	/* ====================================================================== */

	private static void dialogue(Player player, int id, int npcType) {
		player.getDialogueHandler().sendDialogues(id, npcType);
	}

	/** Called first from DialogueHandler#sendDialogues; returns true if the id belongs to Skullbrine. */
	public static boolean sendDialogue(Player player, int id, int npcId) {
		if (VoidRealm.sendDialogue(player, id, npcId)) {
			return true;
		}
		if (id < 9500 || id >= 9600) {
			return false;
		}
		DialogueHandler d = player.getDialogueHandler();
		player.talkingNpc = npcId;
		player.nextChat = 0;
		switch (id) {
		/* ---------------- Bosun Grimsby, Port Sarim: quest start ---------------- */
		case D_GRIMSBY_START:
			d.sendNpcChat(BOSUN_GRIMSBY, ChatEmotes.DISTRESSED, "You there! You look like an adventurer - even a",
					"fresh one will do. I need help, and nobody else will listen.");
			player.nextChat = id + 1;
			break;
		case D_GRIMSBY_START + 1:
			d.sendPlayerChat("What's the matter?");
			player.nextChat = id + 1;
			break;
		case D_GRIMSBY_START + 2:
			d.sendNpcChat(BOSUN_GRIMSBY, ChatEmotes.SAD, "My brother sailed with Captain Blackbrine aboard the",
					"Drowned Maiden. Three moons ago she put in at",
					"Skullbrine Isle, and never came home.");
			player.nextChat = id + 1;
			break;
		case D_GRIMSBY_START + 3:
			d.sendNpcChat(BOSUN_GRIMSBY, ChatEmotes.DISTRESSED, "Fishermen talk of green lights on the water, and dead",
					"men walking the sand. No crew in Port Sarim will",
					"sail there. But I've a boat, if you've the nerve.");
			player.nextChat = id + 1;
			break;
		case D_GRIMSBY_START + 4:
			d.sendOption("I'll find out what happened to the Drowned Maiden.", "Dead men walking? No thank you.");
			player.dialogueAction = A_GRIMSBY_ACCEPT;
			break;
		case D_GRIMSBY_START + 5:
			d.sendPlayerChat("I'll find out what happened to the Drowned Maiden.");
			player.skullbrine = CurseOfSkullbrine.STARTED;
			QuestAssistant.sendStages(player);
			player.nextChat = id + 1;
			break;
		case D_GRIMSBY_START + 6:
			d.sendNpcChat(BOSUN_GRIMSBY, ChatEmotes.HAPPY_JOYFUL, "Bless you! Talk to me again when you're ready and",
					"I'll row you across. Keep your blade close, friend.");
			break;
		case D_GRIMSBY_START + 7:
			d.sendPlayerChat("Dead men walking? No thank you.");
			break;

		/* ---------------- Bosun Grimsby: sailing ---------------- */
		case D_GRIMSBY_SAIL:
			d.sendNpcChat(BOSUN_GRIMSBY, ChatEmotes.CALM_TALK1, "Ready to brave Skullbrine Isle?");
			player.nextChat = id + 1;
			break;
		case D_GRIMSBY_SAIL + 1:
			d.sendOption("Yes, cast off!", "Not just yet.");
			player.dialogueAction = A_GRIMSBY_SAIL;
			break;
		case D_GRIMSBY_RETURN:
			d.sendNpcChat(BOSUN_GRIMSBY, ChatEmotes.DISTRESSED, "Had enough of this cursed rock? I'll take you back to",
					"Port Sarim whenever you like.");
			player.nextChat = id + 1;
			break;
		case D_GRIMSBY_RETURN + 1:
			d.sendOption("Take me back to Port Sarim.", "I'll stay a while longer.");
			player.dialogueAction = A_GRIMSBY_RETURN;
			break;

		/* ---------------- Mad Pete: the story ---------------- */
		case D_PETE:
			d.sendNpcChat(MAD_PETE, ChatEmotes.ANGRY1, "Stay back! Stay BACK! ...Wait. You're warm. You're",
					"breathing. You're not one of them?");
			player.nextChat = id + 1;
			break;
		case D_PETE + 1:
			d.sendPlayerChat("Bosun Grimsby sent me to find the Drowned Maiden.");
			player.nextChat = id + 1;
			break;
		case D_PETE + 2:
			d.sendNpcChat(MAD_PETE, ChatEmotes.SAD, "Then you've found what's left of her. I'm Pete, the",
					"ship's cook. The rest of the crew... they still walk",
					"this island. They just don't breathe any more.");
			player.nextChat = id + 1;
			break;
		case D_PETE + 3:
			d.sendNpcChat(MAD_PETE, ChatEmotes.DISTRESSED, "See that great skull in the rock, up north? The Cap'n",
					"went in through its mouth. Deep inside there's a door",
					"that hums - a door to nowhere. He pulled a pearl out",
					"of it, black as a moonless sea.");
			player.nextChat = id + 1;
			break;
		case D_PETE + 4:
			d.sendNpcChat(MAD_PETE, ChatEmotes.ALMOST_CRYING, "That night the storm drove us onto the beach. Every",
					"man who drowned got back up again. Wrong. Green.",
					"Hungry.");
			player.nextChat = id + 1;
			break;
		case D_PETE + 5:
			d.sendNpcChat(MAD_PETE, ChatEmotes.CALM_TALK2, "The Cap'n kept a log of everything. He went down with",
					"the ship, but his bones washed up by the wreck in",
					"the cove. Find his log - it'll say how to end this.");
			player.nextChat = id + 1;
			break;
		case D_PETE + 6:
			d.sendPlayerChat("I'll search the captain's remains by the wreck.");
			if (player.skullbrine < CurseOfSkullbrine.FIND_LOG) {
				player.skullbrine = CurseOfSkullbrine.FIND_LOG;
				QuestAssistant.sendStages(player);
			}
			player.nextChat = id + 1;
			break;
		case D_PETE + 7:
			d.sendNpcChat(MAD_PETE, ChatEmotes.CALM_TALK1, "Watch yourself. And if you need supplies, I've been",
					"salvaging what I can from the Maiden.");
			break;

		/* ---------------- Mad Pete: hints per stage ---------------- */
		case D_PETE_HINT:
			d.sendNpcChat(MAD_PETE, ChatEmotes.CALM_TALK1, hint(player)[0], hint(player)[1]);
			break;
		case D_PETE_DONE:
			d.sendNpcChat(MAD_PETE, ChatEmotes.HAPPY_JOYFUL, "The lads are finally resting easy, thanks to you. I'll",
					"be leaving this rock on the next tide. Need anything",
					"from the salvage pile before I go?");
			break;

		/* ---------------- Ghost of Captain Blackbrine ---------------- */
		case D_CAPTAIN:
			if (player.skullbrine < CurseOfSkullbrine.RETURN_PEARL) {
				d.sendNpcChat(CAPTAIN_BLACKBRINE, ChatEmotes.DISTRESSED, "The pearl... bring it... back... it must go back...");
			} else {
				d.sendNpcChat(CAPTAIN_BLACKBRINE, ChatEmotes.DISTRESSED, "You have it! Put it back through the door, stranger,",
						"before it calls something else through.");
			}
			break;
		case D_CAPTAIN_DONE:
			d.sendNpcChat(CAPTAIN_BLACKBRINE, ChatEmotes.CALM_TALK2, "My crew sleeps at last. I'll stand watch over this door",
					"a while longer. Whatever lives on the other side",
					"knows we're here now. Be ready when it wakes.");
			break;
		case D_CAPTAIN_DONE + 1:
			// shown once, straight after the pearl is returned
			d.sendNpcChat(CAPTAIN_BLACKBRINE, ChatEmotes.HAPPY_JOYFUL, "It's done... I can feel the curse lifting from my men.",
					"You have my thanks, and what's left of my plunder.");
			player.nextChat = id + 1;
			break;
		case D_CAPTAIN_DONE + 2:
			CurseOfSkullbrine.complete(player);
			break;
		default:
			return false;
		}
		return true;
	}

	private static String[] hint(Player player) {
		switch (player.skullbrine) {
		case CurseOfSkullbrine.FIND_LOG:
			if (player.getItemAssistant().playerHasItem(BLACKBRINES_LOG)) {
				return new String[] { "You found the Cap'n's log! Well, don't just stand", "there - read it!" };
			}
			return new String[] { "The Cap'n's bones washed up beside the wreck in the", "cove, west of the jetty. Search them for his log." };
		case CurseOfSkullbrine.FIND_KEY:
			if (player.getItemAssistant().playerHasItem(CAPTAINS_KEY)) {
				return new String[] { "That's the Cap'n's key! His chest is in the hold", "behind the skull's mouth." };
			}
			return new String[] { "Sam had the key? 'Smiling' Sam was always the worst", "of us. Find him inside Skull Rock - go in by the mouth." };
		case CurseOfSkullbrine.RETURN_PEARL:
			return new String[] { "Don't bring that thing near me! Put it back", "through that humming door, quick!" };
		default:
			return new String[] { "Find the Cap'n's log. It's our only hope.", "" };
		}
	}

	/** Called first from DialogueOptions#handleDialogueOptions. */
	public static boolean handleOption(Player player, int buttonId) {
		if (VoidRealm.handleOption(player, buttonId)) {
			return true;
		}
		boolean first = buttonId == 9157, second = buttonId == 9158;
		if (!first && !second) {
			return false;
		}
		switch (player.dialogueAction) {
		case A_GRIMSBY_ACCEPT:
			player.dialogueAction = -1;
			dialogue(player, first ? D_GRIMSBY_START + 5 : D_GRIMSBY_START + 7, BOSUN_GRIMSBY);
			return true;
		case A_GRIMSBY_SAIL:
			player.dialogueAction = -1;
			if (first) {
				sail(player, DOCK_X, DOCK_Y, "You arrive at Skullbrine Isle. The air smells of salt and rot.");
			} else {
				player.getPacketSender().closeAllWindows();
			}
			return true;
		case A_GRIMSBY_RETURN:
			player.dialogueAction = -1;
			if (first) {
				sail(player, PORT_SARIM_X, PORT_SARIM_Y, "You arrive safely back at Port Sarim.");
			} else {
				player.getPacketSender().closeAllWindows();
			}
			return true;
		}
		return false;
	}

	/* ====================================================================== */
	/*                                  TRAVEL                                */
	/* ====================================================================== */

	/** Ship crossing with the standard sailing interface (route 14 is the long south-western crossing). */
	public static void sail(final Player player, final int x, final int y, final String arrival) {
		final boolean toIsland = onIsland(x, y);
		player.stopPlayerPacket = true;
		player.getPacketSender().closeAllWindows();
		player.getPacketSender().showInterface(3281);
		player.getPacketSender().sendConfig(75, toIsland ? 14 : 15);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				player.getPlayerAssistant().movePlayer(x, y, 0);
				container.stop();
			}

			@Override
			public void stop() {
			}
		}, 9);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				player.stopPlayerPacket = false;
				player.getPacketSender().sendConfig(75, -1);
				player.getPacketSender().closeAllWindows();
				player.getDialogueHandler().sendStatement(arrival);
				player.nextChat = 0;
				container.stop();
			}

			@Override
			public void stop() {
			}
		}, 10);
	}

	/* ====================================================================== */
	/*                                 OBJECTS                                */
	/* ====================================================================== */

	/** Called first from ObjectsActions#firstClickObject. */
	public static boolean firstClickObject(Player player, int objectId, int x, int y) {
		if (VoidRealm.firstClickObject(player, objectId, x, y)) {
			return true;
		}
		if (!onIsland(x, y) && !inDungeon(x, y)) {
			return false;
		}
		switch (objectId) {
		case CAVE_ENTRANCE:
			player.startAnimation(827);
			player.getPacketSender().sendMessage("You step between the teeth and into the skull's mouth...");
			player.getPlayerAssistant().movePlayer(DUNGEON_ARRIVAL_X, DUNGEON_ARRIVAL_Y, 0);
			return true;
		case CAVE_EXIT:
			player.getPlayerAssistant().movePlayer(CAVE_OUTSIDE_X, CAVE_OUTSIDE_Y, 0);
			player.getPacketSender().sendMessage("You climb back out into the grey daylight.");
			return true;
		case CAPTAINS_SKELETON:
			searchSkeleton(player);
			return true;
		case CAPTAINS_CHEST:
			if (inDungeon(x, y)) {
				openCaptainsChest(player);
			} else {
				player.getPacketSender().sendMessage("The chest has been prised open and emptied. Someone got here first.");
			}
			return true;
		case VOID_PORTAL:
			touchPortal(player);
			return true;
		}
		return false;
	}

	/** Called first from UseItem#itemOnObject. */
	public static boolean itemOnObject(Player player, int objectId, int x, int y, int itemId) {
		if (VoidRealm.itemOnObject(player, objectId, x, y, itemId)) {
			return true;
		}
		if (objectId == VOID_PORTAL && itemId == VOID_PEARL && inDungeon(x, y)) {
			touchPortal(player);
			return true;
		}
		if (objectId == CAPTAINS_CHEST && itemId == CAPTAINS_KEY && inDungeon(x, y)) {
			openCaptainsChest(player);
			return true;
		}
		return false;
	}

	private static void searchSkeleton(Player player) {
		player.startAnimation(827);
		if (player.skullbrine < CurseOfSkullbrine.FIND_LOG) {
			player.getDialogueHandler().sendStatement("The bones are tangled in seaweed. A rotted coat still",
					"clings to them - a captain's coat.");
			return;
		}
		if (player.skullbrine > CurseOfSkullbrine.FIND_LOG || player.getItemAssistant().playerHasItem(BLACKBRINES_LOG)) {
			player.getPacketSender().sendMessage("You find nothing else of interest.");
			return;
		}
		if (player.getItemAssistant().freeSlots() < 1) {
			player.getPacketSender().sendMessage("You need a free inventory space.");
			return;
		}
		player.getItemAssistant().addItem(BLACKBRINES_LOG, 1);
		player.getDialogueHandler().sendItemChat(BLACKBRINES_LOG, 200, "Blackbrine's log",
				"Inside the captain's coat you find a water-stained", "log book, sealed with tar.");
	}

	private static void openCaptainsChest(Player player) {
		if (player.skullbrine >= CurseOfSkullbrine.RETURN_PEARL) {
			player.getPacketSender().sendMessage("The chest is empty.");
			return;
		}
		if (!player.getItemAssistant().playerHasItem(CAPTAINS_KEY)) {
			player.getPacketSender().sendMessage("The chest is locked tight. There's a heavy iron lock on it.");
			return;
		}
		if (player.getItemAssistant().freeSlots() < 1) {
			player.getPacketSender().sendMessage("You need a free inventory space.");
			return;
		}
		player.startAnimation(832);
		player.getItemAssistant().deleteItem(CAPTAINS_KEY, 1);
		player.getItemAssistant().addItem(VOID_PEARL, 1);
		player.skullbrine = CurseOfSkullbrine.RETURN_PEARL;
		QuestAssistant.sendStages(player);
		player.getDialogueHandler().sendItemChat(VOID_PEARL, 200, "Void pearl",
				"The key turns with a groan. Beneath a pile of rotten",
				"silks lies a pearl so dark it seems to swallow the light.", "It hums against your palm.");
	}

	private static void touchPortal(Player player) {
		if (player.skullbrine == CurseOfSkullbrine.RETURN_PEARL && player.getItemAssistant().playerHasItem(VOID_PEARL)) {
			player.getItemAssistant().deleteItem(VOID_PEARL, 1);
			player.startAnimation(827);
			player.gfx0(86);
			player.getDialogueHandler().sendStatement("You press the Void pearl into the swirling portal. It flares",
					"violet - for a heartbeat you glimpse a sky of strange stars",
					"beyond - then the light dims to a low, patient hum.");
			player.skullbrine = CurseOfSkullbrine.PEARL_RETURNED;
			player.talkingNpc = CAPTAIN_BLACKBRINE;
			player.nextChat = D_CAPTAIN_DONE + 1;
			return;
		}
		if (player.skullbrine == CurseOfSkullbrine.PEARL_RETURNED) {
			dialogue(player, D_CAPTAIN_DONE + 1, CAPTAIN_BLACKBRINE);
			return;
		}
		enterVoidPortal(player);
	}

	/** The dormant portal before the Void series (see VoidRealm, which handles it once Skullbrine is complete). */
	public static void enterVoidPortal(Player player) {
		if (player.skullbrine >= CurseOfSkullbrine.COMPLETE) {
			player.getPacketSender().sendMessage("The portal hums with a low, steady thrum. Something on the other side is waiting...");
			player.getPacketSender().sendMessage("...but the way is not yet open.");
		} else {
			player.getPacketSender().sendMessage("Cold air pours from the portal. You feel something on the other side looking back at you.");
		}
	}

	/* ====================================================================== */
	/*                                  ITEMS                                 */
	/* ====================================================================== */

	/** Called first from ClickItem#processPacket. */
	public static boolean clickItem(Player player, int itemId) {
		if (VoidRealm.clickItem(player, itemId)) {
			return true;
		}
		if (itemId != BLACKBRINES_LOG) {
			return false;
		}
		CurseOfSkullbrine.readLog(player);
		return true;
	}

	/* ====================================================================== */
	/*                                  DROPS                                 */
	/* ====================================================================== */

	/** Called when an NPC dies; drops the captain's key from Sam while the player needs it. */
	public static void npcKilled(Player killer, int npcType, int x, int y, int height) {
		VoidRealm.npcKilled(killer, npcType, x, y, height);
		if (npcType != CURSED_FIRST_MATE || killer == null) {
			return;
		}
		if (killer.skullbrine == CurseOfSkullbrine.FIND_KEY && !killer.getItemAssistant().playerHasItem(CAPTAINS_KEY)) {
			GameEngine.itemHandler.createGroundItem(killer, CAPTAINS_KEY, x, y, 1, killer.playerId);
			killer.getPacketSender().sendMessage("Something heavy falls from around Sam's neck as he crumbles.");
		}
	}
}
