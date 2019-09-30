package client.main;

import java.awt.AWTException;

import client.session.Session;

public class Main {

	public static void main(String[] args) {
		server.main.Main.main(args);

		try {
			new Session();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
}
