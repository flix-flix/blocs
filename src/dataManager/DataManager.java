package dataManager;

import java.awt.Cursor;

import javax.swing.JPanel;

import window.Displayable;
import window.Fen;
import window.KeyBoard;

public class DataManager implements Displayable {

	private Fen fen;
	private PanDataManager panel;

	private KeyboardDataManager keyboard = new KeyboardDataManager();

	// =========================================================================================================================

	public DataManager(Fen fen) {
		this.fen = fen;

		panel = new PanDataManager(this);
	}

	// =========================================================================================================================

	public void click(ActionDataManager action) {
		switch (action) {
		case LANG:
			break;
		case TEXTURE:
			break;

		case RENAME:
			break;
		case SHOW_FIELDS:
			break;

		case QUIT:
			fen.returnToMainMenu();
			break;

		default:
			break;
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
	public Cursor getCursor() {
		return Cursor.getDefaultCursor();
	}

	@Override
	public KeyBoard getKeyBoard() {
		return keyboard;
	}

	@Override
	public void stop() {
	}
}
