package utilsBlocks.help;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import data.id.ItemTableClient;
import utils.Utils;
import utils.panels.FPanel;

public class PanHelp<T extends Enum<?> & Tip<T>> extends FPanel {
	private static final long serialVersionUID = -3108013483466382742L;

	private Font font = new Font("monospace", Font.BOLD, 15);
	private FontMetrics fm = getFontMetrics(font);

	// =============== Size ===============
	Mark mark;
	private int width, circleSize, border, total;

	// =============== ===============
	private Image img;

	private boolean active = false;

	private Ellipse2D circle;

	// =============== Tip ===============
	private T tip;
	private String tipText;
	private ArrayList<String> tipLines = null;

	// =============== Arrow ===============
	int widthArrow = 30;

	// =============== Color ===============
	Color borderColor = Color.GRAY;

	// =========================================================================================================================

	public PanHelp(Mark mark, int width, int circleSize, int border, T tip) {
		this.mark = mark;
		this.width = width;
		this.circleSize = circleSize;
		this.border = border;

		this.total = circleSize + border * 2;

		this.setOpaque(false);

		setSize(total, total);
		setTip(tip);

		circle = new Ellipse2D.Double(border, border, circleSize, circleSize);

		img = Utils.getImage("static/" + (mark == Mark.INTERROGATION ? "interrogationMark" : "exclamationMark"));

		setBackground(Color.LIGHT_GRAY);
		setForeground(Color.WHITE);
	}

	// =========================================================================================================================

	public void updateTip() {

		tipText = ItemTableClient.getTip(tip);
		tipLines = Utils.getLines(tipText, fm, getWidth() - 100 - widthArrow - 20);
	}

	public void setTip(T tip) {
		this.tip = tip;
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
			g.setColor(getForeground());
			g.setFont(font);
			int lineH = (int) fm.getStringBounds("A", g).getHeight();

			for (int i = 0; i < tipLines.size(); i++) {
				int _y = getHeight() / 2 - lineH * tipLines.size() / 4 + lineH * i;
				g.drawString(tipLines.get(i), total, _y);
			}

			// ========== Arrows ==========
			g.setColor(Color.DARK_GRAY);
			int startArrow = getWidth() - widthArrow;
			int arrowH = getHeight() * 3 / 10, arrowH2 = arrowH / 2;
			int startY = getHeight() / 2 - arrowH - 5, startY2 = getHeight() / 2 + 5;

			for (int y = 0; y <= arrowH; y++) {
				if (tip.ordinal() != 0)
					g.drawLine(startArrow - y / 2, startY + y, startArrow + (y + 1) / 2, startY + y);
				if (tip.ordinal() + 1 != tip._values().length)
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

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	// =========================================================================================================================

	@SuppressWarnings("unchecked")
	@Override
	public void click(MouseEvent e) {
		if (circle.contains(e.getPoint()))
			active = !active;

		setSize(active ? width : total, total);

		if (e.getX() > getWidth() - widthArrow - 20) {
			if (e.getY() > getHeight() / 2) {// Down
				if (tip.ordinal() + 1 != tip._values().length)
					tip = (T) tip.next();
			} else {
				if (tip.ordinal() != 0)
					tip = (T) tip.previous();
			}
			updateTip();
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
