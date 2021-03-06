package data.map.enumerations;

public enum Orientation {
	NORTH(Face.NORTH), EAST(Face.EAST), SOUTH(Face.SOUTH), WEST(Face.WEST);

	// =========================================================================================================================

	public Face face;

	Orientation(Face f) {
		face = f;
	}

	// =========================================================================================================================

	/** Returns the next Orientation North->East->South->West->North */
	public Orientation next() {
		return Orientation.values()[(ordinal() + 1) % 4];
	}

	/** Retruns the previous Orientation North->West->South->East->North */
	public Orientation previous() {
		return Orientation.values()[(ordinal() + 3) % 4];
	}

	// =========================================================================================================================

	/** Returns the opposite Orientation */
	public Orientation opposite() {
		switch (this) {
		case EAST:
			return WEST;
		case NORTH:
			return SOUTH;
		case SOUTH:
			return NORTH;
		case WEST:
			return EAST;
		default:
			return null;
		}
	}

	/**
	 * Returns true if both orientations are in the same axe (North-South |
	 * East-West)
	 */
	public boolean isSameAxe(Orientation ori) {
		return ori == this || ori == opposite();
	}

	// =========================================================================================================================

	/**
	 * Given the degree orientation [0-360] returns the corresponding Orientation
	 */
	public static Orientation getOrientation(double d) {
		if (d >= 315 || d < 45)
			return Orientation.NORTH;
		else if (d >= 45 && d < 135)
			return Orientation.EAST;
		else if (d < 225)
			return Orientation.SOUTH;
		return Orientation.WEST;
	}
}
