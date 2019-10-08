package data.map.multiblocs;

import java.io.Serializable;
import java.util.LinkedList;

import data.map.Cube;

public class MultiBloc implements Serializable {
	private static final long serialVersionUID = 6445228941735562941L;

	private static int nextID = 0;

	public LinkedList<Cube> list = new LinkedList<>();

	protected int id;
	protected int x, y, z;

	public boolean valid = true;

	public MultiBloc(int x, int y, int z) {
		id = nextID++;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public MultiBloc() {
		this(0, 0, 0);
	}

	// =========================================================================================================================

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

	public void setCoords(int x, int y, int z) {
		for (Cube c : list)
			c.shiftCoords(x - this.x, y - this.y, z - this.z);

		this.x = x;
		this.y = y;
		this.z = z;
	}

	public MultiBloc clone() {
		MultiBloc m = new MultiBloc(x, y, z);
		m.list.addAll(list);
		return m;
	}

	// =========================================================================================================================

	public int getId() {
		return id;
	}
}
