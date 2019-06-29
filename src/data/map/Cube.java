package data.map;

import client.session.Session;
import client.window.graphicEngine.calcul.Point3D;
import data.enumeration.ItemID;
import data.enumeration.Orientation;
import data.enumeration.Rotation;
import data.multiblocs.Multibloc;
import data.units.Unit;
import utils.FlixBlocksUtils;
import utils.Tuple;

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
	public double rotaX, rotaY, rotaZ;

	// =========================================================================================================================

	public Multibloc multibloc;

	public Unit unit;

	// =========================================================================================================================

	public Orientation orientation = Orientation.NORTH;
	public Rotation rotation = Rotation.NONE;

	// Step of the bloc's "mining state"
	public int miningState = FlixBlocksUtils.NO_MINING;

	// =========================================================================================================================

	public Cube(double x, double y, double z, int shiftX, int shiftY, int shiftZ, double rotaX, double rotaY,
			double rotaZ, double sizeX, double sizeY, double sizeZ, ItemID itemID) {
		this.itemID = itemID;

		this.x = (int) x;
		this.y = (int) y;
		this.z = (int) z;

		this.center = new Point3D(x, y, z);

		this.rotaX = rotaX;
		this.rotaY = rotaY;
		this.rotaZ = rotaZ;

		this.shiftX = shiftX;
		this.shiftY = shiftY;
		this.shiftZ = shiftZ;

		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
	}

	public Cube(int x, int y, int z, ItemID itemID) {
		this(x, y, z, 0, 0, 0, 0, 0, 0, 1, 1, 1, itemID);
		onGrid = true;
	}

	public Cube(Tuple tuple, ItemID itemID) {
		this(tuple.x, tuple.y, tuple.z, itemID);
	}

	public Cube(ItemID itemID) {
		this(0, 0, 0, itemID);
	}

	public Cube(Unit unit) {
		this(unit.coord, ItemID.UNIT);
		this.unit = unit;
	}

	// =========================================================================================================================
	// Coords

	public Tuple coords() {
		return new Tuple(this);
	}

	public void setCoords(Tuple tuple) {
		setCoords(tuple.x, tuple.y, tuple.z);
	}

	public void setCoords(int x, int y, int z) {
		if (multibloc == null) {
			this.x = x;
			this.y = y;
			this.z = z;

			center = new Point3D(x, y, z);
		} else
			multibloc.setCoords(x, y, z);
	}

	public void shiftCoords(int x, int y, int z) {
		this.x += x;
		this.y += y;
		this.z += z;

		center = new Point3D(this.x, this.y, this.z);
	}

	// =========================================================================================================================

	public boolean hasAction() {
		return false;
	}

	public void doAction(Session session) {
		System.out.println("Do Action");
	}

	// =========================================================================================================================

	@Override
	public String toString() {
		return "Cube [x=" + x + ", y=" + y + ", z=" + z + ", itemID=" + itemID + "]";
	}
}
