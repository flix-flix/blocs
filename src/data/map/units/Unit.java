package data.map.units;

import java.util.LinkedList;

import client.session.Player;
import data.enumeration.Face;
import data.enumeration.Orientation;
import data.enumeration.Rotation;
import data.map.Map;
import utils.Coord;

public class Unit {

	/** Player controlling this unit */
	Player player;

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

	/**
	 * This unit is currently moving to this Coord (an adjacent bloc) (null if not
	 * moving)
	 */
	private Coord movingTo;
	/** This unit come from this Coord (an adjacent bloc) (null if just spawned) */
	private Coord comingFrom;

	/** Coord of the destination */
	private Coord destination;
	/** List of the coords to the destination */
	private LinkedList<Coord> path;

	// =========================================================================================================================

	public Unit(Player player, int x, int y, int z) {
		this.player = player;
		coord = new Coord(x, y, z);
	}

	// =========================================================================================================================

	/**
	 * Increase the rotation by one step and re-calculate the corresponding angles
	 */
	public void doStep(Map map) {
		if (movingTo == null)
			return;

		movingStep++;

		if (movingStep < movingStepFinal) {
			ax = (movingStep / (double) movingStepFinal) * movingAngleX;
			ay = -(movingStep / (double) movingStepFinal) * movingAngleY;
			az = (movingStep / (double) movingStepFinal) * movingAngleZ;
		} else {
			arrive(map);
		}
	}

	// =========================================================================================================================

	/** Update cube position after the rotation */
	public void arrive(Map map) {
		rotation = nextRotation;
		orientation = nextOrientation;

		movingStep = 0;
		ax = 0;
		ay = 0;
		az = 0;

		movingAngleX = 0;
		movingAngleY = 0;
		movingAngleZ = 0;

		map.removeUnit(this);
		comingFrom = coord;
		coord = movingTo;
		map.addUnit(this);
		movingTo = null;

		if (!path.isEmpty())
			doNextMove();
	}

	// =========================================================================================================================

	/** Initialize a rotation to an adjacent position */
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

	/** Initialize a rotation to the upper position */
	public void setUp(Orientation dir) {
		movingTo = coord.face(Face.UP);

		nextRotation = orientation.next() == dir ? rotation.nextX() : rotation.previousX();

		switch (dir) {
		case NORTH:
			rotationPoint = 6;
			movingAngleZ = -90;
			break;
		case EAST:
			rotationPoint = 5;
			movingAngleX = 90;
			break;
		case SOUTH:
			rotationPoint = 4;
			movingAngleZ = 90;
			break;
		case WEST:
			rotationPoint = 4;
			movingAngleX = -90;
			break;
		default:
			break;
		}
	}

	/** Initialize a rotation to the position below */
	public void setDown(Orientation dir) {
		movingTo = coord.face(Face.DOWN);

		nextRotation = orientation.next() == dir ? rotation.nextX() : rotation.previousX();

		switch (dir) {
		case NORTH:
			rotationPoint = 1;
			movingAngleZ = -90;
			break;
		case EAST:
			rotationPoint = 0;
			movingAngleX = 90;
			break;
		case SOUTH:
			rotationPoint = 3;
			movingAngleZ = 90;
			break;
		case WEST:
			rotationPoint = 1;
			movingAngleX = -90;
			break;
		default:
			break;
		}
	}

	/** Initialize an on-place rotation to change the unit's rotation axe */
	public void rotation() {
		movingTo = coord.clone();
		rotationPoint = 1;

		if (orientation == Orientation.NORTH || orientation == Orientation.SOUTH) {
			nextOrientation = orientation == Orientation.NORTH ? Orientation.WEST : Orientation.EAST;
			nextRotation = orientation == Orientation.NORTH ? rotation.nextX() : rotation.previousX();
			movingAngleX = 90;
			movingAngleY = 90;
		} else {
			nextOrientation = orientation == Orientation.WEST ? Orientation.NORTH : Orientation.SOUTH;
			nextRotation = orientation == Orientation.WEST ? rotation.previousX() : rotation.nextX();
			movingAngleY = -90;
			movingAngleZ = 90;
		}
	}

	// =========================================================================================================================

	public void goTo(Map map, int endX, int endY, int endZ) {
		path = PathFinding.generatePathTo(map, coord, endX, endY, endZ);
		if (path == null)
			return;

		destination = new Coord(endX, endY, endZ);
		doNextMove();
	}

	public void goTo(Map map, Coord coord) {
		goTo(map, coord.x, coord.y, coord.z);
	}

	// =========================================================================================================================

	public void mine(Map map, Coord coord, Face face) {
		goTo(map, coord.face(face));
	}

	// =========================================================================================================================

	public void doNextMove() {
		Orientation dir = coord.getOrientation(path.peekFirst());

		if (coord.y + 1 == path.peekFirst().y) {// Going up
			Orientation nextDir = path.get(0).getOrientation(path.get(1));

			if (orientation.isSameAxe(nextDir))
				rotation();
			else {
				path.removeFirst();
				setUp(nextDir);
			}
		} else if (coord.y - 1 == path.peekFirst().y) {// Going down
			Orientation prevDir = comingFrom.getOrientation(coord);

			if (orientation.isSameAxe(prevDir))
				rotation();
			else {
				path.removeFirst();
				setDown(prevDir);
			}
		} else {
			// Detect diagonal rotation
			if (path.size() >= 2 && coord.getRotationPointDiago(path.get(1)) >= 0
					&& coord.getRotationPointDiago(path.get(1)) < 4) {

				path.removeFirst();
				setDiago(coord.getRotationPointDiago(path.removeFirst()));
			} else {// Do adjacent rotation
				if (orientation.isSameAxe(dir))
					rotation();
				else {
					path.removeFirst();

					setDirection(dir);
				}
			}
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
		return "I'm an unit of " + player.getName();
	}
}
