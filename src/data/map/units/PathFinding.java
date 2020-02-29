package data.map.units;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import data.map.Coord;
import data.map.Map;
import data.map.enumerations.Face;
import data.map.enumerations.Orientation;
import utils.Utils;

public class PathFinding {

	private static final int END = 1_000_000;

	private static final int[] nsMoves = new int[] { 1, -1, 1_000, -1_000 };
	private static final int[] ewMoves = new int[] { 1_000_000, -1_000_000, 1_000, -1_000 };
	private static final int[] diagoMoves = new int[] { 999_999, 1_000_001, -1_000_001, -999_999 };

	public static LinkedList<Coord> generatePathAround(Unit unit, Map map, Coord start, Orientation startOri,
			ArrayList<Coord> around) {

		ArrayList<Coord> ends = new ArrayList<>();

		for (Coord coord : around)
			for (Face face : Face.faces)
				ends.add(coord.face(face));

		return generatePathTo(unit, map, start, startOri, ends);
	}

	/**
	 * Path-finding algorithm<br>
	 * 
	 * <strong>/!\ Only work with 0 < coords < 1.000</strong><br>
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

		// Travelled Coords (with number of minimum steps to reach)
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

	/**
	 * @param travelled
	 *            - Map with end-points set to {@link #END}
	 */
	private static LinkedList<Coord> generatePathTo(Unit unit, Map map, Coord start, Orientation startOri,
			HashMap<Integer, Integer> travelled) {
		// List of the coords to the destination
		LinkedList<Coord> path = new LinkedList<>();

		int startID = getID(start, startOri == Orientation.NORTH || startOri == Orientation.SOUTH);

		travelled.put(startID, 0);

		// List of Coords with unexplored neighbors
		LinkedList<Integer> queue = new LinkedList<>(), queueNext = new LinkedList<>();
		queueNext.add(startID);
		// The end point reached
		Coord end = null;

		// ===== Setting values on cubes =====
		int current = 0, length = 0;

		trace: while (!queueNext.isEmpty()) {
			length++;
			queue = queueNext;
			queueNext = new LinkedList<>();

			while (!queue.isEmpty()) {
				current = queue.pollFirst();

				// End reached
				if (travelled.get(current) == END) {
					end = fromID(current);
					break trace;
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
						boolean diagoEmpty = false;
						if (current > 0)// oriented N/S
							if (Math.abs(move + 1) == 1_000_000)// rolling to W
								diagoEmpty = !map.gridContains(fromID(current - 1));
							else// rolling to E
								diagoEmpty = !map.gridContains(fromID(current + 1));
						else // oriented E/W
						if (move > 0)// rolling to N
							diagoEmpty = !map.gridContains(fromID(current + 1_000_000));
						else// rolling to S
							diagoEmpty = !map.gridContains(fromID(current - 1_000_000));

						if (diagoEmpty)
							addToTravelled(current, -(current + move), map, travelled, queueNext, unit);
					}
			}
		}

		// End point not reached
		if (end == null)
			return null;

		// ===== Finding path from values =====
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
				continue backTrace;
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

	/** Give the numeric value of a Coord */
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

	/** Return true if the end has been reached */
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
	 * Returns true if the added coord is closer to the start and have been added
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

	public static boolean isFilled(Coord coord, Map map, Unit unit) {
		// The coord must be empty (and don't be the unit position)
		return map.gridContains(coord) && map.gridGet(coord).unit != unit;
	}

	public static boolean isRollable(Coord coord, Map map, Unit unit) {
		if (isFilled(coord, map, unit))
			return false;
		// The unit can't take support on itself
		return map.isOnFloor(coord) && map.gridGet(coord.face(Face.DOWN)).unit != unit;
	}

	// =========================================================================================================================
}
