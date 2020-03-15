package game;

import java.awt.Cursor;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.util.LinkedList;

import javax.swing.JPanel;

import data.dynamic.TickClock;
import data.id.ItemTable;
import data.id.ItemTableClient;
import data.map.Coord;
import data.map.Cube;
import data.map.Map;
import data.map.buildings.Building;
import data.map.resources.Resource;
import data.map.resources.ResourceType;
import environment.Environment3D;
import environment.extendsData.CubeClient;
import environment.extendsData.MapClient;
import environment.extendsData.UnitClient;
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

	// =============== Cursor ===============
	private Cursor cursorGoto, cursorBuild, cursorAttack;
	private Cursor cursorDrop, cursorDropWood, cursorDropStone, cursorDropWater;
	private Cursor cursorAxe, cursorPickaxe, cursorBucket;

	private boolean cursorVisible = true;

	// =============== Server ===============
	private ServerListener client;
	/** Local server if not online play */
	private Server server;
	public Player player = new Player("Felix");

	// =============== Data ===============
	private UserAction userAction;
	public CameraMode cameraMode = CameraMode.CLASSIC;
	public GameMode gameMode = GameMode.CLASSIC;

	// =============== Action ===============
	public UnitClient selectedUnit;
	public Building selectedBuilding;
	public Action unitAction;

	// =============== ? ===============
	public transient TexturePack texturePack;

	public KeyboardGame keyboard;

	private TickClock clock;

	/** State of the window [GAME, PAUSE, DIALOG, ...] */
	private StateHUD stateHUD = StateHUD.GAME;

	// =============== Error ===============
	public String errorMsg = "UNKNOWN ERROR";

	// =============== Dialog ===============
	public MessageManager messages;

	// =========================================================================================================================

	public Game(Fen fen, Server server) throws IOException {
		this(fen, server.getDescription());
		this.server = server;
	}

	public Game(Fen fen, ServerDescription description) throws IOException {
		this.fen = fen;
		client = new ServerListener(this, description);

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

	// =========================================================================================================================

	public void start() {
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
		this.gamer = map.getGamer(1);

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

		updateTarget();
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

				else if (unitAction == Action.UNIT_STORE) {
					Resource res = selectedUnit.getResource();
					if (res == null || res.getType() == null)
						return ItemTableClient.defaultCursor;

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
				}

				else if (unitAction == Action.UNIT_ATTACK)
					return cursorAttack;

				else if (unitAction == Action.UNIT_GOTO)
					return cursorGoto;

		return ItemTableClient.defaultCursor;
	}

	public void generateCursor() {
		cursorGoto = Utils.createCursor(texturePack.getFolder() + "cursor/game/cursorGoto");
		cursorBuild = Utils.createCursor(texturePack.getFolder() + "cursor/game/cursorBuild");
		cursorAttack = Utils.createCursor(texturePack.getFolder() + "cursor/game/cursorAttack");

		cursorDrop = Utils.createCursor(texturePack.getFolder() + "cursor/game/cursorDrop");
		cursorDropWood = Utils.createCursor(texturePack.getFolder() + "cursor/game/cursorDropWood");
		cursorDropStone = Utils.createCursor(texturePack.getFolder() + "cursor/game/cursorDropStone");
		cursorDropWater = Utils.createCursor(texturePack.getFolder() + "cursor/game/cursorDropWater");

		cursorAxe = Utils.createCursor(texturePack.getFolder() + "cursor/game/cursorAxe");
		cursorPickaxe = Utils.createCursor(texturePack.getFolder() + "cursor/game/cursorPickaxe");
		cursorBucket = Utils.createCursor(texturePack.getFolder() + "cursor/game/cursorBucket");
	}

	public void setCursorVisible(boolean visible) {
		cursorVisible = visible;
		fen.updateCursor();
	}

	// =========================================================================================================================

	public void pause() {
		setStateHUD(StateHUD.PAUSE);
		keyboard.setPaused(true);

		setCursorVisible(true);
		setTargetNull();

		panel.panEnv.pause.setVisible(true);
	}

	public void resume() {
		setStateHUD(StateHUD.GAME);
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

	// =========================================================================================================================
	// Selected

	public void select(CubeClient cube) {
		if (selectedUnit != null)
			selectedUnit.hidePath(map);

		selectedUnit = null;
		selectedBuilding = null;
		panel.displayInfosOf(cube);

		if (cube != null)
			if (cube.unit != null)
				selectedUnit = (UnitClient) cube.unit;
			else if (cube.build != null)
				selectedBuilding = cube.build;

		// Display path
		if (selectedUnit != null)
			selectedUnit.showPath(map);
	}

	public void clearSelected() {
		select(null);
	}

	// =========================================================================================================================
	// Client

	@Override
	public void exception(Exception e) {
		if (e instanceof SocketException || e instanceof EOFException) {
			if (client.isRunning())
				connectionLost();
		} else
			e.printStackTrace();
	}

	public void connectionLost() {
		setStateHUD(StateHUD.ERROR);
		errorMsg = ItemTableClient.getText("game.error.connection_lost");
		stop();

		panel.error();
		fen.updateCursor();
	}

	@Override
	public void receive(Object obj) {
		if (obj instanceof Map)
			setMap(new MapClient((Map) obj));
		else if (obj instanceof Message)
			messages.receive((Message) obj);
		else
			super.receive(obj);
	}

	@Override
	public void send(Object obj) {
		client.send(obj);
	}

	public void sendUnitAction() {
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
			LinkedList<Coord> path = selectedUnit.generatePath(map, cubeAir);
			send(SendAction.goTo(selectedUnit, path));
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
	// Getters

	public StateHUD getStateHUD() {
		return stateHUD;
	}

	public UserAction getUserAction() {
		return userAction;
	}

	public void setStateHUD(StateHUD stateHUD) {
		panel.panEnv.help.setVisible(map != null && stateHUD != StateHUD.DIALOG && cameraMode == CameraMode.CLASSIC);
		this.stateHUD = stateHUD;
	}

	// =========================================================================================================================
	// Environment3D

	@Override
	public void updateTarget() {
		super.updateTarget();
		fen.updateCursor();
	}

	@Override
	public void gainTarget() {
		if (cameraMode == CameraMode.CLASSIC)
			if (target.cube != null)
				if (userAction == UserAction.CREA_ADD) {
					addPreview();
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
							unitAction = Action.UNIT_FRIEND_UNIT;
						else // Opponent
							unitAction = Action.UNIT_ATTACK;

					else
						unitAction = Action.UNIT_GOTO;
				}
	}

	/** Removes previous selection display */
	@Override
	public void loseTarget() {
		removePreview();

		// Removes light
		map.setHighlight(target.cube, false);

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

		send(SendAction.ticksPhys(0));

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
