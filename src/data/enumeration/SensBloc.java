package data.enumeration;

public enum SensBloc {
	AUCUN, X, Y, Z, LOOK_UP, LOOK_DOWN, LOOK_NORTH, LOOK_SOUTH, LOOK_EAST, LOOK_WEST;

	public static SensBloc getAxe(Face face) {
		switch (face) {
		case UP:
		case DOWN:
			return Y;
		case NORTH:
		case SOUTH:
			return Z;
		case EAST:
		case WEST:
			return X;
		default:
			return null;
		}
	}

	public static SensBloc getOrientation(Face face) {
		switch (face) {
		case NORTH:
			return LOOK_SOUTH;
		case SOUTH:
			return LOOK_NORTH;
		case EAST:
			return LOOK_WEST;
		case WEST:
			return LOOK_EAST;
		default:
			return null;
		}
	}

	public static SensBloc getOrientation(Face face, double vueY) {
		if (vueY > 50)
			return LOOK_DOWN;
		if (vueY < -50)
			return LOOK_UP;
		return getOrientation(face);
	}
}
