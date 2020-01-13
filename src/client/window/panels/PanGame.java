package client.window.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import client.session.Session;
import server.game.GameMode;

public class PanGame extends JPanel {

	private static final long serialVersionUID = -8972445552589270416L;

	Session session;
	public BufferedImage img = null;

	private Font font = new Font("arial", Font.BOLD, 100);
	private AffineTransform affinetransform = new AffineTransform();
	private FontRenderContext frc = new FontRenderContext(affinetransform, true, true);

	// =========================================================================================================================

	public int centerX, centerY;
	// Size of the central indicator (creative mode)
	public int crossSize = 7;

	// =========================================================================================================================

	public PanGame(Session session) {
		this.session = session;
		setLayout(null);
	}

	// =========================================================================================================================

	public void paintComponent(Graphics g) {
		if (img == null) {
			g.setColor(new Color(236, 135, 15));
			g.fillRect(0, 0, this.getWidth(), this.getHeight());

			int textwidth = (int) (font.getStringBounds("loading...", frc).getWidth());
			int textheight = (int) (font.getStringBounds("loading...", frc).getHeight());

			g.setColor(Color.LIGHT_GRAY);
			g.setFont(font);
			g.drawString("loading...", this.getWidth() / 2 - textwidth / 2 + 10,
					this.getHeight() / 2 + textheight / 2 - 20);
			g.setFont(new Font("arial", Font.BOLD, 10));
			g.drawString("(If too long it probably crashed ;p)", this.getWidth() / 2 - 200 / 2 + 10,
					this.getHeight() / 2 + textheight / 2);
		} else {
			centerX = getWidth() / 2;
			centerY = getHeight() / 2;

			g.drawImage(img, 0, 0, null);
			session.processing = false;

			if (session.gamemode == GameMode.CREATIVE) {
				// Middle indicator : cross
				g.setColor(Color.WHITE);
				g.drawLine(centerX - crossSize, centerY - 1, centerX + crossSize - 1, centerY - 1);
				g.drawLine(centerX - crossSize, centerY, centerX + crossSize - 1, centerY);
				g.drawLine(centerX - 1, centerY - crossSize, centerX - 1, centerY + crossSize - 1);
				g.drawLine(centerX, centerY - crossSize, centerX, centerY + crossSize - 1);
			}
		}
	}
}
