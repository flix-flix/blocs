package client.textures;

import java.awt.image.BufferedImage;

import utils.FlixBlocksUtils;

public class TextureSquare {

	public int width, height;

	// row * width + col
	private int color[];

	// =========================================================================================================================

	public TextureSquare(int height, int width) {
		this(new int[height * width], width);
	}

	public TextureSquare(int[] color, int width) {
		this.width = width;
		this.height = color.length / width;
		this.color = color;
	}

	// =========================================================================================================================

	public int getColor(int row, int col) {
		return color[row * width + col];
	}

	public void setColor(int row, int col, int color) {
		this.color[row * width + col] = color;
	}

	// =========================================================================================================================

	public static TextureSquare generateSquare(String folder, String file) {
		BufferedImage bimg = (BufferedImage) FlixBlocksUtils.getImage(folder + "/" + file);

		int[] color = new int[bimg.getHeight() * bimg.getWidth()];

		for (int i = 0; i < bimg.getHeight(); i++)
			for (int j = 0; j < bimg.getWidth(); j++)
				color[(bimg.getHeight() - 1 - i) * bimg.getWidth() + j] = bimg.getRGB(j, i);

		return new TextureSquare(color, bimg.getWidth());
	}
}
