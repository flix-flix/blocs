package client.editor;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import client.editor.history.History;
import client.editor.history.HistoryList;
import client.editor.history.PixelHistory;
import client.editor.history.SizeHistory;
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
import client.window.graphicEngine.structures.Quadri;
import client.window.panels.editor.PanEditor;
import data.dynamic.TickClock;
import data.id.Item;
import data.id.ItemID;
import data.id.ItemTable;
import data.map.Cube;
import data.map.enumerations.Face;
import utils.FlixBlocksUtils;
import utils.yaml.YAML;

public class Editor {

	public Session session;

	public PanEditor panel;

	// ======================= World =========================
	public ModelMap map;
	public Camera camera;
	public TickClock clock;

	private ModelCube cube;

	// ======================= Cursor =========================
	private Cursor cursorPaint;
	private Cursor cursorFill;
	private Cursor cursorSelectColor;

	// ======================= Texture generation =========================
	private TextureCube textureCube;
	private static final int MAX_SIZE = 16;
	private int[][][] texture = new int[6][MAX_SIZE][MAX_SIZE];
	private int textureSize = 3;

	// ======================= Buttons =========================
	private ActionEditor action = null;
	private ActionEditor buttonListeningKey = null;

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
	private static final int lineSquareLayer = 12;

	// ======================= Rotation =========================
	private int prevX, prevY;

	// ======================= Keys =========================
	private static final int ALT = 18;
	private static final int SHIFT = 16;

	private boolean controlDown = false;
	private boolean shiftDown = false;
	private boolean altDown = false;

	// ======================= Write =========================
	private String writingString = "";
	private String realString = "";

	// =========================================================================================================================

	public Editor(Session session) {
		this.session = session;

		panel = new PanEditor(this);

		map = new ModelMap(session.texturePack);

		camera = new Camera(new Point3D(0, 0, -10), 90.5, .5);

		clock = new TickClock("Editor Clock");
		clock.add(map);
		clock.start();

		generateCursor();

		// ========================================================================================

		initTextureFrame();
		map.add(new Cube(ItemID.EDITOR_PREVIEW));
		cube = map.gridGet(0, 0, 0);

		// ========================================================================================

		cube.layers = new ArrayList<>();
		// 0-5 : grid
		// 6 - 11: face name
		// 12 : line/square
		for (int i = 0; i <= 12; i++)
			cube.layers.add(null);
	}

	// =========================================================================================================================
	// History

	private void undo() {
		if (!historyPack.isEmpty())
			historyPack();

		if (historyPosition == -1)
			return;

		history.get(historyPosition--).undo(this);
	}

	private void redo() {
		if (historyPosition + 1 >= history.size())
			return;

		history.get(++historyPosition).redo(this);
	}

	private void historyPack() {
		if (historyPack.isEmpty())
			return;

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

		updatePreviewTexture();
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

	public void updatePreviewTexture() {
		updatePreviewTexture(createTexture(), ItemID.EDITOR_PREVIEW);
	}

	public void updatePreviewTexture(TextureCube tc, int id) {
		// Update TexturePack
		session.texturePack.setTextureCube(tc, id);
		// Update miniature preview
		panel.get(ActionEditor.MINIATURE).update();
		textureCube = tc;
	}

	public void setTextureSize(int textureSize) {
		panel.get(ActionEditor.GRID).setWheelStep(textureSize);
		this.textureSize = textureSize;
	}

	public int getTextureSize() {
		return textureSize;
	}

	public void saveTexture() {
		int id = panel.get(ActionEditor.ITEM_ID).getWheelStep();
		String tag = panel.get(ActionEditor.ITEM_NAME).getString();
		int color = panel.get(ActionEditor.ITEM_COLOR).getValue();

		if (!session.texturePack.isIDAvailable(id))
			return;

		// Add to ItemTable
		ItemTable.addItem(new Item(id, tag));
		// Create and set textureCube | Update preview
		updatePreviewTexture(createTexture(), id);
		// Add the cube to the list of available cubes
		session.fen.gui.infos.addCube(new Cube(id));

		// Save the cube in file
		YAML.encodeFile(textureCube.getYAML(id, tag, color), "resources/temp/" + tag.toLowerCase() + ".yml");
	}

	// =========================================================================================================================
	// Painting

	public void paintPixel() {
		drawPixel(session.faceTarget, session.quadriTarget / textureSize, session.quadriTarget % textureSize,
				panel.panColor.getColor());
		updateLastPixel();
		updatePreviewTexture();
	}

	public void paintLine() {
		Line l = new Line(session.quadriTarget % textureSize, session.quadriTarget / textureSize, lastPaintCol,
				lastPaintRow);

		for (int row = l.min; row <= l.max; row++)
			for (int col = l.getLeft(row); col <= l.getRight(row); col++)
				drawPixel(session.faceTarget, row, col, panel.panColor.getColor());

		updateLastPixel();
		updatePreviewTexture();
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
		updatePreviewTexture();
		historyPack();
	}

	public void drawPixel(Face face, int col, int row, int color) {
		// Pack the previous history action if different from PAINT
		if (!historyPack.isEmpty() && !(historyPack.get(historyPack.size() - 1) instanceof PixelHistory))
			historyPack();

		if (texture[face.ordinal()][col][row] != color)
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
		return session.cubeTarget == cube && session.faceTarget == lastPaintFace && lastPaintCol < textureSize
				&& lastPaintRow < textureSize;
	}

	// =========================================================================================================================
	// Buttons events

	public void menuClick(ActionEditor action) {
		mayLooseListeningKey(action);

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
			refreshLayerGrid();
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
			if (panel.get(action).isSelected())
				setListeningKey(action);
			break;
		case ITEM_COLOR:
			panel.get(ActionEditor.ITEM_COLOR).setValue(panel.panColor.getColor() & 0xffffffff);
			break;

		case ITEM_SAVE:
			saveTexture();
			break;
		case ITEM_NEW:
		case ITEM_CLEAR:
			initTextureFrame();
			panel.get(ActionEditor.ITEM_NAME).reinit();
			panel.get(ActionEditor.ITEM_ID).reinit();
			panel.get(ActionEditor.ITEM_COLOR).reinit();
			break;

		// ================== SAVE ======================
		case SAVE:
			break;
		default:
			break;
		}
	}

