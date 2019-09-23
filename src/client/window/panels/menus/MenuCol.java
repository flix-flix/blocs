package client.window.panels.menus;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import client.session.Session;

public class MenuCol extends Menu {
	private static final long serialVersionUID = 7065545494775980118L;

	int border = 10;

	ArrayList<Menu> menus = new ArrayList<>();
	ArrayList<Integer> space = new ArrayList<Integer>();

	// =========================================================================================================================

	public MenuCol(Session session) {
		super(session);

		this.setLayout(null);
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(border, border, getWidth() - 2 * border, getHeight() - 2 * border);
	}

	// =========================================================================================================================

	public void addItem(Menu menu, int height) {
		if (space.size() < menus.size())
			space.add(0);

		int y = 0;
		for (int i = 0; i < menus.size(); i++)
			y += space.get(i) + menus.get(i).getHeight();

		menus.add(menu);

		menu.setLocation(border, border + y);
		menu.setSize(getWidth() - 2 * border, height);
		this.add(menu);
	}

	public void addSpace(int x) {
		space.add(x);
	}

	// =========================================================================================================================

	@Override
	public void click() {
	}
}
