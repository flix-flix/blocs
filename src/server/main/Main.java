package server.main;

import server.Server;

public class Main {

	public final static int port = 1212;

	public static void main(String[] args) {
		System.out.println("======== SERVER ==========");

		Server server = new Server();
		Thread serverThread = new Thread(server);

		serverThread.start();

		// server.stop();
	}
}
