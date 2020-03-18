package game.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import data.id.ItemTableClient;
import environment.PanEnvironment;
import game.CameraMode;
import game.Game;
import game.StateHUD;
import utils.panels.PanHelp;
import utils.panels.PanHelp.Mark;
import utilsBlocks.Tip;

public class PanGameEnv extends PanEnvironment {
	private static final long serialVersionUID = 5191296217478885760L;

	private Game game;

	// =============== Size ===============
	private int menuWidth = 400;
	private int startXEnv;

	// =============== Work In Progress ===============
	private String wip = "WORK IN PROGRESS";
	private Font fontWIP = new Font("monospace", Font.BOLD, 30);
	private FontMetrics fm = getFontMetrics(fontWIP);

	// =============== Cross ===============
	/** Size of the central indicator (creative mode) */
	private int crossSize = 7;

	// =============== Panels ===============
	public PanPause pause;
	public PanHelp help;

	// =========================================================================================================================

	public PanGameEnv(Game game) {
		super(game);
		// Like this.env but already casted
		this.game = game;

		this.setOpaque(false);

		this.add(pause = new PanPause(game));

		help = new PanHelp(ItemTableClient.getTips(Tip.GAME_GLOBAL), Mark.INTERROGATION, 700, 80, 10);
		help.setBackground(new Color(0xff4068c4));
		help.setBorderColor(Color.LIGHT_GRAY);
		help.setForeground(Color.DARK_GRAY);
		help.setLocation(25, getHeight() - 25);
		help.setVisible(false);
		this.add(help);
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g.create(startXEnv, 0, getWidth() - startXEnv, getHeight()));

		if (img != null) {
			if (game.cameraMode == CameraMode.FIRST_PERSON) {
				// Middle indicator : cross
				g.setColor(Color.WHITE);

				int centerW = getWidth() / 2;
				int centerH = getHeight() / 2;

				g.drawLine(centerW - crossSize, centerH - 1, centerW + crossSize - 1, centerH - 1);
				g.drawLine(centerW - crossSize, centerH, centerW + crossSize - 1, centerH);
				g.drawLine(centerW - 1, centerH - crossSize, centerW - 1, centerH + crossSize - 1);
				g.drawLine(centerW, centerH - crossSize, centerW, centerH + crossSize - 1);
			}

			// Dialog Display
			game.messages.draw(g);
		}

		// =========================================================================================================================

		// Work In Progress
		g.setColor(Color.WHITE);
		g.setFont(fontWIP);
		g.drawString(wip, getWidth() - fm.stringWidth(wip) - 10, getHeight() - 10);
	}

	// =========================================================================================================================

	public void repaintEnv() {
		if (game.getStateHUD() == StateHUD.PAUSE)
			repaint();
		else
			repaint(startXEnv, 0, getWidth() - startXEnv, getHeight());
	}

	// =========================================================================================================================

	protected void updateEnvironmentSize() {
		updateEnvironmentSize(getWidth(), getHeight());
	}

	@Override
	protected void updateEnvironmentSize(int width, int height) {
		startXEnv = game.cameraMode == CameraMode.CLASSIC ? menuWidth : 0;

		help.setBottomLeftCorner(startXEnv + 25, getHeight() - 35);

		super.updateEnvironmentSize(width - startXEnv, height);
	}

	// =========================================================================================================================

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);

		pause.setSize(width, height);
	}
}
