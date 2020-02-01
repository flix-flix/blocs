package game.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import data.id.ItemTableClient;
import data.map.Cube;
import environment.PanEnvironment;
import game.Game;
import game.StateHUD;
import game.UserAction;
import game.panels.menus.MenuButtonCube;
import game.panels.menus.MenuButtonUserAction;
import game.panels.menus.MenuMap;
import game.panels.menus.MenuRessources;
import game.panels.menus.infos.MenuInfos;
import server.game.GameMode;
import server.game.messages.Message;
import utils.panels.MenuCol;
import utils.panels.MenuGrid;

public class PanGame extends PanEnvironment {
	private static final long serialVersionUID = -4495593129648278069L;

	private Game game;

	public PanPause pause;

	// =============== Work In Progress ===============
	private String wip = "WORK IN PROGRESS";
	private Font fontWIP = new Font("monospace", Font.BOLD, 30);
	private FontMetrics fm = getFontMetrics(fontWIP);

	// =============== Dialog ===============
	private Font dialogFont = new Font("monospace", Font.BOLD, 18);

	/** Previous messages to display */
	public Message[] messages = new Message[10];
	/** Number of messages to display */
	public int nbMsg = 0;
	/** Width of the dialog background */
	private int msgBackWidth = 1000;

	/** Current line */
	public String msgLine = new String();

	/** Position of the text-cursor */
	public int cursorPos = 0;
	/** true : show the text-cursor (switch to make the cursor flashing) */
	private boolean cursorState = true;
	/** Store time since last cursor state switch */
	private int cursorStateTime = 0;

	// =============== Size ===============
	private int startXPanel;

	// =============== Font ===============
	private Font font = new Font("arial", Font.BOLD, 100);
	private AffineTransform affinetransform = new AffineTransform();
	private FontRenderContext frc = new FontRenderContext(affinetransform, true, true);

	private String loadingText;
	private String loadingTextError;

	// =============== Cross ===============
	/** Size of the central indicator (creative mode) */
	private int crossSize = 7;

	// =============== Menu ===============
	private int menuWidth = 400;

	private MenuCol menu = new MenuCol();

	private UserAction[] _userActions = { UserAction.MOUSE, UserAction.CREA_ADD, UserAction.CREA_DESTROY };
	private MenuButtonUserAction[] userActions = new MenuButtonUserAction[_userActions.length];

	private MenuGrid gridActions;

	private MenuMap map;
	private MenuRessources ress;
	private MenuInfos infos;

	// =========================================================================================================================

	public PanGame(Game game) {
		super(game);
		this.game = game;

		this.setLayout(null);

		pause = new PanPause(game);
		add(pause);

		// ========================================================================================

		menu.setBounds(0, 0, menuWidth, getHeight());
		this.add(menu);

		menu.addTop(gridActions = new MenuGrid(), 100);

		for (int i = 0; i < _userActions.length; i++)
			gridActions.addMenu(userActions[i] = new MenuButtonUserAction(game, _userActions[i]));

		menu.addBottom(map = new MenuMap(game), MenuCol.WIDTH);
		menu.addBottom(ress = new MenuRessources(game), 130);

		menu.addTop(infos = new MenuInfos(game), MenuCol.REMAINING);

		refreshLang();
	}

	// =========================================================================================================================

	@Override
	public void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();

		int centerW = width / 2;
		int centerH = height / 2;

		// Draw Environment
		super.paintComponent(g.create(startXPanel, 0, width - startXPanel, height));

		// Loading screen
		if (img == null) {
			g.setColor(new Color(236, 135, 15)); // Orange
			g.fillRect(0, 0, width, height);

			int textW = (int) (font.getStringBounds(loadingText, frc).getWidth());
			int textH = (int) (font.getStringBounds(loadingText, frc).getHeight());

			g.setColor(Color.LIGHT_GRAY);
			g.setFont(font);
			g.drawString(loadingText, centerW - textW / 2 + 10, centerH + textH / 2 - 20);
			g.setFont(new Font("arial", Font.BOLD, 10));
			g.drawString(loadingTextError, centerW - 200 / 2 + 10, centerH + textH / 2 + 20);

		}

