package data.map.units;

import java.io.Serializable;
import java.util.LinkedList;

import client.session.Action;
import client.session.Player;
import data.enumeration.Face;
import data.enumeration.Orientation;
import data.enumeration.Rotation;
import data.map.Coord;
import data.map.Map;
import data.map.resources.Resource;
import utils.FlixBlocksUtils;

public class Unit implements Serializable {
	private static final long serialVersionUID = -3095427303288055442L;

	private static final LinkedList<Coord> NO_NEW_PATH = new LinkedList<>();

	// ========== Data ==========
	/** Player controlling this unit */
	Player player;

	// ========== Position ==========
	/** Coords of the unit */
	public Coord coord;
	/** The unit orientation (the "north texture" looking to ...) */
	public Orientation orientation = Orientation.NORTH;
	/** The unit rotation (only on Rotation.axeX) */
	public Rotation rotation = Rotation.NONE;

	// ========== Rotation (Step) ==========
	/** The current step of the rotation */
	private int movingStep;
	/** The number of steps to achieve the rotation */
	private final int movingStepFinal = 20;

	// ========== Rotation (Maths) ==========
	/** Index of the cube's rotation point */
	public int rotationPoint = 0;
	/** For each axe : the rotation to perform to complete the rotation */
	private int movingAngleX = 0, movingAngleY = 0, movingAngleZ = 0;
	/** For each axe : the current rotation of the unit */
	public double ax = 0, ay = 0, az = 0;

	// ========== Rotation (End) ==========
	/** Currently moving to this Coord (an adjacent bloc) (null if not moving) */
	private Coord movingTo;
	/** Orientation at the end of the rotation */
	private Orientation nextOrientation = orientation;
	/** Rotation at the end of the rotation */
	private Rotation nextRotation = rotation;

	/** This unit come from this Coord (an adjacent bloc) (null if just spawned) */
	private Coord comingFrom;

	// ========== Journey ==========
	/** Coord of the destination (null if not traveling) */
	private Coord destination;
	/** List of the coords to the destination */
	private LinkedList<Coord> path;
	/** New path (stored till the end of the current rotation then replace path) */
	private LinkedList<Coord> newPath = NO_NEW_PATH;

	// ========== Action ==========
	/** Action to do at the end of the deplacement */
	private Action action = null;
	/** Bloc targeted by the action */
	private Coord actionCube;

	// ========== Harvesting ==========
	private Resource resource;
	private int maxCapacity = 5;

	private int timeHarvest = 5;
	private int alreadyHarvest = 0;

	private int timeDrop = 2;
	private int alreadyDrop = 0;

	// =========================================================================================================================

	public Unit(Player player, int x, int y, int z) {
		this.player = player;
		coord = new Coord(x, y, z);
	}

	// =========================================================================================================================

	public void tick(Map map) {
		if (isTraveling())
			doStep(map);
		else if (action != null)
			doAction(map);
	}

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

	public void doAction(Map map) {
		switch (action) {
		case DESTROY:
			if (map.gridGet(actionCube).addMined(1)) {// Cube break
				map.remove(actionCube);
				action = null;
			}
			break;
		case BUILD:
			if (map.gridGet(actionCube).build.addAlreadyBuild(1)) // Building created
				action = null;
			break;
		case HARVEST:
			alreadyHarvest++;

			if (alreadyHarvest < timeHarvest)
				break;// Keep working

			alreadyHarvest = 0;

			if (resource == null || resource.getType() != map.gridGet(actionCube).getResource().getType())
				resource = new Resource(map.gridGet(actionCube).getResource().getType(), 0, maxCapacity);

			resource.add(map.gridGet(actionCube).resourceTake(1));

			if (map.gridGet(actionCube).resourceIsEmpty()) { // Cube break
				map.remove(actionCube);
				action = null;
			}

			if (resource.isFull())
				action = null;// TODO Go Drop

			break;
		case DROP:
			alreadyDrop++;

			if (alreadyDrop < timeDrop)
				break;// Keep working

			alreadyDrop = 0;

			if (map.gridGet(actionCube).build.addToStock(resource, 1))// Stock full
				action = null;// TODO Except if there is another empty stock around
			else if (resource.isEmpty()) {// TODO Go Harvest
				removeResource();
				action = null;
			}

			break;
		case ATTACK:
			break;
		default:
			FlixBlocksUtils.debug("Action " + action + " unimplemented");
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

		if (newPath != NO_NEW_PATH) {
			path = newPath;
			newPath = NO_NEW_PATH;
		}

		if (path != null && !path.isEmpty())
			doNextMove();
		else
			destination = null;
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
		// TODO Crash if diago before
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

	/**
	 * The unit will roll to reach the coordinates
	 * 
	 * @return true if coords are reachable
	 */
	public boolean goTo(Map map, Coord end) {
		return setPath(PathFinding.generatePathTo(this, map, destination == null ? coord : movingTo, end));
	}

	/** The unit will goto the closest bloc next to the target (not on top) */
	public boolean goAround(Map map, Coord end) {
		LinkedList<Coord> path = null, temp;

		for (Face face : new Face[] { Face.DOWN, Face.NORTH, Face.EAST, Face.SOUTH, Face.WEST })
			if ((temp = PathFinding.generatePathTo(this, map, destination == null ? coord : movingTo,
					end.face(face))) != null)
				if (path == null || temp.size() < path.size())
					path = temp;

		return setPath(path);
	}

	private boolean setPath(LinkedList<Coord> path) {
		if (isTraveling())
			newPath = path;
		else {
			this.path = path;
			destination = (path == null || path.isEmpty()) ? null : path.getLast();
			action = null;
			if (path != null && !path.isEmpty())
				doNextMove();
		}

		return path != null;
	}

	// =========================================================================================================================

	public boolean doAction(Action action, Map map, Coord coord, Face face) {
		if (!goAround(map, coord))
			return false;

		this.action = action;
		this.actionCube = coord;

		return true;
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

	public boolean isTraveling() {
		return destination != null;
	}

	public LinkedList<Coord> getPath() {
		return path;
	}

	public boolean isActif() {
		return destination != null || action != null;
	}

	public Player getPlayer() {
		return player;
	}

	public Resource getResource() {
		return resource;
	}

	public boolean hasResource() {
		return resource != null;
	}

	public void removeResource() {
		resource = null;
	}

	// =========================================================================================================================

	@Override
	public String toString() {
		return "I'm an unit of " + player.getName();
	}
}
