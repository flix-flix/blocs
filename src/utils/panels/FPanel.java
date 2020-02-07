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

	// ======================= Scroll =========================
	private JPanel scrollBar;
	/** Width of the scrollBar */
	private int scrollWidth = 10;
	/** Previous postion of the mouse (if draging) */
	private int scrollClick = -1;
	/** Number of pixels to decal (on scroll) */
	private int scrolled = 0;
	/** Number of pixels to decal on one scroll-tick */
	private int scrollStep = 20;
	/** Number of pixels visible */
	private int visibleHeight = 0;

	// =========================================================================================================================

	public FPanel() {
		this.setLayout(null);
		this.setOpaque(false);

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
			}
		});
	}

	// =========================================================================================================================
	// Scroll

	public void enableScroll() {
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (visibleHeight < getHeight()) {
					e.consume();

					scrolled += e.getWheelRotation() * scrollStep;

					updateScroll();
				}
			}
		});
	}

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

	public void setVisibleSize(int width, int height) {
		super.setSize(width, height);

		visibleHeight = height;
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
	// Utils

	public void setBottomRightCorner(int x, int y) {
		setLocation(x - getWidth(), y - getHeight());
	}

	// =========================================================================================================================
	// Getters

	public int getScrolled() {
		return scrolled;
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
}
