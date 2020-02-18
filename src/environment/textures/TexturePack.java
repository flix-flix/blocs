package environment.textures;

import java.awt.image.BufferedImage;
import java.util.TreeMap;

import data.id.ItemTable;
import data.map.enumerations.Face;
import data.map.enumerations.Orientation;
import data.map.enumerations.Rotation;
import environment.extendsData.CubeClient;
import utils.FlixBlocksUtils;
import utils.yaml.YAML;
import utils.yaml.YAMLTextureCube;
import utils.yaml.YAMLTextureMulti;

public class TexturePack {

	private String folder;

	/** Number of mining animation frames to load */
	public static final int nbAnim = 5;

	public TreeMap<Integer, YAML> yamls = new TreeMap<>();

	/** Map to store the textures of the cubes (access by id) */
	TreeMap<Integer, TextureCube> texturesCubes = new TreeMap<>();
	/** Map to store the textures of the cubes (access by id) */
	TreeMap<Integer, TextureMulti> texturesMulti = new TreeMap<>();

	/** Array to store mining animations (intact to broken) */
	TextureSquare[] miningFrames = new TextureSquare[nbAnim];

	/** Default missing texture */
	TextureSquare faceError = TextureSquare.generateSquare((BufferedImage) FlixBlocksUtils.imgError);

	public TexturePack(String folder) {
		this.folder = folder;

		for (String file : FlixBlocksUtils.getFilesName("resources/textures/" + folder + "/cubes")) {
			YAML yaml = YAML.parseFile(file);
			yamls.put(yaml.getInt("id"), yaml);
			YAMLTextureCube texture = new YAMLTextureCube(yaml);
			texturesCubes.put(texture.id, texture.getTextureCube());
		}

		for (String file : FlixBlocksUtils.getFilesName("resources/textures/" + folder + "/multi")) {
			YAML yaml = YAML.parseFile(file);
			yamls.put(yaml.getInt("id"), yaml);

			if (!ItemTable.getType(yaml.getInt("id")).equals("multibloc"))
				continue;

			YAMLTextureMulti texture = new YAMLTextureMulti(yaml);
			texturesMulti.put(texture.id, texture.getTextureMulti());
		}

		for (int i = 0; i < nbAnim; i++)
			miningFrames[i] = TextureSquare.generateSquare("textures/" + folder + "/anim/mining-" + i);
	}

	// =========================================================================================================================

	public TextureSquare getFace(CubeClient cube, Face face) {
		if (ItemTable.isMultiBloc(cube.getItemID()))
			return texturesMulti.get(cube.getItemID()).getFace(cube, face);
		else
			return getFace(cube.getItemID(), face, cube.rotation, cube.orientation);
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

	// =========================================================================================================================

	public String getFolder() {
		return "textures/" + folder + "/";
	}
}
