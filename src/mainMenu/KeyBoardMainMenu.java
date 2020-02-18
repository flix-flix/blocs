package mainMenu;

import java.awt.event.KeyEvent;

import window.Key;
import window.KeyBoard;

public class KeyBoardMainMenu implements KeyBoard {

	MainMenu main;

	// =========================================================================================================================

	public KeyBoardMainMenu(MainMenu main) {
		this.main = main;
	}

	// =========================================================================================================================

	@Override
	public void keyPressed(KeyEvent e) {
		if (main.keys.isVisible())
			main.keys.keyPressed(e.getKeyCode());

		if (main.panServer.isVisible())
			if (Key.PAUSE.code == e.getKeyCode())
				main.panServer.panAdd.close();
	}
}
