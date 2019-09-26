package client.session;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import client.keys.Keyboard;
import client.messages.CommandExecutor;
import client.messages.MessageManager;
import client.textures.TexturePack;
import client.window.Fen;
import client.window.graphicEngine.calcul.Camera;
import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.calcul.Point3D;
import client.window.graphicEngine.extended.ModelCube;
import client.window.graphicEngine.extended.ModelMap;
import client.window.panels.StateHUD;
import data.ItemTable;
import data.enumeration.Face;
import data.enumeration.Orientation;
import data.map.Coord;
import data.map.Cube;

public class Session implements Serializable {
	private static final long serialVersionUID = 8569378400890835470L;

	public ModelMap map;

	public transient TexturePack texturePack;

	public GameMode gamemode = GameMode.CLASSIC;
	public Player player = new Player("Félix");

	public Action action = Action.CREA_ADD;

	// ================================

	private Engine engine;

	public Camera camera;
	public Keyboard keyboard;
	public Fen fen;
	public TickClock clock;

	// ============= Target ===================

	public ModelCube cubeTarget;
	public Face faceTarget;

	/** Next cube to add (its coords aren't valid) */
	private Cube nextCube;
	/** Coord of the preview cube */
	public Coord previousPreview;

	// ============== F3 (Dev infos) ==================

	/** The orientation of the player */
	public Orientation playerOrientation = Orientation.NORTH;
	/** Chronometric marks */
	public long timeMat, timeDraw, timeQuadri;
	/** Number of cubes and chunks displayed */
	public int nbChunks, nbFaces;
	/** Number of frames displayed the last second */
	public int fps;
	/** Number of state-checks of the mouse and keyboard */
	public int ticksKeyBoard;
	/** Number of steps of the simulated environment */
	public int ticksPhys;
	/** true : show on-screen the dev infos */
	public boolean devlop;

	// ============ Options ============
	/** Max frames/seconde allowed */
	public int FPSmax = 60;

	/** true : currently generating an image */
	public boolean processing = false;

	/** State of the window [GAME, PAUSE, DIALOG, ...] */
	public StateHUD stateGUI = StateHUD.GAME;

	// =============== Dialog =================
	public MessageManager messages;
	public CommandExecutor commands;

	// =========================================================================================================================

	public Session(ModelMap m, boolean with3DEngine) throws AWTException {
		map = m;

		clock = new TickClock(this);
		clock.add(map);

		keyboard = new Keyboard(this);

		camera = new Camera(new Point3D(15, 35, 0));
		camera.setVx(90);
		camera.setVy(-65);

		if (with3DEngine) {
			texturePack = new TexturePack();
			engine = new Engine(camera, map, texturePack);
			ModelCube.texturePack = texturePack;
		}
	}

	// =========================================================================================================================

	public void start() throws AWTException {
		messages = new MessageManager(this, fen.gui);
		commands = new CommandExecutor(this, messages);

		fen.start();
		keyboard.start();
		new Thread(clock).start();
	}

	public void setTexturePack(TexturePack texturePack) {
		this.texturePack = texturePack;

		engine.texturePack = texturePack;
		if (fen != null)
			fen.gui.updateTexturePack();
	}

	// =========================================================================================================================

	public void setGameMode(GameMode gameMode) {
		this.gamemode = gameMode;

		fen.gui.hideMenu();

		switch (gameMode) {
		case CLASSIC:
			// Realign the camera with the grid
			camera.setVx(90);
			camera.setVy(-65);
			// Replace the camera at the correct altitude
			camera.vue.y = 35;

			// Actualize the mouse position
			fen.mouseX = MouseInfo.getPointerInfo().getLocation().x;
			fen.mouseY = MouseInfo.getPointerInfo().getLocation().y;

			fen.cursorVisible(true);
			break;
		case CREATIVE:
			keyboard.mouseToCenter();

			fen.cursorVisible(false);
			break;
		case SPECTATOR:
			break;
		}
	}

	// =========================================================================================================================

	public void setAction(Action action) {
		this.action = action;
	}

	public void setNextCube(Cube cube) {
		nextCube = cube;
	}

	public Cube getNextCube() {
		if (nextCube != null && nextCube.multibloc != null) {
			if (nextCube.build != null)
				return ItemTable.createBuilding(nextCube.build).getCube();
			return nextCube.multibloc.clone().getCube();
		}
		return nextCube;
	}

	// =========================================================================================================================

	public void setTarget(int x, int y) {
		engine.cursorX = x;
		engine.cursorY = y;
	}

	// =========================================================================================================================

	public BufferedImage getImage(int w, int h) {
		return engine.getImage(w, h);
	}

	public void updateTimeDev() {
		timeMat = engine.timeMat - engine.timeStart;
		timeDraw = engine.timeDraw - engine.timeMat;
		timeQuadri = engine.timeEnd - engine.timeDraw;

		nbChunks = map.nbChunks;
		nbFaces = map.nbFaces;

		map.nbChunks = 0;
	}

	public void targetUpdate() {
		boolean sameTarget = cubeTarget == Engine.cubeTarget && faceTarget == Engine.faceTarget;

		// Removes previous selection display
		if (cubeTarget != null)
			if (!sameTarget) {
				// Removes highlight from previous
				map.setHighlight(cubeTarget, false);

				// Removes preview cubes
				if (map.gridContains(previousPreview) && map.gridGet(previousPreview).isPreview())
					map.remove(previousPreview);
			}

		// Refresh target
		cubeTarget = Engine.cubeTarget;
		faceTarget = Engine.faceTarget;

		if (gamemode == GameMode.CLASSIC)
			if (cubeTarget != null)
				if (action == Action.CREA_ADD) {
					// If same target : No need to do something more than the previous iteration
					if (sameTarget)
						return;

					// Test if there is a cube(s) to add
					Cube cubeToAdd = getNextCube();
					if (cubeToAdd == null)
						return;

					// Calcul coords of the new cube(s)
					previousPreview = new Coord(cubeTarget).face(faceTarget);
					cubeToAdd.setCoords(previousPreview);

					// Test if there is place for the cube(s) at the coords
					if (!map.add(cubeToAdd))
						return;

					// Mark cube(s) as "selection display"
					map.setPreview(previousPreview, true);
					map.setTargetable(previousPreview, false);
					map.setHighlight(previousPreview, true);
				} else if (action == Action.CREA_DESTROY) {
					map.setHighlight(cubeTarget, true);
				} else if (action == Action.MOUSE) {
					map.setHighlight(cubeTarget, true);
				}
	}

	// =========================================================================================================================

	public void exec(String line) {
		// TODO [Improve] Detect the player executing the command
		commands.exec("Félix", line);
	}
}
