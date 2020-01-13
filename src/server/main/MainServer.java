package server.main;

import server.Server;

public class MainServer {

	public static void main(String[] args) {
		System.out.println("======== SERVER ==========");

		Server server = new Server();
		Thread serverThread = new Thread(server);

		serverThread.start();

		// server.stop();
	}
}
