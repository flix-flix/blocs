package client.window.panels.menus;

import java.awt.Color;
import java.util.ArrayList;

import client.session.Session;
import data.enumeration.ItemID;
import data.map.Cube;
import data.multiblocs.E;
import data.multiblocs.Tree;

public class MenuSelects extends Menu {
	private static final long serialVersionUID = -7621681231232278749L;

	MenuSelectInfos infos;

	MenuSelectUnit unit;

	public MenuGrid gridCubes;
	private ArrayList<Cube> _cubes = new ArrayList<>();
	public ArrayList<MenuCubeSelection> cubes = new ArrayList<>();

	// =========================================================================================================================

	public MenuSelects(Session session) {
		super(session);

		setBackground(Color.BLUE);

		infos = new MenuSelectInfos(session);
		unit = new MenuSelectUnit(session);
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

		for (int i = 0; i < _cubes.size(); i++) {
			cubes.add(new MenuCubeSelection(session, _cubes.get(i)));
			gridCubes.addItem(cubes.get(i));
		}

		// =========================================================================================================================

		add(infos);
		add(unit);
		add(gridCubes);

		infos.setVisible(false);
		unit.setVisible(true);
		gridCubes.setVisible(false);
	}

	// =========================================================================================================================

	/** Show and update the corresponding panel */
	public void updateCube(Cube cube) {
		infos.setVisible(false);
		unit.setVisible(false);
		gridCubes.setVisible(false);

		if (cube == null)
			return;

		if (cube.unit != null)
			unit.update(cube.unit);
		else
			infos.update(cube);
	}

	public void showCubes() {
		infos.setVisible(false);
		unit.setVisible(false);
		gridCubes.setVisible(true);
	}

	// =========================================================================================================================

	@Override
	public void setSize(int x, int y) {
		super.setSize(x, y);
		infos.setSize(x, y);
		unit.setSize(x, y);
		gridCubes.setSize(x, y);
	}

	@Override
	public void click() {
	}
}
