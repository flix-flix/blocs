package utils.panels;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class PanCol extends FPanel {
	private static final long serialVersionUID = 7065545494775980118L;

	/** Don't change the height of the component */
	public static final int CURRENT = -1;
	/** Set the component's height to the width of this component */
	public static final int WIDTH = -2;
	/** Set the component's height so that it will fill the remaining place */
	public static final int REMAINING = -3;

	// =============== Size ===============
	int padding = 10, border = 10;

	/** List of the panels at the top of the column */
	ArrayList<FPanel> top = new ArrayList<>();
	/** List of the panels at the bottom of the column */
	ArrayList<FPanel> bottom = new ArrayList<>();

	/**
	 * Panel adapting his size to the remaining place in the center of the column
	 */
	FPanel remaining;

	// =========================================================================================================================

	public PanCol() {
		setBorder(10, Color.GRAY);
		setBackground(Color.LIGHT_GRAY);
		setPadding(10);
	}

	// =========================================================================================================================

	public void addTop(FPanel menu, int height) {
		addTo(menu, height, top);
	}

	public void addBottom(FPanel menu, int height) {
		addTo(menu, height, bottom);
	}

	private void addTo(FPanel menu, int height, ArrayList<FPanel> menus) {
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

	private int getHeight(FPanel menu, int height) {
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
		for (FPanel m : top)
			if (m != remaining)
				x += m.getHeight();
		for (FPanel m : bottom)
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
