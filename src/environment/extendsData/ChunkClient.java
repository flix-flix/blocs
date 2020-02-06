package environment.extendsData;

import java.util.ArrayList;

import data.map.Chunk;
import data.map.Cube;
import graphicEngine.calcul.Camera;
import graphicEngine.calcul.Engine;
import graphicEngine.calcul.Matrix;
import graphicEngine.structures.Drawable;
import graphicEngine.structures.Modelisable;

public class ChunkClient extends Chunk implements Modelisable {
	private static final long serialVersionUID = 7534416367996448344L;

	public Engine moteur;

	// ========== Model ===========
	public boolean visible = true;
	private ArrayList<Drawable> draws = new ArrayList<>();

	// =========================================================================================================================

	public ChunkClient(int x, int z) {
		super(x, z);
		grid = new CubeClient[X][Y][Z];
	}

	public ChunkClient(Chunk chunk) {
		this(chunk.x, chunk.z);
		for (int x = 0; x < X; x++)
			for (int y = 0; y < Y; y++)
				for (int z = 0; z < Z; z++)
					grid[x][y][z] = chunk.grid[x][y][z] == null ? null : new CubeClient(chunk.grid[x][y][z]);

		for (Cube c : chunk.cubes)
			cubes.add(new CubeClient(c));
	}

	// =========================================================================================================================

	@Override
	public ArrayList<Drawable> getDraws(Camera camera) {
		draws.clear();

		for (int x = 0; x < X; x++)
			for (int y = 0; y < Y; y++)
				for (int z = 0; z < Z; z++)
					if (grid[x][y][z] != null)
						if (((CubeClient) grid[x][y][z]).isVisible())
							if (((CubeClient) grid[x][y][z]).unit == null)// Ignore unit (added by map)
								draws.addAll(((CubeClient) grid[x][y][z]).getDraws(camera));

		for (Cube cube : cubes)
			draws.addAll(((CubeClient) cube).getDraws(camera));

		return draws;
	}

	@Override
	public void init(Camera camera, Matrix matrix) {
		for (int x = 0; x < X; x++)
			for (int y = 0; y < Y; y++)
				for (int z = 0; z < Z; z++)
					if (grid[x][y][z] != null)
						((CubeClient) grid[x][y][z]).init(camera, matrix);

		for (Cube cube : cubes)
			((CubeClient) cube).init(camera, matrix);
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
