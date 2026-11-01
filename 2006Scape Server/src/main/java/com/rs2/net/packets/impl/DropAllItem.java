package com.rs2.net.packets.impl;

import com.rs2.game.players.Player;
import com.rs2.net.Packet;
import com.rs2.net.packets.PacketType;

/**
 * Drop all (opcode 89, the inventory's "Drop all" option; not in the stock protocol): drops every inventory slot
 * holding this item, one slot at a time through {@link DropItem#drop}, so each obeys the usual rules. It stops at the
 * first slot that stays put (somewhere you can't drop, an item that asks to be destroyed instead, a second pet...).
 */
public class DropAllItem implements PacketType {

	@Override
	public void processPacket(Player player, Packet packet) {
		int itemId = packet.readUnsignedWord();
		for (int slot = 0; slot < player.playerItems.length; slot++) {
			if (player.playerItems[slot] == itemId + 1 && player.playerItemsN[slot] > 0) {
				DropItem.drop(player, itemId, slot);
				if (player.playerItems[slot] == itemId + 1) {
					return;
				}
			}
		}
	}
}
