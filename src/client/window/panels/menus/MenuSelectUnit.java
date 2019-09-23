package client.window.panels.menus;

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
import data.enumeration.ItemID;
import data.enumeration.Orientation;
import data.map.Cube;
import data.units.Unit;

public class MenuSelectUnit extends Menu {
	private static final long serialVersionUID = -5061597857247176796L;

	private Engine engine;
	private Image img;

	private Unit unit;

	private MenuAction goTo;

	private Font font = new Font("monospace", Font.PLAIN, 12);

	public MenuSelectUnit(Session session) {
		super(session);

		goTo = new MenuAction(session, Action.GOTO);

		goTo.setBounds(15, 90, 75, 75);
		add(goTo);
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
