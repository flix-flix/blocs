package environment.textures;

import java.awt.image.BufferedImage;

import utils.FlixBlocksUtils;

public class TextureSquare {

	public int width, height;

	// Y * width + X
	int color[];

	public static TextureSquare defaultFace;

	static {
		defaultFace = new TextureSquare(10, 10);
		for (int x = 0; x < 10; x++)
			for (int y = 0; y < 10; y++)
				defaultFace.setColor(x, y, 0);
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

	public int getColor(int y, int x) {
		return color[y * width + x];
	}

	public void setColor(int y, int x, int color) {
		this.color[y * width + x] = color;
	}

	// =========================================================================================================================

	public static TextureSquare generateSquare(String file) {
		return generateSquare((BufferedImage) FlixBlocksUtils.getImage(file));
	}

	public static TextureSquare generateSquare(BufferedImage bimg) {
		int[] color = new int[bimg.getHeight() * bimg.getWidth()];

		for (int x = 0; x < bimg.getWidth(); x++)
			for (int y = 0; y < bimg.getHeight(); y++)
				color[(bimg.getHeight() - 1 - y) * bimg.getWidth() + x] = bimg.getRGB(x, y);

		return new TextureSquare(color, bimg.getWidth());
	}

	// =========================================================================================================================

	/** Returns a portion of the Face */
	public TextureSquare getRect(int startX, int startY, int width, int height) {
		TextureSquare text = new TextureSquare(width, height);

		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				text.setColor(y, x, getColor(startY + y, startX + x));

		return text;
	}
}
