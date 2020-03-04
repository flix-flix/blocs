package data.map.multiblocs;

import java.io.Serializable;
import java.util.LinkedList;

import data.id.ItemID;
import data.map.Coord;
import data.map.Cube;
import data.map.buildings.Building;
import data.map.units.Unit;

public class MultiBloc implements Serializable {
	private static final long serialVersionUID = 6445228941735562941L;

	private static int nextID = 0;

	public LinkedList<Cube> list = new LinkedList<>();

	protected int id;
	public int itemID = ItemID.ERROR;
	protected int x, y, z;

	public boolean valid = true;

	// =========================================================================================================================

	public MultiBloc(int itemID, int x, int y, int z) {
		id = nextID++;
		this.itemID = itemID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public MultiBloc(int itemID) {
		this(itemID, 0, 0, 0);
	}

	public MultiBloc() {
	}

	// =========================================================================================================================

	public boolean exist() {
		return !list.isEmpty();
	}

	public Cube getCube() {
		return list.getFirst();
	}

	public void add(Cube cube) {
		cube.multibloc = this;

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

	public MultiBloc clone() {
		MultiBloc m = new MultiBloc(itemID, x, y, z);
		nextID--;
		m.id = id;
		m.list.addAll(list);
		m.valid = valid;
		return m;
	}

	// =========================================================================================================================

	public int getId() {
		return id;
	}
}
