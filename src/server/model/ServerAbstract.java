package server.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.TreeMap;

import server.ServerDescription;

public abstract class ServerAbstract implements Runnable {

	private ServerDescription description;

	private ServerSocket server;
	private boolean run = true;

	private TreeMap<Integer, ClientListener> clients = new TreeMap<>();

	// =========================================================================================================================

	public ServerAbstract(int port, String name) {
		String ip = "0.0.0.0";
		try {
			ip = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		if (ip.contains("/"))
			ip = ip.substring(ip.indexOf('/') + 1);

		this.description = new ServerDescription(ip, port, name, this);

		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// =========================================================================================================================

	@Override
	public void run() {
		try {
			while (run) {
				// New client
				Socket socket;
				try {
					socket = server.accept();
				} catch (SocketException e) {
					break;
				}
				addListener(socket);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// =========================================================================================================================

	protected void addListener(Socket socket) {
		ClientListener client = new ClientListener(this, socket);
		client.start();
		putClient(client.getID(), client);
	}

	// =========================================================================================================================

	public void start() {
		System.out.println("===== SERVER [START] =====");
		Thread serverThread = new Thread(this);
		serverThread.setName("Server");
		serverThread.start();
	}

	public void stop() {
		System.out.println("===== SERVER [CLOSE] =====");
		run = false;

		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (ClientListener client : clients.values())
			client.close();
	}

	// =========================================================================================================================

	void stop(int id) {
		clients.remove(id);
	}

	// =========================================================================================================================

	public void sendToAll(Object obj) {
		for (ClientListener client : clients.values())
			client.send(obj);
	}

	public void sendToPlayer(Object obj, int id) {
		clients.get(id).send(obj);
	}

	// =========================================================================================================================

	public abstract void receive(Object obj, int id);

	// =========================================================================================================================
	// Getters

	public void putClient(int id, ClientListener client) {
		clients.put(id, client);
	}

	public ClientListener getClient(int id) {
		return clients.get(id);
	}

	public ServerDescription getDescription() {
		return description;
	}
}
