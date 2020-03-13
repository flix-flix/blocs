package graphicEngine.calcul;

import utils.Utils;

public class Camera {

	// =============== Data ===============
	/** Position of the camera */
	public Point3D vue = new Point3D(0, 0, 0);
	/** Rotation of the camera on the horizontal axe */
	private double vx;
	/** Rotation of the camera on the vertical axe */
	private double vy;

	// =============== Rotation ===============
	private double rotateSpeed = .2;

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

	public void goTo(Camera camera) {
		vue = camera.vue;
		vx = camera.vx;
		vy = camera.vy;
	}

	// =========================================================================================================================
	// Rotate

	/**
	 * Set the camera orientation to put the rotation point in the middle of the
	 * screen
	 */
	public void look(Point3D rotationPoint) {
		setVx(Utils.toDegres * Math.atan((vue.x - rotationPoint.x) / -(vue.z - rotationPoint.z)) + 90
				+ (vue.z - rotationPoint.z >= 0 ? 180 : 0));
		setVy(Utils.toDegres
				* Math.atan(Math.hypot(vue.x - rotationPoint.x, vue.z - rotationPoint.z) / (vue.y - rotationPoint.y))
				- 90 + (vue.y - rotationPoint.y <= 0 ? 180 : 0));
	}

	/**
	 * Rotate the camera around the rotation point (keeping the same distance
	 * between them)
	 */
	public void rotate(Point3D rotationPoint, int x, int y) {
		rotate(rotationPoint, vue.dist(rotationPoint), x, y);
	}

	/**
	 * Changes the distance between the rotation point and the camera (keeping the
	 * same angles)
	 */
	public void moveLooking(Point3D rotationPoint, double move) {
		rotate(rotationPoint, vue.dist(rotationPoint) + move, 0, 0);
	}

	/**
	 * Rotate the camera around the rotation point (given the new distance and the
	 * moving angles)
	 */
	private void rotate(Point3D rotationPoint, double dist, int x, int y) {
		synchronized (vue) {
			double angleY = getVy() + y * -rotateSpeed;

			if (angleY >= 60)
				angleY = 59.9;
			else if (angleY <= -60)
				angleY = -59.9;

			vue.y = rotationPoint.y - Math.sin(Utils.toRadian * angleY) * dist;
			double distX = Math.cos(Utils.toRadian * angleY) * dist;

			double angleX = Utils.toRadian * (getVx() + x * rotateSpeed);

			vue.x = rotationPoint.x - distX * Math.cos(angleX);
			vue.z = rotationPoint.z - distX * Math.sin(angleX);

			look(rotationPoint);
		}
	}

	// =========================================================================================================================

	public double getVx() {
		return vx;
	}

	public double getVy() {
		return vy;
	}

	public void setVx(double vx) {
		this.vx = vx;
	}

	public void setVy(double vy) {
		this.vy = vy;
	}

	// =========================================================================================================================

	public Camera clone() {
		return new Camera(vue.clone(), vx, vy);
	}

	@Override
	public String toString() {
		return "Camera [vue=" + vue.toString() + ", vx=" + vx + ", vy=" + vy + "]";
	}
}
