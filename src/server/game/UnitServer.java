package server.game;

import java.util.LinkedList;

import data.map.Coord;
import data.map.Map;
import data.map.buildings.Building;
import data.map.enumerations.Orientation;
import data.map.units.Unit;
import server.Server;
import server.send.Action;
import server.send.SendAction;

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

		if (resource.isFull())
			action = null;// TODO Go store
		else {
			resource.add(1);

			((MapServer) map).harvest(this, actionCube);// Cube break
			action = null;
		}
	}

	@Override
	public void doStore(Map map, Building build) {
		super.doStore(map, build);

		if (build.addToStock(resource, 1))// Stock full
			action = null;// TODO Except if there is another empty stock around

		((MapServer) map).store(this, build);

		if (resource == null || resource.isEmpty()) {// TODO Go Harvest
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
	// Path

	@Override
	public boolean setPath(LinkedList<Coord> path) {
		if (path != null)
			server.sendToAll(SendAction.goTo(this, new LinkedList<>(path)));

		return super.setPath(path);
	}

	// =========================================================================================================================
	// Roll/Rotation

	/** Initialize a rotation to an adjacent position */
	@Override
	protected void rollAdjacent(Orientation dir) {
		// TODO test if cube is walkable (and set phantom cube)
		Coord movingTo = coord.face(dir.face);

		if (server.map.gridContains(movingTo))
			;
		// else
		super.rollAdjacent(dir);
	}

	/** Initialize a rotation to a diagonal position */
	public void rollDiago(int x) {
		// TODO test if cube is walkable (and set phantom cube)
		super.rollDiago(x);
	}

	/** Initialize a rotation to the upper position */
	public void rollUp(Orientation dir) {
		// TODO test if cube is walkable (and set phantom cube)
		super.rollUp(dir);
	}

	/** Initialize a rotation to the position below */
	public void rollDown(Orientation dir) {
		// TODO test if cube is walkable (and set phantom cube)
		super.rollDown(dir);
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
