package client.textures;

import data.enumeration.Face;
import data.enumeration.SensBloc;

public class TextureCube {

	//Faces : up, down, north, south, east, west
	private TextureFace[] textures = new TextureFace[6];

	// =========================================================================================================================

	public TextureCube(String id) {
		for (Face face : Face.faces)
			textures[face.ordinal()] = new TextureFace(id, face);
	}

	public TextureCube(int id) {
		this(id + "");
	}

	// =========================================================================================================================
	
	public TextureCube(TextureFace up, TextureFace down, TextureFace north, TextureFace south, TextureFace east,
			TextureFace west) {
		textures[0] = up;
		textures[1] = down;
		textures[2] = north;
		textures[3] = south;
		textures[4] = east;
		textures[5] = west;
	}

	public TextureCube(TextureFace up, TextureFace down, TextureFace cote) {
		this(up, down, cote, cote, cote, cote);
	}

	public TextureCube(String up, String down, String north, String south, String east, String west) {
		this(new TextureFace(up), new TextureFace(down), new TextureFace(north), new TextureFace(south),
				new TextureFace(east), new TextureFace(west));
	}

	public TextureCube(String up, String down, String pro) {
		this(new TextureFace(up), new TextureFace(down), new TextureFace(pro));
	}

	// =========================================================================================================================

	public TextureSquare getTexture(Face face) {
		return textures[face.ordinal()].normal;
	}

	public TextureFace getTextureFace(Face face) {
		return textures[face.ordinal()];
	}

	public TextureSquare getTexture(Face face, SensBloc sens) {
		switch (sens) {
		case AUCUN:
			return getTextureFace(face).normal;
		case X:
			switch (face) {
			case UP:
				return getTextureFace(Face.WEST).right;
			case DOWN:
				return getTextureFace(Face.EAST).right;
			case NORTH:
				return getTextureFace(Face.NORTH).left;
			case SOUTH:
				return getTextureFace(Face.SOUTH).right;
			case EAST:
				return getTextureFace(Face.UP).left;
			case WEST:
				return getTextureFace(Face.DOWN).left;
			}

		case Y:
			return getTextureFace(face).normal;

		case Z:
		case LOOK_DOWN:
			switch (face) {
			case UP:
				return getTextureFace(Face.SOUTH).normal;
			case DOWN:
				return getTextureFace(Face.NORTH).reverse;
			case NORTH:
				return getTextureFace(Face.UP).reverse;
			case SOUTH:
				return getTextureFace(Face.DOWN).normal;
			case EAST:
				return getTextureFace(Face.EAST).right;
			case WEST:
				return getTextureFace(Face.WEST).left;
			}

			// =========================================================================================================================

		case LOOK_NORTH:
			return getTextureFace(face).normal;

		case LOOK_SOUTH:
			switch (face) {
			case UP:
				return getTextureFace(Face.UP).reverse;
			case DOWN:
				return getTextureFace(Face.DOWN).reverse;
			case NORTH:
				return getTextureFace(Face.SOUTH).normal;
			case SOUTH:
				return getTextureFace(Face.NORTH).normal;
			case EAST:
				return getTextureFace(Face.WEST).normal;
			case WEST:
				return getTextureFace(Face.EAST).normal;
			}

		case LOOK_EAST:
			switch (face) {
			case UP:
				return getTextureFace(Face.UP).right;
			case DOWN:
				return getTextureFace(Face.DOWN).left;
			case NORTH:
				return getTextureFace(Face.WEST).normal;
			case SOUTH:
				return getTextureFace(Face.EAST).normal;
			case EAST:
				return getTextureFace(Face.NORTH).normal;
			case WEST:
				return getTextureFace(Face.SOUTH).normal;
			}

		case LOOK_WEST:
			switch (face) {
			case UP:
				return getTextureFace(Face.UP).left;
			case DOWN:
				return getTextureFace(Face.DOWN).right;
			case NORTH:
				return getTextureFace(Face.EAST).normal;
			case SOUTH:
				return getTextureFace(Face.WEST).normal;
			case EAST:
				return getTextureFace(Face.SOUTH).normal;
			case WEST:
				return getTextureFace(Face.NORTH).normal;
			}

			// =========================================================================================================================

		case LOOK_UP:
			switch (face) {
			case UP:
				return getTextureFace(Face.NORTH).reverse;
			case DOWN:
				return getTextureFace(Face.SOUTH).normal;
			case NORTH:
				return getTextureFace(Face.DOWN).reverse;
			case SOUTH:
				return getTextureFace(Face.UP).normal;
			case EAST:
				return getTextureFace(Face.WEST).right;
			case WEST:
				return getTextureFace(Face.EAST).left;
			}
		}
		return null;
	}
}
