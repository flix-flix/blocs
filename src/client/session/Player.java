package client.session;

import java.util.LinkedList;

import data.map.units.Unit;

public class Player {

	private String name;

	private int wood = 0;
	private int stone = 0;
	private int water = 0;

	private LinkedList<Unit> units = new LinkedList<>();

	// =========================================================================================================================

	public Player(String name) {
		this.name = name;
	}

	// =========================================================================================================================

	public int getWood() {
		return wood;
	}

	public void addWood(int wood) {
		this.wood += wood;
	}

	public int getStone() {
		return stone;
	}

	public void addRocks(int rocks) {
		this.stone += rocks;
	}

	public int getWater() {
		return water;
	}

	public void addWater(int water) {
		this.water += water;
	}

	// =========================================================================================================================

	public int getNbUnits() {
		return units.size();
	}

	public String getName() {
		return name;
	}

	// =========================================================================================================================

	public boolean equals(Player p) {
		return name.equals(p.name);
	}
}
