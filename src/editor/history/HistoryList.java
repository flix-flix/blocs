package editor.history;

import java.util.ArrayList;

import editor.EditorAbstract;

public class HistoryList<E extends EditorAbstract> implements History<E> {

	ArrayList<History<E>> list;

	public HistoryList(ArrayList<History<E>> list) {
		this.list = list;
	}

	@Override
	public void undo(E editor) {
		for (int i = list.size() - 1; i >= 0; i--)
			list.get(i).undo(editor);
		editor.updateAfterUndoRedo();
	}

	@Override
	public void redo(E editor) {
		for (History<E> h : list)
			h.redo(editor);
		editor.updateAfterUndoRedo();
	}
}
