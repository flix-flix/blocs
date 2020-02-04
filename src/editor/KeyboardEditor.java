package editor;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import data.map.Cube;
import data.map.enumerations.Face;
import environment.Environment3D;
import environment.KeyboardEnvironment3D;
import graphicEngine.structures.Quadri;
import utils.FlixBlocksUtils;

public class KeyboardEditor extends KeyboardEnvironment3D {

	Editor editor;

	// =============== Rotation ===============
	private int prevX, prevY;

	// =============== Last click memory ===============
	private Face lastClickedFace = null;
	/** Coord of the last clicked Quadri */
	private int lastClickedX = Quadri.NOT_NUMBERED, lastClickedY = Quadri.NOT_NUMBERED;

	// =============== Keys ===============
	private static final int ALT = 18;
	private static final int SHIFT = 16;

	boolean controlDown = false;
	boolean shiftDown = false;
	boolean altDown = false;

	// =============== Selection Square ===============
	/** Face on which the selection is */
	private Face selectionFace;
	/** Coords of the first clicked corner of the selection */
	private int selectionStartX, selectionStartY;
	/** Coords of the corner of the selection at the drag-release */
	private int selectionEndX, selectionEndY;

	// =============== Calk ===============
	/**
	 * Value of {@link #calkMoveClickX} and {@link #calkMoveClickY} if no previous
	 * location
	 */
	private static final int VOID = -999;
	/** Remember the previous location of the calk drag point */
	private int calkMoveClickX = VOID, calkMoveClickY = VOID;

	// =========================================================================================================================

	public KeyboardEditor(Editor editor) {
		super(editor.fen, editor);
		this.editor = editor;
	}

	// =========================================================================================================================
	// Override abstract getters

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
	// Override abstract actions

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
	// Update memory

	public void updateControlShiftStatus(KeyEvent e) {
		controlDown = e.isControlDown();
		shiftDown = e.isShiftDown();
		altDown = e.isAltDown();
	}

	void updateLastClickedQuadri() {
		lastClickedFace = editor.target.face;
		lastClickedX = editor.getTargetedX();
		lastClickedY = editor.getTargetedY();
	}

	public void initDrag(int x, int y) {
		prevX = x;
		prevY = y;
	}

	// =========================================================================================================================
	// Selection

	Face getSelectedFace() {
		if (lastClickedFace == null)
			return editor.getFrontFace();
		return lastClickedFace;
	}

	/** Extends the selection to the full face */
	void selectAll(Face face) {
		selectionFace = face;
		selectionStartX = 0;
		selectionStartY = 0;
		selectionEndX = editor.getTextureSize() - 1;
		selectionEndY = editor.getTextureSize() - 1;
	}

	/** Cancel the selection */
	void selectNothing() {
		selectionFace = null;
		editor.removeLayerSelection();
	}

	void refreshLayerSelection() {
		if (selectionFace == null)
			editor.removeLayerSelection();
		else
			editor.refreshLayerSelection(selectionFace, selectionStartX, selectionStartY, selectionEndX, selectionEndY);
	}

	// =========================================================================================================================
	// Mouse Click

	@Override
	public void rightClickPressed(MouseEvent e) {
		editor.looseListeningKey();

		initDrag(e.getX(), e.getY());
		editor.lookCube();

		super.rightClickPressed(e);
	}

	@Override
	public void leftClickPressed(MouseEvent e) {
		editor.looseListeningKey();

		updateLastClickedQuadri();

		// Click in void
		if (editor.target.face == null || editor.target.quadri == Quadri.NOT_NUMBERED) {
			// Loose selection
			if (editor.getAction() == ActionEditor.SQUARE_SELECTION) {
				calkMoveClickX = VOID;
				calkMoveClickY = VOID;
				selectNothing();
			}
		}

		// Click on quadri
		else {
			// !pressR : Ignore action during rotation
			if (!pressR && editor.getAction() != null)
				switch (editor.getAction()) {
				case PAINT:
					if (controlDown && (!shiftDown || !editor.isPreviewCube()))
						editor.pickColor();
					else {
						if (!editor.isPreviewCube())
							break;

						if (shiftDown && editor.hasLastPixel())
							if (controlDown)
								editor.paintSquare();
							else
								editor.paintLine();
						else
							editor.paintPixel();
					}
					break;

				case FILL:
					if (controlDown)
						editor.pickColor();
					else if (editor.isPreviewCube()) {
						editor.initFill(editor.target.face, lastClickedY, lastClickedX);

						editor.updatePreviewTexture();
						editor.historyPack();
					}
					break;

				case SQUARE_SELECTION:
					// Init move
					if (editor.hasCalk()) {
						if (editor.isCursorInCalk()) {
							calkMoveClickX = lastClickedX;
							calkMoveClickY = lastClickedY;
						} else {
							calkMoveClickX = VOID;
							calkMoveClickY = VOID;
						}
						break;
					}

					// Init selection
					selectionFace = editor.target.face;
					selectionStartX = lastClickedX;
					selectionStartY = lastClickedY;
					selectionEndX = selectionStartX;
					selectionEndY = selectionStartY;
					refreshLayerSelection();
					break;

				default:
					break;
				}
		}
		super.leftClickPressed(e);
	}

