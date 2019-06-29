package client.keys;

import java.awt.AWTException;
import java.awt.Robot;

import client.session.Action;
import client.session.GameMode;
import client.session.Session;
import client.window.graphicEngine.models.ModelCube;
import client.window.panels.StateHUD;
import data.enumeration.Face;
import data.map.Cube;
import utils.FlixBlocksUtils;
import utils.Tuple;

public class Keyboard {

	Session session;

	// ================== Mouse ===========================
	// Keep the mouse cursor in the center of the component
	private Robot robot = new Robot();
	// true : freeze the camera rotation until the cursor automatically reach the
	// middle of the screen
	private boolean mouseFreeze = false;

	// Speed of the player rotation
	double mouseSpeed = .1;

	// State of the right/left mouse buttons
	public boolean pressR = false, pressL = false;

	// ================== Keyboard ===========================

	public boolean forwardKeyEnabled = false, backwardKeyEnabled = false, rightKeyEnabled = false,
			leftKeyEnabled = false, jumpKeyEnabled = false, sneakKeyEnabled = false, sprintKeyEnabled = false;

	// =========================== Infos Dev ===========================

	// Ticks count (camera movements/sec)
	public int ticks = 0;

	// =========================================================================================================================

	public Keyboard(Session session) throws AWTException {
		this.session = session;
	}

	// =========================================================================================================================

	public void start() {
		Thread cameraMovements = new Thread(new ThreadCameraMovements());
		cameraMovements.start();
	}

	// =========================================================================================================================

	public void rightClick() {
		if (session.stateGUI != StateHUD.GAME)
			return;

		Cube cube = session.cubeTarget;
		Face face = session.faceTarget;

		if (session.gamemode == GameMode.CREATIVE) {
			// Add a cube to the map
			Cube cubeToAdd = session.getNextCube();
			if (cube != null && face != null && cubeToAdd != null) {
				cubeToAdd.setCoords(new Tuple(cube).face(face));

				session.map.add(cubeToAdd);
			}

		} else if (session.gamemode == GameMode.CLASSIC) {
			pressR = true;

			if (cube != null && face != null) {
				// Add a cube to the map
				if (session.action == Action.CUBES) {
					ModelCube model = session.map.gridGet(new Tuple(cube).face(face));
					if (model != null && model.isPreview()) {
						// Check if multibloc can be added at this position
						if (model.multibloc != null && !model.multibloc.valid)
							return;

						session.map.setHighlight(model, false);
						session.map.setPreview(model, false);
						session.map.setTargetable(model, true);
					}
				}
			}

			// If the cube is right-clickable do the action
			// if (cube != null && !session.keyboard.sneakKeyEnabled && cube.hasAction())
			// cube.doAction(session);

			return;
		}
	}

	public void leftClick() {
		if (session.stateGUI != StateHUD.GAME)
			return;

		if (session.gamemode == GameMode.CREATIVE) {
			if (session.cubeTarget != null)
				session.map.remove((ModelCube) session.cubeTarget);

		} else if (session.gamemode == GameMode.CLASSIC) {
			if (session.action == Action.DESTROY) {
				if (session.cubeTarget != null)
					if(session.cubeTarget.unit != null)
						session.map.removeUnit(session.cubeTarget.unit);
					else
						session.map.remove((ModelCube) session.cubeTarget);

			} else if (session.action == Action.DESTROY) {

			} else if (session.action == Action.MOUSE) {
				session.fen.gui.selectInfos.setCube(session.cubeTarget);
			}
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

		if (session.stateGUI != StateHUD.PAUSE && session.stateGUI != StateHUD.DIALOG && !mouseFreeze) {
			session.camera.setVx(session.camera.getVx() - ((session.fen.getWidth() / 2 - mouseLocationX) * mouseSpeed));
			session.camera
					.setVy(session.camera.getVy() + ((session.fen.getHeight() / 2 - mouseLocationY) * mouseSpeed));

			robot.mouseMove(frameLocationX + session.fen.getWidth() / 2, frameLocationY + session.fen.getHeight() / 2);

			if (session.camera.getVx() >= 360)
				session.camera.setVx(session.camera.getVx() - 360);
			else if (session.camera.getVx() < 0)
				session.camera.setVx(session.camera.getVx() + 360);
			if (session.camera.getVy() > 90)
				session.camera.setVy(90);
			else if (session.camera.getVy() < -90)
				session.camera.setVy(-90);

			if (session.camera.getVx() >= 45 && session.camera.getVx() < 135)
				session.playerOrientation = Face.EAST;
			else if (session.camera.getVx() >= 315 || session.camera.getVx() < 45)
				session.playerOrientation = Face.NORTH;
			else if (session.camera.getVx() < 225)
				session.playerOrientation = Face.SOUTH;
			else
				session.playerOrientation = Face.WEST;
		}
	}

	public void mouseToCenter() {
		mouseFreeze = true;
		robot.mouseMove(session.fen.getWidth() / 2, session.fen.getHeight() / 2);
	}

	// =========================================================================================================================

	public void dialog() {
		session.stateGUI = StateHUD.DIALOG;
	}

	// =========================================================================================================================

	public void pause() {
		session.stateGUI = StateHUD.PAUSE;

		session.fen.cursorVisible(true);

		session.fen.pause.setVisible(true);
	}

	public void resume() {
		session.stateGUI = StateHUD.GAME;

		if (session.gamemode == GameMode.CREATIVE) {
			session.fen.cursorVisible(false);
			mouseToCenter();
		}

		session.fen.pause.setVisible(false);
	}

	// =========================================================================================================================

	public void cameraMovement() {
		double x = 0, z = 0;

		if (session.keyboard.forwardKeyEnabled) {
			x = Math.cos(session.camera.getVx() * FlixBlocksUtils.toRadian);
			z = Math.sin(session.camera.getVx() * FlixBlocksUtils.toRadian);
		}

		if (session.keyboard.backwardKeyEnabled) {
			x += -Math.cos(session.camera.getVx() * FlixBlocksUtils.toRadian);
			z += -Math.sin(session.camera.getVx() * FlixBlocksUtils.toRadian);
		}

		if (session.keyboard.rightKeyEnabled) {
			x += Math.cos((session.camera.getVx() + 90) * FlixBlocksUtils.toRadian);
			z += Math.sin((session.camera.getVx() + 90) * FlixBlocksUtils.toRadian);
		}

		if (session.keyboard.leftKeyEnabled) {
			x += -Math.cos((session.camera.getVx() + 90) * FlixBlocksUtils.toRadian);
			z += -Math.sin((session.camera.getVx() + 90) * FlixBlocksUtils.toRadian);
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
			session.camera.moveY(.4);
		if (session.keyboard.sneakKeyEnabled)
			session.camera.moveY(-.4);

		session.camera.move(x, z);
	}

	// =========================================================================================================================

	class ThreadCameraMovements implements Runnable {
		public void run() {
			while (true) {
				ticks++;

				if (session.stateGUI == StateHUD.GAME)
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
