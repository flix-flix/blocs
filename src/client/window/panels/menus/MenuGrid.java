package client.window.panels.menus;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MenuGrid extends Menu {
	private static final long serialVersionUID = 1941339088614372748L;

	private static final int SQUARE = -1;

	/** Number of columns in the grid */
	int cols = 4;

	/** Height of the rows in the grid */
	int rowHeight = SQUARE;

	/** Number of pixels between the items of the grid */
	int padding = 2;

	private Color color = Color.LIGHT_GRAY;

	/** List of the panels in the grid */
	ArrayList<Menu> list = new ArrayList<>();

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(color);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	// =========================================================================================================================

	public void addItem(Menu menu) {
		int row = list.size() / cols;
		int col = list.size() % cols;

		int size = (getWidth() - (cols - 1) * padding) / cols;

		list.add(menu);

		menu.setLocation((size + padding) * col, (size + padding) * row);
		menu.setSize(size + (col == cols - 1 ? getWidth() - cols * size : 0), rowHeight == SQUARE ? size : rowHeight);

		add(menu);

		super.setSize(getWidth(), (row + 1) * size + row * padding);
	}

	// =========================================================================================================================

	public void setCols(int x) {
		cols = x;
	}

	public void setRowHeight(int rowHeight) {
		this.rowHeight = rowHeight;
	}

	public void clear() {
		list.clear();

		int size = (getWidth() - (cols - 1) * padding) / cols;
		int rows = list.size() / cols + (list.size() % cols == 0 ? 0 : 1);

		super.setSize(getWidth(), rows * (rowHeight == SQUARE ? size : rowHeight) + (rows - 1) * padding);
	}

	// =========================================================================================================================

	public void setColor(Color color) {
		this.color = color;
	}

	// =========================================================================================================================

	@Override
	public void setSize(int width, int height) {
		int size = (width - (cols - 1) * padding) / cols;

		for (int i = 0; i < list.size(); i++) {
			Menu menu = list.get(i);
			int row = i / cols;
			int col = i % cols;

			menu.setLocation((size + padding) * col, ((rowHeight == SQUARE ? size : rowHeight) + padding) * row);
			menu.setSize(size + (col == cols - 1 ? getWidth() - cols * size : 0),
					rowHeight == SQUARE ? size : rowHeight);
		}

		int rows = list.size() / cols + (list.size() % cols == 0 ? 0 : 1);
		if (rows == 0)
			rows = 1;

		super.setSize(width, rows * (rowHeight == SQUARE ? size : rowHeight) + (rows - 1) * padding);
	}

	@Override
	public void click(MouseEvent e) {
	}
}
