package client.window.panels.emplacements;

import java.awt.Color;
import java.awt.Graphics;

import client.session.Session;
import data.map.Cube;

public class EmplacementCubeSelection extends Emplacement {
	private static final long serialVersionUID = -8393842761922506846L;

	public Cube cube;

	public boolean selected = false;

	public EmplacementCubeSelection(int x, int y, int width, int height, Session session, Cube cube) {
		super(x, y, width, height, session);
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
		g.drawString(cube.itemID.name(), 10, getHeight() / 2);
	}

	@Override
	public void click() {
		session.setNextCube(cube);
		selected = true;
	}
}
