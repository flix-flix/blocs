package client.textures;

import data.enumeration.Face;
import utils.FlixBlocksUtils;

public class TextureFace {

	private static String folder = "blocs";

	public int rows = 0, cols = 0;

	public TextureSquare normal = new TextureSquare();
	public TextureSquare reverse = new TextureSquare();
	public TextureSquare right = new TextureSquare();
	public TextureSquare left = new TextureSquare();

	// =========================================================================================================================

	public TextureFace(int[][] color) {
		normal.color = color;
		generateRotatedTexture();
	}

	public TextureFace(int i) {
		this(i + "");
	}

	public TextureFace(String file) {
		this(folder, file);
	}

	public TextureFace(String folder, String file) {
		this(FlixBlocksUtils.imgToTab(folder, file));
	}

	// =========================================================================================================================

	public TextureFace(String id, Face face) {
		// Try to use <id>.png texture
		if (FlixBlocksUtils.pngExist(folder + "/" + id + "-" + face.toString().toLowerCase())) {
			normal.color = FlixBlocksUtils.imgToTab(folder, id + "-" + face.toString().toLowerCase());
			generateRotatedTexture();
			return;
		}

		// != UP : to avoid the profile texture on the top
		if (face != Face.UP)
			// Try to use the up texture for the down face
			if (face == Face.DOWN) {
				if (FlixBlocksUtils.pngExist(folder + "/" + id + "-up")) {
					normal.color = FlixBlocksUtils.imgToTab(folder, id + "-up");
					generateRotatedTexture();
					return;
				}
			}
			// Try to use the profile texture for the side faces
			else if (FlixBlocksUtils.pngExist(folder + "/" + id + "-pro")) {
				normal.color = FlixBlocksUtils.imgToTab(folder, id + "-pro");
				generateRotatedTexture();
				return;
			}

		// Use the default texture of the id
		normal.color = FlixBlocksUtils.imgToTab(folder, id);
		generateRotatedTexture();
	}

	public TextureFace(int id, Face face) {
		this(id + "", face);
	}

	// =========================================================================================================================

	public void generateRotatedTexture() {
		rows = normal.color.length;
		cols = normal.color[0].length;

		reverse.color = new int[rows][cols];
		right.color = new int[cols][rows];
		left.color = new int[cols][rows];

		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				reverse.color[rows - 1 - row][cols - 1 - col] = normal.color[row][col];

		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				right.color[cols - 1 - col][row] = normal.color[row][col];

		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				left.color[col][rows - 1 - row] = normal.color[row][col];
	}
}
