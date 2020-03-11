package editor;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import environment.Environment3D;
import environment.KeyboardEnvironment3D;
import environment.extendsData.CubeClient;
import utils.Utils;

public class KeyboardEditor extends KeyboardEnvironment3D {

	EditorManager editorMan;
	EditorAbstract editor;

	// =============== Keys ===============
	protected static final int ALT = 18;
	protected static final int SHIFT = 16;

	boolean controlDown = false;
	boolean shiftDown = false;
	boolean altDown = false;

	// =============== Rotation ===============
	private int prevX, prevY;

	// =========================================================================================================================

	public KeyboardEditor(EditorAbstract editor) {
		super(editor.editorMan.fen, editor.editorMan);
		this.editor = editor;
		this.editorMan = editor.editorMan;
		this.camera = editor.camera;
	}

	// =========================================================================================================================

	@Override
	public void start() {
		super.start();

		camera = editor.camera;
		camera.look(editor.rotationPoint);
	}

	// =========================================================================================================================
	// Override abstract getters

	@Override
	public boolean isSelecting() {
		return false;
	}

	@Override
	public boolean isDestroying() {
		return false;
	}

	// =========================================================================================================================
	// Override abstract actions

	@Override
	public void selectCube(CubeClient cube) {
	}

	@Override
	public Environment3D getEnvironment() {
		return editorMan;
	}

	// =========================================================================================================================

	private void updateControlShiftStatus(KeyEvent e) {
		controlDown = e.isControlDown();
		shiftDown = e.isShiftDown();
		altDown = e.isAltDown();
	}

	// =========================================================================================================================

	@Override
	public void keyPressed(KeyEvent e) {
		updateControlShiftStatus(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		updateControlShiftStatus(e);
	}

	// =========================================================================================================================

	@Override
	public void rightClickPressed(MouseEvent e) {
		initDrag(e.getX(), e.getY());
		editorMan.editor.camera.look(editor.rotationPoint);

		super.rightClickPressed(e);

		if (editor.target != null && editor.target.cube != null)
			editor.loseTarget();
	}

	@Override
	public void rightClickReleased() {
		super.rightClickReleased();

		if (editor.target != null && editor.target.cube != null)
			editor.gainTarget();
	}

	@Override
	public void mouseDraged(MouseEvent e) {
		super.mouseDraged(e);

		// Rotate
		if (pressR) {
			editorMan.editor.camera.rotate(editor.rotationPoint, e.getX() - prevX, e.getY() - prevY);
			initDrag(e.getX(), e.getY());
		}
	}

	// =========================================================================================================================
	// Mouse Wheel

	@Override
	public void wheelRotation(MouseWheelEvent e) {
		if (editorMan.isPaused())
			return;

		int wheelRotation = e.getWheelRotation();
		if (editorMan.isRotateMode()) {
			// Don't go too close or too far away from the cube
			if ((wheelRotation > 0 && camera.vue.dist(editor.rotationPoint) < closest())
					|| (wheelRotation < 0 && camera.vue.dist(editor.rotationPoint) > farest()))
				return;

			camera.moveLooking(editor.rotationPoint, -wheelRotation);
		}
	}

	public double closest() {
		return 3;
	}

	public double farest() {
		return 20;
	}

	// =========================================================================================================================

	@Override
	public void mouseMoved(MouseEvent e) {
		int startW = 400;

		if (editorMan.isRotateMode())
			getEnvironment().setTarget(e.getX() - 8 - startW, e.getY() - 32);
		else
			Utils.debug("Only Rotate-Mode is available");
		// getEnvironment().setTargetCenter();
	}

	public void initDrag(int x, int y) {
		prevX = x;
		prevY = y;
	}

	// =========================================================================================================================

	@Override
	public void cameraMovement() {
		// Rotate-Mode
		if (editorMan.isRotateMode() && editor.type == editorMan.editorType)
			editorMan.rotateCamera(editor.rotationPoint, forwardKeyEnabled, backwardKeyEnabled, rightKeyEnabled,
					leftKeyEnabled);
		// Classic-Mode
		else
			super.cameraMovement();
	}
}
