package editor;

import java.awt.Cursor;
import java.util.ArrayList;

import data.id.Item;
import data.id.ItemID;
import data.id.ItemTable;
import data.map.Chunk;
import data.map.Cube;
import data.map.MultiCube;
import data.map.enumerations.Face;
import editor.history.HistoryAddCube;
import editor.history.HistoryRemoveCube;
import editor.panels.PanEditor;
import environment.extendsData.CubeClient;
import environment.extendsEngine.DrawLayer;
import graphicEngine.calcul.Point3D;
import utils.yaml.YAML;

public class EditorMultiCubes extends EditorAbstract {

	public ActionEditor action = null;

	private CubeClient socle;

	public double modifiedAltitude = 0;

	public MultiCube multi = new MultiCube();

	private boolean showFaceName = false;

	// =============== Inputs ===============
	public KeyboardEditorMultiCubes keyboard;

	// =========================================================================================================================

	public EditorMultiCubes(EditorManager editor) {
		super(editor, ActionEditor.EDIT_MULTI_CUBE);

		keyboard = new KeyboardEditorMultiCubes(this);

		rotationPoint = new Point3D(.5, 2.5, .5);
		camera.moveLooking(rotationPoint, 3);

		Cube socle = new Cube(0, -1, 0, ItemID.EDITOR_SOCLE);
		socle.onGrid = false;
		map.add(socle);
		this.socle = map.get(socle);

		// 0-5: face name
		for (int i = 0; i <= 14; i++)
			this.socle.addLayer(null);
	}

	// =========================================================================================================================

	@Override
	public void show() {
		panel.cardsSquare.show(PanEditor.CUBES);
		panel.helpMultiCube.setVisible(true);
	}

	@Override
	public void hide() {
		panel.cardsSquare.hide();
		panel.helpMultiCube.setVisible(false);
	}

	@Override
	public boolean isSaved() {
		// TODO [Feature] Is multi saved ?
		return multi.list.isEmpty();
	}

	// =========================================================================================================================

	public void addCube() {
		Cube added;
		if ((added = editorMan.addCube()) == null)
			return;

		historyPack.add(new HistoryAddCube(target.getAir(), added.getItemID()));
		historyPack();

		multi.add(added);
	}

	public void removeCube() {
		if (target != null && target.cube != null) {
			historyPack.add(new HistoryRemoveCube(target.cube.coords(), target.cube.getItemID()));
			historyPack();
			map.remove(target.cube.coords());
			multi.remove(target.cube.coords());
		}
	}

	// =========================================================================================================================

	@Override
	public void action(ActionEditor action) {

		switch (action) {
		case DELETE_CUBE:
			if (this.action == action)
				this.action = null;
			else
				this.action = action;

			editorMan.panel.buttonsCubes.get(0).unselectAll();
			break;
		case MINIATURE:
			break;

		// ================== PanItem ======================
		case ITEM_CLEAR:
			for (Cube cube : multi.list)
				historyPack.add(new HistoryRemoveCube(cube.coords(), cube.getItemID()));

			historyPack();
			map.remove(multi.getCube());
			break;
		case ITEM_SAVE:
			saveMulti(panel.get(ActionEditor.ITEM_ID).getWheelStep(), panel.get(ActionEditor.ITEM_TAG).getString());
			break;

		default:
			break;
		}
	}

	@Override
	public void clickCube(Cube cube) {
		action = ActionEditor.ADD_CUBE;
		editorMan.panel.get(ActionEditor.DELETE_CUBE).setSelected(false);
		editorMan.setCubeToAdd(cube);
	}

	@Override
	public void wheel(ActionEditor action) {
	}

	// =========================================================================================================================

	/** Save MultiCube in YAML and load it in {@link ItemTable} */
	public void saveMulti(int id, String tag) {
		YAML yaml = new YAML();
		yaml.put("id", "" + id);
		ArrayList<YAML> list = new ArrayList<YAML>();

		for (Cube cube : multi.list) {
			YAML y = new YAML();
			y.put("id", "" + cube.getItemID());
			y.put("x", "" + cube.gridCoord.x);
			y.put("y", "" + cube.gridCoord.y);
			y.put("z", "" + cube.gridCoord.z);

			list.add(y);
		}

		yaml.put("multicube.cubes", list);

		String fileName = "edited/multiCubes/" + tag.toLowerCase() + ".yml";

		// Add to ItemTable
		ItemTable.addItem(new Item(yaml));

		YAML.encodeFile(yaml, fileName);

		editorMan.updateButtonsItem();
	}

	// =========================================================================================================================

	@Override
	public void updateAfterUndoRedo() {
	}

	@Override
	public Cursor getCursor() {
		return Cursor.getDefaultCursor();
	}

	@Override
	protected void generateCursor() {
	}

	@Override
	public KeyboardEditor getKeyBoard() {
		return keyboard;
	}

	// =========================================================================================================================
	// Layer

	void switchFaceName() {
		showFaceName = !showFaceName;

		for (Face face : Face.around)
			if (showFaceName) {
				DrawLayer layer = new DrawLayer(socle, face);
				layer.drawFace();
				socle.setLayer(face.ordinal(), layer);
			} else
				socle.removeLayer(face.ordinal());
	}

	// =========================================================================================================================

	@Override
	public void gainTarget() {
		super.gainTarget();

		if (action == ActionEditor.ADD_CUBE) {
			if (keyboard.pressR)
				return;
			if (target.getAir().y < 0 || target.getAir().y >= Chunk.Y)
				return;

			editorMan.addPreview();
		} else if (action == ActionEditor.DELETE_CUBE)
			map.setHighlight(target.cube, true);
	}

	@Override
	public void loseTarget() {
		super.loseTarget();

		editorMan.removePreview();

		map.setHighlight(target.cube, false);
	}

	@Override
	public boolean isNeededQuadriPrecision() {
		return false;
	}
}
