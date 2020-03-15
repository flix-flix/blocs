package data.map.units;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import data.map.Coord;
import data.map.Map;
import data.map.enumerations.Face;
import data.map.enumerations.Orientation;
import utils.Utils;

/** Implementation of a path-finding algorithm */
public class PathFinding {

	private static final int END = 1_000_000;

	// Map class is from data.Map (not java.util.Map)
	// -> and represent a world made of cubes

	// With (X, Y, Z) coordinates:
	// Increasing X it's going North
	// Increasing Y it's going Top
	// Increasing Z it's going East

	/**
	 * Numeric representation of available moves for units looking North or South
	 */
	private static final int[] nsMoves = new int[] { 1, -1, 1_000, -1_000 };
	/** Numeric representation of available moves for units looking East or West */
	private static final int[] ewMoves = new int[] { 1_000_000, -1_000_000, 1_000, -1_000 };
	/** Numeric representation of a diagonal move */
	private static final int[] diagoMoves = new int[] { 999_999, 1_000_001, -1_000_001, -999_999 };

	// =========================================================================================================================
	// Coordinates conversion

	/**
	 * Give the numeric value of a Coord
	 * 
	 * @param ns
	 *            - the unit is oriented to roll
	 */
	public static int getID(Coord coord, boolean ns) {
		int id = 1_000_000 * coord.x + 1_000 * coord.y + coord.z;
		return ns ? id : -id;
	}

	/** Get the Coord from a numeric value */
	public static Coord fromID(int id) {
		id = Math.abs(id);
		return new Coord(id / 1_000_000, id / 1_000 % 1_000, id % 1_000);
	}

	// =========================================================================================================================

	/**
	 * Path-finding algorithm<br>
	 * 
	 * <strong>/!\ Only work with coordinates in range ]0; 1.000[</strong><br>
	 * 
	 * Generates a list of Coords linking start coord to one of the indicated
	 * locations
	 * 
	 * @param ends
	 *            - List of ends
	 * 
	 * @return the list of Coord from start to end (or null if no path exist)
	 */
	public static LinkedList<Coord> generatePathTo(Unit unit, Map map, Coord start, Orientation startOri,
			ArrayList<Coord> ends) {

		// Travelled Coords (with number of minimum steps to reach it)
		HashMap<Integer, Integer> travelled = new HashMap<>();

		// Set the possible ends
		for (Coord end : ends)
			if (isRollable(end, map, unit)) {
				travelled.put(getID(end, true), END);
				travelled.put(getID(end, false), END);

				// Test if the start is the end
				if (start.equals(end))
					return new LinkedList<>();
			}

		// Test if one of the destination is walkable
		if (travelled.isEmpty())
			return null;

		return generatePathTo(unit, map, start, startOri, travelled);
	}

	// =========================================================================================================================

