package editor.panels;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import data.id.ItemTable;
import data.id.ItemTableClient;
import data.map.Cube;
import editor.ActionEditor;
import editor.EditorCubeTexture;
import editor.EditorManager;
import environment.PanEnvironment;
import utils.panels.ClickListener;
import utils.panels.PanCard;
import utils.panels.PanCol;
import utils.panels.PanGrid;
import utils.panels.PanHelp;
import utils.panels.PanHelp.Mark;
import utils.panels.popUp.PopUpConfirm;
import utilsBlocks.ButtonBlocks;
import utilsBlocks.ButtonCube;
import utilsBlocks.Tip;

public class PanEditor extends JPanel {
	private static final long serialVersionUID = -7092208608285186782L;

	private EditorManager editor;

	private PanEnvironment panel;

	// =============== Menu ===============
	public int menuWidth = 400;

	private PanCol menu;
	public PanCard cardsActions;

	private ActionEditor[] _buttonsTop = { ActionEditor.EDIT_CUBE_TEXTURE, ActionEditor.EDIT_MULTI_CUBE,
			ActionEditor.EDIT_MULTI_TEXTURE, ActionEditor.EDIT_CUBE, ActionEditor.CLOSE_EDITOR };

	private ActionEditor[] _buttonsItemID = { ActionEditor.ITEM_TAG, ActionEditor.ITEM_ID, ActionEditor.ITEM_COLOR,
			ActionEditor.ITEM_SAVE, ActionEditor.ITEM_CLEAR };

	private HashMap<ActionEditor, ButtonEditor> buttonsTop = new HashMap<>(), buttonsItemID = new HashMap<>();
	private PanGrid topActions, gridItemID;

	// =============== Cube Texture ===============
	private ActionEditor[] _buttonsCubeTexture = {
			// Line 1
			ActionEditor.ALONE, ActionEditor.DECOR, ActionEditor.PAINT, ActionEditor.FILL,
			// Line 2
			ActionEditor.SQUARE_SELECTION, ActionEditor.GRID, ActionEditor.MINIATURE_CUBE_TEXTURE, ActionEditor.SAVE,
			// Line 3
			ActionEditor.PLAYER_COLOR };

	private PanGrid gridCubeTexture;
	private HashMap<ActionEditor, ButtonEditor> buttonsCubeTexture = new HashMap<>();

	// =============== Multi-Cubes ===============
	private ActionEditor[] _buttonsMultiCubes = { ActionEditor.DELETE_CUBE, ActionEditor.MINIATURE_MULTICUBE,
			ActionEditor.SAVE };
	private PanGrid gridMultiCubes;
	private HashMap<ActionEditor, ButtonEditor> buttonsMultiCubes = new HashMap<>();

	// =============== Help ===============
	private PanHelp helpGeneral;
	public PanHelp helpTool;

	// =============== Square ===============
	public static final String COLOR = "Color";
	public static final String CUBES = "Cubes";
	public PanCard cardsSquare;
	public PanColor panColor;

	public PanGrid gridCubes;
	public ArrayList<ButtonBlocks> buttonsCubes;

	// =============== Pop-Up ===============
	private PopUpConfirm popUpExit, popUpSaveOnExistant;

	// =========================================================================================================================

