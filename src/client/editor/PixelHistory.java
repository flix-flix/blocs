package client.editor;

import data.map.enumerations.Face;

public class PixelHistory implements History {

	public Face face;
	public int col, row;
	public int oldColor, newColor;

	public PixelHistory(Face face, int col, int row, int oldColor, int newColor) {
		this.face = face;
		this.col = col;
		this.row = row;
		this.oldColor = oldColor;
		this.newColor = newColor;
	}

	@Override
	public void undo(Editor editor) {
		editor.setPixel(face, col, row, oldColor);
	}

	@Override
	public void redo(Editor editor) {
		editor.setPixel(face, col, row, newColor);
	}
}
