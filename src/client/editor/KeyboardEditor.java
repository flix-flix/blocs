package client.editor;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import client.keys.KeyboardEnvironment3D;
import data.map.Cube;
import launcher.Environment3D;
import server.game.GameMode;

public class KeyboardEditor extends KeyboardEnvironment3D {

	Editor editor;

	// =========================================================================================================================

	public KeyboardEditor(Editor editor) {
		super(editor.fen, editor);
		this.editor = editor;
	}

	// =========================================================================================================================

	@Override
	public boolean isDestroying() {
		return false;
	}

	@Override
	public boolean isSelecting() {
		return false;
	}

	@Override
	public boolean isForceAdding() {
		return false;
	}

	@Override
	public boolean isPreview() {
		return false;
	}

	@Override
	public void cacheTarget() {

	}

	// =========================================================================================================================

	@Override
	public void selectCube(Cube cube) {

	}

	@Override
	public Cube getCubeToAdd() {
		// TODO Return null
		return null;
	}

	@Override
	public void applyAction() {
	}

	@Override
	public Environment3D getEnvironment() {
		return editor;
	}

	// =========================================================================================================================

	@Override
	public void rightClickPressed(MouseEvent e) {
		editor.rightClick(e);

		super.rightClickPressed(e);
	}

	@Override
	public void leftClickPressed(MouseEvent e) {
		if (editor.leftClick())
			return;

		super.leftClickPressed(e);
	}

	public void rightClickEnd() {
		pressR = false;
	}

	public void leftClickEnd() {
		pressL = false;

		editor.leftClickEnd();
	}

	// =========================================================================================================================

	@Override
	public void wheelRotation(MouseWheelEvent e) {
		int wheelRotation = e.getWheelRotation();
		if (editor.isRotateMode()) {
			camera.move(cameraMovementX(camera.getVx(), wheelRotation > 0, wheelRotation < 0, false, false),
					cameraMovementZ(camera.getVx(), wheelRotation > 0, wheelRotation < 0, false, false));
			editor.cameraMoved();
		}
	}

	// =========================================================================================================================

	@Override
	public void mouseMoved(MouseEvent e) {
		int startW = 400;

		if (editor.gamemode == GameMode.CLASSIC)
			getEnvironment().setTarget(e.getX() - 8 - startW, e.getY() - 32);
		else if (editor.gamemode == GameMode.CREATIVE)
			getEnvironment().setTargetCenter();
	}

	// =========================================================================================================================

	@Override
	public void keyReleased(KeyEvent e) {
		super.keyReleased(e);
		editor.keyReleased(e);
	}

	// =========================================================================================================================

	@Override
	public void mouseDraged(MouseEvent e) {
		editor.drag(e);
		super.mouseDraged(e);
	}

	// =========================================================================================================================

	@Override
	public void cameraMovement() {
		// Rotate-Mode
		if (editor.isRotateMode())
			editor.rotateCamera(forwardKeyEnabled, backwardKeyEnabled, rightKeyEnabled, leftKeyEnabled);

		// Classic-Mode
		else
			super.cameraMovement();
	}
}
