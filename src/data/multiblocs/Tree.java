package data.multiblocs;

import data.enumeration.ItemID;
import data.map.Cube;

public class Tree extends MultiBloc {

	public Tree(int x, int y, int z) {
		super(x, y, z);

		add(new Cube(x, y, z, ItemID.OAK_TRUNK));
		add(new Cube(x, y + 1, z, ItemID.OAK_TRUNK));

		addLeaves(x, y + 2, z);
		addLeaves(x, y + 3, z);

		add(new Cube(x, y + 4, z, ItemID.OAK_LEAVES));
	}

	public Tree() {
		this(0, 0, 0);
	}

	// =========================================================================================================================

	private void addLeaves(int x, int y, int z) {
		add(new Cube(x, y, z, ItemID.OAK_TRUNK));

		add(new Cube(x - 1, y, z, ItemID.OAK_LEAVES));
		add(new Cube(x + 1, y, z, ItemID.OAK_LEAVES));
		add(new Cube(x, y, z + 1, ItemID.OAK_LEAVES));
		add(new Cube(x, y, z - 1, ItemID.OAK_LEAVES));
	}

	// =========================================================================================================================

	@Override
	public MultiBloc clone() {
		return new Tree(x, y, z);
	}

	// =========================================================================================================================

	@Override
	public String toString() {
		return "I'm a tree. I'm at coords : " + x + ", " + y + ", " + z;
	}
}
