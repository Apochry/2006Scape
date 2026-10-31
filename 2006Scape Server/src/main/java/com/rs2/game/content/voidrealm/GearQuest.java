package com.rs2.game.content.voidrealm;

import static com.rs2.game.content.voidrealm.VoidConstants.*;
import static com.rs2.game.content.voidrealm.VoidRealm.*;

import com.rs2.Constants;
import com.rs2.event.CycleEvent;
import com.rs2.event.CycleEventContainer;
import com.rs2.event.CycleEventHandler;
import com.rs2.game.content.quests.impl.Heartforged;
import com.rs2.game.content.quests.impl.HoldTheGate;
import com.rs2.game.content.quests.impl.TheHollowKing;
import com.rs2.game.dialogues.ChatEmotes;
import com.rs2.game.npcs.Npc;
import com.rs2.game.npcs.NpcHandler;
import com.rs2.game.players.Player;
import com.rs2.util.Misc;

/**
 * Where the saga's gear comes from once the quests are done, so it arrives at the pace of the story rather than all at
 * once:
 * <ul>
 * <li>after Hold the Gate: Dorrin opens the Warden's armoury, Kael sets "Fifty for the Wall" (the Duskyew shortbow),
 * and the Warden's gauntlets drop from husks;</li>
 * <li>after Heartforged: the fallen star can be mined for starmetal (Mining 40), Dorrin teaches the Starfall halberd
 * (Smithing 60), and the rime shrine's sentinel re-forms - it sometimes leaves the Rimecleaver behind;</li>
 * <li>after The Hollow King: Aurel's mace, Maelis binds the Staff of the caged star (Crafting 60), and Dorrin reforges
 * rune into Aurel's Royal Guard (Smithing 70) - the same pieces the Starless sometimes drops.</li>
 * </ul>
 * Dialogue ids 20600-20799. Also the shared effect helpers (void bolts, falling stars) the fights use.
 */
final class GearQuest {

	private GearQuest() {
	}

	private static final int D_DORRIN = 20600, D_DORRIN_STAR = 20603, D_DORRIN_NOSTAR = 20606, D_DORRIN_HALBERD = 20608,
			D_DORRIN_ROYAL = 20612, D_KAEL_TASK = 20630, D_KAEL_COUNT = 20636, D_KAEL_BOW = 20638, D_KAEL_SPARE = 20642,
			D_MAELIS = 20660, D_MAELIS_STAFF = 20663, D_MAELIS_MACE = 20667;
	private static final int A_DORRIN = 20600, A_DORRIN_STAR = 20601, A_KAEL = 20602, A_MAELIS = 20603;
	private static final int OPTION_2 = 9157, OPTION_3 = 9167, OPTION_4 = 9178;

	/** gateFlags: Kael's task accepted; the bow handed over. (Bits 0-2 are the barricades.) */
	static final int KAEL_TASK = 1 << 3, KAEL_BOW = 1 << 4;
	/** forgeFlags: Aurel's mace handed over. (1 = sentinel slain, 2 = star woken.) */
	static final int MACE_GIVEN = 1 << 8;
	static final int WALL_KILLS = 50;
	static final int STAR_MINING = 40, HALBERD_SMITHING = 60, ROYAL_SMITHING = 70, STAFF_CRAFTING = 60;
	private static final int HAMMER = 2347, RUNE_BAR = 2363, BATTLESTAFF = 1391, COSMIC_RUNE = 564;
	/** Royal Guard reforging: {piece, base, starmetal}. The gauntlets are the Warden's, remade. */
	private static final int[][] ROYAL_RECIPES = { { ROYAL_HELM, 1163, 1 }, { ROYAL_BODY, 1127, 3 }, { ROYAL_LEGS, 1079, 2 },
			{ ROYAL_KITESHIELD, 1201, 2 }, { ROYAL_BOOTS, 4131, 1 }, { ROYAL_GAUNTLETS, WARDEN_GAUNTLETS, 1 } };

