package client.window.panels.menus.select;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import client.session.Action;
import client.session.Session;
import client.window.graphicEngine.calcul.Camera;
import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.calcul.Point3D;
import client.window.graphicEngine.extended.ModelCube;
import client.window.panels.menus.Menu;
import client.window.panels.menus.MenuButtonAction;
import data.enumeration.ItemID;
import data.enumeration.Orientation;
import data.map.Cube;
import data.map.units.Unit;

public class MenuInfoUnit extends Menu {
	private static final long serialVersionUID = -5061597857247176796L;

	private Engine engine;
	private Image img;

	private Unit unit;

	private MenuButtonAction goTo, mine, build, destroy;

	private Font font = new Font("monospace", Font.PLAIN, 12);

	public MenuInfoUnit(Session session) {
		super(session);

		goTo = new MenuButtonAction(session, Action.GOTO);
		goTo.setBounds(15, 90, 75, 75);
		add(goTo);

		mine = new MenuButtonAction(session, Action.MINE);
		mine.setBounds(105, 90, 75, 75);
		add(mine);

		build = new MenuButtonAction(session, Action.BUILD);
		build.setBounds(195, 90, 75, 75);
		add(build);

		destroy = new MenuButtonAction(session, Action.DESTROY);
		destroy.setBounds(280, 90, 75, 75);
		add(destroy);
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (img != null)
			g.drawImage(img, 15, 15, null);

		g.setFont(font);
		g.setColor(Color.WHITE);
		g.drawString("Unit", img == null ? 15 : img.getWidth(null) + 15, 50);

		if (unit != null)
			g.drawString(unit.toString(), img == null ? 15 : img.getWidth(null) + 15, 70);
	}

	// =========================================================================================================================

	public void update(Unit unit) {
		this.unit = unit;
		session.fen.gui.unit = unit;

		Cube cube = new Cube(ItemID.UNIT);
		cube.orientation = Orientation.WEST;

		engine = new Engine(new Camera(new Point3D(-.4, 1.5, -1), 58, -35), new ModelCube(cube), session.texturePack);
		engine.drawSky = false;
		img = engine.getImage(75, 75);

		setVisible(true);
		repaint();
	}

	// =========================================================================================================================

	@Override
	public void resize() {
		if (engine != null)
			img = engine.getImage(getWidth(), getHeight());
	}

	@Override
	public void click() {
	}
}
