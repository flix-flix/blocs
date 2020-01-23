package utils.yaml;

import client.textures.TextureMulti;

public class YAMLTextureMulti extends YAMLTextureCube {

	int sizeX, sizeY, sizeZ;

	TextureMulti texture;

	// =========================================================================================================================

	public YAMLTextureMulti(YAML yaml) {
		super(yaml);

		sizeX = yaml.getInt("cubes.x");
		sizeY = yaml.getInt("cubes.y");
		sizeZ = yaml.getInt("cubes.z");

		texture = new TextureMulti(ts, sizeX, sizeY, sizeZ);
	}

	// =========================================================================================================================

	public TextureMulti getTextureMulti() {
		return texture;
	}
}
