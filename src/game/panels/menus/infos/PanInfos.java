package game.panels.menus.infos;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import data.id.ItemID;
import data.id.ItemTable;
import data.id.ItemTableClient;
import data.map.Cube;
import data.map.buildings.Building;
import data.map.multiblocs.E;
import data.map.multiblocs.Tree;
import environment.extendsData.CubeClient;
import game.Game;
import utils.panels.ClickListener;
import utils.panels.FPanel;
import utils.panels.PanGrid;
import utilsBlocks.ButtonBlocks;

public class PanInfos extends FPanel {
	private static final long serialVersionUID = -7621681231232278749L;

	private Game game;

	public PanInfosResource resource;
	public PanInfosUnit unit;
	public PanInfosBuilding build;

	public PanGrid gridCubes;

	private ArrayList<Cube> _cubes = new ArrayList<>();
	public ArrayList<ButtonBlocks> cubes = new ArrayList<>();

	// =========================================================================================================================

	public PanInfos(Game game) {
		this.game = game;

		setBackground(Color.LIGHT_GRAY);

		resource = new PanInfosResource();
		unit = new PanInfosUnit(game);
		build = new PanInfosBuilding(game);
		gridCubes = new PanGrid();

		gridCubes.setSize(getSize());

		// =========================================================================================================================

		for (int itemID : ItemTable.getItemIDList())
			_cubes.add(new Cube(itemID));

		_cubes.add(new Tree().getCube());
		_cubes.add(new E().getCube());
		_cubes.add(ItemTable.createBuilding(new Building(null, ItemID.CASTLE, 0, 0, 0, true)).getCube());

		for (Cube cube : _cubes) {
			ButtonBlocks button = createButtonCube(cube);
			cubes.add(button);
			gridCubes.addMenu(button);
		}

		ButtonBlocks.group(cubes);

		// =========================================================================================================================

		add(resource);
		add(unit);
		add(build);
		add(gridCubes);

		clear();
	}

	// =========================================================================================================================

	public ButtonBlocks createButtonCube(Cube cube) {
		ButtonBlocks button = new ButtonBlocks();

		button.setSelectable(true);
		button.setBackground(Color.GRAY);
		button.setForeground(Color.WHITE);
		button.setTextBackground(new Color(75, 75, 75));
		button.setTextYLocation(5, ButtonBlocks.BOTTOM);

		button.setModel(new CubeClient(cube));

		// TODO [Improve] getName(itemID)
		String name = ItemTableClient.getName(cube.multibloc == null ? cube.getItemID() : cube.multibloc.itemID);
		if (name == null)
			name = "NULL";

		button.setFont(new Font("monospace", Font.PLAIN, 12));
		button.setText(name);

		button.setClickListener(new ClickListener() {
			@Override
			public void leftClick() {
				game.setNextCube(cube);
			}
		});

		return button;
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
