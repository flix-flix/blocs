package editor.tips;

import utilsBlocks.help.Tip;

public enum TipCalk implements Tip<TipCalk> {

	SELECT_RECT, COPY_PASTE, MOVE, ROTATE, APPLY_DELETE, SELECT_ALL;

	@Override
	public String getPath() {
		return "tips.editor.calk.";
	}

	@Override
	public Tip<TipCalk>[] _values() {
		return values();
	}
}
