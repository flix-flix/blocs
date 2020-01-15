package client.window.panels.menus;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MenuGrid extends Menu {
	private static final long serialVersionUID = 1941339088614372748L;

	/** Set rowHeight to SQUARE will adjust the height to equals the width */
	public static final int SQUARE = -1;

	/** Set padding to GRID_SPACE will adjust the padding to match the gridSpace */
	public static final int GRID_SPACE = -1;

	/** Number of columns in the grid */
	private int cols = 4;

	/** Height of the rows in the grid */
	private int rowHeight = SQUARE;

	/** Number of pixels between the items of the grid */
	private int gridSpace = 2;

	/** Number of pixel between the border and the grid */
	private int padding = 0;

	private Color backgroundColor = Color.LIGHT_GRAY;
	private Color borderColor = Color.DARK_GRAY;

	private int borderSize = 0;

	/** List of the panels in the grid */
	ArrayList<Menu> list = new ArrayList<>();

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(borderColor);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

		g.setColor(backgroundColor);
		g.fillRect(borderSize, borderSize, getWidth() - 1 - borderSize * 2, getHeight() - 1 - borderSize * 2);
	}

	// =========================================================================================================================

	public void addMenu(Menu menu) {
		int row = list.size() / cols;
		int col = list.size() % cols;

		setMenu(menu, row, col);

		list.add(menu);
		add(menu);
	}

	public void setMenu(Menu menu, int row, int col) {
		int padding = this.padding == GRID_SPACE ? gridSpace : this.padding;

		int w = (getWidth() - (cols - 1) * gridSpace - 2 * (borderSize + padding)) / cols;
		int wMore = (getWidth() - (cols - 1) * gridSpace - 2 * (borderSize + padding)) % cols;
		int h = rowHeight == SQUARE ? w : rowHeight;

		menu.setLocation(borderSize + padding + (w + gridSpace) * col, borderSize + padding + (h + gridSpace) * row);
		menu.setSize(w + (col == cols - 1 ? wMore : 0), h);// w + (col == cols - 1 ? getWidth() - cols * w : 0)

		super.setSize(getWidth(), (row + 1) * h + row * gridSpace + 2 * (borderSize + padding));
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

		int size = (getWidth() - (cols - 1) * gridSpace) / cols;
		int rows = list.size() / cols + (list.size() % cols == 0 ? 0 : 1);

		super.setSize(getWidth(), rows * (rowHeight == SQUARE ? size : rowHeight) + (rows - 1) * gridSpace);
	}

	// =========================================================================================================================

	public void setBackground(Color color) {
		this.backgroundColor = color;
	}

	public void setBorder(int size, Color color) {
		this.borderSize = size;
		this.borderColor = color;
	}

	public void setGridSpace(int x) {
		this.gridSpace = x;
	}

	public void setPadding(int x) {
		this.padding = x;
	}

	// =========================================================================================================================

	@Override
	public void setSize(int width, int height) {
		int size = (width - (cols - 1) * gridSpace) / cols;

		for (int i = 0; i < list.size(); i++) {
			Menu menu = list.get(i);
			int row = i / cols;
			int col = i % cols;

			setMenu(menu, row, col);

			// menu.setLocation((size + gridSpace) * col, ((rowHeight == SQUARE ? size :
			// rowHeight) + gridSpace) * row);
			// menu.setSize(size + (col == cols - 1 ? getWidth() - cols * size : 0),
			// rowHeight == SQUARE ? size : rowHeight);
		}

		int rows = list.size() / cols + (list.size() % cols == 0 ? 0 : 1);
		if (rows == 0)
			rows = 1;

		super.setSize(width, rows * (rowHeight == SQUARE ? size : rowHeight) + (rows - 1) * gridSpace);
	}

	@Override
	public void click(MouseEvent e) {
	}
}
