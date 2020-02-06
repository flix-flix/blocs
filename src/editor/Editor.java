package editor;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

import data.dynamic.TickClock;
import data.id.Item;
import data.id.ItemID;
import data.id.ItemTable;
import data.id.ItemTableClient;
import data.map.Cube;
import data.map.enumerations.Face;
import data.map.resources.ResourceType;
import editor.history.History;
import editor.history.HistoryList;
import editor.history.PixelHistory;
import editor.history.SizeHistory;
import editor.panels.PanEditor;
import environment.Environment3D;
import environment.EnvironmentListener;
import environment.extendsData.CubeClient;
import environment.extendsData.MapClient;
import environment.extendsEngine.DrawLayer;
import environment.textures.TextureCube;
import environment.textures.TextureFace;
import environment.textures.TexturePack;
import environment.textures.TextureSquare;
import graphicEngine.calcul.Camera;
import graphicEngine.calcul.Engine;
import graphicEngine.calcul.Line;
import graphicEngine.calcul.Point3D;
import graphicEngine.calcul.Quadri;
import utils.FlixBlocksUtils;
import utils.yaml.YAML;
import window.Displayable;
import window.Fen;
import window.KeyBoard;

public class Editor extends Environment3D implements Displayable, EnvironmentListener {

	private TexturePack texturePack;

	Fen fen;
	private PanEditor panel;

	// =============== World ===============
	private TickClock clock;

	private CubeClient cube;

	// =============== Rotation ===============
	double rotateSpeed = .2;

	// =============== Cursor ===============
	private Cursor cursorPaint;
	private Cursor cursorFill;
	private Cursor cursorSelectColor;
	private Cursor cursorSquareSelection;
	private Cursor cursorMoveSelection;

	// =============== Texture generation ===============
	private TextureCube textureCube;
	private static final int MAX_SIZE = 16;
	/** colors of the quadri [Face][y][x] */
	private int[][][] texture = new int[6][MAX_SIZE][MAX_SIZE];
	private int textureSize = 3;

	// =============== Buttons ===============
	private ActionEditor action = null;
	private ActionEditor buttonListeningKey = null;

	// =============== Paint Line/Square ===============
	private Face lastPaintFace = null;
	private int lastPaintCol = -1;
	private int lastPaintRow = -1;

	// =============== History ===============
	/** Store the modifications */
	private ArrayList<History> history = new ArrayList<>();
	/**
	 * Store the modifications to be packed together before insertion to #history
	 */
	private ArrayList<History> historyPack = new ArrayList<>();
	/** Index of the last modification (-1 means no previous modif) */
	private int historyPosition = -1;

	// =============== Layer ===============
	private static final int lineSquareLayer = 12;
	private static final int selectionLayer = 13;
	private static final int calkLayer = 14;

	// =============== Keys ===============
	private KeyboardEditor keyboard;

	// =============== Write ===============
	/** Store the value of the string being written */
	private String writingString = "";
	/** Store the value of the string before being modified (in case of undo) */
	private String realString = "";

	// =============== Calk ===============
	/** The full face copy of the copied face */
	private int[][] calk = new int[MAX_SIZE][MAX_SIZE];
	/** The mask of the copied pixels (true : copied | false : not) */
	private boolean[][] calkMask = new boolean[MAX_SIZE][MAX_SIZE];
	/** Coords of the bottom left corner */
	private int calkStartX, calkStartY;
	/** Size of the calk */
	private int calkSizeX, calkSizeY;

	/** Face on which the calk is */
	private Face calkFace = null;
	/** Location of the bottom left corner */
	private int calkCornerX, calkCornerY;

	/** true : currently have a floating calk */
	private boolean hasCalk = false;
	/** true : the cursor is actually in the floating calk */
	private boolean cursorInCalk = false;

	// =========================================================================================================================

