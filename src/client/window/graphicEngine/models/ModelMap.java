package client.window.graphicEngine.models;

import java.util.ArrayList;

import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.calcul.Matrix;
import client.window.graphicEngine.calcul.Point3D;
import client.window.graphicEngine.structures.Draw;
import client.window.graphicEngine.structures.Model;
import data.ItemTable;
import data.enumeration.Face;
import data.map.Cube;
import data.map.Map;
import data.multiblocs.Multibloc;
import utils.Tuple;

public class ModelMap extends Map implements Model {

	public Engine engine;

	// ===== Model =====
	public boolean visible = true;
	private ArrayList<Draw> draws = new ArrayList<>();

	// ======== Parameters ========
	/** Range of chunks to draw */
	int range = 3;

	// =========================================================================================================================
	// Create displayable-data

	@Override
	public ModelChunk createChunk(int x, int z) {
		return new ModelChunk(x, z);
	}

	@Override
	protected ModelCube createCube(Cube c) {
		return new ModelCube(c);
	}

	// =========================================================================================================================
	// Getters : cast Cube to ModelCube

	@Override
	public ModelCube gridGet(Tuple t) {
		return (ModelCube) super.gridGet(t);
	}

	@Override
	public ModelCube gridGet(int x, int y, int z) {
		return (ModelCube) super.gridGet(x, y, z);
	}

	@Override
	public ModelChunk _getChunk(int x, int z) {
		return (ModelChunk) super._getChunk(x, z);
	}

	// =========================================================================================================================
	// Intercept grid modifications for update

	@Override
	protected Cube gridAdd(Cube cube) {
		Cube c = super.gridAdd(cube);
		update(cube.x, cube.y, cube.z);
		return c;
	}

	@Override
	protected void gridRemove(int x, int y, int z) {
		super.gridRemove(x, y, z);
		updateAround(x, y, z);
	}

	// =========================================================================================================================
	// Intercept invalid multibloc position. Force to display it with a red hue

	@Override
	protected void addMultiError(Multibloc multi) {
		multi.valid = false;
	}

	@Override
	protected boolean addMulti(Multibloc multi, boolean full) {
		super.addMulti(multi, full);
		update(multi.getCube().coords());
		// Cubes are always displayed (in red if errors)
		return true;
	}

	// =========================================================================================================================
	// Status modifications (+update if needed)

	public void setHighlight(ModelCube cube, boolean b) {
		cube.setHighlight(b);
	}

	public void setPreview(ModelCube cube, boolean b) {
		cube.setPreview(b);
		update(cube.coords());
	}

	public void setPreviewThrought(ModelCube cube, boolean b) {
		cube.setPreviewThrought(b);
	}

	// =========================================================================================================================
	// Allow coords for status modifications

	public void setHighlight(Tuple tuple, boolean b) {
		setHighlight(gridGet(tuple), b);
	}

	public void setPreview(Tuple tuple, boolean b) {
		setPreview(gridGet(tuple), b);
	}

	public void setPreviewThrought(Tuple tuple, boolean b) {
		setPreviewThrought(gridGet(tuple), b);
	}

	// =========================================================================================================================

	private void update(Tuple t) {
		update(t.x, t.y, t.z);
	}

	private void update(int x, int y, int z) {
		if (gridContains(x, y, z) && gridGet(x, y, z).multibloc != null)
			for (Cube c : gridGet(x, y, z).multibloc.list)
				_update(c.x, c.y, c.z);
		else
			_update(x, y, z);
	}

	/** Update the visibility of blocs and faces at the coords and around */
	private void _update(int x, int y, int z) {
		updateBloc(x, y, z);
		updateAround(x, y, z);
	}

	// =========================================================================================================================

	/** Update visibility of the bloc and its faces */
	private void updateBloc(int x, int y, int z) {
		if (gridContains(x, y, z))
			for (Face face : Face.faces) {
				Tuple t = new Tuple(x, y, z).face(face);
				gridGet(x, y, z).hideFace[face.ordinal()] = isOpaque(t.x, t.y, t.z);
			}

		checkIfCubeVisible(x, y, z);
	}

	/** Update visibility of blocs and faces around coords */
	private void updateAround(int x, int y, int z) {
		for (Face face : Face.faces) {
			Tuple t = new Tuple(x, y, z).face(face);
			if (gridContains(t)) {
				gridGet(t).hideFace[face.opposite()] = isOpaque(x, y, z);
				checkIfCubeVisible(t.x, t.y, t.z);
			}
		}
	}

	/** Update the visibility of the bloc */
	private void checkIfCubeVisible(int x, int y, int z) {
		if (!gridContains(x, y, z))
			return;

		gridGet(x, y, z).setVisible(!(isOpaque(x + 1, y, z) && isOpaque(x - 1, y, z) && isOpaque(x, y + 1, z)
				&& isOpaque(x, y - 1, z) && isOpaque(x, y, z + 1) && isOpaque(x, y, z - 1)));
	}

	// =========================================================================================================================

	/** Returns true if there is an opaque bloc at coords x,y,z */
	private boolean isOpaque(int x, int y, int z) {
		return gridContains(x, y, z) && ItemTable.isOpaque(gridGet(x, y, z).itemID) && !gridGet(x, y, z).isPreview();
	}

	// =========================================================================================================================

	@Override
	public ArrayList<Draw> getDraws() {
		draws.clear();

		int camChunkX = toChunkCoord(engine.camera.vue.x);
		int camChunkZ = toChunkCoord(engine.camera.vue.z);

		for (int x = -range; x <= range; x++)
			for (int z = -range; z <= range; z++)
				if (_containsChunk(camChunkX + x, camChunkZ + z)) {
					draws.addAll(_getChunk(camChunkX + x, camChunkZ + z).getDraws());
					engine.nbChunks++;
				}

		engine.nbCubes = draws.size();

		return draws;
	}

	@Override
	public void init(Point3D camera, Matrix matrice) {
		int camChunkX = toChunkCoord(engine.camera.vue.x);
		int camChunkZ = toChunkCoord(engine.camera.vue.z);

		for (int x = -range; x <= range; x++)
			for (int z = -range; z <= range; z++)
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
