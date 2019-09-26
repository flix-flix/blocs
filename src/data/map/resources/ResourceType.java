package data.map.resources;

import java.awt.Image;

import utils.FlixBlocksUtils;

public enum ResourceType {

	WOOD, STONE, WATER;

	private static Image wood, stone, water;

	static {
		wood = FlixBlocksUtils.getImage("menu/wood");
		stone = FlixBlocksUtils.getImage("menu/stone");
		water = FlixBlocksUtils.getImage("menu/water");
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
