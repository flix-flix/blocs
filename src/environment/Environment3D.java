package environment;

import data.Gamer;
import data.id.ItemTable;
import data.id.ItemType;
import data.map.Cube;
import data.map.buildings.Building;
import data.map.enumerations.Orientation;
import data.map.units.Unit;
import environment.extendsData.CubeClient;
import environment.extendsData.MapClient;
import graphicEngine.calcul.Camera;
import graphicEngine.calcul.Engine;
import server.send.Action;
import server.send.SendAction;
import utils.Utils;

public class Environment3D implements Client {

	protected PanEnvironment panel;

	// =============== Engine ===============
	protected Engine engine;
	protected MapClient map;
	protected Camera camera;

	// =============== Target ===============
	public Target target = new Target();

	// =============== Adding ===============
	/** Next cube(s) to add (its coords aren't valid) */
	private Cube cubeToAdd;
	public Gamer gamer = Gamer.nullGamer;

	// =============== Preview ===============
	/** Previewed cube */
	public CubeClient previewed;

	// =============== Thread ===============
	boolean run = true;
	/** true : currently processing a new image */
	private boolean processing = false;
	/** true : suspend the generation of new images */
	private boolean paused = false;

	// =============== Options ===============
	/** Max frames/seconde allowed */
	public int FPSmax = 30;

	// =============== F3 (Dev infos) ===============
	/** Number of frames displayed the last second */
	public int fps;

	/** Chronometric marks */
	public long timeMat, timeDraw, timeQuadri;
	/** Number of chunks and faces displayed */
	public int nbChunks, nbFaces;

	/** Number of state-checks of the mouse and keyboard */
	public int ticksKeyBoard;
	/** Number of steps of the simulated environment */
	public int ticksPhys;

	// =========================================================================================================================

	public Environment3D() {
		panel = new PanEnvironment(this);
		engine = new Engine(null, null);
	}

	public Environment3D(MapClient map, Camera camera) {
		this();
		this.map = map;
		this.camera = camera;

		panel.setCamera(camera);
		engine.setModelCamera(map, camera);
	}

	// =========================================================================================================================
	// Thread

	public void start() {
		if (!run)
			return;

		panel.setCamera(camera);
		engine.setModelCamera(map, camera);

		Thread thread = new Thread(new RefreshImage());
		thread.setName("Refresh Image");
		thread.start();
	}

	public void stop() {
		run = false;
	}

	// =========================================================================================================================
	// Client

	@Override
	public void send(Object obj) {
		receive(obj);
	}

	@Override
	public void receive(Object obj) {
		if (obj instanceof SendAction)
			receiveAction((SendAction) obj);
		else
			Utils.debug("[Receive] Unknown object (" + (obj == null ? "null" : obj.getClass()) + ")");
	}

	public void receiveAction(SendAction send) {
		if (send.action != Action.SERVER_TICKS_PHYS)
			System.out.println("[Client Receive] " + send.action + " done: " + send.done);

		switch (send.action) {
		case ADD:
			map.add(send.cube);
			break;
		case REMOVE:
			map.remove(send.coord);
			break;

		case UNIT_GOTO:
			map.getUnit(send.id1).setPath(send.path);
			break;
		case UNIT_ARRIVE:
			map.getUnit(send.id1).arrive(map);
			break;

		case UNIT_BUILD:
			map.getUnit(send.id1).building(map, map.getBuilding(send.id2));
			break;

		case UNIT_HARVEST:
			if (send.done)
				map.getUnit(send.id1)._doHarvest(map);
			else
				map.getUnit(send.id1).harvest(map, send.coord);
			break;

		case UNIT_STORE:
			if (send.done)
				map.getUnit(send.id1)._doStore(map, map.getBuilding(send.id2));
			else
				map.getUnit(send.id1).store(map, map.getBuilding(send.id2));
			break;

		case SERVER_TICKS_PHYS:
			ticksPhys = send.ticks;
			break;
		default:
			Utils.debug("[Client] missing receiveSend(): " + send.action);
			break;
		}
	}

	// =========================================================================================================================
	// Engine setters

	public void setEngineBackground(int x) {
		engine.setBackground(x);
	}

	/** Set target location (e.g. mouse location or cross-indicator) */
	public void setTarget(int x, int y) {
		engine.setTarget(x, y);
	}

	public void setTargetCenter() {
		setTarget(panel.envWidth / 2, panel.envHeight / 2);
	}

	public void setTargetNull() {
		setTarget(-10_000, -10_000);
	}

	public void setMap(MapClient map) {
		this.map = map;
		engine.setModelCamera(map, camera);
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
		engine.setModelCamera(map, camera);
	}

	// =========================================================================================================================

	public void setCubeToAdd(Cube cube) {
		cubeToAdd = cube;
		cubeToAdd = cloneCubeToAdd();
	}

	/**
	 * @param clockWise
	 *            - true: North will face East (false: West)
	 */
	public void rotateCubeToAdd(boolean clockWise) {
		cubeToAdd.rotate(clockWise);

		if (previewed != null) {
			removePreview();
			addPreview();
		}
	}