	public PanEditor(EditorManager editor) {
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
				((EditorCubeTexture) editor.editors.get(ActionEditor.EDIT_CUBE_TEXTURE)).saveTextureSaved();
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

		helpGeneral = new PanHelp(ItemTableClient.getTips(Tip.EDITOR_GLOBAL), Mark.INTERROGATION, 600, 80, 10);
		helpGeneral.setLocation(25, 25);
		panel.add(helpGeneral);

		helpTool = new PanHelp(ItemTableClient.getTips(Tip.EDITOR_CUBE_TEXTURE), Mark.EXCLAMATION, 600, 60, 7);
		helpTool.setBackground(new Color(0xff4068c4));
		helpTool.setLocation(25, 25);
		panel.add(helpTool);

		// ========================================================================================
		// Top

		menu.addTop(topActions = new PanGrid(), 75);
		topActions.setCols(5);

		for (ActionEditor action : _buttonsTop) {
			buttonsTop.put(action, new ButtonEditor(editor, action));
			topActions.gridAdd(buttonsTop.get(action));
		}

		get(ActionEditor.EDIT_CUBE_TEXTURE).setSelectable(true, false);
		get(ActionEditor.EDIT_MULTI_CUBE).setSelectable(true, false);
		// get(ActionEditor.EDIT_MULTI_TEXTURE).setSelectable(true);
		// get(ActionEditor.EDIT_CUBE).setSelectable(true);

		get(ActionEditor.EDIT_CUBE_TEXTURE).setSelected(true);

		ButtonEditor.group(get(ActionEditor.EDIT_CUBE_TEXTURE), get(ActionEditor.EDIT_MULTI_CUBE),
				get(ActionEditor.EDIT_MULTI_TEXTURE), get(ActionEditor.EDIT_CUBE));

		get(ActionEditor.EDIT_CUBE).setWIP();
		get(ActionEditor.EDIT_MULTI_TEXTURE).setWIP();

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
		// Square

		menu.addBottom(cardsSquare = new PanCard(), PanCol.WIDTH);
		cardsSquare.put(COLOR, panColor = new PanColor(editor));
		cardsSquare.put(CUBES, gridCubes = new PanGrid());

		buttonsCubes = new ArrayList<>();

		for (int itemID : ItemTable.getItemIDList()) {
			if (ItemTable.isDevelopment(itemID))
				continue;

			Cube cube = ItemTable.create(itemID);
			if (cube.multicube != null)
				continue;

			ButtonBlocks button = new ButtonCube(cube);

			button.setClickListener(new ClickListener() {
				@Override
				public void leftClick() {
					editor.editor.clickCube(cube);
				}
			});

			buttonsCubes.add(button);
			gridCubes.gridAdd(button);
		}

		ButtonBlocks.group(buttonsCubes);
		cardsSquare.setBackground(Color.LIGHT_GRAY);
		cardsSquare.hide();

		// ========================================================================================
		// Cards Actions

		menu.addTop(cardsActions = new PanCard(), PanCol.REMAINING);

		// ========================================================================================
		// Multi-Cubes

		cardsActions.put(ActionEditor.EDIT_MULTI_CUBE.name(), gridMultiCubes = new PanGrid());

		for (ActionEditor action : _buttonsMultiCubes) {
			buttonsMultiCubes.put(action, new ButtonEditor(editor, action));
			gridMultiCubes.gridAdd(buttonsMultiCubes.get(action));
		}

		buttonsMultiCubes.get(ActionEditor.DELETE_CUBE).setSelectable(true);
		buttonsMultiCubes.get(ActionEditor.MINIATURE_MULTICUBE).setSelectable(true);
		ButtonCube.group(get(ActionEditor.DELETE_CUBE), get(ActionEditor.MINIATURE_MULTICUBE));
		buttonsMultiCubes.get(ActionEditor.SAVE).setWIP();

		// ========================================================================================
		// Cube texture

		cardsActions.put(ActionEditor.EDIT_CUBE_TEXTURE.name(), gridCubeTexture = new PanGrid());

		for (ActionEditor action : _buttonsCubeTexture) {
			buttonsCubeTexture.put(action, new ButtonEditor(editor, action));
			gridCubeTexture.gridAdd(buttonsCubeTexture.get(action));
		}

		get(ActionEditor.GRID).listenWheel();
		// TODO 3 -> editor.getTextureSize()
		get(ActionEditor.GRID).setWheelStep(3);
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
		get(ActionEditor.PLAYER_COLOR).setWIP();

		// ========================================================================================

		cardsActions.show(ActionEditor.EDIT_CUBE_TEXTURE.name());
	}

	// =========================================================================================================================

	public ButtonEditor get(ActionEditor action) {
		if (buttonsCubeTexture.containsKey(action))
			return buttonsCubeTexture.get(action);
		else if (buttonsItemID.containsKey(action))
			return buttonsItemID.get(action);
		else if (buttonsTop.containsKey(action))
			return buttonsTop.get(action);
		else if (buttonsMultiCubes.containsKey(action))
			return buttonsMultiCubes.get(action);
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

		helpTool.setBottomLeftCorner(25, getHeight() - 26 - helpGeneral.getSize().height - 25);
		helpGeneral.setBottomLeftCorner(25, getHeight() - 26);

		popUpExit.setSize(width, height);
		popUpSaveOnExistant.setSize(width, height);
	}
}
