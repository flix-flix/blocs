package game;

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
		return game.getAction() == UserAction.CREA_DESTROY || game.gamemode == GameMode.CREATIVE;
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
		return game.gamemode == GameMode.CREATIVE;
	}

	@Override
	public void cacheTarget() {
		targetedCube = game.target.cube;
		targetedFace = game.target.face;
	}

	// =========================================================================================================================

	@Override
	public void selectCube(Cube cube) {
		game.gui.select(cube);
	}

	@Override
	public Cube getCubeToAdd() {
		return game.getNextCube();
	}

	@Override
	public void applyAction() {
		if (game.gui.unit != null)
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
			if (game.gamemode == GameMode.CLASSIC)
				camera.moveY(e.getWheelRotation() * 10);
	}

	// =========================================================================================================================

	@Override
	public void mouseMoved(MouseEvent e) {
		int startW = game.gamemode == GameMode.CLASSIC ? 400 : 0;

		if (getEnvironment() == null)
			return;

		if (game.gamemode == GameMode.CLASSIC)
			getEnvironment().setTarget(e.getX() - 8 - startW, e.getY() - 32);
		else if (game.gamemode == GameMode.CREATIVE) {
			mouse(e.getX(), e.getY());
			getEnvironment().setTargetCenter();
		}
	}

	// =========================================================================================================================

	@Override
	public void keyPressed(KeyEvent e) {
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

			case KEY_EXCLAMATION:
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

	// =========================================================================================================================

	public void dialog() {
		game.stateHUD = StateHUD.DIALOG;
	}
}
