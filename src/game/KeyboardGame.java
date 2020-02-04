package game;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import data.map.Cube;
import environment.Environment3D;
import environment.KeyboardEnvironment3D;
import server.game.GameMode;
import window.Key;

public class KeyboardGame extends KeyboardEnvironment3D {

	private Game game;

	// =========================================================================================================================

	public KeyboardGame(Game game) {
		super(game.fen, game);
		this.game = game;
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
	public boolean isPreview() {
		return game.getAction() == UserAction.CREA_ADD;
	}

	@Override
	public boolean isForceAdding() {
		return game.gameMode == GameMode.CREATIVE;
	}

	@Override
	public void cacheTarget() {
		targetedCube = game.getTarget().cube;
		targetedFace = game.getTarget().face;
	}

	// =========================================================================================================================

	@Override
	public void selectCube(Cube cube) {
		game.select(cube);
	}

	@Override
	public Cube getCubeToAdd() {
		return game.getNextCube();
	}

	@Override
	public void applyAction() {
		if (game.selectedUnit != null)
			game.unitDoAction();
	}

	@Override
	public Environment3D getEnvironment() {
		return game;
	}

	// =========================================================================================================================

	@Override
	public void wheelRotation(MouseWheelEvent e) {
		if (!paused)
			if (game.cameraMode == CameraMode.CLASSIC)
				camera.moveY(e.getWheelRotation() * 10);
	}

	// =========================================================================================================================

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

	public void setTargetOnMouse() {
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		setTarget(mouse.x, mouse.y);
	}

	public void setTarget(int x, int y) {
		int startW = game.cameraMode == CameraMode.CLASSIC ? 400 : 0;
		getEnvironment().setTarget(x - 8 - startW, y - 32);
	}

	// =========================================================================================================================

	@Override
	public void keyPressed(KeyEvent e) {
		if (game.stateHUD == StateHUD.PAUSE) {
			if (Key.get(e.getKeyCode()) != null)
				switch (Key.get(e.getKeyCode())) {
				case PAUSE:
					game.resume();
					break;
				default:
					break;
				}
		} else if (game.stateHUD == StateHUD.DIALOG) {
			if (Key.get(e.getKeyCode()) != null)
				switch (Key.get(e.getKeyCode())) {
				case PAUSE:
					game.messages.clearLine();
					game.resume();
					break;
				case VALID:
					game.messages.send();
					game.keyboard.mouseToCenter();
					game.stateHUD = StateHUD.GAME;
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
		} else if (game.stateHUD == StateHUD.GAME) {
			super.keyPressed(e);

			if (Key.get(e.getKeyCode()) != null)
				switch (Key.get(e.getKeyCode())) {

				case ACCESS_1:
					game.setGameMode(GameMode.CREATIVE);
					break;
				case ACCESS_2:
					game.setGameMode(GameMode.CLASSIC);
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
		game.stateHUD = StateHUD.DIALOG;
	}
}
