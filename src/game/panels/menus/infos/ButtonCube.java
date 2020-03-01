package game.panels.menus.infos;

import java.awt.Color;
import java.awt.Font;

import data.id.ItemTableClient;
import data.map.Cube;
import environment.extendsData.CubeClient;
import game.Game;
import utilsBlocks.ButtonBlocks;

public class ButtonCube extends ButtonBlocks {
	private static final long serialVersionUID = 518575559823257170L;

	private Game game;
	private Cube cube;

	// =========================================================================================================================

	public ButtonCube(Game game, Cube cube) {
		this.game = game;
		this.cube = cube;

		setSelectable(true, false);
		setColor(Color.GRAY, Color.WHITE);
		setPadding(5);

		setModel(new CubeClient(cube));

		setFont(new Font("monospace", Font.PLAIN, 12));
		setText(ItemTableClient.getName(cube));
		setTextBackground(new Color(75, 75, 75));
		setTextYLocation(5, ButtonBlocks.BOTTOM);
	}

	// =========================================================================================================================

	@Override
	public void eventClick() {
		game.setCubeToAdd(cube);
	}
}
