package com.rs2.game.content.quests.impl;

import static com.rs2.game.content.quests.impl.EchoesOfTheVoid.strike;
import static com.rs2.game.content.quests.impl.EchoesOfTheVoid.write;
import static com.rs2.game.content.voidrealm.VoidConstants.*;

import com.rs2.Constants;
import com.rs2.game.content.quests.QuestRewards;
import com.rs2.game.players.Player;

/**
 * Heartforged - part 6 of the Void series (recommended combat 50+).
 *
 * The Hollow King is coming for his heart. Maelis's books say a thing with a heart can be killed - and the only blade
 * that can reach a heart in the dark is one forged around it. Dorrin needs starmetal from the star that fell with you
 * and a Rimeheart from the Frostfang Caves to quench it. While he forges, the King's last knight comes for the Ember.
 */
public final class Heartforged {

	public static final String NAME = "Heartforged";

	/* Stages (Player#voidForge) */
	public static final int NOT_STARTED = 0, STARTED = 1, MATERIALS = 2, FORGING = 3, KNIGHT_SLAIN = 4, COMPLETE = 5;
	/* Player#forgeFlags */
	public static final int SENTINEL_SLAIN = 1;

	public static final int QUEST_POINTS = 3, MINING_LEVEL = 30;

	private Heartforged() {
	}

	public static boolean canStart(Player player) {
		return player.voidCrown >= AshesOfTheCrown.COMPLETE;
	}

	public static void showInformation(Player player) {
		for (int i = 8144; i < 8195; i++) {
			player.getPacketSender().sendString("", i);
		}
		player.getPacketSender().sendString("@dre@" + NAME, 8144);
		int line = 8147;
		int stage = player.voidForge;
		if (stage == NOT_STARTED) {
			line = write(player, line, "I can start this quest by speaking to @dre@Archivist Maelis@bla@ in", "the standing stones above the Gate.",
					"", "@dre@Requirements:", strike(canStart(player), "Completion of Ashes of the Crown"),
					strike(player.getPlayerAssistant().getLevelForXP(player.playerXP[Constants.MINING]) >= MINING_LEVEL,
							"Level " + MINING_LEVEL + " Mining"),
					"@dre@Recommended:@bla@ combat 50+, food");
		} else {
			line = write(player, line, strike(stage > STARTED, "Maelis says the only blade that can reach the Hollow"),
					strike(stage > STARTED, "King is one forged around his own heart. @dre@Dorrin@bla@ is the smith."));
			if (stage >= MATERIALS) {
				boolean metal = player.getItemAssistant().playerHasItem(STARMETAL), rime = player.getItemAssistant().playerHasItem(RIMEHEART);
				line = write(player, line, "", strike(stage > MATERIALS, "Dorrin needs:"),
						strike(stage > MATERIALS || metal, " - starmetal, from the fallen star in Starfall Crater"),
						strike(stage > MATERIALS || rime, " - a Rimeheart, from the rime shrine in the Frostfang"),
						strike(stage > MATERIALS || rime, "   Caves, off the frozen hall"));
			}
			if (stage >= FORGING) {
				line = write(player, line, "", strike(stage > FORGING, "While Dorrin worked, the King's knight came for the"),
						strike(stage > FORGING, "Ember. Sir Vaelric must not leave Gatehold."));
			}
			if (stage >= KNIGHT_SLAIN) {
				line = write(player, line, "", strike(stage > KNIGHT_SLAIN, "Vaelric is dust. Dorrin can finish the blade."));
			}
			if (stage >= COMPLETE) {
				line = write(player, line, "", "The Ember beats in the hilt of the @dre@Emberbrand@bla@.", "Vaelric's last words: the King will open the Scar",
						"himself, and come through it.", "", "@red@QUEST COMPLETE!");
			}
		}
		player.getPacketSender().showInterface(8134);
	}

	public static String questListName(Player player) {
		return player.voidForge == NOT_STARTED ? NAME : (player.voidForge >= COMPLETE ? "@gre@" : "@yel@") + NAME;
	}

	public static void complete(Player player) {
		if (player.voidForge >= COMPLETE) {
			return;
		}
		player.voidForge = COMPLETE;
		player.questPoints += QUEST_POINTS;
		player.getItemAssistant().addOrDropItem(EMBERBRAND, 1);
		player.getPlayerAssistant().addSkillXP(10000, Constants.ATTACK);
		player.getPlayerAssistant().addSkillXP(10000, Constants.STRENGTH);
		player.getPlayerAssistant().addSkillXP(10000, Constants.DEFENCE);
		player.getPlayerAssistant().addSkillXP(5000, Constants.SMITHING);
		player.getPlayerAssistant().addSkillXP(4000, Constants.MINING);
		QuestRewards.questReward(player, NAME, QUEST_POINTS + " Quest Points", "The Emberbrand longsword", "10,000 Attack, Strength",
				"and Defence XP", "5,000 Smithing XP", "4,000 Mining XP", EMBERBRAND);
	}
}
