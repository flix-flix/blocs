package data.map.buildings;

import client.session.Player;
import data.ItemTable;
import data.enumeration.ItemID;
import data.map.Cube;

public class Building extends Cube {

	private Player player;

	/** false : the building isn't ready to be used */
	private boolean isBuild;
	/** Value already worked to build this building (only if isBuild == false) */
	private int alreadyBuild = 0;

	// =========================================================================================================================

	public Building(Player player, double x, double y, double z, double sizeX, double sizeY, double sizeZ,
			ItemID itemID, boolean isBuild) {
		super(x, y, z, sizeX, sizeY, sizeZ, itemID);
		build = this;

		this.player = player;
		this.isBuild = isBuild;
	}

	public Building(Player player, double x, double y, double z, double sizeX, double sizeY, double sizeZ,
			ItemID itemID) {
		this(player, x, y, z, sizeX, sizeY, sizeZ, itemID, true);
	}

	public Building(Player player, double x, double y, double z, ItemID itemID) {
		this(player, x, y, z, 1, 1, 1, itemID);
	}

	// =========================================================================================================================

	public Player getPlayer() {
		return player;
	}

	public boolean isBuild() {
		return isBuild;
	}

	public boolean addAlreadyBuild(int x) {
		alreadyBuild += x;
		return isBuild = alreadyBuild >= ItemTable.getBuildingTime(itemID);
	}

	public int getAlreadyBuild() {
		return alreadyBuild;
	}

	// =========================================================================================================================

	@Override
	public String toString() {
		return "I'm a building of " + player.getName();
	}
}
