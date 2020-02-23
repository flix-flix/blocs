package server.main;

import java.io.IOException;

import server.Server;

public class MainServer {

	public static void main(String[] args) {
		System.out.println("========== SERVER ==========");

		// ItemTable.init();

		Server server;
		try {
			server = new Server();
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// server.stop();
	}
}
