package mainMenu.buttons;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.util.Locale;

import data.id.ItemTableClient;
import mainMenu.MainMenu;
import utils.FlixBlocksUtils;
import utils.panels.Menu;

public class ButtonLang extends Menu {
	private static final long serialVersionUID = 7788433961297824605L;

	private MainMenu main;
	private String lang;
	private boolean french = true;

	private int border = 10;
	private static Image fr, en;

	static {
		fr = FlixBlocksUtils.getImage("static/flags/fr");
		en = FlixBlocksUtils.getImage("static/flags/en");
	}

	// =========================================================================================================================

	public ButtonLang(MainMenu main) {
		this.main = main;
		lang = getLocale().getLanguage();
	}

	// =========================================================================================================================

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

		Image img = french ? fr : en;

		int width = getWidth() - 2 * border - 1;
		int height = (int) (width * ((double) img.getHeight(null) / img.getWidth(null)));
		int decal = (getHeight() - 1 - 2 * border - height) / 2;

		g.drawImage(img, border, border + decal, width, height, null);
	}

	// =========================================================================================================================
	// Menu

	@Override
	public void click(MouseEvent e) {
		french = !french;
		lang = french ? Locale.FRENCH.getLanguage() : Locale.ENGLISH.getLanguage();

		ItemTableClient.setLanguage(lang);

		main.refreshLang();
	}

	@Override
	public void resize() {
	}
}
