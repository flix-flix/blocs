package data.generation;

import data.id.ItemID;
import data.id.ItemTable;
import data.map.Cube;
import data.map.Map;
import data.map.buildings.Building;
import data.map.multiblocs.E;
import data.map.multiblocs.MultiBloc;
import data.map.multiblocs.Tree;
import data.map.units.Unit;
import server.game.Player;
import utils.FlixBlocksUtils;

public class WorldGeneration {

	public static Map generateMap() {
		Map map = new Map();

		Player felix = new Player("Felix");
		Player ia = new Player("IA");

		int ground = 10;
		int size = 20;

		// ========== Ground and Borders ==========
		for (int x = 0; x < size; x++)
			for (int z = 0; z < size; z++)
				if (x % 99 == 0 || z % 99 == 0)
					for (int y = 0; y <= ground + 3; y++)
						map.add(new Cube(x, y, z, ItemID.BORDER));
				else {
					for (int y = 0; y < ground - 1; y++)
						map.add(new Cube(x, y, z, ItemID.DIRT));
					map.add(new Cube(x, ground - 1, z, ItemID.GRASS));
				}

		// ========== Forest ==========
		for (int x = 1; x < 30; x++)
			for (int z = 1; z < 30; z++)
				if (x / 2 + z < 15 || x + z / 2 < 15)
					map.set(new Cube(x, ground - 1, z, ItemID.DIRT));

		// Add multibloc
		map.add(new Tree(20, ground, 10).getCube());

		// Add shifted multibloc
		Tree t = new Tree();
		t.setCoords(25, ground, 10);
		map.add(t.getCube());

		map.add(new Tree(2, ground, 4).getCube());
		map.add(new Tree(2, ground, 17).getCube());
		map.add(new Tree(3, ground, 11).getCube());
		map.add(new Tree(5, ground, 6).getCube());
		map.add(new Tree(5, ground, 15).getCube());
		map.add(new Tree(8, ground, 8).getCube());
		map.add(new Tree(10, ground, 5).getCube());
		map.add(new Tree(14, ground, 5).getCube());
		map.add(new Tree(18, ground, 4).getCube());

		// ========== Mountain ==========
		for (int i = 5; i < 45; i++) {
			if (i % 3 == 0)
				addMountain(map, 50 - i, ground, i);
			if (i % 4 == 0) {
				addMountain(map, 50 - i + 2, ground, i + 2);
				addMountain(map, 50 - i, ground, i);
			}
		}

		// Off-grid cube
		Cube off = new Cube(-2, 2, -4, 0, 45, 45, 1, 1, 1, ItemID.TEST);
		map.add(off);

		// Cubes (texture 16x16) with the differents step of the mining animation
		for (int x = -1; x < 5; x++) {
			Cube c = new Cube(x, 2, -3, ItemID.TEST);
			c.miningState = x;
			map.add(c);
		}

		// Cubes (texture 3x3) with the differents step of the mining animation
		for (int x = -1; x < 5; x++) {
			Cube c = new Cube(x, 2, -5, ItemID.GRASS);
			c.miningState = x;
			map.add(c);
		}

		// Add Glass blocs
		map.add(new Cube(16, ground, 14, ItemID.GLASS));
		map.add(new Cube(15, ground, 15, ItemID.GLASS_GRAY));
		map.add(new Cube(14, ground, 16, ItemID.GLASS_RED));

		// Add multibloc (mixed on-grid/off-grid cubes)
		map.add(new E(10, ground, 20).getCube());

		// Add off-grid cube
		map.add(new Cube(19, ground, 19, 0, 0, 0, 3, 2, 2, ItemID.TEST_BIG));

		// Add Multibloc
		MultiBloc testBig = ItemTable.createMulti(ItemID.TEST_BIG);
		testBig.setCoords(15, ground, 19);
		map.add(testBig.getCube());

		// Add River
		for (int i = 0; i < 90; i++)
			dig(map, (int) (50 * Math.cos(i * FlixBlocksUtils.toRadian)), ground - 1,
					(int) (50 * Math.sin(i * FlixBlocksUtils.toRadian)));

		// =========================================================================================================================
		// Units

		// Dig
		for (int i = 10; i < 20; i++)
			for (int j = 6; j < 15; j++)
				map.remove(i, 9, j);

		// Add Units
		map.addUnit(new Unit(ItemID.UNIT, felix, 5, ground, 5));
		map.addUnit(new Unit(ItemID.UNIT, felix, 6, ground, 2));
		map.addUnit(new Unit(ItemID.UNIT, felix, 4, ground, 2));
		map.addUnit(new Unit(ItemID.UNIT, felix, 5, ground, 2));

		// Add Unit to IA
		map.addUnit(new Unit(ItemID.UNIT, ia, 7, ground, 4));

		// =========================================================================================================================
		// Buildings

		map.addBuilding(new Building(felix, ItemID.CASTLE, 25, ground, 3, true));
		map.addBuilding(new Building(felix, ItemID.CASTLE, 13, ground, 3, false));
		map.addBuilding(new Building(ia, ItemID.CASTLE, 26, ground, 9, true));
		map.addBuilding(new Building(ia, ItemID.CASTLE, 30, ground, 9, false));

		return map;
	}

	// =========================================================================================================================

	private static void addMountain(Map map, int x, int y, int z) {
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

	private static void dig(Map map, int x, int y, int z) {
		for (int _y = 0; _y < 7; _y++) {
			int a = (7 - _y) / 2;
			for (int _x = -a; _x <= a; _x++)
				for (int _z = -a; _z <= a; _z++)
					if (!((_x == -a || _x == a) && (_z == -a || _z == a) && (7 - _y) % 2 == 0)) {
						map.remove(x + _x, y - _y, z + _z);
						if (_y == 0) {
							Cube c = new Cube(x + _x, y - _y, z + _z, 1, .8, 1, ItemID.WATER);
							c.onGrid = true;
							map.add(c);
						}
					}
		}
	}
}
