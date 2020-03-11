package editor;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

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

		int code = e.getKeyCode();

		if (code == ALT)
			editor.switchFaceName();

		// Undo/Redo
		if (e.isControlDown())
			if (code == 'Z') {
				editor.undo();
				return;
			} else if (code == 'Y') {
				editor.redo();
				return;
			}

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

			editor.addCube();
		}
		// Remove
		else if (editor.action == ActionEditor.DELETE_CUBE) {
			editor.removeCube();
		}
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

			editor.rotationPoint.y = .5 + editor.modifiedAltitude;
			camera.look(editor.rotationPoint);
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
		int height = editor.multi.getMaxY();
		int width = Math.max(editor.multi.getMaxX() - editor.multi.getMinX(),
				editor.multi.getMaxZ() - editor.multi.getMinZ());

		return Math.max(super.farest(), Math.max(height, width) + 20);
	}
}
