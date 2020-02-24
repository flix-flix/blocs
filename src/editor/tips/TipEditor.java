package editor.tips;

import utilsBlocks.help.Tip;

public enum TipEditor implements Tip<TipEditor> {

	ZOOM, ROTATE, GRID, UNDO, FACE_NAME;

	@Override
	public String getPath() {
		return "tips.editor.global.";
	}

	@Override
	public Tip<TipEditor>[] _values() {
		return values();
	}
}
