package dataManager.lines;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import utilsBlocks.ButtonBlocks;

public class PanLinePart extends ButtonBlocks {
	private static final long serialVersionUID = 1106221357051831423L;

	private Font font = new Font("monospace", Font.BOLD, 12);

	int separatorWidth = 3;

	// =========================================================================================================================

	public PanLinePart(String str) {
		setText(str);

		setForeground(Color.BLACK);
		setFont(font);
		
		
	}

	public PanLinePart() {
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.WHITE);
		g.fillRect(getWidth() - 1 - separatorWidth, 0, separatorWidth, getHeight());
	}

	// =========================================================================================================================

}
