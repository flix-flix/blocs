package game.panels.menus.infos;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.lang.Thread.State;

import data.id.ItemTable;
import data.id.ItemTableClient;
import data.map.buildings.Building;
import data.map.resources.ResourceType;
import environment.extendsData.MapClient;
import game.Game;
import game.panels.menus.ButtonGameAction;
import game.panels.menus.MenuResource;
import graphicEngine.calcul.Engine;
import server.send.Action;
import utils.panels.FPanel;
import utils.panels.PanGrid;

public class PanInfosBuilding extends FPanel {
	private static final long serialVersionUID = -5061597857247176796L;

	private Game game;

	// =============== Font ===============
	private Font font = new Font("monospace", Font.PLAIN, 12);
	private Font fontBold = new Font("monospace", Font.BOLD, 20);
	private FontMetrics fmBold = getFontMetrics(fontBold);

	private Thread update;

	// =============== Display ===============
	private Engine engine;
	private Image img;
	private int imgSize = 125;
	private String constructionText;

	// =============== Panels ===============
	private ButtonGameAction spawn, upgrade;
	private PanGrid stocks;

	// =============== Data ===============
	private Building build;

	// =========================================================================================================================

	public PanInfosBuilding(Game game) {
		this.game = game;

		stocks = new PanGrid();
		stocks.setCols(3);
		stocks.setRowHeight(50);
		stocks.setSize(getWidth(), 50);
		stocks.setLocation(0, getHeight() - stocks.getHeight());
		add(stocks);

		spawn = new ButtonGameAction(game, Action.BUILDING_SPAWN);
		spawn.setBounds(15, imgSize + 20, 75, 75);
		add(spawn);

		upgrade = new ButtonGameAction(game, Action.BUILDING_RESEARCH);
		upgrade.setBounds(105, imgSize + 20, 75, 75);
		add(upgrade);

		ButtonGameAction.group(spawn, upgrade);

		update = new Thread(new Update());
		update.setName("Update Building infos");
		update.start();

		refreshLang();
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
		g.drawString(build.getName(), 20 + imgSize, 50);

		if (build != null) {
			g.drawString(build.toString(), 20 + imgSize, 70);

			if (!build.isBuild()) {
				g.setColor(Color.RED);
				g.setFont(fontBold);
				g.drawString(constructionText, getWidth() / 2 - fmBold.stringWidth(constructionText) / 2, imgSize + 40);

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

		if (update.getState() == State.WAITING)
			synchronized (update) {
				update.notify();
			}

		MapClient map = new MapClient();
		map.add(new Building(null, build.getItemID(), 0, 0, 0, true).getCube());

		engine = new Engine(ItemTableClient.getCamera(build.getItemID()), map);
		engine.setBackground(Engine.NONE);
		img = engine.getImage(imgSize, imgSize);

		stocks.clear();

		for (ResourceType type : ResourceType.values())
			if (build.hasStock(type)) {
				MenuResource r = new MenuResource();
				r.update(build.getStocks(type));
				stocks.addMenu(r);
			}

		_update();
	}

	private void _update() {
		spawn.setVisible(build.isBuild() && build.getPlayer().equals(game.player));
		upgrade.setVisible(build.isBuild() && build.getPlayer().equals(game.player));

		setVisible(true);
		repaint();
	}

	public void clear() {
		build = null;
		setVisible(false);
		spawn.unselectAll();
	}

	// =========================================================================================================================

	public void refreshLang() {
		constructionText = ItemTableClient.getText("game.infos.build.construction");
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
}
