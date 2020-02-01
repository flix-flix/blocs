package data.id;

import editor.panels.MenuHelp.Tip;
import environment.textures.TexturePack;
import graphicEngine.calcul.Camera;
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

	public static String getName(int itemID) {
		return get(itemID).name;
	}

	public static String getTip(Tip tip) {
		return lang.getString(tip.getPath() + ((Enum<?>) tip).name().toLowerCase());
	}

	public static String getText(String path) {
		return lang.getString("text." + path);
	}
}
