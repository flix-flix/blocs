package client.textures;

import data.enumeration.Face;
import data.enumeration.Orientation;
import data.enumeration.Rotation;

public class TexturePack {

	// Number of mining animation frames to load
	public static final int nbAnim = 5;
	// ID of textures blocks to load
	public static final int[] texturesToLoad = new int[] { 0, 1, 2, 3, 20, 21, 50, 51, 52, 202 };

	// Array to store the textures of the cubes (sorted by id)
	TextureCube[] texturesCubes = new TextureCube[300];
	// Array to store mining animations (intact to broken)
	TextureSquare[] miningFrames = new TextureSquare[nbAnim];

	// Default missing texture
	TextureSquare faceError = TextureSquare.generateSquare("blocs", "999");

	public TexturePack() {
		texturesCubes[199] = new TextureCube("UP", "DOWN", "NORTH", "SOUTH", "EAST", "WEST");
		texturesCubes[201] = new TextureCube(new TextureFace("multi", "test-up"), new TextureFace("multi", "test-down"),
				new TextureFace("multi", "test-north"), new TextureFace("multi", "test-south"),
				new TextureFace("multi", "test-east"), new TextureFace("multi", "test-west"));

		for (int i : texturesToLoad)
			if (texturesCubes[i] == null)
				texturesCubes[i] = new TextureCube(i);

		for (int i = 0; i < nbAnim; i++)
			miningFrames[i] = TextureSquare.generateSquare("anim", "mining-" + i);
	}

	// =========================================================================================================================

	public TextureSquare getFace(int id, Face face) {
		return getFace(id, face, Rotation.NONE, Orientation.NORTH);
	}

	public TextureSquare getFace(int id, Face face, Rotation rota, Orientation ori) {
		if (id < 0 || id > texturesCubes.length || texturesCubes[id] == null)
			return faceError;

		return texturesCubes[id].getTexture(face, rota, ori);
	}

	// =========================================================================================================================

	public TextureSquare getMiningFrame(int step) {
		return miningFrames[Math.min(step, nbAnim - 1)];
	}
}
