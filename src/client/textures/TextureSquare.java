package client.textures;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import utils.FlixBlocksUtils;

public class TextureSquare {

	public int width, height;

	// row * width + col
	private int color[];
	private int alpha[];

	// =========================================================================================================================

	public TextureSquare(int height, int width) {
		this(new int[height * width], width);
	}

	public TextureSquare(int[] color, int width) {
		this(color, new int[color.length], width);
		Arrays.fill(alpha, 255);
	}

	public TextureSquare(int[] color, int[] alpha, int width) {
		this.width = width;
		this.height = color.length / width;
		this.color = color;

		// Remove alpha data from color
		for (int i = 0; i < color.length; i++) {
			int x = color[i] + 16_777_216 < -1 ? color[i] : color[i] + 16_777_216;
			int red = x / 256 / 256 % 256;
			int green = x / 256 % 256;
			int blue = x % 256;
			this.color[i] = -16_777_216 + red * 256 * 256 + green * 256 + blue;
		}

		this.alpha = alpha;
	}

	// =========================================================================================================================

	public int getAlpha(int row, int col) {
		return alpha[(height - 1 - row) * width + col];
	}

	public void setAlpha(int row, int col, int alpha) {
		this.alpha[row * width + col] = alpha;
	}

	public int getColor(int row, int col) {
		return color[row * width + col];
	}

	public void setColor(int row, int col, int color) {
		this.color[row * width + col] = color;
	}

	// =========================================================================================================================

	public TextureSquare miningFusion(TextureSquare texture) {
		return this;
	}

	// =========================================================================================================================

	public int lighter(int color, int shade) {
		color += 16_777_216;

		int red = (color / (256 * 256)) % 256;
		int green = (color / 256) % 256;
		int blue = color % 256;

		red = Math.min(255, red + shade);
		green = Math.min(255, green + shade);
		blue = Math.min(255, blue + shade);

		return -16_777_216 + red * 256 * 256 + green * 256 + blue;
	}

	// =========================================================================================================================

	public static TextureSquare generateSquare(String folder, String file) {
		BufferedImage bimg = (BufferedImage) FlixBlocksUtils.getImage(folder + "/" + file);

		int[] color = new int[bimg.getHeight() * bimg.getWidth()];

		for (int i = 0; i < bimg.getHeight(); i++)
			for (int j = 0; j < bimg.getWidth(); j++)
				color[(bimg.getHeight() - 1 - i) * bimg.getWidth() + j] = bimg.getRGB(j, i);

		WritableRaster raster = bimg.getAlphaRaster();

		int[] alpha = new int[bimg.getHeight() * bimg.getWidth()];

		if (raster == null)
			Arrays.fill(alpha, 255);
		else
			raster.getPixels(0, 0, bimg.getWidth(), bimg.getHeight(), alpha);

		return new TextureSquare(color, alpha, bimg.getWidth());
	}

	// =========================================================================================================================

	// x = getRGB(x, y)
	// x + 16_777_216
	// if(x<0) x++;
	// B : x % 256
	// G : x / 256 % 256
	// R : x / 256 / 256 % 256
	// A : x / 256 / 256 / 256 % 256
}
