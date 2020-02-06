package mainMenu;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import utils.panels.FPanel;
import window.Key;

public class PanKey extends FPanel {
	private static final long serialVersionUID = -4605592917510364091L;

	PanKeys pan;
	Key key;

	boolean selected = false;

	// =============== Key description (left) ===============
	Font fontDesc = new Font("monospace", Font.BOLD, 20);
	FontMetrics fmDesc = getFontMetrics(fontDesc);

	// =============== Key (right) ===============
	private Font fontKey = new Font("monospace", Font.BOLD, 20);
	private FontMetrics fmKey = getFontMetrics(fontKey);

	// =========================================================================================================================

	public PanKey(PanKeys pan, Key key) {
		this.pan = pan;
		this.key = key;
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

		g.setColor(Color.BLACK);
		g.drawString(key.toString(), 10, getHeight() * 2 / 3);

		if (selected)
			g.setColor(Color.WHITE);
		g.setFont(fontKey);

		String text = KeyEvent.getKeyText(key.code);
		g.drawString(text, getWidth() - 100 - fmKey.stringWidth(text) / 2, getHeight() * 2 / 3);
	}

	// =========================================================================================================================

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	// =========================================================================================================================

	@Override
	public void click(MouseEvent e) {
		pan.clicked(key);
	}
}
