package graphicEngine.structures;

import java.awt.Polygon;
import java.util.ArrayList;

import graphicEngine.calcul.Engine;
import graphicEngine.calcul.Point3D;
import graphicEngine.calcul.Quadri;

public interface Drawable extends Comparable<Drawable> {

	/** Returns the list of quadri corresponding to this Draw */
	public ArrayList<Quadri> getQuadri(Engine engine);

	/** Returns the on-screen polygon representative of this draw */
	public Polygon getPoly(Engine engine);

	/** Returns true if at least one point appear on the screen */
	public boolean appearIn(Engine engine, int imgWidth, int imgHeight);

	/** Returns true if the Draw can be targeted */
	public boolean isTargetable();

	// =========================================================================================================================

	@Override
	public default int compareTo(Drawable d) {
		if (getCenter().distToOrigin() != d.getCenter().distToOrigin())
			return getCenter().distToOrigin() > d.getCenter().distToOrigin() ? 1 : -1;
		else if (getIndex() != d.getIndex())
			return getIndex() - d.getIndex();
		return 0;
	}

	// =========================================================================================================================

	/** Center of the Draw (used to calculate the distance to the camera) */
	public Point3D getCenter();

	/** Used to sort the Draws that are at the same distance from the camera */
	public int getIndex();
}
