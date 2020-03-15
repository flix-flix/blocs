package mainMenu;

import java.awt.Color;
import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.util.ArrayList;

import javax.swing.JPanel;

import data.id.ItemTableClient;
import dataManager.DataManager;
import editor.EditorManager;
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
import utils.panels.popUp.PopUpInfos;
import utils.panels.popUp.PopUpText;
import utils.yaml.YAML;
import utilsBlocks.UtilsBlocks;
import window.Displayable;
import window.Fen;
import window.KeyBoard;

public class MainMenu extends JPanel implements Displayable {
	private static final long serialVersionUID = -3189420915172593199L;

	private static final String knownFile = "settings/server/known.yml";

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
	PanServer panServer;

	private YAML servers;

	// =============== Keys ===============
	PanKeys keys;

	private KeyBoardMainMenu keyboard = new KeyBoardMainMenu(this);

	// =============== Pop-Up ===============
	PopUpInfos popUpError;
	PopUpText popUpText;

	// =============== Size ===============
	/** Space between the lang/options/quit/Felix buttons */
	private int margin = 10;

	// =========================================================================================================================

	public MainMenu(Fen fen) {
		this.fen = fen;
		this.setLayout(null);

		// ========================================================================================
		// Pop-Up

		popUpError = new PopUpInfos();
		popUpError.setVoile(new Color(255, 0, 0, 90));
		popUpError.setColor(Color.LIGHT_GRAY, null, 5, UtilsBlocks.RED);
		popUpError.setLocation(0, 0);
		this.add(popUpError);

		popUpText = new PopUpInfos();
		popUpText.setVoile(new Color(150, 150, 150, 150));
		popUpText.setColor(Color.LIGHT_GRAY, null, 5, Color.DARK_GRAY);
		popUpText.setLocation(0, 0);
		popUpText.setYamlKey("main_menu.pop_up.currently_connecting");
		this.add(popUpText);

		// ========================================================================================
		// Main Menu

		menu = new FPanel();
		menu.setBackground(Color.LIGHT_GRAY);
		menu.setLocation(0, 0);
		menu.setSize(getWidth(), getHeight());
		this.add(menu);

		// ========================================================================================
		// Keys

		keys = new PanKeys();
		keys.setSize(getWidth(), getHeight());
		keys.setLocation(0, 0);
		menu.add(keys);

		// ========================================================================================
		// Big buttons

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

		// ========================================================================================
		// Small buttons

		menu.add(felix = new PanFelix());

		menu.add(wip = new ButtonPad("WORK IN PROGRESS", null, Color.LIGHT_GRAY, 3, UtilsBlocks.RED));

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
		panServer.setVisible(false);

		this.add(panServer);

		// Get memorized ones
		if (new File(knownFile).exists()) {
			servers = YAML.parseFile(knownFile);

			if (servers.contains("known"))
				for (YAML server : servers.getList("known"))
					panServer.addServer(new ServerDescription(server.getString("ip"), server.getInt("port"),
							server.getString("name")));
		} else
			servers = new YAML();

		refreshLang();
	}

	// =========================================================================================================================

	public void click(MainMenuAction action) {
		switch (action) {
		case PLAY:
			boolean everythingAlright = true;
			Game game = null;

			try {
				game = Game.startLocalServer(fen);
			} catch (IOException e) {
				if (e instanceof BindException)
					popUpError.setYamlKey("main_menu.error.default_port_already_used");
				else {
					e.printStackTrace();
					popUpError.setYamlKey("main_menu.error.cant_launch_game");
				}
				everythingAlright = false;
				popUpError.setVisible(true);
			}

			if (everythingAlright)
				fen.setDisplay(game);
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
			fen.setDisplay(new EditorManager(fen));
			break;
		case DATA_MANAGER:
			fen.setDisplay(new DataManager(fen));
			break;
		case OPTIONS:
			keys.setVisible(true);
			break;
		case QUIT:
			System.exit(0);
			break;

		default:
			break;
		}
	}

