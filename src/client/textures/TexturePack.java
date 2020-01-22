package client.textures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import client.window.graphicEngine.extended.ModelCube;
import data.id.ItemID;
import data.id.ItemTable;
import data.map.enumerations.Face;
import data.map.enumerations.Orientation;
import data.map.enumerations.Rotation;
import utils.FlixBlocksUtils;
import utils.yaml.YAML;
import utils.yaml.YAMLTextureFace;

public class TexturePack {

	/** Number of mining animation frames to load */
	public static final int nbAnim = 5;

	/** ID of textures multi-blocks to load */
	public static final int[] texturesMultiToLoad = new int[] { ItemID.CASTLE };

	/** Map to store the textures of the cubes (access by id) */
	HashMap<Integer, TextureCube> texturesCubes = new HashMap<>();
	/** Map to store the textures of the cubes (access by id) */
	HashMap<Integer, TextureMulti> texturesMulti = new HashMap<>();

	/** Array to store mining animations (intact to broken) */
	TextureSquare[] miningFrames = new TextureSquare[nbAnim];

	/** Default missing texture */
	TextureSquare faceError = TextureSquare.generateSquare("cubes", "999");

	public TexturePack() {
		texturesCubes.put(996,
				new TextureCube(new TextureFace("multi", "test-up"), new TextureFace("multi", "test-down"),
						new TextureFace("multi", "test-north"), new TextureFace("multi", "test-south"),
						new TextureFace("multi", "test-east"), new TextureFace("multi", "test-west")));

		ArrayList<String> list = FlixBlocksUtils.getFilesName("resources/cubes");

		for (String file : list) {
			if (file.indexOf(".yml") == -1)
				continue;

			TreeMap<String, Object> tree = YAML.parseFile(file);
			YAMLTextureFace texture = new YAMLTextureFace(tree);

			texturesCubes.put(texture.id, texture.getTextureCube());
		}

		for (int id : texturesMultiToLoad)
			texturesMulti.put(id, new TextureMulti(id));

		for (int i = 0; i < nbAnim; i++)
			miningFrames[i] = TextureSquare.generateSquare("anim", "mining-" + i);
	}

	// =========================================================================================================================

	public TextureSquare getFace(ModelCube cube, Face face) {
		if (ItemTable.isMultiBloc(cube.itemID))
			return texturesMulti.get(cube.itemID).getFace(cube, face);
		else
			return getFace(cube.itemID, face, cube.rotation, cube.orientation);
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

	public void setTextureCube(TextureCube t, int id) {
		texturesCubes.put(id, t);
	}

	public boolean isIDAvailable(int id) {
		return !texturesCubes.containsKey(id);
	}
}
