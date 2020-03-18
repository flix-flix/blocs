package game;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import server.game.Player;
import server.game.messages.Message;
import utils.TextPlus;
import utils.TextPlusPart;

public class MessageManager {

	private Player player;
	private JPanel panel;
	private MessageSender sender;

	// =============== Memory ===============
	/**
	 * All messages since the beginning of the session (index 0 is the oldest
	 * message)
	 */
	private ArrayList<Message> messages = new ArrayList<>();
	/** All lines since the beginning of the session (index 0 is the oldest line) */
	private ArrayList<TextPlus> lines = new ArrayList<>();
	/** Writting line */
	private String msg = "";

	// =============== History ===============
	/**
	 * Messages sent history (Navigate with up/down arrows) (index 0 is the oldest
	 * message)
	 */
	private ArrayList<String> history = new ArrayList<>();
	/**
	 * Selection of a previous msg:<br>
	 * 0 > first msg sent<br>
	 * 1 > second msg sent<br>
	 * 2 > third msg sent<br>
	 * 3 > [saved msg]
	 */
	int indexHistory = 0;

	/** Save the currently writing msg during history browsing */
	private String savedMsg = new String();

	// =============== Cursor ===============
	/**
	 * Text cursor position in the written line<br>
	 * <code>
	 * .H.E.L.L.O.<br>
	 * ^ ^ ^ ^ ^ ^<br>
	 * 0 1 2 3 4 5</code>
	 */
	private int cursorPos = 0;

	/** true : show the text-cursor (switch to make the cursor flashing) */
	private boolean cursorState = true;
	/** Store time since last cursor state switch */
	private int cursorStateTime = 0;

	// =============== Recent ===============
	/** Number of lines to display (even if dialog isn't open) */
	private int nbLinesRecent = 0;
	/** The time at which the lines have been added */
	private ArrayList<Long> recentsTime = new ArrayList<Long>();

	// =============== Display state ===============
	/** true: display the writing line */
	private boolean displayLine = false;
	/** true: display the previous message */
	private boolean displayPrevious = false;
	/**
	 * true: display the messages more recent than {@link #recentTime} (even if
	 * {@link #displayPrevious} is false)
	 */
	private boolean displayRecent = false;
	/** Index of the bottom-displayed line */
	private int lastLine = 0;

	// =============== Options ===============
	/** Maximum number of lines displayed */
	private int nbLinesMax = 10;
	/** Width of the dialog background */
	private int dialogWidth = 1000;
	/** Number of ms during a msg is considered as recent */
	private long recentTime = 5000;

	private int grayBack = 120;
	/** Background msg color */
	private Color colorBack = new Color(grayBack, grayBack, grayBack, 200);

	// =============== Thread ===============
	private boolean run = true;

	// =========================================================================================================================

	/**
	 * @param player
	 *            - player sending messages
	 * @param panel
	 *            - tool to measure strings
	 */
	public MessageManager(MessageSender sender, Player player, JPanel panel) {
		this.sender = sender;
		this.player = player;
		this.panel = panel;

		Thread thread = new Thread(new Timer());
		thread.setName("Chat");
		thread.start();
	}

	// =========================================================================================================================

	public void stop() {
		run = false;
	}

	// =========================================================================================================================

	public void draw(Graphics g) {
		int startW = 420, startH = g.getClipBounds().height - 70;

		// Message (Line)
		if (displayLine) {
			TextPlus plus = new TextPlus();
			plus.add(new TextPlusPart(msg.substring(0, cursorPos), null, Color.WHITE, 0));
			plus.add(new TextPlusPart(cursorState ? "|" : " ", null, Color.WHITE, 0));
			plus.add(new TextPlusPart(msg.substring(cursorPos), null, Color.WHITE));

			ArrayList<TextPlus> lines = plus.getLines(panel, dialogWidth);

			for (int i = 0; i < Math.min(3, lines.size()); i++)
				drawLine(g, lines.get(lines.size() - 1 - i), startW, startH - i * 30);

			// TODO [Improve] Edition on multi-lines
		}

		startH -= 100;

		// ===== Messages (Previous) =====
		int nbLines = 0;
		if (displayPrevious)
			nbLines = Math.min(this.nbLinesMax, lines.size());
		else if (displayRecent)
			nbLines = nbLinesRecent;

		for (int i = 0; i < nbLines; i++)
			if (lastLine - i < 0)
				break;
			else
				drawLine(g, lines.get(lastLine - i), startW, startH - 30 * i);
	}

