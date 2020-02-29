package data;

import java.io.Serializable;
import java.util.LinkedList;

import data.map.units.Unit;

public class Gamer implements Serializable {
	private static final long serialVersionUID = 3653426306615022279L;

	// =============== Static ===============
	public static final Gamer nullGamer = new Gamer(-1, "_null");

	// =============== ? ===============
	int number;
	String name;

	// =============== Data ===============
	private LinkedList<Unit> units = new LinkedList<>();

	private int wood = 0;
	private int stone = 0;
	private int water = 0;

	// =========================================================================================================================

	public Gamer(int nb) {
		this(nb, "Player " + nb);
	}

	public Gamer(int nb, String name) {
		this.number = nb;
		this.name = name;
	}

	// =========================================================================================================================

	public String getName() {
		return name;
	}

	public int getNumber() {
		return number;
	}

	// =========================================================================================================================
	public int getNbUnits() {
		return units.size();
	}

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

	public boolean equals(Gamer gamer) {
		return number == gamer.number;
	}
}
