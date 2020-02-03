package editor.tips;

import utils.panels.help.Tip;

public enum TipCalk implements Tip {

	APPLY, DELETE, ROTATE;

	@Override
	public TipCalk next() {
		return values()[(ordinal() + 1) % values().length];
	}

	@Override
	public TipCalk previous() {
		return values()[(ordinal() + values().length - 1) % values().length];
	}

	@Override
	public String getPath() {
		return "tips.editor.calk.";
	}
}
