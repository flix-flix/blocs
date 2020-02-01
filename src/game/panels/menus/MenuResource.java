package game.panels.menus;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import data.map.resources.Resource;
import utils.panels.Menu;

public class MenuResource extends Menu {
	private static final long serialVersionUID = -1692728772288500652L;

	private Font font = new Font("monospace", Font.BOLD, 15);
	private int imgSize = 30;

	Resource res;

	// =========================================================================================================================

	public MenuResource() {
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (res != null) {
			g.drawImage(res.getType().getImage(), 5, getHeight() / 2 - imgSize / 2, null);
			g.setFont(font);
			g.setColor(Color.WHITE);
			String str = ": " + res.getQuantity() + " / " + res.getMax();
			g.drawString(str, imgSize + 10, 30);
		}
	}

	// =========================================================================================================================

	public void update(Resource res) {
		this.res = res;
	}
}
