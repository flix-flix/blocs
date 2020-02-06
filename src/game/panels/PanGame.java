package game.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.TreeMap;

import data.id.ItemID;
import data.id.ItemTableClient;
import data.map.Cube;
import environment.PanEnvironment;
import environment.extendsData.CubeClient;
import game.CameraMode;
import game.Game;
import game.StateHUD;
import game.UserAction;
import game.panels.menus.PanMap;
import game.panels.menus.MenuRessources;
import game.panels.menus.infos.PanInfos;
import game.tips.TipGame;
import server.game.messages.Message;
import utils.FlixBlocksUtils;
import utils.panels.ClickListener;
import utils.panels.FButton;
import utils.panels.PanCol;
import utils.panels.PanGrid;
import utils.panels.help.PanHelp;
import utils.panels.help.PanHelp.Mark;
import utilsBlocks.ButtonBlocks;

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

	// =============== Loading ===============
	private Font fontLoading = new Font("arial", Font.BOLD, 100);
	private AffineTransform affinetransform = new AffineTransform();
	private FontRenderContext frc = new FontRenderContext(affinetransform, true, true);

	private Font fontLoadingError = new Font("arial", Font.BOLD, 10);
	private FontMetrics fmLoadingError = getFontMetrics(fontLoadingError);

	private String loadingText;
	private String loadingTextError;

	// =============== Error ===============
	private Font fontError = new Font("arial", Font.BOLD, 30);
	private FontMetrics fmError = getFontMetrics(fontError);

	private FButton quitButton;

	// =============== Cross ===============
	/** Size of the central indicator (creative mode) */
	private int crossSize = 7;

	// =============== Menu ===============
	private int menuWidth = 400;

	public PanCol menu = new PanCol();

	private UserAction[] _userActions = { UserAction.MOUSE, UserAction.CREA_ADD, UserAction.CREA_DESTROY };
	// private MenuButtonUserAction[] userActions = new
	// MenuButtonUserAction[_userActions.length];
	private TreeMap<UserAction, ButtonBlocks> userActions = new TreeMap<>();

	private PanGrid gridActions;

	private PanMap map;
	private MenuRessources ress;
	private PanInfos infos;

	public PanHelp help;

	// =========================================================================================================================

	public PanGame(Game game) {
		super(game);
		this.game = game;

		pause = new PanPause(game);
		this.add(pause);

		// ========================================================================================

		help = new PanHelp(Mark.INTERROGATION, 700, 80, 10, TipGame.values()[0]);
		help.setBackground(new Color(0xff4068c4));
		help.setLocation(menuWidth + 25, getHeight() - 25);
		this.add(help);

		// ========================================================================================

		menu.setBounds(0, 0, menuWidth, getHeight());
		this.add(menu);

		menu.addTop(gridActions = new PanGrid(), 100);

		for (UserAction action : _userActions) {
			userActions.put(action, createButton(action));
			gridActions.addMenu(userActions.get(action));
		}

		ButtonBlocks.group(userActions.values());

		menu.addBottom(map = new PanMap(game), PanCol.WIDTH);
		menu.addBottom(ress = new MenuRessources(game), 130);
		ress.setVisible(true);

		// ========================================================================================

		menu.addTop(infos = new PanInfos(game), PanCol.REMAINING);

		// ========================================================================================

		quitButton = new FButton();
		quitButton.setClickListener(new ClickListener() {
			@Override
			public void leftClick() {
				game.fen.returnToMainMenu();
			}
		});
		quitButton.setText(ItemTableClient.getText("game.error.buttonQuit"));
		quitButton.setBorder(2, Color.BLACK);
		quitButton.setSize(300, 75);
		quitButton.setVisible(false);
		add(quitButton);

		// ========================================================================================

		refreshLang();

		setGUIVisible(false);
	}

	// =========================================================================================================================

	@Override
	public void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();

		int centerW = width / 2;
		int centerH = height / 2;

		if (game.stateHUD == StateHUD.ERROR) {
			g.setColor(new Color(236, 135, 15)); // Orange
			g.fillRect(0, 0, width, height);

			g.setColor(Color.LIGHT_GRAY);
			g.setFont(fontError);

			int textW = fmError.stringWidth(game.errorMsg);
			int textH = (int) fm.getStringBounds(game.errorMsg, g).getHeight();

			g.drawString(game.errorMsg, centerW - textW / 2 + 10, centerH + textH / 2 - 20);

			return;
		}

		// Draw Environment
		super.paintComponent(g.create(startXPanel, 0, width - startXPanel, height));

		// Loading screen
		if (img == null) {
			g.setColor(new Color(236, 135, 15)); // Orange
			g.fillRect(0, 0, width, height);

			int textW = (int) (fontLoading.getStringBounds(loadingText, frc).getWidth());
			int textH = (int) (fontLoading.getStringBounds(loadingText, frc).getHeight());

			g.setColor(Color.LIGHT_GRAY);
			g.setFont(fontLoading);
			g.drawString(loadingText, centerW + menuWidth / 2 - textW / 2 + 10, centerH + textH / 2 - 20);
			g.setFont(fontLoadingError);
			g.drawString(loadingTextError, centerW + menuWidth / 2 - fmLoadingError.stringWidth(loadingTextError) / 2,
					centerH + textH / 2 + 20);

		}

		// =========================================================================================================================

		else {
			if (game.cameraMode == CameraMode.FIRST_PERSON) {
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

	public ButtonBlocks createButton(UserAction action) {
		ButtonBlocks button = new ButtonBlocks();

		button.setSelectable(true);

		if (action == UserAction.CREA_ADD)
			button.setModel(new CubeClient(new Cube(ItemID.GRASS)));
		else
			button.setImage(FlixBlocksUtils
					.getImage(ItemTableClient.getTexturePack().getFolder() + "menu/" + action.name().toLowerCase()));

		button.setClickListener(new ClickListener() {

			@Override
			public void leftClick() {
				game.setAction(action);
			}
		});

		return button;
	}

	// =========================================================================================================================

	public void refreshSelected(Cube cube) {
		infos.refresh(cube);
	}

	public void refreshGUI() {
		menu.setVisible(game.cameraMode == CameraMode.CLASSIC);

		game.clearSelected();

		userActions.get(UserAction.MOUSE).unselectAll();

		userActions.get(game.getAction()).setSelected(true);

		if (game.getAction() == UserAction.CREA_ADD)
			infos.showCubes();

		map.updateMap();
		map.repaint();
	}

	// =========================================================================================================================

	public void refreshLang() {
		loadingText = ItemTableClient.getText("game.loading");
		loadingTextError = ItemTableClient.getText("game.loadingError");
	}

	// =========================================================================================================================

	public void error() {
		setGUIVisible(false);
		System.out.println(quitButton.getBounds());
		quitButton.setVisible(true);

		repaint();
	}

	public void setGUIVisible(boolean visible) {
		startXPanel = visible ? menuWidth : 0;
		updateEnvironmentSize(getWidth(), getHeight());
		help.setVisible(visible);
		menu.setVisible(visible);
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
		help.setLocation(menuWidth + 25, getHeight() - help.getHeight() - 35);

		quitButton.setLocation(getWidth() / 2 - quitButton.getWidth() / 2, getHeight() / 2 + 75);
	}
}
