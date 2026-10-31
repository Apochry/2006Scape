package com.rs2.game.content.voidrealm;

import static com.rs2.game.content.voidrealm.VoidConstants.*;
import static com.rs2.game.content.voidrealm.VoidRealm.*;

import com.rs2.Constants;
import com.rs2.game.content.skullbrine.SkullbrineConstants;
import com.rs2.game.dialogues.ChatEmotes;
import com.rs2.game.npcs.Npc;
import com.rs2.game.npcs.NpcHandler;
import com.rs2.game.players.Player;
import com.rs2.game.players.PlayerHandler;
import com.rs2.util.Misc;

/**
 * Nythera's optional places: Gatehold and its people, the quarry, the Last Hearth inn, Smugglers' Cove, and the three
 * dungeons (the Sunken Barrow, Hollowdeep and the Frostfang Caves) with their bosses.
 *
 * Called from {@link VoidRealm}'s hooks before anything else.
 */
final class NytheraPlaces {

	private NytheraPlaces() {
	}

	/* Dialogue ids 9800-9899. */
	static final int D_YSOLDE = 9800, D_YSOLDE_DUNGEONS = 9805, D_YSOLDE_REPORT = 9812, D_PELL = 9820, D_DORRIN = 9830,
			D_TESS = 9840, D_MORRA = 9850, D_MORRA_RUMOUR = 9854, D_NETTLE = 9870;
	static final int A_YSOLDE = 9800;

	/** The three bosses, in the order of their bits in {@code Player#nytheraBosses}. */
	static final int[] BOSSES = { BARROW_THANE, HOLLOW_MATRIARCH, RIMECLAW };
	static final String[] BOSS_NAMES = { "the Barrow Thane", "the Hollow Matriarch", "Rimeclaw" };
	static final int[] BOSS_COINS = { 2500, 4000, 6000 };
	static final int[] BOSS_XP = { 4000, 7000, 10000 };

	/* ---------------------------------------------------------------- people */

	static boolean firstClickNpc(Player p, int npcType) {
		switch (npcType) {
		case YSOLDE:
			dialogue(p, unreported(p) != 0 ? D_YSOLDE_REPORT : D_YSOLDE, npcType);
			return true;
		case PELL:
			dialogue(p, D_PELL, npcType);
			return true;
		case DORRIN:
			dialogue(p, D_DORRIN, npcType);
			return true;
		case TESS:
			dialogue(p, D_TESS, npcType);
			return true;
		case MORRA:
			dialogue(p, D_MORRA, npcType);
			return true;
		case NETTLE:
			dialogue(p, D_NETTLE, npcType);
			return true;
		}
		return false;
	}

	static boolean secondClickNpc(Player p, int npcType) {
		int shop;
		switch (npcType) {
		case PELL:
			shop = PELL_SHOP;
			break;
		case DORRIN:
			shop = DORRIN_SHOP;
			break;
		case TESS:
			shop = TESS_SHOP;
			break;
		case MORRA:
			shop = MORRA_SHOP;
			break;
		case NETTLE:
			shop = NETTLE_SHOP;
			break;
		default:
			return false;
		}
		p.getShopAssistant().openShop(shop);
		return true;
	}

	private static int unreported(Player p) {
		return (p.nytheraBosses & 7) & ~(p.nytheraBosses >> 4 & 7);
	}

