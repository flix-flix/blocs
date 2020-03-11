package editor;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import data.map.Cube;

public class KeyboardEditorMultiCubes extends KeyboardEditor {

	EditorMultiCubes editor;

	// =============== Options ===============
	private double upDownSpeed = 1;

	// =========================================================================================================================

	public KeyboardEditorMultiCubes(EditorMultiCubes editor) {
		super(editor);
		this.editor = editor;
	}

	// =========================================================================================================================

	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);

		if (e.getKeyCode() == ALT)
			editor.switchFaceName();

		// Writing
		if (editorMan.getButtonListeningKey() == ActionEditor.ITEM_TAG) {
			editorMan.write(e);
			return;
		}

		super.enableAction(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		super.keyReleased(e);
		super.stopAction(e);
	}

	// =========================================================================================================================

	@Override
	public void leftClickPressed(MouseEvent e) {
		super.leftClickPressed(e);

		if (editor.action == ActionEditor.ADD_CUBE) {
			if (pressR)
				return;

			Cube added;
			if ((added = addCube()) == null)
				return;

			editor.multi.add(added);
		}
		// Remove
		else if (editor.action == ActionEditor.DELETE_CUBE) {
			if (targetedCoord != null)
				editor.map.remove(targetedCoord);

			editor.multi.remove(targetedCoord);
		}

		// updateCameraLocation();
	}

	// =========================================================================================================================

	public void updateCameraLocation() {
		// rotationPoint.y = .5 + editor.multi.getMaxHeight() / 2. +
		// editor.modifiedAltitude;
		editor.rotationPoint.y = .5 + editor.modifiedAltitude;
		camera.look(editor.rotationPoint);
	}

	// =========================================================================================================================

	@Override
	public void wheelRotation(MouseWheelEvent e) {
		if (controlDown) {
			if (e.getWheelRotation() > 0) {
				editor.modifiedAltitude += upDownSpeed;
				camera.moveY(upDownSpeed);
			} else if (e.getWheelRotation() < 0) {
				editor.modifiedAltitude -= upDownSpeed;
				camera.moveY(-upDownSpeed);
			}

			updateCameraLocation();
		}

		else
			super.wheelRotation(e);
	}

	@Override
	public double closest() {
		return 6;
	}

	@Override
	public double farest() {
		return Math.max(super.farest(), editor.multi.getMaxHeight() + 10);
	}

	// =========================================================================================================================

	@Override
	public void cameraMovement() {
		super.cameraMovement();

	}
}
