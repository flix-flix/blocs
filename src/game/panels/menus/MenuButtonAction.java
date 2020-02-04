package game.panels.menus;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;

import game.Game;
import graphicEngine.calcul.Engine;
import server.send.Action;
import utils.FlixBlocksUtils;
import utils.panels.ButtonContainer;
import utils.panels.Menu;

public class MenuButtonAction extends Menu {
	private static final long serialVersionUID = -2696383944798968722L;

	public Action action;

	Engine engine;
	Image img;

	public boolean selected;

	private ButtonContainer container;

	// =========================================================================================================================

	public MenuButtonAction(Game game, Action action, ButtonContainer container) {
		super(game);
		this.action = action;
		this.container = container;

		img = FlixBlocksUtils.getImage(game.texturePack.getFolder() + "menu/" + action.name().toLowerCase());
	}

	// =========================================================================================================================

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(selected ? Color.LIGHT_GRAY : Color.GRAY);

		g.fillRect(0, 0, getWidth(), getHeight());

		if (selected) {
			g.setColor(Color.GRAY);
			for (int i = 0; i < 5; i++)
				g.drawRect(i, i, getWidth() - 1 - 2 * i, getHeight() - 1 - 2 * i);
		}

		g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
	}

	// =========================================================================================================================

	@Override
	public void resize() {
		if (engine != null)
			img = engine.getImage(getWidth(), getHeight());
	}

	@Override
	public void click(MouseEvent e) {
		container.releaseButtons();
		selected = true;
	}
}