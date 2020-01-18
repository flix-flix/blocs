package client.editor;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import client.keys.Key;
import client.session.Session;
import client.session.UserAction;
import client.textures.TextureCube;
import client.textures.TextureFace;
import client.textures.TextureSquare;
import client.window.graphicEngine.calcul.Camera;
import client.window.graphicEngine.calcul.Line;
import client.window.graphicEngine.calcul.Point3D;
import client.window.graphicEngine.extended.DrawLayer;
import client.window.graphicEngine.extended.ModelCube;
import client.window.graphicEngine.extended.ModelMap;
import client.window.panels.editor.MenuButtonEditor;
import client.window.panels.editor.PanEditor;
import data.dynamic.TickClock;
import data.id.ItemID;
import data.map.Coord;
import data.map.Cube;
import data.map.enumerations.Face;
import utils.FlixBlocksUtils;

public class Editor {

	public Session session;

	public PanEditor panel;

	// ======================= World =========================
	public ModelMap map;
	public Camera camera;
	public TickClock clock;

	// ======================= Texture generation =========================
	private static Cursor cursorPaint = FlixBlocksUtils.createCursor("cursorPaint");
	private static Cursor cursorFill = FlixBlocksUtils.createCursor("cursorFill");
	private static Cursor cursorSelectColor = FlixBlocksUtils.createCursor("cursorSelectColor");

	// ======================= Texture generation =========================
	private static final int MAX_SIZE = 16;
	private int[][][] texture = new int[6][MAX_SIZE][MAX_SIZE];
	private int textureSize = 3;

	// ======================= Buttons =========================
	private ActionEditor action = null;
	private ActionEditor listeningKey = null;

	// ======================= Memory =========================
	private Face lastPaintFace = null;
	private int lastPaintCol = -1;
	private int lastPaintRow = -1;

	// ======================= History =========================
	/** Store the modifications */
	private ArrayList<History> history = new ArrayList<>();
	/**
	 * Store the modifications to be packed together before insertion to #history
	 */
	private ArrayList<History> historyPack = new ArrayList<>();
	/** Index of the last modification (-1 means no previous modif) */
	private int historyPosition = -1;

	// ======================= Layer =========================
	private static final String paintLayer = "paint";

	// ======================= Rotation =========================
	private int prevX, prevY;

	// =========================================================================================================================

	public Editor(Session session) {
		this.session = session;

		panel = new PanEditor(this);

		map = new ModelMap();

		camera = new Camera(new Point3D(0, 0, -10), 90.5, .5);

		clock = new TickClock("Editor Clock");
		clock.add(map);
		clock.start();

		// ========================================================================================

		initTextureFrame();
		map.add(new Cube(new Coord(0, 0, 0), ItemID.EDITOR_PREVIEW));
	}

	// =========================================================================================================================
	// History

	public void undo() {
		if (!historyPack.isEmpty())
			historyPack();

		if (historyPosition == -1)
			return;

		history.get(historyPosition--).undo(this);
	}

	public void redo() {
		if (historyPosition + 1 >= history.size())
			return;

		history.get(++historyPosition).redo(this);
	}

	public void historyPack() {
		history.add(++historyPosition, new HistoryList(historyPack));

		while (history.size() > historyPosition + 1)
			history.remove(historyPosition + 1);

		historyPack = new ArrayList<>();
	}

