package game;

import java.util.ArrayList;

import server.game.messages.Message;

public class MessageManager {

	Game game;

	// =============== Data ===============
	/** All messages since the beginning of the session */
	private ArrayList<Message> messagesPrinted = new ArrayList<>();
	/** Messages history (Navigate with up/down arrows) */
	private ArrayList<String> history = new ArrayList<>();

	// =============== Display ===============
	/** Current Line */
	String msg = new String("");
	/** Maximum number of messages that will be displayed */
	int nbMsgMax = 10;
	/** Number of the first message to display (nbMsg-1 msg follow) */
	int firstMsg = 0;

	// =============== History ===============
	/**
	 * Selection of a previous line<br>
	 * 0 > line1<br>
	 * 1 > line2<br>
	 * 2 > line3<br>
	 * 3 > [saved line]
	 */
	int lineHistory = 0;

	/** Saved line */
	String savedLine = new String();

	/**
	 * Text cursor position in the written line<br>
	 * <code>
	 * .H.E.L.L.O.<br>
	 * ^ ^ ^ ^ ^ ^<br>
	 * 0 1 2 3 4 5</code>
	 */
	int cursorPos = 0;

	// =========================================================================================================================

	public MessageManager(Game game) {
		this.game = game;
	}

	// =========================================================================================================================

	public void updateLine() {
		game.panel.msgLine = msg;
	}

	public void updateCursor() {
		game.panel.cursorPos = cursorPos;
	}

	public void updateMessages() {
		game.panel.nbMsg = Math.min(messagesPrinted.size() - firstMsg, nbMsgMax);

		for (int i = 0; i < game.panel.nbMsg; i++)
			game.panel.messages[i] = messagesPrinted.get(firstMsg + i);
	}

	// =========================================================================================================================

	public void write(char c) {
		write("" + c);
	}

	public void write(String str) {
		msg = msg.substring(0, cursorPos) + str + msg.substring(cursorPos);
		updateLine();

		cursorPos += str.length();
		updateCursor();
	}

	// =========================================================================================================================

	public void historyPrevious() {
		if (lineHistory == 0)
			return;
		if (lineHistory == history.size())
			savedLine = msg;
		lineHistory--;

		msg = history.get(lineHistory);
		updateLine();

		cursorPos = msg.length();
		updateCursor();
	}

	public void historyNext() {
		if (lineHistory == history.size())
			return;
		lineHistory++;

		if (lineHistory == history.size())
			msg = savedLine;
		else
			msg = history.get(lineHistory);
		updateLine();

		cursorPos = msg.length();
		updateCursor();
	}

	public void cursorMoveRight() {
		if (cursorPos < msg.length())
			cursorPos++;
		updateCursor();
	}

	public void cursorMoveLeft() {
		if (cursorPos > 0)
			cursorPos--;
		updateCursor();
	}

	// =========================================================================================================================

	public void deletePrevious() {
		if (cursorPos > 0) {
			msg = msg.substring(0, cursorPos - 1) + msg.substring(cursorPos);
			updateLine();
			cursorPos--;
			updateCursor();
		}
	}

	public void deleteNext() {
		if (cursorPos < msg.length()) {
			msg = msg.substring(0, cursorPos) + msg.substring(cursorPos + 1);
			updateLine();
		}
	}

	// =========================================================================================================================

	public void send() {
		if (msg.isEmpty())
			return;

		saveLine();
		game.send(new Message(msg, game.player));
		clearLine();
	}

	public void receive(Message msg) {
		messagesPrinted.add(msg);
		firstMsg = Math.max(0, messagesPrinted.size() - nbMsgMax);
		updateMessages();
	}

	// =========================================================================================================================

	public void saveLine() {
		if (history.isEmpty() || !msg.equals(history.get(history.size() - 1)))
			history.add(msg);
	}

	public void clearLine() {
		msg = "";
		updateLine();
		cursorPos = 0;
		updateCursor();

		lineHistory = history.size();
		savedLine = "";
	}

	// =========================================================================================================================

	public void pageUp() {
		if (firstMsg > 0) {
			firstMsg--;
			updateMessages();
		}
	}

	public void pageDown() {
		if (firstMsg < messagesPrinted.size() - nbMsgMax) {
			firstMsg++;
			updateMessages();
		}
	}

	public void end() {
		cursorPos = msg.length();
		updateCursor();
	}

	public void start() {
		cursorPos = 0;
		updateCursor();
	}
}
