package client.window.graphicEngine.calcul;

public class Camera {

	// ==== Camera infos ====

	// Position of the camera
	public Point3D vue = new Point3D(0, 0, 0);
	// Rotation of the camera (right - left)
	private double vx = 0;
	// Rotation of the camera (up - down)
	private double vy = 0;

	// =========================================================================================================================

	public Camera(Point3D vue) {
		this.vue = vue;
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
}
