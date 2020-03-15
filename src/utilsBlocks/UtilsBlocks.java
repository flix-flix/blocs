package utilsBlocks;

import java.awt.Color;
import java.awt.Image;

import data.id.ItemTable;
import data.id.ItemTableClient;
import environment.extendsData.MapClient;
import graphicEngine.calcul.Engine;

public class UtilsBlocks {

	public final static Color RED = new Color(157, 44, 44);
	public final static Color GREEN = new Color(12, 126, 28);

	// =========================================================================================================================

	public static Image getImage(int itemID, int width, int height) {
		MapClient map = new MapClient();
		map.add(ItemTable.create(itemID));
		Engine engineUnit = new Engine(ItemTableClient.getCamera(itemID), map);
		engineUnit.setBackground(Engine.NONE);
		return engineUnit.getImage(width, height);
	}
}
