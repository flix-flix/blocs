package mainMenu.buttons;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;

import mainMenu.MainMenu;
import mainMenu.MainMenu.ButtonAction;
import utils.FlixBlocksUtils;
import utils.panels.Menu;

public class ButtonQuit extends Menu {
	private static final long serialVersionUID = 7756689488731481855L;

	private MainMenu main;

	private int border = 0;
	private static Image quit;

	static {
		quit = FlixBlocksUtils.getImage("static/quit");
	}

	// =========================================================================================================================

	public ButtonQuit(MainMenu main) {
		this.main = main;
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

		g.drawImage(quit, border, border, getWidth() - 2 * border - 1, getHeight() - 2 * border - 1, null);
	}

	// =========================================================================================================================
	// Menu

	@Override
	public void click(MouseEvent e) {
		main.click(ButtonAction.QUIT);
	}

	@Override
	public void resize() {
	}
}
