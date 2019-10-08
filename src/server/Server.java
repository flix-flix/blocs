package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import data.dynamic.TickClock;
import data.generation.WorldGeneration;
import data.map.Coord;
import data.map.Cube;
import data.map.units.Unit;
import server.game.MapServer;
import server.game.Player;
import server.game.messages.Message;
import server.game.messages.TypeMessage;
import server.send.SendAction;
import server.send.SendServerAction;

public class Server implements Runnable {

	public static final int port = 1212;

	Thread thread;

	ServerSocket server;
	boolean running = true;

	Socket client;
	ObjectInputStream in;
	ObjectOutputStream out;

	// =========================================================================================================================

	MapServer map;

	// =========================================================================================================================

	public Server() {
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		thread = new Thread(this);
		thread.start();

		map = new MapServer(WorldGeneration.generateMap(), this);

		TickClock clock = new TickClock();
		clock.add(map);

		new Thread(clock).start();
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
		else if (obj instanceof SendAction)
			receiveSend((SendAction) obj);
		else
			System.err.println("UNKNOWN OBJECT");
	}

	// =========================================================================================================================

	public void receiveSend(SendAction send) {
		switch (send.action) {
		case GOTO:
			map.getUnit(send.unitID).goTo(map, send.coord);
			send(send);
			break;
		case BUILD:
			map.getUnit(send.unitID).doAction(send.action, map, send.coord);
			break;
		default:
			break;
		}
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

	// =========================================================================================================================

	public void addCube(Cube c) {
		System.out.println("[SERVER] Add : " + c.toString());
		send(SendServerAction.add(c));
	}

	public void removeCube(int x, int y, int z) {
		System.out.println("[SERVER] Remove : " + new Coord(x, y, z).toString());
		new Coord(x, y, z);
		send(SendServerAction.remove(new Coord(x, y, z)));
	}

	// =========================================================================================================================

	public void unitArrive(Unit unit) {
		send(SendServerAction.arrive(unit));
	}
}
