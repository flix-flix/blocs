package data.map.units;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

import data.Gamer;
import data.id.ItemTableClient;
import data.map.Coord;
import data.map.Cube;
import data.map.Map;
import data.map.MultiCube;
import data.map.buildings.Building;
import data.map.enumerations.Face;
import data.map.enumerations.Orientation;
import data.map.enumerations.Rotation;
import data.map.resources.Resource;
import server.send.Action;
import utils.Utils;

public class Unit implements Serializable {
	private static final long serialVersionUID = -3095427303288055442L;

	private static int nextID = 0;

	// ========== Data ==========
	/** ID of the unit (allow server/client to communicate) */
	protected int id;
	/** Gamer controlling this unit */
	protected Gamer gamer;
	/** Type of unit */
	protected int itemID;

	// ========== Position ==========
	/** Coords of the unit */
	public Coord coord;
	/** The unit orientation (the "north texture" looking to ...) */
	public Orientation orientation = Orientation.NORTH;
	/** The unit rotation (only on Rotation.axeX) */
	public Rotation rotation = Rotation.NONE;

	// ========== Rotation (Step) ==========
	/** The current step of the rotation */
	protected int movingStep;
	/** The number of steps to achieve the rotation */
	protected final int movingStepFinal = 20;

	// ========== Rotation (Maths) ==========
	/** Index of the cube's rotation point */
	public int rotationPoint = 0;

	// ========== Rotation (End) ==========
	/** Currently moving to this Coord (an adjacent bloc) (null if not moving) */
	protected Coord movingTo;
	/** Orientation at the end of the rotation */
	protected Orientation nextOrientation = orientation;
	/** Rotation at the end of the rotation */
	protected Rotation nextRotation = rotation;

	/** This unit come from this Coord (an adjacent bloc) (null if just spawned) */
	protected Coord comingFrom;

	// ========== Journey ==========
	/** Coord of the destination (null if not traveling) */
	protected Coord destination;
	/** List of the coords to the destination */
	protected LinkedList<Coord> path;
	/** New path (stored till the end of the current rotation then replace path) */
	protected LinkedList<Coord> newPath;
	/** true : path should be replaced by newPath */
	protected boolean hasNewPath = false;

	/** List of all possible destinations */
	protected ArrayList<Coord> destinations;

	// ========== Action ==========
	/** Action to do at the end of the deplacement */
	protected Action action = null;
	/** Bloc targeted by the action */
	protected Coord actionCube;

	// ========== Harvesting ==========
	protected Resource resource;
	protected int maxCapacity = 5;

	protected int timeHarvest = 50;
	protected int alreadyHarvest = 0;

	protected int timeDrop = 20;
	protected int alreadyDrop = 0;

	// =========================================================================================================================

	public Unit(int itemID, Gamer gamer, int x, int y, int z) {
		this.itemID = itemID;
		id = nextID++;
		this.gamer = gamer;
		coord = new Coord(x, y, z);
	}

	protected Unit(Unit u) {
		id = u.id;
		gamer = u.gamer;
		itemID = u.itemID;
		coord = u.coord;

		orientation = u.orientation;
		rotation = u.rotation;

		movingStep = u.movingStep;
		rotationPoint = u.rotationPoint;

		movingTo = u.movingTo;
		nextOrientation = u.nextOrientation;
		nextRotation = u.nextRotation;
		comingFrom = u.comingFrom;

		destination = u.destination;
		path = u.path;
		newPath = u.newPath;

		action = u.action;
		actionCube = u.actionCube;

		resource = u.resource;
		maxCapacity = u.maxCapacity;

		timeHarvest = u.timeHarvest;
		alreadyHarvest = u.alreadyHarvest;
		timeDrop = u.timeDrop;
		alreadyDrop = u.alreadyDrop;
	}

	// =========================================================================================================================

	public void tick(Map map) {
		if (isTraveling()) {
			doStep(map);
			alreadyDrop = 0;
			alreadyHarvest = 0;
		} else if (action != null)
			doAction(map);
	}

