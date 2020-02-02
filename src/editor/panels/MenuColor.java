package editor.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import editor.ActionEditor;
import editor.Editor;
import utils.panels.Menu;

public class MenuColor extends Menu {
	private static final long serialVersionUID = -9106806857580543322L;

	private static final int WHITE = 0xffffff;
	private static final int BLACK = 0;

	private Font font = new Font("monospace", Font.BOLD, 14);
	private FontMetrics fm = getFontMetrics(font);

	// =========================================================================================================================
	private MenuButtonEditor valid, alpha;

	private int[] colorLine = new int[6 * 44];
	private int lineStartX = 0;// Set in resize
	private int lineStartY = 20;
	private int lineSize = 20;
	private int lineSelector = 0;

	private int lineSelectedColor = 0;

	private int squareStartX = 25;
	private int squareStartY = 25;
	private int squareSelectorX = 255;
	private int squareSelectorY = 0;

	int squareSelectedColor = 0;

	private static final int NULL = 0, SQUARE = 1, LINE = -1;
	private int selectedSelector = 0;

	// =========================================================================================================================
	private int selectedColor = 0;

	String newStr = "New : ";
	String currentStr = "Current : ";

	int startSelectedColor = 25 + Math.max(fm.stringWidth(newStr), fm.stringWidth(currentStr));

	// =========================================================================================================================

