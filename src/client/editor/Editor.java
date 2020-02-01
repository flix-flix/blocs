package client.editor;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

import client.editor.history.History;
import client.editor.history.HistoryList;
import client.editor.history.PixelHistory;
import client.editor.history.SizeHistory;
import client.textures.TextureCube;
import client.textures.TextureFace;
import client.textures.TexturePack;
import client.textures.TextureSquare;
import client.window.Displayable;
import client.window.Fen;
import client.window.KeyBoard;
import client.window.graphicEngine.calcul.Camera;
import client.window.graphicEngine.calcul.Engine;
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
import data.map.resources.ResourceType;
import launcher.Environment3D;
import launcher.EnvironmentListner;
import launcher.Target;
import server.game.GameMode;
import utils.FlixBlocksUtils;
import utils.yaml.YAML;

public class Editor extends Environment3D implements Displayable, EnvironmentListner {

	public TexturePack texturePack;

	public Fen fen;
	private PanEditor panel;

	// ======================= World =========================
	public TickClock clock;

	private ModelCube cube;

	// ======================= GameMode =========================
	// TODO [Change] Gamemode -> CameraMode/EditorMode
	GameMode gamemode = GameMode.CLASSIC;

	// ======================= Target =========================
	private Target target;

	// ======================= Cursor =========================
	private Cursor cursorPaint;
	private Cursor cursorFill;
	private Cursor cursorSelectColor;
	private Cursor cursorSquareSelection;
	private Cursor cursorMoveSelection;

	// ======================= Texture generation =========================
	private TextureCube textureCube;
	private static final int MAX_SIZE = 16;
	/** [Face][y][x] */
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
	private static final int selectionLayer = 13;
	private static final int calkLayer = 14;

	// ======================= Rotation =========================
	private int prevX, prevY;

	// ======================= Keys =========================
	private KeyboardEditor keyboard;
	private static final int ALT = 18;
	private static final int SHIFT = 16;

	private boolean controlDown = false;
	private boolean shiftDown = false;
	private boolean altDown = false;

	// ======================= Write =========================
	private String writingString = "";
	private String realString = "";

	// ======================= Selection Square =========================
	private Face selectionFace;
	private int selectionStartX, selectionStartY;
	private int selectionEndX, selectionEndY;

	// ======================= Calk =========================
	/** The selection copied */
	private int[][] calk = new int[MAX_SIZE][MAX_SIZE];
	/** The mask of the copied pixels (true : copied | false : not) */
	private boolean[][] calkMask = new boolean[MAX_SIZE][MAX_SIZE];
	private int calkStartX, calkStartY;
	private int calkSizeX, calkSizeY;

	private Face calkFace = null;
	/** Location of the bottom left corner */
	private int calkCornerX, calkCornerY;

	private static final int VOID = -999;
	private int calkMoveClickX = VOID, calkMoveClickY = VOID;

	private boolean hasCalk = false;
	private boolean cursorInCalk = false;

	private Face lastClickedFace = null;
	private int lastClickedX = Quadri.NOT_NUMBERED;
	private int lastClickedY = Quadri.NOT_NUMBERED;

	// =========================================================================================================================

