package client.session;

import java.awt.Image;

import utils.FlixBlocksUtils;

public enum Action {
	// Menu
	MOUSE, SELECT, CREA_ADD, CREA_DESTROY,

	// Unit
	GOTO, MINE, BUILD, DESTROY,

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
