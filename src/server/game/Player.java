package server.game;

import java.io.Serializable;

public class Player implements Serializable {
	private static final long serialVersionUID = -3099767464576186554L;

	private String name;
	public transient int id = -1;

	// =========================================================================================================================

	public Player(String name) {
		this.name = name;
	}

	// =========================================================================================================================

	public void setID(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	// =========================================================================================================================

	public boolean equals(Player p) {
		return name.equals(p.name);
	}
}
