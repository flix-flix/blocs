package utils;

import client.window.graphicEngine.calcul.Point3D;
import data.enumeration.Face;
import data.map.Cube;

public class Tuple {

	public int x, y, z;

	public Tuple(double x, double y, double z) {
		this.x = (int) x;
		this.y = (int) y;
		this.z = (int) z;
	}

	public Tuple(double x, double z) {
		this(x, 0, z);
	}

	public Tuple(Point3D point) {
		this(point.x, point.y, point.z);
	}

	public Tuple(Cube cube) {
		this(cube.x, cube.y, cube.z);
	}

	// =========================================================================================================================

	public Tuple face(Face face) {
		Tuple tuple = clone();
		switch (face) {
		case UP:
			tuple.y++;
			break;
		case DOWN:
			tuple.y--;
			break;
		case NORTH:
			tuple.x++;
			break;
		case SOUTH:
			tuple.x--;
			break;
		case EAST:
			tuple.z++;
			break;
		case WEST:
			tuple.z--;
			break;
		}
		return tuple;
	}

	// =========================================================================================================================

	public boolean equals(Cube cube) {
		return x == cube.x && y == cube.y && z == cube.z;
	}

	public boolean equals(Tuple tuple) {
		return x == tuple.x && y == tuple.y && z == tuple.z;
	}

	@Override
	public Tuple clone() {
		return new Tuple(x, y, z);
	}

	@Override
	public String toString() {
		return "Tuple [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}
