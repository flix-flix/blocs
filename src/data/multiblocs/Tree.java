package data.multiblocs;

import data.enumeration.ItemID;
import data.map.Cube;

public class Tree extends Multibloc {

	public Tree(int x, int y, int z) {
		list.add(new Cube(x, y, z, ItemID.OAK_TRUNK));
		list.add(new Cube(x, y + 1, z, ItemID.OAK_TRUNK));

		addLeaves(x, y + 2, z);
		addLeaves(x, y + 3, z);

		list.add(new Cube(x, y + 4, z, ItemID.OAK_LEAVES));
	}

	public void addLeaves(int x, int y, int z) {
		list.add(new Cube(x, y, z, ItemID.OAK_TRUNK));

		list.add(new Cube(x - 1, y, z, ItemID.OAK_LEAVES));
		list.add(new Cube(x + 1, y, z, ItemID.OAK_LEAVES));
		list.add(new Cube(x, y, z + 1, ItemID.OAK_LEAVES));
		list.add(new Cube(x, y, z - 1, ItemID.OAK_LEAVES));
	}
}
