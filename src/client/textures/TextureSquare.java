package client.textures;

import java.awt.image.BufferedImage;

import utils.FlixBlocksUtils;

public class TextureSquare {

	public int width, height;

	// row * width + col
	int color[];

	public static TextureSquare defaultFace;

	static {
		defaultFace = new TextureSquare(10, 10);
		for (int i = 0; i < 10; i++)
			for (int j = 0; j < 10; j++)
				defaultFace.setColor(i, j, 0);
	}

	// =========================================================================================================================

	public TextureSquare(int width, int height) {
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

	public static TextureSquare generateSquare(String file) {
		BufferedImage bimg = (BufferedImage) FlixBlocksUtils.getImage(file);

		int[] color = new int[bimg.getHeight() * bimg.getWidth()];

		for (int i = 0; i < bimg.getHeight(); i++)
			for (int j = 0; j < bimg.getWidth(); j++)
				color[(bimg.getHeight() - 1 - i) * bimg.getWidth() + j] = bimg.getRGB(j, i);

		return new TextureSquare(color, bimg.getWidth());
	}

	// =========================================================================================================================

	/** Returns a portion of the Face */
	public TextureSquare getRect(int x, int y, int width, int height) {
		TextureSquare text = new TextureSquare(width, height);

		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				text.setColor(j, i, getColor(y + j, x + i));

		return text;
	}
}
