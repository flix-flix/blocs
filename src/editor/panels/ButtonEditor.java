package editor.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import data.id.ItemID;
import data.id.ItemTableClient;
import data.map.Cube;
import editor.ActionEditor;
import editor.Editor;
import environment.extendsData.CubeClient;
import utils.Utils;
import utilsBlocks.ButtonBlocks;

public class ButtonEditor extends ButtonBlocks {
	private static final long serialVersionUID = 8368480819248766526L;

	private Editor editor;
	private ActionEditor action;

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

	// =========================================================================================================================

	public ButtonEditor(Editor editor, ActionEditor action) {
		this.editor = editor;
		this.action = action;

		setPadding(5);

		if (hasImage())
			setImage(Utils.getImage(
					ItemTableClient.getTexturePack().getFolder() + "menu/editor/" + action.name().toLowerCase()));

		else if (hasEngine()) {
			int itemID;
			switch (action) {
			case MINIATURE:
				itemID = ItemID.EDITOR_PREVIEW;
				break;

			default:
				itemID = ItemID.BORDER;
				break;
			}
			setModel(new CubeClient(new Cube(itemID)));
		}

		else {
			switch (action) {
			case ITEM_TAG:
			case ITEM_ID:

			case ITEM_SAVE:
			case ITEM_NEW:
			case ITEM_CLEAR:

			case VALID_COLOR:
				setText(ItemTableClient.getText("editor.buttons." + action.name().toLowerCase()));
				break;

			case SELECT_ALPHA:
				setText(wheelStep * 5 + "%");
				break;

			case ITEM_COLOR:
				break;

			default:
				Utils.debug("Missing " + action.name());
			}
			setFont(new Font("monospace", Font.BOLD, 14));
			setBackground(Color.DARK_GRAY);
			setSelectedColor(Color.GRAY);
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

				updateData();

				if (wheelStep == prev) {
					if (prev == wheelMax)
						;
					else if (prev == wheelMin)
						;
				} else
					editor.buttonWheel(action);

				repaint();
			}
		});
	}

	// =========================================================================================================================

	@Override
	public void paintCenter(Graphics g) {
		super.paintCenter(g);

		if (action == ActionEditor.ITEM_COLOR) {
			int x = 40;
			int y = 25;

			g.setColor(Color.LIGHT_GRAY);
			g.drawRect(getContentWidth() / 2 - x / 2 - 1, getContentHeight() / 2 - y / 2 - 1, x + 1, y + 1);
			g.setColor(new Color(value & 0xffffff));
			g.fillRect(getContentWidth() / 2 - x / 2, getContentHeight() / 2 - y / 2, x, y);
		}
	}

	// =========================================================================================================================

	public boolean hasImage() {
		switch (action) {
		case ITEM_CLEAR:
		case ITEM_COLOR:
		case ITEM_ID:
		case ITEM_TAG:
		case ITEM_NEW:
		case ITEM_SAVE:
		case SELECT_ALPHA:
		case VALID_COLOR:

		case EDIT_CUBE:
		case MINIATURE:
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

	// =========================================================================================================================
	// Wheel

	public int getWheelStep() {
		return wheelStep;
	}

	public void setWheelStep(int x) {
		wheelStep = x;
		updateData();
	}

	public void setWheelMinMax(int min, int max) {
		wheelMin = min;
		wheelMax = max;
	}

	// =========================================================================================================================
	// Data

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
		if (action == ActionEditor.ITEM_TAG)
			setText(str.equals("") ? ItemTableClient.getText("editor.buttons.item_tag") : str);
		repaint();
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

	public void updateData() {
		switch (action) {
		case ITEM_ID:
		case ITEM_TAG:
			setForeground(bool ? Color.WHITE : Color.RED);
			break;

		default:
			setForeground(Color.WHITE);
			break;
		}
		if (action == ActionEditor.SELECT_ALPHA)
			setText((wheelStep * 5) + " %");

		if (action == ActionEditor.ITEM_ID)
			setText("" + wheelStep);

		repaint();
	}

	// =========================================================================================================================
	// FButton

	@Override
	public void eventClick() {
		editor.buttonClick(action);
		repaint();
	}
}
