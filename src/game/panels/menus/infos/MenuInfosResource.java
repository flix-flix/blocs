package game.panels.menus.infos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.lang.Thread.State;

import data.map.Cube;
import data.map.resources.Resource;
import game.Game;
import utils.panels.Menu;

public class MenuInfosResource extends Menu {
	private static final long serialVersionUID = 8252009605405911305L;

	private Font font = new Font("monospace", Font.BOLD, 15);

	Thread update;

	private Cube cube;
	private Resource resource;

	// =========================================================================================================================

	public MenuInfosResource(Game game) {
		super(game);

		update = new Thread(new Update());
		update.setName("Update Ressource infos");
		update.start();
	}

	// =========================================================================================================================

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (resource != null) {
			g.drawImage(resource.getType().getImage(), 15, 15, null);

			g.setColor(Color.WHITE);
			g.setFont(font);
			g.drawString(": " + resource.getQuantity(), 75, 40);
		}
	}

	// =========================================================================================================================

	public void update(Cube cube) {
		this.cube = cube;
		resource = cube.getResource();

		if (update.getState() == State.WAITING)
			synchronized (update) {
				update.notify();
			}

		_update();
	}

	private void _update() {
		setVisible(!resource.isEmpty());

		if (resource.isEmpty())
			cube = null;
		else
			repaint();
	}

	public void clear() {
		cube = null;
		setVisible(false);
	}

	// =========================================================================================================================

	private class Update implements Runnable {
		@Override
		public void run() {
			while (true) {
				if (cube == null)
					try {
						synchronized (update) {
							update.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				update(cube);

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
