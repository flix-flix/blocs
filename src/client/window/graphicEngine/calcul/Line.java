package client.window.graphicEngine.calcul;

import java.awt.Point;
import java.util.ArrayList;

public class Line {

	// Coord of the line
	int x1, y1, x2, y2;

	// +1 or -1. Starting from the first point
	// X : + the line go right | - left
	// Y : + the line go down | - top
	private int signeDiffX, signeDiffY;

	// true : the line is more horizontal than vertical | false : the opposite
	private boolean hori;
	// the ratio of size
	private double plus;
	// if(hori) : size of the line on the X axe | else : on the Y axe
	private int diffMax;

	private static ArrayList<Point> list = new ArrayList<>();

	// =========================================================================================================================

	public Line(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;

		double diffX = x2 - x1;
		double diffY = y2 - y1;

		signeDiffX = diffX < 0 ? -1 : 1;
		signeDiffY = diffY < 0 ? -1 : 1;

		double diffXAbs = Math.abs(diffX);
		double diffYAbs = Math.abs(diffY);

		hori = diffXAbs >= diffYAbs;

		if (hori)
			plus = diffY / diffX;
		else
			plus = diffX / diffY;

		diffMax = (int) Math.max(diffXAbs, diffYAbs);
	}

	public Line(Point p1, Point p2) {
		this(p1.x, p1.y, p2.x, p2.y);
	}

	// =========================================================================================================================

	public ArrayList<Point> getPoints() {
		list.clear();

		for (int i = 0; i <= diffMax; i++) {
			int col = hori ? x1 + i * signeDiffX : (int) (x1 + Math.round(i * plus * signeDiffY));
			int row = hori ? (int) (y1 + Math.round(i * plus * signeDiffX)) : y1 + i * signeDiffY;

			list.add(new Point(col, row));
		}
		return list;
	}
}
