package mainMenu.buttons;

import java.awt.Image;

import mainMenu.MainMenu;
import mainMenu.MainMenu.MainMenuAction;
import utils.FlixBlocksUtils;
import utils.panels.ClickListener;
import utils.panels.FButton;

public class ButtonAction extends FButton {
	private static final long serialVersionUID = 7756689488731481855L;

	private static Image options, quit;

	static {
		quit = FlixBlocksUtils.getImage("static/quit");
		options = FlixBlocksUtils.getImage("static/options");
	}

	// =========================================================================================================================

	public ButtonAction(MainMenu main, MainMenuAction action) {
		setImage(action == MainMenuAction.QUIT ? quit : options);

		setClickListener(new ClickListener() {
			@Override
			public void leftClick() {
				main.click(action);
			}
		});
	}
}
