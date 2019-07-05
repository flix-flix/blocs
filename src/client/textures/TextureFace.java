package client.textures;

import data.enumeration.Face;
import utils.FlixBlocksUtils;

public class TextureFace {

	private static String folder = "blocs";

	/** Texture of the face pre-generated in the 4 possibles */
	private TextureSquare normal, reverse, right, left;

	public int rotation = 0;

	// =========================================================================================================================

	public TextureFace(String folder, String file) {
		normal = TextureSquare.generateSquare(folder, file);
		generateRotatedTexture();
	}

	public TextureFace(String file) {
		this(folder, file);
	}

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
		int rows = normal.height;
		int cols = normal.width;

		if (cols != rows)
			return;

		reverse = new TextureSquare(rows, cols);
		right = new TextureSquare(rows, cols);
		left = new TextureSquare(rows, cols);

		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				reverse.setColor(rows - 1 - row, cols - 1 - col, normal.getColor(row, col));

		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				right.setColor(cols - 1 - col, row, normal.getColor(row, col));

		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				left.setColor(col, rows - 1 - row, normal.getColor(row, col));
	}

	// =========================================================================================================================

	public TextureSquare getRotated() {
		switch (rotation % 4) {
		case 0:
			return normal;
		case 1:
			return right;
		case 2:
			return reverse;
		case 3:
			return left;
		}
		return null;
	}
}
