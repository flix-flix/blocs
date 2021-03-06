package graphicEngine.calcul;

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
	 * Return the leftmost pixel of the indicated Y
	 * 
	 * @param y
	 *            - must be between the min and max of the line
	 */
	public int getLeft(int y) {
		return p1.x + (int) Math.round((y - p1.y) * slope);
	}

	/**
	 * Return the rightmost pixel of the indicated Y
	 * 
	 * @param y
	 *            - must be between the min and max of the line
	 */
	public int getRight(int y) {
		if (y == p2.y)
			return p2.x;

		int prevLeft = getLeft(y - variation);

		if (prevLeft == getLeft(y))
			return prevLeft;
		return prevLeft - 1;
	}

	// =========================================================================================================================

	public boolean appearIn(int imgWidth, int imgHeight) {
		return pointIn(p1, imgWidth, imgHeight) || pointIn(p2, imgWidth, imgHeight);
	}

	public boolean pointIn(Point p, int imgWidth, int imgHeight) {
		return 0 < p.x && p.x < imgWidth && 0 < p.y && p.y < imgHeight;
	}
}
