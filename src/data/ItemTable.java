package data;

import client.textures.TexturePack;
import data.enumeration.ItemID;
import data.map.Cube;
import data.map.buildings.Building;
import data.map.resources.Resource;
import data.map.resources.ResourceType;
import data.multiblocs.MultiBloc;
import utils.FlixBlocksUtils;

public class ItemTable {

	// =========================================================================================================================
	// Graphic Engine

	public static boolean isOpaque(ItemID itemID) {
		switch (itemID) {
		case GLASS:
			return false;
		case GLASS_GRAY:
			return false;
		case GLASS_RED:
			return false;
		case WATER:
			return false;
		case TEST_TRANSPARENT:
			return false;

		case UNIT:
			return false;

		default:
			return true;
		}
	}

	public static boolean drawContour(ItemID itemID) {
		switch (itemID) {
		case WATER:
		case CASTLE:
			return false;

		default:
			return true;
		}
	}

	// =========================================================================================================================
	// Multi blocs

	public static MultiBloc createBuilding(Building build) {
		if (!isMultiBloc(build.getItemID())) {
			FlixBlocksUtils.debugBefore(build.getItemID() + " isn't a multibloc");
			return null;
		}

		MultiBloc multi = new MultiBloc();

		for (int x = 0; x < getXSize(build.getItemID()); x++)
			for (int y = 0; y < getYSize(build.getItemID()); y++)
				for (int z = 0; z < getZSize(build.getItemID()); z++) {
					Cube c = new Cube(x, y, z, build.getItemID());
					c.build = build;
					multi.add(c);
				}

		return multi;
	}

	public static boolean isMultiBloc(ItemID itemID) {
		switch (itemID) {
		case CASTLE:
			return true;
		default:
			return false;
		}
	}

	public static int getXSize(ItemID itemID) {
		switch (itemID) {
		case CASTLE:
			return 3;
		default:
			return 1;
		}
	}

	public static int getYSize(ItemID itemID) {
		switch (itemID) {
		case CASTLE:
			return 2;
		default:
			return 1;
		}
	}

	public static int getZSize(ItemID itemID) {
		switch (itemID) {
		case CASTLE:
			return 2;
		default:
			return 1;
		}
	}

	// =========================================================================================================================
	// Mining

	public static int getMiningTime(ItemID itemID) {
		switch (itemID) {
		case DIRT:
		case GRASS:
			return 100;
		case STONE:
			return 200;
		case OAK_TRUNK:
			return 100;
		default:
			return -1;
		}
	}

	public static int getNumberOfMiningSteps() {
		return TexturePack.nbAnim;
	}

	// =========================================================================================================================
	// Build

	public static int getBuildingTime(ItemID itemID) {
		switch (itemID) {
		case CASTLE:
			return 200;
		default:
			return -1;
		}
	}

	// =========================================================================================================================
	// Resource

	public static boolean isResource(ItemID itemID) {
		return getResource(itemID) != null;
	}

	public static Resource getResource(ItemID itemID) {
		switch (itemID) {
		case OAK_TRUNK:
			return new Resource(10, ResourceType.WOOD);
		case STONE:
			return new Resource(10, ResourceType.STONE);
		case WATER:
			return new Resource(10, ResourceType.WATER);
		default:
			return null;
		}
	}
}
