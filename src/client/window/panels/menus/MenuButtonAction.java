package client.window.panels.menus;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import client.session.Session;
import client.window.graphicEngine.calcul.Engine;
import server.send.Action;
import utils.FlixBlocksUtils;

public class MenuButtonAction extends Menu {
	private static final long serialVersionUID = -2696383944798968722L;

	public Action action;

	Engine engine;
	Image img;

	public boolean selected;

	private ButtonContainer container;

	// =========================================================================================================================

	public MenuButtonAction(Session session, Action action, ButtonContainer container) {
		super(session);
		this.action = action;
		this.container = container;

		img = FlixBlocksUtils.getImage("menu/" + action.name().toLowerCase());
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
		container.releaseButtons();
		selected = true;
	}
}
