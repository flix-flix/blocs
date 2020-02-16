package mainMenu;

import java.awt.Color;
import java.awt.Cursor;

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
import mainMenu.server.PanServer;
import server.Server;
import server.ServerDescription;
import utils.panels.ButtonPad;
import utils.panels.FButton;
import utils.panels.FPanel;
import window.Displayable;
import window.Fen;
import window.KeyBoard;

public class MainMenu extends JPanel implements Displayable {
	private static final long serialVersionUID = -3189420915172593199L;

	private Fen fen;

	// =============== Main Menu ===============
	private FPanel menu;

	private ButtonEnv game, server, editor;

	private ButtonAction data;

	private FButton wip;
	private PanFelix felix;
	private ButtonLang lang;
	private ButtonAction options, quit;

	// =============== Server ===============
	private PanServer panServer;

	// =============== Keys ===============
	PanKeys keys;

	private KeyBoardMainMenu keyboard = new KeyBoardMainMenu(this);

	// =============== Size ===============
	/** Space between the lang/options/quit/Felix buttons */
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
		this.add(keys);

		// ========================================================================================
		// Main Menu

		menu = new FPanel();
		menu.setBackground(Color.LIGHT_GRAY);
		menu.setLocation(0, 0);
		menu.setSize(getWidth(), getHeight());
		this.add(menu);

		menu.add(game = ButtonEnv.generateButton(this, MainMenuAction.PLAY));
		game.setSize(750, 500);
		game.setLocation(50, 50);

		menu.add(server = ButtonEnv.generateButton(this, MainMenuAction.SERVER));
		server.setSize(750, 500);
		server.setLocation(50, 50);

		menu.add(editor = ButtonEnv.generateButton(this, MainMenuAction.EDITOR));
		editor.setSize(300, 300);
		editor.setLocation(50, 600);

		menu.add(data = new ButtonAction(this, MainMenuAction.DATA_MANAGER));
		data.setSize(300, 300);
		data.setLocation(380, 600);

		menu.add(felix = new PanFelix());

		menu.add(wip = new ButtonPad("WORK IN PROGRESS", null, Color.LIGHT_GRAY, PanServer.RED, 3));

		lang = new ButtonLang(this);
		lang.setSize(100, 80);
		lang.setBorder(5, Color.DARK_GRAY);
		menu.add(lang);

		options = new ButtonAction(this, MainMenuAction.OPTIONS);
		options.setSize(100, 100);
		options.setBorder(5, Color.DARK_GRAY);
		menu.add(options);

		quit = new ButtonAction(this, MainMenuAction.QUIT);
		quit.setSize(100, 100);
		quit.setBorder(5, Color.DARK_GRAY);
		menu.add(quit);

		// ========================================================================================
		// Server

		panServer = new PanServer(this);
		panServer.setLocation(0, 0);
		panServer.setSize(getWidth(), getHeight());
		panServer.setVisible(true);

		this.add(panServer);

		refreshLang();
	}

	// =========================================================================================================================

	public void click(MainMenuAction action) {
		switch (action) {
		case PLAY:
			fen.setDisplay(Game.startLocalServer(fen));
			break;

		case SERVER:
			menu.setVisible(false);
			panServer.setVisible(true);
			break;

		case SERVER_QUIT:
			menu.setVisible(true);
			panServer.setVisible(false);
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

	public void clickServer(MainMenuAction action, ServerDescription descritpion) {
		switch (action) {
		case SERVER_START:
			Server server = new Server(descritpion.port, descritpion.name);
			server.start();
			panServer.startServer(server);
			break;

		case SERVER_ADD:
			panServer.addServer(descritpion);
			break;

		case SERVER_JOIN:
			fen.setDisplay(new Game(fen, descritpion));
			break;
		case SERVER_STOP:
			panServer.removeHosted(descritpion);
			descritpion.server.stop();
			break;
		case SERVER_DELETE:
			panServer.removeKnown(descritpion);
			break;

		default:
			break;
		}
	}

	// =========================================================================================================================

	public void refreshLang() {
		game.refreshLang();
		server.refreshLang();
		editor.refreshLang();
		data.setText(ItemTableClient.getText("main_menu.buttons.data_manager"));

		panServer.refreshLang();

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
		super.setSize(x, y);

		menu.setSize(getWidth(), getHeight());
		panServer.setSize(getWidth(), getHeight());

		game.setSize((int) (getWidth() * .6), getHeight() / 2);
		game.setLocation((int) (getWidth() * .05), (int) (getHeight() * .07));

		server.setSize((int) (getWidth() * .25), getHeight() / 2);
		server.setLocation((int) (getWidth() * .7), (int) (getHeight() * .07));

		editor.setSize((int) (getHeight() / 3), getHeight() / 3);
		editor.setLocation(getWidth() / 10, game.getLocation().y + game.getHeight() + (int) (getHeight() * .05));

		data.setSize((int) (getHeight() / 3), getHeight() / 3);
		data.setLocation(getWidth() * 4 / 10, game.getLocation().y + game.getHeight() + (int) (getHeight() * .05));

		wip.setBottomRightCorner(getWidth() - quit.getWidth() - felix.getWidth() - 2 * margin, getHeight() - margin-5);
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
		game.setPaused(true);
		server.setPaused(true);
		editor.setPaused(true);
	}

	@Override
	public void resume() {
		game.setPaused(false);
		server.setPaused(false);
		editor.setPaused(false);
	}
}
