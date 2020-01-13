package client.window.panels.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import client.window.panels.menus.Menu;
import utils.FlixBlocksUtils;

public class MenuButtonEditor extends Menu {
	private static final long serialVersionUID = 8368480819248766526L;

	PanEditor editor;
	ActionEditor action;
	Image img;

	// =========================================================================================================================

	public MenuButtonEditor(PanEditor editor, ActionEditor action) {
		this.editor = editor;
		this.action = action;

		img = FlixBlocksUtils.getImage("menu/editor/" + action.name().toLowerCase());

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
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

		g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
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
