package com.rs2.game.content.quests.impl;

import static com.rs2.game.content.quests.impl.EchoesOfTheVoid.strike;
import static com.rs2.game.content.quests.impl.EchoesOfTheVoid.write;
import static com.rs2.game.content.voidrealm.VoidConstants.*;

import com.rs2.Constants;
import com.rs2.game.content.quests.QuestRewards;
import com.rs2.game.players.Player;

/**
 * Ashes of the Crown - part 4 of the Void series, the first quest on Nythera.
 *
 * Something crowned is walking in the Scar. Archivist Maelis pieces together the last Royal Chronicle from three
 * scattered pages: the Hollow King was Nythera's own King Aurel, and his heart - the Ember - was cut out and sealed in
 * the Vault beneath the Broken Gate. The player lights the wards in the chronicle's order, defeats the King's Herald,
 * and carries the Ember out. The King speaks through it: he is coming for it.
 */
public final class AshesOfTheCrown {

	public static final String NAME = "Ashes of the Crown";

	/* Stages (Player#voidCrown) */
	public static final int NOT_STARTED = 0, STARTED = 1, PAGES = 2, CHRONICLE = 3, WARDS_LIT = 4, HERALD_SLAIN = 5, HAS_EMBER = 6,
			COMPLETE = 7;

	public static final int QUEST_POINTS = 3;

	/* Player#crownFlags */
	public static final int BROODMOTHER_SLAIN = 1, WAYSTONE_BITS = 8; // bit 8 + n: attuned to waystone n

	private AshesOfTheCrown() {
	}

	public static boolean canStart(Player player) {
		return player.voidVeil >= BeyondTheVeil.COMPLETE;
	}

	public static void showInformation(Player player) {
		for (int i = 8144; i < 8195; i++) {
			player.getPacketSender().sendString("", i);
		}
		player.getPacketSender().sendString("@dre@" + NAME, 8144);
		int line = 8147;
		int stage = player.voidCrown;
		if (stage == NOT_STARTED) {
			line = write(player, line, "I can start this quest by speaking to @dre@Captain Ysolde@bla@ in",
					"Gatehold, on Nythera.", "", "@dre@Requirements:", strike(canStart(player), "Completion of Beyond the Veil"),
					"I should be able to face a Herald of the Hollow King.");
		} else {
			line = write(player, line, strike(stage > STARTED, "Something crowned is walking in the Scar. Ysolde sent me"),
					strike(stage > STARTED, "to @dre@Archivist Maelis@bla@, in the standing stones above the Gate."));
			if (stage >= PAGES) {
				line = write(player, line, "", strike(stage > PAGES, "Maelis needs the three lost pages of the last @dre@Royal"),
						strike(stage > PAGES, "@dre@Chronicle@bla@:"));
				if (stage == PAGES) {
					line = write(player, line,
							strike(player.getItemAssistant().playerHasItem(THANE_PAGE), " - in the Thane's coffer, in the Sunken Barrow"),
							strike(player.getItemAssistant().playerHasItem(SILK_PAGE), " - wrapped in a cocoon in Silkwood"),
							strike(player.getItemAssistant().playerHasItem(NETTLE_PAGE), " - with Sly Nettle, in Smugglers' Cove"));
				}
			}
			if (stage >= CHRONICLE) {
				line = write(player, line, "", strike(stage > CHRONICLE, "The Hollow King was King Aurel. His heart, the @dre@Ember@bla@,"),
						strike(stage > CHRONICLE, "is sealed in the Vault beneath the Broken Gate. The"),
						strike(stage > CHRONICLE, "wards must be lit in the chronicle's order."));
			}
			if (stage >= WARDS_LIT) {
				line = write(player, line, "", strike(stage > WARDS_LIT, "The wards burn. The seal is open, and the @dre@Herald@bla@"),
						strike(stage > WARDS_LIT, "of the Hollow King is waiting beyond it."));
			}
			if (stage >= HERALD_SLAIN) {
				line = write(player, line, "", strike(stage > HERALD_SLAIN, "The Herald is gone. The Ember is on its pedestal."));
			}
			if (stage >= HAS_EMBER) {
				line = write(player, line, "", strike(stage > HAS_EMBER, "I have the Ember. It's beating. Maelis should see it."));
			}
			if (stage >= COMPLETE) {
				line = write(player, line, "", "The Hollow King spoke through the Ember. He is coming",
						"to take it back. The waystones of Nythera answer me now.", "", "@red@QUEST COMPLETE!", "",
						"@dre@To be continued...@bla@ Keep the Ember warm.");
			}
		}
		player.getPacketSender().showInterface(8134);
	}

	public static String questListName(Player player) {
		if (player.voidCrown == NOT_STARTED) {
			return NAME;
		}
		return (player.voidCrown >= COMPLETE ? "@gre@" : "@yel@") + NAME;
	}

	public static void complete(Player player) {
		if (player.voidCrown >= COMPLETE) {
			return;
		}
		player.voidCrown = COMPLETE;
		player.crownFlags |= 1 << WAYSTONE_BITS; // Gatehold's waystone knows you already
		player.questPoints += QUEST_POINTS;
		player.getItemAssistant().addOrDropItem(GATEHOLD_SIGNET, 1);
		player.getPlayerAssistant().addSkillXP(7500, Constants.SLAYER);
		player.getPlayerAssistant().addSkillXP(5000, Constants.MAGIC);
		player.getPlayerAssistant().addSkillXP(5000, Constants.PRAYER);
		QuestRewards.questReward(player, NAME, QUEST_POINTS + " Quest Points", "The waystone network", "Signet of Gatehold",
				"7,500 Slayer XP", "5,000 Magic and Prayer XP", "The Ember (keep it safe)", GATEHOLD_SIGNET);
	}
}
