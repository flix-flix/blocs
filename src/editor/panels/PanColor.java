package editor.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import data.id.ItemTableClient;
import editor.ActionEditor;
import editor.EditorManager;
import utils.panels.FPanel;

public class PanColor extends FPanel {
	private static final long serialVersionUID = -9106806857580543322L;

	private static final int WHITE = 0xffffff;
	private static final int BLACK = 0;

	// =============== Line Selector ===============
	/** Store the colors of the vertical line (top -> bottom) */
	private int[] colorLine = new int[261];
	/** Location of the line selector */
	private int lineStartX = 0, lineStartY = 20;// X set in resize()
	private int lineSize = 20;
	/** Position of the cursor in the line selector */
	private int lineSelector = 0;

	// =============== Square Selector ===============
	/** Location of the square selector */
	private int startSquareX = 25, startSquareY = 25;
	/** Position of the cursor in the square selector */
	private int squareSelectorX = 255, squareSelectorY = 0;

	// =============== Selector ===============
	private static final int NULL = 0, SQUARE = 1, LINE = -1;
	private int listeningSelector = NULL;

	// ============================== Selected colors ==============================
	int selectedColor = 0;

	// =============== Size ===============
	/** Size of the selected color display */
	int widthColor = 120, heightColor = 30;
	/** Location of the selectedColor */
	int startColorX, startColorY;// X set in refreshLang() | Y set in resize()

	// =============== Text ===============
	private Font font = new Font("monospace", Font.BOLD, 14);
	private FontMetrics fm = getFontMetrics(font);

	private String currentText, previousText;

	// =============== Button ===============
	private ButtonEditor alpha;

	// =============== Memory ===============
	/** Store the last 5 used colors */
	int[] memory = new int[] { 0xff0000, 0x00ff00, 0x0000ff, 0xff00ff, 0x00ffff };
	/** Width of a memory color display */
	int widthMemory = widthColor / 5;
	/**
	 * Padding between the bottom of the component and the bottom of the memory
	 * colors
	 */
	int pad = 10;

	// =========================================================================================================================

