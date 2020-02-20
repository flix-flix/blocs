package utils.panels;

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
	/** Number of pixel between a component and another or with the border */
	int space = 10;

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
	}

	// =========================================================================================================================

	public void addTop(FPanel panel, int height) {
		addTo(panel, height, top);
	}

	public void addBottom(FPanel panel, int height) {
		addTo(panel, height, bottom);
	}

	private void addTo(FPanel panel, int height, ArrayList<FPanel> panels) {
		height = getHeight(panel, height);
		int y = 0;
		for (int i = 0; i < panels.size(); i++)
			y += panels.get(i).getHeight();

		panels.add(panel);

		int size = border + y + space * panels.size();

		panel.setLocation(border + space, panels == top ? size : getHeight() - 1 - height - size);
		panel.setSize(getWidth() - 2 * border - 2 * space, height);
		this.add(panel);
	}

	private int getHeight(FPanel panel, int height) {
		if (height == CURRENT)
			return panel.getHeight();
		if (height == WIDTH)
			return getWidth() - 2 * border - 2 * space;
		if (height == REMAINING) {
			remaining = panel;
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

		return getHeight() - x - 2 * border - space * (1 + top.size() + bottom.size());
	}

	// =========================================================================================================================

	public void setSpace(int space) {
		this.space = space;
	}

	// =========================================================================================================================

	@Override
	public void resize() {
		// Repositions the panels at the bottom of the window
		for (int i = 0; i < bottom.size(); i++) {
			int y = 0;
			for (int j = 0; j < i; j++)
				y += bottom.get(j).getHeight();

			bottom.get(i).setLocation(border + space,
					getHeight() - 1 - bottom.get(i).getHeight() - border - y - space * (i + 1));
		}

		// Resize the center panel
		if (remaining != null)
			remaining.setSize(remaining.getWidth(), getRemainingHeight());

		repaint();
	}

	@Override
	public void click(MouseEvent e) {
	}
}
