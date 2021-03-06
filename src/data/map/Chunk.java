package data.map;

import java.io.Serializable;
import java.util.HashSet;

import data.id.ItemID;

public class Chunk implements Serializable {
	private static final long serialVersionUID = -8763834020086820609L;

	public static final Cube VOID = new Cube(-1, -1, -1, ItemID.ERROR);

	// Size of a chunk
	public static final int X = 10, Y = 50, Z = 10;

	// Index of the chunk
	public int x, z;

	// Array [X][Y][Z] to store the blocs
	public Cube[][][] grid = new Cube[X][Y][Z];

	// Set to store the off-grid cubes
	public HashSet<Cube> cubes = new HashSet<>();

	// =========================================================================================================================

	public Chunk(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public Chunk(Chunk chunk) {
		this(chunk.x, chunk.z);

		grid = chunk.grid;
		cubes = chunk.cubes;
	}

	// =========================================================================================================================
	// Grid access without X, Y, Z verification

	protected Cube _gridGet(int x, int y, int z) {
		return grid[x][y][z];
	}

	protected void _gridSet(int x, int y, int z, Cube cube) {
		grid[x][y][z] = cube;
	}

	protected void _gridRemove(int x, int y, int z) {
		_gridSet(x, y, z, null);
	}

	protected boolean _gridContains(int x, int y, int z) {
		return _gridGet(x, y, z) != null;
	}

	// =========================================================================================================================

	public static boolean wrongY(int y) {
		return y < 0 || y >= Y;
	}

	// =========================================================================================================================
	// grid access with X, Y, Z verification

	public Cube gridGet(int x, int y, int z) {
		if (wrongY(y))
			return null;
		return _gridGet(toInChunkCoord(x), y, toInChunkCoord(z));
	}

	/** Returns true if the blocs has been added, false otherwise */
	public boolean gridSet(Cube cube) {
		if (wrongY(cube.gridCoord.y))
			return false;
		_gridSet(toInChunkCoord(cube.x), cube.gridCoord.y, toInChunkCoord(cube.z), cube);
		return true;
	}

	public void gridRemove(int x, int y, int z) {
		if (wrongY(y))
			return;
		_gridRemove(toInChunkCoord(x), y, toInChunkCoord(z));
	}

	public boolean gridContains(int x, int y, int z) {
		if (wrongY(y))
			return false;
		return _gridContains(toInChunkCoord(x), y, toInChunkCoord(z));
	}

	// =========================================================================================================================

	public Cube getHighestCube(int _x, int _z) {
		int x = toInChunkCoord(_x), z = toInChunkCoord(_z);

		for (int y = Y - 1; y >= 0; y--)
			if (grid[x][y][z] != null)
				return grid[x][y][z];
		return VOID;
	}

	// =========================================================================================================================

	public void addCube(Cube cube) {
		cubes.add(cube);
	}

	public void removeCube(Cube cube) {
		cubes.remove(cube);
	}

	// =========================================================================================================================

	/**
	 * Returns the coordinate "in the chunk" [0-9] of the coordinate
	 * 
	 * @param x
	 * @return
	 */
	public static int toInChunkCoord(double x) {
		if (x >= 0)
			return (int) (x % 10);

		if (x % 1 == 0)
			if (x % 10 == 0)
				return 0;
			else
				return (int) (x % 10) + 10;
		return (int) (x % 10) + 9;
	}
}
