package server.game;

import data.map.Coord;
import data.map.Cube;
import data.map.Map;
import data.map.MultiCube;
import data.map.buildings.Building;
import data.map.units.Unit;
import server.Server;

public class MapServer extends Map {
	private static final long serialVersionUID = 4924539785359682765L;

	transient private Server server;

	// =========================================================================================================================

	public MapServer(Map map, Server server) {
		super(map);
		this.server = server;

		for (Cube cube : units.values())
			((UnitServer) cube.unit).server = server;
	}

	// =========================================================================================================================
	// Create server-data

	@Override
	protected Cube createUnit(Unit unit) {
		if (unit instanceof UnitServer)
			return new Cube(unit);
		return new Cube(new UnitServer(unit, server));
	}

	// =========================================================================================================================
	// Intercept grid modifications for clients update

	@Override
	protected Cube gridAdd(Cube cube) {
		Cube c = super.gridAdd(cube);
		if (cube.multicube == null)
			server.addCube(c);
		return c;
	}

	@Override
	protected void gridRemove(int x, int y, int z) {
		boolean update = true;

		if (!gridContains(x, y, z))
			return;

		if (gridGet(x, y, z).multicube != null) {
			if (!gridGet(x, y, z).multicube.valid)
				update = false;
			gridGet(x, y, z).multicube.valid = false;
		}

		super.gridRemove(x, y, z);

		if (update)
			server.removeCube(x, y, z);
	}

	// =========================================================================================================================

	public void harvest(Unit unit, Coord coord) {
		server.unitHarvest(unit, coord);
		remove(coord);
	}

	public void store(Unit unit, Building build) {
		server.unitStore(unit, build);
	}

	// =========================================================================================================================
	// Cast

	@Override
	public UnitServer getUnit(int id) {
		return (UnitServer) super.getUnit(id);
	}

	// =========================================================================================================================
	// Grid modification avoiding clients update

	protected Cube _gridAdd(Cube cube) {
		return super.gridAdd(cube);
	}

	protected Cube _gridAdd(Unit unit) {
		return super.gridAdd(createUnit(unit));
	}

	protected void _gridRemove(int x, int y, int z) {
		super.gridRemove(x, y, z);
	}

	protected void _gridRemove(Coord coord) {
		super.gridRemove(coord.x, coord.y, coord.z);
	}

	// =========================================================================================================================

	@Override
	protected boolean addMulti(MultiCube multi, boolean full) {
		boolean added = super.addMulti(multi, full);
		if (added)
			server.addCube(multi.getCube());
		return added;
	}

	@Override
	public boolean addBuilding(Building build) {
		return super.addBuilding(build);
	}
}
