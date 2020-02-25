package editor;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.io.File;
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
import editor.panels.ButtonEditor;
import editor.panels.PanEditor;
import editor.tips.TipCalk;
import editor.tips.TipPencil;
import environment.Environment3D;
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
import utils.Utils;
import utils.yaml.YAML;
import window.Displayable;
import window.Fen;
import window.KeyBoard;

public class Editor extends Environment3D implements Displayable {

	private TexturePack texturePack;

	public Fen fen;
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
	/** Colors of the quadri <strong>([face][x][y])</strong> */
	private int[][][] texture = new int[6][MAX_SIZE][MAX_SIZE];
	private int textureSize = 3;

	// =============== Buttons ===============
	private ActionEditor action = null;
	private ActionEditor buttonListeningKey = null;

	// =============== Paint Line/Square ===============
	private Face lastPaintFace = null;
	private int lastPaintX = -1, lastPaintY = -1;

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
	/** The full face copy of the copied face <strong>([x][y])</strong> */
	private int[][] calk = new int[MAX_SIZE][MAX_SIZE];
	/**
	 * The mask of the copied pixels (true : copied | false : not)
	 * <strong>([x][y])</strong>
	 */
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

	// =============== Save ===============
	/** YAML representation of the texture to save */
	private YAML yaml;
	private int saveId;
	private String saveTag;
	private int saveColor;

	// =========================================================================================================================

	public Editor(Fen fen) {
		this.fen = fen;

		texturePack = ItemTableClient.getTexturePack();

		ResourceType.setTextureFolder(texturePack.getFolder());

		generateCursor();

		// ========================================================================================

		map = new MapClient();
		camera = new Camera(new Point3D(4, 1, -4), 90.5, .5);

		engine.setBackground(Engine.FILL);

		// ========================================================================================

		panel = new PanEditor(this);
		updateButtonsItem();

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
			for (int x = 0; x < MAX_SIZE; x++)
				for (int y = 0; y < MAX_SIZE; y++)
					texture[face][x][y] = (x + y) % 2 == 0 ? 0xff888888 : 0xff555555;

		updatePreviewTexture();
	}

