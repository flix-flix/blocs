package client.editor;

public class SizeHistory implements History {

	int oldSize, newSize;

	public SizeHistory(int oldSize, int newSize) {
		this.oldSize = oldSize;
		this.newSize = newSize;
	}

	@Override
	public void undo(Editor editor) {
		editor.setTextureSize(oldSize);
	}

	@Override
	public void redo(Editor editor) {
		editor.setTextureSize(newSize);
	}
}
