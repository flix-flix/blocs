package game.panels.menus;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import data.id.ItemTableClient;
import data.map.Map;
import game.Game;
import game.StateHUD;
import utils.panels.FPanel;

public class PanMap extends FPanel {
	private static final long serialVersionUID = -1593773012271092246L;

	private Game game;

	private Map map;

	// =========================================================================================================================

	public PanMap(Game game) {
		this.game = game;

		setBackground(Color.GRAY);

		updateMap();
	}

	// =========================================================================================================================

	@Override
	protected void paintCenter(Graphics g) {
		super.paintCenter(g);

		int rows = 100, cols = 100;

		// Each cube is represented by a square of "size" pixels
		int size = 3;

		int startX = (getWidth() - (rows * size)) / 2;
		int startZ = (getHeight() - (cols * size)) / 2;

		if (map != null)
			for (int x = 0; x < rows; x++)
				for (int z = 0; z < cols; z++) {
					g.setColor(new Color(ItemTableClient.getMapColor(map.getPixelMapRepresentation(x, z))));
					for (int i = 0; i < size; i++)
						g.drawLine(startZ + (cols - x) * size, startX + (rows - z) * size + i,
								startZ + (cols - x) * size + size - 1, startX + (rows - z) * size + i);
				}
	}

	// =========================================================================================================================

	public void updateMap() {
		map = game.getMap();

		if (game.getStateHUD() != StateHUD.PAUSE)
			repaint();
	}

	// =========================================================================================================================
	// FPanel

	@Override
	public void click(MouseEvent e) {
		// TODO [Feature] Map clickable
	}
}
