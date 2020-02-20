package utils.panels;

import java.awt.Color;
import java.util.ArrayList;

public class PanGrid extends FPanel {
	private static final long serialVersionUID = 1941339088614372748L;

	// =============== Grid size ===============
	/** Number of columns in the grid */
	private int cols = 4;

	/** Height of the rows in the grid */
	private int rowHeight = SQUARE;
	/** Set rowHeight to SQUARE will adjust the height to equals the width */
	public static final int SQUARE = -1;

	/** Number of pixels between the items of the grid */
	private int gridSpace = 2;
	/** Number of pixel between the border and the grid */
	private int gridPadding = 0;
	/**
	 * Set gridPadding to GRID_SPACE will adjust the gridPadding to match the
	 * gridSpace
	 */
	public static final int GRID_SPACE = -1;

	// =============== Panels ===============
	/** List of the panels in the grid */
	private ArrayList<FPanel> list = new ArrayList<>();

	// =========================================================================================================================

	public PanGrid() {
		setBackground(Color.LIGHT_GRAY);

		enableVerticalScroll();
	}

	// =========================================================================================================================

	public void gridAdd(FPanel panels) {
		setInGrid(panels, list.size() % cols, list.size() / cols);

		list.add(panels);
		add(panels, -1);

		updateSize();
		updateScroll();
	}

	private void setInGrid(FPanel panels, int x, int y) {
		int padding = this.gridPadding == GRID_SPACE ? gridSpace : this.gridPadding;

		int width = getWidth() - (getVisibleHeight() < getHeight() ? getScrollWidth() + gridSpace : 0);
		int w = (width - (cols - 1) * gridSpace - 2 * (border + padding)) / cols;

		int wMore = (getWidth() - (cols - 1) * gridSpace - 2 * (border + padding)) % cols;
		int h = rowHeight == SQUARE ? w : rowHeight;

		panels.setLocation(border + padding + (w + gridSpace) * x,
				-getScrolled() + border + padding + (h + gridSpace) * y);
		panels.setSize(w + (x == cols - 1 ? wMore : 0), h);
	}

	private void updateGrid() {
		for (int i = 0; i < list.size(); i++)
			setInGrid(list.get(i), i % cols, i / cols);

		repaint();
	}

	private void updateSize() {
		int padding = this.gridPadding == GRID_SPACE ? gridSpace : this.gridPadding;

		int cellWidth = (getWidth() - (cols - 1) * gridSpace) / cols;
		int rows = list.size() / cols + (list.size() % cols == 0 ? 0 : 1);

		super.setRealSize(getWidth(),
				2 * (border + padding) + rows * (rowHeight == SQUARE ? cellWidth : rowHeight) + (rows - 1) * gridSpace);
	}

	// =========================================================================================================================

	@Override
	public void updateScroll() {
		super.updateScroll();
		updateGrid();
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

	public void setGridSpace(int x) {
		this.gridSpace = x;
	}

	public void setGridPadding(int x) {
		this.gridPadding = x;
	}

	// =========================================================================================================================

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);

		updateSize();
		updateScroll();
		updateGrid();
	}
}
