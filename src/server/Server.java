package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import data.dynamic.TickClock;
import data.generation.WorldGeneration;
import data.map.Map;
import server.game.Player;
import server.game.messages.Message;
import server.game.messages.TypeMessage;

public class Server implements Runnable {

	public static final int port = 1212;

	Thread thread;

	ServerSocket server;
	boolean running = true;

	Socket client;
	ObjectInputStream in;
	ObjectOutputStream out;

	// =========================================================================================================================

	Map map;

	// =========================================================================================================================

	public Server() {
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		thread = new Thread(this);
		thread.start();

		map = WorldGeneration.generateMap();

		TickClock clock = new TickClock();
		clock.add(map);
	}

	// =========================================================================================================================

	@Override
	public void run() {
		try {
			while (running) {
				// New client
				client = server.accept();

				if (!running)
					break;

				in = new ObjectInputStream(client.getInputStream());
				out = new ObjectOutputStream(client.getOutputStream());

				try {
					while (true) {
						receive(in.readObject());
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		running = false;

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

	// =========================================================================================================================

	public void receive(Object obj) {
		if (obj instanceof Player)
			receivePlayer((Player) obj);
		else if (obj instanceof Message)
			receiveMessage((Message) obj);
		else
			System.out.println("UNKNOWN OBJECT");
	}

	// =========================================================================================================================

	public void receivePlayer(Player player) {
		send(map);
	}

	public void receiveMessage(Message msg) {
		if (!msg.getText().isEmpty() && msg.getText().charAt(0) == '!')
			send(new Message("COMMAND", TypeMessage.CONSOLE));
		else
			send(msg);
	}
}
