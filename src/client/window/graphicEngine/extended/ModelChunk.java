package client.window.graphicEngine.extended;

import java.util.ArrayList;

import client.textures.TexturePack;
import client.window.graphicEngine.calcul.Camera;
import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.calcul.Matrix;
import client.window.graphicEngine.structures.Draw;
import client.window.graphicEngine.structures.Model;
import data.map.Chunk;
import data.map.Cube;

public class ModelChunk extends Chunk implements Model {
	private static final long serialVersionUID = 7534416367996448344L;

	public Engine moteur;

	// ========== Model ===========
	public boolean visible = true;
	private ArrayList<Draw> draws = new ArrayList<>();

	// =========================================================================================================================

	public ModelChunk(int x, int z) {
		super(x, z);
		grid = new ModelCube[X][Y][Z];
	}

	public ModelChunk(Chunk chunk, TexturePack texturePack) {
		this(chunk.x, chunk.z);
		for (int x = 0; x < X; x++)
			for (int y = 0; y < Y; y++)
				for (int z = 0; z < Z; z++)
					grid[x][y][z] = chunk.grid[x][y][z] == null ? null
							: new ModelCube(chunk.grid[x][y][z], texturePack);

		for (Cube c : chunk.cubes)
			cubes.add(new ModelCube(c, texturePack));
	}

	// =========================================================================================================================

	@Override
	public ArrayList<Draw> getDraws(Camera camera) {
		draws.clear();

		for (int x = 0; x < X; x++)
			for (int y = 0; y < Y; y++)
				for (int z = 0; z < Z; z++)
					if (grid[x][y][z] != null)
						if (((ModelCube) grid[x][y][z]).isVisible())
							if (((ModelCube) grid[x][y][z]).unit == null)// Ignore unit (added by map)
								draws.addAll(((ModelCube) grid[x][y][z]).getDraws(camera));

		for (Cube cube : cubes)
			draws.addAll(((ModelCube) cube).getDraws(camera));

		return draws;
	}

	@Override
	public void init(Camera camera, Matrix matrix) {
		for (int x = 0; x < X; x++)
			for (int y = 0; y < Y; y++)
				for (int z = 0; z < Z; z++)
					if (grid[x][y][z] != null)
						((ModelCube) grid[x][y][z]).init(camera, matrix);

		for (Cube cube : cubes)
			((ModelCube) cube).init(camera, matrix);
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
