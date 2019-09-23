package client.window.panels.menus;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
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
	private FontMetrics fm = getFontMetrics(font);

	public MenuSelectUnit(Session session) {
		super(session);

		goTo = new MenuAction(session, Action.GOTO);

		goTo.setBounds(15, 90, 75, 75);
		add(goTo);

		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				if (engine != null)
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

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(5, 5, getWidth() - 10, getHeight() - 10);

		if (img != null)
			g.drawImage(img, 15, 15, null);

		g.setFont(font);
		g.setColor(Color.WHITE);
		g.drawString("Unit", img == null ? 15 : img.getWidth(null) + 15, 50);
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
	public void click() {

	}
}
