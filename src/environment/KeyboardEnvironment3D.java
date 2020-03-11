package environment;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import data.map.Coord;
import data.map.enumerations.Face;
import environment.extendsData.CubeClient;
import environment.extendsData.MapClient;
import graphicEngine.calcul.Camera;
import server.send.SendAction;
import utils.Utils;
import window.Fen;
import window.Key;
import window.KeyBoard;

public abstract class KeyboardEnvironment3D implements KeyBoard {

	// =============== ? ===============
	protected Fen fen;
	protected Environment3D env;

	protected MapClient map;
	protected Camera camera;

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

	// =============== Options ===============
	protected int speedModifier = 1;

	// =============== Engine data ===============
	/** Ticks count (camera movements/sec) */
	public int ticks = 0;

	// =============== Status ===============
	protected boolean started = false;
	protected boolean paused = false;

	// =============== Target ===============
	protected CubeClient targetedCube;
	protected Coord targetedCoord;
	protected Face targetedFace;

	// =============== Thread ===============
	private boolean run = true;

	// =========================================================================================================================

	public KeyboardEnvironment3D(Fen fen, Environment3D env) {
		this.fen = fen;
		this.env = env;

		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	// =========================================================================================================================

	public void start() {
		if (!run)
			return;

		started = true;

		map = env.getMap();
		camera = env.getCamera();

		Thread t = new Thread(new ThreadCameraMovements());
		t.setName("Camera Movements");
		t.start();
	}

	public void stop() {
		run = false;
	}

	// =========================================================================================================================

	public abstract boolean isSelecting();

	public abstract boolean isDestroying();

	/**
	 * Set {@link #targetedCube}, {@link #targetedCoord} and {@link #targetedFace}
	 */
	public void cacheTarget() {
		targetedCube = env.getTarget().cube;
		targetedCoord = targetedCube == null ? null : targetedCube.coords();
		targetedFace = env.getTarget().face;
	}

	// =========================================================================================================================

	public abstract void selectCube(CubeClient cube);

	public abstract Environment3D getEnvironment();

	// =========================================================================================================================

	@Override
	public void rightClickPressed(MouseEvent e) {
		pressR = true;

		cacheTarget();
	}

	@Override
	public void leftClickPressed(MouseEvent e) {
		pressL = true;

		cacheTarget();

		if (isDestroying()) {
			if (targetedCoord != null)
				env.send(SendAction.remove(targetedCoord));
		} else if (isSelecting())
			selectCube(targetedCube);
	}

	@Override
	public void rightClickReleased() {
		pressR = false;
	}

	@Override
	public void leftClickReleased() {
		pressL = false;
	}

	// =========================================================================================================================

	@Override
	public void keyPressed(KeyEvent e) {
		enableAction(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		stopAction(e);
	}

	// =========================================================================================================================

	protected void enableAction(KeyEvent e) {
		if (Key.get(e.getKeyCode()) != null)
			switch (Key.get(e.getKeyCode())) {
			case FORWARD:
				forwardKeyEnabled = true;
				break;
			case BACKWARD:
				backwardKeyEnabled = true;
				break;
			case RIGHT:
				rightKeyEnabled = true;
				break;
			case LEFT:
				leftKeyEnabled = true;
				break;
			case UP:
				jumpKeyEnabled = true;
				break;
			case DOWN:
				sneakKeyEnabled = true;
				break;
			case SPEED_BOOST:
				sprintKeyEnabled = true;
				break;

			case DEVLOP:
				getEnvironment().switchDevlopMode();
				break;

			default:
				break;
			}
	}

	protected void stopAction(KeyEvent e) {
		if (Key.get(e.getKeyCode()) != null)
			switch (Key.get(e.getKeyCode())) {
			case FORWARD:
				forwardKeyEnabled = false;
				break;
			case BACKWARD:
				backwardKeyEnabled = false;
				break;
			case RIGHT:
				rightKeyEnabled = false;
				break;
			case LEFT:
				leftKeyEnabled = false;
				break;
			case UP:
				jumpKeyEnabled = false;
				break;
			case DOWN:
				sneakKeyEnabled = false;
				break;
			case SPEED_BOOST:
				sprintKeyEnabled = false;
				break;
			default:
				break;
			}
	}

	// =========================================================================================================================

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseDraged(MouseEvent e) {
		mouseMoved(e);
	}

	// =========================================================================================================================

	/**
	 * Move the Camera orientation in function of the mouse cursor position then
	 * replace the cursor in the middle of the screen
	 * 
	 * @param mouseX
	 *            X location of the mouse in the environment
	 * @param mouseY
	 *            Y location of the mouse in the environment
	 */
	public void rotateCamera(int mouseX, int mouseY) {
		Point fenLocation = fen.getLocationOnScreen();
		if (mouseFreeze) {
			robot.mouseMove(fenLocation.x + fen.getWidth() / 2, fenLocation.y + fen.getHeight() / 2);

			mouseFreeze = !(fen.getWidth() / 2 == mouseX && fen.getHeight() / 2 == mouseY);
		}

		if (!paused && !mouseFreeze) {
			camera.setVx(camera.getVx() - ((fen.getWidth() / 2 - mouseX) * mouseSpeed));
			camera.setVy(camera.getVy() + ((fen.getHeight() / 2 - mouseY) * mouseSpeed));

			robot.mouseMove(fenLocation.x + fen.getWidth() / 2, fenLocation.y + fen.getHeight() / 2);

			if (camera.getVx() >= 360)
				camera.setVx(camera.getVx() - 360);
			else if (camera.getVx() < 0)
				camera.setVx(camera.getVx() + 360);
			if (camera.getVy() > 90)
				camera.setVy(90);
			else if (camera.getVy() < -90)
				camera.setVy(-90);
		}
	}

	public void mouseToCenter() {
		mouseFreeze = true;
		robot.mouseMove(fen.getWidth() / 2, fen.getHeight() / 2);
	}

	// =========================================================================================================================

	@Override
	public void wheelRotation(MouseWheelEvent e) {
	}

	// =========================================================================================================================

	public void cameraMovement() {
		double x = getCameraMovementX(camera.getVx(), forwardKeyEnabled, backwardKeyEnabled, rightKeyEnabled,
				leftKeyEnabled);
		double z = getCameraMovementZ(camera.getVx(), forwardKeyEnabled, backwardKeyEnabled, rightKeyEnabled,
				leftKeyEnabled);

		// Slow down the camera if moving in 2 directions
		if ((rightKeyEnabled || leftKeyEnabled) && (forwardKeyEnabled || backwardKeyEnabled)) {
			x /= 1.414;
			z /= 1.414;
		}

		// Slow down the camera if not running
		if (!sprintKeyEnabled) {
			x *= .2;
			z *= .2;
		}

		camera.move(x * speedModifier, z * speedModifier);

		if (jumpKeyEnabled)
			camera.moveY(.4);
		if (sneakKeyEnabled)
			camera.moveY(-.4);
	}

	/**
	 * Given current orientation and enabled keys returns the deplacement on the X
	 * axe
	 */
	public double getCameraMovementX(double vx, boolean forward, boolean backward, boolean right, boolean left) {
		double x = 0;

		if (forward)
			x = Math.cos(vx * Utils.toRadian);
		if (backward)
			x -= Math.cos(vx * Utils.toRadian);
		if (right)
			x += Math.cos((vx + 90) * Utils.toRadian);
		if (left)
			x -= Math.cos((vx + 90) * Utils.toRadian);

		return x;
	}

	/**
	 * Given current orientation and enabled keys returns the deplacement on the Z
	 * axe
	 */
	public double getCameraMovementZ(double vx, boolean forward, boolean backward, boolean right, boolean left) {
		double z = 0;

		if (forward)
			z = Math.sin(vx * Utils.toRadian);
		if (backward)
			z -= Math.sin(vx * Utils.toRadian);
		if (right)
			z += Math.sin((vx + 90) * Utils.toRadian);
		if (left)
			z -= Math.sin((vx + 90) * Utils.toRadian);

		return z;
	}

	// =========================================================================================================================

	public void setSpeedModifier(int speedModifier) {
		this.speedModifier = speedModifier;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	// =========================================================================================================================

	class ThreadCameraMovements implements Runnable {
		public void run() {
			while (run) {
				ticks++;

				if (!paused)
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