	public Editor(Fen fen) {
		this.fen = fen;

		texturePack = new TexturePack("classic");
		ItemTable.setTexturePack(texturePack);

		ResourceType.setTextureFolder(texturePack.getFolder());

		generateCursor();

		// ========================================================================================

		map = new ModelMap(texturePack);
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

		// TODO [Improve] Layers handled in ModelCube
		cube.layers = new ArrayList<>();
		// 0-5 : grid
		// 6 - 11: face name
		// 12 : line/square
		// 13 : selection
		// 13 : calk
		for (int i = 0; i <= 14; i++)
			cube.layers.add(null);

		start();
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
		texturePack.setTextureCube(tc, id);
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

	public void paintPixel() {
		drawPixel(target.face, getTargetedY(), getTargetedX(), panel.panColor.getColor());
		updateLastPixel();
		updatePreviewTexture();
	}

	public void paintLine() {
		Line l = new Line(getTargetedX(), getTargetedY(), lastPaintCol, lastPaintRow);

		for (int row = l.min; row <= l.max; row++)
			for (int col = l.getLeft(row); col <= l.getRight(row); col++)
				drawPixel(target.face, row, col, panel.panColor.getColor());

		updateLastPixel();
		updatePreviewTexture();
		historyPack();
	}

	public void paintSquare() {
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

	public void drawPixel(Face face, int col, int row, int color) {
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
	// Selection

	private void selectAll() {
		selectionFace = getSelectedFace();
		selectionStartX = 0;
		selectionStartY = 0;
		selectionEndX = textureSize - 1;
		selectionEndY = textureSize - 1;
	}

	private void selectNothing() {
		selectionFace = null;
	}

	private void copy() {
		if (selectionFace == null)
			return;

		// Apply previous calk
		if (hasCalk)
			applyCalk();

		calkStartX = Math.min(selectionStartX, selectionEndX);
		calkStartY = Math.min(selectionStartY, selectionEndY);
		calkSizeX = Math.max(selectionStartX, selectionEndX) - calkStartX + 1;
		calkSizeY = Math.max(selectionStartY, selectionEndY) - calkStartY + 1;

		for (int x = 0; x < MAX_SIZE; x++)
			for (int y = 0; y < MAX_SIZE; y++) {
				calk[y][x] = texture[selectionFace.ordinal()][y][x];// Copy all the face
				calkMask[y][x] = false;// Reset mask
			}

		// Init Mask
		for (int x = 0; x < calkSizeX; x++)
			for (int y = 0; y < calkSizeY; y++)
				calkMask[calkStartY + y][calkStartX + x] = true;
	}

	private void paste() {
		calkFace = getSelectedFace();
		calkCornerX = Math.max(lastClickedX, 0);
		calkCornerY = Math.max(lastClickedY, 0);

		hasCalk = true;
		refreshLayerCalk();
	}

	private void applyCalk() {
		if (calkCornerX < 0 || calkCornerY < 0 || calkCornerX + calkSizeX > textureSize
				|| calkCornerY + calkSizeY > textureSize) {
			System.err.println("OUT OF BOUNDS");
			return;
		}

		for (int x = 0; x < calkSizeX; x++)
			for (int y = 0; y < calkSizeY; y++)
				texture[calkFace.ordinal()][calkCornerY + y][calkCornerX + x] = calk[calkStartY + y][calkStartX + x];

		updatePreviewTexture();
		hasCalk = false;
		refreshLayerCalk();
	}

	private void deleteCalk() {
		hasCalk = false;
		refreshLayerCalk();
	}

	/** The upper part will be set on the right side */
	public void rotateCalkRight() {
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

	// =========================================================================================================================
	// Other tools

	private void selectColor() {
		panel.panColor.setColor(texture[target.face.ordinal()][getTargetedY()][getTargetedX()]);
	}

	private void fill(int erase, int face, int row, int col) {
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
		case EDITOR:// Close Editor
			setAction(null);
			fen.returnToLauncher();
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
	// KeyEvent

	public void keyPressed(KeyEvent e) {
		updateControlShiftStatus(e);

		int code = e.getKeyCode();

		// Show face names
		if (code == ALT)
			refreshLayerFace();

		// Undo/Redo
		if (e.isControlDown())
			if (code == 'Z') {
				undo();
				return;
			} else if (code == 'Y') {
				redo();
				return;
			}

		// Selection
		if (e.isControlDown())
			if (code == 'A') {
				if (e.isShiftDown())
					selectNothing();
				else
					selectAll();
				return;
			} else if (code == 'C') {
				copy();
				return;
			} else if (code == 'V') {
				paste();
				return;
			}

		// Calk
		if (hasCalk) {
			if (code == 10) {// Enter
				applyCalk();
				return;
			} else if (code == 37) {// Left
				rotateCalkRight();
				rotateCalkRight();
				rotateCalkRight();
				return;
			} else if (code == 39) {// Right
				rotateCalkRight();
				return;
			} else if (code == 27)// Esc
				deleteCalk();
		}

		// Consume SHIFT to allow line/square drawing
		if (code == SHIFT && action == ActionEditor.PAINT)
			return;

		// Writing
		if (buttonListeningKey == ActionEditor.ITEM_NAME) {
			write(e);
			return;
		}
	}

	public boolean keyReleased(KeyEvent e) {
		updateControlShiftStatus(e);

		int code = e.getKeyCode();

		if (code == ALT)
			refreshLayerFace();

		return false;
	}

	public void updateControlShiftStatus(KeyEvent e) {
		controlDown = e.isControlDown();
		shiftDown = e.isShiftDown();
		altDown = e.isAltDown();
	}

	// =========================================================================================================================
	// Write

	private void write(KeyEvent e) {
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

	private void looseListeningKey() {
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
		if (keyboard.pressR) {
			looseTarget();
			return;
		}

		// If no quadri targeted -> no update
		if (target.quadri == Quadri.NOT_NUMBERED) {
			cursorInCalk = false;
			return;
		}

		int x = getTargetedX();
		int y = getTargetedY();
		cursorInCalk = calkCornerX <= x && x < calkCornerX + calkSizeX && calkCornerY <= y
				&& y < calkCornerY + calkSizeY;

		if (action == ActionEditor.PAINT) {
			target.cube.setSelectedQuadri(target.face, target.quadri);

			cube.removeLayer(lineSquareLayer);

			if (keyboard.pressL) {
				paintPixel();
				return;
			}

			// Show Line/Square preview
			if (shiftDown && hasLastPixel()) {
				DrawLayer layer = new DrawLayer(cube, target.face);

				int col1 = getTargetedX();
				int row1 = getTargetedY();
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

	// =========================================================================================================================
	// Layers

	public void refreshLayerGrid() {
		for (Face face : Face.faces)
			if (panel.get(ActionEditor.GRID).isSelected()) {
				DrawLayer layer = new DrawLayer(cube, face);
				layer.drawGrid();
				cube.layers.set(face.ordinal() + 6, layer);
			} else
				cube.removeLayer(face.ordinal() + 6);
	}

	private void refreshLayerFace() {
		for (Face face : Face.faces)
			if (altDown) {
				DrawLayer layer = new DrawLayer(cube, face);
				layer.drawFace();
				cube.layers.set(face.ordinal(), layer);
			} else
				cube.removeLayer(face.ordinal());
	}

	private void refreshLayerSelection() {
		if (action == ActionEditor.SQUARE_SELECTION && selectionFace != null) {
			DrawLayer layer = new DrawLayer(cube, selectionFace);
			layer.drawDottedSquare(selectionStartX, selectionStartY, selectionEndX, selectionEndY, 0xffffffff,
					0xff000000, selectionFace);
			cube.layers.set(selectionLayer, layer);
		} else
			cube.removeLayer(selectionLayer);
	}

	private void refreshLayerCalk() {
		if (hasCalk && calkFace != null) {
			DrawLayer layer = new DrawLayer(cube, calkFace);

			layer.drawDottedSquare(calkCornerX, calkCornerY, calkCornerX + calkSizeX - 1, calkCornerY + calkSizeY - 1,
					0xffffffff, 0xff000000, calkFace);

			for (int x = 0; x < calkSizeX; x++)
				for (int y = 0; y < calkSizeY; y++) {
					layer.fillSquare(calkCornerX + x, calkCornerY + y, calk[calkStartY + y][calkStartX + x], true, 1,
							0);
				}

			cube.layers.set(calkLayer, layer);
		} else
			cube.removeLayer(calkLayer);
	}

	// =========================================================================================================================
	// Cursor

	public Cursor getCursor() {
		Cursor cursor = Cursor.getDefaultCursor();

		if (getAction() == ActionEditor.PAINT)
			cursor = (controlDown && (!shiftDown || !isPreviewCube())) ? cursorSelectColor : cursorPaint;
		else if (getAction() == ActionEditor.FILL)
			cursor = controlDown ? cursorSelectColor : cursorFill;
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
	// Mouse Event

	/** Return true if the event is consumed */
	public boolean leftClick() {
		looseListeningKey();

		lastClickedFace = target.face;
		lastClickedX = getTargetedX();
		lastClickedY = getTargetedY();

		// Click in void
		if (target.face == null || target.quadri == Quadri.NOT_NUMBERED) {
			// Loose selection
			if (action == ActionEditor.SQUARE_SELECTION) {
				calkMoveClickX = VOID;
				calkMoveClickY = VOID;
				selectionFace = null;
				refreshLayerSelection();
			}
			return false;
		}

		// Cancel action during rotation
		if (keyboard.pressR)
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
				int face = target.face.ordinal();
				int col = getTargetedX();
				int row = getTargetedY();

				fill(texture[face][row][col], face, row, col);

				updatePreviewTexture();
				historyPack();
			}
			break;

		case SQUARE_SELECTION:
			int x = getTargetedX();
			int y = getTargetedY();
			// Init move
			if (hasCalk) {
				if (cursorInCalk) {
					calkMoveClickX = x;
					calkMoveClickY = y;
				} else {
					calkMoveClickX = VOID;
					calkMoveClickY = VOID;
				}
				break;
			}
			// Init selection
			selectionFace = target.face;
			selectionStartX = x;
			selectionStartY = y;
			selectionEndX = selectionStartX;
			selectionEndY = selectionStartY;
			refreshLayerSelection();
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
		// Rotate
		if (keyboard.pressR) {
			rotateCamera(e.getX() - prevX, e.getY() - prevY);
			initDrag(e.getX(), e.getY());
		} else if (keyboard.pressL) {
			if (action == ActionEditor.SQUARE_SELECTION) {
				int x = getTargetedX();
				int y = getTargetedY();

				// Calk deplacement
				if (hasCalk) {
					if (target.quadri == Quadri.NOT_NUMBERED || calkMoveClickX == VOID || calkMoveClickY == VOID
							|| target.face != calkFace)
						return;

					calkCornerX += x - calkMoveClickX;
					calkCornerY += y - calkMoveClickY;

					calkMoveClickX = x;
					calkMoveClickY = y;

					refreshLayerCalk();
					return;
				}
				// Resize selection
				if (selectionFace != target.face)
					return;
				selectionEndX = x;
				selectionEndY = y;
				refreshLayerSelection();
			}
		}
	}

	public void cameraMoved() {
		if (isRotateMode())
			lookCube();
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

	private Face getSelectedFace() {
		if (lastClickedFace == null)
			return getFrontFace();
		return lastClickedFace;
	}

	private Face getFrontFace() {
		if (camera.getVy() > 45)
			return Face.DOWN;
		if (camera.getVy() < -45)
			return Face.UP;
		return camera.getOrientation().face;
	}

	// =========================================================================================================================

	private int getTargetedX() {
		return target.quadri % textureSize;
	}

	private int getTargetedY() {
		return target.quadri / textureSize;
	}

	// =========================================================================================================================

	public ActionEditor getAction() {
		return action;
	}

	public void setAction(ActionEditor action) {
		this.action = action;
		fen.updateCursor();
	}

	// =========================================================================================================================
	// Environment

	@Override
	public void gainTarget(Target target) {
		this.target = target;
		updateTarget();
	}

	@Override
	public void looseTarget() {
		// Removes highlight of previous selected quadri
		target.cube.setSelectedQuadri(null, ModelCube.NO_QUADRI);
		// Removes line/square preview
		target.cube.removeLayer(lineSquareLayer);
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
