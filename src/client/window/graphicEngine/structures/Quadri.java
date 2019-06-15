package client.window.graphicEngine.structures;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import client.window.graphicEngine.calcul.Line;
import client.window.graphicEngine.calcul.StatePixel;

public class Quadri {

	public Point[] points;
	public int color, alpha;
	public StatePixel statePixel;
	public boolean fill = true;

	private static Comparator<Point> comp = new CompPoint();

	private static ArrayList<Point> list = new ArrayList<>();

	public Quadri(Point[] points, int color, StatePixel etat) {
		this.points = points;
		this.color = color;
		this.statePixel = etat;
	}

	public Quadri(Point p0, Point p1, Point p2, Point p3, int color, StatePixel etat) {
		this(new Point[] { p0, p1, p2, p3 }, color, etat);
	}

	public Quadri(Point p0, Point p1, Point p2, Point p3, int color, StatePixel etat, int alpha) {
		this(new Point[] { p0, p1, p2, p3 }, color, etat);
		this.alpha = alpha;
	}

	public Quadri(Point p0, Point p1, Point p2, Point p3, int color, StatePixel etat, boolean fill) {
		this(new Point[] { p0, p1, p2, p3 }, color, etat);
		this.fill = fill;
	}

	// =========================================================================================================================

	public ArrayList<Point> getList() {
		Line l1 = new Line(points[0], points[1]);
		Line l2 = new Line(points[1], points[2]);
		Line l3 = new Line(points[3], points[2]);
		Line l4 = new Line(points[0], points[3]);

		list.clear();
		list.addAll(l1.getPoints());
		list.addAll(l2.getPoints());
		list.addAll(l3.getPoints());
		list.addAll(l4.getPoints());

		list.sort(comp);

		return list;
	}

	// =========================================================================================================================

	public static class CompPoint implements Comparator<Point> {

		@Override
		public int compare(Point p1, Point p2) {
			if (p1.y != p2.y)
				return p1.y - p2.y;
			if (p1.x != p2.x)
				return p1.x - p2.x;
			return 0;
		}
	}

	// =========================================================================================================================

	@Override
	public String toString() {
		return "Quadri [points=" + Arrays.toString(points) + ", color=" + color + "]";
	}

}