	public Editor(Fen fen) {
		this.fen = fen;

		texturePack = ItemTableClient.getTexturePack();

		ResourceType.setTextureFolder(texturePack.getFolder());

		generateCursor();

		// ========================================================================================

		map = new MapClient();
		camera = new Camera(new Point3D(0, 0, -10), 90.5, .5);

		engine.setBackground(Engine.FILL);

		// ========================================================================================

		panel = new PanEditor(this);

		keyboard = new KeyboardEditor(this);
		keyboard.start();

		clock = new TickClock("Editor Clock");
		clock.add(map);
		clock.start();

		// ========================================================================================

		initTextureFrame();
		map.add(new Cube(ItemID.EDITOR_PREVIEW));
		cube = map.gridGet(0, 0, 0);

		// ========================================================================================

		// 0-5 : grid
		// 6 - 11: face name
		// 12 : line/square
		// 13 : selection
		// 13 : calk
		for (int i = 0; i <= 14; i++)
			cube.addLayer(null);

		start();
	}

	// =========================================================================================================================

	@Override
	public void stop() {
		super.stop();

		keyboard.stop();
		clock.stop();
	}

	// =========================================================================================================================
	// History

	/** Cancel the previous action */
	void undo() {
		if (!historyPack.isEmpty())
			historyPack();

		if (historyPosition == -1)
			return;

		history.get(historyPosition--).undo(this);
	}

	/** Cancel the previous cancel */
	void redo() {
		if (historyPosition + 1 >= history.size())
			return;

		history.get(++historyPosition).redo(this);
	}

	void historyPack() {
		if (historyPack.isEmpty())
			return;

		history.add(++historyPosition, new HistoryList(historyPack));

		while (history.size() > historyPosition + 1)
			history.remove(historyPosition + 1);

		historyPack = new ArrayList<>();
	}

	// =========================================================================================================================
	// Texture management

	private void initTextureFrame() {
		for (int face = 0; face < 6; face++)
			for (int i = 0; i < MAX_SIZE; i++)
				for (int j = 0; j < MAX_SIZE; j++)
					texture[face][i][j] = (i + j) % 2 == 0 ? 0xff888888 : 0xff555555;

		updatePreviewTexture();
	}

	private TextureCube createTexture() {
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
		// Repaint Miniature
		panel.get(ActionEditor.MINIATURE).repaint();
	}

	private void updatePreviewTexture(TextureCube tc, int id) {
		// Update TexturePack
		texturePack.setTextureCube(tc, id);
		// Update miniature preview
		panel.get(ActionEditor.MINIATURE).update();
		textureCube = tc;
	}

	public void setTextureSize(int textureSize) {
		panel.get(ActionEditor.GRID).setWheelStep(textureSize);
		this.textureSize = textureSize;
	}

	private void saveTexture() {
		int id = panel.get(ActionEditor.ITEM_ID).getWheelStep();
		String tag = panel.get(ActionEditor.ITEM_TAG).getString();
		int color = panel.get(ActionEditor.ITEM_COLOR).getValue();

		if (!texturePack.isIDAvailable(id))
			return;

		// Add to ItemTable
		ItemTable.addItem(new Item(id, tag));
		// Create and set textureCube | Update preview
		updatePreviewTexture(createTexture(), id);
		// Add the cube to the list of available cubes
		// session.fen.gui.infos.addCube(new Cube(id));

		// Save the cube in file
		YAML.encodeFile(textureCube.getYAML(id, tag, color), "resources/temp/" + tag.toLowerCase() + ".yml");
	}

	// =========================================================================================================================
	// Painting

	void paintPixel() {
		drawPixel(target.face, getTargetedY(), getTargetedX(), panel.panColor.getColor());
		updateLastPixel();
		updatePreviewTexture();
	}

	void paintLine() {
		Line l = new Line(getTargetedX(), getTargetedY(), lastPaintCol, lastPaintRow);

		for (int row = l.min; row <= l.max; row++)
			for (int col = l.getLeft(row); col <= l.getRight(row); col++)
				drawPixel(target.face, row, col, panel.panColor.getColor());

		updateLastPixel();
		updatePreviewTexture();
		historyPack();
	}

