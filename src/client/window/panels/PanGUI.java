package client.window.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import client.messages.Message;
import client.session.GameMode;
import client.session.Session;

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

	// ======================= Pause =========================

	public JButton resume, save, options, quit;

	// =========================================================================================================================

	public PanGUI(Session s) {
		session = s;
		this.setOpaque(false);
		this.setLayout(null);

		resume = new JButton("Reprendre");
		save = new JButton("Sauvegarder");
		quit = new JButton("Quitter");
		options = new JButton("Options");

		resume.setBounds(900, 150, 200, 50);
		options.setBounds(900, 300, 200, 50);
		save.setBounds(900, 450, 200, 50);
		quit.setBounds(900, 600, 200, 50);

		resume.setVisible(false);
		options.setVisible(false);
		save.setVisible(false);
		quit.setVisible(false);

		add(resume);
		add(options);
		add(save);
		add(quit);

		options.setEnabled(false);
		save.setEnabled(false);

		resume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				session.keyboard.resume();
				session.fen.requestFocusInWindow();
			}
		});
		options.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
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

			if (session.gamemode == GameMode.CLASSIC) {

			}

			else if (session.gamemode == GameMode.CREATIVE) {
				gg.setColor(Color.black);
				gg.drawLine(centerX - crossSize, centerY - 1, centerX + crossSize - 1, centerY - 1);
				gg.drawLine(centerX - crossSize, centerY, centerX + crossSize - 1, centerY);
				gg.drawLine(centerX - 1, centerY - crossSize, centerX - 1, centerY + crossSize - 1);
				gg.drawLine(centerX, centerY - crossSize, centerX, centerY + crossSize - 1);
			}

			break;

		case PAUSE:
			g.setColor(new Color(90, 90, 90, 90));
			g.fillRect(0, 0, w, h);
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

	public void roundRect(Rectangle rect) {
		g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 100, 50);
	}
}