	/**
	 * @param travelled
	 *            - Map with end-points set to {@link #END}
	 */
	private static LinkedList<Coord> generatePathTo(Unit unit, Map map, Coord start, Orientation startOri,
			HashMap<Integer, Integer> travelled) {

		// Numeric representation of the start
		int startID = getID(start, startOri == Orientation.NORTH || startOri == Orientation.SOUTH);
		travelled.put(startID, 0);

		// List of Coords with unexplored neighbors
		LinkedList<Integer> queue = new LinkedList<>(), queueNext = new LinkedList<>();
		queueNext.add(startID);
		// The end point reached
		Coord end = null;

		// ===== Setting values on cubes =====
		int current = 0, length = 0;

		while (end == null && !queueNext.isEmpty()) {
			length++;
			queue = queueNext;
			queueNext = new LinkedList<>();

			while (!queue.isEmpty()) {
				// Current location
				current = queue.pollFirst();

				// End reached
				if (travelled.get(current) == END) {
					end = fromID(current);
					break;
				}

				length = travelled.get(current);

				// In-place rotation
				addToTravelled(current, -current, map, travelled, queueNext, unit);

				// Adjacent + top/bottom
				for (int move : current >= 0 ? nsMoves : ewMoves)
					addToTravelled(current, current + move, map, travelled, queueNext, unit);

				// ===== Diago =====
				// Unit must be on floor to init a diagonal rotation
				if (isRollable(fromID(current), map, unit))
					for (int move : diagoMoves) {
						// Test if diago is empty
						boolean diagoRollable = false;
						if (current > 0)// oriented N/S
							if (Math.abs(move + 1) == 1_000_000)// rolling to W
								diagoRollable = !map.gridContains(fromID(current - 1))
										&& isRollable(fromID(current - 1), map, unit);
							else// rolling to E
								diagoRollable = !map.gridContains(fromID(current + 1))
										&& isRollable(fromID(current + 1), map, unit);
						else // oriented E/W
						if (move > 0)// rolling to N
							diagoRollable = !map.gridContains(fromID(current + 1_000_000))
									&& isRollable(fromID(current + 1_000_000), map, unit);
						else// rolling to S
							diagoRollable = !map.gridContains(fromID(current - 1_000_000))
									&& isRollable(fromID(current - 1_000_000), map, unit);

						if (diagoRollable)
							addToTravelled(current, -(current + move), map, travelled, queueNext, unit);
					}
			}
		}

		// End point not reached
		if (end == null)
			return null;

		// ===== Finding path from values =====
		// List of the coords to the destination
		LinkedList<Coord> path = new LinkedList<>();

		path.add(end);
		travelled.put(current, length);

		backTrace: while (current != startID) {
			// Adjacent + top/bottom
			for (int move : current >= 0 ? nsMoves : ewMoves)
				if (addToPath(path, travelled, map, unit, current, current + move)) {
					current += move;
					continue backTrace;
				}

			// In-place rotation
			if (addToPath(path, travelled, map, unit, current, -current)) {
				current = -current;
				continue;
			}

			// Diago
			for (int move : diagoMoves)
				if (addToPath(path, travelled, map, unit, current, -(current + move))) {
					current = -(current + move);
					continue backTrace;
				}

			Utils.debug("Can't backTrace");
			return null;
		}

		path.removeFirst(); // Remove the start
		return path;
	}

	// =========================================================================================================================

	/**
	 * Add the coordinate to travelled ones if the unit can reach it
	 * 
	 * @param _current
	 *            - the current coord
	 * @param _add
	 *            - the added coord
	 * 
	 * @return true if the end has been reached
	 */
	public static boolean addToTravelled(int _current, int _add, Map map, HashMap<Integer, Integer> travelled,
			LinkedList<Integer> queue, Unit unit) {
		Coord current = fromID(_current), next = fromID(_add);

		// Test if the bloc is empty
		if (isFilled(next, map, unit))
			return false;

		if (travelled.containsKey(_add))
			// End reached
			if (travelled.get(_add) == END) {
				queue.add(_add);
				return true;
			}
			// The cube has already be travelled
			else
				return false;

		// The unit can only do one step without touching the floor
		if (!isRollable(current, map, unit) && !isRollable(next, map, unit))
			return false;

		travelled.put(_add, travelled.get(_current) + 1);
		queue.add(_add);

		return false;
	}

	/**
	 * @return true if the added Coord is closer to the start and have been added
	 */
	private static boolean addToPath(LinkedList<Coord> path, HashMap<Integer, Integer> travelled, Map map, Unit unit,
			int _current, int _add) {
		Coord current = fromID(_current), add = fromID(_add);
		// The bloc must be closer to the start
		if (travelled.containsKey(_add) && travelled.get(_add) < travelled.get(_current)) {
			// The unit can only do one step without touching the floor
			if (!isRollable(current, map, unit) && !isRollable(add, map, unit))
				return false;

			path.addFirst(add);
			return true;
		}
		return false;
	}

	// =========================================================================================================================
	// Map tests

	/**
	 * @param coord
	 *            - the Coord to test
	 * @return true if something at the given Coord prevent the unit to be there
	 */
	public static boolean isFilled(Coord coord, Map map, Unit unit) {
		// The coord must be empty (and don't be the unit position)
		return map.gridContains(coord) && map.gridGet(coord).unit != unit;
	}

	/**
	 * @param coord
	 *            - the Coord to test
	 * @return true if the unit can roll on the given Coord
	 */
	public static boolean isRollable(Coord coord, Map map, Unit unit) {
		if (isFilled(coord, map, unit))
			return false;
		// The unit can't take support on itself
		return map.isOnFloor(coord) && map.gridGet(coord.face(Face.DOWN)).unit != unit;
	}
}
