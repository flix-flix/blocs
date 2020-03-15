package utils;

import java.awt.Image;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * Class to represent text with different colors/fonts and containing images
 */
public class TextPlus {
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
				Utils.debug("Can't split TextPlusPart: " + part.toString() + " (" + sizes[0] + ", " + sizes[1] + ")");
				System.out.println(part.str.length());
				System.out.println(part.str);
				for (char c : part.str.toCharArray())
					System.out.println((int) c);
				return null;
			}

			// End the current line
			lines.add(currentLine);
			// === New line ===
			sizes[1] = sizes[0];
			return addPartToLine(lines, new TextPlus(), part, panel, sizes);
		}
		// It all fits in the current line
		else if (splitted[1] == null) {
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

	public ArrayList<TextPlusPart> getList() {
		return list;
	}
}