	static boolean sendDialogue(Player p, int id, int npcId) {
		if (id < 9800 || id >= 9900) {
			return false;
		}
		p.talkingNpc = npcId;
		p.nextChat = 0;
		switch (id) {
		/* ---------------- Captain Ysolde of Gatehold ---------------- */
		case D_YSOLDE:
			npc(p, id, YSOLDE, ChatEmotes.CALM_TALK1, false, "Welcome to Gatehold - what's left of it. Thirty souls, one",
					"bank, and the only wall between the Scar and the west.");
			break;
		case D_YSOLDE + 1:
			p.getDialogueHandler().sendOption("Tell me about the dangers around here.", "I'll leave you to it.");
			p.dialogueAction = A_YSOLDE;
			break;
		case D_YSOLDE_DUNGEONS:
			npc(p, id, YSOLDE, ChatEmotes.CALM_TALK2, false, "Three places I'd send you if I hated you. West of here, past",
					"the graves, is the Sunken Barrow. The old Thane's tomb. He",
					"didn't stay in it.");
			break;
		case D_YSOLDE_DUNGEONS + 1:
			npc(p, id, YSOLDE, ChatEmotes.CALM_TALK1, false, "In the Scar's black heart there's a fissure. Hollowdeep. The",
					"husks come up out of it like ants. Something down there is",
					"making more of them.");
			break;
		case D_YSOLDE_DUNGEONS + 2:
			npc(p, id, YSOLDE, ChatEmotes.CALM_TALK2, false, "And up the north pass, past the inn, there's a cave in the",
					"mountain's flank. Frostfang. Miners went in for the ore. The",
					"ore is still there. The miners aren't.");
			break;
		case D_YSOLDE_DUNGEONS + 3:
			npc(p, id, YSOLDE, ChatEmotes.CALM_TALK1, true, "Put down whatever rules each of them and come and tell me.",
					"Gatehold pays for good news, and we get very little of it.");
			break;
		case D_YSOLDE_REPORT:
			report(p);
			break;
		case D_YSOLDE_REPORT + 1:
			if (unreported(p) != 0) {
				report(p);
			} else if ((p.nytheraBosses & 0x70) == 0x70) {
				npc(p, id, YSOLDE, ChatEmotes.HAPPY_JOYFUL, true, "All three. Gatehold won't forget it. Neither will I - the bank",
						"knows your name now, and the guards will drink to it.");
			} else {
				npc(p, id, YSOLDE, ChatEmotes.CALM_TALK2, true, "That's one fewer thing in the dark. There are others.");
			}
			break;

		/* ---------------- Pell, Gatehold's general store ---------------- */
		case D_PELL:
			npc(p, id, PELL, ChatEmotes.HAPPY_JOYFUL, false, "Customer! A real one! Everything you need to not die in the",
					"wilds, and a few things you don't.");
			break;
		case D_PELL + 1:
			npc(p, id, PELL, ChatEmotes.CALM_TALK2, true, "Rope, tinderboxes, buckets, the lot. Mostly carried up from the",
					"cove, so don't ask where it came from.");
			break;

		/* ---------------- Dorrin, Gatehold's smith ---------------- */
		case D_DORRIN:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK1, false, "Mind the sparks. The furnace runs hot and the anvil's yours",
					"if you've a hammer and the bars.");
			break;
		case D_DORRIN + 1:
			me(p, id, false, "Where's good ore around here?");
			break;
		case D_DORRIN + 2:
			npc(p, id, DORRIN, ChatEmotes.CALM_TALK2, true, "Tess's quarry, east along the foothills, for iron and coal and",
					"the odd bit of mithril. The good stuff - adamant, runite - is in",
					"Frostfang. If you come back out, I'll buy you a drink.");
			break;

		/* ---------------- Tess, the quarry ---------------- */
		case D_TESS:
			npc(p, id, TESS, ChatEmotes.HAPPY_JOYFUL, false, "Grab a rock, any rock. Iron, coal, silver, gold, a seam of",
					"mithril if you dig deep. All yours - just leave the cart.");
			break;
		case D_TESS + 1:
			npc(p, id, TESS, ChatEmotes.CALM_TALK1, true, "Pickaxes for sale if you've come without. I'd sell you a rune one",
					"but I've never seen one. Frostfang's got runite, they say.");
			break;

		/* ---------------- Morra, the Last Hearth ---------------- */
		case D_MORRA:
			npc(p, id, MORRA, ChatEmotes.HAPPY_JOYFUL, false, "Come in out of the wind! Last hot meal before the mountains,",
					"first one coming down.");
			break;
		case D_MORRA + 1:
			me(p, id, false, "Heard anything interesting?");
			break;
		case D_MORRA + 2:
			dialogue(p, D_MORRA_RUMOUR + Misc.random(3) * 2, npcId);
			return true;
		case D_MORRA_RUMOUR:
			npc(p, id, MORRA, ChatEmotes.CALM_TALK2, true, "Miner came down off the pass last week, white as frost. Said",
					"something in Frostfang breathes ice. Said it had claws like",
					"icicles. Then he paid for his stew in runite ore.");
			break;
		case D_MORRA_RUMOUR + 2:
			npc(p, id, MORRA, ChatEmotes.CALM_TALK1, true, "The old Thane was buried with a sword they say never dulled.",
					"Nobody's taken it off him. Not for want of trying.");
			break;
		case D_MORRA_RUMOUR + 4:
			npc(p, id, MORRA, ChatEmotes.CALM_TALK2, true, "There's a mother to those husks, the warden reckons. Down",
					"in the Scar. Has a heart like a black jewel, if you believe",
					"Kael. I mostly believe Kael.");
			break;
		case D_MORRA_RUMOUR + 6:
			npc(p, id, MORRA, ChatEmotes.HAPPY_JOYFUL, true, "Smugglers in the cove south of the Gate. Sly Nettle buys",
					"anything and sells everything. Count your fingers after.");
			break;

		/* ---------------- Sly Nettle, Smugglers' Cove ---------------- */
		case D_NETTLE:
			npc(p, id, NETTLE, ChatEmotes.CALM_TALK1, false, "You didn't see the ship. You didn't see me. You definitely",
					"didn't see the crates.");
			break;
		case D_NETTLE + 1:
			me(p, id, false, "What crates?");
			break;
		case D_NETTLE + 2:
			npc(p, id, NETTLE, ChatEmotes.HAPPY_JOYFUL, true, "Ha! I like you. Have a look at what's not in them. And the",
					"lobsters in the cove are free - the sharks further out, less so.");
			break;
		default:
			return false;
		}
		return true;
	}

	/** Captain Ysolde pays for one boss the player has slain and not yet reported. */
	private static void report(Player p) {
		int bits = unreported(p);
		if (bits == 0) {
			dialogue(p, D_YSOLDE, YSOLDE);
			return;
		}
		int b = Integer.numberOfTrailingZeros(bits);
		p.nytheraBosses |= 1 << (b + 4);
		p.getItemAssistant().addOrDropItem(995, BOSS_COINS[b]);
		p.getPlayerAssistant().addSkillXP(BOSS_XP[b], Constants.SLAYER);
		String[] lines;
		switch (b) {
		case 0:
			lines = new String[] { "The Thane? Back in his grave for good? Then the old road", "west is ours again. Take this." };
			break;
		case 1:
			lines = new String[] { "The husks' mother... dead. Kael will weep. He won't admit", "it, but he'll weep. Here." };
			break;
		default:
			lines = new String[] { "Rimeclaw. The miners have a name for the thing now, and", "a grave for it. Take this, with my thanks." };
		}
		npc(p, D_YSOLDE_REPORT, YSOLDE, ChatEmotes.HAPPY_JOYFUL, false, lines);
		p.getPacketSender().sendMessage("Captain Ysolde pays you " + String.format("%,d", BOSS_COINS[b]) + " coins. You gain "
				+ String.format("%,d", BOSS_XP[b]) + " Slayer XP.");
		if ((p.nytheraBosses & 0x70) == 0x70) {
			p.getItemAssistant().addOrDropItem(995, 10000);
			p.getPacketSender().sendMessage("For clearing all three, Gatehold adds a purse of 10,000 coins.");
		}
	}

	static boolean handleOption(Player p, boolean first) {
		if (p.dialogueAction != A_YSOLDE) {
			return false;
		}
		p.dialogueAction = -1;
		if (first) {
			dialogue(p, D_YSOLDE_DUNGEONS, YSOLDE);
		} else {
			p.getPacketSender().closeAllWindows();
		}
		return true;
	}

	/* ---------------------------------------------------------------- dungeons */

	/** Where you arrive in a dungeon: just inside, beside the way out. */
	private static final int ARRIVE_LX = 32, ARRIVE_LY = 5;

	static boolean firstClickObject(Player p, int objectId, int x, int y) {
		switch (objectId) {
		case BARROW_DOOR:
			enter(p, BARROW_REGION_X, "You heave the stone door aside and step down into the cold.");
			return true;
		case HOLLOW_FISSURE:
			enter(p, HOLLOWDEEP_REGION_X, "You climb down into the fissure. The dark swallows the light behind you.");
			return true;
		case FROST_CAVE:
			enter(p, FROSTFANG_REGION_X, "Frost crackles under your boots as you go in.");
			return true;
		case SkullbrineConstants.CAVE_EXIT:
			int rx = nytheraDungeon(x, y);
			if (rx < 0) {
				return false;
			}
			if (rx == VAULT_REGION_X) {
				p.getPlayerAssistant().movePlayer(HATCH_X, HATCH_Y - 1, 0);
				p.wardsLit = 0;
			} else if (rx == BARROW_REGION_X) {
				p.getPlayerAssistant().movePlayer(BARROW_DOOR_X, BARROW_DOOR_Y - 1, 0);
			} else if (rx == HOLLOWDEEP_REGION_X) {
				p.getPlayerAssistant().movePlayer(FISSURE_X, FISSURE_Y - 2, 0);
			} else {
				p.getPlayerAssistant().movePlayer(FROST_CAVE_X - 2, FROST_CAVE_Y, 0);
			}
			p.getPacketSender().sendMessage("You climb back out into the light.");
			return true;
		}
		return false;
	}

	private static void enter(Player p, int regionX, String message) {
		p.startAnimation(827);
		p.getPacketSender().sendMessage(message);
		later(p, 2, () -> p.getPlayerAssistant().movePlayer(dungeonBaseX(regionX) + ARRIVE_LX, DUNGEON_BASE_Y + ARRIVE_LY, 0));
	}

	/* ---------------------------------------------------------------- bosses */

	/** Boss npc indices that have already used their once-a-fight ability. */
	private static final java.util.Set<Integer> ENRAGED = new java.util.HashSet<>();

	private static int bossIndex(int npcType) {
		for (int i = 0; i < BOSSES.length; i++) {
			if (BOSSES[i] == npcType) {
				return i;
			}
		}
		return -1;
	}

	/** Called every ambience pulse for each npc: the bosses' fight mechanics. Returns true if it was a boss. */
	static boolean bossPulse(Npc n) {
		int b = bossIndex(n.npcType);
		if (b < 0) {
			return false;
		}
		if (n.isDead || n.HP >= n.MaxHP) {
			ENRAGED.remove(n.npcId);
			return true;
		}
		Player target = n.killerId > 0 && n.killerId < PlayerHandler.players.length ? PlayerHandler.players[n.killerId] : null;
		if (target == null || !n.underAttack) {
			return true;
		}
		if (n.HP * 2 < n.MaxHP && ENRAGED.add(n.npcId)) {
			switch (n.npcType) {
			case BARROW_THANE:
				n.forceChat("Rise! RISE! Your Thane commands it!");
				summon(target, n, 92, 2, 42, 5, 40, 40);
				break;
			case HOLLOW_MATRIARCH:
				n.forceChat("My children... come to mother...");
				summon(target, n, VOIDLING, 3, 30, 6, 45, 30);
				break;
			default:
				n.forceChat("*Rimeclaw's breath turns the air to knives*");
				break;
			}
		}
		// a signature attack now and then
		if (Misc.random(4) == 0 && Math.abs(target.absX - n.absX) <= 6 && Math.abs(target.absY - n.absY) <= 6) {
			switch (n.npcType) {
			case BARROW_THANE:
				n.forceChat(Misc.random(1) == 0 ? "You will lie beside me!" : "Kneel to your Thane!");
				break;
			case HOLLOW_MATRIARCH:
				int drain = 1 + Misc.random(2);
				target.playerLevel[Constants.PRAYER] = Math.max(0, target.playerLevel[Constants.PRAYER] - drain * 3);
				target.getPlayerAssistant().refreshSkill(Constants.PRAYER);
				target.gfx0(78);
				target.getPacketSender().sendMessage("The Matriarch's hollowness pulls at your soul. Your prayer drains.");
				break;
			case RIMECLAW:
				int hit = 4 + Misc.random(8);
				target.gfx0(369);
				target.freezeTimer = 8;
				target.dealDamage(hit);
				target.handleHitMask(hit);
				target.getPacketSender().sendMessage("Rimeclaw's frost breath freezes you where you stand!");
				break;
			}
		}
		return true;
	}

	private static void summon(Player target, Npc boss, int type, int count, int hp, int maxHit, int att, int def) {
		for (int i = 0; i < count; i++) {
			int dx = i == 0 ? -2 : i == 1 ? 2 : 0, dy = i == 2 ? 2 : -1;
			NpcHandler.spawnNpc(target, type, boss.absX + dx, boss.absY + dy, boss.heightLevel, 1, hp, maxHit, att, def, true,
					false);
		}
	}

	/** On a boss kill: mark it, tell the player who to report to. */
	static void npcKilled(Player killer, int npcType) {
		int b = bossIndex(npcType);
		if (b < 0) {
			return;
		}
		boolean first = (killer.nytheraBosses & 1 << b) == 0;
		killer.nytheraBosses |= 1 << b;
		killer.getPacketSender().sendMessage("You have slain " + BOSS_NAMES[b] + "!");
		if (first) {
			killer.getPacketSender().sendMessage("Captain Ysolde in Gatehold will want to hear of this.");
		}
	}

	/** Ambient lines for the new folk. */
	static void chatter(java.util.Map<Integer, String[]> chatter, java.util.Map<Integer, Integer> work) {
		chatter.put(YSOLDE, new String[] { "Eyes east, lads.", "Another quiet night. I hate quiet nights.",
				"Keep that gate barred after dark.", "Gatehold stands." });
		chatter.put(PELL, new String[] { "Rope! Buckets! Get your rope and buckets!", "Bargains! Mostly!",
				"Fresh from the cove - don't ask.", "Everything must go! Some of it twice." });
		chatter.put(DORRIN, new String[] { "*clang* *clang*", "Needs more heat.", "Hold still, you stubborn bar...",
				"There. Good steel." });
		chatter.put(TESS, new String[] { "Mind the edge!", "Coal in the north face, iron in the east.",
				"Who moved my cart?", "Rock and sky. That's the life." });
		chatter.put(MORRA, new String[] { "Stew's on!", "Wipe your boots!", "Who ordered the ale?",
				"The Last Hearth - last for a reason." });
		chatter.put(NETTLE, new String[] { "Nothing to see here.", "Fine goods, finer prices.", "Keep your voice down.",
				"Tide's turning. Load faster." });
		work.put(DORRIN, 898); // hammering at the anvil
		work.put(TESS, 625); // swinging a pickaxe
	}
}