	/** Increase the rotation step by one */
	public boolean doStep(Map map) {
		if (movingTo == null)
			return false;

		return ++movingStep < movingStepFinal;
	}

	/** Increase the action step by one */
	public void doAction(Map map) {
		switch (action) {
		case UNIT_GOTO:
			action = null;
			break;

		case UNIT_BUILD:
			if (map.gridGet(actionCube) != null & map.gridGet(actionCube).build != null)
				doBuild(map, map.gridGet(actionCube).build);
			break;

		case UNIT_HARVEST:
			if (++alreadyHarvest < timeHarvest)
				break;// Keep working

			alreadyHarvest = 0;

			// TODO Avoid destruction of current Resource
			if (resource == null || resource.getType() != map.gridGet(actionCube).getResource().getType())
				resource = new Resource(map.gridGet(actionCube).getResource().getType(), 0, maxCapacity);

			doHarvest(map);
			break;

		case UNIT_STORE:
			if (++alreadyDrop < timeDrop)
				break;// Keep working

			alreadyDrop = 0;

			if (map.gridGet(actionCube) != null & map.gridGet(actionCube).build != null)
				doStore(map, map.gridGet(actionCube).build);
			break;

		case UNIT_ATTACK:
			break;

		default:
			Utils.debugBefore("Action " + action + " unimplemented");
		}
	}

	public void doBuild(Map map, Building build) {
	}

	public void doHarvest(Map map) {
	}

	public void doStore(Map map, Building build) {
	}

	// =========================================================================================================================

	/** Update cube position after the rotation */
	protected void arrive() {
		rotation = nextRotation;
		orientation = nextOrientation;

		movingStep = 0;

		comingFrom = coord;
		if (movingTo == null)
			Utils.debug("movingTo = null " + Thread.currentThread().getName());
		else
			coord = movingTo;
		movingTo = null;

		if (hasNewPath) {
			path = newPath;
			hasNewPath = false;
		}

		if (path != null && !path.isEmpty())
			doNextMove();
		else
			destination = null;
	}

	// =========================================================================================================================

	/** Initialize a rotation to an adjacent position */
	protected void rollAdjacent(Orientation dir) {
		movingTo = coord.face(dir.face);

		nextRotation = orientation.next() == dir ? rotation.nextX() : rotation.previousX();

		switch (dir) {
		case NORTH:
			rotationPoint = 2;
			break;
		case EAST:
			rotationPoint = 1;
			break;
		case SOUTH:
			rotationPoint = 0;
			break;
		case WEST:
			rotationPoint = 0;
			break;
		}
	}

	/** Initialize a rotation to a diagonal position */
	public void rollDiago(int x) {
		rotationPoint = x;

		switch (x) {
		case 0:
			if (orientation == Orientation.NORTH || orientation == Orientation.SOUTH) {
				nextOrientation = orientation == Orientation.NORTH ? Orientation.WEST : Orientation.EAST;
				nextRotation = orientation == Orientation.NORTH ? rotation.previousX() : rotation.nextX();
			} else {
				nextOrientation = orientation == Orientation.WEST ? Orientation.NORTH : Orientation.SOUTH;
				nextRotation = orientation == Orientation.WEST ? rotation.previousX() : rotation.nextX();
			}
			movingTo = coord.face(Face.SOUTH).face(Face.WEST);
			break;
		case 1:
			if (orientation == Orientation.NORTH || orientation == Orientation.SOUTH) {
				nextOrientation = orientation == Orientation.NORTH ? Orientation.EAST : Orientation.WEST;
				nextRotation = orientation == Orientation.NORTH ? rotation.nextX() : rotation.previousX();
			} else {
				nextOrientation = orientation == Orientation.WEST ? Orientation.SOUTH : Orientation.NORTH;
				nextRotation = orientation == Orientation.WEST ? rotation.previousX() : rotation.nextX();
			}
			movingTo = coord.face(Face.SOUTH).face(Face.EAST);
			break;
		case 2:
			if (orientation == Orientation.NORTH || orientation == Orientation.SOUTH) {
				nextOrientation = orientation == Orientation.NORTH ? Orientation.WEST : Orientation.EAST;
				nextRotation = orientation == Orientation.NORTH ? rotation.nextX() : rotation.previousX();
			} else {
				nextOrientation = orientation == Orientation.WEST ? Orientation.NORTH : Orientation.SOUTH;
				nextRotation = orientation == Orientation.WEST ? rotation.nextX() : rotation.previousX();
			}
			movingTo = coord.face(Face.NORTH).face(Face.EAST);
			break;
		case 3:
			if (orientation == Orientation.NORTH || orientation == Orientation.SOUTH) {
				nextOrientation = orientation == Orientation.NORTH ? Orientation.EAST : Orientation.WEST;
				nextRotation = orientation == Orientation.NORTH ? rotation.previousX() : rotation.nextX();
			} else {
				nextOrientation = orientation == Orientation.WEST ? Orientation.SOUTH : Orientation.NORTH;
				nextRotation = orientation == Orientation.WEST ? rotation.nextX() : rotation.previousX();
			}
			movingTo = coord.face(Face.NORTH).face(Face.WEST);
			break;
		}
	}

