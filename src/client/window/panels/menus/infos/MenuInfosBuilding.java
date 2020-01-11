package client.window.panels.menus.infos;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.lang.Thread.State;

import client.session.Session;
import client.window.graphicEngine.calcul.Camera;
import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.calcul.Point3D;
import client.window.graphicEngine.extended.ModelMap;
import client.window.panels.menus.ButtonContainer;
import client.window.panels.menus.Menu;
import client.window.panels.menus.MenuButtonAction;
import client.window.panels.menus.MenuGrid;
import client.window.panels.menus.MenuResource;
import data.id.ItemID;
import data.id.ItemTable;
import data.map.buildings.Building;
import data.map.resources.ResourceType;
import server.send.Action;

public class MenuInfosBuilding extends Menu implements ButtonContainer {
	private static final long serialVersionUID = -5061597857247176796L;

	private Font font = new Font("monospace", Font.PLAIN, 12);
	private Font fontBold = new Font("monospace", Font.BOLD, 20);
	private FontMetrics fmBold = getFontMetrics(fontBold);

	private int imgSize = 125;

	private Thread update;

	private Engine engine;
	private Image img;

	// =========================================================================================================================

	private MenuButtonAction spawn, upgrade;
	private MenuButtonAction[] buttons;
	private MenuGrid stocks;

	// =========================================================================================================================

	private Building build;

	// =========================================================================================================================

	public MenuInfosBuilding(Session session) {
		super(session);

		stocks = new MenuGrid(session);
		stocks.setCols(3);
		stocks.setRowHeight(50);
		stocks.setSize(getWidth(), 50);
		stocks.setLocation(0, getHeight() - stocks.getHeight());
		add(stocks);

		spawn = new MenuButtonAction(session, Action.BUILDING_SPAWN, this);
		spawn.setBounds(15, imgSize + 20, 75, 75);
		add(spawn);

		upgrade = new MenuButtonAction(session, Action.BUILDING_RESEARCH, this);
		upgrade.setBounds(105, imgSize + 20, 75, 75);
		add(upgrade);

		buttons = new MenuButtonAction[] { spawn, upgrade };

		update = new Thread(new Update());
		update.setName("Update Building infos");
		update.start();
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(15, 15, imgSize, imgSize);

		if (img != null)
			g.drawImage(img, 15, 15, null);

		g.setFont(font);
		g.setColor(Color.WHITE);
		g.drawString("Building", 20 + imgSize, 50);

		if (build != null) {
			g.drawString(build.toString(), 20 + imgSize, 70);

			if (!build.isBuild()) {
				g.setColor(Color.RED);
				g.setFont(fontBold);
				g.drawString("En construction", getWidth() / 2 - fmBold.stringWidth("En construction") / 2,
						imgSize + 40);

				int progressWidth = getWidth() / 2;
				int progressHeight = 30;
				int progressY = imgSize + 40 + 10;
				int padding = 5;

				g.setColor(Color.LIGHT_GRAY);
				g.fillRoundRect(getWidth() / 2 - progressWidth / 2 - padding, progressY, progressWidth + 2 * padding,
						progressHeight + 2 * padding, 20, 20);

				g.setColor(new Color(32, 143, 236));
				g.fillRoundRect(getWidth() / 2 - progressWidth / 2, progressY + padding,
						(int) (progressWidth
								* (build.getAlreadyBuild() / (double) ItemTable.getBuildingTime(build.getItemID()))),
						progressHeight, 20, 20);
				return;
			}
		}
	}

	// =========================================================================================================================

	public void update(Building build) {
		this.build = build;
		session.fen.gui.build = build;

		if (update.getState() == State.WAITING)
			synchronized (update) {
				update.notify();
			}

		ModelMap map = new ModelMap();
		map.add(new Building(null, ItemID.CASTLE, 0, 0, 0, true).getCube());

		engine = new Engine(new Camera(new Point3D(3.7, 3, 4.2), 236, -30), map, session.texturePack);
		engine.drawSky = false;
		img = engine.getImage(imgSize, imgSize);

		stocks.clear();

		for (ResourceType type : ResourceType.values())
			if (build.hasStock(type)) {
				MenuResource r = new MenuResource();
				r.update(build.getStocks(type));
				stocks.addItem(r);
			}

		_update();
	}

	private void _update() {
		spawn.setVisible(build.isBuild() && build.getPlayer().equals(session.player));
		upgrade.setVisible(build.isBuild() && build.getPlayer().equals(session.player));

		setVisible(true);
		repaint();
	}

	public void clear() {
		build = null;
		setVisible(false);
		releaseButtons();
	}

	// =========================================================================================================================
	// Menu

	@Override
	public void resize() {
		if (engine != null)
			img = engine.getImage(getWidth(), getHeight());

		stocks.setSize(getWidth(), 50);
		stocks.setLocation(0, getHeight() - stocks.getHeight());
	}

	// =========================================================================================================================

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		resize();
	}

	// =========================================================================================================================

	private class Update implements Runnable {
		@Override
		public void run() {
			while (true) {
				if (build == null)
					try {
						synchronized (update) {
							update.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				_update();

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// =========================================================================================================================

	@Override
	public void releaseButtons() {
		for (MenuButtonAction b : buttons)
			b.selected = false;
	}
}
