package game.panels;

import data.id.ItemID;
import data.id.ItemTableClient;
import data.map.Cube;
import environment.extendsData.CubeClient;
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

		if (action == UserAction.CREA_ADD)
			setModel(new CubeClient(new Cube(ItemID.GRASS)));
		else
			setImage(Utils.getImage(
					ItemTableClient.getTexturePack().getFolder() + "menu/game/" + action.name().toLowerCase()));

		setSelectable(true, false);
		setPadding(5);
	}

	// =========================================================================================================================

	@Override
	public void eventClick() {
		game.setUserAction(action);
	}
}
