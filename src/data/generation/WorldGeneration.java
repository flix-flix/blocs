package data.generation;

import data.Gamer;
import data.id.ItemID;
import data.id.ItemTable;
import data.map.Coord;
import data.map.Cube;
import data.map.Map;
import data.map.MultiCube;
import data.map.buildings.Building;
import data.map.units.Unit;
import utils.Utils;

public class WorldGeneration {

	public static Map generateMap() {
		Map map = new Map();

		Gamer felix, ia;

		map.addGamer(felix = new Gamer(1, "Félix"));
		map.addGamer(ia = new Gamer(2, "IA"));

		int ground = 10;
		int size = 20;// 65;

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

		// ========== Flat ==========
		int arc = 46;
		for (int i = 0; i < 90; i++)
			for (int z = 0; z < (int) (arc * Math.sin(i * Utils.toRadian)); z++)
				map.add(new Cube((int) (arc * Math.cos(i * Utils.toRadian)), ground - 1, z, ItemID.GRASS));

		// ========== Forest ==========
		for (int x = 1; x < 30; x++)
			for (int z = 1; z < 30; z++)
				if (x / 2 + z < 15 || x + z / 2 < 15)
					map.set(new Cube(x, ground - 1, z, ItemID.DIRT));

		Coord[] trees = new Coord[] { new Coord(2, ground, 4), new Coord(2, ground, 17), new Coord(3, ground, 11),
				new Coord(5, ground, 6), new Coord(5, ground, 15), new Coord(8, ground, 8), new Coord(10, ground, 5),
				new Coord(14, ground, 5), new Coord(18, ground, 4), new Coord(20, ground, 10),
				new Coord(25, ground, 10) };

		for (Coord tree : trees) {
			Cube cube = ItemTable.create(ItemID.TREE);
			cube.setCoords(tree);
			map.add(cube);
		}

		// ========== Mountain ==========
		for (int i = 5; i < 45; i++) {
			if (i >= 22 && i < 30)
				continue;
			if (i % 3 == 0)
				addMountain(map, 50 - i, ground, i);
			if (i % 4 == 0) {
				addMountain(map, 50 - i + 2, ground, i + 2);
				addMountain(map, 50 - i, ground, i);
			}
		}

		// ========== River ==========
		for (int i = 0; i <= 90; i++)
			river(map, (int) (60 * Math.cos(i * Utils.toRadian)), ground - 1,
					(int) (60 * Math.sin(i * Utils.toRadian)));

		// ========== Dig ==========
		for (int x = 10; x < 20; x++)
			for (int z = 6; z < 15; z++)
				map.remove(x, ground - 1, z);

		// ========== Lake ==========
		for (int x = 13; x < 17; x++)
			for (int z = 9; z < 12; z++) {
				map.remove(x, ground - 2, z);
				map.add(new Cube(x, ground - 2, z, ItemID.WATER));
			}

		// =========================================================================================================================
		// Units

		// Felix
		map.addUnit(new Unit(ItemID.UNIT, felix, 5, ground, 5));
		map.addUnit(new Unit(ItemID.UNIT, felix, 6, ground, 2));
		map.addUnit(new Unit(ItemID.UNIT, felix, 4, ground, 2));
		map.addUnit(new Unit(ItemID.UNIT, felix, 5, ground, 2));

		// IA
		map.addUnit(new Unit(ItemID.UNIT, ia, 7, ground, 4));

		// =========================================================================================================================
		// Buildings

		map.addBuilding(new Building(felix, ItemID.CASTLE, 13, ground, 3, false));
		map.addBuilding(new Building(felix, ItemID.CASTLE, 21, ground, 3, true));
		map.addBuilding(new Building(ia, ItemID.CASTLE, 9, ground, 19, true));
		map.addBuilding(new Building(ia, ItemID.CASTLE, 16, ground, 21, false));

		return map;
	}

	// =========================================================================================================================

