package client.window.panels.editor;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.JPanel;

import client.session.Session;
import client.session.UserAction;
import client.textures.TextureCube;
import client.textures.TextureFace;
import client.textures.TextureSquare;
import client.window.graphicEngine.calcul.Camera;
import client.window.graphicEngine.calcul.Point3D;
import client.window.graphicEngine.extended.ModelMap;
import client.window.panels.menus.MenuCol;
import client.window.panels.menus.MenuGrid;
import data.dynamic.TickClock;
import data.id.ItemID;
import data.map.Coord;
import data.map.Cube;

public class PanEditor extends JPanel {
	private static final long serialVersionUID = -7092208608285186782L;

	Session session;

	// =======================

	public ModelMap map;
	public Camera camera;
	public TickClock clock;

	// ======================= Menu =========================
	int menuWidth = 400;

	MenuCol menu = new MenuCol();

	ActionEditor[] _buttonsTop = { ActionEditor.EDIT_CUBE, ActionEditor.EDIT_MULTI_CUBE,
			ActionEditor.EDIT_MULTI_TEXTURE, ActionEditor.EDITOR };
	HashMap<ActionEditor, MenuButtonEditor> buttonsTop = new HashMap<>();

	ActionEditor[] _buttonsAction = { ActionEditor.ALONE, ActionEditor.DECOR, ActionEditor.PAINT, ActionEditor.FILL,
			ActionEditor.GRID, ActionEditor.MINIATURE, ActionEditor.SAVE, ActionEditor.PLAYER_COLOR };
	HashMap<ActionEditor, MenuButtonEditor> buttonsAction = new HashMap<>();

	ActionEditor[] _buttonsItemID = { ActionEditor.ITEM_NAME, ActionEditor.ITEM_ID, ActionEditor.ITEM_COLOR,
			ActionEditor.ITEM_SAVE, ActionEditor.ITEM_NEW, ActionEditor.ITEM_CLEAR };
	HashMap<ActionEditor, MenuButtonEditor> buttonsItemID = new HashMap<>();

	MenuGrid topActions;
	MenuGrid gridActions;
	MenuGrid gridItemID;

	MenuColor panColor;

	// ======================= ? =========================
	private ActionEditor action = null;
	private ActionEditor listeningKey = null;

	// ======================= Texture generation =========================
	private static final int MAX_SIZE = 16;
	private int[][][] texture = new int[6][MAX_SIZE][MAX_SIZE];
	private int size = 3;

	// =========================================================================================================================

	public PanEditor(Session session) {
		this.setLayout(null);
		this.setOpaque(false);

		this.session = session;

		map = new ModelMap();

		camera = new Camera(new Point3D(0, 0, -10), 90, 0);

		clock = new TickClock("Editor Clock");
		clock.add(map);
		clock.start();

		// ========================================================================================

		menu.setBounds(0, 0, menuWidth, getHeight());
		this.add(menu);

		menu.addTop(topActions = new MenuGrid(), 100);

		for (ActionEditor action : _buttonsTop) {
			buttonsTop.put(action, new MenuButtonEditor(this, action));
			topActions.addMenu(buttonsTop.get(action));
		}

		menu.addBottom(panColor = new MenuColor(this), MenuCol.WIDTH);
		menu.addBottom(gridItemID = new MenuGrid(), 110);

		gridItemID.setCols(3);
		gridItemID.setRowHeight(50);
		gridItemID.setBackground(Color.GRAY);
		gridItemID.setBorder(5, Color.DARK_GRAY);
		gridItemID.setPadding(MenuGrid.GRID_SPACE);

		for (ActionEditor action : _buttonsItemID) {
			buttonsItemID.put(action, new MenuButtonEditor(this, action));
			gridItemID.addMenu(buttonsItemID.get(action));
		}

		buttonsItemID.get(ActionEditor.ITEM_ID).setWheelMinMax(0, 999);

		menu.addTop(gridActions = new MenuGrid(), MenuCol.REMAINING);

		for (ActionEditor action : _buttonsAction) {
			buttonsAction.put(action, new MenuButtonEditor(this, action));
			gridActions.addMenu(buttonsAction.get(action));
		}

		buttonsAction.get(ActionEditor.GRID).setWheelStep(size);
		buttonsAction.get(ActionEditor.GRID).setWheelMinMax(1, 16);

		// ========================================================================================

		initTextureFrame();
		map.add(new Cube(new Coord(0, 0, 0), ItemID.EDITOR_PREVIEW));
	}

	// =========================================================================================================================

	public void initTextureFrame() {
		for (int face = 0; face < 6; face++)
			for (int i = 0; i < MAX_SIZE; i++)
				for (int j = 0; j < MAX_SIZE; j++)
					texture[face][i][j] = (i + j) % 2 == 0 ? 0xff888888 : 0xff555555;

		saveTexture();
	}

	public TextureCube createTexture() {
		TextureFace[] tf = new TextureFace[6];

		for (int face = 0; face < 6; face++) { // Generates faces
			int[] tab = new int[size * size];
			for (int k = 0; k < tab.length; k++) // Generates data-arrays
				tab[k] = texture[face][k / size][k % size];
			tf[face] = new TextureFace(new TextureSquare(tab, size));
		}

		return new TextureCube(tf);
	}

	public void saveTexture() {
		saveTexture(createTexture(), ItemID.EDITOR_PREVIEW.id);
		saveTexture(createTexture(), ItemID.EDITOR_PREVIEW_GRID.id);
	}

	public void saveTexture(TextureCube tc, int id) {
		session.texturePack.addTextureCube(tc, id);
	}

