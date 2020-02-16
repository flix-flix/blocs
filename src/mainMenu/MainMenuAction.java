package mainMenu;

public enum MainMenuAction {
	// =============== Display ===============
	PLAY, SERVER, EDITOR, DATA_MANAGER,

	// =============== Buttons ===============
	OPTIONS, QUIT,
	/** Return to the main menu (from server panel) */
	SERVER_QUIT,

	// =============== Servers ===============
	/** Join the server */
	SERVER_JOIN,

	// ===== Hosted =====
	/** Start (Host) a new server */
	SERVER_START,
	/** Stop an hosted server */
	SERVER_STOP,

	// ===== Known =====
	/** Add a server to the list of "Known servers" */
	SERVER_ADD,
	/** Delete a server from the list of "Known servers" */
	SERVER_DELETE;
}
