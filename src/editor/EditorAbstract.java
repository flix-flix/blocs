package editor;

import java.awt.Cursor;
import java.util.ArrayList;

import data.id.ItemTableClient;
import data.map.Cube;
import editor.history.History;
import editor.history.HistoryList;
import editor.panels.PanEditor;
import environment.Target;
import environment.extendsData.MapClient;
import environment.textures.TexturePack;
import graphicEngine.calcul.Camera;
import graphicEngine.calcul.Point3D;

public abstract class EditorAbstract {

	EditorManager editorMan;
	ActionEditor type;
	protected PanEditor panel;
	public MapClient map;

	protected TexturePack texturePack;

	public Camera camera;
	public Target target;

	// =============== History ===============
	// TODO suppressWarnings History
	/** Store the modifications */
	@SuppressWarnings("rawtypes")
	protected ArrayList<History> history = new ArrayList<>();
	/**
	 * Store the modifications to be packed together before insertion to #history
	 */
	@SuppressWarnings("rawtypes")
	protected ArrayList<History> historyPack = new ArrayList<>();
	/** Index of the last modification (-1 means no previous modif) */
	private int historyPosition = -1;

	// =============== Rotation ===============
	protected Point3D rotationPoint = new Point3D(.5, .5, .5);

	// =========================================================================================================================

	public EditorAbstract(EditorManager editor, ActionEditor type) {
		this.editorMan = editor;
		this.type = type;

		panel = editor.panel;

		texturePack = ItemTableClient.getTexturePack();
		generateCursor();

		map = new MapClient();
		map.range = 20;
		camera = new Camera(new Point3D(4, 2, -4), 60.5, -20.5);
	}

	// =========================================================================================================================

	public void show() {
	}

	public void hide() {
	}

	public boolean isSaved() {
		return true;
	}

	/** Called after environment repaint */
	public void repainted() {
	}

	// =========================================================================================================================

	public abstract void action(ActionEditor action);

	public void clickCube(Cube cube) {
	}

	public abstract void wheel(ActionEditor action);

	// =========================================================================================================================
	// History

	/** Cancel the previous action */
	@SuppressWarnings("unchecked")
	public void undo() {
		if (!historyPack.isEmpty())
			historyPack();

		if (historyPosition == -1)
			return;

		history.get(historyPosition--).undo(this);
	}

	/** Cancel the previous cancel */
	@SuppressWarnings("unchecked")
	void redo() {
		if (historyPosition + 1 >= history.size())
			return;

		history.get(++historyPosition).redo(this);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	void historyPack() {
		if (historyPack.isEmpty())
			return;

		history.add(++historyPosition, new HistoryList(historyPack));

		while (history.size() > historyPosition + 1)
			history.remove(historyPosition + 1);

		historyPack = new ArrayList<>();
	}

	public abstract void updateAfterUndoRedo();

	// =========================================================================================================================

	public void gainTarget() {
		target = editorMan.getTarget();
	}

	public void loseTarget() {
	}

	public abstract boolean isNeededQuadriPrecision();

	// =========================================================================================================================

	protected abstract void generateCursor();

	public abstract Cursor getCursor();

	public abstract KeyboardEditor getKeyBoard();
}
