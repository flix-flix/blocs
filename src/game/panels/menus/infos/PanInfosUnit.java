package game.panels.menus.infos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import data.id.ItemTableClient;
import data.map.Cube;
import data.map.units.Unit;
import environment.extendsData.CubeClient;
import game.Game;
import game.panels.menus.ButtonGameAction;
import game.panels.menus.PanResource;
import graphicEngine.calcul.Engine;
import server.send.Action;
import utils.panels.FPanel;

public class PanInfosUnit extends FPanel {
	private static final long serialVersionUID = -5061597857247176796L;

	private Game game;

	private Font font = new Font("monospace", Font.PLAIN, 12);

	// =============== Display ===============
	private Engine engine;
	private Image img;

	// =============== Data ===============
	private Cube cube;
	private Unit unit;

	// =============== Panels ===============
	private PanResource res;
	private ButtonGameAction destroy, harvest;

	// =========================================================================================================================

	public PanInfosUnit(Game game) {
		this.game = game;

		setBackground(Color.GRAY);

		res = new PanResource();
		res.setColor(Color.LIGHT_GRAY, Color.DARK_GRAY, 2, Color.DARK_GRAY);
		res.setSize(100, 40);
		res.setLocation(getWidth() / 2 - res.getWidth() / 2, 180);
		add(res);

		destroy = new ButtonGameAction(game, Action.UNIT_DESTROY);
		destroy.setBounds(280, 90, 75, 75);
		destroy.setSelectable(false);
		add(destroy);

		harvest = new ButtonGameAction(game, Action.UNIT_HARVEST);
		harvest.setBounds(190, 90, 75, 75);
		harvest.setSelectable(false);
		add(harvest);

		ButtonGameAction.group(destroy, harvest);
	}

	// =========================================================================================================================

	@Override
	protected void paintCenter(Graphics g) {
		super.paintCenter(g);

		if (img != null)
			g.drawImage(img, 15, 15, null);

		if (unit == null)
			return;

		g.setFont(font);
		g.setColor(Color.WHITE);

		g.drawString(unit.getName(), img == null ? 15 : img.getWidth(null) + 15, 50);
		g.drawString(unit.toString(), img == null ? 15 : img.getWidth(null) + 15, 70);
	}

	// =========================================================================================================================

	public void clear() {
		unit = null;
	}

	// =========================================================================================================================

	public void update() {
		unit = cube.unit;

		// Resources
		res.update(unit.getResource());
		res.setVisible(!res.isEmpty());

		repaint();
	}

	public void setCube(Cube cube) {
		this.cube = cube;
		unit = cube == null ? null : cube.unit;

		// ===== Image =====
		engine = new Engine(ItemTableClient.getCamera(unit.getItemID()), new CubeClient(new Cube(unit.getItemID())));
		engine.setBackground(Engine.NONE);
		img = engine.getImage(75, 75);

		// ===== Buttons =====
		destroy.unselectAll();
		boolean visible = unit.getGamer().equals(game.gamer);
		destroy.setVisible(visible);
		harvest.setVisible(visible);
	}

	// =========================================================================================================================

	@Override
	public void resize() {
		res.setLocation(getWidth() / 2 - res.getWidth() / 2, 180);
	}
}
