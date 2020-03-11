package editor.history;

import editor.EditorAbstract;

public interface History<E extends EditorAbstract> {

	public void undo(E editorAbstract);

	public void redo(E editor);
}
