package client.window.panels.emplacements;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import client.session.Action;
import client.session.Session;
import utils.FlixBlocksUtils;

public class EmplacementAction extends Emplacement {
	private static final long serialVersionUID = -2696383944798968722L;

	public Action action;

	Image img;

	public boolean selected;

	public EmplacementAction(int x, int y, int width, int height, Session session, Action action) {
		super(x, y, width, height, session);
		this.action = action;

		img = FlixBlocksUtils.getImage("menu/" + action.name().toLowerCase());

		selected = session.action == action;
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

		g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
	}

	@Override
	public void click() {
		session.setAction(action);
		selected = true;
	}
}