package client.window.panels.menus;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import client.session.Session;

public class MenuGrid extends Menu {
	private static final long serialVersionUID = 1941339088614372748L;

	int cols = 4;

	int margin = 10;
	int padding = 2;

	ArrayList<Menu> menus = new ArrayList<>();

	// =========================================================================================================================

	public MenuGrid(Session session) {
		super(session);
		this.setLayout(null);
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	// =========================================================================================================================

	public void addItem(Menu menu) {
		int row = menus.size() / 4;
		int col = menus.size() % 4;

		int size = (getWidth() - 2 * margin - (cols - 1) * padding) / cols;

		menus.add(menu);

		menu.setBounds(margin + (size + padding) * col, margin + (size + padding) * row, size, size);

		add(menu);
	}

	// =========================================================================================================================

	@Override
	public void click() {
	}
}
