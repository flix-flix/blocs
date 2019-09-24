package data.generation;

import client.session.Player;
import client.window.graphicEngine.extended.ModelMap;
import data.enumeration.ItemID;
import data.map.Cube;
import data.map.buildings.Building;
import data.map.units.Unit;
import data.multiblocs.E;
import data.multiblocs.Multibloc;
import data.multiblocs.Tree;
import utils.Coord;
import utils.FlixBlocksUtils;

public class WorldGeneration {

	public static ModelMap generateMap(ModelMap map) {

		Player felix = new Player("Félix");
		Player ia = new Player("IA");

		int ground = 10;

		// ========== Ground and Borders ==========
		for (int x = 0; x < 100; x++)
			for (int z = 0; z < 100; z++)
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

		addTree(map, 4, ground, 3);
		addTree(map, 5, ground, 6);
		addTree(map, 8, ground, 8);
		addTree(map, 3, ground, 8);
		addTree(map, 10, ground, 5);

		addTree(map, 15, ground, 5);
		addTree(map, 5, ground, 15);
		addTree(map, 2, ground, 17);
		addTree(map, 13, ground, 5);

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
		Cube off = new Cube(-1, ground, 0, 0, 45, 45, 1, 1, 1, ItemID.TEST);
		map.add(off);

		// ========== Preview cubes ==========
		map.add(new Cube(18, ground, 2, ItemID.GRASS));
		map.setPreview(map.gridGet(18, ground, 2), true);

		map.add(new Cube(13, ground, 13, ItemID.DIRT));
		map.setPreview(map.gridGet(13, ground, 13), true);

		// Cubes (texture 16x16) with the differents step of the mining animation
		for (int x = 0; x < 5; x++) {
			Cube c = new Cube(x, 2, -3, ItemID.IRON_BLOC);
			c.miningState = x;
			map.add(c);
		}

		// Cubes (texture 3x3) with the differents step of the mining animation
		for (int x = 0; x < 5; x++) {
			Cube c = new Cube(x, 2, -5, ItemID.GRASS);
			c.miningState = x;
			map.add(c);
		}

		// Add Glass blocs
		map.add(new Cube(16, ground, 14, ItemID.GLASS));
		map.add(new Cube(15, ground, 15, ItemID.GLASS_GRAY));
		map.add(new Cube(14, ground, 16, ItemID.GLASS_RED));

		// Add multibloc
		map.add(new Tree(20, ground, 10).getCube());

		// Add shifted multibloc
		Tree t = new Tree();
		t.setCoords(25, ground, 10);
		map.add(t.getCube());

		// Add multibloc (mixed on-grid/off-grid cubes)
		map.add(new E(10, ground, 20).getCube());

		// Add off-grid cube
		map.add(new Cube(19, ground, 19, 0, 0, 0, 3, 2, 2, ItemID.TEST_BIG));

		// Add multibloc of off-grids without border
		Multibloc m = new Multibloc();
		m.add(new Cube(0, 0, 0, 0, 0, 0, 3, 2, 2, ItemID.TEST_BIG));
		m.add(new Cube(0, 0, 2, 0, 0, 0, 3, 2, 2, ItemID.TEST_BIG));
		m.add(new Cube(0, 0, 4, 0, 0, 0, 3, 2, 2, ItemID.TEST_BIG));
		m.add(new Cube(5, 0, 0, 0, 90, 0, 3, 2, 2, ItemID.TEST_BIG));
		m.add(new Cube(5, 0, 3, 0, 90, 0, 3, 2, 2, ItemID.TEST_BIG));

		m.setCoords(5, ground, 25);

		map.add(m.getCube());

		// Add River
		for (int i = 0; i < 90; i++)
			dig(map, (int) (50 * Math.cos(i * FlixBlocksUtils.toRadian)), ground - 1,
					(int) (50 * Math.sin(i * FlixBlocksUtils.toRadian)));

		// =========================================================================================================================
		// Units

		for (int i = 10; i < 20; i++)
			for (int j = 5; j < 15; j++)
				// map.add(new Cube(i, 10, j, ItemID.GLASS));
				map.remove(i, 9, j);

		// Add Unit
		Unit u = new Unit(felix, 5, 10, 5);
		map.addUnit(u);
		// u.goTo(map, 15, 11, 10);
		u.goTo(map, new Coord(15, 9, 10));

		// Add Unit to IA
		Unit unitIA = new Unit(ia, 3, 10, 3);
		map.addUnit(unitIA);
		unitIA.goTo(map, new Coord(10, 10, 2));

		// Add cube with UNIT texture
		map.add(new Cube(0, 15, 0, ItemID.UNIT));

		// =========================================================================================================================
		// Buildings

		map.add(new Building(felix, 25, ground, 3, 3, 2, 2, ItemID.CASTLE));

		return map;
	}

	// =========================================================================================================================

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

	// =========================================================================================================================

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

	public static void dig(ModelMap map, int x, int y, int z) {
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
