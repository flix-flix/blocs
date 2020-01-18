package client.editor;

public interface History {

	public void undo(Editor editor);

	public void redo(Editor editor);
}
