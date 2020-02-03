package mainMenu.buttons;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;

import mainMenu.MainMenu;
import mainMenu.MainMenu.MainMenuAction;
import utils.FlixBlocksUtils;
import utils.panels.Menu;

public class ButtonAction extends Menu {
	private static final long serialVersionUID = 7756689488731481855L;

	private MainMenu main;
	private MainMenuAction action;

	private Image img;

	private int border = 0;
	private static Image options, quit;

	static {
		quit = FlixBlocksUtils.getImage("static/quit");
		options = FlixBlocksUtils.getImage("static/options");
	}

	// =========================================================================================================================

	public ButtonAction(MainMenu main, MainMenuAction action) {
		this.main = main;
		this.action = action;

		img = action == MainMenuAction.QUIT ? quit : options;
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

		g.drawImage(img, border, border, getWidth() - 2 * border - 1, getHeight() - 2 * border - 1, null);
	}

	// =========================================================================================================================
	// Menu

	@Override
	public void click(MouseEvent e) {
		main.click(action);
	}

	@Override
	public void resize() {
	}
}
