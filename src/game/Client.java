package game;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import data.id.ItemTableClient;
import server.ServerDescription;

public class Client implements Runnable {

	public static final int port = 1212;

	private boolean run = true;

	Socket server;
	ObjectInputStream in;
	public ObjectOutputStream out;

	Game game;

	// =========================================================================================================================

	public Client(Game game, ServerDescription description) throws IOException {
		this.game = game;

		InetAddress inetAdr;
		try {
			inetAdr = InetAddress.getByName(description.ip);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		}

		// ====================

		server = new Socket(inetAdr, description.port);

		out = new ObjectOutputStream(server.getOutputStream());
		in = new ObjectInputStream(server.getInputStream());

		// ====================

		Thread t = new Thread(this);
		t.setName("Client");
		t.start();
	}

	// =========================================================================================================================

	@Override
	public void run() {
		while (run) {
			try {
				game.receive(in.readObject());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				if (e instanceof SocketException || e instanceof EOFException) {
					if (run)
						game.connexionLost(ItemTableClient.getText("game.error.connexion_lost"));
					break;
				}
				e.printStackTrace();
			}
		}
	}

	// =========================================================================================================================

	public void close() {
		run = false;

		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
