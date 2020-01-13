package client.window.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

import client.session.Session;
import client.session.UserAction;
import client.window.panels.menus.ButtonContainer;
import client.window.panels.menus.MenuButtonCube;
import client.window.panels.menus.MenuButtonUserAction;
import client.window.panels.menus.MenuCol;
import client.window.panels.menus.MenuGrid;
import client.window.panels.menus.MenuMap;
import client.window.panels.menus.MenuRessources;
import client.window.panels.menus.infos.MenuInfos;
import data.map.Cube;
import data.map.buildings.Building;
import data.map.units.Unit;
import server.game.GameMode;
import server.game.messages.Message;

public class PanGUI extends JPanel implements ButtonContainer {
	private static final long serialVersionUID = 3929655843006244723L;

	Session session;

	// Dialog font
	Font font = new Font("monospace", Font.BOLD, 18);

	// Work In Progress
	String wip = "WORK IN PROGRESS";
	Font fontWIP = new Font("monospace", Font.BOLD, 30);
	FontMetrics fm = getFontMetrics(fontWIP);

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

	MenuCol menu = new MenuCol();

	UserAction[] _userActions = { UserAction.MOUSE, UserAction.CREA_ADD, UserAction.CREA_DESTROY, UserAction.EDITOR };
	MenuButtonUserAction[] userActions = new MenuButtonUserAction[_userActions.length];

	MenuGrid gridActions;

	MenuMap map;
	MenuRessources ress;

	// ==== Select ====
	MenuInfos infos;
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

		menu.addTop(gridActions = new MenuGrid(), 100);

		for (int i = 0; i < _userActions.length; i++)
			gridActions.addItem(userActions[i] = new MenuButtonUserAction(session, _userActions[i]));

		menu.addBottom(map = new MenuMap(session), MenuCol.WIDTH);
		menu.addBottom(ress = new MenuRessources(session), 130);

		menu.addTop(infos = new MenuInfos(session), MenuCol.REMAINING);

		refreshGUI();
	}

	// =========================================================================================================================

	public void paintComponent(Graphics g) {
		int h = this.getHeight();

		g.setColor(Color.WHITE);
		g.setFont(fontWIP);
		g.drawString(wip, getWidth() - fm.stringWidth(wip) - 10, getHeight() - 10);

		if (session.stateHUD == StateHUD.DIALOG) {
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
		unit = null;
		build = null;
		infos.refresh(cube);
	}

	public void clear() {
		select(null);
	}

	// =========================================================================================================================

	public void refreshGUI() {
		menu.setVisible(session.gamemode == GameMode.CLASSIC);

		clear();

		for (MenuButtonUserAction e : userActions)
			e.selected = session.getAction() == e.action;

		if (session.getAction() == UserAction.CREA_ADD)
			infos.showCubes();

		map.updateMap();
		map.repaint();
	}

	public void resetCubeSelection() {
		for (MenuButtonCube e : infos.cubes)
			e.selected = false;
	}

	public void updateTexturePack() {
		for (MenuButtonCube m : infos.cubes)
			m.engine.texturePack = session.texturePack;
	}

	// =========================================================================================================================

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		menu.setBounds(x, y, menuWidth, height);
	}

	// =========================================================================================================================
	// ButtonContainer

	@Override
	public void releaseButtons() {
		refreshGUI();
	}
}
