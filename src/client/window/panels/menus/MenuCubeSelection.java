package client.window.panels.menus;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;

import client.session.Session;
import client.window.graphicEngine.calcul.Camera;
import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.calcul.Point3D;
import client.window.graphicEngine.extended.ModelCube;
import data.map.Cube;

public class MenuCubeSelection extends Menu {
	private static final long serialVersionUID = -8393842761922506846L;

	public Cube cube;

	public boolean selected = false;

	public Engine engine;
	FontMetrics fm;
	Image img;

	public MenuCubeSelection(Session session, Cube cube) {
		super(session);
		this.cube = cube;

		engine = new Engine(new Camera(new Point3D(-.4, 1.5, -1), 58, -35), new ModelCube(cube), session.texturePack);
		engine.drawSky = false;

		Font font = new Font("monospace", Font.PLAIN, 12);
		fm = getFontMetrics(font);

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

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(selected ? Color.LIGHT_GRAY : Color.GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (img != null)
			g.drawImage(img, 0, 0, getWidth(), getHeight(), null);

		if (selected) {
			g.setColor(Color.GRAY);
			g.drawRect(0, 0, getWidth(), getHeight());
			g.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
			g.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
		}

		String str = cube.multibloc == null ? cube.itemID.name()
				: cube.multibloc.getClass().getName()
						.substring(cube.multibloc.getClass().getName().lastIndexOf(".") + 1);

		Rectangle2D r = fm.getStringBounds(str, g);
		int x = (getWidth() - (int) r.getWidth()) / 2;
		int y = getHeight() - 5;

		g.setColor(new Color(75, 75, 75));
		g.fillRect(x, y - (int) r.getHeight() + 3, (int) r.getWidth(), (int) r.getHeight());
		g.setColor(Color.WHITE);
		g.drawString(str, x, y);
	}

	@Override
	public void click() {
		session.setNextCube(cube);
		session.fen.gui.resetCubeSelection();
		selected = true;
	}
}
