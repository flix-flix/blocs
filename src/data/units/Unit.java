package data.units;

import java.util.LinkedList;
import java.util.Queue;

import data.enumeration.Face;
import data.enumeration.Orientation;
import data.enumeration.Rotation;
import utils.Coord;

public class Unit {

	/** Path-finding : Value of the Start */
	private static final int START = 0;
	/** Path-finding : Value of an empty cube in the grid */
	private static final int EMPTY = -99;
	/** Path-finding : Value of a non-empty cube in the grid */
	// private static final int FILL = -999;

	/** Coords of the unit */
	public Coord coord;
	/** The unit orientation (the "north texture" looking to ...) */
	public Orientation orientation = Orientation.NORTH;
	/** The unit rotation (only on Rotation.axeX) */
	public Rotation rotation = Rotation.NONE;

	/** The current step of the rotation */
	private int movingStep;
	/** The number of steps to achieve the rotation */
	private final int movingStepFinal = 20;
	/** Orientation at the end of the rotation */
	private Orientation nextOrientation = orientation;
	/** Rotation at the end of the rotation */
	private Rotation nextRotation = rotation;

	/** Index of the cube's rotation point */
	public int rotationPoint = 0;
	/** For each axe : the rotation to perform to complete the rotation */
	private int movingAngleX = 0, movingAngleY = 0, movingAngleZ = 0;
	/** For each axe : the current rotation of the unit */
	public double ax = 0, ay = 0, az = 0;

	/** This unit is currently moving to this coord (null if not moving) */
	private Coord movingTo;

	/** Coord of the destination */
	private Coord destination;
	/** List of the coords to the destination */
	private LinkedList<Coord> path;

	// =========================================================================================================================

	public Unit(int x, int y, int z) {
		coord = new Coord(x, y, z);
	}

	// =========================================================================================================================

	/**
	 * Increase the rotation by one step and re-calculate the corresponding angles
	 */
	public void doStep() {
		if (movingTo == null)
			return;

		movingStep++;

		if (movingStep < movingStepFinal) {
			ax = (movingStep / (double) movingStepFinal) * movingAngleX;
			ay = -(movingStep / (double) movingStepFinal) * movingAngleY;
			az = (movingStep / (double) movingStepFinal) * movingAngleZ;
		} else {
			arrive();
		}
	}

	// =========================================================================================================================

	/** Update cube position after the rotation */
	public void arrive() {
		rotation = nextRotation;
		orientation = nextOrientation;

		movingStep = 0;
		ax = 0;
		ay = 0;
		az = 0;

		movingAngleX = 0;
		movingAngleY = 0;
		movingAngleZ = 0;

		coord = movingTo;
		movingTo = null;

		if (!path.isEmpty())
			doNextMove();
	}

	// =========================================================================================================================

	/** Initialize a rotation to an orthogonal position */
	public void setDirection(Orientation dir) {
		movingTo = coord.face(dir.face);

		nextRotation = orientation.next() == dir ? rotation.nextX() : rotation.previousX();

		switch (dir) {
		case NORTH:
			rotationPoint = 2;
			movingAngleZ = -90;
			break;
		case EAST:
			rotationPoint = 1;
			movingAngleX = 90;
			break;
		case SOUTH:
			rotationPoint = 0;
			movingAngleZ = 90;
			break;
		case WEST:
			rotationPoint = 0;
			movingAngleX = -90;
			break;
		default:
			break;
		}
	}

	/** Initialize a rotation to a diagonal position */
	public void setDiago(int x) {
		rotationPoint = x;

		switch (x) {
		case 0:
			if (orientation == Orientation.NORTH || orientation == Orientation.SOUTH) {
				movingAngleX = -90;
				movingAngleY = 90;
				nextOrientation = orientation == Orientation.NORTH ? Orientation.WEST : Orientation.EAST;
				nextRotation = orientation == Orientation.NORTH ? rotation.previousX() : rotation.nextX();
			} else {
				movingAngleY = -90;
				movingAngleZ = 90;
				nextOrientation = orientation == Orientation.WEST ? Orientation.NORTH : Orientation.SOUTH;
				nextRotation = orientation == Orientation.WEST ? rotation.previousX() : rotation.nextX();
			}
			movingTo = coord.face(Face.SOUTH).face(Face.WEST);
			break;
		case 1:
			if (orientation == Orientation.NORTH || orientation == Orientation.SOUTH) {
				movingAngleX = 90;
				movingAngleY = -90;
				nextOrientation = orientation == Orientation.NORTH ? Orientation.EAST : Orientation.WEST;
				nextRotation = orientation == Orientation.NORTH ? rotation.nextX() : rotation.previousX();
			} else {
				movingAngleY = 90;
				movingAngleZ = 90;
				nextOrientation = orientation == Orientation.WEST ? Orientation.SOUTH : Orientation.NORTH;
				nextRotation = orientation == Orientation.WEST ? rotation.previousX() : rotation.nextX();
			}
			movingTo = coord.face(Face.SOUTH).face(Face.EAST);
			break;
		case 2:
			if (orientation == Orientation.NORTH || orientation == Orientation.SOUTH) {
				movingAngleX = 90;
				movingAngleY = 90;
				nextOrientation = orientation == Orientation.NORTH ? Orientation.WEST : Orientation.EAST;
				nextRotation = orientation == Orientation.NORTH ? rotation.nextX() : rotation.previousX();
			} else {
				movingAngleY = -90;
				movingAngleZ = -90;
				nextOrientation = orientation == Orientation.WEST ? Orientation.NORTH : Orientation.SOUTH;
				nextRotation = orientation == Orientation.WEST ? rotation.nextX() : rotation.previousX();
			}
			movingTo = coord.face(Face.NORTH).face(Face.EAST);
			break;
		case 3:
			if (orientation == Orientation.NORTH || orientation == Orientation.SOUTH) {
				movingAngleX = -90;
				movingAngleY = -90;
				nextOrientation = orientation == Orientation.NORTH ? Orientation.EAST : Orientation.WEST;
				nextRotation = orientation == Orientation.NORTH ? rotation.previousX() : rotation.nextX();
			} else {
				movingAngleY = 90;
				movingAngleZ = -90;
				nextOrientation = orientation == Orientation.WEST ? Orientation.SOUTH : Orientation.NORTH;
				nextRotation = orientation == Orientation.WEST ? rotation.nextX() : rotation.previousX();
			}
			movingTo = coord.face(Face.NORTH).face(Face.WEST);
			break;
		}
	}

