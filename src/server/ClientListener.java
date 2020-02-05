package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ClientListener implements Runnable {

	private static int nextID = 0;

	private int id;

	Server server;
	Socket client;
	ObjectInputStream in;
	ObjectOutputStream out;

	private Thread thread;
	private boolean run = true;

	// =========================================================================================================================

	public ClientListener(Server server, Socket client) {
		this.server = server;
		this.client = client;
		this.id = nextID++;

		try {
			in = new ObjectInputStream(client.getInputStream());
			out = new ObjectOutputStream(client.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// =========================================================================================================================

	@Override
	public void run() {
		while (run) {
			try {
				server.receive(in.readObject(), id);
			} catch (IOException e) {
				if (e instanceof SocketException)
					break;
				else
					e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {
		thread = new Thread(this);
		thread.setName("ClientListner [" + id + "]");
		thread.start();
	}

	public void stop() {
		run = false;
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

	// =========================================================================================================================

	public int getID() {
		return id;
	}

}
