package client.window.graphicEngine.calcul;

import java.io.Serializable;

public class Point3D implements Serializable {
	private static final long serialVersionUID = -2718031765590936269L;

	public double x;
	public double y;
	public double z;

	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// =========================================================================================================================

	public double distToOrigin() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}

	public double dist(Point3D p) {
		return dist(p.x, p.y, p.z);
	}

	public double dist(double x, double y, double z) {
		return Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2) + Math.pow(this.z - z, 2));
	}

	// =========================================================================================================================

	@Override
	public Point3D clone() {
		return new Point3D(x, y, z);
	}

	@Override
	public String toString() {
		return "Point3D [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}
