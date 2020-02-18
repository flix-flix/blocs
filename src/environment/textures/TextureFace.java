package environment.textures;

public class TextureFace {

	/** Texture of the face pre-generated in the 4 possibles */
	TextureSquare normal, reverse, right, left;

	public int rotation = 0;

	// =========================================================================================================================

	public TextureFace(TextureSquare normal) {
		this.normal = normal;

		if (normal.width != normal.height)
			return;

		reverse = new TextureSquare(normal.width, normal.height);
		right = new TextureSquare(normal.width, normal.height);
		left = new TextureSquare(normal.width, normal.height);

		for (int y = 0; y < normal.height; y++)
			for (int x = 0; x < normal.width; x++) {
				reverse.setColor(normal.height - 1 - y, normal.width - 1 - x, normal.getColor(y, x));
				right.setColor(normal.width - 1 - x, y, normal.getColor(y, x));
				left.setColor(x, normal.height - 1 - y, normal.getColor(y, x));
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
