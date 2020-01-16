package client.textures;

import java.util.HashMap;

import client.window.graphicEngine.extended.ModelCube;
import data.id.ItemID;
import data.id.ItemTable;
import data.map.enumerations.Face;
import data.map.enumerations.Orientation;
import data.map.enumerations.Rotation;

public class TexturePack {

	/** Number of mining animation frames to load */
	public static final int nbAnim = 5;
	/** ID of textures blocks to load */
	public static final int[] texturesToLoad = new int[] { 0, 1, 2, 3, 20, 21, 50, 51, 52, 100, 200, 997, 998, 999 };

	/** ID of textures multi-blocks to load */
	public static final ItemID[] texturesMultiToLoad = new ItemID[] { ItemID.CASTLE };

	/** Map to store the textures of the cubes (access by id) */
	HashMap<Integer, TextureCube> texturesCubes = new HashMap<>();
	/** Map to store the textures of the cubes (access by id) */
	HashMap<Integer, TextureMulti> texturesMulti = new HashMap<>();

	/** Array to store mining animations (intact to broken) */
	TextureSquare[] miningFrames = new TextureSquare[nbAnim];

	/** Default missing texture */
	TextureSquare faceError = TextureSquare.generateSquare("blocs", "999");

	public TexturePack() {
		texturesCubes.put(996,
				new TextureCube(new TextureFace("multi", "test-up"), new TextureFace("multi", "test-down"),
						new TextureFace("multi", "test-north"), new TextureFace("multi", "test-south"),
						new TextureFace("multi", "test-east"), new TextureFace("multi", "test-west")));

		for (int i : texturesToLoad)
			texturesCubes.put(i, new TextureCube(i));

		for (ItemID id : texturesMultiToLoad)
			texturesMulti.put(id.id, new TextureMulti(id));

		for (int i = 0; i < nbAnim; i++)
			miningFrames[i] = TextureSquare.generateSquare("anim", "mining-" + i);
	}

	// =========================================================================================================================

	public TextureSquare getFace(ModelCube cube, Face face) {
		if (ItemTable.isMultiBloc(cube.itemID))
			return texturesMulti.get(cube.itemID.id).getFace(cube, face);
		else
			return getFace(cube.itemID.id, face, cube.rotation, cube.orientation);
	}

	public TextureSquare getFace(int id, Face face) {
		return getFace(id, face, Rotation.NONE, Orientation.NORTH);
	}

	public TextureSquare getFace(int id, Face face, Rotation rota, Orientation ori) {
		if (!texturesCubes.containsKey(id))
			return faceError;

		return texturesCubes.get(id).getTexture(face, rota, ori);
	}

	// =========================================================================================================================

	public TextureSquare getMiningFrame(int step) {
		return miningFrames[Math.min(step, nbAnim - 1)];
	}

	// =========================================================================================================================
	// Editor

	public void addTextureCube(TextureCube t, int id) {
		texturesCubes.put(id, t);
	}

	public boolean isIDAvailable(int id) {
		return !texturesCubes.containsKey(id);
	}
}