	@Override
	public void rightClickReleased() {
		super.rightClickReleased();
	}

	@Override
	public void leftClickReleased() {
		super.leftClickReleased();
		// Save the current paint line (drag)
		editor.historyPack();
	}

	// =========================================================================================================================
	// Mouse Wheel

	@Override
	public void wheelRotation(MouseWheelEvent e) {
		int wheelRotation = e.getWheelRotation();
		if (editor.isRotateMode()) {
			// Don't go too close or too far away from the cube
			if ((wheelRotation > 0 && camera.vue.dist(.5, .5, .5) < 3)
					|| (wheelRotation < 0 && camera.vue.dist(.5, .5, .5) > 20))
				return;

			camera.move(cameraMovementX(camera.getVx(), wheelRotation > 0, wheelRotation < 0, false, false),
					cameraMovementZ(camera.getVx(), wheelRotation > 0, wheelRotation < 0, false, false));

			if (editor.isRotateMode())
				editor.lookCube();
		}
	}

	// =========================================================================================================================
	// Mouse Motion

	@Override
	public void mouseMoved(MouseEvent e) {
		int startW = 400;

		if (editor.isRotateMode())
			getEnvironment().setTarget(e.getX() - 8 - startW, e.getY() - 32);
		else
			FlixBlocksUtils.debug("Only Rotate-Mode is available");
		// getEnvironment().setTargetCenter();
	}

	@Override
	public void mouseDraged(MouseEvent e) {
		super.mouseDraged(e);
		// Rotate
		if (pressR) {
			editor.rotateCamera(e.getX() - prevX, e.getY() - prevY);
			initDrag(e.getX(), e.getY());
		} else if (pressL) {
			if (editor.getAction() == ActionEditor.SQUARE_SELECTION) {
				int x = editor.getTargetedX();
				int y = editor.getTargetedY();

				// Calk deplacement
				if (editor.hasCalk()) {
					// Ignore clicks on void and wrong faces
					if (editor.target.quadri == Quadri.NOT_NUMBERED || calkMoveClickX == VOID || calkMoveClickY == VOID
							|| editor.target.face != editor.getCalkFace())
						return;

					editor.moveCalk(x - calkMoveClickX, y - calkMoveClickY);

					calkMoveClickX = x;
					calkMoveClickY = y;
					return;
				}
				// Resize selection
				if (selectionFace != editor.target.face)
					return;
				selectionEndX = x;
				selectionEndY = y;
				refreshLayerSelection();
			}
		}
	}

	// =========================================================================================================================
	// Keys

	@Override
	public void keyPressed(KeyEvent e) {
		updateControlShiftStatus(e);

		fen.updateCursor();
		editor.refreshLineSquareLayer();

		int code = e.getKeyCode();

		// Show face names
		if (code == ALT)
			editor.refreshLayerFace();

		// Undo/Redo
		if (e.isControlDown())
			if (code == 'Z') {
				editor.undo();
				return;
			} else if (code == 'Y') {
				editor.redo();
				return;
			}

		// Selection
		if (e.isControlDown())
			if (code == 'A') {
				if (e.isShiftDown())
					selectNothing();
				else
					selectAll(getSelectedFace());
				return;
			} else if (code == 'C') {
				if (selectionFace != null)
					editor.copy(selectionFace, selectionStartX, selectionStartY, selectionEndX, selectionEndY);
				return;
			} else if (code == 'V') {
				editor.paste(getSelectedFace(), lastClickedX, lastClickedY);
				return;
			}

		// Calk
		if (editor.hasCalk()) {
			if (code == 10) {// Enter
				editor.applyCalk();
				return;
			} else if (code == 37) {// Left
				editor.rotateCalkRight();
				editor.rotateCalkRight();
				editor.rotateCalkRight();
				return;
			} else if (code == 39) {// Right
				editor.rotateCalkRight();
				return;
			} else if (code == 27)// Esc
				editor.deleteCalk();
		}

		// Consume SHIFT to allow line/square drawing
		if (code == SHIFT && editor.getAction() == ActionEditor.PAINT)
			return;

		// Writing
		if (editor.getButtonListeningKey() == ActionEditor.ITEM_NAME) {
			editor.write(e);
			return;
		}

		super.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		super.keyReleased(e);

		updateControlShiftStatus(e);

		fen.updateCursor();
		editor.refreshLineSquareLayer();

		int code = e.getKeyCode();

		if (code == ALT)
			editor.refreshLayerFace();
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
