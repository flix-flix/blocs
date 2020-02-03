package mainMenu;

import java.awt.Color;

import javax.swing.JPanel;

import editor.Editor;
import game.Game;
import mainMenu.buttons.ButtonEnv;
import mainMenu.buttons.ButtonLang;
import mainMenu.buttons.ButtonQuit;
import mainMenu.buttons.PanFelix;
import window.Displayable;
import window.Fen;
import window.KeyBoard;

public class MainMenu extends JPanel implements Displayable {
	private static final long serialVersionUID = -3189420915172593199L;

	private Fen fen;

	private ButtonEnv game, editor;

	private PanFelix felix;
	private ButtonLang lang;
	private ButtonQuit quit;

	private KeyBoardMainMenu keyboard = new KeyBoardMainMenu();

	// =========================================================================================================================

	public MainMenu(Fen fen) {
		this.fen = fen;
		this.setLayout(null);

		add(game = ButtonEnv.generateButton(this, ButtonAction.PLAY));
		add(editor = ButtonEnv.generateButton(this, ButtonAction.EDITOR));

		game.setSize(750, 500);
		editor.setSize(300, 300);

		game.setLocation(50, 50);
		editor.setLocation(50, 600);

		felix = new PanFelix();
		add(felix);

		lang = new ButtonLang(this);
		lang.setSize(100, 80);
		add(lang);

		quit = new ButtonQuit(this);
		quit.setSize(100, 100);
		add(quit);

		setBackground(Color.LIGHT_GRAY);
	}

	// =========================================================================================================================

	public void click(ButtonAction action) {
		switch (action) {
		case PLAY:
			game.stop();
			editor.stop();
			fen.setDisplay(new Game(fen));
			break;
		case EDITOR:
			game.stop();
			editor.stop();
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

	public void refreshLang() {
		game.refreshLang();
		editor.refreshLang();

		repaint();
	}

	// =========================================================================================================================

	@Override
	public JPanel getContentPane() {
		return this;
	}

	@Override
	public void updateSize(int x, int y) {
		setSize(x, y);

		game.setSize((int) (getHeight() * .75), getHeight() / 2);
		game.setLocation(getWidth() / 2 - game.getWidth() / 2, (int) (getHeight() * .07));

		editor.setSize((int) (getHeight() / 3), getHeight() / 3);
		editor.setLocation(getWidth() / 10, game.getLocation().y + game.getHeight() + (int) (getHeight() * .05));

		felix.setBottomRightCorner(getWidth() - quit.getWidth() - 10 - 10, getHeight() - 10);
		lang.setLocation(getWidth() - lang.getWidth() - 10,
				getHeight() - lang.getHeight() - 10 - quit.getHeight() - 10);
		quit.setLocation(getWidth() - quit.getWidth() - 10, getHeight() - quit.getHeight() - 10);
	}

	@Override
	public KeyBoard getKeyBoard() {
		return keyboard;
	}
	// =========================================================================================================================

	public enum ButtonAction {
		PLAY, EDITOR, QUIT;
	}
}