	private void drawLine(Graphics g, TextPlus plus, int startW, int startH) {
		// Background
		g.setColor(colorBack);
		g.fillRect(startW - 5, startH, dialogWidth + 10, 30);
		// Text
		plus.draw(g, panel, startW, startH + 20);
	}

	// =========================================================================================================================

	public void send() {
		if (msg.isEmpty())
			return;

		// Save line
		if (history.isEmpty() || !msg.equals(history.get(history.size() - 1)))
			history.add(msg);

		sender.sendMessage(new Message(msg, player));

		clearLine();
	}

	public synchronized void receive(Message msg) {
		// Save message
		messages.add(msg);

		boolean last = lastLine == lines.size() - 1;

		// Save lines
		ArrayList<TextPlus> lines = msg.toMessage().getLines(panel, dialogWidth);
		this.lines.addAll(lines);

		// Save reception time
		for (int i = 0; i < lines.size(); i++)
			recentsTime.add(System.currentTimeMillis());

		if (last)
			gotoLastLine();
	}

	// =========================================================================================================================

	public void clearLine() {
		msg = "";
		cursorPos = 0;

		indexHistory = history.size();
		savedMsg = "";
	}

	// =========================================================================================================================
	// History browsing

	public void historyPrevious() {
		if (indexHistory == 0)
			return;
		if (indexHistory == history.size())
			savedMsg = msg;
		indexHistory--;

		msg = history.get(indexHistory);

		cursorPos = msg.length();
	}

	public void historyNext() {
		if (indexHistory == history.size())
			return;
		indexHistory++;

		if (indexHistory == history.size())
			msg = savedMsg;
		else
			msg = history.get(indexHistory);

		cursorPos = msg.length();
	}

	// =========================================================================================================================
	// Text edition

	public void write(char c) {
		write("" + c);
	}

	public void write(String str) {
		msg = msg.substring(0, cursorPos) + str + msg.substring(cursorPos);

		cursorPos += str.length();
	}

	public void deletePreviousChar() {
		if (cursorPos > 0) {
			msg = msg.substring(0, cursorPos - 1) + msg.substring(cursorPos);
			cursorPos--;
		}
	}

	public void deleteNextChar() {
		if (cursorPos < msg.length()) {
			msg = msg.substring(0, cursorPos) + msg.substring(cursorPos + 1);
		}
	}

	// =========================================================================================================================
	// Messages browsing

	public void displayPrev() {
		if (lastLine >= nbLinesMax)
			lastLine--;
	}

	public void displayNext() {
		if (lastLine < lines.size() - 1)
			lastLine++;
	}

	// =========================================================================================================================
	// Cursor deplacements

	public void cursorMoveRight() {
		if (cursorPos < msg.length())
			cursorPos++;
	}

	public void cursorMoveLeft() {
		if (cursorPos > 0)
			cursorPos--;
	}

	public void cursorGotoEnd() {
		cursorPos = msg.length();
	}

	public void cursorGotoStart() {
		cursorPos = 0;
	}

	// =========================================================================================================================

	/** Display the most recent messages */
	public void gotoLastLine() {
		lastLine = lines.size() - 1;
	}

	public void displayLine(boolean displayLine) {
		this.displayLine = displayLine;
	}

	public void displayPrevious(boolean displayPrevious) {
		this.displayPrevious = displayPrevious;
	}

	public void displayRecent(boolean displayRecent) {
		this.displayRecent = displayRecent;
	}

	// =========================================================================================================================

	class Timer implements Runnable {
		@Override
		public void run() {
			while (run) {
				// ===== Flash the text-cursor =====
				cursorStateTime++;
				if ((cursorState && cursorStateTime > 3) || (!cursorState && cursorStateTime > 1)) {
					cursorState = !cursorState;
					cursorStateTime = 0;
				}

				// ===== Hide old messages =====
				nbLinesRecent = 0;

				for (int i = Math.max(recentsTime.size() - nbLinesMax, 0); i < recentsTime.size(); i++)
					if (System.currentTimeMillis() - recentsTime.get(i) < recentTime) {
						nbLinesRecent = Math.min(nbLinesMax, lines.size() - i);
						break;
					}

				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
