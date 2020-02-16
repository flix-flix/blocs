package dataManager.lines;

import java.awt.Color;
import java.awt.Font;

import utilsBlocks.ButtonBlocks;

public class PanLinePart extends ButtonBlocks {
	private static final long serialVersionUID = 1106221357051831423L;

	private Font font = new Font("monospace", Font.BOLD, 12);

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
	public void setText(String text) {
		super.setText(text);
		this.setToolTipText(text);
	}

	// =========================================================================================================================

}
