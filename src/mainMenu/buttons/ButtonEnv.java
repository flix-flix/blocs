package mainMenu.buttons;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import data.id.ItemID;
import data.id.ItemTableClient;
import data.map.Cube;
import data.map.buildings.Building;
import data.map.enumerations.Face;
import data.map.enumerations.Orientation;
import data.map.enumerations.Rotation;
import data.map.multiblocs.Tree;
import data.map.units.Unit;
import environment.Environment3D;
import environment.PanEnvironment;
import environment.extendsData.CubeClient;
import environment.extendsData.MapClient;
import environment.extendsEngine.DrawLayer;
import graphicEngine.calcul.Camera;
import graphicEngine.calcul.Engine;
import graphicEngine.calcul.Point3D;
import mainMenu.MainMenu;
import mainMenu.MainMenuAction;
import server.game.Player;

public class ButtonEnv extends PanEnvironment {
	private static final long serialVersionUID = -7013365965777831831L;

	private MainMenu main;
	private MainMenuAction action;

	// =============== Text ===============
	private String text;
	private Font font;
	private FontMetrics fm;

	// =========================================================================================================================

	public ButtonEnv(MainMenu main, Environment3D env, MainMenuAction action) {
		super(env);
		this.main = main;
		this.action = action;

		font = new Font("arial", Font.BOLD, action == MainMenuAction.PLAY ? 100 : 50);
		fm = getFontMetrics(font);

		refreshLang();

		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				main.click(action);
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
	}

	// =========================================================================================================================

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setFont(font);
		g.setColor(action == MainMenuAction.EDITOR ? Color.LIGHT_GRAY : Color.BLACK);
		g.drawString(text, getWidth() / 2 - fm.stringWidth(text) / 2, getHeight() - 25);

