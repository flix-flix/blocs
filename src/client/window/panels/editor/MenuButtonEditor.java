package client.window.panels.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import client.window.panels.menus.Menu;
import utils.FlixBlocksUtils;

public class MenuButtonEditor extends Menu {
	private static final long serialVersionUID = 8368480819248766526L;

	Font font = new Font("monospace", Font.BOLD, 14);
	FontMetrics fm = getFontMetrics(font);

	PanEditor editor;
	ActionEditor action;
	Image img;

	private final static int NULL = -1;

	int wheelStep = NULL;
	int wheelMin = 0;
	int wheelMax = 0;

	int value = 0;
	String str = null;

	// =========================================================================================================================

	public MenuButtonEditor(PanEditor editor, ActionEditor action) {
		this.editor = editor;
		this.action = action;

		if (hasImage())
			img = FlixBlocksUtils.getImage("menu/editor/" + action.name().toLowerCase());
		else
			setMinimumSize(new Dimension(fm.stringWidth(getText()), 20));

		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.isControlDown())
					wheelStep -= 10 * e.getWheelRotation();
				else if (e.isShiftDown())
					wheelStep -= 100 * e.getWheelRotation();
				else
					wheelStep -= e.getWheelRotation();

				if (wheelStep > wheelMax)
					wheelStep = wheelMax;
				if (wheelStep < wheelMin)
					wheelStep = wheelMin;

				editor.wheel(action);
			}
		});
	}

	// =========================================================================================================================

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(hasImage() ? Color.GRAY : Color.DARK_GRAY);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

		if (hasImage())
			g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
		else if (action == ActionEditor.ITEM_COLOR) {

			int x = 40;
			int y = 25;

			g.setColor(Color.LIGHT_GRAY);
			g.drawRect(getWidth() / 2 - x / 2 - 1, getHeight() / 2 - y / 2 - 1, x + 1, y + 1);
			g.setColor(new Color(value));
			g.fillRect(getWidth() / 2 - x / 2, getHeight() / 2 - y / 2, x, y);
		} else {
			g.setColor(Color.LIGHT_GRAY);
			g.setFont(font);

			String text = str == null ? getText() : str;

			int y = getHeight() / 2 + (int) (fm.getStringBounds(text, g).getHeight() / 2) - 3;

			g.drawString(text, getWidth() / 2 - fm.stringWidth(text) / 2, y);
		}
	}

	// =========================================================================================================================

	public boolean hasImage() {
		switch (action) {
		case ITEM_CLEAR:
		case ITEM_COLOR:
		case ITEM_ID:
		case ITEM_NAME:
		case ITEM_NEW:
		case ITEM_SAVE:
		case SELECT_ALPHA:
		case VALID_COLOR:
			return false;
		default:
			return true;
		}
	}

	public String getText() {
		switch (action) {
		// ========== Item ==========
		case ITEM_COLOR:
			return "COLOR";
		case ITEM_ID:
			return wheelStep == NULL ? "ID" : ("" + wheelStep);
		case ITEM_NAME:
			return "ItemID";

		case ITEM_SAVE:
			return "SAVE";
		case ITEM_NEW:
			return "NEW";
		case ITEM_CLEAR:
			return "CLEAR";
		// ========== Color ==========
		case SELECT_ALPHA:
			return wheelStep * 5 + "%";
		case VALID_COLOR:
			return "Select";
		default:
			return "ERROR : Text missing";
		}
	}

	// =========================================================================================================================

	public int getWheelStep() {
		return wheelStep;
	}

	public void setWheelStep(int x) {
		wheelStep = x;
	}

	public void setWheelMinMax(int min, int max) {
		wheelMin = min;
		wheelMax = max;
	}

	// =========================================================================================================================

	public void setValue(int x) {
		this.value = x;
	}

	public int getValue() {
		return value;
	}

	public void addChar(char c) {
		if (str == null)
			str = "" + c;
		else
			str += c;
	}

	public void delChar() {
		if (str.length() >= 1)
			str = str.substring(0, str.length() - 1);
	}

	public void clearString() {
		str = null;
	}

	public String getString() {
		return str;
	}

	// =========================================================================================================================
	// Menu

	@Override
	public void click(MouseEvent e) {
		editor.clicked(action);
	}

	@Override
	public void resize() {
	}
}