package data.id;

import java.util.ArrayList;
import java.util.TreeMap;

import data.map.Cube;
import data.map.buildings.Building;
import data.map.multiblocs.MultiBloc;
import data.map.resources.ResourceType;
import environment.textures.TexturePack;
import utils.FlixBlocksUtils;
import utils.yaml.YAML;

public class ItemTable {

	static TreeMap<Integer, Item> items = new TreeMap<>();

	static ArrayList<String> tags = new ArrayList<>();

	// =========================================================================================================================

	public static void init() {
		for (String file : FlixBlocksUtils.getFilesName("resources/items"))
			addItem(new Item(YAML.parseFile(file)));
	}

	public static void setTexturePack(TexturePack texturePack) {
		for (int i : items.keySet())
			if (texturePack.yamls.get(i) != null)
				items.get(i).setTexture(texturePack.yamls.get(i));
	}

	// =========================================================================================================================

	public static Item get(int itemID) {
		if (!items.containsKey(itemID))
			System.err.println("[ItemTable] Missing ID: " + itemID);
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
	// Multibloc

	public static MultiBloc createBuilding(Building build) {
		MultiBloc multi = createMulti(build.getItemID());
		multi.setBuild(build);
		return multi;
	}

	public static MultiBloc createMulti(int itemID) {
		if (!isMultiBloc(itemID)) {
			FlixBlocksUtils.debugBefore(itemID + " isn't a multibloc");
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
