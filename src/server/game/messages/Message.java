package server.game.messages;

import java.io.Serializable;

import server.game.Player;

public class Message implements Serializable {
	private static final long serialVersionUID = -7696844549236456406L;

	private String text;
	private Player author;
	private TypeMessage type;

	public Message(String text, TypeMessage type, Player author) {
		this.author = author;
		this.text = text;
		this.type = type;
	}

	public Message(String text, TypeMessage type) {
		this(text, type, null);
	}

	public Message(String text, Player author) {
		this(text, TypeMessage.AUTHOR, author);
	}

	public Message(String text) {
		this(text, TypeMessage.TEXT);
	}

	// =========================================================================================================================

	public String toMessage() {
		switch (type) {
		case AUTHOR:
			return author.getName() + " : " + text;
		case CONSOLE:
			// return "[Console] " + text;
		case ERROR:
			// return "[Error] " + text;
		case TEXT:
			return text;
		default:
			return "Error during message retranscription";
		}
	}

	// =========================================================================================================================

	public String getText() {
		return text;
	}

	public Player getAuthor() {
		return author;
	}

	public TypeMessage getType() {
		return type;
	}

	// =========================================================================================================================

	@Override
	public String toString() {
		return "Message [text=" + text + ", author=" + author + ", type=" + type + "]";
	}
}
