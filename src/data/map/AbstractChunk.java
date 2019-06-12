package data.map;

import java.util.HashSet;

public abstract class AbstractChunk<C extends Cube> {

	// Size of a chunk
	public static final int X = 10, Y = 50, Z = 10;

	// Index of the chunk
	public int x, z;

	// Array [X][Y][Z] to store the blocs
	protected Cube[][][] grid;

	// Set to store the cubes
	public HashSet<C> cubes = new HashSet<>();

	// =========================================================================================================================

	public AbstractChunk(int x, int z) {
		this.x = x;
		this.z = z;
	}

	// =========================================================================================================================

	protected abstract C _gridGet(int x, int y, int z);

	protected void _gridSet(int x, int y, int z, C cube) {
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

	public static boolean wrongY(double y) {
		return wrongY((int) y);
	}

	// =========================================================================================================================

	public C gridGet(int x, int y, int z) {
		if (wrongY(y))
			return null;

		return _gridGet(toInChunkCoord(x), y, toInChunkCoord(z));
	}

	public void gridSet(C cube) {
		if (wrongY(cube.getY()))
			return;

		_gridSet(toInChunkCoord(cube.x), (int) cube.getY(), toInChunkCoord(cube.z), cube);
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

	public boolean gridAdd(C cube) {
		if (gridContains(cube))
			return false;

		gridSet(cube);

		return true;
	}

	public boolean gridContains(C cube) {
		return gridContains((int) cube.getX(), (int) cube.getY(), (int) cube.getZ());
	}

	// =========================================================================================================================

	public void addCube(C cube) {
		cubes.add(cube);
	}

	public void removeCube(C cube) {
		cubes.remove(cube);
	}

	// =========================================================================================================================

	/**
	 * Returns the coordinate "in the chunk" [0-10] of the coordinate
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
