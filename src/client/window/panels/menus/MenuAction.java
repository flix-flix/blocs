package client.window.panels.menus;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import client.session.Action;
import client.session.Session;
import client.window.graphicEngine.calcul.Camera;
import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.calcul.Point3D;
import client.window.graphicEngine.extended.ModelCube;
import data.enumeration.ItemID;
import data.map.Cube;
import utils.FlixBlocksUtils;

public class MenuAction extends Menu {
	private static final long serialVersionUID = -2696383944798968722L;

	public Action action;

	Image img;

	public boolean selected;

	public MenuAction(Session session, Action action) {
		super(session);
		this.action = action;

		img = FlixBlocksUtils.getImage("menu/" + action.name().toLowerCase());

		selected = session.action == action;

		if (action == Action.CUBES) {
			Engine engine = new Engine(new Camera(new Point3D(-.4, 1.5, -1), 58, -35),
					new ModelCube(new Cube(ItemID.GRASS)), session.texturePack);
			engine.drawSky = false;
			addComponentListener(new ComponentListener() {
				@Override
				public void componentShown(ComponentEvent e) {
				}

				@Override
				public void componentResized(ComponentEvent e) {
					img = engine.getImage(getWidth(), getHeight());
				}

				@Override
				public void componentMoved(ComponentEvent e) {
				}

				@Override
				public void componentHidden(ComponentEvent e) {
				}
			});
		}
	}

	// =========================================================================================================================

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(selected ? Color.LIGHT_GRAY : Color.GRAY);

		g.fillRect(0, 0, getWidth(), getHeight());

		if (selected) {
			g.setColor(Color.GRAY);
			g.drawRect(0, 0, getWidth(), getHeight());
			g.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
			g.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
		}

		g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
	}

	// =========================================================================================================================

	@Override
	public void click() {
		session.setAction(action);
		session.fen.gui.hideMenu();
		selected = true;
	}
}
