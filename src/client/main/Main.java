package client.main;

import java.awt.AWTException;

import client.session.Session;
import client.textures.TexturePack;
import client.window.Fen;
import client.window.graphicEngine.calcul.Point3D;
import client.window.graphicEngine.models.ModelMap;
import data.generation.WorldGeneration;

public class Main {

	public static void main(String[] args) throws AWTException {

		ModelMap map = new ModelMap();

		Session session = new Session(map, true);
		session.setTexturePack(new TexturePack());

		WorldGeneration.generateMap(map);

		session.camera.setVx(45);
		session.camera.setVy(-30);
		session.camera.vue = new Point3D(-5, 10, -5);

		new Fen(session);
		session.start();
	}
}
