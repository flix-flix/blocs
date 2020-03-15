package environment.extendsData;

import java.util.ArrayList;

import data.id.ItemID;
import data.id.ItemTableClient;
import data.map.Chunk;
import data.map.Coord;
import data.map.Cube;
import data.map.Map;
import data.map.MultiCube;
import data.map.buildings.Building;
import data.map.enumerations.Face;
import data.map.units.Unit;
import graphicEngine.calcul.Camera;
import graphicEngine.calcul.Matrix;
import graphicEngine.structures.Drawable;
import graphicEngine.structures.Modelisable;

public class MapClient extends Map implements Modelisable {
	private static final long serialVersionUID = 1111592162081077768L;

	// =============== Model ===============
	private ArrayList<Drawable> draws = new ArrayList<>();

	// =============== Options ===============
	/** Range of chunks to draw */
	public int range = 4;

	// =============== Infos ===============
	public int nbFaces, nbChunks;

	// =========================================================================================================================

	public MapClient() {
	}

	public MapClient(Map map) {
		copyFromMap(map);

		for (int index : map.getChunks().keySet())
			updateChunk(chunks.get(index).x, chunks.get(index).z);
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
	public ChunkClient createChunk(int x, int z) {
		return new ChunkClient(x, z);
	}

	@Override
	public ChunkClient createChunk(Chunk chunk) {
		return new ChunkClient(chunk);
	}

	@Override
	protected CubeClient createCube(Cube c) {
		if (c instanceof CubeClient)
			return (CubeClient) c;
		return new CubeClient(c);
	}

	@Override
	protected CubeClient createUnit(Unit unit) {
		if (unit instanceof UnitClient)
			return new CubeClient(new Cube(unit));
		return new CubeClient(new Cube(new UnitClient(unit)));
	}

	// =========================================================================================================================
	// Cast Getters

	@Override
	public CubeClient gridGet(Coord t) {
		return (CubeClient) super.gridGet(t);
	}

	@Override
	public CubeClient gridGet(int x, int y, int z) {
		return (CubeClient) super.gridGet(x, y, z);
	}

	@Override
	public ChunkClient _getChunk(int x, int z) {
		return (ChunkClient) super._getChunk(x, z);
	}

	@Override
	public UnitClient getUnit(int unitID) {
		return (UnitClient) super.getUnit(unitID);
	}

	@Override
	public CubeClient getUnitCube(int unitID) {
		return (CubeClient) super.getUnitCube(unitID);
	}

	@Override
	public CubeClient get(Cube c) {
		return (CubeClient) super.get(c);
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
	protected void addMultiError(MultiCube multi) {
		multi.valid = false;
	}

	@Override
	protected boolean addMulti(MultiCube multi, boolean full) {
		super.addMulti(multi, full);
		// If all the cubes are out of bounds
		if (!multi.exist())
			return false;

		update(multi.getCube().coords());
		// Cubes are always displayed (in red if errors)
		return true;
	}

	// =========================================================================================================================
	// Status modifications (+update if needed)

	public void setHighlight(CubeClient cube, boolean b) {
		cube.setHighlight(b);
	}

	public void setPreview(CubeClient cube, boolean b) {
		cube.setPreview(b);
		update(cube.coords());
	}

	// =========================================================================================================================
	// Allow coords for status modifications

	public void setHighlight(Coord coord, boolean b) {
		setHighlight(gridGet(coord), b);
	}

	public void setPreview(Coord coord, boolean b) {
		setPreview(gridGet(coord), b);
	}

	// =========================================================================================================================

	public CubeClient addPreview(Cube cube) {
		synchronized (lock) {
			CubeClient created;

			Cube _cube = cube.clone();
			// ===== Delete data ===== (Will only add physical cubes)
			if (_cube.build != null) {
				_cube.build = _cube.build.clone();
				_cube.multicube = _cube.build.getMulti();
				_cube.multicube.setBuild(null);
			} else if (_cube.multicube != null) {
				_cube.multicube = _cube.multicube.cloneAndCast();
				_cube.multicube.setBuild(null);
			}
			_cube.unit = null;

			// Multi cubes
			if (_cube.multicube != null) {
				super.addMulti(_cube.multicube, true, false);
				for (Cube c : _cube.multicube.list) {
					c.onGrid = false;
					if (c.y >= Chunk.Y)
						_cube.multicube.valid = false;
				}
				created = (CubeClient) _cube.multicube.getCube();
				if (created == null)
					return null;
			}
			// Single cube
			else {
				created = createCube(_cube);
				created.onGrid = false;
				getChunkAtCoord(_cube).addCube(created);
			}

			// ===== Mark as preview =====
			created.setPreview(true);
			created.setTargetable(false);
			created.setHighlight(true);

			return created;
		}
	}

	// =========================================================================================================================

	private void update(Coord t) {
		update(t.x, t.y, t.z);
	}

	private void _update(Coord t) {
		_update(t.x, t.y, t.z);
	}

	private void update(int x, int y, int z) {
		if (gridContains(x, y, z) && gridGet(x, y, z).multicube != null)
			for (Cube c : gridGet(x, y, z).multicube.list)
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

		gridGet(x, y, z).setVisible(!(isOpaqueExceptTransparent(x, y, z, x + 1, y, z)
				&& isOpaqueExceptTransparent(x, y, z, x - 1, y, z) && isOpaqueExceptTransparent(x, y, z, x, y + 1, z)
				&& isOpaqueExceptTransparent(x, y, z, x, y - 1, z) && isOpaqueExceptTransparent(x, y, z, x, y, z + 1)
				&& isOpaqueExceptTransparent(x, y, z, x, y, z - 1)));
	}

	// =========================================================================================================================

	/**
	 * Returns true if a face of the cube at coords x,y,z is hidden by an opaque
	 * bloc at coords x2,y2,z2
	 * 
	 * <br>
	 * Faces of transparent cube are always displayed
	 */
	private boolean isOpaque(int x, int y, int z, int x2, int y2, int z2) {
		return _isOpaque(x, y, z, x2, y2, z2, true);
	}

	/**
	 * Returns true if a face of the cube at coords x,y,z is hidden by an opaque
	 * bloc at coords x2,y2,z2
	 *
	 * <br>
	 * Ignore [Faces of transparent cube are always displayed]
	 */
	private boolean isOpaqueExceptTransparent(int x, int y, int z, int x2, int y2, int z2) {
		return _isOpaque(x, y, z, x2, y2, z2, false);
	}

	/**
	 * Returns true if a face of the cube at coords x,y,z is hidden by an opaque
	 * bloc at coords x2,y2,z2
	 *
	 * @param careTransparency
	 *            - true: [Faces of transparent cube are always displayed]
	 */
	private boolean _isOpaque(int x, int y, int z, int x2, int y2, int z2, boolean careTransparency) {
		CubeClient cube = gridGet(x, y, z), adjacent = gridGet(x2, y2, z2);

		// The face must have an adjacent cube to be opaque
		if (adjacent == null)
			return false;

		int id = cube.getItemID(), aID = adjacent.getItemID();

		// Cubes with same ID hide each others (glass, water)
		if (id == aID)
			return true;

		// The cube musn't be a building in construction
		if (adjacent.build != null && !adjacent.build.isBuild())
			return false;

		// The adjacent cube must be opaque...
		if (!ItemTableClient.isOpaque(aID))
			return false;

		// A transparent bloc always show it's faces (except liquid)
		if (careTransparency && !ItemTableClient.isOpaque(id) && id != ItemID.WATER)
			return false;

		return true;
	}

	// =========================================================================================================================

	@Override
	public synchronized void setBuildingID(Building build) {
		// Leave ID Management to the server
	}

	// =========================================================================================================================
	// Model

	@Override
	public ArrayList<Drawable> getDraws(Camera camera) {
		draws.clear();
		nbChunks = 0;

		int camChunkX = toChunkCoord(camera.vue.x);
		int camChunkZ = toChunkCoord(camera.vue.z);

		for (int x = -range; x <= range; x++)
			for (int z = -range; z <= range; z++)
				if (_containsChunk(camChunkX + x, camChunkZ + z)) {
					draws.addAll(_getChunk(camChunkX + x, camChunkZ + z).getDraws(camera));
					nbChunks++;
				}

		for (Cube c : units.values())
			draws.addAll(((CubeClient) c).getDraws(camera));
		nbFaces = draws.size();

		return draws;
	}

	@Override
	public void init(Camera camera, Matrix matrix) {
		int camChunkX = toChunkCoord(camera.vue.x);
		int camChunkZ = toChunkCoord(camera.vue.z);

		for (int x = -range; x <= range; x++)
			for (int z = -range; z <= range; z++)
				if (_containsChunk(camChunkX + x, camChunkZ + z))
					_getChunk(camChunkX + x, camChunkZ + z).init(camera, matrix);

		// TODO [Fix] ConcurrentModificationException
		for (Cube c : units.values())
			((CubeClient) c).init(camera, matrix);
	}
}
