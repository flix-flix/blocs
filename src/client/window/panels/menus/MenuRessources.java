package client.window.panels.menus;

import java.awt.Color;
import java.awt.Graphics;

import client.session.Session;

public class MenuRessources extends Menu {
	private static final long serialVersionUID = 7179773919376958365L;

	public MenuRessources(Session session) {
		super(session);
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setColor(Color.WHITE);
		g.drawString("130 wood", 20, 20);
	}

	// =========================================================================================================================

	@Override
	public void click() {
	}

}
