package editor.panels;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.JPanel;

import editor.ActionEditor;
import editor.Editor;
import editor.panels.MenuHelp.Mark;
import editor.panels.MenuHelp.TipCalk;
import environment.PanEnvironment;
import utils.panels.MenuCol;
import utils.panels.MenuGrid;

public class PanEditor extends JPanel {
	private static final long serialVersionUID = -7092208608285186782L;

	private PanEnvironment panel;

	// ======================= Menu =========================
	private int menuWidth = 400;

	private MenuCol menu = new MenuCol();

	private ActionEditor[] _buttonsTop = { ActionEditor.EDIT_CUBE, ActionEditor.EDIT_MULTI_CUBE,
			ActionEditor.EDIT_MULTI_TEXTURE, ActionEditor.EDITOR };
	private ActionEditor[] _buttonsAction = {
			// Line 1
			ActionEditor.ALONE, ActionEditor.DECOR, ActionEditor.PAINT, ActionEditor.FILL,
			// Line 2
			ActionEditor.SQUARE_SELECTION, ActionEditor.GRID, ActionEditor.MINIATURE, ActionEditor.SAVE,
			// Line 3
			ActionEditor.PLAYER_COLOR };
	private ActionEditor[] _buttonsItemID = { ActionEditor.ITEM_NAME, ActionEditor.ITEM_ID, ActionEditor.ITEM_COLOR,
			ActionEditor.ITEM_SAVE, ActionEditor.ITEM_NEW, ActionEditor.ITEM_CLEAR };

	private HashMap<ActionEditor, MenuButtonEditor> buttonsTop = new HashMap<>(), buttonsAction = new HashMap<>(),
			buttonsItemID = new HashMap<>();

	private MenuGrid topActions, gridActions, gridItemID;

	public MenuColor panColor;

	private MenuHelp help;
	public MenuHelp helpTool;

	// =========================================================================================================================

	public PanEditor(Editor editor) {
		this.setLayout(null);
		this.setOpaque(false);

		panel = editor.getPanel();
		panel.setLocation(menuWidth, 0);
		panel.setSize(getWidth() - menuWidth, getHeight());
		add(panel);

		help = new MenuHelp(Mark.INTERROGATION, 80, 10);
		help.setBounds(25, 25, 500, 100);
		panel.add(help);

		helpTool = new MenuHelp(Mark.EXCLAMATION, 60, 7);
		helpTool.setBackground(new Color(0xff4068c4));
		helpTool.setBounds(25, 25, 450, 74);
		helpTool.setTip(TipCalk.values()[0]);
		helpTool.setVisible(false);
		panel.add(helpTool);

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

		menu.addBottom(gridItemID = new MenuGrid(), 122);

		gridItemID.setCols(3);
		gridItemID.setRowHeight(50);
		gridItemID.setBackground(Color.GRAY);
		gridItemID.setBorder(5, Color.DARK_GRAY);
		gridItemID.setGridSpace(4);
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

		get(ActionEditor.SQUARE_SELECTION).setSelectable(true);
		get(ActionEditor.PAINT).setSelectable(true);
		get(ActionEditor.FILL).setSelectable(true);
		get(ActionEditor.PLAYER_COLOR).setSelectable(true);
		MenuButtonEditor.group(get(ActionEditor.SQUARE_SELECTION), get(ActionEditor.FILL), get(ActionEditor.PAINT),
				get(ActionEditor.PLAYER_COLOR));
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
	public void setSize(int width, int height) {
		super.setSize(width, height);

		menu.setSize(menuWidth, height);
		panel.setSize(width - menuWidth, height);

		helpTool.setLocation(25, getHeight() - 26 - helpTool.getSize().height);
	}
}
