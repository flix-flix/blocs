package client.window.panels.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import client.window.panels.menus.Menu;

public class MenuItemID extends Menu {
	private static final long serialVersionUID = 2148369248773509036L;

	PanEditor editor;

	// =========================================================================================================================

	public MenuItemID(PanEditor editor) {
		this.editor = editor;
	}

	// =========================================================================================================================

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(5, 5, getWidth() - 11, getHeight() - 11);
		
	}

	// =========================================================================================================================

	@Override
	public void click(MouseEvent e) {
	}

	@Override
	public void resize() {
	}
}
