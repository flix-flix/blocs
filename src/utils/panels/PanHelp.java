package utils.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import utils.TextPlus;
import utils.TextPlusPart;
import utils.Utils;

public class PanHelp extends FPanel {
	private static final long serialVersionUID = -3108013483466382742L;

	// =============== Size ===============
	private int width = 500, circleSize = 80, border = 10, total = 100;

	// =============== Circle ===============
	private Ellipse2D circle;
	private Image img;
	private boolean active = false;

	// =============== Tips ===============
	private TextPlus[] tipsPlus;
	private int tipIndex = 0;

	private ArrayList<TextPlus> tipPlusLines;

	// =============== Arrows ===============
	private int widthArrow = 30;

	// =========================================================================================================================

	public PanHelp(TextPlus[] tips) {
		this.tipsPlus = tips;

		setBorderColor(Color.GRAY);
	}

	public PanHelp(TextPlus[] tips, Mark mark, int width, int circleSize, int border) {
		this(tips);
		this.width = width;
		this.circleSize = circleSize;
		this.border = border;

		this.total = circleSize + border * 2;

		this.setOpaque(false);

		setSize(total, total);
		updateTip();

		circle = new Ellipse2D.Double(border, border, circleSize, circleSize);

		img = Utils.getResourceImage(mark == Mark.INTERROGATION ? "/interrogationMark.png" : "/exclamationMark.png");

		setBackground(Color.LIGHT_GRAY);
		setForeground(Color.WHITE);
	}

	// =========================================================================================================================

	public void updateTip() {
		tipPlusLines = tipsPlus[tipIndex].getLines(this, width - total - widthArrow - 20);
	}

	public void setTips(TextPlus[] tips) {
		if (tips == null || tips.length == 0) {
			TextPlus text = new TextPlus();
			text.add(new TextPlusPart("ERROR: No tips to display", null, Color.RED));
			this.tipsPlus = new TextPlus[] { text };
		} else
			this.tipsPlus = tips;

		tipIndex = 0;
		updateTip();
	}

	// =========================================================================================================================

	// Override paintComponent() cause it's non-rectangular
	@Override
	protected void paintComponent(Graphics g) {
		if (active) {
			g.setColor(borderColor);
			g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, total, total);

			// ========== Tip ==========
			// TODO [Fix] Line height depends on TextPlus font
			int lineH = 20;

			for (int i = 0; i < tipPlusLines.size(); i++) {
				int x = total;
				int y = getHeight() / 2 - lineH * tipPlusLines.size() / 4 + lineH * i;
				tipPlusLines.get(i).draw(g, this, x, y);
			}

			// ========== Arrows ==========
			g.setColor(Color.DARK_GRAY);
			int startArrow = getWidth() - widthArrow;
			int arrowH = getHeight() * 3 / 10, arrowH2 = arrowH / 2;
			int startY = getHeight() / 2 - arrowH - 5, startY2 = getHeight() / 2 + 5;

			for (int y = 0; y <= arrowH; y++) {
				if (tipIndex != 0)// Up
					g.drawLine(startArrow - y / 2, startY + y, startArrow + (y + 1) / 2, startY + y);
				if (tipIndex + 1 != tipsPlus.length) // Down
					g.drawLine(startArrow - arrowH2 + y / 2, startY2 + y, startArrow + arrowH2 - (y + 1) / 2,
							startY2 + y);
			}

		} else {
			g.setColor(borderColor);
			g.fillRoundRect(0, 0, total, total, total, total);
		}

		// ========== Mark ==========
		g.setColor(getBackground());
		g.fillOval(border, border, circleSize, circleSize);

		g.drawImage(img, border * 2, border * 2, circleSize - border * 2, circleSize - border * 2, null);
	}

	// =========================================================================================================================

	@Override
	public void click(MouseEvent e) {
		if (circle.contains(e.getPoint()))
			active = !active;

		setSize(active ? width : total, total);

		if (e.getX() > getWidth() - widthArrow - 20) {
			if (e.getY() > getHeight() / 2) {// Down
				if (tipIndex + 1 != tipsPlus.length) {
					tipIndex++;
					updateTip();
				}
			} else {// Up
				if (tipIndex != 0) {
					tipIndex--;
					updateTip();
				}
			}
		}
	}

	@Override
	public void resize() {
		updateTip();
	}

	// =========================================================================================================================

	public enum Mark {
		INTERROGATION, EXCLAMATION;
	}
}
