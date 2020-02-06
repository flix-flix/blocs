package utilsBlocks;

import data.id.ItemTableClient;
import environment.extendsData.CubeClient;
import environment.extendsData.MapClient;
import graphicEngine.calcul.Engine;
import utils.panels.FButton;

public class ButtonBlocks extends FButton {
	private static final long serialVersionUID = 7876247726882411115L;

	Engine engine = null;
	MapClient map = null;

	// =========================================================================================================================

	// =========================================================================================================================

	public void setModel(CubeClient cube) {
		map = new MapClient();
		map.add(cube);

		engine = new Engine(ItemTableClient.getCamera(cube.getItemID()), cube);
		engine.setBackground(Engine.NONE);

		update();
	}

	public void update() {
		if (engine != null)
			setImage(engine.getImage(getContentWidth(), getContentHeight()));
	}

	// =========================================================================================================================

	@Override
	public void resize() {
		super.resize();
		update();
	}
}
