package client.session;

import java.awt.Image;

import utils.FlixBlocksUtils;

public enum UserAction {
	MOUSE, CREA_ADD, CREA_DESTROY;

	// =========================================================================================================================

	public Image getImage() {
		return FlixBlocksUtils.getImage("menu/" + name().toLowerCase());
	}
}
