package game.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.TreeMap;

import javax.swing.JPanel;

import data.id.ItemTableClient;
import data.map.Cube;
import game.Game;
import game.StateHUD;
import game.UserAction;
import game.panels.menus.PanMap;
import game.panels.menus.PanRessources;
import game.panels.menus.infos.PanInfos;
import utils.Utils;
import utils.panels.ClickListener;
import utils.panels.FButton;
import utils.panels.PanCol;
import utils.panels.PanGrid;
import utilsBlocks.ButtonBlocks;

public class PanGame extends JPanel {
	private static final long serialVersionUID = -4495593129648278069L;

	private Game game;

	// =============== Environment ===============
	public PanGameEnv panEnv;

	// =============== Loading ===============
	private boolean loading = true;

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

	// =============== Menu ===============
	private int menuWidth = 400;

	private PanCol menu = new PanCol();

	private UserAction[] _userActions = { UserAction.MOUSE, UserAction.CREA_ADD, UserAction.CREA_DESTROY };
	private TreeMap<UserAction, ButtonBlocks> userActions = new TreeMap<>();

	private PanGrid gridActions;

	private PanMap map;
	private PanRessources ress;
	private PanInfos infos;

	// =========================================================================================================================

	public PanGame(Game game) {
		this.game = game;

		this.setLayout(null);

		this.add(panEnv = new PanGameEnv(game));
		panEnv.setLocation(0, 0);
		updateEnvBounds();

		// ========================================================================================

		menu.setSize(menuWidth, getHeight());
		menu.setLocation(0, 0);
		menu.setColor(Color.LIGHT_GRAY, null, 10, Color.GRAY);
		menu.setPadding(10);
		this.add(menu);

		menu.addTop(gridActions = new PanGrid(), 100);

		for (UserAction action : _userActions) {
			userActions.put(action, new ButtonUserAction(game, action));
			gridActions.gridAdd(userActions.get(action));
		}

		ButtonBlocks.group(userActions.values());

		menu.addBottom(map = new PanMap(game), PanCol.WIDTH);
		menu.addBottom(ress = new PanRessources(game), 130);
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

		quitButton.setText(ItemTableClient.getText("game.error.button_quit"));
		quitButton.setColor(Color.DARK_GRAY, Color.LIGHT_GRAY, 2, Color.LIGHT_GRAY);
		quitButton.setInColor(Color.GRAY, Color.DARK_GRAY, Color.DARK_GRAY);
		quitButton.setSize(300, 75);
		quitButton.setVisible(false);
		add(quitButton);

		// ========================================================================================

		refreshLang();

		setGUIVisible(false);
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		int centerW = getWidth() / 2;
		int centerH = getHeight() / 2;

		// Error screen
		if (game.getStateHUD() == StateHUD.ERROR) {
			g.setColor(new Color(236, 135, 15)); // Orange
			g.fillRect(0, 0, getWidth(), getHeight());

			g.setColor(Color.DARK_GRAY);
			g.setFont(fontError);

			int textW = fmError.stringWidth(game.errorMsg);

			g.drawString(game.errorMsg, centerW - textW / 2 + 10, centerH - 25);

			return;
		}

		// Loading screen
		else if (loading) {
			g.setColor(new Color(236, 135, 15)); // Orange
			g.fillRect(0, 0, getWidth(), getHeight());

			int textW = (int) (fontLoading.getStringBounds(loadingText, frc).getWidth());
			int textH = (int) (fontLoading.getStringBounds(loadingText, frc).getHeight());

			g.setColor(Color.DARK_GRAY);
			g.setFont(fontLoading);
			g.drawString(loadingText, centerW - textW / 2 + 10, centerH + textH / 2 - 20);
			g.setColor(Color.LIGHT_GRAY);
			g.setFont(fontLoadingError);
			g.drawString(loadingTextError, centerW - fmLoadingError.stringWidth(loadingTextError) / 2,
					centerH + textH / 2 + 20);
		}
	}

	// =========================================================================================================================

	public void start() {
		loading = false;
		setGUIVisible(true);

		map.updateMap();
	}

	public void stop() {
		infos.stop();
	}

	// =========================================================================================================================

	public void displayInfosOf(Cube cube) {
		infos.displayInfosOf(cube);
	}

	// =========================================================================================================================

	public void refreshLang() {
		loadingText = ItemTableClient.getText("game.loading");
		loadingTextError = ItemTableClient.getText("game.loading_error");
	}

	// =========================================================================================================================

	public void error() {
		setGUIVisible(false);
		panEnv.setVisible(false);
		quitButton.setVisible(true);
		Utils.debug("Error: " + game.errorMsg);
	}

	public void setGUIVisible(boolean visible) {
		// Can't be displayed if currently loading
		if (loading && visible)
			return;
		panEnv.help.setVisible(visible);
		menu.setVisible(visible);
	}

	// =========================================================================================================================

	public void updateMap() {
		map.updateMap();
	}

	public void setCubesVisible(boolean visible) {
		if (visible)
			infos.showCubes();
		else
			infos.hide();
	}

	// =========================================================================================================================

	public void updateEnvBounds() {
		panEnv.updateEnvironmentSize();
	}

	// =========================================================================================================================

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);

		panEnv.setSize(getWidth(), getHeight());

		menu.setSize(menuWidth, height);

		quitButton.setLocation(getWidth() / 2 - quitButton.getWidth() / 2, getHeight() / 2);
	}
}
