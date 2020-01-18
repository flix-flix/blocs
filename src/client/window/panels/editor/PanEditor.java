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

		buttonsTop.get(ActionEditor.EDITOR).setSelected(true);

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

		buttonsItemID.get(ActionEditor.ITEM_ID).setWheelMinMax(0, 999);

		// ========================================================================================
		// Actions

		menu.addTop(gridActions = new MenuGrid(), MenuCol.REMAINING);

		for (ActionEditor action : _buttonsAction) {
			buttonsAction.put(action, new MenuButtonEditor(editor, action));
			gridActions.addMenu(buttonsAction.get(action));
		}

		buttonsAction.get(ActionEditor.GRID).setWheelStep(editor.getTextureSize());
		buttonsAction.get(ActionEditor.GRID).setWheelMinMax(1, 16);
		buttonsAction.get(ActionEditor.GRID).setSelectable(true);

		buttonsAction.get(ActionEditor.ROTATE).setSelectable(true);

		buttonsAction.get(ActionEditor.PAINT).setSelectable(true);
		buttonsAction.get(ActionEditor.FILL).setSelectable(true);
		buttonsAction.get(ActionEditor.PLAYER_COLOR).setSelectable(true);
		MenuButtonEditor.group(buttonsAction.get(ActionEditor.FILL), buttonsAction.get(ActionEditor.PAINT),
				buttonsAction.get(ActionEditor.PLAYER_COLOR));
	}

	// =========================================================================================================================

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		menu.setBounds(x, y, menuWidth, height);
	}
}