	public void menuWheel(ActionEditor action) {
		mayLooseListeningKey(action);

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
			historyPack.add(new SizeHistory(textureSize, textureSize = panel.get(ActionEditor.GRID).getWheelStep()));
			updatePreviewTexture();
			refreshLayerGrid();
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
			panel.get(action).setBool(session.texturePack.isIDAvailable(panel.get(action).getWheelStep()));
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
	public boolean keyPressed(KeyEvent e) {
		updateControlShiftStatus(e);

		int code = e.getKeyCode();

		// Show face names
		if (code == ALT)
			refreshFaceLayer();

		// Undo/Redo
		if (e.isControlDown())
			if (e.getKeyCode() == 90) {
				undo();
				return true;
			} else if (e.getKeyCode() == 89) {
				redo();
				return true;
			}

		// Consume SHIFT to allow line/square drawing
		if (e.getKeyCode() == SHIFT && action == ActionEditor.PAINT)
			return true;

		// Writing
		if (buttonListeningKey == ActionEditor.ITEM_NAME) {
			write(e);
			return true;
		}

		return false;
	}

	public boolean keyReleased(KeyEvent e) {
		updateControlShiftStatus(e);

		int code = e.getKeyCode();

		if (code == ALT)
			refreshFaceLayer();

		return false;
	}

	public void updateControlShiftStatus(KeyEvent e) {
		controlDown = e.isControlDown();
		shiftDown = e.isShiftDown();
		altDown = e.isAltDown();
	}

	// =========================================================================================================================
	// Write

	public void write(KeyEvent e) {
		int code = e.getKeyCode();

		if (code == 27)
			esc();
		else if (code == 8) { // Delete
			if (!writingString.isEmpty())
				writeName(writingString = writingString.substring(0, writingString.length() - 1));
		} else if (code == 10)
			enter();

		else {
			char c = e.getKeyChar();

			if ('a' <= c && c <= 'z')
				c -= 32;

			if (c == ' ')
				c = '_';

			if (('A' <= c && c <= 'Z') || c == '_')
				writeName(writingString += c);
		}
	}

	public void writeName(String str) {
		boolean valid = true;

		for (String name : ItemTable.getItemTagList())
			if (str.equals(name)) {
				valid = false;
				break;
			}

		panel.get(buttonListeningKey).setString(str);
		panel.get(buttonListeningKey).setBool(valid);
	}

	public void esc() {
		writeName(realString);
		panel.get(buttonListeningKey).setSelected(false);
		buttonListeningKey = null;
	}

	public void enter() {
		realString = writingString;
		esc();
	}

	public void setListeningKey(ActionEditor action) {
		if (buttonListeningKey != null && buttonListeningKey != action)
			looseListeningKey();

		buttonListeningKey = action;
		realString = panel.get(action).getString();
		writingString = panel.get(action).getString();
	}

	public void looseListeningKey() {
		if (buttonListeningKey == null)
			return;

		esc();
	}

	public void mayLooseListeningKey(ActionEditor action) {
		if (buttonListeningKey == null)
			return;
		// Valid writting button
		if (action == ActionEditor.ITEM_SAVE || action == ActionEditor.ITEM_ID || action == buttonListeningKey)
			enter();
		else // Loose focus on writing button
			looseListeningKey();
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

		// Slow down with shift
		if (shiftDown)
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

		// If no
		if (session.quadriTarget == Quadri.NOT_NUMBERED)
			return;

		if (action == ActionEditor.PAINT) {
			session.cubeTarget.setSelectedQuadri(session.faceTarget, session.quadriTarget);

			cube.removeLayer(lineSquareLayer);

			if (session.keyboard.pressL) {
				paintPixel();
				return;
			}

			// Show Line/Square preview
			if (shiftDown && hasLastPixel()) {
				DrawLayer layer = new DrawLayer(cube, session.faceTarget);

				int col1 = session.quadriTarget % textureSize;
				int row1 = session.quadriTarget / textureSize;
				int col2 = lastPaintCol;
				int row2 = lastPaintRow;

				if (controlDown) // Square
					layer.drawSquareAndCross(col1, row1, col2, row2, 0xffdddddd, 0xff555555);
				else // Line
					layer.drawLineAndCross(col1, row1, col2, row2, 0xffdddddd, 0xff555555);

				cube.layers.set(lineSquareLayer, layer);
			}
		}
	}

	public void looseTarget() {
		// Removes highlight of previous selected quadri
		session.cubeTarget.setSelectedQuadri(null, ModelCube.NO_QUADRI);
		// Removes line/square preview
		session.cubeTarget.removeLayer(lineSquareLayer);
	}

	// =========================================================================================================================
	// Layers

	public void refreshLayerGrid() {
		for (Face face : Face.faces)
			if (panel.get(ActionEditor.GRID).isSelected())
				cube.layers.set(face.ordinal() + 6, generateGridLayer(face));
			else
				cube.removeLayer(face.ordinal() + 6);
	}

	public void refreshFaceLayer() {
		for (Face face : Face.faces)
			if (altDown)
				cube.layers.set(face.ordinal(), generateNameLayer(face));
			else
				cube.removeLayer(face.ordinal());
	}

	public DrawLayer generateGridLayer(Face face) {
		DrawLayer layer = new DrawLayer(cube, face);
		layer.drawGrid();
		return layer;
	}

	public DrawLayer generateNameLayer(Face face) {
		DrawLayer layer = new DrawLayer(cube, face);
		layer.drawFace();
		return layer;
	}

	// =========================================================================================================================
	// Cursor

	public Cursor getCursor() {
		Cursor cursor = Cursor.getDefaultCursor();

		if (session.editor.getAction() == ActionEditor.PAINT)
			cursor = (controlDown && (!shiftDown || !session.editor.isPreviewCube())) ? cursorSelectColor : cursorPaint;
		else if (session.editor.getAction() == ActionEditor.FILL)
			cursor = controlDown ? cursorSelectColor : cursorFill;

		return cursor;
	}

	public void generateCursor() {
		cursorPaint = FlixBlocksUtils.createCursor(session.texturePack.getFolder() + "cursor/cursorPaint");
		cursorFill = FlixBlocksUtils.createCursor(session.texturePack.getFolder() + "cursor/cursorFill");
		cursorSelectColor = FlixBlocksUtils.createCursor(session.texturePack.getFolder() + "cursor/cursorSelectColor");
	}

	// =========================================================================================================================
	// Mouse Event

	/** Return true if the event is consumed */
	public boolean leftClick() {
		looseListeningKey();

		if (session.faceTarget == null || session.quadriTarget == Quadri.NOT_NUMBERED)
			return false;

		// Cancel action during rotation
		if (session.keyboard.pressR)
			return false;

		if (action == null)
			return false;

		switch (action) {
		case PAINT:
			if (controlDown && (!shiftDown || !isPreviewCube()))
				selectColor();
			else {
				if (!isPreviewCube())
					return false;

				if (shiftDown && hasLastPixel())
					if (controlDown)
						paintSquare();
					else
						paintLine();
				else
					paintPixel();
			}
			break;

		case FILL:
			if (controlDown)
				selectColor();
			else if (isPreviewCube()) {
				int face = session.faceTarget.ordinal();
				int row = session.quadriTarget / textureSize;
				int col = session.quadriTarget % textureSize;

				fill(texture[face][row][col], face, row, col);

				updatePreviewTexture();
				historyPack();
			}
			break;

		default:
			break;
		}
		return false;
	}

	public void rightClick(MouseEvent e) {
		looseListeningKey();

		initDrag(e.getX(), e.getY());
		lookCube();
	}

	public void leftClickEnd() {
		// Save the current paint line (drag)
		historyPack();
	}

	public void drag(MouseEvent e) {
		rotateCamera(e.getX() - prevX, e.getY() - prevY);
		initDrag(e.getX(), e.getY());
	}

	public void cameraMoved() {
		if (panel.get(ActionEditor.ROTATE).isSelected())
			lookCube();
	}

	// =========================================================================================================================
	// Mode getters

	public boolean isRotateMode() {
		return panel.get(ActionEditor.ROTATE).isSelected();
	}

	public boolean isPreviewCube() {
		if (session.cubeTarget == null)
			return false;
		return session.cubeTarget.itemID == ItemID.EDITOR_PREVIEW;
	}

	public boolean isNeededQuadriPrecision() {
		if (action == null)
			return false;

		switch (action) {
		case PAINT:
		case FILL:
		case PLAYER_COLOR:
			return true;
		default:
			return false;
		}
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
