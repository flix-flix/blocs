package data.map.resources;

import java.awt.Image;

import utils.FlixBlocksUtils;

public enum ResourceType {

	WOOD, STONE, WATER;

	private static Image wood, stone, water;

	public static void setTextureFolder(String folder) {
		wood = FlixBlocksUtils.getImage(folder + "menu/game/wood");
		stone = FlixBlocksUtils.getImage(folder + "menu/game/stone");
		water = FlixBlocksUtils.getImage(folder + "menu/game/water");
	}

	public Image getImage() {
		switch (this) {
		case WOOD:
			return wood;
		case WATER:
			return water;
		case STONE:
			return stone;

		default:
			return null;
		}
	}
}
