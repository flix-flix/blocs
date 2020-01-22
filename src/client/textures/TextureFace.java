package client.textures;

public class TextureFace {

	/** Texture of the face pre-generated in the 4 possibles */
	TextureSquare normal, reverse, right, left;

	public int rotation = 0;

	// =========================================================================================================================

	public TextureFace(TextureSquare normal) {
		this.normal = normal;

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
