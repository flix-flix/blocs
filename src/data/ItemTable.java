package data;

import client.textures.TexturePack;
import data.enumeration.ItemID;

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
			return false;
		default:
			return true;
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
}
