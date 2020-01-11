package client.window.panels.menus;

import java.awt.Color;
import java.awt.Graphics;

import client.session.Session;
import data.id.ItemTable;
import data.map.Map;

public class MenuMap extends Menu {
	private static final long serialVersionUID = -1593773012271092246L;

	private Map map;

	// =========================================================================================================================

	public MenuMap(Session session) {
		super(session);

		updateMap();
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(new Color(150, 150, 150));
		g.fillRect(0, 0, getWidth(), getHeight());

		int rows = 100, cols = 100;

		// Each cube is represented by a square of "size" pixels
		int size = 3;

		int startX = (getWidth() - (rows * size)) / 2;
		int startZ = (getHeight() - (cols * size)) / 2;

		if (map != null)
			for (int x = 0; x < rows; x++)
				for (int z = 0; z < cols; z++) {
					g.setColor(ItemTable.getMapColor(map.getPixelMapRepresentation(x, z)));
					for (int i = 0; i < size; i++)
						g.drawLine(startZ + (cols - x) * size, startX + (rows - z) * size + i,
								startZ + (cols - x) * size + size - 1, startX + (rows - z) * size + i);
				}
	}

	// =========================================================================================================================

	public void updateMap() {
		map = session.map;
	}

	// =========================================================================================================================
	// Menu

	@Override
	public void click() {
	}
}
