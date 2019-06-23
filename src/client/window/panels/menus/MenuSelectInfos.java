package client.window.panels.menus;

import java.awt.Color;
import java.awt.Graphics;

import client.session.Session;
import data.map.Cube;

public class MenuSelectInfos extends Menu {
	private static final long serialVersionUID = 8252009605405911305L;

	private Cube cube;

	// =========================================================================================================================

	public MenuSelectInfos(Session session) {
		super(session);
	}

	// =========================================================================================================================

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(5, 5, getWidth() - 10, getHeight() - 10);

		g.setColor(Color.BLACK);

		if (cube != null)
			if (cube.multibloc == null)
				g.drawString("Bloc : " + cube.itemID.name().toLowerCase(), 10, 30);
			else
				g.drawString("Multi : " + cube.multibloc.toString(), 10, 30);
	}

	// =========================================================================================================================

	public void setCube(Cube cube) {
		this.cube = cube;
	}

	// =========================================================================================================================

	@Override
	public void click() {
	}

}
