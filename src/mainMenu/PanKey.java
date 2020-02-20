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

	private PanKeys pan;
	private Key key;

	private boolean selected = false;

	// =============== Key description (left) ===============
	private Font fontDesc = new Font("monospace", Font.PLAIN, 14);

	// =============== Key (right) ===============
	private Font fontKey = new Font("monospace", Font.BOLD, 20);
	private FontMetrics fmKey = getFontMetrics(fontKey);

	// =========================================================================================================================

	public PanKey(PanKeys pan, Key key) {
		this.pan = pan;
		this.key = key;

		setBackground(Color.LIGHT_GRAY);
	}

	// =========================================================================================================================

	@Override
	protected void paintCenter(Graphics g) {
		super.paintCenter(g);

		// Action
		g.setColor(Color.BLACK);
		g.setFont(fontDesc);
		g.drawString(key.toString(), 10, getHeight() * 2 / 3);

		// Key
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
