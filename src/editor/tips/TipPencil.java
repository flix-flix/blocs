package editor.tips;

import utilsBlocks.help.Tip;

public enum TipPencil implements Tip<TipPencil> {

	NEW_COLOR, ALPHA, MEMORY, PICK_COLOR, LINE_SQUARE;

	@Override
	public String getPath() {
		return "tips.editor.pencil.";
	}

	@Override
	public Tip<TipPencil>[] _values() {
		return values();
	}
}
