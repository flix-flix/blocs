package data.map.buildings;

import client.session.Player;
import data.enumeration.ItemID;
import data.map.Cube;

public class Building extends Cube {

	Player player;

	// =========================================================================================================================

	public Building(Player player, double x, double y, double z, double sizeX, double sizeY, double sizeZ,
			ItemID itemID) {
		super(x, y, z, sizeX, sizeY, sizeZ, itemID);
		build = this;

		this.player = player;
	}

	public Building(Player player, double x, double y, double z, ItemID itemID) {
		this(player, x, y, z, 1, 1, 1, itemID);
	}

	// =========================================================================================================================

	public Player getPlayer() {
		return player;
	}
	
	// =========================================================================================================================
	
	@Override
	public String toString() {
		return "I'm a building of "+player.getName();
		}
}
