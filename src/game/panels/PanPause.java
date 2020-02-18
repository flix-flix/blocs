package game.panels;

import java.awt.Color;

import data.id.ItemTableClient;
import game.Game;
import utils.panels.ClickListener;
import utils.panels.FButton;
import utils.panels.PopUp;

public class PanPause extends PopUp {
	private static final long serialVersionUID = 2735034739187347959L;

	private FButton resume, options, saveQuit;

	// =========================================================================================================================

	public PanPause(Game game) {
		this.setVisible(false);
		setRect(500, 700);
		setBackground(Color.GRAY);
		setBorder(10, Color.LIGHT_GRAY);
		setVoile(new Color(90, 90, 90, 150));

		this.add(resume = new FButton());
		resume.setSize(200, 50);
		resume.setColor(Color.DARK_GRAY, Color.LIGHT_GRAY, 2, Color.LIGHT_GRAY);
		resume.setInColor(Color.LIGHT_GRAY, Color.DARK_GRAY, Color.DARK_GRAY);

		this.add(options = new FButton());
		options.setSize(200, 50);
		options.setColor(Color.GRAY, Color.LIGHT_GRAY, 2, Color.LIGHT_GRAY);

		this.add(saveQuit = new FButton());
		saveQuit.setSize(200, 50);
		saveQuit.setColor(Color.DARK_GRAY, Color.LIGHT_GRAY, 2, Color.LIGHT_GRAY);
		saveQuit.setInColor(Color.LIGHT_GRAY, Color.DARK_GRAY, Color.DARK_GRAY);

		refreshLang();

		resume.setClickListener(new ClickListener() {
			@Override
			public void leftClick() {
				game.resume();
			}
		});
		options.setClickListener(new ClickListener() {
			@Override
			public void leftClick() {
			}
		});
		saveQuit.setClickListener(new ClickListener() {
			@Override
			public void leftClick() {
				game.exit();
			}
		});
	}

	// =========================================================================================================================

	public void refreshLang() {
		resume.setText(ItemTableClient.getText("game.pause.buttons.resume"));
		options.setText(ItemTableClient.getText("game.pause.buttons.options"));
		saveQuit.setText(ItemTableClient.getText("game.pause.buttons.save_quit"));
	}

	// =========================================================================================================================

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);

		// TODO [Improve] Pause buttons centered
		int start = (height - 3 * 50 - 2 * 100) / 2;

		resume.setLocation(getWidth() / 2 - 100, start);
		options.setLocation(getWidth() / 2 - 100, start + 150);
		saveQuit.setLocation(getWidth() / 2 - 100, start + 300);
	}
}