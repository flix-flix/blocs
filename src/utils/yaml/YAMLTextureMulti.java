package utils.yaml;

import environment.textures.TextureMulti;
import graphicEngine.calcul.Camera;

public class YAMLTextureMulti extends YAMLTextureCube {

	int sizeX, sizeY, sizeZ;

	Camera camera;

	TextureMulti texture;

	// =========================================================================================================================

	public YAMLTextureMulti(YAML yaml) {
		super(yaml);

		texture = new TextureMulti(ts, id);
	}

	// =========================================================================================================================

	public TextureMulti getTextureMulti() {
		return texture;
	}
}
