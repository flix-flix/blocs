package game.panels.menus.infos;

import java.awt.Color;
import java.lang.Thread.State;
import java.util.ArrayList;

import data.id.ItemTable;
import data.map.Cube;
import game.Game;
import game.StateHUD;
import utils.panels.PanCard;
import utils.panels.PanGrid;
import utilsBlocks.ButtonBlocks;

public class PanInfos extends PanCard {
	private static final long serialVersionUID = -7621681231232278749L;

	private static final String CUBES = "cubes";
	private static final String UNIT = "unit";
	private static final String BUILD = "build";
	private static final String RESOURCE = "res";

	// =============== Panels ===============
	private PanGrid gridCubes;
	private PanInfosUnit unit;
	private PanInfosBuilding build;
	private PanInfosResource resource;

	// =============== Buttons cubes ===============
	private ArrayList<ButtonBlocks> buttonsCubes = new ArrayList<>();

	// =============== Thread ===============
	private boolean run = true;
	private Thread update;

	// =============== Data ===============
	private Game game;
	private Cube cube;

	// =========================================================================================================================

	public PanInfos(Game game) {
		this.game = game;

		setBackground(Color.LIGHT_GRAY);

		put(RESOURCE, resource = new PanInfosResource());
		put(UNIT, unit = new PanInfosUnit(game));
		put(BUILD, build = new PanInfosBuilding(game));
		put(CUBES, gridCubes = new PanGrid());

		// =========================================================================================================================
		// gridCubes

		for (int itemID : ItemTable.getItemIDList()) {
			Cube cube = ItemTable.create(itemID);

			if (ItemTable.isDevelopment(cube.getItemID()))
				continue;

			ButtonBlocks button = new ButtonCube(game, cube);
			buttonsCubes.add(button);
			gridCubes.gridAdd(button);
		}

		ButtonBlocks.group(buttonsCubes);

		// =========================================================================================================================

		update = new Thread(new Update());
		update.setName("Update PanInfos");
		update.start();

		hide();
	}

	// =========================================================================================================================

	/** Hide the panel */
	public void hide() {
		super.hide();

		resource.clear();
		build.clear();
		unit.clear();
	}

	/** Show the grid of cubes */
	public void showCubes() {
		show(CUBES);
	}

	// =========================================================================================================================

	/** Display the infos of the cube */
	public void displayInfosOf(Cube cube) {
		this.cube = cube;

		if (cube == null) {
			clear();
			return;
		}

		if (cube.unit != null) {
			unit.setCube(cube);
			show(UNIT);
		} else if (cube.build != null) {
			build.setCube(cube);
			show(BUILD);
		} else if (cube.hasResource()) {
			resource.setCube(cube);
			show(RESOURCE);
		} else
			clear();

		synchronized (update) {
			if (update.getState() == State.WAITING)
				update.notify();
		}
	}

	private void clear() {
		cube = null;
		hide();
	}

	// =========================================================================================================================

	public void stop() {
		run = false;
	}

	// =========================================================================================================================

	private class Update implements Runnable {
		@Override
		public void run() {
			while (run) {
				while (cube == null)
					try {
						synchronized (update) {
							update.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				// ========================================

				String name = getVisibleName();

				if (name == null) {
					clear();
					continue;
				}

				if (game.getStateHUD() != StateHUD.PAUSE)
					switch (name) {
					case UNIT:
						unit.update();
						break;
					case BUILD:
						build.update();
						break;
					case RESOURCE:
						resource.update();
						break;
					default:
						cube = null;
						continue;
					}

				// ========================================

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
