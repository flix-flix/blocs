package game.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import environment.PanEnvironment;
import game.CameraMode;
import game.Game;
import game.StateHUD;
import game.tips.TipGame;
import server.game.messages.Message;
import server.game.messages.TypeMessage;
import utils.panels.help.PanHelp;
import utils.panels.help.PanHelp.Mark;

public class PanGameEnv extends PanEnvironment {
	private static final long serialVersionUID = 5191296217478885760L;

	private Game game;

	// =============== Size ===============
	private int menuWidth = 400;
	private int startXEnv;

	// =============== Work In Progress ===============
	private String wip = "WORK IN PROGRESS";
	private Font fontWIP = new Font("monospace", Font.BOLD, 30);
	private FontMetrics fm = getFontMetrics(fontWIP);

	// =============== Cross ===============
	/** Size of the central indicator (creative mode) */
	private int crossSize = 7;

	// =============== Dialog ===============
	private Font dialogFont = new Font("monospace", Font.BOLD, 18);
	private final static Color RED = new Color(255, 150, 0);

	/** Previous messages to display */
	public Message[] messages = new Message[10];
	/** Number of messages to display */
	public int nbMsg = 0;
	/** Width of the dialog background */
	private int msgBackWidth = 1000;

	/** Current line */
	public String msgLine = new String();

	/** Position of the text-cursor */
	public int cursorPos = 0;
	/** true : show the text-cursor (switch to make the cursor flashing) */
	private boolean cursorState = true;
	/** Store time since last cursor state switch */
	private int cursorStateTime = 0;

	// =============== Panels ===============
	public PanPause pause;
	public PanHelp help;

	// =========================================================================================================================

	public PanGameEnv(Game game) {
		super(game);
		// Like this.env but already casted
		this.game = game;

		this.setOpaque(false);

		this.add(pause = new PanPause(game));

		help = new PanHelp(Mark.INTERROGATION, 700, 80, 10, TipGame.values()[0]);
		help.setBackground(new Color(0xff4068c4));
		help.setBorderColor(Color.LIGHT_GRAY);
		help.setForeground(Color.DARK_GRAY);
		help.setLocation(25, getHeight() - 25);
		this.add(help);
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g.create(startXEnv, 0, getWidth() - startXEnv, getHeight()));

		int centerW = getWidth() / 2;
		int centerH = getHeight() / 2;

		if (img != null) {
			if (game.cameraMode == CameraMode.FIRST_PERSON) {
				// Middle indicator : cross
				g.setColor(Color.WHITE);
				g.drawLine(centerW - crossSize, centerH - 1, centerW + crossSize - 1, centerH - 1);
				g.drawLine(centerW - crossSize, centerH, centerW + crossSize - 1, centerH);
				g.drawLine(centerW - 1, centerH - crossSize, centerW - 1, centerH + crossSize - 1);
				g.drawLine(centerW, centerH - crossSize, centerW, centerH + crossSize - 1);
			}

			// =============== Dialog Display ===============
			if (game.getStateHUD() == StateHUD.DIALOG) {
				int grayBack = 120;
				Color colorBack = new Color(grayBack, grayBack, grayBack, 200);
				Color colorMsg = Color.WHITE;

				int startW = 420;

				// TODO [Improve] If messages text too long split in several lines

				// =============== Message (Line) ===============

				// Background
				g.setColor(colorBack);
				g.fillRect(startW, getHeight() - 100, msgBackWidth, 30);

				// Text
				g.setFont(dialogFont);
				g.setColor(colorMsg);

				// Flash the text-cursor
				cursorStateTime++;
				if (cursorStateTime > 3) {
					cursorState = !cursorState;
					cursorStateTime = 0;
				}

				if (cursorState)
					g.drawString(msgLine.substring(0, cursorPos) + "|" + msgLine.substring(cursorPos), startW + 5,
							getHeight() - 100 + 20);
				else
					g.drawString(msgLine.substring(0, cursorPos) + " " + msgLine.substring(cursorPos), startW + 5,
							getHeight() - 100 + 20);

				// =============== Messages (Previous) ===============

				// Background
				g.setColor(colorBack);
				g.fillRect(startW, getHeight() - 100 - 10 - 30 * nbMsg, msgBackWidth, 30 * nbMsg);

				// Text
				for (int i = 0; i < nbMsg; i++) {
					g.setColor(getColor(messages[i].getType()));
					g.drawString(messages[i].toMessage(), startW + 5, getHeight() - 100 + 20 - 10 - (nbMsg - i) * 30);
				}
			}
		}

		// =========================================================================================================================

		// Work In Progress
		g.setColor(Color.WHITE);
		g.setFont(fontWIP);
		g.drawString(wip, getWidth() - fm.stringWidth(wip) - 10, getHeight() - 10);
	}

	// =========================================================================================================================

	public void repaintEnv() {
		if (game.getStateHUD() == StateHUD.PAUSE)
			repaint();
		else
			repaint(startXEnv, 0, getWidth() - startXEnv, getHeight());
	}

	// =========================================================================================================================

	private Color getColor(TypeMessage type) {
		switch (type) {
		case ERROR:
			return RED;
		case CONSOLE:
			return Color.LIGHT_GRAY;
		case AUTHOR:
		case TEXT:
		default:
			return Color.WHITE;
		}
	}

	// =========================================================================================================================

	public void updateEnvBounds() {
		startXEnv = game.cameraMode == CameraMode.CLASSIC ? menuWidth : 0;

		help.setBottomLeftCorner(startXEnv + 25, getHeight() - 35);
	}

	// =========================================================================================================================

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);

		updateEnvBounds();

		pause.setSize(width, height);
	}
}
