package client.window.graphicEngine.structures;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.calcul.Matrix;
import client.window.graphicEngine.calcul.Point3D;

public abstract class Draw implements Comparable<Draw> {

	public Engine engine;

	// =================== Data ==================
	// List of available textures
	protected HashMap<Integer, int[]> textures = new HashMap<>();
	// List of the Quadri which will be drawn
	protected static ArrayList<Quadri> quadri = new ArrayList<Quadri>();

	// =================== State ==================
	// false : this model won't be drawn
	protected boolean visible = true;
	// Distance to the camera (Used to choose the order of the Models)
	public double dist;

	// =================== Infos ==================
	// Center of the Draw (used to calculate the distance from the camera)
	public Point3D center;
	// Used to sort the Draws that are at the same distance from the camera
	// (multiblock structures)
	public int index = 0;

	// =========================================================================================================================

	public void addTexture(int tag, int[] color) {
		textures.put(tag, color);
	}

	// =========================================================================================================================

	/**
	 * Init the Model and sub-models with a new Camera
	 * 
	 * @param camera
	 */
	public abstract void init(Point3D camera, Matrix matrice);

	/**
	 * Returns the list of quadri corresponding to the asked Model
	 * 
	 * /!\ Must be call after init
	 * 
	 * @param index
	 * @return
	 */
	public abstract ArrayList<Quadri> getQuadri(Point3D camera, Matrix matrice);

	// =========================================================================================================================

	@Override
	public int compareTo(Draw m) {
		if (dist > m.dist)
			return 1;
		else if (dist < m.dist)
			return -1;
		else {
			if (center.x != m.center.x)
				return center.x > m.center.x ? 1 : -1;
			else if (center.y != m.center.y)
				return center.y > m.center.y ? 1 : -1;
			else if (center.z != m.center.z)
				return center.z > m.center.z ? 1 : -1;
			else if (index != m.index)
				return index - m.index;
			return 0;
		}
	}

	// =========================================================================================================================

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	// =========================================================================================================================

	public Point to2D(Point3D p) {
		if (p.x <= 0)
			return new Point(0, 0);

		double xx = p.z / (engine.vue[0] * p.x);
		double yy = p.y / (engine.vue[1] * p.x);

		int x = engine.centerX + (int) (xx * engine.screenWidth);
		int y = engine.centerY + (int) (-yy * engine.screenHeight);

		return new Point(x, y);
	}
}
