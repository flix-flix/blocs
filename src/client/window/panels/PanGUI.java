package client.window.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JPanel;

import client.messages.Message;
import client.session.Action;
import client.session.GameMode;
import client.session.Session;
import client.window.panels.emplacements.EmplacementAction;
import client.window.panels.emplacements.EmplacementCubeSelection;
import client.window.panels.emplacements.EmplacementSelect;
import data.enumeration.ItemID;
import data.map.Cube;
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

	// ======================= Emplacements =========================

	Action[] _actions = { Action.MOUSE, Action.SELECT, Action.BLOCS, Action.DESTROY };
	EmplacementAction[] actions = new EmplacementAction[4];

	ArrayList<Cube> _items = new ArrayList<>();
	ArrayList<EmplacementCubeSelection> items = new ArrayList<>();

	public EmplacementSelect select;

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

		// ========================================================================================

		int cols = 4, size = 90, border = 5;
		int x = 10, y = 10;

		for (int i = 0; i < _actions.length; i++) {
			this.add((actions[i] = new EmplacementAction(x + border + (i % cols) * (size + border),
					y + border + (i / cols) * (size + border), size, size, session, _actions[i])));
		}

		y += 120;

		for (int i = 0; i < _items.size(); i++) {
			items.add(new EmplacementCubeSelection(x + border + (i % cols) * (size + border),
					y + border + (i / cols) * (size + border), size, size, session, _items.get(i)));
			this.add(items.get(i));
		}

		this.add(select = new EmplacementSelect(x, y, 4 * 90 + (4 + 1) * 5, 400, session));

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
		case PAUSE:
			g.setColor(new Color(90, 90, 90, 90));
			g.fillRect(0, 0, w, h);

		case GAME:

			if (session.gamemode == GameMode.CLASSIC) {
				g.setColor(Color.GRAY);
				g.fillRect(0, 0, 20 + 4 * 90 + (4 + 1) * 5, getHeight());

				g.setColor(Color.lightGray);
				g.fillRect(10, 10, 4 * 90 + (4 + 1) * 5, getHeight());
			}

			else if (session.gamemode == GameMode.CREATIVE) {
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
			System.err.println("ERROR: PanGUI enum invalid: " + session.stateGUI);
			break;
		}
	}

	// =========================================================================================================================

	public void hideMenu() {
		for (EmplacementAction e : actions) {
			e.setVisible(session.gamemode == GameMode.CLASSIC);
			e.selected = session.action == e.action;
		}

		for (EmplacementCubeSelection e : items)
			e.setVisible(session.gamemode == GameMode.CLASSIC && session.action == Action.BLOCS);

		select.setVisible(session.gamemode == GameMode.CLASSIC && session.action == Action.MOUSE);
	}

	// =========================================================================================================================

	public void roundRect(Rectangle rect) {
		g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 100, 50);
	}
}
