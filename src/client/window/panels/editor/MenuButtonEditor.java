package client.window.panels.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import client.window.panels.menus.Menu;
import utils.FlixBlocksUtils;

public class MenuButtonEditor extends Menu {
	private static final long serialVersionUID = 8368480819248766526L;

	Font font = new Font("monospace", Font.BOLD, 14);
	FontMetrics fm = getFontMetrics(font);

	PanEditor editor;
	ActionEditor action;
	Image img;

	int wheelStep = 0;

	// =========================================================================================================================

	public MenuButtonEditor(PanEditor editor, ActionEditor action) {
		this.editor = editor;
		this.action = action;

		if (hasImage())
			img = FlixBlocksUtils.getImage("menu/editor/" + action.name().toLowerCase());
		else
			setMinimumSize(new Dimension(fm.stringWidth(getText()), 20));

		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				editor.wheel(action, e);
			}
		});
	}

	// =========================================================================================================================

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(hasImage() ? Color.GRAY : Color.DARK_GRAY);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

		if (hasImage())
			g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
		else {
			g.setColor(Color.LIGHT_GRAY);
			g.setFont(font);

			int y = getHeight() / 2 + (int) (fm.getStringBounds(getText(), g).getHeight() / 2) - 3;

			g.drawString(getText(), getWidth() / 2 - fm.stringWidth(getText()) / 2, y);
		}
	}

	// =========================================================================================================================

	public boolean hasImage() {
		if (action == ActionEditor.VALID_COLOR)
			return false;
		if (action == ActionEditor.SELECT_ALPHA)
			return false;
		return true;
	}

	public String getText() {
		if (action == ActionEditor.VALID_COLOR)
			return "Select";
		if (action == ActionEditor.SELECT_ALPHA)
			return wheelStep * 5 + "%";
		return "ERROR : Text missing";
	}

	// =========================================================================================================================

	public int getWheelStep() {
		return wheelStep;
	}

	public void setWheelStep(int x) {
		wheelStep = x;
	}

	// =========================================================================================================================
	// Menu

	@Override
	public void click(MouseEvent e) {
		editor.clicked(action);
	}

	@Override
	public void resize() {
	}
}
