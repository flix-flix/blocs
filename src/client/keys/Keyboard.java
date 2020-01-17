package client.keys;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.MouseEvent;

import client.session.Session;
import client.session.UserAction;
import client.window.graphicEngine.calcul.Camera;
import client.window.graphicEngine.extended.ModelCube;
import client.window.graphicEngine.extended.ModelMap;
import client.window.panels.StateHUD;
import data.map.Coord;
import data.map.Cube;
import data.map.enumerations.Orientation;
import server.game.GameMode;
import utils.FlixBlocksUtils;

public class Keyboard {

	Session session;

	// ================== Mouse ===========================
	/** Keep the mouse cursor in the center of the component */
	private Robot robot;
	/**
	 * true : freeze the camera rotation until the cursor automatically reach the
	 * middle of the screen
	 */
	private boolean mouseFreeze = false;

	/** Speed of the player rotation */
	double mouseSpeed = .1;

	/** State of the right/left mouse buttons */
	public boolean pressR = false, pressL = false;

	// ================== Keyboard ===========================

	public boolean forwardKeyEnabled = false, backwardKeyEnabled = false, rightKeyEnabled = false,
			leftKeyEnabled = false, jumpKeyEnabled = false, sneakKeyEnabled = false, sprintKeyEnabled = false;

	// =========================== Infos Dev ===========================

	/** Ticks count (camera movements/sec) */
	public int ticks = 0;

	// =========================================================================================================================

