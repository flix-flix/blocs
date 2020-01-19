package client.editor.history;

import client.editor.Editor;

public interface History {

	public void undo(Editor editor);

	public void redo(Editor editor);
}
