package data.map;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

import data.dynamic.Tickable;
import data.map.buildings.Building;
import data.map.multiblocs.MultiBloc;
import data.map.units.Unit;

public class Map implements Tickable, Serializable {
	private static final long serialVersionUID = 6432334760443087967L;

	protected String name = "Default Map Name";

	/** Chunks of the map (see {@link Map#getNumero(int, int)}) */
	protected HashMap<Integer, Chunk> chunks = new HashMap<>();

	/** Used to update references on cast */
	protected HashMap<Integer, MultiBloc> multis = new HashMap<>();

	protected HashMap<Integer, Cube> units = new HashMap<>();
	protected HashMap<Integer, Cube> builds = new HashMap<>();

	// =========================================================================================================================

	public Map() {
	}

	public Map(Map map) {
		copyFromMap(map);
	}

	// =========================================================================================================================

	public void copyFromMap(Map map) {
		name = map.getName();

		for (int index : map.getChunks().keySet())
			chunks.put(index, createChunk(map.getChunks().get(index)));

		for (Cube c : map.getUnits().values())
			units.put(c.unit.getId(), createUnit(c.unit));

		for (Cube c : map.getBuilds().values())
			builds.put(c.build.getId(), createBuilding(c.build));

		multis = map.multis;

		for (MultiBloc multi : multis.values()) {
			LinkedList<Cube> list = new LinkedList<>();

			for (Cube c : multi.list)
				list.add(get(c));

			multi.list = list;
		}
	}

	// =========================================================================================================================

	/** Returns the index of the chunk in the HashMap */
	protected int getNumero(int x, int z) {
		return x + (z * 10000);
	}

	// =========================================================================================================================

	public Chunk createChunk(int x, int z) {
		return new Chunk(x, z);
	}

	public Chunk createChunk(Chunk chunk) {
		return new Chunk(chunk);
	}

	protected Cube createCube(Cube cube) {
		return cube;
	}

	protected Cube createUnit(Unit unit) {
		return new Cube(unit);
	}

	protected Cube createBuilding(Building build) {
		return build.getCube();
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
			MultiBloc m = cube.multibloc;
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

	public Cube get(Cube c) {
		if (c.onGrid)
			return gridGet(c.gridCoord);
		else
			for (Cube cc : getChunkAtCoord(c).cubes)
				if (c.equals(cc))
					return cc;

		return null;
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
	 *            - true : add blocs only if all can be added. false : add blocs
	 *            where grid is empty.
	 * @return true if the multibloc have been added
	 */
	protected boolean addMulti(MultiBloc multi, boolean full) {
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

		if (full && !all)
			addMultiError(multi);
		else
			multis.put(multi.getId(), multi);

		return all;
	}

	protected void addMultiError(MultiBloc multi) {
		removeMulti(multi);
	}

	protected void removeMulti(MultiBloc multi) {
		multis.remove(multi.getId());
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

	/** Retruns the added cube or null if it can't be added */
	protected Cube gridAdd(Cube cube) {
		if (gridContains(cube.gridCoord))
			return null;

		Cube c = createCube(cube);

		if (getChunkAtCoord(cube).gridSet(c))
			return c;

		return null;
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

	public void gridRemove(Coord tuple) {
		gridRemove(tuple.x, tuple.y, tuple.z);
	}

	public boolean gridContains(Coord tuple) {
		if (tuple == null)
			return false;
		return gridContains(tuple.x, tuple.y, tuple.z);
	}

	// =========================================================================================================================
	// Units

	public Cube gridAdd(Unit unit) {
		return gridAdd(createUnit(unit));
	}

	public void addUnit(Unit unit) {
		Cube c = createUnit(unit);
		units.put(unit.getId(), c);
		gridAdd(c);
	}

	public void removeUnit(Unit unit) {
		if (units.containsKey(unit.getId()))
			gridRemove(units.remove(unit.getId()).coords());
	}

	public Unit getUnit(int id) {
		return units.get(id).unit;
	}

	// =========================================================================================================================
	// Buildings

	public void addBuilding(Building build) {
		Cube c = createBuilding(build);
		builds.put(build.getId(), c);
		add(c);
	}

	public void removeBuilding(Building build) {
		if (builds.containsKey(build.getId()))
			remove(builds.remove(build.getId()).coords());
	}

	public Building getBuilding(int id) {
		return builds.get(id).build;
	}

	public Building getBuilding(Coord coord) {
		for (Cube cube : builds.values())
			if (cube.multibloc.contains(coord))
				return cube.build;
		return null;
	}

	// =========================================================================================================================

	public boolean isOnFloor(Coord c) {
		return gridContains(c.x, c.y - 1, c.z);
	}

	// =========================================================================================================================

	public int getPixelMapRepresentation(int x, int z) {
		return getChunkAtCoord(x, z).getHighestCubeID(x, z);
	}
	// =========================================================================================================================

	public String getName() {
		return name;
	}

	public HashMap<Integer, Chunk> getChunks() {
		return chunks;
	}

	public HashMap<Integer, Cube> getUnits() {
		return units;
	}

	public HashMap<Integer, Cube> getBuilds() {
		return builds;
	}

	// =========================================================================================================================
	// Tickable

	@Override
	public void tick() {
		for (int i : units.keySet())
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
