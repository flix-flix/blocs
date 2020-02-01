package game.panels.menus.infos;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import data.id.ItemID;
import data.id.ItemTable;
import data.map.Cube;
import data.map.buildings.Building;
import data.map.multiblocs.E;
import data.map.multiblocs.Tree;
import game.Game;
import game.panels.menus.MenuButtonCube;
import utils.panels.Menu;
import utils.panels.MenuGrid;

public class MenuInfos extends Menu {
	private static final long serialVersionUID = -7621681231232278749L;

	public MenuInfosResource resource;
	public MenuInfoUnit unit;
	public MenuInfosBuilding build;

	public MenuGrid gridCubes;

	private ArrayList<Cube> _cubes = new ArrayList<>();
	public ArrayList<MenuButtonCube> cubes = new ArrayList<>();

	// =========================================================================================================================

	public MenuInfos(Game game) {
		super(game);

		resource = new MenuInfosResource(game);
		unit = new MenuInfoUnit(game);
		build = new MenuInfosBuilding(game);
		gridCubes = new MenuGrid();

		gridCubes.setSize(getSize());

		// =========================================================================================================================

		for (int itemID : ItemTable.getItemIDList())
			_cubes.add(new Cube(itemID));

		_cubes.add(new Tree().getCube());
		_cubes.add(new E().getCube());
		_cubes.add(ItemTable.createBuilding(new Building(null, ItemID.CASTLE, 0, 0, 0, true)).getCube());

		for (Cube cube : _cubes)
			addCube(cube);

		MenuButtonCube.group(cubes);

		// =========================================================================================================================

		add(resource);
		add(unit);
		add(build);
		add(gridCubes);

		clear();
	}

	// =========================================================================================================================

	public void addCube(Cube cube) {
		MenuButtonCube button = new MenuButtonCube(game, cube);
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