	public PanColor(EditorManager editor) {
		setBackground(Color.GRAY);

		int[] tab = new int[] { 0, 255, 0 };// B, R, G
		int index = 0;

		for (int i = 0; i < 6; i++) {
			for (int j = i % 2 == 0 ? 0 : 1; j <= 43; j++) {
				if (i % 2 == 0) // Positive growth
					tab[i % 3] = Math.min(j * 6, 255);
				else // Negative growth
					tab[i % 3] = Math.max(258 - j * 6, 0);

				colorLine[index++] = (((tab[1] << 8) + tab[2]) << 8) + tab[0];
			}
		}
		updatePointedColor();

		alpha = new ButtonEditor(editor, ActionEditor.SELECT_ALPHA);
		alpha.setSize(70, 35);
		alpha.listenWheel();
		alpha.setWheelMinMax(0, 20);
		alpha.setWheelStep(20);
		this.add(alpha);

		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				clickColor(e.getX(), e.getY());
			}
		});

		refreshLang();
	}

	// =========================================================================================================================

	@Override
	protected void paintCenter(Graphics g) {
		super.paintCenter(g);

		// =================================================
		// Line selector

		g.setColor(Color.DARK_GRAY);
		g.drawRect(lineStartX - 1, lineStartY - 1, lineSize + 2, colorLine.length + 1);
		g.drawRect(lineStartX - 2, lineStartY - 2, lineSize + 4, colorLine.length + 3);

		for (int y = 0; y < colorLine.length; y++) {
			g.setColor(new Color(colorLine[y]));
			g.drawLine(lineStartX, lineStartY + y, lineStartX + lineSize, lineStartY + y);
		}

		g.setColor(Color.BLACK);
		g.drawLine(lineStartX, lineStartY + lineSelector, lineStartX + lineSize, lineStartY + lineSelector);
		g.setColor(Color.LIGHT_GRAY);
		g.drawLine(lineStartX, lineStartY + lineSelector - 1, lineStartX + lineSize, lineStartY + lineSelector - 1);
		g.drawLine(lineStartX, lineStartY + lineSelector + 1, lineStartX + lineSize, lineStartY + lineSelector + 1);

		// =================================================
		// Square selector

		g.setColor(Color.DARK_GRAY);
		g.drawRect(startSquareX - 1, startSquareY - 1, 257, 257);
		g.drawRect(startSquareX - 2, startSquareY - 2, 259, 259);

		for (int y = 0; y < 256; y++) {
			int color = addHue(colorLine[lineSelector], WHITE, y / 255.);
			for (int x = 0; x < 256; x++) {
				g.setColor(new Color(addHue(color, BLACK, (255. - x) / 255)));
				g.drawLine(startSquareX + x, startSquareY + y, startSquareX + x, startSquareY + y);
			}
		}

		g.setColor(Color.LIGHT_GRAY);
		g.drawLine(startSquareX, startSquareY + squareSelectorY - 1, startSquareX + 255,
				startSquareY + squareSelectorY - 1);
		g.drawLine(startSquareX, startSquareY + squareSelectorY + 1, startSquareX + 255,
				startSquareY + squareSelectorY + 1);
		g.drawLine(startSquareX + squareSelectorX - 1, startSquareY, startSquareX + squareSelectorX - 1,
				startSquareY + 255);
		g.drawLine(startSquareX + squareSelectorX + 1, startSquareY, startSquareX + squareSelectorX + 1,
				startSquareY + 255);

		g.setColor(Color.BLACK);
		g.drawLine(startSquareX, startSquareY + squareSelectorY, startSquareX + 255, startSquareY + squareSelectorY);
		g.drawLine(startSquareX + squareSelectorX, startSquareY, startSquareX + squareSelectorX, startSquareY + 255);

		// =================================================
		// Selected color

		g.setColor(Color.DARK_GRAY);
		g.drawRect(startColorX - 1, startColorY - 1, widthColor + 1, heightColor * 2 + 1);
		g.drawRect(startColorX - 2, startColorY - 2, widthColor + 3, heightColor * 2 + 3);

		g.setColor(new Color(selectedColor));
		g.fillRect(startColorX, startColorY, widthColor, heightColor);

		for (int i = 0; i < 5; i++) {
			g.setColor(new Color(memory[i]));
			g.fillRect(startColorX + i * widthMemory, startColorY + heightColor, widthMemory, heightColor);
		}

		g.setColor(Color.BLACK);
		g.setFont(font);
		g.drawString(currentText, startColorX - 5 - fm.stringWidth(currentText), getHeight() - pad - heightColor - 10);
		g.drawString(previousText, startColorX - 5 - fm.stringWidth(previousText), getHeight() - pad - 10);
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

	/** Set selector position to the indicated coord */
	public void clickColor(int x, int y) {
		if (listeningSelector == LINE) {
			lineSelector = y - lineStartY;

			lineSelector = Math.min(colorLine.length - 1, lineSelector);
			lineSelector = Math.max(0, lineSelector);
		} else if (listeningSelector == SQUARE) {
			squareSelectorX = x - startSquareX;
			squareSelectorY = y - startSquareY;

			squareSelectorX = Math.min(255, squareSelectorX);
			squareSelectorX = Math.max(0, squareSelectorX);
			squareSelectorY = Math.min(255, squareSelectorY);
			squareSelectorY = Math.max(0, squareSelectorY);
		}

		updatePointedColor();
	}

	/** Set selecteColor from the position of the line and the square cursors */
	public void updatePointedColor() {
		selectedColor = addHue(addHue(colorLine[lineSelector], WHITE, squareSelectorY / 255.), BLACK,
				(255. - squareSelectorX) / 255);

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
		replaceCursorsNonOpti(selectedColor);
	}

	// =========================================================================================================================
	// Replace cursors

	private void replaceCursorsNonOpti(int color) {
		int line = 0, X = 0, Y = 0;
		int diff = 10_000, _diff, hue1, hue2;

		for (int i = 0; i < colorLine.length; i++)
			for (int y = 0; y < 256; y++) {
				hue1 = addHue(colorLine[i], WHITE, y / 255.);
				for (int x = 0; x < 256; x++) {
					hue2 = addHue(hue1, BLACK, (255. - x) / 255);
					_diff = getDiff(color, hue2);

					if (_diff < diff) {
						diff = _diff;
						line = i;
						X = x;
						Y = y;
					}
					if (diff == 0)
						break;
				}
			}

		lineSelector = line;
		squareSelectorX = X;
		squareSelectorY = Y;

		updatePointedColor();
	}

	private int getDiff(int c1, int c2) {
		int r = Math.abs(((c1 >> 16) & 0xff) - ((c2 >> 16) & 0xff));
		int g = Math.abs(((c1 >> 8) & 0xff) - ((c2 >> 8) & 0xff));
		int b = Math.abs((c1 & 0xff) - (c2 & 0xff));

		return r + g + b;
	}

	// =========================================================================================================================

	// TODO [Optimise] Replace cursors on color pick
	public int getLineCursor(int color) {
		int r = color >> 16 & 0xff;
		int g = color >> 8 & 0xff;
		int b = color & 0xff;
		int[] rgb = new int[] { r, g, b };

		int max = Math.max(r, Math.max(g, b));
		int min = Math.min(r, Math.min(g, b));

		boolean[] used = new boolean[] { false, false, false };
		boolean foundMin = false, foundMax = false;

		int[] RGB = new int[] { -1, -1, -1 };

		for (int i = 0; i < 3; i++) {
			if (rgb[i] == max && !foundMax) {
				foundMax = true;
				used[i] = true;
				RGB[i] = 255;
			} else if (rgb[i] == min && !foundMin) {
				foundMin = true;
				used[i] = true;
				RGB[i] = 0;
			}
		}

		for (int i = 0; i < 3; i++)
			if (!used[i]) {
				// RGB[i] = rgb[i] == 255 ? 255 : rgb[i] / 6 * 6;
				RGB[i] = rgb[i] / 6 * 6;
			}

		int fusion = (((RGB[0] << 8) + RGB[1]) << 8) + RGB[2];

		for (int i = 0; i < colorLine.length; i++)
			if (colorLine[i] == fusion)
				return i;

		return -1;
	}

	// =========================================================================================================================
	// Memory

	public void addColorMemory() {
		int color = selectedColor;

		int limit = 4;
		for (int i = 0; i < 4; i++)
			if (color == memory[i])
				limit = i;

		for (int i = limit; i > 0; i--)
			memory[i] = memory[i - 1];

		memory[0] = color;

		repaint();
	}

	// =========================================================================================================================

	public void refreshLang() {
		currentText = ItemTableClient.getText("editor.color.current");
		previousText = ItemTableClient.getText("editor.color.previous");

		startColorX = 25 + Math.max(fm.stringWidth(currentText), fm.stringWidth(previousText));
	}

	// =========================================================================================================================

	@Override
	public void click(MouseEvent e) {
		// ===== Color memory =====
		if (startColorY + heightColor <= e.getY() && e.getY() <= startColorY + 2 * heightColor
				&& startColorX <= e.getX() && e.getX() <= startColorX + widthColor) {
			setColor(memory[(e.getX() - startColorX) / (widthColor / 5)]);
			addColorMemory();
			return;
		}

		// ===== Color selector =====
		if (e.getX() >= lineStartX && e.getX() <= lineStartX + lineSize && e.getY() >= lineStartY
				&& e.getY() < lineStartY + 6 * 44)
			listeningSelector = LINE;

		else if (e.getX() >= startSquareX && e.getX() < startSquareX + 256 && e.getY() >= startSquareY
				&& e.getY() < startSquareY + 256)
			listeningSelector = SQUARE;
		else
			listeningSelector = NULL;

		clickColor(e.getX(), e.getY());
	}

	@Override
	public void resize() {
		lineStartX = getWidth() - 45;

		startColorY = getHeight() - pad - 2 * heightColor;

		int startW = startColorX + widthColor;
		int remainW = getWidth() - startW;

		int startH = startSquareX + 260;
		int remainH = getHeight() - startH;

		alpha.setCenter(startW + remainW / 2, startH + remainH / 2);
	}
}
