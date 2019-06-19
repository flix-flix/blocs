package client.textures;

import data.enumeration.Face;
import utils.FlixBlocksUtils;

public class TextureFace {

	private static String folder = "blocs";

	public int rows = 0, cols = 0;

	public TextureSquare normal, reverse, right, left;

	// =========================================================================================================================

	public TextureFace(String folder, String file) {
		normal = TextureSquare.generateSquare(folder, file);
		// generateRotatedTexture();
	}

	public TextureFace(String file) {
		this(folder, file);
	}

	// =========================================================================================================================

	public TextureFace(String id, Face face) {
		this(folder, getFileName(id, face));
	}

	// =========================================================================================================================

	public static String getFileName(String id, Face face) {
		// Try to use <id>-<face>.png texture
		if (FlixBlocksUtils.pngExist(folder + "/" + id + "-" + face.toString().toLowerCase()))
			return id + "-" + face.toString().toLowerCase();

		// != UP : to avoid the profile texture on the top
		if (face != Face.UP)
			// Try to use the up texture for the down face
			if (face == Face.DOWN) {
				if (FlixBlocksUtils.pngExist(folder + "/" + id + "-up"))
					return id + "-up";
			}
			// Try to use the profile texture for the side faces
			else if (FlixBlocksUtils.pngExist(folder + "/" + id + "-pro"))
				return id + "-pro";

		// Use the default texture of the id
		return id;
	}

	// =========================================================================================================================

	public void generateRotatedTexture() {
		rows = normal.color.length;
		cols = normal.color[0].length;

		reverse = new TextureSquare(rows, cols);
		right = new TextureSquare(rows, cols);
		left = new TextureSquare(rows, cols);

		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++) {
				reverse.color[rows - 1 - row][cols - 1 - col] = normal.color[row][col];
				reverse.setAlpha(rows - 1 - row, cols - 1 - col, normal.getAlpha(row, col));
			}

		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++) {
				right.color[cols - 1 - col][row] = normal.color[row][col];
				right.setAlpha(cols - 1 - col, row, normal.getAlpha(row, col));
			}

		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++) {
				left.color[col][rows - 1 - row] = normal.color[row][col];
				left.setAlpha(col, rows - 1 - row, normal.getAlpha(row, col));
			}
	}
}