	void paintSquare() {
		int col1 = Math.min(getTargetedX(), lastPaintCol);
		int row1 = Math.min(getTargetedY(), lastPaintRow);
		int col2 = Math.max(getTargetedX(), lastPaintCol);
		int row2 = Math.max(getTargetedY(), lastPaintRow);

		for (int col = col1; col <= col2; col++)
			for (int row = row1; row <= row2; row++)
				drawPixel(target.face, row, col, panel.panColor.getColor());

		updateLastPixel();
		updatePreviewTexture();
		historyPack();
	}

	private void drawPixel(Face face, int col, int row, int color) {
		// Pack the previous history action if different from PAINT
		if (!historyPack.isEmpty() && !(historyPack.get(historyPack.size() - 1) instanceof PixelHistory))
			historyPack();

		if (texture[face.ordinal()][col][row] != color)
			historyPack.add(new PixelHistory(face, col, row, texture[face.ordinal()][col][row], color, lastPaintCol,
					lastPaintRow, getTargetedX(), getTargetedY()));

		setPixel(face, col, row, color);
	}

	public void setPixel(Face face, int col, int row, int color) {
		texture[face.ordinal()][col][row] = color;
	}

	// =========================================================================================================================
	// Calk

	/** Copy the current selection */
	void copy(Face face, int x1, int y1, int x2, int y2) {
		// Apply previous calk
		if (hasCalk)
			applyCalk();

		calkStartX = Math.min(x1, x2);
		calkStartY = Math.min(y1, y2);
		calkSizeX = Math.max(x1, x2) - calkStartX + 1;
		calkSizeY = Math.max(y1, y2) - calkStartY + 1;

		for (int x = 0; x < MAX_SIZE; x++)
			for (int y = 0; y < MAX_SIZE; y++) {
				calk[y][x] = texture[face.ordinal()][y][x];// Copy all the face
				calkMask[y][x] = false;// Reset mask
			}

		// Init Mask
		for (int x = 0; x < calkSizeX; x++)
			for (int y = 0; y < calkSizeY; y++)
				calkMask[calkStartY + y][calkStartX + x] = true;
	}

	/** Add a floatting calk of the last copied selection */
	void paste(Face face, int x, int y) {
		calkFace = face;
		calkCornerX = Math.max(x, 0);
		calkCornerY = Math.max(y, 0);

		hasCalk = true;
		refreshLayerCalk();
		keyboard.selectNothing();
	}

	/** Replace the color bellow the calk by the ones of the calk */
	void applyCalk() {
		if (calkCornerX < 0 || calkCornerY < 0 || calkCornerX + calkSizeX > textureSize
				|| calkCornerY + calkSizeY > textureSize) {
			System.err.println("OUT OF BOUNDS");
			return;
		}

		for (int x = 0; x < calkSizeX; x++)
			for (int y = 0; y < calkSizeY; y++)
				drawPixel(calkFace, calkCornerY + y, calkCornerX + x, calk[calkStartY + y][calkStartX + x]);

		historyPack();
		updatePreviewTexture();
		hasCalk = false;
		refreshLayerCalk();
	}

	void deleteCalk() {
		hasCalk = false;
		refreshLayerCalk();
	}

	/** The upper part will be set on the right side */
	void rotateCalkRight() {
		int sizeX = MAX_SIZE;
		int sizeY = MAX_SIZE;
		int[][] calk2 = new int[sizeY][sizeX];

		for (int x = 0; x < sizeX; x++)
			for (int y = 0; y < sizeY; y++)
				calk2[sizeX - 1 - x][y] = calk[y][x];

		int _calkStartX = calkStartX;
		calkStartX = calkStartY;
		calkStartY = sizeY - _calkStartX - calkSizeX;

		int _calkSizeX = calkSizeX;
		calkSizeX = calkSizeY;
		calkSizeY = _calkSizeX;

		calk = calk2;
		refreshLayerCalk();
	}

	void moveCalk(int x, int y) {
		calkCornerX += x;
		calkCornerY += y;
		refreshLayerCalk();
		updateCursorInCalk();
		fen.updateCursor();
	}

