package data.map.buildings;

import client.session.Player;
import data.ItemTable;
import data.enumeration.ItemID;
import data.map.Cube;
import data.multiblocs.MultiBloc;

public class Building {

	private Player player;
	private ItemID itemID;
	private MultiBloc multi;

	/** false : the building isn't ready to be used */
	private boolean isBuild;
	/** Value already worked to build this building (only if isBuild == false) */
	private int alreadyBuild = 0;

	// =========================================================================================================================

	public Building(Player player, ItemID itemID, int x, int y, int z, boolean isBuild) {
		this.player = player;
		this.itemID = itemID;
		this.isBuild = isBuild;

		multi = ItemTable.createBuilding(this);
		multi.setCoords(x, y, z);
	}

	// =========================================================================================================================

	public boolean addAlreadyBuild(int x) {
		return isBuild = (alreadyBuild += x) >= ItemTable.getBuildingTime(itemID);
	}

	// =========================================================================================================================
	// Getters

	public ItemID getItemID() {
		return itemID;
	}

	public Cube getCube() {
		return multi.getCube();
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isBuild() {
		return isBuild;
	}

	public int getAlreadyBuild() {
		return alreadyBuild;
	}

	// =========================================================================================================================

	@Override
	public String toString() {
		return "I'm a building of " + (player == null ? "[null]" : player.getName());
	}
}
