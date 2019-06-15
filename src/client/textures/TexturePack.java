package client.textures;

import data.enumeration.Face;
import data.enumeration.ItemID;
import data.enumeration.SensBloc;

public class TexturePack {

	// Number of mining animation frames to load
	public static final int nbAnim = 5;
	// ID of textures blocks to load
	public static final int[] texturesToLoad = new int[] { 0, 1, 2, 3, 20, 21, 50, 51, 52 };

	// Array to store the textures of the cubes (sorted by id)
	TextureCube[] texturesCubes = new TextureCube[200];
	// Array to store mining animations (intact to broken)
	TextureSquare[] miningFrames = new TextureSquare[nbAnim];

	// Default missing texture
	TextureSquare faceError = TextureSquare.generateSquare("blocs", "999");

	public TexturePack() {
		texturesCubes[199] = new TextureCube("UP", "DOWN", "NORTH", "SOUTH", "EAST", "WEST");

		for (int i : texturesToLoad)
			if (texturesCubes[i] == null)
				texturesCubes[i] = new TextureCube(i);

		for (int i = 0; i < nbAnim; i++)
			miningFrames[i] = TextureSquare.generateSquare("anim", "mining-" + i);
	}

	// =========================================================================================================================

	public TextureSquare getFace(ItemID itemID, Face face) {
		return getFace(itemID, face, SensBloc.AUCUN);
	}

	public TextureSquare getFace(ItemID itemID, Face face, SensBloc sens) {
		if (itemID.id < 0 || itemID.id > texturesCubes.length || texturesCubes[itemID.id] == null)
			return faceError;

		return texturesCubes[itemID.id].getTexture(face, sens);
	}

	// =========================================================================================================================

	public TextureSquare getFace(int id, Face face) {
		if (id < 0 || texturesCubes[id] == null)
			return faceError;
		return texturesCubes[id].getTexture(face);
	}

	public TextureSquare getMiningFrame(int step) {
		return miningFrames[Math.min(step, nbAnim - 1)];
	}
}
