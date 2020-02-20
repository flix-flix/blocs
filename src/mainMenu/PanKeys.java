package mainMenu;

import java.awt.Color;
import java.util.TreeMap;

import utils.panels.FPanel;
import utils.panels.PanGrid;
import utils.panels.PopUp;
import window.Key;

public class PanKeys extends PopUp {
	private static final long serialVersionUID = 7036782098855756010L;

	private TreeMap<Key, PanKey> panels = new TreeMap<>();

	private PanGrid grid = new PanGrid();
	private FPanel container;

	private Key clicked = null;

	private int margin = 50;

	// =========================================================================================================================

	public PanKeys() {
		setRect(500, 700);
		setBackground(Color.GRAY);
		setBorder(10, Color.DARK_GRAY);
		setVoile(new Color(90, 90, 90, 150));
		setExitOnClick(true);

		grid.setRowHeight(50);
		grid.setCols(1);
		grid.setBackground(Color.GRAY);
		grid.setWidth(400);

		for (Key k : Key.values()) {
			if (k.toString() == null)
				continue;
			PanKey p = new PanKey(this, k);
			panels.put(k, p);
			grid.gridAdd(p);
		}

		// TODO [Improve] Scroll => Remove Panel
		container = new FPanel();
		container.add(grid);
		container.setLocation(margin, margin);
		container.setWidth(400);

		this.add(container);

		this.setVisible(false);
	}

	// =========================================================================================================================

	public void clicked(Key key) {
		if (clicked != null)
			panels.get(clicked).setSelected(false);

		if (key == null)
			return;

		clicked = key;
		panels.get(clicked).setSelected(true);

		repaint();
	}

	public void keyPressed(int keyCode) {
		if (clicked == null) {
			if (Key.PAUSE.code == keyCode)
				close();
			return;
		}

		panels.get(clicked).setSelected(false);
		if (keyCode != Key.PAUSE.code)
			clicked.code = keyCode;
		clicked = null;

		repaint();
	}

	// =========================================================================================================================

	@Override
	public void resize() {
		super.resize();

		int height = Math.min(getHeight() - 50, 700);

		height -= 2 * margin + 1;
		grid.setHeight(height);
		container.setHeight(height);

		container.setCenter(getWidth() / 2, getHeight() / 2);
	}
}
