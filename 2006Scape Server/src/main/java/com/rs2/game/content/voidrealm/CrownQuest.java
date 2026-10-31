package com.rs2.game.content.voidrealm;

import static com.rs2.game.content.quests.impl.AshesOfTheCrown.*;
import static com.rs2.game.content.voidrealm.VoidConstants.*;
import static com.rs2.game.content.voidrealm.VoidRealm.*;

import com.rs2.event.CycleEvent;
import com.rs2.event.CycleEventContainer;
import com.rs2.event.CycleEventHandler;
import com.rs2.game.content.quests.QuestAssistant;
import com.rs2.game.content.quests.impl.AshesOfTheCrown;
import com.rs2.game.dialogues.ChatEmotes;
import com.rs2.game.npcs.Npc;
import com.rs2.game.npcs.NpcHandler;
import com.rs2.game.players.Player;
import com.rs2.util.Misc;
import com.rs2.world.clip.Region;

/**
 * Server-side content for "Ashes of the Crown": Ysolde and Maelis, the three chronicle pages, the Vault beneath the
 * Gate (the ward puzzle, the seal, the Herald, the Ember), and the waystone network it opens.
 *
 * Called from {@link VoidRealm}'s hooks before anything else. Dialogue ids 9900-9999.
 */
final class CrownQuest {

	private CrownQuest() {
	}

	private static final int D_YSOLDE = 9900, D_YSOLDE_GO = 9906, D_YSOLDE_REMIND = 9907, D_MAELIS = 9910, D_MAELIS_PAGES = 9920,
			D_MAELIS_BIND = 9921, D_MAELIS_VAULT = 9937, D_MAELIS_EMBER = 9931, D_MAELIS_AFTER = 9938, D_NETTLE = 9950,
			D_NETTLE_PAID = 9954, D_NETTLE_SHARK = 9955, D_NETTLE_POOR = 9956, D_NETTLE_NOSHARK = 9957, D_WAYSTONE_DEAD = 9960,
			D_READ = 9970, D_BOOKS = 9980;
	private static final int A_YSOLDE = 9900, A_NETTLE = 9901, A_WAYSTONE = 9902;

	private static final int COINS = 995, RAW_SHARK = 383, NETTLE_PRICE = 3000;
	private static final int OPTION_2 = 9157, OPTION_3 = 9167, OPTION_4 = 9178, OPTION_5 = 9190;

	/* ====================================================================== */
	/*                                  PEOPLE                                */
	/* ====================================================================== */

	static boolean firstClickNpc(Player p, int npcType) {
		switch (npcType) {
		case YSOLDE:
			if (p.voidCrown == NOT_STARTED && canStart(p)) {
				dialogue(p, D_YSOLDE, npcType);
				return true;
			}
			if (p.voidCrown == STARTED) {
				dialogue(p, D_YSOLDE_REMIND, npcType);
				return true;
			}
			return false;
		case MAELIS:
			int stage = p.voidCrown;
			if (stage == NOT_STARTED) {
				dialogue(p, D_MAELIS_AFTER + 2, npcType); // too busy for strangers
			} else if (stage == STARTED) {
				dialogue(p, D_MAELIS, npcType);
			} else if (stage == PAGES) {
				dialogue(p, hasAllPages(p) ? D_MAELIS_BIND : D_MAELIS_PAGES, npcType);
			} else if (stage < HAS_EMBER) {
				dialogue(p, D_MAELIS_VAULT, npcType);
			} else if (stage == HAS_EMBER) {
				dialogue(p, D_MAELIS_EMBER, npcType);
			} else {
				dialogue(p, D_MAELIS_AFTER, npcType);
			}
			return true;
		case NETTLE:
			if (p.voidCrown == PAGES && !p.getItemAssistant().playerHasItem(NETTLE_PAGE)) {
				dialogue(p, D_NETTLE, npcType);
				return true;
			}
			return false;
		}
		return false;
	}

	private static boolean hasAllPages(Player p) {
		return p.getItemAssistant().playerHasItem(THANE_PAGE) && p.getItemAssistant().playerHasItem(SILK_PAGE)
				&& p.getItemAssistant().playerHasItem(NETTLE_PAGE);
	}