	/** Initialize an on-place rotation to change the unit's rotation axe */
	public void rotation() {
		movingTo = coord.clone();
		rotationPoint = 1;

		if (orientation == Orientation.NORTH || orientation == Orientation.SOUTH) {
			nextOrientation = orientation == Orientation.NORTH ? Orientation.WEST : Orientation.EAST;
			nextRotation = rotation.nextX();
			movingAngleX = 90;
			movingAngleY = 90;
		} else {
			nextOrientation = orientation == Orientation.WEST ? Orientation.NORTH : Orientation.SOUTH;
			nextRotation = rotation.previousX();
			movingAngleY = -90;
			movingAngleZ = 90;
		}
	}

	// =========================================================================================================================

	public void goTo(int endX, int endZ) {
		generatePathTo(endX, endZ);

		doNextMove();
	}

	public void doNextMove() {
		Orientation dir = coord.getOrientation(path.peekLast());

		// Test rotation
		if (path.size() >= 2 && coord.getRotationPoint(path.get(path.size() - 2)) >= 0) {
			path.removeLast();
			setDiago(coord.getRotationPoint(path.removeLast()));
		} else {
			if (orientation.isSameAxe(dir))
				rotation();
			else {
				path.removeLast();

				setDirection(dir);
			}
		}
	}

	// =========================================================================================================================

	/**
	 * Path-finding algorithm
	 * 
	 * Generates a list of Coords linking current coords to the indicated location
	 */
	private boolean generatePathTo(int endX, int endZ) {
		// [x][y]
		int[][] map = new int[100][100];

		for (int i = 0; i < map.length; i++)
			for (int j = 0; j < map[0].length; j++)
				map[i][j] = EMPTY;

		map[coord.x][coord.z] = START;

		Queue<Couple> queue = new LinkedList<>();
		queue.add(new Couple(coord.x, coord.z));

		// === Setting values on cubes ===
		Couple p;
		while ((p = queue.poll()) != null) {
			if (p.x == endX && p.z == endZ)
				break;

			addValue(p.x - 1, p.z, map[p.x][p.z] + 1, map, queue);
			addValue(p.x + 1, p.z, map[p.x][p.z] + 1, map, queue);
			addValue(p.x, p.z - 1, map[p.x][p.z] + 1, map, queue);
			addValue(p.x, p.z + 1, map[p.x][p.z] + 1, map, queue);
		}

		// End point not reached
		if (map[endX][endZ] == EMPTY) {
			return false;
		}

		destination = new Coord(endX, coord.y, endZ);

		// === Finding path from values ===
		path = new LinkedList<>();
		path.add(new Coord(endX, this.coord.y, endZ));

		while (map[endX][endZ] != START) {
			// Generates diagonal path (instead of full north then full right)
			if (path.size() % 2 == 0) {
				if (isPrevious(endX - 1, endZ, map, map[endX][endZ]))
					endX--;
				else if (isPrevious(endX, endZ + 1, map, map[endX][endZ]))
					endZ++;
				else if (isPrevious(endX + 1, endZ, map, map[endX][endZ]))
					endX++;
				else if (isPrevious(endX, endZ - 1, map, map[endX][endZ]))
					endZ--;
			} else {
				if (isPrevious(endX, endZ - 1, map, map[endX][endZ]))
					endZ--;
				else if (isPrevious(endX + 1, endZ, map, map[endX][endZ]))
					endX++;
				else if (isPrevious(endX, endZ + 1, map, map[endX][endZ]))
					endZ++;
				else if (isPrevious(endX - 1, endZ, map, map[endX][endZ]))
					endX--;
			}
		}
		path.removeLast(); // Remove the start
		return true;
	}

	// =========================================================================================================================

	/**
	 * Part of the path-finding algorithm
	 * 
	 * Put in a cube the number of rotations needed to reach it
	 */
	private void addValue(int x, int z, int value, int[][] map, Queue<Couple> q) {
		if (x < 0 || x >= map.length || z < 0 || z >= map[0].length)
			return;

		if (map[x][z] != EMPTY)
			return;

		map[x][z] = value;
		q.add(new Couple(x, z));
	}

	/**
	 * Part of the path-finding algorithm
	 * 
	 * Returns true if the indicated cube is closer to the start than the actual
	 */
	private boolean isPrevious(int x, int z, int[][] map, int value) {
		if (x < 0 || x >= map.length || z < 0 || z >= map[0].length)
			return false;

		if (map[x][z] >= value || map[x][z] == EMPTY)
			return false;

		path.add(new Coord(x, this.coord.y, z));

		return true;
	}

	/** Store a couple of int */
	private class Couple {
		int x, z;

		Couple(int x, int z) {
			this.x = x;
			this.z = z;
		}
	}

	// =========================================================================================================================

	public Coord getDestination() {
		return destination;
	}

	public LinkedList<Coord> getPath() {
		return path;
	}

	// =========================================================================================================================

	@Override
	public String toString() {
		return "I'm an unit";
	}
}
