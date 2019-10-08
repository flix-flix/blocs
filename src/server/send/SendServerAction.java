package server.send;

import java.io.Serializable;

import data.map.Coord;
import data.map.Cube;
import data.map.units.Unit;

public class SendServerAction implements Serializable {
	private static final long serialVersionUID = -6798233656048792145L;

	public ServerAction action;
	public Cube cube;
	public Coord coord;
	public int id;

	// =========================================================================================================================

	private SendServerAction(ServerAction action, Cube cube) {
		this.action = action;
		this.cube = cube;
	}

	private SendServerAction(ServerAction action, Coord coord) {
		this.action = action;
		this.coord = coord;
	}

	private SendServerAction(ServerAction action, int id) {
		this.action = action;
		this.id = id;
	}

	// =========================================================================================================================

	public static SendServerAction add(Cube c) {
		return new SendServerAction(ServerAction.ADD, c);
	}

	public static SendServerAction remove(Coord coord) {
		return new SendServerAction(ServerAction.REMOVE, coord);
	}

	// =========================================================================================================================

	public static SendServerAction arrive(Unit unit) {
		return new SendServerAction(ServerAction.ARRIVE, unit.getId());
	}
}
