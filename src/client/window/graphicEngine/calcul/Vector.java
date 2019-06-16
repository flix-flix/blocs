package client.window.graphicEngine.calcul;

public class Vector {

	// Starting point
	Point3D start;
	// Shift for each axe
	double x, y, z;

	public Vector(Point3D start, Point3D end, int resolution) {
		this.start = start;

		x = (end.x - start.x) / resolution;
		y = (end.y - start.y) / resolution;
		z = (end.z - start.z) / resolution;
	}

	// =========================================================================================================================

	public Point3D multiply(double a) {
		return multiply(start, a);
	}

	public Point3D multiply(Point3D p, double a) {
		return new Point3D(p.x + x * a, p.y + y * a, p.z + z * a);
	}

	// =========================================================================================================================

	@Override
	public String toString() {
		return "Vectorr [ x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}
