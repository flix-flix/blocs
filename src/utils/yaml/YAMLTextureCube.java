package utils.yaml;

import client.textures.TextureCube;
import client.textures.TextureFace;
import client.textures.TextureSquare;
import data.map.enumerations.Face;

public class YAMLTextureCube {

	public int id;
	public String name;

	int x, y, z;

	// face-row-col
	int[][] faces = new int[6][];
	TextureSquare[] ts = new TextureSquare[6];
	TextureFace[] tf = new TextureFace[6];
	TextureCube texture = new TextureCube(tf);

	int mapColor;

	// =========================================================================================================================

	public YAMLTextureCube(YAML yaml) {
		id = yaml.getInt("id");
		name = yaml.getString("name");

		x = yaml.getInt("pixels.x");
		y = yaml.getInt("pixels.y");
		z = yaml.getInt("pixels.z");

		for (Face face : Face.faces) {
			int size = getFaceHeight(face) * getFaceWidth(face);
			faces[face.ordinal()] = yaml.getHexaDoubleArrayInline("colors." + face.name().toLowerCase(), size);

			ts[face.ordinal()] = new TextureSquare(faces[face.ordinal()], getFaceWidth(face));
			tf[face.ordinal()] = new TextureFace(ts[face.ordinal()]);
		}
		texture = new TextureCube(tf);

		mapColor = yaml.getHexa("minimap.color");
	}

	// =========================================================================================================================

	private int getFaceWidth(Face face) {
		switch (face) {
		case UP:
		case DOWN:
		case NORTH:
		case SOUTH:
			return z;
		case EAST:
		case WEST:
			return x;
		default:
			return -1;
		}
	}

	private int getFaceHeight(Face face) {
		switch (face) {
		case UP:
		case DOWN:
			return x;
		case NORTH:
		case SOUTH:
		case EAST:
		case WEST:
			return y;
		default:
			return -1;
		}
	}

	// =========================================================================================================================

	public TextureCube getTextureCube() {
		return texture;
	}
}
