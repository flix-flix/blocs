package client.window.panels.menus;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import client.session.Session;

public class MenuGrid extends Menu {
	private static final long serialVersionUID = 1941339088614372748L;

	/** Number of columns in the grid */
	int cols = 4;

	/** Number of pixels between the items of the grid */
	int padding = 2;

	/** List of the panels in the grid */
	ArrayList<Menu> list = new ArrayList<>();

	// =========================================================================================================================

	public MenuGrid(Session session) {
		super(session);
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	// =========================================================================================================================

	public void addItem(Menu menu) {
		int row = list.size() / cols;
		int col = list.size() % cols;

		int size = (getWidth() - (cols - 1) * padding) / cols;

		list.add(menu);

		menu.setLocation((size + padding) * col, (size + padding) * row);
		menu.setSize(size, size);

		add(menu);

		super.setSize(getWidth(), (row + 1) * size + row * padding);
	}

	// =========================================================================================================================

	@Override
	public void setSize(int width, int height) {
		int size = (width - (cols - 1) * padding) / cols;

		for (int i = 0; i < list.size(); i++) {
			Menu menu = list.get(i);
			int row = i / cols;
			int col = i % cols;

			menu.setLocation((size + padding) * col, (size + padding) * row);
			menu.setSize(size, size);
		}

		super.setSize(width, (list.size() / cols) * size + (list.size() / cols - 1) * padding);
	}

	@Override
	public void click() {
	}
}
