package game;

import java.awt.Cursor;
import java.io.IOException;

import javax.swing.JPanel;

import data.Gamer;
import data.dynamic.TickClock;
import data.id.ItemTable;
import data.id.ItemTableClient;
import data.map.Coord;
import data.map.Cube;
import data.map.Map;
import data.map.buildings.Building;
import data.map.resources.ResourceType;
import data.map.units.Unit;
import environment.Environment3D;
import environment.Target;
import environment.extendsData.CubeClient;
import environment.extendsData.MapClient;
import environment.textures.TexturePack;
import game.panels.PanGame;
import graphicEngine.calcul.Camera;
import graphicEngine.calcul.Point3D;
import server.Server;
import server.ServerDescription;
import server.game.GameMode;
import server.game.Player;
import server.game.messages.Message;
import server.send.Action;
import server.send.SendAction;
import utils.Utils;
import window.Displayable;
import window.Fen;
import window.KeyBoard;

public class Game extends Environment3D implements Displayable {
	public Fen fen;

	// =============== Pan ===============
	PanGame panel;

	// ============= Cursor ===================
	private Cursor cursorGoto, cursorBuild, cursorAttack;
	private Cursor cursorDrop, cursorDropWood, cursorDropStone, cursorDropWater;
	private Cursor cursorAxe, cursorPickaxe, cursorBucket;

	private boolean cursorVisible = true;

	// =============== Server ===============
	private Client client;
	/** Local server if not online play */
	private Server server;
	public Player player = new Player("Felix");

	// =============== Data ===============
	public Gamer gamer = new Gamer(1);
	private UserAction userAction;
	public CameraMode cameraMode = CameraMode.CLASSIC;
	public GameMode gameMode = GameMode.CLASSIC;

	// =============== Action ===============
	public Unit selectedUnit;
	public Building selectedBuilding;
	public Action unitAction;

	// =============== ===============
	public transient TexturePack texturePack;

	public KeyboardGame keyboard;

	private TickClock clock;

	// =============== Devlop Data (F3) ===============
	/** Number of state-checks of the mouse and keyboard */
	public int ticksKeyBoard;
	/** Number of steps of the simulated environment */
	public int ticksPhys;

	/** State of the window [GAME, PAUSE, DIALOG, ...] */
	private StateHUD stateHUD = StateHUD.GAME;

	// =============== Adding ===============
	public String errorMsg = "UNKNOWN";

	// =============== Adding ===============
	/** Next cube to add (its coords aren't valid) */
	private Cube nextCube;
	/** Coord of the preview cube */
	public Coord previousPreview;

	// =============== Dialog ===============
	public MessageManager messages;

	// =========================================================================================================================

	public Game(Fen fen, Server server) throws IOException {
		this(fen, server.getDescription());
		this.server = server;
	}

	public Game(Fen fen, ServerDescription description) throws IOException {
		this.fen = fen;
		client = new Client(this, description);

		// ======================================

		texturePack = ItemTableClient.getTexturePack();
		ResourceType.setTextureFolder(texturePack.getFolder());

		generateCursor();

		// ======================================

		camera = new Camera(new Point3D(15, 35, 0), 90, -65);
		keyboard = new KeyboardGame(this);
		clock = new TickClock("Game (Client) Clock");

		// ======================================

		panel = new PanGame(this);
		super.panel = panel.panEnv;

		// ======================================

		setUserAction(UserAction.MOUSE);
		setCameraMode(CameraMode.CLASSIC);

		send(player);
	}

	// =========================================================================================================================

	public static Game startLocalServer(Fen fen) throws IOException {
		// TODO [Feature] Change port if already used
		Server server = new Server();
		server.start();

		Game game = new Game(fen, server);

		return game;
	}

	public void connexionLost(String errorMsg) {
		stateHUD = StateHUD.ERROR;
		this.errorMsg = errorMsg;
		stop();

		panel.error();
		fen.updateCursor();
	}

	// =========================================================================================================================

	public void start() {
		panel.setGUIVisible(true);
		messages = new MessageManager(this);

		super.start();
		clock.start();
		keyboard.start();

		panel.start();
	}

	@Override
	public void stop() {
		super.stop();

		keyboard.stop();
		clock.stop();
		panel.stop();

		client.close();

		if (server != null)
			server.stop();
	}

	// =========================================================================================================================

	public void setMap(MapClient map) {
		this.map = map;

		clock.add(map);

		start();
	}

	// =========================================================================================================================

	public void setCameraMode(CameraMode cameraMode) {
		this.cameraMode = cameraMode;
		panel.updateEnvBounds();

		switch (cameraMode) {
		case CLASSIC:
			gameMode = GameMode.CLASSIC;
			// Deselect
			clearSelected();

			// Replace the camera
			camera.setVx(90);
			camera.setVy(-65);
			camera.vue.y = 35;

			keyboard.setTargetOnMouse();
			keyboard.setSpeedModifier(2);

			setCursorVisible(true);
			panel.setGUIVisible(true);
			break;
		case FIRST_PERSON:
			gameMode = GameMode.CREATIVE;

			keyboard.mouseToCenter();
			keyboard.setSpeedModifier(1);

			setCursorVisible(false);
			panel.setGUIVisible(false);
			break;
		}

		targetUpdate();
	}

