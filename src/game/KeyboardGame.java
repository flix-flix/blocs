package game;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import data.map.Cube;
import environment.Environment3D;
import environment.KeyboardEnvironment3D;
import environment.extendsData.CubeClient;
import server.game.GameMode;
import window.Key;

public class KeyboardGame extends KeyboardEnvironment3D {

	private Game game;

	// =========================================================================================================================

	public KeyboardGame(Game game) {
		super(game.fen, game);
		this.game = game;
		setSpeedModifier(2);
	}

	// =========================================================================================================================

	@Override
	public boolean isDestroying() {
		return game.getAction() == UserAction.CREA_DESTROY || game.gameMode == GameMode.CREATIVE;
	}

	@Override
	public boolean isSelecting() {
		return game.getAction() == UserAction.MOUSE;
	}

	@Override
	public boolean isAdding() {
		return game.getAction() == UserAction.CREA_ADD;
	}

	@Override
	public boolean isAddable() {
		if (game.previewed == null)
			return false;

		if (game.previewed.multibloc != null && !game.previewed.multibloc.valid)
			return false;

		return true;
	}

	@Override
	public void cacheTarget() {
		targetedCube = game.getTarget().cube;
		targetedCoord = targetedCube == null ? null : targetedCube.coords();
		targetedFace = game.getTarget().face;
	}

	// =========================================================================================================================

	@Override
	public void selectCube(CubeClient cube) {
		game.select(cube);
	}

	@Override
	public Cube getCubeToAdd() {
		return game.cloneCubeToAdd();
	}

	@Override
	public void applyAction() {
		game.sendUnitAction();
	}

	@Override
	public Environment3D getEnvironment() {
		return game;
	}

	// =========================================================================================================================
	// Wheel

	@Override
	public void wheelRotation(MouseWheelEvent e) {
		if (started)
			if (!paused)
				if (game.cameraMode == CameraMode.CLASSIC) {
					if (e.getWheelRotation() > 0 && camera.vue.y >= 50)
						return;
					if (e.getWheelRotation() < 0 && camera.vue.y <= 25)
						return;
					camera.moveY(e.getWheelRotation() * 10);
				}
	}

	// =========================================================================================================================
	// Mouse

	@Override
	public void mouseMoved(MouseEvent e) {
		if (getEnvironment() == null)
			return;

		if (game.cameraMode == CameraMode.CLASSIC)
			setTarget(e.getX(), e.getY());
		else if (game.cameraMode == CameraMode.FIRST_PERSON) {
			rotateCamera(e.getX(), e.getY());
			getEnvironment().setTargetCenter();
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		getEnvironment().setTargetNull();
	}

	public void setTargetOnMouse() {
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		setTarget(mouse.x, mouse.y);
	}

	public void setTarget(int x, int y) {
		int startW = game.cameraMode == CameraMode.CLASSIC ? 400 : 0;
		getEnvironment().setTarget(x - 8 - startW, y - 32);
	}

	// =========================================================================================================================
	// Key Event

	@Override
	public void keyPressed(KeyEvent e) {
		if (!started)
			return;

		if (game.getStateHUD() == StateHUD.PAUSE) {
			if (Key.get(e.getKeyCode()) != null)
				switch (Key.get(e.getKeyCode())) {
				case PAUSE:
					game.resume();
					break;
				default:
					break;
				}
		} else if (game.getStateHUD() == StateHUD.DIALOG) {
			if (Key.get(e.getKeyCode()) != null)
				switch (Key.get(e.getKeyCode())) {
				case PAUSE:
					game.messages.clearLine();
					game.resume();
					break;
				case VALID:
					game.messages.send();
					game.keyboard.mouseToCenter();
					game.setStateHUD(StateHUD.GAME);
					break;
				case DEL:
					game.messages.deletePrevious();
					break;
				case SUPPR:
					game.messages.deleteNext();
					break;

				case ARROW_UP:
					game.messages.historyPrevious();
					break;
				case ARROW_DOWN:
					game.messages.historyNext();
					break;
				case ARROW_RIGHT:
					game.messages.cursorMoveRight();
					break;
				case ARROW_LEFT:
					game.messages.cursorMoveLeft();
					break;

				case PAGE_UP:
					game.messages.pageUp();
					break;
				case PAGE_DOWN:
					game.messages.pageDown();
					break;
				case KEY_TAB:
					break;
				case END:
					game.messages.end();
					break;
				case START:
					game.messages.start();
					break;

				default:
					break;
				}

			// TODO [Improve] List of accepted character in dialog
			char c = e.getKeyChar();
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
					|| " \\/(){}[]+-:;,!?#|&\"'_²@=<>%.*^$£€".contains("" + c))
				game.messages.write(c);
		} else if (game.getStateHUD() == StateHUD.GAME) {
			super.keyPressed(e);

			if (Key.get(e.getKeyCode()) != null)
				switch (Key.get(e.getKeyCode())) {

				case ACCESS_1:
					game.setCameraMode(CameraMode.FIRST_PERSON);
					break;
				case ACCESS_2:
					game.setCameraMode(CameraMode.CLASSIC);
					break;
				case ACCESS_3:
					break;
				case ACCESS_4:
					break;
				case ACCESS_5:
					break;
				case ACCESS_6:
					break;
				case ACCESS_7:
					break;
				case ACCESS_8:
					break;
				case ACCESS_9:
					break;

				// =====================================================

				case DIALOG:
					dialog();
					break;

				// =====================================================

				case PAUSE:
					game.pause();
					break;

				// =====================================================
				default:
					break;
				}
		}
	}

	// =========================================================================================================================

	public void dialog() {
		game.setStateHUD(StateHUD.DIALOG);
	}
}