	void updateCursorInCalk() {
		int x = getTargetedX();
		int y = getTargetedY();
		cursorInCalk = target.face == calkFace && calkCornerX <= x && x < calkCornerX + calkSizeX && calkCornerY <= y
				&& y < calkCornerY + calkSizeY;
	}

	// =========================================================================================================================
	// Other tools

	/** Set the paint color to the picked one */
	void pickColor() {
		panel.panColor.setColor(texture[target.face.ordinal()][getTargetedY()][getTargetedX()]);
		// TODO [Improve] Update colorPan on color pick
	}

	/**
	 * Replace the color of the continuous zone containing coord (row, col) by the
	 * paint color
	 * 
	 * @param face
	 *            - the face to modify
	 * @param row
	 *            - the coord of a point in the zone
	 * @param col
	 *            - the coord of a point in the zone
	 */
	void initFill(Face face, int row, int col) {
		// New color must be different from the previous one
		if (texture[face.ordinal()][row][col] == panel.panColor.getColor())
			return;
		_fill(face.ordinal(), row, col, texture[face.ordinal()][row][col], panel.panColor.getColor());
	}

	/**
	 * Set the new color to the pixel [row, col] then call _fill(...) for the
	 * adjacent pixels
	 * 
	 * @param erasedColor
	 *            - the color to replace
	 * @param newColor
	 *            - the new color
	 * @param face
	 *            - the index of the face to modify
	 * @param row
	 *            - the coord of a point in the zone
	 * @param col
	 *            - the coord of a point in the zone
	 */
	private void _fill(int face, int row, int col, int erasedColor, int newColor) {
		if (row < 0 || textureSize <= row || col < 0 || textureSize <= col)
			return;

		// Stop the propagation if the color doesn't match the one to replace
		if (texture[face][row][col] != erasedColor)
			return;

		drawPixel(Face.faces[face], row, col, newColor);

		_fill(face, row + 1, col, erasedColor, newColor);
		_fill(face, row - 1, col, erasedColor, newColor);
		_fill(face, row, col + 1, erasedColor, newColor);
		_fill(face, row, col - 1, erasedColor, newColor);
	}

	// =========================================================================================================================
	// Memory

	public void setLastPixel(Face face, int col, int row) {
		lastPaintFace = face;
		lastPaintCol = col;
		lastPaintRow = row;
	}

	public void updateLastPixel() {
		setLastPixel(target.face, getTargetedX(), getTargetedY());
	}

	public boolean hasLastPixel() {
		return target.cube == cube && target.face == lastPaintFace && lastPaintCol < textureSize
				&& lastPaintRow < textureSize && lastPaintRow != -1;
	}

	// =========================================================================================================================
	// Buttons events

