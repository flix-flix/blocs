package editor.history;

import data.map.enumerations.Face;
import editor.EditorCubeTexture;

public class PixelHistory implements History<EditorCubeTexture> {

	public Face face;
	public int x, y;
	public int oldColor, newColor;

	/** The previous click location */
	public int prevX, prevY;
	/** The next click location (Different from x/y for line/square draw) */
	public int nextX, nextY;

	public PixelHistory(Face face, int x, int y, int oldColor, int newColor, int prevCol, int prevRow, int nextCol,
			int nextRow) {
		this.face = face;
		this.x = x;
		this.y = y;
		this.oldColor = oldColor;
		this.newColor = newColor;
		this.prevX = prevCol;
		this.prevY = prevRow;
		this.nextX = nextCol;
		this.nextY = nextRow;
	}

	@Override
	public void undo(EditorCubeTexture editor) {
		editor.setPixel(face, x, y, oldColor);
		editor.setLastPixel(face, prevX, prevY);
	}

	@Override
	public void redo(EditorCubeTexture editor) {
		editor.setPixel(face, x, y, newColor);
		editor.setLastPixel(face, nextX, nextY);
	}
}
