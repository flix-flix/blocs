package mainMenu;

import java.awt.Color;
import java.awt.Cursor;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JPanel;

import data.id.ItemTableClient;
import dataManager.DataManager;
import editor.Editor;
import environment.textures.TexturePack;
import game.Game;
import mainMenu.buttons.ButtonAction;
import mainMenu.buttons.ButtonEnv;
import mainMenu.buttons.ButtonLang;
import mainMenu.buttons.PanFelix;
import window.Displayable;
import window.Fen;
import window.KeyBoard;

public class MainMenu extends JPanel implements Displayable {
	private static final long serialVersionUID = -3189420915172593199L;

	private Fen fen;

	private ButtonEnv game, editor;

	private ButtonAction data;

	private PanFelix felix;
	private ButtonLang lang;
	private ButtonAction options, quit;

	PanKeys keys;

	private KeyBoardMainMenu keyboard = new KeyBoardMainMenu(this);

	private int margin = 10;

	// =========================================================================================================================

	public MainMenu(Fen fen) {
		this.fen = fen;
		this.setLayout(null);

		ItemTableClient.setTexturePack(new TexturePack("classic"));

		// ========================================================================================

		keys = new PanKeys();
		keys.setSize(400, getHeight() * 3 / 4);
		keys.setLocation(getWidth() / 2 - keys.getWidth() / 2, getHeight() / 2 - keys.getHeight() / 2);
		add(keys);

		// ========================================================================================

		add(game = ButtonEnv.generateButton(this, MainMenuAction.PLAY));
		game.setSize(750, 500);
		game.setLocation(50, 50);

		add(editor = ButtonEnv.generateButton(this, MainMenuAction.EDITOR));
		editor.setSize(300, 300);
		editor.setLocation(50, 600);

		add(data = new ButtonAction(this, MainMenuAction.DATA_MANAGER));
		data.setSize(300, 300);
		data.setLocation(380, 600);

		felix = new PanFelix();
		add(felix);

		lang = new ButtonLang(this);
		lang.setSize(100, 80);
		add(lang);

		options = new ButtonAction(this, MainMenuAction.OPTIONS);
		options.setSize(100, 100);
		add(options);

		quit = new ButtonAction(this, MainMenuAction.QUIT);
		quit.setSize(100, 100);
		add(quit);

		// ========================================================================================

		setBackground(Color.LIGHT_GRAY);
	}

	// =========================================================================================================================

	public void click(MainMenuAction action) {
		switch (action) {
		case PLAY:
			if (false)
				try {
					fen.setDisplay(new Game(fen, InetAddress.getByName("0.0.0.0")));
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			else
				fen.setDisplay(Game.startLocalServer(fen));
			break;
		case SERVER:
			break;
		case EDITOR:
			fen.setDisplay(new Editor(fen));
			break;
		case DATA_MANAGER:
			fen.setDisplay(new DataManager(fen));
			break;
		case OPTIONS:
			keys.setVisible(!keys.isVisible());
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
	// Displayable

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

		data.setSize((int) (getHeight() / 3), getHeight() / 3);
		data.setLocation(getWidth() *4/ 10, game.getLocation().y + game.getHeight() + (int) (getHeight() * .05));

		felix.setBottomRightCorner(getWidth() - quit.getWidth() - 2 * margin, getHeight() - margin);

		lang.setBottomRightCorner(getWidth() - margin,
				getHeight() - options.getHeight() - quit.getHeight() - 3 * margin);
		options.setBottomRightCorner(getWidth() - margin, getHeight() - quit.getHeight() - 2 * margin);
		quit.setBottomRightCorner(getWidth() - margin, getHeight() - margin);

		keys.setSize(Math.min(500, getWidth() / 2), getHeight() * 3 / 4);
		keys.setLocation(getWidth() / 2 - keys.getWidth() / 2, getHeight() / 2 - keys.getHeight() / 2);
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
		game.stop();
		editor.stop();
	}

	// =========================================================================================================================

	public enum MainMenuAction {
		PLAY, SERVER, EDITOR, DATA_MANAGER, OPTIONS, QUIT;
	}
}
