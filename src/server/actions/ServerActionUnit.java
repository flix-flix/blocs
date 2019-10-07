package server.actions;

import data.dynamic.Action;
import data.map.Coord;
import data.map.units.Unit;

public class ServerActionUnit extends ServerAction {
	private static final long serialVersionUID = -1774442209133237983L;

	Unit unit;
	Coord coord;

	// =========================================================================================================================

	public ServerActionUnit(Action action, Unit unit) {
		super(action);
		this.unit = unit;
	}

	public ServerActionUnit(Action action, Unit unit, Coord coord) {
		this(action, unit);
		this.coord = coord;
	}

	// =========================================================================================================================

}
