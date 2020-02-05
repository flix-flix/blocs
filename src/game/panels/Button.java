package game.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import utils.panels.Menu;
import window.Fen;

public class Button extends Menu {
	private static final long serialVersionUID = -4493479885268844063L;

	private Font font = new Font("arial", Font.BOLD, 20);
	private FontMetrics fm = getFontMetrics(font);

	private Fen fen;
	private String text;

	// =========================================================================================================================

	Button(Fen fen, String text) {
		this.fen = fen;
		this.text = text;
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

		g.setColor(Color.WHITE);
		g.setFont(font);

		int textW = fm.stringWidth(text);

		g.drawString(text, getWidth() / 2 - textW / 2, 30);
	}

	// =========================================================================================================================

	@Override
	public void click(MouseEvent e) {
		fen.returnToMainMenu();
	}
}
