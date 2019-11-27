package server.game;

import data.map.Coord;
import data.map.Map;
import data.map.buildings.Building;
import data.map.enumerations.Orientation;
import data.map.units.Unit;
import server.Server;
import server.send.Action;

public class UnitServer extends Unit {
	private static final long serialVersionUID = -296462819753986938L;

	transient Server server;

	// =========================================================================================================================

	public UnitServer(Unit unit, Server server) {
		super(unit);
		this.server = server;
	}

	// =========================================================================================================================

	public boolean doStep(Map map) {
		boolean b;
		if (!(b = super.doStep(map)))
			arrive((MapServer) map);
		return b;
	}

	@Override
	public void doBuild(Map map, Building build) {
		if (map.gridGet(actionCube).build.addAlreadyBuild(1)) // Building created
			action = null;
	}

	@Override
	public void doHarvest(Map map) {
		super.doHarvest(map);

		resource.add(map.gridGet(actionCube).resourceTake(1));

		if (map.gridGet(actionCube).resourceIsEmpty()) { // Cube break
			map.remove(actionCube);
			action = null;
		}

		if (resource.isFull())
			action = null;// TODO Go Drop
	}

	@Override
	public void doDrop(Map map, Building build) {
		super.doDrop(map, build);

		if (build.addToStock(resource, 1))// Stock full
			action = null;// TODO Except if there is another empty stock around
		else if (resource.isEmpty()) {// TODO Go Harvest
			removeResource();
			action = null;
		}
	}

	// =========================================================================================================================

	/** Update cube position after the rotation */
	public void arrive(MapServer map) {
		map._gridRemove(this.coord);
		super.arrive();
		map._gridAdd(this);
		server.unitArrive(this);
	}

	@Override
	public void doNextMove() {
		super.doNextMove();
	}

	// =========================================================================================================================

	/** Initialize a rotation to an adjacent position */
	@Override
	protected void setDirection(Orientation dir) {
		// TODO test if cube is walkable (and set phantom cube)
		super.setDirection(dir);
	}

	/** Initialize a rotation to a diagonal position */
	public void setDiago(int x) {
		// TODO test if cube is walkable (and set phantom cube)
		super.setDiago(x);
	}

	/** Initialize a rotation to the upper position */
	public void setUp(Orientation dir) {
		// TODO test if cube is walkable (and set phantom cube)
		super.setUp(dir);
	}

	/** Initialize a rotation to the position below */
	public void setDown(Orientation dir) {
		// TODO test if cube is walkable (and set phantom cube)
		super.setDown(dir);
	}

	/** Initialize an on-place rotation to change the unit's rotation axe */
	public void rotation() {
		// TODO test if cube is walkable (and set phantom cube)
		super.rotation();
	}

	// =========================================================================================================================

	@Override
	public boolean doAction(Action action, Map map, Coord coord) {
		return super.doAction(action, map, coord);
	}
}
