package client.window.panels.menus.infos;

import java.awt.event.MouseEvent;
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

	private ItemID[] itemIDs = new ItemID[] { ItemID.BORDER, ItemID.GRASS, ItemID.DIRT, ItemID.OAK_TRUNK,
			ItemID.OAK_LEAVES, ItemID.OAK_BOARD, ItemID.STONE, ItemID.GLASS, ItemID.GLASS_GRAY, ItemID.GLASS_RED,
			ItemID.WATER, ItemID.TEST_TRANSPARENT };
	private ArrayList<Cube> _cubes = new ArrayList<>();
	public ArrayList<MenuButtonCube> cubes = new ArrayList<>();

	// =========================================================================================================================

	public MenuInfos(Session session) {
		super(session);

		resource = new MenuInfosResource(session);
		unit = new MenuInfoUnit(session);
		build = new MenuInfosBuilding(session);
		gridCubes = new MenuGrid();

		gridCubes.setSize(getSize());

		// =========================================================================================================================

		for (ItemID itemID : itemIDs)
			_cubes.add(new Cube(itemID));

		_cubes.add(new Tree().getCube());
		_cubes.add(new E().getCube());
		_cubes.add(ItemTable.createBuilding(new Building(null, ItemID.CASTLE, 0, 0, 0, false)).getCube());

		for (Cube cube : _cubes)
			addCube(cube);

		// =========================================================================================================================

		add(resource);
		add(unit);
		add(build);
		add(gridCubes);

		clear();
	}

	// =========================================================================================================================

	public void addCube(Cube cube) {
		MenuButtonCube button = new MenuButtonCube(session, cube);
		cubes.add(button);
		gridCubes.addMenu(button);
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
	public void click(MouseEvent e) {
	}
}