	public Cube cloneCubeToAdd() {
		if (cubeToAdd == null)
			return null;

		// TODO [Improve] Cube, MultiBlock and MultiCube clone() (separated from data)

		if (cubeToAdd.unit != null) {
			Unit u = cubeToAdd.unit;
			Unit unit = new Unit(u.getItemID(), gamer, u.coord.x, u.coord.y, u.coord.z);
			unit.rotation = u.rotation;
			unit.orientation = u.orientation;

			Cube cube = cubeToAdd.clone();
			cube.unit = unit;
			return cube;
		}

		if (cubeToAdd.build != null) {
			Orientation ori = cubeToAdd.multicube.getOrientation();

			Building build = ItemTable.create(cubeToAdd.build.getItemID()).build;
			build.setGamer(gamer);

			build.getMulti().rotate(ori);

			return build.getCube();
		}

		if (ItemTable.getType(cubeToAdd) == ItemType.MULTICUBE) {
			Orientation ori = cubeToAdd.multicube.getOrientation();
			Cube cube = ItemTable.create(cubeToAdd.multicube.itemID);
			cube.multicube.rotate(ori);
			return cube;
		}

		return cubeToAdd.clone();
	}

	// =========================================================================================================================

	public boolean isAddable() {
		if (previewed == null)
			return false;

		if (previewed.multicube != null && !previewed.multicube.valid)
			return false;

		return true;
	}

	/**
	 * Add {@link #cubeToAdd} next to : {@link Target}
	 * 
	 * @return The added cube or null if it hasn't been added
	 */
	public Cube addCube() {
		if (target == null || target.face == null)
			return null;

		if (!isAddable())
			return null;

		// Add the cube to the server
		send(SendAction.add(cubeToAdd));

		// Clone to avoid issue with identical datas
		Cube added = cubeToAdd;
		cubeToAdd = cloneCubeToAdd();

		return added;
	}
	// =========================================================================================================================

	public void addPreview() {
		if (cubeToAdd == null)
			return;

		// ===== Set coords of the cube(s) =====
		cubeToAdd.setCoords(target.getAir());

		// Test if there is place for the cube(s) at the coords
		previewed = map.addPreview(cubeToAdd);
	}

	public void removePreview() {
		if (previewed != null) {
			map.remove(previewed);
			previewed = null;
		}
	}

	// =========================================================================================================================

	public void updateTarget() {
		Target target = new Target(engine);

		boolean sameTarget = target.equals(this.target, isNeededQuadriPrecision());

		// If different target
		if (!sameTarget) {
			// If it replace an existant one => Update
			if (this.target.isValid())
				loseTarget();

			this.target = target;

			// If target isn't void => Update
			if (target.isValid())
				gainTarget();
		}
	}

	// =========================================================================================================================

	public void updateTimeDev() {
		timeMat = engine.timeMat - engine.timeStart;
		timeDraw = engine.timeDraw - engine.timeMat;
		timeQuadri = engine.timeEnd - engine.timeDraw;

		nbChunks = map.nbChunks;
		nbFaces = map.nbFaces;
	}

	// =========================================================================================================================
	// Interface

	/** Called on new (non-null) target */
	public void gainTarget() {
	}

	/** Called on new Target (if previous was non-null) */
	public void loseTarget() {
	}

	/** Ask if the target must take care of the quadri id */
	public boolean isNeededQuadriPrecision() {
		return false;
	}

	/** Called each second : to update development data */
	public void oneSecondTick() {
	}

	/** Called when a new Image have been generated */
	public void repaintEnvironment() {
		panel.repaint();
	}

	// =========================================================================================================================
	// Getters/Setters

	public PanEnvironment getPanel() {
		return panel;
	}

	public void switchDevlopMode() {
		panel.showEngineInfos = !panel.showEngineInfos;
	}

	public Camera getCamera() {
		return camera;
	}

	public MapClient getMap() {
		return map;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public boolean isPaused() {
		return paused;
	}

	public Target getTarget() {
		return target;
	}

	// =========================================================================================================================
	// Repaint

	private void generateNewImage() {
		if (panel.envWidth > 0 && panel.envHeight > 0)
			panel.setImage(engine.getImage(panel.envWidth, panel.envHeight));

		updateTimeDev();
		updateTarget();

		if (!paused)
			repaintEnvironment();
	}

	/** Refresh the image "FPSmax times" per second and when panel is resized */
	class RefreshImage implements Runnable {
		public void run() {
			// Count the number of frames displayed since the last "second timer" restart
			int fps = 0;
			// Store the time which the last second starts
			long lastSecond = System.currentTimeMillis();
			// The time where a new image would be needed
			long waitTill = 0;

			while (run) {
				// Update FPS infos
				if (System.currentTimeMillis() - lastSecond >= 1000) {
					lastSecond = System.currentTimeMillis();

					Environment3D.this.fps = fps;
					fps = 0;

					oneSecondTick();
				}

				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// If panel resized or enough time flied since previous image
				// => Start the generation of a new image
				if (!paused && !processing && (waitTill <= System.currentTimeMillis() || panel.resized)) {
					panel.resized = false;
					processing = true;

					waitTill = System.currentTimeMillis() + 1000 / FPSmax;

					Thread thread = new Thread(new ImageGenerator());
					thread.setName("Image generator");
					thread.start();

					fps++;
				}
			}
		}
	}

	/** Generates a new image */
	class ImageGenerator implements Runnable {
		public void run() {
			generateNewImage();
			processing = false;
		}
	}
}
