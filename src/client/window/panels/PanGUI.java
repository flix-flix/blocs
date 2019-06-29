package client.window.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import client.messages.Message;
import client.session.Action;
import client.session.GameMode;
import client.session.Session;
import client.window.panels.menus.MenuAction;
import client.window.panels.menus.MenuCol;
import client.window.panels.menus.MenuCubeSelection;
import client.window.panels.menus.MenuGrid;
import client.window.panels.menus.MenuSelectInfos;
import data.enumeration.ItemID;
import data.map.Cube;
import data.multiblocs.E;
import data.multiblocs.Tree;

public class PanGUI extends JPanel {
	private static final long serialVersionUID = 3929655843006244723L;

	Session session;

	// Dialog font
	Font font = new Font("monospace", Font.BOLD, 18);

	// ======================= Graphics =========================

	Graphics g;
	public int w, h, centerX, centerY;

	// Size of the central indicator (creative mode)
	int crossSize = 7;

	// ========================= Dialog =========================

	// Previous messages to display
	public Message[] messages = new Message[10];
	// Number of messages to display
	public int nbMsg = 0;
	// Width of the dialog background
	int msgBackWidth = 1000;

	// Current line
	public String msgLine = new String();

	// Position of the cursor
	public int cursorPos = 0;
	// true : show the cursor (switch to make the cursor flashing)
	boolean cursorState = true;
	// Store time since last cursor state switch
	int cursorStateTime = 0;

	// ======================= Menu =========================
	int menuWidth = 400;

	MenuCol menu = new MenuCol(session);

	Action[] _actions = { Action.MOUSE, Action.SELECT, Action.CUBES, Action.DESTROY };
	MenuAction[] actions = new MenuAction[4];

	ArrayList<Cube> _items = new ArrayList<>();
	ArrayList<MenuCubeSelection> cubes = new ArrayList<>();

	MenuGrid gridActions;
	MenuGrid gridCubes;

	public MenuSelectInfos selectInfos;

	// =========================================================================================================================

	public PanGUI(Session s) {
		session = s;
		this.setOpaque(false);
		this.setLayout(null);

		// ========================================================================================

		_items.add(new Cube(ItemID.BORDER));
		_items.add(new Cube(ItemID.GRASS));
		_items.add(new Cube(ItemID.DIRT));
		_items.add(new Cube(ItemID.OAK_TRUNK));
		_items.add(new Cube(ItemID.OAK_LEAVES));
		_items.add(new Cube(ItemID.OAK_BOARD));
		_items.add(new Cube(ItemID.STONE));
		_items.add(new Cube(ItemID.GLASS));
		_items.add(new Cube(ItemID.GLASS_GRAY));
		_items.add(new Cube(ItemID.GLASS_RED));
		_items.add(new Tree().getCube());
		_items.add(new E().getCube());

		// ========================================================================================

		menu.setBounds(0, 0, menuWidth, getHeight());
		this.add(menu);

		gridActions = new MenuGrid(session);
		menu.addItem(gridActions, 100);

		for (int i = 0; i < _actions.length; i++) {
			actions[i] = new MenuAction(session, _actions[i]);
			gridActions.addItem(actions[i]);
		}

		gridCubes = new MenuGrid(session);
		menu.addItem(gridCubes, 400);

		for (int i = 0; i < _items.size(); i++) {
			cubes.add(new MenuCubeSelection(session, _items.get(i)));
			gridCubes.addItem(cubes.get(i));
		}

		menu.addItem(selectInfos = new MenuSelectInfos(session), 400);

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
				g.setColor(Color.black);
				g.drawLine(centerX - crossSize, centerY - 1, centerX + crossSize - 1, centerY - 1);
				g.drawLine(centerX - crossSize, centerY, centerX + crossSize - 1, centerY);
				g.drawLine(centerX - 1, centerY - crossSize, centerX - 1, centerY + crossSize - 1);
				g.drawLine(centerX, centerY - crossSize, centerX, centerY + crossSize - 1);
			}

			break;

		case DIALOG:
			int grayBackground = 100;
			Color msgColor = Color.WHITE;

			// TODO [Improve] If messages text too long split in several lines

			// =============== Message (Line) =================

			// Background
			g.setColor(new Color(grayBackground, grayBackground, grayBackground, 150));
			g.fillRect(40, h - 100, msgBackWidth, 30);

			// Text
			g.setFont(font);
			g.setColor(msgColor);

			// Flash the text-cursor
			cursorStateTime++;
			if (cursorStateTime > 3) {
				cursorState = !cursorState;
				cursorStateTime = 0;
			}

			if (cursorState)
				g.drawString(msgLine.substring(0, cursorPos) + "|" + msgLine.substring(cursorPos), 45, h - 100 + 20);
			else
				g.drawString(msgLine.substring(0, cursorPos) + " " + msgLine.substring(cursorPos), 45, h - 100 + 20);

			// =============== Messages (Previous) =================

			// Background
			g.setColor(new Color(grayBackground, grayBackground, grayBackground, 80));
			g.fillRect(40, h - 100 - 10 - 30 * nbMsg, msgBackWidth, 30 * nbMsg);

			// Text
			g.setColor(msgColor);
			for (int i = 0; i < nbMsg; i++)
				g.drawString(messages[i].toMessage(), 45, h - 100 + 20 - 10 - (nbMsg - i) * 30);

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

	public void hideMenu() {
		menu.setVisible(session.gamemode == GameMode.CLASSIC);

		for (MenuAction e : actions)
			e.selected = session.action == e.action;

		gridCubes.setVisible(session.action == Action.CUBES);
		selectInfos.setVisible(true);
	}

	public void resetCubeSelection() {
		for (MenuCubeSelection e : cubes)
			e.selected = false;
	}

	// =========================================================================================================================

	public void roundRect(Rectangle rect) {
		g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 100, 50);
	}
}
