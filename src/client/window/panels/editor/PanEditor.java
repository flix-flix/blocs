package client.window.panels.editor;

import java.awt.event.MouseWheelEvent;

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

	ActionEditor[] _buttonsGrid = { ActionEditor.ALONE, ActionEditor.DECOR, ActionEditor.GRID, ActionEditor.MINIATURE,
			ActionEditor.SAVE, ActionEditor.PLAYER_COLOR };
	MenuButtonEditor[] buttonsGrid = new MenuButtonEditor[_buttonsGrid.length];

	MenuGrid topActions;
	MenuGrid gridActions;

	public MenuItemID panItemID;
	public MenuColor panColor;

	// =========================================================================================================================

	public PanEditor(Session session) {
		this.setLayout(null);
		this.setOpaque(false);

		this.session = session;

		map = new ModelMap();
		map.add(new Cube(new Coord(0, 0, 0), ItemID.BORDER));

		camera = new Camera(new Point3D(0, 0, -10), 90, 0);

		clock = new TickClock("Editor Clock");
		clock.add(map);
		clock.start();

		// ========================================================================================

		menu.setBounds(0, 0, menuWidth, getHeight());
		this.add(menu);

		menu.addTop(topActions = new MenuGrid(), 100);

		for (int i = 0; i < _buttonsTop.length; i++)
			topActions.addItem(buttonsTop[i] = new MenuButtonEditor(this, _buttonsTop[i]));

		menu.addBottom(panColor = new MenuColor(this), MenuCol.WIDTH);
		menu.addBottom(panItemID = new MenuItemID(this), 65);

		menu.addTop(gridActions = new MenuGrid(), MenuCol.REMAINING);

		for (int i = 0; i < _buttonsGrid.length; i++)
			gridActions.addItem(buttonsGrid[i] = new MenuButtonEditor(this, _buttonsGrid[i]));
	}

	// =========================================================================================================================

	public void clicked(ActionEditor action) {
		switch (action) {
		case EDITOR:// Close Editor
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

		case GRID:
			break;
		case MINIATURE:
			break;
		case PLAYER_COLOR:
			break;
		case QUIT:
			break;

		case SAVE:
			break;
		default:
			break;
		}
	}

	public void wheel(ActionEditor action, MouseWheelEvent e) {
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

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		menu.setBounds(x, y, menuWidth, height);
	}
}
