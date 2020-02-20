package data.map.resources;

import java.awt.Image;

import data.id.ItemID;
import data.id.ItemTableClient;
import data.map.Cube;
import environment.extendsData.CubeClient;
import graphicEngine.calcul.Engine;
import utils.Utils;

public enum ResourceType {

	WOOD, STONE, WATER, UNIT;

	// =========================================================================================================================

	private static Image wood, stone, water, unit;

	public static void setTextureFolder(String folder) {
		wood = Utils.getImage(folder + "menu/game/wood");
		stone = Utils.getImage(folder + "menu/game/stone");
		water = Utils.getImage(folder + "menu/game/water");

		Engine engine = new Engine(ItemTableClient.getCamera(ItemID.UNIT), new CubeClient(new Cube(ItemID.UNIT)));
		engine.setBackground(Engine.NONE);
		unit = engine.getImage(40, 40);
	}

	// =========================================================================================================================

	public Image getImage() {
		switch (this) {
		case WOOD:
			return wood;
		case WATER:
			return water;
		case STONE:
			return stone;
		case UNIT:
			return unit;

		default:
			return null;
		}
	}
}