	public MenuColor(Editor editor) {
		int[] tab = new int[] { 0, 255, 0 };// B, R, G

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j <= 43; j++) {
				if (i % 2 == 0) // Positive growth
					tab[i % 3] = Math.min(j * 6, 255);
				else // Negative growth
					tab[i % 3] = Math.max(255 - j * 6, 0);

				colorLine[i * 44 + j] = (((tab[1] << 8) + tab[2]) << 8) + tab[0];
			}
		}

		updatePointedColor();

		valid = new MenuButtonEditor(editor, ActionEditor.VALID_COLOR);
		valid.setBounds(getWidth() - 150, getHeight() - 50, 75, 35);
		this.add(valid);

		alpha = new MenuButtonEditor(editor, ActionEditor.SELECT_ALPHA);
		alpha.setWheelMinMax(0, 20);
		alpha.setWheelStep(20);
		this.add(alpha);

		alpha.setBounds(getWidth() - 60, getHeight() - 50, 50, 35);

		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				clickColor(e.getX(), e.getY());
			}
		});
	}

	// =========================================================================================================================

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

		// =================================================

		g.setColor(Color.DARK_GRAY);
		g.drawRect(lineStartX - 1, lineStartY - 1, lineSize + 2, 6 * 44 + 1);
		g.drawRect(lineStartX - 2, lineStartY - 2, lineSize + 4, 6 * 44 + 3);

		for (int y = 0; y < 6 * 44; y++) {
			g.setColor(new Color(colorLine[y]));
			g.drawLine(lineStartX, lineStartY + y, lineStartX + lineSize, lineStartY + y);
		}

		g.setColor(Color.BLACK);
		g.drawLine(lineStartX, lineStartY + lineSelector, lineStartX + lineSize, lineStartY + lineSelector);
		g.setColor(Color.LIGHT_GRAY);
		g.drawLine(lineStartX, lineStartY + lineSelector - 1, lineStartX + lineSize, lineStartY + lineSelector - 1);
		g.drawLine(lineStartX, lineStartY + lineSelector + 1, lineStartX + lineSize, lineStartY + lineSelector + 1);

		// =================================================

		g.setColor(Color.DARK_GRAY);
		g.drawRect(squareStartX - 1, squareStartY - 1, 257, 257);
		g.drawRect(squareStartX - 2, squareStartY - 2, 259, 259);

		for (int y = 0; y < 256; y++) {
			int color = addHue(lineSelectedColor, WHITE, y / 255.);
			for (int x = 0; x < 256; x++) {
				g.setColor(new Color(addHue(color, BLACK, (255. - x) / 255)));
				g.drawLine(squareStartX + x, squareStartY + y, squareStartX + x, squareStartY + y);
			}
		}

		g.setColor(Color.LIGHT_GRAY);
		g.drawLine(squareStartX, squareStartY + squareSelectorY - 1, squareStartX + 255,
				squareStartY + squareSelectorY - 1);
		g.drawLine(squareStartX, squareStartY + squareSelectorY + 1, squareStartX + 255,
				squareStartY + squareSelectorY + 1);
		g.drawLine(squareStartX + squareSelectorX - 1, squareStartY, squareStartX + squareSelectorX - 1,
				squareStartY + 255);
		g.drawLine(squareStartX + squareSelectorX + 1, squareStartY, squareStartX + squareSelectorX + 1,
				squareStartY + 255);

		g.setColor(Color.BLACK);
		g.drawLine(squareStartX, squareStartY + squareSelectorY, squareStartX + 255, squareStartY + squareSelectorY);
		g.drawLine(squareStartX + squareSelectorX, squareStartY, squareStartX + squareSelectorX, squareStartY + 255);

		// =================================================

		int sizeX = 100;
		int sizeY = 30;

		int border = 10;

		g.setColor(Color.DARK_GRAY);
		g.drawRect(startSelectedColor - 1, getHeight() - border - sizeY * 2 - 1, sizeX + 1, sizeY * 2 + 1);
		g.drawRect(startSelectedColor - 2, getHeight() - border - sizeY * 2 - 2, sizeX + 3, sizeY * 2 + 3);

		g.setColor(new Color(squareSelectedColor));
		g.fillRect(startSelectedColor, getHeight() - border - sizeY * 2, sizeX, sizeY);
		g.setColor(new Color(selectedColor));
		g.fillRect(startSelectedColor, getHeight() - border - sizeY, sizeX, sizeY);

		g.setColor(Color.LIGHT_GRAY);
		g.setFont(font);
		g.drawString(newStr, startSelectedColor - fm.stringWidth(newStr), getHeight() - border - sizeY - 10);
		g.drawString(currentStr, startSelectedColor - fm.stringWidth(currentStr), getHeight() - border - 10);
	}

	// =========================================================================================================================

	/**
	 * Add the hue to the color (doesn't modify the alpha)
	 * 
	 * @param color
	 *            - the original color {@link BufferedImage#TYPE_INT_ARGB}
	 * @param hue
	 *            - the hue {@link BufferedImage#TYPE_INT_RGB}
	 * @param percent
	 *            - in bounds [0, 1]
	 * @return mixed color {@link BufferedImage#TYPE_INT_ARGB}
	 */
	public static int addHue(int color, int hue, double percent) {
		int red = (color >> 16) & 0xff;
		int green = (color >> 8) & 0xff;
		int blue = color & 0xff;

		int hueRed = (hue >> 16) & 0xff;
		int hueGreen = (hue >> 8) & 0xff;
		int hueBlue = hue & 0xff;

		red = Math.min(255, red + (int) ((hueRed - red) * percent));
		green = Math.min(255, green + (int) ((hueGreen - green) * percent));
		blue = Math.min(255, blue + (int) ((hueBlue - blue) * percent));

		return (((red << 8) + green) << 8) + blue;
	}

	// =========================================================================================================================

	public void clickColor(int x, int y) {
		if (selectedSelector == LINE) {
			lineSelector = y - lineStartY;

			lineSelector = Math.min(6 * 44 - 1, lineSelector);
			lineSelector = Math.max(0, lineSelector);
		} else if (selectedSelector == SQUARE) {
			squareSelectorX = x - squareStartX;
			squareSelectorY = y - squareStartY;

			squareSelectorX = Math.min(255, squareSelectorX);
			squareSelectorX = Math.max(0, squareSelectorX);
			squareSelectorY = Math.min(255, squareSelectorY);
			squareSelectorY = Math.max(0, squareSelectorY);
		}

		updatePointedColor();
	}

	public void updatePointedColor() {
		lineSelectedColor = colorLine[lineSelector];
		squareSelectedColor = addHue(addHue(lineSelectedColor, WHITE, squareSelectorY / 255.), BLACK,
				(255. - squareSelectorX) / 255);

		repaint();
	}

	public void selectColor() {
		selectedColor = squareSelectedColor;
		repaint();
	}

	// =========================================================================================================================

	public int getColor() {
		if (alpha.getWheelStep() == 0)
			return 0;
		return ((int) (255 * (alpha.getWheelStep() / 20.)) << 24) + selectedColor;
	}

	public void setColor(int color) {
		selectedColor = color & 0xffffff;
		repaint();
	}

	// =========================================================================================================================

	@Override
	public void click(MouseEvent e) {
		if (e.getX() >= lineStartX && e.getX() <= lineStartX + lineSize && e.getY() >= lineStartY
				&& e.getY() < lineStartY + 6 * 44)
			selectedSelector = LINE;

		else if (e.getX() >= squareStartX && e.getX() < squareStartX + 256 && e.getY() >= squareStartY
				&& e.getY() < squareStartY + 256)
			selectedSelector = SQUARE;
		else
			selectedSelector = NULL;

		clickColor(e.getX(), e.getY());
	}

	@Override
	public void resize() {
		lineStartX = getWidth() - 45;

		int start = squareStartX + 260;
		int remain = getHeight() - start;

		valid.setBounds(getWidth() - 150, start + remain / 2 - 35 / 2, 75, 35);
		alpha.setBounds(getWidth() - 60, start + remain / 2 - 35 / 2, 50, 35);
	}
}