	private static int level(Player p, int skill) {
		return p.getPlayerAssistant().getLevelForXP(p.playerXP[skill]);
	}

	static boolean firstClickNpc(Player p, int npcType) {
		switch (npcType) {
		case DORRIN:
			if (p.voidGate < HoldTheGate.COMPLETE) {
				return false;
			}
			dialogue(p, D_DORRIN, npcType);
			return true;
		case KAEL:
			if (p.voidGate < HoldTheGate.COMPLETE) {
				return false;
			}
			if ((p.gateFlags & KAEL_TASK) == 0) {
				dialogue(p, D_KAEL_TASK, npcType);
			} else if ((p.gateFlags & KAEL_BOW) == 0 && p.gateKills >= WALL_KILLS) {
				dialogue(p, D_KAEL_BOW, npcType);
			} else if ((p.gateFlags & KAEL_BOW) == 0 && p.hollowKills < 10) {
				dialogue(p, D_KAEL_COUNT, npcType);
			} else if ((p.gateFlags & KAEL_BOW) != 0 && !ForgeQuest.owns(p, DUSKYEW_SHORTBOW) && p.hollowKills < 10) {
				dialogue(p, D_KAEL_SPARE, npcType);
			} else {
				return false; // her bounty
			}
			return true;
		case MAELIS:
			if (p.voidKing < TheHollowKing.COMPLETE) {
				return false;
			}
			dialogue(p, D_MAELIS, npcType);
			return true;
		}
		return false;
	}

