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
import client.window.graphicEngine.draws.DrawCubeFace;
import client.window.graphicEngine.models.ModelCube;
import client.window.graphicEngine.models.ModelMap;
import client.window.panels.StateHUD;
import data.enumeration.Face;
import data.enumeration.ItemID;
import data.map.Cube;
import utils.Tuple;

public class Session implements Serializable {
	private static final long serialVersionUID = 8569378400890835470L;

	public ModelMap map;

	public transient TexturePack texturePack;

	public GameMode gamemode = GameMode.CLASSIC;

	public Action action = Action.BLOCS;

	// ================================

	private Engine engine;

	public Camera camera;
	public Keyboard keyboard;
	public Fen fen;
	// public TickClock clock;

	// ============= Target ===================

	public ModelCube cubeTarget;
	public Face faceTarget;

	// ID of the next added cube
	public ItemID selectedItemID = ItemID.GRASS;
	// Coord of the preview cube
	public Tuple previousPreview;

	// ============== F3 (Dev infos) ==================

	public Face playerOrientation = Face.NORTH;
	// Various chronometric marks
	public long timeBefore, timeInit, timeMat, timeDraw, timePixel;
	// Number of cubes and chunks displayed
	public int nbChunks, nbCubes;
	// Number of frames displayed the last second
	public volatile int fps, ticksPhys;
	// true : show on-screen the dev infos
	public boolean devlop;

	// ============ Options ============
	// Max frames/seconde allowed
	public int FPSmax = 60;

	// true : currently generating an image
	public boolean processing = false;

	// State of the window [GAME, PAUSE, DIALOG, ...]
	public StateHUD stateGUI = StateHUD.GAME;

	// =============== Dialog =================
	public MessageManager messages;
	public CommandExecutor commands;

	// =========================================================================================================================

	public Session(ModelMap m, boolean with3DEngine) throws AWTException {
		map = m;

		// clock = new TickClock();
		// Thread clockThread = new Thread(clock);
		// clockThread.start();

		keyboard = new Keyboard(this);

		camera = new Camera(new Point3D(15, 25, 0));
		camera.setVx(90);
		camera.setVy(-65);

		if (with3DEngine) {
			engine = new Engine();
			engine.camera = camera;

			engine.model = map;
			map.engine = engine;
		}
	}

	// =========================================================================================================================

	public void start() throws AWTException {
		messages = new MessageManager(this, fen.gui);
		commands = new CommandExecutor(this, messages);

		fen.start();
		keyboard.start();
	}

	public void setTexturePack(TexturePack texturePack) {
		this.texturePack = texturePack;

		Engine.texturePack = texturePack;
		DrawCubeFace.texturePack = texturePack;
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
			camera.vue.y = 25;

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

		fen.gui.hideMenu();

		switch (action) {
		case BLOCS:
			break;
		case DESTROY:
			break;
		case MOUSE:
			break;
		case SELECT:
			break;
		}
	}

	public void setSelectedItemID(ItemID itemID) {
		this.selectedItemID = itemID;

		fen.gui.hideMenu();
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
		timeInit = engine.timeInit - timeBefore;
		timeMat = engine.timeMat - engine.timeInit;
		timeDraw = engine.timeDraw - engine.timeMat;
		timePixel = engine.timePixel - engine.timeDraw;

		nbChunks = engine.nbChunks;
		nbCubes = engine.nbCubes;

		engine.nbChunks = 0;
		engine.nbCubes = 0;
	}

	public void targetUpdate() {
		if (cubeTarget != null) {
			if (previousPreview != null && map.gridContains(previousPreview)
					&& map.gridGet(previousPreview).isHighlight())
				map.gridRemove(previousPreview);
			cubeTarget.setHighlight(false);
		}

		cubeTarget = Engine.cubeTarget;
		faceTarget = Engine.faceTarget;

		if (gamemode == GameMode.CLASSIC)
			if (cubeTarget != null)
				if (action == Action.BLOCS) {
					previousPreview = new Tuple(cubeTarget).face(faceTarget);

					if (!map.gridAdd(new Cube(previousPreview, selectedItemID)))
						return;

					map.gridGet(previousPreview).setPreview(true);
					map.gridGet(previousPreview).setPreviewThrought(true);
					map.gridGet(previousPreview).setHighlight(true);

					map.update(previousPreview.x, previousPreview.y, previousPreview.z);
				} else if (action == Action.DESTROY)
					cubeTarget.setHighlight(true);
	}

	// =========================================================================================================================

	public void exec(String line) {
		// TODO [Improve] Detect the player executing the command
		commands.exec("Félix", line);
	}
}
