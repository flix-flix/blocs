package client.window.graphicEngine.extended;

import java.util.ArrayList;

import client.window.graphicEngine.calcul.Camera;
import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.calcul.Matrix;
import client.window.graphicEngine.structures.Draw;
import client.window.graphicEngine.structures.Model;
import data.map.Chunk;
import data.map.Cube;

public class ModelChunk extends Chunk implements Model {

	public Engine moteur;

	// ========== Model ===========
	public boolean visible = true;
	private ArrayList<Draw> draws = new ArrayList<>();

	// =========================================================================================================================

	public ModelChunk(int x, int z) {
		super(x, z);
		grid = new ModelCube[X][Y][Z];
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
							draws.addAll(((ModelCube) grid[x][y][z]).getDraws(camera));

		for (Cube cube : cubes)
			draws.addAll(((ModelCube) cube).getDraws(camera));

		return draws;
	}

	@Override
	public void init(Camera camera, Matrix matrice) {
		for (int x = 0; x < X; x++)
			for (int y = 0; y < Y; y++)
				for (int z = 0; z < Z; z++)
					if (grid[x][y][z] != null)
						((ModelCube) grid[x][y][z]).init(camera, matrice);

		for (Cube cube : cubes)
			((ModelCube) cube).init(camera, matrice);
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
