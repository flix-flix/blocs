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

	public int startW = 400;
	public int width = 0;
	public int height = 0;

	// =========================================================================================================================

	public int centerW, centerH;
	// Size of the central indicator (creative mode)
	public int crossSize = 7;

	// =========================================================================================================================

	public PanGame(Session session) {
		this.session = session;
		setLayout(null);
	}

	// =========================================================================================================================

	public void paintComponent(Graphics g) {
		width = getWidth() - 1 - startW;
		height = this.getHeight() - 1;

		centerW = startW + width / 2;
		centerH = height / 2;

		if (img == null) {
			g.setColor(new Color(236, 135, 15));
			g.fillRect(startW, 0, width, height);

			int textwidth = (int) (font.getStringBounds("loading...", frc).getWidth());
			int textheight = (int) (font.getStringBounds("loading...", frc).getHeight());

			g.setColor(Color.LIGHT_GRAY);
			g.setFont(font);
			g.drawString("loading...", centerW - textwidth / 2 + 10, centerH + textheight / 2 - 20);
			g.setFont(new Font("arial", Font.BOLD, 10));
			g.drawString("(If too long it probably crashed ;p)", centerW - 200 / 2 + 10, centerH + textheight / 2);
		} else {

			g.drawImage(img, startW, 0, null);
			session.processing = false;

			if (session.gamemode == GameMode.CREATIVE) {
				// Middle indicator : cross
				g.setColor(Color.WHITE);
				g.drawLine(centerW - crossSize, centerH - 1, centerW + crossSize - 1, centerH - 1);
				g.drawLine(centerW - crossSize, centerH, centerW + crossSize - 1, centerH);
				g.drawLine(centerW - 1, centerH - crossSize, centerW - 1, centerH + crossSize - 1);
				g.drawLine(centerW, centerH - crossSize, centerW, centerH + crossSize - 1);
			}
		}
	}
}
