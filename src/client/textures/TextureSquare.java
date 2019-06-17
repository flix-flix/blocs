package client.textures;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import utils.FlixBlocksUtils;

public class TextureSquare {

	public int width, height;

	// TODO color[][] -> []
	// line [0] = down | column [0] = left
	public int color[][];
	public int alpha[];

	// =========================================================================================================================

	public TextureSquare(int rows, int cols) {
		this(new int[rows][cols]);
	}

	public TextureSquare(int[][] color) {
		this(color, new int[color.length * color[0].length]);
		Arrays.fill(alpha, 255);
	}

	public TextureSquare(int[][] color, int[] alpha) {
		this.width = color[0].length;
		this.height = color.length;
		this.color = color;

		// Remove alpha data from color
		for (int i = 0; i < color.length; i++)
			for (int j = 0; j < color[0].length; j++) {
				int x = color[i][j] + 16_777_216 < -1 ? color[i][j] : color[i][j] + 16_777_216;
				int red = x / 256 / 256 % 256;
				int green = x / 256 % 256;
				int blue = x % 256;
				this.color[i][j] = -16_777_216 + red * 256 * 256 + green * 256 + blue;
			}

		this.alpha = alpha;
	}

	// =========================================================================================================================

	public int getAlpha(int row, int col) {
		return alpha[(width - 1 - row) * width + col];
	}

	public void setAlpha(int row, int col, int alpha) {
		this.alpha[row * width + col] = alpha;
	}

	public int getColor(int row, int col) {
		return color[row][col];
	}

	// =========================================================================================================================

	public TextureSquare miningFusion(TextureSquare texture) {
		int[][] tab = new int[color.length][color[0].length];
		for (int row = 0; row < color.length; row++)
			for (int col = 0; col < color[0].length; col++)

				// if (texture.color[row][col] < -16000000)
				// tab[row][col] = ;
				// else
				// tab[row][col] = color[row][col];

				// TODO Handle alpha in fusion()
				if (texture.getAlpha(row, col) == 0)
					tab[row][col] = color[row][col];
				else
					tab[row][col] = texture.color[row][col];

		// TODO Handle alpha in fusion()
		return new TextureSquare(tab);
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

		int[][] color = new int[bimg.getHeight()][bimg.getWidth()];

		for (int i = 0; i < color.length; i++)
			for (int j = 0; j < color[0].length; j++)
				color[color.length - 1 - i][j] = bimg.getRGB(j, i);

		WritableRaster raster = bimg.getAlphaRaster();

		int[] alpha = new int[bimg.getHeight() * bimg.getWidth()];

		if (raster == null)
			Arrays.fill(alpha, 255);
		else
			raster.getPixels(0, 0, bimg.getWidth(), bimg.getHeight(), alpha);

		return new TextureSquare(color, alpha);
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
