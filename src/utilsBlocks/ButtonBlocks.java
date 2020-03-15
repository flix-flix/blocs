package utilsBlocks;

import java.awt.Graphics;
import java.awt.Image;

import data.id.ItemTableClient;
import data.map.Cube;
import environment.extendsData.MapClient;
import graphicEngine.calcul.Camera;
import graphicEngine.calcul.Engine;
import utils.Utils;
import utils.panels.FButton;

public class ButtonBlocks extends FButton {
	private static final long serialVersionUID = 7876247726882411115L;

	private Engine engine = null;
	private MapClient map = null;

	private Image emptyImg = null;

	// ======================= WIP =========================
	private static Image wipImg = Utils.getResourceImage("/WIP.png");
	private boolean wip = false;

	// =========================================================================================================================

	@Override
	public void paintCenter(Graphics g) {
		super.paintCenter(g);

		if (wip)
			g.drawImage(wipImg, 0, 0, getContentWidth(), getContentHeight(), null);
	}

	// =========================================================================================================================

	public void setModel(Cube cube) {
		if (cube == null) {
			engine = null;
			update();
			return;
		}

		map = new MapClient();
		map.add(cube);

		engine = new Engine(ItemTableClient.getCamera(cube), map);
		engine.setBackground(Engine.NONE);

		update();
	}

	public void setCamera(Camera camera) {
		if (engine == null)
			return;
		engine.setCamera(camera);
		update();
	}

	public void update() {
		if (engine == null) {
			if (emptyImg != null)
				setImage(emptyImg);
		} else
			setImage(engine.getImage(getContentWidth(), getContentHeight()));
		repaint();
	}

	// =========================================================================================================================

	public void setEmptyImage(Image emptyImg) {
		this.emptyImg = emptyImg;
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