	/** Initialize a rotation to the upper position */
	public void rollUp(Orientation connection) {
		movingTo = coord.face(Face.UP);

		nextRotation = orientation.next() == connection ? rotation.nextX() : rotation.previousX();

		switch (connection) {
		case NORTH:
			rotationPoint = 6;
			break;
		case EAST:
			rotationPoint = 5;
			break;
		case SOUTH:
			rotationPoint = 4;
			break;
		case WEST:
			rotationPoint = 4;
			break;
		}
	}

	/** Initialize a rotation to the position below */
	public void rollDown(Orientation connection) {
		movingTo = coord.face(Face.DOWN);

		nextRotation = orientation.next() == connection ? rotation.nextX() : rotation.previousX();

		switch (connection) {
		case NORTH:
			rotationPoint = 1;
			break;
		case EAST:
			rotationPoint = 0;
			break;
		case SOUTH:
			rotationPoint = 3;
			break;
		case WEST:
			rotationPoint = 1;
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
		} else {
			nextOrientation = orientation == Orientation.WEST ? Orientation.NORTH : Orientation.SOUTH;
			nextRotation = orientation == Orientation.WEST ? rotation.previousX() : rotation.nextX();
		}
	}

	// =========================================================================================================================

	/**
	 * Generates and returns the path to one the ends
	 */
	public LinkedList<Coord> generatePath(Map map, ArrayList<Coord> ends) {
		if (destination == null)
			return PathFinding.generatePathTo(this, map, coord, orientation, ends);
		else
			return PathFinding.generatePathTo(this, map, movingTo, nextOrientation, ends);
	}

	/**
	 * Generates and returns the path to the end
	 */
	public LinkedList<Coord> generatePath(Map map, Coord end) {
		ArrayList<Coord> ends = new ArrayList<>();
		ends.add(end);
		return generatePath(map, ends);
	}

	// =========================================================================================================================

	/**
	 * The unit will go to the coordinates
	 * 
	 * @return true if coords are reachable
	 */
	public boolean goToCube(Map map, Coord end) {
		return setPath(generatePath(map, end));
	}

	/** The unit will go to the closest bloc next to the target (not on top) */
	public boolean goAroundCube(Map map, Coord end) {
		ArrayList<Coord> ends = new ArrayList<>();

		for (Face face : new Face[] { Face.DOWN, Face.NORTH, Face.EAST, Face.SOUTH, Face.WEST })
			ends.add(end.face(face));

		return setPath(generatePath(map, ends));
	}

	/**
	 * The unit will go to the coordinates
	 * 
	 * @return true if coords are reachable
	 */
	public boolean goAroundMulti(Map map, MultiCube multi) {
		ArrayList<Coord> ends = new ArrayList<>();

		for (Cube cube : multi.list)
			for (Face face : Face.faces)
				ends.add(cube.gridCoord.face(face));

		LinkedList<Coord> path = generatePath(map, ends);

		if (path != null)
			this.destinations = ends;

		return setPath(path);
	}

