package data.id;

import java.util.ArrayList;
import java.util.TreeMap;

import data.Gamer;
import data.map.Cube;
import data.map.buildings.Building;
import data.map.multiblocs.E;
import data.map.multiblocs.MultiBloc;
import data.map.multiblocs.Tree;
import data.map.resources.ResourceType;
import data.map.units.Unit;
import environment.textures.TexturePack;
import utils.Utils;
import utils.yaml.YAML;

public class ItemTable {

	static TreeMap<Integer, Item> items = new TreeMap<>();

	static ArrayList<String> tags = new ArrayList<>();

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

	public static String getType(int itemID) {
		return get(itemID).type;
	}

	// =========================================================================================================================

	public static Cube create(int itemID) {
		Cube cube = new Cube(itemID);

		if (itemID == ItemID.TREE)
			cube = new Tree().getCube();
		else if (itemID == ItemID.E)
			cube = new E().getCube();
		else if (itemID == ItemID.UNIT)
			cube = new Cube(new Unit(ItemID.UNIT, Gamer.nullGamer, 0, 0, 0));
		else if (itemID == ItemID.CASTLE)
			cube = createBuilding(new Building(Gamer.nullGamer, ItemID.CASTLE, 0, 0, 0, true)).getCube();

		return cube;
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

			return true;
		default:
			return false;
		}
	}

	// =========================================================================================================================
	// Multibloc

	public static MultiBloc createBuilding(Building build) {
		MultiBloc multi = createMulti(build.getItemID());
		multi.setBuild(build);
		return multi;
	}

	public static MultiBloc createMulti(int itemID) {
		if (!isMultiBloc(itemID)) {
			Utils.debugBefore(itemID + " isn't a multibloc");
			return null;
		}

		MultiBloc multi = new MultiBloc(itemID);

		for (int x = 0; x < getXSize(itemID); x++)
			for (int y = 0; y < getYSize(itemID); y++)
				for (int z = 0; z < getZSize(itemID); z++)
					multi.add(new Cube(x, y, z, itemID));

		return multi;
	}

	public static boolean isMultiBloc(int itemID) {
		return get(itemID).multibloc;
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
	// Build

	public static int getBuildingTime(int itemID) {
		return get(itemID).buildingTime;
	}

	// =========================================================================================================================
	// Resource

	public static boolean isResource(int itemID) {
		return getResourceType(itemID) != null;
	}

	public static ResourceType getResourceType(int itemID) {
		return get(itemID).resourceType;
	}
}
