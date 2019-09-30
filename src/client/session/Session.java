package client.session;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

import client.keys.Keyboard;
import client.messages.CommandExecutor;
import client.messages.Message;
import client.messages.MessageManager;
import client.textures.TexturePack;
import client.window.Client;
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
import data.map.Map;

public class Session implements Serializable {
	private static final long serialVersionUID = 8569378400890835470L;

	// ============= Emitter ===================
	Client client;

	// ============= ===================
	public ModelMap map;

	public transient TexturePack texturePack;

	public GameMode gamemode = GameMode.CLASSIC;
	public Player player = new Player("Felix");

	private Action action;
	public Action unitAction;

	// ================================

	private boolean with3DEngine = true;
	private Engine engine;

	public Camera camera;
	public Keyboard keyboard;
	public Fen fen;

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

	public Session() throws AWTException {
		texturePack = new TexturePack();
		ModelCube.texturePack = texturePack;

		new Fen(this);

		client = new Client(this);

		keyboard = new Keyboard(this);

		camera = new Camera(new Point3D(15, 35, 0));
		camera.setVx(90);
		camera.setVy(-65);

		setAction(Action.MOUSE);

		send(player);
	}

	// =========================================================================================================================

	public void send(Object obj) {
		try {
			client.out.writeObject(obj);
			client.out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void receive(Object obj) {
		System.out.println("Received : " + obj.toString());

		if (obj instanceof Message)
			messages.receive((Message) obj);
		else if (obj instanceof Map)
			setMap(new ModelMap((Map) obj));
		else
			System.out.println("Unknown object");
	}

	// =========================================================================================================================

	public void start() throws AWTException {
		messages = new MessageManager(this, fen.gui);
		commands = new CommandExecutor(this, messages);

		fen.start();
		keyboard.start();
	}

	// =========================================================================================================================

	public void setMap(ModelMap map) {
		this.map = map;

		if (with3DEngine)
			engine = new Engine(camera, this.map, texturePack);

		try {
			start();
		} catch (AWTException e) {
			e.printStackTrace();
		}
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

			// Deselect
			fen.gui.select(null);

			fen.setCursorVisible(true);
			break;
		case CREATIVE:
			keyboard.mouseToCenter();

			fen.setCursorVisible(false);
			break;
		case SPECTATOR:
			break;
		}
	}

	// =========================================================================================================================

	public void setAction(Action action) {
		this.action = action;
		if (fen != null)
			fen.updateCursor();
	}

	public Action getAction() {
		return action;
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
		fen.updateCursor();

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
