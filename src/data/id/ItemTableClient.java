package data.id;

import java.util.Locale;

import environment.textures.TexturePack;
import graphicEngine.calcul.Camera;
import utils.panels.help.Tip;
import utils.yaml.YAML;
import window.Key;

public class ItemTableClient extends ItemTable {

	static YAML lang;
	static String LANGUAGE;

	// =========================================================================================================================

	public static void init() {
		ItemTable.init();

		setLanguage(Locale.getDefault().getLanguage());
	}

	public static void setTexturePack(TexturePack texturePack) {
		for (int i : items.keySet())
			if (texturePack.yamls.get(i) != null)
				items.get(i).setTexture(texturePack.yamls.get(i));
	}

	// =========================================================================================================================
	// Graphic Engine

	public static boolean isOpaque(int itemID) {
		return get(itemID).opaque;
	}

	public static boolean drawContour(int itemID) {
		return get(itemID).contour;
	}

	// =========================================================================================================================
	// Textures

	public static Camera getCamera(int itemID) {
		return get(itemID).camera;
	}

	public static int getMapColor(int itemID) {
		if (itemID == -1)
			return 0;
		return get(itemID).mapColor;
	}

	// =========================================================================================================================
	// Language

	public static void setLanguage(String language) {
		LANGUAGE = language;

		if (language.equals(Locale.FRENCH.getLanguage()) || language.equals(Locale.CANADA_FRENCH.getLanguage())) {
			lang = YAML.parseFile("resources/lang/fr/fr.yml");
			Locale.setDefault(Locale.FRENCH);
		} else {
			lang = YAML.parseFile("resources/lang/en/en.yml");
			Locale.setDefault(Locale.ENGLISH);
		}

		for (Item item : items.values())
			item.setLanguage(lang);
	}

	public static String getLanguage() {
		return LANGUAGE;
	}

	// =========================================================================================================================
	// Text

	public static String getName(int itemID) {
		return get(itemID).name;
	}

	public static String getTip(Tip tip) {
		return lang.getString(tip.getPath() + ((Enum<?>) tip).name().toLowerCase());
	}

	public static String getKey(Key key) {
		return lang.getString("keys." + key.name().toLowerCase());
	}

	public static String getText(String path) {
		return lang.getString("text." + path);
	}
}
