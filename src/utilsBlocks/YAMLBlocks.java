package utilsBlocks;

import graphicEngine.calcul.Camera;
import graphicEngine.calcul.Point3D;
import utils.yaml.YAML;

/** List of static methods to cast "Blocks objects" to YAML and vice-versa */
public class YAMLBlocks {

	// =========================================================================================================================
	// Camera

	public static YAML toYAML(Camera camera) {
		YAML yaml = new YAML();

		yaml.put("x", camera.vue.x);
		yaml.put("y", camera.vue.y);
		yaml.put("z", camera.vue.z);

		yaml.put("vx", camera.getVx());
		yaml.put("vy", camera.getVy());

		return yaml;
	}

	public static Camera getCamera(YAML yaml) {
		double x = yaml.getDouble("x");
		double y = yaml.getDouble("y");
		double z = yaml.getDouble("z");
		double vx = yaml.getDouble("vx");
		double vy = yaml.getDouble("vy");

		return new Camera(new Point3D(x, y, z), vx, vy);
	}

	// =========================================================================================================================

}
