package client.window.panels.menus;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;

import client.session.Session;
import client.session.UserAction;
import client.window.graphicEngine.calcul.Camera;
import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.calcul.Point3D;
import client.window.graphicEngine.extended.ModelCube;
import data.id.ItemID;
import data.map.Cube;
import utils.FlixBlocksUtils;

public class MenuButtonUserAction extends Menu {
	private static final long serialVersionUID = -2696383944798968722L;

	public UserAction action;

	Engine engine;
	Image img;

	public boolean selected;

	public MenuButtonUserAction(Session session, UserAction action) {
		super(session);
		this.action = action;

		img = FlixBlocksUtils.getImage("menu/" + action.name().toLowerCase());

		if (action == UserAction.CREA_ADD) {
			engine = new Engine(new Camera(new Point3D(-.4, 1.5, -1), 58, -35), new ModelCube(new Cube(ItemID.GRASS)),
					session.texturePack);
			engine.background = Engine.NONE;
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
		session.fen.setAction(action);
	}
}