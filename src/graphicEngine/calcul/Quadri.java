package graphicEngine.calcul;

import java.awt.Point;
import java.awt.Polygon;

public class Quadri {

	public Point[] points;
	public int color;
	public boolean fill = true;

	public Line[] lines = new Line[4];

	public final static int NOT_NUMBERED = -1;
	/** Order of drawing in the Draw */
	public int id = NOT_NUMBERED;

	/** If not null : the Quadri is this line */
	Line line;

	// =========================================================================================================================

	public Quadri(Point p0, Point p1, Point p2, Point p3, int color, boolean fill) {
		this.points = new Point[] { p0, p1, p2, p3 };
		this.color = color;
		this.fill = fill;

		lines[0] = new Line(points[0], points[1]);
		lines[1] = new Line(points[1], points[2]);
		lines[2] = new Line(points[3], points[2]);
		lines[3] = new Line(points[0], points[3]);
	}

	public Quadri(Point p0, Point p1, Point p2, Point p3, int color, boolean fill, int id) {
		this(p0, p1, p2, p3, color, fill);
		this.id = id;
	}

	public Quadri(Point p0, Point p1, int color) {
		line = new Line(p0, p1);
		this.color = color;
	}

	// =========================================================================================================================

	public int getLeft(int y) {
		int x = 10_000;

		for (Line l : lines)
			if (l.min <= y && y <= l.max)
				x = Math.min(x, l.getLeft(y));

		return x;
	}

	public int getRight(int y) {
		int x = -1;

		for (Line l : lines)
			if (l.min <= y && y <= l.max)
				x = Math.max(x, l.getRight(y));

		return x;
	}

	public int getTop() {
		int y = 10_000;

		for (Point p : points)
			y = Math.min(y, p.y);

		return y;
	}

	public int getBottom() {
		int y = -1;

		for (Point p : points)
			y = Math.max(y, p.y);

		return y;
	}

	// =========================================================================================================================

	public boolean isLine() {
		return line != null;
	}

	// =========================================================================================================================

	public boolean appearOn(int imgWidth, int imgHeight) {
		if (isLine())
			return line.appearIn(imgWidth, imgHeight);

		for (int i = 0; i < 4; i++)
			if (points[i].x < imgWidth && points[i].x > 0 && points[i].y < imgHeight && points[i].y > 0)
				return true;
		return false;
	}

	// =========================================================================================================================

	public Polygon getPoly() {
		return new Polygon(new int[] { points[0].x, points[1].x, points[2].x, points[3].x },
				new int[] { points[0].y, points[1].y, points[2].y, points[3].y }, 4);
	}
}
