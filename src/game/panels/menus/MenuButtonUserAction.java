package game.panels.menus;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;

import data.id.ItemID;
import data.map.Cube;
import environment.extendsData.CubeClient;
import game.Game;
import game.UserAction;
import graphicEngine.calcul.Camera;
import graphicEngine.calcul.Engine;
import graphicEngine.calcul.Point3D;
import utils.FlixBlocksUtils;
import utils.panels.Menu;

public class MenuButtonUserAction extends Menu {
	private static final long serialVersionUID = -2696383944798968722L;

	public UserAction action;

	Engine engine;
	Image img;

	public boolean selected;

	public MenuButtonUserAction(Game game, UserAction action) {
		super(game);
		this.action = action;

		img = FlixBlocksUtils.getImage(game.texturePack.getFolder() + "menu/" + action.name().toLowerCase());

		if (action == UserAction.CREA_ADD) {
			engine = new Engine(new Camera(new Point3D(-.4, 1.5, -1), 58, -35),
					new CubeClient(new Cube(ItemID.GRASS), game.texturePack));
			engine.setBackground(Engine.NONE);
		}
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
		game.setAction(action);
	}
}