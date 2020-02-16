package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.TreeMap;

import data.dynamic.TickClock;
import data.generation.WorldGeneration;
import data.map.Coord;
import data.map.Cube;
import data.map.buildings.Building;
import data.map.units.Unit;
import server.game.MapServer;
import server.game.Player;
import server.game.messages.Message;
import server.game.messages.TypeMessage;
import server.send.SendAction;
import utils.FlixBlocksUtils;

public class Server implements Runnable {

	public static final int defaultPort = 1212;

	private ServerDescription description;

	private ServerSocket server;
	private boolean run = true;

	private TreeMap<Integer, ClientListener> clients = new TreeMap<>();

	// =============== Data ===============
	MapServer map;

	// =========================================================================================================================

	public Server(int port, String name) {
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

		map = new MapServer(WorldGeneration.generateMap(), this);

		TickClock clock = new TickClock("Server Clock");
		clock.add(map);

		clock.start();
	}

	public Server(int port) {
		this(port, "Default Name");
	}

	public Server() {
		this(defaultPort);
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

				ClientListener client = new ClientListener(this, socket);
				clients.put(client.getID(), client);
				client.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	}

	// =========================================================================================================================

	public void stop(int id) {
		clients.remove(id);
	}

	// =========================================================================================================================

	public void sendToAll(Object obj) {
		for (ClientListener client : clients.values())
			client.send(obj);
	}

	public void sendToAllSeeing(Object obj) {

	}

	public void sendToPlayer(Object obj, int id) {
		clients.get(id).send(obj);
	}

	// =========================================================================================================================

	public void receive(Object obj, int id) {
		if (obj instanceof Player)
			receivePlayer((Player) obj, id);
		else if (obj instanceof Message)
			receiveMessage((Message) obj);
		else if (obj instanceof SendAction)
			receiveSend((SendAction) obj, id);
		else
			FlixBlocksUtils.debug("[RECEIVE] Unknown object");
	}

	// =========================================================================================================================

	public void receiveSend(SendAction send, int id) {
		System.out.println("[RECEIVE] " + send.action);
		switch (send.action) {
		case UNIT_GOTO:
			map.getUnit(send.id1).goTo(map, send.coord);
			break;
		case UNIT_BUILD:
			map.getUnit(send.id1).building(map, map.getBuilding(send.id2));
			break;
		case UNIT_HARVEST:
			map.getUnit(send.id1).harvest(map, send.coord);
			break;
		case UNIT_STORE:
			map.getUnit(send.id1).store(map, map.getBuilding(send.id2));
			break;
		default:
			break;
		}
		sendToAll(send);
	}

	// =========================================================================================================================

	public void receivePlayer(Player player, int id) {
		sendToPlayer(map, id);
	}

	public void receiveMessage(Message msg) {
		if (!msg.getText().isEmpty() && msg.getText().charAt(0) == '!')
			sendToAll(new Message("COMMAND", TypeMessage.CONSOLE));
		else
			sendToAll(msg);
	}

	// =========================================================================================================================

	public void addCube(Cube c) {
		System.out.println("[SERVER] Add : " + c.toString());
		sendToAll(SendAction.add(c));
	}

	public void removeCube(int x, int y, int z) {
		System.out.println("[SERVER] Remove : " + new Coord(x, y, z).toString());
		new Coord(x, y, z);
		sendToAll(SendAction.remove(new Coord(x, y, z)));
	}

	// =========================================================================================================================

	public void unitArrive(Unit unit) {
		sendToAll(SendAction.arrive(unit));
	}

	public void unitHarvest(Unit unit, Coord coord) {
		sendToAll(SendAction.harvest(unit, coord).finished());
	}

	public void unitStore(Unit unit, Building build) {
		sendToAll(SendAction.store(unit, build).finished());
	}

	// =========================================================================================================================

	public ServerDescription getDescription() {
		return description;
	}
}
