package editor.panels;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.JPanel;

import editor.ActionEditor;
import editor.Editor;
import editor.tips.TipCalk;
import editor.tips.TipEditor;
import editor.tips.TipPencil;
import environment.PanEnvironment;
import utils.panels.ClickListener;
import utils.panels.PanCol;
import utils.panels.PanGrid;
import utils.panels.popUp.PopUpConfirm;
import utilsBlocks.help.PanHelp;
import utilsBlocks.help.PanHelp.Mark;

public class PanEditor extends JPanel {
	private static final long serialVersionUID = -7092208608285186782L;

	private Editor editor;

	private PanEnvironment panel;

	// =============== Menu ===============
	private int menuWidth = 400;

	private PanCol menu;

	private ActionEditor[] _buttonsTop = { ActionEditor.EDIT_CUBE, ActionEditor.EDIT_MULTI_CUBE,
			ActionEditor.EDIT_MULTI_TEXTURE, ActionEditor.QUIT };
	private ActionEditor[] _buttonsAction = {
			// Line 1
			ActionEditor.ALONE, ActionEditor.DECOR, ActionEditor.PAINT, ActionEditor.FILL,
			// Line 2
			ActionEditor.SQUARE_SELECTION, ActionEditor.GRID, ActionEditor.MINIATURE, ActionEditor.SAVE,
			// Line 3
			ActionEditor.PLAYER_COLOR };
	private ActionEditor[] _buttonsItemID = { ActionEditor.ITEM_TAG, ActionEditor.ITEM_ID, ActionEditor.ITEM_COLOR,
			ActionEditor.ITEM_SAVE, ActionEditor.ITEM_CLEAR };

	private HashMap<ActionEditor, ButtonEditor> buttonsTop = new HashMap<>(), buttonsAction = new HashMap<>(),
			buttonsItemID = new HashMap<>();

	private PanGrid topActions, gridActions, gridItemID;

	public PanColor panColor;

	// =============== Help ===============
	private PanHelp<TipEditor> help;
	public PanHelp<TipCalk> helpCalk;
	public PanHelp<TipPencil> helpPencil;

	// =============== Pop-Up ===============
	private PopUpConfirm popUpExit, popUpSaveOnExistant;

	// =========================================================================================================================

