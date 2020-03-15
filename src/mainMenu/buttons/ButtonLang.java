package mainMenu.buttons;

import java.awt.Image;
import java.util.Locale;

import data.id.ItemTableClient;
import mainMenu.MainMenu;
import utils.Utils;
import utils.panels.FButton;

public class ButtonLang extends FButton {
	private static final long serialVersionUID = 7788433961297824605L;

	private MainMenu main;
	private String lang;
	private boolean french = true;

	private static Image fr = Utils.getResourceImage("/flags/fr.png");
	private static Image en = Utils.getResourceImage("/flags/en.png");

	// =========================================================================================================================

	public ButtonLang(MainMenu main) {
		this.main = main;
		lang = ItemTableClient.getLanguage();
		french = lang.equals(Locale.FRENCH.getLanguage()) || lang.equals(Locale.CANADA_FRENCH.getLanguage());

		setPadding(10);
		setImage(french ? fr : en, FButton.KEEP_RATIO);
	}

	// =========================================================================================================================

	@Override
	public void eventClick() {
		french = !french;
		lang = french ? Locale.FRENCH.getLanguage() : Locale.ENGLISH.getLanguage();

		setImage(french ? fr : en, FButton.KEEP_RATIO);

		ItemTableClient.setLanguage(lang);

		main.refreshLang();
	}
}
