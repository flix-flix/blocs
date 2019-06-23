package client.window.panels.menus;

import java.awt.Color;
import java.awt.Graphics;

import client.session.Session;
import data.map.Cube;

public class MenuCubeSelection extends Menu {
	private static final long serialVersionUID = -8393842761922506846L;

	public Cube cube;

	public boolean selected = false;

	public MenuCubeSelection(Session session, Cube cube) {
		super(session);
		this.cube = cube;

		this.setBackground(Color.GRAY);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(selected ? Color.LIGHT_GRAY : Color.GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (selected) {
			g.setColor(Color.GRAY);
			g.drawRect(0, 0, getWidth(), getHeight());
			g.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
			g.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
		}

		g.setColor(Color.BLACK);
		if (cube.multibloc == null)
			g.drawString(cube.itemID.name(), 10, getHeight() / 2);
		else {
			String name = cube.multibloc.getClass().getName();
			g.drawString(name.substring(name.lastIndexOf(".") + 1), 10, getHeight() / 2);
		}
	}

	@Override
	public void click() {
		session.setNextCube(cube);
		session.fen.gui.resetCubeSelection();
		selected = true;
	}
}
