package client.session;

import java.awt.AWTException;
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
import data.map.Cube;

public class Session implements Serializable {
	private static final long serialVersionUID = 8569378400890835470L;

	public ModelMap map;

	public transient TexturePack texturePack;

	public GameMode gamemode = GameMode.CREATIF;

	// ================================

	private Engine engine;

	public Camera camera;
	public Keyboard keyboard;
	public Fen fen;
	// public TickClock clock;

	// ============= Target ===================

	public Cube cubeTarget;
	public Face faceTarget;

	// ============== F3 (Dev infos) ==================

	public Face playerOrientation = Face.NORTH;
	// Various chronometric marks
	public long timeBefore, timeInit, timeMat, timeDraw, timePixel;
	// Number of cubes and chunks displayed
	public int nbChunks, nbCubes;
	// Number of frames displayed the last second
	public volatile int fps;
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

	// true : keep replacing the mouse cursor on the center of the window
	// (allow 1st person movement)
	public boolean captureMouse = true;

	// =========================================================================================================================

	public Session(ModelMap m, boolean with3DEngine) throws AWTException {
		map = m;

		// clock = new TickClock();
		// Thread clockThread = new Thread(clock);
		// clockThread.start();

		keyboard = new Keyboard(this);

		camera = new Camera(new Point3D(-2, 5, -2));

		if (with3DEngine) {
			engine = new Engine();
			engine.camera = camera;

			ModelCube.engine = engine;

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
		switch (gameMode) {
		case CLASSIC:
			break;
		case CREATIF:
			break;
		case SPECTATOR:
			break;
		}
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
		if (cubeTarget != null)
			cubeTarget.isTarget = false;

		cubeTarget = Engine.cubeTarget;
		faceTarget = Engine.faceTarget;

		if (cubeTarget != null)
			cubeTarget.isTarget = true;
	}

	// =========================================================================================================================

	public void exec(String line) {
		// TODO [Improve] Detect the player executing the command
		commands.exec("Félix", line);
	}

}