	static boolean sendDialogue(Player p, int id, int npcId) {
		if (id < 9900 || id >= 10000) {
			return false;
		}
		p.talkingNpc = npcId;
		p.nextChat = 0;
		switch (id) {
		/* ---------------- Captain Ysolde: something crowned in the Scar ---------------- */
		case D_YSOLDE:
			npc(p, id, YSOLDE, ChatEmotes.CALM_TALK1, false, "You. Good. I've been waiting for you to come back from",
					"the walls.");
			break;
		case D_YSOLDE + 1:
			me(p, id, false, "Kael told me what her watch saw. The crowned thing.");
			break;
		case D_YSOLDE + 2:
			npc(p, id, YSOLDE, ChatEmotes.CALM_TALK2, false, "It's been back twice since. Tall. Crowned. It stands at the",
					"edge of the Scar and looks at Gatehold for an hour, then it goes back in.");
			break;
		case D_YSOLDE + 3:
			npc(p, id, YSOLDE, ChatEmotes.CALM_TALK1, false, "You've met the thing on the other side of that rift. Is this",
					"its doing? I need to know what I'm up against, and I've nobody who reads old books.");
			break;
		case D_YSOLDE + 4:
			npc(p, id, YSOLDE, ChatEmotes.CALM_TALK2, false, "Except Maelis. Old archivist, camps in the stone circle up the",
					"hill. Mad as a box of frogs. But if anyone knows what wears a crown in the Scar, he does.");
			break;
		case D_YSOLDE + 5:
			p.getDialogueHandler().sendOption("I'll find Maelis.", "Not right now.");
			p.dialogueAction = A_YSOLDE;
			break;
		case D_YSOLDE_GO:
			npc(p, id, YSOLDE, ChatEmotes.HAPPY_JOYFUL, true, "Go carefully. And come back - I'm short on people who come back.");
			break;
		case D_YSOLDE_REMIND:
			npc(p, id, YSOLDE, ChatEmotes.CALM_TALK1, true, "Maelis is in the stone circle, up the hill north of the square.",
					"Follow the smoke and the muttering.");
			break;

		/* ---------------- Archivist Maelis ---------------- */
		case D_MAELIS:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK1, false, "Mind the stones! They're older than the kingdom. Older than",
					"kingdoms.");
			break;
		case D_MAELIS + 1:
			me(p, id, false, "Captain Ysolde sent me. Something crowned is walking in the Scar.");
			break;
		case D_MAELIS + 2:
			npc(p, id, MAELIS, ChatEmotes.HAPPY_JOYFUL, false, "Crowned. Crowned! ...Of course it is. Sit. No - not there,",
					"that's Volume Nine.");
			break;
		case D_MAELIS + 3:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK2, false, "Nythera had kings, once. The last was Aurel. That ruin down the",
					"hill was the gate of his city. Then a plague came, and Aurel went looking for a cure in places a",
					"king ought not to look.");
			break;
		case D_MAELIS + 4:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK1, false, "And there the chronicles stop. The last Royal Chronicle was torn",
					"apart when the city fell. I've spent thirty years finding out where its pages went.");
			break;
		case D_MAELIS + 5:
			me(p, id, false, "And?");
			break;
		case D_MAELIS + 6:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK2, false, "Three pages. Aurel's champion, the Thane, was buried with one in",
					"his coffer - in the Sunken Barrow, west of here. He won't give it up while he's still walking about.");
			break;
		case D_MAELIS + 7:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK1, false, "One went into Silkwood. The spiders wrap up anything that",
					"glitters. There's a cocoon there with a page in it - and a broodmother who'll take it personally.");
			break;
		case D_MAELIS + 8:
			npc(p, id, MAELIS, ChatEmotes.ANGRY1, false, "And the last is with that smuggler in the cove. Nettle. He",
					"knows what it is, and he won't sell it to me. He might sell it to you.");
			break;
		case D_MAELIS + 9:
			npc(p, id, MAELIS, ChatEmotes.HAPPY_JOYFUL, true, "Bring me all three and I'll bind them. Then we'll know what",
					"is walking in the Scar.");
			if (p.voidCrown == STARTED) {
				p.voidCrown = PAGES;
				QuestAssistant.sendStages(p);
			}
			break;
		case D_MAELIS_PAGES:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK1, true, missingPages(p));
			break;
		case D_MAELIS_BIND:
			npc(p, id, MAELIS, ChatEmotes.HAPPY_JOYFUL, false, "All three! Give them here - carefully, carefully...");
			break;
		case D_MAELIS_BIND + 1:
			if (!hasAllPages(p)) {
				dialogue(p, D_MAELIS_PAGES, MAELIS);
				return true;
			}
			p.getItemAssistant().deleteItem(THANE_PAGE, 1);
			p.getItemAssistant().deleteItem(SILK_PAGE, 1);
			p.getItemAssistant().deleteItem(NETTLE_PAGE, 1);
			p.getItemAssistant().addOrDropItem(ROYAL_CHRONICLE, 1);
			p.voidCrown = CHRONICLE;
			QuestAssistant.sendStages(p);
			p.getDialogueHandler().sendStatement(wide("Maelis stitches the three pages into a cracked leather spine with",
					"quick, shaking fingers, and hands you the Royal chronicle."));
			p.nextChat = id + 1;
			break;
		case D_MAELIS_BIND + 2:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK2, false, "There. Listen. 'In the ninth year of the plague...'");
			break;
		case D_MAELIS_BIND + 3:
		case D_MAELIS_BIND + 4:
		case D_MAELIS_BIND + 5:
			chronicle(p, id, id - (D_MAELIS_BIND + 3), false);
			break;
		case D_MAELIS_BIND + 6:
			npc(p, id, MAELIS, ChatEmotes.SAD, false, "The Hollow King is Aurel. Was Aurel. Whatever wears his crown",
					"now, his heart is still down there. The Ember.");
			break;
		case D_MAELIS_BIND + 7:
			me(p, id, false, "And the crowned thing walking in the Scar?");
			break;
		case D_MAELIS_BIND + 8:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK1, false, "His Herald, I'd guess - hunting for the way into the Vault.",
					"There's a hatch in the Gate's rubble. Get there first.");
			break;
		case D_MAELIS_BIND + 9:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK2, true, "Light the wards in the chronicle's order, and take the Ember",
					"before he does. A thing with a heart can be killed.");
			break;
		case D_MAELIS_VAULT:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK1, true, "The hatch is in the rubble of the Gate. Sun, then Star, then",
					"Moon. Read the chronicle if you forget. And don't let the Herald have the Ember!");
			break;
		case D_MAELIS_EMBER:
			npc(p, id, MAELIS, ChatEmotes.DISTRESSED, false, "Is that - give it here - no. Don't give it to me. Hold it.",
					"It's... beating.");
			break;
		case D_MAELIS_EMBER + 1:
			p.getDialogueHandler().sendStatement(wide("The Ember flares. The camp is gone: you are standing on the",
					"black platform again, and the Hollow King is looking straight at you."));
			p.gfx0(86);
			p.nextChat = id + 1;
			break;
		case D_MAELIS_EMBER + 2:
			p.getDialogueHandler().sendStatement(wide("\"THIEF,\" says a voice that is everywhere. \"You hold something of",
					"mine. Keep it warm for me. I am coming to collect it - and the world it beats in.\""));
			p.nextChat = id + 1;
			break;
		case D_MAELIS_EMBER + 3:
			npc(p, id, MAELIS, ChatEmotes.DISTRESSED, false, "...He spoke. Through it. He can see us through it.");
			break;
		case D_MAELIS_EMBER + 4:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK2, false, "Then we'll be ready when he comes. The waystones answer to anyone",
					"who has held a king's heart - the old books all agree on that. Touch one and it will remember",
					"you. Then you can travel between them.");
			break;
		case D_MAELIS_EMBER + 5:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK1, true, "Take this to Ysolde, too - tell her we have a king's heart, and",
					"a king coming for it. Keep the Ember close. Keep it warm.");
			AshesOfTheCrown.complete(p);
			break;
		case D_MAELIS_AFTER:
			npc(p, id, MAELIS, ChatEmotes.HAPPY_JOYFUL, false, "The waystones! Touch every one you find. I'm writing all of",
					"this down, you know. Volume Ten.");
			break;
		case D_MAELIS_AFTER + 1:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK1, true, "Keep the Ember warm. Whatever he's planning, he'll need it back.");
			break;
		case D_MAELIS_AFTER + 2:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK1, true, "Not now, not now - I'm at a very delicate footnote.");
			break;

		/* ---------------- Sly Nettle: the third page ---------------- */
		case D_NETTLE:
			me(p, id, false, "I'm told you have a page of the Royal Chronicle.");
			break;
		case D_NETTLE + 1:
			npc(p, id, NETTLE, ChatEmotes.ANGRY1, false, "Told by Maelis, I'll bet. That old goat offered me forty coins",
					"for it. Forty! It's history! It's priceless!");
			break;
		case D_NETTLE + 2:
			npc(p, id, NETTLE, ChatEmotes.CALM_TALK1, false, "...It's three thousand coins. Or - and honestly I'd rather this",
					"- a raw shark. I haven't had shark stew since I left home.");
			break;
		case D_NETTLE + 3:
			p.getDialogueHandler().sendOption("Pay 3,000 coins.", "Give him a raw shark.", "I'll think about it.");
			p.dialogueAction = A_NETTLE;
			break;
		case D_NETTLE_PAID:
			npc(p, id, NETTLE, ChatEmotes.HAPPY_JOYFUL, true, "Pleasure doing business. Don't tell Maelis what you paid.");
			break;
		case D_NETTLE_SHARK:
			npc(p, id, NETTLE, ChatEmotes.HAPPY_JOYFUL, true, "Oh, you beauty. You BEAUTY. Here's your page. Take two - no,",
					"there's only one. Take it anyway.");
			break;
		case D_NETTLE_POOR:
			npc(p, id, NETTLE, ChatEmotes.CALM_TALK2, true, "No coin, no page. History's expensive.");
			break;
		case D_NETTLE_NOSHARK:
			npc(p, id, NETTLE, ChatEmotes.CALM_TALK2, true, "That's not a shark. That's the absence of a shark. The cove's",
					"full of them - bring a harpoon.");
			break;

		/* ---------------- objects that talk back ---------------- */
		case D_WAYSTONE_DEAD:
			p.getDialogueHandler().sendStatement(wide("The rune flickers under your hand, but it doesn't answer.",
					"You don't know how to ask it yet."));
			break;
		case D_READ:
		case D_READ + 1:
		case D_READ + 2:
			chronicle(p, id, id - D_READ, id == D_READ + 2);
			break;
		case D_BOOKS:
			p.getDialogueHandler().sendStatement(wide("'A Short History of Nythera', in fourteen volumes. 'Waystones and",
					"Their Keeping.' 'On the Proper Care of Kings (Annotated).' Most are about the Gate."));
			break;
		default:
			return false;
		}
		return true;
	}

	/** One of the chronicle's three passages - read aloud by Maelis, or by you from the book. */
	private static void chronicle(Player p, int id, int part, boolean last) {
		String[][] text = {
				{ "\"In the ninth year of the plague, King Aurel went down beneath his own", "gate, to the Vault where the old wards burned,",
						"and spoke with the dark between the stars.\"" },
				{ "\"It offered him a crown that could not die. He took it. His champion, the", "Thane, cut the heart from him on the Vault's steps",
						"and laid it on the pedestal, burning still, behind the wards.\"" },
				{ "\"First the SUN that crowned him; then the STAR that guided him; last", "the MOON that mourned him. So the wards were",
						"lit, and so they must be lit to open the way again.\"" } };
		p.getDialogueHandler().sendStatement(wide(text[part]));
		p.nextChat = last ? 0 : id + 1;
	}

	private static String[] missingPages(Player p) {
		StringBuilder s = new StringBuilder("Still missing:");
		if (!p.getItemAssistant().playerHasItem(THANE_PAGE)) {
			s.append(" the Thane's page, in the Sunken Barrow;");
		}
		if (!p.getItemAssistant().playerHasItem(SILK_PAGE)) {
			s.append(" the page in the Silkwood cocoon;");
		}
		if (!p.getItemAssistant().playerHasItem(NETTLE_PAGE)) {
			s.append(" Nettle's page, in the cove;");
		}
		String out = s.toString();
		return new String[] { out.substring(0, out.length() - 1) + "." };
	}

	/* ====================================================================== */
	/*                                 OPTIONS                                */
	/* ====================================================================== */

	static boolean handleOption(Player p, int button) {
		switch (p.dialogueAction) {
		case A_YSOLDE:
			if (button != OPTION_2 && button != OPTION_2 + 1) {
				return false;
			}
			p.dialogueAction = -1;
			if (button == OPTION_2) {
				if (p.voidCrown == NOT_STARTED) {
					p.voidCrown = STARTED;
					QuestAssistant.sendStages(p);
				}
				dialogue(p, D_YSOLDE_GO, YSOLDE);
			} else {
				p.getPacketSender().closeAllWindows();
			}
			return true;
		case A_NETTLE:
			if (button < OPTION_3 || button > OPTION_3 + 2) {
				return false;
			}
			p.dialogueAction = -1;
			if (button == OPTION_3) {
				if (p.getItemAssistant().playerHasItem(COINS, NETTLE_PRICE)) {
					p.getItemAssistant().deleteItem(COINS, NETTLE_PRICE);
					p.getItemAssistant().addOrDropItem(NETTLE_PAGE, 1);
					dialogue(p, D_NETTLE_PAID, NETTLE);
				} else {
					dialogue(p, D_NETTLE_POOR, NETTLE);
				}
			} else if (button == OPTION_3 + 1) {
				if (p.getItemAssistant().playerHasItem(RAW_SHARK)) {
					p.getItemAssistant().deleteItem(RAW_SHARK, 1);
					p.getItemAssistant().addOrDropItem(NETTLE_PAGE, 1);
					dialogue(p, D_NETTLE_SHARK, NETTLE);
				} else {
					dialogue(p, D_NETTLE_NOSHARK, NETTLE);
				}
			} else {
				p.getPacketSender().closeAllWindows();
			}
			return true;
		case A_WAYSTONE:
			int base = p.wayChoices == null ? 0 : p.wayChoices.length == 2 ? OPTION_2 : p.wayChoices.length == 3 ? OPTION_3
					: p.wayChoices.length == 4 ? OPTION_4 : OPTION_5;
			int pick = button - base;
			if (p.wayChoices == null || pick < 0 || pick >= p.wayChoices.length) {
				return false;
			}
			p.dialogueAction = -1;
			p.getPacketSender().closeAllWindows();
			int to = p.wayChoices[pick];
			p.wayChoices = null;
			if (to >= 0) {
				travel(p, to);
			}
			return true;
		}
		return false;
	}

	/* ====================================================================== */
	/*                                 OBJECTS                                */
	/* ====================================================================== */

	static boolean firstClickObject(Player p, int objectId, int x, int y) {
		switch (objectId) {
		case THANE_COFFER:
			openCoffer(p);
			return true;
		case SILK_COCOON:
			cutCocoon(p, x, y);
			return true;
		case VAULT_HATCH:
			if (p.voidCrown < CHRONICLE) {
				p.getPacketSender().sendMessage("The hatch is rusted into the rubble. Whatever's down there, nobody's been looking for it.");
				return true;
			}
			p.startAnimation(827);
			p.getPacketSender().sendMessage("You wrench the hatch open and climb down into the cold.");
			later(p, 2, () -> {
				p.getPlayerAssistant().movePlayer(dungeonBaseX(VAULT_REGION_X) + 32, DUNGEON_BASE_Y + 5, 0);
				later(p, 2, () -> showWards(p));
			});
			return true;
		case SUN_WARD:
		case STAR_WARD:
		case MOON_WARD:
			lightWard(p, objectId == SUN_WARD ? 0 : objectId == STAR_WARD ? 1 : 2);
			return true;
		case VAULT_SEAL:
			touchSeal(p);
			return true;
		case EMBER_PEDESTAL:
			takeEmber(p);
			return true;
		case WAYSTONE:
			touchWaystone(p, x, y);
			return true;
		case MAELIS_BOOKS:
			dialogue(p, D_BOOKS, -1);
			return true;
		}
		return false;
	}

	/** The Thane's coffer is guarded by his oathsworn - unless the Thane himself has already been put down. */
	private static void openCoffer(Player p) {
		if (p.voidCrown != PAGES || p.getItemAssistant().playerHasItem(THANE_PAGE)) {
			p.getPacketSender().sendMessage("The coffer holds a mouldering crown-cloth and nothing else.");
			return;
		}
		if (VoidRealm.findSpawned(p, THANE_OATHSWORN) != null) {
			p.getPacketSender().sendMessage("Not with the oathsworn standing over you!");
			return;
		}
		if ((p.nytheraBosses & 1) == 0 && (p.crownFlags & OATHSWORN_SLAIN) == 0) {
			p.getPacketSender().sendMessage("As you touch the lid, something rises out of the floor behind you, still sworn to guard it.");
			NpcHandler.spawnNpc(p, THANE_OATHSWORN, p.absX - 1, p.absY - 1, 0, 1, 55, 5, 40, 35, true, false);
			Npc guard = VoidRealm.findSpawned(p, THANE_OATHSWORN);
			if (guard != null) {
				guard.forceChat("The Thane's things are the Thane's!");
			}
			return;
		}
		p.startAnimation(832);
		p.getItemAssistant().addOrDropItem(THANE_PAGE, 1);
		p.getPacketSender().sendMessage("The lid lifts at last. Under the crown-cloth lies a page of the chronicle.");
	}

	/** Cutting the cocoon wakes its guardian; once she's dead, the page is yours. */
	private static void cutCocoon(Player p, int x, int y) {
		if (p.voidCrown != PAGES || p.getItemAssistant().playerHasItem(SILK_PAGE)) {
			p.getPacketSender().sendMessage("There's nothing left in the cocoon but silk and old bones.");
			return;
		}
		p.startAnimation(451);
		if ((p.crownFlags & BROODMOTHER_SLAIN) != 0) {
			p.getItemAssistant().addOrDropItem(SILK_PAGE, 1);
			p.getPacketSender().sendMessage("You slit the cocoon open. Inside, sticky with silk, is a page of the chronicle.");
			return;
		}
		if (VoidRealm.findSpawned(p, BROODMOTHER) != null) {
			p.getPacketSender().sendMessage("Not while she's watching you!");
			return;
		}
		p.getPacketSender().sendMessage("You start cutting - and something huge drops out of the branches above!");
		NpcHandler.spawnNpc(p, BROODMOTHER, p.absX + 1, p.absY + 1, 0, 1, 70, 6, 45, 40, true, false);
		Npc mother = VoidRealm.findSpawned(p, BROODMOTHER);
		if (mother != null) {
			mother.forceChat("*HISSSSS*");
		}
	}

	/* ---------------- the Vault ---------------- */

	private static final int[] WARD_IDS = { SUN_WARD, STAR_WARD, MOON_WARD };
	private static final int[] LIT_IDS = { SUN_WARD_LIT, STAR_WARD_LIT, MOON_WARD_LIT };
	private static final String[] WARD_NAMES = { "Sun", "Star", "Moon" };

	private static int vx(int lx) {
		return dungeonBaseX(VAULT_REGION_X) + lx;
	}

	private static int vy(int ly) {
		return DUNGEON_BASE_Y + ly;
	}

	/** Shows this player the wards they've lit (all of them once the seal is open). */
	static void showWards(Player p) {
		int lit = p.voidCrown >= WARDS_LIT ? 3 : p.wardsLit;
		for (int i = 0; i < 3; i++) {
			p.getPacketSender().object(i < lit ? LIT_IDS[i] : WARD_IDS[i], vx(WARD_SPOTS[i][0]), vy(WARD_SPOTS[i][1]), 0, 10);
		}
	}

	private static void lightWard(Player p, int ward) {
		if (p.voidCrown < CHRONICLE) {
			p.getPacketSender().sendMessage("Cold iron. You've no idea what it's for.");
			return;
		}
		if (p.voidCrown >= WARDS_LIT || ward < p.wardsLit) {
			p.getPacketSender().sendMessage("The " + WARD_NAMES[ward] + " ward is already burning.");
			return;
		}
		p.startAnimation(733);
		if (ward == p.wardsLit) {
			p.wardsLit++;
			showWards(p);
			p.getPacketSender().sendMessage("The " + WARD_NAMES[ward] + " ward catches, and burns with a clean "
					+ (ward == 0 ? "golden" : ward == 1 ? "blue-white" : "silver") + " flame.");
			if (p.wardsLit == 3) {
				p.voidCrown = WARDS_LIT;
				QuestAssistant.sendStages(p);
				p.getPacketSender().sendMessage("Somewhere to the north, glass cracks. The way to the Ember is open.");
			}
			return;
		}
		// the wrong ward: the dark answers instead
		p.wardsLit = 0;
		showWards(p);
		p.getPacketSender().sendMessage("The " + WARD_NAMES[ward] + " ward flares black - and every flame in the hall goes out.");
		p.getPacketSender().sendMessage("Shapes peel away from the walls...");
		spawnShades(p, 3);
	}

	private static void touchSeal(Player p) {
		boolean south = p.absY < vy(SEAL_LY);
		if (p.voidCrown < WARDS_LIT) {
			p.getPacketSender().sendMessage("Void glass, cold and solid. Behind you, the wards are dark.");
			return;
		}
		p.getPlayerAssistant().movePlayer(vx(SEAL_LX), vy(SEAL_LY + (south ? 2 : -2)), 0);
		p.getPacketSender().sendMessage(south ? "The glass parts around you like cold water." : "You step back through the glass.");
		if (south && p.voidCrown == WARDS_LIT) {
			later(p, 2, () -> summonHerald(p));
		}
	}

	private static void takeEmber(Player p) {
		if (p.voidCrown >= HAS_EMBER || p.getItemAssistant().playerHasItem(EMBER)) {
			p.getPacketSender().sendMessage("The pedestal is cold now.");
			return;
		}
		if (p.voidCrown < HERALD_SLAIN) {
			p.getPacketSender().sendMessage("The Herald stands between you and the Ember.");
			if (p.voidCrown == WARDS_LIT && VoidRealm.findSpawned(p, HOLLOW_HERALD) == null) {
				summonHerald(p);
			}
			return;
		}
		p.startAnimation(832);
		p.getItemAssistant().addOrDropItem(EMBER, 1);
		p.voidCrown = HAS_EMBER;
		QuestAssistant.sendStages(p);
		p.gfx0(86);
		p.getPacketSender().sendMessage("You lift the Ember. It's warm, and heavier than it looks, and it is beating.");
		p.getPacketSender().sendMessage("Maelis will want to see this.");
	}

	/* ---------------- the Hollow Herald ---------------- */

	private static final String[] HERALD_LINES = { "The King has sent me for what is his.", "You stand in a dead man's house.",
			"Kneel. KNEEL.", "He remembers you. He remembers everyone." };

	private static void summonHerald(Player p) {
		if (VoidRealm.findSpawned(p, HOLLOW_HERALD) != null || p.voidCrown != WARDS_LIT) {
			return;
		}
		NpcHandler.spawnNpc(p, HOLLOW_HERALD, vx(HERALD_LX), vy(HERALD_LY), 0, 1, 160, 8, 70, 60, true, false);
		Npc herald = VoidRealm.findSpawned(p, HOLLOW_HERALD);
		if (herald == null) {
			return;
		}
		herald.forceChat("Little thief. The King said you would come.");
		p.getPacketSender().sendMessage("The Hollow Herald unfolds from the dark around the pedestal.");
		CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
			int tick, phase, shieldHp = -1, shieldTicks;

			@Override
			public void execute(CycleEventContainer c) {
				tick++;
				if (herald.isDead || herald.npcType != HOLLOW_HERALD || p.disconnected || Math.abs(p.absX - herald.absX) > 25
						|| Math.abs(p.absY - herald.absY) > 25) {
					if (!herald.isDead && herald.npcType == HOLLOW_HERALD) {
						banish(herald); // the player left (or fell): it goes back into the dark, and so do its shades
						Npc shade;
						while ((shade = VoidRealm.findSpawned(p, HOLLOW_SHADE)) != null) {
							banish(shade);
						}
					}
					c.stop();
					return;
				}
				// at two thirds and one third it wraps itself in void until its shades are dealt with
				int next = phase == 0 ? herald.MaxHP * 2 / 3 : phase == 1 ? herald.MaxHP / 3 : -1;
				if (shieldHp < 0 && next > 0 && herald.HP <= next) {
					phase++;
					shieldHp = herald.HP;
					shieldTicks = 0;
					herald.forceChat(phase == 1 ? "Shades! Shield your King's voice!" : "You will NOT have it!");
					p.getPacketSender().sendMessage("The Herald wraps itself in void. Its shades must fall before it can be hurt!");
					spawnShades(p, 3);
				}
				if (shieldHp >= 0) {
					if (herald.HP < shieldHp) {
						herald.HP = shieldHp; // the void closes over every wound
						herald.gfx0(86);
					}
					if (++shieldTicks > 50) { // never a soft-lock: the shroud can't hold forever
						Npc shade;
						while ((shade = VoidRealm.findSpawned(p, HOLLOW_SHADE)) != null) {
							banish(shade);
						}
					}
					if (VoidRealm.aliveSpawned(p, HOLLOW_SHADE) == 0) {
						shieldHp = -1;
						herald.forceChat("No... no!");
						p.getPacketSender().sendMessage("The Herald's shroud tears away. Strike now!");
					}
				}
				if (tick % 12 == 0) { // a lash of void, whatever you're doing
					int hit = 2 + Misc.random(5);
					GearQuest.voidBolt(p, herald, 1);
					later(p, 2, () -> {
						p.dealDamage(hit);
						p.handleHitMask(hit);
						p.getPacketSender().sendMessage("A lash of void tears at you.");
					});
				}
				if (tick % 9 == 0) {
					herald.forceChat(HERALD_LINES[Misc.random(HERALD_LINES.length - 1)]);
				}
			}

			@Override
			public void stop() {
			}
		}, 1);
	}

	private static final int[][] AROUND = { { -2, 0 }, { 2, 0 }, { 0, 2 }, { 0, -2 }, { -2, 2 }, { 2, 2 }, { -2, -2 }, { 2, -2 },
			{ -3, 0 }, { 3, 0 }, { 0, 3 }, { 0, -3 }, { -1, 1 }, { 1, 1 }, { -1, -1 }, { 1, -1 } };

	/** Two shades peel out of the dark on open floor close to the player, where they can be reached and fought. */
	private static void spawnShades(Player p, int maxHit) {
		int made = 0;
		for (int[] o : AROUND) {
			int x = p.absX + o[0], y = p.absY + o[1];
			if (made < 2 && Region.getClipping(x, y, p.heightLevel) == 0) {
				NpcHandler.spawnNpc(p, HOLLOW_SHADE, x, y, p.heightLevel, 1, 26, maxHit, 30, 22, true, false);
				made++;
			}
		}
		while (made++ < 2) {
			NpcHandler.spawnNpc(p, HOLLOW_SHADE, p.absX, p.absY, p.heightLevel, 1, 26, maxHit, 30, 22, true, false);
		}
	}

	/** NPC deaths that matter to the quest. */
	static void npcKilled(Player killer, int npcType) {
		if (npcType == THANE_OATHSWORN) {
			killer.crownFlags |= OATHSWORN_SLAIN;
			killer.getPacketSender().sendMessage("The oathsworn crumbles. Its oath, at last, is over.");
		} else if (npcType == BROODMOTHER) {
			killer.crownFlags |= BROODMOTHER_SLAIN;
			killer.getPacketSender().sendMessage("The broodmother curls up and dies. Now for that cocoon.");
		} else if (npcType == HOLLOW_HERALD && killer.voidCrown == WARDS_LIT) {
			killer.voidCrown = HERALD_SLAIN;
			QuestAssistant.sendStages(killer);
			killer.getPacketSender().sendMessage("The Herald comes apart like smoke in the wind. The Ember burns brighter.");
		}
	}

	/* ====================================================================== */
	/*                      THE WAYSTONES, AND THE SIGNET                     */
	/* ====================================================================== */

	private static int waystoneAt(int x, int y) {
		for (int i = 0; i < WAYSTONE_SPOTS.length; i++) {
			if (Math.abs(WAYSTONE_SPOTS[i][0] + 1 - x) <= 1 && Math.abs(WAYSTONE_SPOTS[i][1] - y) <= 1) {
				return i;
			}
		}
		return -1;
	}

	private static void touchWaystone(Player p, int x, int y) {
		if (p.voidCrown < COMPLETE) {
			dialogue(p, D_WAYSTONE_DEAD, -1);
			return;
		}
		int here = waystoneAt(x, y);
		if (here >= 0 && (p.crownFlags & 1 << (WAYSTONE_BITS + here)) == 0) {
			p.crownFlags |= 1 << (WAYSTONE_BITS + here);
			p.gfx0(86);
			p.getPacketSender().sendMessage("The rune flares violet. The waystone at " + WAYSTONE_NAMES[here] + " will remember you.");
		}
		java.util.List<Integer> to = new java.util.ArrayList<>();
		for (int i = 0; i < WAYSTONE_NAMES.length; i++) {
			if (i != here && (p.crownFlags & 1 << (WAYSTONE_BITS + i)) != 0) {
				to.add(i);
			}
		}
		if (to.isEmpty()) {
			p.getPacketSender().sendMessage("No other waystone knows you yet. Find them and touch them.");
			return;
		}
		if (to.size() == 1) {
			to.add(-1); // a menu needs at least two lines
		}
		p.wayChoices = to.stream().mapToInt(i -> i).toArray();
		String[] lines = new String[p.wayChoices.length];
		for (int i = 0; i < lines.length; i++) {
			lines[i] = p.wayChoices[i] < 0 ? "Stay here." : WAYSTONE_NAMES[p.wayChoices[i]];
		}
		p.getDialogueHandler().sendOption(lines);
		p.dialogueAction = A_WAYSTONE;
	}

	private static void travel(Player p, int to) {
		p.getPacketSender().sendMessage("The rune pulls you along the old roads...");
		p.getPlayerAssistant().startTeleport(WAYSTONE_SPOTS[to][0], WAYSTONE_SPOTS[to][1], 0, "modern");
	}

	/** The Signet of Gatehold's "Rub". */
	static boolean clickItem(Player p, int itemId) {
		if (itemId == GATEHOLD_SIGNET) {
			p.getPacketSender().sendMessage("You rub the signet. Gatehold calls you home.");
			travel(p, 0);
			return true;
		}
		if (itemId == ROYAL_CHRONICLE) {
			dialogue(p, D_READ, -1);
			return true;
		}
		return false;
	}
}
