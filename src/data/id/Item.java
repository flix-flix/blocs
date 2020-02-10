package data.id;

import java.util.TreeMap;

import data.map.resources.ResourceType;
import graphicEngine.calcul.Camera;
import graphicEngine.calcul.Point3D;
import utils.yaml.YAML;

public class Item {

	// ==================== YAML ====================
	TreeMap<String, Object> tree;

	// ==================== ID ====================
	public int id;
	public String tag;

	// ==================== Properties ====================
	boolean opaque = true;

	// ==================== Multibloc ====================
	boolean multibloc = false;
	int sizeX, sizeY, sizeZ;
	String type;

	// ==================== Resource ====================
	int miningTime = -1;
	int miningSteps = 5;
	ResourceType resourceType = null;

	// ==================== Building ====================
	int buildingTime = -1;

	// ==================== Texture ====================
	Camera camera = new Camera(new Point3D(-.4, 1.5, -1), 58, -35);
	boolean contour = true;
	int mapColor = 0xeaff00;

	// ==================== Language ====================
	public String name;

	// =========================================================================================================================

	public Item(int id, String tag) {
		this.id = id;
		this.tag = tag;
	}

	public Item(YAML yaml) {
		id = yaml.getInt("id");
		tag = yaml.getString("tag");

		if (yaml.contains("opaque"))
			opaque = yaml.getBoolean("opaque");

		if (yaml.contains("type"))
			type = yaml.getString("type");

		if (multibloc = yaml.contains("multibloc")) {
			sizeX = yaml.getInt("multibloc.size.x");
			sizeY = yaml.getInt("multibloc.size.y");
			sizeZ = yaml.getInt("multibloc.size.z");
		}

		if (yaml.contains("mining"))
			miningTime = yaml.getInt("mining.time");

		if (yaml.contains("building"))
			buildingTime = yaml.getInt("building.time");

		if (yaml.contains("resource"))
			resourceType = Enum.valueOf(ResourceType.class, yaml.getString("resource.type"));
	}

	// =========================================================================================================================

	public void setLanguage(YAML yaml) {
		if (yaml.contains("items." + tag.toLowerCase()))
			name = yaml.getString("items." + tag.toLowerCase());
	}

	// =========================================================================================================================

	public void setTexture(YAML yaml) {
		if (yaml.contains("camera")) {
			double x = yaml.getDouble("camera.x");
			double y = yaml.getDouble("camera.y");
			double z = yaml.getDouble("camera.z");
			int vx = yaml.getInt("camera.vx");
			int vy = yaml.getInt("camera.vy");

			camera = new Camera(new Point3D(x, y, z), vx, vy);
		}

		if (yaml.contains("contour"))
			contour = yaml.getBoolean("contour");
		if (yaml.contains("minimap"))
			mapColor = yaml.getHexa("minimap.color");
	}
}
