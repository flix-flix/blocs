package data.generation;

import client.session.Player;
import client.window.graphicEngine.extended.ModelMap;
import data.enumeration.ItemID;
import data.map.Coord;
import data.map.Cube;
import data.map.buildings.Building;
import data.map.units.Unit;
import data.multiblocs.E;
import data.multiblocs.MultiBloc;
import data.multiblocs.Tree;
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

		// Add multibloc (mixed on-grid/off-grid cubes)
		map.add(new E(10, ground, 20).getCube());

		// Add off-grid cube
		map.add(new Cube(19, ground, 19, 0, 0, 0, 3, 2, 2, ItemID.TEST_BIG));

		// Add multibloc of off-grids without border
		MultiBloc m = new MultiBloc();
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
			for (int j = 6; j < 15; j++)
				map.remove(i, 9, j);

		// Add Unit
		Unit u1 = new Unit(felix, 5, ground, 5);
		map.addUnit(u1);
		u1.goTo(map, new Coord(15, 9, ground));

		Unit u2 = new Unit(felix, 6, ground, 2);
		map.addUnit(u2);
		u2.goTo(map, new Coord(10, ground, 2));

		map.addUnit(new Unit(felix, 4, ground, 2));
		map.addUnit(new Unit(felix, 5, ground, 2));

		// Add Unit to IA
		map.addUnit(new Unit(ia, 7, ground, 4));

		// =========================================================================================================================
		// Buildings

		map.add(new Building(felix, ItemID.CASTLE, 25, ground, 3, true).getCube());
		map.add(new Building(felix, ItemID.CASTLE, 13, ground, 3, false).getCube());
		map.add(new Building(ia, ItemID.CASTLE, 26, ground, 9, true).getCube());
		map.add(new Building(ia, ItemID.CASTLE, 30, ground, 9, false).getCube());

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
