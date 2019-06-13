package client.keys;

import java.awt.AWTException;
import java.awt.Robot;

import client.session.GameMode;
import client.session.Session;
import client.window.graphicEngine.models.ModelCube;
import client.window.panels.StateHUD;
import data.enumeration.Face;
import data.enumeration.ItemID;
import data.map.Cube;
import utils.FlixBlocksUtils;

public class Keyboard {

	Session session;

	// ================== Mouse ===========================

	// Position of the mouse cursor in the component
	// (Used to pass clic to GUI (ex: Inventory))
	public int mouseX;
	public int mouseY;

	// Keep the mouse cursor in the center of the component
	Robot robot = new Robot();

	// Position of the mouse cursor on previous tick
	public int memX;
	public int memY;

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

	public void rightClic() {
		if (session.stateGUI != StateHUD.GAME)
			return;

		Cube cube = session.cubeTarget;
		Face face = session.faceTarget;

		if (session.gamemode == GameMode.CREATIVE) {
			// ========== Add a cube to the map ==========
			if (cube != null && face != null)
				session.map.gridAddToFace(cube.x, cube.y, cube.z, ItemID.GRASS, face);

		} else if (session.gamemode == GameMode.CLASSIC) {
			pressR = true;

			// ========== Add a cube to the map ==========
			if (cube != null && face != null)
				session.map.gridAddToFace(cube.x, cube.y, cube.z, ItemID.GRASS, face);

			// If the bloc is right-clickable do the action
			// if (cube != null && !session.keyboard.sneakKeyEnabled && cube.hasAction())
			// cube.doAction(session);

			return;
		}
	}

	public void leftClic() {
		if (session.stateGUI != StateHUD.GAME)
			return;

		if (session.gamemode == GameMode.CREATIVE) {
			if (session.cubeTarget != null)
				if (session.cubeTarget.onGrid)
					session.map.removeGrid(session.cubeTarget);
				else
					session.map._removeCube((ModelCube) session.cubeTarget);

		} else if (session.gamemode == GameMode.CLASSIC) {

		}
	}

	public void rightClicEnd() {
		pressR = false;
	}

	public void leftClicEnd() {
		pressL = false;
	}

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
		mouseX = mouseLocationX;
		mouseY = mouseLocationY;

		if (session.captureMouse) {
			session.camera.setVx(session.camera.getVx() - ((memX - mouseLocationX) * mouseSpeed));
			session.camera.setVy(session.camera.getVy() + ((memY - mouseLocationY) * mouseSpeed));

			memX = session.fen.getWidth() / 2;
			memY = session.fen.getHeight() / 2;

			robot.mouseMove(frameLocationX + memX, frameLocationY + memY);

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

		} else {
			memX = mouseLocationX;
			memY = mouseLocationY;
		}
	}

	// =========================================================================================================================

	public void dialog() {
		session.stateGUI = StateHUD.DIALOG;
	}

	// =========================================================================================================================

	public void pause() {
		session.captureMouse = false;
		session.stateGUI = StateHUD.PAUSE;

		session.fen.cursorVisible(true);

		session.fen.gui.resume.setVisible(true);
		session.fen.gui.options.setVisible(true);
		session.fen.gui.save.setVisible(true);
		session.fen.gui.quit.setVisible(true);
	}

	public void resume() {
		session.stateGUI = StateHUD.GAME;
		session.captureMouse = true;

		session.fen.cursorVisible(false);

		session.fen.gui.resume.setVisible(false);
		session.fen.gui.options.setVisible(false);
		session.fen.gui.save.setVisible(false);
		session.fen.gui.quit.setVisible(false);
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