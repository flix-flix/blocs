package client.textures;

import java.util.TreeMap;

import data.id.ItemTable;
import data.map.Cube;
import data.map.enumerations.Face;
import utils.FlixBlocksUtils;

public class TextureMulti {

	TextureSquare[] textures = new TextureSquare[6];
	TextureCube cubes[][][];

	int resX, resY, resZ;

	// =========================================================================================================================

	public TextureMulti(TextureSquare[] textures, int resX, int resY, int resZ) {
		this.textures = textures;

		this.resX = resX;
		this.resY = resY;
		this.resZ = resZ;

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
		return cubes[cube.multiblocX][cube.multiblocY][cube.multiblocZ].getTexture(face, cube.rotation,
				cube.orientation);
	}

	// =========================================================================================================================

	public TreeMap<String, Object> getYAMLTree(int id, String name, int miniMapColor) {
		TreeMap<String, Object> tree = new TreeMap<>();

		tree.put("id", id);
		tree.put("name", name);

		TreeMap<String, Object> pixels = new TreeMap<>();
		tree.put("pixels", pixels);

		pixels.put("x", textures[0].height);
		pixels.put("y", textures[2].height);
		pixels.put("z", textures[0].width);

		TreeMap<String, Object> cubes = new TreeMap<>();
		tree.put("cubes", cubes);

		cubes.put("x", ItemTable.getXSize(id));
		cubes.put("y", ItemTable.getYSize(id));
		cubes.put("z", ItemTable.getZSize(id));

		TreeMap<String, Object> colors = new TreeMap<>();
		tree.put("colors", colors);

		for (Face face : Face.faces) {
			int[] array = textures[face.ordinal()].color;
			int w = textures[face.ordinal()].width;
			int h = textures[face.ordinal()].height;
			String[][] colorsFace = new String[h][w];

			for (int i = 0; i < h; i++)
				for (int j = 0; j < w; j++)
					colorsFace[i][j] = FlixBlocksUtils.hexaToString(array[i * w + j]);

			colors.put(face.name().toLowerCase(), colorsFace);
		}

		TreeMap<String, Object> minimap = new TreeMap<>();
		tree.put("minimap", minimap);

		minimap.put("color", FlixBlocksUtils.hexaToString(miniMapColor));

		return tree;
	}
}
