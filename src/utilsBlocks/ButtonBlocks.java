package utilsBlocks;

import java.awt.Graphics;
import java.awt.Image;

import data.id.ItemTableClient;
import environment.extendsData.CubeClient;
import environment.extendsData.MapClient;
import graphicEngine.calcul.Engine;
import utils.FlixBlocksUtils;
import utils.panels.FButton;

public class ButtonBlocks extends FButton {
	private static final long serialVersionUID = 7876247726882411115L;

	Engine engine = null;
	MapClient map = null;

	// ======================= WIP =========================
	private static Image wipImg;
	private boolean wip = false;

	static {
		wipImg = FlixBlocksUtils.getImage("static/WIP");
	}

	// =========================================================================================================================

	@Override
	protected void paintCenter(Graphics g) {
		super.paintCenter(g);

		if (wip)
			g.drawImage(wipImg, 0, 0, getContentWidth() - 1, getContentHeight() - 1, null);
	}

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

	public void setWIP() {
		wip = true;
	}

	// =========================================================================================================================

	@Override
	public void resize() {
		super.resize();
		update();
	}
}
