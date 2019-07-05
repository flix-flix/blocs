package client.window.graphicEngine.structures;

import java.awt.Point;
import java.awt.Polygon;

import client.window.graphicEngine.calcul.Line;

public class Quadri {

	public Point[] points;
	public int color;
	public boolean fill = true;

	public Line[] lines = new Line[4];

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

	// =========================================================================================================================

	public int getLeft(int row) {
		int col = 10_000;

		for (Line l : lines)
			if (l.min <= row && row <= l.max)
				col = Math.min(col, l.getLeft(row));

		return col;
	}

	public int getRight(int row) {
		int col = -1;

		for (Line l : lines)
			if (l.min <= row && row <= l.max)
				col = Math.max(col, l.getRight(row));

		return col;
	}

	public int getTop() {
		int row = 10_000;

		for (Point p : points)
			row = Math.min(row, p.y);

		return row;
	}

	public int getBottom() {
		int row = -1;

		for (Point p : points)
			row = Math.max(row, p.y);

		return row;
	}

	// =========================================================================================================================

	public Polygon getPoly() {
		return new Polygon(new int[] { points[0].x, points[1].x, points[2].x, points[3].x },
				new int[] { points[0].y, points[1].y, points[2].y, points[3].y }, 4);
	}
}
