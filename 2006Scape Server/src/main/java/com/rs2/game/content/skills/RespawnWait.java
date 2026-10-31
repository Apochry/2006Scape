package com.rs2.game.content.skills;

import com.rs2.GameEngine;
import com.rs2.event.CycleEvent;
import com.rs2.event.CycleEventContainer;
import com.rs2.event.CycleEventHandler;
import com.rs2.game.players.Player;

/**
 * Keeps a player harvesting a rock or tree after it runs out: they wait by it and start again as soon as
 * it grows back, as long as they still have inventory room and haven't moved or started anything else.
 */
public final class RespawnWait {

	private RespawnWait() {
	}

	/** Only waits longer than this many ticks get a "you wait" message (copper and tin are back in a tick). */
	private static final int QUIET_TICKS = 5;

	/**
	 * @param x, y    the depleted resource (a temporary object in the object manager until it respawns)
	 * @param ticks   how long it takes to respawn
	 * @param restart starts harvesting it again
	 */
	public static void start(Player player, int x, int y, int ticks, String message, Runnable restart) {
		if (player.getItemAssistant().freeSlots() < 1 || player.disconnected) {
			return;
		}
		if (ticks > QUIET_TICKS && message != null) {
			player.getPacketSender().sendMessage(message);
		}
		final int startX = player.absX, startY = player.absY, height = player.heightLevel;
		final int token = ++player.respawnWaitToken;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (interrupted(player, token, startX, startY, height)) {
					container.stop();
					return;
				}
				if (!GameEngine.objectManager.objectExists(x, y)) {
					container.stop();
					restart.run();
				}
			}

			@Override
			public void stop() {
			}
		}, 1);
	}

	private static boolean interrupted(Player p, int token, int x, int y, int height) {
		return p.disconnected || p.respawnWaitToken != token || p.absX != x || p.absY != y || p.heightLevel != height
				|| p.isMining || p.isWoodcutting || p.isSmithing || p.isSmelting || p.isBanking || p.isFiremaking
				|| p.playerIsFletching || p.npcIndex > 0 || p.playerIndex > 0 || p.isDead
				|| p.getItemAssistant().freeSlots() < 1;
	}
}