	private TextureCube createTexture() {
		TextureFace[] tf = new TextureFace[6];

		for (int face = 0; face < 6; face++) { // Generates faces
			int[] tab = new int[textureSize * textureSize];

			for (int k = 0; k < tab.length; k++) // Generates data-arrays
				tab[k] = texture[face][k % textureSize][k / textureSize];

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

	/**
	 * Call {@link #saveT(int, String, int)} <br>
	 * If file already exists ask confirmation to user
	 */
	private void saveTextureConfirm() {
		saveId = panel.get(ActionEditor.ITEM_ID).getWheelStep();
		saveTag = panel.get(ActionEditor.ITEM_TAG).getString();
		saveColor = panel.get(ActionEditor.ITEM_COLOR).getValue();

		// TODO [Feature] Allow replace existant Data
		// Ignore if already exists
		if (ItemTable.getItemIDList().contains(saveId) || ItemTable.getItemTagList().contains(saveTag))
			return;

		String fileName = "resources/temp/" + saveTag.toLowerCase() + ".yml";

		if (new File(fileName).exists())
			panel.confirmSaveOnExistant();
		else
			saveTextureSaved();
	}

	/** Call {@link #saveT(int, String, int)} with saved data */
	public void saveTextureSaved() {
		saveT(saveId, saveTag, saveColor);
	}

	/** Save texture in YAML and load it in {@link ItemTable} */
	public void saveT(int id, String tag, int color) {

		yaml = textureCube.getYAML(saveId, saveTag, color);
		String fileName = "resources/temp/" + saveTag.toLowerCase() + ".yml";

		// Add to ItemTable
		ItemTable.addItem(new Item(saveId, saveTag));
		// Create and set textureCube | Update preview
		updatePreviewTexture(createTexture(), saveId);

		YAML.encodeFile(yaml, fileName);

		updateButtonsItem();
	}

	// =========================================================================================================================
	// Painting

	void paintPixel() {
		drawPixel(target.face, getTargetedX(), getTargetedY(), panel.panColor.getColor());
		updateLastPixel();
		updatePreviewTexture();
		panel.panColor.addColorMemory();
	}

	void paintLine() {
		Line l = new Line(getTargetedX(), getTargetedY(), lastPaintX, lastPaintY);

		for (int y = l.min; y <= l.max; y++)
			for (int x = l.getLeft(y); x <= l.getRight(y); x++)
				drawPixel(target.face, x, y, panel.panColor.getColor());

		updateLastPixel();
		updatePreviewTexture();
		historyPack();
		panel.panColor.addColorMemory();
	}

	void paintSquare() {
		int xMin = Math.min(getTargetedX(), lastPaintX);
		int yMin = Math.min(getTargetedY(), lastPaintY);
		int xMax = Math.max(getTargetedX(), lastPaintX);
		int yMax = Math.max(getTargetedY(), lastPaintY);

		for (int x = xMin; x <= xMax; x++)
			for (int y = yMin; y <= yMax; y++)
				drawPixel(target.face, x, y, panel.panColor.getColor());

		updateLastPixel();
		updatePreviewTexture();
		historyPack();
		panel.panColor.addColorMemory();
	}

	private void drawPixel(Face face, int x, int y, int color) {
		// Pack the previous history action if different from PAINT
		if (!historyPack.isEmpty() && !(historyPack.get(historyPack.size() - 1) instanceof PixelHistory))
			historyPack();

		if (texture[face.ordinal()][x][y] != color)
			historyPack.add(new PixelHistory(face, x, y, texture[face.ordinal()][x][y], color, lastPaintX, lastPaintY,
					getTargetedX(), getTargetedY()));

		setPixel(face, x, y, color);
	}

	public void setPixel(Face face, int x, int y, int color) {
		texture[face.ordinal()][x][y] = color;
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
				calk[x][y] = texture[face.ordinal()][x][y];// Copy all the face
				calkMask[x][y] = false;// Reset mask
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
				|| calkCornerY + calkSizeY > textureSize)
			return;

		for (int x = 0; x < calkSizeX; x++)
			for (int y = 0; y < calkSizeY; y++)
				drawPixel(calkFace, calkCornerX + x, calkCornerY + y, calk[calkStartX + x][calkStartY + y]);

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
		int[][] calk2 = new int[sizeX][sizeY];

		for (int x = 0; x < sizeX; x++)
			for (int y = 0; y < sizeY; y++)
				calk2[y][sizeX - 1 - x] = calk[x][y];

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
		panel.panColor.setColor(texture[target.face.ordinal()][getTargetedX()][getTargetedY()]);
	}

	/**
	 * Replace the color of the continuous zone containing coord (x, y) by the paint
	 * color
	 * 
	 * @param face
	 *            - the face to modify
	 * @param x
	 *            - the coord of a point in the zone
	 * @param y
	 *            - the coord of a point in the zone
	 */
	void initFill(Face face, int x, int y) {
		// New color must be different from the previous one
		if (texture[face.ordinal()][x][y] == panel.panColor.getColor())
			return;
		_fill(face.ordinal(), x, y, texture[face.ordinal()][x][y], panel.panColor.getColor());

		panel.panColor.addColorMemory();
	}

	/**
	 * Set the new color to the pixel [x, y] then call _fill(...) for the adjacent
	 * pixels
	 * 
	 * @param face
	 *            - the index of the face to modify
	 * @param x
	 *            - the coord of a point in the zone
	 * @param y
	 *            - the coord of a point in the zone
	 * @param erasedColor
	 *            - the color to replace
	 * @param newColor
	 *            - the new color
	 */
	private void _fill(int face, int x, int y, int erasedColor, int newColor) {
		if (x < 0 || textureSize <= x || y < 0 || textureSize <= y)
			return;

		// Stop the propagation if the color doesn't match the one to replace
		if (texture[face][x][y] != erasedColor)
			return;

		drawPixel(Face.faces[face], x, y, newColor);

		_fill(face, x, y + 1, erasedColor, newColor);
		_fill(face, x, y - 1, erasedColor, newColor);
		_fill(face, x + 1, y, erasedColor, newColor);
		_fill(face, x - 1, y, erasedColor, newColor);
	}

	// =========================================================================================================================
	// Memory

	public void setLastPixel(Face face, int x, int y) {
		lastPaintFace = face;
		lastPaintX = x;
		lastPaintY = y;
	}

	public void updateLastPixel() {
		setLastPixel(target.face, getTargetedX(), getTargetedY());
	}

	public boolean hasLastPixel() {
		return target.cube == cube && target.face == lastPaintFace && lastPaintX < textureSize
				&& lastPaintY < textureSize && lastPaintY != -1;
	}

	// =========================================================================================================================
	// Buttons events

	public void buttonClick(ActionEditor action) {
		mayLooseListeningKey(action);

		switch (action) {
		case QUIT:// Close Editor
			// TODO [Feature] Count modifications since last save
			if (history.isEmpty())
				fen.returnToMainMenu();
			else {
				setPaused(true);
				panel.confirmReturnToMainMenu();
			}
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
		case SQUARE_SELECTION:
		case FILL:
		case PLAYER_COLOR:
			panel.helpCalk.setVisible(false);
			panel.helpPencil.setVisible(false);
			panel.panColor.setVisible(false);

			if (action == this.action)
				setAction(null);
			else {
				panel.panColor.setVisible(action == ActionEditor.PAINT || action == ActionEditor.FILL);
				if (action == ActionEditor.SQUARE_SELECTION) {
					panel.helpCalk.setTip(TipCalk.values()[0]);
					panel.helpCalk.setVisible(true);
				} else if (action == ActionEditor.PAINT) {
					panel.helpPencil.setTip(TipPencil.values()[0]);
					panel.helpPencil.setVisible(true);
				}
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

		// ================== PanItem ======================
		case ITEM_TAG:
			if (panel.get(action).isSelected())
				setListeningKey(action);
			break;
		case ITEM_COLOR:
			panel.get(ActionEditor.ITEM_COLOR).setValue(panel.panColor.getColor() & 0xffffffff);
			break;

		case ITEM_SAVE:
			saveTextureConfirm();
			break;
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

	public void buttonWheel(ActionEditor action) {
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
			updateButtonsItem();
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

	private void updateButtonsItem() {
		ButtonEditor id = panel.get(ActionEditor.ITEM_ID);
		id.setBool(!ItemTable.getItemIDList().contains(id.getWheelStep()));

		ButtonEditor tag = panel.get(ActionEditor.ITEM_TAG);
		tag.setBool(!ItemTable.getItemTagList().contains(tag.getString()));
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
		camera.setVx(Utils.toDegres * Math.atan((camera.vue.x - .5) / -(camera.vue.z - .5)) + 90
				+ (camera.vue.z - .5 >= 0 ? 180 : 0));
		camera.setVy(Utils.toDegres * Math.atan(Math.hypot(camera.vue.x - .5, camera.vue.z - .5) / (camera.vue.y - .5))
				- 90 + (camera.vue.y - .5 <= 0 ? 180 : 0));
	}

	public void rotateCamera(int x, int y) {
		double distY = camera.vue.dist(0.5, 0.5, 0.5);
		double angleY = camera.getVy() + y * -rotateSpeed;

		if (angleY >= 60)
			angleY = 59.9;
		else if (angleY <= -60)
			angleY = -59.9;

		camera.vue.y = .5 - Math.sin(Utils.toRadian * angleY) * distY;
		double distX = Math.cos(Utils.toRadian * angleY) * distY;

		double angleX = Utils.toRadian * (camera.getVx() + x * rotateSpeed);

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

			int x1 = getTargetedX();
			int y1 = getTargetedY();
			int x2 = lastPaintX;
			int y2 = lastPaintY;

			if (keyboard.controlDown) // Square
				layer.drawSquareAndCross(x1, y1, x2, y2, 0xffdddddd, 0xff555555);
			else // Line
				layer.drawLineAndCross(x1, y1, x2, y2, 0xffdddddd, 0xff555555);

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
					layer.fillSquare(calkCornerX + x, calkCornerY + y, calk[calkStartX + x][calkStartY + y], true, 1,
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
		cursorPaint = Utils.createCursor(folder + "cursorPaint");
		cursorFill = Utils.createCursor(folder + "cursorFill");
		cursorSelectColor = Utils.createCursor(folder + "cursorSelectColor");
		cursorSquareSelection = Utils.createCursor(folder + "cursorSquareSelection");
		cursorMoveSelection = Utils.createCursor(folder + "cursorMoveSelection");
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
		} else if (action == ActionEditor.FILL)
			target.cube.setSelectedQuadri(target.face, target.quadri);

		fen.updateCursor();
	}

	@Override
	public void looseTarget() {
		// Removes highlight of previous selected quadri
		target.cube.setSelectedQuadri(null, CubeClient.NO_QUADRI);
		// Removes line/square preview
		target.cube.removeLayer(lineSquareLayer);

		cursorInCalk = false;

		fen.updateCursor();
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
