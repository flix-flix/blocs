package graphicEngine.calcul;

public class Camera {

	/** Position of the camera */
	public Point3D vue = new Point3D(0, 0, 0);
	/** Rotation of the camera on the horizontal axe */
	private double vx;
	/** Rotation of the camera on the vertical axe */
	private double vy;

	// =========================================================================================================================

	public Camera(Point3D vue, double vx, double vy) {
		this.vue = vue;
		this.vx = vx;
		this.vy = vy;
	}

	public Camera(Point3D vue) {
		this(vue, 0, 0);
	}

	// =========================================================================================================================

	public void moveY(double y) {
		vue.y += y;
	}

	public void move(double x, double z) {
		vue.x += x;
		vue.z += z;
	}

	// =========================================================================================================================

	public double getVx() {
		return vx;
	}

	public void setVx(double vx) {
		this.vx = vx;
	}

	public double getVy() {
		return vy;
	}

	public void setVy(double vy) {
		this.vy = vy;
	}

	// =========================================================================================================================

	@Override
	public String toString() {
		return "Camera [vue=" + vue.toString() + ", vx=" + vx + ", vy=" + vy + "]";
	}
}
