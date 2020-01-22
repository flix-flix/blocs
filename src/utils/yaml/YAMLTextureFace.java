package utils.yaml;

import java.util.TreeMap;

import client.textures.TextureCube;
import client.textures.TextureFace;
import client.textures.TextureSquare;
import data.map.enumerations.Face;
import utils.FlixBlocksUtils;

public class YAMLTextureFace {

	public int id;
	public String name;

	int x, y, z;

	// face-row-col
	int[][] faces = new int[6][];
	TextureFace[] tf = new TextureFace[6];
	TextureCube texture = new TextureCube(tf);

	int mapColor;

	// =========================================================================================================================

	public YAMLTextureFace(TreeMap<String, Object> tree) {
		id = Integer.valueOf((String) tree.get("id"));
		name = (String) tree.get("name");

		TreeMap<String, String> pixels = (TreeMap<String, String>) tree.get("pixels");

		x = Integer.parseInt(pixels.get("x"));
		y = Integer.parseInt(pixels.get("y"));
		z = Integer.parseInt(pixels.get("z"));

		for (Face face : Face.faces) {
			int row = 0, col = 0;
			Object faceData = ((TreeMap) tree.get("colors")).get(face.name().toLowerCase());

			switch (face) {
			case UP:
			case DOWN:
				row = x;
				col = y;
				break;
			case NORTH:
			case SOUTH:
				row = y;
				col = z;
				break;
			case EAST:
			case WEST:
				row = y;
				col = x;
				break;
			}

			faces[face.ordinal()] = new int[row * col];
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < col; j++)
					faces[face.ordinal()][i * row + j] = FlixBlocksUtils
							.parseHexa(((String[]) ((Object[]) faceData)[i])[j]);
				tf[face.ordinal()] = new TextureFace(new TextureSquare(faces[face.ordinal()], col));
			}

			texture = new TextureCube(tf);
		}

		mapColor = FlixBlocksUtils.parseHexa((String) ((TreeMap<String, String>) tree.get("minimap")).get("color"));
	}

	// =========================================================================================================================

	public TextureCube getTextureCube() {
		return texture;
	}
}
