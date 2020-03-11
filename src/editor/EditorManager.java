package editor;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.util.TreeMap;

import javax.swing.JPanel;

import data.dynamic.TickClock;
import data.id.ItemTable;
import editor.panels.ButtonEditor;
import editor.panels.PanEditor;
import environment.Environment3D;
import environment.extendsData.MapClient;
import graphicEngine.calcul.Camera;
import graphicEngine.calcul.Engine;
import graphicEngine.calcul.Point3D;
import window.Displayable;
import window.Fen;
import window.KeyBoard;

public class EditorManager extends Environment3D implements Displayable {

	public Fen fen;
	PanEditor panel;

	// =============== Types ===============
	public TreeMap<ActionEditor, EditorAbstract> editors = new TreeMap<>();
	public ActionEditor editorType = ActionEditor.EDIT_CUBE_TEXTURE;
	public EditorAbstract editor;

	// =============== World ===============
	private TickClock clock;

	// =============== Write ===============
	private ActionEditor buttonListeningKey = null;

	/** Store the value of the string being written */
	private String writingString = "";
	/** Store the value of the string before being modified (in case of undo) */
	private String realString = "";

	// =========================================================================================================================

	public EditorManager(Fen fen) {
		super(new MapClient(), new Camera(new Point3D(0, 0, 0), 0, 0));
		this.fen = fen;

		// ========================================================================================

		engine.setBackground(Engine.FILL);

		// ========================================================================================

		panel = new PanEditor(this);
		updateButtonsItem();

		// TODO Editor Clock
		clock = new TickClock("Editor Clock");
		clock.add(map);

		// ========================================================================================

		editors.put(ActionEditor.EDIT_CUBE_TEXTURE, new EditorCubeTexture(this));
		editors.put(ActionEditor.EDIT_MULTI_CUBE, new EditorMultiCubes(this));

		action(ActionEditor.EDIT_CUBE_TEXTURE);

		start();
	}

	// =========================================================================================================================

	@Override
	public void start() {
		super.start();

		for (EditorAbstract editor : editors.values())
			editor.getKeyBoard().start();

		clock.start();
	}

	@Override
	public void stop() {
		super.stop();

		for (EditorAbstract editor : editors.values())
			editor.getKeyBoard().stop();

		clock.stop();
	}

	// =========================================================================================================================
	// Buttons events

	public void action(ActionEditor action) {
		mayLooseListeningKey(action);

		// Future Editors
		if (action == ActionEditor.EDIT_CUBE || action == ActionEditor.EDIT_MULTI_TEXTURE)
			return;

		if (action.isEditorType()) {
			if (editor != null)
				editor.hide();

			// ===== Set =====
			editorType = action;
			editor = editors.get(action);

			setCamera(editor.camera);
			setMap(editor.map);

			// ===== Show =====
			panel.cardsActions.show(action.name());
			editor.show();
		}

		else
			switch (action) {
			case CLOSE_EDITOR:// Close the Editor
				// TODO [Feature] Count modifications since last save
				int unsaved = 0;
				for (EditorAbstract editor : editors.values())
					if (!editor.isSaved())
						unsaved += 1;

				if (unsaved == 0)
					fen.returnToMainMenu();
				else {
					setPaused(true);
					panel.confirmReturnToMainMenu();
				}
				break;

			// ================== PanItem ======================
			case ITEM_TAG:
				if (panel.get(action).isSelected())
					setListeningKey(action);

			default:
				editor.action(action);
			}
	}

	public void buttonWheel(ActionEditor action) {
		mayLooseListeningKey(action);

		switch (action) {
		case ITEM_ID:
			updateButtonsItem();
			break;
		default:
			editor.wheel(action);
			break;
		}
	}

	// =========================================================================================================================
	// Write

	public void updateButtonsItem() {
		ButtonEditor id = panel.get(ActionEditor.ITEM_ID);
		id.setBool(!ItemTable.getItemIDList().contains(id.getWheelStep()));

		ButtonEditor tag = panel.get(ActionEditor.ITEM_TAG);
		tag.setBool(!ItemTable.getItemTagList().contains(tag.getString()));
	}

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

	public ActionEditor getButtonListeningKey() {
		return buttonListeningKey;
	}

	// =========================================================================================================================
	// Rotate-Mode

	public void rotateCamera(Point3D rotateCenter, boolean forward, boolean backward, boolean right, boolean left) {
		if (!(forward || backward || right || left))
			return;

		int x = 0, y = 0;
		int speed = 10;

		// TODO Slow down the rotate mode with shift
		if (editor != null && ((KeyboardEditor) editor.getKeyBoard()).shiftDown)
			speed = 3;

		if (right)
			x += speed;
		if (left)
			x -= speed;

		if (forward)
			y -= speed;
		if (backward)
			y += speed;

		camera.rotate(rotateCenter, x, y);
	}

	// =========================================================================================================================
	// Cursor

	public Cursor getCursor() {
		return editor.getCursor();
	}

	// =========================================================================================================================
	// Mode getters

	public boolean isRotateMode() {
		return true;
	}

	// =========================================================================================================================
	// Environment

	@Override
	public void updateTarget() {
		super.updateTarget();

		editor.target = target;
	}

	@Override
	public void gainTarget() {
		editor.gainTarget();

		fen.updateCursor();
	}

	@Override
	public void loseTarget() {
		editor.loseTarget();

		fen.updateCursor();
	}

	@Override
	public void oneSecondTick() {
	}

	@Override
	public boolean isNeededQuadriPrecision() {
		return editor.isNeededQuadriPrecision();
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
		return editor.getKeyBoard();
	}
}
