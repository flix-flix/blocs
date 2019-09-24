package data.map.units;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import data.enumeration.Face;
import data.map.Map;
import utils.Coord;

public class PathFinding {

	/**
	 * Path-finding algorithm
	 * 
	 * Generates a list of Coords linking current coords to the indicated location
	 */
	public static LinkedList<Coord> generatePathTo(Unit unit, Map map, Coord start, Coord end) {
		// List of the coords to the destination
		LinkedList<Coord> path;

		// Test if the destination is walkable
		if (!map.isOnFloor(end))
			return null;

		HashMap<Integer, Integer> travelled = new HashMap<>();
		travelled.put(start.getId(), 0);

		Queue<Coord> queue = new LinkedList<>();
		queue.add(start);

		// === Setting values on cubes ===
		Coord current, next;
		while ((current = queue.poll()) != null) {
			if (current.equals(end))
				break;

			for (Face f : Face.faces) {
				next = current.face(f);

				// Test if the bloc is empty and if it hasn't already be travelled
				if ((map.gridContains(next) && map.gridGet(next).unit != unit) || travelled.containsKey(next.getId()))
					continue;

				// The unit can only do one step without touching the floor
				if (!map.isOnFloor(current) && !map.isOnFloor(next))
					continue;

				travelled.put(next.getId(), travelled.get(current.getId()) + 1);
				queue.add(next);
			}
		}

		// End point not reached
		if (!travelled.containsKey(end.getId()))
			return null;

		// === Finding path from values ===
		path = new LinkedList<>();
		path.add(end);

		while (!(current = path.getFirst()).equals(start))
			// %2 generate diagonal path
			findPrevious(path.size() % 2 == 0 ? Face.faces : Face.facesReverse, current, travelled, path, map);

		path.removeFirst(); // Remove the start
		return path;
	}

	// =========================================================================================================================

	private static void findPrevious(Face[] faces, Coord current, HashMap<Integer, Integer> travelled,
			LinkedList<Coord> path, Map map) {
		for (Face f : faces) {
			Coord next = current.face(f);

			// The bloc must be closer to the start
			if (travelled.containsKey(next.getId()) && travelled.get(next.getId()) < travelled.get(current.getId())) {
				// The unit can only do one step without touching the floor
				if (!map.isOnFloor(current) && !map.isOnFloor(next))
					continue;

				path.addFirst(next);
				break;
			}
		}
	}
}
