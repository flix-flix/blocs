package data.map;

import java.io.Serializable;

public class Chunk extends AbstractChunk<Cube> implements Serializable {
	private static final long serialVersionUID = 2228787564547923808L;

	// =========================================================================================================================

	public Chunk(int x, int z) {
		super(x, z);
		grid = new Cube[X][Y][Z];
	}

	// =========================================================================================================================

	@Override
	public String toString() {
		return "Chunk [x=" + x + ", z=" + z + "]";
	}

	@Override
	protected Cube _gridGet(int x, int y, int z) {
		return grid[x][y][z];
	}
}
