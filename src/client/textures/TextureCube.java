package client.textures;

import data.map.enumerations.Face;
import data.map.enumerations.Orientation;
import data.map.enumerations.Rotation;

public class TextureCube {

	/** Textures of the cube (up, down, north, south, east, west) */
	private TextureFace[] textures = new TextureFace[6];
	/** Textures of the cube with simulated rotation/orientation */
	private TextureFace[] preview = new TextureFace[6];

	// =========================================================================================================================

	public TextureCube(String folder, String id) {
		for (Face face : Face.faces)
			textures[face.ordinal()] = new TextureFace(folder, id, face);
	}

	public TextureCube(String id) {
		for (Face face : Face.faces)
			textures[face.ordinal()] = new TextureFace(id, face);
	}

	public TextureCube(int id) {
		this(id + "");
	}

	// =========================================================================================================================

	public TextureCube() {
		for (int i = 0; i < 6; i++)
			this.textures[i] = null;
	}

	public TextureCube(TextureFace[] textures) {
		for (int i = 0; i < 6; i++)
			this.textures[i] = textures[i];
	}

	public TextureCube(TextureFace up, TextureFace down, TextureFace north, TextureFace south, TextureFace east,
			TextureFace west) {
		this(new TextureFace[] { up, down, north, south, east, west });
	}

	public TextureCube(String up, String down, String north, String south, String east, String west) {
		this(new TextureFace(up), new TextureFace(down), new TextureFace(north), new TextureFace(south),
				new TextureFace(east), new TextureFace(west));
	}

	// =========================================================================================================================

	public TextureSquare getTexture(Face face, Rotation rota, Orientation ori) {
		for (int i = 0; i < 6; i++) {
			if (textures[i] != null)
				textures[i].rotation = 0;
			preview[i] = textures[i];
		}

		switch (rota) {
		case NONE:
			break;
		case BACK:
			rotateZ();
		case UPSIDE_DOWN_Z:
			rotateZ();
		case FRONT:
			rotateZ();
			break;
		case LEFT:
			rotateX();
		case UPSIDE_DOWN_X:
			rotateX();
		case RIGHT:
			rotateX();
			break;
		default:
			return null;
		}

		switch (ori) {
		case NORTH:
			break;
		case WEST:
			rotateY();
		case SOUTH:
			rotateY();
		case EAST:
			rotateY();
			break;
		default:
			return null;
		}

		if(preview[face.ordinal()] == null)
			return TextureSquare.defaultFace;
		
		return preview[face.ordinal()].getRotated();
	}

	// =========================================================================================================================

	public void rotateX() {
		preview[2].rotation += 3;
		preview[3].rotation++;

		TextureFace t = preview[0];

		preview[0] = preview[5];
		preview[5] = preview[1];
		preview[1] = preview[4];
		preview[4] = t;

		preview[0].rotation++;
		preview[4].rotation++;
		preview[1].rotation++;
		preview[5].rotation++;
	}

	public void rotateZ() {
		preview[4].rotation++;
		preview[5].rotation += 3;

		TextureFace t = preview[0];

		preview[0] = preview[3];
		preview[3] = preview[1];
		preview[1] = preview[2];
		preview[2] = t;

		preview[2].rotation += 2;
		preview[1].rotation += 2;
	}

	public void rotateY() {
		preview[0].rotation++;
		preview[1].rotation += 3;

		TextureFace t = preview[2];

		preview[2] = preview[5];
		preview[5] = preview[3];
		preview[3] = preview[4];
		preview[4] = t;
	}

	// =========================================================================================================================

	public void setFace(Face face, TextureSquare t) {
		TextureFace tf = new TextureFace();
		tf.setNormal(t);
		textures[face.ordinal()] = tf;
	}
}
