package client.window.panels.menus;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import client.session.Session;
import client.window.graphicEngine.calcul.Engine;
import data.dynamic.Action;

public class MenuButtonAction extends Menu {
	private static final long serialVersionUID = -2696383944798968722L;

	public Action action;

	Engine engine;
	Image img;

	public boolean selected;

	public MenuButtonAction(Session session, Action action) {
		super(session);
		this.action = action;

		img = action.getImage();
	}

	// =========================================================================================================================

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(selected ? Color.LIGHT_GRAY : Color.GRAY);

		g.fillRect(0, 0, getWidth(), getHeight());

		if (selected) {
			g.setColor(Color.GRAY);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			g.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
			g.drawRect(2, 2, getWidth() - 5, getHeight() - 5);
		}

		g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
	}

	// =========================================================================================================================

	@Override
	public void resize() {
		if (engine != null)
			img = engine.getImage(getWidth(), getHeight());
	}

	@Override
	public void click() {
		session.fen.gui.hideMenu();
		selected = true;
	}
}
