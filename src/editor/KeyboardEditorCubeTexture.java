package editor;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import data.map.enumerations.Face;
import graphicEngine.calcul.Quadri;

public class KeyboardEditorCubeTexture extends KeyboardEditor {

	EditorCubeTexture editor;

	// =============== Last click memory ===============
	private Face lastClickedFace = null;
	/** Coord of the last clicked Quadri */
	private int lastClickedX = Quadri.NOT_NUMBERED, lastClickedY = Quadri.NOT_NUMBERED;

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

	public KeyboardEditorCubeTexture(EditorCubeTexture editor) {
		super(editor);
		this.editor = editor;
	}

	// =========================================================================================================================
	// Update memory

	void updateLastClickedQuadri() {
		lastClickedFace = editor.target.face;
		lastClickedX = editor.getTargetedX();
		lastClickedY = editor.getTargetedY();
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
		refreshLayerSelection();
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
		editorMan.looseListeningKey();
		super.rightClickPressed(e);
	}

	@Override
	public void leftClickPressed(MouseEvent e) {
		editorMan.looseListeningKey();

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
						editor.initFill(editor.target.face, lastClickedX, lastClickedY);

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
	public void leftClickReleased() {
		super.leftClickReleased();
		// Save the current paint line (drag)
		editor.historyPack();
	}

	// =========================================================================================================================
	// Mouse Motion

	@Override
	public void mouseDraged(MouseEvent e) {
		super.mouseDraged(e);
		if (pressL) {
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
		super.keyPressed(e);

		if (editor.editorMan.isPaused())
			return;

		fen.updateCursor();
		editor.refreshLineSquareLayer();

		int code = e.getKeyCode();

		// Show/Hide face names
		if (code == ALT) {
			editor.switchFaceName();
			return;
		}

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

		fen.updateCursor();
		editor.refreshLineSquareLayer();
	}
}
