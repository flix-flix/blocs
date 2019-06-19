package data.multiblocs;

import java.util.LinkedList;

import data.map.Cube;

public class Multibloc {

	public LinkedList<Cube> list = new LinkedList<>();

	int x, y, z;

	public Multibloc(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Multibloc() {
		this(0, 0, 0);
	}

	// =========================================================================================================================

	public Cube getCube() {
		return list.getFirst();
	}

	public void add(Cube cube) {
		cube.multibloc = this;
		list.add(cube);
	}

	public void setCoords(int x, int y, int z) {
		for (Cube c : list)
			c.shiftCoords(x - this.x, y - this.y, z - this.z);

		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Multibloc getNew() {
		return new Multibloc();
	}
}
