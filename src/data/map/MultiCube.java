package data.map;

import java.io.Serializable;
import java.util.LinkedList;

import data.id.ItemID;
import data.map.buildings.Building;
import data.map.units.Unit;

public class MultiCube implements Serializable {
	private static final long serialVersionUID = 6445228941735562941L;

	private static int nextID = 0;

	public LinkedList<Cube> list = new LinkedList<>();

	protected int id;
	public int itemID = ItemID.ERROR;
	protected int x, y, z;

	public boolean valid = true;

	// =========================================================================================================================

	public MultiCube(int itemID, int x, int y, int z) {
		this();
		this.itemID = itemID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public MultiCube(int itemID) {
		this(itemID, 0, 0, 0);
	}

	public MultiCube() {
		// TODO [Fix] ID generation on clone()
		id = nextID++;
	}

	// =========================================================================================================================

	public boolean exist() {
		return !list.isEmpty();
	}

	public Cube getCube() {
		if (list.isEmpty())
			return null;
		return list.getFirst();
	}

	public void add(Cube cube) {
		cube.multicube = this;

		if (cube.onGrid) {
			cube.multiblocX = cube.gridCoord.x;
			cube.multiblocY = cube.gridCoord.y;
			cube.multiblocZ = cube.gridCoord.z;
		}
		list.add(cube);
	}

	public boolean contains(Coord coord) {
		for (Cube c : list)
			if (c.coords().equals(coord))
				return true;
		return false;
	}

	// =========================================================================================================================

	public void remove(Coord coord) {
		for (int i = 0; i < list.size(); i++)
			if (list.get(i).coords().equals(coord)) {
				list.remove(i);
				return;
			}
	}

	// =========================================================================================================================

	public void setCoords(int x, int y, int z) {
		for (Cube c : list)
			c.shiftCoords(x - this.x, y - this.y, z - this.z);

		this.x = x;
		this.y = y;
		this.z = z;
	}

	// =========================================================================================================================

	public void setBuild(Building build) {
		for (Cube c : list)
			c.build = build;
	}

	public void setUnit(Unit unit) {
		for (Cube c : list)
			c.unit = unit;
	}

	// =========================================================================================================================

	public int getMaxHeight() {
		int max = -1;

		for (Cube cube : list)
			if (max < cube.gridCoord.y)
				max = cube.gridCoord.y;

		return max;
	}

	// =========================================================================================================================

	public int getId() {
		return id;
	}
}
