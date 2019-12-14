package client.session;

import java.awt.MouseInfo;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

import client.Client;
import client.keys.Keyboard;
import client.textures.TexturePack;
import client.window.Fen;
import client.window.graphicEngine.calcul.Camera;
import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.calcul.Point3D;
import client.window.graphicEngine.extended.ModelCube;
import client.window.graphicEngine.extended.ModelMap;
import client.window.panels.StateHUD;
import data.dynamic.TickClock;
import data.id.ItemTable;
import data.map.Coord;
import data.map.Cube;
import data.map.Map;
import data.map.enumerations.Face;
import data.map.enumerations.Orientation;
import data.map.units.Unit;
import server.game.GameMode;
import server.game.Player;
import server.game.messages.Message;
import server.send.Action;
import server.send.SendAction;
import utils.FlixBlocksUtils;

public class Session implements Serializable {
	private static final long serialVersionUID = 8569378400890835470L;

	// ============= Emitter ===================
	Client client;

	// ============= ===================
	public ModelMap map;

	public transient TexturePack texturePack;

	public GameMode gamemode = GameMode.CLASSIC;
	public Player player = new Player("Felix");

	private UserAction action;
	public Action unitAction;

	// ================================

	private boolean with3DEngine = true;
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

	// =========================================================================================================================

	public Session() {
		texturePack = new TexturePack();
		ModelCube.texturePack = texturePack;

		new Fen(this);

		client = new Client(this);

		keyboard = new Keyboard(this);

		camera = new Camera(new Point3D(15, 35, 0));
		camera.setVx(90);
		camera.setVy(-65);

		setAction(UserAction.MOUSE);

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
		if (obj instanceof Map)
			setMap(new ModelMap((Map) obj));
		else if (obj instanceof Message)
			messages.receive((Message) obj);
		else if (obj instanceof SendAction)
			receiveSend((SendAction) obj);
		else
			System.err.println("Unknown object");
	}

	// =========================================================================================================================

	public void start() {
		messages = new MessageManager(this, fen.gui);

		fen.start();
		keyboard.start();
		Thread tClock = new Thread(clock);
		tClock.setName("Client clock");
		tClock.start();
	}

	// =========================================================================================================================

	public void setMap(ModelMap map) {
		this.map = map;

		if (with3DEngine)
			engine = new Engine(camera, this.map, texturePack);

		clock = new TickClock();
		clock.add(map);

		start();
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

	public void setAction(UserAction action) {
		this.action = action;
		if (fen != null)
			fen.updateCursor();
	}

	public UserAction getAction() {
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
				if (action == UserAction.CREA_ADD) {
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
				} else if (action == UserAction.CREA_DESTROY) {
					map.setHighlight(cubeTarget, true);
				} else if (action == UserAction.MOUSE) {
					map.setHighlight(cubeTarget, true);
				}
	}

	// =========================================================================================================================
	// Receive

	public void receiveSend(SendAction send) {
		System.out.println("[Client RECEIVE] " + send.action + " done: " + send.done);
		switch (send.action) {
		case ADD:
			// map.add(send.cube);
			break;
		case REMOVE:
			map.remove(send.coord);
			break;

		case UNIT_GOTO:
			map.getUnit(send.id1).goTo(map, send.coord);
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
		default:
			FlixBlocksUtils.debug("[Client] missing receiveSend(): " + send.action);
			break;
		}
	}

	// =========================================================================================================================
	// Send

	public void unitDoAction() {
		if (unitAction == null) {
			System.out.println("Action NULL");
			return;
		}

		Unit unit = fen.gui.unit;
		Coord cube = cubeTarget.coords();// Pointed cube
		Coord cubeAir = cubeTarget.coords().face(faceTarget);// Cube adjacent to the pointed face (in the air)

		switch (unitAction) {
		case UNIT_GOTO:
			send(SendAction.goTo(unit, cubeAir));
			break;
		case UNIT_BUILD:
			send(SendAction.build(unit, map.getBuilding(cube)));
			break;
		case UNIT_HARVEST:
			send(SendAction.harvest(unit, cube));
			break;
		case UNIT_STORE:
			send(SendAction.store(unit, map.getBuilding(cube)));
			break;
		default:
			FlixBlocksUtils.debug("[Client] Missing unitDoAction(): " + unitAction);
			break;
		}
	}

}
