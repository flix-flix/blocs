package client.main;

import java.awt.AWTException;

import client.session.Session;
import client.window.Fen;
import client.window.graphicEngine.extended.ModelMap;
import data.generation.WorldGeneration;

public class Main {

	public static void main(String[] args) throws AWTException {
		ModelMap map = new ModelMap();

		Session session = new Session(map, true);

		WorldGeneration.generateMap(map);

		new Fen(session);

		session.start();
	}
}
