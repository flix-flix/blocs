package client.main;

import client.window.Fen;
import data.id.ItemTableClient;

public class Main {

	public static void main(String[] args) {
		System.out.println("======== CLIENT ==========");

		ItemTableClient.init();

		server.main.MainServer.main(args);

		new Fen();
	}
}
