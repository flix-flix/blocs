package game;

import java.awt.Cursor;
import java.io.IOException;

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

	// =============== Data ===============
	public Player player = new Player("Felix");
	private UserAction action;
	public GameMode gamemode = GameMode.CLASSIC;

	private Target target;

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
	/** Next cube to add (its coords aren't valid) */
	private Cube nextCube;
	/** Coord of the preview cube */
	public Coord previousPreview;

	// =============== Dialog ===============
	public MessageManager messages;

	// =========================================================================================================================

	public Game(Fen fen) {
		this.fen = fen;
		texturePack = new TexturePack("classic");
		ItemTableClient.setTexturePack(texturePack);

		ResourceType.setTextureFolder(texturePack.getFolder());

		generateCursor();

		// ======================================

		client = new Client(this);

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

	public void start() {
		messages = new MessageManager(this);

		super.start();
		panel.refreshGUI();
		clock.start();
		keyboard.start();
	}

	// =========================================================================================================================

	public void setMap(MapClient map) {
		this.map = map;

		clock.add(map);

		start();
	}

	// =========================================================================================================================

	public void setGameMode(GameMode gameMode) {
		this.gamemode = gameMode;

		panel.refreshGUI();

		switch (gameMode) {
		case CLASSIC:
			// Realign the camera with the grid
			camera.setVx(90);
			camera.setVy(-65);
			// Replace the camera at the correct altitude
			camera.vue.y = 35;

			panel.setStartXPanel(400);

			// Deselect
			clearSelected();

			keyboard.setTargetOnMouse();

			setCursorVisible(true);
			break;
		case CREATIVE:
			keyboard.mouseToCenter();
			panel.setStartXPanel(0);

			setCursorVisible(false);
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

		Cursor cursor = Cursor.getDefaultCursor();

		if (target == null)
			return cursor;

		Cube cube = target.cube;

		unitAction = null;

		if (getAction() == UserAction.MOUSE)

			if (cube != null && selectedUnit != null && selectedUnit.getPlayer().equals(player))

				// Harvestable
				if (ItemTable.isResource(cube.getItemID()))
					switch (ItemTable.getResourceType(cube.getItemID())) {
					case WOOD:
						cursor = cursorAxe;
						unitAction = Action.UNIT_HARVEST;
						break;
					case STONE:
						cursor = cursorPickaxe;
						unitAction = Action.UNIT_HARVEST;
						break;
					case WATER:
						cursor = cursorBucket;
						unitAction = Action.UNIT_HARVEST;
						break;
					}
				// Building
				else if (cube.build != null) {
					if (cube.build.getPlayer().equals(player)) {
						if (!cube.build.isBuild()) {
							cursor = cursorBuild;
							unitAction = Action.UNIT_BUILD;
						} else if (selectedUnit.hasResource() && cube.build.canStock(selectedUnit.getResource())) {// Stock
							cursor = cursorDrop;
							unitAction = Action.UNIT_STORE;

							switch (selectedUnit.getResource().getType()) {
							case WOOD:
								cursor = cursorDropWood;
								break;
							case STONE:
								cursor = cursorDropStone;
								break;
							case WATER:
								cursor = cursorDropWater;
								break;
							}
						}
					} else {// Opponent
						cursor = cursorAttack;
						unitAction = Action.UNIT_ATTACK;
					}
				}
				// Unit
				else if (cube.unit != null) {
					if (cube.unit.getPlayer().equals(player)) {// Own
					} else {// Opponent
						cursor = cursorAttack;
						unitAction = Action.UNIT_ATTACK;
					}
				} else {
					cursor = cursorGoto;
					unitAction = Action.UNIT_GOTO;
				}
		return cursor;
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

		if (gamemode == GameMode.CREATIVE) {
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
	public void gainTarget(Target target) {
		this.target = target;

		fen.updateCursor();

		if (gamemode == GameMode.CLASSIC)
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
				} else if (action == UserAction.CREA_DESTROY) {
					map.setHighlight(target.cube, true);
				} else if (action == UserAction.MOUSE) {
					map.setHighlight(target.cube, true);
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
