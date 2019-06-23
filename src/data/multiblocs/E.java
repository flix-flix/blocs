package data.multiblocs;

import client.window.graphicEngine.calcul.Engine;
import data.enumeration.ItemID;
import data.map.Cube;

public class E extends Multibloc {

	public E(int x, int y, int z) {
		super(0, 0, 0);

		for (int i = 0; i <= 6; i++)
			add(new Cube(x, y + i, z, ItemID.BORDER));

		for (int i = 1; i <= 3; i++) {
			add(new Cube(x - i, y, z, ItemID.BORDER));
			add(new Cube(x - i, y + 6, z, ItemID.BORDER));
		}

		add(new Cube(x - 1, y + 3, z, ItemID.BORDER));
		add(new Cube(x - 2, y + 3, z, ItemID.BORDER));

		int a = 60, b = 90 - a;

		add(new Cube(x, y + 7.3, z, 0, 0, 0, 0, a, 1, 1, 1, ItemID.BORDER));
		add(new Cube(x - Math.cos(b * Engine.toRadian), y + 7.3 + Math.sin(b * Engine.toRadian), z, 0, 0, 0, 0, a, 1, 1,
				1, ItemID.BORDER));
		add(new Cube(x - 2 * Math.cos(b * Engine.toRadian), y + 7.3 + 2 * Math.sin(b * Engine.toRadian), z, 0, 0, 0, 0,
				a, 1, 1, 1, ItemID.BORDER));
	}

	public E() {
		this(0, 0, 0);
	}

	// =========================================================================================================================

	@Override
	public Multibloc clone() {
		return new E(x, y, z);
	}
}
