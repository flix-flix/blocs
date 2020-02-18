package main;

import data.id.ItemTableClient;
import environment.textures.TexturePack;
import window.Fen;

public class Main {

	public static void main(String[] args) {
		System.out.println("========== CLIENT ==========");

		ItemTableClient.init();
		ItemTableClient.setTexturePack(new TexturePack("classic"));

		new Fen();
	}
}
