package game.panels.menus.infos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import data.map.Cube;
import data.map.resources.Resource;
import utils.panels.FPanel;

public class PanInfosResource extends FPanel {
	private static final long serialVersionUID = 8252009605405911305L;

	private Font font = new Font("monospace", Font.BOLD, 15);

	private Cube cube;
	private Resource resource;

	// =========================================================================================================================

	public PanInfosResource() {
		setBackground(Color.GRAY);
	}

	// =========================================================================================================================

	@Override
	protected void paintCenter(Graphics g) {
		super.paintCenter(g);

		if (resource != null) {
			g.setFont(font);

			if (resource.isEmpty()) {
				g.setColor(Color.BLACK);
				g.drawString("RESOURCE EMPTY", 15, 50);
			} else {
				g.drawImage(resource.getType().getImage(), 15, 15, null);

				g.setColor(Color.WHITE);
				g.drawString(": " + resource.getQuantity(), 75, 40);
			}
		}
	}

	// =========================================================================================================================

	public void update() {
		resource = cube.getResource();

		repaint();
	}

	public void setCube(Cube cube) {
		this.cube = cube;
		update();
	}

	public void clear() {
		cube = null;
	}
}
