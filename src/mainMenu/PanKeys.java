package mainMenu;

import java.awt.Color;
import java.awt.Graphics;
import java.util.TreeMap;

import utils.panels.FPanel;
import utils.panels.PanGrid;
import window.Key;

public class PanKeys extends FPanel {
	private static final long serialVersionUID = 7036782098855756010L;

	private TreeMap<Key, PanKey> panels = new TreeMap<>();

	private PanGrid grid = new PanGrid();

	private int margin = 50;

	FPanel menu;

	Key clicked = null;

	// =========================================================================================================================

	public PanKeys() {
		grid.setRowHeight(50);
		grid.setCols(1);
		grid.setBackground(Color.GRAY);

		for (Key k : Key.values()) {
			if (k.toString() == null)
				continue;
			PanKey p = new PanKey(this, k);
			panels.put(k, p);
			grid.addMenu(p);
		}

		menu = new FPanel();
		menu.add(grid);
		menu.setLocation(margin, margin);

		add(menu);

		setVisible(false);
	}

	// =========================================================================================================================

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.DARK_GRAY);
		g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 100, 100);
		g.setColor(Color.GRAY);
		g.fillRoundRect(8, 8, getWidth() - 1 - 16, getHeight() - 1 - 16, 100 - 16, 100 - 16);
	}

	// =========================================================================================================================

	public void clicked(Key key) {
		if (clicked != null)
			panels.get(clicked).setSelected(false);

		if (key == null)
			return;

		clicked = key;
		panels.get(clicked).setSelected(true);
	}

	public void keyPressed(int keyCode) {
		if (clicked == null)
			return;

		panels.get(clicked).setSelected(false);
		if (keyCode != Key.PAUSE.code)
			clicked.code = keyCode;
		clicked = null;
	}

	// =========================================================================================================================

	@Override
	public void resize() {
		grid.setSize(getWidth() - 2 * margin - 1, getHeight() - 2 * margin - 1);
		menu.setSize(getWidth() - 2 * margin - 1, getHeight() - 2 * margin - 1);
	}
}
