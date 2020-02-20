package data.map.multiblocs;

import data.id.ItemID;
import data.map.Cube;
import utils.Utils;

public class E extends MultiBloc {
	private static final long serialVersionUID = -4312589735027448825L;

	public E(int x, int y, int z) {
		super(ItemID.E, 0, 0, 0);

		for (int i = 0; i <= 6; i++)
			add(new Cube(x, y + i, z, ItemID.BORDER));

		for (int i = 1; i <= 3; i++) {
			add(new Cube(x - i, y, z, ItemID.BORDER));
			add(new Cube(x - i, y + 6, z, ItemID.BORDER));
		}

		add(new Cube(x - 1, y + 3, z, ItemID.BORDER));
		add(new Cube(x - 2, y + 3, z, ItemID.BORDER));

		int a = 60, b = 90 - a;

		add(new Cube(x, y + 7.3, z, 0, 0, a, 1, 1, 1, ItemID.BORDER));
		add(new Cube(x - Math.cos(b * Utils.toRadian), y + 7.3 + Math.sin(b * Utils.toRadian), z, 0,
				0, a, 1, 1, 1, ItemID.BORDER));
		add(new Cube(x - 2 * Math.cos(b * Utils.toRadian),
				y + 7.3 + 2 * Math.sin(b * Utils.toRadian), z, 0, 0, a, 1, 1, 1, ItemID.BORDER));
	}

	public E() {
		this(0, 0, 0);
	}

	// =========================================================================================================================

	@Override
	public MultiBloc clone() {
		return new E(x, y, z);
	}

	// =========================================================================================================================

	@Override
	public String toString() {
		return String.format("I'm an E. I'm at coord %d, %d, %d", x, y, z);
	}
}