	// =========================================================================================================================

	public boolean isListeningClick() {
		if (action == null)
			return false;

		switch (action) {
		case PAINT:
		case FILL:
			return true;
		default:
			return false;
		}
	}

	public void click() {
		if (session.faceTarget == null || (session.cubeTarget.itemID != ItemID.EDITOR_PREVIEW
				&& session.cubeTarget.itemID != ItemID.EDITOR_PREVIEW_GRID))
			return;

		switch (action) {
		case PAINT:
			paint();
			break;

		case FILL:
			if (session.fen.isControlDown())
				selectColor();
			else {
				int face = session.faceTarget.ordinal();
				int row = session.quadriTarget / size;
				int col = session.quadriTarget % size;

				fill(texture[face][row][col], face, row, col);

				saveTexture();
			}
			break;

		default:
			break;
		}
	}

	// =========================================================================================================================

	public void paint() {
		if (session.fen.isControlDown())
			selectColor();
		else {// Paint
			texture[session.faceTarget.ordinal()][session.quadriTarget / size][session.quadriTarget % size] = panColor
					.getColor();

			saveTexture();
		}
	}

	public void selectColor() {
		panColor.setColor(
				texture[session.faceTarget.ordinal()][session.quadriTarget / size][session.quadriTarget % size]);
	}

	public void fill(int erase, int face, int row, int col) {
		if (row < 0 || size <= row || col < 0 || size <= col)
			return;

		if (texture[face][row][col] != erase || erase == panColor.getColor())
			return;

		texture[face][row][col] = panColor.getColor();

		fill(erase, face, row + 1, col);
		fill(erase, face, row - 1, col);
		fill(erase, face, row, col + 1);
		fill(erase, face, row, col - 1);
	}

	// =========================================================================================================================

	public void menuClick(ActionEditor action) {
		listeningKey = null;
		switch (action) {
		case EDITOR:// Close Editor
			setAction(null);
			session.fen.setAction(UserAction.EDITOR);
			break;

		// ================== EDIT TYPE ======================
		case EDIT_CUBE:
			break;
		case EDIT_MULTI_CUBE:
			break;
		case EDIT_MULTI_TEXTURE:
			break;

		// ================== GRID ======================
		case ALONE:
			break;
		case DECOR:
			break;

		case PAINT:
		case FILL:
			setAction(action);
			break;

		case GRID:
			if (map.gridGet(0, 0, 0).itemID == ItemID.EDITOR_PREVIEW)
				map.gridGet(0, 0, 0).itemID = ItemID.EDITOR_PREVIEW_GRID;
			else
				map.gridGet(0, 0, 0).itemID = ItemID.EDITOR_PREVIEW;
			break;
		case MINIATURE:
			break;
		case PLAYER_COLOR:
			break;
		case QUIT:
			break;

		// ================== PanColor ======================
		case VALID_COLOR:
			panColor.selectColor();
			break;

		// ================== PanItem ======================
		case ITEM_NAME:
		case ITEM_ID:
			listeningKey = action;
			break;
		case ITEM_COLOR:
			buttonsItemID.get(ActionEditor.ITEM_COLOR).setValue(panColor.getColor() & 0xffffff);
			break;

		case ITEM_SAVE:
			int id = buttonsItemID.get(ActionEditor.ITEM_ID).getWheelStep();
			if (session.texturePack.isIDAvailable(id))
				saveTexture(createTexture(), id);
			break;
		case ITEM_NEW:
		case ITEM_CLEAR:
			initTextureFrame();
			break;

		// ================== SAVE ======================
		case SAVE:
			break;
		default:
			break;
		}
	}

	public void menuWheel(ActionEditor action) {
		switch (action) {
		// ================== EDIT TYPE ======================
		case EDIT_CUBE:
			break;
		case EDIT_MULTI_CUBE:
			break;
		case EDIT_MULTI_TEXTURE:
			break;

		// ================== EDIT TYPE ======================
		case ALONE:
			break;
		case DECOR:
			break;

		case GRID:
			size = buttonsAction.get(ActionEditor.GRID).getWheelStep();
			saveTexture();
			break;
		case MINIATURE:
			break;
		case PLAYER_COLOR:
			break;

		// ================== PanColor ======================
		case SELECT_ALPHA:
			break;

		// ================== PanItem ======================
		case ITEM_ID:
			listeningKey = action;
			break;

		// ================== Not handled ======================
		case EDITOR:
		case QUIT:
		case SAVE:
			break;
		default:
			break;
		}
	}

	// =========================================================================================================================

	public void keyEvent(KeyEvent e) {
		int key = e.getKeyCode();
		char c = e.getKeyChar();

		MenuButtonEditor button = buttonsItemID.get(ActionEditor.ITEM_NAME);

		if (key == 27) {
			button.clearString();
			listeningKey = null;
		} else if (key == 8)
			button.delChar();
		else if (key == 10)
			listeningKey = null;

		else {
			if ('a' <= c && c <= 'z')
				c -= 32;

			if (c == ' ')
				c = '_';

			if (('A' <= c && c <= 'Z') || c == '_')
				button.addChar(c);
		}
	}

	public boolean isListeningKey() {
		return listeningKey != null;
	}

	// =========================================================================================================================

	public ActionEditor getAction() {
		return action;
	}

	public void setAction(ActionEditor action) {
		this.action = action;
		session.fen.updateCursor();
	}

	// =========================================================================================================================

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		menu.setBounds(x, y, menuWidth, height);
	}
}
