package data.enumeration;

public enum Rotation {
	NONE(0), RIGHT(1), UPSIDE_DOWN_X(2), LEFT(3), FRONT(1), UPSIDE_DOWN_Z(2), BACK(3);

	// =========================================================================================================================

	private int index;

	Rotation(int index) {
		this.index = index;
	}

	// =========================================================================================================================

	public static Rotation[] axeX;
	public static Rotation[] axeZ;

	static {
		axeX = new Rotation[] { NONE, RIGHT, UPSIDE_DOWN_X, LEFT };
		axeZ = new Rotation[] { NONE, FRONT, UPSIDE_DOWN_Z, BACK };
	}

	// =========================================================================================================================

	/** Returns next Rotation on the X axe None->Right->Upside_down_X->Left->None */
	public Rotation nextX() {
		return axeX[(index + 1) % 4];
	}

	/** Returns next Rotation on the X axe None->Left->Upside_down_X->Right->None */
	public Rotation previousX() {
		return axeX[(index + 3) % 4];
	}

}
