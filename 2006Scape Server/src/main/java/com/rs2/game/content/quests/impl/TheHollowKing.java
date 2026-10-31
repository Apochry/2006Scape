package com.rs2.game.content.quests.impl;

import static com.rs2.game.content.quests.impl.EchoesOfTheVoid.strike;
import static com.rs2.game.content.quests.impl.EchoesOfTheVoid.write;
import static com.rs2.game.content.voidrealm.VoidConstants.*;

import com.rs2.Constants;
import com.rs2.game.content.quests.QuestRewards;
import com.rs2.game.players.Player;

/**
 * The Hollow King - part 7 of the Void series, its finale (recommended combat 60+).
 *
 * The Scar is splitting: the King is opening a road for himself. Warden Kael raises the alarm; Oren, who knows the
 * void's roads, opens the rift for whoever carries the Emberbrand. On the platform where it all began, the player
 * fights the Hollow King - and with his own heart in the blade, he can be hurt. Beaten, the crown lets go of him and
 * Aurel is himself again, long enough to warn that what crowned him is still out there, between the stars.
 */
public final class TheHollowKing {

	public static final String NAME = "The Hollow King";

	/* Stages (Player#voidKing) */
	public static final int NOT_STARTED = 0, STARTED = 1, SEE_OREN = 2, ROAD_OPEN = 3, KING_FALLEN = 4, COMPLETE = 5;

	public static final int QUEST_POINTS = 4;

	private TheHollowKing() {
	}

	public static boolean canStart(Player player) {
		return player.voidForge >= Heartforged.COMPLETE;
	}

	public static void showInformation(Player player) {
		for (int i = 8144; i < 8195; i++) {
			player.getPacketSender().sendString("", i);
		}
		player.getPacketSender().sendString("@dre@" + NAME, 8144);
		int line = 8147;
		int stage = player.voidKing;
		if (stage == NOT_STARTED) {
			line = write(player, line, "I can start this quest by speaking to @dre@Warden Kael@bla@ at the", "Scar Watch.", "",
					"@dre@Requirements:", strike(canStart(player), "Completion of Heartforged"),
					"@dre@Recommended:@bla@ combat 60+, good food, the Emberbrand");
		} else {
			line = write(player, line, strike(stage > STARTED, "The Scar is splitting open. Kael says the King is cutting"),
					strike(stage > STARTED, "a road to Nythera. @dre@Maelis@bla@ will know what to do."));
			if (stage >= SEE_OREN) {
				line = write(player, line, "", strike(stage > SEE_OREN, "We go to him first. @dre@Oren@bla@, at his camp by the crater,"),
						strike(stage > SEE_OREN, "knows the void's roads."));
			}
			if (stage >= ROAD_OPEN) {
				line = write(player, line, "", strike(stage > ROAD_OPEN, "Oren has opened the rift in Starfall Crater. With the"),
						strike(stage > ROAD_OPEN, "Emberbrand in my hand, it will take me to the King."));
			}
			if (stage >= KING_FALLEN) {
				line = write(player, line, "", strike(stage > KING_FALLEN, "The crown let go of him. Aurel is free. I should tell"),
						strike(stage > KING_FALLEN, "Captain Ysolde."));
			}
			if (stage >= COMPLETE) {
				line = write(player, line, "", "Nythera's last king is at rest. But the thing that", "crowned him is still out there, between the stars.",
						"Its gate stands open on the void platform.", "", "@red@QUEST COMPLETE!");
			}
		}
		player.getPacketSender().showInterface(8134);
	}

	public static String questListName(Player player) {
		return player.voidKing == NOT_STARTED ? NAME : (player.voidKing >= COMPLETE ? "@gre@" : "@yel@") + NAME;
	}

	public static void complete(Player player) {
		if (player.voidKing >= COMPLETE) {
			return;
		}
		player.voidKing = COMPLETE;
		player.questPoints += QUEST_POINTS;
		player.getItemAssistant().addOrDropItem(CROWN_OF_AUREL, 1);
		for (int skill : new int[] { Constants.ATTACK, Constants.STRENGTH, Constants.DEFENCE, Constants.HITPOINTS }) {
			player.getPlayerAssistant().addSkillXP(15000, skill);
		}
		player.getPlayerAssistant().addSkillXP(8000, Constants.PRAYER);
		QuestRewards.questReward(player, NAME, QUEST_POINTS + " Quest Points", "Crown of Aurel", "15,000 Attack, Strength,",
				"Defence and Hitpoints XP", "8,000 Prayer XP", "The Starless Gate is open...", CROWN_OF_AUREL);
	}
}
