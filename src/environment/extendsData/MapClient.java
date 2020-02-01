package environment.extendsData;

import java.util.ArrayList;

import data.id.ItemTableClient;
import data.map.Chunk;
import data.map.Coord;
import data.map.Cube;
import data.map.Map;
import data.map.enumerations.Face;
import data.map.multiblocs.MultiBloc;
import data.map.units.Unit;
import environment.textures.TexturePack;
import graphicEngine.calcul.Camera;
import graphicEngine.calcul.Matrix;
import graphicEngine.structures.Drawable;
import graphicEngine.structures.Modelisable;

public class MapClient extends Map implements Modelisable {
	private static final long serialVersionUID = 1111592162081077768L;

	// ===== Texture =====
	TexturePack texturePack;

	// ===== Model =====
	public boolean visible = true;
	private ArrayList<Drawable> draws = new ArrayList<>();

	// ======== Parameters ========
	/** Range of chunks to draw */
	int range = 4;

	// ===== Infos =====
	public int nbFaces, nbChunks;

	// =========================================================================================================================

	public MapClient(TexturePack texturePack) {
		super();
		this.texturePack = texturePack;
	}

	public MapClient(Map map, TexturePack texturePack) {
		this(texturePack);

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
		return new ChunkClient(chunk, texturePack);
	}

	@Override
	protected CubeClient createCube(Cube c) {
		return new CubeClient(c, texturePack);
	}

	@Override
	protected CubeClient createUnit(Unit unit) {
		if (unit instanceof UnitClient)
			return new CubeClient(new Cube(unit), texturePack);
		return new CubeClient(new Cube(new UnitClient(unit)), texturePack);
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

	public void setHighlight(CubeClient cube, boolean b) {
		cube.setHighlight(b);
	}

	public void setPreview(CubeClient cube, boolean b) {
		cube.setPreview(b);
		update(cube.coords());
	}

	public void setTargetable(CubeClient cube, boolean b) {
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

	/**
	 * Returns true if a face of the cube at coords x,y,z is hidden by an opaque
	 * bloc at coords x2,y2,z2
	 */
	private boolean isOpaque(int x, int y, int z, int x2, int y2, int z2) {
		// Must contain a cube to be opaque
		return gridContains(x2, y2, z2)
				// The cube must be opaque...
				&& (ItemTableClient.isOpaque(gridGet(x2, y2, z2).getItemID())
						// ... or having the same ID than the next one
						|| gridGet(x, y, z).getItemID() == gridGet(x2, y2, z2).getItemID())
				// The cube musn't be a preview ... or the next one must be one too
				&& (!gridGet(x2, y2, z2).isPreview() || gridGet(x, y, z).isPreview())
				// The cube musn't be a building in construction
				&& !(gridGet(x2, y2, z2).build != null && !gridGet(x2, y2, z2).build.isBuild());
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

		for (Cube c : units.values())
			((CubeClient) c).init(camera, matrix);
	}

	// =========================================================================================================================
	// Modele

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
