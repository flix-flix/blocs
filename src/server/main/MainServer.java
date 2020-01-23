package server.main;

import server.Server;

public class MainServer {

	public static void main(String[] args) {
		System.out.println("======== SERVER ==========");

		// ItemTable.init();

		Server server = new Server();
		Thread serverThread = new Thread(server);

		serverThread.setName("Server");

		serverThread.start();

		// server.stop();
	}
}
