package graphicEngine.calcul;

public class Vector {

	/** Starting point */
	private Point3D start;
	/** Shift for each axe */
	private double x, y, z;

	// =========================================================================================================================

	public Vector(Point3D start, double x, double y, double z) {
		this.start = start;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector(Point3D start, Point3D end) {
		this(start, end.x - start.x, end.y - start.y, end.z - start.z);
	}

	public Vector(Point3D start, Point3D end, int resolution) {
		this(start, end.x - start.x, end.y - start.y, end.z - start.z);

		x /= resolution;
		y /= resolution;
		z /= resolution;
	}

	// =========================================================================================================================

	public Vector divise(int x) {
		Vector v = clone();

		v.x /= x;
		v.y /= x;
		v.z /= x;

		return v;
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
	public Vector clone() {
		return new Vector(start.clone(), x, y, z);
	}

	@Override
	public String toString() {
		return "Vector [ x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}
