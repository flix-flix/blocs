package editor.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Arrays;

import data.id.ItemID;
import data.id.ItemTableClient;
import data.map.Cube;
import editor.ActionEditor;
import editor.Editor;
import environment.extendsData.CubeClient;
import graphicEngine.calcul.Engine;
import utils.FlixBlocksUtils;
import utils.panels.Menu;

public class MenuButtonEditor extends Menu {
	private static final long serialVersionUID = 8368480819248766526L;

	private Editor editor;
	private ActionEditor action;
	private Image img;

	private Engine engine;

	// ======================= Decor =========================
	private Font font = new Font("monospace", Font.BOLD, 14);
	private FontMetrics fm = getFontMetrics(font);

	private int borderSize = 5;

	// ======================= Wheel =========================
	private final static int NULL = -1;

	private int wheelStep = NULL;
	private int wheelMin = 0;
	private int wheelMax = 0;

	// ======================= Data =========================
	/** ITEM_COLOR : color */
	private int value = 0;
	/** ITEM_NAME : name */
	private String str = "";
	/**
	 * ITEM_NAME : name available
	 * 
	 * ITEM_ID : id available
	 */
	private boolean bool = true;

	// ======================= Select =========================
	private boolean selectable = false;
	private boolean selected = false;
	private ArrayList<MenuButtonEditor> group;

	// =========================================================================================================================

	public MenuButtonEditor(Editor editor, ActionEditor action) {
		this.editor = editor;
		this.action = action;

		if (hasImage())
			img = FlixBlocksUtils
					.getImage(editor.texturePack.getFolder() + "menu/editor/" + action.name().toLowerCase());
		else
			setMinimumSize(new Dimension(fm.stringWidth(getText()), 20));

		if (hasEngine()) {
			int itemID;
			switch (action) {
			case MINIATURE:
				itemID = ItemID.EDITOR_PREVIEW;
				break;

			default:
				itemID = ItemID.BORDER;
				break;
			}
			engine = new Engine(ItemTableClient.getCamera(itemID), new CubeClient(new Cube(itemID), editor.texturePack));
			engine.setBackground(Engine.NONE);
			update();
		}

		if (action == ActionEditor.ITEM_ID)
			bool = false;

		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (wheelStep == NULL)
					return;
				e.consume();

				int prev = wheelStep;

				// If needed to reach big values
				if (wheelMax >= 50)
					if (e.isShiftDown())
						wheelStep -= 100 * e.getWheelRotation();
					else if (e.isControlDown())
						wheelStep -= 10 * e.getWheelRotation();
					else
						wheelStep -= e.getWheelRotation();
				else
					wheelStep -= e.getWheelRotation();

				if (wheelStep > wheelMax)
					wheelStep = wheelMax;
				if (wheelStep < wheelMin)
					wheelStep = wheelMin;

				if (wheelStep == prev) {
					if (prev == wheelMax)
						;
					else if (prev == wheelMin)
						;
				} else
					editor.menuWheel(action);
			}
		});
	}

	// =========================================================================================================================

	@Override
	public void paintComponent(Graphics g) {
		// ===== Background =====
		g.setColor(hasImage() ? (selected ? Color.LIGHT_GRAY : Color.GRAY) : (selected ? Color.GRAY : Color.DARK_GRAY));
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

		// ===== Border =====
		g.setColor(hasImage() ? Color.GRAY : Color.DARK_GRAY);
		for (int i = 0; i < borderSize; i++)
			g.drawRect(i, i, getWidth() - 1 - 2 * i, getHeight() - 1 - 2 * i);

		// ===== Content =====
		if (hasImage())
			g.drawImage(img, 0, 0, getWidth(), getHeight(), null);

		else if (action == ActionEditor.ITEM_COLOR) {
			int x = 40;
			int y = 25;

			g.setColor(Color.LIGHT_GRAY);
			g.drawRect(getWidth() / 2 - x / 2 - 1, getHeight() / 2 - y / 2 - 1, x + 1, y + 1);
			g.setColor(new Color(value & 0xffffff));
			g.fillRect(getWidth() / 2 - x / 2, getHeight() / 2 - y / 2, x, y);

		} else { // Text
			g.setColor(Color.LIGHT_GRAY);
			if (action == ActionEditor.ITEM_ID || action == ActionEditor.ITEM_NAME)
				if (!bool)
					g.setColor(Color.RED);

			g.setFont(font);

			String text = str.isEmpty() ? getText() : str;

			int y = getHeight() / 2 + (int) (fm.getStringBounds(text, g).getHeight() / 2) - 3;

			g.drawString(text, getWidth() / 2 - fm.stringWidth(text) / 2, y);
		}
	}

	// =========================================================================================================================

	public void update() {
		if (hasEngine())
			img = engine.getImage(getWidth(), getHeight());
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

	public boolean hasEngine() {
		switch (action) {
		case MINIATURE:
		case EDIT_CUBE:
			return true;
		default:
			return false;
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

	public int getValue() {
		return value;
	}

	public void setValue(int x) {
		this.value = x;
	}

	public String getString() {
		return str;
	}

	public void setString(String str) {
		this.str = str;
	}

	public void setBool(boolean bool) {
		this.bool = bool;
	}

	public void reinit() {
		wheelStep = 0;

		value = 0;
		str = "";
		bool = true;

		if (action == ActionEditor.ITEM_ID)
			bool = false;
	}

	// =========================================================================================================================

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	public static void group(MenuButtonEditor... buttons) {
		ArrayList<MenuButtonEditor> list = new ArrayList<>(Arrays.asList(buttons));

		for (MenuButtonEditor button : buttons)
			button.group = list;
	}

	// =========================================================================================================================
	// Menu

	@Override
	public void click(MouseEvent e) {
		if (selectable) {
			if (selected)
				setSelected(false);
			else {
				if (group != null)
					for (MenuButtonEditor button : group)
						button.setSelected(false);
				setSelected(true);
			}
		}

		editor.menuClick(action);
		e.consume();
	}

	@Override
	public void resize() {
		update();
	}
}
