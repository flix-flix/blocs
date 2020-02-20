package game.panels.menus;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

import data.map.resources.Resource;
import utils.panels.FPanel;

public class PanResource extends FPanel {
	private static final long serialVersionUID = -1692728772288500652L;

	private Font font = new Font("monospace", Font.BOLD, 15);
	private FontMetrics fm = getFontMetrics(font);

	private Resource res;

	// =========================================================================================================================

	public PanResource() {
		setColor(Color.LIGHT_GRAY, Color.DARK_GRAY, 2, Color.DARK_GRAY);
		setPadding(2);
		setMargin(2);
	}

	// =========================================================================================================================

	@Override
	protected void paintCenter(Graphics g) {
		super.paintCenter(g);

		if (res != null) {
			Image img = res.getType().getImage();

			g.drawImage(img, 5, getContentHeight() / 2 - img.getHeight(null) / 2, null);

			g.setFont(font);
			g.setColor(getForeground());

			String str;
			if (res.getMax() == Resource.UNLIMITED)
				str = String.format(": %d ", res.getQuantity());
			else
				str = String.format(": %d / %d", res.getQuantity(), res.getMax());

			g.drawString(str, img.getWidth(null) + 5, getContentHeight() / 2 + fm.getHeight() / 3);
		}
	}

	// =========================================================================================================================

	public void update(Resource res) {
		this.res = res;
		repaint();
	}

	// =========================================================================================================================

	public boolean isEmpty() {
		return res == null || res.isEmpty();
	}
}
