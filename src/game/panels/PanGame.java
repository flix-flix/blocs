package game.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import environment.PanEnvironment;
import game.Game;
import server.game.GameMode;

public class PanGame extends JPanel {
	private static final long serialVersionUID = -4495593129648278069L;

	private PanEnvironment panel;

	// =============== Size ===============
	private int startXPanel;

	// =============== Data ===============
	private GameMode gamemode = GameMode.CLASSIC;

	// =============== Font ===============
	private Font font = new Font("arial", Font.BOLD, 100);
	private AffineTransform affinetransform = new AffineTransform();
	private FontRenderContext frc = new FontRenderContext(affinetransform, true, true);

	// =============== Cross ===============
	/** Size of the central indicator (creative mode) */
	private int crossSize = 7;

	// =========================================================================================================================

	public PanGame(Game game) {
		this.setLayout(null);
		panel = game.getPanel();
		panel.setLocation(startXPanel, 0);
		panel.setSize(getWidth() - startXPanel, getHeight());
		add(panel);
	}

	// =========================================================================================================================

	@Override
	public void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();

		int centerW = width / 2;
		int centerH = height / 2;

		// Loading screen
		if (!panel.hasImage()) {
			g.setColor(new Color(236, 135, 15)); // Orange
			g.fillRect(0, 0, width, height);

			int textwidth = (int) (font.getStringBounds("loading...", frc).getWidth());
			int textheight = (int) (font.getStringBounds("loading...", frc).getHeight());

			g.setColor(Color.LIGHT_GRAY);
			g.setFont(font);
			g.drawString("loading...", centerW - textwidth / 2 + 10, centerH + textheight / 2 - 20);
			g.setFont(new Font("arial", Font.BOLD, 10));
			g.drawString("(If too long it probably crashed ;p)", centerW - 200 / 2 + 10, centerH + textheight / 2);

		} else {
			if (gamemode == GameMode.CREATIVE) {
				// Middle indicator : cross
				g.setColor(Color.WHITE);
				g.drawLine(centerW - crossSize, centerH - 1, centerW + crossSize - 1, centerH - 1);
				g.drawLine(centerW - crossSize, centerH, centerW + crossSize - 1, centerH);
				g.drawLine(centerW - 1, centerH - crossSize, centerW - 1, centerH + crossSize - 1);
				g.drawLine(centerW, centerH - crossSize, centerW, centerH + crossSize - 1);
			}
		}
	}

	// =========================================================================================================================

	public void setStartXPanel(int x) {
		startXPanel = x;
		panel.setLocation(startXPanel, 0);
		panel.setSize(getWidth() - startXPanel, getHeight());
	}

	// =========================================================================================================================

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		panel.setSize(width - startXPanel, height);
	}
}
