package server.send;

import java.io.Serializable;

import data.map.Coord;
import data.map.Cube;
import data.map.buildings.Building;
import data.map.units.Unit;

public class SendAction implements Serializable {
	private static final long serialVersionUID = -1472528237085430687L;

	public Action action;
	public Cube cube;
	public Coord coord;
	public int id1 = -1, id2 = -1;

	public boolean done = false;

	// =========================================================================================================================

	private SendAction(Action action, Cube cube) {
		this.action = action;
		this.cube = cube;
	}

	private SendAction(Action action, Coord coord) {
		this.action = action;
		this.coord = coord;
	}

	private SendAction(Action action, int id) {
		this.action = action;
		this.id1 = id;
	}

	private SendAction(Action action, int id, Coord coord) {
		this.action = action;
		this.id1 = id;
		this.coord = coord;
	}

	private SendAction(Action action, int id1, int id2) {
		this.action = action;
		this.id1 = id1;
		this.id2 = id2;
	}

	// =========================================================================================================================

	public SendAction finished() {
		done = true;
		return this;
	}

	// =========================================================================================================================

	public static SendAction add(Cube c) {
		return new SendAction(Action.ADD, c);
	}

	public static SendAction remove(Coord coord) {
		return new SendAction(Action.REMOVE, coord);
	}

	// =========================================================================================================================

	public static SendAction goTo(Unit unit, Coord coord) {
		return new SendAction(Action.UNIT_GOTO, unit.getId(), coord);
	}

	public static SendAction arrive(Unit unit) {
		return new SendAction(Action.UNIT_ARRIVE, unit.getId());
	}

	// =========================================================================================================================

	public static SendAction build(Unit unit, Building build) {
		return new SendAction(Action.UNIT_BUILD, unit.getId(), build.getId());
	}

	public static SendAction store(Unit unit, Building build) {
		return new SendAction(Action.UNIT_STORE, unit.getId(), build.getId());
	}

	// =========================================================================================================================

	public static SendAction harvest(Unit unit, Coord coord) {
		return new SendAction(Action.UNIT_HARVEST, unit.getId(), coord);
	}
}
