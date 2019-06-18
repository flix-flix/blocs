package data.map;

import client.session.Session;
import client.window.graphicEngine.calcul.Point3D;
import data.enumeration.ItemID;
import data.enumeration.SensBloc;
import utils.FlixBlocksUtils;

public class Cube {

	public int x, y, z;
	public ItemID itemID;

	public Point3D center;

	// Another brick in the wall
	public boolean onGrid = false;

	// =========================================================================================================================

	// Size of the cube
	public double sizeX, sizeY, sizeZ;
	// Shift the point of rotation (pixel)
	public int shiftX, shiftY, shiftZ;
	// Rotation relative to the shifted center (degree)
	public double ax, ay;

	// =========================================================================================================================

	public SensBloc sens = SensBloc.AUCUN;

	// Step of the bloc's "mining state"
	public int miningState = FlixBlocksUtils.NO_MINING;

	// =========================================================================================================================

	public Cube(double x, double y, double z, int shiftX, int shiftY, int shiftZ, double ax, double ay, double sizeX,
			double sizeY, double sizeZ, ItemID itemID) {
		this.itemID = itemID;

		this.x = (int) x;
		this.y = (int) y;
		this.z = (int) z;

		this.center = new Point3D(x, y, z);

		this.ax = -ax;
		this.ay = ay;

		this.shiftX = shiftX;
		this.shiftY = shiftY;
		this.shiftZ = shiftZ;

		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
	}

	public Cube(int x, int y, int z, ItemID itemID) {
		this(x, y, z, 0, 0, 0, 0, 0, 1, 1, 1, itemID);
		onGrid = true;
	}

	// =========================================================================================================================

	public int getX() {
		return (int) center.x;
	}

	public int getY() {
		return (int) center.y;
	}

	public int getZ() {
		return (int) center.z;
	}

	// =========================================================================================================================

	public boolean hasAction() {
		return false;
	}

	public void doAction(Session session) {
		System.out.println("Do Action");
	}

	@Override
	public String toString() {
		return "Cube [x=" + x + ", y=" + y + ", z=" + z + ", itemID=" + itemID + "]";
	}
}
