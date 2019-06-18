package data.map;

import java.util.HashMap;

import utils.Tuple;

public class Map {

	String nom = "Default Map Name";

	// Chunks of the map (see getNumero(x, z))
	private HashMap<Integer, Chunk> chunks = new HashMap<>();

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

	// =========================================================================================================================
	// Transfer actions to the right Chunk

	public Cube gridGet(int x, int y, int z) {
		return getChunkAtCoord(x, z).gridGet(x, y, z);
	}

	public void gridSet(Cube cube) {
		getChunkAtCoord(cube).gridSet(createCube(cube));
	}

	public boolean gridAdd(Cube cube) {
		if (gridContains(cube.x, cube.y, cube.z))
			return false;

		gridSet(cube);

		return true;
	}

	public void gridRemove(int x, int y, int z) {
		if (containsChunkAtCoord(x, z))
			getChunkAtCoord(x, z).gridRemove(x, y, z);
	}

	public boolean gridContains(int x, int y, int z) {
		if (containsChunkAtCoord(x, z))
			return getChunkAtCoord(x, z).gridContains(x, y, z);
		return false;
	}

	// =========================================================================================================================
	// Previous function but with tuple for coordinates

	public Cube gridGet(Tuple tuple) {
		return gridGet(tuple.x, tuple.y, tuple.z);
	}

	public void gridRemove(Tuple tuple) {
		gridRemove(tuple.x, tuple.y, tuple.z);
	}

	public boolean gridContains(Tuple tuple) {
		return gridContains(tuple.x, tuple.y, tuple.z);
	}

	// =========================================================================================================================

	public void addCube(Cube cube) {
		getChunkAtCoord(cube).addCube(createCube(cube));
	}

	public void removeCube(Cube cube) {
		getChunkAtCoord(cube).removeCube(cube);
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
