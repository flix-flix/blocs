package server.main;

import server.Server;

public class Main {

	public static void main(String[] args) {
		System.out.println("======== SERVER ==========");

		Server server = new Server();
		Thread serverThread = new Thread(server);

		serverThread.start();

		// server.stop();
	}
}
