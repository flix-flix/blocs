package data.id;

import client.window.graphicEngine.calcul.Camera;
import client.window.panels.editor.MenuHelp.Tip;
import utils.yaml.YAML;

public class ItemTableClient extends ItemTable {

	static YAML lang;

	// =========================================================================================================================

	public static void init() {
		ItemTable.init();

		lang = YAML.parseFile("resources/lang/fr/fr.yml");

		for (Item item : items.values())
			item.setLanguage(lang);
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

	public static String getName(int itemID) {
		return get(itemID).name;
	}

	public static String getTip(Tip tip) {
		return lang.getString(tip.getPath() + ((Enum<?>) tip).name().toLowerCase());
	}
}
