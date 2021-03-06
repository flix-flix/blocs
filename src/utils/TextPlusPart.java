package utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.io.Serializable;

import javax.swing.JPanel;

/** Sub-part of {@link TextPlus} */
public class TextPlusPart implements Serializable {
	private static final long serialVersionUID = -3211612180350592004L;

	TextType type;
	/** The space to add between this part and the next one (in pixels) */
	int spaceAfter = 3;

	// =============== Str ===============
	String str;
	Font font;
	Color color;

	// =============== Img ===============
	Image img;
	int width, height;

	// =========================================================================================================================

	public TextPlusPart(String str) {
		this(str, null, null);
	}

	public TextPlusPart(String str, Font font, Color color) {
		type = TextType.STR;
		this.str = str;

		this.font = font == null ? new Font("monospace", Font.PLAIN, 18) : font;
		this.color = color == null ? Color.BLACK : color;
	}

	public TextPlusPart(String str, Font font, Color color, int space) {
		this(str, font, color);
		spaceAfter = space;
	}

	// =========================================================================================================================

	public TextPlusPart(Image img, int width, int height) {
		type = TextType.IMG;
		this.img = img;
		this.width = width;
		this.height = height;
	}

	// =========================================================================================================================

	/**
	 * Split the content in two:
	 * <ul>
	 * <li>the part of the content fitting in the given size
	 * <li>the rest
	 * </ul>
	 * 
	 * @param size
	 *            - the remaining size in the current line
	 * @param panel
	 *            - tool to measure strings
	 */
	public TextPlusPart[] split(int size, JPanel panel) {
		if (type == TextType.IMG)
			return new TextPlusPart[] { size >= width ? this : null, size >= width ? null : this };

		// ===== STR =====
		FontMetrics fm = panel.getFontMetrics(font);
		TextPlusPart fitting = new TextPlusPart("", font, color);
		TextPlusPart nextLine = null;

		if (str.trim().length() == 0)
			return new TextPlusPart[] { fitting, nextLine };

		String[] words = str.split(" ");

		// Add words one by one
		for (String word : words)
			// Don't fit in the remaning space
			if (fm.stringWidth(fitting.str + " " + word) > size) {
				nextLine = new TextPlusPart(str.substring(fitting.str.length() + 1), font, color);
				break;
			} else
				fitting.addText((fitting.str.isEmpty() ? "" : " ") + word);

		if (fitting.str.isEmpty())
			return new TextPlusPart[] { null, this };
		return new TextPlusPart[] { fitting, nextLine };
	}

	public TextPlusPart[] forceSplit(int size, JPanel panel) {
		FontMetrics fm = panel.getFontMetrics(font);
		TextPlusPart fitting = new TextPlusPart("", font, color);
		TextPlusPart nextLine = null;

		char[] letters = str.toCharArray();

		for (char letter : letters)
			// Don't fit in the remaning space
			if (fm.stringWidth(fitting.str + " " + letter) > size) {
				nextLine = new TextPlusPart(str.substring(fitting.str.length() + 1), font, color);
				break;
			} else
				fitting.addText("" + letter);

		return new TextPlusPart[] { fitting, nextLine };
	}

	public int getSize(JPanel panel) {
		return (type == TextType.IMG ? width : panel.getFontMetrics(font).stringWidth(str)) + spaceAfter;
	}

	// =========================================================================================================================

	public void draw(Graphics g, int startX, int bottomY) {
		if (type == TextType.IMG)
			g.drawImage(img, startX, bottomY - height + 8, width, height, null);
		else {
			g.setColor(color);
			g.setFont(font);
			g.drawString(str, startX, bottomY);
		}
	}

	// =========================================================================================================================

	public void addText(String text) {
		str += text;
	}

	// =========================================================================================================================

	@Override
	public String toString() {
		if (type == TextType.IMG)
			return "Image: " + width + "x" + height;
		return "Text: " + str;
	}

	// =========================================================================================================================

	public enum TextType {
		STR, IMG;
	}
}
