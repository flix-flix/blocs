package utils.yaml;

import java.util.TreeMap;

import client.textures.TextureMulti;

public class YAMLTextureMulti extends YAMLTextureFace {

	int sizeX, sizeY, sizeZ;

	TextureMulti texture;

	// =========================================================================================================================

	public YAMLTextureMulti(TreeMap<String, Object> tree) {
		super(tree);

		TreeMap<String, String> cubes = (TreeMap<String, String>) tree.get("cubes");

		sizeX = Integer.valueOf(cubes.get("x"));
		sizeY = Integer.valueOf(cubes.get("y"));
		sizeZ = Integer.valueOf(cubes.get("z"));

		texture = new TextureMulti(ts, sizeX, sizeY, sizeZ);

	}

	// =========================================================================================================================

	public TextureMulti getTextureMulti() {
		return texture;
	}
}
