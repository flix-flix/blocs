package data.map;

import java.util.HashMap;

import client.window.graphicEngine.calcul.Point3D;
import data.enumeration.Face;
import data.enumeration.ItemID;
import utils.Tuple;

public abstract class AbstractMap<T extends AbstractChunk<C>, C extends Cube> {

	String nom = "Default Map Name";

	// Chunks of the map (see getNumero(x, z))
	private HashMap<Integer, T> chunks = new HashMap<>();

	// =========================================================================================================================

	/**
	 * Returns the index of the chunk in the HashMap
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	protected int getNumero(int x, int z) {
		return x + (z * 10000);
	}

	// =========================================================================================================================

	protected abstract T createChunk(int x, int z);

	protected abstract C createCube(int x, int y, int z, ItemID itemID);

	protected abstract C createCube(Cube cube);

	// =========================================================================================================================

	protected T _getChunk(int x, int z) {
		return chunks.get(getNumero(x, z));
	}

	protected void _setChunk(int x, int z, T chunk) {
		chunks.put(getNumero(x, z), chunk);
	}

	protected void _removeChunk(int x, int z) {
		chunks.remove(getNumero(x, z));
	}

	protected boolean _containsChunk(int x, int z) {
		return chunks.containsKey(getNumero(x, z));
	}

	// =========================================================================================================================

	public T getChunkAtCoord(double x, double z) {
		int _x = Chunk.toChunkCoord(x);
		int _z = Chunk.toChunkCoord(z);

		if (!_containsChunk(_x, _z))
			_setChunk(_x, _z, createChunk(_x, _z));
		return _getChunk(_x, _z);
	}

	// =========================================================================================================================

	public T getChunkAtCoord(Point3D pos) {
		return getChunkAtCoord(pos.x, pos.z);
	}

	public T getChunkAtCoord(Cube cube) {
		return getChunkAtCoord(cube.x, cube.z);
	}

	// =========================================================================================================================

	public boolean containsChunkAtCoord(double x, double z) {
		return _containsChunk(Chunk.toChunkCoord(x), Chunk.toChunkCoord(z));
	}

	public boolean containsChunkAtCoord(Point3D p) {
		return containsChunkAtCoord(p.x, p.z);
	}

	public boolean containsChunkAtCoord(Cube cube) {
		return containsChunkAtCoord(cube.x, cube.z);
	}

	// =========================================================================================================================

	public C gridGet(int x, int y, int z) {
		return getChunkAtCoord(x, z).gridGet(x, y, z);
	}

	public C gridGet(Tuple tuple) {
		return gridGet(tuple.x, tuple.y, tuple.z);
	}

	/**
	 * Returns the Bloc next to the indicated face
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param face
	 * @return
	 */
	public C gridGetFace(int x, int y, int z, Face face) {
		switch (face) {
		case UP:
			y++;
			break;
		case DOWN:
			y--;
			break;
		case EAST:
			z++;
			break;
		case WEST:
			z--;
			break;
		case SOUTH:
			x--;
			break;
		case NORTH:
			x++;
			break;
		}
		return gridGet(x, y, z);
	}

	public C gridGetFace(Cube cube, Face face) {
		return gridGetFace(cube.x, cube.y, cube.z, face);
	}

	// =========================================================================================================================

	public void gridSet(C cube) {
		getChunkAtCoord(cube).gridSet(createCube(cube.x, cube.y, cube.z, cube.itemID));
	}

	public void gridSet(int x, int y, int z, ItemID itemID) {
		gridSet(createCube(x, y, z, itemID));
	}

	// =========================================================================================================================

	public boolean _gridAdd(C cube) {
		if (getChunkAtCoord(cube).gridContains(cube))
			return false;

		return getChunkAtCoord(cube).gridAdd(cube);
	}

	public boolean gridAdd(int x, int y, int z, ItemID item) {
		return _gridAdd(createCube(x, y, z, item));
	}

	public boolean gridAdd(Cube cube) {
		return _gridAdd(createCube(cube));
	}

	public boolean gridAdd(Tuple tuple, ItemID item) {
		return gridAdd(tuple.x, tuple.y, tuple.z, item);
	}

	// =========================================================================================================================

	public void gridRemove(int x, int y, int z) {
		if (containsChunkAtCoord(x, z)) {
			getChunkAtCoord(x, z).gridRemove(x, y, z);
		}
	}

	public void gridRemove(C b) {
		gridRemove(b.x, b.y, b.z);
	}

	public void gridRemove(Tuple tuple) {
		gridRemove(tuple.x, tuple.y, tuple.z);
	}

	// =========================================================================================================================

	/**
	 * Add a bloc next to another
	 */
	public boolean gridAddToFace(int x, int y, int z, ItemID itemID, Face face) {
		C cube = createCube(x, y, z, itemID);
		Tuple tuple = new Tuple(cube).face(face);

		if (cube.x < tuple.x)
			cube.center.x++;
		if (cube.y < tuple.y)
			cube.center.y++;
		if (cube.z < tuple.z)
			cube.center.z++;

		if (cube.x > tuple.x)
			cube.center.x--;
		if (cube.y > tuple.y)
			cube.center.y--;
		if (cube.z > tuple.z)
			cube.center.z--;

		cube.x = tuple.x;
		cube.y = tuple.y;
		cube.z = tuple.z;

		return gridAdd(cube);
	}

	// =========================================================================================================================

	public boolean gridContains(int x, int y, int z) {
		if (containsChunkAtCoord(x, z))
			return getChunkAtCoord(x, z).gridContains(x, y, z);
		return false;
	}

	public boolean gridContains(Cube cube) {
		return gridContains((int) cube.x, (int) cube.y, (int) cube.z);
	}

	// =========================================================================================================================

	public void addCube(Cube cube) {
		_addCube(createCube(cube));
	}

	public void _addCube(C cube) {
		getChunkAtCoord(cube).addCube(cube);
	}

	public void _removeCube(C cube) {
		getChunkAtCoord(cube).removeCube(cube);
	}
}