	static boolean sendDialogue(Player p, int id, int npcId) {
		switch (id) {
		/* ---------------- Dorrin: the armoury, and starmetal ---------------- */
		case D_DORRIN:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK1, false, "Gatehold's champion! The forge is yours. What'll it be?");
			break;
		case D_DORRIN + 1:
			p.getDialogueHandler().sendOption("The Warden's armoury, please.", "Can you work starmetal?", "Just the usual stock.",
					"Nothing, thanks.");
			p.dialogueAction = A_DORRIN;
			break;
		case D_DORRIN_STAR:
			npc(p, id, DORRIN, ChatEmotes.HAPPY_JOYFUL, false, "Can I? Since the Emberbrand I've thought of nothing else.",
					"Bring me starmetal from the crater and I'll show you how.");
			break;
		case D_DORRIN_STAR + 1:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK2, false, "Three starmetal and two rune bars make a halberd. And if you've",
					"rune plate, I can teach you to reforge it the way Aurel's guard wore it.");
			break;
		case D_DORRIN_STAR + 2:
			p.getDialogueHandler().sendOption("Forge a Starfall halberd.", "Reforge rune into Royal Guard armour.",
					"What does it all take?", "Never mind.");
			p.dialogueAction = A_DORRIN_STAR;
			break;
		case D_DORRIN_NOSTAR:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK1, false, "Starmetal? I've heard of it. Never seen it. If you ever bring",
					"me some, I'll know what to do with it.");
			break;
		case D_DORRIN_NOSTAR + 1:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK2, true, "Maelis was asking after you, by the way. Something about a fire.");
			break;
		case D_DORRIN_HALBERD:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK1, false, "The halberd: three starmetal, two rune bars, a hammer, and",
					"Smithing " + HALBERD_SMITHING + ". The star's still up in the crater - it takes Mining " + STAR_MINING + " to chip.");
			break;
		case D_DORRIN_HALBERD + 1:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK2, false, "Royal Guard plate takes Smithing " + ROYAL_SMITHING + ", and only once",
					"you've seen Aurel's guard for yourself. Rune helm or boots: one starmetal.");
			break;
		case D_DORRIN_HALBERD + 2:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK1, true, "Legs or kiteshield: two. A platebody: three. And Warden's",
					"gauntlets take one to remake. I've seen the Starless drop that plate, too, if you're brave.");
			break;

		/* ---------------- Kael: Fifty for the Wall ---------------- */
		case D_KAEL_TASK:
			npc(p, id, KAEL, ChatEmotes.CALM_TALK1, false, "The raid's over, but they haven't stopped coming. Every husk",
					"that walks out of the Scar is one more at the wall next month.");
			break;
		case D_KAEL_TASK + 1:
			npc(p, id, KAEL, ChatEmotes.CALM_TALK2, false, "I've a bow put by. Duskyew - grew in the Scar's shadow, so it",
					"hates them as much as I do. Fastest bow I've ever drawn.");
			break;
		case D_KAEL_TASK + 2:
			npc(p, id, KAEL, ChatEmotes.CALM_TALK1, false, "Put down fifty Hollowed for the wall, and it's yours. Wolves,",
					"husks, ravagers - it doesn't matter. I'll keep count.");
			break;
		case D_KAEL_TASK + 3:
			p.getDialogueHandler().sendOption("Fifty for the wall. Done.", "Maybe later.");
			p.dialogueAction = A_KAEL;
			break;
		case D_KAEL_TASK + 4:
			npc(p, id, KAEL, ChatEmotes.HAPPY_JOYFUL, true, "Good. Fifty. I'll be watching - and my bounty still stands, ten at a",
					"time, on top.");
			break;
		case D_KAEL_COUNT:
			npc(p, id, KAEL, ChatEmotes.CALM_TALK1, true, "Fifty for the wall. You're at " + p.gateKills + ". " + (WALL_KILLS - p.gateKills)
					+ " to go - they're thick on the black ground east of here.");
			break;
		case D_KAEL_BOW:
			npc(p, id, KAEL, ChatEmotes.HAPPY_JOYFUL, false, "Fifty. I counted every one. The wall's quieter than it's been in",
					"years.");
			break;
		case D_KAEL_BOW + 1:
			if ((p.gateFlags & KAEL_BOW) == 0) {
				p.gateFlags |= KAEL_BOW;
				p.getItemAssistant().addOrDropItem(DUSKYEW_SHORTBOW, 1);
				p.getPlayerAssistant().addSkillXP(8000, Constants.RANGED);
			}
			itemChat(p, DUSKYEW_SHORTBOW, "Kael hands you the Duskyew shortbow. It's light, and it",
					"hums when you draw it. (Ranged 50 to wield. You gain 8,000 Ranged XP.)");
			p.nextChat = id + 1;
			break;
		case D_KAEL_BOW + 2:
			npc(p, id, KAEL, ChatEmotes.CALM_TALK2, true, "It takes rune arrows, if you can get them. Don't waste them on",
					"wolves.");
			break;
		case D_KAEL_SPARE:
			npc(p, id, KAEL, ChatEmotes.ANNOYED, true, "You lost the Duskyew? ...I had the fletcher make a second, in case.",
					"This is the last one.");
			if (!ForgeQuest.owns(p, DUSKYEW_SHORTBOW)) {
				p.getItemAssistant().addOrDropItem(DUSKYEW_SHORTBOW, 1);
			}
			break;

		/* ---------------- Maelis: the caged star, and Aurel's mace ---------------- */
		case D_MAELIS:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK1, false, "The chronicle has a new last page now. I wrote it myself. What",
					"can I do for you?");
			break;
		case D_MAELIS + 1:
			p.getDialogueHandler().sendOption("Can starmetal hold a spell?", "About Aurel's mace...", "Nothing, thanks.");
			p.dialogueAction = A_MAELIS;
			break;
		case D_MAELIS_STAFF:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK2, false, "Hold one? It already does. Every piece of starmetal has a star",
					"in it, asleep. Cage it on a staff and it'll wake when you cast.");
			break;
		case D_MAELIS_STAFF + 1:
			npc(p, id, MAELIS, ChatEmotes.CALM_TALK1, false, "A battlestaff, two starmetal, and fifty cosmic runes to bind the",
					"cage. Crafting " + STAFF_CRAFTING + ", and steady hands. I'll guide you through it.");
			break;
		case D_MAELIS_STAFF + 2:
			p.getDialogueHandler().sendOption("Bind the Staff of the caged star.", "Not right now.");
			p.dialogueAction = A_MAELIS + 100;
			break;
		case D_MAELIS_MACE:
			if (!ForgeQuest.owns(p, KINGS_SCEPTRE)) {
				npc(p, id, MAELIS, ChatEmotes.CALM_TALK1, true, "Aurel's mace? It turned up on my desk, wrapped in a page of the",
						"chronicle. I think he wants you to have it.");
				p.forgeFlags |= MACE_GIVEN;
				p.getItemAssistant().addOrDropItem(KINGS_SCEPTRE, 1);
			} else {
				npc(p, id, MAELIS, ChatEmotes.CALM_TALK2, true, "He carried it as a sceptre, you know, before the dark. It's",
						"heavier now. Look after it.");
			}
			break;
		default:
			return false;
		}
		return true;
	}

	static boolean handleOption(Player p, int button) {
		switch (p.dialogueAction) {
		case A_DORRIN:
			if (button < OPTION_4 || button > OPTION_4 + 3) {
				return false;
			}
			p.dialogueAction = -1;
			if (button == OPTION_4) {
				p.getShopAssistant().openShop(WARDEN_SHOP);
			} else if (button == OPTION_4 + 1) {
				dialogue(p, p.voidForge >= Heartforged.COMPLETE ? D_DORRIN_STAR : D_DORRIN_NOSTAR, DORRIN);
			} else if (button == OPTION_4 + 2) {
				p.getShopAssistant().openShop(DORRIN_SHOP);
			} else {
				p.getPacketSender().closeAllWindows();
			}
			return true;
		case A_DORRIN_STAR:
			if (button < OPTION_4 || button > OPTION_4 + 3) {
				return false;
			}
			p.dialogueAction = -1;
			if (button == OPTION_4) {
				forgeHalberd(p);
			} else if (button == OPTION_4 + 1) {
				reforgeRoyal(p);
			} else if (button == OPTION_4 + 2) {
				dialogue(p, D_DORRIN_HALBERD, DORRIN);
			} else {
				p.getPacketSender().closeAllWindows();
			}
			return true;
		case A_KAEL:
			if (button != OPTION_2 && button != OPTION_2 + 1) {
				return false;
			}
			p.dialogueAction = -1;
			if (button == OPTION_2) {
				p.gateFlags |= KAEL_TASK;
				p.gateKills = 0;
				dialogue(p, D_KAEL_TASK + 4, KAEL);
			} else {
				p.getPacketSender().closeAllWindows();
			}
			return true;
		case A_MAELIS:
			if (button < OPTION_3 || button > OPTION_3 + 2) {
				return false;
			}
			p.dialogueAction = -1;
			if (button == OPTION_3) {
				dialogue(p, D_MAELIS_STAFF, MAELIS);
			} else if (button == OPTION_3 + 1) {
				dialogue(p, D_MAELIS_MACE, MAELIS);
			} else {
				p.getPacketSender().closeAllWindows();
			}
			return true;
		case A_MAELIS + 100:
			if (button != OPTION_2 && button != OPTION_2 + 1) {
				return false;
			}
			p.dialogueAction = -1;
			if (button == OPTION_2) {
				bindStaff(p);
			} else {
				p.getPacketSender().closeAllWindows();
			}
			return true;
		}
		return false;
	}

	/* ====================================================================== */
	/*                                RECIPES                                 */
	/* ====================================================================== */

	private static void itemChat(Player p, int item, String... lines) {
		p.getDialogueHandler().sendItemChat(item, 200, com.rs2.game.items.DeprecatedItems.getItemName(item), wrap(lines));
	}

	private static void statement(Player p, String... lines) {
		p.getDialogueHandler().sendStatement(wide(lines));
		p.nextChat = 0;
	}

	private static void forgeHalberd(Player p) {
		if (level(p, Constants.SMITHING) < HALBERD_SMITHING) {
			statement(p, "You need a Smithing level of " + HALBERD_SMITHING + " to forge a Starfall halberd.");
			return;
		}
		if (!p.getItemAssistant().playerHasItem(HAMMER)) {
			statement(p, "You'll need a hammer.");
			return;
		}
		if (!p.getItemAssistant().playerHasItem(STARMETAL, 3) || !p.getItemAssistant().playerHasItem(RUNE_BAR, 2)) {
			statement(p, "A Starfall halberd takes three starmetal and two rune bars. You have " + p.getItemAssistant().getItemAmount(STARMETAL)
					+ " starmetal and " + p.getItemAssistant().getItemAmount(RUNE_BAR) + " rune bars.");
			return;
		}
		p.getItemAssistant().deleteItem(STARMETAL, 3);
		p.getItemAssistant().deleteItem(RUNE_BAR, 2);
		p.startAnimation(898);
		p.getPacketSender().closeAllWindows();
		p.getPacketSender().sendMessage("Dorrin works the bellows while you hammer the starmetal...");
		later(p, 3, () -> {
			p.startAnimation(898);
			p.gfx0(GFX_STARFALL_MARK);
		});
		later(p, 6, () -> {
			p.getItemAssistant().addOrDropItem(STARFALL_HALBERD, 1);
			p.getPlayerAssistant().addSkillXP(1800, Constants.SMITHING);
			itemChat(p, STARFALL_HALBERD, "The blade comes off the anvil glowing - and doesn't stop.",
					"You've forged a Starfall halberd! (Attack 60 to wield.)");
			p.nextChat = 0;
		});
	}

	private static void reforgeRoyal(Player p) {
		if (p.voidKing < TheHollowKing.COMPLETE) {
			statement(p, "\"Aurel's Royal Guard? I don't even know what it looked like. Find out, and I'll", "show you how.\"");
			return;
		}
		if (level(p, Constants.SMITHING) < ROYAL_SMITHING) {
			statement(p, "You need a Smithing level of " + ROYAL_SMITHING + " to reforge Royal Guard armour.");
			return;
		}
		if (!p.getItemAssistant().playerHasItem(HAMMER)) {
			statement(p, "You'll need a hammer.");
			return;
		}
		int made = 0, xp = 0, last = -1;
		for (int[] r : ROYAL_RECIPES) {
			while (p.getItemAssistant().playerHasItem(r[1]) && p.getItemAssistant().playerHasItem(STARMETAL, r[2])) {
				p.getItemAssistant().deleteItem(r[1], 1);
				p.getItemAssistant().deleteItem(STARMETAL, r[2]);
				p.getItemAssistant().addItem(r[0], 1);
				made++;
				xp += 700 * r[2];
				last = r[0];
			}
		}
		if (made == 0) {
			dialogue(p, D_DORRIN_HALBERD + 1, DORRIN);
			return;
		}
		p.startAnimation(898);
		p.gfx0(GFX_STARFALL_MARK);
		p.getPlayerAssistant().addSkillXP(xp, Constants.SMITHING);
		itemChat(p, last, "Starmetal runs into the old rune like light into water.",
				"You reforge " + made + (made == 1 ? " piece" : " pieces") + " of Royal Guard armour. (Defence 50 to wear.)");
		p.nextChat = 0;
	}

	private static void bindStaff(Player p) {
		if (level(p, Constants.CRAFTING) < STAFF_CRAFTING) {
			statement(p, "You need a Crafting level of " + STAFF_CRAFTING + " to bind the cage.");
			return;
		}
		if (!p.getItemAssistant().playerHasItem(BATTLESTAFF) || !p.getItemAssistant().playerHasItem(STARMETAL, 2)
				|| !p.getItemAssistant().playerHasItem(COSMIC_RUNE, 50)) {
			statement(p, "You need a battlestaff, two starmetal and fifty cosmic runes.");
			return;
		}
		p.getItemAssistant().deleteItem(BATTLESTAFF, 1);
		p.getItemAssistant().deleteItem(STARMETAL, 2);
		p.getItemAssistant().deleteItem(COSMIC_RUNE, 50);
		p.startAnimation(711);
		p.gfx0(GFX_FALLING_STAR);
		p.getPacketSender().closeAllWindows();
		p.getPacketSender().sendMessage("You cage the starmetal round the staff. Maelis reads the binding...");
		later(p, 5, () -> {
			p.getItemAssistant().addOrDropItem(CAGED_STAR_STAFF, 1);
			p.getPlayerAssistant().addSkillXP(2500, Constants.CRAFTING);
			p.getPlayerAssistant().addSkillXP(2500, Constants.MAGIC);
			itemChat(p, CAGED_STAR_STAFF, "Something inside the cage wakes up and starts to glow.",
					"You've made the Staff of the caged star! (Magic 60 to wield.)");
			p.nextChat = 0;
		});
	}

	/** Aurel's mace, handed over at the end of The Hollow King. */
	static void giveMace(Player p) {
		if ((p.forgeFlags & MACE_GIVEN) == 0 || !ForgeQuest.owns(p, KINGS_SCEPTRE)) {
			p.forgeFlags |= MACE_GIVEN;
			if (!ForgeQuest.owns(p, KINGS_SCEPTRE)) {
				p.getItemAssistant().addOrDropItem(KINGS_SCEPTRE, 1);
				p.getPacketSender().sendMessage("Aurel gives you his old sceptre. It's a mace now. It always was.");
			}
		}
	}

	/* ====================================================================== */
	/*                    THE STAR AND THE SHRINE, AFTERWARDS                  */
	/* ====================================================================== */

	/** After Heartforged the star can be mined for as long as you like - but the void inside it still wakes. */
	static void mineStar(Player p) {
		if (level(p, Constants.MINING) < STAR_MINING) {
			p.getPacketSender().sendMessage("You need a Mining level of " + STAR_MINING + " to chip any more starmetal free.");
			return;
		}
		if (!hasPickaxe(p)) {
			p.getPacketSender().sendMessage("You need a pickaxe to chip at the star.");
			return;
		}
		if (aliveSpawned(p, VOIDLING) > 0) {
			p.getPacketSender().sendMessage("Not with those things crawling all over you!");
			return;
		}
		if (p.getItemAssistant().freeSlots() == 0) {
			p.getPacketSender().sendMessage("Your inventory is too full to hold any starmetal.");
			return;
		}
		int sx = p.absX, sy = p.absY;
		p.startAnimation(625);
		p.getPacketSender().sendMessage("You swing at the star. It rings like a bell.");
		CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer c) {
				if (p.disconnected || p.absX != sx || p.absY != sy || p.isDead || p.getItemAssistant().freeSlots() == 0) {
					c.stop();
					return;
				}
				p.startAnimation(625);
				// a better miner chips faster: 1 in 10 swings at level 40, about 1 in 5 at 99 (~25k-50k Mining XP an hour)
				if (Misc.random(99) >= 10 + (level(p, Constants.MINING) - STAR_MINING) / 5) {
					return;
				}
				p.getItemAssistant().addItem(STARMETAL, 1);
				p.getPlayerAssistant().addSkillXP(160, Constants.MINING);
				p.getPacketSender().sendMessage("A shard of starmetal comes away in your hand.");
				if (Misc.random(9) == 0) {
					p.getPacketSender().sendMessage("@red@The star cracks - and the void inside it comes boiling out!");
					p.getPacketSender().stillGfx(GFX_VOID_IMPACT, STAR_X, STAR_Y, 0, 0);
					for (int i = 0; i < 2; i++) {
						NpcHandler.spawnNpc(p, VOIDLING, p.absX + (i == 0 ? 1 : -1), p.absY, 0, 1, 30, 4, 35, 25, true, false);
					}
					c.stop();
				}
			}

			@Override
			public void stop() {
				p.startAnimation(65535);
			}
		}, 4);
	}

	/** After Heartforged the shrine's sentinel re-forms whenever someone reaches for the altar. */
	static void wakeSentinel(Player p) {
		if (findSpawned(p, RIMEBOUND_SENTINEL) != null) {
			p.getPacketSender().sendMessage("The sentinel won't let you near it!");
			return;
		}
		p.getPacketSender().sendMessage("The ice behind you cracks, and stands up again.");
		NpcHandler.spawnNpc(p, RIMEBOUND_SENTINEL, p.absX + 1, p.absY - 1, p.heightLevel, 1, 110, 7, 60, 55, true, false);
		sentinelFight(p);
	}

	/** The sentinel's breath: every so often a burst of rime that hits harder the closer you stand. */
	static void sentinelFight(Player p) {
		Npc s = findSpawned(p, RIMEBOUND_SENTINEL);
		if (s == null) {
			return;
		}
		s.forceChat("NONE SHALL THAW THE HEART.");
		s.gfx0(GFX_RIME_BURST);
		CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
			int tick;

			@Override
			public void execute(CycleEventContainer c) {
				tick++;
				if (s.isDead || s.npcType != RIMEBOUND_SENTINEL || p.disconnected || p.isDead
						|| Math.abs(p.absX - s.absX) > 16 || Math.abs(p.absY - s.absY) > 16) {
					c.stop();
					return;
				}
				if (tick % 10 == 0) {
					s.gfx0(GFX_RIME_BURST);
					s.forceChat("FREEZE.");
					if (Math.abs(p.absX - s.absX) <= 1 && Math.abs(p.absY - s.absY) <= 1) {
						p.gfx100(GFX_RIME_BURST);
						KingQuest.strike(p, 5 + Misc.random(5));
						p.getPacketSender().sendMessage("A burst of rime freezes you to the spot!");
						p.freezeTimer = 3;
					}
				}
			}

			@Override
			public void stop() {
			}
		}, 1);
	}

	static void npcKilled(Player killer, int npcType) {
		if ((npcType == HOLLOWED_WOLF || npcType == HOLLOW_HUSK || npcType == HOLLOW_RAVAGER)
				&& (killer.gateFlags & (KAEL_TASK | KAEL_BOW)) == KAEL_TASK && killer.gateKills < WALL_KILLS) {
			killer.gateKills++;
			int k = killer.gateKills;
			if (k == WALL_KILLS) {
				killer.getPacketSender().sendMessage("@gre@That's fifty for the wall. Warden Kael will want to see you.");
			} else if (k % 10 == 0 || k == 45) {
				killer.getPacketSender().sendMessage("Fifty for the wall: " + k + " of " + WALL_KILLS + ".");
			}
		}
	}

	/* ====================================================================== */
	/*                                EFFECTS                                 */
	/* ====================================================================== */

	/** A bolt of void from an NPC (its centre {@code half} tiles in) to the player, and the burst where it lands. */
	static void voidBolt(Player p, Npc from, int half) {
		int nx = from.absX + half, ny = from.absY + half;
		int offX = (ny - p.absY) * -1, offY = (nx - p.absX) * -1;
		p.getPacketSender().createProjectile(nx, ny, offX, offY, 50, 70, GFX_VOID_BOLT, 60, 31, -p.getId() - 1, 30);
		later(p, 2, () -> p.gfx100(GFX_VOID_IMPACT));
	}

	/** The warning ring on each tile, now; the star itself a moment later, so it lands as the damage does. */
	static void markStars(Player p, int[][] tiles) {
		for (int[] t : tiles) {
			p.getPacketSender().stillGfx(GFX_STARFALL_MARK, t[0], t[1], 0, 0);
		}
	}

	static void dropStars(Player p, int[][] tiles) {
		for (int[] t : tiles) {
			p.getPacketSender().stillGfx(GFX_FALLING_STAR, t[0], t[1], 0, 0);
		}
	}
}
