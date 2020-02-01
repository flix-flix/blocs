package environment.textures;

import data.id.ItemTable;
import data.map.Cube;
import data.map.enumerations.Face;
import utils.yaml.YAML;

public class TextureMulti {

	TextureSquare[] textures = new TextureSquare[6];
	TextureCube cubes[][][];

	int resX, resY, resZ;

	// =========================================================================================================================

	public TextureMulti(TextureSquare[] textures, int itemID) {
		this.textures = textures;

		this.resX = ItemTable.getXSize(itemID);
		this.resY = ItemTable.getYSize(itemID);
		this.resZ = ItemTable.getZSize(itemID);

		cut();
	}

	// =========================================================================================================================

	private void cut() {
		int pixelsX = textures[0].height;
		int pixelsY = textures[2].height;
		int pixelsZ = textures[0].width;

		cubes = new TextureCube[resX][resY][resZ];

		for (int x = 0; x < resX; x++)
			for (int y = 0; y < resY; y++)
				for (int z = 0; z < resZ; z++)
					cubes[x][y][z] = new TextureCube();

		// UP
		for (int x = 0; x < resX; x++)
			for (int z = 0; z < resZ; z++)
				cubes[x][resY - 1][z].setFace(Face.UP, textures[0].getRect((pixelsZ / resZ) * z, (pixelsX / resX) * x,
						pixelsZ / resZ, pixelsX / resX));

		// DOWN
		for (int x = 0; x < resX; x++)
			for (int z = 0; z < resZ; z++)
				cubes[x][0][z].setFace(Face.DOWN, textures[1].getRect((pixelsZ / resZ) * z,
						(pixelsX / resX) * (resX - 1 - x), pixelsZ / resZ, pixelsX / resX));

		// NORTH
		for (int y = 0; y < resY; y++)
			for (int z = 0; z < resZ; z++)
				cubes[resX - 1][y][z].setFace(Face.NORTH, textures[2].getRect((pixelsZ / resZ) * (resZ - 1 - z),
						(pixelsY / resY) * y, pixelsZ / resZ, pixelsY / resY));

		// SOUTH
		for (int y = 0; y < resY; y++)
			for (int z = 0; z < resZ; z++)
				cubes[0][y][z].setFace(Face.SOUTH, textures[3].getRect((pixelsZ / resZ) * z, (pixelsY / resY) * y,
						pixelsZ / resZ, pixelsY / resY));

		// EAST
		for (int y = 0; y < resY; y++)
			for (int x = 0; x < resX; x++)
				cubes[x][y][resZ - 1].setFace(Face.EAST, textures[4].getRect((pixelsX / resX) * x, (pixelsY / resY) * y,
						pixelsX / resX, pixelsY / resY));

		// WEST
		for (int y = 0; y < resY; y++)
			for (int x = 0; x < resX; x++)
				cubes[x][y][0].setFace(Face.WEST, textures[5].getRect((pixelsX / resX) * (resX - 1 - x),
						(pixelsY / resY) * y, pixelsX / resX, pixelsY / resY));
	}

	// =========================================================================================================================

	public TextureSquare getFace(Cube cube, Face face) {
		if (cube.multibloc == null)
			return textures[face.ordinal()];
		return cubes[cube.multiblocX][cube.multiblocY][cube.multiblocZ].getTexture(face, cube.rotation,
				cube.orientation);
	}

	// =========================================================================================================================

	public YAML getYAML(int id, int miniMapColor) {
		YAML yaml = new YAML();

		yaml.put("id", id);

		yaml.put("pixels.x", textures[0].height);
		yaml.put("pixels.y", textures[2].height);
		yaml.put("pixels.z", textures[0].width);

		for (Face face : Face.faces)
			yaml.putHexaDoubleArrayInline("colors." + face.name().toLowerCase(), textures[face.ordinal()].color,
					textures[face.ordinal()].width);

		yaml.putHexa("minimap.color", miniMapColor);

		return yaml;
	}
}
