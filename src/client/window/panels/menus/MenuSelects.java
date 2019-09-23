package client.window.panels.menus;

import client.session.Session;
import data.map.Cube;

public class MenuSelects extends Menu {
	private static final long serialVersionUID = -7621681231232278749L;

	MenuSelectInfos selectInfos;
	MenuSelectUnit selectUnit;

	// =========================================================================================================================

	public MenuSelects(Session session) {
		super(session);

		selectInfos = new MenuSelectInfos(session);
		selectUnit = new MenuSelectUnit(session);

		add(selectInfos);
		add(selectUnit);

		selectInfos.setVisible(false);
		selectUnit.setVisible(true);
	}

	// =========================================================================================================================

	public void update(Cube cube) {
		selectInfos.setVisible(false);
		selectUnit.setVisible(false);

		if (cube.unit != null)
			selectUnit.update(cube.unit);
		else
			selectInfos.update(cube);
	}

	// =========================================================================================================================

	@Override
	public void setSize(int x, int y) {
		super.setSize(x, y);
		selectInfos.setSize(x, y);
		selectUnit.setSize(x, y);
	}

	@Override
	public void click() {

	}
}
