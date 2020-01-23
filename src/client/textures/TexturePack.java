package client.textures;

import java.util.HashMap;

import client.window.graphicEngine.extended.ModelCube;
import data.id.ItemID;
import data.id.ItemTable;
import data.map.enumerations.Face;
import data.map.enumerations.Orientation;
import data.map.enumerations.Rotation;
import utils.FlixBlocksUtils;
import utils.yaml.YAML;
import utils.yaml.YAMLTextureCube;
import utils.yaml.YAMLTextureMulti;

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
	TextureSquare faceError = TextureSquare.generateSquare("999");

	public TexturePack() {
		for (String file : FlixBlocksUtils.getFilesName("resources/cubes")) {
			YAMLTextureCube texture = new YAMLTextureCube(YAML.parseFile(file));
			texturesCubes.put(texture.id, texture.getTextureCube());
		}

		for (String file : FlixBlocksUtils.getFilesName("resources/multi")) {
			YAMLTextureMulti texture = new YAMLTextureMulti(YAML.parseFile(file));
			texturesMulti.put(texture.id, texture.getTextureMulti());
		}

		for (int i = 0; i < nbAnim; i++)
			miningFrames[i] = TextureSquare.generateSquare("anim/mining-" + i);
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
