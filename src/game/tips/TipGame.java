package game.tips;

import utils.panels.help.Tip;

public enum TipGame implements Tip {

	CAMERA, USER_ACTION, MOUSE, ADD, REMOVE, F3;

	@Override
	public TipGame next() {
		return values()[(ordinal() + 1) % values().length];
	}

	@Override
	public TipGame previous() {
		return values()[(ordinal() + values().length - 1) % values().length];
	}

	@Override
	public String getPath() {
		return "tips.game.global.";
	}
}