	public void menuClick(ActionEditor action) {
		mayLooseListeningKey(action);
		panel.helpTool.setVisible(false);

		switch (action) {
		case QUIT:// Close Editor
			fen.returnToMainMenu();
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
		case SQUARE_SELECTION:
			if (action == this.action)
				setAction(null);
			else {
				if (action == ActionEditor.SQUARE_SELECTION)
					panel.helpTool.setVisible(true);
				setAction(action);
			}
			break;

		case GRID:
			refreshLayerGrid();
			break;
		case MINIATURE:
			break;
		case CANCEL:
			break;

		// ================== PanColor ======================
		case VALID_COLOR:
			panel.panColor.selectColor();
			break;

		// ================== PanItem ======================
		case ITEM_TAG:
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
			panel.get(ActionEditor.ITEM_TAG).reinit();
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
			panel.get(action).setBool(texturePack.isIDAvailable(panel.get(action).getWheelStep()));
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
	// Write

	void write(KeyEvent e) {
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

	private void writeName(String str) {
		boolean valid = true;

		for (String name : ItemTable.getItemTagList())
			if (str.equals(name)) {
				valid = false;
				break;
			}

		panel.get(buttonListeningKey).setString(str);
		panel.get(buttonListeningKey).setBool(valid);
		panel.get(buttonListeningKey).updateData();
	}

	private void esc() {
		writeName(realString);
		panel.get(buttonListeningKey).setSelected(false);
		buttonListeningKey = null;
	}

	private void enter() {
		realString = writingString;
		esc();
	}

	private void setListeningKey(ActionEditor action) {
		if (buttonListeningKey != null && buttonListeningKey != action)
			looseListeningKey();

		buttonListeningKey = action;
		realString = panel.get(action).getString();
		writingString = panel.get(action).getString();
	}

	void looseListeningKey() {
		if (buttonListeningKey == null)
			return;

		esc();
	}

	private void mayLooseListeningKey(ActionEditor action) {
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
		double distY = camera.vue.dist(0.5, 0.5, 0.5);
		double angleY = camera.getVy() + y * -rotateSpeed;

		if (angleY >= 60)
			angleY = 59.9;
		else if (angleY <= -60)
			angleY = -59.9;

		camera.vue.y = .5 - Math.sin(FlixBlocksUtils.toRadian * angleY) * distY;
		double distX = Math.cos(FlixBlocksUtils.toRadian * angleY) * distY;

		double angleX = FlixBlocksUtils.toRadian * (camera.getVx() + x * rotateSpeed);

		camera.vue.x = .5 - distX * Math.cos(angleX);
		camera.vue.z = .5 - distX * Math.sin(angleX);

		lookCube();
	}

	public void rotateCamera(boolean forward, boolean backward, boolean right, boolean left) {
		int x = 0, y = 0;
		int speed = 15;

		// Slow down with shift
		if (keyboard.shiftDown)
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

	// =========================================================================================================================
	// Layers

	public void refreshLayerGrid() {
		for (Face face : Face.faces)
			if (panel.get(ActionEditor.GRID).isSelected()) {
				DrawLayer layer = new DrawLayer(cube, face);
				layer.drawGrid();
				cube.setLayer(face.ordinal() + 6, layer);
			} else
				cube.removeLayer(face.ordinal() + 6);
	}

	void refreshLayerFace() {
		for (Face face : Face.faces)
			if (keyboard.altDown) {
				DrawLayer layer = new DrawLayer(cube, face);
				layer.drawFace();
				cube.setLayer(face.ordinal(), layer);
			} else
				cube.removeLayer(face.ordinal());
	}

	void refreshLineSquareLayer() {
		if (keyboard.shiftDown && hasLastPixel()) {
			DrawLayer layer = new DrawLayer(cube, target.face);

			int col1 = getTargetedX();
			int row1 = getTargetedY();
			int col2 = lastPaintCol;
			int row2 = lastPaintRow;

			if (keyboard.controlDown) // Square
				layer.drawSquareAndCross(col1, row1, col2, row2, 0xffdddddd, 0xff555555);
			else // Line
				layer.drawLineAndCross(col1, row1, col2, row2, 0xffdddddd, 0xff555555);

			cube.setLayer(lineSquareLayer, layer);
		} else
			cube.removeLayer(lineSquareLayer);
	}

	void refreshLayerSelection(Face face, int x1, int y1, int x2, int y2) {
		DrawLayer layer = new DrawLayer(cube, face);
		layer.drawDottedSquare(x1, y1, x2, y2, 0xffffffff, 0xff000000, face);
		cube.setLayer(selectionLayer, layer);
	}

	void removeLayerSelection() {
		cube.removeLayer(selectionLayer);
	}

	void refreshLayerCalk() {
		if (hasCalk && calkFace != null) {
			DrawLayer layer = new DrawLayer(cube, calkFace);

			layer.drawDottedSquare(calkCornerX, calkCornerY, calkCornerX + calkSizeX - 1, calkCornerY + calkSizeY - 1,
					0xffffffff, 0xff000000, calkFace);

			for (int x = 0; x < calkSizeX; x++)
				for (int y = 0; y < calkSizeY; y++) {
					layer.fillSquare(calkCornerX + x, calkCornerY + y, calk[calkStartY + y][calkStartX + x], true, 1,
							0);
				}

			cube.setLayer(calkLayer, layer);
		} else
			cube.removeLayer(calkLayer);
	}

	// =========================================================================================================================
	// Cursor

	public Cursor getCursor() {
		Cursor cursor = Cursor.getDefaultCursor();

		if (getAction() == ActionEditor.PAINT)
			cursor = (keyboard.controlDown && (!keyboard.shiftDown || !isPreviewCube())) ? cursorSelectColor
					: cursorPaint;
		else if (getAction() == ActionEditor.FILL)
			cursor = keyboard.controlDown ? cursorSelectColor : cursorFill;
		else if (getAction() == ActionEditor.SQUARE_SELECTION)
			if (hasCalk) {
				if (cursorInCalk)
					cursor = cursorMoveSelection;
			} else
				cursor = cursorSquareSelection;

		return cursor;
	}

	private void generateCursor() {
		String folder = texturePack.getFolder() + "cursor/editor/";
		cursorPaint = FlixBlocksUtils.createCursor(folder + "cursorPaint");
		cursorFill = FlixBlocksUtils.createCursor(folder + "cursorFill");
		cursorSelectColor = FlixBlocksUtils.createCursor(folder + "cursorSelectColor");
		cursorSquareSelection = FlixBlocksUtils.createCursor(folder + "cursorSquareSelection");
		cursorMoveSelection = FlixBlocksUtils.createCursor(folder + "cursorMoveSelection");
	}

	// =========================================================================================================================
	// Mode getters

	public boolean isRotateMode() {
		return true;
	}

	public boolean isPreviewCube() {
		if (target.cube == null)
			return false;
		return target.cube.getItemID() == ItemID.EDITOR_PREVIEW;
	}

	// =========================================================================================================================

	Face getFrontFace() {
		if (camera.getVy() > 45)
			return Face.DOWN;
		if (camera.getVy() < -45)
			return Face.UP;
		return getCameraOrientation().face;
	}

	// =========================================================================================================================

	int getTargetedX() {
		return target.quadri % textureSize;
	}

	int getTargetedY() {
		return target.quadri / textureSize;
	}

	// =========================================================================================================================
	// Getters

	public ActionEditor getAction() {
		return action;
	}

	public void setAction(ActionEditor action) {
		this.action = action;
		fen.updateCursor();
	}

	public TexturePack getTexturePack() {
		return texturePack;
	}

	public boolean hasCalk() {
		return hasCalk;
	}

	public boolean isCursorInCalk() {
		return cursorInCalk;
	}

	public ActionEditor getButtonListeningKey() {
		return buttonListeningKey;
	}

	public Face getCalkFace() {
		return calkFace;
	}

	public int getTextureSize() {
		return textureSize;
	}

	// =========================================================================================================================
	// Environment

	@Override
	public void gainTarget() {
		if (keyboard.pressR) {
			looseTarget();
			return;
		}

		// If no quadri targeted -> no update
		if (target.quadri == Quadri.NOT_NUMBERED) {
			cursorInCalk = false;
			return;
		}

		updateCursorInCalk();

		if (action == ActionEditor.PAINT) {
			target.cube.setSelectedQuadri(target.face, target.quadri);

			cube.removeLayer(lineSquareLayer);

			if (keyboard.pressL) {
				paintPixel();
				return;
			}

			refreshLineSquareLayer();
		}

		fen.updateCursor();
	}

	@Override
	public void looseTarget() {
		// Removes highlight of previous selected quadri
		target.cube.setSelectedQuadri(null, CubeClient.NO_QUADRI);
		// Removes line/square preview
		target.cube.removeLayer(lineSquareLayer);

		cursorInCalk = false;
	}

	@Override
	public void oneSecondTick() {
	}

	@Override
	public boolean isNeededQuadriPrecision() {
		if (action == null)
			return false;

		switch (action) {
		case SQUARE_SELECTION:
		case PAINT:
		case FILL:
		case PLAYER_COLOR:
			return true;
		default:
			return false;
		}
	}

	// =========================================================================================================================
	// Displayable

	@Override
	public JPanel getContentPane() {
		return panel;
	}

	@Override
	public void updateSize(int x, int y) {
		panel.setSize(x, y);
	}

	@Override
	public KeyBoard getKeyBoard() {
		return keyboard;
	}
}
