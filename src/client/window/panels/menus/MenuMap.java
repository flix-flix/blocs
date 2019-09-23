package client.window.panels.menus;

import java.awt.Color;
import java.awt.Graphics;

import client.session.Session;

public class MenuMap extends Menu {
	private static final long serialVersionUID = -1593773012271092246L;

	// =========================================================================================================================

	public MenuMap(Session session) {
		super(session);
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(new Color(20, 110, 0));
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	// =========================================================================================================================
	@Override
	public void click() {
	}

}
