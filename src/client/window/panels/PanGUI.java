package client.window.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

import client.messages.Message;
import client.session.Action;
import client.session.GameMode;
import client.session.Session;
import client.window.panels.menus.MenuButtonAction;
import client.window.panels.menus.MenuButtonCube;
import client.window.panels.menus.MenuCol;
import client.window.panels.menus.MenuGrid;
import client.window.panels.menus.MenuMap;
import client.window.panels.menus.MenuRessources;
import client.window.panels.menus.infos.MenuInfos;
import data.map.Cube;
import data.map.buildings.Building;
import data.map.units.Unit;

public class PanGUI extends JPanel {
	private static final long serialVersionUID = 3929655843006244723L;

	Session session;

	// Dialog font
	Font font = new Font("monospace", Font.BOLD, 18);

	// ======================= Graphics =========================

	private Graphics g;
	public int w, h, centerX, centerY;

	// Size of the central indicator (creative mode)
	int crossSize = 7;

	// ========================= Dialog =========================

	// Previous messages to display
	public Message[] messages = new Message[10];
	// Number of messages to display
	public int nbMsg = 0;
	// Width of the dialog background
	public int msgBackWidth = 1000;

	// Current line
	public String msgLine = new String();

	// Position of the cursor
	public int cursorPos = 0;
	// true : show the cursor (switch to make the cursor flashing)
	private boolean cursorState = true;
	// Store time since last cursor state switch
	private int cursorStateTime = 0;

	// ======================= Menu =========================
	int menuWidth = 400;

	MenuCol menu = new MenuCol(session);

	Action[] _actions = { Action.MOUSE, Action.CREA_ADD, Action.CREA_DESTROY };
	MenuButtonAction[] actions = new MenuButtonAction[_actions.length];

	MenuGrid gridActions;

	MenuMap map;
	MenuRessources ress;

	// ==== Select ====
	MenuInfos select;
	public Unit unit;
	public Building build;

	// =========================================================================================================================

	public PanGUI(Session s) {
		session = s;
		this.setOpaque(false);
		this.setLayout(null);

		// ========================================================================================

		menu.setBounds(0, 0, menuWidth, getHeight());
		this.add(menu);

		menu.addTop(gridActions = new MenuGrid(session), 100);

		for (int i = 0; i < _actions.length; i++)
			gridActions.addItem(actions[i] = new MenuButtonAction(session, _actions[i]));

		menu.addBottom(map = new MenuMap(session), MenuCol.WIDTH);
		menu.addBottom(ress = new MenuRessources(session), 130);

		menu.addTop(select = new MenuInfos(session), MenuCol.REMAINING);

		hideMenu();
	}

	// =========================================================================================================================

	public void paintComponent(Graphics gg) {
		this.setBounds(getParent().getBounds());
		w = this.getWidth();
		h = this.getHeight();
		centerX = w / 2;
		centerY = h / 2;

		g = gg;

		switch (session.stateGUI) {
		case GAME:
			if (session.gamemode == GameMode.CREATIVE) {
				// Middle indicator : cross
				g.setColor(Color.WHITE);
				g.drawLine(centerX - crossSize, centerY - 1, centerX + crossSize - 1, centerY - 1);
				g.drawLine(centerX - crossSize, centerY, centerX + crossSize - 1, centerY);
				g.drawLine(centerX - 1, centerY - crossSize, centerX - 1, centerY + crossSize - 1);
				g.drawLine(centerX, centerY - crossSize, centerX, centerY + crossSize - 1);
			}

			break;

		case DIALOG:
			int grayBack = 120;
			Color colorBack = new Color(grayBack, grayBack, grayBack, 200);
			Color colorMsg = Color.WHITE;

			int startW = 420;

			// TODO [Improve] If messages text too long split in several lines

			// =============== Message (Line) =================

			// Background
			g.setColor(colorBack);
			g.fillRect(startW, h - 100, msgBackWidth, 30);

			// Text
			g.setFont(font);
			g.setColor(colorMsg);

			// Flash the text-cursor
			cursorStateTime++;
			if (cursorStateTime > 3) {
				cursorState = !cursorState;
				cursorStateTime = 0;
			}

			if (cursorState)
				g.drawString(msgLine.substring(0, cursorPos) + "|" + msgLine.substring(cursorPos), startW + 5,
						h - 100 + 20);
			else
				g.drawString(msgLine.substring(0, cursorPos) + " " + msgLine.substring(cursorPos), startW + 5,
						h - 100 + 20);

			// =============== Messages (Previous) =================

			// Background
			g.setColor(colorBack);
			g.fillRect(startW, h - 100 - 10 - 30 * nbMsg, msgBackWidth, 30 * nbMsg);

			// Text
			g.setColor(colorMsg);
			for (int i = 0; i < nbMsg; i++)
				g.drawString(messages[i].toMessage(), startW + 5, h - 100 + 20 - 10 - (nbMsg - i) * 30);

			break;
		default:
			break;
		}

		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				menu.setBounds(0, 0, menuWidth, getHeight());
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
	}

	// =========================================================================================================================

	public void select(Cube cube) {
		select.updateCube(cube);
	}

	// =========================================================================================================================

	public void hideMenu() {
		menu.setVisible(session.gamemode == GameMode.CLASSIC);

		for (MenuButtonAction e : actions)
			e.selected = session.action == e.action;

		if (session.action == Action.CREA_ADD)
			select.showCubes();

		if (session.action == Action.MOUSE)
			select.updateCube(null);
	}

	public void resetCubeSelection() {
		for (MenuButtonCube e : select.cubes)
			e.selected = false;
	}

	public void updateTexturePack() {
		for (MenuButtonCube m : select.cubes)
			m.engine.texturePack = session.texturePack;
	}

	// =========================================================================================================================

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		menu.setBounds(x, y, menuWidth, height);
	}
}
