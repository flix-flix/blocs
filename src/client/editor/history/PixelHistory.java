package client.editor.history;

import client.editor.Editor;
import data.map.enumerations.Face;

public class PixelHistory implements History {

	public Face face;
	public int col, row;
	public int oldColor, newColor;

	/** The previous click location */
	public int prevCol, prevRow;
	/** The next click location (Different from row/col for line/square draw) */
	public int nextCol, nextRow;

	public PixelHistory(Face face, int col, int row, int oldColor, int newColor, int prevCol, int prevRow, int nextCol,
			int nextRow) {
		this.face = face;
		this.col = col;
		this.row = row;
		this.oldColor = oldColor;
		this.newColor = newColor;
		this.prevCol = prevCol;
		this.prevRow = prevRow;
		this.nextCol = nextCol;
		this.nextRow = nextRow;
	}

	@Override
	public void undo(Editor editor) {
		editor.setPixel(face, col, row, oldColor);
		editor.setLastPixel(face, prevCol, prevRow);
	}

	@Override
	public void redo(Editor editor) {
		editor.setPixel(face, col, row, newColor);
		editor.setLastPixel(face, nextCol, nextRow);
	}
}
