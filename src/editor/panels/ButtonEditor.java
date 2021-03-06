package editor.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import data.id.ItemID;
import data.id.ItemTable;
import data.id.ItemTableClient;
import editor.ActionEditor;
import editor.EditorManager;
import utils.Utils;
import utilsBlocks.ButtonBlocks;

public class ButtonEditor extends ButtonBlocks {
	private static final long serialVersionUID = 8368480819248766526L;

	private EditorManager editor;
	private ActionEditor action;

	// =============== Wheel ===============
	private final static int NULL = -1;

	private int wheelStep = NULL;
	private int wheelMin = 0;
	private int wheelMax = 0;

	// =============== Data ===============
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

	// =============== Unsaved ===============
	private boolean saved = true;;
	private static Image notSavedImg = Utils
			.getImage(ItemTableClient.getTexturePack().getFolder() + "menu/editor/not_saved");

	// =========================================================================================================================

	public ButtonEditor(EditorManager editor, ActionEditor action) {
		this.editor = editor;
		this.action = action;

		setPadding(5);

		if (hasImage())
			setImage(getImage(action));

		else if (hasEngine()) {
			int itemID;
			switch (action) {
			case MINIATURE_CUBE_TEXTURE:
				itemID = ItemID.EDITOR_PREVIEW;
				break;
			case MINIATURE_MULTICUBE:
				setEmptyImage(getImage(action));
				itemID = ItemID.EDITOR_PREVIEW_MULTI;
				break;

			case EDIT_CUBE:
				itemID = ItemID.BORDER;
				break;
			case EDIT_MULTI_CUBE:
				itemID = ItemID.TREE;
				break;
			default:
				itemID = ItemID.GRASS;
				break;
			}
			setModel(ItemTable.create(itemID));
		}

		else {
			switch (action) {
			case ITEM_TAG:
			case ITEM_ID:

			case ITEM_SAVE:
			case ITEM_CLEAR:
				setText(ItemTableClient.getText("editor.buttons." + action.name().toLowerCase()));
				break;

			case SELECT_ALPHA:
				setPadding(0);
				setBorder(2, Color.DARK_GRAY);
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

			// TODO [FIX] ITEM_TAG doesn't appear red when mouse on it
			if (action != ActionEditor.ITEM_ID)
				setInColor(Color.LIGHT_GRAY, Color.DARK_GRAY, Color.DARK_GRAY);

			if (action == ActionEditor.SELECT_ALPHA)
				setInColor(Color.LIGHT_GRAY, Color.DARK_GRAY, Color.DARK_GRAY);
		}
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		int h = getHeight();
		if (!saved)
			g.drawImage(notSavedImg, h  / 2, h  / 2, h / 2, h / 2, null);
	}

	@Override
	public void paintCenter(Graphics g) {
		super.paintCenter(g);

		int h = getContentHeight();

		if (action == ActionEditor.ITEM_COLOR) {
			int x = 40;
			int y = 25;

			g.setColor(Color.LIGHT_GRAY);
			g.drawRect(getContentWidth() / 2 - x / 2 - 1, h / 2 - y / 2 - 1, x + 1, y + 1);
			g.setColor(new Color(value & 0xffffff));
			g.fillRect(getContentWidth() / 2 - x / 2, h / 2 - y / 2, x, y);
		}

	}

	// =========================================================================================================================

	public void listenWheel() {
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

	public boolean hasImage() {
		if (hasEngine())
			return false;

		switch (action) {
		case ITEM_CLEAR:
		case ITEM_COLOR:
		case ITEM_ID:
		case ITEM_TAG:
		case ITEM_SAVE:
		case SELECT_ALPHA:
			return false;

		default:
			return true;
		}
	}

	public boolean hasEngine() {
		switch (action) {
		case MINIATURE_CUBE_TEXTURE:
		case MINIATURE_MULTICUBE:
		case EDIT_CUBE:
		case EDIT_MULTI_CUBE:
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
		updateData();
	}

	public void reinit() {
		wheelStep = 0;

		value = 0;
		str = "";
		bool = true;

		if (action == ActionEditor.ITEM_ID)
			bool = false;
	}

	public void setSaved(boolean saved) {
		this.saved = saved;
		repaint();
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

	private Image getImage(ActionEditor action) {
		return Utils
				.getImage(ItemTableClient.getTexturePack().getFolder() + "menu/editor/" + action.name().toLowerCase());
	}

	// =========================================================================================================================
	// FButton

	@Override
	public void eventClick() {
		editor.action(action);
		repaint();
	}
}
