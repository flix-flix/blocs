package server.send;

public enum Action {
	// Server
	SERVER_NAME, SERVER_NB_PLAYERS,

	/** Number of ticks/sec of the TickClock */
	SERVER_TICKS_PHYS,

	// Cube
	ADD, REMOVE,

	// Unit
	UNIT_GOTO, UNIT_ARRIVE, UNIT_HARVEST, UNIT_STORE, UNIT_TAKE, UNIT_BUILD, UNIT_DESTROY, UNIT_ATTACK, UNIT_FRIEND_UNIT,

	// Building
	BUILDING_NEW, BUILDING_FINISHED, BUILDING_SPAWN, BUILDING_RESEARCH;
}
