package server.game.messages;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;

import server.game.Player;
import utils.TextPlus;
import utils.TextPlusPart;

public class Message implements Serializable {
	private static final long serialVersionUID = -7696844549236456406L;

	// =============== Data ===============
	private String text;
	private Player author;
	private TypeMessage type;

	private TextPlus plus;

	// =============== Fonts/Colors ===============
	private final static Color authorC = Color.BLACK;
	private final static Color textC = Color.WHITE;
	private final static Color errorC = new Color(255, 150, 0);// Orange

	private Font font = new Font("monospace", Font.BOLD, 18);

	// =========================================================================================================================

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

	public Message(TextPlus plus, TypeMessage type) {
		this.type = type;
		this.plus = plus;
	}

	// =========================================================================================================================

	public TextPlus toMessage() {
		if (plus != null)
			return plus;

		TextPlus text = new TextPlus();
		switch (type) {
		case AUTHOR:
			text.add(new TextPlusPart(author.getName(), font, authorC));
			text.add(new TextPlusPart(" : ", font, textC));
			text.add(new TextPlusPart(this.text, font, textC));
			break;
		case CONSOLE:
			text.add(new TextPlusPart(this.text, font, Color.LIGHT_GRAY));
			break;
		case ERROR:
			text.add(new TextPlusPart(this.text, null, errorC));
			break;
		case TEXT:
			text.add(new TextPlusPart(this.text, null, Color.LIGHT_GRAY));
			break;
		default:
			text.add(new TextPlusPart("Error during message retranscription", null, errorC));
			break;
		}
		return text;
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
