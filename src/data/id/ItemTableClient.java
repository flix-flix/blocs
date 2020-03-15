package data.id;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.Locale;

import data.Gamer;
import data.map.Chunk;
import data.map.Cube;
import data.map.resources.ResourceType;
import environment.textures.TexturePack;
import graphicEngine.calcul.Camera;
import utils.TextPlus;
import utils.TextPlusPart;
import utils.Utils;
import utils.yaml.YAML;
import utilsBlocks.Tip;
import utilsBlocks.UtilsBlocks;
import window.Key;

public class ItemTableClient extends ItemTable {

	public static Cursor defaultCursor = Cursor.getDefaultCursor();

	public static Image grid, fill, mouse, add, unit, destroy, miniature, castle, tree, goTo, build, axe;

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
		ResourceType.setTextureFolder(texturePack.getFolder());

		for (int i : items.keySet())
			if (texturePack.yamls.get(i) != null)
				items.get(i).setTexture(texturePack.yamls.get(i));

		mouse = Utils.getImage(texturePack.getFolder() + "menu/game/mouse");
		destroy = Utils.getImage(texturePack.getFolder() + "menu/editor/tips/destroy");

		grid = Utils.getImage(texturePack.getFolder() + "menu/editor/tips/grid");
		fill = Utils.getImage(texturePack.getFolder() + "menu/editor/fill");
		miniature = Utils.getImage(texturePack.getFolder() + "menu/editor/tips/miniature");

		add = UtilsBlocks.getImage(ItemID.GRASS, 30, 30);
		unit = UtilsBlocks.getImage(ItemID.UNIT, 30, 30);
		castle = UtilsBlocks.getImage(ItemID.CASTLE, 30, 30);
		tree = UtilsBlocks.getImage(ItemID.TREE, 30, 30);

		goTo = Utils.getImage(texturePack.getFolder() + "menu/game/goto");
		build = Utils.getImage(texturePack.getFolder() + "menu/game/tips/build");
		axe = Utils.getImage(texturePack.getFolder() + "menu/game/tips/axe");
	}

	public static TexturePack getTexturePack() {
		return texturePack;
	}

	// =========================================================================================================================
	// Graphic Engine

	public static boolean drawContour(int itemID) {
		return get(itemID).contour;
	}

	// =========================================================================================================================
	// Textures

	public static Camera getCamera(int itemID) {
		return get(itemID).camera;
	}

	public static Camera getCamera(Cube cube) {
		int itemID = cube.multicube == null ? cube.getItemID() : cube.multicube.itemID;
		return getCamera(itemID);
	}

	public static int getGamerColor(Gamer gamer) {
		switch (gamer.getNumber()) {
		case 1:
			return 0x0000ff;
		case 2:
			return 0xff0000;
		default:
			return 0x9d2c84;
		}
	}

	public static int getMapColor(Cube cube) {
		if (cube == Chunk.VOID)
			return 0;

		if (cube.unit != null)
			return getGamerColor(cube.unit.getGamer());
		if (cube.build != null)
			return getGamerColor(cube.build.getGamer());

		return get(cube.getItemID()).mapColor;
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
		if (get(itemID).name != null)
			return get(itemID).name;
		return get(itemID).tag;
	}

	public static String getName(Cube cube) {
		String name = getName(cube.multicube == null ? cube.getItemID() : cube.multicube.itemID);

		if (name == null)
			name = "NULL";

		return name;
	}

	public static TextPlus[] getTips(Tip tip) {
		String[] tags = tip.getTags();
		TextPlus[] tips = new TextPlus[tags.length];

		for (int i = 0; i < tags.length; i++) {
			tips[i] = modifyText(lang.getString(tip.getPath() + "." + tags[i]), tip);
		}

		return tips;
	}

	public static String getKey(Key key) {
		return lang.getString("keys." + key.name().toLowerCase());
	}

	public static String getText(String path) {
		return lang.getString("text." + path);
	}

	// =========================================================================================================================
	// Text (modifications)

	private static TextPlus modifyText(String str, Tip tip) {
		TextPlus text = new TextPlus();

		int start = 0, index = 0, temp;

		while ((index = str.indexOf('~', start)) != -1) {
			text.add(str.substring(start, index));

			int space = 1_000_000;
			if ((temp = str.indexOf(' ', index)) != -1 && temp < space)
				space = temp;
			if ((temp = str.indexOf(',', index)) != -1 && temp < space)
				space = temp;
			if ((temp = str.indexOf(')', index)) != -1 && temp < space)
				space = temp;
			if ((temp = str.indexOf('.', index)) != -1 && temp < space)
				space = temp;
			if (space == 1_000_000)
				space = str.length();

			String tag = str.substring(index + 1, space);

			Image img = getImage(tag);
			if (img != null)
				text.add(img, 30, 30);
			else {
				String key = getPlainTextKey(tag);

				if (key == null)
					try {
						key = KeyEvent.getKeyText(Key.valueOf(tag).code);
					} catch (IllegalArgumentException e) {
						key = "Error";
						System.out.println("ERROR: " + tag);
					}

				key = "[" + key + "]";

				Color color = tip == Tip.GAME_GLOBAL ? Color.BLUE : Color.WHITE;
				text.add(new TextPlusPart(key, null, color));
			}

			start = space;
		}

		text.add(str.substring(start));

		return text;
	}

	// =========================================================================================================================

	private static Image getImage(String tag) {
		switch (tag) {
		case "MOUSE":
			return mouse;
		case "ADD":
			return add;
		case "DESTROY":
			return destroy;

		case "UNIT":
			return unit;
		case "TREE":
			return tree;
		case "CASTLE":
			return castle;

		case "GOTO":
			return goTo;
		case "BUILD":
			return build;
		case "AXE":
			return axe;

		case "GRID":
			return grid;
		case "FILL":
			return fill;
		case "MINIATURE":
			return miniature;
		}
		return null;
	}

	private static String getPlainTextKey(String tag) {
		switch (tag) {
		case "ESC":
			return "Esc";
		case "CTRL":
			return "Ctrl";
		case "SHIFT":
			return "Shift";
		case "ALT":
			return "Alt";
		case "SHIFT_CTRL":
			return "Shift+Ctrl";
		case "ENTER":
			return "Enter";

		case "UNDO":
			return "Ctrl+Z";
		case "REDO":
			return "Ctrl+Y";

		case "COPY":
			return "Ctrl+C";
		case "PASTE":
			return "Ctrl+V";
		case "ARROW_LEFT":
			return "◄";
		case "ARROW_RIGHT":
			return "►";

		case "SELECT_ALL":
			return "Ctrl+A";
		case "UNSELECT_ALL":
			return "Ctrl+Maj+A";
		}
		return null;
	}
}
