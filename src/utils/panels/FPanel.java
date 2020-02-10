package utils.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

public class FPanel extends JPanel {
	private static final long serialVersionUID = -5458848328043427804L;

	// =============== Border ===============
	private int border = 0;
	private Color borderColor;

	// =============== Box ===============
	/** Pixel size between the panel bounds and the border */
	private int margin = 0;
	/** Pixel size between the border and the content */
	private int padding = 0;

	// ======================= Scroll =========================
	boolean enableVerticalScroll = true;

	private JPanel scrollBar;
	/** Width of the scrollBar */
	private int scrollWidth = 10;
	/** Previous postion of the mouse (if draging) */
	private int scrollClickY = -1;
	/** Number of pixels to decal (on scroll) */
	private int scrolledY = 0;
	/** Number of pixels to decal on one scroll-tick */
	private int scrollStep = 20;
	/** Number of pixels visible */
	private int visibleHeight = 0;

	private int realHeight = 0;

	// =========================================================================================================================

	public FPanel() {
		this.setLayout(null);
		this.setOpaque(false);

		super.setBackground(Color.GRAY);

		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				click(e);
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

		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				resize();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});

		// ======================= Scroll =========================

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
				scrollClickY = e.getYOnScreen();
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
				scrolledY += (int) ((e.getYOnScreen() - scrollClickY) * ((double) getHeight() / visibleHeight));
				scrollClickY = e.getYOnScreen();

				updateScroll();
			}
		});
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		paintBorder(g, margin, border);

		// Fill Padding
		g.setColor(getBackground());
		drawEmptyCenteredRect(g, margin + border, padding);

		int undrawn = getUndrawSize();
		paintCenter(g.create(undrawn, undrawn, getContentWidth(), getContentHeight()));
	}

	// =========================================================================================================================

	protected void paintBorder(Graphics g, int margin, int border) {
		g.setColor(borderColor);
		drawEmptyCenteredRect(g, margin, border);
	}

	protected void paintCenter(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getContentWidth(), getContentHeight());
	}

	// =========================================================================================================================
	// Scroll

	public void enableVerticalScroll() {
		enableVerticalScroll = true;
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (visibleHeight < realHeight) {
					e.consume();

					scrolledY += e.getWheelRotation() * scrollStep;

					updateScroll();
				}
			}
		});
	}

	public void updateScroll() {
		scrollBar.setVisible(visibleHeight < realHeight);

		if (visibleHeight >= realHeight)
			return;

		scrolledY = Math.max(0, scrolledY);
		scrolledY = Math.min(realHeight - visibleHeight, scrolledY);

		double ratio = visibleHeight / (double) realHeight;

		scrollBar.setLocation(getWidth() - scrollWidth, (int) (scrolledY * ratio));
		scrollBar.setSize(scrollWidth, (int) (visibleHeight * ratio));
	}

	public void setVisibleSize(int width, int height) {
		super.setSize(width, height);

		visibleHeight = height;

		updateScroll();
	}

	public void setRealSize(int width, int height) {
		realHeight = height;

		updateScroll();
	}

	// =========================================================================================================================
	// Draw

	protected void drawCenteredRect(Graphics g, int ext) {
		g.fillRect(ext, ext, getWidth() - 1 - 2 * ext, getHeight() - 1 - 2 * ext);
	}

	protected void drawEmptyRect(Graphics g, int x, int y, int width, int height, int thickness) {
		for (int i = 0; i < thickness; i++)
			g.drawRect(x + i, y + i, width - 2 * i, height - 2 * i);
	}

	protected void drawEmptyCenteredRect(Graphics g, int ext, int thickness) {
		drawEmptyRect(g, ext, ext, getWidth() - 1 - 2 * ext, getHeight() - 1 - 2 * ext, thickness);
	}

	// =========================================================================================================================
	// Box

	/** Margin is the number pixels between the panel bounds and the border */
	public void setMargin(int margin) {
		this.margin = margin;
	}

	/** Padding is the number of pixels between the border and the content */
	public void setPadding(int padding) {
		this.padding = padding;
	}

	public void setBorder(int size, Color color) {
		this.border = size;
		this.borderColor = color;
	}

	protected int getBorderSize() {
		return border;
	}

	// =========================================================================================================================
	// Get inside size

	public int getContentWidth() {
		return getWidth() - 2 * getUndrawSize();
	}

	public int getContentHeight() {
		return getHeight() - 2 * getUndrawSize();
	}

	public int getUndrawSize() {
		return margin + border + padding;
	}

	// =========================================================================================================================
	// Utils

	public void setBottomRightCorner(int x, int y) {
		setLocation(x - getWidth(), y - getHeight());
	}

	// =========================================================================================================================
	// Getters (Scroll)

	public int getScrolled() {
		return scrolledY;
	}

	public int getScrollWidth() {
		return scrollWidth;
	}

	public int getVisibleHeight() {
		return visibleHeight;
	}

	// =========================================================================================================================
	// Override

	public void click(MouseEvent e) {
	}

	public void resize() {
	}

	// =========================================================================================================================

	@Override
	public void setSize(int width, int height) {
		if (enableVerticalScroll)
			setVisibleSize(width, height);
		else
			super.setSize(width, height);
	}
}
