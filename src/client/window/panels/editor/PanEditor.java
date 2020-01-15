package client.window.panels.editor;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import client.session.Session;
import client.session.UserAction;
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
	MenuButtonEditor[] buttonsTop = new MenuButtonEditor[_buttonsTop.length];

	ActionEditor[] _buttonsGrid = { ActionEditor.ALONE, ActionEditor.DECOR, ActionEditor.PAINT, ActionEditor.GRID,
			ActionEditor.MINIATURE, ActionEditor.SAVE, ActionEditor.PLAYER_COLOR };
	MenuButtonEditor[] buttonsGrid = new MenuButtonEditor[_buttonsGrid.length];

	ActionEditor[] _buttonsItem = { ActionEditor.ITEM_NAME, ActionEditor.ITEM_ID, ActionEditor.ITEM_COLOR,
			ActionEditor.ITEM_SAVE, ActionEditor.ITEM_NEW, ActionEditor.ITEM_CLEAR };
	MenuButtonEditor[] buttonsItem = new MenuButtonEditor[_buttonsGrid.length];

	MenuGrid topActions;
	MenuGrid gridActions;
	MenuGrid gridItemID;

	MenuColor panColor;

	// ======================= ? =========================
	private ActionEditor action = null;
	private ActionEditor listeningKey = null;

	// =========================================================================================================================

	public PanEditor(Session session) {
		this.setLayout(null);
		this.setOpaque(false);

		this.session = session;

		map = new ModelMap();
		map.add(new Cube(new Coord(0, 0, 0), ItemID.BORDER));
		map.add(new Cube(new Coord(0, 1, 0), ItemID.GLASS));

		camera = new Camera(new Point3D(0, 0, -10), 90, 0);

		clock = new TickClock("Editor Clock");
		clock.add(map);
		clock.start();

		// ========================================================================================

		menu.setBounds(0, 0, menuWidth, getHeight());
		this.add(menu);

		menu.addTop(topActions = new MenuGrid(), 100);

		for (int i = 0; i < _buttonsTop.length; i++)
			topActions.addMenu(buttonsTop[i] = new MenuButtonEditor(this, _buttonsTop[i]));

		menu.addBottom(panColor = new MenuColor(this), MenuCol.WIDTH);
		menu.addBottom(gridItemID = new MenuGrid(), 110);

		gridItemID.setCols(3);
		gridItemID.setRowHeight(50);
		gridItemID.setBackground(Color.GRAY);
		gridItemID.setBorder(5, Color.DARK_GRAY);
		gridItemID.setPadding(MenuGrid.GRID_SPACE);

		for (int i = 0; i < _buttonsItem.length; i++)
			gridItemID.addMenu(buttonsItem[i] = new MenuButtonEditor(this, _buttonsItem[i]));

		buttonsItem[1].setWheelMinMax(0, 999);

		menu.addTop(gridActions = new MenuGrid(), MenuCol.REMAINING);

		for (int i = 0; i < _buttonsGrid.length; i++)
			gridActions.addMenu(buttonsGrid[i] = new MenuButtonEditor(this, _buttonsGrid[i]));
	}

	// =========================================================================================================================

	public void clicked(ActionEditor action) {
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

		// ================== EDIT TYPE ======================
		case ALONE:
			break;
		case DECOR:
			break;

		case PAINT:
			setAction(ActionEditor.PAINT);
			session.fen.updateCursor();
			break;
		case GRID:
			break;
		case MINIATURE:
			break;
		case PLAYER_COLOR:
			break;
		case QUIT:
			break;

		// ================== PanColor ======================
		case VALID_COLOR:
			panColor.validColor();
			break;

		// ================== PanItem ======================
		case ITEM_NAME:
		case ITEM_ID:
			listeningKey = action;
			break;
		case ITEM_COLOR:
			buttonsItem[2].setValue(panColor.selectedColor);
			break;

		case ITEM_SAVE:
		case ITEM_NEW:
		case ITEM_CLEAR:
			break;

		// ================== SAVE ======================
		case SAVE:
			break;
		default:
			break;
		}
	}

	public void wheel(ActionEditor action) {
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

		MenuButtonEditor button = buttonsItem[0];

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
	}

	// =========================================================================================================================

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		menu.setBounds(x, y, menuWidth, height);
	}
}
