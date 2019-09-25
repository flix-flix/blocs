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
import data.ItemTable;
import data.map.Cube;
import data.map.buildings.Building;

public class MenuInfosBuilding extends Menu {
	private static final long serialVersionUID = -5061597857247176796L;

	private Font font = new Font("monospace", Font.PLAIN, 12);
	private Font fontBold = new Font("monospace", Font.BOLD, 20);

	private int imgSize = 75;

	private Engine engine;
	private Image img;

	private Building build;

	private MenuButtonAction spawn, upgrade;

	public MenuInfosBuilding(Session session) {
		super(session);

		spawn = new MenuButtonAction(session, Action.SPAWN);
		spawn.setBounds(15, 90, 75, 75);
		add(spawn);

		upgrade = new MenuButtonAction(session, Action.UPGRADE);
		upgrade.setBounds(105, 90, 75, 75);
		add(upgrade);
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (img == null) {
			g.setColor(Color.DARK_GRAY);
			g.fillRect(15, 15, imgSize, imgSize);
		} else
			g.drawImage(img, 15, 15, null);

		g.setFont(font);
		g.setColor(Color.WHITE);
		g.drawString("Building", 15 + imgSize, 50);

		if (build != null) {
			g.drawString(build.toString(), 15 + imgSize, 70);

			if (!build.isBuild()) {
				g.setColor(Color.RED);
				g.setFont(fontBold);
				g.drawString("En construction", 50, 130);

				int buildProgressX = 100;
				int buildProgressY = 30;
				int padding = 5;

				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(getWidth() / 2 - buildProgressX / 2 - padding, 135, buildProgressX + 2 * padding,
						buildProgressY + 2 * padding);

				g.setColor(new Color(32, 143, 236));
				g.fillRect(getWidth() / 2 - buildProgressX / 2, 135 + padding,
						(int) (buildProgressX
								* (build.getAlreadyBuild() / (double) ItemTable.getBuildingTime(build.itemID))),
						buildProgressY);
				return;
			}

		}
	}

	// =========================================================================================================================

	public void update(Building build) {
		this.build = build;
		session.fen.gui.build = build;

		Cube cube = new Cube(0, 0, 0, 3, 2, 2, build.itemID);

		engine = new Engine(new Camera(new Point3D(-1, 3, -2.5), 58, -35), new ModelCube(cube), session.texturePack);
		engine.drawSky = false;
		img = engine.getImage(imgSize, imgSize);

		spawn.setVisible(build.isBuild());
		upgrade.setVisible(build.isBuild());

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
