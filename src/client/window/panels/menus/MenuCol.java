package client.window.panels.menus;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MenuCol extends Menu {
	private static final long serialVersionUID = 7065545494775980118L;

	/** Don't change the height of the component */
	public static final int CURRENT = -1;
	/** Set the component's height to the width of this component */
	public static final int WIDTH = -2;
	/** Set the component's height so that it will fill the remaining place */
	public static final int REMAINING = -3;

	int border = 10;
	int padding = 10;

	/** List of the panels at the top of the column */
	ArrayList<Menu> top = new ArrayList<>();
	/** List of the panels at the bottom of the column */
	ArrayList<Menu> bottom = new ArrayList<>();

	/**
	 * Panel adapting his size to the remaining place in the center of the column
	 */
	Menu remaining;

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(border, border, getWidth() - 2 * border, getHeight() - 2 * border);
	}

	// =========================================================================================================================

	public void addTop(Menu menu, int height) {
		addTo(menu, height, top);
	}

	public void addBottom(Menu menu, int height) {
		addTo(menu, height, bottom);
	}

	private void addTo(Menu menu, int height, ArrayList<Menu> menus) {
		height = getHeight(menu, height);
		int y = 0;
		for (int i = 0; i < menus.size(); i++)
			y += menus.get(i).getHeight();

		menus.add(menu);

		int size = border + y + padding * menus.size();

		menu.setLocation(border + padding, menus == top ? size : getHeight() - 1 - height - size);
		menu.setSize(getWidth() - 2 * border - 2 * padding, height);
		this.add(menu);
	}

	private int getHeight(Menu menu, int height) {
		if (height == CURRENT)
			return menu.getHeight();
		if (height == WIDTH)
			return getWidth() - 2 * border - 2 * padding;
		if (height == REMAINING) {
			remaining = menu;
			return getRemainingHeight();
		}

		return height;
	}

	private int getRemainingHeight() {
		int x = 0;
		for (Menu m : top)
			if (m != remaining)
				x += m.getHeight();
		for (Menu m : bottom)
			x += m.getHeight();

		return getHeight() - x - 2 * border - padding * (1 + top.size() + bottom.size());
	}

	// =========================================================================================================================

	@Override
	public void resize() {
		// Repositions the panels at the bottom of the window
		for (int i = 0; i < bottom.size(); i++) {
			int y = 0;
			for (int j = 0; j < i; j++)
				y += bottom.get(j).getHeight();

			bottom.get(i).setLocation(border + padding,
					getHeight() - 1 - bottom.get(i).getHeight() - border - y - padding * (i + 1));
		}

		// Resize the center panel
		if (remaining != null)
			remaining.setSize(remaining.getWidth(), getRemainingHeight());
	}

	@Override
	public void click(MouseEvent e) {
	}
}
