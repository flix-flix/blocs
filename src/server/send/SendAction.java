package server.send;

import java.io.Serializable;

import data.dynamic.Action;
import data.map.Coord;
import data.map.units.Unit;

public class SendAction implements Serializable {
	private static final long serialVersionUID = -1472528237085430687L;

	public Action action;
	public Coord coord;
	public int unitID = -1;
	public int buildID = -1;

	// =========================================================================================================================

	public SendAction(Action action, Unit unit, Coord coord) {
		this.action = action;
		this.unitID = unit.getId();
		this.coord = coord;
	}
}
