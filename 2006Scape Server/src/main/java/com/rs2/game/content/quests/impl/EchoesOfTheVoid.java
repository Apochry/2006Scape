package com.rs2.game.content.quests.impl;

import static com.rs2.game.content.voidrealm.VoidConstants.*;

import com.rs2.Constants;
import com.rs2.game.content.quests.QuestRewards;
import com.rs2.game.players.Player;

/**
 * Echoes of the Void - part 2 of the Void series, after Curse of Skullbrine.
 *
 * The portal under Skull Rock has started to sing. Seer Ilsabet needs the three splinters of its broken seal -
 * from the weeping eye of the skull, from the grave of the lookout who saw the far shore, and from the shrieking
 * vein in the Dragonvein cavern - to bind them into a Void sigil.
 */
public final class EchoesOfTheVoid {

	public static final String NAME = "Echoes of the Void";

	/* Stages (Player#voidEchoes) */
	public static final int NOT_STARTED = 0, STARTED = 1, SPLINTERS = 2, COMPLETE = 3;

	public static final int QUEST_POINTS = 2;

	private EchoesOfTheVoid() {
	}

	public static boolean canStart(Player player) {
		return player.skullbrine >= CurseOfSkullbrine.COMPLETE;
	}

	public static void showInformation(Player player) {
		for (int i = 8144; i < 8195; i++) {
			player.getPacketSender().sendString("", i);
		}
		player.getPacketSender().sendString("@dre@" + NAME, 8144);
		int line = 8147;
		int stage = player.voidEchoes;
		if (stage == NOT_STARTED) {
			line = write(player, line, "I can start this quest by speaking to the @dre@Ghost of Captain",
					"@dre@Blackbrine@bla@ in the portal chamber beneath Skull Rock.", "", "@dre@Requirements:",
					strike(canStart(player), "Completion of Curse of Skullbrine"),
					"I will need a @dre@spade@bla@ and a @dre@pickaxe@bla@, and should be able to",
					"defend myself against the restless dead (level 10-30).");
		} else {
			line = write(player, line, strike(stage > STARTED, "Captain Blackbrine's ghost says the portal has begun to"),
					strike(stage > STARTED, "sing my name. A seer has come to the island to listen."));
			if (stage == STARTED) {
				line = write(player, line, "", "I should find the seer by the skull's jaw.");
			}
			if (stage >= SPLINTERS) {
				line = write(player, line, "", strike(stage > SPLINTERS, "Seer Ilsabet needs the three splinters of the door's seal:"));
				line = write(player, line, splinter(player, EYE_SPLINTER, "one weeps in the eye of the skull that cries,"),
						splinter(player, GRAVE_SPLINTER, "one sleeps in the grave of the man who saw the far shore,"),
						splinter(player, VEIN_SPLINTER, "one screams in the rock where the dragon-metal grows."));
				if (stage == SPLINTERS && hasAllSplinters(player)) {
					line = write(player, line, "", "I have all three. I should take them to Ilsabet.");
				}
			}
			if (stage >= COMPLETE) {
				line = write(player, line, "", "Ilsabet bound the splinters into a @dre@Void sigil@bla@.", "",
						"@red@QUEST COMPLETE!", "", "The door is still singing. Ilsabet wants to talk.");
			}
		}
		player.getPacketSender().showInterface(8134);
	}

	private static String splinter(Player player, int item, String text) {
		boolean done = player.voidEchoes > SPLINTERS || player.getItemAssistant().playerHasItem(item);
		return strike(done, text);
	}

	public static boolean hasAllSplinters(Player player) {
		return player.getItemAssistant().playerHasItem(EYE_SPLINTER) && player.getItemAssistant().playerHasItem(GRAVE_SPLINTER)
				&& player.getItemAssistant().playerHasItem(VEIN_SPLINTER);
	}

	static String strike(boolean done, String text) {
		return done ? "@str@" + text : text;
	}

	static int write(Player player, int line, String... text) {
		for (String t : text) {
			player.getPacketSender().sendString(t, line++);
		}
		return line;
	}

	public static String questListName(Player player) {
		if (player.voidEchoes == NOT_STARTED) {
			return NAME;
		}
		return (player.voidEchoes >= COMPLETE ? "@gre@" : "@yel@") + NAME;
	}

	public static void complete(Player player) {
		if (player.voidEchoes >= COMPLETE) {
			return;
		}
		player.getItemAssistant().deleteItem(EYE_SPLINTER, 1);
		player.getItemAssistant().deleteItem(GRAVE_SPLINTER, 1);
		player.getItemAssistant().deleteItem(VEIN_SPLINTER, 1);
		player.voidEchoes = COMPLETE;
		player.questPoints += QUEST_POINTS;
		player.getItemAssistant().addOrDropItem(VOID_SIGIL, 1);
		player.getItemAssistant().addOrDropItem(SEERS_LANTERN, 1);
		player.getPlayerAssistant().addSkillXP(3000, Constants.MAGIC);
		player.getPlayerAssistant().addSkillXP(2500, Constants.MINING);
		player.getPlayerAssistant().addSkillXP(2500, Constants.PRAYER);
		QuestRewards.questReward(player, NAME, QUEST_POINTS + " Quest Points", "The Void sigil",
				"Seer's lantern (teleports to Skull Rock)", "3,000 Magic XP", "2,500 Mining XP", "2,500 Prayer XP", VOID_SIGIL);
	}
}
