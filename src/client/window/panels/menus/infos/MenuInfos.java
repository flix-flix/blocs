package client.window.panels.menus.infos;

import java.awt.Color;
import java.util.ArrayList;

import client.session.Session;
import client.window.panels.menus.Menu;
import client.window.panels.menus.MenuButtonCube;
import client.window.panels.menus.MenuGrid;
import data.ItemTable;
import data.enumeration.ItemID;
import data.map.Cube;
import data.map.buildings.Building;
import data.multiblocs.E;
import data.multiblocs.Tree;

public class MenuInfos extends Menu {
	private static final long serialVersionUID = -7621681231232278749L;

	MenuInfosDefault infos;
	MenuInfoUnit unit;
	MenuInfosBuilding build;

	public MenuGrid gridCubes;
	private ArrayList<Cube> _cubes = new ArrayList<>();
	public ArrayList<MenuButtonCube> cubes = new ArrayList<>();

	// =========================================================================================================================

	public MenuInfos(Session session) {
		super(session);

		setBackground(Color.BLUE);

		infos = new MenuInfosDefault(session);
		unit = new MenuInfoUnit(session);
		build = new MenuInfosBuilding(session);
		gridCubes = new MenuGrid(session);

		gridCubes.setSize(getSize());

		// =========================================================================================================================

		_cubes.add(new Cube(ItemID.BORDER));
		_cubes.add(new Cube(ItemID.GRASS));
		_cubes.add(new Cube(ItemID.DIRT));
		_cubes.add(new Cube(ItemID.OAK_TRUNK));
		_cubes.add(new Cube(ItemID.OAK_LEAVES));
		_cubes.add(new Cube(ItemID.OAK_BOARD));
		_cubes.add(new Cube(ItemID.STONE));
		_cubes.add(new Cube(ItemID.GLASS));
		_cubes.add(new Cube(ItemID.GLASS_GRAY));
		_cubes.add(new Cube(ItemID.GLASS_RED));
		_cubes.add(new Tree().getCube());
		_cubes.add(new E().getCube());
		_cubes.add(new Cube(ItemID.WATER));
		_cubes.add(new Cube(ItemID.TEST_TRANSPARENT));
		_cubes.add(ItemTable.createBuilding(new Building(null, ItemID.CASTLE, 0, 0, 0, false)).getCube());

		for (int i = 0; i < _cubes.size(); i++) {
			cubes.add(new MenuButtonCube(session, _cubes.get(i)));
			gridCubes.addItem(cubes.get(i));
		}

		// =========================================================================================================================

		add(infos);
		add(unit);
		add(build);
		add(gridCubes);

		infos.setVisible(false);
		unit.setVisible(true);
		build.setVisible(true);
		gridCubes.setVisible(false);
	}

	// =========================================================================================================================

	/** Show and update the corresponding panel */
	public void updateCube(Cube cube) {
		infos.setVisible(false);
		unit.setVisible(false);
		build.setVisible(false);
		gridCubes.setVisible(false);

		if (cube == null)
			return;

		if (cube.unit != null)
			unit.update(cube.unit);
		else if (cube.build != null)
			build.update(cube.build);
		else
			infos.update(cube);
	}

	public void showCubes() {
		infos.setVisible(false);
		unit.setVisible(false);
		build.setVisible(false);
		gridCubes.setVisible(true);
	}

	// =========================================================================================================================

	@Override
	public void setSize(int x, int y) {
		super.setSize(x, y);
		infos.setSize(x, y);
		unit.setSize(x, y);
		build.setSize(x, y);
		gridCubes.setSize(x, y);
	}

	@Override
	public void click() {
	}
}
