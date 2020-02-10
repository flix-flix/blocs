
package dataManager;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Locale;

import data.id.ItemID;
import data.id.ItemTableClient;
import data.map.Cube;
import environment.extendsData.CubeClient;
import utils.FlixBlocksUtils;
import utils.panels.FButton;
import utilsBlocks.ButtonBlocks;

public class ButtonDataManager extends ButtonBlocks {
	private static final long serialVersionUID = 5730944323589738090L;

	private DataManager dm;
	private ActionDataManager action;

	public ButtonDataManager(DataManager dm, ActionDataManager action) {
		this.dm = dm;
		this.action = action;

		switch (action) {

		case LANG:
			if (ItemTableClient.getLanguage().equals(Locale.FRENCH.getLanguage()))
				setImage(FlixBlocksUtils.getImage("static/flags/fr"), FButton.KEEP_RATIO);
			else
				setImage(FlixBlocksUtils.getImage("static/flags/en"), FButton.KEEP_RATIO);

			setPadding(10);
			break;
		case TEXTURE:
			setModel(new CubeClient(new Cube(ItemID.GRASS)));
			setPadding(5);
			break;

		case NONE:
			setBackground(Color.LIGHT_GRAY);
			break;
			
		case QUIT:
			setImage(FlixBlocksUtils.getImage("static/quit"));
			break;

		default:
			setText("ERROR");
			break;
		}

	}

	@Override
	public void click(MouseEvent e) {
		super.click(e);

		dm.click(action);
	}
}