		// =========================================================================================================================

		else {
			if (game.gamemode == GameMode.CREATIVE) {
				// Middle indicator : cross
				g.setColor(Color.WHITE);
				g.drawLine(centerW - crossSize, centerH - 1, centerW + crossSize - 1, centerH - 1);
				g.drawLine(centerW - crossSize, centerH, centerW + crossSize - 1, centerH);
				g.drawLine(centerW - 1, centerH - crossSize, centerW - 1, centerH + crossSize - 1);
				g.drawLine(centerW, centerH - crossSize, centerW, centerH + crossSize - 1);
			}

			// =============== Dialog Display ===============
			if (game.stateHUD == StateHUD.DIALOG) {
				int grayBack = 120;
				Color colorBack = new Color(grayBack, grayBack, grayBack, 200);
				Color colorMsg = Color.WHITE;

				int startW = 420;

				// TODO [Improve] If messages text too long split in several lines

				// =============== Message (Line) ===============

				// Background
				g.setColor(colorBack);
				g.fillRect(startW, height - 100, msgBackWidth, 30);

				// Text
				g.setFont(dialogFont);
				g.setColor(colorMsg);

				// Flash the text-cursor
				cursorStateTime++;
				if (cursorStateTime > 3) {
					cursorState = !cursorState;
					cursorStateTime = 0;
				}

				if (cursorState)
					g.drawString(msgLine.substring(0, cursorPos) + "|" + msgLine.substring(cursorPos), startW + 5,
							height - 100 + 20);
				else
					g.drawString(msgLine.substring(0, cursorPos) + " " + msgLine.substring(cursorPos), startW + 5,
							height - 100 + 20);

				// =============== Messages (Previous) ===============

				// Background
				g.setColor(colorBack);
				g.fillRect(startW, height - 100 - 10 - 30 * nbMsg, msgBackWidth, 30 * nbMsg);

				// Text
				g.setColor(colorMsg);
				for (int i = 0; i < nbMsg; i++)
					g.drawString(messages[i].toMessage(), startW + 5, height - 100 + 20 - 10 - (nbMsg - i) * 30);
			}
		}

		// Work In Progress
		g.setColor(Color.WHITE);
		g.setFont(fontWIP);
		g.drawString(wip, getWidth() - fm.stringWidth(wip) - 10, getHeight() - 10);
	}

	// =========================================================================================================================

	public void refreshSelected(Cube cube) {
		infos.refresh(cube);
	}

	public void refreshGUI() {
		menu.setVisible(game.gamemode == GameMode.CLASSIC);

		game.clearSelected();

		for (MenuButtonUserAction e : userActions)
			e.selected = game.getAction() == e.action;

		if (game.getAction() == UserAction.CREA_ADD)
			infos.showCubes();

		map.updateMap();
		map.repaint();
	}

	public void updateTexturePack() {
		for (MenuButtonCube m : infos.cubes)
			m.updateTexturePack(game.texturePack);
	}

	// =========================================================================================================================

	public void refreshLang() {
		loadingText = ItemTableClient.getText("game.loading");
		loadingTextError = ItemTableClient.getText("game.loadingError");
	}

	// =========================================================================================================================

	public void setStartXPanel(int x) {
		startXPanel = x;
		updateEnvironmentSize(getWidth(), getHeight());
	}

	// =========================================================================================================================

	@Override
	public void updateEnvironmentSize(int width, int height) {
		envWidth = width - startXPanel;
		envHeight = height;

		envCenterW = width / 2;
		envCenterH = height / 2;
	}

	// =========================================================================================================================

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);

		pause.setSize(width, height);
		menu.setSize(menuWidth, height);
	}
}
