package utils.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class PanGrid extends FPanel {
	private static final long serialVersionUID = 1941339088614372748L;

	// ======================= Grid size =========================
	/** Number of columns in the grid */
	private int cols = 4;

	/** Height of the rows in the grid */
	private int rowHeight = SQUARE;
	/** Set rowHeight to SQUARE will adjust the height to equals the width */
	public static final int SQUARE = -1;

	private int borderSize = 0;

	/** Number of pixels between the items of the grid */
	private int gridSpace = 2;
	/** Number of pixel between the border and the grid */
	private int padding = 0;
	/** Set padding to GRID_SPACE will adjust the padding to match the gridSpace */
	public static final int GRID_SPACE = -1;

	// ======================= Color =========================
	private Color backgroundColor = Color.LIGHT_GRAY;
	private Color borderColor = Color.DARK_GRAY;

	// ======================= Menus =========================
	/** List of the panels in the grid */
	private ArrayList<FPanel> list = new ArrayList<>();

	// =========================================================================================================================

	public PanGrid() {
		enableVerticalScroll();
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(backgroundColor);
		g.fillRect(borderSize, borderSize, getWidth() - borderSize * 2, getHeight() - borderSize * 2);

		g.setColor(borderColor);
		for (int i = 0; i < borderSize; i++)
			g.drawRect(i, i, getWidth() - 1 - 2 * i, getHeight() - 1 - 2 * i);
	}

	// =========================================================================================================================

	public void addMenu(FPanel menu) {
		setMenu(menu, list.size() / cols, list.size() % cols);

		list.add(menu);
		add(menu, -1);

		updateSize();
		updateScroll();
	}

	private void setMenu(FPanel menu, int row, int col) {
		int padding = this.padding == GRID_SPACE ? gridSpace : this.padding;

		int width = getWidth() - (getVisibleHeight() < getHeight() ? getScrollWidth() + gridSpace : 0);
		int w = (width - (cols - 1) * gridSpace - 2 * (borderSize + padding)) / cols;

		int wMore = (getWidth() - (cols - 1) * gridSpace - 2 * (borderSize + padding)) % cols;
		int h = rowHeight == SQUARE ? w : rowHeight;

		menu.setLocation(borderSize + padding + (w + gridSpace) * col,
				-getScrolled() + borderSize + padding + (h + gridSpace) * row);
		menu.setSize(w + (col == cols - 1 ? wMore : 0), h);
	}

	private void updateMenu() {
		for (int i = 0; i < list.size(); i++)
			setMenu(list.get(i), i / cols, i % cols);
	}

	private void updateSize() {
		int padding = this.padding == GRID_SPACE ? gridSpace : this.padding;

		int menuWidth = (getWidth() - (cols - 1) * gridSpace) / cols;
		int rows = list.size() / cols + (list.size() % cols == 0 ? 0 : 1);

		super.setSize(getWidth(), 2 * (borderSize + padding) + rows * (rowHeight == SQUARE ? menuWidth : rowHeight)
				+ (rows - 1) * gridSpace);
	}

	// =========================================================================================================================

	@Override
	public void updateScroll() {
		super.updateScroll();
		updateMenu();
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
		super.setVisibleSize(width, height);

		updateSize();
		updateScroll();
		updateMenu();
	}
}