	public void setUserAction(UserAction action) {
		// If action changed
		if (action != userAction)
			panel.setCubesVisible(action == UserAction.CREA_ADD);

		this.userAction = action;
	}

	// =========================================================================================================================
	// Cursor

	@Override
	public Cursor getCursor() {
		if (!cursorVisible)
			return Fen.cursorInvisible;

		if (stateHUD == StateHUD.ERROR)
			return ItemTableClient.defaultCursor;

		if (!target.isValid())
			return ItemTableClient.defaultCursor;

		Cube cube = target.cube;

		if (getAction() == UserAction.MOUSE)

			if (cube != null && selectedUnit != null && selectedUnit.getGamer().equals(gamer))

				if (unitAction == Action.UNIT_HARVEST)
					switch (ItemTable.getResourceType(cube.getItemID())) {
					case WOOD:
						return cursorAxe;
					case STONE:
						return cursorPickaxe;
					case WATER:
						return cursorBucket;
					default:
						return ItemTableClient.defaultCursor;
					}

				else if (unitAction == Action.UNIT_BUILD)
					return cursorBuild;

				else if (unitAction == Action.UNIT_STORE)
					switch (selectedUnit.getResource().getType()) {
					case WOOD:
						return cursorDropWood;
					case STONE:
						return cursorDropStone;
					case WATER:
						return cursorDropWater;
					default:
						return cursorDrop;
					}

				else if (unitAction == Action.UNIT_ATTACK)
					return cursorAttack;

				else if (unitAction == Action.UNIT_GOTO)
					return cursorGoto;

		return ItemTableClient.defaultCursor;
	}

	public void generateCursor() {
		cursorGoto = Utils.createCursor(texturePack.getFolder() + "cursor/cursorGoto");
		cursorBuild = Utils.createCursor(texturePack.getFolder() + "cursor/cursorBuild");
		cursorAttack = Utils.createCursor(texturePack.getFolder() + "cursor/cursorAttack");

		cursorDrop = Utils.createCursor(texturePack.getFolder() + "cursor/cursorDrop");
		cursorDropWood = Utils.createCursor(texturePack.getFolder() + "cursor/cursorDropWood");
		cursorDropStone = Utils.createCursor(texturePack.getFolder() + "cursor/cursorDropStone");
		cursorDropWater = Utils.createCursor(texturePack.getFolder() + "cursor/cursorDropWater");

		cursorAxe = Utils.createCursor(texturePack.getFolder() + "cursor/cursorAxe");
		cursorPickaxe = Utils.createCursor(texturePack.getFolder() + "cursor/cursorPickaxe");
		cursorBucket = Utils.createCursor(texturePack.getFolder() + "cursor/cursorBucket");
	}

	public void setCursorVisible(boolean visible) {
		cursorVisible = visible;
		fen.updateCursor();
	}

	// =========================================================================================================================

	public void pause() {
		stateHUD = StateHUD.PAUSE;
		keyboard.setPaused(true);

		setCursorVisible(true);
		setTargetNull();

		panel.panEnv.pause.setVisible(true);
	}

	public void resume() {
		stateHUD = StateHUD.GAME;
		keyboard.setPaused(false);

		if (cameraMode == CameraMode.FIRST_PERSON) {
			setCursorVisible(false);
			keyboard.mouseToCenter();
		}

		panel.updateMap();

		panel.panEnv.pause.close();

		keyboard.setTargetOnMouse();
		fen.requestFocusInWindow();
	}

	public void exit() {
		fen.returnToMainMenu();
	}

	// =========================================================================================================================

	public void setTexturePack(TexturePack texturePack) {
		this.texturePack = texturePack;
	}

	// =========================================================================================================================

	public UserAction getAction() {
		return userAction;
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
	// Selected

	public void select(Cube cube) {
		selectedUnit = null;
		selectedBuilding = null;
		panel.displayInfosOf(cube);

		if (cube != null)
			if (cube.unit != null)
				selectedUnit = cube.unit;
			else if (cube.build != null)
				selectedBuilding = cube.build;
	}

	public void clearSelected() {
		select(null);
	}

	public void unitDoAction() {
		if (selectedUnit == null)
			return;
		if (unitAction == null) {
			Utils.debug("Action NULL");
			return;
		}

		// Pointed cube
		Coord cube = target.cube.coords();
		// Cube adjacent to the pointed face (in the air)
		Coord cubeAir = target.cube.coords().face(target.face);

		switch (unitAction) {
		case UNIT_GOTO:
			send(SendAction.goTo(selectedUnit, cubeAir));
			break;
		case UNIT_BUILD:
			send(SendAction.build(selectedUnit, map.getBuilding(cube)));
			break;
		case UNIT_HARVEST:
			send(SendAction.harvest(selectedUnit, cube));
			break;
		case UNIT_STORE:
			send(SendAction.store(selectedUnit, map.getBuilding(cube)));
			break;
		default:
			Utils.debug("[Client] Missing unitDoAction(): " + unitAction);
			break;
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
			Utils.debug("[Client] missing receiveSend(): " + send.action);
			break;
		}
	}

	// =========================================================================================================================
	// Send

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
			setMap(new MapClient((Map) obj));
		else if (obj instanceof Message)
			messages.receive((Message) obj);
		else if (obj instanceof SendAction)
			receiveSend((SendAction) obj);
		else
			Utils.debug("[Receive] Unknown object");
	}

