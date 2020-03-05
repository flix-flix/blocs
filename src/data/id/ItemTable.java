package data.id;

import java.util.ArrayList;
import java.util.TreeMap;

import data.Gamer;
import data.map.Cube;
import data.map.MultiCube;
import data.map.buildings.Building;
import data.map.resources.ResourceType;
import data.map.units.Unit;
import environment.textures.TexturePack;
import utils.Utils;
import utils.yaml.YAML;

public class ItemTable {

	protected static TreeMap<Integer, Item> items = new TreeMap<>();

	protected static ArrayList<String> tags = new ArrayList<>();

	// =========================================================================================================================

	public static void init() {
		for (String file : Utils.getFilesName("resources/items"))
			addItem(new Item(YAML.parseFile(file)));
	}

	// =========================================================================================================================

	public static Item get(int itemID) {
		if (!items.containsKey(itemID))
			Utils.debug("Missing ID: " + itemID);
		return items.get(itemID);
	}

	public static void addItem(Item item) {
		items.put(item.id, item);
		tags.add(item.tag);
	}

	// =========================================================================================================================
	// List

	public static ArrayList<String> getItemTagList() {
		return tags;
	}

	public static ArrayList<Integer> getItemIDList() {
		return new ArrayList<Integer>(items.keySet());
	}

	// =========================================================================================================================

	public static ItemType getType(Cube cube) {
		return getType(cube.multicube != null ? cube.multicube.itemID : cube.getItemID());
	}

	public static ItemType getType(int itemID) {
		return get(itemID).type;
	}

	// =========================================================================================================================

	public static Cube create(int itemID) {
		Item item = get(itemID);

		if (item.type == ItemType.MULTICUBE) {
			MultiCube multi = new MultiCube(itemID);

			for (Cube c : item.multi.list)
				multi.add(new Cube((int) c.x, (int) c.y, (int) c.z, c.getItemID()));

			return multi.getCube();
		}

		if (itemID == ItemID.UNIT)
			return new Cube(new Unit(ItemID.UNIT, Gamer.nullGamer, 0, 0, 0));

		if (itemID == ItemID.CASTLE)
			return new Building(Gamer.nullGamer, ItemID.CASTLE, 0, 0, 0, true).getMulti().getCube();

		return new Cube(itemID);
	}

	/** Retruns true if it's a cube for development process */
	public static boolean isDevelopment(int itemID) {
		switch (itemID) {
		case ItemID.EDITOR_PREVIEW:
		case ItemID.TEST:
		case ItemID.TEST_BIG:
		case ItemID.TEST_TRANSPARENT:
		case ItemID.MAGIC_BLOC:
		case ItemID.ERROR:
		case ItemID.INVISIBLE:

			return true;
		default:
			return false;
		}
	}

	// =========================================================================================================================
	// Data

	public static boolean isOpaque(int itemID) {
		return get(itemID).opaque;
	}

	public static boolean isFloor(int itemID) {
		return get(itemID).floor;
	}

	// =========================================================================================================================
	// Multibloc

	public static MultiCube createMultiBloc(int itemID) {
		if (!isMultiBloc(itemID)) {
			Utils.debugBefore(itemID + " isn't a multibloc");
			return null;
		}

		MultiCube multi = new MultiCube(itemID);

		for (int x = 0; x < getXSize(itemID); x++)
			for (int y = 0; y < getYSize(itemID); y++)
				for (int z = 0; z < getZSize(itemID); z++)
					multi.add(new Cube(x, y, z, itemID));

		return multi;
	}

	public static boolean isMultiBloc(int itemID) {
		return get(itemID).type == ItemType.MULTIBLOC;
	}

	public static int getXSize(int itemID) {
		return isMultiBloc(itemID) ? get(itemID).sizeX : 1;
	}

	public static int getYSize(int itemID) {
		return isMultiBloc(itemID) ? get(itemID).sizeY : 1;
	}

	public static int getZSize(int itemID) {
		return isMultiBloc(itemID) ? get(itemID).sizeZ : 1;
	}

	// =========================================================================================================================
	// Mining

	public static int getMiningTime(int itemID) {
		return get(itemID).miningTime;
	}

	public static int getNumberOfMiningSteps() {
		return TexturePack.nbAnim;
	}

	// =========================================================================================================================
	// Buildings

	public static int getBuildingTime(int itemID) {
		return get(itemID).buildingTime;
	}

	// =========================================================================================================================
	// Resources

	public static boolean isResource(int itemID) {
		return getResourceType(itemID) != null;
	}

	public static ResourceType getResourceType(int itemID) {
		return get(itemID).resourceType;
	}
}