	public PanEditor(Editor editor) {
		this.editor = editor;

		this.setLayout(null);

		// ========================================================================================
		// PopUp Exit

		popUpExit = new PopUpConfirm();
		popUpExit.setYamlKey("editor.pop_up.close");
		popUpExit.setVoile(new Color(90, 90, 90, 150));
		popUpExit.setBorder(8, Color.LIGHT_GRAY);

		popUpExit.setConfirmAction(new ClickListener() {
			@Override
			public void leftClick() {
				editor.fen.returnToMainMenu();
			}
		});

		popUpExit.setCancelAction(new ClickListener() {
			@Override
			public void leftClick() {
				editor.setPaused(false);
			}
		});

		this.add(popUpExit);

		// ========================================================================================
		// PopUp Save

		popUpSaveOnExistant = new PopUpConfirm();
		popUpSaveOnExistant.setYamlKey("editor.pop_up.save_on_existant");
		popUpSaveOnExistant.setVoile(new Color(90, 90, 90, 150));
		popUpSaveOnExistant.setBorder(8, Color.LIGHT_GRAY);

		popUpSaveOnExistant.setConfirmAction(new ClickListener() {
			@Override
			public void leftClick() {
				editor.saveTextureSaved();
				popUpSaveOnExistant.close();
			}
		});

		popUpSaveOnExistant.setCancelAction(new ClickListener() {
			@Override
			public void leftClick() {
				editor.setPaused(false);
			}
		});

		this.add(popUpSaveOnExistant);

		// ========================================================================================
		// Panels

		panel = editor.getPanel();
		panel.setLocation(menuWidth, 0);
		panel.setSize(getWidth() - menuWidth, getHeight());
		this.add(panel);

		menu = new PanCol();
		menu.setBounds(0, 0, menuWidth, getHeight());
		menu.setBorder(10, Color.GRAY);
		menu.setBackground(Color.LIGHT_GRAY);
		menu.setPadding(10);
		this.add(menu);

		// ========================================================================================
		// Help

		help = new PanHelp<>(Mark.INTERROGATION, 500, 80, 10, TipEditor.ZOOM);
		help.setLocation(25, 25);
		panel.add(help);

		helpCalk = new PanHelp<>(Mark.EXCLAMATION, 450, 60, 7, TipCalk.values()[0]);
		helpCalk.setBackground(new Color(0xff4068c4));
		helpCalk.setLocation(25, 25);
		helpCalk.setVisible(false);
		panel.add(helpCalk);

		helpPencil = new PanHelp<>(Mark.EXCLAMATION, 450, 60, 7, TipPencil.values()[0]);
		helpPencil.setBackground(new Color(0xff4068c4));
		helpPencil.setLocation(25, 25);
		helpPencil.setVisible(false);
		panel.add(helpPencil);

		// ========================================================================================
		// Top

		menu.addTop(topActions = new PanGrid(), 100);

		for (ActionEditor action : _buttonsTop) {
			buttonsTop.put(action, new ButtonEditor(editor, action));
			topActions.gridAdd(buttonsTop.get(action));
		}

		get(ActionEditor.EDIT_CUBE).setWIP();
		get(ActionEditor.EDIT_MULTI_CUBE).setWIP();
		get(ActionEditor.EDIT_MULTI_TEXTURE).setWIP();

		// ========================================================================================
		// Color

		menu.addBottom(panColor = new PanColor(editor), PanCol.WIDTH);
		panColor.setVisible(false);

		// ========================================================================================
		// ItemID

		menu.addBottom(gridItemID = new PanGrid(), 122);

		gridItemID.setCols(3);
		gridItemID.setRowHeight(50);
		gridItemID.setBackground(Color.GRAY);
		gridItemID.setBorder(5, Color.DARK_GRAY);
		gridItemID.setGridSpace(4);
		gridItemID.setGridPadding(PanGrid.GRID_SPACE);

		for (ActionEditor action : _buttonsItemID) {
			buttonsItemID.put(action, new ButtonEditor(editor, action));
			gridItemID.gridAdd(buttonsItemID.get(action));
		}

		get(ActionEditor.ITEM_TAG).setSelectable(true);
		get(ActionEditor.ITEM_ID).listenWheel();
		get(ActionEditor.ITEM_ID).setWheelMinMax(0, 999);
		get(ActionEditor.ITEM_ID).setWheelStep(0);

		// ========================================================================================
		// Actions

		menu.addTop(gridActions = new PanGrid(), PanCol.REMAINING);

		for (ActionEditor action : _buttonsAction) {
			buttonsAction.put(action, new ButtonEditor(editor, action));
			gridActions.gridAdd(buttonsAction.get(action));
		}

		get(ActionEditor.GRID).listenWheel();
		get(ActionEditor.GRID).setWheelStep(editor.getTextureSize());
		get(ActionEditor.GRID).setWheelMinMax(1, 16);
		get(ActionEditor.GRID).setSelectable(true);

		get(ActionEditor.SQUARE_SELECTION).setSelectable(true);
		get(ActionEditor.PAINT).setSelectable(true);
		get(ActionEditor.FILL).setSelectable(true);
		get(ActionEditor.PLAYER_COLOR).setSelectable(true);
		ButtonEditor.group(get(ActionEditor.SQUARE_SELECTION), get(ActionEditor.FILL), get(ActionEditor.PAINT),
				get(ActionEditor.PLAYER_COLOR));

		get(ActionEditor.ALONE).setWIP();
		get(ActionEditor.DECOR).setWIP();
		get(ActionEditor.SAVE).setWIP();
		get(ActionEditor.PLAYER_COLOR).setWIP();
	}

	// =========================================================================================================================

	public ButtonEditor get(ActionEditor action) {
		if (buttonsAction.containsKey(action))
			return buttonsAction.get(action);
		else if (buttonsItemID.containsKey(action))
			return buttonsItemID.get(action);
		else if (buttonsTop.containsKey(action))
			return buttonsTop.get(action);
		return null;
	}

	// =========================================================================================================================

	public void confirmReturnToMainMenu() {
		editor.setPaused(true);
		popUpExit.setVisible(true);
	}

	public void confirmSaveOnExistant() {
		editor.setPaused(true);
		popUpSaveOnExistant.setVisible(true);
	}

	// =========================================================================================================================

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);

		menu.setSize(menuWidth, height);
		panel.setSize(width - menuWidth, height);

		helpPencil.setBottomLeftCorner(25, getHeight() - 26 - help.getSize().height - 25);
		helpCalk.setBottomLeftCorner(25, getHeight() - 26 - help.getSize().height - 25);
		help.setBottomLeftCorner(25, getHeight() - 26);

		popUpExit.setSize(width, height);
		popUpSaveOnExistant.setSize(width, height);
	}
}
