package data.dynamic;

import java.awt.Image;

import utils.FlixBlocksUtils;

public enum Action {
	// Unit
	GOTO, HARVEST, BUILD, DESTROY, DROP, ATTACK,

	// Building
	SPAWN("goto"), UPGRADE;

	// =========================================================================================================================

	String fileName;

	Action() {
	}

	Action(String str) {
		fileName = str;
	}

	// =========================================================================================================================

	public Image getImage() {
		return FlixBlocksUtils.getImage("menu/" + (fileName == null ? name().toLowerCase() : fileName));
	}
}
