package server.game;

import data.map.Coord;
import data.map.Cube;
import data.map.Map;
import data.map.multiblocs.MultiBloc;
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
	// Create displayable-data

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
		server.addCube(c);
		return c;
	}
	
	@Override
	protected void gridRemove(int x, int y, int z) {
		super.gridRemove(x, y, z);
		server.removeCube(x, y, z);
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
	protected boolean addMulti(MultiBloc multi, boolean full) {
		return super.addMulti(multi, full);
	}
}
