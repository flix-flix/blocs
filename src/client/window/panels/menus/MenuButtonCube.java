package client.window.panels.menus;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import client.session.Session;
import client.textures.TexturePack;
import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.extended.ModelCube;
import client.window.graphicEngine.extended.ModelMap;
import data.id.ItemTableClient;
import data.map.Cube;

public class MenuButtonCube extends Menu {
	private static final long serialVersionUID = -8393842761922506846L;

	public Cube cube;
	public ModelMap model;

	public boolean selected = false;

	public Engine engine;
	Font font = new Font("monospace", Font.PLAIN, 12);
	FontMetrics fm = getFontMetrics(font);
	Image img;

	String name;

	// =========================================================================================================================

	public MenuButtonCube(Session session, Cube cube) {
		super(session);
		this.cube = cube;
		this.model = new ModelMap(session.texturePack);
		model.add(new ModelCube(cube, session.texturePack));

		name = ItemTableClient.getName(cube.multibloc == null ? cube.getItemID() : cube.multibloc.itemID);
		if (name == null)
			name = "NULL";

		engine = new Engine(
				ItemTableClient.getCamera(cube.multibloc == null ? cube.getItemID() : cube.multibloc.itemID), model);
		engine.background = Engine.NONE;
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
		ModelCube cube = model.gridGet(0, 0, 0);
		if (cube.multibloc != null)
			for (Cube c : cube.multibloc.list)
				((ModelCube) c).setTexturePack(session.texturePack);
		else
			cube.setTexturePack(session.texturePack);
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