	public Keyboard(Session session) {
		this.session = session;

		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	// =========================================================================================================================

	public void start() {
		Thread cameraMovements = new Thread(new ThreadCameraMovements());
		cameraMovements.setName("Camera Movements");
		cameraMovements.start();
	}

	// =========================================================================================================================

	public void rightClick(MouseEvent e) {
		if (session.stateHUD == StateHUD.EDITOR)
			session.fen.editor.initDrag(e);

		ModelMap map;
		if (session.stateHUD == StateHUD.GAME)
			map = session.map;
		else if (session.stateHUD == StateHUD.EDITOR)
			map = session.fen.editor.map;
		else
			return;

		if (session.gamemode == GameMode.CREATIVE) {// Add a cube to the map
			Cube cubeToAdd = session.getNextCube();
			if (session.cubeTarget != null && session.faceTarget != null && cubeToAdd != null) {
				cubeToAdd.setCoords(new Coord(session.cubeTarget).face(session.faceTarget));

				map.add(cubeToAdd);
			}

		} else if (session.gamemode == GameMode.CLASSIC) {
			pressR = true;

			if (session.cubeTarget != null && session.faceTarget != null) {

				if (session.getAction() == UserAction.CREA_ADD) {// Add a cube to the map
					ModelCube model = map.gridGet(new Coord(session.cubeTarget).face(session.faceTarget));
					if (model != null && model.isPreview()) {
						// Check if multibloc can be added at this position
						if (model.multibloc != null && !model.multibloc.valid)
							return;

						map.setHighlight(model, false);
						map.setPreview(model, false);
						map.setTargetable(model, true);
					}
				} else if (session.fen.gui.unit != null)
					session.unitDoAction();
			}
			return;
		}
	}

	public void leftClick(MouseEvent e) {
		ModelMap map;
		if (session.stateHUD == StateHUD.GAME)
			map = session.map;
		else if (session.stateHUD == StateHUD.EDITOR)
			map = session.fen.editor.map;
		else
			return;

		if (session.gamemode == GameMode.CREATIVE) {
			if (session.cubeTarget != null)
				if (session.cubeTarget.unit != null)
					map.removeUnit(session.cubeTarget.unit);
				else
					map.remove((ModelCube) session.cubeTarget);

		} else if (session.gamemode == GameMode.CLASSIC) {

			if (session.fen.editor.isListeningClick())
				session.fen.editor.click();

			else if (session.getAction() == UserAction.CREA_DESTROY) {
				if (session.cubeTarget != null)
					if (session.cubeTarget.unit != null)
						map.removeUnit(session.cubeTarget.unit);
					else
						map.remove((ModelCube) session.cubeTarget);
			} else
				session.fen.gui.select(session.cubeTarget);
		}
	}

	public void rightClickEnd() {
		pressR = false;
	}

	public void leftClickEnd() {
		pressL = false;
	}

	// =========================================================================================================================

	/**
	 * Move the Player orientation in function of the mouse cursor position then
	 * replace it in the middle of the screen
	 * 
	 * @param frameLocationX
	 *            X location of the frame in the screen
	 * @param frameLocationY
	 *            Y location of the frame in the screen
	 * @param mouseLocationX
	 *            X location of the mouse in the frame
	 * @param mouseLocationY
	 *            Y location of the mouse in the frame
	 */
	public void mouse(int frameLocationX, int frameLocationY, int mouseLocationX, int mouseLocationY) {
		if (mouseFreeze) {
			robot.mouseMove(frameLocationX + session.fen.getWidth() / 2, frameLocationY + session.fen.getHeight() / 2);

			mouseFreeze = !(session.fen.getWidth() / 2 == mouseLocationX
					&& session.fen.getHeight() / 2 == mouseLocationY);
		}

		Camera camera = session.stateHUD == StateHUD.EDITOR ? session.fen.editor.camera : session.camera;

		if (session.stateHUD != StateHUD.PAUSE && session.stateHUD != StateHUD.DIALOG && !mouseFreeze) {
			camera.setVx(camera.getVx() - ((session.fen.getWidth() / 2 - mouseLocationX) * mouseSpeed));
			camera.setVy(camera.getVy() + ((session.fen.getHeight() / 2 - mouseLocationY) * mouseSpeed));

			robot.mouseMove(frameLocationX + session.fen.getWidth() / 2, frameLocationY + session.fen.getHeight() / 2);

			if (camera.getVx() >= 360)
				camera.setVx(camera.getVx() - 360);
			else if (camera.getVx() < 0)
				camera.setVx(camera.getVx() + 360);
			if (camera.getVy() > 90)
				camera.setVy(90);
			else if (camera.getVy() < -90)
				camera.setVy(-90);

			if (camera.getVx() >= 45 && camera.getVx() < 135)
				session.playerOrientation = Orientation.EAST;
			else if (camera.getVx() >= 315 || camera.getVx() < 45)
				session.playerOrientation = Orientation.NORTH;
			else if (camera.getVx() < 225)
				session.playerOrientation = Orientation.SOUTH;
			else
				session.playerOrientation = Orientation.WEST;
		}
	}

	public void mouseToCenter() {
		mouseFreeze = true;
		robot.mouseMove(session.fen.getWidth() / 2, session.fen.getHeight() / 2);
	}

	// =========================================================================================================================

	public void wheelRotation(int wheelRotation) {
		session.camera.moveY(wheelRotation * 10);
	}

	// =========================================================================================================================

	public void dialog() {
		session.stateHUD = StateHUD.DIALOG;
	}

	// =========================================================================================================================

	public void pause() {
		session.stateHUD = StateHUD.PAUSE;

		session.fen.setCursorVisible(true);

		session.fen.pause.setVisible(true);
	}

	public void resume() {
		session.stateHUD = StateHUD.GAME;

		if (session.gamemode == GameMode.CREATIVE) {
			session.fen.setCursorVisible(false);
			mouseToCenter();
		}

		session.fen.pause.setVisible(false);
	}

	// =========================================================================================================================

	public void cameraMovement() {
		double x = 0, z = 0;
		Camera camera = session.stateHUD == StateHUD.EDITOR ? session.fen.editor.camera : session.camera;

		if (session.keyboard.forwardKeyEnabled) {
			x = Math.cos(camera.getVx() * FlixBlocksUtils.toRadian);
			z = Math.sin(camera.getVx() * FlixBlocksUtils.toRadian);
		}

		if (session.keyboard.backwardKeyEnabled) {
			x += -Math.cos(camera.getVx() * FlixBlocksUtils.toRadian);
			z += -Math.sin(camera.getVx() * FlixBlocksUtils.toRadian);
		}

		if (session.keyboard.rightKeyEnabled) {
			x += Math.cos((camera.getVx() + 90) * FlixBlocksUtils.toRadian);
			z += Math.sin((camera.getVx() + 90) * FlixBlocksUtils.toRadian);
		}

		if (session.keyboard.leftKeyEnabled) {
			x += -Math.cos((camera.getVx() + 90) * FlixBlocksUtils.toRadian);
			z += -Math.sin((camera.getVx() + 90) * FlixBlocksUtils.toRadian);
		}

		// Slow down the camera if moving in 2 directions
		if ((session.keyboard.rightKeyEnabled || session.keyboard.leftKeyEnabled)
				&& (session.keyboard.forwardKeyEnabled || session.keyboard.backwardKeyEnabled)) {
			x *= .5;
			z *= .5;
		}

		if (!session.keyboard.sprintKeyEnabled) {
			// Slow down the camera
			x *= .3;
			z *= .3;
		}

		if (session.keyboard.jumpKeyEnabled)
			camera.moveY(.4);
		if (session.keyboard.sneakKeyEnabled)
			camera.moveY(-.4);

		camera.move(x, z);
	}

	// =========================================================================================================================

	class ThreadCameraMovements implements Runnable {
		public void run() {
			while (true) {
				ticks++;

				if (session.stateHUD == StateHUD.GAME || session.stateHUD == StateHUD.EDITOR)
					cameraMovement();

				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
