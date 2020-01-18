package client.window.panels.menus;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import client.session.Session;
import client.window.graphicEngine.calcul.Camera;
import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.calcul.Point3D;
import client.window.graphicEngine.extended.ModelCube;
import data.map.Cube;

public class MenuButtonCube extends Menu {
	private static final long serialVersionUID = -8393842761922506846L;

	public Cube cube;

	public boolean selected = false;

	public Engine engine;
	Font font = new Font("monospace", Font.PLAIN, 12);
	FontMetrics fm = getFontMetrics(font);
	Image img;

	// =========================================================================================================================

	public MenuButtonCube(Session session, Cube cube) {
		super(session);
		this.cube = cube;

		engine = new Engine(new Camera(new Point3D(-.4, 1.5, -1), 58, -35), new ModelCube(cube), session.texturePack);
		engine.background = Engine.NONE;
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(selected ? Color.LIGHT_GRAY : Color.GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (img != null)
			g.drawImage(img, 0, 0, getWidth(), getHeight(), null);

		if (selected) {
			g.setColor(Color.GRAY);
			for (int i = 0; i < 5; i++)
				g.drawRect(i, i, getWidth() - 1 - 2 * i, getHeight() - 1 - 2 * i);
		}

		String str = cube.multibloc == null ? cube.itemID.name()
				: cube.multibloc.getClass().getName()
						.substring(cube.multibloc.getClass().getName().lastIndexOf(".") + 1);

		Rectangle2D r = fm.getStringBounds(str, g);
		int x = (getWidth() - (int) r.getWidth()) / 2;
		int y = getHeight() - 5;

		g.setColor(new Color(75, 75, 75));
		g.fillRect(x, y - (int) r.getHeight() + 3, (int) r.getWidth(), (int) r.getHeight());
		g.setFont(font);
		g.setColor(Color.WHITE);
		g.drawString(str, x, y);
	}

	// =========================================================================================================================

	@Override
	public void resize() {
		img = engine.getImage(getWidth(), getHeight());
	}

	@Override
	public void click(MouseEvent e) {
		session.setNextCube(cube);
		session.fen.gui.resetCubeSelection();
		selected = true;
	}
}
