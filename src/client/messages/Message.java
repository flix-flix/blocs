package client.messages;

public class Message {

	private String text;
	private String author;
	private TypeMessage type;

	public Message(String text, TypeMessage type, String author) {
		this.author = author;
		this.text = text;
		this.type = type;
	}

	public Message(String text, TypeMessage type) {
		this(text, type, null);
	}

	public Message(String text, String author) {
		this(text, TypeMessage.AUTHOR, author);
	}

	public Message(String text) {
		this(text, TypeMessage.TEXT);
	}

	// =========================================================================================================================

	public String toMessage() {
		switch (type) {
		case AUTHOR:
			return author + " : " + text;
		case TEXT:
			return text;
		case CONSOLE:
			return "[Console] " + text;
		case ERROR:
			return "[Error] " + text;
		default:
			return "Error during message retranscription";
		}
	}

	// =========================================================================================================================

	public String getText() {
		return text;
	}

	public String getAuthor() {
		return author;
	}

	public TypeMessage getType() {
		return type;
	}
}
