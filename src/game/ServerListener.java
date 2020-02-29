package game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import environment.Client;
import server.ServerDescription;

public class ServerListener implements Runnable {

	private Client client;

	private boolean run = true;

	private Socket server;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	// =========================================================================================================================

	public ServerListener(Client client, ServerDescription description) throws IOException {
		this.client = client;

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
		t.setName("Server Listener");
		t.start();
	}

	// =========================================================================================================================

	@Override
	public void run() {
		while (run) {
			try {
				receive(in.readObject());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				client.exception(e);
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

	// =========================================================================================================================

	public void send(Object obj) {
		try {
			out.writeObject(obj);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void receive(Object obj) {
		client.receive(obj);
	}

	// =========================================================================================================================

	public boolean isRunning() {
		return run;
	}
}
