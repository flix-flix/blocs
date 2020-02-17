package utils.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

public class PopUp extends FPanel {
	private static final long serialVersionUID = 8093376262133103200L;

	/** Color around the pop-up */
	private Color voile;

	protected int width = 100, height = 100;
	protected int border = 10;
	private Color borderColor = Color.DARK_GRAY;

	/** true: click outside of the rect will close the pop-up */
	private boolean exitOnClick = false;

	// =========================================================================================================================

	public PopUp() {
		setOpaque(false);
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		if (voile != null) {
			g.setColor(voile);
			g.fillRect(0, 0, getWidth(), getHeight());
		}

		g.setColor(borderColor);
		fillCenteredRoundRect(g, getWidth() / 2, getHeight() / 2, width, height, 100);
		g.setColor(getBackground());
		fillCenteredRoundRect(g, getWidth() / 2, getHeight() / 2, width - 2 * border, height - 2 * border,
				100 - border);
	}

	// =========================================================================================================================

	@Override
	public void setBorder(int size, Color color) {
		border = size;
		borderColor = color;
	}

	// =========================================================================================================================

	public void setRect(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void setVoile(Color color) {
		voile = color;
	}

	public void setExitOnClick(boolean exitOnClick) {
		this.exitOnClick = exitOnClick;
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

	// =========================================================================================================================

	public void close() {
		setVisible(false);
	}
}
