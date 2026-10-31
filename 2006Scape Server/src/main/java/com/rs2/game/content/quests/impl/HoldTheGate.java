package com.rs2.game.content.quests.impl;

import static com.rs2.game.content.quests.impl.EchoesOfTheVoid.strike;
import static com.rs2.game.content.quests.impl.EchoesOfTheVoid.write;
import static com.rs2.game.content.voidrealm.VoidConstants.*;

import com.rs2.Constants;
import com.rs2.game.content.quests.QuestRewards;
import com.rs2.game.players.Player;

/**
 * Hold the Gate - part 4 of the Void series, the first quest after the fall to Nythera (recommended combat 30+).
 *
 * The Hollowed have been probing Gatehold every night since the rift tore open. Captain Ysolde won't trust a stranger
 * who fell out of the sky until they've stood a watch: hunt the Hollowed with Warden Kael, get iron for Dorrin's
 * spikes, rebuild the barricades on the Scar road, and hold the gate through a raid. At the end Kael admits what her
 * watch saw on the ridge while the raid broke - something tall, and crowned. (That leads into Ashes of the Crown.)
 */
public final class HoldTheGate {

	public static final String NAME = "Hold the Gate";

	/* Stages (Player#voidGate) */
	public static final int NOT_STARTED = 0, SEE_KAEL = 1, HUNT = 2, SPIKES = 3, BARRICADES = 4, RAID = 5, RAID_WON = 6, COMPLETE = 7;

	public static final int QUEST_POINTS = 2, HUNT_KILLS = 6, IRON_ORE = 440, IRON_NEEDED = 8;

	private HoldTheGate() {
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
		int stage = player.voidGate;
		if (stage == NOT_STARTED) {
			line = write(player, line, "I can start this quest by speaking to @dre@Captain Ysolde@bla@ in", "Gatehold, on Nythera.", "",
					"@dre@Requirements:", strike(canStart(player), "Completion of Beyond the Veil"), "@dre@Recommended:@bla@ combat 30+, some food");
		} else {
			line = write(player, line, strike(stage > SEE_KAEL, "The Hollowed probe Gatehold every night. Ysolde wants"),
					strike(stage > SEE_KAEL, "me to stand a watch with @dre@Warden Kael@bla@ at the Scar Watch."));
			if (stage >= HUNT) {
				line = write(player, line, "", strike(stage > HUNT, "Kael wants " + HUNT_KILLS + " Hollowed put down before they reach the"),
						strike(stage > HUNT, "walls. (" + Math.min(player.gateKills, HUNT_KILLS) + " so far.)"));
			}
			if (stage >= SPIKES) {
				line = write(player, line, "", strike(stage > SPIKES, "The barricades on the Scar road are smashed. @dre@Dorrin@bla@"),
						strike(stage > SPIKES, "will forge spikes if I bring him " + IRON_NEEDED + " iron ore (Tess's quarry)."));
			}
			if (stage >= BARRICADES) {
				int fixed = Integer.bitCount(player.gateFlags & 7);
				line = write(player, line, "", strike(stage > BARRICADES, "Repair the three barricades north-east of the Gate."),
						strike(stage > BARRICADES, "(" + fixed + " of 3 done.)"));
			}
			if (stage >= RAID) {
				line = write(player, line, "", strike(stage > RAID, "Tell Ysolde when I'm ready. When the bell rings, hold"),
						strike(stage > RAID, "the barricades until the last of them falls."));
			}
			if (stage >= RAID_WON) {
				line = write(player, line, "", strike(stage > RAID_WON, "The raid is broken. Ysolde will want to hear it from me."));
			}
			if (stage >= COMPLETE) {
				line = write(player, line, "", "Gatehold trusts me now. But Kael's watch saw something", "on the ridge while the raid broke:",
						"tall, and crowned, and watching.", "", "@red@QUEST COMPLETE!");
			}
		}
		player.getPacketSender().showInterface(8134);
	}

	public static String questListName(Player player) {
		return player.voidGate == NOT_STARTED ? NAME : (player.voidGate >= COMPLETE ? "@gre@" : "@yel@") + NAME;
	}

	public static void complete(Player player) {
		if (player.voidGate >= COMPLETE) {
			return;
		}
		player.voidGate = COMPLETE;
		player.questPoints += QUEST_POINTS;
		player.getItemAssistant().addOrDropItem(WARDEN_KITESHIELD, 1);
		player.getPlayerAssistant().addSkillXP(7000, Constants.ATTACK);
		player.getPlayerAssistant().addSkillXP(7000, Constants.STRENGTH);
		player.getPlayerAssistant().addSkillXP(7000, Constants.DEFENCE);
		player.getPlayerAssistant().addSkillXP(3000, Constants.HITPOINTS);
		QuestRewards.questReward(player, NAME, QUEST_POINTS + " Quest Points", "Warden's kiteshield", "7,000 Attack, Strength",
				"and Defence XP", "3,000 Hitpoints XP", "Gatehold's trust", WARDEN_KITESHIELD);
	}
}
