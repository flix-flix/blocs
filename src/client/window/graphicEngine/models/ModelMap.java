package client.window.graphicEngine.models;

import java.util.ArrayList;

import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.calcul.Matrix;
import client.window.graphicEngine.calcul.Point3D;
import client.window.graphicEngine.structures.Draw;
import client.window.graphicEngine.structures.Model;
import data.ItemTable;
import data.enumeration.Face;
import data.enumeration.ItemID;
import data.map.AbstractMap;
import data.map.Chunk;
import data.map.Cube;
import utils.Tuple;

public class ModelMap extends AbstractMap<ModelChunk, ModelCube> implements Model {

	public Engine engine;

	// ===== Model =====
	public boolean visible = true;
	private ArrayList<Draw> draws = new ArrayList<>();

	// ======== Parameters ========
	// Range of visibles chunks
	int range = 3;

	// ======== Temp ========
	// Coord of the bloc after initFaceCoord
	static private int xFace, yFace, zFace;

	// =========================================================================================================================

	@Override
	public ModelChunk createChunk(int x, int y) {
		return new ModelChunk(x, y);
	}

	@Override
	protected ModelCube createCube(Cube c) {
		return new ModelCube(c);
	}

	@Override
	protected ModelCube createCube(int x, int y, int z, ItemID itemID) {
		return new ModelCube(new Cube(x, y, z, itemID));
	}

	// =========================================================================================================================

	public void gridSet(ModelCube cube) {
		super.gridSet(cube);
		update(cube.x, cube.y, cube.z);
	}

	public boolean _gridAdd(ModelCube cube) {
		if (super._gridAdd(cube)) {
			update(cube.x, cube.y, cube.z);
			return true;
		}
		return false;
	}

	public void removeGrid(Cube c) {
		super.gridRemove(c.x, c.y, c.z);

		for (Face face : Face.faces)
			checkHideFaceRemove(c.x, c.y, c.z, face);

		checkIfAroundVisible(c.x, c.y, c.z);
	}

	public void update(int x, int y, int z) {
		for (Face face : Face.faces)
			checkHideFaceAdd(x, y, z, face);

		checkIfAroundVisible(x, y, z);
	}

	public void update(ModelCube cube) {
		update(cube.x, cube.y, cube.z);
	}

	public void update(Tuple tuple) {
		update(tuple.x, tuple.y, tuple.z);
	}

	// =========================================================================================================================

	public boolean isOpaque(int x, int y, int z) {
		return gridContains(x, y, z) && ItemTable.isOpaque(gridGet(x, y, z).itemID) && !gridGet(x, y, z).preview;
	}

	// =========================================================================================================================

	public void checkIfAroundVisible(Cube cube) {
		checkIfAroundVisible((int) cube.x, (int) cube.y, (int) cube.z);
	}

	public void checkIfAroundVisible(int x, int y, int z) {
		checkIfCubeVisible(x, y, z);

		checkIfCubeVisible(x + 1, y, z);
		checkIfCubeVisible(x - 1, y, z);
		checkIfCubeVisible(x, y + 1, z);
		checkIfCubeVisible(x, y - 1, z);
		checkIfCubeVisible(x, y, z + 1);
		checkIfCubeVisible(x, y, z - 1);
	}

	public void checkIfCubeVisible(int x, int y, int z) {
		if (!gridContains(x, y, z))
			return;

		gridGet(x, y, z).setVisible(!(isOpaque(x + 1, y, z) && isOpaque(x - 1, y, z) && isOpaque(x, y + 1, z)
				&& isOpaque(x, y - 1, z) && isOpaque(x, y, z + 1) && isOpaque(x, y, z - 1)));
	}

	/**
	 * Bloc x, y, z must exist
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param face
	 */
	public void checkHideFaceAdd(int x, int y, int z, Face face) {
		initFaceCoord(x, y, z, face);

		if (Chunk.wrongY(y))
			return;

		if (isOpaque(xFace, yFace, zFace))
			gridGet(x, y, z).hideFace[face.ordinal()] = true;
		else
			gridGet(x, y, z).hideFace[face.ordinal()] = false;

		if (gridContains(xFace, yFace, zFace))
			if (isOpaque(x, y, z))
				gridGet(xFace, yFace, zFace).hideFace[face.opposite(face)] = true;
			else
				gridGet(xFace, yFace, zFace).hideFace[face.opposite(face)] = false;
	}

	/**
	 * Bloc x, y, z mustn't exist
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param face
	 */
	public void checkHideFaceRemove(int x, int y, int z, Face face) {
		initFaceCoord(x, y, z, face);
		if (gridContains(xFace, yFace, zFace))
			gridGet(xFace, yFace, zFace).hideFace[face.opposite(face)] = false;
	}

	// =========================================================================================================================

	public void initFaceCoord(int x, int y, int z, Face face) {
		xFace = x;
		yFace = y;
		zFace = z;
		switch (face) {
		case UP:
			yFace++;
			break;
		case DOWN:
			yFace--;
			break;
		case NORTH:
			xFace++;
			break;
		case SOUTH:
			xFace--;
			break;
		case EAST:
			zFace++;
			break;
		case WEST:
			zFace--;
			break;
		}
	}

	// =========================================================================================================================

	@Override
	public ArrayList<Draw> getDraws() {
		draws.clear();

		int camChunkX = Chunk.toChunkCoord(engine.camera.vue.x);
		int camChunkZ = Chunk.toChunkCoord(engine.camera.vue.z);

		for (int x = -range; x < range + 1; x++)
			for (int z = -range; z < range + 1; z++)
				if (_containsChunk(camChunkX + x, camChunkZ + z)) {
					draws.addAll(_getChunk(camChunkX + x, camChunkZ + z).getDraws());
					engine.nbChunks++;
				}

		engine.nbCubes = draws.size();

		return draws;
	}

	@Override
	public void init(Point3D camera, Matrix matrice) {
		int camChunkX = Chunk.toChunkCoord(engine.camera.vue.x);
		int camChunkZ = Chunk.toChunkCoord(engine.camera.vue.z);

		for (int x = -range; x < range + 1; x++)
			for (int z = -range; z < range + 1; z++)
				if (_containsChunk(camChunkX + x, camChunkZ + z))
					_getChunk(camChunkX + x, camChunkZ + z).init(camera, matrice);
	}

	// =========================================================================================================================

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