	public static Map generateMapTest() {
		Map map = new Map();

		Gamer felix = new Gamer(1, "Félix");
		Gamer ia = new Gamer(2, "IA");

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

		Coord[] trees = new Coord[] { new Coord(2, ground, 4), new Coord(2, ground, 17), new Coord(3, ground, 11),
				new Coord(5, ground, 6), new Coord(5, ground, 15), new Coord(8, ground, 8), new Coord(10, ground, 5),
				new Coord(14, ground, 5), new Coord(18, ground, 4), new Coord(20, ground, 10),
				new Coord(25, ground, 10) };

		for (Coord tree : trees) {
			Cube cube = ItemTable.create(ItemID.TREE);
			cube.setCoords(tree);
			map.add(cube);
		}

		// ========== Mountain ==========
		for (int i = 5; i < 45; i++) {
			if (i % 3 == 0)
				addMountain(map, 50 - i, ground, i);
			if (i % 4 == 0) {
				addMountain(map, 50 - i + 2, ground, i + 2);
				addMountain(map, 50 - i, ground, i);
			}
		}

		// ========== River ==========
		for (int i = 0; i < 90; i++)
			river(map, (int) (50 * Math.cos(i * Utils.toRadian)), ground - 1,
					(int) (50 * Math.sin(i * Utils.toRadian)));

		// ========== Dig ==========
		for (int x = 10; x < 20; x++)
			for (int z = 6; z < 15; z++)
				map.remove(x, ground - 1, z);

		// ========== Lake ==========
		for (int x = 13; x < 17; x++)
			for (int z = 9; z < 12; z++) {
				map.remove(x, ground - 2, z);
				map.add(new Cube(x, ground - 2, z, ItemID.WATER));
			}

		// Off-grid cube
		Cube off = new Cube(-2, 2, -4, 0, 45, 45, 1, 1, 1, ItemID.TEST);
		map.add(off);

		// Cubes (texture 16x16) with the differents step of the mining animation
		map.add(new Cube(-1, 2, -3, ItemID.TEST));
		for (int x = 0; x < 5; x++) {
			Cube c = new Cube(x, 2, -3, ItemID.TEST);
			c.minedAlready = x == 0 ? 1 : ItemTable.getMiningTime(ItemID.TEST) / 5 * x;
			map.add(c);
		}

		// Cubes (texture 3x3) with the differents step of the mining animation
		map.add(new Cube(-1, 2, -5, ItemID.GRASS));
		for (int x = 0; x < 5; x++) {
			Cube c = new Cube(x, 2, -5, ItemID.GRASS);
			c.minedAlready = x == 0 ? 1 : ItemTable.getMiningTime(ItemID.GRASS) / 5 * x;
			map.add(c);
		}

		// Add Glass blocs
		map.add(new Cube(16, ground, 14, ItemID.GLASS));
		map.add(new Cube(15, ground, 15, ItemID.GLASS_GRAY));
		map.add(new Cube(14, ground, 16, ItemID.GLASS_RED));

		// Add multibloc (mixed on-grid/off-grid cubes)
		Cube e = ItemTable.create(ItemID.E);
		e.setCoords(10, ground, 20);
		map.add(e);

		// Add off-grid cube
		map.add(new Cube(19, ground, 19, 0, 0, 0, 3, 2, 2, ItemID.TEST_BIG));

		// Add Multibloc
		MultiCube testBig = ItemTable.createMultiBloc(ItemID.TEST_BIG);
		testBig.setCoords(15, ground, 19);
		map.add(testBig.getCube());

		// =========================================================================================================================
		// Units

		// Felix
		map.addUnit(new Unit(ItemID.UNIT, felix, 5, ground, 5));
		map.addUnit(new Unit(ItemID.UNIT, felix, 6, ground, 2));
		map.addUnit(new Unit(ItemID.UNIT, felix, 4, ground, 2));
		map.addUnit(new Unit(ItemID.UNIT, felix, 5, ground, 2));

		// IA
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
		for (int _y = 0; _y < 7; _y++) {
			int a = (7 - _y) / 2;
			for (int _x = -a; _x <= a; _x++)
				for (int _z = -a; _z <= a; _z++)
					if (!((_x == -a || _x == a) && (_z == -a || _z == a) && (7 - _y) % 2 == 0))
						map.add(new Cube(x + _x, y + _y, z + _z, ItemID.STONE));

			map.add(new Cube(x, y + 7, z, ItemID.STONE));
			map.add(new Cube(x, y + 6, z, ItemID.STONE));
		}
	}

	private static void river(Map map, int x, int y, int z) {
		for (int _y = 0; _y < 7; _y++) {
			int a = (7 - _y) / 2;
			for (int _x = -a; _x <= a; _x++)
				for (int _z = -a; _z <= a; _z++)
					if (!((_x == -a || _x == a) && (_z == -a || _z == a) && (7 - _y) % 2 == 0)) {
						if (x + _x != 0 && z + _z != 0)
							map.remove(x + _x, y - _y, z + _z);
						if (_y == 0) {
							Cube c = new Cube(Math.max(1, x + _x), y - _y, Math.max(1, z + _z), 1, .8, 1, ItemID.WATER);
							c.onGrid = true;
							map.add(c);
						}
					}
		}
	}
}