		g.setColor(Color.WHITE);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		g.drawRect(8, 8, getWidth() - 1 - 16, getHeight() - 1 - 16);
		g.setColor(Color.DARK_GRAY);
		for (int i = 1; i < 8; i++)
			g.drawRect(i, i, getWidth() - 1 - 2 * i, getHeight() - 1 - 2 * i);
	}

	@Override
	public void repaint() {
		super.repaint();
		if (main != null)
			main.repaint();
	}

	// =========================================================================================================================

	public void stop() {
		env.stop();
	}

	public void setPaused(boolean paused) {
		env.setPaused(paused);
	}

	// =========================================================================================================================

	/**
	 * Call setLocation() to make the center of this panel at the given coordinates
	 */
	public void setCenter(int x, int y) {
		setLocation(x - getWidth() / 2, y - getHeight() / 2);
	}

	// =========================================================================================================================

	public void refreshLang() {
		text = ItemTableClient.getText("main_menu.buttons." + action.name().toLowerCase());
	}

	// =========================================================================================================================

	public static ButtonEnv generateButton(MainMenu main, MainMenuAction action) {
		Env env;
		if (action == MainMenuAction.PLAY)
			env = generateEnvPlay(main);
		else if (action == MainMenuAction.SERVER)
			env = generateEnvServer(main);
		else
			env = generateEnvEditor(main);

		env.FPSmax = 1;

		return env.getPanel();
	}

	// =========================================================================================================================

	private static Env generateEnvPlay(MainMenu main) {
		MapClient map = new MapClient();
		Player felix = new Player("");

		int ground = 2;

		// ========== Ground and Borders ==========
		for (int x = 0; x < 45; x++)
			for (int z = 0; z < 25; z++)
				if (x % 99 == 0 || z % 99 == 0)
					for (int y = 0; y <= ground + 3; y++)
						map.add(new Cube(x, y, z, ItemID.BORDER));
				else {
					for (int y = 0; y < ground - 1; y++)
						map.add(new Cube(x, y, z, ItemID.DIRT));
					map.add(new Cube(x, ground - 1, z, ItemID.GRASS));
				}

		// ========== Forest ==========
		for (int x = 1; x < 30; x++)
			for (int z = 1; z < 30; z++)
				if (x / 2 + z < 15 || x + z / 2 < 15)
					map.set(new Cube(x, ground - 1, z, ItemID.DIRT));

		map.add(new Tree(10, ground, 5).getCube());
		map.add(new Tree(14, ground, 5).getCube());
		map.add(new Tree(18, ground, 4).getCube());

		map.add(new Tree(20, ground, 10).getCube());
		map.add(new Tree(25, ground, 10).getCube());

		// =========================================================================================================================
		// Units

		// Dig
		for (int x = 10; x < 20; x++)
			for (int z = 6; z < 15; z++) {
				map.remove(x, ground - 1, z);
				map.add(new Cube(x, ground - 2, z, ItemID.DIRT));
			}

		// Add Units
		map.addUnit(new Unit(ItemID.UNIT, felix, 27, ground, 11));
		map.addUnit(new Unit(ItemID.UNIT, felix, 25, ground, 13));
		map.addUnit(new Unit(ItemID.UNIT, felix, 23, ground, 11));
		map.addUnit(new Unit(ItemID.UNIT, felix, 21, ground, 12));

		CubeClient c1, c2, c3, c4;

		c1 = map.gridGet(27, ground, 11);
		c2 = map.gridGet(25, ground, 13);
		c3 = map.gridGet(23, ground, 11);
		c4 = map.gridGet(21, ground, 12);

		c1.unit.orientation = Orientation.EAST;
		c2.unit.orientation = Orientation.EAST;
		c3.unit.orientation = Orientation.EAST;
		c4.unit.orientation = Orientation.NORTH;

		c1.unit.rotation = Rotation.UPSIDE_DOWN_X;
		c2.unit.rotation = Rotation.RIGHT;
		c4.unit.rotation = Rotation.RIGHT;

		c1.updateFromUnit();
		c2.updateFromUnit();
		c3.updateFromUnit();
		c4.updateFromUnit();

		// =========================================================================================================================
		// Buildings

		map.addBuilding(new Building(felix, ItemID.CASTLE, 26, ground, 8, true));

		return new Env(main, map, new Camera(new Point3D(28, ground + 3.1, 16), 238, -30), MainMenuAction.PLAY);
	}

	// =========================================================================================================================

	private static Env generateEnvServer(MainMenu main) {
		MapClient map = new MapClient();

		Player felix = new Player("");

		int ground = 2;

		// ========== Ground and Borders ==========
		for (int x = 0; x < 45; x++)
			for (int z = 0; z < 25; z++)
				if (x % 99 == 0 || z % 99 == 0)
					for (int y = 0; y <= ground + 3; y++)
						map.add(new Cube(x, y, z, ItemID.BORDER));
				else {
					for (int y = 0; y < ground - 1; y++)
						map.add(new Cube(x, y, z, ItemID.DIRT));
					map.add(new Cube(x, ground - 1, z, ItemID.GRASS));
				}

		// ========== Forest ==========
		for (int x = 1; x < 30; x++)
			for (int z = 1; z < 30; z++)
				if (x / 2 + z < 15 || x + z / 2 < 15)
					map.set(new Cube(x, ground - 1, z, ItemID.DIRT));

		map.add(new Tree(10, ground, 5).getCube());
		map.add(new Tree(14, ground, 5).getCube());
		map.add(new Tree(18, ground, 4).getCube());
		map.add(new Tree(20, ground, 10).getCube());
		map.add(new Tree(25, ground, 10).getCube());

		map.add(new Tree(20, ground, 3).getCube());

		map.add(new Tree(24, ground, 9).getCube());

		// =========================================================================================================================
		// Units

		// Add Units
		map.addUnit(new Unit(ItemID.UNIT, felix, 21, ground, 11));
		map.addUnit(new Unit(ItemID.UNIT, felix, 21, ground, 12));
		map.addUnit(new Unit(ItemID.UNIT, felix, 21, ground, 13));

		map.addUnit(new Unit(ItemID.UNIT, felix, 23, ground, 10));

		CubeClient c = map.gridGet(23, ground, 10);

		c.unit.orientation = Orientation.EAST;
		c.updateFromUnit();

		return new Env(main, map, new Camera(new Point3D(23.5, ground + 2, 17.5), 260, -20), MainMenuAction.SERVER);
	}

	// =========================================================================================================================

	private static Env generateEnvEditor(MainMenu main) {
		MapClient map = new MapClient();

		map.add(new Cube(0, 0, 0, ItemID.BORDER));

		for (Face face : new Face[] { Face.SOUTH, Face.UP, Face.EAST }) {
			DrawLayer layer = new DrawLayer(map.gridGet(0, 0, 0), face);
			layer.drawGrid();
			map.gridGet(0, 0, 0).addLayer(layer);
		}

		return new Env(main, map, new Camera(new Point3D(-2, 1.5, 1.5), -22, -22), MainMenuAction.EDITOR);
	}
}

// =========================================================================================================================

class Env extends Environment3D {
	ButtonEnv panel;

	public Env(MainMenu main, MapClient map, Camera camera, MainMenuAction action) {
		super(map, camera);

		if (action == MainMenuAction.EDITOR)
			engine.setBackground(Engine.FILL);

		panel = new ButtonEnv(main, this, action);
		super.panel = panel;

		start();
	}

	@Override
	public ButtonEnv getPanel() {
		return panel;
	}
}
