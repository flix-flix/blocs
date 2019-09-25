package client.textures;

import data.ItemTable;
import data.enumeration.Face;
import data.enumeration.ItemID;
import data.map.Cube;

public class TextureMulti {

	TextureCube cubes[][][];

	// =========================================================================================================================

	TextureMulti(ItemID itemID) {
		// (up, down, north, south, east, west)
		TextureSquare[] faces = new TextureSquare[6];
		for (Face face : Face.faces)
			faces[face.ordinal()] = TextureSquare.generateSquare("multi",
					TextureFace.getFileName("multi", itemID.id + "", face));

		int pixelsX = faces[0].height;
		int pixelsY = faces[2].height;
		int pixelsZ = faces[0].width;

		int resX = ItemTable.getXSize(itemID);
		int resY = ItemTable.getYSize(itemID);
		int resZ = ItemTable.getZSize(itemID);

		cubes = new TextureCube[resX][resY][resZ];

		for (int x = 0; x < resX; x++)
			for (int y = 0; y < resY; y++)
				for (int z = 0; z < resZ; z++)
					cubes[x][y][z] = new TextureCube();

		// UP
		for (int x = 0; x < resX; x++)
			for (int z = 0; z < resZ; z++)
				cubes[x][resY - 1][z].setFace(Face.UP,
						faces[0].getRect((pixelsZ / resZ) * z, (pixelsX / resX) * x, pixelsZ / resZ, pixelsX / resX));

		// DOWN
		for (int x = 0; x < resX; x++)
			for (int z = 0; z < resZ; z++)
				cubes[x][0][z].setFace(Face.DOWN, faces[1].getRect((pixelsZ / resZ) * z,
						(pixelsX / resX) * (resX - 1 - x), pixelsZ / resZ, pixelsX / resX));

		// NORTH
		for (int y = 0; y < resY; y++)
			for (int z = 0; z < resZ; z++)
				cubes[resX - 1][y][z].setFace(Face.NORTH, faces[2].getRect((pixelsZ / resZ) * (resZ - 1 - z),
						(pixelsY / resY) * y, pixelsZ / resZ, pixelsY / resY));

		// SOUTH
		for (int y = 0; y < resY; y++)
			for (int z = 0; z < resZ; z++)
				cubes[0][y][z].setFace(Face.SOUTH,
						faces[3].getRect((pixelsZ / resZ) * z, (pixelsY / resY) * y, pixelsZ / resZ, pixelsY / resY));

		// EAST
		for (int y = 0; y < resY; y++)
			for (int x = 0; x < resX; x++)
				cubes[x][y][resZ - 1].setFace(Face.EAST,
						faces[4].getRect((pixelsX / resX) * x, (pixelsY / resY) * y, pixelsX / resX, pixelsY / resY));

		// WEST
		for (int y = 0; y < resY; y++)
			for (int x = 0; x < resX; x++)
				cubes[x][y][0].setFace(Face.WEST, faces[5].getRect((pixelsX / resX) * (resX - 1 - x),
						(pixelsY / resY) * y, pixelsX / resX, pixelsY / resY));
	}

	// =========================================================================================================================

	public TextureSquare getFace(Cube cube, Face face) {
		return cubes[cube.multiblocX][cube.multiblocY][cube.multiblocZ].getTexture(face, cube.rotation,
				cube.orientation);
	}
}
