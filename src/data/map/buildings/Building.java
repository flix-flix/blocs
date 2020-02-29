package data.map.buildings;

import java.io.Serializable;
import java.util.HashMap;

import data.Gamer;
import data.id.ItemTable;
import data.id.ItemTableClient;
import data.map.Coord;
import data.map.Cube;
import data.map.multiblocs.MultiBloc;
import data.map.resources.Resource;
import data.map.resources.ResourceType;

public class Building implements Serializable {
	private static final long serialVersionUID = -1662130293196731466L;

	private static final int NO_ID = -1;
	private static int nextID = 0;

	private int id = NO_ID;
	public Coord coord;
	private Gamer gamer;
	private int itemID;
	private MultiBloc multi;

	/** false : the building isn't ready to be used */
	private boolean isBuild;
	/** Value already worked to build this building (only if isBuild == false) */
	private int alreadyBuild = 0;

	private HashMap<ResourceType, Resource> stocks = new HashMap<>();

	// =========================================================================================================================

	public Building(Gamer gamer, int itemID, int x, int y, int z, boolean isBuild) {
		id = nextID++;

		coord = new Coord(x, y, z);
		this.gamer = gamer;
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

	public int getItemID() {
		return itemID;
	}

	public MultiBloc getMulti() {
		return multi;
	}

	public Cube getCube() {
		return multi.getCube();
	}

	public Gamer getGamer() {
		return gamer;
	}

	public boolean isBuild() {
		return isBuild;
	}

	public int getAlreadyBuild() {
		return alreadyBuild;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return ItemTableClient.getName(itemID);
	}

	// =========================================================================================================================
	// Setters

	public void setGamer(Gamer gamer) {
		this.gamer = gamer;
	}

	// =========================================================================================================================

	@Override
	public String toString() {
		return "Player: " + (gamer == null ? "[null]" : gamer.getName()) + " ID: " + id;
	}
}
