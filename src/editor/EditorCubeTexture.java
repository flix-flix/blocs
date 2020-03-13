package editor;

import java.awt.Cursor;
import java.io.File;

import data.id.Item;
import data.id.ItemID;
import data.id.ItemTable;
import data.map.Cube;
import data.map.enumerations.Face;
import data.map.enumerations.Orientation;
import editor.history.PixelHistory;
import editor.history.SizeHistory;
import editor.panels.PanEditor;
import editor.tips.TipCalk;
import editor.tips.TipPencil;
import environment.extendsData.CubeClient;
import environment.extendsEngine.DrawLayer;
import environment.textures.TextureCube;
import environment.textures.TextureFace;
import environment.textures.TextureSquare;
import graphicEngine.calcul.Line;
import graphicEngine.calcul.Quadri;
import utils.Utils;
import utils.panels.ClickListener;
import utils.yaml.YAML;
import utilsBlocks.ButtonCube;

public class EditorCubeTexture extends EditorAbstract {

	// =============== Buttons ===============
	private ActionEditor action = null;

	// =============== World ===============
	private CubeClient cube;

	// =============== Texture generation ===============
	private TextureCube textureCube;
	private static final int MAX_SIZE = 16;
	/** Colors of the quadri <strong>([face][x][y])</strong> */
	private int[][][] texture = new int[6][MAX_SIZE][MAX_SIZE];
	private int textureSize = 3;

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
	private int savedId;
	private String savedTag;
	private int savedColor;
	private String savedFileName;

	// =============== Cursor ===============
	private Cursor cursorPaint, cursorFill, cursorSelectColor, cursorSquareSelection, cursorMoveSelection;

	// =============== Paint Line/Square ===============
	private Face lastPaintFace = null;
	private int lastPaintX = -1, lastPaintY = -1;

	// =============== Layer ===============
	private static final int lineSquareLayer = 12;
	private static final int selectionLayer = 13;
	private static final int calkLayer = 14;

	private boolean showFaceName = false;

	// =============== Inputs ===============
	public KeyboardEditorCubeTexture keyboard;

	// =========================================================================================================================

	public EditorCubeTexture(EditorManager editor) {
		super(editor, ActionEditor.EDIT_CUBE_TEXTURE);

		initTextureFrame();
		map.add(new Cube(ItemID.EDITOR_PREVIEW));
		cube = map.gridGet(0, 0, 0);

		// ========================================================================================

		keyboard = new KeyboardEditorCubeTexture(this);

		// ========================================================================================

		// 0-5 : grid
		// 6 - 11: face name
		// 12 : line/square
		// 13 : selection
		// 13 : calk
		for (int i = 0; i <= 14; i++)
			cube.addLayer(null);
	}

	// =========================================================================================================================

	@Override
	public void show() {
		panel.get(ActionEditor.ITEM_COLOR).setVisible(true);

		if (action == null)
			return;

		ActionEditor _action = action;
		action = null;
		action(_action);
	}

	@Override
	public void hide() {
		panel.cardsSquare.hide();
		panel.helpPencil.setVisible(false);
		panel.helpCalk.setVisible(false);

		panel.get(ActionEditor.ITEM_COLOR).setVisible(false);
	}

	@Override
	public boolean isSaved() {
		// TODO [Feature] Is texture saved ?
		return history.isEmpty();
	}

	@Override
	void historyPack() {
		super.historyPack();
		panel.get(ActionEditor.EDIT_CUBE_TEXTURE).setSaved(false);
	}

	// =========================================================================================================================

	@Override
	public void action(ActionEditor action) {
		switch (action) {
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
			panel.cardsSquare.hide();

			if (action == this.action)
				this.action = null;
			else {
				if (action == ActionEditor.PAINT || action == ActionEditor.FILL)
					panel.cardsSquare.show(PanEditor.COLOR);
				if (action == ActionEditor.SQUARE_SELECTION) {
					panel.helpCalk.setTip(TipCalk.values()[0]);
					panel.helpCalk.setVisible(true);
				} else if (action == ActionEditor.PAINT) {
					panel.helpPencil.setTip(TipPencil.values()[0]);
					panel.helpPencil.setVisible(true);
				}
				this.action = action;
			}
			editorMan.fen.updateCursor();
			break;

		case GRID:
			refreshLayerGrid();
			break;

		// ================== PanItem ======================
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
			if (savedTag != null)
				saveTextureSaved();
			break;

		default:
			break;
		}

	}

	@Override
	public void wheel(ActionEditor action) {
		switch (action) {
		// ================== ? ======================
		case ALONE:
			break;
		case DECOR:
			break;

		case GRID:
			historyPack.add(new SizeHistory(textureSize, textureSize = panel.get(ActionEditor.GRID).getWheelStep()));
			updatePreviewTexture();
			refreshLayerGrid();
			break;
		case MINIATURE_CUBE_TEXTURE:
			break;
		case PLAYER_COLOR:
			break;

		// ================== PanColor ======================
		case SELECT_ALPHA:
			break;

		default:
			break;
		}
	}

	// =========================================================================================================================
	// Texture management

	private void initTextureFrame() {
		for (Face face : Face.faces)
			for (int x = 0; x < MAX_SIZE; x++)
				for (int y = 0; y < MAX_SIZE; y++)
					drawPixel(face, x, y, (x + y) % 2 == 0 ? 0xff888888 : 0xff555555);

		if (!history.isEmpty())
			historyPack();
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
		panel.get(ActionEditor.MINIATURE_CUBE_TEXTURE).repaint();
	}

