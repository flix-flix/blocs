package data.id;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.io.StringWriter;
import java.util.Locale;

import environment.textures.TexturePack;
import graphicEngine.calcul.Camera;
import utils.panels.help.Tip;
import utils.yaml.YAML;
import window.Key;

public class ItemTableClient extends ItemTable {

	public static Cursor defaultCursor;

	static {
		defaultCursor = Cursor.getDefaultCursor();
	}

	// =========================================================================================================================

	private static YAML lang;
	private static String LANGUAGE;

	private static TexturePack texturePack;

	// =========================================================================================================================

	public static void init() {
		ItemTable.init();

		setLanguage(Locale.getDefault().getLanguage());
	}

	public static void setTexturePack(TexturePack texturePack) {
		ItemTableClient.texturePack = texturePack;

		for (int i : items.keySet())
			if (texturePack.yamls.get(i) != null)
				items.get(i).setTexture(texturePack.yamls.get(i));
	}

	public static TexturePack getTexturePack() {
		return texturePack;
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
		return modifyKey(lang.getString(tip.getPath() + ((Enum<?>) tip).name().toLowerCase()));
	}

	public static String getKey(Key key) {
		return lang.getString("keys." + key.name().toLowerCase());
	}

	public static String getText(String path) {
		return lang.getString("text." + path);
	}

	// =========================================================================================================================
	// Text (modification)

	public static String modifyKey(String str) {
		StringWriter sw = new StringWriter();
		int start = 0, index = 0, temp;

		while ((index = str.indexOf('~', start)) != -1) {
			sw.write(str.substring(start, index));

			int space = str.indexOf(' ', index);
			if ((temp = str.indexOf(',', index)) != -1 && temp < space)
				space = temp;
			if (space == -1)
				space = str.length();

			sw.write(KeyEvent.getKeyText(Key.valueOf(str.substring(index + 1, space)).code));

			start = space;
		}

		sw.write(str.substring(start));

		return sw.toString();
	}
}