	// =========================================================================================================================

	public boolean setPath(LinkedList<Coord> path) {
		if (isTraveling()) {
			newPath = path;
			hasNewPath = true;
		} else {
			this.path = path;
			destination = (path == null || path.isEmpty()) ? null : path.getLast();
			action = null;
			if (path != null && !path.isEmpty())
				doNextMove();
		}

		return path != null;
	}

	// =========================================================================================================================

	public boolean doAction(Action action, Map map, Coord coord) {
		if (!goAroundCube(map, coord))
			return false;

		this.action = action;
		this.actionCube = coord;

		return true;
	}

	public boolean building(Map map, Building build) {
		if (!goAroundMulti(map, build.getMulti()))
			return false;

		this.action = Action.UNIT_BUILD;
		this.actionCube = build.getCube().coords();

		return true;
	}

	public boolean harvest(Map map, Coord coord) {
		MultiCube multi = map.gridGet(coord).multicube;

		if (multi != null) {
			if (!goAroundMulti(map, multi))
				return false;
		} else if (!goAroundCube(map, coord))
			return false;

		this.action = Action.UNIT_HARVEST;
		this.actionCube = coord;

		return true;
	}

	public boolean store(Map map, Building build) {
		if (!goAroundMulti(map, build.getMulti()))
			return false;

		this.action = Action.UNIT_STORE;
		this.actionCube = build.getCube().coords();

		return true;
	}

	// =========================================================================================================================

	protected void doNextMove() {
		// Going up
		if (coord.y + 1 == path.peekFirst().y) {
			// We need to know where the unit is going to smoothify the rotation
			if (path.size() < 2) {
				Utils.debug("Unit go up without other movements");
				return;
			}
			Orientation connection = path.get(0).getConnection(path.get(1));

			if (connection == null)
				if (orientation == Orientation.NORTH || orientation == Orientation.SOUTH)
					connection = path.get(1).z > coord.z ? Orientation.WEST : Orientation.EAST;
				else
					connection = path.get(1).x > coord.x ? Orientation.NORTH : Orientation.SOUTH;

			if (orientation.isSameAxe(connection))
				rotation();
			else {
				path.removeFirst();
				rollUp(connection);
			}
		}
		// Going down
		else if (coord.y - 1 == path.peekFirst().y) {
			Orientation connection = comingFrom.getConnection(coord);

			if (connection == null)
				if (orientation == Orientation.NORTH || orientation == Orientation.SOUTH)
					connection = comingFrom.z > coord.z ? Orientation.WEST : Orientation.EAST;
				else
					connection = comingFrom.x > coord.x ? Orientation.SOUTH : Orientation.NORTH;

			path.removeFirst();
			rollDown(connection);
		}
		// Do adjacent rotation
		else if (coord.getConnection(path.peekFirst()) != null) {
			Orientation connection = coord.getConnection(path.peekFirst());

			if (orientation.isSameAxe(connection))
				rotation();
			else {
				path.removeFirst();
				rollAdjacent(connection);
			}
		}
		// In-place rotation
		else if (coord.equals(path.peekFirst())) {
			path.removeFirst();
			rotation();
		}
		// Diagonal rotation
		else {
			if (coord.getRotationPointDiago(path.peekFirst()) == -1) {
				Utils.debug("Unit diagonal rotation without rotation point (" + coord.toString() + " -> "
						+ path.peekFirst() + ")");
				return;
			}
			if (coord.getRotationPointDiago(path.peekFirst()) >= 4) {
				Utils.debug("Unit diagonal rotation point is on top of the bloc");
				return;
			}

			rollDiago(coord.getRotationPointDiago(path.removeFirst()));
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

	public Gamer getGamer() {
		return gamer;
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

	public int getItemID() {
		return itemID;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return ItemTableClient.getName(itemID);
	}

	// =========================================================================================================================

	@Override
	public String toString() {
		return "Player: " + gamer.getName() + " | Coords: " + (coord == null ? null : coord.toString());
	}
}
