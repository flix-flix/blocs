package game.panels.menus.infos;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

import data.id.ItemTable;
import data.id.ItemTableClient;
import data.map.Cube;
import data.map.buildings.Building;
import data.map.resources.ResourceType;
import environment.extendsData.MapClient;
import game.Game;
import game.panels.menus.ButtonGameAction;
import game.panels.menus.PanResource;
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

	// =============== Display ===============
	private Engine engine;
	private Image img;
	private int imgSize = 125;
	private String constructionText;

	// =============== Panels ===============
	private ButtonGameAction spawn, upgrade;
	private PanGrid stocks;

	// =============== Data ===============
	private Cube cube;
	private Building build;

	// =========================================================================================================================

	public PanInfosBuilding(Game game) {
		this.game = game;

		setBackground(Color.GRAY);

		stocks = new PanGrid();
		stocks.setBackground(Color.GRAY);
		stocks.setCols(3);
		stocks.setRowHeight(50);
		stocks.setSize(getWidth(), 50);
		stocks.setLocation(0, getHeight() - stocks.getHeight());
		add(stocks);

		spawn = new ButtonGameAction(game, Action.BUILDING_SPAWN);
		spawn.setBounds(15, imgSize + 20, 75, 75);
		spawn.setSelectable(false);
		add(spawn);

		upgrade = new ButtonGameAction(game, Action.BUILDING_RESEARCH);
		upgrade.setBounds(105, imgSize + 20, 75, 75);
		upgrade.setSelectable(false);
		add(upgrade);

		ButtonGameAction.group(spawn, upgrade);

		refreshLang();
	}

	// =========================================================================================================================

	@Override
	protected void paintCenter(Graphics g) {
		super.paintCenter(g);

		// Image
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(15, 15, imgSize, imgSize);

		if (img != null)
			g.drawImage(img, 15, 15, null);

		// Name
		g.setFont(font);
		g.setColor(Color.WHITE);
		g.drawString(build.getName(), 20 + imgSize, 50);

		if (build != null) {
			g.drawString(build.toString(), 20 + imgSize, 70);

			// Display construction progress
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

	public void update() {
		build = cube == null ? null : cube.build;

		// ===== Buttons =====
		boolean visible = build.isBuild() && build.getGamer().equals(game.gamer);
		spawn.setVisible(visible);
		upgrade.setVisible(visible);

		int index = 0;
		for (ResourceType type : ResourceType.values())
			if (build.hasStock(type)) {
				PanResource res = (PanResource) stocks.get(index++);
				res.update(build.getStocks(type));
			}

		repaint();
	}

	public void setCube(Cube cube) {
		this.cube = cube;
		build = cube.build;

		// Buttons
		spawn.unselectAll();

		// ===== Image =====
		MapClient map = new MapClient();
		map.add(new Building(null, build.getItemID(), 0, 0, 0, true).getCube());

		engine = new Engine(ItemTableClient.getCamera(build.getItemID()), map);
		engine.setBackground(Engine.NONE);
		img = engine.getImage(imgSize, imgSize);

		// ===== Resource =====
		// TODO [Fix] Cause indexException in update (stocks.get())
		stocks.clear();

		for (ResourceType type : ResourceType.values())
			if (build.hasStock(type)) {
				PanResource res = new PanResource();
				res.setColor(Color.GRAY, Color.WHITE, 3, Color.LIGHT_GRAY);
				res.setMargin(5);
				res.update(build.getStocks(type));
				stocks.gridAdd(res);
			}

		update();
	}

	public void clear() {
		build = null;
	}

	// =========================================================================================================================

	public void refreshLang() {
		constructionText = ItemTableClient.getText("game.infos.build.construction");
	}

	// =========================================================================================================================
	// FPanel

	@Override
	public void resize() {
		stocks.setSize(getWidth(), 50);
		stocks.setBottomLeftCorner(0, getHeight());
	}
}
