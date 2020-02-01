package mainMenu;

import javax.swing.JPanel;

import editor.Editor;
import game.Game;
import mainMenu.MainMenuButton.LauncherButtonAction;
import window.Displayable;
import window.Fen;
import window.KeyBoard;

public class MainMenu extends JPanel implements Displayable {
	private static final long serialVersionUID = -3189420915172593199L;

	private Fen fen;

	private MainMenuButton game, editor, quit;

	private KeyBoardMainMenu keyboard = new KeyBoardMainMenu();

	// =========================================================================================================================

	public MainMenu(Fen fen) {
		this.fen = fen;
		this.setLayout(null);

		add(game = new MainMenuButton(this, LauncherButtonAction.GAME));
		add(editor = new MainMenuButton(this, LauncherButtonAction.EDITOR));
		add(quit = new MainMenuButton(this, LauncherButtonAction.QUIT));
	}

	// =========================================================================================================================

	public void click(LauncherButtonAction action) {
		switch (action) {
		case GAME:
			fen.setDisplay(new Game(fen));
			break;
		case EDITOR:
			fen.setDisplay(new Editor(fen));
			break;
		case QUIT:
			System.exit(0);
			break;

		default:
			break;
		}
	}

	// =========================================================================================================================

	@Override
	public JPanel getContentPane() {
		return this;
	}

	@Override
	public void updateSize(int x, int y) {
		setSize(x, y);

		game.setBounds(getWidth() / 2 - 100, 150, 200, 50);
		editor.setBounds(getWidth() / 2 - 100, 400, 200, 50);
		quit.setBounds(getWidth() / 2 - 100, 650, 200, 50);
	}

	@Override
	public KeyBoard getKeyBoard() {
		return keyboard;
	}
}
