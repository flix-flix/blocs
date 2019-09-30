package client.window;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import client.session.Session;

public class Client implements Runnable {

	public static final int port = 1212;
	InetAddress serverAddress;

	Socket server;
	ObjectInputStream in;
	public ObjectOutputStream out;

	Session session;

	public Client(Session session) {
		this.session = session;
		try {
			// TODO Other than LocalHost
			serverAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		try {
			server = new Socket(serverAddress, port);

			out = new ObjectOutputStream(server.getOutputStream());

			in = new ObjectInputStream(server.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		new Thread(this).start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				session.receive(in.readObject());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
