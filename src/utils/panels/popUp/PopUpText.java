package utils.panels.popUp;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;

import data.id.ItemTableClient;
import utils.Utils;
import utils.panels.ClickListener;

public class PopUpText extends PopUp {
	private static final long serialVersionUID = 3474517035327453550L;

	// =============== Action ===============
	private ClickListener cancelAction;

	// =============== Text ===============
	private String yamlKey;
	private String text;
	private ArrayList<String> lines;

	// =============== Font ===============
	protected Font fontText = new Font("monospace", Font.BOLD, 20);
	protected FontMetrics fmText = getFontMetrics(fontText);

	protected Font fontButton = new Font("monospace", Font.BOLD, 25);

	// =========================================================================================================================

	public PopUpText() {
	}

	// =========================================================================================================================

	@Override
	protected void paintCenter(Graphics g) {
		super.paintCenter(g);

		g.setColor(getForeground());
		g.setFont(fontText);

		for (int i = 0; i < lines.size(); i++)
			g.drawString(lines.get(i), width / 2 - fmText.stringWidth(lines.get(i)) / 2, 50 + i * 20);
	}

	// =========================================================================================================================

	@Override
	protected void updateTopLeftCorner() {
		super.updateTopLeftCorner();

		updateContentLocation();
	}

	// =========================================================================================================================

	protected void setText(String text) {
		this.text = text == null ? "No text to display" : text;
		lines = Utils.getLines(text, fmText, width - 50);
	}

	public String getText() {
		return text;
	}

	public void setYamlKey(String yamlKey) {
		this.yamlKey = yamlKey;
		refreshLang();
	}

	// =========================================================================================================================
	// Override

	protected void updateContentLocation() {
	}

	public void refreshLang() {
		if (yamlKey != null)
			setText(ItemTableClient.getText(yamlKey));
	}

	// =========================================================================================================================

	public void setCancelAction(ClickListener listener) {
		cancelAction = listener;
	}

	@Override
	public void close() {
		super.close();
		if (cancelAction != null)
			cancelAction.leftClick();
	}
}
