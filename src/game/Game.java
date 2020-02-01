package game;

import java.awt.Cursor;
import java.io.IOException;

import javax.swing.JPanel;

import data.dynamic.TickClock;
import data.id.ItemTable;
import data.map.Coord;
import data.map.Cube;
import data.map.Map;
import data.map.resources.ResourceType;
import data.map.units.Unit;
import environment.Environment3D;
import environment.EnvironmentListner;
import environment.Target;
import environment.extendsData.MapClient;
import environment.textures.TexturePack;
import game.panels.PanGUI;
import game.panels.PanGame;
import game.panels.PanPause;
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

public class Game extends Environment3D implements Displayable, EnvironmentListner {
	public Fen fen;

	// ============= Cursor ===================
	private Cursor cursorGoto, cursorBuild, cursorAttack;
	private Cursor cursorDrop, cursorDropWood, cursorDropStone, cursorDropWater;
	private Cursor cursorAxe, cursorPickaxe, cursorBucket;

	private boolean cursorVisible = true;

	// ============= Server ===================
	Client client;

	// ============= Data ===================
	public Player player = new Player("Felix");

	Target target;

	// ============= ===================
	private UserAction action;
	public Action unitAction;

	// ============= ===================
	public transient TexturePack texturePack;

	public KeyboardGame keyboard;

	private TickClock clock;

	// ============= Pan ===================
	private PanGame game;
	private PanPause pause;
	public PanGUI gui;

	// ============= Data ===================
	public GameMode gamemode = GameMode.CLASSIC;

	// =============== F3 (Dev infos) ===============
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

	// =============== Dialog =================
	public MessageManager messages;

	// =========================================================================================================================

	public Game(Fen fen) {
		this.fen = fen;
		texturePack = new TexturePack("classic");
		ItemTable.setTexturePack(texturePack);

		ResourceType.setTextureFolder(texturePack.getFolder());

		generateCursor();

		// ======================================

		client = new Client(this);

		// ======================================

		camera = new Camera(new Point3D(15, 35, 0), 90, -65);

		keyboard = new KeyboardGame(this);

		clock = new TickClock("Game (Client) Clock");

		// ======================================

		game = new PanGame(this);
		pause = new PanPause(this);
		gui = new PanGUI(this);

		game.add(pause, -1);
		game.add(gui, -1);

		// ======================================

		setAction(UserAction.MOUSE);
		setGameMode(GameMode.CLASSIC);

		send(player);
	}

	// =========================================================================================================================

	public void start() {
		messages = new MessageManager(this, gui);

		super.start();
		gui.refreshGUI();
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

		gui.refreshGUI();

		switch (gameMode) {
		case CLASSIC:
			// Realign the camera with the grid
			camera.setVx(90);
			camera.setVy(-65);
			// Replace the camera at the correct altitude
			camera.vue.y = 35;

			game.setStartXPanel(400);

			// Deselect
			gui.select(null);

			setCursorVisible(true);
			break;
		case CREATIVE:
			keyboard.mouseToCenter();
			game.setStartXPanel(0);

			setCursorVisible(false);
			break;
		case SPECTATOR:
			break;
		}
	}

	public void setAction(UserAction action) {
		this.action = action;

		fen.updateCursor();
		gui.refreshGUI();
	}

	// =========================================================================================================================
	// Cursor

	@Override
	public Cursor getCursor() {
		Cursor cursor = Cursor.getDefaultCursor();
		Cube cube = target.cube;

		unitAction = null;

		if (!cursorVisible)
			cursor = Fen.cursorInvisible;
		else if (getAction() == UserAction.MOUSE)

			if (cube != null && gui.unit != null && gui.unit.getPlayer().equals(player))

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
						} else if (gui.unit.hasResource() && cube.build.canStock(gui.unit.getResource())) {// Stock
							cursor = cursorDrop;
							unitAction = Action.UNIT_STORE;

							switch (gui.unit.getResource().getType()) {
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

		setCursorVisible(true);

		pause.setVisible(true);
	}

	public void resume() {
		if (gamemode == GameMode.CREATIVE) {
			setCursorVisible(false);
			keyboard.mouseToCenter();
		}

		pause.setVisible(false);
	}

	// =========================================================================================================================

	public void setTexturePack(TexturePack texturePack) {
		this.texturePack = texturePack;

		if (fen != null)
			gui.updateTexturePack();
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

	public void unitDoAction() {
		if (unitAction == null) {
			System.out.println("Action NULL");
			return;
		}

		Unit unit = gui.unit;
		// Pointed cube
		Coord cube = target.cube.coords();
		// Cube adjacent to the pointed face (in the air)
		Coord cubeAir = target.cube.coords().face(target.face);

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

	// =========================================================================================================================
	// EnvironmentListner

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

	// =========================================================================================================================
	// Displayable

	@Override
	public JPanel getContentPane() {
		return game;
	}

	@Override
	public void updateSize(int x, int y) {
		game.setSize(x, y);
		gui.setSize(x, y);
		pause.setSize(x, y);
	}

	@Override
	public KeyBoard getKeyBoard() {
		return keyboard;
	}
}
