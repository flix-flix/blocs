package client.main;

import client.session.Session;

public class Main {

	public static void main(String[] args) {
		server.main.Main.main(args);

		new Session();
	}
}
