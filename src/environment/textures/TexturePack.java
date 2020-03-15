package environment.textures;

import java.awt.Image;
import java.util.TreeMap;

import data.id.ItemTable;
import data.id.ItemType;
import data.map.enumerations.Face;
import data.map.enumerations.Orientation;
import data.map.enumerations.Rotation;
import environment.extendsData.CubeClient;
import utils.Utils;
import utils.yaml.YAML;
import utils.yaml.YAMLTextureCube;
import utils.yaml.YAMLTextureMulti;

public class TexturePack {

	private String folder;

	/** Number of mining animation frames to load */
	public static final int nbAnim = 5;

	public TreeMap<Integer, YAML> yamls = new TreeMap<>();

	/** Map to store the textures of the cubes (access by id) */
	private TreeMap<Integer, TextureCube> texturesCubes = new TreeMap<>();
	/** Map to store the textures of the cubes (access by id) */
	private TreeMap<Integer, TextureMulti> texturesMulti = new TreeMap<>();

	/** Array to store mining animations (intact to broken) */
	private TextureSquare[] miningFrames = new TextureSquare[nbAnim];

	/** Default missing texture */
	private TextureSquare faceError = TextureSquare.generateSquare(Utils.imgError);

	public TexturePack(String folder) {
		this.folder = folder;

		for (String file : Utils.getFilesName("resources/texturesPacks/" + folder + "/cubes")) {
			YAML yaml = YAML.parseFile(file);
			yamls.put(yaml.getInt("id"), yaml);

			if (ItemTable.getType(yaml.getInt("id")) == ItemType.MULTIBLOC) {
				YAMLTextureMulti texture = new YAMLTextureMulti(yaml);
				texturesMulti.put(texture.id, texture.getTextureMulti());
			} else if (ItemTable.getType(yaml.getInt("id")) == ItemType.CUBE) {
				YAMLTextureCube texture = new YAMLTextureCube(yaml);
				texturesCubes.put(texture.id, texture.getTextureCube());
			}
		}

		for (int i = 0; i < nbAnim; i++) {
			Image img = Utils.getImage("texturesPacks/" + folder + "/anim/mining-" + i);
			miningFrames[i] = TextureSquare.generateSquare(img);
		}
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
		return "texturesPacks/" + folder + "/";
	}
}
