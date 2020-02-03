package mainMenu;

import java.awt.event.KeyEvent;

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
		main.keys.keyPressed(e.getKeyCode());
	}
}
