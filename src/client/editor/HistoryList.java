package client.editor;

import java.util.ArrayList;

public class HistoryList implements History {

	ArrayList<History> list;

	public HistoryList(ArrayList<History> list) {
		this.list = list;
	}

	@Override
	public void undo(Editor editor) {
		for (int i = list.size() - 1; i >= 0; i--)
			list.get(i).undo(editor);
		editor.saveTexture();
	}

	@Override
	public void redo(Editor editor) {
		for (History h : list)
			h.redo(editor);
		editor.saveTexture();
	}
}
