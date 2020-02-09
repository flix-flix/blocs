package mainMenu.buttons;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import environment.textures.TextureFace;
import environment.textures.TextureSquare;
import utils.panels.FPanel;

public class PanFelix extends FPanel {
	private static final long serialVersionUID = 952213898263587392L;

	private String felix = "Félix B.";
	private Font font = new Font("arial", Font.BOLD, 40);
	private FontMetrics fm = getFontMetrics(font);

	int size = 2;
	private static TextureFace face;

	static {
		face = new TextureFace(TextureSquare.generateSquare("static/felix"));
		face.rotation += 2;
	}

	// =========================================================================================================================

	public PanFelix() {
		setSize(fm.stringWidth(felix) + 20 + size * face.getRotated().width, 40);
	}

	// =========================================================================================================================

	@Override
	protected void paintCenter(Graphics g) {
		super.paintCenter(g);

		g.setColor(Color.DARK_GRAY);
		g.setFont(font);
		g.drawString(felix, 10, getHeight() - 5);

		int start = fm.stringWidth(felix) + 20;

		TextureSquare sq = face.getRotated();

		for (int x = 0; x < sq.width; x++)
			for (int y = 0; y < sq.height; y++) {
				g.setColor(new Color(sq.getColor(y, x)));
				g.fillRect(start + x * size, y * size, size, size);
			}
	}

	// =========================================================================================================================

	@Override
	public void click(MouseEvent e) {
		face.rotation += 3;
		repaint();
	}
}
