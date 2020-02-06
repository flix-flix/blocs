package utils.panels;

import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class FPanel extends JPanel {
	private static final long serialVersionUID = -5458848328043427804L;

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
	}

	// =========================================================================================================================

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

	public void setBottomRightCorner(int x, int y) {
		setLocation(x - getWidth(), y - getHeight());
	}

	// =========================================================================================================================

	public void click(MouseEvent e) {
	}

	public void resize() {
	}
}
