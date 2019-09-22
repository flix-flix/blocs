package data.enumeration;

public enum Face {

	UP, DOWN, NORTH, SOUTH, EAST, WEST;

	public static Face[] faces = { UP, DOWN, NORTH, SOUTH, EAST, WEST };
	public static Face[] facesReverse = { WEST, EAST, SOUTH, NORTH, DOWN, UP };

	public int opposite() {
		switch (this) {
		case UP:
			return DOWN.ordinal();
		case DOWN:
			return UP.ordinal();
		case NORTH:
			return SOUTH.ordinal();
		case SOUTH:
			return NORTH.ordinal();
		case EAST:
			return WEST.ordinal();
		case WEST:
			return EAST.ordinal();
		}
		return -1;
	}
}
