package client.window.panels.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import client.window.panels.menus.Menu;
import data.id.ItemTable;
import utils.FlixBlocksUtils;

public class MenuHelp extends Menu {
	private static final long serialVersionUID = -3108013483466382742L;

	private Font font = new Font("monospace", Font.BOLD, 15);
	private FontMetrics fm = getFontMetrics(font);

	private Image img = FlixBlocksUtils.getImage("static/questionMark");

	private boolean active = false;

	private Ellipse2D ellipse;

	// ========== Tip ==========
	private Tip tip = Tip.ZOOM;
	private String tipText;
	private ArrayList<String> tipLines = null;

	// ========== Arrow ==========
	int widthArrow = 30;

	// =========================================================================================================================

	MenuHelp() {
		ellipse = new Ellipse2D.Double(10, 10, 80, 80);
		updateTip();
	}

	// =========================================================================================================================

	public void updateTip() {
		tipText = ItemTable.getTip(tip);
		tipLines = FlixBlocksUtils.getLines(tipText, fm, getWidth() - 100 - widthArrow - 20);
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		if (active) {
			g.setColor(Color.GRAY);
			g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 100, 100);

			// ========== Tip ==========
			g.setColor(Color.WHITE);
			g.setFont(font);
			int lineH = (int) fm.getStringBounds("A", g).getHeight();

			for (int i = 0; i < tipLines.size(); i++) {
				int _y = getHeight() / 2 - lineH * tipLines.size() / 4  + lineH * i;
				g.drawString(tipLines.get(i), 100, _y);
			}

			// ========== Arrows ==========
			g.setColor(Color.DARK_GRAY);
			int startArrow = getWidth() - widthArrow;
			int arrowH = getHeight() * 3 / 10, arrowH2 = arrowH / 2;
			int startY = getHeight() / 2 - arrowH - 5, startY2 = getHeight() / 2 + 5;

			for (int y = 0; y <= arrowH; y++) {
				g.drawLine(startArrow - y / 2, startY + y, startArrow + (y + 1) / 2, startY + y);
				g.drawLine(startArrow - arrowH2 + y / 2, startY2 + y, startArrow + arrowH2 - (y + 1) / 2, startY2 + y);
			}

		} else {
			g.setColor(Color.GRAY);
			g.fillRoundRect(0, 0, 100, 100, 100, 100);
		}

		// ========== Logo "?" ==========
		g.setColor(Color.LIGHT_GRAY);
		g.fillOval(10, 10, 80, 80);

		g.drawImage(img, 20, 20, 60, 60, null);
	}

	// =========================================================================================================================

	@Override
	public void click(MouseEvent e) {
		if (ellipse.contains(e.getPoint()))
			active = !active;

		if (e.getX() > getWidth() - widthArrow - 20) {
			if (e.getY() > getHeight() / 2)
				tip = tip.next();
			else
				tip = tip.previous();
			updateTip();
		}
	}

	@Override
	public void resize() {
		updateTip();
	}

	// =========================================================================================================================

	public enum Tip {
		ZOOM, ROTATE, GRID, PICK_COLOR, FACE_NAME, LINE_SQUARE;

		Tip next() {
			return values()[(ordinal() + 1) % values().length];
		}

		Tip previous() {
			return values()[(ordinal() + values().length - 1) % values().length];
		}
	}
}
