package client.session;

import java.util.ArrayList;

import client.window.panels.PanGUI;
import server.game.messages.Message;
import server.game.messages.TypeMessage;

public class MessageManager {

	// =============== Data =================

	// All messages since the beginning of the session
	private ArrayList<Message> messagesPrinted = new ArrayList<>();
	// Messages history (Navigate with up/down arrows)
	private ArrayList<String> history = new ArrayList<>();

	// =============== Display =================

	// Current Line
	String msg = new String("");
	// Maximum number of messages that will be displayed
	int nbMsgMax = 10;
	// Number of the first message to display (nbMsg-1 msg follow)
	int firstMsg = 0;

	// =============== History =================

	// Selection of a previous line
	// 0 > line1
	// 1 > line2
	// 2 > line3
	// 3 > [saved line]
	int lineHistory = 0;

	// Saved line
	String savedLine = new String();

	// Text cursr position in the written line
	// .H.E.L.L.O.
	// ^ ^ ^ ^ ^ ^
	// 0 1 2 3 4 5
	int cursorPos = 0;

	// =============== Draw =================

	Session session;
	PanGUI gui;

	// =========================================================================================================================

	public MessageManager(Session session) {
		this.session = session;
	}

	public MessageManager(Session session, PanGUI gui) {
		this.session = session;
		this.gui = gui;
	}

	// =========================================================================================================================

	public void updateLine() {
		gui.msgLine = msg;
	}

	public void updateCursor() {
		gui.cursorPos = cursorPos;
	}

	public void updateMessages() {
		gui.nbMsg = Math.min(messagesPrinted.size() - firstMsg, nbMsgMax);

		for (int i = 0; i < gui.nbMsg; i++)
			gui.messages[i] = messagesPrinted.get(firstMsg + i);
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
		session.send(new Message(msg, session.player));
		clearLine();
	}

	public void receive(Message msg) {
		messagesPrinted.add(msg);
		firstMsg = Math.max(0, messagesPrinted.size() - nbMsgMax);
		updateMessages();
	}

	// =========================================================================================================================

	public void addConsoleMsg(String text) {
		messagesPrinted.add(new Message(text, TypeMessage.CONSOLE));
	}

	public void addTextMsg(String text) {
		messagesPrinted.add(new Message(text));
	}

	public void addErrorMsg(String text) {
		messagesPrinted.add(new Message(text, TypeMessage.ERROR));
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
