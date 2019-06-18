package client.window.graphicEngine.models;

import java.util.ArrayList;
import java.util.HashSet;

import client.window.graphicEngine.calcul.Engine;
import client.window.graphicEngine.calcul.Matrix;
import client.window.graphicEngine.calcul.Point3D;
import client.window.graphicEngine.structures.Draw;
import client.window.graphicEngine.structures.Model;
import data.map.Chunk;

public class ModelChunk extends Chunk implements Model {

	public Engine moteur;

	// Set to store the off-grid cubes
	public HashSet<ModelCube> cubes = new HashSet<>();

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
	public ArrayList<Draw> getDraws() {
		draws.clear();

		for (int x = 0; x < X; x++)
			for (int y = 0; y < Y; y++)
				for (int z = 0; z < Z; z++)
					if (grid[x][y][z] != null)
						if (((ModelCube) grid[x][y][z]).isVisible())
							draws.addAll(((ModelCube) grid[x][y][z]).getDraws());

		for (ModelCube cube : cubes)
			draws.addAll(cube.getDraws());

		return draws;
	}

	@Override
	public void init(Point3D camera, Matrix matrice) {
		for (int x = 0; x < X; x++)
			for (int y = 0; y < Y; y++)
				for (int z = 0; z < Z; z++)
					if (grid[x][y][z] != null)
						((ModelCube) grid[x][y][z]).init(camera, matrice);

		for (ModelCube cube : cubes)
			cube.init(camera, matrice);
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
