package data.map;

import java.util.HashMap;
import java.util.LinkedList;

import client.session.Tickable;
import data.map.units.Unit;
import data.multiblocs.Multibloc;
import utils.Coord;

public class Map implements Tickable {

	String nom = "Default Map Name";

	// Chunks of the map (see getNumero(x, z))
	private HashMap<Integer, Chunk> chunks = new HashMap<>();

	protected LinkedList<Cube> units = new LinkedList<>();

	// =========================================================================================================================

	/** Returns the index of the chunk in the HashMap */
	protected int getNumero(int x, int z) {
		return x + (z * 10000);
	}

	// =========================================================================================================================

	protected Chunk createChunk(int x, int z) {
		return new Chunk(x, z);
	}

	protected Cube createCube(Cube cube) {
		return cube;
	}

	protected Cube createUnit(Unit unit) {
		return new Cube(unit);
	}

	// =========================================================================================================================
	// Chunks access with chunk's coord (-> use ...ChunkAtCoord)

	protected Chunk _getChunk(int x, int z) {
		return chunks.get(getNumero(x, z));
	}

	protected void _setChunk(int x, int z, Chunk chunk) {
		chunks.put(getNumero(x, z), chunk);
	}

	protected void _removeChunk(int x, int z) {
		chunks.remove(getNumero(x, z));
	}

	protected boolean _containsChunk(int x, int z) {
		return chunks.containsKey(getNumero(x, z));
	}

	// =========================================================================================================================

	public Chunk getChunkAtCoord(double x, double z) {
		int _x = toChunkCoord(x);
		int _z = toChunkCoord(z);

		if (!_containsChunk(_x, _z))
			_setChunk(_x, _z, createChunk(_x, _z));
		return _getChunk(_x, _z);
	}

	public Chunk getChunkAtCoord(Cube cube) {
		return getChunkAtCoord(cube.x, cube.z);
	}

	// =========================================================================================================================

	public boolean containsChunkAtCoord(double x, double z) {
		return _containsChunk(toChunkCoord(x), toChunkCoord(z));
	}

	public boolean containsChunkAtCoord(Cube cube) {
		return containsChunkAtCoord(cube.x, cube.z);
	}

	// =========================================================================================================================
	// Universal Add/Remove

	public boolean add(Cube cube) {
		if (cube.multibloc == null)
			return addCube(cube) != null;
		return addMulti(cube.multibloc, true);
	}

	public void remove(Cube cube) {
		if (containsChunkAtCoord(cube)) {
			Multibloc m = cube.multibloc;
			if (m == null)
				removeCube(cube);
			else
				removeMulti(m);
		}
	}

	public void set(Cube cube) {
		remove(cube);
		add(cube);
	}

	// =========================================================================================================================
	// Allow coords

	public void remove(int x, int y, int z) {
		if (gridContains(x, y, z))
			remove(gridGet(x, y, z));
	}

	public void remove(Coord tuple) {
		remove(tuple.x, tuple.y, tuple.z);
	}

	// =========================================================================================================================
	// Multiblocs Add/Remove

	/**
	 * 
	 * @param multi
	 *            - multibloc to add
	 * @param full
	 *            true : add blocs only if all can be added. false : add blocs where
	 *            grid is empty
	 * @return true if the multibloc have been added
	 */
	protected boolean addMulti(Multibloc multi, boolean full) {
		// All blocs have been added
		boolean all = true;
		LinkedList<Cube> added = new LinkedList<>();

		Cube c;
		while ((c = multi.list.pollFirst()) != null)
			if ((c = addCube(c)) != null)
				added.add(c);
			else
				all = false;

		multi.list = added;

		if (full && !all) {
			addMultiError(multi);
		}

		return all;
	}

	protected void addMultiError(Multibloc multi) {
		removeMulti(multi);
	}

	protected void removeMulti(Multibloc multi) {
		for (Cube c : multi.list)
			removeCube(c);
	}

	// =========================================================================================================================
	// Cubes Add/Remove

	protected Cube addCube(Cube cube) {
		if (cube.onGrid)
			return gridAdd(cube);

		Cube c = createCube(cube);
		getChunkAtCoord(cube).addCube(c);

		return c;
	}

	protected void removeCube(Cube cube) {
		if (cube.onGrid)
			gridRemove(cube.gridCoord);
		else
			getChunkAtCoord(cube).removeCube(cube);
	}

	// =========================================================================================================================
	// Grid Access (transfer actions to the right Chunk)

	public Cube gridGet(int x, int y, int z) {
		return getChunkAtCoord(x, z).gridGet(x, y, z);
	}

	protected Cube gridAdd(Cube cube) {
		if (gridContains(cube.gridCoord))
			return null;

		Cube c = createCube(cube);
		getChunkAtCoord(cube).gridSet(c);

		return c;
	}

	protected void gridRemove(int x, int y, int z) {
		if (containsChunkAtCoord(x, z))
			getChunkAtCoord(x, z).gridRemove(x, y, z);
	}

	public boolean gridContains(int x, int y, int z) {
		if (containsChunkAtCoord(x, z))
			return getChunkAtCoord(x, z).gridContains(x, y, z);
		return false;
	}

	// =========================================================================================================================
	// Allow tuple for coordinates

	public Cube gridGet(Coord tuple) {
		return gridGet(tuple.x, tuple.y, tuple.z);
	}

	protected void gridRemove(Coord tuple) {
		gridRemove(tuple.x, tuple.y, tuple.z);
	}

	public boolean gridContains(Coord tuple) {
		if (tuple == null)
			return false;
		return gridContains(tuple.x, tuple.y, tuple.z);
	}

	// =========================================================================================================================
	// Units

	public void addUnit(Unit unit) {
		Cube c = createUnit(unit);
		units.add(c);
		gridAdd(c);
	}

	public void removeUnit(Unit unit) {
		for (Cube c : units)
			if (c.unit == unit) {
				units.remove(c);
				gridRemove(c.coords());
				return;
			}
	}

	// =========================================================================================================================

	public boolean isOnFloor(Coord c) {
		return gridContains(c.x, c.y - 1, c.z);
	}

	// =========================================================================================================================
	// Tickable

	@Override
	public void tick() {
		for (int i = 0; i < units.size(); i++)
			units.get(i).unit.tick(this);
	}

	// =========================================================================================================================

	/**
	 * Returns the index of the chunk containing the coordinate
	 * 
	 * @param x
	 * @return
	 */
	public static int toChunkCoord(double x) {
		return (int) ((x < 0 ? x - 10 : x) / 10);
	}
}
