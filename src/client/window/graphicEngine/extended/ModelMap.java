package client.window.graphicEngine.extended;

import java.util.ArrayList;
import java.util.LinkedList;

import client.window.graphicEngine.calcul.Camera;
import client.window.graphicEngine.calcul.Matrix;
import client.window.graphicEngine.structures.Draw;
import client.window.graphicEngine.structures.Model;
import data.id.ItemTable;
import data.map.Chunk;
import data.map.Coord;
import data.map.Cube;
import data.map.Map;
import data.map.enumerations.Face;
import data.map.multiblocs.MultiBloc;
import data.map.units.Unit;

public class ModelMap extends Map implements Model {
	private static final long serialVersionUID = 1111592162081077768L;

	// ===== Model =====
	public boolean visible = true;
	private ArrayList<Draw> draws = new ArrayList<>();

	// ======== Parameters ========
	/** Range of chunks to draw */
	int range = 4;

	// ===== Infos =====
	public int nbFaces, nbChunks;

	// =========================================================================================================================

	public ModelMap() {
	}

	public ModelMap(Map map) {
		name = map.getName();
		for (int index : map.getChunks().keySet()) {
			chunks.put(index, new ModelChunk(map.getChunks().get(index)));
			updateChunk(chunks.get(index).x, chunks.get(index).z);
		}

		for (Cube u : map.getUnits())
			units.add(new ModelCube(u));

		multis = map.multis;

		for (MultiBloc multi : multis) {
			LinkedList<Cube> list = new LinkedList<>();

			for (Cube c : multi.list) {
				if (!(get(c) instanceof ModelCube))
					System.out.println(c.toString());
				list.add(get(c));
			}

			multi.list = list;
		}

	}

	private void updateChunk(int xx, int zz) {
		for (int x = xx * 10; x < xx * 10 + 10; x++)
			for (int y = 0; y < Chunk.Y; y++)
				for (int z = zz * 10; z < zz * 10 + 10; z++)
					update(x, y, z);
	}

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

	@Override
	protected ModelCube createUnit(Unit unit) {
		return new ModelCube(super.createUnit(unit));
	}

	// =========================================================================================================================
	// Getters : cast Cube to ModelCube

	@Override
	public ModelCube gridGet(Coord t) {
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
		update(cube.gridCoord);
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
	protected void addMultiError(MultiBloc multi) {
		multi.valid = false;
	}

	@Override
	protected boolean addMulti(MultiBloc multi, boolean full) {
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

	public void setTargetable(ModelCube cube, boolean b) {
		cube.setTargetable(b);
	}

	// =========================================================================================================================
	// Allow coords for status modifications

	public void setHighlight(Coord tuple, boolean b) {
		setHighlight(gridGet(tuple), b);
	}

	public void setPreview(Coord tuple, boolean b) {
		setPreview(gridGet(tuple), b);
	}

	public void setTargetable(Coord tuple, boolean b) {
		setTargetable(gridGet(tuple), b);
	}

	// =========================================================================================================================

	private void update(Coord t) {
		update(t.x, t.y, t.z);
	}

	private void _update(Coord t) {
		_update(t.x, t.y, t.z);
	}

	private void update(int x, int y, int z) {
		if (gridContains(x, y, z) && gridGet(x, y, z).multibloc != null)
			for (Cube c : gridGet(x, y, z).multibloc.list)
				_update(c.gridCoord);
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
				Coord t = new Coord(x, y, z).face(face);
				gridGet(x, y, z).hideFace[face.ordinal()] = isOpaque(x, y, z, t.x, t.y, t.z);
			}

		checkIfCubeVisible(x, y, z);
	}

	/** Update visibility of blocs and faces around coords */
	private void updateAround(int x, int y, int z) {
		for (Face face : Face.faces) {
			Coord t = new Coord(x, y, z).face(face);
			if (gridContains(t)) {
				gridGet(t).hideFace[face.opposite()] = isOpaque(t.x, t.y, t.z, x, y, z);
				checkIfCubeVisible(t.x, t.y, t.z);
			}
		}
	}

	/** Update the visibility of the bloc */
	private void checkIfCubeVisible(int x, int y, int z) {
		if (!gridContains(x, y, z))
			return;

		gridGet(x, y, z).setVisible(!(isOpaque(x, y, z, x + 1, y, z) && isOpaque(x, y, z, x - 1, y, z)
				&& isOpaque(x, y, z, x, y + 1, z) && isOpaque(x, y, z, x, y - 1, z) && isOpaque(x, y, z, x, y, z + 1)
				&& isOpaque(x, y, z, x, y, z - 1)));
	}

	// =========================================================================================================================

	/** Returns true if there is an opaque bloc at coords x,y,z */
	private boolean isOpaque(int x1, int y1, int z1, int x2, int y2, int z2) {
		// Must contain a cube to be opaque
		return gridContains(x2, y2, z2)
				// The cube must be opaque...
				&& (ItemTable.isOpaque(gridGet(x2, y2, z2).itemID)
						// ... or having the same ID than the next one
						|| gridGet(x1, y1, z1).itemID == gridGet(x2, y2, z2).itemID)
				// The cube musn't be a preview ... or the next one must be one too
				&& (!gridGet(x2, y2, z2).isPreview() || gridGet(x1, y1, z1).isPreview());
	}

	// =========================================================================================================================

	@Override
	public ArrayList<Draw> getDraws(Camera camera) {
		draws.clear();

		int camChunkX = toChunkCoord(camera.vue.x);
		int camChunkZ = toChunkCoord(camera.vue.z);

		for (int x = -range; x <= range; x++)
			for (int z = -range; z <= range; z++)
				if (_containsChunk(camChunkX + x, camChunkZ + z)) {
					draws.addAll(_getChunk(camChunkX + x, camChunkZ + z).getDraws(camera));
					nbChunks++;
				}

		for (Cube u : units)
			draws.addAll(((ModelCube) u).getDraws(camera));
		nbFaces = draws.size();

		return draws;
	}

	@Override
	public void init(Camera camera, Matrix matrice) {
		int camChunkX = toChunkCoord(camera.vue.x);
		int camChunkZ = toChunkCoord(camera.vue.z);

		for (int x = -range; x <= range; x++)
			for (int z = -range; z <= range; z++)
				if (_containsChunk(camChunkX + x, camChunkZ + z))
					_getChunk(camChunkX + x, camChunkZ + z).init(camera, matrice);

		for (Cube u : units)
			((ModelCube) u).init(camera, matrice);
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
