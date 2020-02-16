package utils.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

public class ButtonPad extends FButton {
	private static final long serialVersionUID = -9212352133948222194L;

	public ButtonPad(String text, Font font, Color back, Color fore, int border) {
		super.setText(text);
		if (font != null)
			super.setFont(font);
		super.setBackground(back);
		super.setForeground(fore);
		super.setBorder(border, fore);
		super.setPadding(5);

		updateSize();
	}

	// =========================================================================================================================

	@Override
	public void setText(String text) {
		super.setText(text);
		updateSize();
	}

	public void updateSize() {
		FontMetrics fm = getFontMetrics(font);
		setSize(fm.stringWidth(text) + 2 * getUndrawSize(), fm.getHeight() + getUndrawSize());
	}
}
