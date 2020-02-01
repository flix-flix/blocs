package utils.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.JPanel;

public class MenuGrid extends Menu {
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

	// ======================= Scroll =========================
	private JPanel scrollBar;
	private int scrollWidth = 10;
	private int scrollClick = -1;
	/** Number of pixels to decal (on scroll) */
	private int scrolled = 0;
	/** Number of pixels to decal on one scroll-tick */
	private int scrollStep = 20;
	/** MAximum number of pixels that can be scrolled */
	private int visibleHeight = 0;

	// ======================= Menus =========================
	/** List of the panels in the grid */
	private ArrayList<Menu> list = new ArrayList<>();

	// =========================================================================================================================

	public MenuGrid() {
		scrollBar = new JPanel();
		scrollBar.setBackground(Color.DARK_GRAY);
		scrollBar.setVisible(false);
		add(scrollBar);

		scrollBar.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				scrollClick = e.getYOnScreen();
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		scrollBar.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				scrolled += (int) ((e.getYOnScreen() - scrollClick) * ((double) getHeight() / visibleHeight));
				scrollClick = e.getYOnScreen();

				updateScroll();
				updateMenu();
			}
		});

		// ======================= Wheel =========================

		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (visibleHeight < getHeight()) {
					e.consume();

					scrolled += e.getWheelRotation() * scrollStep;

					updateScroll();
					updateMenu();
				}
			}
		});
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

	public void addMenu(Menu menu) {
		setMenu(menu, list.size() / cols, list.size() % cols);

		list.add(menu);
		add(menu, -1);

		updateSize();
		updateScroll();
	}

	private void setMenu(Menu menu, int row, int col) {
		int padding = this.padding == GRID_SPACE ? gridSpace : this.padding;

		int width = getWidth() - (visibleHeight < getHeight() ? scrollWidth+gridSpace : 0);
		int w = (width - (cols - 1) * gridSpace - 2 * (borderSize + padding)) / cols;

		int wMore = (getWidth() - (cols - 1) * gridSpace - 2 * (borderSize + padding)) % cols;
		int h = rowHeight == SQUARE ? w : rowHeight;

		menu.setLocation(borderSize + padding + (w + gridSpace) * col,
				-scrolled + borderSize + padding + (h + gridSpace) * row);
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

	public void updateScroll() {
		scrollBar.setVisible(visibleHeight < getHeight());

		if (visibleHeight >= getHeight())
			return;

		scrolled = Math.max(0, scrolled);
		scrolled = Math.min(getHeight() - visibleHeight, scrolled);

		double ratio = visibleHeight / (double) getHeight();

		scrollBar.setLocation(getWidth() - scrollWidth, (int) (scrolled * ratio));
		scrollBar.setSize(scrollWidth, (int) (visibleHeight * ratio));
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
		super.setSize(width, height);

		visibleHeight = height;

		updateSize();
		updateScroll();
		updateMenu();
	}

	@Override
	public void click(MouseEvent e) {
	}
}
