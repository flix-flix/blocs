package client.textures;

import data.map.enumerations.Face;
import utils.FlixBlocksUtils;

public class TextureFace {

	private static String folder = "blocs";

	/** Texture of the face pre-generated in the 4 possibles */
	TextureSquare normal, reverse, right, left;

	public int rotation = 0;

	// =========================================================================================================================

	public TextureFace() {
	}

	public TextureFace(TextureSquare normal) {
		this.normal = normal;
		generateRotatedTexture();
	}

	public TextureFace(String folder, String file) {
		this(TextureSquare.generateSquare(folder, file));
	}

	public TextureFace(String file) {
		this(folder, file);
	}

	public TextureFace(String folder, String id, Face face) {
		this(folder, getFileName(folder, id, face));
	}

	public TextureFace(String id, Face face) {
		this(folder, id, face);
	}

	// =========================================================================================================================

	public static String getFileName(String folder, String id, Face face) {
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

	public static String getFileName(String id, Face face) {
		return getFileName(folder, id, face);
	}

	// =========================================================================================================================

	public void setNormal(TextureSquare text) {
		normal = text;
		generateRotatedTexture();
	}

	public void generateRotatedTexture() {
		int rows = normal.height;
		int cols = normal.width;

		if (cols != rows)
			return;

		reverse = new TextureSquare(cols, rows);
		right = new TextureSquare(cols, rows);
		left = new TextureSquare(cols, rows);

		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++) {
				reverse.setColor(rows - 1 - row, cols - 1 - col, normal.getColor(row, col));
				right.setColor(cols - 1 - col, row, normal.getColor(row, col));
				left.setColor(col, rows - 1 - row, normal.getColor(row, col));
			}
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
