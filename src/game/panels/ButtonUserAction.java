package game.panels;

import data.id.ItemID;
import data.id.ItemTableClient;
import data.map.Cube;
import game.Game;
import game.UserAction;
import utils.Utils;
import utilsBlocks.ButtonBlocks;

public class ButtonUserAction extends ButtonBlocks {
	private static final long serialVersionUID = 9127561890206065111L;

	private Game game;
	private UserAction action;

	// =========================================================================================================================

	public ButtonUserAction(Game game, UserAction action) {
		this.game = game;
		this.action = action;

		setSelectable(true, false);
		setPadding(5);

		if (action == UserAction.MOUSE)
			setSelected(true);

		if (action == UserAction.CREA_ADD)
			setModel(new Cube(ItemID.GRASS));
		else
			setImage(Utils.getImage(
					ItemTableClient.getTexturePack().getFolder() + "menu/game/" + action.name().toLowerCase()));
	}

	// =========================================================================================================================

	@Override
	public void eventClick() {
		game.setUserAction(action);
	}
}
