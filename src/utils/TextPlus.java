package utils;

import java.awt.Graphics;
import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JPanel;

import utils.TextPlusPart.TextType;

/**
 * Class to represent text with different colors/fonts and containing images
 */
public class TextPlus implements Serializable {
	private static final long serialVersionUID = -1366313903960175159L;

	/** List of parts constituing this text */
	private ArrayList<TextPlusPart> list = new ArrayList<>();

	// =========================================================================================================================

	public TextPlus() {
	}

	// =========================================================================================================================

	public void add(TextPlusPart part) {
		list.add(part);
	}

	public void add(String str) {
		if (!str.isEmpty())
			add(new TextPlusPart(str));
	}

	public void add(Image img, int width, int height) {
		add(new TextPlusPart(img, width, height));
	}

	// =========================================================================================================================

	/** Returns the text splitted in lines of the indicated size */
	public ArrayList<TextPlus> getLines(JPanel panel, int size) {
		ArrayList<TextPlus> lines = new ArrayList<>();

		TextPlus line = new TextPlus();
		int[] sizes = new int[] { size, size };

		for (TextPlusPart part : list)
			line = addPartToLine(lines, line, part, panel, sizes);

		if (!line.list.isEmpty())
			lines.add(line);

		return lines;
	}

	/**
	 * Add a TextPlusPart to the line-splitted text
	 * 
	 * @param lines
	 *            - the list of lines
	 * @param currentLine
	 *            - the line currently written
	 * @param part
	 *            - the part to append
	 * @param panel
	 *            - tool to measure strings
	 * @param sizes
	 *            - 2 integers : "total line size" and "remaining size in the
	 *            current line"
	 * @return the line currently written
	 */
	private TextPlus addPartToLine(ArrayList<TextPlus> lines, TextPlus currentLine, TextPlusPart part, JPanel panel,
			int[] sizes) {
		TextPlusPart[] splitted = part.split(sizes[1], panel);

		// The TextPart will be added to the next line
		if (splitted[0] == null) {
			if (sizes[0] == sizes[1]) {
				if (part.type == TextType.STR)
					splitted = part.forceSplit(sizes[1], panel);
				else {
					Utils.debug(
							"Can't split TextPlusPart: " + part.toString() + " (" + sizes[0] + ", " + sizes[1] + ")");
					return null;
				}
			} else {
				// End the current line
				lines.add(currentLine);
				// === New line ===
				sizes[1] = sizes[0];
				return addPartToLine(lines, new TextPlus(), part, panel, sizes);
			}
		}

		// It all fits in the current line
		if (splitted[1] == null) {
			sizes[1] -= splitted[0].getSize(panel);
			currentLine.add(splitted[0]);
			return currentLine;
		}
		// 2 parts : one on this line and the other one on the next line
		else {
			// === End the current line ===
			currentLine.add(splitted[0]);
			lines.add(currentLine);
			// === New line ===
			sizes[1] = sizes[0];
			return addPartToLine(lines, new TextPlus(), splitted[1], panel, sizes);
		}
	}

	// =========================================================================================================================

	/**
	 * Draw the text
	 * 
	 * @param panel
	 *            - tool to measure strings
	 */
	public void draw(Graphics g, JPanel panel, int startX, int bottomY) {
		for (TextPlusPart part : list) {
			part.draw(g, startX, bottomY);
			startX += part.getSize(panel);
		}
	}
}
