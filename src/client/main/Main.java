package client.main;

import client.session.Session;
import data.id.ItemTable;

public class Main {

	public static void main(String[] args) {
		System.out.println("======== CLIENT ==========");

		ItemTable.init();

		server.main.MainServer.main(args);
		new Session();
	}
}
