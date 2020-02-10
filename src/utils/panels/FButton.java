package utils.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class FButton extends FPanel {
	private static final long serialVersionUID = -8655956676553570245L;

	// =============== Listener ===============
	private ClickListener listener;

	// =============== Select ===============
	private boolean selectable = false;
	private boolean selected = false;
	private ArrayList<FButton> group;

	private Color selectedColor = Color.LIGHT_GRAY;

	// =============== Image ===============
	private Image img = null;
	/** How to draw the image */
	private int imgType = FILL;

	public static final int FILL = 0;
	public static final int KEEP_RATIO = 1;

	// =============== Text ===============
	private String text = null;

	private Font font = new Font("monospace", Font.BOLD, 14);
	private FontMetrics fm = getFontMetrics(font);

	private Color textBackground = null;

	private int textX = CENTERED, textY = CENTERED;
	private int textXRelativeTo = ABSOLUTE, textYRelativeTo = ABSOLUTE;

	// =============== Static ===============
	public static final int CENTERED = -10_000;

	public static final int ABSOLUTE = 0;
	public static final int BOTTOM = 1;

	// =========================================================================================================================

	public FButton() {
		setBackground(Color.GRAY);
		setForeground(Color.WHITE);
	}

	// =========================================================================================================================

	@Override
	/** Paint the panel except margin, border and padding areas */
	protected void paintCenter(Graphics g) {
		super.paintCenter(g);

		int w = getContentWidth();
		int h = getContentHeight();

		// Fill Center in different color if selected
		if (selected) {
			g.setColor(selectedColor);
			g.fillRect(0, 0, w, h);
		}

		if (img != null) {
			int x = 0, y = 0;
			int imgW = w, imgH = h;

			if (imgType == KEEP_RATIO) {
				imgH = (int) (w * ((double) img.getHeight(null) / img.getWidth(null)));
				y = (h - imgH) / 2;
			}

			g.drawImage(img, x, y, imgW, imgH, null);
		}

		if (text != null) {
			Rectangle2D r = fm.getStringBounds(text, g);

			g.setFont(font);

			String text = this.text;

			if (fm.stringWidth(text) > getWidth() - 5) {
				while (fm.stringWidth(text) > getWidth() - 20)
					text = text.substring(0, text.length() - 2);

				// if (!text.equals(this.text))
				text += "...";
			}

			int x = 0, y = 0;
			if (textXRelativeTo == ABSOLUTE)
				if (textX == CENTERED)
					x = w / 2 - fm.stringWidth(text) / 2 - 2;// -2 correct the space lost by the white line

			if (textYRelativeTo == ABSOLUTE) {
				if (textY == CENTERED)
					y = h / 2 + (int) (fm.getStringBounds(text, g).getHeight() / 2) - 3;
			} else if (textYRelativeTo == BOTTOM)
				y = h - textY;

			g.setColor(textBackground);
			g.fillRect(x, y - (int) r.getHeight() + 3, (int) r.getWidth(), (int) r.getHeight());

			g.setColor(getForeground());
			g.drawString(text, x, y);
		}
	}

	// =========================================================================================================================

	public void setClickListener(ClickListener listener) {
		this.listener = listener;
	}

	// =========================================================================================================================

	public void setImage(Image img, int type) {
		this.img = img;
		this.imgType = type;
	}

	public void setImage(Image img) {
		setImage(img, FILL);
	}

	// =========================================================================================================================
	// Text

	public void setText(String text) {
		this.text = text;
		this.setToolTipText(text);
	}

	public void setFont(Font font) {
		this.font = font;
		fm = getFontMetrics(font);
	}

	public void setTextBackground(Color color) {
		this.textBackground = color;
	}

	public void setTextYLocation(int y, int relativeTo) {
		textY = y;
		textYRelativeTo = relativeTo;
	}

	// =========================================================================================================================

	public void setSelectedColor(Color color) {
		this.selectedColor = color;
	}

	// =========================================================================================================================
	// Select

	public boolean isSelected() {
		return selected;
	}

	/**
	 * Select or unselect this button<br>
	 * If select status changed -> call repaint()
	 */
	public void setSelected(boolean selected) {
		if (this.selected != selected) {
			this.selected = selected;
			repaint();
		} else
			this.selected = selected;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	// =========================================================================================================================
	// Group

	public static void _group(ArrayList<FButton> list) {
		for (FButton button : list)
			button.group = list;
	}

	/**
	 * Link FButton together<br>
	 * If one is selected -> deselect others (like radio buttons)
	 */
	public static void group(Collection<? extends FButton> list) {
		_group(new ArrayList<>(list));
	}

	/**
	 * Link FButton together<br>
	 * If one is selected -> deselect others (like radio buttons)
	 */
	public static void group(FButton... buttons) {
		_group(new ArrayList<>(Arrays.asList(buttons)));
	}

	public void unselectAll() {
		if (group != null)
			for (FButton button : group)
				button.setSelected(false);
	}

	// =========================================================================================================================

	@Override
	public void click(MouseEvent e) {
		if (selectable) {
			if (selected)
				setSelected(false);
			else {
				unselectAll();
				setSelected(true);
			}
		}
		if (listener != null)
			listener.leftClick();
	}

	@Override
	public void resize() {
		super.resize();
	}
}
