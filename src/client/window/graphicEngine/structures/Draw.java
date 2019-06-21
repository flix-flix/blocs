package client.window.graphicEngine.structures;

import java.util.ArrayList;

import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.calcul.Point3D;

public abstract class Draw implements Comparable<Draw> {

	public Engine engine;

	// List of the Quadri which will be drawn
	protected static ArrayList<Quadri> quadri = new ArrayList<Quadri>();

	// false : this model won't be drawn
	protected boolean visible = true;

	// Center of the Draw (used to calculate the distance from the camera)
	public Point3D center;
	// Used to sort the Draws that are at the same distance from the camera
	// (multiblock structures)
	public int index = 0;

	// =========================================================================================================================

	/** Returns the list of quadri corresponding to this Draw */
	public abstract ArrayList<Quadri> getQuadri();

	// =========================================================================================================================

	@Override
	public int compareTo(Draw d) {
		if (center.distToOrigin() != d.center.distToOrigin())
			return center.distToOrigin() > d.center.distToOrigin() ? 1 : -1;
		else if (index != d.index)
			return index - d.index;
		return 0;
	}

	// =========================================================================================================================

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
