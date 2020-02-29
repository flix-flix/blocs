package data.map;

import java.io.Serializable;

import data.map.enumerations.Face;
import data.map.enumerations.Orientation;
import graphicEngine.calcul.Point3D;

public class Coord implements Serializable {
	private static final long serialVersionUID = 5676550708765407937L;

	public int x, y, z;

	// =========================================================================================================================

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
		Coord coord = clone();
		switch (face) {
		case UP:
			coord.y++;
			break;
		case DOWN:
			coord.y--;
			break;
		case NORTH:
			coord.x++;
			break;
		case SOUTH:
			coord.x--;
			break;
		case EAST:
			coord.z++;
			break;
		case WEST:
			coord.z--;
			break;
		}
		return coord;
	}

	// =========================================================================================================================

	/**
	 * Returns the Orientation connecting the two Coords (from this to the given
	 * one)
	 */
	public Orientation getConnection(Coord coord) {
		if (coord.y != y)
			return null;

		if (coord.z == z) {
			if (coord.x == x + 1)
				return Orientation.NORTH;
			if (coord.x == x - 1)
				return Orientation.SOUTH;
		}

		if (coord.x == x) {
			if (coord.z == z + 1)
				return Orientation.EAST;
			if (coord.z == z - 1)
				return Orientation.WEST;
		}

		return null;
	}

	// =========================================================================================================================

	/** Returns the index of the point connecting the two Coords */
	public int getRotationPointDiago(Coord coord) {
		int rota = -1;

		if (Math.abs(coord.y - y) <= 1) {
			if (coord.z == z - 1 && coord.x == x - 1)
				rota = 0;
			else if (coord.z == z + 1 && coord.x == x - 1)
				rota = 1;
			else if (coord.z == z + 1 && coord.x == x + 1)
				rota = 2;
			else if (coord.z == z - 1 && coord.x == x + 1)
				rota = 3;
		}

		if (rota == -1)
			return -1;

		if (y + 1 == coord.y)
			rota += 4;

		return rota;
	}

	// =========================================================================================================================

	public boolean equals(Coord coord) {
		if (coord == null)
			return false;
		return x == coord.x && y == coord.y && z == coord.z;
	}

	@Override
	public Coord clone() {
		return new Coord(x, y, z);
	}

	@Override
	public String toString() {
		return "Coord [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}
