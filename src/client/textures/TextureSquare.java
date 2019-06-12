package client.textures;

import utils.FlixBlocksUtils;

public class TextureSquare {

	// line [0] = down | column [0] = left
	public int color[][];

	// =========================================================================================================================

	public TextureSquare() {
	}

	public TextureSquare(int[][] color) {
		this.color = color;
	}

	public TextureSquare(String folder, int id) {
		this(folder, id + "");
	}

	public TextureSquare(String folder, String file) {
		this(FlixBlocksUtils.imgToTab(folder, file));
	}

	// =========================================================================================================================

	public TextureSquare fusion(TextureSquare texture) {
		int[][] tab = new int[color.length][color[0].length];
		for (int row = 0; row < color.length; row++)
			for (int col = 0; col < color[0].length; col++)

				if (texture.color[row][col] < -16000000)
					tab[row][col] = texture.color[row][col];
				else
					tab[row][col] = color[row][col];

		return new TextureSquare(tab);
	}

	public int lighter(int color, int shade) {
		color++;

		int red = color / (256 * 256);
		int green = (color / 256) % 256;
		int blue = color % 256;

		red = Math.min(0, red + shade);
		green = Math.min(0, green + shade);
		blue = Math.min(0, blue + shade);

		return -16_777_217 + red * 256 * 256 + green * 256 + blue;
	}
}
