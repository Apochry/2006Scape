package com.rs2.game.content.quests.impl;

import static com.rs2.game.content.voidrealm.VoidConstants.*;

import com.rs2.Constants;
import com.rs2.game.content.quests.QuestRewards;
import com.rs2.game.players.Player;

/**
 * Beyond the Veil - part 3 of the Void series.
 *
 * With the Void sigil, the portal opens onto the void between worlds, where the Hollow King waits. He cannot be
 * beaten - not yet. He shatters the platform and lets the player fall to Nythera, a continent on the other side,
 * where Oren the Wanderer finds them. The series continues from there.
 */
public final class BeyondTheVeil {

	public static final String NAME = "Beyond the Veil";

	/* Stages (Player#voidVeil) */
	public static final int NOT_STARTED = 0, STARTED = 1, IN_VOID = 2, FELL = 3, COMPLETE = 4;

	public static final int QUEST_POINTS = 3;

	private BeyondTheVeil() {
	}

	public static boolean canStart(Player player) {
		return player.voidEchoes >= EchoesOfTheVoid.COMPLETE;
	}

	public static void showInformation(Player player) {
		for (int i = 8144; i < 8195; i++) {
			player.getPacketSender().sendString("", i);
		}
		player.getPacketSender().sendString("@dre@" + NAME, 8144);
		int line = 8147;
		int stage = player.voidVeil;
		if (stage == NOT_STARTED) {
			line = EchoesOfTheVoid.write(player, line, "I can start this quest by speaking to @dre@Seer Ilsabet@bla@ by",
					"the jaw of Skull Rock, on Skullbrine Isle.", "", "@dre@Requirements:",
					EchoesOfTheVoid.strike(canStart(player), "Completion of Echoes of the Void"),
					"I must be ready to face something far beyond me.");
		} else {
			line = EchoesOfTheVoid.write(player, line,
					EchoesOfTheVoid.strike(stage > STARTED, "Ilsabet has seen a king made of nothing beyond the door."),
					EchoesOfTheVoid.strike(stage > STARTED, "I should use the @dre@Void sigil@bla@ on the portal beneath Skull Rock."));
			if (stage >= IN_VOID) {
				line = EchoesOfTheVoid.write(player, line, "",
						EchoesOfTheVoid.strike(stage > IN_VOID, "Beyond the door there was only a platform hanging in"),
						EchoesOfTheVoid.strike(stage > IN_VOID, "nothing - and the @dre@Hollow King@bla@, waiting on it."));
				if (stage == IN_VOID) {
					line = EchoesOfTheVoid.write(player, line, "I must go back through the portal and face him.");
				}
			}
			if (stage >= FELL) {
				line = EchoesOfTheVoid.write(player, line, "",
						EchoesOfTheVoid.strike(stage > FELL, "He couldn't be harmed. He shattered the platform and let"),
						EchoesOfTheVoid.strike(stage > FELL, "me fall... into a smoking crater on another world."));
				if (stage == FELL) {
					line = EchoesOfTheVoid.write(player, line, "Someone is camped by the crater's rim.");
				}
			}
			if (stage >= COMPLETE) {
				line = EchoesOfTheVoid.write(player, line, "", "Oren the Wanderer told me this land is called",
						"@dre@Nythera@bla@, and that the Hollow King never lets anyone go.", "", "@red@QUEST COMPLETE!", "",
						"@dre@To be continued...@bla@ The old road leads inland, past the",
						"Broken Gate. Oren says there are people who will want to meet me.");
			}
		}
		player.getPacketSender().showInterface(8134);
	}

	public static String questListName(Player player) {
		if (player.voidVeil == NOT_STARTED) {
			return NAME;
		}
		return (player.voidVeil >= COMPLETE ? "@gre@" : "@yel@") + NAME;
	}

	public static void complete(Player player) {
		if (player.voidVeil >= COMPLETE) {
			return;
		}
		player.voidVeil = COMPLETE;
		player.questPoints += QUEST_POINTS;
		player.getItemAssistant().addOrDropItem(HOLLOW_CAPE, 1);
		player.getItemAssistant().addOrDropItem(HOLLOWFANG, 1);
		player.getPlayerAssistant().addSkillXP(6000, Constants.ATTACK);
		player.getPlayerAssistant().addSkillXP(6000, Constants.STRENGTH);
		player.getPlayerAssistant().addSkillXP(6000, Constants.DEFENCE);
		player.getPlayerAssistant().addSkillXP(4000, Constants.HITPOINTS);
		QuestRewards.questReward(player, NAME, QUEST_POINTS + " Quest Points", "Hollow-touched cape",
				"Hollowfang scimitar", "6,000 Atk, Str and Def XP", "4,000 Hitpoints XP",
				"Access to Nythera", HOLLOW_CAPE);
	}
}
