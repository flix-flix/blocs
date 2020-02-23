package game.panels.menus;

import java.awt.Color;

import data.id.ItemTableClient;
import game.Game;
import server.send.Action;
import utils.Utils;
import utilsBlocks.ButtonBlocks;

public class ButtonGameAction extends ButtonBlocks {
	private static final long serialVersionUID = -2696383944798968722L;

	// =========================================================================================================================

	public ButtonGameAction(Game game, Action action) {
		setSelectable(true);
		setBackground(Color.DARK_GRAY);
		setSelectedColor(Color.GRAY);
		setPadding(5);

		setImage(Utils
				.getImage(ItemTableClient.getTexturePack().getFolder() + "menu/game/" + action.name().toLowerCase()));
		
		setWIP();
	}
}