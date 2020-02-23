package utils.panels.popUp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import utils.panels.FPanel;

public class PopUp extends FPanel {
	private static final long serialVersionUID = 8093376262133103200L;

	/** Color around the pop-up box */
	private Color voile;

	/** true: click outside of the rect will close the pop-up */
	private boolean exitOnClick = false;

	// =============== Size ===============
	/** Size of the pop-up box */
	protected int width = 100, height = 100;
	/** Location of the top left corner of the box */
	protected int startX, startY;

	// =========================================================================================================================

	public PopUp() {
		setOpaque(false);
		setBorder(0, Color.DARK_GRAY);
		setVisible(false);
	}

	// =========================================================================================================================

	// Override paintComponent() cause it's non-rectangular
	@Override
	protected void paintComponent(Graphics g) {
		if (voile != null) {
			g.setColor(voile);
			g.fillRect(0, 0, getWidth(), getHeight());
		}

		g.setColor(borderColor);
		g.fillRoundRect(startX, startY, width, height, 100, 100);
		g.setColor(getBackground());
		g.fillRoundRect(startX + border, startY + border, width - 2 * border, height - 2 * border, 100 - border,
				100 - border);

		this.paintCenter(g.create(startX, startY, width, height));
	}

	protected void paintCenter(Graphics g) {
	}

	// =========================================================================================================================

	public void setRect(int width, int height) {
		this.width = width;
		this.height = height;

		updateTopLeftCorner();
	}

	public void setVoile(Color color) {
		voile = color;
	}

	public void setExitOnClick(boolean exitOnClick) {
		this.exitOnClick = exitOnClick;
	}

	// =========================================================================================================================

	protected void updateTopLeftCorner() {
		startX = getWidth() / 2 - width / 2;
		startY = getHeight() / 2 - height / 2;
	}

	// =========================================================================================================================

	public void close() {
		setVisible(false);
	}

	// =========================================================================================================================

	@Override
	public void click(MouseEvent e) {
		super.click(e);

		if (!exitOnClick)
			return;

		int x = Math.abs(getWidth() / 2 - e.getX());
		int y = Math.abs(getHeight() / 2 - e.getY());

		if (x > width / 2 || y > height / 2)
			close();
	}

	@Override
	public void resize() {
		super.resize();

		updateTopLeftCorner();
	}
}
