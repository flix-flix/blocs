package data;

import data.enumeration.ItemID;

public class ItemTable {

	public static boolean isOpaque(ItemID itemID) {
		switch (itemID) {
		case GLASS:
			return false;
		case GLASS_GRAY:
			return false;
		case GLASS_RED:
			return false;
		default:
			return true;
		}
	}

	public static boolean drawContour(ItemID itemID) {
		switch (itemID) {
		case TEST_BIG:
			return false;
		default:
			return true;
		}
	}
}
