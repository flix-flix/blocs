package data.map;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

import data.Gamer;
import data.dynamic.Tickable;
import data.id.ItemTable;
import data.map.buildings.Building;
import data.map.units.Unit;

public class Map implements Tickable, Serializable {
	private static final long serialVersionUID = 6432334760443087967L;

	protected String name = "Default Map Name";

	protected HashMap<Integer, Gamer> gamers = new HashMap<>();

	/** Chunks of the map (see {@link Map#getNumero(int, int)}) */
	protected HashMap<Integer, Chunk> chunks = new HashMap<>();

	/** Used to update references on cast */
	protected HashMap<Integer, MultiCube> multis = new HashMap<>();

	protected HashMap<Integer, Cube> units = new HashMap<>();
	protected HashMap<Integer, Cube> builds = new HashMap<>();

	public Cube lock = new Cube(0);

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

		for (Cube c : map.getUnits().values()) {
			Cube newUnit = createUnit(c.unit);
			units.put(c.unit.getId(), newUnit);
			gridGet(newUnit.gridCoord).unit = newUnit.unit;
		}

		for (Cube c : map.getBuilds().values())
			builds.put(c.build.getId(), createBuilding(c.build));

		multis = map.multis;

		for (MultiCube multi : multis.values()) {
			LinkedList<Cube> list = new LinkedList<>();

			for (Cube c : multi.list)
				list.add(get(c));

			multi.list = list;
		}

		gamers = map.gamers;
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

	/** Returns true if the bloc has been added */
	public boolean add(Cube cube) {
		synchronized (lock) {
			if (cube.unit != null)
				return addUnit(cube.unit);
			if (cube.build != null)
				return addBuilding(cube.build);
			if (cube.multicube != null)
				return addMulti(cube.multicube, true);
			return addCube(cube) != null;
		}
	}

	public void remove(Cube cube) {
		if (containsChunkAtCoord(cube)) {
			if (cube.unit != null)
				removeUnit(cube.unit);
			else if (cube.build != null)
				removeBuilding(cube.build);
			else if (cube.multicube != null)
				removeMulti(cube.multicube);
			else
				removeCube(cube);
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

	public void remove(Coord coord) {
		remove(coord.x, coord.y, coord.z);
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
	protected boolean addMulti(MultiCube multi, boolean full) {
		return addMulti(multi, full, true);
	}

	/**
	 * 
	 * @param multi
	 *            - multibloc to add
	 * @param full
	 *            - true : add blocs only if all can be added. false : add blocs
	 *            where grid is empty.
	 * @param onGrid
	 *            - true: the cubes are added on the grid (false: added on floating
	 *            cubes)
	 * @return true if the multibloc have been added
	 */
	protected boolean addMulti(MultiCube multi, boolean full, boolean onGrid) {
		synchronized (lock) {
			// All blocs have been added
			boolean all = true;
			LinkedList<Cube> added = new LinkedList<>();

			Cube c;
			while ((c = multi.list.pollFirst()) != null)
				if (!c.onGrid || !gridContains(c.gridCoord)) {
					if (onGrid)
						added.add(addCube(c));
					else {
						Cube created = createCube(c);
						getChunkAtCoord(c).addCube(created);
						added.add(created);
					}
				} else
					all = false;

			multi.valid = all;
			multi.list = added;
			multis.put(multi.getId(), multi);

			if (full && !all)
				addMultiError(multi);

			return all;
		}
	}

	protected void addMultiError(MultiCube multi) {
		removeMulti(multi);
	}

	protected void removeMulti(MultiCube multi) {
		multis.remove(multi.getId());
		for (Cube c : multi.list)
			removeCube(c);
	}

	// =========================================================================================================================
	// Cubes Add/Remove

	private Cube addCube(Cube cube) {
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

	/** Returns the added cube or null if it can't be added */
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
	// Allow Coord for coordinates

	public Cube gridGet(Coord coord) {
		return gridGet(coord.x, coord.y, coord.z);
	}

	public void gridRemove(Coord coord) {
		gridRemove(coord.x, coord.y, coord.z);
	}

	public boolean gridContains(Coord coord) {
		if (coord == null)
			return false;
		return gridContains(coord.x, coord.y, coord.z);
	}

	// =========================================================================================================================
	// Units

	/** Use this function to move unit */
	public Cube gridAdd(Unit unit) {
		return gridAdd(createUnit(unit));
	}

	/**
	 * <strong>/!\ This function must only be used one time by unit</strong><br>
	 * (to move units use {@link #gridRemove(Coord)} and {@link #gridAdd(Unit)})
	 * 
	 * @return true if the unit has been added
	 */
	public boolean addUnit(Unit unit) {
		Cube c = createUnit(unit);
		if ((c = gridAdd(c)) != null) {
			units.put(unit.getId(), c);
			return true;
		}
		return false;
	}

	public void removeUnit(Unit unit) {
		if (units.containsKey(unit.getId()))
			gridRemove(units.remove(unit.getId()).unit.coord);
	}

	public Unit getUnit(int id) {
		return units.get(id).unit;
	}

	public Cube getUnitCube(int id) {
		return units.get(id);
	}

	// =========================================================================================================================
	// Buildings

	/** Returns true if the building has been added */
	public boolean addBuilding(Building build) {
		Cube c = createBuilding(build);
		builds.put(build.getId(), c);
		return addMulti(build.getMulti(), true);
	}

	public void removeBuilding(Building build) {
		if (builds.containsKey(build.getId()))
			removeMulti(builds.remove(build.getId()).build.getMulti());
	}

	public Building getBuilding(int id) {
		return builds.get(id).build;
	}

	public Building getBuilding(Coord coord) {
		for (Cube cube : builds.values())
			if (cube.multicube.contains(coord))
				return cube.build;
		return null;
	}

	// =========================================================================================================================
	// Gamers

	public void addGamer(Gamer gamer) {
		gamers.put(gamer.getNumber(), gamer);
	}

	public Gamer getGamer(int index) {
		return gamers.get(index);
	}

	// =========================================================================================================================

	public boolean isOnFloor(Coord c) {
		return gridContains(c.x, c.y - 1, c.z) && ItemTable.isFloor(gridGet(c.x, c.y - 1, c.z).getItemID());
	}

	// =========================================================================================================================

	public Cube getPixelMapRepresentation(int x, int z) {
		return getChunkAtCoord(x, z).getHighestCube(x, z);
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
