package server.actions;

import java.io.Serializable;

import data.dynamic.Action;

public class ServerAction implements Serializable {
	private static final long serialVersionUID = -1472528237085430687L;

	protected Action action;

	public ServerAction(Action action) {
		this.action = action;
	}
}
