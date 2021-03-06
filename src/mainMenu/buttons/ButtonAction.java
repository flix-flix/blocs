package mainMenu.buttons;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import data.id.ItemTableClient;
import mainMenu.MainMenu;
import mainMenu.MainMenuAction;
import utils.Utils;
import utils.panels.ClickListener;
import utils.panels.FButton;

public class ButtonAction extends FButton {
	private static final long serialVersionUID = 7756689488731481855L;

	private static Image data, options, quit;

	static {
		String folder = ItemTableClient.getTexturePack().getFolder() + "/menu/mainMenu/";
		quit = Utils.getImage(folder + "quit");
		options = Utils.getImage(folder + "options");
		data = Utils.getImage(folder + "data");
	}

	private MainMenuAction action;

	// =========================================================================================================================

	public ButtonAction(MainMenu main, MainMenuAction action) {
		this.action = action;

		if (action == MainMenuAction.QUIT || action == MainMenuAction.SERVER_QUIT)
			setImage(quit);
		else if (action == MainMenuAction.OPTIONS)
			setImage(options);
		else if (action == MainMenuAction.DATA_MANAGER) {
			setImage(data);
			setBorder(9, Color.DARK_GRAY);

			setText("Data Manager");
			setFont(new Font("arial", Font.BOLD, 35));
			setForeground(Color.BLACK);
		}

		setClickListener(new ClickListener() {
			@Override
			public void leftClick() {
				main.click(action);
			}
		});
	}

	// =========================================================================================================================

	@Override
	protected void paintBorder(Graphics g, int margin, int border) {
		if (action == MainMenuAction.DATA_MANAGER) {
			g.setColor(Color.WHITE);
			drawCenteredRect(g, margin, 1);
			drawCenteredRect(g, margin + border - 1, 1);
			g.setColor(Color.DARK_GRAY);
			drawCenteredRect(g, margin + 1, border - 2);
		} else
			super.paintBorder(g, margin, border);
	}
}