	// =========================================================================================================================
	// Getters

	public Target getTarget() {
		return target;
	}

	public StateHUD getStateHUD() {
		return stateHUD;
	}

	public void setStateHUD(StateHUD stateHUD) {
		panel.panEnv.help.setVisible(stateHUD != StateHUD.DIALOG);
		this.stateHUD = stateHUD;
	}

	// =========================================================================================================================
	// Environment3D

	@Override
	public void targetUpdate() {
		super.targetUpdate();
		fen.updateCursor();
	}

	@Override
	public void gainTarget() {
		if (cameraMode == CameraMode.CLASSIC)
			if (target.cube != null)
				if (userAction == UserAction.CREA_ADD) {

					// Test if there is a cube(s) to add
					Cube cubeToAdd = getNextCube();
					if (cubeToAdd == null)
						return;

					previousPreview = new Coord(target.cube).face(target.face);

					// TODO [Fix] Add unit
					// Add unit
					if (cubeToAdd.unit != null) {
						// Calcul coords of the new cube(s)
						cubeToAdd.unit.coord = previousPreview.clone();

						map.addUnit(cubeToAdd.unit);

						CubeClient unit = map.getUnitCube(cubeToAdd.unit.getId());

						unit.setPreview(true);
						unit.setTargetable(false);
						unit.setHighlight(true);
					}
					// Add cube
					else {
						// Calcul coords of the new cube(s)
						cubeToAdd.setCoords(previousPreview);

						// Test if there is place for the cube(s) at the coords
						if (!map.add(cubeToAdd))
							return;

						// Mark cube(s) as "preview display"
						map.setPreview(previousPreview, true);
						map.setTargetable(previousPreview, false);
						map.setHighlight(previousPreview, true);
					}
				}

				else if (userAction == UserAction.CREA_DESTROY) {
					map.setHighlight(target.cube, true);
				}

				else if (userAction == UserAction.MOUSE) {
					// Hilight the targeted cube
					map.setHighlight(target.cube, true);

					if (!target.isValid())
						return;

					Cube cube = target.cube;

					if (cube == null || selectedUnit == null || !selectedUnit.getGamer().equals(gamer))
						return;

					// Harvestable
					if (ItemTable.isResource(cube.getItemID()))
						unitAction = Action.UNIT_HARVEST;

					// Building
					else if (cube.build != null) {
						if (cube.build.getGamer().equals(gamer)) {
							if (!cube.build.isBuild())
								unitAction = Action.UNIT_BUILD;

							else if (selectedUnit.hasResource() && cube.build.canStock(selectedUnit.getResource()))
								unitAction = Action.UNIT_STORE;

						} else // Opponent
							unitAction = Action.UNIT_ATTACK;
					}

					// Unit
					else if (cube.unit != null)
						if (cube.unit.getGamer().equals(gamer)) // Own
							;
						else // Opponent
							unitAction = Action.UNIT_ATTACK;

					else
						unitAction = Action.UNIT_GOTO;
				}
	}

	/** Removes previous selection display */
	@Override
	public void looseTarget() {
		// Removes highlight
		map.setHighlight(target.cube, false);

		// Removes preview cubes
		if (map.gridContains(previousPreview) && map.gridGet(previousPreview).isPreview())
			map.remove(previousPreview);

		unitAction = null;
	}

	@Override
	public boolean isNeededQuadriPrecision() {
		return false;
	}

	@Override
	public void oneSecondTick() {
		ticksKeyBoard = keyboard.ticks;
		keyboard.ticks = 0;

		// TODO get ticks from server
		// session.ticksPhys = session.clock.ticks;
		// session.clock.ticks = 0;

		panel.updateMap();
	}

	@Override
	public void repaintEnvironment() {
		panel.panEnv.repaintEnv();
	}

	// =========================================================================================================================
	// Displayable

	@Override
	public JPanel getContentPane() {
		return panel;
	}

	@Override
	public void updateSize(int x, int y) {
		panel.setSize(x, y);
	}

	@Override
	public KeyBoard getKeyBoard() {
		return keyboard;
	}
}
