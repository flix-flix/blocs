package data.id;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import client.textures.TexturePack;
import client.window.graphicEngine.calcul.Camera;
import client.window.graphicEngine.calcul.Point3D;
import data.map.Cube;
import data.map.buildings.Building;
import data.map.multiblocs.MultiBloc;
import data.map.resources.Resource;
import data.map.resources.ResourceType;
import utils.FlixBlocksUtils;

public class ItemTable {

	// =========================================================================================================================
	// Graphic Engine

	public static boolean isOpaque(int itemID) {
		switch (itemID) {
		case ItemID.GLASS:
			return false;
		case ItemID.GLASS_GRAY:
			return false;
		case ItemID.GLASS_RED:
			return false;
		case ItemID.WATER:
			return false;
		case ItemID.TEST_TRANSPARENT:
			return false;

		case ItemID.UNIT:
			return false;

		default:
			return true;
		}
	}

	public static boolean drawContour(int itemID) {
		switch (itemID) {
		case ItemID.WATER:
		case ItemID.CASTLE:
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

	public static boolean isMultiBloc(int itemID) {
		switch (itemID) {
		case ItemID.CASTLE:
			return true;
		default:
			return false;
		}
	}

	public static int getXSize(int itemID) {
		switch (itemID) {
		case ItemID.CASTLE:
			return 3;
		default:
			return 1;
		}
	}

	public static int getYSize(int itemID) {
		switch (itemID) {
		case ItemID.CASTLE:
			return 2;
		default:
			return 1;
		}
	}

	public static int getZSize(int itemID) {
		switch (itemID) {
		case ItemID.CASTLE:
			return 2;
		default:
			return 1;
		}
	}

	// =========================================================================================================================
	// Mining

	public static int getMiningTime(int itemID) {
		switch (itemID) {
		case ItemID.DIRT:
		case ItemID.GRASS:
			return 100;
		case ItemID.STONE:
			return 200;
		case ItemID.OAK_TRUNK:
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

	public static int getBuildingTime(int itemID) {
		switch (itemID) {
		case ItemID.CASTLE:
			return 200;
		default:
			return -1;
		}
	}

	// =========================================================================================================================
	// Resource

	public static boolean isResource(int itemID) {
		return getResource(itemID) != null;
	}

	public static Resource getResource(int itemID) {
		switch (itemID) {
		case ItemID.OAK_TRUNK:
			return new Resource(ResourceType.WOOD, 10);
		case ItemID.STONE:
			return new Resource(ResourceType.STONE, 10);
		case ItemID.WATER:
			return new Resource(ResourceType.WATER, 10);
		default:
			return null;
		}
	}

	public static ResourceType getResourceType(int itemID) {
		switch (itemID) {
		case ItemID.OAK_TRUNK:
			return ResourceType.WOOD;
		case ItemID.STONE:
			return ResourceType.STONE;
		case ItemID.WATER:
			return ResourceType.WATER;
		default:
			return null;
		}
	}

	// =========================================================================================================================

	public static Camera getCamera(int itemID) {
		if (itemID == ItemID.CASTLE)
			return new Camera(new Point3D(3.7, 3, 4.2), 236, -30);
		return new Camera(new Point3D(-.4, 1.5, -1), 58, -35);
	}

	// =========================================================================================================================

	public static String getName(int itemID) {
		switch (itemID) {
		case ItemID.BORDER:
			return "BORDER";

		case ItemID.GRASS:
			return "GRASS";
		case ItemID.DIRT:
			return "DIRT";
		case ItemID.STONE:
			return "STONE";

		case ItemID.OAK_TRUNK:
			return "OAK_TRUNK";
		case ItemID.OAK_LEAVES:
			return "OAK_LEAVES";

		case ItemID.GLASS:
			return "GLASS";
		case ItemID.GLASS_GRAY:
			return "GLASS_GRAY";
		case ItemID.GLASS_RED:
			return "GLASS_RED";

		case ItemID.UNIT:
			return "UNIT";

		case ItemID.CASTLE:
			return "CASTLE";

		default:
			return "NULL";
		}
	}

	public static ArrayList<String> getItemNameList() {
		String[] array = new String[] { "BORDER", "DIRT", "STONE" };

		return new ArrayList<String>(Arrays.asList(array));
	}

	// =========================================================================================================================

	public static Color getMapColor(int itemID) {
		if (itemID == ItemID.NULL)
			return new Color(0x7d0085);

		switch (itemID) {
		case ItemID.GRASS:
			return new Color(0x07d240);
		case ItemID.DIRT:
			return new Color(0x705700);
		case ItemID.STONE:
			return new Color(0x8c8c8c);
		case ItemID.WATER:
			return new Color(0x6b83c8);
		case ItemID.BORDER:
			return new Color(0xffffff);
		case ItemID.OAK_LEAVES:
			return new Color(0x109f0c);
		case ItemID.OAK_TRUNK:
			return new Color(0x5e4303);

		case ItemID.CASTLE:
			return new Color(0x1e00ff);// Player 2 0xff0000
		case ItemID.UNIT:
			return new Color(0x1e00ff);

		default:
			return new Color(0xeaff00);
		}
	}
}
