package data.generation;

import client.window.graphicEngine.models.ModelMap;
import data.enumeration.ItemID;
import data.map.Cube;
import data.multiblocs.E;
import data.multiblocs.Tree;

public class WorldGeneration {

	public static ModelMap generateMap(ModelMap map) {

		// ========== Ground and Borders ==========
		for (int x = 0; x < 100; x++)
			for (int z = 0; z < 100; z++)
				if (x % 99 == 0 || z % 99 == 0)
					for (int y = 0; y <= 3; y++)
						map.add(new Cube(x, y, z, ItemID.BORDER));
				else
					map.add(new Cube(x, 0, z, ItemID.GRASS));

		// ========== Forest ==========
		for (int x = 1; x < 30; x++)
			for (int z = 1; z < 30; z++)
				if (x / 2 + z < 15 || x + z / 2 < 15)
					map.set(new Cube(x, 0, z, ItemID.DIRT));

		addTree(map, 4, 1, 3);
		addTree(map, 5, 1, 6);
		addTree(map, 8, 1, 8);
		addTree(map, 3, 1, 8);
		addTree(map, 10, 1, 5);

		addTree(map, 15, 1, 5);
		addTree(map, 5, 1, 15);
		addTree(map, 2, 1, 17);
		addTree(map, 13, 1, 5);

		// ========== Mountain ==========
		for (int i = 5; i < 45; i++) {
			if (i % 3 == 0)
				addMountain(map, 50 - i, 1, i);
			if (i % 4 == 0) {
				addMountain(map, 50 - i + 2, 1, i + 2);
				addMountain(map, 50 - i, 1, i);
			}
		}

		// Off-grid cube
		map.add(new Cube(-1, 1, 0, 0, 0, 0, 45, 45, 1, 1, 1, ItemID.TEST));

		// ========== Preview cubes ==========
		map.add(new Cube(18, 1, 2, ItemID.GRASS));
		map.gridGet(18, 1, 2).setPreview(true);
		map.update(18, 1, 2);

		map.add(new Cube(13, 1, 13, ItemID.DIRT));
		map.gridGet(13, 1, 13).setPreview(true);
		map.update(13, 1, 13);

		// Cubes with the differents step of the mining animation
		for (int x = 0; x < 5; x++) {
			Cube c = new Cube(x, 2, -3, ItemID.IRON_BLOC);
			c.miningState = x;
			map.add(c);
		}

		// Add Glass blocs
		map.add(new Cube(16, 1, 14, ItemID.GLASS));
		map.add(new Cube(15, 1, 15, ItemID.GLASS_GRAY));
		map.add(new Cube(14, 1, 16, ItemID.GLASS_RED));

		// Add multibloc
		map.add(new Tree(20, 1, 10).getCube());

		// Add shifted multibloc
		Tree t = new Tree();
		t.setCoords(25, 1, 10);
		map.add(t.getCube());

		map.add(new E(10, 1, 20).getCube());

		return map;
	}

	public static void addTree(ModelMap map, int x, int y, int z) {
		map.add(new Cube(x, y, z, ItemID.OAK_TRUNK));
		map.add(new Cube(x, y + 1, z, ItemID.OAK_TRUNK));
		addLeaves(map, x, y + 2, z);
		addLeaves(map, x, y + 3, z);

		map.add(new Cube(x, y + 4, z, ItemID.OAK_LEAVES));
	}

	public static void addLeaves(ModelMap map, int x, int y, int z) {
		map.add(new Cube(x, y, z, ItemID.OAK_TRUNK));

		map.add(new Cube(x - 1, y, z, ItemID.OAK_LEAVES));
		map.add(new Cube(x + 1, y, z, ItemID.OAK_LEAVES));
		map.add(new Cube(x, y, z + 1, ItemID.OAK_LEAVES));
		map.add(new Cube(x, y, z - 1, ItemID.OAK_LEAVES));
	}

	public static void addMountain(ModelMap map, int x, int y, int z) {
		for (int i = 0; i < 7; i++) {
			int a = (7 - i) / 2;
			for (int j = -a; j <= a; j++)
				for (int k = -a; k <= a; k++)
					if (!((j == -a || j == a) && (k == -a || k == a) && (7 - i) % 2 == 0))
						map.add(new Cube(x + j, y + i, z + k, ItemID.STONE));

			map.add(new Cube(x, y + 7, z, ItemID.STONE));
			map.add(new Cube(x, y + 6, z, ItemID.STONE));
		}
	}
}
