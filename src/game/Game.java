package game;

import java.awt.Cursor;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JPanel;

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
import environment.EnvironmentListener;
import environment.Target;
import environment.extendsData.MapClient;
import environment.textures.TexturePack;
import game.panels.PanGame;
import graphicEngine.calcul.Camera;
import graphicEngine.calcul.Point3D;
import server.Server;
import server.game.GameMode;
import server.game.Player;
import server.game.messages.Message;
import server.send.Action;
import server.send.SendAction;
import utils.FlixBlocksUtils;
import window.Displayable;
import window.Fen;
import window.KeyBoard;

public class Game extends Environment3D implements Displayable, EnvironmentListener {
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

	// =============== Data ===============
	 public Player player = new Player("Felix");
//	public Player player = new Player("IA");
	private UserAction action;
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
	public StateHUD stateHUD = StateHUD.GAME;

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

	public Game(Fen fen, InetAddress inetAdr) {
		this.fen = fen;
		client = new Client(this, inetAdr);

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
		super.panel = panel;

		// ======================================

		setAction(UserAction.MOUSE);
		setGameMode(GameMode.CLASSIC);

		send(player);
	}

	// =========================================================================================================================

	public static Game startLocalServer(Fen fen) {
		try {
			Server server = new Server();
			server.start();
			// Game game = new Game(fen, InetAddress.getLocalHost());
			Game game = new Game(fen, InetAddress.getByAddress(new byte[] { 0, 0, 0, 0 }));
			game.server = server;
			return game;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
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
		panel.refreshGUI();
		clock.start();
		keyboard.start();
	}

	@Override
	public void stop() {
		super.stop();

		keyboard.stop();
		clock.stop();

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

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;

		panel.refreshGUI();

		switch (gameMode) {
		case CLASSIC:
			cameraMode = CameraMode.CLASSIC;
			// Realign the camera with the grid
			camera.setVx(90);
			camera.setVy(-65);
			// Replace the camera at the correct altitude
			camera.vue.y = 35;

			// Deselect
			clearSelected();

			keyboard.setTargetOnMouse();

			panel.setGUIVisible(true);
			setCursorVisible(true);
			break;
		case CREATIVE:
			cameraMode = CameraMode.FIRST_PERSON;
			keyboard.mouseToCenter();

			setCursorVisible(false);
			panel.setGUIVisible(false);
			break;
		case SPECTATOR:
			break;
		}

		targetUpdate();
	}

	public void setAction(UserAction action) {
		this.action = action;

		fen.updateCursor();
		panel.refreshGUI();
	}

	// =========================================================================================================================
	// Cursor

	@Override
	public Cursor getCursor() {
		if (!cursorVisible)
			return Fen.cursorInvisible;

		if (stateHUD == StateHUD.ERROR)
			return Cursor.getDefaultCursor();

		if (!target.isValid())
			return Cursor.getDefaultCursor();

		Cube cube = target.cube;

		if (getAction() == UserAction.MOUSE)

			if (cube != null && selectedUnit != null && selectedUnit.getPlayer().equals(player))

				if (unitAction == Action.UNIT_HARVEST)
					switch (ItemTable.getResourceType(cube.getItemID())) {
					case WOOD:
						return cursorAxe;
					case STONE:
						return cursorPickaxe;
					case WATER:
						return cursorBucket;
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

		return Cursor.getDefaultCursor();
	}

	public void generateCursor() {
		cursorGoto = FlixBlocksUtils.createCursor(texturePack.getFolder() + "cursor/cursorGoto");
		cursorBuild = FlixBlocksUtils.createCursor(texturePack.getFolder() + "cursor/cursorBuild");
		cursorAttack = FlixBlocksUtils.createCursor(texturePack.getFolder() + "cursor/cursorAttack");

		cursorDrop = FlixBlocksUtils.createCursor(texturePack.getFolder() + "cursor/cursorDrop");
		cursorDropWood = FlixBlocksUtils.createCursor(texturePack.getFolder() + "cursor/cursorDropWood");
		cursorDropStone = FlixBlocksUtils.createCursor(texturePack.getFolder() + "cursor/cursorDropStone");
		cursorDropWater = FlixBlocksUtils.createCursor(texturePack.getFolder() + "cursor/cursorDropWater");

		cursorAxe = FlixBlocksUtils.createCursor(texturePack.getFolder() + "cursor/cursorAxe");
		cursorPickaxe = FlixBlocksUtils.createCursor(texturePack.getFolder() + "cursor/cursorPickaxe");
		cursorBucket = FlixBlocksUtils.createCursor(texturePack.getFolder() + "cursor/cursorBucket");
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

		panel.pause.setVisible(true);
	}

	public void resume() {
		stateHUD = StateHUD.GAME;
		keyboard.setPaused(false);

		if (cameraMode == CameraMode.FIRST_PERSON) {
			setCursorVisible(false);
			keyboard.mouseToCenter();
		}

		panel.pause.setVisible(false);

		keyboard.setTargetOnMouse();
	}

	// =========================================================================================================================

	public void setTexturePack(TexturePack texturePack) {
		this.texturePack = texturePack;

		panel.updateTexturePack();
	}

	// =========================================================================================================================

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
	// Selected

	public void select(Cube cube) {
		selectedUnit = null;
		selectedBuilding = null;
		panel.refreshSelected(cube);

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
			FlixBlocksUtils.debug("Action NULL");
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
			FlixBlocksUtils.debug("[Client] Missing unitDoAction(): " + unitAction);
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
			FlixBlocksUtils.debug("[Client] missing receiveSend(): " + send.action);
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
			setMap(new MapClient((Map) obj, texturePack));
		else if (obj instanceof Message)
			messages.receive((Message) obj);
		else if (obj instanceof SendAction)
			receiveSend((SendAction) obj);
		else
			System.err.println("Unknown object");
	}

	// =========================================================================================================================
	// Getters

	public Target getTarget() {
		return target;
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
				if (action == UserAction.CREA_ADD) {

					// Test if there is a cube(s) to add
					Cube cubeToAdd = getNextCube();
					if (cubeToAdd == null)
						return;

					// Calcul coords of the new cube(s)
					previousPreview = new Coord(target.cube).face(target.face);
					cubeToAdd.setCoords(previousPreview);

					// Test if there is place for the cube(s) at the coords
					if (!map.add(cubeToAdd))
						return;

					// Mark cube(s) as "preview display"
					map.setPreview(previousPreview, true);
					map.setTargetable(previousPreview, false);
					map.setHighlight(previousPreview, true);
				}

				else if (action == UserAction.CREA_DESTROY) {
					map.setHighlight(target.cube, true);
				}

				else if (action == UserAction.MOUSE) {
					// Hilight the targeted cube
					map.setHighlight(target.cube, true);

					if (!target.isValid())
						return;

					Cube cube = target.cube;

					if (cube == null || selectedUnit == null || !selectedUnit.getPlayer().equals(player))
						return;

					// Harvestable
					if (ItemTable.isResource(cube.getItemID()))
						unitAction = Action.UNIT_HARVEST;

					// Building
					else if (cube.build != null) {
						if (cube.build.getPlayer().equals(player)) {
							if (!cube.build.isBuild())
								unitAction = Action.UNIT_BUILD;

							else if (selectedUnit.hasResource() && cube.build.canStock(selectedUnit.getResource()))
								unitAction = Action.UNIT_STORE;

						} else // Opponent
							unitAction = Action.UNIT_ATTACK;
					}

					// Unit
					else if (cube.unit != null)
						if (cube.unit.getPlayer().equals(player)) // Own
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
	}

	@Override
	public void repaint() {
		panel.repaint();
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
