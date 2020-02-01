package environment;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import data.map.Coord;
import data.map.Cube;
import data.map.enumerations.Face;
import environment.extendsData.CubeClient;
import environment.extendsData.MapClient;
import graphicEngine.calcul.Camera;
import utils.FlixBlocksUtils;
import window.Fen;
import window.Key;
import window.KeyBoard;

public abstract class KeyboardEnvironment3D extends KeyBoard {

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

	protected boolean paused = false;

	protected Fen fen;
	protected Environment3D env;

	protected MapClient map;
	protected Camera camera;

	// ====
	protected Cube targetedCube;
	protected Face targetedFace;

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
		map = env.getMap();
		camera = env.getCamera();

		Thread cameraMovements = new Thread(new ThreadCameraMovements());
		cameraMovements.setName("Camera Movements");
		cameraMovements.start();
	}

	// =========================================================================================================================

	public abstract boolean isDestroying();

	public abstract boolean isSelecting();

	public abstract boolean isForceAdding();

	public abstract boolean isPreview();

	public abstract void cacheTarget();

	// =========================================================================================================================

	public abstract void selectCube(Cube cube);

	public abstract Cube getCubeToAdd();

	public abstract void applyAction();

	public abstract Environment3D getEnvironment();

	// =========================================================================================================================

	@Override
	public void rightClickPressed(MouseEvent e) {
		pressR = true;

		cacheTarget();

		// Add a cube to the map
		if (isForceAdding()) {
			Cube cubeToAdd = getCubeToAdd();
			if (targetedCube != null && targetedFace != null && cubeToAdd != null) {
				cubeToAdd.setCoords(new Coord(targetedCube).face(targetedFace));

				map.add(cubeToAdd);
			}
		}

		else {
			if (targetedCube != null && targetedFace != null) {

				// Set the previewed cube as real one
				if (isPreview()) {
					CubeClient model = map.gridGet(new Coord(targetedCube).face(targetedFace));
					if (model != null && model.isPreview()) {
						// Check if multibloc can be added at this position
						if (model.multibloc != null && !model.multibloc.valid)
							return;

						map.setHighlight(model, false);
						map.setPreview(model, false);
						map.setTargetable(model, true);
					}
				}
				// If any
				else
					applyAction();
			}
			return;
		}
	}

	@Override
	public void leftClickPressed(MouseEvent e) {
		pressL = true;

		cacheTarget();

		if (isDestroying()) {
			if (targetedCube != null)
				remove(targetedCube);
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
			case SPRINT:
				sprintKeyEnabled = true;
				break;

			case DEVLOP:
				getEnvironment().switchDevlopMode();
				break;

			default:
				break;
			}
	}

	@Override
	public void keyReleased(KeyEvent e) {
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
			case SPRINT:
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
	// TODO [Move] -> Map

	public void remove(Cube cube) {
		if (cube.unit != null)
			map.removeUnit(cube.unit);
		else
			map.remove((CubeClient) cube);
	}

	// =========================================================================================================================

	/**
	 * Move the Camera orientation in function of the mouse cursor position then
	 * replace the cursor in the middle of the screen
	 * 
	 * @param mouseX
	 *            X location of the mouse in the frame
	 * @param mouseY
	 *            Y location of the mouse in the frame
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
		double x, z;

		x = cameraMovementX(camera.getVx(), forwardKeyEnabled, backwardKeyEnabled, rightKeyEnabled, leftKeyEnabled);
		z = cameraMovementZ(camera.getVx(), forwardKeyEnabled, backwardKeyEnabled, rightKeyEnabled, leftKeyEnabled);

		if (jumpKeyEnabled)
			camera.moveY(.4);
		if (sneakKeyEnabled)
			camera.moveY(-.4);

		// Slow down the camera if moving in 2 directions
		if ((rightKeyEnabled || leftKeyEnabled) && (forwardKeyEnabled || backwardKeyEnabled)) {
			x *= .5;
			z *= .5;
		}

		// Slow down the camera if not running
		if (!sprintKeyEnabled) {
			x *= .3;
			z *= .3;
		}
		camera.move(x, z);
	}

	public double cameraMovementX(double vx, boolean forward, boolean backward, boolean right, boolean left) {
		double x = 0;

		if (forward)
			x = Math.cos(vx * FlixBlocksUtils.toRadian);
		if (backward)
			x -= Math.cos(vx * FlixBlocksUtils.toRadian);
		if (right)
			x += Math.cos((vx + 90) * FlixBlocksUtils.toRadian);
		if (left)
			x -= Math.cos((vx + 90) * FlixBlocksUtils.toRadian);

		return x;
	}

	public double cameraMovementZ(double vx, boolean forward, boolean backward, boolean right, boolean left) {
		double z = 0;

		if (forward)
			z = Math.sin(vx * FlixBlocksUtils.toRadian);
		if (backward)
			z -= Math.sin(vx * FlixBlocksUtils.toRadian);
		if (right)
			z += Math.sin((vx + 90) * FlixBlocksUtils.toRadian);
		if (left)
			z -= Math.sin((vx + 90) * FlixBlocksUtils.toRadian);

		return z;
	}

	// =========================================================================================================================

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	// =========================================================================================================================

	class ThreadCameraMovements implements Runnable {
		public void run() {
			while (true) {
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