	public void clickServer(MainMenuAction action, ServerDescription description) {
		boolean everythingAlright = true;

		switch (action) {
		case SERVER_JOIN:
			Game game = null;

			// TODO [Fix] Join inexistant server freeze (+ show Connecting... pop-up)
			popUpText.setVisible(true);
			repaint();

			try {
				game = new Game(fen, description);
			} catch (IOException e) {
				if (e instanceof ConnectException)
					popUpError.setYamlKey("main_menu.error.server_not_running");
				else {
					e.printStackTrace();
					popUpError.setYamlKey("main_menu.error.cant_join_server");
				}
				popUpText.close();

				everythingAlright = false;
				popUpError.setVisible(true);
			}

			if (everythingAlright) {
				popUpText.close();
				fen.setDisplay(game);
			}
			break;

		case SERVER_START:
			Server server = null;

			try {
				server = new Server(description.port, description.name);
			} catch (IOException e) {
				if (e instanceof BindException)
					popUpError.setYamlKey("main_menu.error.port_already_used");
				else {
					e.printStackTrace();
					popUpError.setYamlKey("main_menu.error.cant_start_server");
				}
				everythingAlright = false;
				popUpError.setVisible(true);
			}

			if (everythingAlright) {
				server.start();
				panServer.startServer(server);
			}
			break;
		case SERVER_STOP:
			panServer.removeHosted(description);
			description.server.stop();
			break;

		case SERVER_ADD:
			panServer.addServer(description);

			// Write on disk
			if (!servers.contains("known"))
				servers.put("known", new ArrayList<YAML>());

			YAML yaml = new YAML();
			yaml.put("ip", description.ip);
			yaml.put("port", "" + description.port);
			yaml.put("name", description.name);

			servers.getList("known").add(yaml);

			YAML.encodeFile(servers, knownFile);
			break;
		case SERVER_DELETE:
			panServer.removeKnown(description);

			// Remove from disk
			ArrayList<YAML> list = servers.getList("known");
			for (int i = 0; i < list.size(); i++) {
				YAML y = list.get(i);
				if (y.getString("ip").equals(description.ip) && y.getInt("port") == description.port
						&& y.getString("name").equals(description.name)) {
					list.remove(i);
					break;
				}
			}
			YAML.encodeFile(servers, knownFile);
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

		// =============== Panels ===============
		menu.setSize(getWidth(), getHeight());
		panServer.setSize(getWidth(), getHeight());
		keys.setSize(getWidth(), getHeight());
		popUpError.setSize(getWidth(), getHeight());
		popUpText.setSize(getWidth(), getHeight());

		// =============== Buttons ===============
		lang.setBottomRightCorner(getWidth() - margin,
				getHeight() - options.getHeight() - quit.getHeight() - 3 * margin);
		options.setBottomRightCorner(getWidth() - margin, getHeight() - quit.getHeight() - 2 * margin);
		quit.setBottomRightCorner(getWidth() - margin, getHeight() - margin);

		int borderX = quit.getWidth() + 2 * margin;

		// =============== Bottom ===============
		felix.setBottomRightCorner(getWidth() - quit.getWidth() - 2 * margin, getHeight() - margin);
		wip.setBottomRightCorner(getWidth() - quit.getWidth() - felix.getWidth() - 2 * margin,
				getHeight() - margin - 5);

		int borderY = felix.getHeight() + 2 * margin;

		// =============== Display (Top) ===============
		int marginTopX = (int) (getWidth() * .05);
		int marginTopY = (int) (getHeight() * .05);

		int topH = (int) (getHeight() * .6);
		// Avoid the top panels to hide the right-side buttons
		int topW = getWidth() - (topH > lang.getLocation().y - margin ? borderX : marginTopX);

		// Remove the margin before
		topW -= marginTopX;
		topH -= marginTopY;

		// Size without ratio
		int _gameW = (int) (topW * .65), _gameH = topH;
		int _serverW = (int) (topW * .30), _serverH = topH;

		// Size with ratio
		int gameW = _gameW, gameH = _gameH;
		int serverW = _serverW;

		if (gameW >= 3 * gameH)
			gameW = 3 * gameH;
		else if (gameW < gameH * 2 / 3)
			gameH = gameW * 3 / 2;

		if (serverW > gameH)
			serverW = gameH;

		game.setSize(gameW, gameH);
		game.setCenter(marginTopX + _gameW / 2, marginTopY + _gameH / 2);

		server.setSize(serverW, gameH);
		server.setCenter(marginTopX + _gameW + (int) (topW * .05) + _serverW / 2, marginTopY + _serverH / 2);

		// =============== Display (Bottom) ===============
		int bottomStartY = topH + 2 * marginTopY;

		int sizeW = (getWidth() - borderX - 3 * marginTopX) / 2;
		int sizeH = getHeight() - bottomStartY - borderY;
		int size = Math.min(sizeW, sizeH);

		editor.setSize(size, size);
		editor.setCenter(marginTopX + sizeW / 2, bottomStartY + sizeH / 2);

		data.setSize(size, size);
		data.setCenter(marginTopX * 2 + sizeW + sizeW / 2, bottomStartY + sizeH / 2);
	}

	@Override
	public Cursor getCursor() {
		return ItemTableClient.defaultCursor;
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
