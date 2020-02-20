package server.model;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ClientListener implements Runnable {

	private static int nextID = 0;

	protected int id;

	private ServerAbstract server;
	private Socket client;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	private Thread thread;
	private boolean run = true;

	// =========================================================================================================================

	public ClientListener(ServerAbstract server, Socket client) {
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
				if (e instanceof SocketException || e instanceof EOFException)
					if (run)
						stopped();
					else
						e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	// =========================================================================================================================

	public void start() {
		thread = new Thread(this);
		thread.setName("ClientListner [" + id + "]");
		thread.start();
	}

	/** Called when client quit */
	private void stopped() {
		run = false;
		server.stopped(id);
	}

	/** Close the stream */
	public void close() {
		run = false;
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// =========================================================================================================================

	public void send(Object obj) {
		// Size
		// ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// ObjectOutputStream o = null;
		// try {
		// o = new ObjectOutputStream(bos);
		// o.writeObject(obj);
		// o.flush();
		// System.out.println(bos.toByteArray().length);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		if (run)
			try {
				out.writeObject(obj);
				out.flush();
			} catch (IOException e) {
				if (!run)
					return;
				e.printStackTrace();
			}
	}

	// =========================================================================================================================

	public int getID() {
		return id;
	}
}
