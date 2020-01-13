package client.main;

import client.session.Session;

public class Main {

	public static void main(String[] args) {
		System.out.println("======== CLIENT ==========");
		server.main.MainServer.main(args);

		new Session();
	}
}
