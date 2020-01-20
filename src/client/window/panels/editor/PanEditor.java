package client.window.panels.editor;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.JPanel;

import client.editor.ActionEditor;
import client.editor.Editor;
import client.window.panels.menus.MenuCol;
import client.window.panels.menus.MenuGrid;

public class PanEditor extends JPanel {
	private static final long serialVersionUID = -7092208608285186782L;

	Editor editor;

	// ======================= Menu =========================
	int menuWidth = 400;

	MenuCol menu = new MenuCol();

	ActionEditor[] _buttonsTop = { ActionEditor.EDIT_CUBE, ActionEditor.EDIT_MULTI_CUBE,
			ActionEditor.EDIT_MULTI_TEXTURE, ActionEditor.EDITOR };
	HashMap<ActionEditor, MenuButtonEditor> buttonsTop = new HashMap<>();

	ActionEditor[] _buttonsAction = { ActionEditor.ALONE, ActionEditor.DECOR, ActionEditor.PAINT, ActionEditor.FILL,
			ActionEditor.GRID, ActionEditor.MINIATURE, ActionEditor.SAVE, ActionEditor.PLAYER_COLOR,
			ActionEditor.ROTATE };
	public HashMap<ActionEditor, MenuButtonEditor> buttonsAction = new HashMap<>();

	ActionEditor[] _buttonsItemID = { ActionEditor.ITEM_NAME, ActionEditor.ITEM_ID, ActionEditor.ITEM_COLOR,
			ActionEditor.ITEM_SAVE, ActionEditor.ITEM_NEW, ActionEditor.ITEM_CLEAR };
	public HashMap<ActionEditor, MenuButtonEditor> buttonsItemID = new HashMap<>();

	MenuGrid topActions;
	MenuGrid gridActions;
	MenuGrid gridItemID;

	public MenuColor panColor;

	// =========================================================================================================================

	public PanEditor(Editor editor) {
		this.editor = editor;

		this.setLayout(null);
		this.setOpaque(false);

		menu.setBounds(0, 0, menuWidth, getHeight());
		this.add(menu);

		// ========================================================================================
		// Top

		menu.addTop(topActions = new MenuGrid(), 100);

		for (ActionEditor action : _buttonsTop) {
			buttonsTop.put(action, new MenuButtonEditor(editor, action));
			topActions.addMenu(buttonsTop.get(action));
		}

		get(ActionEditor.EDITOR).setSelected(true);

		// ========================================================================================
		// Color

		menu.addBottom(panColor = new MenuColor(editor), MenuCol.WIDTH);

		// ========================================================================================
		// ItemID

		menu.addBottom(gridItemID = new MenuGrid(), 110);

		gridItemID.setCols(3);
		gridItemID.setRowHeight(50);
		gridItemID.setBackground(Color.GRAY);
		gridItemID.setBorder(5, Color.DARK_GRAY);
		gridItemID.setPadding(MenuGrid.GRID_SPACE);

		for (ActionEditor action : _buttonsItemID) {
			buttonsItemID.put(action, new MenuButtonEditor(editor, action));
			gridItemID.addMenu(buttonsItemID.get(action));
		}

		get(ActionEditor.ITEM_NAME).setSelectable(true);
		get(ActionEditor.ITEM_ID).setWheelMinMax(0, 999);
		get(ActionEditor.ITEM_ID).setWheelStep(0);

		// ========================================================================================
		// Actions

		menu.addTop(gridActions = new MenuGrid(), MenuCol.REMAINING);

		for (ActionEditor action : _buttonsAction) {
			buttonsAction.put(action, new MenuButtonEditor(editor, action));
			gridActions.addMenu(buttonsAction.get(action));
		}

		get(ActionEditor.GRID).setWheelStep(editor.getTextureSize());
		get(ActionEditor.GRID).setWheelMinMax(1, 16);
		get(ActionEditor.GRID).setSelectable(true);

		get(ActionEditor.ROTATE).setSelectable(true);

		get(ActionEditor.PAINT).setSelectable(true);
		get(ActionEditor.FILL).setSelectable(true);
		get(ActionEditor.PLAYER_COLOR).setSelectable(true);
		MenuButtonEditor.group(get(ActionEditor.FILL), get(ActionEditor.PAINT), get(ActionEditor.PLAYER_COLOR));
	}

	// =========================================================================================================================

	public MenuButtonEditor get(ActionEditor action) {
		if (buttonsAction.containsKey(action))
			return buttonsAction.get(action);
		else if (buttonsItemID.containsKey(action))
			return buttonsItemID.get(action);
		else if (buttonsTop.containsKey(action))
			return buttonsTop.get(action);
		return null;
	}

	// =========================================================================================================================

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		menu.setBounds(x, y, menuWidth, height);
	}
}
