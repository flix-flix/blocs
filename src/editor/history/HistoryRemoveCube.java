package editor.history;

import data.map.Coord;
import data.map.Cube;
import editor.EditorMultiCubes;

public class HistoryRemoveCube implements History<EditorMultiCubes> {

	Coord coord;
	int itemID;

	public HistoryRemoveCube(Coord coord, int itemID) {
		this.coord = coord;
		this.itemID = itemID;
	}

	@Override
	public void undo(EditorMultiCubes editor) {
		Cube cube = new Cube(coord, itemID);
		editor.map.add(cube);
		editor.multi.add(cube);
	}

	@Override
	public void redo(EditorMultiCubes editor) {
		editor.multi.remove(coord);
		editor.map.remove(coord);
	}
}
