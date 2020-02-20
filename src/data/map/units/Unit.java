package data.map.units;

import java.io.Serializable;
import java.util.LinkedList;

import data.Gamer;
import data.id.ItemTableClient;
import data.map.Coord;
import data.map.Map;
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
			System.err.println("ERROR movingTo = null " + Thread.currentThread().getName());
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
	protected void setDirection(Orientation dir) {
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
	public void setDiago(int x) {
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
	public void setUp(Orientation dir) {
		movingTo = coord.face(Face.UP);

		nextRotation = orientation.next() == dir ? rotation.nextX() : rotation.previousX();

		switch (dir) {
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
	public void setDown(Orientation dir) {
		movingTo = coord.face(Face.DOWN);

		nextRotation = orientation.next() == dir ? rotation.nextX() : rotation.previousX();
		// TODO Crash if diago before
		switch (dir) {
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
	 * The unit will roll to reach the coordinates
	 * 
	 * @return true if coords are reachable
	 */
	public boolean goTo(Map map, Coord end) {
		return setPath(PathFinding.generatePathTo(this, map, destination == null ? coord : movingTo, end));
	}

	/** The unit will goto the closest bloc next to the target (not on top) */
	public boolean goAround(Map map, Coord end) {
		System.out.println("end: " + end.toString());
		LinkedList<Coord> path = null, temp;

		for (Face face : new Face[] { Face.DOWN, Face.NORTH, Face.EAST, Face.SOUTH, Face.WEST })
			if ((temp = PathFinding.generatePathTo(this, map, destination == null ? coord : movingTo,
					end.face(face))) != null)
				if (path == null || temp.size() < path.size())
					path = temp;

		if (path != null)
			for (Coord c : path)
				System.out.println(c);

		return setPath(path);
	}

	private boolean setPath(LinkedList<Coord> path) {
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
		if (!goAround(map, coord))
			return false;

		this.action = action;
		this.actionCube = coord;

		return true;
	}

	public boolean building(Map map, Building build) {
		if (!goAround(map, build.getCube().coords()))
			return false;

		this.action = Action.UNIT_BUILD;
		this.actionCube = build.getCube().coords();

		return true;
	}

	public boolean harvest(Map map, Coord coord) {
		if (!goAround(map, coord))
			return false;

		this.action = Action.UNIT_HARVEST;
		this.actionCube = coord;

		return true;
	}

	public boolean store(Map map, Building build) {
		if (!goAround(map, build.getCube().coords()))
			return false;

		this.action = Action.UNIT_STORE;
		this.actionCube = build.getCube().coords();

		return true;
	}

	// =========================================================================================================================

	public void doNextMove() {
		Orientation dir = coord.getOrientation(path.peekFirst());

		if (path.size() >= 2 && coord.y + 1 == path.peekFirst().y) {// Going up
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
			// Diagonal rotation
			if (path.size() >= 2 && coord.getRotationPointDiago(path.get(1)) >= 0
					&& coord.getRotationPointDiago(path.get(1)) < 4) {

				path.removeFirst();
				setDiago(coord.getRotationPointDiago(path.removeFirst()));

			}
			// Do adjacent rotation
			else {
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
