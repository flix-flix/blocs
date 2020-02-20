package game.panels.menus;

import java.awt.Color;

import data.map.resources.Resource;
import data.map.resources.ResourceType;
import game.Game;
import utils.panels.PanGrid;

public class PanRessources extends PanGrid {
	private static final long serialVersionUID = 7179773919376958365L;

	// =========================================================================================================================

	public PanRessources(Game game) {
		setColor(Color.LIGHT_GRAY, null, 5, Color.DARK_GRAY);
		setPadding(5);

		setCols(3);
		setColor(Color.LIGHT_GRAY, null, 5, Color.DARK_GRAY);
		setRowHeight(57);
		setGridPadding(2);
		setGridSpace(2);

		for (int i = 0; i < 6; i++) {
			PanResource res = new PanResource();
			res.update(new Resource(ResourceType.values()[i % 4], 0, Resource.UNLIMITED));
			gridAdd(res);
		}
	}

	// =========================================================================================================================

}
