package utilsBlocks;

import java.awt.Color;
import java.awt.Font;

import data.id.ItemTableClient;
import data.map.Cube;
import environment.extendsData.CubeClient;

public class ButtonCube extends ButtonBlocks {
	private static final long serialVersionUID = 518575559823257170L;

	public ButtonCube(Cube cube) {
		setSelectable(true, false);
		setColor(Color.GRAY, Color.WHITE);
		setPadding(5);

		setModel(new CubeClient(cube));

		setFont(new Font("monospace", Font.PLAIN, 12));
		setText(ItemTableClient.getName(cube));
		setTextBackground(new Color(75, 75, 75));
		setTextYLocation(5, ButtonBlocks.BOTTOM);
	}
}
