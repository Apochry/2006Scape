package com.rs2.game.content.quests.impl;

import com.rs2.Constants;
import com.rs2.game.content.quests.QuestAssistant;
import com.rs2.game.content.quests.QuestRewards;
import com.rs2.game.content.skullbrine.SkullbrineConstants;
import com.rs2.game.players.Player;

/**
 * Curse of Skullbrine.
 *
 * Bosun Grimsby in Port Sarim asks you to find his brother's ship, the Drowned Maiden, lost at Skullbrine
 * Isle. Its crew have risen as cursed pirates since Captain Blackbrine stole a pearl from a portal in the
 * caves beneath the island. Recover the captain's log, take the key from his cursed first mate, open the
 * captain's chest and return the Void pearl to the portal.
 */
public final class CurseOfSkullbrine {

	public static final String NAME = "Curse of Skullbrine";

	/* Stages (Player#skullbrine) */
	public static final int NOT_STARTED = 0, STARTED = 1, FIND_LOG = 2, FIND_KEY = 3, RETURN_PEARL = 4,
			PEARL_RETURNED = 5, COMPLETE = 6;

	public static final int QUEST_POINTS = 2;

	private CurseOfSkullbrine() {
	}

	public static void showInformation(Player player) {
		for (int i = 8144; i < 8195; i++) {
			player.getPacketSender().sendString("", i);
		}
		player.getPacketSender().sendString("@dre@" + NAME, 8144);
		int line = 8147;
		int stage = player.skullbrine;
		if (stage == NOT_STARTED) {
			line = write(player, line, "I can start this quest by speaking to @dre@Bosun Grimsby@bla@ on",
					"the docks of @dre@Port Sarim@bla@.", "", "@dre@Requirements:", "None - suitable for new adventurers.",
						"Bring some food: the cursed crew bite back (level 5-20).");
		} else {
			line = write(player, line, strike(stage > STARTED, "Bosun Grimsby asked me to find out what happened to"),
					strike(stage > STARTED, "his brother's ship, the Drowned Maiden, at Skullbrine Isle."));
			if (stage == STARTED) {
				line = write(player, line, "", "He will sail me there from Port Sarim.");
			}
			if (stage >= FIND_LOG) {
				line = write(player, line, "", strike(stage > FIND_LOG, "Mad Pete, the ship's cook, told me the crew were cursed"),
						strike(stage > FIND_LOG, "after Captain Blackbrine stole a pearl from a humming door."),
						strike(stage > FIND_LOG, "I should search the captain's remains by the wreck in the cove."));
			}
			if (stage >= FIND_KEY) {
				line = write(player, line, "", strike(stage > FIND_KEY, "The captain's log says he locked the pearl in his chest"),
						strike(stage > FIND_KEY, "inside Skull Rock and hung the key around the neck of"),
						strike(stage > FIND_KEY, "his first mate, 'Smiling' Sam."));
			}
			if (stage >= RETURN_PEARL) {
				line = write(player, line, "", strike(stage > RETURN_PEARL, "I have the Void pearl. I must return it to the portal"),
						strike(stage > RETURN_PEARL, "deep inside Skull Rock to lift the curse."));
			}
			if (stage == PEARL_RETURNED) {
				line = write(player, line, "", "I returned the pearl. I should speak to the captain's ghost.");
			}
			if (stage >= COMPLETE) {
				line = write(player, line, "", "@red@QUEST COMPLETE!", "",
						"The portal still hums. Something on the other side knows", "we are here...");
			}
		}
		player.getPacketSender().showInterface(8134);
	}

	private static String strike(boolean done, String text) {
		return done ? "@str@" + text : text;
	}

	private static int write(Player player, int line, String... text) {
		for (String t : text) {
			player.getPacketSender().sendString(t, line++);
		}
		return line;
	}

	/** Quest list colour: red = not started, yellow = in progress, green = complete. */
	public static String questListName(Player player) {
		if (player.skullbrine == NOT_STARTED) {
			return NAME;
		}
		return (player.skullbrine >= COMPLETE ? "@gre@" : "@yel@") + NAME;
	}

	/** Reading Blackbrine's log advances the quest. */
	public static void readLog(Player player) {
		player.getDialogueHandler().sendStatement("~ Day 9 ~ The pearl sings to me at night. The men grow pale.",
				"~ Day 11 ~ Sam tried to take it. I locked it in my chest in the",
				"hold beneath the hill, and hung the key round HIS neck, so",
				"I'd always know where it was. ~ Day 12 ~ The dead are rising.");
		player.nextChat = 0;
		if (player.skullbrine == FIND_LOG) {
			player.skullbrine = FIND_KEY;
			QuestAssistant.sendStages(player);
			player.getPacketSender().sendMessage("The last page reads: 'Put it back through the door. Only then will we rest.'");
		}
	}

	public static void complete(Player player) {
		if (player.skullbrine >= COMPLETE) {
			return;
		}
		player.skullbrine = COMPLETE;
		player.questPoints += QUEST_POINTS;
		player.getItemAssistant().addOrDropItem(995, 3000);
		player.getItemAssistant().addOrDropItem(2651, 1); // pirate's hat
		player.getPlayerAssistant().addSkillXP(2500, Constants.ATTACK);
		player.getPlayerAssistant().addSkillXP(2500, Constants.STRENGTH);
		QuestRewards.questReward(player, NAME, QUEST_POINTS + " Quest Points", "2,500 Attack XP", "2,500 Strength XP",
				"3,000 Coins", "A pirate's hat", "Access to the Void portal (soon...)", SkullbrineConstants.VOID_PEARL);
	}
}
