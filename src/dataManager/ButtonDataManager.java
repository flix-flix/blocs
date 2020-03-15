
package dataManager;

import java.awt.Color;
import java.util.Locale;

import data.id.ItemID;
import data.id.ItemTableClient;
import data.map.Cube;
import utils.Utils;
import utils.panels.FButton;
import utilsBlocks.ButtonBlocks;

public class ButtonDataManager extends ButtonBlocks {
	private static final long serialVersionUID = 5730944323589738090L;

	private DataManager dm;
	private ActionDataManager action;

	// =========================================================================================================================

	public ButtonDataManager(DataManager dm, ActionDataManager action) {
		this.dm = dm;
		this.action = action;

		switch (action) {

		case LANG:
			setWIP();
			if (ItemTableClient.getLanguage().equals(Locale.FRENCH.getLanguage()))
				setImage(Utils.getResourceImage("/flags/fr.png"), FButton.KEEP_RATIO);
			else
				setImage(Utils.getResourceImage("/flags/en.png"), FButton.KEEP_RATIO);

			setPadding(10);
			break;
		case TEXTURE:
			setWIP();
			setModel(new Cube(ItemID.GRASS));
			setPadding(5);
			break;

		case SHOW_FIELDS:
		case TREE:
			setWIP();
		case QUIT:
			setImage(Utils.getImage("texturesPacks/classic/menu/dataManager/" + action.name().toLowerCase()),
					FButton.KEEP_RATIO);
			break;

		case NONE:
			setBackground(Color.LIGHT_GRAY);
			break;


		default:
			setText("ERROR");
			break;
		}
	}

	// =========================================================================================================================

	@Override
	public void eventClick() {
		dm.click(action);
	}
}
