package main;

import data.id.ItemTableClient;
import window.Fen;

public class Main {

	public static void main(String[] args) {
		System.out.println("======== CLIENT ==========");

		ItemTableClient.init();

		server.main.MainServer.main(args);

		new Fen();
	}
}