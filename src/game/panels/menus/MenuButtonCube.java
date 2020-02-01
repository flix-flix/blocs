package game.panels.menus;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import data.id.ItemTableClient;
import data.map.Cube;
import environment.extendsData.CubeClient;
import environment.extendsData.MapClient;
import environment.textures.TexturePack;
import game.Game;
import graphicEngine.calcul.Engine;
import utils.panels.Menu;

public class MenuButtonCube extends Menu {
	private static final long serialVersionUID = -8393842761922506846L;

	public Cube cube;
	public MapClient model;

	public boolean selected = false;

	public Engine engine;
	Font font = new Font("monospace", Font.PLAIN, 12);
	FontMetrics fm = getFontMetrics(font);
	Image img;

	String name;

	/** Store the linked buttons */
	private ArrayList<MenuButtonCube> group;

	// =========================================================================================================================

	public MenuButtonCube(Game game, Cube cube) {
		super(game);
		this.cube = cube;
		this.model = new MapClient(game.texturePack);
		model.add(new CubeClient(cube, game.texturePack));

		name = ItemTableClient.getName(cube.multibloc == null ? cube.getItemID() : cube.multibloc.itemID);
		if (name == null)
			name = "NULL";

		engine = new Engine(
				ItemTableClient.getCamera(cube.multibloc == null ? cube.getItemID() : cube.multibloc.itemID), model);
		engine.setBackground(Engine.NONE);
	}

	// =========================================================================================================================

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

		Rectangle2D r = fm.getStringBounds(name, g);
		int x = (getWidth() - (int) r.getWidth()) / 2;
		int y = getHeight() - 5;

		g.setColor(new Color(75, 75, 75));
		g.fillRect(x, y - (int) r.getHeight() + 3, (int) r.getWidth(), (int) r.getHeight());
		g.setFont(font);
		g.setColor(Color.WHITE);
		g.drawString(name, x, y);
	}

	// =========================================================================================================================

	public void updateTexturePack(TexturePack texturePack) {
		CubeClient cube = model.gridGet(0, 0, 0);
		if (cube.multibloc != null)
			for (Cube c : cube.multibloc.list)
				((CubeClient) c).setTexturePack(game.texturePack);
		else
			cube.setTexturePack(game.texturePack);
	}

	// =========================================================================================================================

	public static void group(ArrayList<MenuButtonCube> cubes) {
		for (MenuButtonCube button : cubes)
			button.group = cubes;
	}

	// =========================================================================================================================

	@Override
	public void resize() {
		img = engine.getImage(getWidth(), getHeight());
	}

	@Override
	public void click(MouseEvent e) {
		game.setNextCube(cube);

		for (MenuButtonCube button : group)
			button.selected = false;
		selected = true;
	}
}
