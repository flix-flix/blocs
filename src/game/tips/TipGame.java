package game.tips;

import utilsBlocks.help.Tip;

public enum TipGame implements Tip<TipGame> {

	CAMERA, USER_ACTION, MOUSE, UNIT, ADD, REMOVE, F3, CAMERA_MODE, CHAT;

	@Override
	public String getPath() {
		return "tips.game.global.";
	}

	@Override
	public Tip<TipGame>[] _values() {
		return values();
	}
}
