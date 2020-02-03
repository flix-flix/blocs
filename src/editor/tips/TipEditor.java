package editor.tips;

import utils.panels.help.Tip;

public enum TipEditor implements Tip {

	ZOOM, ROTATE, GRID, PICK_COLOR, FACE_NAME, LINE_SQUARE;

	@Override
	public TipEditor next() {
		return values()[(ordinal() + 1) % values().length];
	}

	@Override
	public TipEditor previous() {
		return values()[(ordinal() + values().length - 1) % values().length];
	}

	@Override
	public String getPath() {
		return "tips.editor.global.";
	}
}
