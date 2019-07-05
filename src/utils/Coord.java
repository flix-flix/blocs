package utils;

import client.window.graphicEngine.calcul.Point3D;
import data.enumeration.Face;
import data.enumeration.Orientation;
import data.map.Cube;

public class Coord {

	public int x, y, z;

	public Coord(double x, double y, double z) {
		this.x = (int) x;
		this.y = (int) y;
		this.z = (int) z;
	}

	public Coord(double x, double z) {
		this(x, 0, z);
	}

	public Coord(Point3D point) {
		this(point.x, point.y, point.z);
	}

	public Coord(Cube cube) {
		this(cube.x, cube.y, cube.z);
	}

	// =========================================================================================================================

	/** Returns the Coords of the cube of the other side of the given face */
	public Coord face(Face face) {
		Coord tuple = clone();
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

	/**
	 * Returns the Orientation connecting the two Coords (from this to the given
	 * one)
	 */
	public Orientation getOrientation(Coord t) {
		if (t.y != y)
			return null;

		if (t.z == z) {
			if (t.x == x + 1)
				return Orientation.NORTH;
			if (t.x == x - 1)
				return Orientation.SOUTH;
		}

		if (t.x == x) {
			if (t.z == z + 1)
				return Orientation.EAST;
			if (t.z == z - 1)
				return Orientation.WEST;
		}

		return null;
	}

	// =========================================================================================================================

	/** Returns the index of the point connecting the two Coords */
	public int getRotationPoint(Coord t) {
		if (t.z == z - 1 && t.x == x - 1)
			return 0;
		if (t.z == z + 1 && t.x == x - 1)
			return 1;
		if (t.z == z + 1 && t.x == x + 1)
			return 2;
		if (t.z == z - 1 && t.x == x + 1)
			return 3;

		return -1;
	}

	// =========================================================================================================================

	public boolean equals(Coord tuple) {
		if (tuple == null)
			return false;
		return x == tuple.x && y == tuple.y && z == tuple.z;
	}

	@Override
	public Coord clone() {
		return new Coord(x, y, z);
	}

	@Override
	public String toString() {
		return "Tuple [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}