	private void updatePreviewTexture(TextureCube tc, int id) {
		// Update TexturePack
		texturePack.setTextureCube(tc, id);
		// Update miniature preview
		panel.get(ActionEditor.MINIATURE_CUBE_TEXTURE).update();
		textureCube = tc;
	}

	public void setTextureSize(int textureSize) {
		panel.get(ActionEditor.GRID).setWheelStep(textureSize);
		this.textureSize = textureSize;
	}

	/**
	 * Call {@link #saveTexture(int, String, int)} <br>
	 * If file already exists ask confirmation to user
	 */
	private void saveTextureConfirm() {
		int saveId = panel.get(ActionEditor.ITEM_ID).getWheelStep();
		String saveTag = panel.get(ActionEditor.ITEM_TAG).getString();

		// TODO [Feature] Allow replace existant Data
		// Ignore if already exists
		if (ItemTable.getItemIDList().contains(saveId) || ItemTable.getItemTagList().contains(saveTag))
			return;

		this.savedId = saveId;
		this.savedTag = saveTag;
		savedColor = panel.get(ActionEditor.ITEM_COLOR).getValue();

		savedFileName = "edited/cubeTextures/" + saveTag.toLowerCase() + ".yml";

		if (new File(savedFileName).exists())
			panel.confirmSaveOnExistant();
		else
			saveTextureSaved();
	}

	/** Call {@link #saveTexture(int, String, int)} with saved data */
	public void saveTextureSaved() {
		saveTexture(savedId, savedTag, savedColor);
	}

	/** Save texture in YAML and load it in {@link ItemTable} */
	public void saveTexture(int id, String tag, int color) {
		yaml = textureCube.getYAML(id, color);

		// Add to ItemTable
		ItemTable.addItem(new Item(id, tag));

		// TODO [Improve] Add cubes to other editors
		// Add to Multibloc Editor
		ButtonCube button = new ButtonCube(new Cube(id));
		button.setClickListener(new ClickListener() {
			@Override
			public void leftClick() {
				editorMan.editor.clickCube(new Cube(id));
			}
		});

		panel.gridCubes.gridAdd(button);
		panel.buttonsCubes.add(button);
		ButtonCube.group(panel.buttonsCubes);

		// Create and set textureCube | Update preview
		updatePreviewTexture(createTexture(), id);

		YAML.encodeFile(yaml, savedFileName);

		editorMan.updateButtonsItem();
		panel.get(ActionEditor.EDIT_CUBE_TEXTURE).setSaved(true);
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
		editorMan.fen.updateCursor();
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

	void switchFaceName() {
		showFaceName = !showFaceName;

		for (Face face : Face.faces)
			if (showFaceName) {
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

	public boolean isPreviewCube() {
		if (target.cube == null)
			return false;
		return target.cube.getItemID() == ItemID.EDITOR_PREVIEW;
	}

	// =========================================================================================================================

	int getTargetedX() {
		return editorMan.target.quadri % textureSize;
	}

	int getTargetedY() {
		return editorMan.target.quadri / textureSize;
	}

	// =========================================================================================================================
	// Getters

	public ActionEditor getAction() {
		return action;
	}

	public boolean hasCalk() {
		return hasCalk;
	}

	public boolean isCursorInCalk() {
		return cursorInCalk;
	}

	public Face getCalkFace() {
		return calkFace;
	}

	public int getTextureSize() {
		return textureSize;
	}

	// =========================================================================================================================

	Face getFrontFace() {
		if (editorMan.getCamera().getVy() > 45)
			return Face.DOWN;
		if (editorMan.getCamera().getVy() < -45)
			return Face.UP;
		return Orientation.getOrientation(editorMan.getCamera().getVx()).face;
	}

	// =========================================================================================================================

	@Override
	public void updateAfterUndoRedo() {
		updatePreviewTexture();
		refreshLayerGrid();
	}

	// =========================================================================================================================

	@Override
	public void gainTarget() {
		super.gainTarget();

		if (keyboard.pressR) {
			loseTarget();
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
	}

	@Override
	public void loseTarget() {
		super.loseTarget();
		// Removes highlight of previous selected quadri
		target.cube.setSelectedQuadri(null, CubeClient.NO_QUADRI);
		// Removes line/square preview
		target.cube.removeLayer(lineSquareLayer);

		cursorInCalk = false;
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

	@Override
	public Cursor getCursor() {
		if (getAction() == ActionEditor.PAINT)
			return (keyboard.controlDown && (!keyboard.shiftDown || !isPreviewCube())) ? cursorSelectColor
					: cursorPaint;
		else if (getAction() == ActionEditor.FILL)
			return keyboard.controlDown ? cursorSelectColor : cursorFill;
		else if (getAction() == ActionEditor.SQUARE_SELECTION)
			if (hasCalk) {
				if (cursorInCalk)
					return cursorMoveSelection;
			} else
				return cursorSquareSelection;

		return Cursor.getDefaultCursor();
	}

	@Override
	protected void generateCursor() {
		String folder = texturePack.getFolder() + "cursor/editor/";
		cursorPaint = Utils.createCursor(folder + "cursorPaint");
		cursorFill = Utils.createCursor(folder + "cursorFill");
		cursorSelectColor = Utils.createCursor(folder + "cursorSelectColor");
		cursorSquareSelection = Utils.createCursor(folder + "cursorSquareSelection");
		cursorMoveSelection = Utils.createCursor(folder + "cursorMoveSelection");
	}

	@Override
	public KeyboardEditor getKeyBoard() {
		return keyboard;
	}
}
