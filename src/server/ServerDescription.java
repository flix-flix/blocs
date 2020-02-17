package server;

import server.model.ServerAbstract;

public class ServerDescription {

	/** Null if not local server */
	public ServerAbstract server;

	public String ip = "192.168.0.16";
	public int port = 1212;

	public String name = "Felix' world";

	// =========================================================================================================================

	public ServerDescription(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public ServerDescription(String ip, int port, String name) {
		this(ip, port);
		this.name = name;
	}

	public ServerDescription(String ip, int port, String name, ServerAbstract server) {
		this(ip, port, name);
		this.server = server;
	}
}
