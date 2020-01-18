package data.id;

import java.awt.Color;

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
			return new Resource(ResourceType.WOOD, 10);
		case STONE:
			return new Resource(ResourceType.STONE, 10);
		case WATER:
			return new Resource(ResourceType.WATER, 10);
		default:
			return null;
		}
	}

	public static ResourceType getResourceType(ItemID itemID) {
		switch (itemID) {
		case OAK_TRUNK:
			return ResourceType.WOOD;
		case STONE:
			return ResourceType.STONE;
		case WATER:
			return ResourceType.WATER;
		default:
			return null;
		}
	}

	// =========================================================================================================================

	public static Camera getCamera(ItemID itemID) {
		return new Camera(new Point3D(-.4, 1.5, -1), 58, -35);
	}

	// =========================================================================================================================

	public static Color getMapColor(ItemID itemID) {
		if (itemID == null)
			return new Color(0x7d0085);

		switch (itemID) {
		case GRASS:
			return new Color(0x07d240);
		case DIRT:
			return new Color(0x705700);
		case STONE:
			return new Color(0x8c8c8c);
		case WATER:
			return new Color(0x6b83c8);
		case BORDER:
			return new Color(0xffffff);
		case OAK_LEAVES:
			return new Color(0x109f0c);
		case OAK_TRUNK:
			return new Color(0x5e4303);

		case CASTLE:
			return new Color(0x1e00ff);// Player 2 0xff0000
		case UNIT:
			return new Color(0x1e00ff);

		default:
			return new Color(0xeaff00);
		}
	}
}
