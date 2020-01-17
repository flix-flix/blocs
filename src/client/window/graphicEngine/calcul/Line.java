package client.window.graphicEngine.calcul;

import java.awt.Point;

public class Line {

	/** The leftmost point */
	Point p1;
	/** The rightmost point */
	Point p2;
	public int min, max;

	/** Slope : p1 to p2 */
	double slope;
	/** +1 if y increase or -1 if y decrease */
	int variation;

	// =========================================================================================================================

	public Line(int x1, int y1, int x2, int y2) {
		min = Math.min(y1, y2);
		max = Math.max(y1, y2);

		if (x1 < x2) {
			p1 = new Point(x1, y1);
			p2 = new Point(x2, y2);
		} else {
			p1 = new Point(x2, y2);
			p2 = new Point(x1, y1);
		}

		slope = (p2.x - p1.x) / ((double) p2.y - p1.y);
		variation = p1.y < p2.y ? -1 : 1;
	}

	public Line(Point p1, Point p2) {
		this(p1.x, p1.y, p2.x, p2.y);
	}

	// =========================================================================================================================

	/**
	 * Return the leftmost pixel of the indicated row
	 * 
	 * @param row
	 *            - must be between the min and max of the line
	 */
	public int getLeft(int row) {
		return p1.x + (int) Math.round((row - p1.y) * slope);
	}

	/**
	 * Return the rightmost pixel of the indicated row
	 * 
	 * @param row
	 *            - must be between the min and max of the line
	 */
	public int getRight(int row) {
		if (row == p2.y)
			return p2.x;

		int prevLeft = getLeft(row - variation);

		if (prevLeft == getLeft(row))
			return prevLeft;
		return prevLeft - 1;
	}
}
