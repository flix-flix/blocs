package data.map.buildings;

import java.io.Serializable;
import java.util.HashMap;

import data.id.ItemID;
import data.id.ItemTable;
import data.map.Cube;
import data.map.multiblocs.MultiBloc;
import data.map.resources.Resource;
import data.map.resources.ResourceType;
import server.game.Player;

public class Building implements Serializable {
	private static final long serialVersionUID = -1662130293196731466L;

	private Player player;
	private ItemID itemID;
	private MultiBloc multi;

	/** false : the building isn't ready to be used */
	private boolean isBuild;
	/** Value already worked to build this building (only if isBuild == false) */
	private int alreadyBuild = 0;

	private HashMap<ResourceType, Resource> stocks = new HashMap<>();

	// =========================================================================================================================

	public Building(Player player, ItemID itemID, int x, int y, int z, boolean isBuild) {
		this.player = player;
		this.itemID = itemID;
		this.isBuild = isBuild;

		multi = ItemTable.createBuilding(this);
		multi.setCoords(x, y, z);

		addStock(new Resource(ResourceType.WOOD, 0, 100));
		addStock(new Resource(ResourceType.WATER, 0, 100));
		addStock(new Resource(ResourceType.STONE, 0, 100));
	}

	// =========================================================================================================================
	// Build

	public boolean addAlreadyBuild(int x) {
		return isBuild = (alreadyBuild += x) >= ItemTable.getBuildingTime(itemID);
	}

	// =========================================================================================================================
	// Stocks

	public boolean addToStock(Resource res) {
		if (!hasStock(res))
			return false;
		return stocks.get(res.getType()).addFrom(res);
	}

	public boolean addToStock(Resource res, int quantity) {
		if (!hasStock(res))
			return false;
		return stocks.get(res.getType()).addFrom(res, quantity);
	}

	public void addStock(Resource res) {
		stocks.put(res.getType(), res);
	}

	public boolean canStock(Resource res) {
		if (res == null || !isBuild || !hasStock(res))
			return false;

		return !stocks.get(res.getType()).isFull();
	}

	public Resource getStocks(ResourceType type) {
		return stocks.get(type);
	}

	public boolean hasStock(ResourceType type) {
		return stocks.containsKey(type);
	}

	public boolean hasStock(Resource res) {
		if (res == null)
			return false;
		return hasStock(res.getType());
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
