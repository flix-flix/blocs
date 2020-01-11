package client.window.panels.menus.infos;

import java.awt.Color;
import java.util.ArrayList;

import client.session.Session;
import client.window.panels.menus.Menu;
import client.window.panels.menus.MenuButtonCube;
import client.window.panels.menus.MenuGrid;
import data.id.ItemID;
import data.id.ItemTable;
import data.map.Cube;
import data.map.buildings.Building;
import data.map.multiblocs.E;
import data.map.multiblocs.Tree;

public class MenuInfos extends Menu {
	private static final long serialVersionUID = -7621681231232278749L;

	public MenuInfosResource resource;
	public MenuInfoUnit unit;
	public MenuInfosBuilding build;

	public MenuGrid gridCubes;
	private ArrayList<Cube> _cubes = new ArrayList<>();
	public ArrayList<MenuButtonCube> cubes = new ArrayList<>();

	// =========================================================================================================================

	public MenuInfos(Session session) {
		super(session);

		setBackground(Color.BLUE);

		resource = new MenuInfosResource(session);
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

		add(resource);
		add(unit);
		add(build);
		add(gridCubes);

		clear();
	}

	// =========================================================================================================================

	/** Show and update the corresponding panel */
	public void refresh(Cube cube) {
		resource.clear();
		build.clear();
		unit.clear();
		gridCubes.setVisible(false);

		if (cube == null)
			return;

		if (cube.unit != null)
			unit.update(cube.unit);
		else if (cube.build != null)
			build.update(cube.build);
		else if (cube.hasResource())
			resource.update(cube);
	}

	/** Hide the panel */
	public void clear() {
		refresh(null);
	}

	public void showCubes() {
		gridCubes.setVisible(true);
	}

	// =========================================================================================================================

	@Override
	public void setSize(int x, int y) {
		super.setSize(x, y);
		resource.setSize(x, y);
		unit.setSize(x, y);
		build.setSize(x, y);
		gridCubes.setSize(x, y);
	}

	@Override
	public void click() {
	}
}
