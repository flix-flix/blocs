package server.send;

public enum Action {
	// Cube
	ADD, REMOVE,

	// Unit
	UNIT_GOTO, UNIT_ARRIVE, UNIT_HARVEST, UNIT_STORE, UNIT_TAKE, UNIT_BUILD,

	// Building
	BUILDING_NEW, BUILDING_FINISHED, BUILDING_SPAWN, BUILDING_RESEARCH,

	// Unit
	DESTROY, ATTACK;
}
