package data.map;

import client.window.graphicEngine.calcul.Point3D;
import data.ItemTable;
import data.enumeration.ItemID;
import data.enumeration.Orientation;
import data.enumeration.Rotation;
import data.map.buildings.Building;
import data.map.resources.Resource;
import data.map.units.Unit;
import data.multiblocs.MultiBloc;

public class Cube {

	// =========================================================================================================================

	public double x, y, z;
	public Coord gridCoord;
	public ItemID itemID;

	public Point3D center;

	/** Another brick in the wall */
	public boolean onGrid = false;

	// =========================================================================================================================
	// Properties

	/** Size of the cube */
	public double sizeX, sizeY, sizeZ;
	/** Shift the point of rotation (coef multiplied by the size of the cube) */
	public double shiftX, shiftY, shiftZ;
	/** Rotation relative to the shifted center (degree) */
	public double rotaX, rotaY, rotaZ;

	public Orientation orientation = Orientation.NORTH;
	public Rotation rotation = Rotation.NONE;

	// =========================================================================================================================
	// Data

	public MultiBloc multibloc;
	/** Coords of the bloc in the multiblocs struct */
	public int multiblocX, multiblocY, multiblocZ;
	public Unit unit;
	public Building build;
	public Resource resource;

	// =========================================================================================================================
	// Mining

	public static final int NO_MINING = -1;
	public int minedAlready = 0;
	/** Step of the bloc's "mining state" */
	public int miningState = NO_MINING;

	// =========================================================================================================================

	public Cube(double x, double y, double z, double rotaX, double rotaY, double rotaZ, double sizeX, double sizeY,
			double sizeZ, ItemID itemID) {
		this.itemID = itemID;

		this.x = x;
		this.y = y;
		this.z = z;

		gridCoord = new Coord((int) x, (int) y, (int) z);

		this.center = new Point3D(x, y, z);

		this.rotaX = rotaX;
		this.rotaY = rotaY;
		this.rotaZ = rotaZ;

		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;

		if (ItemTable.isResource(itemID))
			resource = ItemTable.getResource(itemID);
	}

	public Cube(double x, double y, double z, double sizeX, double sizeY, double sizeZ, ItemID itemID) {
		this(x, y, z, 0, 0, 0, sizeX, sizeY, sizeZ, itemID);
	}

	public Cube(int x, int y, int z, ItemID itemID) {
		this(x, y, z, 1, 1, 1, itemID);
		onGrid = true;
	}

	public Cube(Coord tuple, ItemID itemID) {
		this(tuple.x, tuple.y, tuple.z, itemID);
	}

	public Cube(ItemID itemID) {
		this(0, 0, 0, itemID);
	}

	public Cube(Unit unit) {
		this(unit.coord, ItemID.UNIT);
		this.unit = unit;
		this.onGrid = false;
	}

	// =========================================================================================================================
	// Coords

	public Coord coords() {
		return new Coord(this);
	}

	public void setCoords(Coord tuple) {
		setCoords(tuple.x, tuple.y, tuple.z);
	}

	public void setCoords(int x, int y, int z) {
		if (multibloc == null) {
			this.x = x;
			this.y = y;
			this.z = z;

			gridCoord.x = x;
			gridCoord.y = y;
			gridCoord.z = z;

			center = new Point3D(x, y, z);
		} else
			multibloc.setCoords(x, y, z);
	}

	public void shiftCoords(int x, int y, int z) {
		this.x += x;
		this.y += y;
		this.z += z;

		gridCoord.x += x;
		gridCoord.y += y;
		gridCoord.z += z;

		center = new Point3D(gridCoord.x, gridCoord.y, gridCoord.z);
	}

	// =========================================================================================================================
	// Actions

	/**
	 * 
	 * @param x
	 *            - Number of mining steps to add
	 * @return true if the bloc broke
	 */
	public boolean addMined(int x) {
		if ((minedAlready += x) > ItemTable.getMiningTime(itemID))
			minedAlready = ItemTable.getMiningTime(itemID);

		miningState = (int) (minedAlready / (double) (ItemTable.getMiningTime(itemID))
				* ItemTable.getNumberOfMiningSteps());

		return minedAlready == ItemTable.getMiningTime(itemID);
	}

	// =========================================================================================================================
	// Resource

	public boolean hasResource() {
		if (resource != null)
			return true;
		if (multibloc != null)
			for (Cube c : multibloc.list)
				if (c.resource != null)
					return true;
		return false;
	}

	public Resource getResource() {
		Resource res;

		if (multibloc != null) {
			res = new Resource();
			for (Cube c : multibloc.list)
				if (c.resource != null)
					res.regroup(c.resource);
		} else
			res = resource;

		return res;
	}

	public boolean resourceIsEmpty() {
		return getResource().getQuantity() == 0;
	}

	public int resourceTake(int x) {
		if (multibloc != null)
			for (Cube c : multibloc.list)
				if (c.resource != null && !c.resource.isEmpty())
					return c.resource.remove(x);

		if (resource != null)
			return resource.remove(x);
		return -1;
	}

	// =========================================================================================================================

	@Override
	public String toString() {
		return "Cube [coord=" + gridCoord + ", itemID=" + itemID + "]";
	}
}