	// =========================================================================================================================
	// Texture management

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
			int[] tab = new int[textureSize * textureSize];
			for (int k = 0; k < tab.length; k++) // Generates data-arrays
				tab[k] = texture[face][k / textureSize][k % textureSize];
			tf[face] = new TextureFace(new TextureSquare(tab, textureSize));
		}

		return new TextureCube(tf);
	}

	public void saveTexture() {
		saveTexture(createTexture(), ItemID.EDITOR_PREVIEW.id);
		saveTexture(createTexture(), ItemID.EDITOR_PREVIEW_GRID.id);
	}

	public void saveTexture(TextureCube tc, int id) {
		session.texturePack.addTextureCube(tc, id);
		panel.buttonsAction.get(ActionEditor.MINIATURE).update();
	}

	public void setTextureSize(int textureSize) {
		this.textureSize = textureSize;
	}

	public int getTextureSize() {
		return textureSize;
	}

	// =========================================================================================================================
	// Painting

	public void paintPixel() {
		drawPixel(session.faceTarget, session.quadriTarget / textureSize, session.quadriTarget % textureSize,
				panel.panColor.getColor());
		updateLastPixel();
		saveTexture();
		historyPack();
	}

	public void paintLine() {
		Line l = new Line(session.quadriTarget % textureSize, session.quadriTarget / textureSize, lastPaintCol,
				lastPaintRow);

		for (int row = l.min; row <= l.max; row++)
			for (int col = l.getLeft(row); col <= l.getRight(row); col++)
				drawPixel(session.faceTarget, row, col, panel.panColor.getColor());

		updateLastPixel();
		saveTexture();
		historyPack();
	}

	public void paintSquare() {
		int col1 = Math.min(session.quadriTarget % textureSize, lastPaintCol);
		int row1 = Math.min(session.quadriTarget / textureSize, lastPaintRow);
		int col2 = Math.max(session.quadriTarget % textureSize, lastPaintCol);
		int row2 = Math.max(session.quadriTarget / textureSize, lastPaintRow);

		for (int col = col1; col <= col2; col++)
			for (int row = row1; row <= row2; row++)
				drawPixel(session.faceTarget, row, col, panel.panColor.getColor());

		updateLastPixel();
		saveTexture();
		historyPack();
	}

	public void drawPixel(Face face, int col, int row, int color) {
		// Pack the previous history action if different from PAINT
		if (!historyPack.isEmpty() && !(historyPack.get(historyPack.size() - 1) instanceof PixelHistory))
			historyPack();
		historyPack.add(new PixelHistory(face, col, row, texture[face.ordinal()][col][row], color));

		setPixel(face, col, row, color);
	}

	public void setPixel(Face face, int col, int row, int color) {
		texture[face.ordinal()][col][row] = color;
	}

	// =========================================================================================================================
	// Other tools

	public void selectColor() {
		panel.panColor
				.setColor(texture[session.faceTarget.ordinal()][session.quadriTarget / textureSize][session.quadriTarget
						% textureSize]);
	}

	public void fill(int erase, int face, int row, int col) {
		if (row < 0 || textureSize <= row || col < 0 || textureSize <= col)
			return;

		if (texture[face][row][col] != erase || erase == panel.panColor.getColor())
			return;

		drawPixel(Face.faces[face], row, col, panel.panColor.getColor());

		fill(erase, face, row + 1, col);
		fill(erase, face, row - 1, col);
		fill(erase, face, row, col + 1);
		fill(erase, face, row, col - 1);
	}

	// =========================================================================================================================
	// Memory

	public void updateLastPixel() {
		lastPaintFace = session.faceTarget;
		lastPaintCol = session.quadriTarget % textureSize;
		lastPaintRow = session.quadriTarget / textureSize;
	}

	public boolean hasLastPixel() {
		return session.cubeTarget == map.gridGet(0, 0, 0) && session.faceTarget == lastPaintFace
				&& lastPaintCol < textureSize && lastPaintRow < textureSize;
	}

	// =========================================================================================================================
	// Buttons events

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
		case PLAYER_COLOR:
			if (action == this.action)
				setAction(null);
			else
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
		case QUIT:
			break;

		// ================== PanColor ======================
		case VALID_COLOR:
			panel.panColor.selectColor();
			break;

		// ================== PanItem ======================
		case ITEM_NAME:
		case ITEM_ID:
			listeningKey = action;
			break;
		case ITEM_COLOR:
			panel.buttonsItemID.get(ActionEditor.ITEM_COLOR).setValue(panel.panColor.getColor() & 0xffffff);
			break;

		case ITEM_SAVE:
			int id = panel.buttonsItemID.get(ActionEditor.ITEM_ID).getWheelStep();
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
			historyPack.add(new SizeHistory(textureSize,
					textureSize = panel.buttonsAction.get(ActionEditor.GRID).getWheelStep()));
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
	// KeyEvent

	/** @return true if the event is consumed */
	public boolean keyEvent(KeyEvent e) {
		// Undo/Redo
		if (e.isControlDown())
			if (e.getKeyCode() == 90) {
				undo();
				return true;
			} else if (e.getKeyCode() == 89) {
				redo();
				return true;
			}

		// Allow SHIFT to draw line and square
		if (e.getKeyCode() == Key.DOWN.code)
			return action == ActionEditor.PAINT;

		if (listeningKey == null)
			return false;

		int key = e.getKeyCode();
		char c = e.getKeyChar();

		MenuButtonEditor button = panel.buttonsItemID.get(ActionEditor.ITEM_NAME);

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
		return true;
	}

	// =========================================================================================================================
	// Rotate-Mode

	public void lookCube() {
		camera.setVx(FlixBlocksUtils.toDegres * Math.atan((camera.vue.x - .5) / -(camera.vue.z - .5)) + 90
				+ (camera.vue.z - .5 >= 0 ? 180 : 0));
		camera.setVy(FlixBlocksUtils.toDegres
				* Math.atan(Math.hypot(camera.vue.x - .5, camera.vue.z - .5) / (camera.vue.y - .5)) - 90
				+ (camera.vue.y - .5 <= 0 ? 180 : 0));
	}

	public void rotateCamera(int x, int y) {
		double slow = .2;

		double distY = camera.vue.dist(0.5, 0.5, 0.5);
		double angleY = camera.getVy() + y * -slow;

		if (angleY >= 60)
			angleY = 59.9;
		else if (angleY <= -60)
			angleY = -59.9;

		camera.vue.y = .5 - Math.sin(FlixBlocksUtils.toRadian * angleY) * distY;
		double distX = Math.cos(FlixBlocksUtils.toRadian * angleY) * distY;

		double angleX = FlixBlocksUtils.toRadian * (camera.getVx() + x * slow);

		camera.vue.x = .5 - distX * Math.cos(angleX);
		camera.vue.z = .5 - distX * Math.sin(angleX);

		lookCube();
	}

	public void rotateCamera(boolean forward, boolean backward, boolean right, boolean left) {
		int x = 0, y = 0;
		int speed = 15;

		// Slow down with control
		if (session.fen.isControlDown())
			speed = 5;

		if (right)
			x += speed;
		if (left)
			x -= speed;

		if (forward)
			y -= speed;
		if (backward)
			y += speed;

		rotateCamera(x, y);
	}

	public void initDrag(int x, int y) {
		prevX = x;
		prevY = y;
	}

	// =========================================================================================================================
	// Target

	public void updateTarget() {
		if (session.keyboard.pressR) {
			looseTarget();
			return;
		}

		if (action == ActionEditor.PAINT) {
			session.cubeTarget.setSelectedQuadri(session.faceTarget, session.quadriTarget);

			// Show Line/Square preview
			if (session.fen.isShiftDown() && hasLastPixel()) {
				ModelCube cube = map.gridGet(0, 0, 0);
				DrawLayer layer = new DrawLayer(cube, session.faceTarget);

				int col1 = session.quadriTarget % textureSize;
				int row1 = session.quadriTarget / textureSize;
				int col2 = lastPaintCol;
				int row2 = lastPaintRow;

				if (session.fen.isControlDown()) // Square
					layer.drawSquare(col1, row1, col2, row2, 0xffdddddd, 0xff555555);
				else // Line
					layer.drawLineAndCross(col1, row1, col2, row2, 0xffdddddd, 0xff555555);

				cube.addLayer(paintLayer, layer);
			} else
				session.cubeTarget.removeLayer("paint");
		}
	}

	public void looseTarget() {
		// Removes highlight of previous selected quadri
		session.cubeTarget.setSelectedQuadri(null, ModelCube.NO_QUADRI);
		// Removes line/square preview
		session.cubeTarget.removeLayer(paintLayer);
	}

	// =========================================================================================================================
	// Cursor

	public Cursor getCursor() {
		Cursor cursor = Cursor.getDefaultCursor();

		if (session.editor.getAction() == ActionEditor.PAINT)
			cursor = (session.fen.isControlDown() && (!session.fen.isShiftDown() || !session.editor.isPreviewCube()))
					? cursorSelectColor
					: cursorPaint;
		else if (session.editor.getAction() == ActionEditor.FILL)
			cursor = session.fen.isControlDown() ? cursorSelectColor : cursorFill;

		return cursor;
	}

	// =========================================================================================================================
	// Mouse Event

	public boolean isListeningLeftClick() {
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

	public void leftClick() {
		if (session.faceTarget == null || session.keyboard.pressR)
			return;

		switch (action) {
		case PAINT:
			if (session.fen.isControlDown() && (!session.fen.isShiftDown() || !isPreviewCube()))
				selectColor();
			else {
				if (!isPreviewCube())
					return;

				if (session.fen.isShiftDown() && hasLastPixel())
					if (session.fen.isControlDown())
						paintSquare();
					else
						paintLine();
				else
					paintPixel();
			}
			break;

		case FILL:
			if (session.fen.isControlDown())
				selectColor();
			else if (isPreviewCube()) {
				int face = session.faceTarget.ordinal();
				int row = session.quadriTarget / textureSize;
				int col = session.quadriTarget % textureSize;

				fill(texture[face][row][col], face, row, col);

				saveTexture();
				historyPack();
			}
			break;

		default:
			break;
		}
	}

	public void rightClick(MouseEvent e) {
		initDrag(e.getX(), e.getY());
		lookCube();
	}

	public void drag(MouseEvent e) {
		rotateCamera(e.getX() - prevX, e.getY() - prevY);
		initDrag(e.getX(), e.getY());
	}

	public void cameraMoved() {
		if (panel.buttonsAction.get(ActionEditor.ROTATE).isSelected())
			lookCube();
	}

	// =========================================================================================================================
	// Mode getters

	public boolean isRotateMode() {
		return panel.buttonsAction.get(ActionEditor.ROTATE).isSelected();
	}

	public boolean isPreviewCube() {
		if (session.cubeTarget == null)
			return false;
		return session.cubeTarget.itemID == ItemID.EDITOR_PREVIEW
				|| session.cubeTarget.itemID == ItemID.EDITOR_PREVIEW_GRID;
	}

	// =========================================================================================================================

	public ActionEditor getAction() {
		return action;
	}

	public void setAction(ActionEditor action) {
		this.action = action;
		session.fen.updateCursor();
	}
}
