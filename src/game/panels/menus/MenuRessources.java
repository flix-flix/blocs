package game.panels.menus;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;

import data.id.ItemID;
import data.map.Cube;
import data.map.enumerations.Orientation;
import data.map.resources.ResourceType;
import environment.extendsData.CubeClient;
import game.Game;
import graphicEngine.calcul.Camera;
import graphicEngine.calcul.Engine;
import graphicEngine.calcul.Point3D;
import utils.panels.Menu;

public class MenuRessources extends Menu {
	private static final long serialVersionUID = 7179773919376958365L;

	private Graphics g;

	Image units;
	Font font = new Font("monospace", Font.BOLD, 15);

	private int border = 5;
	private int padding = 5;

	private int imgSize = 40;
	private int textSpaceY = 30;

	// =========================================================================================================================

	public MenuRessources(Game game) {
		super(game);

		Cube unit = new Cube(ItemID.UNIT);
		unit.orientation = Orientation.WEST;
		Engine engine = new Engine(new Camera(new Point3D(-.4, 1.5, -1), 58, -35),
				new CubeClient(unit, game.texturePack));
		engine.setBackground(Engine.NONE);
		units = engine.getImage(imgSize, imgSize);
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		this.g = g;

		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(border, border, getWidth() - 1 - 2 * border, getHeight() - 1 - 2 * border);

		g.setColor(Color.WHITE);
		g.setFont(font);

		paintRess(0, 0, ResourceType.WOOD.getImage(), game.player.getWood());
		paintRess(1, 0, ResourceType.STONE.getImage(), game.player.getStone());
		paintRess(2, 0, ResourceType.WATER.getImage(), game.player.getWater());

		paintRess(0, 1, units, game.player.getWood());
		paintRess(1, 1, units, game.player.getStone());
		paintRess(2, 1, units, game.player.getNbUnits());
	}

	private void paintRess(int row, int col, Image img, int value) {
		int width = (getWidth() - 2 * border) / 3;
		int height = (getHeight() - 2 * border) / 2;

		g.setColor(Color.GRAY);
		g.fillRect(row * width + border + padding, col * height + border + padding, width - 2 * padding,
				imgSize + 2 * padding);

		g.drawImage(img, row * width + border + width / 2 - imgSize, col * height + border + padding * 2, imgSize,
				imgSize, null);

		g.setColor(Color.WHITE);
		g.drawString(": " + value, row * width + border + width / 2, col * height + border + padding + textSpaceY);
	}

	// =========================================================================================================================

	@Override
	public void click(MouseEvent e) {
	}
}