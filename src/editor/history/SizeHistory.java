package editor.history;

import editor.EditorCubeTexture;

public class SizeHistory implements History<EditorCubeTexture> {

	int oldSize, newSize;

	public SizeHistory(int oldSize, int newSize) {
		this.oldSize = oldSize;
		this.newSize = newSize;
	}

	@Override
	public void undo(EditorCubeTexture editor) {
		editor.setTextureSize(oldSize);
	}

	@Override
	public void redo(EditorCubeTexture editor) {
		editor.setTextureSize(newSize);
	}
}
