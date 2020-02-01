package game.panels.menus.infos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.lang.Thread.State;

import data.map.Cube;
import data.map.enumerations.Orientation;
import data.map.units.Unit;
import environment.extendsData.CubeClient;
import game.Game;
import game.panels.menus.MenuButtonAction;
import game.panels.menus.MenuResource;
import graphicEngine.calcul.Camera;
import graphicEngine.calcul.Engine;
import graphicEngine.calcul.Point3D;
import server.send.Action;
import utils.panels.ButtonContainer;
import utils.panels.Menu;

public class MenuInfoUnit extends Menu implements ButtonContainer {
	private static final long serialVersionUID = -5061597857247176796L;

	private Thread update;

	private Font font = new Font("monospace", Font.PLAIN, 12);

	// =============== Display ===============
	private Engine engine;
	private Image img;

	// =============== Data ===============
	private Unit unit;

	// =============== Panels ===============
	private MenuResource res;
	private MenuButtonAction destroy, harvest;
	private MenuButtonAction[] buttons;

	// =========================================================================================================================

	public MenuInfoUnit(Game game) {
		super(game);

		update = new Thread(new Update());
		update.setName("Update Unit infos");
		update.start();

		res = new MenuResource();
		res.setSize(100, 40);
		res.setLocation(getWidth() / 2 - res.getWidth() / 2, 180);
		add(res);

		destroy = new MenuButtonAction(game, Action.UNIT_DESTROY, this);
		destroy.setBounds(280, 90, 75, 75);
		add(destroy);

		harvest = new MenuButtonAction(game, Action.UNIT_HARVEST, this);
		harvest.setBounds(190, 90, 75, 75);
		add(harvest);

		buttons = new MenuButtonAction[] { destroy, harvest };
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
		g.drawString(unit.getName(), img == null ? 15 : img.getWidth(null) + 15, 50);

		if (unit != null) {
			g.drawString(unit.toString(), img == null ? 15 : img.getWidth(null) + 15, 70);
		}
	}

	// =========================================================================================================================

	public void update(Unit unit) {
		this.unit = unit;

		if (update.getState() == State.WAITING)
			synchronized (update) {
				update.notify();
			}

		Cube cube = new Cube(unit.getItemID());
		cube.orientation = Orientation.WEST;

		engine = new Engine(new Camera(new Point3D(-.4, 1.5, -1), 58, -35), new CubeClient(cube, game.texturePack));
		engine.setBackground(Engine.NONE);
		img = engine.getImage(75, 75);

		_update();
	}

	private void _update() {
		if (unit != null)
			res.update(unit.getResource());

		destroy.setVisible(unit.getPlayer().equals(game.player));

		setVisible(true);
		repaint();
	}

	public void clear() {
		unit = null;
		setVisible(false);
		releaseButtons();
	}

	// =========================================================================================================================

	@Override
	public void resize() {
		res.setLocation(getWidth() / 2 - res.getWidth() / 2, 180);
	}

	@Override
	public void click(MouseEvent e) {
	}

	// =========================================================================================================================

	private class Update implements Runnable {
		@Override
		public void run() {
			while (true) {
				if (unit == null)
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
